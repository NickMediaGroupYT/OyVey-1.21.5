package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.render.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AutoMine extends Module {

    public enum AnticheatMode { Grim, NCP }
    public enum RenderMode { Fill, Outline, Both }

    private final Setting<Enum<?>> mode = register(new Setting<>("AnticheatMode", AnticheatMode.Grim));
    private final Setting<Boolean> doubleMine = register(new Setting<>("DoubleMine", false));
    private final Setting<Boolean> swing = register(new Setting<>("Swing", false));
    private final Setting<Integer> enemyRange = register(new Setting<>("EnemyRange", 4, 1, 6));

    private final Setting<Enum<?>> renderMode = register(new Setting<>("RenderMode", RenderMode.Both));
    private final Setting<Boolean> rainbow = register(new Setting<>("Rainbow", false));
    private final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    private final Setting<Integer> green = register(new Setting<>("Green", 0, 0, 255));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = register(new Setting<>("Alpha", 50, 0, 255));
    private final Setting<Float> lineWidth = register(new Setting<>("LineWidth", 2.0f, 0.1f, 5.0f));

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private BlockPos primaryTarget = null;
    private BlockPos secondaryTarget = null;
    private final Map<BlockPos, Integer> retryCounts = new HashMap<>();

    public AutoMine() {
        super("AutoMine", "Automatically mines blocks using packets", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;

        if (primaryTarget != null && mc.world.getBlockState(primaryTarget).isAir()) {
            retryCounts.remove(primaryTarget);
            primaryTarget = null;
        }

        if (secondaryTarget != null && mc.world.getBlockState(secondaryTarget).isAir()) {
            retryCounts.remove(secondaryTarget);
            secondaryTarget = null;
        }

        if (mc.options.attackKey.isPressed() && mc.crosshairTarget instanceof BlockHitResult bhr) {
            BlockPos targetPos = bhr.getBlockPos();
            if (canBreak(targetPos)) {
                queueMine(targetPos);
                return;
            }
        }

        double rangeSq = enemyRange.getValue() * enemyRange.getValue();
        for (PlayerEntity entity : mc.world.getPlayers()) {
            if (entity == mc.player || isFriend(entity)) continue;
            if (entity.squaredDistanceTo(mc.player) > rangeSq) continue;

            BlockPos base = entity.getBlockPos();
            for (BlockPos pos : BlockPos.iterateOutwards(base.down(), 1, 2, 1)) {
                if (canBreak(pos)) {
                    queueMine(pos);
                    return;
                }
            }
        }
    }

    private void queueMine(BlockPos pos) {
        if (pos.equals(primaryTarget) || pos.equals(secondaryTarget)) return;

        if (primaryTarget == null) {
            startMine(pos, true);
        } else if (doubleMine.getValue() && secondaryTarget == null) {
            startMine(pos, false);
        }
    }

    private void startMine(BlockPos pos, boolean isPrimary) {
        Direction dir = Direction.UP;
        AnticheatMode acMode = (AnticheatMode) mode.getValue();

        if (acMode == AnticheatMode.Grim) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, dir));
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, dir));
        } else if (acMode == AnticheatMode.NCP) {
            mc.interactionManager.attackBlock(pos, dir);
        }

        if (swing.getValue()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        if (isPrimary) primaryTarget = pos;
        else secondaryTarget = pos;
    }

    private boolean canBreak(BlockPos pos) {
        if (mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        return !state.isAir() && state.getHardness(mc.world, pos) >= 0;
    }

    private boolean isFriend(PlayerEntity player) {
        // Replace this with your friend check logic
        return false;
    }

    @Override
    public void onRender3D(MatrixStack matrices, float tickDelta) {
        if (primaryTarget != null) drawBoxAt(primaryTarget, matrices);
        if (secondaryTarget != null) drawBoxAt(secondaryTarget, matrices);
    }

    private void drawBoxAt(BlockPos pos, MatrixStack matrices) {
        Camera camera = mc.gameRenderer.getCamera();
        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;
        Box box = new Box(pos).offset(-camX, -camY, -camZ);

        Color color = rainbow.getValue() ? getRainbowColor() : new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderMode rm = (RenderMode) renderMode.getValue();
        if (rm == RenderMode.Fill || rm == RenderMode.Both) {
            RenderUtil.drawBoxFilled(matrices, box, color);
        }

        if (rm == RenderMode.Outline || rm == RenderMode.Both) {
            GL11.glLineWidth(lineWidth.getValue());
            RenderUtil.drawBox(matrices, box, color, color.getAlpha() / 255.0);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private Color getRainbowColor() {
        float hue = (System.currentTimeMillis() % 2000L) / 2000f;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
}
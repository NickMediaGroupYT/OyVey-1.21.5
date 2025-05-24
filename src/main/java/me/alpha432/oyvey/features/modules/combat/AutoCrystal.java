package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class AutoCrystal extends Module {

    public Setting<Double> placeRange = this.register(new Setting<>("PlaceRange", 4.5, 0.0, 6.0));
    public Setting<Double> breakRange = this.register(new Setting<>("BreakRange", 4.5, 0.0, 6.0));
    public Setting<Boolean> placeRotate = this.register(new Setting<>("PlaceRotate", true));
    public Setting<Boolean> placeSwing = this.register(new Setting<>("PlaceSwing", true));
    public Setting<Boolean> breakRotate = this.register(new Setting<>("BreakRotate", true));
    public Setting<Boolean> breakSwing = this.register(new Setting<>("BreakSwing", true));
    public Setting<Boolean> friendProtect = this.register(new Setting<>("FriendProtect", true));

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places and breaks end crystals", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                double distance = mc.player.squaredDistanceTo(entity);
                if (distance <= breakRange.getValue() * breakRange.getValue()) {
                    if (breakRotate.getValue()) {
                        rotateTo(entity);
                    }
                    mc.interactionManager.attackEntity(mc.player, entity);
                    if (breakSwing.getValue()) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            }
        }

        PlayerEntity target = mc.world.getPlayers().stream()
                .filter(p -> p != mc.player)
                .filter(p -> !p.isDead())
                .filter(p -> mc.player.squaredDistanceTo(p) <= placeRange.getValue() * placeRange.getValue())
                .filter(p -> !friendProtect.getValue() || !OyVey.friendManager.isFriend(p))
                .min(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(p)))
                .orElse(null);

        if (target != null) {
            BlockPos pos = target.getBlockPos().down();
            if (canPlaceCrystal(pos)) {
                if (placeRotate.getValue()) {
                    rotateTo(pos);
                }
                if (placeSwing.getValue()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new net.minecraft.util.hit.BlockHitResult(Vec3d.ofCenter(pos), net.minecraft.util.math.Direction.UP, pos, false));
            }
        }
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        return mc.world.getBlockState(pos).isOf(net.minecraft.block.Blocks.OBSIDIAN) ||
                mc.world.getBlockState(pos).isOf(net.minecraft.block.Blocks.BEDROCK);
    }

    private void rotateTo(Entity entity) {
        // Stub: rotate to entity
    }

    private void rotateTo(BlockPos pos) {
        // Stub: rotate to position
    }
}

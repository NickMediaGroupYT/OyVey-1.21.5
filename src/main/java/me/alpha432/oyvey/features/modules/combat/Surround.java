package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Surround extends Module {

    public enum AnticheatMode { Grim, NCP }

    private final Setting<Enum<?>> mode = register(new Setting<>("Mode", AnticheatMode.Grim));
    private final Setting<Boolean> center = register(new Setting<>("Center", true));
    private final Setting<Integer> delay = register(new Setting<>("Delay", 0, 0, 10));
    private final Setting<Integer> blocksPerTick = register(new Setting<>("BlocksPerTick", 4, 1, 8));
    private final Setting<Boolean> rotate = register(new Setting<>("Rotate", true));
    private final Setting<Boolean> onlyGround = register(new Setting<>("OnlyGround", true));

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private int timer = 0;

    public Surround() {
        super("Surround", "Places obsidian around your feet", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        if (center.getValue()) {
            Vec3d centerPos = new Vec3d(
                    Math.floor(mc.player.getX()) + 0.5,
                    mc.player.getY(),
                    Math.floor(mc.player.getZ()) + 0.5
            );
            mc.player.updatePosition(centerPos.x, centerPos.y, centerPos.z);
        }

        timer = 0;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (onlyGround.getValue() && !mc.player.isOnGround()) return;

        if (timer < delay.getValue()) {
            timer++;
            return;
        }

        timer = 0;

        BlockPos basePos = mc.player.getBlockPos();
        List<BlockPos> positions = getSurroundOffsets(basePos);

        int placed = 0;
        int obsidianSlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);

        if (obsidianSlot == -1) return;

        int prevSlot = mc.player.getInventory().getSelectedSlot();

        for (BlockPos pos : positions) {
            if (placed >= blocksPerTick.getValue()) break;

            if (BlockUtil.placeBlockSmart(pos, obsidianSlot, rotate.getValue(), (AnticheatMode) mode.getValue())) {
                placed++;
            }
        }

        if (mc.player.getInventory().getSelectedSlot() != prevSlot) {
            mc.player.getInventory().setSelectedSlot(prevSlot);
        }
    }

    private List<BlockPos> getSurroundOffsets(BlockPos pos) {
        List<BlockPos> offsets = new ArrayList<>();
        offsets.add(pos.north());
        offsets.add(pos.south());
        offsets.add(pos.east());
        offsets.add(pos.west());
        return offsets;
    }
}

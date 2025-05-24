package me.alpha432.oyvey.util;

import me.alpha432.oyvey.features.modules.combat.Surround;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean placeBlockSmart(BlockPos pos, int slot, boolean rotate, Surround.AnticheatMode mode) {
        if (!canPlace(pos)) return false;

        mc.player.getInventory().setSelectedSlot(slot);

        Direction direction = Direction.UP;
        if (mode == Surround.AnticheatMode.NCP) {
            // Use actual method for interacting if needed
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(
                    Vec3d.ofCenter(pos), direction, pos, false));
        } else {
            // Grim-compatible: send sneaky packet-like behavior
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(
                    Vec3d.ofCenter(pos), direction, pos, false));
        }

        return true;
    }

    private static boolean canPlace(BlockPos pos) {
        return mc.world.getBlockState(pos).isReplaceable();
    }
}

package me.alpha432.oyvey.util;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class HoleUtil {

    public static boolean isInHole(PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        World world = player.getEntityWorld();

        return isSolidBlock(world, pos.down()) &&
               isSolidBlock(world, pos.north()) &&
               isSolidBlock(world, pos.south()) &&
               isSolidBlock(world, pos.east()) &&
               isSolidBlock(world, pos.west());
    }

    private static boolean isSolidBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isOf(Blocks.BEDROCK) || state.isOf(Blocks.OBSIDIAN);
    }
}

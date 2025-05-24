package me.alpha432.oyvey.util;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;

public class InventoryUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static int findHotbarBlock(Block block) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == block.asItem()) {
                return i;
            }
        }
        return -1;
    }
}

package me.alpha432.oyvey.util;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

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

    public static void switchToOffhand(Item item) {
        if (mc.player == null || mc.interactionManager == null) return;

        int slot = findItemSlot(item);
        if (slot == -1) return;

        // Convert hotbar slots (0–8) to GUI slots (36–44)
        int windowSlot = slot < 9 ? slot + 36 : slot;

        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                windowSlot,
                40, // offhand slot
                SlotActionType.SWAP,
                mc.player
        );
    }

    /**
     * Finds the inventory slot index (0–35) of the given item. Returns -1 if not found.
     */
    public static int findItemSlot(Item item) {
        if (mc.player == null) return -1;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }
}

package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class Burrow extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Burrow() {
        super("Burrow", "Places a block inside the player", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle(); // disable if no world or player
            return;
        }

        BlockPos pos = mc.player.getBlockPos();

        // Check if player already inside a block
        if (!mc.world.getBlockState(pos).isAir()) {
            toggle();
            return;
        }

        int slot = findBlockInHotbar();
        if (slot == -1) {
            // No blocks found, disable
            toggle();
            return;
        }

        int prevSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(slot);

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, (BlockHitResult) mc.player.raycast(5.0, 0f, false));

        mc.player.getInventory().setSelectedSlot(prevSlot);
        toggle(); // disable after placing block
    }

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Blocks.OBSIDIAN.asItem()) {
                return i;
            }
        }
        return -1;
    }
}
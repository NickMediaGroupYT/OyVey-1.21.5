package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class PearlPhase extends Module {

    private final Setting<Boolean> swing = register(new Setting<>("Swing", true));
    private final Setting<Integer> pitch = register(new Setting<>("Pitch", 86, 70, 90));

    public PearlPhase() {
        super("PearlPhase", "Phases into blocks with pearl", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        int pearlSlot = findPearlSlot();
        if (pearlSlot == -1) {
            disable();
            return;
        }

        // Rotate to face block backward if sneaking
        float yaw = mc.player.getYaw();
        if (mc.options.backKey.isPressed()) {
            yaw = yaw + 180;
        }

        float p = pitch.getValue().floatValue();
        rotate(yaw, p);

        // Swap to pearl slot
        int prevSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(pearlSlot);

        // Throw the pearl
        mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, (int) mc.world.getTime(), yaw, pitch.getValue()));
        if (swing.getValue()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        // Restore previous slot
        mc.player.getInventory().setSelectedSlot(prevSlot);

        disable();
    }

    private int findPearlSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.ENDER_PEARL) {
                return i;
            }
        }
        return -1;
    }

    private void rotate(float yaw, float pitch) {
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}

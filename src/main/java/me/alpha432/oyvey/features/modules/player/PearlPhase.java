package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class PearlPhase extends Module {
    public Setting<Boolean> swing = register(new Setting<>("Swing", true));
    public Setting<Float> pitch = register(new Setting<>("Pitch", 86f, 70f, 90f));

    public PearlPhase() {
        super("PearlPhase", "", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }

        int pearlSlot = findPearlSlot();
        if (pearlSlot == -1) {
            toggle();
            return;
        }

        float yaw = mc.player.getYaw();
        if (mc.options.backKey.isPressed()) {
            yaw -= 180f;
        }

        int prevSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(pearlSlot);

        // Apply rotation client-side
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch.getValue());

        // Send pearl throw packet with yaw and pitch (1.21.5 requirement)
        mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, (int) mc.world.getTime(), yaw, pitch.getValue()));

        if (swing.getValue()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        mc.player.getInventory().setSelectedSlot(prevSlot);
        toggle(); // disable after use
    }

    private int findPearlSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.ENDER_PEARL) {
                return i;
            }
        }
        return -1;
    }
}

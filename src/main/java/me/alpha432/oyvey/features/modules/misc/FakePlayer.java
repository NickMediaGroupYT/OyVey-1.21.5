package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.EventHandler;
import me.alpha432.oyvey.event.impl.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class FakePlayer extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private OtherClientPlayerEntity fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns a fake player clone", Category.MISC, false, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }

        if (fakePlayer == null) {
            ClientPlayerEntity localPlayer = mc.player;

            fakePlayer = new OtherClientPlayerEntity(mc.world, localPlayer.getGameProfile());
            fakePlayer.copyPositionAndRotation(localPlayer);
            fakePlayer.setHeadYaw(localPlayer.getHeadYaw());
            fakePlayer.setYaw(localPlayer.getYaw());
            fakePlayer.setPitch(localPlayer.getPitch());
            fakePlayer.setHealth(localPlayer.getHealth());
            fakePlayer.getGameMode();

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = localPlayer.getEquippedStack(slot);
                fakePlayer.equipStack(slot, stack.copy());
            }

            mc.world.addEntity(fakePlayer); // Use a consistent negative entity ID
        }
    }

    @Override
    public void onDisable() {
        if (fakePlayer != null && mc.world != null) {
            mc.world.removeEntity(fakePlayer.getId(), fakePlayer.getRemovalReason());
            fakePlayer = null;
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (fakePlayer != null && mc.player != null) {
            fakePlayer.setYaw(mc.player.getYaw());
            fakePlayer.setPitch(mc.player.getPitch());
            fakePlayer.lastYaw = mc.player.lastYaw;
            fakePlayer.lastPitch = mc.player.lastPitch;
        }
    }
}

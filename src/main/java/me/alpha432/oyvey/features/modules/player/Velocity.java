package me.alpha432.oyvey.features.modules.player;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Velocity extends Module {

    public enum Mode {
        CANCEL,
        REDUCE,
        GRIM,
        WALLS
    }

    private final Setting<Enum<?>> mode = register(new Setting<>("Mode", Mode.GRIM));
    private final Setting<Integer> horizontal = register(new Setting<>("Horizontal%", 0, 0, 100));
    private final Setting<Integer> vertical = register(new Setting<>("Vertical%", 0, 0, 100));
    private final Setting<Boolean> noPush = register(new Setting<>("NoPush", true));

    public Velocity() {
        super("Velocity", "Reduces or cancels knockback", Category.PLAYER, true, false, false);
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet &&
                packet.getEntityId() == mc.player.getId()) {

            if (mode.getValue().equals(Mode.CANCEL) || mode.getValue().equals(Mode.GRIM)) {
                event.cancel();
            } else if (mode.getValue().equals(Mode.REDUCE)) {
                if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                    event.cancel();
                }
                // Let the server apply the reduced values without setting velocity manually.
            } else if (mode.getValue().equals(Mode.WALLS)) {
                if (isTouchingWall(mc.player)) {
                    event.cancel();
                }
            }
        }

        if (event.getPacket() instanceof ExplosionS2CPacket) {
            if (mode.getValue() == Mode.CANCEL || mode.getValue() == Mode.GRIM) {
                event.cancel();
            } else if (mode.getValue() == Mode.WALLS && isTouchingWall(mc.player)) {
                event.cancel();
            }
        }
    }

    private boolean isTouchingWall(ClientPlayerEntity player) {
        return player.horizontalCollision;
    }

    @Override
    public void onUpdate() {
        if (mc.player != null && noPush.getValue()) {
            mc.player.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
        }
    }
}
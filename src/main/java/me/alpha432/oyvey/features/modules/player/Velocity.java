package me.alpha432.oyvey.features.modules.player;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

public class Velocity extends Module {

    public enum Mode {
        CANCEL,
        REDUCE,
        LEGIT,
        GRIM,
        WALLS
    }

    private final Setting<Enum<?>> mode = register(new Setting<>("Mode", Mode.CANCEL));
    private final Setting<Integer> horizontal = register(new Setting<>("Horizontal%", 0, 0, 100));
    private final Setting<Integer> vertical = register(new Setting<>("Vertical%", 0, 0, 100));
    private final Setting<Boolean> noPush = register(new Setting<>("NoPush", true));

    public Velocity() {
        super("Velocity", "Controls knockback and push", Category.PLAYER, true, false, false);
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet &&
                packet.getEntityId() == mc.player.getId()) {

            if (mode.getValue().equals(Mode.CANCEL)) {
                event.cancel();
            } else if (mode.getValue().equals(Mode.REDUCE)) {
                double vx = (packet.getVelocityX() / 8000.0) * (horizontal.getValue() / 100.0);
                double vy = (packet.getVelocityY() / 8000.0) * (vertical.getValue() / 100.0);
                double vz = (packet.getVelocityZ() / 8000.0) * (horizontal.getValue() / 100.0);
                mc.player.setVelocity(vx, vy, vz);
                event.cancel();
            } else if (mode.getValue().equals(Mode.LEGIT)) {
                if (mc.player.hurtTime > 0) {
                    double vx = (packet.getVelocityX() / 8000.0) * 0.4;
                    double vy = (packet.getVelocityY() / 8000.0) * 0.4;
                    double vz = (packet.getVelocityZ() / 8000.0) * 0.4;
                    mc.player.setVelocity(vx, vy, vz);
                    event.cancel();
                }
            } else if (mode.getValue().equals(Mode.GRIM)) {
                if (mc.player.hurtTime > 0) {
                    event.cancel();
                }
            } else if (mode.getValue().equals(Mode.WALLS)) {
                if (isTouchingWall(mc.player)) {
                    event.cancel();
                }
            }
        }

        if (event.getPacket() instanceof ExplosionS2CPacket) {
            if (mode.getValue().equals(Mode.CANCEL)) {
                event.cancel();
            } else if (mode.getValue().equals(Mode.REDUCE)) {
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(
                        vel.x * (horizontal.getValue() / 100.0),
                        vel.y * (vertical.getValue() / 100.0),
                        vel.z * (horizontal.getValue() / 100.0)
                );
            } else if (mode.getValue().equals(Mode.LEGIT)) {
                if (mc.player.hurtTime > 0) {
                    Vec3d vel = mc.player.getVelocity();
                    mc.player.setVelocity(vel.multiply(0.4, 0.4, 0.4));
                }
            } else if (mode.getValue().equals(Mode.GRIM)) {
                if (mc.player.hurtTime > 0) {
                    event.cancel();
                }
            } else if (mode.getValue().equals(Mode.WALLS)) {
                if (isTouchingWall(mc.player)) {
                    event.cancel();
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player != null && noPush.getValue()) {
            mc.player.noClip = true;  // Prevents block pushing
        }
    }

    private boolean isTouchingWall(ClientPlayerEntity player) {
        return player.horizontalCollision || player.isInsideWall();
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
        }
    }
}
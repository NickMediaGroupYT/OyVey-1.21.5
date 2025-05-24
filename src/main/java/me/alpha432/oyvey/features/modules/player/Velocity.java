package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.EventHandler;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

public class Velocity extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public enum VelocityMode {
        NORMAL, GRIM, GRIM_V3, WALLS
    }

    public Setting<Boolean> knockback = new Setting<>("Knockback", true);
    public Setting<Boolean> explosion = new Setting<>("Explosion", true);
    public Setting<Enum<?>> mode = new Setting<>("Mode", VelocityMode.NORMAL);
    public Setting<Float> horizontal = new Setting<>("Horizontal", 0f, 0f, 100f);
    public Setting<Float> vertical = new Setting<>("Vertical", 0f, 0f, 100f);

    public Velocity() {
        super("Velocity", "Cancels or reduces velocity", Category.COMBAT, true, false, false);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        Packet<?> packet = event.getPacket();

        if (mc.player == null || mc.world == null) return;

        if (mode.getValue().equals(VelocityMode.NORMAL)) {
            handleNormal(event, packet);
        } else if (mode.getValue().equals(VelocityMode.GRIM)) {
            handleGrim(event, packet);
        } else if (mode.getValue().equals(VelocityMode.GRIM_V3)) {
            handleGrimV3(event, packet);
        } else if (mode.getValue().equals(VelocityMode.WALLS)) {
            handleWalls(event, packet);
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        // WALLS mode logic for grounded boost
        if (mode.getValue() == VelocityMode.WALLS && mc.player != null && mc.player.isOnGround()) {
            mc.player.setVelocity(new Vec3d(0.25, 0.0, 0.25)); // Boost effect
        }
    }

    private void handleNormal(PacketEvent.Receive event, Packet<?> packet) {
        if (packet instanceof EntityVelocityUpdateS2CPacket p) {
            if (p.getEntityId() == mc.player.getId() && knockback.getValue()) {
                if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                    event.cancel();
                } else {
                    // TODO: Replace with mixin accessors or reflection to scale velocity
                    // Placeholder for modifying packet values
                }
            }
        } else if (packet instanceof ExplosionS2CPacket && explosion.getValue()) {
            if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                event.cancel();
            } else {
                // TODO: Replace with mixin accessors or reflection
            }
        }
    }

    private void handleGrim(PacketEvent.Receive event, Packet<?> packet) {
        if (packet instanceof EntityVelocityUpdateS2CPacket p) {
            if (p.getEntityId() == mc.player.getId()) {
                event.cancel();
                if (knockback.getValue() && horizontal.getValue() > 0 || vertical.getValue() > 0) {
                    // Apply scaled velocity directly to player
                    double x = p.getVelocityX() / 8000.0 * (horizontal.getValue() / 100f);
                    double y = p.getVelocityY() / 8000.0 * (vertical.getValue() / 100f);
                    double z = p.getVelocityZ() / 8000.0 * (horizontal.getValue() / 100f);
                    mc.player.addVelocity(x, y, z);
                }
            }
        } else if (packet instanceof ExplosionS2CPacket && explosion.getValue()) {
            event.cancel();
        }
    }

    private void handleGrimV3(PacketEvent.Receive event, Packet<?> packet) {
        if (packet instanceof BundleS2CPacket bundle) {
            for (Packet<?> subPacket : bundle.getPackets()) {
                if (subPacket instanceof EntityVelocityUpdateS2CPacket p) {
                    if (p.getEntityId() == mc.player.getId()) {
                        event.cancel();
                        if (knockback.getValue()) {
                            double x = p.getVelocityX() / 8000.0 * (horizontal.getValue() / 100f);
                            double y = p.getVelocityY() / 8000.0 * (vertical.getValue() / 100f);
                            double z = p.getVelocityZ() / 8000.0 * (horizontal.getValue() / 100f);
                            mc.player.addVelocity(x, y, z);
                        }
                    }
                }
            }
        }
    }

    private void handleWalls(PacketEvent.Receive event, Packet<?> packet) {
        if (packet instanceof EntityVelocityUpdateS2CPacket p) {
            if (p.getEntityId() == mc.player.getId() && knockback.getValue()) {
                event.cancel();
            }
        } else if (packet instanceof ExplosionS2CPacket && explosion.getValue()) {
            event.cancel();
        }
    }
}
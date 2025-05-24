package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.EventHandler;
import me.alpha432.oyvey.event.Listener;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

public class Velocity extends Module {

    public enum Mode {
        NORMAL,
        GRIM,
        GRIM_V3,
        WALLS,
        NCP
    }

    private final Setting<Enum<?>> mode = register(new Setting<>("Mode", Mode.NORMAL));
    private final Setting<Integer> horizontal = register(new Setting<>("Horizontal", 0, 0, 100));
    private final Setting<Integer> vertical = register(new Setting<>("Vertical", 0, 0, 100));
    private final Setting<Integer> ncpHorizontal = register(new Setting<>("NCP_Horizontal", 0, 0, 100));
    private final Setting<Integer> ncpVertical = register(new Setting<>("NCP_Vertical", 0, 0, 100));
    private final Setting<Boolean> cancelExplosions = register(new Setting<>("CancelExplosions", true));

    private boolean wasHurt = false;

    public Velocity() {
        super("Velocity", "Modifies player velocity packets", Category.PLAYER, true, false, false);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> onPacketReceive = event -> {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if (packet.getEntityId() == mc.player.getId()) {
                if (mode.getValue() == Mode.NORMAL) {
                    double velX = packet.getVelocityX() / 8000.0 * (horizontal.getValue() / 100.0);
                    double velY = packet.getVelocityY() / 8000.0 * (vertical.getValue() / 100.0);
                    double velZ = packet.getVelocityZ() / 8000.0 * (horizontal.getValue() / 100.0);

                    mc.player.setVelocity(mc.player.getVelocity().add(
                            velX - mc.player.getVelocity().x,
                            velY - mc.player.getVelocity().y,
                            velZ - mc.player.getVelocity().z
                    ));

                    event.setCancelled(true);
                } else if (mode.getValue() == Mode.NCP) {
                    // Reduce velocity by NCP percentages
                    double velX = packet.getVelocityX() / 8000.0 * (ncpHorizontal.getValue() / 100.0);
                    double velY = packet.getVelocityY() / 8000.0 * (ncpVertical.getValue() / 100.0);
                    double velZ = packet.getVelocityZ() / 8000.0 * (ncpHorizontal.getValue() / 100.0);

                    mc.player.setVelocity(mc.player.getVelocity().add(
                            velX - mc.player.getVelocity().x,
                            velY - mc.player.getVelocity().y,
                            velZ - mc.player.getVelocity().z
                    ));

                    event.setCancelled(true);
                } else if (mode.getValue() == Mode.GRIM || mode.getValue() == Mode.GRIM_V3) {
                    if (mode.getValue() == Mode.GRIM || (mode.getValue() == Mode.GRIM_V3 && !wasHurt)) {
                        event.setCancelled(true);
                    }
                } else if (mode.getValue() == Mode.WALLS && isTouchingWall(mc.player)) {
                    event.setCancelled(true);
                }
            }
        }

        if (event.getPacket() instanceof ExplosionS2CPacket explosion) {
            if (cancelExplosions.getValue()) {
                if (mode.getValue() == Mode.NORMAL) {
                    Vec3d current = mc.player.getVelocity();
                    mc.player.setVelocity(
                            current.x * (horizontal.getValue() / 100.0),
                            current.y * (vertical.getValue() / 100.0),
                            current.z * (horizontal.getValue() / 100.0)
                    );
                } else if (mode.getValue() == Mode.NCP) {
                    Vec3d current = mc.player.getVelocity();
                    mc.player.setVelocity(
                            current.x * (ncpHorizontal.getValue() / 100.0),
                            current.y * (ncpVertical.getValue() / 100.0),
                            current.z * (ncpHorizontal.getValue() / 100.0)
                    );
                } else if (mode.getValue() == Mode.GRIM || (mode.getValue() == Mode.GRIM_V3 && !wasHurt)) {
                    event.setCancelled(true);
                } else if (mode.getValue() == Mode.WALLS && isTouchingWall(mc.player)) {
                    event.setCancelled(true);
                }
            }
        }
    };

    @Override
    public void onTick() {
        if (mode.getValue() == Mode.GRIM_V3) {
            wasHurt = mc.player.hurtTime > 0;
        }
    }

    private boolean isTouchingWall(ClientPlayerEntity player) {
        return player.horizontalCollision;
    }
}

package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.CancelablePacket;
import me.alpha432.oyvey.event.Event;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

import static me.alpha432.oyvey.util.traits.Util.mc;

public abstract class PacketEvent extends Event {

    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class EntityVelocityUpdate {
        private final EntityVelocityUpdateS2CPacket packet;
        private boolean cancelled = false;

        public EntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
            this.packet = packet;
        }

        public EntityVelocityUpdateS2CPacket getPacket() {
            return packet;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static class Explosion {
        private final ExplosionS2CPacket packet;
        private boolean cancelled = false;

        public Explosion(ExplosionS2CPacket packet) {
            this.packet = packet;
        }

        public ExplosionS2CPacket getPacket() {
            return packet;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

}
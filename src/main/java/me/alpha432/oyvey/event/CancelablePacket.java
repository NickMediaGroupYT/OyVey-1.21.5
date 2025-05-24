package me.alpha432.oyvey.event;

public class CancelablePacket<T> {
    private final T packet;
    private boolean cancelled = false;

    public CancelablePacket(T packet) {
        this.packet = packet;
    }

    public T getPacket() {
        return packet;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

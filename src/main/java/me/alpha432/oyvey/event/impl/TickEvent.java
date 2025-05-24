package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.Event;

public class TickEvent extends Event {
    public enum Phase { START, END }

    private final Phase phase;

    public TickEvent(Phase phase) {
        this.phase = phase;
    }

    public Phase getPhase() {
        return phase;
    }
}

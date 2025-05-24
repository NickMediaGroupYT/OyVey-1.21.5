package me.alpha432.oyvey.event;

@FunctionalInterface
public interface Listener<T> {
    void invoke(T event);
}

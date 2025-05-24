package me.alpha432.oyvey.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    private final List<Consumer<Object>> listeners = new ArrayList<>();

    public void register(Consumer<Object> listener) {
        listeners.add(listener);
    }

    public void unregister(Consumer<Object> listener) {
        listeners.remove(listener);
    }

    public void post(Object event) {
        for (Consumer<Object> listener : new ArrayList<>(listeners)) {
            listener.accept(event);
        }
    }
}

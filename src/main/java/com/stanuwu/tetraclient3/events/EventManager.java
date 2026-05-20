package com.stanuwu.tetraclient3.events;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {
    @Getter
    private static final EventManager instance = new EventManager();

    private final HashMap<String, List<EventRegistryEntry<BaseEvent<?>>>> eventRegistry = new HashMap<>();

    private EventManager() {
    }

    private record EventRegistryEntry<T extends BaseEvent<?>>(Consumer<T> consumer, EventOrder order) {
    }

    /**
     * Register an event listener.
     *
     * @param clazz    event class
     * @param consumer event handler function
     * @param order    event order
     */
    public <T extends BaseEvent<?>> void registerEvent(Class<T> clazz, Consumer<T> consumer, EventOrder order) {
        EventRegistryEntry<T> entry = new EventRegistryEntry<>(consumer, order);
        String key = clazz.getCanonicalName();
        if (eventRegistry.containsKey(key)) {
            //noinspection unchecked
            eventRegistry.get(key).add((EventRegistryEntry<BaseEvent<?>>) entry);
        } else {
            List<EventRegistryEntry<BaseEvent<?>>> entries = new ArrayList<>();
            //noinspection unchecked
            entries.add((EventRegistryEntry<BaseEvent<?>>) entry);
            eventRegistry.put(key, entries);
        }
    }

    /**
     * Trigger an event.
     *
     * @param event event object
     */
    public void fireEvent(BaseEvent<?> event) {
        String key = event.getClass().getCanonicalName();
        if (!eventRegistry.containsKey(key)) return;
        List<EventRegistryEntry<BaseEvent<?>>> entries = eventRegistry.get(key);
        for (EventOrder value : EventOrder.values()) {
            for (EventRegistryEntry<BaseEvent<?>> entry : entries) {
                if (entry.order != value) continue;
                entry.consumer.accept(event);
                if (event.isCancelled()) return;
            }
        }
    }
}

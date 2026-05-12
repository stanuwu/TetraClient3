package com.stanuwu.tetraclient3.events;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {
    @Getter
    private static final EventManager instance = new EventManager();

    private final HashMap<String, List<EventRegistryEntry>> eventRegistry = new HashMap<>();

    private EventManager() {
    }

    private record EventRegistryEntry(Consumer<BaseEvent<?>> consumer, EventOrder order) {
    }

    public void registerEvent(Class<? extends BaseEvent<?>> clazz, Consumer<BaseEvent<?>> consumer, EventOrder order) {
        EventRegistryEntry entry = new EventRegistryEntry(consumer, order);
        String key = clazz.getCanonicalName();
        if (eventRegistry.containsKey(key)) {
            eventRegistry.get(key).add(entry);
        } else {
            List<EventRegistryEntry> entries = new ArrayList<>();
            entries.add(entry);
            eventRegistry.put(key, entries);
        }
    }

    public void fireEvent(BaseEvent<?> event) {
        String key = event.getClass().getCanonicalName();
        if (!eventRegistry.containsKey(key)) return;
        List<EventRegistryEntry> entries = eventRegistry.get(key);
        for (EventOrder value : EventOrder.values()) {
            for (EventRegistryEntry entry : entries) {
                if (entry.order != value) continue;
                entry.consumer.accept(event);
                if (event.isCancelled()) return;
            }
        }
    }
}

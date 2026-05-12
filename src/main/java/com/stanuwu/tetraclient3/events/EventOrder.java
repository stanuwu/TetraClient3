package com.stanuwu.tetraclient3.events;

/**
 * Order of event execution, subscribers using the same order will not be called in a deterministic order within the same module.
 * WATCH should only be used to observe the event and not modify or cancel the event at all.
 */
public enum EventOrder {
    FIRST,
    EARLY,
    NORMAL,
    LATE,
    LAST,
    WATCH
}

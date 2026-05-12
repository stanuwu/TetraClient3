package com.stanuwu.tetraclient3.events;

import lombok.Getter;

public abstract class BaseEvent<T> {
    public BaseEvent(T data, boolean canCancel) {
        this.data = data;
        this.canCancel = canCancel;
    }

    @Getter
    private final T data;

    @Getter
    private final boolean canCancel;

    private boolean cancelled = false;

    void cancel() {
        if (this.canCancel) this.cancelled = true;
    }

    boolean isCancelled() {
        return this.cancelled;
    }
}

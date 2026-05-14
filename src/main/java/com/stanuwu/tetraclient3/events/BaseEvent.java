package com.stanuwu.tetraclient3.events;

import lombok.Getter;

@Getter
public abstract class BaseEvent<T> {
    public BaseEvent(T data, boolean canCancel) {
        this.data = data;
        this.canCancel = canCancel;
    }

    private final T data;

    private final boolean canCancel;

    private boolean cancelled = false;

    public void cancel() {
        if (this.canCancel) this.cancelled = true;
    }

}

package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.EmptyEventData;

public class ClientExitEvent extends BaseEvent<EmptyEventData> {
    public ClientExitEvent() {
        super(new EmptyEventData(), false);
    }
}

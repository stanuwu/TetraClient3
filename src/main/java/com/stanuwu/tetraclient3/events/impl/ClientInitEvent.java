package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.EmptyEventData;

public class ClientInitEvent extends BaseEvent<EmptyEventData> {
    public ClientInitEvent() {
        super(new EmptyEventData(), false);
    }
}

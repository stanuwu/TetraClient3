package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.EmptyEventData;

public class ImGuiInitEvent extends BaseEvent<EmptyEventData> {
    public ImGuiInitEvent() {
        super(new EmptyEventData(), false);
    }
}

package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.impl.context.ReceivePacketContext;

public class ReceivePacketEvent extends BaseEvent<ReceivePacketContext> {
    public ReceivePacketEvent(ReceivePacketContext data) {
        super(data, true);
    }
}

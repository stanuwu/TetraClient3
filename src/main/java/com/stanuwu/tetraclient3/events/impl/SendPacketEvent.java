package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.impl.context.SendPacketContext;

public class SendPacketEvent extends BaseEvent<SendPacketContext> {
    public SendPacketEvent(SendPacketContext data) {
        super(data, true);
    }
}

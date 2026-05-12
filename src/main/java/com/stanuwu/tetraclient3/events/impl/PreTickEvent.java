package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import net.minecraft.client.Minecraft;

public class PreTickEvent extends BaseEvent<Minecraft> {
    public PreTickEvent(Minecraft data) {
        super(data, false);
    }
}

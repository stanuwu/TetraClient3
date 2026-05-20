package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import net.minecraft.client.Minecraft;

public class PreRenderEvent extends BaseEvent<Minecraft> {
    public PreRenderEvent(Minecraft data) {
        super(data, false);
    }
}

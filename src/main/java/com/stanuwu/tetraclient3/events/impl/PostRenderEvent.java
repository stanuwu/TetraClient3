package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import net.minecraft.client.Minecraft;

public class PostRenderEvent extends BaseEvent<Minecraft> {
    public PostRenderEvent(Minecraft data) {
        super(data, false);
    }
}

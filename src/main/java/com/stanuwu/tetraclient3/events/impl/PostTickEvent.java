package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import net.minecraft.client.Minecraft;

public class PostTickEvent extends BaseEvent<Minecraft> {
    public PostTickEvent(Minecraft data) {
        super(data, false);
    }
}

package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.impl.context.RenderOverlayContext;

public class RenderHudEvent extends BaseEvent<RenderOverlayContext> {
    public RenderHudEvent(RenderOverlayContext data) {
        super(data, false);
    }
}

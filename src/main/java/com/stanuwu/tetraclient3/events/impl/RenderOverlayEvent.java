package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.impl.context.RenderOverlayContext;

public class RenderOverlayEvent extends BaseEvent<RenderOverlayContext> {
    public RenderOverlayEvent(RenderOverlayContext data) {
        super(data, false);
    }
}

package com.stanuwu.tetraclient3.events.impl;

import com.stanuwu.tetraclient3.events.BaseEvent;
import com.stanuwu.tetraclient3.events.impl.context.GetBlockCollisionsContext;

public class GetBlockCollisionsEvent extends BaseEvent<GetBlockCollisionsContext> {
    public GetBlockCollisionsEvent(GetBlockCollisionsContext data) {
        super(data, false);
    }
}

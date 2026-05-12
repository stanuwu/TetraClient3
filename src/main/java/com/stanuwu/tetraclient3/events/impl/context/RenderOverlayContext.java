package com.stanuwu.tetraclient3.events.impl.context;

import imgui.ImDrawList;

public record RenderOverlayContext(float scale, ImDrawList drawList) {
}

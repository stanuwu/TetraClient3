package com.stanuwu.tetraclient3.module.impl.hud;

import net.minecraft.world.phys.Vec2;

public enum AlignPosition {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    CENTER,
    TOP_LEFT,
    BOTTOM_LEFT,
    TOP_RIGHT,
    BOTTOM_RIGHT;

    public Vec2 getPosition(float width, float height, float offsetX, float offsetY) {
        float centerX = width / 2;
        float centerY = height / 2;
        return switch (this) {
            case TOP -> new Vec2(centerX + offsetX, offsetY);
            case BOTTOM -> new Vec2(centerX + offsetX, height + offsetY);
            case LEFT -> new Vec2(offsetX, centerY + offsetY);
            case RIGHT -> new Vec2(width + offsetX, centerY + offsetY);
            case CENTER -> new Vec2(centerX + offsetX, centerY + offsetY);
            case TOP_LEFT -> new Vec2(offsetX, offsetY);
            case BOTTOM_LEFT -> new Vec2(offsetX, height + offsetY);
            case TOP_RIGHT -> new Vec2(width + offsetX, offsetY);
            case BOTTOM_RIGHT -> new Vec2(width + offsetX, height + offsetY);
        };
    }
}

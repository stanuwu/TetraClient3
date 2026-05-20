package com.stanuwu.tetraclient3.util;

import com.stanuwu.tetraclient3.render.OverlayUtil;
import imgui.ImGui;
import lombok.experimental.UtilityClass;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.geom.Rectangle2D;

@UtilityClass
public class MathUtil {
    /**
     * Project a 3d AABB to a 2d rect
     *
     * @param aabb bounding box
     * @return rectangle
     */
    public Rectangle2D.Float aabbToRect(AABB aabb) {
        Vec3 corner1 = aabb.getMinPosition();
        Vec3 corner2 = aabb.getMaxPosition();

        Vec2[] positions = {
                OverlayUtil.worldToScreen(corner1),
                OverlayUtil.worldToScreen(new Vec3(corner1.x, corner1.y, corner2.z)),
                OverlayUtil.worldToScreen(new Vec3(corner1.x, corner2.y, corner1.z)),
                OverlayUtil.worldToScreen(new Vec3(corner1.x, corner2.y, corner2.z)),
                OverlayUtil.worldToScreen(new Vec3(corner2.x, corner1.y, corner1.z)),
                OverlayUtil.worldToScreen(new Vec3(corner2.x, corner1.y, corner2.z)),
                OverlayUtil.worldToScreen(new Vec3(corner2.x, corner2.y, corner1.z)),
                OverlayUtil.worldToScreen(corner2)
        };

        float smallestX = Float.MAX_VALUE;
        float largestX = Float.MIN_VALUE;
        float smallestY = Float.MAX_VALUE;
        float largestY = Float.MIN_VALUE;

        for (Vec2 position : positions) {
            if (position.x < smallestX) smallestX = position.x;
            if (position.x > largestX) largestX = position.x;
            if (position.y < smallestY) smallestY = position.y;
            if (position.y > largestY) largestY = position.y;
        }

        return new Rectangle2D.Float(smallestX, smallestY, largestX - smallestX, largestY - smallestY);
    }

    /**
     * Check if a rectangle on the screen intersects with the fov circle.
     *
     * @param fov  fov size
     * @param rect rectangle
     * @return does intersect
     */
    public boolean rectInFov(double fov, Rectangle2D.Float rect) {
        float cx = ImGui.getIO().getDisplaySizeX() / 2;
        float cy = ImGui.getIO().getDisplaySizeY() / 2;

        double closestX = Math.clamp(cx, rect.getMinX(), rect.getMaxX());
        double closestY = Math.clamp(cy, rect.getMinY(), rect.getMaxY());

        double dx = cx - closestX;
        double dy = cy - closestY;

        return (dx * dx + dy * dy) <= (fov * fov);
    }

    /**
     * Check the distance from the rectangle to the screen center.
     *
     * @param rect rectangle
     * @return distance of rectangle from screen center
     */
    public double rectFovDistance(Rectangle2D.Float rect) {
        float cx = ImGui.getIO().getDisplaySizeX() / 2;
        float cy = ImGui.getIO().getDisplaySizeY() / 2;

        double closestX = Math.clamp(cx, rect.getMinX(), rect.getMaxX());
        double closestY = Math.clamp(cy, rect.getMinY(), rect.getMaxY());

        double dx = cx - closestX;
        double dy = cy - closestY;

        return Math.sqrt(dx * dx + dy * dy);
    }
}

package com.stanuwu.tetraclient3.render;

import com.stanuwu.tetraclient3.events.EventManager;
import com.stanuwu.tetraclient3.events.impl.RenderHudEvent;
import com.stanuwu.tetraclient3.events.impl.RenderOverlayEvent;
import com.stanuwu.tetraclient3.events.impl.context.RenderOverlayContext;
import com.stanuwu.tetraclient3.util.ColorUtil;
import imgui.ImDrawList;
import imgui.ImGui;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glTexParameteri;

@UtilityClass
public class OverlayUtil {
    public void doOverlayPass(float scale) {
        // only render while in game
        if (Minecraft.getInstance().level == null) return;

        ImDrawList drawList = ImGui.getBackgroundDrawList();

        // draw overlay
        EventManager.getInstance().fireEvent(new RenderOverlayEvent(new RenderOverlayContext(scale, drawList)));

        // draw hud
        EventManager.getInstance().fireEvent(new RenderHudEvent(new RenderOverlayContext(scale, drawList)));
    }

    @Getter
    @Setter
    private Matrix4f viewRotationProjectionMatrix = new Matrix4f();
    @Getter
    @Setter
    private Vec3 cameraPosition = new Vec3(0, 0, 0);

    /**
     * Transform world position to screen position.
     *
     * @param x world x
     * @param y world y
     * @param z world z
     * @return screen position
     */
    public Vec2 worldToScreen(float x, float y, float z) {
        return worldToScreen(new Vec3(x, y, z));
    }

    /**
     * Transform world position to screen position.
     *
     * @param pos world position
     * @return screen position
     */
    public Vec2 worldToScreen(Vec3 pos) {
        float width = ImGui.getIO().getDisplaySizeX() / 2f;
        float height = ImGui.getIO().getDisplaySizeY() / 2f;

        Vector4f relative = new Vector4f((float) (pos.x() - cameraPosition.x()), (float) (pos.y() - cameraPosition.y()), (float) (pos.z() - cameraPosition.z()), 1.0f);
        Vector4f screen = viewRotationProjectionMatrix.transform(relative);

        Vec2 screenScaled = new Vec2(screen.x() / screen.w(), screen.y() / screen.w());

        // approximate off-screen position
        if (screen.w <= 0.0f) {
            screenScaled = new Vec2(screenScaled.x * -1000f, screenScaled.y * -1000f);
        }

        return new Vec2(width + screenScaled.x * width, height - screenScaled.y * height);
    }

    /**
     * Determine if a 3d point is visible on screen.
     *
     * @param x x
     * @param y y
     * @param z z
     * @return is visible
     */
    public boolean isOnScreen(float x, float y, float z) {
        return isOnScreen(new Vec3(x, y, z));
    }

    /**
     * Determine if a 3d point is visible on screen.
     *
     * @param pos pos
     * @return is visible
     */
    public boolean isOnScreen(Vec3 pos) {
        Vector4f relative = new Vector4f((float) (pos.x() - cameraPosition.x()), (float) (pos.y() - cameraPosition.y()), (float) (pos.z() - cameraPosition.z()), 0.0f);
        Vector4f screen = viewRotationProjectionMatrix.transform(relative);

        if (screen.z <= 0.5f) return false;

        Vec2 toScreen = worldToScreen(pos);

        float width = ImGui.getIO().getDisplaySizeX();
        float height = ImGui.getIO().getDisplaySizeY();

        return toScreen.x > 0 && toScreen.x < width && toScreen.y > 0 && toScreen.y < height;
    }

    /**
     * Determines if any part of area between the 3d points is visible on screen.
     *
     * @param corner1 corner 1
     * @param corner2 corner 2
     * @return part of the area bound by these corners is visible
     */
    public boolean squareOnScreen(Vec3 corner1, Vec3 corner2) {
        return isOnScreen(corner1) || isOnScreen(new Vec3(corner1.x, corner1.y, corner2.z)) || isOnScreen(new Vec3(corner1.x, corner2.y, corner1.z)) || isOnScreen(new Vec3(corner1.x, corner2.y, corner2.z)) || isOnScreen(new Vec3(corner2.x, corner1.y, corner1.z)) || isOnScreen(new Vec3(corner2.x, corner1.y, corner2.z)) || isOnScreen(new Vec3(corner2.x, corner2.y, corner1.z)) || isOnScreen(corner2);
    }

    /**
     * Get the on-screen length of two world points.
     *
     * @param pos1 first position
     * @param pos2 second position
     * @return visual distance
     */
    public float worldToScreenDist(Vec3 pos1, Vec3 pos2) {
        Vec2 screen1 = worldToScreen(pos1);
        Vec2 screen2 = worldToScreen(pos2);
        return (float) Math.sqrt((float) Math.pow((screen1.x - screen2.x), 2) + Math.pow((screen2.y - screen1.y), 2));
    }

    /**
     * Draw a line between two points in 3d space.
     *
     * @param pos1      point 1
     * @param pos2      point 2
     * @param drawList  draw list
     * @param color     color
     * @param thickness line thickness
     */
    public void line(Vec3 pos1, Vec3 pos2, ImDrawList drawList, Color color, float thickness) {
        Vec2 screen1 = worldToScreen(pos1);
        Vec2 screen2 = worldToScreen(pos2);
        drawList.addLine(screen1.x, screen1.y, screen2.x, screen2.y, ColorUtil.colorToImGuiColor(color), thickness);
    }

    /**
     * Draw a 3d outline over a square object in 3d space.
     *
     * @param corner1   top left behind corner
     * @param corner2   bottom right front corner
     * @param drawList  draw list
     * @param color     color
     * @param thickness line thickness
     */
    public void outline3(Vec3 corner1, Vec3 corner2, ImDrawList drawList, Color color, float thickness) {
        Vec2 ttt = worldToScreen(corner1);
        Vec2 ttb = worldToScreen(new Vec3(corner1.x, corner1.y, corner2.z));
        Vec2 tbt = worldToScreen(new Vec3(corner1.x, corner2.y, corner1.z));
        Vec2 tbb = worldToScreen(new Vec3(corner1.x, corner2.y, corner2.z));
        Vec2 btt = worldToScreen(new Vec3(corner2.x, corner1.y, corner1.z));
        Vec2 btb = worldToScreen(new Vec3(corner2.x, corner1.y, corner2.z));
        Vec2 bbt = worldToScreen(new Vec3(corner2.x, corner2.y, corner1.z));
        Vec2 bbb = worldToScreen(corner2);

        drawList.addLine(ttt.x, ttt.y, ttb.x, ttb.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(ttb.x, ttb.y, tbb.x, tbb.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(tbb.x, tbb.y, tbt.x, tbt.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(tbt.x, tbt.y, ttt.x, ttt.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(btt.x, btt.y, btb.x, btb.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(btb.x, btb.y, bbb.x, bbb.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(bbb.x, bbb.y, bbt.x, bbt.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(bbt.x, bbt.y, btt.x, btt.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(ttt.x, ttt.y, btt.x, btt.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(ttb.x, ttb.y, btb.x, btb.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(tbt.x, tbt.y, bbt.x, bbt.y, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(tbb.x, tbb.y, bbb.x, bbb.y, ColorUtil.colorToImGuiColor(color), thickness);
    }

    /**
     * Draw a projected 2d outline over a square object in 3d space.
     *
     * @param corner1   top left behind corner
     * @param corner2   bottom right front corner
     * @param drawList  draw list
     * @param color     color
     * @param thickness line thickness
     */
    public void outline2(Vec3 corner1, Vec3 corner2, ImDrawList drawList, Color color, float thickness) {
        Vec2[] positions = {
                worldToScreen(corner1),
                worldToScreen(new Vec3(corner1.x, corner1.y, corner2.z)),
                worldToScreen(new Vec3(corner1.x, corner2.y, corner1.z)),
                worldToScreen(new Vec3(corner1.x, corner2.y, corner2.z)),
                worldToScreen(new Vec3(corner2.x, corner1.y, corner1.z)),
                worldToScreen(new Vec3(corner2.x, corner1.y, corner2.z)),
                worldToScreen(new Vec3(corner2.x, corner2.y, corner1.z)),
                worldToScreen(corner2)
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

        drawList.addLine(largestX, largestY, largestX, smallestY, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(largestX, largestY, smallestX, largestY, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(smallestX, smallestY, largestX, smallestY, ColorUtil.colorToImGuiColor(color), thickness);
        drawList.addLine(smallestX, smallestY, smallestX, largestY, ColorUtil.colorToImGuiColor(color), thickness);
    }

    /**
     * Loads a texture onto the gpu
     *
     * @param bytes texture file
     * @return gl texture id
     */
    public int loadTexture(byte[] bytes) {
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(bytes.length);
        imageBuffer.put(bytes);
        imageBuffer.flip();

        // Image metadata
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // Decode image
        ByteBuffer pixels = STBImage.stbi_load_from_memory(
                imageBuffer,
                width,
                height,
                channels,
                4 // Force RGBA
        );

        if (pixels == null) {
            throw new RuntimeException(
                    "Failed to load image: " + STBImage.stbi_failure_reason()
            );
        }

        int texId = GL11.glGenTextures();

        GL11.glBindTexture(GL_TEXTURE_2D, texId);

        // Texture filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Upload texture to GPU
        glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
        GL11.glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width.get(0),
                height.get(0),
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                pixels
        );

        // Unbind
        GL11.glBindTexture(GL_TEXTURE_2D, 0);

        // Free CPU image memory
        STBImage.stbi_image_free(pixels);

        return texId;
    }
}

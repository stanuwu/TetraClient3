package com.stanuwu.tetraclient3.util;

import imgui.ImGui;
import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class ColorUtil {
    /**
     * Converts a color into an imgui int color
     *
     * @param color color
     * @return imgui color
     */
    public int colorToImGuiColor(Color color) {
        return ImGui.getColorU32(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }
}

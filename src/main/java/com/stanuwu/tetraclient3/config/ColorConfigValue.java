package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.render.TetraCustomImGui;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.function.Supplier;

public class ColorConfigValue extends AbstractConfigValue<Color> {
    ColorConfigValue(String name, Color initial, @Nullable Supplier<Boolean> conditional) {
        super(name, initial, conditional);
    }

    public ColorConfigValue(String name, Color initial) {
        super(name, initial);
    }

    @Override
    protected void drawInternal(float scale) {
        float[] color = new float[]{
                getValue().getRed() / 255f, getValue().getGreen() / 255f, getValue().getBlue() / 255f, getValue().getAlpha() / 255f
        };

        TetraCustomImGui.colorPicker(getName() + "##" + this.getParent().getName(), color, scale);
        setValue(new Color(color[0], color[1], color[2], color[3]));
    }

    @Override
    Color deserialize(String s) {
        int r = Integer.parseInt(s.substring(0, 2), 16);
        int g = Integer.parseInt(s.substring(2, 4), 16);
        int b = Integer.parseInt(s.substring(4, 6), 16);
        int a = Integer.parseInt(s.substring(6, 8), 16);

        return new Color(r, g, b, a);
    }

    @Override
    String serialize(Color v) {
        return String.format(
                "%02X%02X%02X%02X",
                v.getRed(),
                v.getGreen(),
                v.getBlue(),
                v.getAlpha()
        );
    }
}

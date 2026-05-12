package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.render.TetraCustomImGui;
import lombok.Getter;

import java.util.function.Supplier;

public class FloatSliderValue extends AbstractConfigValue<Float> {

    @Getter
    private final float min;
    @Getter
    private final float max;

    public FloatSliderValue(String name, Float initial, Float min, Float max, Supplier<Boolean> conditional) {
        super(name, initial, conditional);
        this.min = min;
        this.max = max;
    }

    public FloatSliderValue(String name, Float initial, Float min, Float max) {
        super(name, initial);
        this.min = min;
        this.max = max;
    }

    @Override
    public void drawInternal(float scale) {
        this.setValue(TetraCustomImGui.floatSlider(this.getName() + "##" + this.getParent().getName(), this.getValue(), this.getMin(), this.getMax(), scale));
    }

    @Override
    Float deserialize(String s) {
        return Float.valueOf(s);
    }

    @Override
    String serialize(Float v) {
        return v.toString();
    }
}

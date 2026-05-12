package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.render.TetraCustomImGui;
import lombok.Getter;

import java.util.function.Supplier;

public class IntSliderValue extends AbstractConfigValue<Integer> {

    @Getter
    private final int min;
    @Getter
    private final int max;

    public IntSliderValue(String name, Integer initial, Integer min, Integer max, Supplier<Boolean> conditional) {
        super(name, initial, conditional);
        this.min = min;
        this.max = max;
    }

    public IntSliderValue(String name, Integer initial, Integer min, Integer max) {
        super(name, initial);
        this.min = min;
        this.max = max;
    }

    @Override
    public void drawInternal(float scale) {
        this.setValue(TetraCustomImGui.intSlider(this.getName() + "##" + this.getParent().getName(), this.getValue(), this.getMin(), this.getMax(), scale));
    }

    @Override
    Integer deserialize(String s) {
        return Integer.valueOf(s);
    }

    @Override
    String serialize(Integer v) {
        return v.toString();
    }
}

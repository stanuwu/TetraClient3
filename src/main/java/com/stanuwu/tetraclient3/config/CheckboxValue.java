package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.render.TetraCustomImGui;

import java.util.function.Supplier;

public class CheckboxValue extends AbstractConfigValue<Boolean> {

    public CheckboxValue(String name, Boolean initial, Supplier<Boolean> conditional) {
        super(name, initial, conditional);
    }

    public CheckboxValue(String name, Boolean initial) {
        super(name, initial);
    }

    @Override
    public void drawInternal(float scale) {
        this.setValue(TetraCustomImGui.toggleSwitch(this.getName() + "##" + this.getParent().getName(), this.getValue(), scale));
    }

    @Override
    Boolean deserialize(String s) {
        return Boolean.valueOf(s);
    }

    @Override
    String serialize(Boolean v) {
        return v.toString();
    }
}

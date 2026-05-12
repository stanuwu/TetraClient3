package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.render.TetraCustomImGui;

import java.util.function.Supplier;

public class TextValue extends AbstractConfigValue<String> {
    public TextValue(String name, String initial, Supplier<Boolean> conditional) {
        super(name, initial, conditional);
    }

    public TextValue(String name, String initial) {
        super(name, initial);
    }

    @Override
    public void drawInternal(float scale) {
        this.setValue(TetraCustomImGui.textBox(this.getName() + "##" + this.getParent().getName(), this.getValue(), scale));
    }

    @Override
    String deserialize(String s) {
        return s;
    }

    @Override
    String serialize(String v) {
        return v;
    }
}

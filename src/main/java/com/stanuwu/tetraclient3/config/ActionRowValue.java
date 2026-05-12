package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.render.TetraCustomImGui;

import java.util.function.Supplier;

public class ActionRowValue extends AbstractConfigValue<Runnable> {

    public ActionRowValue(String name, Runnable initial, Supplier<Boolean> conditional) {
        super(name, initial, conditional);
    }

    public ActionRowValue(String name, Runnable initial) {
        super(name, initial);
    }

    @Override
    public void drawInternal(float scale) {
        if (TetraCustomImGui.button(this.getName(), scale)) {
            this.getValue().run();
        }
    }

    @Override
    Runnable deserialize(String s) {
        return null;
    }

    @Override
    String serialize(Runnable v) {
        return "";
    }

    @Override
    public void setFromString(String s) {
        // pass
    }

    @Override
    public String getAsString() {
        return "";
    }
}

package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.render.TetraCustomImGui;
import lombok.Getter;

import java.util.function.Supplier;

public class EnumValue<T extends Enum<T>> extends AbstractConfigValue<T> {
    @Getter
    private final Class<T> clazz;

    public EnumValue(String name, T initial, Class<T> clazz, Supplier<Boolean> conditional) {
        this.clazz = clazz;
        super(name, initial, conditional);
    }

    public EnumValue(String name, T initial, Class<T> clazz) {
        this.clazz = clazz;
        super(name, initial);
    }

    @Override
    public void drawInternal(float scale) {
        this.setValue(TetraCustomImGui.enumDropdown(this.getName() + "##" + this.getParent().getName(), this.getValue(), this.getValues(), scale));
    }

    public T[] getValues() {
        return clazz.getEnumConstants();
    }

    @Override
    T deserialize(String s) {
        return Enum.valueOf(clazz, s);
    }

    @Override
    String serialize(T v) {
        return v.name();
    }
}

package com.stanuwu.tetraclient3.config;

import com.stanuwu.tetraclient3.module.AbstractModule;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Represents a value that has a config value and an option associated with it.
 *
 * @param <T> type of the value
 */
public abstract class AbstractConfigValue<T> {
    AbstractConfigValue(String name, T initial, @Nullable Supplier<Boolean> conditional) {
        this.name = name;
        this.value = initial;
        this.conditional = conditional;
    }

    AbstractConfigValue(String name, T initial) {
        this.name = name;
        this.value = initial;
        this.conditional = null;
    }

    @Getter
    @Setter
    private AbstractModule parent = null;

    @Getter
    private final String name;

    @Getter
    @Setter
    private T value;

    @Nullable
    @Getter
    private final Supplier<Boolean> conditional;

    /**
     * Calls draw if the element should be rendered
     *
     * @param scale ui scale
     * @return element was rendered
     */
    public boolean draw(float scale) {
        if (conditional == null || conditional.get()) {
            drawInternal(scale);
            return true;
        }
        return false;
    }

    /**
     * Renders the menu element onto the screen.
     *
     * @param scale ui scale
     */
    protected abstract void drawInternal(float scale);

    /**
     * Returns the value from the given string.
     *
     * @param s Serialized Value
     * @return Value
     */
    abstract T deserialize(String s);

    /**
     * Returns a string from the given value.
     *
     * @param v Value
     * @return Serialized Value
     */
    abstract String serialize(T v);

    /**
     * Set the value from a string representation.
     *
     * @param s Serialized Value
     */
    public void setFromString(String s) {
        this.value = deserialize(s);
    }

    /**
     * Get the values as a string
     *
     * @return Serialized Value
     */
    public String getAsString() {
        return serialize(this.value);
    }
}

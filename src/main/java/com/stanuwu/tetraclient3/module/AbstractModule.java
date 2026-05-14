package com.stanuwu.tetraclient3.module;

import com.stanuwu.tetraclient3.config.AbstractConfigValue;
import com.stanuwu.tetraclient3.events.EventManager;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractModule {
    boolean hasCachedConfigValues = false;
    private final List<AbstractConfigValue<?>> configValues = new ArrayList<>();
    @Getter
    private final ModuleCategory category;
    @Getter
    private final String name;

    public AbstractModule(ModuleCategory category, String name) {
        this.category = category;
        this.name = name;
        this.registerEventConsumers();
    }

    private void registerEventConsumers() {
        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            if (!declaredMethod.isAnnotationPresent(EventSubscriber.class)) continue;
            EventSubscriber data = declaredMethod.getAnnotation(EventSubscriber.class);
            EventManager.getInstance().registerEvent(data.event(), (e) -> {
                try {
                    declaredMethod.setAccessible(true);
                    declaredMethod.invoke(this, e);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    // pass
                }
            }, data.order());
        }
    }

    /**
     * Register a config value with this class.
     *
     * @param in  Config value
     * @param <V> Config value type
     * @return Config value
     */
    protected <V extends AbstractConfigValue<?>> V reg(V in) {
        in.setParent(this);
        return in;
    }

    /**
     * Resolves all config values and caches them.
     *
     * @return List of all config values.
     */
    public List<AbstractConfigValue<?>> getConfigValues() {
        if (!hasCachedConfigValues) {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (AbstractConfigValue.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        AbstractConfigValue<?> value = (AbstractConfigValue<?>) field.get(this);
                        if (value != null) this.configValues.add(value);
                    } catch (IllegalAccessException e) {
                        // pass
                    }
                }
            }
            this.hasCachedConfigValues = true;
        }
        return this.configValues;
    }

    /**
     * Return all config options of a module.
     *
     * @return Config options mapped by name.
     */
    public Map<String, String> getConfigMap() {
        Map<String, String> map = new HashMap<>();
        for (AbstractConfigValue<?> value : this.getConfigValues()) {
            map.put(value.getName(), value.getAsString());
        }
        return map;
    }

    /**
     * Set all config options of a module.
     *
     * @param config Config options mapped by name.
     */
    public void loadConfigMap(Map<String, String> config) {
        for (AbstractConfigValue<?> value : this.getConfigValues()) {
            value.setFromString(config.get(value.getName()));
        }
    }
}

package com.stanuwu.tetraclient3.module;

import com.stanuwu.tetraclient3.module.impl.config.FilesModule;
import com.stanuwu.tetraclient3.module.impl.hud.WaifuModule;
import com.stanuwu.tetraclient3.module.impl.hud.WatermarkModule;
import com.stanuwu.tetraclient3.module.impl.render.PlayerEspModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds modules.
 */
public class ModuleManager {
    @Getter
    private static final ModuleManager instance = new ModuleManager();

    @Getter
    private final List<AbstractModule<?>> modules = new ArrayList<>();

    private void reg(AbstractModule<?>... module) {
        modules.addAll(List.of(module));
    }

    /**
     * Get instance of a module based on the class
     *
     * @param clazz module class
     * @param <T>   module class type
     * @return module instance
     */
    public <T extends AbstractModule<T>> T getModule(Class<T> clazz) {
        for (AbstractModule<?> module : modules) {
            if (module.getClass().equals(clazz)) //noinspection unchecked
                return (T) module;
        }
        throw new RuntimeException("Module not registered!");
    }

    /**
     * Register modules here.
     */
    private ModuleManager() {
        reg(new PlayerEspModule(), new WatermarkModule(), new WaifuModule(), new FilesModule(this));
    }
}

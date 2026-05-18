package com.stanuwu.tetraclient3.module;

import com.stanuwu.tetraclient3.module.impl.combat.AimModule;
import com.stanuwu.tetraclient3.module.impl.combat.ClickerModule;
import com.stanuwu.tetraclient3.module.impl.combat.ReachModule;
import com.stanuwu.tetraclient3.module.impl.combat.TotemModule;
import com.stanuwu.tetraclient3.module.impl.config.FilesModule;
import com.stanuwu.tetraclient3.module.impl.exploit.DisablerModule;
import com.stanuwu.tetraclient3.module.impl.hud.WaifuModule;
import com.stanuwu.tetraclient3.module.impl.hud.WatermarkModule;
import com.stanuwu.tetraclient3.module.impl.misc.ClientSpoofModule;
import com.stanuwu.tetraclient3.module.impl.misc.NoGuiCloseModule;
import com.stanuwu.tetraclient3.module.impl.misc.ResourcePackSpoofModule;
import com.stanuwu.tetraclient3.module.impl.movement.ClipModule;
import com.stanuwu.tetraclient3.module.impl.movement.FlightModule;
import com.stanuwu.tetraclient3.module.impl.movement.JesusModule;
import com.stanuwu.tetraclient3.module.impl.movement.NoFallModule;
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
    private final List<AbstractModule> modules = new ArrayList<>();

    private void reg(AbstractModule... module) {
        modules.addAll(List.of(module));
    }

    /**
     * Get instance of a module based on the class
     *
     * @param clazz module class
     * @param <T>   module class type
     * @return module instance
     */
    public <T extends AbstractModule> T getModule(Class<T> clazz) {
        for (AbstractModule module : modules) {
            if (module.getClass().equals(clazz)) //noinspection unchecked
                return (T) module;
        }
        throw new RuntimeException("Module not registered!");
    }

    /**
     * Register modules here.
     */
    private ModuleManager() {
        reg(
                new ReachModule(), new ClickerModule(), new AimModule(), new TotemModule(),
                new PlayerEspModule(),
                new FlightModule(), new NoFallModule(), new JesusModule(), new ClipModule(),
                new DisablerModule(),
                new WatermarkModule(), new WaifuModule(),
                new ClientSpoofModule(), new NoGuiCloseModule(), new ResourcePackSpoofModule(),
                new FilesModule(this)
        );
    }
}

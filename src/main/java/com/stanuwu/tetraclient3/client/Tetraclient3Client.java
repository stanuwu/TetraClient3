package com.stanuwu.tetraclient3.client;

import com.stanuwu.tetraclient3.events.EventManager;
import com.stanuwu.tetraclient3.events.impl.ClientExitEvent;
import com.stanuwu.tetraclient3.events.impl.ClientInitEvent;
import com.stanuwu.tetraclient3.events.impl.PostRenderEvent;
import com.stanuwu.tetraclient3.events.impl.PreRenderEvent;
import com.stanuwu.tetraclient3.module.ModuleManager;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;

public class Tetraclient3Client implements ClientModInitializer {
    @Getter
    private static boolean initialized = false;

    @Override
    public void onInitializeClient() {
        // force init modules
        ModuleManager _ = ModuleManager.getInstance();

        // init event manager
        EventManager ev = EventManager.getInstance();

        // set initialized
        Tetraclient3Client.initialized = true;

        // fire init event
        ev.fireEvent(new ClientInitEvent());

        // fire fabric api events
        ClientLifecycleEvents.CLIENT_STOPPING.register((_) -> ev.fireEvent(new ClientExitEvent()));
        LevelRenderEvents.START_MAIN.register((_) -> {
            ev.fireEvent(new PreRenderEvent(Minecraft.getInstance()));
        });
        LevelRenderEvents.END_MAIN.register((_) -> {
            ev.fireEvent(new PostRenderEvent(Minecraft.getInstance()));
        });
    }
}

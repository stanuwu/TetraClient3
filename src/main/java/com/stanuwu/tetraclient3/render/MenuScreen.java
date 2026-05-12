package com.stanuwu.tetraclient3.render;

import com.stanuwu.tetraclient3.client.Defaults;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MenuScreen extends Screen {
    protected MenuScreen() {
        super(Component.literal(Defaults.getDisplayName()));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        ImGuiManager.getInstance().closeMenu();
    }
}

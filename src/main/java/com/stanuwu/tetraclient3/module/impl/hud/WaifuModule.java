package com.stanuwu.tetraclient3.module.impl.hud;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.config.IntSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.ImGuiInitEvent;
import com.stanuwu.tetraclient3.events.impl.RenderHudEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.render.ImGuiManager;
import com.stanuwu.tetraclient3.render.OverlayUtil;
import imgui.ImGui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class WaifuModule extends AbstractModule<WaifuModule> {

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", true));
    private final IntSliderValue size = reg(new IntSliderValue("Size", 36, 8, 128));
    private final FloatSliderValue alpha = reg(new FloatSliderValue("Alpha", 50f, 0f, 100f));
    private final FloatSliderValue x = reg(new FloatSliderValue("Position X", 0.2f, 0f, 100f));
    private final FloatSliderValue y = reg(new FloatSliderValue("Position Y", 3f, 0f, 100f));

    public WaifuModule() {
        super(ModuleCategory.HUD, "Waifu");
    }

    private int textureId = 0;

    @EventSubscriber(event = ImGuiInitEvent.class)
    private void onInit(ImGuiInitEvent event) {
        try {
            byte[] file = Files.readAllBytes(Paths.get(Objects.requireNonNull(ImGuiManager.class.getResource("/assets/images/tetra.png")).toURI()));
            this.textureId = OverlayUtil.loadTexture(file);
        } catch (IOException | URISyntaxException e) {
            textureId = 0;
        }
    }

    @EventSubscriber(event = RenderHudEvent.class)
    private void renderWaifu(RenderHudEvent event) {
        if (!enabled.getValue()) return;

        float trueX = ImGui.getIO().getDisplaySizeX() / 100 * x.getValue();
        float trueY = ImGui.getIO().getDisplaySizeY() / 100 * y.getValue();
        float trueX2 = trueX + 640f / 100f * size.getValue();
        float trueY2 = trueY + 800f / 100f * size.getValue();

        event.getData().drawList().addImage(textureId, trueX, trueY, trueX2, trueY2, 0, 0, 1, 1, ImGui.getColorU32(1f, 1f, 1f, alpha.getValue() / 100f));
    }
}

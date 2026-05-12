package com.stanuwu.tetraclient3.module.impl.hud;

import com.stanuwu.tetraclient3.client.Defaults;
import com.stanuwu.tetraclient3.config.*;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.RenderHudEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.render.ImGuiManager;
import com.stanuwu.tetraclient3.util.ColorUtil;
import imgui.ImGui;

import java.awt.*;

public class WatermarkModule extends AbstractModule<WatermarkModule> {
    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", true));
    private final TextValue text = reg(new TextValue("Text", Defaults.getDisplayName()));
    private final ColorConfigValue color = reg(new ColorConfigValue("Color", Color.CYAN));
    private final IntSliderValue size = reg(new IntSliderValue("Size", 24, 8, 128));

    private final FloatSliderValue x = reg(new FloatSliderValue("Position X", 0.2f, 0f, 100f));

    private final FloatSliderValue y = reg(new FloatSliderValue("Position Y", 0.2f, 0f, 100f));

    public WatermarkModule() {
        super(ModuleCategory.HUD, "Watermark");
    }

    @EventSubscriber(event = RenderHudEvent.class)
    private void renderWatermark(RenderHudEvent event) {
        if (!enabled.getValue()) return;

        float trueX = ImGui.getIO().getDisplaySizeX() / 100 * x.getValue();
        float trueY = ImGui.getIO().getDisplaySizeY() / 100 * y.getValue();

        event.getData().drawList().addText(ImGuiManager.getInstance().getAnta(), (int) (size.getValue() * event.getData().scale()), trueX, trueY, ColorUtil.colorToImGuiColor(color.getValue()), text.getValue());
    }
}

package com.stanuwu.tetraclient3.module.impl.hud;

import com.stanuwu.tetraclient3.client.Defaults;
import com.stanuwu.tetraclient3.config.*;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.RenderHudEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.util.ColorUtil;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import net.minecraft.world.phys.Vec2;

import java.awt.*;

public class WatermarkModule extends AbstractModule {
    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", true));
    private final TextValue text = reg(new TextValue("Text", Defaults.getDisplayName()));
    private final ColorConfigValue color = reg(new ColorConfigValue("Color", Color.CYAN));
    private final EnumValue<HUDFont> font = reg(new EnumValue<>("Font", HUDFont.ANTA, HUDFont.class));
    private final IntSliderValue size = reg(new IntSliderValue("Size", 24, 8, 128));
    private final EnumValue<AlignPosition> align = reg(new EnumValue<>("Align", AlignPosition.TOP_LEFT, AlignPosition.class));
    private final IntSliderValue x = reg(new IntSliderValue("Offset X", 75, -256, 256));
    private final IntSliderValue y = reg(new IntSliderValue("Offset Y", 15, -256, 256));

    public WatermarkModule() {
        super(ModuleCategory.HUD, "Watermark");
    }

    @EventSubscriber(event = RenderHudEvent.class)
    private void renderWatermark(RenderHudEvent event) {
        if (!enabled.getValue()) return;

        // calculate size
        float fontSize = size.getValue() * event.getData().scale();
        ImFont font = this.font.getValue().getFont();
        ImGui.pushFont(font, fontSize);
        ImVec2 size = ImGui.calcTextSize(text.getValue());
        ImGui.popFont();

        Vec2 truePos = this.align.getValue().getPosition(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY(), x.getValue() * event.getData().scale(), y.getValue() * event.getData().scale());

        event.getData().drawList().addText(font, (int) fontSize, truePos.x - size.x / 2, truePos.y - size.y / 2, ColorUtil.colorToImGuiColor(color.getValue()), text.getValue());
    }
}

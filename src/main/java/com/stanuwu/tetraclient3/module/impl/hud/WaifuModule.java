package com.stanuwu.tetraclient3.module.impl.hud;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
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
import net.minecraft.world.phys.Vec2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class WaifuModule extends AbstractModule {

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", true));
    private final IntSliderValue size = reg(new IntSliderValue("Size", 36, 8, 128));
    private final FloatSliderValue alpha = reg(new FloatSliderValue("Alpha", 50f, 0f, 100f));
    private final EnumValue<AlignPosition> align = reg(new EnumValue<>("Align", AlignPosition.BOTTOM_RIGHT, AlignPosition.class));
    private final IntSliderValue x = reg(new IntSliderValue("Offset X", -128, -256, 256));
    private final IntSliderValue y = reg(new IntSliderValue("Offset Y", -128, -256, 256));

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

        float imageWidth = 640f / 100f * event.getData().scale() * this.size.getValue();
        float imageHeight = 800f / 100f * event.getData().scale() * this.size.getValue();

        Vec2 truePos = this.align.getValue().getPosition(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY(), x.getValue() * event.getData().scale(), y.getValue() * event.getData().scale());


        event.getData().drawList().addImage(textureId, truePos.x - imageWidth / 2f, truePos.y - imageHeight / 2f, truePos.x + imageWidth / 2f, truePos.y + imageHeight / 2f, 0, 0, 1, 1, ImGui.getColorU32(1f, 1f, 1f, alpha.getValue() / 100f));
    }
}

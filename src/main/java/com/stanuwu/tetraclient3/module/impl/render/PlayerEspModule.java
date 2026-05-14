package com.stanuwu.tetraclient3.module.impl.render;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.ColorConfigValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.IntSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.RenderOverlayEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.render.OverlayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.AABB;

import java.awt.*;

public class PlayerEspModule extends AbstractModule {
    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", true));
    private final EnumValue<EspType> type = reg(new EnumValue<>("Type", EspType.BOX, EspType.class));
    private final ColorConfigValue color = reg(new ColorConfigValue("Color", Color.RED));
    private final IntSliderValue thickness = reg(new IntSliderValue("Line Thickness", 2, 1, 10));

    public PlayerEspModule() {
        super(ModuleCategory.RENDER, "Player Esp");
    }

    @EventSubscriber(event = RenderOverlayEvent.class)
    private void renderEsp(RenderOverlayEvent event) {
        if (!enabled.getValue()) return;

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        LocalPlayer localPlayer = client.player;
        if (level == null || localPlayer == null) return;
        for (AbstractClientPlayer player : level.players()) {
            if (player.getId() == localPlayer.getId()) continue;

            AABB box = player.getBoundingBox();

            if (OverlayUtil.squareOnScreen(box.getMinPosition(), box.getMaxPosition())) {
                switch (type.getValue()) {
                    case BOX ->
                            OverlayUtil.outline3(box.getMinPosition(), box.getMaxPosition(), event.getData().drawList(), color.getValue(), thickness.getValue());
                    case OUTLINE ->
                            OverlayUtil.outline2(box.getMinPosition(), box.getMaxPosition(), event.getData().drawList(), color.getValue(), thickness.getValue());
                }
            }
        }
    }
}

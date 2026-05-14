package com.stanuwu.tetraclient3.render;

import com.stanuwu.tetraclient3.client.Defaults;
import com.stanuwu.tetraclient3.config.AbstractConfigValue;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.module.ModuleManager;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import lombok.Getter;
import net.minecraft.client.Minecraft;

public class Menu {
    @Getter
    private boolean isOpened = false;

    @Getter
    private ModuleCategory selected = ModuleCategory.COMBAT;

    public void toggle() {
        this.isOpened = !this.isOpened;
        if (isOpened) {
            Minecraft.getInstance().setScreen(new MenuScreen());
        } else {
            Minecraft.getInstance().setScreen(null);
            TetraCustomImGui.clearMaps();
        }
    }

    public void draw(float width, float height, float scale) {
        // render menu
        if (!this.isOpened()) return;

        // set up window
        float fontScale = scale / 8;
        TetraCustomImGui.applyTetraTheme(scale);
        ImGui.begin(Defaults.getDisplayName(), new ImBoolean(true), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | (ImGui.getIO().getKeyShift() ? ImGuiWindowFlags.NoScrollWithMouse : 0));
        ImGui.setWindowFontScale(fontScale);
        float x = 900 * scale;
        float y = 600 * scale;
        ImGui.setWindowSize(x, y);
        ImGui.setWindowPos(width / 2 - x / 2, height / 2 - y / 2);

        // set up split screen
        float totalWidth = ImGui.getContentRegionAvailX();
        float totalHeight = ImGui.getContentRegionAvailY();
        float leftWidth = totalWidth * 0.28f;

        // left side
        ImGui.beginChild(
                "##left_panel",
                leftWidth,
                totalHeight,
                false
        );

        // left content

        // title

        ImGui.pushFont(ImGuiManager.getInstance().getBarcode(), ImGuiManager.FONT_SIZE);
        ImGui.setWindowFontScale(fontScale * 2.5f);
        float center = ImGui.getContentRegionAvailX() / 2 - ImGui.calcTextSizeX(Defaults.getDisplayName()) / 2;
        ImGui.setCursorPosX(center);
        ImGui.setCursorPosY(ImGui.calcTextSizeY("I") / 4);
        ImGui.text(Defaults.getDisplayName());
        ImGui.setWindowFontScale(fontScale);
        ImGui.popFont();
        ImGui.dummy(10 * scale, 10 * scale);

        // nav menu
        ImGui.setWindowFontScale(fontScale * 1.5f);
        for (ModuleCategory value : ModuleCategory.values()) {
            if (TetraCustomImGui.navButton(value.getIconDisplay(), this.selected.equals(value), ImGui.getContentRegionAvailX(), 40 * scale, scale)) {
                this.selected = value;
            }
        }
        ImGui.setWindowFontScale(fontScale);

        // credits
        ImGui.setCursorPosY(totalHeight - ImGui.calcTextSizeY("I"));
        ImGui.text(Defaults.getCREDITS());

        ImGui.endChild();

        ImGui.sameLine();

        // Divider
        float dividerX = ImGui.getCursorScreenPosX();
        float topY = ImGui.getCursorScreenPosY();

        ImDrawList drawList = ImGui.getWindowDrawList();

        drawList.addLine(
                dividerX,
                topY,
                dividerX,
                topY + totalHeight,
                ImGui.getColorU32(0.35f, 0.38f, 0.45f, 1.0f),
                1.0f
        );

        ImGui.sameLine();

        // right side
        ImGui.beginChild(
                "##right_panel",
                0,
                totalHeight,
                false
        );

        // right content

        // title
        ImGui.pushFont(ImGuiManager.getInstance().getAnta(), ImGuiManager.FONT_SIZE);
        ImGui.setWindowFontScale(fontScale * 2f);
        ImGui.dummy(5 * scale, 5 * scale);
        ImGui.indent(20 * scale);
        ImGui.text(getSelected().getIconDisplay());
        ImGui.setWindowFontScale(fontScale);
        ImGui.popFont();
        ImGui.dummy(5 * scale, 5 * scale);

        // Modules
        for (AbstractModule module : ModuleManager.getInstance().getModules()) {
            if (!module.getCategory().equals(selected)) continue;
            ImGui.setWindowFontScale(fontScale * 1.5f);
            ImGui.text(module.getName());
            ImGui.setWindowFontScale(fontScale);
            ImGui.separator();
            for (AbstractConfigValue<?> configValue : module.getConfigValues()) {
                // only draw separator if element was rendered
                if (configValue.draw(scale)) ImGui.separator();
            }

            ImGui.dummy(3 * scale, 3 * scale);
        }

        ImGui.endChild();

        ImGui.end();
    }
}

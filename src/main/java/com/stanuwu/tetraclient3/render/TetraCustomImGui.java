package com.stanuwu.tetraclient3.render;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class TetraCustomImGui {

    public void clearMaps() {
        buttonHoverAnim.clear();
        buttonClickAnim.clear();

        colorHoverAnim.clear();
        colorOpenAnim.clear();
        colorBtnAnim.clear();

        dropdownHoverAnim.clear();
        dropdownOpenAnim.clear();
        dropdownBtnAnim.clear();

        textBuffers.clear();
        textHoverAnim.clear();
        textFocusAnim.clear();
        clearBtnAnim.clear();

        floatSliderAnimations.clear();
        floatHoverAnimations.clear();
        floatBadgeAnimations.clear();

        intSliderAnimations.clear();
        intHoverAnimations.clear();
        intBadgeAnimations.clear();

        toggleAnimations.clear();

        navHoverAnimations.clear();
        navSelectAnimations.clear();
    }

    // animation state
    private final Map<Integer, Float> buttonHoverAnim = new HashMap<>();
    private final Map<Integer, Float> buttonClickAnim = new HashMap<>();

    /**
     * Custom button
     *
     * @param label label
     * @param scale ui scale
     * @return clicked
     */
    public boolean button(String label, float scale) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        float width = ImGui.getContentRegionAvailX() - 10f * scale;
        float height = 24f * scale;

        ImVec2 pos = ImGui.getCursorScreenPos();
        int id = ImGui.getID(label);

        String drawnLabel = label.split("##")[0];
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        float x = pos.x;
        float y = pos.y;

        // input area
        ImGui.invisibleButton(label, width, height);

        boolean hovered = ImGui.isItemHovered();
        boolean clicked = ImGui.isItemClicked();

        // animation
        float hoverTarget = hovered ? 1f : 0f;
        float hoverAnim = buttonHoverAnim.getOrDefault(id, hoverTarget);
        hoverAnim += (hoverTarget - hoverAnim) * 0.15f;
        buttonHoverAnim.put(id, hoverAnim);

        float clickTarget = clicked ? 1f : 0f;
        float clickAnim = buttonClickAnim.getOrDefault(id, clickTarget);
        clickAnim += (clickTarget - clickAnim) * 0.25f;
        buttonClickAnim.put(id, clickAnim);

        // colors
        int baseColor = ImGui.getColorU32(1f, 1f, 1f, 0.05f);
        int hoverColor = ImGui.getColorU32(1f, 1f, 1f, 0.10f);
        int activeColor = ImGui.getColorU32(ImGuiCol.CheckMark);

        int bgColor = baseColor;

        if (hovered) bgColor = hoverColor;
        if (clicked) bgColor = activeColor;

        // background
        drawList.addRectFilled(
                x,
                y,
                x + width,
                y + height,
                bgColor,
                8f * scale
        );

        drawList.addRect(
                x,
                y,
                x + width,
                y + height,
                ImGui.getColorU32(1f, 1f, 1f, 0.08f + 0.15f * hoverAnim),
                8f * scale
        );

        // text
        ImVec2 textSize = ImGui.calcTextSize(drawnLabel);

        drawList.addText(
                x + (width - textSize.x) * 0.5f,
                y + (height - textSize.y) * 0.5f,
                textColor,
                drawnLabel
        );

        return clicked;
    }

    // animation state
    private final Map<Integer, Float> colorHoverAnim = new HashMap<>();
    private final Map<Integer, Float> colorOpenAnim = new HashMap<>();
    private final Map<Integer, Float> colorBtnAnim = new HashMap<>();

    /**
     * Compact color picker
     *
     * @param label label
     * @param color rgba color array
     * @param scale ui scale
     */
    public void colorPicker(String label, float[] color, float scale) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        float height = 24f * scale;
        float spacing = 165f * scale;

        ImVec2 pos = ImGui.getCursorScreenPos();
        int id = ImGui.getID(label);

        String drawnLabel = label.split("##")[0];
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        ImVec2 labelSize = ImGui.calcTextSize(drawnLabel);

        float x = pos.x;
        float y = pos.y;

        float labelY = y + (height - labelSize.y) * 0.5f + 1.5f * scale;

        float fieldX = x + spacing;
        float fieldWidth = 395f * scale;

        // field
        ImGui.setCursorScreenPos(new ImVec2(fieldX, y));
        ImGui.invisibleButton("##color_" + id, fieldWidth, height);

        boolean hovered = ImGui.isItemHovered();
        boolean clicked = ImGui.isItemClicked();

        // open popup
        if (clicked && !ImGui.isPopupOpen("##popup_" + id)) {
            ImGui.openPopup("##popup_" + id);
        }

        // animation
        float hoverTarget = hovered ? 1f : 0f;
        float hoverAnim = colorHoverAnim.getOrDefault(id, hoverTarget);
        hoverAnim += (hoverTarget - hoverAnim) * 0.15f;
        colorHoverAnim.put(id, hoverAnim);

        boolean open = ImGui.isPopupOpen("##popup_" + id);

        float openTarget = open ? 1f : 0f;
        float openAnim = colorOpenAnim.getOrDefault(id, openTarget);
        openAnim += (openTarget - openAnim) * 0.2f;
        colorOpenAnim.put(id, openAnim);

        // colors
        int bgColor;

        if (open) {
            // don't show if open because we overlap
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.0f);
        } else if (hovered) {
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.10f);
        } else {
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.05f);
        }

        int borderColor = ImGui.getColorU32(
                1f, 1f, 1f,
                0.08f + (0.15f * openAnim)
        );

        // label
        drawList.addText(x, labelY, textColor, drawnLabel);

        // field background
        drawList.addRectFilled(
                fieldX,
                y,
                fieldX + fieldWidth,
                y + height,
                bgColor,
                height / 2f
        );

        drawList.addRect(
                fieldX,
                y,
                fieldX + fieldWidth,
                y + height,
                borderColor,
                height / 2f
        );

        // color preview
        float previewSize = height - 8f * scale;
        float previewX = fieldX + 6f * scale;
        float previewY = y + (height - previewSize) * 0.5f;

        int previewColor = ImGui.getColorU32(
                color[0],
                color[1],
                color[2],
                color.length > 3 ? color[3] : 1f
        );

        drawList.addRectFilled(
                previewX,
                previewY,
                previewX + previewSize,
                previewY + previewSize,
                previewColor,
                previewSize / 3f
        );

        drawList.addRect(
                previewX,
                previewY,
                previewX + previewSize,
                previewY + previewSize,
                ImGui.getColorU32(1f, 1f, 1f, 0.15f),
                previewSize / 3f
        );

        // rgba text
        String valueText = String.format(
                "#%02X%02X%02X",
                (int) (color[0] * 255f),
                (int) (color[1] * 255f),
                (int) (color[2] * 255f)
        );

        ImVec2 valueSize = ImGui.calcTextSize(valueText);

        drawList.addText(
                previewX + previewSize + 8f * scale,
                y + (height - valueSize.y) * 0.5f + 1.5f * scale,
                textColor,
                valueText
        );

        // picker button
        String pickerText = FontAwesomeIcons.EyeDropper;
        ImVec2 pickerSize = ImGui.calcTextSize(pickerText);

        float btnX = fieldX + fieldWidth + scale;

        ImGui.setCursorScreenPos(new ImVec2(btnX, y + 1.5f * scale));

        //noinspection SuspiciousNameCombination
        ImGui.invisibleButton("##picker_" + id, height, height);

        boolean pickerClicked = ImGui.isItemClicked();
        boolean pickerHovered = ImGui.isItemHovered();

        float pickerTarget = pickerHovered ? 1f : 0f;
        float pickerAnim = colorBtnAnim.getOrDefault(id, pickerTarget);
        pickerAnim += (pickerTarget - pickerAnim) * 0.2f;
        colorBtnAnim.put(id, pickerAnim);

        if (pickerClicked && !ImGui.isPopupOpen("##popup_" + id)) {
            ImGui.openPopup("##popup_" + id);
        }

        int pickerColor = ImGui.getColorU32(
                1f, 1f, 1f,
                0.20f + 0.60f * pickerAnim
        );

        drawList.addText(
                btnX + (height - pickerSize.x) * 0.5f,
                y + (height - pickerSize.y) * 0.5f + 1.5f * scale,
                pickerColor,
                pickerText
        );

        // popup
        ImGui.setNextWindowPos(fieldX, y + height + 2.5f * scale);

        if (ImGui.beginPopup("##popup_" + id)) {
            ImGui.setWindowFontScale(scale / 8f);

            ImGui.pushItemWidth(220f * scale);

            ImGui.colorPicker4(
                    "##picker_internal_" + id,
                    color,

                    ImGuiColorEditFlags.PickerHueWheel
                            | ImGuiColorEditFlags.NoSidePreview
                            | ImGuiColorEditFlags.NoSmallPreview
                            | ImGuiColorEditFlags.AlphaBar
                            | ImGuiColorEditFlags.AlphaPreviewHalf
            );

            ImGui.popItemWidth();

            ImGui.endPopup();
        }
    }

    // animation state
    private final Map<Integer, Float> dropdownHoverAnim = new HashMap<>();
    private final Map<Integer, Float> dropdownOpenAnim = new HashMap<>();
    private final Map<Integer, Float> dropdownBtnAnim = new HashMap<>();

    /**
     * Dropdown based on an enum and animated.
     *
     * @param label   label
     * @param value   value
     * @param options dropdown options
     * @param scale   ui scale
     * @param <T>     enum type
     * @return result
     */
    public <T extends Enum<T>> T enumDropdown(String label, T value, T[] options, float scale) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        float height = 24f * scale;
        float spacing = 165f * scale;

        ImVec2 pos = ImGui.getCursorScreenPos();
        int id = ImGui.getID(label);

        String drawnLabel = label.split("##")[0];
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        ImVec2 labelSize = ImGui.calcTextSize(drawnLabel);

        float x = pos.x;
        float y = pos.y;

        float labelY = y + (height - labelSize.y) * 0.5f + 1.5f * scale;

        float fieldX = x + spacing;
        float fieldWidth = 395f * scale;

        // field
        ImGui.setCursorScreenPos(new ImVec2(fieldX, y));
        ImGui.invisibleButton("##dropdown_" + id, fieldWidth, height);

        boolean hovered = ImGui.isItemHovered();
        boolean clicked = ImGui.isItemClicked();

        // open popup
        if (clicked && !ImGui.isPopupOpen("##popup_" + id)) {
            ImGui.openPopup("##popup_" + id);
        }

        // animation
        float hoverTarget = hovered ? 1f : 0f;
        float hoverAnim = dropdownHoverAnim.getOrDefault(id, hoverTarget);
        hoverAnim += (hoverTarget - hoverAnim) * 0.15f;
        dropdownHoverAnim.put(id, hoverAnim);

        boolean open = ImGui.isPopupOpen("##popup_" + id);

        float openTarget = open ? 1f : 0f;
        float openAnim = dropdownOpenAnim.getOrDefault(id, openTarget);
        openAnim += (openTarget - openAnim) * 0.2f;
        dropdownOpenAnim.put(id, openAnim);

        // colors
        int bgColor;

        if (open) {
            // don't show if open because we overlap
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.0f);
        } else if (hovered) {
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.10f);
        } else {
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.05f);
        }

        int borderColor = ImGui.getColorU32(
                1f, 1f, 1f,
                0.08f + (0.15f * openAnim)
        );

        // label
        drawList.addText(x, labelY, textColor, drawnLabel);

        // field background
        drawList.addRectFilled(
                fieldX,
                y,
                fieldX + fieldWidth,
                y + height,
                bgColor,
                height / 2f
        );

        drawList.addRect(
                fieldX,
                y,
                fieldX + fieldWidth,
                y + height,
                borderColor,
                height / 2f
        );

        // selected value text
        String valueText = value.name();
        ImVec2 valueSize = ImGui.calcTextSize(valueText);

        drawList.addText(
                fieldX + 8f * scale,
                y + (height - valueSize.y) * 0.5f + 1.5f * scale,
                textColor,
                valueText
        );

        // down button
        String arrowText = open ? FontAwesomeIcons.ArrowCircleUp : FontAwesomeIcons.ArrowCircleDown;
        ImVec2 arrowSize = ImGui.calcTextSize(arrowText);

        float btnX = fieldX + fieldWidth + scale;

        ImGui.setCursorScreenPos(new ImVec2(btnX, y + 1.5f * scale));
        // square
        //noinspection SuspiciousNameCombination
        ImGui.invisibleButton("##clear_" + id, height, height);

        boolean arrowClicked = ImGui.isItemClicked();
        boolean arrowHovered = ImGui.isItemHovered();

        float arrowTarget = arrowHovered ? 1f : 0f;
        float arrowAnim = dropdownBtnAnim.getOrDefault(id, arrowTarget);
        arrowAnim += (arrowTarget - arrowAnim) * 0.2f;
        dropdownBtnAnim.put(id, arrowAnim);

        if (arrowClicked && !ImGui.isPopupOpen("##popup_" + id)) {
            ImGui.openPopup("##popup_" + id);
        }

        int arrowColor = ImGui.getColorU32(
                1f, 1f, 1f,
                0.20f + 0.60f * arrowAnim
        );

        drawList.addText(
                btnX + (height - arrowSize.x) * 0.5f,
                y + (height - arrowSize.y) * 0.5f + 1.5f * scale,
                arrowColor,
                arrowText
        );

        // dropdown popup
        ImGui.setNextWindowPos(fieldX, y);
        if (ImGui.beginPopup("##popup_" + id)) {
            ImGui.setWindowFontScale(scale / 8f);
            float popupY = ImGui.getCursorPosY();
            ImGui.dummy(fieldWidth - arrowSize.x - 6f * scale, 0);
            ImGui.setCursorPosY(popupY);
            for (T option : options) {
                boolean selected = option == value;

                if (selected) {
                    ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(ImGuiCol.CheckMark));
                }

                if (ImGui.selectable(option.name(), false)) {
                    value = option;
                }

                if (selected) {
                    ImGui.popStyleColor();
                }
            }

            ImGui.endPopup();
        }

        return value;
    }

    // animation and string state
    private final Map<Integer, ImString> textBuffers = new HashMap<>();
    private final Map<Integer, Float> textHoverAnim = new HashMap<>();
    private final Map<Integer, Float> textFocusAnim = new HashMap<>();
    private final Map<Integer, Float> clearBtnAnim = new HashMap<>();

    /**
     * Text box animated.
     *
     * @param label label
     * @param value value
     * @param scale ui scale
     * @return result
     */
    public String textBox(String label, String value, float scale) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        float height = 24f * scale;
        float spacing = 165f * scale;

        ImVec2 pos = ImGui.getCursorScreenPos();
        int id = ImGui.getID(label);

        String drawnLabel = label.split("##")[0];
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        ImVec2 labelSize = ImGui.calcTextSize(drawnLabel);

        float x = pos.x;
        float y = pos.y;

        float labelY = y + (height - labelSize.y) * 0.5f + 1.5f * scale;

        float fieldX = x + spacing;
        float fieldWidth = 395f * scale;

        // text buffers
        ImString buffer = textBuffers.get(id);
        if (buffer == null) {
            buffer = new ImString(value, 256);
            textBuffers.put(id, buffer);
        }

        if (!ImGui.isItemActive()) {
            buffer.set(value);
        }

        // state for drawing
        boolean hovered = ImGui.isMouseHoveringRect(
                fieldX, y,
                fieldX + fieldWidth, y + height
        );

        boolean active = ImGui.isItemActive();

        // animation
        float hoverTarget = hovered ? 1f : 0f;
        float hoverAnim = textHoverAnim.getOrDefault(id, hoverTarget);
        hoverAnim += (hoverTarget - hoverAnim) * 0.15f;
        textHoverAnim.put(id, hoverAnim);

        float activeTarget = active ? 1f : 0f;
        float activeAnim = textFocusAnim.getOrDefault(id, activeTarget);
        activeAnim += (activeTarget - activeAnim) * 0.18f;
        textFocusAnim.put(id, activeAnim);

        // background
        int bgBase = ImGui.getColorU32(1f, 1f, 1f, 0.06f);
        int bgHover = ImGui.getColorU32(1f, 1f, 1f, 0.10f);
        int bgActive = ImGui.getColorU32(ImGuiCol.CheckMark);

        int bgColor = bgBase;

        if (active) bgColor = bgActive;
        else if (hovered) bgColor = bgHover;

        drawList.addRectFilled(
                fieldX,
                y,
                fieldX + fieldWidth,
                y + height,
                bgColor,
                height / 2f
        );

        drawList.addRect(
                fieldX,
                y,
                fieldX + fieldWidth,
                y + height,
                ImGui.getColorU32(1f, 1f, 1f, 0.08f + 0.15f * activeAnim),
                height / 2f
        );

        // remove background
        ImGui.setCursorScreenPos(new ImVec2(fieldX + 2 * scale, y));
        ImGui.pushItemWidth(fieldWidth - 2 * scale);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5f * scale, 0.5f * scale);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0f);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0f, 0f, 0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0f, 0f, 0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0f, 0f, 0f, 0f);

        ImGui.setCursorPosY(ImGui.getCursorPosY() + scale * 3.5f);
        boolean changed = ImGui.inputText("##" + label, buffer);

        ImGui.popStyleColor(3);
        ImGui.popStyleVar(3);
        ImGui.popItemWidth();

        if (changed) {
            value = buffer.get();
        }

        // label
        drawList.addText(x, labelY, textColor, drawnLabel);

        // clear button
        String clearText = "×";
        ImVec2 clearSize = ImGui.calcTextSize(clearText);

        float btnX = fieldX + fieldWidth + scale;

        ImGui.setCursorScreenPos(new ImVec2(btnX, y));
        // square
        //noinspection SuspiciousNameCombination
        ImGui.invisibleButton("##clear_" + id, height, height);

        boolean clearClicked = ImGui.isItemClicked();
        boolean clearHovered = ImGui.isItemHovered();

        float clearTarget = (clearHovered) && !buffer.get().isEmpty() ? 1f : 0f;
        float clearAnim = clearBtnAnim.getOrDefault(id, clearTarget);
        clearAnim += (clearTarget - clearAnim) * 0.2f;
        clearBtnAnim.put(id, clearAnim);

        if (clearClicked) {
            buffer.set("");
            value = "";
        }

        int clearColor = ImGui.getColorU32(
                1f, 1f, 1f,
                0.20f + 0.60f * clearAnim
        );

        drawList.addText(
                btnX + (height - clearSize.x) * 0.5f,
                y + (height - clearSize.y) * 0.5f,
                clearColor,
                clearText
        );

        return value;
    }

    // animation state
    private final Map<Integer, Float> floatSliderAnimations = new HashMap<>();
    private final Map<Integer, Float> floatHoverAnimations = new HashMap<>();
    private final Map<Integer, Float> floatBadgeAnimations = new HashMap<>();

    /**
     * Animated float slider with scrolling.
     *
     * @param label label
     * @param value value
     * @param min   min value
     * @param max   max value
     * @param scale ui scale
     * @return result
     */
    public float floatSlider(String label, float value, float min, float max, float scale) {
        float width = ImGui.getContentRegionAvailX() - 10 * scale;
        ImDrawList drawList = ImGui.getWindowDrawList();

        float height = 22f * scale;
        float spacing = 120f * scale;

        ImVec2 pos = ImGui.getCursorScreenPos();
        int id = ImGui.getID(label);

        String drawnLabel = label.split("##")[0];

        ImVec2 labelSize = ImGui.calcTextSize(drawnLabel);

        float x = pos.x;
        float y = pos.y;

        float labelY = y + (height - labelSize.y) * 0.5f + 1.5f * scale;

        float sliderX = x + spacing + 75f * scale;
        float sliderWidth = width - (sliderX - x) - 50f * scale;

        float sliderY = y + height * 0.35f;
        float sliderHeight = height * 0.3f;

        // normalize
        float t = (value - min) / (max - min);
        t = Math.clamp(t, 0f, 1f);

        // input tracking
        ImGui.invisibleButton(label, width, height);

        boolean active = ImGui.isItemActive();
        boolean hovered = ImGui.isItemHovered();

        // scroll wheel
        if (hovered && ImGui.getIO().getKeyShift()) {
            boolean precisionMode = ImGui.getIO().getKeyCtrl();
            float wheelScale = (max - min) / 25f;
            float wheel = ImGui.getIO().getMouseWheel();
            value = Math.clamp(value + (precisionMode ? Math.clamp(wheel * -100000f, -0.01f, 0.01f) : wheel * wheelScale * -1f), min, max);
        }

        if (active) {
            float mouseX = ImGui.getIO().getMousePos().x;
            float newT = (mouseX - sliderX) / sliderWidth;
            newT = Math.clamp(newT, 0f, 1f);

            value = min + newT * (max - min);
            t = newT;
        }

        // hover animation
        float hoverTarget = hovered ? 1f : 0f;
        float hoverAnim = floatHoverAnimations.getOrDefault(id, hoverTarget);
        hoverAnim += (hoverTarget - hoverAnim) * 0.15f;
        floatHoverAnimations.put(id, hoverAnim);

        // follow animation
        float followAnim = floatSliderAnimations.getOrDefault(id, t);
        followAnim += (t - followAnim) * 0.25f;
        floatSliderAnimations.put(id, followAnim);

        // badge animation
        float badgeTarget = (hovered || active) ? 1f : 0f;
        float badgeAnim = floatBadgeAnimations.getOrDefault(id, badgeTarget);
        badgeAnim += (badgeTarget - badgeAnim) * 0.25f;
        floatBadgeAnimations.put(id, badgeAnim);

        // colors
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        int bgColor = ImGui.getColorU32(
                1f, 1f, 1f,
                0.05f + (0.05f * hoverAnim)
        );

        int fillColor = ImGui.getColorU32(ImGuiCol.CheckMark);
        int knobColor = ImGui.getColorU32(ImGuiCol.Text);

        // label
        drawList.addText(x, labelY, textColor, drawnLabel);

        // min/max labels
        String minText = String.valueOf(Math.round(min));
        String maxText = String.valueOf(Math.round(max));

        ImVec2 maxSize = ImGui.calcTextSize(maxText);

        int dimText = ImGui.getColorU32(1f, 1f, 1f, 0.35f);

        float minTextWidth = ImGui.calcTextSizeX(minText);
        drawList.addText(
                sliderX - minTextWidth - 10 * scale,
                y + 2.5f * scale,
                dimText,
                minText
        );

        float maxTextWith = ImGui.calcTextSizeX(maxText);
        drawList.addText(
                sliderX + sliderWidth - maxSize.x + maxTextWith + 10f * scale,
                y + 2.5f * scale,
                dimText,
                maxText
        );

        // slider track
        drawList.addRectFilled(
                sliderX,
                sliderY,
                sliderX + sliderWidth,
                sliderY + sliderHeight,
                bgColor,
                sliderHeight / 2f
        );

        drawList.addRectFilled(
                sliderX,
                sliderY,
                sliderX + sliderWidth * followAnim,
                sliderY + sliderHeight,
                fillColor,
                sliderHeight / 2f
        );

        // knob
        float knobRadius = height * 0.35f;
        float knobX = sliderX + sliderWidth * followAnim;
        float knobY = y + height / 2f;

        drawList.addCircleFilled(knobX, knobY, knobRadius, knobColor);

        // truncate to 2 decimals
        value = Math.round(value * 100f) / 100.0f;

        // draw badge
        String valueText = String.format("%.2f", value);
        ImVec2 valueSize = ImGui.calcTextSize(valueText);

        float badgeScale = 0.7f + (0.3f * badgeAnim);

        float badgeWidth = (valueSize.x + 10f * scale) * badgeScale;
        float badgeHeight = (valueSize.y + 6f * scale) * badgeScale;

        float badgeX = knobX - badgeWidth / 2f;
        float badgeY = knobY - knobRadius - 25f * scale - (10f * badgeAnim);

        int badgeBg = ImGui.getColorU32(1f, 1f, 1f, 0.12f * badgeAnim);
        int badgeText = ImGui.getColorU32(1f, 1f, 1f, 0.9f * badgeAnim);

        drawList.addRectFilled(
                badgeX,
                badgeY,
                badgeX + badgeWidth,
                badgeY + badgeHeight,
                badgeBg,
                badgeHeight / 2f
        );

        drawList.addText(
                badgeX + (badgeWidth - valueSize.x) / 2f,
                badgeY + (badgeHeight - valueSize.y) / 2f,
                badgeText,
                valueText
        );

        return value;
    }

    // animation state
    private final Map<Integer, Float> intSliderAnimations = new HashMap<>();
    private final Map<Integer, Float> intHoverAnimations = new HashMap<>();
    private final Map<Integer, Float> intBadgeAnimations = new HashMap<>();

    /**
     * Animated int slider with scrolling.
     *
     * @param label label
     * @param value value
     * @param min   min value
     * @param max   max value
     * @param scale ui scale
     * @return result
     */
    public int intSlider(String label, int value, int min, int max, float scale) {
        float width = ImGui.getContentRegionAvailX() - 10 * scale;
        ImDrawList drawList = ImGui.getWindowDrawList();

        float height = 22f * scale;
        float spacing = 120f * scale;

        ImVec2 pos = ImGui.getCursorScreenPos();
        int id = ImGui.getID(label);

        String drawnLabel = label.split("##")[0];

        ImVec2 labelSize = ImGui.calcTextSize(drawnLabel);

        float x = pos.x;
        float y = pos.y;

        float labelY = y + (height - labelSize.y) * 0.5f + 1.5f * scale;

        float sliderX = x + spacing + 75f * scale;
        float sliderWidth = width - (sliderX - x) - 50f * scale;

        float sliderY = y + height * 0.35f;
        float sliderHeight = height * 0.3f;

        // normalize
        float t = (value - min) / (float) (max - min);
        t = Math.clamp(t, 0f, 1f);

        // input tracking
        ImGui.invisibleButton(label, width, height);

        boolean active = ImGui.isItemActive();
        boolean hovered = ImGui.isItemHovered();

        // scroll wheel
        if (hovered && ImGui.getIO().getKeyShift()) {
            boolean precisionMode = ImGui.getIO().getKeyCtrl();
            float wheelScale = (max - min) / 25f;
            float wheel = ImGui.getIO().getMouseWheel();
            value = Math.clamp(value + (precisionMode ? (int) Math.clamp(wheel * -100000, -1, 1) : Math.round(wheel * wheelScale * -1)), min, max);
        }

        if (active) {
            float mouseX = ImGui.getIO().getMousePos().x;
            float newT = (mouseX - sliderX) / sliderWidth;
            newT = Math.clamp(newT, 0f, 1f);

            value = min + Math.round(newT * (max - min));
            t = newT;
        }

        // hover animation
        float hoverTarget = hovered ? 1f : 0f;
        float hoverAnim = intHoverAnimations.getOrDefault(id, hoverTarget);
        hoverAnim += (hoverTarget - hoverAnim) * 0.15f;
        intHoverAnimations.put(id, hoverAnim);

        // follow animation
        float followAnim = intSliderAnimations.getOrDefault(id, t);
        followAnim += (t - followAnim) * 0.25f;
        intSliderAnimations.put(id, followAnim);

        // badge animation
        float badgeTarget = (hovered || active) ? 1f : 0f;
        float badgeAnim = intBadgeAnimations.getOrDefault(id, badgeTarget);
        badgeAnim += (badgeTarget - badgeAnim) * 0.25f;
        intBadgeAnimations.put(id, badgeAnim);

        // colors
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        int bgColor = ImGui.getColorU32(
                1f, 1f, 1f,
                0.05f + (0.05f * hoverAnim)
        );

        int fillColor = ImGui.getColorU32(ImGuiCol.CheckMark);
        int knobColor = ImGui.getColorU32(ImGuiCol.Text);

        // label
        drawList.addText(x, labelY, textColor, drawnLabel);

        // min/max labels
        String minText = String.valueOf(min);
        String maxText = String.valueOf(max);

        ImVec2 maxSize = ImGui.calcTextSize(maxText);

        int dimText = ImGui.getColorU32(1f, 1f, 1f, 0.35f);

        float minTextWidth = ImGui.calcTextSizeX(minText);
        drawList.addText(
                sliderX - minTextWidth - 10 * scale,
                y + 2.5f * scale,
                dimText,
                minText
        );

        float maxTextWith = ImGui.calcTextSizeX(maxText);
        drawList.addText(
                sliderX + sliderWidth - maxSize.x + maxTextWith + 10f * scale,
                y + 2.5f * scale,
                dimText,
                maxText
        );

        // slider track
        drawList.addRectFilled(
                sliderX,
                sliderY,
                sliderX + sliderWidth,
                sliderY + sliderHeight,
                bgColor,
                sliderHeight / 2f
        );

        drawList.addRectFilled(
                sliderX,
                sliderY,
                sliderX + sliderWidth * followAnim,
                sliderY + sliderHeight,
                fillColor,
                sliderHeight / 2f
        );

        // knob
        float knobRadius = height * 0.35f;
        float knobX = sliderX + sliderWidth * followAnim;
        float knobY = y + height / 2f;

        drawList.addCircleFilled(knobX, knobY, knobRadius, knobColor);

        // draw badge
        String valueText = String.format("%.0f", (float) value);
        ImVec2 valueSize = ImGui.calcTextSize(valueText);

        float badgeScale = 0.7f + (0.3f * badgeAnim);

        float badgeWidth = (valueSize.x + 10f * scale) * badgeScale;
        float badgeHeight = (valueSize.y + 6f * scale) * badgeScale;

        float badgeX = knobX - badgeWidth / 2f;
        float badgeY = knobY - knobRadius - 25f * scale - (10f * badgeAnim);

        int badgeBg = ImGui.getColorU32(1f, 1f, 1f, 0.12f * badgeAnim);
        int badgeText = ImGui.getColorU32(1f, 1f, 1f, 0.9f * badgeAnim);

        drawList.addRectFilled(
                badgeX,
                badgeY,
                badgeX + badgeWidth,
                badgeY + badgeHeight,
                badgeBg,
                badgeHeight / 2f
        );

        drawList.addText(
                badgeX + (badgeWidth - valueSize.x) / 2f,
                badgeY + (badgeHeight - valueSize.y) / 2f,
                badgeText,
                valueText
        );

        return value;
    }

    // store animation states
    private final Map<Integer, Float> toggleAnimations = new HashMap<>();

    public boolean toggleSwitch(String label, boolean value, float scale) {
        float width = ImGui.getContentRegionAvailX() - 10 * scale;
        ImDrawList drawList = ImGui.getWindowDrawList();

        float switchWidth = 42f * scale;
        float switchHeight = 22f * scale;

        ImVec2 pos = ImGui.getCursorScreenPos();

        int id = ImGui.getID(label);

        // calculate switch position
        float switchX = pos.x + width - switchWidth;
        float switchY = pos.y;

        boolean hovered = ImGui.isMouseHoveringRect(
                switchX,
                switchY,
                switchX + switchWidth,
                switchY + switchHeight
        );

        boolean clicked = hovered && ImGui.isMouseClicked(0);

        if (clicked) {
            value = !value;
        }

        // calculate animation
        float anim = toggleAnimations.getOrDefault(id, value ? 1f : 0f);

        float speed = 0.15f;

        anim += ((value ? 1f : 0f) - anim) * speed;

        toggleAnimations.put(id, anim);

        // get colors
        int bgColor;

        if (value) {
            bgColor = ImGui.getColorU32(ImGuiCol.CheckMark);
        } else if (hovered) {
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.10f);
        } else {
            bgColor = ImGui.getColorU32(1f, 1f, 1f, 0.05f);
        }

        int knobColor = ImGui.getColorU32(ImGuiCol.Text);
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        // draw label
        String drawnLabel = label.split("##")[0];
        ImVec2 textSize = ImGui.calcTextSize(drawnLabel);

        drawList.addText(
                pos.x,
                pos.y + (switchHeight - textSize.y) * 0.5f,
                textColor,
                drawnLabel
        );

        // draw toggle track
        drawList.addRectFilled(
                switchX,
                switchY,
                switchX + switchWidth,
                switchY + switchHeight,
                bgColor,
                switchHeight / 2f
        );

        // draw knob
        float padding = 3f * scale;

        float knobRadius = (switchHeight / 2f) - padding;

        float knobX = switchX + padding + knobRadius +
                ((switchWidth - (padding * 2f) - (knobRadius * 2f)) * anim);

        float knobY = switchY + switchHeight / 2f;

        drawList.addCircleFilled(
                knobX,
                knobY,
                knobRadius,
                knobColor
        );

        // invisible input
        ImGui.invisibleButton(
                label,
                width,
                switchHeight
        );

        return value;
    }

    // store animation states
    private final Map<Integer, Float> navHoverAnimations = new HashMap<>();
    private final Map<Integer, Float> navSelectAnimations = new HashMap<>();

    /**
     * Transparent nav button with highlights.
     *
     * @param label    label
     * @param selected is selected
     * @param width    width
     * @param height   height
     * @param scale    ui scale
     * @return was clicked
     */
    public boolean navButton(String label, boolean selected, float width, float height, float scale) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        ImVec2 pos = ImGui.getCursorScreenPos();

        int id = ImGui.getID(label);

        boolean hovered = ImGui.isMouseHoveringRect(
                pos.x,
                pos.y,
                pos.x + width,
                pos.y + height
        );

        boolean clicked = hovered && ImGui.isMouseClicked(0);

        // animations
        float hoverAnim = navHoverAnimations.getOrDefault(id, hovered ? 1f : 0f);
        float selectAnim = navSelectAnimations.getOrDefault(id, selected ? 1f : 0f);

        float speed = 0.50f;

        hoverAnim += ((hovered ? 1f : 0f) - hoverAnim) * speed;
        selectAnim += ((selected ? 1f : 0f) - selectAnim) * speed;

        navHoverAnimations.put(id, hoverAnim);
        navSelectAnimations.put(id, selectAnim);

        // colors
        int highlightColor = ImGui.getColorU32(ImGuiCol.CheckMark);
        int hoverColor = ImGui.getColorU32(ImGuiCol.HeaderHovered);
        int textColor = ImGui.getColorU32(ImGuiCol.Text);

        // transparent background
        float bgAlpha =
                (0.03f * hoverAnim) +
                        (0.06f * selectAnim);

        // render background
        if (bgAlpha > 0.001f) {
            drawList.addRectFilled(
                    pos.x,
                    pos.y,
                    pos.x + width,
                    pos.y + height,
                    ImGui.getColorU32(1f, 1f, 1f, bgAlpha),
                    0f
            );
        }

        // render highlight bar
        float barWidth =
                (2f * hoverAnim * scale) +
                        (3f * selectAnim * scale);

        float barAlpha =
                Math.max(hoverAnim * 0.7f, selectAnim);

        if (barWidth > 0.01f) {
            ImVec4 barColorVec = ImGui.colorConvertU32ToFloat4(
                    selected ? highlightColor : hoverColor
            );

            int barColor = ImGui.getColorU32(
                    barColorVec.x,
                    barColorVec.y,
                    barColorVec.z,
                    barAlpha
            );

            drawList.addRectFilled(
                    pos.x,
                    pos.y,
                    pos.x + barWidth,
                    pos.y + height,
                    barColor,
                    0f
            );
        }

        // render text
        ImVec2 textSize = ImGui.calcTextSize(label);

        float textOffset =
                (2f * hoverAnim + 4f * selectAnim) * scale;

        drawList.addText(
                pos.x + (12 * scale) + textOffset,
                pos.y + (height - textSize.y) * 0.5f,
                textColor,
                label
        );

        // render input area
        ImGui.invisibleButton("##" + label + "nav", width, height);

        return clicked;
    }

    public void applyTetraTheme(float scale) {
        ImGuiStyle style = ImGui.getStyle();

        // rounding
        style.setWindowRounding(14f * scale);
        style.setChildRounding(10f * scale);
        style.setFrameRounding(10f * scale);
        style.setPopupRounding(10f * scale);
        style.setScrollbarRounding(12f * scale);
        style.setGrabRounding(12f * scale);
        style.setTabRounding(10f * scale);

        // borders
        style.setWindowBorderSize(1f);
        style.setChildBorderSize(1f);
        style.setPopupBorderSize(1f);
        style.setFrameBorderSize(0f);
        style.setTabBorderSize(0f);

        // spacing
        style.setWindowPadding(12f * scale, 12f * scale);
        style.setFramePadding(10f * scale, 6f * scale);
        style.setCellPadding(8f * scale, 6f * scale);
        style.setItemSpacing(10f * scale, 8f * scale);
        style.setItemInnerSpacing(6f * scale, 6f * scale);
        style.setIndentSpacing(20f * scale);

        // scroll
        style.setScrollbarSize(10f * scale);

        // window title
        style.setWindowTitleAlign(0.5f * scale, 0.5f * scale);

        // colors
        final ImVec4[] colors = style.getColors();

        colors[ImGuiCol.Text] = new ImVec4(0.95f, 0.96f, 0.98f, 1.00f);
        colors[ImGuiCol.TextDisabled] = new ImVec4(0.50f, 0.50f, 0.55f, 1.00f);

        colors[ImGuiCol.WindowBg] = new ImVec4(0.09f, 0.10f, 0.12f, 0.96f);
        colors[ImGuiCol.ChildBg] = new ImVec4(0.11f, 0.12f, 0.14f, 0.90f);
        colors[ImGuiCol.PopupBg] = new ImVec4(0.08f, 0.08f, 0.10f, 0.98f);

        colors[ImGuiCol.Border] = new ImVec4(0.20f, 0.22f, 0.27f, 0.80f);
        colors[ImGuiCol.BorderShadow] = new ImVec4(0f, 0f, 0f, 0f);

        colors[ImGuiCol.FrameBg] = new ImVec4(0.16f, 0.17f, 0.20f, 1.00f);
        colors[ImGuiCol.FrameBgHovered] = new ImVec4(0.22f, 0.24f, 0.28f, 1.00f);
        colors[ImGuiCol.FrameBgActive] = new ImVec4(0.28f, 0.31f, 0.36f, 1.00f);

        colors[ImGuiCol.TitleBg] = new ImVec4(0.10f, 0.11f, 0.13f, 1.00f);
        colors[ImGuiCol.TitleBgActive] = new ImVec4(0.14f, 0.15f, 0.18f, 1.00f);
        colors[ImGuiCol.TitleBgCollapsed] = new ImVec4(0.08f, 0.08f, 0.09f, 1.00f);

        colors[ImGuiCol.MenuBarBg] = new ImVec4(0.12f, 0.13f, 0.15f, 1.00f);

        colors[ImGuiCol.ScrollbarBg] = new ImVec4(0.10f, 0.11f, 0.13f, 1.00f);
        colors[ImGuiCol.ScrollbarGrab] = new ImVec4(0.24f, 0.26f, 0.30f, 1.00f);
        colors[ImGuiCol.ScrollbarGrabHovered] = new ImVec4(0.30f, 0.33f, 0.38f, 1.00f);
        colors[ImGuiCol.ScrollbarGrabActive] = new ImVec4(0.35f, 0.38f, 0.44f, 1.00f);

        colors[ImGuiCol.CheckMark] = new ImVec4(0.45f, 0.72f, 1.00f, 1.00f);

        colors[ImGuiCol.SliderGrab] = new ImVec4(0.40f, 0.68f, 1.00f, 1.00f);
        colors[ImGuiCol.SliderGrabActive] = new ImVec4(0.55f, 0.78f, 1.00f, 1.00f);

        colors[ImGuiCol.Button] = new ImVec4(0.18f, 0.20f, 0.24f, 1.00f);
        colors[ImGuiCol.ButtonHovered] = new ImVec4(0.25f, 0.28f, 0.34f, 1.00f);
        colors[ImGuiCol.ButtonActive] = new ImVec4(0.30f, 0.34f, 0.40f, 1.00f);

        colors[ImGuiCol.Header] = new ImVec4(0.20f, 0.22f, 0.27f, 1.00f);
        colors[ImGuiCol.HeaderHovered] = new ImVec4(0.28f, 0.31f, 0.38f, 1.00f);
        colors[ImGuiCol.HeaderActive] = new ImVec4(0.32f, 0.36f, 0.43f, 1.00f);

        colors[ImGuiCol.Separator] = new ImVec4(0.25f, 0.27f, 0.32f, 1.00f);
        colors[ImGuiCol.SeparatorHovered] = new ImVec4(0.35f, 0.40f, 0.48f, 1.00f);
        colors[ImGuiCol.SeparatorActive] = new ImVec4(0.40f, 0.45f, 0.55f, 1.00f);

        colors[ImGuiCol.ResizeGrip] = new ImVec4(0.28f, 0.31f, 0.38f, 0.20f);
        colors[ImGuiCol.ResizeGripHovered] = new ImVec4(0.40f, 0.45f, 0.55f, 0.70f);
        colors[ImGuiCol.ResizeGripActive] = new ImVec4(0.50f, 0.56f, 0.68f, 0.95f);

        colors[ImGuiCol.Tab] = new ImVec4(0.14f, 0.15f, 0.18f, 1.00f);
        colors[ImGuiCol.TabHovered] = new ImVec4(0.30f, 0.34f, 0.42f, 1.00f);
        colors[ImGuiCol.TabActive] = new ImVec4(0.22f, 0.25f, 0.31f, 1.00f);
        colors[ImGuiCol.TabUnfocused] = new ImVec4(0.10f, 0.11f, 0.13f, 1.00f);
        colors[ImGuiCol.TabUnfocusedActive] = new ImVec4(0.16f, 0.18f, 0.22f, 1.00f);

        colors[ImGuiCol.DockingPreview] = new ImVec4(0.45f, 0.72f, 1.00f, 0.70f);

        colors[ImGuiCol.PlotLines] = new ImVec4(0.61f, 0.61f, 0.61f, 1.00f);
        colors[ImGuiCol.PlotLinesHovered] = new ImVec4(1.00f, 0.43f, 0.35f, 1.00f);

        colors[ImGuiCol.PlotHistogram] = new ImVec4(0.90f, 0.70f, 0.00f, 1.00f);
        colors[ImGuiCol.PlotHistogramHovered] = new ImVec4(1.00f, 0.60f, 0.00f, 1.00f);

        colors[ImGuiCol.TableHeaderBg] = new ImVec4(0.16f, 0.17f, 0.20f, 1.00f);
        colors[ImGuiCol.TableBorderStrong] = new ImVec4(0.24f, 0.25f, 0.29f, 1.00f);
        colors[ImGuiCol.TableBorderLight] = new ImVec4(0.18f, 0.19f, 0.22f, 1.00f);
        colors[ImGuiCol.TableRowBg] = new ImVec4(0f, 0f, 0f, 0f);
        colors[ImGuiCol.TableRowBgAlt] = new ImVec4(1f, 1f, 1f, 0.03f);

        colors[ImGuiCol.TextSelectedBg] = new ImVec4(0.26f, 0.59f, 0.98f, 0.35f);

        colors[ImGuiCol.DragDropTarget] = new ImVec4(1.00f, 1.00f, 0.00f, 0.90f);

        colors[ImGuiCol.NavHighlight] = new ImVec4(0.45f, 0.72f, 1.00f, 1.00f);
        colors[ImGuiCol.NavWindowingHighlight] = new ImVec4(1.00f, 1.00f, 1.00f, 0.70f);
        colors[ImGuiCol.NavWindowingDimBg] = new ImVec4(0.80f, 0.80f, 0.80f, 0.20f);
        colors[ImGuiCol.ModalWindowDimBg] = new ImVec4(0.20f, 0.20f, 0.20f, 0.35f);
    }
}

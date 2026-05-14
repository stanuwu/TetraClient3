package com.stanuwu.tetraclient3.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stanuwu.tetraclient3.client.Tetraclient3Client;
import com.stanuwu.tetraclient3.events.EventManager;
import com.stanuwu.tetraclient3.events.impl.ImGuiInitEvent;
import imgui.*;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiManager {
    public static final int FONT_SIZE = 128;

    @Getter
    public static final ImGuiManager instance = new ImGuiManager();

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    @Getter
    private ImFont roboto = null;

    @Getter
    private ImFont barcode = null;

    @Getter
    private ImFont anta = null;

    @Getter
    private ImFont cherrybomb = null;

    private final Menu menu = new Menu();

    private long windowHandle = 0;

    private boolean initialized = false;

    public void closeMenu() {
        if (this.menu.isOpened()) this.menu.toggle();
    }

    public void onGlfwInit(long handle) {
        this.windowHandle = handle;
        initializeImGui();
    }

    public void tryRenderThreadInit() {
        if (!this.initialized && this.windowHandle != 0) {
            imGuiGlfw.init(this.windowHandle, false);
            imGuiGl3.init();
            this.initialized = true;
            EventManager.getInstance().fireEvent(new ImGuiInitEvent());
            imGuiGlfw.installCallbacks(this.windowHandle);
        }
    }

    public void onFrameRender() {
        if (RenderSystem.tryGetDevice() == null) return;

        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();

        ImGui.newFrame();

        // can only toggle menu after client init
        if (ImGui.isKeyPressed(ImGuiKey.Insert) && Tetraclient3Client.isInitialized()) this.menu.toggle();
        float width = Minecraft.getInstance().getWindow().getWidth();
        float height = Minecraft.getInstance().getWindow().getHeight();
        float scale = height / 1080f;
        scale = Math.clamp(scale, 0.8f, 1.75f);
        menu.draw(width, height, scale);

        OverlayUtil.doOverlayPass(scale);

        ImGui.render();
        endFrame();
    }

    private void addIcons(ImGuiIO io, ImFontConfig fontConfig, short[] glyphRanges) {
        fontConfig.setMergeMode(true);
        io.getFonts().addFontFromMemoryTTF(loadFromResources("fa-solid-900.ttf"), FONT_SIZE, fontConfig, glyphRanges);
        fontConfig.setMergeMode(false);
    }

    private void initializeImGui() {
        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null); // no ini file

        // flags
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors);
        io.setBackendFlags(ImGuiBackendFlags.HasSetMousePos);

        // fonts
        io.getFonts().setFreeTypeRenderer(true);

        final ImFontGlyphRangesBuilder rangesBuilderRoboto = new ImFontGlyphRangesBuilder();
        rangesBuilderRoboto.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilderRoboto.addRanges(FontAwesomeIcons._IconRange);
        final short[] glyphRangesRoboto = rangesBuilderRoboto.buildRanges();
        final ImFontConfig fontConfigRoboto = new ImFontConfig();
        this.roboto = io.getFonts().addFontFromMemoryTTF(loadFromResources("Roboto-Regular.ttf"), FONT_SIZE, fontConfigRoboto, glyphRangesRoboto);
        addIcons(io, fontConfigRoboto, glyphRangesRoboto);

        this.barcode = io.getFonts().addFontFromMemoryTTF(loadFromResources("LibreBarcode128Text-Regular.ttf"), FONT_SIZE);
        this.cherrybomb = io.getFonts().addFontFromMemoryTTF(loadFromResources("CherryBombOne-Regular.ttf"), FONT_SIZE);

        final ImFontGlyphRangesBuilder rangesBuilderAnta = new ImFontGlyphRangesBuilder();
        rangesBuilderAnta.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilderAnta.addRanges(FontAwesomeIcons._IconRange);
        final short[] glyphRangesAnta = rangesBuilderAnta.buildRanges();
        final ImFontConfig fontConfigAnta = new ImFontConfig();
        this.anta = io.getFonts().addFontFromMemoryTTF(loadFromResources("Anta-Regular.ttf"), FONT_SIZE, fontConfigAnta, glyphRangesAnta);
        addIcons(io, fontConfigAnta, glyphRangesAnta);

        io.getFonts().build();
        fontConfigRoboto.destroy();
        fontConfigAnta.destroy();

        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final ImGuiStyle style = ImGui.getStyle();
            style.setWindowRounding(0.0f);
            style.setColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.WindowBg, 1));
        }
    }

    private static byte[] loadFromResources(String name) {
        try {
            return Files.readAllBytes(Paths.get(Objects.requireNonNull(ImGuiManager.class.getResource("/assets/fonts/" + name)).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void endFrame() {
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }
}

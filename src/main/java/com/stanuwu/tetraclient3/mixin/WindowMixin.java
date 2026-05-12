package com.stanuwu.tetraclient3.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.GpuBackend;
import com.stanuwu.tetraclient3.render.ImGuiManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Shadow
    @Final
    private long handle;

    /**
     * Initialize IMGUI renderer.
     */
    @Inject(at = @At("TAIL"), method = "<init>", remap = false)
    private void postWindowInit(WindowEventHandler eventHandler, DisplayData displayData, String fullscreenVideoModeString, String title, GpuBackend backend, CallbackInfo ci) {
        ImGuiManager.getInstance().onGlfwInit(handle);
    }
}

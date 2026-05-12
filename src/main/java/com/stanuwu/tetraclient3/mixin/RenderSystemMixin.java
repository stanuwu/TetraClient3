package com.stanuwu.tetraclient3.mixin;

import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stanuwu.tetraclient3.render.ImGuiManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    /**
     * Draw IMGUI frames.
     */
    @Inject(at = @At("HEAD"), method = "flipFrame")
    private static void preFlipFrame(TracyFrameCapture tracyFrameCapture, CallbackInfo ci) {
        ImGuiManager.getInstance().tryRenderThreadInit();
        ImGuiManager.getInstance().onFrameRender();
    }
}

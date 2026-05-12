package com.stanuwu.tetraclient3.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.stanuwu.tetraclient3.render.OverlayUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;

    /**
     * Set parameters for overlay rendering.
     */
    @Inject(method = "renderLevel", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=hand"))
    private void onRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci, @Local(name = "projectionMatrix") Matrix4f projectionMatrix, @Local(name = "modelViewMatrix") Matrix4fc modelViewMatrix) {
        Matrix4f viewRotationProjection = new Matrix4f();
        projectionMatrix.mul(modelViewMatrix, viewRotationProjection);

        OverlayUtil.setViewRotationProjectionMatrix(viewRotationProjection);
        OverlayUtil.setCameraPosition(mainCamera.position());
    }
}

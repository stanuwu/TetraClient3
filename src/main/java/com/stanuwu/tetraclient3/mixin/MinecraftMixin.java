package com.stanuwu.tetraclient3.mixin;

import com.stanuwu.tetraclient3.client.Defaults;
import com.stanuwu.tetraclient3.events.EventManager;
import com.stanuwu.tetraclient3.events.impl.PostTickEvent;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    /**
     * Change the window title to our client name.
     *
     * @param cir cir
     */
    @Inject(method = "createTitle", at = @At(value = "TAIL"), cancellable = true)
    private void postCreateTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Defaults.getDisplayName());
    }

    /**
     * Fire pre-tick event.
     *
     * @param ci ci
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void preClientTickEvent(CallbackInfo ci) {
        EventManager.getInstance().fireEvent(new PreTickEvent((Minecraft) (Object) this));
    }

    /**
     * Fire post-tick event.
     *
     * @param ci ci
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void postClientTickEvent(CallbackInfo ci) {
        EventManager.getInstance().fireEvent(new PostTickEvent((Minecraft) (Object) this));
    }
}

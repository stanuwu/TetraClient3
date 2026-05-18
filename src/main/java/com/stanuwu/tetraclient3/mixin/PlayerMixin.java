package com.stanuwu.tetraclient3.mixin;

import com.stanuwu.tetraclient3.module.ModuleManager;
import com.stanuwu.tetraclient3.module.impl.combat.ReachModule;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "blockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void getBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        ReachModule reachModule = ModuleManager.getInstance().getModule(ReachModule.class);
        if (reachModule.blockReach.getValue()) {
            cir.setReturnValue(Double.valueOf(reachModule.blockReachAmount.getValue()));
        }
    }

    @Inject(method = "entityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void getEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        ReachModule reachModule = ModuleManager.getInstance().getModule(ReachModule.class);
        if (reachModule.entityReach.getValue()) {
            cir.setReturnValue(Double.valueOf(reachModule.entityReachAmount.getValue()));
        }
    }
}

package com.stanuwu.tetraclient3.mixin;

import com.stanuwu.tetraclient3.events.EventManager;
import com.stanuwu.tetraclient3.events.impl.GetBlockCollisionsEvent;
import com.stanuwu.tetraclient3.events.impl.context.GetBlockCollisionsContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CollisionGetter.class)
public interface CollisionGetterMixin {
    @Inject(method = "getBlockCollisions", at = @At("RETURN"), cancellable = true)
    default void getBlockCollisions(@Nullable Entity source, AABB box, CallbackInfoReturnable<Iterable<VoxelShape>> cir) {
        if (source instanceof LocalPlayer l) {
            CollisionGetter instance = (CollisionGetter) this;
            GetBlockCollisionsEvent event = new GetBlockCollisionsEvent(new GetBlockCollisionsContext(instance, l, box, cir.getReturnValue()));
            EventManager.getInstance().fireEvent(event);
            cir.setReturnValue(event.getData().getResult());
        }

    }
}

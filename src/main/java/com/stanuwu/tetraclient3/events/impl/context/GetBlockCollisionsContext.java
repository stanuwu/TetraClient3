package com.stanuwu.tetraclient3.events.impl.context;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GetBlockCollisionsContext {
    @Getter
    CollisionGetter collisionGetter;
    @Getter
    LocalPlayer source;
    @Getter
    AABB box;
    @Getter
    @Setter
    Iterable<VoxelShape> result;

    public GetBlockCollisionsContext(CollisionGetter collisionGetter, LocalPlayer source, AABB box, Iterable<VoxelShape> result) {
        this.collisionGetter = collisionGetter;
        this.source = source;
        this.box = box;
        this.result = result;
    }
}

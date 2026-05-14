package com.stanuwu.tetraclient3.module.impl.movement;

import com.stanuwu.tetraclient3.config.ActionRowValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClipModule extends AbstractModule {
    public ClipModule() {
        super(ModuleCategory.MOVEMENT, "Clip");
    }

    private final FloatSliderValue distance = reg(new FloatSliderValue("Distance", 0f, -10f, 10f));
    private final ActionRowValue doClipH = reg(new ActionRowValue("Horizontal", this::doClipH));
    private final ActionRowValue doClipV = reg(new ActionRowValue("Vertical", this::doClipV));
    private final ActionRowValue doClipFacing = reg(new ActionRowValue("Facing", this::doClipFacing));

    /**
     * Clip towards player facing on the x and z axis only.
     */
    private void doClipH() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        float yaw = player.getYRot();
        double rad = Math.toRadians(yaw);
        double dx = -Math.sin(rad) * distance.getValue();
        double dz = Math.cos(rad) * distance.getValue();
        player.setPos(player.position().x + dx, player.position().y, player.position().z + dz);
    }

    /**
     * Clip on the y-axis.
     */
    private void doClipV() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        player.setPos(player.position().x, player.position().y + distance.getValue(), player.position().z);
    }

    /**
     * Clip towards player facing on all axes.
     */
    private void doClipFacing() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        float yaw = player.getYRot();
        float pitch = player.getXRot();
        double distance = this.distance.getValue();
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);
        double dx = -Math.sin(radYaw) * Math.cos(radPitch) * distance;
        double dy = -Math.sin(radPitch) * distance;
        double dz = Math.cos(radYaw) * Math.cos(radPitch) * distance;
        player.setPos(
                player.position().x + dx,
                player.position().y + dy,
                player.position().z + dz
        );
    }
}

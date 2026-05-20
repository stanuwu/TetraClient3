package com.stanuwu.tetraclient3.module.impl.movement;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class SpeedModule extends AbstractModule {
    public SpeedModule() {
        super(ModuleCategory.MOVEMENT, "Speed");
    }

    private enum SpeedModes {
        VELOCITY,
        EFFECT,
        JUMP,
        GRAVITY
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<SpeedModes> mode = reg(new EnumValue<>("Mode", SpeedModes.VELOCITY, SpeedModes.class));
    private final FloatSliderValue speed = reg(new FloatSliderValue("Speed", 2f, 0f, 25f));

    @EventSubscriber(event = PreTickEvent.class)
    private void doPreTick(PreTickEvent event) {
        if (!enabled.getValue()) return;
        LocalPlayer player = event.getData().player;
        if (player == null) return;

        switch (mode.getValue()) {
            case VELOCITY -> {
                if (!player.isSprinting()) return;
                float yaw = player.getYRot();
                double rad = Math.toRadians(yaw);
                double dx = -Math.sin(rad) * speed.getValue() / 25;
                double dz = Math.cos(rad) * speed.getValue() / 25;
                player.setDeltaMovement(new Vec3(player.getDeltaMovement().x + dx, player.getDeltaMovement().y, player.getDeltaMovement().z() + dz));
            }
            case EFFECT -> {
                player.forceAddEffect(new MobEffectInstance(
                        MobEffects.SPEED,
                        20,
                        speed.getValue().intValue(),
                        true,
                        false
                ), player);
            }
            case JUMP -> {
                if (player.isJumping() && player.onGround()) {
                    player.turn(-45, 0);
                    for (int i = 0; i < speed.getValue(); i++) {
                        player.jumpFromGround();
                    }
                    player.setJumping(false);
                    player.turn(45, 0);
                }
            }
            case GRAVITY -> {
                if (player.onGround() && player.isSprinting()) {
                    player.jumpFromGround();
                    player.setDeltaMovement(player.getDeltaMovement().add(0, -1 * speed.getValue(), 0));
                }
            }
        }
    }
}

package com.stanuwu.tetraclient3.module.impl.movement;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;

public class FlightModule extends AbstractModule {
    private enum FlightMode {
        ABILITIES,
        JETPACK,
        VELOCITY,
        POSITION
    }

    public FlightModule() {
        super(ModuleCategory.MOVEMENT, "Flight");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final CheckboxValue antiKick = reg(new CheckboxValue("Anti Kick", true));
    private final EnumValue<FlightMode> mode = reg(new EnumValue<>("Mode", FlightMode.ABILITIES, FlightMode.class));
    private final FloatSliderValue speed = reg(new FloatSliderValue("Speed", 0.1f, 0.1f, 10f));

    private long delta = 0L;
    private boolean shouldDoAntiKick = false;
    private int didAntiKick = 0;
    private boolean lastState = enabled.getValue();
    private FlightMode lastMode = mode.getValue();

    private void doVelocityPositionAntiKick(LocalPlayer player, boolean isFlying) {
        if (shouldDoAntiKick && isFlying) {
            shouldDoAntiKick = false;
            didAntiKick = 0;
            if (player.getDeltaMovement().y() > 0)
                player.setDeltaMovement(player.getDeltaMovement().x, -10 * this.speed.getValue(), player.getDeltaMovement().z);
            player.setPos(player.position().subtract(0f, 0.05f, 0f));
        }

        this.didAntiKick++;

        if (didAntiKick == 2) {
            player.setPos(player.position().add(0f, 0.05f, 0f));
        }
    }

    @EventSubscriber(event = PreTickEvent.class)
    private void doPreTick(PreTickEvent event) {
        // every 1 second do anti kick
        if (System.currentTimeMillis() - 1000 > this.delta) {
            this.shouldDoAntiKick = this.antiKick.getValue();
            this.delta = System.currentTimeMillis();
        }

        switch (this.mode.getValue()) {
            // Abilities Fly
            case ABILITIES -> {
                if (event.getData().player == null) return;
                LocalPlayer player = event.getData().player;
                Abilities abilities = player.getAbilities();
                // enabled
                if (enabled.getValue()) {
                    lastState = true;
                    abilities.mayfly = true;
                    abilities.setFlyingSpeed(speed.getValue());

                    doVelocityPositionAntiKick(player, abilities.flying);
                }
                // disabled
                if (lastState && !enabled.getValue() || lastMode != this.mode.getValue()) {
                    lastState = false;
                    abilities.mayfly = false;
                    abilities.setFlyingSpeed(0.1f);
                }
            }

            // Jetpack Fly
            case JETPACK -> {
                if (enabled.getValue()) {
                    if (event.getData().player == null) return;
                    LocalPlayer player = event.getData().player;
                    if (player.input.keyPresses.jump()) {
                        player.setDeltaMovement(player.getDeltaMovement().x, this.speed.getValue(), player.getDeltaMovement().z);
                        doVelocityPositionAntiKick(player, true);
                    }
                }
            }

            // Velocity Fly
            case VELOCITY -> {
                if (enabled.getValue()) {
                    if (event.getData().player == null) return;
                    LocalPlayer player = event.getData().player;
                    float yVel = 0;
                    if (player.input.keyPresses.jump()) yVel += speed.getValue();
                    if (player.input.keyPresses.shift()) yVel -= speed.getValue();
                    player.setDeltaMovement(player.getDeltaMovement().x, yVel, player.getDeltaMovement().z);

                    doVelocityPositionAntiKick(player, !player.onGround());
                }
            }

            // Position Fly
            case POSITION -> {
                if (enabled.getValue()) {
                    if (event.getData().player == null) return;
                    LocalPlayer player = event.getData().player;
                    float yVel = 0;
                    if (player.input.keyPresses.jump()) yVel += speed.getValue();
                    if (player.input.keyPresses.shift()) yVel -= speed.getValue();
                    player.setPos(player.position().x, player.position().y + yVel, player.position().z);
                    player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);

                    doVelocityPositionAntiKick(player, !player.onGround());
                }
            }
        }

        lastMode = this.mode.getValue();
    }
}

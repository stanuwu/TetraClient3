package com.stanuwu.tetraclient3.module.impl.movement;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PostTickEvent;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.util.PacketUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class FlightModule extends AbstractModule {
    private enum FlightMode {
        ABILITIES,
        JETPACK,
        VELOCITY,
        POSITION,
        ELYTRA,
        ELYTRA_BOOST,
        MOUNT,
        SPEAR
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

            // Elytra Boost
            case ELYTRA_BOOST -> {
                if (event.getData().player == null) return;
                LocalPlayer player = event.getData().player;
                if (player.isFallFlying() && player.input.keyPresses.forward()) {
                    Vec3 look = player.getLookAngle();

                    Vec3 velocity = player.getDeltaMovement();

                    player.setDeltaMovement(
                            velocity.add(
                                    look.x * 0.1 + (look.x * speed.getValue() - velocity.x) * 0.5,
                                    look.y * 0.1 + (look.y * speed.getValue() - velocity.y) * 0.5,
                                    look.z * 0.1 + (look.z * speed.getValue() - velocity.z) * 0.5
                            )
                    );
                }
            }

            // Mount Fly
            case MOUNT -> {
                if (event.getData().player == null) return;
                LocalPlayer player = event.getData().player;
                Entity mount = player.getVehicle();
                if (mount == null) return;
                float yVel = 0;
                if (player.input.keyPresses.jump()) yVel += speed.getValue();
                if (player.input.keyPresses.shift()) yVel -= speed.getValue();
                mount.setDeltaMovement(mount.getDeltaMovement().x, yVel, mount.getDeltaMovement().z);
            }

            // Spear Fly
            case SPEAR -> {
                if (event.getData().player == null) return;
                LocalPlayer player = event.getData().player;
                if (!enabled.getValue() || !player.input.keyPresses.jump()) return;

                Item[] spears = {Items.WOODEN_SPEAR, Items.STONE_SPEAR, Items.COPPER_SPEAR, Items.GOLDEN_SPEAR, Items.IRON_SPEAR, Items.DIAMOND_SPEAR, Items.NETHERITE_SPEAR};

                InteractionHand hand = null;

                if (Arrays.stream(spears).anyMatch(s -> player.getMainHandItem().is(s)))
                    hand = InteractionHand.MAIN_HAND;

                if (hand == null) return;

                int slot = player.getInventory().getSelectedSlot();
                int newSlot = slot == 7 ? 7 : 8;

                for (int i = 0; i < Math.ceil(speed.getValue()); i++) {
                    PacketUtil.sendImmediately(new ServerboundSetCarriedItemPacket(slot));
                    PacketUtil.sendImmediately(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STAB, BlockPos.ZERO, Direction.DOWN));
                    PacketUtil.sendImmediately(new ServerboundSwingPacket(hand));
                    PacketUtil.sendImmediately(new ServerboundSetCarriedItemPacket(newSlot));
                }
            }
        }

        lastMode = this.mode.getValue();
    }

    @EventSubscriber(event = PostTickEvent.class)
    private void doPostTick(PostTickEvent event) {
        switch (this.mode.getValue()) {
            // Elytra Fly
            case ELYTRA -> {
                if (enabled.getValue()) {
                    if (event.getData().player == null) return;
                    LocalPlayer player = event.getData().player;

                    if (player.onGround()) {
                        return;
                    }

                    if (player.isFallFlying()) {
                        PacketUtil.queuePacket(0, new ServerboundPlayerCommandPacket(
                                player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING
                        ));
                    }

                    player.setPose(Pose.STANDING);

                    float yVel = 0;
                    if (player.input.keyPresses.jump()) {
                        yVel += speed.getValue();
                        event.getData().options.keyJump.setDown(false);
                    }
                    if (player.input.keyPresses.shift()) {
                        yVel -= speed.getValue();
                    }
                    player.setDeltaMovement(player.getDeltaMovement().x, yVel, player.getDeltaMovement().z);
                }
            }
        }
    }
}

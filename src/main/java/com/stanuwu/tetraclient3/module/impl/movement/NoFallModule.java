package com.stanuwu.tetraclient3.module.impl.movement;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.events.impl.SendPacketEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFallModule extends AbstractModule {
    private enum NoFallMode {
        PACKET,
        VELOCITY,
    }

    public NoFallModule() {
        super(ModuleCategory.MOVEMENT, "No Fall");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<NoFallMode> mode = reg(new EnumValue<>("Mode", NoFallMode.PACKET, NoFallMode.class));

    @EventSubscriber(event = PreTickEvent.class)
    private void doPreTick(PreTickEvent event) {
        if (!enabled.getValue()) return;
        if (event.getData().player == null) return;
        LocalPlayer player = event.getData().player;

        switch (mode.getValue()) {
            case VELOCITY -> {
                if (player.onGround()) return;
                if (player.getDeltaMovement().y < -0.75f) {
                    player.setDeltaMovement(player.getDeltaMovement().x, 0.01, player.getDeltaMovement().z);
                }

            }
        }
    }

    @EventSubscriber(event = SendPacketEvent.class)
    private void onSendPacket(SendPacketEvent event) {
        if (!enabled.getValue()) return;
        if (!mode.getValue().equals(NoFallMode.PACKET)) return;

        if (event.getData().getPacket() instanceof ServerboundMovePlayerPacket p) {
            ServerboundMovePlayerPacket newPacket;
            if (p.hasPosition() && !p.hasRotation()) {
                newPacket = new ServerboundMovePlayerPacket.Pos(p.getX(0), p.getY(0), p.getZ(0), true, p.horizontalCollision());
            } else if (p.hasRotation() && !p.hasPosition()) {
                newPacket = new ServerboundMovePlayerPacket.Rot(p.getYRot(0), p.getXRot(0), true, p.horizontalCollision());
            } else {
                newPacket = new ServerboundMovePlayerPacket.PosRot(p.getX(0), p.getY(0), p.getZ(0), p.getYRot(0), p.getXRot(0), true, p.horizontalCollision());
            }
            event.getData().setPacket(newPacket);
        }
    }
}

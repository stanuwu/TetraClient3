package com.stanuwu.tetraclient3.module.impl.movement;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.GetBlockCollisionsEvent;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.client.player.LocalPlayer;

public class JesusModule extends AbstractModule {
    private enum JesusMode {
        SOLID,
        JUMP,
        VELOCITY,
    }

    public JesusModule() {
        super(ModuleCategory.MOVEMENT, "Jesus");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<JesusMode> mode = reg(new EnumValue<>("Mode", JesusMode.SOLID, JesusMode.class));

    @EventSubscriber(event = PreTickEvent.class)
    private void doPreTick(PreTickEvent event) {
        if (!enabled.getValue()) return;
        if (event.getData().player == null) return;
        LocalPlayer player = event.getData().player;

        if (player.isInWater() && !player.isSwimming() && !player.input.keyPresses.shift()) {
            switch (mode.getValue()) {
                case JUMP -> {
                    player.setOnGround(true);
                    player.jumpFromGround();
                }
                case VELOCITY -> {
                    player.setDeltaMovement(player.getDeltaMovement().x, 0.06f, player.getDeltaMovement().z);
                }
            }
        }
    }

    @EventSubscriber(event = GetBlockCollisionsEvent.class)
    private void onGetBlockCollisions(GetBlockCollisionsEvent event) {
        if (!enabled.getValue() || !mode.getValue().equals(JesusMode.SOLID)) return;

        event.getData().setResult(event.getData().getCollisionGetter().getBlockAndLiquidCollisions(event.getData().getSource(), event.getData().getBox()));
    }
}

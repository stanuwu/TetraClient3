package com.stanuwu.tetraclient3.module.impl.combat;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.util.PacketUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class ClickerModule extends AbstractModule {
    public ClickerModule() {
        super(ModuleCategory.COMBAT, "Clicker");
    }

    private enum ClickerMode {
        AUTO,
        HOLD
    }

    private enum ClickType {
        PACKET,
        INPUT,
        INTERACT
    }

    public final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<ClickerMode> mode = reg(new EnumValue<>("Mode", ClickerMode.AUTO, ClickerMode.class));
    private final EnumValue<ClickType> type = reg(new EnumValue<>("Click Type", ClickType.PACKET, ClickType.class));
    public final CheckboxValue silent = reg(new CheckboxValue("Silent", false, () -> !type.getValue().equals(ClickType.INPUT)));
    public final CheckboxValue entityCheck = reg(new CheckboxValue("Entity Check", false, () -> type.getValue().equals(ClickType.INPUT)));
    public final CheckboxValue itemCheck = reg(new CheckboxValue("Item Check", false));
    public final CheckboxValue cancelItem = reg(new CheckboxValue("Cancel Item", false));

    @EventSubscriber(event = PreTickEvent.class)
    private void doPreTick(PreTickEvent event) {
        if (!enabled.getValue()) return;
        LocalPlayer player = event.getData().player;
        MultiPlayerGameMode gameMode = event.getData().gameMode;
        if (player == null || gameMode == null) return;

        // no cooldown and auto or held
        if (player.getAttackStrengthScale(0.0f) == 1.0f && (mode.getValue().equals(ClickerMode.AUTO) || (mode.getValue().equals(ClickerMode.HOLD) && event.getData().options.keyAttack.isDown()))) {
            switch (type.getValue()) {
                case PACKET -> {
                    Entity hitEntity = event.getData().crosshairPickEntity;
                    if (cancelItem.getValue()) player.stopUsingItem();
                    if (hitEntity != null && (!itemCheck.getValue() || !player.isUsingItem())) {
                        PacketUtil.sendImmediately(new ServerboundAttackPacket(hitEntity.getId()));
                        if (silent.getValue()) {
                            PacketUtil.sendImmediately(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                        } else {
                            player.swing(InteractionHand.MAIN_HAND);
                        }
                        player.attackStrengthTicker = 0;
                    }

                }
                case INPUT -> {
                    if (!entityCheck.getValue() || event.getData().crosshairPickEntity != null) {
                        if (cancelItem.getValue()) player.stopUsingItem();
                        KeyMapping.click(event.getData().options.keyAttack.key);
                    }

                }
                case INTERACT -> {
                    Entity hitEntity = event.getData().crosshairPickEntity;
                    if (cancelItem.getValue()) player.stopUsingItem();
                    if (hitEntity != null && (!itemCheck.getValue() || !player.isUsingItem())) {
                        event.getData().gameMode.attack(player, hitEntity);
                        if (!silent.getValue()) player.swing(InteractionHand.MAIN_HAND);
                    }
                }
            }
        }
    }
}

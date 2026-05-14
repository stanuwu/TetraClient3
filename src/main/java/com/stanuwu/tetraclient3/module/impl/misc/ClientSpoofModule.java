package com.stanuwu.tetraclient3.module.impl.misc;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.TextValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.SendPacketEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.fabricmc.fabric.impl.networking.CommonRegisterPayload;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;

public class ClientSpoofModule extends AbstractModule {
    private enum ClientSpoofMode {
        LUNAR,
        GEYSER,
        CUSTOM
    }

    public ClientSpoofModule() {
        super(ModuleCategory.MISC, "Client Spoof");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<ClientSpoofMode> mode = reg(new EnumValue<>("Mode", ClientSpoofMode.LUNAR, ClientSpoofMode.class));
    private final TextValue custom = reg(new TextValue("Custom Data", "", () -> mode.getValue().equals(ClientSpoofMode.CUSTOM)));

    private String getBrand() {
        return switch (this.mode.getValue()) {
            case LUNAR -> "Lunar-Client";
            case GEYSER -> "Geyser";
            case CUSTOM -> custom.getValue();
        };
    }

    @EventSubscriber(event = SendPacketEvent.class)
    private void onSendPacket(SendPacketEvent event) {
        if (!enabled.getValue()) return;

        if (event.getData().getPacket() instanceof ServerboundCustomPayloadPacket(
                net.minecraft.network.protocol.common.custom.CustomPacketPayload payload
        )) {
            if (payload instanceof BrandPayload) {
                BrandPayload brandPayload = new BrandPayload(getBrand());
                event.getData().setPacket(new ServerboundCustomPayloadPacket(brandPayload));
            } else //noinspection UnstableApiUsage
                if (payload instanceof CommonRegisterPayload) {
                    event.cancel();
                }
        }
    }
}

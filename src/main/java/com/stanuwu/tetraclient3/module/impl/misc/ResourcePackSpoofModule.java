package com.stanuwu.tetraclient3.module.impl.misc;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.ReceivePacketEvent;
import com.stanuwu.tetraclient3.events.impl.SendPacketEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.util.PacketUtil;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;

public class ResourcePackSpoofModule extends AbstractModule {
    public ResourcePackSpoofModule() {
        super(ModuleCategory.MISC, "Resource Pack Spoof");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));

    @EventSubscriber(event = SendPacketEvent.class)
    private void onSendPacket(SendPacketEvent event) {
        if (!enabled.getValue()) return;

        if (event.getData().getPacket() instanceof ServerboundResourcePackPacket p && (p.action().equals(ServerboundResourcePackPacket.Action.DECLINED) || p.action().equals(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD))) {
            event.cancel();
        }
    }

    @EventSubscriber(event = ReceivePacketEvent.class)
    private void onPacketReceived(ReceivePacketEvent event) {
        if (!enabled.getValue()) return;

        if (event.getData().getPacket() instanceof ClientboundResourcePackPushPacket p) {
            PacketUtil.queuePacket(0, new ServerboundResourcePackPacket(p.id(), ServerboundResourcePackPacket.Action.ACCEPTED));
            PacketUtil.queuePacket(25, new ServerboundResourcePackPacket(p.id(), ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
            event.cancel();
        }
    }
}

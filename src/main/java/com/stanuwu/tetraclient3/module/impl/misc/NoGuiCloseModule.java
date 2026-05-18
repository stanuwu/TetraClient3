package com.stanuwu.tetraclient3.module.impl.misc;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.ReceivePacketEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;

public class NoGuiCloseModule extends AbstractModule {
    public NoGuiCloseModule() {
        super(ModuleCategory.MISC, "No Gui Close");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    
    @EventSubscriber(event = ReceivePacketEvent.class)
    private void onPacketReceived(ReceivePacketEvent event) {
        if (!enabled.getValue()) return;

        if (event.getData().getPacket() instanceof ClientboundContainerClosePacket) event.cancel();
    }
}

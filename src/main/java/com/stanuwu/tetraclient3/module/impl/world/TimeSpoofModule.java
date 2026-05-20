package com.stanuwu.tetraclient3.module.impl.world;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.IntSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.events.impl.ReceivePacketEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;

public class TimeSpoofModule extends AbstractModule {
    public TimeSpoofModule() {
        super(ModuleCategory.WORLD, "Time Spoof");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", true));
    private final IntSliderValue time = reg(new IntSliderValue("Time", 0, 0, 24000));

    @EventSubscriber(event = PreTickEvent.class)
    private void onPreTick(PreTickEvent event) {
        if (!enabled.getValue()) return;
        ClientLevel level = event.getData().level;
        if (level == null) return;

        level.getLevelData().setGameTime(time.getValue());
    }

    @EventSubscriber(event = ReceivePacketEvent.class)
    private void onPacketReceive(ReceivePacketEvent event) {
        if (!enabled.getValue()) return;

        if (event.getData().getPacket() instanceof ClientboundSetTimePacket) {
            event.cancel();
        }
    }
}

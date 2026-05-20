package com.stanuwu.tetraclient3.module.impl.movement;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.config.IntSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.ReceivePacketEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;

public class VelocityModule extends AbstractModule {
    public VelocityModule() {
        super(ModuleCategory.MOVEMENT, "Velocity");
    }

    private enum VelocityMode {
        PACKET,
        DELAY,
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<VelocityMode> mode = reg(new EnumValue<>("Mode", VelocityMode.PACKET, VelocityMode.class));
    private final FloatSliderValue horizontal = reg(new FloatSliderValue("Horizontal", 100f, -100f, 100f, () -> mode.getValue().equals(VelocityMode.PACKET)));
    private final FloatSliderValue vertical = reg(new FloatSliderValue("Vertical", 100f, -100f, 100f, () -> mode.getValue().equals(VelocityMode.PACKET)));
    private final IntSliderValue delay = reg(new IntSliderValue("Delay", 100, 0, 2000, () -> mode.getValue().equals(VelocityMode.DELAY)));

    @EventSubscriber(event = ReceivePacketEvent.class)
    private void onReceivePacket(ReceivePacketEvent event) {
        if (!enabled.getValue()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (event.getData().getPacket() instanceof ClientboundSetEntityMotionPacket p && p.id() == player.getId()) {
            switch (mode.getValue()) {
                case PACKET -> {
                    event.getData().setPacket(new ClientboundSetEntityMotionPacket(player.getId(), new Vec3(p.movement().x * (horizontal.getValue()) / 100, p.movement().y * (vertical.getValue() / 100), p.movement().z * (horizontal.getValue() / 100))));
                }
                case DELAY -> {
                    event.cancel();
                    PacketUtil.queueIncomingPacket(delay.getValue(), p);
                }
            }
        }
    }
}

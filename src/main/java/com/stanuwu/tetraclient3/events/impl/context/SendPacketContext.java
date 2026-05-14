package com.stanuwu.tetraclient3.events.impl.context;

import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

public final class SendPacketContext {
    @Getter
    private final Connection connection;
    @Getter
    @Setter
    private Packet<?> packet;
    @Getter
    private final ChannelFutureListener listener;
    @Getter
    private final boolean flush;

    public SendPacketContext(Connection connection, Packet<?> packet, ChannelFutureListener listener,
                             boolean flush) {
        this.connection = connection;
        this.packet = packet;
        this.listener = listener;
        this.flush = flush;
    }
}

package com.stanuwu.tetraclient3.events.impl.context;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

public class ReceivePacketContext {
    @Getter
    private final Connection connection;
    @Getter
    @Setter
    private Packet<?> packet;
    @Getter
    ChannelHandlerContext channelHandlerContext;
    
    public ReceivePacketContext(Connection connection, Packet<?> packet, ChannelHandlerContext channelHandlerContext) {
        this.connection = connection;
        this.packet = packet;
        this.channelHandlerContext = channelHandlerContext;
    }
}

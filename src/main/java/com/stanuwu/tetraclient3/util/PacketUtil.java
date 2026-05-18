package com.stanuwu.tetraclient3.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

import java.util.ArrayList;

@UtilityClass
public class PacketUtil {
    private record PacketQueueObject(long execute, Packet<?> packet) {
    }

    private final ArrayList<PacketQueueObject> packetQueueObjects = new ArrayList<>();
    private final ArrayList<PacketQueueObject> incomingPacketQueueObjects = new ArrayList<>();

    public void tick(Connection connection) {
        long currentTime = System.currentTimeMillis();

        // outgoing
        packetQueueObjects.removeIf(p -> {
            if (p.execute <= currentTime) {
                connection.sendPacket(p.packet(), null, false);
                return true;
            }
            return false;
        });

        // incoming
        incomingPacketQueueObjects.removeIf(p -> {
            if (p.execute <= currentTime) {
                PacketListener packetListener = connection.packetListener;
                if (packetListener != null) {
                    Connection.genericsFtw(p.packet, packetListener);
                    connection.receivedPackets++;
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Send a packet after a certain delay.
     *
     * @param delayMillis delay in milliseconds
     * @param packet      packet
     */
    public void queuePacket(long delayMillis, Packet<?> packet) {
        long execute = System.currentTimeMillis() + delayMillis;
        packetQueueObjects.add(new PacketQueueObject(execute, packet));
    }

    /**
     * Handle a packet after a certain delay.
     *
     * @param delayMillis delay in milliseconds
     * @param packet      packet
     */
    public void queueIncomingPacket(long delayMillis, Packet<?> packet) {
        long execute = System.currentTimeMillis() + delayMillis;
        incomingPacketQueueObjects.add(new PacketQueueObject(execute, packet));
    }

    /**
     * Sent a packet
     *
     * @param packet packet
     */
    public void sendImmediately(Packet<?> packet) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) connection.send(packet);
    }
}

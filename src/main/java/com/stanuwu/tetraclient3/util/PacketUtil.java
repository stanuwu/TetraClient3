package com.stanuwu.tetraclient3.util;

import lombok.experimental.UtilityClass;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

import java.util.ArrayList;

@UtilityClass
public class PacketUtil {
    private record PacketQueueObject(long execute, Packet<?> packet) {
    }

    private final ArrayList<PacketQueueObject> packetQueueObjects = new ArrayList<>();

    public void tick(Connection connection) {
        long currentTime = System.currentTimeMillis();
        packetQueueObjects.removeIf(p -> {
            if (p.execute <= currentTime) {
                connection.sendPacket(p.packet(), null, false);
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
}

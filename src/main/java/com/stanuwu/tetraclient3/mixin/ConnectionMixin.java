package com.stanuwu.tetraclient3.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.stanuwu.tetraclient3.events.EventManager;
import com.stanuwu.tetraclient3.events.impl.SendPacketEvent;
import com.stanuwu.tetraclient3.events.impl.context.SendPacketContext;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci, @Local(name = "packet", argsOnly = true) LocalRef<Packet<?>> packetLocalRef) {
        SendPacketEvent event = new SendPacketEvent(new SendPacketContext((Connection) (Object) this, packet, listener, flush));
        EventManager.getInstance().fireEvent(event);
        packetLocalRef.set(event.getData().getPacket());
        if (event.isCancelled()) ci.cancel();
    }
}

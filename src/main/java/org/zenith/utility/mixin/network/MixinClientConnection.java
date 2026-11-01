package org.zenith.utility.mixin.network;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.client.events.OutgoingPacketEvent;
import org.zenith.client.events.SendPacketEvent;

@Mixin({ClientConnection.class})
public abstract class MixinClientConnection implements MinecraftAccess {
   @Unique
   private boolean stackOverflowFix;
   @Shadow
   private PacketListener packetListener;

   @Shadow
   public abstract void send(Packet<?> var1);

   @Inject(
      method = {"handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static <T extends PacketListener> void triggerReceivePacketEvent(Packet<T> var0, PacketListener var1, CallbackInfo var2) {
      if (EventManager.hasListeners(PacketEvent.class)) {
         PacketEvent var3 = new PacketEvent(PacketEvent.Type.RECEIVE, var0, var1);
         EventManager.call(var3);
         if (var3.isCancelled()) {
            var2.cancel();
         }
      }
   }

   @Inject(
      method = {"send(Lnet/minecraft/network/packet/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void triggerSendPacketEvent(Packet<?> var1, CallbackInfo var2) {
      if (!this.stackOverflowFix) {
         if (EventManager.hasListeners(OutgoingPacketEvent.class)) {
            OutgoingPacketEvent var3 = new OutgoingPacketEvent(var1);
            EventManager.call(var3);
            if (var3.isCancelled()) {
               var2.cancel();
               return;
            }
         }

         Packet var8 = var1;
         if (EventManager.hasListeners(PacketEvent.class)) {
            PacketEvent var4 = new PacketEvent(PacketEvent.Type.SENT, var1);
            EventManager.call(var4);
            if (var4.isCancelled()) {
               var2.cancel();
               return;
            }

            var8 = var4.getPacket3();
            if (var8 != var1) {
               var2.cancel();

               try {
                  this.stackOverflowFix = true;
                  this.send(var8);
               } finally {
                  this.stackOverflowFix = false;
               }
            }
         }

         if (EventManager.hasListeners(SendPacketEvent.class)) {
            EventManager.call(new SendPacketEvent(var8));
         }
      }
   }
}

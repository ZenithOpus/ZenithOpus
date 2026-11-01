package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.network.packet.Packet;
import org.zenith.CancellableEvent;

public final class PacketReceiveEvent extends CancellableEvent {
   private final Packet<?> packet;

   public PacketReceiveEvent(Packet<?> var1) {
      this.packet = var1;
   }

   @Generated
   public Packet<?> getPacket() {
      return this.packet;
   }
}

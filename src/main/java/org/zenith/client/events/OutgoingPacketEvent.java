package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.network.packet.Packet;
import org.zenith.CancellableEvent;

public class OutgoingPacketEvent extends CancellableEvent {
   private final Packet<?> packet;

   @Generated
   public Packet<?> getPacket4() {
      return this.packet;
   }

   @Generated
   public OutgoingPacketEvent(Packet<?> var1) {
      this.packet = var1;
   }
}

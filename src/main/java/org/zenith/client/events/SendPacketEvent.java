package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.network.packet.Packet;
import org.zenith.CancellableEvent;

public class SendPacketEvent extends CancellableEvent {
   private Packet<?> packet;

   @Generated
   public Packet<?> getPacket2() {
      return this.packet;
   }

   @Generated
   public void setPacket(Packet<?> var1) {
      this.packet = var1;
   }

   @Generated
   public SendPacketEvent(Packet<?> var1) {
      this.packet = var1;
   }
}

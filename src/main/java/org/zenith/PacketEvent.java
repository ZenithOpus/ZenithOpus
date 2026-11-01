package org.zenith;

import lombok.Generated;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends CancellableEvent {
   private final PacketEvent.Type type;
   private Packet<?> packet;
   private PacketListener packetListener;

   public PacketEvent(PacketEvent.Type var1, Packet<?> var2) {
      this(var1, var2, null);
   }

   public boolean method7099() {
      return this.getType3() == PacketEvent.Type.SENT;
   }

   public boolean method7100() {
      return this.getType3() == PacketEvent.Type.RECEIVE;
   }

   @Generated
   public PacketEvent.Type getType3() {
      return this.type;
   }

   @Generated
   public Packet<?> getPacket3() {
      return this.packet;
   }

   @Generated
   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Generated
   public void setPacket2(Packet<?> var1) {
      this.packet = var1;
   }

   @Generated
   public void setPacketListener(PacketListener var1) {
      this.packetListener = var1;
   }

   @Generated
   public PacketEvent(PacketEvent.Type var1, Packet<?> var2, PacketListener var3) {
      this.type = var1;
      this.packet = var2;
      this.packetListener = var3;
   }

   public static enum Type {
      SENT,
      RECEIVE;
   }
}

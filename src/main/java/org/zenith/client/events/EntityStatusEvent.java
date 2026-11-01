package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class EntityStatusEvent implements Event {
   private final EntityStatusS2CPacket entityStatusS2CPacket;

   @Generated
   public EntityStatusEvent(EntityStatusS2CPacket var1) {
      this.entityStatusS2CPacket = var1;
   }

   @Generated
   public EntityStatusS2CPacket getEntityStatusS2CPacket() {
      return this.entityStatusS2CPacket;
   }
}

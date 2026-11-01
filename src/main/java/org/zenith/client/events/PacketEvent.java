package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.network.packet.Packet;
import org.zenith.base.bot.client.BotClient;

public class PacketEvent implements Event {
   private final BotClient botClient;
   private final Packet<?> packet;

   @Generated
   public BotClient getBot() {
      return this.botClient;
   }

   @Generated
   public Packet<?> getPacket() {
      return this.packet;
   }

   @Generated
   public PacketEvent(BotClient var1, Packet<?> var2) {
      this.botClient = var1;
      this.packet = var2;
   }
}

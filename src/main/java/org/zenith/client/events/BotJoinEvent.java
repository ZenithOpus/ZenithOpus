package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;

public class BotJoinEvent implements Event {
   private final BotClient botClient;
   private final BotWorld botWorld;
   private final BotPlayer botPlayer;

   @Generated
   public BotClient getBot() {
      return this.botClient;
   }

   @Generated
   public BotWorld getWorld() {
      return this.botWorld;
   }

   @Generated
   public BotPlayer getPlayer() {
      return this.botPlayer;
   }

   @Generated
   public BotJoinEvent(BotClient var1, BotWorld var2, BotPlayer var3) {
      this.botClient = var1;
      this.botWorld = var2;
      this.botPlayer = var3;
   }
}

package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.text.Text;
import org.zenith.base.bot.client.BotClient;

public class BotChatEvent implements Event {
   private final BotClient botClient;
   private final Text text;

   @Generated
   public BotClient getBot() {
      return this.botClient;
   }

   @Generated
   public Text getText() {
      return this.text;
   }

   @Generated
   public BotChatEvent(BotClient var1, Text var2) {
      this.botClient = var1;
      this.text = var2;
   }
}

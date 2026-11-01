package org.zenith.base.bot.client;

import net.minecraft.text.Text;

public interface BotClientEvents {
   default void onPhaseChanged(BotClient var1, BotPhase var2, BotPhase var3) {
   }

   default void onJoined(BotClient var1) {
   }

   default void onChat(BotClient var1, Text var2) {
   }

   default void onDisconnected(BotClient var1, Text var2) {
   }
}

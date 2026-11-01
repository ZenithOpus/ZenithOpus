package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.GameMode;

public final class BotPlayerListEntry extends PlayerListEntry {
   public BotPlayerListEntry(GameProfile var1, boolean var2) {
      super(var1, var2);
   }

   public void resetBotSession(boolean var1) {
      super.resetSession(var1);
   }

   public void setBotGameMode(GameMode var1) {
      super.setGameMode(var1);
   }

   public void setBotLatency(int var1) {
      super.setLatency(var1);
   }
}

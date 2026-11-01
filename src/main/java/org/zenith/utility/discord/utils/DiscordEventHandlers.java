package org.zenith.utility.discord.utils;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import org.zenith.utility.discord.callbacks.DisconnectedCallback;
import org.zenith.utility.discord.callbacks.ErroredCallback;
import org.zenith.utility.discord.callbacks.JoinGameCallback;
import org.zenith.utility.discord.callbacks.JoinRequestCallback;
import org.zenith.utility.discord.callbacks.ReadyCallback;
import org.zenith.utility.discord.callbacks.SpectateGameCallback;

public class DiscordEventHandlers extends Structure {
   public DisconnectedCallback disconnected;
   public JoinRequestCallback joinRequest;
   public SpectateGameCallback spectateGame;
   public ReadyCallback ready;
   public ErroredCallback errored;
   public JoinGameCallback joinGame;

   public List<String> getFieldOrder() {
      return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
   }

   public static class Builder {
      private final DiscordEventHandlers handlers = new DiscordEventHandlers();

      public DiscordEventHandlers build() {
         return this.handlers;
      }

      public DiscordEventHandlers.Builder disconnected(DisconnectedCallback var1) {
         this.handlers.disconnected = var1;
         return this;
      }

      public DiscordEventHandlers.Builder errored(ErroredCallback var1) {
         this.handlers.errored = var1;
         return this;
      }

      public DiscordEventHandlers.Builder ready(ReadyCallback var1) {
         this.handlers.ready = var1;
         return this;
      }

      public DiscordEventHandlers.Builder joinRequest(JoinRequestCallback var1) {
         this.handlers.joinRequest = var1;
         return this;
      }

      public DiscordEventHandlers.Builder joinGame(JoinGameCallback var1) {
         this.handlers.joinGame = var1;
         return this;
      }

      public DiscordEventHandlers.Builder spectateGame(SpectateGameCallback var1) {
         this.handlers.spectateGame = var1;
         return this;
      }
   }
}

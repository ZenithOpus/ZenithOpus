package org.zenith.base.bot.net;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandSource.SuggestedIdType;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket.Action;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class BotCommandSource implements CommandSource {
   private final BotPlayHandler handler;
   private int completionId = -1;
   @Nullable
   private CompletableFuture<Suggestions> pendingCommandCompletion;
   private final Set<String> chatSuggestions = new HashSet<>();

   public BotCommandSource(BotPlayHandler var1) {
      this.handler = var1;
   }

   @Override
   public Collection<String> getPlayerNames() {
      ArrayList<String> var1 = new ArrayList<>();

      for (PlayerListEntry var3 : this.handler.getPlayerList()) {
         var1.add(var3.getProfile().name());
      }

      return var1;
   }

   @Override
   public synchronized Collection<String> getChatSuggestions() {
      if (this.chatSuggestions.isEmpty()) {
         return this.getPlayerNames();
      } else {
         var var1 = new HashSet<>(this.getPlayerNames());
         var1.addAll(this.chatSuggestions);
         return var1;
      }
   }

   @Override
   public Collection<String> getTeamNames() {
      Scoreboard var1 = this.handler.getScoreboard();
      synchronized (var1) {
         return List.copyOf(var1.getTeamNames());
      }
   }

   @Override
   public Stream<Identifier> getSoundIds() {
      return Registries.SOUND_EVENT.getIds().stream();
   }

   @Override
   public PermissionPredicate getPermissions() {
      return PermissionPredicate.ALL;
   }

   @Override
   public CompletableFuture<Suggestions> listIdSuggestions(
      RegistryKey<? extends Registry<?>> registryRef, SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context
   ) {
      return this.getRegistryManager().getOptional(registryRef).map(var3 -> {
         this.suggestIdentifiers(var3, suggestedIdType, builder);
         return builder.buildFuture();
      }).orElseGet(() -> this.getCompletions(context));
   }

   @Override
   public synchronized CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) {
      if (this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.cancel(false);
      }

      CompletableFuture var2 = new CompletableFuture();
      this.pendingCommandCompletion = var2;
      int var3 = ++this.completionId;
      this.handler.sendPacket(new RequestCommandCompletionsC2SPacket(var3, context.getInput()));
      return var2;
   }

   @Override
   public Set<RegistryKey<World>> getWorldKeys() {
      return this.handler.getWorldKeys();
   }

   @Override
   public DynamicRegistryManager getRegistryManager() {
      return this.handler.getRegistryManager();
   }

   @Override
   public FeatureSet getEnabledFeatures() {
      return this.handler.getEnabledFeatures();
   }

   public synchronized void onCommandSuggestions(int var1, Suggestions var2) {
      if (var1 == this.completionId && this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.complete(var2);
         this.pendingCommandCompletion = null;
         this.completionId = -1;
      }
   }

   public synchronized void onChatSuggestions(Action var1, List<String> var2) {
      switch (var1) {
         case ADD:
            this.chatSuggestions.addAll(var2);
            break;
         case REMOVE:
            var2.forEach(this.chatSuggestions::remove);
            break;
         case SET:
            this.chatSuggestions.clear();
            this.chatSuggestions.addAll(var2);
      }
   }
}

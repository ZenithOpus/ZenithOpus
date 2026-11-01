package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.misc.StreamerModeModule;

public class PlayerArgumentType implements ArgumentType<String>, MinecraftAccess {
   private static final Collection<String> EXAMPLES = List.of("Steve", "Alex", "Bogdan");
   private final boolean displayedNames;

   private PlayerArgumentType(boolean var1) {
      this.displayedNames = var1;
   }

   public static PlayerArgumentType create() {
      return new PlayerArgumentType(false);
   }

   public static PlayerArgumentType createDisplayed() {
      return new PlayerArgumentType(true);
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      return var1.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      var var3 = mc.getNetworkHandler().getPlayerList().stream().map(var0 -> var0.getProfile().name());
      if (this.displayedNames && StreamerModeModule.INSTANCE.method6289() && mc.player != null) {
         String var4 = mc.player.getNameForScoreboard();
         var3 = var3.filter(var1x -> !var1x.equals(var4)).map(StreamerModeModule.INSTANCE::method6291);
      }

      return CommandSource.suggestMatching(var3, var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

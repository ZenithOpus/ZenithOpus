package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.zenith.Zenith;
import org.zenith.base.filemanager.impl.way.Way;

public class WayArgumentType implements ArgumentType<String> {
   private static final List<String> EXAMPLES = List.of("Zamok", "Event", "Home");

   public static WayArgumentType create() {
      return new WayArgumentType();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readString();
      if (!Zenith.getInstance().getWayManager().hasWay(var2)) {
         throw new DynamicCommandExceptionType(var1x -> Text.literal("У тебя нет метки " + var2)).create(var2);
      } else {
         return var2;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(Zenith.getInstance().getWayManager().getItems().stream().map(Way::name), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

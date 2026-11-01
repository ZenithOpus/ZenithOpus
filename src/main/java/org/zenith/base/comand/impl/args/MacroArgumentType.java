package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.zenith.utility.misc.KeyName;

public class MacroArgumentType implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = List.of("b");

   public static MacroArgumentType create() {
      return new MacroArgumentType();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readString();
      if (KeyName.method6704(var2) == -1) {
         throw new DynamicCommandExceptionType(var1x -> Text.literal("Нет такой клавиши " + var2)).create(var2);
      } else {
         return var2;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(Arrays.stream(KeyName.values()).map(var0 -> var0.text), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

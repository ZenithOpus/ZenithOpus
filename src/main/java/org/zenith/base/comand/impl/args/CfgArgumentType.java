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
import net.minecraft.command.CommandSource;
import org.zenith.Zenith;

public class CfgArgumentType implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = List.of("current_config");

   public static CfgArgumentType create() {
      return new CfgArgumentType();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      return var1.readString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(Zenith.getInstance().getManager().method7785(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

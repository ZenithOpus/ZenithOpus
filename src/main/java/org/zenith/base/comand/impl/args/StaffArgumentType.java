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

public class StaffArgumentType implements ArgumentType<String> {
   private static final List<String> EXAMPLES = Zenith.getInstance().getStaffName().getItems().stream().limit(5L).toList();

   public static StaffArgumentType create() {
      return new StaffArgumentType();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readString();
      if (!Zenith.getInstance().getStaffName().method1668(var2)) {
         throw new DynamicCommandExceptionType(var1x -> Text.literal("Нету в стафе " + var2)).create(var2);
      } else {
         return var2;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(Zenith.getInstance().getStaffName().getItems(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.zenith.Zenith;
import org.zenith.base.Module;

public class ModuleArgumentType implements ArgumentType<Module> {
   private static final Collection<String> EXAMPLES = Zenith.getInstance().getModuleManager().getItems().stream().map(Module::getName).limit(5L).toList();

   public static ModuleArgumentType create() {
      return new ModuleArgumentType();
   }

   public Module parse(StringReader var1) throws CommandSyntaxException {
      Module var2 = Zenith.getInstance().getModuleManager().method2484(var1.readString());
      if (var2 == null) {
         throw new DynamicCommandExceptionType(var0 -> Text.literal(var0.toString() + " не существует.")).create(var1.readString());
      } else {
         return var2;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(Zenith.getInstance().getModuleManager().getItems().stream().map(Module::getName), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

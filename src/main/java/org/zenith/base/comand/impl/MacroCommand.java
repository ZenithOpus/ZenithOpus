package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.api.MacroManager;
import org.zenith.utility.misc.KeyName;

public class MacroCommand extends CommandAbstract {
   private final SuggestionProvider<CommandSource> SUGGEST_KEYS = (var0, var1) -> {
      for (KeyName var5 : KeyName.values()) {
         if (!var5.text.isEmpty()) {
            var1.suggest(var5.text);
         }
      }

      return var1.buildFuture();
   };

   public MacroCommand() {
      super("macro");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("add")
            .then(
               arg("name", StringArgumentType.word())
                  .then(
                     arg("key", StringArgumentType.word())
                        .suggests(this.SUGGEST_KEYS)
                        .then(arg("command", StringArgumentType.greedyString()).executes(var1x -> {
                           String var2 = (String)var1x.getArgument("name", String.class);
                           String var3 = (String)var1x.getArgument("key", String.class);
                           String var4 = (String)var1x.getArgument("command", String.class);
                           this.handleAddMacro(var2, var3, var4);
                           return 1;
                        }))
                  )
            )
      );
      var1.then(literal("remove").then(arg("name", StringArgumentType.word()).executes(var0 -> {
         String var1x = (String)var0.getArgument("name", String.class);
         MacroManager.Entry var2 = Zenith.getInstance().getMacroManager().method6986(var1x);
         if (var2 == null) {
            ChatLogger.method2021("Макрос не найден!");
            return 1;
         } else {
            Zenith.getInstance().getMacroManager().method6985(var1x);
            ChatLogger.method2019(var1x + " удален");
            return 1;
         }
      })));
      var1.then(literal("clear").executes(var0 -> {
         Zenith.getInstance().getMacroManager().clear();
         ChatLogger.method2019("Все макросы удалены");
         return 1;
      }));
      var1.then(literal("list").executes(var0 -> {
         if (!Zenith.getInstance().getMacroManager().getItems().isEmpty()) {
            for (MacroManager.Entry var2 : Zenith.getInstance().getMacroManager().getItems()) {
               mc.player.sendMessage(var2.toText(), false);
            }
         } else {
            ChatLogger.method2019("Список макросов пуст!");
         }

         return 1;
      }));
   }

   private void handleAddMacro(String var1, String var2, String var3) {
      MacroManager var4 = Zenith.getInstance().getMacroManager();
      int var5 = KeyName.method6704(var2);
      if (var5 == KeyName.KEY_NONE.intValue) {
         ChatLogger.method2021("Неизвестная клавиша: " + var2);
      } else {
         MacroManager.Entry var6 = var4.method6986(var1);
         if (var6 != null) {
            ChatLogger.method2021("Макрос с таким именем уже существует!");
            ChatLogger.method2019("Наведи мышку на сообщение ниже чтобы удалить его");
            mc.player.sendMessage(var6.toText(), false);
         } else {
            MacroManager.Entry var7 = new MacroManager.Entry(var1, var5, var3);
            var4.method6983(var7);
            mc.player.sendMessage(var7.toText(), false);
            ChatLogger.method2019("Макрос добавлен");
         }
      }
   }
}

package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.modules.impl.player.AutoWardenModule;

public class WardenCommand extends CommandAbstract {
   private static final int MAX_ANARCHIES = 10;

   public WardenCommand() {
      super("warden");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(((LiteralArgumentBuilder)literal("add").executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .warden add <анархия>");
         return 1;
      })).then(arg("анархия", IntegerArgumentType.integer(1, 999)).executes(var0 -> {
         int var1x = (Integer)var0.getArgument("анархия", Integer.class);
         var var2 = AutoWardenModule.INSTANCE.method3950();
         if (var2.contains(var1x)) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Анархия " + var1x + " уже в списке");
            return 1;
         } else if (var2.size() >= 10) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Можно добавить максимум 10 анархий");
            return 1;
         } else {
            var2.add(var1x);
            AutoWardenModule.INSTANCE.method3951();
            ChatLogger.method2020(ChatLogger.Type.INFO, "Анархия " + var1x + " добавлена");
            return 1;
         }
      })));
      var1.then(((LiteralArgumentBuilder)literal("remove").executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .warden remove <анархия>");
         return 1;
      })).then(arg("анархия", IntegerArgumentType.integer(1, 999)).executes(var0 -> {
         int var1x = (Integer)var0.getArgument("анархия", Integer.class);
         var var2 = AutoWardenModule.INSTANCE.method3950();
         if (!var2.remove(Integer.valueOf(var1x))) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Анархия " + var1x + " не найдена");
            return 1;
         } else {
            AutoWardenModule.INSTANCE.method3951();
            ChatLogger.method2020(ChatLogger.Type.INFO, "Анархия " + var1x + " удалена");
            return 1;
         }
      })));
      var1.then(literal("list").executes(var0 -> {
         var var1x = AutoWardenModule.INSTANCE.method3950();
         if (var1x.isEmpty()) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Список анархий пуст");
         } else {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Анархии (" + var1x.size() + "): " + var1x + " | сдача лута: " + var1x.getFirst());
         }

         return 1;
      }));
      var1.then(literal("clear").executes(var0 -> {
         AutoWardenModule.INSTANCE.method3950().clear();
         AutoWardenModule.INSTANCE.method3951();
         ChatLogger.method2020(ChatLogger.Type.INFO, "Список анархий очищен");
         return 1;
      }));
      var1.executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .warden <add|remove|list|clear>");
         return 1;
      });
   }
}

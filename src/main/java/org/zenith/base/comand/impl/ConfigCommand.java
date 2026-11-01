package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.IOException;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OperatingSystem;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.CfgArgumentType;
import org.zenith.utility.api.Manager;

public class ConfigCommand extends CommandAbstract {
   public ConfigCommand() {
      super("cfg");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(((LiteralArgumentBuilder)literal("save").executes(var0 -> {
         boolean var1x = Zenith.getInstance().getManager().method7779("current_config");
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация сохранена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при сохранении конфигурации");
         }

         return 1;
      })).then(arg("name", CfgArgumentType.create()).executes(var0 -> {
         boolean var1x = Zenith.getInstance().getManager().method7779((String)var0.getArgument("name", String.class));
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация сохранена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при сохранении конфигурации");
         }

         return 1;
      })));
      var1.then(literal("list").executes(var0 -> {
         var var1x = Zenith.getInstance().getManager().method7785();
         if (var1x.isEmpty()) {
            ChatLogger.method2019("Список конфигов пуст!");
         } else {
            var1x.forEach(ChatLogger::method2019);
         }

         return 1;
      }));
      var1.then(((LiteralArgumentBuilder)literal("load").executes(var0 -> {
         boolean var1x = Zenith.getInstance().getManager().method7766("current_config");
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация загружена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при загрузке конфигурации");
         }

         return 1;
      })).then(arg("name", CfgArgumentType.create()).executes(var0 -> {
         boolean var1x = Zenith.getInstance().getManager().method7766((String)var0.getArgument("name", String.class));
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация загружена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при загрузке конфигурации");
         }

         return 1;
      })));
      var1.then(literal("help").executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .config <list/save/load/help>");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Команды:");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".config save - сохранить конфигурацию");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".config load - загрузить конфигурацию");
         return 1;
      }));
      var1.then(literal("dir").executes(var0 -> {
         try {
            if (Util.getOperatingSystem() == OperatingSystem.WINDOWS) {
               new ProcessBuilder("explorer", Manager.file.getAbsolutePath()).start();
            } else {
               Util.getOperatingSystem().open(Manager.file);
            }
         } catch (IOException var2) {
         }

         return 1;
      }));
   }
}

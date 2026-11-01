package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotClientConfig;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.comand.api.CommandAbstract;

public class HBotCommand extends CommandAbstract {
   public HBotCommand() {
      super("hbot");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("connect")
            .then(
               ((RequiredArgumentBuilder)arg("name", StringArgumentType.word())
                     .executes(var0 -> connect(StringArgumentType.getString(var0, "name"), null, null)))
                  .then(
                     ((RequiredArgumentBuilder)arg("address", StringArgumentType.word())
                           .executes(var0 -> connect(StringArgumentType.getString(var0, "name"), StringArgumentType.getString(var0, "address"), null)))
                        .then(
                           arg("proxy", StringArgumentType.greedyString())
                              .executes(
                                 var0 -> connect(
                                    StringArgumentType.getString(var0, "name"),
                                    StringArgumentType.getString(var0, "address"),
                                    StringArgumentType.getString(var0, "proxy")
                                 )
                              )
                        )
                  )
            )
      );
      var1.then(literal("disconnect").then(arg("name", StringArgumentType.word()).executes(var0 -> {
         String var1x = StringArgumentType.getString(var0, "name");
         if (var1x.equalsIgnoreCase("all")) {
            HeadlessBots.disconnectAll();
            ChatLogger.method2019("Все headless-боты отключены");
         } else if (HeadlessBots.disconnect(var1x)) {
            ChatLogger.method2019("Бот " + var1x + " отключён");
         } else {
            ChatLogger.method2022("Бот " + var1x + " не найден");
         }

         return 1;
      })));
      var1.then(literal("list").executes(var0 -> {
         if (HeadlessBots.all().isEmpty()) {
            ChatLogger.method2019("Нет активных headless-ботов");
            return 1;
         } else {
            for (BotClient var2 : HeadlessBots.all()) {
               ChatLogger.method2019(var2.getName() + " — " + var2.getPhase());
            }

            return 1;
         }
      }));
      var1.then(literal("say").then(arg("name", StringArgumentType.word()).then(arg("message", StringArgumentType.greedyString()).executes(var0 -> {
         BotClient var1x = requireBot(StringArgumentType.getString(var0, "name"));
         if (var1x == null) {
            return 1;
         } else {
            String var2 = StringArgumentType.getString(var0, "message");
            if (var1x.sendChat(var2)) {
               ChatLogger.method2019("Бот " + var1x.getName() + " отправил: " + var2);
            } else {
               ChatLogger.method2022("Бот " + var1x.getName() + " ещё не в игре");
            }

            return 1;
         }
      }))));
      var1.then(
         literal("module")
            .then(
               arg("name", StringArgumentType.word())
                  .then(arg("module", StringArgumentType.word()).then(arg("state", StringArgumentType.word()).executes(var0 -> {
                     BotClient var1x = requireBot(StringArgumentType.getString(var0, "name"));
                     if (var1x == null) {
                        return 1;
                     } else {
                        String var2 = StringArgumentType.getString(var0, "module");
                        boolean var3 = StringArgumentType.getString(var0, "state").equalsIgnoreCase("on");
                        var1x.execute(() -> {
                           boolean var3x = var1x.getModules().setEnabled(var2, var3);
                           report(
                              var3x ? "Бот " + var1x.getName() + ": модуль " + var2 + (var3 ? " включен" : " выключен") : "Модуль " + var2 + " не найден",
                              var3x
                           );
                        });
                        return 1;
                     }
                  })))
            )
      );
      var1.then(literal("modules").then(arg("name", StringArgumentType.word()).executes(var0 -> {
         BotClient var1x = requireBot(StringArgumentType.getString(var0, "name"));
         if (var1x == null) {
            return 1;
         } else {
            for (BotModule var3 : var1x.getModules().getModules()) {
               ChatLogger.method2019(var3.getName() + " — " + (var3.isEnabled() ? "вкл" : "выкл"));
            }

            return 1;
         }
      })));
   }

   private static int connect(String var0, String var1, String var2) {
      String var3 = var1;
      if (var1 == null) {
         ServerInfo var4 = MinecraftClient.getInstance().getCurrentServerEntry();
         if (var4 == null) {
            ChatLogger.method2022("Не подключён к серверу — укажи адрес: .hbot connect <ник> <host:port>");
            return 1;
         }

         var3 = var4.address;
      }

      ServerAddress var6 = ServerAddress.parse(var3);
      BotClient var5 = HeadlessBots.connect(BotClientConfig.offline(var0, var6.getAddress(), var6.getPort(), var2, HeadlessBots.getProtocolVersion(var0)));
      ChatLogger.method2019("Бот " + var5.getName() + " подключается к " + var3);
      return 1;
   }

   private static BotClient requireBot(String var0) {
      BotClient var1 = HeadlessBots.get(var0);
      if (var1 == null) {
         ChatLogger.method2022("Бот " + var0 + " не найден");
      }

      return var1;
   }

   private static void report(String var0, boolean var1) {
      MinecraftClient.getInstance().execute(() -> {
         if (var1) {
            ChatLogger.method2019(var0);
         } else {
            ChatLogger.method2022(var0);
         }
      });
   }
}

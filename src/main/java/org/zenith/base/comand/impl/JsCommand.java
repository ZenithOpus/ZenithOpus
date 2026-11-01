package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Util;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.ScriptTestApi;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.misc.ScriptHeaderParser;

public class JsCommand extends CommandAbstract {
   private static final SuggestionProvider<CommandSource> SCRIPTS = (var0, var1) -> CommandSource.suggestMatching(
      host().method1293().stream().map(Script::getId), var1
   );

   public JsCommand() {
      super("js");
   }

   private static Storage host() {
      return Zenith.getInstance().getStorage();
   }

   private static void info(String var0) {
      ChatLogger.method2020(ChatLogger.Type.INFO, var0);
   }

   private static void error(String var0) {
      ChatLogger.method2020(ChatLogger.Type.ERROR, var0);
   }

   private static Script find(String var0) {
      Script var1 = host().method1294(var0);
      if (var1 == null) {
         error("Скрипт «" + var0 + "» не найден. Список: .js list");
      }

      return var1;
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         info(".js list — скрипты · .js on/off <имя> · .js reload [имя] · .js new <имя> [" + String.join("|", ScriptHeaderParser.items) + "]");
         info(".js eval [имя] <код> · .js log [имя] · .js test <имя> · .js clear · .js folder · .js api");
         return 1;
      });
      var1.then(
         literal("list")
            .executes(
               var0 -> {
                  if (host().method1293().isEmpty()) {
                     info("Скриптов нет. Создай: .js new hello hud — папка: " + host().getPath());
                     return 1;
                  } else {
                     for (Script var2 : host().method1293()) {
                        String var3 = switch (var2.getType2()) {
                           case RUNNING -> "§aработает";
                           case FAILED -> "§cошибка";
                           default -> host().method1297(var2) ? "§eожидает" : "§7выключен";
                        };
                        String var4 = var2.getItems3().isEmpty() ? "" : " §7· модулей: " + var2.getItems3().size();
                        info(
                           "§f"
                              + var2.getId()
                              + (var2.getVersion().isBlank() ? "" : " §7v" + var2.getVersion())
                              + " §7— "
                              + var3
                              + var4
                              + (var2.getError().isBlank() ? "" : " §c" + trim(var2.getError()))
                        );
                     }

                     return 1;
                  }
               }
            )
      );
      var1.then(literal("on").then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(var0 -> {
         Script var1x = find((String)var0.getArgument("name", String.class));
         if (var1x != null) {
            host().method1298(var1x, true);
            info(var1x.isRunning() ? "Скрипт " + var1x.method1403() + " запущен" : "Не запустился: " + trim(var1x.getError()));
         }

         return 1;
      })));
      var1.then(literal("off").then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(var0 -> {
         Script var1x = find((String)var0.getArgument("name", String.class));
         if (var1x != null) {
            host().method1298(var1x, false);
            info("Скрипт " + var1x.method1403() + " выключен");
         }

         return 1;
      })));
      var1.then(((LiteralArgumentBuilder)literal("reload").executes(var0 -> {
         host().method1300();
         info("Скрипты перезагружены");
         return 1;
      })).then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(var0 -> {
         Script var1x = find((String)var0.getArgument("name", String.class));
         if (var1x != null) {
            host().method1299(var1x);
            info("Перезагружен: " + var1x.method1403());
         }

         return 1;
      })));
      var1.then(
         literal("new")
            .then(
               ((RequiredArgumentBuilder)arg("name", StringArgumentType.word())
                     .executes(var0 -> create((String)var0.getArgument("name", String.class), "empty")))
                  .then(
                     arg("template", StringArgumentType.word())
                        .suggests((var0, var1x) -> CommandSource.suggestMatching(ScriptHeaderParser.items, var1x))
                        .executes(var0 -> create((String)var0.getArgument("name", String.class), (String)var0.getArgument("template", String.class)))
                  )
            )
      );
      var1.then(literal("eval").then(arg("code", StringArgumentType.greedyString()).executes(var0 -> {
         String var1x = (String)var0.getArgument("code", String.class);
         Script var2 = null;
         int var3 = var1x.indexOf(32);
         if (var3 > 0) {
            Script var4 = host().method1294(var1x.substring(0, var3));
            if (var4 != null && var4.isRunning()) {
               var2 = var4;
               var1x = var1x.substring(var3 + 1);
            }
         }

         try {
            info("§7> §f" + trim(String.valueOf(host().method1341(var2, var1x))));
         } catch (Throwable var5) {
            error(trim(Storage.method1325(var5)));
         }

         return 1;
      })));
      var1.then(
         ((LiteralArgumentBuilder)literal("log").executes(var0 -> printLog(null)))
            .then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(var0 -> printLog((String)var0.getArgument("name", String.class))))
      );
      var1.then(literal("test").then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(var0 -> {
         Script var1x = find((String)var0.getArgument("name", String.class));
         if (var1x != null) {
            if (!var1x.isRunning()) {
               error("Скрипт " + var1x.method1403() + " не запущен");
            } else {
               info(ScriptTestApi.method5413(host(), var1x));
            }
         }

         return 1;
      })));
      var1.then(literal("clear").executes(var0 -> {
         host().method1296();
         info("Консоль скриптов очищена");
         return 1;
      }));
      var1.then(literal("folder").executes(var0 -> {
         Util.getOperatingSystem().open(host().getPath().toFile());
         return 1;
      }));
      var1.then(literal("api").executes(var0 -> {
         Util.getOperatingSystem().open(host().getPath().resolve("API.md").toFile());
         return 1;
      }));
   }

   private static int create(String var0, String var1) {
      try {
         Script var2 = host().method1340(var0, var1);
         host().method1298(var2, true);
         info("Создан " + var2.getPath2() + (var2.isRunning() ? " и запущен" : ""));
      } catch (Exception var3) {
         error("Не удалось создать: " + var3.getMessage());
      }

      return 1;
   }

   private static int printLog(String var0) {
      var var1 = host().method1295();
      Script var2 = var0 == null ? null : host().method1294(var0);
      int var3 = 0;

      for (int var4 = var1.size() - 1; var4 >= 0 && var3 < 12; var4--) {
         Storage.Data2 var5 = (Storage.Data2)var1.get(var4);
         if (var2 == null || var5.getText().equals(var2.method1403())) {
            var3++;
            String var7 = var5.getText2();

            String var6 = switch (var7) {
               case "error" -> "§c";
               case "warn" -> "§e";
               default -> "§7";
            };
            info(var6 + "[" + var5.getText() + "] §f" + trim(var5.text()));
         }
      }

      if (var3 == 0) {
         info("Консоль пуста");
      }

      return 1;
   }

   private static String trim(String var0) {
      return var0.length() > 220 ? var0.substring(0, 220) + "…" : var0;
   }
}

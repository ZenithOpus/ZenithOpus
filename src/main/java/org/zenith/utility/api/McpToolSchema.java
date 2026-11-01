package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.zenith.Zenith;
import org.zenith.client.scripting.Module1;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;

public final class McpToolSchema {
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private McpToolSchema() {
   }

   private static void method5498(JsonArray var0, String var1, String var2, String... var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("name", var1);
      var4.addProperty("description", var2);
      JsonObject var5 = new JsonObject();
      var5.addProperty("type", "object");
      JsonObject var6 = new JsonObject();
      JsonArray var7 = new JsonArray();

      for (String var11 : var3) {
         boolean var12 = var11.endsWith("?");
         String[] var13 = (var12 ? var11.substring(0, var11.length() - 1) : var11).split(":");
         JsonObject var14 = new JsonObject();
         var14.addProperty("type", var13[1]);
         var14.addProperty("description", var13.length > 2 ? var13[2] : var13[0]);
         var6.add(var13[0], var14);
         if (!var12) {
            var7.add(var13[0]);
         }
      }

      var5.add("properties", var6);
      var5.add("required", var7);
      var4.add("inputSchema", var5);
      var0.add(var4);
   }

   public static JsonArray method5499() {
      JsonArray var0 = new JsonArray();
      method5498(var0, "get_api_docs", "Полная справка по JS API скриптов Zenith. Начни с неё.");
      method5498(var0, "list_scripts", "Все скрипты: состояние, включён ли, ошибка, модули, файлы.");
      method5498(var0, "read_file", "Прочитать файл скрипта.", "script:string:id скрипта", "path:string:путь внутри папки скрипта, например main.js");
      method5498(
         var0,
         "write_file",
         "Записать файл скрипта. Скрипт сразу перезагружается, в ответе — его состояние и ошибка.",
         "script:string:id скрипта",
         "path:string:путь внутри папки скрипта",
         "content:string:новое содержимое"
      );
      method5498(
         var0,
         "create_script",
         "Создать скрипт-папку из шаблона (" + Server.method229() + ") и включить его.",
         "name:string:имя скрипта",
         "template:string:шаблон?"
      );
      method5498(var0, "delete_file", "Удалить файл скрипта (кроме main.js).", "script:string:id скрипта", "path:string:путь внутри папки скрипта");
      method5498(var0, "reload_script", "Перезагрузить скрипт.", "script:string:id скрипта");
      method5498(var0, "set_script_enabled", "Включить или выключить скрипт.", "script:string:id скрипта", "enabled:boolean:включить?");
      method5498(var0, "check_syntax", "Проверить синтаксис JS без записи.", "code:string:код");
      method5498(var0, "get_logs", "Последние строки консоли скриптов.", "script:string:id скрипта?", "limit:number:сколько строк, до 300?");
      method5498(
         var0, "run_js", "Выполнить JS в живом окружении скрипта (или в общей консоли). Возвращает результат.", "code:string:код", "script:string:id скрипта?"
      );
      method5498(var0, "game_state", "Состояние игры: мир, сервер, позиция и здоровье игрока, FPS, экран.");
      return var0;
   }

   private static Storage host() {
      Storage var0 = Zenith.getInstance().getStorage();
      if (var0 == null) {
         throw new IllegalStateException("скриптовый движок не запущен");
      } else {
         return var0;
      }
   }

   private static String method5500(JsonObject var0, String var1) {
      if (var0.has(var1) && !var0.get(var1).isJsonNull()) {
         return var0.get(var1).getAsString();
      } else {
         throw new IllegalArgumentException("нет параметра " + var1);
      }
   }

   private static JsonObject method5501(Storage var0, Script var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("id", var1.getId());
      var2.addProperty("name", var1.method1403());
      var2.addProperty("version", var1.getVersion());
      var2.addProperty("state", var1.getType2().name().toLowerCase());
      var2.addProperty("enabled", var0.method1297(var1));
      var2.addProperty("error", var1.getError());
      JsonArray var3 = new JsonArray();

      for (Module1 var5 : var1.getItems3().values()) {
         JsonObject var6 = new JsonObject();
         var6.addProperty("name", var5.getName());
         var6.addProperty("enabled", var5.isEnabled());
         var6.addProperty("settings", var5.getSettings().size());
         var3.add(var6);
      }

      var2.add("modules", var3);
      JsonArray var8 = new JsonArray();
      Path var9 = var1.getPath() == null ? var0.getPath() : var1.getPath();

      for (Path var7 : var0.method1345(var1)) {
         var8.add(var9.relativize(var7).toString().replace('\\', '/'));
      }

      var2.add("files", var8);
      return var2;
   }

   public static String method5502(String var0, JsonObject var1) throws Exception {
      Storage var2 = host();
      switch (var0) {
         case "get_api_docs":
            return Files.readString(var2.getPath().resolve("API.md"), StandardCharsets.UTF_8);
         case "list_scripts":
            JsonArray var21 = new JsonArray();

            for (Script var28 : var2.method1293()) {
               var21.add(method5501(var2, var28));
            }

            return gson.toJson(var21);
         case "read_file":
            Script var20 = Server.method230(var2, var1);
            Path var25 = var2.method1346(var20, method5500(var1, "path"));
            return var2.method1306(var25);
         case "write_file":
            Script var19 = Server.method230(var2, var1);
            Path var24 = var2.method1346(var19, method5500(var1, "path"));
            String var27 = var24.getFileName().toString().toLowerCase();
            if (!var27.endsWith(".js") && !var27.endsWith(".json") && !var27.endsWith(".md") && !var27.endsWith(".txt")) {
               throw new IllegalArgumentException("можно писать только .js, .json, .md и .txt");
            }

            var2.method1347(var19, var24, method5500(var1, "content"));
            var2.method1299(var19);
            return gson.toJson(method5501(var2, var19));
         case "create_script":
            Script var18 = var2.method1340(method5500(var1, "name"), var1.has("template") ? var1.get("template").getAsString() : "empty");
            var2.method1298(var18, true);
            return gson.toJson(method5501(var2, var18));
         case "delete_file":
            Script var17 = Server.method230(var2, var1);
            Path var23 = var2.method1346(var17, method5500(var1, "path"));
            if (var23.equals(var17.getPath2().toAbsolutePath().normalize())) {
               throw new IllegalArgumentException("main.js удалить нельзя");
            }

            Files.deleteIfExists(var23);
            var2.method1299(var17);
            return "удалён";
         case "reload_script":
            Script var16 = Server.method230(var2, var1);
            var2.method1299(var16);
            return gson.toJson(method5501(var2, var16));
         case "set_script_enabled":
            Script var15 = Server.method230(var2, var1);
            var2.method1298(var15, var1.has("enabled") && var1.get("enabled").getAsBoolean());
            return gson.toJson(method5501(var2, var15));
         case "check_syntax":
            try {
               var2.method1292(method5500(var1, "code"));
               return "ok";
            } catch (Exception var11) {
               return "ошибка: " + Storage.method1325(var11);
            }
         case "get_logs":
            Script var14 = var1.has("script") ? var2.method1294(var1.get("script").getAsString()) : null;
            int var22 = var1.has("limit") ? Math.max(1, Math.min(300, var1.get("limit").getAsInt())) : 60;
            var var7 = var2.method1349(var14);
            StringBuilder var8 = new StringBuilder();

            for (int var9 = Math.max(0, var7.size() - var22); var9 < var7.size(); var9++) {
               Storage.Data2 var10 = (Storage.Data2)var7.get(var9);
               var8.append('[').append(var10.getText2()).append("] ").append(var10.getText()).append(": ").append(var10.text()).append('\n');
            }

            return var8.isEmpty() ? "пусто" : var8.toString();
         case "run_js":
            Script var13 = var1.has("script") ? var2.method1294(var1.get("script").getAsString()) : null;

            try {
               return String.valueOf(var2.method1341(var13 != null && var13.isRunning() ? var13 : null, method5500(var1, "code")));
            } catch (Throwable var12) {
               return "ошибка: " + Storage.method1325(var12);
            }
         case "game_state":
            MinecraftClient var5 = MinecraftClient.getInstance();
            JsonObject var6 = new JsonObject();
            var6.addProperty("inGame", var5.world != null && var5.player != null);
            var6.addProperty("server", var5.getCurrentServerEntry() == null ? "" : var5.getCurrentServerEntry().address);
            var6.addProperty("fps", var5.getCurrentFps());
            var6.addProperty("screen", var5.currentScreen == null ? "" : var5.currentScreen.getClass().getSimpleName());
            if (var5.player != null) {
               var6.addProperty("x", var5.player.getX());
               var6.addProperty("y", var5.player.getY());
               var6.addProperty("z", var5.player.getZ());
               var6.addProperty("health", var5.player.getHealth());
               var6.addProperty("dimension", var5.world.getRegistryKey().getValue().toString());
            }

            return gson.toJson(var6);
         default:
            throw new IllegalArgumentException("неизвестный инструмент: " + var0);
      }
   }
}

package org.zenith.client.scripting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerArray;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.text.Text;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.settings.Setting;

public final class ScriptModuleApi {
   public static final String[] texts = new String[]{
      "Module",
      "events",
      "settings",
      "player",
      "world",
      "client",
      "mc",
      "input",
      "rotation",
      "render",
      "render3d",
      "Color",
      "storage",
      "console",
      "print",
      "chat",
      "include",
      "setTimeout",
      "setInterval",
      "clearTimeout",
      "clearInterval",
      "Timer",
      "script",
      "keys",
      "commands",
      "hooks",
      "requireApi",
      "sound",
      "image",
      "particles",
      "waypoints",
      "ui",
      "pathfind",
      "camera",
      "http",
      "cloud",
      "packets",
      "exports",
      "require",
      "i18n",
      "anim",
      "util",
      "ai",
      "server",
      "combat",
      "containers",
      "schematic",
      "crosshair",
      "music",
      "debug",
      "profile",
      "test",
      "assert"
   };
   private static final AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(Script.Type.values().length);

   private ScriptModuleApi() {
   }

   public static void method321(Script var0, Script.Type var1, Function var2) {
      var0.method1414(var1, var2);
      atomicIntegerArray.incrementAndGet(var1.ordinal());
      var0.method1413(() -> atomicIntegerArray.decrementAndGet(var1.ordinal()));
   }

   public static boolean method322(Script.Type var0) {
      return atomicIntegerArray.get(var0.ordinal()) > 0;
   }

   public static Map<String, Setting> method323(Script var0) {
      return var0.getItems6();
   }

   public static Module method324(Script var0, String var1) {
      Module1 var2 = Module1.method7803(var0, var1);
      if (!var2.isEnabledRaw()) {
         var2.toggle();
      }

      var0.method1413(() -> {
         if (var2.isEnabledRaw()) {
            var2.toggle();
         }
      });
      return var2;
   }

   public static void method325(Storage var0, Script var1, ScriptableObject var2) {
      ScriptScreenApi.method7045(var1, var2);
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var2, var2);
      var3.method6277("events", ScriptEventBus.method1869(var0, var1, var2));
      var3.method6277("player", ScriptScreenApi.method7049(var1, var2));
      var3.method6277("world", ScriptScreenApi.method7055(var2));
      var3.method6277("client", ScriptScreenApi.method7061(var1, var0, var2));
      var3.method6277("mc", ScriptScreenApi.method7065(var2));
      var3.method6277("input", ScriptScreenApi.method7056(var1, var2));
      var3.method6277("rotation", ScriptScreenApi.method7060(var1, var2));
      var3.method6277("render", ScriptDrawApi.method2607(var1, var0, var2));
      var3.method6277("render3d", ScriptDrawApi.method2609(var2));
      var3.method6277("Color", ScriptDrawApi.method2611(var2));
      var3.method6277("hooks", ScriptHooks.method5050(var0, var1, var2));
      ScriptWorldApi.method4817(var1, var2, (ScriptableObject)var2.get("world", var2));
      ScriptWorldApi.method4820(var2, (ScriptableObject)var2.get("player", var2));
      ScriptGuiApi.method5320(var1, var0, var2, (ScriptableObject)var2.get("render", var2));
      ScriptGuiApi.method5322(var2, (ScriptableObject)var2.get("render3d", var2));
      ScriptValueUtils.method6246(var2, (ScriptableObject)var2.get("client", var2))
         .method6277("friends", ScriptNetApi.method3285(var1, var2))
         .method6277("configs", ScriptNetApi.method3286(var1, var2));
      var3.method6277("sound", RemoteUtil.method1845(var1, var0, var2));
      var3.method6277("image", RemoteUtil.method1844(var1, var0, var2));
      var3.method6277("particles", ScriptGuiApi.method5323(var1, var0, var2));
      var3.method6277("waypoints", ScriptGuiApi.method5324(var1, var2));
      var3.method6277("ui", ScriptGuiApi.method5325(var1, var0, var2));
      var3.method6277("pathfind", ScriptWorldApi.method4825(var1, var2));
      var3.method6277("camera", ScriptWorldApi.method4828(var1, var2));
      var3.method6277("http", ScriptNetApi.method3281(var1, var0, var2));
      var3.method6277("cloud", ScriptNetApi.method3283(var1, var0, var2));
      var3.method6277("packets", ScriptNetApi.method3284(var1, var2));
      ScriptNotifyApi.method2200(var2, (ScriptableObject)var2.get("world", var2));
      ScriptNotifyApi.method2203(var1, var2, (ScriptableObject)var2.get("player", var2));
      ScriptNotifyApi.method2207(var1, var2, (ScriptableObject)((ScriptableObject)var2.get("player", var2)).get("inventory", var2));
      ScriptNotifyApi.method2195(var1, var2, (ScriptableObject)var2.get("pathfind", var2));
      var3.method6277("server", ScriptNotifyApi.method2199(var2));
      var3.method6277("combat", ScriptNotifyApi.method2202(var2));
      var3.method6277("containers", ScriptNotifyApi.method2211(var1, var0, var2));
      var3.method6277("schematic", ScriptNotifyApi.method2213(var1, var0, var2));
      ScriptImGui.method646(var1, var0, var2, (ScriptableObject)var2.get("ui", var2));
      ScriptImGui.method653(var1, var2, (ScriptableObject)var2.get("render3d", var2));
      ScriptImGui.method654(var2, (ScriptableObject)var2.get("render", var2));
      ScriptImGui.method655(var1, var2, (ScriptableObject)var2.get("player", var2));
      ScriptRequire.method4889(var0, var1, var2);
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var2);
      JsonObject var5 = var0.method1314(var1);
      ScriptSettingsApi.method1221(var4, var1, var0, var2, ScriptSettingsApi.method1226(var1, var5));
      var3.method6277("settings", var4.getScriptableObject());
      var3.method6274("Module", var3x -> method329(var0, var1, var2, var3x));
      ScriptValueUtils.Entry2 var6 = ScriptValueUtils.method6247(var2);
      var6.method6274("log", var2x -> {
         var0.method1327(var1, "info", method326(var2x));
         return null;
      });
      var6.method6274("info", var2x -> {
         var0.method1327(var1, "info", method326(var2x));
         return null;
      });
      var6.method6274("warn", var2x -> {
         var0.method1327(var1, "warn", method326(var2x));
         return null;
      });
      var6.method6274("error", var2x -> {
         var0.method1327(var1, "error", method326(var2x));
         return null;
      });
      var3.method6277("console", var6.getScriptableObject());
      var3.method6274("print", var2x -> {
         var0.method1327(var1, "info", method326(var2x));
         return null;
      });
      var3.method6274("chat", var0x -> {
         if (ScriptEntityApi.method6575().player != null) {
            ScriptEntityApi.method6575().player.sendMessage(Text.literal(ScriptValueUtils.method6254(var0x, 0, "")), false);
         }

         return null;
      });
      ScriptableObject var7 = (ScriptableObject)ScriptNotifyApi.method2197(var1, var0, var2);
      ScriptValueUtils.Entry2 var8 = ScriptValueUtils.method6246(var2, (ScriptableObject)var2.get("chat", var2));

      for (Object var12 : var7.getAllIds()) {
         var8.method6277(String.valueOf(var12), var7.get(String.valueOf(var12), var7));
      }

      var3.method6274(
         "setTimeout", var2x -> (double)var0.method1329(var1, ScriptValueUtils.method6260(var2x, 0), (long)ScriptValueUtils.method6256(var2x, 1, 0.0), false)
      );
      var3.method6274(
         "setInterval", var2x -> (double)var0.method1329(var1, ScriptValueUtils.method6260(var2x, 0), (long)ScriptValueUtils.method6256(var2x, 1, 50.0), true)
      );
      var3.method6274("clearTimeout", var2x -> {
         var0.method1330(var1, ScriptValueUtils.method6257(var2x, 0, -1));
         return null;
      });
      var3.method6274("clearInterval", var2x -> {
         var0.method1330(var1, ScriptValueUtils.method6257(var2x, 0, -1));
         return null;
      });
      var3.method6274("Timer", var1x -> method327(var2));
      var3.method6274("requireApi", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, 1);
         if (var1x > 3) {
            throw ScriptValueUtils.method6251("скрипту нужна версия API " + var1x + ", у клиента 3");
         } else {
            return 3.0;
         }
      });
      HashSet var13 = new HashSet();
      var3.method6274("include", var4x -> {
         method331(var0, var1, var2, ScriptValueUtils.method6254(var4x, 0, ""), var13);
         return null;
      });
      var3.method6277(
         "script",
         ScriptValueUtils.method6247(var2)
            .method6277("id", var1.getId())
            .method6277("name", var1.method1403())
            .method6277("version", var1.getVersion())
            .method6277("author", var1.getText2())
            .method6277("api", 3.0)
            .getScriptableObject()
      );
      ScriptRequire.method4891(var0, var1, var2, (ScriptableObject)var2.get("script", var2));
      ScriptValueUtils.Entry2 var14 = ScriptValueUtils.method6247(var2);
      var14.method6274("get", var2x -> {
         JsonElement var3x = var1.method1415().method1006(ScriptValueUtils.method6254(var2x, 0, ""));
         return var3x == null ? ScriptValueUtils.arg(var2x, 1) : ScriptValueUtils.method6273(var2, var3x);
      });
      var14.method6274("set", var1x -> {
         var1.method1415().method1008(ScriptValueUtils.method6254(var1x, 0, ""), ScriptValueUtils.method6271(ScriptValueUtils.arg(var1x, 1), 0));
         return null;
      });
      var14.method6274("remove", var1x -> {
         var1.method1415().method1008(ScriptValueUtils.method6254(var1x, 0, ""), null);
         return null;
      });
      var14.method6274("keys", var2x -> ScriptValueUtils.method6267(var2, new ArrayList<>(var1.method1415().method1007())));
      var14.method6274("size", var1x -> (double)var1.method1415().size());
      var3.method6277("storage", var14.getScriptableObject());
      ScriptValueUtils.Entry2 var15 = ScriptValueUtils.method6247(var2);
      var15.method6274(
         "register",
         var2x -> {
            String var3x = ScriptValueUtils.method6254(var2x, 0, "").toLowerCase(Locale.ROOT).trim();
            if (!var3x.matches("[a-z0-9а-яё_-]{1,24}")) {
               throw ScriptValueUtils.method6251("имя команды: 1-24 символа, буквы, цифры, _ и -");
            } else if (!List.of("js", "friend", "config", "bind", "binds", "macro", "help").contains(var3x)
               && !Zenith.getInstance().getCommandManager().commandNames().contains(var3x)) {
               for (Script var5x : var0.method1293()) {
                  if (var5x != var1 && var5x.isRunning() && var5x.getItems7().containsKey(var3x)) {
                     throw ScriptValueUtils.method6251("команда ." + var3x + " уже занята скриптом «" + var5x.method1403() + "»");
                  }
               }

               if (var1.getItems7().size() >= 32 && !var1.getItems7().containsKey(var3x)) {
                  throw ScriptValueUtils.method6251("у скрипта больше 32 команд");
               } else {
                  var1.getItems7().put(var3x, ScriptValueUtils.method6260(var2x, 1));
                  return null;
               }
            } else {
               throw ScriptValueUtils.method6251("команда ." + var3x + " занята клиентом");
            }
         }
      );
      var3.method6277("commands", var15.getScriptableObject());
      var3.method6277("music", ScriptMediaApi.method1736(var2));
      ScriptMediaApi.method1738(var1, var0, var2, (ScriptableObject)var2.get("image", var2));
      ScriptMediaApi.method1740(var1, var0, var2, (ScriptableObject)var2.get("sound", var2));
      ScriptValueUtils.Entry2 var16 = ScriptValueUtils.method6247(var2);
      var16.method6274("code", var0x -> {
         String var1x = ScriptValueUtils.method6254(var0x, 0, "").toLowerCase(Locale.ROOT);

         try {
            return (double)InputUtil.fromTranslationKey("key.keyboard." + var1x).getCode();
         } catch (RuntimeException var3x) {
            return -1.0;
         }
      });
      var16.method6274("name", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
         return var1x < 0 ? "" : Type.KEYSYM.createFromCode(var1x).getLocalizedText().getString();
      });
      var3.method6277("keys", var16.getScriptableObject());
      ScriptTestApi.method5410(var0, var1, var2);
   }

   private static String method326(Object[] var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length && var1.length() < 4000; var2++) {
         if (var2 > 0) {
            var1.append(' ');
         }

         Object var3 = var0[var2];
         if (var3 instanceof Scriptable var4 && !(var3 instanceof Function)) {
            try {
               var1.append(ScriptValueUtils.method6271(var4, 0));
               continue;
            } catch (RuntimeException var6) {
            }
         }

         var1.append(ScriptValueUtils.method6255(var3));
      }

      return var1.toString();
   }

   private static Scriptable method327(Scriptable var0) {
      long[] var1 = new long[]{System.currentTimeMillis()};
      return ScriptValueUtils.method6247(var0)
         .method6274("passed", var1x -> System.currentTimeMillis() - var1[0] >= ScriptValueUtils.method6256(var1x, 0, 0.0))
         .method6274("reset", var1x -> {
            var1[0] = System.currentTimeMillis();
            return null;
         })
         .method6274("elapsed", var1x -> (double)(System.currentTimeMillis() - var1[0]))
         .getScriptableObject();
   }

   private static ModuleCategory method328(String var0) {
      String var1 = var0.toLowerCase(Locale.ROOT);

      return switch (var1) {
         case "combat" -> ModuleCategory.COMBAT;
         case "movement" -> ModuleCategory.MOVEMENT;
         case "render", "visuals", "visual" -> ModuleCategory.RENDER;
         case "player", "pve" -> ModuleCategory.PLAYER;
         default -> ModuleCategory.MISC;
      };
   }

   private static Object method329(Storage var0, Script var1, Scriptable var2, Object[] var3) {
      String var4 = ScriptValueUtils.method6254(var3, 0, "").trim();
      if (!var4.isEmpty() && var4.length() <= 40) {
         Scriptable var5 = ScriptValueUtils.method6262(var3, 1);
         Module1 var6 = var1.getItems32().get(var4);
         if (var6 != null) {
            return method330(var0, var1, var2, var6);
         } else if (var1.getItems32().size() >= 32) {
            throw ScriptValueUtils.method6251("у скрипта больше 32 модулей");
         } else {
            ModuleCategory var7 = method328(var5 == null ? ScriptValueUtils.method6254(var3, 1, "misc") : ScriptValueUtils.method6266(var5, "category", "misc"));
            String var8 = var5 == null ? "" : ScriptValueUtils.method6266(var5, "description", "");
            String var9 = Module1.method7805(var1, var4);

            for (Module var11 : Zenith.getInstance().getModuleManager().getItems()) {
               if (var11.getId().equals(var9) || var11.getName().equalsIgnoreCase(var4) && !(var11 instanceof Module1)) {
                  throw ScriptValueUtils.method6251("модуль «" + var4 + "» уже есть в клиенте");
               }
            }

            Module1 var12 = Module1.method7802(var1, var4, var8, var7);
            var12.setFlag2(var5 != null && ScriptValueUtils.method6265(var5, "enabled", false));
            var1.getItems32().put(var4, var12);
            Zenith.getInstance().getModuleManager().method2482(var12);
            return method330(var0, var1, var2, var12);
         }
      } else {
         throw ScriptValueUtils.method6251("имя модуля: от 1 до 40 символов");
      }
   }

   private static Scriptable method330(Storage var0, Script var1, Scriptable var2, Module1 var3) {
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var2);
      var4.method6277("name", var3.getName());
      var4.method6274("enabled", var1x -> var3.isEnabled());
      var4.method6274("toggle", var1x -> {
         var3.toggle();
         return var3.isEnabled();
      });
      var4.method6274("setEnabled", var1x -> {
         var3.setToggled(ScriptValueUtils.method6259(var1x, 0, true));
         return null;
      });
      var4.method6274("key", var1x -> (double)var3.getKeyCode());
      var4.method6274("setKey", var1x -> {
         var3.setKeyCode(ScriptValueUtils.method6257(var1x, 0, -1));
         return null;
      });
      Scriptable[] var5 = new Scriptable[]{null};
      var4.method6274("onEnable", var2x -> {
         var3.setFunction(ScriptValueUtils.method6260(var2x, 0));
         return var5[0];
      });
      var4.method6274("onDisable", var2x -> {
         var3.setFunction2(ScriptValueUtils.method6260(var2x, 0));
         return var5[0];
      });
      JsonObject var6 = var0.method1313(var1, var3.getName());
      JsonObject var7 = var6 != null && var6.has("Settings") && var6.get("Settings").isJsonObject() ? var6.getAsJsonObject("Settings") : null;
      var var8 = new ArrayList<>(var3.getSettings());
      ScriptSettingsApi.method1221(var4, var1, var0, var2, ScriptSettingsApi.method1225(var8, var7, var3::method7804));
      var5[0] = var4.getScriptableObject();
      return var5[0];
   }

   private static void method331(Storage var0, Script var1, Scriptable var2, String var3, Set<String> var4) {
      if (var1.getPath() == null) {
         throw ScriptValueUtils.method6251("include доступен только скриптам-папкам");
      } else {
         if (!var3.toLowerCase(Locale.ROOT).endsWith(".js")) {
            var3 = var3 + ".js";
         }

         Path var5 = var1.getPath2().getParent().toAbsolutePath().normalize();
         Path var6 = var5.resolve(var3).toAbsolutePath().normalize();
         Path var7 = var1.getPath().toAbsolutePath().normalize();
         if (var6.startsWith(var7) && Files.isRegularFile(var6)) {
            String var8 = var6.toString();
            if (var4.add(var8)) {
               if (var4.size() > 64) {
                  throw ScriptValueUtils.method6251("больше 64 подключённых файлов");
               } else {
                  String var9;
                  try {
                     var9 = var0.method1306(var6);
                  } catch (Exception var12) {
                     throw ScriptValueUtils.method6251("не удалось прочитать " + var3);
                  }

                  String var10 = var1.getId() + "/" + var7.relativize(var6).toString().replace('\\', '/');
                  Context var11 = Context.getCurrentContext();
                  var11.compileString(var9, var10, 1, null).exec(var11, var2);
               }
            }
         } else {
            throw ScriptValueUtils.method6251("файл не найден: " + var3);
         }
      }
   }
}

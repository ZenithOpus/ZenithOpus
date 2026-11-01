package org.zenith.utility.misc;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.ScriptValueUtils;

public final class ScriptPermissions {
   public static final String text = "chat";
   public static final String text2 = "ai";
   public static final String text3 = "screenshot";
   public static final String text4 = "modules";
   public static final String text5 = "friends";
   public static final String text6 = "configs";
   public static final String text7 = "clipboard";
   public static final String text8 = "microphone";
   public static final String text9 = "tts";
   public static final String text10 = "net";
   public static final Map<String, String> items = new LinkedHashMap<>();

   private ScriptPermissions() {
   }

   public static Set<String> method(Script var0) {
      LinkedHashSet var1 = new LinkedHashSet();

      for (String var5 : Script.pattern.split(var0.text6)) {
         String var6 = var5.strip().toLowerCase(Locale.ROOT);
         if (items.containsKey(var6)) {
            var1.add(var6);
         }
      }

      if (var0.method1406()) {
         var1.add("net");
      }

      return var1;
   }

   public static boolean method2(Script var0) {
      return var0.flag;
   }

   public static boolean method3(Script var0, String var1) {
      return !method2(var0) || method(var0).contains(var1);
   }

   public static void method4(Script var0, String var1) {
      if (!method3(var0, var1)) {
         throw ScriptValueUtils.method6251(
            "скрипту из маркета нужно разрешение «" + items.getOrDefault(var1, var1) + "»: автор должен добавить в шапку // @permissions " + var1
         );
      }
   }

   public static String method5(Script var0) {
      var var1 = method(var0).stream().map(items::get).toList();
      return String.join(", ", var1);
   }

   static {
      items.put("net", "сеть");
      items.put("chat", "чат и команды");
      items.put("ai", "нейросеть игрока");
      items.put("screenshot", "скриншоты");
      items.put("modules", "модули клиента");
      items.put("friends", "друзья");
      items.put("configs", "конфиги");
      items.put("clipboard", "буфер обмена");
      items.put("microphone", "микрофон");
      items.put("tts", "озвучка текста");
   }
}

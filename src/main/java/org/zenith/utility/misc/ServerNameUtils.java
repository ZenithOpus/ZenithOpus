package org.zenith.utility.misc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.zenith.utility.network.Cloud;

public final class ServerNameUtils {
   private static final Map<String, String> items = new LinkedHashMap<>();

   private ServerNameUtils() {
   }

   public static String display(String var0) {
      String var1 = normalize(var0);
      if (var1.isEmpty()) {
         return "";
      } else if (var1.startsWith("singleplayer")) {
         return "одиночная игра";
      } else if (var1.equals("realms")) {
         return "Realms";
      } else {
         String var2 = items.get(var1);
         return var2 == null ? var1 : var2;
      }
   }

   public static String normalize(String var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = var0.trim().toLowerCase(Locale.ROOT);
         if (!var1.isEmpty() && !var1.startsWith("singleplayer") && !var1.equals("realms")) {
            if (var1.endsWith(":25565")) {
               var1 = var1.substring(0, var1.length() - 6);
            }

            return Cloud.method7912(var1);
         } else {
            return var1;
         }
      }
   }

   public static boolean method5640(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.toLowerCase(Locale.ROOT);
         return var1.contains("анарх") || var1.contains("anarch");
      } else {
         return false;
      }
   }

   public static String method5641(String var0) {
      if (var0 == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder(var0.length());
         boolean var2 = false;

         for (int var3 = 0; var3 < var0.length() && var1.length() < 24; var3++) {
            char var4 = var0.charAt(var3);
            boolean var5 = Character.isLetterOrDigit(var4) || var4 == '-' || var4 == '_' || var4 == '.' || var4 == '#';
            if (var5) {
               var1.append(var4);
               var2 = false;
            } else if (!var2 && var1.length() > 0) {
               var1.append(' ');
               var2 = true;
            }
         }

         return var1.toString().strip();
      }
   }

   public static String method5642(String var0, String var1) {
      String var2 = normalize(var0);
      if (var2.startsWith("funtime.") && method5640(var1)) {
         StringBuilder var3 = new StringBuilder();

         for (int var4 = 0; var4 < var1.length(); var4++) {
            char var5 = var1.charAt(var4);
            if (var5 >= '0' && var5 <= '9') {
               var3.append(var5);
            } else if (var3.length() > 0) {
               break;
            }
         }

         return var3.length() != 0 && var3.length() <= 4 ? "/an" + var3 : "";
      } else {
         return "";
      }
   }

   public static String method5643(String var0, String var1) {
      String var2 = display(var0);
      String var3 = method5641(var1);
      if (var2.isEmpty()) {
         return var3;
      } else {
         return !var3.isEmpty() && !var3.equalsIgnoreCase(var2) ? var2 + " · " + var3 : var2;
      }
   }

   static {
      items.put("funtime.su", "FunTime");
      items.put("funtime.ru", "FunTime");
      items.put("holyworld.ru", "HolyWorld");
      items.put("reallyworld.ru", "ReallyWorld");
      items.put("masedworld.net", "MasedWorld");
      items.put("mineblaze.net", "MineBlaze");
      items.put("cheatmine.ru", "CheatMine");
      items.put("dexland.ru", "DexLand");
      items.put("simpleage.ru", "SimpleAge");
      items.put("hypixel.net", "Hypixel");
      items.put("2b2t.org", "2b2t");
   }
}

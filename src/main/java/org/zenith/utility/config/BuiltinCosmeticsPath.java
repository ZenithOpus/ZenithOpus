package org.zenith.utility.config;

import java.nio.file.Path;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;

public final class BuiltinCosmeticsPath {
   private static final String text = "zenith";
   private static final String text2 = "assets/zenith/cosmetics";
   public static final String text3 = "builtin/";
   private static volatile Path path;
   private static volatile boolean flag;

   private BuiltinCosmeticsPath() {
   }

   public static Path method4447() {
      if (!flag) {
         synchronized (BuiltinCosmeticsPath.class) {
            if (!flag) {
               path = method4448();
               flag = true;
            }
         }
      }

      return path;
   }

   private static Path method4448() {
      try {
         return FabricLoader.getInstance()
            .getModContainer("zenith")
            .flatMap(var0 -> var0.findPath("assets/zenith/cosmetics"))
            .map(var0 -> var0.toAbsolutePath().normalize())
            .orElse(null);
      } catch (Exception var1) {
         return null;
      }
   }

   public static boolean method4449(Path var0) {
      Path var1 = method4447();
      return (boolean)(var1 != null && var0 != null ? var0.toAbsolutePath().normalize().startsWith(var1) : 0);
   }

   public static String method4450(Path var0) {
      Path var1 = method4447();
      if (var1 != null && var0 != null) {
         Path var2 = var0.toAbsolutePath().normalize();
         return !var2.startsWith(var1) ? "" : "builtin/" + var1.relativize(var2).toString().replace('\\', '/');
      } else {
         return "";
      }
   }

   public static Path method4451(String var0) {
      Path var1 = method4447();
      if (var1 != null && var0 != null) {
         if (!var0.toLowerCase(Locale.ROOT).startsWith("builtin/")) {
            return null;
         } else {
            String var2 = var0.substring("builtin/".length());
            if (var2.isBlank()) {
               return null;
            } else {
               try {
                  Path var3 = var1.resolve(var2).normalize();
                  return var3.startsWith(var1) ? var3 : null;
               } catch (Exception var4) {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }
}

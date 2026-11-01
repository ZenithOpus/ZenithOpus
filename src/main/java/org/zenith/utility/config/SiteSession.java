package org.zenith.utility.config;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.Zenith;

public final class SiteSession {
   private static final String text = "zenith.site.token";
   private static final String text2 = "ZENITH_SITE_TOKEN";
   private static final int CONST_intValue = 8;
   private static final int CONST_intValue2 = 4096;
   private static final long CONST_longValue = 10000L;
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Site");
   private static volatile String text3 = "";
   private static volatile long longValue;
   private static volatile Path path;
   private static volatile long longValue2;
   private static final Set<String> items = ConcurrentHashMap.newKeySet();
   private static volatile Method[] methods;

   private SiteSession() {
   }

   public static String method2289() {
      String var0 = text3;
      long var1 = System.currentTimeMillis();
      if (!var0.isEmpty()) {
         if (path != null && var1 - longValue >= 10000L) {
            synchronized (SiteSession.class) {
               if (System.currentTimeMillis() - longValue < 10000L) {
                  return text3;
               } else {
                  longValue = System.currentTimeMillis();
                  Path var4 = path;
                  if (var4 != null && method2290(var4) != longValue2) {
                     text3 = method2293();
                  }

                  return text3;
               }
            }
         } else {
            return var0;
         }
      } else if (var1 - longValue < 10000L) {
         return "";
      } else {
         synchronized (SiteSession.class) {
            if (!text3.isEmpty()) {
               return text3;
            } else {
               longValue = System.currentTimeMillis();
               text3 = method2293();
               return text3;
            }
         }
      }
   }

   private static long method2290(Path var0) {
      try {
         return Files.isRegularFile(var0) ? Files.getLastModifiedTime(var0).toMillis() : -1L;
      } catch (Exception var2) {
         return -1L;
      }
   }

   public static boolean method2291() {
      return method2289().length() >= 8;
   }

   public static void method2292() {
      synchronized (SiteSession.class) {
         text3 = "";
         longValue = 0L;
         path = null;
         longValue2 = 0L;
      }
   }

   private static String method2293() {
      path = null;
      longValue2 = 0L;
      String var0 = method2300(System.getProperty("zenith.site.token", ""), "property");
      if (!var0.isEmpty()) {
         return var0;
      } else {
         var0 = method2300(System.getenv("ZENITH_SITE_TOKEN"), "environment");
         if (!var0.isEmpty()) {
            return var0;
         } else {
            var0 = method2295();
            if (!var0.isEmpty()) {
               return var0;
            } else {
               var0 = method2297();
               return !var0.isEmpty() ? var0 : method2294();
            }
         }
      }
   }

   private static String method2294() {
      try {
         File var0 = Zenith.file;
         return var0 == null ? "" : method2298(var0.toPath().resolve("site-token.txt"));
      } catch (Throwable var1) {
         return "";
      }
   }

   private static String method2295() {
      for (Method var3 : method2296()) {
         try {
            Object var4 = var3.invoke(null);
            String var5 = method2300(var4 == null ? "" : var4.toString(), "protection");
            if (!var5.isEmpty()) {
               return var5;
            }
         } catch (RuntimeException | ReflectiveOperationException var6) {
         }
      }

      return "";
   }

   private static Method[] method2296() {
      Method[] var0 = methods;
      if (var0 != null) {
         return var0;
      } else {
         ArrayList var1 = new ArrayList();

         try {
            Class var2 = Class.forName("project.weye.Session");

            for (String var6 : new String[]{"getToken", "getSessionToken", "token"}) {
               try {
                  var1.add(var2.getMethod(var6));
               } catch (RuntimeException | ReflectiveOperationException var8) {
               }
            }
         } catch (RuntimeException | ReflectiveOperationException var9) {
         }

         var0 = (Method[])var1.toArray(new Method[0]);
         methods = var0;
         return var0;
      }
   }

   private static String method2297() {
      Path var0 = method2299();
      return var0 == null ? "" : method2298(var0.resolve("Zenith-Launcher").resolve("session.token"));
   }

   private static String method2298(Path var0) {
      try {
         if (!Files.isReadable(var0)) {
            return "";
         } else if (Files.size(var0) > 4160L) {
            method2301(var0.getFileName().toString(), "файл " + Files.size(var0) + " байт");
            return "";
         } else {
            long var1 = Files.getLastModifiedTime(var0).toMillis();
            String var3 = method2300(new String(Files.readAllBytes(var0), StandardCharsets.UTF_8), var0.getFileName().toString());
            if (!var3.isEmpty()) {
               path = var0;
               longValue2 = var1;
            }

            return var3;
         }
      } catch (Exception var4) {
         return "";
      }
   }

   private static Path method2299() {
      String var0 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

      try {
         if (var0.contains("win")) {
            String var4 = System.getenv("LOCALAPPDATA");
            return var4 != null && !var4.isBlank() ? Path.of(var4) : null;
         } else {
            String var1 = System.getProperty("user.home", "");
            if (var1.isBlank()) {
               return null;
            } else if (!var0.contains("mac") && !var0.contains("darwin")) {
               String var2 = System.getenv("XDG_DATA_HOME");
               return var2 != null && !var2.isBlank() ? Path.of(var2) : Path.of(var1, ".local", "share");
            } else {
               return Path.of(var1, "Library", "Application Support");
            }
         }
      } catch (RuntimeException var3) {
         return null;
      }
   }

   private static String method2300(String var0, String var1) {
      if (var0 == null) {
         return "";
      } else {
         String var2 = var0.strip();
         if (var2.isEmpty()) {
            return "";
         } else if (var2.length() >= 8 && var2.length() <= 4096) {
            for (int var3 = 0; var3 < var2.length(); var3++) {
               char var4 = var2.charAt(var3);
               if (var4 <= ' ' || var4 > '~') {
                  method2301(var1, "непечатаемый символ, длина " + var2.length());
                  return "";
               }
            }

            return var2;
         } else {
            method2301(var1, "длина " + var2.length());
            return "";
         }
      }
   }

   private static void method2301(String var0, String var1) {
      if (items.add(var0)) {
         logger.warn("Сайт: токен сессии из {} отвергнут ({})", var0, var1);
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegionUtil {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/NetRegion");
   private static final String text = "net-region.json";
   private static final String text2 = "/net/region";
   private static final String text3 = "zenith.region";
   public static final String text4 = "api";
   public static final String text5 = "site";
   public static final String text6 = "files";
   public static final String text7 = "nexus";
   private static final Map<String, List<String>> items = Map.of(
      "api",
      List.of("https://api.zenithdlc.com", "https://api.zenithdlc.org"),
      "site",
      List.of("https://zenithdlc.com", "https://zenithdlc.org"),
      "files",
      List.of("https://launcher.zenithdlc.com:8443", "https://launcher.zenithdlc.org:8443"),
      "nexus",
      List.of("wss://api.zenithdlc.com/nexus/ws", "wss://api.zenithdlc.org/nexus/ws")
   );
   private static final Duration duration = Duration.ofSeconds(4L);
   private static final Duration duration2 = Duration.ofSeconds(6L);
   private static final long CONST_longValue = 21600L;
   private static final long CONST_longValue2 = 604800L;
   private static final long CONST_longValue3 = 300L;
   private static final long CONST_longValue4 = 600L;
   private static final int CONST_intValue = 8;
   private static final HttpClient httpClient = HttpClients.method2670(duration);
   private static final List<Runnable> items2 = new CopyOnWriteArrayList<>();
   private static volatile Map<String, List<String>> items3 = items;
   private static volatile String text8 = "default";
   private static volatile int version;
   private static volatile Path file;
   private static volatile Map<String, List<String>> items4;
   private static volatile String text9 = "default";
   private static volatile boolean flag;

   private RegionUtil() {
   }

   public static List<String> method2037() {
      return method2041("api");
   }

   public static List<String> method2038() {
      return method2041("site");
   }

   public static List<String> method2039() {
      return method2041("files");
   }

   public static List<String> method2040() {
      return method2041("nexus");
   }

   public static List<String> method2041(String var0) {
      var var1 = items3.get(var0);
      return var1 != null && !var1.isEmpty() ? var1 : items.getOrDefault(var0, List.of());
   }

   public static String getText8() {
      return text8;
   }

   public static int version() {
      return version;
   }

   public static void method2042(Runnable var0) {
      items2.add(var0);
   }

   public static List<String> method2043(List<String> var0, String var1) {
      if (var1 != null && var0.contains(var1) && !((String)var0.get(0)).equals(var1)) {
         List<String> var2 = new ArrayList<>(var0.size());
         var2.add(var1);

         for (String var4 : var0) {
            if (!var4.equals(var1)) {
               var2.add(var4);
            }
         }

         return var2;
      } else {
         return var0;
      }
   }

   public static boolean method2044(String var0) {
      if (var0 == null) {
         return false;
      } else {
         String var1 = var0.toLowerCase(Locale.ROOT);
         return var1.equals("zenithdlc.org") || var1.endsWith(".zenithdlc.org") || var1.equals("zenithdlc.com") || var1.endsWith(".zenithdlc.com");
      }
   }

   public static synchronized void method2045(Path var0) {
      if (!flag) {
         flag = true;
         String var1 = System.getProperty("zenith.region", "").strip().toLowerCase(Locale.ROOT);
         if (!var1.equals("ru") && !var1.equals("world")) {
            file = var0.resolve("net-region.json");
            long var2 = method2046();
            if (var2 > 0L) {
               method2047(var2);
            } else {
               refresh();
            }
         } else {
            method2054(var1.equals("ru") ? method2053() : items, var1, "-Dzenith.region");
         }
      }
   }

   private static long method2046() {
      JsonObject var0 = CloudJsonIo.method5992(file);
      if (var0 == null) {
         return 0L;
      } else {
         try {
            var var1 = method2050(var0.getAsJsonObject("hosts"));
            if (var1.isEmpty()) {
               return 0L;
            } else {
               long var2 = method2052(var0.has("ttl") ? var0.get("ttl").getAsLong() : 21600L);
               long var4 = var0.has("fetched_at") ? var0.get("fetched_at").getAsLong() : 0L;
               long var6 = (System.currentTimeMillis() - var4) / 1000L;
               String var8 = var0.has("region") ? var0.get("region").getAsString() : "default";
               if (var6 >= -60L && var6 < var2) {
                  var6 = Math.max(0L, var6);
                  method2054(var1, var8, "кеш");
                  return var2 - var6;
               } else {
                  items4 = var1;
                  text9 = var8;
                  return 0L;
               }
            }
         } catch (RuntimeException var9) {
            logger.warn("Регион: кеш {} не разобрался — спросим сайт", file);
            return 0L;
         }
      }
   }

   private static void method2047(long var0) {
      CompletableFuture.delayedExecutor(Math.max(1L, var0), TimeUnit.SECONDS).execute(RegionUtil::refresh);
   }

   private static void refresh() {
      method2048(method2037(), 0).whenComplete((var0, var1) -> {
         if (var0 == null) {
            var var7 = items4;
            if (var7 != null) {
               items4 = null;
               method2054(var7, text9, "протухший кеш");
            }

            logger.info("Регион: сайт не ответил ({}), повтор через {} с", var1 == null ? "нет ответа" : var1.getMessage(), 600L);
            method2047(600L);
         } else {
            var var2 = method2050(var0.getAsJsonObject("hosts"));

            long var3;
            try {
               var3 = method2052(var0.has("ttl") ? var0.get("ttl").getAsLong() : 21600L);
            } catch (RuntimeException var6) {
               var3 = 21600L;
            }

            String var5 = var0.has("region") && var0.get("region").isJsonPrimitive() ? var0.get("region").getAsString() : "default";
            items4 = null;
            method2054(var2, var5, "сайт");
            method2055(var2, var5, var3);
            method2047(var3);
         }
      });
   }

   private static CompletableFuture<JsonObject> method2048(List<String> var0, int var1) {
      if (var1 >= var0.size()) {
         return CompletableFuture.failedFuture(new IllegalStateException("все домены молчат"));
      } else {
         HttpRequest var2;
         try {
            var2 = HttpRequest.newBuilder(URI.create((String)var0.get(var1) + "/net/region"))
               .timeout(duration2)
               .header("Accept", "application/json")
               .GET()
               .build();
         } catch (RuntimeException var4) {
            return method2048(var0, var1 + 1);
         }

         return httpClient.sendAsync(var2, BodyHandlers.ofByteArray()).handle((var2x, var3) -> {
            JsonObject var4x = var3 == null ? method2049((HttpResponse<byte[]>)var2x) : null;
            return var4x != null ? CompletableFuture.completedFuture(var4x) : method2048(var0, var1 + 1);
         }).thenCompose(var0x -> (CompletionStage<JsonObject>)var0x);
      }
   }

   private static JsonObject method2049(HttpResponse<byte[]> var0) {
      if (var0.statusCode() / 100 != 2) {
         return null;
      } else {
         try {
            JsonObject var1 = JsonParser.parseString(new String((byte[])var0.body(), StandardCharsets.UTF_8)).getAsJsonObject();
            if (var1.has("status") && var1.get("status").getAsInt() != 0) {
               return null;
            } else {
               return var1.has("hosts") && var1.get("hosts").isJsonObject() ? var1 : null;
            }
         } catch (RuntimeException var2) {
            return null;
         }
      }
   }

   private static Map<String, List<String>> method2050(JsonObject var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      if (var0 == null) {
         return var1;
      } else {
         for (String var3 : items.keySet()) {
            JsonElement var4 = var0.get(var3);
            if (var4 != null && var4.isJsonArray()) {
               ArrayList var5 = new ArrayList();

               for (JsonElement var7 : var4.getAsJsonArray()) {
                  String var8 = var7.isJsonPrimitive() ? method2051(var3, var7.getAsString()) : null;
                  if (var8 != null && !var5.contains(var8) && var5.size() < 8) {
                     var5.add(var8);
                  }
               }

               if (!var5.isEmpty()) {
                  var1.put(var3, List.copyOf(var5));
               }
            }
         }

         return var1;
      }
   }

   public static String method2051(String var0, String var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.strip();

         while (var2.endsWith("/")) {
            var2 = var2.substring(0, var2.length() - 1);
         }

         URI var3;
         try {
            var3 = new URI(var2);
         } catch (Exception var6) {
            return null;
         }

         String var4 = var3.getScheme() == null ? "" : var3.getScheme().toLowerCase(Locale.ROOT);
         boolean var5 = var0.equals("nexus");
         if (!var4.equals(var5 ? "wss" : "https")) {
            return null;
         } else if (var3.getRawUserInfo() == null && var3.getRawQuery() == null && var3.getRawFragment() == null) {
            return method2044(var3.getHost()) ? var2 : null;
         } else {
            return null;
         }
      }
   }

   private static long method2052(long var0) {
      return Math.max(300L, Math.min(604800L, var0 <= 0L ? 21600L : var0));
   }

   private static Map<String, List<String>> method2053() {
      LinkedHashMap var0 = new LinkedHashMap();
      items.forEach((var1, var2) -> var0.put(var1, List.of(var2.get(1), var2.get(0))));
      return var0;
   }

   private static synchronized void method2054(Map<String, List<String>> var0, String var1, String var2) {
      LinkedHashMap var3 = new LinkedHashMap<>(items);
      var3.putAll(var0);
      var var4 = Map.copyOf(var3);
      boolean var5 = !var4.equals(items3);
      items3 = var4;
      text8 = var1 != null && !var1.isBlank() ? var1 : "default";
      if (var5) {
         version++;
         logger.info("Регион: {} ({}), api первым {}", new Object[]{text8, var2, method2041("api").get(0)});

         for (Runnable var7 : items2) {
            try {
               var7.run();
            } catch (RuntimeException var9) {
               logger.warn("Регион: слушатель упал", var9);
            }
         }
      }
   }

   private static void method2055(Map<String, List<String>> var0, String var1, long var2) {
      Path var4 = file;
      if (var4 != null) {
         JsonObject var5 = new JsonObject();
         var5.addProperty("fetched_at", System.currentTimeMillis());
         var5.addProperty("ttl", var2);
         var5.addProperty("region", var1);
         JsonObject var6 = new JsonObject();
         var0.forEach((var1x, var2x) -> {
            JsonArray var3 = new JsonArray();
            var2x.forEach(var3::add);
            var6.add(var1x, var3);
         });
         var5.add("hosts", var6);
         CloudJsonIo.method5993(var4, var5);
      }
   }
}

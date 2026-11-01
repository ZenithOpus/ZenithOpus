package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.zenith.utility.config.SiteSession;
import org.zenith.utility.network.Cloud;

public final class UserSearchClient {
   private static final String text = "/user/search";
   private static final Duration duration = Duration.ofSeconds(8L);
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 64;
   private static final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(5L));
   private static final Map<String, List<UserSearchClient.Data>> items = new LinkedHashMap<>();
   private static volatile String host;

   private UserSearchClient() {
   }

   public static synchronized List<UserSearchClient.Data> method4758(String var0) {
      return items.get(key(var0));
   }

   public static boolean method4759(String var0) {
      return var0 == null || var0.strip().length() < 2;
   }

   public static CompletableFuture<List<UserSearchClient.Data>> method4760(String var0, int var1) {
      String var2 = var0 == null ? "" : var0.strip();
      if (var2.length() < 2) {
         return CompletableFuture.completedFuture(List.of());
      } else {
         var var3 = method4758(var2);
         if (var3 != null) {
            return CompletableFuture.completedFuture(var3);
         } else {
            JsonObject var4 = new JsonObject();
            var4.addProperty("query", var2);
            var4.addProperty("limit", Math.max(1, Math.min(25, var1)));
            var4.addProperty("token", SiteSession.method2289());
            byte[] var5 = var4.toString().getBytes(StandardCharsets.UTF_8);
            return method4763(var5, RegionUtil.method2043(RegionUtil.method2037(), host), 0).thenApply(var1x -> {
               var var2x = method4765(var1x);
               method4761(var2, var2x);
               return var2x;
            });
         }
      }
   }

   private static synchronized void method4761(String var0, List<UserSearchClient.Data> var1) {
      if (items.size() >= 64) {
         items.clear();
      }

      items.put(key(var0), var1);
   }

   public static synchronized void method4762() {
      items.clear();
   }

   private static String key(String var0) {
      return var0 == null ? "" : var0.strip().toLowerCase(Locale.ROOT);
   }

   private static CompletableFuture<JsonObject> method4763(byte[] var0, List<String> var1, int var2) {
      String var3 = (String)var1.get(var2);
      boolean var4 = var2 + 1 >= var1.size();
      long var5 = Cloud.accountUid();
      if (var5 <= 0L) {
         return CompletableFuture.failedFuture(new IllegalStateException("аккаунт ещё не готов"));
      } else {
         long var7 = Util18.method4489();

         HttpRequest var9;
         String var10;
         try {
            Builder var11 = HttpRequest.newBuilder(URI.create(var3 + "/user/search"))
               .timeout(duration)
               .header("Content-Type", "application/json; charset=utf-8")
               .header("X-Nexus-Uid", Long.toString(var5))
               .header("X-Nexus-Time", Long.toString(var7))
               .header("X-Nexus-Sign", method4766(var5, var7, var0));
            var10 = CloudTicket.method1373();
            if (var10 != null) {
               var11.header("Authorization", "Nexus " + var10);
            }

            var9 = var11.POST(BodyPublishers.ofByteArray(var0)).build();
         } catch (RuntimeException var12) {
            return CompletableFuture.failedFuture(var12);
         }

         return httpClient.sendAsync(var9, BodyHandlers.ofByteArray()).handle((var6, var7x) -> {
            if (var7x != null) {
               return !var4 ? method4763(var0, var1, var2 + 1) : CompletableFuture.failedFuture(var7x);
            } else {
               Util18.method4491((HttpResponse<?>)var6);
               int var8 = var6.statusCode();
               JsonObject var9x = SiteHttpClient.method2415(var6.body());
               if (SiteHttpClient.method2414(var8, var9x)) {
                  if (!var4) {
                     return method4763(var0, var1, var2 + 1);
                  }

                  if (var9x == null) {
                     return CompletableFuture.failedFuture(new IllegalStateException("сайт ответил " + var8));
                  }
               }

               if (var10 != null && (var8 == 401 || var8 == 403)) {
                  CloudTicket.invalidate();
               }

               try {
                  JsonObject var10x = method4764(var9x);
                  if (var8 / 100 != 2) {
                     return CompletableFuture.failedFuture(new IllegalStateException("сайт ответил " + var8));
                  } else {
                     host = var3;
                     return CompletableFuture.completedFuture(var10x);
                  }
               } catch (RuntimeException var11x) {
                  return CompletableFuture.failedFuture(var11x);
               }
            }
         }).thenCompose(var0x -> (CompletionStage<JsonObject>)var0x);
      }
   }

   private static JsonObject method4764(JsonObject var0) {
      int var1;
      try {
         var1 = var0.has("status") && !var0.get("status").isJsonNull() ? var0.get("status").getAsInt() : 0;
      } catch (RuntimeException var3) {
         var1 = 1;
      }

      if (var1 == 0) {
         return var0;
      } else {
         throw new IllegalStateException(var0.has("message") && var0.get("message").isJsonPrimitive() ? var0.get("message").getAsString() : "отказ сайта");
      }
   }

   private static List<UserSearchClient.Data> method4765(JsonObject var0) {
      ArrayList var1 = new ArrayList();
      JsonArray var2 = var0.getAsJsonArray("items");
      if (var2 == null) {
         return List.of();
      } else {
         for (JsonElement var4 : var2) {
            try {
               JsonObject var5 = var4.getAsJsonObject();
               String var6 = var5.has("name") && !var5.get("name").isJsonNull() ? var5.get("name").getAsString() : "";
               long var7 = var5.has("user_id") && !var5.get("user_id").isJsonNull() ? var5.get("user_id").getAsLong() : 0L;
               if (!var6.isBlank() && var6.length() <= 32) {
                  var1.add(new UserSearchClient.Data(var7, var6));
               }
            } catch (RuntimeException var9) {
            }
         }

         return List.copyOf(var1);
      }
   }

   private static String method4766(long var0, long var2, byte[] var4) {
      try {
         Mac var5 = Mac.getInstance("HmacSHA256");
         var5.init(new SecretKeySpec(Cloud.getText2().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
         var5.update(("nexus-cloud-api-v1\n" + var0 + "\n" + var2 + "\nPOST\n/user/search\n").getBytes(StandardCharsets.UTF_8));
         var5.update(MessageDigest.getInstance("SHA-256").digest(var4));
         return Base64.getUrlEncoder().withoutPadding().encodeToString(var5.doFinal());
      } catch (Exception var6) {
         throw new IllegalStateException("HmacSHA256 недоступен", var6);
      }
   }

   static {
      RegionUtil.method2042(() -> host = null);
   }

   public static final class Data {
      private final long longValue;
      private final String text;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "text=" + this.text + "]";
      }

      public Data(long var1, String var3) {
         this.longValue = var1;
         this.text = var3;
      }

      public long uid() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }
   }
}

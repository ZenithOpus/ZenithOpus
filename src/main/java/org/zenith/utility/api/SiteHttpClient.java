package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import org.zenith.utility.config.SiteSession;

public final class SiteHttpClient {
   private static final Duration duration = Duration.ofSeconds(20L);
   private static final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(10L));
   private static final int CONST_intValue = 4194304;
   private static volatile String host;

   private SiteHttpClient() {
   }

   public static String method2409() {
      return method2410().get(0);
   }

   public static List<String> method2410() {
      return RegionUtil.method2043(RegionUtil.method2037(), host);
   }

   public static CompletableFuture<JsonObject> method2411(String var0, JsonObject var1) {
      String var2 = SiteSession.method2289();
      if (var2.isEmpty()) {
         return CompletableFuture.failedFuture(new SiteHttpClient.Entry2("нет сессии сайта: войдите в лаунчере"));
      } else {
         var1.addProperty("token", var2);
         byte[] var3 = var1.toString().getBytes(StandardCharsets.UTF_8);
         return method2413(var0, var3, method2410(), 0);
      }
   }

   public static CompletableFuture<JsonObject> method2412(String var0, JsonObject var1) {
      String var2 = SiteSession.method2289();
      if (!var2.isEmpty()) {
         var1.addProperty("token", var2);
      }

      return method2413(var0, var1.toString().getBytes(StandardCharsets.UTF_8), method2410(), 0);
   }

   private static CompletableFuture<JsonObject> method2413(String var0, byte[] var1, List<String> var2, int var3) {
      String var4 = (String)var2.get(var3);
      boolean var5 = var3 + 1 >= var2.size();

      HttpRequest var6;
      try {
         var6 = HttpRequest.newBuilder(URI.create(var4 + var0))
            .timeout(duration)
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(BodyPublishers.ofByteArray(var1))
            .build();
      } catch (RuntimeException var8) {
         return var5 ? CompletableFuture.failedFuture(new SiteHttpClient.Entry2(method2424(var8))) : method2413(var0, var1, var2, var3 + 1);
      }

      return httpClient.sendAsync(var6, method2423(4194304)).handle((var6x, var7) -> {
         if (var7 != null) {
            return !var5 ? method2413(var0, var1, var2, var3 + 1) : CompletableFuture.failedFuture(new SiteHttpClient.Entry2(method2424(var7)));
         } else {
            Util18.method4491((HttpResponse<?>)var6x);
            int var8x = var6x.statusCode();
            JsonObject var9 = method2415(var6x.body());
            if (method2414(var8x, var9)) {
               if (!var5) {
                  return method2413(var0, var1, var2, var3 + 1);
               }

               if (var9 == null) {
                  return CompletableFuture.failedFuture(new SiteHttpClient.Entry2("сайт ответил " + var8x, var8x));
               }
            }

            try {
               JsonObject var10 = method2416(var8x, var9);
               host = var4;
               return CompletableFuture.completedFuture(var10);
            } catch (RuntimeException var11) {
               return CompletableFuture.failedFuture(var11);
            }
         }
      }).thenCompose(var0x -> (CompletionStage<JsonObject>)var0x);
   }

   public static boolean method2414(int var0, JsonObject var1) {
      return var1 == null || var0 >= 500 || var0 == 429;
   }

   public static JsonObject method2415(byte[] var0) {
      try {
         JsonElement var1 = JsonParser.parseString(new String(var0, StandardCharsets.UTF_8));
         return var1.isJsonObject() ? var1.getAsJsonObject() : null;
      } catch (RuntimeException var2) {
         return null;
      }
   }

   private static JsonObject method2416(int var0, JsonObject var1) {
      if (var0 == 401 || var0 == 403) {
         SiteSession.method2292();
      }

      int var2;
      try {
         var2 = var1.has("status") && !var1.get("status").isJsonNull() ? var1.get("status").getAsInt() : 0;
      } catch (RuntimeException var5) {
         var2 = 1;
      }

      if (var2 == 0 && var0 / 100 == 2) {
         return var1;
      } else {
         String var3 = var2 != 0 ? "message" : "detail";
         String var4 = var1.has(var3) && var1.get(var3).isJsonPrimitive() ? var1.get(var3).getAsString() : (var2 != 0 ? "отказ сайта" : "сайт ответил " + var0);
         if (var4.equalsIgnoreCase("Unauthorized")) {
            SiteSession.method2292();
         }

         throw new SiteHttpClient.Entry2(var4, var0);
      }
   }

   public static String method2417(JsonObject var0, String var1, String var2) {
      JsonElement var3 = var0 == null ? null : var0.get(var1);
      return var3 != null && var3.isJsonPrimitive() ? var3.getAsString() : var2;
   }

   public static JsonObject method2418(JsonObject var0, String var1) {
      JsonElement var2 = var0 == null ? null : var0.get(var1);
      return var2 != null && var2.isJsonObject() ? var2.getAsJsonObject() : null;
   }

   public static JsonArray method2419(JsonObject var0, String var1) {
      JsonElement var2 = var0 == null ? null : var0.get(var1);
      return var2 != null && var2.isJsonArray() ? var2.getAsJsonArray() : null;
   }

   public static boolean method2420(JsonObject var0, String var1, boolean var2) {
      JsonElement var3 = var0 == null ? null : var0.get(var1);
      if (var3 != null && var3.isJsonPrimitive()) {
         JsonPrimitive var4 = var3.getAsJsonPrimitive();

         try {
            return var4.isNumber() ? var4.getAsDouble() != 0.0 : var4.getAsBoolean();
         } catch (RuntimeException var6) {
            return var2;
         }
      } else {
         return var2;
      }
   }

   public static int method2421(JsonObject var0, String var1, int var2) {
      try {
         return var0 != null && var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsInt() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   public static long method2422(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            return Instant.parse(var0).toEpochMilli();
         } catch (DateTimeParseException var4) {
            try {
               return Instant.parse(var0 + "Z").toEpochMilli();
            } catch (DateTimeParseException var3) {
               return 0L;
            }
         }
      } else {
         return 0L;
      }
   }

   public static BodyHandler<byte[]> method2423(int var0) {
      return var1 -> {
         long var2 = var1.headers().firstValueAsLong("Content-Length").orElse(-1L);
         return new SiteHttpClient.Entry(var2 > var0 ? -1 : var0);
      };
   }

   public static String method2424(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null && var1.getCause() != var1) {
         var1 = var1.getCause();
      }

      String var2 = var1.getMessage();
      return var2 != null && !var2.isBlank() ? var2 : var1.getClass().getSimpleName();
   }

   static {
      RegionUtil.method2042(() -> host = null);
   }

   private static final class Entry implements BodySubscriber<byte[]> {
      private final int intValue;
      private final CompletableFuture<byte[]> completableFuture = new CompletableFuture<>();
      private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      private Subscription subscription;

      public Entry(int var1) {
         this.intValue = var1;
      }

      @Override
      public CompletionStage<byte[]> getBody() {
         return this.completableFuture;
      }

      @Override
      public void onSubscribe(Subscription var1) {
         this.subscription = var1;
         if (this.intValue < 0) {
            var1.cancel();
            this.completableFuture.completeExceptionally(new IOException("ответ больше потолка"));
         } else {
            var1.request(Long.MAX_VALUE);
         }
      }

      public void onNext(List<ByteBuffer> var1) {
         if (!this.completableFuture.isDone()) {
            for (ByteBuffer var3 : var1) {
               int var4 = var3.remaining();
               if ((long)this.byteArrayOutputStream.size() + var4 > this.intValue) {
                  this.subscription.cancel();
                  this.completableFuture.completeExceptionally(new IOException("ответ больше потолка"));
                  return;
               }

               byte[] var5 = new byte[var4];
               var3.get(var5);
               this.byteArrayOutputStream.write(var5, 0, var4);
            }
         }
      }

      @Override
      public void onError(Throwable var1) {
         this.completableFuture.completeExceptionally(var1);
      }

      @Override
      public void onComplete() {
         this.completableFuture.complete(this.byteArrayOutputStream.toByteArray());
      }
   }

   public static final class Entry2 extends RuntimeException {
      private final int intValue;

      public Entry2(String var1) {
         this(var1, 0);
      }

      public Entry2(String var1, int var2) {
         super(var1);
         this.intValue = var2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean method2426() {
         return this.intValue == 401 || this.intValue == 403 || "Unauthorized".equalsIgnoreCase(this.getMessage());
      }
   }
}

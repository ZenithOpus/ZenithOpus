package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.network.Cloud;

public final class StoreApi {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Store");
   private static final Duration duration = Duration.ofSeconds(30L);
   private static final long CONST_longValue = 33554432L;
   private static final Duration duration2 = Duration.ofSeconds(130L);
   public static final int CONST_intValue = 524288;
   public static final int CONST_intValue2 = 32;
   public static final String text = "config";
   public static final String text2 = "script";
   public static final String text3 = "PRIVATE";
   public static final String text4 = "FRIENDS";
   public static final String text5 = "PUBLIC";
   public static final String text6 = "CODE";
   private final Cloud cloud;
   private final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(10L));
   private static final int CONST_intValue3 = 1024;
   private static final LinkedHashMap<UUID, StoreApi.Data5> linkedHashMap = new LinkedHashMap<UUID, StoreApi.Data5>(64, 0.75F, false) {
      @Override
      public boolean removeEldestEntry(java.util.Map.Entry<UUID, StoreApi.Data5> var1) {
         return this.size() > 1024;
      }
   };

   public StoreApi(Cloud var1) {
      this.cloud = var1;
   }

   private static void method1958(UUID var0, StoreApi.Data5 var1) {
      synchronized (linkedHashMap) {
         linkedHashMap.remove(var0);
         linkedHashMap.put(var0, var1);
      }
   }

   private static void method1959(UUID var0) {
      synchronized (linkedHashMap) {
         linkedHashMap.remove(var0);
      }
   }

   public static String reason(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null && var1.getCause() != var1) {
         var1 = var1.getCause();
      }

      String var2 = var1.getMessage();
      return var2 != null && !var2.isBlank() ? var2 : var1.getClass().getSimpleName();
   }

   public static StoreApi.Data5 method1960(UUID var0) {
      if (var0 == null) {
         return StoreApi.Data5.INSTANCE;
      } else {
         synchronized (linkedHashMap) {
            return linkedHashMap.getOrDefault(var0, StoreApi.Data5.INSTANCE);
         }
      }
   }

   public CompletableFuture<StoreApi.Data2> method1961(UUID var1, int var2) {
      return this.method1989("POST", "market/" + var1 + "?price=" + Math.max(0, var2), null).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<StoreApi.Data2> method1962(UUID var1) {
      return this.method1989("DELETE", "market/" + var1, null).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<List<StoreApi.Data4>> method1963() {
      return this.method1989("GET", "roster", null).thenApply(var0 -> {
         ArrayList var1 = new ArrayList();
         JsonArray var2 = var0.getAsJsonArray("online");
         if (var2 != null) {
            for (JsonElement var4 : var2) {
               method2001(var1, var4, StoreApi.Data4::method2005);
            }
         }

         return var1;
      });
   }

   public CompletableFuture<List<StoreApi.Data3>> method1964(long var1, int var3) {
      return this.method1989("GET", "dm/" + var1 + "?limit=" + var3, null).thenApply(var0 -> {
         ArrayList var1x = new ArrayList();
         JsonArray var2 = var0.getAsJsonArray("messages");
         if (var2 != null) {
            for (JsonElement var4 : var2) {
               method2001(var1x, var4, StoreApi.Data3::method2004);
            }
         }

         return var1x;
      });
   }

   public CompletableFuture<List<StoreApi.Data3>> method1965(int var1) {
      return this.method1989("GET", "chat?limit=" + var1, null).thenApply(var0 -> {
         ArrayList var1x = new ArrayList();
         JsonArray var2 = var0.getAsJsonArray("messages");
         if (var2 != null) {
            for (JsonElement var4 : var2) {
               method2001(var1x, var4, StoreApi.Data3::method2004);
            }
         }

         return var1x;
      });
   }

   public CompletableFuture<StoreApi.Data3> method1966(long var1, String var3) {
      return this.method1989("POST", "dm/" + var1, var3.getBytes(StandardCharsets.UTF_8)).thenApply(StoreApi.Data3::method2004);
   }

   public CompletableFuture<Void> method1967(long var1, long var3) {
      return this.method1989("DELETE", "dm/" + var1 + "?id=" + var3, null).thenApply(var0 -> null);
   }

   public CompletableFuture<List<StoreApi.Data2>> method1968(String var1) {
      return this.method1989("GET", "list?kind=" + encode(var1), null).thenApply(StoreApi::method1987);
   }

   public CompletableFuture<List<StoreApi.Data2>> method1969(String var1, String var2) {
      return this.method1989("GET", "catalog?scope=" + encode(var1) + "&kind=" + encode(var2), null).thenApply(StoreApi::method1987);
   }

   public CompletableFuture<byte[]> method1970(UUID var1) {
      return this.method1990("GET", "content/" + var1, null);
   }

   public CompletableFuture<byte[]> method1971(UUID var1) {
      return this.method1990("GET", "preview/" + var1, null);
   }

   public CompletableFuture<StoreApi.Data2> method1972(String var1, String var2, String var3, String var4, String var5, byte[] var6, UUID var7, long var8) {
      return this.method1973(var1, var2, var3, var4, var5, var6, var7, var8, this.cloud.method7911());
   }

   public CompletableFuture<StoreApi.Data2> method1973(
      String var1, String var2, String var3, String var4, String var5, byte[] var6, UUID var7, long var8, String var10
   ) {
      JsonObject var11 = new JsonObject();
      var11.addProperty("kind", var1);
      var11.addProperty("name", var2);
      var11.addProperty("fileName", var3);
      var11.addProperty("description", var4 == null ? "" : var4);
      var11.addProperty("visibility", var5);
      var11.addProperty("server", var10 == null ? "" : var10);
      var11.addProperty("ownerName", method1995());
      if (var7 != null) {
         var11.addProperty("replace", var7.toString());
         var11.addProperty("version", var8);
      }

      byte[] var12 = var11.toString().getBytes(StandardCharsets.UTF_8);
      ByteArrayOutputStream var13 = new ByteArrayOutputStream(var12.length + var6.length + 4);
      var13.write(var12.length >>> 24);
      var13.write(var12.length >>> 16);
      var13.write(var12.length >>> 8);
      var13.write(var12.length);
      var13.writeBytes(var12);
      var13.writeBytes(var6);
      return this.method1989("POST", "publish", var13.toByteArray()).thenApply(var0 -> StoreApi.Data2.method2003(var0));
   }

   public CompletableFuture<StoreApi.Data2> method1974(UUID var1, String var2, String var3, String var4) {
      JsonObject var5 = new JsonObject();
      if (var2 != null) {
         var5.addProperty("name", var2);
      }

      if (var3 != null) {
         var5.addProperty("description", var3);
      }

      if (var4 != null) {
         var5.addProperty("visibility", var4);
      }

      return this.method1989("POST", "edit/" + var1, var5.toString().getBytes(StandardCharsets.UTF_8)).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<Void> method1975(UUID var1) {
      return this.method1989("DELETE", "entry/" + var1, null).thenApply(var0 -> null);
   }

   public CompletableFuture<StoreApi.Data2> method1976(UUID var1, byte[] var2) {
      return this.method1989("POST", "preview/" + var1, var2).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<StoreApi.Data2> method1977(UUID var1) {
      return this.method1989("DELETE", "preview/" + var1, null).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<int[]> method1978(UUID var1) {
      return this.method1989("POST", "like/" + var1, null).thenApply(var0 -> new int[]{method1999(var0, "likes", 0), method2000(var0, "liked") ? 1 : 0});
   }

   public CompletableFuture<List<StoreApi.Data>> method1979(UUID var1, int var2) {
      return this.method1989("POST", "codes/" + var1 + "?count=" + var2, null).thenApply(StoreApi::method1988);
   }

   public CompletableFuture<List<StoreApi.Data>> method1980(UUID var1) {
      return this.method1989("GET", "codes/" + var1, null).thenApply(StoreApi::method1988);
   }

   public CompletableFuture<Void> method1981(UUID var1, UUID var2) {
      return this.method1989("DELETE", "codes/" + var1 + "/" + var2, null).thenApply(var0 -> null);
   }

   public CompletableFuture<Map<String, byte[]>> method1982(List<String> var1) {
      JsonObject var2 = new JsonObject();
      JsonArray var3 = new JsonArray();
      var1.forEach(var3::add);
      var2.add("models", var3);
      return this.method1989("POST", "wardrobe-keys", var2.toString().getBytes(StandardCharsets.UTF_8)).thenApply(var0 -> {
         HashMap var1x = new HashMap();
         JsonObject var2x = var0.getAsJsonObject("keys");
         if (var2x != null) {
            for (Map.Entry var4 : var2x.entrySet()) {
               try {
                  var1x.put((String)var4.getKey(), Base64.getDecoder().decode(((JsonElement)var4.getValue()).getAsString()));
               } catch (RuntimeException var6) {
                  logger.debug("Склад: ключ гардероба {} пропущен: {}", var4.getKey(), var6.toString());
               }
            }
         }

         return var1x;
      });
   }

   public String method1983() {
      String var1 = this.method1993();
      return var1.substring(0, var1.length() - "/store/".length());
   }

   public CompletableFuture<String> method1984(byte[] var1, int var2, int var3) {
      if (var1 != null && var1.length != 0) {
         if (var1.length > 524288) {
            return CompletableFuture.failedFuture(new StoreApi.Entry(400, "картинка капчи больше 512 КБ"));
         } else {
            JsonObject var4 = new JsonObject();
            var4.addProperty("image", Base64.getEncoder().encodeToString(var1));
            var4.addProperty("min_length", method1985(var2));
            var4.addProperty("max_length", method1985(var3));
            return this.method1991("POST", "captcha", var4.toString().getBytes(StandardCharsets.UTF_8), duration2).thenApply(var0 -> {
               JsonObject var1x = JsonParser.parseString(new String(var0, StandardCharsets.UTF_8)).getAsJsonObject();
               return method1997(var1x, "answer", "");
            });
         }
      } else {
         return CompletableFuture.failedFuture(new StoreApi.Entry(400, "картинка капчи пуста"));
      }
   }

   private static int method1985(int var0) {
      return Math.max(0, Math.min(32, var0));
   }

   public CompletableFuture<StoreApi.Data2> method1986(String var1) {
      return this.method1989("POST", "redeem?code=" + encode(var1), null).thenApply(StoreApi.Data2::method2003);
   }

   private static List<StoreApi.Data2> method1987(JsonObject var0) {
      ArrayList<StoreApi.Data2> var1 = new ArrayList<>();
      JsonArray var2 = var0.getAsJsonArray("entries");
      if (var2 != null) {
         for (JsonElement var4 : var2) {
            method2001(var1, var4, StoreApi.Data2::method2003);
         }
      }

      return var1;
   }

   private static List<StoreApi.Data> method1988(JsonObject var0) {
      ArrayList<StoreApi.Data> var1 = new ArrayList<>();
      JsonArray var2 = var0.getAsJsonArray("codes");
      if (var2 != null) {
         for (JsonElement var4 : var2) {
            method2001(var1, var4, StoreApi.Data::method2002);
         }
      }

      return var1;
   }

   private CompletableFuture<JsonObject> method1989(String var1, String var2, byte[] var3) {
      return this.method1990(var1, var2, var3).thenApply(var0 -> JsonParser.parseString(new String(var0, StandardCharsets.UTF_8)).getAsJsonObject());
   }

   private CompletableFuture<byte[]> method1990(String var1, String var2, byte[] var3) {
      return this.method1991(var1, var2, var3, duration);
   }

   private CompletableFuture<byte[]> method1991(String var1, String var2, byte[] var3, Duration var4) {
      String var5;
      try {
         var5 = this.method1993();
      } catch (RuntimeException var16) {
         return CompletableFuture.failedFuture(var16);
      }

      String var6 = var5 + var2;
      String var7 = "/store/" + var2;
      byte[] var8 = var3 == null ? new byte[0] : var3;
      long var9 = Util18.method4489();
      long var11 = accountUid();
      if (var11 <= 0L) {
         return CompletableFuture.failedFuture(new StoreApi.Entry(401, "аккаунт ещё не готов"));
      } else {
         HttpRequest var13;
         String var14;
         try {
            Builder var15 = HttpRequest.newBuilder(URI.create(var6))
               .timeout(var4)
               .header("X-Nexus-Uid", Long.toString(var11))
               .header("X-Nexus-Time", Long.toString(var9))
               .header("X-Nexus-Sign", this.method1996(var11, var9, var1, var7, var8));
            var14 = CloudTicket.method1373();
            if (var14 != null) {
               var15.header("Authorization", "Nexus " + var14);
            }

            if (var3 == null) {
               var15.method(var1, BodyPublishers.noBody());
            } else {
               var15.header("Content-Type", "application/octet-stream").method(var1, BodyPublishers.ofByteArray(var3));
            }

            var13 = var15.build();
         } catch (RuntimeException var17) {
            return CompletableFuture.failedFuture(var17);
         }

         BodyHandler<byte[]> var18 = var0 -> new StoreApi.Entry2(33554432L);
         return this.httpClient.sendAsync(var13, var18).thenApply(var1x -> {
            Util18.method4491((HttpResponse<?>)var1x);
            if (var1x.statusCode() / 100 == 2) {
               return (byte[])var1x.body();
            } else {
               if (var14 != null && (var1x.statusCode() == 401 || var1x.statusCode() == 403)) {
                  CloudTicket.invalidate();
               }

               throw new StoreApi.Entry(var1x.statusCode(), method1992((byte[])var1x.body(), var1x.statusCode()));
            }
         });
      }
   }

   private static String method1992(byte[] var0, int var1) {
      try {
         JsonObject var2 = JsonParser.parseString(new String(var0, StandardCharsets.UTF_8)).getAsJsonObject();
         if (var2.has("error")) {
            return var2.get("error").getAsString();
         }
      } catch (RuntimeException var3) {
      }

      return "узел ответил " + var1;
   }

   private String method1993() {
      String var1 = method1994(this.cloud.method7870());
      if (var1.isEmpty()) {
         throw new StoreApi.Entry(503, "адрес облака не задан");
      } else {
         return var1 + "/store/";
      }
   }

   public static String method1994(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.strip();
         String var2 = var1.startsWith("wss://") ? "https://" + var1.substring(6) : (var1.startsWith("ws://") ? "http://" + var1.substring(5) : var1);
         int var3 = var2.lastIndexOf("/ws");
         String var4 = var3 < 0 ? var2 : var2.substring(0, var3);

         while (var4.endsWith("/")) {
            var4 = var4.substring(0, var4.length() - 1);
         }

         return var4;
      } else {
         return "";
      }
   }

   private static long accountUid() {
      return Cloud.accountUid();
   }

   private static String method1995() {
      return Cloud.accountName();
   }

   private String method1996(long var1, long var3, String var5, String var6, byte[] var7) {
      try {
         Mac var8 = Mac.getInstance("HmacSHA256");
         var8.init(new SecretKeySpec(Cloud.getText2().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
         var8.update(("nexus-cloud-api-v1\n" + var1 + "\n" + var3 + "\n" + var5 + "\n" + var6 + "\n").getBytes(StandardCharsets.UTF_8));
         var8.update(MessageDigest.getInstance("SHA-256").digest(var7));
         return Base64.getUrlEncoder().withoutPadding().encodeToString(var8.doFinal());
      } catch (Exception var9) {
         throw new IllegalStateException("HmacSHA256 недоступен", var9);
      }
   }

   private static String method1997(JsonObject var0, String var1, String var2) {
      try {
         return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsString() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static long method1998(JsonObject var0, String var1, long var2) {
      try {
         return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsLong() : var2;
      } catch (RuntimeException var5) {
         return var2;
      }
   }

   private static int method1999(JsonObject var0, String var1, int var2) {
      try {
         return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsInt() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static boolean method2000(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && !var0.get(var1).isJsonNull() && var0.get(var1).getAsBoolean();
      } catch (RuntimeException var3) {
         return false;
      }
   }

   private static <T> void method2001(List<T> var0, JsonElement var1, Function<JsonObject, T> var2) {
      try {
         if (var1 != null && var1.isJsonObject()) {
            var0.add(var2.apply(var1.getAsJsonObject()));
         }
      } catch (RuntimeException var4) {
         logger.debug("Склад: запись пропущена: {}", var4.toString());
      }
   }

   private static String encode(String var0) {
      return URLEncoder.encode(var0 == null ? "" : var0, StandardCharsets.UTF_8);
   }

   public static final class Data {
      private final UUID uUID;
      private final String text;
      private final String text2;
      private final long longValue;
      private final Long longValue2;

      public UUID uUID() {
         return this.uUID;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public Long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, text, text2, longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "uUID=" + this.uUID + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data(UUID var1, String var2, String var3, long var4, Long var6) {
         this.uUID = var1;
         this.text = var2;
         this.text2 = var3;
         this.longValue = var4;
         this.longValue2 = var6;
      }

      public static StoreApi.Data method2002(JsonObject var0) {
         long var1 = StoreApi.method1998(var0, "redeemedAt", -1L);
         return new StoreApi.Data(
            UUID.fromString(var0.get("id").getAsString()),
            StoreApi.method1997(var0, "code", ""),
            StoreApi.method1997(var0, "status", "ACTIVE"),
            StoreApi.method1998(var0, "createdAt", 0L),
            var1 < 0L ? null : var1
         );
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public Long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data2 {
      private final UUID uUID;
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String text5;
      private final long longValue;
      private final String text6;
      private final String text7;
      private final long longValue2;
      private final long longValue3;
      private final long longValue4;
      private final long longValue5;
      private final String text8;
      private final boolean flag;
      private final boolean flag2;
      private final int intValue;
      private final boolean flag3;
      private final int intValue2;

      public UUID uUID() {
         return this.uUID;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      public String text5() {
         return this.text5;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text6() {
         return this.text6;
      }

      public String text7() {
         return this.text7;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public long longValue4() {
         return this.longValue4;
      }

      public long longValue5() {
         return this.longValue5;
      }

      public String text8() {
         return this.text8;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text6, other.text6) && java.util.Objects.equals(this.text7, other.text7) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.longValue4, other.longValue4) && java.util.Objects.equals(this.longValue5, other.longValue5) && java.util.Objects.equals(this.text8, other.text8) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, text, text2, text3, text4, text5, longValue, text6, text7, longValue2, longValue3, longValue4, longValue5, text8, flag, flag2, intValue, flag3, intValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "text5=" + this.text5 + ", " + "longValue=" + this.longValue + ", " + "text6=" + this.text6 + ", " + "text7=" + this.text7 + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + ", " + "longValue4=" + this.longValue4 + ", " + "longValue5=" + this.longValue5 + ", " + "text8=" + this.text8 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "intValue=" + this.intValue + ", " + "flag3=" + this.flag3 + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data2(
         UUID var1,
         String var2,
         String var3,
         String var4,
         String var5,
         String var6,
         long var7,
         String var9,
         String var10,
         long var11,
         long var13,
         long var15,
         long var17,
         String var19,
         boolean var20,
         boolean var21,
         int var22,
         boolean var23,
         int var24
      ) {
         this.uUID = var1;
         this.text = var2;
         this.text2 = var3;
         this.text3 = var4;
         this.text4 = var5;
         this.text5 = var6;
         this.longValue = var7;
         this.text6 = var9;
         this.text7 = var10;
         this.longValue2 = var11;
         this.longValue3 = var13;
         this.longValue4 = var15;
         this.longValue5 = var17;
         this.text8 = var19;
         this.flag = var20;
         this.flag2 = var21;
         this.intValue = var22;
         this.flag3 = var23;
         this.intValue2 = var24;
      }

      public static StoreApi.Data2 method2003(JsonObject var0) {
         UUID var1 = UUID.fromString(var0.get("id").getAsString());
         if (var0.has("market") && var0.get("market").isJsonObject()) {
            JsonObject var2 = var0.getAsJsonObject("market");
            StoreApi.method1958(
               var1,
               new StoreApi.Data5(
                  StoreApi.method1997(var2, "status", ""),
                  StoreApi.method1999(var2, "price", 0),
                  StoreApi.method2000(var2, "sealed"),
                  StoreApi.method1997(var2, "reason", "")
               )
            );
         } else {
            StoreApi.method1959(var1);
         }

         return new StoreApi.Data2(
            var1,
            StoreApi.method1997(var0, "kind", "config"),
            StoreApi.method1997(var0, "name", ""),
            StoreApi.method1997(var0, "fileName", ""),
            StoreApi.method1997(var0, "description", ""),
            StoreApi.method1997(var0, "server", ""),
            StoreApi.method1998(var0, "sizeBytes", 0L),
            StoreApi.method1997(var0, "sha256", ""),
            StoreApi.method1997(var0, "visibility", "PRIVATE"),
            StoreApi.method1998(var0, "version", 1L),
            StoreApi.method1998(var0, "createdAt", 0L),
            StoreApi.method1998(var0, "updatedAt", 0L),
            StoreApi.method1998(var0, "ownerUid", 0L),
            StoreApi.method1997(var0, "ownerName", ""),
            StoreApi.method2000(var0, "mine"),
            StoreApi.method2000(var0, "hasPreview"),
            StoreApi.method1999(var0, "likes", 0),
            StoreApi.method2000(var0, "liked"),
            StoreApi.method1999(var0, "codes", 0)
         );
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public String kind() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public String fileName() {
         return this.text3;
      }

      public String description() {
         return this.text4;
      }

      public String server() {
         return this.text5;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText6() {
         return this.text6;
      }

      public String getText7() {
         return this.text7;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public long getLongValue4() {
         return this.longValue4;
      }

      public long getLongValue5() {
         return this.longValue5;
      }

      public String getText8() {
         return this.text8;
      }

      public boolean mine() {
         return this.flag;
      }

      public boolean hasPreview() {
         return this.flag2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data3 {
      private final long longValue;
      private final long longValue2;
      private final String text;
      private final long longValue3;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, text, longValue3, text2);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "text=" + this.text + ", " + "longValue3=" + this.longValue3 + ", " + "text2=" + this.text2 + "]";
      }

      public Data3(long var1, long var3, String var5, long var6, String var8) {
         this.longValue = var1;
         this.longValue2 = var3;
         this.text = var5;
         this.longValue3 = var6;
         this.text2 = var8;
      }

      public static StoreApi.Data3 method2004(JsonObject var0) {
         return new StoreApi.Data3(
            StoreApi.method1998(var0, "id", 0L),
            StoreApi.method1998(var0, "from", 0L),
            StoreApi.method1997(var0, "fromName", ""),
            StoreApi.method1998(var0, "time", 0L),
            StoreApi.method1997(var0, "text", "")
         );
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String getText() {
         return this.text;
      }

      public long time() {
         return this.longValue3;
      }

      public String text() {
         return this.text2;
      }
   }

   public static final class Data4 {
      private final long longValue;
      private final String text;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2);
      }

      @Override
      public String toString() {
         return "Data4[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data4(long var1, String var3, String var4) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
      }

      public static StoreApi.Data4 method2005(JsonObject var0) {
         return new StoreApi.Data4(StoreApi.method1998(var0, "uid", 0L), StoreApi.method1997(var0, "username", ""), StoreApi.method1997(var0, "mcName", ""));
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   public static final class Data5 {
      private final String text;
      private final int intValue;
      private final boolean flag;
      private final String text2;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, flag, text2);
      }

      @Override
      public String toString() {
         return "Data5[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + ", " + "text2=" + this.text2 + "]";
      }
      public static final StoreApi.Data5 INSTANCE = new StoreApi.Data5("", 0, false, "");

      public Data5(String var1, int var2, boolean var3, String var4) {
         this.text = var1;
         this.intValue = var2;
         this.flag = var3;
         this.text2 = var4;
      }

      public boolean pending() {
         return "PENDING".equals(this.text);
      }

      public boolean method2007() {
         return "APPROVED".equals(this.text);
      }

      public boolean method2008() {
         return "REJECTED".equals(this.text);
      }

      public boolean method2009() {
         return this.intValue > 0;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public String getText2() {
         return this.text2;
      }
   }

   public static final class Entry extends RuntimeException {
      private final int intValue;

      public Entry(int var1, String var2) {
         super(var2);
         this.intValue = var1;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Entry2 implements BodySubscriber<byte[]> {
      private final BodySubscriber<byte[]> bodySubscriber = BodySubscribers.ofByteArray();
      private final long longValue;
      private Subscription subscription;
      private long longValue2;
      private boolean flag;

      public Entry2(long var1) {
         this.longValue = var1;
      }

      @Override
      public CompletionStage<byte[]> getBody() {
         return this.bodySubscriber.getBody();
      }

      @Override
      public void onSubscribe(Subscription var1) {
         this.subscription = var1;
         this.bodySubscriber.onSubscribe(var1);
      }

      public void onNext(List<ByteBuffer> var1) {
         if (!this.flag) {
            for (ByteBuffer var3 : var1) {
               this.longValue2 = this.longValue2 + var3.remaining();
            }

            if (this.longValue2 > this.longValue) {
               this.flag = true;
               this.subscription.cancel();
               this.bodySubscriber.onError(new StoreApi.Entry(413, "ответ узла больше " + (this.longValue >> 20) + " МБ"));
            } else {
               this.bodySubscriber.onNext(var1);
            }
         }
      }

      @Override
      public void onError(Throwable var1) {
         if (!this.flag) {
            this.bodySubscriber.onError(var1);
         }
      }

      @Override
      public void onComplete() {
         if (!this.flag) {
            this.bodySubscriber.onComplete();
         }
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class MicrosoftAuth {
   public static final String text = "54fd49e4-2103-4044-9603-2b028c814ec3";
   private static final String text2 = "XboxLive.signin XboxLive.offline_access";
   private static final Duration duration = Duration.ofSeconds(30L);
   private static final String text3 = "Zenith/3.5";
   private static final Executor executor = Executors.newCachedThreadPool(var0 -> {
      Thread var1 = new Thread(var0, "Zenith Microsoft Auth");
      var1.setDaemon(true);
      return var1;
   });
   private static final HttpClient httpClient = HttpClients.method2671(duration, Redirect.NORMAL);

   private MicrosoftAuth() {
   }

   public static CompletableFuture<MicrosoftAuth.Data> method3056() {
      String var0 = "client_id=54fd49e4-2103-4044-9603-2b028c814ec3&scope=" + encode("XboxLive.signin XboxLive.offline_access");
      return method3067("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode", "application/x-www-form-urlencoded", var0)
         .thenApply(
            var0x -> new MicrosoftAuth.Data(
               var0x.get("user_code").getAsString(),
               var0x.get("verification_uri").getAsString(),
               var0x.get("device_code").getAsString(),
               var0x.has("interval") ? var0x.get("interval").getAsInt() : 5,
               var0x.has("expires_in") ? var0x.get("expires_in").getAsInt() : 900
            )
         );
   }

   public static CompletableFuture<MicrosoftAuth.Data2> method3057(MicrosoftAuth.Data var0, Consumer<String> var1) {
      return method3058(var0, var1, () -> false);
   }

   public static CompletableFuture<MicrosoftAuth.Data2> method3058(MicrosoftAuth.Data var0, Consumer<String> var1, BooleanSupplier var2) {
      return CompletableFuture.<String[]>supplyAsync(() -> {
         long var2x = System.currentTimeMillis() + var0.getIntValue2() * 1000L;
         long var4 = Math.max(1, var0.getIntValue()) * 1000L;

         while (System.currentTimeMillis() < var2x) {
            if (var2.getAsBoolean()) {
               throw new CancellationException("Login cancelled");
            }

            method3070(var4);
            if (var2.getAsBoolean()) {
               throw new CancellationException("Login cancelled");
            }

            JsonObject var6 = method3066(var0.getText3());
            if (var6 != null) {
               if (var6.has("access_token")) {
                  return new String[]{var6.get("access_token").getAsString(), var6.has("refresh_token") ? var6.get("refresh_token").getAsString() : ""};
               }

               String var7 = var6.has("error") ? var6.get("error").getAsString() : "";
               switch (var7) {
                  case "authorization_pending":
                     break;
                  case "slow_down":
                     var4 += 5000L;
                     break;
                  case "authorization_declined":
                     throw new IllegalStateException("Login declined");
                  case "expired_token":
                     throw new IllegalStateException("Code expired");
                  default:
                     throw new IllegalStateException("Microsoft error: " + var7);
               }
            }
         }

         throw new IllegalStateException("Code expired");
      }, executor).thenCompose(var1x -> {
         var1.accept("Signing in to Xbox Live...");
         return method3060(var1x[0], var1x[1], var1);
      });
   }

   public static CompletableFuture<MicrosoftAuth.Data2> method3059(String var0, Consumer<String> var1) {
      String var2 = "client_id=54fd49e4-2103-4044-9603-2b028c814ec3&refresh_token="
         + encode(var0)
         + "&grant_type=refresh_token&scope="
         + encode("XboxLive.signin XboxLive.offline_access");
      return method3067("https://login.microsoftonline.com/consumers/oauth2/v2.0/token", "application/x-www-form-urlencoded", var2)
         .thenCompose(
            var2x -> method3060(var2x.get("access_token").getAsString(), var2x.has("refresh_token") ? var2x.get("refresh_token").getAsString() : var0, var1)
         );
   }

   private static CompletableFuture<MicrosoftAuth.Data2> method3060(String var0, String var1, Consumer<String> var2) {
      return method3061(var0).thenCompose(var1x -> {
         var2.accept("Requesting XSTS token...");
         return method3062(var1x[0]).thenApply(var0xx -> new String[]{var0xx[0], var0xx[1]});
      }).thenCompose(var1x -> {
         var2.accept("Authenticating with Minecraft...");
         return method3064(var1x[0], var1x[1]);
      }).thenCompose(var2x -> {
         var2.accept("Loading profile...");
         return method3065(var2x).thenApply(var2xx -> new MicrosoftAuth.Data2(var2xx[0], method3069(var2xx[1]), var2x, var1));
      });
   }

   private static CompletableFuture<String[]> method3061(String var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("AuthMethod", "RPS");
      var1.addProperty("SiteName", "user.auth.xboxlive.com");
      var1.addProperty("RpsTicket", "d=" + var0);
      JsonObject var2 = new JsonObject();
      var2.add("Properties", var1);
      var2.addProperty("RelyingParty", "http://auth.xboxlive.com");
      var2.addProperty("TokenType", "JWT");
      return method3067("https://user.auth.xboxlive.com/user/authenticate", "application/json", var2.toString()).thenApply(MicrosoftAuth::method3063);
   }

   private static CompletableFuture<String[]> method3062(String var0) {
      JsonArray var1 = new JsonArray();
      var1.add(var0);
      JsonObject var2 = new JsonObject();
      var2.add("UserTokens", var1);
      var2.addProperty("SandboxId", "RETAIL");
      JsonObject var3 = new JsonObject();
      var3.add("Properties", var2);
      var3.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
      var3.addProperty("TokenType", "JWT");
      return method3067("https://xsts.auth.xboxlive.com/xsts/authorize", "application/json", var3.toString()).thenApply(MicrosoftAuth::method3063);
   }

   private static String[] method3063(JsonObject var0) {
      String var1 = var0.get("Token").getAsString();
      String var2 = var0.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString();
      return new String[]{var1, var2};
   }

   private static CompletableFuture<String> method3064(String var0, String var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("identityToken", "XBL3.0 x=" + var1 + ";" + var0);
      return method3067("https://api.minecraftservices.com/authentication/login_with_xbox", "application/json", var2.toString())
         .thenApply(var0x -> var0x.get("access_token").getAsString());
   }

   private static CompletableFuture<String[]> method3065(String var0) {
      HttpRequest var1 = HttpRequest.newBuilder()
         .uri(URI.create("https://api.minecraftservices.com/minecraft/profile"))
         .header("Authorization", "Bearer " + var0)
         .header("Accept", "application/json")
         .header("User-Agent", "Zenith/3.5")
         .timeout(duration)
         .GET()
         .build();
      return httpClient.sendAsync(var1, BodyHandlers.ofString()).thenApplyAsync(var0x -> {
         if (var0x.statusCode() == 404) {
            throw new IllegalStateException("This account does not own Minecraft");
         } else {
            JsonObject var1x = method3068((HttpResponse<String>)var0x, "profile");
            return new String[]{var1x.get("name").getAsString(), var1x.get("id").getAsString()};
         }
      }, executor);
   }

   private static JsonObject method3066(String var0) {
      String var1 = "grant_type=urn:ietf:params:oauth:grant-type:device_code&client_id=54fd49e4-2103-4044-9603-2b028c814ec3&device_code=" + encode(var0);
      HttpRequest var2 = HttpRequest.newBuilder()
         .uri(URI.create("https://login.microsoftonline.com/consumers/oauth2/v2.0/token"))
         .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Accept", "application/json")
         .header("User-Agent", "Zenith/3.5")
         .timeout(duration)
         .POST(BodyPublishers.ofString(var1))
         .build();

      try {
         HttpResponse var3 = httpClient.send(var2, BodyHandlers.ofString());
         return JsonParser.parseString((String)var3.body()).getAsJsonObject();
      } catch (Exception var4) {
         return null;
      }
   }

   private static CompletableFuture<JsonObject> method3067(String var0, String var1, String var2) {
      HttpRequest var3 = HttpRequest.newBuilder()
         .uri(URI.create(var0))
         .header("Content-Type", var1)
         .header("Accept", "application/json")
         .header("User-Agent", "Zenith/3.5")
         .timeout(duration)
         .POST(BodyPublishers.ofString(var2))
         .build();
      return httpClient.sendAsync(var3, BodyHandlers.ofString()).thenApplyAsync(var1x -> method3068((HttpResponse<String>)var1x, var0), executor);
   }

   private static JsonObject method3068(HttpResponse<String> var0, String var1) {
      if (var0.statusCode() / 100 != 2) {
         throw new IllegalStateException("HTTP " + var0.statusCode() + " from " + var1);
      } else {
         return JsonParser.parseString((String)var0.body()).getAsJsonObject();
      }
   }

   private static UUID method3069(String var0) {
      String var1 = var0.length() == 32
         ? var0.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")
         : var0;
      return UUID.fromString(var1);
   }

   private static String encode(String var0) {
      return URLEncoder.encode(var0, StandardCharsets.UTF_8);
   }

   private static void method3070(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
         throw new IllegalStateException("Interrupted");
      }
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final String text3;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(String var1, String var2, String var3, int var4, int var5) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
         this.intValue = var4;
         this.intValue2 = var5;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final UUID uUID;
      private final String text2;
      private final String text3;

      public String text() {
         return this.text;
      }

      public UUID uUID() {
         return this.uUID;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, uUID, text2, text3);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "uUID=" + this.uUID + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      public Data2(String var1, UUID var2, String var3, String var4) {
         this.text = var1;
         this.uUID = var2;
         this.text2 = var3;
         this.text3 = var4;
      }

      public String name() {
         return this.text;
      }

      public UUID uuid() {
         return this.uUID;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }
}

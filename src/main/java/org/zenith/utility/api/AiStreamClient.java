package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.zenith.utility.misc.ShownProvider;

public final class AiStreamClient {
   private static final Set<String> items = Set.of("claude-opus-5-5", "claude-sonnet-5-5", "claude-fable-5-1", "claude-opus-5");
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15L)).followRedirects(Redirect.NORMAL).build();

   private AiStreamClient() {
   }

   public static String method4739(
      ShownProvider var0, String var1, String var2, String var3, String var4, List<AiStreamClient.Data> var5, Consumer<String> var6, BooleanSupplier var7
   ) throws IOException, InterruptedException {
      HttpRequest var8 = switch (var0.getType()) {
         case ANTHROPIC -> method4744(var1, var2, var3, var4, var5);
         case GEMINI -> method4746(var1, var2, var3, var4, var5);
         case OPENAI -> method4745(var0, var1, var2, var3, var4, var5);
      };
      HttpResponse var9 = method4740(httpClient.sendAsync(var8, BodyHandlers.ofInputStream()), var7);
      StringBuilder var10 = new StringBuilder();
      if (var9 == null) {
         return var10.toString();
      } else {
         AtomicBoolean var11 = new AtomicBoolean();

         try (
            InputStream var12 = (InputStream)var9.body();
            BufferedReader var13 = new BufferedReader(new InputStreamReader(var12, StandardCharsets.UTF_8));
         ) {
            method4742(var12, var7, var11);
            if (var9.statusCode() / 100 != 2) {
               StringBuilder var41 = new StringBuilder();

               String var42;
               while ((var42 = method4743(var13, var7)) != null && var41.length() < 8000) {
                  var41.append(var42).append('\n');
               }

               throw new AiStreamClient.Entry(method4747(var9.statusCode(), var41.toString()));
            }

            String var15 = "";

            String var14;
            while ((var14 = method4743(var13, var7)) != null && !var7.getAsBoolean()) {
               if (var14.startsWith("data:")) {
                  String var16 = var14.substring(5).trim();
                  if (!var16.isEmpty() && !var16.equals("[DONE]")) {
                     JsonObject var17;
                     try {
                        var17 = JsonParser.parseString(var16).getAsJsonObject();
                     } catch (RuntimeException var37) {
                        continue;
                     }

                     String var47_1 = switch (var0.getType()) {
                        case ANTHROPIC -> {
                           String var44 = method4749(var17, "type");
                           if (var44.equals("error")) {
                              throw new AiStreamClient.Entry(method4748(var17));
                           }

                           if (var44.equals("message_delta") && var17.has("delta")) {
                              var15 = method4749(var17.getAsJsonObject("delta"), "stop_reason");
                           }

                           if (var44.equals("content_block_delta") && var17.has("delta")) {
                              JsonObject var46 = var17.getAsJsonObject("delta");
                              yield method4749(var46, "type").equals("text_delta") ? method4749(var46, "text") : "";
                           } else {
                              yield "";
                           }
                        }
                        case GEMINI -> {
                           if (var17.has("error")) {
                              throw new AiStreamClient.Entry(method4748(var17));
                           }

                           StringBuilder var43 = new StringBuilder();

                           for (JsonElement var22 : var17.has("candidates") ? var17.getAsJsonArray("candidates") : new JsonArray()) {
                              JsonObject var23 = var22.getAsJsonObject();
                              if (var23.has("content")) {
                                 JsonObject var24 = var23.getAsJsonObject("content");
                                 if (var24.has("parts")) {
                                    for (JsonElement var26 : var24.getAsJsonArray("parts")) {
                                       JsonObject var27 = var26.getAsJsonObject();
                                       if (!var27.has("thought") || !var27.get("thought").getAsBoolean()) {
                                          var43.append(method4749(var27, "text"));
                                       }
                                    }
                                 }
                              }
                           }

                           yield var43.toString();
                        }
                        case OPENAI -> {
                           if (var17.has("error")) {
                              throw new AiStreamClient.Entry(method4748(var17));
                           }

                           JsonArray var19 = var17.has("choices") ? var17.getAsJsonArray("choices") : new JsonArray();
                           if (var19.isEmpty()) {
                              yield "";
                           } else {
                              JsonObject var20 = var19.get(0).getAsJsonObject();
                              yield var20.has("delta") && var20.get("delta").isJsonObject() ? method4749(var20.getAsJsonObject("delta"), "content") : "";
                           }
                        }
                     };

                     String var18 = var47_1;
                     if (!var18.isEmpty()) {
                        var10.append((String)var18);
                        var6.accept(var18);
                     }
                  }
               }
            }

            if (var15.equals("refusal")) {
               throw new AiStreamClient.Entry("модель отказалась отвечать на этот запрос");
            }
         } finally {
            var11.set(true);
         }

         return var10.toString();
      }
   }

   private static HttpResponse<InputStream> method4740(CompletableFuture<HttpResponse<InputStream>> var0, BooleanSupplier var1) throws IOException, InterruptedException {
      while (!var1.getAsBoolean()) {
         try {
            return (HttpResponse<InputStream>)var0.get(100L, TimeUnit.MILLISECONDS);
         } catch (TimeoutException var5) {
         } catch (ExecutionException var6) {
            Throwable var3 = var6.getCause();

            while (var3 instanceof CompletionException && var3.getCause() != null) {
               var3 = var3.getCause();
            }

            if (var3 instanceof IOException var8) {
               throw var8;
            }

            if (var3 instanceof RuntimeException var7) {
               throw var7;
            }

            if (var3 instanceof Error var4) {
               throw var4;
            }

            throw new IOException(var3 == null ? var6.getMessage() : var3.getMessage(), var3);
         }
      }

      var0.cancel(true);
      var0.thenAccept(AiStreamClient::method4741);
      return null;
   }

   private static void method4741(HttpResponse<InputStream> var0) {
      try {
         var0.body().close();
      } catch (IOException var2) {
      }
   }

   private static void method4742(InputStream var0, BooleanSupplier var1, AtomicBoolean var2) {
      Thread.ofVirtual().name("zenith-ai-cancel").start(() -> {
         try {
            while (!var2.get()) {
               if (var1.getAsBoolean()) {
                  var0.close();
                  return;
               }

               Thread.sleep(100L);
            }
         } catch (InterruptedException | IOException var4) {
         }
      });
   }

   private static String method4743(BufferedReader var0, BooleanSupplier var1) throws IOException {
      try {
         return var0.readLine();
      } catch (IOException var3) {
         if (var1.getAsBoolean()) {
            return null;
         } else {
            throw var3;
         }
      }
   }

   private static HttpRequest method4744(String var0, String var1, String var2, String var3, List<AiStreamClient.Data> var4) {
      JsonObject var5 = new JsonObject();
      var5.addProperty("model", var1);
      var5.addProperty("max_tokens", 32000);
      var5.addProperty("stream", true);
      var5.addProperty("system", var3);
      JsonArray var6 = new JsonArray();

      for (AiStreamClient.Data var8 : var4) {
         JsonObject var9 = new JsonObject();
         var9.addProperty("role", var8.isFlag() ? "user" : "assistant");
         if (var8.getItems().isEmpty()) {
            var9.addProperty("content", var8.text());
         } else {
            JsonArray var10 = new JsonArray();

            for (AiStreamClient.Data2 var12 : var8.getItems()) {
               JsonObject var13 = new JsonObject();
               var13.addProperty("type", "base64");
               var13.addProperty("media_type", var12.getText());
               var13.addProperty("data", var12.getText2());
               JsonObject var14 = new JsonObject();
               var14.addProperty("type", "image");
               var14.add("source", var13);
               var10.add(var14);
            }

            JsonObject var16 = new JsonObject();
            var16.addProperty("type", "text");
            var16.addProperty("text", var8.text().isBlank() ? "(картинка)" : var8.text());
            var10.add(var16);
            var9.add("content", var10);
         }

         var6.add(var9);
      }

      var5.add("messages", var6);
      Builder var15 = HttpRequest.newBuilder(URI.create(var2))
         .timeout(Duration.ofMinutes(10L))
         .header("content-type", "application/json")
         .header("anthropic-version", "2023-06-01")
         .header("x-api-key", var0);
      if (items.contains(var1)) {
         var5.addProperty("fallbacks", "default");
         var15.header("anthropic-beta", "server-side-fallback-2026-07-01");
      }

      return var15.POST(BodyPublishers.ofString(var5.toString(), StandardCharsets.UTF_8)).build();
   }

   private static HttpRequest method4745(ShownProvider var0, String var1, String var2, String var3, String var4, List<AiStreamClient.Data> var5) {
      JsonObject var6 = new JsonObject();
      var6.addProperty("model", AiSettingsStore.clean(var2));
      var6.addProperty("stream", true);
      JsonArray var7 = new JsonArray();
      JsonObject var8 = new JsonObject();
      var8.addProperty("role", "system");
      var8.addProperty("content", var4);
      var7.add(var8);

      for (AiStreamClient.Data var10 : var5) {
         JsonObject var11 = new JsonObject();
         var11.addProperty("role", var10.isFlag() ? "user" : "assistant");
         if (var10.getItems().isEmpty()) {
            var11.addProperty("content", var10.text());
         } else {
            JsonArray var12 = new JsonArray();
            JsonObject var13 = new JsonObject();
            var13.addProperty("type", "text");
            var13.addProperty("text", var10.text().isBlank() ? "(картинка)" : var10.text());
            var12.add(var13);

            for (AiStreamClient.Data2 var15 : var10.getItems()) {
               JsonObject var16 = new JsonObject();
               var16.addProperty("url", "data:" + var15.getText() + ";base64," + var15.getText2());
               JsonObject var17 = new JsonObject();
               var17.addProperty("type", "image_url");
               var17.add("image_url", var16);
               var12.add(var17);
            }

            var11.add("content", var12);
         }

         var7.add(var11);
      }

      var6.add("messages", var7);
      String var18 = AiSettingsStore.clean(var3);

      while (var18.endsWith("/")) {
         var18 = var18.substring(0, var18.length() - 1);
      }

      if (!var18.endsWith("/chat/completions")) {
         var18 = var18 + "/chat/completions";
      }

      Builder var19 = HttpRequest.newBuilder(URI.create(var18)).timeout(Duration.ofMinutes(10L)).header("Content-Type", "application/json");
      if (var1 != null && !var1.isBlank()) {
         var19.header("Authorization", "Bearer " + var1);
      }

      if (var0 == ShownProvider.INSTANCE6) {
         var19.header("HTTP-Referer", "https://zenithdlc.com").header("X-Title", "Zenith Client");
      }

      return var19.POST(BodyPublishers.ofString(var6.toString(), StandardCharsets.UTF_8)).build();
   }

   private static HttpRequest method4746(String var0, String var1, String var2, String var3, List<AiStreamClient.Data> var4) {
      JsonObject var5 = new JsonObject();
      JsonObject var6 = new JsonObject();
      JsonArray var7 = new JsonArray();
      JsonObject var8 = new JsonObject();
      var8.addProperty("text", var3);
      var7.add(var8);
      var6.add("parts", var7);
      var5.add("systemInstruction", var6);
      JsonArray var9 = new JsonArray();

      for (AiStreamClient.Data var11 : var4) {
         JsonObject var12 = new JsonObject();
         var12.addProperty("role", var11.isFlag() ? "user" : "model");
         JsonArray var13 = new JsonArray();

         for (AiStreamClient.Data2 var15 : var11.getItems()) {
            JsonObject var16 = new JsonObject();
            var16.addProperty("mime_type", var15.getText());
            var16.addProperty("data", var15.getText2());
            JsonObject var17 = new JsonObject();
            var17.add("inline_data", var16);
            var13.add(var17);
         }

         JsonObject var20 = new JsonObject();
         var20.addProperty("text", var11.text().isBlank() ? "(картинка)" : var11.text());
         var13.add(var20);
         var12.add("parts", var13);
         var9.add(var12);
      }

      var5.add("contents", var9);
      String var18 = var2.endsWith("/") ? var2 : var2 + "/";
      String var19 = var18 + URLEncoder.encode(var1, StandardCharsets.UTF_8) + ":streamGenerateContent?alt=sse";
      return HttpRequest.newBuilder(URI.create(var19))
         .timeout(Duration.ofMinutes(10L))
         .header("Content-Type", "application/json")
         .header("x-goog-api-key", var0)
         .POST(BodyPublishers.ofString(var5.toString(), StandardCharsets.UTF_8))
         .build();
   }

   private static String method4747(int var0, String var1) {
      String var2 = "";
      String var3 = var1.stripLeading().toLowerCase(Locale.ROOT);
      if (!var3.startsWith("<!doctype") && !var3.startsWith("<html")) {
         try {
            JsonElement var4 = JsonParser.parseString(var1.trim());
            if (var4.isJsonArray() && !var4.getAsJsonArray().isEmpty()) {
               var4 = var4.getAsJsonArray().get(0);
            }

            if (var4.isJsonObject()) {
               var2 = method4748(var4.getAsJsonObject());
            }
         } catch (RuntimeException var5) {
         }

         if (var2.isEmpty()) {
            var2 = var1.strip();
         }

         if (var2.length() > 300) {
            var2 = var2.substring(0, 300) + "…";
         }
         String var6 = switch (var0) {
            case 400 -> "запрос отклонён";
            case 401, 403 -> "ключ не подошёл";
            case 404 -> "модель или адрес не найдены";
            case 429 -> "лимит запросов или кончились деньги на счёте";
            default -> var0 >= 500 ? "сервис нейросети недоступен" : "ошибка " + var0;
         };
         return var6 + (var2.isEmpty() ? "" : ": " + var2);
      } else {
         return (var0 == 404 ? "адрес не найден" : "ошибка " + var0)
            + ": сервер вернул веб-страницу вместо ответа API — проверь адрес (должен вести на /v1, без лишних символов в конце)";
      }
   }

   private static String method4748(JsonObject var0) {
      if (var0.has("error")) {
         JsonElement var1 = var0.get("error");
         if (var1.isJsonObject()) {
            String var2 = method4749(var1.getAsJsonObject(), "message");
            if (!var2.isEmpty()) {
               return var2;
            }
         } else if (var1.isJsonPrimitive()) {
            return var1.getAsString();
         }
      }

      return method4749(var0, "message");
   }

   private static String method4749(JsonObject var0, String var1) {
      return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : "";
   }

   public static final class Data {
      private final boolean flag;
      private final String text;
      private final List<AiStreamClient.Data2> items;

      public boolean flag() {
         return this.flag;
      }

      public String text() {
         return this.text;
      }

      public List<AiStreamClient.Data2> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "flag=" + this.flag + ", " + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(boolean var1, String var2) {
         this(var1, var2, List.of());
      }

      public Data(boolean var1, String var2, List<AiStreamClient.Data2> var3) {
         this.flag = var1;
         this.text = var2;
         this.items = var3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<AiStreamClient.Data2> getItems() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data2(String var1, String var2) {
         this.text = var1;
         this.text2 = var2;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   public static final class Entry extends IOException {
      public Entry(String var1) {
         super(var1);
      }
   }
}

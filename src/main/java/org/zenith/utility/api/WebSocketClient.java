package org.zenith.utility.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import org.zenith.utility.config.SiteSession;

public final class WebSocketClient {
   private static final Duration duration = Duration.ofSeconds(8L);
   private static final long CONST_longValue = 2000L;
   private static final long CONST_longValue2 = 30000L;
   private static final long CONST_longValue3 = 70000L;
   private static final long CONST_longValue4 = 25000L;
   private static final int CONST_intValue = 1048576;
   private final String text;
   private final Consumer<JsonObject> consumer;
   private final StringBuilder stringBuilder = new StringBuilder();
   private boolean flag;
   private volatile HttpClient httpClient;
   private volatile long longValue;
   private volatile WebSocket webSocket;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private volatile long longValue2;
   private volatile long longValue3 = 2000L;
   private volatile long longValue4;
   private volatile long longValue5;
   private volatile String text2 = "";
   private volatile String host;
   private volatile int intValue;
   private static final int CONST_intValue2 = 2;
   private volatile int intValue2;
   private volatile long longValue6;
   private static final long CONST_longValue5 = 30000L;

   public WebSocketClient(String var1, Consumer<JsonObject> var2) {
      this.text = var1;
      this.consumer = var2;
   }

   public boolean isLive() {
      WebSocket var1 = this.webSocket;
      return var1 != null && !var1.isOutputClosed() && System.currentTimeMillis() - this.longValue4 < 70000L;
   }

   public void method491() {
      this.flag2 = true;
      String var1 = SiteSession.method2289();
      if (var1.isEmpty()) {
         this.close();
      } else {
         if (!var1.equals(this.text2) && this.webSocket != null) {
            this.close();
         }

         WebSocket var2 = this.webSocket;
         long var3 = System.currentTimeMillis();
         if (var2 != null && !var2.isOutputClosed()) {
            if (var3 - this.longValue4 >= 70000L) {
               this.close();
               this.method495(this.longValue);
               return;
            }

            if (var3 - this.longValue5 >= 25000L) {
               this.method496(var2, "{\"type\":\"ping\"}");
            }
         }

         if (!this.flag3 && !this.isLive() && var3 >= this.longValue2) {
            this.method492(var1);
         }
      }
   }

   public void release() {
      this.flag2 = false;
      this.close();
   }

   private void method492(String var1) {
      this.flag3 = true;
      this.text2 = var1;
      long var2 = ++this.longValue;
      HttpClient var4 = HttpClients.method2670(duration);
      this.httpClient = var4;
      String var6 = this.method493();

      CompletableFuture<java.net.http.WebSocket> var5;
      try {
         URI var7 = URI.create(var6.replaceFirst("^http", "ws") + this.text);
         var5 = var4.newWebSocketBuilder().connectTimeout(duration).buildAsync(var7, new WebSocketClient.Entry(var2, var1));
      } catch (RuntimeException var8) {
         this.flag3 = false;
         this.method494(var6);
         this.method495(var2);
         return;
      }

      var5.whenComplete((var4x, var5x) -> {
         this.flag3 = false;
         if (var5x != null) {
            this.method494(var6);
            this.method495(var2);
         } else {
            this.host = var6;
            this.intValue = 0;
            this.intValue2 = 0;
            if (var2 != this.longValue) {
               var4x.abort();
            } else {
               this.longValue4 = System.currentTimeMillis();
               this.longValue6 = this.longValue4;
               this.webSocket = var4x;
            }
         }
      });
   }

   private String method493() {
      String var1 = this.host;
      return var1 != null && RegionUtil.method2037().contains(var1) ? var1 : SiteHttpClient.method2409();
   }

   private void method494(String var1) {
      if (++this.intValue >= 2) {
         this.intValue = 0;
         var var2 = RegionUtil.method2037();
         int var3 = var2.indexOf(var1);
         this.host = (String)var2.get(var3 < 0 ? 0 : (var3 + 1) % var2.size());
         if (++this.intValue2 < var2.size()) {
            this.longValue3 = 2000L;
         }
      }
   }

   private void method495(long var1) {
      if (var1 == this.longValue) {
         this.webSocket = null;
         long var3 = this.longValue6;
         this.longValue6 = 0L;
         if (var3 != 0L && System.currentTimeMillis() - var3 >= 30000L) {
            this.longValue3 = 2000L;
         }

         long var5 = this.longValue3;
         this.longValue2 = System.currentTimeMillis() + Math.round(var5 * (0.8 + ThreadLocalRandom.current().nextDouble() * 0.4));
         this.longValue3 = Math.min(30000L, var5 * 2L);
      }
   }

   private void close() {
      this.longValue++;
      WebSocket var1 = this.webSocket;
      this.webSocket = null;
      this.text2 = "";
      synchronized (this.stringBuilder) {
         this.stringBuilder.setLength(0);
         this.flag = false;
      }

      if (var1 != null) {
         try {
            var1.abort();
         } catch (RuntimeException var4) {
         }
      }

      this.httpClient = null;
   }

   private void method496(WebSocket var1, String var2) {
      try {
         this.longValue5 = System.currentTimeMillis();
         var1.sendText(var2, true);
      } catch (RuntimeException var4) {
         this.method495(this.longValue);
      }
   }

   private void method497(WebSocket var1, String var2) {
      JsonObject var3;
      try {
         var3 = JsonParser.parseString(var2).getAsJsonObject();
      } catch (RuntimeException var7) {
         return;
      }

      String var4 = SiteHttpClient.method2417(var3, "type", "");
      if (var4.equals("ping")) {
         this.method496(var1, "{\"type\":\"pong\"}");
      } else if (!var4.equals("pong")) {
         try {
            this.consumer.accept(var3);
         } catch (RuntimeException var6) {
         }
      }
   }

   private final class Entry implements Listener {
      private final long longValue;
      private final String text;

      public Entry(long var2, String var4) {
         this.longValue = var2;
         this.text = var4;
      }

      @Override
      public void onOpen(WebSocket var1) {
         WebSocketClient.this.longValue4 = System.currentTimeMillis();
         JsonObject var2 = new JsonObject();
         var2.addProperty("type", "auth");
         var2.addProperty("token", this.text);
         WebSocketClient.this.method496(var1, var2.toString());
         var1.request(1L);
      }

      @Override
      public CompletionStage<?> onText(WebSocket var1, CharSequence var2, boolean var3) {
         if (this.longValue != WebSocketClient.this.longValue) {
            return null;
         } else {
            WebSocketClient.this.longValue4 = System.currentTimeMillis();
            String var4 = null;
            synchronized (WebSocketClient.this.stringBuilder) {
               if (!WebSocketClient.this.flag && WebSocketClient.this.stringBuilder.length() + var2.length() > 1048576) {
                  WebSocketClient.this.stringBuilder.setLength(0);
                  WebSocketClient.this.flag = true;
               }

               if (!WebSocketClient.this.flag) {
                  WebSocketClient.this.stringBuilder.append(var2);
               }

               if (var3) {
                  if (!WebSocketClient.this.flag) {
                     var4 = WebSocketClient.this.stringBuilder.toString();
                  }

                  WebSocketClient.this.stringBuilder.setLength(0);
                  WebSocketClient.this.flag = false;
               }
            }

            var1.request(1L);
            if (var4 != null) {
               WebSocketClient.this.method497(var1, var4);
            }

            return null;
         }
      }

      @Override
      public CompletionStage<?> onPing(WebSocket var1, ByteBuffer var2) {
         if (this.longValue == WebSocketClient.this.longValue) {
            WebSocketClient.this.longValue4 = System.currentTimeMillis();
         }

         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onPong(WebSocket var1, ByteBuffer var2) {
         if (this.longValue == WebSocketClient.this.longValue) {
            WebSocketClient.this.longValue4 = System.currentTimeMillis();
         }

         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onClose(WebSocket var1, int var2, String var3) {
         this.done();
         return null;
      }

      @Override
      public void onError(WebSocket var1, Throwable var2) {
         this.done();
      }

      private void done() {
         if (this.longValue == WebSocketClient.this.longValue) {
            if (WebSocketClient.this.flag2) {
               WebSocketClient.this.method495(this.longValue);
            } else {
               WebSocketClient.this.webSocket = null;
            }
         }
      }
   }
}

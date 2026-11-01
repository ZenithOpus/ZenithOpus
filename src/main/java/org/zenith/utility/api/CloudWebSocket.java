package org.zenith.utility.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public final class CloudWebSocket {
   private static final int CONST_intValue = 256;
   public static final int CONST_intValue2 = -1;
   private static final int CONST_intValue3 = 8;
   private static final int CONST_intValue4 = 1048576;
   private static final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(8L));
   private final URI uRI;
   private final BiConsumer<CloudWebSocket, ByteBuffer> biConsumer;
   private final Runnable runnable;
   private final BiConsumer<CloudWebSocket, String> biConsumer2;
   private final ArrayDeque<CloudWebSocket.Data> arrayDeque = new ArrayDeque<>();
   private boolean flag;
   private volatile WebSocket webSocket;
   private volatile boolean closed;
   private volatile long longValue = System.currentTimeMillis();
   private volatile long longValue2 = System.currentTimeMillis();
   private volatile long longValue3;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private volatile long longValue4;
   private volatile int intValue = -1;
   private ByteBuffer byteBuffer;

   public CloudWebSocket(URI var1, BiConsumer<CloudWebSocket, ByteBuffer> var2, Runnable var3, BiConsumer<CloudWebSocket, String> var4) {
      this.uRI = var1;
      this.biConsumer = var2;
      this.runnable = var3;
      this.biConsumer2 = var4;
   }

   public void connect() {
      httpClient.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(8L)).buildAsync(this.uRI, new CloudWebSocket.Entry()).whenComplete((var1, var2) -> {
         if (var2 != null) {
            this.method1955("не удалось подключиться: " + method1956(var2));
         } else if (this.closed) {
            var1.abort();
         } else {
            this.webSocket = var1;
            if (this.closed) {
               this.webSocket = null;
               var1.abort();
            } else {
               this.method1946();
               this.runnable.run();
            }
         }
      });
   }

   private void method1946() {
      boolean var1;
      synchronized (this.arrayDeque) {
         var1 = !this.flag && !this.arrayDeque.isEmpty() && this.webSocket != null;
         if (var1) {
            this.flag = true;
         }
      }

      if (var1) {
         this.method1952();
      }
   }

   public boolean isOpen() {
      WebSocket var1 = this.webSocket;
      return var1 != null && !this.closed && !var1.isOutputClosed();
   }

   public long method1947() {
      return System.currentTimeMillis() - this.longValue;
   }

   public long method1948() {
      return System.currentTimeMillis() - this.longValue2;
   }

   public long method1949() {
      return System.currentTimeMillis() - this.longValue3;
   }

   public void ping() {
      WebSocket var1 = this.webSocket;
      if (var1 != null && !this.closed && this.atomicBoolean.compareAndSet(false, true)) {
         this.longValue2 = System.currentTimeMillis();
         this.longValue3 = this.longValue2;
         this.longValue4 = System.nanoTime();
         var1.sendPing(ByteBuffer.allocate(0)).whenComplete((var1x, var2) -> {
            this.atomicBoolean.set(false);
            if (var2 != null) {
               this.method1955("ping не прошёл: " + method1956(var2));
            }
         });
      }
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void method1950(String var1) {
      this.method1955(var1);
   }

   public void method1951(ByteBuffer var1, int var2) {
      if (!this.closed) {
         boolean var3;
         synchronized (this.arrayDeque) {
            if (var2 != -1) {
               if (var2 != 0) {
                  var var11 = this.arrayDeque.iterator();

                  while (var11.hasNext()) {
                     if (((CloudWebSocket.Data)var11.next()).intValue == var2) {
                        var11.remove();
                        break;
                     }
                  }
               } else if (this.arrayDeque.size() >= 256) {
                  this.arrayDeque.pollFirst();
               }
            } else {
               int var5 = 0;
               CloudWebSocket.Data var6 = null;

               for (CloudWebSocket.Data var8 : this.arrayDeque) {
                  if (var8.intValue == -1) {
                     if (var6 == null) {
                        var6 = var8;
                     }

                     var5++;
                  }
               }

               if (var5 >= 8) {
                  this.arrayDeque.remove(var6);
               }
            }

            this.arrayDeque.addLast(new CloudWebSocket.Data(var1, var2));
            var3 = !this.flag && this.webSocket != null;
            if (var3) {
               this.flag = true;
            }
         }

         if (var3) {
            this.method1952();
         }
      }
   }

   private void method1952() {
      WebSocket var1 = this.webSocket;
      CloudWebSocket.Data var2;
      synchronized (this.arrayDeque) {
         var2 = this.arrayDeque.pollFirst();
         if (var2 == null || var1 == null || this.closed) {
            this.flag = false;
            return;
         }
      }

      this.longValue2 = System.currentTimeMillis();
      var1.sendBinary(var2.byteBuffer, true).whenComplete((var1x, var2x) -> {
         if (var2x != null) {
            this.method1955("отправка не прошла: " + method1956(var2x));
         } else {
            this.method1952();
         }
      });
   }

   private synchronized boolean method1953() {
      if (this.closed) {
         return false;
      } else {
         this.closed = true;
         return true;
      }
   }

   public void method1954(String var1) {
      if (this.method1953()) {
         WebSocket var2 = this.webSocket;
         this.webSocket = null;
         if (var2 != null) {
            var2.sendClose(1000, var1).orTimeout(3L, TimeUnit.SECONDS).whenComplete((var1x, var2x) -> var2.abort());
         }

         synchronized (this.arrayDeque) {
            this.arrayDeque.clear();
            this.flag = false;
         }
      }
   }

   private void method1955(String var1) {
      if (this.method1953()) {
         WebSocket var2 = this.webSocket;
         this.webSocket = null;
         if (var2 != null) {
            var2.abort();
         }

         synchronized (this.arrayDeque) {
            this.arrayDeque.clear();
            this.flag = false;
         }

         this.biConsumer2.accept(this, var1);
      }
   }

   public static String method1956(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null) {
         var1 = var1.getCause();
      }

      String var2 = var1.getMessage();
      return var2 != null && !var2.isBlank() ? var2 : var1.getClass().getSimpleName();
   }

   private static final class Data {
      private final ByteBuffer byteBuffer;
      private final int intValue;

      public ByteBuffer byteBuffer() {
         return this.byteBuffer;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.byteBuffer, other.byteBuffer) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(byteBuffer, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "byteBuffer=" + this.byteBuffer + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(ByteBuffer var1, int var2) {
         this.byteBuffer = var1;
         this.intValue = var2;
      }

      public ByteBuffer getByteBuffer() {
         return this.byteBuffer;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private final class Entry implements Listener {
      @Override
      public void onOpen(WebSocket var1) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         var1.request(1L);
      }

      @Override
      public CompletionStage<?> onBinary(WebSocket var1, ByteBuffer var2, boolean var3) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         ByteBuffer var4 = this.method1957(var2, var3);
         if (var4 != null) {
            try {
               CloudWebSocket.this.biConsumer.accept(CloudWebSocket.this, var4);
            } catch (Throwable var6) {
               CloudWebSocket.this.method1955("разбор кадра: " + CloudWebSocket.method1956(var6));
               return null;
            }
         }

         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onText(WebSocket var1, CharSequence var2, boolean var3) {
         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onPing(WebSocket var1, ByteBuffer var2) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onPong(WebSocket var1, ByteBuffer var2) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         long var3 = CloudWebSocket.this.longValue4;
         if (var3 != 0L) {
            CloudWebSocket.this.longValue4 = 0L;
            CloudWebSocket.this.intValue = (int)Math.min(9999L, (System.nanoTime() - var3) / 1000000L);
         }

         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onClose(WebSocket var1, int var2, String var3) {
         CloudWebSocket.this.method1955("соединение закрыто (" + var2 + (var3 != null && !var3.isBlank() ? ", " + var3 : "") + ")");
         return null;
      }

      @Override
      public void onError(WebSocket var1, Throwable var2) {
         CloudWebSocket.this.method1955("обрыв: " + CloudWebSocket.method1956(var2));
      }

      private ByteBuffer method1957(ByteBuffer var1, boolean var2) {
         if (var2 && CloudWebSocket.this.byteBuffer == null) {
            return var1;
         } else if ((CloudWebSocket.this.byteBuffer == null ? 0 : CloudWebSocket.this.byteBuffer.position()) + var1.remaining() > 1048576) {
            CloudWebSocket.this.byteBuffer = null;
            CloudWebSocket.this.method1955("кадр больше допустимого");
            return null;
         } else {
            if (CloudWebSocket.this.byteBuffer == null) {
               CloudWebSocket.this.byteBuffer = ByteBuffer.allocate(var1.remaining()).put(var1);
            } else {
               ByteBuffer var3 = ByteBuffer.allocate(CloudWebSocket.this.byteBuffer.position() + var1.remaining());
               CloudWebSocket.this.byteBuffer.flip();
               var3.put(CloudWebSocket.this.byteBuffer).put(var1);
               CloudWebSocket.this.byteBuffer = var3;
            }

            if (!var2) {
               return null;
            } else {
               ByteBuffer var4 = CloudWebSocket.this.byteBuffer.flip();
               CloudWebSocket.this.byteBuffer = null;
               return var4;
            }
         }
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonObject;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.config.SiteSession;
import org.zenith.utility.network.Cloud;

public final class CloudTicket {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   private static final String text = "/user/nexus_ticket";
   private static final long CONST_longValue = 300L;
   public static final int CONST_intValue = 2048;
   private static final long CONST_longValue2 = 60000L;
   private static volatile CloudTicket.Data data;
   private static volatile CompletableFuture<String> completableFuture;
   private static volatile long longValue;

   private CloudTicket() {
   }

   public static String method1373() {
      CloudTicket.Data var0 = data;
      if (var0 != null && var0.method1377(Cloud.accountUid())) {
         return var0.value();
      } else {
         method1374();
         return null;
      }
   }

   public static CompletableFuture<String> method1374() {
      CloudTicket.Data var0 = data;
      if (var0 != null && var0.method1377(Cloud.accountUid())) {
         return CompletableFuture.completedFuture(var0.value());
      } else {
         var var1 = completableFuture;
         if (var1 != null && !var1.isDone()) {
            return var1.copy();
         } else if (SiteSession.method2291() && (longValue == 0L || System.currentTimeMillis() - longValue >= 60000L)) {
            synchronized (CloudTicket.class) {
               var1 = completableFuture;
               if (var1 != null && !var1.isDone()) {
                  return var1.copy();
               } else {
                  var var3 = SiteHttpClient.method2411("/user/nexus_ticket", new JsonObject()).handle((var0x, var1x) -> {
                     if (var1x != null) {
                        longValue = System.currentTimeMillis();
                        if (method1376(var1x)) {
                           data = null;
                        }

                        logger.debug("Облако: сайт не выдал билет: {}", SiteHttpClient.method2424(var1x));
                        return null;
                     } else {
                        CloudTicket.Data var2 = method1375(var0x);
                        if (var2 != null && var2.method1377(Cloud.accountUid())) {
                           longValue = 0L;
                           data = var2;
                           return var2.value();
                        } else {
                           longValue = System.currentTimeMillis();
                           logger.debug("Облако: ответ сайта без годного билета");
                           return null;
                        }
                     }
                  });
                  completableFuture = var3;
                  return var3.copy();
               }
            }
         } else {
            return CompletableFuture.completedFuture(null);
         }
      }
   }

   public static void invalidate() {
      data = null;
   }

   private static CloudTicket.Data method1375(JsonObject var0) {
      String var1;
      try {
         var1 = SiteHttpClient.method2417(var0, "ticket", "");
      } catch (RuntimeException var9) {
         return null;
      }

      if (!var1.isEmpty() && var1.length() <= 2048 && var1.startsWith("nx1.")) {
         for (int var2 = 0; var2 < var1.length(); var2++) {
            char var3 = var1.charAt(var2);
            if (var3 <= ' ' || var3 > '~') {
               return null;
            }
         }

         long var4;
         long var11;
         try {
            var11 = var0.has("expires_at") && !var0.get("expires_at").isJsonNull() ? var0.get("expires_at").getAsLong() : 0L;
            var4 = var0.has("uid") && !var0.get("uid").isJsonNull() ? var0.get("uid").getAsLong() : 0L;
         } catch (RuntimeException var10) {
            return null;
         }

         if (var11 <= 0L) {
            return null;
         } else {
            CloudTicket.Data var6 = new CloudTicket.Data(var1, var11, var4);
            long var7 = Cloud.accountUid();
            if (var4 > 0L && var7 > 0L && var4 != var7) {
               logger.warn("Облако: билет сайта на uid {}, а аккаунт клиента — {}; билет не используется", var4, var7);
               return null;
            } else {
               return var6;
            }
         }
      } else {
         return null;
      }
   }

   private static boolean method1376(Throwable var0) {
      for (Throwable var1 = var0; var1 != null; var1 = var1.getCause()) {
         if (var1 instanceof SiteHttpClient.Entry2 var2 && var2.method2426()) {
            return true;
         }

         if (var1.getCause() == var1) {
            break;
         }
      }

      return false;
   }

   private static final class Data {
      private final String text;
      private final long longValue;
      private final long longValue2;

      public String text() {
         return this.text;
      }

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      private Data(String var1, long var2, long var4) {
         this.text = var1;
         this.longValue = var2;
         this.longValue2 = var4;
      }

      public boolean method1377(long var1) {
         return Util18.method4489() / 1000L < this.longValue - 300L && (this.longValue2 <= 0L || var1 <= 0L || this.longValue2 == var1);
      }

      public String value() {
         return this.text;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long uid() {
         return this.longValue2;
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.Zenith;
import org.zenith.utility.config.SiteSession;

public final class SiteLicenseService {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Site");
   public static final int CONST_intValue = 1;
   public static final int CONST_intValue2 = 2;
   public static final int CONST_intValue3 = 3;
   public static final int CONST_intValue4 = 4;
   public static final int CONST_intValue5 = 9;
   private static final long CONST_longValue = 1800000L;
   private static final long CONST_longValue2 = 300000L;
   private static final long CONST_longValue3 = 60L;
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private static final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   private static volatile long longValue;
   private static volatile long longValue2;
   private static volatile List<SiteLicenseService.Data> items = List.of();
   private static volatile String text = "";

   private SiteLicenseService() {
   }

   public static void start() {
      if (atomicBoolean.compareAndSet(false, true)) {
         ScheduledExecutorService var0 = Executors.newSingleThreadScheduledExecutor(var0x -> {
            Thread var1 = new Thread(var0x, "zenith-site-licenses");
            var1.setDaemon(true);
            return var1;
         });
         var0.scheduleWithFixedDelay(SiteLicenseService::tick, 0L, 60L, TimeUnit.SECONDS);
      }
   }

   public static List<SiteLicenseService.Data> getItems() {
      return items;
   }

   public static void method4604() {
      longValue2 = 0L;
      longValue = 0L;
      tick();
   }

   private static void tick() {
      try {
         long var0 = System.currentTimeMillis();
         String var2 = SiteSession.method2289();
         if (atomicBoolean2.get() || var2.isEmpty()) {
            return;
         }

         if (!var2.equals(text)) {
            longValue = 0L;
            longValue2 = 0L;
         }

         if (longValue != 0L && var0 - longValue < 1800000L) {
            return;
         }

         if (longValue2 != 0L && var0 - longValue2 < 300000L) {
            return;
         }

         if (!atomicBoolean2.compareAndSet(false, true)) {
            return;
         }

         text = var2;
         method4605().whenComplete((var0x, var1) -> atomicBoolean2.set(false));
      } catch (Throwable var3) {
         atomicBoolean2.set(false);
         logger.debug("Лицензии сайта: {}", var3.toString());
      }
   }

   private static CompletableFuture<Void> method4605() {
      var var0 = SiteHttpClient.method2411("/license/get_all_working", new JsonObject()).thenApply(SiteLicenseService::method4606);
      var var1 = SiteHttpClient.method2411("/user/login_token", new JsonObject())
         .thenApply(var0x -> "admin".equalsIgnoreCase(SiteHttpClient.method2417(var0x, "role", "").trim()))
         .exceptionally(var0x -> false);
      return var0.<Boolean, Void>thenCombine(var1, (var0x, var1x) -> {
         method4607((List<SiteLicenseService.Data>)var0x, var1x);
         return (Void)null;
      }).whenComplete((var0x, var1x) -> {
         if (var1x != null) {
            longValue2 = System.currentTimeMillis();
            logger.info("Лицензии сайта не пришли: {}", SiteHttpClient.method2424(var1x));
         }
      });
   }

   private static List<SiteLicenseService.Data> method4606(JsonObject var0) {
      JsonElement var1 = var0 == null ? null : var0.get("current_licenses");
      if (var1 != null && var1.isJsonArray()) {
         JsonArray var2 = var1.getAsJsonArray();
         List<Data> var3 = new ArrayList<>(var2.size());

         for (JsonElement var5 : var2) {
            if (var5.isJsonObject()) {
               JsonObject var6 = var5.getAsJsonObject();
               int var7 = SiteHttpClient.method2421(var6, "id", 0);
               if (var7 > 0) {
                  var3.add(
                     new SiteLicenseService.Data(var7, SiteHttpClient.method2417(var6, "name", ""), Math.max(0, SiteHttpClient.method2421(var6, "days", 0)))
                  );
               }
            }
         }

         return List.copyOf(var3);
      } else {
         return List.of();
      }
   }

   private static void method4607(List<SiteLicenseService.Data> var0, boolean var1) {
      items = var0;
      longValue = System.currentTimeMillis();
      longValue2 = 0L;
      SiteLicenseService.Data var2 = null;

      for (SiteLicenseService.Data var4 : var0) {
         int var5 = method4608(var4.getIntValue());
         if (var5 > 0
            && (var2 == null || var5 > method4608(var2.getIntValue()) || var5 == method4608(var2.getIntValue()) && var4.getIntValue2() > var2.getIntValue2())) {
            var2 = var4;
         }
      }

      Zenith.Entry.Type var7 = var2 == null ? null : method4609(var2.getIntValue());
      long var8 = var2 == null ? -1L : var2.getIntValue2();
      Zenith var6 = Zenith.getInstance();
      if (var6 != null && var6.getEntry() != null) {
         var6.getEntry().method1505(var7, var8, var1);
      }

      logger.info(
         "Лицензии сайта: {} → {}{}", new Object[]{method4610(var0), var7 == null ? "нет лицензии этой сборки" : var7.getName(), var1 ? " (админ)" : ""}
      );
   }

   private static int method4608(int var0) {
      return switch (var0) {
         case 1, 2, 3 -> 1;
         case 4 -> 3;
         default -> 0;
         case 9 -> 2;
      };
   }

   private static Zenith.Entry.Type method4609(int var0) {
      return switch (var0) {
         case 4 -> Zenith.Entry.Type.PRIVATE;
         case 9 -> Zenith.Entry.Type.OPUS;
         default -> Zenith.Entry.Type.method1514(null);
      };
   }

   private static String method4610(List<SiteLicenseService.Data> var0) {
      if (var0.isEmpty()) {
         return "[]";
      } else {
         StringBuilder var1 = new StringBuilder("[");

         for (SiteLicenseService.Data var3 : var0) {
            if (var1.length() > 1) {
               var1.append(", ");
            }

            var1.append(var3.getIntValue()).append(':').append(var3.name().toLowerCase(Locale.ROOT)).append('/').append(var3.getIntValue2()).append('d');
         }

         return var1.append(']').toString();
      }
   }

   public static final class Data {
      private final int intValue;
      private final String text;
      private final int intValue2;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(int var1, String var2, int var3) {
         this.intValue = var1;
         this.text = var2;
         this.intValue2 = var3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String name() {
         return this.text;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}

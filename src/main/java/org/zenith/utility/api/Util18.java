package org.zenith.utility.api;

import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class Util18 {
   private static final long CONST_longValue = 2000L;
   private static final long CONST_longValue2 = 86400000L;
   private static volatile long longValue;
   private static volatile boolean flag;

   private Util18() {
   }

   public static long method4489() {
      return System.currentTimeMillis() + longValue;
   }

   public static long getLongValue() {
      return longValue;
   }

   public static void method4490(long var0) {
      if (var0 > 0L) {
         long var2 = var0 - System.currentTimeMillis();
         if (Math.abs(var2) <= 86400000L) {
            longValue = Math.abs(var2) < 500L ? 0L : var2;
            flag = true;
         }
      }
   }

   public static void method4491(HttpResponse<?> var0) {
      if (var0 != null && !flag) {
         try {
            String var1 = var0.headers().firstValue("Date").orElse(null);
            if (var1 == null || var1.isBlank()) {
               return;
            }

            long var2 = ZonedDateTime.parse(var1, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli() + 500L;
            String var4 = var0.headers().firstValue("Age").orElse(null);
            if (var4 != null && !var4.isBlank()) {
               long var5 = Long.parseLong(var4.strip());
               if (var5 > 0L && var5 <= 86400L) {
                  var2 += var5 * 1000L;
               }
            }

            long var8 = var2 - System.currentTimeMillis();
            if (Math.abs(var8) > 86400000L) {
               return;
            }

            longValue = Math.abs(var8) < 2000L ? 0L : var8;
         } catch (RuntimeException var7) {
         }
      }
   }
}

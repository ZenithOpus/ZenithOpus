package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public class PlayTimeTracker {
   private static final String text = "protection.zenith";
   private static final String text2 = "playTimeZnth";
   private final File file;
   private final long longValue;
   private long longValue2;
   private long longValue3;
   private String text3;

   public PlayTimeTracker() {
      this.file = new File(Zenith.file, "protection.zenith");
      this.longValue = System.currentTimeMillis();
      this.text3 = this.method1398();
      this.load();
   }

   private String method1398() {
      LocalDate var1 = LocalDate.now();
      WeekFields var2 = WeekFields.of(Locale.getDefault());
      int var3 = var1.get(var2.weekOfWeekBasedYear());
      int var4 = var1.get(var2.weekBasedYear());
      return var4 + "-W" + var3;
   }

   private void load() {
      if (!this.file.exists()) {
         this.longValue2 = 0L;
         this.longValue3 = 0L;
      } else {
         try {
            try (BufferedReader var1 = new BufferedReader(new FileReader(this.file))) {
               String var2 = var1.readLine();
               if (var2 == null || var2.isEmpty()) {
                  this.longValue2 = 0L;
                  this.longValue3 = 0L;
                  return;
               }

               String var3 = AesGcmCrypto.method5332(var2, "playTimeZnth");
               Gson var4 = new Gson();
               JsonObject var5 = (JsonObject)var4.fromJson(var3, JsonObject.class);
               if (var5 != null) {
                  this.longValue2 = var5.has("totalSeconds") ? var5.get("totalSeconds").getAsLong() : 0L;
                  if (var5.has("weeklyData")) {
                     JsonObject var6 = var5.getAsJsonObject("weeklyData");
                     this.longValue3 = var6.has(this.text3) ? var6.get(this.text3).getAsLong() : 0L;
                  }

                  return;
               }

               this.longValue2 = 0L;
               this.longValue3 = 0L;
            }
         } catch (Exception var9) {
            this.longValue2 = 0L;
            this.longValue3 = 0L;
         }
      }
   }

   public void save() {
      long var1 = this.method1399();
      long var3 = this.longValue2 + var1;
      String var5 = this.method1398();
      long var6;
      if (var5.equals(this.text3)) {
         var6 = this.longValue3 + var1;
      } else {
         var6 = var1;
      }

      JsonObject var8 = new JsonObject();
      var8.addProperty("totalSeconds", var3);
      JsonObject var9 = new JsonObject();
      var9.addProperty(var5, var6);
      var8.add("weeklyData", var9);

      try {
         String var10 = new Gson().toJson(var8);
         String var11 = AesGcmCrypto.method5331(var10, "playTimeZnth");

         try (FileWriter var12 = new FileWriter(this.file)) {
            var12.write(var11);
         }
      } catch (Exception var17) {
      }
   }

   public long method1399() {
      return (System.currentTimeMillis() - this.longValue) / 1000L;
   }

   public long method1400() {
      String var1 = this.method1398();
      return var1.equals(this.text3) ? this.longValue3 + this.method1399() : this.method1399();
   }

   public long method1401() {
      return this.longValue2 + this.method1399();
   }

   public static String method1402(long var0) {
      long var2 = var0 / 3600L;
      long var4 = var0 % 3600L / 60L;
      return var2 > 0L ? var2 + "h " + var4 + "m" : var4 + "m";
   }
}

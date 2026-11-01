package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import org.zenith.ZenithAccess;

public final class NeuroClicks implements ZenithAccess {
   public static final NeuroClicks INSTANCE = new NeuroClicks();
   public static final String text = "neuro_clicks.json";
   public static final List<String> items = List.of("strong", "early", "exact", "relaxed");
   private static final int CONST_intValue = 72000;
   private final Map<String, NeuroClicks.Entry> items2 = new HashMap<>();
   private int intValue;
   private int intValue2;
   private double[] doubleValues;
   private double[] doubleValues2;
   private double[] doubleValues3;
   private double[] doubleValues4;
   private double doubleValue;
   private double doubleValue2;
   private volatile boolean loaded;
   private String status = "не загружался";
   private final SplittableRandom splittableRandom = new SplittableRandom();
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4;
   private int intValue5 = -1073741824;
   private float floatValue = 1.0F;
   private boolean flag = true;
   private int intValue6;
   private double doubleValue3;
   private int intValue7;
   private int intValue8 = -1;
   private int intValue9 = -1;
   private static final int CONST_intValue2 = 16;
   private int intValue10 = Integer.MIN_VALUE;
   private boolean flag2;
   private int intValue11 = Integer.MIN_VALUE;
   private int intValue12 = Integer.MAX_VALUE;
   private int intValue13 = Integer.MIN_VALUE;
   private int intValue14 = Integer.MAX_VALUE;
   private int intValue15 = Integer.MIN_VALUE;
   private boolean flag3;
   private BufferedWriter bufferedWriter;
   private int intValue16;

   private NeuroClicks() {
   }

   public String getStatus() {
      return this.status;
   }

   public synchronized boolean method442() {
      if (this.loaded) {
         return true;
      } else {
         try (InputStream var1 = this.method445()) {
            JsonObject var2 = JsonParser.parseReader(new InputStreamReader(var1, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!"neurotest-clicks-v1".equals(var2.get("format").getAsString())) {
               throw new IOException("формат " + var2.get("format").getAsString());
            }

            JsonObject var3 = var2.getAsJsonObject("profiles");

            for (String var5 : items) {
               JsonObject var6 = var3.getAsJsonObject(var5);
               NeuroClicks.Entry var7 = new NeuroClicks.Entry();
               JsonObject var8 = var6.getAsJsonObject("crit");
               this.intValue = var8.get("f_max").getAsInt();
               this.intValue2 = var8.get("da_max").getAsInt();
               var7.doubleValues = method443(var8.getAsJsonArray("p"));
               JsonObject var9 = var6.getAsJsonObject("ground");
               var7.intValue = var9.get("d_min").getAsInt();
               var7.intValue2 = var9.get("d_max").getAsInt();
               var7.doubleValues2 = method444(var9.getAsJsonArray("h"));
               if (var7.doubleValues.length != this.intValue2
                  || var7.doubleValues[0].length != this.intValue
                  || var7.doubleValues2.length != var7.intValue2 - var7.intValue + 1) {
                  throw new IOException("размеры таблиц профиля " + var5);
               }

               int var10 = Math.min(16, var7.intValue2) - var7.intValue + 1;
               var7.doubleValues3 = new double[var10];
               double var11 = 1.0;
               double var13 = 0.0;

               for (int var15 = 0; var15 < var10; var15++) {
                  var7.doubleValues3[var15] = var11 * var7.doubleValues2[var15];
                  var11 *= 1.0 - var7.doubleValues2[var15];
                  var13 += var7.doubleValues3[var15];
               }

               for (int var21 = 0; var21 < var10; var21++) {
                  var7.doubleValues3[var21] = var7.doubleValues3[var21] / var13;
               }

               this.items2.put(var5, var7);
            }

            JsonObject var19 = var2.getAsJsonObject("sprint");
            this.doubleValues = method444(var19.getAsJsonArray("release_after_takeoff"));
            this.doubleValues2 = method444(var19.getAsJsonArray("repress_after_hit"));
            this.doubleValue = var19.get("hold_p").getAsDouble();
            JsonObject var20 = var19.getAsJsonObject("tap");
            this.doubleValue2 = var20.get("p").getAsDouble();
            this.doubleValues3 = method444(var20.getAsJsonArray("delay"));
            this.doubleValues4 = method444(var20.getAsJsonArray("dur"));
            this.loaded = true;
            this.status = "NeuroClicker загружен (neuro_clicks.json)";
         } catch (Exception var18) {
            this.status = "NeuroClicker НЕ загружен (neuro_clicks.json): " + var18.getMessage();
         }

         System.out.println(this.status);
         return this.loaded;
      }
   }

   private static double[][] method443(JsonArray var0) {
      double[][] var1 = new double[var0.size()][];

      for (int var2 = 0; var2 < var0.size(); var2++) {
         var1[var2] = method444(var0.get(var2).getAsJsonArray());
      }

      return var1;
   }

   private static double[] method444(JsonArray var0) {
      double[] var1 = new double[var0.size()];
      int var2 = 0;

      for (JsonElement var4 : var0) {
         var1[var2++] = var4.getAsDouble();
      }

      return var1;
   }

   private InputStream method445() throws IOException {
      for (Path var2 : List.of(Path.of("neuro_clicks.json"), Path.of("models", "neuro_clicks.json"), Path.of("..", "models", "neuro_clicks.json"))) {
         if (Files.isRegularFile(var2)) {
            return Files.newInputStream(var2);
         }
      }

      InputStream var3 = NeuroClicks.class.getClassLoader().getResourceAsStream("models/neuro_clicks.json");
      if (var3 == null) {
         throw new IOException("neuro_clicks.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return var3;
      }
   }

   private int method446(double[] var1) {
      double var2 = this.splittableRandom.nextDouble();
      double var4 = 0.0;

      for (int var6 = 0; var6 < var1.length; var6++) {
         var4 += var1[var6];
         if (var2 < var4) {
            return var6;
         }
      }

      return var1.length - 1;
   }

   public void reset() {
      this.intValue3 = Integer.MIN_VALUE;
      this.intValue8 = -1;
      this.intValue9 = -1;
      this.intValue10 = Integer.MIN_VALUE;
      this.intValue11 = Integer.MIN_VALUE;
      this.intValue12 = Integer.MAX_VALUE;
      this.intValue13 = Integer.MIN_VALUE;
      this.intValue14 = Integer.MAX_VALUE;
      this.intValue15 = Integer.MIN_VALUE;
      this.flag3 = false;
      this.floatValue = 1.0F;
   }

   public void method447() {
      if (mc.player != null) {
         int var1 = mc.player.age;
         if (var1 != this.intValue3) {
            if (var1 != this.intValue3 + 1) {
               this.intValue5 = var1 - 100;
               this.flag = mc.player.isOnGround();
               this.intValue6 = var1 - 1;
               this.doubleValue3 = mc.player.getY();
               this.intValue7 = var1 - 1;
               this.intValue8 = -1;
            }

            this.intValue3 = var1;
            this.intValue4 = var1;
            float var2 = mc.player.getAttackCooldownProgress(0.0F);
            if (var2 < this.floatValue - 1.0E-4F) {
               this.intValue5 = var1 - 1;
               this.intValue9 = -1;
            }

            this.floatValue = var2;
            int var3 = var1 - 1;
            double var4 = mc.player.getY();
            this.flag = mc.player.isOnGround();
            if (this.flag) {
               this.intValue6 = var3;
               this.doubleValue3 = var4;
               this.intValue7 = var3;
               this.intValue8 = -1;
            } else if (var4 > this.doubleValue3) {
               this.doubleValue3 = var4;
               this.intValue7 = var3;
            }
         }
      }
   }

   public int method448() {
      return this.intValue4 - this.intValue5;
   }

   public int method449() {
      return this.flag ? 0 : this.intValue4 - 1 - this.intValue7;
   }

   public boolean method450(boolean var1, String var2, int var3) {
      NeuroClicks.Entry var4 = this.items2.get(var2);
      if (var4 == null) {
         return false;
      } else {
         return var1 ? mc.player.getAttackCooldownProgress(0.5F + var3) > 0.9F : this.method448() + var3 >= var4.intValue;
      }
   }

   public boolean method451(boolean var1, String var2) {
      if (this.intValue10 == this.intValue4) {
         return this.flag2;
      } else {
         this.intValue10 = this.intValue4;
         NeuroClicks.Entry var3 = this.items2.get(var2);
         if (var3 == null) {
            return this.flag2 = false;
         } else {
            int var4 = this.method448();
            if (var1) {
               int var5 = this.method449();
               if (var5 < 1) {
                  return this.flag2 = false;
               } else {
                  if (this.intValue8 < 0) {
                     int var6 = Math.max(1, Math.min(this.intValue2, var4 - var5));
                     this.intValue8 = 1 + this.method446(var3.doubleValues[var6 - 1]);
                  }

                  return this.flag2 = var5 >= this.intValue8;
               }
            } else {
               if (this.intValue9 < 0) {
                  this.intValue9 = var3.intValue + this.method446(var3.doubleValues3);
               }

               return this.flag2 = var4 >= this.intValue9;
            }
         }
      }
   }

   public void method452(boolean var1, boolean var2, boolean var3) {
      int var4 = mc.player.age;
      if (!this.flag && this.flag3) {
         int var5 = 1 + this.method446(this.doubleValues2);
         this.intValue13 = var5 >= this.doubleValues2.length ? Integer.MAX_VALUE : var4 + var5;
         this.intValue12 = Integer.MAX_VALUE;
         this.flag3 = false;
      } else if (var3 && this.flag && var1 && var2 && this.splittableRandom.nextDouble() < this.doubleValue2) {
         this.intValue14 = var4 + this.method446(this.doubleValues3);
         this.intValue15 = this.intValue14 + 1 + this.method446(this.doubleValues4);
      }
   }

   public boolean method453(boolean var1, boolean var2, boolean var3) {
      int var4 = mc.player.age;
      boolean var5 = !mc.player.isOnGround();
      if (!var5) {
         this.flag3 = false;
         this.intValue13 = Integer.MIN_VALUE;
         this.intValue12 = Integer.MAX_VALUE;
         this.intValue11 = Integer.MIN_VALUE;
         return var1 && (var4 < this.intValue14 || var4 >= this.intValue15);
      } else {
         if (this.intValue11 != this.intValue6) {
            this.intValue11 = this.intValue6;
            this.intValue12 = var1 && var2 ? this.intValue6 + 1 + this.method446(this.doubleValues) : Integer.MAX_VALUE;
         }

         if (var4 < this.intValue13) {
            return false;
         } else if (this.flag3) {
            return false;
         } else if ((var4 >= this.intValue12 || var3) && var2) {
            this.flag3 = true;
            return false;
         } else {
            return var1;
         }
      }
   }

   public void method454(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      if (this.intValue16 < 72000 && mc.player != null) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of("neuro_clicks_log.csv"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND
               );
               this.bufferedWriter.write("age,target,d,f,on_ground,y,cd,w_user,w_out,sprint,attack,f_star\n");
            }

            this.bufferedWriter
               .write(
                  mc.player.age
                     + ","
                     + (var1 ? 1 : 0)
                     + ","
                     + this.method448()
                     + ","
                     + this.method449()
                     + ","
                     + (mc.player.isOnGround() ? 1 : 0)
                     + ","
                     + mc.player.getY()
                     + ","
                     + mc.player.getAttackCooldownProgress(0.5F)
                     + ","
                     + (var2 ? 1 : 0)
                     + ","
                     + (var3 ? 1 : 0)
                     + ","
                     + (var4 ? 1 : 0)
                     + ","
                     + (var5 ? 1 : 0)
                     + ","
                     + this.intValue8
                     + "\n"
               );
            if (++this.intValue16 % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException var7) {
            this.intValue16 = 72000;
         }
      }
   }

   private static final class Entry {
      public double[][] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
      public int intValue;
      public int intValue2;
   }
}

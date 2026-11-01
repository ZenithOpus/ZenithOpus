package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.MouseRotationEvent;

public final class ReplayStrokes implements MinecraftAccess {
   public static final ReplayStrokes INSTANCE = new ReplayStrokes();
   private static final double CONST_doubleValue = 0.15;
   private static final String text = "replay_strokes.json";
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 600;
   private static final int CONST_intValue3 = 160;
   private static final int CONST_intValue4 = 8;
   private static final double CONST_doubleValue2 = 25.0;
   private static final double CONST_doubleValue3 = 40.0;
   private static final double CONST_doubleValue4 = 10.0;
   private static final double CONST_doubleValue5 = 60.0;
   private static final double CONST_doubleValue6 = 50.0;
   private static final double CONST_doubleValue7 = 900.0;
   private static final double CONST_doubleValue8 = 1.0;
   private static final int CONST_intValue5 = 3;
   private static final double CONST_doubleValue9 = 2.5;
   private static final double CONST_doubleValue10 = 3.0;
   private static final double CONST_doubleValue11 = 60.0;
   private static final double CONST_doubleValue12 = 0.4;
   private static final double CONST_doubleValue13 = 2.5;
   private static final double CONST_doubleValue14 = 70.0;
   private static final double CONST_doubleValue15 = 0.4;
   private static final double CONST_doubleValue16 = 1.3;
   private static final int CONST_intValue6 = 6;
   private List<ReplayStrokes.Entry> library = new ArrayList<>();
   private CompletableFuture<List<ReplayStrokes.Entry>> completableFuture;
   private boolean loaded;
   private int intValue;
   private double doubleValue;
   private final ArrayDeque<ReplayStrokes.Entry> arrayDeque = new ArrayDeque<>();
   private double doubleValue2 = Double.NaN;
   private double doubleValue3;
   private double doubleValue4;
   private double doubleValue5 = Double.NaN;
   private boolean flag;
   private final ArrayDeque<double[]> arrayDeque2 = new ArrayDeque<>();
   private final List<double[]> items = new ArrayList<>();
   private double doubleValue6;
   private ReplayStrokes.Entry entry;
   private boolean flag2;
   private double doubleValue7;
   private double doubleValue8;
   private double doubleValue9;
   private double doubleValue10;
   private double doubleValue11;
   private double doubleValue12;
   private double doubleValue13;
   private double doubleValue14;
   private double doubleValue15;
   private double doubleValue16;
   private int intValue2;
   private double doubleValue17;
   private final double[] doubleValues = new double[5];
   private final double[] doubleValues2 = new double[2];
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private double doubleValue18 = Double.NaN;
   private double doubleValue19 = Double.NaN;

   private ReplayStrokes() {
   }

   public void method6649(MouseRotationEvent var1, LivingEntity var2, boolean var3, float var4, boolean var5, double var6) {
      double var8 = System.nanoTime() / 1000000.0;
      double var10 = Double.isNaN(this.doubleValue2) ? 16.0 : MathHelper.clamp(var8 - this.doubleValue2, 0.5, 100.0);
      this.doubleValue2 = var8;
      this.method6662();
      double var12 = var1.getDoubleValue() * 0.15;
      double var14 = var1.getDoubleValue2() * 0.15;
      double var16 = 1.0 - Math.exp(-var10 / 25.0);
      this.doubleValue3 = this.doubleValue3 + (var12 / var10 * 1000.0 - this.doubleValue3) * var16;
      this.doubleValue4 = this.doubleValue4 + (var14 / var10 * 1000.0 - this.doubleValue4) * var16;
      if (var12 != 0.0 || var14 != 0.0 || Double.isNaN(this.doubleValue5)) {
         this.doubleValue5 = var8;
      }

      double var18 = mc.player.getYaw();
      double var20 = mc.player.getPitch();
      boolean var22 = !mc.options.smoothCameraEnabled && !mc.player.isUsingSpyglass();
      if (!var22) {
         this.method6653(var8, false);
         this.method6657();
      } else {
         if (this.entry == null && var8 >= this.doubleValue17 && var2 != null && var8 - this.doubleValue5 >= var6) {
            this.method6650(var2, var4, var5, var18, var20);
         }

         if (this.entry != null) {
            this.method6652(var1, var2, var8, var10);
         } else {
            if (var3) {
               this.method6655(var8, var10, var18 + var12, var20 + var14, var2);
            } else {
               this.method6657();
            }
         }
      }
   }

   private void method6650(LivingEntity var1, float var2, boolean var3, double var4, double var6) {
      if (!this.library.isEmpty()) {
         method6659(var1, var4, var6, this.doubleValues);
         double var8 = this.doubleValues[0];
         double var10 = var3 ? this.doubleValues[1] + this.doubleValue * this.doubleValues[4] : 0.0;
         double var12 = var3 ? this.doubleValues[2] : Math.max(0.0, Math.abs(this.doubleValues[0]) - this.doubleValues[3]);
         double var14 = Math.hypot(var8, var10);
         if (!(var12 < 0.4) && !(Math.hypot(this.doubleValues[0], this.doubleValues[1]) > var2) && !(var14 < 0.001)) {
            ReplayStrokes.Entry var16 = this.method6651(var8, var10, var14);
            if (var16 == null) {
               this.intValue5++;
            } else {
               double var17 = var16.doubleValue3 * Math.signum(var8);
               double var19 = var16.doubleValue4;
               double var21 = this.doubleValues[0] + var17 * this.doubleValues[3];
               double var23 = var3 ? this.doubleValues[1] + var19 * this.doubleValues[4] : 0.0;
               double var25 = Math.hypot(var21, var23);
               if (!(var25 < 0.001)) {
                  double var27 = var25 / var16.doubleValue;
                  double var29 = Math.pow(var27, 0.3) * (0.94 + ThreadLocalRandom.current().nextDouble() * 0.12);
                  this.entry = var16;
                  this.flag2 = var3;
                  this.doubleValue7 = 0.0;
                  this.doubleValue8 = var29;
                  this.doubleValue9 = var17;
                  this.doubleValue10 = var19;
                  this.doubleValue13 = var21;
                  this.doubleValue14 = var23;
                  this.doubleValue11 = var4;
                  this.doubleValue12 = var6;
                  this.doubleValue15 = 0.0;
                  this.doubleValue16 = 0.0;
                  this.intValue2 = var1.getId();
                  this.arrayDeque.addLast(var16);

                  while (this.arrayDeque.size() > 6) {
                     this.arrayDeque.removeFirst();
                  }

                  this.method6657();
                  this.intValue3++;
                  this.doubleValue18 = var27;
                  this.doubleValue19 = Math.toDegrees(Math.abs(method6660(Math.atan2(var10, var8), var16.doubleValue2)));
               }
            }
         }
      }
   }

   private ReplayStrokes.Entry method6651(double var1, double var3, double var5) {
      double var7 = Math.atan2(var3, var1);
      ThreadLocalRandom var9 = ThreadLocalRandom.current();
      ReplayStrokes.Entry var10 = null;
      double var11 = Double.MAX_VALUE;

      for (ReplayStrokes.Entry var14 : this.library) {
         double var15 = var5 / var14.doubleValue;
         if (!(var15 < 0.4) && !(var15 > 2.5)) {
            double var17 = Math.toDegrees(Math.abs(method6660(var7, var14.doubleValue2)));
            if (!(var17 > 60.0)) {
               double var19 = Math.log(var15) / Math.log(1.6);
               double var21 = var17 / 30.0;
               double var23 = var19 * var19 + var21 * var21 + var9.nextDouble() * 0.6;
               if (this.arrayDeque.contains(var14)) {
                  var23++;
               }

               if (var23 < var11) {
                  var11 = var23;
                  var10 = var14;
               }
            }
         }
      }

      return var10;
   }

   private void method6652(MouseRotationEvent var1, LivingEntity var2, double var3, double var5) {
      if (var2 != null && var2.getId() == this.intValue2 && var2.isAlive()) {
         double var7 = this.flag2 ? Math.hypot(this.doubleValue3, this.doubleValue4) : Math.abs(this.doubleValue3);
         if (var7 >= 40.0) {
            this.intValue4++;
            this.method6653(var3, true);
         } else {
            this.doubleValue7 = this.doubleValue7 + var5 / this.doubleValue8;
            method6659(var2, this.doubleValue11, this.doubleValue12, this.doubleValues);
            double var9 = this.doubleValues[0] + this.doubleValue9 * this.doubleValues[3];
            double var11 = this.flag2 ? this.doubleValues[1] + this.doubleValue10 * this.doubleValues[4] : 0.0;
            double var13 = 1.0 - Math.exp(-var5 / 70.0);
            this.doubleValue13 = this.doubleValue13 + (var9 - this.doubleValue13) * var13;
            this.doubleValue14 = this.doubleValue14 + (var11 - this.doubleValue14) * var13;
            this.entry.method6669(this.doubleValue7, this.doubleValues2);
            double var15 = this.doubleValues2[0] * this.doubleValue13 - this.doubleValues2[1] * this.doubleValue14;
            double var17 = this.flag2 ? this.doubleValues2[0] * this.doubleValue14 + this.doubleValues2[1] * this.doubleValue13 : 0.0;
            double var19 = var15 - this.doubleValue15;
            double var21 = var17 - this.doubleValue16;
            double var23 = this.entry.doubleValue5 * Math.hypot(this.doubleValue13, this.doubleValue14) / this.doubleValue8 * 1.3 * var5;
            double var25 = Math.hypot(var19, var21);
            if (var25 > var23 && var25 > 0.0) {
               var19 *= var23 / var25;
               var21 *= var23 / var25;
            }

            double var27 = method6654();
            double var29 = Math.round(var19 / 0.15 / var27) * var27;
            double var31 = Math.round(var21 / 0.15 / var27) * var27;
            this.doubleValue15 += var29 * 0.15;
            this.doubleValue16 += var31 * 0.15;
            var1.setDoubleValue(var1.getDoubleValue() + var29);
            var1.setDoubleValue2(var1.getDoubleValue2() + var31);
            if (this.doubleValue7 >= this.entry.method6668()) {
               this.method6653(var3, true);
            }
         }
      } else {
         this.method6653(var3, false);
      }
   }

   private void method6653(double var1, boolean var3) {
      if (this.entry != null) {
         this.entry = null;
         if (var3) {
            this.doubleValue17 = var1 + 90.0 + ThreadLocalRandom.current().nextDouble() * 110.0;
         }

         this.method6657();
      }
   }

   private static double method6654() {
      double var0 = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
      return var0 * var0 * var0 * 8.0;
   }

   private void method6655(double var1, double var3, double var5, double var7, LivingEntity var9) {
      double var10 = Math.hypot(this.doubleValue3, this.doubleValue4);
      double[] var12 = new double[]{var1, var5, var7};
      if (this.flag) {
         this.items.add(var12);
         if (var1 - this.items.getFirst()[0] > 900.0) {
            this.method6657();
         } else {
            if (var10 < 10.0) {
               this.doubleValue6 += var3;
               if (this.doubleValue6 >= 60.0) {
                  this.method6656(var9);
                  this.method6657();
               }
            } else {
               this.doubleValue6 = 0.0;
            }
         }
      } else {
         this.arrayDeque2.addLast(var12);

         while (this.arrayDeque2.size() > 3) {
            this.arrayDeque2.removeFirst();
         }

         if (var10 >= 40.0) {
            this.flag = true;
            this.items.clear();
            this.items.addAll(this.arrayDeque2);
            this.arrayDeque2.clear();
            this.doubleValue6 = 0.0;
         }
      }
   }

   private void method6656(LivingEntity var1) {
      int var2 = this.items.size();
      if (var1 != null && var2 >= 4) {
         double[] var3 = this.items.getFirst();
         double[] var4 = this.items.get(var2 - 1);
         double var5 = var4[0] - var3[0];
         double var7 = var4[1] - var3[1];
         double var9 = var4[2] - var3[2];
         double var11 = Math.hypot(var7, var9);
         if (!(var5 < 50.0) && !(var11 < 1.0)) {
            method6659(var1, var3[1], var3[2], this.doubleValues);
            double var13 = this.doubleValues[2];
            method6659(var1, var4[1], var4[2], this.doubleValues);
            double var15 = this.doubleValues[2];
            boolean var17 = var15 <= 2.5 || var15 <= 0.3 * var13;
            boolean var18 = var13 - var15 >= 0.5 * var11;
            if (var17 && var18) {
               double var19 = -this.doubleValues[0] / Math.max(0.001, this.doubleValues[3]);
               double var21 = -this.doubleValues[1] / Math.max(0.001, this.doubleValues[4]);
               if (!(Math.abs(var19) > 3.0) && !(Math.abs(var21) > 3.0)) {
                  double var23 = var19 * (var7 >= 0.0 ? 1.0 : -1.0);
                  int var25 = Math.min(var2, 160);
                  float[] var26 = new float[var25];
                  float[] var27 = new float[var25];
                  float[] var28 = new float[var25];
                  double var29 = var7 / var11;
                  double var31 = var9 / var11;

                  for (int var33 = 0; var33 < var25; var33++) {
                     int var34 = var25 == var2 ? var33 : (int)Math.round((double)var33 * (var2 - 1) / (var25 - 1));
                     double[] var35 = this.items.get(var34);
                     double var36 = var35[1] - var3[1];
                     double var38 = var35[2] - var3[2];
                     var26[var33] = (float)(var35[0] - var3[0]);
                     var27[var33] = (float)((var36 * var29 + var38 * var31) / var11);
                     var28[var33] = (float)((-var36 * var31 + var38 * var29) / var11);
                  }

                  this.library.add(new ReplayStrokes.Entry(var11, Math.atan2(var9, var7), var23, var21, var26, var27, var28));
                  if (this.library.size() > 600) {
                     this.library.removeFirst();
                  }

                  this.method6658();
                  this.intValue6++;
                  if (++this.intValue >= 8) {
                     this.save();
                  }
               }
            }
         }
      }
   }

   private void method6657() {
      this.flag = false;
      this.items.clear();
      this.arrayDeque2.clear();
      this.doubleValue6 = 0.0;
   }

   private void method6658() {
      double var1 = 0.0;

      for (ReplayStrokes.Entry var4 : this.library) {
         var1 += var4.doubleValue4;
      }

      this.doubleValue = this.library.isEmpty() ? 0.0 : var1 / this.library.size();
   }

   private static void method6659(LivingEntity var0, double var1, double var3, double[] var5) {
      float var6 = mc.getRenderTickCounter().getTickProgress(true);
      Vec3d var7 = mc.player.getCameraPosVec(var6);
      Vec3d var8 = var0.getLerpedPos(var6);
      Box var9 = var0.getBoundingBox().offset(var8.x - var0.getX(), var8.y - var0.getY(), var8.z - var0.getZ());
      Vec3d var10 = var9.getCenter();
      Rotation var11 = RotationUtils.method6912(var10.subtract(var7));
      double var12 = MathHelper.wrapDegrees(var11.getFloatValue() - var1);
      double var14 = var11.getFloatValue2() - var3;
      double var16 = Math.max(0.3, var7.distanceTo(var10));
      double var18 = Math.toDegrees(Math.atan(var9.getLengthX() * 0.5 / var16));
      double var20 = Math.toDegrees(Math.atan(var9.getLengthY() * 0.5 / var16));
      var5[0] = var12;
      var5[1] = var14;
      var5[2] = Math.hypot(Math.max(0.0, Math.abs(var12) - var18), Math.max(0.0, Math.abs(var14) - var20));
      var5[3] = var18;
      var5[4] = var20;
   }

   private static double method6660(double var0, double var2) {
      double var4 = var0 - var2;

      while (var4 > Math.PI) {
         var4 -= Math.PI * 2;
      }

      while (var4 < -Math.PI) {
         var4 += Math.PI * 2;
      }

      return var4;
   }

   private static Path method6661() {
      return Zenith.file.toPath().resolve("assist").resolve("replay_strokes.json");
   }

   private void method6662() {
      if (this.completableFuture == null) {
         this.completableFuture = CompletableFuture.supplyAsync(ReplayStrokes::method6663);
      } else {
         if (this.completableFuture.isDone() && !this.completableFuture.isCompletedExceptionally()) {
            var var1 = this.completableFuture.getNow(null);
            if (var1 != null) {
               var1.addAll(this.library);

               while (var1.size() > 600) {
                  var1.removeFirst();
               }

               this.library = var1;
               this.loaded = true;
               this.method6658();
               this.completableFuture = CompletableFuture.completedFuture(null);
            }
         }
      }
   }

   private static List<ReplayStrokes.Entry> method6663() {
      ArrayList<ReplayStrokes.Entry> var0 = new ArrayList<>();
      Path var1 = method6661();
      if (!Files.isRegularFile(var1)) {
         return var0;
      } else {
         try {
            label73: {
               ArrayList var13;
               try (BufferedReader var2 = Files.newBufferedReader(var1, StandardCharsets.UTF_8)) {
                  JsonObject var3 = JsonParser.parseReader(var2).getAsJsonObject();
                  if (var3.get("format").getAsInt() == 2) {
                     for (JsonElement var5 : var3.getAsJsonArray("strokes")) {
                        JsonObject var6 = var5.getAsJsonObject();
                        float[] var7 = method6664(var6.getAsJsonArray("t"));
                        float[] var8 = method6664(var6.getAsJsonArray("u"));
                        float[] var9 = method6664(var6.getAsJsonArray("w"));
                        if (var7.length >= 2 && var7.length == var8.length && var7.length == var9.length) {
                           var0.add(
                              new ReplayStrokes.Entry(
                                 var6.get("amp").getAsDouble(),
                                 var6.get("dir").getAsDouble(),
                                 var6.get("fx").getAsDouble(),
                                 var6.get("fy").getAsDouble(),
                                 var7,
                                 var8,
                                 var9
                              )
                           );
                        }
                     }
                     break label73;
                  }

                  var13 = var0;
               }

               return var13;
            }
         } catch (Exception var12) {
            System.err.println("[ReplayAssist] не прочитан " + var1 + ": " + var12);
            var0.clear();
         }

         return var0;
      }
   }

   private static float[] method6664(JsonArray var0) {
      float[] var1 = new float[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0.get(var2).getAsFloat();
      }

      return var1;
   }

   public void save() {
      if (this.loaded) {
         this.intValue = 0;
         var var1 = new ArrayList<>(this.library);
         CompletableFuture.runAsync(() -> method6665(var1));
      }
   }

   private static synchronized void method6665(List<ReplayStrokes.Entry> var0) {
      Path var1 = method6661();

      try {
         Files.createDirectories(var1.getParent());
         Path var2 = var1.resolveSibling("replay_strokes.json.tmp");

         try (BufferedWriter var3 = Files.newBufferedWriter(var2, StandardCharsets.UTF_8)) {
            JsonWriter var4 = new JsonWriter(var3);

            try {
               var4.beginObject();
               var4.name("format").value(2L);
               var4.name("strokes").beginArray();

               for (ReplayStrokes.Entry var6 : var0) {
                  var4.beginObject();
                  var4.name("amp").value(round(var6.doubleValue));
                  var4.name("dir").value(round(var6.doubleValue2));
                  var4.name("fx").value(round(var6.doubleValue3));
                  var4.name("fy").value(round(var6.doubleValue4));
                  method6666(var4, "t", var6.floatValues);
                  method6666(var4, "u", var6.floatValues2);
                  method6666(var4, "w", var6.floatValues3);
                  var4.endObject();
               }

               var4.endArray();
               var4.endObject();
            } catch (Throwable var9) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            var4.close();
         }

         Files.move(var2, var1, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception var11) {
         System.err.println("[ReplayAssist] не записан " + var1 + ": " + var11);
      }
   }

   private static void method6666(JsonWriter var0, String var1, float[] var2) throws IOException {
      var0.name(var1).beginArray();

      for (float var6 : var2) {
         var0.value(round(var6));
      }

      var0.endArray();
   }

   private static double round(double var0) {
      return Math.round(var0 * 10000.0) / 10000.0;
   }

   public int size() {
      return this.library.size();
   }

   public void clear() {
      this.library = new ArrayList<>();
      this.arrayDeque.clear();
      this.doubleValue = 0.0;
      this.completableFuture = CompletableFuture.completedFuture(null);
      this.loaded = true;
      this.entry = null;
      this.method6657();
      this.save();
   }

   public void reset() {
      this.entry = null;
      this.doubleValue17 = 0.0;
      this.doubleValue2 = Double.NaN;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.doubleValue5 = Double.NaN;
      this.method6657();
   }

   public void shutdown() {
      this.reset();
      if (this.intValue > 0) {
         this.save();
      }
   }

   public String method6667() {
      String var1 = String.format(
         Locale.ROOT,
         "replay lib=%d rec+%d played=%d yielded=%d nomatch=%d scale=%.2f ang=%.0f fy=%.2f%s",
         this.library.size(),
         this.intValue6,
         this.intValue3,
         this.intValue4,
         this.intValue5,
         this.doubleValue18,
         this.doubleValue19,
         this.doubleValue,
         this.entry != null ? " [playing]" : ""
      );
      this.intValue6 = 0;
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.intValue5 = 0;
      return var1;
   }

   private static final class Entry {
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final double doubleValue4;
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float[] floatValues3;
      public final double doubleValue5;

      public Entry(double var1, double var3, double var5, double var7, float[] var9, float[] var10, float[] var11) {
         this.doubleValue = var1;
         this.doubleValue2 = var3;
         this.doubleValue3 = var5;
         this.doubleValue4 = var7;
         this.floatValues = var9;
         this.floatValues2 = var10;
         this.floatValues3 = var11;
         double var12 = 0.0;

         for (int var14 = 1; var14 < var9.length; var14++) {
            double var15 = var9[var14] - var9[var14 - 1];
            if (var15 > 0.0) {
               var12 = Math.max(var12, Math.hypot(var10[var14] - var10[var14 - 1], var11[var14] - var11[var14 - 1]) / var15);
            }
         }

         this.doubleValue5 = var12;
      }

      public double method6668() {
         return this.floatValues[this.floatValues.length - 1];
      }

      public void method6669(double var1, double[] var3) {
         int var4 = this.floatValues.length;
         if (var1 <= this.floatValues[0]) {
            var3[0] = this.floatValues2[0];
            var3[1] = this.floatValues3[0];
         } else if (var1 >= this.floatValues[var4 - 1]) {
            var3[0] = this.floatValues2[var4 - 1];
            var3[1] = this.floatValues3[var4 - 1];
         } else {
            int var5 = 0;
            int var6 = var4 - 1;

            while (var6 - var5 > 1) {
               int var7 = var5 + var6 >>> 1;
               if (this.floatValues[var7] <= var1) {
                  var5 = var7;
               } else {
                  var6 = var7;
               }
            }

            double var11 = this.floatValues[var6] - this.floatValues[var5];
            double var9 = var11 > 0.0 ? (var1 - this.floatValues[var5]) / var11 : 0.0;
            var3[0] = this.floatValues2[var5] + (this.floatValues2[var6] - this.floatValues2[var5]) * var9;
            var3[1] = this.floatValues3[var5] + (this.floatValues3[var6] - this.floatValues3[var5]) * var9;
         }
      }
   }
}

package org.zenith.base.rotation.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public final class Motor extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final String text = "motor.json";
   private static final String text2 = "models/motor.json";
   private static final List<Path> items = List.of(Path.of("motor.json"), Path.of("models", "motor.json"), Path.of("..", "models", "motor.json"));
   private static final String text3 = "neurotest-motor-v1";
   private static final int CONST_intValue2 = 47;
   private static final int CONST_intValue3 = 11;
   private static final double CONST_doubleValue = 1.0E-9;
   private static final double CONST_doubleValue2 = 1.0;
   private static final boolean CONST_flag = false;
   private static final double CONST_doubleValue3 = 0.0;
   private static final double CONST_doubleValue4 = 0.35;
   private static final double CONST_doubleValue5 = 20.0;
   private static final boolean CONST_flag2 = false;
   private static final int CONST_intValue4 = 8;
   private Motor.Entry4 entry4;
   private boolean flag;
   private CompletableFuture<Motor.Entry4> completableFuture;
   private long longValue;
   private final Random random = new Random();
   private double[][] doubleValues;
   private final Motor.Entry[] entrys = new Motor.Entry[8];
   private int intValue;
   private final double[] doubleValues2 = new double[2];
   private final double[] doubleValues3 = new double[2];
   private final double[] doubleValues4 = new double[2];
   private final double[] doubleValues5 = new double[2];
   private final double[] doubleValues6 = new double[2];
   private final double[] doubleValues7 = new double[2];
   private final double[] doubleValues8 = new double[2];
   private float floatValue = 1.0F;
   private boolean flag2;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4 = Integer.MIN_VALUE;
   private Rotation rotation;
   public float offset = 0.0F;

   public Rotation method3659() {
      Rotation var1 = rotationManager.method7844();
      LivingEntity var2 = AuraModule.INSTANCE.method6358();
      if (mc.player != null && var2 != null && var2.isAlive()) {
         Motor.Entry4 var3 = this.method3673();
         if (var3 == null) {
            return var1;
         } else {
            int var4 = mc.player.age;
            if (var4 == this.intValue4 && this.rotation != null) {
               return this.rotation;
            } else {
               if (this.intValue3 != Integer.MIN_VALUE && var4 != this.intValue3 + 1) {
                  this.method3670(var2.getId(), var4);
               } else if (var2.getId() != this.intValue2) {
                  this.method3671(var2.getId());
               }

               this.intValue3 = var4;
               float var5 = Rotation.method3138();
               if (Float.isFinite(var5) && !(var5 <= 0.0F)) {
                  this.method3669();
                  Motor.Entry var6 = this.method3661(var1, var2);
                  if (var6 == null) {
                     this.method3672(var4, var1);
                     return var1;
                  } else {
                     this.method3662(var6);
                     double[] var7 = this.method3664(var3, var1);
                     if (!method3679(var7)) {
                        this.method3672(var4, var1);
                        return var1;
                     } else {
                        Motor.Entry5 var8 = var3.method3683(var7, this.doubleValues);
                        double[] var9 = this.method3665(var3, var8);
                        double var10 = var6.doubleValue3 - var1.getFloatValue();
                        var10 = MathHelper.wrapDegrees((float)var10);
                        double var12 = var6.doubleValue4 - var1.getFloatValue2();
                        boolean var14 = false;
                        if (var14) {
                           var9[0] = method3677(0.35 * var10, -20.0, 20.0);
                           var9[1] = method3677(0.35 * var12, -20.0, 20.0);
                        }

                        if (var3.method3681()) {
                           for (int var15 = 0; var15 < 2; var15++) {
                              double var16 = Math.max(Math.abs(this.doubleValues2[var15]), Math.abs(this.doubleValues3[var15])) / var5;
                              double var18 = var3.method3682(var15, var16) * var5;
                              var9[var15] = method3677(var9[var15], -var18, var18);
                           }
                        }

                        int var23 = this.method3667(var9[0], 0, var5, var3.flag);
                        int var24 = this.method3667(var9[1], 1, var5, var3.flag);
                        Rotation var17 = var1.method3140(var23 * var5, var24 * var5);
                        double var25 = MathHelper.wrapDegrees(var17.getFloatValue() - var1.getFloatValue());
                        double var20 = var17.getFloatValue2() - var1.getFloatValue2();
                        this.method3668(var25, var20);
                        if (var23 == 0 && var24 == 0) {
                           this.method3672(var4, var1);
                           return var1;
                        } else {
                           this.method3672(var4, var17);
                           return var17;
                        }
                     }
                  }
               } else {
                  this.method3672(var4, var1);
                  return var1;
               }
            }
         }
      } else {
         this.reset();
         if (mc.player != null) {
            mc.player.setYaw(var1.getFloatValue());
            mc.player.setPitch(var1.getFloatValue2());
         }

         return var1;
      }
   }

   public boolean method3660() {
      return this.method3673() != null;
   }

   private Motor.Entry method3661(Rotation var1, LivingEntity var2) {
      Vec3d var3 = mc.player.getEyePos();
      Box var4 = var2.getBoundingBox();
      Vec3d var5 = var4.getCenter();
      double var6 = var5.x - var3.x;
      double var8 = var5.y - var3.y;
      double var10 = var5.z - var3.z;
      if (var6 * var6 + var8 * var8 + var10 * var10 < 1.0E-10) {
         return null;
      } else {
         Motor.Entry var12 = new Motor.Entry();
         var12.doubleValue = var1.getFloatValue();
         var12.doubleValue2 = var1.getFloatValue2();
         var12.doubleValue3 = Math.toDegrees(Math.atan2(-var6, var10));
         var12.doubleValue4 = Math.toDegrees(Math.atan2(-var8, Math.hypot(var6, var10)));
         var12.doubleValue9 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
         double var13 = Double.MAX_VALUE;
         double var15 = -Double.MAX_VALUE;
         double var17 = Double.MAX_VALUE;
         double var19 = -Double.MAX_VALUE;

         for (int var21 = 0; var21 < 8; var21++) {
            double var22 = ((var21 & 1) == 0 ? var4.minX : var4.maxX) - var3.x;
            double var24 = ((var21 & 2) == 0 ? var4.minY : var4.maxY) - var3.y;
            double var26 = ((var21 & 4) == 0 ? var4.minZ : var4.maxZ) - var3.z;
            double var28 = Math.toDegrees(Math.atan2(-var22, var26));
            double var30 = Math.toDegrees(Math.atan2(-var24, Math.hypot(var22, var26)));
            if (Double.isFinite(var28) && Double.isFinite(var30)) {
               double var32 = MathHelper.wrapDegrees(var28 - var12.doubleValue3);
               double var34 = var30 - var12.doubleValue4;
               var13 = Math.min(var13, var32);
               var15 = Math.max(var15, var32);
               var17 = Math.min(var17, var34);
               var19 = Math.max(var19, var34);
            }
         }

         if (var13 == Double.MAX_VALUE) {
            return null;
         } else {
            var12.doubleValue5 = var13;
            var12.doubleValue6 = var15;
            var12.doubleValue7 = var17;
            var12.doubleValue8 = var19;
            return var12;
         }
      }
   }

   private void method3662(Motor.Entry var1) {
      System.arraycopy(this.entrys, 0, this.entrys, 1, 7);
      this.entrys[0] = var1;
      if (this.intValue < 8) {
         this.intValue++;
      }
   }

   private Motor.Entry method3663(int var1) {
      return this.entrys[Math.min(var1, this.intValue - 1)];
   }

   private double[] method3664(Motor.Entry4 var1, Rotation var2) {
      int var3 = var1.intValue5;
      Motor.Entry var4 = this.method3663(var3);
      Motor.Entry var5 = this.method3663(var3 + 1);
      Motor.Entry var6 = this.method3663(var3 + 2);
      double var7 = var1.doubleValue;
      double var9 = var2.getFloatValue();
      double var11 = var2.getFloatValue2();
      double var13 = method3675(var4.doubleValue3 - var9);
      double var15 = var4.doubleValue4 - var11;
      double var17 = method3675(var4.doubleValue3 - var4.doubleValue);
      double var19 = var4.doubleValue4 - var4.doubleValue2;
      double var21 = method3675(var4.doubleValue3 - var5.doubleValue3);
      double var23 = var4.doubleValue4 - var5.doubleValue4;
      double var25 = var21 - method3675(var5.doubleValue3 - var6.doubleValue3);
      double var27 = var23 - (var5.doubleValue4 - var6.doubleValue4);
      double var29 = method3675(var4.doubleValue3 + (var3 + 1) * var21 - var9);
      double var31 = var4.doubleValue4 + (var3 + 1) * var23 - var11;
      double var33 = Math.max(Math.hypot(var13, var15), 1.0E-6);
      double var35 = 0.5 * (var4.doubleValue5 + var4.doubleValue6);
      double var37 = 0.5 * (var4.doubleValue7 + var4.doubleValue8);
      double var39 = Math.max(0.5 * (var4.doubleValue6 - var4.doubleValue5), 0.001);
      double var41 = Math.max(0.5 * (var4.doubleValue8 - var4.doubleValue7), 0.001);
      double var43 = (-var13 - var35) / var39;
      double var45 = (-var15 - var37) / var41;
      double var47 = method3676(var1.doubleValue4 * (1.0 - Math.max(Math.abs(var43), Math.abs(var45))));
      double[] var49 = new double[var1.intValue];
      int var50 = 0;
      var49[var50++] = asinh(var13 / var7);
      var49[var50++] = asinh(var15 / var7);
      var49[var50++] = asinh(var17 / var7);
      var49[var50++] = asinh(var19 / var7);
      var49[var50++] = asinh(var29 / var7);
      var49[var50++] = asinh(var31 / var7);
      var49[var50++] = asinh(var33 / var7);
      var49[var50++] = var13 / var33;
      var49[var50++] = var15 / var33;
      var49[var50++] = asinh(var21 / var7);
      var49[var50++] = asinh(var23 / var7);
      var49[var50++] = asinh(var25 / var7);
      var49[var50++] = asinh(var27 / var7);
      var49[var50++] = method3677(var43, -4.0, 4.0);
      var49[var50++] = method3677(var45, -4.0, 4.0);
      var49[var50++] = var47;
      var49[var50++] = asinh(var39 / var7);
      var49[var50++] = asinh(var41 / var7);
      var49[var50++] = Math.log1p(var4.doubleValue9);
      var49[var50++] = method3677(var4.doubleValue9 - var5.doubleValue9, -2.0, 2.0);
      var49[var50++] = var11 / 90.0;
      var49[var50++] = (90.0 - Math.abs(var11)) / 90.0;
      var49[var50++] = asinh(this.doubleValues2[0] / var7);
      var49[var50++] = asinh(this.doubleValues2[1] / var7);
      var49[var50++] = asinh(this.doubleValues3[0] / var7);
      var49[var50++] = asinh(this.doubleValues3[1] / var7);
      var49[var50++] = asinh((this.doubleValues2[0] - this.doubleValues3[0]) / var7);
      var49[var50++] = asinh((this.doubleValues2[1] - this.doubleValues3[1]) / var7);
      var49[var50++] = Math.tanh(this.doubleValues4[0] / 8.0);
      var49[var50++] = Math.tanh(this.doubleValues4[1] / 8.0);
      var49[var50++] = Math.tanh(this.doubleValues5[0] / 8.0);
      var49[var50++] = Math.tanh(this.doubleValues5[1] / 8.0);
      var49[var50++] = method3677(Math.abs(this.doubleValues2[0]) / Math.max(this.doubleValues6[0], 1.0E-6), 0.0, 4.0);
      var49[var50++] = method3677(Math.abs(this.doubleValues2[1]) / Math.max(this.doubleValues6[1], 1.0E-6), 0.0, 4.0);
      var49[var50++] = asinh(this.doubleValues6[0] / var7);
      var49[var50++] = asinh(this.doubleValues6[1] / var7);
      var49[var50++] = mc.options.forwardKey.isPressed() ? 1.0 : 0.0;
      var49[var50++] = mc.options.backKey.isPressed() ? 1.0 : 0.0;
      var49[var50++] = mc.options.leftKey.isPressed() ? 1.0 : 0.0;
      var49[var50++] = mc.options.rightKey.isPressed() ? 1.0 : 0.0;
      var49[var50++] = mc.options.jumpKey.isPressed() ? 1.0 : 0.0;
      var49[var50++] = mc.player.isOnGround() ? 1.0 : 0.0;
      var49[var50++] = mc.player.isSprinting() ? 1.0 : 0.0;
      var49[var50++] = mc.player.isInSneakingPose() ? 1.0 : 0.0;
      var49[var50++] = mc.player.getAttackCooldownProgress(0.0F);
      var49[var50++] = MathHelper.clamp((float)mc.player.ticksSinceLastAttack, 0.0F, 50.0F);
      var49[var50] = this.flag2 ? 1.0 : 0.0;
      return var49;
   }

   private double[] method3665(Motor.Entry4 var1, Motor.Entry5 var2) {
      boolean var3 = this.random.nextDouble() < method3676(var2.doubleValue);
      if (var3) {
         return new double[]{0.0, 0.0};
      } else {
         int var4 = this.method3666(var2.doubleValues);
         double var9 = this.random.nextGaussian();
         double var11 = this.random.nextGaussian();
         double var13 = Math.exp(var2.doubleValues3[var4][0]) * 1.0;
         double var15 = Math.exp(var2.doubleValues3[var4][1]) * 1.0;
         double var17 = var2.doubleValues4[var4];
         double var5 = var2.doubleValues2[var4][0] + var13 * var9;
         double var7 = var2.doubleValues2[var4][1] + var15 * (var17 * var9 + Math.sqrt(Math.max(1.0 - var17 * var17, 1.0E-6)) * var11);
         var5 = method3677(var5, -var1.doubleValue11, var1.doubleValue11);
         var7 = method3677(var7, -var1.doubleValue12, var1.doubleValue12);
         var9 = method3677(var1.doubleValue2 * Math.sinh(var5), -var1.doubleValue5, var1.doubleValue5);
         var11 = method3677(var1.doubleValue3 * Math.sinh(var7), -var1.doubleValue6, var1.doubleValue6);
         return new double[]{var9, var11};
      }
   }

   private int method3666(double[] var1) {
      double var2 = Double.NEGATIVE_INFINITY;

      for (double var7 : var1) {
         var2 = Math.max(var2, var7);
      }

      double var12 = 0.0;
      double[] var13 = new double[var1.length];

      for (int var14 = 0; var14 < var1.length; var14++) {
         var13[var14] = Math.exp(var1[var14] - var2);
         var12 += var13[var14];
      }

      double var15 = this.random.nextDouble() * var12;
      double var9 = 0.0;

      for (int var11 = 0; var11 < var13.length; var11++) {
         var9 += var13[var11];
         if (var15 <= var9) {
            return var11;
         }
      }

      return var13.length - 1;
   }

   private int method3667(double var1, int var3, double var4, boolean var6) {
      double var7 = var1 + (this.entry4.flag2 ? this.doubleValues8[var3] : 0.0);
      double var9 = var7 / var4;
      var9 += Math.signum(var9) * 1.0E-4;
      long var11 = var6 ? Math.round(var9) : (long)var9;
      double var13 = var11 * var4;
      this.doubleValues8[var3] = this.entry4.flag2 ? method3677(var7 - var13, -var4, var4) : 0.0;
      return (int)var11;
   }

   private void method3668(double var1, double var3) {
      double[] var5 = new double[]{var1, var3};

      for (int var6 = 0; var6 < 2; var6++) {
         double var7 = var5[var6];
         boolean var9 = Math.abs(var7) > this.entry4.doubleValue8;
         double var10 = Math.signum(var7);
         boolean var12 = var9 && var10 * this.doubleValues7[var6] < 0.0;
         this.doubleValues4[var6] = var9 ? 0.0 : this.doubleValues4[var6] + 1.0;
         this.doubleValues5[var6] = var12 ? 0.0 : this.doubleValues5[var6] + 1.0;
         this.doubleValues6[var6] = Math.max(this.doubleValues6[var6] * this.entry4.doubleValue7, Math.abs(var7));
         if (var9) {
            this.doubleValues7[var6] = var10;
         }

         this.doubleValues3[var6] = this.doubleValues2[var6];
         this.doubleValues2[var6] = var7;
      }
   }

   private void method3669() {
      float var1 = mc.player.getAttackCooldownProgress(0.0F);
      this.flag2 = var1 < this.floatValue - 1.0E-4F;
      this.floatValue = var1;
   }

   private void method3670(int var1, int var2) {
      Motor.Entry4 var3 = this.entry4;
      this.doubleValues = var3 == null ? null : new double[var3.layers][var3.intValue3];
      Arrays.fill(this.entrys, null);
      this.intValue = 0;
      Arrays.fill(this.doubleValues2, 0.0);
      Arrays.fill(this.doubleValues3, 0.0);
      Arrays.fill(this.doubleValues4, 0.0);
      Arrays.fill(this.doubleValues5, 0.0);
      Arrays.fill(this.doubleValues6, 0.0);
      Arrays.fill(this.doubleValues7, 0.0);
      Arrays.fill(this.doubleValues8, 0.0);
      this.floatValue = 1.0F;
      this.flag2 = false;
      this.intValue2 = var1;
      this.intValue3 = Integer.MIN_VALUE;
      this.intValue4 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3671(int var1) {
      this.intValue2 = var1;
      this.intValue4 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3672(int var1, Rotation var2) {
      this.intValue4 = var1;
      this.rotation = var2;
   }

   public void reset() {
      this.method3670(Integer.MIN_VALUE, 0);
   }

   private Motor.Entry4 method3673() {
      if (this.entry4 != null || this.flag) {
         return this.entry4;
      } else if (this.completableFuture == null) {
         this.longValue = System.currentTimeMillis();
         this.completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
               Motor.Entry4 var0 = method3674();
               var0.method3684();
               return var0;
            } catch (IOException var1) {
               throw new CompletionException(var1);
            }
         });
         return null;
      } else if (!this.completableFuture.isDone()) {
         return null;
      } else {
         try {
            this.entry4 = this.completableFuture.join();
            this.doubleValues = new double[this.entry4.layers][this.entry4.intValue3];
            System.out
               .println(
                  "v4 motor loaded: "
                     + this.entry4.intValue
                     + " признаков, GRU "
                     + this.entry4.layers
                     + "x"
                     + this.entry4.intValue3
                     + ", смесь "
                     + this.entry4.intValue4
                     + ", latency "
                     + this.entry4.intValue5
                     + ", масштаб действия "
                     + this.entry4.doubleValue2
                     + "/"
                     + this.entry4.doubleValue3
                     + ", огибающая "
                     + (this.entry4.method3681() ? this.entry4.doubleValues.length + " корзин" : "выключена")
                     + ", эталон сошёлся, "
                     + (System.currentTimeMillis() - this.longValue)
                     + " мс"
               );
         } catch (Exception var3) {
            Object var2 = var3 instanceof CompletionException && var3.getCause() != null ? var3.getCause() : var3;
            this.entry4 = null;
            this.flag = true;
            System.out.println("v4 не смог загрузить motor.json: " + var2);
         }

         this.completableFuture = null;
         return this.entry4;
      }
   }

   private static Motor.Entry4 method3674() throws IOException {
      for (Path var1 : items) {
         if (Files.isRegularFile(var1)) {
            Motor.Entry4 var2 = Motor.Entry4.method3687(var1);
            System.out.println("v4 motor: " + var1.toAbsolutePath());
            return var2;
         }
      }

      InputStream var6 = Motor.class.getClassLoader().getResourceAsStream("models/motor.json");
      if (var6 == null) {
         throw new IOException("motor.json не найден ни рядом с клиентом, ни в jar");
      } else {
         Motor.Entry4 var8;
         try (InputStreamReader var7 = new InputStreamReader(var6, StandardCharsets.UTF_8)) {
            var8 = Motor.Entry4.method3688(var7);
         }

         return var8;
      }
   }

   private static double method3675(double var0) {
      return MathHelper.wrapDegrees((float)var0);
   }

   private static double method3676(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   private static double asinh(double var0) {
      return var0 >= 0.0 ? Math.log(var0 + Math.sqrt(var0 * var0 + 1.0)) : -Math.log(-var0 + Math.sqrt(var0 * var0 + 1.0));
   }

   private static double method3677(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   private static int method3678(double[] var0) {
      int var1 = 0;

      for (int var2 = 1; var2 < var0.length; var2++) {
         if (var0[var2] > var0[var1]) {
            var1 = var2;
         }
      }

      return var1;
   }

   private static boolean method3679(double[] var0) {
      for (double var4 : var0) {
         if (!Double.isFinite(var4)) {
            return false;
         }
      }

      return true;
   }

   private static String method3680(double var0) {
      return String.format(Locale.ROOT, "%.3f", var0);
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
   }

   private static final class Entry2 {
      public final double[][] doubleValues;
      public final double[][] doubleValues2;
      public final double[] doubleValues3;
      public final double[] doubleValues4;

      public Entry2(double[][] var1, double[][] var2, double[] var3, double[] var4) {
         this.doubleValues = var1;
         this.doubleValues2 = var2;
         this.doubleValues3 = var3;
         this.doubleValues4 = var4;
      }
   }

   private static final class Entry3 {
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[][] doubleValues3;
      public double[][] doubleValues4;
      public double[] doubleValues5;
      public double doubleValue;
      public double[] doubleValues6;
   }

   private static final class Entry4 {
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int layers;
      public int intValue4;
      public int intValue5;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public boolean flag;
      public boolean flag2 = true;
      public double doubleValue9 = -7.0;
      public double doubleValue10 = 2.0;
      public boolean flag3;
      public double doubleValue11;
      public double doubleValue12;
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public Motor.Entry6[] entry6s;
      public Motor.Entry2[] entry2s;
      public Motor.Entry6 entry6;
      public Motor.Entry6 entry62;
      public Motor.Entry6 entry63;
      public Motor.Entry6 entry64;
      public Motor.Entry6 entry65;
      public final List<Motor.Entry3> items = new ArrayList<>();

      public boolean method3681() {
         return this.doubleValues != null && this.doubleValues2 != null && this.doubleValues3 != null;
      }

      public double method3682(int var1, double var2) {
         int var4 = 0;

         while (var4 < this.doubleValues.length && this.doubleValues[var4] < var2) {
            var4++;
         }

         double[] var5 = var1 == 0 ? this.doubleValues2 : this.doubleValues3;
         return var5[Math.min(var4, var5.length - 1)];
      }

      public Motor.Entry5 method3683(double[] var1, double[][] var2) {
         double[] var3 = new double[var1.length];

         for (int var4 = 0; var4 < var3.length; var4++) {
            var3[var4] = (var1[var4] - this.doubleValues4[var4]) / Math.sqrt(this.doubleValues5[var4] + 1.0E-4);
         }

         for (Motor.Entry6 var7 : this.entry6s) {
            var3 = var7.method3698(var3);

            for (int var8 = 0; var8 < var3.length; var8++) {
               var3[var8] /= 1.0 + Math.exp(-var3[var8]);
            }
         }

         for (int var18 = 0; var18 < this.entry2s.length; var18++) {
            Motor.Entry2 var20 = this.entry2s[var18];
            double[] var22 = var2[var18];
            double[] var24 = method3686(var20.doubleValues, var3, var20.doubleValues3);
            double[] var26 = method3686(var20.doubleValues2, var22, var20.doubleValues4);
            double[] var9 = new double[this.intValue3];

            for (int var10 = 0; var10 < this.intValue3; var10++) {
               double var11 = Motor.method3676(var24[var10] + var26[var10]);
               double var13 = Motor.method3676(var24[this.intValue3 + var10] + var26[this.intValue3 + var10]);
               double var15 = Math.tanh(var24[2 * this.intValue3 + var10] + var11 * var26[2 * this.intValue3 + var10]);
               var9[var10] = (1.0 - var13) * var15 + var13 * var22[var10];
            }

            var2[var18] = var9;
            var3 = var9;
         }

         double[] var19 = this.entry62.method3698(var3);
         double[] var21 = this.entry63.method3698(var3);
         double[][] var23 = new double[this.intValue4][2];
         double[][] var25 = new double[this.intValue4][2];

         for (int var27 = 0; var27 < this.intValue4; var27++) {
            var23[var27][0] = var19[2 * var27];
            var23[var27][1] = var19[2 * var27 + 1];
            var25[var27][0] = Motor.method3677(var21[2 * var27], this.doubleValue9, this.doubleValue10);
            var25[var27][1] = Motor.method3677(var21[2 * var27 + 1], this.doubleValue9, this.doubleValue10);
         }

         double[] var28 = this.entry64.method3698(var3);
         double[] var29 = new double[this.intValue4];

         for (int var30 = 0; var30 < this.intValue4; var30++) {
            var29[var30] = Math.tanh(var28[var30]) * 0.95;
         }

         return new Motor.Entry5(this.entry6.method3698(var3), var23, var25, var29, this.entry65.method3698(var3)[0]);
      }

      public void method3684() {
         if (this.items.isEmpty()) {
            throw new IllegalStateException("в модели нет эталонных тиков");
         } else {
            double[][] var1 = new double[this.layers][this.intValue3];

            for (int var2 = 0; var2 < this.items.size(); var2++) {
               Motor.Entry3 var3 = this.items.get(var2);
               if (var3.doubleValues.length != this.intValue) {
                  throw new IllegalStateException("эталон " + var2 + ": " + var3.doubleValues.length + " признаков вместо " + this.intValue);
               }

               Motor.Entry5 var4 = this.method3683(var3.doubleValues, var1);
               double[] var5 = new double[this.layers * this.intValue3];

               for (int var6 = 0; var6 < this.layers; var6++) {
                  System.arraycopy(var1[var6], 0, var5, var6 * this.intValue3, this.intValue3);
               }

               double var17 = 0.0;
               String var8 = "";

               for (int var9 = 0; var9 < this.intValue4; var9++) {
                  double var10 = method3685(var4.doubleValues2[var9], var3.doubleValues3[var9]);
                  if (var10 > var17) {
                     var17 = var10;
                     var8 = "mu[" + var9 + "]";
                  }

                  double var12 = method3685(var4.doubleValues3[var9], var3.doubleValues4[var9]);
                  if (var12 > var17) {
                     var17 = var12;
                     var8 = "log_sigma[" + var9 + "]";
                  }
               }

               double var18 = method3685(var4.doubleValues, var3.doubleValues2);
               if (var18 > var17) {
                  var17 = var18;
                  var8 = "pi";
               }

               double var11 = method3685(var4.doubleValues4, var3.doubleValues5);
               if (var11 > var17) {
                  var17 = var11;
                  var8 = "rho";
               }

               double var13 = Math.abs(var4.doubleValue - var3.doubleValue);
               if (var13 > var17) {
                  var17 = var13;
                  var8 = "pause";
               }

               double var15 = method3685(var5, var3.doubleValues6);
               if (var15 > var17) {
                  var17 = var15;
                  var8 = "hidden";
               }

               if (!(var17 <= 1.0E-9)) {
                  throw new IllegalStateException(
                     "эталонный тик "
                        + var2
                        + " не сошёлся: max|Δ| = "
                        + var17
                        + " в '"
                        + var8
                        + "', допуск 1.0E-9. Раскладка весов не та, что ожидает рантайм."
                  );
               }
            }
         }
      }

      private static double method3685(double[] var0, double[] var1) {
         double var2 = 0.0;

         for (int var4 = 0; var4 < var0.length; var4++) {
            var2 = Math.max(var2, Math.abs(var0[var4] - var1[var4]));
         }

         return var2;
      }

      private static double[] method3686(double[][] var0, double[] var1, double[] var2) {
         double[] var3 = new double[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            double var5 = var2[var4];
            double[] var7 = var0[var4];

            for (int var8 = 0; var8 < var1.length; var8++) {
               var5 += var7[var8] * var1[var8];
            }

            var3[var4] = var5;
         }

         return var3;
      }

      public static Motor.Entry4 method3687(Path var0) throws IOException {
         Motor.Entry4 var2;
         try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
            var2 = method3688(var1);
         }

         return var2;
      }

      public static Motor.Entry4 method3688(Reader var0) throws IOException {
         Motor.Entry4 var1 = new Motor.Entry4();
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();
         JsonReader var4 = new JsonReader(var0);

         try {
            var4.beginObject();

            while (var4.hasNext()) {
               String var5 = var4.nextName();
               switch (var5) {
                  case "format":
                     String var11 = var4.nextString();
                     if (!"neurotest-motor-v1".equals(var11)) {
                        throw new IllegalStateException("формат '" + var11 + "', ожидался 'neurotest-motor-v1'");
                     }
                     break;
                  case "config":
                     var1.method3689(var4);
                     break;
                  case "norm":
                     var4.beginObject();

                     while (var4.hasNext()) {
                        String var7 = var4.nextName();
                        switch (var7) {
                           case "mean":
                              var1.doubleValues4 = method3696(var4);
                              break;
                           case "var":
                              var1.doubleValues5 = method3696(var4);
                              break;
                           default:
                              var4.skipValue();
                        }
                     }

                     var4.endObject();
                     break;
                  case "inp":
                     var4.beginArray();

                     while (var4.hasNext()) {
                        var2.add(method3694(var4));
                     }

                     var4.endArray();
                     break;
                  case "gru":
                     var4.beginArray();

                     while (var4.hasNext()) {
                        var3.add(method3695(var4));
                     }

                     var4.endArray();
                     break;
                  case "heads":
                     var1.method3691(var4);
                     break;
                  case "golden":
                     var1.method3692(var4);
                     break;
                  default:
                     var4.skipValue();
               }
            }

            var4.endObject();
         } catch (Throwable var10) {
            try {
               var4.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         var4.close();
         var1.entry6s = (Motor.Entry6[])var2.toArray(new Motor.Entry6[0]);
         var1.entry2s = (Motor.Entry2[])var3.toArray(new Motor.Entry2[0]);
         var1.method3693();
         return var1;
      }

      private void method3689(JsonReader var1) throws IOException {
         var1.beginObject();

         while (var1.hasNext()) {
            String var2 = var1.nextName();
            switch (var2) {
               case "n_features":
                  this.intValue = var1.nextInt();
                  break;
               case "mlp_hidden":
                  this.intValue2 = var1.nextInt();
                  break;
               case "gru_hidden":
                  this.intValue3 = var1.nextInt();
                  break;
               case "gru_layers":
                  this.layers = var1.nextInt();
                  break;
               case "mix":
                  this.intValue4 = var1.nextInt();
                  break;
               case "latency":
                  this.intValue5 = var1.nextInt();
                  break;
               case "asinh_scale":
                  this.doubleValue = var1.nextDouble();
                  break;
               case "act_scale":
                  double[] var4 = method3696(var1);
                  this.doubleValue2 = var4[0];
                  this.doubleValue3 = var4.length > 1 ? var4[1] : var4[0];
                  break;
               case "inside_sharp":
                  this.doubleValue4 = var1.nextDouble();
                  break;
               case "max_cmd_yaw":
                  this.doubleValue5 = var1.nextDouble();
                  break;
               case "max_cmd_pitch":
                  this.doubleValue6 = var1.nextDouble();
                  break;
               case "peak_decay":
                  this.doubleValue7 = var1.nextDouble();
                  break;
               case "zero_eps":
                  this.doubleValue8 = var1.nextDouble();
                  break;
               case "act_quant":
                  this.flag = "round".equals(var1.nextString());
                  break;
               case "use_player":
                  this.flag3 = var1.nextBoolean();
                  break;
               case "log_sigma_min":
                  this.doubleValue9 = var1.nextDouble();
                  break;
               case "log_sigma_max":
                  this.doubleValue10 = var1.nextDouble();
                  break;
               case "act_residual":
                  this.flag2 = var1.nextBoolean();
                  break;
               case "envelope":
                  this.method3690(var1);
                  break;
               default:
                  var1.skipValue();
            }
         }

         var1.endObject();
      }

      private void method3690(JsonReader var1) throws IOException {
         if (var1.peek() == JsonToken.NULL) {
            var1.nextNull();
         } else {
            var1.beginObject();

            while (var1.hasNext()) {
               String var2 = var1.nextName();
               switch (var2) {
                  case "edges_counts":
                     this.doubleValues = method3696(var1);
                     break;
                  case "caps_yaw_counts":
                     this.doubleValues2 = method3696(var1);
                     break;
                  case "caps_pitch_counts":
                     this.doubleValues3 = method3696(var1);
                     break;
                  default:
                     var1.skipValue();
               }
            }

            var1.endObject();
         }
      }

      private void method3691(JsonReader var1) throws IOException {
         var1.beginObject();

         while (var1.hasNext()) {
            String var2 = var1.nextName();
            Motor.Entry6 var3 = method3694(var1);
            switch (var2) {
               case "pi":
                  this.entry6 = var3;
                  break;
               case "mu":
                  this.entry62 = var3;
                  break;
               case "log_sigma":
                  this.entry63 = var3;
                  break;
               case "rho":
                  this.entry64 = var3;
                  break;
               case "pause":
                  this.entry65 = var3;
            }
         }

         var1.endObject();
      }

      private void method3692(JsonReader var1) throws IOException {
         var1.beginArray();

         while (var1.hasNext()) {
            Motor.Entry3 var2 = new Motor.Entry3();
            var1.beginObject();

            while (var1.hasNext()) {
               String var3 = var1.nextName();
               switch (var3) {
                  case "features":
                     var2.doubleValues = method3696(var1);
                     break;
                  case "pi":
                     var2.doubleValues2 = method3696(var1);
                     break;
                  case "mu":
                     var2.doubleValues3 = method3697(var1);
                     break;
                  case "log_sigma":
                     var2.doubleValues4 = method3697(var1);
                     break;
                  case "rho":
                     var2.doubleValues5 = method3696(var1);
                     break;
                  case "pause":
                     var2.doubleValue = var1.nextDouble();
                     break;
                  case "hidden_out":
                     var2.doubleValues6 = method3696(var1);
                     break;
                  default:
                     var1.skipValue();
               }
            }

            var1.endObject();
            this.items.add(var2);
         }

         var1.endArray();
      }

      private void method3693() {
         if (this.intValue != 47) {
            throw new IllegalStateException("модель ждёт " + this.intValue + " признаков, рантайм собирает 47");
         } else if (this.flag3) {
            throw new IllegalStateException(
               "модель обучена с one-hot игрока (--player-cond on); клиенту неоткуда взять его id. Переобучите с --player-cond none"
            );
         } else if (this.entry6s.length == 0 || this.entry2s.length != this.layers) {
            throw new IllegalStateException("слоёв GRU " + this.entry2s.length + ", в конфиге " + this.layers);
         } else if (this.doubleValues4 == null
            || this.doubleValues4.length != this.intValue
            || this.doubleValues5 == null
            || this.doubleValues5.length != this.intValue) {
            throw new IllegalStateException("нормировка не на " + this.intValue + " признаков");
         } else if (this.entry6 == null || this.entry62 == null || this.entry63 == null || this.entry64 == null || this.entry65 == null) {
            throw new IllegalStateException("в файле не все головы");
         } else if (this.entry62.doubleValues.length == this.intValue4 * 2 && this.entry6.doubleValues.length == this.intValue4) {
            for (Motor.Entry2 var4 : this.entry2s) {
               if (var4.doubleValues.length != 3 * this.intValue3 || var4.doubleValues2.length != 3 * this.intValue3) {
                  throw new IllegalStateException("матрицы GRU не на " + this.intValue3);
               }
            }

            if (this.intValue - 11 != 36) {
               throw new IllegalStateException("ожидалось 36 признаков задачи и руки, получилось " + (this.intValue - 11));
            } else {
               if (this.doubleValue2 <= 0.0) {
                  this.doubleValue2 = this.doubleValue;
               }

               if (this.doubleValue3 <= 0.0) {
                  this.doubleValue3 = this.doubleValue;
               }

               this.doubleValue11 = Motor.asinh(this.doubleValue5 / this.doubleValue2);
               this.doubleValue12 = Motor.asinh(this.doubleValue6 / this.doubleValue3);
            }
         } else {
            throw new IllegalStateException("головы не на " + this.intValue4 + " компонент");
         }
      }

      private static Motor.Entry6 method3694(JsonReader var0) throws IOException {
         double[][] var1 = null;
         double[] var2 = null;
         var0.beginObject();

         while (var0.hasNext()) {
            String var3 = var0.nextName();
            switch (var3) {
               case "w":
                  var1 = method3697(var0);
                  break;
               case "b":
                  var2 = method3696(var0);
                  break;
               default:
                  var0.skipValue();
            }
         }

         var0.endObject();
         if (var1 != null && var2 != null && var1.length == var2.length) {
            return new Motor.Entry6(var1, var2);
         } else {
            throw new IllegalStateException("битый линейный слой");
         }
      }

      private static Motor.Entry2 method3695(JsonReader var0) throws IOException {
         double[][] var1 = null;
         double[][] var2 = null;
         double[] var3 = null;
         double[] var4 = null;
         var0.beginObject();

         while (var0.hasNext()) {
            String var5 = var0.nextName();
            switch (var5) {
               case "w_ih":
                  var1 = method3697(var0);
                  break;
               case "w_hh":
                  var2 = method3697(var0);
                  break;
               case "b_ih":
                  var3 = method3696(var0);
                  break;
               case "b_hh":
                  var4 = method3696(var0);
                  break;
               default:
                  var0.skipValue();
            }
         }

         var0.endObject();
         if (var1 != null && var2 != null && var3 != null && var4 != null) {
            return new Motor.Entry2(var1, var2, var3, var4);
         } else {
            throw new IllegalStateException("битый слой GRU");
         }
      }

      private static double[] method3696(JsonReader var0) throws IOException {
         double[] var1 = new double[32];
         int var2 = 0;
         var0.beginArray();

         for (; var0.hasNext(); var1[var2++] = var0.nextDouble()) {
            if (var2 == var1.length) {
               var1 = Arrays.copyOf(var1, var2 * 2);
            }
         }

         var0.endArray();
         return Arrays.copyOf(var1, var2);
      }

      private static double[][] method3697(JsonReader var0) throws IOException {
         ArrayList var1 = new ArrayList();
         var0.beginArray();

         while (var0.hasNext()) {
            var1.add(method3696(var0));
         }

         var0.endArray();
         return (double[][])var1.toArray(new double[0][]);
      }
   }

   private static final class Entry5 {
      public final double[] doubleValues;
      public final double[][] doubleValues2;
      public final double[][] doubleValues3;
      public final double[] doubleValues4;
      public final double doubleValue;

      public Entry5(double[] var1, double[][] var2, double[][] var3, double[] var4, double var5) {
         this.doubleValues = var1;
         this.doubleValues2 = var2;
         this.doubleValues3 = var3;
         this.doubleValues4 = var4;
         this.doubleValue = var5;
      }
   }

   private static final class Entry6 {
      public final double[][] doubleValues;
      public final double[] doubleValues2;

      public Entry6(double[][] var1, double[] var2) {
         this.doubleValues = var1;
         this.doubleValues2 = var2;
      }

      public double[] method3698(double[] var1) {
         double[] var2 = new double[this.doubleValues.length];

         for (int var3 = 0; var3 < this.doubleValues.length; var3++) {
            double var4 = this.doubleValues2[var3];
            double[] var6 = this.doubleValues[var3];

            for (int var7 = 0; var7 < var1.length; var7++) {
               var4 += var6[var7] * var1[var7];
            }

            var2[var3] = var4;
         }

         return var2;
      }
   }
}

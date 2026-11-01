package org.zenith.base.rotation.mods;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Util22;

public final class HandRotationLogger extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final boolean CONST_flag = false;
   private static final boolean CONST_flag2 = true;
   private BufferedWriter bufferedWriter;
   private int intValue;
   private final HandRotationLogger.Entry2 entry2 = new HandRotationLogger.Entry2();
   private final HandRotationLogger.Entry entry = new HandRotationLogger.Entry(this.entry2);
   private final Random random = new Random();
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4 = Integer.MIN_VALUE;
   private Rotation rotation;
   private Vec3d vec3d;
   private Vec3d vec3d2 = Vec3d.ZERO;
   private Vec3d[] vec3ds;
   private Vec3d vec3d3;
   private Box box;
   private static final String text = "\n";

   public Rotation method3727() {
      Rotation var1 = rotationManager.method7844();
      LivingEntity var2 = AuraModule.INSTANCE.method6358();
      if (mc.player != null && var2 != null && var2.isAlive()) {
         int var3 = mc.player.age;
         if (var3 == this.intValue4 && this.rotation != null) {
            return this.rotation;
         } else {
            if (this.intValue3 != Integer.MIN_VALUE && var3 != this.intValue3 + 1) {
               this.method3730(var2.getId());
            } else if (var2.getId() != this.intValue2) {
               this.method3730(var2.getId());
            }

            this.intValue3 = var3;
            this.box = var2.getBoundingBox();
            Vec3d var4 = this.box.getCenter();
            this.vec3d2 = this.vec3d == null ? Vec3d.ZERO : var4.subtract(this.vec3d);
            this.vec3d = var4;
            this.vec3d3 = mc.player.getEyePos();
            this.vec3ds = null;
            double[] var5 = new double[]{var1.getFloatValue(), var1.getFloatValue2()};
            int var6 = this.entry.items.size();
            double var7 = mc.player.getAttackCooldownProgress(0.0F);
            int[] var9 = this.entry.method3745(var5, this::predict, this.random, var7);
            double var10 = this.entry2.doubleValue;
            Rotation var12 = var1.method3140((float)(var9[0] * var10), (float)(var9[1] * var10));
            this.method3729(var3, var5, var9, var6);
            this.method3731(var3, var12);
            return var12;
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

   private void predict(int var1, double[] var2) {
      var1 = Math.max(0, Math.min(var1, this.entry2.intValue2));
      Vec3d var3;
      if (var1 == 0) {
         var3 = this.vec3d3;
      } else {
         if (this.vec3ds == null) {
            this.vec3ds = this.method3728(this.entry2.intValue2);
         }

         var3 = this.vec3ds[var1 - 1];
      }

      Vec3d var4 = this.vec3d2.multiply(var1);
      Vec3d var5 = this.box.getCenter().add(var4);
      double var6 = var5.x - var3.x;
      double var8 = var5.y - var3.y;
      double var10 = var5.z - var3.z;
      double var12 = Math.toDegrees(Math.atan2(-var6, var10));
      double var14 = Math.toDegrees(Math.atan2(-var8, Math.hypot(var6, var10)));
      double var16 = Double.MAX_VALUE;
      double var18 = -Double.MAX_VALUE;
      double var20 = Double.MAX_VALUE;
      double var22 = -Double.MAX_VALUE;

      for (int var24 = 0; var24 < 8; var24++) {
         double var25 = ((var24 & 1) == 0 ? this.box.minX : this.box.maxX) + var4.x - var3.x;
         double var27 = ((var24 & 2) == 0 ? this.box.minY : this.box.maxY) + var4.y - var3.y;
         double var29 = ((var24 & 4) == 0 ? this.box.minZ : this.box.maxZ) + var4.z - var3.z;
         double var31 = Math.toDegrees(Math.atan2(-var25, var29));
         double var33 = Math.toDegrees(Math.atan2(-var27, Math.hypot(var25, var29)));
         if (Double.isFinite(var31) && Double.isFinite(var33)) {
            double var35 = HandRotationLogger.Entry.method3735(var31 - var12);
            double var37 = var33 - var14;
            var16 = Math.min(var16, var35);
            var18 = Math.max(var18, var35);
            var20 = Math.min(var20, var37);
            var22 = Math.max(var22, var37);
         }
      }

      var2[0] = var12;
      var2[1] = var14;
      var2[2] = var16 == Double.MAX_VALUE ? 0.001 : 0.5 * (var18 - var16);
      var2[3] = var20 == Double.MAX_VALUE ? 0.001 : 0.5 * (var22 - var20);
   }

   private Vec3d[] method3728(int var1) {
      Vec3d[] var2 = new Vec3d[var1];
      Vec3d var3 = this.vec3d3.subtract(mc.player.getEntityPos());

      try {
         PlayerSimulation var4 = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(mc.player.input.playerInput));
         Vec3d var19 = this.box.getCenter();

         for (int var6 = 0; var6 < var1; var6++) {
            var4.tick();
            Vec3d var7 = var4.vec3d.add(var3);
            boolean var8 = Double.isFinite(var7.x) && Double.isFinite(var7.y) && Double.isFinite(var7.z);
            if (!var8) {
               var2[var6] = var6 == 0 ? this.vec3d3 : var2[var6 - 1];
            } else {
               Vec3d var9 = var19.add(this.vec3d2.multiply(var6 + 1));
               double var10 = var7.x - var9.x;
               double var12 = var7.z - var9.z;
               double var14 = Math.hypot(var10, var12);
               if (var14 < this.getEntry2().doubleValue6 && var14 > 1.0E-9) {
                  double var16 = this.getEntry2().doubleValue6 / var14;
                  var7 = new Vec3d(var9.x + var10 * var16, var7.y, var9.z + var12 * var16);
                  var4.vec3d = new Vec3d(var7.x - var3.x, var4.vec3d.y, var7.z - var3.z);
               }

               var2[var6] = var7;
            }
         }
      } catch (Throwable var18) {
         for (int var5 = 0; var5 < var1; var5++) {
            var2[var5] = this.vec3d3;
         }
      }

      return var2;
   }

   private void method3729(int var1, double[] var2, int[] var3, int var4) {
      try {
         if (this.bufferedWriter == null) {
            Path var5 = Path.of("hand_v6_log.csv");
            boolean var6 = !Files.exists(var5);
            this.bufferedWriter = Files.newBufferedWriter(var5, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (var6) {
               this.bufferedWriter
                  .write(
                     "age,cam_yaw,cam_pitch,tgt_yaw1,tgt_pitch1,half_yaw,half_pitch,tgt_yaw4,eye_x,eye_y,eye_z,cen_x,cen_y,cen_z,cy,cp,active_before,active_after,forward,back,left,right,jump,eye4_x,eye4_z,hdist\n"
                  );
            }
         }

         double[] var12 = new double[4];
         this.predict(1, var12);
         double[] var13 = new double[4];
         this.predict(4, var13);
         Vec3d var7 = this.box.getCenter();
         PlayerInput var8 = mc.player.input.playerInput;
         if (this.vec3ds == null) {
            this.vec3ds = this.method3728(this.entry2.intValue2);
         }

         Vec3d var9 = this.vec3ds[3];
         this.bufferedWriter
            .write(
               var1
                  + ","
                  + var2[0]
                  + ","
                  + var2[1]
                  + ","
                  + var12[0]
                  + ","
                  + var12[1]
                  + ","
                  + var12[2]
                  + ","
                  + var12[3]
                  + ","
                  + var13[0]
                  + ","
                  + this.vec3d3.x
                  + ","
                  + this.vec3d3.y
                  + ","
                  + this.vec3d3.z
                  + ","
                  + var7.x
                  + ","
                  + var7.y
                  + ","
                  + var7.z
                  + ","
                  + var3[0]
                  + ","
                  + var3[1]
                  + ","
                  + var4
                  + ","
                  + this.entry.items.size()
                  + ","
                  + (var8.forward() ? 1 : 0)
                  + ","
                  + (var8.backward() ? 1 : 0)
                  + ","
                  + (var8.left() ? 1 : 0)
                  + ","
                  + (var8.right() ? 1 : 0)
                  + ","
                  + (var8.jump() ? 1 : 0)
                  + ","
                  + var9.x
                  + ","
                  + var9.z
                  + ","
                  + Math.hypot(var7.x - this.vec3d3.x, var7.z - this.vec3d3.z)
                  + "\n"
            );
         if (++this.intValue % 40 == 0) {
            this.bufferedWriter.flush();
         }
      } catch (Exception var11) {
         if (this.bufferedWriter != null) {
            try {
               this.bufferedWriter.close();
            } catch (Exception var10) {
            }
         }

         this.bufferedWriter = null;
      }
   }

   private HandRotationLogger.Entry2 getEntry2() {
      return this.entry2;
   }

   private void method3730(int var1) {
      this.entry.reset();
      this.intValue2 = var1;
      this.intValue3 = Integer.MIN_VALUE;
      this.intValue4 = Integer.MIN_VALUE;
      this.rotation = null;
      this.vec3d = null;
      this.vec3d2 = Vec3d.ZERO;
      this.vec3ds = null;
   }

   private void method3731(int var1, Rotation var2) {
      this.intValue4 = var1;
      this.rotation = var2;
   }

   public void reset() {
      this.method3730(Integer.MIN_VALUE);
   }

   public boolean method3732() {
      return true;
   }

   public static String method3733() {
      return "без прогиба:\n" + method3734(0.0) + "\nпрогиб c = 0.10:\n" + method3734(0.1);
   }

   private static String method3734(double var0) {
      HandRotationLogger.Entry2 var2 = new HandRotationLogger.Entry2();
      var2.flag2 = true;
      var2.doubleValue9 = var0;
      HandRotationLogger.Entry var3 = new HandRotationLogger.Entry(var2);
      Random var4 = new Random(0L);
      double[] var5 = new double[]{0.0, 0.0};
      StringBuilder var6 = new StringBuilder();
      StringBuilder var7 = new StringBuilder();
      double[] var8 = new double[]{30.0, -5.0};

      for (int var9 = 0; var9 < 40; var9++) {
         if (var9 == 20) {
            var8[0] = -20.0;
            var8[1] = 10.0;
         }

         HandRotationLogger.Handler var10 = (var1, var2x) -> {
            var2x[0] = var8[0];
            var2x[1] = var8[1];
            var2x[2] = 6.0;
            var2x[3] = 17.0;
         };
         int[] var11 = var3.method3745(var5, var10, var4, 1.0);
         var5[0] += var11[0] * var2.doubleValue;
         var5[1] = Math.max(-90.0, Math.min(90.0, var5[1] + var11[1] * var2.doubleValue));
         var6.append(var9 > 0 ? " " : "").append(var11[0]);
         var7.append(var9 > 0 ? " " : "").append(var11[1]);
      }

      return "yaw:   " + var6 + "\npitch: " + var7;
   }

   public static final class Entry {
      public final HandRotationLogger.Entry2 entry2;
      public final List<double[][]> items = new ArrayList<>();
      public int intValue;
      public int intValue2;
      public double doubleValue;
      public double doubleValue2;

      public Entry(HandRotationLogger.Entry2 var1) {
         this.entry2 = var1;
      }

      public void reset() {
         this.items.clear();
         this.intValue = 0;
         this.intValue2 = 0;
         this.doubleValue = 0.0;
         this.doubleValue2 = 0.0;
      }

      public static double method3735(double var0) {
         double var2 = (var0 + 180.0) % 360.0;
         if (var2 < 0.0) {
            var2 += 360.0;
         }

         return var2 - 180.0;
      }

      public static double[] method3736(int var0) {
         double[] var1 = new double[var0];
         double var2 = 0.0;

         for (int var4 = 1; var4 <= var0; var4++) {
            double var5 = (double)var4 / var0;
            double var7 = 10.0 * Math.pow(var5, 3.0) - 15.0 * Math.pow(var5, 4.0) + 6.0 * Math.pow(var5, 5.0);
            var1[var4 - 1] = var7 - var2;
            var2 = var7;
         }

         return var1;
      }

      public double method3737(double var1) {
         return Math.max(0.0, Math.min(1.0, (var1 - this.entry2.doubleValue24) / Math.max(this.entry2.doubleValue25 - this.entry2.doubleValue24, 1.0E-6)));
      }

      public int method3738(double var1, Random var3, double var4) {
         double var6 = this.method3737(var4);
         double var8 = this.entry2.doubleValue2
            * Math.pow(Math.max(var1, 0.5), this.entry2.doubleValue3)
            * (this.entry2.doubleValue22 - (this.entry2.doubleValue22 - this.entry2.doubleValue23) * var6);
         double var10 = this.entry2.flag2 ? 1.0 : Math.exp(var3.nextGaussian() * this.entry2.doubleValue4);
         long var12 = Math.max(Math.round(Math.rint(var8 * var10)), (long)Math.ceil(var1 / this.entry2.doubleValue5));
         return (int)Math.max((long)this.entry2.intValue, Math.min((long)this.entry2.intValue2, var12));
      }

      public double method3739(double var1, Random var3) {
         double var4;
         if (var1 >= this.entry2.doubleValue13) {
            var4 = this.entry2.doubleValue10;
         } else if (var1 <= this.entry2.doubleValue12) {
            var4 = this.entry2.doubleValue11;
         } else {
            double var6 = (var1 - this.entry2.doubleValue12) / (this.entry2.doubleValue13 - this.entry2.doubleValue12);
            var4 = this.entry2.doubleValue11 + var6 * (this.entry2.doubleValue10 - this.entry2.doubleValue11);
         }

         return var4 * (this.entry2.flag2 ? 1.0 : Math.exp(var3.nextGaussian() * this.entry2.doubleValue14));
      }

      public double[] method3740() {
         int var1 = 0;
         double var2 = 0.0;
         double var4 = 0.0;

         for (double[][] var7 : this.items) {
            var1 = Math.max(var1, var7[0].length);

            for (double var11 : var7[0]) {
               var2 += var11;
            }

            for (double var16 : var7[1]) {
               var4 += var16;
            }
         }

         return new double[]{var1, var2, var4};
      }

      public double[] method3741(double[] var1, HandRotationLogger.Handler var2, int var3) {
         double[] var4 = this.method3740();
         double[] var5 = new double[4];
         var2.predict(var3, var5);
         double var6 = method3735(var5[0] - (var1[0] + var4[1]));
         if (Math.abs(var6) > this.entry2.doubleValue18) {
            double[] var8 = new double[4];
            var2.predict(Math.max(var3 - 1, 0), var8);
            double var9 = method3735(var5[0] - var8[0]);
            if (Math.abs(var9) > this.entry2.doubleValue19 && Math.signum(var9) != Math.signum(var6)) {
               var6 -= 360.0 * Math.signum(var6);
            }
         }

         return new double[]{var6, var5[1] - (var1[1] + var4[2]), var5[2], var5[3]};
      }

      public double method3742(double var1) {
         return this.entry2.doubleValue21 + (this.entry2.doubleValue17 - this.entry2.doubleValue21) * this.method3737(var1);
      }

      public double[][] method3743(double var1, double var3, int var5, Random var6) {
         double[] var7 = method3736(var5);
         double[] var8 = new double[var5];
         double[] var9 = new double[var5];
         double var10 = Math.hypot(var1, var3);
         double var12 = this.entry2.flag2
            ? this.entry2.doubleValue9
            : Math.max(-this.entry2.doubleValue8, Math.min(this.entry2.doubleValue8, var6.nextGaussian() * this.entry2.doubleValue7));
         if (!(var10 < 1.0E-9) && var12 != 0.0) {
            double var27 = -var3 / var10;
            double var16 = var1 / var10;
            double var18 = 0.0;
            double var20 = 0.0;

            for (int var22 = 0; var22 < var5; var22++) {
               var18 += var7[var22];
               double var23 = 4.0 * var18 * (1.0 - var18);
               double var25 = var10 * (var23 - var20);
               var20 = var23;
               var8[var22] = var1 * var7[var22] + var12 * var25 * var27;
               var9[var22] = var3 * var7[var22] + var12 * var25 * var16;
            }

            return new double[][]{var8, var9};
         } else {
            for (int var14 = 0; var14 < var5; var14++) {
               var8[var14] = var1 * var7[var14];
               var9[var14] = var3 * var7[var14];
            }

            return new double[][]{var8, var9};
         }
      }

      public void method3744(double[] var1, HandRotationLogger.Handler var2, Random var3, double var4) {
         int var6 = (int)this.method3740()[0];
         double[] var7 = this.method3741(var1, var2, Math.max(var6, this.entry2.intValue3));
         int var8 = this.method3738(Math.hypot(var7[0], var7[1]), var3, var4);
         var7 = this.method3741(var1, var2, Math.max(var6, var8));
         double var9 = Math.hypot(var7[0], var7[1]);
         if (!(var9 < 1.0E-6)) {
            var8 = this.method3738(var9, var3, var4);
            double var11 = this.method3739(var9, var3);
            double var13 = var11 * var7[0];
            double var15 = var11 * var7[1];
            double var17 = this.method3740()[1];
            if (Math.abs(var17) > 1.0E-9 && Math.signum(var13) == -Math.signum(var17) && Math.abs(var13) > 1.0E-9) {
               double var19 = Math.max(Math.abs(var7[0]) / Math.max(var7[2], 0.001), Math.abs(var7[1]) / Math.max(var7[3], 0.001));
               if (var19 < this.entry2.doubleValue20 * this.method3742(var4)) {
                  return;
               }
            }

            if (!this.entry2.flag2) {
               double var29 = var7[0] / var9;
               double var21 = var7[1] / var9;
               double var23 = var3.nextGaussian() * this.entry2.doubleValue15 * var9;
               double var25 = var3.nextGaussian() * this.entry2.doubleValue16 * var9;
               var13 += var29 * var23 + -var21 * var25;
               var15 += var21 * var23 + var29 * var25;
            }

            this.items.add(this.method3743(var13, var15, var8, var3));
         }
      }

      public int[] method3745(double[] var1, HandRotationLogger.Handler var2, Random var3, double var4) {
         var4 = Math.max(0.0, Math.min(1.0, var4));
         if (this.intValue2 >= this.intValue || this.entry2.flag && this.items.isEmpty()) {
            int var6 = (int)this.method3740()[0];
            double[] var7 = this.method3741(var1, var2, Math.max(var6, this.entry2.intValue3));
            double var8 = Math.max(Math.abs(var7[0]) / Math.max(var7[2], 0.001), Math.abs(var7[1]) / Math.max(var7[3], 0.001));
            if (var8 > this.method3742(var4)) {
               this.method3744(var1, var2, var3, var4);
            }

            int var10 = this.entry2.flag2
               ? (this.entry2.intValue3 + this.entry2.intValue4) / 2
               : this.entry2.intValue3 + var3.nextInt(this.entry2.intValue4 - this.entry2.intValue3 + 1);
            this.intValue = this.intValue2 + var10;
         }

         this.intValue2++;
         if (this.items.isEmpty()) {
            return new int[]{0, 0};
         } else {
            double var18 = 0.0;
            double var19 = 0.0;
            ArrayList var20 = new ArrayList();

            for (double[][] var12 : this.items) {
               var18 += var12[0][0];
               var19 += var12[1][0];
               if (var12[0].length > 1) {
                  double[] var13 = new double[var12[0].length - 1];
                  double[] var14 = new double[var12[1].length - 1];
                  System.arraycopy(var12[0], 1, var13, 0, var13.length);
                  System.arraycopy(var12[1], 1, var14, 0, var14.length);
                  var20.add(new double[][]{var13, var14});
               }
            }

            this.items.clear();
            this.items.addAll(var20);
            double var21 = var18 / this.entry2.doubleValue + this.doubleValue;
            double var22 = var19 / this.entry2.doubleValue + this.doubleValue2;
            int var15 = (int)(var21 < 0.0 ? Math.ceil(var21) : Math.floor(var21));
            int var16 = (int)(var22 < 0.0 ? Math.ceil(var22) : Math.floor(var22));
            this.doubleValue = var21 - var15;
            this.doubleValue2 = var22 - var16;
            return new int[]{var15, var16};
         }
      }
   }

   public static final class Entry2 {
      public double doubleValue = 0.15190947;
      public double doubleValue2 = 1.66;
      public double doubleValue3 = 0.32;
      public double doubleValue4 = 0.25;
      public int intValue = 2;
      public int intValue2 = 14;
      public double doubleValue5 = 35.0;
      public double doubleValue6 = 0.6;
      public double doubleValue7 = 0.065;
      public double doubleValue8 = 0.3;
      public double doubleValue9 = 0.0;
      public double doubleValue10 = 0.95;
      public double doubleValue11 = 0.7;
      public double doubleValue12 = 30.0;
      public double doubleValue13 = 60.0;
      public double doubleValue14 = 0.12;
      public double doubleValue15 = 0.05;
      public double doubleValue16 = 0.05;
      public int intValue3 = 2;
      public int intValue4 = 4;
      public double doubleValue17 = 0.05;
      public boolean flag = true;
      public double doubleValue18 = 90.0;
      public double doubleValue19 = 3.0;
      public double doubleValue20 = 2.0;
      public double doubleValue21 = 0.9;
      public double doubleValue22 = 1.2;
      public double doubleValue23 = 0.75;
      public double doubleValue24 = 0.25;
      public double doubleValue25 = 0.55;
      public boolean flag2 = false;

      public HandRotationLogger.Entry2 method3746() {
         HandRotationLogger.Entry2 var1 = new HandRotationLogger.Entry2();
         var1.doubleValue = this.doubleValue;
         var1.doubleValue2 = this.doubleValue2;
         var1.doubleValue3 = this.doubleValue3;
         var1.doubleValue4 = this.doubleValue4;
         var1.intValue = this.intValue;
         var1.intValue2 = this.intValue2;
         var1.doubleValue10 = this.doubleValue10;
         var1.doubleValue11 = this.doubleValue11;
         var1.doubleValue12 = this.doubleValue12;
         var1.doubleValue13 = this.doubleValue13;
         var1.doubleValue14 = this.doubleValue14;
         var1.doubleValue15 = this.doubleValue15;
         var1.doubleValue16 = this.doubleValue16;
         var1.intValue3 = this.intValue3;
         var1.intValue4 = this.intValue4;
         var1.doubleValue17 = this.doubleValue17;
         var1.flag = this.flag;
         var1.flag2 = this.flag2;
         var1.doubleValue18 = this.doubleValue18;
         var1.doubleValue19 = this.doubleValue19;
         var1.doubleValue20 = this.doubleValue20;
         var1.doubleValue5 = this.doubleValue5;
         var1.doubleValue6 = this.doubleValue6;
         var1.doubleValue21 = this.doubleValue21;
         var1.doubleValue22 = this.doubleValue22;
         var1.doubleValue23 = this.doubleValue23;
         var1.doubleValue24 = this.doubleValue24;
         var1.doubleValue25 = this.doubleValue25;
         var1.doubleValue7 = this.doubleValue7;
         var1.doubleValue8 = this.doubleValue8;
         var1.doubleValue9 = this.doubleValue9;
         return var1;
      }
   }

   public interface Handler {
      void predict(int var1, double[] var2);
   }
}

package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class EmberField {
   private static final int CONST_intValue = 5;
   private static final int CONST_intValue2 = 12;
   private final EmberField.Entry[] entrys;
   private final long longValue = System.currentTimeMillis();
   private final Random random = new Random();
   private float floatValue;
   private float floatValue2;
   private float floatValue3;

   public EmberField() {
      ArrayList var1 = new ArrayList();

      for (byte var2 = -1; var2 <= 1; var2 += 2) {
         for (int var3 = 0; var3 < 5; var3++) {
            float var4 = var3 / 4.0F;
            double var5 = -0.15 + var4 * 1.45;
            EmberField.Entry var7 = new EmberField.Entry();
            var7.doubleValue = 1.36 - var4 * 0.05;
            var7.doubleValue2 = 0.15;
            var7.doubleValue3 = var2 * 0.12;
            var7.doubleValue4 = 0.55;
            var7.doubleValue5 = var2 * (Math.sin(var5) * 1.15 + 0.25);
            var7.doubleValue6 = Math.cos(var5) * 0.95;
            var7.doubleValue7 = -0.45 - var4 * 0.25;
            var7.doubleValue8 = 2.15 - var4 * 0.45;
            var7.doubleValue9 = 0.085 - var4 * 0.01;
            var7.doubleValue10 = 0.16;
            var7.doubleValue11 = 0.1;
            var7.doubleValue12 = 2.6;
            var7.floatValue = var2 * 1.7F + var3 * 0.9F;
            var1.add(var7);
         }
      }

      this.entrys = (EmberField.Entry[])var1.toArray(new EmberField.Entry[0]);
   }

   public void method7018(PlayerEntity var1, float var2, float var3, double var4, float var6) {
      Vec3d var7 = var1.getLerpedPos(var2);
      float var8 = MathHelper.lerpAngleDegrees(var2, var1.lastBodyYaw, var1.bodyYaw);
      double var9 = Math.toRadians(var8);
      double var11 = -Math.sin(var9);
      double var13 = Math.cos(var9);
      double var15 = -Math.cos(var9);
      double var17 = -Math.sin(var9);
      double var19 = var1.isInSneakingPose() ? -0.3 : 0.0;
      float var21 = (float)(System.currentTimeMillis() - this.longValue) / 1000.0F;
      this.floatValue = MathHelper.clamp(var21 / 0.6F, 0.0F, 1.0F);
      this.floatValue2 = Math.max(0.0F, this.floatValue2 - var3 * 1.4F);
      this.floatValue3 = var3;

      for (EmberField.Entry var25 : this.entrys) {
         System.arraycopy(var25.doubleValues, 0, var25.doubleValues4, 0, 12);
         System.arraycopy(var25.doubleValues2, 0, var25.doubleValues5, 0, 12);
         System.arraycopy(var25.doubleValues3, 0, var25.doubleValues6, 0, 12);
         double var26 = var7.x - var11 * var25.doubleValue2 + var15 * var25.doubleValue3;
         double var28 = var7.y + var25.doubleValue + var19;
         double var30 = var7.z - var13 * var25.doubleValue2 + var17 * var25.doubleValue3;
         double var32 = var25.doubleValue5 * (1.0 + 0.4 * this.floatValue2);
         double var34 = -var11 * var25.doubleValue4 + var15 * var32;
         double var36 = var25.doubleValue6 + 0.25 * this.floatValue2;
         double var38 = -var13 * var25.doubleValue4 + var17 * var32;
         double var40 = Math.sqrt(var34 * var34 + var36 * var36 + var38 * var38);
         var34 /= var40;
         var36 /= var40;
         var38 /= var40;
         double var42 = -var38;
         double var46 = Math.sqrt(var42 * var42 + var34 * var34);
         double var44;
         if (var46 < 1.0E-5) {
            var42 = var15;
            var44 = var17;
         } else {
            var42 /= var46;
            var44 = var34 / var46;
         }

         double var48 = var25.doubleValue8 * var6 * this.floatValue / 11.0;
         double var50 = var26;
         double var52 = var28;
         double var54 = var30;
         var25.doubleValues[0] = var26;
         var25.doubleValues2[0] = var28;
         var25.doubleValues3[0] = var30;

         for (int var56 = 1; var56 < 12; var56++) {
            double var57 = var56 / 11.0;
            double var59 = Math.pow(var57, 1.15);
            double var61 = var25.doubleValue10 * Math.sin(var57 * 5.2 - var4 * 2.1 * var25.doubleValue12 + var25.floatValue) * var59;
            double var63 = var25.doubleValue11 * Math.sin(var57 * 4.1 - var4 * 1.6 * var25.doubleValue12 + var25.floatValue * 1.9) * var59;
            double var65 = var25.doubleValue7 * Math.pow(var57, 1.3) + var61;
            double var67 = Math.cos(var65);
            double var69 = Math.sin(var65);
            double var71 = var34 * var67 + -var44 * var36 * var69;
            double var73 = var36 * var67 + (var44 * var34 - var42 * var38) * var69;
            double var75 = var38 * var67 + var42 * var36 * var69;
            double var77 = Math.cos(var63);
            double var79 = Math.sin(var63);
            double var81 = var71 * var77 + var75 * var79;
            double var83 = -var71 * var79 + var75 * var77;
            var50 += var81 * var48;
            var52 += var73 * var48;
            var54 += var83 * var48;
            var25.doubleValues[var56] = var50;
            var25.doubleValues2[var56] = var52;
            var25.doubleValues3[var56] = var54;
         }

         double var89 = method7022(var25.doubleValues[0] - var25.doubleValues4[0])
            + method7022(var25.doubleValues2[0] - var25.doubleValues5[0])
            + method7022(var25.doubleValues3[0] - var25.doubleValues6[0]);
         if (!var25.initialized || var89 > 9.0) {
            System.arraycopy(var25.doubleValues, 0, var25.doubleValues4, 0, 12);
            System.arraycopy(var25.doubleValues2, 0, var25.doubleValues5, 0, 12);
            System.arraycopy(var25.doubleValues3, 0, var25.doubleValues6, 0, 12);
            var25.initialized = true;
         }
      }
   }

   public void method7019(EmberShards var1, float var2, float var3) {
      if (!(var3 <= 0.0F)) {
         float var4 = this.floatValue3 > 1.0E-4F ? 1.0F / this.floatValue3 : 0.0F;

         for (EmberField.Entry var8 : this.entrys) {
            var8.floatValue2 = var8.floatValue2 + var2 * 9.0F * var3 * this.floatValue;

            while (var8.floatValue2 >= 1.0F) {
               var8.floatValue2--;
               int var9 = 3 + this.random.nextInt(9);
               double var10 = var8.doubleValue9;
               double var12 = method7021((var8.doubleValues[var9] - var8.doubleValues4[var9]) * var4) * 0.25 + this.random.nextGaussian() * 0.25;
               double var14 = method7021((var8.doubleValues2[var9] - var8.doubleValues5[var9]) * var4) * 0.25 + 0.35 + this.random.nextDouble() * 0.3;
               double var16 = method7021((var8.doubleValues3[var9] - var8.doubleValues6[var9]) * var4) * 0.25 + this.random.nextGaussian() * 0.25;
               var1.method7578(
                  var8.doubleValues[var9] + this.random.nextGaussian() * var10,
                  var8.doubleValues2[var9] + this.random.nextGaussian() * var10,
                  var8.doubleValues3[var9] + this.random.nextGaussian() * var10,
                  var12,
                  var14,
                  var16,
                  0.016F + this.random.nextFloat() * 0.03F
               );
            }
         }
      }
   }

   public boolean method7020(EmberShards var1, LivingEntity var2, int var3, float var4) {
      if (this.floatValue < 0.6F) {
         return false;
      } else {
         var var5 = new ArrayList<>(List.of(this.entrys));
         Collections.shuffle(var5, this.random);
         int var6 = Math.min(var5.size(), 2 + var3 * 2);

         for (int var7 = 0; var7 < var6; var7++) {
            EmberField.Entry var8 = (EmberField.Entry)var5.get(var7);
            var1.method7579(var8.doubleValues[11], var8.doubleValues2[11], var8.doubleValues3[11], var2, var7 * 0.045F, var4);
         }

         this.floatValue2 = 1.0F;
         return true;
      }
   }

   private static double method7021(double var0) {
      return Math.max(-20.0, Math.min(20.0, var0));
   }

   private static double method7022(double var0) {
      return var0 * var0;
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
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public float floatValue;
      public final double[] doubleValues = new double[12];
      public final double[] doubleValues2 = new double[12];
      public final double[] doubleValues3 = new double[12];
      public final double[] doubleValues4 = new double[12];
      public final double[] doubleValues5 = new double[12];
      public final double[] doubleValues6 = new double[12];
      public boolean initialized;
      public float floatValue2;
   }
}

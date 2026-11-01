package org.zenith.base.rotation.mods;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public class Class21 extends Util22 {
   private int intValue = 1;
   private int intValue2 = 4;
   private int intValue3 = 6;
   private int intValue4 = 16;
   private int intValue5 = 25;
   private float floatValue = 0.12F;
   private int intValue6 = 1;
   private int intValue7 = 4;
   private float floatValue2 = 0.18F;
   private float floatValue3 = 0.22F;
   private float floatValue4 = 1.12F;
   private float floatValue5 = 0.42F;
   public float[] values = new float[]{0.0123967F, 0.053719F, 0.109504F, 0.17562F, 0.21281F, 0.272727F, 0.11157F, 0.0392562F, 0.0103306F, 0.00206612F};
   public int index = 0;
   private final Random random = new Random();
   private Class21.Entry3 entry3;
   private int intValue8 = Integer.MIN_VALUE;
   private int intValue9 = 0;
   private int intValue10 = 1;
   private int intValue11 = 0;
   private boolean flag = true;

   public Rotation method3431(Rotation var1) {
      return var1;
   }

   private Rotation method3432(Rotation var1, Rotation var2) {
      if (this.values == null) {
         this.values = new float[]{0.0123967F, 0.053719F, 0.109504F, 0.17562F, 0.21281F, 0.272727F, 0.11157F, 0.0392562F, 0.0103306F, 0.00206612F};
      }

      float var3 = this.values[this.index] * 2.0F;
      this.index++;
      if (this.index >= this.values.length) {
         this.index = 0;
      }

      Vec2f var4 = new Vec2f(MathHelper.wrapDegrees(var2.getFloatValue() - var1.getFloatValue()) * var3, (var2.getFloatValue2() - var1.getFloatValue2()) * var3);
      return var1.method3141(var4);
   }

   private void method3433() {
      if (!this.flag && this.intValue9 > 0) {
         this.intValue9++;
      } else {
         this.intValue9 = 1;
         this.intValue10 = this.method3444();
      }
   }

   private Rotation method3434(Rotation var1, Rotation var2) {
      if (this.entry3 != null && !this.entry3.isDone()) {
         Class21.Entry2 var3 = this.entry3.method3451();
         Vec2f var4 = var1.method3131(var2);
         int var5 = this.method3435(var3.intValue, var4.getFloatValue(), this.entry3.floatValue);
         int var6 = this.method3435(var3.intValue2, var4.getFloatValue2(), this.entry3.floatValue);
         Rotation var7 = var1.method3140(var5 * this.entry3.floatValue, var6 * this.entry3.floatValue);
         if (this.entry3.isDone()) {
            this.method3447();
         }

         return var7;
      } else {
         this.method3447();
         return var1;
      }
   }

   private int method3435(int var1, float var2, float var3) {
      if (var1 != 0 && this.isFinite(var2) && this.isFinite(var3) && !(var3 <= 0.0F)) {
         boolean var4 = Math.signum((float)var1) == Math.signum(var2);
         float var5 = var4 ? 1.8F : 0.3F;
         int var6 = Math.round(var1 * var5);
         if (var6 == 0) {
            var6 = var1 > 0 ? 1 : -1;
         }

         int var7 = Math.round(var2 / var3);
         return this.method3436(var6, var7);
      } else {
         return 0;
      }
   }

   private int method3436(int var1, int var2) {
      if (var1 != 0 && var2 != 0) {
         if (Integer.signum(var1) != Integer.signum(var2)) {
            return Math.abs(var1) <= 1 ? 0 : var1;
         } else {
            return Math.abs(var1) > Math.abs(var2) ? var2 : var1;
         }
      } else {
         return 0;
      }
   }

   private Class21.Entry3 method3437(Rotation var1, Rotation var2, float var3) {
      Vec2f var4 = var1.method3131(var2);
      int var5 = Math.round(var4.getFloatValue() / var3);
      int var6 = Math.round(var4.getFloatValue2() / var3);
      int var7 = Math.abs(var5) + Math.abs(var6);
      if (var7 <= 0) {
         return null;
      } else {
         int var8 = this.method3445();
         int var9 = Math.max(1, Math.min(var8, var7));
         float var10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.1F;
         int[] var11 = this.method3439(var5, this.method3438(var9, var10));
         int[] var12 = this.method3439(var6, this.method3438(var9, -var10 * 0.75F));
         this.method3442(var11);
         this.method3442(var12);
         this.method3441(var11, var12);
         return new Class21.Entry3(var11, var12, var3);
      }
   }

   private float[] method3438(int var1, float var2) {
      float[] var3 = new float[var1];
      float var4 = MathHelper.clamp(0.46F + var2 + (this.random.nextFloat() - 0.5F) * this.floatValue3, 0.28F, 0.7F);
      float var5 = 0.18F + this.random.nextFloat() * 0.12F;
      float var6 = (this.random.nextFloat() * 2.0F - 1.0F) * this.floatValue2;
      float var7 = 2.2F + this.random.nextFloat() * 0.6F;
      float var8 = 2.1F + this.random.nextFloat() * 0.7F;
      float var9 = 0.0F;

      for (int var10 = 0; var10 < var1; var10++) {
         float var11 = (var10 + 0.5F) / var1;
         float var12 = (var11 - var4) / var5;
         float var13 = (float)Math.exp(-0.5F * var12 * var12);
         float var14 = 30.0F * var11 * var11 * (1.0F - var11) * (1.0F - var11);
         float var15 = MathHelper.clamp(var11 * var7, 0.0F, 1.0F);
         float var16 = MathHelper.clamp((1.0F - var11) * var8, 0.0F, 1.0F);
         var6 = var6 * 0.72F + (this.random.nextFloat() * 2.0F - 1.0F) * this.floatValue2 * 0.28F;
         float var17 = MathHelper.clamp(1.0F + var6, 0.58F, 1.48F);
         float var18 = Math.max(0.001F, (var13 * 0.62F + var14 * 0.38F) * var15 * var16 * var17);
         var3[var10] = var18;
         var9 += var18;
      }

      if (!(var9 <= 0.0F) && this.isFinite(var9)) {
         for (int var19 = 0; var19 < var1; var19++) {
            var3[var19] /= var9;
         }

         return var3;
      } else {
         Arrays.fill(var3, 1.0F / Math.max(1, var1));
         return var3;
      }
   }

   private int[] method3439(int var1, float[] var2) {
      int var3 = var2.length;
      int[] var4 = new int[var3];
      int var5 = Integer.compare(var1, 0);
      int var6 = Math.abs(var1);
      if (var6 == 0) {
         return var4;
      } else {
         float var7 = 0.0F;
         int var8 = 0;

         for (int var9 = 0; var9 < var3; var9++) {
            var7 += var2[var9];
            int var10 = var9 == var3 - 1 ? var6 : Math.round(var7 * var6);
            var4[var9] = var10 - var8;
            var8 = var10;
         }

         if (var5 < 0) {
            for (int var11 = 0; var11 < var3; var11++) {
               var4[var11] = -var4[var11];
            }
         }

         this.method3440(var4);
         return var4;
      }
   }

   private void method3440(int[] var1) {
      int var2 = Integer.compare(this.sum(var1), 0);
      if (var2 != 0 && var1.length >= 3) {
         for (int var3 = 0; var3 < 2; var3++) {
            for (int var4 = 1; var4 < var1.length - 1; var4++) {
               int var5 = Math.abs(var1[var4]);
               int var6 = Math.abs(var1[var4 - 1]);
               int var7 = Math.abs(var1[var4 + 1]);
               int var8 = Math.max(var6, var7);
               if (var5 > var8 + 4 && var5 > 2) {
                  int var9 = Math.min((var5 - var8) / 2, var5 - 1);
                  int var10 = var6 <= var7 ? var4 - 1 : var4 + 1;
                  var1[var4] -= var2 * var9;
                  var1[var10] += var2 * var9;
               }
            }
         }
      }
   }

   private void method3441(int[] var1, int[] var2) {
      for (int var3 = 0; var3 < var1.length; var3++) {
         if (var1[var3] == 0 && var2[var3] == 0 && !this.method3443(var1, var3) && !this.method3443(var2, var3)) {
            this.method3443(Math.abs(this.sum(var1)) >= Math.abs(this.sum(var2)) ? var1 : var2, var3);
         }
      }
   }

   private void method3442(int[] var1) {
      if (Math.abs(this.sum(var1)) >= var1.length) {
         for (int var2 = 0; var2 < var1.length; var2++) {
            if (var1[var2] == 0) {
               this.method3443(var1, var2);
            }
         }
      }
   }

   private boolean method3443(int[] var1, int var2) {
      int var3 = -1;
      int var4 = 0;

      for (int var5 = 0; var5 < var1.length; var5++) {
         int var6 = Math.abs(var1[var5]);
         if (var5 != var2 && var6 > var4 && var6 > 1) {
            var3 = var5;
            var4 = var6;
         }
      }

      if (var3 < 0) {
         return false;
      } else {
         int var7 = var1[var3] > 0 ? 1 : -1;
         var1[var3] -= var7;
         var1[var2] += var7;
         return true;
      }
   }

   private int sum(int[] var1) {
      int var2 = 0;

      for (int var6 : var1) {
         var2 += var6;
      }

      return var2;
   }

   private int method3444() {
      this.intValue2 = 2;
      int var1 = Math.max(0, Math.min(this.intValue, this.intValue2));
      int var2 = Math.max(var1, Math.max(this.intValue, this.intValue2));
      return var1 + this.random.nextInt(var2 - var1 + 1);
   }

   private int method3445() {
      int var1 = Math.max(1, Math.min(this.intValue3, this.intValue4));
      int var2 = Math.max(var1, Math.max(this.intValue3, this.intValue4));
      if (this.random.nextFloat() < this.floatValue) {
         var2 = Math.max(var2, this.intValue5);
      }

      return var1 + this.random.nextInt(var2 - var1 + 1);
   }

   private int method3446() {
      int var1 = Math.max(0, Math.min(this.intValue6, this.intValue7));
      int var2 = Math.max(var1, Math.max(this.intValue6, this.intValue7));
      return var1 + this.random.nextInt(var2 - var1 + 1);
   }

   private void method3447() {
      this.entry3 = null;
      this.intValue9 = 0;
      this.intValue11 = this.method3446();
      this.intValue10 = this.method3444();
   }

   private Class21.Entry method3448(Rotation var1, LivingEntity var2) {
      if (var1 != null && var2 != null && mc.player != null) {
         Box var3 = var2.getBoundingBox();
         Vec3d var4 = mc.player.getEyePos();
         Vec3d var5 = var3.getCenter();
         Rotation var6 = RotationUtils.method6912(var5.subtract(var4));
         Vec2f var7 = var1.method3131(var6);
         float var8 = var7.getFloatValue();
         float var9 = var7.getFloatValue2();
         if (this.isFinite(var8) && this.isFinite(var9)) {
            float var10 = Float.MAX_VALUE;
            float var11 = -Float.MAX_VALUE;
            float var12 = Float.MAX_VALUE;
            float var13 = -Float.MAX_VALUE;
            Vec3d[] var14 = new Vec3d[]{
               new Vec3d(var3.minX, var3.minY, var3.minZ),
               new Vec3d(var3.minX, var3.minY, var3.maxZ),
               new Vec3d(var3.minX, var3.maxY, var3.minZ),
               new Vec3d(var3.minX, var3.maxY, var3.maxZ),
               new Vec3d(var3.maxX, var3.minY, var3.minZ),
               new Vec3d(var3.maxX, var3.minY, var3.maxZ),
               new Vec3d(var3.maxX, var3.maxY, var3.minZ),
               new Vec3d(var3.maxX, var3.maxY, var3.maxZ)
            };

            for (Vec3d var18 : var14) {
               Rotation var19 = RotationUtils.method6912(var18.subtract(var4));
               Vec2f var20 = var1.method3131(var19);
               float var21 = var20.getFloatValue2();
               if (this.isFinite(var20.getFloatValue()) && this.isFinite(var21)) {
                  float var22 = this.method3449(var20.getFloatValue(), var8);
                  var10 = Math.min(var10, var22);
                  var11 = Math.max(var11, var22);
                  var12 = Math.min(var12, var21);
                  var13 = Math.max(var13, var21);
               }
            }

            if (var10 == Float.MAX_VALUE) {
               var10 = var8;
               var11 = var8;
               var12 = var9;
               var13 = var9;
            }

            boolean var23 = var10 <= 0.0F && var11 >= 0.0F;
            boolean var24 = var12 <= 0.0F && var13 >= 0.0F;
            return new Class21.Entry(var23, var24);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private float method3449(float var1, float var2) {
      float var3 = var1 - var2;
      return !Float.isFinite(var3) ? var1 : var2 + MathHelper.wrapDegrees(var3);
   }

   private void method3450() {
      this.entry3 = null;
      this.intValue9 = 0;
      this.intValue11 = 0;
      this.intValue10 = this.method3444();
      this.flag = true;
      this.intValue8 = Integer.MIN_VALUE;
   }

   private boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   private static final class Entry {
      public final boolean flag;

      public Entry(boolean var1, boolean var2) {
         this.flag = var1 && var2;
      }
   }

   private static final class Entry2 {
      public final int intValue;
      public final int intValue2;

      public Entry2(int var1, int var2) {
         this.intValue = var1;
         this.intValue2 = var2;
      }
   }

   private static final class Entry3 {
      private final int[] intValues;
      private final int[] intValues2;
      private final float floatValue;
      private int index;

      public Entry3(int[] var1, int[] var2, float var3) {
         this.intValues = var1;
         this.intValues2 = var2;
         this.floatValue = var3;
      }

      public Class21.Entry2 method3451() {
         Class21.Entry2 var1 = new Class21.Entry2(this.intValues[this.index], this.intValues2[this.index]);
         this.index++;
         return var1;
      }

      public boolean isDone() {
         return this.index >= this.intValues.length;
      }
   }
}

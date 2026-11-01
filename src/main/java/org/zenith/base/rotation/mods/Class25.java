package org.zenith.base.rotation.mods;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.math.MathHelper;
import org.zenith.Rotation;
import org.zenith.utility.misc.Util22;

public final class Class25 extends Util22 {
   private static final float CONST_floatValue = 0.05F;
   private static final float CONST_floatValue2 = 0.045F;
   private static final float CONST_floatValue3 = 0.06F;
   private static final float CONST_floatValue4 = 3.5F;
   private static final float CONST_floatValue5 = 1.5F;
   private static final float CONST_floatValue6 = 2.5F;
   private static final long CONST_longValue = 250000000L;
   private static final long CONST_longValue2 = 400000000L;
   private static final float CONST_floatValue7 = 8.0F;
   private boolean active;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private float floatValue11;
   private int intValue;
   private float floatValue12 = 1.0F;
   private final float floatValue13 = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
   private float floatValue14;
   private float floatValue15;
   private long longValue;
   private long longValue2;

   public Rotation method3653(Rotation var1) {
      Rotation var2 = rotationManager.method7844();
      float var3 = var2.getFloatValue();
      float var4 = var2.getFloatValue2();
      float var5 = var3 + MathHelper.wrapDegrees(var1.getFloatValue() - var3);
      float var6 = MathHelper.clamp(var1.getFloatValue2(), -90.0F, 90.0F);
      long var7 = System.nanoTime();
      if (var7 - this.longValue > 250000000L) {
         this.active = false;
         this.intValue = 0;
      }

      this.longValue = var7;
      if (this.active) {
         this.method3654(var3, var5, var6);
      }

      if (!this.active) {
         float var9 = (float)Math.hypot(var5 - var3, var6 - var4);
         if (var9 <= this.floatValue12) {
            return var2;
         }

         this.method3655(var3, var4, var5, var6, var9, var7);
      }

      if (this.intValue > 0) {
         this.intValue--;
         return var2;
      } else {
         float var19 = this.method3656(this.floatValue10 / this.floatValue9);
         this.floatValue10 = Math.min(this.floatValue10 + 1.0F, this.floatValue9);
         float var10 = this.floatValue10 / this.floatValue9;
         float var11 = this.method3656(var10);
         float var12 = method3657(this.floatValue, this.floatValue3, this.floatValue5, var11);
         float var13 = method3657(this.floatValue2, this.floatValue4, this.floatValue6, var11);
         float var14 = (float)Math.hypot(this.floatValue5 - this.floatValue, this.floatValue6 - this.floatValue2);
         float var15 = (var11 - var19) * var14;
         ThreadLocalRandom var16 = ThreadLocalRandom.current();
         float var17 = 0.035F * var15 + 0.015F;
         this.floatValue14 = this.floatValue14 * 0.55F + (float)var16.nextGaussian() * var17;
         this.floatValue15 = this.floatValue15 * 0.55F + (float)var16.nextGaussian() * var17 * 0.8F;
         float var18 = 1.0F - var10;
         var12 += this.floatValue14 * var18;
         var13 += this.floatValue15 * var18;
         if (var10 >= 1.0F) {
            this.active = false;
            this.longValue2 = var7;
         }

         var12 = var3 + MathHelper.wrapDegrees(var12 - var3);
         return new Rotation(var12, MathHelper.clamp(var13, -90.0F, 90.0F));
      }
   }

   private void method3654(float var1, float var2, float var3) {
      float var4 = var1 + MathHelper.wrapDegrees(this.floatValue7 - var1);
      float var5 = var2 - var4;
      float var6 = var3 - this.floatValue8;
      float var7 = (float)Math.hypot(var5, var6);
      if (!(var7 <= 1.0E-4F)) {
         float var8 = (float)Math.hypot(this.floatValue5 - this.floatValue, this.floatValue6 - this.floatValue2);
         if (var7 > Math.max(2.5F, var8 * 0.35F)) {
            this.active = false;
         } else {
            this.floatValue5 += var5;
            this.floatValue6 = MathHelper.clamp(this.floatValue6 + var6, -90.0F, 90.0F);
            this.floatValue3 += var5 * 0.5F;
            this.floatValue4 += var6 * 0.5F;
            this.floatValue7 = var2;
            this.floatValue8 = var3;
         }
      }
   }

   private void method3655(float var1, float var2, float var3, float var4, float var5, long var6) {
      ThreadLocalRandom var8 = ThreadLocalRandom.current();
      boolean var9 = var5 < 8.0F && var6 - this.longValue2 < 400000000L;
      float var10 = var9 ? 1.5F : 3.5F;
      float var11 = 0.045F + 0.06F * (float)(Math.log(var5 / var10 + 1.0F) / Math.log(2.0));
      var11 *= (float)Math.exp(var8.nextGaussian() * 0.12);
      this.floatValue9 = Math.max(1.6F, var11 / 0.05F);
      this.floatValue10 = 0.0F;
      float var12 = (var3 - var1) / var5;
      float var13 = (var4 - var2) / var5;
      float var14 = -var13;
      float var16;
      float var17;
      if (!var9 && !(var5 < 3.0F)) {
         var16 = method3658(var8, 0.95F, 0.035F, 0.85F, 1.04F);
         var17 = (float)var8.nextGaussian() * 0.03F * var5;
      } else {
         var16 = method3658(var8, 0.99F, 0.03F, 0.9F, 1.04F);
         var17 = (float)var8.nextGaussian() * 0.015F * var5;
      }

      this.floatValue = var1;
      this.floatValue2 = var2;
      this.floatValue5 = var1 + (var3 - var1) * var16 + var14 * var17;
      this.floatValue6 = MathHelper.clamp(var2 + (var4 - var2) * var16 + var12 * var17, -90.0F, 90.0F);
      float var18 = var8.nextFloat() < 0.7F ? this.floatValue13 : -this.floatValue13;
      float var19 = var18 * Math.abs((float)var8.nextGaussian()) * 0.07F * var5;
      this.floatValue3 = (this.floatValue + this.floatValue5) * 0.5F + var14 * var19;
      this.floatValue4 = MathHelper.clamp((this.floatValue2 + this.floatValue6) * 0.5F + var12 * var19, -90.0F, 90.0F);
      this.floatValue11 = 0.8F + var8.nextFloat() * 0.15F;
      this.intValue = !var9 && var5 > 25.0F && var8.nextFloat() < 0.5F ? 1 : 0;
      this.floatValue12 = 0.6F + var8.nextFloat() * 0.7F;
      this.floatValue7 = var3;
      this.floatValue8 = var4;
      this.active = true;
   }

   private float method3656(float var1) {
      float var2 = (float)Math.pow(MathHelper.clamp(var1, 0.0F, 1.0F), this.floatValue11);
      return var2 * var2 * var2 * (10.0F - 15.0F * var2 + 6.0F * var2 * var2);
   }

   private static float method3657(float var0, float var1, float var2, float var3) {
      float var4 = 1.0F - var3;
      return var4 * var4 * var0 + 2.0F * var4 * var3 * var1 + var3 * var3 * var2;
   }

   private static float method3658(ThreadLocalRandom var0, float var1, float var2, float var3, float var4) {
      return MathHelper.clamp(var1 + (float)var0.nextGaussian() * var2, var3, var4);
   }
}

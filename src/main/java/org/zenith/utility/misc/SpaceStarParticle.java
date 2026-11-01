package org.zenith.utility.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithColor;

public class SpaceStarParticle extends Particle {
   private static final String text = "particle.texture.spaceStar";
   private final double doubleValue;
   private final double doubleValue2;
   private final double doubleValue3;
   private final double doubleValue4;
   private final double doubleValue5;
   private final double doubleValue6;
   private final double doubleValue7;
   private final double doubleValue8;
   private final double doubleValue9;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private double doubleValue10;
   private float floatValue4 = 1.0F;

   private SpaceStarParticle(
      Vec3d var1,
      int var2,
      float var3,
      ZenithColor var4,
      float var5,
      float var6,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      float var25,
      float var26,
      float var27
   ) {
      super(var1, Vec3d.ZERO, var2, var3, var4, "particle.texture.spaceStar", var5, var6);
      this.doubleValue = var7;
      this.doubleValue2 = var9;
      this.doubleValue3 = var11;
      this.doubleValue4 = var13;
      this.doubleValue5 = var15;
      this.doubleValue6 = var17;
      this.doubleValue7 = var19;
      this.doubleValue8 = var21;
      this.doubleValue9 = var23;
      this.floatValue = var25;
      this.floatValue2 = var26;
      this.floatValue3 = var27;
   }

   public static SpaceStarParticle method3077(Vec3d var0, float var1, int var2, float var3, ZenithColor var4) {
      ThreadLocalRandom var5 = ThreadLocalRandom.current();
      double var6 = Math.pow(var5.nextDouble(0.08, 1.0), 0.72);
      double var8 = Math.max(6.0, var1 * var6);
      double var10 = var5.nextDouble(-var1 * 0.34, var1 * 0.42);
      double var12 = var5.nextDouble(Math.PI * 2);
      double var14 = var5.nextInt(4) * (Math.PI / 2);
      double var16 = var5.nextDouble(0.0015, 0.0075) * (var5.nextBoolean() ? 1.0 : -1.0);
      double var18 = var5.nextDouble(Math.PI * 2);
      double var20 = var5.nextDouble(0.015, 0.035);
      double var22 = var5.nextDouble(1.2, 4.8);
      double var24 = var5.nextDouble(0.8, 3.6);
      float var26 = var3 * var5.nextFloat(0.55F, 1.65F);
      float var27 = var5.nextFloat(0.0F, 360.0F);
      float var28 = var5.nextFloat(-0.7F, 0.7F);
      float var29 = var5.nextFloat(0.65F, 1.35F);
      float var30 = var5.nextFloat(0.05F, 0.12F);
      float var31 = var5.nextFloat(0.0F, (float) (Math.PI * 2));
      SpaceStarParticle var32 = new SpaceStarParticle(
         var0, var2, var26, var4, var27, var28, var8, var10, var12, var14, var16, var18, var20, var22, var24, var29, var30, var31
      );
      var32.doubleValue10 = var5.nextDouble(0.0, 260.0);
      var32.intValue = var5.nextInt(Math.max(2, var2 / 3), var2);
      var32.intValue2 = var32.intValue;
      var32.vec3d = var32.method3079(var0);
      var32.vec3d2 = var32.vec3d;
      return var32;
   }

   public void method3078(Vec3d var1, float var2) {
      this.intValue2 = this.intValue;
      this.vec3d2 = this.vec3d;
      this.intValue--;
      if (this.intValue > 0) {
         this.floatValue4 = Math.max(0.05F, var2);
         this.doubleValue10 = this.doubleValue10 + this.floatValue4;
         this.vec3d = this.method3079(var1);
         this.vec3d3 = this.vec3d.subtract(this.vec3d2);
         this.method6570();
      }
   }

   private Vec3d method3079(Vec3d var1) {
      double var2 = this.doubleValue10;
      double var4 = this.doubleValue * 0.055;
      double var6 = this.doubleValue3 + this.doubleValue4 + var4 + var2 * this.doubleValue5;
      double var8 = this.doubleValue + Math.sin(var2 * this.doubleValue7 + this.doubleValue6) * this.doubleValue8;
      double var10 = Math.cos(var6) * var8;
      double var12 = Math.sin(var6) * var8;
      double var14 = 4.5
         + this.doubleValue2
         + Math.sin(var2 * this.doubleValue7 * 1.7 + this.doubleValue6) * this.doubleValue9
         + Math.sin(var6 * 2.0 + this.doubleValue6) * 0.85;
      return var1.add(var10, var14, var12);
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float var1) {
      float var2 = this.method6571(var1);
      float var3 = Math.min(var2 / 0.12F, 1.0F);
      float var4 = var2 > 0.82F ? 1.0F - (var2 - 0.82F) / 0.18F : 1.0F;
      float var5 = 0.62F + 0.38F * this.method3080(var1);
      return Math.max(0.0F, var3 * var4 * var5);
   }

   public float method3080(float var1) {
      return (float)((Math.sin((this.doubleValue10 + var1 * this.floatValue4) * this.floatValue2 + this.floatValue3) + 1.0) * 0.5);
   }

   public float method3081(float var1) {
      return this.floatValue * (4.0F + this.method3080(var1) * 2.5F) * this.floatValue;
   }

   public float method3082(float var1) {
      return this.floatValue * (0.65F + this.method3080(var1) * 0.35F);
   }

   public float method3083(float var1) {
      return this.floatValue * (1.35F + this.method3080(var1) * 0.45F);
   }

   public float method3084(float var1) {
      return Math.min(10.0F, (float)this.vec3d3.length() * 18.0F * var1);
   }
}

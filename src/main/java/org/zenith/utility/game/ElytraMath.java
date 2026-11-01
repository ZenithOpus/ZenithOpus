package org.zenith.utility.game;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class ElytraMath {
   private ElytraMath() {
   }

   public static Vec3d method5972(LivingEntity var0, Vec3d var1, Vec3d var2) {
      return method5973(var0, var1, var2, 3.2);
   }

   public static Vec3d method5973(LivingEntity var0, Vec3d var1, Vec3d var2, double var3) {
      return method5974(var0, var1, var2, var3).multiply(0.99F, 0.98F, 0.99F);
   }

   public static Vec3d method5974(LivingEntity var0, Vec3d var1, Vec3d var2, double var3) {
      float var5 = (float)(-Math.asin(MathHelper.clamp(var2.y, -1.0, 1.0)));
      double var6 = Math.sqrt(var2.x * var2.x + var2.z * var2.z);
      double var8 = var1.horizontalLength();
      double var10 = method5975(var0);
      double var12 = MathHelper.square(Math.cos(var5));
      var1 = var1.add(0.0, var10 * (-1.0 + var12 * 0.75), 0.0);
      if (var1.y < 0.0 && var6 > 0.0) {
         double var14 = var1.y * -0.1 * var12;
         var1 = var1.add(var2.x * var14 / var6, var14, var2.z * var14 / var6);
      }

      if (var5 < 0.0F && var6 > 0.0) {
         double var17 = var8 * -MathHelper.sin(var5) * 0.04;
         var1 = var1.add(-var2.x * var17 / var6, var17 * var3, -var2.z * var17 / var6);
      }

      if (var6 > 0.0) {
         var1 = var1.add((var2.x / var6 * var8 - var1.x) * 0.1, 0.0, (var2.z / var6 * var8 - var1.z) * 0.1);
      }

      return var1;
   }

   public static double method5975(LivingEntity var0) {
      double var1 = var0.getFinalGravity();
      return var0.getVelocity().y <= 0.0 && var0.hasStatusEffect(StatusEffects.SLOW_FALLING) ? Math.min(var1, 0.01) : var1;
   }
}

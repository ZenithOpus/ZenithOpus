package org.zenith.utility.render;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.zenith.MinecraftAccess;

public final class MathUtils implements MinecraftAccess {
   public static double doubleValue = Math.PI * 2;
   private static final int CONST_intValue = 65536;
   private static final double CONST_doubleValue = Math.PI * 2;
   private static final double[] CONST_doubleValues = new double[65536];

   public static double sin(double var0) {
      int var2 = (int)(var0 * 10430.378350470453) & 65535;
      return CONST_doubleValues[var2];
   }

   public static double cos(double var0) {
      int var2 = (int)(var0 * 10430.378350470453 + 16384.0) & 65535;
      return CONST_doubleValues[var2];
   }

   public static float method2642(float var0, float var1, float var2) {
      float var3 = Math.min(var2 / var1, 1.0F);
      return var0 * (1.0F + var3 * var3 * 0.5F);
   }

   public static float method2643(double var0, double var2) {
      return (float)(var0 + (var2 - var0) * Math.random());
   }

   public static double method2644(double var0, double var2, double var4, double var6, double var8) {
      return Math.pow(1.0 - var0, 3.0) * var2
         + 3.0 * var0 * Math.pow(1.0 - var0, 2.0) * var4
         + 3.0 * Math.pow(var0, 2.0) * (1.0 - var0) * var6
         + Math.pow(var0, 3.0) * var8;
   }

   public static int method2645(String var0, String var1) {
      int var2 = var0.length();
      int var3 = var1.length();
      int[] var4 = new int[var3 + 1];
      int var5 = 0;

      while (var5 <= var3) {
         var4[var5] = var5++;
      }

      for (int var10 = 1; var10 <= var2; var10++) {
         int var6 = var4[0];
         var4[0] = var10;

         for (int var7 = 1; var7 <= var3; var7++) {
            int var8 = var4[var7];
            int var9 = var0.charAt(var10 - 1) == var1.charAt(var7 - 1) ? 0 : 1;
            var4[var7] = Math.min(Math.min(var4[var7] + 1, var4[var7 - 1] + 1), var6 + var9);
            var6 = var8;
         }
      }

      return var4[var3];
   }

   public static float method2646(float var0, float var1) {
      float var2 = (var0 - var1) % 360.0F;
      if (var2 < -180.0F) {
         var2 += 360.0F;
      } else if (var2 > 180.0F) {
         var2 -= 360.0F;
      }

      return var2;
   }

   public static boolean method2647(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var0 >= var4 && var0 <= var4 + var8 && var2 >= var6 && var2 <= var6 + var10;
   }

   public static boolean method2648(double var0, double var2, int var4, int var5, int var6, int var7) {
      return var0 >= var4 && var0 <= var6 && var2 >= var5 && var2 <= var7;
   }

   public static float method2649(double var0, double var2, double var4) {
      return (float)(var0 + (var2 - var0) * var4);
   }

   public static float method2650(float var0, float var1) {
      return Math.max(var0, var1) - Math.min(var0, var1);
   }

   public static double method2651(double var0, double var2) {
      if (var0 == var2) {
         return var0;
      } else {
         if (var0 > var2) {
            double var4 = var0;
            var0 = var2;
            var2 = var4;
         }

         return ThreadLocalRandom.current().nextDouble() * (var2 - var0) + var0;
      }
   }

   public static double round(double var0) {
      return (float)Math.round(var0 * 10.0) / 10.0F;
   }

   public static float round(float var0) {
      return Math.round(var0 * 10.0F) / 10.0F;
   }

   public static double method2652(double var0, double var2) {
      double var4 = Math.round(var0 / var2) * var2;
      return Math.round(var4 * 100.0) / 100.0;
   }

   public static Vec3d method2653(float var0, float var1, double var2) {
      float var4 = Math.min(var0, var1);
      float var5 = (float)(Math.cos(var4 * doubleValue / var1) * var2);
      float var6 = (float)(-Math.sin(var4 * doubleValue / var1) * var2);
      return new Vec3d(var5, 0.0, var6);
   }

   public static Vector3d method2654(Vector3d var0, Vector3d var1) {
      return new Vector3d(method2658(var0.x, var1.x), method2658(var0.y, var1.y), method2658(var0.z, var1.z));
   }

   public static Vec3d method2655(Vec3d var0, Vec3d var1) {
      return new Vec3d(method2658(var0.x, var1.x), method2658(var0.y, var1.y), method2658(var0.z, var1.z));
   }

   public static Vec3d method2656(Entity var0) {
      return var0 == null
         ? Vec3d.ZERO
         : new Vec3d(method2658(var0.lastX, var0.getX()), method2658(var0.lastY, var0.getY()), method2658(var0.lastZ, var0.getZ()));
   }

   public static float method2657(float var0, float var1) {
      return MathHelper.lerp(mc.getRenderTickCounter().getTickProgress(false), var0, var1);
   }

   public static double method2658(double var0, double var2) {
      return MathHelper.lerp((double)mc.getRenderTickCounter().getTickProgress(false), var0, var2);
   }

   public static int method2659(double var0, int var2, int var3) {
      return (int)MathHelper.lerp(mc.getRenderTickCounter().getFixedDeltaTicks() / var0, (double)var2, (double)var3);
   }

   public static float method2660(double var0, float var2, float var3) {
      return (float)MathHelper.lerp(mc.getRenderTickCounter().getFixedDeltaTicks() / var0, (double)var2, (double)var3);
   }

   public static double method2661(double var0, double var2, double var4) {
      return MathHelper.lerp(mc.getRenderTickCounter().getFixedDeltaTicks() / var0, var2, var4);
   }

   public static double method2662(Vec3d var0, Vec3d var1) {
      double var2 = var0.getX() - var1.getX();
      double var4 = var0.getY() - var1.getY();
      double var6 = var0.getZ() - var1.getZ();
      return MathHelper.sqrt((float)(var2 * var2 + var4 * var4 + var6 * var6));
   }

   public static double method2663(double var0) {
      return Math.abs(1.0 + Math.sin(var0)) / 2.0;
   }

   public static float method2664(float var0) {
      return (float)Math.sin(var0 * Math.PI / 2.0);
   }

   @Generated
   private MathUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   static {
      for (int var0 = 0; var0 < 65536; var0++) {
         CONST_doubleValues[var0] = Math.sin(var0 * (Math.PI * 2) / 65536.0);
      }
   }
}

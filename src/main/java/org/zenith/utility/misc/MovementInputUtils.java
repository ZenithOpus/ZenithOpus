package org.zenith.utility.misc;

import java.util.Objects;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.MoveInputEvent;

public final class MovementInputUtils implements MinecraftAccess {
   public static boolean method7418() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F || mc.player.input.playerInput.jump();
   }

   public static double[] method7419(double var0, float var2) {
      float var3 = ViaCompat.method4846(mc.player.input);
      float var4 = ViaCompat.method4847(mc.player.input);
      if (var3 != 0.0F) {
         if (var4 > 0.0F) {
            var2 += var3 > 0.0F ? -25.0F : 45.0F;
         } else if (var4 < 0.0F) {
            var2 += var3 > 0.0F ? 25.0F : -25.0F;
         }

         var4 = 0.0F;
         var3 = var3 > 0.0F ? 1.0F : -1.0F;
      }

      double var5 = Math.sin(Math.toRadians(var2 + 90.0F));
      double var7 = Math.cos(Math.toRadians(var2 + 90.0F));
      double var9 = var3 * var0 * var7 + var4 * var0 * var5;
      double var11 = var3 * var0 * var5 - var4 * var0 * var7;
      return new double[]{var9, var11};
   }

   public static double[] method7420(double var0, float var2, Vec3d var3, double var4) {
      float var6 = ViaCompat.method4846(mc.player.input);
      float var7 = ViaCompat.method4847(mc.player.input);
      double var8 = var3.x;
      double var10 = var3.z;
      if (var4 < 1.0E-8) {
         return new double[]{0.0, 0.0};
      } else {
         if (var6 != 0.0F) {
            if (var7 > 0.0F) {
               var2 += var6 > 0.0F ? -45.0F : 45.0F;
            } else if (var7 < 0.0F) {
               var2 += var6 > 0.0F ? 45.0F : -45.0F;
            }

            var7 = 0.0F;
            var6 = var6 > 0.0F ? 1.0F : -1.0F;
         }

         double var12 = Math.sin(Math.toRadians(var2 + 90.0F));
         double var14 = Math.cos(Math.toRadians(var2 + 90.0F));
         double var16 = var6 * var0 * var14 + var7 * var0 * var12;
         double var18 = var6 * var0 * var12 - var7 * var0 * var14;
         double var20 = var8 - var16;
         double var22 = var10 - var18;
         if (Math.signum(var8) != Math.signum(var20)) {
            double var24 = Math.abs(var8) / (Math.abs(var16) + 1.0E-8);
            var16 *= var24 * 0.9;
         }

         if (Math.signum(var10) != Math.signum(var22)) {
            double var26 = Math.abs(var10) / (Math.abs(var18) + 1.0E-8);
            var18 *= var26 * 0.9;
         }

         return new double[]{var16, var18, 1.0};
      }
   }

   public static double[] method7421(double var0) {
      return method7419(var0, mc.player.getYaw());
   }

   public static void method7422(double var0) {
      double[] var2 = method7421(var0);
      Objects.requireNonNull(mc.player).setVelocity(var2[0], mc.player.getVelocity().getY(), var2[1]);
   }

   public static void method7423(double var0, double var2) {
      double[] var4 = method7421(var0);
      Objects.requireNonNull(mc.player).setVelocity(var4[0], var2, var4[1]);
   }

   public static double method7424(Vec3d var0, float var1) {
      float var2 = (float)Math.atan2(-var0.x, var0.z);
      double var3 = Math.toRadians(MathHelper.wrapDegrees(var1));
      return Math.toDegrees(MathHelper.wrapDegrees(var2 - var3));
   }

   public static PlayerInput method7425(PlayerInput var0, double var1, float var3) {
      boolean var4 = var0.forward();
      boolean var5 = var0.backward();
      boolean var6 = var0.left();
      boolean var7 = var0.right();
      if (var1 >= -90.0F + var3 && var1 <= 90.0F - var3) {
         var4 = true;
      } else if (var1 < -90.0F - var3 || var1 > 90.0F + var3) {
         var5 = true;
      }

      if (var1 >= 0.0F + var3 && var1 <= 180.0F - var3) {
         var7 = true;
      } else if (var1 >= -180.0F + var3 && var1 <= 0.0F - var3) {
         var6 = true;
      }

      return new PlayerInput(var4, var5, var6, var7, var0.jump(), var0.sneak(), var0.sprint());
   }

   public static void method7426(MoveInputEvent var0, float var1, float var2) {
      float var3 = ViaCompat.method4846(mc.player.input);
      float var4 = ViaCompat.method4847(mc.player.input);
      double var5 = MathHelper.wrapDegrees(Math.toDegrees(method7427(mc.player.isGliding() ? var1 : var2, var3, var4)));
      if (var3 != 0.0F || var4 != 0.0F) {
         float var7 = 0.0F;
         float var8 = 0.0F;
         float var9 = Float.MAX_VALUE;

         for (float var10 = -1.0F; var10 <= 1.0F; var10++) {
            for (float var11 = -1.0F; var11 <= 1.0F; var11++) {
               if (var11 != 0.0F || var10 != 0.0F) {
                  double var12 = MathHelper.wrapDegrees(Math.toDegrees(method7427(var1, var10, var11)));
                  double var14 = Math.abs(var5 - var12);
                  if (var14 < var9) {
                     var9 = (float)var14;
                     var7 = var10;
                     var8 = var11;
                  }
               }
            }
         }

         var0.method3042(var7, var8);
      }
   }

   public static double method7427(float var0, float var1, float var2) {
      if (var1 < 0.0F) {
         var0 += 180.0F;
      }

      float var3 = 1.0F;
      if (var1 < 0.0F) {
         var3 = -0.5F;
      }

      if (var1 > 0.0F) {
         var3 = 0.5F;
      }

      if (var2 > 0.0F) {
         var0 -= 90.0F * var3;
      }

      if (var2 < 0.0F) {
         var0 += 90.0F * var3;
      }

      return Math.toRadians(var0);
   }

   public static PlayerInput method7428(PlayerInput var0, double var1) {
      return method7425(var0, var1, 20.0F);
   }

   public static double method7429(Entity var0) {
      double var1 = var0.getX() - var0.lastX;
      double var3 = var0.getY() - var0.lastY;
      double var5 = var0.getZ() - var0.lastZ;
      return Math.sqrt(var1 * var1 + var5 * var5 + var3 * var3);
   }

   @Generated
   private MovementInputUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

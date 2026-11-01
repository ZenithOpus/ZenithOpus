package org.zenith.utility.game;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class Class4 {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final double CONST_doubleValue = 0.05;
   private static final int CONST_intValue = 5;
   private static final double CONST_doubleValue2 = 0.12;
   private static final double CONST_doubleValue3 = 0.72;
   private static final double CONST_doubleValue4 = 0.08;
   private static final double CONST_doubleValue5 = 0.98;
   private static final double CONST_doubleValue6 = -0.5;
   private static final float CONST_floatValue = 0.6F;
   private static final float CONST_floatValue2 = 0.91F;
   private static final double CONST_doubleValue7 = 0.52;
   private static final double CONST_doubleValue8 = 0.45;
   private static final double CONST_doubleValue9 = 2.0;
   private static final double CONST_doubleValue10 = 10.0;
   private static final double CONST_doubleValue11 = 1.1;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private double doubleValue7;
   private double doubleValue8;
   private double doubleValue9;
   private float floatValue;
   private float floatValue2;
   private boolean flag;
   private boolean flag2;
   private double doubleValue10;
   private long longValue;
   private int ticks;
   private int intValue = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
   private int intValue2;
   private boolean flag3;
   private double doubleValue11;

   public long method455() {
      return this.longValue;
   }

   public void method456(long var1) {
      this.longValue = var1;
   }

   public boolean method457() {
      return this.flag3;
   }

   public void method458(Vec3d var1, float var2, boolean var3, boolean var4, LivingEntity var5, double var6) {
      if (!this.flag2) {
         this.method461(var1);
         this.flag2 = true;
      }

      this.doubleValue10 = Math.min(this.doubleValue10 + Math.max(0.0, var6), 0.25);
      int var8 = 0;

      while (this.doubleValue10 >= 0.05 && var8 < 5) {
         this.doubleValue10 -= 0.05;
         var8++;
         this.method462(var1, var2, var3, var4, var5);
      }
   }

   public Vec3d method459() {
      float var1 = (float)MathHelper.clamp(this.doubleValue10 / 0.05, 0.0, 1.0);
      return new Vec3d(
         MathHelper.lerp((double)var1, this.doubleValue4, this.doubleValue),
         MathHelper.lerp((double)var1, this.doubleValue5, this.doubleValue2),
         MathHelper.lerp((double)var1, this.doubleValue6, this.doubleValue3)
      );
   }

   public float method460() {
      float var1 = (float)MathHelper.clamp(this.doubleValue10 / 0.05, 0.0, 1.0);
      return this.floatValue2 + MathHelper.wrapDegrees(this.floatValue - this.floatValue2) * var1;
   }

   private void method461(Vec3d var1) {
      this.doubleValue = this.doubleValue4 = var1.x;
      this.doubleValue2 = this.doubleValue5 = var1.y;
      this.doubleValue3 = this.doubleValue6 = var1.z;
      this.floatValue = this.floatValue2 = 0.0F;
      this.doubleValue7 = this.doubleValue8 = this.doubleValue9 = 0.0;
      this.doubleValue10 = 0.0;
   }

   private void method462(Vec3d var1, float var2, boolean var3, boolean var4, LivingEntity var5) {
      this.doubleValue4 = this.doubleValue;
      this.doubleValue5 = this.doubleValue2;
      this.doubleValue6 = this.doubleValue3;
      this.floatValue2 = this.floatValue;
      this.ticks++;
      if (!(method470(this.doubleValue, this.doubleValue3, var1.x, var1.z) > 10.0) && !(Math.abs(this.doubleValue2 - var1.y) > 10.0)) {
         if (!var4 && !var3) {
            this.method463();
            this.method464();
            this.method465(var1, var5, true);
         } else {
            double var6 = var4 ? Math.sin(this.ticks * 0.08) * 0.08 : 0.0;
            double var8 = var1.y + (var4 ? 1.1 : 0.0) + var6;
            this.doubleValue2 = this.doubleValue2 + (var8 - this.doubleValue2) * 0.2;
            this.doubleValue8 = 0.0;
            this.flag = false;
            this.method465(var1, var5, false);
         }

         this.method469(var1, var5);
         double var10 = this.doubleValue - this.doubleValue4;
         double var11 = this.doubleValue3 - this.doubleValue6;
         this.doubleValue11 = Math.sqrt(var10 * var10 + var11 * var11) / 0.05;
         this.flag3 = (this.doubleValue11 > (this.flag3 ? 0.5 : 0.9));
      } else {
         this.method461(var1);
      }
   }

   private void method463() {
      this.doubleValue8 -= 0.08;
      this.doubleValue8 *= 0.98;
      if (this.doubleValue8 < -0.5) {
         this.doubleValue8 = -0.5;
      }
   }

   private void method464() {
      this.flag = false;
      if (!method472(this.doubleValue, this.doubleValue2 + this.doubleValue8, this.doubleValue3)) {
         this.doubleValue2 = this.doubleValue2 + this.doubleValue8;
      } else {
         if (this.doubleValue8 < 0.0) {
            double var1 = this.doubleValue2 + this.doubleValue8;
            double var3 = this.doubleValue2;

            for (int var5 = 0; var5 < 8; var5++) {
               double var6 = (var1 + var3) * 0.5;
               if (method472(this.doubleValue, var6, this.doubleValue3)) {
                  var1 = var6;
               } else {
                  var3 = var6;
               }
            }

            this.doubleValue2 = var3;
            this.flag = true;
         }

         this.doubleValue8 = 0.0;
      }

      if (!this.flag && method472(this.doubleValue, this.doubleValue2 - 0.02, this.doubleValue3)) {
         this.flag = true;
      }
   }

   private void method465(Vec3d var1, LivingEntity var2, boolean var3) {
      double var4 = !this.flag && var3 ? 0.2 : 1.0;
      if (var2 != null && var2.isAlive()) {
         this.method468(var1, var2);
      } else {
         this.intValue2 = 0;
         double var6;
         if ((var6 = method470(this.doubleValue, this.doubleValue3, var1.x, var1.z)) > 2.0) {
            double var8 = Math.min(1.0, (var6 - 2.0) / 3.0) * var4;
            double var10 = (var1.x - this.doubleValue) / var6;
            double var12 = (var1.z - this.doubleValue3) / var6;
            this.doubleValue7 += var10 * var8;
            this.doubleValue9 += var12 * var8;
         } else if (var6 < 0.1) {
            double var15 = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0;
            this.doubleValue7 = this.doubleValue7 + -Math.sin(var15) * 0.1;
            this.doubleValue9 = this.doubleValue9 + Math.cos(var15) * 0.1;
         }
      }

      if (var3) {
         this.method466();
         this.method467();
      } else {
         this.doubleValue = this.doubleValue + this.doubleValue7;
         this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
      }

      float var14 = !this.flag && var3 ? 0.91F : 0.6F;
      this.doubleValue7 *= var14;
      this.doubleValue9 *= var14;
   }

   private void method466() {
      if (!(Math.abs(this.doubleValue7) <= 1.0E-5)) {
         if (!method472(this.doubleValue + this.doubleValue7, this.doubleValue2, this.doubleValue3)) {
            this.doubleValue = this.doubleValue + this.doubleValue7;
         } else {
            for (double var1 = 0.1; var1 <= 0.6; var1 += 0.1) {
               if (!method472(this.doubleValue + this.doubleValue7, this.doubleValue2 + var1, this.doubleValue3)
                  && !method472(this.doubleValue, this.doubleValue2 + var1, this.doubleValue3)) {
                  this.doubleValue2 += var1;
                  this.doubleValue = this.doubleValue + this.doubleValue7;
                  return;
               }
            }

            if (this.flag
               && this.doubleValue8 <= 0.0
               && !method472(this.doubleValue + Math.signum(this.doubleValue7) * 0.35, this.doubleValue2 + 1.0, this.doubleValue3)) {
               this.doubleValue8 = 0.52;
            }

            this.doubleValue7 = 0.0;
         }
      }
   }

   private void method467() {
      if (!(Math.abs(this.doubleValue9) <= 1.0E-5)) {
         if (!method472(this.doubleValue, this.doubleValue2, this.doubleValue3 + this.doubleValue9)) {
            this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
         } else {
            for (double var1 = 0.1; var1 <= 0.6; var1 += 0.1) {
               if (!method472(this.doubleValue, this.doubleValue2 + var1, this.doubleValue3 + this.doubleValue9)
                  && !method472(this.doubleValue, this.doubleValue2 + var1, this.doubleValue3)) {
                  this.doubleValue2 += var1;
                  this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
                  return;
               }
            }

            if (this.flag
               && this.doubleValue8 <= 0.0
               && !method472(this.doubleValue, this.doubleValue2 + 1.0, this.doubleValue3 + Math.signum(this.doubleValue9) * 0.35)) {
               this.doubleValue8 = 0.52;
            }

            this.doubleValue9 = 0.0;
         }
      }
   }

   private void method468(Vec3d var1, LivingEntity var2) {
      Box var3 = var2.getBoundingBox().expand(0.1);
      double var4 = (var3.minX + var3.maxX) * 0.5;
      double var6 = (var3.minZ + var3.maxZ) * 0.5;
      double var8 = Math.max((var3.maxX - var3.minX) * 0.5, (var3.maxZ - var3.minZ) * 0.5) + 0.6;
      double var10 = var2.getY();
      if ((!(Math.abs(this.doubleValue2 - var10) > 1.0) || !this.flag) && !(method470(this.doubleValue, this.doubleValue3, var4, var6) > 4.0)) {
         double var14 = Math.atan2(this.doubleValue3 - var6, this.doubleValue - var4) + 0.3 * this.intValue;
         double var16 = Math.atan2(var1.z - var6, var1.x - var4);
         double var18;
         if ((var18 = MathHelper.wrapDegrees(Math.toDegrees(var14 - var16)) / 180.0 * Math.PI) > Math.PI / 2) {
            var18 = Math.PI / 2;
            this.intValue = -this.intValue;
         } else if (var18 < -Math.PI / 2) {
            var18 = -Math.PI / 2;
            this.intValue = -this.intValue;
         }

         var14 = var16 + var18;
         double var20 = var4 + Math.cos(var14) * var8;
         double var22 = var6 + Math.sin(var14) * var8;
         double var24 = MathHelper.lerp(0.35, this.doubleValue, var20);
         double var26 = MathHelper.lerp(0.35, this.doubleValue3, var22);
         if (!method472(var24, this.doubleValue2, this.doubleValue3) && method473(var24, this.doubleValue2, this.doubleValue3)) {
            this.doubleValue = var24;
         }

         if (!method472(this.doubleValue, this.doubleValue2, var26) && method473(this.doubleValue, this.doubleValue2, var26)) {
            this.doubleValue3 = var26;
         }

         this.doubleValue7 = 0.0;
         this.doubleValue9 = 0.0;
         if (this.ticks % 40 == 0) {
            this.intValue = -this.intValue;
         }

         if (this.flag) {
            if (++this.intValue2 >= 3) {
               this.doubleValue8 = 0.45;
               this.intValue2 = 0;
            }
         } else {
            this.intValue2 = 0;
         }
      } else {
         double var12 = Math.atan2(var1.z - var6, var1.x - var4);
         this.doubleValue = var4 + Math.cos(var12) * var8;
         this.doubleValue3 = var6 + Math.sin(var12) * var8;
         this.doubleValue2 = var10;
         this.doubleValue4 = this.doubleValue;
         this.doubleValue5 = this.doubleValue2;
         this.doubleValue6 = this.doubleValue3;
         this.doubleValue7 = this.doubleValue8 = this.doubleValue9 = 0.0;
         this.intValue2 = 0;
      }
   }

   private void method469(Vec3d var1, LivingEntity var2) {
      Vec3d var3 = var2 != null && var2.isAlive() ? var2.getEntityPos() : var1;
      this.floatValue = (float)Math.toDegrees(Math.atan2(var3.z - this.doubleValue3, var3.x - this.doubleValue)) - 90.0F;
   }

   private static double method470(double var0, double var2, double var4, double var6) {
      double var8 = var0 - var4;
      double var10 = var2 - var6;
      return Math.sqrt(var8 * var8 + var10 * var10);
   }

   private static Box method471(double var0, double var2, double var4) {
      return new Box(var0 - 0.12, var2, var4 - 0.12, var0 + 0.12, var2 + 0.72, var4 + 0.12);
   }

   private static boolean method472(double var0, double var2, double var4) {
      return (mc.world != null && !mc.world.isSpaceEmpty(method471(var0, var2, var4)));
   }

   private static boolean method473(double var0, double var2, double var4) {
      if (mc.world == null) {
         return true;
      } else {
         for (double var6 = 0.01; var6 <= 1.5; var6 += 0.25) {
            if (!mc.world.isSpaceEmpty(method471(var0, var2 - var6, var4))) {
               return true;
            }
         }

         return false;
      }
   }
}

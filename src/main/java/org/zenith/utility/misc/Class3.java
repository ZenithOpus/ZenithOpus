package org.zenith.utility.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class Class3 {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   public static final double CONST_doubleValue = 0.05;
   private static final int CONST_intValue = 5;
   private static final double CONST_doubleValue2 = 0.08;
   private static final double CONST_doubleValue3 = -0.6;
   public final Class15.Type4 type4;
   public final double doubleValue;
   public final double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public double doubleValue6;
   public double doubleValue7;
   public double doubleValue8;
   public double doubleValue9;
   public double doubleValue10;
   public double doubleValue11;
   public float floatValue;
   public float floatValue2;
   public float floatValue3;
   public float floatValue4;
   public boolean flag;
   public boolean flag2;
   public boolean flag3;
   public double doubleValue12;
   public double doubleValue13;
   public double doubleValue14;

   public Class3(Class15.Type4 var1, double var2, double var4) {
      this.type4 = var1;
      this.doubleValue = var2;
      this.doubleValue2 = var4;
   }

   public boolean method423() {
      return this.type4 == Class15.Type4.FLYER || this.type4 == Class15.Type4.FLOATER;
   }

   public int method424(double var1) {
      this.doubleValue12 = Math.min(this.doubleValue12 + Math.max(0.0, var1), 0.25);

      int var3;
      for (var3 = 0; this.doubleValue12 >= 0.05 && var3 < 5; var3++) {
         this.doubleValue12 -= 0.05;
      }

      return var3;
   }

   public void begin() {
      this.doubleValue6 = this.doubleValue3;
      this.doubleValue7 = this.doubleValue4;
      this.doubleValue8 = this.doubleValue5;
      this.floatValue2 = this.floatValue;
      this.floatValue4 = this.floatValue3;
      this.flag3 = false;
   }

   public void method425(Vec3d var1) {
      this.doubleValue3 = this.doubleValue6 = var1.x;
      this.doubleValue4 = this.doubleValue7 = var1.y;
      this.doubleValue5 = this.doubleValue8 = var1.z;
      this.doubleValue9 = this.doubleValue10 = this.doubleValue11 = 0.0;
      this.doubleValue12 = 0.0;
   }

   public void method426(double var1, double var3, double var5, double var7, double var9) {
      double var11 = var1 - this.doubleValue3;
      double var13 = var5 - this.doubleValue5;
      double var15 = Math.sqrt(var11 * var11 + var13 * var13);
      if (var15 > 0.05) {
         double var17 = Math.min(1.0, var15 / 2.0) * var7;
         double var19 = !this.method423() && !this.flag ? 0.25 : 1.0;
         double var21 = this.method423() ? 0.062 : 0.06;
         this.doubleValue9 += var11 / var15 * var17 * var21 * var19;
         this.doubleValue11 += var13 / var15 * var17 * var21 * var19;
      }

      if (this.method423()) {
         this.doubleValue14 += 0.08;
         double var23 = var3 + var9 + Math.sin(this.doubleValue14) * (this.type4 == Class15.Type4.FLOATER ? 0.12 : 0.06);
         double var26 = MathHelper.clamp(var23 - this.doubleValue4, -0.25, 0.25);

         while (this.method434(this.doubleValue3, this.doubleValue4 + var26, this.doubleValue5) && var26 < 1.0) {
            var26 += 0.1;
         }

         this.doubleValue4 += var26 * 0.25;
         this.doubleValue10 = 0.0;
         this.flag = false;
         this.method432(false);
         this.method433(false);
         this.doubleValue9 *= 0.85;
         this.doubleValue11 *= 0.85;
      } else {
         this.method430();
         this.method431();
         this.method432(true);
         this.method433(true);
         float var24 = this.flag ? 0.78F : 0.91F;
         this.doubleValue9 *= var24;
         this.doubleValue11 *= var24;
      }

      double var25 = this.doubleValue3 - this.doubleValue6;
      double var27 = this.doubleValue5 - this.doubleValue8;
      this.doubleValue13 = Math.sqrt(var25 * var25 + var27 * var27) / 0.05;
   }

   public void method427(double var1) {
      if (this.flag && this.doubleValue10 <= 0.0) {
         this.doubleValue10 = var1;
         this.flag = false;
      }
   }

   public void method428(double var1, double var3, float var5) {
      float var6 = (float)Math.toDegrees(Math.atan2(var3 - this.doubleValue5, var1 - this.doubleValue3)) - 90.0F;
      float var7 = MathHelper.wrapDegrees(var6 - this.floatValue);
      this.floatValue = this.floatValue + MathHelper.clamp(var7 * 0.28F, -var5, var5);
   }

   public void method429(double var1, double var3) {
      float var5 = (float)Math.toDegrees(Math.atan2(var3 - this.doubleValue5, var1 - this.doubleValue3)) - 90.0F;
      float var6 = MathHelper.clamp(MathHelper.wrapDegrees(var5 - this.floatValue), -55.0F, 55.0F);
      this.floatValue3 = this.floatValue3 + (var6 - this.floatValue3) * 0.2F;
   }

   private void method430() {
      this.doubleValue10 -= 0.08;
      this.doubleValue10 *= 0.98;
      if (this.doubleValue10 < -0.6) {
         this.doubleValue10 = -0.6;
      }
   }

   private void method431() {
      boolean var1 = this.flag;
      this.flag = false;
      if (!this.method434(this.doubleValue3, this.doubleValue4 + this.doubleValue10, this.doubleValue5)) {
         this.doubleValue4 = this.doubleValue4 + this.doubleValue10;
      } else {
         if (this.doubleValue10 < 0.0) {
            double var2 = this.doubleValue4 + this.doubleValue10;
            double var4 = this.doubleValue4;

            for (int var6 = 0; var6 < 8; var6++) {
               double var7 = (var2 + var4) * 0.5;
               if (this.method434(this.doubleValue3, var7, this.doubleValue5)) {
                  var2 = var7;
               } else {
                  var4 = var7;
               }
            }

            this.doubleValue4 = var4;
            this.flag = true;
         }

         this.doubleValue10 = 0.0;
      }

      if (!this.flag && this.method434(this.doubleValue3, this.doubleValue4 - 0.02, this.doubleValue5)) {
         this.flag = true;
      }

      this.flag3 = this.flag && !var1;
   }

   private void method432(boolean var1) {
      if (!(Math.abs(this.doubleValue9) <= 1.0E-5)) {
         if (!this.method434(this.doubleValue3 + this.doubleValue9, this.doubleValue4, this.doubleValue5)) {
            this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
         } else {
            for (double var2 = 0.1; var2 <= (var1 ? 0.6 : 1.2); var2 += 0.1) {
               if (!this.method434(this.doubleValue3 + this.doubleValue9, this.doubleValue4 + var2, this.doubleValue5)
                  && !this.method434(this.doubleValue3, this.doubleValue4 + var2, this.doubleValue5)) {
                  this.doubleValue4 += var2;
                  this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
                  return;
               }
            }

            if (var1 && this.flag && !this.method434(this.doubleValue3 + Math.signum(this.doubleValue9) * 0.35, this.doubleValue4 + 1.1, this.doubleValue5)) {
               this.doubleValue10 = 0.52;
            }

            this.doubleValue9 = 0.0;
         }
      }
   }

   private void method433(boolean var1) {
      if (!(Math.abs(this.doubleValue11) <= 1.0E-5)) {
         if (!this.method434(this.doubleValue3, this.doubleValue4, this.doubleValue5 + this.doubleValue11)) {
            this.doubleValue5 = this.doubleValue5 + this.doubleValue11;
         } else {
            for (double var2 = 0.1; var2 <= (var1 ? 0.6 : 1.2); var2 += 0.1) {
               if (!this.method434(this.doubleValue3, this.doubleValue4 + var2, this.doubleValue5 + this.doubleValue11)
                  && !this.method434(this.doubleValue3, this.doubleValue4 + var2, this.doubleValue5)) {
                  this.doubleValue4 += var2;
                  this.doubleValue5 = this.doubleValue5 + this.doubleValue11;
                  return;
               }
            }

            if (var1 && this.flag && !this.method434(this.doubleValue3, this.doubleValue4 + 1.1, this.doubleValue5 + Math.signum(this.doubleValue11) * 0.35)) {
               this.doubleValue10 = 0.52;
            }

            this.doubleValue11 = 0.0;
         }
      }
   }

   public boolean method434(double var1, double var3, double var5) {
      return mc.world != null
         && !mc.world
            .isSpaceEmpty(
               new Box(var1 - this.doubleValue, var3, var5 - this.doubleValue, var1 + this.doubleValue, var3 + this.doubleValue2, var5 + this.doubleValue)
            );
   }

   public Vec3d method435(float var1) {
      return new Vec3d(
         MathHelper.lerp((double)var1, this.doubleValue6, this.doubleValue3),
         MathHelper.lerp((double)var1, this.doubleValue7, this.doubleValue4),
         MathHelper.lerp((double)var1, this.doubleValue8, this.doubleValue5)
      );
   }

   public float method436(float var1) {
      return this.floatValue2 + MathHelper.wrapDegrees(this.floatValue - this.floatValue2) * var1;
   }

   public float method437(float var1) {
      return this.floatValue4 + (this.floatValue3 - this.floatValue4) * var1;
   }

   public float method438() {
      return (float)MathHelper.clamp(this.doubleValue12 / 0.05, 0.0, 1.0);
   }
}

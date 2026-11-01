package org.zenith.utility.game;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.Vec2f;

public final class Class13 {
   private static final int CONST_intValue = 59;
   private static final int CONST_intValue2 = 0;
   private static final int CONST_intValue3 = 1;
   private static final int CONST_intValue4 = 16;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 176.0F;
   private static final float CONST_floatValue3 = 8.0F;
   private static final float CONST_floatValue4 = 8.0F;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private boolean flag;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private int intValue;
   private int intValue2;
   private float floatValue11;
   private float floatValue12;
   private float floatValue13;
   private float floatValue14;
   private Class13.Entry entry;

   public float[] method1911(MinecraftClient var1, Rotation var2, LivingEntity var3, float var4, float var5, float var6) {
      if (var1.player != null && var3 != null && var3.getBoundingBox() != null && this.isFinite(var6) && !(var6 <= 0.0F)) {
         Vec3d var7 = var1.player.getEntityPos();
         Box var8 = var3.getBoundingBox();
         Vec3d var9 = this.method1916(var3, var8);
         Vec3d var10 = this.method1915(var1, var7);
         float[] var11 = this.method1912(var1, var2, var1.player.getEyePos(), var3, var8, var4, var5, var9, var10, var6);
         if (var11 != null) {
            this.entry = new Class13.Entry(var11, var7, var8, var6);
         }

         return var11;
      } else {
         return null;
      }
   }

   private float[] method1912(
      MinecraftClient var1, Rotation var2, Vec3d var3, LivingEntity var4, Box var5, float var6, float var7, Vec3d var8, Vec3d var9, float var10
   ) {
      Vec3d var11 = this.method1918(var5);
      Vec2f var12 = var2.method3131(this.method1917(var3, var11));
      float var13 = var12.getFloatValue();
      float var14 = var12.getFloatValue2();
      if (this.isFinite(var13) && this.isFinite(var14)) {
         Vec3d var15 = this.method1913(var1, var2, var3);
         Box var16 = this.method1914(var4, var5, var8);
         Vec2f var17 = var2.method3131(this.method1917(var15, this.method1918(var16)));
         float var18 = var17.getFloatValue();
         float var19 = var17.getFloatValue2();
         if (this.isFinite(var18) && this.isFinite(var19)) {
            float var20 = Float.MAX_VALUE;
            float var21 = -Float.MAX_VALUE;
            float var22 = Float.MAX_VALUE;
            float var23 = -Float.MAX_VALUE;

            for (Vec3d var27 : this.method1919(var5)) {
               Vec2f var28 = var2.method3131(this.method1917(var3, var27));
               float var29 = var28.getFloatValue();
               float var30 = var28.getFloatValue2();
               if (this.isFinite(var29) && this.isFinite(var30)) {
                  var29 = this.method1921(var29, var13);
                  var20 = Math.min(var20, var29);
                  var21 = Math.max(var21, var29);
                  var22 = Math.min(var22, var30);
                  var23 = Math.max(var23, var30);
               }
            }

            if (var20 == Float.MAX_VALUE) {
               var21 = var13;
               var20 = var13;
               var23 = var14;
               var22 = var14;
            }

            float var65 = var21 - var20;
            float var66 = var23 - var22;
            float var67 = (float)(var11.x - var3.x);
            float var68 = Math.abs((float)(var11.y - var3.y));
            float var69 = (float)(var11.z - var3.z);
            float var71 = (float)var3.distanceTo(var11);
            float var72 = this.hypot(var67, var69);
            boolean var31 = var20 <= 0.0F && var21 >= 0.0F;
            boolean var32 = var22 <= 0.0F && var23 >= 0.0F;
            float var33 = var31 && Math.abs(var65) > 1.0E-6F ? MathHelper.clamp(-var20 / var65, 0.0F, 1.0F) : 0.0F;
            float var34 = var32 && Math.abs(var66) > 1.0E-6F ? MathHelper.clamp(-var22 / var66, 0.0F, 1.0F) : 0.0F;
            boolean var35 = var31 && var32;
            this.floatValue5 = var35 ? MathHelper.clamp(this.floatValue5 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.floatValue6 = var35 ? 0.0F : Math.min(this.floatValue6 + 1.0F, 8.0F);
            float var38 = var6 - this.floatValue;
            float var39 = var7 - this.floatValue2;
            float var40 = var38 - this.floatValue3;
            float var41 = var39 - this.floatValue4;
            float var42 = this.entry == null ? var13 : this.method1924(this.entry.floatValues[0], this.entry.floatValue);
            float var43 = this.entry == null ? var14 : this.method1924(this.entry.floatValues[1], this.entry.floatValue);
            float var44 = this.entry == null ? var71 : this.entry.floatValues[16];
            float var45 = MathHelper.wrapDegrees(var13 - var42);
            float var46 = var14 - var43;
            float var47 = var71 - var44;
            float var48 = var13 / Math.max(Math.abs(var65), 0.1F);
            float var49 = var14 / Math.max(Math.abs(var66), 0.1F);
            float var50 = this.hypot(var13, var14);
            float var51 = (float)Math.atan2(var14, var13);
            float var52 = this.method1923(var6, var10);
            float var53 = this.method1923(var7, var10);
            float var54 = this.method1922(var13, var10);
            float var55 = this.method1922(var14, var10);
            float var56 = 0.0F;
            float var57 = 0.0F;
            float var58 = 0.0F;
            float var59 = 0.0F;
            if (this.entry != null) {
               float var60 = this.entry.floatValues[0];
               float var61 = this.entry.floatValues[1];
               float var62 = var60 - var52;
               float var63 = var61 - var53;
               var56 = Math.abs(var60) - Math.abs(var62);
               var57 = Math.abs(var61) - Math.abs(var63);
               var58 = this.method1920(var54 - var62, var10);
               var59 = var55 - var63;
            }

            this.floatValue9 = var52 != 0.0F ? this.floatValue9 + 1.0F : 0.0F;
            this.floatValue10 = var53 != 0.0F ? this.floatValue10 + 1.0F : 0.0F;
            int var73 = this.method1925(var52);
            int var74 = this.method1925(var53);
            this.floatValue13 = this.method1927(var52, var73, this.intValue, this.floatValue13);
            this.floatValue14 = this.method1927(var53, var74, this.intValue2, this.floatValue14);
            float var75 = this.method1928(var52, this.floatValue13);
            float var76 = this.method1928(var53, this.floatValue14);
            this.floatValue11 = this.method1926(var73, this.intValue, this.floatValue11);
            this.floatValue12 = this.method1926(var74, this.intValue2, this.floatValue12);
            if (var73 != 0) {
               this.intValue = var73;
            }

            if (var74 != 0) {
               this.intValue2 = var74;
            }

            if (var35) {
               this.floatValue7 = MathHelper.clamp(this.flag ? this.floatValue7 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.floatValue8 = 0.0F;
            } else {
               this.floatValue7 = 0.0F;
               this.floatValue8 = MathHelper.clamp(this.flag ? 1.0F : this.floatValue8 + 1.0F, 0.0F, 8.0F);
            }

            float[] var64 = new float[]{
               var54,
               var55,
               this.method1922(var20, var10),
               this.method1922(var21, var10),
               this.method1922(var22, var10),
               this.method1922(var23, var10),
               this.method1922(var65, var10),
               this.method1922(var66, var10),
               var48,
               var49,
               this.method1922(var50, var10),
               (float)Math.sin(var51),
               (float)Math.cos(var51),
               var33,
               var34,
               var35 ? 1.0F : 0.0F,
               var71,
               var72,
               var68,
               this.floatValue5,
               this.floatValue6,
               this.floatValue7,
               this.floatValue8,
               this.method1922(var45, var10),
               this.method1922(var46, var10),
               var47,
               var58,
               var59,
               this.method1922(var18, var10),
               this.method1922(var19, var10),
               (float)var8.x,
               (float)var8.y,
               (float)var8.z,
               var52,
               var53,
               var52,
               var53,
               this.method1923(var38, var10),
               this.method1923(var39, var10),
               this.method1923(var40, var10),
               this.method1923(var41, var10),
               this.floatValue9,
               this.floatValue10,
               this.floatValue11,
               this.floatValue12,
               var56,
               var57,
               var75,
               var76,
               this.floatValue13,
               this.floatValue14,
               var1.player.input.playerInput.forward() ? 1.0F : 0.0F,
               var1.player.input.playerInput.backward() ? 1.0F : 0.0F,
               var1.player.input.playerInput.left() ? 1.0F : 0.0F,
               var1.player.input.playerInput.right() ? 1.0F : 0.0F,
               var1.player.input.playerInput.jump() ? 1.0F : 0.0F,
               (float)var9.x,
               (float)var9.y,
               (float)var9.z
            };
            if (var64.length == 59 && this.method1929(var64)) {
               this.floatValue = var6;
               this.floatValue2 = var7;
               this.floatValue3 = var38;
               this.floatValue4 = var39;
               this.flag = var35;
               return var64;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void reset() {
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.flag = false;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.floatValue7 = 0.0F;
      this.floatValue8 = 0.0F;
      this.floatValue9 = 0.0F;
      this.floatValue10 = 0.0F;
      this.intValue = 0;
      this.intValue2 = 0;
      this.floatValue11 = 0.0F;
      this.floatValue12 = 0.0F;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.entry = null;
   }

   private Vec3d method1913(MinecraftClient var1, Rotation var2, Vec3d var3) {
      Vec3d var4 = var1.player.getEntityPos();
      Vec3d var5 = var3.subtract(var4);

      try {
         PlayerSimulation var6 = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(var1.player.input.playerInput));
         var6.floatValue = var2.getFloatValue();
         var6.floatValue2 = var2.getFloatValue2();
         var6.tick();
         var6.tick();
         Vec3d var7 = var6.vec3d.add(var5);
         return this.method1930(var7) ? var7 : var3;
      } catch (Throwable var8) {
         return var3;
      }
   }

   private Box method1914(LivingEntity var1, Box var2, Vec3d var3) {
      if (var1 instanceof PlayerEntity var4) {
         try {
            PlayerSimulation var5 = PlayerSimulation.method6034(var4, PlayerSimulation.Entry.method6093(var4));
            var5.tick();
            var5.tick();
            if (var5.box != null && this.method1930(var5.vec3d)) {
               return var5.box;
            }
         } catch (Throwable var6) {
         }
      }

      Vec3d var7 = var3 == null ? Vec3d.ZERO : var3.multiply(2.0);
      return this.method1930(var7) ? var2.offset(var7) : var2;
   }

   private Vec3d method1915(MinecraftClient var1, Vec3d var2) {
      if (this.entry != null) {
         Vec3d var3 = var2.subtract(this.entry.vec3d);
         if (this.method1930(var3) && var3.lengthSquared() > 1.0E-10) {
            return var3;
         }
      }

      Vec3d var4 = var1.player.getVelocity();
      return this.method1930(var4) ? var4 : Vec3d.ZERO;
   }

   private Vec3d method1916(LivingEntity var1, Box var2) {
      if (this.entry != null && this.entry.box != null) {
         Vec3d var3 = this.method1918(var2).subtract(this.method1918(this.entry.box));
         if (this.method1930(var3) && var3.lengthSquared() > 1.0E-10) {
            return var3;
         }
      }

      Vec3d var4 = var1.getVelocity();
      return this.method1930(var4) ? var4 : Vec3d.ZERO;
   }

   private Rotation method1917(Vec3d var1, Vec3d var2) {
      return RotationUtils.method6912(var2.subtract(var1));
   }

   private Vec3d method1918(Box var1) {
      return new Vec3d((var1.minX + var1.maxX) * 0.5, (var1.minY + var1.maxY) * 0.5, (var1.minZ + var1.maxZ) * 0.5);
   }

   private Vec3d[] method1919(Box var1) {
      return new Vec3d[]{
         new Vec3d(var1.minX, var1.minY, var1.minZ),
         new Vec3d(var1.minX, var1.minY, var1.maxZ),
         new Vec3d(var1.minX, var1.maxY, var1.minZ),
         new Vec3d(var1.minX, var1.maxY, var1.maxZ),
         new Vec3d(var1.maxX, var1.minY, var1.minZ),
         new Vec3d(var1.maxX, var1.minY, var1.maxZ),
         new Vec3d(var1.maxX, var1.maxY, var1.minZ),
         new Vec3d(var1.maxX, var1.maxY, var1.maxZ)
      };
   }

   private float method1920(float var1, float var2) {
      float var3 = 360.0F / var2;
      float var4 = var1 % var3;
      if (var4 >= var3 * 0.5F) {
         var4 -= var3;
      } else if (var4 < -var3 * 0.5F) {
         var4 += var3;
      }

      return var4;
   }

   private float method1921(float var1, float var2) {
      float var3 = var1 - var2;
      return !Float.isFinite(var3) ? var1 : var2 + MathHelper.wrapDegrees(var3);
   }

   private float method1922(float var1, float var2) {
      return var1 / var2;
   }

   private float method1923(float var1, float var2) {
      return Math.round(var1 / var2);
   }

   private float method1924(float var1, float var2) {
      return var1 * var2;
   }

   private float hypot(float var1, float var2) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2);
   }

   private int method1925(float var1) {
      return var1 > 0.0F ? 1 : (var1 < 0.0F ? -1 : 0);
   }

   private float method1926(int var1, int var2, float var3) {
      return var1 != 0 && var2 != 0 && var1 != var2 ? 0.0F : var3 + 1.0F;
   }

   private float method1927(float var1, int var2, int var3, float var4) {
      if (var2 == 0) {
         return 0.0F;
      } else {
         return var3 != 0 && var2 == var3 ? Math.max(var4, Math.abs(var1)) : Math.abs(var1);
      }
   }

   private float method1928(float var1, float var2) {
      return var2 > 0.0F ? Math.abs(var1) / var2 : 0.0F;
   }

   private boolean method1929(float[] var1) {
      for (float var5 : var1) {
         if (!this.isFinite(var5)) {
            return false;
         }
      }

      return true;
   }

   private boolean isFinite(float var1) {
      return Float.isFinite(var1);
   }

   private boolean method1930(Vec3d var1) {
      return var1 != null && Double.isFinite(var1.x) && Double.isFinite(var1.y) && Double.isFinite(var1.z);
   }

   private static final class Entry {
      private final float[] floatValues;
      private final Vec3d vec3d;
      private final Box box;
      private final float floatValue;

      private Entry(float[] var1, Vec3d var2, Box var3, float var4) {
         this.floatValues = (float[])var1.clone();
         this.vec3d = var2;
         this.box = var3;
         this.floatValue = var4;
      }
   }
}

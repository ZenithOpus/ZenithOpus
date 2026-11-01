package org.zenith.base.rotation.mods;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public final class Class24 extends Util22 {
   private static final float CONST_floatValue = 1.0E-4F;
   private static final float CONST_floatValue2 = 0.001F;
   private static final float CONST_floatValue3 = 1.75F;
   private static final float CONST_floatValue4 = 1.25F;
   private static final float CONST_floatValue5 = 0.4F;
   private static final float CONST_floatValue6 = 0.75F;
   private static final float CONST_floatValue7 = 2.5F;
   private static final float CONST_floatValue8 = 1.75F;
   private Rotation rotation;
   private float floatValue;
   private float floatValue2;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;

   public Class24.Data method3535(Rotation var1, Vec3d var2, Box var3, float var4, boolean var5) {
      Vec2f var6 = this.method3541(var1, var2, var3);
      if (var6 == null) {
         return new Class24.Data(var1, false, false, false);
      } else {
         float var7 = var6.getFloatValue() * 1.1F;
         float var8 = var6.getFloatValue2();
         boolean var9 = Math.abs(var7) <= 1.0E-4F && Math.abs(var8) <= 1.0E-4F;
         float var10 = Float.isFinite(var4) ? Math.max(0.0F, var4) : 0.0F;
         boolean var11 = Math.abs(var7) <= var10 && Math.abs(var8) <= var10;
         if (!var9 && (var11 || var5)) {
            Rotation var12 = var1.method3140(this.method3544(var7, var10), this.method3544(var8, var10));
            return new Class24.Data(var12, true, false, var11);
         } else {
            return new Class24.Data(var1, true, var9, var11);
         }
      }
   }

   public Class24.Data method3536(Rotation var1, Rotation var2, Vec3d var3, Box var4, float var5, int var6, int var7) {
      Vec2f var8 = this.method3541(var1, var3, var4);
      if (var8 == null) {
         this.method3537();
         return new Class24.Data(var1, false, false, false);
      } else {
         float var9 = Float.isFinite(var5) ? Math.max(0.0F, var5) : 0.0F;
         float var10 = this.method3544(var8.getFloatValue() * 1.1F, var9);
         float var11 = this.method3544(var8.getFloatValue2(), var9);
         boolean var12 = Math.abs(var8.getFloatValue() * 1.1F) <= var9 && Math.abs(var8.getFloatValue2()) <= var9;
         boolean var13 = this.intValue == var6 && (this.intValue2 == var7 || this.intValue2 == var7 - 1) && this.rotation != null;
         if (!var13) {
            this.method3538(var1, var2, var9);
         }

         Vec2f var14 = this.rotation.method3131(var1);
         this.floatValue = this.method3539(this.floatValue, var10, var14.getFloatValue(), false);
         this.floatValue2 = 0.0F;
         this.floatValue = this.method3544(this.floatValue, var9);
         this.floatValue2 = this.method3544(this.floatValue2, var9);
         Rotation var15 = var1.method3140(this.floatValue, this.floatValue2);
         Vec2f var16 = this.method3541(var15, var3, var4);
         boolean var17 = var16 != null && Math.abs(var16.getFloatValue()) <= 1.0E-4F && Math.abs(var16.getFloatValue2()) <= 1.0E-4F;
         this.rotation = var1;
         this.intValue = var6;
         this.intValue2 = var7;
         return new Class24.Data(var15, true, var17, var12);
      }
   }

   public void method3537() {
      this.rotation = null;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
   }

   private void method3538(Rotation var1, Rotation var2, float var3) {
      Vec2f var4 = var1.method3131(var2);
      if (Float.isFinite(var4.getFloatValue())
         && Float.isFinite(var4.getFloatValue2())
         && Math.abs(var4.getFloatValue()) <= var3
         && Math.abs(var4.getFloatValue2()) <= var3) {
         this.floatValue = var4.getFloatValue();
         this.floatValue2 = var4.getFloatValue2();
      } else {
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
      }

      this.rotation = var1;
   }

   private float method3539(float var1, float var2, float var3, boolean var4) {
      if (Math.abs(var3) <= 0.001F) {
         return var1;
      } else {
         float var5 = var2 - var1;
         if (Math.abs(var5) <= 1.0E-4F) {
            return var2;
         } else {
            boolean var6 = Math.signum(var3) == Math.signum(var5);
            float var7 = var6 ? (var4 ? 1.25F : 1.75F) : (var4 ? 0.75F : 0.4F);
            float var8 = 100.0F;
            float var9 = MathHelper.clamp(var3 * (var7 - 1.0F), -var8, var8);
            return this.method3540(var1, var1 + var9, var2);
         }
      }
   }

   private float method3540(float var1, float var2, float var3) {
      boolean var4 = (var3 - var1) * (var3 - var2) < 0.0F;
      return var4 ? var3 : var2;
   }

   private Vec2f method3541(Rotation var1, Vec3d var2, Box var3) {
      Vec3d var4 = var3.getCenter();
      return var1.method3131(this.method3542(var2, var4));
   }

   private Rotation method3542(Vec3d var1, Vec3d var2) {
      return RotationUtils.method6912(var2.subtract(var1));
   }

   private float method3543(float var1, float var2) {
      return var1 > 0.0F ? var1 : Math.min(var2, 0.0F);
   }

   private float method3544(float var1, float var2) {
      return MathHelper.clamp(var1, -var2, var2);
   }

   private float method3545(float var1, float var2) {
      float var3 = var1 - var2;
      return !Float.isFinite(var3) ? var1 : var2 + MathHelper.wrapDegrees(var3);
   }

   public static final class Data {
      private final Rotation rotation;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;

      public Rotation rotation() {
         return this.rotation;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation, flag, flag2, flag3);
      }

      @Override
      public String toString() {
         return "Data[" + "rotation=" + this.rotation + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + "]";
      }

      public Data(Rotation var1, boolean var2, boolean var3, boolean var4) {
         this.rotation = var1;
         this.flag = var2;
         this.flag2 = var3;
         this.flag3 = var4;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }
   }
}

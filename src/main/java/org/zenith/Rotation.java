package org.zenith;

import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.utility.misc.Vec2f;

public class Rotation {
   private final float floatValue;
   private final float floatValue2;
   private boolean flag;
   public static final Rotation INSTANCE = new Rotation(0.0F, 0.0F);

   public Rotation(float var1, float var2) {
      this(var1, var2, false);
   }

   public Rotation(float var1, float var2, boolean var3) {
      this.floatValue = var1;
      this.floatValue2 = MathHelper.clamp(var2, -90.0F, 90.0F);
      this.flag = var3;
   }

   public static Rotation method3128(Vec3d var0, Vec3d var1) {
      return method3129(var0.subtract(var1));
   }

   public static Rotation method3129(Vec3d var0) {
      double var1 = var0.x;
      double var3 = var0.y;
      double var5 = var0.z;
      return new Rotation(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var5, var1)) - 90.0),
         (float)MathHelper.wrapDegrees(-Math.toDegrees(Math.atan2(var3, Math.sqrt(var1 * var1 + var5 * var5))))
      );
   }

   public float method3130(Rotation var1) {
      return Math.min(this.method3131(var1).method5327(), 180.0F);
   }

   public Vec2f method3131(Rotation var1) {
      return new Vec2f(this.method3133(var1.floatValue, this.floatValue), this.method3133(var1.floatValue2, this.floatValue2));
   }

   public Vec2f method3132(Rotation var1) {
      return new Vec2f(var1.floatValue - this.floatValue, var1.floatValue2 - this.floatValue2);
   }

   private float method3133(float var1, float var2) {
      return MathHelper.wrapDegrees(var1 - var2);
   }

   public boolean method3134(Rotation var1, float var2) {
      return this.method3130(var1) <= var2;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public Vec3d method3135() {
      return Vec3d.fromPolar(this.floatValue2, this.floatValue);
   }

   public Rotation method3136(Rotation var1, float var2, float var3) {
      Vec2f var4 = this.method3131(var1);
      float var5 = var4.method5327();
      if (!(var5 <= 0.0F) && !Float.isNaN(var5)) {
         float var6 = Math.abs(var4.getFloatValue() / var5) * var2;
         float var7 = Math.abs(var4.getFloatValue2() / var5) * var3;
         float var8 = MathHelper.clamp(var4.getFloatValue(), -var6, var6);
         float var9 = MathHelper.clamp(var4.getFloatValue2(), -var7, var7);
         return new Rotation(this.floatValue + var8, this.floatValue2 + var9);
      } else {
         return this;
      }
   }

   public boolean method3137() {
      return Float.isInfinite(this.floatValue) || Float.isNaN(this.floatValue) || Float.isInfinite(this.floatValue2) || Float.isNaN(this.floatValue2);
   }

   public static float method3138() {
      double var0 = MinecraftClient.getInstance().options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return (float)(var0 * var0 * var0 * 8.0 * 0.15F);
   }

   public Rotation method3139(Rotation var1) {
      if (!this.flag && !this.equals(var1)) {
         Vec2f var2 = var1.method3131(this);
         float var3 = method3138();
         if (!Float.isNaN(var3) && !Float.isInfinite(var3) && !(var3 <= 0.0F)) {
            float var4 = Math.round(var2.getFloatValue() / var3) * var3;
            float var5 = Math.round(var2.getFloatValue2() / var3) * var3;
            return new Rotation(var1.getFloatValue() + var4, var1.getFloatValue2() + var5, true);
         } else {
            return this;
         }
      } else {
         return this;
      }
   }

   public Rotation method3140(float var1, float var2) {
      return new Rotation(this.floatValue + var1, MathHelper.clamp(this.floatValue2 + var2, -90.0F, 90.0F));
   }

   public Rotation method3141(Vec2f var1) {
      return new Rotation(this.floatValue + var1.getFloatValue(), MathHelper.clamp(this.floatValue2 + var1.getFloatValue2(), -90.0F, 90.0F));
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof Rotation var2) ? false : var2.floatValue == this.floatValue && var2.floatValue2 == this.floatValue2;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }
}

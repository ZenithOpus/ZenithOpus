package org.zenith.base.rotation;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;

public final class RotationSmoother implements MinecraftAccess {
   private static final RotationSmoother.Entry entry = new RotationSmoother.Entry();

   public static Rotation method7954(Vec3d var0) {
      return method7955(entry, var0);
   }

   public static Rotation method7955(RotationSmoother.Entry var0, Vec3d var1) {
      return method7957(var0, RotationUtils.method6913(var1));
   }

   public static Rotation method7956(Rotation var0) {
      return method7957(entry, var0);
   }

   public static Rotation method7957(RotationSmoother.Entry var0, Rotation var1) {
      return method7959(var0, var1, 34.0F, 18.0F, 0.55F);
   }

   public static Rotation method7958(Rotation var0, float var1, float var2, float var3) {
      return method7959(entry, var0, var1, var2, var3);
   }

   public static Rotation method7959(RotationSmoother.Entry var0, Rotation var1, float var2, float var3, float var4) {
      RotationSmoother.Entry var5 = var0 == null ? entry : var0;
      Rotation var6 = var5.rotation == null ? RotationUtils.method6911() : var5.rotation;
      Rotation var7 = method7961(var5, var6, var1, var2, var3, var4);
      return var1;
   }

   public static Rotation method7960(Rotation var0, Rotation var1, float var2, float var3, float var4) {
      return method7961(entry, var0, var1, var2, var3, var4);
   }

   public static Rotation method7961(RotationSmoother.Entry var0, Rotation var1, Rotation var2, float var3, float var4, float var5) {
      RotationSmoother.Entry var6 = var0 == null ? entry : var0;
      method7964(var6);
      float var7 = MathHelper.wrapDegrees(var2.getFloatValue() - var1.getFloatValue());
      float var8 = var2.getFloatValue2() - var1.getFloatValue2();
      float var9 = (float)Math.hypot(var7, var8);
      float var10 = MathHelper.clamp(var9 / 12.0F, 0.0F, 1.0F);
      float var11 = MathHelper.clamp(var9 / 45.0F, 0.18F, 1.0F);
      method7965(var6, var10);
      float var12 = MathHelper.clamp(var5 * var6.smoothScale, 0.08F, 0.95F);
      float var13 = method7968(var7, var3 * var6.floatValue3, var12, var11);
      float var14 = method7968(var8, var4 * var6.floatValue3, var12, var11);
      float var15 = method7966(var7, var6.floatValue4 + var6.floatValue6, var10);
      float var16 = method7966(var8, var6.floatValue5 + var6.floatValue7, var10);
      float var17 = MathHelper.clamp(var7 + var15, -var13, var13);
      float var18 = MathHelper.clamp(var8 + var16, -var14, var14);
      var6.floatValue = MathHelper.lerp(var12, var6.floatValue, var17);
      var6.floatValue2 = MathHelper.lerp(var12, var6.floatValue2, var18);
      var6.floatValue = method7967(var6.floatValue, var7, var9);
      var6.floatValue2 = method7967(var6.floatValue2, var8, var9);
      Rotation var19 = var1.method3140(var6.floatValue, var6.floatValue2);
      return var19.method3139(var1);
   }

   public static void method7962(Rotation var0) {
      if (mc.player != null && var0 != null) {
         mc.player.setYaw(var0.getFloatValue());
         mc.player.setPitch(var0.getFloatValue2());
      }
   }

   public static void reset() {
      method7963(entry);
   }

   public static void method7963(RotationSmoother.Entry var0) {
      RotationSmoother.Entry var1 = var0 == null ? entry : var0;
      var1.reset();
   }

   private static void method7964(RotationSmoother.Entry var0) {
      if (var0.intValue-- <= 0) {
         var0.intValue = method7969(3, 9);
         var0.floatValue3 = method7970(0.86F, 1.18F);
         var0.smoothScale = method7970(0.82F, 1.08F);
         var0.floatValue4 = method7971(0.18F);
         var0.floatValue5 = method7971(0.11F);
      }
   }

   private static void method7965(RotationSmoother.Entry var0, float var1) {
      float var2 = 0.025F + var1 * 0.08F;
      var0.floatValue6 = MathHelper.lerp(0.22F, var0.floatValue6, method7971(var2));
      var0.floatValue7 = MathHelper.lerp(0.22F, var0.floatValue7, method7971(var2 * 0.65F));
   }

   private static float method7966(float var0, float var1, float var2) {
      float var3 = Math.signum(var0);
      float var4 = 0.0F;
      if (Math.abs(var0) > 8.0F) {
         var4 = MathHelper.clamp(var0 * method7970(0.01F, 0.035F), -1.15F, 1.15F);
      }

      return var4 + var1 * var2 + var3 * method7970(0.0F, 0.018F) * var2;
   }

   private static float method7967(float var0, float var1, float var2) {
      return !(var2 > 2.0F) && Math.signum(var0) != Math.signum(var1) ? var0 * method7970(0.35F, 0.7F) : var0;
   }

   private static float method7968(float var0, float var1, float var2, float var3) {
      float var4 = Math.abs(var0);
      float var5 = Math.max(0.1F, var1);
      float var6 = MathHelper.clamp(var4 / 35.0F, 0.22F, 1.0F);
      float var7 = 0.72F + var3 * method7970(0.22F, 0.38F);
      return Math.max(Rotation.method3138(), var5 * var6 * var2 * var7);
   }

   private static int method7969(int var0, int var1) {
      return ThreadLocalRandom.current().nextInt(var0, var1 + 1);
   }

   private static float method7970(float var0, float var1) {
      return (float)ThreadLocalRandom.current().nextDouble(var0, var1);
   }

   private static float method7971(float var0) {
      return method7970(-var0, var0);
   }

   @Generated
   private RotationSmoother() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Entry {
      private Rotation rotation;
      private float floatValue;
      private float floatValue2;
      private float floatValue3 = 1.0F;
      private float smoothScale = 1.0F;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private int intValue;

      private void reset() {
         this.rotation = null;
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
         this.floatValue3 = 1.0F;
         this.smoothScale = 1.0F;
         this.floatValue4 = 0.0F;
         this.floatValue5 = 0.0F;
         this.floatValue6 = 0.0F;
         this.floatValue7 = 0.0F;
         this.intValue = 0;
      }
   }
}

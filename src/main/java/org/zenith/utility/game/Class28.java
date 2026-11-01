package org.zenith.utility.game;

import java.lang.ref.WeakReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.zenith.utility.misc.Class42;

public final class Class28 {
   public final int intValue;
   public final long longValue = System.currentTimeMillis();
   public final int intValue2;
   public final int intValue3;
   public final long longValue2;
   public final float floatValue;
   public final double doubleValue;
   public final double doubleValue2;
   public final double doubleValue3;
   public final double doubleValue4;
   public final double doubleValue5;
   public final double doubleValue6;
   public final double doubleValue7;
   public final double doubleValue8;
   public final double doubleValue9;
   public final double doubleValue10;
   public final double doubleValue11;
   public final double doubleValue12;
   public final double doubleValue13;
   public final double doubleValue14;
   public final double doubleValue15;
   private WeakReference<LivingEntity> weakReference;
   private Class42 class42;

   public Class28(int var1, Vec3d var2, Vec3d var3, double var4, int var6, int var7, long var8) {
      this(var1, var2, var3, var4, 0.0, var2.y, var6, var7, var8);
   }

   public Class28(int var1, Vec3d var2, Vec3d var3, double var4, double var6, double var8, int var10, int var11, long var12) {
      this.intValue = var1;
      this.intValue2 = Math.max(60, var10);
      this.intValue3 = var11;
      this.longValue2 = var12;
      this.floatValue = (float)(var12 % 628L) / 100.0F;
      this.doubleValue13 = var4;
      this.doubleValue14 = var6;
      this.doubleValue15 = var8;
      this.doubleValue = var2.x;
      this.doubleValue2 = var2.y;
      this.doubleValue3 = var2.z;
      Vec3d var14 = var3.lengthSquared() > 1.0E-6 ? var3.normalize() : new Vec3d(0.0, 0.0, 1.0);
      Vec3d var15 = var14.crossProduct(new Vec3d(0.0, 1.0, 0.0));
      if (var15.lengthSquared() < 1.0E-6) {
         var15 = new Vec3d(1.0, 0.0, 0.0);
      }

      var15 = var15.normalize();
      Vec3d var16 = var15.crossProduct(var14).normalize();
      this.doubleValue4 = var14.x;
      this.doubleValue5 = var14.y;
      this.doubleValue6 = var14.z;
      this.doubleValue7 = var15.x;
      this.doubleValue8 = var15.y;
      this.doubleValue9 = var15.z;
      this.doubleValue10 = var16.x;
      this.doubleValue11 = var16.y;
      this.doubleValue12 = var16.z;
   }

   public Class28 method4652(LivingEntity var1) {
      this.weakReference = var1 == null ? null : new WeakReference<>(var1);
      return this;
   }

   public LivingEntity method4653() {
      return this.weakReference == null ? null : this.weakReference.get();
   }

   public Class42 method4654(int var1, int var2, float var3) {
      if (this.class42 == null || !this.class42.method7381(var1, var2, var3)) {
         this.class42 = Class42.method7380(var1, this, var2, var3);
      }

      return this.class42;
   }

   public float method4655(long var1) {
      return (float)(var1 - this.longValue) / this.intValue2;
   }

   public boolean method4656(long var1) {
      return var1 - this.longValue >= this.intValue2;
   }
}

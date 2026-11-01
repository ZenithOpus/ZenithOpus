package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithColor;

public abstract class Particle implements Handler7 {
   public Vec3d vec3d;
   public Vec3d vec3d2;
   public Vec3d vec3d3;
   public int intValue;
   public int intValue2;
   public final int intValue3;
   public final float floatValue;
   public ZenithColor zenithColor;
   public final String text;
   public float floatValue2;
   public final float floatValue3;

   public Particle(Vec3d var1, Vec3d var2, int var3, float var4, ZenithColor var5, String var6, float var7, float var8) {
      this.vec3d = var1;
      this.vec3d2 = var1;
      this.vec3d3 = var2;
      this.intValue = var3;
      this.intValue2 = var3;
      this.intValue3 = var3;
      this.floatValue = var4;
      this.zenithColor = var5;
      this.text = var6;
      this.floatValue2 = var7;
      this.floatValue3 = var8;
   }

   @Override
   public boolean method1555() {
      return this.intValue <= 0;
   }

   public void method6570() {
      this.floatValue2 = this.floatValue2 + this.floatValue3;
   }

   public float method6571(float var1) {
      float var2 = this.intValue2 + (this.intValue - this.intValue2) * var1;
      return 1.0F - var2 / this.intValue3;
   }

   public void method6572(Vec3d var1) {
      double var2 = this.vec3d.distanceTo(var1);
      this.intValue -= var2 > 64.0 ? 8 : 1;
   }

   @Generated
   @Override
   public Vec3d method1556() {
      return this.vec3d;
   }

   @Generated
   @Override
   public Vec3d method1557() {
      return this.vec3d2;
   }

   @Generated
   public Vec3d getVec3d3() {
      return this.vec3d3;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   @Override
   public float getSize() {
      return this.floatValue;
   }

   @Generated
   @Override
   public ZenithColor method1558() {
      return this.zenithColor;
   }

   @Generated
   @Override
   public String method1559() {
      return this.text;
   }

   @Generated
   @Override
   public float method1560() {
      return this.floatValue2;
   }

   @Generated
   public float getFloatValue3() {
      return this.floatValue3;
   }

   @Generated
   public void setVec3d(Vec3d var1) {
      this.vec3d = var1;
   }

   @Generated
   public void setVec3d2(Vec3d var1) {
      this.vec3d2 = var1;
   }

   @Generated
   public void setVec3d3(Vec3d var1) {
      this.vec3d3 = var1;
   }

   @Generated
   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   @Generated
   public void setIntValue2(int var1) {
      this.intValue2 = var1;
   }

   @Generated
   public void setZenithColor(ZenithColor var1) {
      this.zenithColor = var1;
   }

   @Generated
   public void setFloatValue2(float var1) {
      this.floatValue2 = var1;
   }
}

package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithColor;

public class Class11 implements Handler7 {
   private Vec3d vec3d;
   private Vec3d vec3d2;
   private Vec3d vec3d3;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private final int intValue4;
   private final float floatValue;
   private final ZenithColor zenithColor;
   private final String text;
   private float floatValue2;
   private final float floatValue3;
   private boolean flag;
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public Class11(Vec3d var1, Vec3d var2, int var3, float var4, ZenithColor var5, String var6, float var7, float var8) {
      this.vec3d = var1;
      this.vec3d2 = var1;
      this.vec3d3 = var2;
      this.intValue = 0;
      this.intValue2 = 0;
      this.intValue4 = var3;
      this.floatValue = var4;
      this.zenithColor = var5;
      this.text = var6;
      this.floatValue2 = var7;
      this.floatValue3 = var8;
      this.flag = false;
   }

   public void update() {
      this.intValue++;
      this.vec3d2 = this.vec3d;
      this.intValue3 = this.intValue2;
      if (this.flag) {
         this.intValue2++;
      } else if (mc.world != null) {
         float var1 = this.floatValue / 2.0F;
         double var2 = this.vec3d.x;
         double var4 = this.vec3d.y;
         double var6 = this.vec3d.z;
         Vec3d var8 = this.vec3d3;
         Box var9 = new Box(var2 - var1, var4 - var1 * 2.0F + var8.y, var6 - var1, var2 + var1, var4 + var1 + var8.y, var6 + var1);
         if (!mc.world.isSpaceEmpty(null, var9)) {
            if (var8.y < 0.0) {
               this.flag = true;
            }

            var8 = new Vec3d(var8.x, 0.0, var8.z);
         } else {
            var4 += var8.y;
         }

         Box var10 = new Box(var2 - var1 + var8.x, var4 - var1, var6 - var1, var2 + var1 + var8.x, var4 + var1, var6 + var1);
         if (!mc.world.isSpaceEmpty(null, var10)) {
            var8 = new Vec3d(0.0, var8.y, var8.z);
         } else {
            var2 += var8.x;
         }

         Box var11 = new Box(var2 - var1, var4 - var1, var6 - var1 + var8.z, var2 + var1, var4 + var1, var6 + var1 + var8.z);
         if (!mc.world.isSpaceEmpty(null, var11)) {
            var8 = new Vec3d(var8.x, var8.y, 0.0);
         } else {
            var6 += var8.z;
         }

         this.vec3d3 = var8;
         this.vec3d = new Vec3d(var2, var4, var6);
         if (this.flag) {
            this.vec3d3 = Vec3d.ZERO;
         }
      }
   }

   @Override
   public boolean method1555() {
      return this.intValue >= this.intValue4 * 2 || this.intValue2 >= this.intValue4;
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float var1) {
      if (!this.flag) {
         return 1.0F;
      } else {
         float var2 = this.intValue3 + (this.intValue2 - this.intValue3) * var1;
         return 1.0F - var2 / this.intValue4;
      }
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
   public int getIntValue4() {
      return this.intValue4;
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
   public boolean isFlag() {
      return this.flag;
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
   public void setIntValue3(int var1) {
      this.intValue3 = var1;
   }

   @Generated
   public void setFloatValue2(float var1) {
      this.floatValue2 = var1;
   }

   @Generated
   public void setFlag(boolean var1) {
      this.flag = var1;
   }
}

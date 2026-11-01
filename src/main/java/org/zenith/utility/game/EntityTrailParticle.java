package org.zenith.utility.game;

import java.lang.ref.WeakReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithColor;
import org.zenith.utility.misc.Particle;

public class EntityTrailParticle extends Particle {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private WeakReference<Entity> weakReference;
   private float floatValue;
   private float floatValue2;
   private boolean flag;
   private static final float CONST_floatValue = 0.3F;
   private static final float CONST_floatValue2 = 0.2F;

   public EntityTrailParticle(Vec3d var1, Vec3d var2, int var3, float var4, ZenithColor var5, String var6, float var7, float var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void method5662(Entity var1, float var2, float var3) {
      this.weakReference = new WeakReference<>(var1);
      this.floatValue = var2;
      this.floatValue2 = var3;
      Vec3d var4 = var1.getEntityPos();
      this.doubleValue = Math.atan2(this.vec3d.z - var4.z, this.vec3d.x - var4.x);
      this.doubleValue2 = Math.sqrt(Math.pow(this.vec3d.x - var4.x, 2.0) + Math.pow(this.vec3d.z - var4.z, 2.0));
      this.doubleValue3 = this.vec3d.y - var4.y;
   }

   public void method5663(Vec3d var1, int var2) {
      this.intValue2 = this.intValue;
      this.method6572(var1);
      if (this.intValue > 0) {
         this.vec3d2 = this.vec3d;
         float var3 = 1.0F - (float)this.intValue / this.intValue3;
         switch (var2) {
            case 1:
               this.method5665(var3);
               break;
            case 2:
               this.method5666();
               break;
            default:
               this.method5664();
         }

         this.method6570();
      }
   }

   private void method5664() {
      this.vec3d = this.vec3d.add(this.vec3d3);
      this.vec3d3 = new Vec3d(this.vec3d3.x * 0.98, this.vec3d3.y * 0.98 - 3.0E-4, this.vec3d3.z * 0.98);
      if (mc.world != null) {
         BlockPos var1 = BlockPos.ofFloored(this.vec3d.x, this.vec3d.y - 0.1, this.vec3d.z);
         if (!mc.world.getBlockState(var1).isAir()) {
            this.vec3d3 = new Vec3d(this.vec3d3.x / 1.1, -this.vec3d3.y / 1.1, this.vec3d3.z / 1.1);
         }
      }

      if (this.vec3d.y <= mc.world.getBottomY()) {
         this.intValue = 0;
      }
   }

   private void method5665(float var1) {
      float var2 = (float)Math.sin(var1 * Math.PI);
      var2 = 0.3F + var2 * 1.2F;
      this.vec3d = this.vec3d.add(this.vec3d3.multiply(var2));
      this.vec3d3 = this.vec3d3.multiply(0.96);
   }

   private void method5666() {
      float var1 = 1.0F - (float)this.intValue / this.intValue3;
      Entity var2 = this.weakReference != null ? this.weakReference.get() : null;
      if (var2 != null && !var2.isRemoved()) {
         if (!(var1 > 0.5F) && !this.flag) {
            this.doubleValue = this.doubleValue + this.floatValue / 10.0;
            this.doubleValue2 = this.doubleValue2 + this.floatValue2 / 100.0;
            double var3 = var2.getX();
            double var5 = var2.getZ();
            double var7 = var2.getY();
            double var9 = var3 + Math.cos(this.doubleValue) * this.doubleValue2;
            double var11 = var5 + Math.sin(this.doubleValue) * this.doubleValue2;
            double var13 = var7 + this.doubleValue3 + this.floatValue * 0.1;
            if (var1 < 0.3F) {
               this.vec3d = new Vec3d(var9, var13, var11);
            } else {
               float var15 = (var1 - 0.3F) / 0.2F;
               var15 = var15 * var15 * (3.0F - 2.0F * var15);
               if (this.vec3d3.equals(Vec3d.ZERO)) {
                  this.method5667();
               }

               Vec3d var16 = this.vec3d.add(this.vec3d3);
               this.vec3d3 = new Vec3d(this.vec3d3.x * 0.98, this.vec3d3.y * 0.98 - 3.0E-4, this.vec3d3.z * 0.98);
               this.vec3d = new Vec3d(
                  MathHelper.lerp((double)var15, var9, var16.x), MathHelper.lerp((double)var15, var13, var16.y), MathHelper.lerp((double)var15, var11, var16.z)
               );
            }
         } else {
            if (!this.flag) {
               this.flag = true;
               this.method5667();
            }

            this.method5664();
         }
      } else {
         if (!this.flag) {
            this.flag = true;
            this.method5667();
         }

         this.method5664();
      }
   }

   private void method5667() {
      double var1 = -Math.sin(this.doubleValue) * this.floatValue * 0.02;
      double var3 = Math.cos(this.doubleValue) * this.floatValue * 0.02;
      double var5 = Math.cos(this.doubleValue) * this.floatValue2 * 0.005;
      double var7 = Math.sin(this.doubleValue) * this.floatValue2 * 0.005;
      this.vec3d3 = new Vec3d(var1 + var5 + (Math.random() - 0.5) * 0.02, 0.015 + Math.random() * 0.025, var3 + var7 + (Math.random() - 0.5) * 0.02);
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float var1) {
      float var2 = this.method6571(var1);
      if (var2 < 0.1F) {
         return var2 / 0.1F;
      } else {
         float var3 = 0.5F;
         if (!this.flag && !(var2 > var3)) {
            return 1.0F;
         } else {
            float var4 = (var2 - var3) / (1.0F - var3);
            return 1.0F - var4 * var4;
         }
      }
   }
}

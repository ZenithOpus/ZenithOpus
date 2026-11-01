package org.zenith.utility.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithColor;

public class SparkParticle extends Particle {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final int CONST_intValue = 10;
   private Vec3d vec3d;
   private float floatValue = 1.0F;

   public SparkParticle(Vec3d var1, Vec3d var2, int var3, float var4, ZenithColor var5, String var6, float var7, float var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      ThreadLocalRandom var9 = ThreadLocalRandom.current();
      double var10 = Math.toRadians(var9.nextDouble() * 80.0);
      double var12 = var9.nextDouble() * Math.PI * 2.0;
      double var14 = Math.sin(var10);
      double var16 = -Math.cos(var10);
      this.vec3d = new Vec3d(Math.cos(var12) * var14, var16, Math.sin(var12) * var14).normalize();
   }

   public void method1448(Vec3d var1, boolean var2, float var3) {
      this.intValue2 = this.intValue;
      this.method6572(var1);
      if (this.intValue > 0) {
         this.vec3d2 = this.vec3d;
         Vec3d var4 = this.vec3d3.multiply(var3);
         if (mc.world != null) {
            int var5 = -1;
            Vec3d var6 = this.vec3d;

            for (int var7 = 1; var7 <= 10; var7++) {
               var6 = var6.add(var4);
               if (!mc.world.getBlockState(BlockPos.ofFloored(var6)).isAir()) {
                  var5 = var7;
                  break;
               }
            }

            if (var5 > 0) {
               float var9 = var5 / 10.0F;
               this.floatValue = var9 * var9;
               if (var5 <= 1) {
                  this.intValue = 0;
                  return;
               }
            } else {
               this.floatValue = Math.min(this.floatValue + 0.1F, 1.0F);
            }
         }

         this.vec3d = this.vec3d.add(var4);
         if (var2) {
            this.vec3d3 = this.vec3d3.multiply(0.9);
            this.vec3d3 = new Vec3d(this.vec3d3.x * 0.85, this.vec3d3.y - 0.008, this.vec3d3.z * 0.85);
            if (this.vec3d3.y < -0.5) {
               this.vec3d3 = new Vec3d(this.vec3d3.x, -0.5, this.vec3d3.z);
            }

            if (this.vec3d.y <= mc.world.getBottomY()) {
               this.intValue = 0;
            }
         } else {
            float var8 = 1.0F - (float)this.intValue / this.intValue3;
            if (var8 < 0.7F) {
               this.vec3d3 = this.vec3d3.multiply(0.995);
            } else {
               this.vec3d3 = this.vec3d3.multiply(0.92);
            }

            if (this.vec3d.y <= mc.world.getBottomY() || this.vec3d.y > 320.0) {
               this.intValue = 0;
            }
         }

         this.method6570();
      }
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float var1) {
      float var2 = this.method6571(var1);
      float var3;
      if (var2 < 0.1F) {
         var3 = var2 / 0.1F;
      } else if (var2 > 0.7F) {
         var3 = 1.0F - (var2 - 0.7F) / 0.3F;
      } else {
         var3 = 1.0F;
      }

      return var3 * this.floatValue;
   }
}

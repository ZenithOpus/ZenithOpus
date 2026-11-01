package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithColor;

public class FireflyParticle implements Handler7 {
   private Vec3d vec3d;
   private Vec3d vec3d2;
   private Vec3d vec3d3;
   private Vec3d vec3d4;
   private int intValue;
   private int intValue2;
   private final int intValue3;
   private final float floatValue;
   private ZenithColor zenithColor;
   private final List<FireflyParticle.Entry> items = new ArrayList<>();
   private static final int CONST_intValue = 15;
   private int intValue4 = -1;
   private float floatValue2 = 1.0F;

   public FireflyParticle(Vec3d var1, Vec3d var2, int var3, float var4, ZenithColor var5) {
      this.vec3d = var1;
      this.vec3d2 = var1;
      this.vec3d3 = Vec3d.ZERO;
      this.vec3d4 = var2;
      this.intValue = var3;
      this.intValue2 = var3;
      this.intValue3 = var3;
      this.floatValue = var4;
      this.zenithColor = var5;
   }

   @Override
   public String method1559() {
      return "particle.texture.firefly";
   }

   @Override
   public float method1560() {
      return 0.0F;
   }

   public void method4704(Vec3d var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      this.intValue2 = this.intValue;
      double var3 = this.vec3d.distanceTo(var1);
      this.intValue -= var3 > 64.0 ? 8 : 1;
      if (this.intValue > 0) {
         Vec3d var5 = this.vec3d;
         this.vec3d2 = this.vec3d;
         float var6 = 1.0F - (float)this.intValue / this.intValue3;
         if (var6 < 0.35F) {
            float var7 = 1.0F - (float)Math.pow(1.0F - var6 / 0.35F, 3.0);
            this.vec3d3 = this.vec3d4.multiply(var7);
         } else if (var6 > 0.6F) {
            float var9 = 1.0F - (float)Math.pow((var6 - 0.6F) / 0.4F, 3.0);
            this.vec3d3 = this.vec3d3.multiply(0.92 + 0.08 * var9);
         } else {
            this.vec3d3 = this.vec3d3.multiply(0.998);
         }

         if (var2.world != null) {
            this.intValue4 = -1;
            Vec3d var10 = this.vec3d;

            for (int var8 = 1; var8 <= 15; var8++) {
               var10 = var10.add(this.vec3d3);
               if (!var2.world.getBlockState(BlockPos.ofFloored(var10)).isAir()) {
                  this.intValue4 = var8;
                  break;
               }
            }

            if (this.intValue4 > 0) {
               this.floatValue2 = this.intValue4 / 15.0F;
               this.floatValue2 = this.floatValue2 * this.floatValue2;
               if (this.intValue4 <= 1) {
                  this.intValue = 0;
                  return;
               }
            } else {
               this.floatValue2 = Math.min(this.floatValue2 + 0.1F, 1.0F);
            }
         }

         this.vec3d = this.vec3d.add(this.vec3d3);
         this.items.add(new FireflyParticle.Entry(var5, this.vec3d, this.zenithColor));
         this.items.removeIf(var0 -> {
            var0.update();
            return var0.method4708();
         });
      }
   }

   @Override
   public boolean method1555() {
      return this.intValue <= 0;
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float var1) {
      float var2 = this.intValue2 + (this.intValue - this.intValue2) * var1;
      float var3 = var2 / this.intValue3;
      return var3 * this.floatValue2;
   }

   public void method4705(Vec3d var1, Vec3d var2) {
      this.vec3d2 = this.vec3d;
      this.vec3d = var1;
      this.vec3d3 = var2;
      this.vec3d4 = var2;
      this.items.add(new FireflyParticle.Entry(this.vec3d2, this.vec3d, this.zenithColor));
      this.items.removeIf(var0 -> {
         var0.update();
         return var0.method4708();
      });
   }

   public void method4706() {
      this.intValue = this.intValue3;
   }

   public void method4707() {
      this.intValue = this.intValue3 / 2;
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
   public Vec3d getVec3d4() {
      return this.vec3d4;
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
   public List<FireflyParticle.Entry> getItems() {
      return this.items;
   }

   @Generated
   public int getIntValue4() {
      return this.intValue4;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public void setVec3d3(Vec3d var1) {
      this.vec3d3 = var1;
   }

   @Generated
   public void setZenithColor(ZenithColor var1) {
      this.zenithColor = var1;
   }

   public static class Entry {
      private final Vec3d vec3d;
      private final Vec3d vec3d2;
      private final ZenithColor zenithColor;
      private int intValue;
      private static final int CONST_intValue = 20;

      public Entry(Vec3d var1, Vec3d var2, ZenithColor var3) {
         this.vec3d = var1;
         this.vec3d2 = var2;
         this.zenithColor = var3;
         this.intValue = 20;
      }

      public void update() {
         this.intValue--;
      }

      public boolean method4708() {
         return this.intValue <= 0;
      }

      public float getAlpha() {
         return this.intValue / 20.0F;
      }

      @Generated
      public Vec3d getVec3d() {
         return this.vec3d;
      }

      @Generated
      public Vec3d getVec3d2() {
         return this.vec3d2;
      }

      @Generated
      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }
   }
}

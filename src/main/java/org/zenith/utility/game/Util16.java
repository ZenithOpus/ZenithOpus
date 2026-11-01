package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import org.zenith.Rotation;
import org.zenith.utility.api.WorldStylePlanModel;

public final class Util16 {
   private Util16() {
   }

   public static Util16.Data method3048(WorldStylePlanModel var0, Rotation var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.player != null && var2.world != null && var1 != null && !var1.method3137()) {
         float[][][][] var3 = method3050(var2, var0, var1.getFloatValue());
         float[] var4 = method3049(var2, var0, var1);
         return var3 != null && var4 != null ? new Util16.Data(var3, var4) : null;
      } else {
         return null;
      }
   }

   private static float[] method3049(MinecraftClient var0, WorldStylePlanModel var1, Rotation var2) {
      Vec3d var3 = var0.player.getVelocity();
      Box var4 = var0.player.getBoundingBox();
      double var5 = Math.toRadians(var2.getFloatValue());
      float[] var7 = new float[]{
         (float)var3.x,
         (float)var3.y,
         (float)var3.z,
         method3055(var0.player.isOnGround()),
         method3055(var0.player.horizontalCollision),
         method3055(var0.player.verticalCollision),
         method3055(var0.player.isSprinting()),
         method3055(var0.player.jumping),
         method3055(var0.options.forwardKey.isPressed()),
         method3055(var0.options.backKey.isPressed()),
         method3055(var0.options.leftKey.isPressed()),
         method3055(var0.options.rightKey.isPressed()),
         method3055(var0.options.jumpKey.isPressed()),
         method3055(var0.options.sprintKey.isPressed()),
         (float)Math.sin(var5),
         (float)Math.cos(var5),
         var2.getFloatValue2() / 90.0F,
         (float)var4.getLengthX(),
         (float)var4.getLengthY(),
         (float)var4.getLengthZ(),
         (float)var0.player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED),
         (float)var0.player.getAttributeValue(EntityAttributes.GRAVITY),
         (float)var0.player.getAttributeValue(EntityAttributes.JUMP_STRENGTH),
         var0.player.getStepHeight()
      };
      if (var7.length != var1.getIntValue6()) {
         return null;
      } else {
         for (float var11 : var7) {
            if (!Float.isFinite(var11)) {
               return null;
            }
         }

         return var7;
      }
   }

   private static float[][][][] method3050(MinecraftClient var0, WorldStylePlanModel var1, float var2) {
      int var3 = var1.getIntValue8();
      float[][][][] var4 = new float[3][var3][var3][var3];
      Vec3d var5 = var0.player.getEntityPos();
      Box var6 = var0.player.getBoundingBox();
      List<Box> var7 = method3051(var0, var6);
      double var8 = Math.toRadians(var2);
      double var10 = Math.sin(var8);
      double var12 = Math.cos(var8);
      double var14 = 2.0 * var1.getFloatValue6() / var3;
      double var16 = (var1.getFloatValue8() - var1.getFloatValue7()) / var3;
      int var18 = var1.getIntValue13();

      for (int var19 = 0; var19 < var3; var19++) {
         for (int var20 = 0; var20 < var3; var20++) {
            for (int var21 = 0; var21 < var3; var21++) {
               double var22 = -var1.getFloatValue6() + (var21 + 0.5) * var14;
               double var24 = var1.getFloatValue7() + (var19 + 0.5) * var16;
               double var26 = -var1.getFloatValue6() + (var20 + 0.5) * var14;
               Vec3d var28 = method3052(var5, var12, var10, var22, var24, var26);
               int var29 = 0;

               for (int var30 = 0; var30 < var18; var30++) {
                  for (int var31 = 0; var31 < var18; var31++) {
                     for (int var32 = 0; var32 < var18; var32++) {
                        double var33 = ((var32 + 0.5) / var18 - 0.5) * var14;
                        double var35 = ((var30 + 0.5) / var18 - 0.5) * var16;
                        double var37 = ((var31 + 0.5) / var18 - 0.5) * var14;
                        Vec3d var39 = method3052(var5, var12, var10, var22 + var33, var24 + var35, var26 + var37);
                        if (method3053(var39, var7)) {
                           var29++;
                        }
                     }
                  }
               }

               var4[0][var19][var20][var21] = (float)var29 / (var18 * var18 * var18);
               double var40 = var1.getFloatValue9();

               for (Object var43_o : (Iterable<?>)(Object)(var7)) {
                  Box var43 = (Box)var43_o;
                  var40 = Math.min(var40, method3054(var28, var43));
               }

               var4[1][var19][var20][var21] = (float)(var40 / var1.getFloatValue9());
               BlockState var42 = var0.world.getBlockState(BlockPos.ofFloored(var28));
               var4[2][var19][var20][var21] = MathHelper.clamp(var42.getBlock().getSlipperiness(), 0.0F, 1.0F);
            }
         }
      }

      return var4;
   }

   private static List<Box> method3051(MinecraftClient var0, Box var1) {
      int var2 = MathHelper.floor(var1.minX) - 6;
      int var3 = MathHelper.floor(var1.maxX) + 6;
      int var4 = MathHelper.floor(var1.minY) - 3;
      int var5 = MathHelper.floor(var1.maxY) + 5;
      int var6 = MathHelper.floor(var1.minZ) - 6;
      int var7 = MathHelper.floor(var1.maxZ) + 6;
      ArrayList<Box> var8 = new ArrayList<>();
      Mutable var9 = new Mutable();

      for (int var10 = var4; var10 <= var5; var10++) {
         for (int var11 = var6; var11 <= var7; var11++) {
            for (int var12 = var2; var12 <= var3; var12++) {
               var9.set(var12, var10, var11);
               BlockState var13 = var0.world.getBlockState(var9);
               VoxelShape var14 = var13.getCollisionShape(var0.world, var9);

               for (Box var16 : var14.getBoundingBoxes()) {
                  var8.add(var16.offset(var12, var10, var11));
               }
            }
         }
      }

      return var8;
   }

   private static Vec3d method3052(Vec3d var0, double var1, double var3, double var5, double var7, double var9) {
      return new Vec3d(var0.x + var5 * var1 - var9 * var3, var0.y + var7, var0.z + var5 * var3 + var9 * var1);
   }

   private static boolean method3053(Vec3d var0, List<Box> var1) {
      for (Box var3 : var1) {
         if (var0.x >= var3.minX && var0.x < var3.maxX && var0.y >= var3.minY && var0.y < var3.maxY && var0.z >= var3.minZ && var0.z < var3.maxZ) {
            return true;
         }
      }

      return false;
   }

   private static double method3054(Vec3d var0, Box var1) {
      double var2 = Math.max(Math.max(var1.minX - var0.x, 0.0), var0.x - var1.maxX);
      double var4 = Math.max(Math.max(var1.minY - var0.y, 0.0), var0.y - var1.maxY);
      double var6 = Math.max(Math.max(var1.minZ - var0.z, 0.0), var0.z - var1.maxZ);
      return Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   private static float method3055(boolean var0) {
      return var0 ? 1.0F : 0.0F;
   }

   public static final class Data {
      private final float[][][][] floatValues;
      private final float[] floatValues2;

      public float[][][][] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, floatValues2);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + "]";
      }

      public Data(float[][][][] var1, float[] var2) {
         this.floatValues = var1;
         this.floatValues2 = var2;
      }

      public float[][][][] getFloatValues() {
         return this.floatValues;
      }

      public float[] getFloatValues2() {
         return this.floatValues2;
      }
   }
}

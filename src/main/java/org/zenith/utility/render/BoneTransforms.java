package org.zenith.utility.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.utility.api.CosmeticAnimation;
import org.zenith.utility.misc.CosmeticSkeleton;

public final class BoneTransforms {
   private static Matrix4f[] matrix4fs = new Matrix4f[0];
   private static Matrix3f[] matrix3fs = new Matrix3f[0];
   private static final float[] CONST_floatValues = new float[9];
   private static final Quaternionf quaternionf = new Quaternionf();
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector3f vector3f2 = new Vector3f();

   private BoneTransforms() {
   }

   public static void method547(
      CosmeticSkeleton var0,
      Identifier[] var1,
      MatrixStack var2,
      OrderedRenderCommandQueue var3,
      int var4,
      int var5,
      int var6,
      float var7,
      CosmeticAnimation var8,
      boolean var9
   ) {
      if (var0 != null && var1 != null && var1.length != 0 && var1[0] != null && var3 != null && var0.method7138() != 0) {
         CosmeticSkeleton.Data[] var10 = var0.getDatas();
         if (var10.length != 0) {
            boolean var11 = var6 >>> 24 < 255;
            int var12 = var0.method7138();
            int var13 = Integer.MIN_VALUE;

            for (int var14 = 0; var14 < var12; var14++) {
               int var15 = var0.method7140(var14);
               if (var15 != var13) {
                  var13 = var15;
                  Identifier var16 = var1[var15 >= 0 && var15 < var1.length ? var15 : 0];
                  if (var16 == null) {
                     var16 = var1[0];
                  }

                  RenderLayer var17 = var11 ? RenderLayers.entityTranslucent(var16) : RenderLayers.entityCutoutNoCull(var16);
                  var3.submitCustom(var2, var17, (var9x, var10x) -> method548(var0, var10, var9x, var10x, var15, var4, var5, var6, var7, var8, var9));
               }
            }
         }
      }
   }

   private static void method548(
      CosmeticSkeleton var0,
      CosmeticSkeleton.Data[] var1,
      Entry var2,
      VertexConsumer var3,
      int var4,
      int var5,
      int var6,
      int var7,
      float var8,
      CosmeticAnimation var9,
      boolean var10
   ) {
      method550(var1.length);
      method549(var0, var1, var2, var8, var9, var10);
      int var11 = -1;
      Matrix4f var12 = matrix4fs[0];
      Matrix3f var13 = matrix3fs[0];
      int var14 = var0.method7138();

      for (int var15 = 0; var15 < var14; var15++) {
         if (var0.method7140(var15) == var4) {
            int var16 = var0.method7139(var15);
            if (var16 != var11) {
               var11 = var16;
               int var17 = var16 >= 0 && var16 < var1.length ? var16 : 0;
               var12 = matrix4fs[var17];
               var13 = matrix3fs[var17];
            }

            vector3f2.set(var0.method7144(var15, 0), var0.method7144(var15, 1), var0.method7144(var15, 2));
            var13.transform(vector3f2);
            if (vector3f2.lengthSquared() > 1.0E-8F) {
               vector3f2.normalize();
            } else {
               vector3f2.set(0.0F, 1.0F, 0.0F);
            }

            for (int var18 = 0; var18 < 4; var18++) {
               vector3f.set(var0.method7141(var15, var18, 0), var0.method7141(var15, var18, 1), var0.method7141(var15, var18, 2));
               var12.transformPosition(vector3f);
               var3.vertex(
                  vector3f.x,
                  vector3f.y,
                  vector3f.z,
                  var7,
                  var0.method7142(var15, var18),
                  var0.method7143(var15, var18),
                  var6,
                  var5,
                  vector3f2.x,
                  vector3f2.y,
                  vector3f2.z
               );
            }
         }
      }
   }

   private static void method549(CosmeticSkeleton var0, CosmeticSkeleton.Data[] var1, Entry var2, float var3, CosmeticAnimation var4, boolean var5) {
      for (int var6 = 0; var6 < var1.length; var6++) {
         CosmeticSkeleton.Data var7 = var1[var6];
         Matrix4f var8 = matrix4fs[var6];
         Matrix3f var9 = matrix3fs[var6];
         int var10 = var7.getIntValue();
         if (var10 >= 0 && var10 < var6) {
            var8.set(matrix4fs[var10]);
            var9.set(matrix3fs[var10]);
         } else {
            var8.set(var2.getPositionMatrix());
            var9.set(var2.getNormalMatrix());
         }

         float var11 = var7.getRotationX();
         float var12 = var7.getRotationY();
         float var13 = var7.getFloatValue4();
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         float var17 = 1.0F;
         float var18 = 1.0F;
         float var19 = 1.0F;
         if (var4 != null && var4.method7810(var7.name(), var3, var5, CONST_floatValues)) {
            var11 += CONST_floatValues[0];
            var12 += CONST_floatValues[1];
            var13 += CONST_floatValues[2];
            var14 = CONST_floatValues[3];
            var15 = CONST_floatValues[4];
            var16 = CONST_floatValues[5];
            var17 = CONST_floatValues[6];
            var18 = CONST_floatValues[7];
            var19 = CONST_floatValues[8];
         }

         var8.translate(var7.getFloatValue() + var14, var7.getFloatValue2() + var15, var7.getFloatValue3() + var16);
         if (var11 != 0.0F || var12 != 0.0F || var13 != 0.0F) {
            quaternionf.rotationXYZ((float)Math.toRadians(-var11), (float)Math.toRadians(var12), (float)Math.toRadians(-var13));
            var8.rotate(quaternionf);
            var9.rotate(quaternionf);
         }

         if (var17 != 1.0F || var18 != 1.0F || var19 != 1.0F) {
            var8.scale(var17, var18, var19);
         }

         var8.translate(-var7.getFloatValue(), -var7.getFloatValue2(), -var7.getFloatValue3());
      }
   }

   private static void method550(int var0) {
      if (matrix4fs.length < var0) {
         Matrix4f[] var1 = new Matrix4f[var0];
         Matrix3f[] var2 = new Matrix3f[var0];
         System.arraycopy(matrix4fs, 0, var1, 0, matrix4fs.length);
         System.arraycopy(matrix3fs, 0, var2, 0, matrix3fs.length);

         for (int var3 = matrix4fs.length; var3 < var0; var3++) {
            var1[var3] = new Matrix4f();
            var2[var3] = new Matrix3f();
         }

         matrix4fs = var1;
         matrix3fs = var2;
      }
   }
}

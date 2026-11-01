package org.zenith.utility.render;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.utility.misc.Class15;

public final class BoneMatrices {
   private static final int CONST_intValue = 15728880;
   private static final int CONST_intValue2 = -1;
   private static Matrix4f[] matrix4fs = new Matrix4f[0];
   private static Matrix3f[] matrix3fs = new Matrix3f[0];
   private static final Quaternionf quaternionf = new Quaternionf();
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector3f vector3f2 = new Vector3f();
   private static final int CONST_intValue3 = 24;
   private static final int CONST_intValue4 = 4;
   private static final Vector3f vector3f3 = new Vector3f(-0.35F, 0.78F, 0.52F).normalize();
   private static final Vector3f vector3f4 = new Vector3f(0.55F, 0.35F, 0.76F).normalize();
   private static final Vector3f[] vector3fs = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};
   private static final float[] CONST_floatValues = new float[8];
   private static final Vector3f vector3f5 = new Vector3f();
   private static final Vector3f vector3f6 = new Vector3f();
   private static final Vector3f vector3f7 = new Vector3f();
   private static final Vector3f vector3f8 = new Vector3f();
   private static final Vector3f vector3f9 = new Vector3f();
   private static final Map<Identifier, RenderLayer> items = new HashMap<>();
   private static final int CONST_intValue5 = 25;
   private static final float[] CONST_floatValues2 = new float[25];
   private static final float[] CONST_floatValues3 = new float[25];
   private static final float[] CONST_floatValues4 = new float[25];
   private static final float[] CONST_floatValues5 = new float[25];
   private static final float[] CONST_floatValues6 = new float[25];
   private static final int[] CONST_intValues = new int[25];

   private BoneMatrices() {
   }

   public static void method4787(
      Class15 var0, float[] var1, int var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, int var6, boolean var7, boolean var8, int var9
   ) {
      if (var0 != null && var0.intValue3 != 0 && var4 != null && var0.identifier != null) {
         if (var0.method2033(var2)) {
            method4788(var0, var1 == null ? null : (float[])var1.clone(), var2, var3, var4, var5, var6, var7, var8, var9);
         }
      }
   }

   public static void method4788(
      Class15 var0, float[] var1, int var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, int var6, boolean var7, boolean var8, int var9
   ) {
      if (var0 != null && var0.intValue3 != 0 && var4 != null && var0.identifier != null) {
         if (var0.method2033(var2)) {
            method4796(var0, var1, var2, var3, var4, var5, var6, var7, BoneMatrices.Type.SOLID, var9);
            if (var0.method2034(2)) {
               method4796(var0, var1, var2, var3, var4, var5, var6, var7, BoneMatrices.Type.TRANSLUCENT, var9);
            }

            if (var8 && var0.method2034(1)) {
               method4796(var0, var1, var2, var3, var4, var5, var6, var7, BoneMatrices.Type.GLOW, var9);
            }

            if (method4789(var0)) {
               method4796(var0, var1, var2, var3, var4, var5, var6, var7, BoneMatrices.Type.SHINE, var9);
            }
         }
      }
   }

   private static boolean method4789(Class15 var0) {
      return var0.method2034(8) || var0.method2034(16);
   }

   public static void method4790(
      Class15 var0,
      float[] var1,
      int var2,
      MatrixStack var3,
      OrderedRenderCommandQueue var4,
      int var5,
      int var6,
      boolean var7,
      ModelPart var8,
      Consumer<MatrixStack> var9
   ) {
      method4791(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, false);
   }

   public static void method4791(
      Class15 var0,
      float[] var1,
      int var2,
      MatrixStack var3,
      OrderedRenderCommandQueue var4,
      int var5,
      int var6,
      boolean var7,
      ModelPart var8,
      Consumer<MatrixStack> var9,
      boolean var10
   ) {
      if (var0 != null && var0.intValue3 != 0 && var4 != null && var0.identifier != null && var0.method2033(var2)) {
         float[] var11 = var1 == null ? null : (float[])var1.clone();
         method4792(var0, var11, var2, var3, var4, var5, var6, BoneMatrices.Type.SOLID, var8, var9, var10);
         if (var0.method2034(2)) {
            method4792(var0, var11, var2, var3, var4, var5, var6, BoneMatrices.Type.TRANSLUCENT, var8, var9, var10);
         }

         if (var7 && var0.method2034(1)) {
            method4792(var0, var11, var2, var3, var4, var5, var6, BoneMatrices.Type.GLOW, var8, var9, var10);
         }

         if (method4789(var0)) {
            method4792(var0, var11, var2, var3, var4, var5, var6, BoneMatrices.Type.SHINE, var8, var9, var10);
         }
      }
   }

   private static void method4792(
      Class15 var0,
      float[] var1,
      int var2,
      MatrixStack var3,
      OrderedRenderCommandQueue var4,
      int var5,
      int var6,
      BoneMatrices.Type var7,
      ModelPart var8,
      Consumer<MatrixStack> var9,
      boolean var10
   ) {
      method4794(var4, var7).submitCustom(var3, method4795(var0, var7, -1), (var9x, var10x) -> {
         MatrixStack var11 = new MatrixStack();
         var11.peek().getPositionMatrix().set(var9x.getPositionMatrix());
         var11.peek().getNormalMatrix().set(var9x.getNormalMatrix());
         var8.applyTransform(var11);
         if (var9 != null) {
            var9.accept(var11);
         }

         method4798(var0, var1, var2, var11.peek(), var10x, var5, var6, true, var7, -1, var10);
      });
   }

   public static void method4793(
      Class15 var0, float[] var1, int var2, MatrixStack var3, VertexConsumerProvider var4, int var5, int var6, boolean var7, boolean var8, int var9
   ) {
      if (var0 != null && var0.intValue3 != 0 && var4 != null && var0.identifier != null && var0.method2033(var2)) {
         Entry var10 = var3.peek();
         method4797(var0, var1, var2, var10, var4.getBuffer(method4795(var0, BoneMatrices.Type.SOLID, var9)), var5, var6, var7, BoneMatrices.Type.SOLID, var9);
         if (var0.method2034(2)) {
            method4797(
               var0,
               var1,
               var2,
               var10,
               var4.getBuffer(method4795(var0, BoneMatrices.Type.TRANSLUCENT, var9)),
               var5,
               var6,
               var7,
               BoneMatrices.Type.TRANSLUCENT,
               var9
            );
         }

         if (var8 && var0.method2034(1)) {
            method4797(var0, var1, var2, var10, var4.getBuffer(method4795(var0, BoneMatrices.Type.GLOW, var9)), var5, var6, var7, BoneMatrices.Type.GLOW, var9);
         }

         if (method4789(var0)) {
            method4797(
               var0, var1, var2, var10, var4.getBuffer(method4795(var0, BoneMatrices.Type.SHINE, var9)), var5, var6, var7, BoneMatrices.Type.SHINE, var9
            );
         }
      }
   }

   private static RenderCommandQueue method4794(OrderedRenderCommandQueue var0, BoneMatrices.Type var1) {
      return (RenderCommandQueue)(switch (var1) {
         case TRANSLUCENT -> var0.getBatchingQueue(1);
         case SHINE -> var0.getBatchingQueue(2);
         default -> var0;
      });
   }

   private static RenderLayer method4795(Class15 var0, BoneMatrices.Type var1, int var2) {
      return switch (var1) {
         case SOLID -> var2 >>> 24 < 255 ? RenderLayers.entityTranslucent(var0.identifier) : RenderLayers.entityCutoutNoCull(var0.identifier);
         case TRANSLUCENT -> RenderLayers.entityTranslucent(var0.identifier);
         case GLOW -> RenderLayers.eyes(var0.identifier);
         case SHINE -> (RenderLayer)items.computeIfAbsent(var0.identifier, var0x -> RenderLayers.energySwirl(var0x, 0.0F, 0.0F));
      };
   }

   private static void method4796(
      Class15 var0,
      float[] var1,
      int var2,
      MatrixStack var3,
      OrderedRenderCommandQueue var4,
      int var5,
      int var6,
      boolean var7,
      BoneMatrices.Type var8,
      int var9
   ) {
      RenderLayer var10 = method4795(var0, var8, var9);
      method4794(var4, var8).submitCustom(var3, var10, (var8x, var9x) -> method4797(var0, var1, var2, var8x, var9x, var5, var6, var7, var8, var9));
   }

   private static void method4797(
      Class15 var0, float[] var1, int var2, Entry var3, VertexConsumer var4, int var5, int var6, boolean var7, BoneMatrices.Type var8, int var9
   ) {
      method4798(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, false);
   }

   private static void method4798(
      Class15 var0, float[] var1, int var2, Entry var3, VertexConsumer var4, int var5, int var6, boolean var7, BoneMatrices.Type var8, int var9, boolean var10
   ) {
      int var11 = var0.method2030();
      method4809(var11);

      for (int var12 = 0; var12 < var11; var12++) {
         if (var0.intValues3[var12] == var2) {
            Matrix4f var13 = matrix4fs[var12];
            Matrix3f var14 = matrix3fs[var12];
            int var15 = var0.intValues[var12];
            if (var15 >= 0) {
               var13.set(matrix4fs[var15]);
               var14.set(matrix3fs[var15]);
            } else {
               var13.set(var3.getPositionMatrix());
               var14.set(var3.getNormalMatrix());
            }

            int var16 = var12 * 9;
            float var17 = var1 == null ? var0.floatValues2[var12 * 3] : var1[var16];
            float var18 = var1 == null ? var0.floatValues2[var12 * 3 + 1] : var1[var16 + 1];
            float var19 = var1 == null ? var0.floatValues2[var12 * 3 + 2] : var1[var16 + 2];
            float var20 = var1 == null ? 0.0F : var1[var16 + 3];
            float var21 = var1 == null ? 0.0F : var1[var16 + 4];
            float var22 = var1 == null ? 0.0F : var1[var16 + 5];
            float var23 = var1 == null ? 1.0F : var1[var16 + 6];
            float var24 = var1 == null ? 1.0F : var1[var16 + 7];
            float var25 = var1 == null ? 1.0F : var1[var16 + 8];
            boolean var26 = var15 < 0 && var0.intValues2[var12] >= 0;
            float var27 = var26 ? 0.0F : var0.floatValues[var12 * 3];
            float var28 = var26 ? 0.0F : var0.floatValues[var12 * 3 + 1];
            float var29 = var26 ? 0.0F : var0.floatValues[var12 * 3 + 2];
            var13.translate((var27 + var20) / 16.0F, (var28 + var21) / 16.0F, (var29 + var22) / 16.0F);
            if (var17 != 0.0F || var18 != 0.0F || var19 != 0.0F) {
               quaternionf.rotationZYX(var19, var18, var17);
               var13.rotate(quaternionf);
               var14.rotate(quaternionf);
            }

            if (var23 != 1.0F || var24 != 1.0F || var25 != 1.0F) {
               var13.scale(var23, var24, var25);
            }
         }
      }

      int var30 = var0.method2028(var2);

      for (int var31 = var0.method2029(var2); var30 < var31; var30++) {
         int var32 = var0.intValues4[var30];
         if (var0.intValues3[var32] == var2 && (!var10 || var0.intValues[var32] < 0)) {
            int var33 = var0.intValues5[var30];
            if (var7 || (var33 & 4) == 0) {
               boolean var34 = (var33 & 2) != 0;
               boolean var35 = (var33 & 1) != 0;
               switch (var8) {
                  case SOLID:
                     if (var34) {
                        continue;
                     }
                     break;
                  case TRANSLUCENT:
                     if (!var34) {
                        continue;
                     }
                     break;
                  case GLOW:
                     if (!var35) {
                        continue;
                     }
                     break;
                  case SHINE:
                     if ((var33 & 24) == 0) {
                        continue;
                     }
               }

               if (var1 == null || var1[var32 * 9 + 6] != 0.0F || var1[var32 * 9 + 7] != 0.0F) {
                  Matrix4f var36 = matrix4fs[var32];
                  Matrix3f var37 = matrix3fs[var32];
                  vector3f2.set(var0.floatValues6[var30 * 3], var0.floatValues6[var30 * 3 + 1], var0.floatValues6[var30 * 3 + 2]);
                  var37.transform(vector3f2);
                  if (vector3f2.lengthSquared() > 1.0E-8F) {
                     vector3f2.normalize();
                  } else {
                     vector3f2.set(0.0F, 1.0F, 0.0F);
                  }

                  int var38 = !var35 && var8 != BoneMatrices.Type.GLOW ? var5 : 15728880;
                  int var39 = var8 == BoneMatrices.Type.GLOW ? -1 : var9;
                  boolean var40 = (var33 & 8) != 0;
                  if (var8 == BoneMatrices.Type.SHINE || var8 == BoneMatrices.Type.TRANSLUCENT && var40) {
                     for (int var42 = 0; var42 < 4; var42++) {
                        int var44 = var30 * 12 + var42 * 3;
                        vector3fs[var42].set(var0.floatValues4[var44], var0.floatValues4[var44 + 1], var0.floatValues4[var44 + 2]);
                        var36.transformPosition(vector3fs[var42]);
                        CONST_floatValues[var42 * 2] = var0.floatValues5[var30 * 8 + var42 * 2];
                        CONST_floatValues[var42 * 2 + 1] = var0.floatValues5[var30 * 8 + var42 * 2 + 1];
                     }

                     if (var8 == BoneMatrices.Type.SHINE && !var40) {
                        method4805(var4, var30, var9, var6, var5);
                     } else {
                        method4804(var4, var8 == BoneMatrices.Type.SHINE, var6, var38, var9);
                     }
                  } else {
                     for (int var41 = 0; var41 < 4; var41++) {
                        int var43 = var30 * 12 + var41 * 3;
                        vector3f.set(var0.floatValues4[var43], var0.floatValues4[var43 + 1], var0.floatValues4[var43 + 2]);
                        var36.transformPosition(vector3f);
                        var4.vertex(
                           vector3f.x,
                           vector3f.y,
                           vector3f.z,
                           var39,
                           var0.floatValues5[var30 * 8 + var41 * 2],
                           var0.floatValues5[var30 * 8 + var41 * 2 + 1],
                           var6,
                           var38,
                           vector3f2.x,
                           vector3f2.y,
                           vector3f2.z
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private static void method4799() {
      vector3f5.set(vector3f2);
   }

   private static float method4800(Vector3f var0, Vector3f var1, Vector3f var2, float var3) {
      vector3f8.set(var1).add(var2);
      if (vector3f8.lengthSquared() < 1.0E-8F) {
         return 0.0F;
      } else {
         vector3f8.normalize();
         return method4801(Math.max(0.0F, var0.dot(vector3f8)), (int)var3);
      }
   }

   private static float method4801(float var0, int var1) {
      float var2 = 1.0F;
      float var3 = var0;

      while (var1 > 0) {
         if ((var1 & 1) != 0) {
            var2 *= var3;
         }

         var3 *= var3;
         var1 >>= 1;
      }

      return var2;
   }

   private static void method4802(Vector3f var0, Vector3f var1) {
      var1.set(var0).negate();
      if (var1.lengthSquared() > 1.0E-8F) {
         var1.normalize();
      } else {
         var1.set(0.0F, 0.0F, 1.0F);
      }
   }

   private static float method4803(int var0) {
      if (var0 == 15728880) {
         return 1.0F;
      } else {
         float var1 = (var0 >> 4 & 15) / 15.0F;
         float var2 = (var0 >> 20 & 15) / 15.0F;
         ClientWorld var3 = MinecraftClient.getInstance().world;
         if (var3 != null) {
            var2 *= 1.0F - var3.getAmbientDarkness() / 11.0F * 0.75F;
         }

         return Math.max(0.15F, Math.min(1.0F, Math.max(var1, var2)));
      }
   }

   private static void method4804(VertexConsumer var0, boolean var1, int var2, int var3, int var4) {
      method4799();
      float var5 = Math.max(vector3fs[0].distance(vector3fs[1]), vector3fs[1].distance(vector3fs[2]));
      int var6 = Math.max(1, Math.min(4, (int)Math.ceil(var5 / 0.06F)));
      float var7 = (var4 >>> 24 & 0xFF) / 255.0F;
      float var8 = method4803(var3);
      int var9 = var6 + 1;

      for (int var10 = 0; var10 <= var6; var10++) {
         for (int var11 = 0; var11 <= var6; var11++) {
            float var12 = (float)var11 / var6;
            float var13 = (float)var10 / var6;
            method4806(var12, var13, vector3f9);
            method4802(vector3f9, vector3f7);
            float var14 = Math.abs(vector3f5.dot(vector3f7));
            float var15 = method4801(1.0F - var14, 5);
            int var16 = var10 * var9 + var11;
            CONST_floatValues2[var16] = vector3f9.x;
            CONST_floatValues3[var16] = vector3f9.y;
            CONST_floatValues4[var16] = vector3f9.z;
            CONST_floatValues5[var16] = method4807(CONST_floatValues, 0, var12, var13);
            CONST_floatValues6[var16] = method4807(CONST_floatValues, 1, var12, var13);
            if (var1) {
               vector3f8.set(vector3f3).add(vector3f7);
               float var17;
               if (vector3f8.lengthSquared() < 1.0E-8F) {
                  var17 = 0.0F;
               } else {
                  vector3f8.normalize();
                  float var18 = Math.max(0.0F, vector3f5.dot(vector3f8));
                  var17 = method4801(var18, 60) * 0.85F + method4801(var18, 10) * 0.12F;
               }

               float var27 = method4800(vector3f5, vector3f4, vector3f7, 40.0F) * 0.35F;
               float var19 = Math.min(1.0F, (var17 + var27 + var15 * 0.15F) * var7 * var8);
               int var20 = (int)(var19 * 255.0F);
               CONST_intValues[var16] = 0xFF000000 | var20 << 16 | var20 << 8 | var20;
            } else {
               float var26 = Math.min(1.0F, 0.05F + var15 * 0.2F) * var7;
               CONST_intValues[var16] = (int)(var26 * 255.0F) << 24 | 16777215;
            }
         }
      }

      int var21 = var1 ? 15728880 : var3;

      for (int var22 = 0; var22 < var6; var22++) {
         for (int var23 = 0; var23 < var6; var23++) {
            for (int var24 = 0; var24 < 4; var24++) {
               int var25 = (var22 + (var24 >= 2 ? 1 : 0)) * var9 + var23 + (var24 != 1 && var24 != 2 ? 0 : 1);
               var0.vertex(
                  CONST_floatValues2[var25],
                  CONST_floatValues3[var25],
                  CONST_floatValues4[var25],
                  CONST_intValues[var25],
                  CONST_floatValues5[var25],
                  CONST_floatValues6[var25],
                  var2,
                  var21,
                  vector3f2.x,
                  vector3f2.y,
                  vector3f2.z
               );
            }
         }
      }
   }

   private static void method4805(VertexConsumer var0, int var1, int var2, int var3, int var4) {
      method4799();
      int var5 = var1 * -1640531535;
      var5 ^= var5 >>> 15;
      var5 *= -2048144777;
      var5 ^= var5 >>> 13;
      float var6 = ((var5 & 0xFF) / 255.0F - 0.5F) * 0.9F;
      float var7 = ((var5 >>> 8 & 0xFF) / 255.0F - 0.5F) * 0.9F;
      float var8 = ((var5 >>> 16 & 0xFF) / 255.0F - 0.5F) * 0.9F;
      vector3f6.set(vector3f5).add(var6, var7, var8);
      if (!(vector3f6.lengthSquared() < 1.0E-8F)) {
         vector3f6.normalize();
         vector3f9.set(vector3fs[0]).add(vector3fs[1]).add(vector3fs[2]).add(vector3fs[3]).mul(0.25F);
         method4802(vector3f9, vector3f7);
         float var9 = method4800(vector3f6, vector3f3, vector3f7, 16.0F) * 0.9F + method4800(vector3f6, vector3f4, vector3f7, 16.0F) * 0.45F;
         float var10 = (var2 >>> 24 & 0xFF) / 255.0F;
         float var11 = Math.min(1.0F, var9 * var10 * method4803(var4));
         if (!(var11 < 0.02F)) {
            int var12 = (int)(var11 * 255.0F);
            int var13 = 0xFF000000 | var12 << 16 | var12 << 8 | var12;
            float var14 = 0.0012F;

            for (int var15 = 0; var15 < 4; var15++) {
               Vector3f var16 = vector3fs[var15];
               float var17 = vector3f9.x + (var16.x - vector3f9.x) * 1.12F + vector3f2.x * var14;
               float var18 = vector3f9.y + (var16.y - vector3f9.y) * 1.12F + vector3f2.y * var14;
               float var19 = vector3f9.z + (var16.z - vector3f9.z) * 1.12F + vector3f2.z * var14;
               var0.vertex(
                  var17,
                  var18,
                  var19,
                  var13,
                  CONST_floatValues[var15 * 2],
                  CONST_floatValues[var15 * 2 + 1],
                  var3,
                  15728880,
                  vector3f2.x,
                  vector3f2.y,
                  vector3f2.z
               );
            }
         }
      }
   }

   private static void method4806(float var0, float var1, Vector3f var2) {
      float var3 = vector3fs[0].x + (vector3fs[1].x - vector3fs[0].x) * var0;
      float var4 = vector3fs[0].y + (vector3fs[1].y - vector3fs[0].y) * var0;
      float var5 = vector3fs[0].z + (vector3fs[1].z - vector3fs[0].z) * var0;
      float var6 = vector3fs[3].x + (vector3fs[2].x - vector3fs[3].x) * var0;
      float var7 = vector3fs[3].y + (vector3fs[2].y - vector3fs[3].y) * var0;
      float var8 = vector3fs[3].z + (vector3fs[2].z - vector3fs[3].z) * var0;
      var2.set(var3 + (var6 - var3) * var1, var4 + (var7 - var4) * var1, var5 + (var8 - var5) * var1);
   }

   private static float method4807(float[] var0, int var1, float var2, float var3) {
      float var4 = var0[var1] + (var0[2 + var1] - var0[var1]) * var2;
      float var5 = var0[6 + var1] + (var0[4 + var1] - var0[6 + var1]) * var2;
      return var4 + (var5 - var4) * var3;
   }

   public static void method4808(Class15 var0, float[] var1, int var2, Matrix4f var3) {
      int[] var4 = new int[var0.method2030()];
      int var5 = 0;

      for (int var6 = var2; var6 >= 0 && var5 < var4.length; var6 = var0.intValues[var6]) {
         var4[var5++] = var6;
      }

      for (int var19 = var5 - 1; var19 >= 0; var19--) {
         int var7 = var4[var19];
         int var8 = var7 * 9;
         boolean var9 = var0.intValues[var7] < 0 && var0.intValues2[var7] >= 0;
         float var10 = var9 ? 0.0F : var0.floatValues[var7 * 3];
         float var11 = var9 ? 0.0F : var0.floatValues[var7 * 3 + 1];
         float var12 = var9 ? 0.0F : var0.floatValues[var7 * 3 + 2];
         float var13 = var1 == null ? 0.0F : var1[var8 + 3];
         float var14 = var1 == null ? 0.0F : var1[var8 + 4];
         float var15 = var1 == null ? 0.0F : var1[var8 + 5];
         var3.translate((var10 + var13) / 16.0F, (var11 + var14) / 16.0F, (var12 + var15) / 16.0F);
         float var16 = var1 == null ? var0.floatValues2[var7 * 3] : var1[var8];
         float var17 = var1 == null ? var0.floatValues2[var7 * 3 + 1] : var1[var8 + 1];
         float var18 = var1 == null ? var0.floatValues2[var7 * 3 + 2] : var1[var8 + 2];
         var3.rotateZYX(var18, var17, var16);
         if (var1 != null) {
            var3.scale(var1[var8 + 6], var1[var8 + 7], var1[var8 + 8]);
         }
      }
   }

   private static void method4809(int var0) {
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

   private static enum Type {
      SOLID,
      TRANSLUCENT,
      GLOW,
      SHINE;
   }
}

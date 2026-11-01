package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.misc.ShaderUniform;
import org.zenith.utility.misc.WeatherFlakeChannel;

public final class SnowShader {
   private static KawaseShaderKey kawaseShaderKey;
   private static boolean flag = true;
   private static float[] floatValues = new float[6144];
   private static int[] intValues = new int[2048];
   private static int intValue;
   private static final int CONST_intValue = 16;
   private static final int CONST_intValue2 = 10;
   private static final int CONST_intValue3 = 80;
   private static final SnowShader.Entry[] entrys = new SnowShader.Entry[100];
   private static double doubleValue;
   private static double doubleValue2;
   private static double doubleValue3;
   private static int intValue2;
   private static int[] intValues2 = new int[512];
   private static int[] intValues3 = new int[512];
   private static final int[] CONST_intValues = new int[100];
   private static final int[] CONST_intValues2 = new int[100];
   private static final int[] CONST_intValues3 = new int[100];
   private static double doubleValue4;
   private static double doubleValue5;
   private static double doubleValue6;
   private static boolean flag2;
   private static boolean flag3;
   private static final double CONST_doubleValue = 24.0;
   private static boolean flag4;
   private static double doubleValue7;
   private static double doubleValue8;
   private static double doubleValue9;
   private static BufferAllocator allocator;
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Matrix4f matrix4f2 = new Matrix4f();
   private static final float[] CONST_floatValues = new float[16];
   private static final List<MeshBuffer> items = new ArrayList<>(100);
   private static boolean flag5;
   private static boolean flag6;
   private static final double CONST_doubleValue2 = 256.0;
   private static double doubleValue10;
   private static double doubleValue11;
   private static double doubleValue12;
   private static float floatValue;
   private static float floatValue2;
   private static float floatValue3;
   private static float floatValue4 = 24.0F;
   private static float floatValue5 = 1.0F;
   private static float floatValue6 = 0.6F;
   private static float floatValue7 = 0.19F;
   private static float floatValue8 = 0.3F;
   private static float floatValue9 = 1.0F;
   private static float[] floatValues2 = new float[]{0.3F, 0.9F, 0.3F};
   private static float[] floatValues3 = new float[]{1.0F, 0.98F, 0.94F};
   private static float[] floatValues4 = new float[]{0.52F, 0.56F, 0.62F};

   private SnowShader() {
   }

   public static void method387() {
      if (kawaseShaderKey == null) {
         kawaseShaderKey = new KawaseShaderKey(Zenith.method1501("snow/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      }
   }

   public static boolean isFlag() {
      return flag;
   }

   public static boolean method388() {
      flag = !flag;
      return flag;
   }

   public static String method389() {
      if (kawaseShaderKey == null) {
         return "программа не создана";
      } else if (kawaseShaderKey.text3 != null) {
         return kawaseShaderKey.text3;
      } else {
         try {
            if (!LightUtil.method6712(kawaseShaderKey.getIdentifier())) {
               return "исходники шейдера не прочитаны";
            } else {
               return kawaseShaderKey.method5560("Origin") ? "ок" : "юниформ Origin не найден";
            }
         } catch (RuntimeException var1) {
            return "программа не загрузилась: " + var1;
         }
      }
   }

   public static boolean method390() {
      if (!flag6) {
         flag5 = method391();
         flag6 = true;
      }

      return flag5;
   }

   private static boolean method391() {
      if (kawaseShaderKey != null && kawaseShaderKey.text3 == null) {
         try {
            return LightUtil.method6712(kawaseShaderKey.getIdentifier()) && kawaseShaderKey.method5560("Origin");
         } catch (RuntimeException var1) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void method392(Vec3d var0, WeatherFlakeChannel var1, float var2) {
      doubleValue10 = var0.x;
      doubleValue11 = var0.y;
      doubleValue12 = var0.z;
      intValue = 0;
      flag2 = false;
      flag6 = false;
      float var3 = (float)Math.sqrt(
         var1.floatValues4[0] * var1.floatValues4[0] + var1.floatValues4[1] * var1.floatValues4[1] + var1.floatValues4[2] * var1.floatValues4[2]
      );
      if (var3 < 1.0E-5F) {
         var3 = 1.0F;
      }

      floatValues2[0] = var1.floatValues4[0] / var3;
      floatValues2[1] = var1.floatValues4[1] / var3;
      floatValues2[2] = var1.floatValues4[2] / var3;
      System.arraycopy(var1.floatValues5, 0, floatValues3, 0, 3);
      floatValues4[0] = (var1.floatValues[0] + var1.floatValues2[0]) * 0.5F + 0.16F;
      floatValues4[1] = (var1.floatValues[1] + var1.floatValues2[1]) * 0.5F + 0.16F;
      floatValues4[2] = (var1.floatValues[2] + var1.floatValues2[2]) * 0.5F + 0.17F;
      floatValue8 = var2;
   }

   public static void method393() {
      method394();
      intValue = 0;
      flag2 = true;
      flag3 = false;
   }

   private static void method394() {
      double var0 = Math.floor(doubleValue10);
      double var2 = Math.floor(doubleValue11);
      double var4 = Math.floor(doubleValue12);
      if (!flag4 || Math.abs(var0 - doubleValue7) > 24.0 || Math.abs(var2 - doubleValue8) > 24.0 || Math.abs(var4 - doubleValue9) > 24.0) {
         doubleValue7 = var0;
         doubleValue8 = var2;
         doubleValue9 = var4;
         flag4 = true;
      }

      doubleValue4 = doubleValue7;
      doubleValue5 = doubleValue8;
      doubleValue6 = doubleValue9;
   }

   public static void method395(
      double var0,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      double var20,
      double var22,
      int var24,
      int var25,
      int var26,
      int var27
   ) {
      if (!flag2) {
         method394();
         flag2 = true;
      }

      ensure(intValue + 1);
      int var28 = intValue * 12;
      floatValues[var28] = (float)(var0 - doubleValue4);
      floatValues[var28 + 1] = (float)(var2 - doubleValue5);
      floatValues[var28 + 2] = (float)(var4 - doubleValue6);
      floatValues[var28 + 3] = (float)(var6 - doubleValue4);
      floatValues[var28 + 4] = (float)(var8 - doubleValue5);
      floatValues[var28 + 5] = (float)(var10 - doubleValue6);
      floatValues[var28 + 6] = (float)(var12 - doubleValue4);
      floatValues[var28 + 7] = (float)(var14 - doubleValue5);
      floatValues[var28 + 8] = (float)(var16 - doubleValue6);
      floatValues[var28 + 9] = (float)(var18 - doubleValue4);
      floatValues[var28 + 10] = (float)(var20 - doubleValue5);
      floatValues[var28 + 11] = (float)(var22 - doubleValue6);
      int var29 = intValue * 4;
      intValues[var29] = var24;
      intValues[var29 + 1] = var25;
      intValues[var29 + 2] = var26;
      intValues[var29 + 3] = var27;
      intValues2[intValue] = method396(floatValues[var28], floatValues[var28 + 2]);
      intValue++;
   }

   private static int method396(float var0, float var1) {
      int var2 = Math.clamp((long)((int)Math.floor((var0 + 80.0F) / 16.0F)), 0, 9);
      int var3 = Math.clamp((long)((int)Math.floor((var1 + 80.0F) / 16.0F)), 0, 9);
      return var3 * 10 + var2;
   }

   public static boolean isEmpty() {
      return intValue == 0;
   }

   public static void method397(MatrixStack var0) {
      if (flag2) {
         long var1 = WorldProfiler.method782();
         method398();
         WorldProfiler.method783(15, var1);
         WorldProfiler.method784(2);
      }

      if (intValue2 != 0 && method390()) {
         KawaseShaderKey var12 = kawaseShaderKey.method5165();
         if (var12 == null) {
            intValue = 0;
         } else {
            floatValue = (float)method402(doubleValue10);
            floatValue2 = (float)method402(doubleValue11);
            floatValue3 = (float)method402(doubleValue12);
            method407("Origin", floatValue, floatValue2, floatValue3);
            method407("SunDir", floatValues2[0], floatValues2[1], floatValues2[2]);
            method407("SunColor", floatValues3[0], floatValues3[1], floatValues3[2]);
            method407("SkyColor", floatValues4[0], floatValues4[1], floatValues4[2]);
            method405("GrainScale", floatValue4);
            method405("GrainStrength", floatValue5);
            method405("CrustAmount", floatValue6);
            method405("SparkleAmount", floatValue7);
            method405("Wetness", floatValue8);
            method405("Sheen", floatValue9);
            method405("WrapPeriod", (float)(256.0 * floatValue4));
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            RenderState.method1878();
            RenderState.method1882(true);
            float var2 = (float)(doubleValue - doubleValue10);
            float var3 = (float)(doubleValue2 - doubleValue11);
            float var4 = (float)(doubleValue3 - doubleValue12);
            Matrix4f var5 = matrix4f.set(var0.peek().getPositionMatrix()).translate(var2, var3, var4);
            method406("InverseView", matrix4f2.translation(var2, var3, var4));
            Frustum var6 = FrustumHolder.getFrustum();
            var var7 = items;
            var7.clear();

            for (SnowShader.Entry var11 : entrys) {
               if (var11 != null
                  && var11.intValue != 0
                  && var11.meshBuffer != null
                  && !var11.meshBuffer.isEmpty()
                  && (
                     var6 == null
                        || var6.isVisible(
                           new Box(
                              doubleValue + var11.floatValue,
                              doubleValue2 + var11.floatValue2,
                              doubleValue3 + var11.floatValue3,
                              doubleValue + var11.floatValue4,
                              doubleValue2 + var11.floatValue5,
                              doubleValue3 + var11.floatValue6
                           )
                        )
                  )) {
                  var7.add(var11.meshBuffer);
               }
            }

            if (!var7.isEmpty()) {
               RenderPipeline var13 = kawaseShaderKey.method5561(
                  net.minecraft.client.render.VertexFormats.POSITION_COLOR, DrawMode.QUADS, RenderState.method1904()
               );
               if (var13 != null) {
                  MeshBuffer.method6589(var7, var13, var5, null, kawaseShaderKey.method5562());
               }

               var7.clear();
            }

            ImmediateRenderer.method8012(null);
            RenderState.method1876();
            RenderState.method1872();
            intValue = 0;
         }
      } else {
         intValue = 0;
      }
   }

   private static void method398() {
      flag2 = false;
      flag3 = true;
      if (intValue == 0) {
         method400();
      } else {
         for (SnowShader.Entry var3 : entrys) {
            if (var3 != null) {
               var3.intValue = 0;
            }
         }

         int[] var4 = CONST_intValues3;
         Arrays.fill(var4, 0);

         for (int var5 = 0; var5 < intValue; var5++) {
            var4[intValues2[var5]]++;
         }

         if (intValues3.length < intValue) {
            intValues3 = new int[Math.max(intValue, intValues3.length * 2)];
         }

         int var6 = 0;

         for (int var7 = 0; var7 < var4.length; var7++) {
            CONST_intValues[var7] = var6;
            var6 += var4[var7];
         }

         System.arraycopy(CONST_intValues, 0, CONST_intValues2, 0, var4.length);
         int var8 = 0;

         while (var8 < intValue) {
            intValues3[CONST_intValues2[intValues2[var8]]++] = var8++;
         }

         for (int var9 = 0; var9 < var4.length; var9++) {
            if (var4[var9] > 0) {
               method399(var9, CONST_intValues[var9], var4[var9]);
            }
         }

         doubleValue = doubleValue4;
         doubleValue2 = doubleValue5;
         doubleValue3 = doubleValue6;
         intValue2 = intValue;
      }
   }

   private static void method399(int var0, int var1, int var2) {
      SnowShader.Entry var3 = entrys[var0];
      if (var3 == null) {
         var3 = new SnowShader.Entry();
         entrys[var0] = var3;
      }

      if (var3.meshBuffer == null) {
         var3.meshBuffer = new MeshBuffer("zenith snow tile " + var0);
      }

      if (var3.method408(intValues3, var1, var2) && !var3.meshBuffer.isEmpty()) {
         var3.intValue = var2;
      } else {
         var3.intValue2 = 0;
         var3.reset();
         if (allocator == null) {
            allocator = new BufferAllocator(262144);
         }

         allocator.reset();
         BufferBuilder var4 = new BufferBuilder(allocator, DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);

         for (int var5 = var1; var5 < var1 + var2; var5++) {
            int var6 = intValues3[var5];
            int var7 = var6 * 12;
            int var8 = var6 * 4;

            for (int var9 = 0; var9 < 4; var9++) {
               float var10 = floatValues[var7 + var9 * 3];
               float var11 = floatValues[var7 + var9 * 3 + 1];
               float var12 = floatValues[var7 + var9 * 3 + 2];
               var3.method410(var10, var11, var12);
               var4.vertex(var10, var11, var12).color(intValues[var8 + var9]);
            }
         }

         BuiltBuffer var13 = var4.endNullable();
         if (var13 != null) {
            var3.meshBuffer.method6583(var13);
            var3.intValue = var2;
            var3.method409(intValues3, var1, var2);
         }
      }
   }

   private static void method400() {
      intValue2 = 0;

      for (SnowShader.Entry var3 : entrys) {
         if (var3 != null) {
            var3.intValue = 0;
         }
      }
   }

   public static void method401() {
      intValue2 = 0;
      flag2 = false;
      intValue = 0;
      flag3 = false;
   }

   public static boolean isFlag3() {
      return flag3;
   }

   private static double method402(double var0) {
      return var0 - Math.floor(var0 / 256.0) * 256.0;
   }

   public static void method403(float var0) {
      floatValue9 = Math.clamp(var0, 0.0F, 2.5F);
   }

   public static void method404(float var0, float var1, float var2, float var3) {
      floatValue4 = Math.max(1.0F, (float)Math.round(var0));
      floatValue5 = var1;
      floatValue6 = var2;
      floatValue7 = var3;
   }

   private static void method405(String var0, float var1) {
      ShaderUniform var2 = kawaseShaderKey.method5166(var0);
      if (var2 != null) {
         var2.method7412(var1);
      }
   }

   private static void method406(String var0, Matrix4f var1) {
      ShaderUniform var2 = kawaseShaderKey.method5166(var0);
      if (var2 != null) {
         var1.get(CONST_floatValues);
         var2.method7417(CONST_floatValues);
      }
   }

   private static void method407(String var0, float var1, float var2, float var3) {
      ShaderUniform var4 = kawaseShaderKey.method5166(var0);
      if (var4 != null) {
         var4.method7414(var1, var2, var3);
      }
   }

   private static void ensure(int var0) {
      if (var0 * 12 > floatValues.length) {
         float[] var1 = new float[Math.max(floatValues.length * 2, var0 * 12)];
         int[] var2 = new int[Math.max(intValues.length * 2, var0 * 4)];
         int[] var3 = new int[Math.max(intValues2.length * 2, var0)];
         System.arraycopy(floatValues, 0, var1, 0, intValue * 12);
         System.arraycopy(intValues, 0, var2, 0, intValue * 4);
         System.arraycopy(intValues2, 0, var3, 0, intValue);
         floatValues = var1;
         intValues = var2;
         intValues2 = var3;
      }
   }

   private static final class Entry {
      private MeshBuffer meshBuffer;
      private int intValue;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private int intValue2;
      private int[] intValues = new int[0];
      private int[] intValues2 = new int[0];

      private boolean method408(int[] var1, int var2, int var3) {
         if (this.intValue2 != var3) {
            return false;
         } else {
            int[] var4 = this.intValues;
            int[] var5 = this.intValues2;

            for (int var6 = 0; var6 < var3; var6++) {
               int var7 = var1[var2 + var6];
               int var8 = var7 * 12;
               int var9 = var7 * 4;
               int var10 = var6 * 12;
               int var11 = var6 * 4;

               for (int var12 = 0; var12 < 12; var12++) {
                  if (Float.floatToRawIntBits(SnowShader.floatValues[var8 + var12]) != var4[var10 + var12]) {
                     return false;
                  }
               }

               for (int var13 = 0; var13 < 4; var13++) {
                  if (SnowShader.intValues[var9 + var13] != var5[var11 + var13]) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      private void method409(int[] var1, int var2, int var3) {
         if (this.intValues.length < var3 * 12) {
            this.intValues = new int[Math.max(var3 * 12, this.intValues.length * 2)];
            this.intValues2 = new int[Math.max(var3 * 4, this.intValues2.length * 2)];
         }

         for (int var4 = 0; var4 < var3; var4++) {
            int var5 = var1[var2 + var4];
            int var6 = var5 * 12;
            int var7 = var5 * 4;

            for (int var8 = 0; var8 < 12; var8++) {
               this.intValues[var4 * 12 + var8] = Float.floatToRawIntBits(SnowShader.floatValues[var6 + var8]);
            }

            System.arraycopy(SnowShader.intValues, var7, this.intValues2, var4 * 4, 4);
         }

         this.intValue2 = var3;
      }

      private void reset() {
         this.floatValue = Float.MAX_VALUE;
         this.floatValue2 = Float.MAX_VALUE;
         this.floatValue3 = Float.MAX_VALUE;
         this.floatValue4 = -Float.MAX_VALUE;
         this.floatValue5 = -Float.MAX_VALUE;
         this.floatValue6 = -Float.MAX_VALUE;
      }

      private void method410(float var1, float var2, float var3) {
         if (var1 < this.floatValue) {
            this.floatValue = var1;
         }

         if (var2 < this.floatValue2) {
            this.floatValue2 = var2;
         }

         if (var3 < this.floatValue3) {
            this.floatValue3 = var3;
         }

         if (var1 > this.floatValue4) {
            this.floatValue4 = var1;
         }

         if (var2 > this.floatValue5) {
            this.floatValue5 = var2;
         }

         if (var3 > this.floatValue6) {
            this.floatValue6 = var3;
         }
      }
   }
}

package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class RoundedRectRenderer {
   public static final float CONST_floatValue = 0.0F;
   public static final float CONST_floatValue2 = 1.0F;
   public static final float CONST_floatValue3 = 2.0F;
   public static final float CONST_floatValue4 = 3.0F;
   public static final float CONST_floatValue5 = 7.0F;
   private BufferBuilder builder;
   private boolean flag;

   public boolean isFlag() {
      return this.flag;
   }

   public void begin() {
      MsdfRenderer.flushBatch();
      if (!this.flag) {
         KawaseShaderKey var1 = RenderShaders.kawaseShaderKey3;
         var1.method5165();
         var1.method5166("Smoothness").method7413(0.8F, 1.0F);
         RenderState.method1871();
         RenderState.method1873();
         this.builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.vertexFormat);
         this.flag = true;
      }
   }

   public void method4779(Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      this.method4784(var1, var2, var3, var4, var5, var6, var7, 0.0F, 0.0F);
   }

   public void method4780(Matrix4f var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      this.method4784(var1, var2, var3, var4, var5, ProxyDotRadiusData.INSTANCE, var6, 0.0F, 1.0F);
   }

   public void method4781(Matrix4f var1, float var2, float var3, float var4, float var5, float var6, ProxyDotRadiusData var7, ZenithColor var8) {
      this.method4784(var1, var2, var3, var4, var5, var7, var8, var6, 2.0F);
   }

   public void method4782(Matrix4f var1, float var2, float var3, float var4, float var5, float var6, ProxyDotRadiusData var7, ZenithColor var8, float var9) {
      this.method4784(var1, var2, var3, var4, var5, var7, var8, var6, 3.0F + var9);
   }

   public void method4783(Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, ZenithColor var9) {
      ProxyDotRadiusData var10 = new ProxyDotRadiusData(var7, 0.0F, 0.0F, var8);
      method4785(this.builder, var1, var2, var3, var4, var5, var10, var9, var6, 7.0F);
   }

   private void method4784(Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, float var8, float var9) {
      method4785(this.builder, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static void method4785(
      BufferBuilder var0, Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, float var8, float var9
   ) {
      int var10 = var7.method7287();
      float var11 = var2 + var4;
      float var12 = var3 + var5;
      method4786(var0, var1, var2, var3, var4, var5, var10, var6, var8, var9);
      method4786(var0, var1, var2, var12, var4, var5, var10, var6, var8, var9);
      method4786(var0, var1, var11, var12, var4, var5, var10, var6, var8, var9);
      method4786(var0, var1, var11, var3, var4, var5, var10, var6, var8, var9);
   }

   private static void method4786(
      BufferBuilder var0, Matrix4f var1, float var2, float var3, float var4, float var5, int var6, ProxyDotRadiusData var7, float var8, float var9
   ) {
      var0.vertex(var1, var2, var3, 0.0F).color(var6).texture(var4, var5);
      long var10 = var0.beginElement(VertexFormats.vertexFormatElement);
      MemoryUtil.memPutFloat(var10, var7.getFloatValue());
      MemoryUtil.memPutFloat(var10 + 4L, var7.getFloatValue4());
      MemoryUtil.memPutFloat(var10 + 8L, var7.getFloatValue2());
      MemoryUtil.memPutFloat(var10 + 12L, var7.getFloatValue3());
      long var12 = var0.beginElement(VertexFormats.vertexFormatElement2);
      MemoryUtil.memPutFloat(var12, var8);
      MemoryUtil.memPutFloat(var12 + 4L, var9);
   }

   public void flush() {
      if (this.flag) {
         ImmediateRenderer.method8015(this.builder.end());
         RenderState.method1872();
         this.builder = null;
         this.flag = false;
      }
   }
}

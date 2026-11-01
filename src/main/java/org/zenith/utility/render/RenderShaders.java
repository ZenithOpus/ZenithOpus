package org.zenith.utility.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import lombok.Generated;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.Handler2;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.misc.ShadowShaderKey;
import org.zenith.utility.render.display.base.CategoryIcon;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class RenderShaders implements Handler2 {
   public static final float CONST_floatValue = 0.8F;
   public static KawaseShaderKey kawaseShaderKey;
   public static KawaseShaderKey kawaseShaderKey2;
   public static KawaseShaderKey kawaseShaderKey3;
   public static KawaseShaderKey kawaseShaderKey4;
   private static ShadowShaderKey shadowShaderKey;
   private static KawaseShaderKey kawaseShaderKey5;
   private static KawaseShaderKey kawaseShaderKey6;
   private static KawaseShaderKey kawaseShaderKey7;
   private static KawaseShaderKey kawaseShaderKey8;
   private static KawaseShaderKey kawaseShaderKey9;
   private static KawaseShaderKey kawaseShaderKey10;
   private static KawaseShaderKey kawaseShaderKey11;
   private static KawaseShaderKey kawaseShaderKey12;
   private static KawaseShaderKey kawaseShaderKey13;
   private static KawaseShaderKey kawaseShaderKey14;
   private static KawaseShaderKey kawaseShaderKey15;
   public static GpuTextureView gpuTextureView;
   public static boolean flag = true;
   public static float[] floatValues;

   public static void method5255() {
      kawaseShaderKey = new KawaseShaderKey(Zenith.method1501("rectangle/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey2 = new KawaseShaderKey(Zenith.method1501("batch_rectangle/data"), net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      kawaseShaderKey3 = new KawaseShaderKey(Zenith.method1501("batch_shape/data"), VertexFormats.vertexFormat);
      kawaseShaderKey4 = new KawaseShaderKey(Zenith.method1501("batch_noise/data"), VertexFormats.vertexFormat);
      shadowShaderKey = new ShadowShaderKey(Zenith.method1501("drop_shadow/data"));
      kawaseShaderKey5 = new KawaseShaderKey(Zenith.method1501("squircle/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey7 = new KawaseShaderKey(Zenith.method1501("squircle_texture/data"), net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      kawaseShaderKey6 = new KawaseShaderKey(Zenith.method1501("texture/data"), net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      kawaseShaderKey8 = new KawaseShaderKey(Zenith.method1501("border/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey9 = new KawaseShaderKey(Zenith.method1501("corner/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey10 = new KawaseShaderKey(Zenith.method1501("arc_border/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey12 = new KawaseShaderKey(Zenith.method1501("loading/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey13 = new KawaseShaderKey(Zenith.method1501("menu_background/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey14 = new KawaseShaderKey(Zenith.method1501("media_background/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey15 = new KawaseShaderKey(Zenith.method1501("gradient_rectangle/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      kawaseShaderKey11 = new KawaseShaderKey(Zenith.method1501("liquidglass/data"), net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      SnowShader.method387();
   }

   private static void method5256(float var0, float var1, float var2, float var3, boolean var4) {
      BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      byte var6 = -1;
      float var7 = var4 ? 0.0F : 1.0F;
      float var8 = var4 ? 1.0F : 0.0F;
      var5.vertex(var0, var1, 0.0F).texture(0.0F, var8).color(-1);
      var5.vertex(var0, var1 + var3, 0.0F).texture(0.0F, var7).color(-1);
      var5.vertex(var0 + var2, var1 + var3, 0.0F).texture(1.0F, var7).color(-1);
      var5.vertex(var0 + var2, var1, 0.0F).texture(1.0F, var8).color(-1);
      ImmediateRenderer.method8015(var5.end());
   }

   public static void method5257(Matrix4f var0, Vec2f var1, Vec2f var2, ZenithColor var3) {
      MsdfRenderer.flushBatch();

      try {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1898("core/position_color");
         RenderState.method1902(1.0F);
         method5284();
         BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
         var5.vertex(var0, var1.x, var1.y, 0.0F).color(var3.method7287());
         var5.vertex(var0, var2.x, var2.y, 0.0F).color(var3.method7287());
         ImmediateRenderer.method8015(var5.end());
         method5285();
      } finally {
         RenderState.method1872();
         RenderState.method1902(1.0F);
      }
   }

   public static void method5258(Matrix4f var0, Vec2f var1, Vec2f var2, Vec2f var3, Vec2f var4, ZenithColor var5, int var6) {
      MsdfRenderer.flushBatch();

      try {
         Matrix4f var7 = var0;
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1898("core/position_color");
         RenderState.method1902(1.0F);
         method5284();
         BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, net.minecraft.client.render.VertexFormats.POSITION_COLOR);

         for (int var9 = 0; var9 <= var6; var9++) {
            float var10 = (float)var9 / var6;
            float var11 = (float)MathUtils.method2644(var10, var1.x, var2.x, var3.x, var4.x);
            float var12 = (float)MathUtils.method2644(var10, var1.y, var2.y, var3.y, var4.y);
            var8.vertex(var7, var11, var12, 0.0F).color(var5.method7287());
         }

         ImmediateRenderer.method8015(var8.end());
         method5285();
      } finally {
         RenderState.method1872();
         RenderState.method1902(1.0F);
      }
   }

   private static float method5259(float var0, float var1, float var2, float var3, float var4) {
      float var5 = 1.0F - var0;
      float var6 = var0 * var0;
      float var7 = var5 * var5;
      return var7 * var5 * var1 + 3.0F * var7 * var0 * var2 + 3.0F * var5 * var6 * var3 + var6 * var0 * var4;
   }

   public static void method5260(Matrix4f var0, float var1, float var2, float var3, float var4, ZenithColor var5) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_color");
      method5284();
      BufferBuilder var7 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var7.vertex(var0, var1, var2 + var4, 0.0F).color(var5.method7287());
      var7.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var5.method7287());
      var7.vertex(var0, var1 + var3, var2, 0.0F).color(var5.method7287());
      var7.vertex(var0, var1, var2, 0.0F).color(var5.method7287());
      ImmediateRenderer.method8015(var7.end());
      method5285();
   }

   public static void method5261(Matrix4f var0, float var1, float var2, ZenithColor var3, ZenithColor var4, float var5) {
      MsdfRenderer.flushBatch();
      kawaseShaderKey13.method5165();
      kawaseShaderKey13.method5166("Resolution").method7413(var1, var2);
      kawaseShaderKey13.method5166("Time").method7412((float)(System.currentTimeMillis() % 3600000L) / 1000.0F);
      kawaseShaderKey13.method5166("Accent").method7414(var3.getIntValue() / 255.0F, var3.getIntValue2() / 255.0F, var3.getIntValue3() / 255.0F);
      kawaseShaderKey13.method5166("Secondary").method7414(var4.getIntValue() / 255.0F, var4.getIntValue2() / 255.0F, var4.getIntValue3() / 255.0F);
      kawaseShaderKey13.method5166("Intensity").method7412(var5);
      method5284();
      BufferBuilder var6 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      int var7 = ZenithColor.INSTANCE.method7287();
      var6.vertex(var0, 0.0F, 0.0F, 0.0F).color(var7);
      var6.vertex(var0, 0.0F, var2, 0.0F).color(var7);
      var6.vertex(var0, var1, var2, 0.0F).color(var7);
      var6.vertex(var0, var1, 0.0F, 0.0F).color(var7);
      ImmediateRenderer.method8015(var6.end());
      method5285();
   }

   public static void method5262(Matrix4f var0, float var1, float var2, ZenithColor[] var3, float var4, float var5, float var6) {
      MsdfRenderer.flushBatch();
      kawaseShaderKey14.method5165();
      kawaseShaderKey14.method5166("Resolution").method7413(var1, var2);
      kawaseShaderKey14.method5166("Time").method7412(var4);
      String[] var7 = new String[]{"ColorA", "ColorB", "ColorC", "ColorD"};

      for (int var8 = 0; var8 < var7.length; var8++) {
         ZenithColor var9 = var3[Math.min(var8, var3.length - 1)];
         kawaseShaderKey14.method5166(var7[var8]).method7414(var9.getIntValue() / 255.0F, var9.getIntValue2() / 255.0F, var9.getIntValue3() / 255.0F);
      }

      kawaseShaderKey14.method5166("Energy").method7412(var5);
      method5284();
      BufferBuilder var10 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      int var11 = ZenithColor.INSTANCE.method7292(var6).method7287();
      var10.vertex(var0, 0.0F, 0.0F, 0.0F).color(var11);
      var10.vertex(var0, 0.0F, var2, 0.0F).color(var11);
      var10.vertex(var0, var1, var2, 0.0F).color(var11);
      var10.vertex(var0, var1, 0.0F, 0.0F).color(var11);
      ImmediateRenderer.method8015(var10.end());
      method5285();
   }

   public static void method5263(
      Matrix4f var0,
      float var1,
      float var2,
      float var3,
      float var4,
      ProxyDotRadiusData var5,
      ZenithColor var6,
      float var7,
      float var8,
      ZenithColor var9,
      float var10,
      boolean var11,
      float var12,
      float var13,
      float var14
   ) {
      MsdfRenderer.flushBatch();
      method5284();
      RenderState.method1877();
      RenderState.method1889(0, gpuTextureView != null ? gpuTextureView : Zenith.getInstance().getKawaseBlur().getFbo().getColorAttachmentView());
      kawaseShaderKey11.method5165();
      kawaseShaderKey11.method5166("GlobalAlpha").method7412(var7);
      kawaseShaderKey11.method5166("Size").method7413(var3, var4);
      kawaseShaderKey11.method5166("Radius").method7415(var5.getFloatValue(), var5.getFloatValue4(), var5.getFloatValue2(), var5.getFloatValue3());
      kawaseShaderKey11.method5166("Smoothness").method7412(1.0F);
      kawaseShaderKey11.method5166("FresnelPower").method7412(var8);
      kawaseShaderKey11.method5166("FresnelColor").method7414(var9.getIntValue() / 255.0F, var9.getIntValue2() / 255.0F, var9.getIntValue3() / 255.0F);
      kawaseShaderKey11.method5166("FresnelAlpha").method7412(var9.getIntValue4() / 255.0F);
      kawaseShaderKey11.method5166("BaseAlpha").method7412(var10);
      kawaseShaderKey11.method5166("FresnelInvert").set(var11 ? 1 : 0);
      kawaseShaderKey11.method5166("FresnelMix").method7412(var12);
      kawaseShaderKey11.method5166("DistortStrength").method7412(var13);
      kawaseShaderKey11.method5166("Time").method7412((float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
      ZenithColor var15 = InterfaceModule.INSTANCE.method6824();
      kawaseShaderKey11.method5166("GlareColor").method7414(var15.getIntValue() / 255.0F, var15.getIntValue2() / 255.0F, var15.getIntValue3() / 255.0F);
      kawaseShaderKey11.method5166("GlareAlpha").method7412(var15.getIntValue4() / 255.0F);
      kawaseShaderKey11.method5166("GlareSpeed").method7412(InterfaceModule.INSTANCE.method6825());
      kawaseShaderKey11.method5166("GlareMode").method7412(InterfaceModule.INSTANCE.method6826());
      kawaseShaderKey11.method5166("GlareTexture").method7412(InterfaceModule.INSTANCE.method6827());
      kawaseShaderKey11.method5166("CornerSmoothness").method7412(var14);
      method5283(floatValues, kawaseShaderKey11, var1, var2, var14 / 2.0F);
      int var16 = window.getScaledWidth();
      int var17 = window.getScaledHeight();
      float var18 = var1 / var16;
      float var19 = (var17 - var2 - var4) / var17;
      float var20 = var3 / var16;
      float var21 = var4 / var17;
      BufferBuilder var22 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var22.vertex(var0, var1, var2, 0.0F).texture(var18, var19 + var21).color(var6.method7287());
      var22.vertex(var0, var1, var2 + var4, 0.0F).texture(var18, var19).color(var6.method7287());
      var22.vertex(var0, var1 + var3, var2 + var4, 0.0F).texture(var18 + var20, var19).color(var6.method7287());
      var22.vertex(var0, var1 + var3, var2, 0.0F).texture(var18 + var20, var19 + var21).color(var6.method7287());
      ImmediateRenderer.method8015(var22.end());
      RenderState.method1892(0, 0);
      RenderState.method1876();
      method5285();
   }

   public static void method5264(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, ProxyDotRadiusData var7, ZenithColor var8
   ) {
      var7 = new ProxyDotRadiusData(
         var7.getFloatValue() * var5 / 2.0F, var7.getFloatValue2() * var5 / 2.0F, var7.getFloatValue3() * var5 / 2.0F, var7.getFloatValue4() * var5 / 2.0F
      );
      float var9 = 1.5F;
      method5263(
         var0,
         var1 - var9,
         var2 - var9,
         var3 + var9 * 2.0F,
         var4 + var9 * 2.0F,
         var7,
         var8,
         var8.getIntValue4() / 255.0F,
         var4 == 240.0F ? 100.0F : 50.0F,
         var8.method7293(255),
         1.0F,
         true,
         0.0F,
         var6,
         var5
      );
   }

   public static void method5265(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, boolean var8
   ) {
      var6 = new ProxyDotRadiusData(
         var6.getFloatValue() * var5 / 2.0F, var6.getFloatValue2() * var5 / 2.0F, var6.getFloatValue3() * var5 / 2.0F, var6.getFloatValue4() * var5 / 2.0F
      );
      method5263(
         var0,
         var1,
         var2,
         var3,
         var4,
         var6,
         var7,
         var7.getIntValue4() / 255.0F,
         var4 == 240.0F ? 100.0F : 50.0F,
         var7.method7293(255),
         1.0F,
         true,
         0.0F,
         0.08F,
         var5
      );
   }

   public static void method5266(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      MsdfRenderer.flushBatch();
      float var9 = 0.8F;
      kawaseShaderKey5.method5165();
      kawaseShaderKey5.method5166("Size").method7413(var3, var4);
      kawaseShaderKey5.method5166("Radius")
         .method7415(
            var6.getFloatValue() * var5 / 2.0F, var6.getFloatValue4() * var5 / 2.0F, var6.getFloatValue2() * var5 / 2.0F, var6.getFloatValue3() * var5 / 2.0F
         );
      kawaseShaderKey5.method5166("Smoothness").method7412(var9);
      kawaseShaderKey5.method5166("CornerSmoothness").method7412(var5);
      method5284();
      BufferBuilder var14 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var14.vertex(var0, var1, var2, 0.0F).color(var7.method7287());
      var14.vertex(var0, var1, var2 + var4, 0.0F).color(var7.method7287());
      var14.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var7.method7287());
      var14.vertex(var0, var1 + var3, var2, 0.0F).color(var7.method7287());
      ImmediateRenderer.method8015(var14.end());
      method5285();
   }

   public static void method5267(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      MsdfRenderer.flushBatch();
      float var9 = 0.8F;
      kawaseShaderKey12.method5165();
      kawaseShaderKey12.method5166("Size").method7413(var3, var4);
      kawaseShaderKey12.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
      kawaseShaderKey12.method5166("Smoothness").method7412(var9);
      kawaseShaderKey12.method5166("Progress").method7412(var5);
      kawaseShaderKey12.method5166("StripeWidth").method7412(0.0F);
      kawaseShaderKey12.method5166("Fade").method7412(0.5F);
      method5284();
      BufferBuilder var14 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var14.vertex(var0, var1, var2, 0.0F).color(var7.method7287());
      var14.vertex(var0, var1, var2 + var4, 0.0F).color(var7.method7287());
      var14.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var7.method7287());
      var14.vertex(var0, var1 + var3, var2, 0.0F).color(var7.method7287());
      ImmediateRenderer.method8015(var14.end());
      method5285();
   }

   public static void method5268(Matrix4f var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      MsdfRenderer.flushBatch();
      float var8 = 0.8F;
      kawaseShaderKey.method5165();
      kawaseShaderKey.method5166("Size").method7413(var3, var4);
      kawaseShaderKey.method5166("Radius").method7415(var5.getFloatValue(), var5.getFloatValue4(), var5.getFloatValue2(), var5.getFloatValue3());
      kawaseShaderKey.method5166("Smoothness").method7412(var8);
      method5283(floatValues, kawaseShaderKey, var1, var2, 1.0F);
      method5284();
      BufferBuilder var13 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var13.vertex(var0, var1, var2, 0.0F).color(var6.method7287());
      var13.vertex(var0, var1, var2 + var4, 0.0F).color(var6.method7287());
      var13.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var6.method7287());
      var13.vertex(var0, var1 + var3, var2, 0.0F).color(var6.method7287());
      ImmediateRenderer.method8015(var13.end());
      method5285();
   }

   public static void method5269(
      Matrix4f var0,
      float var1,
      float var2,
      float var3,
      float var4,
      ProxyDotRadiusData var5,
      ZenithColor var6,
      ZenithColor var7,
      ZenithColor var8,
      ZenithColor var9
   ) {
      MsdfRenderer.flushBatch();
      float var11 = 0.8F;
      kawaseShaderKey15.method5165();
      kawaseShaderKey15.method5166("Size").method7413(var3, var4);
      kawaseShaderKey15.method5166("Radius").method7415(var5.getFloatValue(), var5.getFloatValue4(), var5.getFloatValue2(), var5.getFloatValue3());
      kawaseShaderKey15.method5166("Smoothness").method7412(var11);
      kawaseShaderKey15.method5166("TopLeftColor")
         .method7415(var6.getIntValue() / 255.0F, var6.getIntValue2() / 255.0F, var6.getIntValue3() / 255.0F, var6.getIntValue4() / 255.0F);
      kawaseShaderKey15.method5166("BottomLeftColor")
         .method7415(var7.getIntValue() / 255.0F, var7.getIntValue2() / 255.0F, var7.getIntValue3() / 255.0F, var7.getIntValue4() / 255.0F);
      kawaseShaderKey15.method5166("BottomRightColor")
         .method7415(var8.getIntValue() / 255.0F, var8.getIntValue2() / 255.0F, var8.getIntValue3() / 255.0F, var8.getIntValue4() / 255.0F);
      kawaseShaderKey15.method5166("TopRightColor")
         .method7415(var9.getIntValue() / 255.0F, var9.getIntValue2() / 255.0F, var9.getIntValue3() / 255.0F, var9.getIntValue4() / 255.0F);
      method5284();
      BufferBuilder var16 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var16.vertex(var0, var1, var2, 0.0F).color(var6.method7287());
      var16.vertex(var0, var1, var2 + var4, 0.0F).color(var7.method7287());
      var16.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var8.method7287());
      var16.vertex(var0, var1 + var3, var2, 0.0F).color(var9.method7287());
      ImmediateRenderer.method8015(var16.end());
      method5285();
   }

   public static void method5270(Matrix4f var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, GlowColor var6) {
      method5269(var0, var1, var2, var3, var4, var5, var6.getZenithColor(), var6.getZenithColor2(), var6.getZenithColor4(), var6.getZenithColor3());
   }

   public static void method5271(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, GlowColor var7) {
      method5273(var0, var1, var2, var3, var4, var5, var6, var7.getZenithColor(), var7.getZenithColor2(), var7.getZenithColor4(), var7.getZenithColor3());
   }

   public static void method5272(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      method5273(var0, var1, var2, var3, var4, var5, var6, var7, var7, var7, var7);
   }

   public static void method5273(
      Matrix4f var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ProxyDotRadiusData var6,
      ZenithColor var7,
      ZenithColor var8,
      ZenithColor var9,
      ZenithColor var10
   ) {
      MsdfRenderer.flushBatch();
      float var12 = 0.4F;
      float var13 = 0.4F;
      if (var5 >= 0.15) {
         var12 = 0.6F;
         var13 = var12;
      }

      kawaseShaderKey8.method5165();
      kawaseShaderKey8.method5166("Size").method7413(var3, var4);
      kawaseShaderKey8.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
      kawaseShaderKey8.method5166("Smoothness").method7413(var12, var13);
      kawaseShaderKey8.method5166("Thickness").method7412(var5);
      method5284();
      BufferBuilder var18 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var18.vertex(var0, var1, var2, 0.0F).color(var7.method7287());
      var18.vertex(var0, var1, var2 + var4, 0.0F).color(var8.method7287());
      var18.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var9.method7287());
      var18.vertex(var0, var1 + var3, var2, 0.0F).color(var10.method7287());
      ImmediateRenderer.method8015(var18.end());
      method5285();
   }

   public static void method5274(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, GlowColor var8) {
      method5276(var0, var1, var2, var3, var4, var5, var6, var7, var8.getZenithColor(), var8.getZenithColor2(), var8.getZenithColor4(), var8.getZenithColor3());
   }

   public static void method5275(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithColor var8) {
      method5276(var0, var1, var2, var3, var4, var5, var6, var7, var8, var8, var8, var8);
   }

   public static void method5276(
      Matrix4f var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      ZenithColor var8,
      ZenithColor var9,
      ZenithColor var10,
      ZenithColor var11
   ) {
      MsdfRenderer.flushBatch();
      float var13 = 0.4F;
      float var14 = 0.4F;
      float var15 = Math.max(0.0F, Math.min(360.0F, var6));
      float var16 = Math.max(0.0F, Math.min(1.0F, var7));
      kawaseShaderKey10.method5165();
      kawaseShaderKey10.method5166("Size").method7413(var3, var4);
      kawaseShaderKey10.method5166("Smoothness").method7413(var13, var14);
      kawaseShaderKey10.method5166("Thickness").method7412(var5);
      kawaseShaderKey10.method5166("ArcDegrees").method7412(var15);
      kawaseShaderKey10.method5166("CapRoundness").method7412(var16);
      method5284();
      BufferBuilder var21 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var21.vertex(var0, var1, var2, 0.0F).color(var8.method7287());
      var21.vertex(var0, var1, var2 + var4, 0.0F).color(var9.method7287());
      var21.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var10.method7287());
      var21.vertex(var0, var1 + var3, var2, 0.0F).color(var11.method7287());
      ImmediateRenderer.method8015(var21.end());
      method5285();
   }

   public static void method5277(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7, ProxyDotRadiusData var8
   ) {
      var1 -= 0.3F;
      var2 -= 0.3F;
      var3 += 0.6F;
      var4 += 0.6F;
      method5279(var0, var1, var2, var6, var6, var5, var8, var7, 0.0F);
      method5279(var0, var1 + var3 - var6, var2, var6, var6, var5, var8, var7, 1.0F);
      method5279(var0, var1, var2 + var4 - var6, var6, var6, var5, var8, var7, 2.0F);
      method5279(var0, var1 + var3 - var6, var2 + var4 - var6, var6, var6, var5, var8, var7, 3.0F);
   }

   public static void method5278(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7, ProxyDotRadiusData var8
   ) {
      if (InterfaceModule.INSTANCE.method6822()) {
         var1 -= 0.3F;
         var2 -= 0.3F;
         var3 += 0.6F;
         var4 += 0.6F;
         method5279(var0, var1, var2, var6, var6, var5, var8, var7, 0.0F);
         method5279(var0, var1 + var3 - var6, var2, var6, var6, var5, var8, var7, 1.0F);
         method5279(var0, var1, var2 + var4 - var6, var6, var6, var5, var8, var7, 2.0F);
         method5279(var0, var1 + var3 - var6, var2 + var4 - var6, var6, var6, var5, var8, var7, 3.0F);
      }
   }

   public static void method5279(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, float var8
   ) {
      MsdfRenderer.flushBatch();
      float var10 = 0.8F;
      float var11 = 1.0F;
      kawaseShaderKey9.method5165();
      kawaseShaderKey9.method5166("Size").method7413(var3, var4);
      kawaseShaderKey9.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
      kawaseShaderKey9.method5166("Smoothness").method7413(var10, var11);
      kawaseShaderKey9.method5166("Thickness").method7412(var5);
      kawaseShaderKey9.method5166("CornerIndex").method7412(var8);
      method5284();
      BufferBuilder var16 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var16.vertex(var0, var1, var2, 0.0F).color(var7.method7287());
      var16.vertex(var0, var1, var2 + var4, 0.0F).color(var7.method7287());
      var16.vertex(var0, var1 + var3, var2 + var4, 0.0F).color(var7.method7287());
      var16.vertex(var0, var1 + var3, var2, 0.0F).color(var7.method7287());
      ImmediateRenderer.method8015(var16.end());
      method5285();
   }

   public static void method5280(Matrix4f var0, Identifier var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      RenderState.method1888(0, TextureFilterUtils.method7269(var1), TextureFilterUtils.method7268(var1));
      method5284();
      BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var8.vertex(var0, var2, var3, 0.0F).texture(0.0F, 0.0F).color(var6.method7287());
      var8.vertex(var0, var2, var3 + var5, 0.0F).texture(0.0F, 1.0F).color(var6.method7287());
      var8.vertex(var0, var2 + var4, var3 + var5, 0.0F).texture(1.0F, 1.0F).color(var6.method7287());
      var8.vertex(var0, var2 + var4, var3, 0.0F).texture(1.0F, 0.0F).color(var6.method7287());
      ImmediateRenderer.method8015(var8.end());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5281(Matrix4f var0, Identifier var1, float var2, float var3, float var4, float var5, GlowColor var6) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      RenderState.method1888(0, TextureFilterUtils.method7269(var1), TextureFilterUtils.method7268(var1));
      method5284();
      BufferBuilder var7 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var7.vertex(var0, var2, var3, 0.0F).texture(0.0F, 0.0F).color(var6.getZenithColor().method7287());
      var7.vertex(var0, var2, var3 + var5, 0.0F).texture(0.0F, 1.0F).color(var6.getZenithColor2().method7287());
      var7.vertex(var0, var2 + var4, var3 + var5, 0.0F).texture(1.0F, 1.0F).color(var6.getZenithColor4().method7287());
      var7.vertex(var0, var2 + var4, var3, 0.0F).texture(1.0F, 0.0F).color(var6.getZenithColor3().method7287());
      ImmediateRenderer.method8015(var7.end());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5282(
      Matrix4f var0, Identifier var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, ZenithColor var10
   ) {
      MsdfRenderer.flushBatch();
      RenderState.method1871();
      RenderState.method1873();
      int var11 = var10.method7287();
      float var13 = var2 + var4;
      float var14 = var3 + var5;
      RenderState.method1898("core/position_tex_color");
      RenderState.method1888(0, TextureFilterUtils.method7269(var1), TextureFilterUtils.method7268(var1));
      BufferBuilder var15 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var15.vertex(var0, var2, var3, 0.0F).texture(var6, var8).color(var11);
      var15.vertex(var0, var2, var14, 0.0F).texture(var6, var9).color(var11);
      var15.vertex(var0, var13, var14, 0.0F).texture(var7, var9).color(var11);
      var15.vertex(var0, var13, var3, 0.0F).texture(var7, var8).color(var11);
      ImmediateRenderer.method8015(var15.end());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
      RenderState.method1872();
   }

   public static void method5283(float[] var0, KawaseShaderKey var1, float var2, float var3, float var4) {
      if (var0 == null) {
         var1.method5166("Tab").method7415(0.0F, 0.0F, 0.0F, 0.0F);
         var1.method5166("TabRadius").method7412(0.0F);
         var1.method5166("MainHeight").method7412(0.0F);
         var1.method5166("Joint").method7412(0.0F);
      } else {
         var1.method5166("Tab").method7415(var0[0] - var2, var0[1] - var3, var0[2], var0[3]);
         var1.method5166("TabRadius").method7412(var0[4] * var4);
         var1.method5166("MainHeight").method7412(var0[5] - var3);
         var1.method5166("Joint").method7412(var0[6]);
      }
   }

   public static void method5284() {
      RenderState.method1871();
      RenderState.method1873();
   }

   public static void method5285() {
      RenderState.method1872();
   }

   public static void method5286(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      method5287(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         (InterfaceModule.INSTANCE.method6817() || InterfaceModule.INSTANCE.method6818()) && flag,
         InterfaceModule.INSTANCE.method6819()
      );
   }

   public static void method5287(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, boolean var8, boolean var9
   ) {
      if (var9 && InterfaceModule.INSTANCE.method6821()) {
         method5290(var0, var1, var2, var3, var4, InterfaceModule.INSTANCE.method6823(), ProxyDotRadiusData.method8069(InterfaceModule.method6801()));
      }

      if (var8) {
         method5301(var0, var1, var2, var3, var4, var6, var7, InterfaceModule.INSTANCE.method6818());
      }

      if (var9 && !InterfaceModule.INSTANCE.method6821()) {
         method5290(var0, var1, var2, var3, var4, InterfaceModule.INSTANCE.method6823(), ProxyDotRadiusData.method8069(InterfaceModule.method6801()));
      }
   }

   public static void method5288(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, ProxyDotRadiusData var9, ZenithColor var10
   ) {
      float var11 = var3;
      float var12 = var4;
      if (!(var3 <= 0.0F) && !(var4 <= 0.0F) && var10.getIntValue4() != 0) {
         MsdfRenderer.flushBatch();

         try {
            float var13 = (float)Math.ceil(Math.max(0.0F, var7) * 1.5F + Math.max(0.0F, var8)) + 1.0F;
            float var14 = var1 + var5;
            float var15 = var2 + var6;
            float var16 = var14 - var13;
            float var17 = var15 - var13;
            float var18 = var11 + var13 * 2.0F;
            float var19 = var12 + var13 * 2.0F;
            shadowShaderKey.method5165();
            shadowShaderKey.method5497(var18, var19, var11, var12, var13, Math.max(0.0F, var7), var8, var9);
            method5284();
            BufferBuilder var20 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
            var20.vertex(var0, var16, var17, 0.0F).color(var10.method7287());
            var20.vertex(var0, var16, var17 + var19, 0.0F).color(var10.method7287());
            var20.vertex(var0, var16 + var18, var17 + var19, 0.0F).color(var10.method7287());
            var20.vertex(var0, var16 + var18, var17, 0.0F).color(var10.method7287());
            ImmediateRenderer.method8015(var20.end());
         } finally {
            method5285();
         }
      }
   }

   public static void method5289(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      MsdfRenderer.flushBatch();
      Zenith.getInstance().getKawaseBlur().method7355(var0, var1, var2, var3, var4, var6, var7);
   }

   public static void method5290(Matrix4f var0, float var1, float var2, float var3, float var4, int var5, ProxyDotRadiusData var6) {
      ShapeRenderer.method7167(var0, var1, var2, var3, var4, var5, var6, Zenith.getInstance().getStyleManager().getGlowColor());
   }

   public static void method5291(Matrix4f var0, Identifier var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      method5296(var0, var1, var2, var3, var4, var4, var5, var6, 0.125F, 0.125F, 0.25F, 0.25F);
   }

   public static void method5292(Matrix4f var0, Identifier var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6) {
      method5294(var0, var1, var2, var3, var4, var5, var6, ZenithColor.INSTANCE);
   }

   public static void method5293(
      Matrix4f var0,
      Identifier var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ProxyDotRadiusData var6,
      ZenithColor var7,
      ZenithColor var8,
      ZenithColor var9,
      ZenithColor var10
   ) {
      MsdfRenderer.flushBatch();
      float var12 = 0.8F;
      kawaseShaderKey6.method5165();
      RenderState.method1888(0, TextureFilterUtils.method7269(var1), TextureFilterUtils.method7268(var1));
      kawaseShaderKey6.method5166("Size").method7413(var4, var5);
      kawaseShaderKey6.method5166("Radius")
         .method7415(var6.getFloatValue() * 2.0F, var6.getFloatValue4() * 2.0F, var6.getFloatValue2() * 2.0F, var6.getFloatValue3() * 2.0F);
      kawaseShaderKey6.method5166("Smoothness").method7412(var12);
      method5284();
      BufferBuilder var17 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var17.vertex(var0, var2, var3, 0.0F).texture(0.0F, 0.0F).color(var7.method7287());
      var17.vertex(var0, var2, var3 + var5, 0.0F).texture(0.0F, 1.0F).color(var8.method7287());
      var17.vertex(var0, var2 + var4, var3 + var5, 0.0F).texture(1.0F, 1.0F).color(var9.method7287());
      var17.vertex(var0, var2 + var4, var3, 0.0F).texture(1.0F, 0.0F).color(var10.method7287());
      ImmediateRenderer.method8015(var17.end());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5294(Matrix4f var0, Identifier var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      method5293(var0, var1, var2, var3, var4, var5, var6, var7, var7, var7, var7);
   }

   public static void method5295(Matrix4f var0, Identifier var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, GlowColor var7) {
      method5293(var0, var1, var2, var3, var4, var5, var6, var7.getZenithColor(), var7.getZenithColor2(), var7.getZenithColor4(), var7.getZenithColor3());
   }

   public static void method5296(
      Matrix4f var0,
      Identifier var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ProxyDotRadiusData var6,
      ZenithColor var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      method5297(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, false);
   }

   private static void method5297(
      Matrix4f var0,
      Identifier var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ProxyDotRadiusData var6,
      ZenithColor var7,
      float var8,
      float var9,
      float var10,
      float var11,
      boolean var12
   ) {
      MsdfRenderer.flushBatch();
      float var14 = 0.8F;
      kawaseShaderKey6.method5165();
      RenderState.method1888(0, TextureFilterUtils.method7269(var1), TextureFilterUtils.method7268(var1));
      kawaseShaderKey6.method5166("Size").method7413(var4, var5);
      kawaseShaderKey6.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
      kawaseShaderKey6.method5166("Smoothness").method7412(var14);
      method5284();
      if (var12) {
         RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      }

      BufferBuilder var19 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var19.vertex(var0, var2, var3, 0.0F).texture(var8, var9).color(var7.method7287());
      var19.vertex(var0, var2, var3 + var5, 0.0F).texture(var8, var11).color(var7.method7287());
      var19.vertex(var0, var2 + var4, var3 + var5, 0.0F).texture(var10, var11).color(var7.method7287());
      var19.vertex(var0, var2 + var4, var3, 0.0F).texture(var10, var9).color(var7.method7287());
      ImmediateRenderer.method8015(var19.end());
      if (var12) {
         RenderState.method1873();
      }

      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5298(
      Matrix4f var0,
      GpuTextureView var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ProxyDotRadiusData var6,
      ZenithColor var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      MsdfRenderer.flushBatch();
      float var13 = 0.8F;
      kawaseShaderKey6.method5165();
      RenderState.method1887(0, var1);
      kawaseShaderKey6.method5166("Size").method7413(var4, var5);
      kawaseShaderKey6.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
      kawaseShaderKey6.method5166("Smoothness").method7412(var13);
      method5284();
      BufferBuilder var18 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var18.vertex(var0, var2, var3, 0.0F).texture(var8, var9).color(var7.method7287());
      var18.vertex(var0, var2, var3 + var5, 0.0F).texture(var8, var11).color(var7.method7287());
      var18.vertex(var0, var2 + var4, var3 + var5, 0.0F).texture(var10, var11).color(var7.method7287());
      var18.vertex(var0, var2 + var4, var3, 0.0F).texture(var10, var9).color(var7.method7287());
      ImmediateRenderer.method8015(var18.end());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5299(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      MsdfRenderer.flushBatch();
      kawaseShaderKey.method5165();
      kawaseShaderKey.method5166("Size").method7413(var3, var4);
      kawaseShaderKey.method5166("Radius")
         .method7415(var6.getFloatValue() * 3.0F, var6.getFloatValue4() * 3.0F, var6.getFloatValue2() * 3.0F, var6.getFloatValue3() * 3.0F);
      kawaseShaderKey.method5166("Smoothness").method7412(var5);
      method5284();
      float var9 = -var5 / 2.0F + var5 * 2.0F;
      float var10 = var5 / 2.0F + var5;
      float var11 = var1 - var9 / 2.0F;
      float var12 = var2 - var10 / 2.0F;
      float var13 = var3 + var9;
      float var14 = var4 + var10;
      BufferBuilder var15 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var15.vertex(var0, var11, var12, 0.0F).color(var7.method7287());
      var15.vertex(var0, var11, var12 + var14, 0.0F).color(var7.method7287());
      var15.vertex(var0, var11 + var13, var12 + var14, 0.0F).color(var7.method7287());
      var15.vertex(var0, var11 + var13, var12, 0.0F).color(var7.method7287());
      ImmediateRenderer.method8015(var15.end());
      method5285();
   }

   public static void method5300(Matrix4f var0, CategoryIcon var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      method5282(var0, var1.getIdentifier(), var2, var3, var4, var5, 0.0F, 1.0F, 0.0F, 1.0F, var6);
   }

   public static void method5301(Matrix4f var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6, boolean var7) {
      if (Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F) {
         MsdfRenderer.flushBatch();
         if (var7) {
            method5264(var0, var1, var2, var3, var4, 10.0F, 0.08F, var5, var6);
         } else {
            Zenith.getInstance().getKawaseBlur().method7353(var0, var1, var2, var3, var4, var5, var6);
         }
      }
   }

   @Generated
   private RenderShaders() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

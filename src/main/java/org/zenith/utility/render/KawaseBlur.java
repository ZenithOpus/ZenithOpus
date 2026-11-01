package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.ResourceProvider;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class KawaseBlur implements MinecraftAccess {
   private static final int CONST_intValue = 2;
   private static final float[] CONST_floatValues = new float[]{0.5F, 1.0F, 1.5F, 2.0F, 2.5F, 3.0F};
   private SimpleFramebuffer fbo;
   private SimpleFramebuffer simpleFramebuffer;
   private SimpleFramebuffer tempFbo;
   private KawaseShaderKey kawaseShaderKey;
   private KawaseShaderKey kawaseShaderKey2;
   private KawaseShaderKey kawaseShaderKey3;
   private KawaseShaderKey kawaseShaderKey4;
   private int intValue = 0;
   private int intValue2 = 0;
   private boolean flag = true;

   public KawaseBlur() {
      EventManager.register(this);
   }

   @EventTarget(0)
   public void method7348(ScreenRenderEvent var1) {
      if (mc.getShaderLoader() != null && (Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F || InterfaceModule.INSTANCE.method6818())) {
         Framebuffer var2 = mc.getFramebuffer();
         this.method7361(var2);
         if (this.intValue % 3 == 0) {
            float var3 = Zenith.getInstance().getNLMenuScreen().getBlurPower();
            if (var3 == 0.0F) {
               return;
            }

            this.method7352(this.fbo, MatrixUtils.method664(var1.getMenuDrawContext().getMatrices()), var3);
            this.intValue = 0;
         }
      }
   }

   public void method7349(MenuDrawContext var1) {
      if (mc.world == null && this.intValue == 0 && this.fbo != null && mc.getShaderLoader() != null) {
         float var2 = Zenith.getInstance().getNLMenuScreen().getBlurPower();
         if (var2 != 0.0F) {
            this.method7352(this.fbo, MatrixUtils.method664(var1.getMatrices()), var2);
         }
      }
   }

   public void method7350(MenuDrawContext var1) {
      if (mc.getShaderLoader() != null && Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F && this.flag) {
         Framebuffer var2 = mc.getFramebuffer();
         this.method7362(var2);
         if (this.intValue2 % (this.method7351() ? 2 : 10) == 0) {
            this.method7352(this.simpleFramebuffer, MatrixUtils.method664(var1.getMatrices()), 10.0F);
            this.intValue2 = 0;
         }
      }
   }

   private boolean method7351() {
      return MenuModule.INSTANCE.method5344()
         && (Zenith.getInstance().getNLMenuScreen().isClosing() || Zenith.getInstance().getNLMenuScreen().isElementSwapBlurActive());
   }

   private void method7352(Framebuffer var1, Matrix4f var2, float var3) {
      Framebuffer var4 = mc.getFramebuffer();
      this.method7363(var4);
      if (this.kawaseShaderKey == null) {
         this.kawaseShaderKey = new KawaseShaderKey(
            ResourceProvider.getShaderIdentifier("kawase_blur/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR
         );
      }

      if (this.isShaderReady(this.kawaseShaderKey)) {
         float var5 = 1.0F / var1.textureWidth;
         float var6 = 1.0F / var1.textureHeight;
         RenderState.Data2 var7 = RenderState.method1904();
         RenderState.method1872();
         RenderState.method1877();

         try {
            this.downsample(var4, var1);
            Object var8 = var1;
            Object var9 = this.tempFbo;

            for (float var13 : CONST_floatValues) {
               float var14 = var13 * var3 / 10.0F;
               if (!this.kawasePass(BlitUtils.method6450((Framebuffer)var8), (Framebuffer)var9, var2, var5, var6, var14)) {
                  return;
               }

               Object var15 = var8;
               var8 = var9;
               var9 = var15;
            }
         } finally {
            RenderState.method1893(0);
            RenderState.method1905(var7);
         }
      }
   }

   private void downsample(Framebuffer var1, Framebuffer var2) {
      BlitUtils.method6455(BlitUtils.method6450(var1), var2);
   }

   private boolean kawasePass(GpuTextureView var1, Framebuffer var2, Matrix4f var3, float var4, float var5, float var6) {
      ImmediateRenderer.method8013(var2);
      RenderState.method1889(0, var1);
      KawaseShaderKey var7 = this.kawaseShaderKey.method5165();
      if (var7 == null) {
         ImmediateRenderer.method8013(null);
         RenderState.method1893(0);
         return false;
      } else {
         var7.method5166("Resolution").method7413(var4, var5);
         var7.method5166("Offset").method7412(var6);
         BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
         ShapeRenderer.method7161(var3, var8, 0.0F, 0.0F, var2.textureWidth, var2.textureHeight, -1);
         ImmediateRenderer.method8010(var2.textureWidth, var2.textureHeight);
         ImmediateRenderer.method8015(var8.end());
         RenderState.method1893(0);
         return true;
      }
   }

   public void method7353(Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      this.method7356(this.fbo, var1, var2, var3, var4, var5, var6, var7);
   }

   public void method7354(List<KawaseBlur.Data> var1) {
      MsdfRenderer.flushBatch();
      this.method7357(this.fbo, var1);
   }

   public void method7355(Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      this.flag = true;
      Framebuffer var8 = mc.getFramebuffer();
      this.method7362(var8);
      this.method7356(this.simpleFramebuffer, var1, var2, var3, var4, var5, var6, var7);
   }

   private void method7356(Framebuffer var1, Matrix4f var2, float var3, float var4, float var5, float var6, ProxyDotRadiusData var7, ZenithColor var8) {
      if (var1 != null) {
         if (this.kawaseShaderKey2 == null) {
            this.kawaseShaderKey2 = new KawaseShaderKey(
               ResourceProvider.getShaderIdentifier("wtf/data"), net.minecraft.client.render.VertexFormats.POSITION_COLOR
            );
         }

         if (this.isShaderReady(this.kawaseShaderKey2)) {
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();

            try {
               RenderState.method1889(0, BlitUtils.method6450(var1));
               KawaseShaderKey var9 = this.kawaseShaderKey2.method5165();
               if (var9 != null) {
                  var9.method5166("Size").method7413(var5, var6);
                  var9.method5166("Radius").method7415(var7.getFloatValue(), var7.getFloatValue4(), var7.getFloatValue2(), var7.getFloatValue3());
                  var9.method5166("Smoothness").method7412(0.01F);
                  BufferBuilder var10 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
                  var10.vertex(var2, var3, var4, 0.0F).color(var8.method7287());
                  var10.vertex(var2, var3, var4 + var6, 0.0F).color(var8.method7287());
                  var10.vertex(var2, var3 + var5, var4 + var6, 0.0F).color(var8.method7287());
                  var10.vertex(var2, var3 + var5, var4, 0.0F).color(var8.method7287());
                  ImmediateRenderer.method8015(var10.end());
                  return;
               }
            } finally {
               RenderState.method1893(0);
               RenderState.method1876();
               RenderState.method1872();
            }
         }
      }
   }

   private void method7357(Framebuffer var1, List<KawaseBlur.Data> var2) {
      if (var1 != null && !var2.isEmpty()) {
         if (this.kawaseShaderKey3 == null) {
            this.kawaseShaderKey3 = new KawaseShaderKey(
               ResourceProvider.getShaderIdentifier("batch_wtf/data"), net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR
            );
         }

         if (!this.isShaderReady(this.kawaseShaderKey3)) {
            for (KawaseBlur.Data var10 : var2) {
               this.method7356(
                  var1, var10.matrix4f, var10.floatValue, var10.floatValue2, var10.floatValue3, var10.floatValue4, var10.proxyDotRadiusData, var10.zenithColor
               );
            }
         } else {
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            RenderState.method1889(0, BlitUtils.method6450(var1));

            try {
               int var3 = 0;

               while (var3 < var2.size()) {
                  ProxyDotRadiusData var4 = ((KawaseBlur.Data)var2.get(var3)).proxyDotRadiusData;
                  int var5 = var3 + 1;

                  while (var5 < var2.size() && this.method7360(var4, ((KawaseBlur.Data)var2.get(var5)).proxyDotRadiusData)) {
                     var5++;
                  }

                  this.method7358(var2, var3, var5, var4);
                  var3 = var5;
               }
            } finally {
               RenderState.method1893(0);
               RenderState.method1876();
               RenderState.method1872();
            }
         }
      }
   }

   private void method7358(List<KawaseBlur.Data> var1, int var2, int var3, ProxyDotRadiusData var4) {
      KawaseShaderKey var5 = this.kawaseShaderKey3.method5165();
      if (var5 != null) {
         var5.method5166("Radius").method7415(var4.getFloatValue(), var4.getFloatValue4(), var4.getFloatValue2(), var4.getFloatValue3());
         var5.method5166("Smoothness").method7412(0.01F);
         BufferBuilder var6 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);

         for (int var7 = var2; var7 < var3; var7++) {
            this.method7359(var6, (KawaseBlur.Data)var1.get(var7));
         }

         ImmediateRenderer.method8015(var6.end());
      }
   }

   private void method7359(BufferBuilder var1, KawaseBlur.Data var2) {
      int var3 = var2.zenithColor.method7287();
      float var4 = var2.floatValue + var2.floatValue3;
      float var5 = var2.floatValue2 + var2.floatValue4;
      var1.vertex(var2.matrix4f, var2.floatValue, var2.floatValue2, 0.0F).texture(var2.floatValue3, var2.floatValue4).color(var3);
      var1.vertex(var2.matrix4f, var2.floatValue, var5, 0.0F).texture(var2.floatValue3, var2.floatValue4).color(var3);
      var1.vertex(var2.matrix4f, var4, var5, 0.0F).texture(var2.floatValue3, var2.floatValue4).color(var3);
      var1.vertex(var2.matrix4f, var4, var2.floatValue2, 0.0F).texture(var2.floatValue3, var2.floatValue4).color(var3);
   }

   private boolean method7360(ProxyDotRadiusData var1, ProxyDotRadiusData var2) {
      return var1.getFloatValue() == var2.getFloatValue()
         && var1.getFloatValue4() == var2.getFloatValue4()
         && var1.getFloatValue2() == var2.getFloatValue2()
         && var1.getFloatValue3() == var2.getFloatValue3();
   }

   private boolean isShaderReady(KawaseShaderKey var1) {
      return var1 != null && var1.text3 == null;
   }

   private void method7361(Framebuffer var1) {
      int var2 = this.method7366(var1.textureWidth);
      int var3 = this.method7366(var1.textureHeight);
      if (this.fbo == null) {
         this.fbo = this.method7364(var2, var3);
      }

      if (this.fbo.textureWidth != var2 || this.fbo.textureHeight != var3) {
         this.method7365(this.fbo, var2, var3);
      }
   }

   private void method7362(Framebuffer var1) {
      int var2 = this.method7366(var1.textureWidth);
      int var3 = this.method7366(var1.textureHeight);
      if (this.simpleFramebuffer == null) {
         this.simpleFramebuffer = this.method7364(var2, var3);
      }

      if (this.simpleFramebuffer.textureWidth != var2 || this.simpleFramebuffer.textureHeight != var3) {
         this.method7365(this.simpleFramebuffer, var2, var3);
      }
   }

   private void method7363(Framebuffer var1) {
      int var2 = this.method7366(var1.textureWidth);
      int var3 = this.method7366(var1.textureHeight);
      if (this.tempFbo == null) {
         this.tempFbo = this.method7364(var2, var3);
      }

      if (this.tempFbo.textureWidth != var2 || this.tempFbo.textureHeight != var3) {
         this.method7365(this.tempFbo, var2, var3);
      }
   }

   private SimpleFramebuffer method7364(int var1, int var2) {
      return BlitUtils.method6448("zenith blur", var1, var2, false);
   }

   private void method7365(SimpleFramebuffer var1, int var2, int var3) {
      BlitUtils.method6457(var1, var2, var3);
   }

   private int method7366(int var1) {
      return Math.max(1, var1 / 2);
   }

   public void method7367() {
      this.intValue = 3;
      this.intValue2 = 10;
   }

   public void method7368() {
      this.intValue++;
      this.intValue2++;
   }

   public void method7369(List<KawaseBlur.Data2> var1) {
      if (this.fbo != null && !var1.isEmpty()) {
         MsdfRenderer.flushBatch();
         if (this.kawaseShaderKey4 == null) {
            this.kawaseShaderKey4 = new KawaseShaderKey(
               ResourceProvider.getShaderIdentifier("batch_glass/data"), net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR
            );
         }

         if (!this.isShaderReady(this.kawaseShaderKey4)) {
            for (KawaseBlur.Data2 var9 : var1) {
               RenderShaders.floatValues = var9.getFloatValues();
               RenderShaders.method5264(
                  var9.getMatrix4f(),
                  var9.getFloatValue(),
                  var9.getFloatValue2(),
                  var9.width(),
                  var9.height(),
                  var9.getFloatValue5(),
                  var9.getFloatValue6(),
                  var9.getProxyDotRadiusData(),
                  var9.getZenithColor()
               );
               RenderShaders.floatValues = null;
            }
         } else {
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            RenderState.method1889(0, BlitUtils.method6450(this.fbo));

            try {
               int var2 = 0;

               while (var2 < var1.size()) {
                  KawaseBlur.Data2 var3 = (KawaseBlur.Data2)var1.get(var2);
                  int var4 = var2 + 1;

                  while (var4 < var1.size() && this.method7371(var3, (KawaseBlur.Data2)var1.get(var4))) {
                     var4++;
                  }

                  this.method7372(var1, var2, var4, var3);
                  var2 = var4;
               }
            } finally {
               RenderState.method1893(0);
               RenderState.method1876();
               RenderState.method1872();
            }
         }
      }
   }

   private void method7370(KawaseShaderKey var1, KawaseBlur.Data2 var2) {
      float[] var3 = var2.getFloatValues();
      if (var3 == null) {
         var1.method5166("Tab").method7415(0.0F, 0.0F, 0.0F, 0.0F);
         var1.method5166("TabRadius").method7412(0.0F);
         var1.method5166("MainHeight").method7412(0.0F);
         var1.method5166("Joint").method7412(0.0F);
      } else {
         float var4 = var2.method7373();
         float var5 = var2.method7374();
         float var6 = var2.getFloatValue5() / 2.0F;
         var1.method5166("Tab").method7415(var3[0] - var4, var3[1] - var5, var3[2], var3[3]);
         var1.method5166("TabRadius").method7412(var3[4] * var6);
         var1.method5166("MainHeight").method7412(var3[5] - var5);
         var1.method5166("Joint").method7412(var3[6]);
      }
   }

   private boolean method7371(KawaseBlur.Data2 var1, KawaseBlur.Data2 var2) {
      return var1.getFloatValues() == null
         && var2.getFloatValues() == null
         && this.method7360(var1.method7377(), var2.method7377())
         && var1.method7378() == var2.method7378()
         && var1.getFloatValue5() == var2.getFloatValue5()
         && var1.getFloatValue6() == var2.getFloatValue6();
   }

   private void method7372(List<KawaseBlur.Data2> var1, int var2, int var3, KawaseBlur.Data2 var4) {
      KawaseShaderKey var5 = this.kawaseShaderKey4.method5165();
      if (var5 != null) {
         ProxyDotRadiusData var6 = var4.method7377();
         var5.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
         var5.method5166("Smoothness").method7412(1.0F);
         var5.method5166("CornerSmoothness").method7412(var4.getFloatValue5());
         var5.method5166("FresnelPower").method7412(var4.method7378());
         var5.method5166("FresnelAlpha").method7412(1.0F);
         var5.method5166("BaseAlpha").method7412(1.0F);
         var5.method5166("FresnelInvert").set(1);
         var5.method5166("FresnelMix").method7412(0.0F);
         var5.method5166("DistortStrength").method7412(var4.getFloatValue6());
         var5.method5166("Time").method7412((float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
         ZenithColor var7 = InterfaceModule.INSTANCE.method6824();
         var5.method5166("GlareColor").method7414(var7.getIntValue() / 255.0F, var7.getIntValue2() / 255.0F, var7.getIntValue3() / 255.0F);
         var5.method5166("GlareAlpha").method7412(var7.getIntValue4() / 255.0F);
         var5.method5166("GlareSpeed").method7412(InterfaceModule.INSTANCE.method6825());
         var5.method5166("GlareMode").method7412(InterfaceModule.INSTANCE.method6826());
         var5.method5166("GlareTexture").method7412(InterfaceModule.INSTANCE.method6827());
         this.method7370(var5, var4);
         BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);

         for (int var9 = var2; var9 < var3; var9++) {
            KawaseBlur.Data2 var10 = (KawaseBlur.Data2)var1.get(var9);
            int var11 = var10.getZenithColor().method7287();
            float var12 = var10.method7373();
            float var13 = var10.method7374();
            float var14 = var10.method7375();
            float var15 = var10.method7376();
            float var16 = var12 + var14;
            float var17 = var13 + var15;
            var8.vertex(var10.getMatrix4f(), var12, var13, 0.0F).texture(var14, var15).color(var11);
            var8.vertex(var10.getMatrix4f(), var12, var17, 0.0F).texture(var14, var15).color(var11);
            var8.vertex(var10.getMatrix4f(), var16, var17, 0.0F).texture(var14, var15).color(var11);
            var8.vertex(var10.getMatrix4f(), var16, var13, 0.0F).texture(var14, var15).color(var11);
         }

         ImmediateRenderer.method8015(var8.end());
      }
   }

   @Generated
   public SimpleFramebuffer getFbo() {
      return this.fbo;
   }

   public static final class Data {
      private final Matrix4f matrix4f;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final ProxyDotRadiusData proxyDotRadiusData;
      private final ZenithColor zenithColor;

      public Matrix4f matrix4f() {
         return this.matrix4f;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData proxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor zenithColor() {
         return this.zenithColor;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.matrix4f, other.matrix4f) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.proxyDotRadiusData, other.proxyDotRadiusData) && java.util.Objects.equals(this.zenithColor, other.zenithColor);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(matrix4f, floatValue, floatValue2, floatValue3, floatValue4, proxyDotRadiusData, zenithColor);
      }

      @Override
      public String toString() {
         return "Data[" + "matrix4f=" + this.matrix4f + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "proxyDotRadiusData=" + this.proxyDotRadiusData + ", " + "zenithColor=" + this.zenithColor + "]";
      }

      public Data(Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
         this.matrix4f = var1;
         this.floatValue = var2;
         this.floatValue2 = var3;
         this.floatValue3 = var4;
         this.floatValue4 = var5;
         this.proxyDotRadiusData = var6;
         this.zenithColor = var7;
      }

      public Matrix4f getMatrix4f() {
         return this.matrix4f;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float width() {
         return this.floatValue3;
      }

      public float height() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData getProxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }
   }

   public static final class Data2 {
      private final Matrix4f matrix4f;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final ProxyDotRadiusData proxyDotRadiusData;
      private final ZenithColor zenithColor;
      private final float floatValue5;
      private final float floatValue6;
      private final float[] floatValues;

      public Matrix4f matrix4f() {
         return this.matrix4f;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData proxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor zenithColor() {
         return this.zenithColor;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.matrix4f, other.matrix4f) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.proxyDotRadiusData, other.proxyDotRadiusData) && java.util.Objects.equals(this.zenithColor, other.zenithColor) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValues, other.floatValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(matrix4f, floatValue, floatValue2, floatValue3, floatValue4, proxyDotRadiusData, zenithColor, floatValue5, floatValue6, floatValues);
      }

      @Override
      public String toString() {
         return "Data2[" + "matrix4f=" + this.matrix4f + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "proxyDotRadiusData=" + this.proxyDotRadiusData + ", " + "zenithColor=" + this.zenithColor + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValues=" + this.floatValues + "]";
      }
      private static final float CONST_floatValue = 1.5F;

      public Data2(
         Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, float var8, float var9, float[] var10
      ) {
         this.matrix4f = var1;
         this.floatValue = var2;
         this.floatValue2 = var3;
         this.floatValue3 = var4;
         this.floatValue4 = var5;
         this.proxyDotRadiusData = var6;
         this.zenithColor = var7;
         this.floatValue5 = var8;
         this.floatValue6 = var9;
         this.floatValues = var10;
      }

      public float method7373() {
         return this.floatValue - 1.5F;
      }

      public float method7374() {
         return this.floatValue2 - 1.5F;
      }

      public float method7375() {
         return this.floatValue3 + 3.0F;
      }

      public float method7376() {
         return this.floatValue4 + 3.0F;
      }

      public ProxyDotRadiusData method7377() {
         return new ProxyDotRadiusData(
            this.proxyDotRadiusData.getFloatValue() * this.floatValue5 / 2.0F,
            this.proxyDotRadiusData.getFloatValue2() * this.floatValue5 / 2.0F,
            this.proxyDotRadiusData.getFloatValue3() * this.floatValue5 / 2.0F,
            this.proxyDotRadiusData.getFloatValue4() * this.floatValue5 / 2.0F
         );
      }

      public float method7378() {
         return this.floatValue4 == 240.0F ? 100.0F : 50.0F;
      }

      public Matrix4f getMatrix4f() {
         return this.matrix4f;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float width() {
         return this.floatValue3;
      }

      public float height() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData getProxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }
   }
}

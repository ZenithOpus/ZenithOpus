package org.zenith.client.screens.override.main;

import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.ResourceProvider;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MainMenuBlurRenderer implements MinecraftAccess {
   private static final int DOWNSAMPLE_FACTOR = 2;
   private static final float[] KAWASE_OFFSETS = new float[]{0.5F, 1.0F, 1.5F, 2.0F, 2.5F, 3.0F};
   private SimpleFramebuffer backgroundFbo;
   private SimpleFramebuffer tempFbo;
   private KawaseShaderKey kawaseShaderKey;
   private KawaseShaderKey maskShaderKey;

   public void capture(MenuDrawContext var1, float var2) {
      if (mc.getShaderLoader() != null && !(var2 <= 0.0F)) {
         MsdfRenderer.flushBatch();
         Framebuffer var3 = mc.getFramebuffer();
         this.ensureFramebuffers(var3);
         this.blurCurrentFramebuffer(var3, MatrixUtils.method664(var1.getMatrices()), var2);
      }
   }

   public GpuTextureView texture() {
      return this.backgroundFbo == null ? null : this.backgroundFbo.getColorAttachmentView();
   }

   public void render(Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      if (this.backgroundFbo != null && mc.getShaderLoader() != null) {
         if (this.maskShaderKey == null) {
            this.maskShaderKey = new KawaseShaderKey(ResourceProvider.getShaderIdentifier("wtf/data"), VertexFormats.POSITION_COLOR);
         }

         if (this.isShaderReady(this.maskShaderKey)) {
            MsdfRenderer.flushBatch();
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();

            try {
               RenderState.method1889(0, this.backgroundFbo.getColorAttachmentView());
               KawaseShaderKey var8 = this.maskShaderKey.method5165();
               if (var8 != null) {
                  var8.method5166("Size").method7413(var4, var5);
                  var8.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
                  var8.method5166("Smoothness").method7412(0.01F);
                  BufferBuilder var9 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                  var9.vertex(var1, var2, var3, 0.0F).color(var7.method7287());
                  var9.vertex(var1, var2, var3 + var5, 0.0F).color(var7.method7287());
                  var9.vertex(var1, var2 + var4, var3 + var5, 0.0F).color(var7.method7287());
                  var9.vertex(var1, var2 + var4, var3, 0.0F).color(var7.method7287());
                  ImmediateRenderer.method8015(var9.end());
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

   private void blurCurrentFramebuffer(Framebuffer var1, Matrix4f var2, float var3) {
      if (this.kawaseShaderKey == null) {
         this.kawaseShaderKey = new KawaseShaderKey(ResourceProvider.getShaderIdentifier("kawase_blur/data"), VertexFormats.POSITION_COLOR);
      }

      if (this.isShaderReady(this.kawaseShaderKey)) {
         float var4 = 1.0F / this.backgroundFbo.textureWidth;
         float var5 = 1.0F / this.backgroundFbo.textureHeight;
         RenderState.Data2 var6 = RenderState.method1904();
         RenderState.method1872();
         RenderState.method1877();

         try {
            this.downsample(var1, this.backgroundFbo);
            SimpleFramebuffer var7 = this.backgroundFbo;
            SimpleFramebuffer var8 = this.tempFbo;

            for (float var12 : KAWASE_OFFSETS) {
               float var13 = var12 * var3 / 10.0F;
               if (!this.kawasePass(BlitUtils.method6450(var7), var8, var2, var4, var5, var13)) {
                  return;
               }

               SimpleFramebuffer var14 = var7;
               var7 = var8;
               var8 = var14;
            }
         } finally {
            RenderState.method1893(0);
            RenderState.method1905(var6);
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
         BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         ShapeRenderer.method7161(var3, var8, 0.0F, 0.0F, var2.textureWidth, var2.textureHeight, -1);
         ImmediateRenderer.method8010(var2.textureWidth, var2.textureHeight);
         ImmediateRenderer.method8015(var8.end());
         RenderState.method1893(0);
         return true;
      }
   }

   private void ensureFramebuffers(Framebuffer var1) {
      int var2 = Math.max(1, var1.textureWidth / 2);
      int var3 = Math.max(1, var1.textureHeight / 2);
      this.backgroundFbo = this.ensureFramebuffer(this.backgroundFbo, var2, var3);
      this.tempFbo = this.ensureFramebuffer(this.tempFbo, var2, var3);
   }

   private SimpleFramebuffer ensureFramebuffer(SimpleFramebuffer var1, int var2, int var3) {
      if (var1 == null) {
         var1 = BlitUtils.method6448("zenith menu blur", var2, var3, false);
      } else if (var1.textureWidth != var2 || var1.textureHeight != var3) {
         BlitUtils.method6457(var1, var2, var3);
      }

      return var1;
   }

   private boolean isShaderReady(KawaseShaderKey var1) {
      ShaderLoader var2 = mc.getShaderLoader();
      return var2 == null ? false : var2.getSource(var1.getIdentifier(), ShaderType.FRAGMENT) != null;
   }

   public void close() {
      if (this.backgroundFbo != null) {
         this.backgroundFbo.delete();
         this.backgroundFbo = null;
      }

      if (this.tempFbo != null) {
         this.tempFbo.delete();
         this.tempFbo = null;
      }
   }
}

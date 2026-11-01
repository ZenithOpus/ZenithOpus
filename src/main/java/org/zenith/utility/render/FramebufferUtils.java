package org.zenith.utility.render;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.FloatBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlSampler;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.GlTextureView;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.zenith.RenderState;
import org.zenith.utility.mixin.accessors.GlCommandEncoderAccessor;

public final class FramebufferUtils {
   private static final int CONST_intValue = 8;
   private static int intValue;
   private static int intValue2;

   private FramebufferUtils() {
   }

   public static void method5653() {
      GlCommandEncoderAccessor var0 = (GlCommandEncoderAccessor)(Object)RenderSystem.getDevice().createCommandEncoder();
      var0.zenith$setCurrentProgram(null);
      var0.zenith$setCurrentPipeline(null);
   }

   public static void method5654(int var0, @Nullable Framebuffer var1) {
      method5655(var0, var1, -1.0F, -1.0F, 1.0F, 1.0F);
   }

   public static void method5655(int var0, @Nullable Framebuffer var1, float var2, float var3, float var4, float var5) {
      RenderSystem.assertOnRenderThread();
      Framebuffer var6 = var1 != null ? var1 : MinecraftClient.getInstance().getFramebuffer();
      GpuTextureView var7 = var6.getColorAttachmentView();
      if (var7 != null) {
         GpuTexture var8 = var6.useDepthAttachment ? var6.getDepthAttachment() : null;
         GlBackend var9 = (GlBackend)RenderSystem.getDevice();
         int var10 = ((GlTextureView)var7).getOrCreateFramebuffer(var9.getBufferManager(), var8);
         GlStateManager._glBindFramebuffer(36160, var10);
         GlStateManager._viewport(0, 0, var6.textureWidth, var6.textureHeight);
         GlStateManager._glUseProgram(var0);
         method5656(RenderState.method1904());
         method5657();
         method5658();
         method5659();
         GlStateManager._glBindVertexArray(intValue);
         GlStateManager._glBindBuffer(34962, intValue2);
         MemoryStack var11 = MemoryStack.stackPush();

         try {
            FloatBuffer var12 = var11.floats(new float[]{var2, var3, 0.0F, var4, var3, 0.0F, var2, var5, 0.0F, var4, var5, 0.0F});
            GL15.glBufferSubData(34962, 0L, var12);
         } catch (Throwable var15) {
            if (var11 != null) {
               try {
                  var11.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (var11 != null) {
            var11.close();
         }

         GL11.glDrawArrays(5, 0, 4);
         GlStateManager._glBindBuffer(34962, 0);
         GlStateManager._glBindVertexArray(0);
         GlStateManager._glBindFramebuffer(36160, 0);
         method5653();
      }
   }

   private static void method5656(RenderState.Data2 var0) {
      if (var0.isFlag()) {
         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(
            GlConst.toGl(var0.getSourceFactor()),
            GlConst.toGl(var0.getDestFactor()),
            GlConst.toGl(var0.getSourceFactor2()),
            GlConst.toGl(var0.getDestFactor2())
         );
      } else {
         GlStateManager._disableBlend();
      }

      if (var0.isFlag2()) {
         GlStateManager._enableCull();
      } else {
         GlStateManager._disableCull();
      }

      if (var0.getDepthTestFunction() != DepthTestFunction.NO_DEPTH_TEST) {
         GlStateManager._enableDepthTest();
         GlStateManager._depthFunc(GlConst.toGl(var0.getDepthTestFunction()));
      } else {
         GlStateManager._disableDepthTest();
      }

      GlStateManager._depthMask(var0.isFlag3());
      boolean var1 = RenderState.isFlag5();
      GlStateManager._colorMask(var1, var1, var1, RenderState.isFlag6());
      GlStateManager._disablePolygonOffset();
      GlStateManager._disableColorLogicOp();
   }

   private static void method5657() {
      ScissorState var0 = RenderSystem.getScissorStateForRenderTypeDraws();
      if (var0.isEnabled()) {
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(var0.getX(), var0.getY(), var0.getWidth(), var0.getHeight());
      } else {
         GlStateManager._disableScissorTest();
      }
   }

   private static void method5658() {
      for (int var0 = 0; var0 < 8; var0++) {
         GpuTextureView var1 = RenderState.method1894(var0);
         if (var1 != null) {
            GlStateManager._activeTexture(33984 + var0);
            GlStateManager._bindTexture(((GlTexture)var1.texture()).getGlId());
            GlSampler var2 = (GlSampler)RenderState.method1896(var0);
            GL33.glBindSampler(var0, var2.getSamplerId());
         }
      }

      GlStateManager._activeTexture(33984);
   }

   private static void method5659() {
      if (intValue == 0) {
         intValue = GlStateManager._glGenVertexArrays();
         intValue2 = GlStateManager._glGenBuffers();
         GlStateManager._glBindVertexArray(intValue);
         GlStateManager._glBindBuffer(34962, intValue2);
         GL15.glBufferData(34962, 48L, 35048);
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 3, 5126, false, 12, 0L);
         GlStateManager._glBindVertexArray(0);
         GlStateManager._glBindBuffer(34962, 0);
      }
   }
}

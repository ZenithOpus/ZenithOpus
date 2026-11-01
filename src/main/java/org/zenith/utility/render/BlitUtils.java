package org.zenith.utility.render;

import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class BlitUtils {
   private BlitUtils() {
   }

   public static SimpleFramebuffer method6448(String var0, int var1, int var2, boolean var3) {
      SimpleFramebuffer var4 = new SimpleFramebuffer(var0, Math.max(1, var1), Math.max(1, var2), var3);
      method6456(var4, 0);
      return var4;
   }

   public static void method6449(@Nullable Framebuffer var0) {
      if (var0 != null) {
         var0.delete();
      }
   }

   @Nullable
   public static GpuTextureView method6450(@Nullable Framebuffer var0) {
      return var0 == null ? null : var0.getColorAttachmentView();
   }

   @Nullable
   public static GpuTextureView method6451(@Nullable Framebuffer var0) {
      return var0 != null && var0.useDepthAttachment ? var0.getDepthAttachmentView() : null;
   }

   public static Framebuffer method6452() {
      return MinecraftClient.getInstance().getFramebuffer();
   }

   public static void method6453(@Nullable Framebuffer var0, @Nullable Framebuffer var1) {
      if (var0 != null && var1 != null) {
         GpuTexture var2 = var0.getColorAttachment();
         GpuTexture var3 = var1.getColorAttachment();
         if (var2 != null && var3 != null) {
            if ((var0.textureWidth != var1.textureWidth || var0.textureHeight != var1.textureHeight) && method6454()) {
               method6455(var0.getColorAttachmentView(), var1);
            } else {
               RenderSystem.getDevice()
                  .createCommandEncoder()
                  .copyTextureToTexture(
                     var2, var3, 0, 0, 0, 0, 0, Math.min(var0.textureWidth, var1.textureWidth), Math.min(var0.textureHeight, var1.textureHeight)
                  );
            }
         }
      }
   }

   public static boolean method6454() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0 != null && var0.getShaderLoader() != null
         ? var0.getShaderLoader().getSource(Identifier.ofVanilla("core/screenquad"), ShaderType.VERTEX) != null
            && var0.getShaderLoader().getSource(Identifier.ofVanilla("core/blit_screen"), ShaderType.FRAGMENT) != null
         : false;
   }

   public static void method6455(@Nullable GpuTextureView var0, @Nullable Framebuffer var1) {
      if (var0 != null && var1 != null && var1.getColorAttachmentView() != null && method6454()) {
         try (RenderPass var2 = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith blit", var1.getColorAttachmentView(), OptionalInt.empty())) {
            var2.setPipeline(RenderPipelines.TRACY_BLIT);
            RenderSystem.bindDefaultUniforms(var2);
            var2.bindTexture("InSampler", var0, RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
            var2.draw(0, 3);
         }
      }
   }

   public static void method6456(@Nullable Framebuffer var0, int var1) {
      if (var0 != null && var0.getColorAttachment() != null) {
         if (var0.useDepthAttachment && var0.getDepthAttachment() != null) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(var0.getColorAttachment(), var1, var0.getDepthAttachment(), 1.0);
         } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(var0.getColorAttachment(), var1);
         }
      }
   }

   public static void method6457(@Nullable Framebuffer var0, int var1, int var2) {
      if (var0 != null) {
         var0.resize(Math.max(1, var1), Math.max(1, var2));
         method6456(var0, 0);
      }
   }
}

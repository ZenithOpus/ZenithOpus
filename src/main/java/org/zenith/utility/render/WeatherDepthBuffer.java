package org.zenith.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.jetbrains.annotations.Nullable;

public final class WeatherDepthBuffer {
   public static final int CONST_intValue = 8;
   private static SimpleFramebuffer simpleFramebuffer;
   private static boolean flag;

   private WeatherDepthBuffer() {
   }

   public static void method5130() {
      flag = false;
      Framebuffer var0 = MinecraftClient.getInstance().getFramebuffer();
      if (var0 != null && var0.useDepthAttachment) {
         int var1 = var0.textureWidth;
         int var2 = var0.textureHeight;
         if (var1 > 0 && var2 > 0) {
            if (simpleFramebuffer == null) {
               simpleFramebuffer = BlitUtils.method6448("zenith weather depth", var1, var2, true);
            } else if (simpleFramebuffer.textureWidth != var1 || simpleFramebuffer.textureHeight != var2) {
               simpleFramebuffer.resize(var1, var2);
            }

            GpuTexture var3 = var0.getDepthAttachment();
            GpuTexture var4 = simpleFramebuffer.getDepthAttachment();
            if (var3 != null && var4 != null) {
               RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(var3, var4, 0, 0, 0, 0, 0, var1, var2);
               flag = true;
            }
         }
      }
   }

   public static void method5131() {
   }

   @Nullable
   public static GpuTextureView method5132() {
      return flag && simpleFramebuffer != null ? simpleFramebuffer.getDepthAttachmentView() : null;
   }

   public static boolean method5133() {
      return flag && simpleFramebuffer != null;
   }
}

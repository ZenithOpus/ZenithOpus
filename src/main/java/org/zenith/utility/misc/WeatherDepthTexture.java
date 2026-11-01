package org.zenith.utility.misc;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.zenith.utility.render.WeatherDepthBuffer;

public final class WeatherDepthTexture extends AbstractTexture {
   public static final Identifier identifier = Identifier.of("zenith", "weather_terrain_depth");
   private static final Identifier identifier2 = identifier.withPath(var0 -> "textures/effect/" + var0 + ".png");
   private static final WeatherDepthTexture INSTANCE = new WeatherDepthTexture();
   private static boolean flag;

   private WeatherDepthTexture() {
   }

   public static void method1386() {
      if (!flag) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null && var0.getTextureManager() != null) {
            var0.getTextureManager().registerTexture(identifier2, INSTANCE);
            flag = true;
         }
      }
   }

   @Override
   public GpuTextureView getGlTextureView() {
      GpuTextureView var1 = WeatherDepthBuffer.method5132();
      return var1 != null ? var1 : MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
   }

   @Override
   public void close() {
   }
}

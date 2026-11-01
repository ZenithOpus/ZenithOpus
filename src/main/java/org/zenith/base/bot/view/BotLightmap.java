package org.zenith.base.bot.view;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.zenith.base.bot.world.BotWorld;

public final class BotLightmap implements AutoCloseable {
   private final NativeImage image = new NativeImage(16, 16, false);
   private final NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> "zenith", this.image);
   private boolean uploaded;

   public GpuTextureView getGlTextureView() {
      return this.texture.getGlTextureView();
   }

   public void update(BotWorld var1) {
      if (!this.uploaded) {
         this.uploaded = true;
         int var2 = ColorHelper.getArgb(255, 255, 255, 255);

         for (int var3 = 0; var3 < 16; var3++) {
            for (int var4 = 0; var4 < 16; var4++) {
               this.image.setColorArgb(var4, var3, var2);
            }
         }

         this.texture.upload();
      }
   }

   public static float getSkyBrightness(BotWorld var0) {
      float var1 = var0.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.SUN_ANGLE_VISUAL);
      float var2 = 1.0F - (MathHelper.cos(var1 * (float) (Math.PI * 2)) * 2.0F + 0.2F);
      var2 = MathHelper.clamp(var2, 0.0F, 1.0F);
      var2 = 1.0F - var2;
      var2 *= 1.0F - var0.getRainGradient(1.0F) * 5.0F / 16.0F;
      var2 *= 1.0F - var0.getThunderGradient(1.0F) * 5.0F / 16.0F;
      return var2 * 0.8F + 0.2F;
   }

   @Override
   public void close() {
      this.texture.close();
   }
}

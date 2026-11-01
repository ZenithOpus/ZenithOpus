package org.zenith.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.block.MapColor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public final class MapAtlasPage {
   public static final int CONST_intValue = 128;
   public static final int CONST_intValue2 = 2048;
   public static final int CONST_intValue3 = 16;
   public static final int CONST_intValue4 = 256;
   private static int intValue;
   private final Identifier identifier;
   private final NativeImageBackedTexture nativeImageBackedTexture;
   private int intValue2;
   private boolean closed;

   public MapAtlasPage(TextureManager var1) {
      int var2 = intValue++;
      this.identifier = Identifier.of("zenith", "map_atlas/" + var2);
      this.nativeImageBackedTexture = new NativeImageBackedTexture(() -> "Zenith map atlas #" + var2, 2048, 2048, true);
      var1.registerTexture(this.identifier, this.nativeImageBackedTexture);
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public boolean isFull() {
      return this.intValue2 >= 256;
   }

   public int method5416() {
      return this.intValue2 >= 256 ? -1 : this.intValue2++;
   }

   public void method5417(int var1, int var2, byte[] var3) {
      if (!this.closed) {
         NativeImage var4 = this.nativeImageBackedTexture.getImage();
         GpuTexture var5 = this.nativeImageBackedTexture.getGlTexture();
         if (var4 != null && var5 != null && !var5.isClosed()) {
            for (int var6 = 0; var6 < 128; var6++) {
               for (int var7 = 0; var7 < 128; var7++) {
                  var4.setColorArgb(var1 + var7, var2 + var6, MapColor.getRenderColor(var3[var7 + var6 * 128]));
               }
            }

            RenderSystem.getDevice().createCommandEncoder().writeToTexture(var5, var4, 0, 0, var1, var2, 128, 128, var1, var2);
         }
      }
   }

   public void method5418(TextureManager var1) {
      if (!this.closed) {
         this.closed = true;
         var1.destroyTexture(this.identifier);
      }
   }
}

package org.zenith.utility.mixin.optimization;

import net.minecraft.client.font.GlyphAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.zenith.utility.misc.GlyphShaderNames;

@Mixin({GlyphAtlasTexture.class})
public abstract class MixinOptGlyphAtlasTexture {
   @Unique
   private int zenith$atlasSize;

   @ModifyConstant(
      method = {"<init>(Ljava/util/function/Supplier;Lnet/minecraft/client/font/TextRenderLayerSet;Z)V"},
      constant = {@Constant(
         intValue = 256
      )}
   )
   private int zenith$atlasSize(int var1) {
      if (this.zenith$atlasSize == 0) {
         this.zenith$atlasSize = GlyphShaderNames.method1418();
      }

      return this.zenith$atlasSize;
   }

   @ModifyConstant(
      method = {"bake(Lnet/minecraft/client/font/GlyphMetrics;Lnet/minecraft/client/font/UploadableGlyph;)Lnet/minecraft/client/font/BakedGlyphImpl;"},
      constant = {@Constant(
         floatValue = 256.0F
      )}
   )
   private float zenith$atlasUvScale(float var1) {
      return this.zenith$atlasSize == 0 ? var1 : this.zenith$atlasSize;
   }
}

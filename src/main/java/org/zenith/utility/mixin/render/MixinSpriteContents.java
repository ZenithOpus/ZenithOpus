package org.zenith.utility.mixin.render;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.FoliageSprites;

@Mixin({SpriteContents.class})
public class MixinSpriteContents {
   @Shadow
   @Final
   private NativeImage image;

   @Inject(
      method = {"<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/SpriteDimensions;Lnet/minecraft/client/texture/NativeImage;Ljava/util/Optional;Ljava/util/List;Ljava/util/Optional;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$snowCap(Identifier var1, SpriteDimensions var2, NativeImage var3, Optional<?> var4, List<?> var5, Optional<?> var6, CallbackInfo var7) {
      if (FoliageSprites.method1431(var1)) {
         FoliageSprites.method1432(var1, this.image);
      }
   }
}

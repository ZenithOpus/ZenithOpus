package org.zenith.utility.mixin.optimization;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader.StitchResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.Util20;

@Mixin({SpriteAtlasTexture.class})
public class MixinOptSpriteAtlasTexture {
   @Inject(
      method = {"create(Lnet/minecraft/client/texture/SpriteLoader$StitchResult;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$onCreate(StitchResult var1, CallbackInfo var2) {
      Util20.method5135();
   }

   @Inject(
      method = {"clear()V"},
      at = {@At("RETURN")}
   )
   private void zenith$onClear(CallbackInfo var1) {
      Util20.method5135();
   }
}

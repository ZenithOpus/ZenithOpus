package org.zenith.utility.mixin.scripting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.game.Util4;

@Mixin({AbstractClientPlayerEntity.class})
public abstract class MixinScriptSkin {
   @Inject(
      method = {"getSkin()Lnet/minecraft/entity/player/SkinTextures;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void zenith$scriptSkin(CallbackInfoReturnable<SkinTextures> var1) {
      if (Util4.method1285()) {
         if ((Object)this == MinecraftClient.getInstance().player) {
            SkinTextures var2 = (SkinTextures)var1.getReturnValue();
            if (var2 != null) {
               var1.setReturnValue(Util4.method1286(var2));
            }
         }
      }
   }
}

package org.zenith.utility.mixin.scripting;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.game.Util4;

@Mixin({EntityRenderManager.class})
public abstract class MixinScriptEntityRender {
   @Inject(
      method = {"shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Frustum;DDD)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <E extends Entity> void zenith$scriptHidden(E var1, Frustum var2, double var3, double var5, double var7, CallbackInfoReturnable<Boolean> var9) {
      if (Util4.method1280(var1.getId())) {
         var9.setReturnValue(false);
      }
   }
}

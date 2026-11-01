package org.zenith.utility.mixin.render;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.WardrobeRenderer;

@Mixin({ElytraFeatureRenderer.class})
public class MixinElytraFeatureRenderer {
   @Inject(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideWingsUnderWardrobe(
      MatrixStack var1, OrderedRenderCommandQueue var2, int var3, BipedEntityRenderState var4, float var5, float var6, CallbackInfo var7
   ) {
      if (var4 instanceof PlayerEntityRenderState var8 && WardrobeRenderer.method6689(var8)) {
         var7.cancel();
      }
   }
}

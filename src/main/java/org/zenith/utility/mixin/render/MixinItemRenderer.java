package org.zenith.utility.mixin.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.HandRenderState;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
   @Inject(
      method = {"getItemGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void noItemGlintInHandFirePass(
      VertexConsumerProvider var0, RenderLayer var1, boolean var2, boolean var3, CallbackInfoReturnable<VertexConsumer> var4
   ) {
      if (HandRenderState.isActive()) {
         var4.setReturnValue(var0.getBuffer(var1));
      }
   }
}

package org.zenith.utility.mixin.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.WardrobeRenderer;

@Mixin({PlayerEntityRenderer.class})
public class MixinPlayerEntityRendererWardrobe {
   @Inject(
      method = {"renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;Lnet/minecraft/client/model/ModelPart;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skinArm(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, Identifier var4, ModelPart var5, boolean var6, CallbackInfo var7) {
      if (WardrobeRenderer.method6690((PlayerEntityRenderer)(Object)this, var1, var2, var3, var5, var6, true)) {
         var7.cancel();
      }
   }

   @Inject(
      method = {"renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;Lnet/minecraft/client/model/ModelPart;Z)V"},
      at = {@At("TAIL")}
   )
   private void zenith$clothingArm(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, Identifier var4, ModelPart var5, boolean var6, CallbackInfo var7) {
      WardrobeRenderer.method6690((PlayerEntityRenderer)(Object)this, var1, var2, var3, var5, var6, false);
   }
}

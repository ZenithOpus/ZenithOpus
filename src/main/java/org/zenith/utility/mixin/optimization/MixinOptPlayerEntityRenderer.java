package org.zenith.utility.mixin.optimization;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util29;

@Mixin({PlayerEntityRenderer.class})
public class MixinOptPlayerEntityRenderer {
   @Inject(
      method = {"updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V"},
      at = {@At("TAIL")}
   )
   private void zenith$farSkinLayers(PlayerLikeEntity var1, PlayerEntityRenderState var2, float var3, CallbackInfo var4) {
      if (OptimizationFlags.flag12 && Util29.flag && !(var2.squaredDistanceToCamera <= OptimizationFlags.doubleValue3)) {
         if (var1 != MinecraftClient.getInstance().player) {
            var2.hatVisible = false;
            var2.jacketVisible = false;
            var2.leftSleeveVisible = false;
            var2.rightSleeveVisible = false;
            var2.leftPantsLegVisible = false;
            var2.rightPantsLegVisible = false;
            if (!var2.equippedChestStack.contains(DataComponentTypes.GLIDER)) {
               var2.capeVisible = false;
            }
         }
      }
   }
}

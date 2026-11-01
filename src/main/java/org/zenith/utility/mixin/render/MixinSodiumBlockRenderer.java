package org.zenith.utility.mixin.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.game.NoRenderBlocks;
import org.zenith.utility.render.CrystalAnimation;

@Pseudo
@Mixin(
   targets = {"net/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer"},
   remap = false
)
public class MixinSodiumBlockRenderer {
   @Inject(
      method = {"renderModel"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void zenith$thinGrass(BlockStateModel var1, BlockState var2, BlockPos var3, BlockPos var4, CallbackInfo var5) {
      if (NoRenderBlocks.method5237(var2, var3) || CrystalAnimation.method4460(var2)) {
         var5.cancel();
      }
   }
}

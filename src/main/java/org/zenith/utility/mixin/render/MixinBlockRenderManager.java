package org.zenith.utility.mixin.render;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.game.NoRenderBlocks;
import org.zenith.utility.render.CrystalAnimation;

@Mixin({BlockRenderManager.class})
public class MixinBlockRenderManager {
   @Inject(
      method = {"renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLjava/util/List;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$thinGrass(
      BlockState var1, BlockPos var2, BlockRenderView var3, MatrixStack var4, VertexConsumer var5, boolean var6, List<?> var7, CallbackInfo var8
   ) {
      if (NoRenderBlocks.method5237(var1, var2) || CrystalAnimation.method4460(var1)) {
         var8.cancel();
      }
   }
}

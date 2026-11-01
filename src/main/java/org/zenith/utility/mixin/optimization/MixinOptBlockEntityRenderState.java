package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util20;

@Mixin({BlockEntityRenderState.class})
public class MixinOptBlockEntityRenderState {
   @WrapOperation(
      method = {"updateBlockEntityRenderState(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/render/block/entity/state/BlockEntityRenderState;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/WorldRenderer;getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"
      )}
   )
   private static int zenith$cachedLight(BlockRenderView var0, BlockPos var1, Operation<Integer> var2, BlockEntity var3) {
      if (!OptimizationFlags.flag7) {
         return (Integer)var2.call(new Object[]{var0, var1});
      } else {
         Util20.Entry var4 = Util20.method5139(var3);
         long var5 = Util20.longValue;
         if (var4.longValue3 != var5) {
            var4.intValue3 = (Integer)var2.call(new Object[]{var0, var1});
            var4.longValue3 = var5;
         }

         return var4.intValue3;
      }
   }
}

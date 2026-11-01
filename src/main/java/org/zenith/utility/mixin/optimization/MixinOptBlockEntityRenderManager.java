package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util20;

@Mixin({BlockEntityRenderManager.class})
public class MixinOptBlockEntityRenderManager {
   @WrapOperation(
      method = {"getRenderState(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)Lnet/minecraft/client/render/block/entity/state/BlockEntityRenderState;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/entity/BlockEntityType;supports(Lnet/minecraft/block/BlockState;)Z"
      )}
   )
   private boolean zenith$cachedSupports(BlockEntityType<?> var1, BlockState var2, Operation<Boolean> var3, BlockEntity var4) {
      if (!OptimizationFlags.flag7) {
         return (Boolean)var3.call(new Object[]{var1, var2});
      } else {
         Util20.Entry var5 = Util20.method5139(var4);
         if (var5.blockState2 != var2) {
            var5.flag = (Boolean)var3.call(new Object[]{var1, var2});
            var5.blockState2 = var2;
         }

         return var5.flag;
      }
   }

   @Inject(
      method = {"reload(Lnet/minecraft/resource/ResourceManager;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$onReload(ResourceManager var1, CallbackInfo var2) {
      Util20.method5135();
   }
}

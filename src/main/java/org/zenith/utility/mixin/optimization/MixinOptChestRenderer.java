package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties.PropertySource;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util20;

@Mixin({ChestBlockEntityRenderer.class})
public class MixinOptChestRenderer {
   @WrapOperation(
      method = {"updateRenderState(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/render/block/entity/state/ChestBlockEntityRenderState;FLnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/ChestBlock;getBlockEntitySource(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/block/DoubleBlockProperties$PropertySource;"
      )}
   )
   private PropertySource<? extends ChestBlockEntity> zenith$cachedSource(
      ChestBlock var1, BlockState var2, World var3, BlockPos var4, boolean var5, Operation<PropertySource<? extends ChestBlockEntity>> var6, BlockEntity var7
   ) {
      if (!OptimizationFlags.flag5) {
         return (PropertySource<? extends ChestBlockEntity>)var6.call(new Object[]{var1, var2, var3, var4, var5});
      } else {
         Util20.Entry var8 = Util20.method5139(var7);
         long var9 = Util20.longValue;
         if (var8.source == null || var8.longValue != var9 || var8.blockState != var2) {
            var8.source = var6.call(new Object[]{var1, var2, var3, var4, var5});
            var8.longValue = var9;
            var8.blockState = var2;
         }

         return (PropertySource<? extends ChestBlockEntity>)var8.source;
      }
   }

   @WrapOperation(
      method = {"updateRenderState(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/render/block/entity/state/ChestBlockEntityRenderState;FLnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lit/unimi/dsi/fastutil/ints/Int2IntFunction;applyAsInt(I)I"
      )}
   )
   private int zenith$cachedPairLight(Int2IntFunction var1, int var2, Operation<Integer> var3, BlockEntity var4) {
      if (!OptimizationFlags.flag5) {
         return (Integer)var3.call(new Object[]{var1, var2});
      } else {
         Util20.Entry var5 = Util20.method5139(var4);
         long var6 = Util20.longValue;
         if (var5.longValue2 != var6 || var5.intValue != var2) {
            var5.intValue2 = (Integer)var3.call(new Object[]{var1, var2});
            var5.longValue2 = var6;
            var5.intValue = var2;
         }

         return var5.intValue2;
      }
   }
}

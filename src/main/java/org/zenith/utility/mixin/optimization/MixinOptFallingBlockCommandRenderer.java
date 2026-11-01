package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.command.FallingBlockCommandRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util20;

@Mixin({FallingBlockCommandRenderer.class})
public class MixinOptFallingBlockCommandRenderer {
   @WrapOperation(
      method = {"render(Lnet/minecraft/client/render/command/BatchingRenderCommandQueue;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/block/BlockRenderManager;Lnet/minecraft/client/render/OutlineVertexConsumerProvider;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/block/BlockModelRenderer;render(Lnet/minecraft/world/BlockRenderView;Ljava/util/List;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZI)V"
      )}
   )
   private void zenith$flatMovingBlock(
      BlockModelRenderer var1,
      BlockRenderView var2,
      List<?> var3,
      BlockState var4,
      BlockPos var5,
      MatrixStack var6,
      VertexConsumer var7,
      boolean var8,
      int var9,
      Operation<Void> var10
   ) {
      if (!OptimizationFlags.flag10) {
         var10.call(new Object[]{var1, var2, var3, var4, var5, var6, var7, var8, var9});
      } else {
         Util20.method5136();

         try {
            var10.call(new Object[]{var1, var2, var3, var4, var5, var6, var7, var8, var9});
         } finally {
            Util20.method5137();
         }
      }
   }
}

package org.zenith.utility.mixin.optimization;

import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.OptimizationFlags;

@Mixin({OrderedRenderCommandQueueImpl.class})
public class MixinOptOrderedRenderCommandQueue {
   @Unique
   private int zenith$lastOrder;
   @Unique
   private BatchingRenderCommandQueue zenith$lastQueue;

   @Inject(
      method = {"getBatchingQueue(I)Lnet/minecraft/client/render/command/BatchingRenderCommandQueue;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$cachedQueue(int var1, CallbackInfoReturnable<BatchingRenderCommandQueue> var2) {
      BatchingRenderCommandQueue var3 = this.zenith$lastQueue;
      if (var3 != null && this.zenith$lastOrder == var1 && OptimizationFlags.flag16) {
         var2.setReturnValue(var3);
      }
   }

   @Inject(
      method = {"getBatchingQueue(I)Lnet/minecraft/client/render/command/BatchingRenderCommandQueue;"},
      at = {@At("RETURN")}
   )
   private void zenith$rememberQueue(int var1, CallbackInfoReturnable<BatchingRenderCommandQueue> var2) {
      this.zenith$lastOrder = var1;
      this.zenith$lastQueue = (BatchingRenderCommandQueue)var2.getReturnValue();
   }

   @Inject(
      method = {"onNextFrame()V"},
      at = {@At("HEAD")}
   )
   private void zenith$forgetQueue(CallbackInfo var1) {
      this.zenith$lastQueue = null;
   }
}

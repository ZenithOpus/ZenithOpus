package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;

@Mixin(
   targets = {"net/minecraft/client/font/TextRenderer$GlyphDrawer$1"}
)
public abstract class MixinOptGlyphDrawer {
   @Unique
   private RenderLayer zenith$lastLayer;
   @Unique
   private VertexConsumer zenith$lastConsumer;

   @WrapOperation(
      method = {"draw(Lnet/minecraft/client/font/TextDrawable;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"
      )}
   )
   private VertexConsumer zenith$fastTextLookup(VertexConsumerProvider var1, RenderLayer var2, Operation<VertexConsumer> var3) {
      if (!OptimizationFlags.flag19) {
         this.zenith$lastLayer = null;
         this.zenith$lastConsumer = null;
         return (VertexConsumer)var3.call(new Object[]{var1, var2});
      } else if (var2 == this.zenith$lastLayer && this.zenith$lastConsumer != null) {
         return this.zenith$lastConsumer;
      } else {
         VertexConsumer var4 = (VertexConsumer)var3.call(new Object[]{var1, var2});
         if (var2 != null && var2.areVerticesNotShared()) {
            this.zenith$lastLayer = var2;
            this.zenith$lastConsumer = var4;
         } else {
            this.zenith$lastLayer = null;
            this.zenith$lastConsumer = null;
         }

         return var4;
      }
   }
}

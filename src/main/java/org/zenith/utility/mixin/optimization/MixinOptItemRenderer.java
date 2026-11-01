package org.zenith.utility.mixin.optimization;

import java.util.List;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util29;
import org.zenith.utility.render.BakedQuadCache;

@Mixin({ItemRenderer.class})
public class MixinOptItemRenderer {
   @ModifyVariable(
      method = {"renderItem(Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILjava/util/List;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private static List<BakedQuad> zenith$farItemFaces(List<BakedQuad> var0, ItemDisplayContext var1, MatrixStack var2) {
      if (OptimizationFlags.flag13 && Util29.flag2 && var1 != ItemDisplayContext.GUI && !var1.isFirstPerson()) {
         Matrix4f var3 = var2.peek().getPositionMatrix();
         float var4 = var3.m30();
         float var5 = var3.m31();
         float var6 = var3.m32();
         return var4 * var4 + var5 * var5 + var6 * var6 <= OptimizationFlags.doubleValue4 ? var0 : BakedQuadCache.method5533(var0);
      } else {
         return var0;
      }
   }
}

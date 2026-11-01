package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.MapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.MapAtlas;
import org.zenith.utility.misc.Util30;

@Mixin({DrawContext.class})
public abstract class MixinOptDrawContextMap {
   @WrapOperation(
      method = {"drawMap(Lnet/minecraft/client/render/MapRenderState;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/DrawContext;drawTexturedQuad(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lnet/minecraft/client/gl/GpuSampler;IIIIFFFFI)V",
         ordinal = 0
      )}
   )
   private void zenith$atlasMapUv(
      DrawContext var1,
      RenderPipeline var2,
      GpuTextureView var3,
      GpuSampler var4,
      int var5,
      int var6,
      int var7,
      int var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13,
      Operation<Void> var14,
      @Local(argsOnly = true) MapRenderState var15
   ) {
      MapAtlas.Data var16 = ((Util30.Handler3)var15).method7747(var15.texture);
      if (var16 != null) {
         float var17 = 0.0625F;
         float var18 = var16.method6154();
         float var19 = var16.method6155();
         var9 = var18 + var9 * var17;
         var10 = var18 + var10 * var17;
         var11 = var19 + var11 * var17;
         var12 = var19 + var12 * var17;
      }

      var14.call(new Object[]{var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13});
   }
}

package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueue.Custom;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.MapAtlas;
import org.zenith.utility.misc.Util30;

@Mixin({MapRenderer.class})
public abstract class MixinOptMapRenderer {
   @Shadow
   @Final
   private MapTextureManager textureManager;

   @Inject(
      method = {"update(Lnet/minecraft/component/type/MapIdComponent;Lnet/minecraft/item/map/MapState;Lnet/minecraft/client/render/MapRenderState;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$captureAtlasSlot(MapIdComponent var1, MapState var2, MapRenderState var3, CallbackInfo var4) {
      MapAtlas.Data var5 = ((Util30.Handler)this.textureManager).zenith$getAtlasSlot(var1.id());
      ((Util30.Handler3)var3).zenith$setAtlasSlot(var5);
   }

   @WrapOperation(
      method = {"draw(Lnet/minecraft/client/render/MapRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ZI)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitCustom(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$Custom;)V",
         ordinal = 0
      )}
   )
   private void zenith$atlasMapQuad(
      OrderedRenderCommandQueue var1,
      MatrixStack var2,
      RenderLayer var3,
      Custom var4,
      Operation<Void> var5,
      @Local(argsOnly = true) MapRenderState var6,
      @Local(argsOnly = true) int var7
   ) {
      MapAtlas.Data var8 = ((Util30.Handler3)var6).method7747(var6.texture);
      if (var8 != null) {
         float var9 = var8.method6154();
         float var10 = var8.method6155();
         float var11 = var9 + 0.0625F;
         float var12 = var10 + 0.0625F;
         var4 = (var5x, var6x) -> {
            var6x.vertex(var5x, 0.0F, 128.0F, -0.01F).color(-1).texture(var9, var12).light(var7);
            var6x.vertex(var5x, 128.0F, 128.0F, -0.01F).color(-1).texture(var11, var12).light(var7);
            var6x.vertex(var5x, 128.0F, 0.0F, -0.01F).color(-1).texture(var11, var10).light(var7);
            var6x.vertex(var5x, 0.0F, 0.0F, -0.01F).color(-1).texture(var9, var10).light(var7);
         };
      }

      var5.call(new Object[]{var1, var2, var3, var4});
   }
}

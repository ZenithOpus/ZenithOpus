package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({InGameOverlayRenderer.class})
public class MixinGameOverlayRenderer {
   @Inject(
      method = {"renderOverlays(ZFLnet/minecraft/client/render/command/OrderedRenderCommandQueue;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void deferOverlaysForHandShader(boolean var1, float var2, OrderedRenderCommandQueue var3, CallbackInfo var4) {
      if (ShaderHandModule.INSTANCE.method1241()) {
         var4.cancel();
      }
   }

   @Inject(
      method = {"renderFireOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/texture/Sprite;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void removeFireOverlay(MatrixStack var0, VertexConsumerProvider var1, Sprite var2, CallbackInfo var3) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class125)) {
         var3.cancel();
      }
   }

   @Inject(
      method = {"renderInWallOverlay(Lnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void renderInWallOverlayHook(Sprite var0, MatrixStack var1, VertexConsumerProvider var2, CallbackInfo var3) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class126)) {
         var3.cancel();
      }
   }
}

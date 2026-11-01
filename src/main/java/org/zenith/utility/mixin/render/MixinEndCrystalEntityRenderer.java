package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.render.CrystalAnimation;

@Mixin({EndCrystalEntityRenderer.class})
public class MixinEndCrystalEntityRenderer {
   @WrapOperation(
      method = {"render(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
      )}
   )
   private void zenith$crystalSkin(
      OrderedRenderCommandQueue var1,
      Model<?> var2,
      Object var3,
      MatrixStack var4,
      RenderLayer var5,
      int var6,
      int var7,
      int var8,
      CrumblingOverlayCommand var9,
      Operation<Void> var10
   ) {
      if (!(var3 instanceof EndCrystalEntityRenderState var11 && CrystalAnimation.method4463(var11, var4, var1))) {
         var10.call(new Object[]{var1, var2, var3, var4, var5, var6, var7, var8, var9});
      }
   }
}

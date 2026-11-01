package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.render.HeldItemPoseController;

@Mixin({HeldItemFeatureRenderer.class})
public class MixinHeldItemFeatureRenderer {
   @WrapOperation(
      method = {"renderItem(Lnet/minecraft/client/render/entity/state/ArmedEntityRenderState;Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/ItemRenderState;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V"
      )}
   )
   private void zenith$weaponSkin(
      ItemRenderState var1,
      MatrixStack var2,
      OrderedRenderCommandQueue var3,
      int var4,
      int var5,
      int var6,
      Operation<Void> var7,
      @Local(argsOnly = true) ArmedEntityRenderState var8,
      @Local(argsOnly = true) ItemStack var9
   ) {
      if (!HeldItemPoseController.method6156(var8, var1, var9, var2, var3, var4)) {
         var7.call(new Object[]{var1, var2, var3, var4, var5, var6});
      }
   }
}

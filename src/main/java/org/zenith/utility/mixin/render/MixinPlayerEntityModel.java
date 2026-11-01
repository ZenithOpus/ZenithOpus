package org.zenith.utility.mixin.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.WardrobeRenderer;

@Mixin({PlayerEntityModel.class})
public class MixinPlayerEntityModel {
   @Unique
   private boolean zenith$hidden;

   @Inject(
      method = {"setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$characterSkin(PlayerEntityRenderState var1, CallbackInfo var2) {
      PlayerEntityModel var3 = (PlayerEntityModel)(Object)this;
      boolean var4 = WardrobeRenderer.method6687(var3, var1);
      if (var4) {
         zenith$visible(var3, false);
         this.zenith$hidden = true;
      } else if (this.zenith$hidden) {
         var3.head.visible = true;
         this.zenith$hidden = false;
      }
   }

   @Unique
   private static void zenith$visible(PlayerEntityModel var0, boolean var1) {
      for (ModelPart var5 : new ModelPart[]{var0.head, var0.body, var0.rightArm, var0.leftArm, var0.rightLeg, var0.leftLeg}) {
         var5.visible = var1;
      }
   }
}

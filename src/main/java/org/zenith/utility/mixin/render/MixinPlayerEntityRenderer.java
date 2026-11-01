package org.zenith.utility.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.modules.impl.movement.SpeedModule;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.misc.Handler4;
import org.zenith.utility.render.WardrobeFeatureRenderer;

@Mixin({PlayerEntityRenderer.class})
public class MixinPlayerEntityRenderer {
   @Inject(
      method = {"updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V"},
      at = {@At("RETURN")}
   )
   private void zenith$hideGrimElytraGlidingPose(PlayerLikeEntity var1, PlayerEntityRenderState var2, float var3, CallbackInfo var4) {
      if (var1 == MinecraftClient.getInstance().player
         && SpeedModule.INSTANCE.isEnabled()
         && SpeedModule.INSTANCE.method6606()
         && (var1.isGliding() || var1.getPose() == EntityPose.GLIDING)) {
         var2.isGliding = false;
         var2.glidingTicks = 0.0F;
         var2.leaningPitch = 0.0F;
         var2.pose = EntityPose.STANDING;
         var2.height = var1.getDimensions(EntityPose.STANDING).height();
         var2.width = var1.getDimensions(EntityPose.STANDING).width();
         var2.standingEyeHeight = var1.getEyeHeight(EntityPose.STANDING);
         var2.isInSneakingPose = false;
         var2.isSwimming = false;
         var2.applyFlyingRotation = false;
         var2.flyingRotation = 0.0F;
      }
   }

   @Inject(
      method = {"renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void render(PlayerEntityRenderState var1, MatrixStack var2, OrderedRenderCommandQueue var3, CameraRenderState var4, CallbackInfo var5) {
      if (EntityESPModule.INSTANCE.method568()) {
         var5.cancel();
      }

      if (PetManager.method4557(var1.id)) {
         var5.cancel();
      }
   }

   @Inject(
      method = {"updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V"},
      at = {@At("TAIL")}
   )
   private void zenith$hideCosmeticParts(PlayerLikeEntity var1, PlayerEntityRenderState var2, float var3, CallbackInfo var4) {
      ((Handler4)var2).zenith$setProfileName(var1 instanceof PlayerEntity var5 ? var5.getGameProfile().name() : null);
      WardrobeFeatureRenderer.method4446(var2);
      var2.displayName = StreamerModeModule.INSTANCE.method6293(var2.displayName);
      var2.playerName = StreamerModeModule.INSTANCE.method6293(var2.playerName);
   }
}

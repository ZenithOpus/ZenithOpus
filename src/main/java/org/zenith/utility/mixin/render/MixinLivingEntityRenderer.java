package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.zenith.MinecraftAccess;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.base.figura.ducks.LivingEntityRendererAccessor;
import org.zenith.client.events.LivingRenderColorEvent;
import org.zenith.client.modules.impl.movement.SpeedModule;
import org.zenith.client.modules.impl.render.ChamsModule;
import org.zenith.utility.render.GlowUtil;

@Mixin({LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
   implements MinecraftAccess {
   @ModifyExpressionValue(
      method = {"updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;clampBodyYaw(Lnet/minecraft/entity/LivingEntity;FF)F"
      )}
   )
   public float changeYaw(float var1, LivingEntity var2) {
      RotationManager var3 = Zenith.getInstance().getRotationManager();
      return !var2.equals(mc.player) || var3.method7845() || SpeedModule.INSTANCE.isEnabled() && SpeedModule.INSTANCE.method6606()
         ? var1
         : MathHelper.lerpAngleDegrees(GlowUtil.getTickDelta(), var3.getRotation().getFloatValue(), var3.getCurrentRotation().getFloatValue());
   }

   @ModifyExpressionValue(
      method = {"updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F"
      )}
   )
   public float changeHeadYaw(float var1, LivingEntity var2) {
      RotationManager var3 = Zenith.getInstance().getRotationManager();
      return !var2.equals(mc.player) || var3.method7845() || SpeedModule.INSTANCE.isEnabled() && SpeedModule.INSTANCE.method6606()
         ? var1
         : MathHelper.lerpAngleDegrees(GlowUtil.getTickDelta(), var3.getRotation().getFloatValue(), var3.getCurrentRotation().getFloatValue());
   }

   @ModifyExpressionValue(
      method = {"updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getLerpedPitch(F)F"
      )}
   )
   public float changePitch(float var1, LivingEntity var2) {
      RotationManager var3 = Zenith.getInstance().getRotationManager();
      return !var2.equals(mc.player) || var3.method7845() || SpeedModule.INSTANCE.isEnabled() && SpeedModule.INSTANCE.method6606()
         ? var1
         : MathHelper.lerpAngleDegrees(GlowUtil.getTickDelta(), var3.getRotation().getFloatValue2(), var3.getCurrentRotation().getFloatValue2());
   }

   @Shadow
   @Nullable
   public abstract RenderLayer getRenderLayer(LivingEntityRenderState var1, boolean var2, boolean var3, boolean var4);

   @Shadow
   public abstract Identifier getTexture(S var1);

   @Redirect(
      method = {"render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/render/RenderLayer;"
      )
   )
   private RenderLayer renderHook(LivingEntityRenderer var1, LivingEntityRenderState var2, boolean var3, boolean var4, boolean var5) {
      ChamsModule var6 = ChamsModule.INSTANCE;
      if (var6.method5600(var2)) {
         var6.method5599();
         if (var6.method5602()) {
            RenderLayer var8 = var6.method5601(this.getTexture((S)var2));
            if (var8 != null) {
               return var8;
            }
         }

         return this.getRenderLayer(var2, var3, true, var5);
      } else {
         if (!var4 && !var3 && var2.invisibleToPlayer && var2.width == 0.6F && EventManager.hasListeners(LivingRenderColorEvent.class)) {
            LivingRenderColorEvent var7 = new LivingRenderColorEvent(-1);
            EventManager.call(var7);
            if (var7.isCancelled()) {
               var4 = true;
            }
         }

         return this.getRenderLayer(var2, var3, var4, var5);
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
      )
   )
   private void renderModelHook(
      OrderedRenderCommandQueue var1,
      Model var2,
      Object var3,
      MatrixStack var4,
      RenderLayer var5,
      int var6,
      int var7,
      int var8,
      Sprite var9,
      int var10,
      CrumblingOverlayCommand var11,
      @Local(ordinal = 0,argsOnly = true) LivingEntityRenderState var12
   ) {
      var7 = LivingEntityRendererAccessor.overrideOverlay.orElse(var7);
      ChamsModule var13 = ChamsModule.INSTANCE;
      if (var13.method5600(var12)) {
         var8 = var13.method5602() ? var13.method5603(var8) : var13.method5604(var8);
      } else if (var12.invisibleToPlayer && EventManager.hasListeners(LivingRenderColorEvent.class)) {
         LivingRenderColorEvent var14 = new LivingRenderColorEvent(var8);
         EventManager.call(var14);
         var8 = var14.getColor();
      }

      var1.submitModel(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }
}

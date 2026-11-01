package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.RenderState;
import org.zenith.client.events.HeldItemRenderEvent;
import org.zenith.client.modules.impl.render.HandFireModule;
import org.zenith.client.modules.impl.render.SwingAnimationModule;
import org.zenith.client.modules.impl.render.ViewModelModule;
import org.zenith.utility.render.HeldItemAnimator;

@Mixin({HeldItemRenderer.class})
public abstract class MixinHeldItemRenderer {
   @Shadow
   public abstract void swingArm(float var1, MatrixStack var2, int var3, Arm var4);

   @Inject(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = {@At("HEAD")}
   )
   private void handFireBeginFrame(float var1, MatrixStack var2, OrderedRenderCommandQueue var3, ClientPlayerEntity var4, int var5, CallbackInfo var6) {
      HandFireModule var7 = HandFireModule.INSTANCE;
      if (var7.method6612()) {
         var7.method6617();
      }
   }

   @Inject(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = {@At("TAIL")}
   )
   private void handFireEndFrame(float var1, MatrixStack var2, OrderedRenderCommandQueue var3, ClientPlayerEntity var4, int var5, CallbackInfo var6) {
      HandFireModule var7 = HandFireModule.INSTANCE;
      if (var7.method6612()) {
         var7.method6618();
      }
   }

   @Inject(
      method = {"renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
         ordinal = 0
      )}
   )
   public void injectBeforeRenderCrossBowItem(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModelModule var12 = ViewModelModule.INSTANCE;
      if (var12.isEnabled()) {
         boolean var13 = var4 == Hand.MAIN_HAND;
         Arm var14 = var13 ? var1.getMainArm() : var1.getMainArm().getOpposite();
         var12.method235(var8, var14);
      }
   }

   @Inject(
      method = {"renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
         ordinal = 1
      )}
   )
   public void injectBeforeRenderItem(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModelModule var12 = ViewModelModule.INSTANCE;
      if (var12.isEnabled()) {
         boolean var13 = var4 == Hand.MAIN_HAND;
         Arm var14 = var13 ? var1.getMainArm() : var1.getMainArm().getOpposite();
         var12.method235(var8, var14);
      }
   }

   @Inject(
      method = {"renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
         shift = Shift.AFTER,
         ordinal = 0
      )}
   )
   public void injectAfterMatrixPushHandPosition(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModelModule var12 = ViewModelModule.INSTANCE;
      if (var12.isEnabled() && !var6.isEmpty() && !var6.contains(DataComponentTypes.MAP_ID)) {
         boolean var13 = var4 == Hand.MAIN_HAND;
         Arm var14 = var13 ? var1.getMainArm() : var1.getMainArm().getOpposite();
         var12.method236(var8, var14);
      }
   }

   @Redirect(
      method = {"renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;swingArm(FLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V",
         ordinal = 2
      )
   )
   public void redirectSwingArmForCustomAnim(
      HeldItemRenderer var1, float var2, MatrixStack var3, int var4, Arm var5, @Local(argsOnly = true,ordinal = 3) float var6
   ) {
      SwingAnimationModule var7 = SwingAnimationModule.INSTANCE;
      ClientPlayerEntity var8 = MinecraftClient.getInstance().player;
      if (var7.active() && var8 != null && var5 == var8.getMainArm()) {
         var7.method3270(var3, var2, var6, var5);
      } else {
         this.swingArm(var2, var3, var4, var5);
      }
   }

   @WrapOperation(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"
      )}
   )
   private void itemRenderHook(
      HeldItemRenderer var1,
      AbstractClientPlayerEntity var2,
      float var3,
      float var4,
      Hand var5,
      float var6,
      ItemStack var7,
      float var8,
      MatrixStack var9,
      OrderedRenderCommandQueue var10,
      int var11,
      Operation<Void> var12
   ) {
      HeldItemRenderEvent var13 = new HeldItemRenderEvent(var2, var7, var5);
      EventManager.call(var13);
      HandFireModule var14 = HandFireModule.INSTANCE;
      if (var14.method6612()) {
         var14.method6619(
            var13.getAbstractClientPlayerEntity(),
            var3,
            var4,
            var13.getHand(),
            var6,
            var13.getItemStack(),
            var8,
            var9,
            var10,
            var11,
            var9x -> {
               if (!HeldItemAnimator.method4775(var1, var13.getAbstractClientPlayerEntity(), var13.getHand(), var9, var9x, var11)) {
                  var12.call(
                     new Object[]{
                        var1, var13.getAbstractClientPlayerEntity(), var3, var4, var13.getHand(), var6, var13.getItemStack(), var8, var9, var9x, var11
                     }
                  );
               }
            }
         );
      } else if (!HeldItemAnimator.method4775(var1, var13.getAbstractClientPlayerEntity(), var13.getHand(), var9, var10, var11)) {
         var12.call(
            new Object[]{var1, var13.getAbstractClientPlayerEntity(), var3, var4, var13.getHand(), var6, var13.getItemStack(), var8, var9, var10, var11}
         );
      }
   }

   @WrapOperation(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V"
      )}
   )
   private void handFireVanillaFlush(Immediate var1, Operation<Void> var2) {
      float var3 = HandFireModule.method6615();
      if (!(var3 <= 0.0F)) {
         if (var3 >= 1.0F) {
            var2.call(new Object[]{var1});
         } else {
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1883(1.0F, 1.0F, 1.0F, var3);

            try {
               var2.call(new Object[]{var1});
            } finally {
               RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
               RenderState.method1873();
               RenderState.method1872();
            }
         }
      }
   }
}

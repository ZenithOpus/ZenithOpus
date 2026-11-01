package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Zenith;
import org.zenith.client.events.BlockBreakingEvent;
import org.zenith.client.events.ClickSlotEvent;
import org.zenith.client.events.InteractBlockEvent;
import org.zenith.client.events.InteractItemEvent;

@Mixin({ClientPlayerInteractionManager.class})
public class MixinClientPlayerInteractionManager {
   @Unique
   public float safeYaw;
   @Unique
   public float safePitch;

   @Inject(
      method = {"clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void clickSlotHook(int var1, int var2, int var3, SlotActionType var4, PlayerEntity var5, CallbackInfo var6) {
      ClickSlotEvent var7 = new ClickSlotEvent(var1, var2, var3, var4);
      EventManager.call(var7);
      if (var7.isCancelled()) {
         var6.cancel();
      }
   }

   @Inject(
      method = {"updateBlockBreakingProgress(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectBlockBreaking(BlockPos var1, Direction var2, CallbackInfoReturnable<Boolean> var3) {
      BlockBreakingEvent var4 = new BlockBreakingEvent(var1, var2);
      EventManager.call(var4);
      if (var4.isCancelled()) {
         var3.setReturnValue(false);
      }
   }

   @Inject(
      method = {"interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"},
      at = {@At("HEAD")}
   )
   private void interact(PlayerEntity var1, Hand var2, CallbackInfoReturnable<ActionResult> var3) {
      if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null && MinecraftClient.getInstance().player != null) {
         this.safeYaw = MinecraftClient.getInstance().player.getYaw();
         this.safePitch = MinecraftClient.getInstance().player.getPitch();
         MinecraftClient.getInstance().player.setYaw(Zenith.getInstance().getRotationManager().getCurrentRotation().getFloatValue());
         MinecraftClient.getInstance().player.setPitch(Zenith.getInstance().getRotationManager().getCurrentRotation().getFloatValue2());
      }

      EventManager.call(new InteractItemEvent());
   }

   @Inject(
      method = {"interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"},
      at = {@At("RETURN")}
   )
   private void interact2(PlayerEntity var1, Hand var2, CallbackInfoReturnable<ActionResult> var3) {
      if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null && MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance().player.setYaw(this.safeYaw);
         MinecraftClient.getInstance().player.setPitch(this.safePitch);
      }
   }

   @Inject(
      method = {"interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void interactBlock(ClientPlayerEntity var1, Hand var2, BlockHitResult var3, CallbackInfoReturnable<ActionResult> var4) {
      InteractBlockEvent var5 = new InteractBlockEvent();
      EventManager.call(var5);
      if (var5.isCancelled()) {
         var4.setReturnValue(ActionResult.FAIL);
      }
   }
}

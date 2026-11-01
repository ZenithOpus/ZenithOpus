package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.events.Event;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.client.events.CloseHandledScreenEvent;
import org.zenith.client.events.EntityPushEvent;
import org.zenith.client.events.NoSlowEvent;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.SprintPacketEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.misc.NoFriendDamageModule;
import org.zenith.client.modules.impl.misc.ViaFabricFixModule;

@Mixin({ClientPlayerEntity.class})
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
   @Shadow
   public Input input;
   @Shadow
   @Final
   public MinecraftClient client;
   @Unique
   private float zenith$savedYaw;
   @Unique
   private float zenith$savedPitch;
   @Unique
   private boolean zenith$rotationPushed;

   public MixinClientPlayerEntity(ClientWorld var1, GameProfile var2) {
      super(var1, var2);
   }

   @Shadow
   public abstract void sendSprintingPacket();

   @Shadow
   public abstract void autoJump(float var1, float var2);

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   public void tick(CallbackInfo var1) {
      dispatchEvent(new UpdateEvent());
   }

   @Inject(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
         shift = Shift.AFTER
      )}
   )
   public void tickEnd(CallbackInfo var1) {
      dispatchEvent(new PlayerTickEvent());
   }

   @Inject(
      method = {"sendMovementPackets()V"},
      at = {@At("RETURN")}
   )
   public void motion(CallbackInfo var1) {
      dispatchEvent(new PostMotionEvent());
   }

   @Redirect(
      method = {"sendMovementPackets()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"
      )
   )
   public void invokeSprintUpdate(ClientPlayerEntity var1) {
      SprintPacketEvent var2 = new SprintPacketEvent();
      dispatchEvent(var2);
      if (!var2.isCancelled()) {
         this.sendSprintingPacket();
      }
   }

   @Inject(
      method = {"canSprint(Z)Z"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void zenith$canSprint(boolean var1, CallbackInfoReturnable<Boolean> var2) {
      SprintUpdateEvent var3 = new SprintUpdateEvent((Boolean)var2.getReturnValue());
      dispatchEvent(var3);
      var2.setReturnValue(var3.isCancelled() || var3.isFlag2());
   }

   @ModifyExpressionValue(
      method = {"shouldStopSprinting()Z"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;collidedSoftly:Z"
      )}
   )
   private boolean zenith$legacySoftCollision(boolean var1) {
      return ViaFabricFixModule.INSTANCE.isEnabled() ? false : var1;
   }

   @Inject(
      method = {"pushOutOfBlocks(DD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pushOutOfBlocks(double var1, double var3, CallbackInfo var5) {
      EntityPushEvent var6 = new EntityPushEvent(EntityPushEvent.Type.BLOCK);
      dispatchEvent(var6);
      if (var6.isCancelled()) {
         var5.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets()V", "tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"
      )}
   )
   private float hookSilentRotationYaw(float var1) {
      Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return var2 == null ? var1 : var2.getFloatValue();
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets()V", "tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"
      )}
   )
   private float hookSilentRotationPitch(float var1) {
      Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return var2 == null ? var1 : var2.getFloatValue2();
   }

   @Redirect(
      method = {"applyMovementSpeedFactors(Lnet/minecraft/util/math/Vec2f;)Lnet/minecraft/util/math/Vec2f;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
      ),
      require = 0
   )
   private boolean onIsUsingItemRedirect(ClientPlayerEntity var1) {
      if (var1.isUsingItem()) {
         NoSlowEvent var2 = new NoSlowEvent();
         dispatchEvent(var2);
         return var1.isUsingItem() && var1.getVehicle() == null && !var2.isCancelled();
      } else {
         return var1.isUsingItem() && var1.getVehicle() == null;
      }
   }

   @Inject(
      method = {"closeHandledScreen()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void closeHandledScreenHook(CallbackInfo var1) {
      CloseHandledScreenEvent var2 = new CloseHandledScreenEvent(this.client.currentScreen);
      dispatchEvent(var2);
      if (var2.isCancelled()) {
         var1.cancel();
      }
   }

   @Inject(
      method = {"move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
      )},
      cancellable = true
   )
   public void onMoveHook(MovementType var1, Vec3d var2, CallbackInfo var3) {
      PlayerMoveEvent var4 = new PlayerMoveEvent(var2);
      dispatchEvent(var4);
      double var5 = this.getX();
      double var7 = this.getZ();
      super.move(var1, var4.getVec3d());
      float var9 = (float)(this.getX() - var5);
      float var10 = (float)(this.getZ() - var7);
      this.autoJump(var9, var10);
      this.addDistanceMoved(MathHelper.hypot(var9, var10) * 0.6F);
      var3.cancel();
   }

   private static void dispatchEvent(Event var0) {
      EventManager.call(var0);
   }

   @Inject(
      method = {"getCrosshairTarget(FLnet/minecraft/entity/Entity;)Lnet/minecraft/util/hit/HitResult;"},
      at = {@At("HEAD")}
   )
   private void zenith$pushCrosshairRotation(float var1, Entity var2, CallbackInfoReturnable<HitResult> var3) {
      Rotation var4 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      if (var4 != null && var2 == MinecraftClient.getInstance().player) {
         this.zenith$savedYaw = var2.getYaw();
         this.zenith$savedPitch = var2.getPitch();
         this.zenith$rotationPushed = true;
         var2.setYaw(var4.getFloatValue());
         var2.setPitch(var4.getFloatValue2());
      }
   }

   @Inject(
      method = {"getCrosshairTarget(FLnet/minecraft/entity/Entity;)Lnet/minecraft/util/hit/HitResult;"},
      at = {@At("RETURN")}
   )
   private void zenith$popCrosshairRotation(float var1, Entity var2, CallbackInfoReturnable<HitResult> var3) {
      if (this.zenith$rotationPushed) {
         this.zenith$rotationPushed = false;
         var2.setYaw(this.zenith$savedYaw);
         var2.setPitch(this.zenith$savedPitch);
      }
   }

   @ModifyExpressionValue(
      method = {"getCrosshairTarget(Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"
      )}
   )
   private static Vec3d zenith$crosshairRotationVector(Vec3d var0, Entity var1, double var2, double var4, float var6) {
      if (var1 != MinecraftClient.getInstance().player) {
         return var0;
      } else {
         Rotation var7 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var7 != null ? var7.method3135() : var0;
      }
   }

   @ModifyExpressionValue(
      method = {"getCrosshairTarget(Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/predicate/entity/EntityPredicates;CAN_HIT:Ljava/util/function/Predicate;"
      )}
   )
   private static Predicate<Entity> zenith$noFriendDamage(Predicate<Entity> var0) {
      return !NoFriendDamageModule.INSTANCE.isEnabled() ? var0 : var1 -> var0.test(var1) && !Zenith.getInstance().getFriends().method7626(var1);
   }
}

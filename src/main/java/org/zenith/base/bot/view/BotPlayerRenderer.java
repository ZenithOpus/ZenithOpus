package org.zenith.base.bot.view;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.utility.misc.Handler4;
import org.zenith.utility.mixin.accessors.EntityRenderDispatcherAccessor;
import org.zenith.utility.render.OutlineVertexProvider;

public final class BotPlayerRenderer {
   private final PlayerEntityRenderState state = new PlayerEntityRenderState();

   @Nullable
   public static PlayerEntityRenderer rendererFor(PlayerSkinType var0) {
      EntityRenderDispatcherAccessor var1 = (EntityRenderDispatcherAccessor)(Object)MinecraftClient.getInstance().getEntityRenderDispatcher();
      EntityRenderer var2 = var1.zenith$getModelRenderers().get(var0);
      if (var2 == null) {
         var2 = var1.zenith$getModelRenderers().get(PlayerSkinType.WIDE);
      }

      return var2 instanceof PlayerEntityRenderer var3 ? var3 : null;
   }

   public void render(
      PlayerEntity var1,
      @Nullable BotPlayHandler var2,
      double var3,
      double var5,
      double var7,
      float var9,
      MatrixStack var10,
      VertexConsumerProvider var11,
      int var12,
      boolean var13
   ) {
      SkinTextures var14 = resolveSkin(var1, var2);
      PlayerEntityRenderer var15 = rendererFor(var14.model());
      if (var15 != null) {
         this.fillState(var1, var14, var9, var3, var5, var7, var13);
         double var16 = MathHelper.lerp((double)var9, var1.lastRenderX, var1.getX());
         double var18 = MathHelper.lerp((double)var9, var1.lastRenderY, var1.getY());
         double var20 = MathHelper.lerp((double)var9, var1.lastRenderZ, var1.getZ());
         var10.push();
         var10.translate(var16 - var3, var18 - var5, var20 - var7);
         this.state.light = var12;
         if (var11 instanceof Immediate var22) {
            OutlineVertexProvider.method1545(var15, this.state, var10, var22, OutlineVertexProvider.method1541(new Vec3d(var3, var5, var7)));
         }

         var10.pop();
      }
   }

   public static SkinTextures resolveSkin(PlayerEntity var0, @Nullable BotPlayHandler var1) {
      if (var1 != null) {
         PlayerListEntry var2 = var1.getPlayerListEntry(var0.getUuid());
         if (var2 != null) {
            try {
               return var2.getSkinTextures();
            } catch (Exception var4) {
            }
         }
      }

      return DefaultSkinHelper.getSkinTextures(var0.getUuid());
   }

   private void fillState(PlayerEntity var1, SkinTextures var2, float var3, double var4, double var6, double var8, boolean var10) {
      this.state.x = MathHelper.lerp((double)var3, var1.lastRenderX, var1.getX());
      this.state.y = MathHelper.lerp((double)var3, var1.lastRenderY, var1.getY());
      this.state.z = MathHelper.lerp((double)var3, var1.lastRenderZ, var1.getZ());
      this.state.invisible = var1.isInvisible();
      this.state.age = var1.age + var3;
      this.state.width = var1.getWidth();
      this.state.height = var1.getHeight();
      this.state.standingEyeHeight = var1.getStandingEyeHeight();
      this.state.positionOffset = null;
      this.state.squaredDistanceToCamera = MathHelper.squaredMagnitude(this.state.x - var4, this.state.y - var6, this.state.z - var8);
      this.state.sneaking = var1.isSneaky();
      this.state.onFire = var1.doesRenderOnFire();
      this.state.leashDatas = List.of();
      if (var10 && this.state.squaredDistanceToCamera < 4096.0) {
         this.state.displayName = var1.getDisplayName();
         this.state.nameLabelPos = var1.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, var1.getLerpedYaw(var3));
      } else {
         this.state.displayName = null;
         this.state.nameLabelPos = null;
      }

      float var11 = MathHelper.lerpAngleDegrees(var3, var1.lastHeadYaw, var1.headYaw);
      this.state.bodyYaw = MathHelper.lerpAngleDegrees(var3, var1.lastBodyYaw, var1.bodyYaw);
      this.state.relativeHeadYaw = MathHelper.wrapDegrees(var11 - this.state.bodyYaw);
      this.state.pitch = var1.getLerpedPitch(var3);
      this.state.flipUpsideDown = false;
      if (!var1.hasVehicle() && var1.isAlive()) {
         this.state.limbSwingAnimationProgress = var1.limbAnimator.getAnimationProgress(var3);
         this.state.limbSwingAmplitude = var1.limbAnimator.getAmplitude(var3);
      } else {
         this.state.limbSwingAnimationProgress = 0.0F;
         this.state.limbSwingAmplitude = 0.0F;
      }

      this.state.headItemAnimationProgress = this.state.limbSwingAnimationProgress;
      this.state.baseScale = var1.getScale();
      this.state.ageScale = var1.getScaleFactor();
      this.state.pose = var1.getPose();
      this.state.sleepingDirection = var1.getSleepingDirection();
      if (this.state.sleepingDirection != null) {
         this.state.standingEyeHeight = var1.getEyeHeight(EntityPose.STANDING);
      }

      this.state.shaking = var1.isFrozen();
      this.state.baby = var1.isBaby();
      this.state.touchingWater = var1.isTouchingWater();
      this.state.usingRiptide = var1.isUsingRiptide();
      this.state.hurt = var1.hurtTime > 0 || var1.deathTime > 0;
      this.state.wearingSkullType = null;
      this.state.wearingSkullProfile = null;
      this.state.headItemRenderState.clear();
      this.state.deathTime = var1.deathTime > 0 ? var1.deathTime + var3 : 0.0F;
      this.state.invisibleToPlayer = false;
      this.state.outlineColor = 0;
      BipedEntityRenderer.updateBipedRenderState(var1, this.state, var3, MinecraftClient.getInstance().getItemModelManager());
      this.state.leftArmPose = getArmPose(var1, Arm.LEFT);
      this.state.rightArmPose = getArmPose(var1, Arm.RIGHT);
      this.state.skinTextures = var2;
      this.state.stuckArrowCount = var1.getStuckArrowCount();
      this.state.stingerCount = var1.getStingerCount();
      this.state.spectator = var1.isSpectator();
      this.state.hatVisible = var1.isModelPartVisible(PlayerModelPart.HAT);
      this.state.jacketVisible = var1.isModelPartVisible(PlayerModelPart.JACKET);
      this.state.leftPantsLegVisible = var1.isModelPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
      this.state.rightPantsLegVisible = var1.isModelPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
      this.state.leftSleeveVisible = var1.isModelPartVisible(PlayerModelPart.LEFT_SLEEVE);
      this.state.rightSleeveVisible = var1.isModelPartVisible(PlayerModelPart.RIGHT_SLEEVE);
      this.state.capeVisible = false;
      this.state.glidingTicks = 0.0F;
      this.state.applyFlyingRotation = false;
      this.state.flyingRotation = 0.0F;
      this.state.playerName = null;
      this.state.leftShoulderParrotVariant = null;
      this.state.rightShoulderParrotVariant = null;
      this.state.id = var1.getId();
      ((Handler4)this.state).zenith$setProfileName(var1.getGameProfile().name());
      this.state.spyglassState.clear();
   }

   private static ArmPose getArmPose(PlayerEntity var0, Arm var1) {
      ArmPose var2 = getArmPose(var0, var0.getStackInHand(Hand.MAIN_HAND), Hand.MAIN_HAND);
      ArmPose var3 = getArmPose(var0, var0.getStackInHand(Hand.OFF_HAND), Hand.OFF_HAND);
      if (var2.isTwoHanded()) {
         var3 = var0.getStackInHand(Hand.OFF_HAND).isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
      }

      return var0.getMainArm() == var1 ? var2 : var3;
   }

   private static ArmPose getArmPose(PlayerEntity var0, ItemStack var1, Hand var2) {
      if (var1.isEmpty()) {
         return ArmPose.EMPTY;
      } else {
         if (var0.getActiveHand() == var2 && var0.getItemUseTimeLeft() > 0) {
            UseAction var3 = var1.getUseAction();
            switch (var3) {
               case BLOCK:
                  return ArmPose.BLOCK;
               case BOW:
                  return ArmPose.BOW_AND_ARROW;
               case TRIDENT:
                  return ArmPose.THROW_TRIDENT;
               case SPEAR:
                  return ArmPose.SPEAR;
               case CROSSBOW:
                  return ArmPose.CROSSBOW_CHARGE;
               case SPYGLASS:
                  return ArmPose.SPYGLASS;
               case TOOT_HORN:
                  return ArmPose.TOOT_HORN;
               case BRUSH:
                  return ArmPose.BRUSH;
            }
         } else if (!var0.handSwinging && var1.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(var1)) {
            return ArmPose.CROSSBOW_HOLD;
         }

         return ArmPose.ITEM;
      }
   }
}

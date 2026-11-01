package org.zenith.base.bot.view;

import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.zenith.MinecraftAccess;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;

public final class BotHeldItemRenderer implements MinecraftAccess {
   private static final RenderLayer MAP_BACKGROUND = RenderLayers.text(Identifier.ofVanilla("textures/map/map_background.png"));
   private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayers.text(Identifier.ofVanilla("textures/map/map_background_checkerboard.png"));
   private final MapRenderState mapRenderState = new MapRenderState();
   private ItemStack mainHand = ItemStack.EMPTY;
   private ItemStack offHand = ItemStack.EMPTY;
   private float equipProgressMainHand;
   private float prevEquipProgressMainHand;
   private float equipProgressOffHand;
   private float prevEquipProgressOffHand;

   public void tick(BotPlayer var1) {
      this.prevEquipProgressMainHand = this.equipProgressMainHand;
      this.prevEquipProgressOffHand = this.equipProgressOffHand;
      ItemStack var2 = var1.getMainHandStack();
      ItemStack var3 = var1.getOffHandStack();
      if (this.shouldSkipHandAnimationOnSwap(this.mainHand, var2)) {
         this.mainHand = var2;
      }

      if (this.shouldSkipHandAnimationOnSwap(this.offHand, var3)) {
         this.offHand = var3;
      }

      if (var1.isRiding()) {
         this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
         this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
      } else {
         float var4 = var1.getAttackCooldownProgress(1.0F);
         float var5 = this.mainHand != var2 ? 0.0F : var4 * var4 * var4;
         float var6 = this.offHand != var3 ? 0.0F : 1.0F;
         this.equipProgressMainHand = this.equipProgressMainHand + MathHelper.clamp(var5 - this.equipProgressMainHand, -0.4F, 0.4F);
         this.equipProgressOffHand = this.equipProgressOffHand + MathHelper.clamp(var6 - this.equipProgressOffHand, -0.4F, 0.4F);
      }

      if (this.equipProgressMainHand < 0.1F) {
         this.mainHand = var2;
      }

      if (this.equipProgressOffHand < 0.1F) {
         this.offHand = var3;
      }
   }

   public void snap(BotPlayer var1) {
      this.mainHand = var1.getMainHandStack();
      this.offHand = var1.getOffHandStack();
      this.equipProgressMainHand = this.prevEquipProgressMainHand = 1.0F;
      this.equipProgressOffHand = this.prevEquipProgressOffHand = 1.0F;
   }

   private boolean shouldSkipHandAnimationOnSwap(ItemStack var1, ItemStack var2) {
      return ItemStack.areEqual(var1, var2) || !mc.getItemModelManager().hasHandAnimationOnSwap(var2);
   }

   public void render(BotPlayer var1, @Nullable BotPlayHandler var2, float var3, MatrixStack var4, OrderedRenderCommandQueue var5, int var6) {
      float var7 = var1.getHandSwingProgress(var3);
      Hand var8 = var1.preferredHand != null ? var1.preferredHand : Hand.MAIN_HAND;
      float var9 = var1.getLerpedPitch(var3);
      BotHeldItemRenderer.HandRenderType var10 = getHandRenderType(var1);
      if (var10.renderMainHand) {
         float var11 = var8 == Hand.MAIN_HAND ? var7 : 0.0F;
         float var12 = 1.0F - MathHelper.lerp(var3, this.prevEquipProgressMainHand, this.equipProgressMainHand);
         this.renderFirstPersonItem(var1, var2, var3, var9, Hand.MAIN_HAND, var11, this.mainHand, var12, var4, var5, var6);
      }

      if (var10.renderOffHand) {
         float var13 = var8 == Hand.OFF_HAND ? var7 : 0.0F;
         float var14 = 1.0F - MathHelper.lerp(var3, this.prevEquipProgressOffHand, this.equipProgressOffHand);
         this.renderFirstPersonItem(var1, var2, var3, var9, Hand.OFF_HAND, var13, this.offHand, var14, var4, var5, var6);
      }
   }

   private static BotHeldItemRenderer.HandRenderType getHandRenderType(BotPlayer var0) {
      ItemStack var1 = var0.getMainHandStack();
      ItemStack var2 = var0.getOffHandStack();
      boolean var3 = var1.isOf(Items.BOW) || var2.isOf(Items.BOW);
      boolean var4 = var1.isOf(Items.CROSSBOW) || var2.isOf(Items.CROSSBOW);
      if (!var3 && !var4) {
         return BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      } else if (var0.isUsingItem()) {
         return getUsingItemHandRenderType(var0);
      } else {
         return isChargedCrossbow(var1) ? BotHeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY : BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      }
   }

   private static BotHeldItemRenderer.HandRenderType getUsingItemHandRenderType(BotPlayer var0) {
      ItemStack var1 = var0.getActiveItem();
      Hand var2 = var0.getActiveHand();
      if (!var1.isOf(Items.BOW) && !var1.isOf(Items.CROSSBOW)) {
         return var2 == Hand.MAIN_HAND && isChargedCrossbow(var0.getOffHandStack())
            ? BotHeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY
            : BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      } else {
         return BotHeldItemRenderer.HandRenderType.shouldOnlyRender(var2);
      }
   }

   private static boolean isChargedCrossbow(ItemStack var0) {
      return var0.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(var0);
   }

   private void renderFirstPersonItem(
      BotPlayer var1,
      @Nullable BotPlayHandler var2,
      float var3,
      float var4,
      Hand var5,
      float var6,
      ItemStack var7,
      float var8,
      MatrixStack var9,
      OrderedRenderCommandQueue var10,
      int var11
   ) {
      if (!var1.isUsingSpyglass()) {
         boolean var12 = var5 == Hand.MAIN_HAND;
         Arm var13 = var12 ? var1.getMainArm() : var1.getMainArm().getOpposite();
         var9.push();
         if (var7.isEmpty()) {
            if (var12 && !var1.isInvisible()) {
               this.renderArmHoldingItem(var1, var2, var9, var10, var11, var8, var6, var13);
            }
         } else if (var7.contains(DataComponentTypes.MAP_ID)) {
            if (var12 && this.offHand.isEmpty()) {
               this.renderMapInBothHands(var1, var2, var9, var10, var11, var4, var8, var6);
            } else {
               this.renderMapInOneHand(var1, var2, var9, var10, var11, var8, var13, var6, var7);
            }
         } else if (var7.isOf(Items.CROSSBOW)) {
            boolean var14 = CrossbowItem.isCharged(var7);
            boolean var15 = var13 == Arm.RIGHT;
            int var16 = var15 ? 1 : -1;
            if (var1.isUsingItem() && var1.getItemUseTimeLeft() > 0 && var1.getActiveHand() == var5) {
               this.applyEquipOffset(var9, var13, var8);
               var9.translate(var16 * -0.4785682F, -0.094387F, 0.05731531F);
               var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
               var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var16 * 65.3F));
               var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var16 * -9.785F));
               float var17 = var7.getMaxUseTime(var1) - (var1.getItemUseTimeLeft() - var3 + 1.0F);
               float var18 = var17 / CrossbowItem.getPullTime(var7, var1);
               if (var18 > 1.0F) {
                  var18 = 1.0F;
               }

               if (var18 > 0.1F) {
                  float var19 = MathHelper.sin((var17 - 0.1F) * 1.3F) * (var18 - 0.1F);
                  var9.translate(0.0F, var19 * 0.004F, 0.0F);
               }

               var9.translate(0.0F, 0.0F, var18 * 0.04F);
               var9.scale(1.0F, 1.0F, 1.0F + var18 * 0.2F);
               var9.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(var16 * 45.0F));
            } else {
               this.swingArm(var6, var8, var9, var16, var13);
               if (var14 && var6 < 0.001F && var12) {
                  var9.translate(var16 * -0.641864F, 0.0F, 0.0F);
                  var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var16 * 10.0F));
               }
            }

            this.renderItem(
               var1, var7, var15 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !var15, var9, var10, var11
            );
         } else {
            boolean var20 = var13 == Arm.RIGHT;
            int var21 = var20 ? 1 : -1;
            if (var1.isUsingItem() && var1.getItemUseTimeLeft() > 0 && var1.getActiveHand() == var5) {
               switch (var7.getUseAction()) {
                  case NONE:
                     this.applyEquipOffset(var9, var13, var8);
                     break;
                  case EAT:
                  case DRINK:
                     this.applyEatOrDrinkTransformation(var9, var3, var13, var7, var1);
                     this.applyEquipOffset(var9, var13, var8);
                     break;
                  case BLOCK:
                     this.applyEquipOffset(var9, var13, var8);
                     if (!(var7.getItem() instanceof ShieldItem)) {
                        var9.translate(var21 * -0.14142136F, 0.08F, 0.14142136F);
                        var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-102.25F));
                        var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var21 * 13.365F));
                        var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var21 * 78.05F));
                     }
                     break;
                  case BOW:
                     this.applyEquipOffset(var9, var13, var8);
                     var9.translate(var21 * -0.2785682F, 0.18344387F, 0.15731531F);
                     var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
                     var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var21 * 35.3F));
                     var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var21 * -9.785F));
                     float var23 = var7.getMaxUseTime(var1) - (var1.getItemUseTimeLeft() - var3 + 1.0F);
                     float var25 = var23 / 20.0F;
                     var25 = (var25 * var25 + var25 * 2.0F) / 3.0F;
                     if (var25 > 1.0F) {
                        var25 = 1.0F;
                     }

                     if (var25 > 0.1F) {
                        float var28 = MathHelper.sin((var23 - 0.1F) * 1.3F) * (var25 - 0.1F);
                        var9.translate(0.0F, var28 * 0.004F, 0.0F);
                     }

                     var9.translate(0.0F, 0.0F, var25 * 0.04F);
                     var9.scale(1.0F, 1.0F, 1.0F + var25 * 0.2F);
                     var9.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(var21 * 45.0F));
                     break;
                  case SPEAR:
                     this.applyEquipOffset(var9, var13, var8);
                     var9.translate(var21 * -0.5F, 0.7F, 0.1F);
                     var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
                     var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var21 * 35.3F));
                     var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var21 * -9.785F));
                     float var22 = var7.getMaxUseTime(var1) - (var1.getItemUseTimeLeft() - var3 + 1.0F);
                     float var24 = Math.min(var22 / 10.0F, 1.0F);
                     if (var24 > 0.1F) {
                        float var27 = MathHelper.sin((var22 - 0.1F) * 1.3F) * (var24 - 0.1F);
                        var9.translate(0.0F, var27 * 0.004F, 0.0F);
                     }

                     var9.translate(0.0F, 0.0F, var24 * 0.2F);
                     var9.scale(1.0F, 1.0F, 1.0F + var24 * 0.2F);
                     var9.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(var21 * 45.0F));
                     break;
                  case BRUSH:
                     this.applyBrushTransformation(var9, var3, var13, var1, var8);
                     break;
                  case BUNDLE:
                  default:
                     this.swingArm(var6, var8, var9, var21, var13);
               }
            } else if (var1.isUsingRiptide()) {
               this.applyEquipOffset(var9, var13, var8);
               var9.translate(var21 * -0.4F, 0.8F, 0.3F);
               var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var21 * 65.0F));
               var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var21 * -85.0F));
            } else {
               this.swingArm(var6, var8, var9, var21, var13);
            }

            this.renderItem(
               var1, var7, var20 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !var20, var9, var10, var11
            );
         }

         var9.pop();
      }
   }

   private void renderItem(BotPlayer var1, ItemStack var2, ItemDisplayContext var3, boolean var4, MatrixStack var5, OrderedRenderCommandQueue var6, int var7) {
      if (!var2.isEmpty()) {
         ItemRenderState var8 = new ItemRenderState();
         mc.getItemModelManager().clearAndUpdate(var8, var2, var3, var1.getEntityWorld(), var1, var1.getId() + var3.ordinal());
         var8.render(var5, var6, var7, OverlayTexture.DEFAULT_UV, 0);
      }
   }

   private void renderArmHoldingItem(
      BotPlayer var1, @Nullable BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, float var6, float var7, Arm var8
   ) {
      boolean var9 = var8 != Arm.LEFT;
      float var10 = var9 ? 1.0F : -1.0F;
      float var11 = MathHelper.sqrt(var7);
      float var12 = -0.3F * MathHelper.sin(var11 * (float) Math.PI);
      float var13 = 0.4F * MathHelper.sin(var11 * (float) (Math.PI * 2));
      float var14 = -0.4F * MathHelper.sin(var7 * (float) Math.PI);
      var3.translate(var10 * (var12 + 0.64000005F), var13 + -0.6F + var6 * -0.6F, var14 + -0.71999997F);
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var10 * 45.0F));
      float var15 = MathHelper.sin(var7 * var7 * (float) Math.PI);
      float var16 = MathHelper.sin(var11 * (float) Math.PI);
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var10 * var16 * 70.0F));
      var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var10 * var15 * -20.0F));
      var3.translate(var10 * -1.0F, 3.6F, 3.5F);
      var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var10 * 120.0F));
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(200.0F));
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var10 * -135.0F));
      var3.translate(var10 * 5.6F, 0.0F, 0.0F);
      this.renderArm(var1, var2, var3, var4, var5, var8);
   }

   private void renderArm(BotPlayer var1, @Nullable BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, Arm var6) {
      SkinTextures var7 = BotPlayerRenderer.resolveSkin(var1, var2);
      PlayerEntityRenderer var8 = BotPlayerRenderer.rendererFor(var7.model());
      if (var8 != null) {
         Identifier var9 = var7.body().texturePath();
         if (var6 == Arm.RIGHT) {
            var8.renderRightArm(var3, var4, var5, var9, true);
         } else {
            var8.renderLeftArm(var3, var4, var5, var9, true);
         }
      }
   }

   private void renderMapInOneHand(
      BotPlayer var1,
      @Nullable BotPlayHandler var2,
      MatrixStack var3,
      OrderedRenderCommandQueue var4,
      int var5,
      float var6,
      Arm var7,
      float var8,
      ItemStack var9
   ) {
      float var10 = var7 == Arm.RIGHT ? 1.0F : -1.0F;
      var3.translate(var10 * 0.125F, -0.125F, 0.0F);
      if (!var1.isInvisible()) {
         var3.push();
         var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var10 * 10.0F));
         this.renderArmHoldingItem(var1, var2, var3, var4, var5, var6, var8, var7);
         var3.pop();
      }

      var3.push();
      var3.translate(var10 * 0.51F, -0.08F + var6 * -1.2F, -0.75F);
      float var11 = MathHelper.sqrt(var8);
      float var12 = MathHelper.sin(var11 * (float) Math.PI);
      float var13 = -0.5F * var12;
      float var14 = 0.4F * MathHelper.sin(var11 * (float) (Math.PI * 2));
      float var15 = -0.3F * MathHelper.sin(var8 * (float) Math.PI);
      var3.translate(var10 * var13, var14 - 0.3F * var12, var15);
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var12 * -45.0F));
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var10 * var12 * -30.0F));
      this.renderFirstPersonMap(var1, var3, var4, var5, var9);
      var3.pop();
   }

   private void renderMapInBothHands(
      BotPlayer var1, @Nullable BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, float var6, float var7, float var8
   ) {
      float var9 = MathHelper.sqrt(var8);
      float var10 = -0.2F * MathHelper.sin(var8 * (float) Math.PI);
      float var11 = -0.4F * MathHelper.sin(var9 * (float) Math.PI);
      var3.translate(0.0F, -var10 / 2.0F, var11);
      float var12 = this.getMapAngle(var6);
      var3.translate(0.0F, 0.04F + var7 * -1.2F + var12 * -0.5F, -0.72F);
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var12 * -85.0F));
      if (!var1.isInvisible()) {
         var3.push();
         var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
         this.renderPlainArm(var1, var2, var3, var4, var5, Arm.RIGHT);
         this.renderPlainArm(var1, var2, var3, var4, var5, Arm.LEFT);
         var3.pop();
      }

      float var13 = MathHelper.sin(var9 * (float) Math.PI);
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var13 * 20.0F));
      var3.scale(2.0F, 2.0F, 2.0F);
      this.renderFirstPersonMap(var1, var3, var4, var5, this.mainHand);
   }

   private void renderPlainArm(BotPlayer var1, @Nullable BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, Arm var6) {
      var3.push();
      float var7 = var6 == Arm.RIGHT ? 1.0F : -1.0F;
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(92.0F));
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
      var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var7 * -41.0F));
      var3.translate(var7 * 0.3F, -1.1F, 0.45F);
      this.renderArm(var1, var2, var3, var4, var5, var6);
      var3.pop();
   }

   private void renderFirstPersonMap(BotPlayer var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4, ItemStack var5) {
      Immediate var6 = mc.getBufferBuilders().getEntityVertexConsumers();
      var2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      var2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
      var2.scale(0.38F, 0.38F, 0.38F);
      var2.translate(-0.5F, -0.5F, 0.0F);
      var2.scale(0.0078125F, 0.0078125F, 0.0078125F);
      MapIdComponent var7 = var5.get(DataComponentTypes.MAP_ID);
      MapState var8 = FilledMapItem.getMapState(var7, var1.getEntityWorld());
      VertexConsumer var9 = var6.getBuffer(var8 == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
      Matrix4f var10 = var2.peek().getPositionMatrix();
      var9.vertex(var10, -7.0F, 135.0F, 0.0F).color(-1).texture(0.0F, 1.0F).light(var4);
      var9.vertex(var10, 135.0F, 135.0F, 0.0F).color(-1).texture(1.0F, 1.0F).light(var4);
      var9.vertex(var10, 135.0F, -7.0F, 0.0F).color(-1).texture(1.0F, 0.0F).light(var4);
      var9.vertex(var10, -7.0F, -7.0F, 0.0F).color(-1).texture(0.0F, 0.0F).light(var4);
      if (var8 != null) {
         MapRenderer var11 = mc.getMapRenderer();
         var11.update(var7, var8, this.mapRenderState);
         var11.draw(this.mapRenderState, var2, var3, false, var4);
      }
   }

   private float getMapAngle(float var1) {
      float var2 = 1.0F - var1 / 45.0F + 0.1F;
      var2 = MathHelper.clamp(var2, 0.0F, 1.0F);
      return -MathHelper.cos(var2 * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void applyEatOrDrinkTransformation(MatrixStack var1, float var2, Arm var3, ItemStack var4, BotPlayer var5) {
      float var6 = var5.getItemUseTimeLeft() - var2 + 1.0F;
      float var7 = var6 / var4.getMaxUseTime(var5);
      if (var7 < 0.8F) {
         float var8 = MathHelper.abs(MathHelper.cos(var6 / 4.0F * (float) Math.PI) * 0.1F);
         var1.translate(0.0F, var8, 0.0F);
      }

      float var10 = 1.0F - (float)Math.pow(var7, 27.0);
      int var9 = var3 == Arm.RIGHT ? 1 : -1;
      var1.translate(var10 * 0.6F * var9, var10 * -0.5F, 0.0F);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var9 * var10 * 90.0F));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var10 * 10.0F));
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var9 * var10 * 30.0F));
   }

   private void applyBrushTransformation(MatrixStack var1, float var2, Arm var3, BotPlayer var4, float var5) {
      this.applyEquipOffset(var1, var3, var5);
      float var6 = var4.getItemUseTimeLeft() % 10;
      float var7 = var6 - var2 + 1.0F;
      float var8 = 1.0F - var7 / 10.0F;
      float var9 = -15.0F + 75.0F * MathHelper.cos(var8 * 2.0F * (float) Math.PI);
      if (var3 != Arm.RIGHT) {
         var1.translate(0.1, 0.83, 0.35);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var9));
         var1.translate(-0.3, 0.22, 0.35);
      } else {
         var1.translate(-0.25, 0.22, 0.35);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var9));
      }
   }

   private void applySwingOffset(MatrixStack var1, Arm var2, float var3) {
      int var4 = var2 == Arm.RIGHT ? 1 : -1;
      float var5 = MathHelper.sin(var3 * var3 * (float) Math.PI);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var4 * (45.0F + var5 * -20.0F)));
      float var6 = MathHelper.sin(MathHelper.sqrt(var3) * (float) Math.PI);
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var4 * var6 * -20.0F));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var6 * -80.0F));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var4 * -45.0F));
   }

   private void applyEquipOffset(MatrixStack var1, Arm var2, float var3) {
      int var4 = var2 == Arm.RIGHT ? 1 : -1;
      var1.translate(var4 * 0.56F, -0.52F + var3 * -0.6F, -0.72F);
   }

   private void swingArm(float var1, float var2, MatrixStack var3, int var4, Arm var5) {
      float var6 = -0.4F * MathHelper.sin(MathHelper.sqrt(var1) * (float) Math.PI);
      float var7 = 0.2F * MathHelper.sin(MathHelper.sqrt(var1) * (float) (Math.PI * 2));
      float var8 = -0.2F * MathHelper.sin(var1 * (float) Math.PI);
      var3.translate(var4 * var6, var7, var8);
      this.applyEquipOffset(var3, var5, var2);
      this.applySwingOffset(var3, var5, var1);
   }

   private static enum HandRenderType {
      RENDER_BOTH_HANDS(true, true),
      RENDER_MAIN_HAND_ONLY(true, false),
      RENDER_OFF_HAND_ONLY(false, true);

      public final boolean renderMainHand;
      public final boolean renderOffHand;

      private HandRenderType(boolean var3, boolean var4) {
         this.renderMainHand = var3;
         this.renderOffHand = var4;
      }

      public static BotHeldItemRenderer.HandRenderType shouldOnlyRender(Hand var0) {
         return var0 == Hand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
      }
   }
}

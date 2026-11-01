package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.AttackEvent;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.mixin.accessors.ItemRenderStateAccessor;
import org.zenith.utility.mixin.accessors.LayerRenderStateAccessor;

public final class HeldItemPoseController implements MinecraftAccess {
   private static final int CONST_intValue = -1;

   public HeldItemPoseController() {
      EventManager.register(this);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1 -> this.tick());
   }

   public static boolean method6156(
      ArmedEntityRenderState var0, ItemRenderState var1, ItemStack var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5
   ) {
      if (var0 instanceof PlayerEntityRenderState var6) {
         HoveredData var7 = method6158(WardrobeFeatureRenderer.method4444(var6), var2);
         if (var7 == null) {
            return false;
         } else {
            float var8 = 0.0F;
            if (FocusSlotEnum.CROSSBOW.matches(var2)) {
               var8 = CrossbowItem.isCharged(var2)
                  ? 1.0F
                  : (var6.isUsingItem && var6.crossbowPullTime > 0.0F ? Math.min(0.97F, var6.itemUseTime / var6.crossbowPullTime) : 0.0F);
            } else if (var6.isUsingItem && FocusSlotEnum.BOW.matches(var2)) {
               var8 = BowItem.getPullProgress((int)var6.itemUseTime);
            }

            return method6159(var7, var6.id, var1, var3, var4, var5, var8, var6.age / 20.0F, true);
         }
      } else {
         return false;
      }
   }

   public static boolean method6157(LivingEntity var0, ItemStack var1, ItemRenderState var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5) {
      Wardrobe var6 = Wardrobe.method2488();
      if (var6 != null && var0 != null && var0 == mc.player && var6.showSelf.isEnabled()) {
         HoveredData var7 = method6158(var6.method2498(mc.player.getGameProfile().name(), true), var1);
         if (var7 == null) {
            return false;
         } else {
            float var8 = 0.0F;
            if (FocusSlotEnum.CROSSBOW.matches(var1)) {
               var8 = CrossbowItem.isCharged(var1)
                  ? 1.0F
                  : (
                     var0.isUsingItem() && var0.getActiveItem() == var1
                        ? Math.min(0.97F, (float)var0.getItemUseTime() / CrossbowItem.getPullTime(var1, var0))
                        : 0.0F
                  );
            } else if (var0.isUsingItem() && var0.getActiveItem() == var1 && FocusSlotEnum.BOW.matches(var1)) {
               var8 = BowItem.getPullProgress(var0.getItemUseTime());
            }

            float var9 = (var0.age + mc.getRenderTickCounter().getTickProgress(true)) / 20.0F;
            return method6159(var7, var0.getId(), var2, var3, var4, var5, var8, var9, false);
         }
      } else {
         return false;
      }
   }

   private static HoveredData method6158(List<HoveredData> var0, ItemStack var1) {
      Wardrobe var2 = Wardrobe.method2488();
      FocusSlotEnum var3 = FocusSlotEnum.method545(var1);
      return var2 != null && var3 != null && !var0.isEmpty() ? var2.method2499(var0, var3) : null;
   }

   private static boolean method6159(
      HoveredData var0, int var1, ItemRenderState var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, float var6, float var7, boolean var8
   ) {
      Wardrobe var9 = Wardrobe.method2488();
      Class15 var10 = var9 == null ? null : var9.getRegistry().method1427(var0);
      if (var10 != null && var10.data2 != null) {
         ItemRenderStateAccessor var11 = (ItemRenderStateAccessor)(Object)var2;
         if (var11.zenith$getLayerCount() > 0 && var11.zenith$getDisplayContext() != null) {
            Transformation var12 = ((LayerRenderStateAccessor)(Object)var11.zenith$getLayers()[0]).zenith$getTransform();
            Class15.Data2 var13 = var10.data2;
            AnimationCache.Entry var14 = AnimationCache.method7621(var1, var0, var9.isFlag());
            var14.animationState.method6539(var10, "idle", var7);
            float[] var15 = var14.class16.method2139(var10, var14.animationState, var7, null);
            var14.class16.method2141(var10, var14.animationState, "pull", var6, var6 > 0.0F ? 1.0F : 0.0F);
            if (var13.getIntValue() >= 0) {
               float var16 = var6 > (var0.getFocusSlotEnum() == FocusSlotEnum.CROSSBOW ? 0.99F : 0.05F) ? 1.0F : 0.0F;
               var15[var13.getIntValue() * 9 + 6] = var16;
               var15[var13.getIntValue() * 9 + 7] = var16;
               var15[var13.getIntValue() * 9 + 8] = var16;
            }

            var3.push();

            try {
               var12.apply(var11.zenith$getDisplayContext().isLeftHand(), var3.peek());
               var3.translate(var13.getFloatValue() / 16.0F, var13.getFloatValue2() / 16.0F, var13.getFloatValue3() / 16.0F);
               boolean var20 = var8 && var0.method1546() == WardrobeCategory.ITEM && var0.getFocusSlotEnum() != FocusSlotEnum.ANCHOR;
               if (var20) {
                  var3.translate(0.0F, 0.0F, 0.22F);
               }

               if (var13.getFloatValue4() != 0.0F) {
                  var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var13.getFloatValue4()));
               }

               var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
               if (var13.getFloatValue5() != 0.0F) {
                  var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var13.getFloatValue5()));
               }

               if (var20) {
                  var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                  var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
               }

               if (var13.getFloatValue6() != 1.0F) {
                  var3.scale(var13.getFloatValue6(), var13.getFloatValue6(), var13.getFloatValue6());
               }

               BoneMatrices.method4787(var10, var15, 6, var3, var4, var5, OverlayTexture.DEFAULT_UV, true, var0.getRarity().isFlag(), -1);
            } finally {
               var3.pop();
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Class15 method6160(ItemStack var0) {
      Wardrobe var1 = Wardrobe.method2488();
      if (var1 != null && mc.player != null && var1.showSelf.isEnabled()) {
         HoveredData var2 = method6158(var1.method2498(mc.player.getGameProfile().name(), true), var0);
         return var2 == null ? null : var1.getRegistry().method1427(var2);
      } else {
         return null;
      }
   }

   @EventTarget
   public void method6161(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.PRE && mc.player != null && Util19.enabled()) {
         Class15 var2 = method6160(mc.player.getStackInHand(Hand.MAIN_HAND));
         Entity var3 = var1.getEntity();
         if (var2 != null && var2.data2 != null && var3 != null) {
            Vec3d var4 = var3.getBoundingBox().getCenter();
            if (var2.data2.getParticleEffect2() != null) {
               for (int var5 = 0; var5 < 10; var5++) {
                  Util19.method5043(
                     var2.data2.getParticleEffect2(),
                     var4.x + (Math.random() - 0.5) * var3.getWidth(),
                     var4.y + (Math.random() - 0.5) * var3.getHeight() * 0.8,
                     var4.z + (Math.random() - 0.5) * var3.getWidth(),
                     (Math.random() - 0.5) * 0.2,
                     Math.random() * 0.15,
                     (Math.random() - 0.5) * 0.2
                  );
               }
            }
         }
      }
   }

   private void tick() {
      if (mc.player != null && mc.world != null && Util19.enabled()) {
         Class15 var1 = method6160(mc.player.getStackInHand(Hand.MAIN_HAND));
         if (var1 != null && var1.data2 != null && var1.data2.getParticleEffect() != null && mc.player.handSwinging) {
            float var2 = mc.player.getHandSwingProgress(1.0F);
            float var3 = (float)Math.toRadians(mc.player.getYaw());
            Vec3d var4 = mc.player.getEyePos();
            Vec3d var5 = new Vec3d(-Math.sin(var3), 0.0, Math.cos(var3));
            Vec3d var6 = new Vec3d(-Math.cos(var3), 0.0, -Math.sin(var3));

            for (int var7 = 0; var7 < 3; var7++) {
               float var8 = Math.min(1.0F, var2 + var7 * 0.08F);
               double var9 = 0.7 - 1.4 * var8;
               double var11 = 0.35 - 0.7 * var8;
               Vec3d var13 = var4.add(var5.multiply(0.95)).add(var6.multiply(var9)).add(0.0, var11, 0.0);
               Util19.method5043(var1.data2.getParticleEffect(), var13.x, var13.y, var13.z, 0.0, 0.0, 0.0);
            }
         }

         Class15 var14 = null;
         Wardrobe var15 = Wardrobe.method2488();
         if (var15 != null) {
            FocusSlotEnum var16 = !FocusSlotEnum.CROSSBOW.matches(mc.player.getMainHandStack()) && !FocusSlotEnum.CROSSBOW.matches(mc.player.getOffHandStack())
               ? FocusSlotEnum.BOW
               : FocusSlotEnum.CROSSBOW;
            HoveredData var18 = var15.method2491(var16);
            var14 = var18 == null ? null : var15.getRegistry().method1427(var18);
         }

         if (var14 != null && var14.data2 != null && var14.data2.getParticleEffect() != null) {
            for (Entity var19 : mc.world.getEntities()) {
               if (var19 instanceof PersistentProjectileEntity var20
                  && var20.getOwner() == mc.player
                  && var20.getVelocity().lengthSquared() > 0.01
                  && var20.age < 200) {
                  Util19.method5043(var14.data2.getParticleEffect(), var20.getX(), var20.getY(), var20.getZ(), 0.0, 0.0, 0.0);
               }
            }
         }
      }
   }
}

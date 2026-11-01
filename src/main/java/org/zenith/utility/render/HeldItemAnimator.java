package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.mixin.accessors.HeldItemRendererAccessor;

public final class HeldItemAnimator implements MinecraftAccess {
   private static final Vector3f vector3f = new Vector3f(0.73597F, -0.43693F, -0.85587F);
   private static final Vector3f vector3f2 = new Vector3f(0.0F, -0.12F, -0.5F);
   private static final Quaternionf quaternionf = new Quaternionf(0.12325F, 0.18889F, 0.71204F, 0.66493F);
   private static final Vector3f vector3f3 = new Vector3f(-0.8F, 0.25F, -0.45F).normalize();
   private static final Vector3f vector3f4 = method4770();
   private static final float CONST_floatValue = 0.15F;
   private static final float CONST_floatValue2 = 0.75F;
   private static final float CONST_floatValue3 = 0.6F;
   private static final float CONST_floatValue4 = 0.55F;
   private static final float[][] CONST_floatValues = new float[][]{
      {0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
      {0.45F, -10.0F, 0.004F, 0.018F, 0.008F},
      {0.85F, 0.0F, 0.0F, 0.006F, 0.0F},
      {1.55F, 58.0F, 0.012F, 0.024F, 0.022F},
      {2.05F, 52.0F, 0.01F, 0.02F, 0.02F},
      {2.7F, -24.0F, -0.006F, 0.01F, 0.0F},
      {3.25F, 2.0F, 0.0F, 0.003F, 0.0F},
      {3.7F, 0.0F, 0.0F, 0.0F, 0.0F},
      {4.2F, 0.0F, 0.0F, 0.0F, 0.0F}
   };
   private static HeldItemAnimator INSTANCE;
   private long longValue = -1L;
   private boolean flag;
   private int slot = -1;

   public HeldItemAnimator() {
      INSTANCE = this;
      EventManager.register(this);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1 -> this.tick());
   }

   private static Vector3f method4770() {
      Vector3f var0 = new Vector3f(vector3f2).negate().normalize();
      return var0.sub(new Vector3f(vector3f3).mul(var0.dot(vector3f3))).normalize();
   }

   @EventTarget
   private void method4771(KeyEvent var1) {
      if (var1.isPress(74) && this.longValue < 0L && method4772()) {
         Hand var2 = method4773();
         this.flag = var2 != null && !mc.player.getStackInHand(var2).isEmpty();
         this.slot = mc.player.getInventory().getSelectedSlot();
         this.longValue = System.nanoTime();
      }
   }

   private void tick() {
      if (this.longValue >= 0L) {
         if (!method4772()
            || mc.player.handSwinging
            || mc.player.isUsingItem()
            || mc.player.getInventory().getSelectedSlot() != this.slot
            || this.method4774() >= CONST_floatValues[CONST_floatValues.length - 1][0]) {
            this.stop();
         }
      }
   }

   private void stop() {
      this.longValue = -1L;
      Hand var1 = method4773();
      if (this.flag && var1 != null && mc.gameRenderer != null) {
         mc.gameRenderer.firstPersonRenderer.resetEquipProgress(var1);
      }

      this.flag = false;
   }

   private static boolean method4772() {
      if (mc.player != null && mc.currentScreen == null && mc.options.getPerspective().isFirstPerson() && !mc.player.isInvisible()) {
         Wardrobe var0 = Wardrobe.method2488();
         return var0 != null && var0.showSelf.isEnabled()
            ? var0.method2499(var0.method2498(mc.player.getGameProfile().name(), true), FocusSlotEnum.WATCH) != null
            : false;
      } else {
         return false;
      }
   }

   private static Hand method4773() {
      if (mc.player == null) {
         return null;
      } else {
         return mc.player.getMainArm() == Arm.RIGHT ? Hand.MAIN_HAND : Hand.OFF_HAND;
      }
   }

   private float method4774() {
      return (float)(System.nanoTime() - this.longValue) / 1.0E9F;
   }

   public static boolean method4775(
      HeldItemRenderer var0, AbstractClientPlayerEntity var1, Hand var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5
   ) {
      HeldItemAnimator var6 = INSTANCE;
      if (var6 != null && var6.longValue >= 0L && var1 == mc.player && var2 == method4773()) {
         float var7 = CONST_floatValues[CONST_floatValues.length - 1][0];
         float var8 = Math.min(var6.method4774(), var7);
         float var9 = method4776(var8 / 0.6F) * method4776((var7 - var8) / 0.55F);
         float var10 = method4777(var8, 1);
         float var11 = (float)Math.sin(var8 * 2.4F) * 0.0035F * var9;
         Vector3f var12 = new Vector3f(vector3f2).add(method4777(var8, 2), method4777(var8, 3) + var11, method4777(var8, 4));
         Vector3f var13 = new Vector3f(vector3f);
         if (var6.flag) {
            var13.y -= 0.75F;
         }

         Vector3f var14 = var13.lerp(var12, var9);
         Quaternionf var15 = new Quaternionf().slerp(quaternionf, var9);
         Vector3f var16 = new Vector3f(vector3f4).mul(-0.15F);
         var3.push();
         var3.translate(var14.x, var14.y, var14.z);
         var3.translate(var16.x, var16.y, var16.z);
         var3.multiply(new Quaternionf().rotationAxis(var10 * (float) (Math.PI / 180.0), vector3f3));
         var3.translate(-var16.x, -var16.y, -var16.z);
         var3.multiply(var15);
         var3.translate(-vector3f.x, -vector3f.y, -vector3f.z);
         ((HeldItemRendererAccessor)(Object)var0).zenith$renderArmHoldingItem(var3, var4, var5, 0.0F, 0.0F, Arm.RIGHT);
         var3.pop();
         return true;
      } else {
         return false;
      }
   }

   private static float method4776(float var0) {
      float var1 = MathHelper.clamp(var0, 0.0F, 1.0F);
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   private static float method4777(float var0, int var1) {
      int var2 = 0;

      while (var2 < CONST_floatValues.length - 2 && var0 > CONST_floatValues[var2 + 1][0]) {
         var2++;
      }

      float var3 = CONST_floatValues[var2][0];
      float var4 = CONST_floatValues[var2 + 1][0];
      float var5 = var4 - var3;
      float var6 = MathHelper.clamp((var0 - var3) / var5, 0.0F, 1.0F);
      float var7 = CONST_floatValues[var2][var1];
      float var8 = CONST_floatValues[var2 + 1][var1];
      float var9 = method4778(var2, var1) * var5;
      float var10 = method4778(var2 + 1, var1) * var5;
      float var11 = var6 * var6;
      float var12 = var11 * var6;
      return (2.0F * var12 - 3.0F * var11 + 1.0F) * var7
         + (var12 - 2.0F * var11 + var6) * var9
         + (-2.0F * var12 + 3.0F * var11) * var8
         + (var12 - var11) * var10;
   }

   private static float method4778(int var0, int var1) {
      if (var0 > 0 && var0 < CONST_floatValues.length - 1) {
         float[] var2 = CONST_floatValues[var0 - 1];
         float[] var3 = CONST_floatValues[var0 + 1];
         return (var3[var1] - var2[var1]) / (var3[0] - var2[0]);
      } else {
         return 0.0F;
      }
   }
}

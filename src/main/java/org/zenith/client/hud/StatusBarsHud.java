package org.zenith.client.hud;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.InGameHud.HeartType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profilers;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.combat.PvpHelperModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.StatusBarColors;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class StatusBarsHud extends Interface {
   private final List<StatusBarsHud.Entry> items = new ArrayList<>();
   private Animation animation = new Animation(200L, Easing.INSTANCE10);
   private MutableText mutableText;
   private boolean flag;

   public StatusBarsHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      float var9 = 22.0F;
      this.width = var9 * 9.0F;
      this.height = var9;

      for (int var10 = 0; var10 < 9; var10++) {
         this.items.add(new StatusBarsHud.Entry(var10));
      }
   }

   @Override
   public void method8(CustomDrawContext var1) {
      this.height = 22.0F;
      this.width = 198.0F;
      float var2 = this.getX();
      float var3 = this.getY();
      float var4 = InterfaceModule.method6801();
      ZenithStyle var5 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (mc.interactionManager.getCurrentGameMode().isCreative()) {
         this.method5546(var1, var3 - 35.0F);
         this.method5548(var1, mc.getRenderTickCounter(), var3 - 35.0F - 9.0F);
         Font var18 = Fonts.NEW_MEDIUM.getFont(6.0F);
         int var19 = mc.player.experienceLevel;
         var1.drawText(
            var18,
            String.valueOf(var19),
            var2 + this.width / 2.0F - var18.width(String.valueOf(var19)) / 2.0F,
            var3 - 15.0F + var18.height() / 2.0F,
            ZenithColor.INSTANCE3
         );
         var1.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(var4), ZenithColor.INSTANCE);
         var1.drawRoundedRect(
            this.floatValue,
            this.floatValue2,
            this.width,
            this.height,
            ProxyDotRadiusData.method8069(var4),
            var5.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
         );
         ItemStack var20 = mc.player.getOffHandStack();
         if (!var20.isEmpty()) {
            float var21 = var2 - this.height - 12.0F;
            var1.drawBlurHud(var21, var3, this.height, this.height, 21.0F, ProxyDotRadiusData.method8069(var4), ZenithColor.INSTANCE);
            var1.drawRoundedRect(
               var21,
               var3,
               this.height,
               this.height,
               ProxyDotRadiusData.method8069(var4),
               var5.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
            );
            var1.pushMatrix();
            var1.getMatrices().translate((float)(var21 + 4.6), (float)(var3 + 4.6));
            var1.getMatrices().scale(0.8F, 0.8F);
            var1.drawItem(var20, 0, 0);
            var1.drawItemBar(var20, 0, 0);
            var1.drawCooldownProgress(var20, 0, 0);
            var1.popMatrix();
            if (var20.getCount() > 1) {
               String var24 = "x" + var20.getCount();
               float var27 = var18.width(var24);
               float var28 = var21 + 22.0F - var27 - 1.0F;
               float var30 = var3 + 22.0F - var18.height() - 3.0F;
               var1.drawText(var18, var24, var28, var30, var5.getTextTertiary().getZenithColor());
            }
         }

         float var22 = var2;

         for (StatusBarsHud.Entry var25 : this.items) {
            var25.method5558(var1, var22, var3, var5);
            var22 += this.height;
         }
      } else {
         if (mc.interactionManager.hasStatusBars()) {
            int var6 = var1.getScaledWindowWidth() / 2 - 91;
            int var7 = var1.getScaledWindowHeight() - 39;
            float var8 = 0.9F;
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(var2, var3 - 12.0F);
            var1.getMatrices().scale(var8, var8);
            var1.getMatrices().translate(-var6, -var7);
            if (!mc.interactionManager.getCurrentGameMode().isCreative()) {
               this.method5549(var1);
            }

            var1.getMatrices().popMatrix();
            this.method5546(var1, var3 - 35.0F);
            this.method5548(var1, mc.getRenderTickCounter(), var3 - 35.0F - 9.0F);
            Font var9 = Fonts.NEW_MEDIUM.getFont(6.0F);
            int var10 = mc.player.experienceLevel;
            var1.drawText(
               var9,
               String.valueOf(var10),
               var2 + this.width / 2.0F - var9.width(String.valueOf(var10)) / 2.0F,
               var3 - 15.0F + var9.height() / 2.0F,
               ZenithColor.INSTANCE3
            );
            var1.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(var4), ZenithColor.INSTANCE);
            var1.drawRoundedRect(
               this.floatValue,
               this.floatValue2,
               this.width,
               this.height,
               ProxyDotRadiusData.method8069(var4),
               var5.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
            );
            ItemStack var11 = mc.player.getOffHandStack();
            if (!var11.isEmpty()) {
               float var12 = var2 - this.height - 12.0F;
               var1.drawBlurHud(var12, var3, this.height, this.height, 21.0F, ProxyDotRadiusData.method8069(var4), ZenithColor.INSTANCE);
               var1.drawRoundedRect(
                  var12,
                  var3,
                  this.height,
                  this.height,
                  ProxyDotRadiusData.method8069(var4),
                  var5.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
               );
               var1.pushMatrix();
               var1.getMatrices().translate((float)(var12 + 4.6), (float)(var3 + 4.6));
               var1.getMatrices().scale(0.8F, 0.8F);
               var1.drawItem(var11, 0, 0);
               var1.drawItemBar(var11, 0, 0);
               var1.drawCooldownProgress(var11, 0, 0);
               var1.popMatrix();
               if (var11.getCount() > 1) {
                  String var14 = "x" + var11.getCount();
                  float var15 = var9.width(var14);
                  float var16 = var12 + 22.0F - var15 - 3.0F;
                  float var17 = var3 + 22.0F - var9.height() - 3.0F;
                  var1.drawText(var9, var14, var16, var17, var5.getTextTertiary().getZenithColor());
               }
            }

            float var26 = var2;

            for (StatusBarsHud.Entry var29 : this.items) {
               var29.method5558(var1, var26, var3, var5);
               var26 += this.height;
            }
         } else {
            this.width = 0.0F;
            this.height = 0.0F;
         }
      }
   }

   private void method5546(CustomDrawContext var1, float var2) {
      if (this.animation == null) {
         this.animation = new Animation(300L, Easing.INSTANCE10);
      }

      this.animation.setDuration(200L);
      this.animation.setEasing(Easing.INSTANCE10);
      if (mc.inGameHud.heldItemTooltipFade > 0 && !mc.inGameHud.currentStack.isEmpty()) {
         this.animation.method2808(1.0F);
         this.mutableText = Text.empty().append(mc.inGameHud.currentStack.getName()).formatted(mc.inGameHud.currentStack.getRarity().getFormatting());
         if (mc.inGameHud.currentStack.contains(DataComponentTypes.CUSTOM_NAME)) {
            this.mutableText.formatted(Formatting.ITALIC);
         }
      } else {
         this.animation.method2808(0.0F);
      }

      if (this.mutableText != null && this.animation.getValue() > 0.0F) {
         this.method5547(var1, this.mutableText, var2);
      }
   }

   private void method5547(CustomDrawContext var1, MutableText var2, float var3) {
      Font var4 = HudListLayout.nameFont();
      float var5 = var4.width(var2);
      float var6 = (this.width - var5) / 2.0F;
      int var7 = (int)var3;
      if (!mc.interactionManager.hasStatusBars() || mc.interactionManager.getCurrentGameMode().isCreative()) {
         var7 += 14;
      }

      short var8 = 255;
      if (var8 > 0) {
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.floatValue + var6, var7);
         var1.drawText(var4, var2, 0.0F, 0.0F, ColorHelper.withAlpha((int)(var8 * this.animation.getValue()), -1));
         var1.getMatrices().popMatrix();
      }
   }

   public final void method5548(CustomDrawContext var1, RenderTickCounter var2, float var3) {
      if (mc.inGameHud.overlayMessage != null && mc.inGameHud.overlayRemaining > 0) {
         float var4 = mc.inGameHud.overlayRemaining - var2.getTickProgress(false);
         int var5 = (int)(var4 * 255.0F / 20.0F);
         if (var5 > 255) {
            var5 = 255;
         }

         if (var5 > 8) {
            Font var6 = Fonts.NEW_MEDIUM.getFont(8.0F);
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(this.floatValue + this.width / 2.0F, var3);
            int var7;
            if (mc.inGameHud.overlayTinted) {
               var7 = MathHelper.hsvToArgb(var4 / 50.0F, 0.7F, 0.6F, var5);
            } else {
               var7 = ColorHelper.withAlpha(var5, -1);
            }

            float var8 = var6.width(mc.inGameHud.overlayMessage);
            var1.getMatrices().translate(-var8 / 2.0F, -var6.height() / 2.0F);
            var1.drawText(var6, mc.inGameHud.overlayMessage, 0.0F, 0.0F, var7);
            var1.getMatrices().popMatrix();
         }
      }
   }

   @Override
   public void load(JsonObject var1) {
      this.flag = var1.has("windowWidth");
      super.load(var1);
   }

   @Override
   public void method288(float var1, float var2) {
      if (!(var2 <= 0.0F) && !(var1 <= 0.0F)) {
         if (!this.method2090()) {
            float var3 = this.floatValue3 > 0.0F ? this.floatValue3 : var1;
            float var4 = this.floatValue4 > 0.0F ? this.floatValue4 : var2;
            this.method2091(var3, var4);
         }

         float var6 = (this.method296() - this.width) / 2.0F;
         float var7 = this.method294();
         if (this.flag && this.floatValue3 > 0.0F && this.floatValue3 != var1) {
            float var5 = this.method294() + this.method296() / 2.0F - this.floatValue3 / 2.0F;
            var7 = var1 / 2.0F + var5 - this.method296() / 2.0F;
         }

         this.flag = true;
         this.floatValue3 = var1;
         this.floatValue4 = var2;
         float var8 = this.method2084(var2) + this.floatValue6 * this.method2086(var2);
         this.floatValue = this.method2088(var7, var1) + var6;
         this.floatValue2 = this.method2089(var8, var2) + this.method2065();
      }
   }

   @Override
   public void method2091(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         float var3 = this.method2083(var1);
         float var4 = this.method2086(var2);
         if (!(var3 <= 0.0F) && !(var4 <= 0.0F)) {
            this.floatValue6 = (this.method295() - this.method2084(var2)) / var4;
         }
      }
   }

   private void method5549(DrawContext var1) {
      ClientPlayerEntity var2 = mc.player;
      if (var2 != null) {
         int var3 = MathHelper.ceil(var2.getHealth());
         boolean var4 = mc.inGameHud.heartJumpEndTick > mc.inGameHud.ticks && (mc.inGameHud.heartJumpEndTick - mc.inGameHud.ticks) / 3L % 2L == 1L;
         long var5 = Util.getMeasuringTimeMs();
         if (var3 < mc.inGameHud.lastHealthValue && var2.timeUntilRegen > 0) {
            mc.inGameHud.lastHealthCheckTime = var5;
            mc.inGameHud.heartJumpEndTick = mc.inGameHud.ticks + 20;
         } else if (var3 > mc.inGameHud.lastHealthValue && var2.timeUntilRegen > 0) {
            mc.inGameHud.lastHealthCheckTime = var5;
            mc.inGameHud.heartJumpEndTick = mc.inGameHud.ticks + 10;
         }

         if (var5 - mc.inGameHud.lastHealthCheckTime > 1000L) {
            mc.inGameHud.renderHealthValue = var3;
            mc.inGameHud.lastHealthCheckTime = var5;
         }

         mc.inGameHud.lastHealthValue = var3;
         int var7 = mc.inGameHud.renderHealthValue;
         mc.inGameHud.random.setSeed(mc.inGameHud.ticks * 312871);
         int var8 = var1.getScaledWindowWidth() / 2 - 91;
         int var9 = var1.getScaledWindowWidth() / 2 + 90 + 36;
         int var10 = var1.getScaledWindowHeight() - 39;
         float var11 = Math.max((float)var2.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(var7, var3));
         int var12 = MathHelper.ceil(var2.getAbsorptionAmount());
         int var13 = MathHelper.ceil((var11 + var12) / 2.0F / 10.0F);
         int var14 = Math.max(10 - (var13 - 2), 3);
         int var15 = var10 - 10;
         int var16 = -1;
         if (var2.hasStatusEffect(StatusEffects.REGENERATION)) {
            var16 = mc.inGameHud.ticks % MathHelper.ceil(var11 + 5.0F);
         }

         Profilers.get().push("armor");
         method5550(var1, var2, var10, var13, var14, var8);
         Profilers.get().swap("health");
         this.method5551(var1, var2, var8, var10, var14, var16, var11, var3, var7, var12, var4);
         LivingEntity var17 = mc.inGameHud.getRiddenEntity();
         int var18 = mc.inGameHud.getHeartCount(var17);
         if (var18 == 0) {
            Profilers.get().swap("food");
            this.method5552(var1, var2, var10, var9);
            var15 -= 10;
         }

         Profilers.get().swap("air");
         this.method5554(var1, var2, var18, var15, var9);
         Profilers.get().pop();
      }
   }

   private static void method5550(DrawContext var0, PlayerEntity var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.getArmor();
      if (var6 > 0) {
         int var7 = var2 - (var3 - 1) * var4 - 10;

         for (int var8 = 0; var8 < 10; var8++) {
            int var9 = var5 + var8 * 8;
            if (var8 * 2 + 1 < var6) {
               var0.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.ARMOR_FULL_TEXTURE, var9, var7, 9, 9);
            }

            if (var8 * 2 + 1 == var6) {
               var0.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.ARMOR_HALF_TEXTURE, var9, var7, 9, 9);
            }

            if (var8 * 2 + 1 > var6) {
               var0.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.ARMOR_EMPTY_TEXTURE, var9, var7, 9, 9);
            }
         }
      }
   }

   private void method5551(
      DrawContext var1, PlayerEntity var2, int var3, int var4, int var5, int var6, float var7, int var8, int var9, int var10, boolean var11
   ) {
      HeartType var12 = HeartType.fromPlayerState(var2);
      boolean var13 = var2.getEntityWorld().getLevelProperties().isHardcore();
      int var14 = MathHelper.ceil(var7 / 2.0);
      int var15 = MathHelper.ceil(var10 / 2.0);
      int var16 = var14 * 2;

      for (int var17 = var14 + var15 - 1; var17 >= 0; var17--) {
         int var18 = var17 / 10;
         int var19 = var17 % 10;
         int var20 = var3 + var19 * 8;
         int var21 = var4 - var18 * var5;
         if (var8 + var10 <= 4) {
            var21 += mc.inGameHud.random.nextInt(2);
         }

         if (var17 < var14 && var17 == var6) {
            var21 -= 2;
         }

         this.method5553(var1, HeartType.CONTAINER, var20, var21, var13, var11, false);
         int var22 = var17 * 2;
         boolean var23 = var17 >= var14;
         if (var23) {
            int var24 = var22 - var16;
            if (var24 < var10) {
               boolean var25 = var24 + 1 == var10;
               this.method5553(var1, var12 == HeartType.WITHERED ? var12 : HeartType.ABSORBING, var20, var21, var13, false, var25);
            }
         }

         if (var11 && var22 < var9) {
            boolean var26 = var22 + 1 == var9;
            this.method5553(var1, var12, var20, var21, var13, true, var26);
         }

         if (var22 < var8) {
            boolean var27 = var22 + 1 == var8;
            this.method5553(var1, var12, var20, var21, var13, false, var27);
         }
      }
   }

   private void method5552(DrawContext var1, PlayerEntity var2, int var3, int var4) {
      HungerManager var5 = var2.getHungerManager();
      int var6 = var5.getFoodLevel();

      for (int var7 = 0; var7 < 10; var7++) {
         int var8 = var3;
         Identifier var9;
         Identifier var10;
         Identifier var11;
         if (var2.hasStatusEffect(StatusEffects.HUNGER)) {
            var9 = InGameHud.FOOD_EMPTY_HUNGER_TEXTURE;
            var10 = InGameHud.FOOD_HALF_HUNGER_TEXTURE;
            var11 = InGameHud.FOOD_FULL_HUNGER_TEXTURE;
         } else {
            var9 = InGameHud.FOOD_EMPTY_TEXTURE;
            var10 = InGameHud.FOOD_HALF_TEXTURE;
            var11 = InGameHud.FOOD_FULL_TEXTURE;
         }

         if (var2.getHungerManager().getSaturationLevel() <= 0.0F && mc.inGameHud.ticks % (var6 * 3 + 1) == 0) {
            var8 = var3 + (mc.inGameHud.random.nextInt(3) - 1);
         }

         int var12 = var4 - var7 * 8 - 9;
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, var9, var12, var8, 9, 9);
         if (var7 * 2 + 1 < var6) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, var11, var12, var8, 9, 9);
         }

         if (var7 * 2 + 1 == var6) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, var10, var12, var8, 9, 9);
         }
      }

      StatusBarColors.method1755(var1, var2, var3, var4);
   }

   private void method5553(DrawContext var1, HeartType var2, int var3, int var4, boolean var5, boolean var6, boolean var7) {
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, var2.getTexture(var5, var7, var6), var3, var4, 9, 9);
   }

   private void method5554(DrawContext var1, PlayerEntity var2, int var3, int var4, int var5) {
      int var6 = var2.getMaxAir();
      int var7 = Math.clamp((long)var2.getAir(), 0, var6);
      boolean var8 = var2.isSubmergedIn(FluidTags.WATER);
      if (var8 || var7 < var6) {
         var4 = this.method5555(var3, var4);
         int var9 = this.method5556(var7, var6, -2);
         int var10 = this.method5556(var7, var6, 0);
         int var11 = 10 - this.method5556(var7, var6, this.method5557(var7, var8));
         boolean var12 = var9 != var10;
         if (!var8) {
            mc.inGameHud.lastBurstBubble = 0;
         }

         for (int var13 = 1; var13 <= 10; var13++) {
            int var14 = var5 - (var13 - 1) * 8 - 9;
            if (var13 <= var9) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.AIR_TEXTURE, var14, var4, 9, 9);
            } else if (var12 && var13 == var10 && var8) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.AIR_BURSTING_TEXTURE, var14, var4, 9, 9);
               mc.inGameHud.playBurstSound(var13, var2, var11);
            } else if (var13 > 10 - var11) {
               int var15 = var11 == 10 && mc.inGameHud.ticks % 2 == 0 ? mc.inGameHud.random.nextInt(2) : 0;
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.AIR_EMPTY_TEXTURE, var14, var4 + var15, 9, 9);
            }
         }
      }
   }

   private int method5555(int var1, int var2) {
      int var3 = mc.inGameHud.getHeartRows(var1) - 1;
      return var2 - var3 * 10;
   }

   private int method5556(int var1, int var2, int var3) {
      return MathHelper.ceil((float)((var1 + var3) * 10) / var2);
   }

   private int method5557(int var1, boolean var2) {
      return var1 != 0 && var2 ? 1 : 0;
   }

   public class Entry {
      private final Animation animation = new Animation(260L, 1.0F, Easing.INSTANCE10);
      private final Animation animation2 = new Animation(150L, 0.0F, Easing.INSTANCE13);
      private boolean flag;
      private final int intValue;

      private Entry(int var2) {
         this.intValue = var2;
      }

      public void method5558(CustomDrawContext var1, float var2, float var3, ZenithStyle var4) {
         float var5 = InterfaceModule.method6801();
         ProxyDotRadiusData var6 = ProxyDotRadiusData.method8069(var5);
         Font var7 = Fonts.NEW_MEDIUM.getFont(5.4F);
         boolean var8 = this.intValue == MinecraftAccess.mc.player.getInventory().getSelectedSlot();
         if (var8 && !this.flag) {
            this.animation.method2809(0.0F);
         }

         if (!var8) {
            this.animation.method2809(1.0F);
         }

         this.flag = var8;
         float var9 = var8 ? this.animation.method2808(1.0F) : 1.0F;
         ZenithColor var10 = var4.getHeaderHudBackground().method1447(var8 ? 1.0F : 0.0F);
         ZenithColor var11 = var8 ? var4.getTextEnable().getZenithColor() : var4.getTextSecondary().getZenithColor();
         ItemStack var12 = MinecraftAccess.mc.player.getInventory().getMainStacks().get(this.intValue);
         this.animation2.method2807(var12.isEmpty());
         var1.drawRoundedRect(var2, var3, 22.0F, 22.0F, var6, var10);
         if (var8) {
            var1.drawRoundedRect(var2, var3, 22.0F, 22.0F, var6, var4.getPrimaryColor().getZenithColor().method7293(100));
         }

         if (PvpHelperModule.INSTANCE.method4645(this.intValue)) {
            var1.drawRoundedRect(var2, var3, 22.0F, 22.0F, var6, var4.getPrimaryColor().getZenithColor().method7294(0.45F));
         }

         if (var8 && var9 < 1.0F) {
            ZenithColor var13 = var4.getPrimaryColor().getZenithColor().method7295(ZenithColor.INSTANCE, 0.55F);
            float var14 = 1.0F - (1.0F - var9) * (1.0F - var9) * (1.0F - var9);
            float var15 = 4.0F * (1.0F - var14);
            float var16 = 22.0F - var15 * 2.0F;
            var1.drawRoundedBorder(
               var2 + var15,
               var3 + var15,
               var16,
               var16,
               1.2F,
               ProxyDotRadiusData.method8069(Math.max(0.0F, var5 - var15)),
               var13.method7292(0.8F * (1.0F - var9))
            );
         }

         var1.pushMatrix();
         var1.getMatrices().translate((float)(var2 + 4.6), (float)(var3 + 4.6));
         var1.getMatrices().scale(0.8F, 0.8F);
         var1.drawItem(var12, 0, 0);
         var1.drawItemBar(var12, 0, 0);
         var1.drawCooldownProgress(var12, 0, 0);
         var1.popMatrix();
         var1.drawText(
            var7,
            String.valueOf(this.intValue + 1),
            var2 + (22.0F - var7.width(String.valueOf(this.intValue + 1))) / 2.0F,
            var3 + (22.0F - var7.height()) / 2.0F,
            var4.getTextTertiary().method1447(this.animation2.getValue())
         );
         if (var12.getCount() > 1) {
            String var17 = "x" + var12.getCount();
            float var18 = var7.width(var17);
            float var19 = var2 + 22.0F - var18 - 3.0F;
            float var20 = var3 + 22.0F - var7.height() - 3.0F;
            var1.drawText(var7, var17, var19, var20, var11);
         }
      }
   }
}

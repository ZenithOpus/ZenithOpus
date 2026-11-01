package org.zenith.client.hud;

import java.util.List;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.game.Util14;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class TargetHud extends Interface {
   private final Animation animation = new Animation(200L, Easing.INSTANCE10);
   private final Animation animation2 = new Animation(200L, Easing.INSTANCE10);
   private final Animation animation3 = new Animation(200L, Easing.INSTANCE13);
   private final Animation animation4 = new Animation(150L, Easing.INSTANCE25);
   private LivingEntity livingEntity;
   private BoundsData bounds;
   private String text = "";
   private static final float CONST_floatValue = 7.0F;
   private static final float CONST_floatValue2 = 1.0F;
   private static final float CONST_floatValue3 = 7.0F;
   private static final float CONST_floatValue4 = 62.0F;
   private static final float CONST_floatValue5 = 10.0F;

   public TargetHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public boolean method2056(MouseButtonEvent var1) {
      Vector2f var2 = this.method2099();
      if (this.bounds != null && this.bounds.contains(var2.x(), var2.y()) && mc.player != null) {
         mc.keyboard.setClipboard(mc.player.getGameProfile().name());
         return true;
      } else {
         return super.method2056(var1);
      }
   }

   @Override
   public void method8(CustomDrawContext var1) {
      AuraModule var2 = AuraModule.INSTANCE;
      Object var3 = !(mc.currentScreen instanceof ChatScreen) && !Zenith.getInstance().getNLMenuScreen().isRenderHud()
         ? (var2.method6358() == null ? AimAssistModule.INSTANCE.method5156() : var2.method6358())
         : mc.player;
      this.method7235((LivingEntity)var3);
      if (this.animation3.getValue() != 0.0F && this.livingEntity != null) {
         this.method7233((MenuDrawContext)var1, this.livingEntity, this.animation3.getValue());
      }
   }

   private void method7233(MenuDrawContext var1, LivingEntity var2, float var3) {
      ZenithStyle var4 = HudListLayout.style();
      boolean var5 = var2 instanceof PlayerEntity;
      float var6 = MathHelper.clamp(UseItemUtils.method6527(var2), 0.0F, 999.0F);
      float var7 = Math.max(1.0F, var2.getMaxHealth());
      float var8 = MathHelper.clamp(var6 / var7, 0.0F, 1.0F);
      float var9 = MathHelper.clamp(this.animation.method2808(var8), 0.0F, 1.0F);
      float var10 = Math.max(0.0F, var6 - var7);
      float var11 = MathHelper.clamp(this.animation2.method2808(var10 / var7), 0.0F, 1.0F);
      String var12 = String.valueOf(Math.round(var6));
      Font var13 = HudListLayout.nameFont();
      Font var14 = Fonts.NEW_SEMIBOLD.getFont(7.5F);
      Font var15 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String var16 = NameProtectModule.method5149(var2.getName().getString());
      float var17 = 49.0F;
      float var18 = Math.max(var17, Math.min(70.0F, var13.width(var16)));
      float var19 = Math.max(18.0F, var14.width("999") + 4.0F);
      float var20 = 27.0F + var18 + 4.0F + var19 + 4.0F;
      float var21 = 26.0F;
      this.width = var20;
      this.height = var21;
      float var22 = this.floatValue;
      float var23 = this.floatValue2;
      float var24 = var22 + 27.0F;
      float var25 = var5 ? var23 + 4.0F : var23 + (var21 - var13.height()) / 2.0F;
      this.bounds = new BoundsData(var24, var25 - 1.0F, var18, var13.height() + 2.0F);
      Vector2f var26 = this.method2099();
      boolean var27 = InterfaceModule.INSTANCE.method6795(this);
      boolean var28 = !var27
         && (mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud())
         && this.bounds.contains(var26.x(), var26.y());
      if (var27 || !var16.equals(this.text)) {
         this.animation4.method2809(0.0F);
         this.text = var16;
      }

      float var29 = this.animation4.method2808(var28 ? 1.0F : 0.0F);
      String var30 = var13.clip(var16, var18);
      String var31 = var13.clip(method7236("hud.target.copy"), var18);
      ZenithColor var32 = var4.getPrimaryColor().getZenithColor();
      ZenithColor var33 = var4.getTextEnable().getZenithColor();
      ZenithColor var34 = var4.getTextTertiary().getZenithColor();
      float var35 = MathHelper.clamp((0.35F - var9) / 0.35F, 0.0F, 1.0F);
      ZenithColor var36 = var32.method7295(new ZenithColor(255, 104, 119), var35);
      ZenithColor var37 = new ZenithColor(245, 201, 112);
      var1.pushMatrix();

      try {
         var1.getMatrices().translate(var22 + var20 / 2.0F, var23 + var21 / 2.0F);
         var1.getMatrices().scale(var3, var3);
         var1.getMatrices().translate(-(var22 + var20 / 2.0F), -(var23 + var21 / 2.0F));
         HudListLayout.method7635(var1, var22, var23, var20, var21);
         float var38 = var22 + 4.0F;
         float var39 = var23 + 4.0F;
         float var40 = 18.0F;
         float var41 = 13.0F;
         float var42 = (var40 - var41) / 2.0F;
         float var43 = MathHelper.clamp(var2.hurtTime / 10.0F, 0.0F, 1.0F);
         ZenithColor var44 = var36.method7295(new ZenithColor(255, 104, 119), var43 * 0.6F);
         var1.drawRoundedRect(var38 + 1.5F, var39 + 1.5F, var40 - 3.0F, var40 - 3.0F, ProxyDotRadiusData.method8069(7.5F), var32.method7294(0.08F));
         var1.drawArcBorder(var38, var39, var40, var40, 1.1F, 360.0F, 0.5F, var34.method7294(0.2F));
         if (var9 > 0.002F) {
            var1.drawArcBorder(var38, var39, var40, var40, 1.1F, 360.0F * var9, 0.5F, var44);
         }

         if (var11 > 0.002F) {
            var1.drawArcBorder(var38 - 1.5F, var39 - 1.5F, var40 + 3.0F, var40 + 3.0F, 0.8F, 360.0F * var11, 0.5F, var37);
         }

         ProxyDotRadiusData var45 = ProxyDotRadiusData.method8069(4.0F);
         if (var2 instanceof AbstractClientPlayerEntity var46) {
            var1.drawPlayerHeadWithRoundedShader(
               var46.getSkin().body().texturePath(),
               var38 + var42,
               var39 + var42,
               var41,
               var45,
               ZenithColor.INSTANCE.method7295(new ZenithColor(255, 125, 135), var43 * 0.65F)
            );
         } else {
            var1.drawRoundedRect(var38 + var42, var39 + var42, var41, var41, var45, var32.method7294(0.25F));
            var1.drawText(var13, "?", var38 + (var40 - var13.width("?")) / 2.0F, var39 + (var40 - var13.height()) / 2.0F, var33);
         }

         float var52 = var5 ? var24 : var24 + (var18 - var13.width(var30)) / 2.0F;
         if (var29 < 0.999F) {
            var1.drawText(var13, var30, var52, var25 - var29 * 2.0F, var33.method7294(1.0F - var29));
         }

         if (var29 > 0.001F) {
            float var47 = var5 ? var24 : var24 + (var18 - var13.width(var31)) / 2.0F;
            var1.drawText(var13, var31, var47, var25 + (1.0F - var29) * 2.0F, var32.method7294(var29));
         }

         float var53 = var22 + var20 - 4.0F - var19;
         var1.drawRect(var53 - 2.0F, var23 + 6.0F, 0.5F, var21 - 12.0F, var34.method7294(0.2F));
         var1.drawText(var14, var12, var53 + (var19 - var14.width(var12)) / 2.0F, var23 + 5.0F, var36.method7295(var33, 0.3F));
         String var48 = var10 > 0.0F ? "+" + Math.round(var10) : "HP";
         var1.drawText(var15, var48, var53 + (var19 - var15.width(var48)) / 2.0F, var23 + 16.0F, var10 > 0.0F ? var37 : var34);
         if (var5) {
            this.method7234(var1, (PlayerEntity)var2, var24, var23 + 12.0F, var18);
         }
      } finally {
         var1.popMatrix();
      }
   }

   private void method7234(CustomDrawContext var1, PlayerEntity var2, float var3, float var4, float var5) {
      var var6 = ArmorSlots.method5384(var2);
      ItemStack[] var7 = new ItemStack[]{
         var2.getMainHandStack(), var2.getOffHandStack(), (ItemStack)var6.get(3), (ItemStack)var6.get(2), (ItemStack)var6.get(1), (ItemStack)var6.get(0)
      };
      ZenithStyle var8 = HudListLayout.style();
      float var9 = 49.0F;
      float var10 = var3 + (var5 - var9) / 2.0F;
      float var11 = var4 + 1.5F;
      Font var12 = Fonts.ICONS.getFont(4.5F);
      boolean var13 = Util14.method2639(var2);

      for (int var14 = 0; var14 < var7.length; var14++) {
         if (var14 == 2) {
            var1.drawRect(var10 + 0.5F, var11 + 2.0F, 0.5F, 3.0F, var8.getTextTertiary().getZenithColor().method7294(0.22F));
            var10 += 2.0F;
         }

         ItemStack var15 = var7[var14];
         if (var15.isEmpty()) {
            var1.drawText(var12, "M", var10 + (7.0F - var12.width("M")) / 2.0F, var11 + (7.0F - var12.height()) / 2.0F, var8.getTextTertiary().getZenithColor());
         } else {
            var1.pushMatrix();

            try {
               var1.getMatrices().translate(var10, var11);
               var1.getMatrices().scale(0.4375F, 0.4375F);
               var1.drawItem(var15, 0, 0);
               if (var13) {
                  var1.drawItemBar(var15, 0, 0);
               }

               var1.drawCooldownProgress(var15, 0, 0);
            } finally {
               var1.popMatrix();
            }
         }

         var10 += 8.0F;
      }
   }

   public void method7235(LivingEntity var1) {
      if (var1 == null) {
         this.animation3.method2808(0.0F);
         if (this.animation3.getValue() == 0.0F) {
            this.livingEntity = null;
         }
      } else if (var1 != this.livingEntity) {
         this.animation3.method2808(0.0F);
         if (this.animation3.getValue() == 0.0F) {
            this.livingEntity = var1;
         }
      } else {
         this.animation3.method2808(1.0F);
      }
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private static String method7236(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }
}

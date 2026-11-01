package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class TargetPotionsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "0";
   private static final String text2 = "Target potions";
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Map<String, TargetPotionsHud.Entry> items = new LinkedHashMap<>();
   private final Set<String> items2 = new HashSet<>();
   private boolean flag = false;
   private LivingEntity livingEntity = null;

   public TargetPotionsHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      if (mc.player != null && mc.world != null) {
         LivingEntity var2 = null;
         if (AuraModule.INSTANCE.getLivingEntity22() != null
            && mc.world.getEntityById(AuraModule.INSTANCE.getLivingEntity22().getId()) instanceof LivingEntity var4) {
            var2 = var4;
         }

         this.flag = var2 == null;
         this.livingEntity = var2;
         this.items2.clear();
         if (var2 != null) {
            for (StatusEffectInstance var5 : new ArrayList<>(var2.getActiveStatusEffects().values())) {
               String var6 = this.method6994(var5);
               this.items2.add(var6);
               if (!this.items.containsKey(var6)) {
                  this.items.put(var6, new TargetPotionsHud.Entry(var5, this.method6992(var5)));
               }
            }
         }

         this.items.values().removeIf(TargetPotionsHud.Entry::method7001);
         if (this.items.isEmpty()) {
            this.animation2.method2808(0.0F);
         } else {
            TargetPotionsHud.Entry var17 = this.items.values().iterator().next();
            this.animation2.method2808(this.items.size() == 1 && var17.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
         }

         ZenithStyle var18 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font var20 = Fonts.NEW_ICONS.getFont(5.5F);
         Font var21 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float var22 = this.floatValue;
         float var7 = this.floatValue2;
         float var8 = (float)this.items
            .values()
            .stream()
            .mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var0.animation.getValue())
            .sum();
         float var9 = 20.0F + GuiStyle.PADDING.intValue() / 2.0F + var8;
         float var10 = (float)this.items.values().stream().mapToDouble(TargetPotionsHud.Entry::method6999).max().orElse(0.0);
         var10 = this.animation.method2808(Math.max(HudListLayout.method7638("0", "Target potions"), var10));
         this.width = var10;
         this.height = var9;
         this.animation3.method2807(mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
         float var11 = InterfaceModule.method6801();
         ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(var11);
         var1.pushMatrix();
         var1.getMatrices().translate(var22 + var10 / 2.0F, var7 + var9 / 2.0F);
         var1.getMatrices().scale(this.animation3.getValue(), this.animation3.getValue());
         var1.getMatrices().translate(-(var22 + var10 / 2.0F), -(var7 + var9 / 2.0F));
         HudListLayout.method7635(var1, var22, var7, var10, var9);
         HudListLayout.method7636(var1, var22, var7, var10, "0", "Target potions");
         if (this.animation3.getValue() == 1.0F) {
            float var13 = var7 + 20.0F;
            var1.enableScissor((int)var22, (int)var7, (int)(var22 + var10), (int)(var7 + var9));

            for (TargetPotionsHud.Entry var15 : this.items.values()) {
               var15.method7000(var1, var22, var13, var10);
               var13 += (var15.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var15.animation.getValue();
            }

            var1.disableScissor();
         }

         var1.popMatrix();
         this.livingEntity = null;
      }
   }

   private boolean method6992(StatusEffectInstance var1) {
      return var1.getEffectType().value().getCategory().equals(StatusEffectCategory.BENEFICIAL);
   }

   private String method6993(StatusEffectInstance var1) {
      String var2 = I18n.translate(var1.getEffectType().value().getTranslationKey());
      String var3 = this.method6995(var1.getAmplifier());
      return var2 + " " + var3;
   }

   private String method6994(StatusEffectInstance var1) {
      return var1.getEffectType().value().getTranslationKey() + var1.getAmplifier();
   }

   private String method6995(int var1) {
      return String.valueOf(var1 + 1);
   }

   private String method6996(int var1) {
      int var2 = var1 / 20;
      int var3 = var2 / 60;
      int var4 = var2 % 60;
      return String.format("%02d:%02d", var3, var4);
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private class Entry {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private final Animation animation2 = new Animation(200L, 1.0F, Easing.INSTANCE10);
      private final boolean flag;
      private StatusEffectInstance statusEffectInstance;
      private int intValue;
      private final String text;
      private Object obj;
      private String text2;
      private int intValue2;
      private String text3;

      public Entry(StatusEffectInstance var2, boolean var3) {
         this.statusEffectInstance = var2;
         this.flag = var3;
         this.intValue = Math.max(1, var2.getDuration());
         this.text = TargetPotionsHud.this.method6994(var2);
      }

      private String method6997() {
         Language var1 = Language.getInstance();
         if (this.text2 == null || var1 != this.obj) {
            this.obj = var1;
            this.text2 = TargetPotionsHud.this.method6993(this.statusEffectInstance);
         }

         return this.text2;
      }

      private String method6998() {
         int var1 = this.statusEffectInstance.getDuration() / 20;
         if (this.text3 == null || var1 != this.intValue2) {
            this.intValue2 = var1;
            this.text3 = TargetPotionsHud.this.method6996(this.statusEffectInstance.getDuration());
         }

         return this.text3;
      }

      public float method6999() {
         Font var1 = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font var2 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String var3 = this.method6997();
         String var4 = this.method6998();
         return HudListLayout.method7651(var3, var4);
      }

      public float getHeight() {
         return 14.0F;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public void method7000(CustomDrawContext var1, float var2, float var3, float var4) {
         if (!TargetPotionsHud.this.flag && TargetPotionsHud.this.livingEntity != null) {
            StatusEffectInstance var5 = TargetPotionsHud.this.livingEntity.getStatusEffect(this.statusEffectInstance.getEffectType());
            if (var5 != null && var5.getAmplifier() == this.statusEffectInstance.getAmplifier()) {
               this.statusEffectInstance = var5;
            }
         }

         this.animation.method2808(TargetPotionsHud.this.items2.contains(this.text) && !TargetPotionsHud.this.flag ? 1.0F : 0.0F);
         ZenithStyle var16 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font var6 = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font var7 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String var8 = this.method6997();
         String var9 = this.method6998();
         int var10 = Math.max(0, this.statusEffectInstance.getDuration());
         if (TargetPotionsHud.this.items2.contains(this.text) && !TargetPotionsHud.this.flag) {
            this.intValue = Math.max(this.intValue, Math.max(1, var10));
         }

         float var11 = this.intValue <= 0 ? 0.0F : MathHelper.clamp((float)var10 / this.intValue, 0.0F, 1.0F);
         float var12 = MathHelper.clamp(this.animation2.method2808(var11), 0.0F, 1.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         float var13 = this.animation.getValue();
         var1.getMatrices().scale(var13, var13);
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         float var14 = HudListLayout.method7640(var1, var2, var3);
         float var15 = 8.0F;
         var1.pushMatrix();
         var1.getMatrices().translate(HudListLayout.method7641(var2) + (10.0F - var15) / 2.0F, HudListLayout.method7642(var3) + (10.0F - var15) / 2.0F);
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(this.statusEffectInstance.getEffectType()), 0, 0, (int)var15, (int)var15);
         var1.popMatrix();
         HudListLayout.method7644(var1, var14, var3, var8);
         HudListLayout.method7649(var1, var2, var3, var4, var9, var12);
         var1.popMatrix();
      }

      public boolean method7001() {
         return this.animation.getValue() == 0.0F;
      }
   }
}

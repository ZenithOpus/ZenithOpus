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
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class PotionsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "o";
   private static final String text2 = "Potions";
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Map<String, PotionsHud.Entry> items = new LinkedHashMap<>();
   private final Set<String> items2 = new HashSet<>();

   public PotionsHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      if (mc.player != null) {
         this.items2.clear();

         for (StatusEffectInstance var4 : new ArrayList<>(mc.player.getActiveStatusEffects().values())) {
            String var5 = this.method5489(var4);
            this.items2.add(var5);
            if (!this.items.containsKey(var5)) {
               this.items.put(var5, new PotionsHud.Entry(var4, this.method5487(var4)));
            }
         }

         this.items.values().removeIf(PotionsHud.Entry::method5496);
         if (this.items.isEmpty()) {
            this.animation2.method2808(0.0F);
         } else {
            PotionsHud.Entry var16 = this.items.values().iterator().next();
            this.animation2.method2808(this.items.size() == 1 && var16.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
         }

         Font var17 = Fonts.NEW_ICONS.getFont(5.5F);
         Font var18 = Fonts.NEW_MEDIUM.getFont(5.5F);
         ZenithStyle var19 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         float var6 = this.floatValue;
         float var7 = this.floatValue2;
         float var8 = (float)this.items
            .values()
            .stream()
            .mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var0.animation.getValue())
            .sum();
         float var9 = 20.0F + GuiStyle.PADDING.intValue() / 2.0F + var8;
         float var10 = (float)this.items.values().stream().mapToDouble(PotionsHud.Entry::method5494).max().orElse(0.0);
         var10 = this.animation.method2808(Math.max(HudListLayout.method7638("o", "Potions"), var10));
         this.width = var10;
         this.height = var9;
         this.animation3.method2807(mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
         float var11 = InterfaceModule.method6801();
         ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(var11);
         var1.pushMatrix();
         var1.getMatrices().translate(var6 + var10 / 2.0F, var7 + var9 / 2.0F);
         var1.getMatrices().scale(this.animation3.getValue(), this.animation3.getValue());
         var1.getMatrices().translate(-(var6 + var10 / 2.0F), -(var7 + var9 / 2.0F));
         HudListLayout.method7635(var1, var6, var7, var10, var9);
         HudListLayout.method7636(var1, var6, var7, var10, "o", "Potions");
         if (this.animation3.getValue() == 1.0F) {
            float var13 = var7 + 20.0F;
            var1.enableScissor((int)var6, (int)var7, (int)(var6 + var10), (int)(var7 + var9));

            for (PotionsHud.Entry var15 : this.items.values()) {
               var15.method5495(var1, var6, var13, var10);
               var13 += (var15.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var15.animation.getValue();
            }

            var1.disableScissor();
         }

         var1.popMatrix();
      }
   }

   private boolean method5487(StatusEffectInstance var1) {
      return var1.getEffectType().value().getCategory().equals(StatusEffectCategory.BENEFICIAL);
   }

   private String method5488(StatusEffectInstance var1) {
      String var2 = I18n.translate(var1.getEffectType().value().getTranslationKey());
      String var3 = this.method5490(var1.getAmplifier());
      return var2 + " " + var3;
   }

   private String method5489(StatusEffectInstance var1) {
      return var1.getEffectType().value().getTranslationKey() + var1.getAmplifier();
   }

   private String method5490(int var1) {
      return String.valueOf(var1 + 1);
   }

   private String method5491(int var1) {
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
         this.text = PotionsHud.this.method5489(var2);
      }

      private String method5492() {
         Language var1 = Language.getInstance();
         if (this.text2 == null || var1 != this.obj) {
            this.obj = var1;
            this.text2 = PotionsHud.this.method5488(this.statusEffectInstance);
         }

         return this.text2;
      }

      private String method5493() {
         int var1 = this.statusEffectInstance.getDuration() / 20;
         if (this.text3 == null || var1 != this.intValue2) {
            this.intValue2 = var1;
            this.text3 = PotionsHud.this.method5491(this.statusEffectInstance.getDuration());
         }

         return this.text3;
      }

      public float method5494() {
         Font var1 = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font var2 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String var3 = this.method5492();
         String var4 = this.method5493();
         return HudListLayout.method7651(var3, var4);
      }

      public float getHeight() {
         return 14.0F;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public void method5495(CustomDrawContext var1, float var2, float var3, float var4) {
         StatusEffectInstance var5 = MinecraftAccess.mc.player.getStatusEffect(this.statusEffectInstance.getEffectType());
         if (var5 != null && var5.getAmplifier() == this.statusEffectInstance.getAmplifier()) {
            this.statusEffectInstance = var5;
         }

         this.animation.method2808(PotionsHud.this.items2.contains(this.text) ? 1.0F : 0.0F);
         ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font var7 = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font var8 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String var9 = this.method5492();
         String var10 = this.method5493();
         int var11 = Math.max(0, this.statusEffectInstance.getDuration());
         if (PotionsHud.this.items2.contains(this.text)) {
            this.intValue = Math.max(this.intValue, Math.max(1, var11));
         }

         float var12 = this.intValue <= 0 ? 0.0F : MathHelper.clamp((float)var11 / this.intValue, 0.0F, 1.0F);
         float var13 = MathHelper.clamp(this.animation2.method2808(var12), 0.0F, 1.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         float var14 = this.animation.getValue();
         var1.getMatrices().scale(var14, var14);
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         float var15 = HudListLayout.method7640(var1, var2, var3);
         float var16 = 8.0F;
         var1.pushMatrix();
         var1.getMatrices().translate(HudListLayout.method7641(var2) + (10.0F - var16) / 2.0F, HudListLayout.method7642(var3) + (10.0F - var16) / 2.0F);
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(this.statusEffectInstance.getEffectType()), 0, 0, (int)var16, (int)var16);
         var1.popMatrix();
         HudListLayout.method7644(var1, var15, var3, var9);
         HudListLayout.method7649(var1, var2, var3, var4, var10, var13);
         var1.popMatrix();
      }

      public boolean method5496() {
         return this.animation.getValue() == 0.0F;
      }
   }
}

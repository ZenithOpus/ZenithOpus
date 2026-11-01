package org.zenith.client.screens.nlgui.elements.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiMultiBooleanSetting extends GuiSetting<MultiSelectSetting> {
   private final Animation animationExpanded = new Animation(200L, Easing.INSTANCE13);
   private final Animation textFadeAnimation = new Animation(130L, 1.0F, Easing.INSTANCE10);
   private final List<GuiMultiBooleanSetting.RenderValue> renderValues = new ArrayList<>();
   private BoundsData bounds;
   private BoundsData rectBounds;
   private boolean expanded;
   private String displayedValueText = "";
   private String pendingValueText = "";
   private boolean textSwitchPending;

   public GuiMultiBooleanSetting(MultiSelectSetting var1) {
      this(var1, 166.0F);
   }

   public GuiMultiBooleanSetting(MultiSelectSetting var1, float var2) {
      super(var2, var1);

      for (MultiSelectSetting.Entry var4 : var1.getItems2()) {
         this.renderValues.add(new GuiMultiBooleanSetting.RenderValue(var4));
      }

      this.displayedValueText = this.collectCurrentValueText();
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         this.expanded = (!this.expanded);
         return true;
      } else if (this.expanded && this.rectBounds != null && this.rectBounds.contains(var1, var3)) {
         return true;
      } else {
         this.expanded = false;
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         var6 *= this.animationVisible.getValue();
         Font var8 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var9 = Fonts.NEW_REGULAR.getFont(5.4F);
         float var10 = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ZenithColor var11 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var12 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(var1, var2, var3, "v", this.setting.getName(), this.setting.getDescription(), var8, var9, var4, var5, var10, var11, var12, var13);
         float var14 = this.width / 2.0F;
         float var15 = this.getHeight();
         this.bounds = new BoundsData(var4 + this.width - var14, var5 + (this.getHeight() - var15) / 2.0F, var14, var15);
         var1.drawRoundedRectBatched(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var14,
            var15,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            var7.getFieldSurfaceBackground().getZenithColor().method7294(var6)
         );
         var1.flushRoundedRects();
         var1.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var14,
            var15,
            0.1F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            var7.getFieldBorder().getZenithColor().method7294(var6)
         );
         String var16 = this.collectCurrentValueText();
         if (this.displayedValueText.isEmpty()) {
            this.displayedValueText = var16;
            this.textFadeAnimation.method2809(1.0F);
         }

         if (!this.textSwitchPending && !var16.equals(this.displayedValueText)) {
            this.pendingValueText = var16;
            this.textSwitchPending = true;
            this.textFadeAnimation.method2810(0.0F);
         } else if (this.textSwitchPending && !var16.equals(this.pendingValueText)) {
            this.pendingValueText = var16;
         }

         if (this.textSwitchPending && this.textFadeAnimation.update() <= 0.02F) {
            this.displayedValueText = this.pendingValueText;
            this.textSwitchPending = false;
            this.textFadeAnimation.method2809(0.0F);
            this.textFadeAnimation.method2810(1.0F);
         } else if (!this.textSwitchPending) {
            this.textFadeAnimation.method2808(1.0F);
         }

         Font var17 = Fonts.NEW_MEDIUM.getFont(5.3F);
         var10 = var14 - GuiStyle.PADDING * 2;
         float var18 = 0.4F;
         float var19 = this.textFadeAnimation.getValue();
         MsdfRenderer.renderText(
            var17.getFont(),
            this.displayedValueText,
            var17.getSize(),
            var11.method7294(var19).method7287(),
            MatrixUtils.method663(var1),
            this.bounds.getFloatValue() + GuiStyle.PADDING.intValue(),
            this.bounds.getFloatValue2() + (var15 - var17.height()) / 2.0F,
            0.0F,
            true,
            var18,
            1.0F,
            var10
         );
      }
   }

   @Override
   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var6 *= this.animationExpanded.getValue() * this.animationVisible.getValue();
      float var8 = this.width / 2.0F;
      float var9 = this.getHeight();
      this.bounds = new BoundsData(var4 + this.width - var8, var5 + (this.getHeight() - var9) / 2.0F, var8, var9);
      this.animationExpanded.method2807(this.expanded);
      if (this.animationExpanded.getValue() > 0.0F) {
         this.getWidth();
         float var11 = GuiStyle.PADDING.intValue() / 2.0F + this.renderValues.size() * (10.0F + GuiStyle.PADDING.intValue() / 2.0F);
         float var12 = this.bounds.getFloatValue2() + var9 + GuiStyle.PADDING.intValue();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.bounds.getFloatValue() + var8 / 2.0F, var12);
         var1.getMatrices().scale(this.animationExpanded.getValue(), this.animationExpanded.getValue());
         var1.getMatrices().translate(-(this.bounds.getFloatValue() + var8 / 2.0F), -var12);
         this.rectBounds = new BoundsData(this.bounds.getFloatValue(), var12, var8, var11);
         RenderShaders.method5289(
            MatrixUtils.method664(var1.getMatrices()),
            this.bounds.getFloatValue(),
            var12,
            var8,
            var11,
            12.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            ZenithColor.INSTANCE.method7294(var6)
         );
         ZenithStyle var13 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var13 != null) {
            var1.drawRoundedRectBatched(
               this.bounds.getFloatValue(),
               var12,
               var8,
               var11,
               ProxyDotRadiusData.method8069(4.0F),
               var13.getLeftBackground().getZenithColor().method7294(var6)
            );
            var1.flushRoundedRects();
            var1.drawRoundedBorder(
               this.bounds.getFloatValue(),
               var12,
               var8,
               var11,
               0.1F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               var13.getFieldBorder().getZenithColor().method7294(var6 * 2.0F)
            );
         }

         var12 += GuiStyle.PADDING.intValue() / 2.0F;
         Font var14 = Fonts.NEW_MEDIUM.getFont(5.3F);
         Font var15 = Fonts.NEW_ICONS.getFont(5.01F);

         for (GuiMultiBooleanSetting.RenderValue var17 : this.renderValues) {
            var17.addRectToBatch(var1, var2, var3, this.bounds.getFloatValue(), var12, var6);
            var12 += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         var1.flushRoundedRects();
         float var21 = this.bounds.getFloatValue2() + var9 + GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() / 2.0F;

         for (GuiMultiBooleanSetting.RenderValue var18 : this.renderValues) {
            var18.renderText(var1, this.bounds.getFloatValue(), var21, var6, var14, var15);
            var21 += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         var1.getMatrices().popMatrix();
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      if (this.expanded && this.rectBounds != null && this.rectBounds.contains(var1, var3) && var5.getIntValue() == 0) {
         for (GuiMultiBooleanSetting.RenderValue var7 : this.renderValues) {
            if (var7.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return true;
      } else if ((this.bounds == null || !this.bounds.contains(var1, var3)) && this.expanded) {
         this.expanded = false;
         return false;
      } else {
         return super.onMousePriorityClicked(var1, var3, var5);
      }
   }

   @Override
   public float getHeight() {
      return 14.0F;
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      this.expanded = false;
      return false;
   }

   private String collectCurrentValueText() {
      String var1 = this.setting
         .getItems2()
         .stream()
         .filter(MultiSelectSetting.Entry::isEnabled)
         .map(MultiSelectSetting.Entry::getName)
         .collect(Collectors.joining(",  "));
      return var1.isEmpty() ? "---------" : var1;
   }

   public class RenderValue {
      private final MultiSelectSetting.Entry value;
      private final Animation animationEnable;
      private BoundsData bounds;

      public RenderValue(MultiSelectSetting.Entry var2) {
         this.value = var2;
         this.animationEnable = new Animation(200L, var2.isEnabled() ? 1.0F : 0.0F, Easing.INSTANCE10);
      }

      public void addRectToBatch(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
         ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var7 != null) {
            float var8 = GuiMultiBooleanSetting.this.width / 2.0F - GuiStyle.PADDING.intValue();
            this.bounds = new BoundsData(var4 + GuiStyle.PADDING.intValue() / 2.0F, var5, var8, this.getHeight());
            this.animationEnable.method2808(this.value.isEnabled() ? 1.0F : (this.bounds.contains(var2, var3) ? 0.5F : 0.0F));
            var1.drawRoundedRectBatched(
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               this.bounds.width(),
               this.bounds.height(),
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 4.0F),
               ZenithColor.INSTANCE8.method7295(var7.getFieldSurfaceBackground().getZenithColor(), this.animationEnable.getValue()).method7294(var6)
            );
         }
      }

      public void renderText(MenuDrawContext var1, float var2, float var3, float var4, Font var5, Font var6) {
         ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var7 != null) {
            float var8 = GuiMultiBooleanSetting.this.width / 2.0F - GuiStyle.PADDING.intValue();
            float var9 = var2 + GuiStyle.PADDING.intValue();
            float var10 = var2 + GuiStyle.PADDING.intValue() / 2.0F + var8 - var6.width("<") - GuiStyle.PADDING.intValue() - var9;
            var1.drawText(
               var5,
               var5.clip(this.value.getName(), var10),
               var9,
               var3 + (this.getHeight() - var5.height()) / 2.0F,
               var7.getTextSecondary().getZenithColor().method7295(var7.getTextEnable().getZenithColor(), this.animationEnable.getValue()).method7294(var4)
            );
            var1.drawText(
               var6,
               "<",
               var2 + GuiStyle.PADDING.intValue() / 2.0F + var8 - var6.width("<") - GuiStyle.PADDING.intValue() / 2.0F,
               var3 + (this.getHeight() - var6.height()) / 2.0F,
               ZenithColor.INSTANCE8.method7295(var7.getPrimaryColor().getZenithColor(), this.animationEnable.getValue()).method7294(var4)
            );
         }
      }

      public float getHeight() {
         return 10.0F;
      }

      public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
         if (this.bounds != null && this.bounds.method8092(var1, var3, GuiStyle.PADDING.intValue() / 2.0F)) {
            this.value.toggle();
            return true;
         } else {
            return false;
         }
      }
   }
}

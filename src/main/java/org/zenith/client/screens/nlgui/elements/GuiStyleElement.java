package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import kotlin.collections.EmptyList;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiColorSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiItemSelectSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiKeySetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiModeSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiMultiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiNumberSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiStringSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiWindowSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiStyleElement extends Element {
   private static final float HEADER_HEIGHT = 23.0F;
   private static final int HEADER_SETTINGS_COUNT = 2;
   private final ZenithStyle style;
   private final Animation animationEnable = new Animation(200L, Easing.INSTANCE10);
   private final Animation expandedAnimation = new Animation(200L, Easing.INSTANCE10);
   private final Animation animationPosX = new Animation(150L, Easing.INSTANCE13);
   private final Animation animationPosY = new Animation(150L, Easing.INSTANCE13);
   private BoundsData bounds;
   private boolean expanded;
   private boolean animated = false;
   private boolean positionInitialized = false;
   private float lastX;
   private float lastY;
   private int lastIndex;
   private final List<GuiSetting<?>> settings = new ArrayList<>();

   public GuiStyleElement(ZenithStyle var1) {
      this.style = var1;
      float var2 = this.getWidth() - GuiStyle.PADDING * 4;

      for (Setting var4 : var1.getSettings()) {
         if (var4 instanceof SliderSetting var5) {
            this.settings.add(new GuiNumberSetting(var5, var2));
         } else if (var4 instanceof ModeSetting var6) {
            this.settings.add(new GuiModeSetting(var6, var2));
         } else if (var4 instanceof MultiSelectSetting var7) {
            this.settings.add(new GuiMultiBooleanSetting(var7, var2));
         } else if (var4 instanceof BooleanSetting var8) {
            this.settings.add(new GuiBooleanSetting(var8, var2));
         } else if (var4 instanceof ColorSetting var9) {
            this.settings.add(new GuiColorSetting(var9, var2));
         } else if (var4 instanceof ButtonSetting var10) {
            this.settings.add(new GuiButtonSetting(var10, var2));
         } else if (var4 instanceof BlockListSetting var11) {
            this.settings.add(new GuiItemSelectSetting(var11, var2));
         } else if (var4 instanceof BindSetting var12) {
            this.settings.add(new GuiKeySetting(var12, var2));
         } else if (var4 instanceof TextSetting var13) {
            this.settings.add(new GuiStringSetting(var13, var2));
         } else if (var4 instanceof GroupSetting var14) {
            this.settings.add(new GuiWindowSetting(var14, var2));
         }
      }
   }

   @Override
   public String getName() {
      return this.style.getName();
   }

   @Override
   public float getHeight() {
      this.expandedAnimation.method2807(this.expanded);
      float var1 = this.expandedAnimation.getValue();
      return 23.0F + this.getExpandedSettingsHeight() * var1;
   }

   public boolean hasSettings() {
      if (this.settings != null) {
         if (this.settings.getClass() == EmptyList.class) {
            return (1 == 0 && this.settings.stream().anyMatch(GuiSetting::isVisible));
         }
      }

      return (!this.settings.isEmpty() && this.settings.stream().anyMatch(GuiSetting::isVisible));
   }

   @Override
   public float getWidth() {
      return 178.0F;
   }

   public boolean isPriority() {
      return (this.expandedAnimation.getValue() > 0.01F);
   }

   public boolean isEnable() {
      return (Zenith.getInstance().getStyleManager().getCurrentStyle() == this.style);
   }

   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      this.expandedAnimation.method2807(this.expanded);
      float var7 = this.expandedAnimation.getValue();
      this.renderHeaderSettingsPriority(var1, var2, var3, var4, var5, var6);
      if (!(var7 <= 0.01F)) {
         float var10000 = var5 + 23.0F;
         float var9 = this.getExpandedSettingsHeight() * var7;
         float var10 = var4 + GuiStyle.PADDING * 2;
         float var11 = var5 + 23.0F + GuiStyle.PADDING * 2;
         float var12 = GuiStyle.PADDING * 2;
         float var13 = (float)(MathHelper.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * var6);

         for (int var14 = 2; var14 < this.settings.size(); var14++) {
            GuiSetting var15 = this.settings.get(var14);
            if (var15.isVisible()) {
               float var16 = var15.getAnimHeight() + 6.0F * var15.getVisibleProgress();
               MathHelper.clamp((var9 - var12) / Math.max(var16, 1.0E-4F), 0.0F, 1.0F);
               var15.renderPriority(var1, var2, var3, var10, var11, var6, var13);
               var11 += var16;
               var12 += var16;
            }
         }
      }
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      if (!this.positionInitialized) {
         this.animationPosX.reset(var4);
         this.animationPosY.reset(var5);
         this.lastX = var4;
         this.lastY = var5;
         this.positionInitialized = true;
         this.lastIndex = var7;
      } else if ((var4 != this.lastX || var5 != this.lastY) && var7 != this.lastIndex) {
         this.animated = true;
         this.animationPosX.method2810(var4);
         this.animationPosY.method2810(var5);
         this.lastX = var4;
         this.lastY = var5;
      }

      if (this.animated) {
         var4 = this.animationPosX.method2808(var4);
         var5 = this.animationPosY.method2808(var5);
         if (this.animationPosX.isDone() && this.animationPosY.isDone()) {
            this.animated = false;
         }
      } else {
         this.animationPosX.reset(var4);
         this.animationPosY.reset(var5);
      }

      this.lastIndex = var7;
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         this.expandedAnimation.method2807(this.expanded);
         float var9 = this.expandedAnimation.getValue();
         this.bounds = new BoundsData(var4, var5, this.getWidth(), 23.0F);
         this.animationEnable.method2807((this.style == var8));
         float var10 = this.bounds.width();
         float var11 = this.getHeight();
         var1.drawRoundedRectBatched(
            var4,
            var5,
            var10,
            var11,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            var8.getSurfaceDisableBackground().getZenithColor().method7294(var6)
         );
         var1.drawRoundedRectBatched(
            var4,
            var5,
            var10,
            23.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            var8.getHeaderDisableBackground()
               .getZenithColor()
               .method7295(var8.getSurfaceEnableBackground().getZenithColor(), this.animationEnable.getValue())
               .method7294(var6)
         );
         var1.flushRoundedRects();
         float var12 = 0.5F;
         float var13 = 2.0F;
         float var14 = 4.0F;
         var1.enableScissor(var4 + var14, var5 + 23.0F - 2.0F, var4 + var10 - var14, var5 + 23.0F + 10.0F);
         var1.drawRoundedBorder(
            var4,
            var5,
            var10,
            23.0F,
            0.15F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            GlowColor.method8096(
               this.style.getPrimaryColor().method1447(var6),
               this.style.getPrimaryColor().method1447(var6),
               this.style.getSecondaryPrimaryColor().method1447(var6),
               this.style.getSecondaryPrimaryColor().method1447(var6)
            )
         );
         var1.disableScissor();
         var14 = var4 + GuiStyle.PADDING * 2 + MathHelper.lerp(this.animationEnable.getValue(), 0, 5 + GuiStyle.PADDING);
         Font var15 = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font var16 = Fonts.NEW_ICONS.getFont(6.0F);
         var1.drawText(
            var15,
            var15.clip(this.style.getName(), var4 + 150.0F - GuiStyle.PADDING.intValue() - var14),
            var14,
            var5 + (23.0F - var15.height()) / 2.0F,
            var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), this.animationEnable.getValue()).method7294(var6)
         );
         var1.drawText(
            var16,
            "`",
            var4 + GuiStyle.PADDING * 2,
            var5 + (23.0F - var16.height()) / 2.0F,
            ZenithColor.INSTANCE8.method7295(var8.getPrimaryColor().getZenithColor(), this.animationEnable.getValue()).method7294(var6)
         );
         this.renderHeaderSettings(var1, var2, var3, var4, var5, var6);

         try {
            if (var9 <= 0.01F) {
               return;
            }

            float var17 = var5 + 23.0F;
            float var18 = this.getExpandedSettingsHeight() * var9;
            float var19 = var4 + GuiStyle.PADDING * 2;
            float var20 = var5 + 23.0F + GuiStyle.PADDING * 2;
            float var21 = GuiStyle.PADDING * 2;
            float var22 = (float)(MathHelper.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * var6);
            var1.enableScissor(var4, var17, var4 + var10, var17 + var18);

            for (int var23 = 2; var23 < this.settings.size(); var23++) {
               GuiSetting var24 = this.settings.get(var23);
               if (var24.isVisible()) {
                  float var25 = var24.getAnimHeight() + 6.0F * var24.getVisibleProgress();
                  float var26;
                  if ((var26 = MathHelper.clamp((var18 - var21) / Math.max(var25, 1.0E-4F), 0.0F, 1.0F)) > 0.0F) {
                     var24.render(var1, var2, var3, var19, var20, var22 * var26);
                  }

                  var20 += var25;
                  var21 += var25;
               }
            }

            var1.disableScissor();
         } catch (Exception var27) {
            var27.printStackTrace();
         }
      }
   }

   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      byte var6 = 0;

      for (int var7 = 0; var7 < this.settings.size(); var7++) {
         GuiSetting var8 = this.settings.get(var7);
         if (var8.isVisible() && var8.onMousePriorityClicked(var1, var3, var5)) {
            var6 = 1;
         }
      }

      return (var6 != 0);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      for (int var6 = Math.min(2, this.settings.size()) - 1; var6 >= 0; var6--) {
         GuiSetting var7 = this.settings.get(var6);
         if (var7.isVisible() && var7.onMouseClicked(var1, var3, var5)) {
            return true;
         }
      }

      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         if (var5 == Enum3.LEFT) {
            Zenith.getInstance().getStyleManager().setCurrentStyle(this.style);
         } else if (var5 == Enum3.RIGHT) {
            this.expanded = (!this.expanded);
         }

         return true;
      } else if (this.expandedAnimation.getValue() <= 0.01F) {
         return false;
      } else {
         for (int var8 = 2; var8 < this.settings.size(); var8++) {
            GuiSetting var9 = this.settings.get(var8);
            if (var9.isVisible() && var9.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      for (int var9 = 0; var9 < Math.min(2, this.settings.size()); var9++) {
         GuiSetting var10 = this.settings.get(var9);
         if (var10.isVisible() && var10.onMousePriorityScroll(var1, var3, var5, var7)) {
            return true;
         }
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         return false;
      } else {
         byte var12 = 0;

         for (int var13 = 2; var13 < this.settings.size(); var13++) {
            GuiSetting var11 = this.settings.get(var13);
            if (var11.isVisible() && var11.onMousePriorityScroll(var1, var3, var5, var7)) {
               var12 = 1;
            }
         }

         return (var12 != 0);
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      for (int var6 = 0; var6 < Math.min(2, this.settings.size()); var6++) {
         this.settings.get(var6).onMouseReleased(var1, var3, var5);
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         super.onMouseReleased(var1, var3, var5);
      } else {
         for (int var8 = 2; var8 < this.settings.size(); var8++) {
            GuiSetting var7 = this.settings.get(var8);
            var7.onMouseReleased(var1, var3, var5);
         }

         super.onMouseReleased(var1, var3, var5);
      }
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (int var4 = 0; var4 < Math.min(2, this.settings.size()); var4++) {
         GuiSetting var5 = this.settings.get(var4);
         if (var5.isVisible() && var5.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         return super.keyPressed(new KeyInput(var1, var2, var3));
      } else {
         for (int var6 = 2; var6 < this.settings.size(); var6++) {
            GuiSetting var7 = this.settings.get(var6);
            if (var7.isVisible() && var7.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         return super.keyPressed(new KeyInput(var1, var2, var3));
      }
   }

   @Override
   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (int var3 = 0; var3 < Math.min(2, this.settings.size()); var3++) {
         GuiSetting var4 = this.settings.get(var3);
         if (var4.isVisible() && var4.charTyped(var1, var2)) {
            return true;
         }
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         return super.charTyped(new CharInput(var1, var2));
      } else {
         for (int var5 = 2; var5 < this.settings.size(); var5++) {
            GuiSetting var6 = this.settings.get(var5);
            if (var6.isVisible() && var6.charTyped(var1, var2)) {
               return true;
            }
         }

         return super.charTyped(new CharInput(var1, var2));
      }
   }

   private float getExpandedSettingsHeight() {
      if (!this.hasSettings()) {
         return 0.0F;
      } else {
         float var1 = 0.0F;
         int var2 = 0;

         for (int var3 = 2; var3 < this.settings.size(); var3++) {
            GuiSetting var4 = this.settings.get(var3);
            if (var4.isVisible()) {
               var1 += var4.getAnimHeight() + 6.0F * var4.getVisibleProgress();
               var2++;
            }
         }

         return var2 == 0 ? 0.0F : GuiStyle.PADDING * 2 + var1 - 6.0F + GuiStyle.PADDING * 2;
      }
   }

   private void renderHeaderSettings(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float var8 = var4 + GuiStyle.PADDING * 2;
      float var9 = var5 + (23.0F - this.settings.getFirst().getHeight()) / 2.0F;
      var1.enableScissor(
         var4 + 150.0F, 0.0F, MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight()
      );
      GuiSetting var10 = this.settings.get(0);
      var10.render(var1, var2, var3, var8 - 10.0F, var9, var6);
      var10 = this.settings.get(1);
      var10.render(var1, var2, var3, var8, var9, var6);
      var1.disableScissor();
   }

   private void renderHeaderSettingsPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = (float)(MathHelper.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * var6);
      float var8 = var4 + GuiStyle.PADDING * 2;
      float var9 = var5 + 8.0F;

      for (int var10 = 0; var10 < Math.min(2, this.settings.size()); var10++) {
         GuiSetting var11 = this.settings.get(var10);
         if (var11.isVisible()) {
            var11.renderPriority(var1, var2, var3, var8, var9, var6, var7);
         }
      }
   }

   @Generated
   public ZenithStyle getStyle() {
      return this.style;
   }

   @Generated
   public void setPositionInitialized(boolean var1) {
      this.positionInitialized = var1;
   }
}

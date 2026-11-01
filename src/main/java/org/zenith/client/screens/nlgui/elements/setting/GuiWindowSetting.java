package org.zenith.client.screens.nlgui.elements.setting;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.input.KeyInput;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
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
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiWindowSetting extends GuiSetting<GroupSetting> {
   private final Animation animationExpanded = new Animation(200L, Easing.INSTANCE13);
   private final List<GuiSetting<?>> renderableSettings = new ArrayList<>();
   private BoundsData bounds;
   private BoundsData rectBounds;
   private BoundsData exitBounds;
   private boolean expanded;

   public GuiWindowSetting(GroupSetting var1) {
      this(var1, 166.0F);
   }

   public GuiWindowSetting(GroupSetting var1, float var2) {
      super(var2, var1);
      var2 = 182.0F - GuiStyle.PADDING * 4;

      for (Setting var4 : var1.getSettings()) {
         if (var4 instanceof SliderSetting var5) {
            this.renderableSettings.add(new GuiNumberSetting(var5, var2));
         } else if (var4 instanceof ModeSetting var6) {
            this.renderableSettings.add(new GuiModeSetting(var6, var2));
         } else if (var4 instanceof MultiSelectSetting var7) {
            this.renderableSettings.add(new GuiMultiBooleanSetting(var7, var2));
         } else if (var4 instanceof BooleanSetting var8) {
            this.renderableSettings.add(new GuiBooleanSetting(var8, var2));
         } else if (var4 instanceof ColorSetting var9) {
            this.renderableSettings.add(new GuiColorSetting(var9, var2));
         } else if (var4 instanceof ButtonSetting var10) {
            this.renderableSettings.add(new GuiButtonSetting(var10, var2));
         } else if (var4 instanceof BlockListSetting var11) {
            this.renderableSettings.add(new GuiItemSelectSetting(var11, var2));
         } else if (var4 instanceof BindSetting var12) {
            this.renderableSettings.add(new GuiKeySetting(var12, var2));
         } else if (var4 instanceof TextSetting var13) {
            this.renderableSettings.add(new GuiStringSetting(var13, var2));
         } else if (var4 instanceof GroupSetting var14) {
            this.renderableSettings.add(new GuiWindowSetting(var14, var2));
         }
      }
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         if (this.animationExpanded.getFloatValue2() == 0.0F) {
            this.expanded = true;
         } else {
            this.expanded = false;
         }

         return true;
      } else {
         return this.expanded;
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
         float var10 = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor var11 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var12 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(var1, var2, var3, "t", this.setting.getName(), this.setting.getDescription(), var8, var9, var4, var5, var10, var11, var12, var13);
         float var14 = 6.0F;
         float var15 = 6.0F;
         this.bounds = new BoundsData(var4 + this.width - var14, var5 + (this.getHeight() - var15) / 2.0F, var14, var15);
         var1.drawRoundedRect(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var14,
            var15,
            ProxyDotRadiusData.method8069(1.0F),
            var7.getFieldSurfaceBackground().getZenithColor().method7294(var6)
         );
         var1.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var14,
            var15,
            -0.5F,
            ProxyDotRadiusData.method8069(1.0F),
            var7.getFieldBorder().getZenithColor().method7294(var6)
         );
         Font var16 = Fonts.NEW_ICONS.getFont(5.5F);
         var1.drawText(
            var16,
            "w",
            this.bounds.getFloatValue() + (var14 - var16.width("w")) / 2.0F,
            this.bounds.getFloatValue2() + (var15 - var16.height()) / 2.0F,
            var7.getPrimaryColor().getZenithColor().method7294(var6)
         );
      }
   }

   @Override
   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.animationExpanded.method2807(this.expanded);
      if (!(this.animationExpanded.getValue() <= 0.0F)) {
         float var8 = GuiStyle.PADDING * 4 + this.getHeight();
         float var9 = GuiStyle.PADDING.intValue();
         float var10 = 182.0F;
         float var11 = 0.0F;

         for (GuiSetting var13 : this.renderableSettings) {
            if (var13.isVisible()) {
               var11 += var13.getAnimHeight() + 6.0F;
            }
         }

         if (var11 > 0.0F) {
            var11 -= 6.0F;
         }

         float var30 = var8 + var9 * 2.0F + var11;
         float var31 = this.bounds.getFloatValue() + GuiStyle.PADDING * 2;
         float var14 = this.bounds.getFloatValue2() - var30 / 2.0F;
         this.rectBounds = new BoundsData(var31, var14, var10, var30);
         var6 *= this.animationExpanded.getValue();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.bounds.getFloatValue(), this.bounds.getFloatValue2());
         var1.getMatrices().scale(this.animationExpanded.getValue(), this.animationExpanded.getValue());
         var1.getMatrices().translate(-this.bounds.getFloatValue(), -this.bounds.getFloatValue2());
         ZenithStyle var15 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var15 != null) {
            RenderShaders.method5289(
               MatrixUtils.method664(var1.getMatrices()),
               var31,
               var14,
               var10,
               var30,
               12.0F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               ZenithColor.INSTANCE.method7294(var6)
            );
            var1.drawRoundedRect(
               var31,
               var14 + var8,
               var10,
               var30 - var8,
               ProxyDotRadiusData.method8076(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               var15.getPanelLeftBackground().getZenithColor().method7294(var6)
            );
            var1.drawRoundedRect(
               var31,
               var14,
               var10,
               var8,
               ProxyDotRadiusData.method8074(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               var15.getRightBackground().getZenithColor().method7294(var6)
            );
            Font var16 = Fonts.NEW_ICONS.getFont(4.0F);
            float var17 = var31 + var10 - var16.width("2") - var9 * 2.0F;
            float var18 = var14 + var9 + var16.height();
            this.exitBounds = new BoundsData(var17, var18, 5.0F, 5.0F);
            var1.drawText(var16, "2", var17, var18, var15.getTextTertiary().getZenithColor().method7294(var6));
            Fonts.NEW_MEDIUM.getFont(5.5F);
            Font var20 = Fonts.NEW_MEDIUM.getFont(5.4F);
            Font var21 = Fonts.NEW_REGULAR.getFont(5.3F);
            ZenithColor var22 = var15.getTextEnable().getZenithColor().method7294(var6);
            ZenithColor var23 = var15.getTextSecondary().getZenithColor().method7294(var6);
            ZenithColor var24 = var15.getPrimaryColor().getZenithColor().method7294(var6);
            this.drawDefault(
               var1,
               var2,
               var3,
               "w",
               this.setting.getName(),
               this.setting.getDescription(),
               var20,
               var21,
               var31 + var9 * 2.0F,
               var14 + var9 * 2.0F,
               var10 / 2.0F,
               var22,
               var23,
               var24
            );
            float var25 = var31 + var9 * 2.0F;
            float var26 = var14 + var8 + var9;

            for (GuiSetting var28 : this.renderableSettings) {
               if (var28.isVisible()) {
                  var28.render(var1, var2, var3, var25, var26, var6);
                  var26 += var28.getAnimHeight() + 6.0F;
               }
            }

            var26 = var14 + var8 + var9;

            for (GuiSetting var34 : this.renderableSettings) {
               if (var34.isVisible()) {
                  var34.renderPriority(var1, var2, var3, var25, var26, var6, 1.0F);
                  var26 += var34.getAnimHeight() + 6.0F;
               }
            }

            var1.getMatrices().popMatrix();
         }
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      if (this.expanded && this.rectBounds != null) {
         byte var6 = 0;

         for (GuiSetting var8 : this.renderableSettings) {
            if (var8.isVisible() && var8.onMousePriorityClicked(var1, var3, var5)) {
               var6 = 1;
            }
         }

         if (var6 != 0) {
            return true;
         } else if (!this.rectBounds.contains(var1, var3)) {
            this.expanded = false;
            return false;
         } else if (this.exitBounds != null && this.exitBounds.method8092(var1, var3, 2.0F)) {
            this.expanded = false;
            return true;
         } else {
            for (GuiSetting var10 : this.renderableSettings) {
               if (var10.isVisible() && var10.onMouseClicked(var1, var3, var5)) {
                  System.out.println(var10);
                  return true;
               }
            }

            return true;
         }
      } else if (this.expanded) {
         this.expanded = false;
         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiSetting var5 : this.renderableSettings) {
         if (var5.isVisible() && var5.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(new KeyInput(var1, var2, var3));
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      for (GuiSetting var7 : this.renderableSettings) {
         if (var7.isVisible()) {
            var7.onMouseReleased(var1, var3, var5);
         }
      }

      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      if (!this.expanded) {
         return false;
      } else {
         this.expanded = false;

         for (GuiSetting var10 : this.renderableSettings) {
            if (var10.isVisible() && var10.onMousePriorityScroll(var1, var3, var5, var7)) {
               return true;
            }
         }

         return false;
      }
   }

   @Generated
   public void setExpanded(boolean var1) {
      this.expanded = var1;
   }
}

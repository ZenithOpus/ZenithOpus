package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import kotlin.collections.EmptyList;
import lombok.Generated;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
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
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiModuleElement extends Element {
   private final Module module;
   private final Animation animationEnable = new Animation(200L, Easing.INSTANCE10);
   private final Animation animationHovered = new Animation(200L, Easing.INSTANCE10);
   private final Animation bindAnimation = new Animation(300L, Easing.INSTANCE10);
   private final Animation searchVisibleAnimation = new Animation(180L, 1.0F, Easing.INSTANCE13);
   private float animationPosX = 0.0F;
   private float animationPosY = 0.0F;
   private BoundsData bounds;
   private BoundsData bindBounds;
   private boolean binding;
   private boolean animated = false;
   private boolean positionInitialized = false;
   private boolean searchVisible = true;
   private float lastX;
   private float lastY;
   private int lastIndex;
   private final List<GuiSetting<?>> settings = new ArrayList<>();

   public GuiModuleElement(Module var1) {
      this.module = var1;
      if (var1.isEnabled()) {
         this.animationEnable.method2809(1.0F);
      }

      if (var1.isRenderSetting()) {
         for (Setting var3 : var1.getSettings()) {
            if (var3 instanceof SliderSetting var4) {
               this.settings.add(new GuiNumberSetting(var4));
            } else if (var3 instanceof ModeSetting var5) {
               this.settings.add(new GuiModeSetting(var5));
            } else if (var3 instanceof MultiSelectSetting var6) {
               this.settings.add(new GuiMultiBooleanSetting(var6));
            } else if (var3 instanceof BooleanSetting var7) {
               this.settings.add(new GuiBooleanSetting(var7));
            } else if (var3 instanceof ColorSetting var8) {
               this.settings.add(new GuiColorSetting(var8));
            } else if (var3 instanceof ButtonSetting var9) {
               this.settings.add(new GuiButtonSetting(var9));
            } else if (var3 instanceof BlockListSetting var10) {
               this.settings.add(new GuiItemSelectSetting(var10));
            } else if (var3 instanceof BindSetting var11) {
               this.settings.add(new GuiKeySetting(var11));
            } else if (var3 instanceof TextSetting var12) {
               this.settings.add(new GuiStringSetting(var12));
            } else if (var3 instanceof GroupSetting var13) {
               this.settings.add(new GuiWindowSetting(var13));
            }
         }
      }
   }

   @Override
   public String getName() {
      return this.module.getName();
   }

   @Override
   public float getHeight() {
      return (float)(
         23.0
            + (
               this.hasSettings()
                  ? GuiStyle.PADDING * 2
                     + this.settings.stream().filter(GuiSetting::isVisible).mapToDouble(var0 -> var0.getAnimHeight() + 6.0F * var0.getVisibleProgress()).sum()
                     + -6.0
                     + GuiStyle.PADDING * 2
                  : 0.0
            )
      );
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
      return 182.0F;
   }

   public boolean isEnable() {
      return this.module.isEnabled();
   }

   public void setSearchVisible(boolean var1) {
      this.searchVisible = var1;
   }

   public void resetSearchVisible(boolean var1) {
      this.searchVisible = var1;
      this.searchVisibleAnimation.method2809(var1 ? 1.0F : 0.0F);
   }

   public float updateSearchVisible() {
      return this.searchVisibleAnimation.method2808(this.searchVisible ? 1.0F : 0.0F);
   }

   public float getSearchVisibleProgress() {
      return this.searchVisibleAnimation.getValue();
   }

   public boolean shouldRenderInSearch() {
      return !(!this.searchVisible && !(this.searchVisibleAnimation.getValue() > 0.01F));
   }

   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 + GuiStyle.PADDING * 2;
      float var8 = var5 + 23.0F + GuiStyle.PADDING * 2;
      float var9 = (float)(MathHelper.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * var6);
      float var10 = 6.0F;

      for (GuiSetting var12 : this.settings) {
         if (var12.isVisible()) {
            var12.renderPriority(var1, var2, var3, var7, var8, var6, var9);
            var8 += var12.getAnimHeight() + var10 * var12.getVisibleProgress();
         }
      }
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      this.render(var1, var2, var3, var4, var5, var6, var7, null);
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7, BoundsData var8) {
      if (!this.positionInitialized) {
         this.animationPosX = var4;
         this.animationPosY = var5;
         this.lastX = var4;
         this.lastY = var5;
         this.positionInitialized = true;
         this.lastIndex = var7;
      } else if ((var4 != this.lastX || var5 != this.lastY) && var7 != this.lastIndex) {
         this.animated = true;
         this.lastX = var4;
         this.lastY = var5;
      }

      if (this.animated) {
         this.animationPosX = Math.round(MathHelper.lerp(0.4F, this.animationPosX, var4));
         this.animationPosY = Math.round(MathHelper.lerp(0.4F, this.animationPosY, var5));
         if (Math.abs(this.animationPosX - var4) < 2.0F && Math.abs(this.animationPosY - var5) < 2.0F) {
            this.animated = false;
         } else {
            var4 = this.animationPosX;
            var5 = this.animationPosY;
         }
      } else {
         this.animationPosX = var4;
         this.animationPosY = var5;
      }

      this.lastIndex = var7;
      ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var9 != null) {
         this.bounds = new BoundsData(var4, var5, this.getWidth(), 23.0F);
         float var10 = this.bounds.width();
         float var11 = this.getHeight();
         boolean var12 = this.isEnable();
         this.animationEnable.method2807(var12);
         int var13 = !this.bounds.contains(var2, var3) || var8 != null && !var8.contains(var2, var3) ? 0 : 1;
         this.animationHovered.method2808(var12 ? 1.0F : (var13 != 0 ? 0.6F : 0.0F));
         float var14 = this.animationHovered.getValue();
         float var15 = var5 + GuiStyle.PADDING * 2;
         float var16 = MathHelper.lerp(this.animationEnable.getValue(), 0, 5 + GuiStyle.PADDING);
         ZenithColor var17 = var9.getPrimaryColor().getZenithColor();
         ZenithColor var18 = var9.getTextSecondary().getZenithColor();
         ZenithColor var19 = var9.getTextEnable().getZenithColor();
         var1.drawRoundedRectBatched(
            var4,
            var5,
            var10,
            var11,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            var9.getSurfaceDisableBackground().getZenithColor().method7294(var6)
         );
         var1.drawRoundedRectBatched(
            var4,
            var5,
            var10,
            23.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            var9.getHeaderDisableBackground()
               .getZenithColor()
               .method7295(var9.getSurfaceEnableBackground().getZenithColor(), this.animationEnable.getValue())
               .method7294(var6)
         );
         var1.flushRoundedRects();
         float var20 = var4 + GuiStyle.PADDING * 2 + var16;
         Font var21 = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font var22 = Fonts.NEW_ICONS.getFont(6.0F);
         float var23 = var4 + var10 - GuiStyle.PADDING * 3 - 12.0F - var20;
         var1.drawText(
            var21, var21.clip(this.module.getName(), var23), var20, var5 + (23.0F - var21.height()) / 2.0F, var18.method7295(var19, var14).method7294(var6)
         );
         var1.drawText(
            var22,
            "O",
            var4 + GuiStyle.PADDING * 2,
            var5 + (23.0F - var22.height()) / 2.0F,
            ZenithColor.INSTANCE8.method7295(var17, this.animationEnable.getValue()).method7294(var6)
         );
         float var24 = 12.0F;
         float var25 = 7.0F;
         float var26 = var4 + var10 - GuiStyle.PADDING * 2 - var24;
         var1.drawRoundedRectBatched(
            var26,
            var15,
            var24,
            var25,
            ProxyDotRadiusData.method8069(2.5F),
            var9.getDisableActiveBg().getZenithColor().method7295(var17, this.animationEnable.getValue()).method7294(var6)
         );
         float var27 = MathHelper.lerp(this.animationEnable.getValue(), 1.0F, var24 - 1.0F - 5.0F);
         var1.drawRoundedRectBatched(
            var26 + var27,
            var15 + 1.0F,
            5.0F,
            5.0F,
            ProxyDotRadiusData.method8069(1.5F),
            var9.getTextTertiary().getZenithColor().method7295(var19, var14).method7294(var6)
         );
         String var35 = this.getBindText();
         this.bindAnimation.method2807(!(!this.binding && this.module.getKeyCode() == -1));
         Font var28 = Fonts.NEW_MEDIUM.getFont(4.5F);
         Font var29 = Fonts.NEW_ICONS.getFont(4.2F);
         float var30 = var28.width(var35);
         float var31 = GuiStyle.PADDING.intValue() / 2.0F + var30 + GuiStyle.PADDING.intValue() / 3.0F + 3.75F + GuiStyle.PADDING.intValue() / 2.0F;
         float var32 = var26 - GuiStyle.PADDING.intValue() - 7.0F - GuiStyle.PADDING.intValue() / 2.0F - var31;
         this.bindBounds = new BoundsData(var32, var15, var31, var25);
         var1.drawRoundedRectBatched(
            var32,
            var15,
            var31,
            var25,
            ProxyDotRadiusData.method8069(1.5F),
            var9.getDisableActiveBg().getZenithColor().method7295(var17.method7294(0.15F), this.bindAnimation.getValue()).method7294(var6)
         );
         var1.flushRoundedRects();
         var1.drawText(
            var28,
            var35,
            var32 + GuiStyle.PADDING.intValue() / 2.0F,
            var15 + (var25 - var28.height()) / 2.0F,
            var9.getTextTertiary().getZenithColor().method7295(var17, this.bindAnimation.getValue()).method7294(var6)
         );
         var1.drawText(
            var29,
            "N",
            var32 + GuiStyle.PADDING.intValue() / 2.0F + var30 + GuiStyle.PADDING.intValue() / 3.0F,
            var15 + GuiStyle.PADDING.intValue() / 2.0F,
            var9.getTextTertiary().getZenithColor().method7295(var17, this.bindAnimation.getValue()).method7294(var6)
         );

         try {
            var27 = var4 + GuiStyle.PADDING * 2;
            float var37 = var5 + 23.0F + GuiStyle.PADDING * 2;
            float var38 = MathHelper.lerp(this.animationEnable.getValue(), MathHelper.lerp(this.animationHovered.getValue(), 0.5F, 1.0F), 1.0F) * var6;
            var30 = 6.0F;

            for (GuiSetting var41 : this.settings) {
               if (var41.isVisible()) {
                  var41.render(var1, var2, var3, var27, var37, var38);
                  var37 += var41.getAnimHeight() + var30 * var41.getVisibleProgress();
               }
            }
         } catch (Exception var34) {
            var34.printStackTrace();
         }
      }
   }

   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      byte var6 = 0;

      for (GuiSetting var8 : this.settings) {
         if (var8.isVisible() && var8.onMousePriorityClicked(var1, var3, var5)) {
            var6 = 1;
         }
      }

      return (var6 != 0);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bindBounds != null && this.bindBounds.contains(var1, var3)) {
         this.binding = true;
         return true;
      } else if (this.binding && var5.getIntValue() > 2) {
         this.module.setKeyCode(var5.getIntValue());
         this.binding = false;
         return true;
      } else if (this.bounds != null && this.bounds.contains(var1, var3)) {
         if (var5 == Enum3.LEFT || var5 == Enum3.RIGHT) {
            this.module.toggle();
         } else if (var5 == Enum3.MIDDLE) {
            this.binding = true;
         }

         return true;
      } else {
         for (GuiSetting var7 : this.settings) {
            if (var7.isVisible() && var7.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      byte var9 = 0;

      for (GuiSetting var11 : this.settings) {
         if (var11.isVisible() && var11.onMousePriorityScroll(var1, var3, var5, var7)) {
            var9 = 1;
         }
      }

      return (var9 != 0);
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      for (GuiSetting var7 : this.settings) {
         var7.onMouseReleased(var1, var3, var5);
      }

      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.binding) {
         for (GuiSetting var5 : this.settings) {
            if (var5.isVisible() && var5.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         return super.keyPressed(new KeyInput(var1, var2, var3));
      } else {
         if (var1 != 256 && var1 != 261 && var1 != 259) {
            this.module.setKeyCode(var1);
         } else {
            this.module.setKeyCode(-1);
         }

         this.binding = false;
         return true;
      }
   }

   @Override
   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (GuiSetting var4 : this.settings) {
         if (var4.isVisible() && var4.charTyped(var1, var2)) {
            return true;
         }
      }

      return super.charTyped(new CharInput(var1, var2));
   }

   private String getBindText() {
      if (this.binding) {
         return this.getBindingDots();
      } else {
         String var1 = "n/a";
         int var2;
         if ((var2 = this.module.getKeyCode()) != -1 && var2 != 0) {
            try {
               String var3 = KeyName.method6701(var2);
               if (var3 != null && !var3.isBlank()) {
                  var1 = var3.toUpperCase();
               }
            } catch (Exception var4) {
            }
         }

         return var1;
      }
   }

   private String getBindingDots() {
      int var1;
      if ((var1 = (int)(System.currentTimeMillis() / 500L % 3L)) == 0) {
         return ".";
      } else {
         return var1 == 1 ? ".." : "...";
      }
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public void setPositionInitialized(boolean var1) {
      this.positionInitialized = var1;
   }
}

package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.ColorPicker;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ColorSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiColorSetting extends GuiSetting<ColorSetting> {
   private static final float BOX_SIZE = 4.0F;
   private final ColorPicker picker;
   private final Animation animationExpanded = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private boolean expanded;
   private float pickerHeight;
   private BoundsData rowBounds;
   private BoundsData boxBounds;

   public GuiColorSetting(ColorSetting var1) {
      this(var1, 166.0F);
   }

   public GuiColorSetting(ColorSetting var1, float var2) {
      super(var2, var1);
      this.picker = new ColorPicker(var1);
      this.pickerHeight = this.picker.height(var2);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public float getHeight() {
      return this.rowHeight() + this.pickerHeight * this.animationExpanded.getValue();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.expanded && this.picker.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.rowBounds != null && this.rowBounds.contains(var1, var3)) {
         this.expanded = (!this.expanded);
         if (!this.expanded) {
            this.picker.remember();
         }

         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      this.picker.onMouseReleased();
      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return (boolean)(this.expanded && this.picker.keyPressed(var1, var2, var3) ? 1 : super.keyPressed(new KeyInput(var1, var2, var3)));
   }

   @Override
   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return (boolean)(this.expanded && this.picker.charTyped(var1, var2) ? 1 : super.charTyped(new CharInput(var1, var2)));
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         var6 *= this.animationVisible.getValue();
         float var8 = this.rowHeight();
         this.rowBounds = new BoundsData(var4, var5, this.width, var8);
         Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var10 = Fonts.NEW_REGULAR.getFont(5.4F);
         float var11 = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor var12 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var14 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(var1, var2, var3, "x", this.setting.getName(), this.setting.getDescription(), var9, var10, var4, var5, var11, var12, var13, var14);
         this.boxBounds = new BoundsData(var4 + this.width - 4.0F, var5 + (var8 - 4.0F) / 2.0F, 4.0F, 4.0F);
         float var15 = 1.5F;
         ZenithColor var16 = this.setting.method1447(var6);
         ZenithColor var17 = ZenithColor.INSTANCE9.method7294(var6);
         var1.drawRoundedBorder(
            this.boxBounds.getFloatValue() - var15,
            this.boxBounds.getFloatValue2() - var15,
            4.0F + var15 * 2.0F,
            4.0F + var15 * 2.0F,
            0.1F,
            ProxyDotRadiusData.method8069(3.0F),
            var16
         );
         var1.drawRoundedRect(
            this.boxBounds.getFloatValue(),
            this.boxBounds.getFloatValue2(),
            4.0F,
            4.0F,
            ProxyDotRadiusData.method8069(3.0F),
            GlowColor.method8096(var17, var16, var16, var17)
         );
         float var18;
         if (!((var18 = this.animationExpanded.method2808(this.expanded ? 1.0F : 0.0F)) <= 0.001F)) {
            float var19 = var5 + var8;
            this.pickerHeight = this.picker.height(this.width);
            float var20 = this.pickerHeight * var18;
            var1.enableScissor(var4, var19, var4 + this.width, var19 + var20);
            this.picker.render(var1, var2, var3, var4, var19, this.width, var6);
            var1.disableScissor();
         }
      }
   }
}

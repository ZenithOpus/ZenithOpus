package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.math.Vector2f;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiStringSetting extends GuiSetting<TextSetting> {
   private final Animation focusAnimation = new Animation(200L, Easing.INSTANCE13);
   private BoundsData bounds;
   private SearchBox textBox;

   public GuiStringSetting(TextSetting var1) {
      super(166.0F, var1);
   }

   public GuiStringSetting(TextSetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds != null && this.bounds.contains(var1, var3) && var5 == Enum3.LEFT) {
         this.getBox().setSelected(true);
         return true;
      } else {
         if (!this.getBox().onMouseClicked(var1, var3, var5)) {
            this.setting.method2122(this.getBox().getText());
         }

         return false;
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         var6 *= this.animationVisible.getValue();
         SearchBox var8 = this.getBox();
         if (!var8.isSelected()) {
            var8.setText(this.setting.getValue());
            var8.setCursor(this.setting.getValue().length());
         }

         Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var10 = Fonts.NEW_REGULAR.getFont(5.4F);
         float var11 = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ZenithColor var12 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var14 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(var1, var2, var3, "M", this.setting.getName(), this.setting.getDescription(), var9, var10, var4, var5, var11, var12, var13, var14);
         float var15 = this.width / 2.0F;
         float var16 = this.getHeight();
         this.bounds = new BoundsData(var4 + this.width - var15, var5 + (this.getHeight() - var16) / 2.0F, var15, var16);
         this.focusAnimation.method2807(!(!var8.isSelected() && !this.bounds.contains(var2, var3)));
         var1.drawRoundedRect(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var15,
            var16,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            var7.getFieldSurfaceBackground().getZenithColor().method7294(var6)
         );
         var1.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var15,
            var16,
            0.1F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            var7.getFieldBorder()
               .getZenithColor()
               .method7295(var7.getPrimaryColor().getZenithColor().method7294(0.5F), this.focusAnimation.getValue())
               .method7294(var6)
         );
         Font var17 = Fonts.NEW_MEDIUM.getFont(5.3F);
         var8.setFont(var17);
         var8.setWidth(var15 - GuiStyle.PADDING.intValue() * 2.0F);
         var8.setMaxLength(this.setting.getEntry().getMaxLength());
         var8.setMasked(this.setting.isFlag());
         var8.setText2(Zenith.getInstance().getLocalization().translate(this.setting.getText()));
         var8.method987(
            var1,
            this.bounds.getFloatValue() + GuiStyle.PADDING.intValue(),
            this.bounds.getFloatValue2() + (var16 - var17.height()) / 2.0F,
            var7.getTextEnable().getZenithColor().method7294(var6),
            var7.getTextTertiary().getZenithColor().method7294(var6)
         );
      }
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      SearchBox var4 = this.getBox();
      if (var4.isSelected()) {
         if (var1 == 257) {
            this.setting.method2122(var4.getText());
            var4.setSelected(false);
            return true;
         }

         if (var1 == 256) {
            var4.setSelected(false);
            return true;
         }
      }

      boolean var5 = var4.keyPressed(var1, var2, var3);
      this.pushLive(var4);
      return var5;
   }

   @Override
   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      SearchBox var3 = this.getBox();
      boolean var4 = var3.charTyped(var1, var2);
      this.pushLive(var3);
      return var4;
   }

   private void pushLive(SearchBox var1) {
      if (this.setting.isLive()) {
         this.setting.method2122(var1.getText());
      }
   }

   @Override
   public float getHeight() {
      return 14.0F;
   }

   private SearchBox getBox() {
      if (this.textBox == null) {
         this.textBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.3F), this.setting.getText(), 40.0F);
      }

      return this.textBox;
   }
}

package org.zenith.client.screens.nlgui.elements.setting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiNumberSetting extends GuiSetting<SliderSetting> {
   private static final double HANDLE_GRAB = 4.0;
   private static final double FINE_FACTOR = 0.25;
   private static final long SHAKE_DURATION = 320L;
   private final Animation hoverAnimation = new Animation(160L, Easing.INSTANCE12);
   private final Animation dragAnimation = new Animation(160L, Easing.INSTANCE12);
   private final Animation focusAnimation = new Animation(180L, Easing.INSTANCE13);
   private final Animation numberAnimation;
   private final Animation boxWidthAnimation = new Animation(160L, -1.0F, Easing.INSTANCE12);
   private final Animation editAnimation = new Animation(220L, Easing.INSTANCE15);
   private final Animation commitFlash = new Animation(420L, Easing.INSTANCE12);
   private SearchBox valueBox;
   private BoundsData bounds;
   private BoundsData selectedBounds;
   private boolean selected;
   private boolean editing;
   private double anchorPercent;
   private double anchorMouseX;
   private boolean fineDrag;
   private long shakeStart = -1L;
   public float applayValue;

   public GuiNumberSetting(SliderSetting var1) {
      this(var1, 166.0F);
   }

   public GuiNumberSetting(SliderSetting var1, float var2) {
      super(var2, var1);
      this.numberAnimation = new Animation(220L, var1.getValue3(), Easing.INSTANCE27);
      this.applayValue = var1.getValue3();
   }

   public float getApplayValue() {
      return !this.selected && !this.editing ? this.setting.getValue3() : this.applayValue;
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (var5 != Enum3.LEFT) {
         return false;
      } else if (this.bounds != null && this.valueBox != null && this.bounds.method8092(var1, var3, 1.5F)) {
         if (!this.editing) {
            this.beginEditing();
         } else if (!this.valueBox.onMouseClicked(var1, var3, var5)) {
            this.valueBox.setSelected(true);
         }

         return true;
      } else {
         if (this.editing) {
            this.commitEditing();
         }

         if (this.selectedBounds != null && this.selectedBounds.method8092(var1, var3, 3.0F)) {
            this.selected = true;
            this.fineDrag = this.isFineDrag();
            double var6 = this.pointerAt(var1);
            double var8 = this.getProgress(this.setting.getValue3());
            this.anchorPercent = Math.abs(var6 - var8) * this.selectedBounds.width() <= 4.0 ? var8 : var6;
            this.anchorMouseX = var1;
            Zenith.getInstance().getNLMenuScreen().lockGuiScale();
            this.updateSlider(var1);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      this.selected = false;
      if (this.editing) {
         this.updateInputPreview();
      } else {
         this.applayValue = this.setting.getValue3();
      }
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.editing) {
         return false;
      } else if (var1 == 257 || var1 == 335) {
         this.commitEditing();
         return true;
      } else if (var1 == 256) {
         this.cancelEditing();
         return true;
      } else if (var1 != 265 && var1 != 264) {
         boolean var9 = this.valueBox.keyPressed(var1, var2, var3);
         this.updateInputPreview();
         return var9;
      } else {
         Float var4 = this.parseInput(this.valueBox.getText());
         float var5 = var4 != null ? this.normalizeValue(var4.floatValue()) : this.applayValue;
         float var6 = Math.abs(this.setting.getFloatValue2()) * ((var3 & 1) != 0 ? 10.0F : 1.0F);
         float var7 = this.normalizeValue(var5 + (var1 == 265 ? var6 : -var6));
         String var8 = this.formatValue(var7);
         this.valueBox.setText(var8);
         this.valueBox.setCursor(var8.length());
         this.valueBox.setFlag(false);
         this.applayValue = var7;
         return true;
      }
   }

   @Override
   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (!this.editing) {
         return false;
      } else {
         boolean var3 = this.valueBox.charTyped(var1, var2);
         this.updateInputPreview();
         return var3;
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      if (this.valueBox == null) {
         this.valueBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.5F), "", 40.0F);
         this.valueBox.setType(SearchBox.Type.DECIMAL_NUMBER);
         this.valueBox.setMaxLength(32);
      }

      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         if (!this.editing && !this.selected) {
            this.applayValue = this.setting.getValue3();
         }

         this.animationVisible.method2807(this.setting.isVisible());
         var6 *= this.animationVisible.getValue();
         Font var8 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var9 = Fonts.NEW_REGULAR.getFont(5.4F);
         float var10 = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ZenithColor var11 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var12 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(var1, var2, var3, "u", this.setting.getName(), this.setting.getDescription(), var8, var9, var4, var5, var10, var11, var12, var13);
         float var14 = this.editing ? this.applayValue : this.setting.getValue3();
         float var15;
         if (this.selected) {
            this.numberAnimation.method2809(var14);
            var15 = var14;
         } else {
            var15 = this.numberAnimation.method2808(var14);
         }

         this.editAnimation.method2807(this.editing);
         this.commitFlash.method2808(0.0F);
         String var16 = this.editing ? this.valueBox.getText() : this.formatValue(this.selected ? var14 : var15);
         if (!this.editing) {
            this.valueBox.setText(var16);
            this.valueBox.setCursor(var16.length());
         }

         float var17 = var8.width(this.setting.getSuffix());
         float var18 = Math.max(var8.width(this.formatValue(this.setting.getMin())), var8.width(this.formatValue(this.setting.getMax())));
         float var19;
         float var20 = Math.min(var19 = Math.max(12.0F, var10 - GuiStyle.PADDING.intValue() - 12.0F), GuiStyle.PADDING.intValue() + var18 + var17 + 1.0F);
         float var21 = Math.min(var19, GuiStyle.PADDING.intValue() + Math.max(this.editing ? 8.0F : 4.0F, var8.width(var16)) + var17 + 1.0F);
         if (this.boxWidthAnimation.getValue() < 0.0F) {
            this.boxWidthAnimation.method2809(var21);
         }

         float var22 = this.boxWidthAnimation.method2808(var21);
         float var23 = 9.0F;
         float var24 = var4 + this.width;
         float var25 = this.shakeOffset();
         this.bounds = new BoundsData(var24 - var22, var5 + (this.getHeight() - var23) / 2.0F, var22, var23);
         boolean var26 = this.bounds.method8092(var2, var3, 1.5F);
         this.focusAnimation.method2807(this.editing || var26);
         float var27 = this.focusAnimation.getValue();
         float var28 = this.editAnimation.getValue();
         float var29 = this.commitFlash.getValue();
         float var30 = Math.max(var27, var29);
         ZenithColor var31 = var7.getPrimaryColor().getZenithColor();
         if (var30 > 0.01F) {
            ZenithColor var32 = var7.getFieldSurfaceBackground()
               .getZenithColor()
               .method7295(var31.method7294(0.14F + 0.08F * var28), var28)
               .method7295(var31.method7294(0.4F), var29)
               .method7294(var6 * var30);
            ZenithColor var33 = var7.getFieldBorder().getZenithColor().method7295(var31.method7294(0.8F), var28).method7294(var6 * var30);
            float var34 = 0.85F + 0.15F * var30;
            float var35 = var22 * var34;
            float var36 = var24 - var35 + var25;
            var1.drawRoundedRectBatched(var36, this.bounds.getFloatValue2(), var35, var23, ProxyDotRadiusData.method8069(1.5F), var32);
            var1.flushRoundedRects();
            var1.drawRoundedBorder(var36, this.bounds.getFloatValue2(), var35, var23, 0.1F, ProxyDotRadiusData.method8069(1.5F), var33);
            if (var28 > 0.01F) {
               float var37 = (var35 - 3.0F) * var28;
               var1.drawRoundedRectBatched(
                  var36 + (var35 - var37) / 2.0F,
                  this.bounds.getFloatValue2() + var23 - 0.9F,
                  var37,
                  0.6F,
                  ProxyDotRadiusData.method8069(0.3F),
                  var31.method7294(var6 * var28)
               );
               var1.flushRoundedRects();
            }
         }

         float var50 = this.bounds.getFloatValue2() + (var23 - var8.height()) / 2.0F;
         float var51 = var24 - GuiStyle.PADDING.intValue() / 2.0F - var17 + var25;
         float var52 = Math.max(1.0F, var22 - GuiStyle.PADDING.intValue() - var17 - 1.0F);
         float var53 = var51 - var8.width(var16) - (var17 > 0.0F ? 0.6F : 0.0F);
         float var54 = this.bounds.getFloatValue() + GuiStyle.PADDING.intValue() / 2.0F + var25;
         float var55 = MathHelper.lerp(var28, var53, var54);
         ZenithColor var38 = var7.getTextSecondary()
            .getZenithColor()
            .method7295(var7.getTextEnable().getZenithColor(), Math.max(Math.max(var30, var28), this.dragAnimation.getValue()))
            .method7295(var31, var29 * 0.6F)
            .method7294(var6);
         this.valueBox.setFont(var8);
         this.valueBox.setWidth(var52);
         this.valueBox.method987(var1, var55, var50, var38, var31.method7294(0.7F * var6));
         var1.drawText(
            var8,
            this.setting.getSuffix(),
            var51,
            var50,
            var7.getTextTertiary().getZenithColor().method7295(var7.getTextSecondary().getZenithColor(), var30).method7294(var6)
         );
         float var39 = Math.max(1.0F, var10 - var20 - GuiStyle.PADDING.intValue());
         float var40 = 2.0F;
         float var41 = var4 + this.width - var20 - var39 - GuiStyle.PADDING.intValue();
         float var42 = var5 + (this.getHeight() - var40) / 2.0F;
         this.selectedBounds = new BoundsData(var41, var42, var39, var40);
         this.updateSlider(var2);
         this.hoverAnimation.method2807(this.selected || this.selectedBounds.method8092(var2, var3, 3.0F));
         this.dragAnimation.method2807(this.selected);
         float var43 = this.hoverAnimation.getValue();
         float var44 = this.dragAnimation.getValue();
         float var45 = this.getProgress(this.selected ? this.applayValue : var15);
         float var46 = var39 * var45;
         ZenithColor var47 = var7.getFieldSurfaceBackground()
            .getZenithColor()
            .method7295(var7.getFieldBorder().getZenithColor(), 0.35F * var43)
            .method7294(var6);
         ZenithColor var48 = var31.method7294(0.82F + 0.18F * Math.max(var43, var44)).method7294(var6);
         var1.drawRoundedRectBatched(var41, var42, var39, var40, ProxyDotRadiusData.method8069(0.04F), var47);
         if (var46 > 0.01F) {
            var1.drawRoundedRectBatched(var41, var42, var46, var40, ProxyDotRadiusData.method8078(0.04F, 0.04F), var48);
         }

         var1.flushRoundedRects();
      }
   }

   private double pointerAt(double var1) {
      return this.selectedBounds != null && !(this.selectedBounds.width() <= 0.0F)
         ? MathHelper.clamp((var1 - this.selectedBounds.getFloatValue()) / this.selectedBounds.width(), 0.0, 1.0)
         : 0.0;
   }

   private boolean isFineDrag() {
      return InputUtil.isKeyPressed(mc.getWindow(), 340) || InputUtil.isKeyPressed(mc.getWindow(), 344);
   }

   private double dragPercent(double var1) {
      double var3 = this.fineDrag ? 0.25 : 1.0;
      return this.anchorPercent + (var1 - this.anchorMouseX) / this.selectedBounds.width() * var3;
   }

   public void updateSlider(double var1) {
      if (this.selected && this.selectedBounds != null && !(this.selectedBounds.width() <= 0.0F)) {
         boolean var3;
         if ((var3 = this.isFineDrag()) != this.fineDrag) {
            this.anchorPercent = MathHelper.clamp(this.dragPercent(var1), 0.0, 1.0);
            this.anchorMouseX = var1;
            this.fineDrag = var3;
         }

         double var4;
         if ((var4 = this.dragPercent(var1)) < 0.0 || var4 > 1.0) {
            this.anchorPercent = MathHelper.clamp(var4, 0.0, 1.0);
            this.anchorMouseX = var1;
         }

         double var6 = MathHelper.clamp(var4, 0.0, 1.0);
         float var8 = this.normalizeValue(this.setting.getMin() + (this.setting.getMax() - this.setting.getMin()) * var6);
         this.applayValue = var8;
         if (Float.compare(this.setting.getValue3(), var8) != 0) {
            Zenith.getInstance().getClientSounds().method1571(this.getProgress(var8));
            this.setting.method2400(var8);
         }
      }
   }

   @Override
   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   private void beginEditing() {
      this.editing = true;
      this.selected = false;
      String var1 = this.formatValue(this.setting.getValue3());
      this.valueBox.setText(var1);
      this.valueBox.setCursor(var1.length());
      this.valueBox.setSelected(true);
      this.valueBox.setFlag(true);
      this.applayValue = this.setting.getValue3();
   }

   private void commitEditing() {
      String var1 = this.valueBox.getText();
      Float var2 = this.parseInput(var1);
      if (var2 != null) {
         float var3 = this.normalizeValue(var2.floatValue());
         if (Float.compare(this.setting.getValue3(), var3) != 0) {
            this.setting.method2400(var3);
            Zenith.getInstance().getClientSounds().method1571(this.getProgress(var3));
         }

         this.commitFlash.method2809(1.0F);
         this.finishEditing();
      } else {
         if (var1 != null && !var1.isBlank()) {
            this.shakeStart = System.currentTimeMillis();
         } else {
            this.finishEditing();
         }
      }
   }

   private void cancelEditing() {
      this.finishEditing();
   }

   private void finishEditing() {
      this.editing = false;
      this.valueBox.setSelected(false);
      this.valueBox.setFlag(false);
      this.applayValue = this.setting.getValue3();
      String var1 = this.formatValue(this.setting.getValue3());
      this.valueBox.setText(var1);
      this.valueBox.setCursor(var1.length());
   }

   private float shakeOffset() {
      if (this.shakeStart < 0L) {
         return 0.0F;
      } else {
         long var1;
         if ((var1 = System.currentTimeMillis() - this.shakeStart) >= 320L) {
            this.shakeStart = -1L;
            return 0.0F;
         } else {
            float var3;
            return (float)Math.sin((var3 = (float)var1 / 320.0F) * Math.PI * 6.0) * 1.6F * (1.0F - var3);
         }
      }
   }

   private void updateInputPreview() {
      this.sanitizeInput();
      Float var1 = this.parseInput(this.valueBox.getText());
      if (var1 != null) {
         this.applayValue = this.normalizeValue(var1.floatValue());
      }
   }

   private void sanitizeInput() {
      String var1 = this.valueBox.getText();
      int var2 = MathHelper.clamp(this.valueBox.getCursor(), 0, var1.length());
      int var3 = 0;
      boolean var4 = false;
      StringBuilder var5 = new StringBuilder(var1.length());

      for (int var6 = 0; var6 < var1.length(); var6++) {
         char var7;
         boolean var8 = Character.isDigit(var7 = var1.charAt(var6));
         if ((var7 == '.' || var7 == ',') && !var4) {
            var7 = '.';
            var4 = true;
            var8 = true;
         } else if (var7 == '-' && var5.isEmpty() && this.setting.getMin() < 0.0F) {
            var8 = true;
         }

         if (var8) {
            var5.append(var7);
            if (var6 < var2) {
               var3++;
            }
         }
      }

      String var9 = var5.toString();
      if (!var9.equals(var1)) {
         this.valueBox.setText(var9);
         this.valueBox.setCursor(var3);
      }
   }

   private Float parseInput(String var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.trim().replace(',', '.');
         if (!var2.isEmpty() && !var2.equals("-") && !var2.equals(".") && !var2.equals("-.")) {
            Float var10000;
            try {
               float var3;
               var10000 = Float.isFinite(var3 = Float.parseFloat(var2)) ? var3 : null;
            } catch (NumberFormatException var4) {
               return null;
            }

            return var10000;
         } else {
            return null;
         }
      }
   }

   private float normalizeValue(double var1) {
      double var3 = Math.min(this.setting.getMin(), this.setting.getMax());
      double var5 = Math.max(this.setting.getMin(), this.setting.getMax());
      double var7 = Math.abs(this.setting.getFloatValue2());
      double var9 = var1;
      if (var7 > 0.0 && Double.isFinite(var7)) {
         var9 = BigDecimal.valueOf(Math.round((var1 - var3) / var7) * var7 + var3).setScale(this.getDecimals(), RoundingMode.HALF_UP).doubleValue();
      }

      return (float)MathHelper.clamp(var9, var3, var5);
   }

   private float getProgress(float var1) {
      float var2;
      return (var2 = this.setting.getMax() - this.setting.getMin()) == 0.0F ? 0.0F : MathHelper.clamp((var1 - this.setting.getMin()) / var2, 0.0F, 1.0F);
   }

   private String formatValue(float var1) {
      int var2 = this.getDecimals();
      double var3 = 0.5 * Math.pow(10.0, -var2);
      return BigDecimal.valueOf(Math.abs(var1) < var3 ? 0.0 : var1).setScale(var2, RoundingMode.HALF_UP).toPlainString();
   }

   private int getDecimals() {
      return Float.isFinite(this.setting.getFloatValue2()) && this.setting.getFloatValue2() != 0.0F
         ? MathHelper.clamp(new BigDecimal(Float.toString(Math.abs(this.setting.getFloatValue2()))).stripTrailingZeros().scale(), 0, 8)
         : 0;
   }
}

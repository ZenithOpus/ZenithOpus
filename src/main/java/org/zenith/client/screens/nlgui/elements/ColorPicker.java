package org.zenith.client.screens.nlgui.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ColorSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ColorPicker {
   private static final float TOP_PAD = 4.0F;
   private static final float BOTTOM_PAD = 6.0F;
   private static final float GAP = 5.0F;
   private static final float BAR_GAP = 6.0F;
   private static final float SECTION_GAP = 6.0F;
   private static final float BAR_HEIGHT = 4.0F;
   private static final float ROW_HEIGHT = 11.0F;
   private static final float CAPTION_HEIGHT = 8.0F;
   private static final float BUTTON = 11.0F;
   private static final float BUTTON_GAP = 3.0F;
   private static final float SWATCH = 7.0F;
   private static final float SWATCH_GAP = 2.0F;
   private static final float MIN_FIELD = 30.0F;
   private static final float MAX_FIELD = 56.0F;
   private static final int PALETTE_LIMIT = 12;
   private static final int RECENT_LIMIT = 6;
   private static final List<ZenithColor> RECENT = new ArrayList<>();
   private final ColorSetting setting;
   private final List<ZenithColor> palette = new ArrayList<>();
   private final List<BoundsData> paletteBounds = new ArrayList<>();
   private SearchBox hexField;
   private float hue;
   private float saturation;
   private float brightness;
   private int alphaValue = 255;
   private ZenithColor applied;
   private ColorPicker.Drag drag = ColorPicker.Drag.NONE;
   private BoundsData bounds;
   private BoundsData fieldBounds;
   private BoundsData hueBounds;
   private BoundsData alphaBounds;
   private BoundsData hexBounds;
   private BoundsData copyBounds;
   private BoundsData pasteBounds;
   private BoundsData resetBounds;

   public ColorPicker(ColorSetting var1) {
      this.setting = var1;
      this.syncFromSetting();
   }

   public float height(float var1) {
      float var2 = this.paletteRows(var1);
      return 4.0F + this.fieldHeight(var1) + 5.0F + 4.0F + 6.0F + 4.0F + 6.0F + 11.0F + 6.0F + 8.0F + var2 * 7.0F + (var2 - 1.0F) * 2.0F + 6.0F;
   }

   public boolean contains(double var1, double var3) {
      return (this.bounds != null && this.bounds.contains(var1, var3));
   }

   public void remember() {
      remember(this.setting.getZenithColor());
      if (this.hexField != null) {
         this.hexField.setSelected(false);
      }

      this.drag = ColorPicker.Drag.NONE;
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null && !(var7 <= 0.001F) && !(var6 <= 0.0F)) {
         this.ensureField();
         this.syncFromSetting();
         this.applyDrag(var2, var3);
         this.bounds = new BoundsData(var4, var5, var6, this.height(var6));
         ZenithColor var9 = this.setting.getZenithColor();
         ZenithColor var10 = var8.getFieldBorder().getZenithColor().method7294(var7 * 4.0F);
         ZenithColor var11 = var8.getTextEnable().getZenithColor().method7294(var7);
         ZenithColor var12 = var8.getTextTertiary().getZenithColor().method7294(var7);
         float var13 = this.fieldHeight(var6);
         float var14 = var5 + 4.0F;
         this.renderField(var1, var4, var14, var6, var13, var10, var7);
         float var15 = var14 + var13 + 5.0F;
         this.renderHueBar(var1, var4, var15, var6, var7);
         float var16 = var15 + 4.0F + 6.0F;
         this.renderAlphaBar(var1, var4, var16, var6, var9, var7);
         float var17 = var16 + 4.0F + 6.0F;
         this.renderHexRow(var1, var2, var3, var4, var17, var6, var8, var11, var12, var10, var7);
         float var18 = var17 + 11.0F + 6.0F;
         this.renderCaption(var1, var4, var18, var6, var8, var7);
         this.renderPalette(var1, var4, var18 + 8.0F, var6, var9, var8, var10, var7);
         this.syncFromField(var9);
      }
   }

   private void renderField(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithColor var6, float var7) {
      this.fieldBounds = new BoundsData(var2, var3, var4, var5);
      ZenithColor var8 = ZenithColor.method7297(this.hue, 1.0F, 1.0F).method7294(var7);
      ZenithColor var9 = ZenithColor.INSTANCE.method7294(var7);
      ZenithColor var10 = ZenithColor.INSTANCE2.method7293(0);
      ZenithColor var11 = ZenithColor.INSTANCE2.method7294(var7);
      var1.drawRoundedRect(var2, var3, var4, var5, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(var9, var9, var8, var8));
      var1.drawRoundedRect(var2, var3, var4, var5, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(var10, var11, var10, var11));
      var1.drawRoundedBorder(var2, var3, var4, var5, 0.1F, ProxyDotRadiusData.INSTANCE, var6);
      this.drawHandle(var1, var2 + var4 * this.saturation, var3 + var5 * (1.0F - this.brightness), var7);
   }

   private void renderHueBar(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      this.hueBounds = new BoundsData(var2, var3, var4, 4.0F);
      float var6 = var4 / 6.0F;

      for (int var7 = 0; var7 < 6; var7++) {
         ZenithColor var8 = ZenithColor.method7297(var7 / 6.0F, 1.0F, 1.0F).method7294(var5);
         ZenithColor var9 = ZenithColor.method7297((var7 + 1) / 6.0F, 1.0F, 1.0F).method7294(var5);
         var1.drawRoundedRect(var2 + var7 * var6, var3, var6, 4.0F, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(var8, var8, var9, var9));
      }

      this.drawTick(var1, var2 + var4 * this.hue, var3, var5);
   }

   private void renderAlphaBar(MenuDrawContext var1, float var2, float var3, float var4, ZenithColor var5, float var6) {
      this.alphaBounds = new BoundsData(var2, var3, var4, 4.0F);
      this.drawChecker(var1, var2, var3, var4, 4.0F, var6);
      ZenithColor var7 = var5.method7293(255).method7294(var6);
      ZenithColor var8 = var5.method7293(0);
      var1.drawRoundedRect(var2, var3, var4, 4.0F, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(var8, var8, var7, var7));
      this.drawTick(var1, var2 + var4 * (this.alphaValue / 255.0F), var3, var6);
   }

   private void renderHexRow(
      MenuDrawContext var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      ZenithStyle var7,
      ZenithColor var8,
      ZenithColor var9,
      ZenithColor var10,
      float var11
   ) {
      float var12 = 39.0F;
      float var13 = Math.max(20.0F, var6 - var12 - 3.0F);
      this.hexBounds = new BoundsData(var4, var5, var13, 11.0F);
      var1.drawRect(var4, var5, var13, 11.0F, var7.getFieldSurfaceBackground().getZenithColor().method7294(var11 * 2.0F));
      var1.drawRoundedBorder(
         var4,
         var5,
         var13,
         11.0F,
         0.1F,
         ProxyDotRadiusData.method8069(1.5F),
         this.hexField.isSelected() ? var7.getPrimaryColor().getZenithColor().method7294(var11) : var10
      );
      Font var14 = Fonts.NEW_MEDIUM.getFont(5.01F);
      float var15 = var5 + (11.0F - var14.height()) / 2.0F;
      float var16 = var14.width("#");
      var1.drawText(var14, "#", var4 + 4.0F, var15, var9);
      this.hexField.setWidth(var13 - 8.0F - var16);
      this.hexField.method987(var1, var4 + 4.0F + var16 + 1.0F, var15, var8, var9);
      float var17 = var4 + var6 - var12;
      this.copyBounds = new BoundsData(var17, var5, 11.0F, 11.0F);
      this.pasteBounds = new BoundsData(var17 + 11.0F + 3.0F, var5, 11.0F, 11.0F);
      this.resetBounds = new BoundsData(var17 + 28.0F, var5, 11.0F, 11.0F);
      this.drawButton(var1, this.copyBounds, MenuGlyph.COPY, var2, var3, var7, var11);
      this.drawButton(var1, this.pasteBounds, MenuGlyph.PASTE, var2, var3, var7, var11);
      this.drawButton(var1, this.resetBounds, MenuGlyph.RESET, var2, var3, var7, var11);
   }

   private void renderCaption(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5, float var6) {
      Font var7 = Fonts.NEW_MEDIUM.getFont(4.0F);
      ZenithColor var8 = var5.getTextTertiary().getZenithColor().method7294(var6 * 0.9F);
      String var9 = Zenith.getInstance().getLocalization().translate("gui.picker.palette").toUpperCase(Locale.ROOT);
      var1.drawText(var7, HoverInfo.spaced(var9), var2, var3, var8);
      String var10 = "A " + Math.round(this.alphaValue / 255.0F * 100.0F) + "%";
      var1.drawText(var7, var10, var2 + var4 - var7.width(var10), var3, var8);
   }

   private void renderPalette(MenuDrawContext var1, float var2, float var3, float var4, ZenithColor var5, ZenithStyle var6, ZenithColor var7, float var8) {
      this.buildPalette(var6);
      this.paletteBounds.clear();
      int var9 = this.paletteColumns(var4);

      for (int var10 = 0; var10 < this.palette.size(); var10++) {
         float var11 = var2 + var10 % var9 * 9.0F;
         float var12 = var3 + var10 / var9 * 9.0F;
         this.paletteBounds.add(new BoundsData(var11, var12, 7.0F, 7.0F));
         ZenithColor var13 = this.palette.get(var10);
         var1.drawRect(var11 + 0.5F, var12 + 0.5F, 6.0F, 6.0F, var13.method7294(var8));
         var1.drawRoundedBorder(var11, var12, 7.0F, 7.0F, 0.1F, ProxyDotRadiusData.INSTANCE, var7);
         if (sameRgb(var13, var5)) {
            var1.drawRoundedBorder(
               var11 - 1.5F, var12 - 1.5F, 10.0F, 10.0F, 0.1F, ProxyDotRadiusData.INSTANCE, var6.getPrimaryColor().getZenithColor().method7294(var8)
            );
         }
      }
   }

   private void drawButton(MenuDrawContext var1, BoundsData var2, MenuGlyph var3, float var4, float var5, ZenithStyle var6, float var7) {
      boolean var8;
      if (var8 = var2.contains(var4, var5)) {
         var1.drawRect(
            var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), var6.getFieldSurfaceBackground().getZenithColor().method7294(var7 * 3.0F)
         );
      }

      var1.drawRoundedBorder(
         var2.getFloatValue(),
         var2.getFloatValue2(),
         var2.width(),
         var2.height(),
         0.1F,
         ProxyDotRadiusData.method8069(1.5F),
         var6.getFieldBorder().getZenithColor().method7294(var7 * 4.0F)
      );
      var3.draw(
         var1,
         var2.getFloatValue() + (var2.width() - 6.0F) / 2.0F,
         var2.getFloatValue2() + (var2.height() - 6.0F) / 2.0F,
         (var8 ? var6.getTextEnable() : var6.getTextTertiary()).getZenithColor().method7294(var7)
      );
   }

   private void drawHandle(MenuDrawContext var1, float var2, float var3, float var4) {
      drawFrame(var1, var2 - 3.5F, var3 - 3.5F, 7.0F, 7.0F, ZenithColor.INSTANCE2.method7294(0.45F * var4));
      drawFrame(var1, var2 - 2.5F, var3 - 2.5F, 5.0F, 5.0F, ZenithColor.INSTANCE.method7294(var4));
   }

   private void drawTick(MenuDrawContext var1, float var2, float var3, float var4) {
      var1.drawRect(var2 - 1.25F, var3 - 2.0F, 2.5F, 8.0F, ZenithColor.INSTANCE2.method7294(0.5F * var4));
      var1.drawRect(var2 - 0.75F, var3 - 1.5F, 1.5F, 7.0F, ZenithColor.INSTANCE.method7294(var4));
   }

   private void drawChecker(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = 2.0F;
      var1.drawRect(var2, var3, var4, var5, ZenithColor.INSTANCE2.method7294(0.45F * var6));
      ZenithColor var8 = ZenithColor.INSTANCE.method7294(0.2F * var6);

      for (int var9 = 0; var9 * var7 < var5; var9++) {
         for (int var10 = 0; var10 * var7 < var4; var10++) {
            if ((var9 + var10) % 2 == 0) {
               float var11 = var2 + var10 * var7;
               float var12 = var3 + var9 * var7;
               var1.drawRect(var11, var12, Math.min(var7, var2 + var4 - var11), Math.min(var7, var3 + var5 - var12), var8);
            }
         }
      }
   }

   private static void drawFrame(MenuDrawContext var0, float var1, float var2, float var3, float var4, ZenithColor var5) {
      var0.drawRect(var1, var2, var3, 1.0F, var5);
      var0.drawRect(var1, var2 + var4 - 1.0F, var3, 1.0F, var5);
      var0.drawRect(var1, var2, 1.0F, var4, var5);
      var0.drawRect(var1 + var3 - 1.0F, var2, 1.0F, var4, var5);
   }

   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         this.ensureField();
         this.hexField.setSelected(false);
         if (this.hexBounds != null && this.hexBounds.contains(var1, var3)) {
            this.hexField.onMouseClicked(var1, var3, var5);
            this.hexField.setSelected(true);
            return true;
         } else {
            for (int var6 = 0; var6 < this.paletteBounds.size() && var6 < this.palette.size(); var6++) {
               if (this.paletteBounds.get(var6).contains(var1, var3)) {
                  this.adopt(this.palette.get(var6).method7293(this.alphaValue));
                  this.commit();
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            if (this.copyBounds != null && this.copyBounds.contains(var1, var3)) {
               MinecraftClient.getInstance().keyboard.setClipboard("#" + ColorUtils.method6434(this.setting.getZenithColor()));
               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            } else if (this.pasteBounds != null && this.pasteBounds.contains(var1, var3)) {
               this.paste();
               return true;
            } else if (this.resetBounds != null && this.resetBounds.contains(var1, var3)) {
               this.setting.reset();
               this.applied = null;
               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            } else {
               if (this.fieldBounds != null && this.fieldBounds.contains(var1, var3)) {
                  this.drag = ColorPicker.Drag.FIELD;
               } else if (this.hueBounds != null && this.hueBounds.method8092(var1, var3, 3.0F)) {
                  this.drag = ColorPicker.Drag.HUE;
               } else if (this.alphaBounds != null && this.alphaBounds.method8092(var1, var3, 3.0F)) {
                  this.drag = ColorPicker.Drag.ALPHA;
               }

               if (this.drag != ColorPicker.Drag.NONE) {
                  this.applyDrag((float)var1, (float)var3);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void onMouseReleased() {
      this.drag = ColorPicker.Drag.NONE;
   }

   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return (this.hexField != null && this.hexField.keyPressed(var1, var2, var3));
   }

   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      return (this.hexField != null && this.hexField.charTyped(var1, var2));
   }

   private void paste() {
      String var1 = MinecraftClient.getInstance().keyboard.getClipboard();
      if (var1 != null) {
         ZenithColor var2 = ColorUtils.method6433(var1.replace("#", "").trim(), null);
         if (var2 != null) {
            this.adopt(var2.method7293(this.alphaValue));
            this.commit();
            Zenith.getInstance().getClientSounds().method1570(false);
         }
      }
   }

   private void ensureField() {
      if (this.hexField == null) {
         this.hexField = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.01F), "000000", 0.0F);
         this.hexField.setType(SearchBox.Type.ENGLISH_NUMBERS);
         this.hexField.setMaxLength(6);
      }
   }

   private float fieldHeight(float var1) {
      return MathHelper.clamp(var1 * 0.44F, 30.0F, 56.0F);
   }

   private int paletteColumns(float var1) {
      return Math.max(1, (int)((var1 + 2.0F) / 9.0F));
   }

   private int paletteRows(float var1) {
      ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var2 == null) {
         return 1;
      } else {
         this.buildPalette(var2);
         int var3 = this.paletteColumns(var1);
         return Math.max(1, (this.palette.size() + var3 - 1) / var3);
      }
   }

   private void applyDrag(float var1, float var2) {
      if (this.drag != ColorPicker.Drag.NONE) {
         switch (this.drag) {
            case FIELD:
               if (this.fieldBounds == null) {
                  return;
               }

               this.saturation = MathHelper.clamp((var1 - this.fieldBounds.getFloatValue()) / this.fieldBounds.width(), 0.0F, 1.0F);
               this.brightness = 1.0F - MathHelper.clamp((var2 - this.fieldBounds.getFloatValue2()) / this.fieldBounds.height(), 0.0F, 1.0F);
               break;
            case HUE:
               if (this.hueBounds == null) {
                  return;
               }

               this.hue = MathHelper.clamp((var1 - this.hueBounds.getFloatValue()) / this.hueBounds.width(), 0.0F, 1.0F);
               break;
            case ALPHA:
               if (this.alphaBounds == null) {
                  return;
               }

               this.alphaValue = Math.round(MathHelper.clamp((var1 - this.alphaBounds.getFloatValue()) / this.alphaBounds.width(), 0.0F, 1.0F) * 255.0F);
               break;
            default:
               return;
         }

         this.commit();
      }
   }

   private void syncFromField(ZenithColor var1) {
      if (this.hexField.isSelected()) {
         ZenithColor var2 = ColorUtils.method6433(this.hexField.getText(), null);
         if (var2 != null && !sameRgb(var2, var1)) {
            this.adopt(var2.method7293(this.alphaValue));
            this.commit();
         }
      } else {
         this.hexField.setText(ColorUtils.method6434(var1));
         this.hexField.setCursor(6);
      }
   }

   private void syncFromSetting() {
      ZenithColor var1 = this.setting.getZenithColor();
      if (this.applied == null || !var1.equals(this.applied)) {
         this.adopt(var1);
         this.applied = var1;
      }
   }

   private void adopt(ZenithColor var1) {
      float[] var2 = Color.RGBtoHSB(var1.getIntValue(), var1.getIntValue2(), var1.getIntValue3(), null);
      if (var2[1] > 0.0F) {
         this.hue = var2[0];
      }

      if (var2[2] > 0.0F) {
         this.saturation = var2[1];
      }

      this.brightness = var2[2];
      this.alphaValue = var1.getIntValue4();
   }

   private void commit() {
      ZenithColor var1 = ZenithColor.method7297(this.hue, this.saturation, this.brightness).method7293(this.alphaValue);
      this.setting.setZenithColor(var1);
      this.applied = var1;
   }

   private void buildPalette(ZenithStyle var1) {
      this.palette.clear();
      this.addPaletteColor(var1.getPrimaryColor().getZenithColor());
      this.addPaletteColor(var1.getSecondaryPrimaryColor().getZenithColor());
      this.addPaletteColor(var1.getFriendColor().getZenithColor());
      this.addPaletteColor(ZenithColor.INSTANCE);
      this.addPaletteColor(ZenithColor.INSTANCE2);

      for (ZenithColor var3 : RECENT) {
         this.addPaletteColor(var3);
      }
   }

   private void addPaletteColor(ZenithColor var1) {
      if (var1 != null && this.palette.size() < 12) {
         ZenithColor var2 = var1.method7293(255);

         for (ZenithColor var4 : this.palette) {
            if (sameRgb(var4, var2)) {
               return;
            }
         }

         this.palette.add(var2);
      }
   }

   private static void remember(ZenithColor var0) {
      if (var0 != null) {
         ZenithColor var1 = var0.method7293(255);
         RECENT.removeIf(var1x -> sameRgb(var1x, var1));
         RECENT.add(0, var1);

         while (RECENT.size() > 6) {
            RECENT.remove(RECENT.size() - 1);
         }
      }
   }

   private static boolean sameRgb(ZenithColor var0, ZenithColor var1) {
      return (var0 != null
            && var1 != null
            && var0.getIntValue() == var1.getIntValue()
            && var0.getIntValue2() == var1.getIntValue2()
            && var0.getIntValue3() == var1.getIntValue3());
   }

   private static enum Drag {
      NONE,
      FIELD,
      HUE,
      ALPHA;
   }
}

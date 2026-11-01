package org.zenith.client.screens.crosshair;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Canvas;
import org.zenith.utility.misc.CrosshairPresets;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class CrosshairEditorPanel implements MinecraftAccess {
   public static final float WIDTH = 176.0F;
   private static final float TITLE_HEIGHT = 20.0F;
   private static final float TOOL_SIZE = 15.0F;
   private static final float TOOL_GAP = 3.0F;
   private static final float ROW_GAP = 6.0F;
   private static final float BUTTON_HEIGHT = 14.0F;
   private static final float MIN_CELL = 3.0F;
   private static final int TOOL_BRUSH = 0;
   private static final int TOOL_ERASER = 1;
   private static final int TOOL_FILL = 2;
   private static final int TOOL_PICKER = 3;
   private static final int TOOL_UNDO = 4;
   private static final int TOOL_REDO = 5;
   private static final String[][] TOOL_ICONS = new String[][]{
      {".....##", "....##.", "...##..", "..##...", ".##....", "##.....", "#......"},
      {"....##.", "...####", "..####.", ".####..", ".###...", ".##....", "......."},
      {"...#...", "..###..", ".#####.", "#######", "#######", ".#####.", "..###.."},
      {"....###", "....###", "...##..", "..##...", ".##....", "##.....", "#......"},
      {"..#....", ".##....", "###....", ".##....", "..#....", ".......", "......."},
      {"....#..", "....##.", "....###", "....##.", "....#..", ".......", "......."}
   };
   private static final int[] PALETTE = new int[]{-1, -4210753, -16777216, -48060, 40768, 57418, -11870592, -11876097, -6591489, 0};
   private final Animation visible = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final List<InspectorRow> rows = new ArrayList<>();
   private boolean open;
   private Canvas canvas;
   private int[] backup;
   private int tool = 0;
   private int brushColor = -1;
   private int brushSize = 1;
   private boolean painting;
   private boolean erasingStroke;
   private float mouseX;
   private float mouseY;
   private float cell = 4.6F;
   private BoundsData panelBounds;
   private BoundsData canvasBounds;
   private BoundsData clearBounds;
   private BoundsData saveBounds;
   private BoundsData cancelBounds;
   private BoundsData closeBounds;
   private BoundsData brushSliderBounds;
   private boolean draggingBrushSize;
   private final BoundsData[] toolBounds = new BoundsData[TOOL_ICONS.length];
   private final BoundsData[] presetBounds = new BoundsData[CrosshairPresets.texts.length];
   private final BoundsData[] paletteBounds = new BoundsData[PALETTE.length];
   private static int[][] presetPreviews;

   public boolean isOpen() {
      return this.open;
   }

   public boolean isVisible() {
      return this.open || this.visible.getValue() > 0.001F;
   }

   public void open(Canvas var1, List<Setting> var2) {
      this.canvas = var1;
      this.backup = var1.method138();
      this.open = true;
      this.painting = false;
      this.rows.clear();

      for (Setting var4 : var2) {
         this.rows.add(new InspectorRow(var4, 0));
      }
   }

   public void close() {
      this.open = false;
      this.painting = false;
      this.draggingBrushSize = false;
      this.panelBounds = null;
   }

   private void cancel() {
      if (this.canvas != null && this.backup != null) {
         this.canvas.method139(this.backup);
      }

      this.close();
   }

   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      ZenithStyle var10 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.visible.method2807(this.open);
      float var11 = this.visible.getValue();
      if (var10 != null && !(var11 <= 0.001F) && this.canvas != null) {
         this.mouseX = var2;
         this.mouseY = var3;
         var8 *= var11 * var9 * var9;
         if (!(var8 <= 0.01F)) {
            this.panelBounds = this.open ? new BoundsData(var4, var5, var6, var7) : null;
            float var12 = var4 + GuiStyle.PADDING * 2;
            float var13 = var6 - GuiStyle.PADDING * 4;
            this.drawHeader(var1, var4, var5, var6, var8, var10);
            InspectorRow.alignTracks(this.rows, var6);
            float var14 = 72.0F + this.settingsHeight() + 14.0F + GuiStyle.PADDING.intValue() * 3.0F;
            float var15 = Math.min(var13, var7 - 20.0F - var14 - GuiStyle.PADDING.intValue() * 2.0F);
            this.cell = Math.max(3.0F, var15 / 31.0F);
            var15 = this.cell * 31.0F;
            float var16 = var5 + 20.0F;
            this.drawCanvas(var1, var4 + (var6 - var15) / 2.0F, var16, var15, var8, var10);
            var16 += var15 + 6.0F;
            this.drawTools(var1, var4 + (var6 - this.toolsWidth()) / 2.0F, var16, var8, var10);
            var16 += 21.0F;
            var16 = this.drawBrushSize(var1, var12, var16, var13, var8, var10);
            var16 = this.drawPalette(var1, var12, var16, var8, var10);
            this.drawPresets(var1, var12, var16, var8, var10);
            var16 += 22.0F;
            this.drawSettings(var1, var2, var3, var4, var16, var6, var8);
            this.drawButtons(var1, var4, var5 + var7 - 14.0F - GuiStyle.PADDING * 2, var6, var8, var10);
            if (this.painting) {
               this.paintAt(var2, var3, this.erasingStroke);
            }
         }
      }
   }

   private float toolsWidth() {
      return TOOL_ICONS.length * 15.0F + (TOOL_ICONS.length - 1) * 3.0F;
   }

   private float settingsHeight() {
      float var1 = 0.0F;

      for (InspectorRow var3 : this.rows) {
         if (var3.isVisible()) {
            var1 += var3.height();
         }
      }

      return var1;
   }

   private void drawHeader(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      Font var7 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var8 = Fonts.NEW_REGULAR.getFont(4.4F);
      float var9 = var2 + GuiStyle.PADDING * 2;
      float var10 = var3 + GuiStyle.PADDING.intValue();
      var1.drawText(
         var7,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.title"),
         var9,
         var10 + 1.0F,
         var6.getTextEnable().getZenithColor().method7294(var5)
      );
      var1.drawText(
         var8,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.hint"),
         var9,
         var10 + var7.height() + 3.0F,
         var6.getTextTertiary().getZenithColor().method7294(var5)
      );
      Font var11 = Fonts.NEW_ICONS.getFont(4.0F);
      float var12 = var2 + var4 - var11.width("2") - GuiStyle.PADDING * 2;
      float var13 = var10 + 2.0F;
      this.closeBounds = this.open ? new BoundsData(var12, var13, 5.0F, 5.0F) : null;
      var1.drawText(var11, "2", var12, var13, var6.getTextTertiary().getZenithColor().method7294(var5));
   }

   private void drawSettings(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      float var8 = var5;

      for (InspectorRow var10 : this.rows) {
         if (var10.isVisible()) {
            var10.render(var1, var2, var3, var4, var8, var6, var7);
            var8 += var10.height();
         }
      }
   }

   private void drawTools(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      float var6 = 15.0F;

      for (int var7 = 0; var7 < TOOL_ICONS.length; var7++) {
         float var8 = var2 + var7 * (var6 + 3.0F);
         this.toolBounds[var7] = this.open ? new BoundsData(var8, var3, var6, var6) : null;
         boolean var10 = var7 == this.tool && var7 < 4;
         boolean var11 = var7 != 4 ? var7 != 5 || this.canvas.method143() : this.canvas.method142();
         ZenithColor var12 = var10
            ? var5.getPrimaryColor().getZenithColor().method7294(var4 * 0.9F)
            : var5.getRightBackground().getZenithColor().method7294(var4 * 0.75F);
         var1.drawRoundedRect(var8, var3, var6, var6, ProxyDotRadiusData.method8069(4.0F), var12);
         ZenithColor var13 = var10 ? ZenithColor.INSTANCE.method7294(var4) : var5.getTextEnable().getZenithColor().method7294(var4 * (var11 ? 0.85F : 0.3F));
         this.drawIcon(var1, TOOL_ICONS[var7], var8 + 4.0F, var3 + 4.0F, 1.0F, var13);
      }
   }

   private void drawIcon(MenuDrawContext var1, String[] var2, float var3, float var4, float var5, ZenithColor var6) {
      var1.beginRectBatch();

      for (int var7 = 0; var7 < var2.length; var7++) {
         String var8 = var2[var7];

         for (int var9 = 0; var9 < var8.length(); var9++) {
            if (var8.charAt(var9) == '#') {
               var1.drawRectBatched(var3 + var9 * var5, var4 + var7 * var5, var5, var5, var6);
            }
         }
      }

      var1.flushRects();
   }

   private void drawCanvas(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      this.canvasBounds = this.open ? new BoundsData(var2, var3, var4, var4) : null;
      var1.drawRoundedRect(
         var2 - 2.0F,
         var3 - 2.0F,
         var4 + 4.0F,
         var4 + 4.0F,
         ProxyDotRadiusData.method8069(4.0F),
         var6.getRightBackground().getZenithColor().method7294(var5 * 0.8F)
      );
      ZenithColor var7 = new ZenithColor(58, 58, 64, 255).method7294(var5);
      ZenithColor var8 = new ZenithColor(44, 44, 50, 255).method7294(var5);
      var1.beginRectBatch();

      for (int var9 = 0; var9 < 31; var9++) {
         for (int var10 = 0; var10 < 31; var10++) {
            var1.drawRectBatched(var2 + var10 * this.cell, var3 + var9 * this.cell, this.cell, this.cell, (var10 + var9 & 1) == 0 ? var7 : var8);
         }
      }

      for (int var12 = 0; var12 < 31; var12++) {
         for (int var13 = 0; var13 < 31; var13++) {
            int var11 = this.canvas.method136(var13, var12);
            if (var11 >>> 24 != 0) {
               var1.drawRectBatched(var2 + var13 * this.cell, var3 + var12 * this.cell, this.cell, this.cell, ZenithColor.method7291(var11).method7294(var5));
            }
         }
      }

      var1.flushRects();
      var1.drawRoundedBorder(
         var2 + 15.0F * this.cell,
         var3 + 15.0F * this.cell,
         this.cell,
         this.cell,
         0.6F,
         ProxyDotRadiusData.method8069(1.0F),
         var6.getPrimaryColor().getZenithColor().method7294(var5 * 0.9F)
      );
   }

   private float drawBrushSize(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      Font var7 = Fonts.NEW_REGULAR.getFont(4.4F);
      var1.drawText(
         var7,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.brushSize"),
         var2,
         var3,
         var6.getTextTertiary().getZenithColor().method7294(var5)
      );
      float var8 = var2 + 52.0F;
      float var9 = Math.max(24.0F, var4 - 52.0F - 10.0F);
      float var10 = var3 + 2.0F;
      this.brushSliderBounds = this.open ? new BoundsData(var8, var10 - 3.0F, var9, 8.0F) : null;
      var1.drawRoundedRect(var8, var10, var9, 2.0F, ProxyDotRadiusData.method8069(1.0F), var6.getRightBackground().getZenithColor().method7294(var5));
      float var11 = var9 * ((this.brushSize - 1) / 3.0F);
      var1.drawRoundedRect(var8, var10, var11, 2.0F, ProxyDotRadiusData.method8069(1.0F), var6.getPrimaryColor().getZenithColor().method7294(var5));
      var1.drawRoundedRect(var8 + var11 - 1.5F, var10 - 1.5F, 3.0F, 5.0F, ProxyDotRadiusData.method8069(1.5F), ZenithColor.INSTANCE.method7294(var5));
      var1.drawText(var7, String.valueOf(this.brushSize), var8 + var9 + 5.0F, var3, var6.getTextEnable().getZenithColor().method7294(var5));
      return var3 + 13.0F;
   }

   private float drawPalette(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font var6 = Fonts.NEW_REGULAR.getFont(4.4F);
      var1.drawText(
         var6,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.brushColor"),
         var2,
         var3,
         var5.getTextTertiary().getZenithColor().method7294(var4)
      );
      float var7 = 10.0F;
      float var8 = var2;
      float var9 = var3 + 7.0F;

      for (int var10 = 0; var10 < PALETTE.length; var10++) {
         float var11 = var8 + var10 * (var7 + 2.5F);
         this.paletteBounds[var10] = this.open ? new BoundsData(var11, var9, var7, var7) : null;
         int var12 = this.paletteColor(var10);
         var1.drawRoundedRect(var11, var9, var7, var7, ProxyDotRadiusData.method8069(2.5F), ZenithColor.method7291(var12).method7294(var4));
         if (var12 == this.brushColor) {
            var1.drawRoundedBorder(
               var11 - 1.0F, var9 - 1.0F, var7 + 2.0F, var7 + 2.0F, 0.8F, ProxyDotRadiusData.method8069(3.5F), ZenithColor.INSTANCE.method7294(var4)
            );
         }
      }

      return var3 + 21.0F;
   }

   private int paletteColor(int var1) {
      return PALETTE[var1] != 0 ? PALETTE[var1] : Zenith.getInstance().getStyleManager().getClientColor(0).method7287();
   }

   private static int[] presetPreview(int var0) {
      if (presetPreviews == null) {
         int[][] var1 = new int[CrosshairPresets.texts.length][];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = CrosshairPresets.method5712(var2);
         }

         presetPreviews = var1;
      }

      return presetPreviews[var0];
   }

   private void drawPresets(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font var6 = Fonts.NEW_REGULAR.getFont(4.4F);
      var1.drawText(
         var6,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.presets"),
         var2,
         var3,
         var5.getTextTertiary().getZenithColor().method7294(var4)
      );
      float var7 = 17.0F;
      float var8 = var2;
      float var9 = var3 + 7.0F;

      for (int var10 = 0; var10 < CrosshairPresets.texts.length; var10++) {
         float var11 = var8 + var10 * (var7 + 2.0F);
         this.presetBounds[var10] = this.open ? new BoundsData(var11, var9, var7, var7) : null;
         var1.drawRoundedRect(var11, var9, var7, var7, ProxyDotRadiusData.method8069(3.0F), var5.getRightBackground().getZenithColor().method7294(var4 * 0.8F));
         int[] var12 = presetPreview(var10);
         float var13 = (var7 - 3.0F) / 31.0F;
         ZenithColor var14 = var5.getTextEnable().getZenithColor().method7294(var4 * 0.9F);
         var1.beginRectBatch();

         for (int var15 = 0; var15 < 31; var15++) {
            for (int var16 = 0; var16 < 31; var16++) {
               if (var12[var15 * 31 + var16] >>> 24 != 0) {
                  var1.drawRectBatched(var11 + 1.5F + var16 * var13, var9 + 1.5F + var15 * var13, Math.max(var13, 0.6F), Math.max(var13, 0.6F), var14);
               }
            }
         }

         var1.flushRects();
      }
   }

   private void drawButtons(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      float var7 = 4.0F;
      float var8 = var4 - GuiStyle.PADDING * 4;
      float var9 = (var8 - var7 * 2.0F) / 3.0F;
      float var10 = var2 + GuiStyle.PADDING * 2;
      this.clearBounds = this.open ? new BoundsData(var10, var3, var9, 14.0F) : null;
      this.saveBounds = this.open ? new BoundsData(var10 + var9 + var7, var3, var9, 14.0F) : null;
      this.cancelBounds = this.open ? new BoundsData(var10 + (var9 + var7) * 2.0F, var3, var9, 14.0F) : null;
      this.drawButton(var1, this.clearBounds, "crosshair.editor.clear", false, var5, var6);
      this.drawButton(var1, this.saveBounds, "crosshair.editor.save", true, var5, var6);
      this.drawButton(var1, this.cancelBounds, "crosshair.editor.cancel", false, var5, var6);
   }

   private void drawButton(MenuDrawContext var1, BoundsData var2, String var3, boolean var4, float var5, ZenithStyle var6) {
      if (var2 != null) {
         ZenithColor var7 = var4
            ? var6.getPrimaryColor().getZenithColor().method7294(var5 * 0.95F)
            : var6.getRightBackground().getZenithColor().method7294(var5 * 0.85F);
         var1.drawRoundedRect(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), ProxyDotRadiusData.method8069(4.0F), var7);
         Font var8 = Fonts.NEW_MEDIUM.getFont(4.4F);
         String var9 = Zenith.getInstance().getLocalization().translate(var3);
         var1.drawText(
            var8,
            var9,
            var2.getFloatValue() + (var2.width() - var8.width(var9)) / 2.0F,
            var2.getFloatValue2() + (var2.height() - var8.height()) / 2.0F,
            (var4 ? ZenithColor.INSTANCE : var6.getTextEnable().getZenithColor()).method7294(var5)
         );
      }
   }

   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.open && this.canvas != null) {
         for (InspectorRow var7 : this.rows) {
            if (var7.isVisible() && var7.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         if (hit(this.closeBounds, var1, var3) || hit(this.cancelBounds, var1, var3)) {
            this.cancel();
            return true;
         } else if (hit(this.saveBounds, var1, var3)) {
            this.close();
            return true;
         } else if (hit(this.clearBounds, var1, var3)) {
            this.canvas.clear();
            return true;
         } else if (hit(this.brushSliderBounds, var1, var3)) {
            this.draggingBrushSize = true;
            this.updateBrushSize(var1);
            return true;
         } else {
            for (int var8 = 0; var8 < this.toolBounds.length; var8++) {
               if (hit(this.toolBounds[var8], var1, var3)) {
                  if (var8 == 4) {
                     this.canvas.undo();
                  } else if (var8 == 5) {
                     this.canvas.redo();
                  } else {
                     this.tool = var8;
                  }

                  return true;
               }
            }

            for (int var9 = 0; var9 < this.paletteBounds.length; var9++) {
               if (hit(this.paletteBounds[var9], var1, var3)) {
                  this.brushColor = this.paletteColor(var9);
                  if (this.tool == 1) {
                     this.tool = 0;
                  }

                  return true;
               }
            }

            for (int var10 = 0; var10 < this.presetBounds.length; var10++) {
               if (hit(this.presetBounds[var10], var1, var3)) {
                  this.canvas.method140();
                  this.canvas.method139(CrosshairPresets.method5712(var10));
                  return true;
               }
            }

            if (hit(this.canvasBounds, var1, var3)) {
               boolean var11 = var5 == Enum3.RIGHT;
               this.canvas.method140();
               this.painting = true;
               this.erasingStroke = var11;
               this.paintAt(var1, var3, var11);
               return true;
            } else {
               return hit(this.panelBounds, var1, var3);
            }
         }
      } else {
         return false;
      }
   }

   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      this.painting = false;
      this.draggingBrushSize = false;
      if (this.open) {
         for (InspectorRow var7 : this.rows) {
            var7.onMouseReleased(var1, var3, var5);
         }
      }
   }

   public boolean onMouseDragged(double var1, double var3) {
      if (!this.open) {
         return false;
      } else if (this.draggingBrushSize) {
         this.updateBrushSize(this.mouseX);
         return true;
      } else {
         for (InspectorRow var6 : this.rows) {
            if (var6.isVisible() && var6.onMouseDragged(this.mouseX, this.mouseY)) {
               return true;
            }
         }

         return this.painting;
      }
   }

   public boolean onMouseScrolled(double var1, double var3, double var5, double var7) {
      return !this.open ? false : hit(this.panelBounds, var1, var3);
   }

   public boolean charTyped(char var1, int var2) {
      if (!this.open) {
         return false;
      } else {
         for (InspectorRow var4 : this.rows) {
            if (var4.isVisible() && var4.charTyped(var1, var2)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.open && this.canvas != null) {
         for (InspectorRow var5 : this.rows) {
            if (var5.isVisible() && var5.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         boolean var6 = (var3 & 2) != 0;
         if (var6 && var1 == 90 && (var3 & 1) != 0) {
            this.canvas.redo();
            return true;
         } else if (var6 && var1 == 90) {
            this.canvas.undo();
            return true;
         } else if (var6 && var1 == 89) {
            this.canvas.redo();
            return true;
         } else if (var1 == 256) {
            this.cancel();
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private void updateBrushSize(double var1) {
      if (this.brushSliderBounds != null) {
         float var3 = (float)(var1 - this.brushSliderBounds.getFloatValue()) / this.brushSliderBounds.width();
         this.brushSize = MathHelper.clamp(Math.round(1.0F + var3 * 3.0F), 1, 4);
      }
   }

   private void paintAt(double var1, double var3, boolean var5) {
      if (this.canvasBounds != null && this.canvasBounds.contains(var1, var3)) {
         int var6 = (int)((var1 - this.canvasBounds.getFloatValue()) / this.cell);
         int var7 = (int)((var3 - this.canvasBounds.getFloatValue2()) / this.cell);
         if (this.tool == 3) {
            int var12 = this.canvas.method136(var6, var7);
            if (var12 >>> 24 != 0) {
               this.brushColor = var12;
               this.tool = 0;
            }

            this.painting = false;
         } else if (this.tool == 2) {
            this.canvas.method144(var6, var7, var5 ? 0 : this.brushColor);
            this.painting = false;
         } else {
            int var8 = !var5 && this.tool != 1 ? this.brushColor : 0;
            int var9 = this.brushSize - 1;

            for (int var10 = -var9; var10 <= var9; var10++) {
               for (int var11 = -var9; var11 <= var9; var11++) {
                  this.canvas.method137(var6 + var11, var7 + var10, var8);
               }
            }
         }
      }
   }

   private static boolean hit(BoundsData var0, double var1, double var3) {
      return var0 != null && var0.contains(var1, var3);
   }
}

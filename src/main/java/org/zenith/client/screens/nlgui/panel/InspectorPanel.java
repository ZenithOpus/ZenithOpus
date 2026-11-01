package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.collections.EmptyList;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.BindLabel;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.elements.RollingText;
import org.zenith.client.screens.nlgui.elements.Scrollbar;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.HeaderBindSetting;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class InspectorPanel extends Panel implements MouseBindTarget {
   private static final float INSET = 8.0F;
   private static final float SCROLL_SPEED = 18.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private final Map<Module, List<InspectorRow>> cache = new HashMap<>();
   private Module module;
   private float scroll;
   private float scrollTarget;
   private final Animation openAnimation = new Animation(240L, 0.0F, Easing.INSTANCE12);
   private static final float MAX_ROW_DELAY = 0.4F;
   private float entranceShift = 7.0F;
   private final RollingText stateRoll = new RollingText();
   private BindLabel headerBindLabel;
   private HeaderBindSetting headerBind;
   private Module headerBindOwner;
   private boolean headerHasSettings;
   private BoundsData headerBindBounds;
   private String sectionTitle;
   private List<Setting> sectionSettings;
   private ModulePreviewStage.Kind sectionPreview;
   private Module sectionPreviewOwner;
   private List<InspectorRow> sectionRows = new ArrayList<>();
   private BoundsData backBounds;
   private boolean active;
   private BoundsData columnBounds;
   private BoundsData headerToggleBounds;
   private BoundsData contentBounds;
   private final Scrollbar scrollbar = new Scrollbar();

   public void setActive(boolean var1) {
      this.active = var1;
      if (!var1) {
         this.resetHeaderBind();
      }
   }

   public boolean isShowing(Module var1) {
      return this.module != null && this.module == var1;
   }

   public boolean isOpen() {
      return this.module != null;
   }

   public boolean hasPreview() {
      return this.previewKind() != null;
   }

   public ModulePreviewStage.Kind previewKind() {
      if (this.sectionPreview != null) {
         return this.sectionPreview;
      } else {
         return this.module == null ? null : this.module.previewKind();
      }
   }

   public Module previewOwner() {
      return this.sectionPreviewOwner != null ? this.sectionPreviewOwner : this.module;
   }

   public void openSection(String var1, List<Setting> var2, ModulePreviewStage.Kind var3, Module var4) {
      this.resetHeaderBind();
      this.sectionTitle = var1;
      this.sectionSettings = var2;
      this.sectionPreview = var3;
      this.sectionPreviewOwner = var4;
      this.sectionRows = new ArrayList<>();

      for (Setting var6 : var2) {
         this.sectionRows.add(new InspectorRow(var6, 0, var4 == null ? "" : var4.getClass().getSimpleName()));
      }

      this.scroll = 0.0F;
      this.scrollTarget = 0.0F;
      this.entranceShift = 18.0F;
      this.openAnimation.reset(0.0F);
      this.openAnimation.method2810(1.0F);
   }

   public void closeSection() {
      if (this.sectionSettings != null) {
         this.resetHeaderBind();
         this.sectionTitle = null;
         this.sectionSettings = null;
         this.sectionPreview = null;
         this.sectionPreviewOwner = null;
         this.sectionRows = new ArrayList<>();
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.entranceShift = -18.0F;
         this.openAnimation.reset(0.0F);
         this.openAnimation.method2810(1.0F);
      }
   }

   public boolean isSectionOpen() {
      return this.sectionSettings != null;
   }

   public void showFor(Module var1) {
      if (this.module != var1) {
         this.resetHeaderBind();
         this.closeSection();
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.entranceShift = 7.0F;
         this.openAnimation.reset(0.0F);
         this.openAnimation.method2810(1.0F);
      }

      this.module = var1;
   }

   public void toggleFor(Module var1) {
      if (this.isShowing(var1)) {
         this.close();
      } else {
         this.showFor(var1);
      }
   }

   public void close() {
      this.resetHeaderBind();
      this.module = null;
      this.closeSection();
   }

   private Module headerModule() {
      return this.sectionSettings == null ? this.module : this.sectionPreviewOwner;
   }

   private void resetHeaderBind() {
      for (InspectorRow var2 : this.sectionRows) {
         var2.cancelBinding();
      }

      List<InspectorRow> var4 = this.cache.get(this.module);
      if (var4 != null) {
         for (Object var3_o : (Iterable<?>)(Object)(var4)) {
            InspectorRow var3 = (InspectorRow)var3_o;
            var3.cancelBinding();
         }
      }

      BindCapture.end(this);
      this.headerBind = null;
      this.headerBindOwner = null;
      this.headerBindLabel = null;
      this.headerBindBounds = null;
      this.headerToggleBounds = null;
      this.backBounds = null;
   }

   public void forget(Module var1) {
      this.cache.remove(var1);
      if (this.module == var1) {
         this.close();
      }
   }

   private List<InspectorRow> visibleRows(Module var1) {
      return this.sectionSettings != null ? this.sectionRows : this.rowsOf(var1);
   }

   private List<InspectorRow> rowsOf(Module var1) {
      return this.cache.computeIfAbsent(var1, var0 -> {
         ArrayList<InspectorRow> var1x = new ArrayList<>();

         for (Setting var3 : var0.getSettings()) {
            var1x.add(new InspectorRow(var3, 0, var0.getClass().getSimpleName()));
         }

         if (var1x.isEmpty()) {
            var1x.add(new InspectorRow(new HeaderBindSetting(var0), 0));
         }

         return var1x;
      });
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         float var8 = 144.0F;
         float var9 = NLMenuScreen.heightPanel;
         this.columnBounds = new BoundsData(var5, var6, var8, var9);
         float var10 = HoverInfo.boxHeight();
         float var11 = var6 + var9 - var10;
         this.renderHeader(var1, var2, var3, var5, var6, var8, var4, var7);
         float var12 = var6 + 23.0F;
         float var13 = var11 - GuiStyle.PADDING.intValue();
         this.contentBounds = new BoundsData(var5, var12, var8, var13 - var12);
         this.renderRows(var1, var2, var3, var5, var12, var8, var13 - var12, var4, var7);
         if (!(var10 <= 0.01F)) {
            var1.drawRect(
               var5 + 8.0F, var11 - GuiStyle.PADDING.intValue(), var8 - 16.0F, 1.0F, var7.getFieldBorder().getZenithColor().method7294(var4 * var10 / 34.0F)
            );
            if (!HoverInfo.hasText() && this.module != null) {
               String var14 = this.module.getDescription();
               if (var14 != null && !var14.isBlank()) {
                  HoverInfo.request(this.module.getName(), var14);
               }
            }

            HoverInfo.render(
               var1, var5 + 8.0F, var11 + GuiStyle.PADDING.intValue(), var8 - 16.0F, Math.max(0.0F, var10 - GuiStyle.PADDING.intValue() * 2.0F), var4
            );
         }
      }
   }

   private void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
      Font var10 = Fonts.NEW_MEDIUM.getFont(5.01F);
      float var11 = var5 + (23.0F - var9.height()) / 2.0F;
      this.headerBindBounds = null;
      if (this.module == null) {
         var1.drawText(var9, "-", var4 + 8.0F, var11, var8.getTextTertiary().getZenithColor().method7294(var7));
         this.headerToggleBounds = null;
         this.backBounds = null;
      } else {
         Module var12 = this.sectionSettings == null ? this.module : this.sectionPreviewOwner;
         if (this.headerBindOwner != var12) {
            this.resetHeaderBind();
         }

         if (this.headerBind == null && var12 != null) {
            boolean var34;
            label152: {
               this.headerBind = new HeaderBindSetting(var12);
               this.headerBindOwner = var12;
               this.headerBindLabel = new BindLabel();
               if (this.sectionSettings == null) {
                  var var10001 = var12.getSettings();
                  if (var10001.isEmpty()) {
                     var34 = false;
                     break label152;
                  }
               }

               var34 = true;
            }

            this.headerHasSettings = var34;
         }

         if (this.sectionSettings != null) {
            float var24 = var4 + 8.0F + 11.0F;
            float var26 = var4 + var6 - 8.0F;
            boolean var28 = BindCapture.owner() == this;
            String var30 = this.headerBind == null ? null : BindLabel.text(var28, this.headerBind.getKeyCode(), this.headerBind.getModifiers());
            if (this.headerBind != null && var30 == null) {
               var30 = "+";
            }

            float var31 = var30 == null ? var26 : var26 - var10.width(var30) - GuiStyle.PADDING.intValue() * 2.0F;
            float var32 = Math.max(0.0F, var31 - var24);
            var1.drawText(
               var9,
               var9.clip(this.sectionTitle == null ? this.module.getName() : this.sectionTitle, var32),
               var24,
               var11,
               var8.getTextEnable().getZenithColor().method7294(var7)
            );
            boolean var33 = var2 >= var4 && var2 <= var4 + var6 && var3 >= var5 && var3 <= var5 + 23.0F;
            if (this.headerBind != null) {
               this.headerBindBounds = this.headerBindLabel
                  .render(var1, var2, var3, var5, 23.0F, var26, var30, var28, var10, var31 + GuiStyle.PADDING.intValue(), var33, var7, null, var8);
               if (this.headerBindBounds != null && this.headerBindBounds.contains(var2, var3)) {
                  HoverInfo.request(this.headerBind.getName(), this.headerBind.getDescription());
               }
            }

            float var20 = this.headerBindBounds == null ? var4 + var6 : this.headerBindBounds.getFloatValue() - 1.0F;
            this.backBounds = new BoundsData(var4, var5, Math.max(0.0F, var20 - var4), 23.0F);
            boolean var21 = this.backBounds.contains(var2, var3);
            ZenithColor var22 = var8.getTextSecondary()
               .getZenithColor()
               .method7295(var8.getTextEnable().getZenithColor(), var21 ? 1.0F : 0.4F)
               .method7294(var7);
            MenuGlyph.drawChevron(var1, var4 + 8.0F, var5 + 11.5F, 0.0F, var22);
            this.headerToggleBounds = null;
         } else {
            this.backBounds = null;
            MenuGlyph.forCategory(this.module.getCategory()).draw(var1, var4 + 8.0F, var5 + 8.5F, var8.getTextEnable().getZenithColor().method7294(var7));
            float var13 = var4 + 8.0F + 11.0F;
            if (this.headerHasSettings) {
               this.headerToggleBounds = null;
               boolean var25;
               String var27 = BindLabel.text(var25 = BindCapture.owner() == this, this.headerBind.getKeyCode(), this.headerBind.getModifiers());
               if (var27 == null) {
                  var27 = "+";
               }

               float var29;
               float var17 = (var29 = var4 + var6 - 8.0F) - var10.width(var27) - GuiStyle.PADDING.intValue() * 2.0F;
               String var18 = var9.clip(this.module.getName(), Math.max(0.0F, var17 - var13));
               var1.drawText(var9, var18, var13, var11, var8.getTextEnable().getZenithColor().method7294(var7));
               boolean var19 = var2 >= var4 && var2 <= var4 + var6 && var3 >= var5 && var3 <= var5 + 23.0F;
               this.headerBindBounds = this.headerBindLabel
                  .render(
                     var1,
                     var2,
                     var3,
                     var5,
                     23.0F,
                     var29,
                     var27,
                     var25,
                     var10,
                     var13 + var9.width(var18) + GuiStyle.PADDING.intValue(),
                     var19,
                     var7,
                     null,
                     var8
                  );
               if (this.headerBindBounds != null && this.headerBindBounds.contains(var2, var3)) {
                  HoverInfo.request(this.headerBind.getName(), this.headerBind.getDescription());
               }
            } else {
               String var14 = this.module.isEnabled() ? "ON" : "OFF";
               float var15 = var10.width(var14);
               float var16 = var4 + var6 - 8.0F - var15;
               var1.drawText(
                  var9,
                  var9.clip(this.module.getName(), var16 - GuiStyle.PADDING * 2 - var13),
                  var13,
                  var11,
                  var8.getTextEnable().getZenithColor().method7294(var7)
               );
               this.headerToggleBounds = new BoundsData(var16 - GuiStyle.PADDING.intValue(), var5, var15 + GuiStyle.PADDING.intValue() + 8.0F, 23.0F);
               this.stateRoll
                  .draw(
                     var1,
                     var10,
                     var14,
                     var16 + var15,
                     var5 + (23.0F - var10.height()) / 2.0F,
                     (this.module.isEnabled() ? var8.getPrimaryColor().getZenithColor() : var8.getTextTertiary().getZenithColor()).method7294(var7),
                     this.module.isEnabled() ? 1 : -1
                  );
               if (this.headerToggleBounds.contains(var2, var3)) {
                  HoverInfo.request(
                     this.module.getName(),
                     Zenith.getInstance().getLocalization().translate(this.module.isEnabled() ? "gui.inspector.toggle.off" : "gui.inspector.toggle.on")
                  );
               }
            }
         }
      }
   }

   private void renderRows(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      if (this.module != null) {
         List<InspectorRow> var10 = this.visibleRows(this.module);
         InspectorRow.alignTracks(var10, var6);
         float var11 = 0.0F;

         for (Object var13_o : (Iterable<?>)(Object)(var10)) {
            InspectorRow var13 = (InspectorRow)var13_o;
            if (var13.isVisible()) {
               var11 += var13.height();
            }
         }

         this.clampScroll(var11, var7);
         this.scroll = this.scrollbar.isDragging() ? this.scrollTarget : this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
         float var18 = this.openAnimation.method2808(1.0F);
         var1.enableScissor(var4, var5, var4 + var6, var5 + var7);
         float var19 = var5 + this.scroll;
         int var14 = 0;

         for (Object var16_o : (Iterable<?>)(Object)(var10)) {
            InspectorRow var16 = (InspectorRow)var16_o;
            if (var16.isVisible()) {
               float var17;
               if ((var17 = rowEntrance(var18, var14++)) > 0.001F) {
                  var16.render(var1, var2, var3, var4 + (1.0F - var17) * this.entranceShift, var19, var6, var8 * var17);
               }

               var19 += var16.height();
            }
         }

         var1.disableScissor();
         this.scrollbar.render(var1, var4 + var6 - 2.0F, var5, var7, var11, this.scroll, var9.getTextTertiary().getZenithColor().method7294(var8 * 0.6F));
      }
   }

   private static float rowEntrance(float var0, int var1) {
      if (var0 >= 1.0F) {
         return 1.0F;
      } else {
         float var2 = Math.min(var1 * 0.045F, 0.4F);
         return MathHelper.clamp((var0 - var2) / 0.6F, 0.0F, 1.0F);
      }
   }

   private void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float var3 = var2 - var1;
         this.scrollTarget = MathHelper.clamp(this.scrollTarget, var3, 0.0F);
         this.scroll = MathHelper.clamp(this.scroll, var3, 0.0F);
      }
   }

   @Override
   public boolean bindMouse(Enum3 var1) {
      if (this.active
         && this.module != null
         && BindCapture.owner() == this
         && this.headerBind != null
         && this.headerBindOwner == (this.sectionSettings == null ? this.module : this.sectionPreviewOwner)
         && MouseBindTarget.bindable(var1)) {
         this.headerBind.setBind(var1.getIntValue(), 0);
         BindCapture.end(this);
         this.headerBindLabel.set();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   public void cancelHeaderBindingOutside(double var1, double var3) {
      if (this.headerBindBounds == null || !this.headerBindBounds.contains(var1, var3)) {
         BindCapture.end(this);
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bindMouse(var5)) {
         return true;
      } else {
         this.cancelHeaderBindingOutside(var1, var3);
         if (this.active && this.module != null && this.columnBounds != null && this.columnBounds.contains(var1, var3)) {
            if (this.headerBind != null
               && this.headerBindOwner == (this.sectionSettings == null ? this.module : this.sectionPreviewOwner)
               && this.headerBindBounds != null
               && this.headerBindBounds.contains(var1, var3)) {
               if (var5 == Enum3.RIGHT) {
                  this.headerBind.setBind(-1, 0);
                  BindCapture.end(this);
                  this.headerBindLabel.cleared();
                  Zenith.getInstance().getClientSounds().method1567();
               } else {
                  BindCapture.begin(this);
                  this.headerBindLabel.waiting();
                  Zenith.getInstance().getClientSounds().method1570(false);
               }

               return true;
            } else if (this.backBounds != null && this.backBounds.contains(var1, var3)) {
               this.closeSection();
               Zenith.getInstance().getClientSounds().method1563();
               return true;
            } else if (this.headerToggleBounds != null && this.headerToggleBounds.contains(var1, var3)) {
               this.module.toggle();
               return true;
            } else {
               float var6 = this.scrollbar.onMouseClicked(var1, var3);
               if (this.scrollbar.isDragging()) {
                  if (!Float.isNaN(var6)) {
                     this.scrollTarget = var6;
                  }

                  return true;
               } else {
                  if (this.contentBounds != null && this.contentBounds.contains(var1, var3)) {
                     for (InspectorRow var8 : this.visibleRows(this.module)) {
                        if (var8.isVisible() && var8.onMouseClicked(var1, var3, var5)) {
                           return true;
                        }
                     }
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      if (this.module == null) {
         return false;
      } else {
         this.scrollbar.release();

         for (InspectorRow var7 : this.visibleRows(this.module)) {
            var7.onMouseReleased(var1, var3, var5);
         }

         return false;
      }
   }

   public boolean onMouseDragged(double var1, double var3) {
      if (this.active && this.module != null) {
         float var5;
         if (!Float.isNaN(var5 = this.scrollbar.onMouseDragged(var3))) {
            this.scrollTarget = var5;
            return true;
         } else {
            boolean var6 = false;

            for (InspectorRow var8 : this.visibleRows(this.module)) {
               if (var8.onMouseDragged(var1, var3)) {
                  var6 = true;
               }
            }

            return var6;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.active && this.module != null && this.columnBounds != null && this.columnBounds.contains(var1, var3)) {
         this.scrollTarget += (float)var7 * 18.0F;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.active && this.module != null) {
         if (BindCapture.owner() == this
            && this.headerBind != null
            && this.headerBindOwner == (this.sectionSettings == null ? this.module : this.sectionPreviewOwner)) {
            if (var1 == 256) {
               this.headerBind.setBind(-1, 0);
               this.headerBindLabel.cleared();
               Zenith.getInstance().getClientSounds().method1567();
            } else {
               this.headerBind.setBind(var1, KeyName.method6700(var1, var3));
               this.headerBindLabel.set();
               Zenith.getInstance().getClientSounds().method1566();
            }

            BindCapture.end(this);
            return true;
         } else {
            for (InspectorRow var5 : this.visibleRows(this.module)) {
               if (var5.isVisible() && var5.keyPressed(var1, var2, var3)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.active && this.module != null) {
         for (InspectorRow var4 : this.visibleRows(this.module)) {
            if (var4.isVisible() && var4.charTyped(var1, var2)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Generated
   public Module getModule() {
      return this.module;
   }
}

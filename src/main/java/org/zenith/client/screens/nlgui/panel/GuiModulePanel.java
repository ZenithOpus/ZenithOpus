package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptyMap;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.BotModuleUiAdapter;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.GuiModuleRowElement;
import org.zenith.client.screens.nlgui.elements.Scrollbar;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class GuiModulePanel extends ElementPanel {
   private static final float SCROLL_SPEED = 22.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private final Animation animationChangeCategory = new Animation(200L, 1.0F, Easing.INSTANCE13);
   private int categoryDirection = 1;
   private static final float CATEGORY_TRAVEL = 16.0F;
   private static final float INCOMING_TAKEOVER = 0.2F;
   private float shownContentHeight = Float.NaN;
   private final Animation scrollbarHeight = new Animation(200L, Easing.INSTANCE10);
   private final Animation scrollbarPresence = new Animation(160L, 0.0F, Easing.INSTANCE10);
   private final Scrollbar scrollbar = new Scrollbar();
   private static final float SUBMODULE_INDENT = 12.0F;
   private final Map<ModuleCategory, List<GuiModuleRowElement>> categories = new HashMap<>();
   private final Map<GuiModuleRowElement, List<GuiModuleRowElement>> searchChildren = new HashMap<>();
   private final Map<GuiModuleRowElement, GuiModuleRowElement> searchParents = new HashMap<>();
   private final Set<GuiModuleRowElement> searchMatches = new HashSet<>();
   private final Map<GuiModuleRowElement, BoundsData> rowHitBounds = new LinkedHashMap<>();
   private String lastSearchQuery = "";
   private ModuleCategory currentCategory = ModuleCategory.COMBAT;
   private ModuleCategory lastCategory;
   private boolean botOnly;
   private final List<GuiModuleRowElement> botModuleElements = new ArrayList<>();
   private BoundsData scissorBounds;
   private float scroll = 0.0F;
   private float scrollTarget = 0.0F;
   private int caret = -1;

   public GuiModulePanel() {
      this.rebuild();
   }

   public void rebuild() {
      this.searchParents.keySet().forEach(GuiModuleRowElement::cancelBinding);
      this.searchChildren.clear();
      this.searchParents.clear();
      this.searchMatches.clear();
      this.rowHitBounds.clear();
      this.categories.clear();

      for (ModuleCategory var4 : ModuleCategory.values()) {
         this.categories
            .put(
               var4,
               Zenith.getInstance()
                  .getModuleManager()
                  .getItems()
                  .stream()
                  .filter(var1 -> var1.getCategory() == var4 && !var1.isHidden())
                  .map(GuiModuleRowElement::new)
                  .toList()
            );
      }

      this.categories.forEach((var1, var2) -> {
         for (GuiModuleRowElement var4x : var2) {
            var4x.resetSearchVisible(var1 == this.currentCategory);
         }
      });
      HashSet var12 = new HashSet();
      this.categories.values().forEach(var1 -> var1.forEach(var1x -> var12.add(var1x.getModule())));

      for (ModuleCategory var5 : ModuleCategory.values()) {
         for (GuiModuleRowElement var7 : this.categories.get(var5)) {
            ArrayList var8 = new ArrayList();

            for (Module var10 : var7.getModule().getSurfacedModules()) {
               if (var12.add(var10)) {
                  GuiModuleRowElement var11 = new GuiModuleRowElement(var10);
                  var11.resetSearchVisible(false);
                  var8.add(var11);
                  this.searchParents.put(var11, var7);
               }
            }

            if (!var8.isEmpty()) {
               this.searchChildren.put(var7, var8);
            }
         }
      }
   }

   @Override
   public boolean isHeaderSearchOnly() {
      return true;
   }

   @Override
   public float getContentWidth() {
      return 236.0F;
   }

   @Override
   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.rowHitBounds.clear();
      float var7 = Math.max(0.0F, NLMenuScreen.contentBottom - 20.0F - GuiStyle.PADDING.intValue() - var6);
      this.scissorBounds = new BoundsData(var5, var6, 236.0F, var7);
      this.animationChangeCategory.setDuration(MenuModule.INSTANCE.method5347());
      var var8 = this.getSearchRenderableElements(this.currentCategory);
      var var9 = this.getOrderedElements(var8);
      float var10 = this.orderedContentHeight(var9);
      this.clampScroll(var10, var7);
      this.scroll = this.scrollbar.isDragging() ? this.scrollTarget : this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float var11 = this.animationChangeCategory.method2808(1.0F);
      if (this.animationChangeCategory.isDone()) {
         this.lastCategory = null;
      }

      float var12 = MathHelper.clamp((0.45F - var11) / 0.45F, 0.0F, 1.0F);
      float var13 = MathHelper.clamp((var11 - 0.2F) / 0.8F, 0.0F, 1.0F);
      var1.enableScissor(var5, var6, var5 + 236.0F, var6 + var7);
      if (this.lastCategory != null && var12 > 0.0F) {
         this.renderElements(
            this.getSearchRenderableElements(this.lastCategory),
            var1,
            var2,
            var3,
            var4 * var12,
            var5,
            var6 - 16.0F * this.categoryDirection * (1.0F - var12),
            false
         );
      }

      if (var13 > 0.0F) {
         this.renderElements(var8, var9, var1, var2, var3, var4 * var13, var5, var6 + 16.0F * this.categoryDirection * (1.0F - var13), true);
      }

      var1.disableScissor();
      this.renderScrollbar(var1, var5, var6, var7, var10, var4);
   }

   private void renderScrollbar(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = var5 > var4;
      float var8 = this.scrollbarPresence.method2808(var7 ? 1.0F : 0.0F);
      if (var7) {
         if (!Float.isNaN(this.shownContentHeight) && !(var8 <= 0.001F)) {
            this.shownContentHeight = this.scrollbarHeight.method2808(var5);
         } else {
            this.shownContentHeight = var5;
            this.scrollbarHeight.reset(var5);
         }
      }

      if (!(var8 <= 0.001F) && !Float.isNaN(this.shownContentHeight)) {
         ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var9 == null) {
            this.scrollbar.clear();
         } else {
            this.scrollbar
               .render(
                  var1,
                  var2 + 236.0F - 2.0F,
                  var3,
                  var4,
                  this.shownContentHeight,
                  var5,
                  this.scroll,
                  var9.getTextTertiary().getZenithColor().method7294(var6 * var8 * 0.6F)
               );
         }
      } else {
         this.scrollbar.clear();
      }
   }

   @Override
   public void renderPriority(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
   }

   private void renderElements(List<GuiModuleRowElement> var1, MenuDrawContext var2, int var3, int var4, float var5, float var6, float var7, boolean var8) {
      this.renderElements(
         var1,
         (var1.isEmpty()) ? var1 : this.getOrderedElements(var1),
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8
      );
   }

   private void renderElements(
      List<GuiModuleRowElement> var1,
      List<GuiModuleRowElement> var2,
      MenuDrawContext var3,
      int var4,
      int var5,
      float var6,
      float var7,
      float var8,
      boolean var9
   ) {
      if (var1.isEmpty()) {
         ZenithStyle var40 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var40 != null) {
            Font var41 = Fonts.NEW_REGULAR.getFont(5.0F);
            String var42 = Zenith.getInstance().getLocalization().translate("gui.modules.empty");
            var3.drawText(var41, var42, var7 + (236.0F - var41.width(var42)) / 2.0F, var8 + 30.0F, var40.getTextTertiary().getZenithColor().method7294(var6));
         }
      } else {
         ZenithStyle var10 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var10 != null) {
            boolean var11 = !this.getSearchQuery().isEmpty();
            float var12;
            float var13 = var12 = var8 + this.scroll;
            String var14 = null;
            int var15 = 0;

            for (GuiModuleRowElement var17 : var2) {
               String var18 = this.groupOf(var17, var11);
               if (!Objects.equals(var18, var14)) {
                  var14 = var18;
                  if (var18 != null && !var18.isEmpty()) {
                     this.renderGroupHeader(var3, var7, var13, var18, var6);
                     var13 += 13.0F;
                  }
               }

               float var19;
               if ((var19 = var17.getSearchVisibleProgress()) <= 0.01F) {
                  var17.forgetSlide();
               } else {
                  boolean var10000;
                  label250: {
                     if (this.searchParents != null) {
                        if (this.searchParents.getClass() == EmptyMap.class) {
                           var10000 = false;
                           break label250;
                        }
                     }

                     var10000 = this.searchParents.containsKey(var17);
                  }

                  boolean var20 = var10000;
                  float var21 = var10000 ? 12.0F : 0.0F;
                  float var22 = var7 + var21 + (var20 ? (1.0F - var19) * 8.0F : 0.0F);
                  float var23 = 236.0F - (var22 - var7);
                  var17.setWidth(var23);
                  var17.setShowCategoryIcon(var11 && !var20);
                  float var24 = 16.0F * var19;
                  float var25 = var20 ? var13 - 16.0F * (1.0F - var19) : var12 + var17.slideTo(var13 - var12);
                  BoundsData var26 = new BoundsData(var22, var20 ? var13 : var25, var23, var24);
                  boolean var27 = this.searchMatches.contains(var17);
                  if (this.scissorBounds.method8093(var26)) {
                     float var28 = Math.max(this.scissorBounds.getFloatValue(), var26.getFloatValue());
                     float var29 = Math.max(this.scissorBounds.getFloatValue2(), var26.getFloatValue2());
                     float var30 = Math.min(this.scissorBounds.getFloatValue() + this.scissorBounds.width(), var26.getFloatValue() + var26.width());
                     float var31;
                     if ((var31 = Math.min(this.scissorBounds.getFloatValue2() + this.scissorBounds.height(), var26.getFloatValue2() + var26.height()))
                           <= var29
                        || var30 <= var28) {
                        var13 += var24;
                        if (var27) {
                           var15++;
                        }
                        continue;
                     }

                     BoundsData var32 = new BoundsData(var28, var29, var30 - var28, var31 - var29);
                     boolean var33;
                     if (var33 = var9 && var27 && this.animationChangeCategory.isDone()) {
                        this.rowHitBounds.put(var17, var32);
                     }

                     boolean var34;
                     if (var34 = var20 && var19 < 0.999F) {
                        var3.enableScissor(var28, var29, var30, var31);
                     }

                     try {
                        if (var9 && var27 && var15 == this.caret) {
                           var3.drawRect(var22, var25 + 2.0F, 1.5F, 12.0F, var10.getTextTertiary().getZenithColor().method7294(var6 * var19));
                        }

                        var17.render(var3, var33 ? var4 : -10000.0F, var33 ? var5 : -10000.0F, var22, var25, var6 * var19, var32);
                     } finally {
                        if (var34) {
                           var3.disableScissor();
                        }
                     }
                  }

                  var13 += var24;
                  if (var27) {
                     var15++;
                  }
               }
            }
         }
      }
   }

   private String groupOf(GuiModuleRowElement var1, boolean var2) {
      var1 = this.searchParents.getOrDefault(var1, var1);
      if (var2) {
         return var1.getModule().getCategory().getName();
      } else {
         return var1.getModule().isPriority() ? Zenith.getInstance().getLocalization().translate("gui.modules.favourites") : var1.getModule().getGroup();
      }
   }

   private void renderGroupHeader(MenuDrawContext var1, float var2, float var3, String var4, float var5) {
      ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var6 != null) {
         Font var7 = Fonts.NEW_MEDIUM.getFont(4.0F);
         var1.drawText(
            var7,
            HoverInfo.spaced(var4.toUpperCase(Locale.ROOT)),
            var2 + 8.0F,
            var3 + (13.0F - var7.height()) / 2.0F + 2.0F,
            var6.getTextTertiary().getZenithColor().method7294(var5)
         );
      }
   }

   private float getContentHeight(List<GuiModuleRowElement> var1) {
      return (var1.isEmpty()) ? 0.0F : this.orderedContentHeight(this.getOrderedElements(var1));
   }

   private float orderedContentHeight(List<GuiModuleRowElement> var1) {
      if (var1.isEmpty()) {
         return 0.0F;
      } else {
         boolean var2 = !this.getSearchQuery().isEmpty();
         float var3 = 0.0F;
         String var4 = null;

         for (GuiModuleRowElement var6 : var1) {
            String var7 = this.groupOf(var6, var2);
            if (!Objects.equals(var7, var4)) {
               var4 = var7;
               if (var7 != null && !var7.isEmpty()) {
                  var3 += 13.0F;
               }
            }

            float var8;
            if (!((var8 = var6.getSearchVisibleProgress()) <= 0.01F)) {
               var3 += 16.0F * var8;
            }
         }

         return var3;
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
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.scissorBounds != null && this.scissorBounds.contains(var1, var3)) {
         var var9 = this.getSearchRenderableElements(this.currentCategory);
         if (var9.isEmpty()) {
            return false;
         } else {
            float var10;
            if ((var10 = this.getContentHeight(var9)) <= this.scissorBounds.height()) {
               return false;
            } else {
               this.scrollTarget = (float)(this.scrollTarget + var7 * 22.0);
               this.clampScroll(var10, this.scissorBounds.height());
               return true;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      float var6 = this.scrollbar.onMouseClicked(var1, var3);
      if (this.scrollbar.isDragging()) {
         this.scrollTarget = Float.isNaN(var6) ? this.scroll : var6;
         if (this.scissorBounds != null) {
            this.clampScroll(this.getContentHeight(this.getSearchRenderableElements(this.currentCategory)), this.scissorBounds.height());
         }

         return true;
      } else if (!this.animationChangeCategory.isDone()) {
         return false;
      } else if (this.scissorBounds != null && this.scissorBounds.contains(var1, var3)) {
         this.getSearchRenderableElements(this.currentCategory);

         for (Entry var8 : this.rowHitBounds.entrySet()) {
            GuiModuleRowElement var9 = (GuiModuleRowElement)var8.getKey();
            if (this.searchMatches.contains(var9) && ((BoundsData)var8.getValue()).contains(var1, var3) && var9.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      this.scrollbar.release();
      return false;
   }

   @Override
   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      float var10;
      if (!Float.isNaN(var10 = this.scrollbar.onMouseDragged(var3))) {
         this.scrollTarget = var10;
         if (this.scissorBounds != null) {
            this.clampScroll(this.getContentHeight(this.getSearchRenderableElements(this.currentCategory)), this.scissorBounds.height());
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiModuleRowElement var5 : this.getSearchRenderableElements(this.currentCategory)) {
         if (this.searchMatches.contains(var5) && var5.isBinding() && var5.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   private List<GuiModuleRowElement> visibleOrdered() {
      ArrayList<GuiModuleRowElement> var1 = new ArrayList<>();

      for (GuiModuleRowElement var3 : this.getOrderedElements(this.getSearchRenderableElements(this.currentCategory))) {
         if (this.searchMatches.contains(var3) && var3.getSearchVisibleProgress() > 0.01F) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public void moveCaret(int var1) {
      var var2 = this.visibleOrdered();
      if (var2.isEmpty()) {
         this.caret = -1;
      } else {
         if (this.caret < 0) {
            this.caret = var1 > 0 ? 0 : var2.size() - 1;
         } else {
            this.caret = MathHelper.clamp(this.caret + var1, 0, var2.size() - 1);
         }

         this.ensureCaretVisible(var2);
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().showFor(((GuiModuleRowElement)var2.get(this.caret)).getModule());
      }
   }

   private void ensureCaretVisible(List<GuiModuleRowElement> var1) {
      if (this.scissorBounds != null && this.caret >= 0 && this.caret < var1.size()) {
         boolean var2 = !this.getSearchQuery().isEmpty();
         float var3 = 0.0F;
         String var4 = null;
         GuiModuleRowElement var5 = (GuiModuleRowElement)var1.get(this.caret);

         for (GuiModuleRowElement var7 : this.getOrderedElements(this.getSearchRenderableElements(this.currentCategory))) {
            String var8 = this.groupOf(var7, var2);
            if (!Objects.equals(var8, var4)) {
               var4 = var8;
               if (var8 != null && !var8.isEmpty()) {
                  var3 += 13.0F;
               }
            }

            float var9;
            if (!((var9 = var7.getSearchVisibleProgress()) <= 0.01F)) {
               if (var7 == var5) {
                  break;
               }

               var3 += 16.0F * var9;
            }
         }

         float var10 = this.scissorBounds.height();
         float var11 = var3 + 16.0F * var5.getSearchVisibleProgress();
         if (var3 + this.scrollTarget < 0.0F) {
            this.scrollTarget = -var3;
         } else if (var11 + this.scrollTarget > var10) {
            this.scrollTarget = var10 - var11;
         }

         this.clampScroll(this.getContentHeight(this.getSearchRenderableElements(this.currentCategory)), var10);
      }
   }

   public boolean toggleCaret() {
      var var1 = this.visibleOrdered();
      if (this.caret >= 0 && this.caret < var1.size()) {
         ((GuiModuleRowElement)var1.get(this.caret)).getModule().toggle();
         return true;
      } else {
         return false;
      }
   }

   public boolean openCaret() {
      var var1 = this.visibleOrdered();
      if (this.caret >= 0 && this.caret < var1.size()) {
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().showFor(((GuiModuleRowElement)var1.get(this.caret)).getModule());
         return true;
      } else {
         return false;
      }
   }

   public void clearCaret() {
      this.caret = -1;
   }

   private List<GuiModuleRowElement> getOrderedElements(List<GuiModuleRowElement> var1) {
      ArrayList<GuiModuleRowElement> var2 = new ArrayList(var1.size());
      boolean var3 = !this.getSearchQuery().isEmpty();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      LinkedHashMap var6 = new LinkedHashMap();
      HashMap var7 = new HashMap();

      for (GuiModuleRowElement var9 : var1) {
         boolean var10000;
         label58: {
            if (this.searchParents != null) {
               if (this.searchParents.getClass() == EmptyMap.class) {
                  var10000 = false;
                  break label58;
               }
            }

            var10000 = this.searchParents.containsKey(var9);
         }

         if (!var10000) {
            if (var9.getModule().isPriority()) {
               if (var3) {
                  String var10 = this.groupOf(var9, true);
                  int var11 = ((Integer)var7.getOrDefault(var10, 0));
                  ((java.util.List)var6.computeIfAbsent(var10, var0 -> new ArrayList<>())).add(var11, var9);
                  var7.put(var10, var11 + 1);
               } else {
                  var4.add(var9);
               }
            } else if (var3) {
               ((java.util.List)var6.computeIfAbsent(this.groupOf(var9, true), var0 -> new ArrayList<>())).add(var9);
            } else {
               String var16 = var9.getModule().getGroup();
               if (var16.isEmpty()) {
                  var5.add(var9);
               } else {
                  ((java.util.List)var6.computeIfAbsent(var16, var0 -> new ArrayList<>())).add(var9);
               }
            }
         }
      }

      var2.addAll(var4);
      var2.addAll(var5);
      var6.values().forEach(c -> var2.addAll((java.util.Collection)c));
      var var14 = new HashSet<>(var1);
      List<GuiModuleRowElement> var15 = new ArrayList<>(var1.size());

      for (Object var18_o : (Iterable<?>)(Object)(var2)) {
         GuiModuleRowElement var18 = (GuiModuleRowElement)var18_o;
         var15.add(var18);

         for (GuiModuleRowElement var13 : this.searchChildren.getOrDefault(var18, Collections.emptyList())) {
            if (var14.contains(var13)) {
               var15.add(var13);
            }
         }
      }

      return var15;
   }

   public void setCategory(ModuleCategory var1) {
      if (this.currentCategory != var1) {
         if (Zenith.getInstance().getNLMenuScreen().getType() == NLMenuScreen.ElementsType.CATEGORY) {
            float var2 = this.animationChangeCategory.getValue();
            boolean var3;
            if ((var3 = this.lastCategory != null && !this.animationChangeCategory.isDone()) && this.lastCategory == var1) {
               this.lastCategory = this.currentCategory;
               this.categoryDirection = -this.categoryDirection;
               this.animationChangeCategory.reset(1.0F - var2);
               this.animationChangeCategory.method2810(1.0F);
            } else if (var3 && var2 < 0.2F) {
               this.categoryDirection = var1.ordinal() >= this.lastCategory.ordinal() ? 1 : -1;
            } else {
               this.lastCategory = this.currentCategory;
               this.categoryDirection = var1.ordinal() >= this.currentCategory.ordinal() ? 1 : -1;
               this.animationChangeCategory.reset(0.0F);
               this.animationChangeCategory.method2810(1.0F);
            }

            this.scrollTarget = 0.0F;
         }

         this.currentCategory = var1;
         this.caret = -1;
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().close();
      }
   }

   public boolean isCategorySwitching() {
      return this.lastCategory != null && !this.animationChangeCategory.isDone();
   }

   public void setBotOnly(boolean var1) {
      if (this.botOnly != var1) {
         this.botOnly = var1;
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
         this.caret = -1;
      }
   }

   public void forceCategory(ModuleCategory var1) {
      this.currentCategory = var1;
      this.lastCategory = null;
      this.animationChangeCategory.reset(1.0F);
      this.scrollTarget = 0.0F;
      this.scroll = 0.0F;
      this.caret = -1;
      this.shownContentHeight = Float.NaN;
   }

   public void setBotContext(String var1) {
      this.botModuleElements.clear();
      BotClient var2 = HeadlessBots.get(var1);
      if (var2 != null) {
         for (BotModule var4 : var2.getModules().getModules()) {
            GuiModuleRowElement var5 = new GuiModuleRowElement(new BotModuleUiAdapter(var1, var4));
            var5.resetSearchVisible(true);
            this.botModuleElements.add(var5);
         }
      }
   }

   private List<GuiModuleRowElement> applyBotFilter(List<GuiModuleRowElement> var1) {
      return this.botOnly && (!var1.isEmpty())
         ? var1.stream().filter(var0 -> var0.getModule().isBotModule()).toList()
         : var1;
   }

   private List<GuiModuleRowElement> withBotElements(List<GuiModuleRowElement> var1, boolean var2) {
      if (this.botOnly
         && var2
         && (this.botModuleElements != null && this.botModuleElements.getClass() == EmptyList.class ? false : !this.botModuleElements.isEmpty())) {
         ArrayList<GuiModuleRowElement> var3 = new ArrayList(var1);
         var3.addAll(this.botModuleElements);
         return var3;
      } else {
         return var1;
      }
   }

   @Override
   public List<GuiModuleRowElement> getElements() {
      return this.getSearchRenderableElements(this.currentCategory);
   }

   public int getVisibleCount() {
      int var1 = 0;

      for (GuiModuleRowElement var3 : this.getSearchRenderableElements(this.currentCategory)) {
         if (this.searchMatches.contains(var3) && var3.getSearchVisibleProgress() > 0.01F) {
            var1++;
         }
      }

      return var1;
   }

   public Module findModule(String var1) {
      for (List var3 : this.categories.values()) {
         for (Object var5_o : (Iterable<?>)(Object)(var3)) {
            GuiModuleRowElement var5 = (GuiModuleRowElement)var5_o;
            if (var5.getName().equalsIgnoreCase(var1)) {
               return var5.getModule();
            }
         }
      }

      return null;
   }

   private List<GuiModuleRowElement> getSearchRenderableElements(ModuleCategory var1) {
      String var2 = this.getSearchQuery();
      boolean var3 = !var2.isEmpty();
      if (!var2.equals(this.lastSearchQuery)) {
         if (var2.isEmpty()) {
            InspectorPanel var4 = Zenith.getInstance().getNLMenuScreen().getInspectorPanel();

            for (Entry var6 : this.searchParents.entrySet()) {
               if (var4.isShowing(((GuiModuleRowElement)var6.getKey()).getModule())) {
                  var4.showFor(((GuiModuleRowElement)var6.getValue()).getModule());
                  break;
               }
            }
         }

         this.lastSearchQuery = var2;
         this.caret = -1;
         this.scrollTarget = 0.0F;
         this.rowHitBounds.clear();
      }

      List<GuiModuleRowElement> var14 = this.getSearchSourceElements(var1);
      var var15 = new HashSet<>(var14);
      this.searchMatches.clear();

      for (GuiModuleRowElement var7 : this.searchParents.keySet()) {
         GuiModuleRowElement var8 = this.searchParents.get(var7);
         boolean var9 = !this.botOnly || var7.getModule().isBotModule();
         boolean var10 = var3 && var15.contains(var8) && var9 && (var8.matchesOwnSearch(var2) || var7.matchesOwnSearch(var2));
         var7.setSearchVisible(var10);
         var7.updateSearchVisible();
         if (var10) {
            this.searchMatches.add(var7);
         }
      }

      List<GuiModuleRowElement> var17 = new ArrayList<>(var14.size());

      for (Object var19_o : (Iterable<?>)(Object)(var14)) {
         GuiModuleRowElement var19 = (GuiModuleRowElement)var19_o;
         List<GuiModuleRowElement> var20 = this.searchChildren.getOrDefault(var19, Collections.emptyList());
         boolean var21 = !var3 || var19.matchesOwnSearch(var2);
         boolean var11 = false;

         for (Object var13_o : (Iterable<?>)(Object)(var20)) {
            GuiModuleRowElement var13 = (GuiModuleRowElement)var13_o;
            var21 |= this.searchMatches.contains(var13);
            var11 |= var13.getSearchVisibleProgress() > 0.01F;
         }

         var19.setSearchVisible(var21);
         var19.updateSearchVisible();
         if (var21) {
            this.searchMatches.add(var19);
         }

         if (var19.shouldRenderInSearch() || var11) {
            var17.add(var19);

            for (Object var23_o : (Iterable<?>)(Object)(var20)) {
               GuiModuleRowElement var23 = (GuiModuleRowElement)var23_o;
               if (var23.shouldRenderInSearch()) {
                  var17.add(var23);
               }
            }
         }
      }

      return var17;
   }

   private List<GuiModuleRowElement> getSearchSourceElements(ModuleCategory var1) {
      if (this.getSearchQuery().isEmpty()) {
         var var7 = this.categories.get(var1);
         return var7 == null ? Collections.emptyList() : this.withBotElements(this.applyBotFilter(var7), var1 == ModuleCategory.PLAYER);
      } else {
         ArrayList var2 = new ArrayList();

         for (ModuleCategory var6 : ModuleCategory.values()) {
            var2.addAll(this.categories.getOrDefault(var6, Collections.emptyList()));
         }

         return this.withBotElements(this.applyBotFilter(var2), true);
      }
   }

   private String getSearchQuery() {
      String var1 = Zenith.getInstance().getNLMenuScreen().getSearchValue();
      return var1 == null ? "" : var1.trim().toLowerCase();
   }

   @Override
   public boolean isRender() {
      return false;
   }

   @Override
   public void close() {
      this.animationChangeCategory.reset(1.0F);
      this.lastCategory = null;
      this.caret = -1;
      this.rowHitBounds.clear();

      for (GuiModuleRowElement var2 : this.searchParents.keySet()) {
         var2.cancelBinding();
         var2.resetSearchVisible(false);
      }

      this.shownContentHeight = Float.NaN;
      this.scrollbarPresence.reset(0.0F);
   }

   @Generated
   public ModuleCategory getCurrentCategory() {
      return this.currentCategory;
   }

   @Generated
   public boolean isBotOnly() {
      return this.botOnly;
   }
}

package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import kotlin.collections.EmptyList;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.elements.PressFx;
import org.zenith.client.screens.nlgui.elements.Scrollbar;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public abstract class SectionPanel extends ElementPanel {
   public static final float INSET = 8.0F;
   private static final float MIN_TAB_PAD = 3.0F;
   private static final float PLATE_INSET = 2.0F;
   private static final float MIN_INLINE_TAB_FONT = 4.5F;
   private static final float SCROLL_TAB_PAD = 6.0F;
   public static final float CAPTION_HEIGHT = 16.0F;
   public static final float LEAD_CAPTION_TRIM = 6.0F;
   public static final float ICON_COLUMN = 9.0F;
   private final List<SectionPanel.Entry> mainEntries = new ArrayList<>();
   private final List<SectionPanel.Entry> asideEntries = new ArrayList<>();
   private final List<String> tabs = new ArrayList<>();
   private final List<MenuGlyph> tabIcons = new ArrayList<>();
   private SectionPanel.AsideRenderer asideFull;
   private final Animation plateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private final Animation switchAnimation = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private boolean built;
   private boolean embedded;
   private int activeTab;
   private float panelAlpha = 1.0F;
   private float scroll;
   private float scrollTarget;
   private BoundsData mainBounds;
   private BoundsData asideBounds;
   private float asideScroll;
   private float asideScrollTarget;
   private BoundsData footerBounds;
   private BoundsData mainHeaderBounds;
   private BoundsData lastRightClickBounds;
   private final List<BoundsData> tabBounds = new ArrayList<>();
   private float tabScroll;
   private float tabScrollTarget;
   private boolean tabsScrollable;
   private int tabScrolledTo = -1;
   private BoundsData tabViewport;
   private final Scrollbar mainScrollbar = new Scrollbar();
   private final Scrollbar asideScrollbar = new Scrollbar();
   public final PressFx pressFx = new PressFx();
   private static final float VALUE_GAP = 6.0F;

   public boolean pressFeedback() {
      return false;
   }

   public float entryEnter(int var1, boolean var2) {
      return 1.0F;
   }

   public int mainEntryCount() {
      return this.mainEntries.size();
   }

   public int asideEntryCount() {
      return this.asideEntries.size();
   }

   private void renderEntry(
      SectionPanel.Entry var1,
      String var2,
      int var3,
      boolean var4,
      MenuDrawContext var5,
      int var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      ZenithStyle var12
   ) {
      float var13;
      if (!((var13 = this.entryEnter(var3, var4)) <= 0.001F)) {
         boolean var14;
         if (var14 = var13 < 0.999F) {
            var5.pushMatrix();
            var5.getMatrices().translate((1.0F - var13) * 4.0F, (1.0F - var13) * 6.0F);
         }

         float var15 = var11 * var13;
         boolean var16;
         BoundsData var17 = (var16 = this.pressFeedback() && var1.kind != SectionPanel.Entry.Kind.SETTING && var1.kind != SectionPanel.Entry.Kind.CAPTION)
            ? new BoundsData(var8, var9, var10, var1.height())
            : null;
         boolean var18 = var16 && this.pressFx.begin(var5, var2, var17);
         var1.render(var5, var6, var7, var8, var9, var10, var15, var12);
         if (var16 && var1.kind == SectionPanel.Entry.Kind.ROW) {
            this.pressFx.ink(var5, var2, var17, 3.0F, var12.getPrimaryColor().getZenithColor().method7292(0.2F), var15);
         }

         this.pressFx.end(var5, var18);
         if (var14) {
            var5.popMatrix();
         }
      }
   }

   private void markPress(SectionPanel.Entry var1, String var2, double var3, double var5) {
      if (this.pressFeedback() && var1.kind != SectionPanel.Entry.Kind.SETTING) {
         this.pressFx.press(var2, var3, var5);
      }
   }

   public static String t(String var0) {
      return var0 == null ? "" : Zenith.getInstance().getLocalization().translate(var0);
   }

   public abstract void build();

   public abstract String headerTitle();

   @Override
   public float getHeaderTitleWidth() {
      return 19.0F + Fonts.NEW_MEDIUM.getFont(5.5F).width(this.headerTitle());
   }

   public abstract MenuGlyph headerIcon();

   public abstract String sectionDescription();

   public void invalidate() {
      this.built = false;
   }

   public void tabs(String... var1) {
      this.tabs.clear();

      for (String var5 : var1) {
         this.tabs.add(var5);
      }

      if (this.activeTab >= this.tabs.size()) {
         this.activeTab = 0;
      }
   }

   public void tabIcons(MenuGlyph... var1) {
      this.tabIcons.clear();

      for (MenuGlyph var5 : var1) {
         this.tabIcons.add(var5);
      }
   }

   private void restartSwitch() {
      this.switchAnimation.reset(0.0F);
      this.switchAnimation.method2810(1.0F);
   }

   public int getActiveTab() {
      return this.activeTab;
   }

   public void setEmbedded(boolean var1) {
      this.embedded = var1;
   }

   private float inlineTabsHeight() {
      return this.embedded && (this.tabs != null && this.tabs.getClass() == EmptyList.class ? false : !this.tabs.isEmpty()) ? 19.0F : 0.0F;
   }

   public String searchQuery() {
      String var1 = Zenith.getInstance().getNLMenuScreen().getSearchValue();
      return var1 == null ? "" : var1.trim().toLowerCase(Locale.ROOT);
   }

   public void selectTab(int var1) {
      if (this.activeTab != var1) {
         this.activeTab = var1;
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.resetAsideScroll();
         this.restartSwitch();
         this.invalidate();
      }
   }

   public void caption(String var1) {
      this.mainEntries
         .add(
            SectionPanel.Entry.caption(
               var1,
               (this.mainEntries != null && this.mainEntries.getClass() == EmptyList.class ? true : this.mainEntries.isEmpty())
                  && this.mainHeaderHeight() <= 0.0F
            )
         );
   }

   public void setting(Setting var1) {
      if (var1 != null) {
         this.mainEntries.add(SectionPanel.Entry.setting(new InspectorRow(var1, 0)));
      }
   }

   public void setting(InspectorRow var1) {
      if (var1 != null) {
         this.mainEntries.add(SectionPanel.Entry.setting(var1));
      }
   }

   public void info(String var1, Supplier<String> var2, String var3) {
      this.row(var1).value(var2).description(var3).add();
   }

   public void action(String var1, MenuGlyph var2, Supplier<String> var3, Runnable var4, String var5) {
      this.row(var1).icon(var2).value(var3).onClick(var4).description(var5).add();
   }

   public SectionPanel.RowBuilder row(String var1) {
      return new SectionPanel.RowBuilder(var1, this.mainEntries);
   }

   public void custom(float var1, SectionPanel.AsideRenderer var2) {
      this.mainEntries.add(SectionPanel.Entry.custom(var1, var2));
   }

   public void custom(float var1, SectionPanel.AsideRenderer var2, Runnable var3, BooleanSupplier var4) {
      this.mainEntries.add(SectionPanel.Entry.custom(var1, var2, var3, var4, null));
   }

   public void custom(float var1, SectionPanel.AsideRenderer var2, Runnable var3, BooleanSupplier var4, Runnable var5) {
      this.mainEntries.add(SectionPanel.Entry.custom(var1, var2, var3, var4, var5));
   }

   public BoundsData footerBounds() {
      return this.footerBounds;
   }

   public float mainHeaderHeight() {
      return 0.0F;
   }

   public void renderMainHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
   }

   public BoundsData mainHeaderBounds() {
      return this.mainHeaderBounds;
   }

   public BoundsData lastRightClickBounds() {
      return this.lastRightClickBounds;
   }

   public void gap(float var1) {
      this.mainEntries.add(SectionPanel.Entry.custom(var1, (var0, var1x, var2, var3, var4, var5, var6, var7) -> {}));
   }

   public void asideCaption(String var1) {
      this.asideEntries
         .add(
            SectionPanel.Entry.caption(var1, this.asideEntries != null && this.asideEntries.getClass() == EmptyList.class ? true : this.asideEntries.isEmpty())
         );
   }

   public void asideSetting(Setting var1) {
      if (var1 != null) {
         this.asideEntries.add(SectionPanel.Entry.setting(new InspectorRow(var1, 0)));
      }
   }

   public void asideSetting(InspectorRow var1) {
      if (var1 != null) {
         this.asideEntries.add(SectionPanel.Entry.setting(var1));
      }
   }

   public void asideCustom(float var1, SectionPanel.AsideRenderer var2, Runnable var3, BooleanSupplier var4) {
      this.asideEntries.add(SectionPanel.Entry.custom(var1, var2, var3, var4, null));
   }

   public void asideCustom(float var1, SectionPanel.AsideRenderer var2, Runnable var3, BooleanSupplier var4, Runnable var5) {
      this.asideEntries.add(SectionPanel.Entry.custom(var1, var2, var3, var4, var5));
   }

   public void asideInfo(String var1, Supplier<String> var2, String var3) {
      this.asideEntries.add(SectionPanel.Entry.info(var1, var2, var3));
   }

   public void asideAction(String var1, MenuGlyph var2, Supplier<String> var3, Runnable var4, String var5) {
      this.asideEntries.add(SectionPanel.Entry.action(var1, var2, var3, var4, var5));
   }

   public void asideCustom(float var1, SectionPanel.AsideRenderer var2) {
      this.asideEntries.add(SectionPanel.Entry.custom(var1, var2));
   }

   public void asideFull(SectionPanel.AsideRenderer var1) {
      this.asideFull = var1;
   }

   private void ensureBuilt() {
      if (!this.built) {
         this.built = true;
         this.mainEntries.clear();
         this.asideEntries.clear();
         this.asideFull = null;
         this.build();
      }
   }

   @Override
   public boolean isHeaderSearchOnly() {
      return false;
   }

   @Override
   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float var10 = var5 + 8.0F;
         this.headerIcon().draw(var1, var10, var6 + 8.5F, var8.getTextEnable().getZenithColor().method7294(var4));
         var1.drawText(var9, this.headerTitle(), var10 + 11.0F, var6 + (23.0F - var9.height()) / 2.0F, var8.getTextEnable().getZenithColor().method7294(var4));
      }
   }

   @Override
   public float getButtonWidth() {
      this.ensureBuilt();
      if ((this.tabs != null && this.tabs.getClass() == EmptyList.class ? false : !this.tabs.isEmpty()) && !this.embedded) {
         Font var1 = Fonts.NEW_MEDIUM.getFont(5.0F);
         float var2 = 0.0F;

         for (String var4 : this.tabs) {
            var2 += var1.width(t(var4)) + 16.0F;
         }

         return var2;
      } else {
         return 0.0F;
      }
   }

   @Override
   public void renderHeaderButtons(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
      if (!this.embedded) {
         this.ensureBuilt();
         ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var9 != null && (this.tabs != null && this.tabs.getClass() == EmptyList.class ? false : !this.tabs.isEmpty())) {
            this.renderTabStrip(var1, var2, var3, var4, var5, var6, 23.0F, var7, var9, false);
         }
      }
   }

   private void renderTabStrip(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9, boolean var10
   ) {
      Font var11 = Fonts.NEW_MEDIUM.getFont(5.0F);
      this.tabBounds.clear();
      boolean var12 = this.tabIcons != null && this.tabIcons.getClass() == EmptyList.class ? false : !this.tabIcons.isEmpty();
      float var13 = 8.0F;
      boolean var14 = false;
      float var15 = var10 ? var5 + 8.0F - 2.0F : var5;
      float var16 = var10 ? var8 - 12.0F : var8;
      if (var10) {
         if (this.tabsWidth(var11, var12, var13) > var16) {
            var12 = false;
            float var17 = this.tabsWidth(var11, false, 0.0F);
            float var18;
            if ((var18 = var16 - var17) >= this.tabs.size() * 3.0F * 2.0F) {
               var13 = Math.min(8.0F, var18 / (this.tabs.size() * 2.0F));
            } else {
               float var19 = var16 - this.tabs.size() * 3.0F * 2.0F;
               float var20;
               if ((var20 = (float)Math.floor(5.0F * var19 / var17 * 10.0F) / 10.0F) >= 4.5F) {
                  var11 = Fonts.NEW_MEDIUM.getFont(var20);
                  var13 = 3.0F;
               } else {
                  var14 = true;
                  var13 = 6.0F;
               }
            }
         }
      } else {
         float var37;
         if ((var37 = this.tabsWidth(var11, false, 3.0F)) > var8 && var37 > 0.0F) {
            float var39 = this.tabsWidth(var11, false, 0.0F);
            float var42 = var8 - this.tabs.size() * 3.0F * 2.0F;
            float var44 = Math.max(3.8F, Math.round(5.0F * var42 / var39 * 10.0F) / 10.0F);
            var11 = Fonts.NEW_MEDIUM.getFont(var44);
         }

         for (int var40 = 0; var40 < 2 && !(this.tabsWidth(var11, var12, var13) <= var8); var40++) {
            if (var40 != 0 || !var12) {
               float var45 = this.tabsWidth(var11, var12, 0.0F);
               var13 = Math.max(3.0F, (var8 - var45) / (this.tabs.size() * 2.0F));
               break;
            }

            var12 = false;
         }
      }

      float var38 = 0.0F;
      float var41 = 0.0F;
      float var43 = 0.0F;
      float[] var46 = new float[this.tabs.size()];
      float[] var21 = new float[this.tabs.size()];

      for (int var22 = 0; var22 < this.tabs.size(); var22++) {
         String var23 = t(this.tabs.get(var22));
         boolean var24 = var12 && var22 < this.tabIcons.size() && this.tabIcons.get(var22) != null;
         float var25 = var11.width(var23) + var13 * 2.0F + (var24 ? 9.0F : 0.0F);
         var46[var22] = var38;
         var21[var22] = var25;
         if (var22 == this.activeTab) {
            var41 = var38;
            var43 = var25;
         }

         var38 += var25;
      }

      float var47 = 0.0F;
      if (var14) {
         float var48 = Math.max(0.0F, var38 - var16);
         if (this.tabScrolledTo != this.activeTab) {
            this.tabScrolledTo = this.activeTab;
            float var51 = 12.0F;
            if (var41 - var51 < this.tabScrollTarget) {
               this.tabScrollTarget = var41 - var51;
            } else if (var41 + var43 + var51 > this.tabScrollTarget + var16) {
               this.tabScrollTarget = var41 + var43 + var51 - var16;
            }
         }

         this.tabScrollTarget = MathHelper.clamp(this.tabScrollTarget, 0.0F, var48);
         this.tabScroll = MathHelper.clamp(this.tabScroll + (this.tabScrollTarget - this.tabScroll) * 0.25F, 0.0F, var48);
         var47 = this.tabScroll;
      } else {
         this.tabScroll = 0.0F;
         this.tabScrollTarget = 0.0F;
         this.tabScrolledTo = -1;
      }

      this.tabsScrollable = var14;
      this.tabViewport = var10 ? new BoundsData(var15 + 2.0F, var6, var16 - 4.0F, var7) : null;

      for (int var49 = 0; var49 < this.tabs.size(); var49++) {
         this.tabBounds.add(new BoundsData(var15 + var46[var49] - var47, var6, var21[var49], var7));
      }

      if (!this.plateReady) {
         this.plateX.method2809(var41);
         this.plateWidth.method2809(var43);
         this.plateReady = true;
      }

      float var50 = var15 - var47 + this.plateX.method2808(var41);
      float var52 = this.plateWidth.method2808(var43);
      float var53 = var7 - 7.0F;
      float var26;
      float var27 = Math.min(var26 = Math.max(0.0F, var52 - 4.0F), var53) / 2.0F;
      var1.drawRoundedRect(
         var50 + 2.0F,
         var6 + (var7 - var53) / 2.0F,
         var26,
         var53,
         ProxyDotRadiusData.method8069(var27),
         var9.getDisableActiveBg().getZenithColor().method7294(var4)
      );
      float var28 = var15 + var16;

      for (int var29 = 0; var29 < this.tabs.size(); var29++) {
         BoundsData var30 = this.tabBounds.get(var29);
         String var31 = t(this.tabs.get(var29));
         boolean var32 = var29 == this.activeTab;
         boolean var33 = var30.contains(var2, var3) && (this.tabViewport == null || this.tabViewport.contains(var2, var3));
         float var34 = 1.0F;
         if (var14) {
            var34 = (
                  var34 = MathHelper.clamp(
                     (Math.min(var30.getFloatValue() + var30.width(), var28) - Math.max(var30.getFloatValue(), var15)) / var30.width(), 0.0F, 1.0F
                  )
               )
               * var34;
         }

         ZenithColor var35 = (var32 ? var9.getTextEnable() : var9.getTextTertiary())
            .getZenithColor()
            .method7295(var9.getTextEnable().getZenithColor(), var33 && !var32 ? 0.5F : 0.0F)
            .method7294(var4 * var34);
         float var36 = var30.getFloatValue() + var13;
         if (var12 && var29 < this.tabIcons.size() && this.tabIcons.get(var29) != null) {
            this.tabIcons.get(var29).draw(var1, var36, var6 + (var7 - 6.0F) / 2.0F, var35);
            var36 += 9.0F;
         }

         var1.drawText(var11, var31, var36, var6 + (var7 - var11.height()) / 2.0F, var35);
      }
   }

   private float tabsWidth(Font var1, boolean var2, float var3) {
      float var4 = 0.0F;

      for (int var5 = 0; var5 < this.tabs.size(); var5++) {
         boolean var6 = var2 && var5 < this.tabIcons.size() && this.tabIcons.get(var5) != null;
         var4 += var1.width(t(this.tabs.get(var5))) + var3 * 2.0F + (var6 ? 9.0F : 0.0F);
      }

      return var4;
   }

   @Override
   public boolean onHeaderButtonsClicked(double var1, double var3, Enum3 var5) {
      return this.clickTabs(var1, var3);
   }

   private boolean clickTabs(double var1, double var3) {
      if (this.tabViewport != null && !this.tabViewport.contains(var1, var3)) {
         return false;
      } else {
         for (int var5 = 0; var5 < this.tabBounds.size(); var5++) {
            if (this.tabBounds.get(var5).contains(var1, var3)) {
               boolean var6 = var5 != this.activeTab;
               this.selectTab(var5);
               if (var6) {
                  Zenith.getInstance().getClientSounds().method1564(var5, Math.max(1, this.tabBounds.size()));
               }

               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.ensureBuilt();
      this.panelAlpha = var4;
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         float var8 = NLMenuScreen.contentBottom - var6;
         boolean var9 = this.asideFirst();
         float var10 = this.asideWidth();
         float var11 = this.mainWidth();
         boolean var12 = var10 >= 1.0F;
         float var13 = var9 ? var5 : var5 + var11;
         float var14 = var9 ? var5 + var10 : var5;
         float var15 = var9 ? var5 + var10 : var5 + var11;
         if (var12) {
            var1.drawRect(var15, var6, 1.0F, var8 - GuiStyle.PADDING.intValue(), var7.getFieldBorder().getZenithColor().method7294(var4 * 2.0F));
         }

         float var16 = this.switchAnimation.method2808(1.0F);
         float var17 = var4 * var16;
         float var18 = (1.0F - var16) * 6.0F;
         this.renderMain(var1, var2, var3, var14, var6 + var18, var8 - var18, var17, var7);
         if (var12) {
            this.renderAside(var1, var2, var3, var13, var6 + var18, var8 - var18, var17, var7);
         } else if (!HoverInfo.hasText()) {
            HoverInfo.request(this.headerTitle(), t(this.sectionDescription()));
         }

         this.renderFloating(var1, var2, var3, var17, var7);
      }
   }

   public void renderFloating(MenuDrawContext var1, int var2, int var3, float var4, ZenithStyle var5) {
   }

   private void renderMain(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = this.mainWidth();
      float var10;
      if ((var10 = this.inlineTabsHeight()) > 0.0F) {
         MsdfRenderer.flushBatch();
         var1.enableScissor(var4 + 8.0F, var5, var4 + var9 - 8.0F, var5 + var10);
         this.renderTabStrip(var1, var2, var3, Math.min(1.0F, var7 / Math.max(0.001F, this.switchAnimation.getValue())), var4, var5, var10, var9, var8, true);
         MsdfRenderer.flushBatch();
         var1.disableScissor();
         var1.drawRect(var4 + 8.0F, var5 + var10, var9 - 16.0F, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var7));
         var5 += var10 + GuiStyle.PADDING.intValue();
         var6 -= var10 + GuiStyle.PADDING.intValue();
      }

      float var11;
      if ((var11 = this.mainHeaderHeight()) > 0.0F) {
         this.mainHeaderBounds = new BoundsData(var4, var5, var9, var11);
         this.renderMainHeader(var1, var2, var3, var4, var5, var9, var7, var8);
         var5 += var11;
         var6 -= var11;
      } else {
         this.mainHeaderBounds = null;
      }

      float var12 = this.footerHeight();
      float var13 = var6 - GuiStyle.PADDING.intValue() - var12;
      this.mainBounds = new BoundsData(var4, var5, var9, var13);
      if (var12 > 0.0F) {
         this.footerBounds = new BoundsData(var4, var5 + var13, var9, var12);
         this.renderFooter(var1, var2, var3, var4, var5 + var13, var9, var7, var8);
      } else {
         this.footerBounds = null;
      }

      InspectorRow.alignTracks(settingRows(this.mainEntries), var9);
      float var14 = 0.0F;

      for (SectionPanel.Entry var16 : this.mainEntries) {
         var14 += var16.height();
      }

      float var24 = var12 > 0.0F ? var13 - GuiStyle.PADDING.intValue() * 2.0F : var13;
      this.clampScroll(var14, var24);
      this.scroll = this.mainScrollbar.isDragging() ? this.scrollTarget : this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      if (this.mainEntries != null && this.mainEntries.getClass() == EmptyList.class ? true : this.mainEntries.isEmpty()) {
         Font var26 = Fonts.NEW_REGULAR.getFont(5.0F);
         float var27 = var5 + 40.0F;

         for (String var29 : wrapText(t(this.emptyText()), var26, var9 - 16.0F, 4)) {
            var1.drawText(var26, var29, var4 + (var9 - var26.width(var29)) / 2.0F, var27, var8.getTextTertiary().getZenithColor().method7294(var7));
            var27 += var26.height() + 2.0F;
         }
      } else {
         float var25 = var5 + var24;
         MsdfRenderer.flushBatch();
         var1.enableScissor(var4, var5, var4 + var9, var25);
         float var17 = var5 + this.scroll;
         float var18 = var25;
         int var19 = 0;

         for (SectionPanel.Entry var21 : this.mainEntries) {
            int var22 = var19++;
            float var23 = var21.height();
            if (var21.cullable() && (!(var17 + var23 > var5) || !(var17 < var18))) {
               var21.detach();
            } else {
               this.renderEntry(var21, "main:" + var22, var22, false, var1, var2, var3, var4, var17, var9, var7, var8);
            }

            var17 += var23;
         }

         this.renderMainOverlay(var1);
         MsdfRenderer.flushBatch();
         var1.disableScissor();
         this.mainScrollbar.render(var1, var4 + var9 - 2.0F, var5, var24, var14, this.scroll, var8.getTextTertiary().getZenithColor().method7294(var7 * 0.6F));
      }
   }

   public static List<String> wrapText(String var0, Font var1, float var2, int var3) {
      ArrayList<String> var4 = new ArrayList<>();
      StringBuilder var5 = new StringBuilder();

      for (String var9 : (var0 == null ? "" : var0).split(" ")) {
         while (var1.width(var9) > var2) {
            if (var5.length() > 0) {
               var4.add(var5.toString());
               if (var4.size() == var3) {
                  return var4;
               }

               var5 = new StringBuilder();
            }

            int var10 = 1;

            while (var10 < var9.length() && var1.width(var9.substring(0, var10 + 1)) <= var2) {
               var10++;
            }

            var4.add(var9.substring(0, var10));
            if (var4.size() == var3) {
               return var4;
            }

            var9 = var9.substring(var10);
         }

         String var11 = var5.length() == 0 ? var9 : var5 + " " + var9;
         if (var1.width(var11) > var2 && var5.length() > 0) {
            var4.add(var5.toString());
            if (var4.size() == var3) {
               return var4;
            }

            var5 = new StringBuilder(var9);
         } else {
            var5 = new StringBuilder(var11);
         }
      }

      if (var5.length() > 0) {
         var4.add(var5.toString());
      }

      return var4;
   }

   public String emptyText() {
      return "gui.section.empty";
   }

   private static List<InspectorRow> settingRows(List<SectionPanel.Entry> var0) {
      ArrayList<InspectorRow> var1 = new ArrayList<>();

      for (SectionPanel.Entry var3 : var0) {
         if (var3.row != null) {
            var1.add(var3.row);
         }
      }

      return var1;
   }

   public float footerHeight() {
      return 0.0F;
   }

   public void renderFooter(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
   }

   public boolean onFooterClicked(double var1, double var3, Enum3 var5) {
      return false;
   }

   private void renderAside(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = this.asideWidth();
      float var10 = HoverInfo.boxHeight();
      float var11;
      float var12 = (var11 = var5 + var6 - var10) - GuiStyle.PADDING.intValue() - var5;
      float var13 = var7 * this.asideAlpha();
      if (this.asideFull != null) {
         this.asideBounds = null;
         this.asideFull.render(var1, var2, var3, var4, var5, var9, var13, var8);
      } else {
         this.asideBounds = new BoundsData(var4, var5, var9, var12);
         InspectorRow.alignTracks(settingRows(this.asideEntries), var9);
         float var14 = 0.0F;

         for (SectionPanel.Entry var16 : this.asideEntries) {
            var14 += var16.height();
         }

         this.asideScrollTarget = clamp(this.asideScrollTarget, var14, var12);
         this.asideScroll = clamp(this.asideScroll, var14, var12);
         this.asideScroll = this.asideScrollbar.isDragging() ? this.asideScrollTarget : this.asideScroll + (this.asideScrollTarget - this.asideScroll) * 0.25F;
         MsdfRenderer.flushBatch();
         var1.enableScissor(var4, var5, var4 + var9, var5 + var12);
         float var21 = var5 + this.asideScroll;
         int var22 = 0;

         for (SectionPanel.Entry var18 : this.asideEntries) {
            int var19 = var22++;
            float var20 = var18.height();
            if (var18.cullable() && (!(var21 + var20 > var5) || !(var21 < var5 + var12))) {
               var18.detach();
            } else {
               this.renderEntry(var18, "aside:" + var19, var19, true, var1, var2, var3, var4, var21, var9, var13, var8);
            }

            var21 += var20;
         }

         MsdfRenderer.flushBatch();
         var1.disableScissor();
         this.asideScrollbar
            .render(var1, var4 + var9 - 2.0F, var5, var12, var14, this.asideScroll, var8.getTextTertiary().getZenithColor().method7294(var7 * 0.6F));
      }

      if (!(var10 <= 0.01F)) {
         var1.drawRect(
            var4 + 8.0F, var11 - GuiStyle.PADDING.intValue(), var9 - 16.0F, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var7 * var10 / 34.0F)
         );
         if (!HoverInfo.hasText()) {
            HoverInfo.request(this.headerTitle(), t(this.sectionDescription()));
         }

         HoverInfo.render(
            var1, var4 + 8.0F, var11 + GuiStyle.PADDING.intValue(), var9 - 16.0F, Math.max(0.0F, var10 - GuiStyle.PADDING.intValue() * 2.0F), var7
         );
      }
   }

   public float mainWidth() {
      return 236.0F;
   }

   public float asideWidth() {
      return 144.0F;
   }

   public boolean asideFirst() {
      return false;
   }

   public float asideAlpha() {
      return 1.0F;
   }

   public boolean isFullyVisible() {
      NLMenuScreen var1 = Zenith.getInstance() == null ? null : Zenith.getInstance().getNLMenuScreen();
      float var2 = var1 == null ? 1.0F : var1.getContentDip();
      return this.panelAlpha > 0.995F * var2;
   }

   public void renderMainOverlay(MenuDrawContext var1) {
   }

   public BoundsData mainViewport() {
      return this.mainBounds;
   }

   public BoundsData asideViewport() {
      return this.asideBounds;
   }

   public void resetScroll() {
      this.scroll = 0.0F;
      this.scrollTarget = 0.0F;
   }

   public void scrollToEnd() {
      this.scrollTarget = -8.5070587E37F;
      this.scroll = this.scrollTarget;
   }

   public void resetAsideScroll() {
      this.asideScroll = 0.0F;
      this.asideScrollTarget = 0.0F;
   }

   private static float clamp(float var0, float var1, float var2) {
      return var1 <= var2 ? 0.0F : MathHelper.clamp(var0, var2 - var1, 0.0F);
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
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      this.ensureBuilt();
      if (this.embedded && this.clickTabs(var1, var3)) {
         return true;
      } else {
         float var6 = this.mainScrollbar.onMouseClicked(var1, var3);
         if (this.mainScrollbar.isDragging()) {
            if (!Float.isNaN(var6)) {
               this.scrollTarget = var6;
            }

            return true;
         } else {
            var6 = this.asideScrollbar.onMouseClicked(var1, var3);
            if (this.asideScrollbar.isDragging()) {
               if (!Float.isNaN(var6)) {
                  this.asideScrollTarget = var6;
               }

               return true;
            } else if (this.footerBounds != null && this.footerBounds.contains(var1, var3) && this.onFooterClicked(var1, var3, var5)) {
               return true;
            } else {
               if (this.asideBounds == null || this.asideBounds.contains(var1, var3)) {
                  int var7 = 0;

                  for (SectionPanel.Entry var9 : new ArrayList<>(this.asideEntries)) {
                     String var10 = "aside:" + var7++;
                     if (var9.onMouseClicked(var1, var3, var5)) {
                        this.markPress(var9, var10, var1, var3);
                        return true;
                     }
                  }
               }

               if (this.mainBounds != null && this.mainBounds.contains(var1, var3)) {
                  int var12 = 0;

                  for (SectionPanel.Entry var14 : new ArrayList<>(this.mainEntries)) {
                     String var15 = "main:" + var12++;
                     if (var5 == Enum3.RIGHT && var14.bounds != null && var14.bounds.contains(var1, var3)) {
                        this.lastRightClickBounds = var14.bounds;
                     }

                     if (var14.onMouseClicked(var1, var3, var5)) {
                        this.markPress(var14, var15, var1, var3);
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      this.mainScrollbar.release();
      this.asideScrollbar.release();

      for (SectionPanel.Entry var7 : this.mainEntries) {
         var7.onMouseReleased(var1, var3, var5);
      }

      for (SectionPanel.Entry var9 : this.asideEntries) {
         var9.onMouseReleased(var1, var3, var5);
      }

      return false;
   }

   @Override
   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      float var10;
      if (!Float.isNaN(var10 = this.mainScrollbar.onMouseDragged(var3))) {
         this.scrollTarget = var10;
         return true;
      } else if (!Float.isNaN(var10 = this.asideScrollbar.onMouseDragged(var3))) {
         this.asideScrollTarget = var10;
         return true;
      } else {
         boolean var11 = false;

         for (SectionPanel.Entry var13 : this.mainEntries) {
            if (var13.onMouseDragged(var1, var3)) {
               var11 = true;
            }
         }

         for (SectionPanel.Entry var16 : this.asideEntries) {
            if (var16.onMouseDragged(var1, var3)) {
               var11 = true;
            }
         }

         return var11;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.tabsScrollable && this.tabViewport != null && this.tabViewport.contains(var1, var3)) {
         this.tabScrollTarget -= (float)(var7 + var5) * 24.0F;
         return true;
      } else if (this.asideBounds != null && this.asideBounds.contains(var1, var3)) {
         this.asideScrollTarget = this.asideScrollTarget + (float)var7 * this.scrollStep();
         return true;
      } else if (this.mainBounds != null && this.mainBounds.contains(var1, var3)) {
         this.scrollTarget = this.scrollTarget + (float)var7 * this.scrollStep();
         return true;
      } else {
         return false;
      }
   }

   public float scrollStep() {
      return 18.0F;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (SectionPanel.Entry var5 : this.mainEntries) {
         if (var5.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      for (SectionPanel.Entry var7 : this.asideEntries) {
         if (var7.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (SectionPanel.Entry var4 : this.mainEntries) {
         if (var4.charTyped(var1, var2)) {
            return true;
         }
      }

      for (SectionPanel.Entry var6 : this.asideEntries) {
         if (var6.charTyped(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public List<? extends Element> getElements() {
      return List.of();
   }

   @Override
   public void close() {
   }

   public interface AsideRenderer {
      void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8);
   }

   private static final class Entry {
      private final SectionPanel.Entry.Kind kind;
      private final String label;
      private final MenuGlyph icon;
      private final Supplier<String> value;
      private final String description;
      private final Runnable onClick;
      private final Runnable onRightClick;
      private final BooleanSupplier selected;
      private final boolean accentValue;
      private final InspectorRow row;
      private final float customHeight;
      private final SectionPanel.AsideRenderer renderer;
      private BoundsData bounds;
      private boolean lead;
      private SectionPanel.IconRenderer iconRenderer;
      private final Animation hoverAnimation = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private final Animation selectAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private String fitSource;
      private float fitRoom = Float.NaN;
      private Object fitFont;
      private float fitSize;
      private String fitShown;

      private Entry(
         SectionPanel.Entry.Kind var1,
         String var2,
         MenuGlyph var3,
         Supplier<String> var4,
         String var5,
         Runnable var6,
         Runnable var7,
         BooleanSupplier var8,
         boolean var9,
         InspectorRow var10,
         float var11,
         SectionPanel.AsideRenderer var12
      ) {
         this.kind = var1;
         this.label = var2;
         this.icon = var3;
         this.value = var4;
         this.description = var5;
         this.onClick = var6;
         this.onRightClick = var7;
         this.selected = var8;
         this.accentValue = var9;
         this.row = var10;
         this.customHeight = var11;
         this.renderer = var12;
      }

      private static SectionPanel.Entry caption(String var0, boolean var1) {
         SectionPanel.Entry var2 = new SectionPanel.Entry(SectionPanel.Entry.Kind.CAPTION, var0, null, null, null, null, null, null, false, null, 0.0F, null);
         var2.lead = var1;
         return var2;
      }

      private static SectionPanel.Entry setting(InspectorRow var0) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.SETTING, null, null, null, null, null, null, null, false, var0, 0.0F, null);
      }

      private static SectionPanel.Entry info(String var0, Supplier<String> var1, String var2) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.ROW, var0, null, var1, var2, null, null, null, false, null, 0.0F, null);
      }

      private static SectionPanel.Entry action(String var0, MenuGlyph var1, Supplier<String> var2, Runnable var3, String var4) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.ROW, var0, var1, var2, var4, var3, null, null, true, null, 0.0F, null);
      }

      private static SectionPanel.Entry row(
         String var0, MenuGlyph var1, Supplier<String> var2, String var3, Runnable var4, Runnable var5, BooleanSupplier var6, boolean var7
      ) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.ROW, var0, var1, var2, var3, var4, var5, var6, var7, null, 0.0F, null);
      }

      private static SectionPanel.Entry custom(float var0, SectionPanel.AsideRenderer var1) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.CUSTOM, null, null, null, null, null, null, null, false, null, var0, var1);
      }

      private static SectionPanel.Entry custom(float var0, SectionPanel.AsideRenderer var1, Runnable var2, BooleanSupplier var3, Runnable var4) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.CUSTOM, null, null, null, null, var2, var4, var3, false, null, var0, var1);
      }

      private float height() {
         return switch (this.kind) {
            case CAPTION -> this.lead ? 10.0F : 16.0F;
            case SETTING -> this.row.height();
            default -> 16.0F;
            case CUSTOM -> this.customHeight;
         };
      }

      private boolean cullable() {
         return this.kind != SectionPanel.Entry.Kind.SETTING;
      }

      private void detach() {
         this.bounds = null;
      }

      private void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
         switch (this.kind) {
            case CAPTION:
               Font var9 = Fonts.NEW_MEDIUM.getFont(4.0F);
               var1.drawText(
                  var9,
                  HoverInfo.spaced(SectionPanel.t(this.label).toUpperCase(Locale.ROOT)),
                  var4 + 8.0F,
                  var5 + (16.0F - var9.height()) / 2.0F + 2.0F - (this.lead ? 6.0F : 0.0F),
                  var8.getTextTertiary().getZenithColor().method7294(var7)
               );
               break;
            case SETTING:
               this.row.render(var1, var2, var3, var4, var5, var6, var7);
               break;
            case ROW:
            default:
               this.renderTextRow(var1, var2, var3, var4, var5, var6, var7, var8);
               break;
            case CUSTOM:
               if (this.onClick != null || this.onRightClick != null) {
                  this.bounds = new BoundsData(var4, var5, var6, this.customHeight);
               }

               this.renderer.render(var1, var2, var3, var4, var5, var6, var7, var8);
         }
      }

      private void renderTextRow(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
         this.bounds = new BoundsData(var4, var5, var6, 16.0F);
         boolean var9 = this.bounds.contains(var2, var3);
         boolean var10 = this.onClick != null || this.onRightClick != null;
         boolean var11 = this.selected != null && this.selected.getAsBoolean();
         float var12 = this.hoverAnimation.method2808(var9 && var10 ? 1.0F : 0.0F);
         float var13 = this.selectAnimation.method2808(var11 ? 1.0F : 0.0F);
         float var14 = Math.max(var13, var12 * 0.45F);
         if (var14 > 0.002F) {
            var1.drawRect(var4, var5, var6, 16.0F, var8.getDisableActiveBg().getZenithColor().method7294(var7 * var14));
         }

         if (var13 > 0.002F) {
            float var15 = 12.0F * var13;
            var1.drawRect(var4, var5 + (16.0F - var15) / 2.0F, 2.0F, var15, var8.getPrimaryColor().getZenithColor().method7294(var7));
         }

         if (var9 && this.description != null && !this.description.isBlank()) {
            HoverInfo.request(SectionPanel.t(this.label), SectionPanel.t(this.description));
         }

         Font var24 = Fonts.NEW_MEDIUM.getFont(5.2F);
         ZenithColor var16 = var8.getTextSecondary()
            .getZenithColor()
            .method7295(var8.getTextEnable().getZenithColor(), Math.max(var13, var12 * 0.6F))
            .method7294(var7);
         float var17 = var4 + 8.0F;
         boolean var18 = Zenith.getInstance().getNLMenuScreen().isRenderIcon();
         if (var18 && this.iconRenderer != null && this.iconRenderer.draw(var1, var17, var5 + 8.0F, 14.0F, var7)) {
            var17 += 14.0F;
         } else if (this.icon != null && var18) {
            this.icon.draw(var1, var17, var5 + 5.0F, var16);
            var17 += 9.0F;
         }

         String var19 = SectionPanel.t(this.label);
         var1.drawText(var24, var19, var17, var5 + (16.0F - var24.height()) / 2.0F, var16);
         String var20 = this.value == null ? null : this.value.get();
         if (var20 != null && !var20.isEmpty()) {
            Font var21 = Fonts.NEW_MEDIUM.getFont(5.0F);
            float var22 = var4 + var6 - 8.0F - (var17 + var24.width(var19)) - 6.0F;
            String var23;
            if (var22 == this.fitRoom && var21.getFont() == this.fitFont && var21.getSize() == this.fitSize && var20.equals(this.fitSource)) {
               var23 = this.fitShown;
            } else {
               var23 = fit(var21, var20, var22);
               this.fitSource = var20;
               this.fitRoom = var22;
               this.fitFont = var21.getFont();
               this.fitSize = var21.getSize();
               this.fitShown = var23;
            }

            if (!var23.isEmpty()) {
               var1.drawText(
                  var21,
                  var23,
                  var4 + var6 - 8.0F - var21.width(var23),
                  var5 + (16.0F - var21.height()) / 2.0F,
                  (this.accentValue ? var8.getPrimaryColor() : var8.getTextEnable()).getZenithColor().method7294(var7)
               );
               if (var9 && !var23.equals(var20)) {
                  HoverInfo.request(var19, var20);
               }
            }
         }
      }

      private static String fit(Font var0, String var1, float var2) {
         if (var2 <= 0.0F) {
            return "";
         } else if (var0.width(var1) <= var2) {
            return var1;
         } else {
            String var3 = "…";
            float var4 = var0.width(var3);
            if (var4 > var2) {
               return "";
            } else {
               int var5 = var1.length();

               while (var5 > 0 && var0.width(var1.substring(0, var5)) + var4 > var2) {
                  var5--;
               }

               return var5 == 0 ? "" : var1.substring(0, var5) + var3;
            }
         }
      }

      private boolean onMouseClicked(double var1, double var3, Enum3 var5) {
         if (this.kind == SectionPanel.Entry.Kind.SETTING) {
            return this.row.onMouseClicked(var1, var3, var5);
         } else if ((this.kind == SectionPanel.Entry.Kind.ROW || this.kind == SectionPanel.Entry.Kind.CUSTOM)
            && this.bounds != null
            && this.bounds.contains(var1, var3)) {
            if (var5 == Enum3.RIGHT && this.onRightClick != null) {
               this.onRightClick.run();
               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            } else if (this.onClick != null) {
               this.onClick.run();
               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      private void onMouseReleased(double var1, double var3, Enum3 var5) {
         if (this.kind == SectionPanel.Entry.Kind.SETTING) {
            this.row.onMouseReleased(var1, var3, var5);
         }
      }

      private boolean onMouseDragged(double var1, double var3) {
         return this.kind == SectionPanel.Entry.Kind.SETTING && this.row.onMouseDragged(var1, var3);
      }

      private boolean keyPressed(int var1, int var2, int var3) {
         return this.kind == SectionPanel.Entry.Kind.SETTING && this.row.keyPressed(var1, var2, var3);
      }

      private boolean charTyped(char var1, int var2) {
         return this.kind == SectionPanel.Entry.Kind.SETTING && this.row.charTyped(var1, var2);
      }

      private static enum Kind {
         CAPTION,
         SETTING,
         ROW,
         CUSTOM;
      }
   }

   @FunctionalInterface
   public interface IconRenderer {
      boolean draw(MenuDrawContext var1, float var2, float var3, float var4, float var5);
   }

   public final class RowBuilder {
      private SectionPanel.IconRenderer iconRenderer;
      private final String label;
      private final List<SectionPanel.Entry> target;
      private MenuGlyph icon;
      private Supplier<String> value;
      private String description;
      private Runnable onClick;
      private Runnable onRightClick;
      private BooleanSupplier selected;
      private boolean accentValue;

      private RowBuilder(String var2, List<SectionPanel.Entry> var3) {
         this.label = var2;
         this.target = var3;
      }

      public SectionPanel.RowBuilder icon(MenuGlyph var1) {
         this.icon = var1;
         return this;
      }

      public SectionPanel.RowBuilder iconRenderer(SectionPanel.IconRenderer var1) {
         this.iconRenderer = var1;
         return this;
      }

      public SectionPanel.RowBuilder value(Supplier<String> var1) {
         this.value = var1;
         return this;
      }

      public SectionPanel.RowBuilder description(String var1) {
         this.description = var1;
         return this;
      }

      public SectionPanel.RowBuilder onClick(Runnable var1) {
         this.onClick = var1;
         return this;
      }

      public SectionPanel.RowBuilder onRightClick(Runnable var1) {
         this.onRightClick = var1;
         return this;
      }

      public SectionPanel.RowBuilder selected(BooleanSupplier var1) {
         this.selected = var1;
         return this;
      }

      public SectionPanel.RowBuilder accent() {
         this.accentValue = true;
         return this;
      }

      public void add() {
         SectionPanel.Entry var1 = SectionPanel.Entry.row(
            this.label, this.icon, this.value, this.description, this.onClick, this.onRightClick, this.selected, this.accentValue
         );
         var1.iconRenderer = this.iconRenderer;
         this.target.add(var1);
      }

      public void addAside() {
         SectionPanel.Entry var1 = SectionPanel.Entry.row(
            this.label, this.icon, this.value, this.description, this.onClick, this.onRightClick, this.selected, this.accentValue
         );
         var1.iconRenderer = this.iconRenderer;
         SectionPanel.this.asideEntries.add(var1);
      }
   }
}

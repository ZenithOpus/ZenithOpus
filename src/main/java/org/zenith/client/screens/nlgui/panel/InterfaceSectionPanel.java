package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.StyleManager;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ClientSounds;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class InterfaceSectionPanel extends SectionPanel {
   private static final int TAB_THEME = 0;
   private static final int TAB_HUD = 1;
   private static final int TAB_MENU = 2;
   private static final int CARD_COLUMNS = 2;
   private static final float CARD_GAP = 5.0F;
   private static final float CARD_PREVIEW = 28.0F;
   private static final float CARD_LABEL = 13.0F;
   private static final float CARD_HEIGHT = 41.0F;
   private static final float HUD_ROW_HEIGHT = 16.0F;
   private static final float HUD_HEADER_HEIGHT = 28.0F;
   private static final float SWITCH_WIDTH = 9.0F;
   private static final float SWITCH_HEIGHT = 5.0F;
   private static final float SWITCH_KNOB = 3.0F;
   private static final float EMPTY_HEIGHT = 22.0F;
   private static final String GROUP_COMBAT = "gui.interface.hud.group.combat";
   private static final String GROUP_INFO = "gui.interface.hud.group.info";
   private static final String GROUP_CONTROL = "gui.interface.hud.group.control";
   private static final String GROUP_CLIENT = "gui.interface.hud.group.client";
   private static final String GROUP_OTHER = "gui.interface.hud.group.other";
   private static final String[] GROUP_ORDER = new String[]{
      "gui.interface.hud.group.combat",
      "gui.interface.hud.group.info",
      "gui.interface.hud.group.control",
      "gui.interface.hud.group.client",
      "gui.interface.hud.group.other"
   };
   private static final Map<String, String> GROUPS = new HashMap<>();
   private static final Map<String, MenuGlyph> GLYPHS = new HashMap<>();
   private static final String[] HINTS = new String[]{"gui.interface.hud.hint.pick", "gui.interface.hud.hint.toggle", "gui.interface.hud.hint.drag"};
   private final Map<ZenithStyle, BoundsData> cardBounds = new LinkedHashMap<>();
   private final Map<ZenithStyle, Animation> cardHover = new HashMap<>();
   private final Map<Interface, BoundsData> switchBounds = new LinkedHashMap<>();
   private final Map<Interface, Animation> switchKnob = new HashMap<>();
   private final Map<Interface, Animation> rowHover = new HashMap<>();
   private final Map<Interface, Animation> rowSelect = new HashMap<>();
   private final Animation countBar = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private Interface selectedElement;
   private String lastSignature = "";
   private ZenithStyle lastStyle;
   private final BindSetting menuBind = new InterfaceSectionPanel.MenuBindSetting();

   @Override
   public String headerTitle() {
      return "Interface";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.INTERFACE;
   }

   @Override
   public String sectionDescription() {
      return "gui.interface.desc";
   }

   @Override
   public String emptyText() {
      return "gui.section.empty";
   }

   public boolean isHudTab() {
      return this.getActiveTab() == 1;
   }

   public void openHudElementSettings(Interface var1) {
      this.selectedElement = var1;
      this.selectTab(1);
      this.invalidate();
   }

   @Override
   public void tick() {
      super.tick();
      String var1 = this.getActiveTab() + "|" + this.searchQuery();
      ZenithStyle var2 = styles().getCurrentStyle();
      if (!var1.equals(this.lastSignature) || var2 != this.lastStyle) {
         this.lastSignature = var1;
         this.lastStyle = var2;
         this.invalidate();
      }
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.cardBounds.clear();
      this.switchBounds.clear();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      BoundsData var6 = this.mainViewport();
      if (var5 == Enum3.LEFT && var6 != null && var6.contains(var1, var3)) {
         for (Map.Entry var8 : this.switchBounds.entrySet()) {
            if (((BoundsData)var8.getValue()).contains(var1, var3)) {
               this.toggleElement((Interface)var8.getKey());
               return true;
            }
         }

         for (Map.Entry var10 : this.cardBounds.entrySet()) {
            if (((BoundsData)var10.getValue()).contains(var1, var3)) {
               styles().setCurrentStyle((ZenithStyle)var10.getKey());
               Zenith.getInstance().getClientSounds().method1570(false);
               this.invalidate();
               return true;
            }
         }
      }

      return super.onMouseClicked(var1, var3, var5);
   }

   @Override
   public void build() {
      this.tabs(new String[]{"gui.interface.tab.theme", "gui.interface.tab.hud", "gui.interface.tab.menu"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.COLOR, MenuGlyph.INTERFACE, MenuGlyph.WINDOW});
      switch (this.getActiveTab()) {
         case 1:
            this.buildHud();
            break;
         case 2:
            this.buildMenu();
            break;
         default:
            this.buildTheme();
      }
   }

   private void buildMenu() {
      this.caption("gui.interface.caption.menuControl");
      this.setting(this.menuBind);

      for (Setting var2 : MenuModule.INSTANCE.method5349()) {
         this.setting(var2);
      }

      this.asideCaption("gui.interface.caption.menuAnimation");

      for (Setting var4 : MenuModule.INSTANCE.method5350()) {
         this.asideSetting(var4);
      }
   }

   private static StyleManager styles() {
      return Zenith.getInstance().getStyleManager();
   }

   private void buildTheme() {
      String var1 = this.searchQuery();
      StyleManager var2 = styles();
      ArrayList var3 = new ArrayList();

      for (ZenithStyle var5 : var2.getStyles()) {
         if (matches(styleName(var5), var1)) {
            var3.add(var5);
         }
      }

      this.caption("gui.interface.caption.styles");
      if (var3.isEmpty()) {
         this.custom(22.0F, this::renderNoMatch);
      } else {
         for (byte var6 = 0; var6 < var3.size(); var6 += 2) {
            byte var9 = var6;
            this.custom(
               46.0F,
               (var3x, var4, var5x, var6x, var7, var8x, var9x, var10x) -> this.renderCardRow(var3x, var4, var5x, var6x, var7, var8x, var9x, var10x, var3, var9)
            );
         }
      }

      this.gap(GuiStyle.PADDING.intValue() * 2.0F);
      this.caption("gui.interface.caption.look");

      for (Setting var10 : InterfaceModule.INSTANCE.method6771()) {
         this.setting(var10);
      }

      ZenithStyle var8 = var2.getCurrentStyle();
      if (var8 != null) {
         this.asideCaption("gui.interface.caption.colors");
         this.asideSetting(var8.getPrimaryColor());
         this.asideSetting(var8.getSecondaryPrimaryColor());
         this.asideSetting(var8.getTextEnable());
         this.asideSetting(var8.getTextSecondary());
         this.asideSetting(var8.getTextTertiary());
         this.asideSetting(var8.getLeftBackground());
         this.asideSetting(var8.getRightBackground());
         this.asideSetting(var8.getHudBackground());
         this.asideSetting(var8.getFriendColor());
      }
   }

   private static String styleName(ZenithStyle var0) {
      return var0.getName() != null && !var0.getName().isBlank() ? var0.getName() : "Style";
   }

   private void renderCardRow(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, List<ZenithStyle> var9, int var10
   ) {
      float var11 = var6 - 16.0F;
      float var12 = (var11 - 5.0F) / 2.0F;

      for (int var13 = 0; var13 < 2; var13++) {
         int var14 = var10 + var13;
         if (var14 >= var9.size()) {
            return;
         }

         this.renderCard(var1, var2, var3, (ZenithStyle)var9.get(var14), var4 + 8.0F + var13 * (var12 + 5.0F), var5, var12, var7, var8);
      }
   }

   private void renderCard(MenuDrawContext var1, int var2, int var3, ZenithStyle var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      boolean var10 = var4 == styles().getCurrentStyle();
      BoundsData var11 = new BoundsData(var5, var6, var7, 41.0F);
      this.cardBounds.put(var4, var11);
      boolean var12 = var11.contains(var2, var3);
      float var13 = this.cardHover.computeIfAbsent(var4, var0 -> new Animation(140L, 0.0F, Easing.INSTANCE12)).method2808(var12 ? 1.0F : 0.0F);
      if (var12) {
         HoverInfo.request(styleName(var4), t("gui.interface.style.desc"));
      }

      ProxyDotRadiusData var14 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F);
      var1.drawRoundedRect(
         var5,
         var6,
         var7,
         41.0F,
         var14,
         var9.getFieldSurfaceBackground().getZenithColor().method7295(var9.getDisableActiveBg().getZenithColor(), 0.35F + var13 * 0.5F).method7294(var8)
      );
      float var15 = var5 + 3.0F;
      float var16 = var6 + 3.0F;
      float var17 = var7 - 6.0F;
      float var18 = 22.0F;
      ProxyDotRadiusData var19 = ProxyDotRadiusData.method8069(3.0F);
      float var20 = var17 * 0.34F;
      var1.drawRoundedRect(var15, var16, var17, var18, var19, var4.getRightBackground().getZenithColor().method7293(255).method7294(var8));
      var1.drawRoundedRect(
         var15, var16, var20, var18, ProxyDotRadiusData.method8078(3.0F, 3.0F), var4.getLeftBackground().getZenithColor().method7293(255).method7294(var8)
      );
      float var21 = var20 - 5.0F;
      var1.drawRoundedRect(
         var15 + 2.5F, var16 + 3.5F, var21, 1.8F, ProxyDotRadiusData.method8069(0.9F), var4.getPrimaryColor().getZenithColor().method7294(var8)
      );
      var1.drawRoundedRect(
         var15 + 2.5F,
         var16 + 8.0F,
         var21 * 0.7F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         var4.getTextTertiary().getZenithColor().method7293(255).method7294(var8 * 0.7F)
      );
      var1.drawRoundedRect(
         var15 + 2.5F,
         var16 + 12.0F,
         var21 * 0.85F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         var4.getTextTertiary().getZenithColor().method7293(255).method7294(var8 * 0.5F)
      );
      float var22 = var15 + var20 + 3.0F;
      float var23 = var17 - var20 - 6.0F;
      var1.drawRoundedRect(
         var22,
         var16 + 3.5F,
         var23 * 0.8F,
         1.8F,
         ProxyDotRadiusData.method8069(0.9F),
         var4.getTextEnable().getZenithColor().method7293(255).method7294(var8 * 0.8F)
      );
      var1.drawRoundedRect(
         var22,
         var16 + 8.0F,
         var23 * 0.55F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         var4.getTextSecondary().getZenithColor().method7293(255).method7294(var8 * 0.55F)
      );
      var1.drawRoundedRect(
         var22,
         var16 + 12.0F,
         var23 * 0.68F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         var4.getTextTertiary().getZenithColor().method7293(255).method7294(var8 * 0.5F)
      );
      var1.drawRoundedRect(
         var22 + var23 - 5.0F,
         var16 + var18 - 5.5F,
         5.0F,
         2.4F,
         ProxyDotRadiusData.method8069(1.2F),
         var4.getSecondaryPrimaryColor().getZenithColor().method7294(var8)
      );
      var1.drawRoundedBorder(var15, var16, var17, var18, 0.3F, var19, var4.getFieldBorder().getZenithColor().method7294(var8 * 2.0F));
      Font var24 = Fonts.NEW_MEDIUM.getFont(4.8F);
      float var25 = var6 + 28.0F + (13.0F - var24.height()) / 2.0F - 1.0F;
      float var26 = var7 - 8.0F - (var10 ? 9.0F : 0.0F);
      String var27 = fit(var24, styleName(var4), var26);
      var1.drawText(
         var24,
         var27,
         var5 + 4.0F,
         var25,
         (var10 ? var9.getTextEnable() : var9.getTextSecondary()).getZenithColor().method7294(var8 * (var10 ? 1.0F : 0.75F + var13 * 0.25F))
      );
      if (var10) {
         MenuGlyph.CHECK.draw(var1, var5 + var7 - 4.0F - 6.0F, var6 + 28.0F + 3.5F - 1.0F, var9.getPrimaryColor().getZenithColor().method7294(var8));
      }

      ZenithColor var28 = var10
         ? var9.getPrimaryColor().getZenithColor().method7292(0.5F)
         : var9.getFieldBorder().getZenithColor().method7294(2.0F + var13 * 1.5F);
      var1.drawRoundedBorder(var5, var6, var7, 41.0F, 0.2F, var14, var28.method7294(var8));
   }

   @Override
   public float mainHeaderHeight() {
      return this.getActiveTab() == 1 ? 28.0F : 0.0F;
   }

   @Override
   public void renderMainHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      List<Interface> var9 = InterfaceModule.INSTANCE.method6794();
      int var10 = var9.size();
      int var11 = 0;

      for (Object var13_o : (Iterable<?>)(Object)(var9)) {
         Interface var13 = (Interface)var13_o;
         if (var13.isEnabled()) {
            var11++;
         }
      }

      Font var20 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var21 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String var14 = t("gui.interface.hud.shown");
      String var15 = var11 + " / " + var10;
      float var16 = var5 + 3.0F;
      var1.drawText(var20, var14, var4 + 8.0F, var16, var8.getTextSecondary().getZenithColor().method7294(var7));
      var1.drawText(var21, var15, var4 + var6 - 8.0F - var21.width(var15), var16, var8.getPrimaryColor().getZenithColor().method7294(var7));
      float var17 = var6 - 16.0F;
      float var18 = var16 + var20.height() + 5.0F;
      var1.drawRoundedRect(var4 + 8.0F, var18, var17, 2.0F, ProxyDotRadiusData.method8069(1.0F), var8.getDisableActiveBg().getZenithColor().method7294(var7));
      float var19 = this.countBar.method2808(var10 == 0 ? 0.0F : (float)var11 / var10);
      if (var19 > 0.001F) {
         var1.drawRoundedRect(
            var4 + 8.0F,
            var18,
            Math.max(2.0F, var17 * var19),
            2.0F,
            ProxyDotRadiusData.method8069(1.0F),
            var8.getPrimaryColor().getZenithColor().method7294(var7)
         );
      }
   }

   private void buildHud() {
      List<Interface> var1 = InterfaceModule.INSTANCE.method6794();
      if (this.selectedElement != null && !var1.contains(this.selectedElement)) {
         this.selectedElement = null;
      }

      String var2 = this.searchQuery();
      LinkedHashMap var3 = new LinkedHashMap();

      for (String var7 : GROUP_ORDER) {
         var3.put(var7, new ArrayList());
      }

      for (Object var14_o : (Iterable<?>)(Object)(var1)) {
         Interface var14 = (Interface)var14_o;
         if (matches(elementName(var14), var2) || matches(var14.getName(), var2)) {
            ((List)var3.get(GROUPS.getOrDefault(var14.getName(), "gui.interface.hud.group.other"))).add(var14);
         }
      }

      boolean var13 = false;

      for (String var8 : GROUP_ORDER) {
         List<Interface> var9 = (List)var3.get(var8);
         if (!var9.isEmpty()) {
            var13 = true;
            this.caption(var8);

            for (Object var11_o : (Iterable<?>)(Object)(var9)) {
               Interface var11 = (Interface)var11_o;
               this.custom(
                  16.0F,
                  (var2x, var3x, var4, var5, var6, var7x, var8x, var9x) -> this.renderHudRow(var2x, var3x, var4, var5, var6, var7x, var8x, var9x, var11),
                  () -> this.toggleElement(var11),
                  () -> var11 == this.selectedElement,
                  () -> {
                     this.selectedElement = var11;
                     this.resetAsideScroll();
                     this.invalidate();
                  }
               );
            }
         }
      }

      if (!var13) {
         this.custom(22.0F, this::renderNoMatch);
      }

      this.buildHudAside();
   }

   private void toggleElement(Interface var1) {
      var1.toggle();
      ClientSounds var2 = Zenith.getInstance().getClientSounds();
      var2.method1561(var1.isEnabled() ? var2.soundEvent5 : var2.soundEvent6);
   }

   private void renderHudRow(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, Interface var9) {
      BoundsData var10 = new BoundsData(var4, var5, var6, 16.0F);
      boolean var11 = var10.contains(var2, var3);
      float var12 = this.rowHover.computeIfAbsent(var9, var0 -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(var11 ? 1.0F : 0.0F);
      float var13 = this.rowSelect
         .computeIfAbsent(var9, var0 -> new Animation(140L, 0.0F, Easing.INSTANCE12))
         .method2808(var9 == this.selectedElement ? 1.0F : 0.0F);
      float var14 = Math.max(var13, var12 * 0.45F);
      if (var14 > 0.002F) {
         var1.drawRect(var4, var5, var6, 16.0F, var8.getDisableActiveBg().getZenithColor().method7294(var7 * var14));
      }

      if (var13 > 0.002F) {
         float var15 = 11.0F * var13;
         var1.drawRect(var4, var5 + (16.0F - var15) / 2.0F, 2.0F, var15, var8.getPrimaryColor().getZenithColor().method7294(var7));
      }

      boolean var34 = var9.isEnabled();
      Font var16 = Fonts.NEW_MEDIUM.getFont(5.01F);
      String var17 = t(var34 ? "gui.value.on" : "gui.value.off");
      float var18 = var16.width(var17);
      float var19 = Math.max(var16.width(t("gui.value.on")), var16.width(t("gui.value.off")));
      float var20 = var4 + var6 - 8.0F;
      float var21 = var20 - var19 - GuiStyle.PADDING.intValue() - 9.0F;
      BoundsData var22 = new BoundsData(var21, var5, var20 - var21, 16.0F);
      this.switchBounds.put(var9, var22);
      boolean var23 = var22.contains(var2, var3);
      String var24 = elementName(var9);
      if (var11) {
         HoverInfo.request(var24, t(var23 ? "gui.interface.hud.show.desc" : "gui.interface.hud.row.desc"));
      }

      ZenithColor var25 = var8.getTextSecondary()
         .getZenithColor()
         .method7295(var8.getTextEnable().getZenithColor(), Math.max(var13, var12 * 0.6F))
         .method7294(var7);
      float var26 = var4 + 8.0F;
      if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
         MenuGlyph var27 = GLYPHS.getOrDefault(var9.getName(), MenuGlyph.INTERFACE);
         var27.draw(var1, var26, var5 + 5.0F, var25);
         var26 += 9.0F;
      }

      Font var35 = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font var28 = Fonts.NEW_REGULAR.getFont(4.4F);
      String var29 = elementTag(var9);
      float var30 = var21 - GuiStyle.PADDING.intValue() - var26;
      float var31 = var29.isEmpty() ? 0.0F : var28.width(var29) + GuiStyle.PADDING.intValue();
      String var32 = fit(var35, var24, var30 - var31);
      var1.drawText(var35, var32, var26, var5 + (16.0F - var35.height()) / 2.0F, var25);
      if (!var29.isEmpty() && var35.width(var32) + var31 <= var30) {
         var1.drawText(
            var28,
            var29,
            var26 + var35.width(var32) + GuiStyle.PADDING.intValue(),
            var5 + (16.0F - var28.height()) / 2.0F,
            var8.getTextTertiary().getZenithColor().method7294(var7 * 0.85F)
         );
      }

      var1.drawText(
         var16,
         var17,
         var20 - var19,
         var5 + (16.0F - var16.height()) / 2.0F,
         (var34 ? var8.getTextEnable() : var8.getTextTertiary()).getZenithColor().method7294(var7)
      );
      float var33 = this.switchKnob.computeIfAbsent(var9, var0 -> new Animation(140L, 0.0F, Easing.INSTANCE10)).method2808(var34 ? 1.0F : 0.0F);
      this.drawSwitch(var1, var21, var5 + 5.5F, var33, var23, var7, var8);
   }

   private void drawSwitch(MenuDrawContext var1, float var2, float var3, float var4, boolean var5, float var6, ZenithStyle var7) {
      var1.drawRect(var2, var3, 9.0F, 5.0F, var7.getFieldSurfaceBackground().getZenithColor().method7294(var6 * 3.0F));
      var1.drawRoundedBorder(
         var2, var3, 9.0F, 5.0F, 0.1F, ProxyDotRadiusData.INSTANCE, var7.getFieldBorder().getZenithColor().method7294(var6 * (var5 ? 6.0F : 4.0F))
      );
      float var8 = var2 + 1.0F + 4.0F * var4;
      var1.drawRect(
         var8, var3 + 1.0F, 3.0F, 3.0F, var7.getTextTertiary().getZenithColor().method7295(var7.getPrimaryColor().getZenithColor(), var4).method7294(var6)
      );
   }

   private void buildHudAside() {
      Interface var1 = this.selectedElement;
      if (var1 == null) {
         this.asideCaption("gui.interface.caption.actions");

         for (Setting var8 : InterfaceModule.INSTANCE.method6773()) {
            this.asideSetting(var8);
         }

         this.asideCaption("gui.interface.caption.panels");
         ZenithStyle var6 = styles().getCurrentStyle();
         if (var6 != null) {
            this.asideSetting(var6.getHudBackground());
         }

         for (Setting var14 : InterfaceModule.INSTANCE.method6774()) {
            this.asideSetting(var14);
         }

         this.asideCaption("gui.interface.caption.layout");

         for (Setting var15 : InterfaceModule.INSTANCE.method6775()) {
            this.asideSetting(var15);
         }

         this.asideCaption("gui.interface.caption.behaviour");

         for (Setting var16 : InterfaceModule.INSTANCE.method6776()) {
            this.asideSetting(var16);
         }

         this.asideCaption("gui.interface.caption.game");

         for (Setting var17 : InterfaceModule.INSTANCE.method6772()) {
            this.asideSetting(var17);
         }

         this.asideCaption("gui.interface.caption.element");
         this.asideCustom(this.hintsHeight(), this::renderHints);
      } else {
         List<Setting> var2 = var1.method2058();
         if (!var2.isEmpty()) {
            this.asideCaption(elementName(var1));

            for (Object var4_o : (Iterable<?>)(Object)(var2)) {
               Setting var4 = (Setting)var4_o;
               this.asideSetting(var4);
            }

            this.asideCaption(t("gui.interface.caption.display"));
         } else {
            this.asideCaption(elementName(var1));
         }

         for (Setting var13 : var1.method2057()) {
            this.asideSetting(var13);
         }
      }
   }

   private static String elementTag(Interface var0) {
      ArrayList var1 = new ArrayList(2);
      if (var0.method2060()) {
         var1.add(t("gui.interface.hud.tag.conditional"));
      }

      if (var0.method2059()) {
         var1.add(t("gui.interface.hud.tag.locked"));
      }

      if (Math.abs(var0.getScale().getValue3() - 100.0F) >= 1.0F) {
         var1.add(Math.round(var0.getScale().getValue3()) + "%");
      }

      return String.join(" · ", var1);
   }

   private static Font hintFont() {
      return Fonts.NEW_REGULAR.getFont(4.4F);
   }

   private float hintsHeight() {
      Font var1 = hintFont();
      float var2 = this.asideWidth() - 16.0F - 7.0F;
      float var3 = 6.0F;

      for (String var7 : HINTS) {
         var3 += wrapText(t(var7), var1, var2, 3).size() * (var1.height() + 2.0F) + 5.0F;
      }

      return var3;
   }

   private void renderHints(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = hintFont();
      float var10 = var6 - 16.0F - 7.0F;
      float var11 = var5 + 6.0F;

      for (String var15 : HINTS) {
         var1.drawRoundedRect(
            var4 + 8.0F,
            var11 + var9.height() / 2.0F - 1.0F,
            2.0F,
            2.0F,
            ProxyDotRadiusData.method8069(1.0F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * 0.8F)
         );

         for (String var17 : wrapText(t(var15), var9, var10, 3)) {
            var1.drawText(var9, var17, var4 + 8.0F + 7.0F, var11, var8.getTextTertiary().getZenithColor().method7294(var7));
            var11 += var9.height() + 2.0F;
         }

         var11 += 5.0F;
      }
   }

   private void renderNoMatch(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = Fonts.NEW_REGULAR.getFont(4.8F);
      String var10 = fit(var9, t("gui.interface.search.empty"), var6 - 16.0F);
      var1.drawText(
         var9, var10, var4 + (var6 - var9.width(var10)) / 2.0F, var5 + (22.0F - var9.height()) / 2.0F, var8.getTextTertiary().getZenithColor().method7294(var7)
      );
   }

   private static String elementName(Interface var0) {
      String var1 = "gui.interface.hud.name." + var0.getName();
      String var2 = t(var1);
      return var2.equals(var1) ? var0.getName() : var2;
   }

   private static boolean matches(String var0, String var1) {
      return var1.isEmpty() || var0.toLowerCase(Locale.ROOT).contains(var1);
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

   static {
      GROUPS.put("TargetHud", "gui.interface.hud.group.combat");
      GROUPS.put("ArmorHud", "gui.interface.hud.group.combat");
      GROUPS.put("Cooldowns", "gui.interface.hud.group.combat");
      GROUPS.put("Potions", "gui.interface.hud.group.combat");
      GROUPS.put("TargetPotions", "gui.interface.hud.group.combat");
      GROUPS.put("Staffs", "gui.interface.hud.group.combat");
      GROUPS.put("Information", "gui.interface.hud.group.info");
      GROUPS.put("Coordinates", "gui.interface.hud.group.info");
      GROUPS.put("TimerBar", "gui.interface.hud.group.info");
      GROUPS.put("ScoreBoard", "gui.interface.hud.group.info");
      GROUPS.put("AnimatedTab", "gui.interface.hud.group.info");
      GROUPS.put("Events", "gui.interface.hud.group.info");
      GROUPS.put("Inventory", "gui.interface.hud.group.info");
      GROUPS.put("Party", "gui.interface.hud.group.info");
      GROUPS.put("Keybinds", "gui.interface.hud.group.control");
      GROUPS.put("ItemBinds", "gui.interface.hud.group.control");
      GROUPS.put("HootBar", "gui.interface.hud.group.control");
      GROUPS.put("Logo", "gui.interface.hud.group.client");
      GROUPS.put("Watermark", "gui.interface.hud.group.client");
      GROUPS.put("Notifications", "gui.interface.hud.group.client");
      GROUPS.put("MusicInfo", "gui.interface.hud.group.client");
      GLYPHS.put("TargetHud", MenuGlyph.TARGET);
      GLYPHS.put("ArmorHud", MenuGlyph.CHESTPLATE);
      GLYPHS.put("Cooldowns", MenuGlyph.TIMER);
      GLYPHS.put("Potions", MenuGlyph.POTION);
      GLYPHS.put("TargetPotions", MenuGlyph.POTION);
      GLYPHS.put("Staffs", MenuGlyph.SHIELD);
      GLYPHS.put("Information", MenuGlyph.CLIENT);
      GLYPHS.put("Coordinates", MenuGlyph.GPS);
      GLYPHS.put("TimerBar", MenuGlyph.TIMER);
      GLYPHS.put("ScoreBoard", MenuGlyph.CONFIGS);
      GLYPHS.put("AnimatedTab", MenuGlyph.FRIENDS);
      GLYPHS.put("Events", MenuGlyph.WORLD);
      GLYPHS.put("Inventory", MenuGlyph.STORAGE);
      GLYPHS.put("Party", MenuGlyph.FRIENDS);
      GLYPHS.put("Keybinds", MenuGlyph.KEY);
      GLYPHS.put("ItemBinds", MenuGlyph.ITEMS);
      GLYPHS.put("HootBar", MenuGlyph.SLOT);
      GLYPHS.put("Logo", MenuGlyph.CLIENT);
      GLYPHS.put("Watermark", MenuGlyph.PIN);
      GLYPHS.put("Notifications", MenuGlyph.CHAT);
      GLYPHS.put("MusicInfo", MenuGlyph.SOUND);
   }

   private static final class MenuBindSetting extends BindSetting {
      private MenuBindSetting() {
         super("gui.interface.menu.bind", "gui.interface.menu.bind.desc");
      }

      @Override
      public int getKeyCode() {
         return MenuModule.INSTANCE.getKeyCode();
      }

      @Override
      public int getModifiers() {
         return MenuModule.INSTANCE.getKeyModifiers();
      }

      @Override
      public String getNameKey() {
         return KeyName.method6702(MenuModule.INSTANCE.getKeyCode(), MenuModule.INSTANCE.getKeyModifiers());
      }

      @Override
      public void setBind(int var1, int var2) {
         if (var1 != -1) {
            MenuModule.INSTANCE.setKeyCode(var1);
            MenuModule.INSTANCE.setKeyModifiers(KeyName.method6698(var2));
         }
      }

      @Override
      public void safe(JsonObject var1) {
      }

      @Override
      public void load(JsonObject var1) {
      }
   }
}

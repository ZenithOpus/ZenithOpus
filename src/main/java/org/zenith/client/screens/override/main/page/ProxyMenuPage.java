package org.zenith.client.screens.override.main.page;

import java.util.List;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.misc.ProxyConfig;
import org.zenith.utility.network.Proxies;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class ProxyMenuPage implements MainMenuPage {
   private static final float WIDTH = MenuTheme.px(431.0F);
   private static final float LIST_HEIGHT = MenuTheme.px(342.0F);
   private static final float CONTROLS_GAP = MenuTheme.px(48.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(62.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float ROW_RADIUS = MenuTheme.px(12.0F);
   private static final float ROW_PADDING = MenuTheme.px(16.0F);
   private static final float RAIL_GAP = MenuTheme.px(16.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float CONTROL_GAP = MenuTheme.px(8.0F);
   private static final float BUTTON_GAP = MenuTheme.px(4.0F);
   private final MenuTextField addressField = new MenuTextField(MenuIcons.ADDRESS, "host:port[:user:pass]");
   private final MenuButton typeButton = MenuButton.ghost(MenuIcons.PROXY, "SOCKS5");
   private final MenuButton addButton = MenuButton.iconOnly(MenuIcons.ADD, MenuButton.Style.ACCENT);
   private final MenuScroll scroll = new MenuScroll();
   private ProxyConfig.Type pendingType = ProxyConfig.Type.SOCKS5;
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private BoundsData[] rowBounds = new BoundsData[0];
   private BoundsData[] removeBounds = new BoundsData[0];
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

   public ProxyMenuPage() {
      this.addButton.onClick(this::addProxy);
      this.addressField.setOnEnter(this::addProxy);
      this.typeButton.onClick(() -> {
         this.pendingType = this.pendingType == ProxyConfig.Type.SOCKS5 ? ProxyConfig.Type.SOCKS4 : ProxyConfig.Type.SOCKS5;
         this.typeButton.setLabel(this.pendingType.name());
      });
   }

   private Proxies manager() {
      return Zenith.getInstance().getProxies();
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return LIST_HEIGHT + CONTROLS_GAP + MenuTextField.HEIGHT;
   }

   private void addProxy() {
      ProxyConfig var1 = ProxyConfig.method439(this.addressField.getValue(), this.pendingType);
      if (var1 != null) {
         this.manager().method6537(var1);
         this.addressField.clear();
         this.addressField.setFocused(false);
      }
   }

   @Override
   public void layout(BoundsData var1) {
      float var2 = var1.getFloatValue2() + LIST_HEIGHT + CONTROLS_GAP;
      float var3 = this.typeButton.preferredWidth();
      float var4 = var1.width() - var3 - MenuButton.HEIGHT - CONTROL_GAP - BUTTON_GAP;
      this.addressField.setBounds(var1.getFloatValue(), var2, var4, MenuTextField.HEIGHT);
      this.typeButton.setBounds(var1.getFloatValue() + var4 + CONTROL_GAP, var2, var3, MenuButton.HEIGHT);
      this.addButton.setBounds(var1.getFloatValue() + var1.width() - MenuButton.HEIGHT, var2, MenuButton.HEIGHT, MenuButton.HEIGHT);
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
      var var5 = this.manager().method6535();
      BoundsData var6 = MenuSkin.listArea(var1, var4, LIST_HEIGHT);
      float var7 = var6.width() - RAIL_GAP - RAIL_WIDTH;
      float var8 = var5.isEmpty() ? 0.0F : var5.size() * ROW_HEIGHT + (var5.size() - 1) * ROW_GAP;
      this.scroll.update(var6.height(), var8);
      this.viewport = new BoundsData(var6.getFloatValue(), var6.getFloatValue2(), var7, var6.height());
      this.rowBounds = new BoundsData[var5.size()];
      this.removeBounds = new BoundsData[var5.size()];
      float var9 = this.entrance.method2808(1.0F);
      var1.enableScissor(var6.getFloatValue(), var6.getFloatValue2(), var6.getFloatValue() + var7, var6.getFloatValue2() + var6.height());
      float var10 = var6.getFloatValue2() - this.scroll.offset();

      for (int var11 = 0; var11 < var5.size(); var11++) {
         this.renderRow(var1, var2, var3, (ProxyConfig)var5.get(var11), var11, var6.getFloatValue(), var10 + MenuSkin.enterOffset(var9, var11), var7);
         this.rowBounds[var11] = new BoundsData(var6.getFloatValue(), var10, var7, ROW_HEIGHT);
         var10 += ROW_HEIGHT + ROW_GAP;
      }

      var1.disableScissor();
      if (var5.isEmpty()) {
         Font var13 = MenuTheme.medium(12.0F);
         String var12 = MenuTheme.t("menu.proxy.empty");
         var1.drawText(
            var13,
            var12,
            var6.getFloatValue() + (var7 - var13.width(var12)) / 2.0F,
            var6.getFloatValue2() + (var6.height() - var13.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRail(var1, var6.getFloatValue() + var6.width() - RAIL_WIDTH, var6.getFloatValue2(), var6.height());
      this.addressField.render(var1, var2, var3);
      this.typeButton.render(var1, var2, var3);
      this.addButton.render(var1, var2, var3);
   }

   private void renderRow(MenuDrawContext var1, float var2, float var3, ProxyConfig var4, int var5, float var6, float var7, float var8) {
      BoundsData var9 = new BoundsData(var6, var7, var8, ROW_HEIGHT);
      this.rowBounds[var5] = var9;
      boolean var10 = this.manager().getIntValue2() == var5;
      boolean var11 = var9.contains(var2, var3);
      ZenithColor var12 = var11 ? MenuTheme.plate().method7295(MenuTheme.track(), 0.5F) : MenuTheme.plate();
      MenuSkin.plate(var1, var6, var7, var8, ROW_HEIGHT, var10 ? var12.method7295(MenuTheme.accent(), 0.18F) : var12);
      Font var13 = MenuTheme.medium(11.0F);
      Font var14 = MenuTheme.regular(10.0F);
      float var15 = MenuTheme.px(30.0F);
      float var16 = var7 + (ROW_HEIGHT - var15) / 2.0F;
      float var17 = MenuSkin.ROW_TILE;
      float var18 = var7 + (ROW_HEIGHT - var17) / 2.0F;
      if (var10) {
         MenuSkin.accentTile(var1, var6 + MenuSkin.EDGE, var18, var17, MenuIcons.PROXY);
      } else {
         MenuSkin.mutedTile(var1, var6 + MenuSkin.EDGE, var18, var17, MenuIcons.PROXY, MenuTheme.textTertiary());
      }

      float var19 = MenuSkin.divider(var1, var6 + MenuSkin.EDGE + var17, var7, ROW_HEIGHT);
      var1.drawText(var13, var4.method441(), var19, var16, MenuTheme.text());
      String var20 = var4.getType().name() + (var4.method440() ? " - " + MenuTheme.t("menu.proxy.authenticated") : "");
      var1.drawText(var14, var20, var19, var16 + MenuTheme.px(18.0F), MenuTheme.textSecondary());
      String var21 = MenuTheme.t(var10 ? "menu.proxy.active" : "menu.proxy.idle");
      Font var22 = MenuTheme.medium(10.0F);
      float var23 = var6 + var8 - MenuSkin.EDGE;
      float var24 = MenuTheme.px(12.0F);
      float var25 = var7 + (ROW_HEIGHT - var24) / 2.0F;
      BoundsData var26 = new BoundsData(var23 - var24, var25, var24, var24);
      this.removeBounds[var5] = var26;
      var1.drawText(
         var22,
         var21,
         var26.getFloatValue() - MenuTheme.px(8.0F) - var22.width(var21),
         var7 + (ROW_HEIGHT - var22.height()) / 2.0F,
         var10 ? MenuTheme.accent() : MenuTheme.textTertiary()
      );
      Font var27 = MenuIcons.REMOVE.font(12.0F);
      var1.drawText(
         var27,
         MenuIcons.REMOVE.glyph(),
         var26.getFloatValue(),
         var25 + (var24 - var27.height()) / 2.0F,
         var26.contains(var2, var3) ? MenuTheme.danger() : MenuTheme.textTertiary()
      );
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.addressField.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.typeButton.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.addButton.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (var5 == 0 && this.viewport.contains(var1, var3)) {
         for (int var6 = 0; var6 < this.rowBounds.length; var6++) {
            if (this.removeBounds[var6] != null && this.removeBounds[var6].contains(var1, var3)) {
               this.manager().method6538(var6);
               return true;
            }

            if (this.rowBounds[var6] != null && this.rowBounds[var6].contains(var1, var3)) {
               this.manager().select(var6);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.scroll.scroll(var5, ROW_HEIGHT + ROW_GAP);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.addressField.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.addressField.charTyped(var1, var2);
   }
}

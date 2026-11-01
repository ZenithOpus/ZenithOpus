package org.zenith.client.screens.override.main;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.component.MenuNavigationBar;
import org.zenith.client.screens.override.main.page.AccountManagerPage;
import org.zenith.client.screens.override.main.page.HomeMenuPage;
import org.zenith.client.screens.override.main.page.MainMenuPage;
import org.zenith.client.screens.override.main.page.MultiplayerMenuPage;
import org.zenith.client.screens.override.main.page.ProxyMenuPage;
import org.zenith.client.screens.override.main.page.SettingsMenuPage;
import org.zenith.client.screens.override.main.page.SingleplayerMenuPage;
import org.zenith.client.screens.override.particle.MenuParticleRenderer;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class MainMenuScreen extends Screen implements MinecraftAccess {
   public static final int HOME_TAB = 0;
   public static final int SINGLEPLAYER_TAB = 1;
   public static final int MULTIPLAYER_TAB = 2;
   public static final int ACCOUNTS_TAB = 3;
   public static final int PROXY_TAB = 4;
   public static final int SETTINGS_TAB = 5;
   private static final float SECTION_GUTTER = MenuTheme.px(48.0F);
   private static final float NAVIGATION_GAP = MenuTheme.px(32.0F);
   private static final float SCREEN_MARGIN = MenuTheme.px(24.0F);
   private final MainMenuBlurRenderer backdrop = new MainMenuBlurRenderer();
   private final List<MainMenuPage> pages = new ArrayList<>();
   private final MenuNavigationBar navigationBar;
   private final Animation pageTransition = new Animation(260L, 1.0F, Easing.INSTANCE7);
   private final Animation openTransition = new Animation(380L, 0.0F, Easing.INSTANCE7);
   private int activeIndex;
   private float fit = 1.0F;

   public MainMenuScreen() {
      super(Text.literal("Zenith"));
      this.pages.add(new HomeMenuPage());
      this.pages.add(new SingleplayerMenuPage(this));
      this.pages.add(new MultiplayerMenuPage(this));
      this.pages.add(new AccountManagerPage());
      this.pages.add(new ProxyMenuPage());
      this.pages.add(new SettingsMenuPage());
      this.navigationBar = new MenuNavigationBar(
         List.of(
            new MenuNavigationBar.Item(MenuIcons.HOME, "menu.nav.home", () -> this.select(0)),
            new MenuNavigationBar.Item(MenuIcons.SINGLEPLAYER, "menu.nav.singleplayer", () -> this.select(1)),
            new MenuNavigationBar.Item(MenuIcons.MULTIPLAYER, "menu.nav.multiplayer", () -> this.select(2)),
            new MenuNavigationBar.Item(MenuIcons.ALT_MANAGER, "menu.nav.accounts", () -> this.select(3)),
            new MenuNavigationBar.Item(MenuIcons.PROXY, "menu.nav.proxy", () -> this.select(4)),
            new MenuNavigationBar.Item(MenuIcons.SETTINGS, "menu.nav.settings", () -> this.select(5)),
            new MenuNavigationBar.Item(MenuIcons.EXIT, "menu.nav.exit", () -> mc.scheduleStop())
         )
      );
   }

   public void select(int var1) {
      if (var1 != this.activeIndex && var1 >= 0 && var1 < this.pages.size()) {
         this.pages.get(this.activeIndex).onHide();
         this.activeIndex = var1;
         this.pages.get(this.activeIndex).onShow();
         this.pageTransition.method2809(0.0F);
      }
   }

   private MainMenuPage page() {
      return this.pages.get(this.activeIndex);
   }

   @Override
   public void init() {
      Zenith.getInstance().getMenuParticleRenderer().init();
      this.openTransition.method2809(0.0F);
      this.pageTransition.method2809(0.0F);
      super.init();
   }

   @Override
   public void onDisplayed() {
      super.onDisplayed();
      this.openTransition.method2809(0.0F);
      this.pageTransition.method2809(0.0F);
      this.page().onShow();
   }

   @Override
   public void tick() {
      this.page().tick();
      super.tick();
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext var5 = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      Window var6 = mc.getWindow();
      float var7 = var6.getScaledWidth();
      float var8 = var6.getScaledHeight();
      MenuParticleRenderer.renderBackgroundAndParticles(var5);
      this.backdrop.capture(var5, MenuTheme.px(36.0F));
      MenuSurface.setMenuSource(this.backdrop.texture());
      this.openTransition.method2808(1.0F);
      this.pageTransition.method2808(1.0F);
      MainMenuPage var9 = this.page();
      var9.update();
      float var10 = var9.footerHeight();
      float var11 = MenuSkin.HEADER_HEIGHT
         + SECTION_GUTTER
         + MenuNavigationBar.HEIGHT
         + NAVIGATION_GAP
         + var9.contentHeight()
         + (var10 > 0.0F ? SECTION_GUTTER + var10 : 0.0F);
      float var12 = this.navigationBar.measureWidth(this.activeIndex);
      float var13 = Math.max(var12, Math.max(var9.contentWidth(), var9.footerWidth()));
      float var14 = Math.min(1.0F, (var8 - SCREEN_MARGIN * 2.0F) / Math.max(1.0F, var11));
      float var15 = Math.min(1.0F, (var7 - SCREEN_MARGIN * 2.0F) / Math.max(1.0F, var13));
      this.fit = Math.max(0.35F, Math.min(var14, var15));
      float var16 = var7 / this.fit;
      float var17 = var8 / this.fit;
      float var18 = MenuTheme.snap(Math.max(SCREEN_MARGIN, (var17 - var11) / 2.0F));
      float var19 = MenuTheme.snap(var16 / 2.0F);
      var5.getMatrices().pushMatrix();
      var5.getMatrices().scale(this.fit, this.fit);
      float var20 = (1.0F - this.openTransition.getValue()) * MenuTheme.px(40.0F);
      var5.getMatrices().pushMatrix();
      var5.getMatrices().translate(0.0F, var20);
      float var21 = mouseX / this.fit;
      float var22 = mouseY / this.fit;
      this.renderIdentity(var5, var19, var18);
      float var23 = var18 + MenuSkin.HEADER_HEIGHT + SECTION_GUTTER;
      float var24 = var23 + MenuNavigationBar.HEIGHT + NAVIGATION_GAP;
      BoundsData var25 = new BoundsData(MenuTheme.snap(var19 - var9.contentWidth() / 2.0F), MenuTheme.snap(var24), var9.contentWidth(), var9.contentHeight());
      var9.layout(var25);
      float var26 = (1.0F - this.pageTransition.getValue()) * MenuTheme.px(24.0F);
      var5.getMatrices().pushMatrix();
      var5.getMatrices().translate(0.0F, var26);
      var9.render(var5, var21, var22, var25);
      if (var10 > 0.0F) {
         BoundsData var27 = new BoundsData(
            MenuTheme.snap(var19 - var9.footerWidth() / 2.0F), var24 + var9.contentHeight() + SECTION_GUTTER, var9.footerWidth(), var10
         );
         var9.renderFooter(var5, var21, var22, var27);
      }

      var5.getMatrices().popMatrix();
      this.navigationBar.render(var5, var21, var22, var19, var23, this.activeIndex);
      var5.getMatrices().popMatrix();
      var5.getMatrices().popMatrix();
   }

   private void renderIdentity(MenuDrawContext var1, float var2, float var3) {
      Font var4 = MenuTheme.medium(12.0F);
      Font var5 = MenuTheme.regular(12.0F);
      String var6 = "Zenith";
      String var7 = MenuTheme.t("menu.welcome");
      String var8 = Zenith.getInstance().getEntry().getUsername();
      if (var8 == null || var8.isEmpty()) {
         var8 = MenuTheme.t("menu.guest");
      }

      float var9 = MenuSkin.HEADER_HEIGHT;
      float var10 = MenuSkin.TILE_INSET
         + MenuSkin.HEADER_TILE
         + MenuSkin.dividerWidth()
         + var4.width(var6)
         + MenuSkin.dividerWidth()
         + var5.width(var7)
         + MenuSkin.GAP
         + var4.width(var8)
         + MenuSkin.EDGE;
      float var11 = MenuTheme.snap(var2 - var10 / 2.0F);
      MenuSkin.panel(var1, var11, var3, var10, var9);
      float var12 = var3 + MenuSkin.TILE_INSET;
      MenuSkin.accentTile(var1, var11 + MenuSkin.TILE_INSET, var12, MenuSkin.HEADER_TILE, MenuIcons.ZENITH);
      float var13 = MenuSkin.divider(var1, var11 + MenuSkin.TILE_INSET + MenuSkin.HEADER_TILE, var3, var9);
      var1.drawText(var4, var6, var13, var3 + (var9 - var4.height()) / 2.0F, MenuTheme.text());
      var13 += var4.width(var6);
      var13 = MenuSkin.divider(var1, var13, var3, var9);
      var1.drawText(var5, var7, var13, var3 + (var9 - var5.height()) / 2.0F, MenuTheme.textSecondary());
      var13 += var5.width(var7) + MenuSkin.GAP;
      var1.drawText(var4, var8, var13, var3 + (var9 - var4.height()) / 2.0F, MenuTheme.accent());
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      double var6 = var1 / this.fit;
      double var8 = var3 / this.fit;
      if (this.navigationBar.mouseClicked(var6, var8)) {
         return true;
      } else {
         return this.page().mouseClicked(var6, var8, var5) ? true : super.mouseClicked(new Click(var1, var3, new MouseInput(var5, 0)), false);
      }
   }

   @Override
   public boolean mouseReleased(Click click) {
      return this.mouseReleased(click.x(), click.y(), click.button());
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.page().mouseReleased(var1 / this.fit, var3 / this.fit, var5) ? true : super.mouseReleased(new Click(var1, var3, new MouseInput(var5, 0)));
   }

   @Override
   public boolean mouseDragged(Click click, double offsetX, double offsetY) {
      return this.mouseDragged(click.x(), click.y(), click.button(), offsetX, offsetY);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.page().mouseDragged(var1 / this.fit, var3 / this.fit, var5)
         ? true
         : super.mouseDragged(new Click(var1, var3, new MouseInput(var5, 0)), var6, var8);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.page().mouseScrolled(mouseX / this.fit, mouseY / this.fit, verticalAmount)
         ? true
         : super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.page().keyPressed(var1, var2, var3) ? true : super.keyPressed(new KeyInput(var1, var2, var3));
   }

   @Override
   public boolean charTyped(CharInput input) {
      return this.charTyped((char)input.codepoint(), input.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      return this.page().charTyped(var1, var2) ? true : super.charTyped(new CharInput(var1, var2));
   }

   @Override
   public void removed() {
      this.page().onHide();
      MenuSurface.setMenuSource(null);
      this.backdrop.close();
      super.removed();
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void renderInGameBackground(DrawContext context) {
   }
}

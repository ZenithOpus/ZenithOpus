package org.zenith.client.screens.override.main.page;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.PlayerSkinWidget;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.component.SkinPreview;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.api.MicrosoftAuth;
import org.zenith.utility.misc.Account;
import org.zenith.utility.misc.Accounts;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class AccountManagerPage implements MainMenuPage {
   private static final float WIDTH = MenuTheme.px(800.0F);
   private static final float STRIP_HEIGHT = MenuTheme.px(314.0F);
   private static final float RAIL_GAP = MenuTheme.px(24.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float CONTROLS_GAP = MenuTheme.px(48.0F);
   private static final float CARD_WIDTH = MenuTheme.px(188.0F);
   private static final float CARD_GAP = MenuTheme.px(16.0F);
   private static final float PANEL_HEIGHT = MenuTheme.px(264.0F);
   private static final float PANEL_GAP = MenuTheme.px(8.0F);
   private static final float PANEL_RADIUS = MenuTheme.px(12.0F);
   private static final float PANEL_PADDING_Y = MenuTheme.px(16.0F);
   private static final float SKIN_HEIGHT = MenuTheme.px(192.0F);
   private static final float SKIN_WINDOW = MenuTheme.px(92.0F);
   private static final float SELECT_WIDTH = MenuTheme.px(138.0F);
   private static final float ACTION_GAP = MenuTheme.px(8.0F);
   private static final float INPUT_WIDTH = MenuTheme.px(256.0F);
   private static final float BUTTON_GAP = MenuTheme.px(4.0F);
   private static final DateTimeFormatter ADDED_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.systemDefault());
   private static final List<String> NAME_PARTS = List.of(
      "Void",
      "Nova",
      "Echo",
      "Frost",
      "Ember",
      "Lunar",
      "Cyber",
      "Neon",
      "Shadow",
      "Prism",
      "Riot",
      "Drift",
      "Pulse",
      "Zen",
      "Flux",
      "Onyx",
      "Vertex",
      "Quartz",
      "Raven",
      "Solar"
   );
   private final MenuTextField usernameField = new MenuTextField(MenuIcons.USER, MenuTheme.t("menu.accounts.username"));
   private final MenuButton addButton = MenuButton.iconOnly(MenuIcons.ADD, MenuButton.Style.ACCENT);
   private final MenuButton microsoftButton = MenuButton.iconOnly(MenuIcons.LAUNCH, MenuButton.Style.GHOST);
   private final MenuButton randomButton = MenuButton.iconOnly(MenuIcons.RANDOM, MenuButton.Style.GHOST);
   private final MenuButton clearButton = MenuButton.iconOnly(MenuIcons.CLEAR, MenuButton.Style.DANGER);
   private final MenuButton cancelLoginButton = MenuButton.ghost(MenuIcons.CLEAR, MenuTheme.t("menu.accounts.cancel"));
   private final MenuScroll scroll = new MenuScroll();
   private final Map<String, PlayerSkinWidget> skinWidgets = new HashMap<>();
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private BoundsData[] cardSelect = new BoundsData[0];
   private BoundsData[] cardRemove = new BoundsData[0];
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private MicrosoftAuth.Data deviceCode;
   private BoundsData deviceCodeBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private boolean loginRunning;
   private volatile int loginGeneration;

   public AccountManagerPage() {
      this.addButton.onClick(this::addOffline);
      this.microsoftButton.onClick(this::startMicrosoftLogin);
      this.randomButton.onClick(() -> this.usernameField.setValue(randomName()));
      this.clearButton.onClick(this.usernameField::clear);
      this.cancelLoginButton.onClick(this::cancelLogin);
      this.usernameField.setOnEnter(this::addOffline);
      this.usernameField.setMaxLength(16);
   }

   private Accounts manager() {
      return Zenith.getInstance().getAccounts();
   }

   private PlayerSkinWidget skinWidget(int var1, Account var2) {
      String var3 = var2.getName() + "#" + var2.getType();
      return this.skinWidgets
         .computeIfAbsent(
            var3,
            var1x -> {
               String var2x = var2.getName();
               UUID var3x = var2.method5472();
               return new PlayerSkinWidget(
                  Math.round(SKIN_WINDOW),
                  Math.round(SKIN_HEIGHT),
                  MinecraftClient.getInstance().getLoadedEntityModels(),
                  () -> SkinPreview.textures(var2x, var3x)
               );
            }
         );
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return STRIP_HEIGHT + RAIL_GAP + RAIL_WIDTH + CONTROLS_GAP + MenuTextField.HEIGHT;
   }

   private void addOffline() {
      String var1 = this.usernameField.getValue().trim();
      if (!var1.isEmpty()) {
         this.manager().method4753(Account.method5470(var1));
         this.manager().method4757(MenuTheme.t("menu.accounts.added") + " " + var1);
         this.usernameField.clear();
         this.usernameField.setFocused(false);
      }
   }

   private static String randomName() {
      ThreadLocalRandom var0 = ThreadLocalRandom.current();
      return NAME_PARTS.get(var0.nextInt(NAME_PARTS.size())) + NAME_PARTS.get(var0.nextInt(NAME_PARTS.size())).toLowerCase(Locale.ROOT) + var0.nextInt(10, 100);
   }

   private void startMicrosoftLogin() {
      if (!this.loginRunning) {
         this.loginRunning = true;
         int var1 = ++this.loginGeneration;
         Accounts var2 = this.manager();
         var2.method4757(MenuTheme.t("menu.accounts.contacting"));
         MicrosoftAuth.method3056().whenComplete((var3, var4) -> MinecraftClient.getInstance().execute(() -> {
            if (var1 == this.loginGeneration) {
               if (var4 == null && var3 != null) {
                  this.deviceCode = var3;
                  var2.method4757(MenuTheme.t("menu.accounts.waiting"));
                  MinecraftClient.getInstance().keyboard.setClipboard(var3.getText());
                  Util.getOperatingSystem().open(var3.getText2());
                  this.awaitLogin(var3, var1);
               } else {
                  this.loginRunning = false;
                  var2.method4757(MenuTheme.t("menu.accounts.loginFailed") + " " + Accounts.rootMessage(var4));
               }
            }
         }));
      }
   }

   private void awaitLogin(MicrosoftAuth.Data var1, int var2) {
      Accounts var3 = this.manager();
      MicrosoftAuth.method3058(var1, var3x -> MinecraftClient.getInstance().execute(() -> {
         if (var2 == this.loginGeneration) {
            var3.method4757(var3x);
         }
      }), () -> var2 != this.loginGeneration).whenComplete((var3x, var4) -> MinecraftClient.getInstance().execute(() -> {
         if (var2 == this.loginGeneration) {
            this.loginRunning = false;
            this.deviceCode = null;
            if (var4 == null && var3x != null) {
               var3.method4753(Account.method5471(var3x.name(), var3x.uuid(), var3x.getText2(), var3x.getText3()));
               var3.method4757(MenuTheme.t("menu.accounts.added") + " " + var3x.name());
            } else {
               var3.method4757(MenuTheme.t("menu.accounts.loginFailed") + " " + Accounts.rootMessage(var4));
            }
         }
      }));
   }

   private void cancelLogin() {
      this.loginGeneration++;
      this.deviceCode = null;
      this.loginRunning = false;
      this.manager().method4757("Microsoft login cancelled");
   }

   @Override
   public void layout(BoundsData var1) {
      float var2 = var1.getFloatValue2() + STRIP_HEIGHT + RAIL_GAP + RAIL_WIDTH + CONTROLS_GAP;
      float var3 = INPUT_WIDTH + ACTION_GAP + MenuButton.HEIGHT * 4.0F + BUTTON_GAP * 3.0F;
      float var4 = var1.getFloatValue() + (var1.width() - var3) / 2.0F;
      this.usernameField.setBounds(var4, var2, INPUT_WIDTH, MenuTextField.HEIGHT);
      var4 += INPUT_WIDTH + ACTION_GAP;

      for (MenuButton var6 : List.of(this.addButton, this.microsoftButton, this.randomButton, this.clearButton)) {
         var6.setBounds(var4, var2, MenuButton.HEIGHT, MenuButton.HEIGHT);
         var4 += MenuButton.HEIGHT + BUTTON_GAP;
      }

      if (this.deviceCode != null) {
         float var9 = MenuTheme.px(360.0F);
         float var10 = MenuTheme.px(160.0F);
         this.deviceCodeBounds = new BoundsData(
            var1.getFloatValue() + (var1.width() - var9) / 2.0F, var1.getFloatValue2() + (STRIP_HEIGHT - var10) / 2.0F, var9, var10
         );
         float var7 = this.cancelLoginButton.preferredWidth();
         this.cancelLoginButton
            .setBounds(
               this.deviceCodeBounds.getFloatValue() + (var9 - var7) / 2.0F,
               this.deviceCodeBounds.getFloatValue2() + var10 - MenuTheme.px(16.0F) - MenuButton.HEIGHT,
               var7,
               MenuButton.HEIGHT
            );
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
      var var5 = this.manager().method4751();
      float var6 = var5.isEmpty() ? 0.0F : var5.size() * CARD_WIDTH + (var5.size() - 1) * CARD_GAP;
      this.scroll.update(var4.width(), var6);
      this.cardSelect = new BoundsData[var5.size()];
      this.cardRemove = new BoundsData[var5.size()];
      BoundsData var7 = MenuSkin.listArea(var1, var4, STRIP_HEIGHT);
      this.viewport = new BoundsData(var7.getFloatValue(), var7.getFloatValue2(), var7.width(), var7.height());
      float var8 = this.entrance.method2808(1.0F);
      var1.enableScissor(var7.getFloatValue(), var7.getFloatValue2(), var7.getFloatValue() + var7.width(), var7.getFloatValue2() + var7.height());
      float var9 = var7.getFloatValue() + Math.max(0.0F, (var7.width() - var6) / 2.0F) - this.scroll.offset();

      for (int var10 = 0; var10 < var5.size(); var10++) {
         if (var9 + CARD_WIDTH >= var7.getFloatValue() && var9 <= var7.getFloatValue() + var7.width()) {
            this.renderCard(var1, var2, var3, (Account)var5.get(var10), var10, var9, var7.getFloatValue2() + MenuSkin.enterOffset(var8, var10));
         }

         var9 += CARD_WIDTH + CARD_GAP;
      }

      var1.disableScissor();
      if (var5.isEmpty()) {
         Font var12 = MenuTheme.medium(12.0F);
         String var11 = MenuTheme.t("menu.accounts.empty");
         var1.drawText(
            var12,
            var11,
            var7.getFloatValue() + (var7.width() - var12.width(var11)) / 2.0F,
            var7.getFloatValue2() + (var7.height() - var12.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRailHorizontal(var1, var7.getFloatValue(), var7.getFloatValue2() + var7.height() - RAIL_WIDTH, var7.width());
      this.usernameField.render(var1, var2, var3);
      this.addButton.render(var1, var2, var3);
      this.microsoftButton.render(var1, var2, var3);
      this.randomButton.render(var1, var2, var3);
      this.clearButton.render(var1, var2, var3);
      this.renderStatus(var1, var4);
      if (this.deviceCode != null) {
         this.renderDeviceCode(var1, var2, var3);
      }
   }

   private void renderStatus(MenuDrawContext var1, BoundsData var2) {
      String var3 = this.manager().getStatus();
      if (var3 != null && !var3.isEmpty()) {
         Font var4 = MenuTheme.regular(10.0F);
         var1.drawText(
            var4,
            var3,
            var2.getFloatValue() + (var2.width() - var4.width(var3)) / 2.0F,
            var2.getFloatValue2() + this.contentHeight() + MenuTheme.px(12.0F),
            MenuTheme.textTertiary()
         );
      }
   }

   private void renderCard(MenuDrawContext var1, float var2, float var3, Account var4, int var5, float var6, float var7) {
      boolean var8 = this.manager().getIntValue() == var5;
      ProxyDotRadiusData var9 = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      MenuSurface.card(var1, var6, var7, CARD_WIDTH, PANEL_HEIGHT, PANEL_RADIUS, MenuTheme.surface(), var8 ? MenuTheme.border() : MenuTheme.borderSubtle());
      PlayerSkinWidget var10 = this.skinWidget(var5, var4);
      float var11 = var6 + (CARD_WIDTH - SKIN_WINDOW) / 2.0F;
      var10.setPosition(Math.round(var11), Math.round(var7 + PANEL_PADDING_Y));
      var10.setDimensions(Math.round(SKIN_WINDOW), Math.round(SKIN_HEIGHT));
      var1.enableScissor(var11, var7 + PANEL_PADDING_Y, var11 + SKIN_WINDOW, var7 + PANEL_PADDING_Y + SKIN_HEIGHT);
      var10.render(var1, (int)var2, (int)var3, 0.0F);
      var1.disableScissor();
      Font var12 = MenuTheme.medium(12.0F);
      Font var13 = MenuTheme.regular(11.0F);
      float var14 = var7 + PANEL_PADDING_Y + SKIN_HEIGHT + PANEL_GAP;
      var1.drawText(var12, var4.getName(), var6 + (CARD_WIDTH - var12.width(var4.getName())) / 2.0F, var14, MenuTheme.text());
      String var15 = ADDED_FORMAT.format(Instant.ofEpochMilli(var4.getLongValue()));
      var1.drawText(var13, var15, var6 + (CARD_WIDTH - var13.width(var15)) / 2.0F, var14 + MenuTheme.px(18.0F), MenuTheme.textSecondary());
      float var16 = var7 + PANEL_HEIGHT + PANEL_GAP;
      float var17 = MenuButton.HEIGHT;
      float var18 = Math.min(SELECT_WIDTH, CARD_WIDTH - var17 - ACTION_GAP);
      BoundsData var19 = new BoundsData(var6, var16, var18, MenuButton.HEIGHT);
      BoundsData var20 = new BoundsData(var6 + var18 + ACTION_GAP, var16, var17, MenuButton.HEIGHT);
      this.cardSelect[var5] = var19;
      this.cardRemove[var5] = var20;
      this.drawAction(var1, var19, var8 ? MenuIcons.ACCEPT : MenuIcons.USER, var8 ? "Selected" : "Select", var8, var2, var3);
      this.drawIconAction(var1, var20, MenuIcons.REMOVE, var8, var2, var3);
   }

   private void drawAction(MenuDrawContext var1, BoundsData var2, MenuIcon var3, String var4, boolean var5, float var6, float var7) {
      boolean var8 = var2.contains(var6, var7);
      ProxyDotRadiusData var9 = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      if (var5) {
         var1.drawRoundedRect(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), var9, MenuTheme.accent());
      } else {
         var1.drawRoundedRect(
            var2.getFloatValue(),
            var2.getFloatValue2(),
            var2.width(),
            var2.height(),
            var9,
            var8 ? MenuTheme.surface().method7295(MenuTheme.track(), 0.6F) : MenuTheme.surface()
         );
         var1.drawRoundedBorder(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), -0.1F, var9, MenuTheme.borderSubtle());
      }

      Font var10 = MenuTheme.medium(12.0F);
      Font var11 = var3.font(12.0F);
      float var12 = var3.width(12.0F) + MenuTheme.px(8.0F) + var10.width(var4);
      float var13 = var2.getFloatValue() + (var2.width() - var12) / 2.0F;
      ZenithColor var14 = var5 ? MenuTheme.onAccent() : MenuTheme.textTertiary();
      ZenithColor var15 = var5 ? MenuTheme.onAccent() : MenuTheme.textSecondary();
      var1.drawText(var11, var3.glyph(), var13, var2.getFloatValue2() + (var2.height() - var11.height()) / 2.0F, var14);
      var1.drawText(var10, var4, var13 + var3.width(12.0F) + MenuTheme.px(8.0F), var2.getFloatValue2() + (var2.height() - var10.height()) / 2.0F, var15);
   }

   private void drawIconAction(MenuDrawContext var1, BoundsData var2, MenuIcon var3, boolean var4, float var5, float var6) {
      boolean var7 = var2.contains(var5, var6);
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      if (var4) {
         var1.drawRoundedRect(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), var8, MenuTheme.danger());
      } else {
         var1.drawRoundedRect(
            var2.getFloatValue(),
            var2.getFloatValue2(),
            var2.width(),
            var2.height(),
            var8,
            var7 ? MenuTheme.surface().method7295(MenuTheme.track(), 0.6F) : MenuTheme.surface()
         );
         var1.drawRoundedBorder(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), -0.1F, var8, MenuTheme.borderSubtle());
      }

      Font var9 = var3.font(12.0F);
      ZenithColor var10 = var4 ? MenuTheme.onAccent() : (var7 ? MenuTheme.danger() : MenuTheme.textTertiary());
      var1.drawText(
         var9,
         var3.glyph(),
         var2.getFloatValue() + (var2.width() - var3.width(12.0F)) / 2.0F,
         var2.getFloatValue2() + (var2.height() - var9.height()) / 2.0F,
         var10
      );
   }

   private void renderDeviceCode(MenuDrawContext var1, float var2, float var3) {
      BoundsData var4 = this.deviceCodeBounds;
      ProxyDotRadiusData var5 = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      var1.drawRoundedRect(
         var4.getFloatValue(), var4.getFloatValue2(), var4.width(), var4.height(), var5, MenuTheme.surface().method7295(ZenithColor.INSTANCE2, 0.85F)
      );
      var1.drawRoundedBorder(var4.getFloatValue(), var4.getFloatValue2(), var4.width(), var4.height(), -0.1F, var5, MenuTheme.borderSubtle());
      Font var6 = MenuTheme.semibold(14.0F);
      Font var7 = MenuTheme.semibold(20.0F);
      Font var8 = MenuTheme.regular(10.0F);
      String var9 = "Sign in to Microsoft";
      var1.drawText(var6, var9, var4.getFloatValue() + (var4.width() - var6.width(var9)) / 2.0F, var4.getFloatValue2() + MenuTheme.px(16.0F), MenuTheme.text());
      String var10 = this.deviceCode.getText();
      var1.drawText(
         var7, var10, var4.getFloatValue() + (var4.width() - var7.width(var10)) / 2.0F, var4.getFloatValue2() + MenuTheme.px(46.0F), MenuTheme.accent()
      );
      String var11 = "Code copied - enter it at " + this.deviceCode.getText2();
      var1.drawText(
         var8, var11, var4.getFloatValue() + (var4.width() - var8.width(var11)) / 2.0F, var4.getFloatValue2() + MenuTheme.px(82.0F), MenuTheme.textSecondary()
      );
      this.cancelLoginButton.render(var1, var2, var3);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.deviceCode != null) {
         if (this.cancelLoginButton.mouseClicked(var1, var3, var5)) {
            return true;
         }

         if (this.deviceCodeBounds.contains(var1, var3)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.deviceCode.getText());
            return true;
         }
      }

      if (this.usernameField.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (!this.addButton.mouseClicked(var1, var3, var5)
         && !this.microsoftButton.mouseClicked(var1, var3, var5)
         && !this.randomButton.mouseClicked(var1, var3, var5)
         && !this.clearButton.mouseClicked(var1, var3, var5)) {
         if (var5 == 0 && this.viewport.contains(var1, var3)) {
            for (int var6 = 0; var6 < this.cardSelect.length; var6++) {
               if (this.cardRemove[var6] != null && this.cardRemove[var6].contains(var1, var3)) {
                  this.manager().method4754(var6);
                  return true;
               }

               if (this.cardSelect[var6] != null && this.cardSelect[var6].contains(var1, var3)) {
                  this.manager().select(var6);
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.scroll.scroll(var5, CARD_WIDTH + CARD_GAP);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.usernameField.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.usernameField.charTyped(var1, var2);
   }
}

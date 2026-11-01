package com.altsmod.gui;

import com.altsmod.auth.AccountStorage;
import com.altsmod.auth.MicrosoftOAuth;
import com.altsmod.auth.SessionHelper;
import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.session.Session;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;

public class AltManagerScreen extends Screen {
   private final Screen parent;
   private final List<AccountEntry> accounts = new ArrayList<>();
   private final List<AccountEntry> filteredAccounts = new ArrayList<>();
   private final Map<String, Supplier<SkinTextures>> skinCache = new HashMap<>();
   private AccountEntry selectedAccount = null;
   private TextFieldWidget searchBox;
   private ButtonWidget loginButton;
   private int scrollOffset = 0;
   private final int itemHeight = 24;
   private String status = "§7Готов к работе";
   private boolean authenticating = false;

   public AltManagerScreen(Screen parent) {
      super(Text.literal("Alt Manager"));
      this.parent = parent;
   }

   protected void init() {
      this.loadAccounts();
      int listX = 20;
      int listWidth = 190;
      this.searchBox = new TextFieldWidget(this.textRenderer, listX, 30, listWidth, 18, Text.literal("Поиск..."));
      this.searchBox.setPlaceholder(Text.literal("Поиск аккаунта..."));
      this.searchBox.setChangedListener(this::onSearchChanged);
      this.addDrawableChild(this.searchBox);

      int contentCenterX = (listX + listWidth + 20 + this.width) / 2;
      int btnW = 200;
      int btnX = contentCenterX - btnW / 2;
      int btnStartY = 70;

      this.loginButton = ButtonWidget.builder(Text.literal("▶ ВОЙТИ"), (btn) -> this.loginWithSelectedAccount())
         .dimensions(btnX, btnStartY, btnW, 22)
         .build();
      this.loginButton.active = this.selectedAccount != null;
      this.addDrawableChild(this.loginButton);

      this.addDrawableChild(ButtonWidget.builder(Text.literal("➕ Добавить оффлайн"), (btn) -> {
         if (this.client != null) {
            this.client.setScreen(new AddOfflineAccountScreen(this));
         }
      }).dimensions(btnX, btnStartY + 26, btnW, 20).build());

      this.addDrawableChild(ButtonWidget.builder(Text.literal("\ud83d\udcc1 Добавить файл (Cookie / Token)"), (btn) -> this.selectFileAndLogin())
         .dimensions(btnX, btnStartY + 50, btnW, 20).build());

      this.addDrawableChild(ButtonWidget.builder(Text.literal("\ud83c\udf10 Войти через браузер"), (btn) -> this.loginViaBrowser())
         .dimensions(btnX, btnStartY + 74, btnW, 20).build());

      this.addDrawableChild(ButtonWidget.builder(Text.literal("\ud83d\udcc2 Папка Alts"), (btn) -> this.openAltsFolder())
         .dimensions(btnX, btnStartY + 98, btnW, 20).build());

      this.addDrawableChild(ButtonWidget.builder(Text.literal("\ud83d\udd04 Обновить список"), (btn) -> {
         this.loadAccounts();
         this.setStatus("§7Список аккаунтов обновлен.");
      }).dimensions(btnX, btnStartY + 122, btnW, 20).build());

      this.addDrawableChild(ButtonWidget.builder(Text.literal("Назад"), (btn) -> {
         if (this.client != null) {
            this.client.setScreen(this.parent);
         }
      }).dimensions(btnX, btnStartY + 150, btnW, 20).build());
   }

   public void onOfflineAccountAdded(String name) {
      this.loadAccounts();
      for (AccountEntry a : this.accounts) {
         if (a.name().equalsIgnoreCase(name)) {
            this.selectedAccount = a;
            break;
         }
      }
      if (this.selectedAccount != null) {
         this.loginOffline(this.selectedAccount);
      }
      this.setStatus("§aОффлайн аккаунт §f" + name + " §aдобавлен и выбран!");
      this.updateLoginButton();
   }

   public void loginOffline(AccountEntry account) {
      MinecraftClient mc = this.client;
      if (mc != null) {
         UUID uuid = account.uuid() != null
            ? account.uuid()
            : UUID.nameUUIDFromBytes(("OfflinePlayer:" + account.displayName()).getBytes(StandardCharsets.UTF_8));
         Session session = new Session(account.displayName(), uuid, "", Optional.empty(), Optional.empty());
         SessionHelper.applySession(mc, session);
         this.setStatus("§aУспешный вход (Offline)! Ник: §f" + account.displayName());
         System.out.println("[AltsMod] Logged in as offline: " + account.displayName() + " (" + uuid + ")");
         this.updateLoginButton();
      }
   }

   private void onSearchChanged(String query) {
      this.updateFilteredAccounts();
   }

   private void loadAccounts() {
      this.accounts.clear();
      if (this.client != null) {
         this.accounts.addAll(AccountStorage.loadAccounts(this.client));
      }
      this.updateFilteredAccounts();
   }

   private void updateFilteredAccounts() {
      this.filteredAccounts.clear();
      String query = this.searchBox != null ? this.searchBox.getText().trim().toLowerCase(Locale.ROOT) : "";

      for (AccountEntry entry : this.accounts) {
         if (query.isEmpty() || entry.displayName().toLowerCase(Locale.ROOT).contains(query)) {
            this.filteredAccounts.add(entry);
         }
      }
   }

   private void updateLoginButton() {
      if (this.loginButton != null) {
         if (this.selectedAccount != null) {
            this.loginButton.setMessage(Text.literal("§a▶ ВОЙТИ: " + this.selectedAccount.displayName()));
            this.loginButton.active = true;
         } else {
            this.loginButton.setMessage(Text.literal("▶ ВОЙТИ (Выберите аккаунт)"));
            this.loginButton.active = false;
         }
      }
   }

   private SkinTextures getSkin(AccountEntry entry) {
      if (this.client == null) {
         return null;
      } else {
         Supplier<SkinTextures> supp = this.skinCache.computeIfAbsent(entry.displayName(), (name) -> {
            UUID uuid = entry.uuid() != null ? entry.uuid() : UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
            return this.client.getSkinProvider().supplySkinTextures(new GameProfile(uuid, name), true);
         });
         return supp != null ? supp.get() : null;
      }
   }

   private void loginWithSelectedAccount() {
      if (this.selectedAccount != null && !this.authenticating) {
         if (this.selectedAccount.credential() != null && !this.selectedAccount.credential().isBlank()) {
            this.authenticateFromCredential(this.selectedAccount.credential(), this.selectedAccount.displayName());
         } else if (this.selectedAccount.path() != null && this.selectedAccount.path().getFileName().toString().endsWith(".txt")) {
            this.authenticateFromFile(this.selectedAccount.path());
         } else {
            this.loginOffline(this.selectedAccount);
         }
      }
   }

   private void authenticateFromCredential(String credential, String name) {
      this.setAuthenticating(true);
      this.setStatus("§eАвторизация [" + name + "]...");
      MicrosoftOAuth.loginFromStoredCredential(credential).whenComplete((login, error) -> {
         this.setAuthenticating(false);
         if (error != null) {
            System.err.println("[AltsMod] Login error: " + MicrosoftOAuth.friendlyMessage(error));
            error.printStackTrace();
            this.setStatus("§cОшибка входа: " + MicrosoftOAuth.friendlyMessage(error));
         } else if (login != null) {
            this.applyLoginResult(login);
         }
      });
   }

   private void selectFileAndLogin() {
      if (!this.authenticating) {
         this.setStatus("§eВыбор файла...");
         NativeTextFilePicker.chooseCredentialFile().whenComplete((optPath, error) -> {
            if (error != null) {
               this.setStatus("§cОшибка: " + error.getMessage());
            } else if (optPath == null || optPath.isEmpty()) {
               this.setStatus("§7Выбор файла отменен.");
            } else {
               this.authenticateFromFile((Path)optPath.get());
            }
         });
      }
   }

   private void authenticateFromFile(Path path) {
      this.setAuthenticating(true);
      this.setStatus("§eАвторизация [" + String.valueOf(path.getFileName()) + "]...");
      MicrosoftOAuth.loginFromCredentialFile(path).whenComplete((login, error) -> {
         this.setAuthenticating(false);
         if (error != null) {
            System.err.println("[AltsMod] Login error: " + MicrosoftOAuth.friendlyMessage(error));
            error.printStackTrace();
            this.setStatus("§cОшибка входа: " + MicrosoftOAuth.friendlyMessage(error));
         } else if (login != null) {
            this.applyLoginResult(login);
         }
      });
   }

   private void loginViaBrowser() {
      if (!this.authenticating) {
         this.setAuthenticating(true);
         this.setStatus("§eОжидание входа в браузере...");
         MicrosoftOAuth.loginInBrowser().whenComplete((login, error) -> {
            this.setAuthenticating(false);
            if (error != null) {
               System.err.println("[AltsMod] Browser login error: " + MicrosoftOAuth.friendlyMessage(error));
               error.printStackTrace();
               this.setStatus("§cОшибка входа: " + MicrosoftOAuth.friendlyMessage(error));
            } else if (login != null) {
               this.applyLoginResult(login);
            }
         });
      }
   }

   private void openAltsFolder() {
      CompletableFuture.runAsync(() -> {
         try {
            if (this.client == null) {
               return;
            }

            Path altsDir = AccountStorage.getAltsDir(this.client);
            (new ProcessBuilder(new String[]{"explorer.exe", altsDir.toAbsolutePath().toString()})).start();
         } catch (Throwable e) {
            this.setStatus("§cНе удалось открыть папку: " + e.getMessage());
         }
      });
   }

   private void applyLoginResult(MicrosoftOAuth.LoginResult login) {
      MinecraftClient mc = this.client;
      if (mc != null) {
         mc.execute(() -> {
            try {
               Session session = new Session(login.username(), login.uuid(), login.minecraftAccessToken(), toOptional(login.xuid()), toOptional(login.clientId()));
               SessionHelper.applySession(mc, session);
               AccountStorage.saveAccount(mc, login.username(), login.uuid(), login.refreshToken());
               this.loadAccounts();

               for (AccountEntry a : this.accounts) {
                  if (a.name().equalsIgnoreCase(login.username())) {
                     this.selectedAccount = a;
                     break;
                  }
               }

               this.setStatus("§aУспешный вход! Ник: §f" + login.username());
               PrintStream var10000 = System.out;
               String var10001 = login.username();
               var10000.println("[AltsMod] Logged in as: " + var10001 + " (" + String.valueOf(login.uuid()) + ")");
               this.updateLoginButton();
            } catch (Throwable e) {
               System.err.println("[AltsMod] Session apply error: " + e.getMessage());
               e.printStackTrace();
               this.setStatus("§cОшибка применения сессии: " + e.getMessage());
            }
         });
      }
   }

   private static Optional<String> toOptional(String value) {
      return value != null && !value.isBlank() ? Optional.of(value) : Optional.empty();
   }

   private void setStatus(String message) {
      this.status = message;
   }

   private void setAuthenticating(boolean state) {
      this.authenticating = state;
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      int listX = 20;
      int listWidth = 190;
      int listY = 56;
      int listHeight = this.height - 76;
      if (click.x() >= (double)listX && click.x() <= (double)(listX + listWidth) && click.y() >= (double)listY && click.y() <= (double)(listY + listHeight)) {
         int relativeY = (int)(click.y() - (double)listY + (double)this.scrollOffset);
         int index = relativeY / 24;
         if (index >= 0 && index < this.filteredAccounts.size()) {
            this.selectedAccount = this.filteredAccounts.get(index);
            this.setStatus("§7Выбран: §e" + this.selectedAccount.displayName());
            this.updateLoginButton();
            if (doubled) {
               this.loginWithSelectedAccount();
            }

            return true;
         }
      }

      return super.mouseClicked(click, doubled);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      int listX = 20;
      int listWidth = 190;
      int listY = 56;
      int listHeight = this.height - 76;
      if (mouseX >= (double)listX && mouseX <= (double)(listX + listWidth) && mouseY >= (double)listY && mouseY <= (double)(listY + listHeight)) {
         int maxScroll = Math.max(0, this.filteredAccounts.size() * 24 - listHeight);
         this.scrollOffset = Math.max(0, Math.min(maxScroll, (int)((double)this.scrollOffset - verticalAmount * 18.0)));
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   public void close() {
      if (this.client != null) {
         this.client.setScreen(this.parent);
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      context.fill(0, 0, this.width, this.height, -805306368);
      int listX = 20;
      int listWidth = 190;
      int listY = 56;
      int listHeight = this.height - 76;
      context.drawText(this.textRenderer, "§fСписок аккаунтов (§7" + this.filteredAccounts.size() + "§f)", listX, 18, -1, true);
      context.fill(listX, listY, listX + listWidth, listY + listHeight, -2146430960);
      context.fill(listX - 1, listY - 1, listX + listWidth + 1, listY, -13421773);
      context.fill(listX - 1, listY + listHeight, listX + listWidth + 1, listY + listHeight + 1, -13421773);
      context.fill(listX - 1, listY, listX, listY + listHeight, -13421773);
      context.fill(listX + listWidth, listY, listX + listWidth + 1, listY + listHeight, -13421773);
      String currentNick = this.client != null && this.client.getSession() != null ? this.client.getSession().getUsername() : "";

      for (int i = 0; i < this.filteredAccounts.size(); ++i) {
         int itemY = listY + i * 24 - this.scrollOffset;
         if (itemY + 24 >= listY && itemY <= listY + listHeight) {
            AccountEntry entry = this.filteredAccounts.get(i);
            boolean isSelected = entry == this.selectedAccount;
            boolean isHovered = mouseX >= listX && mouseX <= listX + listWidth && mouseY >= Math.max(listY, itemY) && mouseY <= Math.min(listY + listHeight, itemY + 24);
            int drawHeight = Math.min(24, listY + listHeight - itemY);
            if (drawHeight > 0) {
               if (isSelected) {
                  context.fill(listX + 1, itemY, listX + listWidth - 1, itemY + drawHeight, -2145033237);
               } else if (isHovered) {
                  context.fill(listX + 1, itemY, listX + listWidth - 1, itemY + drawHeight, 721420287);
               }

               try {
                  SkinTextures skin = this.getSkin(entry);
                  if (skin != null) {
                     PlayerSkinDrawer.draw(context, skin, listX + 4, itemY + 4, 16);
                  }
               } catch (Throwable var31) {
               }

               boolean isCurrentActive = entry.displayName().equalsIgnoreCase(currentNick);
               String nameText = (isCurrentActive ? "§a✔ " : (isSelected ? "§b" : "§f")) + entry.displayName();
               context.drawText(this.textRenderer, nameText, listX + 24, itemY + 8, -1, true);
            }
         }
      }

      int contentCenterX = (listX + listWidth + 20 + this.width) / 2;
      String prefix = "Made by ";
      String brand = "PremiumAlts";
      int prefixW = this.textRenderer.getWidth(prefix);
      int brandW = this.textRenderer.getWidth(brand);
      int fullW = prefixW + brandW;
      context.getMatrices().pushMatrix();
      float scale = 1.35F;
      context.getMatrices().translate((float)contentCenterX, 18.0F);
      context.getMatrices().scale(scale, scale);
      int baseX = -fullW / 2;
      context.drawText(this.textRenderer, prefix, baseX, 0, -1, true);
      long time = System.currentTimeMillis();
      int brandStartX = baseX + prefixW;

      for (int i = 0; i < brand.length(); ++i) {
         char ch = brand.charAt(i);
         int charOffset = this.textRenderer.getWidth(brand.substring(0, i));
         double wave = Math.sin((double)time * 0.004 + (double)i * 0.4);
         float factor = (float)(0.5 + 0.5 * wave);
         int r = 255;
         int g = (int)(165.0F + 77.0F * factor);
         int b = (int)(0.0F + 117.0F * factor);
         int goldColor = -16777216 | r << 16 | g << 8 | b;
         context.drawText(this.textRenderer, String.valueOf(ch), brandStartX + charOffset, 0, goldColor, true);
      }

      context.getMatrices().popMatrix();
      String activeText = "§7Текущий аккаунт: §a" + (currentNick.isEmpty() ? "Offline" : currentNick);
      context.drawText(this.textRenderer, activeText, contentCenterX - this.textRenderer.getWidth(activeText) / 2, 40, -1, true);
      String var10000 = this.selectedAccount != null ? this.selectedAccount.displayName() : "не выбран";
      String selText = "§7Выбран: §e" + var10000;
      context.drawText(this.textRenderer, selText, contentCenterX - this.textRenderer.getWidth(selText) / 2, 52, -1, true);
      if (this.status != null) {
         context.drawText(this.textRenderer, this.status, contentCenterX - this.textRenderer.getWidth(this.status) / 2, this.height - 24, -1, true);
      }

      super.render(context, mouseX, mouseY, deltaTicks);
   }
}

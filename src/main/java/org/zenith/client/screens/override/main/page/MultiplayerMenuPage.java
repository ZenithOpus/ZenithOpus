package org.zenith.client.screens.override.main.page;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.network.ServerInfo.Status;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MultiplayerMenuPage implements MainMenuPage, MinecraftAccess {
   private static final float WIDTH = MenuTheme.px(431.0F);
   private static final float LIST_HEIGHT = MenuTheme.px(342.0F);
   private static final float CONTROLS_GAP = MenuTheme.px(48.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(62.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float ROW_RADIUS = MenuTheme.px(12.0F);
   private static final float ROW_PADDING = MenuTheme.px(16.0F);
   private static final float ICON_SIZE = MenuTheme.px(30.0F);
   private static final float ICON_RADIUS = MenuTheme.px(6.0F);
   private static final float RAIL_GAP = MenuTheme.px(16.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float CONTROL_GAP = MenuTheme.px(8.0F);
   private static final ThreadPoolExecutor PINGER = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
   private final Screen parent;
   private final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
   private final List<MultiplayerMenuPage.Row> rows = new ArrayList<>();
   private final MenuScroll scroll = new MenuScroll();
   private final MenuTextField nameField = new MenuTextField(MenuIcons.SERVER, MenuTheme.t("menu.multiplayer.name"));
   private final MenuTextField addressField = new MenuTextField(MenuIcons.ADDRESS, MenuTheme.t("menu.multiplayer.address"));
   private final MenuButton addButton = MenuButton.iconOnly(MenuIcons.ADD, MenuButton.Style.ACCENT);
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private ServerList serverList;
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

   public MultiplayerMenuPage(Screen var1) {
      this.parent = var1;
      this.addButton.onClick(this::addServer);
      this.nameField.setOnEnter(this::addServer);
      this.addressField.setOnEnter(this::addServer);
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return LIST_HEIGHT + CONTROLS_GAP + MenuTextField.HEIGHT;
   }

   @Override
   public void onShow() {
      ensureForeignMenuHooks();
      this.reload();
   }

   private static void ensureForeignMenuHooks() {
      try {
         Class var0 = Class.forName("ru.fiw.proxyserver.ProxyServer");
         Field var1 = var0.getField("proxyMenuButton");
         if (var1.get(null) == null) {
            var1.set(null, ButtonWidget.builder(Text.empty(), var0x -> {}).dimensions(0, 0, 0, 0).build());
         }
      } catch (Throwable var2) {
      }
   }

   @Override
   public void onHide() {
      this.pinger.cancel();
      this.closeRows();
   }

   @Override
   public void tick() {
      this.pinger.tick();
   }

   private void closeRows() {
      for (MultiplayerMenuPage.Row var2 : this.rows) {
         var2.icon.close();
      }

      this.rows.clear();
   }

   private void reload() {
      this.entrance.method2809(0.0F);
      this.closeRows();
      this.serverList = new ServerList(mc);
      this.serverList.loadFile();

      for (int var1 = 0; var1 < this.serverList.size(); var1++) {
         MultiplayerMenuPage.Row var2 = new MultiplayerMenuPage.Row(this.serverList.get(var1));
         this.rows.add(var2);
         var2.ping();
      }

      this.scroll.reset();
   }

   private void addServer() {
      String var1 = this.nameField.getValue().trim();
      String var2 = this.addressField.getValue().trim();
      if (!var2.isEmpty()) {
         if (var1.isEmpty()) {
            var1 = var2;
         }

         if (this.serverList == null) {
            this.reload();
         }

         ServerInfo var3 = new ServerInfo(var1, var2, ServerType.OTHER);
         this.serverList.add(var3, false);
         this.serverList.saveFile();
         this.nameField.clear();
         this.addressField.clear();
         this.nameField.setFocused(false);
         this.addressField.setFocused(false);
         this.reload();
      }
   }

   private void removeServer(MultiplayerMenuPage.Row var1) {
      if (this.serverList != null) {
         this.serverList.remove(var1.info);
         this.serverList.saveFile();
         this.reload();
      }
   }

   private void joinServer(MultiplayerMenuPage.Row var1) {
      ConnectScreen.connect(this.parent, mc, ServerAddress.parse(var1.info.address), var1.info, false, null);
   }

   @Override
   public void layout(BoundsData var1) {
      float var2 = var1.getFloatValue2() + LIST_HEIGHT + CONTROLS_GAP;
      float var3 = (var1.width() - MenuButton.HEIGHT - CONTROL_GAP * 2.0F) / 2.0F;
      this.nameField.setBounds(var1.getFloatValue(), var2, var3, MenuTextField.HEIGHT);
      this.addressField.setBounds(var1.getFloatValue() + var3 + CONTROL_GAP, var2, var3, MenuTextField.HEIGHT);
      this.addButton.setBounds(var1.getFloatValue() + var1.width() - MenuButton.HEIGHT, var2, MenuButton.HEIGHT, MenuButton.HEIGHT);
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
      BoundsData var5 = MenuSkin.listArea(var1, var4, LIST_HEIGHT);
      float var6 = var5.width() - RAIL_GAP - RAIL_WIDTH;
      float var7 = this.rows.isEmpty() ? 0.0F : this.rows.size() * ROW_HEIGHT + (this.rows.size() - 1) * ROW_GAP;
      this.scroll.update(var5.height(), var7);
      this.viewport = new BoundsData(var5.getFloatValue(), var5.getFloatValue2(), var6, var5.height());
      float var8 = this.entrance.method2808(1.0F);
      var1.enableScissor(var5.getFloatValue(), var5.getFloatValue2(), var5.getFloatValue() + var6, var5.getFloatValue2() + var5.height());
      float var9 = var5.getFloatValue2() - this.scroll.offset();
      int var10 = 0;

      for (MultiplayerMenuPage.Row var12 : this.rows) {
         var12.bounds = new BoundsData(var5.getFloatValue(), var9, var6, ROW_HEIGHT);
         if (var9 + ROW_HEIGHT >= var5.getFloatValue2() && var9 <= var5.getFloatValue2() + var5.height()) {
            var12.render(var1, var2, var3, var5.getFloatValue(), var9 + MenuSkin.enterOffset(var8, var10), var6);
         }

         var9 += ROW_HEIGHT + ROW_GAP;
         var10++;
      }

      var1.disableScissor();
      if (this.rows.isEmpty()) {
         Font var13 = MenuTheme.medium(12.0F);
         String var14 = MenuTheme.t("menu.multiplayer.empty");
         var1.drawText(
            var13,
            var14,
            var5.getFloatValue() + (var6 - var13.width(var14)) / 2.0F,
            var5.getFloatValue2() + (var5.height() - var13.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRail(var1, var5.getFloatValue() + var5.width() - RAIL_WIDTH, var5.getFloatValue2(), var5.height());
      this.nameField.render(var1, var2, var3);
      this.addressField.render(var1, var2, var3);
      this.addButton.render(var1, var2, var3);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.nameField.mouseClicked(var1, var3, var5) | this.addressField.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.addButton.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (var5 == 0 && this.viewport.contains(var1, var3)) {
         for (MultiplayerMenuPage.Row var7 : this.rows) {
            if (var7.playBounds.contains(var1, var3)) {
               this.joinServer(var7);
               return true;
            }

            if (var7.removeBounds.contains(var1, var3)) {
               this.removeServer(var7);
               return true;
            }

            if (var7.bounds.contains(var1, var3)) {
               this.joinServer(var7);
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
      return this.nameField.keyPressed(var1, var2, var3) || this.addressField.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.nameField.charTyped(var1, var2) || this.addressField.charTyped(var1, var2);
   }

   private final class Row {
      private final ServerInfo info;
      private final WorldIcon icon;
      private byte[] lastFavicon;
      private BoundsData bounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData removeBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData playBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

      private Row(ServerInfo var2) {
         this.info = var2;
         this.icon = WorldIcon.forServer(MinecraftAccess.mc.getTextureManager(), var2.address);
      }

      private void ping() {
         this.info.setStatus(Status.PINGING);
         MultiplayerMenuPage.PINGER.submit(() -> {
            try {
               MultiplayerMenuPage.this.pinger.add(this.info, () -> MinecraftAccess.mc.execute(this::syncFavicon), () -> {}, NetworkingBackend.remote(false));
            } catch (Exception var2) {
               this.info.setStatus(Status.UNREACHABLE);
            }
         });
      }

      private void syncFavicon() {
         byte[] var1 = this.info.getFavicon();
         if (var1 != null && !Arrays.equals(var1, this.lastFavicon)) {
            try {
               this.icon.load(NativeImage.read(var1));
               this.lastFavicon = var1;
            } catch (Exception var3) {
            }
         }
      }

      private void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
         this.syncFavicon();
         boolean var7 = this.bounds.contains(var2, var3);
         MenuSkin.plate(
            var1, var4, var5, var6, MultiplayerMenuPage.ROW_HEIGHT, var7 ? MenuTheme.plate().method7295(MenuTheme.track(), 0.5F) : MenuTheme.plate()
         );
         Identifier var8 = this.icon.getTextureId();
         float var9 = var5 + (MultiplayerMenuPage.ROW_HEIGHT - MultiplayerMenuPage.ICON_SIZE) / 2.0F;
         RenderShaders.method5294(
            MatrixUtils.method664(var1.getMatrices()),
            var8,
            var4 + MenuSkin.EDGE,
            var9,
            MultiplayerMenuPage.ICON_SIZE,
            MultiplayerMenuPage.ICON_SIZE,
            ProxyDotRadiusData.method8069(MenuSkin.tileRadius(MultiplayerMenuPage.ICON_SIZE)),
            ZenithColor.INSTANCE
         );
         float var10 = MenuSkin.divider(var1, var4 + MenuSkin.EDGE + MultiplayerMenuPage.ICON_SIZE, var5, MultiplayerMenuPage.ROW_HEIGHT);
         Font var11 = MenuTheme.medium(11.0F);
         Font var12 = MenuTheme.regular(10.0F);
         float var13 = MenuTheme.px(30.0F);
         float var14 = var5 + (MultiplayerMenuPage.ROW_HEIGHT - var13) / 2.0F;
         var1.drawText(var11, this.info.name, var10, var14, MenuTheme.text());
         var1.drawText(var12, this.info.address, var10, var14 + MenuTheme.px(18.0F), MenuTheme.textTertiary());
         float var15 = var4 + var6 - MenuSkin.EDGE;
         float var16 = MenuTheme.px(12.0F);
         float var17 = var5 + (MultiplayerMenuPage.ROW_HEIGHT - var16) / 2.0F;
         this.playBounds = new BoundsData(var15 - var16, var17, var16, var16);
         this.removeBounds = new BoundsData(var15 - var16 * 2.0F - MenuTheme.px(4.0F), var17, var16, var16);
         MenuSkin.valuePill(var1, this.removeBounds.getFloatValue() - MenuSkin.GAP * 2.0F, var5, MultiplayerMenuPage.ROW_HEIGHT, this.playerCount());
         Font var18 = MenuIcons.REMOVE.font(12.0F);
         var1.drawText(
            var18,
            MenuIcons.REMOVE.glyph(),
            this.removeBounds.getFloatValue(),
            var17 + (var16 - var18.height()) / 2.0F,
            this.removeBounds.contains(var2, var3) ? MenuTheme.danger() : MenuTheme.textTertiary()
         );
         Font var19 = MenuIcons.PLAY.font(12.0F);
         var1.drawText(var19, MenuIcons.PLAY.glyph(), this.playBounds.getFloatValue(), var17 + (var16 - var19.height()) / 2.0F, MenuTheme.accent());
      }

      private String playerCount() {
         if (this.info.playerCountLabel != null) {
            String var1 = this.info.playerCountLabel.getString();
            if (!var1.isBlank()) {
               return var1;
            }
         }

         return this.info.getStatus() == Status.PINGING ? "..." : "-";
      }
   }
}

package org.zenith.client.screens.override.main.page;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorage.LevelList;
import net.minecraft.world.level.storage.LevelStorage.Session;
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
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class SingleplayerMenuPage implements MainMenuPage, MinecraftAccess {
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
   private static final DateTimeFormatter LAST_PLAYED = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.systemDefault());
   private final Screen parent;
   private final List<SingleplayerMenuPage.Row> rows = new ArrayList<>();
   private final MenuScroll scroll = new MenuScroll();
   private final MenuButton createButton = MenuButton.accent(MenuIcons.ADD, MenuTheme.t("menu.singleplayer.create"));
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private boolean loading;
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private int loadGeneration;

   public SingleplayerMenuPage(Screen var1) {
      this.parent = var1;
      this.createButton.onClick(this::createWorld);
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return LIST_HEIGHT + CONTROLS_GAP + MenuButton.HEIGHT;
   }

   @Override
   public void onShow() {
      this.reload();
   }

   @Override
   public void onHide() {
      this.loadGeneration++;
      this.loading = false;
      this.closeRows();
   }

   private void closeRows() {
      for (SingleplayerMenuPage.Row var2 : this.rows) {
         var2.icon.close();
      }

      this.rows.clear();
   }

   private void reload() {
      this.closeRows();
      this.scroll.reset();
      this.loading = true;
      int var1 = ++this.loadGeneration;
      LevelStorage var2 = mc.getLevelStorage();

      try {
         LevelList var3 = var2.getLevelList();
         var2.loadSummaries(var3).thenAcceptAsync(var2x -> {
            if (var1 == this.loadGeneration) {
               this.closeRows();

               for (LevelSummary var4x : var2x) {
                  if (!var4x.isUnavailable()) {
                     this.rows.add(new SingleplayerMenuPage.Row(var4x));
                  }
               }

               this.loading = false;
            }
         }, mc);
      } catch (Exception var4) {
         this.loading = false;
      }
   }

   private void createWorld() {
      CreateWorldScreen.show(mc, () -> mc.setScreen(this.parent));
   }

   private void play(SingleplayerMenuPage.Row var1) {
      if (!var1.summary.isLocked() && !var1.summary.requiresConversion()) {
         mc.createIntegratedServerLoader().start(var1.summary.getName(), () -> mc.setScreen(this.parent));
      }
   }

   private void confirmDelete(SingleplayerMenuPage.Row var1) {
      Screen var2 = mc.currentScreen;
      mc.setScreen(
         new ConfirmScreen(
            var3 -> {
               if (var3) {
                  this.delete(var1);
               }

               mc.setScreen(var2);
            },
            Text.translatable("selectWorld.deleteQuestion"),
            Text.translatable("selectWorld.deleteWarning", var1.summary.getDisplayName()),
            Text.translatable("selectWorld.deleteButton"),
            ScreenTexts.CANCEL
         )
      );
   }

   private void delete(SingleplayerMenuPage.Row var1) {
      try (Session var2 = mc.getLevelStorage().createSession(var1.summary.getName())) {
         var2.deleteSessionLock();
      } catch (Exception var7) {
      }

      this.reload();
   }

   @Override
   public void layout(BoundsData var1) {
      float var2 = var1.getFloatValue2() + LIST_HEIGHT + CONTROLS_GAP;
      float var3 = this.createButton.preferredWidth();
      this.createButton.setBounds(var1.getFloatValue() + (var1.width() - var3) / 2.0F, var2, var3, MenuButton.HEIGHT);
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

      for (SingleplayerMenuPage.Row var12 : this.rows) {
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
         String var14 = MenuTheme.t(this.loading ? "menu.singleplayer.loading" : "menu.singleplayer.empty");
         var1.drawText(
            var13,
            var14,
            var5.getFloatValue() + (var6 - var13.width(var14)) / 2.0F,
            var5.getFloatValue2() + (var5.height() - var13.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRail(var1, var5.getFloatValue() + var5.width() - RAIL_WIDTH, var5.getFloatValue2(), var5.height());
      this.createButton.render(var1, var2, var3);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.createButton.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (var5 == 0 && this.viewport.contains(var1, var3)) {
         for (SingleplayerMenuPage.Row var7 : this.rows) {
            if (var7.playBounds.contains(var1, var3)) {
               this.play(var7);
               return true;
            }

            if (var7.removeBounds.contains(var1, var3)) {
               this.confirmDelete(var7);
               return true;
            }

            if (var7.bounds.contains(var1, var3)) {
               this.play(var7);
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

   private final class Row {
      private final LevelSummary summary;
      private final WorldIcon icon;
      private BoundsData bounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData removeBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData playBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

      private Row(LevelSummary var2) {
         this.summary = var2;
         this.icon = WorldIcon.forWorld(MinecraftAccess.mc.getTextureManager(), var2.getName());
         this.loadIcon();
      }

      private void loadIcon() {
         Path var1 = this.summary.getIconPath();
         if (var1 != null && Files.isRegularFile(var1)) {
            try (InputStream var2 = Files.newInputStream(var1)) {
               this.icon.load(NativeImage.read(var2));
            } catch (Exception var7) {
            }
         }
      }

      private void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
         boolean var7 = this.bounds.contains(var2, var3);
         MenuSkin.plate(
            var1, var4, var5, var6, SingleplayerMenuPage.ROW_HEIGHT, var7 ? MenuTheme.plate().method7295(MenuTheme.track(), 0.5F) : MenuTheme.plate()
         );
         Identifier var8 = this.icon.getTextureId();
         float var9 = var5 + (SingleplayerMenuPage.ROW_HEIGHT - SingleplayerMenuPage.ICON_SIZE) / 2.0F;
         RenderShaders.method5294(
            MatrixUtils.method664(var1.getMatrices()),
            var8,
            var4 + MenuSkin.EDGE,
            var9,
            SingleplayerMenuPage.ICON_SIZE,
            SingleplayerMenuPage.ICON_SIZE,
            ProxyDotRadiusData.method8069(MenuSkin.tileRadius(SingleplayerMenuPage.ICON_SIZE)),
            ZenithColor.INSTANCE
         );
         float var10 = MenuSkin.divider(var1, var4 + MenuSkin.EDGE + SingleplayerMenuPage.ICON_SIZE, var5, SingleplayerMenuPage.ROW_HEIGHT);
         Font var11 = MenuTheme.medium(11.0F);
         Font var12 = MenuTheme.regular(10.0F);
         float var13 = MenuTheme.px(30.0F);
         float var14 = var5 + (SingleplayerMenuPage.ROW_HEIGHT - var13) / 2.0F;
         var1.drawText(var11, this.summary.getDisplayName(), var10, var14, MenuTheme.text());
         var1.drawText(var12, this.details(), var10, var14 + MenuTheme.px(18.0F), MenuTheme.textTertiary());
         float var15 = var4 + var6 - MenuSkin.EDGE;
         float var16 = MenuTheme.px(12.0F);
         float var17 = var5 + (SingleplayerMenuPage.ROW_HEIGHT - var16) / 2.0F;
         this.playBounds = new BoundsData(var15 - var16, var17, var16, var16);
         this.removeBounds = new BoundsData(var15 - var16 * 2.0F - MenuTheme.px(4.0F), var17, var16, var16);
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

      private String details() {
         String var1 = this.summary.getGameMode().getTranslatableName().getString();
         String var2 = this.summary.getLastPlayed() <= 0L ? "-" : SingleplayerMenuPage.LAST_PLAYED.format(Instant.ofEpochMilli(this.summary.getLastPlayed()));
         return var1 + " - " + var2;
      }
   }
}

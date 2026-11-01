package org.zenith.client.screens.override.main.page;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.utility.api.PlayTimeTracker;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class HomeMenuPage implements MainMenuPage {
   private static final float WIDTH = MenuTheme.px(402.0F);
   private static final float HEADER_HEIGHT = MenuTheme.px(42.0F);
   private static final float HEADER_GAP = MenuTheme.px(8.0F);
   private static final float LIST_HEIGHT = MenuTheme.px(240.0F);
   private static final float CARD_RADIUS = MenuTheme.px(12.0F);
   private static final float CARD_PADDING_X = MenuTheme.px(16.0F);
   private static final float CARD_PADDING_Y = MenuTheme.px(14.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(14.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float MARKER_BOX = MenuTheme.px(14.0F);
   private static final float MARKER_GAP = MenuTheme.px(4.0F);
   private static final float RAIL_GAP = MenuTheme.px(8.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float FOOTER_WIDTH = MenuTheme.px(360.0F);
   private static final float FOOTER_HEIGHT = MenuTheme.px(48.0F);
   private static final float STAT_WIDTH = MenuTheme.px(54.0F);
   private static final float STAT_GAP = MenuTheme.px(48.0F);
   private static List<HomeMenuPage.Entry> changelog;
   private final Animation expansion = new Animation(240L, 1.0F, Easing.INSTANCE7);
   private final MenuScroll scroll = new MenuScroll();
   private boolean expanded = true;
   private BoundsData headerBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private String subscriptionLabel = "-";
   private long subscriptionRevision = Long.MIN_VALUE;
   private long subscriptionCheckedAt;

   private static List<HomeMenuPage.Entry> changelog() {
      if (changelog != null) {
         return changelog;
      } else {
         ArrayList var0 = new ArrayList();

         try {
            var var1 = MinecraftClient.getInstance().getResourceManager().getResource(Zenith.method1501("menu/changelog.json"));
            if (var1.isPresent()) {
               try (InputStreamReader var2 = new InputStreamReader(((Resource)var1.get()).getInputStream(), StandardCharsets.UTF_8)) {
                  JsonObject var3 = JsonParser.parseReader(var2).getAsJsonObject();

                  for (JsonElement var5 : var3.getAsJsonArray("entries")) {
                     JsonObject var6 = var5.getAsJsonObject();
                     var0.add(new HomeMenuPage.Entry(var6.get("text").getAsString(), var6.has("added") && var6.get("added").getAsBoolean()));
                  }
               }
            }
         } catch (Exception var9) {
         }

         changelog = var0;
         return changelog;
      }
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return HEADER_HEIGHT + (HEADER_GAP + LIST_HEIGHT) * this.expansion.getValue();
   }

   @Override
   public float footerWidth() {
      return FOOTER_WIDTH;
   }

   @Override
   public float footerHeight() {
      return FOOTER_HEIGHT;
   }

   @Override
   public void update() {
      this.expansion.method2808(this.expanded ? 1.0F : 0.0F);
   }

   @Override
   public void layout(BoundsData var1) {
      this.headerBounds = new BoundsData(var1.getFloatValue(), var1.getFloatValue2(), var1.width(), HEADER_HEIGHT);
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
      drawCard(var1, var4.getFloatValue(), var4.getFloatValue2(), var4.width(), HEADER_HEIGHT);
      this.drawHeader(var1, var4, var2, var3);
      float var5 = var4.height() - HEADER_HEIGHT - HEADER_GAP;
      if (!(var5 <= 1.0F)) {
         float var6 = var4.getFloatValue2() + HEADER_HEIGHT + HEADER_GAP;
         drawCard(var1, var4.getFloatValue(), var6, var4.width(), var5);
         this.drawChangelog(var1, var4.getFloatValue(), var6, var4.width(), var5);
      }
   }

   private void drawHeader(MenuDrawContext var1, BoundsData var2, float var3, float var4) {
      MenuSkin.header(var1, var2.getFloatValue(), var2.getFloatValue2(), MenuIcons.REFRESH, MenuTheme.t("menu.home.version") + " 3.5");
      MenuIcon var5 = this.expanded ? MenuIcons.CHEVRON_UP : MenuIcons.CHEVRON_DOWN;
      Font var6 = var5.font(12.0F);
      boolean var7 = this.headerBounds.contains(var3, var4);
      var1.drawText(
         var6,
         var5.glyph(),
         var2.getFloatValue() + var2.width() - MenuSkin.EDGE - var5.width(12.0F),
         var2.getFloatValue2() + (HEADER_HEIGHT - var6.height()) / 2.0F,
         var7 ? MenuTheme.text() : MenuTheme.textTertiary()
      );
   }

   private void drawChangelog(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      float var6 = var2 + CARD_PADDING_X;
      float var7 = var3 + CARD_PADDING_Y;
      float var8 = var4 - CARD_PADDING_X * 2.0F - RAIL_GAP - RAIL_WIDTH;
      float var9 = var5 - CARD_PADDING_Y * 2.0F;
      List<HomeMenuPage.Entry> var10 = changelog();
      float var11 = var10.isEmpty() ? 0.0F : var10.size() * ROW_HEIGHT + (var10.size() - 1) * ROW_GAP;
      this.scroll.update(var9, var11);
      var1.enableScissor(var6, var7, var6 + var8, var7 + var9);
      Font var12 = MenuTheme.regular(11.0F);
      float var13 = var7 - this.scroll.offset();

      for (Object var15_o : (Iterable<?>)(Object)(var10)) {
         HomeMenuPage.Entry var15 = (HomeMenuPage.Entry)var15_o;
         MenuIcon var16 = var15.added() ? MenuIcons.PLUS : MenuIcons.MINUS;
         Font var17 = var16.font(11.0F);
         ZenithColor var18 = var15.added() ? MenuTheme.accent() : MenuTheme.textTertiary();
         ZenithColor var19 = var15.added() ? MenuTheme.text() : MenuTheme.textSecondary();
         var1.drawText(var17, var16.glyph(), var6 + (MARKER_BOX - var16.width(11.0F)) / 2.0F, var13 + (ROW_HEIGHT - var17.height()) / 2.0F, var18);
         var1.drawText(var12, MenuTheme.t(var15.text()), var6 + MARKER_BOX + MARKER_GAP, var13 + (ROW_HEIGHT - var12.height()) / 2.0F, var19);
         var13 += ROW_HEIGHT + ROW_GAP;
      }

      var1.disableScissor();
      this.scroll.renderRail(var1, var2 + var4 - CARD_PADDING_X - RAIL_WIDTH, var7, var9);
   }

   @Override
   public void renderFooter(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
      Zenith.Entry var5 = Zenith.getInstance().getEntry();
      List<HomeMenuPage.Stat> var6 = List.of(
         new HomeMenuPage.Stat(MenuIcons.SUBSCRIPTION, this.subscriptionDays(var5), MenuTheme.t("menu.home.subscription")),
         new HomeMenuPage.Stat(MenuIcons.UID, var5.getText2() == null ? "-" : var5.getText2(), MenuTheme.t("menu.home.uid")),
         new HomeMenuPage.Stat(MenuIcons.PLAYTIME, playTime(), MenuTheme.t("menu.home.playtime")),
         new HomeMenuPage.Stat(MenuIcons.ONLINE, onlineFriends(), MenuTheme.t("menu.home.online"))
      );
      float var7 = var4.getFloatValue();

      for (Object var9_o : (Iterable<?>)(Object)(var6)) {
         HomeMenuPage.Stat var9 = (HomeMenuPage.Stat)var9_o;
         this.drawStat(var1, var9, var7, var4.getFloatValue2());
         var7 += STAT_WIDTH + STAT_GAP;
      }
   }

   private void drawStat(MenuDrawContext var1, HomeMenuPage.Stat var2, float var3, float var4) {
      Font var5 = var2.icon().font(16.0F);
      Font var6 = MenuTheme.medium(10.0F);
      Font var7 = MenuTheme.regular(9.0F);
      var1.drawText(
         var5,
         var2.icon().glyph(),
         var3 + (STAT_WIDTH - var2.icon().width(16.0F)) / 2.0F,
         var4 + (MenuTheme.px(16.0F) - var5.height()) / 2.0F,
         MenuTheme.accent()
      );
      float var8 = var4 + MenuTheme.px(24.0F);
      var1.drawText(var6, var2.value(), var3 + (STAT_WIDTH - var6.width(var2.value())) / 2.0F, var8, MenuTheme.text());
      var1.drawText(var7, var2.label(), var3 + (STAT_WIDTH - var7.width(var2.label())) / 2.0F, var8 + MenuTheme.px(14.0F), MenuTheme.textTertiary());
   }

   private static String playTime() {
      PlayTimeTracker var0 = Zenith.getInstance().getPlayTimeTracker();
      return var0 == null ? "0m" : PlayTimeTracker.method1402(var0.method1401());
   }

   private static String onlineFriends() {
      Cloud var0 = Zenith.getInstance().getCloud();
      return var0 != null && var0.method7862() ? String.valueOf(var0.state().method8045()) : "0";
   }

   private String subscriptionDays(Zenith.Entry var1) {
      long var2 = System.currentTimeMillis();
      long var4 = var1.method1510();
      if (var4 == this.subscriptionRevision && var2 - this.subscriptionCheckedAt < 60000L) {
         return this.subscriptionLabel;
      } else {
         this.subscriptionRevision = var4;
         this.subscriptionCheckedAt = var2;
         long var6 = var1.method1506();
         if (var6 == Long.MAX_VALUE) {
            this.subscriptionLabel = MenuTheme.t("gui.sub.lifetime");
         } else if (var6 == -1L) {
            this.subscriptionLabel = MenuTheme.t("gui.sub.active");
         } else {
            this.subscriptionLabel = var6 + " " + MenuTheme.t("menu.home.daysLeft");
         }

         return this.subscriptionLabel;
      }
   }

   private static void drawCard(MenuDrawContext var0, float var1, float var2, float var3, float var4) {
      MenuSkin.panel(var0, var1, var2, var3, var4);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0 && this.headerBounds.contains(var1, var3)) {
         this.expanded = !this.expanded;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.scroll.scroll(var5, ROW_HEIGHT + ROW_GAP);
   }

   private static final class Entry {
      private final String text;
      private final boolean added;

      public String text() {
         return this.text;
      }

      public boolean added() {
         return this.added;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Entry other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.added, other.added);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, added);
      }

      @Override
      public String toString() {
         return "Entry[" + "text=" + this.text + ", " + "added=" + this.added + "]";
      }

      private Entry(String var1, boolean var2) {
         this.text = var1;
         this.added = var2;
      }
   }

   private static final class Stat {
      private final MenuIcon icon;
      private final String value;
      private final String label;

      public MenuIcon icon() {
         return this.icon;
      }

      public String value() {
         return this.value;
      }

      public String label() {
         return this.label;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Stat other)) return false;
         return java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.value, other.value) && java.util.Objects.equals(this.label, other.label);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(icon, value, label);
      }

      @Override
      public String toString() {
         return "Stat[" + "icon=" + this.icon + ", " + "value=" + this.value + ", " + "label=" + this.label + "]";
      }

      private Stat(MenuIcon var1, String var2, String var3) {
         this.icon = var1;
         this.value = var2;
         this.label = var3;
      }
   }
}

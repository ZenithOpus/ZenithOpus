package org.zenith.client.screens.nlgui.panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PlayTimeTracker;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.Server;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class QuickMenuPanel extends Panel {
   private static final float ROW_HEIGHT = 15.0F;
   private static final float CAPTION_HEIGHT = 13.0F;
   private static final float OPTION_HEIGHT = 12.0F;
   private static final float ICON_COLUMN = 10.0F;
   private static final float INSET = 8.0F;
   private static final float ACCOUNT_HEIGHT = 16.0F;
   private static final float ACCOUNT_AVATAR = 16.0F;
   private static final float ACCOUNT_BLOCK = 16.0F + GuiStyle.PADDING.intValue() * 4.0F;
   private static final float TOGGLE_WIDTH = 12.0F;
   private static final float CHEVRON_SIZE = 5.0F;
   private static final float NUMBER_HEIGHT = 24.0F;
   private static final float TRACK_OFFSET = 17.0F;
   private static final float TRACK_GRAB = 5.0F;
   private static final float HANDLE_GRAB = 5.0F;
   private static final float PIP = 2.0F;
   private static final float PIP_GAP = 1.5F;
   private static final float OPTION_INDENT = 6.0F;
   private static final float SCROLL_SPEED = 18.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private static final float MAX_ROW_DELAY = 0.4F;
   private final Animation openAnimation = new Animation(380L, 0.0F, Easing.INSTANCE15);
   private final List<QuickMenuPanel.Row> rows = new ArrayList<>();
   private boolean expanded;
   private QuickMenuPanel.Row openRow;
   private BoundsData bounds;
   private BoundsData contentBounds;
   private BoundsData closeBounds;
   private BoundsData accountBounds;
   private float scroll;
   private float scrollTarget;

   public QuickMenuPanel() {
      this.build();
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private void build() {
      NLMenuScreen var1 = Zenith.getInstance().getNLMenuScreen();
      ProfilePanel var2 = var1.getProfilePanel();
      ClientPanel var3 = var1.getClientPanel();
      SliderSetting var4 = var3.getGuiScale().getSetting();
      SliderSetting var5 = var3.getBlurStrength().getSetting();
      BooleanSetting var6 = var3.getRenderDescription().getSetting();
      BooleanSetting var7 = var3.getRenderIcon().getSetting();
      BooleanSetting var8 = var2.getSteamMode().getSetting();
      BooleanSetting var9 = var2.getDiscordRPC().getSetting();
      QuickMenuPanel.Row var10 = this.mode("quick.language", MenuGlyph.MODE);

      for (String var12 : Zenith.getInstance().getLocalization().method1732()) {
         var10.option(
            () -> languageTitle(var12),
            () -> var12.equalsIgnoreCase(Zenith.getInstance().getLocalization().getLanguageName()),
            () -> Zenith.getInstance().getLocalization().method1728(var12)
         );
      }

      this.number("quick.menuScale", MenuGlyph.SIZE_GLYPH, var4, () -> (int)var4.getValue3() + "%");
      this.number("quick.blur", MenuGlyph.OPACITY, var5, () -> (int)var5.getValue3() == 0 ? t("quick.value.off") : (int)var5.getValue3() + "px");
      this.toggle("quick.description", MenuGlyph.TEXT, var6::isEnabled, var6::toggle);
      this.toggle("quick.icons", MenuGlyph.ITEMS, var7::isEnabled, var7::toggle);
      this.caption("quick.caption.privacy");
      this.toggle("quick.streamer", MenuGlyph.SHIELD, var8::isEnabled, var8::toggle);
      this.toggle("quick.discord", MenuGlyph.NETWORK, var9::isEnabled, var9::toggle);
      this.caption("quick.caption.ai");
      Server var14 = Server.getInstance();
      this.toggle("quick.mcp", MenuGlyph.NETWORK, var14::isRunning, () -> var14.setEnabled(!var14.isRunning()));
      this.action("quick.mcpCopy", MenuGlyph.COPY, () -> t("quick.value.copy"), () -> {
         if (!var14.isRunning()) {
            var14.setEnabled(true);
         }

         MinecraftClient.getInstance().keyboard.setClipboard(var14.method216());
      });
      this.action("quick.mcpToken", MenuGlyph.RESET, () -> t("quick.value.reset"), var14::method218);
      this.action("quick.scriptDocs", MenuGlyph.TEXT, () -> openLink(RegionUtil.method2038().get(0) + "/docs/addons"));
      this.caption("quick.caption.links");
      this.action("quick.webProfile", MenuGlyph.PROFILE, () -> openLink(RegionUtil.method2038().get(0) + "/dashboard"));
      this.action("quick.folder", MenuGlyph.CONFIGS, this::openFolder);
      this.action("quick.devlog", MenuGlyph.CHAT, () -> openLink("https://t.me/zenithdlcdevlog"));
      this.action("quick.chat", MenuGlyph.CHAT, () -> openLink("https://t.me/+J-lV24z-Wmg1M2Y6"));
      this.caption("quick.caption.about");
      QuickMenuPanel.Row var15 = this.submenu("quick.about", MenuGlyph.CLIENT, () -> "3.5");
      var15.info("quick.about.version", () -> "3.5");
      var15.info("quick.about.role", () -> Zenith.getInstance().getEntry().method1504().getName());
      PlayTimeTracker var13 = Zenith.getInstance().getPlayTimeTracker();
      var15.info("quick.playtime.session", () -> PlayTimeTracker.method1402(var13.method1399()));
      var15.info("quick.playtime.week", () -> PlayTimeTracker.method1402(var13.method1400()));
      var15.info("quick.playtime.total", () -> PlayTimeTracker.method1402(var13.method1401()));
   }

   private void caption(String var1) {
      this.rows.add(QuickMenuPanel.Row.caption(var1));
   }

   private void toggle(String var1, MenuGlyph var2, BooleanSupplier var3, Runnable var4) {
      this.rows.add(QuickMenuPanel.Row.toggle(var1, var2, var3, var4));
   }

   private void action(String var1, MenuGlyph var2, Runnable var3) {
      this.rows.add(QuickMenuPanel.Row.action(var1, var2, var3));
   }

   private void action(String var1, MenuGlyph var2, Supplier<String> var3, Runnable var4) {
      this.rows.add(QuickMenuPanel.Row.action(var1, var2, var3, var4));
   }

   private QuickMenuPanel.Row submenu(String var1, MenuGlyph var2, Supplier<String> var3) {
      QuickMenuPanel.Row var4 = QuickMenuPanel.Row.submenu(var1, var2, var3);
      this.rows.add(var4);
      return var4;
   }

   private QuickMenuPanel.Row number(String var1, MenuGlyph var2, SliderSetting var3, Supplier<String> var4) {
      QuickMenuPanel.Row var5 = QuickMenuPanel.Row.number(var1, var2, var3, var4);
      this.rows.add(var5);
      return var5;
   }

   private QuickMenuPanel.Row mode(String var1, MenuGlyph var2) {
      QuickMenuPanel.Row var3 = QuickMenuPanel.Row.mode(var1, var2);
      this.rows.add(var3);
      return var3;
   }

   public boolean isRender() {
      return this.bounds != null;
   }

   public void setExpanded(boolean var1) {
      if (this.expanded != var1) {
         this.expanded = var1;
         this.openRow = null;
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         if (var1) {
            this.openAnimation.reset(0.0F);
            this.openAnimation.method2810(1.0F);
         }
      }
   }

   public void toggleExpanded() {
      this.setExpanded(!this.expanded);
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public boolean collapseOpenRow() {
      if (this.openRow == null) {
         return false;
      } else {
         this.openRow = null;
         return true;
      }
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.renderColumn(var1, var2, var3, var4, var5, var6, 100.0F);
   }

   public void renderColumn(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (!(var4 <= 0.01F) && var8 != null) {
         float var9 = NLMenuScreen.heightPanel;
         this.bounds = new BoundsData(var5, var6, var7, var9);
         this.renderTopBar(var1, var2, var3, var5, var6, var7, var4, var8);
         this.renderAccount(var1, var2, var3, var5, var6 + var9, var7, var4, var8);
         float var10 = var6 + 23.0F + GuiStyle.PADDING.intValue();
         float var11 = var6 + var9 - ACCOUNT_BLOCK - var10;
         this.contentBounds = new BoundsData(var5, var10, var7, var11);
         float var12 = 0.0F;

         for (QuickMenuPanel.Row var14 : this.rows) {
            var12 += var14.height();
         }

         this.clampScroll(var12, var11);
         this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
         float var19 = this.openAnimation.method2808(1.0F);
         var1.enableScissor(var5, var10, var5 + var7, var10 + var11);
         float var20 = var10 + this.scroll;
         int var15 = 0;

         for (QuickMenuPanel.Row var17 : this.rows) {
            float var18;
            if ((var18 = rowEntrance(var19, var15++)) > 0.001F) {
               var17.render(var1, var2, var3, var5 + (1.0F - var18) * 7.0F, var20, var7, var4 * var18, var8, var17 == this.openRow);
            } else {
               var17.clearBounds();
            }

            var20 += var17.height();
         }

         var1.disableScissor();
         if (var12 > var11) {
            float var21 = Math.max(12.0F, var11 * (var11 / var12));
            float var22;
            float var23 = (var22 = var12 - var11) <= 0.0F ? 0.0F : MathHelper.clamp(-this.scroll / var22, 0.0F, 1.0F);
            var1.drawRect(var5 + var7 - 2.0F, var10 + (var11 - var21) * var23, 1.0F, var21, var8.getTextTertiary().getZenithColor().method7294(var4 * 0.6F));
         }
      } else {
         this.clearBounds();
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

   private static float rowEntrance(float var0, int var1) {
      if (var0 >= 1.0F) {
         return 1.0F;
      } else {
         float var2 = Math.min(var1 * 0.045F, 0.4F);
         return MathHelper.clamp((var0 - var2) / 0.6F, 0.0F, 1.0F);
      }
   }

   private void clearBounds() {
      this.bounds = null;
      this.contentBounds = null;
      this.closeBounds = null;
      this.accountBounds = null;

      for (QuickMenuPanel.Row var2 : this.rows) {
         var2.clearBounds();
         var2.dragging = false;
      }
   }

   private void renderTopBar(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = Fonts.NEW_MEDIUM.getFont(4.6F);
      var1.drawText(
         var9, HoverInfo.spaced("ZENITH"), var4 + 8.0F, var5 + (23.0F - var9.height()) / 2.0F, var8.getTextTertiary().getZenithColor().method7294(var7)
      );
      float var10 = var4 + var6 - 8.0F - 6.0F;
      float var11 = var5 + 8.5F;
      this.closeBounds = new BoundsData(var10 - 4.0F, var11 - 4.0F, 14.0F, 14.0F);
      MenuGlyph.CLOSE
         .draw(var1, var10, var11, (this.closeBounds.contains(var2, var3) ? var8.getTextEnable() : var8.getTextTertiary()).getZenithColor().method7294(var7));
   }

   private void renderAccount(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Zenith.Entry var9 = Zenith.getInstance().getEntry();
      String var10 = var9.getUsername() != null && !var9.getUsername().isBlank() ? var9.getUsername() : "guest";
      float var11 = var4 + 8.0F;
      float var12 = var6 - 16.0F;
      float var13 = var5 - GuiStyle.PADDING.intValue() * 2.0F - 16.0F;
      var1.drawRect(var11, var13 - GuiStyle.PADDING.intValue() * 2.0F, var12, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var7));
      this.accountBounds = new BoundsData(var4, var13 - 2.0F, var6, 20.0F);
      Identifier var14 = CloudAvatarCache.get(var9.getText2());
      if (var14 != null) {
         var1.drawRoundedTexture(var14, var11, var13, 16.0F, 16.0F, ProxyDotRadiusData.method8069(8.0F), ZenithColor.INSTANCE.method7294(var7));
      } else {
         var1.drawRoundedRect(var11, var13, 16.0F, 16.0F, ProxyDotRadiusData.method8069(8.0F), var8.getPrimaryColor().getZenithColor().method7294(var7 * 0.3F));
         Font var15 = Fonts.NEW_MEDIUM.getFont(6.72F);
         String var16 = var10.substring(0, 1).toUpperCase(Locale.ROOT);
         var1.drawText(
            var15,
            var16,
            var11 + (16.0F - var15.width(var16)) / 2.0F,
            var13 + (16.0F - var15.height()) / 2.0F,
            var8.getTextEnable().getZenithColor().method7294(var7)
         );
      }

      Font var20 = Fonts.NEW_MEDIUM.getFont(6.4F);
      Font var21 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var17 = var11 + 16.0F + GuiStyle.PADDING.intValue() - 1.0F;
      float var18 = var11 + var12;
      String var19 = var9.method1504() == null ? "user" : var9.method1504().getName().toLowerCase(Locale.ROOT);
      var1.drawText(var20, var20.clip(var10, var18 - var17), var17, var13 + 0.4F, var8.getTextEnable().getZenithColor().method7294(var7));
      var1.drawText(
         var21, var21.clip(var19, var18 - var17), var17, var13 + 16.0F - var21.height() - 0.4F, var8.getTextTertiary().getZenithColor().method7294(var7)
      );
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.expanded && this.bounds != null && this.bounds.contains(var1, var3)) {
         if (this.closeBounds != null && this.closeBounds.contains(var1, var3)) {
            this.setExpanded(false);
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else if (this.accountBounds != null && this.accountBounds.contains(var1, var3)) {
            this.setExpanded(false);
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else {
            if (this.openRow != null) {
               for (QuickMenuPanel.Option var7 : this.openRow.options) {
                  if (var7.bounds != null && var7.bounds.contains(var1, var3) && var7.clickable()) {
                     var7.click.run();
                     Zenith.getInstance().getClientSounds().method1570(false);
                     return true;
                  }
               }
            }

            for (QuickMenuPanel.Row var9 : this.rows) {
               if (var9.kind == QuickMenuPanel.Row.Kind.NUMBER && var9.trackBounds != null && var9.trackBounds.contains(var1, var3)) {
                  var9.startDrag(var1);
                  return true;
               }

               if (var9.bounds != null && var9.bounds.contains(var1, var3)) {
                  switch (var9.kind) {
                     case SUBMENU:
                        this.openRow = this.openRow == var9 ? null : var9;
                     case NUMBER:
                        break;
                     case MODE:
                        var9.cycle();
                        break;
                     default:
                        if (var9.click != null) {
                           var9.click.run();
                           this.openRow = null;
                        }
                  }

                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      boolean var6 = false;

      for (QuickMenuPanel.Row var8 : this.rows) {
         if (var8.dragging) {
            var8.dragging = false;
            var6 = true;
         }
      }

      return var6;
   }

   public boolean onMouseDragged(double var1, float var3) {
      for (QuickMenuPanel.Row var5 : this.rows) {
         if (var5.dragging) {
            var5.dragBy(var1, var3);
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.expanded && this.contentBounds != null && this.contentBounds.contains(var1, var3)) {
         this.scrollTarget += (float)var7 * 18.0F;
         return true;
      } else {
         return false;
      }
   }

   private static String languageTitle(String var0) {
      String var1 = var0 == null ? "" : var0.toLowerCase(Locale.ROOT);

      return switch (var1) {
         case "ru" -> "Русский";
         case "pl" -> "Polski";
         case "tr" -> "Türkçe";
         default -> "English";
      };
   }

   private void openFolder() {
      try {
         Runtime.getRuntime().exec("explorer.exe \"" + Zenith.file.getAbsolutePath() + "\"");
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   private static void openLink(String var0) {
      try {
         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + var0);
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   private static final class Option {
      private final Supplier<String> label;
      private final BooleanSupplier selected;
      private final Runnable click;
      private final Supplier<String> value;
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private BoundsData bounds;

      private Option(Supplier<String> var1, BooleanSupplier var2, Runnable var3, Supplier<String> var4) {
         this.label = var1;
         this.selected = var2;
         this.click = var3;
         this.value = var4;
      }

      private boolean clickable() {
         return this.click != null;
      }

      private void renderOption(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
         this.bounds = new BoundsData(var4, var5, var6, 12.0F);
         boolean var9 = this.clickable() && this.bounds.contains(var2, var3);
         boolean var10 = this.selected != null && this.selected.getAsBoolean();
         float var11 = this.hover.method2808(var9 ? 1.0F : 0.0F);
         float var12 = var4 + 8.0F + 6.0F;
         float var13 = var4 + var6 - 8.0F;
         if (var10 || var11 > 0.002F) {
            var1.drawRoundedRect(
               var12 - 3.0F,
               var5 + 0.5F,
               var13 - var12 + 5.0F,
               11.0F,
               ProxyDotRadiusData.method8069(3.0F),
               var8.getDisableActiveBg().getZenithColor().method7294(var7 * (var10 ? 1.0F : var11 * 0.5F))
            );
         }

         if (var10) {
            var1.drawRect(var4, var5 + 2.0F, 2.0F, 8.0F, var8.getPrimaryColor().getZenithColor().method7294(var7));
         }

         Font var14 = Fonts.NEW_REGULAR.getFont(4.8F);
         float var15 = var5 + (12.0F - var14.height()) / 2.0F;
         if (this.value != null) {
            String var19 = this.value.get();
            Font var17 = Fonts.NEW_REGULAR.getFont(4.6F);
            float var18 = var17.width(var19);
            var1.drawText(var17, var19, var13 - var18, var5 + (12.0F - var17.height()) / 2.0F, var8.getTextTertiary().getZenithColor().method7294(var7));
            var1.drawText(
               var14,
               var14.clip(this.label.get(), var13 - var18 - GuiStyle.PADDING.intValue() - var12),
               var12,
               var15,
               var8.getTextSecondary().getZenithColor().method7294(var7)
            );
         } else {
            ZenithColor var16 = (!var10 && !var9 ? var8.getTextSecondary() : var8.getTextEnable()).getZenithColor().method7294(var7);
            var1.drawText(var14, var14.clip(this.label.get(), var13 - var12), var12, var15, var16);
         }
      }
   }

   private static final class Row {
      private final QuickMenuPanel.Row.Kind kind;
      private final String key;
      private final MenuGlyph icon;
      private final BooleanSupplier state;
      private final Supplier<String> value;
      private final Runnable click;
      private final SliderSetting number;
      private float shownNumber = Float.NaN;
      private boolean dragging;
      private float dragValue;
      private BoundsData trackBounds;
      private final List<QuickMenuPanel.Option> options = new ArrayList<>();
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private final Animation toggleAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private final Animation expandAnimation = new Animation(180L, 0.0F, Easing.INSTANCE13);
      private BoundsData bounds;

      private Row(QuickMenuPanel.Row.Kind var1, String var2, MenuGlyph var3, BooleanSupplier var4, Supplier<String> var5, Runnable var6, SliderSetting var7) {
         this.kind = var1;
         this.key = var2;
         this.icon = var3;
         this.state = var4;
         this.value = var5;
         this.click = var6;
         this.number = var7;
      }

      private static QuickMenuPanel.Row caption(String var0) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.CAPTION, var0, null, null, null, null, null);
      }

      private static QuickMenuPanel.Row toggle(String var0, MenuGlyph var1, BooleanSupplier var2, Runnable var3) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.TOGGLE, var0, var1, var2, null, var3, null);
      }

      private static QuickMenuPanel.Row action(String var0, MenuGlyph var1, Runnable var2) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.ACTION, var0, var1, null, null, var2, null);
      }

      private static QuickMenuPanel.Row action(String var0, MenuGlyph var1, Supplier<String> var2, Runnable var3) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.ACTION, var0, var1, null, var2, var3, null);
      }

      private static QuickMenuPanel.Row submenu(String var0, MenuGlyph var1, Supplier<String> var2) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.SUBMENU, var0, var1, null, var2, null, null);
      }

      private static QuickMenuPanel.Row number(String var0, MenuGlyph var1, SliderSetting var2, Supplier<String> var3) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.NUMBER, var0, var1, null, var3, null, var2);
      }

      private static QuickMenuPanel.Row mode(String var0, MenuGlyph var1) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.MODE, var0, var1, null, null, null, null);
      }

      private QuickMenuPanel.Row option(Supplier<String> var1, BooleanSupplier var2, Runnable var3) {
         this.options.add(new QuickMenuPanel.Option(var1, var2, var3, null));
         return this;
      }

      private QuickMenuPanel.Row info(String var1, Supplier<String> var2) {
         this.options.add(new QuickMenuPanel.Option(() -> QuickMenuPanel.t(var1), null, null, var2));
         return this;
      }

      private float height() {
         return switch (this.kind) {
            case CAPTION -> 13.0F;
            case NUMBER -> 24.0F;
            case MODE -> 15.0F;
            default -> 15.0F + this.options.size() * 12.0F * this.expandAnimation.getValue();
         };
      }

      private float ownHeight() {
         return this.kind == QuickMenuPanel.Row.Kind.NUMBER ? 24.0F : 15.0F;
      }

      private void clearBounds() {
         this.bounds = null;
         this.trackBounds = null;

         for (QuickMenuPanel.Option var2 : this.options) {
            var2.bounds = null;
         }
      }

      private void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, boolean var9) {
         if (this.kind == QuickMenuPanel.Row.Kind.CAPTION) {
            this.clearBounds();
            Font var30 = Fonts.NEW_MEDIUM.getFont(3.8F);
            var1.drawText(
               var30,
               HoverInfo.spaced(QuickMenuPanel.t(this.key).toUpperCase(Locale.ROOT)),
               var4 + 8.0F,
               var5 + (13.0F - var30.height()) / 2.0F + 1.5F,
               var8.getTextTertiary().getZenithColor().method7294(var7 * 0.9F)
            );
         } else {
            float var10 = this.ownHeight();
            this.bounds = new BoundsData(var4, var5, var6, var10);
            boolean var11 = this.bounds.contains(var2, var3);
            float var12 = this.hover.method2808(var11 ? 1.0F : 0.0F);
            float var13 = this.expandAnimation.method2808(var9 ? 1.0F : 0.0F);
            float var14 = Math.max(Math.max(var12, var13), this.dragging ? 1.0F : 0.0F);
            if (var14 > 0.002F) {
               var1.drawRoundedRect(
                  var4 + 2.0F,
                  var5,
                  var6 - 4.0F,
                  var10,
                  ProxyDotRadiusData.method8069(3.0F),
                  var8.getDisableActiveBg().getZenithColor().method7294(var7 * var14 * 0.7F)
               );
            }

            if (var11) {
               HoverInfo.request(QuickMenuPanel.t(this.key), QuickMenuPanel.t(this.key + ".desc"));
            }

            Font var15 = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font var16 = Fonts.NEW_MEDIUM.getFont(5.01F);
            ZenithColor var17 = var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var14).method7294(var7);
            float var18 = var4 + 8.0F;
            float var19 = this.kind == QuickMenuPanel.Row.Kind.NUMBER ? 15.0F : var10;
            if (this.icon != null && Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
               this.icon.draw(var1, var18, var5 + (var19 - 6.0F) / 2.0F, var17);
               var18 += 10.0F;
            }
            String var20 = switch (this.kind) {
               case ACTION -> this.value == null ? QuickMenuPanel.t("quick.value.open") : (String)this.value.get();
               case SUBMENU, NUMBER -> this.value == null ? "" : (String)this.value.get();
               case MODE -> this.currentOptionLabel();
               default -> "";
            };

            float var21 = switch (this.kind) {
               case TOGGLE -> 12.0F;
               default -> var16.width(var20);
               case SUBMENU -> 8.0F + var16.width(var20);
               case MODE -> this.pipsWidth() + GuiStyle.PADDING.intValue() + var16.width(var20);
            };
            float var22 = var4 + var6 - 8.0F - var21 - GuiStyle.PADDING.intValue() - var18;
            var1.drawText(var15, var15.clip(QuickMenuPanel.t(this.key), var22), var18, var5 + (var19 - var15.height()) / 2.0F, var17);
            float var23 = var5 + (var19 - var16.height()) / 2.0F;
            float var24 = var4 + var6 - 8.0F;
            switch (this.kind) {
               case TOGGLE:
                  this.renderToggle(var1, var24, var5, var7, var8);
                  break;
               case ACTION:
                  var1.drawText(var16, var20, var24 - var16.width(var20), var23, var8.getPrimaryColor().getZenithColor().method7294(var7));
                  break;
               case SUBMENU:
                  float var31 = var24 - 5.0F;
                  MenuGlyph.drawChevron(var1, var31, var5 + var10 / 2.0F, var13, var8.getTextTertiary().getZenithColor().method7294(var7));
                  var1.drawText(var16, var20, var31 - 3.0F - var16.width(var20), var23, var8.getPrimaryColor().getZenithColor().method7294(var7));
                  break;
               case NUMBER:
                  var1.drawText(var16, var20, var24 - var16.width(var20), var23, var8.getPrimaryColor().getZenithColor().method7294(var7));
                  this.renderTrack(var1, var4, var5, var6, var7, var8);
                  break;
               case MODE:
                  float var25 = var16.width(var20);
                  var1.drawText(var16, var20, var24 - var25, var23, var8.getPrimaryColor().getZenithColor().method7294(var7));
                  this.renderPips(var1, var24 - var25 - GuiStyle.PADDING.intValue(), var5 + var10 / 2.0F, var7, var8);
            }

            if (var13 <= 0.001F) {
               for (QuickMenuPanel.Option var34 : this.options) {
                  var34.bounds = null;
               }
            } else {
               float var32 = var5 + 15.0F;
               float var26 = this.options.size() * 12.0F * var13;
               var1.enableScissor(var4, var32, var4 + var6, var32 + var26);
               float var27 = var32;

               for (QuickMenuPanel.Option var29 : this.options) {
                  var29.renderOption(var1, var2, var3, var4, var27, var6, var7, var8);
                  var27 += 12.0F;
               }

               var1.disableScissor();
            }
         }
      }

      private void renderTrack(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
         if (this.number == null) {
            this.trackBounds = null;
         } else {
            float var7 = var2 + 8.0F;
            float var8 = var4 - 16.0F;
            float var9 = var3 + 17.0F;
            this.trackBounds = new BoundsData(var7 - 5.0F, var9 - 5.0F, var8 + 10.0F, 10.0F);
            float var10 = this.smoothNumber();
            float var11 = this.number.getMax() - this.number.getMin();
            float var12 = var11 <= 0.0F ? 0.0F : MathHelper.clamp((var10 - this.number.getMin()) / var11, 0.0F, 1.0F);
            var1.drawRect(var7, var9, var8, 1.0F, var6.getFieldBorder().getZenithColor().method7294(var5 * 3.0F));
            var1.drawRect(var7, var9, var8 * var12, 1.0F, var6.getPrimaryColor().getZenithColor().method7294(var5));
            var1.drawRect(var7 + var8 * var12 - 0.75F, var9 - 2.0F, 1.5F, 5.0F, var6.getPrimaryColor().getZenithColor().method7294(var5));
         }
      }

      private float smoothNumber() {
         float var1 = this.number.getValue3();
         if (Float.isNaN(this.shownNumber)) {
            this.shownNumber = var1;
            return var1;
         } else {
            this.shownNumber = MathHelper.lerp(0.3F, this.shownNumber, var1);
            if (Math.abs(var1 - this.shownNumber) < this.number.getFloatValue2() / 100.0F) {
               this.shownNumber = var1;
            }

            return this.shownNumber;
         }
      }

      private void renderPips(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
         int var6 = this.options.size();
         if (var6 > 1) {
            float var7 = var2 - this.pipsWidth();
            int var8 = this.currentOptionIndex();
            ZenithColor var9 = var5.getFieldBorder().getZenithColor().method7294(var4 * 4.0F);
            ZenithColor var10 = var5.getPrimaryColor().getZenithColor().method7294(var4);

            for (int var11 = 0; var11 < var6; var11++) {
               var1.drawRect(var7 + var11 * 3.5F, var3 - 1.0F, 2.0F, 2.0F, var11 == var8 ? var10 : var9);
            }
         }
      }

      private float pipsWidth() {
         int var1 = this.options.size();
         return var1 <= 1 ? 0.0F : var1 * 2.0F + (var1 - 1) * 1.5F;
      }

      private int currentOptionIndex() {
         for (int var1 = 0; var1 < this.options.size(); var1++) {
            QuickMenuPanel.Option var2 = this.options.get(var1);
            if (var2.selected != null && var2.selected.getAsBoolean()) {
               return var1;
            }
         }

         return 0;
      }

      private String currentOptionLabel() {
         return this.options.isEmpty() ? "" : this.options.get(this.currentOptionIndex()).label.get();
      }

      private void cycle() {
         if (!this.options.isEmpty()) {
            QuickMenuPanel.Option var1 = this.options.get((this.currentOptionIndex() + 1) % this.options.size());
            if (var1.click != null) {
               var1.click.run();
            }
         }
      }

      private boolean startDrag(double var1) {
         if (this.number != null && this.trackBounds != null) {
            float var3 = this.number.getMax() - this.number.getMin();
            float var4 = var3 <= 0.0F ? 0.0F : (this.number.getValue3() - this.number.getMin()) / var3;
            float var5 = this.pointerAt(var1);
            this.dragValue = Math.abs(var5 - var4) * this.trackWidth() <= 5.0F ? var4 : var5;
            this.dragging = true;
            this.applyNormalized(this.dragValue);
            return true;
         } else {
            return false;
         }
      }

      private void dragBy(double var1, float var3) {
         if (this.number != null && this.trackBounds != null) {
            float var4 = this.trackWidth();
            if (!(var4 <= 0.0F) && !(var3 <= 0.0F)) {
               this.dragValue = MathHelper.clamp(this.dragValue + (float)(var1 / var3) / var4, 0.0F, 1.0F);
               this.applyNormalized(this.dragValue);
            }
         }
      }

      private float trackWidth() {
         return this.trackBounds == null ? 0.0F : this.trackBounds.width() - 10.0F;
      }

      private float pointerAt(double var1) {
         float var3 = this.trackBounds.getFloatValue() + 5.0F;
         float var4 = this.trackWidth();
         return var4 <= 0.0F ? 0.0F : MathHelper.clamp((float)(var1 - var3) / var4, 0.0F, 1.0F);
      }

      private void applyNormalized(float var1) {
         float var2 = this.number.getMin() + (this.number.getMax() - this.number.getMin()) * var1;
         float var3 = this.number.getFloatValue2();
         if (var3 > 0.0F) {
            var2 = Math.round(var2 / var3) * var3;
         }

         float var4 = this.number.getValue3();
         this.number.method2400(MathHelper.clamp(var2, this.number.getMin(), this.number.getMax()));
         if (this.number.getValue3() != var4) {
            Zenith.getInstance().getClientSounds().method1571(var1);
         }
      }

      private void renderToggle(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
         float var6 = 12.0F;
         float var7 = 6.5F;
         float var8 = var2 - var6;
         float var9 = var3 + (15.0F - var7) / 2.0F;
         boolean var10 = this.state != null && this.state.getAsBoolean();
         float var11 = this.toggleAnimation.method2808(var10 ? 1.0F : 0.0F);
         var1.drawRoundedRect(
            var8,
            var9,
            var6,
            var7,
            ProxyDotRadiusData.method8069(var7 / 2.0F),
            var5.getFieldSurfaceBackground()
               .getZenithColor()
               .method7295(var5.getPrimaryColor().getZenithColor(), var11)
               .method7294(var4 * (1.0F + (1.0F - var11)))
         );
         float var12 = var7 - 2.0F;
         float var13 = var8 + 1.0F + (var6 - var12 - 2.0F) * var11;
         var1.drawRoundedRect(
            var13,
            var9 + 1.0F,
            var12,
            var12,
            ProxyDotRadiusData.method8069(var12 / 2.0F),
            var5.getTextEnable().getZenithColor().method7294(var4 * (0.55F + 0.45F * var11))
         );
      }

      private static enum Kind {
         CAPTION,
         TOGGLE,
         ACTION,
         SUBMENU,
         NUMBER,
         MODE;
      }
   }
}

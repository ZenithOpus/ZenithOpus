package org.zenith.client.screens.nlgui.panel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.cloud.CloudCard;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.ConfigSyncService;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ConfigInfoPage {
   public static final int[] PRICES = new int[]{0, 49, 99, 199, 499};
   private static final String[] LOAD_KEYS = new String[]{"gui.configs.load.all", "gui.configs.load.noBinds", "gui.configs.load.themeOnly"};
   private static final String[] VISIBILITY = new String[]{"PRIVATE", "FRIENDS", "CODE"};
   private static final String[] VISIBILITY_KEYS = new String[]{"gui.configs.vis.private", "gui.configs.vis.friends", "gui.configs.vis.code"};
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor WARN = new ZenithColor(247, 206, 59);
   private static final SimpleDateFormat DATE = new SimpleDateFormat("dd.MM HH:mm", Locale.ROOT);
   private final ConfigInfoPage.Host host;
   private final FormField name = new FormField(48, false);
   private final FormField description = new FormField(160, true);
   private final List<FormField> fields = List.of(this.name, this.description);
   private final Map<String, BoundsData> buttons = new LinkedHashMap<>();
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Long> pressed = new HashMap<>();
   private volatile List<StoreApi.Data> codes = List.of();
   private final Smooth scroller = new Smooth(16.0F);
   private final Animation appear = new Animation(320L, 0.0F, Easing.INSTANCE15);
   private String fileName;
   private String display;
   private int loadMode;
   private int priceIndex;
   private boolean busy;
   private String armed = "";
   private long armedAt;
   private BoundsData bounds;
   private volatile String message = "";
   private volatile boolean messageError;
   private volatile long messageAt;
   private static final long FILE_STAT_MS = 1000L;
   private String statFile;
   private String statLanguage;
   private long statAt;
   private String statText = "";

   public void message(String var1, boolean var2) {
      this.message = var1 == null ? "" : var1;
      this.messageError = var2;
      this.messageAt = System.currentTimeMillis();
   }

   public ConfigInfoPage(ConfigInfoPage.Host var1) {
      this.host = var1;
   }

   public String fileName() {
      return this.fileName;
   }

   public void open(String var1, String var2) {
      boolean var3 = var1.equals(this.fileName);
      this.fileName = var1;
      this.display = var2;
      if (!var3) {
         this.name.set(var2);
         this.name.commit();
         StoreApi.Data2 var4 = this.twin();
         this.description.set(var4 == null ? "" : var4.description());
         this.description.commit();
         StoreApi.Data5 var5 = var4 == null ? StoreApi.Data5.INSTANCE : StoreApi.method1960(var4.getUUID());
         this.priceIndex = 0;

         for (int var6 = 0; var6 < PRICES.length; var6++) {
            if (PRICES[var6] == var5.getIntValue()) {
               this.priceIndex = var6;
            }
         }

         this.armed = "";
         this.codes = List.of();
         this.statFile = null;
         this.scroller.reset(0.0F);
         this.appear.reset(0.0F);
         this.appear.method2810(1.0F);

         for (FormField var7 : this.fields) {
            var7.setFocused(false);
         }

         if (var4 != null) {
            this.loadCodes(var4);
         }
      }
   }

   private StoreApi.Data2 twin() {
      return ConfigSyncService.getInstance().method672(this.display);
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   public boolean isDirty() {
      return this.name.changed() || this.description.changed();
   }

   public boolean isTyping() {
      for (FormField var2 : this.fields) {
         if (var2.isFocused()) {
            return true;
         }
      }

      return false;
   }

   private float hot(String var1, boolean var2) {
      return this.hover.computeIfAbsent(var1, var0 -> new Animation(150L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private static void card(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      ScriptsSectionPanel.card(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   private float button(
      MenuDrawContext var1,
      ZenithStyle var2,
      String var3,
      String var4,
      MenuGlyph var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      float var11,
      int var12
   ) {
      BoundsData var13 = new BoundsData(var6, var7, var8, 16.0F);
      boolean var14 = var13.contains(var9, var10);
      float var15 = this.hot(var3, var14);
      ZenithColor var16 = var12 == 2 ? ERROR : var2.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData var17 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      float var18 = Smooth.press(this.pressed.getOrDefault(var3, 0L), 320L);
      ZenithColor var19 = var2.getFieldSurfaceBackground().getZenithColor().method7295(var2.getDisableActiveBg().getZenithColor(), 0.35F + var15 * 0.55F);
      if (var12 > 0) {
         var19 = var19.method7295(var16.method7292(0.14F), 0.3F + var15 * 0.35F + (this.armed.equals(var3) ? 0.4F : 0.0F));
      }

      var1.drawRoundedRect(var6, var7, var8, 16.0F, var17, var19.method7294(var11));
      if (var18 > 0.01F) {
         float var20 = (1.0F - var18) * 3.0F;
         var1.drawRoundedRect(var6 - var20 * 0.5F, var7 - var20 * 0.5F, var8 + var20, 16.0F + var20, var17, var16.method7292(0.35F * var18).method7294(var11));
      }

      var1.drawRoundedBorder(
         var6,
         var7,
         var8,
         16.0F,
         0.15F,
         var17,
         (var12 > 0 ? var16.method7292(0.2F + var15 * 0.25F) : var2.getFieldBorder().getZenithColor().method7294(2.0F + var15 * 2.0F)).method7294(var11)
      );
      Font var23 = Fonts.NEW_MEDIUM.getFont(4.8F);
      float var21 = var6 + 7.0F;
      if (var5 != null) {
         var5.draw(var1, var21, var7 + 5.0F, (var12 > 0 ? var16 : var2.getTextSecondary().getZenithColor()).method7294(var11));
         var21 += 10.0F;
      }

      String var22 = this.armed.equals(var3) ? t("gui.scripts.info.confirm") : var4;
      var1.drawText(
         var23,
         var23.clip(var22, var6 + var8 - var21 - 5.0F),
         var5 == null ? var6 + (var8 - var23.width(var23.clip(var22, var8 - 10.0F))) / 2.0F : var21,
         var7 + (16.0F - var23.height()) / 2.0F,
         (var12 > 0 ? var16 : var2.getTextSecondary().getZenithColor().method7295(var2.getTextEnable().getZenithColor(), var15)).method7294(var11)
      );
      this.buttons.put(var3, var13);
      return 16.0F;
   }

   private float chips(
      MenuDrawContext var1, ZenithStyle var2, String var3, String[] var4, int var5, float var6, float var7, float var8, int var9, int var10, float var11
   ) {
      float var12 = 3.0F;
      float var13 = (var8 - var12 * (var4.length - 1)) / var4.length;

      for (int var14 = 0; var14 < var4.length; var14++) {
         this.button(var1, var2, var3 + var14, var4[var14], null, var6 + var14 * (var13 + var12), var7, var13, var9, var10, var11, var14 == var5 ? 1 : 0);
      }

      return 16.0F;
   }

   private float field(MenuDrawContext var1, ZenithStyle var2, String var3, FormField var4, String var5, float var6, float var7, float var8, float var9) {
      Font var10 = Fonts.NEW_MEDIUM.getFont(4.4F);
      Font var11 = Fonts.NEW_MEDIUM.getFont(5.0F);
      var1.drawText(var10, t(var3), var6, var7, var2.getTextSecondary().getZenithColor().method7294(var9));
      float var12 = var4.render(
         var1,
         var11,
         var6,
         var7 + var10.height() + 3.0F,
         var8,
         var9,
         t(var5),
         var2.getFieldSurfaceBackground().getZenithColor().method7295(var2.getDisableActiveBg().getZenithColor(), 0.3F),
         var2.getFieldBorder().getZenithColor().method7294(2.5F),
         var2.getPrimaryColor().getZenithColor().method7292(0.6F),
         var2.getTextEnable().getZenithColor(),
         var2.getTextTertiary().getZenithColor()
      );
      return var10.height() + 3.0F + var12 + 7.0F;
   }

   private static void caption(MenuDrawContext var0, ZenithStyle var1, String var2, float var3, float var4, float var5) {
      Font var6 = Fonts.NEW_MEDIUM.getFont(4.2F);
      var0.drawText(var6, var2, var3, var4, var1.getTextTertiary().getZenithColor().method7294(var5));
   }

   private static void line(MenuDrawContext var0, String var1, ZenithColor var2, float var3, float var4, float var5, float var6) {
      Font var7 = Fonts.NEW_REGULAR.getFont(4.3F);
      var0.drawText(var7, var7.clip(var1, var5), var3, var4, var2.method7294(var6));
   }

   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      if (this.fileName != null) {
         float var10 = var8 * this.appear.method2808(1.0F);
         var4 += (1.0F - this.appear.getValue()) * 10.0F;
         this.bounds = new BoundsData(var4, var5, var6, var7);
         this.buttons.clear();
         if (!this.armed.isEmpty() && System.currentTimeMillis() - this.armedAt > 3000L) {
            this.armed = "";
         }

         float var11 = this.scroller.update();
         float var12 = 8.0F;
         float var13 = var4 + var12;
         float var14 = var6 - var12 * 2.0F;
         Font var15 = Fonts.NEW_REGULAR.getFont(4.3F);
         ZenithColor var16 = var9.getPrimaryColor().getZenithColor();
         BoundsData var17 = new BoundsData(var13 - 2.0F, var5 + 1.0F, 12.0F, 12.0F);
         boolean var18 = var17.contains(var2, var3);
         Font var19 = Fonts.NEW_MEDIUM.getFont(5.2F);
         var1.drawText(
            var19,
            "‹",
            var17.getFloatValue() + 3.0F,
            var17.getFloatValue2() + (12.0F - var19.height()) / 2.0F,
            (var18 ? var9.getTextEnable() : var9.getTextSecondary()).getZenithColor().method7294(var10)
         );
         this.buttons.put("close", var17);
         Font var20 = Fonts.NEW_MEDIUM.getFont(4.2F);
         var1.drawText(
            var20,
            t("gui.configs.info.settings"),
            var13 + 12.0F,
            var5 + 1.0F + (12.0F - var20.height()) / 2.0F,
            var9.getTextTertiary().getZenithColor().method7294(var10)
         );
         if (this.isDirty()) {
            String var21 = t("gui.scripts.ws.save");
            float var22 = var20.width(var21) + 12.0F;
            BoundsData var23 = new BoundsData(var4 + var6 - var12 - var22, var5, var22, 13.0F);
            boolean var24 = var23.contains(var2, var3);
            var1.drawRoundedRect(
               var23.getFloatValue(),
               var23.getFloatValue2(),
               var23.width(),
               var23.height(),
               ProxyDotRadiusData.method8069(6.5F),
               var16.method7294(var10 * (var24 ? 1.0F : 0.8F))
            );
            var1.drawText(
               var20, var21, var23.getFloatValue() + 6.0F, var23.getFloatValue2() + (13.0F - var20.height()) / 2.0F, ZenithColor.INSTANCE.method7294(var10)
            );
            this.buttons.put("save", var23);
         }

         float var47 = var5 + 18.0F;
         MsdfRenderer.flushBatch();
         var1.enableScissor(var4, var47, var4 + var6, var5 + var7);
         float var48 = var47 + var11;
         StoreApi.Data2 var66 = this.twin();
         ConfigSyncService.Type var25 = ConfigSyncService.getInstance().method671(this.display);
         card(var1, var9, var13, var48, var14, 44.0F, 0.0F, var10);
         float var26 = 32.0F;
         ZenithColor var27 = CloudConfigsSectionPanel.letterColor(this.display);
         var1.drawRoundedRect(var13 + 6.0F, var48 + 6.0F, var26, var26, ProxyDotRadiusData.method8069(var26 * 0.28F), var27.method7292(0.16F).method7294(var10));
         Font var28 = Fonts.NEW_SEMIBOLD.getFont(var26 * 0.38F);
         String var29 = this.display.isEmpty() ? "?" : this.display.substring(0, 1).toUpperCase(Locale.ROOT);
         var1.drawText(
            var28, var29, var13 + 6.0F + (var26 - var28.width(var29)) / 2.0F, var48 + 6.0F + (var26 - var28.height()) / 2.0F, var27.method7294(var10)
         );
         Font var30 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         float var31 = var13 + 6.0F + var26 + 6.0F;
         float var32 = var13 + var14 - var31 - 4.0F;
         var1.drawText(
            var30,
            var30.clip(this.name.get().isEmpty() ? this.display : this.name.get(), var32),
            var31,
            var48 + 9.0F,
            var9.getTextEnable().getZenithColor().method7294(var10)
         );
         String var33 = this.fileStat()
            + (var66 == null ? "" : "  ·  v" + var66.getLongValue2() + (var66.getIntValue() > 0 ? "  ·  ♥ " + var66.getIntValue() : ""));
         var1.drawText(var15, var15.clip(var33, var32), var31, var48 + 11.0F + var30.height(), var9.getTextTertiary().getZenithColor().method7294(var10));
         var48 += 52.0F;
         var48 += this.field(var1, var9, "gui.configs.info.name", this.name, "gui.configs.creator.hint", var13, var48, var14, var10);
         var48 += this.field(var1, var9, "gui.configs.info.description", this.description, "gui.configs.info.description.hint", var13, var48, var14, var10);
         float var34 = (var14 - 4.0F) / 2.0F;
         caption(var1, var9, t("gui.configs.caption.apply"), var13, var48, var10);
         var48 += 9.0F;
         String[] var35 = new String[LOAD_KEYS.length];

         for (int var36 = 0; var36 < LOAD_KEYS.length; var36++) {
            var35[var36] = t(LOAD_KEYS[var36]);
         }

         var48 += this.chips(var1, var9, "load:", var35, this.loadMode, var13, var48, var14, var2, var3, var10) + 4.0F;
         this.button(var1, var9, "apply", t("gui.configs.apply"), MenuGlyph.CHECK, var13, var48, var34, var2, var3, var10, 1);
         this.button(var1, var9, "overwrite", t("gui.configs.overwrite"), MenuGlyph.COPY, var13 + var34 + 4.0F, var48, var34, var2, var3, var10, 0);
         var48 += 26.0F;
         String var67 = t("gui.configs.sync.state." + var25.name().toLowerCase(Locale.ROOT));
         caption(var1, var9, t("gui.configs.caption.cloud") + "  ·  " + var67, var13, var48, var10);
         var48 += 9.0F;
         if (var66 != null) {
            String[] var37 = new String[VISIBILITY_KEYS.length];

            for (int var38 = 0; var38 < VISIBILITY_KEYS.length; var38++) {
               var37[var38] = t(VISIBILITY_KEYS[var38]);
            }

            int var69 = visibilityIndex(var66.getText7());
            var48 += this.chips(var1, var9, "vis:", var37, var69, var13, var48, var14, var2, var3, var10) + 4.0F;
            this.button(var1, var9, "sync", t("gui.configs.syncNow"), MenuGlyph.CLOUD, var13, var48, var34, var2, var3, var10, 0);
            this.button(var1, var9, "code", t("gui.configs.issueCode"), MenuGlyph.KEY, var13 + var34 + 4.0F, var48, var34, var2, var3, var10, 0);
            var48 += 20.0F;

            for (StoreApi.Data var40 : this.codes) {
               BoundsData var41 = new BoundsData(var13, var48, var14, 11.0F);
               boolean var42 = var41.contains(var2, var3);
               boolean var43 = "ACTIVE".equalsIgnoreCase(var40.getText2());
               line(
                  var1,
                  var40.getText().isBlank() ? "••••••" : var40.getText(),
                  var42 ? var9.getTextEnable().getZenithColor() : (var43 ? var9.getTextSecondary().getZenithColor() : var9.getTextTertiary().getZenithColor()),
                  var13 + 2.0F,
                  var48 + 2.0F,
                  var14 * 0.7F,
                  var10
               );
               String var44 = var40.getText2().toLowerCase(Locale.ROOT);
               var1.drawText(var15, var44, var13 + var14 - var15.width(var44), var48 + 2.0F, var9.getTextTertiary().getZenithColor().method7294(var10));
               this.buttons.put("copy:" + var40.getUUID(), var41);
               if (var42) {
                  HoverInfo.request(t("gui.scripts.market.codes"), t("gui.configs.code.copy.desc"));
               }

               var48 += 12.0F;
            }

            var48 += 4.0F;
         } else {
            line(var1, t("gui.configs.info.notInCloud"), var9.getTextTertiary().getZenithColor(), var13, var48, var14, var10);
            var48 += 10.0F;
            this.button(var1, var9, "sync", t("gui.configs.syncNow"), MenuGlyph.CLOUD, var13, var48, var14, var2, var3, var10, 0);
            var48 += 24.0F;
         }

         caption(var1, var9, t("gui.market.caption.publish"), var13, var48, var10);
         var48 += 9.0F;
         StoreApi.Data5 var68 = var66 == null ? StoreApi.Data5.INSTANCE : StoreApi.method1960(var66.getUUID());
         var48 += marketStatus(var1, var9, var68, var13, var48, var14, var10);
         String[] var70 = new String[PRICES.length];

         for (int var71 = 0; var71 < PRICES.length; var71++) {
            var70[var71] = PRICES[var71] == 0 ? t("gui.market.price.free") : PRICES[var71] + " ₽";
         }

         boolean var72 = var68.pending() || var68.method2007();
         if (!var72) {
            var48 += this.chips(var1, var9, "price:", var70, this.priceIndex, var13, var48, var14, var2, var3, var10) + 4.0F;
            String var73 = this.busy
               ? t("gui.scripts.market.busy")
               : (PRICES[this.priceIndex] == 0 ? t("gui.market.publish.free") : t("gui.market.publish.review"));
            this.button(var1, var9, "market.submit", var73, MenuGlyph.STORAGE, var13, var48, var14, var2, var3, var10, 1);
         } else {
            this.button(
               var1,
               var9,
               "market.withdraw",
               this.busy ? t("gui.scripts.market.busy") : t("gui.market.withdraw"),
               MenuGlyph.CLOSE,
               var13,
               var48,
               var14,
               var2,
               var3,
               var10,
               0
            );
         }

         var48 += 26.0F;
         caption(var1, var9, t("gui.scripts.info.caption.manage"), var13, var48, var10);
         var48 += 9.0F;
         this.button(var1, var9, "folder", t("gui.configs.openFolder"), MenuGlyph.STORAGE, var13, var48, var34, var2, var3, var10, 0);
         this.button(var1, var9, "delete", t("gui.configs.deleteEverywhere"), MenuGlyph.CLOSE, var13 + var34 + 4.0F, var48, var34, var2, var3, var10, 2);
         var48 += 22.0F;
         long var74 = System.currentTimeMillis() - this.messageAt;
         if (!this.message.isEmpty() && var74 < 8000L) {
            float var75 = var74 > 7000L ? 1.0F - (float)(var74 - 7000L) / 1000.0F : Math.min(1.0F, (float)var74 / 180.0F);

            for (String var79 : SectionPanel.wrapText(this.message, var15, var14, 3)) {
               var1.drawText(var15, var79, var13, var48, (this.messageError ? ERROR : OK).method7294(var10 * var75));
               var48 += var15.height() + 2.0F;
            }

            var48 += 4.0F;
         }

         MsdfRenderer.flushBatch();
         var1.disableScissor();
         float var76 = var48 - var48 + 6.0F;
         float var78 = var5 + var7 - var47;
         this.scroller.clamp(Math.min(0.0F, var78 - var76), 0.0F);
         if (var76 > var78 + 1.0F) {
            float var80 = Math.max(14.0F, var78 * var78 / var76);
            float var45 = var47 + -var11 / (var76 - var78) * (var78 - var80);
            var1.drawRoundedRect(
               var4 + var6 - 3.0F, var45, 1.5F, var80, ProxyDotRadiusData.method8069(0.75F), var9.getTextTertiary().getZenithColor().method7294(var10 * 0.5F)
            );
         }

         for (Entry var82 : this.buttons.entrySet()) {
            if (((String)var82.getKey()).startsWith("price:") && ((BoundsData)var82.getValue()).contains(var2, var3)) {
               HoverInfo.request(t("gui.market.price"), t("gui.market.price.config.desc"));
            }
         }
      }
   }

   private String fileStat() {
      long var1 = System.currentTimeMillis();
      String var3 = Zenith.getInstance().getLocalization().getLanguageName();
      if (!this.fileName.equals(this.statFile) || !var3.equals(this.statLanguage) || var1 - this.statAt >= 1000L) {
         File var4 = new File(Manager.file, this.fileName);
         this.statText = CloudCard.size(var4.length()) + "  ·  " + DATE.format(new Date(var4.lastModified()));
         this.statFile = this.fileName;
         this.statLanguage = var3;
         this.statAt = var1;
      }

      return this.statText;
   }

   public static float marketStatus(MenuDrawContext var0, ZenithStyle var1, StoreApi.Data5 var2, float var3, float var4, float var5, float var6) {
      String var7;
      ZenithColor var8;
      if (var2.pending()) {
         var7 = t("gui.market.status.pending");
         var8 = WARN;
      } else if (var2.method2007()) {
         var7 = t("gui.market.status.approved")
            + "  ·  "
            + (var2.method2009() ? var2.getIntValue() + " ₽" : t("gui.market.price.free"))
            + (var2.isFlag() ? "  ·  " + t("gui.market.status.sealed") : "");
         var8 = OK;
      } else if (var2.method2008()) {
         var7 = t("gui.market.status.rejected") + (var2.getText2().isBlank() ? "" : ": " + var2.getText2());
         var8 = ERROR;
      } else {
         var7 = t("gui.market.status.none");
         var8 = var1.getTextTertiary().getZenithColor();
      }

      Font var9 = Fonts.NEW_REGULAR.getFont(4.3F);
      List<String> var10 = SectionPanel.wrapText(var7, var9, var5, 3);
      float var11 = var4;

      for (Object var13_o : (Iterable<?>)(Object)(var10)) {
         String var13 = (String)var13_o;
         var0.drawText(var9, var13, var3, var11, var8.method7294(var6));
         var11 += var9.height() + 2.0F;
      }

      return var11 - var4 + 4.0F;
   }

   private static int visibilityIndex(String var0) {
      for (int var1 = 0; var1 < VISIBILITY.length; var1++) {
         if (VISIBILITY[var1].equalsIgnoreCase(var0)) {
            return var1;
         }
      }

      return -1;
   }

   public boolean mouseClicked(double var1, double var3) {
      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         for (FormField var10 : this.fields) {
            BoundsData var7 = var10.bounds();
            var10.setFocused(var7 != null && var7.contains(var1, var3));
         }

         for (Entry var11 : this.buttons.entrySet()) {
            if (((BoundsData)var11.getValue()).contains(var1, var3)) {
               this.pressed.put((String)var11.getKey(), System.currentTimeMillis());
               this.action((String)var11.getKey());
               return true;
            }
         }

         return true;
      } else {
         for (FormField var6 : this.fields) {
            var6.setFocused(false);
         }

         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         this.scroller.add((float)var5 * 28.0F);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int var1, int var2) {
      for (FormField var4 : this.fields) {
         if (var4.isFocused()) {
            if (var4 != this.name || var1 != 257 && var1 != 335) {
               var4.keyPressed(var1, var2);
               return true;
            }

            this.save();
            return true;
         }
      }

      return false;
   }

   public boolean charTyped(char var1) {
      for (FormField var3 : this.fields) {
         if (var3.isFocused()) {
            return var3.charTyped(var1);
         }
      }

      return false;
   }

   private boolean confirm(String var1) {
      if (this.armed.equals(var1) && System.currentTimeMillis() - this.armedAt < 3000L) {
         this.armed = "";
         return true;
      } else {
         this.armed = var1;
         this.armedAt = System.currentTimeMillis();
         return false;
      }
   }

   private void action(String var1) {
      StoreApi.Data2 var2 = this.twin();
      this.statFile = null;
      if (var1.startsWith("load:")) {
         this.loadMode = Integer.parseInt(var1.substring(5));
      } else if (var1.startsWith("price:")) {
         this.priceIndex = Integer.parseInt(var1.substring(6));
      } else if (var1.startsWith("vis:") && var2 != null) {
         this.changeVisibility(var2, VISIBILITY[Integer.parseInt(var1.substring(4))]);
      } else if (var1.startsWith("copy:")) {
         for (StoreApi.Data var5 : this.codes) {
            if (var1.equals("copy:" + var5.getUUID()) && !var5.getText().isBlank()) {
               MinecraftClient.getInstance().keyboard.setClipboard(var5.getText());
               this.host.report(t("gui.scripts.market.status.copied"), false);
            }
         }
      } else {
         switch (var1) {
            case "close":
               this.host.close();
               break;
            case "save":
               this.save();
               break;
            case "apply":
               this.host.apply(this.fileName, this.loadMode);
               break;
            case "overwrite":
               Zenith.getInstance().getManager().method7779(this.fileName);
               this.host.report(t("gui.configs.report.saved") + " " + this.display, false);
               break;
            case "sync":
               ConfigSyncService.getInstance().method675();
               break;
            case "code":
               if (var2 != null) {
                  this.issueCode(var2);
               }
               break;
            case "market.submit":
               this.submit(var2);
               break;
            case "market.withdraw":
               this.withdraw(var2);
               break;
            case "folder":
               Util.getOperatingSystem().open(Manager.file);
               break;
            case "delete":
               if (this.confirm("delete")) {
                  Zenith.getInstance().getManager().method7786(this.fileName);
                  this.host.report(t("gui.configs.report.deleted") + " " + this.display, false);
                  this.host.close();
               }
         }

         this.host.changed();
      }
   }

   private void save() {
      Manager var1 = Zenith.getInstance().getManager();
      String var2 = this.name.get().strip();
      if (this.name.changed() && !var2.isEmpty() && !var2.equals(this.display)) {
         if (var1.method7787(this.display, var2)) {
            String var3 = "." + "Zenith".toLowerCase(Locale.ROOT);
            this.fileName = var2 + var3;
            this.display = var2;
            this.name.commit();
            this.host.report(t("gui.configs.report.renamed") + " " + var2, false);
         } else {
            this.host.report(t("gui.configs.report.renameFailed"), true);
         }
      }

      if (this.description.changed()) {
         StoreApi.Data2 var6 = this.twin();
         StoreApi var4 = store();
         if (var6 != null && var4 != null) {
            String var5 = this.description.get().strip();
            var4.method1974(var6.getUUID(), null, var5, null).whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  this.host.report(StoreApi.reason(var2x), true);
               } else {
                  ConfigSyncService.getInstance().method679(var1x);
                  this.host.report(t("gui.scripts.info.saved"), false);
               }

               this.host.changed();
            });
         }

         this.description.commit();
      }

      for (FormField var8 : this.fields) {
         var8.setFocused(false);
      }

      this.host.changed();
   }

   private void changeVisibility(StoreApi.Data2 var1, String var2) {
      StoreApi var3 = store();
      if (var3 != null) {
         var3.method1974(var1.getUUID(), null, null, var2).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               this.host.report(StoreApi.reason(var2x), true);
            } else {
               ConfigSyncService.getInstance().method679(var1x);
               this.host.report(t("gui.scripts.market.status.visibility"), false);
            }

            this.host.changed();
         });
      }
   }

   private void loadCodes(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         var2.method1980(var1.getUUID()).whenComplete((var1x, var2x) -> {
            if (var2x == null) {
               this.codes = Collections.unmodifiableList(new ArrayList<>(var1x));
               this.host.changed();
            }
         });
      }
   }

   private void issueCode(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         var2.method1979(var1.getUUID(), 1).whenComplete((var2x, var3) -> {
            if (var3 != null) {
               this.host.report(StoreApi.reason(var3), true);
            } else {
               if (!var2x.isEmpty()) {
                  String var4 = var2x.get(0).getText();
                  MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().keyboard.setClipboard(var4));
                  this.host.report(t("gui.scripts.market.status.codeCreated").replace("{n}", var4), false);
               }

               this.loadCodes(var1);
            }
         });
      }
   }

   private void submit(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null && !this.busy) {
         if (var1 == null) {
            this.host.report(t("gui.market.needCloud"), true);
            ConfigSyncService.getInstance().method675();
         } else {
            this.busy = true;
            int var3 = PRICES[this.priceIndex];
            var2.method1961(var1.getUUID(), var3).whenComplete((var2x, var3x) -> {
               this.busy = false;
               if (var3x != null) {
                  this.host.report(StoreApi.reason(var3x), true);
               } else {
                  ConfigSyncService.getInstance().method679(var2x);
                  this.host.report(t(var3 == 0 ? "gui.market.report.published" : "gui.market.report.review"), false);
               }

               this.host.changed();
            });
         }
      }
   }

   private void withdraw(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null && var1 != null && !this.busy) {
         this.busy = true;
         var2.method1962(var1.getUUID()).whenComplete((var1x, var2x) -> {
            this.busy = false;
            if (var2x != null) {
               this.host.report(StoreApi.reason(var2x), true);
            } else {
               ConfigSyncService.getInstance().method679(var1x);
               this.host.report(t("gui.market.report.withdrawn"), false);
            }

            this.host.changed();
         });
      }
   }

   public interface Host {
      void apply(String var1, int var2);

      void report(String var1, boolean var2);

      void close();

      void changed();
   }
}

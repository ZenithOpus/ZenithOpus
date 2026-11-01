package org.zenith.client.screens.scripts;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.panel.ConfigInfoPage;
import org.zenith.client.screens.nlgui.panel.SectionPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptInfoPage {
   private static final int[] PALETTE = new int[]{-7686913, -4809473, 36785, 40811, 53606, -8462175, -10562336, -2039584};
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private final Storage host;
   private final BiConsumer<String, Boolean> toast;
   private final Consumer<Script> reopen;
   private Script script;
   private final FormField name = new FormField(60, false);
   private final FormField author = new FormField(40, false);
   private final FormField link = new FormField(120, false);
   private final FormField tags = new FormField(80, false);
   private final FormField description = new FormField(120, true);
   private final FormField note = new FormField(120, false);
   private final FormField folder = new FormField(48, false);
   private final List<FormField> fields = List.of(this.name, this.author, this.link, this.tags, this.description, this.note, this.folder);
   private int color = -1;
   private int savedColor = -1;
   private final Map<String, BoundsData> buttons = new LinkedHashMap<>();
   private final Map<String, Animation> hover = new HashMap<>();
   private final List<Object[]> swatches = new ArrayList<>();
   private BoundsData avatarRect;
   private BoundsData bounds;
   private final Smooth scroller = new Smooth(16.0F);
   private final Map<String, Long> pressed = new HashMap<>();
   private String armed = "";
   private long armedAt;
   private final Animation appear = new Animation(320L, 0.0F, Easing.INSTANCE15);
   private Runnable onClose = () -> {};
   private int marketPrice;
   private volatile boolean marketBusy;
   private Script changesScript;
   private int changesLimit;
   private long changesAt;
   private List<String> changes = List.of();

   public void setOnClose(Runnable var1) {
      this.onClose = var1;
   }

   public Script script() {
      return this.script;
   }

   public void renderCompact(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      if (this.script != null) {
         float var10 = var8 * this.appear.method2808(1.0F);
         var4 += (1.0F - this.appear.getValue()) * 10.0F;
         this.bounds = new BoundsData(var4, var5, var6, var7);
         this.buttons.clear();
         this.swatches.clear();
         if (!this.armed.isEmpty() && System.currentTimeMillis() - this.armedAt > 3000L) {
            this.armed = "";
         }

         float var11 = this.scroller.update();
         float var12 = 8.0F;
         float var13 = var4 + var12;
         float var14 = var6 - var12 * 2.0F;
         Font var15 = Fonts.NEW_REGULAR.getFont(4.3F);
         BoundsData var16 = new BoundsData(var13 - 2.0F, var5 + 1.0F, 12.0F, 12.0F);
         boolean var17 = var16.contains(var2, var3);
         Font var18 = Fonts.NEW_MEDIUM.getFont(5.2F);
         var1.drawText(
            var18,
            "‹",
            var16.getFloatValue() + 3.0F,
            var16.getFloatValue2() + (12.0F - var18.height()) / 2.0F,
            (var17 ? var9.getTextEnable() : var9.getTextSecondary()).getZenithColor().method7294(var10)
         );
         this.buttons.put("close", var16);
         Font var19 = Fonts.NEW_MEDIUM.getFont(4.2F);
         var1.drawText(
            var19,
            t("gui.scripts.info.settings"),
            var13 + 12.0F,
            var5 + 1.0F + (12.0F - var19.height()) / 2.0F,
            var9.getTextTertiary().getZenithColor().method7294(var10)
         );
         if (this.isDirty()) {
            String var20 = t("gui.scripts.ws.save");
            float var21 = var19.width(var20) + 12.0F;
            BoundsData var22 = new BoundsData(var4 + var6 - var12 - var21, var5, var21, 13.0F);
            boolean var23 = var22.contains(var2, var3);
            var1.drawRoundedRect(
               var22.getFloatValue(),
               var22.getFloatValue2(),
               var22.width(),
               var22.height(),
               ProxyDotRadiusData.method8069(6.5F),
               var9.getPrimaryColor().getZenithColor().method7294(var10 * (var23 ? 1.0F : 0.8F))
            );
            var1.drawText(
               var19, var20, var22.getFloatValue() + 6.0F, var22.getFloatValue2() + (13.0F - var19.height()) / 2.0F, ZenithColor.INSTANCE.method7294(var10)
            );
            this.buttons.put("save", var22);
         }

         float var40 = var5 + 18.0F;
         MsdfRenderer.flushBatch();
         var1.enableScissor(var4, var40, var4 + var6, var5 + var7);
         float var41 = var40 + var11;
         card(var1, var9, var13, var41, var14, 44.0F, 0.0F, var10);
         float var60 = 32.0F;
         this.avatarRect = new BoundsData(var13 + 6.0F, var41 + 6.0F, var60, var60);
         boolean var24 = this.avatarRect.contains(var2, var3);
         float var25 = this.hot("avatar", var24);
         drawAvatar(var1, var9, this.script, this.avatarRect.getFloatValue(), this.avatarRect.getFloatValue2(), var60, var10, this.color);
         if (var25 > 0.01F) {
            var1.drawRoundedRect(
               this.avatarRect.getFloatValue(),
               this.avatarRect.getFloatValue2(),
               var60,
               var60,
               ProxyDotRadiusData.method8069(var60 * 0.28F),
               new ZenithColor(0, 0, 0, 150).method7294(var10 * var25)
            );
            Font var26 = Fonts.NEW_MEDIUM.getFont(4.0F);
            String var27 = t("gui.scripts.info.avatarChange");
            var1.drawText(
               var26,
               var27,
               this.avatarRect.getFloatValue() + (var60 - var26.width(var27)) / 2.0F,
               this.avatarRect.getFloatValue2() + (var60 - var26.height()) / 2.0F,
               ZenithColor.INSTANCE.method7294(var10 * var25)
            );
         }

         Font var61 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         float var62 = this.avatarRect.getFloatValue() + var60 + 6.0F;
         float var28 = var13 + var14 - var62 - 4.0F;
         var1.drawText(
            var61,
            var61.clip(this.name.get().isEmpty() ? this.script.getId() : this.name.get(), var28),
            var62,
            var41 + 9.0F,
            var9.getTextEnable().getZenithColor().method7294(var10)
         );
         String var29 = (this.script.getVersion().isBlank() ? "" : "v" + this.script.getVersion() + "  ·  ")
            + (this.author.get().isEmpty() ? this.script.getId() : this.author.get());
         var1.drawText(var15, var15.clip(var29, var28), var62, var41 + 11.0F + var61.height(), var9.getTextTertiary().getZenithColor().method7294(var10));
         var41 += 50.0F;
         float var30 = (var14 - 4.0F) / 2.0F;
         this.button(var1, var9, "avatar.upload", t("gui.scripts.info.upload"), MenuGlyph.ACTION, var13, var41, var30, var2, var3, var10, 1);
         this.button(var1, var9, "avatar.remove", t("gui.scripts.info.removeAvatar"), null, var13 + var30 + 4.0F, var41, var30, var2, var3, var10, 0);
         var41 += 20.0F;
         float var31 = (var14 - 20.0F) / 9.0F;

         for (int var32 = 0; var32 <= PALETTE.length; var32++) {
            int var33 = var32 == PALETTE.length ? -1 : PALETTE[var32];
            float var34 = var13 + var32 * (var31 + 2.5F);
            ZenithColor var35 = var33 == -1 ? var9.getPrimaryColor().getZenithColor() : new ZenithColor(var33);
            var1.drawRoundedRect(
               var34, var41, var31, var31, ProxyDotRadiusData.method8069(var31 / 2.0F), var35.method7294(var10 * (var33 == -1 ? 0.35F : 0.9F))
            );
            float var36 = this.hot("ring:" + var33, var33 == this.color);
            if (var36 > 0.01F) {
               float var37 = 1.2F + (1.0F - var36) * 2.5F;
               var1.drawRoundedBorder(
                  var34 - var37,
                  var41 - var37,
                  var31 + var37 * 2.0F,
                  var31 + var37 * 2.0F,
                  0.2F,
                  ProxyDotRadiusData.method8069((var31 + var37 * 2.0F) / 2.0F),
                  var9.getTextEnable().getZenithColor().method7294(var10 * var36)
               );
            }

            float var71 = Smooth.press(this.pressed.getOrDefault("swatch:" + var33, 0L), 300L);
            if (var71 > 0.01F) {
               var1.drawRoundedRect(
                  var34 - var71 * 2.0F,
                  var41 - var71 * 2.0F,
                  var31 + var71 * 4.0F,
                  var31 + var71 * 4.0F,
                  ProxyDotRadiusData.method8069(var31 / 2.0F + var71 * 2.0F),
                  var35.method7292(0.35F * var71).method7294(var10)
               );
            }

            this.swatches.add(new Object[]{new BoundsData(var34, var41, var31, var31), var33});
         }

         var41 += var31 + 9.0F;
         var41 += this.field(var1, var9, "gui.scripts.info.name", this.name, "gui.scripts.info.name.hint", var13, var41, var14, var10);
         var41 += this.field(var1, var9, "gui.scripts.info.author", this.author, "gui.scripts.info.author.hint", var13, var41, var14, var10);
         var41 += this.field(var1, var9, "gui.scripts.info.description", this.description, "gui.scripts.info.description.hint", var13, var41, var14, var10);
         var41 += this.field(var1, var9, "gui.scripts.info.tags", this.tags, "gui.scripts.info.tags.hint", var13, var41, var14, var10);
         var41 += this.field(var1, var9, "gui.scripts.info.link", this.link, "gui.scripts.info.link.hint", var13, var41, var14, var10);
         caption(
            var1,
            var9,
            t("gui.scripts.info.caption.version") + "  ·  v" + (this.script.getVersion().isBlank() ? "-" : this.script.getVersion()),
            var13,
            var41,
            var10
         );
         var41 += 9.0F;
         float var63 = (var14 - 6.0F) / 3.0F;
         this.button(var1, var9, "bump.2", t("gui.scripts.info.bumpPatch"), null, var13, var41, var63, var2, var3, var10, 1);
         this.button(var1, var9, "bump.1", t("gui.scripts.info.bumpMinor"), null, var13 + var63 + 3.0F, var41, var63, var2, var3, var10, 1);
         this.button(var1, var9, "bump.0", t("gui.scripts.info.bumpMajor"), null, var13 + (var63 + 3.0F) * 2.0F, var41, var63, var2, var3, var10, 1);
         var41 += 20.0F;
         var41 += this.field(var1, var9, "gui.scripts.info.note", this.note, "gui.scripts.info.note.hint", var13, var41, var14, var10);
         List<String> var64 = this.changes(6);
         if (!var64.isEmpty()) {
            float var65 = var64.size() * (var15.height() + 3.0F) + 8.0F;
            card(var1, var9, var13, var41, var14, var65, 0.0F, var10);
            float var67 = var41 + 5.0F;

            for (Object var72_o : (Iterable<?>)(Object)(var64)) {
               String var72 = (String)var72_o;
               boolean var38 = var72.startsWith("## ");
               var1.drawText(
                  var15,
                  var15.clip(var38 ? var72.substring(3) : "  " + var72.substring(2), var14 - 10.0F),
                  var13 + 5.0F,
                  var67,
                  (var38 ? var9.getTextEnable() : var9.getTextSecondary()).getZenithColor().method7294(var10)
               );
               var67 += var15.height() + 3.0F;
            }

            var41 += var65 + 8.0F;
         }

         var41 += this.marketSection(var1, var9, var13, var41, var14, var2, var3, var10);
         caption(var1, var9, t("gui.scripts.info.caption.manage"), var13, var41, var10);
         var41 += 9.0F;
         this.button(var1, var9, "duplicate", t("gui.scripts.info.duplicate"), MenuGlyph.COPY, var13, var41, var30, var2, var3, var10, 0);
         this.button(var1, var9, "export", t("gui.scripts.info.export"), MenuGlyph.STORAGE, var13 + var30 + 4.0F, var41, var30, var2, var3, var10, 0);
         var41 += 20.0F;
         this.button(var1, var9, "resetSettings", t("gui.scripts.info.resetSettings"), MenuGlyph.RESET, var13, var41, var30, var2, var3, var10, 0);
         this.button(var1, var9, "resetStorage", t("gui.scripts.info.resetStorage"), MenuGlyph.RESET, var13 + var30 + 4.0F, var41, var30, var2, var3, var10, 0);
         var41 += 20.0F;
         this.button(var1, var9, "folder", t("gui.scripts.openFolder"), MenuGlyph.STORAGE, var13, var41, var30, var2, var3, var10, 0);
         this.button(var1, var9, "delete", t("gui.scripts.info.delete"), MenuGlyph.CLOSE, var13 + var30 + 4.0F, var41, var30, var2, var3, var10, 2);
         var41 += 22.0F;
         if (this.script.getPath() != null) {
            var41 += this.field(var1, var9, "gui.scripts.info.folderName", this.folder, "gui.scripts.info.folderName.hint", var13, var41, var14, var10);
            this.button(var1, var9, "rename", t("gui.scripts.info.rename"), null, var13, var41 - 4.0F, var14, var2, var3, var10, 1);
            var41 += 18.0F;
         }

         MsdfRenderer.flushBatch();
         var1.disableScissor();
         float var66 = var41 - var41 + 6.0F;
         float var68 = var5 + var7 - var40;
         this.scroller.clamp(Math.min(0.0F, var68 - var66), 0.0F);
         if (var66 > var68 + 1.0F) {
            float var70 = Math.max(14.0F, var68 * var68 / var66);
            float var73 = var40 + -var11 / (var66 - var68) * (var68 - var70);
            var1.drawRoundedRect(
               var4 + var6 - 3.0F, var73, 1.5F, var70, ProxyDotRadiusData.method8069(0.75F), var9.getTextTertiary().getZenithColor().method7294(var10 * 0.5F)
            );
         }

         if (var24) {
            HoverInfo.request(t("gui.scripts.info.avatar"), t("gui.scripts.info.avatar.desc"));
         }
      }
   }

   private float marketSection(MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, int var6, int var7, float var8) {
      caption(var1, var2, t("gui.market.caption.publish"), var3, var4, var8);
      var4 += 9.0F;
      Font var10 = Fonts.NEW_REGULAR.getFont(4.3F);
      OriginUtil.Data2 var11 = OriginUtil.method6921(this.script);
      if (var11 != null && !var11.mine()) {
         StoreApi.Data5 var22 = StoreApi.method1960(var11.getUUID());
         String var23 = t(var22.isFlag() ? "gui.market.script.sealedForeign" : "gui.market.script.foreign");

         for (String var25 : SectionPanel.wrapText(var23, var10, var5, 3)) {
            var1.drawText(var10, var25, var3, var4, var2.getTextTertiary().getZenithColor().method7294(var8));
            var4 += var10.height() + 2.0F;
         }

         return var4 - var4 + 8.0F;
      } else {
         StoreApi.Data5 var12 = var11 == null ? StoreApi.Data5.INSTANCE : StoreApi.method1960(var11.getUUID());
         var4 += ConfigInfoPage.marketStatus(var1, var2, var12, var3, var4, var5, var8);
         if (!var12.pending() && !var12.method2007()) {
            int[] var13 = ConfigInfoPage.PRICES;
            float var14 = 3.0F;
            float var15 = (var5 - var14 * (var13.length - 1)) / var13.length;

            for (int var16 = 0; var16 < var13.length; var16++) {
               String var17 = var13[var16] == 0 ? t("gui.market.price.free") : var13[var16] + " ₽";
               this.button(
                  var1, var2, "mprice:" + var16, var17, null, var3 + var16 * (var15 + var14), var4, var15, var6, var7, var8, var16 == this.marketPrice ? 1 : 0
               );
            }

            var4 += 20.0F;
            this.button(
               var1,
               var2,
               "market.submit",
               this.marketBusy ? t("gui.scripts.market.busy") : t("gui.market.publish.review"),
               MenuGlyph.STORAGE,
               var3,
               var4,
               var5,
               var6,
               var7,
               var8,
               1
            );
            var4 += 20.0F;

            for (String var27 : SectionPanel.wrapText(t("gui.market.script.hint"), var10, var5, 3)) {
               var1.drawText(var10, var27, var3, var4, var2.getTextTertiary().getZenithColor().method7294(var8));
               var4 += var10.height() + 2.0F;
            }
         } else {
            this.button(
               var1,
               var2,
               "market.withdraw",
               this.marketBusy ? t("gui.scripts.market.busy") : t("gui.market.withdraw"),
               MenuGlyph.CLOSE,
               var3,
               var4,
               var5,
               var6,
               var7,
               var8,
               0
            );
            var4 += 20.0F;
         }

         return var4 - var4 + 8.0F;
      }
   }

   private void submitToMarket() {
      StoreApi var1 = Zenith.getInstance().getStoreApi();
      if (var1 != null && !this.marketBusy && this.script != null) {
         if (this.script.getPath() == null) {
            this.toast.accept(t("gui.market.script.folderOnly"), true);
         } else {
            Script var2 = this.script;
            int var3 = ConfigInfoPage.PRICES[Math.max(0, Math.min(ConfigInfoPage.PRICES.length - 1, this.marketPrice))];
            OriginUtil.Data2 var4 = OriginUtil.method6921(var2);
            this.marketBusy = true;
            var var5 = var4 != null && var4.mine() ? var1.method1968("script").thenApply(var1x -> {
               for (StoreApi.Data2 var3x : var1x) {
                  if (var3x.getUUID().equals(var4.getUUID())) {
                     boolean var4x = StoreApi.method1960(var3x.getUUID()).method2007();
                     return "PUBLIC".equals(var3x.getText7()) && !var4x ? "PRIVATE" : var3x.getText7();
                  }
               }

               return "PRIVATE";
            }) : CompletableFuture.completedFuture("PRIVATE");
            var5.<StoreApi.Data2>thenCompose(var3x -> OriginUtil.method6933(var1, this.host, var2, var3x))
               .thenCompose(var2x -> var1.method1961(var2x.getUUID(), var3))
               .whenComplete((var1x, var2x) -> MinecraftClient.getInstance().execute(() -> {
                  this.marketBusy = false;
                  if (var2x != null) {
                     this.toast.accept(StoreApi.reason(var2x), true);
                  } else {
                     this.toast.accept(t("gui.market.report.review"), false);
                  }
               }));
         }
      }
   }

   private void withdrawFromMarket() {
      StoreApi var1 = Zenith.getInstance().getStoreApi();
      OriginUtil.Data2 var2 = this.script == null ? null : OriginUtil.method6921(this.script);
      if (var1 != null && var2 != null && !this.marketBusy) {
         this.marketBusy = true;
         var1.method1962(var2.getUUID()).whenComplete((var1x, var2x) -> MinecraftClient.getInstance().execute(() -> {
            this.marketBusy = false;
            this.toast.accept(var2x != null ? StoreApi.reason(var2x) : t("gui.market.report.withdrawn"), var2x != null);
         }));
      }
   }

   public ScriptInfoPage(Storage var1, BiConsumer<String, Boolean> var2, Consumer<Script> var3) {
      this.host = var1;
      this.toast = var2;
      this.reopen = var3;
   }

   private List<String> changes(int var1) {
      long var2 = System.currentTimeMillis();
      if (this.changesScript != this.script || this.changesLimit != var1 || var2 - this.changesAt >= 1000L) {
         this.changes = this.host.method1352(this.script, var1);
         this.changesScript = this.script;
         this.changesLimit = var1;
         this.changesAt = var2;
      }

      return this.changes;
   }

   public void open(Script var1) {
      this.script = var1;
      this.changesScript = null;
      this.name.set(var1.method1403().equals(var1.getId()) && var1.getVersion().isBlank() ? var1.method1403() : var1.method1403());
      this.author.set(var1.getText2());
      this.link.set(var1.getText4());
      this.tags.set(var1.getText3());
      this.description.set(var1.getDescription());
      this.note.set("");
      this.folder.set(var1.getId());
      this.color = var1.getColor();
      this.savedColor = this.color;
      this.scroller.reset(0.0F);
      this.armed = "";
      this.appear.reset(0.0F);
      this.appear.method2810(1.0F);

      for (FormField var3 : this.fields) {
         var3.setFocused(false);
      }

      this.marketPrice = 0;
      OriginUtil.Data2 var4 = OriginUtil.method6921(var1);
      StoreApi var5 = Zenith.getInstance().getStoreApi();
      if (var4 != null && var5 != null) {
         var5.method1968("script").exceptionally(var0 -> List.of());
         var5.method1969("LIBRARY", "script").exceptionally(var0 -> List.of());
      }
   }

   public boolean isDirty() {
      return this.script != null
         && (
            this.name.changed()
               || this.author.changed()
               || this.link.changed()
               || this.tags.changed()
               || this.description.changed()
               || this.color != this.savedColor
         );
   }

   public boolean isTyping() {
      for (FormField var2 : this.fields) {
         if (var2.isFocused()) {
            return true;
         }
      }

      return false;
   }

   public void apply() {
      if (this.script != null) {
         LinkedHashMap var1 = new LinkedHashMap();
         var1.put("name", this.name.get());
         var1.put("author", this.author.get());
         var1.put("description", this.description.get());
         var1.put("tags", this.tags.get());
         var1.put("link", this.link.get());
         var1.put("color", this.color == -1 ? "" : String.format("#%06X", this.color & 16777215));

         try {
            this.host.method1350(this.script, var1);

            for (FormField var3 : List.of(this.name, this.author, this.link, this.tags, this.description)) {
               var3.commit();
            }

            this.savedColor = this.color;
            this.toast.accept(t("gui.scripts.info.saved"), false);
         } catch (Exception var4) {
            this.toast.accept(t("gui.scripts.toast.saveFailed") + " " + var4.getMessage(), true);
         }
      }
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private float hot(String var1, boolean var2) {
      return this.hover.computeIfAbsent(var1, var0 -> new Animation(150L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private static void card(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      var0.drawRoundedRect(
         var2,
         var3,
         var4,
         var5,
         var8,
         var1.getFieldSurfaceBackground().getZenithColor().method7295(var1.getDisableActiveBg().getZenithColor(), 0.35F + var6 * 0.55F).method7294(var7)
      );
      var0.drawRoundedBorder(var2, var3, var4, var5, 0.15F, var8, var1.getFieldBorder().getZenithColor().method7294(2.0F + var6 * 2.0F).method7294(var7));
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
         var21,
         var7 + (16.0F - var23.height()) / 2.0F,
         (var12 > 0 ? var16 : var2.getTextSecondary().getZenithColor().method7295(var2.getTextEnable().getZenithColor(), var15)).method7294(var11)
      );
      this.buttons.put(var3, var13);
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

   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      if (this.script != null) {
         float var10 = var8 * this.appear.method2808(1.0F);
         this.bounds = new BoundsData(var4, var5, var6, var7);
         this.buttons.clear();
         this.swatches.clear();
         if (!this.armed.isEmpty() && System.currentTimeMillis() - this.armedAt > 3000L) {
            this.armed = "";
         }

         float var11 = this.scroller.update();
         MsdfRenderer.flushBatch();
         var1.enableScissor(var4, var5, var4 + var6, var5 + var7);
         float var12 = 112.0F;
         float var13 = var4;
         float var14 = var5 + var11;
         card(var1, var9, var4, var14, var12, 80.0F, 0.0F, var10);
         float var16 = 42.0F;
         float var17 = var4 + (var12 - var16) / 2.0F;
         float var18 = var14 + 9.0F;
         this.avatarRect = new BoundsData(var17, var18, var16, var16);
         boolean var19 = this.avatarRect.contains(var2, var3);
         float var20 = this.hot("avatar", var19);
         drawAvatar(var1, var9, this.script, var17, var18, var16, var10, this.color);
         if (var20 > 0.01F) {
            var1.drawRoundedRect(
               var17, var18, var16, var16, ProxyDotRadiusData.method8069(var16 * 0.28F), new ZenithColor(0, 0, 0, 150).method7294(var10 * var20)
            );
            Font var21 = Fonts.NEW_MEDIUM.getFont(4.8F);
            String var22 = t("gui.scripts.info.avatarChange");
            var1.drawText(
               var21,
               var22,
               var17 + (var16 - var21.width(var22)) / 2.0F,
               var18 + (var16 - var21.height()) / 2.0F,
               ZenithColor.INSTANCE.method7294(var10 * var20)
            );
         }

         Font var50 = Fonts.NEW_SEMIBOLD.getFont(5.6F);
         String var51 = var50.clip(this.name.get().isEmpty() ? this.script.getId() : this.name.get(), var12 - 12.0F);
         var1.drawText(var50, var51, var4 + (var12 - var50.width(var51)) / 2.0F, var18 + var16 + 6.0F, var9.getTextEnable().getZenithColor().method7294(var10));
         Font var23 = Fonts.NEW_REGULAR.getFont(4.3F);
         String var24 = (this.script.getVersion().isBlank() ? "" : "v" + this.script.getVersion() + "  ·  ")
            + (this.author.get().isEmpty() ? this.script.getId() : this.author.get());
         var24 = var23.clip(var24, var12 - 12.0F);
         var1.drawText(
            var23,
            var24,
            var4 + (var12 - var23.width(var24)) / 2.0F,
            var18 + var16 + 8.0F + var50.height(),
            var9.getTextTertiary().getZenithColor().method7294(var10)
         );
         var14 += 86.0F;
         float var25 = (var12 - 4.0F) / 2.0F;
         this.button(var1, var9, "avatar.upload", t("gui.scripts.info.upload"), MenuGlyph.ACTION, var4, var14, var25, var2, var3, var10, 1);
         this.button(var1, var9, "avatar.remove", t("gui.scripts.info.removeAvatar"), null, var4 + var25 + 4.0F, var14, var25, var2, var3, var10, 0);
         var14 += 22.0F;
         caption(var1, var9, t("gui.scripts.info.color"), var4, var14, var10);
         var14 += 9.0F;
         float var26 = (var12 - 20.0F) / 9.0F;

         for (int var27 = 0; var27 <= PALETTE.length; var27++) {
            int var28 = var27 == PALETTE.length ? -1 : PALETTE[var27];
            float var29 = var13 + var27 * (var26 + 2.5F);
            BoundsData var30 = new BoundsData(var29, var14, var26, var26);
            boolean var31 = var28 == this.color;
            ZenithColor var32 = var28 == -1 ? var9.getPrimaryColor().getZenithColor() : new ZenithColor(var28);
            var1.drawRoundedRect(
               var29, var14, var26, var26, ProxyDotRadiusData.method8069(var26 / 2.0F), var32.method7294(var10 * (var28 == -1 ? 0.35F : 0.9F))
            );
            if (var28 == -1) {
               Font var33 = Fonts.NEW_MEDIUM.getFont(4.0F);
               var1.drawText(
                  var33,
                  "A",
                  var29 + (var26 - var33.width("A")) / 2.0F,
                  var14 + (var26 - var33.height()) / 2.0F,
                  var9.getTextEnable().getZenithColor().method7294(var10)
               );
            }

            float var69 = this.hot("ring:" + var28, var31);
            if (var69 > 0.01F) {
               float var34 = 1.5F + (1.0F - var69) * 2.5F;
               var1.drawRoundedBorder(
                  var29 - var34,
                  var14 - var34,
                  var26 + var34 * 2.0F,
                  var26 + var34 * 2.0F,
                  0.2F,
                  ProxyDotRadiusData.method8069((var26 + var34 * 2.0F) / 2.0F),
                  var9.getTextEnable().getZenithColor().method7294(var10 * var69)
               );
            }

            this.swatches.add(new Object[]{var30, var28});
         }

         var14 += var26 + 10.0F;
         caption(var1, var9, t("gui.scripts.info.preview"), var13, var14, var10);
         var14 += 9.0F;
         card(var1, var9, var13, var14, var12, 26.0F, 0.0F, var10);
         drawAvatar(var1, var9, this.script, var13 + 5.0F, var14 + 4.0F, 18.0F, var10, this.color);
         Font var53 = Fonts.NEW_MEDIUM.getFont(5.0F);
         var1.drawText(
            var53,
            var53.clip(this.name.get().isEmpty() ? this.script.getId() : this.name.get(), var12 - 36.0F),
            var13 + 28.0F,
            var14 + 5.0F,
            var9.getTextEnable().getZenithColor().method7294(var10)
         );
         var1.drawText(
            var23,
            var23.clip(this.description.get().isEmpty() ? t("gui.scripts.info.noDescription") : this.description.get(), var12 - 36.0F),
            var13 + 28.0F,
            var14 + 7.0F + var53.height(),
            var9.getTextTertiary().getZenithColor().method7294(var10)
         );
         float var54 = var14 + 26.0F;
         float var55 = var4 + var12 + 10.0F;
         float var56 = var6 - var12 - 10.0F;
         float var57 = var5 + var11;
         caption(var1, var9, t("gui.scripts.info.caption.main"), var55, var57, var10);
         var57 += 10.0F;
         float var68 = (var56 - 8.0F) / 2.0F;
         float var70 = this.field(var1, var9, "gui.scripts.info.name", this.name, "gui.scripts.info.name.hint", var55, var57, var68, var10);
         this.field(var1, var9, "gui.scripts.info.author", this.author, "gui.scripts.info.author.hint", var55 + var68 + 8.0F, var57, var68, var10);
         var57 += var70;
         var57 += this.field(var1, var9, "gui.scripts.info.description", this.description, "gui.scripts.info.description.hint", var55, var57, var56, var10);
         float var71 = this.field(var1, var9, "gui.scripts.info.tags", this.tags, "gui.scripts.info.tags.hint", var55, var57, var68, var10);
         this.field(var1, var9, "gui.scripts.info.link", this.link, "gui.scripts.info.link.hint", var55 + var68 + 8.0F, var57, var68, var10);
         var57 += var71 + 2.0F;
         caption(var1, var9, t("gui.scripts.info.caption.version"), var55, var57, var10);
         var57 += 10.0F;
         Font var35 = Fonts.NEW_SEMIBOLD.getFont(7.0F);
         String var36 = "v" + (this.script.getVersion().isBlank() ? "—" : this.script.getVersion());
         var1.drawText(var35, var36, var55, var57 + 3.0F, var9.getTextEnable().getZenithColor().method7294(var10));
         float var37 = var55 + var35.width(var36) + 10.0F;
         float var38 = (var55 + var56 - var37 - 8.0F) / 3.0F;
         this.button(var1, var9, "bump.2", t("gui.scripts.info.bumpPatch"), null, var37, var57, var38, var2, var3, var10, 1);
         this.button(var1, var9, "bump.1", t("gui.scripts.info.bumpMinor"), null, var37 + var38 + 4.0F, var57, var38, var2, var3, var10, 1);
         this.button(var1, var9, "bump.0", t("gui.scripts.info.bumpMajor"), null, var37 + (var38 + 4.0F) * 2.0F, var57, var38, var2, var3, var10, 1);
         var57 += 22.0F;
         var57 += this.field(var1, var9, "gui.scripts.info.note", this.note, "gui.scripts.info.note.hint", var55, var57, var56, var10);
         List<String> var39 = this.changes(8);
         if (!var39.isEmpty()) {
            float var40 = var39.size() * (var23.height() + 3.0F) + 8.0F;
            card(var1, var9, var55, var57, var56, var40, 0.0F, var10);
            float var41 = var57 + 5.0F;

            for (Object var43_o : (Iterable<?>)(Object)(var39)) {
               String var43 = (String)var43_o;
               boolean var44 = var43.startsWith("## ");
               var1.drawText(
                  var44 ? Fonts.NEW_MEDIUM.getFont(4.5F) : var23,
                  var23.clip(var44 ? var43.substring(3) : "  " + var43.substring(2), var56 - 12.0F),
                  var55 + 6.0F,
                  var41,
                  (var44 ? var9.getTextEnable() : var9.getTextSecondary()).getZenithColor().method7294(var10)
               );
               var41 += var23.height() + 3.0F;
            }

            var57 += var40 + 8.0F;
         }

         caption(var1, var9, t("gui.scripts.info.caption.manage"), var55, var57, var10);
         var57 += 10.0F;
         float var72 = (var56 - 8.0F) / 3.0F;
         this.button(var1, var9, "duplicate", t("gui.scripts.info.duplicate"), MenuGlyph.COPY, var55, var57, var72, var2, var3, var10, 0);
         this.button(var1, var9, "export", t("gui.scripts.info.export"), MenuGlyph.STORAGE, var55 + var72 + 4.0F, var57, var72, var2, var3, var10, 0);
         this.button(var1, var9, "folder", t("gui.scripts.openFolder"), MenuGlyph.STORAGE, var55 + (var72 + 4.0F) * 2.0F, var57, var72, var2, var3, var10, 0);
         var57 += 20.0F;
         this.button(var1, var9, "resetSettings", t("gui.scripts.info.resetSettings"), MenuGlyph.RESET, var55, var57, var72, var2, var3, var10, 0);
         this.button(var1, var9, "resetStorage", t("gui.scripts.info.resetStorage"), MenuGlyph.RESET, var55 + var72 + 4.0F, var57, var72, var2, var3, var10, 0);
         this.button(var1, var9, "delete", t("gui.scripts.info.delete"), MenuGlyph.CLOSE, var55 + (var72 + 4.0F) * 2.0F, var57, var72, var2, var3, var10, 2);
         var57 += 22.0F;
         if (this.script.getPath() != null) {
            float var73 = this.field(
               var1, var9, "gui.scripts.info.folderName", this.folder, "gui.scripts.info.folderName.hint", var55, var57, var56 - 80.0F, var10
            );
            this.button(var1, var9, "rename", t("gui.scripts.info.rename"), null, var55 + var56 - 76.0F, var57 + 8.0F, 76.0F, var2, var3, var10, 1);
            var57 += var73;
         }

         MsdfRenderer.flushBatch();
         var1.disableScissor();
         float var74 = Math.max(var54, var57) - var14 + 10.0F;
         this.scroller.clamp(Math.min(0.0F, var7 - var74), 0.0F);
         if (var19) {
            HoverInfo.request(t("gui.scripts.info.avatar"), t("gui.scripts.info.avatar.desc"));
         }
      }
   }

   public static void drawAvatar(MenuDrawContext var0, ZenithStyle var1, Script var2, float var3, float var4, float var5, float var6, int var7) {
      Identifier var8 = ScriptIcons.get(var2);
      ProxyDotRadiusData var9 = ProxyDotRadiusData.method8069(var5 * 0.28F);
      if (var8 != null) {
         var0.drawRoundedTexture(var8, var3, var4, var5, var5, var9, ZenithColor.INSTANCE.method7294(var6));
      } else {
         int var10 = var7 != -2 ? var7 : var2.getColor();
         ZenithColor var11 = var2.getType2() == Script.Type2.FAILED ? ERROR : (var10 == -1 ? var1.getPrimaryColor().getZenithColor() : new ZenithColor(var10));
         var0.drawRoundedRect(var3, var4, var5, var5, var9, var11.method7292(0.16F).method7294(var6));
         Font var12 = Fonts.NEW_SEMIBOLD.getFont(var5 * 0.38F);
         String var13 = var2.method1403();
         String var14 = var13.isEmpty() ? "?" : var13.substring(0, 1).toUpperCase();
         var0.drawText(var12, var14, var3 + (var5 - var12.width(var14)) / 2.0F, var4 + (var5 - var12.height()) / 2.0F, var11.method7294(var6));
      }
   }

   private void flash(String var1) {
      this.pressed.put(var1, System.currentTimeMillis());
   }

   public boolean mouseClicked(double var1, double var3) {
      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         for (FormField var11 : this.fields) {
            BoundsData var7 = var11.bounds();
            var11.setFocused(var7 != null && var7.contains(var1, var3));
         }

         if (this.avatarRect != null && this.avatarRect.contains(var1, var3)) {
            this.flash("avatar");
            this.pickAvatar();
            return true;
         } else {
            for (Object[] var12 : this.swatches) {
               if (((BoundsData)var12[0]).contains(var1, var3)) {
                  this.color = (Integer)var12[1];
                  this.flash("swatch:" + var12[1]);
                  return true;
               }
            }

            for (Entry var13 : this.buttons.entrySet()) {
               if (((BoundsData)var13.getValue()).contains(var1, var3)) {
                  this.flash((String)var13.getKey());
                  this.action((String)var13.getKey());
                  return true;
               }
            }

            return true;
         }
      } else {
         for (FormField var6 : this.fields) {
            var6.setFocused(false);
         }

         return false;
      }
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
      try {
         if (var1.startsWith("mprice:")) {
            this.marketPrice = Integer.parseInt(var1.substring(7));
            return;
         }

         switch (var1) {
            case "market.submit":
               this.submitToMarket();
               break;
            case "market.withdraw":
               this.withdrawFromMarket();
               break;
            case "close":
               this.onClose.run();
               break;
            case "save":
               this.apply();
               break;
            case "avatar.upload":
               this.pickAvatar();
               break;
            case "avatar.remove":
               this.host.method1354(this.script);
               ScriptIcons.invalidate(this.script);
               this.toast.accept(t("gui.scripts.info.avatarRemoved"), false);
               break;
            case "bump.0":
            case "bump.1":
            case "bump.2":
               if (this.isDirty()) {
                  this.apply();
               }

               String var8 = this.host.method1351(this.script, var1.charAt(5) - '0', this.note.get());
               this.changesScript = null;
               this.note.set("");
               this.toast.accept(t("gui.scripts.info.bumped").replace("{v}", var8), false);
               break;
            case "duplicate":
               Script var7 = this.host.method1355(this.script);
               this.toast.accept(t("gui.scripts.info.duplicated"), false);
               if (var7 != null) {
                  this.reopen.accept(var7);
               }
               break;
            case "export":
               Path var6 = this.host.method1360(this.script);
               Util.getOperatingSystem().open(var6.getParent().toFile());
               this.toast.accept(t("gui.scripts.info.exported").replace("{f}", var6.getFileName().toString()), false);
               break;
            case "folder":
               Util.getOperatingSystem().open((this.script.getPath() == null ? this.script.getPath2().getParent() : this.script.getPath()).toFile());
               break;
            case "resetSettings":
               if (!this.confirm(var1)) {
                  return;
               }

               this.host.method1359(this.script);
               this.toast.accept(t("gui.scripts.info.settingsReset"), false);
               break;
            case "resetStorage":
               if (!this.confirm(var1)) {
                  return;
               }

               this.host.method1358(this.script);
               this.toast.accept(t("gui.scripts.info.storageReset"), false);
               break;
            case "delete":
               if (!this.confirm(var1)) {
                  return;
               }

               this.host.method1357(this.script);
               this.toast.accept(t("gui.scripts.info.deleted"), false);
               this.reopen.accept(null);
               break;
            case "rename":
               Script var4 = this.host.method1356(this.script, this.folder.get());
               this.toast.accept(t("gui.scripts.info.renamed"), false);
               this.reopen.accept(var4);
         }
      } catch (Exception var5) {
         this.toast.accept(var5.getMessage(), true);
      }
   }

   private void pickAvatar() {
      Script var1 = this.script;
      Thread var2 = new Thread(() -> {
         String var2x;
         try {
            MemoryStack var3 = MemoryStack.stackPush();

            try {
               PointerBuffer var4 = var3.mallocPointer(1);
               var4.put(var3.UTF8("*.png"));
               var4.flip();
               var2x = TinyFileDialogs.tinyfd_openFileDialog(t("gui.scripts.info.pickTitle"), null, var4, "PNG", false);
            } catch (Throwable var7) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (Throwable var8) {
            var2x = null;
            MinecraftClient.getInstance().execute(() -> {
               this.toast.accept(t("gui.scripts.info.pickFallback"), true);
               if (var1.getPath() != null) {
                  Util.getOperatingSystem().open(var1.getPath().toFile());
               }
            });
         }

         String var9 = var2x;
         if (var9 != null) {
            MinecraftClient.getInstance().execute(() -> {
               try {
                  this.host.method1353(var1, Path.of(var9));
                  ScriptIcons.invalidate(var1);
                  this.toast.accept(t("gui.scripts.info.avatarSet"), false);
               } catch (Exception var4x) {
                  this.toast.accept(var4x.getMessage(), true);
               }
            });
         }
      }, "Zenith-ScriptIconPicker");
      var2.setDaemon(true);
      var2.start();
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
            var4.keyPressed(var1, var2);
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
}

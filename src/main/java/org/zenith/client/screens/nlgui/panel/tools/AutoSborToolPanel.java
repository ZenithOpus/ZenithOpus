package org.zenith.client.screens.nlgui.panel.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.item.ItemStack;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.AutoInventoryModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.InventoryPreset;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.SborList;
import org.zenith.utility.api.ServerNameNormalizer;
import org.zenith.utility.game.ItemEntry;
import org.zenith.utility.game.ItemMatcher;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.HudBatchState;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class AutoSborToolPanel extends ToolTab {
   private static final int LIST_ITEMS = 0;
   private static final int LIST_HISTORY = 1;
   private static final float SLOT_GAP = 3.0F;
   private static final int COLUMNS = 9;
   private static final int ROWS = 4;
   private static final float ICON_SCALE = 0.65F;
   private static final float SET_HEAD = 22.0F;
   private static final float INPUT_HEIGHT = 18.0F;
   private static final float BUTTON_HEIGHT = 17.0F;
   private static final float HINT_HEIGHT = 13.0F;
   private static final float SECTION_HEAD = 20.0F;
   private static final float CATALOG_ROW = 22.0F;
   private static final float PRESET_ROW = 22.0F;
   private static final float START_HEIGHT = 22.0F;
   private static final float RUN_CARD = 74.0F;
   private static final float SLOT_CARD = 34.0F;
   private static final float STEPPER_ROW = 22.0F;
   private static final float STEPPER_BUTTON = 11.0F;
   private static final float STEPPER_PILL_WIDTH = 62.0F;
   private static final long PRICE_STEP = 1000L;
   private static final float DURABILITY_STEP = 0.05F;
   private static final int FAST_STEP = 10;
   private static final long HAVE_REFRESH_MS = 400L;
   private final ChipStrip listStrip = new ChipStrip("gui.tools.inv.list.items", "gui.tools.inv.list.history");
   private final ChipStrip serverStrip = new ChipStrip("gui.tools.inv.server.funtime", "gui.tools.inv.server.holyworld", "gui.tools.inv.server.spookytime");
   private static final String[] SERVERS = new String[]{"Funtime 1.21", "HolyWorld", "SpookyTime"};
   private final BoundsData[] slotBounds = new BoundsData[36];
   private final Map<ItemMatcher, Integer> inSetCounts = new HashMap<>();
   private int listMode = 0;
   private int selectedSlot = -1;
   private int listCount;
   private int listFrom = 7;
   private int slotFrom = 6;
   private BoundsData stripBounds;
   private BoundsData serverStripBounds;
   private SearchBox searchInput;
   private SearchBox nameInput;
   private String contentSignature = "";
   private int haveSlot = -1;
   private int haveCount;
   private long haveAt;
   private BoundsData clearButton;
   private BoundsData saveButton;
   private BoundsData startButton;
   private BoundsData searchField;
   private BoundsData nameField;
   private BoundsData countMinus;
   private BoundsData countPlus;
   private BoundsData priceMinus;
   private BoundsData pricePlus;
   private BoundsData durabilityMinus;
   private BoundsData durabilityPlus;

   private static AutoInventoryModule module() {
      return AutoInventoryModule.INSTANCE;
   }

   private static InventoryPreset set() {
      return module().method207();
   }

   private static ServerNameNormalizer presets() {
      return module().getServerNameNormalizer();
   }

   @Override
   public String headerTitle() {
      return t("gui.tools.tab.inventory");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.STORAGE;
   }

   @Override
   public String sectionDescription() {
      return "gui.tools.inv.desc";
   }

   @Override
   public String emptyText() {
      return "gui.tools.inv.empty";
   }

   public boolean isLive() {
      return module().isEnabled();
   }

   @Override
   public void build() {
      InventoryPreset var1 = set();
      if (this.selectedSlot >= 0 && var1.method871(this.selectedSlot)) {
         this.selectedSlot = -1;
      }

      this.custom(22.0F, this.entering(0, this::renderSetHead));
      this.custom(this.gridHeight() + 2.0F, this.entering(1, this::renderGrid));
      this.custom(13.0F, this.entering(2, this::renderHint));
      this.custom(27.0F, this.entering(3, this::renderButtons));
      this.custom(20.0F, this.entering(4, this::renderServerStrip));
      this.custom(24.0F, this.entering(5, this::renderSearchField));
      this.custom(20.0F, this.entering(6, this::renderListStrip));
      this.listFrom = this.mainEntryCount();
      if (this.listMode == 1) {
         this.buildHistory();
      } else {
         this.buildCatalog();
      }

      this.buildAside();
   }

   private void buildCatalog() {
      String var1 = this.catalogQuery();
      int var2 = 7;
      int var3 = 0;

      for (ItemMatcher var5 : InventoryPreset.method891(module().method206())) {
         String var6 = var5.getText2() == null ? var5.getText22() : var5.getText2();
         if (var6 != null && !var6.isBlank() && (var1.isEmpty() || var6.toLowerCase(Locale.ROOT).contains(var1))) {
            var3++;
            this.custom(
               22.0F,
               this.entering(
                  var2++,
                  (var3x, var4, var5x, var6x, var7, var8, var9, var10) -> this.renderCatalogRow(var3x, var4, var5x, var6x, var7, var8, var9, var10, var5, var6)
               ),
               () -> this.place(var5),
               () -> this.inSetCounts.containsKey(var5),
               () -> this.removeAll(var5)
            );
         }
      }

      this.listCount = var3;
      if (var3 == 0) {
         this.custom(
            76.0F,
            this.entering(
               7,
               (var1x, var2x, var3x, var4, var5x, var6x, var7, var8) -> this.emptyCard(
                  var1x,
                  var8,
                  var4 + 8.0F,
                  var5x + 2.0F,
                  var6x - 16.0F,
                  70.0F,
                  MenuGlyph.SEARCH,
                  capitalize(t("gui.tools.inv.catalog.none")),
                  t("gui.tools.inv.catalog.none.desc"),
                  var7
               )
            )
         );
      }
   }

   private void buildHistory() {
      List<ItemEntry> var1 = module().getInventorySnapshot().method1863();
      this.listCount = var1.size();
      if (var1.isEmpty()) {
         this.custom(
            76.0F,
            this.entering(
               7,
               (var1x, var2x, var3, var4x, var5, var6, var7, var8) -> this.emptyCard(
                  var1x,
                  var8,
                  var4x + 8.0F,
                  var5 + 2.0F,
                  var6 - 16.0F,
                  70.0F,
                  MenuGlyph.TIMER,
                  capitalize(t("gui.tools.inv.history.none")),
                  t("gui.tools.inv.history.none.desc"),
                  var7
               )
            )
         );
      } else {
         int var2 = 7;

         for (Object var4_o : (Iterable<?>)(Object)(var1)) {
            ItemEntry var4 = (ItemEntry)var4_o;
            this.custom(
               22.0F,
               this.entering(
                  var2++, (var2x, var3, var4x, var5, var6, var7, var8, var9) -> this.renderHistoryRow(var2x, var3, var4x, var5, var6, var7, var8, var9, var4)
               )
            );
         }
      }
   }

   private void buildAside() {
      this.asideCaption("gui.tools.inv.list.presets");
      this.asideCustom(22.0F, this.entering(1, this::renderNameField));
      this.asideCustom(23.0F, this.entering(2, (var1x, var2x, var3, var4x, var5, var6, var7, var8) -> {
         BoundsData var9 = new BoundsData(var4x + 8.0F, var5 + 2.0F, var6 - 16.0F, 17.0F);
         if (this.button(var1x, var8, "capture", var9, MenuGlyph.STORAGE, t("gui.tools.inv.capture"), 0, true, var2x, var3, var7)) {
            HoverInfo.request(t("gui.tools.inv.capture"), t("gui.tools.inv.capture.desc"));
         }
      }), this::captureInventory, null);
      List<SborList> var1 = presets().method1229(module().method206());
      if (var1.isEmpty()) {
         this.asideCustom(
            30.0F,
            this.entering(
               3,
               (var0, var1x, var2x, var3, var4x, var5, var6, var7) -> {
                  BoundsData var8 = new BoundsData(var3 + 8.0F, var4x + 2.0F, var5 - 16.0F, 24.0F);
                  var0.drawRoundedBorder(
                     var8.getFloatValue(),
                     var8.getFloatValue2(),
                     var8.width(),
                     var8.height(),
                     0.3F,
                     ProxyDotRadiusData.method8069(CARD_ROUND),
                     var7.getFieldBorder().getZenithColor().method7294(var6 * 3.0F)
                  );
                  Font var9 = Fonts.NEW_REGULAR.getFont(4.3F);
                  String var10 = var9.clip(t("gui.tools.inv.presets.none"), var8.width() - 10.0F);
                  var0.drawText(
                     var9,
                     var10,
                     var8.getFloatValue() + (var8.width() - var9.width(var10)) / 2.0F,
                     var8.getFloatValue2() + (var8.height() - var9.height()) / 2.0F,
                     var7.getTextTertiary().getZenithColor().method7294(var6)
                  );
                  if (var8.contains(var1x, var2x)) {
                     HoverInfo.request(t("gui.tools.inv.presets.none"), t("gui.tools.inv.presets.none.desc"));
                  }
               }
            )
         );
      } else {
         int var2 = 3;

         for (Object var4_o : (Iterable<?>)(Object)(var1)) {
            SborList var4 = (SborList)var4_o;
            this.asideCustom(
               22.0F,
               this.entering(
                  var2++, (var2x, var3, var4x, var5, var6, var7, var8, var9) -> this.renderPreset(var2x, var3, var4x, var5, var6, var7, var8, var9, var4)
               ),
               () -> this.loadPreset(var4),
               null,
               () -> this.deletePreset(var4)
            );
         }
      }

      this.asideCaption("gui.tools.inv.caption.run");
      this.asideCustom(74.0F, this.entering(4, this::renderRunCard));
      this.buildSlotAside();
   }

   private void buildSlotAside() {
      this.asideCaption("gui.tools.inv.caption.slot");
      this.slotFrom = this.asideEntryCount();
      if (this.selectedSlot < 0) {
         this.asideCustom(
            76.0F,
            this.entering(
               6,
               (var1x, var2x, var3x, var4, var5, var6, var7, var8) -> this.emptyCard(
                  var1x,
                  var8,
                  var4 + 8.0F,
                  var5 + 2.0F,
                  var6 - 16.0F,
                  70.0F,
                  MenuGlyph.SLOT,
                  capitalize(t("gui.tools.inv.slot.none")),
                  t("gui.tools.inv.slot.none.desc"),
                  var7
               )
            )
         );
      } else {
         int var1 = this.selectedSlot;
         InventoryPreset var2 = set();
         ItemMatcher var3 = var2.method870(var1);
         this.asideCustom(
            38.0F,
            this.entering(6, (var3x, var4, var5, var6, var7, var8, var9, var10) -> this.renderSlotHead(var3x, var6, var7, var8, var9, var10, var1, var3))
         );
         if (InventoryPreset.method885(var3)) {
            this.asideCustom(
               22.0F,
               this.entering(
                  7,
                  (var3x, var4, var5, var6, var7, var8, var9, var10) -> {
                     BoundsData[] var11 = this.drawStepper(
                        var3x,
                        var4,
                        var5,
                        var6,
                        var7,
                        var8,
                        var9,
                        var10,
                        "count",
                        t("gui.tools.inv.count"),
                        String.valueOf(var2.method877(var1)),
                        "gui.tools.inv.count.desc"
                     );
                     this.countMinus = var11[0];
                     this.countPlus = var11[1];
                  }
               )
            );
         }

         this.asideCustom(
            22.0F,
            this.entering(
               8,
               (var3x, var4, var5, var6, var7, var8, var9, var10) -> {
                  long var11 = var2.method879(var1);
                  BoundsData[] var13 = this.drawStepper(
                     var3x,
                     var4,
                     var5,
                     var6,
                     var7,
                     var8,
                     var9,
                     var10,
                     "price",
                     t("gui.tools.inv.price"),
                     var11 <= 0L ? t("gui.tools.inv.price.any") : String.valueOf(var11),
                     "gui.tools.inv.price.desc"
                  );
                  this.priceMinus = var13[0];
                  this.pricePlus = var13[1];
               }
            )
         );
         if (InventoryPreset.method886(var3)) {
            this.asideCustom(
               22.0F,
               this.entering(
                  9,
                  (var3x, var4, var5, var6, var7, var8, var9, var10) -> {
                     BoundsData[] var11 = this.drawStepper(
                        var3x,
                        var4,
                        var5,
                        var6,
                        var7,
                        var8,
                        var9,
                        var10,
                        "durability",
                        t("gui.tools.inv.durability"),
                        Math.round(var2.method881(var1) * 100.0F) + "%",
                        "gui.tools.inv.durability.desc"
                     );
                     this.durabilityMinus = var11[0];
                     this.durabilityPlus = var11[1];
                  }
               )
            );
         }

         this.asideCustom(25.0F, this.entering(10, (var1x, var2x, var3x, var4, var5, var6, var7, var8) -> {
            BoundsData var9 = new BoundsData(var4 + 8.0F, var5 + 4.0F, var6 - 16.0F, 17.0F);
            if (this.button(var1x, var8, "remove", var9, MenuGlyph.CLOSE, t("gui.tools.inv.remove"), 2, true, var2x, var3x, var7)) {
               HoverInfo.request(t("gui.tools.inv.remove"), t("gui.tools.inv.remove.desc"));
            }
         }), () -> {
            var2.method875(var1);
            this.selectedSlot = -1;
            this.markDirty();
            this.invalidate();
         }, null);
      }
   }

   private void renderSetHead(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = Fonts.NEW_MEDIUM.getFont(4.0F);
      float var10 = var5 + (22.0F - var9.height()) / 2.0F;
      caption(var1, var8, t("gui.tools.inv.caption.set"), var4 + 8.0F, var10, var7);
      int var11 = set().method872();
      Font var12 = Fonts.NEW_MEDIUM.getFont(4.4F);
      String var13 = var11 + " / 36";
      float var14 = var12.width(var13);
      float var15 = var4 + var6 - 8.0F;
      var1.drawText(
         var12,
         var13,
         var15 - var14,
         var5 + (22.0F - var12.height()) / 2.0F,
         (var11 > 0 ? var8.getTextEnable() : var8.getTextTertiary()).getZenithColor().method7294(var7)
      );
      float var16 = 46.0F;
      progress(
         var1,
         var8,
         var15 - var14 - 6.0F - var16,
         var5 + 11.0F - 1.25F,
         var16,
         2.5F,
         var11 / 36.0F,
         var8.getPrimaryColor().getZenithColor(),
         module().isEnabled(),
         var7
      );
   }

   private float slotSize() {
      return (this.mainWidth() - 16.0F - 24.0F) / 9.0F;
   }

   private float gridHeight() {
      return 4.0F * this.slotSize() + 9.0F;
   }

   private void renderGrid(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = this.slotSize();
      float var10 = var4 + 8.0F;
      InventoryPreset var11 = set();
      ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.55F);
      ZenithColor var13 = var8.getPrimaryColor().getZenithColor();
      int var14 = -1;
      float[] var15 = new float[36];
      float[] var16 = new float[36];

      for (int var17 = 0; var17 < 36; var17++) {
         float var18 = var10 + var17 % 9 * (var9 + 3.0F);
         float var19 = var5 + var17 / 9 * (var9 + 3.0F);
         BoundsData var20 = new BoundsData(var18, var19, var9, var9);
         this.slotBounds[var17] = var20;
         boolean var21 = var20.contains(var2, var3);
         if (var21) {
            var14 = var17;
         }

         float var22 = this.hot("slot:" + var17, var21);
         float var23 = this.hot("slotsel:" + var17, var17 == this.selectedSlot);
         var15[var17] = var22;
         var16[var17] = var23;
         boolean var24 = var11.method871(var17);
         float var25 = this.pressed("slotp:" + var17);
         float var26 = var25 * 1.4F;
         float var27 = var18 + var26 / 2.0F;
         float var28 = var19 + var26 / 2.0F - var22 * 0.6F;
         float var29 = var9 - var26;
         ZenithColor var30 = var24
            ? var8.getPanelLeftBackground().getZenithColor().method7295(var8.getDisableActiveBg().getZenithColor(), var22 * 0.8F)
            : cardFill(var8, var22).method7295(var13.method7292(0.14F), 0.25F + var23 * 0.5F);
         var1.drawRoundedRect(var27, var28, var29, var29, var12, var30.method7294(var7));
         if (var23 > 0.01F) {
            var1.drawRoundedBorder(var27, var28, var29, var29, 0.4F, var12, var13.method7292(0.85F * var23).method7294(var7));
         } else if (!var24) {
            var1.drawRoundedBorder(var27, var28, var29, var29, 0.15F, var12, var8.getFieldBorder().getZenithColor().method7294(var7 * (2.0F + var22 * 2.0F)));
         }
      }

      HudBatchState.begin();

      try {
         for (int var34 = 0; var34 < 36; var34++) {
            ItemMatcher var36 = var11.method870(var34);
            if (var36 != null && !var36.getItemStack().isEmpty()) {
               BoundsData var38 = this.slotBounds[var34];
               float var40 = this.since("place:" + var34, 380L);
               float var43 = 1.0F - (1.0F - var40) * (1.0F - var40) * (1.0F - var40);
               float var46 = var43 * (1.0F + (float)Math.sin(var40 * Math.PI) * 0.18F);
               float var47 = var9 * 0.65F * var46;
               if (!(var47 < 1.0F)) {
                  float var48 = var15[var34] * 0.6F;
                  this.drawItem(
                     var1,
                     var36.getItemStack(),
                     var38.getFloatValue() + (var9 - var47) / 2.0F,
                     var38.getFloatValue2() + (var9 - var47) / 2.0F - var48,
                     var47,
                     var7
                  );
               }
            }
         }
      } finally {
         HudBatchState.end();
      }

      Font var35 = Fonts.NEW_MEDIUM.getFont(4.2F);

      for (int var37 = 0; var37 < 36; var37++) {
         BoundsData var39 = this.slotBounds[var37];
         if (var11.method871(var37)) {
            float var41 = Math.max(var15[var37], var16[var37]);
            float var44 = var16[var37];
            if (var41 > 0.02F) {
               this.drawPlus(
                  var1,
                  var39.getFloatValue() + var9 / 2.0F,
                  var39.getFloatValue2() + var9 / 2.0F,
                  5.0F + 1.5F * var44,
                  var8.getTextTertiary().getZenithColor().method7295(var13, var44).method7294(var7 * var41)
               );
            }
         } else {
            int var42 = var11.method877(var37);
            if (var42 > 1) {
               String var45 = String.valueOf(var42);
               var1.drawText(
                  var35,
                  var45,
                  var39.getFloatValue() + var9 - 2.0F - var35.width(var45),
                  var39.getFloatValue2() + var9 - 2.0F - var35.height(),
                  var8.getTextEnable().getZenithColor().method7294(var7)
               );
            }

            if (var11.method879(var37) > 0L) {
               dot(var1, var39.getFloatValue() + 3.5F, var39.getFloatValue2() + 3.5F, 2.2F, var13, 0.0F, var7);
            }
         }
      }

      if (var14 >= 0) {
         HoverInfo.request(
            t("gui.tools.inv.slot") + " " + (var14 + 1), t(var11.method871(var14) ? "gui.tools.inv.slot.empty.desc" : "gui.tools.inv.slot.filled.desc")
         );
      }
   }

   private void drawPlus(MenuDrawContext var1, float var2, float var3, float var4, ZenithColor var5) {
      var1.drawRect(var2 - var4 / 2.0F, var3 - 0.5F, var4, 1.0F, var5);
      var1.drawRect(var2 - 0.5F, var3 - var4 / 2.0F, 1.0F, var4, var5);
   }

   private void renderHint(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = Fonts.NEW_REGULAR.getFont(4.4F);
      String var10 = fit(var9, t(this.hintKey()), var6 - 16.0F - 10.0F);
      float var11 = var9.width(var10);
      float var12 = var4 + (var6 - var11) / 2.0F;
      boolean var13 = this.selectedSlot >= 0;
      if (var13) {
         dot(var1, var12 - 5.0F, var5 + 6.5F, 2.4F, var8.getPrimaryColor().getZenithColor(), pulse(1.0F), var7);
      }

      var1.drawText(
         var9,
         var10,
         var12,
         var5 + (13.0F - var9.height()) / 2.0F,
         (var13 ? var8.getTextSecondary() : var8.getTextTertiary()).getZenithColor().method7294(var7)
      );
   }

   private String hintKey() {
      if (this.selectedSlot >= 0) {
         return set().method871(this.selectedSlot) ? "gui.tools.inv.hint.slotEmpty" : "gui.tools.inv.hint.slotFilled";
      } else {
         return set().isEmpty() ? "gui.tools.inv.hint.empty" : "gui.tools.inv.hint.idle";
      }
   }

   private void renderButtons(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = var6 - 16.0F;
      float var10 = (var9 - 5.0F) / 2.0F;
      this.clearButton = new BoundsData(var4 + 8.0F, var5 + 3.0F, var10, 17.0F);
      this.saveButton = new BoundsData(var4 + 8.0F + var10 + 5.0F, var5 + 3.0F, var10, 17.0F);
      boolean var11 = set().isEmpty();
      if (this.button(var1, var8, "clear", this.clearButton, MenuGlyph.CLOSE, t("gui.tools.inv.clear"), 2, !var11, var2, var3, var7)) {
         HoverInfo.request(t("gui.tools.inv.clear"), t("gui.tools.inv.clear.desc"));
      }

      if (this.button(var1, var8, "save", this.saveButton, MenuGlyph.CHECK, t("gui.tools.inv.presets.save"), 1, !var11, var2, var3, var7)) {
         HoverInfo.request(t("gui.tools.inv.presets.save"), t("gui.tools.inv.presets.save.desc"));
      }

      if (var11) {
         this.clearButton = null;
         this.saveButton = null;
      }
   }

   private void renderServerStrip(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = Fonts.NEW_MEDIUM.getFont(4.0F);
      caption(var1, var8, t("gui.tools.inv.server"), var4 + 8.0F, var5 + (20.0F - var9.height()) / 2.0F, var7);
      float var10 = this.serverStrip.width();
      float var11 = var4 + var6 - 8.0F - var10;
      float var12 = var5 + 4.5F;
      this.serverStripBounds = new BoundsData(var11, var12, var10, 11.0F);
      this.serverStrip.render(var1, var2, var3, var11, var12, var10 + 1.0F, serverIndex(), var7, var8);
   }

   private static int serverIndex() {
      String var0 = module().method206();

      for (int var1 = 0; var1 < SERVERS.length; var1++) {
         if (SERVERS[var1].equals(var0)) {
            return var1;
         }
      }

      return -1;
   }

   private void renderSearchField(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      this.searchField = new BoundsData(var4 + 8.0F, var5 + 2.0F, var6 - 16.0F, 18.0F);
      this.drawField(var1, var2, var3, "search", this.searchField, this.searchInput(), MenuGlyph.SEARCH, var7, var8);
   }

   private void renderListStrip(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = var5 + 4.5F;
      float var10 = this.listStrip.width();
      this.stripBounds = new BoundsData(var4 + 8.0F, var9, var10, 11.0F);
      this.listStrip.render(var1, var2, var3, var4 + 8.0F, var9, var10 + 1.0F, this.listMode, var7, var8);
      Font var11 = Fonts.NEW_MEDIUM.getFont(4.2F);
      String var12 = String.valueOf(this.listCount);
      var1.drawText(
         var11,
         var12,
         var4 + var6 - 8.0F - var11.width(var12),
         var5 + (20.0F - var11.height()) / 2.0F,
         var8.getTextTertiary().getZenithColor().method7294(var7)
      );
   }

   private void drawField(MenuDrawContext var1, int var2, int var3, String var4, BoundsData var5, SearchBox var6, MenuGlyph var7, float var8, ZenithStyle var9) {
      boolean var10 = var6.isSelected();
      float var11 = this.hot("field:" + var4, var5.contains(var2, var3));
      float var12 = this.hot("focus:" + var4, var10);
      ProxyDotRadiusData var13 = ProxyDotRadiusData.method8069(CARD_ROUND);
      ZenithColor var14 = var9.getPrimaryColor().getZenithColor();
      var1.drawRoundedRect(
         var5.getFloatValue(),
         var5.getFloatValue2(),
         var5.width(),
         var5.height(),
         var13,
         var9.getPanelLeftBackground().getZenithColor().method7295(var9.getDisableActiveBg().getZenithColor(), var11 * 0.4F).method7294(var8)
      );
      var1.drawRoundedBorder(
         var5.getFloatValue(),
         var5.getFloatValue2(),
         var5.width(),
         var5.height(),
         0.2F + var12 * 0.15F,
         var13,
         var9.getFieldBorder().getZenithColor().method7294(2.0F + var11 * 2.0F).method7295(var14.method7292(0.7F), var12).method7294(var8)
      );
      var7.draw(
         var1,
         var5.getFloatValue() + 6.0F,
         var5.getFloatValue2() + (var5.height() - 6.0F) / 2.0F,
         var9.getTextTertiary().getZenithColor().method7295(var14, var12).method7294(var8)
      );
      Font var15 = Fonts.NEW_MEDIUM.getFont(4.8F);
      var6.setFont(var15);
      var6.setWidth(var5.width() - 22.0F);
      var6.method987(
         var1,
         var5.getFloatValue() + 16.0F,
         var5.getFloatValue2() + (var5.height() - var15.height()) / 2.0F,
         var9.getTextEnable().getZenithColor().method7294(var8),
         var9.getTextTertiary().getZenithColor().method7294(var8)
      );
   }

   private void renderCatalogRow(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, ItemMatcher var9, String var10
   ) {
      BoundsData var11 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 19.0F);
      boolean var12 = var11.contains(var2, var3);
      int var13 = this.inSetCounts.getOrDefault(var9, 0);
      String var14 = "cat:" + System.identityHashCode(var9);
      float var15 = this.hot(var14, var12);
      float var16 = this.hot(var14 + ":on", var13 > 0);
      ZenithColor var17 = var8.getPrimaryColor().getZenithColor();
      if (var16 > 0.01F) {
         accentCard(var1, var8, var11.getFloatValue(), var11.getFloatValue2(), var11.width(), var11.height(), var15, var7, var17, var16 * 0.55F);
      } else {
         card(var1, var8, var11.getFloatValue(), var11.getFloatValue2(), var11.width(), var11.height(), var15, var7);
      }

      float var18 = var11.height() - 6.0F;
      tile(var1, var8, var11.getFloatValue() + 3.0F, var11.getFloatValue2() + 3.0F, var18, var17, var16 * 0.5F + var15 * 0.2F, var7);
      this.drawItem(
         var1,
         var9.getItemStack(),
         var11.getFloatValue() + 3.0F + (var18 - 11.0F) / 2.0F,
         var11.getFloatValue2() + 3.0F + (var18 - 11.0F) / 2.0F - var15 * 0.6F,
         11.0F,
         var7
      );
      Font var19 = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font var20 = Fonts.NEW_MEDIUM.getFont(4.1F);
      float var21 = var11.getFloatValue() + var11.width() - 5.0F;
      if (var13 > 0) {
         String var22 = "×" + var13;
         float var23 = pillWidth(var20, var22);
         pill(var1, var20, var21 - var23, var11.getFloatValue2() + (var11.height() - var20.height() - 3.6F) / 2.0F, var22, var17, var7);
         var21 -= var23 + 4.0F;
      }

      if (var15 > 0.02F) {
         float var25 = 10.0F;
         float var27 = var21 - var25;
         float var24 = var11.getFloatValue2() + (var11.height() - var25) / 2.0F;
         var1.drawRoundedRect(var27, var24, var25, var25, ProxyDotRadiusData.method8069(var25 / 2.0F), var17.method7292(0.2F * var15).method7294(var7));
         this.drawPlus(var1, var27 + var25 / 2.0F, var24 + var25 / 2.0F, 4.5F, var17.method7294(var7 * var15));
         var21 -= var25 + 4.0F;
      }

      float var26 = var11.getFloatValue() + 3.0F + var18 + 6.0F;
      var1.drawText(
         var19,
         var19.clip(var10, var21 - var26),
         var26,
         var11.getFloatValue2() + (var11.height() - var19.height()) / 2.0F,
         var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), Math.max(var16, var15 * 0.7F)).method7294(var7)
      );
      if (var12) {
         HoverInfo.request(var10, t("gui.tools.inv.catalog.desc"));
      }
   }

   private void renderHistoryRow(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, ItemEntry var9) {
      BoundsData var10 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 19.0F);
      boolean var11 = var10.contains(var2, var3);
      card(
         var1,
         var8,
         var10.getFloatValue(),
         var10.getFloatValue2(),
         var10.width(),
         var10.height(),
         this.hot("hist:" + System.identityHashCode(var9), var11),
         var7
      );
      float var12 = var10.height() - 6.0F;
      tile(var1, var8, var10.getFloatValue() + 3.0F, var10.getFloatValue2() + 3.0F, var12, OK, 0.25F, var7);
      this.drawItem(
         var1, var9.getItemStack(), var10.getFloatValue() + 3.0F + (var12 - 11.0F) / 2.0F, var10.getFloatValue2() + 3.0F + (var12 - 11.0F) / 2.0F, 11.0F, var7
      );
      Font var13 = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font var14 = Fonts.NEW_MEDIUM.getFont(4.1F);
      String var15 = String.valueOf(var9.getLongValue());
      float var16 = pillWidth(var14, var15);
      float var17 = var10.getFloatValue() + var10.width() - 5.0F;
      pill(var1, var14, var17 - var16, var10.getFloatValue2() + (var10.height() - var14.height() - 3.6F) / 2.0F, var15, OK, var7);
      float var18 = var10.getFloatValue() + 3.0F + var12 + 6.0F;
      String var19 = var9.getText() + "  ×" + var9.getIntValue();
      var1.drawText(
         var13,
         var13.clip(var19, var17 - var16 - 5.0F - var18),
         var18,
         var10.getFloatValue2() + (var10.height() - var13.height()) / 2.0F,
         var8.getTextSecondary().getZenithColor().method7294(var7)
      );
      if (var11) {
         HoverInfo.request(var9.getText(), t("gui.tools.inv.history.desc"));
      }
   }

   private void renderNameField(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      this.nameField = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 18.0F);
      this.drawField(var1, var2, var3, "name", this.nameField, this.nameInput(), MenuGlyph.CONFIGS, var7, var8);
   }

   private void renderPreset(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, SborList var9) {
      BoundsData var10 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 19.0F);
      boolean var11 = var10.contains(var2, var3);
      float var12 = this.hot("preset:" + var9.getName(), var11);
      card(var1, var8, var10.getFloatValue(), var10.getFloatValue2(), var10.width(), var10.height(), var12, var7);
      float var13 = var10.height() - 6.0F;
      ZenithColor var14 = var8.getPrimaryColor().getZenithColor();
      tile(var1, var8, var10.getFloatValue() + 3.0F, var10.getFloatValue2() + 3.0F, var13, var14, 0.35F + var12 * 0.3F, var7);
      MenuGlyph.CONFIGS
         .draw(var1, var10.getFloatValue() + 3.0F + (var13 - 6.0F) / 2.0F, var10.getFloatValue2() + 3.0F + (var13 - 6.0F) / 2.0F, var14.method7294(var7));
      Font var15 = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font var16 = Fonts.NEW_MEDIUM.getFont(4.1F);
      String var17 = String.valueOf(var9.size());
      float var18 = pillWidth(var16, var17);
      float var19 = var10.getFloatValue() + var10.width() - 5.0F;
      pill(
         var1,
         var16,
         var19 - var18,
         var10.getFloatValue2() + (var10.height() - var16.height() - 3.6F) / 2.0F,
         var17,
         var8.getTextSecondary().getZenithColor(),
         var7
      );
      float var20 = var10.getFloatValue() + 3.0F + var13 + 6.0F;
      var1.drawText(
         var15,
         var15.clip(var9.getName(), var19 - var18 - 5.0F - var20),
         var20,
         var10.getFloatValue2() + (var10.height() - var15.height()) / 2.0F,
         var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var12).method7294(var7)
      );
      if (var11) {
         HoverInfo.request(var9.getName(), t("gui.tools.inv.presets.desc"));
      }
   }

   private void renderRunCard(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      AutoInventoryModule var9 = module();
      boolean var10 = var9.isEnabled();
      BoundsData var11 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 70.0F);
      ZenithColor var12 = var8.getPrimaryColor().getZenithColor();
      accentCard(
         var1, var8, var11.getFloatValue(), var11.getFloatValue2(), var11.width(), var11.height(), 0.0F, var7, var12, var10 ? 0.4F + 0.2F * pulse(0.7F) : 0.1F
      );
      this.startButton = new BoundsData(var11.getFloatValue() + 5.0F, var11.getFloatValue2() + 5.0F, var11.width() - 10.0F, 20.0F);
      String var13 = t(var10 ? "gui.tools.inv.stop" : "gui.tools.inv.start");
      if (this.button(var1, var8, "start", this.startButton, var10 ? MenuGlyph.CLOSE : MenuGlyph.CHECK, var13, var10 ? 2 : 1, true, var2, var3, var7)) {
         HoverInfo.request(var13, t(var10 ? "gui.tools.inv.stop.desc" : "gui.tools.inv.start.desc"));
      }

      if (var10) {
         this.shimmer(var1, this.startButton, DANGER, var7);
      }

      int var14 = var9.getIntValue4();
      int var15 = var9.getIntValue5();
      Font var16 = Fonts.NEW_REGULAR.getFont(4.3F);
      Font var17 = Fonts.NEW_MEDIUM.getFont(4.3F);
      float var18 = var11.getFloatValue() + 7.0F;
      float var19 = var11.getFloatValue() + var11.width() - 7.0F;
      float var20 = this.startButton.getFloatValue2() + this.startButton.height() + 6.0F;
      var1.drawText(var16, t("gui.tools.inv.progress"), var18, var20, var8.getTextTertiary().getZenithColor().method7294(var7));
      String var21 = var14 <= 0 ? "-" : var15 + " / " + var14;
      var1.drawText(var17, var21, var19 - var17.width(var21), var20, var8.getTextSecondary().getZenithColor().method7294(var7));
      progress(var1, var8, var18, var20 + var16.height() + 3.0F, var19 - var18, 3.0F, var14 <= 0 ? 0.0F : (float)var15 / var14, var12, var10, var7);
      float var22 = var20 + var16.height() + 11.0F;
      ZenithColor var23 = var10 ? var12 : var8.getTextTertiary().getZenithColor();
      dot(var1, var18 + 1.5F, var22 + var16.height() / 2.0F, 3.0F, var23, var10 ? pulse(1.2F) : 0.0F, var7);
      String var24 = var9.method211();
      String var25 = var9.method212();
      String var26 = var24 + (var25 != null && !var25.isBlank() ? "  ·  " + var25 : "");
      var1.drawText(
         var16,
         var16.clip(var26, var19 - var18 - 7.0F),
         var18 + 6.0F,
         var22,
         (var10 ? var8.getTextSecondary() : var8.getTextTertiary()).getZenithColor().method7294(var7)
      );
      if (new BoundsData(var18, var22 - 2.0F, var19 - var18, var16.height() + 4.0F).contains(var2, var3)) {
         HoverInfo.request(t("gui.tools.inv.status"), t("gui.tools.inv.status.desc"));
      }
   }

   private void renderSlotHead(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6, int var7, ItemMatcher var8) {
      BoundsData var9 = new BoundsData(var2 + 8.0F, var3 + 2.0F, var4 - 16.0F, 34.0F);
      ZenithColor var10 = var6.getPrimaryColor().getZenithColor();
      accentCard(var1, var6, var9.getFloatValue(), var9.getFloatValue2(), var9.width(), var9.height(), 0.0F, var5, var10, 0.3F);
      float var11 = var9.height() - 8.0F;
      tile(var1, var6, var9.getFloatValue() + 4.0F, var9.getFloatValue2() + 4.0F, var11, var10, 0.5F, var5);
      float var12 = this.since("place:" + var7, 380L);
      float var13 = 16.0F * (1.0F + (float)Math.sin(var12 * Math.PI) * 0.12F);
      this.drawItem(
         var1, var8.getItemStack(), var9.getFloatValue() + 4.0F + (var11 - var13) / 2.0F, var9.getFloatValue2() + 4.0F + (var11 - var13) / 2.0F, var13, var5
      );
      Font var14 = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font var15 = Fonts.NEW_REGULAR.getFont(4.2F);
      Font var16 = Fonts.NEW_MEDIUM.getFont(4.1F);
      float var17 = var9.getFloatValue() + 4.0F + var11 + 7.0F;
      float var18 = var9.getFloatValue() + var9.width() - 6.0F - var17;
      String var19 = var8.getText2() == null ? var8.getText22() : var8.getText2();
      var1.drawText(var14, var14.clip(var19, var18), var17, var9.getFloatValue2() + 6.0F, var6.getTextEnable().getZenithColor().method7294(var5));
      float var20 = var9.getFloatValue2() + 8.0F + var14.height();
      String var21 = t("gui.tools.inv.slot") + " " + (var7 + 1);
      var1.drawText(var15, var21, var17, var20 + 1.0F, var6.getTextTertiary().getZenithColor().method7294(var5));
      String var22 = t("gui.tools.inv.have") + " " + this.haveCount;
      pill(var1, var16, var17 + var15.width(var21) + 5.0F, var20, var22, this.haveCount > 0 ? OK : var6.getTextTertiary().getZenithColor(), var5);
   }

   private BoundsData[] drawStepper(
      MenuDrawContext var1,
      int var2,
      int var3,
      float var4,
      float var5,
      float var6,
      float var7,
      ZenithStyle var8,
      String var9,
      String var10,
      String var11,
      String var12
   ) {
      BoundsData var13 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 19.0F);
      boolean var14 = var13.contains(var2, var3);
      card(var1, var8, var13.getFloatValue(), var13.getFloatValue2(), var13.width(), var13.height(), this.hot("step:" + var9, var14), var7);
      if (var14 && var12 != null) {
         HoverInfo.request(var10, t(var12));
      }

      Font var15 = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font var16 = Fonts.NEW_MEDIUM.getFont(4.8F);
      float var17 = var13.getFloatValue2() + var13.height() / 2.0F;
      float var18 = 13.0F;
      float var19 = var13.getFloatValue() + var13.width() - 4.0F - 62.0F;
      var1.drawText(
         var15,
         fit(var15, var10, var19 - var13.getFloatValue() - 10.0F),
         var13.getFloatValue() + 7.0F,
         var17 - var15.height() / 2.0F,
         var8.getTextSecondary().getZenithColor().method7294(var7)
      );
      var1.drawRoundedRect(
         var19,
         var17 - var18 / 2.0F,
         62.0F,
         var18,
         ProxyDotRadiusData.method8069(var18 / 2.0F),
         var8.getPanelLeftBackground().getZenithColor().method7294(var7)
      );
      BoundsData var20 = new BoundsData(var19 + 1.0F, var17 - 5.5F, 11.0F, 11.0F);
      BoundsData var21 = new BoundsData(var19 + 62.0F - 11.0F - 1.0F, var17 - 5.5F, 11.0F, 11.0F);
      this.stepperButton(var1, var2, var3, var9 + ":minus", var20, false, var7, var8);
      this.stepperButton(var1, var2, var3, var9 + ":plus", var21, true, var7, var8);
      float var22 = var20.getFloatValue() + 11.0F;
      float var23 = var21.getFloatValue() - var22;
      String var24 = fit(var16, var11, var23);
      float var25 = Math.max(this.pressed("step:" + var9 + ":minus"), this.pressed("step:" + var9 + ":plus"));
      ZenithColor var26 = var8.getTextEnable().getZenithColor().method7295(var8.getPrimaryColor().getZenithColor(), var25);
      var1.drawText(var16, var24, var22 + (var23 - var16.width(var24)) / 2.0F, var17 - var16.height() / 2.0F - var25 * 0.8F, var26.method7294(var7));
      return new BoundsData[]{var20, var21};
   }

   private void stepperButton(MenuDrawContext var1, int var2, int var3, String var4, BoundsData var5, boolean var6, float var7, ZenithStyle var8) {
      boolean var9 = var5.contains(var2, var3);
      float var10 = this.hot("sb:" + var4, var9);
      float var11 = this.pressed("step:" + var4);
      ZenithColor var12 = var8.getPrimaryColor().getZenithColor();
      float var13 = var11 * 1.2F;
      if (var10 > 0.01F || var11 > 0.01F) {
         var1.drawRoundedRect(
            var5.getFloatValue() - var13 / 2.0F,
            var5.getFloatValue2() - var13 / 2.0F,
            var5.width() + var13,
            var5.height() + var13,
            ProxyDotRadiusData.method8069((var5.height() + var13) / 2.0F),
            var12.method7292(0.14F * var10 + 0.3F * var11).method7294(var7)
         );
      }

      ZenithColor var14 = var8.getTextSecondary()
         .getZenithColor()
         .method7295(var12.method7295(ZenithColor.INSTANCE, 0.3F), Math.max(var10, var11))
         .method7294(var7);
      float var15 = var5.getFloatValue() + var5.width() / 2.0F;
      float var16 = var5.getFloatValue2() + var5.height() / 2.0F;
      var1.drawRect(var15 - 2.5F, var16 - 0.5F, 5.0F, 1.0F, var14);
      if (var6) {
         var1.drawRect(var15 - 0.5F, var16 - 2.5F, 1.0F, 5.0F, var14);
      }
   }

   private static String capitalize(String var0) {
      return var0 != null && !var0.isEmpty() ? var0.substring(0, 1).toUpperCase(Locale.ROOT) + var0.substring(1) : "";
   }

   private void place(ItemMatcher var1) {
      InventoryPreset var2 = set();
      int var3 = this.selectedSlot;
      if (var3 < 0 || !var2.method871(var3)) {
         var3 = var2.method873();
      }

      if (var3 < 0) {
         ChatLogger.method2021(t("gui.tools.inv.full"));
      } else {
         var2.method874(var3, var1);
         this.mark("place:" + var3);
         this.selectedSlot = var3;
         this.haveAt = 0L;
         this.markDirty();
         this.invalidate();
      }
   }

   private void removeAll(ItemMatcher var1) {
      InventoryPreset var2 = set();
      boolean var3 = false;

      for (int var4 = 0; var4 < 36; var4++) {
         if (var2.method870(var4) == var1) {
            var2.method875(var4);
            var3 = true;
         }
      }

      if (var3) {
         if (this.selectedSlot >= 0 && var2.method871(this.selectedSlot)) {
            this.selectedSlot = -1;
         }

         this.markDirty();
         this.invalidate();
      }
   }

   private void loadPreset(SborList var1) {
      this.selectedSlot = -1;
      int var2 = var1.method1753(set());

      for (int var3 = 0; var3 < 36; var3++) {
         if (!set().method871(var3)) {
            this.mark("place:" + var3);
         }
      }

      this.markDirty();
      this.invalidate();
      String var4 = t("gui.tools.inv.presets.loaded").replace("{name}", var1.getName());
      if (var2 > 0) {
         ChatLogger.method2021(var4 + " " + t("gui.tools.inv.presets.lost").replace("{count}", String.valueOf(var2)));
      } else {
         ChatLogger.method2019(var4);
      }
   }

   private void deletePreset(SborList var1) {
      if (presets().method1232(var1)) {
         this.markDirty();
         this.invalidate();
      }
   }

   private void clearSet() {
      set().method876();
      this.selectedSlot = -1;
      this.markDirty();
      this.invalidate();
   }

   private void captureInventory() {
      if (mc.player == null) {
         ChatLogger.method2021(t("gui.tools.inv.capture.noPlayer"));
      } else {
         AutoInventoryModule.Data2 var1 = module().method208();
         this.selectedSlot = -1;
         if (var1.getIntValue() == 0) {
            this.markDirty();
            this.invalidate();
            ChatLogger.method2021(t("gui.tools.inv.capture.none"));
         } else {
            for (int var2 = 0; var2 < 36; var2++) {
               if (!set().method871(var2)) {
                  this.mark("place:" + var2);
               }
            }

            String var5 = this.inputName();
            if (var5.isEmpty()) {
               var5 = presets().method1233(t("gui.tools.inv.capture.name"), module().method206());
            }

            presets().method1231(SborList.method1752(var5, set()));
            this.clearInput();
            this.markDirty();
            this.invalidate();
            String var3 = t("gui.tools.inv.capture.done").replace("{name}", var5).replace("{count}", String.valueOf(var1.getIntValue()));
            int var4 = var1.getIntValue2() + var1.getIntValue3();
            if (var4 > 0) {
               var3 = var3 + " " + t("gui.tools.inv.capture.missed").replace("{count}", String.valueOf(var4));
            }

            ChatLogger.method2019(var3);
         }
      }
   }

   private void savePreset() {
      String var1 = this.inputName();
      if (var1.isEmpty()) {
         ChatLogger.method2021(t("gui.tools.inv.presets.needName"));
      } else {
         InventoryPreset var2 = set();
         if (var2.isEmpty()) {
            ChatLogger.method2021(t("gui.tools.inv.presets.needItems"));
         } else {
            boolean var3 = presets().method1230(var1, module().method206()) != null;
            presets().method1231(SborList.method1752(var1, var2));
            this.clearInput();
            this.markDirty();
            this.invalidate();
            ChatLogger.method2019(t(var3 ? "gui.tools.inv.presets.replaced" : "gui.tools.inv.presets.saved").replace("{name}", var1));
         }
      }
   }

   private String inputName() {
      String var1 = this.nameInput().getText();
      return var1 == null ? "" : var1.trim();
   }

   private void clearInput() {
      this.nameInput().setText("");
      this.nameInput().setCursor(0);
   }

   private void markDirty() {
      Zenith.getInstance().getManager().method7788();
   }

   @Override
   public void close() {
      Manager var1 = Zenith.getInstance().getManager();
      if (var1.method7790()) {
         var1.save();
      }

      super.close();
   }

   private SearchBox searchInput() {
      if (this.searchInput == null) {
         this.searchInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(4.8F), t("gui.tools.inv.search.hint"), 32.0F);
      }

      return this.searchInput;
   }

   private SearchBox nameInput() {
      if (this.nameInput == null) {
         this.nameInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(4.8F), t("gui.tools.inv.presets.hint"), 32.0F);
      }

      return this.nameInput;
   }

   private String catalogQuery() {
      String var1 = this.searchInput().getText();
      var1 = var1 == null ? "" : var1.trim().toLowerCase(Locale.ROOT);
      return var1.isEmpty() ? this.searchQuery() : var1;
   }

   private void focus(SearchBox var1) {
      this.searchInput().setSelected(var1 == this.searchInput);
      this.nameInput().setSelected(var1 == this.nameInput);
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      Arrays.fill(this.slotBounds, null);
      this.stripBounds = null;
      this.serverStripBounds = null;
      this.clearButton = null;
      this.saveButton = null;
      this.startButton = null;
      this.searchField = null;
      this.nameField = null;
      this.countMinus = null;
      this.countPlus = null;
      this.priceMinus = null;
      this.pricePlus = null;
      this.durabilityMinus = null;
      this.durabilityPlus = null;
      super.render(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (hit(this.searchField, var1, var3)) {
         this.focus(this.searchInput());
         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else if (hit(this.nameField, var1, var3)) {
         this.focus(this.nameInput());
         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else {
         BoundsData var6 = this.mainViewport();
         if (var6 == null || !var6.contains(var1, var3)) {
            this.focus(null);
         }

         if (var5 == Enum3.LEFT && hit(this.startButton, var1, var3)) {
            BoundsData var7 = this.asideViewport();
            if (var7 == null || var7.contains(var1, var3)) {
               this.pressButton("start", var1, var3);
               module().toggle();
               Zenith.getInstance().getClientSounds().method1570(false);
               this.invalidate();
               return true;
            }
         }

         if (var6 != null && var6.contains(var1, var3)) {
            if (this.serverStripBounds != null && this.serverStripBounds.contains(var1, var3)) {
               int var9 = this.serverStrip.hovered();
               if (var9 >= 0) {
                  this.selectServer(var9);
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            if (this.stripBounds != null && this.stripBounds.contains(var1, var3)) {
               int var10 = this.listStrip.hovered();
               if (var10 >= 0) {
                  if (var10 != this.listMode) {
                     this.listMode = var10;
                     this.resetScroll();
                     this.replayMainFrom(this.listFrom);
                     this.invalidate();
                  }

                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            if (var5 == Enum3.LEFT && hit(this.clearButton, var1, var3)) {
               this.pressButton("clear", var1, var3);
               this.clearSet();
               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            }

            if (var5 == Enum3.LEFT && hit(this.saveButton, var1, var3)) {
               this.pressButton("save", var1, var3);
               this.savePreset();
               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            }

            for (int var11 = 0; var11 < 36; var11++) {
               BoundsData var8 = this.slotBounds[var11];
               if (var8 != null && var8.contains(var1, var3)) {
                  this.mark("slotp:" + var11);
                  this.onSlotClicked(var11, var5);
                  return true;
               }
            }
         }

         return this.clickStepper(var1, var3) ? true : super.onMouseClicked(var1, var3, var5);
      }
   }

   private void selectServer(int var1) {
      if (var1 >= 0 && var1 < SERVERS.length && !SERVERS[var1].equals(module().method206())) {
         module().getServer().set(SERVERS[var1]);
         this.selectedSlot = -1;
         this.haveAt = 0L;
         this.resetScroll();
         this.replayMainFrom(this.listFrom);
         this.markDirty();
         this.invalidate();
      }
   }

   private void onSlotClicked(int var1, Enum3 var2) {
      InventoryPreset var3 = set();
      if (var2 == Enum3.RIGHT) {
         if (!var3.method871(var1)) {
            var3.method875(var1);
            if (this.selectedSlot == var1) {
               this.selectedSlot = -1;
            }

            this.markDirty();
            this.invalidate();
         }

         Zenith.getInstance().getClientSounds().method1570(true);
      } else {
         this.selectedSlot = this.selectedSlot == var1 ? -1 : var1;
         this.haveAt = 0L;
         Zenith.getInstance().getClientSounds().method1570(false);
         this.replayAsideFrom(this.slotFrom);
         this.invalidate();
      }
   }

   private boolean clickStepper(double var1, double var3) {
      BoundsData var5 = this.asideViewport();
      if (var5 != null && !var5.contains(var1, var3)) {
         return false;
      } else if (this.selectedSlot < 0) {
         return false;
      } else {
         InventoryPreset var6 = set();
         int var7 = this.selectedSlot;
         int var8 = WindowUtils.method5476() ? 10 : 1;
         if (hit(this.countMinus, var1, var3)) {
            var6.method878(var7, var6.method877(var7) - var8);
            return this.stepped("count:minus");
         } else if (hit(this.countPlus, var1, var3)) {
            var6.method878(var7, var6.method877(var7) + var8);
            return this.stepped("count:plus");
         } else if (hit(this.priceMinus, var1, var3)) {
            var6.method880(var7, var6.method879(var7) - 1000L * var8);
            return this.stepped("price:minus");
         } else if (hit(this.pricePlus, var1, var3)) {
            var6.method880(var7, var6.method879(var7) + 1000L * var8);
            return this.stepped("price:plus");
         } else if (hit(this.durabilityMinus, var1, var3)) {
            var6.method882(var7, var6.method881(var7) - 0.05F);
            return this.stepped("durability:minus");
         } else if (hit(this.durabilityPlus, var1, var3)) {
            var6.method882(var7, var6.method881(var7) + 0.05F);
            return this.stepped("durability:plus");
         } else {
            return false;
         }
      }
   }

   private boolean stepped(String var1) {
      this.mark("step:" + var1);
      this.markDirty();
      Zenith.getInstance().getClientSounds().method1570(false);
      return true;
   }

   private static boolean hit(BoundsData var0, double var1, double var3) {
      return var0 != null && var0.contains(var1, var3);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.searchInput().isSelected()) {
         if (var1 == 256) {
            this.searchInput().setText("");
            this.searchInput().setCursor(0);
            this.searchInput().setSelected(false);
            this.resetScroll();
            this.invalidate();
            return true;
         }

         if (this.searchInput().keyPressed(var1, var2, var3)) {
            this.resetScroll();
            this.invalidate();
            return true;
         }
      }

      if (this.nameInput().isSelected()) {
         if (var1 == 256) {
            this.nameInput().setSelected(false);
            return true;
         }

         if (var1 == 257 || var1 == 335) {
            this.savePreset();
            return true;
         }

         if (this.nameInput().keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.searchInput().isSelected() && this.searchInput().charTyped(var1, var2)) {
         this.resetScroll();
         this.invalidate();
         return true;
      } else {
         return this.nameInput().isSelected() && this.nameInput().charTyped(var1, var2) ? true : super.charTyped(var1, var2);
      }
   }

   @Override
   public void tick() {
      this.refreshSetCounts();
      this.refreshHave();
      String var1 = module().method206()
         + "|"
         + set().method872()
         + "|"
         + this.listMode
         + "|"
         + presets().method1229(module().method206()).size()
         + "|"
         + (this.listMode == 1 ? module().getInventorySnapshot().method1863().size() : 0);
      if (!var1.equals(this.contentSignature)) {
         this.contentSignature = var1;
         this.invalidate();
      }
   }

   private void refreshSetCounts() {
      this.inSetCounts.clear();
      InventoryPreset var1 = set();

      for (int var2 = 0; var2 < 36; var2++) {
         ItemMatcher var3 = var1.method870(var2);
         if (var3 != null && !var3.getItemStack().isEmpty()) {
            this.inSetCounts.merge(var3, var1.method877(var2), Integer::sum);
         }
      }
   }

   private void refreshHave() {
      if (this.selectedSlot < 0) {
         this.haveSlot = -1;
         this.haveCount = 0;
      } else {
         long var1 = System.currentTimeMillis();
         if (this.selectedSlot != this.haveSlot || var1 >= this.haveAt) {
            this.haveSlot = this.selectedSlot;
            this.haveAt = var1 + 400L;
            this.haveCount = 0;
            ItemMatcher var3 = set().method870(this.selectedSlot);
            if (mc.player != null && var3 != null) {
               for (int var4 = 0; var4 < mc.player.getInventory().size(); var4++) {
                  ItemStack var5 = mc.player.getInventory().getStack(var4);
                  if (var5 != null && !var5.isEmpty() && var3.method2672(var5)) {
                     this.haveCount = this.haveCount + Math.max(1, var5.getCount());
                  }
               }
            }
         }
      }
   }
}

package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import org.joml.Quaternionf;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.builder.PlayerPreview3D;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.panel.tools.ToolTab;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.Rarity;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.Stage;
import org.zenith.utility.render.Thumbnails;
import org.zenith.utility.render.WardrobePreview;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class WardrobeSectionPanel extends ToolTab {
   private final TextSetting lookName = new TextSetting(
      "wardrobe.look.name", "wardrobe.look.name.desc", "", "wardrobe.look.name.empty", TextSetting.Entry.method2125(32)
   );
   private final TextSetting lookCode = new TextSetting(
      "wardrobe.look.code", "wardrobe.look.code.desc", "", "wardrobe.look.code.empty", TextSetting.Entry.method2125(8)
   );
   private String lookStatus = "";
   private String lookShared = "";
   private static final int TAB_PETS = 0;
   private static final int TAB_CLOTHING = 1;
   private static final int TAB_ACCESSORIES = 2;
   private static final int TAB_SKINS = 3;
   private static final int TAB_WEAPONS = 4;
   private static final float SUB_ROW = 22.0F;
   private static final FocusSlotEnum[] DRESS = new FocusSlotEnum[]{
      FocusSlotEnum.HEAD, FocusSlotEnum.BODY, FocusSlotEnum.LEGS, FocusSlotEnum.BACK, FocusSlotEnum.CAPE
   };
   private final ChipStrip clothingSubs = new ChipStrip(
      "wardrobe.sub.all", "wardrobe.slot.head", "wardrobe.slot.body", "wardrobe.sub.legs", "wardrobe.slot.back", "wardrobe.sub.capes", "wardrobe.panel.sets"
   );
   private final ChipStrip accessorySubs = new ChipStrip("wardrobe.sub.all", "wardrobe.slot.watch", "wardrobe.sub.bracelets", "wardrobe.sub.neck");
   private final ChipStrip skinSubs = new ChipStrip("wardrobe.sub.all", "wardrobe.panel.characters", "wardrobe.panel.politics");
   private final ChipStrip weaponSubs = new ChipStrip(
      "wardrobe.sub.all",
      "wardrobe.slot.sword",
      "wardrobe.slot.axe",
      "wardrobe.slot.bow",
      "wardrobe.slot.mace",
      "wardrobe.slot.spear",
      "wardrobe.slot.crossbow"
   );
   private final ChipStrip itemSubs = new ChipStrip(
      "wardrobe.sub.all", "wardrobe.slot.gapple", "wardrobe.slot.enchanted_gapple", "wardrobe.slot.totem", "wardrobe.slot.anchor", "wardrobe.slot.crystal"
   );
   private final int[] subs = new int[8];
   private BoundsData subBounds;
   private ChipStrip shownSubs;
   private static final int TAB_ITEMS = 5;
   private static final int TAB_PRESETS = 6;
   private static final int TAB_SETTINGS = 7;
   private static final float SAVE_CARD = 62.0F;
   private static final float PRESET_ROW = 34.0F;
   private static final float TOGGLE_ROW = 30.0F;
   private static final float SLIDER_ROW = 36.0F;
   private SearchBox presetName;
   private BoundsData presetField;
   private BoundsData presetSave;
   private String presetStatus = "";
   private boolean presetStatusError;
   private long presetStatusAt;
   private String armedDelete = "";
   private long armedAt;
   private final Map<String, BoundsData> deleteBounds = new HashMap<>();
   private final Map<SliderSetting, BoundsData> sliderTracks = new HashMap<>();
   private SliderSetting draggingSlider;
   private static final String POLITICS = "politics";
   private static final FocusSlotEnum[] ACCESSORIES = new FocusSlotEnum[]{FocusSlotEnum.WATCH, FocusSlotEnum.NECK};
   private static final FocusSlotEnum[] WEAPONS = new FocusSlotEnum[]{
      FocusSlotEnum.SWORD, FocusSlotEnum.AXE, FocusSlotEnum.BOW, FocusSlotEnum.MACE, FocusSlotEnum.SPEAR, FocusSlotEnum.CROSSBOW
   };
   private static final FocusSlotEnum[] ITEMS = new FocusSlotEnum[]{
      FocusSlotEnum.GAPPLE, FocusSlotEnum.ENCHANTED_GAPPLE, FocusSlotEnum.TOTEM, FocusSlotEnum.ANCHOR, FocusSlotEnum.CRYSTAL
   };
   private static final int COLUMNS = 3;
   private static final float CARD_GAP = 5.0F;
   private static final float CARD_HEIGHT = 84.0F;
   private static final float CARD_LABEL = 19.0F;
   private static final float CARD_RADIUS = 6.0F;
   private static final float CAPTION_BAND = 14.0F;
   private static final float DEFAULT_FOCUS = 1.05F;
   private static final float LEGACY_BASE = 66.0F;
   private float autoZoom = 1.1F;
   private float previewFit = 66.0F;
   private final Thumbnails thumbnails = new Thumbnails();
   private final Stage stage = new Stage();
   private FocusSlotEnum focusSlot;
   private float stageYaw = 210.0F;
   private boolean stageDrag;
   private long stageFrame;
   private float stageX0;
   private float stageY0;
   private float stageX1;
   private float stageY1;
   private final Map<String, Animation> lift = new HashMap<>();
   private final Map<String, Animation> worn = new HashMap<>();
   private PlayerPreview3D preview;
   private HoveredData hovered;
   private int lastVersion = -1;
   private String lastSearch = "";
   private final Map<String, Animation> slides = new HashMap<>();
   private static final float WATCH_YAW = -90.0F;

   @Override
   public boolean pressFeedback() {
      return false;
   }

   @Override
   public float entryEnter(int var1, boolean var2) {
      return !var2 && this.getActiveTab() == 7 ? super.entryEnter(var1, false) : 1.0F;
   }

   @Override
   public String headerTitle() {
      return t("wardrobe.tab");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.COSMETICS;
   }

   @Override
   public String sectionDescription() {
      return "wardrobe.desc";
   }

   @Override
   public String emptyText() {
      return "wardrobe.panel.empty";
   }

   private static Wardrobe manager() {
      return Wardrobe.method2488();
   }

   @Override
   public void tick() {
      Wardrobe var1 = manager();
      if (var1 != null) {
         String var2 = this.searchQuery();
         if (var1.version() != this.lastVersion || !var2.equals(this.lastSearch)) {
            this.lastVersion = var1.version();
            this.lastSearch = var2;
            this.invalidate();
         }
      }
   }

   @Override
   public void selectTab(int var1) {
      if (this.preview != null) {
         this.preview.resetRotation();
      }

      this.lift.clear();
      this.hovered = null;
      this.focusSlot = null;
      this.stage.reset();
      super.selectTab(var1);
      this.shown();
   }

   @Override
   public void build() {
      this.tabs(
         new String[]{
            "wardrobe.category.pet",
            "wardrobe.category.clothing",
            "wardrobe.category.accessory",
            "wardrobe.category.character",
            "wardrobe.category.weapon",
            "wardrobe.category.item",
            "wardrobe.panel.looks",
            "wardrobe.panel.settings"
         }
      );
      this.tabIcons(
         new MenuGlyph[]{
            MenuGlyph.PVE, MenuGlyph.CHESTPLATE, MenuGlyph.TIMER, MenuGlyph.PROFILE, MenuGlyph.COMBAT, MenuGlyph.POTION, MenuGlyph.CONFIGS, MenuGlyph.INTERFACE
         }
      );
      Wardrobe var1 = manager();
      if (var1 != null) {
         switch (this.getActiveTab()) {
            case 0:
               this.section(var1, FocusSlotEnum.PET, true);
               break;
            case 1:
               this.subStrip(this.clothingSubs);
               int var10 = this.subs[1];
               if (var10 == 0) {
                  for (FocusSlotEnum var18 : DRESS) {
                     this.section(var1, var18, false);
                  }

                  this.buildSets(var1);
               } else if (var10 <= DRESS.length) {
                  this.section(var1, DRESS[var10 - 1], false);
                  if (DRESS[var10 - 1] == FocusSlotEnum.CAPE) {
                     this.caption("wardrobe.panel.capeHint");
                  }
               } else {
                  this.buildSets(var1);
               }
               break;
            case 2:
               this.subStrip(this.accessorySubs);
               int var9 = this.subs[2];
               if (var9 == 0 || var9 == 1) {
                  this.section(var1, FocusSlotEnum.WATCH, "wardrobe.slot.watch", var0 -> !isBracelet(var0));
               }

               if (var9 == 0 || var9 == 2) {
                  this.section(var1, FocusSlotEnum.WATCH, "wardrobe.sub.bracelets", WardrobeSectionPanel::isBracelet);
               }

               if (var9 == 0 || var9 == 3) {
                  this.section(var1, FocusSlotEnum.NECK, false);
               }
               break;
            case 3:
               this.subStrip(this.skinSubs);
               int var8 = this.subs[3];
               if (var8 != 2) {
                  this.section(var1, FocusSlotEnum.CHARACTER, "wardrobe.panel.characters", false);
               }

               if (var8 != 1) {
                  this.section(var1, FocusSlotEnum.CHARACTER, "wardrobe.panel.politics", true);
               }

               this.caption("wardrobe.panel.skinHides");
               break;
            case 4:
               this.subStrip(this.weaponSubs);
               int var7 = this.subs[4];
               if (var7 == 0) {
                  for (FocusSlotEnum var17 : WEAPONS) {
                     this.section(var1, var17, false);
                  }
               } else {
                  this.section(var1, WEAPONS[Math.min(var7, WEAPONS.length) - 1], false);
               }
               break;
            case 5:
               this.subStrip(this.itemSubs);
               int var2 = this.subs[5];
               if (var2 == 0) {
                  for (FocusSlotEnum var6 : ITEMS) {
                     this.section(var1, var6, false);
                  }
               } else {
                  this.section(var1, ITEMS[Math.min(var2, ITEMS.length) - 1], false);
               }
               break;
            case 6:
            default:
               this.buildPresets(var1);
               break;
            case 7:
               this.buildSettings(var1);
         }

         this.buildAside(var1);
      }
   }

   private void subStrip(ChipStrip var1) {
      this.shownSubs = var1;
      int var2 = this.getActiveTab();
      this.custom(22.0F, (var3, var4, var5, var6, var7, var8, var9, var10) -> {
         float var11 = var8 - 16.0F;
         float var12 = var7 + (22.0F - var1.height(var11)) / 2.0F;
         this.subBounds = new BoundsData(var6 + 8.0F, var12, var11, var1.height(var11));
         var1.render(var3, var4, var5, var6 + 8.0F, var12, var11, this.subs[var2], var9, var10);
      });
   }

   private FocusSlotEnum subSlot() {
      int var1 = this.getActiveTab();
      int var2 = var1 >= 0 && var1 < this.subs.length ? this.subs[var1] : 0;
      if (var2 <= 0) {
         return null;
      } else {
         return switch (var1) {
            case 1 -> var2 <= DRESS.length ? DRESS[var2 - 1] : null;
            case 2 -> var2 == 3 ? FocusSlotEnum.NECK : FocusSlotEnum.WATCH;
            default -> null;
            case 4 -> var2 <= WEAPONS.length ? WEAPONS[var2 - 1] : null;
            case 5 -> var2 <= ITEMS.length ? ITEMS[var2 - 1] : null;
         };
      }
   }

   private static boolean isBracelet(HoveredData var0) {
      String var1 = var0.getText();
      return "bracelet".equals(var0.getText4()) || var1.startsWith("watch_tennis") || var1.contains("bracelet");
   }

   private void section(Wardrobe var1, FocusSlotEnum var2, String var3, Predicate<HoveredData> var4) {
      var var5 = this.filter(var1, var2);
      var5.removeIf(var1x -> !var4.test(var1x));
      HoveredData var6 = var1.method2491(var2);
      boolean var7 = var6 != null && var4.test(var6);
      this.caption(t(var3) + " · " + var5.size() + (var7 ? " · " + t(var6.method1547()) : ""));
      this.cards(var1, var5);
   }

   private void section(Wardrobe var1, FocusSlotEnum var2, boolean var3) {
      var var4 = this.filter(var1, var2);
      String var5 = var3 ? t(var2.getWardrobeCategory().key()) : t(var2.key());
      HoveredData var6 = var1.method2491(var2);
      this.caption(var5 + " · " + var4.size() + (var6 != null ? " · " + t(var6.method1547()) : ""));
      this.cards(var1, var4);
   }

   private void section(Wardrobe var1, FocusSlotEnum var2, String var3, boolean var4) {
      var var5 = this.filter(var1, var2);
      var5.removeIf(var1x -> "politics".equals(var1x.getText4()) != var4);
      HoveredData var6 = var1.method2491(var2);
      boolean var7 = var6 != null && "politics".equals(var6.getText4()) == var4;
      this.caption(t(var3) + " · " + var5.size() + (var7 ? " · " + t(var6.method1547()) : ""));
      this.cards(var1, var5);
   }

   private void cards(Wardrobe var1, List<HoveredData> var2) {
      if (var2.isEmpty()) {
         this.info("wardrobe.panel.soon", () -> "", "wardrobe.panel.soon.desc");
         this.gap(5.0F);
      } else {
         this.grid(var1, var2);
         this.gap(5.0F);
      }
   }

   private List<HoveredData> filter(Wardrobe var1, FocusSlotEnum var2) {
      String var3 = this.searchQuery();
      ArrayList<org.zenith.utility.misc.HoveredData> var4 = new ArrayList<>();

      for (HoveredData var6 : var1.getRegistry().all()) {
         if (var6.getFocusSlotEnum() == var2
            && (var3.isEmpty() || t(var6.method1547()).toLowerCase(Locale.ROOT).contains(var3) || var6.getText().contains(var3))) {
            var4.add(var6);
         }
      }

      var4.sort((org.zenith.utility.misc.HoveredData var0, org.zenith.utility.misc.HoveredData var1x) -> Integer.compare(var1x.getRarity().getIntValue2(), var0.getRarity().getIntValue2()));
      return var4;
   }

   private void buildSets(Wardrobe var1) {
      List<WardrobeSetInfo> var2 = var1.getRegistry().method1426();
      if (var2.isEmpty()) {
         this.caption("wardrobe.panel.sets");
         this.info("wardrobe.panel.soon", () -> "", "wardrobe.panel.soon.desc");
      } else {
         this.caption(t("wardrobe.panel.sets") + " · " + var2.size());

         for (Object var4_o : (Iterable<?>)(Object)(var2)) {
            WardrobeSetInfo var4 = (WardrobeSetInfo)var4_o;
            this.row(var4.method7305())
               .icon(MenuGlyph.COSMETICS)
               .value(() -> var4.pieces().size() + " " + t("gui.unit.pieces"))
               .description(var4.method7306())
               .selected(() -> var1.method2500(var1.method2498(null, true)) == var4)
               .onClick(() -> {
                  var1.method2495(var4);
                  this.invalidate();
               })
               .add();
         }
      }
   }

   private void buildWorn(Wardrobe var1) {
      ArrayList var2 = new ArrayList();

      for (FocusSlotEnum var6 : FocusSlotEnum.values()) {
         HoveredData var7 = var1.method2491(var6);
         if (var7 != null) {
            var2.add(var7);
         }
      }

      if (!var2.isEmpty()) {
         this.caption(t("wardrobe.panel.worn") + " · " + var2.size());
         this.grid(var1, var2);
         this.gap(5.0F);
         this.action("wardrobe.panel.undressAll", MenuGlyph.CLOSE, () -> var2.size() + " " + t("gui.unit.pieces"), () -> {
            var1.method2496();
            this.invalidate();
         }, "wardrobe.panel.localOnly");
      }
   }

   private void buildLooks(Wardrobe var1, List<HoveredData> var2) {
      Cloud var3 = Zenith.getInstance().getCloud();
      this.caption("wardrobe.look.caption");
      if (var3 != null && var3.method7860()) {
         PartyMember var4 = var3.getPartyMember();
         if (!var2.isEmpty()) {
            this.setting(this.lookName);
            this.action("wardrobe.look.share", MenuGlyph.COPY, () -> this.lookShared.isEmpty() ? t("wardrobe.look.share.value") : this.lookShared, () -> {
               var var3x = new ArrayList<>(var1.method2504().values());
               this.lookStatus = t("wardrobe.look.saving");
               var4.method1167(this.lookName.getValue() == null ? "" : this.lookName.getValue(), var3x, var1xx -> {
                  this.lookShared = var1xx;
                  this.lookStatus = t("wardrobe.look.copied").replace("{c}", var1xx);
                  if (mc.keyboard != null) {
                     mc.keyboard.setClipboard(var1xx);
                  }

                  this.invalidate();
               });
               this.invalidate();
            }, "wardrobe.look.share.desc");
         }

         this.setting(this.lookCode);
         this.action(
            "wardrobe.look.apply",
            MenuGlyph.ACTION,
            () -> t("wardrobe.look.apply.value"),
            () -> {
               String var3x = this.lookCode.getValue() == null ? "" : this.lookCode.getValue().strip();
               if (var3x.length() < 4) {
                  this.lookStatus = t("wardrobe.look.badCode");
                  this.invalidate();
               } else {
                  this.lookStatus = t("wardrobe.look.loading");
                  var4.method1168(
                     var3x,
                     var2xx -> {
                        if (var2xx == null) {
                           this.lookStatus = t("wardrobe.look.missing");
                        } else {
                           int var3xx = var1.method2503(var2xx.getItems());
                           this.lookStatus = t("wardrobe.look.applied")
                              .replace("{n}", String.valueOf(var3xx))
                              .replace("{o}", var2xx.getText3().isEmpty() ? "?" : var2xx.getText3())
                              .replace("{t}", var2xx.name().isEmpty() ? var2xx.getText() : var2xx.name());
                        }

                        this.invalidate();
                     }
                  );
                  this.invalidate();
               }
            },
            "wardrobe.look.apply.desc"
         );
         if (!this.lookStatus.isEmpty()) {
            String var5 = this.lookStatus;
            this.info(var5, () -> "", "wardrobe.look.status.desc");
         }
      } else {
         this.info("wardrobe.look.offline", () -> "", "wardrobe.look.offline.desc");
      }
   }

   private void buildSettings(Wardrobe var1) {
      this.caption("wardrobe.settings.group.display");
      this.toggleCard(var1.showSelf);
      this.toggleCard(var1.showOthers);
      this.toggleCard(var1.armorOnSkins);
      this.caption("wardrobe.settings.group.effects");
      this.toggleCard(var1.particles);
      this.sliderCard(var1.particleBudget);
      this.caption("wardrobe.settings.group.pet");
      this.sliderCard(var1.petScale);
   }

   private void toggleCard(BooleanSetting var1) {
      this.custom(
         30.0F,
         (var2, var3, var4, var5, var6, var7, var8, var9) -> {
            BoundsData var10 = new BoundsData(var5 + 8.0F, var6 + 1.0F, var7 - 16.0F, 26.0F);
            boolean var11 = var10.contains(var3, var4);
            float var12 = this.hot("tg:" + var1.getKey(), var11);
            card(var2, var9, var10.getFloatValue(), var10.getFloatValue2(), var10.width(), var10.height(), var12, var8);
            float var13 = 15.0F;
            float var14 = 8.0F;
            float var15 = var10.getFloatValue() + var10.width() - 8.0F - var13;
            this.drawSwitch(var2, var1.getKey(), var15, var10.getFloatValue2() + (var10.height() - var14) / 2.0F, var13, var14, var1.isEnabled(), var8, var9);
            Font var16 = Fonts.NEW_MEDIUM.getFont(5.01F);
            Font var17 = Fonts.NEW_REGULAR.getFont(4.1F);
            float var18 = var15 - var10.getFloatValue() - 16.0F;
            var2.drawText(
               var16,
               var16.clip(var1.getName(), var18),
               var10.getFloatValue() + 8.0F,
               var10.getFloatValue2() + 5.0F,
               var9.getTextSecondary().getZenithColor().method7295(var9.getTextEnable().getZenithColor(), 0.3F + var12 * 0.7F).method7294(var8)
            );
            var2.drawText(
               var17,
               var17.clip(var1.getDescription(), var18),
               var10.getFloatValue() + 8.0F,
               var10.getFloatValue2() + 7.0F + var16.height(),
               var9.getTextTertiary().getZenithColor().method7294(var8)
            );
         },
         () -> {
            var1.toggle();
            this.invalidate();
         },
         null
      );
   }

   private void sliderCard(SliderSetting var1) {
      this.custom(
         36.0F,
         (var2, var3, var4, var5, var6, var7, var8, var9) -> {
            BoundsData var10 = new BoundsData(var5 + 8.0F, var6 + 1.0F, var7 - 16.0F, 32.0F);
            boolean var11 = var10.contains(var3, var4);
            boolean var12 = this.draggingSlider == var1;
            float var13 = this.hot("sl:" + var1.getKey(), var11 || var12);
            card(var2, var9, var10.getFloatValue(), var10.getFloatValue2(), var10.width(), var10.height(), var13, var8);
            Font var14 = Fonts.NEW_MEDIUM.getFont(5.01F);
            Font var15 = Fonts.NEW_MEDIUM.getFont(4.4F);
            ZenithColor var16 = var9.getPrimaryColor().getZenithColor();
            String var17 = format(var1);
            float var18 = pillWidth(var15, var17);
            pill(var2, var15, var10.getFloatValue() + var10.width() - 8.0F - var18, var10.getFloatValue2() + 4.5F, var17, var16, var8);
            var2.drawText(
               var14,
               var14.clip(var1.getName(), var10.width() - var18 - 24.0F),
               var10.getFloatValue() + 8.0F,
               var10.getFloatValue2() + 5.5F,
               var9.getTextSecondary().getZenithColor().method7295(var9.getTextEnable().getZenithColor(), 0.3F + var13 * 0.7F).method7294(var8)
            );
            float var19 = var10.getFloatValue() + 8.0F;
            float var20 = var10.width() - 16.0F;
            float var21 = var10.getFloatValue2() + var10.height() - 9.0F;
            float var22 = var1.getMax() - var1.getMin();
            float var23 = var22 <= 0.0F ? 0.0F : (var1.getValue3() - var1.getMin()) / var22;
            float var24 = this.hotValue("slv:" + var1.getKey(), var23);
            this.sliderTracks.put(var1, new BoundsData(var19 - 3.0F, var21 - 5.0F, var20 + 6.0F, 12.0F));
            var2.drawRoundedRect(
               var19, var21, var20, 2.5F, ProxyDotRadiusData.method8069(1.25F), var9.getDisableActiveBg().getZenithColor().method7294(var8 * 1.4F)
            );
            var2.drawRoundedRect(var19, var21, Math.max(2.5F, var20 * var24), 2.5F, ProxyDotRadiusData.method8069(1.25F), var16.method7294(var8));
            float var25 = 6.0F + var13 * 1.5F;
            float var26 = var19 + var20 * var24 - var25 / 2.0F;
            var2.drawRoundedRect(
               var26,
               var21 + 1.25F - var25 / 2.0F,
               var25,
               var25,
               ProxyDotRadiusData.method8069(var25 / 2.0F),
               ZenithColor.INSTANCE.method7295(var16, 0.15F).method7294(var8)
            );
            if (var11) {
               HoverInfo.request(var1.getName(), var1.getDescription());
            }
         }
      );
   }

   private float hotValue(String var1, float var2) {
      Animation var3 = this.slides.computeIfAbsent(var1, var1x -> new Animation(140L, var2, Easing.INSTANCE12));
      return var3.method2808(var2);
   }

   private static String format(SliderSetting var0) {
      float var1 = var0.getValue3();
      String var2 = var0.getSuffix() == null ? "" : var0.getSuffix();
      String var3 = var0.getFloatValue2() >= 1.0F ? String.valueOf(Math.round(var1)) : String.format(Locale.ROOT, "%.2f", var1);
      return var3 + var2;
   }

   private void slide(SliderSetting var1, double var2) {
      BoundsData var4 = this.sliderTracks.get(var1);
      if (var4 != null) {
         float var5 = var4.getFloatValue() + 3.0F;
         float var6 = var4.width() - 6.0F;
         float var7 = Math.max(0.0F, Math.min(1.0F, (float)((var2 - var5) / var6)));
         float var8 = var1.getMin() + (var1.getMax() - var1.getMin()) * var7;
         float var9 = var1.getFloatValue2() <= 0.0F ? 0.01F : var1.getFloatValue2();
         float var10 = Math.round(var8 / var9) * var9;
         var1.method2400(Math.max(var1.getMin(), Math.min(var1.getMax(), var10)));
      }
   }

   private SearchBox presetInput() {
      if (this.presetName == null) {
         this.presetName = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(4.8F), t("wardrobe.presets.name.hint"), 32.0F);
      }

      return this.presetName;
   }

   private void buildPresets(Wardrobe var1) {
      this.custom(
         66.0F, (var2x, var3, var4x, var5, var6, var7x, var8x, var9x) -> this.renderSaveCard(var2x, var1, var3, var4x, var5, var6, var7x, var8x, var9x)
      );
      this.buildWorn(var1);
      List<Wardrobe.Data> var2 = var1.method2505();
      this.caption(t("wardrobe.presets.caption") + " · " + var2.size());
      if (var2.isEmpty()) {
         this.custom(
            78.0F,
            (var1x, var2x, var3, var4x, var5, var6, var7x, var8x) -> this.emptyCard(
               var1x,
               var8x,
               var4x + 8.0F,
               var5 + 2.0F,
               var6 - 16.0F,
               72.0F,
               MenuGlyph.CONFIGS,
               t("wardrobe.presets.empty"),
               t("wardrobe.presets.empty.desc"),
               var7x
            )
         );
      } else {
         for (Object var4_o : (Iterable<?>)(Object)(var2)) {
            Wardrobe.Data var4 = (Wardrobe.Data)var4_o;
            this.custom(
               34.0F,
               (var3, var4x, var5, var6, var7x, var8x, var9x, var10) -> this.renderPreset(var3, var1, var4, var4x, var5, var6, var7x, var8x, var9x, var10),
               () -> {
                  int var3 = var1.method2508(var4);
                  this.status(t("wardrobe.presets.applied").replace("{n}", var4.name()) + " · " + var3, false);
                  this.invalidate();
               },
               () -> var1.method2510(var4)
            );
         }
      }

      ArrayList var9 = new ArrayList();

      for (FocusSlotEnum var7 : FocusSlotEnum.values()) {
         HoveredData var8 = var1.method2491(var7);
         if (var8 != null) {
            var9.add(var8);
         }
      }

      this.gap(GuiStyle.PADDING.intValue());
      this.buildLooks(var1, var9);
   }

   private void status(String var1, boolean var2) {
      this.presetStatus = var1;
      this.presetStatusError = var2;
      this.presetStatusAt = System.currentTimeMillis();
   }

   private void renderSaveCard(MenuDrawContext var1, Wardrobe var2, int var3, int var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      BoundsData var10 = new BoundsData(var5 + 8.0F, var6 + 2.0F, var7 - 16.0F, 62.0F);
      ZenithColor var11 = var9.getPrimaryColor().getZenithColor();
      accentCard(var1, var9, var10.getFloatValue(), var10.getFloatValue2(), var10.width(), var10.height(), 0.0F, var8, var11, 0.18F);
      int var12 = var2.method2497();
      Font var13 = Fonts.NEW_SEMIBOLD.getFont(5.8F);
      Font var14 = Fonts.NEW_REGULAR.getFont(4.3F);
      float var15 = var10.getFloatValue() + 8.0F;
      var1.drawText(var13, t("wardrobe.presets.current"), var15, var10.getFloatValue2() + 7.0F, var9.getTextEnable().getZenithColor().method7294(var8));
      String var16 = var12 == 0 ? t("wardrobe.presets.nothing") : t("wardrobe.presets.currentCount").replace("{n}", String.valueOf(var12));
      long var17 = System.currentTimeMillis() - this.presetStatusAt;
      boolean var19 = !this.presetStatus.isEmpty() && var17 < 5000L;
      ZenithColor var20 = var19 ? (this.presetStatusError ? DANGER : OK) : var9.getTextTertiary().getZenithColor();
      float var21 = var19 && var17 > 4000L ? 1.0F - (float)(var17 - 4000L) / 1000.0F : 1.0F;
      var1.drawText(
         var14,
         var14.clip(var19 ? this.presetStatus : var16, var10.width() - 16.0F),
         var15,
         var10.getFloatValue2() + 9.0F + var13.height(),
         var20.method7294(var8 * var21)
      );
      float var22 = var10.getFloatValue2() + var10.height() - 24.0F;
      Font var23 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String var24 = t("wardrobe.presets.save");
      float var25 = var23.width(var24) + 26.0F;
      this.presetSave = new BoundsData(var10.getFloatValue() + var10.width() - 7.0F - var25, var22, var25, 17.0F);
      this.presetField = new BoundsData(var15 - 1.0F, var22, this.presetSave.getFloatValue() - 5.0F - (var15 - 1.0F), 17.0F);
      SearchBox var26 = this.presetInput();
      boolean var27 = var26.isSelected();
      float var28 = this.hot("pf:focus", var27);
      ProxyDotRadiusData var29 = ProxyDotRadiusData.method8069(CARD_ROUND);
      var1.drawRoundedRect(
         this.presetField.getFloatValue(),
         this.presetField.getFloatValue2(),
         this.presetField.width(),
         this.presetField.height(),
         var29,
         var9.getPanelLeftBackground().getZenithColor().method7294(var8)
      );
      var1.drawRoundedBorder(
         this.presetField.getFloatValue(),
         this.presetField.getFloatValue2(),
         this.presetField.width(),
         this.presetField.height(),
         0.2F + var28 * 0.15F,
         var29,
         var9.getFieldBorder().getZenithColor().method7294(2.5F).method7295(var11.method7292(0.7F), var28).method7294(var8)
      );
      MenuGlyph.TEXT
         .draw(
            var1,
            this.presetField.getFloatValue() + 5.0F,
            this.presetField.getFloatValue2() + 5.5F,
            var9.getTextTertiary().getZenithColor().method7295(var11, var28).method7294(var8)
         );
      Font var30 = Fonts.NEW_MEDIUM.getFont(4.8F);
      var26.setFont(var30);
      var26.setWidth(this.presetField.width() - 20.0F);
      var26.method987(
         var1,
         this.presetField.getFloatValue() + 15.0F,
         this.presetField.getFloatValue2() + (17.0F - var30.height()) / 2.0F,
         var9.getTextEnable().getZenithColor().method7294(var8),
         var9.getTextTertiary().getZenithColor().method7294(var8)
      );
      if (this.button(var1, var9, "presetSave", this.presetSave, MenuGlyph.CHECK, var24, 1, var12 > 0, var3, var4, var8)) {
         HoverInfo.request(var24, t("wardrobe.presets.save.desc"));
      }

      if (var12 == 0) {
         this.presetSave = null;
      }
   }

   private void renderPreset(
      MenuDrawContext var1, Wardrobe var2, Wardrobe.Data var3, int var4, int var5, float var6, float var7, float var8, float var9, ZenithStyle var10
   ) {
      BoundsData var11 = new BoundsData(var6 + 8.0F, var7 + 1.0F, var8 - 16.0F, 30.0F);
      boolean var12 = var11.contains(var4, var5);
      boolean var13 = var2.method2510(var3);
      String var14 = "pr:" + var3.name() + ":" + var3.getLongValue();
      float var15 = this.hot(var14, var12);
      float var16 = this.hot(var14 + ":on", var13);
      ZenithColor var17 = var10.getPrimaryColor().getZenithColor();
      if (var16 > 0.01F) {
         accentCard(var1, var10, var11.getFloatValue(), var11.getFloatValue2(), var11.width(), var11.height(), var15, var9, var17, var16 * 0.6F);
      } else {
         card(var1, var10, var11.getFloatValue(), var11.getFloatValue2(), var11.width(), var11.height(), var15, var9);
      }

      ZenithColor var18 = var17;
      int var19 = -1;
      ArrayList var20 = new ArrayList();

      for (String var22 : var3.getItems().values()) {
         HoveredData var23 = var2.getRegistry().method1421(var22);
         if (var23 != null) {
            var20.add(t(var23.method1547()));
            if (var23.getRarity().getIntValue2() > var19) {
               var19 = var23.getRarity().getIntValue2();
               var18 = color(var23.getRarity().getIntValue());
            }
         }
      }

      float var34 = var11.height() - 8.0F;
      tile(var1, var10, var11.getFloatValue() + 4.0F, var11.getFloatValue2() + 4.0F, var34, var18, 0.45F + var15 * 0.2F, var9);
      MenuGlyph.COSMETICS
         .draw(var1, var11.getFloatValue() + 4.0F + (var34 - 6.0F) / 2.0F, var11.getFloatValue2() + 4.0F + (var34 - 6.0F) / 2.0F, var18.method7294(var9));
      Font var35 = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font var36 = Fonts.NEW_REGULAR.getFont(4.1F);
      Font var24 = Fonts.NEW_MEDIUM.getFont(4.0F);
      float var25 = var11.getFloatValue() + var11.width() - 6.0F;
      float var26 = 11.0F;
      BoundsData var27 = new BoundsData(var25 - var26, var11.getFloatValue2() + (var11.height() - var26) / 2.0F, var26, var26);
      this.deleteBounds.put(var14, var27);
      boolean var28 = this.armedDelete.equals(var14) && System.currentTimeMillis() - this.armedAt < 3000L;
      boolean var29 = var27.contains(var4, var5);
      float var30 = this.hot(var14 + ":del", var29 || var28);
      if (var15 > 0.02F || var28) {
         var1.drawRoundedRect(
            var27.getFloatValue(),
            var27.getFloatValue2(),
            var26,
            var26,
            ProxyDotRadiusData.method8069(var26 / 2.0F),
            DANGER.method7292(0.12F + 0.25F * var30).method7294(var9 * Math.max(var15, var28 ? 1.0F : 0.0F))
         );
         MenuGlyph.CLOSE
            .draw(
               var1,
               var27.getFloatValue() + (var26 - 6.0F) / 2.0F,
               var27.getFloatValue2() + (var26 - 6.0F) / 2.0F,
               DANGER.method7294(var9 * Math.max(var15, var28 ? 1.0F : 0.0F))
            );
         var25 = var27.getFloatValue() - 4.0F;
      }

      if (var28) {
         String var31 = t("wardrobe.presets.again");
         float var32 = pillWidth(var24, var31);
         pill(var1, var24, var25 - var32, var11.getFloatValue2() + (var11.height() - var24.height() - 3.6F) / 2.0F, var31, DANGER, var9);
         var25 -= var32 + 4.0F;
      } else if (var13) {
         String var37 = t("wardrobe.presets.wearing");
         float var39 = pillWidth(var24, var37);
         pill(var1, var24, var25 - var39, var11.getFloatValue2() + (var11.height() - var24.height() - 3.6F) / 2.0F, var37, OK, var9);
         var25 -= var39 + 4.0F;
      }

      float var38 = var11.getFloatValue() + 4.0F + var34 + 7.0F;
      float var40 = var25 - var38;
      var1.drawText(
         var35,
         var35.clip(var3.name(), var40),
         var38,
         var11.getFloatValue2() + 5.0F,
         var10.getTextSecondary().getZenithColor().method7295(var10.getTextEnable().getZenithColor(), Math.max(var16, var15 * 0.7F)).method7294(var9)
      );
      String var33 = t("wardrobe.presets.items").replace("{n}", String.valueOf(var20.size())) + "  ·  " + String.join(", ", var20);
      var1.drawText(
         var36, var36.clip(var33, var40), var38, var11.getFloatValue2() + 7.0F + var35.height(), var10.getTextTertiary().getZenithColor().method7294(var9)
      );
      if (var12 && !var29) {
         HoverInfo.request(var3.name(), t("wardrobe.presets.row.desc"));
      }

      if (var29) {
         HoverInfo.request(t("wardrobe.presets.delete"), t("wardrobe.presets.delete.desc"));
      }
   }

   private void savePreset() {
      Wardrobe var1 = manager();
      if (var1 != null) {
         if (var1.method2497() == 0) {
            this.status(t("wardrobe.presets.nothing"), true);
         } else {
            String var2 = this.presetInput().getText() == null ? "" : this.presetInput().getText().strip();
            if (var2.isEmpty()) {
               int var3 = var1.method2505().size() + 1;

               do {
                  var2 = t("wardrobe.presets.defaultName") + " " + var3++;
               } while (var1.method2507(var2));
            }

            boolean var4 = var1.method2507(var2);
            var1.method2506(var2);
            this.presetInput().setText("");
            this.presetInput().setCursor(0);
            this.presetInput().setSelected(false);
            this.status(t(var4 ? "wardrobe.presets.replaced" : "wardrobe.presets.saved").replace("{n}", var2), false);
            Zenith.getInstance().getManager().method7788();
            this.invalidate();
         }
      }
   }

   private void grid(Wardrobe var1, List<HoveredData> var2) {
      for (byte var3 = 0; var3 < var2.size(); var3 += 3) {
         HoveredData[] var4 = new HoveredData[Math.min(3, var2.size() - var3)];

         for (int var5 = 0; var5 < var4.length; var5++) {
            var4[var5] = (HoveredData)var2.get(var3 + var5);
         }

         this.custom(
            89.0F,
            (var3x, var4x, var5x, var6, var7, var8, var9, var10) -> this.renderRow(var3x, var1, var4, var4x, var5x, var6, var7, var8, var9, var10),
            () -> this.toggle(var1),
            null
         );
      }
   }

   private void toggle(Wardrobe var1) {
      HoveredData var2 = this.hovered;
      if (var2 != null) {
         var1.method2494(var2);
         this.invalidate();
      }
   }

   private void renderRow(
      MenuDrawContext var1, Wardrobe var2, HoveredData[] var3, int var4, int var5, float var6, float var7, float var8, float var9, ZenithStyle var10
   ) {
      float var11 = (var8 - 16.0F - 10.0F) / 3.0F;
      if (var5 >= var7 && var5 < var7 + 84.0F + 5.0F) {
         this.hovered = null;
      }

      boolean var12 = var5 >= var7 && var5 < var7 + 84.0F;

      for (int var13 = 0; var13 < var3.length; var13++) {
         float var14 = var6 + 8.0F + (var11 + 5.0F) * var13;
         boolean var15 = var12 && var4 >= var14 && var4 < var14 + var11;
         if (var15) {
            this.hovered = var3[var13];
         }

         this.renderCard(var1, var2, var3[var13], var15, var14, var7, var11, var9, var10);
      }
   }

   private void renderCard(
      MenuDrawContext var1, Wardrobe var2, HoveredData var3, boolean var4, float var5, float var6, float var7, float var8, ZenithStyle var9
   ) {
      boolean var10 = var2.method2490(var3);
      float var11 = animate(this.lift, var3.getText(), var4, 150L);
      float var12 = animate(this.worn, var3.getText(), var10, 220L);
      Rarity var13 = var3.getRarity();
      ZenithColor var14 = color(var13.getIntValue());
      ProxyDotRadiusData var15 = ProxyDotRadiusData.method8069(6.0F);
      var1.drawRoundedRect(
         var5, var6, var7, 84.0F, var15, var9.getFieldSurfaceBackground().getZenithColor().method7294((2.2F + var11 * 1.6F + var12 * 2.2F) * var8)
      );
      float var16 = 37.8F;
      var1.drawRoundedRect(var5, var6 + 84.0F - var16, var7, var16, var15, var14.method7294((0.07F + var11 * 0.06F + var12 * 0.08F) * var8));
      float var17 = var13.isFlag() ? 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 420.0 + var5 * 0.05) : 0.0F;
      ZenithColor var18 = var14.method7294((0.45F + var11 * 0.35F + var12 * 0.2F + var17 * 0.2F) * var8);
      var1.drawRoundedBorder(var5, var6, var7, 84.0F, 1.0F + var12 * 0.6F, var15, var18);
      var1.drawRoundedRect(var5 + 6.0F, var6, var7 - 12.0F, 1.6F, ProxyDotRadiusData.method8069(0.8F), var14.method7294((0.8F + var17 * 0.2F) * var8));
      float var19 = 65.0F;
      float var20 = var5 + var7 / 2.0F;
      float var21 = var6 + var19 / 2.0F + 2.0F;
      float var22 = (Math.min(var7, var19) - 10.0F) * (1.0F + var11 * 0.08F);
      Class15 var23 = var2.getRegistry().method1427(var3);
      boolean var24 = this.isFullyVisible()
         && this.thumbnails.method7855(var3, var23, var20, var21, var22, var5 + 1.0F, var6 + 1.0F, var5 + var7 - 1.0F, var6 + var19, var11);
      if (!var24) {
         MenuGlyph var25 = switch (var3.method1546()) {
            case PET -> MenuGlyph.PVE;
            case WEAPON -> MenuGlyph.COMBAT;
            case ITEM -> MenuGlyph.POTION;
            case CHARACTER -> MenuGlyph.PROFILE;
            case CAPE -> MenuGlyph.SHIELD;
            case ACCESSORY -> MenuGlyph.TIMER;
            default -> MenuGlyph.COSMETICS;
         };
         var25.draw(var1, var20 - 3.0F, var21 - 3.0F, var9.getTextTertiary().getZenithColor().method7294(var8 * 0.6F));
      }

      if (var12 > 0.01F) {
         float var31 = 9.0F;
         float var26 = var5 + var7 - 4.0F - var31;
         var1.pushMatrix();

         try {
            var1.getMatrices().translate(var26 + var31 / 2.0F, var6 + 5.0F + var31 / 2.0F);
            var1.getMatrices().scale(var12, var12);
            var1.drawRoundedRect(-var31 / 2.0F, -var31 / 2.0F, var31, var31, ProxyDotRadiusData.method8069(var31 / 2.0F), var14.method7294(var8));
            MenuGlyph.CHECK.draw(var1, -3.0F, -3.0F, new ZenithColor(14, 14, 16).method7294(var8));
         } finally {
            var1.popMatrix();
         }
      }

      Font var32 = Fonts.NEW_MEDIUM.getFont(4.4F);
      Font var33 = Fonts.NEW_MEDIUM.getFont(3.6F);
      String var27 = fit(var32, t(var3.method1547()), var7 - 8.0F);
      var1.drawText(
         var32,
         var27,
         var20 - var32.width(var27) / 2.0F,
         var6 + var19 + 2.0F,
         var9.getTextSecondary().getZenithColor().method7295(var9.getTextEnable().getZenithColor(), Math.max(var12, var11 * 0.5F)).method7294(var8)
      );
      String var28 = t(var13.key()).toUpperCase(Locale.ROOT);
      var1.drawText(var33, var28, var20 - var33.width(var28) / 2.0F, var6 + var19 + 2.0F + var32.height() + 1.5F, var14.method7294(var8 * 0.9F));
      if (var4) {
         HoverInfo.request(
            t(var3.method1547()),
            t(var13.key())
               + " · "
               + t(var3.getFocusSlotEnum().key())
               + ". "
               + t(var3.method1548())
               + " "
               + t(var10 ? "wardrobe.panel.unequip" : "wardrobe.panel.equip")
         );
      }
   }

   private static float animate(Map<String, Animation> var0, String var1, boolean var2, long var3) {
      Animation var5 = (Animation)var0.get(var1);
      if (var5 == null) {
         if (!var2) {
            return 0.0F;
         }

         var5 = new Animation(var3, 0.0F, Easing.INSTANCE12);
         var0.put(var1, var5);
      }

      float var6 = var5.method2808(var2 ? 1.0F : 0.0F);
      if (var6 < 0.004F && !var2) {
         var0.remove(var1);
         return 0.0F;
      } else {
         return var6;
      }
   }

   private static ZenithColor color(int var0) {
      return new ZenithColor(var0 >> 16 & 0xFF, var0 >> 8 & 0xFF, var0 & 0xFF);
   }

   private void buildAside(Wardrobe var1) {
      this.asideFull(
         (var2, var3, var4, var5, var6, var7, var8, var9) -> {
            Font var10 = Fonts.NEW_MEDIUM.getFont(4.0F);
            var2.drawText(
               var10, HoverInfo.spaced(t("wardrobe.panel.fitting")), var5 + 8.0F, var6 + 4.0F, var9.getTextTertiary().getZenithColor().method7294(var8)
            );
            float var11 = var6 + 14.0F;
            float var12 = NLMenuScreen.contentBottom - HoverInfo.boxHeight() - GuiStyle.PADDING.intValue();
            float var13 = var5 + 4.0F;
            float var14 = var7 - 8.0F;
            float var15 = Math.max(60.0F, var12 - var11);
            float var16 = Math.min(var15 / 2.55F, var14 / 1.3F);
            if (this.preview == null) {
               this.preview = new PlayerPreview3D(var13, var11, var14, var15);
               this.preview.setIdleSpin(10.0F);
               this.autoZoom = var16 / 60.0F;
               this.preview.setZoom(this.autoZoom);
            } else {
               this.preview.setBounds(var13, var11, var14, var15);
               if (Math.abs(this.preview.getZoom() - this.autoZoom) < 5.0E-4F) {
                  this.autoZoom = var16 / 60.0F;
                  this.preview.setZoom(this.autoZoom);
               }
            }

            this.previewFit = var16;
            this.preview.setMaxSize(var16 * 2.4F);
            HoveredData var17 = this.mainViewport() != null && this.mainViewport().contains(var3, var4) ? this.hovered : null;
            if (var17 != null) {
               this.focusSlot = var17.getFocusSlotEnum();
            }

            if (this.getActiveTab() == 0 && mc.player != null) {
               HoveredData var18 = var17 != null && var17.getFocusSlotEnum() == FocusSlotEnum.PET ? var17 : var1.method2491(FocusSlotEnum.PET);
               if (var18 == null) {
                  var var19 = this.filter(var1, FocusSlotEnum.PET);
                  var18 = var19.isEmpty() ? null : (HoveredData)var19.get(0);
               }

               if (var18 != null && var8 > 0.01F && this.isFullyVisible()) {
                  long var30 = System.nanoTime();
                  float var31 = this.stageFrame == 0L ? 0.0F : Math.min(0.1F, (float)(var30 - this.stageFrame) / 1.0E9F);
                  this.stageFrame = var30;
                  if (!this.stageDrag) {
                     this.stageYaw += var31 * 14.0F;
                  }

                  this.stageX0 = var13;
                  this.stageY0 = var11;
                  this.stageX1 = var13 + var14;
                  this.stageY1 = var11 + var15;
                  float var32 = var11 + var15 * 0.8F;
                  var2.drawRoundedRect(
                     var13 + var14 * 0.2F,
                     var32 - 4.0F,
                     var14 * 0.6F,
                     8.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     new ZenithColor(0, 0, 0).method7294(var8 * 0.25F)
                  );
                  this.stage
                     .method893(
                        var2, var18, var1.getRegistry().method1427(var18), this.stageX0, this.stageY0, this.stageX1, this.stageY1, this.stageYaw, -14.0F
                     );
                  if (var3 >= this.stageX0 && var3 <= this.stageX1 && var4 >= this.stageY0 && var4 <= this.stageY1) {
                     HoverInfo.request(t(var18.method1547()), t("wardrobe.panel.drag"));
                  }

                  return;
               }
            }

            this.applyFocus(var1);
            FocusSlotEnum var28 = this.subSlot();
            boolean var29 = this.getActiveTab() == 4 || this.getActiveTab() == 5;
            FocusSlotEnum var20 = var17 != null && var17.getFocusSlotEnum().method543()
               ? var17.getFocusSlotEnum()
               : (var28 != null && var28.method543() ? var28 : equippedHeldSlot(var1, this.getActiveTab() == 5 ? ITEMS : WEAPONS));
            this.preview.setForcedMainHand(var29 && var20 != null ? var20.method544() : null);
            this.preview.setHideHeldItems(!var29);
            HoveredData var21 = var17 != null && var17.getFocusSlotEnum() == FocusSlotEnum.PET ? var17 : var1.method2491(FocusSlotEnum.PET);
            boolean var22 = this.getActiveTab() == 1 && (var28 == FocusSlotEnum.CAPE || var17 != null && var17.getFocusSlotEnum() == FocusSlotEnum.CAPE);
            this.preview
               .setQueueOverlay(
                  var21 != null && this.getActiveTab() != 2 && !var22
                     ? (var2x, var3x, var4x, var5x, var6x) -> renderPet(var1, var21, var2x, var3x, var4x)
                     : null
               );
            if (mc.player == null) {
               Font var23 = Fonts.NEW_REGULAR.getFont(5.0F);
               String var24 = t("wardrobe.panel.enterWorld");
               var2.drawText(
                  var23,
                  var24,
                  var5 + (var7 - var23.width(var24)) / 2.0F,
                  var11 + var15 / 2.0F - 3.0F,
                  var9.getTextTertiary().getZenithColor().method7294(var8)
               );
            } else {
               if (var8 > 0.01F && this.isFullyVisible()) {
                  var2.enableScissor(var13, var11, var13 + var14, var11 + var15);
                  RenderState.method1883(1.0F, 1.0F, 1.0F, var8);
                  var1.setHoveredData(var17);
                  var1.setFlag(true);

                  try {
                     this.preview.render(var2, mc.player, var3, var4);
                  } finally {
                     var1.setFlag(false);
                     var1.setHoveredData(null);
                     RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
                  }

                  var2.disableScissor();
               }

               if (var3 >= var13 && var3 <= var13 + var14 && var4 >= var11 && var4 <= var11 + var15) {
                  HoverInfo.request(t("wardrobe.panel.fitting"), t("wardrobe.panel.drag") + " " + t("wardrobe.panel.tryOn"));
               }
            }
         }
      );
   }

   private void applyFocus(Wardrobe var1) {
      FocusSlotEnum var2 = this.focusSlot;
      int var3 = this.getActiveTab();
      FocusSlotEnum var4 = this.subSlot();
      if (var4 != null && (var2 == null || var2.getWardrobeCategory() != var4.getWardrobeCategory() || var2.method543())) {
         var2 = var4;
      }

      if (var3 != 1 || var2 != FocusSlotEnum.CAPE && var4 != FocusSlotEnum.CAPE) {
         if (var3 == 2) {
            if (var2 != FocusSlotEnum.WATCH && var2 != FocusSlotEnum.NECK) {
               var2 = var1.method2491(FocusSlotEnum.WATCH) != null && var1.method2491(FocusSlotEnum.NECK) == null ? FocusSlotEnum.WATCH : FocusSlotEnum.NECK;
            }
         } else if (var3 == 1) {
            if (var2 == null || var2.getWardrobeCategory() != WardrobeCategory.CLOTHING) {
               var2 = null;
            }
         } else if (var3 == 4) {
            var2 = var4 != null ? var4 : FocusSlotEnum.SWORD;
         } else if (var3 == 5) {
            var2 = var4 != null ? var4 : equippedHeldSlot(var1, ITEMS);
         } else {
            var2 = null;
         }
      } else {
         var2 = FocusSlotEnum.CAPE;
      }

      if (var2 == null) {
         this.preview.setFocus(1.05F, 1.0F, Float.NaN);
      } else {
         switch (var2) {
            case HEAD:
               this.focus(1.62F, 2.3F, 0.0F);
               break;
            case BODY:
               this.focus(1.12F, 1.5F, 25.0F);
               break;
            case LEGS:
               this.focus(0.45F, 1.6F, 25.0F);
               break;
            case BACK:
               this.focus(1.15F, 1.35F, 180.0F);
               break;
            case CAPE:
               this.focus(1.0F, 1.05F, 180.0F);
               break;
            case NECK:
               this.focus(1.4F, 3.2F, 0.0F);
               break;
            case WATCH:
               this.focus(0.98F, 3.6F, -90.0F);
               break;
            default:
               if (var2.getWardrobeCategory() == WardrobeCategory.ITEM) {
                  this.focus(0.95F, 2.3F, -35.0F);
               } else if (var2.method542()) {
                  this.focus(0.98F, 1.7F, -35.0F);
               } else {
                  this.preview.setFocus(1.05F, 1.0F, Float.NaN);
               }
         }
      }
   }

   private void focus(float var1, float var2, float var3) {
      this.preview.setFocus(var1, Math.max(1.0F, var2 * 66.0F / Math.max(1.0F, this.previewFit)), var3);
   }

   public void devTab(int var1) {
      this.selectTab(var1);
   }

   private static FocusSlotEnum equippedHeldSlot(Wardrobe var0, FocusSlotEnum[] var1) {
      for (FocusSlotEnum var5 : var1) {
         if (var0.method2491(var5) != null) {
            return var5;
         }
      }

      return var1[0];
   }

   private static void renderPet(Wardrobe var0, HoveredData var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4) {
      Class15 var5 = var0.getRegistry().method1427(var1);
      if (var5 != null && var5.data3 != null) {
         float var6 = var5.data3.getFloatValue() * var0.petScale.getValue3();
         var2.multiply(new Quaternionf().rotationY((float) Math.PI));
         var2.scale(-1.0F, -1.0F, 1.0F);
         var2.translate(0.0F, -1.501F, 0.0F);
         var2.translate(0.95F, 1.5F, 0.35F);
         var2.multiply(new Quaternionf().rotationY((float)Math.toRadians(-35.0)));
         var2.scale(var6, var6, var6);
         var2.translate(0.0F, -1.5F, 0.0F);
         float var7 = (float)(System.nanoTime() / 1000000L % 1000000L) / 1000.0F;
         WardrobePreview.method3147(var1, var5, var2, var3, var4, var7);
      }
   }

   @Override
   public void renderMainOverlay(MenuDrawContext var1) {
      if (!this.isFullyVisible()) {
         this.thumbnails.method7856();
      } else {
         this.thumbnails.method7857(var1);
      }
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.subBounds = null;
      this.presetField = null;
      this.presetSave = null;
      this.deleteBounds.clear();
      this.sliderTracks.clear();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.presetName != null && this.presetName.isSelected()) {
         if (var1 == 256) {
            this.presetName.setSelected(false);
            return true;
         }

         if (var1 == 257 || var1 == 335) {
            this.savePreset();
            return true;
         }

         if (this.presetName.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.presetName != null && this.presetName.isSelected() && this.presetName.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.subBounds != null && this.shownSubs != null && this.subBounds.contains(var1, var3)) {
         int var6 = this.shownSubs.hovered();
         int var7 = this.getActiveTab();
         if (var6 >= 0 && var7 >= 0 && var7 < this.subs.length) {
            if (var6 != this.subs[var7]) {
               this.subs[var7] = var6;
               this.focusSlot = this.subSlot();
               this.hovered = null;
               this.lift.clear();
               this.resetScroll();
               this.invalidate();
            }

            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }
      }

      if (this.getActiveTab() == 6) {
         boolean var12 = this.presetField != null && this.presetField.contains(var1, var3);
         this.presetInput().setSelected(var12);
         if (var12) {
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }

         if (var5 == Enum3.LEFT && this.presetSave != null && this.presetSave.contains(var1, var3)) {
            this.pressButton("presetSave", var1, var3);
            this.savePreset();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }

         for (Map.Entry var8 : this.deleteBounds.entrySet()) {
            if (((BoundsData)var8.getValue()).contains(var1, var3)) {
               Wardrobe var9 = manager();
               if (var9 == null) {
                  return true;
               }

               if (this.armedDelete.equals(var8.getKey()) && System.currentTimeMillis() - this.armedAt < 3000L) {
                  for (Wardrobe.Data var11 : var9.method2505()) {
                     if (((String)var8.getKey()).equals("pr:" + var11.name() + ":" + var11.getLongValue())) {
                        var9.method2509(var11);
                        Zenith.getInstance().getManager().method7788();
                        break;
                     }
                  }

                  this.armedDelete = "";
                  this.invalidate();
               } else {
                  this.armedDelete = (String)var8.getKey();
                  this.armedAt = System.currentTimeMillis();
               }

               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            }
         }
      }

      if (this.getActiveTab() == 7 && var5 == Enum3.LEFT) {
         for (Map.Entry var15 : this.sliderTracks.entrySet()) {
            if (((BoundsData)var15.getValue()).contains(var1, var3)) {
               this.draggingSlider = (SliderSetting)var15.getKey();
               this.slide(this.draggingSlider, var1);
               return true;
            }
         }
      }

      if (this.getActiveTab() == 0
         && var1 >= this.stageX0
         && var1 <= this.stageX1
         && var3 >= this.stageY0
         && var3 <= this.stageY1
         && this.stageX1 > this.stageX0) {
         this.stageDrag = true;
         return true;
      } else {
         return this.preview != null && this.preview.onMouseClicked(var1, var3, var5.getIntValue()) ? true : super.onMouseClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      this.stageDrag = false;
      if (this.draggingSlider != null) {
         this.draggingSlider = null;
         Zenith.getInstance().getManager().method7788();
      }

      if (this.preview != null) {
         this.preview.onMouseReleased(var1, var3, var5.getIntValue());
      }

      return super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.draggingSlider != null) {
         this.slide(this.draggingSlider, var1);
         return true;
      } else if (this.stageDrag) {
         this.stageYaw += (float)var6 * 1.4F;
         return true;
      } else {
         return this.preview != null && this.preview.onMouseDragged(var1, var3, var5, var6, var8) ? true : super.onMouseDragged(var1, var3, var5, var6, var8);
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return this.preview != null && this.preview.onMouseScrolled(var1, var3, var7) ? true : super.mouseScrolled(var1, var3, var5, var7);
   }
}

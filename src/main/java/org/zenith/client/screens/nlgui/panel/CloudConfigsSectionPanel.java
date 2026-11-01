package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.cloud.CloudCard;
import org.zenith.client.screens.nlgui.cloud.CloudLibrary;
import org.zenith.client.screens.nlgui.cloud.CloudPreviewTextureCache;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.CloudJsonIo;
import org.zenith.utility.api.ConfigFile;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.ConfigSyncService;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CloudConfigsSectionPanel extends SectionPanel {
   private static final int TAB_LOCAL = 0;
   private static final int TAB_CLOUD = 1;
   private static final int TAB_MARKET = 2;
   private static final float ROW = 36.0F;
   private static final float ROW_GAP = 4.0F;
   private static final float AVATAR = 22.0F;
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final String[] SORT_KEYS = new String[]{"gui.configs.sort.fresh", "gui.configs.sort.name", "gui.configs.sort.top", "gui.configs.sort.size"};
   private static final int CARD_COLUMNS = 2;
   private static final float CARD_GAP = 6.0F;
   private static final float BUTTON_HEIGHT = 16.0F;
   private static final float BUTTON_TALL_HEIGHT = 23.0F;
   private static final float FILE_ROW_HEIGHT = 26.0F;
   private static final float CURRENT_HEIGHT = 34.0F;
   private static final float SUGGEST_HEIGHT = 22.0F;
   private static final ZenithColor CHANGE_ON = new ZenithColor(-11870592);
   private static final ZenithColor CHANGE_OFF = new ZenithColor(-495247);
   private static final float FILE_ROW_GAP = 3.0F;
   private static final float TILE = 16.0F;
   private static final int TONE_PLAIN = 0;
   private static final int TONE_PRIMARY = 1;
   private static final int TONE_DANGER = 2;
   private static final String[] VISIBILITY = new String[]{"PRIVATE", "FRIENDS", "PUBLIC", "CODE"};
   private static final String[] VISIBILITY_NAMES = new String[]{
      "gui.configs.vis.private", "gui.configs.vis.friends", "gui.configs.vis.public", "gui.configs.vis.code"
   };
   private static final int SORT_FRESH = 0;
   private static final int SORT_NAME = 1;
   private static final int SORT_LIKES = 2;
   private static final int SORT_SIZE = 3;
   private static final int LOCAL_SORT_NAME = 0;
   private static final int LOCAL_SORT_DATE = 1;
   private static final int LOCAL_SORT_SIZE = 2;
   private static final SimpleDateFormat DATE = new SimpleDateFormat("dd.MM HH:mm", Locale.ROOT);
   private static final long REFRESH_MS = 15000L;
   private static final long STATUS_MS = 6000L;
   private static final long ARM_MS = 4000L;
   private static final int DESCRIPTION_MAX_LINES = 6;
   private final TextSetting newNameSetting = new TextSetting("gui.configs.name", "", "gui.configs.name.hint");
   private final TextSetting codeSetting = new TextSetting("gui.configs.code", "", "gui.configs.code.hint");
   private final InspectorRow newNameRow = new InspectorRow(this.newNameSetting, 0);
   private final InspectorRow codeRow = new InspectorRow(this.codeSetting, 0);
   private int seenSync = -1;
   private BoundsData headerButton;
   private boolean creatorOpen;
   private static final float TEMPLATE_ROW = 19.0F;
   private static final float FIELD_ROW = 26.0F;
   private static final String[] TEMPLATE_KEYS = new String[]{
      "gui.configs.template.full", "gui.configs.template.noBinds", "gui.configs.template.visual", "gui.configs.template.functions"
   };
   private static final boolean[][] TEMPLATE_PARTS = new boolean[][]{{true, true, true}, {true, true, false}, {true, false, false}, {false, true, true}};
   private static final MenuGlyph[] TEMPLATE_GLYPHS = new MenuGlyph[]{MenuGlyph.CONFIGS, MenuGlyph.KEY, MenuGlyph.COLOR, MenuGlyph.MODE};
   private final FormField nameField = new FormField(48, false);
   private final Animation creatorAnimation = new Animation(260L, 0.0F, Easing.INSTANCE15);
   private BoundsData savePill;
   private int seenSummaries = -1;
   private final Set<String> applying = ConcurrentHashMap.newKeySet();
   private final ConfigInfoPage page = new ConfigInfoPage(new ConfigInfoPage.Host() {
      @Override
      public void apply(String var1, int var2) {
         CloudConfigsSectionPanel.this.loadMode = var2;
         CloudConfigsSectionPanel.this.applyLocal(Zenith.getInstance().getManager(), var1);
      }

      @Override
      public void report(String var1, boolean var2) {
         CloudConfigsSectionPanel.this.status = var2 ? "!" + var1 : var1;
         CloudConfigsSectionPanel.this.statusAt = System.currentTimeMillis();
         CloudConfigsSectionPanel.this.page.message(var1, var2);
         CloudConfigsSectionPanel.this.bump();
      }

      @Override
      public void close() {
         CloudConfigsSectionPanel.this.selectedLocal = null;
         CloudConfigsSectionPanel.this.bump();
      }

      @Override
      public void changed() {
         CloudConfigsSectionPanel.this.bump();
      }
   });
   private final ChipStrip sortChips = new ChipStrip("gui.configs.sort.fresh", "gui.configs.sort.name", "gui.configs.sort.top", "gui.configs.sort.size");
   private final ChipStrip localSortChips = new ChipStrip("gui.configs.sort.name", "gui.configs.sort.date", "gui.configs.sort.size");
   private final ChipStrip loadModeChips = new ChipStrip("gui.configs.load.all", "gui.configs.load.noBinds", "gui.configs.load.themeOnly");
   private final ChipStrip uploadVisibilityChips = new ChipStrip(VISIBILITY_NAMES);
   private final ChipStrip entryVisibilityChips = new ChipStrip(VISIBILITY_NAMES);
   private int kind;
   private int cloudSort = 0;
   private int localSort = 0;
   private int loadMode;
   private int uploadVisibility;
   private final List<StoreApi.Data2> mine = new CopyOnWriteArrayList<>();
   private volatile List<StoreApi.Data2> library = List.of();
   private volatile List<StoreApi.Data2> market = List.of();
   private volatile List<StoreApi.Data> codes = List.of();
   private final Map<UUID, CloudCard> cardVisuals = new LinkedHashMap<UUID, CloudCard>(16, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<UUID, CloudCard> var1) {
         return this.size() > 96;
      }
   };
   private final Map<String, Animation> hoverAnimations = new HashMap<>();
   private final Map<UUID, Animation> previewFade = new HashMap<>();
   private final Animation noticeAppear = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private String noticeAnimatedFor = "";
   private CloudLibrary taken;
   private String selectedLocal;
   private String appliedName = "";
   private volatile long sideFetch;
   private UUID selectedCloud;
   private UUID codesFor;
   private String armedKey = "";
   private long armedAt;
   private volatile long lastFetch;
   private volatile int fetchedTab = -1;
   private volatile int fetchedKind = -1;
   private volatile String status = "";
   private volatile long statusAt;
   private volatile int revision;
   private int seenRevision;
   private String seenQuery = "";
   private boolean pillHot;
   private static final int[] LETTER_COLORS = new int[]{-10443270, -757066, -13315175, -278748, -5796870, -495247, -13058568, -290244};
   private static final Map<String, String> DOWNLOADED_FILES = new HashMap<>();
   private static boolean downloadedFilesLoaded;
   private static final String DOWNLOADED_FILES_NAME = "cloud-config-files.json";
   private static final int MAX_DOWNLOADED_FILES = 1024;
   private static final Set<String> RESERVED = Set.of(
      "con",
      "prn",
      "aux",
      "nul",
      "com1",
      "com2",
      "com3",
      "com4",
      "com5",
      "com6",
      "com7",
      "com8",
      "com9",
      "lpt1",
      "lpt2",
      "lpt3",
      "lpt4",
      "lpt5",
      "lpt6",
      "lpt7",
      "lpt8",
      "lpt9"
   );

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private String kind() {
      return "config";
   }

   private CloudLibrary taken() {
      if (this.taken == null) {
         this.taken = new CloudLibrary();
      }

      return this.taken;
   }

   @Override
   public void selectTab(int var1) {
      if (var1 != this.getActiveTab()) {
         this.status = "";
         this.disarm();
         this.selectedCloud = null;
      }

      super.selectTab(var1);
   }

   @Override
   public String headerTitle() {
      return t("gui.cloud.tab.configs");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CONFIGS;
   }

   @Override
   public String sectionDescription() {
      return "gui.configs.desc";
   }

   private boolean localSection() {
      return this.getActiveTab() == 0;
   }

   @Override
   public float mainWidth() {
      return 236.0F;
   }

   @Override
   public float asideWidth() {
      return 380.0F - this.mainWidth();
   }

   @Override
   public void tick() {
      ConfigSyncService var1 = ConfigSyncService.getInstance();
      var1.method676();
      if (var1.revision() != this.seenSync) {
         this.seenSync = var1.revision();
         this.invalidate();
      }

      if (this.revision != this.seenRevision) {
         this.seenRevision = this.revision;
         this.invalidate();
      }

      int var2 = Zenith.getInstance().getManager().method7770();
      if (var2 != this.seenSummaries) {
         this.seenSummaries = var2;
         this.invalidate();
      }

      String var3 = this.searchQuery();
      if (!var3.equals(this.seenQuery)) {
         this.seenQuery = var3;
         this.invalidate();
      }

      if (!this.status.isEmpty() && System.currentTimeMillis() - this.statusAt > 6000L) {
         this.status = "";
         this.bump();
      }

      if (!this.armedKey.isEmpty() && System.currentTimeMillis() - this.armedAt > 4000L) {
         this.disarm();
         this.bump();
      }

      long var4 = System.currentTimeMillis();
      if (var4 - this.lastFetch > 15000L) {
         this.lastFetch = var4;
         StoreApi var6 = store();
         if (var6 != null) {
            var6.method1969("LIBRARY", this.kind())
               .whenComplete((var1x, var2x) -> this.accept(var1xx -> this.library = var1xx, (List<StoreApi.Data2>)var1x, var2x));
         }
      }
   }

   private StoreApi.Data2 findEntry(UUID var1) {
      for (List var3 : List.of(this.mine, this.library, this.market)) {
         for (Object var5_o : (Iterable<?>)(Object)(var3)) {
            StoreApi.Data2 var5 = (StoreApi.Data2)var5_o;
            if (var5.getUUID().equals(var1)) {
               return var5;
            }
         }
      }

      return this.taken().all().stream().filter(var1x -> var1x.getUUID().equals(var1)).findFirst().orElse(null);
   }

   private void accept(Consumer<List<StoreApi.Data2>> var1, List<StoreApi.Data2> var2, Throwable var3) {
      if (var3 == null) {
         var1.accept(Collections.unmodifiableList(new ArrayList(var2)));
      } else {
         this.status = shortError(var3);
         this.statusAt = System.currentTimeMillis();
      }

      this.bump();
   }

   private void fetchCodes(UUID var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         this.codesFor = var1;
         this.codes = List.of();
         var2.method1980(var1).whenComplete((var1x, var2x) -> {
            if (var2x == null) {
               this.codes = Collections.unmodifiableList(new ArrayList<>(var1x));
            } else {
               this.status = shortError(var2x);
               this.statusAt = System.currentTimeMillis();
            }

            this.bump();
         });
      }
   }

   private void refetch() {
      this.lastFetch = 0L;
      this.fetchedTab = -1;
   }

   private void bump() {
      this.revision++;
   }

   private void report(String var1) {
      this.status = var1;
      this.statusAt = System.currentTimeMillis();
      this.bump();
   }

   @Override
   public void build() {
      this.buildLocal();
   }

   @Override
   public float mainHeaderHeight() {
      return 11.0F + GuiStyle.PADDING.intValue() * 2.0F;
   }

   private String headerLabel() {
      return this.creatorOpen ? t("gui.configs.creator.cancel") : "+  " + t("gui.configs.creator.new");
   }

   private String syncLine() {
      ConfigSyncService var1 = ConfigSyncService.getInstance();
      if (var1.method670()) {
         return t("gui.configs.sync.busy");
      } else if (!var1.isFlag()) {
         return var1.getLongValue() == 0L ? t("gui.configs.sync.waiting") : t("gui.configs.sync.offline");
      } else {
         return t("gui.configs.sync.ok");
      }
   }

   private float headerButtonWidth(Font var1) {
      return Math.max(var1.width("+  " + t("gui.configs.creator.new")), var1.width(t("gui.configs.creator.cancel"))) + 16.0F;
   }

   @Override
   public void renderMainHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = var5 + GuiStyle.PADDING.intValue();
      Font var10 = Fonts.NEW_MEDIUM.getFont(4.6F);
      float var11 = this.headerButtonWidth(var10);
      this.headerButton = new BoundsData(var4 + var6 - 8.0F - var11, var9, var11, 11.0F);
      boolean var12 = this.headerButton.contains(var2, var3);
      float var13 = this.animation("hdr:button", 140).method2808(var12 ? 1.0F : 0.0F);
      ZenithColor var14 = var8.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData var15 = ProxyDotRadiusData.method8069(5.5F);
      boolean var16 = this.pressFx.begin(var1, "hdr:button", this.headerButton);
      String var17 = this.headerLabel();
      ZenithColor var18 = !this.creatorOpen
         ? var14.method7292(0.18F + 0.16F * var13)
         : var8.getDisableActiveBg().getZenithColor().method7294(0.55F + 0.45F * var13);
      ZenithColor var19 = !this.creatorOpen
         ? var14.method7295(ZenithColor.INSTANCE, 0.25F + 0.4F * var13)
         : var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), 0.4F + 0.6F * var13);
      var1.drawRoundedRect(this.headerButton.getFloatValue(), var9, var11, 11.0F, var15, var18.method7294(var7));
      this.pressFx.ink(var1, "hdr:button", this.headerButton, 5.5F, ZenithColor.INSTANCE.method7292(0.22F), var7);
      var1.drawText(
         var10, var17, this.headerButton.getFloatValue() + (var11 - var10.width(var17)) / 2.0F, var9 + (11.0F - var10.height()) / 2.0F, var19.method7294(var7)
      );
      this.pressFx.end(var1, var16);
      if (var12) {
         HoverInfo.request(var17, t("gui.configs.saveNew.desc"));
      }

      ConfigSyncService var20 = ConfigSyncService.getInstance();
      ZenithColor var21 = var20.method670() ? var14 : (var20.isFlag() ? OK : var8.getTextTertiary().getZenithColor());
      float var22 = var20.method670() ? 0.55F + 0.45F * (float)Math.abs(Math.sin(System.currentTimeMillis() / 300.0)) : 1.0F;
      float var23 = var9 + 5.5F - 1.5F;
      BoundsData var24 = new BoundsData(var4 + 8.0F, var9, this.headerButton.getFloatValue() - var4 - 8.0F - 6.0F, 11.0F);
      this.pressFx.ink(var1, "hdr:sync", var24, 5.5F, var14.method7292(0.16F), var7);
      float var25 = Math.max(0.0F, this.pressFx.dip("hdr:sync"));
      var1.drawRoundedRect(
         var4 + 8.0F - var25 * 1.5F,
         var23 - var25 * 1.5F,
         3.0F + var25 * 3.0F,
         3.0F + var25 * 3.0F,
         ProxyDotRadiusData.method8069(1.5F + var25 * 1.5F),
         var21.method7294(var7 * var22)
      );
      String var26 = this.syncLine();
      var1.drawText(
         var10,
         CloudCard.fit(var10, var26, this.headerButton.getFloatValue() - var4 - 8.0F - 14.0F),
         var4 + 8.0F + 6.0F,
         var9 + (11.0F - var10.height()) / 2.0F,
         var8.getTextSecondary().getZenithColor().method7294(var7)
      );
      if (new BoundsData(var4 + 8.0F, var9, this.headerButton.getFloatValue() - var4 - 8.0F, 11.0F).contains(var2, var3)) {
         HoverInfo.request(var26, var20.getLastError().isEmpty() ? t("gui.configs.sync.desc") : var20.getLastError());
      }
   }

   @Override
   public boolean pressFeedback() {
      return true;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      BoundsData var6 = this.mainHeaderBounds();
      if (var6 != null && var6.contains(var1, var3)) {
         if (var5 == Enum3.LEFT && this.headerButton != null && this.headerButton.contains(var1, var3)) {
            this.pressFx.press("hdr:button", var1, var3);
            this.toggleCreator();
            Zenith.getInstance().getClientSounds().method1570(false);
         } else if (var5 == Enum3.LEFT) {
            this.pressFx.press("hdr:sync", var1, var3);
            ConfigSyncService.getInstance().method675();
         }

         return true;
      } else if (this.selectedLocal != null && var5 == Enum3.LEFT && this.page.mouseClicked(var1, var3)) {
         return true;
      } else {
         if (this.creatorOpen && var5 == Enum3.LEFT) {
            BoundsData var7 = this.nameField.bounds();
            if (this.savePill != null && this.savePill.contains(var1, var3)) {
               this.pressFx.press("creator:save", var1, var3);
               this.saveTemplate(0);
               return true;
            }

            boolean var8 = var7 != null && var7.contains(var1, var3);
            this.nameField.setFocused(var8);
            if (var8) {
               return true;
            }
         }

         return super.onMouseClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.selectedLocal != null && this.page.isTyping() && this.page.keyPressed(var1, var3)) {
         return true;
      } else if (this.creatorOpen && this.nameField.isFocused()) {
         if (var1 == 257 || var1 == 335) {
            this.saveTemplate(0);
         } else if (var1 == 256) {
            this.nameField.setFocused(false);
         } else {
            this.nameField.keyPressed(var1, var3);
         }

         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.selectedLocal != null && this.page.isTyping() && this.page.charTyped(var1)) {
         return true;
      } else if (this.creatorOpen && this.nameField.isFocused()) {
         this.nameField.charTyped(var1);
         return true;
      } else {
         return super.charTyped(var1, var2);
      }
   }

   private void toggleCreator() {
      this.creatorOpen = !this.creatorOpen;
      this.creatorAnimation.reset(0.0F);
      this.creatorAnimation.method2810(1.0F);
      this.nameField.setFocused(this.creatorOpen);
      this.invalidate();
   }

   private void renderCreatorField(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = this.creatorAnimation.method2808(1.0F);
      float var10 = var7 * Math.min(1.0F, var9 * 1.6F);
      float var11 = (1.0F - var9) * -4.0F;
      BoundsData var12 = new BoundsData(var4 + 8.0F, var5 + 1.0F + var11, var6 - 16.0F, 22.0F);
      boolean var13 = var12.contains(var2, var3);
      float var14 = this.animation("creator:field", 140).method2808(!var13 && !this.nameField.isFocused() ? 0.0F : 1.0F);
      ScriptsSectionPanel.card(var1, var8, var12.getFloatValue(), var12.getFloatValue2(), var12.width(), var12.height(), var14 * 0.6F, var10);
      Font var15 = Fonts.NEW_MEDIUM.getFont(4.8F);
      String var16 = t("gui.configs.creator.save");
      float var17 = var15.width(var16) + 14.0F;
      float var18 = 14.0F;
      this.savePill = new BoundsData(
         var12.getFloatValue() + var12.width() - var17 - 4.0F, var12.getFloatValue2() + (var12.height() - var18) / 2.0F, var17, var18
      );
      boolean var19 = this.savePill.contains(var2, var3);
      float var20 = this.animation("creator:save", 120).method2808(var19 ? 1.0F : 0.0F);
      ZenithColor var21 = var8.getPrimaryColor().getZenithColor();
      boolean var22 = this.pressFx.begin(var1, "creator:save", this.savePill);
      var1.drawRoundedRect(
         this.savePill.getFloatValue(),
         this.savePill.getFloatValue2(),
         var17,
         var18,
         ProxyDotRadiusData.method8069(var18 / 2.0F),
         var21.method7292(0.22F + 0.4F * var20).method7294(var10)
      );
      this.pressFx.ink(var1, "creator:save", this.savePill, var18 / 2.0F, ZenithColor.INSTANCE.method7292(0.25F), var10);
      var1.drawText(
         var15,
         var16,
         this.savePill.getFloatValue() + 7.0F,
         this.savePill.getFloatValue2() + (var18 - var15.height()) / 2.0F,
         var21.method7295(ZenithColor.INSTANCE, 0.3F + 0.6F * var20).method7294(var10)
      );
      this.pressFx.end(var1, var22);
      MenuGlyph.TEXT
         .draw(
            var1,
            var12.getFloatValue() + 6.0F,
            var12.getFloatValue2() + (var12.height() - 6.0F) / 2.0F,
            var8.getTextTertiary().getZenithColor().method7295(var21, var14).method7294(var10)
         );
      float var23 = var12.getFloatValue() + 16.0F;
      float var24 = this.savePill.getFloatValue() - 5.0F - var23;
      this.nameField
         .render(
            var1,
            var15,
            var23,
            var12.getFloatValue2() + (var12.height() - 16.0F) / 2.0F,
            var24,
            var10,
            t("gui.configs.creator.hint"),
            ZenithColor.INSTANCE.method7293(0),
            ZenithColor.INSTANCE.method7293(0),
            var21,
            var8.getTextEnable().getZenithColor(),
            var8.getTextTertiary().getZenithColor()
         );
      if (var19) {
         HoverInfo.request(var16, t("gui.configs.template.full.desc"));
      }
   }

   private void renderTemplate(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, int var9) {
      float var10 = this.creatorAnimation.method2808(1.0F);
      float var11 = Math.max(0.0F, Math.min(1.0F, var10 * 1.8F - (var9 + 1) * 0.15F));
      float var12 = var7 * var11;
      float var13 = (1.0F - var11) * -4.0F;
      BoundsData var14 = new BoundsData(var4 + 8.0F, var5 + 1.0F + var13, var6 - 16.0F, 16.0F);
      boolean var15 = var14.contains(var2, var3);
      float var16 = this.animation("template:" + var9, 140).method2808(var15 ? 1.0F : 0.0F);
      ScriptsSectionPanel.card(var1, var8, var14.getFloatValue(), var14.getFloatValue2(), var14.width(), var14.height(), var16, var12);
      ZenithColor var17 = var8.getPrimaryColor().getZenithColor();
      TEMPLATE_GLYPHS[var9]
         .draw(
            var1,
            var14.getFloatValue() + 7.0F,
            var14.getFloatValue2() + (var14.height() - 6.0F) / 2.0F,
            (var15 ? var17 : var8.getTextSecondary().getZenithColor()).method7294(var12)
         );
      Font var18 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var19 = Fonts.NEW_REGULAR.getFont(4.3F);
      String var20 = t(TEMPLATE_KEYS[var9]);
      var1.drawText(
         var18,
         var20,
         var14.getFloatValue() + 18.0F,
         var14.getFloatValue2() + (var14.height() - var18.height()) / 2.0F,
         var8.getTextEnable().getZenithColor().method7294(var12)
      );
      String var21 = var15 ? t("gui.configs.template.go") : t(TEMPLATE_KEYS[var9] + ".short");
      float var22 = var14.getFloatValue() + var14.width() - 7.0F - var19.width(var21);
      var1.drawText(
         var19,
         CloudCard.fit(var19, var21, var14.width() - 30.0F - var18.width(var20)),
         Math.max(var22, var14.getFloatValue() + 24.0F + var18.width(var20)),
         var14.getFloatValue2() + (var14.height() - var19.height()) / 2.0F,
         (var15 ? var17 : var8.getTextTertiary().getZenithColor()).method7294(var12)
      );
      if (var15) {
         HoverInfo.request(var20, t(TEMPLATE_KEYS[var9] + ".desc"));
      }
   }

   private void saveTemplate(int var1) {
      String var2 = this.nameField.get() == null ? "" : this.nameField.get().strip();
      if (var2.isEmpty()) {
         var2 = t("gui.configs.creator.defaultName") + " " + new SimpleDateFormat("dd.MM HH-mm", Locale.ROOT).format(new Date());
      }

      boolean[] var3 = TEMPLATE_PARTS[Math.max(0, Math.min(TEMPLATE_PARTS.length - 1, var1))];
      boolean var4 = Zenith.getInstance().getManager().method7780(var2, var3[0], var3[1], var3[2]);
      if (!var4) {
         this.report(t("gui.configs.report.writeFailed"));
      } else {
         this.selectedLocal = var2 + "." + "Zenith".toLowerCase(Locale.ROOT);
         this.selectedCloud = null;
         this.nameField.clear();
         this.nameField.setFocused(false);
         this.creatorOpen = false;
         this.report(t("gui.configs.report.created") + " " + var2);
         this.invalidate();
      }
   }

   private void hero(String var1, String var2, String[][] var3) {
      this.custom(
         50.0F,
         (var3x, var4, var5, var6, var7, var8, var9, var10) -> {
            float var11 = var6 + 8.0F;
            float var12 = var8 - 16.0F;
            float var13 = var7 + 2.0F;
            float var14 = 43.0F;
            ZenithColor var15 = var10.getPrimaryColor().getZenithColor();
            ProxyDotRadiusData var16 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.9F);
            var3x.drawRoundedRect(var11, var13, var12, var14, var16, ScriptsSectionPanel.cardFill(var10, 0.2F).method7294(var9));
            ScriptsSectionPanel.flowGlow(var3x, var11, var13, var12, var14, var16, var15, 0.45F, var9);
            var3x.drawRoundedBorder(var11, var13, var12, var14, 0.2F, var16, var15.method7292(0.28F).method7294(var9));
            Font var17 = Fonts.NEW_SEMIBOLD.getFont(6.4F);
            Font var18 = Fonts.NEW_REGULAR.getFont(4.3F);
            Font var19 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
            Font var20 = Fonts.NEW_MEDIUM.getFont(3.8F);
            var3x.drawText(var17, var1, var11 + 9.0F, var13 + 8.0F, var10.getTextEnable().getZenithColor().method7294(var9));
            float var21 = 0.0F;

            for (String[] var25 : var3) {
               var21 += Math.max(var19.width(var25[0]), var20.width(var25[1])) + 12.0F;
            }

            float var26 = var13 + 11.0F + var17.height();

            for (String var30 : wrapText(var2, var18, var12 - 18.0F - var21, 2)) {
               var3x.drawText(var18, var30, var11 + 9.0F, var26, var10.getTextTertiary().getZenithColor().method7294(var9));
               var26 += var18.height() + 1.5F;
            }

            float var28 = var11 + var12 - 9.0F;

            for (int var31 = var3.length - 1; var31 >= 0; var31--) {
               float var32 = Math.max(var19.width(var3[var31][0]), var20.width(var3[var31][1]));
               var28 -= var32;
               var3x.drawText(
                  var19,
                  var3[var31][0],
                  var28 + (var32 - var19.width(var3[var31][0])) / 2.0F,
                  var13 + 10.0F,
                  var10.getTextEnable().getZenithColor().method7294(var9)
               );
               var3x.drawText(
                  var20,
                  var3[var31][1],
                  var28 + (var32 - var20.width(var3[var31][1])) / 2.0F,
                  var13 + 13.0F + var19.height(),
                  var10.getTextTertiary().getZenithColor().method7294(var9)
               );
               var28 -= 12.0F;
            }
         }
      );
   }

   private float drawPill(MenuDrawContext var1, int var2, int var3, BoundsData var4, String var5, String var6, boolean var7, float var8, ZenithStyle var9) {
      BoundsData var10 = ScriptMarketTab.pillRect(var4, var4.getFloatValue() + var4.width() - 7.0F, var6);
      boolean var11 = var10.contains(var2, var3);
      float var12 = this.animation("pill:" + var5, 120).method2808(var11 ? 1.0F : 0.0F);
      ScriptMarketTab.drawPill(var1, var9, var10, var6, var7, var12, false, var8);
      this.pillHot = this.pillHot || var11;
      return var10.getFloatValue();
   }

   private void avatar(MenuDrawContext var1, String var2, StoreApi.Data2 var3, float var4, float var5, float var6, float var7) {
      ScriptMarketTab.cloudAvatar(var1, var3, var2, letterColor(var2), var4, var5, var6, var7);
   }

   private void card(MenuDrawContext var1, ZenithStyle var2, BoundsData var3, String var4, String var5, boolean var6, boolean var7, float var8) {
      float var9 = this.animation("row:" + var4, 140).method2808(var6 ? 1.0F : 0.0F);
      float var10 = this.animation("sel:" + var4, 160).method2808(var7 ? 1.0F : 0.0F);
      ScriptMarketTab.drawCard(var1, var2, var3, var9, var10, 0.0F, letterColor(var5), var8);
   }

   private void statusRow() {
      this.asideCustom(14.0F, (var1, var2, var3, var4, var5, var6, var7, var8) -> {
         long var9 = System.currentTimeMillis() - this.statusAt;
         if (!this.status.isEmpty() && var9 <= 8000L) {
            float var11 = var9 > 7000L ? 1.0F - (float)(var9 - 7000L) / 1000.0F : Math.min(1.0F, (float)var9 / 180.0F);
            Font var12 = Fonts.NEW_REGULAR.getFont(4.3F);
            String var13 = this.status;
            boolean var14 = var13.equals(t("gui.configs.report.writeFailed")) || var13.startsWith("!");
            if (var13.startsWith("!")) {
               var13 = var13.substring(1);
            }

            var1.drawText(var12, CloudCard.fit(var12, var13, var6 - 16.0F), var4 + 8.0F, var5 + 4.0F, (var14 ? ERROR : OK).method7294(var7 * var11));
         }
      });
   }

   private void buildLocal() {
      Manager var1 = Zenith.getInstance().getManager();
      List<CloudConfigsSectionPanel.LocalFile> var2 = this.localFiles(var1);
      if (this.selectedLocal != null && var2.stream().noneMatch(var1x -> var1x.name().equals(this.selectedLocal))) {
         this.selectedLocal = null;
      }

      int var3 = 0;
      int var4 = 0;

      for (Object var6_o : (Iterable<?>)(Object)(var2)) {
         CloudConfigsSectionPanel.LocalFile var6 = (CloudConfigsSectionPanel.LocalFile)var6_o;
         StoreApi.Data2 var7 = this.cloudTwin(var6.name());
         if (var7 != null) {
            var3++;
            var4 += var7.getIntValue();
         }
      }

      int[] var11 = liveCounts();
      String var12 = t("gui.configs.current.line").replace("{m}", String.valueOf(var11[0])).replace("{b}", String.valueOf(var11[1]))
         + (this.appliedName.isEmpty() ? "" : " · " + t("gui.configs.current.from").replace("{n}", display(this.appliedName)));
      this.hero(
         t("gui.configs.hero.local"),
         var12,
         new String[][]{
            {String.valueOf(var2.size()), t("gui.configs.hero.files")},
            {var3 + "/" + var2.size(), t("gui.configs.hero.synced")},
            {String.valueOf(var4), t("gui.configs.hero.likes")}
         }
      );
      if (this.creatorOpen) {
         this.custom(26.0F, this::renderCreatorField);

         for (int var13 = 0; var13 < TEMPLATE_KEYS.length; var13++) {
            final int var13_i = var13;
            this.custom(
               19.0F,
               (var2x, var3x, var4x, var5, var6x, var7x, var8x, var9x) -> this.renderTemplate(var2x, var3x, var4x, var5, var6x, var7x, var8x, var9x, var13_i),
               () -> this.saveTemplate(var13_i),
               null
            );
         }

         this.gap(6.0F);
      }

      for (Object var8_o : (Iterable<?>)(Object)(var2)) {
         CloudConfigsSectionPanel.LocalFile var8 = (CloudConfigsSectionPanel.LocalFile)var8_o;
         CloudConfigsSectionPanel.FileRow var9 = new CloudConfigsSectionPanel.FileRow(var1, var8);
         this.custom(40.0F, var9::render, var9::apply, () -> var8.name().equals(this.selectedLocal), var9::click);
      }

      if (var2.isEmpty()) {
         this.emptyState(
            this.searchQuery().isEmpty() ? "gui.configs.empty.local" : "gui.configs.empty.query",
            this.searchQuery().isEmpty() ? "gui.configs.empty.local.desc" : "gui.configs.empty.query.local.desc"
         );
      }

      List<StoreApi.Data2> var15 = this.arrange(this.sharedEntries());
      if (this.selectedCloud != null && var15.stream().noneMatch(var1x -> var1x.getUUID().equals(this.selectedCloud))) {
         this.selectedCloud = null;
      }

      if (!var15.isEmpty()) {
         this.gap(GuiStyle.PADDING.intValue());
         this.caption("gui.configs.caption.shared");

         for (Object var18_o : (Iterable<?>)(Object)(var15)) {
            StoreApi.Data2 var18 = (StoreApi.Data2)var18_o;
            CloudConfigsSectionPanel.EntryRow var10 = new CloudConfigsSectionPanel.EntryRow(var18);
            this.custom(40.0F, var10::render, var10::primary, () -> var18.getUUID().equals(this.selectedCloud), var10::click);
         }
      }

      StoreApi.Data2 var17 = this.find(var15);
      if (var17 != null) {
         this.selectedLocal = null;
         this.buildEntryAside(var17, false);
         if (this.taken().contains(var17.getUUID())) {
            this.asideDanger("forget", "gui.configs.forget", "gui.configs.forget.desc", () -> {
               this.taken().forget(var17.getUUID());
               this.selectedCloud = null;
               this.report(t("gui.configs.report.forgotten") + " " + var17.name());
            });
         }

         this.statusRow();
         this.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
            this.selectedCloud = null;
            this.invalidate();
         }, "gui.scripts.market.back.desc");
      } else if (this.selectedLocal != null) {
         this.page.open(this.selectedLocal, display(this.selectedLocal));
         this.asideFull(
            (var1x, var2x, var3x, var4x, var5, var6x, var7x, var8x) -> this.page
               .render(
                  var1x,
                  var2x,
                  var3x,
                  var4x,
                  var5,
                  var6x,
                  NLMenuScreen.contentBottom - var5 - GuiStyle.PADDING.intValue() * 4.0F - HoverInfo.boxHeight(),
                  var7x,
                  var8x
               )
         );
      } else {
         this.buildLocalAside(var1, var2.size());
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return this.selectedLocal != null && this.page.mouseScrolled(var1, var3, var7) ? true : super.mouseScrolled(var1, var3, var5, var7);
   }

   private List<StoreApi.Data2> sharedEntries() {
      LinkedHashMap var1 = new LinkedHashMap();

      for (StoreApi.Data2 var3 : this.taken().all()) {
         if (this.kind().equalsIgnoreCase(var3.kind()) && !var3.mine()) {
            var1.put(var3.getUUID(), var3);
         }
      }

      for (StoreApi.Data2 var5 : this.library) {
         if (!var5.mine()) {
            var1.put(var5.getUUID(), var5);
         }
      }

      return new ArrayList<>(var1.values());
   }

   private static int[] liveCounts() {
      int var0 = 0;
      int var1 = 0;

      for (Module var3 : Zenith.getInstance().getModuleManager().getItems()) {
         if (var3.isEnabled()) {
            var0++;
         }

         if (var3.getKeyCode() > 0) {
            var1++;
         }
      }

      return new int[]{var0, var1};
   }

   public static ZenithColor letterColor(String var0) {
      int var1 = var0 == null ? 0 : var0.toLowerCase(Locale.ROOT).hashCode();
      return new ZenithColor(LETTER_COLORS[Math.floorMod(var1, LETTER_COLORS.length)]);
   }

   private static void letterTile(MenuDrawContext var0, String var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithColor var7 = letterColor(var1);
      var0.drawRoundedRect(var2, var3, var4, var4, ProxyDotRadiusData.method8069(var4 * 0.3F), var7.method7294(var5 * (0.22F + 0.25F * var6)));
      Font var8 = Fonts.NEW_MEDIUM.getFont(var4 * 0.48F);
      String var9 = var1 != null && !var1.isEmpty() ? var1.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
      var0.drawText(
         var8,
         var9,
         var2 + (var4 - var8.width(var9)) / 2.0F,
         var3 + (var4 - var8.height()) / 2.0F,
         var7.method7295(new ZenithColor(255, 255, 255), 0.35F).method7294(var5)
      );
   }

   private StoreApi.Data2 cloudTwin(String var1) {
      return ConfigSyncService.getInstance().method672(display(var1));
   }

   private void buildLocalAside(Manager var1, int var2) {
      if (this.selectedLocal == null) {
         int[] var7 = liveCounts();
         ConfigSyncService var8 = ConfigSyncService.getInstance();
         this.asideCaption("gui.configs.caption.overview");
         String var9 = String.valueOf(var2);
         this.asideInfo("gui.configs.info.files", () -> var9, "gui.configs.info.files.desc");
         this.asideInfo("gui.configs.info.modules", () -> String.valueOf(var7[0]), "gui.configs.info.modules.desc");
         this.asideInfo("gui.configs.info.binds", () -> String.valueOf(var7[1]), "gui.configs.info.binds.desc");
         this.asideInfo("gui.configs.info.applied", () -> this.appliedName.isEmpty() ? "—" : display(this.appliedName), "gui.configs.info.applied.desc");
         this.asideInfo("gui.configs.info.sync", this::syncLine, "gui.configs.sync.desc");
         this.asideCaption("gui.configs.caption.actions");
         this.asideAction("gui.configs.saveNew", MenuGlyph.COPY, () -> this.creatorOpen ? "" : "+", this::toggleCreator, "gui.configs.saveNew.desc");
         this.asideAction(
            "gui.configs.syncNow", MenuGlyph.CLOUD, () -> var8.method670() ? t("gui.scripts.market.busy") : "", var8::method675, "gui.configs.syncNow.desc"
         );
         this.asideAction(
            "gui.configs.openFolder", MenuGlyph.STORAGE, () -> "", () -> Util.getOperatingSystem().open(Manager.file), "gui.configs.openFolder.desc"
         );
         this.asideAction(
            "gui.configs.openMarket", MenuGlyph.CLOUD, () -> "", () -> Zenith.getInstance().getNLMenuScreen().openMarket(2), "gui.configs.openMarket.desc"
         );
         this.asideCaption("gui.configs.caption.redeem");
         this.asideSetting(this.codeRow);
         this.asideAction("gui.configs.redeem", MenuGlyph.KEY, () -> "", this::redeem, "gui.configs.redeem.desc");
         this.statusRow();
      } else {
         String var3 = this.selectedLocal;
         File var4 = new File(Manager.file, var3);
         StoreApi.Data2 var5 = this.cloudTwin(var3);
         this.asideHeader(display(var3), var5, () -> CloudCard.size(var4.length()) + "  ·  " + DATE.format(new Date(var4.lastModified())));
         this.asideCaption("gui.configs.caption.changes");
         Manager.Data var6 = var1.method7772(var3);
         if (var6 == null) {
            this.asideHint("…");
         } else {
            this.changes(var6);
         }

         this.asideCaption("gui.configs.caption.apply");
         this.asideChips(this.loadModeChips, () -> this.loadMode, var1x -> this.loadMode = var1x, "gui.configs.load.desc");
         this.asideAction("apply", MenuGlyph.CHECK, "gui.configs.apply", null, 1, "gui.configs.apply.desc", () -> this.applyLocal(var1, var3));
         this.asideAction("overwrite", MenuGlyph.COPY, "gui.configs.overwrite", null, 0, "gui.configs.overwrite.desc", () -> {
            var1.method7779(var3);
            this.report(t("gui.configs.report.saved") + " " + display(var3));
         });
         this.asideCaption("gui.configs.caption.cloud");
         this.asideInfo(
            "gui.configs.info.sync",
            () -> t("gui.configs.sync.state." + ConfigSyncService.getInstance().method671(display(var3)).name().toLowerCase(Locale.ROOT)),
            "gui.configs.sync.desc"
         );
         if (var5 == null) {
            this.asideAction("gui.configs.syncNow", MenuGlyph.CLOUD, () -> "", ConfigSyncService.getInstance()::method675, "gui.configs.syncNow.desc");
         } else {
            this.asideInfo("gui.configs.inCloud", () -> "v" + var5.getLongValue2() + " · ♥ " + var5.getIntValue(), "gui.configs.inCloud.desc");
            this.publication(var5);
         }

         this.asideDanger("delete-local", "gui.configs.deleteEverywhere", "gui.configs.deleteEverywhere.desc", () -> {
            var1.method7786(var3);
            this.selectedLocal = null;
            if (var3.equals(this.appliedName)) {
               this.appliedName = "";
            }

            this.report(t("gui.configs.report.deleted") + " " + display(var3));
         });
         this.statusRow();
         this.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
            this.selectedLocal = null;
            this.invalidate();
         }, "gui.scripts.market.back.desc");
      }
   }

   private void changes(Manager.Data var1) {
      ArrayList<String> var2 = new ArrayList();
      ArrayList<String> var3 = new ArrayList();
      if (this.loadMode != 2) {
         for (Module var5 : Zenith.getInstance().getModuleManager().getItems()) {
            if (var5 != InterfaceModule.INSTANCE && var5 != MenuModule.INSTANCE) {
               boolean var6 = var5.isEnabled();
               boolean var7 = var1.getItems().contains(var5.getId());
               if (var7 && !var6) {
                  var2.add(var5.getName());
               } else if (!var7 && var6) {
                  var3.add(var5.getName());
               }
            }
         }
      }

      var2.sort(String.CASE_INSENSITIVE_ORDER);
      var3.sort(String.CASE_INSENSITIVE_ORDER);
      if (this.loadMode == 2) {
         this.asideHint(t(var1.isFlag() ? "gui.configs.changes.theme" : "gui.configs.changes.noTheme"));
      } else if (var2.isEmpty() && var3.isEmpty()) {
         this.asideHint(t("gui.configs.changes.none"));
      } else {
         Font var10 = Fonts.NEW_REGULAR.getFont(4.6F);
         Font var11 = Fonts.NEW_MEDIUM.getFont(5.0F);
         ArrayList<String[]> var12 = new ArrayList();

         for (Object var8_o : (Iterable<?>)(Object)(var2.subList(0, Math.min(5, var2.size())))) {
            String var8 = (String)var8_o;
            var12.add(new String[]{"+", var8});
         }

         for (Object var16_o : (Iterable<?>)(Object)(var3.subList(0, Math.min(5, var3.size())))) {
            String var16 = (String)var16_o;
            var12.add(new String[]{"−", var16});
         }

         int var15 = var2.size() + var3.size() - var12.size();
         float var17 = var10.height() + 3.0F;
         float var9 = var11.height() + 6.0F + var12.size() * var17 + (var15 > 0 ? var17 : 0.0F) + 4.0F;
         this.asideCustom(
            var9 + GuiStyle.PADDING.intValue(),
            (var7x, var8x, var9x, var10x, var11x, var12x, var13, var14) -> {
               float var15x = var10x + 8.0F;
               String var16x = "+" + var2.size();
               String var17x = "−" + var3.size();
               var7x.drawText(var11, var16x, var15x, var11x, CHANGE_ON.method7294(var13));
               var7x.drawText(
                  var10,
                  t("gui.configs.changes.on"),
                  var15x + var11.width(var16x) + 3.0F,
                  var11x + 0.5F,
                  var14.getTextTertiary().getZenithColor().method7294(var13)
               );
               float var18 = var15x + var11.width(var16x) + 3.0F + var10.width(t("gui.configs.changes.on")) + 10.0F;
               var7x.drawText(var11, var17x, var18, var11x, CHANGE_OFF.method7294(var13));
               var7x.drawText(
                  var10,
                  t("gui.configs.changes.off"),
                  var18 + var11.width(var17x) + 3.0F,
                  var11x + 0.5F,
                  var14.getTextTertiary().getZenithColor().method7294(var13)
               );
               float var19 = var11x + var11.height() + 6.0F;

               for (Object var21_o : (Iterable<?>)(Object)(var12)) {
                  String[] var21 = (String[])var21_o;
                  boolean var22 = var21[0].equals("+");
                  var7x.drawRoundedRect(
                     var15x, var19 + 1.0F, 3.0F, var10.height() - 1.0F, ProxyDotRadiusData.method8069(1.5F), (var22 ? CHANGE_ON : CHANGE_OFF).method7294(var13)
                  );
                  var7x.drawText(
                     var10,
                     CloudCard.fit(var10, var21[1], var12x - 16.0F - 8.0F),
                     var15x + 7.0F,
                     var19,
                     var14.getTextSecondary().getZenithColor().method7294(var13)
                  );
                  var19 += var17;
               }

               if (var15 > 0) {
                  var7x.drawText(
                     var10,
                     t("gui.configs.changes.more").replace("{n}", String.valueOf(var15)),
                     var15x + 7.0F,
                     var19,
                     var14.getTextTertiary().getZenithColor().method7294(var13)
                  );
               }
            }
         );
      }
   }

   private static String ago(long var0) {
      long var2 = Math.max(0L, (System.currentTimeMillis() - var0) / 1000L);
      if (var2 < 60L) {
         return t("gui.party.ago.now");
      } else if (var2 < 3600L) {
         return t("gui.party.ago.min").replace("{n}", String.valueOf(var2 / 60L));
      } else {
         return var2 < 86400L
            ? t("gui.party.ago.hour").replace("{n}", String.valueOf(var2 / 3600L))
            : t("gui.party.ago.day").replace("{n}", String.valueOf(var2 / 86400L));
      }
   }

   private void applyLocal(Manager var1, String var2) {
      if (this.applying.add(var2)) {
         var1.method7768(var2, this.loadMode()).whenComplete((var2x, var3) -> {
            this.applying.remove(var2);
            boolean var4 = var3 == null && Boolean.TRUE.equals(var2x);
            if (var4) {
               this.appliedName = var2;
            }

            String var5 = t(var4 ? "gui.configs.report.applied" : "gui.configs.report.unreadable") + " " + display(var2);
            this.report(var4 ? var5 : "!" + var5);
            this.page.message(var5, !var4);
         });
      }
   }

   private List<CloudConfigsSectionPanel.LocalFile> localFiles(Manager var1) {
      String var2 = "." + "Zenith".toLowerCase(Locale.ROOT);
      String var3 = this.searchQuery();
      ArrayList<CloudConfigsSectionPanel.LocalFile> var4 = new ArrayList<>();

      for (String var6 : var1.method7785()) {
         if (var6.toLowerCase(Locale.ROOT).endsWith(var2)) {
            File var7 = new File(Manager.file, var6);
            if (var7.isFile()) {
               String var8 = var6.substring(0, var6.length() - var2.length());
               if (!var8.equalsIgnoreCase("current_config") && (var3.isEmpty() || var8.toLowerCase(Locale.ROOT).contains(var3))) {
                  var4.add(new CloudConfigsSectionPanel.LocalFile(var6, var8, "", var7.length(), var7.lastModified()));
               }
            }
         }
      }

      switch (this.localSort) {
         case 1:
            var4.sort(Comparator.comparingLong(CloudConfigsSectionPanel.LocalFile::modified).reversed());
            break;
         case 2:
            var4.sort(Comparator.comparingLong(CloudConfigsSectionPanel.LocalFile::size).reversed());
            break;
         default:
            var4.sort(Comparator.comparing(var0 -> var0.display().toLowerCase(Locale.ROOT)));
      }

      return var4;
   }

   private List<StoreApi.Data2> arrange(List<StoreApi.Data2> var1) {
      String var2 = this.searchQuery();
      ArrayList<StoreApi.Data2> var3 = new ArrayList<>();

      for (StoreApi.Data2 var5 : var1) {
         if (var2.isEmpty() || matches(var5, var2)) {
            var3.add(var5);
         }
      }

      switch (this.cloudSort) {
         case 1:
            var3.sort(Comparator.comparing(var0 -> blank(var0.name()).toLowerCase(Locale.ROOT)));
            break;
         case 2:
            var3.sort(Comparator.comparingInt(StoreApi.Data2::getIntValue).reversed());
            break;
         case 3:
            var3.sort(Comparator.comparingLong(StoreApi.Data2::getLongValue).reversed());
            break;
         default:
            var3.sort(Comparator.comparingLong(StoreApi.Data2::getLongValue4).reversed());
      }

      return var3;
   }

   private static boolean matches(StoreApi.Data2 var0, String var1) {
      return blank(var0.name()).toLowerCase(Locale.ROOT).contains(var1)
         || blank(var0.getText8()).toLowerCase(Locale.ROOT).contains(var1)
         || blank(var0.server()).toLowerCase(Locale.ROOT).contains(var1);
   }

   private StoreApi.Data2 find(List<StoreApi.Data2> var1) {
      return var1.stream().filter(var1x -> var1x.getUUID().equals(this.selectedCloud)).findFirst().orElse(null);
   }

   private static String cardHover(StoreApi.Data2 var0) {
      StringBuilder var1 = new StringBuilder();
      if (var0.description() != null && !var0.description().isBlank()) {
         var1.append(var0.description()).append(' ');
      }

      var1.append(CloudCard.size(var0.getLongValue()));
      if (var0.server() != null && !var0.server().isBlank()) {
         var1.append(" · ").append(var0.server());
      }

      var1.append(" · ").append(DATE.format(new Date(var0.getLongValue4())));
      var1.append(". ").append(t("gui.configs.card.hover"));
      return var1.toString();
   }

   private void buildEntryAside(StoreApi.Data2 var1, boolean var2) {
      if (var1 == null) {
         this.asideCaption("gui.configs.caption.card");
         this.asideHint(t("gui.configs.aside.pickCard"));
      } else {
         UUID var3 = var1.getUUID();
         this.asideHeader(
            var1.name(), var1, () -> "v" + var1.getLongValue2() + "  ·  " + blankToDash(var1.getText8()) + "  ·  " + CloudCard.size(var1.getLongValue())
         );
         this.preview(var1);
         this.description(var1);
         this.asideCaption("gui.scripts.caption.actions");
         this.asideAction(
            "download", MenuGlyph.STORAGE, "gui.configs.download", "gui.configs.download.sub", 1, "gui.configs.download.desc", () -> this.download(var1)
         );
         this.asideAction(
            "like",
            MenuGlyph.HEART,
            t(var1.isFlag3() ? "gui.configs.unlike" : "gui.configs.like") + " · " + var1.getIntValue(),
            null,
            0,
            "gui.configs.like.desc",
            () -> this.toggleLike(var3)
         );
         this.asideCaption("gui.scripts.market.caption.about");
         this.asideInfo("gui.scripts.market.info.updated", () -> ago(var1.getLongValue4()), "gui.scripts.market.info.updated.desc");
         if (!var2) {
            this.asideInfo("gui.configs.author", () -> blankToDash(var1.getText8()), "gui.configs.author.desc");
         }

         this.asideInfo("gui.configs.server", () -> blankToDash(var1.server()), "gui.configs.server.desc");
      }
   }

   private void publication(StoreApi.Data2 var1) {
      UUID var2 = var1.getUUID();
      this.asideCaption("gui.configs.caption.visibility");
      this.asideChips(
         this.entryVisibilityChips, () -> visibilityIndex(var1.getText7()), var2x -> this.changeVisibility(var1, var2x), "gui.configs.visibility.desc"
      );
      this.asideAction("push", MenuGlyph.CLOUD, "gui.configs.push", "gui.configs.push.sub", 0, "gui.configs.push.desc", () -> this.pushContent(var1));
      this.asideCaption("gui.configs.caption.codes");
      this.asideAction("issue-code", MenuGlyph.KEY, "gui.configs.issueCode", "gui.configs.issueCode.sub", 0, "gui.configs.issueCode.desc", () -> {
         StoreApi var2x = store();
         if (var2x != null) {
            var2x.method1979(var2, 1).whenComplete((var2xx, var3) -> {
               if (var3 != null) {
                  this.status = shortError(var3);
                  this.statusAt = System.currentTimeMillis();
               }

               this.fetchCodes(var2);
            });
         }
      });
      if (var2.equals(this.codesFor)) {
         for (StoreApi.Data var4 : this.codes) {
            boolean var5 = "ACTIVE".equalsIgnoreCase(var4.getText2());
            this.row(var4.getText())
               .icon(MenuGlyph.KEY)
               .value(() -> lower(var4.getText2()))
               .description(var5 ? "gui.configs.code.active.desc" : "gui.configs.code.used.desc")
               .onRightClick(() -> {
                  StoreApi var3 = store();
                  if (var3 != null) {
                     var3.method1981(var2, var4.getUUID()).whenComplete((var2xx, var3x) -> {
                        if (var3x != null) {
                           this.status = shortError(var3x);
                           this.statusAt = System.currentTimeMillis();
                        }

                        this.fetchCodes(var2);
                     });
                  }
               })
               .addAside();
         }

         if (this.codes.isEmpty()) {
            this.asideHint(t("gui.configs.noCodes"));
         }
      }
   }

   private void asideHeader(String var1, StoreApi.Data2 var2, Supplier<String> var3) {
      this.asideCustom(
         46.0F,
         (var4, var5, var6, var7, var8, var9, var10, var11) -> {
            float var12 = 34.0F;
            this.avatar(var4, var1, var2, var7 + 8.0F, var8 + 5.0F, var12, var10);
            Font var13 = Fonts.NEW_SEMIBOLD.getFont(5.8F);
            Font var14 = Fonts.NEW_REGULAR.getFont(4.3F);
            float var15 = var7 + 8.0F + var12 + 7.0F;
            var4.drawText(
               var13, CloudCard.fit(var13, var1, var7 + var9 - 8.0F - var15), var15, var8 + 9.0F, var11.getTextEnable().getZenithColor().method7294(var10)
            );
            var4.drawText(
               var14,
               CloudCard.fit(var14, (String)var3.get(), var7 + var9 - 8.0F - var15),
               var15,
               var8 + 11.0F + var13.height(),
               var11.getTextTertiary().getZenithColor().method7294(var10)
            );
         }
      );
   }

   private void preview(StoreApi.Data2 var1) {
      if (var1.hasPreview()) {
         float var2 = this.asideWidth() - 16.0F;
         float var3 = var2 * 9.0F / 16.0F;
         UUID var4 = var1.getUUID();
         this.asideCustom(
            var3 + GuiStyle.PADDING.intValue(),
            (var4x, var5, var6, var7, var8, var9, var10, var11) -> {
               float var12 = var7 + 8.0F;
               float var13 = var9 - 16.0F;
               Identifier var14 = CloudPreviewTextureCache.get(var4, var1.getText6());
               ProxyDotRadiusData var15 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
               if (var14 == null) {
                  float var19 = 0.7F + 0.3F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0));
                  var4x.drawRoundedRect(var12, var8, var13, var3, var15, var11.getSurfaceEnableBackground().getZenithColor().method7294(var10 * 2.0F));
                  Font var17 = Fonts.NEW_REGULAR.getFont(4.6F);
                  String var18 = t("gui.configs.previewLoading");
                  var4x.drawText(
                     var17,
                     var18,
                     var12 + (var13 - var17.width(var18)) / 2.0F,
                     var8 + (var3 - var17.height()) / 2.0F,
                     var11.getTextTertiary().getZenithColor().method7294(var10 * var19)
                  );
               } else {
                  float var16 = this.previewFade.computeIfAbsent(var4, var0 -> new Animation(260L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
                  if (var16 < 1.0F) {
                     var4x.drawRoundedRect(
                        var12, var8, var13, var3, var15, var11.getSurfaceEnableBackground().getZenithColor().method7294(var10 * 2.0F * (1.0F - var16))
                     );
                  }

                  var4x.drawRoundedTexture(var14, var12, var8, var13, var3, var15, new ZenithColor(255, 255, 255).method7294(var10 * var16));
               }
            }
         );
      }
   }

   private void description(StoreApi.Data2 var1) {
      String var2 = var1.description();
      if (var2 != null && !var2.isBlank()) {
         Font var3 = Fonts.NEW_REGULAR.getFont(4.8F);
         float var4 = this.asideWidth() - 16.0F;
         List<String> var5 = wrapText(var2, var3, var4, 6);
         float var6 = var3.height() + 1.5F;
         this.asideCustom(var5.size() * var6 + GuiStyle.PADDING.intValue(), (var3x, var4x, var5x, var6x, var7, var8, var9, var10) -> {
            float var11 = var7;

            for (Object var13_o : (Iterable<?>)(Object)(var5)) {
               String var13 = (String)var13_o;
               var3x.drawText(var3, var13, var6x + 8.0F, var11, var10.getTextSecondary().getZenithColor().method7294(var9));
               var11 += var6;
            }
         });
      }
   }

   private void asideHint(String var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.8F);
      float var3 = this.asideWidth() - 16.0F;
      List<String> var4 = wrapText(var1, var2, var3, 8);
      float var5 = var2.height() + 1.5F;
      this.asideCustom(var4.size() * var5 + GuiStyle.PADDING.intValue(), (var3x, var4x, var5x, var6, var7, var8, var9, var10) -> {
         float var11 = var7;

         for (Object var13_o : (Iterable<?>)(Object)(var4)) {
            String var13 = (String)var13_o;
            var3x.drawText(var2, var13, var6 + 8.0F, var11, var10.getTextTertiary().getZenithColor().method7294(var9));
            var11 += var5;
         }
      });
   }

   private void emptyState(String var1, String var2) {
      Font var3 = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font var4 = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> var5 = wrapText(t(var2), var4, this.mainWidth() - 32.0F, 4);
      float var6 = var4.height() + 2.0F;
      float var7 = 30.0F;
      this.custom(
         var7 + var3.height() + 6.0F + var5.size() * var6 + GuiStyle.PADDING.intValue() * 2.0F, (var6x, var7x, var8, var9, var10, var11, var12, var13) -> {
            String var14 = t(var1);
            var6x.drawText(var3, var14, var9 + (var11 - var3.width(var14)) / 2.0F, var10 + var7, var13.getTextSecondary().getZenithColor().method7294(var12));
            float var15 = var10 + var7 + var3.height() + 6.0F;

            for (Object var17_o : (Iterable<?>)(Object)(var5)) {
               String var17 = (String)var17_o;
               var6x.drawText(var4, var17, var9 + (var11 - var4.width(var17)) / 2.0F, var15, var13.getTextTertiary().getZenithColor().method7294(var12));
               var15 += var6;
            }
         }
      );
   }

   private void asideChips(ChipStrip var1, IntSupplier var2, IntConsumer var3, String var4) {
      float var5 = this.asideWidth() - 16.0F;
      this.asideCustom(var1.height(var5) + GuiStyle.PADDING.intValue(), (var3x, var4x, var5x, var6, var7, var8, var9, var10) -> {
         var1.render(var3x, var4x, var5x, var6 + 8.0F, var7, var8 - 16.0F, var2.getAsInt(), var9, var10);
         if (var1.hovered() >= 0) {
            HoverInfo.request(var1.label(var1.hovered()), t(var4));
         }
      }, () -> {
         int var3x = var1.hovered();
         if (var3x >= 0) {
            var3.accept(var3x);
            this.invalidate();
         }
      }, null);
   }

   private Animation animation(String var1, int var2) {
      return this.hoverAnimations.computeIfAbsent(var1, var1x -> new Animation(var2, 0.0F, Easing.INSTANCE12));
   }

   private void asideAction(String var1, MenuGlyph var2, String var3, String var4, int var5, String var6, Runnable var7) {
      this.asideAction(var3, var2, () -> "", var7, var6);
   }

   private void asideDanger(String var1, String var2, String var3, Runnable var4) {
      this.asideAction(var2, MenuGlyph.CLOSE, () -> var1.equals(this.armedKey) ? t("gui.scripts.market.again") : "", () -> {
         if (var1.equals(this.armedKey)) {
            this.disarm();
            var4.run();
         } else {
            this.armedKey = var1;
            this.armedAt = System.currentTimeMillis();
            this.invalidate();
         }
      }, var3);
   }

   private void disarm() {
      this.armedKey = "";
      this.armedAt = 0L;
   }

   private void upload(String var1) {
      StoreApi var2 = store();
      File var3 = new File(Manager.file, var1);
      if (var2 != null && var3.isFile()) {
         try {
            byte[] var4 = Files.readAllBytes(var3.toPath());
            var2.method1972("config", display(var1), var3.getName(), "", VISIBILITY[this.uploadVisibility], var4, null, 0L).whenComplete((var1x, var2x) -> {
               this.status = var2x == null ? t("gui.configs.report.uploaded") + " " + var1x.name() : shortError(var2x);
               ConfigSyncService.getInstance().method675();
               this.statusAt = System.currentTimeMillis();
               this.refetch();
               this.bump();
            });
         } catch (Exception var5) {
            this.report(t("gui.configs.report.readFailed"));
         }
      } else {
         this.report(t("gui.configs.report.nothingToSend"));
      }
   }

   private void pushContent(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      ConfigFile var3 = Zenith.getInstance().getManager().method7783(var1.name());
      if (var2 != null && var3 != null) {
         try {
            byte[] var4 = Files.readAllBytes(var3.getFile().toPath());
            var2.method1972(var1.kind(), var1.name(), var1.fileName(), var1.description(), var1.getText7(), var4, var1.getUUID(), var1.getLongValue2())
               .whenComplete((var1x, var2x) -> {
                  this.status = var2x == null ? t("gui.configs.report.updatedTo") + " v" + var1x.getLongValue2() : shortError(var2x);
                  this.statusAt = System.currentTimeMillis();
                  this.refetch();
                  this.bump();
               });
         } catch (Exception var5) {
            this.report(t("gui.configs.report.readFailed"));
         }
      } else {
         this.report(t("gui.configs.report.noLocal").replace("{n}", var1.name()));
      }
   }

   private void download(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         var2.method1970(var1.getUUID()).whenComplete((var2x, var3) -> {
            if (var3 != null) {
               this.status = shortError(var3);
               this.statusAt = System.currentTimeMillis();
               this.bump();
            } else {
               try {
                  File var4 = saveDownloaded(var1, var2x);
                  if (!var1.mine()) {
                     this.taken().remember(var1);
                  }

                  this.status = t("gui.configs.report.downloaded").replace("{n}", display(var4.getName()));
               } catch (Exception var5) {
                  this.status = t("gui.configs.report.writeFailed");
               }

               this.statusAt = System.currentTimeMillis();
               this.bump();
            }
         });
      }
   }

   private static String extension() {
      return "." + "Zenith".toLowerCase(Locale.ROOT);
   }

   public static File targetFile(StoreApi.Data2 var0) {
      String var1 = recordedFile(var0.getUUID());
      return var1 != null ? new File(Manager.file, var1) : new File(Manager.file, safeName(var0.name()) + extension());
   }

   public static File downloadTarget(StoreApi.Data2 var0, byte[] var1) {
      String var2 = recordedFile(var0.getUUID());
      if (var2 != null) {
         return new File(Manager.file, var2);
      } else {
         String var3 = safeName(var0.name());
         File var4 = new File(Manager.file, var3 + extension());
         if (var4.exists() && !sameContent(var4, var1)) {
            for (int var5 = 2; var5 < 1000; var5++) {
               File var6 = new File(Manager.file, var3 + " (" + var5 + ")" + extension());
               if (!var6.exists() || sameContent(var6, var1)) {
                  return var6;
               }
            }

            return new File(Manager.file, var3 + " (" + UUID.randomUUID() + ")" + extension());
         } else {
            return var4;
         }
      }
   }

   public static File saveDownloaded(StoreApi.Data2 var0, byte[] var1) throws IOException {
      synchronized (DOWNLOADED_FILES) {
         File var3 = downloadTarget(var0, var1);
         var3.getParentFile().mkdirs();
         Files.write(var3.toPath(), var1);
         recordFile(var0.getUUID(), var3.getName());
         return var3;
      }
   }

   private static boolean sameContent(File var0, byte[] var1) {
      try {
         return var0.isFile() && var0.length() == var1.length && Arrays.equals(Files.readAllBytes(var0.toPath()), var1);
      } catch (Exception var3) {
         return false;
      }
   }

   private static String recordedFile(UUID var0) {
      if (var0 == null) {
         return null;
      } else {
         synchronized (DOWNLOADED_FILES) {
            loadDownloadedFiles();
            String var2 = DOWNLOADED_FILES.get(var0.toString());
            return var2 != null && !new File(Manager.file, var2).isFile() ? null : var2;
         }
      }
   }

   private static void recordFile(UUID var0, String var1) {
      if (var0 != null) {
         synchronized (DOWNLOADED_FILES) {
            loadDownloadedFiles();
            if (!var1.equals(DOWNLOADED_FILES.get(var0.toString()))) {
               DOWNLOADED_FILES.put(var0.toString(), var1);

               while (DOWNLOADED_FILES.size() > 1024) {
                  DOWNLOADED_FILES.remove(DOWNLOADED_FILES.keySet().iterator().next());
               }

               JsonObject var3 = new JsonObject();
               DOWNLOADED_FILES.forEach(var3::addProperty);
               CloudJsonIo.method5993(Zenith.file.toPath().resolve("cloud-config-files.json"), var3);
            }
         }
      }
   }

   private static void loadDownloadedFiles() {
      if (!downloadedFilesLoaded) {
         downloadedFilesLoaded = true;
         JsonObject var0 = CloudJsonIo.method5992(Zenith.file.toPath().resolve("cloud-config-files.json"));
         if (var0 != null) {
            for (Map.Entry var2 : var0.entrySet()) {
               try {
                  String var3 = ((JsonElement)var2.getValue()).getAsString();
                  if (!var3.isBlank() && var3.indexOf(47) < 0 && var3.indexOf(92) < 0 && !var3.contains("..")) {
                     DOWNLOADED_FILES.put(UUID.fromString((String)var2.getKey()).toString(), var3);
                  }
               } catch (RuntimeException var4) {
               }
            }
         }
      }
   }

   private void toggleLike(UUID var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         var2.method1978(var1).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               this.status = shortError(var2x);
               this.statusAt = System.currentTimeMillis();
            }

            this.refetch();
            this.bump();
         });
      }
   }

   private void redeem() {
      String var1 = this.codeSetting.getValue();
      StoreApi var2 = store();
      if (var2 != null && var1 != null && !var1.isBlank()) {
         var2.method1986(var1.trim()).whenComplete((var1x, var2x) -> {
            this.status = var2x == null ? t("gui.configs.report.access") + " " + var1x.name() : shortError(var2x);
            this.statusAt = System.currentTimeMillis();
            this.codeSetting.setValue("");
            this.refetch();
            this.bump();
         });
      } else {
         this.report(t("gui.configs.report.enterCode"));
      }
   }

   private void changeVisibility(StoreApi.Data2 var1, int var2) {
      StoreApi var3 = store();
      if (var3 != null && var2 >= 0 && var2 < VISIBILITY.length) {
         if (visibilityIndex(var1.getText7()) != var2) {
            var3.method1974(var1.getUUID(), null, null, VISIBILITY[var2]).whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  this.status = shortError(var2x);
                  this.statusAt = System.currentTimeMillis();
               }

               this.refetch();
               this.bump();
            });
         }
      }
   }

   private Manager.Type loadMode() {
      return switch (this.loadMode) {
         case 1 -> Manager.Type.IGNORE_BINDS;
         case 2 -> Manager.Type.ONLY_THEMES;
         default -> Manager.Type.ALL;
      };
   }

   private static int visibilityIndex(String var0) {
      if (var0 == null) {
         return 0;
      } else {
         String var1 = var0.toUpperCase(Locale.ROOT);

         for (int var2 = 0; var2 < VISIBILITY.length; var2++) {
            if (VISIBILITY[var2].equals(var1)) {
               return var2;
            }
         }

         return 0;
      }
   }

   private static String display(String var0) {
      String var1 = "." + "Zenith".toLowerCase(Locale.ROOT);
      return var0 != null && var0.toLowerCase(Locale.ROOT).endsWith(var1) ? var0.substring(0, var0.length() - var1.length()) : blank(var0);
   }

   private static String plural(int var0, String var1, String var2, String var3) {
      int var4 = var0 % 100;
      if (var4 >= 11 && var4 <= 14) {
         return var3;
      } else {
         return switch (var0 % 10) {
            case 1 -> var1;
            case 2, 3, 4 -> var2;
            default -> var3;
         };
      }
   }

   private static String blank(String var0) {
      return var0 == null ? "" : var0;
   }

   private static String lower(String var0) {
      return var0 != null && !var0.isBlank() ? var0.toLowerCase(Locale.ROOT) : "-";
   }

   private static String blankToDash(String var0) {
      return var0 != null && !var0.isBlank() ? var0 : "-";
   }

   public static String safeName(String var0) {
      StringBuilder var1 = new StringBuilder();
      String var2 = var0 == null ? "" : var0.strip();

      for (int var3 = 0; var3 < var2.length() && var1.length() < 80; var3++) {
         char var4 = var2.charAt(var3);
         boolean var5 = var4 < ' ' || var4 == 127 || "\\/:*?\"<>|".indexOf(var4) >= 0;
         var1.append(var5 ? '_' : var4);
      }

      String var7 = var1.toString().strip();

      while (var7.endsWith(".") || var7.endsWith(" ")) {
         var7 = var7.substring(0, var7.length() - 1);
      }

      boolean var8 = false;

      for (int var9 = 0; var9 < var7.length(); var9++) {
         if (Character.isLetterOrDigit(var7.charAt(var9))) {
            var8 = true;
            break;
         }
      }

      if (!var8) {
         return "config";
      } else {
         int var10 = var7.indexOf(46);
         String var6 = var10 < 0 ? var7 : var7.substring(0, var10);
         return RESERVED.contains(var6.toLowerCase(Locale.ROOT)) ? "_" + var7 : var7;
      }
   }

   private static String shortError(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null && var1.getCause() != var1) {
         var1 = var1.getCause();
      }

      String var2 = var1.getMessage();
      if (var2 == null || var2.isBlank()) {
         var2 = var1.getClass().getSimpleName();
      }

      return "!" + (var2.length() > 40 ? var2.substring(0, 39) + "…" : var2);
   }

   public void openCloudTab() {
      this.selectedCloud = null;
      this.invalidate();
   }

   private final class EntryRow {
      private final StoreApi.Data2 entry;
      private final boolean have;
      private final String cardKey;
      private final String pillKey;
      private final String version;
      private final String likes;
      private final String description;
      private boolean downloadHot;

      private EntryRow(StoreApi.Data2 var2) {
         this.entry = var2;
         this.have = CloudConfigsSectionPanel.targetFile(var2).isFile();
         this.cardKey = "cloud:" + var2.getUUID();
         this.pillKey = "dl:" + var2.getUUID();
         this.version = "v" + var2.getLongValue2();
         this.likes = "♥ " + var2.getIntValue();
         this.description = var2.description() != null && !var2.description().isBlank() ? var2.description().replace('\n', ' ') : null;
      }

      private void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
         BoundsData var9 = new BoundsData(var4 + 8.0F, var5, var6 - 16.0F, 36.0F);
         boolean var10 = var9.contains(var2, var3);
         CloudConfigsSectionPanel.this.card(
            var1, var8, var9, this.cardKey, this.entry.name(), var10, this.entry.getUUID().equals(CloudConfigsSectionPanel.this.selectedCloud), var7
         );
         CloudConfigsSectionPanel.this.avatar(var1, this.entry.name(), this.entry, var9.getFloatValue() + 7.0F, ScriptMarketTab.cardAvatarY(var9), 22.0F, var7);
         CloudConfigsSectionPanel.this.pillHot = false;
         float var11 = CloudConfigsSectionPanel.this.drawPill(
            var1,
            var2,
            var3,
            var9,
            this.pillKey,
            SectionPanel.t(this.have ? "gui.market.config.redownload" : "gui.market.config.download"),
            this.have,
            var7,
            var8
         );
         this.downloadHot = var10 && CloudConfigsSectionPanel.this.pillHot;
         float var12 = ScriptMarketTab.cardTextX(var9);
         float var13 = var11 - 7.0F;
         ScriptMarketTab.CardLines var14 = ScriptMarketTab.cardLines(var9);
         ScriptMarketTab.drawTitleLine(
            var1,
            var8,
            this.entry.name(),
            this.version,
            this.entry.getIntValue() <= 0 && !this.entry.isFlag3() ? "" : this.likes,
            this.entry.isFlag3() ? var8.getHeartIcon().getZenithColor() : null,
            var12,
            var13,
            var14.title(),
            var7
         );
         ScriptMarketTab.drawDescription(var1, var8, this.description, null, var12, var13, var14.note(), var7);
         ArrayList var15 = new ArrayList();
         var15.add(
            this.entry.mine()
               ? SectionPanel.t(CloudConfigsSectionPanel.VISIBILITY_NAMES[CloudConfigsSectionPanel.visibilityIndex(this.entry.getText7())])
               : CloudConfigsSectionPanel.blankToDash(this.entry.getText8())
         );
         var15.add(CloudCard.size(this.entry.getLongValue()));
         var15.add(CloudConfigsSectionPanel.ago(this.entry.getLongValue4()));
         if (this.entry.server() != null && !this.entry.server().isBlank()) {
            var15.add(this.entry.server());
         }

         ScriptMarketTab.drawMeta(var1, String.join("  ·  ", var15), var8.getTextTertiary().getZenithColor(), var12, var12, var13, var14.meta(), var7);
         if (var10) {
            HoverInfo.request(
               this.entry.name(), this.downloadHot ? SectionPanel.t("gui.configs.download.desc") : CloudConfigsSectionPanel.cardHover(this.entry)
            );
         }
      }

      private void primary() {
         CloudConfigsSectionPanel.this.download(this.entry);
      }

      private void click() {
         CloudConfigsSectionPanel.this.selectedCloud = this.entry.getUUID().equals(CloudConfigsSectionPanel.this.selectedCloud) ? null : this.entry.getUUID();
         CloudConfigsSectionPanel.this.selectedLocal = null;
         CloudConfigsSectionPanel.this.disarm();
         CloudConfigsSectionPanel.this.invalidate();
      }
   }

   private final class FileRow {
      private final Manager manager;
      private final CloudConfigsSectionPanel.LocalFile file;
      private final Manager.Data summary;
      private final StoreApi.Data2 twin;
      private final boolean inCloud;
      private final String cardKey;
      private final String pillKey;
      private boolean applyHot;

      private FileRow(Manager var2, CloudConfigsSectionPanel.LocalFile var3) {
         this.manager = var2;
         this.file = var3;
         this.summary = var2.method7772(var3.name());
         this.twin = CloudConfigsSectionPanel.this.cloudTwin(var3.name());
         this.inCloud = this.twin != null;
         this.cardKey = "file:" + var3.name();
         this.pillKey = "apply:" + var3.name();
      }

      private void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
         boolean var9 = this.file.name().equals(CloudConfigsSectionPanel.this.selectedLocal);
         BoundsData var10 = new BoundsData(var4 + 8.0F, var5, var6 - 16.0F, 36.0F);
         boolean var11 = var10.contains(var2, var3);
         CloudConfigsSectionPanel.this.card(var1, var8, var10, this.cardKey, this.file.display(), var11, var9, var7);
         CloudConfigsSectionPanel.this.avatar(var1, this.file.display(), null, var10.getFloatValue() + 7.0F, ScriptMarketTab.cardAvatarY(var10), 22.0F, var7);
         boolean var12 = this.file.name().equals(CloudConfigsSectionPanel.this.appliedName);
         CloudConfigsSectionPanel.this.pillHot = false;
         float var13 = CloudConfigsSectionPanel.this.drawPill(
            var1, var2, var3, var10, this.pillKey, SectionPanel.t(var12 ? "gui.configs.tag.applied" : "gui.configs.applyShort"), var12, var7, var8
         );
         this.applyHot = var11 && CloudConfigsSectionPanel.this.pillHot;
         ZenithColor var14 = var8.getPrimaryColor().getZenithColor();
         float var15 = ScriptMarketTab.cardTextX(var10);
         float var16 = var13 - 7.0F;
         ScriptMarketTab.CardLines var17 = ScriptMarketTab.cardLines(var10);
         ScriptMarketTab.drawTitleLine(var1, var8, this.file.display(), "", "", null, var15, var16, var17.title(), var7);
         String var18 = this.summary == null
            ? "…"
            : SectionPanel.t("gui.configs.row.meta")
               .replace("{m}", String.valueOf(this.summary.getItems().size()))
               .replace("{b}", String.valueOf(this.summary.getIntValue()));
         ScriptMarketTab.drawDescription(var1, var8, var18, null, var15, var16, var17.note(), var7);
         float var19 = var17.meta();
         ConfigSyncService.Type var20 = ConfigSyncService.getInstance().method671(this.file.display());
         String var21 = SectionPanel.t("gui.configs.sync.state." + var20.name().toLowerCase(Locale.ROOT));

         ZenithColor var22 = switch (var20) {
            case SYNCED -> CloudConfigsSectionPanel.OK;
            case ERROR -> CloudConfigsSectionPanel.ERROR;
            case LOCAL -> var8.getTextTertiary().getZenithColor();
            default -> var14;
         };
         float var23 = ScriptMarketTab.drawChip(var1, var21, var22.method7292(0.14F), var22, var15, var19, var7);
         if (this.inCloud) {
            String var24 = SectionPanel.t(CloudConfigsSectionPanel.VISIBILITY_NAMES[CloudConfigsSectionPanel.visibilityIndex(this.twin.getText7())])
               + "  ·  v"
               + this.twin.getLongValue2()
               + (this.twin.getIntValue() > 0 ? "  ·  ♥ " + this.twin.getIntValue() : "");
            var23 = ScriptMarketTab.drawMeta(var1, var24, var14, var23, var15, var16, var19, var7);
         }

         String var25 = CloudCard.size(this.file.size()) + "  ·  " + CloudConfigsSectionPanel.ago(this.file.modified());
         ScriptMarketTab.drawMeta(var1, var25, var8.getTextTertiary().getZenithColor(), var23, var15, var16, var19, var7);
         if (var11) {
            HoverInfo.request(this.file.display(), SectionPanel.t(this.applyHot ? "gui.configs.apply.desc" : "gui.configs.row.desc"));
         }
      }

      private void click() {
         CloudConfigsSectionPanel.this.selectedLocal = this.file.name().equals(CloudConfigsSectionPanel.this.selectedLocal) ? null : this.file.name();
         CloudConfigsSectionPanel.this.selectedCloud = null;
         if (CloudConfigsSectionPanel.this.selectedLocal != null && this.twin != null) {
            CloudConfigsSectionPanel.this.fetchCodes(this.twin.getUUID());
         }

         CloudConfigsSectionPanel.this.disarm();
         CloudConfigsSectionPanel.this.invalidate();
      }

      private void apply() {
         CloudConfigsSectionPanel.this.applyLocal(this.manager, this.file.name());
      }
   }

   private static final class LocalFile {
      private final String name;
      private final String display;
      private final String meta;
      private final long size;
      private final long modified;

      public String name() {
         return this.name;
      }

      public String display() {
         return this.display;
      }

      public String meta() {
         return this.meta;
      }

      public long size() {
         return this.size;
      }

      public long modified() {
         return this.modified;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof LocalFile other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.display, other.display) && java.util.Objects.equals(this.meta, other.meta) && java.util.Objects.equals(this.size, other.size) && java.util.Objects.equals(this.modified, other.modified);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, display, meta, size, modified);
      }

      @Override
      public String toString() {
         return "LocalFile[" + "name=" + this.name + ", " + "display=" + this.display + ", " + "meta=" + this.meta + ", " + "size=" + this.size + ", " + "modified=" + this.modified + "]";
      }

      private LocalFile(String var1, String var2, String var3, long var4, long var6) {
         this.name = var1;
         this.display = var2;
         this.meta = var3;
         this.size = var4;
         this.modified = var6;
      }
   }
}

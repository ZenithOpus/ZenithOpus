package org.zenith.client.screens.nlgui.panel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.CodeArea;
import org.zenith.client.screens.scripts.ScriptIcons;
import org.zenith.client.screens.scripts.ScriptInfoPage;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.client.scripting.Module1;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.AiChatStorage;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.Server;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ScriptsSectionPanel extends SectionPanel {
   private static final int TAB_SCRIPTS = 0;
   private static final int TAB_CONFIGS = 1;
   private static final int TAB_MARKET = 2;
   private static final int TAB_AI = 3;
   private static final float CARD_HEIGHT = 26.0F;
   private static final float LOG_ROW = 9.0F;
   private static final float WORKSPACE_EXTRA = 40.0F;
   private static final float WS_INSPECTOR = 124.0F;
   private static final float WS_CONSOLE = 62.0F;
   private static final float FILE_ROW = 14.0F;
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor WARN = new ZenithColor(247, 206, 59);
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor EDITOR_BG = new ZenithColor(10, 11, 16, 150);
   private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");
   private static final String[][] TEMPLATES = new String[][]{
      {"empty", "gui.scripts.template.empty"},
      {"hud", "gui.scripts.template.hud"},
      {"esp", "gui.scripts.template.esp"},
      {"combat", "gui.scripts.template.combat"},
      {"chat", "gui.scripts.template.chat"},
      {"fun", "gui.scripts.template.fun"}
   };
   private static final float HERO_HEIGHT = 32.0F;
   private static final float TEMPLATE_ROW = 19.0F;
   private static final float EMPTY_HEIGHT = 70.0F;
   private static final float ROW_HEIGHT = 36.0F;
   private static final float ROW_GAP = 4.0F;
   private final List<Object[]> homeHits = new ArrayList<>();
   private final Map<String, Animation> hoverAnimations = new HashMap<>();
   private final Animation creatorAnimation = new Animation(260L, 0.0F, Easing.INSTANCE15);
   private boolean creatorOpen;
   private BoundsData headerBack;
   private BoundsData headerRoot;
   private long lastSignature;
   private long lastCheck;
   private final List<Object[]> toggles = new ArrayList<>();
   private final List<String> evalLines = new ArrayList<>();
   private Script workspace;
   private Script settingsScript;
   private Path file;
   private List<Path> files = new ArrayList<>();
   private CodeArea code;
   private final Animation workspaceAnimation = new Animation(360L, 0.0F, Easing.INSTANCE15);
   private final Animation filePlate = new Animation(220L, 0.0F, Easing.INSTANCE15);
   private boolean filePlateReady;
   private final Map<String, BoundsData> headerButtons = new LinkedHashMap<>();
   private final Map<String, Runnable> headerActions = new LinkedHashMap<>();
   private final List<Object[]> fileRects = new ArrayList<>();
   private BoundsData backRect;
   private BoundsData newFileRect;
   private BoundsData leftBounds;
   private BoundsData codeBounds;
   private BoundsData consoleBounds;
   private BoundsData inputBounds;
   private BoundsData inspectorBounds;
   private final StringBuilder input = new StringBuilder();
   private boolean inputFocused;
   private final List<String> history = new ArrayList<>();
   private int historyIndex = -1;
   private float consoleScroll;
   private float inspectorScroll;
   private float inspectorScrollTarget;
   private String toast = "";
   private long toastAt;
   private boolean toastError;
   private final Map<Module1, List<InspectorRow>> moduleRows = new IdentityHashMap<>();
   private final Map<Module1, Boolean> expanded = new IdentityHashMap<>();
   private final List<Object[]> inspectorHits = new ArrayList<>();
   private InspectorRow activeRow;
   private int filesTick;
   private long filesAt;
   private boolean infoPage;
   private BoundsData infoRect;
   private ScriptInfoPage info;
   private final ScriptMarketTab market = new ScriptMarketTab(this);
   private final ScriptAiTab ai = new ScriptAiTab(this);
   private final CloudConfigsSectionPanel configs = new CloudConfigsSectionPanel();
   private static final float TAB_SHIFT = 14.0F;
   private final Animation tabSwitch = new Animation(220L, 1.0F, Easing.INSTANCE12);
   private float tabDirection = 1.0F;
   private int shownTab = -1;
   private long enteredAt;
   private int enteredTab;
   private long asideEnteredAt;
   private final Map<String, ScriptsSectionPanel.FileCount> fileCounts;
   private final Map<String, Animation> switchAnimations;
   private final Map<String, Long> pressedAt;
   private double lastClickX;
   private double lastClickY;

   public ScriptsSectionPanel() {
      this.configs.setEmbedded(true);
      this.enteredAt = System.currentTimeMillis();
      this.enteredTab = -1;
      this.asideEnteredAt = System.currentTimeMillis();
      this.fileCounts = new HashMap<>();
      this.switchAnimations = new HashMap<>();
      this.pressedAt = new HashMap<>();
      this.lastClickX = -1.0;
      this.lastClickY = -1.0;
   }

   private boolean storeTab() {
      return this.getActiveTab() == 0 || this.getActiveTab() == 2;
   }

   private boolean configsTab() {
      return this.workspace == null && this.getActiveTab() == 1;
   }

   public void openConfigsCloud() {
      this.selectTab(1);
      this.configs.openCloudTab();
   }

   public void openMarket(int var1) {
      if (this.workspace != null) {
         this.closeWorkspace();
      }

      this.selectTab(2);
      this.market.openCategory(var1);
      this.invalidate();
   }

   public Storage hostOrNull() {
      return host();
   }

   public void openWorkspaceFile(Script var1, String var2) {
      this.openWorkspace(var1);
      Storage var3 = host();
      if (var3 != null) {
         try {
            Path var4 = var1.getPath() == null ? var1.getPath2() : var3.method1346(var1, var2);
            if (Files.isRegularFile(var4)) {
               this.openFile(var4);
            }
         } catch (Exception var5) {
         }
      }
   }

   public void filesDropped(List<Path> var1) {
      if (this.workspace == null && this.getActiveTab() == 3) {
         this.ai.attachFiles(var1);
      }
   }

   public void replayEntrance() {
      this.enteredAt = System.currentTimeMillis();
      this.asideEnteredAt = this.enteredAt;
   }

   public void replayAside() {
      this.asideEnteredAt = System.currentTimeMillis();
   }

   public float asideEnter(int var1) {
      float var2 = (float)(System.currentTimeMillis() - this.asideEnteredAt - Math.min(var1, 14) * 38L) / 340.0F;
      if (var2 <= 0.0F) {
         return 0.0F;
      } else if (var2 >= 1.0F) {
         return 1.0F;
      } else {
         float var3 = 1.0F - var2;
         return 1.0F - var3 * var3 * var3;
      }
   }

   public SectionPanel.AsideRenderer asideEntering(int var1, SectionPanel.AsideRenderer var2) {
      return (var3, var4, var5, var6, var7, var8, var9, var10) -> {
         float var11 = this.asideEnter(var1);
         if (!(var11 <= 0.001F)) {
            var2.render(var3, var4, var5, var6 + (1.0F - var11) * 4.0F, var7 + (1.0F - var11) * 7.0F, var8, var9 * var11, var10);
         }
      };
   }

   public float enter(int var1) {
      float var2 = (float)(System.currentTimeMillis() - this.enteredAt - Math.min(var1, 14) * 38L) / 340.0F;
      if (var2 <= 0.0F) {
         return 0.0F;
      } else if (var2 >= 1.0F) {
         return 1.0F;
      } else {
         float var3 = 1.0F - var2;
         return 1.0F - var3 * var3 * var3;
      }
   }

   public SectionPanel.AsideRenderer entering(int var1, SectionPanel.AsideRenderer var2) {
      return (var3, var4, var5, var6, var7, var8, var9, var10) -> {
         float var11 = this.enter(var1);
         if (!(var11 <= 0.001F)) {
            var2.render(var3, var4, var5, var6 + (1.0F - var11) * 4.0F, var7 + (1.0F - var11) * 7.0F, var8, var9 * var11, var10);
         }
      };
   }

   private ScriptInfoPage info() {
      if (this.info == null) {
         this.info = new ScriptInfoPage(host(), this::listToast, var1 -> {
            if (var1 == null) {
               this.settingsScript = null;
               this.invalidate();
            } else {
               this.openSettings(var1);
            }
         });
      }

      return this.info;
   }

   private void openSettings(Script var1) {
      if (this.settingsScript != null && this.info != null && this.info.isDirty()) {
         this.info.apply();
      }

      this.settingsScript = var1;
      this.info().setOnClose(this::closeSettings);
      this.info().open(var1);
      this.invalidate();
   }

   private void closeSettings() {
      if (this.info != null && this.info.isDirty()) {
         this.info.apply();
      }

      this.settingsScript = null;
      this.invalidate();
   }

   private void showInfo() {
      if (this.workspace != null) {
         if (this.code != null && this.code.isDirty()) {
            this.save();
         }

         this.infoPage = true;
         this.info().open(this.workspace);
      }
   }

   private void showCode() {
      if (this.infoPage && this.info != null && this.info.isDirty()) {
         this.info.apply();
      }

      this.infoPage = false;
   }

   private static Storage host() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStorage();
   }

   @Override
   public String headerTitle() {
      return "Scripts & Configs";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CODE;
   }

   @Override
   public String sectionDescription() {
      return "gui.scripts.description";
   }

   @Override
   public float extraWidth() {
      return this.workspace != null ? 40.0F : 0.0F;
   }

   @Override
   public boolean hasLeftColumn() {
      return this.workspace != null;
   }

   @Override
   public float getContentWidth() {
      return 380.0F + NLMenuScreen.extraWidth;
   }

   @Override
   public float mainWidth() {
      return 236.0F;
   }

   @Override
   public float asideWidth() {
      return 144.0F;
   }

   private long signature() {
      Storage var1 = host();
      if (var1 == null) {
         return 0L;
      } else {
         long var2 = this.getActiveTab() * 31L;
         if (this.storeTab()) {
            var2 = var2 * 31L + this.market.revision();
         }

         for (Script var5 : var1.method1293()) {
            var2 = var2 * 31L + var5.getId().hashCode();
            var2 = var2 * 31L + var5.getType2().ordinal();
            var2 = var2 * 31L + var5.getError().hashCode();
            var2 = var2 * 31L + var5.getItems3().size();
            var2 = var2 * 31L + (var1.method1297(var5) ? 1 : 0);
         }

         var2 = var2 * 31L + var1.method1328() + this.evalLines.size();
         return var2 * 31L + (this.creatorOpen ? 1 : 0) + (Server.getInstance().isRunning() ? 2 : 0);
      }
   }

   @Override
   public void build() {
      if (this.enteredTab != this.getActiveTab()) {
         this.enteredTab = this.getActiveTab();
         this.replayEntrance();
      }

      this.tabs(new String[]{"gui.scripts.tab.scripts", "gui.scripts.tab.configs", "gui.scripts.tab.market", "gui.scripts.tab.ai"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.CODE, MenuGlyph.CONFIGS, MenuGlyph.STORAGE, MenuGlyph.CHAT});
      if (this.getActiveTab() != 1) {
         Storage var1 = host();
         if (var1 == null) {
            this.caption("gui.scripts.unavailable");
         } else {
            switch (this.getActiveTab()) {
               case 3:
                  this.ai.build(var1);
                  break;
               default:
                  this.market.syncMode(this.getActiveTab() == 2);
                  this.market.build(var1);
            }
         }
      }
   }

   private ScriptsSectionPanel.RowInfo rowInfo(Storage var1, Script var2) {
      long var3 = 0L;

      try {
         var3 = Files.getLastModifiedTime(var2.getPath2()).toMillis();
      } catch (Exception var9) {
      }

      long var5 = System.currentTimeMillis();
      ScriptsSectionPanel.FileCount var7 = this.fileCounts.get(var2.getId());
      int var8;
      if (var7 != null && var7.modified() == var3 && var5 - var7.countedAt() < 2000L) {
         var8 = var7.files();
      } else {
         var8 = var1.method1345(var2).size();
         this.fileCounts.put(var2.getId(), new ScriptsSectionPanel.FileCount(var3, var8, var5));
      }

      return new ScriptsSectionPanel.RowInfo(var2, var8, var3);
   }

   public boolean creatorOpen() {
      return this.creatorOpen;
   }

   public void toggleCreator() {
      this.creatorOpen = !this.creatorOpen;
      this.creatorAnimation.reset(0.0F);
      this.creatorAnimation.method2810(1.0F);
      this.invalidate();
   }

   public void openScriptSettings(Script var1) {
      if (this.workspace != null) {
         this.closeWorkspace();
      }

      super.selectTab(0);
      this.market.showLocal();
      this.openSettings(var1);
   }

   public void closeSettingsIfOpen() {
      if (this.settingsScript != null) {
         this.closeSettings();
      }

      this.creatorOpen = false;
   }

   public void buildLocal(Storage var1) {
      int var2 = 1;
      ArrayList<Script> var3 = new ArrayList<>(var1.method1293());
      String var4 = this.searchQuery().toLowerCase(Locale.ROOT).strip();
      if (!var4.isEmpty()) {
         var3.removeIf(
            var1x -> !var1x.method1403().toLowerCase(Locale.ROOT).contains(var4)
               && !var1x.getDescription().toLowerCase(Locale.ROOT).contains(var4)
               && !var1x.getText3().toLowerCase(Locale.ROOT).contains(var4)
         );
      }

      if (this.creatorOpen) {
         for (int var5 = 0; var5 < TEMPLATES.length; var5++) {
            final int var5_i = var5;
            this.custom(
                19.0F,
               (var2x, var3x, var4x, var5x, var6x, var7x, var8x, var9) -> this.renderTemplate(var2x, var3x, var4x, var5x, var6x, var7x, var8x, var9, var5_i)
            );
         }

         this.gap(6.0F);
      }

      if (var3.isEmpty() && !this.creatorOpen) {
         this.custom(
            70.0F, this.entering(1, (var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8x) -> this.renderEmpty(var1x, var4x, var5x, var6x, var7x, var8x))
         );
      }

      for (Object var6_o : (Iterable<?>)(Object)(var3)) {
         Script var6 = (Script)var6_o;
         ScriptsSectionPanel.RowInfo var7 = this.rowInfo(var1, var6);
         this.custom(
            40.0F,
            this.entering(
               var2++,
               (var3x, var4x, var5x, var6x, var7x, var8x, var9, var10x) -> this.renderRow(var3x, var4x, var5x, var6x, var7x, var8x, var9, var10x, var1, var7)
            )
         );
      }

      if (this.settingsScript != null && var1.method1294(this.settingsScript.getId()) != null) {
         this.asideFull(
            (var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8x) -> this.info()
               .renderCompact(
                  var1x,
                  var2x,
                  var3x,
                  var4x,
                  var5x,
                  var6x,
                  NLMenuScreen.contentBottom - var5x - GuiStyle.PADDING.intValue() * 4.0F - HoverInfo.boxHeight(),
                  var7x,
                  var8x
               )
         );
      } else {
         this.settingsScript = null;
         var var18 = var1.method1293();
         long var19 = var18.stream().filter(Script::isRunning).count();
         long var8 = var18.stream().filter(var0 -> var0.getType2() == Script.Type2.FAILED).count();
         int var10 = var18.stream().mapToInt(var0 -> var0.getItems3().size()).sum();
         this.asideCaption("gui.scripts.caption.overview");
         this.asideInfo("gui.scripts.info.running", () -> var19 + " / " + var18.size(), "gui.scripts.info.running.desc");
         this.asideInfo("gui.scripts.info.modules", () -> String.valueOf(var10), "gui.scripts.info.modulesAll.desc");
         if (var8 > 0L) {
            this.asideInfo("gui.scripts.info.failed", () -> String.valueOf(var8), "gui.scripts.info.failed.desc");
         }

         this.asideInfo("gui.scripts.info.api", () -> "v3", "gui.scripts.info.api.desc");
         int var11 = this.market.updatesCount(var1);
         if (var11 > 0) {
            this.asideAction(
               "gui.scripts.market.updateAll",
               MenuGlyph.CLOUD,
               () -> String.valueOf(var11),
               () -> this.market.updateAll(var1),
               "gui.scripts.market.updateAll.desc"
            );
         }

         this.asideCaption("gui.scripts.caption.actions");
         this.asideAction("gui.scripts.home.new", MenuGlyph.ACTION, () -> this.creatorOpen ? "" : "+", this::toggleCreator, "gui.scripts.home.new.desc");
         this.asideAction("gui.scripts.reloadAll", MenuGlyph.RESET, () -> "", () -> {
            var1.method1300();
            this.invalidate();
         }, "gui.scripts.reloadAll.desc");
         this.asideAction(
            "gui.scripts.folder", MenuGlyph.STORAGE, () -> "", () -> Util.getOperatingSystem().open(var1.getPath().toFile()), "gui.scripts.folder.desc"
         );
         this.asideAction(
            "gui.scripts.api",
            MenuGlyph.TEXT,
            () -> "",
            () -> Util.getOperatingSystem().open(var1.getPath().resolve("API.md").toFile()),
            "gui.scripts.api.desc"
         );
         this.asideAction(
            "gui.scripts.docs",
            MenuGlyph.NETWORK,
            () -> "",
            () -> Util.getOperatingSystem().open(RegionUtil.method2038().get(0) + "/docs/addons"),
            "gui.scripts.docs.desc"
         );
         Server var12 = Server.getInstance();
         this.asideAction("gui.scripts.mcp", MenuGlyph.NETWORK, () -> t(var12.isRunning() ? "gui.scripts.mcp.on" : "gui.scripts.mcp.off"), () -> {
            var12.setEnabled(!var12.isRunning());
            this.invalidate();
         }, "gui.scripts.mcp.desc");
         this.market.publishSection(var1);
         this.asideCaption("gui.scripts.caption.log");
         var var13 = var1.method1295();
         List<Storage.Data2> var14 = var13.subList(Math.max(0, var13.size() - 7), var13.size());
         if (var14.isEmpty()) {
            this.paragraph(t("gui.scripts.console.empty"), true);
         }

         for (Object var16_o : (Iterable<?>)(Object)(var14)) {
            Storage.Data2 var16 = (Storage.Data2)var16_o;
            this.asideCustom(
               12.0F,
               (var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8x) -> {
                  Font var9 = Fonts.NEW_REGULAR.getFont(4.2F);
                  String var10x = var16.getText() + "  ";
                  var1x.drawText(
                     var9, var9.clip(var10x, var6x * 0.45F), var4x + 8.0F, var5x + 2.0F, var8x.getTextSecondary().getZenithColor().method7294(var7x)
                  );
                  float var11x = var4x + 8.0F + var9.width(var9.clip(var10x, var6x * 0.45F));
                  var1x.drawText(
                     var9,
                     var9.clip(var16.text(), var4x + var6x - 8.0F - var11x),
                     var11x,
                     var5x + 2.0F,
                     levelColor(var16.getText2(), var8x).method7294(var7x * 0.9F)
                  );
               }
            );
         }
      }
   }

   public void paragraph(String var1, boolean var2) {
      Font var3 = Fonts.NEW_REGULAR.getFont(4.5F);
      float var4 = (var2 ? 144.0F : 236.0F) - 16.0F;
      List<String> var5 = wrapText(var1, var3, var4, 10);
      SectionPanel.AsideRenderer var6 = (var2x, var3x, var4x, var5x, var6x, var7x, var8, var9) -> {
         float var10 = var6x + 3.0F;

         for (Object var12_o : (Iterable<?>)(Object)(var5)) {
            String var12 = (String)var12_o;
            var2x.drawText(var3, var12, var5x + 8.0F, var10, var9.getTextTertiary().getZenithColor().method7294(var8));
            var10 += var3.height() + 2.0F;
         }
      };
      float var7 = var5.size() * (var3.height() + 2.0F) + 8.0F;
      if (var2) {
         this.asideCustom(var7, var6);
      } else {
         this.custom(var7, var6);
      }
   }

   public static ZenithColor cardFill(ZenithStyle var0, float var1) {
      return var0.getFieldSurfaceBackground().getZenithColor().method7295(var0.getDisableActiveBg().getZenithColor(), 0.35F + var1 * 0.55F);
   }

   public static void card(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      var0.drawRoundedRect(var2, var3, var4, var5, var8, cardFill(var1, var6).method7294(var7));
      var0.drawRoundedBorder(var2, var3, var4, var5, 0.15F, var8, var1.getFieldBorder().getZenithColor().method7294(2.0F + var6 * 2.0F).method7294(var7));
   }

   public static void flowGlow(
      MenuDrawContext var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6, float var7, float var8
   ) {
      double var9 = System.currentTimeMillis() / 1000.0;
      double var11 = var9 * 0.42 + Math.sin(var9 * 0.31) * 0.9;
      float var13 = (float)Math.cos(var11);
      float var14 = (float)Math.sin(var11);
      float var15 = var7 * (0.85F + 0.15F * (float)Math.sin(var9 * 0.8));
      var0.drawRoundedRect(
         var1,
         var2,
         var3,
         var4,
         var5,
         GlowColor.method8096(
            glowCorner(var6, -var13 - var14, var15, var8),
            glowCorner(var6, -var13 + var14, var15, var8),
            glowCorner(var6, var13 - var14, var15, var8),
            glowCorner(var6, var13 + var14, var15, var8)
         )
      );
   }

   private static ZenithColor glowCorner(ZenithColor var0, float var1, float var2, float var3) {
      float var4 = MathHelper.clamp((var1 / 1.4142135F + 1.0F) * 0.5F, 0.0F, 1.0F);
      return var0.method7292(var2 * var4 * var4 * var3);
   }

   public float hot(String var1, boolean var2) {
      return this.hoverAnimations.computeIfAbsent(var1, var0 -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private void renderHero(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, Storage var9) {
      Font var10 = Fonts.NEW_SEMIBOLD.getFont(6.4F);
      Font var11 = Fonts.NEW_REGULAR.getFont(4.5F);
      Font var12 = Fonts.NEW_MEDIUM.getFont(5.0F);
      long var13 = var9.method1293().stream().filter(Script::isRunning).count();
      long var15 = var9.method1293().stream().filter(var0 -> var0.getType2() == Script.Type2.FAILED).count();
      var1.drawText(var10, t("gui.scripts.home.title"), var4 + 8.0F, var5 + 5.0F, var8.getTextEnable().getZenithColor().method7294(var7));
      String var17 = var9.method1293().isEmpty()
         ? t("gui.scripts.home.none")
         : t("gui.scripts.home.running").replace("{r}", String.valueOf(var13)).replace("{n}", String.valueOf(var9.method1293().size()))
            + (var15 > 0L ? "  ·  " + t("gui.scripts.home.failed").replace("{n}", String.valueOf(var15)) : "");
      var1.drawText(var11, var17, var4 + 8.0F, var5 + 7.0F + var10.height(), (var15 > 0L ? WARN : var8.getTextTertiary().getZenithColor()).method7294(var7));
      String var18 = this.creatorOpen ? t("gui.scripts.home.cancel") : "+  " + t("gui.scripts.home.new");
      float var19 = var12.width(var18) + 16.0F;
      BoundsData var20 = new BoundsData(var4 + var6 - 8.0F - var19, var5 + 6.0F, var19, 15.0F);
      boolean var21 = var20.contains(var2, var3);
      float var22 = this.hot("hero:new", var21);
      ZenithColor var23 = var8.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData var24 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      ZenithColor var25 = cardFill(var8, var22).method7295(var23.method7292(0.14F), (this.creatorOpen ? 0.25F : 0.6F) + var22 * 0.35F);
      boolean var26 = this.pressFx.begin(var1, "hero:new", var20);
      var1.drawRoundedRect(var20.getFloatValue(), var20.getFloatValue2(), var20.width(), var20.height(), var24, var25.method7294(var7));
      this.pressFx.ink(var1, "hero:new", var20, var24.getFloatValue(), ZenithColor.INSTANCE.method7292(0.18F), var7);
      var1.drawRoundedBorder(
         var20.getFloatValue(), var20.getFloatValue2(), var20.width(), var20.height(), 0.15F, var24, var23.method7292(0.25F + var22 * 0.3F).method7294(var7)
      );
      var1.drawText(
         var12,
         var18,
         var20.getFloatValue() + 8.0F,
         var20.getFloatValue2() + (15.0F - var12.height()) / 2.0F,
         (this.creatorOpen ? var8.getTextSecondary().getZenithColor() : var23).method7294(var7 * (0.85F + var22 * 0.15F))
      );
      this.pressFx.end(var1, var26);
      this.homeHits.add(new Object[]{var20, "new"});
   }

   private void renderTemplate(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, int var9) {
      float var10 = this.creatorAnimation.method2808(1.0F);
      float var11 = MathHelper.clamp(var10 * 1.8F - var9 * 0.15F, 0.0F, 1.0F);
      float var12 = var7 * var11;
      float var13 = (1.0F - var11) * -4.0F;
      BoundsData var14 = new BoundsData(var4 + 8.0F, var5 + 1.0F + var13, var6 - 16.0F, 16.0F);
      boolean var15 = var14.contains(var2, var3);
      float var16 = this.hot("template:" + var9, var15);
      card(var1, var8, var14.getFloatValue(), var14.getFloatValue2(), var14.width(), var14.height(), var16, var12);
      float var17 = this.pressFlash("hero:template:" + TEMPLATES[var9][0]);
      if (var17 > 0.01F) {
         var1.drawRoundedRect(
            var14.getFloatValue(),
            var14.getFloatValue2(),
            var14.width(),
            var14.height(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            var8.getPrimaryColor().getZenithColor().method7292(0.2F * var17).method7294(var12)
         );
      }

      String var19 = TEMPLATES[var9][0];

      MenuGlyph var18 = switch (var19) {
         case "hud" -> MenuGlyph.WINDOW;
         case "esp" -> MenuGlyph.BOX;
         case "combat" -> MenuGlyph.TARGET;
         case "chat" -> MenuGlyph.CHAT;
         default -> MenuGlyph.CODE;
      };
      var18.draw(
         var1,
         var14.getFloatValue() + 7.0F,
         var14.getFloatValue2() + (var14.height() - 6.0F) / 2.0F,
         (var15 ? var8.getPrimaryColor() : var8.getTextSecondary()).getZenithColor().method7294(var12)
      );
      Font var24 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var25 = Fonts.NEW_REGULAR.getFont(4.3F);
      String var21 = t(TEMPLATES[var9][1]);
      var1.drawText(
         var24,
         var21,
         var14.getFloatValue() + 18.0F,
         var14.getFloatValue2() + (var14.height() - var24.height()) / 2.0F,
         var8.getTextEnable().getZenithColor().method7294(var12)
      );
      String var22 = var15 ? t("gui.scripts.create.value") : t(TEMPLATES[var9][1] + ".short");
      float var23 = var14.getFloatValue() + var14.width() - 7.0F - var25.width(var22);
      var1.drawText(
         var25,
         var25.clip(var22, var14.width() - 30.0F - var24.width(var21)),
         Math.max(var23, var14.getFloatValue() + 24.0F + var24.width(var21)),
         var14.getFloatValue2() + (var14.height() - var25.height()) / 2.0F,
         (var15 ? var8.getPrimaryColor() : var8.getTextTertiary()).getZenithColor().method7294(var12)
      );
      this.homeHits.add(new Object[]{var14, "template:" + TEMPLATES[var9][0]});
   }

   private void renderEmpty(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      Font var7 = Fonts.NEW_MEDIUM.getFont(5.6F);
      Font var8 = Fonts.NEW_REGULAR.getFont(4.5F);
      float var9 = var3 + 2.0F;
      float var10 = 62.0F;
      card(var1, var6, var2 + 8.0F, var9, var4 - 16.0F, var10, 0.0F, var5);
      String var11 = t("gui.scripts.home.emptyTitle");
      List<String> var12 = wrapText(t("gui.scripts.empty"), var8, var4 - 48.0F, 3);
      float var13 = 6.0F;
      float var14 = 4.0F;
      float var15 = var8.height() + 2.0F;
      float var16 = 6.0F + var13 + var7.height() + var14 + var12.size() * var15 - 2.0F;
      float var17 = var9 + (var10 - var16) / 2.0F;
      MenuGlyph.CODE.draw(var1, var2 + var4 / 2.0F - 3.0F, var17, var6.getPrimaryColor().getZenithColor().method7294(var5));
      float var18 = var17 + 6.0F + var13;
      var1.drawText(var7, var11, var2 + (var4 - var7.width(var11)) / 2.0F, var18, var6.getTextEnable().getZenithColor().method7294(var5));
      float var19 = var18 + var7.height() + var14;

      for (Object var21_o : (Iterable<?>)(Object)(var12)) {
         String var21 = (String)var21_o;
         var1.drawText(var8, var21, var2 + (var4 - var8.width(var21)) / 2.0F, var19, var6.getTextTertiary().getZenithColor().method7294(var5));
         var19 += var8.height() + 2.0F;
      }
   }

   public static String ago(long var0) {
      if (var0 <= 0L) {
         return "";
      } else {
         long var2 = Math.max(0L, (System.currentTimeMillis() - var0) / 1000L);
         if (var2 < 60L) {
            return t("gui.scripts.ago.now");
         } else if (var2 < 3600L) {
            return t("gui.scripts.ago.min").replace("{n}", String.valueOf(var2 / 60L));
         } else {
            return var2 < 86400L
               ? t("gui.scripts.ago.hour").replace("{n}", String.valueOf(var2 / 3600L))
               : t("gui.scripts.ago.day").replace("{n}", String.valueOf(var2 / 86400L));
         }
      }
   }

   private void renderRow(
      MenuDrawContext var1,
      int var2,
      int var3,
      float var4,
      float var5,
      float var6,
      float var7,
      ZenithStyle var8,
      Storage var9,
      ScriptsSectionPanel.RowInfo var10
   ) {
      Script var11 = var10.script();
      BoundsData var12 = ScriptMarketTab.cardRect(var4, var5, var6);
      boolean var13 = var12.contains(var2, var3);
      boolean var14 = this.settingsScript != null && this.settingsScript.getId().equals(var11.getId());
      float var15 = this.hot("row:" + var11.getId(), var13);
      float var16 = this.hot("rowsel:" + var11.getId(), var14);
      boolean var17 = var11.getType2() == Script.Type2.FAILED;
      ZenithColor var18 = var8.getPrimaryColor().getZenithColor();
      int var19 = var11.getColor();
      ZenithColor var20 = var17 ? ERROR : (var19 == -1 ? var18 : new ZenithColor(var19));
      ScriptMarketTab.drawCard(var1, var8, var12, var15, var16, this.pressFlash("row:" + var11.getId()), var20, var7);
      float var21 = 22.0F;
      float var22 = var12.getFloatValue() + 7.0F;
      float var23 = ScriptMarketTab.cardAvatarY(var12);
      Identifier var24 = ScriptIcons.get(var11);
      if (var24 != null) {
         var1.drawRoundedTexture(var24, var22, var23, var21, var21, ProxyDotRadiusData.method8069(var21 * 0.3F), ZenithColor.INSTANCE.method7294(var7));
      } else {
         ScriptMarketTab.letterAvatar(var1, var20, var11.method1403(), var22, var23, var21, var7);
      }

      ZenithColor var25 = var11.isRunning() ? OK : (var17 ? ERROR : var8.getTextTertiary().getZenithColor());
      var1.drawRoundedRect(var22 + var21 - 4.5F, var23 + var21 - 4.5F, 6.0F, 6.0F, ProxyDotRadiusData.method8069(3.0F), cardFill(var8, var15).method7294(var7));
      var1.drawRoundedRect(var22 + var21 - 3.5F, var23 + var21 - 3.5F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), var25.method7294(var7));
      float var26 = 14.0F;
      float var27 = 8.0F;
      float var28 = var12.getFloatValue() + var12.width() - 7.0F - var26;
      float var29 = var12.getFloatValue2() + (var12.height() - var27) / 2.0F;
      StoreApi.Data2 var30 = this.market.updateFor(var9, var11);
      boolean var31 = this.market.updating(var9, var11);
      BoundsData var32 = null;
      if (var30 != null || var31) {
         String var33 = t(var31 ? "gui.scripts.market.busy" : "gui.scripts.market.update");
         var32 = ScriptMarketTab.pillRect(var12, var28 - 7.0F, var33);
         boolean var34 = var32.contains(var2, var3);
         ScriptMarketTab.drawPill(var1, var8, var32, var33, false, this.hot("upd:" + var11.getId(), var34), var31, var7);
         if (var34 && var30 != null) {
            HoverInfo.request(t("gui.scripts.market.update"), t("gui.scripts.market.update.row").replace("{v}", String.valueOf(var30.getLongValue2())));
         }
      }

      float var45 = ScriptMarketTab.cardTextX(var12);
      float var46 = (var32 != null ? var32.getFloatValue() : var28) - 7.0F;
      ScriptMarketTab.CardLines var35 = ScriptMarketTab.cardLines(var12);
      Font var36 = ScriptMarketTab.cardNoteFont();
      float var37 = this.hot("open:" + var11.getId(), var13 && var32 == null);
      String var38 = t("gui.scripts.card.open");
      float var39 = var46 - var36.width(var38);
      float var40 = var37 > 0.01F ? var39 - 6.0F : var46;
      ScriptMarketTab.drawTitleLine(
         var1, var8, var11.method1403(), var11.getVersion().isBlank() ? "" : "v" + var11.getVersion(), "", null, var45, var40, var35.title(), var7
      );
      if (var37 > 0.01F) {
         float var41 = var35.title() + ScriptMarketTab.cardTitleFont().height() - var36.height();
         var1.drawText(var36, var38, var39 + (1.0F - var37) * 6.0F, var41, var18.method7294(var7 * var37));
      }

      if (!var11.getError().isBlank()) {
         ScriptMarketTab.drawDescription(var1, var8, var11.getError(), ERROR, var45, var46, var35.note(), var7);
      } else {
         ScriptMarketTab.drawDescription(var1, var8, var11.getDescription(), null, var45, var46, var35.note(), var7);
      }

      float var47 = ScriptMarketTab.drawTags(var1, var8, Arrays.asList(var11.getText3().split(",")), var45, var45, var46, var35.meta(), var7);
      ArrayList var42 = new ArrayList();
      var42.add(this.stateText(var9, var11));
      OriginUtil.Data2 var43 = OriginUtil.method6921(var11);
      if (var43 != null) {
         var42.add(
            var43.mine()
               ? t("gui.scripts.market.origin.mine")
               : t("gui.scripts.market.origin.from").replace("{n}", var43.getText3().isBlank() ? "?" : var43.getText3())
         );
      }

      if (var11.isFlag()) {
         String var44 = ScriptPermissions.method5(var11);
         if (!var44.isEmpty()) {
            var42.add(t("gui.scripts.market.rights").replace("{r}", var44));
         }
      }

      if (!var11.getItems3().isEmpty()) {
         var42.add(t("gui.scripts.card.modules").replace("{n}", String.valueOf(var11.getItems3().size())));
      }

      String var48 = ago(var10.modified());
      if (!var48.isEmpty()) {
         var42.add(var48);
      }

      ScriptMarketTab.drawMeta(var1, String.join("  ·  ", var42), var8.getTextTertiary().getZenithColor(), var47, var45, var46, var35.meta(), var7);
      this.drawSwitch(var1, "script:" + var11.getId(), var28, var29, var26, var27, var9.method1297(var11), var7, var8);
      if (var13) {
         HoverInfo.request(var11.method1403(), t("gui.scripts.card.hint"));
      }

      this.homeHits.add(new Object[]{new BoundsData(var28 - 5.0F, var12.getFloatValue2(), var26 + 12.0F, var12.height()), "toggle", var11});
      if (var32 != null) {
         this.homeHits.add(new Object[]{var32, "update", var11});
      }

      this.homeHits.add(new Object[]{var12, "open", var11});
   }

   private String stateText(Storage var1, Script var2) {
      return switch (var2.getType2()) {
         case RUNNING -> t("gui.scripts.state.running");
         case FAILED -> t("gui.scripts.state.failed");
         default -> var1.method1297(var2) ? t("gui.scripts.state.waiting") : t("gui.scripts.state.off");
      };
   }

   public void drawSwitch(MenuDrawContext var1, String var2, float var3, float var4, float var5, float var6, boolean var7, float var8, ZenithStyle var9) {
      float var10 = this.switchAnimations
         .computeIfAbsent(var2, var1x -> new Animation(220L, var7 ? 1.0F : 0.0F, Easing.INSTANCE15))
         .method2808(var7 ? 1.0F : 0.0F);
      ZenithColor var11 = var9.getFieldBorder().getZenithColor().method7294(3.0F);
      var1.drawRoundedRect(
         var3, var4, var5, var6, ProxyDotRadiusData.method8069(var6 / 2.0F), var11.method7295(var9.getPrimaryColor().getZenithColor(), var10).method7294(var8)
      );
      float var12 = var6 - 2.0F;
      float var13 = var3 + 1.0F + (var5 - var12 - 2.0F) * var10;
      float var14 = (float)Math.sin(var10 * Math.PI) * 1.5F;
      var1.drawRoundedRect(
         var13 - var14 * 0.5F,
         var4 + 1.0F,
         var12 + var14,
         var12,
         ProxyDotRadiusData.method8069(var12 / 2.0F),
         var9.getTextSecondary().getZenithColor().method7295(ZenithColor.INSTANCE, var10).method7294(var8)
      );
   }

   private float pressFlash(String var1) {
      return Smooth.press(this.pressedAt.getOrDefault(var1, 0L), 340L);
   }

   private boolean handleHome(Object[] var1) {
      Storage var2 = host();
      if (var2 == null) {
         return false;
      } else {
         String var3 = (String)var1[1];
         if (var1.length > 2 && var1[2] instanceof Script var4) {
            this.pressedAt.put("row:" + var4.getId(), System.currentTimeMillis());
         } else {
            this.pressedAt.put("hero:" + var3, System.currentTimeMillis());
            if (var1[0] instanceof BoundsData var7) {
               this.pressFx
                  .press(
                     "hero:" + var3,
                     this.lastClickX >= 0.0 ? this.lastClickX : var7.getFloatValue() + var7.width() / 2.0F,
                     this.lastClickY >= 0.0 ? this.lastClickY : var7.getFloatValue2() + var7.height() / 2.0F
                  );
            }
         }

         switch (var3) {
            case "new":
               this.creatorOpen = !this.creatorOpen;
               this.creatorAnimation.reset(0.0F);
               this.creatorAnimation.method2810(1.0F);
               this.invalidate();
               break;
            case "reload":
               var2.method1300();
               this.invalidate();
               break;
            case "folder":
               Util.getOperatingSystem().open(var2.getPath().toFile());
               break;
            case "api":
               Util.getOperatingSystem().open(var2.getPath().resolve("API.md").toFile());
               break;
            case "toggle":
               Script var9 = (Script)var1[2];
               var2.method1298(var9, !var2.method1297(var9));
               this.invalidate();
               break;
            case "open":
               this.openWorkspace((Script)var1[2]);
               break;
            case "update":
               this.market.update(var2, (Script)var1[2]);
               break;
            default:
               if (!var3.startsWith("template:")) {
                  return false;
               }

               this.creatorOpen = false;
               this.create(var3.substring("template:".length()));
         }

         return true;
      }
   }

   private BoundsData asideViewportOrNull() {
      float var1 = NLMenuScreen.leftWidthPanel;
      BoundsData var2 = this.mainViewport();
      return var2 == null
         ? null
         : new BoundsData(
            var2.getFloatValue() + var2.width(), var2.getFloatValue2() - 30.0F, 144.0F, NLMenuScreen.contentBottom - var2.getFloatValue2() + 30.0F
         );
   }

   private void create(String var1) {
      Storage var2 = host();
      if (var2 != null) {
         String var3 = "script";

         for (int var4 = 2; var2.method1294(var3) != null || Files.exists(var2.getPath().resolve(var3)); var4++) {
            var3 = "script" + var4;
         }

         try {
            Script var6 = var2.method1340(var3, var1);
            var2.method1298(var6, true);
            this.invalidate();
            this.openWorkspace(var6);
         } catch (Exception var5) {
            this.evalLines.add("! " + var5.getMessage());
         }
      }
   }

   private static ZenithColor levelColor(String var0, ZenithStyle var1) {
      return switch (var0) {
         case "error" -> ERROR;
         case "warn" -> WARN;
         case "eval" -> var1.getTextEnable().getZenithColor();
         default -> var1.getTextSecondary().getZenithColor();
      };
   }

   @Override
   public float footerHeight() {
      return this.workspace == null && this.getActiveTab() == 3 ? this.ai.footerHeight() : 0.0F;
   }

   @Override
   public void renderFooter(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      if (this.getActiveTab() == 3) {
         this.ai.renderFooter(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public boolean onFooterClicked(double var1, double var3, Enum3 var5) {
      return this.getActiveTab() == 3 && this.ai.clickFooter(var1, var3);
   }

   @Override
   public float mainHeaderHeight() {
      if (this.workspace != null) {
         return 0.0F;
      } else if (this.storeTab()) {
         return this.market.headerHeight();
      } else {
         return this.getActiveTab() == 3 ? this.ai.headerHeight() : 0.0F;
      }
   }

   @Override
   public void renderMainHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      if (this.storeTab()) {
         this.market.renderHeader(var1, var2, var3, var4, var5, var6, var7, var8);
      } else if (this.getActiveTab() == 3) {
         this.ai.renderHeader(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public void selectTab(int var1) {
      if (var1 != this.getActiveTab()) {
         this.ai.commitFields();
         if (this.settingsScript != null) {
            this.closeSettings();
         }
      }

      super.selectTab(var1);
   }

   public void openWorkspace(Script var1) {
      Storage var2 = host();
      if (var2 != null) {
         OriginUtil.Data2 var3 = OriginUtil.method6921(var1);
         if (var3 != null && !var3.mine() && StoreApi.method1960(var3.getUUID()).isFlag()) {
            this.toast(t("gui.market.sealed.locked"), true);
         } else {
            if (this.code == null) {
               this.code = new CodeArea(var2::method1343);
            }

            this.workspace = var1;
            this.files = var2.method1345(var1);
            this.moduleRows.clear();
            this.expanded.clear();
            this.filePlateReady = false;
            this.inspectorScroll = this.inspectorScrollTarget = 0.0F;
            this.consoleScroll = 0.0F;
            this.openFile(var1.getPath2());
            this.infoPage = false;
            this.workspaceAnimation.reset(0.0F);
            this.workspaceAnimation.method2810(1.0F);
            Zenith.getInstance().getClientSounds().method1562();
         }
      }
   }

   private void closeWorkspace() {
      if (this.workspace != null) {
         this.showCode();
         if (this.code != null && this.code.isDirty()) {
            this.save();
         }

         this.workspace = null;
         this.activeRow = null;
         this.inputFocused = false;
         this.invalidate();
         Zenith.getInstance().getClientSounds().method1563();
      }
   }

   private void openFile(Path var1) {
      Storage var2 = host();
      if (var2 != null && this.workspace != null) {
         try {
            this.code.setText(Files.isRegularFile(var1) ? var2.method1306(var1) : "");
            this.file = var1;
            this.code.setErrorLine(Storage.method1348(this.workspace, this.file));
            this.code.setFocused(true);
            this.inputFocused = false;
         } catch (Exception var4) {
            this.toast(t("gui.scripts.toast.openFailed") + " " + var4.getMessage(), true);
         }
      }
   }

   private void save() {
      Storage var1 = host();
      if (var1 != null && this.workspace != null && this.file != null) {
         try {
            var1.method1347(this.workspace, this.file, this.code.getText());
            this.code.markSaved();
            var1.method1299(this.workspace);
            this.code.setErrorLine(Storage.method1348(this.workspace, this.file));
            this.moduleRows.clear();
            if (this.workspace.getError().isBlank()) {
               this.toast(t("gui.scripts.toast.saved"), false);
            } else {
               this.toast(this.workspace.getError(), true);
            }
         } catch (Exception var3) {
            this.toast(t("gui.scripts.toast.saveFailed") + " " + var3.getMessage(), true);
         }
      }
   }

   private void newFile() {
      Storage var1 = host();
      if (var1 != null && this.workspace != null) {
         if (this.workspace.getPath() == null) {
            this.toast(t("gui.scripts.toast.singleFile"), true);
         } else {
            try {
               for (int var2 = 1; var2 < 100; var2++) {
                  Path var3 = var1.method1346(this.workspace, "util" + (var2 == 1 ? "" : var2) + ".js");
                  if (!Files.exists(var3)) {
                     var1.method1347(this.workspace, var3, "// include(\"" + var3.getFileName() + "\")\n");
                     this.files = var1.method1345(this.workspace);
                     if (this.code.isDirty()) {
                        this.save();
                     }

                     this.openFile(var3);
                     return;
                  }
               }
            } catch (Exception var4) {
               this.toast(var4.getMessage(), true);
            }
         }
      }
   }

   private void listToast(String var1, boolean var2) {
      if (this.workspace != null) {
         this.toast(var1, var2);
      } else {
         this.evalLines.add((var2 ? "! " : "  ") + var1);
         this.toast(var1, var2);
         this.invalidate();
      }
   }

   private void toast(String var1, boolean var2) {
      this.toast = var1;
      this.toastError = var2;
      this.toastAt = System.currentTimeMillis();
   }

   private void runScriptAction(Runnable var1) {
      Storage var2 = host();
      if (var2 != null && this.workspace != null) {
         var1.run();
         this.moduleRows.clear();
         this.code.setErrorLine(Storage.method1348(this.workspace, this.file));
      }
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      if (this.workspace != null) {
         Storage var7 = host();
         if (var7 != null && var7.method1294(this.workspace.getId()) != null) {
            this.renderWorkspace(var1, var2, var3, var4, var5, var6);
            return;
         }

         this.workspace = null;
         this.invalidate();
      }

      this.homeHits.clear();
      long var15 = System.currentTimeMillis();
      if (var15 - this.lastCheck > 250L) {
         this.lastCheck = var15;
         long var9 = this.signature();
         if (var9 != this.lastSignature) {
            this.lastSignature = var9;
            this.invalidate();
         }
      }

      if (this.getActiveTab() == 3 && this.ai.pollChanged()) {
         this.invalidate();
      }

      int var16 = this.getActiveTab();
      if (this.shownTab != var16) {
         if (this.shownTab >= 0 && (this.shownTab == 1 || var16 == 1)) {
            this.tabDirection = var16 > this.shownTab ? 1.0F : -1.0F;
            this.tabSwitch.reset(0.0F);
            this.tabSwitch.method2810(1.0F);
         }

         this.shownTab = var16;
      }

      float var10 = this.tabSwitch.method2808(1.0F);
      float var11 = 14.0F * this.tabDirection * (1.0F - var10);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var11, 0.0F);

      try {
         if (this.configsTab()) {
            this.configs.render(var1, var2, var3, var4 * var10, var5, var6);
         } else {
            super.render(var1, var2, var3, var4 * var10, var5, var6);
         }
      } finally {
         var1.getMatrices().popMatrix();
      }
   }

   @Override
   public void renderPriority(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      if (this.configsTab()) {
         this.configs.renderPriority(var1, var2, var3, var4, var5, var6);
      } else {
         super.renderPriority(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void tick() {
      if (this.configsTab()) {
         this.configs.tick();
      } else {
         super.tick();
      }
   }

   @Override
   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      if (this.workspace == null) {
         super.renderHeader(var1, var2, var3, var4, var5, var6, var7);
      } else {
         ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var8 != null) {
            float var9 = var4 * this.workspaceAnimation.getValue();
            Font var10 = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font var11 = Fonts.NEW_MEDIUM.getFont(4.8F);
            float var12 = var6 + 11.5F;
            float var13 = var5 + 8.0F;
            ZenithColor var14 = var8.getTextTertiary().getZenithColor();
            this.headerBack = new BoundsData(var13 - 2.0F, var12 - 7.0F, 14.0F, 14.0F);
            boolean var15 = this.headerBack.contains(var2, var3);
            var1.drawRoundedRect(
               this.headerBack.getFloatValue(),
               this.headerBack.getFloatValue2(),
               this.headerBack.width(),
               this.headerBack.height(),
               ProxyDotRadiusData.method8069(4.0F),
               var8.getSurfaceDisableBackground().getZenithColor().method7294(var9 * (var15 ? 1.0F : 0.55F))
            );
            var1.drawText(
               var10,
               "‹",
               this.headerBack.getFloatValue() + (14.0F - var10.width("‹")) / 2.0F,
               var12 - var10.height() / 2.0F - 0.5F,
               (var15 ? var8.getTextEnable() : var8.getTextSecondary()).getZenithColor().method7294(var9)
            );
            var13 += 17.0F;
            this.headerRoot = new BoundsData(var13 - 2.0F, var12 - 7.0F, var10.width("Scripts") + 4.0F, 14.0F);
            var13 = crumb(var1, var10, "Scripts", var13, var12, this.headerRoot.contains(var2, var3) ? var8.getTextEnable().getZenithColor() : var14, var9);
            var13 = crumb(var1, var10, "/", var13, var12, var14.method7294(0.6F), var9);
            var13 = crumb(var1, var10, this.workspace.method1403(), var13, var12, var8.getTextEnable().getZenithColor(), var9);
            if (this.infoPage) {
               var13 = crumb(var1, var10, "/", var13, var12, var14.method7294(0.6F), var9);
               crumb(
                  var1,
                  var10,
                  t("gui.scripts.ws.about") + (this.info != null && this.info.isDirty() ? "  •" : ""),
                  var13,
                  var12,
                  this.info != null && this.info.isDirty() ? WARN : var8.getTextSecondary().getZenithColor(),
                  var9
               );
            } else if (this.file != null) {
               Path var16 = this.workspace.getPath() == null ? this.file.getParent() : this.workspace.getPath();
               var13 = crumb(var1, var10, "/", var13, var12, var14.method7294(0.6F), var9);
               String var17 = var16.relativize(this.file).toString().replace('\\', '/') + (this.code.isDirty() ? "  •" : "");
               crumb(var1, var10, var17, var13, var12, this.code.isDirty() ? WARN : var8.getTextSecondary().getZenithColor(), var9);
            }

            this.headerActions.clear();
            this.headerActions.put("gui.scripts.ws.ai", () -> {
               Script var1x = this.workspace;
               this.closeWorkspace();
               AiChatStorage.method2741().method2751(var1x);
               this.selectTab(3);
               this.invalidate();
            });
            this.headerActions
               .put(
                  "gui.scripts.ws.reload",
                  () -> this.runScriptAction(
                     () -> {
                        if (this.code.isDirty()) {
                           this.save();
                        } else {
                           host().method1299(this.workspace);
                        }

                        this.toast(
                           this.workspace.getError().isBlank() ? t("gui.scripts.toast.reloaded") : this.workspace.getError(),
                           !this.workspace.getError().isBlank()
                        );
                     }
                  )
               );
            this.headerActions
               .put(
                  this.workspace.isRunning() ? "gui.scripts.ws.stop" : "gui.scripts.ws.run",
                  () -> this.runScriptAction(
                     () -> {
                        if (this.code.isDirty()) {
                           this.save();
                        }

                        host().method1298(this.workspace, !this.workspace.isRunning());
                        this.toast(
                           this.workspace.isRunning()
                              ? t("gui.scripts.toast.started")
                              : (this.workspace.getError().isBlank() ? t("gui.scripts.toast.stopped") : this.workspace.getError()),
                           !this.workspace.getError().isBlank()
                        );
                     }
                  )
               );
            if (this.infoPage) {
               this.headerActions.put(this.info != null && this.info.isDirty() ? "gui.scripts.ws.save" : "gui.scripts.ws.saved", () -> this.info().apply());
            } else {
               this.headerActions.put(this.code.isDirty() ? "gui.scripts.ws.save" : "gui.scripts.ws.saved", this::save);
            }

            this.headerButtons.clear();
            float var32 = var5 + var7;
            var var34 = new ArrayList<>(this.headerActions.entrySet());

            for (int var18 = var34.size() - 1; var18 >= 0; var18--) {
               String var19 = (String)((Map.Entry)var34.get(var18)).getKey();
               String var20 = t(var19);
               float var21 = var11.width(var20) + 14.0F;
               var32 -= var21;
               BoundsData var22 = new BoundsData(var32, var12 - 6.5F, var21, 13.0F);
               boolean var23 = var22.contains(var2, var3);
               boolean var24 = var19.equals("gui.scripts.ws.save");
               ZenithColor var25 = var24
                  ? var8.getPrimaryColor().getZenithColor().method7294(var23 ? 1.0F : 0.85F)
                  : var8.getSurfaceDisableBackground().getZenithColor().method7294(var23 ? 1.0F : 0.6F);
               var1.drawRoundedRect(
                  var22.getFloatValue(), var22.getFloatValue2(), var22.width(), var22.height(), ProxyDotRadiusData.method8069(4.0F), var25.method7294(var9)
               );
               var1.drawText(
                  var11,
                  var20,
                  var22.getFloatValue() + 7.0F,
                  var12 - var11.height() / 2.0F,
                  (var24 ? ZenithColor.INSTANCE : var8.getTextEnable().getZenithColor()).method7294(var9)
               );
               this.headerButtons.put(var19, var22);
               var32 -= 4.0F;
            }
         }
      }
   }

   private static float crumb(MenuDrawContext var0, Font var1, String var2, float var3, float var4, ZenithColor var5, float var6) {
      var0.drawText(var1, var2, var3, var4 - var1.height() / 2.0F, var5.method7294(var6));
      return var3 + var1.width(var2) + 5.0F;
   }

   @Override
   public float getButtonWidth() {
      return this.workspace != null ? 0.0F : super.getButtonWidth();
   }

   @Override
   public void renderHeaderButtons(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
      if (this.workspace == null) {
         super.renderHeaderButtons(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public boolean onHeaderButtonsClicked(double var1, double var3, Enum3 var5) {
      if (this.workspace == null) {
         return super.onHeaderButtonsClicked(var1, var3, var5);
      } else if ((this.headerBack == null || !this.headerBack.contains(var1, var3)) && (this.headerRoot == null || !this.headerRoot.contains(var1, var3))) {
         for (Map.Entry var7 : this.headerButtons.entrySet()) {
            if (((BoundsData)var7.getValue()).contains(var1, var3)) {
               Runnable var8 = this.headerActions.get(var7.getKey());
               if (var8 != null) {
                  var8.run();
               }

               return true;
            }
         }

         return false;
      } else {
         this.closeWorkspace();
         return true;
      }
   }

   @Override
   public void renderLeftColumn(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
      ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Storage var10 = host();
      if (var9 != null && this.workspace != null && var10 != null) {
         if (++this.filesTick % 40 == 0) {
            long var11 = System.currentTimeMillis();
            if (var11 - this.filesAt >= 500L) {
               this.filesAt = var11;
               this.files = var10.method1345(this.workspace);
            }
         }

         this.leftBounds = new BoundsData(var5, var6, var7, var8);
         Font var33 = Fonts.NEW_MEDIUM.getFont(5.2F);
         Font var12 = Fonts.NEW_REGULAR.getFont(4.4F);
         Font var13 = Fonts.NEW_MEDIUM.getFont(4.2F);
         float var14 = 10.0F;
         this.backRect = new BoundsData(var5 + 6.0F, var6 + 6.0F, var7 - 12.0F, 14.0F);
         boolean var15 = this.backRect.contains(var2, var3);
         if (var15) {
            var1.drawRoundedRect(
               this.backRect.getFloatValue(),
               this.backRect.getFloatValue2(),
               this.backRect.width(),
               this.backRect.height(),
               ProxyDotRadiusData.method8069(4.0F),
               var9.getSurfaceEnableBackground().getZenithColor().method7294(var4 * 0.4F)
            );
         }

         var1.drawText(
            var33,
            "‹  " + t("gui.scripts.ws.back"),
            var5 + var14,
            this.backRect.getFloatValue2() + (14.0F - var33.height()) / 2.0F,
            (var15 ? var9.getTextEnable() : var9.getTextSecondary()).getZenithColor().method7294(var4)
         );
         float var16 = var6 + 30.0F;
         Font var17 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
         ScriptInfoPage.drawAvatar(var1, var9, this.workspace, var5 + var14, var16 - 1.0F, 20.0F, var4, -2);
         var1.drawText(
            var17,
            var17.clip(this.workspace.method1403(), var7 - var14 * 2.0F - 25.0F),
            var5 + var14 + 25.0F,
            var16,
            var9.getTextEnable().getZenithColor().method7294(var4)
         );
         var16 += var17.height() + 3.0F;
         boolean var18 = this.workspace.getType2() == Script.Type2.FAILED;
         ZenithColor var19 = this.workspace.isRunning() ? OK : (var18 ? ERROR : var9.getTextTertiary().getZenithColor());
         var1.drawRoundedRect(
            var5 + var14 + 25.0F, var16 + var12.height() / 2.0F - 1.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), var19.method7294(var4)
         );
         var1.drawText(
            var12,
            var12.clip(
               this.stateText(var10, this.workspace) + (this.workspace.getVersion().isBlank() ? "" : "  ·  v" + this.workspace.getVersion()),
               var7 - var14 * 2.0F - 31.0F
            ),
            var5 + var14 + 31.0F,
            var16,
            var9.getTextTertiary().getZenithColor().method7294(var4)
         );
         var16 += var12.height() + 10.0F;
         this.infoRect = null;
         var1.drawText(var13, t("gui.scripts.ws.files"), var5 + var14, var16, var9.getTextTertiary().getZenithColor().method7294(var4));
         var16 += var13.height() + 5.0F;
         this.fileRects.clear();
         Path var20 = this.workspace.getPath() == null ? this.workspace.getPath2().getParent() : this.workspace.getPath();
         float var21 = this.workspaceAnimation.getValue();
         float var22 = var6 + var8 - 44.0F;
         float var23 = -1.0F;

         for (int var24 = 0; var24 < this.files.size(); var24++) {
            float var25 = var16 + var24 * 15.0F;
            if (var25 > var22) {
               break;
            }

            if (!this.infoPage && this.files.get(var24).equals(this.file)) {
               var23 = var25;
            }
         }

         if (var23 >= 0.0F) {
            if (!this.filePlateReady) {
               this.filePlate.reset(var23);
               this.filePlateReady = true;
            }

            float var37 = this.filePlate.method2808(var23);
            var1.drawRoundedRect(
               var5 + 6.0F, var37, var7 - 12.0F, 14.0F, ProxyDotRadiusData.method8069(4.0F), var9.getPrimaryColor().getZenithColor().method7294(var4 * 0.2F)
            );
            var1.drawRoundedRect(
               var5 + 6.0F, var37 + 3.5F, 1.5F, 7.0F, ProxyDotRadiusData.method8069(0.75F), var9.getPrimaryColor().getZenithColor().method7294(var4)
            );
         }

         int var38 = 0;

         for (Path var26 : this.files) {
            float var27 = var16 + var38 * 15.0F;
            if (var27 > var22) {
               break;
            }

            float var28 = var4 * MathHelper.clamp(var21 * 3.0F - var38 * 0.25F, 0.0F, 1.0F);
            BoundsData var29 = new BoundsData(var5 + 6.0F, var27, var7 - 12.0F, 14.0F);
            boolean var30 = !this.infoPage && var26.equals(this.file);
            if (!var30 && var29.contains(var2, var3)) {
               var1.drawRoundedRect(
                  var29.getFloatValue(),
                  var29.getFloatValue2(),
                  var29.width(),
                  var29.height(),
                  ProxyDotRadiusData.method8069(4.0F),
                  var9.getSurfaceEnableBackground().getZenithColor().method7294(var28 * 0.35F)
               );
            }

            String var31 = var20.relativize(var26).toString().replace('\\', '/');
            boolean var32 = Storage.method1348(this.workspace, var26) > 0;
            (var31.endsWith(".js") ? MenuGlyph.CODE : MenuGlyph.TEXT)
               .draw(
                  var1,
                  var29.getFloatValue() + 5.0F,
                  var27 + 4.0F,
                  (var30 ? var9.getPrimaryColor().getZenithColor() : var9.getTextTertiary().getZenithColor()).method7294(var28)
               );
            var1.drawText(
               var12,
               var12.clip(var31 + (var30 && this.code.isDirty() ? "  •" : ""), var29.width() - 22.0F),
               var29.getFloatValue() + 15.0F,
               var27 + (14.0F - var12.height()) / 2.0F,
               (var32 ? ERROR : (var30 ? var9.getTextEnable().getZenithColor() : var9.getTextSecondary().getZenithColor())).method7294(var28)
            );
            this.fileRects.add(new Object[]{var29, var26});
            var38++;
         }

         this.newFileRect = null;
         if (this.workspace.getPath() != null) {
            float var40 = var16 + var38 * 15.0F + 2.0F;
            this.newFileRect = new BoundsData(var5 + 6.0F, var40, var7 - 12.0F, 14.0F);
            boolean var43 = this.newFileRect.contains(var2, var3);
            var1.drawText(
               var12,
               "+  " + t("gui.scripts.ws.newFile"),
               var5 + 11.0F,
               var40 + (14.0F - var12.height()) / 2.0F,
               (var43 ? var9.getTextEnable() : var9.getTextTertiary()).getZenithColor().method7294(var4)
            );
         }

         float var41 = var6 + var8 - 74.0F;
         if (!this.infoPage && this.code != null && var41 > var16 + var38 * 15.0F + 24.0F) {
            var1.drawText(var13, t("gui.scripts.ws.stats"), var5 + var14, var41, var9.getTextTertiary().getZenithColor().method7294(var4));
            var41 += var13.height() + 4.0F;
            String[] var44 = new String[]{
               t("gui.scripts.ws.lines").replace("{n}", String.valueOf(this.code.lineCount())),
               t("gui.scripts.ws.caret").replace("{l}", String.valueOf(this.code.caretLine())).replace("{c}", String.valueOf(this.code.caretColumn())),
               t("gui.scripts.ws.modulesCount").replace("{n}", String.valueOf(this.workspace.getItems3().size()))
            };

            for (String var52 : var44) {
               var1.drawText(var12, var12.clip(var52, var7 - var14 * 2.0F), var5 + var14, var41, var9.getTextSecondary().getZenithColor().method7294(var4));
               var41 += var12.height() + 2.5F;
            }
         }

         float var45 = var6 + var8 - 36.0F;
         var1.drawRect(var5 + var14, var45 - 6.0F, var7 - var14 * 2.0F, 1.0F, var9.getFieldBorder().getZenithColor().method7294(var4 * 2.0F));
         String[] var47 = new String[]{
            "Ctrl+S  " + t("gui.scripts.hint.save"), "Ctrl+Space  " + t("gui.scripts.hint.complete"), "Esc  " + t("gui.scripts.hint.back")
         };

         for (String var54 : var47) {
            var1.drawText(var12, var12.clip(var54, var7 - var14 * 2.0F), var5 + var14, var45, var9.getTextTertiary().getZenithColor().method7294(var4));
            var45 += var12.height() + 2.5F;
         }
      }
   }

   private void renderWorkspace(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         float var8 = this.workspaceAnimation.method2808(1.0F);
         float var9 = var4 * var8;
         float var10 = (1.0F - var8) * 8.0F;
         float var11 = 380.0F + NLMenuScreen.extraWidth;
         float var12 = NLMenuScreen.contentBottom - var6 - GuiStyle.PADDING.intValue() * 2.0F;
         float var13 = var6 + var10;
         if (this.infoPage) {
            this.codeBounds = new BoundsData(var5 + 8.0F, var13, var11 - 16.0F, var12);
            this.inspectorBounds = null;
            this.consoleBounds = null;
            this.inputBounds = null;
            this.info().render(var1, var2, var3, var5 + 8.0F, var13, var11 - 16.0F, var12, var9, var7);
            this.renderToast(var1, var9, var7);
         } else {
            float var14 = var13 + var12 - 62.0F;
            this.codeBounds = new BoundsData(var5 + 8.0F, var13, var11 - 16.0F, var14 - var13 - 6.0F);
            this.code
               .render(
                  var1,
                  this.codeBounds.getFloatValue(),
                  this.codeBounds.getFloatValue2(),
                  this.codeBounds.width(),
                  this.codeBounds.height(),
                  var2,
                  var3,
                  var9,
                  EDITOR_BG
               );
            this.inspectorBounds = null;
            this.consoleBounds = new BoundsData(var5 + 8.0F, var14, var11 - 16.0F, 62.0F);
            this.renderWorkspaceConsole(var1, var9, var7);
            this.renderToast(var1, var9, var7);
         }
      }
   }

   private void renderToast(MenuDrawContext var1, float var2, ZenithStyle var3) {
      if (!this.toast.isEmpty()) {
         long var4 = System.currentTimeMillis() - this.toastAt;
         float var6 = var4 < 200L ? (float)var4 / 200.0F : (var4 > 3600L ? Math.max(0.0F, 1.0F - (float)(var4 - 3600L) / 400.0F) : 1.0F);
         if (var6 <= 0.01F) {
            this.toast = "";
         } else {
            Font var7 = Fonts.NEW_MEDIUM.getFont(4.8F);
            String var8 = var7.clip(this.toast, this.codeBounds.width() - 40.0F);
            float var9 = var7.width(var8) + 22.0F;
            float var10 = this.codeBounds.getFloatValue() + (this.codeBounds.width() - var9) / 2.0F;
            float var11 = this.codeBounds.getFloatValue2() + this.codeBounds.height() - 22.0F + (1.0F - var6) * 6.0F;
            var1.drawRoundedRect(var10, var11, var9, 15.0F, ProxyDotRadiusData.method8069(7.5F), new ZenithColor(18, 20, 28, 240).method7294(var2 * var6));
            var1.drawRoundedRect(
               var10 + 7.0F, var11 + 6.0F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), (this.toastError ? ERROR : OK).method7294(var2 * var6)
            );
            var1.drawText(
               var7,
               var8,
               var10 + 14.0F,
               var11 + (15.0F - var7.height()) / 2.0F,
               (this.toastError ? ERROR.method7303(0.3F) : var3.getTextEnable().getZenithColor()).method7294(var2 * var6)
            );
         }
      }
   }

   private void renderInspector(MenuDrawContext var1, int var2, int var3, float var4, ZenithStyle var5) {
      BoundsData var6 = this.inspectorBounds;
      Font var7 = Fonts.NEW_MEDIUM.getFont(4.2F);
      Font var8 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var9 = Fonts.NEW_REGULAR.getFont(4.4F);
      this.inspectorHits.clear();
      this.inspectorScroll = this.inspectorScroll + (this.inspectorScrollTarget - this.inspectorScroll) * 0.3F;
      MsdfRenderer.flushBatch();
      var1.enableScissor(var6.getFloatValue(), var6.getFloatValue2(), var6.getFloatValue() + var6.width(), var6.getFloatValue2() + var6.height());
      float var10 = var6.getFloatValue2() + 2.0F + this.inspectorScroll;
      float var11 = var10;
      if (!this.workspace.getError().isBlank()) {
         var1.drawText(var7, t("gui.scripts.ws.error"), var6.getFloatValue() + 2.0F, var10, ERROR.method7294(var4));
         var10 += var7.height() + 4.0F;
         List<String> var12 = wrapText(this.workspace.getError(), var9, var6.width() - 12.0F, 6);
         float var13 = var12.size() * (var9.height() + 2.0F) + 8.0F;
         BoundsData var14 = new BoundsData(var6.getFloatValue(), var10, var6.width(), var13);
         var1.drawRoundedRect(
            var14.getFloatValue(), var14.getFloatValue2(), var14.width(), var14.height(), ProxyDotRadiusData.method8069(4.0F), ERROR.method7294(var4 * 0.12F)
         );
         float var15 = var10 + 4.0F;

         for (Object var17_o : (Iterable<?>)(Object)(var12)) {
            String var17 = (String)var17_o;
            var1.drawText(var9, var17, var6.getFloatValue() + 6.0F, var15, ERROR.method7303(0.3F).method7294(var4));
            var15 += var9.height() + 2.0F;
         }

         int var34 = Storage.method1348(this.workspace, this.file);
         if (var34 > 0) {
            this.inspectorHits.add(new Object[]{var14, (Runnable)() -> {
               this.code.goToLine(var34);
               this.code.setFocused(true);
            }});
         }

         var10 += var13 + 8.0F;
      }

      var1.drawText(var7, t("gui.scripts.ws.modules"), var6.getFloatValue() + 2.0F, var10, var5.getTextTertiary().getZenithColor().method7294(var4));
      var10 += var7.height() + 5.0F;
      if (this.workspace.getItems3().isEmpty()) {
         for (String var30 : wrapText(t("gui.scripts.ws.noModules"), var9, var6.width() - 4.0F, 5)) {
            var1.drawText(var9, var30, var6.getFloatValue() + 2.0F, var10, var5.getTextTertiary().getZenithColor().method7294(var4));
            var10 += var9.height() + 2.0F;
         }

         var10 += 6.0F;
      }

      for (Module1 var31 : this.workspace.getItems3().values()) {
         BoundsData var32 = new BoundsData(var6.getFloatValue(), var10, var6.width(), 16.0F);
         boolean var33 = var32.contains(var2, var3);
         var1.drawRoundedRect(
            var32.getFloatValue(),
            var32.getFloatValue2(),
            var32.width(),
            var32.height(),
            ProxyDotRadiusData.method8069(4.0F),
            var5.getSurfaceDisableBackground().getZenithColor().method7294(var4 * (var33 ? 0.9F : 0.55F))
         );
         boolean var35 = this.expanded.getOrDefault(var31, true);
         var1.drawText(
            var8,
            var8.clip(var31.getName(), var6.width() - 40.0F),
            var6.getFloatValue() + 6.0F,
            var10 + (16.0F - var8.height()) / 2.0F,
            var5.getTextEnable().getZenithColor().method7294(var4)
         );
         float var36 = 13.0F;
         float var18 = 7.0F;
         float var19 = var6.getFloatValue() + var6.width() - var36 - 5.0F;
         float var20 = var10 + (16.0F - var18) / 2.0F;
         this.drawSwitch(var1, "module:" + var31.getId(), var19, var20, var36, var18, var31.isEnabled(), var4, var5);
         this.inspectorHits.add(new Object[]{new BoundsData(var19 - 4.0F, var10, var36 + 9.0F, 16.0F), (Runnable)var31::toggle});
         this.inspectorHits.add(new Object[]{var32, (Runnable)() -> this.expanded.put(var31, !var35)});
         var10 += 18.0F;
         if (var35) {
            List<InspectorRow> var21 = this.moduleRows.computeIfAbsent(var31, var0 -> {
               ArrayList var1x = new ArrayList();

               for (Setting var3x : var0.getSettings()) {
                  var1x.add(new InspectorRow(var3x, 0));
               }

               return var1x;
            });
            InspectorRow.alignTracks(var21, var6.width());

            for (Object var23_o : (Iterable<?>)(Object)(var21)) {
               InspectorRow var23 = (InspectorRow)var23_o;
               if (var23.isVisible()) {
                  float var24 = var23.height();
                  var23.render(var1, var2, var3, var6.getFloatValue(), var10, var6.width(), var4);
                  var10 += var24;
               }
            }

            var10 += 4.0F;
         }
      }

      MsdfRenderer.flushBatch();
      var1.disableScissor();
      float var29 = var10 - var11;
      this.inspectorScrollTarget = MathHelper.clamp(this.inspectorScrollTarget, Math.min(0.0F, var6.height() - var29 - 6.0F), 0.0F);
   }

   private void renderWorkspaceConsole(MenuDrawContext var1, float var2, ZenithStyle var3) {
      BoundsData var4 = this.consoleBounds;
      Storage var5 = host();
      var1.drawRoundedRect(
         var4.getFloatValue(), var4.getFloatValue2(), var4.width(), var4.height(), ProxyDotRadiusData.method8069(4.0F), EDITOR_BG.method7294(var2)
      );
      Font var6 = Fonts.NEW_MEDIUM.getFont(4.2F);
      Font var7 = Fonts.NEW_REGULAR.getFont(4.4F);
      var1.drawText(
         var6, t("gui.scripts.ws.console"), var4.getFloatValue() + 6.0F, var4.getFloatValue2() + 5.0F, var3.getTextTertiary().getZenithColor().method7294(var2)
      );
      var var8 = var5 != null ? var5.method1349(this.workspace) : List.of();
      int var9 = var8.size() + this.evalLines.size();
      float var10 = var7.height() + 2.5F;
      float var11 = var4.getFloatValue2() + 13.0F;
      float var12 = 13.0F;
      float var13 = var4.getFloatValue2() + var4.height() - var12 - 6.0F;
      int var14 = Math.max(1, (int)((var13 - var11) / var10));
      this.consoleScroll = MathHelper.clamp(this.consoleScroll, 0.0F, (float)Math.max(0, var9 - var14));
      int var15 = Math.max(0, var9 - var14 - (int)this.consoleScroll);
      float var16 = var11;

      for (int var17 = var15; var17 < Math.min(var9, var15 + var14); var17++) {
         String var18;
         String var19;
         if (var17 < var8.size()) {
            Storage.Data2 var20 = (Storage.Data2)var8.get(var17);
            var18 = TIME.format(new Date(var20.time())) + "  " + var20.text();
            var19 = var20.getText2();
         } else {
            var18 = this.evalLines.get(var17 - var8.size());
            var19 = var18.startsWith("! ") ? "error" : "eval";
         }

         var1.drawText(var7, var7.clip(var18, var4.width() - 12.0F), var4.getFloatValue() + 6.0F, var16, levelColor(var19, var3).method7294(var2));
         var16 += var10;
      }

      this.inputBounds = new BoundsData(var4.getFloatValue() + 4.0F, var4.getFloatValue2() + var4.height() - var12 - 4.0F, var4.width() - 8.0F, var12);
      this.renderInput(var1, this.inputBounds, var2, var3, this.workspace.isRunning() ? t("gui.scripts.ws.inputHint") : t("gui.scripts.console.hint"));
   }

   private void renderInput(MenuDrawContext var1, BoundsData var2, float var3, ZenithStyle var4, String var5) {
      Font var6 = Fonts.NEW_MEDIUM.getFont(4.8F);
      var1.drawRoundedRect(
         var2.getFloatValue(),
         var2.getFloatValue2(),
         var2.width(),
         var2.height(),
         ProxyDotRadiusData.method8069(3.0F),
         var4.getSurfaceDisableBackground().getZenithColor().method7294(var3 * (this.inputFocused ? 1.0F : 0.55F))
      );
      boolean var7 = this.input.isEmpty();
      String var8;
      if (var7 && !this.inputFocused) {
         var8 = var6.clip(var5, var2.width() - 12.0F);
      } else {
         String var9 = this.input.toString();
         String var10 = this.inputFocused && System.currentTimeMillis() / 530L % 2L == 0L ? "▏" : "";

         while (var6.width("›  " + var9 + var10) > var2.width() - 12.0F && var9.length() > 1) {
            var9 = var9.substring(1);
         }

         var8 = "›  " + var9 + var10;
      }

      var1.drawText(
         var6,
         var8,
         var2.getFloatValue() + 6.0F,
         var2.getFloatValue2() + (var2.height() - var6.height()) / 2.0F,
         (var7 && !this.inputFocused ? var4.getTextTertiary() : var4.getTextEnable()).getZenithColor().method7294(var3)
      );
   }

   private void runInput() {
      Storage var1 = host();
      String var2 = this.input.toString().trim();
      if (var1 != null && !var2.isEmpty()) {
         this.history.add(var2);
         this.historyIndex = -1;
         this.input.setLength(0);
         this.evalLines.add("> " + var2);
         Script var3 = this.workspace != null && this.workspace.isRunning() ? this.workspace : null;

         try {
            this.evalLines.add("  " + var1.method1341(var3, var2));
         } catch (Throwable var5) {
            this.evalLines.add("! " + Storage.method1325(var5));
         }

         while (this.evalLines.size() > 200) {
            this.evalLines.remove(0);
         }

         this.consoleScroll = 0.0F;
         if (this.workspace == null) {
            this.invalidate();
            this.scrollToEnd();
         }
      }
   }

   @Override
   public boolean pressFeedback() {
      return true;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      return this.handleClick(var1, var3, var5);
   }

   private boolean handleClick(double var1, double var3, Enum3 var5) {
      this.lastClickX = var1;
      this.lastClickY = var3;
      if (this.configsTab()) {
         return this.configs.onMouseClicked(var1, var3, var5);
      } else if (this.workspace == null) {
         if (this.storeTab()) {
            this.market.mouse(var1, var3);
            BoundsData var12 = this.mainHeaderBounds();
            if (var12 != null && var12.contains(var1, var3) && this.market.clickHeader()) {
               return true;
            }

            if (this.market.clickRedeem(var1, var3)) {
               return true;
            }
         }

         if (this.getActiveTab() == 3) {
            BoundsData var13 = this.mainHeaderBounds();
            if (var13 != null && var13.contains(var1, var3) && this.ai.clickHeader(var1, var3)) {
               return true;
            }

            this.ai.mouse(var1, var3);
            this.ai.blurAll(var1, var3);
         }

         if (this.storeTab() && this.settingsScript != null) {
            BoundsData var14 = this.asideViewportOrNull();
            if (var14 != null && var14.contains(var1, var3)) {
               this.info().mouseClicked(var1, var3);
               return true;
            }
         }

         if (this.storeTab() && var5 == Enum3.RIGHT) {
            BoundsData var15 = this.mainViewport();
            if (var15 == null || var15.contains(var1, var3)) {
               for (Object[] var23 : new ArrayList<>(this.homeHits)) {
                  if ("open".equals(var23[1]) && ((BoundsData)var23[0]).contains(var1, var3)) {
                     this.openSettings((Script)var23[2]);
                     return true;
                  }
               }
            }
         }

         if (this.storeTab() && var5 == Enum3.LEFT) {
            BoundsData var16 = this.mainViewport();
            if (var16 == null || var16.contains(var1, var3)) {
               for (Object[] var24 : new ArrayList<>(this.homeHits)) {
                  if (((BoundsData)var24[0]).contains(var1, var3) && this.handleHome(var24)) {
                     return true;
                  }
               }
            }
         }

         BoundsData var17 = this.footerBounds();
         if (this.inputFocused && (var17 == null || !var17.contains(var1, var3))) {
            this.inputFocused = false;
         }

         return super.onMouseClicked(var1, var3, var5);
      } else if (this.leftBounds != null && this.leftBounds.contains(var1, var3)) {
         if (this.backRect != null && this.backRect.contains(var1, var3)) {
            this.closeWorkspace();
            return true;
         } else if (this.infoRect != null && this.infoRect.contains(var1, var3)) {
            if (!this.infoPage) {
               this.showInfo();
            }

            return true;
         } else if (this.newFileRect != null && this.newFileRect.contains(var1, var3)) {
            this.newFile();
            return true;
         } else {
            for (Object[] var19 : this.fileRects) {
               if (((BoundsData)var19[0]).contains(var1, var3)) {
                  Path var22 = (Path)var19[1];
                  if (this.infoPage) {
                     this.showCode();
                     if (var22.equals(this.file)) {
                        return true;
                     }
                  }

                  if (!var22.equals(this.file)) {
                     if (this.code.isDirty()) {
                        this.save();
                     }

                     this.openFile(var22);
                  }

                  return true;
               }
            }

            return true;
         }
      } else if (this.infoPage) {
         this.info().mouseClicked(var1, var3);
         return true;
      } else if (this.inputBounds != null && this.inputBounds.contains(var1, var3)) {
         this.inputFocused = true;
         this.code.setFocused(false);
         return true;
      } else {
         this.inputFocused = false;
         if (this.inspectorBounds != null && this.inspectorBounds.contains(var1, var3)) {
            this.code.setFocused(false);

            for (List var7 : this.moduleRows.values()) {
               for (Object var9_o : (Iterable<?>)(Object)(var7)) {
                  InspectorRow var9 = (InspectorRow)var9_o;
                  if (var9.isVisible() && var9.onMouseClicked(var1, var3, var5)) {
                     this.activeRow = var9;
                     return true;
                  }
               }
            }

            for (Object[] var18 : this.inspectorHits) {
               if (((BoundsData)var18[0]).contains(var1, var3)) {
                  ((Runnable)var18[1]).run();
                  return true;
               }
            }

            return true;
         } else if (this.codeBounds != null && this.codeBounds.contains(var1, var3)) {
            return this.code.mouseClicked(var1, var3, var5 == Enum3.LEFT ? 0 : 1);
         } else {
            this.code.setFocused(false);
            return this.consoleBounds != null && this.consoleBounds.contains(var1, var3);
         }
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      if (this.configsTab()) {
         return this.configs.onMouseReleased(var1, var3, var5);
      } else if (this.workspace == null) {
         return super.onMouseReleased(var1, var3, var5);
      } else {
         this.code.mouseReleased();

         for (List var7 : this.moduleRows.values()) {
            for (Object var9_o : (Iterable<?>)(Object)(var7)) {
               InspectorRow var9 = (InspectorRow)var9_o;
               var9.onMouseReleased(var1, var3, var5);
            }
         }

         return true;
      }
   }

   @Override
   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.configsTab()) {
         return this.configs.onMouseDragged(var1, var3, var5, var6, var8);
      } else if (this.workspace == null) {
         return super.onMouseDragged(var1, var3, var5, var6, var8);
      } else {
         for (List var11 : this.moduleRows.values()) {
            for (Object var13_o : (Iterable<?>)(Object)(var11)) {
               InspectorRow var13 = (InspectorRow)var13_o;
               if (var13.onMouseDragged(var1, var3)) {
                  return true;
               }
            }
         }

         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.configsTab()) {
         return this.configs.mouseScrolled(var1, var3, var5, var7);
      } else if (this.workspace == null) {
         if (this.settingsScript != null) {
            BoundsData var9 = this.asideViewportOrNull();
            if (var9 != null && var9.contains(var1, var3)) {
               this.info().mouseScrolled(var1, var3, var7);
               return true;
            }
         }

         return super.mouseScrolled(var1, var3, var5, var7);
      } else if (this.infoPage) {
         this.info().mouseScrolled(var1, var3, var7);
         return true;
      } else if (this.code.mouseScrolled(var1, var3, var7)) {
         return true;
      } else if (this.inspectorBounds != null && this.inspectorBounds.contains(var1, var3)) {
         this.inspectorScrollTarget += (float)var7 * 18.0F;
         return true;
      } else {
         if (this.consoleBounds != null && this.consoleBounds.contains(var1, var3)) {
            this.consoleScroll += (float)var7;
         }

         return true;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.configsTab() && !this.inputFocused) {
         return this.configs.keyPressed(var1, var2, var3);
      } else {
         boolean var4 = (var3 & 2) != 0;
         if (this.inputFocused) {
            switch (var1) {
               case 86:
                  if (var4) {
                     String var5 = MinecraftClient.getInstance().keyboard.getClipboard();
                     if (var5 != null) {
                        this.input.append(var5.replace("\n", " "));
                     }
                  }
                  break;
               case 256:
                  this.inputFocused = false;
                  break;
               case 257:
               case 335:
                  this.runInput();
                  break;
               case 259:
                  if (!this.input.isEmpty()) {
                     this.input.setLength(this.input.length() - 1);
                  }
                  break;
               case 264:
                  if (this.historyIndex >= 0) {
                     this.historyIndex++;
                     this.input.setLength(0);
                     if (this.historyIndex < this.history.size()) {
                        this.input.append(this.history.get(this.historyIndex));
                     } else {
                        this.historyIndex = -1;
                     }
                  }
                  break;
               case 265:
                  if (!this.history.isEmpty()) {
                     this.historyIndex = this.historyIndex < 0 ? this.history.size() - 1 : Math.max(0, this.historyIndex - 1);
                     this.input.setLength(0);
                     this.input.append(this.history.get(this.historyIndex));
                  }
            }

            return true;
         } else if (this.workspace == null) {
            if (this.getActiveTab() == 3 && this.ai.keyPressed(var1, var3)) {
               return true;
            } else if (this.storeTab() && this.market.redeem.isFocused()) {
               if (var1 != 257 && var1 != 335) {
                  this.market.redeem.keyPressed(var1, var3);
               } else {
                  this.market.doRedeem();
               }

               return true;
            } else {
               if (this.settingsScript != null) {
                  if (var4 && var1 == 83) {
                     this.info().apply();
                     return true;
                  }

                  if (this.info().keyPressed(var1, var3)) {
                     return true;
                  }

                  if (var1 == 256) {
                     this.closeSettings();
                     return true;
                  }
               }

               return super.keyPressed(var1, var2, var3);
            }
         } else if (var4 && var1 == 83) {
            if (this.infoPage) {
               this.info().apply();
            } else {
               this.save();
            }

            return true;
         } else if (this.infoPage) {
            if (this.info().keyPressed(var1, var3)) {
               return true;
            } else if (var1 == 256) {
               this.showCode();
               return true;
            } else {
               return var1 < 290;
            }
         } else if (this.activeRow != null && this.activeRow.isEditing()) {
            this.activeRow.keyPressed(var1, var2, var3);
            return true;
         } else if (var1 == 256) {
            if (this.code.hasCompletions()) {
               this.code.closeCompletions();
            } else {
               this.closeWorkspace();
            }

            return true;
         } else {
            if (!this.code.isFocused()) {
               this.code.setFocused(true);
            }

            return this.code.keyPressed(var1, var3);
         }
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.configsTab() && !this.inputFocused) {
         return this.configs.charTyped(var1, var2);
      } else if (this.inputFocused) {
         if (var1 >= ' ' && this.input.length() < 2000) {
            this.input.append(var1);
         }

         return true;
      } else if (this.workspace == null) {
         if (this.getActiveTab() == 3 && this.ai.charTyped(var1)) {
            return true;
         } else if (this.storeTab() && this.market.redeem.charTyped(var1)) {
            return true;
         } else {
            return this.settingsScript != null && this.info().charTyped(var1) ? true : super.charTyped(var1, var2);
         }
      } else if (this.infoPage) {
         this.info().charTyped(var1);
         return true;
      } else if (this.activeRow != null && this.activeRow.isEditing()) {
         this.activeRow.charTyped(var1, var2);
         return true;
      } else {
         this.code.setFocused(true);
         return this.code.charTyped(var1);
      }
   }

   @Override
   public void close() {
      this.configs.close();
      this.ai.commitFields();
      if (this.workspace != null && this.code != null && this.code.isDirty()) {
         this.save();
      }

      super.close();
   }

   private static final class FileCount {
      private final long modified;
      private final int files;
      private final long countedAt;

      public long modified() {
         return this.modified;
      }

      public int files() {
         return this.files;
      }

      public long countedAt() {
         return this.countedAt;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof FileCount other)) return false;
         return java.util.Objects.equals(this.modified, other.modified) && java.util.Objects.equals(this.files, other.files) && java.util.Objects.equals(this.countedAt, other.countedAt);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(modified, files, countedAt);
      }

      @Override
      public String toString() {
         return "FileCount[" + "modified=" + this.modified + ", " + "files=" + this.files + ", " + "countedAt=" + this.countedAt + "]";
      }

      private FileCount(long var1, int var3, long var4) {
         this.modified = var1;
         this.files = var3;
         this.countedAt = var4;
      }
   }

   private static final class RowInfo {
      private final Script script;
      private final int files;
      private final long modified;

      public Script script() {
         return this.script;
      }

      public int files() {
         return this.files;
      }

      public long modified() {
         return this.modified;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof RowInfo other)) return false;
         return java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.files, other.files) && java.util.Objects.equals(this.modified, other.modified);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(script, files, modified);
      }

      @Override
      public String toString() {
         return "RowInfo[" + "script=" + this.script + ", " + "files=" + this.files + ", " + "modified=" + this.modified + "]";
      }

      private RowInfo(Script var1, int var2, long var3) {
         this.script = var1;
         this.files = var2;
         this.modified = var3;
      }
   }
}

package org.zenith.client.screens.nlgui.panel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.cloud.CloudLibrary;
import org.zenith.client.screens.nlgui.cloud.CloudPreviewTextureCache;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.screens.scripts.ScriptInfoPage;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptMarketTab {
   public static final int VIEW_LOCAL = 0;
   public static final int VIEW_CATALOG = 1;
   public static final int VIEW_LIBRARY = 2;
   public static final int VIEW_MINE = 3;
   private static final String[] VISIBILITY = new String[]{"PUBLIC", "FRIENDS", "CODE", "PRIVATE"};
   private static final String[] VISIBILITY_KEYS = new String[]{
      "gui.scripts.market.vis.public", "gui.scripts.market.vis.friends", "gui.scripts.market.vis.code", "gui.scripts.market.vis.private"
   };
   public static final float ROW = 36.0F;
   public static final float GAP = 4.0F;
   public static final float CARD_PAD = 7.0F;
   public static final float AVATAR = 22.0F;
   public static final float PILL_HEIGHT = 14.0F;
   public static final float PILL_MIN = 44.0F;
   private static final float TITLE_GAP = 2.0F;
   private static final float NOTE_GAP = 2.5F;
   private static final int MAX_TAGS = 3;
   private static final float LOCAL_ROW = 24.0F;
   private static final float INSET = 8.0F;
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor LIKE = new ZenithColor(255, 107, 129);
   private static final long REFRESH_MS = 45000L;
   private final ScriptsSectionPanel panel;
   public static final int CATEGORY_ALL = 0;
   public static final int CATEGORY_SCRIPTS = 1;
   public static final int CATEGORY_CONFIGS = 2;
   private final ChipStrip views = new ChipStrip("gui.scripts.market.view.catalog", "gui.scripts.market.view.library", "gui.scripts.market.view.mine");
   private final ChipStrip categories = new ChipStrip("gui.market.category.all", "gui.market.category.scripts", "gui.market.category.configs");
   private int category;
   private final CloudLibrary taken = new CloudLibrary();
   private final ChipStrip sorts = new ChipStrip("gui.scripts.market.sort.fresh", "gui.scripts.market.sort.top", "gui.scripts.market.sort.name");
   private final ChipStrip publishVisibility = new ChipStrip(VISIBILITY_KEYS);
   private final ChipStrip entryVisibility = new ChipStrip(VISIBILITY_KEYS);
   private final List<StoreApi.Data2> catalog = new CopyOnWriteArrayList<>();
   private final List<StoreApi.Data2> library = new CopyOnWriteArrayList<>();
   private final List<StoreApi.Data2> mine = new CopyOnWriteArrayList<>();
   private final Set<UUID> busy = ConcurrentHashMap.newKeySet();
   private final Set<String> publishing = ConcurrentHashMap.newKeySet();
   private final Map<UUID, List<StoreApi.Data>> codes = new ConcurrentHashMap<>();
   private final Map<Object, Boolean> pillHover = new ConcurrentHashMap<>();
   private final Map<UUID, Long> pressedAt = new ConcurrentHashMap<>();
   public final FormField redeem = new FormField(48, false);
   private BoundsData redeemButton;
   private BoundsData rightButton;
   private static final String[] SORT_KEYS = new String[]{"gui.scripts.market.sort.fresh", "gui.scripts.market.sort.top", "gui.scripts.market.sort.name"};
   private Map<UUID, Script> installed = Map.of();
   private int view;
   private int sort;
   private int publishVis;
   private UUID selected;
   private final AtomicInteger revision = new AtomicInteger();
   private volatile long fetchedAt;
   private final AtomicInteger loading = new AtomicInteger();
   private volatile String status = "";
   private volatile boolean statusError;
   private volatile long statusAt;
   private String armed = "";
   private long armedAt;
   private final Map<UUID, ScriptMarketTab.Presence> presence = new ConcurrentHashMap<>();
   private double lastX;
   private double lastY;
   private static final String[] HERO_TITLES = new String[]{
      "gui.scripts.market.hero.local", "gui.scripts.market.hero.catalog", "gui.scripts.market.hero.library", "gui.scripts.market.hero.mine"
   };

   private float rightWidth(Font var1) {
      float var2 = Math.max(var1.width("+  " + t("gui.scripts.home.new")), var1.width(t("gui.scripts.home.cancel")));

      for (String var6 : SORT_KEYS) {
         var2 = Math.max(var2, var1.width(t(var6)));
      }

      return var2 + 16.0F;
   }

   public ScriptMarketTab(ScriptsSectionPanel var1) {
      this.panel = var1;
   }

   public int revision() {
      return this.revision.get() * 31
         + this.loading.get() * 7
         + this.busy.size() * 3
         + this.publishing.size()
         + this.view * 101
         + this.category * 1009
         + (this.selected == null ? 0 : this.selected.hashCode());
   }

   private static String t(String var0) {
      return SectionPanel.t(var0);
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private void report(String var1, boolean var2) {
      this.status = var1 == null ? "" : var1;
      this.statusError = var2;
      this.statusAt = System.currentTimeMillis();
      this.revision.incrementAndGet();
   }

   private static String reason(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null && var1.getCause() != var1) {
         var1 = var1.getCause();
      }

      String var2 = var1.getMessage();
      return var2 != null && !var2.isBlank() ? var2 : var1.getClass().getSimpleName();
   }

   public void fetch(boolean var1) {
      long var2 = System.currentTimeMillis();
      if (var1 || var2 - this.fetchedAt >= 45000L) {
         StoreApi var4 = store();
         if (var4 != null) {
            this.fetchedAt = var2;
            this.loading.addAndGet(6);

            for (String var6 : List.of("script", "config")) {
               var4.method1969("PUBLIC", var6).whenComplete((var2x, var3) -> this.accept(this.catalog, var6, (List<StoreApi.Data2>)var2x, var3));
               var4.method1969("LIBRARY", var6).whenComplete((var2x, var3) -> this.accept(this.library, var6, (List<StoreApi.Data2>)var2x, var3));
               var4.method1968(var6).whenComplete((var2x, var3) -> this.accept(this.mine, var6, (List<StoreApi.Data2>)var2x, var3));
            }
         }
      }
   }

   private void accept(List<StoreApi.Data2> var1, String var2, List<StoreApi.Data2> var3, Throwable var4) {
      this.loading.updateAndGet(var0 -> Math.max(0, var0 - 1));
      if (var4 != null) {
         this.report(t("gui.scripts.market.status.offline") + " " + reason(var4), true);
      } else {
         ArrayList var5 = new ArrayList();

         for (StoreApi.Data2 var7 : var3) {
            if (isConfig(var7) || OriginUtil.method6918(var7)) {
               var5.add(var7);
            }
         }

         synchronized (var1) {
            var1.removeIf(var1x -> var2.equalsIgnoreCase(var1x.kind()));
            var1.addAll(var5);
         }

         this.revision.incrementAndGet();
      }
   }

   public static boolean isConfig(StoreApi.Data2 var0) {
      return "config".equalsIgnoreCase(var0.kind());
   }

   private boolean inCategory(StoreApi.Data2 var1) {
      return switch (this.category) {
         case 1 -> !isConfig(var1);
         case 2 -> isConfig(var1);
         default -> true;
      };
   }

   private static File configFile(StoreApi.Data2 var0) {
      return CloudConfigsSectionPanel.targetFile(var0);
   }

   private boolean configPresent(StoreApi.Data2 var1) {
      return configFile(var1).isFile();
   }

   private boolean configPresentCached(StoreApi.Data2 var1) {
      long var2 = System.currentTimeMillis();
      ScriptMarketTab.Presence var4 = this.presence.get(var1.getUUID());
      if (var4 != null && var2 - var4.checkedAt() < 1000L && Objects.equals(var4.name(), var1.name())) {
         return var4.present();
      } else {
         boolean var5 = this.configPresent(var1);
         this.presence.put(var1.getUUID(), new ScriptMarketTab.Presence(var1.name(), var5, var2));
         return var5;
      }
   }

   public void syncMode(boolean var1) {
      if (var1 && this.view == 0) {
         this.view = 1;
         this.selected = null;
      } else if (!var1 && this.view != 0) {
         this.view = 0;
         this.selected = null;
      }
   }

   public void openCategory(int var1) {
      this.category = Math.max(0, Math.min(2, var1));
      if (this.view == 0) {
         this.view = 1;
      }

      this.selected = null;
      this.revision.incrementAndGet();
   }

   private void replace(StoreApi.Data2 var1) {
      for (List var3 : List.of(this.catalog, this.library, this.mine)) {
         for (int var4 = 0; var4 < var3.size(); var4++) {
            if (((StoreApi.Data2)var3.get(var4)).getUUID().equals(var1.getUUID())) {
               var3.set(var4, var1);
            }
         }
      }

      this.revision.incrementAndGet();
   }

   private StoreApi.Data2 entry(UUID var1) {
      if (var1 == null) {
         return null;
      } else {
         for (List var3 : List.of(this.mine, this.library, this.catalog)) {
            for (Object var5_o : (Iterable<?>)(Object)(var3)) {
               StoreApi.Data2 var5 = (StoreApi.Data2)var5_o;
               if (var5.getUUID().equals(var1)) {
                  return var5;
               }
            }
         }

         return null;
      }
   }

   private boolean armed(String var1) {
      if (this.armed.equals(var1) && System.currentTimeMillis() - this.armedAt < 3000L) {
         this.armed = "";
         return true;
      } else {
         this.armed = var1;
         this.armedAt = System.currentTimeMillis();
         this.report(t("gui.scripts.market.status.confirm"), false);
         return false;
      }
   }

   public float headerHeight() {
      float var1 = 11.0F + GuiStyle.PADDING.intValue() * 2.0F;
      return this.view == 0 ? var1 : var1 + 11.0F + GuiStyle.PADDING.intValue();
   }

   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = var5 + GuiStyle.PADDING.intValue();
      Font var10 = Fonts.NEW_MEDIUM.getFont(4.6F);
      float var11 = this.rightWidth(var10);
      ZenithColor var12 = var8.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData var13 = ProxyDotRadiusData.method8069(5.5F);
      this.rightButton = new BoundsData(var4 + var6 - 8.0F - var11, var9, var11, 11.0F);
      float var14 = this.panel.hot("hdr:right", this.rightButton.contains(var2, var3));
      if (this.view == 0) {
         boolean var15 = this.panel.creatorOpen();
         String var16 = var15 ? t("gui.scripts.home.cancel") : "+  " + t("gui.scripts.home.new");
         var1.drawRoundedRect(
            this.rightButton.getFloatValue(),
            var9,
            var11,
            11.0F,
            var13,
            (var15 ? var8.getDisableActiveBg().getZenithColor().method7294(0.6F + 0.4F * var14) : var12.method7292(0.18F + 0.16F * var14)).method7294(var7)
         );
         var1.drawText(
            var10,
            var16,
            this.rightButton.getFloatValue() + (var11 - var10.width(var16)) / 2.0F,
            var9 + (11.0F - var10.height()) / 2.0F,
            (var15 ? var8.getTextSecondary().getZenithColor() : var12.method7295(ZenithColor.INSTANCE, 0.25F + 0.4F * var14)).method7294(var7)
         );
         if (this.rightButton.contains(var2, var3)) {
            HoverInfo.request(t("gui.scripts.home.new"), t("gui.scripts.home.new.desc"));
         }
      } else {
         String var17 = t(SORT_KEYS[this.sort]);
         var1.drawRoundedRect(
            this.rightButton.getFloatValue(), var9, var11, 11.0F, var13, var8.getDisableActiveBg().getZenithColor().method7294(var7 * (0.55F + 0.45F * var14))
         );
         var1.drawText(
            var10,
            var17,
            this.rightButton.getFloatValue() + (var11 - var10.width(var17)) / 2.0F,
            var9 + (11.0F - var10.height()) / 2.0F,
            var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), 0.4F + 0.6F * var14).method7294(var7)
         );
         if (this.rightButton.contains(var2, var3)) {
            HoverInfo.request(t("gui.scripts.market.sort"), t("gui.scripts.market.sort.desc"));
         }
      }

      if (this.view == 0) {
         Font var18 = Fonts.NEW_MEDIUM.getFont(5.0F);
         String var20 = t("gui.scripts.home.title").replace("{n}", String.valueOf(this.installed.size()));
         var1.drawText(var18, var20, var4 + 8.0F, var9 + (11.0F - var18.height()) / 2.0F, var8.getTextSecondary().getZenithColor().method7294(var7));
      } else {
         this.views.render(var1, var2, var3, var4 + 8.0F, var9, var6 - 16.0F - var11 - 8.0F, this.view - 1, var7, var8);
         this.categories.render(var1, var2, var3, var4 + 8.0F, var9 + 11.0F + GuiStyle.PADDING.intValue(), var6 - 16.0F, this.category, var7, var8);
         if (this.categories.hovered() >= 0) {
            HoverInfo.request(t("gui.market.category"), t("gui.market.category.desc"));
         }
      }

      if (this.loading.get() > 0 && this.view != 0) {
         float var19 = (float)(System.currentTimeMillis() % 900L) / 900.0F;
         var1.drawRoundedRect(
            var4 + 8.0F + (var6 - 16.0F - 30.0F) * var19,
            var5 + this.headerHeight() - 1.5F,
            30.0F,
            1.0F,
            ProxyDotRadiusData.method8069(0.5F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * 0.8F)
         );
      }

      if (this.view != 0 && this.views.hovered() >= 0) {
         HoverInfo.request(t("gui.scripts.market.views"), t("gui.scripts.market.views.desc"));
      }
   }

   public boolean clickHeader() {
      if (this.view != 0) {
         int var1 = this.categories.hovered();
         if (var1 >= 0) {
            if (var1 != this.category) {
               this.category = var1;
               this.selected = null;
               this.panel.resetScroll();
               this.panel.replayEntrance();
               this.panel.invalidate();
            }

            return true;
         }
      }

      int var2 = this.view == 0 ? -1 : this.views.hovered() + 1;
      if (var2 > 0) {
         if (var2 != this.view) {
            this.view = var2;
            this.selected = null;
            this.panel.closeSettingsIfOpen();
            this.panel.resetScroll();
            this.panel.resetAsideScroll();
            this.panel.replayEntrance();
            this.panel.invalidate();
         }

         return true;
      } else if (this.rightButton != null && this.rightButton.contains(this.lastX, this.lastY)) {
         if (this.view == 0) {
            this.panel.toggleCreator();
         } else {
            this.sort = (this.sort + 1) % SORT_KEYS.length;
            this.panel.replayEntrance();
            this.panel.invalidate();
         }

         return true;
      } else {
         return false;
      }
   }

   public void showLocal() {
      if (this.view != 0) {
         this.view = 0;
         this.selected = null;
         this.panel.invalidate();
      }
   }

   public void mouse(double var1, double var3) {
      this.lastX = var1;
      this.lastY = var3;
   }

   public void build(Storage var1) {
      this.fetch(false);
      this.installed = OriginUtil.method6925(var1);
      this.hero(var1);
      if (this.view == 0) {
         this.selected = null;
         this.panel.buildLocal(var1);
      } else {
         switch (this.view) {
            case 2:
               this.entries(var1, this.arrange(this.library), "gui.scripts.market.empty.library", "gui.scripts.market.empty.library.desc");
               break;
            case 3:
               this.entries(var1, this.arrange(this.mine), "gui.scripts.market.empty.mine", "gui.scripts.market.empty.mine.desc");
               break;
            default:
               this.entries(var1, this.arrange(this.catalog), "gui.scripts.market.empty.catalog", "gui.scripts.market.empty.catalog.desc");
         }

         StoreApi.Data2 var2 = this.entry(this.selected);
         if (var2 != null) {
            this.entryAside(var1, var2);
         } else {
            this.selected = null;
            this.overviewAside(var1);
         }
      }
   }

   private UUID idOf(Script var1) {
      for (Entry var3 : this.installed.entrySet()) {
         if (var3.getValue() == var1 || ((Script)var3.getValue()).getId().equals(var1.getId())) {
            return (UUID)var3.getKey();
         }
      }

      return null;
   }

   public StoreApi.Data2 updateFor(Storage var1, Script var2) {
      UUID var3 = this.idOf(var2);
      StoreApi.Data2 var4 = this.entry(var3);
      return var4 != null && !this.busy.contains(var3) && OriginUtil.method6927(var1, var4) ? var4 : null;
   }

   public boolean updating(Storage var1, Script var2) {
      UUID var3 = this.idOf(var2);
      return var3 != null && this.busy.contains(var3);
   }

   public int updatesCount(Storage var1) {
      int var2 = 0;

      for (Script var4 : var1.method1293()) {
         if (this.updateFor(var1, var4) != null) {
            var2++;
         }
      }

      return var2;
   }

   public void update(Storage var1, Script var2) {
      StoreApi.Data2 var3 = this.updateFor(var1, var2);
      if (var3 != null) {
         this.install(var1, var3);
      }
   }

   public void updateAll(Storage var1) {
      for (Script var3 : new ArrayList<>(var1.method1293())) {
         this.update(var1, var3);
      }
   }

   private List<StoreApi.Data2> arrange(List<StoreApi.Data2> var1) {
      String var2 = this.panel.searchQuery().toLowerCase(Locale.ROOT).strip();
      ArrayList<StoreApi.Data2> var3 = new ArrayList<>();

      for (StoreApi.Data2 var5 : var1) {
         if (this.inCategory(var5)
            && (
               var2.isEmpty()
                  || var5.name().toLowerCase(Locale.ROOT).contains(var2)
                  || var5.description().toLowerCase(Locale.ROOT).contains(var2)
                  || var5.getText8().toLowerCase(Locale.ROOT).contains(var2)
                  || String.join(",", OriginUtil.method6916(var5).getItems()).toLowerCase(Locale.ROOT).contains(var2)
            )) {
            var3.add(var5);
         }
      }
      Comparator<StoreApi.Data2> var6 = switch (this.sort) {
         case 1 -> Comparator.comparingInt(StoreApi.Data2::getIntValue)
            .reversed()
            .thenComparing(Comparator.comparingLong(StoreApi.Data2::getLongValue4).reversed());
         case 2 -> Comparator.comparing(var0 -> var0.name().toLowerCase(Locale.ROOT));
         default -> Comparator.comparingLong(StoreApi.Data2::getLongValue4).reversed();
      };
      var3.sort(var6);
      return var3;
   }

   private String[][] heroStats(Storage var1) {
      if (this.view == 0) {
         var var7 = var1.method1293();
         long var8 = var7.stream().filter(Script::isRunning).count();
         int var9 = var7.stream().mapToInt(var0 -> var0.getItems3().size()).sum();
         return new String[][]{
            {var8 + "/" + var7.size(), t("gui.scripts.market.hero.running")},
            {String.valueOf(var9), t("gui.scripts.market.hero.modules")},
            {String.valueOf(this.updatesCount(var1)), t("gui.scripts.market.hero.updates")}
         };
      } else {
         int var2 = 0;
         int var3 = 0;
         HashSet var4 = new HashSet();

         for (StoreApi.Data2 var6 : this.catalog) {
            if (this.inCategory(var6)) {
               var3++;
               var2 += var6.getIntValue();
               var4.add(var6.getLongValue5());
            }
         }

         return new String[][]{
            {
                  String.valueOf(var3),
                  t(this.category == 2 ? "gui.market.hero.configs" : (this.category == 1 ? "gui.scripts.market.hero.scripts" : "gui.market.hero.items"))
            },
            {String.valueOf(var4.size()), t("gui.scripts.market.hero.authors")},
            {String.valueOf(var2), t("gui.scripts.market.hero.likes")}
         };
      }
   }

   private String heroNote(Storage var1) {
      if (this.view != 0) {
         return t(HERO_TITLES[this.view] + ".desc");
      } else {
         var var2 = var1.method1293();
         long var3 = var2.stream().filter(var0 -> var0.getType2() == Script.Type2.FAILED).count();
         return var3 > 0L ? t("gui.scripts.home.failed").replace("{n}", String.valueOf(var3)) : t(HERO_TITLES[this.view] + ".desc");
      }
   }

   private void hero(Storage var1) {
      this.panel
         .custom(
            50.0F,
            (var2, var3, var4, var5, var6, var7, var8, var9) -> {
               float var10 = var5 + 8.0F;
               float var11 = var7 - 16.0F;
               float var12 = var6 + 2.0F;
               float var13 = 43.0F;
               ZenithColor var14 = var9.getPrimaryColor().getZenithColor();
               ProxyDotRadiusData var15 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.9F);
               var2.drawRoundedRect(var10, var12, var11, var13, var15, ScriptsSectionPanel.cardFill(var9, 0.2F).method7294(var8));
               ScriptsSectionPanel.flowGlow(var2, var10, var12, var11, var13, var15, var14, 0.45F, var8);
               var2.drawRoundedBorder(var10, var12, var11, var13, 0.2F, var15, var14.method7292(0.28F).method7294(var8));
               Font var16 = Fonts.NEW_SEMIBOLD.getFont(6.4F);
               Font var17 = Fonts.NEW_REGULAR.getFont(4.3F);
               Font var18 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
               Font var19 = Fonts.NEW_MEDIUM.getFont(3.8F);
               var2.drawText(var16, t(HERO_TITLES[this.view]), var10 + 9.0F, var12 + 8.0F, var9.getTextEnable().getZenithColor().method7294(var8));
               String var20 = this.heroNote(var1);
               boolean var21 = this.view == 0 && var1.method1293().stream().anyMatch(var0 -> var0.getType2() == Script.Type2.FAILED);
               String[][] var22 = this.heroStats(var1);
               float var23 = 0.0F;

               for (String[] var27 : var22) {
                  var23 += Math.max(var18.width(var27[0]), var19.width(var27[1])) + 12.0F;
               }

               List<String> var29 = SectionPanel.wrapText(var20, var17, var11 - 18.0F - var23, 2);
               float var30 = var12 + 11.0F + var16.height();

               for (Object var34_o : (Iterable<?>)(Object)(var29)) {
                  String var34 = (String)var34_o;
                  var2.drawText(
                     var17, var34, var10 + 9.0F, var30, (var21 ? new ZenithColor(247, 206, 59) : var9.getTextTertiary().getZenithColor()).method7294(var8)
                  );
                  var30 += var17.height() + 1.5F;
               }

               float var32 = var10 + var11 - 9.0F;

               for (int var35 = var22.length - 1; var35 >= 0; var35--) {
                  float var28 = Math.max(var18.width(var22[var35][0]), var19.width(var22[var35][1]));
                  var32 -= var28;
                  var2.drawText(
                     var18,
                     var22[var35][0],
                     var32 + (var28 - var18.width(var22[var35][0])) / 2.0F,
                     var12 + 10.0F,
                     var9.getTextEnable().getZenithColor().method7294(var8)
                  );
                  var2.drawText(
                     var19,
                     var22[var35][1],
                     var32 + (var28 - var19.width(var22[var35][1])) / 2.0F,
                     var12 + 13.0F + var18.height(),
                     var9.getTextTertiary().getZenithColor().method7294(var8)
                  );
                  var32 -= 12.0F;
               }
            }
         );
   }

   private void skeleton(int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         int var3 = var2;
         this.panel
            .custom(
               40.0F,
               this.panel
                  .entering(
                     var2 + 1,
                     (var1x, var2x, var3x, var4, var5, var6, var7, var8) -> {
                        BoundsData var9 = new BoundsData(var4 + 8.0F, var5, var6 - 16.0F, 36.0F);
                        ScriptsSectionPanel.card(var1x, var8, var9.getFloatValue(), var9.getFloatValue2(), var9.width(), var9.height(), 0.0F, var7);
                        float var10 = (float)((System.currentTimeMillis() + var3 * 140L) % 1400L) / 1400.0F;
                        float var11 = 0.5F + 0.5F * (float)Math.sin(var10 * Math.PI * 2.0);
                        ZenithColor var12 = var8.getDisableActiveBg().getZenithColor().method7294(0.5F + 0.5F * var11);
                        var1x.drawRoundedRect(
                           var9.getFloatValue() + 7.0F,
                           var9.getFloatValue2() + 7.0F,
                           22.0F,
                           22.0F,
                           ProxyDotRadiusData.method8069(6.6000004F),
                           var12.method7294(var7)
                        );
                        float var13 = var9.getFloatValue() + 7.0F + 22.0F + 7.0F;
                        float var14 = var9.getFloatValue2() + 18.0F;
                        var1x.drawRoundedRect(var13, var14 - 9.0F, var9.width() * 0.3F, 5.0F, ProxyDotRadiusData.method8069(2.5F), var12.method7294(var7));
                        var1x.drawRoundedRect(
                           var13, var14 - 1.5F, var9.width() * 0.48F, 4.0F, ProxyDotRadiusData.method8069(2.0F), var12.method7294(var7 * 0.7F)
                        );
                        var1x.drawRoundedRect(
                           var13, var14 + 5.5F, var9.width() * 0.24F, 4.0F, ProxyDotRadiusData.method8069(2.0F), var12.method7294(var7 * 0.5F)
                        );
                        var1x.drawRoundedRect(
                           var9.getFloatValue() + var9.width() - 7.0F - 44.0F,
                           var14 - 7.0F,
                           44.0F,
                           14.0F,
                           ProxyDotRadiusData.method8069(7.0F),
                           var12.method7294(var7 * 0.6F)
                        );
                     }
                  )
            );
      }
   }

   private void empty(String var1, String var2) {
      if (this.loading.get() > 0) {
         this.skeleton(4);
      } else {
         this.panel.custom(76.0F, (var3, var4, var5, var6, var7, var8, var9, var10) -> {
            Font var11 = Fonts.NEW_SEMIBOLD.getFont(5.6F);
            Font var12 = Fonts.NEW_REGULAR.getFont(4.4F);
            float var13 = var7 + 16.0F;
            String var14 = t(this.loading.get() > 0 ? "gui.scripts.market.loading" : var1);
            var3.drawText(var11, var14, var6 + (var8 - var11.width(var14)) / 2.0F, var13, var10.getTextEnable().getZenithColor().method7294(var9));
            List<String> var15 = SectionPanel.wrapText(t(var2), var12, var8 - 48.0F, 3);
            float var16 = var13 + var11.height() + 6.0F;

            for (Object var18_o : (Iterable<?>)(Object)(var15)) {
               String var18 = (String)var18_o;
               var3.drawText(var12, var18, var6 + (var8 - var12.width(var18)) / 2.0F, var16, var10.getTextTertiary().getZenithColor().method7294(var9));
               var16 += var12.height() + 2.0F;
            }
         });
      }
   }

   private void entries(Storage var1, List<StoreApi.Data2> var2, String var3, String var4) {
      if (var2.isEmpty()) {
         this.empty(var3, var4);
      } else {
         int var5 = 1;

         for (StoreApi.Data2 var7 : var2) {
            UUID var8 = var7.getUUID();
            this.panel.custom(40.0F, this.panel.entering(var5++, (var3x, var4x, var5x, var6, var7x, var8x, var9, var10) -> {
               StoreApi.Data2 var11 = this.entry(var8);
               if (var11 != null) {
                  this.renderEntry(var3x, var4x, var5x, var6, var7x, var8x, var9, var10, var1, var11);
               }
            }), () -> {
               StoreApi.Data2 var3x = this.entry(var8);
               if (var3x != null) {
                  if (Boolean.TRUE.equals(this.pillHover.get(var8))) {
                     this.pressedAt.put(var8, System.currentTimeMillis());
                     this.primary(var1, var3x);
                  } else {
                     this.selected = var8.equals(this.selected) ? null : var8;
                     if (this.selected != null && var3x.mine()) {
                        this.loadCodes(var3x);
                     }

                     this.panel.invalidate();
                  }
               }
            }, () -> var8.equals(this.selected), () -> {
               this.selected = var8;
               this.panel.invalidate();
            });
         }
      }
   }

   private boolean needsPurchase(StoreApi.Data2 var1) {
      if (!var1.mine() && StoreApi.method1960(var1.getUUID()).method2009()) {
         for (StoreApi.Data2 var3 : this.library) {
            if (var3.getUUID().equals(var1.getUUID())) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void buy(StoreApi.Data2 var1) {
      Util.getOperatingSystem().open(RegionUtil.method2038().get(0) + "/market/" + var1.getUUID());
      this.report(t("gui.market.buy.opened"), false);
   }

   private String marketLabel(StoreApi.Data2 var1) {
      StoreApi.Data5 var2 = StoreApi.method1960(var1.getUUID());
      if (var2.pending()) {
         return t("gui.market.status.pending");
      } else if (var2.method2008()) {
         return t("gui.market.status.rejected");
      } else {
         return var2.method2007()
            ? t(var2.method2009() ? "gui.market.tag.paid" : "gui.market.tag.free").replace("{p}", String.valueOf(var2.getIntValue()))
            : t(VISIBILITY_KEYS[visibilityIndex(var1.getText7())]);
      }
   }

   private String pillText(StoreApi.Data2 var1, boolean var2, boolean var3) {
      if (this.busy.contains(var1.getUUID())) {
         return t("gui.scripts.market.busy");
      } else if (this.needsPurchase(var1)) {
         return t("gui.market.buy").replace("{p}", String.valueOf(StoreApi.method1960(var1.getUUID()).getIntValue()));
      } else if (isConfig(var1)) {
         return t(var2 ? "gui.market.config.redownload" : "gui.market.config.download");
      } else {
         Script var4 = this.installed.get(var1.getUUID());
         if (var4 == null) {
            return t(var1.mine() ? "gui.scripts.market.download" : "gui.scripts.market.install");
         } else {
            return var3 ? t("gui.scripts.market.update") : t("gui.scripts.market.open");
         }
      }
   }

   private void primary(Storage var1, StoreApi.Data2 var2) {
      if (!this.busy.contains(var2.getUUID())) {
         if (this.needsPurchase(var2)) {
            this.buy(var2);
         } else if (isConfig(var2)) {
            this.downloadConfig(var2);
         } else {
            Script var3 = this.installed.get(var2.getUUID());
            if (var3 != null && !OriginUtil.method6927(var1, var2)) {
               this.panel.openWorkspace(var3);
            } else {
               this.install(var1, var2);
            }
         }
      }
   }

   private void renderEntry(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, Storage var9, StoreApi.Data2 var10
   ) {
      BoundsData var11 = cardRect(var4, var5, var6);
      boolean var12 = var11.contains(var2, var3);
      boolean var13 = var10.getUUID().equals(this.selected);
      float var14 = this.panel.hot("m:" + var10.getUUID(), var12);
      float var15 = this.panel.hot("msel:" + var10.getUUID(), var13);
      float var16 = Smooth.press(this.pressedAt.getOrDefault(var10.getUUID(), 0L), 360L);
      OriginUtil.Data3 var17 = OriginUtil.method6916(var10);
      drawCard(var1, var8, var11, var14, var15, var16, tint(var10, var17), var7);
      this.avatar(var1, var10, var17, var11.getFloatValue() + 7.0F, cardAvatarY(var11), 22.0F, var7);
      boolean var18 = isConfig(var10);
      boolean var19 = var18 && this.configPresentCached(var10);
      boolean var20 = !var18 && this.installed.containsKey(var10.getUUID()) && OriginUtil.method6927(var9, var10);
      String var21 = this.pillText(var10, var19, var20);
      BoundsData var22 = pillRect(var11, var11.getFloatValue() + var11.width() - 7.0F, var21);
      boolean var23 = var22.contains(var2, var3);
      this.pillHover.put(var10.getUUID(), var23);
      float var24 = cardTextX(var11);
      float var25 = var22.getFloatValue() - 7.0F;
      ScriptMarketTab.CardLines var26 = cardLines(var11);
      drawTitleLine(
         var1,
         var8,
         var10.name(),
         var17.version().isBlank() ? "" : "v" + var17.version(),
         var10.getIntValue() <= 0 && !var10.isFlag3() ? "" : "♥ " + var10.getIntValue(),
         var10.isFlag3() ? LIKE : null,
         var24,
         var25,
         var26.title(),
         var7
      );
      drawDescription(var1, var8, var10.description(), null, var24, var25, var26.note(), var7);
      float var27 = drawTags(var1, var8, var17.getItems(), var24, var24, var25, var26.meta(), var7);
      ArrayList var28 = new ArrayList();
      if (this.category == 0) {
         var28.add(t(var18 ? "gui.market.kind.config" : "gui.market.kind.script"));
      }

      var28.add(var10.getText8().isBlank() ? "?" : var10.getText8());
      var28.add(size(var10.getLongValue()));
      String var29 = ScriptsSectionPanel.ago(var10.getLongValue4());
      if (!var29.isEmpty()) {
         var28.add(var29);
      }

      if (var10.mine()) {
         var28.add(this.marketLabel(var10));
      } else if (StoreApi.method1960(var10.getUUID()).method2009()) {
         var28.add(StoreApi.method1960(var10.getUUID()).getIntValue() + " ₽");
      }

      drawMeta(var1, String.join("  ·  ", var28), var8.getTextTertiary().getZenithColor(), var27, var24, var25, var26.meta(), var7);
      boolean var30 = var18 ? var19 : this.installed.containsKey(var10.getUUID());
      boolean var31 = var30 && !var18 && var20;
      drawPill(var1, var8, var22, var21, var30 && !var31, this.panel.hot("mp:" + var10.getUUID(), var23), this.busy.contains(var10.getUUID()), var7);
      if (var12 && !var23) {
         HoverInfo.request(var10.name(), t("gui.scripts.market.row.hint"));
      }
   }

   public static Font cardTitleFont() {
      return Fonts.NEW_MEDIUM.getFont(5.0F);
   }

   public static Font cardNoteFont() {
      return Fonts.NEW_REGULAR.getFont(4.3F);
   }

   public static Font cardMetaFont() {
      return Fonts.NEW_REGULAR.getFont(4.1F);
   }

   public static Font cardTagFont() {
      return Fonts.NEW_MEDIUM.getFont(3.8F);
   }

   public static Font cardButtonFont() {
      return Fonts.NEW_MEDIUM.getFont(4.4F);
   }

   public static BoundsData cardRect(float var0, float var1, float var2) {
      return new BoundsData(var0 + 8.0F, var1, var2 - 16.0F, 36.0F);
   }

   public static float cardTextX(BoundsData var0) {
      return var0.getFloatValue() + 7.0F + 22.0F + 7.0F;
   }

   public static float cardAvatarY(BoundsData var0) {
      return var0.getFloatValue2() + (var0.height() - 22.0F) / 2.0F;
   }

   public static ScriptMarketTab.CardLines cardLines(BoundsData var0) {
      Font var1 = cardTitleFont();
      Font var2 = cardNoteFont();
      float var3 = var1.height() + 2.0F + var2.height() + 2.5F + cardMetaFont().height();
      float var4 = var0.getFloatValue2() + (var0.height() - var3) / 2.0F;
      float var5 = var4 + var1.height() + 2.0F;
      return new ScriptMarketTab.CardLines(var4, var5, var5 + var2.height() + 2.5F);
   }

   public static void drawCard(MenuDrawContext var0, ZenithStyle var1, BoundsData var2, float var3, float var4, float var5, ZenithColor var6, float var7) {
      ScriptsSectionPanel.card(var0, var1, var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), Math.max(var3, var4 * 0.6F), var7);
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      ZenithColor var9 = var1.getPrimaryColor().getZenithColor();
      if (var3 > 0.01F) {
         ZenithColor var10 = var6.method7292(0.09F * var3 * var7);
         ZenithColor var11 = var6.method7292(0.0F);
         var0.drawRoundedRect(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), var8, GlowColor.method8096(var10, var10, var11, var11));
      }

      if (var4 > 0.01F) {
         var0.drawRoundedBorder(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), 0.25F, var8, var9.method7294(var7 * 0.7F * var4));
      }

      if (var5 > 0.01F) {
         var0.drawRoundedRect(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), var8, var9.method7292(0.16F * var5).method7294(var7));
      }
   }

   public static float drawTitleLine(
      MenuDrawContext var0, ZenithStyle var1, String var2, String var3, String var4, ZenithColor var5, float var6, float var7, float var8, float var9
   ) {
      Font var10 = cardTitleFont();
      Font var11 = cardNoteFont();
      float var12 = var8 + var10.height() - var11.height();
      float var13 = (var3.isEmpty() ? 0.0F : var11.width(var3) + 4.0F) + (var4.isEmpty() ? 0.0F : var11.width(var4) + 5.0F);
      String var14 = var10.clip(var2, Math.max(20.0F, var7 - var6 - var13));
      var0.drawText(var10, var14, var6, var8, var1.getTextEnable().getZenithColor().method7294(var9));
      float var15 = var6 + var10.width(var14);
      if (!var3.isEmpty() && var15 + 4.0F + var11.width(var3) <= var7) {
         var0.drawText(var11, var3, var15 + 4.0F, var12, var1.getTextTertiary().getZenithColor().method7294(var9));
         var15 += 4.0F + var11.width(var3);
      }

      if (!var4.isEmpty() && var15 + 5.0F + var11.width(var4) <= var7) {
         var0.drawText(var11, var4, var15 + 5.0F, var12, (var5 == null ? var1.getTextTertiary().getZenithColor() : var5).method7294(var9));
         var15 += 5.0F + var11.width(var4);
      }

      return var15;
   }

   public static void drawDescription(MenuDrawContext var0, ZenithStyle var1, String var2, ZenithColor var3, float var4, float var5, float var6, float var7) {
      Font var8 = cardNoteFont();
      String var9 = var2 == null ? "" : var2.replace('\r', ' ').replace('\n', ' ').strip();
      boolean var10 = var9.isEmpty();
      ZenithColor var11 = var10 ? var1.getTextTertiary().getZenithColor().method7294(0.7F) : (var3 == null ? var1.getTextSecondary().getZenithColor() : var3);
      var0.drawText(var8, var8.clip(var10 ? t("gui.scripts.info.noDescription") : var9, var5 - var4), var4, var6, var11.method7294(var7));
   }

   public static float drawChip(MenuDrawContext var0, String var1, ZenithColor var2, ZenithColor var3, float var4, float var5, float var6) {
      Font var7 = cardTagFont();
      float var8 = var7.height() + 2.6F;
      float var9 = var5 + (cardMetaFont().height() - var8) / 2.0F;
      float var10 = var7.width(var1) + 6.0F;
      var0.drawRoundedRect(var4, var9, var10, var8, ProxyDotRadiusData.method8069(var8 / 2.0F), var2.method7294(var6));
      var0.drawText(var7, var1, var4 + 3.0F, var9 + (var8 - var7.height()) / 2.0F, var3.method7294(var6));
      return var4 + var10 + 3.0F;
   }

   public static float chipWidth(String var0) {
      return cardTagFont().width(var0) + 6.0F;
   }

   public static float drawTags(MenuDrawContext var0, ZenithStyle var1, List<String> var2, float var3, float var4, float var5, float var6, float var7) {
      ZenithColor var8 = var1.getPrimaryColor().getZenithColor();
      float var9 = var4 + (var5 - var4) * 0.5F;
      float var10 = var3;
      int var11 = 0;

      for (String var13 : var2) {
         String var14 = var13.trim();
         if (!var14.isEmpty()) {
            String var15 = "#" + var14;
            if (var11 >= 3 || var10 + chipWidth(var15) > var9) {
               break;
            }

            var10 = drawChip(var0, var15, var8.method7292(0.14F), var8, var10, var6, var7);
            var11++;
         }
      }

      int var16 = 0;

      for (String var19 : var2) {
         if (!var19.isBlank()) {
            var16++;
         }
      }

      if (var11 > 0 && var11 < var16) {
         String var18 = "+" + (var16 - var11);
         if (var10 + chipWidth(var18) <= var9 + 10.0F) {
            var10 = drawChip(var0, var18, var1.getDisableActiveBg().getZenithColor(), var1.getTextTertiary().getZenithColor(), var10, var6, var7);
         }
      }

      return var10;
   }

   public static float drawMeta(MenuDrawContext var0, String var1, ZenithColor var2, float var3, float var4, float var5, float var6, float var7) {
      Font var8 = cardMetaFont();
      float var9 = var3 + (var3 > var4 ? 2.0F : 0.0F);
      if (!var1.isEmpty() && !(var9 >= var5 - 16.0F)) {
         String var10 = var8.clip(var1, var5 - var9);
         var0.drawText(var8, var10, var9, var6, var2.method7294(var7));
         return var9 + var8.width(var10) + 4.0F;
      } else {
         return var3;
      }
   }

   public static float pillWidth(String var0) {
      return Math.max(44.0F, cardButtonFont().width(var0) + 14.0F);
   }

   public static BoundsData pillRect(BoundsData var0, float var1, String var2) {
      float var3 = pillWidth(var2);
      return new BoundsData(var1 - var3, var0.getFloatValue2() + (var0.height() - 14.0F) / 2.0F, var3, 14.0F);
   }

   public static void drawPill(MenuDrawContext var0, ZenithStyle var1, BoundsData var2, String var3, boolean var4, float var5, boolean var6, float var7) {
      Font var8 = cardButtonFont();
      ZenithColor var9 = var1.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData var10 = ProxyDotRadiusData.method8069(var2.height() / 2.0F);
      ZenithColor var11 = var4 ? var1.getFieldSurfaceBackground().getZenithColor() : var9.method7292(0.2F);
      var0.drawRoundedRect(
         var2.getFloatValue(),
         var2.getFloatValue2(),
         var2.width(),
         var2.height(),
         var10,
         var11.method7295(var9.method7292(0.35F), var5 * 0.6F).method7294(var7)
      );
      var0.drawRoundedBorder(
         var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), 0.15F, var10, var9.method7292(0.12F + 0.25F * var5).method7294(var7)
      );
      ZenithColor var12 = var4 ? (var5 > 0.5F ? var1.getTextEnable().getZenithColor() : OK) : var9.method7295(ZenithColor.INSTANCE, var5 * 0.5F);
      if (var6) {
         float var13 = (float)(System.currentTimeMillis() % 1000L) / 1000.0F;
         var12 = var12.method7294(0.55F + 0.45F * (float)Math.abs(Math.sin(var13 * Math.PI)));
      }

      var0.drawText(
         var8,
         var3,
         var2.getFloatValue() + (var2.width() - var8.width(var3)) / 2.0F,
         var2.getFloatValue2() + (var2.height() - var8.height()) / 2.0F,
         var12.method7294(var7)
      );
   }

   private static ZenithColor tint(StoreApi.Data2 var0, OriginUtil.Data3 var1) {
      if (var1.color() != -1) {
         return new ZenithColor(var1.color() | 0xFF000000);
      } else {
         String var2 = var0.name().isBlank() ? var0.getUUID().toString() : var0.name().toLowerCase(Locale.ROOT);
         float var3 = (var2.hashCode() & 65535) / 65536.0F;
         return ZenithColor.method7297(var3, isConfig(var0) ? 0.45F : 0.55F, 0.95F);
      }
   }

   private static String initial(String var0) {
      if (var0 != null) {
         for (int var1 = 0; var1 < var0.length(); var1++) {
            char var2 = var0.charAt(var1);
            if (Character.isLetterOrDigit(var2)) {
               return String.valueOf(var2).toUpperCase(Locale.ROOT);
            }
         }
      }

      return "?";
   }

   public static void letterAvatar(MenuDrawContext var0, ZenithColor var1, String var2, float var3, float var4, float var5, float var6) {
      ProxyDotRadiusData var7 = ProxyDotRadiusData.method8069(var5 * 0.3F);
      var0.drawRoundedRect(
         var3,
         var4,
         var5,
         var5,
         var7,
         GlowColor.method8096(var1.method7292(0.26F * var6), var1.method7292(0.13F * var6), var1.method7292(0.18F * var6), var1.method7292(0.08F * var6))
      );
      var0.drawRoundedBorder(var3, var4, var5, var5, 0.15F, var7, var1.method7292(0.3F * var6));
      Font var8 = Fonts.NEW_SEMIBOLD.getFont(var5 * 0.42F);
      String var9 = initial(var2);
      ZenithColor var10 = var1.method7295(ZenithColor.INSTANCE, 0.2F).method7294(var6);
      float[] var11 = var8.inkBounds(var9);
      if (var11 == null) {
         var0.drawText(var8, var9, var3 + (var5 - var8.width(var9)) / 2.0F, var4 + (var5 - var8.height()) / 2.0F, var10);
      } else {
         var0.drawText(var8, var9, var3 + (var5 - var11[0]) / 2.0F, var4 + (var5 - var11[1]) / 2.0F - var8.baseline() + var11[2], var10);
      }
   }

   public static void cloudAvatar(MenuDrawContext var0, StoreApi.Data2 var1, String var2, ZenithColor var3, float var4, float var5, float var6, float var7) {
      Identifier var8 = var1 != null && var1.hasPreview() ? CloudPreviewTextureCache.get(var1.getUUID(), var1.getText6()) : null;
      if (var8 != null) {
         var0.drawRoundedTexture(var8, var4, var5, var6, var6, ProxyDotRadiusData.method8069(var6 * 0.3F), ZenithColor.INSTANCE.method7294(var7));
      } else {
         letterAvatar(var0, var3, var2, var4, var5, var6, var7);
      }
   }

   private void avatar(MenuDrawContext var1, StoreApi.Data2 var2, OriginUtil.Data3 var3, float var4, float var5, float var6, float var7) {
      cloudAvatar(var1, var2, var2.name(), tint(var2, var3), var4, var5, var6, var7);
   }

   private void header(StoreApi.Data2 var1) {
      this.panel
         .asideCustom(
            46.0F,
            (var2, var3, var4, var5, var6, var7, var8, var9) -> {
               OriginUtil.Data3 var10 = OriginUtil.method6916(var1);
               float var11 = 34.0F;
               this.avatar(var2, var1, var10, var5 + 8.0F, var6 + 5.0F, var11, var8);
               Font var12 = Fonts.NEW_SEMIBOLD.getFont(5.8F);
               Font var13 = Fonts.NEW_REGULAR.getFont(4.3F);
               float var14 = var5 + 8.0F + var11 + 7.0F;
               var2.drawText(
                  var12, var12.clip(var1.name(), var5 + var7 - 8.0F - var14), var14, var6 + 9.0F, var9.getTextEnable().getZenithColor().method7294(var8)
               );
               String var15 = (var10.version().isBlank() ? "" : "v" + var10.version() + "  ·  ") + (var1.getText8().isBlank() ? "?" : var1.getText8());
               var2.drawText(
                  var13,
                  var13.clip(var15, var5 + var7 - 8.0F - var14),
                  var14,
                  var6 + 11.0F + var12.height(),
                  var9.getTextTertiary().getZenithColor().method7294(var8)
               );
               if (!var10.getItems().isEmpty()) {
                  Font var16 = Fonts.NEW_MEDIUM.getFont(3.9F);
                  float var17 = var14;
                  float var18 = var6 + 14.0F + var12.height() + var13.height();

                  for (String var20 : var10.getItems()) {
                     String var21 = "#" + var20;
                     float var22 = var16.width(var21) + 6.0F;
                     if (var17 + var22 > var5 + var7 - 8.0F) {
                        break;
                     }

                     var2.drawRoundedRect(
                        var17,
                        var18 - 1.2F,
                        var22,
                        var16.height() + 2.4F,
                        ProxyDotRadiusData.method8069((var16.height() + 2.4F) / 2.0F),
                        var9.getPrimaryColor().getZenithColor().method7292(0.14F).method7294(var8)
                     );
                     var2.drawText(var16, var21, var17 + 3.0F, var18, var9.getPrimaryColor().getZenithColor().method7294(var8));
                     var17 += var22 + 3.0F;
                  }
               }
            }
         );
   }

   private void configAside(StoreApi.Data2 var1) {
      this.header(var1);
      if (!var1.description().isBlank()) {
         this.panel.paragraph(var1.description(), true);
      }

      this.panel.asideCaption("gui.scripts.caption.actions");
      if (this.needsPurchase(var1)) {
         this.panel
            .asideAction(
               "gui.market.buy.action",
               MenuGlyph.STORAGE,
               () -> StoreApi.method1960(var1.getUUID()).getIntValue() + " ₽",
               () -> this.buy(var1),
               "gui.market.buy.desc"
            );
      }

      boolean var2 = this.configPresent(var1);
      this.panel
         .asideAction(
            var2 ? "gui.market.config.redownload" : "gui.market.config.download",
            MenuGlyph.STORAGE,
            () -> this.busy.contains(var1.getUUID()) ? t("gui.scripts.market.busy") : "",
            () -> this.downloadConfig(var1),
            "gui.market.config.download.desc"
         );
      if (var2) {
         this.panel.asideAction("gui.market.config.apply", MenuGlyph.CHECK, () -> "", () -> this.applyConfig(var1), "gui.market.config.apply.desc");
      }

      if (!var1.mine()) {
         this.panel
            .asideAction(
               "gui.scripts.market.like",
               MenuGlyph.HEART,
               () -> (this.entry(var1.getUUID()) != null && this.entry(var1.getUUID()).isFlag3() ? "♥ " : "") + this.likesOf(var1),
               () -> this.like(var1),
               "gui.scripts.market.like.desc"
            );
      }

      this.panel.asideCaption("gui.scripts.market.caption.about");
      this.panel.asideInfo("gui.scripts.market.info.author", () -> var1.getText8().isBlank() ? "?" : var1.getText8(), "gui.scripts.market.info.author.desc");
      this.panel.asideInfo("gui.configs.server", () -> var1.server().isBlank() ? "—" : var1.server(), "gui.configs.server.desc");
      this.panel.asideInfo("gui.scripts.market.info.revision", () -> "#" + var1.getLongValue2(), "gui.scripts.market.info.revision.desc");
      this.panel.asideInfo("gui.scripts.market.info.size", () -> size(var1.getLongValue()), "gui.scripts.market.info.size.desc");
      this.panel.asideInfo("gui.scripts.market.info.updated", () -> ScriptsSectionPanel.ago(var1.getLongValue4()), "gui.scripts.market.info.updated.desc");
      if (var1.mine()) {
         this.panel.asideCaption("gui.scripts.market.caption.publication");
         this.chips(
            this.entryVisibility,
            () -> visibilityIndex(this.entry(var1.getUUID()) == null ? var1.getText7() : this.entry(var1.getUUID()).getText7()),
            var2x -> this.changeVisibility(var1, var2x)
         );
         this.panel
            .asideAction(
               "gui.scripts.market.codes.create",
               MenuGlyph.KEY,
               () -> String.valueOf(this.codes.getOrDefault(var1.getUUID(), List.of()).size()),
               () -> this.createCode(var1),
               "gui.scripts.market.codes.create.desc"
            );
         this.panel
            .asideAction(
               "gui.scripts.market.unpublish",
               MenuGlyph.CLOSE,
               () -> this.armed.equals("delete:" + var1.getUUID()) ? t("gui.scripts.market.again") : "",
               () -> {
                  if (this.armed("delete:" + var1.getUUID())) {
                     this.unpublish(null, var1);
                  }
               },
               "gui.scripts.market.unpublish.desc"
            );
      }

      this.statusRow();
      this.panel.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
         this.selected = null;
         this.panel.invalidate();
      }, "gui.scripts.market.back.desc");
   }

   private void downloadConfig(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null && this.busy.add(var1.getUUID())) {
         this.revision.incrementAndGet();
         var2.method1970(var1.getUUID()).whenComplete((var2x, var3) -> {
            try {
               if (var3 == null) {
                  File var4 = CloudConfigsSectionPanel.saveDownloaded(var1, var2x);
                  if (!var1.mine()) {
                     this.taken.remember(var1);
                  }

                  String var5 = var4.getName();
                  int var6 = var5.lastIndexOf(46);
                  this.report(t("gui.configs.report.downloaded").replace("{n}", var6 > 0 ? var5.substring(0, var6) : var5), false);
                  return;
               }

               this.report(reason(var3), true);
            } catch (Exception var10) {
               this.report(t("gui.configs.report.writeFailed"), true);
               return;
            } finally {
               this.presence.remove(var1.getUUID());
               this.busy.remove(var1.getUUID());
               MinecraftClient.getInstance().execute(this.panel::invalidate);
            }
         });
      }
   }

   private void applyConfig(StoreApi.Data2 var1) {
      Zenith.getInstance().getManager().method7768(configFile(var1).getName(), Manager.Type.ALL).whenComplete((var2, var3) -> {
         boolean var4 = var3 == null && Boolean.TRUE.equals(var2);
         this.report(var4 ? t("gui.market.config.applied").replace("{n}", var1.name()) : t("gui.configs.report.unreadable"), !var4);
         this.panel.invalidate();
      });
   }

   private void entryAside(Storage var1, StoreApi.Data2 var2) {
      if (isConfig(var2)) {
         this.configAside(var2);
      } else {
         this.header(var2);
         if (!var2.description().isBlank()) {
            this.panel.paragraph(var2.description(), true);
         }

         Script var3 = this.installed.get(var2.getUUID());
         boolean var4 = var3 != null && OriginUtil.method6927(var1, var2);
         this.panel.asideCaption("gui.scripts.caption.actions");
         if (this.needsPurchase(var2)) {
            this.panel
               .asideAction(
                  "gui.market.buy.action",
                  MenuGlyph.STORAGE,
                  () -> StoreApi.method1960(var2.getUUID()).getIntValue() + " ₽",
                  () -> this.buy(var2),
                  "gui.market.buy.desc"
               );
         }

         String var5 = var3 == null
            ? (var2.mine() ? "gui.scripts.market.download" : "gui.scripts.market.install")
            : (var4 ? "gui.scripts.market.update" : "gui.scripts.market.open");
         this.panel
            .asideAction(
               var5,
               MenuGlyph.CLOUD,
               () -> this.busy.contains(var2.getUUID()) ? t("gui.scripts.market.busy") : "",
               () -> this.primary(var1, var2),
               "gui.scripts.market.primary.desc"
            );
         if (var3 != null && !var4) {
            this.panel
               .asideAction("gui.scripts.market.reinstall", MenuGlyph.RESET, () -> "", () -> this.install(var1, var2), "gui.scripts.market.reinstall.desc");
         }

         if (!var2.mine()) {
            this.panel
               .asideAction(
                  "gui.scripts.market.like",
                  MenuGlyph.HEART,
                  () -> (this.entry(var2.getUUID()) != null && this.entry(var2.getUUID()).isFlag3() ? "♥ " : "") + this.likesOf(var2),
                  () -> this.like(var2),
                  "gui.scripts.market.like.desc"
               );
         }

         if (var3 != null) {
            this.panel
               .asideAction(
                  "gui.scripts.market.uninstall",
                  MenuGlyph.CLOSE,
                  () -> this.armed.equals("uninstall:" + var2.getUUID()) ? t("gui.scripts.market.again") : "",
                  () -> {
                     if (this.armed("uninstall:" + var2.getUUID())) {
                        this.uninstall(var1, var3);
                     }
                  },
                  "gui.scripts.market.uninstall.desc"
               );
         }

         this.panel.asideCaption("gui.scripts.market.caption.about");
         this.panel.asideInfo("gui.scripts.market.info.author", () -> var2.getText8().isBlank() ? "?" : var2.getText8(), "gui.scripts.market.info.author.desc");
         this.panel.asideInfo("gui.scripts.market.info.revision", () -> "#" + var2.getLongValue2(), "gui.scripts.market.info.revision.desc");
         this.panel.asideInfo("gui.scripts.market.info.size", () -> size(var2.getLongValue()), "gui.scripts.market.info.size.desc");
         this.panel.asideInfo("gui.scripts.market.info.updated", () -> ScriptsSectionPanel.ago(var2.getLongValue4()), "gui.scripts.market.info.updated.desc");
         String var6 = OriginUtil.method6916(var2).getText3();
         if (!var6.isBlank() && (var6.startsWith("https://") || var6.startsWith("http://"))) {
            this.panel
               .asideAction("gui.scripts.market.link", MenuGlyph.NETWORK, () -> "", () -> Util.getOperatingSystem().open(var6), "gui.scripts.market.link.desc");
         }

         if (var2.mine()) {
            this.panel.asideCaption("gui.scripts.market.caption.publication");
            this.panel
               .asideInfo(
                  "gui.market.caption.publish",
                  () -> this.marketLabel(this.entry(var2.getUUID()) == null ? var2 : this.entry(var2.getUUID())),
                  "gui.market.status.desc"
               );
            this.chips(
               this.entryVisibility,
               () -> visibilityIndex(this.entry(var2.getUUID()) == null ? var2.getText7() : this.entry(var2.getUUID()).getText7()),
               var2x -> this.changeVisibility(var2, var2x)
            );
            if (var3 != null) {
               this.panel
                  .asideAction(
                     "gui.scripts.market.push",
                     MenuGlyph.CLOUD,
                     () -> this.publishing.contains(var3.getId()) ? t("gui.scripts.market.busy") : "",
                     () -> this.publish(var1, var3, visibilityIndex(var2.getText7())),
                     "gui.scripts.market.push.desc"
                  );
            }

            this.panel
               .asideAction(
                  "gui.scripts.market.codes.create",
                  MenuGlyph.KEY,
                  () -> String.valueOf(this.codes.getOrDefault(var2.getUUID(), List.of()).size()),
                  () -> this.createCode(var2),
                  "gui.scripts.market.codes.create.desc"
               );

            for (StoreApi.Data var8 : this.codes.getOrDefault(var2.getUUID(), List.of())) {
               this.panel
                  .asideCustom(
                     12.0F,
                     (var1x, var2x, var3x, var4x, var5x, var6x, var7, var8x) -> {
                        Font var9 = Fonts.NEW_MEDIUM.getFont(4.4F);
                        boolean var10 = new BoundsData(var4x, var5x, var6x, 12.0F).contains(var2x, var3x);
                        ZenithColor var11 = "ACTIVE".equalsIgnoreCase(var8.getText2())
                           ? var8x.getTextSecondary().getZenithColor()
                           : var8x.getTextTertiary().getZenithColor();
                        var1x.drawText(
                           var9,
                           var8.getText().isBlank() ? "••••••" : var8.getText(),
                           var4x + 8.0F,
                           var5x + 3.0F,
                           (var10 ? var8x.getTextEnable().getZenithColor() : var11).method7294(var7)
                        );
                        String var12 = var8.getText2().toLowerCase(Locale.ROOT);
                        var1x.drawText(
                           var9, var12, var4x + var6x - 8.0F - var9.width(var12), var5x + 3.0F, var8x.getTextTertiary().getZenithColor().method7294(var7)
                        );
                        if (var10) {
                           HoverInfo.request(t("gui.scripts.market.codes"), t("gui.scripts.market.codes.row"));
                        }
                     },
                     () -> {
                        if (!var8.getText().isBlank()) {
                           MinecraftClient.getInstance().keyboard.setClipboard(var8.getText());
                           this.report(t("gui.scripts.market.status.copied"), false);
                        }
                     },
                     null,
                     () -> this.revokeCode(var2, var8)
                  );
            }

            this.panel
               .asideAction(
                  "gui.scripts.market.unpublish",
                  MenuGlyph.CLOSE,
                  () -> this.armed.equals("delete:" + var2.getUUID()) ? t("gui.scripts.market.again") : "",
                  () -> {
                     if (this.armed("delete:" + var2.getUUID())) {
                        this.unpublish(var1, var2);
                     }
                  },
                  "gui.scripts.market.unpublish.desc"
               );
         }

         this.statusRow();
         this.panel.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
            this.selected = null;
            this.panel.invalidate();
         }, "gui.scripts.market.back.desc");
      }
   }

   private int count(List<StoreApi.Data2> var1, boolean var2) {
      int var3 = 0;

      for (StoreApi.Data2 var5 : var1) {
         if (isConfig(var5) == var2) {
            var3++;
         }
      }

      return var3;
   }

   private int likesOf(StoreApi.Data2 var1) {
      StoreApi.Data2 var2 = this.entry(var1.getUUID());
      return var2 == null ? var1.getIntValue() : var2.getIntValue();
   }

   private void overviewAside(Storage var1) {
      this.panel.asideCaption("gui.scripts.market.caption.overview");
      this.panel.asideInfo("gui.market.info.scripts", () -> String.valueOf(this.count(this.catalog, false)), "gui.market.info.scripts.desc");
      this.panel.asideInfo("gui.market.info.configs", () -> String.valueOf(this.count(this.catalog, true)), "gui.market.info.configs.desc");
      this.panel.asideInfo("gui.scripts.market.info.library", () -> String.valueOf(this.library.size()), "gui.scripts.market.info.library.desc");
      this.panel.asideInfo("gui.scripts.market.info.mine", () -> String.valueOf(this.mine.size()), "gui.scripts.market.info.mine.desc");
      this.panel.asideCaption("gui.scripts.market.caption.redeem");
      this.panel
         .asideCustom(
            22.0F,
            (var1x, var2, var3, var4, var5, var6, var7, var8) -> {
               Font var9 = Fonts.NEW_MEDIUM.getFont(4.6F);
               String var10 = t("gui.scripts.market.redeem.go");
               float var11 = var9.width(var10) + 14.0F;
               float var12 = var4 + 8.0F;
               float var13 = var6 - 16.0F - var11 - 4.0F;
               this.redeem
                  .render(
                     var1x,
                     var9,
                     var12,
                     var5 + 3.0F,
                     var13,
                     var7,
                     t("gui.scripts.market.redeem.hint"),
                     var8.getFieldSurfaceBackground().getZenithColor(),
                     var8.getFieldBorder().getZenithColor().method7294(2.0F),
                     var8.getPrimaryColor().getZenithColor(),
                     var8.getTextEnable().getZenithColor(),
                     var8.getTextTertiary().getZenithColor()
                  );
               this.redeemButton = new BoundsData(var12 + var13 + 4.0F, var5 + 3.0F, var11, 16.0F);
               boolean var14 = this.panel.pressFx.begin(var1x, "redeem", this.redeemButton);
               boolean var15 = this.redeemButton.contains(var2, var3);
               float var16 = this.panel.hot("redeem", var15);
               ZenithColor var17 = var8.getPrimaryColor().getZenithColor();
               var1x.drawRoundedRect(
                  this.redeemButton.getFloatValue(),
                  this.redeemButton.getFloatValue2(),
                  var11,
                  16.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  var17.method7292(0.18F + 0.2F * var16).method7294(var7)
               );
               this.panel.pressFx.ink(var1x, "redeem", this.redeemButton, 4.0F, ZenithColor.INSTANCE.method7292(0.2F), var7);
               var1x.drawText(
                  var9,
                  var10,
                  this.redeemButton.getFloatValue() + 7.0F,
                  this.redeemButton.getFloatValue2() + (16.0F - var9.height()) / 2.0F,
                  var17.method7295(ZenithColor.INSTANCE, var16 * 0.5F).method7294(var7)
               );
               this.panel.pressFx.end(var1x, var14);
            },
            () -> {},
            null
         );
      if (this.category != 2) {
         this.publishSection(var1);
      }

      if (this.category != 1) {
         this.panel.asideCaption("gui.market.caption.publishConfig");
         this.panel.paragraph(t("gui.market.publishConfig.hint"), true);
         this.panel.asideAction("gui.market.publishConfig.go", MenuGlyph.CONFIGS, () -> "", this.panel::openConfigsCloud, "gui.market.publishConfig.go.desc");
      }

      this.panel
         .asideAction(
            "gui.scripts.market.refresh",
            MenuGlyph.RESET,
            () -> this.loading.get() > 0 ? t("gui.scripts.market.busy") : "",
            () -> this.fetch(true),
            "gui.scripts.market.refresh.desc"
         );
      this.statusRow();
   }

   public void publishSection(Storage var1) {
      this.panel.asideCaption("gui.scripts.market.caption.publish");
      this.chips(this.publishVisibility, () -> this.publishVis, var1x -> this.publishVis = var1x);
      ArrayList<Script> var2 = new ArrayList();

      for (Script var4 : var1.method1293()) {
         OriginUtil.Data2 var5 = OriginUtil.method6921(var4);
         if (var5 == null || var5.mine()) {
            var2.add(var4);
         }
      }

      if (var2.isEmpty()) {
         this.panel.paragraph(t("gui.scripts.market.publish.none"), true);
      }

      for (Object var7_o : (Iterable<?>)(Object)(var2)) {
         Script var7 = (Script)var7_o;
         this.panel
            .asideCustom(
               24.0F,
               (var2x, var3, var4x, var5x, var6, var7x, var8, var9) -> this.renderCandidate(var2x, var3, var4x, var5x, var6, var7x, var8, var9, var7),
               () -> this.publish(var1, var7, this.publishVis),
               null
            );
      }

      if (this.view == 0) {
         this.statusRow();
      }
   }

   private void renderCandidate(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, Script var9) {
      BoundsData var10 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 21.0F);
      boolean var11 = var10.contains(var2, var3);
      float var12 = this.panel.hot("pub:" + var9.getId(), var11);
      ScriptsSectionPanel.card(var1, var8, var10.getFloatValue(), var10.getFloatValue2(), var10.width(), var10.height(), var12, var7);
      ScriptInfoPage.drawAvatar(var1, var8, var9, var10.getFloatValue() + 4.0F, var10.getFloatValue2() + 3.0F, var10.height() - 6.0F, var7, -2);
      Font var13 = Fonts.NEW_MEDIUM.getFont(4.7F);
      Font var14 = Fonts.NEW_REGULAR.getFont(3.9F);
      OriginUtil.Data2 var15 = OriginUtil.method6921(var9);
      boolean var16 = this.publishing.contains(var9.getId());
      String var17 = t(var16 ? "gui.scripts.market.busy" : (var15 != null ? "gui.scripts.market.publish.update" : "gui.scripts.market.publish.go"));
      float var18 = var14.width(var17) + 10.0F;
      float var19 = var10.getFloatValue() + var10.height() + 2.0F;
      float var20 = var10.getFloatValue() + var10.width() - var18 - 8.0F;
      var1.drawText(
         var13, var13.clip(var9.method1403(), var20 - var19), var19, var10.getFloatValue2() + 3.5F, var8.getTextEnable().getZenithColor().method7294(var7)
      );
      String var21 = var9.getPath() == null
         ? t("gui.scripts.market.publish.single")
         : (var15 != null ? t("gui.scripts.market.publish.live").replace("{n}", String.valueOf(var15.getLongValue())) : t("gui.scripts.market.publish.draft"));
      var1.drawText(
         var14,
         var14.clip(var21, var20 - var19),
         var19,
         var10.getFloatValue2() + 5.0F + var13.height(),
         var8.getTextTertiary().getZenithColor().method7294(var7)
      );
      ZenithColor var22 = var8.getPrimaryColor().getZenithColor();
      float var23 = var10.getFloatValue() + var10.width() - var18 - 4.0F;
      float var24 = var10.getFloatValue2() + (var10.height() - 10.0F) / 2.0F;
      var1.drawRoundedRect(var23, var24, var18, 10.0F, ProxyDotRadiusData.method8069(5.0F), var22.method7292(0.16F + 0.2F * var12).method7294(var7));
      var1.drawText(var14, var17, var23 + 5.0F, var24 + (10.0F - var14.height()) / 2.0F, var22.method7295(ZenithColor.INSTANCE, var12 * 0.5F).method7294(var7));
   }

   private void chips(ChipStrip var1, IntSupplier var2, IntConsumer var3) {
      this.panel.asideCustom(var1.height(128.0F) + 6.0F, (var2x, var3x, var4, var5, var6, var7, var8, var9) -> {
         var1.render(var2x, var3x, var4, var5 + 8.0F, var6 + 3.0F, var7 - 16.0F, var2.getAsInt(), var8, var9);
         if (var1.hovered() >= 0) {
            HoverInfo.request(t("gui.scripts.market.visibility"), t("gui.scripts.market.visibility.desc"));
         }
      }, () -> {
         int var3x = var1.hovered();
         if (var3x >= 0) {
            var3.accept(var3x);
            this.panel.invalidate();
         }
      }, null);
   }

   private void statusRow() {
      this.panel.asideCustom(14.0F, (var1, var2, var3, var4, var5, var6, var7, var8) -> {
         long var9 = System.currentTimeMillis() - this.statusAt;
         if (!this.status.isEmpty() && var9 <= 8000L) {
            float var11 = var9 > 7000L ? 1.0F - (float)(var9 - 7000L) / 1000.0F : Math.min(1.0F, (float)var9 / 180.0F);
            Font var12 = Fonts.NEW_REGULAR.getFont(4.3F);
            var1.drawText(var12, var12.clip(this.status, var6 - 16.0F), var4 + 8.0F, var5 + 4.0F, (this.statusError ? ERROR : OK).method7294(var7 * var11));
         }
      });
   }

   public boolean clickRedeem(double var1, double var3) {
      BoundsData var5 = this.redeem.bounds();
      boolean var6 = var5 != null && var5.contains(var1, var3);
      this.redeem.setFocused(var6);
      if (this.redeemButton != null && this.redeemButton.contains(var1, var3)) {
         this.panel.pressFx.press("redeem", var1, var3);
         this.doRedeem();
         return true;
      } else {
         return var6;
      }
   }

   public void doRedeem() {
      String var1 = this.redeem.get();
      StoreApi var2 = store();
      if (!var1.isEmpty() && var2 != null) {
         var2.method1986(var1).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               this.report(reason(var2x), true);
            } else {
               MinecraftClient.getInstance().execute(() -> {
                  this.redeem.set("");
                  this.report(t("gui.scripts.market.status.redeemed").replace("{n}", var1x.name()), false);
                  this.view = 2;
                  this.category = isConfig(var1x) ? 2 : 1;
                  this.selected = var1x.getUUID();
                  this.fetch(true);
                  this.panel.invalidate();
               });
            }
         });
      }
   }

   private void install(Storage var1, StoreApi.Data2 var2) {
      StoreApi var3 = store();
      if (var3 != null && this.busy.add(var2.getUUID())) {
         this.revision.incrementAndGet();
         var3.method1970(var2.getUUID())
            .whenComplete(
               (var3x, var4) -> {
                  if (var4 != null) {
                     this.busy.remove(var2.getUUID());
                     this.report(reason(var4), true);
                  } else {
                     MinecraftClient.getInstance()
                        .execute(
                           () -> {
                              try {
                                 Script var4x = OriginUtil.method6928(var1, var2, var3x);
                                 this.installed = OriginUtil.method6925(var1);
                                 String var5 = var4x == null ? "" : ScriptPermissions.method5(var4x);
                                 this.report(
                                    t("gui.scripts.market.status.installed").replace("{n}", var4x == null ? var2.name() : var4x.method1403())
                                       + (var5.isEmpty() ? "" : " · " + t("gui.scripts.market.rights").replace("{r}", var5)),
                                    false
                                 );
                              } catch (Exception var9) {
                                 this.report(reason(var9), true);
                              } finally {
                                 this.busy.remove(var2.getUUID());
                                 this.panel.invalidate();
                              }
                           }
                        );
                  }
               }
            );
      }
   }

   private void uninstall(Storage var1, Script var2) {
      try {
         OriginUtil.uninstall(var1, var2);
         this.installed = OriginUtil.method6925(var1);
         this.report(t("gui.scripts.market.status.removed").replace("{n}", var2.method1403()), false);
      } catch (Exception var4) {
         this.report(reason(var4), true);
      }

      this.panel.invalidate();
   }

   private void publish(Storage var1, Script var2, int var3) {
      StoreApi var4 = store();
      if (var4 != null && this.publishing.add(var2.getId())) {
         this.revision.incrementAndGet();
         String var5 = VISIBILITY[Math.max(0, Math.min(VISIBILITY.length - 1, var3))];
         OriginUtil.Data2 var6 = OriginUtil.method6921(var2);
         boolean var7 = "PUBLIC".equals(var5) && (var6 == null || !StoreApi.method1960(var6.getUUID()).method2007());
         OriginUtil.method6933(var4, var1, var2, var7 ? "PRIVATE" : var5)
            .thenCompose(var2x -> var7 ? var4.method1961(var2x.getUUID(), 0) : CompletableFuture.completedFuture(var2x))
            .whenComplete((var3x, var4x) -> {
               this.publishing.remove(var2.getId());
               if (var4x != null) {
                  this.report(reason(var4x), true);
               } else {
                  this.report(var7 ? t("gui.market.report.review") : t("gui.scripts.market.status.published").replace("{n}", var3x.name()), false);
                  MinecraftClient.getInstance().execute(() -> {
                     this.fetch(true);
                     this.selected = var3x.getUUID();
                     this.view = 3;
                     this.panel.invalidate();
                  });
               }
            });
      }
   }

   private void unpublish(Storage var1, StoreApi.Data2 var2) {
      StoreApi var3 = store();
      if (var3 != null) {
         var3.method1975(var2.getUUID()).whenComplete((var3x, var4) -> {
            if (var4 != null) {
               this.report(reason(var4), true);
            } else {
               MinecraftClient.getInstance().execute(() -> {
                  Script var3xx = this.installed.get(var2.getUUID());
                  if (var3xx != null) {
                     OriginUtil.method6924(var3xx);
                  }

                  this.mine.removeIf(var1xxx -> var1xxx.getUUID().equals(var2.getUUID()));
                  this.catalog.removeIf(var1xxx -> var1xxx.getUUID().equals(var2.getUUID()));
                  this.selected = null;
                  if (var1 != null) {
                     this.installed = OriginUtil.method6925(var1);
                  }

                  this.report(t("gui.scripts.market.status.unpublished"), false);
                  this.panel.invalidate();
               });
            }
         });
      }
   }

   private void like(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         var2.method1978(var1.getUUID())
            .whenComplete(
               (var2x, var3) -> {
                  if (var3 != null) {
                     this.report(reason(var3), true);
                  } else {
                     StoreApi.Data2 var4 = this.entry(var1.getUUID());
                     if (var4 == null) {
                        var4 = var1;
                     }

                     this.replace(
                        new StoreApi.Data2(
                           var4.getUUID(),
                           var4.kind(),
                           var4.name(),
                           var4.fileName(),
                           var4.description(),
                           var4.server(),
                           var4.getLongValue(),
                           var4.getText6(),
                           var4.getText7(),
                           var4.getLongValue2(),
                           var4.getLongValue3(),
                           var4.getLongValue4(),
                           var4.getLongValue5(),
                           var4.getText8(),
                           var4.mine(),
                           var4.hasPreview(),
                           var2x[0],
                           var2x[1] == 1,
                           var4.getIntValue2()
                        )
                     );
                     MinecraftClient.getInstance().execute(this.panel::invalidate);
                  }
               }
            );
      }
   }

   private void changeVisibility(StoreApi.Data2 var1, int var2) {
      StoreApi var3 = store();
      if (var3 != null) {
         boolean var4 = "PUBLIC".equals(VISIBILITY[var2]) && !isConfig(var1) && !StoreApi.method1960(var1.getUUID()).method2007();
         (var4 ? var3.method1961(var1.getUUID(), 0) : var3.method1974(var1.getUUID(), null, null, VISIBILITY[var2])).whenComplete((var2x, var3x) -> {
            if (var3x != null) {
               this.report(reason(var3x), true);
            } else {
               this.replace(var2x);
               this.report(t(var4 ? "gui.market.report.review" : "gui.scripts.market.status.visibility"), false);
               MinecraftClient.getInstance().execute(this.panel::invalidate);
            }
         });
      }
   }

   private void loadCodes(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         var2.method1980(var1.getUUID()).whenComplete((var2x, var3) -> {
            if (var3 == null) {
               this.codes.put(var1.getUUID(), (List<StoreApi.Data>)var2x);
               this.revision.incrementAndGet();
            }
         });
      }
   }

   private void createCode(StoreApi.Data2 var1) {
      StoreApi var2 = store();
      if (var2 != null) {
         var2.method1979(var1.getUUID(), 1).whenComplete((var2x, var3) -> {
            if (var3 != null) {
               this.report(reason(var3), true);
            } else {
               if (!var2x.isEmpty()) {
                  String var4 = var2x.get(0).getText();
                  MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().keyboard.setClipboard(var4));
                  this.report(t("gui.scripts.market.status.codeCreated").replace("{n}", var4), false);
               }

               this.loadCodes(var1);
            }
         });
      }
   }

   private void revokeCode(StoreApi.Data2 var1, StoreApi.Data var2) {
      StoreApi var3 = store();
      if (var3 != null) {
         var3.method1981(var1.getUUID(), var2.getUUID()).whenComplete((var2x, var3x) -> {
            if (var3x != null) {
               this.report(reason(var3x), true);
            } else {
               this.report(t("gui.scripts.market.status.revoked"), false);
            }

            this.loadCodes(var1);
         });
      }
   }

   private static int visibilityIndex(String var0) {
      for (int var1 = 0; var1 < VISIBILITY.length; var1++) {
         if (VISIBILITY[var1].equalsIgnoreCase(var0)) {
            return var1;
         }
      }

      return 3;
   }

   private static String size(long var0) {
      if (var0 < 1024L) {
         return var0 + " B";
      } else {
         return var0 < 1048576L
            ? String.format(Locale.ROOT, "%.1f KB", (float)var0 / 1024.0F)
            : String.format(Locale.ROOT, "%.1f MB", (float)var0 / 1024.0F / 1024.0F);
      }
   }

   public static final class CardLines {
      private final float title;
      private final float note;
      private final float meta;

      public float title() {
         return this.title;
      }

      public float note() {
         return this.note;
      }

      public float meta() {
         return this.meta;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CardLines other)) return false;
         return java.util.Objects.equals(this.title, other.title) && java.util.Objects.equals(this.note, other.note) && java.util.Objects.equals(this.meta, other.meta);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(title, note, meta);
      }

      @Override
      public String toString() {
         return "CardLines[" + "title=" + this.title + ", " + "note=" + this.note + ", " + "meta=" + this.meta + "]";
      }

      public CardLines(float var1, float var2, float var3) {
         this.title = var1;
         this.note = var2;
         this.meta = var3;
      }
   }

   private static final class Presence {
      private final String name;
      private final boolean present;
      private final long checkedAt;

      public String name() {
         return this.name;
      }

      public boolean present() {
         return this.present;
      }

      public long checkedAt() {
         return this.checkedAt;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Presence other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.present, other.present) && java.util.Objects.equals(this.checkedAt, other.checkedAt);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, present, checkedAt);
      }

      @Override
      public String toString() {
         return "Presence[" + "name=" + this.name + ", " + "present=" + this.present + ", " + "checkedAt=" + this.checkedAt + "]";
      }

      private Presence(String var1, boolean var2, long var3) {
         this.name = var1;
         this.present = var2;
         this.checkedAt = var3;
      }
   }
}

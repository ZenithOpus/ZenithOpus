package org.zenith.client.screens.nlgui.panel;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.chat.ChatCards;
import org.zenith.client.screens.nlgui.chat.ChatCompose;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.CloudChatClient;
import org.zenith.utility.api.CloudDmClient;
import org.zenith.utility.api.StickerPackService;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.api.UserSearchClient;
import org.zenith.utility.config.ChatImageCache;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.config.SiteSession;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.Coord;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CloudChatSectionPanel extends SectionPanel {
   private static final int TAB_GLOBAL = 0;
   private static final int TAB_DIRECT = 1;
   private static final float INPUT_HEIGHT = 20.0F;
   private static final float ERROR_ROW = 7.0F;
   private static final float BUBBLE_PAD_X = 7.0F;
   private static final float BUBBLE_PAD_Y = 5.0F;
   private static final float BUBBLE_GAP = 3.0F;
   private static final float BUBBLE_ROUND = 5.0F;
   private static final float BUBBLE_TAIL = 1.5F;
   private static final float TIME_GAP = 6.0F;
   private static final float CONTACT_HEIGHT = 24.0F;
   private static final float ASIDE_WIDTH = 138.0F;
   private static final float AVATAR = 16.0F;
   private static final float PEER_BAR = 24.0F;
   private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm", Locale.ROOT);
   private static final ZenithColor ERROR_COLOR = new ZenithColor(232, 120, 108);
   private static final Map<String, SimpleDateFormat> DAY = new HashMap<>();
   private static final long ROSTER_MS = 5000L;
   private static final long POLL_MS = 2000L;
   private static final int MAX_PREVIEWS = 64;
   private static final int TAG_RESERVE = 120;
   private SearchBox input;
   private int lastCount = -1;
   private int lastTab = -1;
   private int lastColumn = -1;
   private String lastStatus = "";
   private String lastQuery = "";
   private boolean stickToEnd = true;
   private final Animation messageArrival = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private final Animation focusAnimation = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation listReveal = new Animation(320L, 1.0F, Easing.INSTANCE12);
   private final Map<Long, Animation> selectFade = new ConcurrentHashMap<>();
   private final Map<Long, Animation> avatarFade = new ConcurrentHashMap<>();
   private final Animation cardReveal = new Animation(240L, 1.0F, Easing.INSTANCE12);
   private boolean cardClosing;
   private final Map<Long, Animation> contactHover = new ConcurrentHashMap<>();
   private final Map<Long, Animation> unreadPop = new ConcurrentHashMap<>();
   private final Map<Long, Animation> onlineFade = new ConcurrentHashMap<>();
   private final Map<String, Animation> glyphHover = new ConcurrentHashMap<>();
   private final Map<String, Animation> cellHover = new ConcurrentHashMap<>();
   private final Map<String, Animation> stickerFade = new ConcurrentHashMap<>();
   private final Animation pickerAnimation = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private final Animation packPlateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation packPlateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean packPlateReady;
   private final Animation pendingAnimation = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private final Animation peerBarAnimation = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private long barUid;
   private String barName = "";
   private final Animation noticeAnimation = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private final Animation errorAnimation = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private String shownNotice = "";
   private String shownError = "";
   private String shownLabel = "";
   private boolean wantFocus;
   private boolean blurred;
   private long peerUid;
   private String peerName = "";
   private long selectedUid;
   private String selectedName = "";
   private String selectedMcName = "";
   private String draft = "";
   private String draftTag = "";
   private String pendingTag = "";
   private String pendingLabel = "";
   private boolean pickerOpen;
   private int pickerPack;
   private BoundsData pickerBounds;
   private BoundsData pickerAt;
   private BoundsData coordsButton;
   private BoundsData inventoryButton;
   private BoundsData stickerButton;
   private BoundsData pendingButton;
   private long deleteMenuMessage;
   private long deleteMenuPeer;
   private BoundsData deleteMenuBounds;
   private float deleteMenuAnchorTop = Float.NaN;
   private final Animation deleteMenuAnimation = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation deleteMenuHover = new Animation(120L, 0.0F, Easing.INSTANCE12);
   private static final long DYING_MS = 240L;
   private static final int DYING_MAX = 4;
   private static final int FEED_NODE_GLOBAL = 0;
   private static final int FEED_NODE_DIRECT = 1;
   private static final int FEED_SITE_GLOBAL = 2;
   private final List<CloudChatSectionPanel.Ghost> ghosts = new ArrayList<>();
   private final Map<Long, CloudPackets.Data13> knownNodeGlobal = new LinkedHashMap<>();
   private final Map<Long, StoreApi.Data3> knownNodeDirect = new LinkedHashMap<>();
   private final Map<Long, CloudChatClient.Data3> knownSiteGlobal = new LinkedHashMap<>();
   private long knownDirectPeer = -1L;
   private volatile List<StoreApi.Data4> online = List.of();
   private volatile List<StoreApi.Data3> conversation = List.of();
   private final Object conversationLock = new Object();
   private final Map<Long, String> previews = new ConcurrentHashMap<>();
   private volatile long lastPoll;
   private int seenDirectSeq;
   private volatile long lastRoster;
   private volatile long polledPeer = -1L;
   private boolean dayBroke;
   private long peerUnderFor = -1L;
   private int peerUnderStamp = -1;
   private String peerUnderText = "";
   private volatile String error = "";
   private volatile String notice = "";
   private volatile long noticeAt;
   private volatile int revision;
   private int seenRevision;
   private int stickersPrepared = -1;
   private int seenSiteRevision;
   private final Map<CloudChatSectionPanel.BubbleKey, CloudChatSectionPanel.BubbleLayout> bubbleLayouts = new LinkedHashMap<CloudChatSectionPanel.BubbleKey, CloudChatSectionPanel.BubbleLayout>(
      256, 0.75F, true
   ) {
      @Override
      public boolean removeEldestEntry(Entry<CloudChatSectionPanel.BubbleKey, CloudChatSectionPanel.BubbleLayout> var1) {
         return this.size() > 768;
      }
   };
   private static final Map<CloudChatSectionPanel.ClipKey, String> CLIPS = new LinkedHashMap<CloudChatSectionPanel.ClipKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<CloudChatSectionPanel.ClipKey, String> var1) {
         return this.size() > 256;
      }
   };

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   private static CloudConnectionState state() {
      Cloud var0 = cloud();
      return var0 == null ? null : var0.state();
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private static CloudChatClient global() {
      return CloudChatClient.getInstance();
   }

   private static CloudDmClient direct() {
      return CloudDmClient.getInstance();
   }

   @Override
   public String headerTitle() {
      return t("gui.cloud.tab.chat");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CHAT;
   }

   @Override
   public String sectionDescription() {
      return "gui.chat.desc";
   }

   @Override
   public String emptyText() {
      if (this.getActiveTab() == 1) {
         if (!siteMode()) {
            CloudConnectionState var4 = state();
            if (var4 == null) {
               return "gui.friends.reason.down";
            }
         }

         if (!this.hasPeer()) {
            return "gui.chat.empty.pick";
         } else {
            return siteMode() && direct().isFlag2() ? "gui.chat.loading" : "gui.chat.empty.first";
         }
      } else if (siteMode()) {
         if (global().method2166()) {
            return "gui.chat.empty.global";
         } else {
            String var3 = global().error();
            return var3 != null && !var3.isEmpty() ? clipText(var3) : "gui.chat.loading";
         }
      } else {
         CloudConnectionState var1 = state();
         if (var1 == null) {
            return "gui.friends.reason.down";
         } else if (var1.method8035()) {
            return "gui.chat.empty.global";
         } else {
            String var2 = var1.note();
            if (var2 == null || var2.isBlank()) {
               var2 = var1.getLastError();
            }

            return var2 != null && !var2.isBlank() ? var1.getType().label() + " — " + var2 : var1.getType().label();
         }
      }
   }

   @Override
   public float asideWidth() {
      return 138.0F;
   }

   @Override
   public float asideAlpha() {
      return this.selectedUid == 0L ? 1.0F : this.cardReveal.getValue();
   }

   @Override
   public float mainWidth() {
      return 242.0F;
   }

   private static boolean siteMode() {
      return SiteSession.method2291();
   }

   private boolean hasPeer() {
      return siteMode() ? direct().hasPeer() : this.peerUid != 0L;
   }

   private long openPeerUid() {
      return siteMode() ? direct().getLongValue() : this.peerUid;
   }

   private String openPeerName() {
      return siteMode() ? direct().getPeerName() : this.peerName;
   }

   @Override
   public void tick() {
      this.prepareStickers();
      if (this.cardClosing) {
         this.cardReveal.method2808(0.0F);
      }

      if (this.cardClosing && this.cardReveal.getValue() <= 0.01F) {
         this.cardClosing = false;
         this.selectedUid = 0L;
         this.selectedName = "";
         this.selectedMcName = "";
         this.invalidate();
      }

      if (this.revision != this.seenRevision) {
         this.seenRevision = this.revision;
         this.invalidate();
      }

      long var1 = System.currentTimeMillis();
      if (siteMode()) {
         if (this.getActiveTab() == 0) {
            global().method2167();
         } else {
            direct().method4863();
         }

         int var3 = global().revision() * 31 + direct().revision();
         if (var3 != this.seenSiteRevision) {
            this.seenSiteRevision = var3;
            this.revision++;
         }
      } else {
         if (this.getActiveTab() == 0 && var1 - this.lastRoster > 5000L) {
            this.lastRoster = var1;
            this.fetchRoster();
         }

         Cloud var11 = cloud();
         if (var11 != null && var11.getPartyMember().getIntValue() != this.seenDirectSeq) {
            this.seenDirectSeq = var11.getPartyMember().getIntValue();
            if (var11.getPartyMember().getLongValue2() == this.peerUid) {
               this.lastPoll = 0L;
            }
         }

         if (this.getActiveTab() == 1 && this.hasPeer() && (this.peerUid != this.polledPeer || var1 - this.lastPoll > 2000L)) {
            this.lastPoll = var1;
            this.polledPeer = this.peerUid;
            this.fetchConversation(this.peerUid);
         }
      }

      int var12 = this.countForChange();
      String var4 = this.statusForChange();
      int var5 = Math.round(this.mainWidth());
      boolean var6 = this.getActiveTab() != this.lastTab;
      boolean var7 = !var4.equals(this.lastStatus);
      String var8 = this.searchQuery() == null ? "" : this.searchQuery();
      boolean var9 = !var8.equals(this.lastQuery);
      if (var12 != this.lastCount || var6 || var5 != this.lastColumn || var7 || var9) {
         boolean var10 = var12 > this.lastCount && this.lastCount >= 0 && !var6;
         this.lastCount = var12;
         this.lastTab = this.getActiveTab();
         this.lastColumn = var5;
         this.lastStatus = var4;
         this.lastQuery = var8;
         if (var10) {
            this.messageArrival.reset(0.0F);
            this.messageArrival.method2810(1.0F);
         }

         if (var6) {
            this.focusInput();
            this.listReveal.reset(0.0F);
            this.listReveal.method2810(1.0F);
            this.pickerOpen = false;
         }

         if (var6 || var7) {
            this.ghosts.clear();
            this.knownNodeGlobal.clear();
            this.knownNodeDirect.clear();
            this.knownSiteGlobal.clear();
         }

         if (var9 && this.getActiveTab() == 1 && var8.length() >= 2) {
            UserSearchClient.method4760(var8, 8).whenComplete((var1x, var2) -> this.revision++);
         }

         this.deleteMenuMessage = 0L;
         this.invalidate();
         if (this.stickToEnd || var6) {
            this.stickToEnd = true;
            this.scrollToEnd();
         }
      }
   }

   private int countForChange() {
      if (this.getActiveTab() == 1) {
         return siteMode() ? direct().method4857().size() * 131 + direct().method4856().size() : this.conversation.size();
      } else if (siteMode()) {
         return global().method2164().size() * 131 + global().getOnline().size();
      } else {
         CloudConnectionState var1 = state();
         return var1 == null ? 0 : this.countGlobal(var1);
      }
   }

   private int countGlobal(CloudConnectionState var1) {
      Cloud var2 = cloud();
      int var3 = 0;

      for (CloudPackets.Data13 var5 : var1.getArrayDeque()) {
         if (var5.getIntValue() == 0 && (var2 == null || !var2.prefs().method3316(var5.username()))) {
            var3++;
         }
      }

      return var3;
   }

   private String statusForChange() {
      if (siteMode()) {
         return (global().isLive() ? "live" : "poll") + (direct().isLive() ? "+" : "-") + (this.hasPeer() ? this.openPeerUid() : 0L);
      } else {
         CloudConnectionState var1 = state();
         return var1 == null ? "none" : var1.getType().name();
      }
   }

   private void fetchRoster() {
      StoreApi var1 = store();
      if (var1 != null) {
         var1.method1963().whenComplete((var1x, var2) -> {
            if (var2 == null) {
               this.online = Collections.unmodifiableList(new ArrayList<>(var1x));
               this.error = "";
            } else {
               this.error = shortError(var2);
            }

            this.revision++;
         });
      }
   }

   private void fetchConversation(long var1) {
      StoreApi var3 = store();
      if (var3 != null) {
         var3.method1964(var1, 200).whenComplete((var3x, var4) -> {
            if (var4 == null) {
               if (!var3x.isEmpty()) {
                  this.rememberPreview(var1, var3x.get(var3x.size() - 1).text());
               }

               if (var1 != this.peerUid) {
                  return;
               }

               synchronized (this.conversationLock) {
                  this.conversation = Collections.unmodifiableList(new ArrayList<>(var3x));
               }

               this.error = "";
            } else {
               if (var1 != this.peerUid) {
                  return;
               }

               this.error = shortError(var4);
            }

            this.revision++;
         });
      }
   }

   private void rememberPreview(long var1, String var3) {
      if (this.previews.size() >= 64 && !this.previews.containsKey(var1)) {
         this.previews.keySet().stream().findFirst().ifPresent(this.previews::remove);
      }

      this.previews.put(var1, var3);
   }

   private List<CloudPackets.Data13> globalMessages(CloudConnectionState var1) {
      Cloud var2 = cloud();
      ArrayList<CloudPackets.Data13> var3 = new ArrayList<>();

      for (CloudPackets.Data13 var5 : var1.getArrayDeque()) {
         if (var5.getIntValue() == 0 && (var2 == null || !var2.prefs().method3316(var5.username()))) {
            var3.add(var5);
         }
      }

      return var3;
   }

   @Override
   public void build() {
      this.dayBroke = false;
      this.tabs(new String[]{"gui.chat.tab.global", "gui.chat.tab.direct"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.CLOUD, MenuGlyph.FRIENDS});
      if (this.getActiveTab() == 1) {
         this.buildDirect();
         this.buildDirectAside();
      } else {
         this.buildGlobal();
         this.buildGlobalAside();
      }
   }

   private void buildGlobal() {
      if (siteMode()) {
         this.buildSiteGlobal();
      } else {
         CloudConnectionState var1 = state();
         Cloud var2 = cloud();
         if (var1 != null && var2 != null) {
            long var3 = var2.getLongValue();
            List<CloudPackets.Data13> var5 = this.globalMessages(var1);
            CloudPackets.Data13 var6 = var5.isEmpty() ? null : (CloudPackets.Data13)var5.get(var5.size() - 1);
            LinkedHashMap var7 = new LinkedHashMap();

            for (Object var9_o : (Iterable<?>)(Object)(var5)) {
               CloudPackets.Data13 var9 = (CloudPackets.Data13)var9_o;
               if (var9.getLongValue3() > 0L) {
                  var7.put(var9.getLongValue3(), var9);
               }
            }

            int var23 = this.ghosts.size();
            this.knownNodeGlobal.forEach((var4, var5x) -> {
               if (!var7.containsKey(var4)) {
                  this.ghosts.add(new CloudChatSectionPanel.Ghost(0, var5x.username(), "", var5x.text(), var5x.time(), var3 != 0L && var5x.uid() == var3));
               }
            });
            this.forgetGone(var23);
            this.knownNodeGlobal.clear();
            this.knownNodeGlobal.putAll(var7);
            String var24 = null;
            boolean var10 = false;
            long var11 = 0L;

            for (Object var14 : this.mergeWithGhosts(var5, 0)) {
               if (var14 instanceof CloudChatSectionPanel.Ghost var25) {
                  boolean var26 = !var25.mine && (!var25.author.equals(var24) || var10);
                  var11 = this.daySeparator(var11, var25.time);
                  var26 = var26 || this.dayBroke && !var25.mine;
                  this.dayBroke = false;
                  var24 = var25.author;
                  var10 = var25.mine;
                  this.ghostBubble(var25, var26);
               } else {
                  CloudPackets.Data13 var15 = (CloudPackets.Data13)var14;
                  boolean var16 = var3 != 0L && var15.uid() == var3;
                  String var17 = var15.username();
                  boolean var18 = !var16 && (!var17.equals(var24) || var10);
                  var11 = this.daySeparator(var11, var15.time());
                  var18 = var18 || this.dayBroke && !var16;
                  this.dayBroke = false;
                  var24 = var17;
                  var10 = var16;
                  long var19 = var15.uid();
                  long var21 = var15.getLongValue3();
                  this.bubble(
                     var17,
                     var19,
                     "",
                     var15.text(),
                     var15.time(),
                     var16,
                     var18,
                     var15 == var6,
                     false,
                     var16 ? null : () -> this.selectFromChat(var19, var17),
                     var16 && var21 > 0L ? () -> this.openDeleteMenu(var21) : null
                  );
               }
            }
         } else {
            this.knownNodeGlobal.clear();
         }
      }
   }

   private void forgetGone(int var1) {
      if (this.ghosts.size() - var1 > 4) {
         this.ghosts.subList(var1, this.ghosts.size()).clear();
      }
   }

   private void buildSiteGlobal() {
      CloudChatClient var1 = global();
      long var2 = var1.getLongValue();
      List<CloudChatClient.Data3> var4 = var1.method2164();
      CloudChatClient.Data3 var5 = var1.getData3();
      if (var5 != null) {
         this.pinnedBar(var5);
      }

      CloudChatClient.Data3 var6 = var4.isEmpty() ? null : (CloudChatClient.Data3)var4.get(var4.size() - 1);
      LinkedHashMap var7 = new LinkedHashMap();

      for (Object var9_o : (Iterable<?>)(Object)(var4)) {
         CloudChatClient.Data3 var9 = (CloudChatClient.Data3)var9_o;
         if (var9.getLongValue() > 0L) {
            var7.put(var9.getLongValue(), var9);
         }
      }

      int var24 = this.ghosts.size();
      this.knownSiteGlobal
         .forEach(
            (var4x, var5x) -> {
               if (!var7.containsKey(var4x)) {
                  this.ghosts
                     .add(
                        new CloudChatSectionPanel.Ghost(
                           2, var5x.name(), var5x.role(), var5x.body(), var5x.getLongValue3(), var2 != 0L && var5x.getLongValue2() == var2
                        )
                     );
               }
            }
         );
      this.forgetGone(var24);
      this.knownSiteGlobal.clear();
      this.knownSiteGlobal.putAll(var7);
      String var25 = null;
      boolean var10 = false;
      long var11 = 0L;

      for (Object var14 : this.mergeWithGhosts(var4, 2)) {
         if (var14 instanceof CloudChatSectionPanel.Ghost var26) {
            boolean var27 = !var26.mine && (!var26.author.equals(var25) || var10);
            var11 = this.daySeparator(var11, var26.time);
            var27 = var27 || this.dayBroke && !var26.mine;
            this.dayBroke = false;
            var25 = var26.author;
            var10 = var26.mine;
            this.ghostBubble(var26, var27);
         } else {
            CloudChatClient.Data3 var15 = (CloudChatClient.Data3)var14;
            boolean var16 = var2 != 0L && var15.getLongValue2() == var2;
            boolean var17 = !var16 && (!var15.name().equals(var25) || var10);
            var11 = this.daySeparator(var11, var15.getLongValue3());
            var17 = var17 || this.dayBroke && !var16;
            this.dayBroke = false;
            var25 = var15.name();
            var10 = var16;
            long var18 = var15.getLongValue2();
            String var20 = var15.name();
            long var21 = var15.getLongValue();
            boolean var23 = var16 || global().isFlag();
            this.bubble(
               var20,
               var18,
               var15.role(),
               var15.body(),
               var15.getLongValue3(),
               var16,
               var17,
               var15 == var6,
               false,
               var16 ? null : () -> this.selectFromChat(var18, var20),
               var23 ? () -> this.openDeleteMenu(var21) : null
            );
         }
      }
   }

   private void pinnedBar(CloudChatClient.Data3 var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.4F);
      var var3 = wrap(var1.name() + ": " + Coord.method5172(var1.body()).text(), var2, this.mainWidth() - 16.0F - 6.0F - 8.0F);
      float var4 = 6.0F + Math.min(2, var3.size()) * (var2.height() + 1.0F);
      this.custom(
         var4 + 3.0F,
         (var3x, var4x, var5, var6, var7, var8, var9, var10) -> {
            var3x.drawRoundedRect(
               var6 + 8.0F,
               var7,
               var8 - 16.0F,
               var4,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F),
               var10.getPrimaryColor().getZenithColor().method7294(var9 * 0.16F)
            );
            MenuGlyph.PIN.draw(var3x, var6 + 8.0F + 3.0F, var7 + 3.0F, var10.getPrimaryColor().getZenithColor().method7294(var9));
            float var11 = var7 + 3.0F;

            for (int var12 = 0; var12 < var3.size() && var12 < 2; var12++) {
               var3x.drawText(
                  var2, (String)var3.get(var12), var6 + 8.0F + 3.0F + 6.0F + 4.0F, var11, var10.getTextSecondary().getZenithColor().method7294(var9)
               );
               var11 += var2.height() + 1.0F;
            }
         }
      );
   }

   private void openDeleteMenu(long var1) {
      this.openDeleteMenu(var1, 0L);
   }

   private void openDeleteMenu(long var1, long var3) {
      if (var1 > 0L) {
         this.deleteMenuMessage = var1;
         this.deleteMenuPeer = var3;
         BoundsData var5 = this.lastRightClickBounds();
         this.deleteMenuAnchorTop = var5 == null ? Float.NaN : var5.getFloatValue2();
      }
   }

   private boolean clickDeleteMenu(double var1, double var3) {
      if (this.deleteMenuMessage != 0L && this.deleteMenuBounds != null && this.deleteMenuBounds.contains(var1, var3)) {
         long var5 = this.deleteMenuMessage;
         long var7 = this.deleteMenuPeer;
         this.deleteMenuMessage = 0L;
         this.deleteMenuPeer = 0L;
         if (siteMode()) {
            this.deleteOwnMessage(var5);
         } else if (var7 != 0L) {
            this.deleteNodeDirect(var7, var5);
         } else {
            this.deleteNodeMessage(var5);
         }

         return true;
      } else {
         return false;
      }
   }

   private void deleteNodeMessage(long var1) {
      Cloud var3 = cloud();
      if (var3 != null && var1 > 0L) {
         var3.method7909(var1);
      }
   }

   private void deleteNodeDirect(long var1, long var3) {
      StoreApi var5 = store();
      if (var5 != null && var3 > 0L) {
         var5.method1967(var1, var3).whenComplete((var3x, var4) -> {
            if (var4 == null) {
               synchronized (this.conversationLock) {
                  var var6 = new ArrayList<>(this.conversation);
                  var6.removeIf(var2 -> var2.getLongValue() == var3);
                  this.conversation = Collections.unmodifiableList(var6);
               }
            }

            this.error = var4 == null ? "" : shortError(var4);
            this.revision++;
         });
      }
   }

   private void deleteOwnMessage(long var1) {
      if (siteMode() && var1 > 0L) {
         global().method2169(var1).whenComplete((var1x, var2) -> {
            this.error = var2 == null ? "" : siteError(var2);
            this.revision++;
         });
      }
   }

   private void selectFromChat(long var1, String var3) {
      Cloud var4 = cloud();
      if (var4 == null || var1 != var4.getLongValue()) {
         if (!siteMode() || var1 != global().getLongValue()) {
            if (var1 != this.selectedUid || this.cardClosing) {
               this.cardReveal.reset(0.0F);
               this.cardReveal.method2810(1.0F);
            }

            this.cardClosing = false;
            this.selectedUid = var1;
            this.selectedName = var3;
            CloudConnectionState var5 = state();
            PartyPlayerInfo var6 = var5 == null ? null : var5.method8039(var1);
            this.selectedMcName = var6 == null ? "" : var6.getText2();

            for (StoreApi.Data4 var8 : this.online) {
               if (var8.uid() == var1) {
                  this.selectedMcName = var8.getText2();
                  break;
               }
            }

            this.invalidate();
         }
      }
   }

   private void buildGlobalAside() {
      if (this.selectedUid != 0L) {
         this.buildSelectedAside();
      } else if (siteMode()) {
         this.buildSiteOnlineAside();
      } else {
         this.buildNodeOnlineAside();
      }
   }

   private void buildSelectedAside() {
      Cloud var1 = cloud();
      CloudConnectionState var2 = state();
      boolean var3 = var1 != null && var1.getFriends().method6442(this.selectedName);
      boolean var4 = var2 != null && var2.method8061(this.selectedName);
      this.asideCustom(
         24.0F + GuiStyle.PADDING.intValue(),
         (var1x, var2x, var3x, var4x, var5, var6, var7, var8) -> {
            float var9 = this.cardReveal.method2808(this.cardClosing ? 0.0F : 1.0F);
            float var10 = (1.0F - var9) * 10.0F;
            float var11 = var7 * var9;
            this.avatar(var1x, this.selectedUid, this.selectedName, var4x + 8.0F + var10, var5, 24.0F, var11, var8);
            Font var12 = Fonts.NEW_MEDIUM.getFont(5.6F);
            Font var13 = Fonts.NEW_REGULAR.getFont(4.4F);
            float var14 = var4x + 8.0F + 24.0F + 5.0F + var10;
            float var15 = var6 - 16.0F - 24.0F - 5.0F;
            var1x.drawText(var12, clip(this.selectedName, var12, var15), var14, var5 + 2.0F, var8.getTextEnable().getZenithColor().method7294(var11));
            String var16 = this.selectedMcName.isEmpty() ? t("gui.chat.mcUnknown") : this.selectedMcName;
            var1x.drawText(
               var13, clip(var16, var13, var15), var14, var5 + 2.0F + var12.height() + 3.0F, var8.getTextTertiary().getZenithColor().method7294(var11)
            );
         }
      );
      if (siteMode() || var3 && !var4) {
         this.asideAction("gui.friends.write", MenuGlyph.CHAT, () -> t("gui.chat.write.direct"), () -> {
            this.openPeer(this.selectedUid, this.selectedName);
            this.selectTab(1);
         }, "gui.chat.write.desc");
      }

      if (var4) {
         this.asideInfo("gui.chat.request", () -> t("gui.chat.request.sent"), "gui.chat.request.desc");
      } else if (!var3) {
         this.asideAction("gui.chat.addFriend", MenuGlyph.FRIENDS, () -> t("gui.chat.addFriend.value"), () -> {
            Cloud var1x = cloud();
            boolean var2x = var1x != null && var1x.method7904(0, this.selectedName);
            this.notice = t(var2x ? "gui.chat.addFriend.sent" : "gui.chat.addFriend.already");
            this.noticeAt = System.currentTimeMillis();
            this.invalidate();
         }, "gui.chat.addFriend.desc");
      }

      this.asideAction("gui.chat.close", MenuGlyph.CLOSE, () -> t("gui.chat.close.value"), this::closeCard, "gui.chat.close.desc");
   }

   private void closeCard() {
      if (this.selectedUid != 0L && !this.cardClosing) {
         this.cardClosing = true;
         this.cardReveal.method2810(0.0F);
      }
   }

   private void buildSiteOnlineAside() {
      CloudChatClient var1 = global();
      this.asideCaption("gui.chat.caption.online");
      this.asideInfo("gui.chat.total", () -> String.valueOf(global().getOnline().size()), "gui.chat.online.desc");
      this.asideInfo("gui.chat.messagesTotal", () -> String.valueOf(global().getData2().getIntValue()), "gui.chat.messagesTotal.desc");
      this.asideInfo("gui.chat.link", () -> t(global().isLive() ? "gui.chat.link.live" : "gui.chat.link.slow"), "gui.chat.link.desc");
      if (!var1.error().isEmpty()) {
         this.asideInfo("gui.chat.error", () -> clipText(global().error()), "gui.chat.error.site.desc");
      }

      int var2 = 0;

      for (CloudChatClient.Data var4 : var1.getOnline()) {
         int var5 = var2++;
         long var6 = var4.getLongValue();
         String var8 = var4.name();
         this.asideCustom(24.0F, (var6x, var7, var8x, var9, var10, var11, var12, var13) -> {
            float var14 = this.reveal(var5);
            this.renderMemberRow(var6x, var9 + (1.0F - var14) * 8.0F, var10, var11, var12 * var14, var13, var6, var8, var4.role());
         }, () -> this.selectFromChat(var6, var8), () -> var6 == this.selectedUid);
      }

      if (var1.getOnline().isEmpty()) {
         this.asideInfo("gui.chat.listEmpty", () -> "", "gui.chat.listEmpty.site.desc");
      }
   }

   private void buildNodeOnlineAside() {
      CloudConnectionState var1 = state();
      this.asideCaption("gui.chat.caption.nowInCloud");
      this.asideInfo("gui.chat.total", () -> {
         CloudConnectionState var0 = state();
         return var0 != null && var0.method8035() ? String.valueOf(var0.getIntValue()) : "-";
      }, "gui.chat.total.desc");
      this.asideInfo("gui.chat.ofThemFriends", () -> {
         CloudConnectionState var0 = state();
         return var0 == null ? "-" : String.valueOf(var0.method8045());
      }, "gui.chat.ofThemFriends.desc");
      this.asideInfo("gui.chat.site", () -> t("gui.chat.site.absent"), "gui.chat.site.desc");
      if (!this.error.isEmpty()) {
         this.asideInfo("gui.chat.error", () -> this.error, "gui.chat.error.list.desc");
      }

      List<StoreApi.Data4> var2 = this.online;
      if (!var2.isEmpty()) {
         this.asideCaption("gui.chat.caption.online");
         Cloud var3 = cloud();
         long var4 = var3 == null ? 0L : var3.getLongValue();
         int var6 = 0;

         for (Object var8_o : (Iterable<?>)(Object)(var2)) {
            StoreApi.Data4 var8 = (StoreApi.Data4)var8_o;
            boolean var9 = var8.uid() == var4;
            int var10 = var6++;
            this.asideCustom(24.0F, (var4x, var5, var6x, var7, var8x, var9x, var10x, var11) -> {
               float var12 = this.reveal(var10);
               this.renderOnlineRow(var4x, var7 + (1.0F - var12) * 8.0F, var8x, var9x, var10x * var12, var11, var8, var9);
            }, () -> this.selectFromChat(var8.uid(), var8.username()), () -> false);
         }
      } else {
         this.asideInfo(var1 != null && var1.method8035() ? "gui.chat.listEmpty" : "gui.chat.noLink", () -> "", "gui.chat.listEmpty.desc");
      }
   }

   private void renderMemberRow(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6, long var7, String var9, String var10) {
      float var11 = var2 + 8.0F;
      this.avatar(var1, var7, var9, var11, var3 + 4.0F, 16.0F, var5, var6);
      Font var12 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var13 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var14 = var11 + 16.0F + 4.0F;
      float var15 = var4 - 16.0F - 16.0F - 4.0F;
      float var16 = var3 + (24.0F - var12.height() - var13.height() - 1.0F) / 2.0F;
      var1.drawText(var12, clip(var9, var12, var15), var14, var16, var6.getTextEnable().getZenithColor().method7294(var5));
      var1.drawText(var13, clip(roleLabel(var10), var13, var15), var14, var16 + var12.height() + 1.0F, roleColor(var10, var6).method7294(var5));
   }

   private void renderOnlineRow(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6, StoreApi.Data4 var7, boolean var8) {
      float var9 = var2 + 8.0F;
      this.avatar(var1, var7.uid(), var7.username(), var9, var3 + 4.0F, 16.0F, var5, var6);
      Font var10 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var11 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var12 = var9 + 16.0F + 4.0F;
      float var13 = var4 - 16.0F - 16.0F - 4.0F;
      float var14 = var3 + (24.0F - var10.height() - var11.height() - 1.0F) / 2.0F;
      var1.drawText(
         var10,
         clip(var7.username(), var10, var13),
         var12,
         var14,
         (var8 ? var6.getPrimaryColor().getZenithColor() : var6.getTextEnable().getZenithColor()).method7294(var5)
      );
      String var15 = var8 ? t("gui.chat.you") : var7.getText2();
      var1.drawText(var11, clip(var15, var11, var13), var12, var14 + var10.height() + 1.0F, var6.getTextTertiary().getZenithColor().method7294(var5));
   }

   private void buildDirect() {
      if (!this.hasPeer()) {
         this.knownNodeDirect.clear();
         this.knownDirectPeer = -1L;
         this.ghosts.removeIf(var0 -> var0.feed == 1);
      } else if (siteMode()) {
         this.buildSiteConversation();
      } else {
         if (this.peerUid != this.knownDirectPeer) {
            this.knownDirectPeer = this.peerUid;
            this.knownNodeDirect.clear();
            this.ghosts.removeIf(var0 -> var0.feed == 1);
         }

         Cloud var1 = cloud();
         long var2 = var1 == null ? 0L : var1.getLongValue();
         List<StoreApi.Data3> var4 = this.conversation;
         StoreApi.Data3 var5 = var4.isEmpty() ? null : (StoreApi.Data3)var4.get(var4.size() - 1);
         LinkedHashMap var6 = new LinkedHashMap();

         for (Object var8_o : (Iterable<?>)(Object)(var4)) {
            StoreApi.Data3 var8 = (StoreApi.Data3)var8_o;
            if (var8.getLongValue() > 0L) {
               var6.put(var8.getLongValue(), var8);
            }
         }

         int var20 = this.ghosts.size();
         this.knownNodeDirect.forEach((var4x, var5x) -> {
            if (!var6.containsKey(var4x)) {
               this.ghosts.add(new CloudChatSectionPanel.Ghost(1, var5x.getText(), "", var5x.text(), var5x.time(), var5x.getLongValue2() == var2));
            }
         });
         this.forgetGone(var20);
         this.knownNodeDirect.clear();
         this.knownNodeDirect.putAll(var6);
         String var21 = null;
         boolean var9 = false;
         long var10 = 0L;

         for (Object var13 : this.mergeWithGhosts(var4, 1)) {
            if (var13 instanceof CloudChatSectionPanel.Ghost var22) {
               boolean var23 = !var22.mine && (!var22.author.equals(var21) || var9);
               var10 = this.daySeparator(var10, var22.time);
               var23 = var23 || this.dayBroke && !var22.mine;
               this.dayBroke = false;
               var21 = var22.author;
               var9 = var22.mine;
               this.ghostBubble(var22, var23);
            } else {
               StoreApi.Data3 var14 = (StoreApi.Data3)var13;
               boolean var15 = var14.getLongValue2() == var2;
               String var16 = var14.getText();
               boolean var17 = !var15 && (!var16.equals(var21) || var9);
               var10 = this.daySeparator(var10, var14.time());
               var17 = var17 || this.dayBroke && !var15;
               this.dayBroke = false;
               var21 = var16;
               var9 = var15;
               long var18 = var14.getLongValue();
               this.bubble(
                  var16,
                  var15 ? 0L : this.peerUid,
                  "",
                  var14.text(),
                  var14.time(),
                  var15,
                  var17,
                  var14 == var5,
                  false,
                  null,
                  var15 && var18 > 0L ? () -> this.openDeleteMenu(var18, this.peerUid) : null
               );
            }
         }
      }
   }

   private void buildSiteConversation() {
      CloudDmClient var1 = direct();
      long var2 = var1.getLongValue();
      List<CloudDmClient.Data> var4 = var1.method4857();
      CloudDmClient.Data var5 = var4.isEmpty() ? null : (CloudDmClient.Data)var4.get(var4.size() - 1);
      long var6 = 0L;
      boolean var8 = false;
      String var9 = null;

      for (Object var11_o : (Iterable<?>)(Object)(var4)) {
         CloudDmClient.Data var11 = (CloudDmClient.Data)var11_o;
         boolean var12 = var11.getLongValue2() != var2;
         String var13 = var12 ? "" : var1.getPeerName();
         boolean var14 = !var12 && (!var13.equals(var9) || var8);
         var6 = this.daySeparator(var6, var11.getLongValue4());
         var14 = var14 || this.dayBroke && !var12;
         this.dayBroke = false;
         var9 = var13;
         var8 = var12;
         this.bubble(var13, var12 ? 0L : var2, "", var11.body(), var11.getLongValue4(), var12, var14, var11 == var5, var12 && var11.read(), null);
      }
   }

   private void buildDirectAside() {
      if (siteMode()) {
         this.buildSiteThreads();
      } else {
         this.buildNodeContacts();
      }
   }

   private void buildSiteThreads() {
      CloudDmClient var1 = direct();
      String var2 = this.searchQuery() == null ? "" : this.searchQuery().trim();
      if (var2.length() >= 2) {
         List<UserSearchClient.Data> var3 = UserSearchClient.method4758(var2);
         if (var3 != null && !var3.isEmpty()) {
            this.asideCaption("gui.chat.caption.firstMessage");
            int var4 = 0;

            for (Object var6_o : (Iterable<?>)(Object)(var3)) {
               UserSearchClient.Data var6 = (UserSearchClient.Data)var6_o;
               int var7 = var4++;
               long var8 = var6.uid();
               String var10 = var6.name();
               this.asideCustom(
                  24.0F,
                  (var5, var6x, var7x, var8x, var9x, var10x, var11x, var12x) -> {
                     float var13 = this.reveal(var7);
                     this.renderThreadRow(
                        var5, var6x, var7x, var8x + (1.0F - var13) * 8.0F, var9x, var10x, var11x * var13, var12x, var8, var10, t("gui.chat.startNew"), 0, null
                     );
                  },
                  () -> this.openPeer(var8, var10),
                  () -> var8 == direct().getLongValue()
               );
            }
         } else if (UserSearchClient.method4759(var2)) {
            this.asideInfo("gui.chat.search", () -> t("gui.chat.search.short"), "gui.chat.search.desc");
         } else if (var3 == null) {
            this.asideInfo("gui.chat.search", () -> t("gui.chat.search.wait"), "gui.chat.search.desc");
         } else {
            this.asideInfo("gui.chat.search", () -> t("gui.chat.search.none"), "gui.chat.search.desc");
         }
      }

      this.asideCaption("gui.chat.caption.threads");
      if (!var1.error().isEmpty()) {
         this.asideInfo("gui.chat.error", () -> clipText(direct().error()), "gui.chat.error.site.desc");
      }

      int var16 = 0;
      boolean var17 = false;

      for (CloudDmClient.Data2 var19 : var1.method4856()) {
         if (var2.isEmpty() || var19.getText().toLowerCase(Locale.ROOT).contains(var2.toLowerCase(Locale.ROOT))) {
            var17 = true;
            long var20 = var19.getLongValue();
            String var9 = var19.getText();
            String var21 = var19.isFlag() ? t("gui.chat.mePrefix") + " " + previewOf(var19.getText3()) : previewOf(var19.getText3());
            int var11 = var19.getIntValue();
            long var12 = var19.getLongValue2();
            String var14 = var12 > 0L ? TIME.format(new Date(var12)) : null;
            int var15;
            int var22 = var15 = var16++;
            this.asideCustom(
               24.0F,
               (var8x, var9x, var10x, var11x, var12x, var13, var14x, var15x) -> {
                  float var16x = this.reveal(var15);
                  this.renderThreadRow(
                     var8x, var9x, var10x, var11x + (1.0F - var16x) * 8.0F, var12x, var13, var14x * var16x, var15x, var20, var9, var21, var11, var14
                  );
               },
               () -> this.openPeer(var20, var9),
               () -> var20 == direct().getLongValue()
            );
         }
      }

      if (!var17 && var2.isEmpty()) {
         this.asideInfo("gui.chat.emptyThreads", () -> "", "gui.chat.emptyThreads.desc");
      }
   }

   private void buildNodeContacts() {
      Cloud var1 = cloud();
      CloudConnectionState var2 = state();
      if (var1 != null && var2 != null) {
         this.asideCaption("gui.chat.caption.contacts");
         this.asideInfo("gui.chat.threads", () -> t("gui.chat.threads.gameOnly"), "gui.chat.threads.desc");
         if (!this.error.isEmpty()) {
            this.asideInfo("gui.chat.error", () -> this.error, "gui.chat.error.list.desc");
         }

         boolean var3 = false;
         int var4 = 0;

         for (String var6 : var1.getFriends().all()) {
            if (!var2.method8061(var6)) {
               long var7 = var2.uidOf(var6);
               if (var7 <= 0L && var1.prefs() != null) {
                  var7 = var1.prefs().method3311(var6);
               }

               if (var7 > 0L) {
                  long var9 = var7;
                  var3 = true;
                  PartyPlayerInfo var11 = var2.method8044(var6);
                  String var12 = var11 == null ? var6 : var11.username();
                  boolean var13 = var11 != null;
                  int var14 = var4++;
                  this.asideCustom(
                     24.0F,
                     (var6x, var7x, var8, var9x, var10, var11x, var12x, var13x) -> {
                        float var14x = this.reveal(var14);
                        this.renderContact(
                           var6x, var7x, var8, var9x + (1.0F - var14x) * 8.0F, var10, var11x, var12x * var14x, var13x, var9, var12, var13, var9 == this.peerUid
                        );
                     },
                     () -> this.openPeer(var9, var12),
                     () -> var9 == this.peerUid
                  );
               }
            }
         }

         if (!var3) {
            this.asideInfo("gui.chat.emptyContacts", () -> "", "gui.chat.emptyContacts.desc");
         }
      }
   }

   private void renderThreadRow(
      MenuDrawContext var1,
      int var2,
      int var3,
      float var4,
      float var5,
      float var6,
      float var7,
      ZenithStyle var8,
      long var9,
      String var11,
      String var12,
      int var13,
      String var14
   ) {
      float var15 = this.selectFade
         .computeIfAbsent(var9, var0 -> new Animation(160L, 0.0F, Easing.INSTANCE12))
         .method2808(var9 == this.openPeerUid() ? 1.0F : 0.0F);
      float var16 = this.contactHover
         .computeIfAbsent(var9, var0 -> new Animation(130L, 0.0F, Easing.INSTANCE12))
         .method2808(new BoundsData(var4, var5, var6, 24.0F).contains(var2, var3) ? 1.0F : 0.0F);
      float var17 = Math.max(var15, var16 * 0.45F);
      if (var17 > 0.002F) {
         var1.drawRoundedRect(
            var4 + 8.0F - 2.0F,
            var5 + 1.0F,
            var6 - 16.0F + 4.0F,
            22.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F),
            var8.getDisableActiveBg().getZenithColor().method7294(var7 * var17)
         );
      }

      float var18 = var4 + 8.0F;
      this.avatar(var1, var9, var11, var18, var5 + 4.0F, 16.0F, var7, var8);
      Font var19 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var20 = Fonts.NEW_REGULAR.getFont(4.2F);
      Font var21 = Fonts.NEW_REGULAR.getFont(3.8F);
      float var22 = var18 + 16.0F + 4.0F;
      float var23 = var4 + var6 - 8.0F;
      float var24 = var5 + (24.0F - var19.height() - var20.height() - 1.0F) / 2.0F;
      float var25 = var23 - var22;
      if (var14 != null) {
         float var26 = var21.width(var14);
         var1.drawText(var21, var14, var23 - var26, var24 + 1.0F, var8.getTextTertiary().getZenithColor().method7294(var7 * 0.8F));
         var25 -= var26 + 3.0F;
      }

      var1.drawText(var19, clip(var11, var19, var25), var22, var24, var8.getTextEnable().getZenithColor().method7294(var7));
      float var34 = var23 - var22;
      float var27 = this.unreadPop.computeIfAbsent(var9, var0 -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(var13 > 0 ? 1.0F : 0.0F);
      if (var27 > 0.01F) {
         Font var28 = Fonts.NEW_MEDIUM.getFont(4.2F);
         String var29 = var13 > 99 ? "99+" : String.valueOf(var13);
         float var30 = Math.max(8.0F, var28.width(var29) + 5.0F);
         float var31 = 7.0F;
         float var32 = var24 + var19.height() + 1.0F;
         float var33 = 0.6F + 0.4F * var27;
         var1.pushMatrix();
         var1.getMatrices().translate(var23 - var30 / 2.0F, var32 + var31 / 2.0F);
         var1.getMatrices().scale(var33, var33);
         var1.drawRoundedRect(
            -var30 / 2.0F,
            -var31 / 2.0F,
            var30,
            var31,
            ProxyDotRadiusData.method8069(var31 / 2.0F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * var27)
         );
         var1.drawText(var28, var29, -var28.width(var29) / 2.0F, -var28.height() / 2.0F, new ZenithColor(255, 255, 255).method7294(var7 * var27));
         var1.popMatrix();
         var34 -= var30 * var27 + 3.0F;
      }

      var1.drawText(
         var20,
         clip(var12, var20, var34),
         var22,
         var24 + var19.height() + 1.0F,
         var8.getTextTertiary().getZenithColor().method7295(var8.getTextSecondary().getZenithColor(), var27).method7294(var7)
      );
   }

   private void renderContact(
      MenuDrawContext var1,
      int var2,
      int var3,
      float var4,
      float var5,
      float var6,
      float var7,
      ZenithStyle var8,
      long var9,
      String var11,
      boolean var12,
      boolean var13
   ) {
      float var14 = this.selectFade.computeIfAbsent(var9, var0 -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(var13 ? 1.0F : 0.0F);
      float var15 = this.contactHover
         .computeIfAbsent(var9, var0 -> new Animation(130L, 0.0F, Easing.INSTANCE12))
         .method2808(new BoundsData(var4, var5, var6, 24.0F).contains(var2, var3) ? 1.0F : 0.0F);
      float var16 = Math.max(var14, var15 * 0.45F);
      if (var16 > 0.002F) {
         var1.drawRoundedRect(
            var4 + 8.0F - 2.0F,
            var5 + 1.0F,
            var6 - 16.0F + 4.0F,
            22.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F),
            var8.getDisableActiveBg().getZenithColor().method7294(var7 * var16)
         );
      }

      float var17 = var4 + 8.0F;
      this.avatar(var1, var9, var11, var17, var5 + 4.0F, 16.0F, var7, var8);
      float var18 = this.onlineFade.computeIfAbsent(var9, var0 -> new Animation(300L, 0.0F, Easing.INSTANCE13)).method2808(var12 ? 1.0F : 0.0F);
      if (var18 > 0.01F) {
         float var19 = 4.0F * var18;
         float var20 = var17 + 16.0F - 4.0F + (4.0F - var19) / 2.0F;
         float var21 = var5 + 20.0F - 4.0F + (4.0F - var19) / 2.0F;
         var1.drawRoundedRect(
            var20, var21, var19, var19, ProxyDotRadiusData.method8069(var19 / 2.0F), var8.getFriendColor().getZenithColor().method7294(var7 * var18)
         );
      }

      Font var25 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var26 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var27 = var17 + 16.0F + 4.0F;
      float var22 = var6 - 16.0F - 16.0F - 4.0F;
      float var23 = var5 + (24.0F - var25.height() - var26.height() - 1.0F) / 2.0F;
      var1.drawText(var25, clip(var11, var25, var22), var27, var23, var8.getTextEnable().getZenithColor().method7294(var7));
      String var24 = this.previews.getOrDefault(var9, t(var12 ? "gui.friends.status.online" : "gui.friends.status.offline"));
      var1.drawText(var26, clip(previewOf(var24), var26, var22), var27, var23 + var25.height() + 1.0F, var8.getTextTertiary().getZenithColor().method7294(var7));
   }

   public boolean openDirectWith(String var1) {
      if (var1 != null && !var1.isBlank()) {
         CloudConnectionState var2 = state();
         long var3 = var2 == null ? 0L : var2.uidOf(var1);
         if (var3 <= 0L) {
            Cloud var5 = cloud();
            var3 = var5 != null && var5.prefs() != null ? var5.prefs().method3311(var1) : 0L;
         }

         if (var3 <= 0L) {
            return false;
         } else {
            this.openPeer(var3, var1);
            this.selectTab(1);
            return true;
         }
      } else {
         return false;
      }
   }

   private void openPeer(long var1, String var3) {
      if (siteMode()) {
         direct().method4865(var1, var3);
      } else {
         if (this.peerUid == var1) {
            return;
         }

         this.peerUid = var1;
         this.peerName = var3;
         this.clearConversation();
         this.polledPeer = -1L;
      }

      this.stickToEnd = true;
      this.pickerOpen = false;
      this.focusInput();
      this.invalidate();
   }

   private void clearConversation() {
      synchronized (this.conversationLock) {
         this.conversation = List.of();
      }
   }

   private void focusInput() {
      this.blurred = false;
      if (this.input != null) {
         this.input.setSelected(true);
      } else {
         this.wantFocus = true;
      }
   }

   @Override
   public float mainHeaderHeight() {
      boolean var1 = this.getActiveTab() == 1 && this.hasPeer();
      if (var1) {
         this.barUid = this.openPeerUid();
         this.barName = this.openPeerName();
      }

      return 24.0F * this.peerBarAnimation.method2808(var1 ? 1.0F : 0.0F);
   }

   @Override
   public void renderMainHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      long var9 = this.barUid;
      String var11 = this.barName;
      float var12 = this.peerBarAnimation.getValue();
      var7 *= var12;
      var5 -= (1.0F - var12) * 4.0F;
      this.avatar(var1, var9, var11, var4 + 8.0F, var5 + 4.0F, 16.0F, var7, var8);
      Font var13 = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font var14 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var15 = var4 + 8.0F + 16.0F + 5.0F;
      float var16 = var5 + (24.0F - var13.height() - var14.height() - 1.0F) / 2.0F;
      float var17 = var6 - 16.0F - 16.0F - 5.0F - 12.0F;
      var1.drawText(var13, clip(var11, var13, var17), var15, var16, var8.getTextEnable().getZenithColor().method7294(var7));
      var1.drawText(
         var14, clip(this.peerUnder(var9), var14, var17), var15, var16 + var13.height() + 1.0F, var8.getTextTertiary().getZenithColor().method7294(var7)
      );
      float var18 = var4 + var6 - 8.0F - 6.0F;
      float var19 = var5 + 9.0F;
      float var20 = this.glyphHover
         .computeIfAbsent("peerClose", var0 -> new Animation(130L, 0.0F, Easing.INSTANCE12))
         .method2808(new BoundsData(var18 - 3.0F, var19 - 3.0F, 12.0F, 12.0F).contains(var2, var3) ? 1.0F : 0.0F);
      MenuGlyph.CLOSE
         .draw(var1, var18, var19, var8.getTextTertiary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var20).method7294(var7));
      var1.drawRect(var4 + 8.0F, var5 + 24.0F - 1.0F, var6 - 16.0F, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F));
   }

   private String peerUnder(long var1) {
      if (siteMode()) {
         var var9 = direct().method4857();
         int var10 = var9.size() * 31 + (var9.isEmpty() ? 0 : (int)((CloudDmClient.Data)var9.get(var9.size() - 1)).getLongValue());
         if (var1 != this.peerUnderFor || var10 != this.peerUnderStamp) {
            this.peerUnderFor = var1;
            this.peerUnderStamp = var10;
            this.peerUnderText = t("gui.chat.peer.site");

            for (int var11 = var9.size() - 1; var11 >= 0; var11--) {
               CloudDmClient.Data var6 = (CloudDmClient.Data)var9.get(var11);
               if (var6.getLongValue2() == var1) {
                  Coord var7 = Coord.method5172(var6.body()).method5179("at");
                  if (var7 != null) {
                     String var8 = ServerNameUtils.method5643(var7.method5169("s", ""), var7.method5169("m", ""));
                     if (!var8.isEmpty()) {
                        this.peerUnderText = var8;
                     }
                  }
                  break;
               }
            }
         }

         return this.peerUnderText;
      } else {
         CloudConnectionState var3 = state();
         PartyPlayerInfo var4 = var3 == null ? null : var3.method8039(var1);
         if (var4 == null) {
            return t("gui.friends.status.offline");
         } else {
            String var5 = var4.getText2();
            return var5.isEmpty() ? t("gui.friends.status.online") : var5;
         }
      }
   }

   private long daySeparator(long var1, long var3) {
      long var5 = dayOf(var3);
      if (var5 != var1 && var3 > 0L) {
         this.dayBroke = true;
         String var7 = this.dayLabel(var3);
         Font var8 = Fonts.NEW_REGULAR.getFont(4.2F);
         this.custom(
            var8.height() + 7.0F,
            (var2, var3x, var4, var5x, var6, var7x, var8x, var9) -> var2.drawText(
               var8, var7, var5x + (var7x - var8.width(var7)) / 2.0F, var6 + 3.0F, var9.getTextTertiary().getZenithColor().method7294(var8x * 0.8F)
            )
         );
         return var5;
      } else {
         return var1 == 0L ? var5 : var1;
      }
   }

   private static long dayOf(long var0) {
      if (var0 <= 0L) {
         return 0L;
      } else {
         LocalDate var2 = LocalDate.ofInstant(Instant.ofEpochMilli(var0), ZoneId.systemDefault());
         return var2.getYear() * 1000L + var2.getDayOfYear();
      }
   }

   private String dayLabel(long var1) {
      long var3 = dayOf(System.currentTimeMillis());
      long var5 = dayOf(var1);
      if (var5 == var3) {
         return t("gui.chat.today");
      } else if (var5 == var3 - 1L) {
         return t("gui.chat.yesterday");
      } else {
         String var7 = Zenith.getInstance().getLocalization().getLanguageName();
         return DAY.computeIfAbsent(var7, var0 -> new SimpleDateFormat("d MMMM", Locale.forLanguageTag(var0))).format(new Date(var1));
      }
   }

   private void bubble(String var1, long var2, String var4, String var5, long var6, boolean var8, boolean var9, boolean var10, boolean var11, Runnable var12) {
      this.bubble(var1, var2, var4, var5, var6, var8, var9, var10, var11, var12, null);
   }

   private void bubble(
      String var1, long var2, String var4, String var5, long var6, boolean var8, boolean var9, boolean var10, boolean var11, Runnable var12, Runnable var13
   ) {
      this.bubble(var1, var2, var4, var5, var6, var8, var9, var10, var11, var12, var13, 1.0F);
   }

   private void bubble(
      String var1,
      long var2,
      String var4,
      String var5,
      long var6,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      Runnable var12,
      Runnable var13,
      float var14
   ) {
      Font var15 = Fonts.NEW_MEDIUM.getFont(4.6F);
      Font var16 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var17 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var18 = this.mainWidth();
      CloudChatSectionPanel.BubbleKey var19 = new CloudChatSectionPanel.BubbleKey(
         var1, var2, var4, var5, var6, var8, var9, var10, var11, var18, Zenith.getInstance().getLocalization().getLanguageName(), var15, var16, var17
      );
      CloudChatSectionPanel.BubbleLayout var20 = this.bubbleLayouts.get(var19);
      if (var20 == null) {
         var20 = this.layoutBubble(var1, var2, var4, var5, var6, var8, var9, var10, var11, var18, var15, var16, var17);
         this.bubbleLayouts.put(var19, var20);
      }

      CloudChatSectionPanel.Bubble var21 = var20.shape();
      float var22 = var21.height();
      SectionPanel.AsideRenderer var23 = (var3, var4x, var5x, var6x, var7, var8x, var9x, var10x) -> this.renderBubble(
         var3, var6x, var7, var8x, var9x * var14, var10x, var21
      );
      float var24 = (var22 + 3.0F) * var14;
      if (var14 < 0.999F) {
         this.custom(var24, var23);
      } else {
         Coord var25 = var20.coord();
         if (var25 != null) {
            this.custom(var24, var23, () -> this.markCoords(var25, var1), () -> var2 == this.selectedUid, var13 != null ? var13 : () -> this.copyCoords(var25));
         } else {
            if (var12 == null && var13 == null) {
               this.custom(var24, var23);
            } else {
               this.custom(var24, var23, var12, () -> var2 == this.selectedUid, var13);
            }
         }
      }
   }

   private CloudChatSectionPanel.BubbleLayout layoutBubble(
      String var1,
      long var2,
      String var4,
      String var5,
      long var6,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      float var12,
      Font var13,
      Font var14,
      Font var15
   ) {
      Coord.Data var16 = Coord.method5172(var5);
      ArrayList<Coord> var17 = new ArrayList();

      for (Coord var19 : var16.getItems()) {
         if (ChatCards.isCard(var19)) {
            var17.add(var19);
         }
      }

      Coord var34 = var16.method5179("at");
      String var35 = var34 == null ? "" : ServerNameUtils.method5643(var34.method5169("s", ""), var34.method5169("m", ""));
      String var20 = var8 ? "" : roleLabel(var4);
      float var21 = var8 ? 0.0F : 20.0F;
      float var22 = Math.max(40.0F, (var12 - 16.0F - var21) * 0.78F);
      List var23 = var16.text().isEmpty() ? new ArrayList() : wrap(var16.text(), var14, var22 - 14.0F);
      if (var16.isBroken()) {
         var23.add(t("gui.chat.attachBroken"));
      }

      String var24 = var6 > 0L ? TIME.format(new Date(var6)) : "";
      float var25 = var15.width(var24) + (var11 ? 8.0F : 0.0F);
      if (!var23.isEmpty() && !var24.isEmpty() && var14.width((String)var23.get(var23.size() - 1)) + 6.0F + var25 > var22 - 14.0F) {
         var23.add("");
      }

      float var26 = 0.0F;

      for (int var27 = 0; var27 < var23.size(); var27++) {
         float var28 = var14.width((String)var23.get(var27));
         if (var27 == var23.size() - 1) {
            var28 += 6.0F + var25;
         }

         var26 = Math.max(var26, var28);
      }

      if (var23.isEmpty()) {
         var26 = Math.max(var26, var25);
      }

      for (Object var39_o : (Iterable<?>)(Object)(var17)) {
         Coord var39 = (Coord)var39_o;
         var26 = Math.max(var26, ChatCards.width(var39));
      }

      float var38 = 0.0F;
      if (var9) {
         var38 = var13.width(var1);
         if (!var20.isEmpty()) {
            var38 += 4.0F + Fonts.NEW_REGULAR.getFont(3.8F).width(var20);
         }
      }

      if (!var35.isEmpty()) {
         var38 = Math.max(var38, Fonts.NEW_REGULAR.getFont(3.8F).width(var35) + 8.0F);
      }

      var26 = Math.max(var26, var38);
      float var40 = var22;

      for (Object var30_o : (Iterable<?>)(Object)(var17)) {
         Coord var30 = (Coord)var30_o;
         var40 = Math.max(var40, ChatCards.width(var30) + 14.0F);
      }

      boolean var41 = var23.isEmpty() && var17.size() == 1 && var35.isEmpty() && ((Coord)var17.get(0)).kind().equals("st");
      float var42 = var41 ? Math.max(ChatCards.width((Coord)var17.get(0)), var38) : Math.min(var40, var26 + 14.0F);
      float var31 = 0.0F;

      for (Object var33_o : (Iterable<?>)(Object)(var17)) {
         Coord var33 = (Coord)var33_o;
         var31 += ChatCards.height(var33) + 2.0F;
      }

      float var43 = var41
         ? (var9 ? var13.height() + 2.0F : 0.0F) + var31 + var15.height() + 1.0F
         : 10.0F
            + (var9 ? var13.height() + 2.0F : 0.0F)
            + (var35.isEmpty() ? 0.0F : Fonts.NEW_REGULAR.getFont(3.8F).height() + 2.0F)
            + (var23.isEmpty() ? 0.0F : var23.size() * (var14.height() + 1.5F) - 1.5F)
            + var31;
      if (!var41 && var23.isEmpty() && var17.isEmpty()) {
         var43 += var14.height();
      }

      CloudChatSectionPanel.Bubble var44 = new CloudChatSectionPanel.Bubble(
         var1, var2, var20, var35, (List<String>)var23, var17, var24, var11, var8, var9, var10, var41, var42, var43, var13, var14, var15
      );
      return new CloudChatSectionPanel.BubbleLayout(var44, var16.method5179("coord"));
   }

   private List<Object> mergeWithGhosts(List<? extends Object> var1, int var2) {
      boolean var3 = false;

      for (CloudChatSectionPanel.Ghost var5 : this.ghosts) {
         if (var5.feed == var2) {
            var3 = true;
            break;
         }
      }

      if (!var3) {
         return new ArrayList<>(var1);
      } else {
         ArrayList<Object> var7 = new ArrayList<>(var1);

         for (CloudChatSectionPanel.Ghost var6 : this.ghosts) {
            if (var6.feed == var2) {
               var7.add(var6);
            }
         }

         var7.sort(Comparator.comparingLong(CloudChatSectionPanel::timeOf));
         return var7;
      }
   }

   private static long timeOf(Object var0) {
      if (var0 instanceof CloudChatSectionPanel.Ghost var4) {
         return var4.time;
      } else if (var0 instanceof CloudPackets.Data13 var3) {
         return var3.time();
      } else if (var0 instanceof CloudChatClient.Data3 var2) {
         return var2.getLongValue3();
      } else {
         return var0 instanceof StoreApi.Data3 var1 ? var1.time() : 0L;
      }
   }

   private void ghostBubble(CloudChatSectionPanel.Ghost var1, boolean var2) {
      this.bubble(var1.author, 0L, var1.role, var1.body, var1.time, var1.mine, var2, false, false, null, null, var1.factor());
   }

   private void markCoords(Coord var1, String var2) {
      String var3 = CloudWaypointsModule.INSTANCE
         .method6402(
            var1.method5170("x", 0) + 0.5,
            var1.method5170("y", 0) + 0.5,
            var1.method5170("z", 0) + 0.5,
            var2,
            var1.method5169("n", ""),
            var1.method5169("s", ""),
            var1.method5169("d", "")
         );
      this.notice = t(var3.isEmpty() ? "gui.chat.marked" : var3);
      this.noticeAt = System.currentTimeMillis();
   }

   private void copyCoords(Coord var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null && var2.keyboard != null) {
         var2.keyboard.setClipboard(ChatCards.coordText(var1));
         this.notice = t("gui.chat.coordsCopied");
         this.noticeAt = System.currentTimeMillis();
      }
   }

   private void renderBubble(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6, CloudChatSectionPanel.Bubble var7) {
      float var8 = var7.newest() ? this.messageArrival.method2808(1.0F) : 1.0F;
      float var9 = (1.0F - var8) * (var7.mine() ? 12.0F : -12.0F);
      var5 *= 0.25F + 0.75F * var8;
      float var10 = var7.mine() ? 0.0F : 20.0F;
      float var11 = (var7.mine() ? var2 + var4 - 8.0F - var7.width() : var2 + 8.0F + var10) + var9;
      if (!var7.mine() && var7.showName() && var7.authorUid() > 0L) {
         this.avatar(var1, var7.authorUid(), var7.author(), var2 + 8.0F + var9, var3, 16.0F, var5, var6);
      }

      ProxyDotRadiusData var12 = var7.mine() ? new ProxyDotRadiusData(5.0F, 5.0F, 1.5F, 5.0F) : new ProxyDotRadiusData(5.0F, 5.0F, 5.0F, 1.5F);
      ZenithColor var13 = var7.mine()
         ? var6.getPrimaryColor().getZenithColor().method7294(var5 * 0.3F)
         : var6.getSurfaceEnableBackground().getZenithColor().method7294(var5 * 2.2F);
      if (!var7.bare()) {
         var1.drawRoundedRectBatched(var11, var3, var7.width(), var7.height(), var12, var13);
         var1.flushRoundedRects();
      }

      float var14 = var7.bare() ? var3 : var3 + 5.0F;
      float var15 = var11 + (var7.bare() ? 0.0F : 7.0F);
      if (var7.showName()) {
         var1.drawText(var7.nameFont(), var7.author(), var15, var14, var6.getPrimaryColor().getZenithColor().method7294(var5));
         if (!var7.badge().isEmpty()) {
            Font var16 = Fonts.NEW_REGULAR.getFont(3.8F);
            var1.drawText(
               var16, var7.badge(), var15 + var7.nameFont().width(var7.author()) + 4.0F, var14 + 0.6F, roleColor(var7.badge(), var6).method7294(var5 * 0.9F)
            );
         }

         var14 += var7.nameFont().height() + 2.0F;
      }

      if (!var7.where().isEmpty()) {
         Font var19 = Fonts.NEW_REGULAR.getFont(3.8F);
         MenuGlyph.WORLD.draw(var1, var11 + 7.0F, var14 - 0.5F, var6.getTextTertiary().getZenithColor().method7294(var5 * 0.8F));
         var1.drawText(var19, var7.where(), var11 + 7.0F + 6.0F + 2.0F, var14, var6.getTextTertiary().getZenithColor().method7294(var5 * 0.9F));
         var14 += var19.height() + 2.0F;
      }

      for (String var17 : var7.lines()) {
         var1.drawText(var7.textFont(), var17, var11 + 7.0F, var14, var6.getTextEnable().getZenithColor().method7294(var5 * 0.92F));
         var14 += var7.textFont().height() + 1.5F;
      }

      for (Coord var23 : var7.cards()) {
         ChatCards.render(var1, var23, var11 + (var7.bare() ? 0.0F : 7.0F), var14, var5, var6);
         var14 += ChatCards.height(var23) + 2.0F;
      }

      if (!var7.time().isEmpty()) {
         float var22 = var11 + var7.width() - (var7.bare() ? 0.0F : 7.0F) - var7.timeFont().width(var7.time());
         if (var7.read()) {
            MenuGlyph.CHECK
               .draw(
                  var1,
                  var22 - 6.0F - 1.0F,
                  var3 + var7.height() - (var7.bare() ? 0.0F : 5.0F) - 6.0F,
                  var6.getPrimaryColor().getZenithColor().method7294(var5)
               );
         }

         var1.drawText(
            var7.timeFont(),
            var7.time(),
            var22,
            var3 + var7.height() - (var7.bare() ? 0.0F : 5.0F) - var7.timeFont().height() + 0.5F,
            var6.getTextTertiary().getZenithColor().method7294(var5)
         );
      }
   }

   private void avatar(MenuDrawContext var1, long var2, String var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      Identifier var10 = var2 > 0L ? CloudAvatarCache.get(Long.toString(var2)) : null;
      ProxyDotRadiusData var11 = ProxyDotRadiusData.method8069(var7 / 2.0F);
      if (var10 != null) {
         float var14 = this.avatarFade.computeIfAbsent(var2, var0 -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
         if (var14 < 1.0F) {
            var1.drawRoundedRect(var5, var6, var7, var7, var11, var9.getSurfaceEnableBackground().getZenithColor().method7294(var8 * 2.4F * (1.0F - var14)));
         }

         var1.drawRoundedTexture(var10, var5, var6, var7, var7, var11, ZenithColor.INSTANCE.method7294(var8 * var14));
      } else {
         var1.drawRoundedRect(var5, var6, var7, var7, var11, var9.getSurfaceEnableBackground().getZenithColor().method7294(var8 * 2.4F));
         String var12 = var4 != null && !var4.isEmpty() ? var4.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         Font var13 = Fonts.NEW_MEDIUM.getFont(var7 * 0.5F);
         var1.drawText(
            var13,
            var12,
            var5 + (var7 - var13.width(var12)) / 2.0F,
            var6 + (var7 - var13.height()) / 2.0F,
            var9.getTextTertiary().getZenithColor().method7294(var8)
         );
      }
   }

   @Override
   public float footerHeight() {
      return 34.0F + GuiStyle.PADDING.intValue() + this.errorRoom();
   }

   private float errorRoom() {
      return this.error.isEmpty() && this.errorAnimation.getValue() <= 0.01F ? 0.0F : 7.0F;
   }

   @Override
   public void renderFooter(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      if (this.input == null) {
         this.input = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.chat.inputHint"), var6);
      }

      int var9 = siteMode() ? (this.getActiveTab() == 1 ? 1000 : 500) : 256;
      int var10 = siteMode() ? 120 : 0;
      int var11 = var9 - var10 - (this.pendingTag.isEmpty() ? 0 : this.pendingTag.length() + 1);
      this.input.setMaxLength(siteMode() ? Math.max(64, var11) : Math.max(1, var11));
      float var12 = this.errorRoom();
      float var13 = var5 + GuiStyle.PADDING.intValue() + var12 + 7.0F;
      var1.drawRect(var4 + 8.0F, var5, var6 - 16.0F, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F));
      if (!this.error.isEmpty()) {
         this.shownError = this.error;
      }

      float var14 = this.errorAnimation.method2808(this.error.isEmpty() ? 0.0F : 1.0F);
      if (var14 > 0.01F && !this.shownError.isEmpty()) {
         Font var15 = Fonts.NEW_REGULAR.getFont(4.2F);
         var1.drawText(
            var15,
            clip(this.shownError, var15, var6 - 16.0F),
            var4 + 8.0F,
            var5 + GuiStyle.PADDING.intValue() + (1.0F - var14) * 3.0F,
            ERROR_COLOR.method7294(var7 * var14)
         );
      }

      if (this.wantFocus) {
         this.wantFocus = false;
         this.blurred = false;
         this.input.setSelected(true);
      }

      if (!this.blurred && !this.input.isSelected()) {
         this.input.setSelected(true);
      }

      float var29 = this.focusAnimation.method2808(this.input.isSelected() ? 1.0F : 0.0F);
      var1.drawRoundedRectBatched(
         var4 + 8.0F,
         var13 + 2.0F,
         var6 - 16.0F,
         16.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         var8.getPanelLeftBackground().getZenithColor().method7295(var8.getPrimaryColor().getZenithColor(), var29 * 0.18F).method7294(var7)
      );
      var1.flushRoundedRects();
      float var16 = var13 + 7.0F;
      float var17 = var4 + 8.0F + 4.0F;
      this.coordsButton = this.glyphButton(var1, "coords", MenuGlyph.GPS, var17, var16, var2, var3, false, var7, var8);
      var17 += 11.0F;
      this.inventoryButton = this.glyphButton(var1, "inventory", MenuGlyph.CHESTPLATE, var17, var16, var2, var3, false, var7, var8);
      var17 += 11.0F;
      this.stickerButton = this.glyphButton(var1, "stickers", MenuGlyph.STICKER, var17, var16, var2, var3, this.pickerOpen, var7, var8);
      var17 += 12.0F;
      Font var18 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String var19 = this.getActiveTab() == 1 ? (this.hasPeer() ? this.openPeerName() : t("gui.chat.toWhom")) : t("gui.chat.toAll");
      if (!this.notice.isEmpty() && System.currentTimeMillis() - this.noticeAt > 2000L) {
         this.notice = "";
      }

      if (!this.notice.isEmpty()) {
         this.shownNotice = this.notice;
      }

      float var20 = this.noticeAnimation.method2808(this.notice.isEmpty() ? 0.0F : 1.0F);
      Font var21 = Fonts.NEW_REGULAR.getFont(4.4F);
      float var22 = Math.max(var21.width(var19), this.shownNotice.isEmpty() ? 0.0F : var21.width(this.shownNotice) * var20);
      float var23 = var4 + var6 - 8.0F - 4.0F;
      float var24 = var17;
      if (!this.pendingTag.isEmpty()) {
         this.shownLabel = this.pendingLabel;
      }

      float var25 = this.pendingAnimation.method2808(this.pendingTag.isEmpty() ? 0.0F : 1.0F);
      if (var25 > 0.01F && !this.shownLabel.isEmpty()) {
         Font var26 = Fonts.NEW_REGULAR.getFont(4.0F);
         float var27 = var26.width(this.shownLabel) + 6.0F + 9.0F;
         var1.drawRoundedRect(
            var17,
            var13 + 5.0F,
            var27 * var25,
            10.0F,
            ProxyDotRadiusData.method8069(5.0F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * 0.28F * var25)
         );
         float var28 = var25 * var25;
         var1.drawText(
            var26, this.shownLabel, var17 + 4.0F, var13 + (20.0F - var26.height()) / 2.0F, var8.getTextEnable().getZenithColor().method7294(var7 * var28)
         );
         MenuGlyph.CLOSE.draw(var1, var17 + var27 * var25 - 6.0F - 3.0F, var13 + 7.0F, var8.getTextTertiary().getZenithColor().method7294(var7 * var28));
         this.pendingButton = this.pendingTag.isEmpty() ? null : new BoundsData(var17, var13, var27, 20.0F);
         var24 = var17 + (var27 + 4.0F) * var25;
      } else {
         this.pendingButton = null;
      }

      this.input.setFont(var18);
      float var33 = Math.max(var24, var23 - var22 - 6.0F);
      this.input.setWidth(Math.max(20.0F, var33 - var24));
      var1.enableScissor(var24, var13 + 2.0F, var33, var13 + 20.0F - 2.0F);
      this.input
         .method987(
            var1,
            var24,
            var13 + (20.0F - var18.height()) / 2.0F,
            var8.getTextEnable().getZenithColor().method7294(var7),
            var8.getTextTertiary().getZenithColor().method7294(var7)
         );
      var1.disableScissor();
      float var34 = var13 + (20.0F - var21.height()) / 2.0F;
      if (var20 < 0.99F) {
         var1.drawText(
            var21, var19, var23 - var21.width(var19), var34 + var20 * 3.0F, var8.getTextTertiary().getZenithColor().method7294(var7 * (1.0F - var20))
         );
      }

      if (var20 > 0.01F && !this.shownNotice.isEmpty()) {
         var1.drawText(
            var21,
            this.shownNotice,
            var23 - var21.width(this.shownNotice),
            var34 - (1.0F - var20) * 3.0F,
            var8.getPrimaryColor().getZenithColor().method7294(var7 * var20)
         );
      }

      this.pickerAt = new BoundsData(var4, var13, var6, var7);
   }

   @Override
   public void renderFloating(MenuDrawContext var1, int var2, int var3, float var4, ZenithStyle var5) {
      if (var5 != null) {
         BoundsData var6 = this.pickerAt;
         float var7 = this.pickerAnimation.method2808(this.pickerOpen ? 1.0F : 0.0F);
         if (var6 != null && !(var7 <= 0.01F)) {
            ScissorFlusher.flush();
            this.renderPicker(var1, var2, var3, var6.getFloatValue(), var6.getFloatValue2(), var6.width(), var6.height(), var5, var7);
         } else {
            this.pickerBounds = null;
         }

         if (!this.ghosts.isEmpty()) {
            long var8 = System.currentTimeMillis();
            this.ghosts.removeIf(var2x -> var8 - var2x.startedAt >= 240L);
            this.invalidate();
         }

         this.renderDeleteMenu(var1, var2, var3, var4, var5);
      }
   }

   private void renderDeleteMenu(MenuDrawContext var1, int var2, int var3, float var4, ZenithStyle var5) {
      float var6 = this.deleteMenuAnimation.method2808(this.deleteMenuMessage != 0L ? 1.0F : 0.0F);
      BoundsData var7 = this.mainViewport();
      if (!(var6 <= 0.01F) && var7 != null) {
         ScissorFlusher.flush();
         Font var8 = Fonts.NEW_MEDIUM.getFont(4.6F);
         String var9 = t("gui.chat.delete");
         float var10 = var8.width(var9) + 18.0F;
         float var11 = 15.0F;
         float var12 = var7.getFloatValue() + var7.width() - var10 - 7.0F;
         float var13 = Float.isNaN(this.deleteMenuAnchorTop)
            ? var7.getFloatValue2() + 4.0F
            : Math.max(var7.getFloatValue2() + 4.0F, this.deleteMenuAnchorTop - var11 - 2.0F);
         float var14 = var13 - (1.0F - var6) * 5.0F;
         float var15 = var4 * var6;
         this.deleteMenuBounds = new BoundsData(var12, var14, var10, var11);
         var1.drawRoundedRect(
            var12,
            var14,
            var10,
            var11,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            var5.getPanelLeftBackground().getZenithColor().method7294(var15)
         );
         var1.drawRoundedBorder(
            var12,
            var14,
            var10,
            var11,
            0.5F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            var5.getFieldBorder().getZenithColor().method7294(var15 * 2.5F)
         );
         boolean var16 = this.deleteMenuBounds.contains(var2, var3);
         float var17 = this.deleteMenuHover.method2808(var16 ? 1.0F : 0.0F);
         if (var17 > 0.01F) {
            var1.drawRoundedRect(
               var12 + 1.0F,
               var14 + 1.0F,
               var10 - 2.0F,
               var11 - 2.0F,
               ProxyDotRadiusData.method8069(Math.max(1.0F, GuiStyle.ROUND.intValue() * 0.6F - 1.0F)),
               var5.getDisableActiveBg().getZenithColor().method7294(var15 * var17)
            );
         }

         var1.drawText(
            var8, var9, var12 + 9.0F, var14 + (var11 - var8.height()) / 2.0F, ERROR_COLOR.method7295(ZenithColor.INSTANCE, var17 * 0.2F).method7294(var15)
         );
      } else {
         this.deleteMenuBounds = null;
      }
   }

   private BoundsData glyphButton(
      MenuDrawContext var1, String var2, MenuGlyph var3, float var4, float var5, int var6, int var7, boolean var8, float var9, ZenithStyle var10
   ) {
      BoundsData var11 = new BoundsData(var4 - 2.0F, var5 - 3.0F, 10.0F, 12.0F);
      boolean var12 = var11.contains(var6, var7);
      float var13 = this.glyphHover.computeIfAbsent(var2, var0 -> new Animation(130L, 0.0F, Easing.INSTANCE12)).method2808(!var12 && !var8 ? 0.0F : 1.0F);
      if (var13 > 0.01F) {
         var1.drawRoundedRect(
            var11.getFloatValue(),
            var11.getFloatValue2(),
            var11.width(),
            var11.height(),
            ProxyDotRadiusData.method8069(var11.width() / 2.0F),
            var10.getDisableActiveBg().getZenithColor().method7294(var9 * var13 * 0.55F)
         );
      }

      var3.draw(var1, var4, var5, var10.getTextTertiary().getZenithColor().method7295(var10.getTextEnable().getZenithColor(), var13).method7294(var9));
      return var11;
   }

   private void renderPicker(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, float var9) {
      StickerPackService var10 = StickerPackService.getInstance();
      var10.method5508();
      float var11 = (1.0F - var9) * 10.0F;
      var7 *= var9;
      float var12 = 26.0F;
      int var13 = Math.max(1, (int)((var6 - 16.0F - 6.0F) / var12));
      var var14 = var10.getItems2();
      Font var15 = Fonts.NEW_REGULAR.getFont(4.2F);
      if (var14.isEmpty()) {
         float var37 = 26.0F;
         float var38 = var5 - var37 - 4.0F + var11;
         var1.drawRoundedRect(
            var4 + 8.0F,
            var38,
            var6 - 16.0F,
            var37,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            var8.getPanelLeftBackground().getZenithColor().method7294(var7)
         );
         String var39 = t("gui.chat.stickers.none");
         var1.drawText(
            var15,
            var39,
            var4 + 8.0F + (var6 - 16.0F - var15.width(var39)) / 2.0F,
            var38 + (var37 - var15.height()) / 2.0F,
            var8.getTextTertiary().getZenithColor().method7294(var7)
         );
         this.pickerBounds = new BoundsData(var4 + 8.0F, var38, var6 - 16.0F, var37);
      } else {
         int var16 = Math.floorMod(this.pickerPack, var14.size());
         StickerPackService.Data var17 = (StickerPackService.Data)var14.get(var16);
         int var18 = Math.min(4, (var17.getItems().size() + var13 - 1) / var13);
         float var19 = 14.0F + var18 * var12;
         float var20 = var5 - var19 - 4.0F + var11;
         var1.drawRoundedRect(
            var4 + 8.0F,
            var20,
            var6 - 16.0F,
            var19,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            var8.getPanelLeftBackground().getZenithColor().method7294(var7)
         );
         this.pickerBounds = new BoundsData(var4 + 8.0F, var20, var6 - 16.0F, var19);
         float var21 = var4 + 8.0F + 4.0F;
         float var22 = var21;
         float var23 = 0.0F;

         for (int var24 = 0; var24 < var14.size(); var24++) {
            float var25 = var15.width(((StickerPackService.Data)var14.get(var24)).name()) + 8.0F;
            if (var24 == var16) {
               var22 = var21;
               var23 = var25;
            }

            var21 += var25 + 3.0F;
         }

         if (!this.packPlateReady) {
            this.packPlateX.method2809(var22 - var4);
            this.packPlateWidth.method2809(var23);
            this.packPlateReady = true;
         }

         var1.drawRoundedRect(
            var4 + this.packPlateX.method2808(var22 - var4),
            var20 + 2.0F,
            this.packPlateWidth.method2808(var23),
            10.0F,
            ProxyDotRadiusData.method8069(5.0F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * 0.3F)
         );
         var21 = var4 + 8.0F + 4.0F;

         for (int var41 = 0; var41 < var14.size(); var41++) {
            String var43 = ((StickerPackService.Data)var14.get(var41)).name();
            float var26 = var15.width(var43) + 8.0F;
            boolean var27 = var41 == var16;
            boolean var28 = !var27 && var2 >= var21 && var2 <= var21 + var26 && var3 >= var20 + 2.0F && var3 <= var20 + 12.0F;
            float var29 = this.cellHover.computeIfAbsent("pack:" + var41, var0 -> new Animation(130L, 0.0F, Easing.INSTANCE12)).method2808(var28 ? 1.0F : 0.0F);
            ZenithColor var30 = var27
               ? var8.getTextEnable().getZenithColor()
               : var8.getTextTertiary().getZenithColor().method7295(var8.getTextSecondary().getZenithColor(), var29);
            var1.drawText(var15, var43, var21 + 4.0F, var20 + 2.0F + (10.0F - var15.height()) / 2.0F, var30.method7294(var7));
            var21 += var26 + 3.0F;
         }

         float var42 = var20 + 14.0F;

         for (int var44 = 0; var44 < var17.getItems().size() && var44 < var18 * var13; var44++) {
            int var45 = var44 / var13;
            int var46 = var44 % var13;
            float var47 = var4 + 8.0F + 3.0F + var46 * var12;
            float var48 = var42 + var45 * var12;
            String var49 = var10.method5507(var17.getText(), var17.getItems().get(var44));
            Identifier var31 = ChatImageCache.get(var49);
            if (var31 == null) {
               var1.drawRoundedRect(
                  var47 + 2.0F,
                  var48 + 2.0F,
                  var12 - 4.0F,
                  var12 - 4.0F,
                  ProxyDotRadiusData.method8069(3.0F),
                  var8.getSurfaceEnableBackground().getZenithColor().method7294(var7 * 1.4F)
               );
            } else {
               boolean var32 = var2 >= var47 && var2 <= var47 + var12 && var3 >= var48 && var3 <= var48 + var12;
               float var33 = this.cellHover.computeIfAbsent(var49, var0 -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(var32 ? 1.0F : 0.0F);
               if (var33 > 0.01F) {
                  var1.drawRoundedRect(
                     var47, var48, var12, var12, ProxyDotRadiusData.method8069(4.0F), var8.getDisableActiveBg().getZenithColor().method7294(var7 * var33)
                  );
               }

               float var34 = this.stickerFade.computeIfAbsent(var49, var0 -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
               float var35 = (var12 - 4.0F) * (1.0F + var33 * 0.1F);
               ChatCards.drawSticker(
                  var1,
                  var17.getItems().get(var44),
                  var31,
                  var47 + (var12 - var35) / 2.0F,
                  var48 + (var12 - var35) / 2.0F,
                  var35,
                  ProxyDotRadiusData.method8069(3.0F),
                  ZenithColor.INSTANCE.method7294(var7 * var34)
               );
            }
         }
      }
   }

   @Override
   public boolean onFooterClicked(double var1, double var3, Enum3 var5) {
      if (this.pendingButton != null && this.pendingButton.contains(var1, var3)) {
         this.pendingTag = "";
         this.pendingLabel = "";
         return true;
      } else if (this.coordsButton != null && this.coordsButton.contains(var1, var3)) {
         this.attachCoords();
         return true;
      } else if (this.inventoryButton != null && this.inventoryButton.contains(var1, var3)) {
         this.attachInventory();
         return true;
      } else if (this.stickerButton != null && this.stickerButton.contains(var1, var3)) {
         this.pickerOpen = !this.pickerOpen;
         if (this.pickerOpen) {
            StickerPackService.getInstance().method5508();
         }

         return true;
      } else {
         this.focusInput();
         return true;
      }
   }

   private void prepareStickers() {
      StickerPackService var1 = StickerPackService.getInstance();
      var1.method5508();
      if (var1.method5506() && var1.revision() != this.stickersPrepared) {
         this.stickersPrepared = var1.revision();
         List<StickerPackService.Data> var2 = var1.getItems2();

         for (Object var4_o : (Iterable<?>)(Object)(var2)) {
            StickerPackService.Data var4 = (StickerPackService.Data)var4_o;
            ChatImageCache.get(var1.method5507(var4.getText(), var4.getText3()));
         }

         StickerPackService.Data var6 = (StickerPackService.Data)var2.get(0);

         for (String var5 : var6.getItems()) {
            ChatImageCache.get(var1.method5507(var6.getText(), var5));
         }
      }
   }

   private void attachCoords() {
      String var1 = ChatCompose.inWorld() ? ChatCompose.coordsHere("") : "";
      if (var1.isEmpty()) {
         this.error = t("gui.chat.notInWorld");
         this.revision++;
      } else {
         this.pendingTag = var1;
         this.pendingLabel = t("gui.chat.attach.coords");
         this.focusInput();
      }
   }

   private void attachInventory() {
      if (!ChatCompose.inWorld()) {
         this.error = t("gui.chat.notInWorld");
         this.revision++;
      } else if (!ChatCompose.hasItems()) {
         this.error = t("gui.chat.emptyInventory");
         this.revision++;
      } else {
         String var1 = ChatCompose.inventory(this.getActiveTab() == 1, siteMode());
         if (var1.isEmpty()) {
            this.error = t("gui.chat.inventoryTooBig");
            this.revision++;
         } else {
            this.pendingTag = var1;
            this.pendingLabel = t("gui.chat.attach.inventory");
            this.focusInput();
         }
      }
   }

   private boolean clickPicker(double var1, double var3) {
      if (this.pickerOpen && this.pickerBounds != null && this.pickerBounds.contains(var1, var3)) {
         var var5 = StickerPackService.getInstance().getItems2();
         if (var5.isEmpty()) {
            return true;
         } else {
            Font var6 = Fonts.NEW_REGULAR.getFont(4.2F);
            float var7 = this.pickerBounds.getFloatValue2();
            if (var3 <= var7 + 12.0F) {
               float var14 = this.pickerBounds.getFloatValue() + 4.0F;

               for (int var15 = 0; var15 < var5.size(); var15++) {
                  float var16 = var6.width(((StickerPackService.Data)var5.get(var15)).name()) + 8.0F;
                  if (var1 >= var14 && var1 <= var14 + var16) {
                     this.pickerPack = var15;
                     return true;
                  }

                  var14 += var16 + 3.0F;
               }

               return true;
            } else {
               float var8 = 26.0F;
               int var9 = Math.max(1, (int)((this.pickerBounds.width() - 6.0F) / var8));
               int var10 = (int)((var1 - this.pickerBounds.getFloatValue() - 3.0) / var8);
               int var11 = (int)((var3 - var7 - 14.0) / var8);
               if (var10 >= 0 && var10 < var9 && var11 >= 0) {
                  StickerPackService.Data var12 = (StickerPackService.Data)var5.get(Math.floorMod(this.pickerPack, var5.size()));
                  int var13 = var11 * var9 + var10;
                  if (var13 >= 0 && var13 < var12.getItems().size()) {
                     this.sendBody(StickerPackService.method5510(var12.getText(), var12.getItems().get(var13)));
                     this.pickerOpen = false;
                  }

                  return true;
               } else {
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.clickDeleteMenu(var1, var3)) {
         return true;
      } else {
         if (this.deleteMenuMessage != 0L) {
            this.deleteMenuMessage = 0L;
         }

         if (this.clickPicker(var1, var3)) {
            return true;
         } else {
            if (this.pickerOpen) {
               BoundsData var6 = this.footerBounds();
               if (var6 == null || !var6.contains(var1, var3)) {
                  this.pickerOpen = false;
               }
            }

            BoundsData var8 = this.mainHeaderBounds();
            if (var8 != null && this.hasPeer() && var8.contains(var1, var3)) {
               if (var1 >= var8.getFloatValue() + var8.width() - 8.0F - 6.0F - 3.0F) {
                  this.closePeer();
               }

               return true;
            } else {
               boolean var7 = super.onMouseClicked(var1, var3, var5);
               this.focusInput();
               return var7;
            }
         }
      }
   }

   private void closePeer() {
      if (siteMode()) {
         direct().close();
      } else {
         this.peerUid = 0L;
         this.peerName = "";
         this.clearConversation();
      }

      this.invalidate();
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.input != null && this.input.isSelected()) {
         if (var1 == 257 || var1 == 335) {
            this.send();
            return true;
         }

         if (var1 == 256) {
            if (this.deleteMenuMessage != 0L) {
               this.deleteMenuMessage = 0L;
               return true;
            }

            if (this.pickerOpen) {
               this.pickerOpen = false;
               return true;
            }

            this.input.setSelected(false);
            this.blurred = true;
            return true;
         }

         if (this.input.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.input != null && this.input.isSelected() && this.input.charTyped(var1, var2)) {
         if (!this.error.isEmpty()) {
            this.error = "";
            this.revision++;
         }

         return true;
      } else {
         return super.charTyped(var1, var2);
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      this.stickToEnd = false;
      this.deleteMenuMessage = 0L;
      return super.mouseScrolled(var1, var3, var5, var7);
   }

   private void send() {
      String var1 = this.input == null ? "" : this.input.getText();
      String var2 = this.pendingTag;
      if (var1 != null && !var1.isBlank() || !var2.isEmpty()) {
         StringBuilder var3 = new StringBuilder(var1 == null ? "" : var1.strip());
         if (!var2.isEmpty()) {
            if (var3.length() > 0) {
               var3.append(' ');
            }

            var3.append(var2);
         }

         this.draft = var1 == null ? "" : var1;
         this.draftTag = var2;
         if (this.sendBody(var3.toString())) {
            this.input.setText("");
            this.input.setCursor(0);
            this.pendingTag = "";
            this.pendingLabel = "";
         }
      }
   }

   private void restoreDraft(Throwable var1, boolean var2) {
      this.error = var2 ? siteError(var1) : shortError(var1);
      if (this.input != null && this.input.getText().isEmpty() && !this.draft.isEmpty()) {
         this.input.setText(this.draft);
         this.input.setCursor(this.draft.length());
      }

      if (this.pendingTag.isEmpty() && !this.draftTag.isEmpty()) {
         this.pendingTag = this.draftTag;
         this.pendingLabel = this.labelOfTag(this.draftTag);
      }

      this.revision++;
   }

   private String labelOfTag(String var1) {
      Coord var2 = Coord.method5172(var1).getItems().stream().findFirst().orElse(null);
      if (var2 == null) {
         return "";
      } else {
         String var3 = var2.kind();

         return switch (var3) {
            case "coord" -> t("gui.chat.attach.coords");
            case "inv" -> t("gui.chat.attach.inventory");
            default -> "";
         };
      }
   }

   private boolean sendBody(String var1) {
      boolean var2 = this.getActiveTab() == 1;
      int var3 = !siteMode() ? 256 : (var2 ? 1000 : 500);
      String var4 = var1;
      if (siteMode()) {
         String var5 = ChatCompose.whereTag();
         if (!var5.isEmpty() && var1.length() + var5.length() + 1 <= var3) {
            var4 = var1 + " " + var5;
         }
      }

      String var10 = Coord.method5174(var4, var3);
      if (!var10.isBlank() && (var10.length() >= var4.length() || Coord.method5172(var10).getItems().size() >= Coord.method5172(var4).getItems().size())) {
         this.stickToEnd = true;
         if (var2) {
            long var11 = this.openPeerUid();
            if (var11 == 0L) {
               this.error = t("gui.chat.pickPeer");
               this.revision++;
               return false;
            } else if (siteMode()) {
               direct().method4867(var11, var10).whenComplete((var3x, var4x) -> {
                  if (var4x != null) {
                     if (this.openPeerUid() == var11) {
                        this.restoreDraft(var4x, true);
                     } else {
                        this.error = siteError(var4x);
                        this.revision++;
                     }
                  } else {
                     this.error = "";
                     this.revision++;
                  }
               });
               return true;
            } else {
               StoreApi var8 = store();
               if (var8 == null) {
                  this.error = t("gui.chat.storeUnavailable");
                  this.revision++;
                  return false;
               } else {
                  var8.method1966(var11, var10).whenComplete((var4x, var5x) -> {
                     boolean var6x = this.openPeerUid() != var11;
                     if (var5x != null) {
                        if (var6x) {
                           this.error = shortError(var5x);
                           this.revision++;
                        } else {
                           this.restoreDraft(var5x, false);
                        }
                     } else {
                        if (!var6x) {
                           synchronized (this.conversationLock) {
                              var var8x = new ArrayList<>(this.conversation);
                              var8x.add(var4x);
                              this.conversation = Collections.unmodifiableList(var8x);
                           }
                        }

                        this.rememberPreview(var11, var10);
                        this.error = "";
                        this.revision++;
                     }
                  });
                  return true;
               }
            }
         } else if (siteMode()) {
            global().method2168(var10).whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  this.restoreDraft(var2x, true);
               } else {
                  this.error = "";
                  this.revision++;
               }
            });
            return true;
         } else {
            Cloud var6 = cloud();
            if (var6 != null && var6.method7908(0, var10)) {
               return true;
            } else {
               CloudConnectionState var7 = state();
               this.error = var7 != null && !var7.method8035() ? var7.getType().label() : t("gui.chat.sendFailed");
               this.revision++;
               return false;
            }
         }
      } else {
         this.error = t("gui.chat.tooLong");
         this.revision++;
         return false;
      }
   }

   private float reveal(int var1) {
      float var2 = this.listReveal.method2808(1.0F);
      float var3 = Math.min(0.5F, var1 * 0.06F);
      return var2 <= var3 ? 0.0F : Math.min(1.0F, (var2 - var3) / Math.max(0.05F, 1.0F - var3));
   }

   private static String roleLabel(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         return switch (var0) {
            case "Administrator" -> "админ";
            case "Support" -> "саппорт";
            case "Media" -> "медиа";
            case "Private" -> "private";
            case "Opus", "Recode" -> "opus";
            case "Legacy" -> "legacy";
            case "AutoBuy" -> "autobuy";
            case "Alpha" -> "alpha";
            case "User" -> "";
            default -> var0;
         };
      } else {
         return "";
      }
   }

   private static ZenithColor roleColor(String var0, ZenithStyle var1) {
      if (var0 == null) {
         return var1.getTextTertiary().getZenithColor();
      } else {
         return switch (var0) {
            case "Administrator", "админ", "Private", "private" -> var1.getPrimaryColor().getZenithColor();
            case "Support", "саппорт", "Media", "медиа", "Opus", "opus", "Recode", "recode" -> var1.getFriendColor().getZenithColor();
            default -> var1.getTextTertiary().getZenithColor();
         };
      }
   }

   private static String previewOf(String var0) {
      Coord.Data var1 = Coord.method5172(var0);
      if (!var1.text().isEmpty()) {
         return var1.text();
      } else if (var1.isBroken()) {
         return t("gui.chat.attachBroken");
      } else if (var1.method5180("st")) {
         return "стикер";
      } else if (var1.method5180("coord")) {
         return "координаты";
      } else {
         return var1.method5180("inv") ? "инвентарь" : "";
      }
   }

   private static List<String> wrap(String var0, Font var1, float var2) {
      ArrayList<String> var3 = new ArrayList<>();
      if (var0 == null) {
         return var3;
      } else {
         StringBuilder var4 = new StringBuilder();

         for (String var8 : var0.split(" ")) {
            while (var1.width(var8) > var2) {
               if (!var4.isEmpty()) {
                  var3.add(var4.toString());
                  var4 = new StringBuilder();
               }

               int var9 = fitting(var8, var1, var2);
               var3.add(var8.substring(0, var9));
               var8 = var8.substring(var9);
            }

            String var10 = var4.isEmpty() ? var8 : var4 + " " + var8;
            if (var1.width(var10) > var2 && !var4.isEmpty()) {
               var3.add(var4.toString());
               var4 = new StringBuilder(var8);
            } else {
               var4 = new StringBuilder(var10);
            }
         }

         if (!var4.isEmpty()) {
            var3.add(var4.toString());
         }

         if (var3.isEmpty()) {
            var3.add("");
         }

         return var3;
      }
   }

   private static int fitting(String var0, Font var1, float var2) {
      int var3 = 1;

      while (var3 < var0.length() && var1.width(var0.substring(0, var3 + 1)) <= var2) {
         var3++;
      }

      return var3;
   }

   private static String clip(String var0, Font var1, float var2) {
      if (var0 != null && !(var1.width(var0) <= var2)) {
         CloudChatSectionPanel.ClipKey var3 = new CloudChatSectionPanel.ClipKey(var0, var1, var2);
         String var4 = CLIPS.get(var3);
         if (var4 != null) {
            return var4;
         } else {
            String var5 = var1.clip(var0, var2);
            CLIPS.put(var3, var5);
            return var5;
         }
      } else {
         return var0 == null ? "" : var0;
      }
   }

   private static String clipText(String var0) {
      return var0 != null && var0.length() > 40 ? var0.substring(0, 39) + "…" : var0;
   }

   private static String siteError(Throwable var0) {
      return !SiteSession.method2291() ? t("gui.chat.site.needLogin") : shortError(var0);
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

      return clipText(var2);
   }

   private static final class Bubble {
      private final String author;
      private final long authorUid;
      private final String badge;
      private final String where;
      private final List<String> lines;
      private final List<Coord> cards;
      private final String time;
      private final boolean read;
      private final boolean mine;
      private final boolean showName;
      private final boolean newest;
      private final boolean bare;
      private final float width;
      private final float height;
      private final Font nameFont;
      private final Font textFont;
      private final Font timeFont;

      public String author() {
         return this.author;
      }

      public long authorUid() {
         return this.authorUid;
      }

      public String badge() {
         return this.badge;
      }

      public String where() {
         return this.where;
      }

      public List<String> lines() {
         return this.lines;
      }

      public List<Coord> cards() {
         return this.cards;
      }

      public String time() {
         return this.time;
      }

      public boolean read() {
         return this.read;
      }

      public boolean mine() {
         return this.mine;
      }

      public boolean showName() {
         return this.showName;
      }

      public boolean newest() {
         return this.newest;
      }

      public boolean bare() {
         return this.bare;
      }

      public float width() {
         return this.width;
      }

      public float height() {
         return this.height;
      }

      public Font nameFont() {
         return this.nameFont;
      }

      public Font textFont() {
         return this.textFont;
      }

      public Font timeFont() {
         return this.timeFont;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Bubble other)) return false;
         return java.util.Objects.equals(this.author, other.author) && java.util.Objects.equals(this.authorUid, other.authorUid) && java.util.Objects.equals(this.badge, other.badge) && java.util.Objects.equals(this.where, other.where) && java.util.Objects.equals(this.lines, other.lines) && java.util.Objects.equals(this.cards, other.cards) && java.util.Objects.equals(this.time, other.time) && java.util.Objects.equals(this.read, other.read) && java.util.Objects.equals(this.mine, other.mine) && java.util.Objects.equals(this.showName, other.showName) && java.util.Objects.equals(this.newest, other.newest) && java.util.Objects.equals(this.bare, other.bare) && java.util.Objects.equals(this.width, other.width) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.nameFont, other.nameFont) && java.util.Objects.equals(this.textFont, other.textFont) && java.util.Objects.equals(this.timeFont, other.timeFont);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(author, authorUid, badge, where, lines, cards, time, read, mine, showName, newest, bare, width, height, nameFont, textFont, timeFont);
      }

      @Override
      public String toString() {
         return "Bubble[" + "author=" + this.author + ", " + "authorUid=" + this.authorUid + ", " + "badge=" + this.badge + ", " + "where=" + this.where + ", " + "lines=" + this.lines + ", " + "cards=" + this.cards + ", " + "time=" + this.time + ", " + "read=" + this.read + ", " + "mine=" + this.mine + ", " + "showName=" + this.showName + ", " + "newest=" + this.newest + ", " + "bare=" + this.bare + ", " + "width=" + this.width + ", " + "height=" + this.height + ", " + "nameFont=" + this.nameFont + ", " + "textFont=" + this.textFont + ", " + "timeFont=" + this.timeFont + "]";
      }

      private Bubble(
         String var1,
         long var2,
         String var4,
         String var5,
         List<String> var6,
         List<Coord> var7,
         String var8,
         boolean var9,
         boolean var10,
         boolean var11,
         boolean var12,
         boolean var13,
         float var14,
         float var15,
         Font var16,
         Font var17,
         Font var18
      ) {
         this.author = var1;
         this.authorUid = var2;
         this.badge = var4;
         this.where = var5;
         this.lines = var6;
         this.cards = var7;
         this.time = var8;
         this.read = var9;
         this.mine = var10;
         this.showName = var11;
         this.newest = var12;
         this.bare = var13;
         this.width = var14;
         this.height = var15;
         this.nameFont = var16;
         this.textFont = var17;
         this.timeFont = var18;
      }
   }

   private static final class BubbleKey {
      private final String author;
      private final long authorUid;
      private final String role;
      private final String body;
      private final long time;
      private final boolean mine;
      private final boolean showName;
      private final boolean newest;
      private final boolean read;
      private final float column;
      private final String language;
      private final Font nameFont;
      private final Font textFont;
      private final Font timeFont;

      public String author() {
         return this.author;
      }

      public long authorUid() {
         return this.authorUid;
      }

      public String role() {
         return this.role;
      }

      public String body() {
         return this.body;
      }

      public long time() {
         return this.time;
      }

      public boolean mine() {
         return this.mine;
      }

      public boolean showName() {
         return this.showName;
      }

      public boolean newest() {
         return this.newest;
      }

      public boolean read() {
         return this.read;
      }

      public float column() {
         return this.column;
      }

      public String language() {
         return this.language;
      }

      public Font nameFont() {
         return this.nameFont;
      }

      public Font textFont() {
         return this.textFont;
      }

      public Font timeFont() {
         return this.timeFont;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BubbleKey other)) return false;
         return java.util.Objects.equals(this.author, other.author) && java.util.Objects.equals(this.authorUid, other.authorUid) && java.util.Objects.equals(this.role, other.role) && java.util.Objects.equals(this.body, other.body) && java.util.Objects.equals(this.time, other.time) && java.util.Objects.equals(this.mine, other.mine) && java.util.Objects.equals(this.showName, other.showName) && java.util.Objects.equals(this.newest, other.newest) && java.util.Objects.equals(this.read, other.read) && java.util.Objects.equals(this.column, other.column) && java.util.Objects.equals(this.language, other.language) && java.util.Objects.equals(this.nameFont, other.nameFont) && java.util.Objects.equals(this.textFont, other.textFont) && java.util.Objects.equals(this.timeFont, other.timeFont);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(author, authorUid, role, body, time, mine, showName, newest, read, column, language, nameFont, textFont, timeFont);
      }

      @Override
      public String toString() {
         return "BubbleKey[" + "author=" + this.author + ", " + "authorUid=" + this.authorUid + ", " + "role=" + this.role + ", " + "body=" + this.body + ", " + "time=" + this.time + ", " + "mine=" + this.mine + ", " + "showName=" + this.showName + ", " + "newest=" + this.newest + ", " + "read=" + this.read + ", " + "column=" + this.column + ", " + "language=" + this.language + ", " + "nameFont=" + this.nameFont + ", " + "textFont=" + this.textFont + ", " + "timeFont=" + this.timeFont + "]";
      }

      private BubbleKey(
         String var1,
         long var2,
         String var4,
         String var5,
         long var6,
         boolean var8,
         boolean var9,
         boolean var10,
         boolean var11,
         float var12,
         String var13,
         Font var14,
         Font var15,
         Font var16
      ) {
         this.author = var1;
         this.authorUid = var2;
         this.role = var4;
         this.body = var5;
         this.time = var6;
         this.mine = var8;
         this.showName = var9;
         this.newest = var10;
         this.read = var11;
         this.column = var12;
         this.language = var13;
         this.nameFont = var14;
         this.textFont = var15;
         this.timeFont = var16;
      }
   }

   private static final class BubbleLayout {
      private final CloudChatSectionPanel.Bubble shape;
      private final Coord coord;

      public CloudChatSectionPanel.Bubble shape() {
         return this.shape;
      }

      public Coord coord() {
         return this.coord;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BubbleLayout other)) return false;
         return java.util.Objects.equals(this.shape, other.shape) && java.util.Objects.equals(this.coord, other.coord);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shape, coord);
      }

      @Override
      public String toString() {
         return "BubbleLayout[" + "shape=" + this.shape + ", " + "coord=" + this.coord + "]";
      }

      private BubbleLayout(CloudChatSectionPanel.Bubble var1, Coord var2) {
         this.shape = var1;
         this.coord = var2;
      }
   }

   private static final class ClipKey {
      private final String text;
      private final Font font;
      private final float maxWidth;

      public String text() {
         return this.text;
      }

      public Font font() {
         return this.font;
      }

      public float maxWidth() {
         return this.maxWidth;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ClipKey other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.maxWidth, other.maxWidth);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, font, maxWidth);
      }

      @Override
      public String toString() {
         return "ClipKey[" + "text=" + this.text + ", " + "font=" + this.font + ", " + "maxWidth=" + this.maxWidth + "]";
      }

      private ClipKey(String var1, Font var2, float var3) {
         this.text = var1;
         this.font = var2;
         this.maxWidth = var3;
      }
   }

   private static final class Ghost {
      private final int feed;
      private final String author;
      private final String role;
      private final String body;
      private final long time;
      private final boolean mine;
      private final long startedAt = System.currentTimeMillis();

      private Ghost(int var1, String var2, String var3, String var4, long var5, boolean var7) {
         this.feed = var1;
         this.author = var2;
         this.role = var3;
         this.body = var4;
         this.time = var5;
         this.mine = var7;
      }

      private float factor() {
         float var1 = 1.0F - (float)(System.currentTimeMillis() - this.startedAt) / 240.0F;
         var1 = Math.max(0.0F, Math.min(1.0F, var1));
         return var1 * var1;
      }
   }
}

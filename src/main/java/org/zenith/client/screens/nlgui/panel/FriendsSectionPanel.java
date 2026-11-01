package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Prefs;
import org.zenith.utility.api.Share;
import org.zenith.utility.api.UserSearchClient;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.PartyManager;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class FriendsSectionPanel extends SectionPanel {
   private static final int TAB_FRIENDS = 0;
   private static final int TAB_WEEK = 1;
   private static final int TAB_SHARING = 2;
   private static final int FILTER_ALL = 0;
   private static final int FILTER_ONLINE = 1;
   private static final int FILTER_NEAR = 2;
   private static final int FILTER_OFFLINE = 3;
   private static final int SLOT_OFFHAND = 40;
   private static final int SLOT_ARMOR_FIRST = 36;
   private static final int SLOT_ARMOR_LAST = 39;
   private static final float HERO_HEIGHT = 40.0F;
   private static final float TILE_PAD = 5.0F;
   private static final float HEAD_SIZE = 26.0F;
   private static final float BAR_HEIGHT = 3.0F;
   private static final float ITEM_SIZE = 16.0F;
   private static final float BUTTON_HEIGHT = 15.0F;
   private static final float CARD_HEIGHT = 26.0F;
   private static final float CARD_AVATAR = 16.0F;
   private static final float STATUS_DOT = 4.0F;
   private static final float SMALL_BUTTON = 12.0F;
   private static final float CHIP_HEIGHT = 18.0F;
   private static final float ADD_FIELD_HEIGHT = 22.0F;
   private static final float QUICK_HEIGHT = 30.0F;
   private static final float SHARE_CARD_HEIGHT = 28.0F;
   private static final long REOPEN_GAP_MS = 400L;
   private static final long FLASH_MS = 2600L;
   private static final ZenithColor FAVORITE = new ZenithColor(-278748);
   private static final ZenithColor DANGER = new ZenithColor(-495247);
   private static final long TYPING_PAUSE_MS = 260L;
   private static final int SUGGEST_LIMIT = 6;
   private static final int ADD_LOCAL = -1;
   private static final float NOTICE_PAD_X = 7.0F;
   private static final float NOTICE_PAD_Y = 5.0F;
   private final TextSetting addTargetSetting = new TextSetting("gui.friends.nick", "gui.friends.nick.desc", "", "gui.friends.nick.hint").method2121();
   private final InspectorRow addRow = new InspectorRow(this.addTargetSetting, 0);
   private String selected;
   private boolean selectedLocal;
   private final PartyBlock party = new PartyBlock(this);
   private final WeekBlock week = new WeekBlock(this);
   private int filter = 0;
   private final Animation cardReveal = new Animation(220L, 1.0F, Easing.INSTANCE12);
   private Predicate<String> openChatWith;
   private final Map<Long, Animation> avatarFade = new ConcurrentHashMap<>();
   private final Map<String, Animation> buttonHover = new HashMap<>();
   private final Map<String, Animation> barFill = new HashMap<>();
   private final Animation noticeAppear = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private String noticeAnimatedFor = "";
   private final Animation heroPulse = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private String noticeText = "";
   private String noticeTarget = "";
   private boolean offerOffline;
   private boolean addOpen;
   private final Map<String, Integer> hoveredButton = new HashMap<>();
   private final Map<String, Animation> cardSelect = new HashMap<>();
   private String typed = "";
   private long typedAt;
   private boolean searching;
   private String suggestFor = "";
   private boolean directoryAnswered;
   private List<UserSearchClient.Data> suggestions = List.of();
   private final Animation suggestWave = new Animation(460L, 0.0F, Easing.INSTANCE12);
   private final Animation listWave = new Animation(560L, 1.0F, Easing.INSTANCE12);
   private final Animation asideWave = new Animation(480L, 1.0F, Easing.INSTANCE12);
   private final Map<String, Long> pressedAt = new HashMap<>();
   private boolean submitWhenReady;
   private String flashText = "";
   private long flashAt;
   private String lastSignature = "";
   private String lastQuery = "";
   private String lastShareSignature = "";
   private long lastTickAt;
   private SliderSetting voiceVolume;
   private String voiceVolumeFor;
   private int order;
   private int asideOrder;
   private final TreeSet<String> nearbyNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
   private Object nearbyWorld;
   private int nearbyCount = -1;
   private long nearbyAt;

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   private static CloudConnectionState state() {
      Cloud var0 = cloud();
      return var0 == null ? null : var0.state();
   }

   private static String shareSignature() {
      Share var0 = Zenith.getInstance().getShare();
      return var0 == null
         ? ""
         : var0.cosmeticsVisibility.getIndex()
            + "|"
            + var0.position.isEnabled()
            + "|"
            + var0.health.isEnabled()
            + "|"
            + var0.inventory.isEnabled()
            + "|"
            + var0.server.isEnabled()
            + "|"
            + var0.colorSetting.method1445();
   }

   public void openChatWith(Predicate<String> var1) {
      this.openChatWith = var1;
   }

   @Override
   public String headerTitle() {
      return "Friends";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.FRIENDS;
   }

   @Override
   public String sectionDescription() {
      return "gui.friends.desc";
   }

   @Override
   public String emptyText() {
      return "";
   }

   @Override
   public float asideAlpha() {
      return this.selected == null ? 1.0F : this.cardReveal.method2808(1.0F);
   }

   @Override
   public void tick() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.lastTickAt > 400L) {
         this.startListWave();
         restart(this.asideWave);
      }

      this.lastTickAt = var1;
      this.pollTyping();
      StringBuilder var3 = new StringBuilder();
      Cloud var4 = cloud();
      CloudConnectionState var5 = state();
      if (var4 != null && var5 != null) {
         var3.append(var5.getType()).append(';');
         String var6 = var4.method7911();
         String var7 = var4.method7913();

         for (String var9 : var4.getFriends().all()) {
            PartyPlayerInfo var10 = var5.method8044(var9);
            var3.append(var9)
               .append(':')
               .append(var10 == null ? 45L : var10.uid())
               .append((char)(var5.method8061(var9) ? '?' : '.'))
               .append((char)(var10 != null && var10.method3289() ? 'v' : '.'))
               .append((char)(var4.prefs().method3319(var9) ? '*' : '.'))
               .append((char)(var10 != null && var10.method3291(var6, var7) ? 'w' : '.'))
               .append(';');
         }

         if (this.voiceVolume != null && this.voiceVolumeFor != null) {
            var4.prefs().method3324(this.voiceVolumeFor, Math.round(this.voiceVolume.getValue3()));
         }

         for (CloudPackets.Data8 var18 : var5.method8055()) {
            var3.append('R').append(var18.username()).append(';');
         }

         PartyMember var15 = var4.getPartyMember();
         var3.append('S')
            .append(var15.version())
            .append(var15.method1147())
            .append(var15.method1136())
            .append(var15.getLongValue())
            .append('P')
            .append(this.party.signature(var4))
            .append('W')
            .append(this.week.signature(var4));
         this.week.tick(var4, this.getActiveTab() == 1);
      }

      var var11 = Zenith.getInstance().getFriends().getItems();
      if (var11 != null) {
         for (Object var16_o : (Iterable<?>)(Object)(List.copyOf(var11))) {
            String var16 = (String)var16_o;
            var3.append('L').append(var16).append((char)(this.isNearby(var16) ? '+' : '-')).append(';');
         }
      }

      var3.append('N').append(this.noticeText).append('|').append(this.searching).append(this.addOpen).append('|').append(this.flashing());
      String var13 = shareSignature();
      if (!var13.equals(this.lastShareSignature)) {
         if (!this.lastShareSignature.isEmpty()) {
            Zenith.getInstance().getShare().save();
         }

         this.lastShareSignature = var13;
      }

      String var17 = this.searchQuery();
      if (!var3.toString().equals(this.lastSignature) || !var17.equals(this.lastQuery)) {
         this.lastSignature = var3.toString();
         this.lastQuery = var17;
         this.invalidate();
      }
   }

   @Override
   public void selectTab(int var1) {
      if (var1 != this.getActiveTab()) {
         this.startListWave();
         restart(this.asideWave);
         if (var1 == 1) {
            this.week.opened(cloud());
         }
      }

      super.selectTab(var1);
   }

   private void startListWave() {
      restart(this.listWave);
   }

   private static void restart(Animation var0) {
      var0.reset(0.0F);
      var0.method2810(1.0F);
   }

   private static float wave(Animation var0, int var1) {
      float var2 = var0.method2808(1.0F);
      return MathHelper.clamp((var2 - Math.min(var1, 12) * 0.06F) / 0.5F, 0.0F, 1.0F);
   }

   @Override
   public void build() {
      this.tabs(new String[]{"gui.friends.tab.list", "gui.friends.tab.week", "gui.friends.tab.share"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.FRIENDS, MenuGlyph.TIMER, MenuGlyph.SHIELD});
      this.order = 0;
      this.asideOrder = 0;
      switch (this.getActiveTab()) {
         case 1:
            this.week.build(cloud());
            break;
         case 2:
            this.buildSharing();
            break;
         default:
            this.buildFriends();
      }
   }

   private void buildFriends() {
      String var1 = this.searchQuery();
      Cloud var2 = cloud();
      CloudConnectionState var3 = state();
      Prefs var4 = prefs();
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      ArrayList var8 = new ArrayList();
      if (var2 != null) {
         for (String var10 : var2.getFriends().all()) {
            if (matches(var10, var1)) {
               PartyPlayerInfo var11 = peer(var10);
               if (var11 != null) {
                  var5.add(var10);
                  if (var11.method3291(var2.method7911(), var2.method7913())) {
                     var6.add(var10);
                  }
               } else if (var3 != null && var3.method8061(var10)) {
                  var7.add(var10);
               } else {
                  var8.add(var10);
               }
            }
         }
      }

      ArrayList var17 = new ArrayList();
      ArrayList var18 = new ArrayList();
      var var19 = Zenith.getInstance().getFriends().getItems();
      if (var19 != null) {
         for (Object var13_o : (Iterable<?>)(Object)(new LinkedHashSet(List.copyOf(var19)))) {
            String var13 = (String)var13_o;
            if (matches(var13, var1)) {
               (this.isNearby(var13) ? var17 : var18).add(var13);
            }
         }
      }

      if (var4 != null) {
         Comparator<String> var20 = (var1x, var2x) -> Boolean.compare(var4.method3319(var2x), var4.method3319(var1x));
         var5.sort(var20);
         var6.sort(var20);
         var7.sort(var20);
         var8.sort(var20);
      }

      int var21 = var5.size() + var7.size() + var8.size() + var17.size() + var18.size();
      List var22 = var3 == null ? List.of() : new ArrayList<>(var3.method8055());
      this.hero(var5.size() + var17.size(), var21);
      this.party.build(var2);
      this.buildAddBlock();
      if (!var22.isEmpty()) {
         this.caption(t("gui.friends.caption.requests") + " · " + var22.size());

         for (Object var15_o : (Iterable<?>)(Object)(var22)) {
            CloudPackets.Data8 var15 = (CloudPackets.Data8)var15_o;
            this.requestCard(var15, this.order++);
         }
      }

      int var23 = this.buildInvites(var2);
      if (var21 == 0) {
         if (var22.isEmpty() && var23 == 0) {
            this.emptyState(var1);
         }

         this.buildFriendAside();
      } else {
         int[] var24 = new int[]{var21, var5.size() + var17.size(), var6.size() + var17.size(), var7.size() + var8.size() + var18.size()};
         this.filterChips(var24);
         int var16 = 0;
         if (this.filter == 0 || this.filter == 1) {
            var16 += this.section("gui.friends.caption.online", var5, var17);
         }

         if (this.filter == 2) {
            var16 += this.section("gui.friends.caption.near", var6, var17);
         }

         if (this.filter == 0 || this.filter == 3) {
            var16 += this.section("gui.friends.caption.pending", var7, List.of());
            var16 += this.section("gui.friends.caption.offline", var8, var18);
         }

         if (var16 == 0) {
            this.hint(t("gui.friends.filter.empty"));
         }

         this.buildFriendAside();
      }
   }

   private int buildInvites(Cloud var1) {
      if (var1 == null) {
         return 0;
      } else {
         PartyMember var2 = var1.getPartyMember();
         List<PartyMember.Data7> var3 = var2.method1144();
         List<PartyMember.Data16> var4 = var2.method1145();
         List<PartyMember.Data13> var5 = var2.method1146();
         int var6 = var3.size() + var4.size() + var5.size();
         if (var6 == 0) {
            return 0;
         } else {
            this.caption(t("gui.friends.caption.invites") + " · " + var6);

            for (Object var8_o : (Iterable<?>)(Object)(var5)) {
               PartyMember.Data13 var8 = (PartyMember.Data13)var8_o;
               String var9 = ServerNameUtils.method5643(var8.server(), var8.mode());
               this.inviteCard(
                  "join:" + var8.getLongValue(),
                  var8.getText(),
                  var8.getLongValue(),
                  t("gui.friends.invite.join").replace("{w}", var9.isEmpty() ? var8.getText3() : var9),
                  t("gui.friends.invite.go"),
                  () -> {
                     PartyManager var1x = PartyManager.method17();
                     if (var1x != null) {
                        var1x.method44(var8);
                     }
                  },
                  () -> var2.method1178(var8)
               );
            }

            for (Object var12_o : (Iterable<?>)(Object)(var3)) {
               PartyMember.Data7 var12 = (PartyMember.Data7)var12_o;
               this.inviteCard(
                  "party:" + var12.getLongValue(),
                  var12.getText2(),
                  var12.getLongValue2(),
                  t("gui.friends.invite.party").replace("{p}", var12.name()).replace("{n}", String.valueOf(var12.getItems().size())),
                  t("gui.friends.invite.accept"),
                  () -> var2.method1154(var12),
                  () -> var2.method1155(var12)
               );
            }

            for (Object var13_o : (Iterable<?>)(Object)(var4)) {
               PartyMember.Data16 var13 = (PartyMember.Data16)var13_o;
               this.inviteCard(
                  "room:" + var13.getText(),
                  var13.getText3(),
                  var13.getLongValue(),
                  t("gui.friends.invite.room").replace("{r}", var13.name()),
                  t("gui.friends.invite.enter"),
                  () -> var2.method1172(var13.getText()),
                  () -> var2.method1173(var13)
               );
            }

            return var6;
         }
      }
   }

   private void inviteCard(String var1, String var2, long var3, String var5, String var6, Runnable var7, Runnable var8) {
      int var9 = this.order++;
      this.custom(
         26.0F,
         (var8x, var9x, var10, var11, var12, var13, var14, var15) -> {
            float var16 = this.appear(var1) * wave(this.listWave, var9);
            if (!(var16 <= 0.002F)) {
               var14 *= var16;
               var11 += (1.0F - var16) * 8.0F;
               float var17 = var11 + 8.0F;
               float var18 = var13 - 16.0F;
               float var19 = var12 + 1.5F;
               float var20 = 23.0F;
               boolean var21 = new BoundsData(var17, var19, var18, var20).contains(var9x, var10);
               plate(var8x, var17, var19, var18, var20, this.hover(var1, var21), 0.0F, var14, var15);
               float var22 = 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 420.0));
               var8x.drawRoundedRect(
                  var17, var19, var18, var20, ProxyDotRadiusData.method8069(4.0F), var15.getPrimaryColor().getZenithColor().method7294(var14 * 0.07F)
               );
               var8x.drawRoundedRect(
                  var17 + 1.5F,
                  var19 + 4.0F,
                  2.0F,
                  var20 - 8.0F,
                  ProxyDotRadiusData.method8069(1.0F),
                  var15.getPrimaryColor().getZenithColor().method7294(var14 * var22)
               );
               float var23 = var19 + var20 / 2.0F;
               this.avatar(var8x, var2, var3, var17 + 7.0F, var23 - 8.0F, 16.0F, var14, var15);
               Font var24 = Fonts.NEW_MEDIUM.getFont(4.8F);
               String var25 = t("gui.friends.invite.hide");
               float var26 = var24.width(var25) + 12.0F;
               float var27 = var24.width(var6) + 12.0F;
               float var28 = var23 - 6.0F;
               BoundsData var29 = new BoundsData(var17 + var18 - 6.0F - var26, var28, var26, 12.0F);
               BoundsData var30 = new BoundsData(var29.getFloatValue() - 4.0F - var27, var28, var27, 12.0F);
               int var31 = var30.contains(var9x, var10) ? 0 : (var29.contains(var9x, var10) ? 1 : -1);
               this.hoveredButton.put(var1, var31);
               Font var32 = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font var33 = Fonts.NEW_REGULAR.getFont(4.4F);
               float var34 = var17 + 7.0F + 16.0F + 6.0F;
               float var35 = var23 - (var32.height() + 2.0F + var33.height()) / 2.0F;
               float var36 = var30.getFloatValue() - 4.0F - var34;
               var8x.drawText(var32, fit(var32, var2, var36), var34, var35, var15.getTextEnable().getZenithColor().method7294(var14));
               var8x.drawText(var33, fit(var33, var5, var36), var34, var35 + var32.height() + 2.0F, var15.getTextTertiary().getZenithColor().method7294(var14));
               this.pillButton(var8x, var1 + ":yes", var30, var6, true, var31 == 0, var14, var15);
               this.pillButton(var8x, var1 + ":no", var29, var25, false, var31 == 1, var14, var15);
            }
         },
         () -> {
            int var4 = this.hoveredButton.getOrDefault(var1, -1);
            if (var4 >= 0) {
               this.press(var1 + (var4 == 0 ? ":yes" : ":no"));
               (var4 == 0 ? var7 : var8).run();
               this.invalidate();
            }
         },
         null
      );
   }

   private int section(String var1, List<String> var2, List<String> var3) {
      int var4 = var2.size() + var3.size();
      if (var4 == 0) {
         return 0;
      } else {
         this.caption(t(var1) + " · " + var4);

         for (String var6 : var2) {
            this.friendCard(var6, false, this.order++);
         }

         for (String var8 : var3) {
            this.friendCard(var8, true, this.order++);
         }

         return var4;
      }
   }

   private void hero(int var1, int var2) {
      int var3 = this.order++;
      String var4 = "hero";
      this.custom(
         40.0F,
         (var5, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = wave(this.listWave, var3);
            if (!(var13 <= 0.002F)) {
               var11 *= var13;
               var8 += (1.0F - var13) * 8.0F;
               float var14 = var8 + 8.0F;
               float var15 = var8 + var10 - 8.0F;
               float var16 = var9 + 3.0F;
               float var17 = 34.0F;
               CloudConnectionState var18 = state();
               boolean var19 = var18 != null && var18.method8035();
               float var20 = this.heroPulse.method2808(var19 ? 1.0F : 0.0F);
               plate(var5, var14, var16, var15 - var14, var17, 0.0F, 0.0F, var11, var12);
               var5.drawRoundedRect(
                  var14,
                  var16,
                  var15 - var14,
                  var17,
                  ProxyDotRadiusData.method8069(4.0F),
                  var12.getPrimaryColor().getZenithColor().method7294(var11 * 0.06F * var20)
               );
               ZenithColor var21 = var12.getTextTertiary().getZenithColor().method7295(var12.getPrimaryColor().getZenithColor(), var20);
               bigGlyph(var5, MenuGlyph.FRIENDS, var14 + 8.0F, var16 + (var17 - 12.0F) / 2.0F, var21.method7294(var11));
               Font var22 = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font var23 = Fonts.NEW_REGULAR.getFont(4.6F);
               float var24 = var14 + 8.0F + 12.0F + 8.0F;
               float var25 = var16 + (var17 - var22.height() - 3.0F - var23.height()) / 2.0F;
               var5.drawText(var22, t("gui.friends.title"), var24, var25, var12.getTextEnable().getZenithColor().method7294(var11));
               float var28 = 1.0F;
               String var26;
               ZenithColor var27;
               if (var18 == null) {
                  var26 = t("gui.cloud.state.missing");
                  var27 = DANGER;
               } else if (var19) {
                  var26 = var2 == 0
                     ? t("gui.friends.hero.none")
                     : t("gui.friends.hero.online").replace("{n}", String.valueOf(var1)).replace("{m}", String.valueOf(var2));
                  var27 = var12.getFriendColor().getZenithColor();
               } else if (var18.getType() == CloudConnectionState.Type.CONNECTING) {
                  var26 = var18.getType().label();
                  var27 = new ZenithColor(-680437);
                  var28 = 0.45F + 0.55F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 220.0));
               } else {
                  String var29 = var18.note();
                  if (var29 == null || var29.isBlank()) {
                     var29 = var18.getLastError();
                  }

                  var26 = var29 != null && !var29.isBlank() ? var29 : var18.getType().label();
                  var27 = DANGER;
               }

               float var44 = 3.0F;
               float var30 = var25 + var22.height() + 3.0F;
               var5.drawRoundedRect(
                  var24,
                  var30 + var23.height() / 2.0F - var44 / 2.0F,
                  var44,
                  var44,
                  ProxyDotRadiusData.method8069(var44 / 2.0F),
                  var27.method7294(var11 * var28)
               );
               Font var31 = Fonts.NEW_MEDIUM.getFont(5.0F);
               String var32 = t(this.addOpen ? "gui.friends.hero.hide" : "gui.friends.hero.add");
               float var33 = var31.width(var32) + 18.0F;
               float var34 = 13.0F;
               BoundsData var35 = new BoundsData(var15 - 8.0F - var33, var16 + (var17 - var34) / 2.0F, var33, var34);
               boolean var36 = var35.contains(var6, var7);
               this.hoveredButton.put(var4, var36 ? 0 : -1);
               if (var36) {
                  HoverInfo.request(t("gui.friends.caption.add"), t("gui.friends.add.desc"));
               } else if (new BoundsData(var14, var16, var15 - var14, var17).contains(var6, var7)) {
                  HoverInfo.request(t("gui.friends.title"), t("gui.friends.hero.desc"));
               }

               float var37 = this.hover(var4 + ":pill", var36);
               float var38 = this.pick(var4 + ":open", this.addOpen);
               ZenithColor var39 = var12.getPrimaryColor().getZenithColor().method7295(var12.getDisableActiveBg().getZenithColor(), var38);
               var5.drawRoundedRect(
                  var35.getFloatValue() - var37,
                  var35.getFloatValue2() - var37 * 0.5F,
                  var35.width() + var37 * 2.0F,
                  var35.height() + var37,
                  ProxyDotRadiusData.method8069(var34 / 2.0F),
                  var39.method7294(var11)
               );
               plusGlyph(
                  var5,
                  var35.getFloatValue() + 8.0F,
                  var35.getFloatValue2() + var34 / 2.0F,
                  var38,
                  new ZenithColor(255, 255, 255).method7295(var12.getTextEnable().getZenithColor(), var38).method7294(var11)
               );
               var5.drawText(
                  var31,
                  var32,
                  var35.getFloatValue() + 13.0F,
                  var35.getFloatValue2() + (var34 - var31.height()) / 2.0F,
                  new ZenithColor(255, 255, 255).method7295(var12.getTextEnable().getZenithColor(), var38).method7294(var11)
               );
               float var41 = var35.getFloatValue() - 8.0F - (var24 + var44 + 4.0F);
               var5.drawText(var23, fit(var23, var26, var41), var24 + var44 + 4.0F, var30, var12.getTextTertiary().getZenithColor().method7294(var11));
            }
         },
         () -> {
            if (this.hoveredButton.getOrDefault(var4, -1) == 0) {
               this.press(var4);
               if (this.addOpen) {
                  this.closeAdd();
               } else {
                  this.openAdd();
               }
            }
         },
         null
      );
   }

   private void emptyState(String var1) {
      int var2 = this.order++;
      String var3 = var1.isEmpty() ? t("gui.friends.empty.text") : t("gui.friends.emptyQuery").replace("{q}", var1);
      Font var4 = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> var5 = wrapText(var3, var4, this.mainWidth() - 16.0F - 30.0F, 4);
      float var6 = var4.height() + 2.0F;
      float var7 = Math.max(48.0F, 30.0F + var5.size() * var6);
      this.custom(var7, (var6x, var7x, var8, var9, var10, var11, var12, var13) -> {
         float var14 = wave(this.listWave, var2);
         if (!(var14 <= 0.002F)) {
            var12 *= var14;
            float var15 = var9 + var11 / 2.0F;
            float var16 = (float)Math.sin(System.currentTimeMillis() / 700.0) * 1.2F;
            bigGlyph(var6x, MenuGlyph.FRIENDS, var15 - 6.0F, var10 + 6.0F + var16, var13.getTextTertiary().getZenithColor().method7294(var12 * 0.8F));
            Font var17 = Fonts.NEW_MEDIUM.getFont(5.4F);
            String var18 = t(var1.isEmpty() ? "gui.friends.empty.title" : "gui.friends.filter.empty");
            var6x.drawText(var17, var18, var15 - var17.width(var18) / 2.0F, var10 + 22.0F, var13.getTextEnable().getZenithColor().method7294(var12));
            float var19 = var10 + 22.0F + var17.height() + 3.0F;

            for (Object var21_o : (Iterable<?>)(Object)(var5)) {
               String var21 = (String)var21_o;
               var6x.drawText(var4, var21, var15 - var4.width(var21) / 2.0F, var19, var13.getTextTertiary().getZenithColor().method7294(var12));
               var19 += var6;
            }
         }
      });
   }

   private void filterChips(int[] var1) {
      int var2 = this.order++;
      String var3 = "filter";
      String[] var4 = new String[]{"gui.friends.filter.all", "gui.friends.filter.online", "gui.friends.filter.near", "gui.friends.filter.offline"};
      this.custom(
         22.0F,
         (var5, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = wave(this.listWave, var2);
            if (!(var13 <= 0.002F)) {
               var11 *= var13;
               var8 += (1.0F - var13) * 8.0F;
               Font var14 = Fonts.NEW_MEDIUM.getFont(4.8F);
               Font var15 = Fonts.NEW_MEDIUM.getFont(4.2F);
               float var16 = var8 + 8.0F;
               float var17 = var9 + 3.0F;
               int var18 = -1;

               for (int var19 = 0; var19 < var4.length; var19++) {
                  String var20 = t(var4[var19]);
                  String var21 = String.valueOf(var1[var19]);
                  float var22 = 8.0F + var14.width(var20) + 4.0F + var15.width(var21) + 8.0F;
                  BoundsData var23 = new BoundsData(var16, var17, var22, 16.0F);
                  boolean var24 = var23.contains(var6, var7);
                  if (var24) {
                     var18 = var19;
                     HoverInfo.request(var20, t("gui.friends.filter.desc"));
                  }

                  float var25 = this.hover(var3 + var19, var24);
                  float var26 = this.pick(var3 + var19, this.filter == var19);
                  var5.drawRoundedRect(
                     var23.getFloatValue(),
                     var23.getFloatValue2(),
                     var23.width(),
                     var23.height(),
                     ProxyDotRadiusData.method8069(var23.height() / 2.0F),
                     var12.getDisableActiveBg()
                        .getZenithColor()
                        .method7294(var11 * (0.4F + 0.3F * var25))
                        .method7295(var12.getPrimaryColor().getZenithColor().method7294(var11 * 0.9F), var26)
                  );
                  ZenithColor var27 = var12.getTextSecondary()
                     .getZenithColor()
                     .method7295(var12.getTextEnable().getZenithColor(), var25)
                     .method7295(new ZenithColor(255, 255, 255), var26);
                  var5.drawText(
                     var14, var20, var23.getFloatValue() + 8.0F, var23.getFloatValue2() + (var23.height() - var14.height()) / 2.0F, var27.method7294(var11)
                  );
                  var5.drawText(
                     var15,
                     var21,
                     var23.getFloatValue() + 8.0F + var14.width(var20) + 4.0F,
                     var23.getFloatValue2() + (var23.height() - var15.height()) / 2.0F,
                     var12.getTextTertiary().getZenithColor().method7295(new ZenithColor(255, 255, 255, 200), var26).method7294(var11)
                  );
                  var16 += var22 + 4.0F;
               }

               this.hoveredButton.put(var3, var18);
            }
         },
         () -> {
            int var2x = this.hoveredButton.getOrDefault(var3, -1);
            if (var2x >= 0 && var2x != this.filter) {
               this.filter = var2x;
               this.startListWave();
               this.invalidate();
            }
         },
         null
      );
   }

   private void buildAddBlock() {
      if (this.addOpen || !this.noticeText.isEmpty()) {
         if (this.addOpen) {
            this.addInputField();
            this.buildSuggestions();
         }

         this.buildNotice();
         this.gap(GuiStyle.PADDING.intValue());
      }
   }

   private void addInputField() {
      int var1 = this.order++;
      String var2 = "add.field";
      this.custom(
         26.0F,
         (var3, var4, var5, var6, var7, var8, var9, var10) -> {
            float var11 = wave(this.listWave, var1);
            if (!(var11 <= 0.002F)) {
               var9 *= var11;
               float var12 = var6 + 8.0F + (1.0F - var11) * 8.0F;
               float var13 = var8 - 16.0F;
               float var14 = var7 + 2.0F;
               float var15 = 22.0F;
               boolean var16 = this.addRow.isEditing();
               float var17 = this.hover(var2 + ":focus", var16);
               BoundsData var18 = new BoundsData(var12, var14, var13, var15);
               boolean var19 = var18.contains(var4, var5);
               var3.drawRoundedRect(
                  var12,
                  var14,
                  var13,
                  var15,
                  ProxyDotRadiusData.method8069(var15 / 2.0F),
                  var10.getFieldSurfaceBackground().getZenithColor().method7294(var9 * (2.0F + this.hover(var2 + ":hot", var19) * 0.8F))
               );
               var3.drawRoundedBorder(
                  var12,
                  var14,
                  var13,
                  var15,
                  0.15F,
                  ProxyDotRadiusData.method8069(var15 / 2.0F),
                  var10.getFieldBorder().getZenithColor().method7295(var10.getPrimaryColor().getZenithColor(), var17).method7294(var9 * (1.4F + var17))
               );
               float var20 = var12 + 9.0F;
               MenuGlyph.SEARCH
                  .draw(
                     var3,
                     var20,
                     var14 + (var15 - 6.0F) / 2.0F,
                     var10.getTextTertiary().getZenithColor().method7295(var10.getPrimaryColor().getZenithColor(), var17).method7294(var9)
                  );
               String var21 = this.typedName();
               boolean var22 = !var21.isEmpty();
               Font var23 = Fonts.NEW_MEDIUM.getFont(5.2F);
               String var24 = t(this.searching ? "gui.friends.add.wait" : "gui.friends.add");
               float var25 = Fonts.NEW_MEDIUM.getFont(4.8F).width(var24) + 20.0F;
               float var26 = var15 - 5.0F;
               BoundsData var27 = new BoundsData(var12 + var13 - 3.0F - var25, var14 + 2.5F, var25, var26);
               float var28 = 11.0F;
               BoundsData var29 = new BoundsData(var27.getFloatValue() - 4.0F - var28, var14 + (var15 - var28) / 2.0F, var28, var28);
               float var30 = var20 + 6.0F + 6.0F;
               float var31 = (var22 ? var29.getFloatValue() : var27.getFloatValue()) - 6.0F;
               float var32 = var14 + (var15 - var23.height()) / 2.0F;
               if (var22) {
                  String var33 = fit(var23, var21, var31 - var30);
                  float var34 = var30 + var23.width(var33);
                  var3.drawText(var23, var33, var30, var32, var10.getTextEnable().getZenithColor().method7294(var9));
                  if (var16 && System.currentTimeMillis() % 1000L < 550L) {
                     var3.drawRoundedRect(
                        var34 + 0.5F,
                        var32 - 0.5F,
                        1.0F,
                        var23.height() + 1.0F,
                        ProxyDotRadiusData.method8069(0.5F),
                        var10.getPrimaryColor().getZenithColor().method7294(var9)
                     );
                  }
               } else {
                  float var41 = var16 && System.currentTimeMillis() % 1000L < 550L ? 1.0F : 0.0F;
                  if (var41 > 0.0F) {
                     var3.drawRoundedRect(
                        var30 + 0.5F,
                        var32 - 0.5F,
                        1.0F,
                        var23.height() + 1.0F,
                        ProxyDotRadiusData.method8069(0.5F),
                        var10.getPrimaryColor().getZenithColor().method7294(var9)
                     );
                  }

                  var3.drawText(
                     var23, t("gui.friends.nick.hint"), var30 + var41 + 2.0F, var32, var10.getTextTertiary().getZenithColor().method7294(var9 * 0.8F)
                  );
               }

               byte var42 = -1;
               if (var22) {
                  boolean var43 = var29.contains(var4, var5);
                  if (var43) {
                     var42 = 1;
                  }

                  float var35 = this.hover(var2 + ":clear", var43);
                  var3.drawRoundedRect(
                     var29.getFloatValue(),
                     var29.getFloatValue2(),
                     var29.width(),
                     var29.height(),
                     ProxyDotRadiusData.method8069(var28 / 2.0F),
                     var10.getDisableActiveBg().getZenithColor().method7294(var9 * (0.4F + var35 * 0.5F))
                  );
                  plusGlyph(
                     var3,
                     var29.getFloatValue() + var28 / 2.0F,
                     var29.getFloatValue2() + var28 / 2.0F,
                     1.0F,
                     var10.getTextSecondary().getZenithColor().method7295(var10.getTextEnable().getZenithColor(), var35).method7294(var9)
                  );
                  boolean var36 = var27.contains(var4, var5);
                  if (var36 && !this.searching) {
                     var42 = 0;
                  }

                  float var37 = this.hover(var2 + ":pill", var36 && !this.searching);
                  float var38 = this.searching ? 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0)) : 1.0F;
                  var3.drawRoundedRect(
                     var27.getFloatValue(),
                     var27.getFloatValue2(),
                     var27.width(),
                     var27.height(),
                     ProxyDotRadiusData.method8069(var26 / 2.0F),
                     var10.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), var37 * 0.15F).method7294(var9 * var38)
                  );
                  Font var39 = Fonts.NEW_MEDIUM.getFont(4.8F);
                  var3.drawText(
                     var39,
                     var24,
                     var27.getFloatValue() + (var27.width() - var39.width(var24)) / 2.0F,
                     var27.getFloatValue2() + (var27.height() - var39.height()) / 2.0F,
                     new ZenithColor(255, 255, 255).method7294(var9 * var38)
                  );
               }

               this.hoveredButton.put(var2, Integer.valueOf(var42));
               if (var19 && var42 < 0) {
                  HoverInfo.request(t("gui.friends.caption.add"), t("gui.friends.add.desc"));
               }
            }
         },
         () -> {
            int var2x = this.hoveredButton.getOrDefault(var2, -1);
            if (var2x == 0) {
               this.press(var2 + ":pill");
               this.submitAdd();
            } else if (var2x == 1) {
               this.press(var2 + ":clear");
               this.clearInput();
               if (!this.addRow.isEditing()) {
                  this.addRow.beginEdit();
               }

               this.invalidate();
            } else if (!this.addRow.isEditing()) {
               this.addRow.beginEdit();
               this.invalidate();
            }
         },
         null
      );
   }

   private void openAdd() {
      this.addOpen = true;
      this.addRow.beginEdit();
      this.startListWave();
      this.invalidate();
   }

   public void devOpenAdd(String var1) {
      this.selectTab(0);
      this.openAdd();
      if (var1 != null && !var1.isEmpty()) {
         this.addTargetSetting.setValue(var1);
         this.pollTyping();
      }

      this.invalidate();
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.getActiveTab() == 0 && this.addOpen && this.addRow.isEditing()) {
         switch (var1) {
            case 256:
               this.closeAdd();
               return true;
            case 257:
            case 335:
               if (!this.typedName().isEmpty()) {
                  this.submitAdd();
               }

               return true;
            case 258:
               this.completeToSuggestion();
               return true;
            default:
               this.addRow.keyPressed(var1, var2, var3);
               this.pollTyping();
               this.invalidate();
               return true;
         }
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.getActiveTab() == 0 && this.addOpen && this.addRow.isEditing()) {
         this.addRow.charTyped(var1, var2);
         this.pollTyping();
         this.invalidate();
         return true;
      } else {
         return super.charTyped(var1, var2);
      }
   }

   private void completeToSuggestion() {
      String var1 = this.typedName();

      for (FriendsSectionPanel.Hint var3 : this.ordered(var1)) {
         if (!var3.name().equalsIgnoreCase(var1)) {
            this.addTargetSetting.setValue(var3.name());
            this.addRow.endEdit();
            this.addRow.beginEdit();
            this.invalidate();
            return;
         }
      }
   }

   private void closeAdd() {
      this.addOpen = false;
      this.startListWave();
      this.addRow.endEdit();
      this.clearInput();
      this.setNotice("", "", false);
   }

   private void requestCard(CloudPackets.Data8 var1, int var2) {
      String var3 = var1.username();
      String var4 = "request:" + var3;
      String var5 = blankToDash(var1.getText2());
      this.custom(
         26.0F,
         (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = this.appear(var4) * wave(this.listWave, var2);
            if (!(var13 <= 0.002F)) {
               var11 *= var13;
               var8 += (1.0F - var13) * 8.0F;
               float var14 = var8 + 8.0F;
               float var15 = var10 - 16.0F;
               float var16 = var9 + 1.5F;
               float var17 = 23.0F;
               boolean var18 = new BoundsData(var14, var16, var15, var17).contains(var6, var7);
               float var19 = this.hover(var4, var18);
               plate(var5x, var14, var16, var15, var17, var19, 0.0F, var11, var12);
               float var20 = 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 420.0));
               var5x.drawRoundedRect(
                  var14, var16, var15, var17, ProxyDotRadiusData.method8069(4.0F), var12.getPrimaryColor().getZenithColor().method7294(var11 * 0.07F)
               );
               var5x.drawRoundedRect(
                  var14 + 1.5F,
                  var16 + 4.0F,
                  2.0F,
                  var17 - 8.0F,
                  ProxyDotRadiusData.method8069(1.0F),
                  var12.getPrimaryColor().getZenithColor().method7294(var11 * var20)
               );
               if (var18) {
                  HoverInfo.request(var3, t("gui.friends.request.desc").replace("{n}", var5));
               }

               float var21 = var16 + var17 / 2.0F;
               this.avatar(var5x, var3, var14 + 7.0F, var21 - 8.0F, 16.0F, var11, var12);
               Font var22 = Fonts.NEW_MEDIUM.getFont(4.8F);
               String var23 = t("gui.friends.accept.button");
               String var24 = t("gui.friends.decline");
               float var25 = var22.width(var24) + 12.0F;
               float var26 = var22.width(var23) + 12.0F;
               float var27 = var21 - 6.0F;
               BoundsData var28 = new BoundsData(var14 + var15 - 6.0F - var25, var27, var25, 12.0F);
               BoundsData var29 = new BoundsData(var28.getFloatValue() - 4.0F - var26, var27, var26, 12.0F);
               int var30 = var29.contains(var6, var7) ? 0 : (var28.contains(var6, var7) ? 1 : -1);
               this.hoveredButton.put(var4, var30);
               Font var31 = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font var32 = Fonts.NEW_REGULAR.getFont(4.4F);
               float var33 = var14 + 7.0F + 16.0F + 6.0F;
               float var34 = var21 - (var31.height() + 2.0F + var32.height()) / 2.0F;
               float var35 = var29.getFloatValue() - 4.0F - var33;
               var5x.drawText(var31, fit(var31, var3, var35), var33, var34, var12.getTextEnable().getZenithColor().method7294(var11));
               String var36 = t("gui.friends.request.want") + ("-".equals(var5) ? "" : " · " + var5);
               var5x.drawText(var32, fit(var32, var36, var35), var33, var34 + var31.height() + 2.0F, var12.getTextTertiary().getZenithColor().method7294(var11));
               this.pillButton(var5x, var4 + ":yes", var29, var23, true, var30 == 0, var11, var12);
               this.pillButton(var5x, var4 + ":no", var28, var24, false, var30 == 1, var11, var12);
            }
         },
         () -> {
            int var3x = this.hoveredButton.getOrDefault(var4, -1);
            Cloud var4x = cloud();
            if (var4x != null && var3x >= 0) {
               this.press(var4 + (var3x == 0 ? ":yes" : ":no"));
               if (var3x == 1) {
                  var4x.method7906(var3);
               } else {
                  var4x.method7905(var3);
               }

               this.invalidate();
            }
         },
         null
      );
   }

   private void friendCard(String var1, boolean var2, int var3) {
      String var4 = (var2 ? "local:" : "cloud:") + var1;
      String var5 = var2 ? var1 : displayName(var1);
      String var6 = var2 ? "gui.friends.row.local.desc" : "gui.friends.row.desc";
      this.custom(
         26.0F,
         (var7, var8, var9, var10, var11, var12, var13, var14) -> {
            float var15 = this.appear(var4) * wave(this.listWave, var3);
            if (!(var15 <= 0.002F)) {
               var13 *= var15;
               var10 += (1.0F - var15) * 8.0F;
               float var16 = var10 + 8.0F;
               float var17 = var12 - 16.0F;
               float var18 = var11 + 1.5F;
               float var19 = 23.0F;
               boolean var20 = this.selectedLocal == var2 && var1.equals(this.selected);
               boolean var21 = new BoundsData(var16, var18, var17, var19).contains(var8, var9);
               float var22 = this.hover(var4, var21);
               float var23 = this.select(var4, var20);
               plate(var7, var16, var18, var17, var19, var22, 0.0F, var13, var14);
               if (var23 > 0.002F) {
                  var7.drawRoundedRect(
                     var16,
                     var18,
                     var17,
                     var19,
                     ProxyDotRadiusData.method8069(4.0F),
                     var14.getPrimaryColor().getZenithColor().method7294(var13 * 0.08F * var23)
                  );
                  float var24 = (var19 - 8.0F) * var23;
                  var7.drawRoundedRect(
                     var16 + 1.5F,
                     var18 + (var19 - var24) / 2.0F,
                     2.0F,
                     var24,
                     ProxyDotRadiusData.method8069(1.0F),
                     var14.getPrimaryColor().getZenithColor().method7294(var13)
                  );
               }

               PartyPlayerInfo var45 = var2 ? null : peer(var1);
               boolean var25 = !var2 && !isPendingName(var1);
               BoundsData var26 = new BoundsData(var16 + var17 - 6.0F - 12.0F - 4.0F, var18 + (var19 - 12.0F) / 2.0F, 16.0F, 12.0F);
               boolean var27 = var25 && var21 && var26.contains(var8, var9);
               this.hoveredButton.put(var4, var27 ? 1 : -1);
               if (var21) {
                  HoverInfo.request(var5, t(var27 ? "gui.friends.quick.chat.desc" : var6));
               }

               float var28 = var22 * 1.5F;
               float var29 = var18 + var19 / 2.0F;
               float var30 = var16 + 7.0F + var28;
               float var31 = var29 - 8.0F;
               this.avatar(var7, var1, var30, var31, 16.0F, var13, var14);
               this.drawStatusDot(var7, var4, var1, var2, var30 + 16.0F - 4.0F + 0.5F, var31 + 16.0F - 4.0F + 0.5F, var13, var14);
               float var32 = var16 + var17 - 6.0F;
               float var33 = this.hover(var4 + ":chat", var25 && var21);
               if (var33 > 0.01F) {
                  var7.drawRoundedRect(
                     var26.getFloatValue(),
                     var26.getFloatValue2(),
                     var26.width(),
                     var26.height(),
                     ProxyDotRadiusData.method8069(var26.height() / 2.0F),
                     var14.getPrimaryColor().getZenithColor().method7294(var13 * var33 * (var27 ? 0.95F : 0.25F))
                  );
                  MenuGlyph.CHAT
                     .draw(
                        var7,
                        var26.getFloatValue() + (var26.width() - 6.0F) / 2.0F,
                        var26.getFloatValue2() + (var26.height() - 6.0F) / 2.0F,
                        (var27 ? new ZenithColor(255, 255, 255) : var14.getPrimaryColor().getZenithColor()).method7294(var13 * var33)
                     );
                  var32 = var26.getFloatValue() - 4.0F;
               }

               Font var34 = Fonts.NEW_REGULAR.getFont(4.4F);
               String var35 = var2 ? "" : sideNote(var1);
               if (!var35.isEmpty() && var33 < 0.99F) {
                  var7.drawText(
                     var34,
                     var35,
                     var32 - var34.width(var35),
                     var29 - var34.height() / 2.0F,
                     var14.getTextTertiary().getZenithColor().method7294(var13 * (1.0F - var33))
                  );
                  var32 -= (var34.width(var35) + 5.0F) * (1.0F - var33);
               }

               if (var45 != null && var45.method3289()) {
                  Voice var36 = Zenith.getInstance().getVoice();
                  boolean var37 = var36 != null && var36.getVoiceState().method5365(var45.uid());
                  if (var37) {
                     Voice.method371(
                        var7, var32 - 11.0F, var29, var36.getVoiceState().method5366(var45.uid()), var14.getPrimaryColor().getZenithColor().method7294(var13)
                     );
                     var32 -= 15.0F;
                  } else {
                     MenuGlyph.MIC.draw(var7, var32 - 6.0F, var29 - 3.0F, var14.getTextTertiary().getZenithColor().method7294(var13 * 0.8F));
                     var32 -= 11.0F;
                  }
               }

               Font var46 = Fonts.NEW_MEDIUM.getFont(5.2F);
               float var47 = var30 + 16.0F + 6.0F;
               float var38 = var32 - 4.0F - var47;
               float var39 = var29 - (var46.height() + 2.0F + var34.height()) / 2.0F;
               Prefs var40 = prefs();
               boolean var41 = !var2 && var40 != null && var40.method3319(var1);
               float var42 = var47;
               if (var41) {
                  diamond(var7, var47 + 1.5F, var39 + var46.height() / 2.0F, 3.0F, FAVORITE.method7294(var13));
                  var42 = var47 + 6.0F;
               }

               var7.drawText(
                  var46,
                  fit(var46, var5, var38 - (var42 - var47)),
                  var42,
                  var39,
                  var14.getTextSecondary()
                     .getZenithColor()
                     .method7295(var14.getTextEnable().getZenithColor(), Math.max(0.55F, Math.max(var23, var22)))
                     .method7294(var13)
               );
               var7.drawText(
                  var34,
                  fit(var34, this.cardNote(var1, var2), var38),
                  var47,
                  var39 + var46.height() + 2.0F,
                  var14.getTextTertiary().getZenithColor().method7294(var13)
               );
            }
         },
         () -> {
            if (this.hoveredButton.getOrDefault(var4, -1) == 1) {
               this.openChat(var1);
            } else {
               if (!var1.equals(this.selected) || var2 != this.selectedLocal) {
                  restart(this.cardReveal);
                  restart(this.asideWave);
               }

               this.party.clearSelection();
               this.selected = var1;
               this.selectedLocal = var2;
               this.resetAsideScroll();
               this.invalidate();
            }
         },
         null
      );
   }

   public void selectFriendFromParty(String var1) {
      if (!var1.equals(this.selected) || this.selectedLocal) {
         restart(this.cardReveal);
         restart(this.asideWave);
      }

      this.selected = var1;
      this.selectedLocal = false;
      this.resetAsideScroll();
      this.invalidate();
   }

   public void partySelectionChanged() {
      this.selected = null;
      restart(this.asideWave);
      this.resetAsideScroll();
   }

   public void partyNotice(String var1) {
      this.flashMessage(var1);
   }

   public String selectedFriendName() {
      return this.selectedLocal ? null : this.selected;
   }

   private String cardNote(String var1, boolean var2) {
      if (var2) {
         return t(this.isNearby(var1) ? "gui.friends.nearbyWorld" : "gui.friends.localFriend");
      } else {
         CloudConnectionState var3 = state();
         PartyPlayerInfo var4 = peer(var1);
         if (var4 != null) {
            String var5 = presenceNote(var4.uid());
            return var5.isEmpty() ? whereLine(var1) : var5 + " · " + whereLine(var1);
         } else {
            return var3 != null && var3.method8061(var1) ? t("gui.friends.status.pendingLong") : lastSeen(var1);
         }
      }
   }

   private static String presenceNote(long var0) {
      Cloud var2 = cloud();
      if (var2 != null && var2.getPartyMember().method1142(var0)) {
         PartyMember.Data2 var3 = var2.getPartyMember().method1141(var0);
         boolean var4 = "online".equals(var3.getText()) || "menu".equals(var3.getText());
         if (var4 && var3.text().isEmpty()) {
            return "";
         } else {
            return var4 ? var3.text() : PartyManager.method52(var3);
         }
      } else {
         return "";
      }
   }

   private static String sideNote(String var0) {
      PartyPlayerInfo var1 = peer(var0);
      Cloud var2 = cloud();
      if (var1 != null && var2 != null) {
         return var1.method3291(var2.method7911(), var2.method7913()) ? distanceTo(var0) : "";
      } else {
         return "";
      }
   }

   private float appear(String var1) {
      return this.buttonHover.computeIfAbsent("appear:" + var1, var0 -> new Animation(240L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
   }

   private void drawStatusDot(MenuDrawContext var1, String var2, String var3, boolean var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = this.barFill
         .computeIfAbsent("dot:" + var2, var0 -> new Animation(320L, 0.0F, Easing.INSTANCE13))
         .method2808(var4 ? (this.isNearby(var3) ? 1.0F : 0.0F) : statusLit(var3));
      ZenithColor var10 = var4 ? var8.getFriendColor().getZenithColor() : statusColor(var3, var8);
      CloudConnectionState var11 = state();
      if (!var4 && var11 != null && var11.method8061(var3) && peer(var3) == null) {
         var9 *= 0.45F + 0.55F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 420.0));
      }

      float var12 = 6.0F;
      var1.drawRoundedRect(
         var5 - 1.0F,
         var6 - 1.0F,
         var12,
         var12,
         ProxyDotRadiusData.method8069(var12 / 2.0F),
         var8.getRightBackground().getZenithColor().method7292(1.0F).method7294(var7)
      );
      var1.drawRoundedRect(
         var5, var6, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), var8.getTextTertiary().getZenithColor().method7295(var10, var9).method7294(var7)
      );
   }

   private void avatar(MenuDrawContext var1, String var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      this.avatar(var1, var2, 0L, var3, var4, var5, var6, var7);
   }

   private void avatar(MenuDrawContext var1, String var2, long var3, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      long var10 = var3 > 0L ? var3 : uidOf(var2);
      Identifier var12 = var10 > 0L ? CloudAvatarCache.get(Long.toString(var10)) : null;
      ProxyDotRadiusData var13 = ProxyDotRadiusData.method8069(var7 / 2.0F);
      if (var12 != null) {
         float var16 = this.avatarFade.computeIfAbsent(var10, var0 -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
         if (var16 < 1.0F) {
            var1.drawRoundedRect(var5, var6, var7, var7, var13, var9.getSurfaceEnableBackground().getZenithColor().method7294(var8 * 2.4F * (1.0F - var16)));
         }

         var1.drawRoundedTexture(var12, var5, var6, var7, var7, var13, ZenithColor.INSTANCE.method7294(var8 * var16));
      } else {
         var1.drawRoundedRect(var5, var6, var7, var7, var13, var9.getSurfaceEnableBackground().getZenithColor().method7294(var8 * 2.4F));
         String var14 = var2 != null && !var2.isEmpty() ? var2.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         Font var15 = Fonts.NEW_MEDIUM.getFont(var7 * 0.5F);
         var1.drawText(
            var15,
            var14,
            var5 + (var7 - var15.width(var14)) / 2.0F,
            var6 + (var7 - var15.height()) / 2.0F,
            var9.getTextTertiary().getZenithColor().method7294(var8)
         );
      }
   }

   private void submitAdd() {
      String var1 = this.typedName();
      if (var1.isEmpty()) {
         this.setNotice(t("gui.friends.notice.empty"), "", false);
      } else if (this.isAlreadyFriend(var1)) {
         this.setNotice(t("gui.friends.notice.already").replace("{n}", var1), "", false);
      } else {
         UserSearchClient.Data var2 = this.exactMatch(var1);
         if (var2 != null) {
            this.addCloud(var2);
         } else if (UserSearchClient.method4759(var1)) {
            this.setNotice(t("gui.friends.notice.short"), "", false);
         } else if (!this.searching && var1.equalsIgnoreCase(this.suggestFor)) {
            this.setNotice(t("gui.friends.notice.notFound").replace("{n}", var1), var1, true);
         } else {
            this.submitWhenReady = true;
            this.setNotice(t("gui.friends.notice.searching").replace("{n}", var1), "", false);
            this.askDirectory(var1);
         }
      }
   }

   private void addCloud(UserSearchClient.Data var1) {
      Cloud var2 = cloud();
      if (var2 != null) {
         var2.method7904(0, var1.name());
         if (var2.prefs() != null) {
            var2.prefs().method3312(var1.name(), var1.uid());
         }
      }

      this.setNotice((var2 != null && var2.method7862() ? t("gui.friends.notice.sent") : t("gui.friends.notice.queued")).replace("{n}", var1.name()), "", false);
      this.clearInput();
      UserSearchClient.method4762();
      this.invalidate();
   }

   private void addBlind(String var1) {
      Cloud var2 = cloud();
      if (var2 != null && var2.method7904(0, var1)) {
         this.setNotice((var2.method7862() ? t("gui.friends.notice.sent") : t("gui.friends.notice.queued")).replace("{n}", var1), "", false);
         this.clearInput();
         this.invalidate();
      } else {
         this.setNotice(t("gui.friends.notice.already").replace("{n}", var1), "", false);
      }
   }

   private void addLocal(String var1) {
      Zenith.getInstance().getFriends().add(var1);
      Zenith.getInstance().getFriends().save();
      this.setNotice(t("gui.friends.notice.addedLocal").replace("{n}", var1), "", false);
      this.clearInput();
      this.invalidate();
   }

   private void clearInput() {
      this.addTargetSetting.setValue("");
      this.typed = "";
      this.suggestFor = "";
      this.suggestions = List.of();
      this.submitWhenReady = false;
   }

   private String typedName() {
      String var1 = this.addTargetSetting.getValue();
      return var1 == null ? "" : var1.strip();
   }

   private UserSearchClient.Data exactMatch(String var1) {
      for (UserSearchClient.Data var3 : this.suggestions) {
         if (var3.name().equalsIgnoreCase(var1)) {
            return var3;
         }
      }

      return null;
   }

   private void pollTyping() {
      String var1 = this.typedName();
      if (!var1.equals(this.typed)) {
         this.typed = var1;
         this.typedAt = System.currentTimeMillis();
         if (var1.isEmpty()) {
            this.suggestions = List.of();
            this.suggestFor = "";
            this.invalidate();
         }
      } else if (!var1.isEmpty()
         && !this.searching
         && !UserSearchClient.method4759(var1)
         && !var1.equalsIgnoreCase(this.suggestFor)
         && System.currentTimeMillis() - this.typedAt >= 260L) {
         this.askDirectory(var1);
      }
   }

   private void askDirectory(String var1) {
      var var2 = UserSearchClient.method4758(var1);
      if (var2 != null) {
         this.applyDirectory(var1, var2, null);
      } else {
         this.searching = true;
         UserSearchClient.method4760(var1, 6)
            .whenComplete((var2x, var3) -> mc.execute(() -> this.applyDirectory(var1, (List<UserSearchClient.Data>)var2x, var3)));
      }
   }

   private void applyDirectory(String var1, List<UserSearchClient.Data> var2, Throwable var3) {
      this.searching = false;
      this.suggestFor = var1;
      this.directoryAnswered = var3 == null;
      boolean var4 = !sameNames(this.suggestions, var2);
      this.suggestions = var2 == null ? List.of() : var2;
      if (var4) {
         restart(this.suggestWave);
      }

      if (var3 != null) {
         this.suggestions = List.of();
         if (this.submitWhenReady) {
            this.setNotice(t("gui.friends.notice.searchFailed"), var1, true);
         }

         this.submitWhenReady = false;
         this.invalidate();
      } else {
         if (this.submitWhenReady) {
            this.submitWhenReady = false;
            UserSearchClient.Data var5 = this.exactMatch(var1);
            if (var5 != null) {
               this.addCloud(var5);
               return;
            }

            this.setNotice(t("gui.friends.notice.notFound").replace("{n}", var1), var1, true);
         }

         this.invalidate();
      }
   }

   private void buildSuggestions() {
      String var1 = this.typedName();
      if (!var1.isEmpty() && !UserSearchClient.method4759(var1)) {
         List<FriendsSectionPanel.Hint> var2 = this.ordered(var1);
         if (var2.isEmpty()) {
            if (this.searching) {
               this.searchingRow();
            }
         } else {
            int var3 = 0;

            for (Object var5_o : (Iterable<?>)(Object)(var2)) {
               FriendsSectionPanel.Hint var5 = (FriendsSectionPanel.Hint)var5_o;
               this.suggestionCard(var5, var1, var3++);
            }

            if (this.searching) {
               this.searchingRow();
            }
         }
      }
   }

   private List<FriendsSectionPanel.Hint> ordered(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      LinkedHashSet var5 = new LinkedHashSet();
      var var6 = this.onlineNames();

      for (UserSearchClient.Data var8 : this.suggestions) {
         boolean var9 = !var6.contains(var8.name().toLowerCase(Locale.ROOT));
         this.offer(new FriendsSectionPanel.Hint(var8.name(), var9 ? "gui.friends.hint.offline" : "", var8.uid(), 0), var2, var5, var3, var4);
      }

      CloudConnectionState var11 = state();
      if (var11 != null) {
         for (PartyPlayerInfo var15 : var11.method8038()) {
            this.offer(new FriendsSectionPanel.Hint(var15.username(), "", var15.uid(), 0), var2, var5, var3, var4);
         }
      }

      for (String var16 : this.serverPlayers()) {
         this.offer(new FriendsSectionPanel.Hint(var16, "", 0L, 1), var2, var5, var3, var4);
      }

      var3.addAll(var4);
      Object var14 = var3.size() > 6 ? var3.subList(0, 6) : var3;
      FriendsSectionPanel.Hint var17 = this.localHint(var1, var5);
      if (var17 == null) {
         return (List<FriendsSectionPanel.Hint>)var14;
      } else {
         var var10 = new ArrayList<>((Collection)var14);
         var10.add(var17);
         return var10;
      }
   }

   private FriendsSectionPanel.Hint localHint(String var1, Set<String> var2) {
      String var3 = var1.strip();
      if (!var3.isEmpty() && !this.isSelf(var3) && !this.isAlreadyFriend(var3) && !var2.contains(var3.toLowerCase(Locale.ROOT))) {
         boolean var4 = this.directoryAnswered && !this.searching && var3.equalsIgnoreCase(this.suggestFor);
         return new FriendsSectionPanel.Hint(var3, var4 ? "gui.friends.hint.local" : "gui.friends.hint.unchecked", 0L, -1);
      } else {
         return null;
      }
   }

   private Set<String> onlineNames() {
      LinkedHashSet var1 = new LinkedHashSet();
      CloudConnectionState var2 = state();
      if (var2 != null) {
         for (PartyPlayerInfo var4 : var2.method8038()) {
            if (var4.username() != null) {
               var1.add(var4.username().toLowerCase(Locale.ROOT));
            }
         }
      }

      for (String var6 : this.serverPlayers()) {
         var1.add(var6.toLowerCase(Locale.ROOT));
      }

      return var1;
   }

   private void offer(FriendsSectionPanel.Hint var1, String var2, Set<String> var3, List<FriendsSectionPanel.Hint> var4, List<FriendsSectionPanel.Hint> var5) {
      String var6 = var1.name();
      if (var6 != null && !var6.isBlank() && !this.isAlreadyFriend(var6)) {
         String var7 = var6.toLowerCase(Locale.ROOT);
         if (!this.isSelf(var6) && var3.add(var7)) {
            if (var7.startsWith(var2)) {
               var4.add(var1);
            } else if (var7.contains(var2)) {
               var5.add(var1);
            }
         }
      }
   }

   private boolean isSelf(String var1) {
      return mc.getSession() != null && var1.equalsIgnoreCase(mc.getSession().getUsername());
   }

   private List<String> serverPlayers() {
      if (mc.getNetworkHandler() == null) {
         return List.of();
      } else {
         ArrayList<String> var1 = new ArrayList<>();

         for (PlayerListEntry var3 : mc.getNetworkHandler().getPlayerList()) {
            String var4 = var3.getProfile() == null ? null : var3.getProfile().name();
            if (var4 != null && !var4.isBlank()) {
               var1.add(var4);
            }
         }

         return var1;
      }
   }

   private void suggestionCard(FriendsSectionPanel.Hint var1, String var2, int var3) {
      String var4 = "suggest:" + var1.name();
      this.custom(
         26.0F,
         (var5, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = wave(this.suggestWave, var3);
            if (!(var13 <= 0.002F)) {
               float var14 = var11 * var13;
               float var15 = var8 + 8.0F + (1.0F - var13) * 6.0F;
               float var16 = var10 - 16.0F - (1.0F - var13) * 6.0F;
               float var17 = var9 + 1.5F;
               float var18 = 23.0F;
               boolean var19 = new BoundsData(var15, var17, var16, var18).contains(var6, var7);
               plate(var5, var15, var17, var16, var18, this.hover(var4, var19), 0.0F, var14, var12);
               if (var19) {
                  HoverInfo.request(
                     var1.name(), t(var1.addKind() == -1 ? "gui.friends.suggest.descLocal" : "gui.friends.suggest.desc").replace("{n}", var1.name())
                  );
               }

               float var20 = var17 + var18 / 2.0F;
               float var21 = var15 + 7.0F;
               this.avatar(var5, var1.name(), var1.uid(), var21, var20 - 8.0F, 16.0F, var14, var12);
               Font var22 = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font var23 = Fonts.NEW_MEDIUM.getFont(4.4F);
               String var24 = t(var1.addKind() == -1 ? "gui.friends.suggest.addLocal" : "gui.friends.suggest.add");
               float var25 = var23.width(var24) + 11.0F;
               float var26 = 11.0F;
               BoundsData var27 = new BoundsData(var15 + var16 - 6.0F - var25, var20 - var26 / 2.0F, var25, var26);
               float var28 = this.hover(var4 + ":pill", var19);
               ZenithColor var29 = var12.getPrimaryColor().getZenithColor();
               var5.drawRoundedRect(
                  var27.getFloatValue(),
                  var27.getFloatValue2(),
                  var27.width(),
                  var27.height(),
                  ProxyDotRadiusData.method8069(var26 / 2.0F),
                  var29.method7292(0.12F + var28 * 0.7F).method7294(var14)
               );
               var5.drawText(
                  var23,
                  var24,
                  var27.getFloatValue() + (var27.width() - var23.width(var24)) / 2.0F,
                  var27.getFloatValue2() + (var26 - var23.height()) / 2.0F,
                  var29.method7295(new ZenithColor(255, 255, 255), var28).method7294(var14)
               );
               Font var30 = Fonts.NEW_REGULAR.getFont(4.2F);
               String var31 = var1.source().isEmpty() ? "" : t(var1.source());
               float var32 = var31.isEmpty() ? 0.0F : var30.width(var31);
               if (!var31.isEmpty()) {
                  var5.drawText(
                     var30,
                     var31,
                     var27.getFloatValue() - 5.0F - var32,
                     var20 - var30.height() / 2.0F,
                     var12.getTextTertiary().getZenithColor().method7294(var14 * 0.8F)
                  );
               }

               float var33 = var21 + 16.0F + 6.0F;
               float var34 = var27.getFloatValue() - 9.0F - var32 - var33;
               this.drawMatchedName(var5, var22, var1.name(), var2, var33, var20 - var22.height() / 2.0F, var34, var14, var12);
            }
         },
         () -> {
            this.press(var4 + ":pill");
            this.addHint(var1);
         },
         null
      );
   }

   private void addHint(FriendsSectionPanel.Hint var1) {
      if (var1.addKind() == -1) {
         this.addLocal(var1.name());
      } else {
         Cloud var2 = cloud();
         if (var2 != null) {
            if (var1.addKind() == 0) {
               if (var1.uid() > 0L && var2.prefs() != null) {
                  var2.prefs().method3312(var1.name(), var1.uid());
               }

               this.addBlind(var1.name());
               UserSearchClient.method4762();
            } else if (!var2.method7904(1, var1.name())) {
               this.setNotice(t("gui.friends.notice.offlineCloud"), var1.name(), true);
            } else {
               this.setNotice(t("gui.friends.notice.sent").replace("{n}", var1.name()), "", false);
               this.clearInput();
               this.invalidate();
            }
         }
      }
   }

   private void drawMatchedName(MenuDrawContext var1, Font var2, String var3, String var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      ZenithColor var10 = var9.getTextSecondary().getZenithColor().method7294(var8);
      String var11 = fit(var2, var3, var7);
      int var12 = !var4.isEmpty() && var11.toLowerCase(Locale.ROOT).startsWith(var4.toLowerCase(Locale.ROOT)) ? var4.length() : 0;
      if (var12 != 0 && var12 <= var11.length()) {
         String var13 = var11.substring(0, var12);
         var1.drawText(var2, var13, var5, var6, var9.getPrimaryColor().getZenithColor().method7294(var8));
         var1.drawText(var2, var11.substring(var12), var5 + var2.width(var13), var6, var10);
      } else {
         var1.drawText(var2, var11, var5, var6, var10);
      }
   }

   private void searchingRow() {
      this.custom(
         16.0F,
         (var0, var1, var2, var3, var4, var5, var6, var7) -> {
            Font var8 = Fonts.NEW_REGULAR.getFont(4.8F);
            String var9 = t("gui.friends.suggest.searching");
            float var10 = 0.55F + 0.45F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 260.0));
            var0.drawText(var8, var9, var3 + 8.0F, var4 + (16.0F - var8.height()) / 2.0F, var7.getTextTertiary().getZenithColor().method7294(var6 * var10));
            float var11 = 3.0F;
            float var12 = var3 + 8.0F + var8.width(var9) + 4.0F;

            for (int var13 = 0; var13 < 3; var13++) {
               float var14 = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 220.0 - var13 * 0.7));
               var0.drawRoundedRect(
                  var12 + var13 * (var11 + 2.0F),
                  var4 + (16.0F - var11) / 2.0F,
                  var11,
                  var11,
                  ProxyDotRadiusData.method8069(var11 / 2.0F),
                  var7.getPrimaryColor().getZenithColor().method7294(var6 * (0.25F + 0.55F * var14))
               );
            }
         }
      );
   }

   private void buildNotice() {
      if (!this.noticeText.isEmpty()) {
         this.noticeBox(this.noticeText);
         if (this.offerOffline) {
            String var1 = this.noticeTarget;
            this.buttonsRow(
               "notice.actions", "gui.friends.notice.addLocal", () -> this.addLocal(var1), "gui.friends.notice.sendCloud", () -> this.addBlind(var1)
            );
         }
      }
   }

   private void setNotice(String var1, String var2, boolean var3) {
      this.noticeText = var1;
      this.noticeTarget = var2;
      this.offerOffline = var3 && !var2.isEmpty();
      this.invalidate();
   }

   private static boolean sameNames(List<UserSearchClient.Data> var0, List<UserSearchClient.Data> var1) {
      var var2 = var1 == null ? List.of() : var1;
      if (var0.size() != var2.size()) {
         return false;
      } else {
         for (int var3 = 0; var3 < var2.size(); var3++) {
            if (!((UserSearchClient.Data)var0.get(var3)).name().equals(((UserSearchClient.Data)var2.get(var3)).name())) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isAlreadyFriend(String var1) {
      Cloud var2 = cloud();
      if (var2 != null && var2.getFriends().method6442(var1)) {
         return true;
      } else {
         var var3 = Zenith.getInstance().getFriends().getItems();
         if (var3 == null) {
            return false;
         } else {
            for (Object var5_o : (Iterable<?>)(Object)(List.copyOf(var3))) {
               String var5 = (String)var5_o;
               if (var5.equalsIgnoreCase(var1)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private void buildSharing() {
      Share var1 = Zenith.getInstance().getShare();
      this.shareHero(var1);
      this.caption("gui.friends.caption.cosmetics");
      this.shareTiles(var1);
      this.caption("gui.friends.caption.shareWhat");
      this.shareToggle(var1.position, MenuGlyph.GPS);
      this.shareToggle(var1.server, MenuGlyph.WORLD);
      this.shareToggle(var1.health, MenuGlyph.HEART);
      this.shareToggle(var1.inventory, MenuGlyph.CHESTPLATE);
      this.caption("gui.friends.caption.social");
      this.shareToggle(var1.allowJoin, MenuGlyph.WORLD);
      this.shareToggle(var1.deaths, MenuGlyph.PIN);
      this.shareToggle(var1.stats, MenuGlyph.TIMER);
      this.caption("gui.friends.caption.presence");
      this.shareToggle(var1.autoPvp, MenuGlyph.COMBAT);
      this.setting(var1.autoAfk);
      this.caption("gui.friends.caption.notify");
      this.shareToggle(var1.notifyMessages, MenuGlyph.CHAT);
      this.shareToggle(var1.notifyInvites, MenuGlyph.FRIENDS);
      this.caption("gui.friends.caption.accent");
      this.setting(var1.colorSetting);
      this.hint(t("gui.friends.share.hint"));
      this.buildCloudAside();
      this.asideHint(t("gui.friends.share.private"));
   }

   private void shareHero(Share var1) {
      int var2 = this.order++;
      this.custom(
         40.0F,
         (var3, var4, var5, var6, var7, var8, var9, var10) -> {
            float var11 = wave(this.listWave, var2);
            if (!(var11 <= 0.002F)) {
               var9 *= var11;
               var6 += (1.0F - var11) * 8.0F;
               float var12 = var6 + 8.0F;
               float var13 = var6 + var8 - 8.0F;
               float var14 = var7 + 3.0F;
               float var15 = 34.0F;
               plate(var3, var12, var14, var13 - var12, var15, 0.0F, 0.0F, var9, var10);
               bigGlyph(var3, MenuGlyph.SHIELD, var12 + 8.0F, var14 + (var15 - 12.0F) / 2.0F, var10.getPrimaryColor().getZenithColor().method7294(var9));
               Font var16 = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font var17 = Fonts.NEW_REGULAR.getFont(4.6F);
               float var18 = var12 + 8.0F + 12.0F + 8.0F;
               float var19 = var14 + (var15 - var16.height() - 3.0F - var17.height()) / 2.0F;
               var3.drawText(var16, t("gui.friends.share.title"), var18, var19, var10.getTextEnable().getZenithColor().method7294(var9));
               var3.drawText(
                  var17,
                  fit(var17, shareSummary(var1), var13 - 8.0F - var18),
                  var18,
                  var19 + var16.height() + 3.0F,
                  var10.getTextTertiary().getZenithColor().method7294(var9)
               );
            }
         }
      );
   }

   private static String shareSummary(Share var0) {
      ArrayList var1 = new ArrayList();
      if (var0.position.isEnabled()) {
         var1.add(t("cloud.share.position").toLowerCase(Locale.ROOT));
      }

      if (var0.server.isEnabled()) {
         var1.add(t("cloud.share.server").toLowerCase(Locale.ROOT));
      }

      if (var0.health.isEnabled()) {
         var1.add(t("cloud.share.health").toLowerCase(Locale.ROOT));
      }

      if (var0.inventory.isEnabled()) {
         var1.add(t("cloud.share.inventory").toLowerCase(Locale.ROOT));
      }

      return var1.isEmpty() ? t("gui.friends.share.summary.none") : t("gui.friends.share.summary").replace("{list}", String.join(", ", var1));
   }

   private void shareTiles(Share var1) {
      int var2 = this.order++;
      String var3 = "cosmetics";
      String[][] var4 = new String[][]{
         {"cloud.share.cosmeticsVisibility.everyone", "gui.friends.share.everyone.note"},
         {"cloud.share.cosmeticsVisibility.friends", "gui.friends.share.friends.note"},
         {"cloud.share.cosmeticsVisibility.nobody", "gui.friends.share.nobody.note"}
      };
      float var5 = 28.0F;
      this.custom(
         var5,
         (var6, var7, var8, var9, var10, var11, var12, var13) -> {
            float var14 = wave(this.listWave, var2);
            if (!(var14 <= 0.002F)) {
               var12 *= var14;
               var9 += (1.0F - var14) * 8.0F;
               float var15 = 4.0F;
               float var16 = var9 + 8.0F;
               float var17 = (var11 - 16.0F - var15 * 2.0F) / 3.0F;
               int var18 = -1;

               for (int var19 = 0; var19 < 3; var19++) {
                  float var20 = var16 + var19 * (var17 + var15);
                  float var21 = var10 + 2.0F;
                  float var22 = var5 - 4.0F;
                  boolean var23 = new BoundsData(var20, var21, var17, var22).contains(var7, var8);
                  if (var23) {
                     var18 = var19;
                     HoverInfo.request(t("cloud.share.cosmeticsVisibility"), t(var4[var19][1]));
                  }

                  float var24 = this.pick(var3 + var19, var1.cosmeticsVisibility.getIndex() == var19);
                  plate(var6, var20, var21, var17, var22, this.hover(var3 + var19, var23), var24, var12, var13);
                  Font var25 = Fonts.NEW_MEDIUM.getFont(5.2F);
                  Font var26 = Fonts.NEW_REGULAR.getFont(4.2F);
                  String var27 = t(var4[var19][0]);
                  var27 = var27.isEmpty() ? var27 : var27.substring(0, 1).toUpperCase(Locale.ROOT) + var27.substring(1);
                  var6.drawText(
                     var25,
                     fit(var25, var27, var17 - 10.0F),
                     var20 + 5.0F,
                     var21 + 4.0F,
                     var13.getTextSecondary().getZenithColor().method7295(var13.getTextEnable().getZenithColor(), var24).method7294(var12)
                  );
                  var6.drawText(
                     var26,
                     fit(var26, t(var4[var19][1]), var17 - 10.0F),
                     var20 + 5.0F,
                     var21 + 4.0F + var25.height() + 2.0F,
                     var13.getTextTertiary().getZenithColor().method7294(var12)
                  );
               }

               this.hoveredButton.put(var3, var18);
            }
         },
         () -> {
            int var3x = this.hoveredButton.getOrDefault(var3, -1);
            if (var3x >= 0) {
               var1.cosmeticsVisibility.getValues().get(var3x).method2103();
               this.invalidate();
            }
         },
         null
      );
   }

   private void shareToggle(BooleanSetting var1, MenuGlyph var2) {
      int var3 = this.order++;
      String var4 = "share:" + var1.getKey();
      this.custom(
         28.0F,
         (var5, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = wave(this.listWave, var3);
            if (!(var13 <= 0.002F)) {
               var11 *= var13;
               var8 += (1.0F - var13) * 8.0F;
               float var14 = var8 + 8.0F;
               float var15 = var10 - 16.0F;
               float var16 = var9 + 1.5F;
               float var17 = 25.0F;
               boolean var18 = new BoundsData(var14, var16, var15, var17).contains(var6, var7);
               float var19 = this.pick(var4, var1.isEnabled());
               plate(var5, var14, var16, var15, var17, this.hover(var4, var18), 0.0F, var11, var12);
               if (var18) {
                  HoverInfo.request(t(var1.getKey()), t(var1.getKey() + ".desc"));
               }

               float var20 = 14.0F;
               float var21 = var14 + 7.0F;
               float var22 = var16 + (var17 - var20) / 2.0F;
               var5.drawRoundedRect(
                  var21,
                  var22,
                  var20,
                  var20,
                  ProxyDotRadiusData.method8069(4.0F),
                  var12.getTextTertiary()
                     .getZenithColor()
                     .method7294(0.18F)
                     .method7295(var12.getPrimaryColor().getZenithColor().method7294(0.25F), var19)
                     .method7294(var11)
               );
               var2.draw(
                  var5,
                  var21 + (var20 - 6.0F) / 2.0F,
                  var22 + (var20 - 6.0F) / 2.0F,
                  var12.getTextTertiary().getZenithColor().method7295(var12.getPrimaryColor().getZenithColor(), var19).method7294(var11)
               );
               Font var23 = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font var24 = Fonts.NEW_REGULAR.getFont(4.2F);
               float var25 = var21 + var20 + 6.0F;
               float var26 = 18.0F;
               float var27 = var14 + var15 - 8.0F - var26 - 6.0F - var25;
               float var28 = var16 + (var17 - var23.height() - 2.0F - var24.height()) / 2.0F;
               var5.drawText(var23, fit(var23, t(var1.getKey()), var27), var25, var28, var12.getTextEnable().getZenithColor().method7294(var11));
               var5.drawText(
                  var24,
                  fit(var24, t(var1.getKey() + ".desc"), var27),
                  var25,
                  var28 + var23.height() + 2.0F,
                  var12.getTextTertiary().getZenithColor().method7294(var11)
               );
               this.drawToggle(var5, var14 + var15 - 8.0F - var26, var16 + (var17 - 9.0F) / 2.0F, var26, 9.0F, var19, var11, var12);
            }
         },
         () -> {
            var1.setEnabled(!var1.isEnabled());
            this.invalidate();
         },
         null
      );
   }

   private void buildFriendAside() {
      if (this.party.hasSelection()) {
         this.flashRow();
         this.party.buildAside(cloud());
      } else if (this.selected == null) {
         this.flashRow();
         this.buildMeAside();
         this.buildCloudAside();
         this.asideHint(t("gui.friends.aside.pick"));
      } else {
         if (this.selectedLocal) {
            this.buildLocalAside(this.selected);
         } else {
            this.buildCloudFriendAside(this.selected);
         }
      }
   }

   private void buildMeAside() {
      this.asideCaption("gui.friends.me.caption");
      int var1 = this.asideOrder++;
      String var2 = "me";
      this.asideCustom(
         48.0F,
         (var3x, var4, var5, var6, var7, var8, var9, var10) -> {
            float var11 = wave(this.asideWave, var1);
            if (!(var11 <= 0.002F)) {
               var9 *= var11;
               var6 += (1.0F - var11) * 6.0F;
               String var12 = accountName();
               String var13 = mc.getSession() == null ? "" : mc.getSession().getUsername();
               long var14 = accountUid();
               float var16 = var6 + 8.0F;
               this.avatar(var3x, var12.isEmpty() ? var13 : var12, var14, var16, var7, 26.0F, var9, var10);
               Font var17 = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font var18 = Fonts.NEW_REGULAR.getFont(4.6F);
               float var19 = var16 + 26.0F + 6.0F;
               float var20 = var6 + var8 - 8.0F - var19;
               var3x.drawText(
                  var17, fit(var17, var12.isEmpty() ? var13 : var12, var20), var19, var7 + 4.0F, var10.getTextEnable().getZenithColor().method7294(var9)
               );
               var3x.drawText(
                  var18,
                  fit(var18, t("gui.friends.me.mc") + ": " + blankToDash(var13), var20),
                  var19,
                  var7 + 4.0F + var17.height() + 3.0F,
                  var10.getTextTertiary().getZenithColor().method7294(var9)
               );
               Font var21 = Fonts.NEW_MEDIUM.getFont(4.8F);
               boolean var22 = this.flashing() && this.flashText.equals(t("gui.friends.copied"));
               String var23 = t(var22 ? "gui.friends.copied" : "gui.friends.me.copy");
               BoundsData var24 = new BoundsData(var16, var7 + 26.0F + 5.0F, var8 - 16.0F, 14.0F);
               boolean var25 = var24.contains(var4, var5);
               this.hoveredButton.put(var2, var25 ? 0 : -1);
               if (var25) {
                  HoverInfo.request(t("gui.friends.me.copy"), t("gui.friends.me.hint"));
               }

               this.pillButton(var3x, var2, var24, var23, true, var25, var9, var10);
               if (var22) {
                  MenuGlyph.CHECK
                     .draw(
                        var3x,
                        var24.getFloatValue() + (var24.width() - var21.width(var23)) / 2.0F - 9.0F,
                        var24.getFloatValue2() + (var24.height() - 6.0F) / 2.0F,
                        var10.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), this.hover(var2, var25)).method7294(var9)
                     );
               }
            }
         },
         () -> {
            if (this.hoveredButton.getOrDefault("me", -1) == 0) {
               String var1x = accountName();
               this.copy(var1x.isEmpty() ? (mc.getSession() == null ? "" : mc.getSession().getUsername()) : var1x);
            }
         },
         null
      );
      Share var3 = Zenith.getInstance().getShare();
      this.asideCaption("gui.friends.caption.presence");
      this.asideInfo("gui.friends.presence.now", () -> {
         PartyManager var0 = PartyManager.method17();
         return var0 == null ? "-" : PartyManager.method50(var0.method31());
      }, "gui.friends.presence.now.desc");
      this.asideSetting(var3.status);
      this.asideSetting(var3.text);
   }

   private void buildCloudAside() {
      this.asideCaption("gui.friends.caption.cloud");
      this.asideInfo("gui.friends.link", () -> {
         CloudConnectionState var0 = state();
         return var0 == null ? t("gui.cloud.state.missing") : var0.getType().label();
      }, "gui.friends.link.desc");
      this.asideInfo("gui.friends.onlineCount", () -> {
         CloudConnectionState var0 = state();
         return var0 != null && var0.method8035() ? var0.method8045() + " / " + var0.getIntValue() : "-";
      }, "gui.friends.onlineCount.desc");
      CloudConnectionState var1 = state();
      if (var1 == null || !var1.method8035()) {
         this.asideInfo("gui.friends.reason", () -> {
            CloudConnectionState var0 = state();
            if (var0 == null) {
               return t("gui.friends.reason.down");
            } else if (var0.method8035()) {
               return "-";
            } else {
               String var1x = var0.note();
               if (var1x == null || var1x.isBlank()) {
                  var1x = var0.getLastError();
               }

               return var1x != null && !var1x.isBlank() ? var1x : var0.getType().label();
            }
         }, "gui.friends.reason.desc");
         this.asideAction("gui.friends.reconnect", MenuGlyph.ACTION, () -> t("gui.friends.reconnect.value"), () -> {
            Cloud var0 = cloud();
            if (var0 != null) {
               var0.connect();
            }
         }, "gui.friends.reconnect.desc");
      }
   }

   private void buildCloudFriendAside(String var1) {
      PartyPlayerInfo var2 = peer(var1);
      Cloud var3 = cloud();
      this.profileHead(var1, whereLine(var1), false);
      this.actionTiles(var1, var2);
      this.flashRow();
      if (var2 != null && var3 != null) {
         this.party.leaderActions(var3, var2.uid());
      }

      this.asideCaption("gui.friends.caption.now");
      this.asideInfo("gui.friends.status", () -> statusValue(var1, var2), "gui.friends.status.desc");
      if (var2 != null) {
         this.asideInfo("gui.friends.playingOn", () -> {
            Cloud var1x = cloud();
            String var2x = var1x == null ? "" : var1x.getPartyMember().method1141(var2.uid()).mode();
            return blankToDash(ServerNameUtils.method5643(var2.server(), var2x));
         }, "gui.friends.playingOn.desc");
         this.asideInfo("gui.friends.onlineFor", () -> onlineFor(var1), "gui.friends.onlineFor.desc");
         if (var3 != null && var2.method3291(var3.method7911(), var3.method7913())) {
            this.asideInfo("gui.friends.distance", () -> distanceTo(var1), "gui.friends.distance.desc");
         }
      } else {
         this.asideInfo("gui.friends.lastSeen", () -> lastSeen(var1), "gui.friends.lastSeen.desc");
      }

      String var4 = rememberedMcName(var1);
      if (!var4.isEmpty()) {
         this.asideInfo("gui.friends.mcName", () -> var4, "gui.friends.mcName.desc");
      }

      if (var2 != null) {
         this.asideCaption("gui.friends.caption.state");
         this.bar(var1, t("gui.friends.health"), () -> var2.getFloatValue3(), () -> Math.max(1.0F, var2.getFloatValue4()), var0 -> DANGER);
         this.bar(var1, t("gui.friends.food"), () -> (float)var2.getIntValue(), () -> 20.0F, var0 -> new ZenithColor(-680437));
         List<ItemStack> var5 = equipment(var2);
         if (!var5.isEmpty()) {
            this.asideCaption("gui.friends.caption.gear");
            this.asideCustom(
               16.0F + GuiStyle.PADDING.intValue() + 2.0F,
               (var1x, var2x, var3x, var4x, var5x, var6, var7, var8) -> {
                  float var9 = var4x + 8.0F;

                  for (Object var11_o : (Iterable<?>)(Object)(var5)) {
                     ItemStack var11 = (ItemStack)var11_o;
                     var1x.drawRoundedRect(
                        var9 - 1.0F,
                        var5x - 1.0F,
                        18.0F,
                        18.0F,
                        ProxyDotRadiusData.method8069(3.0F),
                        var8.getDisableActiveBg().getZenithColor().method7294(var7 * 0.6F)
                     );
                     var1x.getMatrices().pushMatrix();
                     var1x.getMatrices().translate(var9, var5x);
                     var1x.drawItem(var11, 0, 0);
                     var1x.drawItemBar(var11, 0, 0);
                     var1x.getMatrices().popMatrix();
                     var9 += 20.0F;
                  }
               }
            );
         }

         if (!var2.getItems().isEmpty()) {
            this.asideInfo("gui.friends.cosmetics", () -> var2.getItems().size() + " " + t("gui.friends.cosmetics.items"), "gui.friends.cosmetics.desc");
         }
      }

      this.voiceRows(var1, var2);
      this.friendActions(var1);
   }

   private void profileHead(String var1, String var2, boolean var3) {
      int var4 = this.asideOrder++;
      String var5 = (var3 ? "local:" : "cloud:") + var1;
      this.asideCustom(
         26.0F + GuiStyle.PADDING.intValue() + 2.0F,
         (var6, var7, var8, var9, var10, var11, var12, var13) -> {
            float var14 = wave(this.asideWave, var4);
            if (!(var14 <= 0.002F)) {
               var12 *= var14;
               var9 += (1.0F - var14) * 6.0F;
               float var15 = var9 + 8.0F;
               this.avatar(var6, var1, var15, var10, 26.0F, var12, var13);
               this.drawStatusDot(var6, "head:" + var5, var1, var3, var15 + 26.0F - 4.0F - 0.5F, var10 + 26.0F - 4.0F - 0.5F, var12, var13);
               float var16 = var15 + 26.0F + 6.0F;
               Font var17 = Fonts.NEW_MEDIUM.getFont(6.4F);
               Font var18 = Fonts.NEW_REGULAR.getFont(4.6F);
               float var19 = var9 + var11 - 8.0F - var16;
               Prefs var20 = prefs();
               boolean var21 = !var3 && var20 != null && var20.method3319(var1);
               float var22 = var16;
               float var23 = var10 + 4.0F;
               if (var21) {
                  diamond(var6, var16 + 2.0F, var23 + var17.height() / 2.0F, 3.5F, FAVORITE.method7294(var12));
                  var22 = var16 + 7.0F;
               }

               var6.drawText(
                  var17,
                  fit(var17, var3 ? var1 : displayName(var1), var19 - (var22 - var16)),
                  var22,
                  var23,
                  var13.getTextEnable().getZenithColor().method7294(var12)
               );
               var6.drawText(var18, fit(var18, var2, var19), var16, var23 + var17.height() + 3.0F, var13.getTextTertiary().getZenithColor().method7294(var12));
            }
         }
      );
   }

   private void actionTiles(String var1, PartyPlayerInfo var2) {
      Cloud var3 = cloud();
      PartyManager var4 = PartyManager.method17();
      ArrayList var5 = new ArrayList();
      if (!isPendingName(var1)) {
         var5.add(
            new FriendsSectionPanel.Tile("chat", MenuGlyph.CHAT, "gui.friends.quick.chat", "gui.friends.quick.chat.desc", false, () -> this.openChat(var1))
         );
      }

      if (var2 != null && var4 != null && var4.method42(var2.uid())) {
         var5.add(new FriendsSectionPanel.Tile("join", MenuGlyph.WORLD, "gui.friends.quick.join", "gui.friends.quick.join.desc", false, () -> {
            this.flashMessage(t("gui.friends.join.going").replace("{n}", displayName(var1)));
            var4.method43(var2.uid());
         }));
      }

      if (var2 != null && var3 != null && var3.method7860()) {
         PartyMember var6 = var3.getPartyMember();
         boolean var7 = var6.method1137(var2.uid());
         boolean var8 = var6.getData6() != null && var6.getData6().getItems().size() >= 6;
         if (var7 || !var8) {
            var5.add(
               new FriendsSectionPanel.Tile(
                  "party", MenuGlyph.FRIENDS, var7 ? "gui.friends.quick.inParty" : "gui.friends.quick.party", "gui.friends.quick.party.desc", var7, () -> {
                     if (!var6.method1137(var2.uid())) {
                        var6.method1153(var2.uid());
                        this.flashMessage(t("gui.friends.party.invited").replace("{n}", displayName(var1)));
                     }
                  }
               )
            );
         }
      }

      if (var2 != null && var2.method3292()) {
         var5.add(
            new FriendsSectionPanel.Tile("mark", MenuGlyph.GPS, "gui.friends.quick.mark", "gui.friends.quick.mark.desc", false, () -> this.markFriend(var1))
         );
      }

      if (!var5.isEmpty()) {
         int var9 = this.asideOrder++;
         String var10 = "tiles:" + var1;
         this.asideCustom(
            34.0F,
            (var4x, var5x, var6x, var7x, var8x, var9x, var10x, var11) -> {
               float var12 = wave(this.asideWave, var9);
               if (!(var12 <= 0.002F)) {
                  var10x *= var12;
                  var7x += (1.0F - var12) * 6.0F;
                  float var13 = 3.0F;
                  float var14 = var7x + 8.0F;
                  float var15 = (var9x - 16.0F - var13 * (var5.size() - 1)) / var5.size();
                  int var16 = -1;

                  for (int var17 = 0; var17 < var5.size(); var17++) {
                     FriendsSectionPanel.Tile var18 = (FriendsSectionPanel.Tile)var5.get(var17);
                     BoundsData var19 = new BoundsData(var14 + var17 * (var15 + var13), var8x, var15, 30.0F);
                     boolean var20 = var19.contains(var5x, var6x);
                     if (var20) {
                        var16 = var17;
                        HoverInfo.request(t(var18.label()), t(var18.note()));
                     }

                     float var21 = this.hover(var10 + var18.key(), var20);
                     float var22 = this.flash(var10 + var18.key());
                     var4x.drawRoundedRect(
                        var19.getFloatValue(),
                        var19.getFloatValue2(),
                        var19.width(),
                        var19.height(),
                        ProxyDotRadiusData.method8069(4.0F),
                        var11.getDisableActiveBg().getZenithColor().method7294(var10x * (0.5F + 0.3F * var21 + 0.3F * var22))
                     );
                     if (var18.active()) {
                        var4x.drawRoundedRect(
                           var19.getFloatValue(),
                           var19.getFloatValue2(),
                           var19.width(),
                           var19.height(),
                           ProxyDotRadiusData.method8069(4.0F),
                           var11.getPrimaryColor().getZenithColor().method7294(var10x * 0.12F)
                        );
                     }

                     ZenithColor var23 = var11.getTextSecondary()
                        .getZenithColor()
                        .method7295(var11.getPrimaryColor().getZenithColor(), Math.max(var21, var18.active() ? 1.0F : 0.0F))
                        .method7294(var10x);
                     float var24 = var21 * -1.0F;
                     var18.icon().draw(var4x, var19.getFloatValue() + (var19.width() - 6.0F) / 2.0F, var19.getFloatValue2() + 6.0F + var24, var23);
                     Font var25 = Fonts.NEW_MEDIUM.getFont(4.4F);
                     String var26 = fit(var25, t(var18.label()), var19.width() - 4.0F);
                     var4x.drawText(
                        var25,
                        var26,
                        var19.getFloatValue() + (var19.width() - var25.width(var26)) / 2.0F,
                        var19.getFloatValue2() + var19.height() - var25.height() - 5.0F,
                        var11.getTextSecondary().getZenithColor().method7295(var11.getTextEnable().getZenithColor(), var21).method7294(var10x)
                     );
                  }

                  this.hoveredButton.put(var10, var16);
               }
            },
            () -> {
               int var3x = this.hoveredButton.getOrDefault(var10, -1);
               if (var3x >= 0 && var3x < var5.size()) {
                  this.press(var10 + ((FriendsSectionPanel.Tile)var5.get(var3x)).key());
                  ((FriendsSectionPanel.Tile)var5.get(var3x)).action().run();
                  this.invalidate();
               }
            },
            null
         );
      }
   }

   private static String statusValue(String var0, PartyPlayerInfo var1) {
      if (var1 == null) {
         return t(isPendingName(var0) ? "gui.friends.status.pending" : "gui.friends.status.offline");
      } else {
         String var2 = presenceNote(var1.uid());
         return var2.isEmpty() ? t("gui.friends.status.online") : var2;
      }
   }

   private void flashRow() {
      if (this.flashing()) {
         String var1 = this.flashText;
         Font var2 = Fonts.NEW_REGULAR.getFont(4.6F);
         this.asideCustom(
            var2.height() + 6.0F,
            (var3, var4, var5, var6, var7, var8, var9, var10) -> {
               long var11 = System.currentTimeMillis() - this.flashAt;
               float var13 = var11 < 180L ? (float)var11 / 180.0F : (var11 > 2200L ? Math.max(0.0F, (float)(2600L - var11) / 400.0F) : 1.0F);
               MenuGlyph.CHECK
                  .draw(var3, var6 + 8.0F, var7 + 2.0F + (var2.height() - 6.0F) / 2.0F, var10.getPrimaryColor().getZenithColor().method7294(var9 * var13));
               var3.drawText(
                  var2,
                  fit(var2, var1, var8 - 16.0F - 9.0F),
                  var6 + 8.0F + 9.0F,
                  var7 + 2.0F,
                  var10.getTextSecondary().getZenithColor().method7294(var9 * var13)
               );
            }
         );
      }
   }

   private void markFriend(String var1) {
      PartyPlayerInfo var2 = peer(var1);
      if (var2 != null && var2.method3292()) {
         String var3 = CloudWaypointsModule.INSTANCE
            .method6402(var2.getDoubleValue(), var2.getDoubleValue2() + 1.0, var2.getDoubleValue3(), displayName(var1), "", var2.server(), var2.dimension());
         this.flashMessage(var3.isEmpty() ? t("gui.friends.mark.done").replace("{n}", displayName(var1)) : t(var3));
      } else {
         this.flashMessage(t("gui.friends.mark.noPosition").replace("{n}", displayName(var1)));
      }
   }

   private void voiceRows(String var1, PartyPlayerInfo var2) {
      Prefs var3 = prefs();
      if (var3 != null) {
         this.asideCaption("gui.friends.caption.voice");
         if (var2 != null) {
            this.asideInfo(
               "gui.friends.voiceStatus",
               () -> t(var2.method3289() ? "gui.friends.voiceStatus.on" : "gui.friends.voiceStatus.off"),
               "gui.friends.voiceStatus.desc"
            );
         }

         this.asideAction(
            "gui.friends.voiceMute", MenuGlyph.MIC, () -> t(var3.method3321(var1) ? "gui.friends.voiceMute.on" : "gui.friends.voiceMute.off"), () -> {
               var3.method3322(var1, !var3.method3321(var1));
               this.invalidate();
            }, "gui.friends.voiceMute.desc"
         );
         Cloud var4 = cloud();
         if (var2 != null && var2.method3289() && var4 != null && var4.method7860()) {
            this.asideAction(
               "gui.friends.quick.whisper",
               MenuGlyph.SOUND,
               () -> t(var4.getPartyMember().getLongValue() == var2.uid() ? "gui.value.enabled" : "gui.value.disabled"),
               () -> {
                  boolean var3x = var4.getPartyMember().getLongValue() == var2.uid();
                  var4.getPartyMember().method1176(var3x ? 0L : var2.uid());
                  this.invalidate();
               },
               "gui.friends.quick.whisper.desc"
            );
         }

         this.voiceVolume = new SliderSetting(
            "gui.friends.voiceVolume", (float)var3.method3323(var1), 0.0F, 200.0F, 5.0F, "gui.friends.voiceVolume.desc", " %", null
         );
         this.voiceVolumeFor = var1;
         this.asideSetting(this.voiceVolume);
      }
   }

   private void friendActions(String var1) {
      Prefs var2 = prefs();
      if (var2 != null) {
         CloudConnectionState var3 = state();
         boolean var4 = var3 != null && var3.method8061(var1);
         this.asideCaption("gui.friends.caption.actions");
         PartyManager var5 = PartyManager.method17();
         PartyPlayerInfo var6 = peer(var1);
         if (var5 != null && var6 != null && var5.method47()) {
            this.asideAction(
               "gui.friends.quick.invite",
               MenuGlyph.ACTION,
               () -> t("gui.friends.inviteHere.value"),
               () -> var5.method48(var6.uid()),
               "gui.friends.quick.invite.desc"
            );
         }

         this.asideAction(
            "gui.friends.favorite", MenuGlyph.PIN, () -> t(var2.method3319(var1) ? "gui.friends.favorite.on" : "gui.friends.favorite.off"), () -> {
               var2.method3320(var1, !var2.method3319(var1));
               this.invalidate();
            }, "gui.friends.favorite.desc"
         );
         this.asideAction("gui.friends.notify", MenuGlyph.MODE, () -> t(var2.method3315(var1) ? "gui.value.disabled" : "gui.value.enabled"), () -> {
            var2.method3317(var1, !var2.method3315(var1));
            this.invalidate();
         }, "gui.friends.notify.desc");
         String var7 = rememberedMcName(var1);
         this.asideAction(
            "gui.friends.copyNick",
            MenuGlyph.COPY,
            () -> t("gui.friends.copyNick.value"),
            () -> this.copy(var7.isEmpty() ? displayName(var1) : var7),
            "gui.friends.copyNick.desc"
         );
         this.asideAction("gui.friends.block", MenuGlyph.CLOSE, () -> t(var2.method3316(var1) ? "gui.friends.block.on" : "gui.value.no"), () -> {
            var2.method3318(var1, !var2.method3316(var1));
            this.invalidate();
         }, "gui.friends.block.desc");
         this.dangerButton(
            var4 ? "gui.friends.cancelRequest" : "gui.friends.remove", var4 ? "gui.friends.cancelRequest.desc" : "gui.friends.remove.desc", () -> {
               Cloud var2x = cloud();
               if (var2x != null) {
                  var2x.method7907(var1);
               }

               this.selected = null;
               this.invalidate();
            }
         );
      }
   }

   private void dangerButton(String var1, String var2, Runnable var3) {
      String var4 = "danger:" + var1;
      this.asideCustom(
         21.0F,
         (var4x, var5, var6, var7, var8, var9, var10, var11) -> {
            BoundsData var12 = new BoundsData(var7 + 8.0F, var8 + 3.0F, var9 - 16.0F, 15.0F);
            boolean var13 = var12.contains(var5, var6);
            this.hoveredButton.put(var4, var13 ? 0 : -1);
            if (var13) {
               HoverInfo.request(t(var1), t(var2));
            }

            float var14 = this.hover(var4, var13);
            var4x.drawRoundedRect(
               var12.getFloatValue(),
               var12.getFloatValue2(),
               var12.width(),
               var12.height(),
               ProxyDotRadiusData.method8069(var12.height() / 2.0F),
               DANGER.method7292(0.1F + var14 * 0.18F).method7294(var10)
            );
            Font var15 = Fonts.NEW_MEDIUM.getFont(4.8F);
            String var16 = t(var1);
            var4x.drawText(
               var15,
               var16,
               var12.getFloatValue() + (var12.width() - var15.width(var16)) / 2.0F,
               var12.getFloatValue2() + (var12.height() - var15.height()) / 2.0F,
               DANGER.method7295(new ZenithColor(255, 255, 255), var14 * 0.4F).method7294(var10)
            );
         },
         () -> {
            if (this.hoveredButton.getOrDefault(var4, -1) == 0) {
               var3.run();
            }
         },
         null
      );
   }

   private void openChat(String var1) {
      if (this.openChatWith != null) {
         if (!this.openChatWith.test(var1)) {
            this.flashMessage(t("gui.friends.notice.noChat").replace("{n}", displayName(var1)));
            if (this.selected == null) {
               this.setNotice(t("gui.friends.notice.noChat").replace("{n}", displayName(var1)), "", false);
            }
         }
      }
   }

   private static Prefs prefs() {
      Cloud var0 = cloud();
      return var0 == null ? null : var0.prefs();
   }

   private static String rememberedMcName(String var0) {
      PartyPlayerInfo var1 = peer(var0);
      if (var1 != null && !var1.getText2().isBlank()) {
         return var1.getText2();
      } else {
         Prefs var2 = prefs();
         return var2 == null ? "" : var2.method3313(var0);
      }
   }

   private static String onlineFor(String var0) {
      Prefs var1 = prefs();
      long var2 = var1 == null ? 0L : var1.method3309(var0);
      return var2 <= 0L ? t("gui.friends.justJoined") : duration(var2);
   }

   private static String lastSeen(String var0) {
      Prefs var1 = prefs();
      long var2 = var1 == null ? 0L : var1.method3310(var0);
      return var2 <= 0L ? t("gui.friends.neverSeen") : t("gui.friends.ago").replace("{t}", duration(System.currentTimeMillis() - var2));
   }

   private static String duration(long var0) {
      long var2 = var0 / 60000L;
      if (var2 < 1L) {
         return t("gui.time.lessMinute");
      } else if (var2 < 60L) {
         return var2 + " " + t("gui.time.min");
      } else {
         long var4 = var2 / 60L;
         if (var4 < 24L) {
            long var8 = var2 % 60L;
            return var8 == 0L ? var4 + " " + t("gui.time.hour") : var4 + " " + t("gui.time.hour") + " " + var8 + " " + t("gui.time.min");
         } else {
            long var6 = var4 / 24L;
            return var6 + " " + t("gui.time.day");
         }
      }
   }

   private void bar(String var1, String var2, Supplier<Float> var3, Supplier<Float> var4, Function<ZenithStyle, ZenithColor> var5) {
      Font var6 = Fonts.NEW_REGULAR.getFont(4.6F);
      this.asideCustom(
         var6.height() + 3.0F + 7.0F,
         (var7, var8, var9, var10, var11, var12, var13, var14) -> {
            float var15 = (Float)var3.get();
            if (!Float.isFinite(var15)) {
               var15 = 0.0F;
            }

            float var16 = (Float)var4.get();
            var16 = Float.isFinite(var16) ? Math.max(0.001F, var16) : 20.0F;
            float var17 = this.barFill
               .computeIfAbsent("bar:" + var1 + ":" + var2, var0 -> new Animation(260L, 0.0F, Easing.INSTANCE12))
               .method2808(Math.max(0.0F, Math.min(1.0F, var15 / var16)));
            String var18 = String.valueOf(Math.round(var15));
            var7.drawText(var6, var2, var10 + 8.0F, var11, var14.getTextTertiary().getZenithColor().method7294(var13));
            var7.drawText(var6, var18, var10 + var12 - 8.0F - var6.width(var18), var11, var14.getTextEnable().getZenithColor().method7294(var13));
            float var19 = var11 + var6.height() + 3.0F;
            float var20 = var12 - 16.0F;
            var7.drawRoundedRect(
               var10 + 8.0F, var19, var20, 3.0F, ProxyDotRadiusData.method8069(1.5F), var14.getFieldBorder().getZenithColor().method7294(var13)
            );
            if (var17 > 0.0F) {
               var7.drawRoundedRect(
                  var10 + 8.0F, var19, var20 * var17, 3.0F, ProxyDotRadiusData.method8069(1.5F), ((ZenithColor)var5.apply(var14)).method7294(var13)
               );
            }
         }
      );
   }

   private void buildLocalAside(String var1) {
      this.profileHead(var1, t(this.isNearby(var1) ? "gui.friends.nearbyWorld" : "gui.friends.localFriend"), true);
      this.flashRow();
      this.asideCaption("gui.friends.caption.now");
      this.asideInfo("gui.friends.storedIn", () -> t("gui.friends.storedIn.value"), "gui.friends.storedIn.desc");
      this.asideInfo("gui.friends.near", () -> t(this.isNearby(var1) ? "gui.value.yes" : "gui.value.no"), "gui.friends.near.desc");
      this.asideCaption("gui.friends.caption.actions");
      this.asideAction("gui.friends.invite", MenuGlyph.CHECK, () -> t("gui.friends.invite.value"), () -> {
         Cloud var2 = cloud();
         if (var2 != null) {
            var2.method7904(0, var1);
         }

         this.flashMessage(t("gui.friends.notice.queued").replace("{n}", var1));
      }, "gui.friends.invite.desc");
      this.asideAction("gui.friends.copyNick", MenuGlyph.COPY, () -> t("gui.friends.copyNick.value"), () -> this.copy(var1), "gui.friends.copyNick.desc");
      this.dangerButton("gui.friends.remove", "gui.friends.remove.local.desc", () -> {
         Zenith.getInstance().getFriends().method7629(var1);
         Zenith.getInstance().getFriends().save();
         this.selected = null;
         this.invalidate();
      });
   }

   private static void plate(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      var0.drawRoundedRect(
         var1,
         var2,
         var3,
         var4,
         ProxyDotRadiusData.method8069(4.0F),
         var8.getDisableActiveBg().getZenithColor().method7294(var7 * (0.45F + 0.25F * var5 + 0.3F * var6))
      );
      if (var6 > 0.002F) {
         float var9 = (var3 - 10.0F) * var6;
         var0.drawRoundedRect(
            var1 + (var3 - var9) / 2.0F,
            var2 + var4 - 2.0F,
            var9,
            1.5F,
            ProxyDotRadiusData.method8069(0.75F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * var6)
         );
      }
   }

   private static void bigGlyph(MenuDrawContext var0, MenuGlyph var1, float var2, float var3, ZenithColor var4) {
      var0.pushMatrix();
      var0.getMatrices().translate(var2, var3);
      var0.getMatrices().scale(2.0F, 2.0F);
      var1.draw(var0, 0.0F, 0.0F, var4);
      var0.popMatrix();
   }

   private static void plusGlyph(MenuDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      var0.pushMatrix();
      var0.getMatrices().translate(var1, var2);
      var0.getMatrices().rotate((float)((Math.PI / 4) * var3));
      var0.drawRect(-2.5F, -0.5F, 5.0F, 1.0F, var4);
      var0.drawRect(-0.5F, -2.5F, 1.0F, 5.0F, var4);
      var0.popMatrix();
   }

   private static void diamond(MenuDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      var0.pushMatrix();
      var0.getMatrices().translate(var1, var2);
      var0.getMatrices().rotate((float)Math.toRadians(45.0));
      var0.drawRoundedRect(-var3 / 2.0F, -var3 / 2.0F, var3, var3, ProxyDotRadiusData.method8069(0.6F), var4);
      var0.popMatrix();
   }

   private void drawToggle(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      ZenithColor var9 = var8.getTextTertiary().getZenithColor().method7294(0.35F).method7295(var8.getPrimaryColor().getZenithColor(), var6);
      var1.drawRoundedRect(var2, var3, var4, var5, ProxyDotRadiusData.method8069(var5 / 2.0F), var9.method7294(var7));
      float var10 = var5 - 3.0F;
      float var11 = var2 + 1.5F + (var4 - var10 - 3.0F) * var6;
      var1.drawRoundedRect(var11, var3 + 1.5F, var10, var10, ProxyDotRadiusData.method8069(var10 / 2.0F), new ZenithColor(255, 255, 255).method7294(var7));
   }

   private void pillButton(MenuDrawContext var1, String var2, BoundsData var3, String var4, boolean var5, boolean var6, float var7, ZenithStyle var8) {
      float var9 = Math.min(1.0F, this.hover(var2 + ":btn", var6) + this.flash(var2) * 0.8F);
      ProxyDotRadiusData var10 = ProxyDotRadiusData.method8069(var3.height() / 2.0F);
      ZenithColor var11 = var8.getPrimaryColor().getZenithColor();
      if (var5) {
         var1.drawRoundedRect(
            var3.getFloatValue(), var3.getFloatValue2(), var3.width(), var3.height(), var10, var11.method7292(0.16F + var9 * 0.74F).method7294(var7)
         );
      } else {
         var1.drawRoundedRect(
            var3.getFloatValue(),
            var3.getFloatValue2(),
            var3.width(),
            var3.height(),
            var10,
            var8.getDisableActiveBg().getZenithColor().method7294(var7 * (0.8F + var9 * 0.6F))
         );
      }

      Font var12 = Fonts.NEW_MEDIUM.getFont(4.8F);
      ZenithColor var13 = var5
         ? var11.method7295(new ZenithColor(255, 255, 255), var9).method7294(var7)
         : var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var9).method7294(var7);
      var1.drawText(
         var12, var4, var3.getFloatValue() + (var3.width() - var12.width(var4)) / 2.0F, var3.getFloatValue2() + (var3.height() - var12.height()) / 2.0F, var13
      );
   }

   public void asideHint(String var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.6F);
      List<String> var3 = wrapText(var1, var2, this.asideWidth() - 16.0F, 4);
      float var4 = var2.height() + 2.0F;
      int var5 = this.asideOrder++;
      this.asideCustom(var3.size() * var4 + GuiStyle.PADDING.intValue() * 2.0F, (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
         float var13 = wave(this.asideWave, var5);
         float var14 = var9 + GuiStyle.PADDING.intValue();

         for (Object var16_o : (Iterable<?>)(Object)(var3)) {
            String var16 = (String)var16_o;
            var5x.drawText(var2, var16, var8 + 8.0F + (1.0F - var13) * 6.0F, var14, var12.getTextTertiary().getZenithColor().method7294(var11 * var13));
            var14 += var4;
         }
      });
   }

   private void hint(String var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> var3 = wrapText(var1, var2, this.mainWidth() - 16.0F, 4);
      float var4 = var2.height() + 2.0F;
      int var5 = this.order++;
      this.custom(var3.size() * var4 + GuiStyle.PADDING.intValue(), (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
         float var13 = wave(this.listWave, var5);
         float var14 = var9 + 2.0F;

         for (Object var16_o : (Iterable<?>)(Object)(var3)) {
            String var16 = (String)var16_o;
            var5x.drawText(var2, var16, var8 + 8.0F + (1.0F - var13) * 8.0F, var14, var12.getTextTertiary().getZenithColor().method7294(var11 * var13));
            var14 += var4;
         }
      });
   }

   private void noticeBox(String var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> var3 = wrapText(var1, var2, this.mainWidth() - 16.0F - 14.0F, 4);
      float var4 = var2.height() + 1.5F;
      float var5 = var3.size() * var4 + 10.0F;
      if (!var1.equals(this.noticeAnimatedFor)) {
         this.noticeAnimatedFor = var1;
         restart(this.noticeAppear);
      }

      this.custom(
         var5 + GuiStyle.PADDING.intValue(),
         (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = this.noticeAppear.method2808(1.0F);
            float var14 = var8 + 8.0F;
            float var15 = var10 - 16.0F;
            var5x.drawRoundedRect(
               var14, var9, var15, var5, ProxyDotRadiusData.method8069(4.0F), var12.getDisableActiveBg().getZenithColor().method7294(var11 * 0.7F * var13)
            );
            float var16 = (var5 - 6.0F) * var13;
            var5x.drawRoundedRect(
               var14 + 1.5F,
               var9 + 3.0F + (var5 - 6.0F - var16) / 2.0F,
               2.0F,
               var16,
               ProxyDotRadiusData.method8069(1.0F),
               var12.getPrimaryColor().getZenithColor().method7294(var11)
            );
            float var17 = var9 + 5.0F + (1.0F - var13) * 2.0F;

            for (Object var19_o : (Iterable<?>)(Object)(var3)) {
               String var19 = (String)var19_o;
               var5x.drawText(var2, var19, var14 + 7.0F, var17, var12.getTextSecondary().getZenithColor().method7294(var11 * var13));
               var17 += var4;
            }
         }
      );
   }

   private void press(String var1) {
      this.pressedAt.put(var1, System.currentTimeMillis());
   }

   private float flash(String var1) {
      long var2 = this.pressedAt.getOrDefault(var1, 0L);
      if (var2 == 0L) {
         return 0.0F;
      } else {
         float var4 = (float)(System.currentTimeMillis() - var2) / 220.0F;
         return var4 >= 1.0F ? 0.0F : 1.0F - var4;
      }
   }

   private void flashMessage(String var1) {
      this.flashText = var1;
      this.flashAt = System.currentTimeMillis();
      this.invalidate();
   }

   private boolean flashing() {
      return !this.flashText.isEmpty() && System.currentTimeMillis() - this.flashAt < 2600L;
   }

   private void copy(String var1) {
      if (var1 != null && !var1.isBlank()) {
         mc.keyboard.setClipboard(var1);
         this.flashMessage(t("gui.friends.copied"));
      }
   }

   private void buttonsRow(String var1, String var2, Runnable var3, String var4, Runnable var5) {
      this.buttonsRow(var1, var2, var3, var4, var5, false);
   }

   private void buttonsRow(String var1, String var2, Runnable var3, String var4, Runnable var5, boolean var6) {
      this.custom(19.0F, (var5x, var6x, var7, var8, var9, var10, var11, var12) -> {
         float var13 = (var10 - 16.0F - 4.0F) / 2.0F;
         BoundsData var14 = new BoundsData(var8 + 8.0F, var9 + 2.0F, var13, 15.0F);
         BoundsData var15 = new BoundsData(var8 + 8.0F + var13 + 4.0F, var9 + 2.0F, var13, 15.0F);
         int var16 = var14.contains(var6x, var7) ? 0 : (var15.contains(var6x, var7) ? 1 : -1);
         this.hoveredButton.put(var1, var16);
         float var17 = var6 ? 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0)) : 1.0F;
         this.pillButton(var5x, var1 + ":0", var14, t(var2), false, var16 == 0, var11, var12);
         this.pillButton(var5x, var1 + ":1", var15, t(var4), true, var16 == 1 && !var6, var11 * var17, var12);
      }, () -> {
         int var5x = this.hoveredButton.getOrDefault(var1, -1);
         if (var5x == 0) {
            this.press(var1 + ":0");
            var3.run();
         } else if (var5x == 1 && !var6) {
            this.press(var1 + ":1");
            var5.run();
         }
      }, null);
   }

   private float hover(String var1, boolean var2) {
      return this.buttonHover.computeIfAbsent("hover:" + var1, var0 -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private float pick(String var1, boolean var2) {
      return this.cardSelect.computeIfAbsent("pick:" + var1, var0 -> new Animation(180L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private float select(String var1, boolean var2) {
      return this.cardSelect.computeIfAbsent(var1, var0 -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private static String fit(Font var0, String var1, float var2) {
      return var1 != null && !var1.isEmpty() && !(var2 <= 0.0F) ? var0.clip(var1, var2) : "";
   }

   private static String accountName() {
      Zenith var0 = Zenith.getInstance();
      return var0 != null && var0.getEntry() != null && var0.getEntry().getUsername() != null ? var0.getEntry().getUsername() : "";
   }

   private static long accountUid() {
      Zenith var0 = Zenith.getInstance();
      if (var0 != null && var0.getEntry() != null && var0.getEntry().getText2() != null) {
         try {
            return Long.parseLong(var0.getEntry().getText2().trim());
         } catch (NumberFormatException var2) {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   private static List<ItemStack> equipment(PartyPlayerInfo var0) {
      CloudPackets.Data7[] var1 = var0.getData7s();
      if (var1.length <= 40) {
         return List.of();
      } else {
         ArrayList var2 = new ArrayList(5);

         for (int var3 = 39; var3 >= 36; var3--) {
            ItemStack var4 = stackOf(var1[var3]);
            if (!var4.isEmpty()) {
               var2.add(var4);
            }
         }

         ItemStack var5 = stackOf(var1[40]);
         if (!var5.isEmpty()) {
            var2.add(var5);
         }

         return var2;
      }
   }

   private static ItemStack stackOf(CloudPackets.Data7 var0) {
      if (var0 != null && !var0.isEmpty()) {
         Identifier var1 = Identifier.tryParse(var0.getText());
         if (var1 != null && Registries.ITEM.containsId(var1)) {
            Item var2 = Registries.ITEM.get(var1);
            if (var2 == Items.AIR) {
               return ItemStack.EMPTY;
            } else {
               ItemStack var3 = new ItemStack(var2, Math.max(1, Math.min(127, var0.count())));
               if (var0.getIntValue2() > 0) {
                  var3.setDamage(var0.getIntValue2());
               }

               return var3;
            }
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private static long uidOf(String var0) {
      CloudConnectionState var1 = state();
      long var2 = var1 == null ? 0L : var1.uidOf(displayName(var0));
      if (var2 > 0L) {
         return var2;
      } else {
         Prefs var4 = prefs();
         return var4 == null ? 0L : var4.method3311(var0);
      }
   }

   private static PartyPlayerInfo peer(String var0) {
      CloudConnectionState var1 = state();
      return var1 == null ? null : var1.method8044(var0);
   }

   private static boolean isPendingName(String var0) {
      CloudConnectionState var1 = state();
      return var1 != null && var1.method8061(var0) && var1.method8044(var0) == null;
   }

   private static String displayName(String var0) {
      PartyPlayerInfo var1 = peer(var0);
      return var1 == null ? var0 : var1.username();
   }

   private static String whereLine(String var0) {
      PartyPlayerInfo var1 = peer(var0);
      CloudConnectionState var2 = state();
      if (var1 == null) {
         return t(var2 != null && var2.method8061(var0) ? "gui.friends.status.pendingLong" : "gui.friends.status.offline");
      } else {
         String var3 = blankToDash(var1.method3290());
         String var4 = shortWorld(var1.dimension());
         if ("-".equals(var3)) {
            return t("gui.friends.inMenu");
         } else {
            return !var4.isEmpty() && !"-".equals(var4) ? var3 + " · " + var4 : var3;
         }
      }
   }

   private static float statusLit(String var0) {
      CloudConnectionState var1 = state();
      if (var1 == null) {
         return 0.0F;
      } else {
         return var1.method8044(var0) == null && !var1.method8061(var0) ? 0.0F : 1.0F;
      }
   }

   private static ZenithColor statusColor(String var0, ZenithStyle var1) {
      CloudConnectionState var2 = state();
      PartyPlayerInfo var3 = var2 == null ? null : var2.method8044(var0);
      if (var3 != null) {
         Cloud var4 = cloud();
         if (var4 != null && var4.getPartyMember().method1142(var3.uid())) {
            String var5 = var4.getPartyMember().method1141(var3.uid()).getText();
            if (!"online".equals(var5) && !var5.isEmpty()) {
               return PartyManager.method51(var5);
            }
         }

         return var1.getFriendColor().getZenithColor();
      } else {
         return var2 != null && var2.method8061(var0) ? var1.getPrimaryColor().getZenithColor() : var1.getTextTertiary().getZenithColor();
      }
   }

   private static String distanceTo(String var0) {
      PartyPlayerInfo var1 = peer(var0);
      Cloud var2 = cloud();
      if (var1 != null && var2 != null && mc.player != null) {
         if (!var1.method3291(var2.method7911(), var2.method7913())) {
            return "-";
         } else {
            double var3 = var1.getDoubleValue() - mc.player.getX();
            double var5 = var1.getDoubleValue2() - mc.player.getY();
            double var7 = var1.getDoubleValue3() - mc.player.getZ();
            double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
            return Double.isFinite(var9) && !(var9 >= 1.0E8) ? Math.round(var9) + " " + t("gui.unit.meter") : "-";
         }
      } else {
         return "-";
      }
   }

   private boolean isNearby(String var1) {
      if (mc.world != null && var1 != null) {
         List<AbstractClientPlayerEntity> var2 = mc.world.getPlayers();
         long var3 = System.nanoTime();
         if (this.nearbyWorld != mc.world || this.nearbyCount != var2.size() || var3 - this.nearbyAt > 4000000L) {
            this.nearbyNames.clear();

            for (Object var6_o : (Iterable<?>)(Object)(var2)) {
               AbstractClientPlayerEntity var6 = (AbstractClientPlayerEntity)var6_o;
               this.nearbyNames.add(var6.getName().getString());
            }

            this.nearbyWorld = mc.world;
            this.nearbyCount = var2.size();
            this.nearbyAt = var3;
         }

         return this.nearbyNames.contains(var1);
      } else {
         return false;
      }
   }

   private static boolean matches(String var0, String var1) {
      return var1.isEmpty() || var0 != null && var0.toLowerCase(Locale.ROOT).contains(var1);
   }

   private static String blankToDash(String var0) {
      return var0 != null && !var0.isBlank() ? var0 : "-";
   }

   private static String shortWorld(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim();
         int var2 = var1.indexOf(58);
         return var2 > 0 && var2 + 1 < var1.length() ? var1.substring(var2 + 1) : var1;
      } else {
         return "";
      }
   }

   private static final class Hint {
      private final String name;
      private final String source;
      private final long uid;
      private final int addKind;

      public String name() {
         return this.name;
      }

      public String source() {
         return this.source;
      }

      public long uid() {
         return this.uid;
      }

      public int addKind() {
         return this.addKind;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Hint other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.source, other.source) && java.util.Objects.equals(this.uid, other.uid) && java.util.Objects.equals(this.addKind, other.addKind);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, source, uid, addKind);
      }

      @Override
      public String toString() {
         return "Hint[" + "name=" + this.name + ", " + "source=" + this.source + ", " + "uid=" + this.uid + ", " + "addKind=" + this.addKind + "]";
      }

      private Hint(String var1, String var2, long var3, int var5) {
         this.name = var1;
         this.source = var2;
         this.uid = var3;
         this.addKind = var5;
      }
   }

   private static final class Tile {
      private final String key;
      private final MenuGlyph icon;
      private final String label;
      private final String note;
      private final boolean active;
      private final Runnable action;

      public String key() {
         return this.key;
      }

      public MenuGlyph icon() {
         return this.icon;
      }

      public String label() {
         return this.label;
      }

      public String note() {
         return this.note;
      }

      public boolean active() {
         return this.active;
      }

      public Runnable action() {
         return this.action;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Tile other)) return false;
         return java.util.Objects.equals(this.key, other.key) && java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.label, other.label) && java.util.Objects.equals(this.note, other.note) && java.util.Objects.equals(this.active, other.active) && java.util.Objects.equals(this.action, other.action);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(key, icon, label, note, active, action);
      }

      @Override
      public String toString() {
         return "Tile[" + "key=" + this.key + ", " + "icon=" + this.icon + ", " + "label=" + this.label + ", " + "note=" + this.note + ", " + "active=" + this.active + ", " + "action=" + this.action + "]";
      }

      private Tile(String var1, MenuGlyph var2, String var3, String var4, boolean var5, Runnable var6) {
         this.key = var1;
         this.icon = var2;
         this.label = var3;
         this.note = var4;
         this.active = var5;
         this.action = var6;
      }
   }
}

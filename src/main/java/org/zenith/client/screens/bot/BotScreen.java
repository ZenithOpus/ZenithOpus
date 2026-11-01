package org.zenith.client.screens.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.bot.BotAvatarCache;
import org.zenith.base.bot.ServerIconCache;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.ChatMessage;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.BotModuleManager;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.via.BotProtocolVersions;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.base.font.Font;
import org.zenith.base.font.FontData;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class BotScreen extends CustomScreen {
   private static final float leftPanelWidth = 240.0F;
   private static final float rightPanelWidth = 240.0F;
   private static final float panelWidth = 480.0F;
   private static final float panelHeight = 320.0F;
   private static final ProxyDotRadiusData panelRadius = ProxyDotRadiusData.method8069(10.0F);
   private static final ProxyDotRadiusData leftPanelRadius = ProxyDotRadiusData.method8078(10.0F, 10.0F);
   private static final ProxyDotRadiusData rightPanelRadius = ProxyDotRadiusData.method8079(10.0F, 10.0F);
   private static final long screenOpenAnimationDuration = 300L;
   private static final long screenCloseAnimationDuration = 220L;
   private static final float screenAnimationEpsilon = 0.001F;
   private static final float screenStartScale = 0.88F;
   private static final float screenStartOffsetY = 10.0F;
   private static final long popupAnimationDuration = 180L;
   private static final float shadowOffsetX = 0.0F;
   private static final float shadowOffsetY = 16.0F;
   private static final float shadowBlur = 32.0F;
   private static final float shadowSpread = -16.0F;
   private static final ZenithColor shadowColor = new ZenithColor(12, 12, 12, 20);
   private static final float blurRadius = 32.0F;
   private static final ZenithColor blurColor = new ZenithColor(255, 255, 255, 255);
   private static final ZenithColor leftColor = new ZenithColor(14, 14, 16, 122);
   private static final float headerOffset = 4.0F;
   private static final float headerWidth = 232.0F;
   private static final float headerHeight = 23.0F;
   private static ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(4.0F);
   private static final ZenithColor headerColor = new ZenithColor(14, 14, 16, 122);
   private static final float addButtonWidth = 23.0F;
   private static final ZenithColor addButtonColor = new ZenithColor(14, 14, 16, 61);
   private static final float searchSplitGap = 4.0F;
   private static final float searchHalfWidth = 114.0F;
   private static final float rowGap = 4.0F;
   private static final float searchOffsetY = 31.0F;
   private static final float listRowWidth = 227.0F;
   private static final float listStartY = 58.0F;
   private static final float listRowGap = 2.0F;
   private static final float listRowStride = 25.0F;
   private static final float listViewportHeight = 211.0F;
   private static final float bulkActionTabsOffsetY = 275.0F;
   private static final float bulkActionModeHeight = 14.0F;
   private static final float bulkActionModeButtonSize = 14.0F;
   private static final float bulkActionModeGap = 3.0F;
   private static final ProxyDotRadiusData bulkActionModeRadius = ProxyDotRadiusData.method8069(4.0F);
   private static final float bulkActionPayloadOffsetY = 292.0F;
   private static final float bulkActionRunWidth = 64.0F;
   private static final int botNameMinLength = 3;
   private static final int botNameMaxLength = 16;
   private static final int rctMinAnarchy = 1;
   private static final int rctMaxAnarchy = 66;
   private static final float listScrollStep = 25.0F;
   private static final float scrollbarGap = 4.0F;
   private static final float scrollbarWidth = 1.0F;
   private static final float scrollbarMinThumb = 16.0F;
   private static final ProxyDotRadiusData scrollbarRadius = ProxyDotRadiusData.method8069(0.5F);
   private static final ZenithColor scrollbarTrackColor = new ZenithColor(255, 255, 255, 5);
   private static final ZenithColor scrollbarThumbColor = new ZenithColor(255, 255, 255, 10);
   private static final float listActionWidth = 64.0F;
   private static final float serverIconSize = 7.0F;
   private static final float serverIconGap = 4.0F;
   private static final ProxyDotRadiusData serverIconRadius = ProxyDotRadiusData.method8069(2.0F);
   private static final ZenithColor listColor = new ZenithColor(14, 14, 16, 61);
   private static final float avatarSize = 15.0F;
   private static final ProxyDotRadiusData avatarRadius = ProxyDotRadiusData.method8069(1.9565217F);
   private static final float rightHeaderWidth = 232.0F;
   private static final float rightHeaderPanelWidth = 157.0F;
   private static final float rightHeaderPanelHeight = 23.0F;
   private static final float rightHeaderPanelX = 319.0F;
   private static final float screenGap = 12.0F;
   private static final float screenOffsetY = 39.0F;
   private static final float screenWidth = 232.0F;
   private static final float screenHeight = 130.5F;
   private static final float screenOffsetX = 244.0F;
   private static final float chatGap = 4.0F;
   private static final float chatHeight = 107.5F;
   private static final float chatPadding = 4.0F;
   private static final float chatLineGap = 1.0F;
   private static final float chatTextOffset = 8.0F;
   private static final float chatScrollStep = 14.0F;
   private static final long chatAppearDuration = 260L;
   private static final float inputGap = 4.0F;
   private static final float inputHeight = 23.0F;
   private static final float inputButtonWidth = 48.0F;
   private static final ZenithColor inputEmptyColor = new ZenithColor(255, 255, 255, 184);
   private static final Font nickFont = Fonts.MEDIUM.getFont(6.0F);
   private static final Font timeFont = Fonts.REGULAR.getFont(5.0F);
   private static final Font botIconFont = Fonts.NEW_ICONS.getFont(7.0F);
   private static final String botIcon = "P";
   private static final String addIcon = "D";
   private static final Font connectIconFont = Fonts.NEW_ICONS.getFont(6.0F);
   private static final String connectIcon = "Z";
   private static final String controlIcon = "P";
   private static final String offlineIcon = "]";
   private static final String sendIcon = "d";
   private static final String rctIcon = "7";
   private static final String disconnectIcon = "]";
   private static final ZenithColor disconnectColor = new ZenithColor(255, 99, 99, 255);
   private static final Font roleFont = Fonts.MEDIUM.getFont(5.0F);
   private static final ProxyDotRadiusData roleBadgeRadius = ProxyDotRadiusData.method8069(3.0F);
   private static final Font moduleMenuFont = Fonts.MEDIUM.getFont(5.0F);
   private static final float moduleMenuWidth = 118.0F;
   private static final float moduleMenuHeaderHeight = 18.0F;
   private static final float moduleMenuRowHeight = 20.0F;
   private static final float moduleMenuPadding = 4.0F;
   private static final float moduleMenuGap = 3.0F;
   private static final ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(6.0F);
   private static final ProxyDotRadiusData moduleMenuRowRadius = ProxyDotRadiusData.method8069(4.0F);
   private static final Font deleteIconFont = Fonts.NEW_ICONS.getFont(8.0F);
   private static final Font proxyDeleteIconFont = Fonts.NEW_ICONS.getFont(4.0F);
   private static final String deleteIcon = "2";
   private static final ZenithColor deleteColor = new ZenithColor(255, 64, 64, 255);
   private static final ZenithColor deleteDarkColor = new ZenithColor(18, 18, 20, 255);
   private static final float deleteDarken = 0.62F;
   private static final float connectIconGap = 4.0F;
   private static final float botIconGap = 3.0F;
   private static final String proxyIcon = "Z";
   private static final float proxyDotSize = 4.0F;
   private static final ProxyDotRadiusData proxyDotRadius = ProxyDotRadiusData.method8069(2.0F);
   private static final float proxyPanelSpace = 4.0F;
   private static final float proxyPanelSpaceHalf = 2.0F;
   private static final float proxyPanelSpaceDouble = 8.0F;
   private static final float proxyPanelWidth = 156.0F;
   private static final float proxyPanelHeight = 222.0F;
   private static final float proxyPanelGap = 8.0F;
   private static final float proxyPanelButtonSize = 18.0F;
   private static final float proxyPanelCloseHitSize = 14.0F;
   private static final float proxyPanelPadding = 4.0F;
   private static final float proxyPanelHeaderHeight = 27.0F;
   private static final float proxyPanelRowHeight = 23.0F;
   private static final float proxyPanelRowGap = 2.0F;
   private static final float proxyPanelInputGap = 4.0F;
   private static final float proxyPanelInputHeight = 18.0F;
   private static final float proxyPanelInputButtonSize = 18.0F;
   private static final float proxyPanelInputAfterGap = 4.0F;
   private static final float proxyPanelInputOffsetY = 31.0F;
   private static final float proxyPanelListOffsetY = 53.0F;
   private static final float proxyPanelListHeight = 161.0F;
   private static final float proxyPanelDeleteWidth = 18.0F;
   private static final float proxyPanelDeleteGap = 2.0F;
   private static final float proxyPanelScrollbarGap = 4.0F;
   private static final float proxyPanelStatusPadX = 4.0F;
   private static final float proxyPanelStatusInnerGap = 4.0F;
   private static final float proxyPanelStatusSeparatorWidth = 0.5F;
   private static final float proxyPanelStatusSeparatorHeight = 6.0F;
   private static final float proxyPanelScrollStep = 25.0F;
   private static final long proxyPanelAnimationDuration = 220L;
   private static final ProxyDotRadiusData proxyPanelRadius = ProxyDotRadiusData.method8069(10.0F);
   private static final ProxyDotRadiusData proxyPanelButtonRadius = ProxyDotRadiusData.method8069(5.0F);
   private static final ZenithColor proxyPingOkColor = new ZenithColor(88, 220, 166, 255);
   private static final ZenithColor proxyPingSlowColor = new ZenithColor(255, 206, 112, 255);
   private static final ZenithColor proxyPingBadColor = new ZenithColor(255, 99, 99, 255);
   private static final float chatLineStepCache = lineHeight(nickFont) + 1.0F;
   private static final float chatTimeTopOffsetCache = nickFont.height() - timeFont.height();
   private static final float chatTimeColumnWidthCache = timeFont.width("00:00:00");
   private static final float statusPadding = 8.0F;
   private static final float statusLineGap = 4.0F;
   private static final Font scoreboardFont = Fonts.NEW_MEDIUM.getFont(5.0F);
   private static final float scoreboardRowGap = 1.5F;
   private static final Font versionFont = Fonts.MEDIUM.getFont(5.0F);
   private static final float versionChipWidth = 34.0F;
   private static final float versionChipGap = 4.0F;
   private static final float versionMenuWidth = 72.0F;
   private static final float versionMenuRowHeight = 14.0F;
   private static final float versionMenuRowGap = 2.0F;
   private static final float versionMenuPadding = 4.0F;
   private static final int versionMenuVisibleRows = 9;
   private final Animation screenAnimation = new Animation(300L, 0.0F, Easing.INSTANCE4);
   private final Animation proxyPanelAnimation = new Animation(220L, 0.0F, Easing.INSTANCE13);
   private final Animation moduleMenuAnimation = new Animation(180L, 0.0F, Easing.INSTANCE15);
   private final Animation versionMenuAnimation = new Animation(180L, 0.0F, Easing.INSTANCE15);
   private final Map<String, BotScreen.RowAnim> rowAnims = new HashMap<>();
   private final Map<String, Animation> proxyHoverAnims = new HashMap<>();
   private final Map<ChatMessage, Animation> chatAnims = new IdentityHashMap<>();
   private final Map<ChatMessage, List<Text>> wrapCache = new IdentityHashMap<>();
   private BotClient chatAnimClient;
   private boolean closing = false;
   private static boolean savedProxyPanelOpen = false;
   private boolean proxyPanelOpen = savedProxyPanelOpen;
   private String selectedName;
   private final LinkedHashSet<String> selectedNames = new LinkedHashSet<>();
   private float chatScroll;
   private float listScroll;
   private float proxyPanelScroll;
   private final SearchBox chatInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Message...", 0.0F);
   private final SearchBox connectInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Connect to...", 0.0F);
   private final SearchBox addInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Add...", 0.0F);
   private final SearchBox searchInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Search...", 0.0F);
   private final SearchBox bulkActionInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Value...", 0.0F);
   private final SearchBox proxyManagerInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Proxy...", 0.0F);
   private BotScreen.BulkAction bulkAction = BotScreen.BulkAction.CONNECT;
   private String moduleMenuBotName;
   private float moduleMenuX;
   private float moduleMenuY;
   private float connectButtonWidth;
   private boolean versionMenuOpen;
   private float versionMenuScroll;
   private BotWorldView previewView;
   private BotClient previewClient;
   private BotClient scoreboardClient;
   private Text scoreboardTitle;
   private List<Text> scoreboardNames = List.of();
   private static final Map<BotScreen.FitKey, String> FIT_CACHE = new LinkedHashMap<BotScreen.FitKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<BotScreen.FitKey, String> var1) {
         return this.size() > 128;
      }
   };
   private static final Map<String, String> serverLabelCache = new HashMap<>();

   @Override
   public void init() {
      super.init();
      this.closing = false;
      this.screenAnimation.setDuration(300L);
      this.screenAnimation.setEasing(Easing.INSTANCE4);
      this.screenAnimation.method2809(0.0F);
      this.screenAnimation.method2808(1.0F);
      this.moduleMenuAnimation.method2809(this.moduleMenuBotName == null ? 0.0F : 1.0F);
      this.versionMenuAnimation.method2809(this.versionMenuOpen ? 1.0F : 0.0F);
      this.connectButtonWidth = 23.0F;
      this.chatInput.setText2(tr("module.bot.message"));
      this.connectInput.setText2(tr("module.bot.connectTo"));
      this.connectInput.setType(SearchBox.Type.SERVER_ADDRESS);
      if (this.connectInput.getText().isBlank()) {
         ServerInfo var1 = mc.getCurrentServerEntry();
         if (var1 != null && var1.address != null && !var1.address.isBlank()) {
            this.connectInput.setText(var1.address.trim());
            this.connectInput.setCursor(this.connectInput.getText().length());
         }
      }

      this.addInput.setText2(tr("module.bot.add"));
      this.addInput.setType(SearchBox.Type.ENGLISH_NAME);
      this.addInput.setMaxLength(16);
      this.searchInput.setText2(tr("module.bot.search"));
      this.bulkActionInput.setMaxLength(256);
      this.proxyManagerInput.setText2(tr("module.bot.proxy"));
      this.proxyManagerInput.setMaxLength(256);
      this.proxyManagerInput.setType(SearchBox.Type.ANY);
      if (this.selectedName == null) {
         var var2 = HeadlessBots.allNames();
         if (!var2.isEmpty()) {
            this.selectedName = (String)var2.get(0);
         }
      }

      if (this.selectedName != null && this.selectedNames.isEmpty()) {
         this.selectedNames.add(this.selectedName);
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3) {
   }

   public void renderTop(MenuDrawContext var1, float var2, float var3) {
      float var4 = (mc.getWindow().getScaledWidth() - 480.0F) / 2.0F;
      float var5 = (mc.getWindow().getScaledHeight() - 320.0F) / 2.0F;
      float var6 = this.screenAnimation.method2808(this.closing ? 0.0F : 1.0F);
      if (this.closing && this.screenAnimation.isDone() && var6 <= 0.001F) {
         mc.setScreen(null);
      } else {
         float var7 = clamp01(var6);
         float var8 = var4 + 240.0F;
         float var9 = var5 + 160.0F;
         float var10 = this.closing ? var7 : var6;
         float var11 = 0.88F + 0.120000005F * var10;
         float var12 = 10.0F * (1.0F - var7);
         float var13 = (var2 - var8) / var11 + var8;
         float var14 = (var3 - var9 - var12) / var11 + var9;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var8, var9 + var12);
         var1.getMatrices().scale(var11, var11);
         var1.getMatrices().translate(-var8, -var9);
         RenderState.method1871();
         RenderState.method1873();
         this.renderProxyPanel(var1, var4, var5, var7, var13, var14);
         this.renderPanels(var1, var4, var5, var7, var13, var14);
         this.renderProxyPanelButton(var1, var4, var5, var7, var13, var14);
         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         var1.getMatrices().popMatrix();
      }
   }

   private void renderPanels(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         ServerIconCache.tick();
         RenderShaders.method5288(
            MatrixUtils.method664(var1.getMatrices()), var2, var3, 480.0F, 320.0F, 0.0F, 16.0F, 32.0F, -16.0F, panelRadius, shadowColor.method7294(var4)
         );
         this.renderPanelBlur(var1, var2, var3, var4);
         var1.drawRoundedRect(var2, var3, 240.0F, 320.0F, leftPanelRadius, leftColor.method7294(var4));
         var1.drawRoundedRect(var2 + 240.0F, var3, 240.0F, 320.0F, rightPanelRadius, var7.getRightBackground().getZenithColor().method7294(var4));
         var1.drawRoundedRect(var2 + 4.0F, var3 + 4.0F, 232.0F, 23.0F, headerRadius, headerColor.method7294(var4));
         float var8 = var3 + 31.0F;
         float var9 = centeredTextY(nickFont, var8, 23.0F);
         float var10 = var2 + 4.0F;
         var1.drawRoundedRect(var10, var8, 114.0F, 23.0F, headerRadius, headerColor.method7294(var4));
         float var11 = var10 + 114.0F - 23.0F;
         var1.drawRoundedRect(var11, var8, 23.0F, 23.0F, headerRadius, addButtonColor.method7294(var4));
         float var12 = botIconFont.width("D");
         var1.drawText(botIconFont, "D", var11 + (23.0F - var12) / 2.0F, centeredTextY(botIconFont, var8, 23.0F), inputEmptyColor.method7294(var4));
         this.addInput.setWidth(75.0F);
         this.addInput.method987(var1, var10 + 8.0F, var9, ZenithColor.INSTANCE.method7294(var4), inputEmptyColor.method7294(var4));
         float var13 = var10 + 114.0F + 4.0F;
         var1.drawRoundedRect(var13, var8, 114.0F, 23.0F, headerRadius, headerColor.method7294(var4));
         this.searchInput.setWidth(98.0F);
         this.searchInput.method987(var1, var13 + 8.0F, var9, ZenithColor.INSTANCE.method7294(var4), inputEmptyColor.method7294(var4));
         List<String> var14 = HeadlessBots.allNames();
         this.refreshSelection(var14);
         if (!this.rowAnims.isEmpty()) {
            var var15 = this.rowAnims.keySet().iterator();

            while (var15.hasNext()) {
               String var16 = (String)var15.next();
               boolean var17 = false;

               for (Object var19_o : (Iterable<?>)(Object)(var14)) {
                  String var19 = (String)var19_o;
                  if (var19.equals(var16)) {
                     var17 = true;
                     break;
                  }
               }

               if (!var17) {
                  var15.remove();
               }
            }
         }

         var var46 = this.filterBots(var14);
         float var47 = var3 + 58.0F;
         float var48 = var47 + 211.0F;
         this.listScroll = Math.max(0.0F, Math.min(this.listScroll, listMaxScroll(var46.size())));
         var1.enableScissor(var2 + 4.0F, var47, var2 + 4.0F + 227.0F, var48);

         for (int var49 = 0; var49 < var46.size(); var49++) {
            this.renderListRow(var1, (String)var46.get(var49), var49, var2, var47, var48, var4, var5, var6, var7);
         }

         var1.disableScissor();
         float var50 = var2 + 4.0F + 227.0F + 4.0F;
         var1.drawRoundedRect(var50, var47, 1.0F, 211.0F, scrollbarRadius, scrollbarTrackColor.method7294(var4));
         float var51 = listContentHeight(var46.size());
         float var20 = var51 <= 211.0F ? 211.0F : Math.max(16.0F, 44521.0F / var51);
         float var21 = listMaxScroll(var46.size());
         float var22 = var47 + (var21 > 0.0F ? this.listScroll / var21 : 0.0F) * (211.0F - var20);
         var1.drawRoundedRect(var50, var22, 1.0F, var20, scrollbarRadius, scrollbarThumbColor.method7294(var4));
         this.renderBulkActionTabs(var1, var2, var3 + 275.0F, var4, var7);
         this.renderBulkActionPayload(var1, var2, var3 + 292.0F, var4);
         var1.drawRoundedRect(
            var2 + 240.0F + 4.0F, var3 + 4.0F, 232.0F, 23.0F, headerRadius, var7.getSurfaceDisableBackground().getZenithColor().method7294(var4)
         );
         var1.drawRoundedRect(var2 + 240.0F + 4.0F, var3 + 4.0F, 23.0F, 23.0F, headerRadius, var7.getPanelLeftBackground().getZenithColor().method7294(var4));
         boolean var23 = this.selectedNames.size() > 1;
         if (var23) {
            float var24 = botIconFont.width("P");
            var1.drawText(
               botIconFont,
               "P",
               var2 + 240.0F + 4.0F + (23.0F - var24) / 2.0F,
               centeredTextY(botIconFont, var3 + 4.0F, 23.0F),
               var7.getPrimaryColor().getZenithColor().method7294(var4)
            );
            var1.drawText(
               nickFont,
               tr("module.bot.selected") + ": " + this.selectedNames.size(),
               var2 + 240.0F + 4.0F + 23.0F + 8.0F,
               centeredTextY(nickFont, var3 + 4.0F, 23.0F),
               var7.getTextEnable().getZenithColor().method7294(var4)
            );
         } else if (this.selectedName != null) {
            Identifier var52 = BotAvatarCache.prewarm(this.selectedName);
            if (var52 != null) {
               float var25 = var2 + 240.0F + 4.0F + 4.0F;
               float var26 = var3 + 4.0F + 4.0F;
               RenderShaders.method5294(
                  MatrixUtils.method664(var1.getMatrices()), var52, var25, var26, 15.0F, 15.0F, avatarRadius, ZenithColor.INSTANCE.method7294(var4)
               );
            }

            var1.drawText(
               nickFont,
               this.selectedName,
               var2 + 240.0F + 4.0F + 23.0F + 8.0F,
               centeredTextY(nickFont, var3 + 4.0F, 23.0F),
               var7.getTextEnable().getZenithColor().method7294(var4)
            );
         }

         float var53 = nickFont.width("Bot");
         float var54 = botIconFont.width("P");
         float var55 = var2 + 4.0F + (232.0F - var54 - 3.0F - var53) / 2.0F;
         var1.drawText(botIconFont, "P", var55, centeredTextY(botIconFont, var3 + 4.0F, 23.0F), var7.getPrimaryColor().getZenithColor().method7294(var4));
         var1.drawText(nickFont, "Bot", var55 + var54 + 3.0F, centeredTextY(nickFont, var3 + 4.0F, 23.0F), ZenithColor.INSTANCE.method7294(var4));
         BotClient var27 = this.selectedClient();
         boolean var28 = var27 != null && var27.isJoined();
         float var29 = var2 + 319.0F;
         float var30 = var3 + 4.0F;
         float var31 = var29 + 157.0F - this.connectButtonWidth;
         float var32 = var31 - 4.0F - 34.0F;
         this.connectInput.setWidth(157.0F - this.connectButtonWidth - 34.0F - 4.0F - 15.0F - 8.0F);
         this.connectInput
            .method987(
               var1,
               var29 + 15.0F,
               centeredTextY(nickFont, var30, 23.0F),
               var7.getTextEnable().getZenithColor().method7294(var4),
               var7.getTextSecondary().getZenithColor().method7294(var4)
            );
         var1.drawRoundedRect(var31, var30, this.connectButtonWidth, 23.0F, headerRadius, var7.getPanelLeftBackground().getZenithColor().method7294(var4));
         String var33 = var28 ? "P" : "Z";
         float var34 = connectIconFont.width(var33);
         var1.drawText(
            connectIconFont,
            var33,
            var31 + (this.connectButtonWidth - var34) / 2.0F,
            centeredTextY(connectIconFont, var30, 23.0F) + 0.5F,
            (var28 ? var7.getPrimaryColor().getZenithColor() : var7.getTextSecondary().getZenithColor()).method7294(var4)
         );
         var1.drawRoundedRect(
            var32,
            var30,
            34.0F,
            23.0F,
            headerRadius,
            (this.versionMenuOpen ? var7.getPrimaryColor().getZenithColor().method7293(42) : var7.getPanelLeftBackground().getZenithColor()).method7294(var4)
         );
         String var35 = fitText(versionFont, BotProtocolVersions.label(this.selectedProtocolVersion()), 28.0F);
         ZenithColor var36 = this.selectedProtocolVersion() > 0 ? var7.getPrimaryColor().getZenithColor() : var7.getTextSecondary().getZenithColor();
         var1.drawText(versionFont, var35, var32 + (34.0F - versionFont.width(var35)) / 2.0F, centeredTextY(versionFont, var30, 23.0F), var36.method7294(var4));
         float var37 = var2 + 244.0F;
         float var38 = var3 + 39.0F + 130.5F + 4.0F;
         float var39 = var38 + 107.5F + 4.0F;
         if (var28) {
            var1.drawRoundedRect(
               var2 + 244.0F, var3 + 39.0F, 232.0F, 130.5F, headerRadius, var7.getSurfaceDisableBackground().getZenithColor().method7294(var4)
            );
            this.renderPreview(var1, var27, var2 + 244.0F, var3 + 39.0F, var4);
            this.renderStatusPanel(var1, var27, var2 + 244.0F, var3 + 39.0F, var4, var7);
            var1.drawRoundedRect(var37, var38, 232.0F, 107.5F, headerRadius, var7.getSurfaceDisableBackground().getZenithColor().method7294(var4));
         } else {
            this.closePreview();
            float var40 = 242.0F;
            var1.drawRoundedRect(var37, var3 + 39.0F, 232.0F, var40, headerRadius, var7.getSurfaceDisableBackground().getZenithColor().method7294(var4));
            String var41 = tr("module.bot.noConnection");
            float var42 = nickFont.width(var41);
            var1.drawText(
               nickFont,
               var41,
               var37 + (232.0F - var42) / 2.0F,
               centeredTextY(nickFont, var3 + 39.0F, var40),
               var7.getTextSecondary().getZenithColor().method7294(var4)
            );
         }

         if (var28) {
            float var56 = var37 + 232.0F - 48.0F;
            this.chatInput.setWidth(168.0F);
            this.chatInput
               .method987(
                  var1,
                  var37 + 8.0F,
                  centeredTextY(nickFont, var39, 23.0F),
                  var7.getTextEnable().getZenithColor().method7294(var4),
                  var7.getTextSecondary().getZenithColor().method7294(var4)
               );
            var1.drawRoundedRect(var56, var39, 48.0F, 23.0F, headerRadius, var7.getPanelLeftBackground().getZenithColor().method7294(var4));
            String var57 = tr("module.bot.send");
            float var58 = nickFont.width(var57);
            float var43 = connectIconFont.width("d");
            float var44 = 2.0F;
            float var45 = var56 + (48.0F - var43 - var44 - var58) / 2.0F;
            var1.drawText(connectIconFont, "d", var45, centeredTextY(connectIconFont, var39, 23.0F), var7.getTextSecondary().getZenithColor().method7294(var4));
            var1.drawText(
               nickFont, var57, var45 + var43 + var44, centeredTextY(nickFont, var39, 23.0F), var7.getTextSecondary().getZenithColor().method7294(var4)
            );
         } else {
            this.chatInput.setSelected(false);
         }

         if (var28) {
            this.renderChatMessages(var1, var27, var37, var38, var4, var7);
         }

         this.renderModuleMenu(var1, var4, var5, var6, var7);
         this.renderVersionMenu(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   private void renderStatusPanel(MenuDrawContext var1, BotClient var2, float var3, float var4, float var5, ZenithStyle var6) {
      float var7 = var3 + 8.0F;
      float var8 = var4 + 8.0F;
      float var9 = lineHeight(nickFont) + 4.0F;
      var1.drawText(nickFont, var2.getName(), var7, var8, var6.getTextEnable().getZenithColor().method7294(var5));
      String var10 = var2.getPhase().name();
      float var11 = roleFont.width(var10);
      float var12 = var11 + 8.0F;
      float var13 = var3 + 232.0F - 8.0F - var12;
      var1.drawRoundedRect(var13, var8 - 1.5F, var12, 10.0F, roleBadgeRadius, var6.getPrimaryColor().getZenithColor().method7293(34).method7294(var5));
      var1.drawText(roleFont, var10, var13 + 4.0F, var8, var6.getPrimaryColor().getZenithColor().method7294(var5));
      var8 += var9 + 4.0F;
      this.renderBotScoreboard(var1, var2, var3, var4, var5, var6);
   }

   private void renderBotScoreboard(MenuDrawContext var1, BotClient var2, float var3, float var4, float var5, ZenithStyle var6) {
      BotWorld var7 = var2.getWorld();
      if (var7 != null) {
         Text var8;
         List var9;
         try {
            synchronized (var2.getRenderStateLock()) {
               label113: {
                  Scoreboard var11 = var7.getScoreboard();
                  synchronized (var11) {
                     ScoreboardObjective var13 = null;
                     Team var14 = var11.getScoreHolderTeam(var2.getName());
                     if (var14 != null) {
                        ScoreboardDisplaySlot var15 = ScoreboardDisplaySlot.fromFormatting(var14.getColor());
                        if (var15 != null) {
                           var13 = var11.getObjectiveForSlot(var15);
                        }
                     }

                     if (var13 == null) {
                        var13 = var11.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
                     }

                     if (var13 == null) {
                        this.scoreboardTitle = null;
                     } else {
                        ArrayList<ScoreboardEntry> var26 = new ArrayList<>(var11.getScoreboardEntries(var13));
                        var26.removeIf(ScoreboardEntry::hidden);
                        var26.sort(InGameHud.SCOREBOARD_ENTRY_COMPARATOR);
                        var9 = new ArrayList(var26.size());

                        for (Object var17_o : (Iterable<?>)(Object)(var26)) {
                           ScoreboardEntry var17 = (ScoreboardEntry)var17_o;
                           var9.add(Team.decorateName(var11.getScoreHolderTeam(var17.owner()), var17.name()));
                        }

                        var8 = var13.getDisplayName();
                        break label113;
                     }
                  }

                  return;
               }
            }

            this.scoreboardClient = var2;
            this.scoreboardTitle = var8;
            this.scoreboardNames = (List<Text>)var9;
         } catch (RuntimeException var22) {
            if (this.scoreboardClient != var2 || this.scoreboardTitle == null) {
               return;
            }

            var8 = this.scoreboardTitle;
            var9 = this.scoreboardNames;
         }

         float var10 = lineHeight(scoreboardFont) + 1.5F;
         float var23 = var4 + 8.0F + 12.0F;
         float var12 = var4 + 130.5F - 8.0F;
         int var24 = Math.max(0, (int)((var12 - var23 - var10) / var10));
         if (var24 > 0) {
            List var25 = var9.size() > var24 ? var9.subList(0, var24) : var9;
            if (!var25.isEmpty()) {
               float var27 = var3 + 232.0F - 8.0F;
               var1.drawText(
                  scoreboardFont, var8, var27 - scoreboardFont.width(var8), var23, var6.getTextEnable().getZenithColor().method7294(var5).method7287()
               );
               float var28 = var23 + (var10 + 1.5F);

               for (Object var18_o : (Iterable<?>)(Object)(var25)) {
                  Text var18 = (Text)var18_o;
                  var1.drawText(
                     scoreboardFont, var18, var27 - scoreboardFont.width(var18), var28, var6.getTextEnable().getZenithColor().method7294(var5).method7287()
                  );
                  var28 += var10;
               }
            }
         }
      }
   }

   private List<String[]> statusLines(BotClient var1) {
      ArrayList<String[]> var2 = new ArrayList<>();
      var2.add(new String[]{"Server", var1.getAddress()});
      BotPlayer var3 = var1.getPlayer();
      if (var3 != null) {
         var2.add(new String[]{"Health", String.format(Locale.ROOT, "%.1f", var3.getHealth())});
         var2.add(new String[]{"Pos", (int)var3.getX() + " " + (int)var3.getY() + " " + (int)var3.getZ()});
      }

      BotWorld var4 = var1.getWorld();
      if (var4 != null) {
         var2.add(new String[]{"World", var4.getRegistryKey().getValue().getPath()});
      }

      var2.add(new String[]{"RCT", var1.getRct().isActive() ? "active" : "idle"});
      StringBuilder var5 = new StringBuilder();

      for (BotModule var7 : var1.getModules().getModules()) {
         if (var7.isEnabled()) {
            if (var5.length() > 0) {
               var5.append(", ");
            }

            var5.append(var7.getName());
         }
      }

      var2.add(new String[]{"Modules", var5.length() == 0 ? "-" : var5.toString()});
      return var2;
   }

   private void renderPreview(MenuDrawContext var1, BotClient var2, float var3, float var4, float var5) {
      if (this.previewClient != var2) {
         this.closePreview();
         this.previewClient = var2;
         this.previewView = new BotWorldView(var2);
      }

      double var6 = mc.getWindow().getScaleFactor();
      int var8 = (int)(232.0 * var6);
      int var9 = (int)(130.5 * var6);
      ScissorFlusher.flush();
      if (this.previewView.renderToFbo(var8, var9)) {
         RenderShaders.method5298(
            MatrixUtils.method664(var1.getMatrices()),
            this.previewView.getColorAttachment(),
            var3,
            var4,
            232.0F,
            130.5F,
            headerRadius,
            ZenithColor.INSTANCE.method7294(var5),
            0.0F,
            1.0F,
            1.0F,
            0.0F
         );
         var1.drawRoundedRect(var3, var4, 232.0F, 130.5F, headerRadius, new ZenithColor(10, 10, 14, 150).method7294(var5));
      }
   }

   private void closePreview() {
      if (this.previewView != null) {
         this.previewView.close();
         this.previewView = null;
      }

      this.previewClient = null;
   }

   @Override
   public void removed() {
      this.closePreview();
      ServerIconCache.clear();
      BotAvatarCache.clear();
      super.removed();
   }

   private void renderPanelBlur(MenuDrawContext var1, float var2, float var3, float var4) {
      RenderShaders.method5287(
         MatrixUtils.method664(var1.getMatrices()), var2, var3, 480.0F, 320.0F, 32.0F, panelRadius, blurColor.method7294(var4), true, false
      );
   }

   private void renderProxyPanel(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = this.proxyPanelAnimation.method2808(this.proxyPanelOpen ? 1.0F : 0.0F);
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         if (var7 > 0.001F) {
            float var9 = proxyPanelRenderX(var2, var7);
            float var10 = proxyPanelY(var3);
            float var11 = var4 * var7;
            RenderShaders.method5299(
               MatrixUtils.method664(var1.getMatrices()), var9, var10 + 8.0F, 156.0F, 222.0F, 16.0F, proxyPanelRadius, shadowColor.method7294(var11)
            );
            RenderShaders.method5287(
               MatrixUtils.method664(var1.getMatrices()), var9, var10, 156.0F, 222.0F, 32.0F, proxyPanelRadius, blurColor.method7294(var11), true, false
            );
            var1.drawRoundedRect(var9, var10, 156.0F, 222.0F, proxyPanelRadius, var8.getRightBackground().getZenithColor().method7294(var11));
            var1.drawRoundedRect(
               var9, var10, 156.0F, 27.0F, ProxyDotRadiusData.method8075(10.0F), var8.getHeaderDisableBackground().getZenithColor().method7294(var11)
            );
            float var12 = connectIconFont.width("Z");
            float var13 = var9 + 4.0F + 4.0F;
            var1.drawText(connectIconFont, "Z", var13, centeredTextY(connectIconFont, var10, 27.0F), var8.getPrimaryColor().getZenithColor().method7294(var11));
            var1.drawText(
               nickFont,
               tr("module.bot.proxyPanel"),
               var13 + var12 + 4.0F,
               centeredTextY(nickFont, var10, 27.0F),
               var8.getTextEnable().getZenithColor().method7294(var11)
            );
            float var14 = proxyPanelCloseHitX(var9);
            float var15 = proxyPanelCloseHitY(var10);
            boolean var16 = var5 >= var14 && var5 <= var14 + 14.0F && var6 >= var15 && var6 <= var15 + 14.0F;
            float var17 = proxyDeleteIconFont.width("2");
            ZenithColor var18 = var8.getTextTertiary().getZenithColor().method7295(var8.getTextSecondary().getZenithColor(), var16 ? 0.9F : 0.0F);
            var1.drawText(
               proxyDeleteIconFont, "2", var14 + (14.0F - var17) / 2.0F, var15 + (14.0F - proxyDeleteIconFont.height()) / 2.0F, var18.method7294(var11)
            );
            this.renderProxyPanelInput(var1, var8, var9, var10 + 31.0F, var11);
            this.renderProxyPanelList(var1, var8, var9, var10 + 53.0F, var11, var5, var6);
         }
      }
   }

   private void renderProxyPanelButton(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         float var8 = this.proxyPanelAnimation.getValue();
         float var9 = proxyPanelButtonX(var2);
         float var10 = proxyPanelButtonY(var3);
         boolean var11 = var5 >= var9 && var5 <= var9 + 18.0F && var6 >= var10 && var6 <= var10 + 18.0F;
         ZenithColor var12 = (this.proxyPanelOpen ? var7.getHeaderDisableBackground().getZenithColor() : var7.getDisableActiveBg().getZenithColor())
            .method7295(var7.getPrimaryColor().getZenithColor(), !var11 && !this.proxyPanelOpen ? 0.0F : 0.18F);
         var1.drawRoundedRect(var9, var10, 18.0F, 18.0F, proxyPanelButtonRadius, var12.method7294(var4));
         float var13 = connectIconFont.width("Z");
         var1.drawText(
            connectIconFont,
            "Z",
            var9 + (18.0F - var13) / 2.0F,
            centeredTextY(connectIconFont, var10, 18.0F),
            var7.getTextSecondary().getZenithColor().method7295(var7.getPrimaryColor().getZenithColor(), var8 > 0.01F ? 1.0F : 0.0F).method7294(var4)
         );
      }
   }

   private void renderProxyPanelInput(MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5) {
      float var6 = var3 + 4.0F;
      float var7 = 126.0F;
      var1.drawRoundedRect(var6, var4, var7, 18.0F, headerRadius, var2.getHeaderHudBackground().getZenithColor().method7294(var5));
      this.proxyManagerInput.setWidth(var7 - 16.0F);
      this.proxyManagerInput
         .method987(
            var1,
            var6 + 8.0F,
            centeredTextY(nickFont, var4, 18.0F),
            var2.getTextEnable().getZenithColor().method7294(var5),
            var2.getTextSecondary().getZenithColor().method7294(var5)
         );
      float var8 = var6 + var7 + 4.0F;
      var1.drawRoundedRect(var8, var4, 18.0F, 18.0F, headerRadius, var2.getPrimaryColor().getZenithColor().method7294(0.25F).method7294(var5));
      float var9 = botIconFont.width("D");
      var1.drawText(
         botIconFont,
         "D",
         var8 + (18.0F - var9) / 2.0F + 1.0F,
         centeredTextY(botIconFont, var4, 18.0F),
         var2.getPrimaryColor().getZenithColor().method7294(var5)
      );
   }

   private void renderProxyPanelList(MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, float var6, float var7) {
      List<String> var8 = HeadlessBots.getProxyPool();
      this.pruneProxyHoverAnimations(var8);
      int var9 = var8.size() + 1;
      this.proxyPanelScroll = Math.max(0.0F, Math.min(this.proxyPanelScroll, proxyPanelMaxScroll(var9)));
      float var10 = var3 + 4.0F;
      float var11 = proxyPanelRowWidth();
      float var12 = var4 + 161.0F;
      var1.enableScissor(var10, var4, var10 + var11, var12);
      float var13 = var4 - this.proxyPanelScroll;
      this.renderProxyPanelRow(
         var1,
         proxyNoneKey(),
         tr("module.bot.proxyNone"),
         this.noProxyCount(),
         this.selectionHasNoProxy(),
         false,
         var10,
         var13,
         var11,
         var4,
         var12,
         var5,
         var6,
         var7,
         var2
      );
      var13 += 25.0F;

      for (Object var15_o : (Iterable<?>)(Object)(var8)) {
         String var15 = (String)var15_o;
         this.renderProxyPanelRow(
            var1,
            var15,
            var15,
            HeadlessBots.getProxyUseCount(var15),
            this.selectionUsesProxy(var15),
            true,
            var10,
            var13,
            var11,
            var4,
            var12,
            var5,
            var6,
            var7,
            var2
         );
         var13 += 25.0F;
      }

      var1.disableScissor();
      float var19 = proxyPanelScrollbarX(var3);
      var1.drawRoundedRect(var19, var4, 1.0F, 161.0F, scrollbarRadius, var2.getDisableActiveBg().getZenithColor().method7294(var5 * 0.65F));
      if (proxyPanelMaxScroll(var9) > 0.0F) {
         float var20 = proxyPanelContentHeight(var9);
         float var16 = Math.max(16.0F, 25921.0F / var20);
         float var17 = var4 + this.proxyPanelScroll / proxyPanelMaxScroll(var9) * (161.0F - var16);
         var1.drawRoundedRect(var19, var17, 1.0F, var16, scrollbarRadius, var2.getPrimaryColor().getZenithColor().method7294(var5));
      }
   }

   private void renderProxyPanelRow(
      MenuDrawContext var1,
      String var2,
      String var3,
      int var4,
      boolean var5,
      boolean var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      ZenithStyle var15
   ) {
      boolean var16 = var14 >= var10 && var14 <= var11 && var13 >= var7 && var13 <= var7 + var9 && var14 >= var8 && var14 <= var8 + 23.0F;
      boolean var17 = var6 && var16 && var13 >= var7 + var9 - 18.0F;
      Animation var18 = this.proxyHoverAnims.computeIfAbsent(var2, var0 -> new Animation(150L, 0.0F, Easing.INSTANCE15));
      float var19 = var18.method2808(var16 ? 1.0F : 0.0F);
      ZenithColor var20 = var15.getHeaderDisableBackground()
         .getZenithColor()
         .method7295(var15.getSurfaceEnableBackground().getZenithColor(), var5 ? 0.9F : var19 * 0.45F)
         .method7294(var12);
      var1.drawRoundedRect(var7, var8, var9, 23.0F, headerRadius, var20);
      if (var5) {
         float var21 = var7 + 4.0F;
         float var22 = var8 + 4.0F;
         float var23 = 15.0F;
         var1.drawRoundedRect(var21, var22, 3.0F, var23, scrollbarRadius, var15.getDisableActiveBg().getZenithColor().method7294(var12));
         var1.drawRoundedRect(var21, var22, 2.0F, var23, scrollbarRadius, var15.getPrimaryColor().getZenithColor().method7294(var12));
      }

      String var37 = String.valueOf(var4);
      float var38 = roleFont.width(var37);
      HeadlessBots.ProxyPingSnapshot var39 = var6 ? HeadlessBots.getProxyPing(var2) : null;
      String var24 = var39 == null ? "" : var39.label();
      float var25 = var39 == null ? 0.0F : roleFont.width(var24);
      float var26 = 8.0F + var38;
      if (var6) {
         var26 += var25 + 8.0F + 0.5F;
      }

      float var27 = var6 ? 20.0F : 8.0F;
      float var28 = var7 + var9 - var26 - var27;
      float var29 = 10.0F;
      float var30 = var8 + (23.0F - var29) / 2.0F;
      float var31 = var7 + 8.0F + 2.0F + var19;
      String var32 = fitText(nickFont, var3, Math.max(12.0F, var28 - var31 - 4.0F));
      var1.drawText(nickFont, var32, var31, centeredTextY(nickFont, var8, 23.0F), var15.getTextEnable().getZenithColor().method7294(var12));
      var1.drawRoundedRect(var28, var30, var26, var29, roleBadgeRadius, var15.getPrimaryColor().getZenithColor().method7293(34).method7294(var12));
      if (var6) {
         var1.drawText(roleFont, var24, var28 + 4.0F, centeredTextY(roleFont, var8, 23.0F), proxyPingColor(var39, var15).method7294(var12));
         float var33 = var28 + 4.0F + var25 + 4.0F;
         float var34 = var8 + 8.5F;
         var1.drawRect(var33, var34, 0.5F, 6.0F, var15.getDisableActiveBg().getZenithColor().method7294(var12));
      }

      var1.drawText(
         roleFont, var37, var28 + var26 - 4.0F - var38, centeredTextY(roleFont, var30, var29), var15.getPrimaryColor().getZenithColor().method7294(var12)
      );
      if (var6) {
         float var40 = proxyDeleteIconFont.width("2");
         float var41 = var7 + var9 - var40 - 8.0F;
         float var35 = var8 + (23.0F - proxyDeleteIconFont.height()) / 2.0F;
         ZenithColor var36 = var15.getTextTertiary().getZenithColor().method7295(var15.getTextSecondary().getZenithColor(), var17 ? 0.9F : var19 * 0.35F);
         var1.drawText(proxyDeleteIconFont, "2", var41, var35, var36.method7294(var12));
      }
   }

   private static float proxyPanelRenderX(float var0, float var1) {
      return var0 - 8.0F - 156.0F * var1;
   }

   private static float proxyPanelListWidth() {
      return 148.0F;
   }

   private static float proxyPanelRowWidth() {
      return proxyPanelListWidth() - 1.0F - 4.0F;
   }

   private static float proxyPanelScrollbarX(float var0) {
      return var0 + 156.0F - 4.0F - 1.0F;
   }

   private static float proxyPanelY(float var0) {
      return var0;
   }

   private static float proxyPanelButtonX(float var0) {
      return var0 + 4.0F + 2.5F;
   }

   private static float proxyPanelButtonY(float var0) {
      return var0 + 4.0F + 2.5F;
   }

   private static float proxyPanelCloseHitX(float var0) {
      return var0 + 156.0F - 4.0F - 14.0F;
   }

   private static float proxyPanelCloseHitY(float var0) {
      return var0 + 6.5F;
   }

   private static float proxyPanelContentHeight(int var0) {
      return var0 <= 0 ? 0.0F : var0 * 25.0F - 2.0F;
   }

   private static float proxyPanelMaxScroll(int var0) {
      return Math.max(0.0F, proxyPanelContentHeight(var0) - 161.0F);
   }

   private static ZenithColor proxyPingColor(HeadlessBots.ProxyPingSnapshot var0, ZenithStyle var1) {
      if (var0 == null) {
         return var1.getTextTertiary().getZenithColor();
      } else {
         return switch (var0.status()) {
            case OK -> var0.latencyMs() <= 750 ? proxyPingOkColor : proxyPingSlowColor;
            case FAILED -> proxyPingBadColor;
            case CHECKING, UNKNOWN -> var1.getTextTertiary().getZenithColor();
         };
      }
   }

   private static String proxyNoneKey() {
      return "__none__";
   }

   private int noProxyCount() {
      int var1 = 0;

      for (String var3 : HeadlessBots.allNames()) {
         if (!HeadlessBots.hasProxy(var3)) {
            var1++;
         }
      }

      return var1;
   }

   private boolean selectionHasNoProxy() {
      List<String> var1 = this.selectionNames();
      if (var1.isEmpty()) {
         return false;
      } else {
         for (Object var3_o : (Iterable<?>)(Object)(var1)) {
            String var3 = (String)var3_o;
            if (HeadlessBots.hasProxy(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean selectionUsesProxy(String var1) {
      List<String> var2 = this.selectionNames();
      if (!var2.isEmpty() && var1 != null && !var1.isBlank()) {
         for (Object var4_o : (Iterable<?>)(Object)(var2)) {
            String var4 = (String)var4_o;
            String var5 = HeadlessBots.getProxy(var4);
            if (var5 == null || !var5.equalsIgnoreCase(var1)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void pruneProxyHoverAnimations(List<String> var1) {
      var var2 = new HashSet<>(var1);
      var2.add(proxyNoneKey());
      this.proxyHoverAnims.keySet().removeIf(var1x -> !var2.contains(var1x));
   }

   private void renderBulkActionTabs(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      float var6 = var2 + 4.0F;
      var1.drawRoundedRect(var6, var3 - 3.0F, 232.0F, 1.0F, scrollbarRadius, scrollbarThumbColor.method7294(var4));
      float var7 = connectIconFont.width(this.bulkAction.icon);
      float var8 = 2.0F;
      var1.drawText(
         connectIconFont,
         this.bulkAction.icon,
         var6 + 3.0F,
         centeredTextY(connectIconFont, var3, 14.0F),
         var5.getPrimaryColor().getZenithColor().method7294(var4)
      );
      var1.drawText(
         nickFont, tr(this.bulkAction.tabKey), var6 + 3.0F + var7 + var8, centeredTextY(nickFont, var3, 14.0F), ZenithColor.INSTANCE.method7294(var4)
      );
      float var9 = bulkActionModeStartX(var2);

      for (BotScreen.BulkAction var13 : BotScreen.BulkAction.values()) {
         boolean var14 = var13 == this.bulkAction;
         var1.drawRoundedRect(var9, var3, 14.0F, 14.0F, bulkActionModeRadius, (var14 ? headerColor : addButtonColor).method7294(var4));
         float var15 = connectIconFont.width(var13.icon);
         var1.drawText(
            connectIconFont,
            var13.icon,
            var9 + (14.0F - var15) / 2.0F,
            centeredTextY(connectIconFont, var3, 14.0F),
            (var14 ? var5.getPrimaryColor().getZenithColor() : inputEmptyColor).method7294(var4)
         );
         var9 += 17.0F;
      }
   }

   private static float bulkActionModeStartX(float var0) {
      float var1 = BotScreen.BulkAction.values().length * 14.0F + (BotScreen.BulkAction.values().length - 1) * 3.0F;
      return var0 + 4.0F + 232.0F - var1;
   }

   private void focusTextBox(SearchBox var1, double var2, double var4) {
      var1.onMouseClicked(var2, var4, Enum3.LEFT);
      if (!var1.isSelected()) {
         var1.setSelected(true);
         var1.setFlag(false);
      }
   }

   private void renderBulkActionPayload(MenuDrawContext var1, float var2, float var3, float var4) {
      float var5 = var2 + 4.0F;
      this.bulkActionInput.setText2(tr(this.bulkAction.placeholderKey));
      this.syncBulkActionInputRules();
      float var6 = 164.0F;
      var1.drawRoundedRect(var5, var3, var6, 23.0F, headerRadius, headerColor.method7294(var4));
      this.bulkActionInput.setWidth(var6 - 16.0F);
      this.bulkActionInput
         .method987(var1, var5 + 8.0F, centeredTextY(nickFont, var3, 23.0F), ZenithColor.INSTANCE.method7294(var4), inputEmptyColor.method7294(var4));
      float var7 = var5 + var6 + 4.0F;
      var1.drawRoundedRect(var7, var3, 64.0F, 23.0F, headerRadius, addButtonColor.method7294(var4));
      this.drawCenteredIconText(var1, this.bulkAction.icon, tr(this.bulkAction.buttonKey), var7, var3, 64.0F, 23.0F, inputEmptyColor.method7294(var4));
   }

   private void syncBulkActionInputRules() {
      switch (this.bulkAction) {
         case CONNECT:
            this.bulkActionInput.setType(SearchBox.Type.SERVER_ADDRESS);
            this.bulkActionInput.setMaxLength(256);
            break;
         case CHAT:
            this.bulkActionInput.setType(SearchBox.Type.ANY);
            this.bulkActionInput.setMaxLength(256);
            break;
         case RCT:
            this.bulkActionInput.setType(SearchBox.Type.NUMBERS_ONLY);
            this.bulkActionInput.setMaxLength(2);
      }
   }

   private void drawCenteredIconText(MenuDrawContext var1, String var2, String var3, float var4, float var5, float var6, float var7, ZenithColor var8) {
      float var9 = connectIconFont.width(var2);
      float var10 = nickFont.width(var3);
      float var11 = 2.0F;
      float var12 = var9 + var11 + var10;
      if (var12 <= var6 - 6.0F) {
         float var13 = var4 + (var6 - var12) / 2.0F;
         var1.drawText(connectIconFont, var2, var13, centeredTextY(connectIconFont, var5, var7), var8);
         var1.drawText(nickFont, var3, var13 + var9 + var11, centeredTextY(nickFont, var5, var7), var8);
      } else if (var10 <= var6 - 8.0F) {
         var1.drawText(nickFont, var3, var4 + (var6 - var10) / 2.0F, centeredTextY(nickFont, var5, var7), var8);
      } else {
         var1.drawText(connectIconFont, var2, var4 + (var6 - var9) / 2.0F, centeredTextY(connectIconFont, var5, var7), var8);
      }
   }

   private void renderListRow(
      MenuDrawContext var1, String var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9, ZenithStyle var10
   ) {
      BotScreen.RowAnim var11 = this.rowAnims.computeIfAbsent(var2, var0 -> new BotScreen.RowAnim());
      BotClient var12 = HeadlessBots.get(var2);
      boolean var13 = var12 != null && var12.isJoined();
      float var14 = var5 + var3 * 25.0F - this.listScroll;
      boolean var15 = this.isMultiSelectDown() || this.isShiftDown();
      boolean var16 = var9 >= var5 && var9 <= var6 && var8 >= var4 + 4.0F && var8 <= var4 + 4.0F + 227.0F && var9 >= var14 && var9 <= var14 + 23.0F;
      float var17 = var11.appear.method2808(1.0F);
      float var18 = var11.hover.method2808(var16 ? 1.0F : 0.0F);
      float var19 = var7 * var17;
      float var20 = var14 + (1.0F - var17) * 6.0F;
      float var21 = var4 + 4.0F;
      var1.drawRoundedRect(var21, var20, 227.0F, 23.0F, headerRadius, listColor.method7294(var19));
      float var22 = var11.select.method2808(this.selectedNames.contains(var2) ? 1.0F : 0.0F);
      var1.drawRoundedRect(var21, var20, 23.0F, 23.0F, headerRadius, listColor.method7294(var19));
      float var23 = var21 + 227.0F - 64.0F;
      var1.drawRoundedRect(var23, var20, 64.0F, 23.0F, headerRadius, listColor.method7294(var19));
      if (var13) {
         boolean var24 = !var15 && var8 >= var23 && var8 <= var23 + 64.0F && var9 >= var20 && var9 <= var20 + 23.0F && var9 >= var5 && var9 <= var6;
         float var25 = var11.disc.method2808(var24 ? 1.0F : 0.0F);
         float var26 = 23.0F;
         var1.enableScissor(var21, var20, var21 + 227.0F, var20 + 23.0F);
         if (var25 < 0.999F) {
            String var27 = var12.getAddress();
            String var28 = serverLabel(var27);
            Identifier var29 = ServerIconCache.get(var27);
            float var30 = nickFont.width(var28);
            float var31 = var30 + (var29 != null ? 11.0F : 0.0F);
            float var32 = var23 + (64.0F - var31) / 2.0F;
            float var33 = var20 - var25 * var26;
            float var34 = var19 * (1.0F - var25);
            if (var29 != null) {
               float var35 = var33 + 8.0F;
               RenderShaders.method5294(
                  MatrixUtils.method664(var1.getMatrices()), var29, var32, var35, 7.0F, 7.0F, serverIconRadius, ZenithColor.INSTANCE.method7294(var34)
               );
               var32 += 11.0F;
            }

            var1.drawText(nickFont, var28, var32, centeredTextY(nickFont, var33, 23.0F), ZenithColor.INSTANCE.method7294(var34));
         }

         if (var25 > 0.001F) {
            String var42 = tr("module.bot.disconnect");
            float var48 = connectIconFont.width("]");
            float var53 = nickFont.width(var42);
            float var56 = 2.0F;
            float var57 = var23 + (64.0F - var48 - var56 - var53) / 2.0F;
            float var58 = var20 + (1.0F - var25) * var26;
            float var59 = var19 * var25;
            var1.drawText(connectIconFont, "]", var57, centeredTextY(connectIconFont, var58, 23.0F), disconnectColor.method7294(var59));
            var1.drawText(nickFont, var42, var57 + var48 + var56, centeredTextY(nickFont, var58, 23.0F), disconnectColor.method7294(var59));
         }

         var1.disableScissor();
      } else {
         String var36 = "Offline";
         float var38 = connectIconFont.width("]");
         float var40 = nickFont.width(var36);
         float var43 = 2.0F;
         float var49 = var23 + (64.0F - var38 - var43 - var40) / 2.0F;
         var1.drawText(connectIconFont, "]", var49, centeredTextY(connectIconFont, var20, 23.0F), inputEmptyColor.method7294(var19));
         var1.drawText(nickFont, var36, var49 + var38 + var43, centeredTextY(nickFont, var20, 23.0F), inputEmptyColor.method7294(var19));
      }

      boolean var37 = !var15 && var8 >= var21 && var8 <= var21 + 23.0F && var9 >= var20 && var9 <= var20 + 23.0F && var9 >= var5 && var9 <= var6;
      float var39 = var11.delete.method2808(var37 ? 1.0F : 0.0F);
      Identifier var41 = BotAvatarCache.prewarm(var2);
      if (var41 != null) {
         float var44 = var21 + 4.0F;
         float var50 = var20 + 4.0F;
         ZenithColor var54 = var39 > 0.0F ? ZenithColor.INSTANCE.method7295(deleteDarkColor, 0.62F * var39) : ZenithColor.INSTANCE;
         RenderShaders.method5294(MatrixUtils.method664(var1.getMatrices()), var41, var44, var50, 15.0F, 15.0F, avatarRadius, var54.method7294(var19));
      }

      if (var39 > 0.001F) {
         float var45 = deleteIconFont.width("2");
         var1.drawText(
            deleteIconFont,
            "2",
            var21 + (23.0F - var45) / 2.0F - 0.5F,
            centeredTextY(deleteIconFont, var20, 23.0F) + 0.5F,
            deleteColor.method7294(var19 * var39)
         );
      }

      if (var39 < 0.5F && HeadlessBots.hasProxy(var2)) {
         float var46 = var21 + 23.0F - 4.0F - 4.0F;
         float var51 = var20 + 4.0F;
         var1.drawRoundedRect(var46, var51, 4.0F, 4.0F, proxyDotRadius, var10.getPrimaryColor().getZenithColor().method7294(var19 * (1.0F - var39 * 2.0F)));
      }

      float var47 = var21 + 23.0F + 6.0F + var18 * 2.0F;
      float var52 = centeredTextY(nickFont, var20, 23.0F);
      ZenithColor var55 = var22 > 0.001F ? ZenithColor.INSTANCE.method7295(var10.getPrimaryColor().getZenithColor(), var22) : ZenithColor.INSTANCE;
      var1.drawText(nickFont, var2, var47, var52, var55.method7294(var19));
      this.renderRowModuleBadges(var1, var2, var47 + nickFont.width(var2) + 6.0F, var23 - 5.0F, var20, var19, var10);
   }

   private void renderRowModuleBadges(MenuDrawContext var1, String var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      float var8 = var3;
      float var9 = var5 + 6.5F;
      int var10 = 0;

      for (String var12 : BotModuleManager.supportedModuleNames()) {
         if (this.isBotModuleEnabled(var2, var12)) {
            String var13 = this.shortModuleName(var12);
            float var14 = moduleMenuFont.width(var13) + 7.0F;
            if (var8 + var14 > var4 || var10 >= 2) {
               break;
            }

            var1.drawRoundedRect(var8, var9, var14, 10.0F, roleBadgeRadius, var7.getPrimaryColor().getZenithColor().method7293(34).method7294(var6));
            var1.drawText(
               moduleMenuFont, var13, var8 + 3.5F, centeredTextY(moduleMenuFont, var9, 10.0F), var7.getPrimaryColor().getZenithColor().method7294(var6)
            );
            var8 += var14 + 3.0F;
            var10++;
         }
      }
   }

   private void renderModuleMenu(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      String var6 = this.moduleMenuBotName();
      List<String> var7 = BotModuleManager.supportedModuleNames();
      if (var6 != null && !var7.isEmpty()) {
         float var8 = this.moduleMenuAnimation.method2808(1.0F);
         float var9 = var2 * clamp01(var8);
         float var10 = this.moduleMenuY + (1.0F - var8) * 6.0F;
         float var11 = 26.0F + var7.size() * 20.0F + Math.max(0, var7.size() - 1) * 3.0F;
         RenderShaders.method5299(
            MatrixUtils.method664(var1.getMatrices()), this.moduleMenuX, var10 + 5.0F, 118.0F, var11, 18.0F, moduleMenuRadius, shadowColor.method7294(var9)
         );
         RenderShaders.method5287(
            MatrixUtils.method664(var1.getMatrices()), this.moduleMenuX, var10, 118.0F, var11, 16.0F, moduleMenuRadius, blurColor.method7294(var9), true, false
         );
         var1.drawRoundedRect(this.moduleMenuX, var10, 118.0F, var11, moduleMenuRadius, var5.getRightBackground().getZenithColor().method7294(var9));
         float var12 = var10 + 4.0F;
         var1.drawText(
            moduleMenuFont,
            fitText(moduleMenuFont, var6, 110.0F),
            this.moduleMenuX + 4.0F,
            centeredTextY(moduleMenuFont, var12, 18.0F),
            var5.getTextEnable().getZenithColor().method7294(var9)
         );
         float var13 = var12 + 18.0F;

         for (Object var15_o : (Iterable<?>)(Object)(var7)) {
            String var15 = (String)var15_o;
            boolean var16 = this.isBotModuleEnabled(var6, var15);
            boolean var17 = var3 >= this.moduleMenuX + 4.0F && var3 <= this.moduleMenuX + 118.0F - 4.0F && var4 >= var13 && var4 <= var13 + 20.0F;
            ZenithColor var18 = (var16 ? var5.getPrimaryColor().getZenithColor().method7293(42) : var5.getSurfaceDisableBackground().getZenithColor())
               .method7295(var5.getPrimaryColor().getZenithColor(), var17 ? 0.12F : 0.0F);
            var1.drawRoundedRect(this.moduleMenuX + 4.0F, var13, 110.0F, 20.0F, moduleMenuRowRadius, var18.method7294(var9));
            ZenithColor var19 = var16 ? var5.getPrimaryColor().getZenithColor() : var5.getTextSecondary().getZenithColor();
            var1.drawText(
               moduleMenuFont,
               fitText(moduleMenuFont, var15, 85.0F),
               this.moduleMenuX + 4.0F + 6.0F,
               centeredTextY(moduleMenuFont, var13, 20.0F),
               var19.method7294(var9)
            );
            float var20 = 6.0F;
            float var21 = this.moduleMenuX + 118.0F - 4.0F - 10.0F;
            float var22 = var13 + (20.0F - var20) / 2.0F;
            var1.drawRoundedRect(
               var21,
               var22,
               var20,
               var20,
               ProxyDotRadiusData.method8069(var20 / 2.0F),
               (var16 ? var5.getPrimaryColor().getZenithColor() : var5.getDisableActiveBg().getZenithColor()).method7294(var9)
            );
            var13 += 23.0F;
         }
      } else {
         this.moduleMenuBotName = null;
      }
   }

   private boolean isBotModuleEnabled(String var1, String var2) {
      BotClient var3 = HeadlessBots.get(var1);
      if (var3 != null) {
         BotModule var4 = var3.getModules().getModule(var2);
         if (var4 != null) {
            return var4.isEnabled();
         }
      }

      return HeadlessBots.isModuleEnabledInProfile(var1, var2);
   }

   private String shortModuleName(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.replace("BotAuto", "A").replace("Auto", "A").replace("Bot", "");
         return var2.length() <= 6 ? var2 : var2.substring(0, 6);
      } else {
         return "";
      }
   }

   private String moduleMenuBotName() {
      if (this.moduleMenuBotName == null) {
         return null;
      } else {
         for (String var2 : HeadlessBots.allNames()) {
            if (var2.equalsIgnoreCase(this.moduleMenuBotName)) {
               return var2;
            }
         }

         return null;
      }
   }

   private void openModuleMenu(String var1, float var2, float var3) {
      if (var1 == null) {
         this.closeModuleMenu();
      } else {
         this.versionMenuOpen = false;
         this.moduleMenuAnimation.method2809(0.0F);
         this.moduleMenuBotName = var1;
         this.moduleMenuX = var2 + 227.0F + 3.0F;
         this.moduleMenuY = var3;
      }
   }

   private void closeModuleMenu() {
      this.moduleMenuBotName = null;
   }

   private boolean handleModuleMenuClick(double var1, double var3) {
      String var5 = this.moduleMenuBotName();
      List<String> var6 = BotModuleManager.supportedModuleNames();
      if (var5 != null && !var6.isEmpty()) {
         float var7 = 26.0F + var6.size() * 20.0F + Math.max(0, var6.size() - 1) * 3.0F;
         boolean var8 = var1 >= this.moduleMenuX && var1 <= this.moduleMenuX + 118.0F && var3 >= this.moduleMenuY && var3 <= this.moduleMenuY + var7;
         if (!var8) {
            this.closeModuleMenu();
            return false;
         } else {
            float var9 = this.moduleMenuY + 4.0F + 18.0F;

            for (Object var11_o : (Iterable<?>)(Object)(var6)) {
               String var11 = (String)var11_o;
               if (var1 >= this.moduleMenuX + 4.0F && var1 <= this.moduleMenuX + 118.0F - 4.0F && var3 >= var9 && var3 <= var9 + 20.0F) {
                  boolean var12 = this.isBotModuleEnabled(var5, var11);
                  HeadlessBots.setModuleEnabled(var5, var11, !var12);
                  return true;
               }

               var9 += 23.0F;
            }

            return true;
         }
      } else {
         this.closeModuleMenu();
         return false;
      }
   }

   private int selectedProtocolVersion() {
      return this.selectedName == null ? -1 : HeadlessBots.getProtocolVersion(this.selectedName);
   }

   private void applyVersionToSelection(int var1) {
      LinkedHashSet<String> var2 = new LinkedHashSet<>(this.selectedNames);
      if (this.selectedName != null) {
         var2.add(this.selectedName);
      }

      for (Object var4_o : (Iterable<?>)(Object)(var2)) {
         String var4 = (String)var4_o;
         HeadlessBots.setProtocolVersion(var4, var1);
      }
   }

   private float versionChipLeftX(float var1) {
      return var1 + 319.0F + 157.0F - this.connectButtonWidth - 4.0F - 34.0F;
   }

   private float versionMenuX(float var1) {
      return Math.min(this.versionChipLeftX(var1), var1 + 480.0F - 4.0F - 72.0F);
   }

   private float versionMenuY(float var1) {
      return var1 + 4.0F + 23.0F + 3.0F;
   }

   private float versionMenuViewportHeight() {
      int var1 = Math.min(9, BotProtocolVersions.ENTRIES.size());
      return var1 * 14.0F + Math.max(0, var1 - 1) * 2.0F;
   }

   private float versionMenuMaxScroll() {
      float var1 = 16.0F;
      float var2 = BotProtocolVersions.ENTRIES.size() * var1 - 2.0F;
      return Math.max(0.0F, var2 - this.versionMenuViewportHeight());
   }

   private void renderVersionMenu(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      if (this.versionMenuOpen) {
         if (this.selectedName == null) {
            this.versionMenuOpen = false;
         } else {
            float var8 = this.versionMenuX(var2);
            float var9 = this.versionMenuAnimation.method2808(1.0F);
            float var10 = var4 * clamp01(var9);
            float var11 = this.versionMenuY(var3) + (1.0F - var9) * 6.0F;
            float var12 = this.versionMenuViewportHeight();
            float var13 = 8.0F + var12;
            RenderShaders.method5299(
               MatrixUtils.method664(var1.getMatrices()), var8, var11 + 5.0F, 72.0F, var13, 18.0F, moduleMenuRadius, shadowColor.method7294(var10)
            );
            RenderShaders.method5287(
               MatrixUtils.method664(var1.getMatrices()), var8, var11, 72.0F, var13, 16.0F, moduleMenuRadius, blurColor.method7294(var10), true, false
            );
            var1.drawRoundedRect(var8, var11, 72.0F, var13, moduleMenuRadius, var7.getRightBackground().getZenithColor().method7294(var10));
            this.versionMenuScroll = Math.max(0.0F, Math.min(this.versionMenuScroll, this.versionMenuMaxScroll()));
            float var14 = 16.0F;
            float var15 = var11 + 4.0F;
            int var16 = this.selectedProtocolVersion();
            var1.enableScissor(var8, var15, var8 + 72.0F, var15 + var12);
            float var17 = var15 - this.versionMenuScroll;

            for (BotProtocolVersions.Entry var19 : BotProtocolVersions.ENTRIES) {
               if (var17 + 14.0F >= var15 && var17 <= var15 + var12) {
                  boolean var20 = var19.protocol() == var16;
                  boolean var21 = var5 >= var8 + 4.0F
                     && var5 <= var8 + 72.0F - 4.0F
                     && var6 >= Math.max(var17, var15)
                     && var6 <= Math.min(var17 + 14.0F, var15 + var12);
                  ZenithColor var22 = (var20 ? var7.getPrimaryColor().getZenithColor().method7293(42) : var7.getSurfaceDisableBackground().getZenithColor())
                     .method7295(var7.getPrimaryColor().getZenithColor(), var21 ? 0.12F : 0.0F);
                  var1.drawRoundedRect(var8 + 4.0F, var17, 64.0F, 14.0F, moduleMenuRowRadius, var22.method7294(var10));
                  ZenithColor var23 = var20 ? var7.getPrimaryColor().getZenithColor() : var7.getTextSecondary().getZenithColor();
                  var1.drawText(versionFont, var19.label(), var8 + 4.0F + 5.0F, centeredTextY(versionFont, var17, 14.0F), var23.method7294(var10));
               }

               var17 += var14;
            }

            var1.disableScissor();
         }
      }
   }

   private boolean handleVersionMenuClick(double var1, double var3, float var5, float var6) {
      if (!this.versionMenuOpen) {
         return false;
      } else {
         float var7 = this.versionMenuX(var5);
         float var8 = this.versionMenuY(var6);
         float var9 = this.versionMenuViewportHeight();
         float var10 = 8.0F + var9;
         boolean var11 = var1 >= var7 && var1 <= var7 + 72.0F && var3 >= var8 && var3 <= var8 + var10;
         if (!var11) {
            this.versionMenuOpen = false;
            return false;
         } else {
            float var12 = 16.0F;
            float var13 = (float)var3 - (var8 + 4.0F) + this.versionMenuScroll;
            int var14 = (int)Math.floor(var13 / var12);
            var var15 = BotProtocolVersions.ENTRIES;
            if (var14 >= 0 && var14 < var15.size() && var13 - var14 * var12 <= 14.0F) {
               this.applyVersionToSelection(((BotProtocolVersions.Entry)var15.get(var14)).protocol());
               this.versionMenuOpen = false;
            }

            return true;
         }
      }
   }

   private void renderChatMessages(MenuDrawContext var1, BotClient var2, float var3, float var4, float var5, ZenithStyle var6) {
      if (var2 != null) {
         ChatMessage[] var7 = var2.getChatLog().toArray(new ChatMessage[0]);
         if (var2 != this.chatAnimClient) {
            this.chatAnimClient = var2;
            this.chatAnims.clear();
            this.wrapCache.clear();

            for (ChatMessage var11 : var7) {
               this.chatAnims.put(var11, new Animation(260L, 1.0F, Easing.INSTANCE15));
            }
         }

         var var25 = Collections.newSetFromMap(new IdentityHashMap(var7.length * 2));
         Collections.addAll(var25, var7);
         var var26 = this.chatAnims.keySet().iterator();

         while (var26.hasNext()) {
            ChatMessage var27 = (ChatMessage)var26.next();
            if (!var25.contains(var27)) {
               var26.remove();
               this.wrapCache.remove(var27);
            }
         }

         if (var7.length > 0) {
            float var28 = chatLineStepCache;
            float var29 = chatTimeTopOffsetCache;
            float var12 = var3 + 8.0F + chatTimeColumnWidthCache + 8.0F;
            float var13 = var3 + 232.0F - 8.0F - var12;
            float[] var14 = new float[var7.length];
            List[] var15 = new List[var7.length];
            float var16 = 99.5F;
            float var17 = 0.0F;

            for (int var18 = 0; var18 < var7.length; var18++) {
               ChatMessage var19 = var7[var18];
               Animation var20 = this.chatAnims.computeIfAbsent(var19, var0 -> new Animation(260L, 0.0F, Easing.INSTANCE15));
               float var21 = var20.method2808(1.0F);
               var var22 = this.wrapCache.get(var19);
               if (var22 == null) {
                  var22 = wrapText(var19.text(), nickFont, var13);
                  this.wrapCache.put(var19, var22);
               }

               var14[var18] = var21;
               var15[var18] = var22;

               for (int var23 = 0; var23 < var22.size(); var23++) {
                  var17 += var28 * var21;
               }
            }

            float var30 = Math.max(0.0F, var17 - var16);
            this.chatScroll = Math.max(0.0F, Math.min(this.chatScroll, var30));
            float var31 = var4 + 107.5F - 4.0F - var17 + this.chatScroll;
            var1.enableScissor(var3, var4, var3 + 232.0F, var4 + 107.5F);

            for (int var32 = 0; var32 < var7.length; var32++) {
               float var33 = var14[var32];
               var var34 = var15[var32];

               for (int var35 = 0; var35 < var34.size(); var35++) {
                  if (var31 + var28 > var4 && var31 < var4 + 107.5F) {
                     float var24 = var5 * var33;
                     if (var35 == 0 && var7[var32].time() != null) {
                        var1.drawText(timeFont, var7[var32].time(), var3 + 8.0F, var31 + var29, var6.getTextTertiary().getZenithColor().method7294(var24));
                     }

                     var1.drawText(nickFont, (Text)var34.get(var35), var12, var31, var6.getTextEnable().getZenithColor().method7294(var24).method7287());
                  }

                  var31 += var28 * var33;
               }
            }

            var1.disableScissor();
         }
      }
   }

   private static String tr(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private static float centeredTextY(Font var0, float var1, float var2) {
      FontData.MetricsData var3 = var0.getFont().getMetrics();
      float var4 = (var3.ascender() - var3.descender()) * var0.getSize();
      return var1 + (var2 - var4) / 2.0F;
   }

   private static float clamp01(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static float lineHeight(Font var0) {
      FontData.MetricsData var1 = var0.getFont().getMetrics();
      return (var1.ascender() - var1.descender()) * var0.getSize();
   }

   private static String fitText(Font var0, String var1, float var2) {
      if (var1 != null && !var1.isEmpty()) {
         BotScreen.FitKey var3 = new BotScreen.FitKey(var0, var1, var2);
         String var4 = FIT_CACHE.get(var3);
         if (var4 == null) {
            var4 = fitTextUncached(var0, var1, var2);
            FIT_CACHE.put(var3, var4);
         }

         return var4;
      } else {
         return var1 == null ? "" : var1;
      }
   }

   private static String fitTextUncached(Font var0, String var1, float var2) {
      if (var0.width(var1) <= var2) {
         return var1;
      } else {
         String var3 = "...";
         int var4 = var1.length();

         while (var4 > 0 && var0.width(var1.substring(0, var4) + var3) > var2) {
            var4--;
         }

         return var4 <= 0 ? var3 : var1.substring(0, var4) + var3;
      }
   }

   private static float listContentHeight(int var0) {
      return var0 <= 0 ? 0.0F : var0 * 25.0F - 2.0F;
   }

   private static float listMaxScroll(int var0) {
      return Math.max(0.0F, listContentHeight(var0) - 211.0F);
   }

   private List<String> filterBots(List<String> var1) {
      String var2 = this.searchInput.getText().trim();
      if (var2.isEmpty()) {
         return var1;
      } else {
         String var3 = var2.toLowerCase(Locale.ROOT);
         ArrayList<String> var4 = new ArrayList<>();

         for (String var6 : var1) {
            if (var6.toLowerCase(Locale.ROOT).contains(var3)) {
               var4.add(var6);
            }
         }

         return var4;
      }
   }

   private List<String> visibleBots() {
      return this.filterBots(HeadlessBots.allNames());
   }

   private BotClient selectedClient() {
      return this.selectedName == null ? null : HeadlessBots.get(this.selectedName);
   }

   private static String serverLabel(String var0) {
      if (var0 != null && !"No Server".equalsIgnoreCase(var0)) {
         String var1 = serverLabelCache.get(var0);
         if (var1 != null) {
            return var1;
         } else {
            String var2 = computeServerLabel(var0);
            serverLabelCache.put(var0, var2);
            return var2;
         }
      } else {
         return "";
      }
   }

   private static String computeServerLabel(String var0) {
      int var1 = var0.indexOf(58);
      String var2 = var1 > 0 ? var0.substring(0, var1) : var0;
      String[] var3 = var2.split("\\.");
      if (var3.length < 2) {
         return var2;
      } else {
         for (String var7 : var3) {
            if (!var7.chars().allMatch(Character::isDigit)) {
               return var3[var3.length - 2];
            }
         }

         return var2;
      }
   }

   public boolean isSearch() {
      return this.addInput.isSelected()
         || this.searchInput.isSelected()
         || this.proxyManagerInput.isSelected()
         || this.chatInput.isSelected()
         || this.connectInput.isSelected();
   }

   private static List<Text> wrapText(Text var0, Font var1, float var2) {
      ArrayList<Text> var3 = new ArrayList<>();
      ArrayList var4 = new ArrayList();
      float var5 = 0.0F;
      int var6 = -1;

      for (BotScreen.StyledTextFragment var8 : splitTextToGlyphs(var0)) {
         if (var8.isLineBreak()) {
            addWrappedLine(var3, var4);
            var4.clear();
            var5 = 0.0F;
            var6 = -1;
         } else if (!var4.isEmpty() || !var8.isWhitespace()) {
            var4.add(var8);
            var5 += var8.width(var1);
            if (var8.isWhitespace()) {
               var6 = var4.size() - 1;
            }

            if (var2 > 0.0F && var5 > var2 && var4.size() > 1) {
               if (var6 > 0) {
                  addWrappedLine(var3, var4.subList(0, var6));
                  var4 = new ArrayList(var4.subList(var6 + 1, var4.size()));
                  trimLeadingWhitespace(var4);
               } else {
                  BotScreen.StyledTextFragment var9 = (BotScreen.StyledTextFragment)var4.remove(var4.size() - 1);
                  addWrappedLine(var3, var4);
                  var4 = new ArrayList();
                  if (!var9.isWhitespace()) {
                     var4.add(var9);
                  }
               }

               var5 = textWidth(var4, var1);
               var6 = lastWhitespace(var4);
            }
         }
      }

      addWrappedLine(var3, var4);
      return var3;
   }

   private static List<BotScreen.StyledTextFragment> splitTextToGlyphs(Text var0) {
      ArrayList<BotScreen.StyledTextFragment> var1 = new ArrayList<>();
      var0.visit((var1x, var2) -> {
         int var3 = 0;

         while (var3 < var2.length()) {
            int var4 = var2.codePointAt(var3);
            int var5 = var3 + Character.charCount(var4);
            var1.add(new BotScreen.StyledTextFragment(var2.substring(var3, var5), var1x));
            var3 = var5;
         }

         return Optional.empty();
      }, Style.EMPTY);
      return var1;
   }

   private static void addWrappedLine(List<Text> var0, List<BotScreen.StyledTextFragment> var1) {
      var var2 = new ArrayList<>(var1);
      trimTrailingWhitespace(var2);
      if (!var2.isEmpty()) {
         var0.add(toText(var2));
      }
   }

   private static Text toText(List<BotScreen.StyledTextFragment> var0) {
      MutableText var1 = Text.empty();

      for (BotScreen.StyledTextFragment var3 : var0) {
         var1.append(Text.literal(var3.value()).setStyle(var3.style()));
      }

      return var1;
   }

   private static float textWidth(List<BotScreen.StyledTextFragment> var0, Font var1) {
      float var2 = 0.0F;

      for (BotScreen.StyledTextFragment var4 : var0) {
         var2 += var4.width(var1);
      }

      return var2;
   }

   private static int lastWhitespace(List<BotScreen.StyledTextFragment> var0) {
      for (int var1 = var0.size() - 1; var1 >= 0; var1--) {
         if (((BotScreen.StyledTextFragment)var0.get(var1)).isWhitespace()) {
            return var1;
         }
      }

      return -1;
   }

   private static void trimLeadingWhitespace(List<BotScreen.StyledTextFragment> var0) {
      while (!var0.isEmpty() && ((BotScreen.StyledTextFragment)var0.get(0)).isWhitespace()) {
         var0.remove(0);
      }
   }

   private static void trimTrailingWhitespace(List<BotScreen.StyledTextFragment> var0) {
      while (!var0.isEmpty() && ((BotScreen.StyledTextFragment)var0.get(var0.size() - 1)).isWhitespace()) {
         var0.remove(var0.size() - 1);
      }
   }

   private void sendChatMessage() {
      String var1 = this.chatInput.getText();
      BotClient var2 = this.selectedClient();
      if (var2 != null && !var1.isBlank()) {
         var2.sendChat(var1);
      }

      this.chatInput.setText("");
      this.chatInput.setCursor(0);
   }

   private static boolean isValidBotName(String var0) {
      int var1 = var0 == null ? 0 : var0.trim().length();
      return var1 >= 3 && var1 <= 16;
   }

   private void addBotFromSearch() {
      String var1 = this.addInput.getText().trim();
      if (isValidBotName(var1) && HeadlessBots.add(var1)) {
         this.addInput.setText("");
         this.addInput.setCursor(0);
         this.selectOnly(var1);
         this.listScroll = 0.0F;
      }
   }

   private List<String> selectionNames() {
      var var1 = new ArrayList<>(this.selectedNames);
      if (var1.isEmpty() && this.selectedName != null) {
         var1.add(this.selectedName);
      }

      return var1;
   }

   private void applyProxyToSelection(String var1) {
      String var2 = var1 == null ? "" : var1.trim();
      if (!var2.isBlank()) {
         HeadlessBots.addProxy(var2);
      }

      for (String var4 : this.selectionNames()) {
         HeadlessBots.setProxy(var4, var2);
      }

      this.proxyManagerInput.setSelected(false);
   }

   private void addProxyFromManager() {
      String var1 = this.proxyManagerInput.getText().trim();
      if (!var1.isBlank()) {
         HeadlessBots.addProxy(var1);
         this.proxyManagerInput.setText("");
         this.proxyManagerInput.setCursor(0);
         this.proxyPanelScroll = 0.0F;
      }
   }

   private boolean handleProxyPanelClick(double var1, double var3, float var5, float var6) {
      float var7 = proxyPanelButtonX(var5);
      float var8 = proxyPanelButtonY(var6);
      if (var1 >= var7 && var1 <= var7 + 18.0F && var3 >= var8 && var3 <= var8 + 18.0F) {
         this.proxyPanelOpen = !this.proxyPanelOpen;
         savedProxyPanelOpen = this.proxyPanelOpen;
         this.proxyManagerInput.setSelected(false);
         return true;
      } else {
         float var9 = this.proxyPanelAnimation.getValue();
         if (!this.proxyPanelOpen && var9 <= 0.001F) {
            return false;
         } else {
            float var10 = proxyPanelRenderX(var5, Math.max(var9, this.proxyPanelOpen ? 1.0F : 0.0F));
            float var11 = proxyPanelY(var6);
            if (!(var1 < var10) && !(var1 > var10 + 156.0F) && !(var3 < var11) && !(var3 > var11 + 222.0F)) {
               float var12 = proxyPanelCloseHitX(var10);
               float var13 = proxyPanelCloseHitY(var11);
               if (var1 >= var12 && var1 <= var12 + 14.0F && var3 >= var13 && var3 <= var13 + 14.0F) {
                  this.proxyPanelOpen = false;
                  savedProxyPanelOpen = false;
                  this.proxyManagerInput.setSelected(false);
                  return true;
               } else {
                  this.handleProxyPanelBodyClick(var1, var3, var10, var11);
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   private void handleProxyPanelBodyClick(double var1, double var3, float var5, float var6) {
      float var7 = var6 + 31.0F;
      float var8 = var5 + 4.0F;
      float var9 = 126.0F;
      float var10 = var8 + var9 + 4.0F;
      if (!(var3 >= var7) || !(var3 <= var7 + 18.0F)) {
         float var11 = var6 + 53.0F;
         float var12 = proxyPanelRowWidth();
         if (!(var1 < var8) && !(var1 > var8 + var12) && !(var3 < var11) && !(var3 > var11 + 161.0F)) {
            float var13 = (float)(var3 - var11 + this.proxyPanelScroll);
            float var14 = 25.0F;
            int var15 = (int)(var13 / var14);
            if (var15 >= 0) {
               if (var13 - var15 * var14 > 23.0F) {
                  this.proxyManagerInput.setSelected(false);
               } else if (var15 == 0) {
                  this.applyProxyToSelection("");
               } else {
                  var var16 = HeadlessBots.getProxyPool();
                  int var17 = var15 - 1;
                  if (var17 < var16.size()) {
                     String var18 = (String)var16.get(var17);
                     float var19 = var8 + var12 - 18.0F;
                     if (var1 >= var19 && var1 <= var19 + 18.0F) {
                        if (HeadlessBots.removeProxy(var18)) {
                           this.proxyHoverAnims.remove(var18);
                           this.proxyPanelScroll = Math.min(this.proxyPanelScroll, proxyPanelMaxScroll(HeadlessBots.getProxyPool().size() + 1));
                        }

                        this.proxyManagerInput.setSelected(false);
                     } else {
                        this.applyProxyToSelection(var18);
                     }
                  }
               }
            }
         } else {
            this.proxyManagerInput.setSelected(false);
         }
      } else if (var1 >= var10 && var1 <= var10 + 18.0F) {
         this.addProxyFromManager();
         this.proxyManagerInput.setSelected(false);
      } else if (var1 >= var8 && var1 <= var8 + var9) {
         this.focusTextBox(this.proxyManagerInput, var1, var3);
         this.clearMainInputFocus();
      } else {
         this.proxyManagerInput.setSelected(false);
      }
   }

   private void clearMainInputFocus() {
      this.chatInput.setSelected(false);
      this.connectInput.setSelected(false);
      this.addInput.setSelected(false);
      this.searchInput.setSelected(false);
      this.bulkActionInput.setSelected(false);
   }

   private boolean isMultiSelectDown() {
      Window var1 = mc.getWindow();
      return InputUtil.isKeyPressed(var1, 341) || InputUtil.isKeyPressed(var1, 345) || InputUtil.isKeyPressed(var1, 343) || InputUtil.isKeyPressed(var1, 347);
   }

   private boolean isShiftDown() {
      Window var1 = mc.getWindow();
      return InputUtil.isKeyPressed(var1, 340) || InputUtil.isKeyPressed(var1, 344);
   }

   private void selectOnly(String var1) {
      this.selectedNames.clear();
      if (var1 != null) {
         this.selectedNames.add(var1);
      }

      this.selectedName = var1;
   }

   private void toggleSelection(String var1) {
      if (this.selectedNames.contains(var1)) {
         this.selectedNames.remove(var1);
         if (this.selectedName != null && this.selectedName.equals(var1)) {
            this.selectedName = null;
            this.chatScroll = 0.0F;
            var var2 = this.selectedNames.iterator();
            if (var2.hasNext()) {
               String var3 = (String)var2.next();
               this.selectedName = var3;
            }
         }
      } else {
         this.selectedNames.add(var1);
         this.selectedName = var1;
         this.chatScroll = 0.0F;
      }
   }

   private void rangeSelect(List<String> var1, int var2) {
      int var3 = -1;
      if (this.selectedName != null) {
         for (int var4 = 0; var4 < var1.size(); var4++) {
            if (((String)var1.get(var4)).equalsIgnoreCase(this.selectedName)) {
               var3 = var4;
               break;
            }
         }
      }

      if (var3 < 0) {
         this.selectOnly((String)var1.get(var2));
         this.chatScroll = 0.0F;
      } else {
         this.selectedNames.clear();

         for (int var5 = Math.min(var3, var2); var5 <= Math.max(var3, var2); var5++) {
            this.selectedNames.add((String)var1.get(var5));
         }
      }
   }

   private void selectAllVisible() {
      var var1 = this.visibleBots();
      if (!var1.isEmpty()) {
         this.selectedNames.clear();
         this.selectedNames.addAll(var1);
         if (this.selectedName == null || !this.selectedNames.contains(this.selectedName)) {
            this.selectedName = (String)var1.get(0);
            this.chatScroll = 0.0F;
         }
      }
   }

   private void refreshSelection(List<String> var1) {
      if (this.selectedName != null) {
         for (String var3 : var1) {
            if (var3.equalsIgnoreCase(this.selectedName)) {
               if (!var3.equals(this.selectedName)) {
                  this.selectedName = var3;
               }

               return;
            }
         }

         this.selectedName = null;
      }

      for (String var7 : this.selectedNames) {
         for (String var5 : var1) {
            if (var5.equalsIgnoreCase(var7)) {
               this.selectedName = var5;
               return;
            }
         }
      }
   }

   private boolean anyInputSelected() {
      return this.chatInput.isSelected()
         || this.connectInput.isSelected()
         || this.addInput.isSelected()
         || this.searchInput.isSelected()
         || this.bulkActionInput.isSelected()
         || this.proxyManagerInput.isSelected();
   }

   private List<String> offlineSelectedNames() {
      ArrayList<String> var1 = new ArrayList<>();

      for (String var3 : this.selectedNames) {
         if (!HeadlessBots.isOnline(var3)) {
            var1.add(var3);
         }
      }

      if (var1.isEmpty() && this.selectedName != null && !HeadlessBots.isOnline(this.selectedName)) {
         var1.add(this.selectedName);
      }

      return var1;
   }

   private void doConnect() {
      String var1 = this.connectInput.getText().trim();
      if (var1.isBlank()) {
         ServerInfo var2 = mc.getCurrentServerEntry();
         if (var2 != null && var2.address != null) {
            var1 = var2.address.trim();
         }
      }

      if (var1.isBlank()) {
         var1 = HeadlessBots.getLastAddress();
      }

      if (!var1.isBlank()) {
         ServerAddress var6 = ServerAddress.parse(var1);
         String var3 = var6.getAddress();
         int var4 = var6.getPort();
         var var5 = this.offlineSelectedNames();
         if (!var5.isEmpty()) {
            if (var5.size() == 1) {
               HeadlessBots.connect((String)var5.get(0), var3, var4);
            } else {
               HeadlessBots.connectAll(var5, var3, var4);
            }

            this.connectInput.setText("");
            this.connectInput.setCursor(0);
            this.connectInput.setSelected(false);
         }
      }
   }

   private void executeBulkAction() {
      switch (this.bulkAction) {
         case CONNECT:
            this.doBulkConnect();
            break;
         case CHAT:
            this.doBulkChat();
            break;
         case RCT:
            this.doBulkRct();
      }
   }

   private void doBulkRct() {
      int var1 = this.parseRctAnarchy();
      if (var1 >= 0) {
         for (BotClient var3 : HeadlessBots.all()) {
            if (var3.isJoined()) {
               var3.getRct().reconnect(var1);
            }
         }

         this.bulkActionInput.setSelected(false);
      }
   }

   private int parseRctAnarchy() {
      String var1 = this.bulkActionInput.getText().trim();
      if (var1.isEmpty()) {
         return -1;
      } else {
         try {
            int var2 = Integer.parseInt(var1);
            return var2 >= 1 && var2 <= 66 ? var2 : -1;
         } catch (NumberFormatException var3) {
            return -1;
         }
      }
   }

   private void doBulkConnect() {
      String var1 = this.bulkActionInput.getText().trim();
      if (var1.isBlank()) {
         ServerInfo var2 = mc.getCurrentServerEntry();
         if (var2 != null && var2.address != null) {
            var1 = var2.address.trim();
         }
      }

      if (var1.isBlank()) {
         var1 = HeadlessBots.getLastAddress();
      }

      if (!var1.isBlank()) {
         var var4 = this.offlineBotNames();
         if (!var4.isEmpty()) {
            ServerAddress var3 = ServerAddress.parse(var1);
            HeadlessBots.connectAll(var4, var3.getAddress(), var3.getPort());
            this.bulkActionInput.setText("");
            this.bulkActionInput.setCursor(0);
            this.bulkActionInput.setSelected(false);
         }
      }
   }

   private void doBulkChat() {
      String var1 = this.bulkActionInput.getText();
      if (!var1.isBlank()) {
         for (BotClient var3 : HeadlessBots.all()) {
            if (var3.isJoined()) {
               var3.sendChat(var1);
            }
         }

         this.bulkActionInput.setText("");
         this.bulkActionInput.setCursor(0);
         this.bulkActionInput.setSelected(false);
      }
   }

   private List<String> offlineBotNames() {
      ArrayList<String> var1 = new ArrayList<>();

      for (String var3 : HeadlessBots.allNames()) {
         if (!HeadlessBots.isOnline(var3)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   @Override
   public void onMouseClicked(double var1, double var3, Enum3 var5) {
      if (!this.closing && this.screenAnimation.isDone()) {
         if (var5 == Enum3.RIGHT) {
            float var6 = (mc.getWindow().getScaledWidth() - 480.0F) / 2.0F;
            float var7 = (mc.getWindow().getScaledHeight() - 320.0F) / 2.0F;
            var var8 = this.visibleBots();
            float var9 = var7 + 58.0F;
            float var10 = var9 + 211.0F;

            for (int var11 = 0; var11 < var8.size(); var11++) {
               float var12 = var9 + var11 * 25.0F - this.listScroll;
               if (var3 >= var9 && var3 <= var10 && var1 >= var6 + 4.0F && var1 <= var6 + 4.0F + 227.0F && var3 >= var12 && var3 <= var12 + 23.0F) {
                  this.openModuleMenu((String)var8.get(var11), var6 + 4.0F, var12);
                  return;
               }
            }

            this.closeModuleMenu();
         }

         if (var5 == Enum3.LEFT) {
            float var36 = (mc.getWindow().getScaledWidth() - 480.0F) / 2.0F;
            float var37 = (mc.getWindow().getScaledHeight() - 320.0F) / 2.0F;
            if (this.handleProxyPanelClick(var1, var3, var36, var37)) {
               return;
            }

            this.proxyManagerInput.setSelected(false);
            if (this.handleModuleMenuClick(var1, var3)) {
               return;
            }

            if (this.handleVersionMenuClick(var1, var3, var36, var37)) {
               return;
            }

            float var38 = var37 + 31.0F;
            boolean var39 = var3 >= var38 && var3 <= var38 + 23.0F;
            float var40 = var36 + 4.0F;
            float var41 = var40 + 114.0F - 23.0F;
            boolean var42 = var39 && var1 >= var40 && var1 <= var40 + 114.0F;
            if (var42 && var1 >= var41) {
               this.bulkActionInput.setSelected(false);
               this.addBotFromSearch();
               return;
            }

            boolean var13 = var42 && var1 < var41;
            if (var13) {
               this.focusTextBox(this.addInput, var1, var3);
               this.chatInput.setSelected(false);
               this.connectInput.setSelected(false);
               this.searchInput.setSelected(false);
               this.bulkActionInput.setSelected(false);
               return;
            }

            this.addInput.setSelected(false);
            float var14 = var40 + 114.0F + 4.0F;
            boolean var15 = var39 && var1 >= var14 && var1 <= var14 + 114.0F;
            if (var15) {
               this.focusTextBox(this.searchInput, var1, var3);
               this.chatInput.setSelected(false);
               this.connectInput.setSelected(false);
               this.addInput.setSelected(false);
               this.bulkActionInput.setSelected(false);
               return;
            }

            this.searchInput.setSelected(false);
            float var16 = var37 + 275.0F;
            boolean var17 = var3 >= var16 && var3 <= var16 + 14.0F;
            float var18 = bulkActionModeStartX(var36);
            float var19 = var36 + 4.0F + 232.0F;
            if (var17 && var1 >= var18 && var1 <= var19) {
               for (BotScreen.BulkAction var23 : BotScreen.BulkAction.values()) {
                  if (var1 >= var18 && var1 <= var18 + 14.0F) {
                     if (this.bulkAction != var23) {
                        this.bulkActionInput.setText("");
                        this.bulkActionInput.setCursor(0);
                     }

                     this.bulkAction = var23;
                     this.syncBulkActionInputRules();
                     this.clearMainInputFocus();
                     this.bulkActionInput.setSelected(true);
                     this.bulkActionInput.setFlag(false);
                     return;
                  }

                  var18 += 17.0F;
               }
            }

            float var43 = var37 + 292.0F;
            boolean var44 = var3 >= var43 && var3 <= var43 + 23.0F;
            if (var44 && var1 >= var36 + 4.0F && var1 <= var36 + 4.0F + 232.0F) {
               float var45 = var36 + 4.0F;
               float var47 = 164.0F;
               float var24 = var45 + var47 + 4.0F;
               if (var1 >= var24) {
                  this.executeBulkAction();
                  return;
               }

               boolean var25 = var1 < var45 + var47;
               if (var25) {
                  this.clearMainInputFocus();
                  this.focusTextBox(this.bulkActionInput, var1, var3);
               } else {
                  this.bulkActionInput.setSelected(false);
               }

               if (var25) {
                  return;
               }
            }

            if (var44 && var1 >= var36 + 4.0F && var1 <= var36 + 4.0F + 232.0F) {
               this.chatInput.setSelected(false);
               this.connectInput.setSelected(false);
               this.addInput.setSelected(false);
               this.searchInput.setSelected(false);
               this.bulkActionInput.setSelected(false);
               return;
            }

            this.bulkActionInput.setSelected(false);
            var var46 = this.visibleBots();
            float var48 = var37 + 58.0F;
            float var49 = var48 + 211.0F;

            for (int var50 = 0; var50 < var46.size(); var50++) {
               float var26 = var48 + var50 * 25.0F - this.listScroll;
               if (var3 >= var48 && var3 <= var49 && var1 >= var36 + 4.0F && var1 <= var36 + 4.0F + 227.0F && var3 >= var26 && var3 <= var26 + 23.0F) {
                  String var27 = (String)var46.get(var50);
                  if (this.isMultiSelectDown()) {
                     this.toggleSelection(var27);
                     return;
                  }

                  if (this.isShiftDown()) {
                     this.rangeSelect(var46, var50);
                     return;
                  }

                  if (var1 <= var36 + 4.0F + 23.0F) {
                     HeadlessBots.remove(var27);
                     this.rowAnims.remove(var27);
                     this.selectedNames.remove(var27);
                     if (var27.equals(this.selectedName)) {
                        this.chatScroll = 0.0F;
                        this.selectedName = null;
                        var var28 = this.selectedNames.iterator();
                        if (var28.hasNext()) {
                           String var29 = (String)var28.next();
                           this.selectedName = var29;
                        }

                        if (this.selectedName == null && this.selectedNames.isEmpty()) {
                           var var54 = HeadlessBots.allNames();
                           if (!var54.isEmpty()) {
                              this.selectOnly((String)var54.get(0));
                           }
                        }
                     }

                     return;
                  }

                  if (HeadlessBots.isOnline(var27) && var1 >= var36 + 4.0F + 227.0F - 64.0F) {
                     HeadlessBots.disconnect(var27);
                     return;
                  }

                  this.selectOnly(var27);
                  this.chatScroll = 0.0F;
                  return;
               }
            }

            float var51 = var36 + 319.0F;
            float var52 = var37 + 4.0F;
            boolean var53 = var1 >= var51 && var1 <= var51 + 157.0F && var3 >= var52 && var3 <= var52 + 23.0F;
            float var55 = var51 + 157.0F - this.connectButtonWidth;
            if (var53 && var1 >= var55) {
               BotClient var57 = this.selectedClient();
               if (var57 != null && var57.isJoined()) {
                  mc.setScreen(new BotControlScreen(var57.getName()));
               } else {
                  this.doConnect();
               }

               return;
            }

            float var56 = var55 - 4.0F - 34.0F;
            if (var53 && var1 >= var56) {
               this.versionMenuOpen = !this.versionMenuOpen;
               if (this.versionMenuOpen) {
                  this.versionMenuAnimation.method2809(0.0F);
               }

               this.versionMenuScroll = 0.0F;
               this.clearMainInputFocus();
               return;
            }

            boolean var30 = var53 && var1 < var56;
            if (var30) {
               this.focusTextBox(this.connectInput, var1, var3);
               this.chatInput.setSelected(false);
               return;
            }

            this.connectInput.setSelected(false);
            BotClient var31 = this.selectedClient();
            if (var31 != null && var31.isJoined()) {
               float var32 = var36 + 244.0F;
               float var33 = var37 + 39.0F + 130.5F + 4.0F + 107.5F + 4.0F;
               float var34 = var32 + 232.0F - 48.0F;
               if (var1 >= var34 && var1 <= var34 + 48.0F && var3 >= var33 && var3 <= var33 + 23.0F) {
                  this.sendChatMessage();
                  return;
               }

               boolean var35 = var1 >= var32 && var1 <= var32 + 232.0F && var3 >= var33 && var3 <= var33 + 23.0F;
               if (var35) {
                  this.focusTextBox(this.chatInput, var1, var3);
                  return;
               }

               this.chatInput.setSelected(false);
            }
         }

         super.onMouseClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.closing && this.screenAnimation.isDone()) {
         if (this.proxyManagerInput.isSelected()) {
            if (var1 == 257 || var1 == 335) {
               this.addProxyFromManager();
               return true;
            }

            if (var1 == 256) {
               this.proxyManagerInput.setSelected(false);
               return true;
            }

            if (this.proxyManagerInput.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         if (this.chatInput.isSelected()) {
            if (var1 == 257 || var1 == 335) {
               this.sendChatMessage();
               return true;
            }

            if (this.chatInput.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         if (this.connectInput.isSelected()) {
            if (var1 == 257 || var1 == 335) {
               this.doConnect();
               return true;
            }

            if (this.connectInput.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         if (this.addInput.isSelected()) {
            if (var1 == 257 || var1 == 335) {
               this.addBotFromSearch();
               return true;
            }

            if (this.addInput.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         if (this.searchInput.isSelected() && this.searchInput.keyPressed(var1, var2, var3)) {
            return true;
         } else {
            if (this.bulkActionInput.isSelected()) {
               if (var1 == 257 || var1 == 335) {
                  this.executeBulkAction();
                  return true;
               }

               if (var1 == 256) {
                  this.bulkActionInput.setSelected(false);
                  return true;
               }

               if (this.bulkActionInput.keyPressed(var1, var2, var3)) {
                  return true;
               }
            }

            if (var1 == 65 && (var3 & 10) != 0 && !this.anyInputSelected()) {
               this.selectAllVisible();
               return true;
            } else {
               return super.keyPressed(new KeyInput(var1, var2, var3));
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(CharInput input) {
      return this.charTyped((char)input.codepoint(), input.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      if (this.closing || !this.screenAnimation.isDone()) {
         return false;
      } else if (this.chatInput.charTyped(var1, var2)) {
         return true;
      } else if (this.connectInput.charTyped(var1, var2)) {
         return true;
      } else if (this.addInput.charTyped(var1, var2)) {
         return true;
      } else if (this.searchInput.charTyped(var1, var2)) {
         return true;
      } else if (this.bulkActionInput.charTyped(var1, var2)) {
         return true;
      } else {
         return this.proxyManagerInput.charTyped(var1, var2) ? true : super.charTyped(new CharInput(var1, var2));
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.closing && this.screenAnimation.isDone()) {
         float var9 = (mc.getWindow().getScaledWidth() - 480.0F) / 2.0F;
         float var10 = (mc.getWindow().getScaledHeight() - 320.0F) / 2.0F;
         if (this.versionMenuOpen) {
            float var11 = this.versionMenuX(var9);
            float var12 = this.versionMenuY(var10);
            float var13 = 8.0F + this.versionMenuViewportHeight();
            if (mouseX >= var11 && mouseX <= var11 + 72.0F && mouseY >= var12 && mouseY <= var12 + var13) {
               float var23 = 16.0F;
               this.versionMenuScroll = Math.max(0.0F, Math.min(this.versionMenuMaxScroll(), this.versionMenuScroll - (float)verticalAmount * var23));
               return true;
            }
         }

         float var17 = this.proxyPanelAnimation.getValue();
         if (this.proxyPanelOpen || var17 > 0.001F) {
            float var18 = proxyPanelRenderX(var9, Math.max(var17, this.proxyPanelOpen ? 1.0F : 0.0F));
            float var20 = proxyPanelY(var10);
            float var14 = var20 + 53.0F;
            if (mouseX >= var18 + 4.0F && mouseX <= var18 + 4.0F + proxyPanelListWidth() && mouseY >= var14 && mouseY <= var14 + 161.0F) {
               int var24 = HeadlessBots.getProxyPool().size() + 1;
               float var25 = proxyPanelMaxScroll(var24);
               this.proxyPanelScroll = Math.max(0.0F, Math.min(var25, this.proxyPanelScroll - (float)verticalAmount * 25.0F));
               return true;
            }
         }

         float var19 = var9 + 244.0F;
         float var21 = var10 + 39.0F + 130.5F + 4.0F;
         if (mouseX >= var19 && mouseX <= var19 + 232.0F && mouseY >= var21 && mouseY <= var21 + 107.5F) {
            this.chatScroll = Math.max(0.0F, this.chatScroll + (float)verticalAmount * 14.0F);
            return true;
         } else {
            float var22 = var10 + 58.0F;
            float var15 = var22 + 211.0F;
            if (mouseX >= var9 + 4.0F && mouseX <= var9 + 4.0F + 227.0F && mouseY >= var22 && mouseY <= var15) {
               float var16 = listMaxScroll(this.visibleBots().size());
               this.listScroll = Math.max(0.0F, Math.min(var16, this.listScroll - (float)verticalAmount * 25.0F));
               return true;
            } else {
               return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   public String getSelectedBotName() {
      return this.selectedName;
   }

   @Override
   public void close() {
      if (!this.closing) {
         this.screenAnimation.setDuration(220L);
         this.screenAnimation.setEasing(Easing.INSTANCE16);
         this.closing = true;
      }
   }

   private static enum BulkAction {
      CONNECT("Z", "module.bot.connect", "module.bot.connect", "module.bot.connectTo"),
      CHAT("d", "module.bot.chat", "module.bot.send", "module.bot.message"),
      RCT("7", "module.bot.rct", "module.bot.rct", "module.bot.anarchy");

      public final String icon;
      public final String tabKey;
      public final String buttonKey;
      public final String placeholderKey;

      private BulkAction(String var3, String var4, String var5, String var6) {
         this.icon = var3;
         this.tabKey = var4;
         this.buttonKey = var5;
         this.placeholderKey = var6;
      }
   }

   private static final class FitKey {
      private final Font font;
      private final String text;
      private final float maxWidth;

      public Font font() {
         return this.font;
      }

      public String text() {
         return this.text;
      }

      public float maxWidth() {
         return this.maxWidth;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof FitKey other)) return false;
         return java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.maxWidth, other.maxWidth);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(font, text, maxWidth);
      }

      @Override
      public String toString() {
         return "FitKey[" + "font=" + this.font + ", " + "text=" + this.text + ", " + "maxWidth=" + this.maxWidth + "]";
      }

      private FitKey(Font var1, String var2, float var3) {
         this.font = var1;
         this.text = var2;
         this.maxWidth = var3;
      }
   }

   private static final class RowAnim {
      public final Animation appear = new Animation(220L, 0.0F, Easing.INSTANCE15);
      public final Animation hover = new Animation(150L, 0.0F, Easing.INSTANCE15);
      public final Animation delete = new Animation(160L, 0.0F, Easing.INSTANCE15);
      public final Animation disc = new Animation(220L, 0.0F, Easing.INSTANCE15);
      public final Animation select = new Animation(160L, 0.0F, Easing.INSTANCE15);
   }

   private static final class StyledTextFragment {
      private final String value;
      private final Style style;

      public String value() {
         return this.value;
      }

      public Style style() {
         return this.style;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof StyledTextFragment other)) return false;
         return java.util.Objects.equals(this.value, other.value) && java.util.Objects.equals(this.style, other.style);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(value, style);
      }

      @Override
      public String toString() {
         return "StyledTextFragment[" + "value=" + this.value + ", " + "style=" + this.style + "]";
      }

      private StyledTextFragment(String var1, Style var2) {
         this.value = var1;
         this.style = var2;
      }

      private Text asText() {
         return Text.literal(this.value).setStyle(this.style);
      }

      private float width(Font var1) {
         return var1.width(this.asText());
      }

      private boolean isLineBreak() {
         return "\n".equals(this.value) || "\r".equals(this.value);
      }

      private boolean isWhitespace() {
         return !this.value.isEmpty() && !this.isLineBreak() ? Character.isWhitespace(this.value.codePointAt(0)) : false;
      }
   }
}

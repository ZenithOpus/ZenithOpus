package org.zenith.client.screens.nlgui.panel.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.ChatLogger;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.bot.BotAvatarCache;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.BotModuleManager;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.SvgIcon;
import org.zenith.client.screens.bot.BotControlScreen;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class BotsToolPanel extends ToolTab {
   private static final float MAIN_WIDTH = 208.0F;
   private static final float ASIDE_WIDTH = 172.0F;
   private static final float INPUT_HEIGHT = 18.0F;
   private static final float SERVER_BAR_HEIGHT = 48.0F;
   private static final float CHIP_HEIGHT = 16.0F;
   private static final float CARD_HEIGHT = 21.0F;
   private static final float AVATAR = 13.0F;
   private static final float POWER = 14.0F;
   private static final float PREVIEW_HEIGHT = 98.0F;
   private static final float PREVIEW_ROUND = 6.0F;
   private static final float ACTION_HEIGHT = 26.0F;
   private static final float VIEW_BUTTON_WIDTH = 30.0F;
   private static final float VITALS_HEIGHT = 22.0F;
   private static final float DOT = 4.0F;
   private static final float EMPTY_LIST_HEIGHT = 202.0F;
   private static final int DEFAULT_PORT = 25565;
   private SearchBox nameInput;
   private SearchBox addressInput;
   private String selected;
   private String contentSignature = "";
   private BotWorldView previewView;
   private BotClient previewClient;
   private String previewOwner;
   private BoundsData previewRect;
   private float previewAlpha;
   private boolean previewHovered;
   private boolean previewHasFrame;
   private final Map<String, BoundsData> powerBounds = new LinkedHashMap<>();
   private BoundsData connectAllBounds;
   private BoundsData disconnectAllBounds;
   private BoundsData asideViewBounds;
   private double lastClickX;
   private double lastClickY;
   private final Map<String, BotsToolPanel.CardAnimations> cardAnimations = new LinkedHashMap<>();

   @Override
   public boolean pressFeedback() {
      return false;
   }

   @Override
   public String headerTitle() {
      return "Bots";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.BOTS;
   }

   @Override
   public String sectionDescription() {
      return "gui.tools.bots.desc";
   }

   @Override
   public String emptyText() {
      return "gui.tools.bots.empty";
   }

   @Override
   public float mainWidth() {
      return 208.0F;
   }

   @Override
   public float asideWidth() {
      return 172.0F;
   }

   public boolean isLive() {
      for (BotClient var2 : HeadlessBots.all()) {
         if (var2.getPhase() != BotPhase.DISCONNECTED) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void build() {
      List<String> var1 = HeadlessBots.allNames();
      if (this.selected != null && var1.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(this.selected))) {
         this.selected = null;
      }

      if (this.selected == null && !var1.isEmpty()) {
         this.selected = (String)var1.getFirst();
      }

      this.pruneCardAnimations(var1);
      this.buildServerBar();
      String var2 = this.searchQuery();
      ArrayList<String> var3 = new ArrayList();

      for (Object var5_o : (Iterable<?>)(Object)(var1)) {
         String var5 = (String)var5_o;
         if (var2.isEmpty() || var5.toLowerCase(Locale.ROOT).contains(var2)) {
            var3.add(var5);
         }
      }

      this.buildListCaption(var1.size());
      if (var3.isEmpty()) {
         boolean var6 = !var1.isEmpty();
         if (var6) {
            this.custom(
               44.0F,
               (var1x, var2x, var3x, var4, var5x, var6x, var7, var8x) -> this.drawHint(
                  var1x, var4, var5x + 8.0F, var6x, t("gui.tools.bots.notFound"), var7, var8x
               )
            );
         } else {
            this.custom(202.0F, this::renderEmptyList);
         }
      } else {
         for (Object var8_o : (Iterable<?>)(Object)(var3)) {
            String var8 = (String)var8_o;
            this.buildBotCard(var8);
         }
      }

      this.gap(GuiStyle.PADDING.intValue());
      this.buildAside();
   }

   private void pruneCardAnimations(List<String> var1) {
      HashSet var2 = new HashSet();

      for (String var4 : var1) {
         var2.add(var4.toLowerCase(Locale.ROOT));
      }

      this.cardAnimations.keySet().retainAll(var2);
   }

   private BotsToolPanel.CardAnimations animations(String var1) {
      return this.cardAnimations.computeIfAbsent(var1.toLowerCase(Locale.ROOT), var0 -> new BotsToolPanel.CardAnimations());
   }

   private void buildServerBar() {
      this.custom(
         48.0F,
         (var1, var2, var3, var4, var5, var6, var7, var8) -> {
            if (this.addressInput == null) {
               this.addressInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.tools.bots.address.hint"), 64.0F);
               this.addressInput.setText(HeadlessBots.getLastAddress());
               this.addressInput.setCursor(this.addressInput.getText().length());
            }

            float var9 = var5 + 3.0F;
            this.drawInput(var1, this.addressInput, MenuGlyph.NETWORK, var4, var9, var6, var7, var8);
            float var10 = var9 + 18.0F + 5.0F;
            float var11 = var6 - 16.0F;
            float var12 = (var11 - GuiStyle.PADDING.intValue()) / 2.0F;
            float var13 = var4 + 8.0F;
            float var14 = var13 + var12 + GuiStyle.PADDING.intValue();
            int var15 = HeadlessBots.allNames().size();
            int var16 = this.onlineCount();
            this.connectAllBounds = new BoundsData(var13, var10, var12, 16.0F);
            this.disconnectAllBounds = new BoundsData(var14, var10, var12, 16.0F);
            this.drawChip(
               var1,
               var2,
               var3,
               this.connectAllBounds,
               t("gui.tools.bots.connectAll"),
               String.valueOf(var15),
               var8.getPrimaryColor().getZenithColor(),
               var15 > 0,
               var7,
               var8,
               true
            );
            this.drawChip(
               var1,
               var2,
               var3,
               this.disconnectAllBounds,
               t("gui.tools.bots.disconnectAll"),
               String.valueOf(var16),
               var8.getHeartIcon().getZenithColor(),
               var16 > 0,
               var7,
               var8,
               false
            );
            if (this.insideMain(var2, var3)) {
               if (this.connectAllBounds.contains(var2, var3)) {
                  HoverInfo.request(t("gui.tools.bots.connectAll"), t("gui.tools.bots.connectAll.desc"));
               } else if (this.disconnectAllBounds.contains(var2, var3)) {
                  HoverInfo.request(t("gui.tools.bots.disconnectAll"), t("gui.tools.bots.disconnectAll.desc"));
               } else if (new BoundsData(var4, var9, var6, 18.0F).contains(var2, var3)) {
                  HoverInfo.request(t("gui.tools.bots.caption.server"), t("gui.tools.bots.address.desc"));
               }
            }
         }
      );
   }

   private void drawChip(
      MenuDrawContext var1,
      int var2,
      int var3,
      BoundsData var4,
      String var5,
      String var6,
      ZenithColor var7,
      boolean var8,
      float var9,
      ZenithStyle var10,
      boolean var11
   ) {
      boolean var12 = var8 && var4.contains(var2, var3) && this.insideMain(var2, var3);
      ZenithColor var13 = var8 ? var7 : var10.getTextTertiary().getZenithColor();
      var1.drawRoundedRect(
         var4.getFloatValue(),
         var4.getFloatValue2(),
         var4.width(),
         var4.height(),
         ProxyDotRadiusData.method8069(var4.height() / 2.0F),
         var13.method7293(var12 ? 46 : 24).method7294(var9)
      );
      Font var14 = Fonts.NEW_MEDIUM.getFont(4.8F);
      float var15 = var4.getFloatValue() + 7.0F;
      float var16 = var4.getFloatValue2() + var4.height() / 2.0F;
      ZenithColor var17 = var13.method7293(var8 ? (var12 ? 255 : 220) : 130).method7294(var9);
      if (var11) {
         drawPlay(var1, var15, var16 - 3.0F, var17);
      } else {
         drawStop(var1, var15, var16 - 2.5F, var17);
      }

      float var18 = var15 + 8.0F;
      float var19 = var14.width(var6);
      float var20 = var4.getFloatValue() + var4.width() - 7.0F - var19 - 4.0F - var18;
      var1.drawText(var14, fit(var14, var5, var20), var18, var16 - var14.height() / 2.0F, var17);
      var1.drawText(var14, var6, var4.getFloatValue() + var4.width() - 7.0F - var19, var16 - var14.height() / 2.0F, var17.method7294(0.7F));
   }

   private void buildListCaption(int var1) {
      this.custom(
         16.0F,
         (var2, var3, var4, var5, var6, var7, var8, var9) -> {
            Font var10 = Fonts.NEW_MEDIUM.getFont(4.0F);
            float var11 = var6 + (16.0F - var10.height()) / 2.0F + 2.0F;
            var2.drawText(
               var10,
               HoverInfo.spaced(t("gui.tools.bots.caption.list").toUpperCase(Locale.ROOT)),
               var5 + 8.0F,
               var11,
               var9.getTextTertiary().getZenithColor().method7294(var8)
            );
            int var12 = this.onlineCount();
            String var13 = var12 + " / " + var1 + " " + t("gui.tools.bots.online");
            var2.drawText(
               var10,
               var13,
               var5 + var7 - 8.0F - var10.width(var13),
               var11,
               (var12 > 0 ? var9.getFriendColor() : var9.getTextTertiary()).getZenithColor().method7294(var8)
            );
         }
      );
   }

   private void buildBotCard(String var1) {
      this.custom(21.0F, (var2, var3, var4, var5, var6, var7, var8, var9) -> this.renderBotCard(var2, var1, var3, var4, var5, var6, var7, var8, var9), () -> {
         this.selected = var1;
         this.resetAsideScroll();
         this.invalidate();
      }, null, () -> {
         this.toggleConnection(var1);
         this.invalidate();
      });
   }

   private void renderBotCard(MenuDrawContext var1, String var2, int var3, int var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      BotsToolPanel.CardAnimations var10 = this.animations(var2);
      boolean var11 = this.insideMain(var3, var4);
      BoundsData var12 = new BoundsData(var5 + 2.0F, var6 + 1.0F, var7 - 4.0F, 19.0F);
      BoundsData var13 = new BoundsData(var5 + var7 - 8.0F - 14.0F, var6 + 3.5F, 14.0F, 14.0F);
      this.powerBounds.put(var2, var13);
      boolean var14 = var11 && var13.contains(var3, var4);
      boolean var15 = var11 && var12.contains(var3, var4) && !var14;
      boolean var16 = var2.equalsIgnoreCase(this.selected);
      boolean var17 = this.isOnline(var2);
      float var18 = var10.hover.method2808(var15 ? 1.0F : 0.0F);
      float var19 = var10.select.method2808(var16 ? 1.0F : 0.0F);
      float var20 = var10.power.method2808(var14 ? 1.0F : 0.0F);
      float var21 = Math.max(var19, var18 * 0.5F);
      if (var21 > 0.002F) {
         var1.drawRoundedRect(
            var12.getFloatValue(),
            var12.getFloatValue2(),
            var12.width(),
            var12.height(),
            ProxyDotRadiusData.method8069(5.0F),
            var9.getDisableActiveBg().getZenithColor().method7294(var8 * var21)
         );
      }

      if (var19 > 0.002F) {
         float var22 = var12.height() * 0.62F * var19;
         var1.drawRoundedRect(
            var12.getFloatValue(),
            var12.getFloatValue2() + (var12.height() - var22) / 2.0F,
            1.5F,
            var22,
            ProxyDotRadiusData.method8069(0.75F),
            var9.getPrimaryColor().getZenithColor().method7294(var8)
         );
      }

      if (var15) {
         HoverInfo.request(var2, t("gui.tools.bots.row.desc"));
      }

      float var35 = var5 + 8.0F;
      float var23 = var6 + 4.0F;
      Identifier var24 = BotAvatarCache.prewarm(var2);
      if (var24 != null) {
         RenderShaders.method5294(
            MatrixUtils.method664(var1.getMatrices()),
            var24,
            var35,
            var23,
            13.0F,
            13.0F,
            ProxyDotRadiusData.method8069(3.5F),
            ZenithColor.INSTANCE.method7294(var8)
         );
      } else {
         var1.drawRoundedRect(
            var35, var23, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.5F), var9.getFieldSurfaceBackground().getZenithColor().method7294(var8 * 4.0F)
         );
      }

      ZenithColor var25 = this.phaseColor(var2, var9);
      float var26 = var35 + 13.0F - 4.0F + 1.0F;
      float var27 = var23 + 13.0F - 4.0F + 1.0F;
      var1.drawRoundedRect(
         var26 - 1.0F,
         var27 - 1.0F,
         6.0F,
         6.0F,
         ProxyDotRadiusData.method8069(3.0F),
         var9.getRightBackground().getZenithColor().method7293(255).method7294(var8)
      );
      var1.drawRoundedRect(var26, var27, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), var25.method7294(var8));
      String var28 = this.phaseLabel(var2);
      Font var29 = Fonts.NEW_MEDIUM.getFont(4.4F);
      float var30 = var29.width(var28) + 9.0F;
      float var31 = var13.getFloatValue() - 5.0F - var30;
      this.drawPill(var1, var31, var6 + 6.0F, var30, var28, var25, var8);
      Font var32 = Fonts.NEW_MEDIUM.getFont(5.2F);
      ZenithColor var33 = var9.getTextSecondary()
         .getZenithColor()
         .method7295(var9.getTextEnable().getZenithColor(), Math.max(var19, var18 * 0.6F))
         .method7294(var8);
      float var34 = var35 + 13.0F + 6.0F;
      var1.drawText(var32, fit(var32, var2, var31 - 5.0F - var34), var34, var6 + (21.0F - var32.height()) / 2.0F, var33);
      this.drawPowerButton(var1, var13, var17, var20, var8, var9);
      if (var14) {
         HoverInfo.request(var2, t(var17 ? "gui.tools.bots.disconnect.desc" : "gui.tools.bots.connect.desc"));
      }
   }

   private void drawPowerButton(MenuDrawContext var1, BoundsData var2, boolean var3, float var4, float var5, ZenithStyle var6) {
      ZenithColor var7 = var3 ? var6.getHeartIcon().getZenithColor() : var6.getPrimaryColor().getZenithColor();
      var1.drawRoundedRect(
         var2.getFloatValue(),
         var2.getFloatValue2(),
         var2.width(),
         var2.height(),
         ProxyDotRadiusData.method8069(4.0F),
         var7.method7293((int)(18.0F + 34.0F * var4)).method7294(var5)
      );
      ZenithColor var8 = var7.method7293((int)(170.0F + 85.0F * var4)).method7294(var5);
      if (var3) {
         drawStop(var1, var2.getFloatValue() + (var2.width() - 5.0F) / 2.0F, var2.getFloatValue2() + (var2.height() - 5.0F) / 2.0F, var8);
      } else {
         drawPlay(var1, var2.getFloatValue() + (var2.width() - 3.0F) / 2.0F + 0.5F, var2.getFloatValue2() + (var2.height() - 6.0F) / 2.0F, var8);
      }
   }

   private void buildAside() {
      String var1 = this.selected;
      this.previewOwner = var1;
      if (var1 == null) {
         this.asideCustom(
            98.0F, (var1x, var2, var3x, var4x, var5x, var6, var7, var8) -> this.renderPreviewFrame(var1x, null, var2, var3x, var4x, var5x, var6, var7, var8)
         );
         this.asideCustom(32.0F, (var0, var1x, var2, var3x, var4x, var5x, var6, var7) -> {
            float var8 = var5x - 16.0F;
            float var9 = var8 - 30.0F - GuiStyle.PADDING.intValue();
            ToolTab.skeleton(var0, var7, var3x + 8.0F, var4x + 3.0F, var9, 26.0F, 0, var6);
            ToolTab.skeleton(var0, var7, var3x + 8.0F + var9 + GuiStyle.PADDING.intValue(), var4x + 3.0F, 30.0F, 26.0F, 1, var6);
         });
         this.asideCaption("gui.tools.bots.caption.state");
         this.asideCustom(22.0F, (var0, var1x, var2, var3x, var4x, var5x, var6, var7) -> {
            float var8 = var5x - 16.0F;
            float var9 = (var8 - 6.0F) / 2.0F;
            ToolTab.skeleton(var0, var7, var3x + 8.0F, var4x + 6.0F, var9, 4.0F, 2, var6);
            ToolTab.skeleton(var0, var7, var3x + 8.0F + var9 + 6.0F, var4x + 6.0F, var9, 4.0F, 3, var6);
            ToolTab.skeleton(var0, var7, var3x + 8.0F, var4x + 14.0F, var8 * 0.4F, 3.0F, 4, var6 * 0.7F);
         });
         this.asideCaption("gui.tools.bots.caption.modules");

         for (int var4 = 0; var4 < 4; var4++) {
            int var5 = var4;
            this.asideCustom(16.0F, (var1x, var2, var3x, var4x, var5x, var6, var7, var8) -> {
               float var9 = var6 - 16.0F;
               ToolTab.skeleton(var1x, var8, var4x + 8.0F, var5x + 6.0F, var9 * (0.35F + 0.12F * (var5 % 3)), 4.0F, 5 + var5, var7 * 0.8F);
               ToolTab.skeleton(var1x, var8, var4x + var6 - 8.0F - 12.0F, var5x + 5.0F, 12.0F, 6.0F, 6 + var5, var7 * 0.8F);
            });
         }
      } else {
         this.asideCustom(
            98.0F,
            (var2, var3x, var4x, var5x, var6, var7, var8, var9) -> this.renderPreviewFrame(var2, var1, var3x, var4x, var5x, var6, var7, var8, var9),
            () -> this.openPreview(var1),
            null
         );
         this.buildActionRow(var1);
         this.asideCaption("gui.tools.bots.caption.state");
         this.asideCustom(
            22.0F, (var2, var3x, var4x, var5x, var6, var7, var8, var9) -> this.renderVitals(var2, var1, var3x, var4x, var5x, var6, var7, var8, var9)
         );
         this.asideInfo("gui.tools.bots.pos", () -> {
            BotClient var1x = HeadlessBots.get(var1);
            BotPlayer var2 = var1x == null ? null : var1x.getPlayer();
            return var2 == null ? "—" : (int)var2.getX() + " " + (int)var2.getY() + " " + (int)var2.getZ();
         }, "gui.tools.bots.pos.desc");
         this.asideCaption("gui.tools.bots.caption.modules");

         for (String var3 : BotModuleManager.supportedModuleNames()) {
            this.buildModuleRow(var1, var3);
         }

         this.asideCaption("gui.tools.bots.caption.network");
         this.asideInfo("gui.tools.bots.proxy", () -> {
            String var1x = HeadlessBots.getProxy(var1);
            return var1x != null && !var1x.isBlank() ? var1x : t("gui.tools.bots.proxy.none");
         }, "gui.tools.bots.proxy.desc");
         this.asideInfo("gui.tools.bots.protocol", () -> {
            int var1x = HeadlessBots.getProtocolVersion(var1);
            return var1x <= 0 ? t("gui.tools.bots.protocol.native") : String.valueOf(var1x);
         }, "gui.tools.bots.protocol.desc");
         this.asideAction("gui.tools.bots.remove", MenuGlyph.CLOSE, () -> "", () -> {
            HeadlessBots.disconnect(var1);
            HeadlessBots.remove(var1);
            this.selected = null;
            this.invalidate();
         }, "gui.tools.bots.remove.desc");
      }
   }

   private void buildActionRow(String var1) {
      this.asideCustom(
         32.0F,
         (var2, var3, var4, var5, var6, var7, var8, var9) -> {
            float var10 = var6 + 3.0F;
            float var11 = var7 - 16.0F;
            float var12 = var11 - 30.0F - GuiStyle.PADDING.intValue();
            BoundsData var13 = new BoundsData(var5 + 8.0F, var10, var12, 26.0F);
            BoundsData var14 = new BoundsData(var5 + 8.0F + var12 + GuiStyle.PADDING.intValue(), var10, 30.0F, 26.0F);
            this.asideViewBounds = var14;
            boolean var15 = this.insideAside(var3, var4);
            boolean var16 = this.isOnline(var1);
            boolean var17 = var15 && var13.contains(var3, var4);
            boolean var18 = var15 && var14.contains(var3, var4);
            ZenithColor var19 = var16 ? var9.getHeartIcon().getZenithColor() : var9.getPrimaryColor().getZenithColor();
            var2.drawRoundedRect(
               var13.getFloatValue(),
               var13.getFloatValue2(),
               var13.width(),
               var13.height(),
               ProxyDotRadiusData.method8069(6.0F),
               var19.method7293(var17 ? 62 : 34).method7294(var8)
            );
            Font var20 = Fonts.NEW_MEDIUM.getFont(5.4F);
            String var21 = t(var16 ? "gui.tools.bots.disconnect" : "gui.tools.bots.connect");
            float var22 = var20.width(var21);
            float var23 = var13.getFloatValue() + (var13.width() - var22 - 9.0F) / 2.0F;
            ZenithColor var24 = var19.method7293(var17 ? 255 : 225).method7294(var8);
            if (var16) {
               drawStop(var2, var23, var13.getFloatValue2() + (var13.height() - 5.0F) / 2.0F, var24);
            } else {
               drawPlay(var2, var23 + 1.0F, var13.getFloatValue2() + (var13.height() - 6.0F) / 2.0F, var24);
            }

            var2.drawText(var20, var21, var23 + 9.0F, var13.getFloatValue2() + (var13.height() - var20.height()) / 2.0F, var24);
            var2.drawRoundedRect(
               var14.getFloatValue(),
               var14.getFloatValue2(),
               var14.width(),
               var14.height(),
               ProxyDotRadiusData.method8069(6.0F),
               var9.getDisableActiveBg().getZenithColor().method7294(var8 * (var18 ? 1.7F : 1.0F))
            );
            MenuGlyph.WORLD
               .draw(
                  var2,
                  var14.getFloatValue() + (var14.width() - 6.0F) / 2.0F,
                  var14.getFloatValue2() + (var14.height() - 6.0F) / 2.0F,
                  (var18 ? var9.getTextEnable() : var9.getTextSecondary()).getZenithColor().method7294(var8)
               );
            if (var17) {
               HoverInfo.request(var21, t(var16 ? "gui.tools.bots.disconnect.desc" : "gui.tools.bots.connect.desc"));
            } else if (var18) {
               HoverInfo.request(t("gui.tools.bots.view"), t("gui.tools.bots.view.desc"));
            }
         },
         () -> {
            if (this.asideViewBounds != null && this.asideViewBounds.contains(this.lastClickX, this.lastClickY)) {
               mc.setScreen(new BotControlScreen(var1));
            } else {
               this.toggleConnection(var1);
               this.invalidate();
            }
         },
         null
      );
   }

   private void buildModuleRow(String var1, String var2) {
      String var3 = displayModuleName(var2);
      this.asideCustom(16.0F, (var4, var5, var6, var7, var8, var9, var10, var11) -> {
         boolean var12 = this.isModuleEnabled(var1, var2);
         boolean var13 = this.insideAside(var5, var6) && new BoundsData(var7, var8, var9, 16.0F).contains(var5, var6);
         if (var13) {
            var4.drawRect(var7, var8, var9, 16.0F, var11.getDisableActiveBg().getZenithColor().method7294(var10 * 0.45F));
            HoverInfo.request(var3, this.moduleDescription(var2));
         }

         Font var14 = Fonts.NEW_MEDIUM.getFont(5.2F);
         ZenithColor var15 = (var12 ? var11.getTextEnable() : var11.getTextSecondary()).getZenithColor().method7294(var10);
         var4.drawText(var14, var3, var7 + 8.0F, var8 + (16.0F - var14.height()) / 2.0F, var15);
         float var16 = 9.0F;
         float var17 = 5.0F;
         this.drawSwitch(var4, var7 + var9 - 8.0F - var16, var8 + (16.0F - var17) / 2.0F, var16, var17, var12, var10, var11);
      }, () -> {
         HeadlessBots.setModuleEnabled(var1, var2, !this.isModuleEnabled(var1, var2));
         Zenith.getInstance().getClientSounds().method1570(false);
      }, null);
   }

   private void renderVitals(MenuDrawContext var1, String var2, int var3, int var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      BotClient var10 = HeadlessBots.get(var2);
      BotPlayer var11 = var10 == null ? null : var10.getPlayer();
      float var12 = var11 == null ? 0.0F : var11.getHealth();
      float var13 = var11 == null ? 20.0F : Math.max(1.0F, var11.getMaxHealth());
      float var14 = MathHelper.clamp(var12 / var13, 0.0F, 1.0F);
      Font var15 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String var16 = t("gui.tools.bots.health");
      var1.drawText(var15, var16, var5 + 8.0F, var6 + 3.0F, var9.getTextSecondary().getZenithColor().method7294(var8));
      String var17 = var11 == null ? "—" : String.format(Locale.ROOT, "%.1f", var12);
      var1.drawText(
         var15,
         var17,
         var5 + var7 - 8.0F - var15.width(var17),
         var6 + 3.0F,
         (var11 == null ? var9.getTextTertiary() : var9.getTextEnable()).getZenithColor().method7294(var8)
      );
      float var18 = var6 + 3.0F + var15.height() + 4.0F;
      float var19 = var7 - 16.0F;
      var1.drawRoundedRect(
         var5 + 8.0F, var18, var19, 3.0F, ProxyDotRadiusData.method8069(1.5F), var9.getFieldSurfaceBackground().getZenithColor().method7294(var8 * 4.0F)
      );
      if (var14 > 0.0F) {
         ZenithColor var20 = var14 > 0.35F ? var9.getFriendColor().getZenithColor() : var9.getHeartIcon().getZenithColor();
         var1.drawRoundedRect(var5 + 8.0F, var18, Math.max(3.0F, var19 * var14), 3.0F, ProxyDotRadiusData.method8069(1.5F), var20.method7294(var8));
      }

      if (this.insideAside(var3, var4) && new BoundsData(var5, var6, var7, 22.0F).contains(var3, var4)) {
         HoverInfo.request(var16, t("gui.tools.bots.health.desc"));
      }
   }

   private void renderPreviewFrame(MenuDrawContext var1, String var2, int var3, int var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      float var10 = var5 + 8.0F;
      float var11 = var6 + 2.0F;
      float var12 = var7 - 16.0F;
      float var13 = 90.0F;
      ProxyDotRadiusData var14 = ProxyDotRadiusData.method8069(6.0F);
      BoundsData var15 = new BoundsData(var10, var11, var12, var13);
      boolean var16 = var2 != null && this.insideAside(var3, var4) && var15.contains(var3, var4);
      var1.drawRoundedRect(var10, var11, var12, var13, var14, var9.getPanelLeftBackground().getZenithColor().method7293(190).method7294(var8));
      var1.drawRoundedBorder(var10, var11, var12, var13, 0.1F, var14, var9.getFieldBorder().getZenithColor().method7294(var8 * (var16 ? 6.0F : 3.0F)));
      if (var2 == null) {
         this.previewRect = null;
         this.renderEmptyPreview(var1, var10, var11, var12, var13, var8, var9);
      } else {
         boolean var17 = this.isJoined(var2);
         if (!var17) {
            Identifier var18 = BotAvatarCache.prewarm(var2);
            float var19 = 22.0F;
            float var20 = var11 + var13 / 2.0F - var19 / 2.0F - 7.0F;
            if (var18 != null) {
               RenderShaders.method5294(
                  MatrixUtils.method664(var1.getMatrices()),
                  var18,
                  var10 + (var12 - var19) / 2.0F,
                  var20,
                  var19,
                  var19,
                  ProxyDotRadiusData.method8069(5.0F),
                  ZenithColor.INSTANCE.method7294(var8 * 0.75F)
               );
            }

            Font var21 = Fonts.NEW_MEDIUM.getFont(5.0F);
            String var22 = this.phaseLabel(var2);
            var1.drawText(var21, var22, var10 + (var12 - var21.width(var22)) / 2.0F, var20 + var19 + 6.0F, this.phaseColor(var2, var9).method7294(var8));
            Font var23 = Fonts.NEW_REGULAR.getFont(4.6F);
            String var24 = t(this.isOnline(var2) ? "gui.tools.bots.hint.wait" : "gui.tools.bots.hint.connect");
            var1.drawText(
               var23,
               var24,
               var10 + (var12 - var23.width(var24)) / 2.0F,
               var20 + var19 + 6.0F + var21.height() + 4.0F,
               var9.getTextTertiary().getZenithColor().method7294(var8 * 0.8F)
            );
         } else if (!this.previewHasFrame) {
            Font var25 = Fonts.NEW_REGULAR.getFont(4.8F);
            String var27 = t("gui.bot.loadingWorld");
            var1.drawText(
               var25,
               var27,
               var10 + (var12 - var25.width(var27)) / 2.0F,
               var11 + var13 / 2.0F - var25.height() / 2.0F,
               var9.getTextTertiary().getZenithColor().method7294(var8)
            );
         }

         if (var16) {
            String var26 = var17 ? "gui.tools.bots.view.desc" : (this.isOnline(var2) ? "gui.tools.bots.disconnect.desc" : "gui.tools.bots.connect.desc");
            HoverInfo.request(var2, t(var26));
         }

         this.previewRect = var17 ? var15 : null;
         this.previewAlpha = var8;
         this.previewHovered = var16;
      }
   }

   @Override
   public void renderPriority(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      BoundsData var7 = this.previewRect;
      this.previewRect = null;
      String var8 = this.previewOwner;
      BotClient var9 = var8 == null ? null : HeadlessBots.get(var8);
      if (var9 == null || !var9.isJoined()) {
         this.releasePreview();
         this.previewHasFrame = false;
      } else if (var7 != null && !(this.previewAlpha < 0.9F) && !(var4 < 0.9F)) {
         ZenithStyle var10 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var10 != null) {
            if (this.previewClient != var9) {
               this.releasePreview();
               this.previewClient = var9;
               this.previewView = new BotWorldView(var9);
            }

            MsdfRenderer.flushBatch();
            var1.draw();
            double var11 = mc.getWindow().getScaleFactor() * Math.max(0.5F, Zenith.getInstance().getNLMenuScreen().getGuiScale());
            int var13 = MathHelper.clamp((int)(var7.width() * var11), 64, 960);
            int var14 = MathHelper.clamp((int)(var7.height() * var11), 64, 960);
            this.previewHasFrame = this.previewView.renderToFbo(var13, var14);
            if (this.previewHasFrame) {
               BoundsData var15 = this.asideViewport();
               boolean var16 = var15 != null;
               if (var16) {
                  var1.enableScissor(
                     var15.getFloatValue(), var15.getFloatValue2(), var15.getFloatValue() + var15.width(), var15.getFloatValue2() + var15.height()
                  );
               }

               RenderShaders.method5298(
                  MatrixUtils.method664(var1.getMatrices()),
                  this.previewView.getColorAttachment(),
                  var7.getFloatValue(),
                  var7.getFloatValue2(),
                  var7.width(),
                  var7.height(),
                  ProxyDotRadiusData.method8069(6.0F),
                  ZenithColor.INSTANCE.method7294(this.previewAlpha),
                  0.0F,
                  1.0F,
                  1.0F,
                  0.0F
               );
               this.renderPreviewOverlay(var1, var8, var9, var7, var10);
               if (var16) {
                  MsdfRenderer.flushBatch();
                  var1.disableScissor();
               }
            }
         }
      }
   }

   private void renderPreviewOverlay(MenuDrawContext var1, String var2, BotClient var3, BoundsData var4, ZenithStyle var5) {
      float var6 = 15.0F;
      var1.drawRoundedRect(
         var4.getFloatValue(),
         var4.getFloatValue2() + var4.height() - var6,
         var4.width(),
         var6,
         ProxyDotRadiusData.method8077(6.0F),
         new ZenithColor(10, 10, 14, 195).method7294(this.previewAlpha)
      );
      Font var7 = Fonts.NEW_MEDIUM.getFont(5.0F);
      float var8 = var4.getFloatValue2() + var4.height() - var6 / 2.0F - var7.height() / 2.0F;
      BotPlayer var9 = var3.getPlayer();
      String var10 = var9 == null ? "" : String.format(Locale.ROOT, "%.0f HP", var9.getHealth());
      float var11 = var7.width(var10);
      var1.drawText(var7, fit(var7, var2, var4.width() - 16.0F - var11), var4.getFloatValue() + 6.0F, var8, ZenithColor.INSTANCE.method7294(this.previewAlpha));
      if (!var10.isEmpty()) {
         var1.drawText(
            var7, var10, var4.getFloatValue() + var4.width() - 6.0F - var11, var8, var5.getHeartIcon().getZenithColor().method7294(this.previewAlpha)
         );
      }

      if (this.previewHovered) {
         var1.drawRoundedRect(
            var4.getFloatValue(),
            var4.getFloatValue2(),
            var4.width(),
            var4.height(),
            ProxyDotRadiusData.method8069(6.0F),
            new ZenithColor(10, 10, 14, 120).method7294(this.previewAlpha)
         );
         Font var12 = Fonts.NEW_MEDIUM.getFont(5.2F);
         String var13 = t("gui.tools.bots.hint.control");
         var1.drawText(
            var12,
            var13,
            var4.getFloatValue() + (var4.width() - var12.width(var13)) / 2.0F,
            var4.getFloatValue2() + var4.height() / 2.0F - var12.height() / 2.0F,
            ZenithColor.INSTANCE.method7294(this.previewAlpha)
         );
      }
   }

   private void openPreview(String var1) {
      if (this.isJoined(var1)) {
         mc.setScreen(new BotControlScreen(var1));
      } else {
         this.toggleConnection(var1);
         this.invalidate();
      }
   }

   private void releasePreview() {
      if (this.previewView != null) {
         this.previewView.close();
         this.previewView = null;
      }

      this.previewClient = null;
   }

   private void drawPill(MenuDrawContext var1, float var2, float var3, float var4, String var5, ZenithColor var6, float var7) {
      float var8 = 9.0F;
      var1.drawRoundedRect(var2, var3, var4, var8, ProxyDotRadiusData.method8069(var8 / 2.0F), var6.method7293(34).method7294(var7));
      Font var9 = Fonts.NEW_MEDIUM.getFont(4.4F);
      var1.drawText(var9, var5, var2 + (var4 - var9.width(var5)) / 2.0F, var3 + (var8 - var9.height()) / 2.0F, var6.method7294(var7));
   }

   private void drawSwitch(MenuDrawContext var1, float var2, float var3, float var4, float var5, boolean var6, float var7, ZenithStyle var8) {
      var1.drawRect(var2, var3, var4, var5, var8.getFieldSurfaceBackground().getZenithColor().method7294(var7 * 3.0F));
      var1.drawRoundedBorder(var2, var3, var4, var5, 0.1F, ProxyDotRadiusData.INSTANCE, var8.getFieldBorder().getZenithColor().method7294(var7 * 4.0F));
      float var9 = 3.0F;
      float var10 = var6 ? var2 + var4 - var9 - 1.0F : var2 + 1.0F;
      var1.drawRect(var10, var3 + 1.0F, var9, var5 - 2.0F, (var6 ? var8.getPrimaryColor() : var8.getTextTertiary()).getZenithColor().method7294(var7));
   }

   private void renderEmptyPreview(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      ZenithColor var8 = var7.getPrimaryColor().getZenithColor();
      float var9 = (float)(System.currentTimeMillis() % 2600L) / 2600.0F;
      float var10 = var3 + 4.0F + (var5 - 8.0F) * var9;
      float var11 = (float)Math.sin(var9 * Math.PI);
      var1.drawRect(var2 + 6.0F, var10, var4 - 12.0F, 0.6F, var8.method7292(0.35F * var11).method7294(var6));
      var1.drawRect(var2 + 6.0F, var10 - 6.0F, var4 - 12.0F, 6.0F, var8.method7292(0.04F * var11).method7294(var6));
      float var12 = 7.0F;
      ZenithColor var13 = var7.getTextTertiary().getZenithColor().method7292(0.5F).method7294(var6);
      float[][] var14 = new float[][]{
         {var2 + 6.0F, var3 + 6.0F, 1.0F, 1.0F},
         {var2 + var4 - 6.0F, var3 + 6.0F, -1.0F, 1.0F},
         {var2 + 6.0F, var3 + var5 - 6.0F, 1.0F, -1.0F},
         {var2 + var4 - 6.0F, var3 + var5 - 6.0F, -1.0F, -1.0F}
      };

      for (float[] var18 : var14) {
         float var19 = var18[2] > 0.0F ? var18[0] : var18[0] - var12;
         float var20 = var18[3] > 0.0F ? var18[1] : var18[1] - 0.6F;
         var1.drawRect(var19, var20, var12, 0.6F, var13);
         float var21 = var18[2] > 0.0F ? var18[0] : var18[0] - 0.6F;
         float var22 = var18[3] > 0.0F ? var18[1] : var18[1] - var12;
         var1.drawRect(var21, var22, 0.6F, var12, var13);
      }

      float var25 = 26.0F;
      float var26 = (float)Math.sin(System.currentTimeMillis() / 650.0) * 1.3F;
      float var27 = var2 + (var4 - var25) / 2.0F;
      float var28 = var3 + var5 / 2.0F - var25 / 2.0F - 10.0F + var26;
      ToolTab.tile(var1, var7, var27, var28, var25, var8, 0.55F, var6);
      ToolTab.bigGlyph(var1, MenuGlyph.BOTS, var27 + var25 / 2.0F, var28 + var25 / 2.0F, 2.0F, var8.method7294(var6));
      Font var29 = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font var30 = Fonts.NEW_REGULAR.getFont(4.3F);
      String var31 = t("gui.tools.bots.preview.title");
      float var32 = var28 + var25 + 7.0F - var26;
      var1.drawText(var29, var31, var2 + (var4 - var29.width(var31)) / 2.0F, var32, var7.getTextEnable().getZenithColor().method7294(var6));
      var32 += var29.height() + 3.0F;

      for (String var24 : wrapText(t("gui.tools.bots.preview.hint"), var30, var4 - 24.0F, 2)) {
         var1.drawText(var30, var24, var2 + (var4 - var30.width(var24)) / 2.0F, var32, var7.getTextTertiary().getZenithColor().method7294(var6));
         var32 += var30.height() + 2.0F;
      }
   }

   private void renderEmptyList(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      ZenithColor var9 = var8.getPrimaryColor().getZenithColor();
      float var10 = var4 + 8.0F;
      float var11 = var6 - 16.0F;
      ToolTab.card(var1, var8, var10, var5 + 4.0F, var11, 62.0F, 0.0F, var7);
      float var12 = 22.0F;
      float var13 = (float)Math.sin(System.currentTimeMillis() / 650.0) * 1.2F;
      float var14 = var10 + 10.0F;
      float var15 = var5 + 4.0F + (62.0F - var12) / 2.0F + var13;
      ToolTab.tile(var1, var8, var14, var15, var12, var9, 0.55F, var7);
      ToolTab.bigGlyph(var1, MenuGlyph.BOTS, var14 + var12 / 2.0F, var15 + var12 / 2.0F, 1.6F, var9.method7294(var7));
      Font var16 = Fonts.NEW_SEMIBOLD.getFont(5.8F);
      Font var17 = Fonts.NEW_REGULAR.getFont(4.4F);
      float var18 = var14 + var12 + 10.0F;
      float var19 = var10 + var11 - 8.0F - var18;
      List<String> var20 = wrapText(t("gui.tools.bots.empty"), var17, var19, 3);
      float var21 = var16.height() + 4.0F + var20.size() * (var17.height() + 2.0F);
      float var22 = var5 + 4.0F + (62.0F - var21) / 2.0F;
      var1.drawText(var16, var16.clip(t("gui.tools.bots.emptyTitle"), var19), var18, var22, var8.getTextEnable().getZenithColor().method7294(var7));
      var22 += var16.height() + 4.0F;

      for (Object var24_o : (Iterable<?>)(Object)(var20)) {
         String var24 = (String)var24_o;
         var1.drawText(var17, var24, var18, var22, var8.getTextTertiary().getZenithColor().method7294(var7));
         var22 += var17.height() + 2.0F;
      }

      boolean var41 = this.addressInput != null && !this.addressInput.getText().isBlank();
      boolean var42 = this.nameInput != null && !this.nameInput.getText().isBlank();
      String[] var25 = new String[]{t("gui.tools.bots.step.address"), t("gui.tools.bots.step.name"), t("gui.tools.bots.step.add")};
      boolean[] var26 = new boolean[]{var41, var42, false};
      int var27 = !var41 ? 0 : (!var42 ? 1 : 2);
      float var28 = var5 + 72.0F;
      Font var29 = Fonts.NEW_MEDIUM.getFont(4.7F);
      Font var30 = Fonts.NEW_MEDIUM.getFont(4.3F);

      for (int var31 = 0; var31 < var25.length; var31++) {
         float var32 = var28 + var31 * 18.0F;
         boolean var33 = var31 == var27;
         float var34 = var33 ? ToolTab.pulse(0.8F) : 0.0F;
         ZenithColor var35 = var26[var31] ? ToolTab.OK : (var33 ? var9 : var8.getTextTertiary().getZenithColor());
         ToolTab.card(var1, var8, var10, var32, var11, 15.0F, var33 ? 0.4F : 0.0F, var7);
         float var36 = 10.0F;
         float var37 = var10 + 4.0F;
         float var38 = var32 + 2.5F;
         var1.drawRoundedRect(var37, var38, var36, var36, ProxyDotRadiusData.method8069(var36 / 2.0F), var35.method7292(0.18F + 0.12F * var34).method7294(var7));
         String var39 = var26[var31] ? "✓" : String.valueOf(var31 + 1);
         if (var26[var31]) {
            MenuGlyph.CHECK.draw(var1, var37 + (var36 - 6.0F) / 2.0F, var38 + (var36 - 6.0F) / 2.0F, var35.method7294(var7));
         } else {
            var1.drawText(var30, var39, var37 + (var36 - var30.width(var39)) / 2.0F, var38 + (var36 - var30.height()) / 2.0F, var35.method7294(var7));
         }

         var1.drawText(
            var29,
            var29.clip(var25[var31], var11 - 22.0F),
            var37 + var36 + 6.0F,
            var32 + (15.0F - var29.height()) / 2.0F,
            (var26[var31] ? var8.getTextTertiary() : (var33 ? var8.getTextEnable() : var8.getTextSecondary())).getZenithColor().method7294(var7)
         );
      }

      float var43 = var28 + var25.length * 18.0F + 6.0F;

      for (int var44 = 0; var44 < 3; var44++) {
         float var45 = var43 + var44 * 22.0F;
         float var46 = var7 * (0.55F - var44 * 0.15F);
         ToolTab.skeleton(var1, var8, var10, var45 + 4.0F, 13.0F, 13.0F, var44, var46);
         ToolTab.skeleton(var1, var8, var10 + 13.0F + 6.0F, var45 + 10.5F - 2.0F, var11 * (0.35F - var44 * 0.05F), 4.0F, var44 + 1, var46);
         ToolTab.skeleton(var1, var8, var10 + var11 - 14.0F, var45 + 3.5F, 14.0F, 14.0F, var44 + 2, var46);
         ToolTab.skeleton(var1, var8, var10 + var11 - 14.0F - 34.0F, var45 + 6.5F, 28.0F, 8.0F, var44 + 3, var46);
      }
   }

   private void drawHint(MenuDrawContext var1, float var2, float var3, float var4, String var5, float var6, ZenithStyle var7) {
      Font var8 = Fonts.NEW_REGULAR.getFont(4.8F);
      float var9 = var3;

      for (String var11 : wrapText(var5, var8, var4 - 32.0F, 3)) {
         var1.drawText(var8, var11, var2 + (var4 - var8.width(var11)) / 2.0F, var9, var7.getTextTertiary().getZenithColor().method7294(var6));
         var9 += var8.height() + 2.0F;
      }
   }

   private static void drawPlay(MenuDrawContext var0, float var1, float var2, ZenithColor var3) {
      SvgIcon.PLAY.draw(var0, var1 - 1.5F, var2, 6.0F, var3);
   }

   private static void drawStop(MenuDrawContext var0, float var1, float var2, ZenithColor var3) {
      SvgIcon.STOP.draw(var0, var1 - 0.5F, var2 - 0.5F, 6.0F, var3);
   }

   private boolean insideMain(double var1, double var3) {
      BoundsData var5 = this.mainViewport();
      return var5 == null || var5.contains(var1, var3);
   }

   private boolean insideAside(double var1, double var3) {
      BoundsData var5 = this.asideViewport();
      return var5 == null || var5.contains(var1, var3);
   }

   private static String displayModuleName(String var0) {
      return var0.startsWith("Bot") && var0.length() > 3 ? var0.substring(3) : var0;
   }

   private String moduleDescription(String var1) {
      String var2 = BotModuleManager.supportedModuleDescription(var1);
      return var2 != null && !var2.isBlank() ? var2 : t("gui.tools.bots.module.desc");
   }

   private boolean isModuleEnabled(String var1, String var2) {
      BotClient var3 = HeadlessBots.get(var1);
      if (var3 != null) {
         BotModule var4 = var3.getModules().getModule(var2);
         if (var4 != null) {
            return var4.isEnabled();
         }
      }

      return HeadlessBots.isModuleEnabledInProfile(var1, var2);
   }

   private boolean isOnline(String var1) {
      BotClient var2 = HeadlessBots.get(var1);
      return var2 != null && var2.getPhase() != BotPhase.DISCONNECTED;
   }

   private boolean isJoined(String var1) {
      BotClient var2 = HeadlessBots.get(var1);
      return var2 != null && var2.isJoined();
   }

   private int onlineCount() {
      int var1 = 0;

      for (BotClient var3 : HeadlessBots.all()) {
         if (var3.getPhase() != BotPhase.DISCONNECTED) {
            var1++;
         }
      }

      return var1;
   }

   private ZenithColor phaseColor(String var1, ZenithStyle var2) {
      BotClient var3 = HeadlessBots.get(var1);
      BotPhase var4 = var3 == null ? BotPhase.DISCONNECTED : var3.getPhase();

      return switch (var4) {
         case PLAY -> var2.getFriendColor().getZenithColor();
         case DISCONNECTED -> var2.getTextTertiary().getZenithColor();
         default -> var2.getPrimaryColor().getZenithColor();
      };
   }

   private String phaseLabel(String var1) {
      BotClient var2 = HeadlessBots.get(var1);
      if (var2 == null) {
         return t("gui.tools.bots.phase.offline");
      } else {
         return switch (var2.getPhase()) {
            case PLAY -> t("gui.tools.bots.phase.play");
            case DISCONNECTED -> t("gui.tools.bots.phase.offline");
            case CONNECTING -> t("gui.tools.bots.phase.connecting");
            case LOGIN -> t("gui.tools.bots.phase.login");
            case CONFIGURATION -> t("gui.tools.bots.phase.config");
         };
      }
   }

   private void toggleConnection(String var1) {
      BotClient var2 = HeadlessBots.get(var1);
      if (var2 != null && var2.getPhase() != BotPhase.DISCONNECTED) {
         HeadlessBots.disconnect(var1);
      } else {
         String[] var3 = this.resolveAddress();
         if (var3 != null) {
            HeadlessBots.connect(var1, var3[0], Integer.parseInt(var3[1]));
         }
      }
   }

   private void connectAll() {
      String[] var1 = this.resolveAddress();
      if (var1 != null) {
         HeadlessBots.connectAll(HeadlessBots.allNames(), var1[0], Integer.parseInt(var1[1]));
         this.invalidate();
      }
   }

   private String[] resolveAddress() {
      String var1 = this.addressInput == null ? HeadlessBots.getLastAddress() : this.addressInput.getText();
      if (var1 != null && !var1.isBlank()) {
         var1 = var1.trim();
         int var2 = var1.lastIndexOf(58);
         if (var2 < 0) {
            return new String[]{var1, String.valueOf(25565)};
         } else {
            String var3 = var1.substring(0, var2);
            String var4 = var1.substring(var2 + 1);

            try {
               int var5 = Integer.parseInt(var4);
               if (var5 > 0 && var5 <= 65535) {
                  return new String[]{var3, String.valueOf(var5)};
               } else {
                  throw new NumberFormatException(var4);
               }
            } catch (NumberFormatException var6) {
               ChatLogger.method2021(t("gui.tools.bots.address.badPort"));
               return null;
            }
         }
      } else {
         ChatLogger.method2021(t("gui.tools.bots.address.missing"));
         return null;
      }
   }

   private void addBot() {
      if (this.nameInput != null) {
         String var1 = this.nameInput.getText();
         if (var1 != null && !var1.isBlank()) {
            if (!HeadlessBots.add(var1.trim())) {
               ChatLogger.method2021(t("gui.tools.bots.add.exists"));
            } else {
               this.selected = var1.trim();
               this.nameInput.setText("");
               this.nameInput.setCursor(0);
               this.invalidate();
               this.scrollToEnd();
            }
         }
      }
   }

   @Override
   public float footerHeight() {
      return 34.0F;
   }

   @Override
   public void renderFooter(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      if (this.nameInput == null) {
         this.nameInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.tools.bots.add.hint"), 16.0F);
         this.nameInput.setType(SearchBox.Type.ENGLISH);
         this.nameInput.setType2(SearchBox.Type2.ONLY_ONE_WORLD);
      }

      var1.drawRect(var4 + 8.0F, var5 - GuiStyle.PADDING.intValue(), var6 - 16.0F, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F));
      float var9 = var5 + 8.0F;
      float var10 = 26.0F;
      float var11 = var6 - 16.0F - var10 - GuiStyle.PADDING.intValue();
      var1.drawRoundedRect(
         var4 + 8.0F,
         var9,
         var11,
         18.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         var8.getPanelLeftBackground().getZenithColor().method7294(var7)
      );
      boolean var12 = this.addButtonBounds(var4, var9, var6, var10).contains(var2, var3);
      var1.drawRoundedRect(
         var4 + var6 - 8.0F - var10,
         var9,
         var10,
         18.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         var8.getPrimaryColor().getZenithColor().method7293(var12 ? 62 : 30).method7294(var7)
      );
      Font var13 = Fonts.NEW_MEDIUM.getFont(5.0F);
      boolean var14 = this.nameInput.isSelected();
      MenuGlyph.PROFILE.draw(var1, var4 + 8.0F + 5.0F, var9 + 6.0F, (var14 ? var8.getTextEnable() : var8.getTextTertiary()).getZenithColor().method7294(var7));
      this.nameInput.setFont(var13);
      this.nameInput.setWidth(var11 - 20.0F);
      this.nameInput
         .method987(
            var1,
            var4 + 8.0F + 15.0F,
            var9 + (18.0F - var13.height()) / 2.0F,
            var8.getTextEnable().getZenithColor().method7294(var7),
            var8.getTextTertiary().getZenithColor().method7294(var7)
         );
      float var15 = var4 + var6 - 8.0F - var10 / 2.0F;
      float var16 = var9 + 9.0F;
      ZenithColor var17 = var8.getPrimaryColor().getZenithColor().method7293(var12 ? 255 : 215).method7294(var7);
      var1.drawRect(var15 - 3.0F, var16 - 0.5F, 6.0F, 1.0F, var17);
      var1.drawRect(var15 - 0.5F, var16 - 3.0F, 1.0F, 6.0F, var17);
      if (var12) {
         HoverInfo.request(t("gui.tools.bots.caption.list"), t("gui.tools.bots.add.hint"));
      }
   }

   private BoundsData addButtonBounds(float var1, float var2, float var3, float var4) {
      return new BoundsData(var1 + var3 - 8.0F - var4, var2, var4, 18.0F);
   }

   @Override
   public boolean onFooterClicked(double var1, double var3, Enum3 var5) {
      BoundsData var6 = this.footerBounds();
      if (var6 == null) {
         return false;
      } else {
         float var7 = var6.getFloatValue2() + 8.0F;
         if (this.addButtonBounds(var6.getFloatValue(), var7, var6.width(), 26.0F).contains(var1, var3)) {
            this.addBot();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else {
            if (this.nameInput != null) {
               if (this.addressInput != null) {
                  this.addressInput.setSelected(false);
               }

               this.nameInput.setSelected(true);
            }

            return true;
         }
      }
   }

   private void drawInput(MenuDrawContext var1, SearchBox var2, MenuGlyph var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      var1.drawRoundedRect(
         var4 + 8.0F,
         var5,
         var6 - 16.0F,
         18.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         var8.getPanelLeftBackground().getZenithColor().method7295(var8.getPrimaryColor().getZenithColor(), var2.isSelected() ? 0.18F : 0.0F).method7294(var7)
      );
      Font var9 = Fonts.NEW_MEDIUM.getFont(5.0F);
      var3.draw(var1, var4 + 8.0F + 5.0F, var5 + 6.0F, (var2.isSelected() ? var8.getTextEnable() : var8.getTextTertiary()).getZenithColor().method7294(var7));
      var2.setFont(var9);
      var2.setWidth(var6 - 16.0F - 20.0F);
      var2.method987(
         var1,
         var4 + 8.0F + 15.0F,
         var5 + (18.0F - var9.height()) / 2.0F,
         var8.getTextEnable().getZenithColor().method7294(var7),
         var8.getTextTertiary().getZenithColor().method7294(var7)
      );
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.powerBounds.clear();
      this.connectAllBounds = null;
      this.disconnectAllBounds = null;
      super.render(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      this.lastClickX = var1;
      this.lastClickY = var3;
      if (var5 == Enum3.LEFT && this.insideMain(var1, var3)) {
         if (this.connectAllBounds != null && this.connectAllBounds.contains(var1, var3)) {
            this.connectAll();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }

         if (this.disconnectAllBounds != null && this.disconnectAllBounds.contains(var1, var3)) {
            HeadlessBots.disconnectAll();
            Zenith.getInstance().getClientSounds().method1570(false);
            this.invalidate();
            return true;
         }

         for (Map.Entry var7 : this.powerBounds.entrySet()) {
            if (((BoundsData)var7.getValue()).contains(var1, var3)) {
               this.toggleConnection((String)var7.getKey());
               Zenith.getInstance().getClientSounds().method1570(false);
               this.invalidate();
               return true;
            }
         }
      }

      if (this.addressInput != null && this.insideMain(var1, var3) && this.addressInput.onMouseClicked(var1, var3, var5)) {
         if (this.nameInput != null) {
            this.nameInput.setSelected(false);
         }

         return true;
      } else {
         return super.onMouseClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      SearchBox var4 = this.focusedInput();
      if (var4 != null) {
         if (var1 == 256) {
            var4.setSelected(false);
            return true;
         }

         if (var1 == 257 || var1 == 335) {
            if (var4 == this.nameInput) {
               this.addBot();
            } else {
               HeadlessBots.setLastAddress(var4.getText());
               var4.setSelected(false);
            }

            return true;
         }

         if (var4.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      SearchBox var3 = this.focusedInput();
      return var3 != null && var3.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   private SearchBox focusedInput() {
      if (this.nameInput != null && this.nameInput.isSelected()) {
         return this.nameInput;
      } else {
         return this.addressInput != null && this.addressInput.isSelected() ? this.addressInput : null;
      }
   }

   @Override
   public void tick() {
      String var1 = String.join(",", HeadlessBots.allNames())
         + "|"
         + this.onlineCount()
         + "|"
         + this.selected
         + "|"
         + (this.selected == null ? "" : this.phaseLabel(this.selected));
      if (!var1.equals(this.contentSignature)) {
         this.contentSignature = var1;
         this.invalidate();
      }
   }

   @Override
   public void close() {
      this.releasePreview();
   }

   private static final class CardAnimations {
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private final Animation select = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private final Animation power = new Animation(120L, 0.0F, Easing.INSTANCE12);
   }
}

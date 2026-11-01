package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.List;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.CloudDmClient;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CloudSectionPanel extends ElementPanel {
   private static final int TAB_FRIENDS = 0;
   private static final int TAB_CHAT = 1;
   private static final int TABS = 7;
   private static final float SWITCH_SHIFT = 14.0F;
   private final List<SectionPanel> children = new ArrayList<>();
   private final List<String> names = new ArrayList<>();
   private final List<BoundsData> tabBounds = new ArrayList<>();
   private final List<MenuGlyph> icons = new ArrayList<>();
   private int active;
   private final Animation plateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private final Animation switchAnimation = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private float switchDirection = 1.0F;
   private final int[] badgeCache = new int[7];
   private long badgeStamp;
   private static final long BADGE_REFRESH_MS = 250L;
   private final Animation[] badgeShow = new Animation[7];
   private FriendsSectionPanel friendsPanel;
   private final int[] badgeLast = new int[7];
   private final Animation[] tabHover = new Animation[7];

   public CloudSectionPanel(FriendsSectionPanel var1, CloudChatSectionPanel var2, VoiceSectionPanel var3) {
      this.friendsPanel = var1;
      this.add("gui.cloud.tab.friends", MenuGlyph.FRIENDS, var1);
      var1.openChatWith(var2x -> {
         if (!var2.openDirectWith(var2x)) {
            return false;
         } else {
            this.open(1);
            return true;
         }
      });
      this.add("gui.cloud.tab.chat", MenuGlyph.CHAT, var2);
      this.add("gui.cloud.tab.voice", MenuGlyph.MIC, var3);
   }

   private void add(String var1, MenuGlyph var2, SectionPanel var3) {
      var3.setEmbedded(true);
      this.badgeShow[this.names.size()] = new Animation(220L, 0.0F, Easing.INSTANCE12);
      this.tabHover[this.names.size()] = new Animation(130L, 0.0F, Easing.INSTANCE12);
      this.names.add(var1);
      this.icons.add(var2);
      this.children.add(var3);
   }

   private void switchTo(int var1) {
      if (var1 != this.active && var1 >= 0 && var1 < this.children.size()) {
         this.switchDirection = var1 > this.active ? 1.0F : -1.0F;
         this.active = var1;
         this.switchAnimation.reset(0.0F);
         this.switchAnimation.method2810(1.0F);
         if (this.switchDirection > 0.0F) {
            Zenith.getInstance().getClientSounds().method1562();
         } else {
            Zenith.getInstance().getClientSounds().method1563();
         }
      }
   }

   private int badge(int var1) {
      long var2 = System.currentTimeMillis();
      if (var2 - this.badgeStamp > 250L) {
         this.badgeStamp = var2;
         this.badgeCache[0] = this.computeBadge(0);
         this.badgeCache[1] = this.computeBadge(1);
      }

      return var1 >= 0 && var1 < this.badgeCache.length ? this.badgeCache[var1] : 0;
   }

   private int computeBadge(int var1) {
      if (var1 == 1) {
         return CloudDmClient.getInstance().method4869();
      } else {
         CloudConnectionState var2 = state();
         if (var2 == null || !var2.method8035()) {
            return 0;
         } else if (var1 == 0) {
            Cloud var3 = Zenith.getInstance().getCloud();
            return var2.method8055().size() + (var3 == null ? 0 : var3.getPartyMember().method1147());
         } else {
            return 0;
         }
      }
   }

   private static CloudConnectionState state() {
      Cloud var0 = Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
      return var0 == null ? null : var0.state();
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private SectionPanel current() {
      return this.children.get(this.active);
   }

   public void open(int var1) {
      this.switchTo(var1);
   }

   public void openFriendsAdd(String var1) {
      this.open(0);
      if (this.friendsPanel != null) {
         this.friendsPanel.devOpenAdd(var1);
      }
   }

   @Override
   public float getHeaderTitleWidth() {
      return 19.0F + Fonts.NEW_MEDIUM.getFont(5.5F).width("Cloud");
   }

   @Override
   public float getContentWidth() {
      return 380.0F;
   }

   @Override
   public boolean isHeaderSearchOnly() {
      return false;
   }

   @Override
   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float var10 = var5 + 8.0F;
         MenuGlyph.CLOUD.draw(var1, var10, var6 + 8.5F, var8.getTextEnable().getZenithColor().method7294(var4));
         var1.drawText(var9, "Cloud", var10 + 11.0F, var6 + (23.0F - var9.height()) / 2.0F, var8.getTextEnable().getZenithColor().method7294(var4));
      }
   }

   @Override
   public float getButtonWidth() {
      Font var1 = Fonts.NEW_MEDIUM.getFont(5.0F);
      float var2 = 0.0F;

      for (int var3 = 0; var3 < this.names.size(); var3++) {
         var2 += this.tabWidth(var1, var3);
      }

      return var2;
   }

   private float tabWidth(Font var1, int var2) {
      float var3 = var1.width(t(this.names.get(var2))) + 16.0F + 9.0F;
      float var4 = this.badgeShow(var2);
      if (var4 > 0.001F) {
         var3 += (badgeWidth(this.badgeLast[var2]) + 4.0F) * var4;
      }

      return var3;
   }

   private float badgeShow(int var1) {
      if (var1 >= 0 && var1 < this.badgeShow.length && this.badgeShow[var1] != null) {
         int var2 = this.badge(var1);
         if (var2 > 0) {
            this.badgeLast[var1] = var2;
         }

         return this.badgeShow[var1].method2808(var2 > 0 ? 1.0F : 0.0F);
      } else {
         return 0.0F;
      }
   }

   private static float badgeWidth(int var0) {
      Font var1 = Fonts.NEW_MEDIUM.getFont(4.4F);
      return Math.max(9.0F, var1.width(badgeText(var0)) + 6.0F);
   }

   private static String badgeText(int var0) {
      return var0 > 99 ? "99+" : String.valueOf(var0);
   }

   @Override
   public void renderHeaderButtons(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
      ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var9 != null) {
         Font var10 = Fonts.NEW_MEDIUM.getFont(5.0F);
         this.tabBounds.clear();
         float var11 = var5;
         float var12 = var5;
         float var13 = 0.0F;

         for (int var14 = 0; var14 < this.names.size(); var14++) {
            float var15 = this.tabWidth(var10, var14);
            this.tabBounds.add(new BoundsData(var11, var6, var15, 23.0F));
            if (var14 == this.active) {
               var12 = var11;
               var13 = var15;
            }

            var11 += var15;
         }

         if (!this.plateReady) {
            this.plateX.method2809(var12);
            this.plateWidth.method2809(var13);
            this.plateReady = true;
         }

         float var25 = this.plateX.method2808(var12);
         float var26 = this.plateWidth.method2808(var13);
         float var16 = 15.0F;
         var1.drawRoundedRect(
            var25 + 2.0F,
            var6 + 4.0F,
            Math.max(0.0F, var26 - 4.0F),
            var16,
            ProxyDotRadiusData.method8069(var16 / 2.0F),
            var9.getDisableActiveBg().getZenithColor().method7294(var4)
         );

         for (int var17 = 0; var17 < this.names.size(); var17++) {
            BoundsData var18 = this.tabBounds.get(var17);
            boolean var19 = var17 == this.active;
            float var20 = this.tabHover[var17].method2808(!var19 && var18.contains(var2, var3) ? 1.0F : 0.0F);
            ZenithColor var21 = (var19 ? var9.getTextEnable() : var9.getTextTertiary())
               .getZenithColor()
               .method7295(var9.getTextEnable().getZenithColor(), var20 * 0.5F)
               .method7294(var4);
            float var22 = var18.getFloatValue() + 8.0F;
            this.icons.get(var17).draw(var1, var22, var6 + 8.5F, var21);
            var22 += 9.0F;
            String var23 = t(this.names.get(var17));
            var1.drawText(var10, var23, var22, var6 + (23.0F - var10.height()) / 2.0F, var21);
            float var24 = this.badgeShow(var17);
            if (var24 > 0.01F) {
               this.drawBadge(var1, var22 + var10.width(var23) + 4.0F, var6, this.badgeLast[var17], var24, var4, var9);
            }
         }
      }
   }

   private void drawBadge(MenuDrawContext var1, float var2, float var3, int var4, float var5, float var6, ZenithStyle var7) {
      Font var8 = Fonts.NEW_MEDIUM.getFont(4.4F);
      String var9 = badgeText(var4);
      float var10 = badgeWidth(var4);
      float var11 = 7.0F;
      float var12 = var3 + (23.0F - var11) / 2.0F;
      float var13 = 0.5F + 0.5F * var5;
      var1.pushMatrix();
      var1.getMatrices().translate(var2 + var10 / 2.0F, var12 + var11 / 2.0F);
      var1.getMatrices().scale(var13, var13);
      var1.drawRoundedRect(
         -var10 / 2.0F,
         -var11 / 2.0F,
         var10,
         var11,
         ProxyDotRadiusData.method8069(var11 / 2.0F),
         var7.getPrimaryColor().getZenithColor().method7294(var6 * var5)
      );
      var1.drawText(var8, var9, -var8.width(var9) / 2.0F, -var8.height() / 2.0F, new ZenithColor(255, 255, 255).method7294(var6 * var5));
      var1.popMatrix();
   }

   @Override
   public boolean onHeaderButtonsClicked(double var1, double var3, Enum3 var5) {
      for (int var6 = 0; var6 < this.tabBounds.size(); var6++) {
         if (this.tabBounds.get(var6).contains(var1, var3)) {
            this.switchTo(var6);
            return true;
         }
      }

      return false;
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      float var7 = this.switchAnimation.method2808(1.0F);
      float var8 = 14.0F * this.switchDirection * (1.0F - var7);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var8, 0.0F);
      this.current().render(var1, var2, var3, var4 * var7, var5, var6);
      var1.getMatrices().popMatrix();
   }

   @Override
   public void renderPriority(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.current().renderPriority(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void tick() {
      this.current().tick();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      return this.current().onMouseClicked(var1, var3, var5);
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      return this.current().onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.current().onMouseDragged(var1, var3, var5, var6, var8);
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return this.current().mouseScrolled(var1, var3, var5, var7);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.current().keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.current().charTyped(var1, var2);
   }

   @Override
   public List<? extends Element> getElements() {
      return this.current().getElements();
   }

   @Override
   public void close() {
      for (SectionPanel var2 : this.children) {
         var2.close();
      }
   }
}

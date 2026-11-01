package org.zenith.client.screens.nlgui.panel.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.panel.SectionPanel;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ToolsSectionPanel extends ElementPanel {
   private static final float SWITCH_SHIFT = 14.0F;
   private static final float LIVE_GAP = 10.0F;
   private final List<SectionPanel> children = new ArrayList<>();
   private final List<String> names = new ArrayList<>();
   private final List<MenuGlyph> icons = new ArrayList<>();
   private final List<BooleanSupplier> liveStates = new ArrayList<>();
   private final List<BoundsData> tabBounds = new ArrayList<>();
   private int active;
   private final Animation plateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private final Animation switchAnimation = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private float switchDirection = 1.0F;
   private final Animation liveAnimation = new Animation(320L, 0.0F, Easing.INSTANCE13);
   private boolean liveReady;
   private long lastFrame;

   public ToolsSectionPanel() {
      BotsToolPanel var1 = new BotsToolPanel();
      AutoSborToolPanel var2 = new AutoSborToolPanel();
      AutoCraftToolPanel var3 = new AutoCraftToolPanel();
      this.add("gui.tools.tab.bots", MenuGlyph.BOTS, var1, var1::isLive);
      this.add("gui.tools.tab.inventory", MenuGlyph.STORAGE, var2, var2::isLive);
      this.add("gui.tools.tab.craft", MenuGlyph.CRAFT, var3, var3::isLive);
   }

   private void add(String var1, MenuGlyph var2, SectionPanel var3, BooleanSupplier var4) {
      var3.setEmbedded(false);
      this.names.add(var1);
      this.icons.add(var2);
      this.children.add(var3);
      this.liveStates.add(var4);
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

   private void switchTo(int var1) {
      if (var1 != this.active && var1 >= 0 && var1 < this.children.size()) {
         this.switchDirection = var1 > this.active ? 1.0F : -1.0F;
         this.active = var1;
         if (this.current() instanceof ToolTab var2) {
            var2.shown();
         }

         this.switchAnimation.reset(0.0F);
         this.switchAnimation.method2810(1.0F);
         if (this.switchDirection > 0.0F) {
            Zenith.getInstance().getClientSounds().method1562();
         } else {
            Zenith.getInstance().getClientSounds().method1563();
         }
      }
   }

   @Override
   public float getHeaderTitleWidth() {
      return 19.0F + Fonts.NEW_MEDIUM.getFont(5.5F).width("Tools");
   }

   @Override
   public float getContentWidth() {
      return 380.0F;
   }

   @Override
   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float var10 = var5 + 8.0F;
         MenuGlyph.TOOLS.draw(var1, var10, var6 + 8.5F, var8.getTextEnable().getZenithColor().method7294(var4));
         var1.drawText(var9, "Tools", var10 + 11.0F, var6 + (23.0F - var9.height()) / 2.0F, var8.getTextEnable().getZenithColor().method7294(var4));
      }
   }

   @Override
   public float getButtonWidth() {
      Font var1 = Fonts.NEW_MEDIUM.getFont(5.0F);
      float var2 = 0.0F;

      for (int var3 = 0; var3 < this.names.size(); var3++) {
         var2 += this.tabWidth(var1, var3);
      }

      return var2 + 10.0F + this.liveWidth();
   }

   private float tabWidth(Font var1, int var2) {
      return var1.width(t(this.names.get(var2))) + 16.0F + 9.0F;
   }

   private float liveWidth() {
      return 9.0F + Fonts.NEW_REGULAR.getFont(4.6F).width(this.liveText());
   }

   private boolean isLive(int var1) {
      BooleanSupplier var2 = this.liveStates.get(var1);

      try {
         return var2 != null && var2.getAsBoolean();
      } catch (Exception var4) {
         return false;
      }
   }

   private String liveText() {
      return t(this.isLive(this.active) ? "gui.tools.state.running" : "gui.tools.state.idle");
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

         float var24 = this.plateX.method2808(var12);
         float var25 = this.plateWidth.method2808(var13);
         float var16 = 15.0F;
         var1.drawRoundedRect(
            var24 + 2.0F,
            var6 + 4.0F,
            Math.max(0.0F, var25 - 4.0F),
            var16,
            ProxyDotRadiusData.method8069(var16 / 2.0F),
            var9.getDisableActiveBg().getZenithColor().method7294(var4)
         );

         for (int var17 = 0; var17 < this.names.size(); var17++) {
            BoundsData var18 = this.tabBounds.get(var17);
            boolean var19 = var17 == this.active;
            boolean var20 = var18.contains(var2, var3);
            ZenithColor var21 = (var19 ? var9.getTextEnable() : var9.getTextTertiary())
               .getZenithColor()
               .method7295(var9.getTextEnable().getZenithColor(), var20 && !var19 ? 0.5F : 0.0F)
               .method7294(var4);
            float var22 = var18.getFloatValue() + 8.0F;
            this.icons.get(var17).draw(var1, var22, var6 + 8.5F, var21);
            var1.drawText(var10, t(this.names.get(var17)), var22 + 9.0F, var6 + (23.0F - var10.height()) / 2.0F, var21);
            if (!var19 && this.isLive(var17)) {
               float var23 = 2.5F;
               var1.drawRoundedRect(
                  var18.getFloatValue() + var18.width() - 6.0F,
                  var6 + 6.0F,
                  var23,
                  var23,
                  ProxyDotRadiusData.method8069(var23 / 2.0F),
                  var9.getFriendColor().getZenithColor().method7294(var4)
               );
            }
         }

         this.drawLive(var1, var11 + 10.0F, var6, var4, var9);
      }
   }

   private void drawLive(MenuDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      boolean var6 = this.isLive(this.active);
      if (!this.liveReady) {
         this.liveAnimation.method2809(var6 ? 1.0F : 0.0F);
         this.liveReady = true;
      }

      float var7 = this.liveAnimation.method2808(var6 ? 1.0F : 0.0F);
      Font var8 = Fonts.NEW_REGULAR.getFont(4.6F);
      String var9 = this.liveText();
      float var10 = 3.0F;
      float var11 = var3 + 11.5F;
      ZenithColor var12 = var5.getTextTertiary().getZenithColor();
      ZenithColor var13 = var5.getFriendColor().getZenithColor();
      var1.drawRoundedRect(
         var2, var11 - var10 / 2.0F, var10, var10, ProxyDotRadiusData.method8069(var10 / 2.0F), var12.method7295(var13, var7).method7294(var4)
      );
      var1.drawText(var8, var9, var2 + var10 + 4.0F, var11 - var8.height() / 2.0F, var12.method7295(var13, var7 * 0.65F).method7294(var4));
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
      long var7 = System.currentTimeMillis();
      if (var7 - this.lastFrame > 400L && this.current() instanceof ToolTab var9) {
         var9.shown();
      }

      this.lastFrame = var7;
      float var11 = this.switchAnimation.method2808(1.0F);
      float var12 = 14.0F * this.switchDirection * (1.0F - var11);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var12, 0.0F);
      this.current().render(var1, var2, var3, var4 * var11, var5, var6);
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

package org.zenith.client.screens.nlgui.panel.tools;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.panel.SectionPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public abstract class ToolTab extends SectionPanel {
   public static final ZenithColor OK = new ZenithColor(96, 214, 128);
   public static final ZenithColor DANGER = new ZenithColor(242, 96, 110);
   public static final ZenithColor WARN = new ZenithColor(240, 180, 80);
   public static final float CARD_ROUND = GuiStyle.ROUND.intValue() * 0.7F;
   public static final int TONE_GHOST = 0;
   public static final int TONE_PRIMARY = 1;
   public static final int TONE_DANGER = 2;
   private final Map<String, Animation> hovers = new HashMap<>();
   private final Map<String, Animation> switches = new HashMap<>();
   private final Map<String, Long> marks = new HashMap<>();
   private long enteredAt = System.currentTimeMillis();
   private long asideAt = System.currentTimeMillis();
   private long replayAt;
   private int replayFrom = Integer.MAX_VALUE;
   private long asideReplayAt;
   private int asideReplayFrom = Integer.MAX_VALUE;
   private long lastFrame;

   @Override
   public boolean pressFeedback() {
      return true;
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      long var7 = System.currentTimeMillis();
      if (var7 - this.lastFrame > 400L) {
         this.shown();
      }

      this.lastFrame = var7;
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public void shown() {
      this.enteredAt = System.currentTimeMillis();
      this.asideAt = this.enteredAt;
      this.replayFrom = Integer.MAX_VALUE;
      this.asideReplayFrom = Integer.MAX_VALUE;
   }

   public void replayMainFrom(int var1) {
      this.replayAt = System.currentTimeMillis();
      this.replayFrom = var1;
   }

   public void replayAsideFrom(int var1) {
      this.asideReplayAt = System.currentTimeMillis();
      this.asideReplayFrom = var1;
   }

   public float hot(String var1, boolean var2) {
      return this.hovers.computeIfAbsent(var1, var0 -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   public void mark(String var1) {
      this.marks.put(var1, System.currentTimeMillis());
   }

   public float since(String var1, long var2) {
      Long var4 = this.marks.get(var1);
      return var4 == null ? 1.0F : Math.min(1.0F, (float)(System.currentTimeMillis() - var4) / (float)var2);
   }

   public float pressed(String var1) {
      return Smooth.press(this.marks.getOrDefault(var1, 0L), 340L);
   }

   private static float curve(long var0, int var2) {
      float var3 = (float)(System.currentTimeMillis() - var0 - Math.min(var2, 14) * 34L) / 320.0F;
      if (var3 <= 0.0F) {
         return 0.0F;
      } else if (var3 >= 1.0F) {
         return 1.0F;
      } else {
         float var4 = 1.0F - var3;
         return 1.0F - var4 * var4 * var4;
      }
   }

   @Override
   public float entryEnter(int var1, boolean var2) {
      if (var2) {
         return var1 >= this.asideReplayFrom && this.asideReplayAt > this.asideAt
            ? curve(this.asideReplayAt, var1 - this.asideReplayFrom)
            : curve(this.asideAt, var1 + 1);
      } else {
         return var1 >= this.replayFrom && this.replayAt > this.enteredAt ? curve(this.replayAt, var1 - this.replayFrom) : curve(this.enteredAt, var1);
      }
   }

   public SectionPanel.AsideRenderer entering(int var1, SectionPanel.AsideRenderer var2) {
      return var2;
   }

   public static float pulse(float var0) {
      return 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 1000.0 * var0 * Math.PI * 2.0);
   }

   public static ZenithColor cardFill(ZenithStyle var0, float var1) {
      return var0.getFieldSurfaceBackground().getZenithColor().method7295(var0.getDisableActiveBg().getZenithColor(), 0.35F + var1 * 0.55F);
   }

   public static void card(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(CARD_ROUND);
      var0.drawRoundedRect(var2, var3, var4, var5, var8, cardFill(var1, var6).method7294(var7));
      var0.drawRoundedBorder(var2, var3, var4, var5, 0.15F, var8, var1.getFieldBorder().getZenithColor().method7294(2.0F + var6 * 2.0F).method7294(var7));
   }

   public static void accentCard(
      MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithColor var8, float var9
   ) {
      ProxyDotRadiusData var10 = ProxyDotRadiusData.method8069(CARD_ROUND);
      var0.drawRoundedRect(var2, var3, var4, var5, var10, cardFill(var1, var6).method7295(var8.method7292(0.16F), var9).method7294(var7));
      var0.drawRoundedBorder(
         var2,
         var3,
         var4,
         var5,
         0.2F,
         var10,
         var1.getFieldBorder().getZenithColor().method7294(2.0F + var6 * 2.0F).method7295(var8.method7292(0.6F), var9).method7294(var7)
      );
   }

   public static void tile(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, ZenithColor var5, float var6, float var7) {
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(var4 * 0.28F);
      var0.drawRoundedRect(
         var2, var3, var4, var4, var8, var1.getPanelLeftBackground().getZenithColor().method7295(var5.method7292(0.3F), var6).method7294(var7)
      );
   }

   public static float pill(MenuDrawContext var0, Font var1, float var2, float var3, String var4, ZenithColor var5, float var6) {
      float var7 = var1.width(var4) + 8.0F;
      float var8 = var1.height() + 3.6F;
      var0.drawRoundedRect(var2, var3, var7, var8, ProxyDotRadiusData.method8069(var8 / 2.0F), var5.method7292(0.16F).method7294(var6));
      var0.drawText(var1, var4, var2 + 4.0F, var3 + 1.8F, var5.method7294(var6));
      return var7;
   }

   public static float pillWidth(Font var0, String var1) {
      return var0.width(var1) + 8.0F;
   }

   public static void dot(MenuDrawContext var0, float var1, float var2, float var3, ZenithColor var4, float var5, float var6) {
      var0.drawRoundedRect(
         var1 - var3 / 2.0F, var2 - var3 / 2.0F, var3, var3, ProxyDotRadiusData.method8069(var3 / 2.0F), var4.method7294(var6 * (1.0F - 0.45F * var5))
      );
   }

   public static void caption(MenuDrawContext var0, ZenithStyle var1, String var2, float var3, float var4, float var5) {
      Font var6 = Fonts.NEW_MEDIUM.getFont(4.0F);
      var0.drawText(var6, HoverInfo.spaced(var2.toUpperCase(Locale.ROOT)), var3, var4, var1.getTextTertiary().getZenithColor().method7294(var5));
   }

   public boolean button(
      MenuDrawContext var1,
      ZenithStyle var2,
      String var3,
      BoundsData var4,
      MenuGlyph var5,
      String var6,
      int var7,
      boolean var8,
      int var9,
      int var10,
      float var11
   ) {
      boolean var12 = var8 && var4.contains(var9, var10);
      float var13 = this.hot("btn:" + var3, var12);
      ZenithColor var14 = var7 == 2 ? DANGER : var2.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData var15 = ProxyDotRadiusData.method8069(Math.min(CARD_ROUND, var4.height() / 2.0F));
      boolean var16 = this.pressFx.begin(var1, "btn:" + var3, var4);
      ZenithColor var17 = cardFill(var2, var13);
      if (!var8) {
         var17 = var2.getFieldSurfaceBackground().getZenithColor().method7295(var2.getDisableActiveBg().getZenithColor(), 0.25F);
      } else if (var7 == 1) {
         var17 = var17.method7295(var14.method7292(0.2F), 0.55F + var13 * 0.45F);
      } else if (var7 == 2) {
         var17 = var17.method7295(var14.method7292(0.14F), 0.25F + var13 * 0.5F);
      }

      var1.drawRoundedRect(var4.getFloatValue(), var4.getFloatValue2(), var4.width(), var4.height(), var15, var17.method7294(var11));
      ZenithColor var18 = !var8
         ? var2.getFieldBorder().getZenithColor().method7294(1.5F)
         : (var7 == 0 ? var2.getFieldBorder().getZenithColor().method7294(2.0F + var13 * 2.5F) : var14.method7292(0.22F + var13 * 0.3F));
      var1.drawRoundedBorder(var4.getFloatValue(), var4.getFloatValue2(), var4.width(), var4.height(), 0.18F, var15, var18.method7294(var11));
      this.pressFx.ink(var1, "btn:" + var3, var4, var15.getFloatValue(), (var7 == 0 ? ZenithColor.INSTANCE : var14).method7292(0.22F), var11);
      Font var19 = Fonts.NEW_MEDIUM.getFont(var4.height() >= 18.0F ? 5.2F : 4.8F);
      ZenithColor var20 = !var8
         ? var2.getTextTertiary().getZenithColor()
         : (
            var7 == 0
               ? var2.getTextSecondary().getZenithColor().method7295(var2.getTextEnable().getZenithColor(), var13)
               : var14.method7295(ZenithColor.INSTANCE, 0.2F + var13 * 0.45F)
         );
      float var21 = var5 == null ? 0.0F : 10.0F;
      String var22 = var19.clip(var6, var4.width() - 10.0F - var21);
      float var23 = var19.width(var22) + var21;
      float var24 = var4.getFloatValue() + (var4.width() - var23) / 2.0F;
      if (var5 != null) {
         var5.draw(var1, var24, var4.getFloatValue2() + (var4.height() - 6.0F) / 2.0F, var20.method7294(var11));
         var24 += var21;
      }

      var1.drawText(var19, var22, var24, var4.getFloatValue2() + (var4.height() - var19.height()) / 2.0F, var20.method7294(var11));
      this.pressFx.end(var1, var16);
      return var12;
   }

   public void pressButton(String var1, double var2, double var4) {
      this.pressFx.press("btn:" + var1, var2, var4);
   }

   public void shimmer(MenuDrawContext var1, BoundsData var2, ZenithColor var3, float var4) {
      float var5 = (float)(System.currentTimeMillis() % 1600L) / 1600.0F;
      float var6 = var2.width() * 0.35F;
      float var7 = var2.getFloatValue() - var6 + (var2.width() + var6) * var5;
      float var8 = Math.max(var2.getFloatValue(), var7);
      float var9 = Math.min(var2.getFloatValue() + var2.width(), var7 + var6);
      if (!(var9 - var8 < 1.0F)) {
         float var10 = (var8 + var9) / 2.0F;
         float var11 = 1.0F - Math.abs(var10 - (var7 + var6 / 2.0F)) / (var6 / 2.0F + 0.01F);
         var1.drawRoundedRect(
            var8,
            var2.getFloatValue2() + 1.0F,
            var9 - var8,
            var2.height() - 2.0F,
            ProxyDotRadiusData.method8069(Math.min(4.0F, (var2.height() - 2.0F) / 2.0F)),
            var3.method7292(0.12F * Math.max(0.3F, var11)).method7294(var4)
         );
      }
   }

   public void drawSwitch(MenuDrawContext var1, String var2, float var3, float var4, float var5, float var6, boolean var7, float var8, ZenithStyle var9) {
      float var10 = this.switches.computeIfAbsent(var2, var1x -> new Animation(220L, var7 ? 1.0F : 0.0F, Easing.INSTANCE15)).method2808(var7 ? 1.0F : 0.0F);
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

   public static void progress(
      MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7, boolean var8, float var9
   ) {
      ProxyDotRadiusData var10 = ProxyDotRadiusData.method8069(var5 / 2.0F);
      var0.drawRoundedRect(var2, var3, var4, var5, var10, var1.getDisableActiveBg().getZenithColor().method7294(var9));
      float var11 = Math.max(0.0F, Math.min(1.0F, var6));
      if (var11 > 0.001F) {
         float var12 = Math.max(var5, var4 * var11);
         var0.drawRoundedRect(var2, var3, var12, var5, var10, var7.method7294(var9));
         if (var8) {
            float var13 = (float)(System.currentTimeMillis() % 1200L) / 1200.0F;
            float var14 = var2 + (var12 + 10.0F) * var13 - 10.0F;
            float var15 = Math.max(var2, var14);
            float var16 = Math.min(var2 + var12, var14 + 10.0F);
            if (var16 > var15) {
               var0.drawRoundedRect(var15, var3, var16 - var15, var5, var10, ZenithColor.INSTANCE.method7292(0.35F).method7294(var9));
            }
         }
      }
   }

   public static void skeleton(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4, float var5, int var6, float var7) {
      float var8 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 420.0 - var6 * 0.7);
      var0.drawRoundedRect(
         var2,
         var3,
         var4,
         var5,
         ProxyDotRadiusData.method8069(Math.min(var5 / 2.0F, 3.0F)),
         var1.getDisableActiveBg().getZenithColor().method7294(var7 * (0.45F + 0.45F * var8))
      );
   }

   public static void bigGlyph(MenuDrawContext var0, MenuGlyph var1, float var2, float var3, float var4, ZenithColor var5) {
      var0.pushMatrix();
      var0.getMatrices().translate(var2, var3);
      var0.getMatrices().scale(var4, var4);
      var1.draw(var0, -3.0F, -3.0F, var5);
      var0.popMatrix();
   }

   public void emptyCard(
      MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, float var6, MenuGlyph var7, String var8, String var9, float var10
   ) {
      card(var1, var2, var3, var4, var5, var6, 0.0F, var10);
      float var11 = (float)Math.sin(System.currentTimeMillis() / 600.0) * 1.2F;
      float var12 = 20.0F;
      float var13 = var3 + (var5 - var12) / 2.0F;
      float var14 = var4 + 9.0F + var11;
      ZenithColor var15 = var2.getPrimaryColor().getZenithColor();
      tile(var1, var2, var13, var14, var12, var15, 0.6F, var10);
      bigGlyph(var1, var7, var13 + var12 / 2.0F, var14 + var12 / 2.0F, 1.5F, var15.method7294(var10));
      Font var16 = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font var17 = Fonts.NEW_REGULAR.getFont(4.4F);
      float var18 = var4 + 9.0F + var12 + 8.0F;
      var1.drawText(
         var16,
         var16.clip(var8, var5 - 12.0F),
         var3 + (var5 - Math.min(var5 - 12.0F, var16.width(var8))) / 2.0F,
         var18,
         var2.getTextEnable().getZenithColor().method7294(var10)
      );
      var18 += var16.height() + 3.0F;

      for (String var20 : wrapText(var9, var17, var5 - 16.0F, 3)) {
         var1.drawText(var17, var20, var3 + (var5 - var17.width(var20)) / 2.0F, var18, var2.getTextTertiary().getZenithColor().method7294(var10));
         var18 += var17.height() + 2.0F;
      }
   }

   public void drawItem(MenuDrawContext var1, ItemStack var2, float var3, float var4, float var5, float var6) {
      if (var2 != null && !var2.isEmpty() && !(var6 <= 0.02F) && this.isFullyVisible()) {
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var3, var4);
         float var7 = var5 / 16.0F;
         var1.getMatrices().scale(var7, var7);
         var1.drawItem(var2, 0, 0);
         var1.getMatrices().popMatrix();
      }
   }

   public static String fit(Font var0, String var1, float var2) {
      if (var1 != null && !var1.isEmpty() && !(var2 <= 0.0F)) {
         if (var0.width(var1) <= var2) {
            return var1;
         } else {
            String var3 = "..";
            float var4 = var0.width(var3);
            if (var4 > var2) {
               return "";
            } else {
               int var5 = var1.length();

               while (var5 > 0 && var0.width(var1.substring(0, var5)) + var4 > var2) {
                  var5--;
               }

               return var5 == 0 ? "" : var1.substring(0, var5) + var3;
            }
         }
      } else {
         return "";
      }
   }
}

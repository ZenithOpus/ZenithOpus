package org.zenith.utility.misc;

import java.util.List;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class HudListLayout {
   public static final float CONST_floatValue = 20.0F;
   public static final float CONST_floatValue2 = 14.0F;
   public static final float CONST_floatValue3 = 3.5F;
   public static final float CONST_floatValue4 = 13.0F;
   public static final float CONST_floatValue5 = 10.0F;
   public static final float CONST_floatValue6 = 6.0F;
   public static final float CONST_floatValue7 = GuiStyle.PADDING.intValue() * 1.5F;
   public static final String text = "m";
   public static final float CONST_floatValue8 = 17.0F;
   private static final float CONST_floatValue9 = 2.0F;

   private HudListLayout() {
   }

   public static ZenithStyle style() {
      return Zenith.getInstance().getStyleManager().getCurrentStyle();
   }

   public static Font method7630() {
      return Fonts.NEW_ICONS.getFont(5.5F);
   }

   public static Font method7631() {
      return Fonts.NEW_MEDIUM.getFont(6.0F);
   }

   public static Font nameFont() {
      return Fonts.NEW_MEDIUM.getFont(5.6F);
   }

   public static Font method7632() {
      return Fonts.NEW_SEMIBOLD.getFont(5.4F);
   }

   public static ProxyDotRadiusData method7633() {
      return ProxyDotRadiusData.method8069(InterfaceModule.method6801());
   }

   public static float dividerWidth() {
      return CONST_floatValue7 * 2.0F + 1.0F;
   }

   public static float method7634(CustomDrawContext var0, float var1, float var2, float var3) {
      float var4 = var3 * 0.45F;
      var0.drawRect(var1 + CONST_floatValue7, var2 + (var3 - var4) / 2.0F, 1.0F, var4, style().getTextTertiary().getZenithColor().method7294(0.45F));
      return var1 + dividerWidth();
   }

   public static void method7635(CustomDrawContext var0, float var1, float var2, float var3, float var4) {
      ProxyDotRadiusData var5 = method7633();
      var0.drawBlurHud(var1, var2, var3, var4, 21.0F, var5, ZenithColor.INSTANCE);
      var0.drawRoundedRect(var1, var2, var3, var4, var5, style().getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778()));
   }

   public static void method7636(CustomDrawContext var0, float var1, float var2, float var3, String var4, String var5) {
      ZenithStyle var6 = style();
      Font var7 = method7630();
      Font var8 = method7631();
      float var9 = var2 + 3.5F;
      var0.drawRoundedRect(var1 + 3.5F, var9, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.9F), var6.getPrimaryColor().getZenithColor());
      method7637(var0, var7, var4, var1 + 3.5F, var9, 13.0F, ZenithColor.INSTANCE);
      float var10 = method7634(var0, var1 + 3.5F + 13.0F, var2, 20.0F);
      var0.drawText(var8, var5, var10, var2 + (20.0F - var8.height()) / 2.0F, var6.getTextEnable().getZenithColor());
      var0.drawText(var7, "m", var1 + var3 - 6.0F - var7.width("m"), var2 + (20.0F - var7.height()) / 2.0F, var6.getTextTertiary().getZenithColor());
   }

   public static void method7637(CustomDrawContext var0, Font var1, String var2, float var3, float var4, float var5, ZenithColor var6) {
      float[] var7 = var1.inkBounds(var2);
      if (var7 == null) {
         var0.drawText(var1, var2, var3 + (var5 - var1.width(var2)) / 2.0F, var4 + (var5 - var1.height()) / 2.0F, var6);
      } else {
         float var8 = var3 + (var5 - var7[0]) / 2.0F;
         float var9 = var4 + (var5 - var7[1]) / 2.0F - var1.baseline() + var7[2];
         var0.drawText(var1, var2, var8, var9, var6);
      }
   }

   public static float method7638(String var0, String var1) {
      return 16.5F + dividerWidth() + method7631().width(var1) + CONST_floatValue7 + method7630().width("m") + 6.0F;
   }

   public static float method7639(CustomDrawContext var0, float var1, float var2, String var3) {
      method7640(var0, var1, var2);
      method7637(var0, method7630(), var3, method7641(var1), method7642(var2), 10.0F, style().getPrimaryColor().getZenithColor());
      return method7643(var1);
   }

   public static float method7640(CustomDrawContext var0, float var1, float var2) {
      var0.drawRoundedRect(
         method7641(var1), method7642(var2), 10.0F, 10.0F, ProxyDotRadiusData.method8069(3.0F), style().getHeaderHudBackground().getZenithColor()
      );
      return method7634(var0, var1 + 6.0F + 10.0F, var2, 14.0F);
   }

   public static float method7641(float var0) {
      return var0 + 6.0F;
   }

   public static float method7642(float var0) {
      return var0 + 2.0F;
   }

   public static float method7643(float var0) {
      return var0 + method7645();
   }

   public static void method7644(CustomDrawContext var0, float var1, float var2, String var3) {
      Font var4 = nameFont();
      var0.drawText(var4, var3, var1, var2 + (14.0F - var4.height()) / 2.0F, style().getTextEnable().getZenithColor());
   }

   public static float method7645() {
      return 16.0F + dividerWidth();
   }

   public static float method7646(CustomDrawContext var0, float var1, float var2, float var3, String var4) {
      ZenithStyle var5 = style();
      Font var6 = method7632();
      float var7 = var6.width(var4);
      float var8 = method7647(var4);
      float var9 = var1 + var3 - 6.0F - var8;
      float var10 = var2 + 2.0F;
      var0.drawRoundedRect(var9, var10, var8, 10.0F, ProxyDotRadiusData.method8069(3.0F), var5.getHeaderHudBackground().getZenithColor());
      var0.drawText(var6, var4, var9 + (var8 - var7) / 2.0F, var10 + (10.0F - var6.height()) / 2.0F, var5.getTextEnable().getZenithColor());
      return var9;
   }

   public static float method7647(String var0) {
      return Math.max(10.0F, method7632().width(var0) + GuiStyle.PADDING.intValue());
   }

   public static float method7648(String var0, String var1) {
      return method7645() + nameFont().width(var0) + CONST_floatValue7 + method7647(var1) + 6.0F;
   }

   public static float method7649(CustomDrawContext var0, float var1, float var2, float var3, String var4, float var5) {
      ZenithStyle var6 = style();
      Font var7 = method7632();
      float var8 = var7.width(var4);
      float var9 = method7650(var4);
      float var10 = var1 + var3 - 6.0F - var9;
      float var11 = method7642(var2);
      float var12 = 8.0F;
      float var13 = var10 + var9 - GuiStyle.PADDING.intValue() / 2.0F - var12;
      float var14 = var11 + (10.0F - var12) / 2.0F;
      var0.drawRoundedRect(var10, var11, var9, 10.0F, ProxyDotRadiusData.method8069(3.0F), var6.getHeaderHudBackground().getZenithColor());
      var0.drawText(var7, var4, var10 + GuiStyle.PADDING.intValue() / 2.0F, var11 + (10.0F - var7.height()) / 2.0F, var6.getTextEnable().getZenithColor());
      var0.drawArcBorder(var13, var14, var12, var12, 1.0F, 360.0F, 0.5F, var6.getFieldBorder().getZenithColor());
      var0.drawArcBorder(var13, var14, var12, var12, 1.0F, 360.0F * var5, 0.5F, var6.getPrimaryColor().getZenithColor());
      return var10;
   }

   public static float method7650(String var0) {
      return GuiStyle.PADDING.intValue() / 2.0F + method7632().width(var0) + GuiStyle.PADDING.intValue() / 2.0F + 8.0F + GuiStyle.PADDING.intValue() / 2.0F;
   }

   public static float method7651(String var0, String var1) {
      return method7645() + nameFont().width(var0) + CONST_floatValue7 + method7650(var1) + 6.0F;
   }

   public static float method7652() {
      return 7.0F;
   }

   public static void method7653(CustomDrawContext var0, float var1, float var2, float var3, String var4, int var5) {
      ZenithStyle var6 = style();
      Font var7 = Fonts.NEW_MEDIUM.getFont(5.0F);
      float var8 = method7652();
      var0.drawText(var7, var4, var1 + 6.0F, var2 + (var8 - var7.height()) / 2.0F, var6.getTextTertiary().getZenithColor());
      String var9 = String.valueOf(var5);
      var0.drawText(var7, var9, var1 + var3 - 6.0F - var7.width(var9), var2 + (var8 - var7.height()) / 2.0F, var6.getTextTertiary().getZenithColor());
   }

   public static Font method7654() {
      return Fonts.NEW_ICONS.getFont(5.0F);
   }

   public static Font method7655() {
      return Fonts.NEW_MEDIUM.getFont(6.0F);
   }

   public static Font method7656() {
      return Fonts.NEW_REGULAR.getFont(5.4F);
   }

   public static float method7657(HudListLayout.Data var0) {
      float var1 = method7654().width(var0.getText()) + 2.0F;

      for (HudListLayout.Data2 var3 : var0.getItems()) {
         var1 += (var3.isFlag() ? method7656() : method7655()).width(var3.text());
      }

      return var1;
   }

   public static float method7658(List<HudListLayout.Data> var0) {
      if (var0.isEmpty()) {
         return 0.0F;
      } else {
         float var1 = 12.0F;

         for (int var2 = 0; var2 < var0.size(); var2++) {
            var1 += method7657((HudListLayout.Data)var0.get(var2));
            if (var2 > 0) {
               var1 += dividerWidth();
            }
         }

         return var1;
      }
   }

   public static void method7659(CustomDrawContext var0, float var1, float var2, List<HudListLayout.Data> var3) {
      if (!var3.isEmpty()) {
         ZenithStyle var4 = style();
         Font var5 = method7654();
         Font var6 = method7655();
         Font var7 = method7656();
         ZenithColor var8 = var4.getTextTertiary().getZenithColor();
         ZenithColor var9 = var4.getTextEnable().getZenithColor();
         ZenithColor var10 = var4.getTextTertiary().getZenithColor();
         method7635(var0, var1, var2, method7658(var3), 17.0F);
         float var11 = var1 + 6.0F;

         for (int var12 = 0; var12 < var3.size(); var12++) {
            if (var12 > 0) {
               var11 = method7634(var0, var11, var2, 17.0F);
            }

            HudListLayout.Data var13 = (HudListLayout.Data)var3.get(var12);
            var0.drawText(var5, var13.getText(), var11, var2 + (17.0F - var5.height()) / 2.0F, var8);
            var11 += var5.width(var13.getText()) + 2.0F;

            for (HudListLayout.Data2 var15 : var13.getItems()) {
               Font var16 = var15.isFlag() ? var7 : var6;
               ZenithColor var17 = var15.isFlag() ? var10 : var9;
               var0.drawText(var16, var15.text(), var11, var2 + (17.0F - var16.height()) / 2.0F, var17);
               var11 += var16.width(var15.text());
            }
         }
      }
   }

   public static final class Data {
      private final String text;
      private final List<HudListLayout.Data2> items;

      public String text() {
         return this.text;
      }

      public List<HudListLayout.Data2> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(String var1, List<HudListLayout.Data2> var2) {
         this.text = var1;
         this.items = var2;
      }

      public static HudListLayout.Data2 method7660(String var0) {
         return new HudListLayout.Data2(var0, false);
      }

      public static HudListLayout.Data2 method7661(String var0) {
         return new HudListLayout.Data2(var0, true);
      }

      public static HudListLayout.Data method7662(String var0, String var1, String var2) {
         return new HudListLayout.Data(var0, List.of(method7660(var1), method7661(var2)));
      }

      public String getText() {
         return this.text;
      }

      public List<HudListLayout.Data2> getItems() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final String text;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, flag);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "flag=" + this.flag + "]";
      }

      public Data2(String var1, boolean var2) {
         this.text = var1;
         this.flag = var2;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

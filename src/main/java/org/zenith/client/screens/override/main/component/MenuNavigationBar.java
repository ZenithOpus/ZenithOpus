package org.zenith.client.screens.override.main.component;

import java.util.ArrayList;
import java.util.List;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MenuNavigationBar {
   public static final float HEIGHT = MenuSkin.HEADER_HEIGHT;
   private static final float GAP = MenuTheme.px(8.0F);
   private static final float ACTIVE_PADDING = MenuTheme.px(20.0F);
   private static final float ICON_LABEL_GAP = MenuTheme.px(8.0F);
   private static final float ICON_SIZE = 12.0F;
   private static final float LABEL_SIZE = 12.0F;
   private final List<MenuNavigationBar.Entry> entries = new ArrayList<>();

   public MenuNavigationBar(List<MenuNavigationBar.Item> var1) {
      for (MenuNavigationBar.Item var3 : var1) {
         this.entries.add(new MenuNavigationBar.Entry(var3));
      }
   }

   public float measureWidth(int var1) {
      float var2 = GAP * (this.entries.size() - 1);

      for (int var3 = 0; var3 < this.entries.size(); var3++) {
         MenuNavigationBar.Entry var4 = this.entries.get(var3);
         var2 += lerp(HEIGHT, this.activeWidth(var4.item), var3 == var1 ? 1.0F : var4.activity.getValue());
      }

      return var2;
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, int var6) {
      for (int var7 = 0; var7 < this.entries.size(); var7++) {
         this.entries.get(var7).activity.method2807(var7 == var6);
      }

      float var19 = GAP * (this.entries.size() - 1);

      for (MenuNavigationBar.Entry var9 : this.entries) {
         var19 += lerp(HEIGHT, this.activeWidth(var9.item), var9.activity.getValue());
      }

      float var20 = var4 - var19 / 2.0F;
      ZenithColor var21 = MenuTheme.accent();

      for (MenuNavigationBar.Entry var11 : this.entries) {
         float var12 = var11.activity.getValue();
         float var13 = this.activeWidth(var11.item);
         float var14 = lerp(HEIGHT, var13, var12);
         var11.x = var20;
         var11.y = var5;
         var11.width = var14;
         var11.hover.method2807(var11.contains((double)var2, (double)var3));
         float var15 = MenuSkin.radius(HEIGHT);
         ProxyDotRadiusData var16 = ProxyDotRadiusData.method8069(var15);
         ZenithColor var17 = MenuTheme.panel().method7295(MenuTheme.plate(), var11.hover.getValue() * (1.0F - var12));
         ZenithColor var18 = var17.method7295(var21, var12);
         MenuSurface.blur(var1, var20, var5, var14, HEIGHT, var16, 1.0F - var12);
         if (var12 > 0.01F) {
            MenuSurface.glow(var1, var20, var5, var14, HEIGHT, var15, var21, 0.4F * var12);
         }

         var1.drawRoundedRect(var20, var5, var14, HEIGHT, var16, var18);
         this.drawContent(var1, var11, var13, var12, var21);
         var20 += var14 + GAP;
      }
   }

   private void drawContent(MenuDrawContext var1, MenuNavigationBar.Entry var2, float var3, float var4, ZenithColor var5) {
      MenuIcon var6 = var2.item.icon();
      Font var7 = var6.font(12.0F);
      Font var8 = MenuTheme.medium(12.0F);
      String var9 = var2.item.label();
      float var10 = var6.width(12.0F);
      float var11 = var8.width(var9);
      float var12 = var2.x + (HEIGHT - var10) / 2.0F;
      float var13 = var10 + ICON_LABEL_GAP + var11;
      float var14 = var2.x + (var3 - var13) / 2.0F;
      float var15 = lerp(var12, var14, var4);
      ZenithColor var16 = MenuTheme.textTertiary().method7295(var5, var2.hover.getValue() * (1.0F - var4));
      ZenithColor var17 = var16.method7295(MenuTheme.onAccent(), var4);
      var1.enableScissor(var2.x, var2.y, var2.x + var2.width, var2.y + HEIGHT);
      var1.drawText(var7, var6.glyph(), var15, var2.y + (HEIGHT - var7.height()) / 2.0F, var17);
      if (var4 > 0.01F) {
         var1.drawText(var8, var9, var15 + var10 + ICON_LABEL_GAP, var2.y + (HEIGHT - var8.height()) / 2.0F, MenuTheme.onAccent().method7294(var4));
      }

      var1.disableScissor();
   }

   private float activeWidth(MenuNavigationBar.Item var1) {
      Font var2 = MenuTheme.medium(12.0F);
      return ACTIVE_PADDING * 2.0F + var1.icon().width(12.0F) + ICON_LABEL_GAP + var2.width(var1.label());
   }

   private static float lerp(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   public boolean mouseClicked(double var1, double var3) {
      for (MenuNavigationBar.Entry var6 : this.entries) {
         if (var6.contains(var1, var3)) {
            var6.item.action().run();
            return true;
         }
      }

      return false;
   }

   public BoundsData boundsOf(int var1) {
      MenuNavigationBar.Entry var2 = this.entries.get(var1);
      return new BoundsData(var2.x, var2.y, var2.width, HEIGHT);
   }

   private static final class Entry {
      private final MenuNavigationBar.Item item;
      private final Animation activity = new Animation(220L, Easing.INSTANCE7);
      private final Animation hover = new Animation(160L, Easing.INSTANCE12);
      private float x;
      private float y;
      private float width;

      private Entry(MenuNavigationBar.Item var1) {
         this.item = var1;
      }

      private boolean contains(double var1, double var3) {
         return var1 >= this.x && var1 <= this.x + this.width && var3 >= this.y && var3 <= this.y + MenuNavigationBar.HEIGHT;
      }
   }

   public static final class Item {
      private final MenuIcon icon;
      private final String labelKey;
      private final Runnable action;

      public MenuIcon icon() {
         return this.icon;
      }

      public String labelKey() {
         return this.labelKey;
      }

      public Runnable action() {
         return this.action;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Item other)) return false;
         return java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.labelKey, other.labelKey) && java.util.Objects.equals(this.action, other.action);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(icon, labelKey, action);
      }

      @Override
      public String toString() {
         return "Item[" + "icon=" + this.icon + ", " + "labelKey=" + this.labelKey + ", " + "action=" + this.action + "]";
      }

      public Item(MenuIcon var1, String var2, Runnable var3) {
         this.icon = var1;
         this.labelKey = var2;
         this.action = var3;
      }

      public String label() {
         return MenuTheme.t(this.labelKey);
      }
   }
}

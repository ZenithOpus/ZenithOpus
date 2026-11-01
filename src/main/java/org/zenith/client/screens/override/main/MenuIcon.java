package org.zenith.client.screens.override.main;

import org.zenith.base.font.Font;
import org.zenith.base.font.SvgIcon;

public final class MenuIcon {
   private final String glyph;
   private final boolean legacy;
   private final boolean vector;

   public String glyph() {
      return this.glyph;
   }

   public boolean legacy() {
      return this.legacy;
   }

   public boolean vector() {
      return this.vector;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof MenuIcon other)) return false;
      return java.util.Objects.equals(this.glyph, other.glyph) && java.util.Objects.equals(this.legacy, other.legacy) && java.util.Objects.equals(this.vector, other.vector);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(glyph, legacy, vector);
   }

   @Override
   public String toString() {
      return "MenuIcon[" + "glyph=" + this.glyph + ", " + "legacy=" + this.legacy + ", " + "vector=" + this.vector + "]";
   }

   public MenuIcon(String var1, boolean var2) {
      this(var1, var2, false);
   }

   public MenuIcon(String var1, boolean var2, boolean var3) {
      this.glyph = var1;
      this.legacy = var2;
      this.vector = var3;
   }

   public static MenuIcon of(char var0) {
      return new MenuIcon(String.valueOf(var0), false);
   }

   public static MenuIcon legacy(char var0) {
      return new MenuIcon(String.valueOf(var0), true);
   }

   public static MenuIcon svg(SvgIcon var0) {
      return new MenuIcon(var0.glyph(), false, true);
   }

   public Font font(float var1) {
      if (this.vector) {
         return SvgIcon.font(MenuTheme.px(var1));
      } else {
         return this.legacy ? MenuTheme.legacyIcon(var1) : MenuTheme.icon(var1);
      }
   }

   public float width(float var1) {
      return this.font(var1).width(this.glyph);
   }

   public float height(float var1) {
      return this.font(var1).height();
   }
}

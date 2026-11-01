package org.zenith.base.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.utility.misc.ColorUtils;

public class FormattedTextProcessor {
   public static List<FormattedTextProcessor.TextSegment> processText(Text var0, int var1) {
      ArrayList<FormattedTextProcessor.TextSegment> var2 = new ArrayList<>();
      var0.visit((var2x, var3) -> {
         if (!var3.isEmpty()) {
            int var4 = extractColor(var2x, var1);
            var3 = NameProtectModule.method5149(var3);
            boolean var5 = var2x.isBold();
            boolean var6 = var2x.isItalic();
            boolean var7 = var2x.isUnderlined();
            boolean var8 = var2x.isStrikethrough();
            var2.add(new FormattedTextProcessor.TextSegment(var3, var4, var5, var6, var7, var8));
         }

         return Optional.empty();
      }, Style.EMPTY);
      return var2;
   }

   private static int extractColor(Style var0, int var1) {
      TextColor var2 = var0.getColor();
      if (var2 != null) {
         int var3 = var2.getRgb() | 0xFF000000;
         return var3 != -1 ? ColorUtils.method6439(var3, ColorUtils.method6427(var1)) : var1;
      } else {
         return var1;
      }
   }

   public static final class TextSegment {
      private final String text;
      private final int color;
      private final boolean bold;
      private final boolean italic;
      private final boolean underlined;
      private final boolean strikethrough;

      public String text() {
         return this.text;
      }

      public int color() {
         return this.color;
      }

      public boolean bold() {
         return this.bold;
      }

      public boolean italic() {
         return this.italic;
      }

      public boolean underlined() {
         return this.underlined;
      }

      public boolean strikethrough() {
         return this.strikethrough;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof TextSegment other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.color, other.color) && java.util.Objects.equals(this.bold, other.bold) && java.util.Objects.equals(this.italic, other.italic) && java.util.Objects.equals(this.underlined, other.underlined) && java.util.Objects.equals(this.strikethrough, other.strikethrough);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, color, bold, italic, underlined, strikethrough);
      }

      @Override
      public String toString() {
         return "TextSegment[" + "text=" + this.text + ", " + "color=" + this.color + ", " + "bold=" + this.bold + ", " + "italic=" + this.italic + ", " + "underlined=" + this.underlined + ", " + "strikethrough=" + this.strikethrough + "]";
      }

      public TextSegment(String var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6) {
         this.text = var1;
         this.color = var2;
         this.bold = var3;
         this.italic = var4;
         this.underlined = var5;
         this.strikethrough = var6;
      }
   }
}

package org.zenith.base.font;

import lombok.Generated;
import net.minecraft.text.Text;

public class Font {
   private MsdfFont font;
   private float size;

   public float height() {
      return this.font.getAscent() * this.size;
   }

   public float baseline() {
      return MsdfRenderer.textLineOffset(this.font, this.size);
   }

   public float getStringHeight(String var1) {
      float var2 = 0.0F;
      float var3 = 0.0F;

      for (char var7 : (var1.isEmpty() ? " " : var1).toCharArray()) {
         if (var7 == '\n') {
            var2 = var2 == 0.0F ? this.height() : var2;
            var3 += var2;
            var2 = 0.0F;
         } else {
            var2 = Math.max(this.height(), var2);
         }
      }

      return var2 + var3;
   }

   public float width(String var1) {
      return this.font.getWidth(var1, this.size);
   }

   public float[] inkBounds(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         MsdfGlyph var2 = this.font.getGlyph(var1.charAt(0));
         return var2 == null ? null : new float[]{var2.getInkWidth(this.size), var2.getInkHeight(this.size), var2.getInkTop(this.size)};
      } else {
         return null;
      }
   }

   public float width(Text var1) {
      return this.font.getTextWidth(var1, this.size);
   }

   public String clip(String var1, float var2) {
      if (var1 == null || var1.isEmpty()) {
         return var1;
      } else if (var2 <= 0.0F) {
         return "";
      } else if (this.width(var1) <= var2) {
         return var1;
      } else {
         float var3 = this.width("…");
         int var4 = this.font.fitPrefix(var1, this.size, var3, var2);
         return var4 == 0 ? "" : var1.substring(0, var4) + "…";
      }
   }

   @Generated
   public MsdfFont getFont() {
      return this.font;
   }

   @Generated
   public float getSize() {
      return this.size;
   }

   @Generated
   public Font(MsdfFont var1, float var2) {
      this.font = var1;
      this.size = var2;
   }
}

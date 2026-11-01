package org.zenith.utility.misc;

import java.util.List;

public final class WardrobeSetInfo {
   private final String text;
   private final List<String> items;
   private final int intValue;
   private final String text2;

   public String text() {
      return this.text;
   }

   public List<String> items() {
      return this.items;
   }

   public int intValue() {
      return this.intValue;
   }

   public String text2() {
      return this.text2;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof WardrobeSetInfo other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text2, other.text2);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, items, intValue, text2);
   }

   @Override
   public String toString() {
      return "WardrobeSetInfo[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "text2=" + this.text2 + "]";
   }

   public WardrobeSetInfo(String var1, List<String> var2, int var3, String var4) {
      this.text = var1;
      this.items = var2;
      this.intValue = var3;
      this.text2 = var4;
   }

   public String method7305() {
      return "wardrobe.set." + this.text;
   }

   public String method7306() {
      return "wardrobe.set." + this.text + ".bonus";
   }

   public String getText() {
      return this.text;
   }

   public List<String> pieces() {
      return this.items;
   }

   public int color() {
      return this.intValue;
   }

   public String getText2() {
      return this.text2;
   }
}

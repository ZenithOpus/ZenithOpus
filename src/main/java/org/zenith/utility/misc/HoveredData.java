package org.zenith.utility.misc;

import org.zenith.utility.game.FocusSlotEnum;

public final class HoveredData {
   private final String text;
   private final FocusSlotEnum focusSlotEnum;
   private final Rarity rarity;
   private final String text2;
   private final String text3;
   private final String text4;

   public String text() {
      return this.text;
   }

   public FocusSlotEnum focusSlotEnum() {
      return this.focusSlotEnum;
   }

   public Rarity rarity() {
      return this.rarity;
   }

   public String text2() {
      return this.text2;
   }

   public String text3() {
      return this.text3;
   }

   public String text4() {
      return this.text4;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof HoveredData other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.focusSlotEnum, other.focusSlotEnum) && java.util.Objects.equals(this.rarity, other.rarity) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, focusSlotEnum, rarity, text2, text3, text4);
   }

   @Override
   public String toString() {
      return "HoveredData[" + "text=" + this.text + ", " + "focusSlotEnum=" + this.focusSlotEnum + ", " + "rarity=" + this.rarity + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + "]";
   }

   public HoveredData(String var1, FocusSlotEnum var2, Rarity var3, String var4, String var5, String var6) {
      this.text = var1;
      this.focusSlotEnum = var2;
      this.rarity = var3;
      this.text2 = var4;
      this.text3 = var5;
      this.text4 = var6;
   }

   public WardrobeCategory method1546() {
      return this.focusSlotEnum.getWardrobeCategory();
   }

   public String method1547() {
      return "wardrobe.item." + this.text;
   }

   public String method1548() {
      return "wardrobe.item." + this.text + ".desc";
   }

   public String getText() {
      return this.text;
   }

   public FocusSlotEnum getFocusSlotEnum() {
      return this.focusSlotEnum;
   }

   public Rarity getRarity() {
      return this.rarity;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public String getText4() {
      return this.text4;
   }
}

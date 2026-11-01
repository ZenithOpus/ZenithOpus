package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StoreEntry {
   private final String text;
   private final String text2;
   private final String text3;
   private final int intValue;
   private final long longValue;
   private final List<Data1> items;

   public String text() {
      return this.text;
   }

   public String text2() {
      return this.text2;
   }

   public String text3() {
      return this.text3;
   }

   public int intValue() {
      return this.intValue;
   }

   public long longValue() {
      return this.longValue;
   }

   public List<Data1> items() {
      return this.items;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof StoreEntry other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.items, other.items);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, text3, intValue, longValue, items);
   }

   @Override
   public String toString() {
      return "StoreEntry[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "items=" + this.items + "]";
   }
   public static final String text4 = "PUBLIC";
   public static final String text5 = "FRIENDS";
   public static final String text6 = "PRIVATE";

   public StoreEntry(String var1, String var2, String var3, int var4, long var5, List<Data1> var7) {
      Objects.requireNonNull(var1, "userId");
      var3 = var3 != null && !var3.isBlank() ? var3 : "PUBLIC";
      var7 = var7 == null ? List.of() : List.copyOf(var7);
      this.text = var1;
      this.text2 = var2;
      this.text3 = var3;
      this.intValue = var4;
      this.longValue = var5;
      this.items = var7;
   }

   public List<String> method1680() {
      List<String> var1 = new ArrayList<>(this.items.size());

      for (Data1 var3 : this.items) {
         var1.add(var3.getText());
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public boolean method1681(StoreEntry var1) {
      if (var1 == null) {
         return true;
      } else {
         return this.intValue != var1.intValue ? this.intValue > var1.intValue : this.longValue > var1.longValue;
      }
   }

   public String userId() {
      return this.text;
   }

   public String name() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public int version() {
      return this.intValue;
   }

   public long getLongValue() {
      return this.longValue;
   }

   public List<Data1> getItems() {
      return this.items;
   }
}

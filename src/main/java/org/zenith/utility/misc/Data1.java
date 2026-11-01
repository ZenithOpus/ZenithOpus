package org.zenith.utility.misc;

import java.util.Locale;
import java.util.Objects;

public final class Data1 {
   private final String text;
   private final String text2;
   private final int intValue;

   public String text() {
      return this.text;
   }

   public String text2() {
      return this.text2;
   }

   public int intValue() {
      return this.intValue;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Data1 other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, intValue);
   }

   @Override
   public String toString() {
      return "Data1[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + "]";
   }
   public static final int CONST_intValue = -1;

   public Data1(String var1, String var2, int var3) {
      Objects.requireNonNull(var1, "id");
      var2 = var2 != null && !var2.isBlank() ? var2.trim().toUpperCase(Locale.ROOT) : "UNKNOWN";
      this.text = var1;
      this.text2 = var2;
      this.intValue = var3;
   }

   public static Data1 method823(String var0, String var1) {
      return new Data1(var0, var1, -1);
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public int getIntValue() {
      return this.intValue;
   }
}

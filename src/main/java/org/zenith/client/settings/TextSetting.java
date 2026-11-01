package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Generated;

public class TextSetting extends Setting {
   private final TextSetting.Entry entry;
   private String value;
   private final String text;
   private boolean flag;
   private boolean flag2;

   public TextSetting method2120() {
      this.flag = true;
      return this;
   }

   public TextSetting method2121() {
      this.flag2 = true;
      return this;
   }

   public TextSetting(String var1, String var2, String var3) {
      this(var1, "", var2, var3);
   }

   public TextSetting(String var1, String var2, String var3, String var4) {
      super(var1, var2);
      this.value = var3;
      this.text = var4;
      this.entry = TextSetting.Entry.method2124();
   }

   public TextSetting(String var1, String var2, String var3, TextSetting.Entry var4) {
      this(var1, "", var2, var3, var4);
   }

   public TextSetting(String var1, String var2, String var3, String var4, TextSetting.Entry var5) {
      super(var1, var2);
      this.value = var3;
      this.text = var4;
      this.entry = var5 == null ? TextSetting.Entry.method2124() : var5;
   }

   public TextSetting(String var1, String var2, String var3, Supplier<Boolean> var4) {
      this(var1, "", var2, var3, var4);
   }

   public TextSetting(String var1, String var2, String var3, String var4, Supplier<Boolean> var5) {
      super(var1, var2);
      this.value = var3;
      this.text = var4;
      this.entry = TextSetting.Entry.method2124();
      this.setVisible(var5);
   }

   public TextSetting(String var1, String var2, String var3, Supplier<Boolean> var4, TextSetting.Entry var5) {
      this(var1, "", var2, var3, var4, var5);
   }

   public TextSetting(String var1, String var2, String var3, String var4, Supplier<Boolean> var5, TextSetting.Entry var6) {
      super(var1, var2);
      this.value = var3;
      this.text = var4;
      this.entry = var6 == null ? TextSetting.Entry.method2124() : var6;
      this.setVisible(var5);
   }

   public boolean method2122(String var1) {
      if (this.entry.method2123(var1)) {
         this.value = var1;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.value);
   }

   @Override
   public void load(JsonObject var1) {
      if (var1.has(String.valueOf(this.key))) {
         String var2 = var1.get(String.valueOf(this.key)).getAsString();
         this.method2122(var2);
      }
   }

   @Generated
   public TextSetting.Entry getEntry() {
      return this.entry;
   }

   @Generated
   public String getValue() {
      return this.value;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public boolean isLive() {
      return this.flag2;
   }

   @Generated
   public void setValue(String var1) {
      this.value = var1;
   }

   public abstract static class Entry {
      private final int maxLength;

      public Entry() {
         this.maxLength = Integer.MAX_VALUE;
      }

      public abstract boolean method2123(String var1);

      public static TextSetting.Entry method2124() {
         return new TextSetting.Entry(Integer.MAX_VALUE) {
            @Override
            public boolean method2123(String var1) {
               return var1 != null;
            }
         };
      }

      public static TextSetting.Entry method2125(final int var0) {
         return new TextSetting.Entry(var0) {
            @Override
            public boolean method2123(String var1) {
               return var1 != null && var1.length() <= var0;
            }
         };
      }

      public static TextSetting.Entry method2126(int var0, final Predicate<String> var1) {
         return new TextSetting.Entry(var0) {
            @Override
            public boolean method2123(String var1x) {
               return var1x != null && var1.test(var1x);
            }
         };
      }

      @Generated
      public Entry(int var1) {
         this.maxLength = var1;
      }

      @Generated
      public int getMaxLength() {
         return this.maxLength;
      }
   }
}

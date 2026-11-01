package org.zenith.utility.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;

public class Localization {
   private final Map<String, LangEntry> items = new HashMap<>();
   private final List<String> items2 = new ArrayList<>();
   private LangEntry langEntry;
   private String text;

   public Localization() {
      this.method1727("ru");
      this.method1727("en");
      this.method1727("pl");
      this.method1727("tr");
   }

   public void method1727(String var1) {
      if (!this.items2.contains(var1)) {
         this.items2.add(var1);
      }

      if (this.text == null) {
         this.method1728(var1);
      }
   }

   public void method1728(String var1) {
      if (this.items2.contains(var1)) {
         if (this.langEntry == null || !var1.equals(this.text)) {
            LangEntry var2 = this.method1733(var1);
            if (var2 != null) {
               this.items.clear();
               this.items.put(var1, var2);
               this.langEntry = var2;
               this.text = var1;
            } else if (this.text == null) {
               this.text = var1;
            }
         }
      }
   }

   public void method1729() {
      this.method1730(true);
   }

   public void method1730(boolean var1) {
      if (!this.items2.isEmpty()) {
         int var2;
         if ((var2 = this.items2.indexOf(this.text)) < 0) {
            var2 = 0;
         }

         int var3;
         if (var1) {
            var3 = (var2 + 1) % this.items2.size();
         } else {
            var3 = (var2 - 1 + this.items2.size()) % this.items2.size();
         }

         this.method1728(this.items2.get(var3));
      }
   }

   public void method1731() {
      this.method1730(false);
   }

   public List<String> method1732() {
      return Collections.unmodifiableList(this.items2);
   }

   public String getLanguageName() {
      return this.text == null ? "ru" : this.text;
   }

   public String translate(String var1) {
      if (this.langEntry == null && this.text != null) {
         this.langEntry = this.method1733(this.text);
      }

      return this.langEntry == null ? var1 : this.langEntry.method3304(var1);
   }

   private LangEntry method1733(String var1) {
      LangEntry var2 = this.items.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         try {
            LangEntry var5;
            try (InputStream var3 = Localization.class.getResourceAsStream("/assets/zenith/languages/" + var1 + ".language")) {
               if (var3 == null) {
                  return null;
               }

               LangEntry var4 = new LangEntry(var1, var3);
               this.items.put(var1, var4);
               var5 = var4;
            }

            return var5;
         } catch (IOException var8) {
            var8.printStackTrace();
            return null;
         }
      }
   }

   @Generated
   public String getText() {
      return this.text;
   }
}

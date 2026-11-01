package org.zenith.utility.misc;

import org.zenith.client.modules.impl.misc.AutoCraftModule;

public class CraftPresetData {
   private final String text;
   private String text2 = "";
   private String text3 = "";
   private final String[] texts = new String[9];
   private final String[] texts2 = new String[9];
   private int intValue = 0;

   public CraftPresetData(String var1) {
      this.text = var1 == null ? "" : var1;

      for (int var2 = 0; var2 < 9; var2++) {
         this.texts[var2] = "";
         this.texts2[var2] = "";
      }
   }

   public static CraftPresetData method6198() {
      return new CraftPresetData("");
   }

   public boolean method6199() {
      return this.text.isBlank();
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public String method6200(int var1) {
      return this.texts[var1];
   }

   public String method6201(int var1) {
      return this.texts2[var1];
   }

   public boolean method6202() {
      if (this.text2 != null && !this.text2.isBlank()) {
         int var1 = 0;

         for (int var2 = 0; var2 < 9; var2++) {
            if (this.texts[var2] != null && !this.texts[var2].isBlank()) {
               var1++;
            }
         }

         return var1 >= 2;
      } else {
         return false;
      }
   }

   public void method6203(String var1, String var2, String[] var3, String[] var4, int var5) {
      this.text2 = var1 == null ? "" : var1;
      this.text3 = var2 == null ? "" : var2;
      this.intValue = Math.max(0, var5);

      for (int var6 = 0; var6 < 9; var6++) {
         this.texts[var6] = var3 != null && var6 < var3.length && var3[var6] != null ? var3[var6] : "";
         this.texts2[var6] = var4 != null && var6 < var4.length && var4[var6] != null ? var4[var6] : "";
      }
   }

   public void method6204() {
      this.text2 = "";
      this.text3 = "";
      this.intValue = 0;

      for (int var1 = 0; var1 < 9; var1++) {
         this.texts[var1] = "";
         this.texts2[var1] = "";
      }
   }

   public String method6205(AutoCraftModule var1) {
      return !this.method6202()
         ? "AutoCraft: craft item in crafting table to add preset"
         : "AutoCraft: take crafted item to save " + var1.method1107(this.text2, this.text3);
   }
}

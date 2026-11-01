package org.zenith.utility.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.api.Class1;
import org.zenith.utility.game.CraftRecipe;

public final class AutoCraftState {
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 2;
   private final AutoCraftModule autoCraftModule;
   private String text = "";
   private int intValue = 0;
   private final Set<String> items = new HashSet<>();
   private final Map<String, Integer> items2 = new HashMap<>();

   public AutoCraftState(AutoCraftModule var1) {
      this.autoCraftModule = var1;
   }

   public void reset() {
      this.items.clear();
      this.items2.clear();
      this.method744();
   }

   public void method744() {
      this.text = "";
      this.intValue = 0;
   }

   public int method745() {
      return 2;
   }

   public boolean method746(CraftRecipe var1, String var2) {
      String var3 = this.method752(var1, var2);
      if (!var3.isBlank() && !var3.equals(this.text)) {
         this.text = var3;
         this.intValue = 2;
      }

      if (this.intValue > 0) {
         this.intValue--;
         return true;
      } else {
         return false;
      }
   }

   public boolean method747(CraftRecipe var1, String var2) {
      String var3 = this.method752(var1, var2);
      return !var3.isBlank() && this.items.contains(var3);
   }

   public boolean method748(CraftRecipe var1, String var2) {
      String var3 = this.method752(var1, var2);
      return !var3.isBlank() && this.items2.getOrDefault(var3, 0) >= 2;
   }

   public int method749(CraftRecipe var1, String var2) {
      String var3 = this.method752(var1, var2);
      if (var3.isBlank()) {
         return 0;
      } else {
         int var4 = this.items2.getOrDefault(var3, 0) + 1;
         this.items2.put(var3, var4);
         return var4;
      }
   }

   public void method750(CraftRecipe var1, String var2) {
      String var3 = this.method752(var1, var2);
      if (!var3.isBlank()) {
         this.items2.remove(var3);
         this.items.remove(var3);
      }
   }

   public void method751(CraftRecipe var1, String var2, String var3, String var4, boolean var5) {
      String var6 = this.method752(var1, var2);
      if (!var6.isBlank()) {
         boolean var7 = this.items.add(var6);
         this.items2.put(var6, 2);
         if (var5 && var7) {
            this.autoCraftModule.method1128("Source empty: " + this.autoCraftModule.method1107(var3, var4));
         }
      }
   }

   private String method752(CraftRecipe var1, String var2) {
      if (var1 != null && var2 != null && !var2.isBlank()) {
         Class1 var3 = var1.method5398(var2);
         long var4 = var3 != null && var3.method56() ? var3.method57().asLong() : Long.MIN_VALUE;
         return var1.getText2().toLowerCase(Locale.ROOT) + ":" + var1.getId().toLowerCase(Locale.ROOT) + ":" + var2 + ":" + var4;
      } else {
         return "";
      }
   }
}

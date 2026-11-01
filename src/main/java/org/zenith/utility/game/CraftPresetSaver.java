package org.zenith.utility.game;

import java.util.Locale;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.misc.CraftPresetData;

public final class CraftPresetSaver {
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final String text = "custom";
   private final AutoCraftModule autoCraftModule;
   private CraftPresetData craftPresetData = CraftPresetData.method6198();

   public CraftPresetSaver(AutoCraftModule var1) {
      this.autoCraftModule = var1;
   }

   public boolean isActive() {
      return !this.craftPresetData.method6199();
   }

   public String method3189() {
      return this.craftPresetData.method6199() ? "" : this.craftPresetData.method6205(this.autoCraftModule);
   }

   public void reset() {
      this.craftPresetData = CraftPresetData.method6198();
   }

   public void method3190() {
      String var1 = this.autoCraftModule.method1098();
      if (var1 == null || var1.isBlank()) {
         var1 = "custom";
      }

      this.autoCraftModule.method1112();
      this.craftPresetData = new CraftPresetData(var1);
      this.autoCraftModule.method1113();
      AutoCraftModule.mc.setScreen(null);
   }

   public boolean method3191() {
      if (this.craftPresetData.method6199()) {
         return false;
      } else if (AutoCraftModule.mc.player != null && AutoCraftModule.mc.world != null) {
         CraftingScreenHandler var1 = AutoCraftModule.mc.player.currentScreenHandler instanceof CraftingScreenHandler var2 ? var2 : null;
         if (var1 != null && this.craftPresetData.method6202() && this.method3193(var1)) {
            this.method3196();
            return true;
         } else if (var1 == null) {
            if (this.craftPresetData.method6202()) {
               if (this.method3193(null)) {
                  this.method3196();
                  return true;
               }

               this.craftPresetData.method6204();
            }

            return true;
         } else {
            ItemStack var4 = var1.getSlot(0).getStack();
            if (this.craftPresetData.method6202() && var4.isEmpty()) {
               if (this.method3193(var1)) {
                  this.method3196();
                  return true;
               } else {
                  this.craftPresetData.method6204();
                  return true;
               }
            } else {
               if (!var4.isEmpty() && !this.craftPresetData.method6202()) {
                  this.method3192(var1, var4);
               }

               if (this.craftPresetData.method6202() && this.method3193(var1)) {
                  this.method3196();
               }

               return true;
            }
         }
      } else {
         return true;
      }
   }

   private void method3192(CraftingScreenHandler var1, ItemStack var2) {
      String var3 = method3201(var2.getItem());
      if (!var3.isBlank()) {
         String var4 = this.method3195(var2);
         String[] var5 = new String[9];
         String[] var6 = new String[9];
         int var7 = 0;

         for (int var8 = 0; var8 < 9; var8++) {
            ItemStack var9 = var1.getSlot(1 + var8).getStack();
            if (var9.isEmpty()) {
               var5[var8] = "";
               var6[var8] = "";
            } else {
               var5[var8] = method3201(var9.getItem());
               var6[var8] = this.method3195(var9);
               if (!var5[var8].isBlank()) {
                  var7++;
               }
            }
         }

         if (var7 >= 2) {
            int var10 = this.method3194(var1, var3, var4);
            this.craftPresetData.method6203(var3, var4, var5, var6, var10);
         }
      }
   }

   private boolean method3193(CraftingScreenHandler var1) {
      if (!this.craftPresetData.method6199() && this.craftPresetData.method6202()) {
         int var2 = this.method3194(var1, this.craftPresetData.getText2(), this.craftPresetData.getText3());
         return var2 > this.craftPresetData.getIntValue();
      } else {
         return false;
      }
   }

   private int method3194(CraftingScreenHandler var1, String var2, String var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < 36; var5++) {
         ItemStack var6 = AutoCraftModule.mc.player.getInventory().getStack(var5);
         if (!var6.isEmpty() && this.autoCraftModule.method1073(var6, var2, var3)) {
            var4 += var6.getCount();
         }
      }

      if (var1 != null) {
         ItemStack var7 = var1.getCursorStack();
         if (!var7.isEmpty() && this.autoCraftModule.method1073(var7, var2, var3)) {
            var4 += var7.getCount();
         }
      }

      return var4;
   }

   private String method3195(ItemStack var1) {
      if (var1 == null || var1.isEmpty()) {
         return "";
      } else {
         return var1.get(DataComponentTypes.CUSTOM_NAME) == null ? "" : var1.getName().getString();
      }
   }

   private void method3196() {
      CraftRecipe var1 = this.method3197(this.craftPresetData);
      this.craftPresetData = CraftPresetData.method6198();
      if (!this.autoCraftModule.isEnabledRaw()) {
         this.autoCraftModule.method1114();
      }

      if (var1 != null) {
         this.autoCraftModule.method1127("Craft saved: " + var1.getText3());
      } else {
         this.autoCraftModule.method1128("Unable to save crafted preset");
      }
   }

   private CraftRecipe method3197(CraftPresetData var1) {
      if (var1 != null && !var1.method6199() && var1.method6202()) {
         String var2 = var1.getText();
         if (var2 == null || var2.isBlank()) {
            var2 = "custom";
         }

         String var3 = this.autoCraftModule.method1107(var1.getText2(), var1.getText3());
         String var4 = this.method3198(var2, var3);
         String var5 = this.method3200(var4, var1.getText2());
         String var6 = var5;
         int var7 = 1;

         while (this.autoCraftModule.method1094(var2, var6) != null) {
            var6 = var5 + "_" + var7++;
         }

         CraftRecipe var8 = new CraftRecipe(var6, var2, var4);
         var8.setFlag2(false);
         var8.setLocked(false);
         var8.method5403(var1.getText2());
         var8.method5404(var1.getText3());

         for (int var9 = 0; var9 < 9; var9++) {
            var8.method5390(var9, var1.method6200(var9));
            var8.method5392(var9, var1.method6201(var9));
         }

         this.autoCraftModule.method1104(var8);
         this.autoCraftModule.method1115(var2, var6);
         this.autoCraftModule.method1091();
         Zenith.getInstance().getManager().save();
         return var8;
      } else {
         return null;
      }
   }

   private String method3198(String var1, String var2) {
      String var3 = var2 == null ? "" : var2;
      if (var3.isBlank()) {
         var3 = "New Craft";
      }

      String var4 = var3;
      int var5 = 2;

      while (this.method3199(var1, var4)) {
         var4 = var3 + " " + var5++;
      }

      return var4;
   }

   private boolean method3199(String var1, String var2) {
      for (CraftRecipe var4 : this.autoCraftModule.method1092(var1)) {
         if (var4.getText3().equalsIgnoreCase(var2)) {
            return true;
         }
      }

      return false;
   }

   private String method3200(String var1, String var2) {
      String var3 = var1 == null ? "" : var1.toLowerCase(Locale.ROOT).replace(" ", "_").replaceAll("[^a-z0-9_]+", "");
      if (var3.isBlank()) {
         String var4 = var2 == null ? "" : var2;
         int var5 = var4.indexOf(58);
         if (var5 != -1 && var5 + 1 < var4.length()) {
            var4 = var4.substring(var5 + 1);
         }

         var3 = var4.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]+", "_");
      }

      if (var3.isBlank()) {
         var3 = "item";
      }

      return "captured_" + var3;
   }

   private static String method3201(Item var0) {
      return var0 != null && var0 != Items.AIR ? Registries.ITEM.getId(var0).toString() : "";
   }
}

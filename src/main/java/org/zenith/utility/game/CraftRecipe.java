package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.api.Class1;

public class CraftRecipe {
   private final String text;
   private final String text2;
   private String text3;
   private String text4 = "";
   private String text5 = "";
   private String text6 = "";
   private boolean flag;
   private boolean flag2;
   private boolean locked;
   private final List<String> items = new ArrayList<>();
   private final List<String> items2 = new ArrayList<>();
   private final Map<String, Class1> items3 = new HashMap<>();
   private Class1 class1 = Class1.INSTANCE;
   private Class1 class12 = Class1.INSTANCE;
   private ItemStack itemStack;
   private String text7;
   private boolean flag3;
   private transient Map<String, Integer> items4;

   public CraftRecipe(String var1, String var2, String var3) {
      this.text = var1;
      this.text2 = var2;
      this.text3 = var3;

      for (int var4 = 0; var4 < 9; var4++) {
         this.items.add("");
         this.items2.add("");
      }
   }

   public boolean method5386() {
      return this.items.stream().allMatch(String::isBlank);
   }

   public List<String> method5387() {
      ArrayList<String> var1 = new ArrayList<>();

      for (String var3 : this.items) {
         if (!var3.isBlank() && !var1.contains(var3)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public Map<String, Integer> method5388() {
      var var1 = this.items4;
      if (var1 != null) {
         return var1;
      } else {
         HashMap var2 = new HashMap();

         for (int var3 = 0; var3 < this.items.size(); var3++) {
            String var4 = this.items.get(var3);
            if (!var4.isBlank()) {
               String var5 = this.method5394(var3);
               var2.put(var5, ((Integer)var2.getOrDefault(var5, 0)) + 1);
            }
         }

         var1 = Collections.unmodifiableMap(var2);
         this.items4 = var1;
         return var1;
      }
   }

   public int method5389(String var1) {
      return this.method5388().getOrDefault(var1, 0);
   }

   public void method5390(int var1, String var2) {
      this.items.set(var1, var2 == null ? "" : var2);
      this.items4 = null;
   }

   public String method5391(int var1) {
      return this.items.get(var1);
   }

   public void method5392(int var1, String var2) {
      this.items2.set(var1, var2 == null ? "" : var2);
      this.items4 = null;
   }

   public String method5393(int var1) {
      return this.items2.get(var1);
   }

   public String method5394(int var1) {
      String var2 = this.method5391(var1);
      String var3 = this.method5393(var1);
      return var3.isBlank() ? var2 : var2 + "#" + var3;
   }

   public String method5395(String var1) {
      int var2 = var1.indexOf(35);
      return var2 == -1 ? var1 : var1.substring(0, var2);
   }

   public String method5396(String var1) {
      int var2 = var1.indexOf(35);
      return var2 == -1 ? "" : var1.substring(var2 + 1);
   }

   public String method5397(String var1, AutoCraftModule var2) {
      return var2.method1107(this.method5395(var1), this.method5396(var1));
   }

   public Class1 method5398(String var1) {
      return this.items3.get(var1);
   }

   public boolean method5399(ItemStack var1, AutoCraftModule var2) {
      return var2.method1073(var1, this.text4, this.text5);
   }

   public String method5400(AutoCraftModule var1) {
      return var1.method1107(this.text4, this.text5);
   }

   public ItemStack method5401(AutoCraftModule var1) {
      String var2 = this.text6.isBlank() ? this.text4 : this.text6;
      ItemStack var3 = this.itemStack;
      if (var3 != null && var2.equals(this.text7) && this.flag == this.flag3) {
         return var3;
      } else {
         Item var4 = var1.method1117(var2);
         ItemStack var5 = var4 == Items.AIR ? ItemStack.EMPTY : var4.getDefaultStack();
         if (!var5.isEmpty() && this.flag) {
            var5.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
         }

         this.itemStack = var5;
         this.text7 = var2;
         this.flag3 = this.flag;
         return var5;
      }
   }

   public String getId() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public void method5402(String var1) {
      this.text3 = var1 == null ? "" : var1;
   }

   public String getText4() {
      return this.text4;
   }

   public void method5403(String var1) {
      this.text4 = var1 == null ? "" : var1;
   }

   public String getText5() {
      return this.text5;
   }

   public void method5404(String var1) {
      this.text5 = var1 == null ? "" : var1;
   }

   public void method5405(String var1) {
      this.text6 = var1 == null ? "" : var1;
   }

   public void setFlag(boolean var1) {
      this.flag = var1;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean method5406() {
      return !this.flag2;
   }

   public void setFlag2(boolean var1) {
      this.flag2 = var1;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   public List<String> getItems() {
      return this.items;
   }

   public List<String> getItems2() {
      return this.items2;
   }

   public Map<String, Class1> getItems3() {
      return this.items3;
   }

   public Class1 getClass1() {
      return this.class1;
   }

   public void method5407(Class1 var1) {
      this.class1 = var1 == null ? Class1.INSTANCE : var1;
   }

   public Class1 getClass12() {
      return this.class12;
   }

   public void method5408(Class1 var1) {
      this.class12 = var1 == null ? Class1.INSTANCE : var1;
   }
}

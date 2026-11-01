package org.zenith.utility.game;

import lombok.Generated;
import net.minecraft.item.ItemStack;

public class ItemEntry {
   private final ItemStack itemStack;
   private final String text;
   private final int intValue;
   private final long longValue;
   private final long longValue2;

   public ItemEntry(ItemStack var1, String var2, int var3, long var4) {
      this.itemStack = var1 == null ? ItemStack.EMPTY : var1.copy();
      this.text = var2 == null ? "" : var2;
      this.intValue = var3;
      this.longValue = var4;
      this.longValue2 = System.currentTimeMillis();
   }

   @Generated
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public long getLongValue2() {
      return this.longValue2;
   }
}

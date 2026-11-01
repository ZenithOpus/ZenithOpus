package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.item.ItemStack;

public abstract class EnchantMatcher {
   public final String text;
   public final String text2;
   public int intValue;

   public abstract boolean method1746(ItemStack var1);

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + " [checked=" + this.text2 + ", level=" + this.intValue + "]";
   }

   @Generated
   public EnchantMatcher(String var1, String var2, int var3) {
      this.text = var1;
      this.text2 = var2;
      this.intValue = var3;
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public void setIntValue(int var1) {
      this.intValue = var1;
   }
}

package org.zenith.utility.game;

import lombok.Generated;
import net.minecraft.item.ItemStack;
import org.zenith.ZenithAccess;

public class ItemMatcher implements ZenithAccess {
   public ItemStack itemStack;
   public final String text;
   public final String text2;
   public final ItemMatcher.Type type;

   public ItemMatcher(ItemStack var1, String var2, ItemMatcher.Type var3) {
      this.itemStack = var1;
      this.text = var2;
      this.text2 = var2;
      this.type = var3;
   }

   public ItemMatcher(ItemStack var1, String var2, String var3, ItemMatcher.Type var4) {
      this.itemStack = var1;
      this.text = var2;
      this.text2 = var3;
      this.type = var4;
   }

   public boolean method2672(ItemStack var1) {
      return var1 != null && var1.getItem() == this.itemStack.getItem();
   }

   @Generated
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Generated
   public String getText2() {
      return this.text;
   }

   @Generated
   public String getText22() {
      return this.text2;
   }

   @Generated
   public ItemMatcher.Type getType() {
      return this.type;
   }

   public static enum Type {
      FUNTIME,
      HOLLYWORLD,
      ANY;
   }
}

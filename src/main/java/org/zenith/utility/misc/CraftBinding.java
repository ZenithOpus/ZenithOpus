package org.zenith.utility.misc;

import org.zenith.client.modules.impl.misc.AutoCraftModule;

public final class CraftBinding {
   private final CraftBinding.Type type;
   private final String text;
   private final String text2;
   private final String text3;

   public CraftBinding.Type type() {
      return this.type;
   }

   public String text() {
      return this.text;
   }

   public String text2() {
      return this.text2;
   }

   public String text3() {
      return this.text3;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CraftBinding other)) return false;
      return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(type, text, text2, text3);
   }

   @Override
   public String toString() {
      return "CraftBinding[" + "type=" + this.type + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
   }

   public CraftBinding(CraftBinding.Type var1, String var2, String var3, String var4) {
      this.type = var1;
      this.text = var2;
      this.text2 = var3;
      this.text3 = var4;
   }

   public static CraftBinding method1685() {
      return new CraftBinding(null, "", "", "");
   }

   public boolean method1686() {
      return this.type == null;
   }

   public String method1687(AutoCraftModule var1) {
      if (this.type == CraftBinding.Type.SOURCE) {
         return "Нажмите ПКМ по хранилищу с " + var1.method1108(this.text3) + " для автокрафта";
      } else if (this.type == CraftBinding.Type.OUTPUT) {
         return "Нажмите ПКМ по сундуку склада для автокрафта";
      } else {
         return this.type == CraftBinding.Type.WORKBENCH ? "Нажмите ПКМ по верстаку для автокрафта" : "Нажмите ПКМ по блоку для привязки";
      }
   }

   public CraftBinding.Type getType() {
      return this.type;
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

   public static enum Type {
      SOURCE,
      OUTPUT,
      WORKBENCH;
   }
}

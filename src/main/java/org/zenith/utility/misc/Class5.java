package org.zenith.utility.misc;

import java.util.Arrays;

public final class Class5 {
   private final int intValue;
   private byte byteValue;
   private byte[] byteValues;

   public Class5(int var1, byte var2) {
      this.intValue = var1;
      this.byteValue = var2;
   }

   public byte method521(int var1, int var2, int var3) {
      return this.byteValues == null ? this.byteValue : this.byteValues[this.method527(var1, var2, var3)];
   }

   public void method522(int var1, int var2, int var3, byte var4) {
      if (this.byteValues == null) {
         if (var4 == this.byteValue) {
            return;
         }

         this.byteValues = new byte[this.intValue * this.intValue * this.intValue];
         Arrays.fill(this.byteValues, this.byteValue);
      }

      this.byteValues[this.method527(var1, var2, var3)] = var4;
   }

   public boolean method523() {
      return this.byteValues == null;
   }

   public byte getByteValue() {
      return this.byteValue;
   }

   public boolean method524() {
      if (this.byteValues == null) {
         return true;
      } else {
         byte var1 = this.byteValues[0];

         for (int var5 : this.byteValues) {
            if (var5 != var1) {
               return false;
            }
         }

         this.byteValue = var1;
         this.byteValues = null;
         return true;
      }
   }

   public int method525(byte var1) {
      if (this.byteValues == null) {
         return this.byteValue > var1 ? this.intValue * this.intValue * this.intValue : 0;
      } else {
         int var2 = 0;

         for (int var6 : this.byteValues) {
            if (var6 > var1) {
               var2++;
            }
         }

         return var2;
      }
   }

   public long method526() {
      return this.byteValues == null ? 16L : 16L + this.byteValues.length;
   }

   private int method527(int var1, int var2, int var3) {
      return (var2 * this.intValue + var3) * this.intValue + var1;
   }
}

package org.zenith.utility.misc;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class Canvas {
   public static final int CONST_intValue = 31;
   public static final int CONST_intValue2 = 15;
   private static final int CONST_intValue3 = 48;
   private int[] intValues = new int[961];
   private final Deque<int[]> deque = new ArrayDeque<>();
   private final Deque<int[]> deque2 = new ArrayDeque<>();

   public int method136(int var1, int var2) {
      return var1 >= 0 && var2 >= 0 && var1 < 31 && var2 < 31 ? this.intValues[var2 * 31 + var1] : 0;
   }

   public void method137(int var1, int var2, int var3) {
      if (var1 >= 0 && var2 >= 0 && var1 < 31 && var2 < 31) {
         this.intValues[var2 * 31 + var1] = var3;
      }
   }

   public boolean isEmpty() {
      for (int var4 : this.intValues) {
         if (var4 >>> 24 != 0) {
            return false;
         }
      }

      return true;
   }

   public int[] method138() {
      return (int[])this.intValues.clone();
   }

   public void method139(int[] var1) {
      if (var1 != null && var1.length == this.intValues.length) {
         System.arraycopy(var1, 0, this.intValues, 0, this.intValues.length);
      }
   }

   public void method140() {
      this.method141();
      this.deque2.clear();
   }

   public void clear() {
      this.method141();
      this.deque2.clear();
      Arrays.fill(this.intValues, 0);
   }

   private void method141() {
      this.deque.push((int[])this.intValues.clone());

      while (this.deque.size() > 48) {
         this.deque.removeLast();
      }
   }

   public boolean method142() {
      return !this.deque.isEmpty();
   }

   public boolean method143() {
      return !this.deque2.isEmpty();
   }

   public void undo() {
      if (!this.deque.isEmpty()) {
         this.deque2.push((int[])this.intValues.clone());
         this.intValues = this.deque.pop();
      }
   }

   public void redo() {
      if (!this.deque2.isEmpty()) {
         this.deque.push((int[])this.intValues.clone());
         this.intValues = this.deque2.pop();
      }
   }

   public void method144(int var1, int var2, int var3) {
      int var4 = this.method136(var1, var2);
      if (var4 != var3) {
         this.method140();
         ArrayDeque var5 = new ArrayDeque();
         var5.push(new int[]{var1, var2});

         while (!var5.isEmpty()) {
            int[] var6 = (int[])var5.pop();
            int var7 = var6[0];
            int var8 = var6[1];
            if (var7 >= 0 && var8 >= 0 && var7 < 31 && var8 < 31 && this.intValues[var8 * 31 + var7] == var4) {
               this.intValues[var8 * 31 + var7] = var3;
               var5.push(new int[]{var7 + 1, var8});
               var5.push(new int[]{var7 - 1, var8});
               var5.push(new int[]{var7, var8 + 1});
               var5.push(new int[]{var7, var8 - 1});
            }
         }
      }
   }

   public String method145() {
      StringBuilder var1 = new StringBuilder();
      int var2 = this.intValues[0];
      int var3 = 1;

      for (int var4 = 1; var4 < this.intValues.length; var4++) {
         if (this.intValues[var4] == var2) {
            var3++;
         } else {
            method146(var1, var3, var2);
            var2 = this.intValues[var4];
            var3 = 1;
         }
      }

      method146(var1, var3, var2);
      return var1.toString();
   }

   private static void method146(StringBuilder var0, int var1, int var2) {
      if (var0.length() > 0) {
         var0.append(';');
      }

      var0.append(var1).append(':').append(Integer.toHexString(var2));
   }

   public void method147(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         int[] var2 = new int[this.intValues.length];
         int var3 = 0;

         try {
            for (String var7 : var1.split(";")) {
               int var8 = var7.indexOf(58);
               if (var8 <= 0) {
                  return;
               }

               int var9 = Integer.parseInt(var7.substring(0, var8));
               int var10 = (int)Long.parseLong(var7.substring(var8 + 1), 16);

               for (int var11 = 0; var11 < var9 && var3 < var2.length; var11++) {
                  var2[var3++] = var10;
               }
            }
         } catch (RuntimeException var12) {
            return;
         }

         if (var3 == var2.length) {
            this.intValues = var2;
            this.deque.clear();
            this.deque2.clear();
         }
      }
   }
}

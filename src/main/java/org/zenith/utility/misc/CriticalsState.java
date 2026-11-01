package org.zenith.utility.misc;

import java.util.ArrayDeque;
import java.util.Arrays;

public final class CriticalsState {
   private static final double CONST_doubleValue = 5.0E7;
   private static final double CONST_doubleValue2 = 5.0E7;
   private static final double CONST_doubleValue3 = 1.0E9;
   private static final double CONST_doubleValue4 = 2.0E7;
   private static final double CONST_doubleValue5 = 5.0E9;
   private static final double CONST_doubleValue6 = 0.2;
   private static final double CONST_doubleValue7 = 0.2;
   private static final double CONST_doubleValue8 = 0.2;
   private static final double CONST_doubleValue9 = 2.0;
   private static final int CONST_intValue = 9;
   private final ArrayDeque<Double> arrayDeque = new ArrayDeque<>(9);
   private double doubleValue = 5.0E7;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private long longValue;
   private long longValue2;
   private long longValue3 = Long.MIN_VALUE;
   private boolean flag;

   public synchronized void method1706(long var1, long var3) {
      this.method1714(var3);
      if (this.longValue2 != 0L && var1 > this.longValue3) {
         long var5 = var3 - this.longValue2;
         long var7 = var1 - this.longValue3;
         double var9 = (double)var5 / var7;
         if (var5 > 0L && Double.isFinite(var9) && var9 >= 2.0E7 && var9 <= 5.0E9) {
            this.method1715(var9);
            double var11 = this.method1716();
            this.doubleValue2 = this.doubleValue2 + (Math.abs(var9 - var11) - this.doubleValue2) * 0.2;
            double var13 = method1717(var11, 5.0E7, 1.0E9);
            double var15 = method1717(Math.max(var13, var9 - this.doubleValue2), 5.0E7, 1.0E9);
            if (var15 > this.doubleValue) {
               this.doubleValue = var15;
            } else {
               this.doubleValue = this.doubleValue + (var13 - this.doubleValue) * 0.2;
            }
         }
      } else if (this.longValue3 != Long.MIN_VALUE && var1 <= this.longValue3) {
         this.arrayDeque.clear();
         this.doubleValue2 = 0.0;
      }

      this.longValue3 = var1;
      this.longValue2 = var3;
   }

   public synchronized void method1707(long var1) {
      this.method1714(var1);
      this.doubleValue4 = this.doubleValue3;
      this.flag = true;
   }

   public synchronized boolean method1708(double var1, double var3) {
      return this.flag && !(var1 <= 0.0) ? this.method1713(System.nanoTime()) + Math.max(0.0, var3) >= var1 : true;
   }

   public synchronized long method1709(double var1, double var3) {
      if (this.flag && !(var1 <= 0.0)) {
         double var5 = var1 - this.method1713(System.nanoTime()) - Math.max(0.0, var3);
         if (var5 <= 0.0) {
            return 0L;
         } else {
            double var7 = var5 * this.doubleValue;
            return var7 >= 9.223372E18F ? Long.MAX_VALUE : (long)Math.ceil(var7);
         }
      } else {
         return 0L;
      }
   }

   public synchronized double method1710() {
      if (!this.flag) {
         return Double.POSITIVE_INFINITY;
      } else {
         this.method1714(System.nanoTime());
         return Math.max(0.0, this.doubleValue3 - this.doubleValue4);
      }
   }

   public synchronized double method1711() {
      return !this.flag ? Double.POSITIVE_INFINITY : this.method1713(System.nanoTime());
   }

   public synchronized double getDoubleValue() {
      return this.doubleValue;
   }

   public synchronized float method1712() {
      return (float)Math.min(20.0, 1.0E9 / this.doubleValue);
   }

   public synchronized void reset() {
      this.arrayDeque.clear();
      this.doubleValue = 5.0E7;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.flag = false;
      this.doubleValue4 = 0.0;
      this.longValue = 0L;
      this.longValue2 = 0L;
      this.longValue3 = Long.MIN_VALUE;
   }

   private double method1713(long var1) {
      this.method1714(var1);
      double var3 = Math.max(0.0, this.doubleValue3 - this.doubleValue4);
      double var5 = 0.2 + this.doubleValue2 / this.doubleValue;
      return Math.max(0.0, var3 - method1717(var5, 0.2, 2.0));
   }

   private void method1714(long var1) {
      if (this.longValue != 0L && var1 > this.longValue) {
         this.doubleValue3 = this.doubleValue3 + (var1 - this.longValue) / this.doubleValue;
      }

      this.longValue = var1;
   }

   private void method1715(double var1) {
      if (this.arrayDeque.size() >= 9) {
         this.arrayDeque.removeFirst();
      }

      this.arrayDeque.addLast(var1);
   }

   private double method1716() {
      double[] var1 = new double[this.arrayDeque.size()];
      int var2 = 0;

      for (double var4 : this.arrayDeque) {
         var1[var2++] = var4;
      }

      Arrays.sort(var1);
      int var6 = var1.length / 2;
      return var1.length % 2 == 0 ? (var1[var6 - 1] + var1[var6]) * 0.5 : var1[var6];
   }

   private static double method1717(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }
}

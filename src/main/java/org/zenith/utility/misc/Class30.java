package org.zenith.utility.misc;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class Class30 {
   private final StopWatch stopWatch = new StopWatch();
   private final List<Class30.Entry3> items = Lists.newCopyOnWriteArrayList();
   private final List<Class30.Entry4> items2 = Lists.newCopyOnWriteArrayList();
   private int intValue;
   private int intValue2;
   private boolean flag;
   private Class30.Handler handler = new Class30.Entry(1);

   public Class30() {
      this.method4722();
   }

   public Class30 method4711(int var1, Handler5 var2) {
      return this.method4714(var1, var2, () -> true, 0);
   }

   public Class30 method4712(int var1, Handler5 var2, BooleanSupplier var3) {
      return this.method4714(var1, var2, var3, 0);
   }

   public Class30 method4713(int var1, Handler5 var2, int var3) {
      return this.method4714(var1, var2, () -> true, var3);
   }

   public Class30 method4714(int var1, Handler5 var2, BooleanSupplier var3, int var4) {
      this.items.add(new Class30.Entry3(var1, var2, var3, var4));
      Collections.sort(this.items);
      return this;
   }

   public Class30 method4715(int var1, Handler5 var2) {
      return this.method4718(var1, var2, () -> true, 0);
   }

   public Class30 method4716(int var1, Handler5 var2, BooleanSupplier var3) {
      return this.method4718(var1, var2, var3, 0);
   }

   public Class30 method4717(int var1, Handler5 var2, int var3) {
      return this.method4718(var1, var2, () -> true, var3);
   }

   public Class30 method4718(int var1, Handler5 var2, BooleanSupplier var3, int var4) {
      this.items2.add(new Class30.Entry4(var1, var2, var3, var4));
      Collections.sort(this.items2);
      return this;
   }

   public void method4719() {
      this.stopWatch.reset();
   }

   public void method4720() {
      this.intValue = 0;
      this.intValue2 = 0;
   }

   public Class30 method4721() {
      if (this.method4724()) {
         this.method4722();
      }

      return this;
   }

   public Class30 method4722() {
      this.items.clear();
      this.items2.clear();
      this.method4719();
      this.method4720();
      return this;
   }

   public void update() {
      if ((!this.items.isEmpty() || !this.items2.isEmpty()) && !this.flag) {
         this.items.forEach(var1 -> {
            if (this.intValue < this.items.size()) {
               Class30.Entry3 var2 = this.items.get(this.intValue);
               if (var2.getBooleanSupplier().getAsBoolean() && this.stopWatch.method317(var2.getIntValue())) {
                  var2.getHandler5().method4767();
                  this.intValue++;
                  this.method4719();
                  if (this.handler.method4725(this.intValue, this.items.size())) {
                     this.method4720();
                     this.handler.method4726();
                  }
               }
            }
         });
         this.items2.forEach(var1 -> {
            if (this.intValue2 < this.items2.size()) {
               Class30.Entry4 var2 = this.items2.get(this.intValue2);
               if (var2.getBooleanSupplier().getAsBoolean() && var2.getTicks() <= 0) {
                  var2.getHandler5().method4767();
                  this.intValue2++;
                  this.method4719();
                  if (this.handler.method4725(this.intValue2, this.items2.size())) {
                     this.method4720();
                     this.handler.method4726();
                  }
               }

               var2.method4734();
            }
         });
         this.intValue = Math.min(this.intValue, this.items.size());
         this.intValue2 = Math.min(this.intValue2, this.items2.size());
      }
   }

   public Class30 method4723(Class30.Handler var1) {
      this.handler = var1;
      return this;
   }

   public boolean method4724() {
      return this.intValue >= this.items.size() && this.intValue2 >= this.items2.size() && !this.flag && this.handler.method4727();
   }

   @Generated
   public StopWatch getStopWatch() {
      return this.stopWatch;
   }

   @Generated
   public List<Class30.Entry3> getItems() {
      return this.items;
   }

   @Generated
   public List<Class30.Entry4> getItems2() {
      return this.items2;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public Class30.Handler getHandler() {
      return this.handler;
   }

   @Generated
   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   @Generated
   public void setIntValue2(int var1) {
      this.intValue2 = var1;
   }

   @Generated
   public void setFlag(boolean var1) {
      this.flag = var1;
   }

   public static class Entry implements Class30.Handler {
      private final int intValue;
      private int intValue2;

      public Entry(int var1) {
         this.intValue = var1 - 1;
      }

      @Override
      public boolean method4725(int var1, int var2) {
         return var1 >= var2 && this.intValue2 < this.intValue;
      }

      @Override
      public void method4726() {
         this.intValue2++;
      }

      @Override
      public boolean method4727() {
         return this.intValue2 >= this.intValue;
      }
   }

   public static class Entry2 implements Class30.Handler {
      @Override
      public boolean method4725(int var1, int var2) {
         return var1 >= var2;
      }

      @Override
      public void method4726() {
      }

      @Override
      public boolean method4727() {
         return false;
      }
   }

   public static final class Entry3 implements Comparable<Class30.Entry3> {
      private int intValue;
      private Handler5 handler5;
      private BooleanSupplier booleanSupplier;
      private int intValue2;

      public Entry3(int var1, Handler5 var2, BooleanSupplier var3, int var4) {
         this.intValue = var1;
         this.handler5 = var2;
         this.booleanSupplier = var3;
         this.intValue2 = var4;
      }

      public int compareTo(Class30.Entry3 var1) {
         return Integer.compare(var1.getIntValue2(), this.getIntValue2());
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }

      @Generated
      public Handler5 getHandler5() {
         return this.handler5;
      }

      @Generated
      public BooleanSupplier getBooleanSupplier() {
         return this.booleanSupplier;
      }

      @Generated
      public int getIntValue2() {
         return this.intValue2;
      }

      @Generated
      public Class30.Entry3 method4729(int var1) {
         this.intValue = var1;
         return this;
      }

      @Generated
      public Class30.Entry3 method4730(Handler5 var1) {
         this.handler5 = var1;
         return this;
      }

      @Generated
      public Class30.Entry3 method4731(BooleanSupplier var1) {
         this.booleanSupplier = var1;
         return this;
      }

      @Generated
      public Class30.Entry3 method4732(int var1) {
         this.intValue2 = var1;
         return this;
      }
   }

   public static final class Entry4 implements Comparable<Class30.Entry4> {
      private int ticks;
      private Handler5 handler5;
      private BooleanSupplier booleanSupplier;
      private int intValue;

      public Entry4(int var1, Handler5 var2, BooleanSupplier var3, int var4) {
         this.ticks = var1;
         this.handler5 = var2;
         this.booleanSupplier = var3;
         this.intValue = var4;
      }

      public int compareTo(Class30.Entry4 var1) {
         return Integer.compare(var1.getIntValue(), this.getIntValue());
      }

      public void method4734() {
         this.ticks--;
      }

      @Generated
      public int getTicks() {
         return this.ticks;
      }

      @Generated
      public Handler5 getHandler5() {
         return this.handler5;
      }

      @Generated
      public BooleanSupplier getBooleanSupplier() {
         return this.booleanSupplier;
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }

      @Generated
      public Class30.Entry4 method4735(int var1) {
         this.ticks = var1;
         return this;
      }

      @Generated
      public Class30.Entry4 method4736(Handler5 var1) {
         this.handler5 = var1;
         return this;
      }

      @Generated
      public Class30.Entry4 method4737(BooleanSupplier var1) {
         this.booleanSupplier = var1;
         return this;
      }

      @Generated
      public Class30.Entry4 method4738(int var1) {
         this.intValue = var1;
         return this;
      }
   }

   public interface Handler {
      boolean method4725(int var1, int var2);

      void method4726();

      boolean method4727();
   }
}

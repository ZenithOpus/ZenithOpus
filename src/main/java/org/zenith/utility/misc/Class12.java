package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public final class Class12<C> {
   public static final int CONST_intValue = -100;
   public static final int CONST_intValue2 = 0;
   public static final int CONST_intValue3 = 100;
   private final String text;
   private final String text2;
   private final Class<C> classValue;
   private volatile Class12.Data<C>[] datas = method1644();
   private long longValue;

   private static <C> Class12.Data<C>[] method1644() {
      return new Class12.Data[0];
   }

   public Class12(String var1, Class<C> var2, String var3) {
      this.text = var1;
      this.classValue = var2;
      this.text2 = var3;
   }

   public String name() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public Class<C> getClassValue() {
      return this.classValue;
   }

   public boolean active() {
      return this.datas.length != 0;
   }

   public C method1645(C var1) {
      for (Class12.Data var5 : this.datas) {
         var5.handler.handle((C)var1);
      }

      return (C)var1;
   }

   public synchronized Runnable method1646(int var1, Class12.Handler<C> var2) {
      Class12.Data var3 = new Class12.Data(var1, this.longValue++, var2);
      var var4 = new ArrayList<>(Arrays.asList(this.datas));
      var4.add(var3);
      var4.sort(Comparator.<Class12.Data<?>>comparingInt(d -> d.getIntValue()).thenComparingLong(d -> d.getLongValue()));
      this.datas = (Class12.Data[])var4.toArray(new Class12.Data[0]);
      return () -> this.method1647(var3);
   }

   private synchronized void method1647(Class12.Data<C> var1) {
      var var2 = new ArrayList<>(Arrays.asList(this.datas));
      if (var2.remove(var1)) {
         this.datas = (Class12.Data[])var2.toArray(new Class12.Data[0]);
      }
   }

   private static final class Data<C> {
      private final int intValue;
      private final long longValue;
      private final Class12.Handler<C> handler;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      public Class12.Handler<C> handler() {
         return this.handler;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data<?> other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.handler, other.handler);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue, handler);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "handler=" + this.handler + "]";
      }

      private Data(int var1, long var2, Class12.Handler<C> var4) {
         this.intValue = var1;
         this.longValue = var2;
         this.handler = var4;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public Class12.Handler<C> getHandler() {
         return this.handler;
      }
   }

   @FunctionalInterface
   public interface Handler<C> {
      void handle(C var1);
   }
}

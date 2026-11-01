package org.zenith.utility.misc;

import java.util.HashMap;
import java.util.Map;

public final class AnimationCache {
   private static final long CONST_longValue = 10000000000L;
   private static final long CONST_longValue2 = 5000000000L;
   private static final Map<Long, AnimationCache.Entry> items = new HashMap<>();
   private static long lastPrune;

   private AnimationCache() {
   }

   public static AnimationCache.Entry method7621(int var0, HoveredData var1, boolean var2) {
      return method7622(var0, var1.getText(), var2);
   }

   public static AnimationCache.Entry method7622(int var0, String var1, boolean var2) {
      long var3 = System.nanoTime();
      method7623(var3);
      long var5 = (long)var0 << 32 ^ var1.hashCode() & 4294967295L ^ (var2 ? 6510615553911029760L : 0L);
      AnimationCache.Entry var7 = items.computeIfAbsent(var5, var0x -> new AnimationCache.Entry());
      var7.longValue = var3;
      return var7;
   }

   public static void clear() {
      items.clear();
   }

   private static void method7623(long var0) {
      if (var0 - lastPrune >= 5000000000L) {
         lastPrune = var0;
         items.entrySet().removeIf(var2 -> var0 - var2.getValue().longValue > 10000000000L);
      }
   }

   public static final class Entry {
      public final AnimationState animationState = new AnimationState();
      public final Class16 class16 = new Class16();
      public final Class2 class2 = new Class2();
      public float[] floatValues = new float[0];
      public float floatValue;
      public long longValue;
      public long longValue2;
      public float floatValue2 = Float.NaN;
      public float floatValue3;

      public float method7624(long var1) {
         float var3 = this.longValue2 == 0L ? 0.0F : (float)(var1 - this.longValue2) / 1.0E9F;
         this.longValue2 = var1;
         return Math.max(0.0F, Math.min(var3, 0.25F));
      }

      public float[] method7625(int var1) {
         if (this.floatValues.length != var1) {
            this.floatValues = new float[var1];
         }

         return this.floatValues;
      }
   }
}

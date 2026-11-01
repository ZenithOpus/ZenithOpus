package org.zenith.utility.game;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import org.zenith.utility.misc.FlakeUtil;

public final class Util11 {
   private static final long CONST_longValue = 15000L;
   private static final int CONST_intValue = 12;
   public static final long CONST_longValue2 = 900L;
   private static final int CONST_intValue2 = 96;
   private static final int CONST_intValue3 = 4096;
   private static final Long2ObjectOpenHashMap<Util11.Entry> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   private static final List<Util11.Data> items = new ArrayList<>();
   private static final LongArrayList longArrayList = new LongArrayList();
   private static long longValue;

   private Util11() {
   }

   public static void method2330(World var0, BlockPos var1, BlockState var2, BlockState var3) {
      boolean var4 = !var2.isAir();
      boolean var5 = !var3.isAir();
      if (var4 != var5) {
         int var6 = var1.getX();
         int var7 = var1.getZ();
         if (var0.isChunkLoaded(var6 >> 4, var7 >> 4)) {
            int var8 = var0.getTopY(Type.MOTION_BLOCKING, var6, var7);
            long var9 = System.currentTimeMillis();
            if (var5) {
               if (var8 != var1.getY() + 1) {
                  return;
               }

               Util11.Entry var11 = method2338(var6, var7);
               var11.longValue = var9;
               var11.doubleValue = var8;
               longValue++;
            } else {
               if (var8 > var1.getY()) {
                  return;
               }

               float var12 = FlakeUtil.method7509(var6 + 0.5, var7 + 0.5);
               if (var12 <= 0.02F) {
                  long2ObjectOpenHashMap.remove(method2339(var6, var7));
                  longArrayList.add(method2339(var6, var7));
                  return;
               }

               long2ObjectOpenHashMap.remove(method2339(var6, var7));
               if (items.size() >= 96) {
                  items.remove(0);
               }

               items.add(new Util11.Data(var6, var7, var1.getY() + 1 + var12, var8, var9));
               longValue++;
            }

            longArrayList.add(method2339(var6, var7));
         }
      }
   }

   public static void method2331(long var0) {
      if (!items.isEmpty()) {
         items.removeIf(var2 -> var0 - var2.getLongValue() > 900L);
      }

      if (!long2ObjectOpenHashMap.isEmpty()) {
         long2ObjectOpenHashMap.values().removeIf(var2 -> {
            if (var0 - var2.longValue < 15000L) {
               return false;
            } else {
               longValue++;
               return true;
            }
         });
      }
   }

   public static void method2332(World var0, FlakeState var1) {
      if (!longArrayList.isEmpty()) {
         for (int var2 = 0; var2 < longArrayList.size(); var2++) {
            long var3 = longArrayList.getLong(var2);
            var1.method4538(var0, (int)(var3 >> 32), (int)var3);
         }

         longArrayList.clear();
      }
   }

   public static List<Util11.Data> getItems() {
      return items;
   }

   public static long method2333(long var0) {
      long var2 = longValue;
      ObjectIterator var4 = long2ObjectOpenHashMap.values().iterator();

      while (var4.hasNext()) {
         Util11.Entry var5 = (Util11.Entry)var4.next();
         var2 = var2 * 31L + method2337(var5, var0);
      }

      return var2;
   }

   public static float method2334(int var0, int var1, double var2, long var4) {
      if (long2ObjectOpenHashMap.isEmpty()) {
         return 1.0F;
      } else {
         Util11.Entry var6 = (Util11.Entry)long2ObjectOpenHashMap.get(method2339(var0, var1));
         if (var6 == null) {
            return 1.0F;
         } else {
            return Math.abs(var6.doubleValue - var2) > 0.51 ? 1.0F : Math.clamp(method2337(var6, var4) / 12.0F, 0.0F, 1.0F);
         }
      }
   }

   public static boolean method2335() {
      return !long2ObjectOpenHashMap.isEmpty();
   }

   public static void method2336() {
      long2ObjectOpenHashMap.clear();
      items.clear();
      longArrayList.clear();
      longValue++;
   }

   private static int method2337(Util11.Entry var0, long var1) {
      long var3 = var1 - var0.longValue;
      if (var3 <= 0L) {
         return 0;
      } else if (var3 >= 15000L) {
         return 12;
      } else {
         float var5 = (float)var3 / 15000.0F;
         return (int)(var5 * var5 * 12.0F);
      }
   }

   private static Util11.Entry method2338(int var0, int var1) {
      long var2 = method2339(var0, var1);
      Util11.Entry var4 = (Util11.Entry)long2ObjectOpenHashMap.get(var2);
      if (var4 != null) {
         return var4;
      } else {
         if (long2ObjectOpenHashMap.size() >= 4096) {
            long2ObjectOpenHashMap.clear();
         }

         var4 = new Util11.Entry();
         long2ObjectOpenHashMap.put(var2, var4);
         return var4;
      }
   }

   private static long method2339(int var0, int var1) {
      return (long)var0 << 32 | var1 & 4294967295L;
   }

   public static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final double doubleValue4;
      private final long longValue;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public double doubleValue4() {
         return this.doubleValue4;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.doubleValue4, other.doubleValue4) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, doubleValue4, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "doubleValue4=" + this.doubleValue4 + ", " + "longValue=" + this.longValue + "]";
      }

      public Data(double var1, double var3, double var5, double var7, long var9) {
         this.doubleValue = var1;
         this.doubleValue2 = var3;
         this.doubleValue3 = var5;
         this.doubleValue4 = var7;
         this.longValue = var9;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public double getDoubleValue4() {
         return this.doubleValue4;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private long longValue;
      private double doubleValue;
   }
}

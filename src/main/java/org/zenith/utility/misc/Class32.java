package org.zenith.utility.misc;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public final class Class32 {
   public static final int CONST_intValue = 4;
   public static final int CONST_intValue2 = 16;
   public static final int CONST_intValue3 = 64;
   public static final byte CONST_byteValue = -127;
   public static final byte CONST_byteValue2 = 127;
   public static final byte CONST_byteValue3 = 0;
   private final Long2ObjectMap<Class5> long2ObjectMap = new Long2ObjectOpenHashMap();

   public byte method4902(int var1, int var2, int var3) {
      Class5 var4 = (Class5)this.long2ObjectMap.get(method4914(method4912(var1), method4912(var2), method4912(var3)));
      return var4 == null ? -127 : var4.method521(method4913(var1), method4913(var2), method4913(var3));
   }

   public float method4903(int var1, int var2, int var3) {
      return this.method4902(var1, var2, var3) / 127.0F;
   }

   public void method4904(int var1, int var2, int var3, byte var4) {
      long var5 = method4914(method4912(var1), method4912(var2), method4912(var3));
      Class5 var7 = (Class5)this.long2ObjectMap.get(var5);
      if (var7 == null) {
         if (var4 == -127) {
            return;
         }

         var7 = new Class5(64, (byte)-127);
         this.long2ObjectMap.put(var5, var7);
      }

      var7.method522(method4913(var1), method4913(var2), method4913(var3), var4);
   }

   public boolean method4905(int var1, int var2, int var3) {
      return this.long2ObjectMap.containsKey(method4914(var1, var2, var3));
   }

   public int method4906() {
      return this.long2ObjectMap.size();
   }

   public int method4907() {
      int var1 = 0;
      ObjectIterator var2 = this.long2ObjectMap.values().iterator();

      while (var2.hasNext()) {
         Class5 var3 = (Class5)var2.next();
         if (var3.method523()) {
            var1++;
         }
      }

      return var1;
   }

   public long method4908() {
      long var1 = 0L;
      ObjectIterator var3 = this.long2ObjectMap.values().iterator();

      while (var3.hasNext()) {
         Class5 var4 = (Class5)var3.next();
         var1 += var4.method525((byte)0);
      }

      return var1;
   }

   public long method4909() {
      long var1 = 0L;
      ObjectIterator var3 = this.long2ObjectMap.values().iterator();

      while (var3.hasNext()) {
         Class5 var4 = (Class5)var3.next();
         var1 += var4.method526();
      }

      return var1;
   }

   public int method4910() {
      LongArrayList var1 = new LongArrayList();
      int var2 = 0;
      ObjectIterator var3 = this.long2ObjectMap.long2ObjectEntrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Class5 var5 = (Class5)var4.getValue();
         boolean var6 = var5.method523();
         if (var5.method524() && !var6) {
            var2++;
         }

         if (var5.method523() && var5.getByteValue() == -127) {
            var1.add(var4.getLongKey());
         }
      }

      for (int var7 = 0; var7 < var1.size(); var7++) {
         this.long2ObjectMap.remove(var1.getLong(var7));
      }

      return var2;
   }

   public void clear() {
      this.long2ObjectMap.clear();
   }

   public static int method4911(int var0) {
      return var0 * 4;
   }

   public static int method4912(int var0) {
      return Math.floorDiv(var0, 64);
   }

   private static int method4913(int var0) {
      return Math.floorMod(var0, 64);
   }

   private static long method4914(int var0, int var1, int var2) {
      return (long)(var0 & 2097151) << 43 | (long)(var1 & 2097151) << 22 | var2 & 4194303;
   }
}

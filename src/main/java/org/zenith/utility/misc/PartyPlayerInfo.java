package org.zenith.utility.misc;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.zenith.utility.network.CloudPackets;

public final class PartyPlayerInfo {
   public static final long CONST_longValue = 15000L;
   private static final double CONST_doubleValue = 3.2E7;
   private static final double CONST_doubleValue2 = 1000000.0;
   private final long longValue;
   private final String text;
   private final String text2;
   private volatile String text3 = "";
   private volatile String text4 = "";
   private volatile double doubleValue;
   private volatile double doubleValue2;
   private volatile double doubleValue3;
   private volatile float floatValue;
   private volatile float floatValue2;
   private volatile long longValue2;
   private volatile double doubleValue4;
   private volatile double doubleValue5;
   private volatile double doubleValue6;
   private volatile long longValue3;
   private volatile float floatValue3;
   private volatile float floatValue4 = 20.0F;
   private volatile float floatValue5;
   private volatile int intValue = 20;
   private volatile CloudPackets.Data7[] data7s = new CloudPackets.Data7[0];
   private volatile List<CloudPackets.Data9> items = List.of();
   private volatile int intValue2;

   public PartyPlayerInfo(long var1, String var3, String var4) {
      this.longValue = var1;
      this.text = var3;
      this.text2 = var4;
   }

   public long uid() {
      return this.longValue;
   }

   public String username() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public boolean method3289() {
      return (this.intValue2 & 1) != 0;
   }

   public void setIntValue2(int var1) {
      this.intValue2 = var1;
   }

   public String server() {
      return this.text3;
   }

   public String dimension() {
      return this.text4;
   }

   public String method3290() {
      return this.text3.startsWith("singleplayer") ? "одиночная игра" : this.text3;
   }

   public boolean method3291(String var1, String var2) {
      return this.method3292() && this.text3.equals(var1) && this.text4.equals(var2);
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

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public boolean method3292() {
      long var1 = this.longValue2;
      return var1 != 0L && System.currentTimeMillis() - var1 <= 15000L;
   }

   public boolean method3293() {
      return this.longValue2 != 0L;
   }

   public Vec3d method3294() {
      if (this.longValue2 == 0L) {
         return null;
      } else if (this.longValue3 == 0L) {
         return new Vec3d(this.doubleValue, this.doubleValue2, this.doubleValue3);
      } else {
         long var1 = Math.max(1L, this.longValue2 - this.longValue3);
         double var3 = (double)(System.currentTimeMillis() - this.longValue2) / var1;
         if (var3 <= 0.0) {
            return new Vec3d(this.doubleValue4, this.doubleValue5, this.doubleValue6);
         } else {
            return var3 >= 1.0
               ? new Vec3d(this.doubleValue, this.doubleValue2, this.doubleValue3)
               : new Vec3d(
                  this.doubleValue4 + (this.doubleValue - this.doubleValue4) * var3,
                  this.doubleValue5 + (this.doubleValue2 - this.doubleValue5) * var3,
                  this.doubleValue6 + (this.doubleValue3 - this.doubleValue6) * var3
               );
         }
      }
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public float getFloatValue4() {
      return this.floatValue4;
   }

   public float getFloatValue5() {
      return this.floatValue5;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public CloudPackets.Data7[] getData7s() {
      return this.data7s;
   }

   public List<CloudPackets.Data9> getItems() {
      return this.items;
   }

   public void method3295(CloudPackets.Data5 var1) {
      this.text3 = var1.server();
      this.text4 = var1.dimension();
   }

   public void method3296(CloudPackets.Data2 var1) {
      if (method3300(var1.getDoubleValue(), 3.2E7)
         && method3300(var1.getDoubleValue2(), 3.2E7)
         && method3300(var1.getDoubleValue3(), 3.2E7)
         && Float.isFinite(var1.getFloatValue())
         && Float.isFinite(var1.getFloatValue2())) {
         long var2 = System.currentTimeMillis();
         double var4 = var1.getDoubleValue() - this.doubleValue;
         double var6 = var1.getDoubleValue2() - this.doubleValue2;
         double var8 = var1.getDoubleValue3() - this.doubleValue3;
         boolean var10 = this.longValue2 == 0L || var4 * var4 + var6 * var6 + var8 * var8 > 64.0;
         this.doubleValue4 = var10 ? var1.getDoubleValue() : this.doubleValue;
         this.doubleValue5 = var10 ? var1.getDoubleValue2() : this.doubleValue2;
         this.doubleValue6 = var10 ? var1.getDoubleValue3() : this.doubleValue3;
         this.longValue3 = this.longValue2;
         this.doubleValue = var1.getDoubleValue();
         this.doubleValue2 = var1.getDoubleValue2();
         this.doubleValue3 = var1.getDoubleValue3();
         this.floatValue = var1.getFloatValue();
         this.floatValue2 = var1.getFloatValue2();
         this.longValue2 = var2;
      }
   }

   public void method3297(CloudPackets.Data3 var1) {
      if (method3300(var1.getFloatValue(), 1000000.0)
         && method3300(var1.getFloatValue3(), 1000000.0)
         && method3300(var1.getFloatValue2(), 1000000.0)
         && !(var1.getFloatValue2() <= 0.0F)) {
         this.floatValue3 = var1.getFloatValue();
         this.floatValue4 = var1.getFloatValue2();
         this.floatValue5 = var1.getFloatValue3();
         this.intValue = var1.getIntValue();
      }
   }

   public void method3298(CloudPackets.Data11 var1) {
      this.data7s = var1.getData7s();
   }

   public void method3299(CloudPackets.Data15 var1) {
      this.items = var1.getItems();
   }

   private static boolean method3300(double var0, double var2) {
      return Double.isFinite(var0) && Math.abs(var0) <= var2;
   }

   @Override
   public String toString() {
      return this.text + " (" + this.text2 + ")";
   }
}

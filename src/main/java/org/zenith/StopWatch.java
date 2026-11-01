package org.zenith;

import lombok.Generated;

public class StopWatch {
   private long longValue;

   public StopWatch() {
      this.reset();
   }

   public boolean method7544(long var1) {
      boolean var3 = this.method7546(var1);
      if (var3) {
         this.reset();
      }

      return var3;
   }

   public boolean method7545(float var1) {
      return System.currentTimeMillis() - (long)var1 >= this.longValue;
   }

   public boolean method7546(long var1) {
      return System.currentTimeMillis() - var1 >= this.longValue;
   }

   public void reset() {
      this.longValue = System.currentTimeMillis();
   }

   public long method7547() {
      return System.currentTimeMillis() - this.longValue;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public void setLongValue(long var1) {
      this.longValue = var1;
   }
}

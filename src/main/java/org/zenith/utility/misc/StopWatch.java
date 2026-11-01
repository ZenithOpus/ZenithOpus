package org.zenith.utility.misc;

import lombok.Generated;

public class StopWatch {
   private long startTime;

   public StopWatch() {
      this.reset();
   }

   public boolean method317(double var1) {
      return System.currentTimeMillis() - var1 >= this.startTime;
   }

   public boolean method318(double var1) {
      boolean var3 = this.method317(var1);
      if (var3) {
         this.reset();
      }

      return var3;
   }

   public void reset() {
      this.startTime = System.currentTimeMillis();
   }

   public int method319() {
      return Math.toIntExact(System.currentTimeMillis() - this.startTime);
   }

   public void method320(long var1) {
      this.startTime = System.currentTimeMillis() - var1;
   }

   @Generated
   public long getStartTime() {
      return this.startTime;
   }
}

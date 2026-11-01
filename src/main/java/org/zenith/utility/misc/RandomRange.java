package org.zenith.utility.misc;

import java.util.Random;
import lombok.Generated;

public class RandomRange {
   private final int intValue;
   private final int intValue2;
   private final Random random = new Random();

   public RandomRange(int var1, int var2) {
      if (var1 > var2) {
         throw new IllegalArgumentException("Start must be less than or equal to endInclusive");
      } else {
         this.intValue = var1;
         this.intValue2 = var2;
      }
   }

   public int method6968() {
      return this.intValue + this.random.nextInt(this.intValue2 - this.intValue + 1);
   }

   @Override
   public String toString() {
      return this.intValue + ".." + this.intValue2;
   }

   @Generated
   public int getStart() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }
}

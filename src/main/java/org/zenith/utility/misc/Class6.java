package org.zenith.utility.misc;

import lombok.Generated;

public class Class6 extends RotationConfig {
   private int intValue;
   private boolean flag;
   private boolean flag2;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;

   @Override
   public Enum1 method853() {
      return Enum1.GOWNO;
   }

   @Generated
   private static int method854() {
      return 3;
   }

   @Generated
   private static boolean method855() {
      return true;
   }

   @Generated
   private static boolean method856() {
      return true;
   }

   @Generated
   private static float method857() {
      return 1.75F;
   }

   @Generated
   private static float method858() {
      return 0.4F;
   }

   @Generated
   private static float method859() {
      return 1.25F;
   }

   @Generated
   private static float method860() {
      return 0.75F;
   }

   @Generated
   public Class6(int var1, boolean var2, boolean var3, float var4, float var5, float var6, float var7) {
      this.intValue = var1;
      this.flag = var2;
      this.flag2 = var3;
      this.floatValue = var4;
      this.floatValue2 = var5;
      this.floatValue3 = var6;
      this.floatValue4 = var7;
   }

   @Generated
   public static Class6.Entry method861() {
      return new Class6.Entry();
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag2;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public float getFloatValue3() {
      return this.floatValue3;
   }

   @Generated
   public float getFloatValue4() {
      return this.floatValue4;
   }

   @Generated
   public static class Entry {
      @Generated
      private boolean flag;
      @Generated
      private int intValue;
      @Generated
      private boolean flag2;
      @Generated
      private boolean flag3;
      @Generated
      private boolean flag4;
      @Generated
      private boolean flag5;
      @Generated
      private boolean flag6;
      @Generated
      private float floatValue;
      @Generated
      private boolean flag7;
      @Generated
      private float floatValue2;
      @Generated
      private boolean flag8;
      @Generated
      private float floatValue3;
      @Generated
      private boolean flag9;
      @Generated
      private float floatValue4;

      @Generated
      public Class6.Entry method862(int var1) {
         this.intValue = var1;
         this.flag = true;
         return this;
      }

      @Generated
      public Class6.Entry method863(boolean var1) {
         this.flag3 = var1;
         this.flag2 = true;
         return this;
      }

      @Generated
      public Class6.Entry method864(boolean var1) {
         this.flag5 = var1;
         this.flag4 = true;
         return this;
      }

      @Generated
      public Class6.Entry method865(float var1) {
         this.floatValue = var1;
         this.flag6 = true;
         return this;
      }

      @Generated
      public Class6.Entry method866(float var1) {
         this.floatValue2 = var1;
         this.flag7 = true;
         return this;
      }

      @Generated
      public Class6.Entry method867(float var1) {
         this.floatValue3 = var1;
         this.flag8 = true;
         return this;
      }

      @Generated
      public Class6.Entry method868(float var1) {
         this.floatValue4 = var1;
         this.flag9 = true;
         return this;
      }

      @Generated
      public Class6 method869() {
         int var1 = this.intValue;
         if (!this.flag) {
            var1 = Class6.method854();
         }

         boolean var2 = this.flag3;
         if (!this.flag2) {
            var2 = Class6.method855();
         }

         boolean var3 = this.flag5;
         if (!this.flag4) {
            var3 = Class6.method856();
         }

         float var4 = this.floatValue;
         if (!this.flag6) {
            var4 = Class6.method857();
         }

         float var5 = this.floatValue2;
         if (!this.flag7) {
            var5 = Class6.method858();
         }

         float var6 = this.floatValue3;
         if (!this.flag8) {
            var6 = Class6.method859();
         }

         float var7 = this.floatValue4;
         if (!this.flag9) {
            var7 = Class6.method860();
         }

         return new Class6(var1, var2, var3, var4, var5, var6, var7);
      }

      @Generated
      @Override
      public String toString() {
         return "GownoRotationConfig.GownoRotationConfigBuilder(tick$value="
            + this.intValue
            + ", multiplyYaw$value="
            + this.flag3
            + ", multiplyPitch$value="
            + this.flag5
            + ", yawAcceleration$value="
            + this.floatValue
            + ", yawDeceleration$value="
            + this.floatValue2
            + ", pitchAcceleration$value="
            + this.floatValue3
            + ", pitchDeceleration$value="
            + this.floatValue4
            + ")";
      }
   }
}

package org.zenith.utility.misc;

import org.zenith.utility.api.CosmeticPartInfo;

public final class Util31 {
   public static final float CONST_floatValue = 1.501F;
   public static final float CONST_floatValue2 = 16.0F;
   public static final float CONST_floatValue3 = 1.35F;

   public static float[] method7850(CosmeticType.Type var0) {
      return switch (var0) {
         case LEFT_ARM -> new float[]{0.3125F, 0.125F};
         case RIGHT_ARM -> new float[]{-0.3125F, 0.125F};
         case LEFT_LEG -> new float[]{0.11875F, 0.75F};
         case RIGHT_LEG -> new float[]{-0.11875F, 0.75F};
         default -> new float[]{0.0F, 0.0F};
      };
   }

   private static boolean method7851(CosmeticType var0) {
      return var0 != CosmeticType.PET && var0 != CosmeticType.FLYING_PET ? var0.getType() != CosmeticType.Type.NONE : false;
   }

   private Util31() {
   }

   public static Util31.Data method7852(CosmeticPartInfo.Data var0, CosmeticPartInfo.Type var1, CosmeticType var2) {
      if (var0 == null) {
         return new Util31.Data(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, false);
      } else {
         boolean var3 = var1 == CosmeticPartInfo.Type.BEDROCK;
         float var4 = var3 ? 16.0F : 1.0F;
         float var5 = var3 ? -1.0F : 1.0F;
         float var6 = var3 ? Math.abs(var0.getFloatValue8()) : var0.getFloatValue8();
         boolean var7 = method7851(var2);
         return new Util31.Data(
            var0.getFloatValue(),
            var0.getFloatValue2(),
            var0.getFloatValue3(),
            var0.getFloatValue4(),
            var0.getFloatValue5(),
            var0.getFloatValue6(),
            var0.getFloatValue7() * var4,
            var6 * var4 * var5,
            var0.getFloatValue9() * var4 * var5,
            var7
         );
      }
   }

   public static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final boolean flag;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "flag=" + this.flag + "]";
      }

      public Data(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, boolean var10) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
         this.floatValue9 = var9;
         this.flag = var10;
      }

      public boolean method7853() {
         return this.floatValue7 == 0.0F || this.floatValue8 == 0.0F || this.floatValue9 == 0.0F;
      }

      public boolean method7854() {
         return this.floatValue4 != 0.0F || this.floatValue5 != 0.0F || this.floatValue6 != 0.0F;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
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

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

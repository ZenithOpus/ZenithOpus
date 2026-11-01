package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CosmeticSkeleton {
   private final CosmeticSkeleton.Data[] datas;
   private final String[] texts;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final int[] intValues;
   private final int[] intValues2;

   private CosmeticSkeleton(CosmeticSkeleton.Data[] var1, String[] var2, float[] var3, float[] var4, float[] var5, int[] var6, int[] var7) {
      this.datas = var1;
      this.texts = var2;
      this.floatValues = var3;
      this.floatValues2 = var4;
      this.floatValues3 = var5;
      this.intValues = var6;
      this.intValues2 = var7;
   }

   public CosmeticSkeleton.Data[] getDatas() {
      return this.datas;
   }

   public String[] getTexts() {
      return this.texts;
   }

   public int method7138() {
      return this.intValues.length;
   }

   public int method7139(int var1) {
      return this.intValues[var1];
   }

   public int method7140(int var1) {
      return this.intValues2[var1];
   }

   public float method7141(int var1, int var2, int var3) {
      return this.floatValues[var1 * 12 + var2 * 3 + var3];
   }

   public float method7142(int var1, int var2) {
      return this.floatValues2[var1 * 8 + var2 * 2];
   }

   public float method7143(int var1, int var2) {
      return this.floatValues2[var1 * 8 + var2 * 2 + 1];
   }

   public float method7144(int var1, int var2) {
      return this.floatValues3[var1 * 3 + var2];
   }

   public float[] method7145() {
      if (this.floatValues.length == 0) {
         return new float[6];
      } else {
         float[] var1 = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

         for (byte var2 = 0; var2 < this.floatValues.length; var2 += 3) {
            for (int var3 = 0; var3 < 3; var3++) {
               float var4 = this.floatValues[var2 + var3];
               var1[var3] = Math.min(var1[var3], var4);
               var1[var3 + 3] = Math.max(var1[var3 + 3], var4);
            }
         }

         return var1;
      }
   }

   public static final class Data {
      private final String text;
      private final int intValue;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float rotationX;
      private final float rotationY;
      private final float floatValue4;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float rotationX() {
         return this.rotationX;
      }

      public float rotationY() {
         return this.rotationY;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.rotationX, other.rotationX) && java.util.Objects.equals(this.rotationY, other.rotationY) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, floatValue, floatValue2, floatValue3, rotationX, rotationY, floatValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "rotationX=" + this.rotationX + ", " + "rotationY=" + this.rotationY + ", " + "floatValue4=" + this.floatValue4 + "]";
      }

      public Data(String var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.text = var1;
         this.intValue = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
         this.rotationX = var6;
         this.rotationY = var7;
         this.floatValue4 = var8;
      }

      public String name() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
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

      public float getRotationX() {
         return this.rotationX;
      }

      public float getRotationY() {
         return this.rotationY;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }
   }

   public static final class Entry {
      private final List<CosmeticSkeleton.Data> items = new ArrayList<>();
      private final List<String> items2 = new ArrayList<>();
      private final List<CosmeticSkeleton.Entry.Data> items3 = new ArrayList<>();

      public int method7146(String var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.items.add(new CosmeticSkeleton.Data(var1, var2, var3, var4, var5, var6, var7, var8));
         return this.items.size() - 1;
      }

      public int method7147(String var1) {
         int var2 = this.items2.indexOf(var1);
         if (var2 >= 0) {
            return var2;
         } else {
            this.items2.add(var1);
            return this.items2.size() - 1;
         }
      }

      public void method7148(int var1, int var2, float[] var3, float[] var4, float[] var5) {
         this.items3.add(new CosmeticSkeleton.Entry.Data(var1, var2, var3, var4, var5 == null ? method7151(var3) : var5));
      }

      public int method7149() {
         return this.items.size();
      }

      public int method7150() {
         return this.items3.size();
      }

      private static float[] method7151(float[] var0) {
         float var1 = var0[3] - var0[0];
         float var2 = var0[4] - var0[1];
         float var3 = var0[5] - var0[2];
         float var4 = var0[6] - var0[0];
         float var5 = var0[7] - var0[1];
         float var6 = var0[8] - var0[2];
         float var7 = var2 * var6 - var3 * var5;
         float var8 = var3 * var4 - var1 * var6;
         float var9 = var1 * var5 - var2 * var4;
         float var10 = (float)Math.sqrt(var7 * var7 + var8 * var8 + var9 * var9);
         return var10 < 1.0E-6F ? new float[]{0.0F, 1.0F, 0.0F} : new float[]{var7 / var10, var8 / var10, var9 / var10};
      }

      public CosmeticSkeleton method7152() {
         if (this.items.isEmpty()) {
            this.items.add(new CosmeticSkeleton.Data("root", -1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
         }

         if (this.items2.isEmpty()) {
            this.items2.add("");
         }

         this.items3.sort(Comparator.comparingInt(CosmeticSkeleton.Entry.Data::getIntValue2).thenComparingInt(CosmeticSkeleton.Entry.Data::getIntValue));
         int var1 = this.items3.size();
         float[] var2 = new float[var1 * 12];
         float[] var3 = new float[var1 * 8];
         float[] var4 = new float[var1 * 3];
         int[] var5 = new int[var1];
         int[] var6 = new int[var1];

         for (int var7 = 0; var7 < var1; var7++) {
            CosmeticSkeleton.Entry.Data var8 = this.items3.get(var7);
            System.arraycopy(var8.floatValues, 0, var2, var7 * 12, 12);
            System.arraycopy(var8.floatValues2, 0, var3, var7 * 8, 8);
            System.arraycopy(var8.floatValues3, 0, var4, var7 * 3, 3);
            var5[var7] = var8.intValue;
            var6[var7] = var8.intValue2;
         }

         return new CosmeticSkeleton(this.items.toArray(new CosmeticSkeleton.Data[0]), this.items2.toArray(new String[0]), var2, var3, var4, var5, var6);
      }

      private static final class Data {
         private final int intValue;
         private final int intValue2;
         private final float[] floatValues;
         private final float[] floatValues2;
         private final float[] floatValues3;

         public int intValue() {
            return this.intValue;
         }

         public int intValue2() {
            return this.intValue2;
         }

         public float[] floatValues() {
            return this.floatValues;
         }

         public float[] floatValues2() {
            return this.floatValues2;
         }

         public float[] floatValues3() {
            return this.floatValues3;
         }

         @Override
         public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Data other)) return false;
            return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3);
         }

         @Override
         public int hashCode() {
            return java.util.Objects.hash(intValue, intValue2, floatValues, floatValues2, floatValues3);
         }

         @Override
         public String toString() {
            return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + "]";
         }

         private Data(int var1, int var2, float[] var3, float[] var4, float[] var5) {
            this.intValue = var1;
            this.intValue2 = var2;
            this.floatValues = var3;
            this.floatValues2 = var4;
            this.floatValues3 = var5;
         }

         public int getIntValue() {
            return this.intValue;
         }

         public int getIntValue2() {
            return this.intValue2;
         }

         public float[] getFloatValues() {
            return this.floatValues;
         }

         public float[] getFloatValues2() {
            return this.floatValues2;
         }

         public float[] getFloatValues3() {
            return this.floatValues3;
         }
      }
   }
}

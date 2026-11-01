package org.zenith.utility.misc;

public final class Util8 {
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final float CONST_floatValue2 = 0.55F;
   private static final float CONST_floatValue3 = 0.06F;
   private static final float CONST_floatValue4 = -0.38F;
   private static final float CONST_floatValue5 = -0.06F;
   public final float floatValue;
   public final float floatValue2;
   public final float floatValue3;
   public final float floatValue4;
   public final float floatValue5;
   public final float floatValue6;
   public final float[] floatValues;
   public final float[] floatValues2;
   public final float floatValue7;
   public final float floatValue8;
   public final float floatValue9;
   public final float floatValue10;
   public final float floatValue11;
   public final float floatValue12;
   public final boolean flag;
   public final float floatValue13;
   public final float floatValue14;
   public final float floatValue15;
   public final float floatValue16;
   public final float floatValue17;
   private static final Util8.Data[] datas = new Util8.Data[]{
      new Util8.Data(0.0F, 0.15F, 1.0F, 0.8F, 0.86F, 1.14F, 0.6F, 0.68F, 0.92F, 0.38F, 0.92F, 0.5F, 0.82F, 0.68F, 1.12F),
      new Util8.Data(3.0F, 0.14F, 1.0F, 0.79F, 0.85F, 1.15F, 0.58F, 0.66F, 0.92F, 0.36F, 0.95F, 0.52F, 0.8F, 0.66F, 1.12F),
      new Util8.Data(4.4F, 0.2F, 1.0F, 0.82F, 0.86F, 1.12F, 0.66F, 0.7F, 0.9F, 0.42F, 0.78F, 0.42F, 0.88F, 0.7F, 1.14F),
      new Util8.Data(5.4F, 0.34F, 0.9F, 1.0F, 0.9F, 1.02F, 0.78F, 0.78F, 0.9F, 0.6F, 0.34F, 0.14F, 1.2F, 0.78F, 1.16F),
      new Util8.Data(6.2F, 0.58F, 0.82F, 1.16F, 0.94F, 0.88F, 1.26F, 0.66F, 0.42F, 1.5F, 0.08F, 0.03F, 1.42F, 0.85F, 1.18F),
      new Util8.Data(7.4F, 0.82F, 0.9F, 1.1F, 1.0F, 0.9F, 1.18F, 0.84F, 0.58F, 1.35F, 0.01F, 0.0F, 1.32F, 0.93F, 1.1F),
      new Util8.Data(9.6F, 0.93F, 0.97F, 1.04F, 1.0F, 0.95F, 1.08F, 0.92F, 0.74F, 1.14F, 0.0F, 0.0F, 1.14F, 1.0F, 1.03F),
      new Util8.Data(12.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.97F, 0.86F, 1.0F, 0.0F, 0.0F, 1.0F, 1.05F, 1.0F),
      new Util8.Data(14.4F, 0.98F, 1.0F, 1.02F, 1.0F, 0.98F, 1.02F, 0.95F, 0.82F, 1.05F, 0.0F, 0.0F, 1.06F, 1.04F, 1.0F),
      new Util8.Data(16.4F, 0.9F, 0.96F, 1.07F, 0.99F, 0.92F, 1.14F, 0.87F, 0.64F, 1.22F, 0.0F, 0.0F, 1.2F, 0.98F, 1.05F),
      new Util8.Data(17.4F, 0.8F, 0.86F, 1.18F, 0.96F, 0.86F, 1.3F, 0.74F, 0.46F, 1.55F, 0.02F, 0.01F, 1.4F, 0.92F, 1.14F),
      new Util8.Data(18.2F, 0.55F, 0.82F, 1.16F, 0.9F, 0.92F, 0.84F, 0.78F, 0.88F, 0.7F, 0.16F, 0.06F, 1.36F, 0.84F, 1.18F),
      new Util8.Data(19.4F, 0.34F, 0.9F, 0.98F, 0.88F, 1.04F, 0.72F, 0.74F, 0.92F, 0.58F, 0.46F, 0.22F, 1.12F, 0.78F, 1.16F),
      new Util8.Data(20.6F, 0.22F, 0.98F, 0.86F, 0.87F, 1.1F, 0.66F, 0.7F, 0.92F, 0.48F, 0.72F, 0.38F, 0.94F, 0.72F, 1.14F),
      new Util8.Data(22.0F, 0.17F, 1.0F, 0.82F, 0.87F, 1.12F, 0.64F, 0.7F, 0.92F, 0.44F, 0.84F, 0.44F, 0.88F, 0.7F, 1.12F)
   };

   private Util8(float var1, Util8.Data var2, float[] var3, float[] var4, float var5, float var6, boolean var7) {
      this.floatValue = var1;
      this.floatValue2 = var2.getFloatValue2();
      this.floatValue3 = var2.getFloatValue3();
      this.floatValue4 = var2.getFloatValue4();
      this.floatValue5 = var2.getFloatValue5();
      this.floatValue6 = var2.getFloatValue6();
      this.floatValues = var3;
      this.floatValues2 = var4;
      this.floatValue7 = var5;
      this.floatValue8 = var2.getFloatValue7();
      this.floatValue9 = var2.getFloatValue8();
      this.floatValue10 = var2.getFloatValue9();
      this.floatValue11 = var2.getFloatValue10();
      this.floatValue12 = var6;
      this.flag = var7;
      this.floatValue13 = var2.getFloatValue11();
      this.floatValue14 = var2.getFloatValue12();
      this.floatValue15 = var2.getFloatValue13();
      this.floatValue16 = var2.getFloatValue14();
      this.floatValue17 = var2.getFloatValue15();
   }

   public static Util8 method1722(float var0) {
      float var1 = method1723(var0);
      Util8.Data var2 = method1724(var1);
      float var3 = (var1 - 6.0F) / 24.0F * (float) (Math.PI * 2);
      float var4 = (float)Math.sin(var3);
      float var5 = (float)Math.cos(var3);
      float var6 = (float)Math.cos(0.55F);
      float var7 = (float)Math.sin(0.55F);
      float[] var8 = new float[]{var5, var4 * var6, var4 * var7};
      boolean var9 = var8[1] < 0.0F;
      float[] var10 = var9 ? new float[]{-var8[0], -var8[1], -var8[2]} : (float[])var8.clone();
      float var11 = Math.clamp(var10[1] / 0.06F, 0.0F, 1.0F);
      var11 = var11 * var11 * (3.0F - 2.0F * var11);
      float var12 = Math.clamp((var8[1] - -0.38F) / 0.32F, 0.0F, 1.0F);
      var12 = var12 * var12 * (3.0F - 2.0F * var12);
      return new Util8(var1, var2, var10, var8, var12, var11, var9);
   }

   private static float method1723(float var0) {
      float var1 = var0 % 24.0F;
      return var1 < 0.0F ? var1 + 24.0F : var1;
   }

   private static Util8.Data method1724(float var0) {
      int var1 = 0;

      while (var1 < datas.length && datas[var1].getFloatValue() <= var0) {
         var1++;
      }

      Util8.Data var2 = datas[var1 % datas.length];
      Util8.Data var3 = datas[(var1 - 1 + datas.length) % datas.length];
      float var4 = method1725(var3.getFloatValue(), var2.getFloatValue());
      float var5 = var4 <= 0.0F ? 0.0F : Math.clamp(method1725(var3.getFloatValue(), var0) / var4, 0.0F, 1.0F);
      var5 = var5 * var5 * (3.0F - 2.0F * var5);
      return new Util8.Data(
         var0,
         method1726(var3.getFloatValue2(), var2.getFloatValue2(), var5),
         method1726(var3.getFloatValue3(), var2.getFloatValue3(), var5),
         method1726(var3.getFloatValue4(), var2.getFloatValue4(), var5),
         method1726(var3.getFloatValue5(), var2.getFloatValue5(), var5),
         method1726(var3.getFloatValue6(), var2.getFloatValue6(), var5),
         method1726(var3.getFloatValue7(), var2.getFloatValue7(), var5),
         method1726(var3.getFloatValue8(), var2.getFloatValue8(), var5),
         method1726(var3.getFloatValue9(), var2.getFloatValue9(), var5),
         method1726(var3.getFloatValue10(), var2.getFloatValue10(), var5),
         method1726(var3.getFloatValue11(), var2.getFloatValue11(), var5),
         method1726(var3.getFloatValue12(), var2.getFloatValue12(), var5),
         method1726(var3.getFloatValue13(), var2.getFloatValue13(), var5),
         method1726(var3.getFloatValue14(), var2.getFloatValue14(), var5),
         method1726(var3.getFloatValue15(), var2.getFloatValue15(), var5)
      );
   }

   private static float method1725(float var0, float var1) {
      float var2 = var1 - var0;
      return var2 <= 0.0F ? var2 + 24.0F : var2;
   }

   private static float method1726(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final float floatValue13;
      private final float floatValue14;
      private final float floatValue15;

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

      public float floatValue10() {
         return this.floatValue10;
      }

      public float floatValue11() {
         return this.floatValue11;
      }

      public float floatValue12() {
         return this.floatValue12;
      }

      public float floatValue13() {
         return this.floatValue13;
      }

      public float floatValue14() {
         return this.floatValue14;
      }

      public float floatValue15() {
         return this.floatValue15;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.floatValue12, other.floatValue12) && java.util.Objects.equals(this.floatValue13, other.floatValue13) && java.util.Objects.equals(this.floatValue14, other.floatValue14) && java.util.Objects.equals(this.floatValue15, other.floatValue15);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, floatValue13, floatValue14, floatValue15);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "floatValue12=" + this.floatValue12 + ", " + "floatValue13=" + this.floatValue13 + ", " + "floatValue14=" + this.floatValue14 + ", " + "floatValue15=" + this.floatValue15 + "]";
      }

      private Data(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         float var13,
         float var14,
         float var15
      ) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
         this.floatValue9 = var9;
         this.floatValue10 = var10;
         this.floatValue11 = var11;
         this.floatValue12 = var12;
         this.floatValue13 = var13;
         this.floatValue14 = var14;
         this.floatValue15 = var15;
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

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public float getFloatValue12() {
         return this.floatValue12;
      }

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }

      public float getFloatValue15() {
         return this.floatValue15;
      }
   }
}

package org.zenith.utility.misc;

public final class WeatherPresets {
   public final String text;
   private final WeatherPresets.Data[] datas;
   public static final WeatherPresets INSTANCE = new WeatherPresets(
      "clear",
      new WeatherPresets.Data[]{
         new WeatherPresets.Data(
            0.0F,
            new float[]{0.018F, 0.03F, 0.075F},
            new float[]{0.055F, 0.08F, 0.15F},
            new float[]{0.02F, 0.028F, 0.05F},
            new float[]{0.13F, 0.16F, 0.235F},
            new float[]{0.04F, 0.052F, 0.085F},
            0.22F,
            0.35F,
            1.05F,
            0.018F,
            0.16F,
            new float[]{0.07F, 0.1F, 0.18F},
            0.8F,
            0.42F,
            0.0F,
            0.22F,
            0.02F,
            0.3F,
            -15985370,
            0.72F,
            0.0F
         ),
         new WeatherPresets.Data(
            4.8F,
            new float[]{0.055F, 0.105F, 0.235F},
            new float[]{0.28F, 0.26F, 0.33F},
            new float[]{0.055F, 0.07F, 0.105F},
            new float[]{0.43F, 0.43F, 0.52F},
            new float[]{0.15F, 0.165F, 0.23F},
            0.24F,
            0.28F,
            1.05F,
            0.019F,
            0.3F,
            new float[]{0.34F, 0.31F, 0.38F},
            0.34F,
            0.16F,
            0.0F,
            0.08F,
            0.022F,
            0.35F,
            -14012862,
            0.8F,
            0.0F
         ),
         new WeatherPresets.Data(
            6.3F,
            new float[]{0.14F, 0.265F, 0.545F},
            new float[]{0.98F, 0.64F, 0.43F},
            new float[]{0.12F, 0.115F, 0.145F},
            new float[]{1.0F, 0.815F, 0.67F},
            new float[]{0.4F, 0.39F, 0.5F},
            0.28F,
            0.18F,
            1.05F,
            0.02F,
            0.42F,
            new float[]{0.98F, 0.68F, 0.48F},
            0.05F,
            0.02F,
            0.0F,
            0.02F,
            0.032F,
            0.95F,
            -9543840,
            0.92F,
            0.0F
         ),
         new WeatherPresets.Data(
            8.0F,
            new float[]{0.15F, 0.35F, 0.74F},
            new float[]{0.76F, 0.83F, 0.93F},
            new float[]{0.23F, 0.29F, 0.38F},
            new float[]{1.0F, 0.985F, 0.955F},
            new float[]{0.62F, 0.685F, 0.805F},
            0.3F,
            0.1F,
            1.05F,
            0.02F,
            0.26F,
            new float[]{0.72F, 0.8F, 0.92F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.03F,
            0.85F,
            -5718572,
            1.02F,
            0.0F
         ),
         new WeatherPresets.Data(
            12.0F,
            new float[]{0.095F, 0.315F, 0.8F},
            new float[]{0.73F, 0.86F, 0.985F},
            new float[]{0.25F, 0.32F, 0.42F},
            new float[]{1.0F, 1.0F, 1.0F},
            new float[]{0.66F, 0.74F, 0.865F},
            0.32F,
            0.07F,
            1.0F,
            0.021F,
            0.2F,
            new float[]{0.76F, 0.865F, 0.965F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.03F,
            0.88F,
            -4929314,
            1.08F,
            0.0F
         ),
         new WeatherPresets.Data(
            16.8F,
            new float[]{0.135F, 0.335F, 0.735F},
            new float[]{0.8F, 0.83F, 0.91F},
            new float[]{0.235F, 0.29F, 0.375F},
            new float[]{1.0F, 0.975F, 0.93F},
            new float[]{0.64F, 0.69F, 0.8F},
            0.3F,
            0.11F,
            1.05F,
            0.02F,
            0.28F,
            new float[]{0.82F, 0.82F, 0.9F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.03F,
            0.88F,
            -5259566,
            1.0F,
            0.0F
         ),
         new WeatherPresets.Data(
            18.3F,
            new float[]{0.13F, 0.23F, 0.5F},
            new float[]{1.0F, 0.58F, 0.36F},
            new float[]{0.115F, 0.105F, 0.135F},
            new float[]{1.0F, 0.76F, 0.6F},
            new float[]{0.395F, 0.36F, 0.46F},
            0.28F,
            0.2F,
            1.05F,
            0.02F,
            0.46F,
            new float[]{1.0F, 0.62F, 0.41F},
            0.06F,
            0.03F,
            0.0F,
            0.03F,
            0.032F,
            0.95F,
            -9152166,
            0.9F,
            0.0F
         ),
         new WeatherPresets.Data(
            20.5F,
            new float[]{0.045F, 0.08F, 0.195F},
            new float[]{0.2F, 0.18F, 0.275F},
            new float[]{0.045F, 0.058F, 0.09F},
            new float[]{0.33F, 0.34F, 0.44F},
            new float[]{0.11F, 0.125F, 0.185F},
            0.24F,
            0.3F,
            1.05F,
            0.019F,
            0.26F,
            new float[]{0.25F, 0.24F, 0.33F},
            0.48F,
            0.22F,
            0.0F,
            0.14F,
            0.022F,
            0.34F,
            -14472640,
            0.78F,
            0.0F
         )
      }
   );
   public static final WeatherPresets INSTANCE2 = new WeatherPresets(
      "storm",
      new WeatherPresets.Data[]{
         new WeatherPresets.Data(
            0.0F,
            new float[]{0.022F, 0.028F, 0.036F},
            new float[]{0.052F, 0.06F, 0.07F},
            new float[]{0.016F, 0.02F, 0.026F},
            new float[]{0.115F, 0.13F, 0.15F},
            new float[]{0.026F, 0.032F, 0.04F},
            0.97F,
            0.9F,
            1.55F,
            0.036F,
            0.44F,
            new float[]{0.06F, 0.068F, 0.08F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.05F,
            -15459808,
            0.66F,
            0.26F
         ),
         new WeatherPresets.Data(
            4.8F,
            new float[]{0.048F, 0.055F, 0.066F},
            new float[]{0.105F, 0.11F, 0.125F},
            new float[]{0.03F, 0.035F, 0.042F},
            new float[]{0.2F, 0.215F, 0.24F},
            new float[]{0.05F, 0.058F, 0.07F},
            0.96F,
            0.88F,
            1.55F,
            0.036F,
            0.48F,
            new float[]{0.115F, 0.12F, 0.135F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.06F,
            -14801876,
            0.72F,
            0.24F
         ),
         new WeatherPresets.Data(
            6.3F,
            new float[]{0.085F, 0.092F, 0.105F},
            new float[]{0.25F, 0.195F, 0.175F},
            new float[]{0.05F, 0.055F, 0.065F},
            new float[]{0.34F, 0.315F, 0.315F},
            new float[]{0.08F, 0.085F, 0.098F},
            0.97F,
            0.86F,
            1.55F,
            0.038F,
            0.56F,
            new float[]{0.245F, 0.205F, 0.19F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.08F,
            -13422034,
            0.8F,
            0.28F
         ),
         new WeatherPresets.Data(
            8.0F,
            new float[]{0.135F, 0.155F, 0.175F},
            new float[]{0.265F, 0.29F, 0.31F},
            new float[]{0.07F, 0.08F, 0.092F},
            new float[]{0.45F, 0.48F, 0.51F},
            new float[]{0.092F, 0.105F, 0.122F},
            0.97F,
            0.85F,
            1.55F,
            0.038F,
            0.52F,
            new float[]{0.265F, 0.29F, 0.312F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.1F,
            -12761522,
            0.88F,
            0.3F
         ),
         new WeatherPresets.Data(
            12.0F,
            new float[]{0.16F, 0.185F, 0.205F},
            new float[]{0.31F, 0.34F, 0.36F},
            new float[]{0.082F, 0.094F, 0.108F},
            new float[]{0.51F, 0.545F, 0.575F},
            new float[]{0.1F, 0.115F, 0.135F},
            0.98F,
            0.85F,
            1.6F,
            0.04F,
            0.55F,
            new float[]{0.305F, 0.335F, 0.358F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.1F,
            -12169128,
            0.92F,
            0.34F
         ),
         new WeatherPresets.Data(
            16.8F,
            new float[]{0.14F, 0.158F, 0.176F},
            new float[]{0.28F, 0.3F, 0.318F},
            new float[]{0.072F, 0.082F, 0.095F},
            new float[]{0.47F, 0.495F, 0.522F},
            new float[]{0.094F, 0.107F, 0.125F},
            0.97F,
            0.86F,
            1.55F,
            0.039F,
            0.54F,
            new float[]{0.278F, 0.298F, 0.318F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.09F,
            -12629937,
            0.88F,
            0.32F
         ),
         new WeatherPresets.Data(
            18.3F,
            new float[]{0.082F, 0.088F, 0.1F},
            new float[]{0.255F, 0.19F, 0.165F},
            new float[]{0.048F, 0.053F, 0.062F},
            new float[]{0.33F, 0.3F, 0.298F},
            new float[]{0.078F, 0.082F, 0.094F},
            0.97F,
            0.87F,
            1.55F,
            0.038F,
            0.58F,
            new float[]{0.25F, 0.2F, 0.18F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.08F,
            -13488340,
            0.8F,
            0.3F
         ),
         new WeatherPresets.Data(
            20.5F,
            new float[]{0.04F, 0.046F, 0.056F},
            new float[]{0.09F, 0.095F, 0.108F},
            new float[]{0.026F, 0.03F, 0.037F},
            new float[]{0.175F, 0.188F, 0.21F},
            new float[]{0.042F, 0.049F, 0.06F},
            0.97F,
            0.89F,
            1.55F,
            0.037F,
            0.46F,
            new float[]{0.098F, 0.104F, 0.118F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.06F,
            -14999256,
            0.7F,
            0.28F
         )
      }
   );
   public static final WeatherPresets INSTANCE3 = new WeatherPresets(
      "stars",
      new WeatherPresets.Data[]{
         new WeatherPresets.Data(
            0.0F,
            new float[]{0.012F, 0.02F, 0.058F},
            new float[]{0.05F, 0.078F, 0.15F},
            new float[]{0.014F, 0.02F, 0.04F},
            new float[]{0.3F, 0.36F, 0.5F},
            new float[]{0.055F, 0.075F, 0.13F},
            0.34F,
            0.38F,
            1.2F,
            0.014F,
            0.18F,
            new float[]{0.07F, 0.105F, 0.195F},
            1.15F,
            1.0F,
            0.0F,
            0.55F,
            0.024F,
            0.6F,
            -16117468,
            0.7F,
            0.0F
         ),
         new WeatherPresets.Data(
            4.8F,
            new float[]{0.02F, 0.032F, 0.082F},
            new float[]{0.09F, 0.115F, 0.195F},
            new float[]{0.02F, 0.028F, 0.05F},
            new float[]{0.34F, 0.395F, 0.53F},
            new float[]{0.065F, 0.085F, 0.145F},
            0.34F,
            0.36F,
            1.2F,
            0.014F,
            0.22F,
            new float[]{0.105F, 0.13F, 0.21F},
            1.05F,
            0.92F,
            0.0F,
            0.48F,
            0.024F,
            0.58F,
            -15721934,
            0.74F,
            0.0F
         ),
         new WeatherPresets.Data(
            6.3F,
            new float[]{0.03F, 0.048F, 0.11F},
            new float[]{0.195F, 0.165F, 0.225F},
            new float[]{0.026F, 0.034F, 0.058F},
            new float[]{0.43F, 0.43F, 0.52F},
            new float[]{0.08F, 0.095F, 0.155F},
            0.34F,
            0.34F,
            1.2F,
            0.015F,
            0.28F,
            new float[]{0.185F, 0.165F, 0.23F},
            0.98F,
            0.86F,
            0.0F,
            0.44F,
            0.026F,
            0.66F,
            -14999496,
            0.78F,
            0.0F
         ),
         new WeatherPresets.Data(
            8.0F,
            new float[]{0.04F, 0.062F, 0.145F},
            new float[]{0.15F, 0.185F, 0.3F},
            new float[]{0.032F, 0.042F, 0.07F},
            new float[]{0.48F, 0.52F, 0.64F},
            new float[]{0.09F, 0.11F, 0.175F},
            0.34F,
            0.34F,
            1.2F,
            0.015F,
            0.26F,
            new float[]{0.15F, 0.185F, 0.295F},
            0.95F,
            0.84F,
            0.0F,
            0.44F,
            0.026F,
            0.7F,
            -14800315,
            0.82F,
            0.0F
         ),
         new WeatherPresets.Data(
            12.0F,
            new float[]{0.048F, 0.075F, 0.175F},
            new float[]{0.175F, 0.215F, 0.345F},
            new float[]{0.036F, 0.048F, 0.078F},
            new float[]{0.545F, 0.59F, 0.715F},
            new float[]{0.1F, 0.122F, 0.19F},
            0.34F,
            0.32F,
            1.2F,
            0.016F,
            0.24F,
            new float[]{0.175F, 0.215F, 0.335F},
            0.9F,
            0.8F,
            0.0F,
            0.42F,
            0.028F,
            0.74F,
            -14405556,
            0.86F,
            0.0F
         ),
         new WeatherPresets.Data(
            16.8F,
            new float[]{0.042F, 0.066F, 0.152F},
            new float[]{0.155F, 0.19F, 0.308F},
            new float[]{0.033F, 0.044F, 0.072F},
            new float[]{0.5F, 0.54F, 0.66F},
            new float[]{0.092F, 0.112F, 0.178F},
            0.34F,
            0.33F,
            1.2F,
            0.015F,
            0.26F,
            new float[]{0.155F, 0.19F, 0.3F},
            0.95F,
            0.84F,
            0.0F,
            0.44F,
            0.026F,
            0.7F,
            -14734522,
            0.82F,
            0.0F
         ),
         new WeatherPresets.Data(
            18.3F,
            new float[]{0.03F, 0.048F, 0.112F},
            new float[]{0.2F, 0.16F, 0.22F},
            new float[]{0.026F, 0.034F, 0.058F},
            new float[]{0.43F, 0.42F, 0.51F},
            new float[]{0.08F, 0.095F, 0.155F},
            0.34F,
            0.34F,
            1.2F,
            0.015F,
            0.3F,
            new float[]{0.19F, 0.16F, 0.225F},
            1.0F,
            0.88F,
            0.0F,
            0.48F,
            0.026F,
            0.66F,
            -14934217,
            0.78F,
            0.0F
         ),
         new WeatherPresets.Data(
            20.5F,
            new float[]{0.018F, 0.028F, 0.075F},
            new float[]{0.07F, 0.098F, 0.175F},
            new float[]{0.018F, 0.025F, 0.046F},
            new float[]{0.33F, 0.385F, 0.52F},
            new float[]{0.062F, 0.082F, 0.14F},
            0.34F,
            0.37F,
            1.2F,
            0.014F,
            0.2F,
            new float[]{0.09F, 0.118F, 0.2F},
            1.1F,
            0.96F,
            0.0F,
            0.52F,
            0.024F,
            0.6F,
            -15920088,
            0.72F,
            0.0F
         )
      }
   );

   private WeatherPresets(String var1, WeatherPresets.Data[] var2) {
      this.text = var1;
      this.datas = var2;
   }

   public WeatherPresets.Data method5994(float var1) {
      float var2 = var1 % 24.0F;
      if (var2 < 0.0F) {
         var2 += 24.0F;
      }

      int var3 = 0;

      while (var3 < this.datas.length && this.datas[var3].getFloatValue() <= var2) {
         var3++;
      }

      WeatherPresets.Data var4 = this.datas[var3 % this.datas.length];
      WeatherPresets.Data var5 = this.datas[(var3 - 1 + this.datas.length) % this.datas.length];
      float var6 = method5995(var5.getFloatValue(), var4.getFloatValue());
      float var7 = var6 <= 0.0F ? 0.0F : Math.clamp(method5995(var5.getFloatValue(), var2) / var6, 0.0F, 1.0F);
      var7 = var7 * var7 * (3.0F - 2.0F * var7);
      return new WeatherPresets.Data(
         var2,
         method5997(var5.getFloatValues(), var4.getFloatValues(), var7),
         method5997(var5.getFloatValues2(), var4.getFloatValues2(), var7),
         method5997(var5.getFloatValues3(), var4.getFloatValues3(), var7),
         method5997(var5.getFloatValues4(), var4.getFloatValues4(), var7),
         method5997(var5.getFloatValues5(), var4.getFloatValues5(), var7),
         method5996(var5.getFloatValue2(), var4.getFloatValue2(), var7),
         method5996(var5.getFloatValue3(), var4.getFloatValue3(), var7),
         method5996(var5.getFloatValue4(), var4.getFloatValue4(), var7),
         method5996(var5.getFloatValue5(), var4.getFloatValue5(), var7),
         method5996(var5.getFloatValue6(), var4.getFloatValue6(), var7),
         method5997(var5.getFloatValues6(), var4.getFloatValues6(), var7),
         method5996(var5.getFloatValue7(), var4.getFloatValue7(), var7),
         method5996(var5.getFloatValue8(), var4.getFloatValue8(), var7),
         method5996(var5.getFloatValue9(), var4.getFloatValue9(), var7),
         method5996(var5.getFloatValue10(), var4.getFloatValue10(), var7),
         method5996(var5.getFloatValue11(), var4.getFloatValue11(), var7),
         method5996(var5.getFloatValue12(), var4.getFloatValue12(), var7),
         method5998(var5.getIntValue(), var4.getIntValue(), var7),
         method5996(var5.getFloatValue13(), var4.getFloatValue13(), var7),
         method5996(var5.getFloatValue14(), var4.getFloatValue14(), var7)
      );
   }

   private static float method5995(float var0, float var1) {
      float var2 = var1 - var0;
      return var2 <= 0.0F ? var2 + 24.0F : var2;
   }

   private static float method5996(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static float[] method5997(float[] var0, float[] var1, float var2) {
      return new float[]{method5996(var0[0], var1[0], var2), method5996(var0[1], var1[1], var2), method5996(var0[2], var1[2], var2)};
   }

   private static int method5998(int var0, int var1, float var2) {
      int var3 = method5999(var0 >>> 24, var1 >>> 24, var2);
      int var4 = method5999(var0 >> 16 & 0xFF, var1 >> 16 & 0xFF, var2);
      int var5 = method5999(var0 >> 8 & 0xFF, var1 >> 8 & 0xFF, var2);
      int var6 = method5999(var0 & 0xFF, var1 & 0xFF, var2);
      return var3 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   private static int method5999(int var0, int var1, float var2) {
      return Math.clamp((long)((int)(var0 + (var1 - var0) * var2 + 0.5F)), 0, 255);
   }

   public static final class Data {
      private final float floatValue;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;
      private final float[] floatValues5;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float[] floatValues6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final int intValue;
      private final float floatValue13;
      private final float floatValue14;

      public float floatValue() {
         return this.floatValue;
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

      public float[] floatValues4() {
         return this.floatValues4;
      }

      public float[] floatValues5() {
         return this.floatValues5;
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

      public float[] floatValues6() {
         return this.floatValues6;
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

      public int intValue() {
         return this.intValue;
      }

      public float floatValue13() {
         return this.floatValue13;
      }

      public float floatValue14() {
         return this.floatValue14;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4) && java.util.Objects.equals(this.floatValues5, other.floatValues5) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValues6, other.floatValues6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.floatValue12, other.floatValue12) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue13, other.floatValue13) && java.util.Objects.equals(this.floatValue14, other.floatValue14);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValues, floatValues2, floatValues3, floatValues4, floatValues5, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValues6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, intValue, floatValue13, floatValue14);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + ", " + "floatValues5=" + this.floatValues5 + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValues6=" + this.floatValues6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "floatValue12=" + this.floatValue12 + ", " + "intValue=" + this.intValue + ", " + "floatValue13=" + this.floatValue13 + ", " + "floatValue14=" + this.floatValue14 + "]";
      }

      public Data(
         float var1,
         float[] var2,
         float[] var3,
         float[] var4,
         float[] var5,
         float[] var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float[] var12,
         float var13,
         float var14,
         float var15,
         float var16,
         float var17,
         float var18,
         int var19,
         float var20,
         float var21
      ) {
         this.floatValue = var1;
         this.floatValues = var2;
         this.floatValues2 = var3;
         this.floatValues3 = var4;
         this.floatValues4 = var5;
         this.floatValues5 = var6;
         this.floatValue2 = var7;
         this.floatValue3 = var8;
         this.floatValue4 = var9;
         this.floatValue5 = var10;
         this.floatValue6 = var11;
         this.floatValues6 = var12;
         this.floatValue7 = var13;
         this.floatValue8 = var14;
         this.floatValue9 = var15;
         this.floatValue10 = var16;
         this.floatValue11 = var17;
         this.floatValue12 = var18;
         this.intValue = var19;
         this.floatValue13 = var20;
         this.floatValue14 = var21;
      }

      public float getFloatValue() {
         return this.floatValue;
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

      public float[] getFloatValues4() {
         return this.floatValues4;
      }

      public float[] getFloatValues5() {
         return this.floatValues5;
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

      public float[] getFloatValues6() {
         return this.floatValues6;
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

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }
   }
}

package org.zenith;

import lombok.Generated;
import org.zenith.utility.render.MathUtils;

public interface Easing {
   Easing INSTANCE = method2832(0.45F, 1.45F, 0.49F, 1.15F);
   Easing INSTANCE2 = method2832(0.45F, 1.45F, 0.43F, 0.91F);
   Easing INSTANCE3 = method2832(0.1F, 1.07F, 0.34F, 1.04F);
   Easing INSTANCE4 = method2832(0.27F, 1.09F, 0.49F, 1.06F);
   Easing INSTANCE5 = method2832(0.62, -0.16, 0.8, 0.37);
   Easing INSTANCE6 = method2832(0.25, 1.07, 0.11, 1.1);
   Easing INSTANCE7 = method2832(0.42, 0.0, 0.58, 1.0);
   Easing INSTANCE8 = (var0, var1, var2, var3) -> {
      float var4 = var2 * var0 / var3 + var1;
      return (float)(-2.0 * Math.pow(var4, 3.0) + 3.0 * Math.pow(var4, 2.0));
   };
   Easing INSTANCE9 = (var0, var1, var2, var3) -> {
      float var4 = var2 * var0 / var3 + var1;
      return var4 < 0.5 ? 4.0F * var4 * var4 * var4 : (float)(1.0 - Math.pow(-2.0F * var4 + 2.0F, 3.0) / 2.0);
   };
   Easing INSTANCE10 = (var0, var1, var2, var3) -> var2 * var0 / var3 + var1;
   Easing INSTANCE11 = (var0, var1, var2, var3) -> {
      float var4;
      return var2 * (var4 = var0 / var3) * var4 + var1;
   };
   Easing INSTANCE12 = (var0, var1, var2, var3) -> {
      float var4;
      return -var2 * (var4 = var0 / var3) * (var4 - 2.0F) + var1;
   };
   Easing INSTANCE13 = (var0, var1, var2, var3) -> {
      float var4;
      return (var4 = var0 / (var3 / 2.0F)) < 1.0F ? var2 / 2.0F * var4 * var4 + var1 : -var2 / 2.0F * (--var4 * (var4 - 2.0F) - 1.0F) + var1;
   };
   Easing INSTANCE14 = (var0, var1, var2, var3) -> {
      float var4;
      return var2 * (var4 = var0 / var3) * var4 * var4 + var1;
   };
   Easing INSTANCE15 = (var0, var1, var2, var3) -> {
      float var4;
      return var2 * ((var4 = var0 / var3 - 1.0F) * var4 * var4 + 1.0F) + var1;
   };
   Easing INSTANCE16 = (var0, var1, var2, var3) -> {
      float var4;
      float var5;
      return (var4 = var0 / (var3 / 2.0F)) < 1.0F ? var2 / 2.0F * var4 * var4 * var4 + var1 : var2 / 2.0F * ((var5 = var4 - 2.0F) * var5 * var5 + 2.0F) + var1;
   };
   Easing INSTANCE17 = (var0, var1, var2, var3) -> {
      float var4;
      return var2 * (var4 = var0 / var3) * var4 * var4 * var4 + var1;
   };
   Easing INSTANCE18 = (var0, var1, var2, var3) -> {
      float var4;
      return -var2 * ((var4 = var0 / var3 - 1.0F) * var4 * var4 * var4 - 1.0F) + var1;
   };
   Easing INSTANCE19 = (var0, var1, var2, var3) -> {
      float var4;
      float var5;
      return (var4 = var0 / (var3 / 2.0F)) < 1.0F
         ? var2 / 2.0F * var4 * var4 * var4 * var4 + var1
         : -var2 / 2.0F * ((var5 = var4 - 2.0F) * var5 * var5 * var5 - 2.0F) + var1;
   };
   Easing INSTANCE20 = (var0, var1, var2, var3) -> {
      float var4;
      return var2 * (var4 = var0 / var3) * var4 * var4 * var4 * var4 + var1;
   };
   Easing INSTANCE21 = (var0, var1, var2, var3) -> {
      float var4;
      return var2 * ((var4 = var0 / var3 - 1.0F) * var4 * var4 * var4 * var4 + 1.0F) + var1;
   };
   Easing INSTANCE22 = (var0, var1, var2, var3) -> {
      float var4;
      float var5;
      return (var4 = var0 / (var3 / 2.0F)) < 1.0F
         ? var2 / 2.0F * var4 * var4 * var4 * var4 * var4 + var1
         : var2 / 2.0F * ((var5 = var4 - 2.0F) * var5 * var5 * var5 * var5 + 2.0F) + var1;
   };
   Easing INSTANCE23 = (var0, var1, var2, var3) -> -var2 * (float)MathUtils.cos(var0 / var3 * (Math.PI / 2)) + var2 + var1;
   Easing INSTANCE24 = (var0, var1, var2, var3) -> var2 * (float)MathUtils.sin(var0 / var3 * (Math.PI / 2)) + var1;
   Easing INSTANCE25 = (var0, var1, var2, var3) -> -var2 / 2.0F * ((float)MathUtils.cos(Math.PI * var0 / var3) - 1.0F) + var1;
   Easing INSTANCE26 = (var0, var1, var2, var3) -> var0 == 0.0F ? var1 : var2 * (float)Math.pow(2.0, 10.0F * (var0 / var3 - 1.0F)) + var1;
   Easing INSTANCE27 = (var0, var1, var2, var3) -> var0 == var3 ? var1 + var2 : var2 * (-((float)Math.pow(2.0, -10.0F * var0 / var3)) + 1.0F) + var1;
   Easing INSTANCE28 = (var0, var1, var2, var3) -> {
      if (var0 == 0.0F) {
         return var1;
      } else if (var0 == var3) {
         return var1 + var2;
      } else {
         float var4;
         return (var4 = var0 / (var3 / 2.0F)) < 1.0F
            ? var2 / 2.0F * (float)Math.pow(2.0, 10.0F * (var4 - 1.0F)) + var1
            : var2 / 2.0F * (-((float)Math.pow(2.0, -10.0F * --var4)) + 2.0F) + var1;
      }
   };
   Easing INSTANCE29 = (var0, var1, var2, var3) -> {
      float var4;
      return -var2 * ((float)Math.sqrt(1.0F - (var4 = var0 / var3) * var4) - 1.0F) + var1;
   };
   Easing INSTANCE30 = (var0, var1, var2, var3) -> {
      float var4;
      return var2 * (float)Math.sqrt(1.0F - (var4 = var0 / var3 - 1.0F) * var4) + var1;
   };
   Easing INSTANCE31 = (var0, var1, var2, var3) -> {
      float var4;
      float var5;
      return (var4 = var0 / (var3 / 2.0F)) < 1.0F
         ? -var2 / 2.0F * ((float)Math.sqrt(1.0F - var4 * var4) - 1.0F) + var1
         : var2 / 2.0F * ((float)Math.sqrt(1.0F - (var5 = var4 - 2.0F) * var5) + 1.0F) + var1;
   };
   Easing.Entry8 entry8 = new Easing.Entry6();
   Easing.Entry8 entry82 = new Easing.Entry4();
   Easing.Entry8 entry83 = new Easing.Entry();
   Easing.Entry2 entry2 = new Easing.Entry5();
   Easing.Entry2 entry22 = new Easing.Entry7();
   Easing.Entry2 entry23 = new Easing.Entry3();
   Easing INSTANCE32 = (var0, var1, var2, var3) -> {
      if ((var0 = var0 / var3) < 0.36363637F) {
         return var2 * (7.5625F * var0 * var0) + var1;
      } else if (var0 < 0.72727275F) {
         float var7;
         return var2 * (7.5625F * (var7 = var0 - 0.54545456F) * var7 + 0.75F) + var1;
      } else {
         float var5;
         float var6;
         return var0 < 0.90909094F
            ? var2 * (7.5625F * (var5 = var0 - 0.8181818F) * var5 + 0.9375F) + var1
            : var2 * (7.5625F * (var6 = var0 - 0.95454544F) * var6 + 0.984375F) + var1;
      }
   };
   Easing INSTANCE33 = (var0, var1, var2, var3) -> var2 - INSTANCE32.ease(var3 - var0, 0.0F, var2, var3) + var1;
   Easing INSTANCE34 = (var0, var1, var2, var3) -> var0 < var3 / 2.0F
      ? INSTANCE33.ease(var0 * 2.0F, 0.0F, var2, var3) * 0.5F + var1
      : INSTANCE32.ease(var0 * 2.0F - var3, 0.0F, var2, var3) * 0.5F + var2 * 0.5F + var1;

   static Easing method2832(final double var0, final double var2, final double var4, final double var6) {
      return new Easing() {
         @Override
         public float ease(float var1, float var2x, float var3, float var4x) {
            if (var4x <= 0.0F || var1 <= 0.0F) {
               return var2x;
            } else if (var1 >= var4x) {
               return var2x + var3;
            } else {
               float var5 = var1 / var4x;
               float var6x = this.method2833((float)var0, (float)var4, var5);
               float var7 = this.method2836(var6x, (float)var2, (float)var6);
               return var2x + var3 * var7;
            }
         }

         private float method2833(float var1, float var2x, float var3) {
            float var4x = var3;
            byte var5 = 8;
            float var6x = 1.0E-5F;

            for (int var7 = 0; var7 < 8; var7++) {
               float var8 = this.method2834(var4x, var1, var2x);
               float var9 = this.method2835(var4x, var1, var2x);
               if (Math.abs(var8 - var3) < 1.0E-5F || Math.abs(var9) < 1.0E-6F) {
                  break;
               }

               var4x -= (var8 - var3) / var9;
               var4x = Math.max(0.0F, Math.min(1.0F, var4x));
            }

            return var4x;
         }

         private float method2834(float var1, float var2x, float var3) {
            return 3.0F * (1.0F - var1) * (1.0F - var1) * var1 * var2x + 3.0F * (1.0F - var1) * var1 * var1 * var3 + var1 * var1 * var1;
         }

         private float method2835(float var1, float var2x, float var3) {
            return 3.0F * ((1.0F - var1) * (1.0F - 3.0F * var1) * var2x + (2.0F * var1 - 3.0F * var1 * var1) * var3) + 3.0F * var1 * var1;
         }

         private float method2836(float var1, float var2x, float var3) {
            return 3.0F * (1.0F - var1) * (1.0F - var1) * var1 * var2x + 3.0F * (1.0F - var1) * var1 * var1 * var3 + var1 * var1 * var1;
         }
      };
   }

   float ease(float var1, float var2, float var3, float var4);

   public static class Entry extends Easing.Entry8 {
      public Entry(float var1, float var2) {
         super(var1, var2);
      }

      public Entry() {
      }

      @Override
      public float ease(float var1, float var2, float var3, float var4) {
         float var5 = this.getFloatValue();
         float var6 = this.getFloatValue2();
         if (var1 == 0.0F) {
            return var2;
         } else if ((var1 = var1 / (var4 / 2.0F)) == 2.0F) {
            return var2 + var3;
         } else {
            if (var6 == 0.0F) {
               var6 = var4 * 0.45000002F;
            }

            float var7 = 0.0F;
            if (var5 < Math.abs(var3)) {
               var5 = var3;
               var7 = var6 / 4.0F;
            } else {
               var7 = var6 / (float) (Math.PI * 2) * (float)Math.asin(var3 / var5);
            }

            return var1 < 1.0F
               ? -0.5F * (var5 * (float)Math.pow(2.0, 10.0F * --var1) * (float)MathUtils.sin((var1 * var4 - var7) * (Math.PI * 2) / var6)) + var2
               : var5 * (float)Math.pow(2.0, -10.0F * --var1) * (float)MathUtils.sin((var1 * var4 - var7) * (Math.PI * 2) / var6) * 0.5F + var3 + var2;
         }
      }
   }

   public abstract static class Entry2 implements Easing {
      public static final float CONST_floatValue = 1.70158F;
      private float floatValue;

      public Entry2() {
         this(1.70158F);
      }

      public Entry2(float var1) {
         this.floatValue = var1;
      }

      @Generated
      public void setFloatValue(float var1) {
         this.floatValue = var1;
      }

      @Generated
      public float getFloatValue() {
         return this.floatValue;
      }
   }

   public static class Entry3 extends Easing.Entry2 {
      public Entry3() {
      }

      public Entry3(float var1) {
         super(var1);
      }

      @Override
      public float ease(float var1, float var2, float var3, float var4) {
         float var5 = this.getFloatValue();
         float var6;
         float var7;
         float var8;
         float var9;
         return (var6 = var1 / (var4 / 2.0F)) < 1.0F
            ? var3 / 2.0F * (var6 * var6 * (((var8 = var5 * 1.525F) + 1.0F) * var6 - var8)) + var2
            : var3 / 2.0F * ((var7 = var6 - 2.0F) * var7 * (((var9 = var5 * 1.525F) + 1.0F) * var7 + var9) + 2.0F) + var2;
      }
   }

   public static class Entry4 extends Easing.Entry8 {
      public Entry4(float var1, float var2) {
         super(var1, var2);
      }

      public Entry4() {
      }

      @Override
      public float ease(float var1, float var2, float var3, float var4) {
         float var5 = this.getFloatValue();
         float var6 = this.getFloatValue2();
         if (var1 == 0.0F) {
            return var2;
         } else if ((var1 = var1 / var4) == 1.0F) {
            return var2 + var3;
         } else {
            if (var6 == 0.0F) {
               var6 = var4 * 0.3F;
            }

            float var7 = 0.0F;
            if (var5 < Math.abs(var3)) {
               var5 = var3;
               var7 = var6 / 4.0F;
            } else {
               var7 = var6 / (float) (Math.PI * 2) * (float)Math.asin(var3 / var5);
            }

            return var5 * (float)Math.pow(2.0, -10.0F * var1) * (float)MathUtils.sin((var1 * var4 - var7) * (Math.PI * 2) / var6) + var3 + var2;
         }
      }
   }

   public static class Entry5 extends Easing.Entry2 {
      public Entry5() {
      }

      public Entry5(float var1) {
         super(var1);
      }

      @Override
      public float ease(float var1, float var2, float var3, float var4) {
         float var5 = this.getFloatValue();
         float var6;
         return var3 * (var6 = var1 / var4) * var6 * ((var5 + 1.0F) * var6 - var5) + var2;
      }
   }

   public static class Entry6 extends Easing.Entry8 {
      public Entry6(float var1, float var2) {
         super(var1, var2);
      }

      public Entry6() {
      }

      @Override
      public float ease(float var1, float var2, float var3, float var4) {
         float var5 = this.getFloatValue();
         float var6 = this.getFloatValue2();
         if (var1 == 0.0F) {
            return var2;
         } else if ((var1 = var1 / var4) == 1.0F) {
            return var2 + var3;
         } else {
            if (var6 == 0.0F) {
               var6 = var4 * 0.3F;
            }

            float var7 = 0.0F;
            if (var5 < Math.abs(var3)) {
               var5 = var3;
               var7 = var6 / 4.0F;
            } else {
               var7 = var6 / (float) (Math.PI * 2) * (float)Math.asin(var3 / var5);
            }

            return -(var5 * (float)Math.pow(2.0, 10.0F * --var1) * (float)MathUtils.sin((var1 * var4 - var7) * (Math.PI * 2) / var6)) + var2;
         }
      }
   }

   public static class Entry7 extends Easing.Entry2 {
      public Entry7() {
      }

      public Entry7(float var1) {
         super(var1);
      }

      @Override
      public float ease(float var1, float var2, float var3, float var4) {
         float var5 = this.getFloatValue();
         float var6;
         return var3 * ((var6 = var1 / var4 - 1.0F) * var6 * ((var5 + 1.0F) * var6 + var5) + 1.0F) + var2;
      }
   }

   public abstract static class Entry8 implements Easing {
      private float floatValue;
      private float floatValue2;

      public Entry8(float var1, float var2) {
         this.floatValue = var1;
         this.floatValue2 = var2;
      }

      public Entry8() {
         this(-1.0F, 0.0F);
      }

      @Generated
      public void setFloatValue(float var1) {
         this.floatValue = var1;
      }

      @Generated
      public void setFloatValue2(float var1) {
         this.floatValue2 = var1;
      }

      @Generated
      public float getFloatValue() {
         return this.floatValue;
      }

      @Generated
      public float getFloatValue2() {
         return this.floatValue2;
      }
   }
}

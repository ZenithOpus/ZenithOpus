package org.zenith.utility.misc;

public final class AnimationState {
   private static final float CONST_floatValue = 0.38F;
   private static final float CONST_floatValue2 = 0.26F;
   private final float[] floatValues = new float[9];
   private String text = "idle";
   private float floatValue;
   private String current = "idle";
   private float floatValue2;
   private String previous;
   private float floatValue3;
   private float floatValue4 = -10.0F;
   private float floatValue5 = 0.38F;
   private boolean flag;
   private float speed = 1.0F;
   private float floatValue6 = 1.0F;
   private float floatValue7 = Float.NaN;
   private float[] floatValues2 = new float[0];
   private float[] floatValues3 = new float[0];
   private boolean frozen;

   public String getCurrent() {
      return this.current;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void setFloatValue6(float var1) {
      this.floatValue6 = var1;
   }

   public void method6539(Class15 var1, String var2, float var3) {
      String var4 = method6545(var1, var2);
      if (var4 != null && !var4.equals(this.text)) {
         this.text = var4;
         this.floatValue = 0.0F;
         if (!this.flag) {
            this.method6544(var4, var3, 0.0F, 0.38F);
         }
      }
   }

   public boolean method6540(Class15 var1, String var2, float var3) {
      Class37 var4 = var1.method2032(var2);
      if (var4 == null) {
         return false;
      } else {
         this.flag = !var4.isFlag();
         this.method6544(var2, var3, 0.0F, 0.26F);
         return true;
      }
   }

   public void method6541(float var1) {
      this.flag = false;
      this.method6544(this.text, var1, this.floatValue, 0.38F);
   }

   public void method6542(Class15 var1, float var2) {
      this.method6543(var2);
      if (this.flag) {
         Class37 var3 = var1.method2032(this.current);
         if (var3 == null || this.floatValue2 >= var3.getFloatValue()) {
            this.flag = false;
            this.method6544(this.text, var2, this.floatValue, 0.38F);
         }
      }
   }

   private void method6543(float var1) {
      if (Float.isNaN(this.floatValue7)) {
         this.floatValue7 = var1;
      } else {
         float var2 = Math.max(0.0F, Math.min(0.25F, var1 - this.floatValue7));
         this.floatValue7 = var1;
         if (!(var2 <= 0.0F)) {
            this.speed = this.speed + (this.floatValue6 - this.speed) * Math.min(1.0F, var2 * 6.0F);
            boolean var3 = !this.flag;
            float var4 = var3 ? this.speed : 1.0F;
            this.floatValue2 += var2 * var4;
            if (this.previous != null) {
               this.floatValue3 += var2 * var4;
            }

            if (this.flag) {
               this.floatValue += var2;
            } else {
               this.floatValue = this.floatValue2;
            }
         }
      }
   }

   private void method6544(String var1, float var2, float var3, float var4) {
      if (!var1.equals(this.current)) {
         float var5 = (var2 - this.floatValue4) / this.floatValue5;
         if (var5 < 1.0F && this.floatValues2.length > 0) {
            if (this.floatValues3.length != this.floatValues2.length) {
               this.floatValues3 = new float[this.floatValues2.length];
            }

            System.arraycopy(this.floatValues2, 0, this.floatValues3, 0, this.floatValues2.length);
            this.frozen = true;
         } else {
            this.frozen = false;
         }

         this.previous = this.current;
         this.floatValue3 = this.floatValue2;
         this.current = var1;
         this.floatValue2 = var3;
         this.floatValue4 = var2;
         this.floatValue5 = var4;
      }
   }

   private static String method6545(Class15 var0, String var1) {
      if (var0.method2032(var1) != null) {
         return var1;
      } else {
         return switch (var1) {
            case "run" -> var0.method2032("walk") != null ? "walk" : method6545(var0, "idle");
            case "perch" -> var0.method2032("sit") != null ? "sit" : method6545(var0, "idle");
            case "walk", "fly", "sleep", "sneak", "glide", "idle2", "sit", "lie" -> method6545(var0, "idle");
            case "idle" -> var0.method2032("idle") != null ? "idle" : null;
            default -> method6545(var0, "idle");
         };
      }
   }

   public void method6546(Class15 var1, int var2, float var3, float[] var4) {
      this.method6543(var3);
      Class37 var5 = var1.method2032(this.current);
      if (var5 == null) {
         method6550(var4);
      } else {
         var5.method6242(var2, var5.method6240(this.floatValue2), var4);
      }

      float var6 = (var3 - this.floatValue4) / this.floatValue5;
      if (var6 < 1.0F && this.previous != null) {
         float var7 = method6549(Math.max(0.0F, var6));
         if (this.frozen && var2 * 9 + 9 <= this.floatValues3.length) {
            for (int var10 = 0; var10 < 9; var10++) {
               float var11 = this.floatValues3[var2 * 9 + var10];
               var4[var10] = var11 + (var4[var10] - var11) * var7;
            }
         } else {
            Class37 var8 = var1.method2032(this.previous);
            if (var8 != null) {
               var8.method6242(var2, var8.method6240(this.floatValue3), this.floatValues);

               for (int var9 = 0; var9 < 9; var9++) {
                  var4[var9] = this.floatValues[var9] + (var4[var9] - this.floatValues[var9]) * var7;
               }
            }
         }
      }

      this.method6547(var2, var4);
   }

   private void method6547(int var1, float[] var2) {
      int var3 = (var1 + 1) * 9;
      if (this.floatValues2.length < var3) {
         float[] var4 = new float[Math.max(var3, this.floatValues2.length * 2)];
         System.arraycopy(this.floatValues2, 0, var4, 0, this.floatValues2.length);

         for (int var5 = this.floatValues2.length; var5 < var4.length; var5 += 9) {
            var4[var5 + 6] = var4[var5 + 7] = var4[var5 + 8] = 1.0F;
         }

         this.floatValues2 = var4;
      }

      System.arraycopy(var2, 0, this.floatValues2, var1 * 9, 9);
   }

   public void method6548(Class15 var1, String var2, int var3, float var4, float[] var5) {
      Class37 var6 = var1.method2032(var2);
      if (var6 == null) {
         method6550(var5);
      } else {
         var6.method6242(var3, Math.max(0.0F, Math.min(1.0F, var4)) * var6.getFloatValue(), var5);
      }
   }

   private static float method6549(float var0) {
      return var0 * var0 * var0 * (var0 * (var0 * 6.0F - 15.0F) + 10.0F);
   }

   private static void method6550(float[] var0) {
      var0[0] = var0[1] = var0[2] = 0.0F;
      var0[3] = var0[4] = var0[5] = 0.0F;
      var0[6] = var0[7] = var0[8] = 1.0F;
   }
}

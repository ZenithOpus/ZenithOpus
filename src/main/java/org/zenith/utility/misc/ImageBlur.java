package org.zenith.utility.misc;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;
import org.jetbrains.annotations.NotNull;

public class ImageBlur {
   public float floatValue;
   public Kernel kernel;

   public ImageBlur(float var1) {
      this.method2286(var1);
   }

   public static void method2283(@NotNull Kernel var0, int[] var1, int[] var2, int var3, int var4, boolean var5, boolean var6, boolean var7, int var8) {
      float[] var9 = var0.getKernelData(null);
      int var10 = var0.getWidth();
      int var11 = var10 / 2;

      for (int var12 = 0; var12 < var4; var12++) {
         int var13 = var12;
         int var14 = var12 * var3;

         for (int var15 = 0; var15 < var3; var15++) {
            float var16 = 0.0F;
            float var17 = 0.0F;
            float var18 = 0.0F;
            float var19 = 0.0F;
            int var20 = var11;

            for (int var21 = -var11; var21 <= var11; var21++) {
               float var22 = var9[var20 + var21];
               if (var22 != 0.0F) {
                  int var23 = var15 + var21;
                  if (var23 < 0) {
                     if (var8 == 1) {
                        var23 = 0;
                     } else if (var8 == 2) {
                        var23 = (var15 + var3) % var3;
                     }
                  } else if (var23 >= var3) {
                     if (var8 == 1) {
                        var23 = var3 - 1;
                     } else if (var8 == 2) {
                        var23 = (var15 + var3) % var3;
                     }
                  }

                  int var24 = var1[var14 + var23];
                  int var25 = var24 >> 24 & 0xFF;
                  int var26 = var24 >> 16 & 0xFF;
                  int var27 = var24 >> 8 & 0xFF;
                  int var28 = var24 & 0xFF;
                  if (var6) {
                     float var29 = var25 * 0.003921569F;
                     var26 = (int)(var26 * var29);
                     var27 = (int)(var27 * var29);
                     var28 = (int)(var28 * var29);
                  }

                  var19 += var22 * var25;
                  var16 += var22 * var26;
                  var17 += var22 * var27;
                  var18 += var22 * var28;
               }
            }

            if (var7 && var19 != 0.0F && var19 != 255.0F) {
               float var30 = 255.0F / var19;
               var16 *= var30;
               var17 *= var30;
               var18 *= var30;
            }

            int var31 = var5 ? method2284((int)(var19 + 0.5)) : 255;
            int var32 = method2284((int)(var16 + 0.5));
            int var33 = method2284((int)(var17 + 0.5));
            int var34 = method2284((int)(var18 + 0.5));
            var2[var13] = var31 << 24 | var32 << 16 | var33 << 8 | var34;
            var13 += var4;
         }
      }
   }

   public static int method2284(int var0) {
      return var0 < 0 ? 0 : Math.min(var0, 255);
   }

   public static Kernel method2285(float var0) {
      int var1 = (int)Math.ceil(var0);
      int var2 = var1 * 2 + 1;
      float[] var3 = new float[var2];
      float var4 = var0 / 3.0F;
      float var5 = 2.0F * var4 * var4;
      float var6 = (float) (Math.PI * 2) * var4;
      float var7 = (float)Math.sqrt(var6);
      float var8 = var0 * var0;
      float var9 = 0.0F;
      int var10 = 0;

      for (int var11 = -var1; var11 <= var1; var11++) {
         float var12 = var11 * var11;
         if (var12 > var8) {
            var3[var10] = 0.0F;
         } else {
            var3[var10] = (float)Math.exp(-var12 / var5) / var7;
         }

         var9 += var3[var10];
         var10++;
      }

      for (int var13 = 0; var13 < var2; var13++) {
         var3[var13] /= var9;
      }

      return new Kernel(var2, 1, var3);
   }

   public void method2286(float var1) {
      this.floatValue = var1;
      this.kernel = method2285(var1);
   }

   public BufferedImage method2287(BufferedImage var1, BufferedImage var2) {
      int var3 = var1.getWidth();
      int var4 = var1.getHeight();
      if (var2 == null) {
         var2 = this.method2288(var1, null);
      }

      int[] var5 = new int[var3 * var4];
      int[] var6 = new int[var3 * var4];
      var1.getRGB(0, 0, var3, var4, var5, 0, var3);
      if (this.floatValue > 0.0F) {
         method2283(this.kernel, var5, var6, var3, var4, true, true, false, 1);
         method2283(this.kernel, var6, var5, var4, var3, true, false, true, 1);
      }

      var2.setRGB(0, 0, var3, var4, var5, 0, var3);
      return var2;
   }

   public BufferedImage method2288(BufferedImage var1, ColorModel var2) {
      if (var2 == null) {
         var2 = var1.getColorModel();
      }

      return new BufferedImage(var2, var2.createCompatibleWritableRaster(var1.getWidth(), var1.getHeight()), var2.isAlphaPremultiplied(), null);
   }
}

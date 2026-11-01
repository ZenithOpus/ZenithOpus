package org.zenith.utility.misc;

import java.util.Arrays;

public final class MeshImporter {
   private static final int[][] CONST_intValues = new int[][]{{0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {1, 1, 0}, {0, 0, 1}, {1, 0, 1}, {0, 1, 1}, {1, 1, 1}};
   private static final int[][] CONST_intValues2 = new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}, {0, 2}, {1, 3}, {4, 6}, {5, 7}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};

   private MeshImporter() {
   }

   public static MeshImporter.Entry method658(Class32 var0, FloatArrayBuffer var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      MeshImporter.Entry var8 = new MeshImporter.Entry();
      var1.clear();
      if (var5 > 1 && var6 > 1 && var7 > 1) {
         int[] var9 = new int[var5 * var6 * var7];
         Arrays.fill(var9, -1);
         float[] var10 = new float[1024];
         float[] var11 = new float[1024];
         float[] var12 = new float[1024];
         float[] var13 = new float[1024];
         float[] var14 = new float[1024];
         float[] var15 = new float[1024];
         int var16 = 0;
         float[] var17 = new float[8];

         for (int var18 = 0; var18 < var7 - 1; var18++) {
            for (int var19 = 0; var19 < var6 - 1; var19++) {
               for (int var20 = 0; var20 < var5 - 1; var20++) {
                  int var21 = 0;

                  for (int var22 = 0; var22 < 8; var22++) {
                     var17[var22] = var0.method4903(
                        var2 + var20 + CONST_intValues[var22][0], var3 + var19 + CONST_intValues[var22][1], var4 + var18 + CONST_intValues[var22][2]
                     );
                     if (var17[var22] >= 0.0F) {
                        var21++;
                     }
                  }

                  if (var21 != 0 && var21 != 8) {
                     float var37 = 0.0F;
                     float var23 = 0.0F;
                     float var24 = 0.0F;
                     int var25 = 0;

                     for (int[] var29 : CONST_intValues2) {
                        float var30 = var17[var29[0]];
                        float var31 = var17[var29[1]];
                        if (var30 >= 0.0F != var31 >= 0.0F) {
                           float var32 = var30 / (var30 - var31);
                           var37 += CONST_intValues[var29[0]][0] + var32 * (CONST_intValues[var29[1]][0] - CONST_intValues[var29[0]][0]);
                           var23 += CONST_intValues[var29[0]][1] + var32 * (CONST_intValues[var29[1]][1] - CONST_intValues[var29[0]][1]);
                           var24 += CONST_intValues[var29[0]][2] + var32 * (CONST_intValues[var29[1]][2] - CONST_intValues[var29[0]][2]);
                           var25++;
                        }
                     }

                     if (var25 != 0) {
                        if (var16 == var10.length) {
                           var10 = method662(var10);
                           var11 = method662(var11);
                           var12 = method662(var12);
                           var13 = method662(var13);
                           var14 = method662(var14);
                           var15 = method662(var15);
                        }

                        var10[var16] = var2 + var20 + var37 / var25;
                        var11[var16] = var3 + var19 + var23 / var25;
                        var12[var16] = var4 + var18 + var24 / var25;
                        float var41 = var17[0] + var17[2] + var17[4] + var17[6] - (var17[1] + var17[3] + var17[5] + var17[7]);
                        float var43 = var17[0] + var17[1] + var17[4] + var17[5] - (var17[2] + var17[3] + var17[6] + var17[7]);
                        float var45 = var17[0] + var17[1] + var17[2] + var17[3] - (var17[4] + var17[5] + var17[6] + var17[7]);
                        float var47 = (float)Math.sqrt(var41 * var41 + var43 * var43 + var45 * var45);
                        if (var47 < 1.0E-6F) {
                           var41 = 0.0F;
                           var43 = 1.0F;
                           var45 = 0.0F;
                        } else {
                           var41 /= var47;
                           var43 /= var47;
                           var45 /= var47;
                        }

                        var13[var16] = var41;
                        var14[var16] = var43;
                        var15[var16] = var45;
                        var9[method661(var20, var19, var18, var5, var6)] = var16++;
                     }
                  }
               }
            }
         }

         var8.intValue = var16;
         float[] var33 = new float[12];
         float[] var34 = new float[12];

         for (int var35 = 1; var35 < var7 - 1; var35++) {
            for (int var36 = 1; var36 < var6 - 1; var36++) {
               for (int var38 = 1; var38 < var5 - 1; var38++) {
                  float var39 = var0.method4903(var2 + var38, var3 + var36, var4 + var35);
                  boolean var40 = var39 >= 0.0F;
                  if (var40 != var0.method4903(var2 + var38 + 1, var3 + var36, var4 + var35) >= 0.0F) {
                     method659(
                        var1,
                        var9,
                        var5,
                        var6,
                        var33,
                        var34,
                        var10,
                        var11,
                        var12,
                        var13,
                        var14,
                        var15,
                        var38,
                        var36 - 1,
                        var35 - 1,
                        var38,
                        var36,
                        var35 - 1,
                        var38,
                        var36,
                        var35,
                        var38,
                        var36 - 1,
                        var35,
                        var40,
                        var8
                     );
                  }

                  if (var40 != var0.method4903(var2 + var38, var3 + var36 + 1, var4 + var35) >= 0.0F) {
                     method659(
                        var1,
                        var9,
                        var5,
                        var6,
                        var33,
                        var34,
                        var10,
                        var11,
                        var12,
                        var13,
                        var14,
                        var15,
                        var38 - 1,
                        var36,
                        var35 - 1,
                        var38 - 1,
                        var36,
                        var35,
                        var38,
                        var36,
                        var35,
                        var38,
                        var36,
                        var35 - 1,
                        var40,
                        var8
                     );
                  }

                  if (var40 != var0.method4903(var2 + var38, var3 + var36, var4 + var35 + 1) >= 0.0F) {
                     method659(
                        var1,
                        var9,
                        var5,
                        var6,
                        var33,
                        var34,
                        var10,
                        var11,
                        var12,
                        var13,
                        var14,
                        var15,
                        var38 - 1,
                        var36 - 1,
                        var35,
                        var38,
                        var36 - 1,
                        var35,
                        var38,
                        var36,
                        var35,
                        var38 - 1,
                        var36,
                        var35,
                        var40,
                        var8
                     );
                  }
               }
            }
         }

         return var8;
      } else {
         return var8;
      }
   }

   private static void method659(
      FloatArrayBuffer var0,
      int[] var1,
      int var2,
      int var3,
      float[] var4,
      float[] var5,
      float[] var6,
      float[] var7,
      float[] var8,
      float[] var9,
      float[] var10,
      float[] var11,
      int var12,
      int var13,
      int var14,
      int var15,
      int var16,
      int var17,
      int var18,
      int var19,
      int var20,
      int var21,
      int var22,
      int var23,
      boolean var24,
      MeshImporter.Entry var25
   ) {
      int var26 = method660(var1, var2, var3, var12, var13, var14);
      int var27 = method660(var1, var2, var3, var15, var16, var17);
      int var28 = method660(var1, var2, var3, var18, var19, var20);
      int var29 = method660(var1, var2, var3, var21, var22, var23);
      if (var26 >= 0 && var27 >= 0 && var28 >= 0 && var29 >= 0) {
         int[] var30 = var24 ? new int[]{var26, var27, var28, var29} : new int[]{var26, var29, var28, var27};

         for (int var31 = 0; var31 < 4; var31++) {
            int var32 = var30[var31];
            var4[var31 * 3] = var6[var32];
            var4[var31 * 3 + 1] = var7[var32];
            var4[var31 * 3 + 2] = var8[var32];
            var5[var31 * 3] = var9[var32];
            var5[var31 * 3 + 1] = var10[var32];
            var5[var31 * 3 + 2] = var11[var32];
         }

         var0.method379(var4, var5);
      } else {
         var25.skipped++;
      }
   }

   private static int method660(int[] var0, int var1, int var2, int var3, int var4, int var5) {
      return var3 >= 0 && var4 >= 0 && var5 >= 0 ? var0[method661(var3, var4, var5, var1, var2)] : -1;
   }

   private static int method661(int var0, int var1, int var2, int var3, int var4) {
      return (var2 * var4 + var1) * var3 + var0;
   }

   private static float[] method662(float[] var0) {
      float[] var1 = new float[var0.length * 2];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }

   public static final class Entry {
      public int intValue;
      public int skipped;

      @Override
      public String toString() {
         return "вершин " + this.intValue + ", пропущено граней " + this.skipped;
      }
   }
}

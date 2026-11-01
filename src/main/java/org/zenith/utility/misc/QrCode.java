package org.zenith.utility.misc;

import java.io.ByteArrayOutputStream;

public final class QrCode {
   private static final int[] CONST_intValues = new int[]{0, 26, 44, 70, 100, 134, 172, 196, 242, 292, 346};
   private static final int[] CONST_intValues2 = new int[]{0, 10, 16, 26, 18, 24, 16, 18, 22, 22, 26};
   private static final int[] CONST_intValues3 = new int[]{0, 1, 1, 1, 2, 2, 4, 4, 4, 5, 5};
   private static final int[][] CONST_intValues4 = new int[][]{
      new int[0], new int[0], {6, 18}, {6, 22}, {6, 26}, {6, 30}, {6, 34}, {6, 22, 38}, {6, 24, 42}, {6, 26, 46}, {6, 28, 50}
   };
   private final int intValue;
   private final int intValue2;
   private final boolean[][] flags;
   private final boolean[][] flags2;

   private QrCode(int var1) {
      this.intValue = var1;
      this.intValue2 = var1 * 4 + 17;
      this.flags = new boolean[this.intValue2][this.intValue2];
      this.flags2 = new boolean[this.intValue2][this.intValue2];
   }

   public static boolean[][] method3020(byte[] var0) {
      int var1;
      for (var1 = 1; var1 <= 10; var1++) {
         int var2 = (CONST_intValues[var1] - CONST_intValues2[var1] * CONST_intValues3[var1]) * 8;
         int var3 = 4 + (var1 < 10 ? 8 : 16) + var0.length * 8;
         if (var3 <= var2) {
            break;
         }
      }

      if (var1 > 10) {
         throw new IllegalArgumentException("текст слишком длинный для QR (до 213 байт)");
      } else {
         int var13 = CONST_intValues[var1] - CONST_intValues2[var1] * CONST_intValues3[var1];
         QrCode.Entry var14 = new QrCode.Entry();
         var14.method3033(4, 4);
         var14.method3033(var0.length, var1 < 10 ? 8 : 16);

         for (int var7 : var0) {
            var14.method3033(var7 & 255, 8);
         }

         int var15 = var13 * 8;
         var14.method3033(0, Math.min(4, var15 - var14.length));
         var14.method3033(0, (8 - var14.length % 8) % 8);

         for (short var16 = 236; var14.length < var15; var16 ^= 253) {
            var14.method3033(var16, 8);
         }

         byte[] var17 = var14.method3034();
         QrCode var18 = new QrCode(var1);
         var18.method3022();
         var18.method3030(var18.method3026(var17));
         int var19 = 0;
         long var8 = Long.MAX_VALUE;

         for (int var10 = 0; var10 < 8; var10++) {
            var18.method3031(var10);
            var18.method3025(var10);
            long var11 = var18.method3032();
            if (var11 < var8) {
               var8 = var11;
               var19 = var10;
            }

            var18.method3031(var10);
         }

         var18.method3031(var19);
         var18.method3025(var19);
         return var18.flags;
      }
   }

   private void method3021(int var1, int var2, boolean var3) {
      this.flags[var2][var1] = var3;
      this.flags2[var2][var1] = true;
   }

   private void method3022() {
      for (int var1 = 0; var1 < this.intValue2; var1++) {
         this.method3021(6, var1, var1 % 2 == 0);
         this.method3021(var1, 6, var1 % 2 == 0);
      }

      this.method3023(3, 3);
      this.method3023(this.intValue2 - 4, 3);
      this.method3023(3, this.intValue2 - 4);
      int[] var8 = CONST_intValues4[this.intValue];

      for (int var2 = 0; var2 < var8.length; var2++) {
         for (int var3 = 0; var3 < var8.length; var3++) {
            boolean var4 = var2 == 0 && var3 == 0 || var2 == 0 && var3 == var8.length - 1 || var2 == var8.length - 1 && var3 == 0;
            if (!var4) {
               this.method3024(var8[var2], var8[var3]);
            }
         }
      }

      this.method3025(0);
      if (this.intValue >= 7) {
         int var9 = this.intValue;

         for (int var10 = 0; var10 < 12; var10++) {
            var9 = var9 << 1 ^ (var9 >>> 11) * 7973;
         }

         int var11 = this.intValue << 12 | var9;

         for (int var12 = 0; var12 < 18; var12++) {
            boolean var5 = (var11 >>> var12 & 1) != 0;
            int var6 = this.intValue2 - 11 + var12 % 3;
            int var7 = var12 / 3;
            this.method3021(var6, var7, var5);
            this.method3021(var7, var6, var5);
         }
      }
   }

   private void method3023(int var1, int var2) {
      for (int var3 = -4; var3 <= 4; var3++) {
         for (int var4 = -4; var4 <= 4; var4++) {
            int var5 = Math.max(Math.abs(var4), Math.abs(var3));
            int var6 = var1 + var4;
            int var7 = var2 + var3;
            if (var6 >= 0 && var6 < this.intValue2 && var7 >= 0 && var7 < this.intValue2) {
               this.method3021(var6, var7, var5 != 2 && var5 != 4);
            }
         }
      }
   }

   private void method3024(int var1, int var2) {
      for (int var3 = -2; var3 <= 2; var3++) {
         for (int var4 = -2; var4 <= 2; var4++) {
            this.method3021(var1 + var4, var2 + var3, Math.max(Math.abs(var4), Math.abs(var3)) != 1);
         }
      }
   }

   private void method3025(int var1) {
      int var3 = var1;

      for (int var4 = 0; var4 < 10; var4++) {
         var3 = var3 << 1 ^ (var3 >>> 9) * 1335;
      }

      int var6 = (var1 << 10 | var3) ^ 21522;

      for (int var5 = 0; var5 <= 5; var5++) {
         this.method3021(8, var5, (var6 >>> var5 & 1) != 0);
      }

      this.method3021(8, 7, (var6 >>> 6 & 1) != 0);
      this.method3021(8, 8, (var6 >>> 7 & 1) != 0);
      this.method3021(7, 8, (var6 >>> 8 & 1) != 0);

      for (int var7 = 9; var7 < 15; var7++) {
         this.method3021(14 - var7, 8, (var6 >>> var7 & 1) != 0);
      }

      for (int var8 = 0; var8 < 8; var8++) {
         this.method3021(this.intValue2 - 1 - var8, 8, (var6 >>> var8 & 1) != 0);
      }

      for (int var9 = 8; var9 < 15; var9++) {
         this.method3021(8, this.intValue2 - 15 + var9, (var6 >>> var9 & 1) != 0);
      }

      this.method3021(8, this.intValue2 - 8, true);
   }

   private byte[] method3026(byte[] var1) {
      int var2 = CONST_intValues3[this.intValue];
      int var3 = CONST_intValues2[this.intValue];
      int var4 = CONST_intValues[this.intValue];
      int var5 = var2 - var4 % var2;
      int var6 = var4 / var2;
      byte[] var7 = method3027(var3);
      byte[][] var8 = new byte[var2][];
      int var9 = 0;

      for (int var10 = 0; var9 < var2; var9++) {
         int var11 = var6 - var3 + (var9 < var5 ? 0 : 1);
         byte[] var12 = new byte[var11];
         System.arraycopy(var1, var10, var12, 0, var11);
         var10 += var11;
         byte[] var13 = method3028(var12, var7);
         byte[] var14 = new byte[var6 + 1];
         System.arraycopy(var12, 0, var14, 0, var11);
         System.arraycopy(var13, 0, var14, var14.length - var3, var3);
         var8[var9] = var14;
      }

      byte[] var15 = new byte[var4];
      int var16 = 0;

      for (int var17 = 0; var17 < var8[0].length; var17++) {
         for (int var18 = 0; var18 < var8.length; var18++) {
            if (var17 != var6 - var3 || var18 >= var5) {
               var15[var16++] = var8[var18][var17];
            }
         }
      }

      return var15;
   }

   private static byte[] method3027(int var0) {
      byte[] var1 = new byte[var0];
      var1[var0 - 1] = 1;
      int var2 = 1;

      for (int var3 = 0; var3 < var0; var3++) {
         for (int var4 = 0; var4 < var1.length; var4++) {
            var1[var4] = (byte)method3029(var1[var4] & 255, var2);
            if (var4 + 1 < var1.length) {
               var1[var4] ^= var1[var4 + 1];
            }
         }

         var2 = method3029(var2, 2);
      }

      return var1;
   }

   private static byte[] method3028(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var1.length];

      for (int var6 : var0) {
         int var7 = (var6 ^ var2[0]) & 0xFF;
         System.arraycopy(var2, 1, var2, 0, var2.length - 1);
         var2[var2.length - 1] = 0;

         for (int var8 = 0; var8 < var2.length; var8++) {
            var2[var8] ^= (byte)method3029(var1[var8] & 255, var7);
         }
      }

      return var2;
   }

   private static int method3029(int var0, int var1) {
      int var2 = 0;

      for (int var3 = 7; var3 >= 0; var3--) {
         var2 = var2 << 1 ^ (var2 >>> 7) * 285;
         var2 ^= (var1 >>> var3 & 1) * var0;
      }

      return var2 & 0xFF;
   }

   private void method3030(byte[] var1) {
      int var2 = 0;

      for (int var3 = this.intValue2 - 1; var3 >= 1; var3 -= 2) {
         if (var3 == 6) {
            var3 = 5;
         }

         for (int var4 = 0; var4 < this.intValue2; var4++) {
            for (int var5 = 0; var5 < 2; var5++) {
               int var6 = var3 - var5;
               boolean var7 = (var3 + 1 & 2) == 0;
               int var8 = var7 ? this.intValue2 - 1 - var4 : var4;
               if (!this.flags2[var8][var6] && var2 < var1.length * 8) {
                  this.flags[var8][var6] = (var1[var2 >>> 3] >>> 7 - (var2 & 7) & 1) != 0;
                  var2++;
               }
            }
         }
      }
   }

   private void method3031(int var1) {
      for (int var2 = 0; var2 < this.intValue2; var2++) {
         for (int var3 = 0; var3 < this.intValue2; var3++) {
            if (!this.flags2[var2][var3]) {
               boolean var4 = switch (var1) {
                  case 0 -> (var3 + var2) % 2 == 0;
                  case 1 -> var2 % 2 == 0;
                  case 2 -> var3 % 3 == 0;
                  case 3 -> (var3 + var2) % 3 == 0;
                  case 4 -> (var3 / 3 + var2 / 2) % 2 == 0;
                  case 5 -> var3 * var2 % 2 + var3 * var2 % 3 == 0;
                  case 6 -> (var3 * var2 % 2 + var3 * var2 % 3) % 2 == 0;
                  default -> ((var3 + var2) % 2 + var3 * var2 % 3) % 2 == 0;
               };
               this.flags[var2][var3] = this.flags[var2][var3] ^ var4;
            }
         }
      }
   }

   private long method3032() {
      long var1 = 0L;

      for (int var3 = 0; var3 < 2; var3++) {
         for (int var4 = 0; var4 < this.intValue2; var4++) {
            int var5 = 1;

            for (int var6 = 1; var6 < this.intValue2; var6++) {
               boolean var7 = var3 == 0 ? this.flags[var4][var6] : this.flags[var6][var4];
               boolean var8 = var3 == 0 ? this.flags[var4][var6 - 1] : this.flags[var6 - 1][var4];
               if (var7 == var8) {
                  if (++var5 == 5) {
                     var1 += 3L;
                  } else if (var5 > 5) {
                     var1++;
                  }
               } else {
                  var5 = 1;
               }
            }
         }
      }

      int var9 = 0;

      for (int var10 = 0; var10 < this.intValue2; var10++) {
         for (int var12 = 0; var12 < this.intValue2; var12++) {
            if (this.flags[var10][var12]) {
               var9++;
            }

            if (var12 + 1 < this.intValue2 && var10 + 1 < this.intValue2) {
               boolean var14 = this.flags[var10][var12];
               if (var14 == this.flags[var10][var12 + 1] && var14 == this.flags[var10 + 1][var12] && var14 == this.flags[var10 + 1][var12 + 1]) {
                  var1 += 3L;
               }
            }
         }
      }

      int var11 = this.intValue2 * this.intValue2;
      int var13 = (Math.abs(var9 * 20 - var11 * 10) + var11 - 1) / var11 - 1;
      return var1 + Math.max(0, var13) * 10L;
   }

   private static final class Entry {
      public final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      public int current;
      public int length;

      public void method3033(int var1, int var2) {
         for (int var3 = var2 - 1; var3 >= 0; var3--) {
            this.current = this.current << 1 | var1 >>> var3 & 1;
            this.length++;
            if (this.length % 8 == 0) {
               this.byteArrayOutputStream.write(this.current);
               this.current = 0;
            }
         }
      }

      public byte[] method3034() {
         return this.byteArrayOutputStream.toByteArray();
      }
   }
}

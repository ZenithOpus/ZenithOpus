package org.zenith.utility.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class VaultCipher {
   private static final byte[] CONST_byteValues = new byte[]{90, 86, 76, 84};
   private static final byte CONST_byteValue = 1;
   private static final int CONST_intValue = 16;
   private static final SecureRandom secureRandom = new SecureRandom();

   private VaultCipher() {
   }

   public static byte[] method1852(byte[] var0, byte[] var1, String var2) {
      if (var1.length < 64) {
         throw new IllegalArgumentException("key must be >= 64 bytes");
      } else {
         try {
            byte[] var3 = new byte[16];
            secureRandom.nextBytes(var3);
            byte[] var4 = AesGcmCrypto.method5339((byte[])var0.clone(), method1854(var1, var2));
            byte[] var5 = method1859(var1, 8, 40);
            method1855(var4, var5, var3);
            long var6 = method1857(method1859(var1, 0, 8), var3);
            method1856(var4, var6, false);
            int var8 = 1 + Math.floorMod(var6, 48);
            byte[] var9 = new byte[var8];
            secureRandom.nextBytes(var9);
            ByteBuffer var10 = ByteBuffer.allocate(CONST_byteValues.length + 1 + 16 + 2 + 4 + var8 + var4.length).order(ByteOrder.BIG_ENDIAN);
            var10.put(CONST_byteValues);
            var10.put((byte)1);
            var10.put(var3);
            var10.putShort((short)var8);
            var10.putInt(var4.length);
            var10.put(var9);
            var10.put(var4);
            Arrays.fill(var5, (byte)0);
            return var10.array();
         } catch (RuntimeException var11) {
            throw var11;
         } catch (Exception var12) {
            throw new RuntimeException("vault seal failed", var12);
         }
      }
   }

   public static byte[] method1853(byte[] var0, byte[] var1, String var2) {
      if (var1.length < 64) {
         throw new IllegalArgumentException("key must be >= 64 bytes");
      } else {
         try {
            ByteBuffer var3 = ByteBuffer.wrap(var0).order(ByteOrder.BIG_ENDIAN);
            byte[] var4 = new byte[CONST_byteValues.length];
            var3.get(var4);
            if (!Arrays.equals(var4, CONST_byteValues)) {
               throw new IllegalArgumentException("bad magic");
            } else if (var3.get() != 1) {
               throw new IllegalArgumentException("bad version");
            } else {
               byte[] var5 = new byte[16];
               var3.get(var5);
               int var6 = Short.toUnsignedInt(var3.getShort());
               int var7 = var3.getInt();
               var3.position(var3.position() + var6);
               byte[] var8 = new byte[var7];
               var3.get(var8);
               long var9 = method1857(method1859(var1, 0, 8), var5);
               method1856(var8, var9, true);
               byte[] var11 = method1859(var1, 8, 40);
               method1855(var8, var11, var5);
               Arrays.fill(var11, (byte)0);
               return AesGcmCrypto.method5340(var8, method1854(var1, var2));
            }
         } catch (RuntimeException var12) {
            throw var12;
         } catch (Exception var13) {
            throw new RuntimeException("vault open failed (wrong key/HWID/session or tampered)", var13);
         }
      }
   }

   private static String method1854(byte[] var0, String var1) {
      byte[] var2 = method1861(method1860(method1859(var0, 40, 24), var1.getBytes(StandardCharsets.UTF_8)));
      return Base64.getEncoder().encodeToString(var2);
   }

   private static void method1855(byte[] var0, byte[] var1, byte[] var2) {
      try {
         MessageDigest var3 = MessageDigest.getInstance("SHA-256");
         int var4 = 0;

         for (int var5 = 0; var4 < var0.length; var5++) {
            var3.reset();
            var3.update(var1);
            var3.update(var2);
            var3.update((byte)var5);
            var3.update((byte)(var5 >> 8));
            var3.update((byte)(var5 >> 16));
            byte[] var6 = var3.digest();
            int var7 = Math.min(var6.length, var0.length - var4);

            for (int var8 = 0; var8 < var7; var8++) {
               var0[var4 + var8] = (byte)(var0[var4 + var8] ^ var6[var8]);
            }

            var4 += var7;
         }
      } catch (Exception var9) {
         throw new RuntimeException(var9);
      }
   }

   private static void method1856(byte[] var0, long var1, boolean var3) {
      int var4 = var0.length;
      if (var4 >= 2) {
         int[] var5 = new int[var4];
         long var6 = var1 == 0L ? -7046029254386353131L : var1;

         for (int var8 = var4 - 1; var8 > 0; var8--) {
            long var10 = var6 ^ var6 << 13;
            long var11 = var10 ^ var10 >>> 7;
            var6 = var11 ^ var11 << 17;
            int var9 = Math.floorMod(var6, var8 + 1);
            var5[var8] = var9;
         }

         if (!var3) {
            for (int var12 = var4 - 1; var12 > 0; var12--) {
               method1858(var0, var12, var5[var12]);
            }
         } else {
            for (int var13 = 1; var13 < var4; var13++) {
               method1858(var0, var13, var5[var13]);
            }
         }
      }
   }

   private static long method1857(byte[] var0, byte[] var1) {
      byte[] var2 = method1861(method1860(var0, var1));
      long var3 = 0L;

      for (int var5 = 0; var5 < 8; var5++) {
         var3 = var3 << 8 | var2[var5] & 255L;
      }

      return var3;
   }

   private static void method1858(byte[] var0, int var1, int var2) {
      byte var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   private static byte[] method1859(byte[] var0, int var1, int var2) {
      return Arrays.copyOfRange(var0, var1, var1 + var2);
   }

   private static byte[] method1860(byte[] var0, byte[] var1) {
      byte[] var2 = Arrays.copyOf(var0, var0.length + var1.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }

   private static byte[] method1861(byte[] var0) {
      try {
         return MessageDigest.getInstance("SHA-512").digest(var0);
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }
}

package org.zenith.utility.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesGcmCrypto {
   private static final int CONST_intValue = 256;
   private static final int CONST_intValue2 = 32;
   private static final int CONST_intValue3 = 128;
   private static final int CONST_intValue4 = 12;
   private static final SecureRandom secureRandom = new SecureRandom();
   private static final byte CONST_byteValue = 1;
   private static final int CONST_intValue5 = 64;
   private static final ConcurrentHashMap<String, byte[]> concurrentHashMap = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<String, byte[]> concurrentHashMap2 = new ConcurrentHashMap<>();
   private static final int CONST_intValue6 = 5000;
   private static final int CONST_intValue7 = 12;

   public static String method5331(String var0, String var1) throws Exception {
      if (var0 == null) {
         throw new IllegalArgumentException("plainText == null");
      } else if (var1 == null) {
         throw new IllegalArgumentException("password == null");
      } else {
         byte[] var2 = method5334(var1);
         int var3 = method5333(var1);
         byte[] var4 = method5336(var1, var2, var3);
         byte[] var5 = Arrays.copyOf(var4, 32);
         byte[] var6 = Arrays.copyOfRange(var4, 32, var4.length);
         SecretKeySpec var7 = new SecretKeySpec(var5, "AES");
         byte[] var8 = new byte[12];
         secureRandom.nextBytes(var8);
         Cipher var9 = Cipher.getInstance("AES/GCM/NoPadding");
         GCMParameterSpec var10 = new GCMParameterSpec(128, var8);
         var9.init(1, var7, var10);
         byte[] var11 = var9.doFinal(var0.getBytes(StandardCharsets.UTF_8));
         byte[] var12 = method5338(var6, var8, var11);
         ByteBuffer var13 = ByteBuffer.allocate(2 + var2.length + 1 + var8.length + 4 + var12.length);
         var13.order(ByteOrder.BIG_ENDIAN);
         var13.put((byte)1);
         var13.put((byte)var2.length);
         var13.put(var2);
         var13.put((byte)var8.length);
         var13.put(var8);
         var13.putInt(var3);
         var13.put(var12);
         byte[] var14 = var13.array();
         return Base64.getEncoder().encodeToString(var14);
      }
   }

   public static String method5332(String var0, String var1) throws Exception {
      if (var0 == null) {
         throw new IllegalArgumentException("base64Blob == null");
      } else if (var1 == null) {
         throw new IllegalArgumentException("password == null");
      } else {
         byte[] var2 = Base64.getDecoder().decode(var0);
         ByteBuffer var3 = ByteBuffer.wrap(var2).order(ByteOrder.BIG_ENDIAN);
         byte var4 = var3.get();
         if (var4 != 1) {
            throw new IllegalArgumentException("Unsupported version: " + var4);
         } else {
            int var5 = Byte.toUnsignedInt(var3.get());
            byte[] var6 = new byte[var5];
            var3.get(var6);
            int var7 = Byte.toUnsignedInt(var3.get());
            byte[] var8 = new byte[var7];
            var3.get(var8);
            int var9 = var3.getInt();
            byte[] var10 = new byte[var3.remaining()];
            var3.get(var10);
            byte[] var11 = method5336(var1, var6, var9);
            byte[] var12 = Arrays.copyOf(var11, 32);
            byte[] var13 = Arrays.copyOfRange(var11, 32, var11.length);
            SecretKeySpec var14 = new SecretKeySpec(var12, "AES");
            byte[] var15 = method5338(var13, var8, var10);
            Cipher var16 = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec var17 = new GCMParameterSpec(128, var8);
            var16.init(2, var14, var17);
            byte[] var18 = var16.doFinal(var15);
            method5335(var1, var6);
            return new String(var18, StandardCharsets.UTF_8);
         }
      }
   }

   private static int method5333(String var0) {
      int var1 = Math.max(1, var0.length());
      int var2 = 0;
      int var3 = 0;

      for (int var4 = 0; var4 < var0.length(); var4++) {
         char var5 = var0.charAt(var4);
         var2 += var5 * (var4 + 1);
         var3 ^= var5 << var4 % 8;
      }

      long var7 = var2 * 31L ^ var3 & 255L ^ var0.hashCode();
      int var6 = 20000 + (int)(Math.abs(var7) % 100000L);
      return Math.max(10000, Math.min(200000, var6));
   }

   private static byte[] method5334(String var0) {
      byte[] var1 = concurrentHashMap2.computeIfAbsent(var0, var0x -> {
         byte[] var1x = new byte[16];
         secureRandom.nextBytes(var1x);
         return var1x;
      });
      return (byte[])var1.clone();
   }

   private static void method5335(String var0, byte[] var1) {
      if (var1.length == 16) {
         concurrentHashMap2.putIfAbsent(var0, (byte[])var1.clone());
      }
   }

   private static byte[] method5336(String var0, byte[] var1, int var2) {
      String var3 = var0 + "\u0000" + Base64.getEncoder().encodeToString(var1) + "\u0000" + var2;
      byte[] var4 = concurrentHashMap.get(var3);
      if (var4 == null) {
         var4 = method5337(var0.toCharArray(), var1, var2, 64);
         if (concurrentHashMap.size() >= 64) {
            concurrentHashMap.clear();
         }

         concurrentHashMap.put(var3, var4);
      }

      return (byte[])var4.clone();
   }

   private static byte[] method5337(char[] var0, byte[] var1, int var2, int var3) {
      try {
         PBEKeySpec var4 = new PBEKeySpec(var0, var1, var2, var3 * 8);
         SecretKeyFactory var5 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
         SecretKey var6 = var5.generateSecret(var4);
         return var6.getEncoded();
      } catch (GeneralSecurityException var7) {
         throw new RuntimeException("PBKDF2 failure", var7);
      }
   }

   private static byte[] method5338(byte[] var0, byte[] var1, byte[] var2) {
      try {
         MessageDigest var3 = MessageDigest.getInstance("SHA-512");
         byte[] var4 = new byte[var2.length];
         int var5 = 0;

         for (int var6 = 0; var6 < var2.length; var5++) {
            var3.reset();
            var3.update(var0);
            var3.update((byte)(var5 & 0xFF));
            var3.update((byte)(var5 >> 8 & 0xFF));
            var3.update(var1);
            byte[] var7 = var3.digest();
            int var8 = Math.min(var7.length, var2.length - var6);

            for (int var9 = 0; var9 < var8; var9++) {
               var4[var6 + var9] = (byte)(var2[var6 + var9] ^ var7[var9]);
            }

            var6 += var8;
         }

         return var4;
      } catch (NoSuchAlgorithmException var10) {
         throw new RuntimeException(var10);
      }
   }

   public static byte[] method5339(byte[] var0, String var1) throws Exception {
      byte[] var2 = method5334(var1);
      int var3 = method5333(var1);
      byte[] var4 = method5336(var1, var2, var3);
      byte[] var5 = Arrays.copyOf(var4, 32);
      byte[] var6 = Arrays.copyOfRange(var4, 32, var4.length);
      SecretKeySpec var7 = new SecretKeySpec(var5, "AES");
      byte[] var8 = new byte[12];
      secureRandom.nextBytes(var8);
      Cipher var9 = Cipher.getInstance("AES/GCM/NoPadding");
      var9.init(1, var7, new GCMParameterSpec(128, var8));
      byte[] var10 = var9.doFinal(var0);
      byte[] var11 = method5338(var6, var8, var10);
      ByteBuffer var12 = ByteBuffer.allocate(2 + var2.length + 1 + var8.length + 4 + var11.length).order(ByteOrder.BIG_ENDIAN);
      var12.put((byte)1);
      var12.put((byte)var2.length);
      var12.put(var2);
      var12.put((byte)var8.length);
      var12.put(var8);
      var12.putInt(var3);
      var12.put(var11);
      byte[] var13 = var12.array();
      Arrays.fill(var4, (byte)0);
      Arrays.fill(var5, (byte)0);
      Arrays.fill(var6, (byte)0);
      Arrays.fill(var0, (byte)0);
      return Base64.getEncoder().encodeToString(var13).getBytes(StandardCharsets.UTF_8);
   }

   public static byte[] method5340(byte[] var0, String var1) throws Exception {
      String var2 = new String(var0, StandardCharsets.UTF_8);
      byte[] var3 = Base64.getDecoder().decode(var2);
      ByteBuffer var4 = ByteBuffer.wrap(var3).order(ByteOrder.BIG_ENDIAN);
      byte var5 = var4.get();
      if (var5 != 1) {
         throw new IllegalArgumentException("Unsupported version: " + var5);
      } else {
         int var6 = Byte.toUnsignedInt(var4.get());
         byte[] var7 = new byte[var6];
         var4.get(var7);
         int var8 = Byte.toUnsignedInt(var4.get());
         byte[] var9 = new byte[var8];
         var4.get(var9);
         int var10 = var4.getInt();
         byte[] var11 = new byte[var4.remaining()];
         var4.get(var11);
         byte[] var12 = method5336(var1, var7, var10);
         byte[] var13 = Arrays.copyOf(var12, 32);
         byte[] var14 = Arrays.copyOfRange(var12, 32, var12.length);
         SecretKeySpec var15 = new SecretKeySpec(var13, "AES");
         byte[] var16 = method5338(var14, var9, var11);
         Cipher var17 = Cipher.getInstance("AES/GCM/NoPadding");
         var17.init(2, var15, new GCMParameterSpec(128, var9));
         byte[] var18 = var17.doFinal(var16);
         method5335(var1, var7);
         Arrays.fill(var12, (byte)0);
         Arrays.fill(var13, (byte)0);
         Arrays.fill(var14, (byte)0);
         return var18;
      }
   }

   public static byte[] method5341(byte[] var0, String var1) throws Exception {
      byte[] var2 = new byte[12];
      secureRandom.nextBytes(var2);
      byte[] var3 = method5337(var1.toCharArray(), var2, 5000, 32);
      SecretKeySpec var4 = new SecretKeySpec(var3, "AES");
      byte[] var5 = new byte[12];
      secureRandom.nextBytes(var5);
      Cipher var6 = Cipher.getInstance("AES/GCM/NoPadding");
      var6.init(1, var4, new GCMParameterSpec(128, var5));
      byte[] var7 = var6.doFinal(var0);
      ByteBuffer var8 = ByteBuffer.allocate(var2.length + var5.length + var7.length);
      var8.put(var2);
      var8.put(var5);
      var8.put(var7);
      Arrays.fill(var3, (byte)0);
      return var8.array();
   }

   public static byte[] method5342(byte[] var0, String var1) throws Exception {
      ByteBuffer var2 = ByteBuffer.wrap(var0);
      byte[] var3 = new byte[12];
      var2.get(var3);
      byte[] var4 = new byte[12];
      var2.get(var4);
      byte[] var5 = new byte[var2.remaining()];
      var2.get(var5);
      byte[] var6 = method5337(var1.toCharArray(), var3, 5000, 32);
      SecretKeySpec var7 = new SecretKeySpec(var6, "AES");
      Cipher var8 = Cipher.getInstance("AES/GCM/NoPadding");
      var8.init(2, var7, new GCMParameterSpec(128, var4));
      byte[] var9 = var8.doFinal(var5);
      Arrays.fill(var6, (byte)0);
      return var9;
   }
}

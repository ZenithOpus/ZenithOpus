package org.zenith.utility.misc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class VaultResourceLoader {
   private VaultResourceLoader() {
   }

   public static InputStream method2218(String var0) {
      return method2219("models/" + var0, VaultKeyResolver.Type.ENVELOPE);
   }

   public static InputStream method2219(String var0, VaultKeyResolver.Type var1) {
      byte[] var2 = method2221(var0 + ".vault");
      if (var2 == null) {
         InputStream var9 = method2222().getResourceAsStream(var0);
         if (var9 != null) {
            return var9;
         } else {
            throw new IllegalStateException("vault resource not found: " + var0);
         }
      } else {
         byte[] var3 = VaultKeyResolver.method2860(var1, var0);

         ByteArrayInputStream var5;
         try {
            byte[] var4 = VaultCipher.method1853(var2, var3, var0);
            var5 = new ByteArrayInputStream(var4);
         } finally {
            Arrays.fill(var3, (byte)0);
         }

         return var5;
      }
   }

   public static byte[] method2220(String var0, VaultKeyResolver.Type var1) {
      byte[] var2 = method2221(var0 + ".vault");
      if (var2 == null) {
         throw new IllegalStateException("vault resource not found: " + var0 + ".vault");
      } else {
         byte[] var3 = VaultKeyResolver.method2860(var1, var0);

         byte[] var4;
         try {
            var4 = VaultCipher.method1853(var2, var3, var0);
         } finally {
            Arrays.fill(var3, (byte)0);
         }

         return var4;
      }
   }

   private static byte[] method2221(String var0) {
      try {
         byte[] var2;
         try (InputStream var1 = method2222().getResourceAsStream(var0)) {
            var2 = var1 == null ? null : var1.readAllBytes();
         }

         return var2;
      } catch (IOException var6) {
         throw new RuntimeException("read " + var0, var6);
      }
   }

   private static ClassLoader method2222() {
      ClassLoader var0 = VaultResourceLoader.class.getClassLoader();
      return var0 != null ? var0 : ClassLoader.getSystemClassLoader();
   }
}

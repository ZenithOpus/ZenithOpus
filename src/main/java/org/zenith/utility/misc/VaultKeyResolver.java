package org.zenith.utility.misc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public final class VaultKeyResolver {
   private static volatile VaultKeyResolver.Handler handler = var0 -> null;

   private VaultKeyResolver() {
   }

   public static void method2859(VaultKeyResolver.Handler var0) {
      if (var0 != null) {
         handler = var0;
      }
   }

   public static byte[] method2860(VaultKeyResolver.Type var0, String var1) {
      byte[] var2 = switch (var0) {
         case MACHINE -> method2862();
         case ENVELOPE -> {
            byte[] var3 = handler.resolve(var1);
            if (var3 == null || var3.length < 32) {
               throw new IllegalStateException("vault: no server DEK for " + var1 + " (login/server required)");
            }

            yield var3;
         }
         default -> throw new IllegalStateException();
      };
      byte[] var4 = method2864(method2863(var2, ("zenith/" + var0 + "/" + var1).getBytes(StandardCharsets.UTF_8)));
      Arrays.fill(var2, (byte)0);
      return var4;
   }

   public static byte[] method2861(VaultKeyResolver.Type var0, String var1, byte[] var2) {
      return method2864(method2863(var2, ("zenith/" + var0 + "/" + var1).getBytes(StandardCharsets.UTF_8)));
   }

   private static byte[] method2862() {
      return new byte[]{
         90, 126, 17, -61, 66, -99, -16, 104, -95, 59, -41, 12, -124, 85, 46, -69, 25, -26, 71, -112, 124, 42, -11, 97, 8, -51, 51, -78, 111, -104, 20, -34
      };
   }

   private static byte[] method2863(byte[] var0, byte[] var1) {
      byte[] var2 = Arrays.copyOf(var0, var0.length + var1.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }

   private static byte[] method2864(byte[] var0) {
      try {
         return MessageDigest.getInstance("SHA-512").digest(var0);
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   public interface Handler {
      byte[] resolve(String var1);
   }

   public static enum Type {
      MACHINE,
      ENVELOPE;
   }
}

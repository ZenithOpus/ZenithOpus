package org.zenith.utility.misc;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

public final class HwidProvider {
   private static volatile byte[] byteValues;

   private HwidProvider() {
   }

   public static byte[] method1369() {
      byte[] var0 = byteValues;
      if (var0 != null) {
         return (byte[])var0.clone();
      } else {
         synchronized (HwidProvider.class) {
            if (byteValues == null) {
               byteValues = method1370();
            }

            return (byte[])byteValues.clone();
         }
      }
   }

   private static byte[] method1370() {
      StringBuilder var0 = new StringBuilder(256);
      method1371(var0, "os", System.getProperty("os.name"));
      method1371(var0, "arch", System.getProperty("os.arch"));
      method1371(var0, "user", System.getProperty("user.name"));
      method1371(var0, "cpu", Integer.toString(Runtime.getRuntime().availableProcessors()));

      try {
         method1371(var0, "host", InetAddress.getLocalHost().getHostName());
      } catch (Exception var3) {
         method1371(var0, "host", "?");
      }

      method1371(var0, "mguid", method1372());

      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         return var1.digest(var0.toString().getBytes(StandardCharsets.UTF_8));
      } catch (Exception var2) {
         throw new RuntimeException("SHA-256 unavailable", var2);
      }
   }

   private static void method1371(StringBuilder var0, String var1, String var2) {
      var0.append(var1).append('=').append(var2 == null ? "" : var2.toLowerCase(Locale.ROOT)).append('\n');
   }

   private static String method1372() {
      String var0 = System.getProperty("os.name", "");
      if (!var0.toLowerCase(Locale.ROOT).contains("win")) {
         return "nonwin";
      } else {
         try {
            Process var1 = new ProcessBuilder("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid").redirectErrorStream(true).start();
            byte[] var2 = var1.getInputStream().readAllBytes();
            var1.waitFor();
            String var3 = new String(var2, StandardCharsets.UTF_8);
            int var4 = var3.indexOf("REG_SZ");
            if (var4 >= 0) {
               return var3.substring(var4 + 6).trim();
            }
         } catch (Exception var5) {
         }

         return "noguid";
      }
   }
}

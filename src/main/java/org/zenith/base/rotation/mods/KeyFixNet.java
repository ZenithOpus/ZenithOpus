package org.zenith.base.rotation.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public final class KeyFixNet {
   public static final String text = "neurotest-keyfix-v1";
   public static final double CONST_doubleValue = 0.001;
   public double[][][] doubleValues;
   public double[][] doubleValues2;
   public double doubleValue;
   public double doubleValue2;
   public int intValue;
   public String source = "?";
   public String text2 = "";

   public static double method3555(double var0) {
      double var2 = Math.signum(var0);
      double var4 = Math.abs(var0);
      double var6 = 1.0 / (1.0 + 0.3275911 * var4);
      double var8 = 1.0
         - ((((1.061405429 * var6 - 1.453152027) * var6 + 1.421413741) * var6 - 0.284496736) * var6 + 0.254829592) * var6 * Math.exp(-var4 * var4);
      return var2 * var8;
   }

   public static double method3556(double var0) {
      return 0.5 * var0 * (1.0 + method3555(var0 / Math.sqrt(2.0)));
   }

   public double[] method3557(double[] var1, int var2) {
      double[] var3 = new double[this.intValue + 8];
      System.arraycopy(var1, 0, var3, 0, this.intValue);

      for (int var4 = 0; var4 < 7; var4++) {
         var3[this.intValue + var4] = var2 >> var4 & 1;
      }

      var3[this.intValue + 7] = this.doubleValue;

      for (int var12 = 0; var12 < this.doubleValues.length; var12++) {
         double[][] var5 = this.doubleValues[var12];
         double[] var6 = new double[var5.length];

         for (int var7 = 0; var7 < var5.length; var7++) {
            double var8 = this.doubleValues2[var12][var7];
            double[] var10 = var5[var7];

            for (int var11 = 0; var11 < var10.length; var11++) {
               var8 += var10[var11] * var3[var11];
            }

            var6[var7] = var12 < this.doubleValues.length - 1 ? method3556(var8) : var8;
         }

         var3 = var6;
      }

      return var3;
   }

   public static int method3558(int var0, int var1) {
      int var2 = var0 & 1;
      int var3 = var0 >> 1 & 1;
      int var4 = var0 >> 2;
      int var5 = var4 % 3 - 1;
      int var6 = var4 / 3 - 1;
      return (var6 > 0 ? 1 : 0) | (var6 < 0 ? 2 : 0) | (var5 > 0 ? 4 : 0) | (var5 < 0 ? 8 : 0) | var3 << 4 | var1 & 32 | var2 << 6;
   }

   public boolean method3559(double[] var1) {
      return var1[21] > 0.5 && Math.exp(var1[29] * 2.0) < this.doubleValue2;
   }

   public int method3560(double[] var1, int var2, Random var3) {
      double[] var4 = this.method3557(var1, var2);
      double var5 = Double.NEGATIVE_INFINITY;

      for (double var10 : var4) {
         var5 = Math.max(var5, var10);
      }

      double var15 = 0.0;
      double[] var16 = new double[var4.length];

      for (int var17 = 0; var17 < var4.length; var17++) {
         var16[var17] = Math.exp(var4[var17] - var5);
         var15 += var16[var17];
      }

      double var18 = var3.nextDouble() * var15;
      double var12 = 0.0;

      for (int var14 = 0; var14 < var16.length; var14++) {
         var12 += var16[var14];
         if (var18 < var12) {
            return method3558(var14, var2);
         }
      }

      return method3558(var16.length - 1, var2);
   }

   public static double[] method3561(JsonArray var0) {
      double[] var1 = new double[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0.get(var2).getAsDouble();
      }

      return var1;
   }

   public static KeyFixNet method3562(InputStream var0) throws IOException {
      InputStreamReader var2 = new InputStreamReader(var0, StandardCharsets.UTF_8);

      JsonObject var1;
      try {
         var1 = JsonParser.parseReader(var2).getAsJsonObject();
      } catch (Throwable var15) {
         try {
            var2.close();
         } catch (Throwable var14) {
            var15.addSuppressed(var14);
         }

         throw var15;
      }

      var2.close();
      if (!"neurotest-keyfix-v1".equals(var1.get("format").getAsString())) {
         throw new IOException("формат " + var1.get("format").getAsString() + ", ждали neurotest-keyfix-v1");
      } else {
         KeyFixNet var16 = new KeyFixNet();
         var16.doubleValue = var1.get("q").getAsDouble();
         var16.doubleValue2 = var1.get("max_dist").getAsDouble();
         var16.intValue = var1.get("nf").getAsInt();
         var16.source = var1.get("source").getAsString();
         JsonArray var3 = var1.getAsJsonArray("layers");
         var16.doubleValues = new double[var3.size()][][];
         var16.doubleValues2 = new double[var3.size()][];

         for (int var4 = 0; var4 < var3.size(); var4++) {
            JsonObject var5 = var3.get(var4).getAsJsonObject();
            JsonArray var6 = var5.getAsJsonArray("w");
            var16.doubleValues[var4] = new double[var6.size()][];

            for (int var7 = 0; var7 < var6.size(); var7++) {
               var16.doubleValues[var4][var7] = method3561(var6.get(var7).getAsJsonArray());
            }

            var16.doubleValues2[var4] = method3561(var5.getAsJsonArray("b"));
         }

         JsonObject var17 = var1.getAsJsonObject("ref");
         JsonArray var18 = var17.getAsJsonArray("x");
         JsonArray var19 = var17.getAsJsonArray("u");
         JsonArray var20 = var17.getAsJsonArray("logits");
         double var8 = 0.0;

         for (int var10 = 0; var10 < var18.size(); var10++) {
            double[] var11 = var16.method3557(method3561(var18.get(var10).getAsJsonArray()), var19.get(var10).getAsInt());
            double[] var12 = method3561(var20.get(var10).getAsJsonArray());

            for (int var13 = 0; var13 < var11.length; var13++) {
               var8 = Math.max(var8, Math.abs(var11[var13] - var12[var13]));
            }
         }

         if (!(var8 < 0.001)) {
            throw new IOException("эталон поправщика не сошёлся: max |Δлогит| " + var8);
         } else {
            var16.text2 = String.format(
               "поправщик клавиш %s: эталон %d строк, max |Δлогит| %.1e, q %.2f, ближе %.1f блока",
               var16.source,
               var18.size(),
               var8,
               var16.doubleValue,
               var16.doubleValue2
            );
            return var16;
         }
      }
   }

   public static void main(String[] var0) throws IOException {
      try (InputStream var1 = Files.newInputStream(Path.of(var0[0]))) {
         System.out.println(method3562(var1).text2);
      }
   }
}

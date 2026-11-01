package org.zenith.base.rotation.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MotorHand {
   public static final String text = "neurotest-motor-hand-v1";
   public int w;
   public int intValue;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double[] doubleValues;
   public double[][][] doubleValues2;
   public double[][] doubleValues3;
   public String text2 = "";

   public MotorHand.Entry method3588() {
      return new MotorHand.Entry();
   }

   public static double method3589(double var0, double var2) {
      double var4 = var0 / var2;
      return Math.signum(var4) * Math.log(Math.abs(var4) + Math.sqrt(var4 * var4 + 1.0));
   }

   public static double method3590(double var0) {
      double var2 = (var0 + 180.0) % 360.0;
      if (var2 < 0.0) {
         var2 += 360.0;
      }

      return var2 - 180.0;
   }

   public int method3591(MotorHand.Entry var1, double[] var2, double var3) {
      double[] var5 = new double[]{
         method3589(var2[0], this.doubleValue4) * this.doubleValues[0],
         method3589(var2[1], this.doubleValue4) * this.doubleValues[1],
         method3589(var1.doubleValues2[0], this.doubleValue4) * this.doubleValues[2],
         method3589(var1.doubleValues2[1], this.doubleValue4) * this.doubleValues[3]
      };
      int var6 = this.intValue;
      int[] var7 = new int[var6];
      double[] var8 = new double[var6];
      int var9 = 0;

      for (int var10 = 0; var10 < this.doubleValues3.length; var10++) {
         double[] var11 = this.doubleValues3[var10];
         double var12 = 0.0;

         for (int var14 = 0; var14 < 4; var14++) {
            double var15 = var11[var14] - var5[var14];
            var12 += var15 * var15;
         }

         if (var9 < var6 || var12 < var8[var9 - 1]) {
            int var17;
            for (var17 = var9 < var6 ? var9 : var6 - 1; var17 > 0 && var8[var17 - 1] > var12; var17--) {
               if (var17 < var6) {
                  var8[var17] = var8[var17 - 1];
                  var7[var17] = var7[var17 - 1];
               }
            }

            var8[var17] = var12;
            var7[var17] = var10;
            if (var9 < var6) {
               var9++;
            }
         }
      }

      return var7[Math.min(var6 - 1, (int)(var3 * var6))];
   }

   public double[] method3592(MotorHand.Entry var1, double[] var2, double[] var3, double var4, double var6) {
      double var8 = method3590(var2[0] - var3[0]);
      double var10 = var2[1] - var3[1];
      double var12 = 0.0;
      double var14 = 0.0;
      if (var1.doubleValues4 != null) {
         var12 = method3590(var2[0] - var1.doubleValues4[0]);
         var14 = var2[1] - var1.doubleValues4[1];
      }

      var1.doubleValues4 = new double[]{var2[0], var2[1]};
      int var16 = -1;
      if (var1.doubleValues == null || var1.pos >= this.w) {
         double[] var17 = new double[]{this.doubleValue * var8 + var12 * this.w, this.doubleValue * var10 + var14 * this.w};
         var16 = this.method3591(var1, var17, var6);
         double[][] var18 = this.doubleValues2[var16];
         double[][] var19 = new double[this.w][2];

         for (int var20 = 0; var20 < 2; var20++) {
            double var21 = 0.0;

            for (int var23 = 0; var23 < this.w; var23++) {
               var21 += var18[var23][var20];
            }

            double var30 = 1.0;
            if (Math.abs(var21) > 1.0E-9 && Math.abs(var17[var20]) > 1.0E-9 && Math.signum(var21) == Math.signum(var17[var20])) {
               var30 = Math.min(Math.max(var17[var20] / var21, this.doubleValue2), this.doubleValue3);
            }

            for (int var25 = 0; var25 < this.w; var25++) {
               var19[var25][var20] = var18[var25][var20] * var30;
            }
         }

         var1.doubleValues = var19;
         var1.pos = 0;
      }

      double[] var26 = var1.doubleValues[var1.pos++];
      var1.doubleValues2[0] = var26[0];
      var1.doubleValues2[1] = var26[1];
      double[] var27 = new double[3];

      for (int var28 = 0; var28 < 2; var28++) {
         double var29 = var26[var28] + var1.doubleValues3[var28];
         var27[var28] = Math.rint(var29 / var4);
         var1.doubleValues3[var28] = var29 - var27[var28] * var4;
      }

      var27[2] = var16;
      return var27;
   }

   public static MotorHand method3593(InputStream var0) throws IOException {
      InputStreamReader var2 = new InputStreamReader(var0, StandardCharsets.UTF_8);

      JsonObject var1;
      try {
         var1 = JsonParser.parseReader(var2).getAsJsonObject();
      } catch (Throwable var17) {
         try {
            var2.close();
         } catch (Throwable var16) {
            var17.addSuppressed(var16);
         }

         throw var17;
      }

      var2.close();
      String var18 = var1.get("format").getAsString();
      if (!"neurotest-motor-hand-v1".equals(var18)) {
         throw new IOException("формат " + var18 + ", ожидался neurotest-motor-hand-v1");
      } else {
         MotorHand var3 = new MotorHand();
         JsonObject var4 = var1.getAsJsonObject("params");
         var3.w = var4.get("w").getAsInt();
         var3.intValue = var4.get("knn").getAsInt();
         var3.doubleValue = var4.get("alpha").getAsDouble();
         var3.doubleValue2 = var4.get("slo").getAsDouble();
         var3.doubleValue3 = var4.get("shi").getAsDouble();
         var3.doubleValue4 = var4.get("comp_c").getAsDouble();
         var3.doubleValues = method3594(var4.getAsJsonArray("kw"));
         JsonArray var5 = var1.getAsJsonArray("seqs");
         JsonArray var6 = var1.getAsJsonArray("prev");
         int var7 = var5.size();
         var3.doubleValues2 = new double[var7][][];
         var3.doubleValues3 = new double[var7][4];

         for (int var8 = 0; var8 < var7; var8++) {
            JsonArray var9 = var5.get(var8).getAsJsonArray();
            double[][] var10 = new double[var9.size()][];
            double var11 = 0.0;
            double var13 = 0.0;

            for (int var15 = 0; var15 < var10.length; var15++) {
               var10[var15] = method3594(var9.get(var15).getAsJsonArray());
               var11 += var10[var15][0];
               var13 += var10[var15][1];
            }

            if (var10.length != var3.w) {
               throw new IOException("отрезок " + var8 + " длиной " + var10.length + ", ждём " + var3.w);
            }

            var3.doubleValues2[var8] = var10;
            double[] var19 = method3594(var6.get(var8).getAsJsonArray());
            var3.doubleValues3[var8][0] = method3589(var11, var3.doubleValue4) * var3.doubleValues[0];
            var3.doubleValues3[var8][1] = method3589(var13, var3.doubleValue4) * var3.doubleValues[1];
            var3.doubleValues3[var8][2] = method3589(var19[0], var3.doubleValue4) * var3.doubleValues[2];
            var3.doubleValues3[var8][3] = method3589(var19[1], var3.doubleValue4) * var3.doubleValues[3];
         }

         var3.method3595(var1.getAsJsonObject("ref"));
         return var3;
      }
   }

   public static double[] method3594(JsonArray var0) {
      double[] var1 = new double[var0.size()];

      for (int var2 = 0; var2 < var0.size(); var2++) {
         var1[var2] = var0.get(var2).getAsDouble();
      }

      return var1;
   }

   public void method3595(JsonObject var1) throws IOException {
      double var2 = var1.get("g").getAsDouble();
      MotorHand.Entry var4 = this.method3588();
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      for (JsonElement var9 : var1.getAsJsonArray("ticks")) {
         JsonObject var10 = var9.getAsJsonObject();
         double[] var11 = this.method3592(
            var4, method3594(var10.getAsJsonArray("target")), method3594(var10.getAsJsonArray("cam")), var2, var10.get("u").getAsDouble()
         );
         double[] var12 = method3594(var10.getAsJsonArray("c"));
         int var13 = var10.get("idx").getAsInt();
         if (var11[0] != var12[0] || var11[1] != var12[1] || (int)var11[2] != var13) {
            var5++;
         }

         if (var13 >= 0) {
            var7++;
         }

         var6++;
      }

      this.text2 = String.format(
         "рука: отрезков %d по %d тиков; эталон совпал в %d из %d тиков (%d выборов)", this.doubleValues2.length, this.w, var6 - var5, var6, var7
      );
      if (var5 > 0) {
         throw new IOException("ЭТАЛОН РАЗОШЁЛСЯ — " + this.text2);
      }
   }

   public static MotorHand method3596(Path var0) throws IOException {
      MotorHand var2;
      try (InputStream var1 = Files.newInputStream(var0)) {
         var2 = method3593(var1);
      }

      return var2;
   }

   public static void main(String[] var0) throws Exception {
      MotorHand var1 = method3596(Path.of(var0[0]));
      System.out.println(var1.text2);
   }

   public final class Entry {
      public double[][] doubleValues;
      public int pos;
      public final double[] doubleValues2 = new double[2];
      public final double[] doubleValues3 = new double[2];
      public double[] doubleValues4;
   }
}

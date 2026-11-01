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
import java.util.Arrays;

public final class MotorNet {
   public static final String text = "neurotest-motor-v1";
   public static final double CONST_doubleValue = 1.0E-7;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public double doubleValue6;
   public double doubleValue7;
   public double[][] doubleValues;
   public double[][] doubleValues2;
   public double[][] doubleValues3;
   public double[][] doubleValues4;
   public double[][] doubleValues5;
   public double[] doubleValues6;
   public double[] doubleValues7;
   public double[] doubleValues8;
   public double[] doubleValues9;
   public double[] doubleValues10;
   public double[] doubleValues11;
   public double doubleValue8;
   public String text2 = "";

   public static double method3597(double var0) {
      return var0 > 20.0 ? var0 : Math.log1p(Math.exp(var0));
   }

   public static double method3598(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   public static double method3599(double var0) {
      return var0 > 0.0 ? var0 : Math.expm1(var0);
   }

   public static double[] method3600(double[] var0) {
      double var1 = Double.NEGATIVE_INFINITY;

      for (double var6 : var0) {
         var1 = Math.max(var1, var6);
      }

      double var10 = 0.0;

      for (double var8 : var0) {
         var10 += Math.exp(var8 - var1);
      }

      double var12 = var1 + Math.log(var10);
      double[] var14 = new double[var0.length];

      for (int var15 = 0; var15 < var0.length; var15++) {
         var14[var15] = var0[var15] - var12;
      }

      return var14;
   }

   public static int method3601(double[] var0, double var1) {
      double[] var3 = method3600(var0);
      double var4 = 0.0;

      int var6;
      for (var6 = 0; var6 < var3.length; var6++) {
         var4 += Math.exp(var3[var6]);
         if (var4 > var1) {
            break;
         }
      }

      return Math.min(var6, var3.length - 1);
   }

   public static double[] method3602(double[][] var0, double[] var1, double[] var2) {
      double[] var3 = new double[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         double var5 = var2[var4];
         double[] var7 = var0[var4];

         for (int var8 = 0; var8 < var7.length; var8++) {
            var5 += var7[var8] * var1[var8];
         }

         var3[var4] = var5;
      }

      return var3;
   }

   public static double[] method3603(double[] var0, double[] var1) {
      double[] var2 = new double[var0.length + var1.length];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }

   public double[] method3604(double[] var1, double[] var2) {
      double[] var3 = method3602(this.doubleValues3, var1, this.doubleValues8);
      double[] var4 = method3602(this.doubleValues4, var2, this.doubleValues9);
      double[] var5 = new double[this.intValue7];

      for (int var6 = 0; var6 < this.intValue7; var6++) {
         double var7 = method3598(var3[var6] + var4[var6]);
         double var9 = method3598(var3[this.intValue7 + var6] + var4[this.intValue7 + var6]);
         double var11 = Math.tanh(var3[2 * this.intValue7 + var6] + var7 * var4[2 * this.intValue7 + var6]);
         var5[var6] = (1.0 - var9) * var11 + var9 * var2[var6];
      }

      return var5;
   }

   public double method3605(double var1) {
      return Math.signum(var1) * Math.log1p(Math.abs(var1) / this.doubleValue5);
   }

   public double[] method3606(double[][] var1) {
      int var2 = var1.length;
      double[] var3 = new double[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = Math.abs(var1[var4][0]) + Math.abs(var1[var4][1]) == 0.0 ? 1.0 : 0.0;
      }

      double[] var18 = new double[this.intValue2];
      int var5 = 0;

      for (int var9 : new int[]{3, 10, this.intValue}) {
         double var10 = 0.0;
         double var12 = 0.0;
         double var14 = 0.0;

         for (int var16 = var2 - var9; var16 < var2; var16++) {
            var10 += Math.abs(var1[var16][0]);
            var12 += Math.abs(var1[var16][1]);
            var14 += var3[var16];
         }

         var18[var5++] = var10 / var9;
         var18[var5++] = var12 / var9;
         var18[var5++] = var14 / var9;
      }

      double var22 = 0.0;
      double var23 = 0.0;
      int var24 = 0;
      double var11 = 0.0;

      for (int var13 = 0; var13 < var2; var13++) {
         double var25 = 1.0 - var3[var13];
         var11 += var25;
         var22 = Math.max(var22, var25 * var13);
         double var26 = var25 * (var13 + 1.0);
         if (var26 > var23) {
            var23 = var26;
            var24 = var13;
         }
      }

      var18[var5++] = (var2 - 1.0 - var22) / this.intValue;
      var18[var5] = Math.abs(var1[var24][0]) * (var11 > 0.0 ? 1.0 : 0.0);
      return var18;
   }

   public static double method3607(double var0) {
      return Math.signum(var0) * Math.log1p(Math.abs(var0)) / 5.0;
   }

   public static double[] method3608(double var0, double var2, int var4, Double var5, double var6, double var8) {
      double var10 = Math.sqrt(var0 * var0 + var2 * var2 + 1.0E-9);
      return new double[]{
         method3607(var0),
         method3607(var2),
         Math.log1p(var10) / 5.0,
         var5 == null ? 0.0 : method3607(var5),
         var4 / 2.0,
         0.0,
         Math.signum(var0) * var6,
         Math.signum(var2) * var8
      };
   }

   public double method3609(double[] var1, double[] var2, double[] var3, double[] var4, double var5, double var7, double var9) {
      int var11 = method3601(var1, var5);
      double[] var12 = new double[this.intValue6];
      double[] var13 = new double[this.intValue6];
      double[] var14 = method3600(var2);

      for (int var15 = 0; var15 < this.intValue6; var15++) {
         var12[var15] = Math.exp(Math.max(-7.0, Math.min(7.0, var4[var15])));
         var13[var15] = var14[var15] - method3597((0.5 - var3[var15]) / var12[var15]);
      }

      int var24 = method3601(var13, var7);
      double var16 = method3598((0.5 - var3[var24]) / var12[var24]);
      double var18 = var16 + (1.0 - var16) * var9;
      var18 = Math.min(Math.max(var18, 1.0E-6), 0.999999);
      double var20 = var3[var24] + var12[var24] * (Math.log(var18) - Math.log1p(-var18));
      double var22 = Math.min(Math.max(Math.rint(var20), 1.0), this.doubleValue) + this.intValue5;
      return var11 < this.intValue5 ? var11 + 1.0 : var22;
   }

   public double method3610(double[] var1, double var2, double var4, boolean var6, double[] var7, int var8, double[] var9) {
      double[] var10 = method3602(this.doubleValues5, var1, this.doubleValues10);
      System.arraycopy(var10, 0, var9, 0, var10.length);
      double var11 = var10[0];
      double var13 = var10[1];
      double[] var15 = new double[this.intValue5 + 1];
      System.arraycopy(var10, 2, var15, 0, this.intValue5 + 1);
      int var16 = 2 + this.intValue5 + 1;
      double[] var17 = new double[this.intValue6];
      double[] var18 = new double[this.intValue6];
      double[] var19 = new double[this.intValue6];
      boolean var20 = var7[var8 + 1] < method3598(var13);
      double var21 = var20 ? -var4 : var4;
      boolean var23 = var21 == Math.signum(var2) && var2 != 0.0;
      double var24 = var23 ? Math.abs(var2) : 0.0;

      for (int var26 = 0; var26 < this.intValue6; var26++) {
         var17[var26] = var10[var16 + var26];
         double var27 = this.doubleValue3 * method3598(var10[var16 + 2 * this.intValue6 + var26]);
         double var29 = this.doubleValue2 * method3598(var10[var16 + 3 * this.intValue6 + var26]);
         var18[var26] = var27 * var24 + var29;
         var19[var26] = Math.log(method3597(var10[var16 + this.intValue6 + var26]) + this.doubleValue4 + this.doubleValue7 * var18[var26]);
      }

      boolean var31 = var7[var8] < method3598(var11);
      double var32 = this.method3609(var15, var17, var18, var19, var7[var8 + 2], var7[var8 + 3], var7[var8 + 4]);
      double var33 = var31 ? 0.0 : var21 * var32;
      return var6 ? 0.0 : var33;
   }

   public MotorNet.Entry method3611(double[][] var1, double[] var2, double[] var3, double[] var4) {
      double[][] var5 = new double[this.intValue][2];
      double[] var6 = new double[this.intValue * 2];

      for (int var7 = 0; var7 < this.intValue; var7++) {
         var5[var7][0] = this.method3605(var1[var7][0]);
         var5[var7][1] = this.method3605(var1[var7][1]);
         var6[2 * var7] = var5[var7][0];
         var6[2 * var7 + 1] = var5[var7][1];
      }

      double[] var23 = method3603(method3603(var6, this.method3606(var5)), var2);
      double[] var8 = method3602(this.doubleValues, var23, this.doubleValues6);

      for (int var9 = 0; var9 < var8.length; var9++) {
         var8[var9] = method3599(var8[var9]);
      }

      double[] var24 = method3602(this.doubleValues2, var8, this.doubleValues7);

      for (int var10 = 0; var10 < var24.length; var10++) {
         var24[var10] = Math.tanh(var24[var10]);
      }

      double var25 = var2[0] >= 0.0 ? 1.0 : -1.0;
      double var12 = var2[1] >= 0.0 ? 1.0 : -1.0;
      double var14 = var3[0];
      double var16 = var3[1];
      MotorNet.Entry var18 = new MotorNet.Entry();
      double[] var19 = this.method3604(method3603(method3608(var14, var16, 0, null, var25, var12), var24), var24);
      double var20 = this.doubleValue8;

      for (int var22 = 0; var22 < this.intValue7; var22++) {
         var20 += this.doubleValues11[var22] * var19[var22];
      }

      var18.doubleValue3 = var20;
      boolean var28 = var4[0] < method3598(var20);
      var19 = this.method3604(method3603(method3608(var14, var16, 1, null, var25, var12), var24), var19);
      var18.doubleValues = new double[this.intValue4];
      var18.doubleValue = this.method3610(var19, var14, var25, var28, var4, 1, var18.doubleValues);
      var19 = this.method3604(method3603(method3608(var14, var16, 2, var18.doubleValue, var25, var12), var24), var19);
      var18.doubleValues2 = new double[this.intValue4];
      var18.doubleValue2 = this.method3610(var19, var16, var12, var28, var4, 6, var18.doubleValues2);
      return var18;
   }

   public static double[] method3612(double var0, double var2, double[] var4, double[] var5, double[] var6, double var7, double var9, double var11) {
      double var13 = var5[3] - var5[0];
      double var15 = (var5[0] + var5[3]) / 2.0;
      double var17 = (var5[1] + var5[4]) / 2.0;
      double var19 = (var5[2] + var5[5]) / 2.0;
      double var21 = var15 - var4[0];
      double var23 = var17 - var4[1];
      double var25 = var19 - var4[2];
      double var27 = Math.sqrt(var21 * var21 + var25 * var25 + 1.0E-12);
      double var29 = Math.toDegrees(Math.atan2(-var21, var25));
      double var31 = Math.toDegrees(Math.atan2(-var23, var27));
      double var33 = Math.toDegrees(Math.atan2(var13 * 0.5, var27));
      double var35 = Math.toDegrees(Math.atan2(-(var5[4] - var4[1]), var27));
      double var37 = Math.toDegrees(Math.atan2(-(var5[1] - var4[1]), var27));
      double var39 = 0.5 * Math.abs(var37 - var35);
      double var41 = method3614(var29 - var0 + 180.0, 360.0) - 180.0;
      double var43 = var31 - var2;
      double var45 = Math.sqrt(var41 * var41 + var43 * var43 + 1.0E-12);
      return new double[]{
         method3615(var41 / Math.max(var33, 0.001), 12.0),
         method3615(var43 / Math.max(var39, 0.001), 12.0),
         Math.log1p(var45) / 5.0,
         var41 / var45,
         var43 / var45,
         Math.log1p(var33) / 3.0,
         Math.log1p(var39) / 3.0,
         var6[0],
         var6[1],
         var6[2],
         var6[3],
         var6[4],
         var7,
         var9,
         var11
      };
   }

   public double[] method3613(double[] var1, double var2, double[] var4) {
      if (this.intValue3 == var1.length) {
         return var1;
      } else if (this.intValue3 != var1.length + 3) {
         throw new IllegalStateException("раскладка геометрии: модель ждёт " + this.intValue3);
      } else {
         double var5 = Math.toRadians(var2);
         double var7 = -var4[0] * Math.sin(var5) + var4[2] * Math.cos(var5);
         double var9 = -var4[0] * Math.cos(var5) - var4[2] * Math.sin(var5);
         double[] var11 = Arrays.copyOf(var1, this.intValue3);
         var11[var1.length] = Math.tanh(var7 / 0.3);
         var11[var1.length + 1] = Math.tanh(var9 / 0.3);
         var11[var1.length + 2] = Math.tanh(var4[1] / 0.3);
         return var11;
      }
   }

   public static double method3614(double var0, double var2) {
      double var4 = var0 % var2;
      return var4 < 0.0 ? var4 + var2 : var4;
   }

   public static double method3615(double var0, double var2) {
      return Math.max(-var2, Math.min(var2, var0));
   }

   public static MotorNet method3616(InputStream var0) throws IOException {
      InputStreamReader var2 = new InputStreamReader(var0, StandardCharsets.UTF_8);

      JsonObject var1;
      try {
         var1 = JsonParser.parseReader(var2).getAsJsonObject();
      } catch (Throwable var7) {
         try {
            var2.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      var2.close();
      String var8 = var1.get("format").getAsString();
      if (!"neurotest-motor-v1".equals(var8)) {
         throw new IOException("формат " + var8 + ", ожидался neurotest-motor-v1");
      } else {
         MotorNet var3 = new MotorNet();
         JsonObject var4 = var1.getAsJsonObject("const");
         var3.intValue = var4.get("p_ctx").getAsInt();
         var3.intValue2 = var4.get("n_sum").getAsInt();
         var3.intValue3 = var4.get("n_geom").getAsInt();
         var3.intValue4 = var4.get("n_out").getAsInt();
         var3.intValue5 = var4.get("m_core").getAsInt();
         var3.intValue6 = var4.get("k_mix").getAsInt();
         var3.intValue7 = var4.get("hid").getAsInt();
         var3.intValue8 = var4.get("n_u").getAsInt();
         var3.doubleValue = var4.get("c_max").getAsDouble();
         var3.doubleValue2 = var4.get("b_max").getAsDouble();
         var3.doubleValue3 = var4.get("k_max").getAsDouble();
         var3.doubleValue4 = var4.get("s_min").getAsDouble();
         var3.doubleValue7 = var4.has("s_rel") ? var4.get("s_rel").getAsDouble() : 0.0;
         var3.doubleValue5 = var4.get("scale").getAsDouble();
         var3.doubleValue6 = var4.get("mdpc_train").getAsDouble();
         JsonObject var5 = var1.getAsJsonObject("weights");
         var3.doubleValues = method3619(var5, "cond.0.weight");
         var3.doubleValues6 = method3618(var5, "cond.0.bias");
         var3.doubleValues2 = method3619(var5, "cond.2.weight");
         var3.doubleValues7 = method3618(var5, "cond.2.bias");
         var3.doubleValues3 = method3619(var5, "cell.weight_ih");
         var3.doubleValues4 = method3619(var5, "cell.weight_hh");
         var3.doubleValues8 = method3618(var5, "cell.bias_ih");
         var3.doubleValues9 = method3618(var5, "cell.bias_hh");
         var3.doubleValues5 = method3619(var5, "head.weight");
         var3.doubleValues10 = method3618(var5, "head.bias");
         var3.doubleValues11 = method3618(var5, "rest.weight");
         var3.doubleValue8 = method3618(var5, "rest.bias")[0];
         method3617(var3.doubleValues[0].length == var3.intValue * 2 + var3.intValue2 + var3.intValue3, "вход cond");
         method3617(var3.doubleValues3.length == 3 * var3.intValue7 && var3.doubleValues3[0].length == 8 + var3.intValue7, "вход GRU");
         method3617(var3.doubleValues5.length == var3.intValue4, "голова");
         var3.method3621(var1);
         return var3;
      }
   }

   public static void method3617(boolean var0, String var1) throws IOException {
      if (!var0) {
         throw new IOException("не тот размер: " + var1);
      }
   }

   public static double[] method3618(JsonObject var0, String var1) throws IOException {
      JsonElement var2 = var0.get(var1);
      if (var2 == null) {
         throw new IOException("нет весов " + var1);
      } else {
         return method3620(var2.getAsJsonArray());
      }
   }

   public static double[][] method3619(JsonObject var0, String var1) throws IOException {
      JsonElement var2 = var0.get(var1);
      if (var2 == null) {
         throw new IOException("нет весов " + var1);
      } else {
         JsonArray var3 = var2.getAsJsonArray();
         double[][] var4 = new double[var3.size()][];

         for (int var5 = 0; var5 < var3.size(); var5++) {
            var4[var5] = method3620(var3.get(var5).getAsJsonArray());
         }

         return var4;
      }
   }

   public static double[] method3620(JsonArray var0) {
      double[] var1 = new double[var0.size()];

      for (int var2 = 0; var2 < var0.size(); var2++) {
         var1[var2] = var0.get(var2).getAsDouble();
      }

      return var1;
   }

   public void method3621(JsonObject var1) throws IOException {
      double var2 = 0.0;

      for (JsonElement var5 : var1.getAsJsonArray("ref_feats")) {
         JsonObject var6 = var5.getAsJsonObject();
         double[] var7 = method3620(var6.getAsJsonArray("cam"));
         double[] var8 = method3612(
            var7[0],
            var7[1],
            method3620(var6.getAsJsonArray("eye")),
            method3620(var6.getAsJsonArray("box")),
            method3620(var6.getAsJsonArray("keys")),
            var6.get("ground").getAsDouble(),
            var6.get("sprint").getAsDouble(),
            var6.get("cool").getAsDouble()
         );
         if (var6.has("vel")) {
            var8 = this.method3613(var8, var7[0], method3620(var6.getAsJsonArray("vel")));
         }

         double[] var9 = method3620(var6.getAsJsonArray("g"));
         method3617(var8.length == var9.length, "длина геометрии в эталоне");

         for (int var10 = 0; var10 < var9.length; var10++) {
            var2 = Math.max(var2, Math.abs(var8[var10] - var9[var10]));
         }
      }

      double var17 = 0.0;
      int var19 = 0;
      int var20 = 0;

      for (JsonElement var22 : var1.getAsJsonArray("ref_ticks")) {
         JsonObject var23 = var22.getAsJsonObject();
         JsonArray var11 = var23.getAsJsonArray("hist");
         double[][] var12 = new double[var11.size()][];

         for (int var13 = 0; var13 < var11.size(); var13++) {
            var12[var13] = method3620(var11.get(var13).getAsJsonArray());
         }

         MotorNet.Entry var24 = this.method3611(
            var12, method3620(var23.getAsJsonArray("g")), method3620(var23.getAsJsonArray("v")), method3620(var23.getAsJsonArray("u"))
         );
         var17 = Math.max(var17, Math.abs(var24.doubleValue3 - var23.get("rest").getAsDouble()));
         double[] var14 = method3620(var23.getAsJsonArray("oy"));
         double[] var15 = method3620(var23.getAsJsonArray("op"));

         for (int var16 = 0; var16 < var14.length; var16++) {
            var17 = Math.max(var17, Math.abs(var24.doubleValues[var16] - var14[var16]));
            var17 = Math.max(var17, Math.abs(var24.doubleValues2[var16] - var15[var16]));
         }

         if (var24.doubleValue != var23.get("dy").getAsDouble() || var24.doubleValue2 != var23.get("dp").getAsDouble()) {
            var19++;
         }

         var20++;
      }

      this.text2 = String.format("мотор: признаки сошлись до %.2e, тики до %.2e, счета совпали в %d из %d", var2, var17, var20 - var19, var20);
      if (var2 > 1.0E-7 || var17 > 1.0E-7 || var19 > 0) {
         throw new IOException("ЭТАЛОН РАЗОШЁЛСЯ — " + this.text2);
      }
   }

   public static MotorNet method3622(Path var0) throws IOException {
      MotorNet var2;
      try (InputStream var1 = Files.newInputStream(var0)) {
         var2 = method3616(var1);
      }

      return var2;
   }

   public static void main(String[] var0) throws Exception {
      MotorNet var1 = method3622(Path.of(var0[0]));
      System.out.println(var1.text2);
      System.out.println("ок: скрытых " + var1.intValue7 + ", сенса обучения " + var1.doubleValue6);
   }

   public static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double[] doubleValues;
      public double[] doubleValues2;
   }
}

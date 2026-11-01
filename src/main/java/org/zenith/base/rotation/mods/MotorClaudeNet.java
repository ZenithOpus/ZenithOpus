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

public final class MotorClaudeNet {
   public static final String text = "neurotest-motor-claude-v1";
   public static final double CONST_doubleValue = 1.0E-7;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public int intValue9;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public double doubleValue6;
   public double doubleValue7;
   public double doubleValue8;
   public double doubleValue9;
   public double doubleValue10;
   public double doubleValue11;
   public double doubleValue12;
   public double doubleValue13;
   public double doubleValue14;
   public double doubleValue15;
   public double doubleValue16;
   public double[][] doubleValues;
   public double[][] doubleValues2;
   public double[][] doubleValues3;
   public double[][] doubleValues4;
   public double[][] doubleValues5;
   public double[][] doubleValues6;
   public double[][] doubleValues7;
   public double[] doubleValues8;
   public double[] doubleValues9;
   public double[] doubleValues10;
   public double[] doubleValues11;
   public double[] doubleValues12;
   public double[] doubleValues13;
   public double[] doubleValues14;
   public String text2 = "";

   public MotorClaudeNet.Entry method3563() {
      return new MotorClaudeNet.Entry();
   }

   public static double method3564(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   public static double method3565(double var0) {
      return var0 >= 0.0 ? -Math.log1p(Math.exp(-var0)) : var0 - Math.log1p(Math.exp(var0));
   }

   public static double method3566(double var0) {
      return var0 > 0.0 ? var0 + Math.log1p(Math.exp(-var0)) : Math.log1p(Math.exp(var0));
   }

   public static double method3567(double var0) {
      return var0 > 0.0 ? var0 : Math.expm1(var0);
   }

   public static double[] method3568(double[] var0) {
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

   public static int method3569(double[] var0, double var1) {
      double[] var3 = method3568(var0);
      double var4 = 0.0;

      for (int var6 = 0; var6 < var3.length; var6++) {
         var4 += Math.exp(var3[var6]);
         if (var4 > var1) {
            return var6;
         }
      }

      return var3.length - 1;
   }

   public static double[] method3570(double[][] var0, double[] var1, double[] var2) {
      double[] var3 = new double[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         double var5 = 0.0;
         double[] var7 = var0[var4];

         for (int var8 = 0; var8 < var7.length; var8++) {
            var5 += var7[var8] * var2[var8];
         }

         var3[var4] = var5 + var1[var4];
      }

      return var3;
   }

   public static double[] method3571(double[] var0) {
      double[] var1 = new double[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = method3567(var0[var2]);
      }

      return var1;
   }

   public double method3572(double var1) {
      return Math.log1p(Math.abs(var1) / this.doubleValue);
   }

   public double method3573(double var1) {
      return Math.signum(var1) * this.method3572(var1) / 3.0;
   }

   public double[] method3574(MotorClaudeNet.Entry var1, double[] var2, double[] var3, double var4, double var6, double var8) {
      double[] var10 = new double[this.intValue2];
      int var11 = 0;
      double[][] var12 = var1.doubleValues;
      double[] var13 = var12[this.intValue4 - 1];
      double[] var14 = var12[this.intValue4 - 2];
      double[] var15 = var12[this.intValue4 - 3];

      for (int var16 = 0; var16 < 2; var16++) {
         var10[var11++] = this.method3573(var13[var16]);
      }

      for (int var40 = 0; var40 < 2; var40++) {
         var10[var11++] = this.method3573(var14[var40]);
      }

      for (int var41 = 0; var41 < 2; var41++) {
         var10[var11++] = this.method3573(var15[var41]);
      }

      for (int var42 = 0; var42 < 2; var42++) {
         var10[var11++] = this.method3573(var13[var42] - 2.0 * var14[var42] + var15[var42]);
      }

      for (int var43 = 0; var43 < 2; var43++) {
         var10[var11++] = Math.log1p(var1.doubleValues2[var43]) / 3.0;
      }

      for (int var44 = 0; var44 < 2; var44++) {
         var10[var11++] = Math.log1p(var1.doubleValues3[var44]) / 3.0;
      }

      for (int var45 = 0; var45 < 2; var45++) {
         double var17 = Double.NEGATIVE_INFINITY;

         for (int var19 = this.intValue4 - this.intValue5; var19 < this.intValue4; var19++) {
            var17 = Math.max(var17, this.method3572(var12[var19][var45]));
         }

         var10[var11++] = (this.method3572(var12[this.intValue4 - 1][var45]) - var17) / 3.0;
      }

      for (int var46 = 0; var46 < 2; var46++) {
         var10[var11++] = this.method3572(var1.doubleValues5[var46]) / 3.0;
      }

      for (int var54 : new int[]{3, 10, 30}) {
         for (int var20 = 0; var20 < 2; var20++) {
            double var21 = 0.0;

            for (int var23 = this.intValue4 - var54; var23 < this.intValue4; var23++) {
               var21 += this.method3572(var12[var23][var20]);
            }

            var10[var11++] = var21 / var54 / 3.0;
         }
      }

      for (int var55 : new int[]{3, 10, 30}) {
         double var56 = 0.0;

         for (int var22 = this.intValue4 - var55; var22 < this.intValue4; var22++) {
            var56 += var12[var22][0] == 0.0 && var12[var22][1] == 0.0 ? 1.0 : 0.0;
         }

         var10[var11++] = var56 / var55;
      }

      var10[var11++] = Math.min(var1.doubleValue, this.doubleValue2) / this.doubleValue2;
      var10[var11++] = Math.tanh(var1.doubleValue / this.doubleValue3);
      var10[var11++] = Math.min(var1.doubleValue2, this.doubleValue2) / this.doubleValue2;
      var10[var11++] = Math.tanh(var1.doubleValue2 / this.doubleValue3);
      double[] var49 = new double[]{Math.abs(var2[0]) < this.doubleValue4 ? 0.0 : var2[0], Math.abs(var2[1]) < this.doubleValue4 ? 0.0 : var2[1]};
      double var52 = Math.sqrt(var49[0] * var49[0] + var49[1] * var49[1]);
      var10[var11++] = this.method3573(var49[0]);
      var10[var11++] = this.method3573(var49[1]);
      var10[var11++] = var49[0] / (var52 + this.doubleValue5);
      var10[var11++] = var49[1] / (var52 + this.doubleValue5);
      var10[var11++] = asinh(var52) / 3.0;
      var10[var11++] = Math.signum(var13[0]) * Math.signum(var49[0]);
      var10[var11++] = Math.signum(var13[1]) * Math.signum(var49[1]);
      var10[var11++] = this.method3573(var3[0]);
      var10[var11++] = this.method3573(var3[1]);
      var10[var11++] = var4 - this.doubleValue6;
      var10[var11++] = var6 / 90.0;
      var10[var11++] = var8 - this.doubleValue7;
      if (var11 != this.intValue2) {
         throw new IllegalStateException("признаков " + var11 + ", ждём " + this.intValue2);
      } else {
         return var10;
      }
   }

   public static double asinh(double var0) {
      return Math.log(var0 + Math.sqrt(var0 * var0 + 1.0));
   }

   public void method3575(MotorClaudeNet.Entry var1, double[] var2) {
      boolean var3 = true;

      for (int var4 = 0; var4 < 2; var4++) {
         boolean var5 = var2[var4] == 0.0;
         double var6 = Math.signum(var2[var4]);
         boolean var8 = !var5 && var1.doubleValues4[var4] != 0.0 && var6 != var1.doubleValues4[var4];
         var1.doubleValues2[var4] = var5 ? var1.doubleValues2[var4] + 1.0 : 0.0;
         var1.doubleValues3[var4] = var8 ? 0.0 : var1.doubleValues3[var4] + 1.0;
         if (!var5) {
            var1.doubleValues4[var4] = var6;
            var1.doubleValues5[var4] = Math.abs(var2[var4]);
         }

         var3 &= var5;
      }

      var1.doubleValue = var3 ? 0.0 : var1.doubleValue + 1.0;
      var1.doubleValue2 = var3 ? var1.doubleValue2 + 1.0 : 0.0;

      for (int var9 = 0; var9 + 1 < this.intValue4; var9++) {
         var1.doubleValues[var9][0] = var1.doubleValues[var9 + 1][0];
         var1.doubleValues[var9][1] = var1.doubleValues[var9 + 1][1];
      }

      var1.doubleValues[this.intValue4 - 1][0] = var2[0];
      var1.doubleValues[this.intValue4 - 1][1] = var2[1];
   }

   public double[] method3576(double[] var1, double[] var2) {
      double[] var3 = method3570(this.doubleValues2, this.doubleValues9, var1);
      double[] var4 = method3570(this.doubleValues3, this.doubleValues10, var2);
      double[] var5 = new double[this.intValue];

      for (int var6 = 0; var6 < this.intValue; var6++) {
         double var7 = method3564(var3[var6] + var4[var6]);
         double var9 = method3564(var3[this.intValue + var6] + var4[this.intValue + var6]);
         double var11 = Math.tanh(var3[2 * this.intValue + var6] + var7 * var4[2 * this.intValue + var6]);
         var5[var6] = (1.0 - var9) * var11 + var9 * var2[var6];
      }

      return var5;
   }

   public double[] method3577(double[][] var1, double[] var2, double[][] var3, double[] var4, double[] var5) {
      double[] var6 = method3570(var3, var4, method3571(method3570(var1, var2, var5)));
      if (this.doubleValue15 != 1.0) {
         int var7 = 2 + this.intValue6 + 1;

         for (int var8 = 2; var8 < var7 + this.intValue7; var8++) {
            var6[var8] /= this.doubleValue15;
         }
      }

      return var6;
   }

   public double method3578(double[] var1, double var2, double var4, double[] var6, int var7) {
      boolean var8 = var6[var7] < method3564(var1[0]);
      boolean var9 = var6[var7 + 1] < method3564(var1[1]);
      double var10 = var9 ? -var4 : var4;
      double[] var12 = new double[this.intValue6 + 1];
      System.arraycopy(var1, 2, var12, 0, this.intValue6 + 1);
      int var13 = method3569(var12, var6[var7 + 2]);
      double var14;
      if (var13 < this.intValue6) {
         var14 = var13 + 1.0;
      } else {
         double var16 = var2 != 0.0 && Math.signum(var2) == var10 ? Math.abs(var2) : 0.0;
         int var18 = 2 + this.intValue6 + 1;
         double[] var19 = new double[this.intValue7];
         double[] var20 = new double[this.intValue7];
         double[] var21 = new double[this.intValue7];
         double[] var22 = new double[this.intValue7];

         for (int var23 = 0; var23 < this.intValue7; var23++) {
            var19[var23] = var1[var18 + var23];
         }

         double[] var33 = method3568(var19);
         double var24 = this.intValue6 + 0.5;

         for (int var26 = 0; var26 < this.intValue7; var26++) {
            double var27 = this.doubleValue8 * method3564(var1[var18 + 2 * this.intValue7 + var26]);
            double var29 = Math.exp(Math.max(this.doubleValue9, Math.min(this.doubleValue10, var1[var18 + 3 * this.intValue7 + var26])));
            var20[var26] = var27 * var16 + var29;
            var21[var26] = (method3566(var1[var18 + this.intValue7 + var26]) + this.doubleValue11 + this.doubleValue12 * var20[var26]) * this.doubleValue14;
            var22[var26] = var33[var26] + method3565(-(var24 - var20[var26]) / var21[var26]);
         }

         int var34 = method3569(var22, var6[var7 + 3]);
         double var35 = method3564((var24 - var20[var34]) / var21[var34]);
         double var36 = Math.min(Math.max(var35 + (1.0 - var35) * var6[var7 + 4], 1.0E-6), 0.999999);
         double var31 = var20[var34] + var21[var34] * (Math.log(var36) - Math.log1p(-var36));
         var14 = Math.min(Math.max(Math.rint(var31), this.intValue6 + 1.0), this.doubleValue13);
      }

      return var8 ? 0.0 : var10 * var14;
   }

   public MotorClaudeNet.Entry2 method3579(
      MotorClaudeNet.Entry var1, double[] var2, double[] var3, double var4, double var6, double var8, double[] var10, double[] var11
   ) {
      MotorClaudeNet.Entry2 var12 = new MotorClaudeNet.Entry2();
      var12.doubleValues = this.method3574(var1, var2, var3, var8, var4, Math.log(var6));
      var1.doubleValues7 = this.method3576(method3571(method3570(this.doubleValues, this.doubleValues8, var12.doubleValues)), var1.doubleValues7);
      double[] var13 = new double[]{var1.doubleValues4[0] == 0.0 ? 1.0 : var1.doubleValues4[0], var1.doubleValues4[1] == 0.0 ? 1.0 : var1.doubleValues4[1]};
      var12.doubleValues2 = this.method3577(this.doubleValues4, this.doubleValues11, this.doubleValues5, this.doubleValues12, var1.doubleValues7);
      var12.doubleValue = var11 != null ? var11[0] : this.method3578(var12.doubleValues2, var1.doubleValues6[0], var13[0], var10, 0);
      double[] var14 = new double[this.intValue + 2];
      System.arraycopy(var1.doubleValues7, 0, var14, 0, this.intValue);
      var14[this.intValue] = this.method3573(var12.doubleValue * var6);
      var14[this.intValue + 1] = var12.doubleValue == 0.0 ? 1.0 : 0.0;
      var12.doubleValues3 = this.method3577(this.doubleValues6, this.doubleValues13, this.doubleValues7, this.doubleValues14, var14);
      var12.doubleValue2 = var11 != null ? var11[1] : this.method3578(var12.doubleValues3, var1.doubleValues6[1], var13[1], var10, 5);
      this.method3575(var1, new double[]{var12.doubleValue * var6, var12.doubleValue2 * var6});
      var1.doubleValues6[0] = var12.doubleValue;
      var1.doubleValues6[1] = var12.doubleValue2;
      return var12;
   }

   public double getDoubleValue16() {
      return this.doubleValue16;
   }

   public int getIntValue8() {
      return this.intValue8;
   }

   public int getIntValue9() {
      return this.intValue9;
   }

   public static MotorClaudeNet method3580(InputStream var0) throws IOException {
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
      if (!"neurotest-motor-claude-v1".equals(var8)) {
         throw new IOException("формат " + var8 + ", ожидался neurotest-motor-claude-v1");
      } else {
         MotorClaudeNet var3 = new MotorClaudeNet();
         JsonObject var4 = var1.getAsJsonObject("const");
         var3.intValue = var4.get("hid").getAsInt();
         var3.intValue2 = var4.get("n_feat").getAsInt();
         var3.intValue3 = var4.get("n_out").getAsInt();
         var3.intValue4 = var4.get("hist").getAsInt();
         var3.intValue5 = var4.get("peak").getAsInt();
         var3.intValue6 = var4.get("m_core").getAsInt();
         var3.intValue7 = var4.get("k_mix").getAsInt();
         var3.intValue8 = var4.get("warm").getAsInt();
         var3.intValue9 = var4.get("n_u").getAsInt();
         var3.doubleValue = var4.get("sc").getAsDouble();
         var3.doubleValue2 = var4.get("fine").getAsDouble();
         var3.doubleValue3 = var4.get("coarse").getAsDouble();
         var3.doubleValue4 = var4.get("r_eps").getAsDouble();
         var3.doubleValue5 = var4.get("dir_eps").getAsDouble();
         var3.doubleValue6 = var4.get("log_k0").getAsDouble();
         var3.doubleValue7 = var4.get("log_g0").getAsDouble();
         var3.doubleValue8 = var4.get("k_max").getAsDouble();
         var3.doubleValue9 = var4.get("b_log_lo").getAsDouble();
         var3.doubleValue10 = var4.get("b_log_hi").getAsDouble();
         var3.doubleValue11 = var4.get("s_min").getAsDouble();
         var3.doubleValue12 = var4.get("s_rel").getAsDouble();
         var3.doubleValue13 = var4.get("c_max").getAsDouble();
         var3.doubleValue14 = var4.get("tau").getAsDouble();
         var3.doubleValue15 = var4.get("temp_d").getAsDouble();
         var3.doubleValue16 = var4.get("k_default").getAsDouble();
         JsonObject var5 = var1.getAsJsonObject("weights");
         var3.doubleValues = method3583(var5, "inp.0.weight");
         var3.doubleValues8 = method3582(var5, "inp.0.bias");
         var3.doubleValues2 = method3583(var5, "gru.weight_ih_l0");
         var3.doubleValues3 = method3583(var5, "gru.weight_hh_l0");
         var3.doubleValues9 = method3582(var5, "gru.bias_ih_l0");
         var3.doubleValues10 = method3582(var5, "gru.bias_hh_l0");
         var3.doubleValues4 = method3583(var5, "hy.0.weight");
         var3.doubleValues11 = method3582(var5, "hy.0.bias");
         var3.doubleValues5 = method3583(var5, "hy.2.weight");
         var3.doubleValues12 = method3582(var5, "hy.2.bias");
         var3.doubleValues6 = method3583(var5, "hp.0.weight");
         var3.doubleValues13 = method3582(var5, "hp.0.bias");
         var3.doubleValues7 = method3583(var5, "hp.2.weight");
         var3.doubleValues14 = method3582(var5, "hp.2.bias");
         method3581(var3.doubleValues[0].length == var3.intValue2 && var3.doubleValues.length == var3.intValue, "вход");
         method3581(var3.doubleValues2.length == 3 * var3.intValue && var3.doubleValues2[0].length == var3.intValue, "GRU вход");
         method3581(var3.doubleValues3.length == 3 * var3.intValue && var3.doubleValues3[0].length == var3.intValue, "GRU состояние");
         method3581(var3.doubleValues5.length == var3.intValue3 && var3.doubleValues7.length == var3.intValue3, "головы");
         method3581(var3.doubleValues6[0].length == var3.intValue + 2, "голова pitch");
         var3.method3585(var1.getAsJsonObject("ref"));
         return var3;
      }
   }

   public static void method3581(boolean var0, String var1) throws IOException {
      if (!var0) {
         throw new IOException("не тот размер: " + var1);
      }
   }

   public static double[] method3582(JsonObject var0, String var1) throws IOException {
      JsonElement var2 = var0.get(var1);
      if (var2 == null) {
         throw new IOException("нет весов " + var1);
      } else {
         return method3584(var2.getAsJsonArray());
      }
   }

   public static double[][] method3583(JsonObject var0, String var1) throws IOException {
      JsonElement var2 = var0.get(var1);
      if (var2 == null) {
         throw new IOException("нет весов " + var1);
      } else {
         JsonArray var3 = var2.getAsJsonArray();
         double[][] var4 = new double[var3.size()][];

         for (int var5 = 0; var5 < var3.size(); var5++) {
            var4[var5] = method3584(var3.get(var5).getAsJsonArray());
         }

         return var4;
      }
   }

   public static double[] method3584(JsonArray var0) {
      double[] var1 = new double[var0.size()];

      for (int var2 = 0; var2 < var0.size(); var2++) {
         var1[var2] = var0.get(var2).getAsDouble();
      }

      return var1;
   }

   public void method3585(JsonObject var1) throws IOException {
      double var2 = var1.get("g").getAsDouble();
      double var4 = var1.get("log_k").getAsDouble();
      MotorClaudeNet.Entry var6 = this.method3563();
      double var7 = 0.0;
      double var9 = 0.0;
      int var11 = 0;
      int var12 = 0;

      for (JsonElement var14 : var1.getAsJsonArray("ticks")) {
         JsonObject var15 = var14.getAsJsonObject();
         double[] var16 = var15.get("force").isJsonNull() ? null : method3584(var15.getAsJsonArray("force"));
         MotorClaudeNet.Entry2 var17 = this.method3579(
            var6,
            method3584(var15.getAsJsonArray("r")),
            method3584(var15.getAsJsonArray("bvel")),
            var15.get("pitch").getAsDouble(),
            var2,
            var4,
            method3584(var15.getAsJsonArray("u")),
            var16
         );
         var7 = Math.max(var7, method3586(var17.doubleValues, method3584(var15.getAsJsonArray("x"))));
         var9 = Math.max(var9, method3586(var17.doubleValues2, method3584(var15.getAsJsonArray("oy"))));
         var9 = Math.max(var9, method3586(var17.doubleValues3, method3584(var15.getAsJsonArray("op"))));
         double[] var18 = method3584(var15.getAsJsonArray("c"));
         if (var17.doubleValue != var18[0] || var17.doubleValue2 != var18[1]) {
            var11++;
         }

         var12++;
      }

      this.text2 = String.format("мотор Claude: признаки сошлись до %.2e, выходы голов до %.2e, счета совпали в %d из %d", var7, var9, var12 - var11, var12);
      if (var7 > 1.0E-7 || var9 > 1.0E-7 || var11 > 0) {
         throw new IOException("ЭТАЛОН РАЗОШЁЛСЯ — " + this.text2);
      }
   }

   public static double method3586(double[] var0, double[] var1) {
      if (var0.length != var1.length) {
         return Double.POSITIVE_INFINITY;
      } else {
         double var2 = 0.0;

         for (int var4 = 0; var4 < var0.length; var4++) {
            var2 = Math.max(var2, Math.abs(var0[var4] - var1[var4]));
         }

         return var2;
      }
   }

   public static MotorClaudeNet method3587(Path var0) throws IOException {
      MotorClaudeNet var2;
      try (InputStream var1 = Files.newInputStream(var0)) {
         var2 = method3580(var1);
      }

      return var2;
   }

   public static void main(String[] var0) throws Exception {
      MotorClaudeNet var1 = method3587(Path.of(var0[0]));
      System.out.println(var1.text2);
      System.out.println("ок: скрытых " + var1.intValue + ", признаков " + var1.intValue2 + ", K по умолчанию " + var1.doubleValue16);
   }

   public final class Entry {
      public final double[][] doubleValues = new double[MotorClaudeNet.this.intValue4][2];
      public final double[] doubleValues2 = new double[2];
      public final double[] doubleValues3 = new double[2];
      public final double[] doubleValues4 = new double[2];
      public final double[] doubleValues5 = new double[2];
      public final double[] doubleValues6 = new double[2];
      public double doubleValue;
      public double doubleValue2;
      public double[] doubleValues7 = new double[MotorClaudeNet.this.intValue];
   }

   public static final class Entry2 {
      public double doubleValue;
      public double doubleValue2;
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
   }
}

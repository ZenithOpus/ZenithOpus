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

public final class SilentAssistNet {
   public static final String text = "neurotest-assist-silent-v1";
   public static final double CONST_doubleValue = 0.001;
   public static final double CONST_doubleValue2 = 0.002;
   public static final double CONST_doubleValue3 = 0.002;
   public static final double CONST_doubleValue4 = 1.62;
   public static final double CONST_doubleValue5 = 1.27;
   public static final double CONST_doubleValue6 = 0.6;
   public static final double CONST_doubleValue7 = 1.8;
   public static final double CONST_doubleValue8 = 1.5;
   public int intValue;
   public int intValue2;
   public int layers;
   public int intValue3;
   public int intValue4;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public boolean flag;
   public boolean flag2;
   public int[] intValues;
   public BcCameraNet bcCameraNet;
   public double[][] doubleValues;
   public double[][] doubleValues2;
   public double[] doubleValues3;
   public double[] doubleValues4;
   public double[][][] doubleValues5;
   public double[][][] doubleValues6;
   public double[][] doubleValues7;
   public double[][] doubleValues8;
   public String text2 = "";
   public String source = "?";

   public SilentAssistNet.Entry method3623() {
      return new SilentAssistNet.Entry();
   }

   public static double method3624(double var0, double var2) {
      return Double.isNaN(var0) ? var2 : (Double.isNaN(var2) ? var0 : Math.max(var0, var2));
   }

   public static double method3625(double var0, double var2) {
      return Double.isNaN(var0) ? var2 : (Double.isNaN(var2) ? var0 : Math.min(var0, var2));
   }

   public static double[] method3626(BcCameraNet.Entry4 var0, double var1, double var3) {
      boolean var5 = var0.intValue2 >= 0
         && BcCameraNet.method3361(var0.doubleValue4, var0.doubleValue5, var0.doubleValue6)
         && BcCameraNet.method3361(var0.doubleValue, var0.doubleValue2, var0.doubleValue3);
      if (!var5) {
         return new double[4];
      } else {
         boolean var6 = (var0.intValue >> 5 & 1) != 0;
         double var7 = var0.doubleValue2 + (var6 ? 1.27 : 1.62);
         double var9 = var0.flag4 ? 1.5 : 1.8;
         double var11 = var0.doubleValue4 - var0.doubleValue;
         double var13 = var0.doubleValue6 - var0.doubleValue3;
         double var15 = Math.max(Math.hypot(var11, var13), 0.001);
         double var17 = Math.toDegrees(Math.atan2(-var11, var13));
         double var19 = Double.POSITIVE_INFINITY;
         double var21 = Double.NEGATIVE_INFINITY;

         for (byte var23 = -1; var23 <= 1; var23 += 2) {
            for (byte var24 = -1; var24 <= 1; var24 += 2) {
               double var25 = BcCameraNet.method3357(Math.toDegrees(Math.atan2(-(var11 + var23 * 0.6 / 2.0), var13 + var24 * 0.6 / 2.0)) - var17);
               var19 = Math.min(var19, var25);
               var21 = Math.max(var21, var25);
            }
         }

         boolean var69 = Math.abs(var11) < 0.3 && Math.abs(var13) < 0.3;
         double var70 = var19 + var17;
         double var26 = var21 + var17;
         double var28 = var11 / var15;
         double var30 = var13 / var15;
         double var32 = 0.3 / Math.max(Math.max(Math.abs(var28), Math.abs(var30)), 1.0E-6);
         double var34 = var15 - var32;
         double var36 = var15 + var32;
         double var38 = Math.toRadians(var1);
         double var40 = -Math.sin(var38);
         double var42 = Math.cos(var38);
         double var44 = (var11 - 0.3) / var40;
         double var46 = (var11 + 0.3) / var40;
         double var48 = (var13 - 0.3) / var42;
         double var50 = (var13 + 0.3) / var42;
         double var52 = method3624(Math.min(var44, var46), Math.min(var48, var50));
         double var54 = method3625(Math.max(var44, var46), Math.max(var48, var50));
         boolean var56 = Double.isFinite(var52) && Double.isFinite(var54) && var54 >= Math.max(var52, 0.0);
         double var57 = Math.max(var56 ? var52 : var34, 0.001);
         double var59 = Math.max(var56 ? var54 : var36, 0.001);
         double var61 = var7 - (var0.doubleValue5 + var9);
         double var63 = var7 - var0.doubleValue5;
         double var65 = Math.toDegrees(Math.atan(var61 / (var61 < 0.0 ? var57 : var59)));
         double var67 = Math.toDegrees(Math.atan(var63 / (var63 > 0.0 ? var57 : var59)));
         return var57 / Math.max(Math.cos(Math.toRadians(Math.abs(var3))), 0.001) >= 6.0
            ? new double[]{1000.0, -1000.0, 1000.0, -1000.0}
            : new double[]{
               var69 ? -1000.0 : BcCameraNet.method3357(var70 - var1), var69 ? 1000.0 : BcCameraNet.method3357(var26 - var1), var65 - var3, var67 - var3
            };
      }
   }

   public static double method3627(double[] var0, double var1, double var3) {
      return Math.min(Math.min(var1 - var0[0], var0[1] - var1), Math.min(var3 - var0[2], var0[3] - var3));
   }

   public static BcCameraNet.Entry4 method3628(BcCameraNet.Entry4 var0) {
      BcCameraNet.Entry4 var1 = new BcCameraNet.Entry4();
      var1.doubleValue = var0.doubleValue;
      var1.doubleValue2 = var0.doubleValue2;
      var1.doubleValue3 = var0.doubleValue3;
      var1.intValue = var0.intValue;
      var1.flag = var0.flag;
      var1.flag2 = var0.flag2;
      var1.flag3 = var0.flag3;
      var1.doubleValue4 = var0.doubleValue4;
      var1.doubleValue5 = var0.doubleValue5;
      var1.doubleValue6 = var0.doubleValue6;
      var1.intValue2 = var0.intValue2;
      var1.intValue3 = var0.intValue3;
      var1.flag4 = var0.flag4;
      var1.doubleValue7 = var0.doubleValue7;
      var1.doubleValue8 = var0.doubleValue8;
      var1.doubleValue9 = var0.doubleValue9;
      var1.doubleValue10 = var0.doubleValue10;
      var1.flag5 = var0.flag5;
      return var1;
   }

   public void method3629(SilentAssistNet.Entry var1, BcCameraNet.Entry4 var2) {
      var1.entry2.method3394(var2);
      BcCameraNet.Entry4 var3 = method3628(var2);
      if (var1.flag) {
         var3.doubleValue7 = var1.doubleValue;
         var3.doubleValue8 = var1.doubleValue2;
         var3.doubleValue9 = var1.doubleValue7;
         var3.doubleValue10 = var1.doubleValue8;
         var3.flag5 = var1.flag2;
      }

      var1.entry22.method3394(var3);
   }

   public double[] method3630(
      SilentAssistNet.Entry var1, BcCameraNet.Entry4 var2, double var3, double var5, boolean var7, double var8, double var10, double var12, double[] var14
   ) {
      double[] var15 = this.bcCameraNet.method3365(var1.entry2, var8);
      double[] var16 = this.bcCameraNet.method3365(var1.entry22, var8);
      double[] var17 = method3626(var2, var2.doubleValue7, var2.doubleValue8);
      double var18 = method3627(var17, 0.0, 0.0);
      double var20 = method3627(var17, var1.doubleValue3, var1.doubleValue4);
      double var22 = var15[21];
      double[] var24 = new double[this.intValue2];
      System.arraycopy(var15, 0, var24, 0, this.intValue3);
      int var25 = this.intValue3;

      for (int var26 = 0; var26 < this.intValues.length; var26++) {
         var24[var25++] = var16[this.intValues[var26]];
      }

      var24[var25 - 1] = var22 * BcCameraNet.method3371(var20 / this.doubleValue4);
      var24[var25++] = BcCameraNet.method3358(var3 / 10.0, -6.0, 6.0);
      var24[var25++] = BcCameraNet.method3358(var5 / 10.0, -6.0, 6.0);
      var24[var25++] = BcCameraNet.asinh(var3 / 0.5) / 4.0;
      var24[var25++] = BcCameraNet.asinh(var5 / 0.5) / 4.0;
      var24[var25++] = var7 ? 1.0 : 0.0;
      var24[var25++] = BcCameraNet.asinh(var1.doubleValue3) / 2.0;
      var24[var25++] = BcCameraNet.asinh(var1.doubleValue4) / 2.0;
      var24[var25++] = var1.doubleValue3 / 8.0;
      var24[var25++] = var1.doubleValue4 / 8.0;
      var24[var25++] = var22 * BcCameraNet.asinh(var18) / 3.0;
      var24[var25++] = var22 * BcCameraNet.asinh(var20) / 3.0;
      if (var14 != null) {
         System.arraycopy(var24, 0, var14, 0, this.intValue2);
      }

      double[] var66 = this.method3633(var1, var24);
      double var27;
      double var29;
      if (this.flag) {
         var27 = Math.expm1(this.doubleValue * Math.tanh(var66[0]));
         var29 = Math.expm1(this.doubleValue * Math.tanh(var66[1]));
      } else {
         var27 = this.doubleValue * Math.tanh(var66[0]);
         var29 = this.doubleValue * Math.tanh(var66[1]);
      }

      double var31 = var27 * var3 + this.doubleValue2 * Math.tanh(var66[2]);
      double var33 = var29 * var5 + this.doubleValue2 * Math.tanh(var66[3]);
      double var35 = var31;
      double var37 = var33;
      var1.doubleValue9 = var27;
      var1.doubleValue12 = var20;
      var1.intValue++;
      if (var1.intValue <= this.intValue4) {
         var1.doubleValue10 = 0.0;
         var1.doubleValue11 = 0.0;
         return new double[]{var10, var12, var31, var33};
      } else {
         if (!var1.flag) {
            var1.flag = true;
            var1.doubleValue = var2.doubleValue7;
            var1.doubleValue2 = var2.doubleValue8;
            var1.doubleValue3 = 0.0;
            var1.doubleValue4 = 0.0;
            var1.doubleValue5 = 0.0;
            var1.doubleValue6 = 0.0;
         }

         if (!var7) {
            var31 = 0.0;
            var33 = 0.0;
         }

         var31 = BcCameraNet.method3358(var1.doubleValue3 + var31, -this.doubleValue3, this.doubleValue3) - var1.doubleValue3;
         var33 = BcCameraNet.method3358(var1.doubleValue4 + var33, -this.doubleValue3, this.doubleValue3) - var1.doubleValue4;
         double var39 = var1.doubleValue5 + var3 + var31;
         double var41 = var1.doubleValue6 + var5 + var33;
         var1.doubleValue13 = var39;
         var1.doubleValue14 = var41;
         double var43 = Math.rint(var39 / var8) * var8;
         double var45 = Math.rint(var41 / var8) * var8;
         if (this.flag2) {
            var43 = method3632(var43, var3, var8);
            var45 = method3632(var45, var5, var8);
         }

         double var47;
         double var49;
         if (var7) {
            var1.doubleValue5 = var39 - var43;
            var1.doubleValue6 = var41 - var45;
            var47 = var43;
            var49 = var45;
         } else {
            var47 = BcCameraNet.method3357(var10 - var2.doubleValue7);
            var49 = var12 - var2.doubleValue8;
         }

         double var51 = var1.doubleValue + var47;
         double var53 = BcCameraNet.method3358(var1.doubleValue2 + var49, -90.0, 90.0);
         var1.doubleValue7 = var51 - var1.doubleValue;
         var1.doubleValue8 = var53 - var1.doubleValue2;
         var1.flag2 = var7;
         var1.doubleValue = var51;
         var1.doubleValue2 = var53;
         var1.doubleValue3 = BcCameraNet.method3357(var51 - var10);
         var1.doubleValue4 = var53 - var12;
         var1.doubleValue10 = var31;
         var1.doubleValue11 = var33;
         return new double[]{var51, var53, var35, var37};
      }
   }

   public static boolean method3631(double var0, double var2, double var4) {
      if (Math.abs(var0) <= 1.0E-6) {
         return true;
      } else {
         double var6 = var2 / var4 - Math.floor(var2 / var4);
         return Math.abs(Math.abs(var0) - var4) <= 1.0E-6 && Math.abs(var6 - 0.5) < 0.001;
      }
   }

   public static double method3632(double var0, double var2, double var4) {
      if (var2 == 0.0) {
         return 0.0;
      } else {
         return var0 != 0.0 && Math.signum(var0) == Math.signum(var2) ? var0 : Math.signum(var2) * var4;
      }
   }

   public double[] method3633(SilentAssistNet.Entry var1, double[] var2) {
      double[] var3 = BcCameraNet.method3372(this.doubleValues, this.doubleValues3, var2);

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = BcCameraNet.method3369(var3[var4]);
      }

      for (int var16 = 0; var16 < this.layers; var16++) {
         double[] var5 = var1.doubleValues[var16];
         double[] var6 = BcCameraNet.method3372(this.doubleValues5[var16], this.doubleValues7[var16], var3);
         double[] var7 = BcCameraNet.method3372(this.doubleValues6[var16], this.doubleValues8[var16], var5);
         double[] var8 = new double[this.intValue];

         for (int var9 = 0; var9 < this.intValue; var9++) {
            double var10 = BcCameraNet.method3371(var6[var9] + var7[var9]);
            double var12 = BcCameraNet.method3371(var6[this.intValue + var9] + var7[this.intValue + var9]);
            double var14 = Math.tanh(var6[2 * this.intValue + var9] + var10 * var7[2 * this.intValue + var9]);
            var8[var9] = (1.0 - var12) * var14 + var12 * var5[var9];
         }

         var1.doubleValues[var16] = var8;
         var3 = var8;
      }

      return BcCameraNet.method3372(this.doubleValues2, this.doubleValues4, var3);
   }

   public static SilentAssistNet method3634(InputStream var0) throws IOException {
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
      if (!"neurotest-assist-silent-v1".equals(var1.get("format").getAsString())) {
         throw new IOException("формат " + var1.get("format").getAsString() + ", ожидался neurotest-assist-silent-v1");
      } else {
         SilentAssistNet var8 = new SilentAssistNet();
         var8.intValue = var1.get("h").getAsInt();
         var8.intValue2 = var1.get("nin").getAsInt();
         var8.layers = var1.get("layers").getAsInt();
         var8.intValue3 = var1.get("nf").getAsInt();
         var8.intValue4 = var1.get("warm").getAsInt();
         var8.doubleValue = var1.get("alpha").getAsDouble();
         var8.doubleValue2 = var1.get("beta").getAsDouble();
         var8.doubleValue3 = var1.get("omax").getAsDouble();
         var8.doubleValue4 = var1.get("tau_in").getAsDouble();
         var8.flag = "log".equals(var1.get("mode").getAsString());
         var8.flag2 = var1.get("keep").getAsBoolean();
         double[] var3 = BcCameraNet.method3388(var1.getAsJsonArray("sent_idx"));
         var8.intValues = new int[var3.length];

         for (int var4 = 0; var4 < var3.length; var4++) {
            var8.intValues[var4] = (int)var3[var4];
         }

         if (var8.intValue2 != var8.intValue3 + var8.intValues.length + 11) {
            throw new IOException("вход сети " + var8.intValue2 + " не совпадает с раскладкой");
         } else {
            var8.bcCameraNet = BcCameraNet.method3356(var8.intValue3, var1.get("clip").getAsInt(), var1.get("window").getAsInt());
            JsonObject var9 = var1.getAsJsonObject("weights");
            var8.doubleValues = BcCameraNet.method3386(var9, "inp.weight");
            var8.doubleValues3 = BcCameraNet.method3385(var9, "inp.bias");
            var8.doubleValues5 = new double[var8.layers][][];
            var8.doubleValues6 = new double[var8.layers][][];
            var8.doubleValues7 = new double[var8.layers][];
            var8.doubleValues8 = new double[var8.layers][];

            for (int var5 = 0; var5 < var8.layers; var5++) {
               var8.doubleValues5[var5] = BcCameraNet.method3386(var9, "rnn.weight_ih_l" + var5);
               var8.doubleValues6[var5] = BcCameraNet.method3386(var9, "rnn.weight_hh_l" + var5);
               var8.doubleValues7[var5] = BcCameraNet.method3385(var9, "rnn.bias_ih_l" + var5);
               var8.doubleValues8[var5] = BcCameraNet.method3385(var9, "rnn.bias_hh_l" + var5);
            }

            var8.doubleValues2 = BcCameraNet.method3386(var9, "head.weight");
            var8.doubleValues4 = BcCameraNet.method3385(var9, "head.bias");
            var8.source = var1.get("source").getAsString().replace(',', ';');
            var8.text2 = var8.method3635(var1.getAsJsonArray("ref")) + "; модель " + var1.get("source").getAsString();
            return var8;
         }
      }
   }

   public String method3635(JsonArray var1) throws IOException {
      double var2 = 0.0;
      double var4 = 0.0;
      double var6 = 0.0;
      double var8 = 0.0;
      int var10 = 0;
      int var11 = -1;
      int var12 = 0;
      String var13 = "";

      for (JsonElement var15 : var1) {
         var11++;
         JsonObject var16 = var15.getAsJsonObject();
         double var17 = var16.get("grid").getAsDouble();
         double[] var19 = BcCameraNet.method3388(var16.getAsJsonArray("yaw"));
         double[] var20 = BcCameraNet.method3388(var16.getAsJsonArray("pitch"));
         double[] var21 = BcCameraNet.method3388(var16.getAsJsonArray("my"));
         double[] var22 = BcCameraNet.method3388(var16.getAsJsonArray("mp"));
         double[] var23 = BcCameraNet.method3388(var16.getAsJsonArray("ok"));
         double[] var24 = BcCameraNet.method3388(var16.getAsJsonArray("s_yaw"));
         double[] var25 = BcCameraNet.method3388(var16.getAsJsonArray("s_pitch"));
         JsonObject var26 = var16.getAsJsonObject("rec");
         JsonArray var27 = var26.getAsJsonArray("pos");
         JsonArray var28 = var26.getAsJsonArray("tgt");
         double[] var29 = BcCameraNet.method3388(var26.getAsJsonArray("keys"));
         double[] var30 = BcCameraNet.method3388(var26.getAsJsonArray("attack"));
         double[] var31 = BcCameraNet.method3388(var26.getAsJsonArray("tgt_id"));
         double[] var32 = BcCameraNet.method3388(var26.getAsJsonArray("n_cand"));
         double[] var33 = BcCameraNet.method3388(var26.getAsJsonArray("tgt_sneak"));
         double[] var34 = BcCameraNet.method3388(var26.getAsJsonArray("on_ground"));
         double[] var35 = BcCameraNet.method3388(var26.getAsJsonArray("hcoll"));
         JsonArray var36 = var16.getAsJsonArray("x");
         JsonArray var37 = var16.getAsJsonArray("d");
         int var38 = var16.get("start").getAsInt();
         SilentAssistNet.Entry var39 = this.method3623();
         double[] var40 = new double[this.intValue2];

         for (int var41 = 1; var41 < var19.length; var41++) {
            int var42 = var41 - 1;
            BcCameraNet.Entry4 var43 = new BcCameraNet.Entry4();
            JsonArray var44 = var27.get(var42).getAsJsonArray();
            JsonArray var45 = var28.get(var42).getAsJsonArray();
            var43.doubleValue = BcCameraNet.method3389(var44.get(0).getAsDouble());
            var43.doubleValue2 = BcCameraNet.method3389(var44.get(1).getAsDouble());
            var43.doubleValue3 = BcCameraNet.method3389(var44.get(2).getAsDouble());
            var43.doubleValue4 = BcCameraNet.method3389(var45.get(0).getAsDouble());
            var43.doubleValue5 = BcCameraNet.method3389(var45.get(1).getAsDouble());
            var43.doubleValue6 = BcCameraNet.method3389(var45.get(2).getAsDouble());
            var43.intValue = (int)var29[var42];
            var43.flag3 = var30[var42] > 0.0;
            var43.intValue2 = (int)var31[var42];
            var43.intValue3 = (int)var32[var42];
            var43.flag4 = var33[var42] > 0.0;
            var43.flag = var34[var42] > 0.0;
            var43.flag2 = var35[var42] > 0.0;
            var43.doubleValue7 = var19[var42];
            var43.doubleValue8 = var20[var42];
            var43.doubleValue9 = var21[var42];
            var43.doubleValue10 = var22[var42];
            var43.flag5 = var23[var42] > 0.0;
            this.method3629(var39, var43);
            if (var41 >= var38) {
               double[] var46 = this.method3630(var39, var43, var21[var41], var22[var41], var23[var41] > 0.0, var17, var19[var41], var20[var41], var40);
               double[] var47 = BcCameraNet.method3388(var36.get(var41 - var38).getAsJsonArray());

               for (int var48 = 0; var48 < this.intValue2; var48++) {
                  var2 = Math.max(var2, Math.abs(var40[var48] - var47[var48]));
               }

               double[] var56 = BcCameraNet.method3388(var37.get(var41 - var38).getAsJsonArray());
               var4 = Math.max(var4, Math.max(Math.abs(var46[2] - var56[0]), Math.abs(var46[3] - var56[1])));
               double var49 = BcCameraNet.method3357(var46[0] - var24[var41]);
               double var51 = var46[1] - var25[var41];
               double var53 = Math.max(Math.abs(var49), Math.abs(var51));
               if (var53 > 1.0E-6 && method3631(var49, var39.doubleValue13, var17) && method3631(var51, var39.doubleValue14, var17)) {
                  var12++;
                  var39.doubleValue5 += var49;
                  var39.doubleValue6 += var51;
                  var39.doubleValue -= var49;
                  var39.doubleValue2 -= var51;
                  var39.doubleValue7 -= var49;
                  var39.doubleValue8 -= var51;
                  var39.doubleValue3 = BcCameraNet.method3357(var39.doubleValue - var19[var41]);
                  var39.doubleValue4 = var39.doubleValue2 - var20[var41];
                  var53 = 0.0;
               }

               if (var53 > 1.0E-6 && var13.isEmpty()) {
                  var13 = String.format(
                     "; первое расхождение камеры: кусок %d, тик %d (ход человека %.4f %.4f, решётка %.4f, выход %.5f %.5f / эталон %.5f %.5f)",
                     var11,
                     var41,
                     var21[var41],
                     var22[var41],
                     var17,
                     var46[2],
                     var46[3],
                     var56[0],
                     var56[1]
                  );
               }

               var6 = Math.max(var6, var53);
               var8 = Math.max(var8, Math.max(Math.abs(var39.doubleValue3), Math.abs(var39.doubleValue4)));
               var10++;
            }
         }
      }

      String var55 = String.format(
            "эталон: %d тиков, вход макс. расхождение %.2e, выход %.2e, отправленная камера %.2e°, перескоков у границы округления %d, |o| макс %.2f°",
            var10,
            var2,
            var4,
            var6,
            var12,
            var8
         )
         + var13;
      if (var2 <= 0.001 && var4 <= 0.002 && var6 <= 0.002 && var12 <= Math.max(3, var10 / 100)) {
         return var55;
      } else {
         throw new IOException("ЭТАЛОН НЕ СОШЁЛСЯ — " + var55);
      }
   }

   public static void main(String[] var0) throws Exception {
      try (InputStream var1 = Files.newInputStream(Path.of(var0[0]))) {
         SilentAssistNet var2 = method3634(var1);
         System.out.println("OK: " + var2.text2);
         SilentAssistNet.Entry var3 = var2.method3623();
         BcCameraNet.Entry4 var4 = new BcCameraNet.Entry4();
         long var5 = System.nanoTime();

         for (int var7 = 0; var7 < 2000; var7++) {
            var2.method3629(var3, var4);
            var2.method3630(var3, var4, 0.3, 0.0, true, 0.15, 0.0, 0.0, null);
         }

         System.out.printf("шаг: %.3f мс%n", (System.nanoTime() - var5) / 2000L / 1000000.0);
      }
   }

   public final class Entry {
      public final BcCameraNet.Entry2 entry2 = SilentAssistNet.this.bcCameraNet.method3355();
      public final BcCameraNet.Entry2 entry22 = SilentAssistNet.this.bcCameraNet.method3355();
      public double[][] doubleValues;
      public int intValue;
      public boolean flag;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public boolean flag2;
      public double doubleValue9;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public double doubleValue13;
      public double doubleValue14;

      public Entry() {
         this.doubleValues = new double[SilentAssistNet.this.layers][SilentAssistNet.this.intValue];
         this.doubleValue12 = Double.NaN;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public double getDoubleValue4() {
         return this.doubleValue4;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

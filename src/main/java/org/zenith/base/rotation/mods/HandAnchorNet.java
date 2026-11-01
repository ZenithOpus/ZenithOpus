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

public final class HandAnchorNet {
   public static final String text = "neurotest-hand-anchor-v1";
   public static final double CONST_doubleValue = 0.002;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public double[] doubleValues;
   public double doubleValue;
   public boolean flag;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public String source = "?";
   public String text2 = "";
   public double[][] doubleValues2;
   public double[][] doubleValues3;
   public double[][] doubleValues4;
   public double[][] doubleValues5;
   public double[][] doubleValues6;
   public double[][] doubleValues7;
   public double[][] doubleValues8;
   public double[][] doubleValues9;
   public double[][] doubleValues10;
   public double[][] doubleValues11;
   public double[][] doubleValues12;
   public double[][] doubleValues13;
   public double[][] doubleValues14;
   public double[][] doubleValues15;
   public double[] doubleValues16;
   public double[] doubleValues17;
   public double[] doubleValues18;
   public double[] doubleValues19;
   public double[] doubleValues20;
   public double[] doubleValues21;
   public double[] doubleValues22;
   public double[] doubleValues23;
   public double[] doubleValues24;
   public double[] doubleValues25;
   public double[] doubleValues26;
   public boolean flag2;
   public double doubleValue6;
   public double doubleValue7;
   public double doubleValue8;
   public double doubleValue9;
   public double doubleValue10;
   public double doubleValue11;
   public double doubleValue12;
   public double doubleValue13;
   public boolean flag3;
   public double doubleValue14;
   public double doubleValue15;

   public HandAnchorNet.Entry method3398() {
      return new HandAnchorNet.Entry();
   }

   public static double method3399(double var0) {
      return BcCameraNet.asinh(var0 / 0.5) / 4.0;
   }

   public double[] method3400(double[] var1, int var2, double[] var3, double[] var4, double[] var5, double var6, double var8) {
      double[] var10 = new double[this.intValue4];
      var10[0] = method3399(var1[0]);
      var10[1] = method3399(var1[1]);
      var10[2] = BcCameraNet.method3358(var1[0] / 30.0, -6.0, 6.0);
      var10[3] = BcCameraNet.method3358(var1[1] / 30.0, -6.0, 6.0);
      var10[4 + Math.max(0, Math.min(this.intValue - 1, var2))] = 1.0;
      int var11 = 4 + this.intValue;
      var10[var11] = method3399(var3[0]);
      var10[var11 + 1] = method3399(var3[1]);

      for (int var12 = 0; var12 < this.intValue3; var12++) {
         var10[var11 + 2 + var12] = var4[var12] * var8;
      }

      var11 += 2 + this.intValue3;
      var10[var11] = method3399(var5[0]);
      var10[var11 + 1] = method3399(var5[1]);
      var10[var11 + 2] = Math.log(var6 / 0.15);
      var10[var11 + 3] = var8;
      if (!(var8 > 0.0)) {
         for (int var14 = 0; var14 < 4 + this.intValue + 2; var14++) {
            var10[var14] = 0.0;
         }
      }

      return var10;
   }

   public double[] method3401(HandAnchorNet.Entry var1, double[] var2) {
      double[] var3 = BcCameraNet.method3372(this.doubleValues2, this.doubleValues16, var2);

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = BcCameraNet.method3369(var3[var4]);
      }

      double[] var15 = var1.doubleValues;
      double[] var5 = BcCameraNet.method3372(this.doubleValues3, this.doubleValues17, var3);
      double[] var6 = BcCameraNet.method3372(this.doubleValues4, this.doubleValues18, var15);
      double[] var7 = new double[this.intValue5];

      for (int var8 = 0; var8 < this.intValue5; var8++) {
         double var9 = BcCameraNet.method3371(var5[var8] + var6[var8]);
         double var11 = BcCameraNet.method3371(var5[this.intValue5 + var8] + var6[this.intValue5 + var8]);
         double var13 = Math.tanh(var5[2 * this.intValue5 + var8] + var9 * var6[2 * this.intValue5 + var8]);
         var7[var8] = (1.0 - var11) * var13 + var11 * var15[var8];
      }

      var1.doubleValues = var7;
      return var7;
   }

   public static double[] method3402(double[][] var0, double[] var1, double[][] var2, double[] var3, double[] var4) {
      double[] var5 = BcCameraNet.method3372(var0, var1, var4);

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = BcCameraNet.method3369(var5[var6]);
      }

      return BcCameraNet.method3372(var2, var3, var5);
   }

   public static double[] method3403(double[]... var0) {
      int var1 = 0;

      for (double[] var5 : var0) {
         var1 += var5.length;
      }

      double[] var8 = new double[var1];
      int var9 = 0;

      for (double[] var7 : var0) {
         System.arraycopy(var7, 0, var8, var9, var7.length);
         var9 += var7.length;
      }

      return var8;
   }

   public double[] method3404(double[] var1) {
      return method3402(this.doubleValues5, this.doubleValues19, this.doubleValues6, this.doubleValues20, var1);
   }

   public double[] method3405(double[] var1, int var2) {
      return method3402(this.doubleValues8, this.doubleValues21, this.doubleValues9, this.doubleValues22, method3403(var1, this.doubleValues7[var2]));
   }

   public double[] method3406(double[] var1, int var2, int var3) {
      return method3402(
         this.doubleValues11,
         this.doubleValues23,
         this.doubleValues12,
         this.doubleValues24,
         method3403(var1, this.doubleValues7[var2], this.doubleValues10[var3])
      );
   }

   public double[] method3407(double[] var1, int var2, int var3, int var4) {
      return method3402(
         this.doubleValues14,
         this.doubleValues25,
         this.doubleValues15,
         this.doubleValues26,
         method3403(var1, this.doubleValues7[var2], this.doubleValues10[var3], this.doubleValues13[var4])
      );
   }

   public static int method3408(double[] var0, Random var1) {
      double var2 = Double.NEGATIVE_INFINITY;

      for (double var7 : var0) {
         var2 = Math.max(var2, var7);
      }

      double var12 = 0.0;
      double[] var13 = new double[var0.length];

      for (int var14 = 0; var14 < var0.length; var14++) {
         var13[var14] = Math.exp(var0[var14] - var2);
         var12 += var13[var14];
      }

      double var15 = var1.nextDouble() * var12;
      double var9 = 0.0;

      for (int var11 = 0; var11 < var13.length; var11++) {
         var9 += var13[var11];
         if (var15 < var9) {
            return var11;
         }
      }

      return var13.length - 1;
   }

   public double method3409(int var1, int var2, double var3, Random var5) {
      if (var1 == 0) {
         return 0.0;
      } else {
         boolean var6 = var1 <= this.intValue6;
         int var7 = Math.max(0, Math.min(this.intValue6 - 1, var6 ? var1 - 1 : var1 - 1 - this.intValue6));
         double var8 = Math.log(this.doubleValues[var7]);
         double var10 = Math.log(this.doubleValues[var7 + 1]);
         double var12 = var8 + (var10 - var8) * var2 / this.intValue7;
         double var14 = var8 + (var10 - var8) * (var2 + 1) / this.intValue7;
         double var16 = Math.exp(var12 + (var14 - var12) * var5.nextDouble());
         double var18 = Math.ceil(Math.exp(var12) / var3);
         double var20 = Math.ceil(Math.exp(var14) / var3) - 1.0;
         double var22 = var5.nextDouble();
         double var24 = var20 >= var18 ? (var18 + Math.floor(var22 * (var20 - var18 + 1.0))) * var3 : var16;
         return var6 ? var24 : -var24;
      }
   }

   public double[] method3410(double[] var1, double[] var2, double[] var3, double[] var4, boolean var5, boolean var6, boolean var7, double var8, double var10) {
      double[] var12 = new double[3];
      double[] var13 = new double[3];

      for (int var14 = 0; var14 < 3; var14++) {
         double var15 = var2 == null ? 0.0 : var1[var14] - var2[var14];
         double var17 = var5 && var4 != null ? var3[var14] - var4[var14] : 0.0;
         var12[var14] = Double.isFinite(var15) ? var15 : 0.0;
         var13[var14] = Double.isFinite(var17) ? var17 : 0.0;
      }

      if (!this.flag) {
         var12[1] = 0.0;
         var13[1] = 0.0;
      }

      double var29 = var1[0] + var12[0] * this.doubleValue;
      double var16 = var1[1] + var12[1] * this.doubleValue + (var7 ? this.doubleValue3 : this.doubleValue2);
      double var18 = var1[2] + var12[2] * this.doubleValue;
      double var20 = var6 ? this.doubleValue5 : this.doubleValue4;
      double var22 = var3[0] + var13[0] * this.doubleValue;
      double var24 = var3[1] + var13[1] * this.doubleValue + var20 / 2.0;
      double var26 = var3[2] + var13[2] * this.doubleValue;
      double[] var28 = BcCameraNet.method3362(var22 - var29, var24 - var16, var26 - var18);
      return new double[]{BcCameraNet.method3357(var28[0] - var8), BcCameraNet.method3358(var28[1], -90.0, 90.0) - var10};
   }

   public void method3411(HandAnchorNet.Entry var1, double[] var2) {
      var1.doubleValues5[0] = var2[0];
      var1.doubleValues5[1] = var2[1];
      var1.doubleValue4 = var1.doubleValue4 + (Math.abs(var2[0]) - var1.doubleValue4) / this.doubleValue13;
   }

   public double[] method3412(HandAnchorNet.Entry var1, double var2, double var4) {
      var2 = BcCameraNet.method3358(var2, var1.doubleValues5[0] - this.doubleValue6, var1.doubleValues5[0] + this.doubleValue6);
      var4 = BcCameraNet.method3358(var4, var1.doubleValues5[1] - this.doubleValue7, var1.doubleValues5[1] + this.doubleValue7);
      double var6 = BcCameraNet.method3358((var1.doubleValue4 - this.doubleValue10) / (this.doubleValue11 - this.doubleValue10), 0.0, 1.0);
      double var8 = this.doubleValue8 + (this.doubleValue12 - this.doubleValue8) * var6;
      return new double[]{BcCameraNet.method3358(var2, -var8, var8), BcCameraNet.method3358(var4, -this.doubleValue9, this.doubleValue9)};
   }

   public double[] method3413(HandAnchorNet.Entry var1, double var2, double var4, boolean var6) {
      double var7 = var1.doubleValues6[0] + var1.doubleValues7[0] * this.intValue;
      double var9 = var1.doubleValues6[1] + var1.doubleValues7[1] * this.intValue;
      double var11 = BcCameraNet.method3357(var2 - var7);
      double var13 = var4 - var9;
      boolean var15 = !var1.flag;
      double var16 = var15 ? var2 : var7 + this.doubleValue14 * var11;
      double var18 = var15 ? var4 : var9 + this.doubleValue14 * var13;
      double var20 = var15 ? 0.0 : var1.doubleValues7[0] + this.doubleValue15 * var11 / this.intValue;
      double var22 = var15 ? 0.0 : var1.doubleValues7[1] + this.doubleValue15 * var13 / this.intValue;
      var1.doubleValues6 = new double[]{var16, var18};
      var1.doubleValues7 = new double[]{var20, var22};
      var1.flag = var6;
      return new double[]{var16, var18};
   }

   public double[] method3414(HandAnchorNet.Entry var1, double[] var2, double[] var3, double var4, double var6, double var8, Random var10) {
      if (var1.ticks > 0) {
         var1.doubleValues3[0] = var1.doubleValues3[0] + var2[0];
         var1.doubleValues3[1] = var1.doubleValues3[1] + var2[1];
      }

      if (this.flag2) {
         this.method3411(var1, var2);
      }

      if (var1.ticks % this.intValue == 0) {
         if (this.flag3) {
            if (var3 != null) {
               double[] var11 = this.method3413(var1, var4 + var3[0], var6 + var3[1], true);
               var3 = new double[]{BcCameraNet.method3357(var11[0] - var4), var11[1] - var6};
            } else {
               var1.flag = false;
            }
         }

         var1.doubleValue = var3 != null ? 1.0 : 0.0;
         var1.doubleValues2 = var3 != null ? new double[]{var3[0], var3[1]} : new double[2];

         for (int var18 = 0; var18 < this.intValue3; var18++) {
            var1.doubleValues4[var18] = var3 != null ? var10.nextGaussian() : 0.0;
         }

         var1.doubleValues3[0] = 0.0;
         var1.doubleValues3[1] = 0.0;
         var1.j = 0;
      }

      double[] var19 = new double[]{var1.doubleValues2[0] - var1.doubleValues3[0], var1.doubleValues2[1] - var1.doubleValues3[1]};
      double[] var12 = this.method3401(var1, this.method3400(var19, var1.j, var1.doubleValues2, var1.doubleValues4, var2, var8, var1.doubleValue));
      int var13 = method3408(this.method3404(var12), var10);
      int var14 = method3408(this.method3405(var12, var13), var10);
      int var15 = method3408(this.method3406(var12, var13, var14), var10);
      int var16 = method3408(this.method3407(var12, var13, var14, var15), var10);
      var1.j++;
      var1.ticks++;
      double[] var17 = new double[]{this.method3409(var13, var14, var8, var10), this.method3409(var15, var16, var8, var10)};
      return this.flag2 ? this.method3412(var1, var17[0], var17[1]) : var17;
   }

   public static double[] method3415(HandAnchorNet.Entry var0, double var1, double var3, double var5, double var7) {
      var0.doubleValue2 += var1;
      var0.doubleValue3 += var3;
      double var9 = Math.rint(var0.doubleValue2 / var5) * var5;
      double var11 = Math.rint(var0.doubleValue3 / var5) * var5;
      var0.doubleValue2 -= var9;
      var0.doubleValue3 -= var11;
      double var13 = Math.max(-90.0, Math.min(90.0, var7 + var11));
      return new double[]{var9, var13 - var7};
   }

   public static double[][] method3416(JsonArray var0) {
      double[][] var1 = new double[var0.size()][];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = BcCameraNet.method3388(var0.get(var2).getAsJsonArray());
      }

      return var1;
   }

   public static HandAnchorNet method3417(InputStream var0) throws IOException {
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
      if (!"neurotest-hand-anchor-v1".equals(var1.get("format").getAsString())) {
         throw new IOException("формат " + var1.get("format").getAsString() + ", ожидался neurotest-hand-anchor-v1");
      } else {
         HandAnchorNet var18 = new HandAnchorNet();
         var18.intValue = var1.get("K").getAsInt();
         var18.intValue2 = var1.get("H").getAsInt();
         var18.intValue3 = var1.get("DZ").getAsInt();
         var18.intValue4 = var1.get("nin").getAsInt();
         var18.intValue5 = var1.get("h").getAsInt();
         var18.intValue6 = var1.get("nb").getAsInt();
         var18.intValue7 = var1.get("nsub").getAsInt();
         var18.intValue8 = var1.get("ncls").getAsInt();
         var18.doubleValues = BcCameraNet.method3388(var1.getAsJsonArray("edges"));
         var18.doubleValue = var1.get("ex").getAsDouble();
         var18.flag = var1.get("vert").getAsBoolean();
         var18.doubleValue2 = var1.get("eye").getAsDouble();
         var18.doubleValue3 = var1.get("eye_sneak").getAsDouble();
         var18.doubleValue4 = var1.get("tgt_h").getAsDouble();
         var18.doubleValue5 = var1.get("tgt_h_sneak").getAsDouble();
         if (4 + var18.intValue + 2 + var18.intValue3 + 4 != var18.intValue4) {
            throw new IOException("вход руки " + var18.intValue4 + " не сходится с K " + var18.intValue + ", DZ " + var18.intValue3);
         } else {
            JsonObject var3 = var1.getAsJsonObject("weights");
            var18.doubleValues2 = BcCameraNet.method3386(var3, "inp.0.weight");
            var18.doubleValues16 = BcCameraNet.method3385(var3, "inp.0.bias");
            var18.doubleValues3 = BcCameraNet.method3386(var3, "gru.weight_ih_l0");
            var18.doubleValues4 = BcCameraNet.method3386(var3, "gru.weight_hh_l0");
            var18.doubleValues17 = BcCameraNet.method3385(var3, "gru.bias_ih_l0");
            var18.doubleValues18 = BcCameraNet.method3385(var3, "gru.bias_hh_l0");
            var18.doubleValues5 = BcCameraNet.method3386(var3, "yaw.0.weight");
            var18.doubleValues19 = BcCameraNet.method3385(var3, "yaw.0.bias");
            var18.doubleValues6 = BcCameraNet.method3386(var3, "yaw.2.weight");
            var18.doubleValues20 = BcCameraNet.method3385(var3, "yaw.2.bias");
            var18.doubleValues7 = BcCameraNet.method3386(var3, "yemb.weight");
            var18.doubleValues8 = BcCameraNet.method3386(var3, "ysub.0.weight");
            var18.doubleValues21 = BcCameraNet.method3385(var3, "ysub.0.bias");
            var18.doubleValues9 = BcCameraNet.method3386(var3, "ysub.2.weight");
            var18.doubleValues22 = BcCameraNet.method3385(var3, "ysub.2.bias");
            var18.doubleValues10 = BcCameraNet.method3386(var3, "ysemb.weight");
            var18.doubleValues11 = BcCameraNet.method3386(var3, "pitch.0.weight");
            var18.doubleValues23 = BcCameraNet.method3385(var3, "pitch.0.bias");
            var18.doubleValues12 = BcCameraNet.method3386(var3, "pitch.2.weight");
            var18.doubleValues24 = BcCameraNet.method3385(var3, "pitch.2.bias");
            var18.doubleValues13 = BcCameraNet.method3386(var3, "pcemb.weight");
            var18.doubleValues14 = BcCameraNet.method3386(var3, "psub.0.weight");
            var18.doubleValues25 = BcCameraNet.method3385(var3, "psub.0.bias");
            var18.doubleValues15 = BcCameraNet.method3386(var3, "psub.2.weight");
            var18.doubleValues26 = BcCameraNet.method3385(var3, "psub.2.bias");
            String var4 = "";
            if (var1.has("governor") && var1.get("governor").isJsonObject()) {
               JsonObject var5 = var1.getAsJsonObject("governor");
               var18.flag2 = true;
               var18.doubleValue6 = var5.get("ay").getAsDouble();
               var18.doubleValue7 = var5.get("ap").getAsDouble();
               var18.doubleValue8 = var5.get("vy").getAsDouble();
               var18.doubleValue9 = var5.get("vp").getAsDouble();
               var18.doubleValue10 = var5.get("e0").getAsDouble();
               var18.doubleValue11 = var5.get("e1").getAsDouble();
               var18.doubleValue12 = var5.get("vmin").getAsDouble();
               var18.doubleValue13 = var5.get("tau").getAsDouble();
               JsonObject var6 = var5.getAsJsonObject("ref");
               double[][] var7 = method3416(var6.getAsJsonArray("real"));
               double[][] var8 = method3416(var6.getAsJsonArray("want"));
               double[][] var9 = method3416(var6.getAsJsonArray("out"));
               HandAnchorNet.Entry var10 = var18.method3398();
               double var11 = 0.0;

               for (int var13 = 0; var13 < var7.length; var13++) {
                  var18.method3411(var10, var7[var13]);
                  double[] var14 = var18.method3412(var10, var8[var13][0], var8[var13][1]);
                  var11 = Math.max(var11, Math.max(Math.abs(var14[0] - var9[var13][0]), Math.abs(var14[1] - var9[var13][1])));
               }

               if (!(var11 < 1.0E-9)) {
                  throw new IOException("эталон мышц не сошёлся: " + var11);
               }

               var4 = var4
                  + String.format(
                     "; мышцы: ускорение %.0f/%.0f°, скорость %.0f/%.0f°, усталость %.0f->%.0f °/тик до %.0f° (эталон %.1e)",
                     var18.doubleValue6,
                     var18.doubleValue7,
                     var18.doubleValue8,
                     var18.doubleValue9,
                     var18.doubleValue10,
                     var18.doubleValue11,
                     var18.doubleValue12,
                     var11
                  );
            }

            if (var1.has("tracker") && var1.get("tracker").isJsonObject()) {
               JsonObject var19 = var1.getAsJsonObject("tracker");
               var18.flag3 = true;
               var18.doubleValue14 = var19.get("alpha").getAsDouble();
               var18.doubleValue15 = var18.doubleValue14 * var18.doubleValue14 / (2.0 - var18.doubleValue14);
               JsonObject var20 = var19.getAsJsonObject("ref");
               double[] var21 = BcCameraNet.method3388(var20.getAsJsonArray("wy"));
               double[] var22 = BcCameraNet.method3388(var20.getAsJsonArray("wp"));
               double[] var23 = BcCameraNet.method3388(var20.getAsJsonArray("has"));
               double[][] var24 = method3416(var20.getAsJsonArray("out"));
               HandAnchorNet.Entry var25 = var18.method3398();
               double var12 = 0.0;

               for (int var26 = 0; var26 < var21.length; var26++) {
                  double[] var15 = var18.method3413(var25, var21[var26], var22[var26], var23[var26] > 0.0);
                  var12 = Math.max(var12, Math.max(Math.abs(var15[0] - var24[var26][0]), Math.abs(var15[1] - var24[var26][1])));
               }

               if (!(var12 < 1.0E-6)) {
                  throw new IOException("эталон трекера не сошёлся: " + var12);
               }

               var4 = var4 + String.format("; трекер намерения alpha %.2f (эталон %.1e)", var18.doubleValue14, var12);
            }

            var18.source = var1.get("source").getAsString().replace(',', ';');
            var18.text2 = var18.method3418(var1.getAsJsonObject("ref_anchor"), var1.getAsJsonObject("ref_net")) + var4;
            return var18;
         }
      }
   }

   public String method3418(JsonObject var1, JsonObject var2) throws IOException {
      double[][] var3 = method3416(var1.getAsJsonArray("p1"));
      double[][] var4 = method3416(var1.getAsJsonArray("p2"));
      double[][] var5 = method3416(var1.getAsJsonArray("g1"));
      double[][] var6 = method3416(var1.getAsJsonArray("g2"));
      double[][] var7 = method3416(var1.getAsJsonArray("out"));
      double[] var8 = BcCameraNet.method3388(var1.getAsJsonArray("same"));
      double[] var9 = BcCameraNet.method3388(var1.getAsJsonArray("tsneak"));
      double[] var10 = BcCameraNet.method3388(var1.getAsJsonArray("sneak"));
      double[] var11 = BcCameraNet.method3388(var1.getAsJsonArray("cy"));
      double[] var12 = BcCameraNet.method3388(var1.getAsJsonArray("cp"));
      double var13 = 0.0;

      for (int var15 = 0; var15 < var3.length; var15++) {
         double[] var16 = this.method3410(
            var3[var15], var4[var15], var5[var15], var6[var15], var8[var15] > 0.0, var9[var15] > 0.0, var10[var15] > 0.0, var11[var15], var12[var15]
         );
         var13 = Math.max(var13, Math.max(Math.abs(var16[0] - var7[var15][0]), Math.abs(var16[1] - var7[var15][1])));
      }

      double[][] var45 = method3416(var2.getAsJsonArray("r"));
      double[][] var46 = method3416(var2.getAsJsonArray("D"));
      double[][] var17 = method3416(var2.getAsJsonArray("z"));
      double[][] var18 = method3416(var2.getAsJsonArray("prev"));
      double[][] var19 = method3416(var2.getAsJsonArray("f"));
      double[][] var20 = method3416(var2.getAsJsonArray("ly"));
      double[][] var21 = method3416(var2.getAsJsonArray("lys"));
      double[][] var22 = method3416(var2.getAsJsonArray("lp"));
      double[][] var23 = method3416(var2.getAsJsonArray("lps"));
      double[] var24 = BcCameraNet.method3388(var2.getAsJsonArray("j"));
      double[] var25 = BcCameraNet.method3388(var2.getAsJsonArray("grid"));
      double[] var26 = BcCameraNet.method3388(var2.getAsJsonArray("has"));
      double[] var27 = BcCameraNet.method3388(var2.getAsJsonArray("yc"));
      double[] var28 = BcCameraNet.method3388(var2.getAsJsonArray("ys"));
      double[] var29 = BcCameraNet.method3388(var2.getAsJsonArray("pc"));
      HandAnchorNet.Entry var30 = this.method3398();
      double var31 = 0.0;
      double var33 = 0.0;

      for (int var35 = 0; var35 < var45.length; var35++) {
         double[] var36 = this.method3400(var45[var35], (int)var24[var35], var46[var35], var17[var35], var18[var35], var25[var35], var26[var35]);

         for (int var37 = 0; var37 < this.intValue4; var37++) {
            var31 = Math.max(var31, Math.abs(var36[var37] - var19[var35][var37]));
         }

         double[] var48 = this.method3401(var30, var36);
         int var38 = (int)var27[var35];
         int var39 = (int)var28[var35];
         int var40 = (int)var29[var35];
         double[][] var41 = new double[][]{
            this.method3404(var48), this.method3405(var48, var38), this.method3406(var48, var38, var39), this.method3407(var48, var38, var39, var40)
         };
         double[][] var42 = new double[][]{var20[var35], var21[var35], var22[var35], var23[var35]};

         for (int var43 = 0; var43 < 4; var43++) {
            for (int var44 = 0; var44 < var41[var43].length; var44++) {
               var33 = Math.max(var33, Math.abs(var41[var43][var44] - var42[var43][var44]));
            }
         }
      }

      String var47 = String.format(
         "эталон: якорь %d (%.1e), признаки %.1e, логиты %d тиков %.1e; K %d H %d, продление %.0f тиков%s",
         var3.length,
         var13,
         var31,
         var45.length,
         var33,
         this.intValue,
         this.intValue2,
         this.doubleValue,
         this.flag ? " с вертикалью" : ""
      );
      if (var13 < 1.0E-6 && var31 < 1.0E-5 && var33 <= 0.002) {
         return var47;
      } else {
         throw new IOException("ЭТАЛОН РУКИ НЕ СОШЁЛСЯ — " + var47);
      }
   }

   public static void main(String[] var0) throws Exception {
      try (InputStream var1 = Files.newInputStream(Path.of(var0[0]))) {
         HandAnchorNet var2 = method3417(var1);
         System.out.println("OK: " + var2.text2 + "; модель " + var2.source);
         HandAnchorNet.Entry var3 = var2.method3398();
         Random var4 = new Random(0L);
         long var5 = System.nanoTime();

         for (int var7 = 0; var7 < 2000; var7++) {
            var2.method3414(var3, new double[]{0.3, -0.1}, new double[]{12.0, 3.0}, 0.0, 0.0, 0.15, var4);
         }

         System.out.printf("шаг руки: %.3f мс%n", (System.nanoTime() - var5) / 2000L / 1000000.0);

         for (int var19 = 0; var19 < 3; var19++) {
            var3 = var2.method3398();
            double var8 = 0.0;
            double var10 = 0.0;
            double[] var12 = new double[]{0.0, 0.0};
            StringBuilder var13 = new StringBuilder("контур:");

            for (int var14 = 0; var14 < 24; var14++) {
               double[] var15 = var2.method3414(var3, var12, new double[]{30.0 - var8, 10.0 - var10}, var8, var10, 0.15, var4);
               var12 = method3415(var3, var15[0], var15[1], 0.15, var10);
               var8 += var12[0];
               var10 += var12[1];
               var13.append(String.format(" %.1f/%.1f", var8, var10));
            }

            System.out.println(var13);
         }
      }
   }

   public final class Entry {
      public double[] doubleValues = new double[HandAnchorNet.this.intValue5];
      public double[] doubleValues2 = new double[2];
      public double[] doubleValues3 = new double[2];
      public double[] doubleValues4 = new double[HandAnchorNet.this.intValue3];
      public double doubleValue;
      public int j;
      public int ticks;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double[] doubleValues5 = new double[2];
      public double[] doubleValues6 = new double[2];
      public double[] doubleValues7 = new double[2];
      public boolean flag;

      public void method3419() {
         this.doubleValue2 = 0.0;
         this.doubleValue3 = 0.0;
      }
   }
}

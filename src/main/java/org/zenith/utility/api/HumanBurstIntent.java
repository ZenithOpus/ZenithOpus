package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HumanBurstIntent {
   public static final int CONST_intValue = 59;
   public static final int CONST_intValue2 = 0;
   public static final int CONST_intValue3 = 1;
   public static final int CONST_intValue4 = 2;
   private static final String text = "/assets/zenith/pointclick/human_burst_intent.json";
   private static final Path path = Path.of("human_burst_intent.json");
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final HumanBurstIntent.Data[] datas;
   private final HumanBurstIntent.Data5[] data5s;
   private final HumanBurstIntent.Data2[] data2s;
   private final float[][] floatValues;
   private final float[] floatValues2;
   private final float[][] floatValues3;
   private final float[] floatValues4;
   private final float[][] floatValues5;
   private final float[] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final float floatValue;
   private final float[][] floatValues9;
   private final float[] floatValues10;
   private final float[][] floatValues11;
   private final float[] floatValues12;
   private final float[][] floatValues13;
   private final float[] floatValues14;
   private final float[][] floatValues15;
   private final float[] floatValues16;
   private final float[][] floatValues17;
   private final float[] floatValues18;
   private final float[][] floatValues19;
   private final float[] floatValues20;
   private final float[][] floatValues21;
   private final float[] floatValues22;
   private final float[][] floatValues23;
   private int intValue5;

   private HumanBurstIntent(
      int var1,
      int var2,
      int var3,
      int var4,
      HumanBurstIntent.Data[] var5,
      HumanBurstIntent.Data5[] var6,
      HumanBurstIntent.Data2[] var7,
      float[][] var8,
      float[] var9,
      float[][] var10,
      float[] var11,
      float[][] var12,
      float[] var13,
      float[] var14,
      float[] var15,
      float var16,
      float[][] var17,
      float[] var18,
      float[][] var19,
      float[] var20,
      float[][] var21,
      float[] var22,
      float[][] var23,
      float[] var24,
      float[][] var25,
      float[] var26,
      float[][] var27,
      float[] var28,
      float[][] var29,
      float[] var30
   ) {
      this.intValue = var1;
      this.intValue2 = var2;
      this.intValue3 = var3;
      this.intValue4 = var4;
      this.datas = var5;
      this.data5s = var6;
      this.data2s = var7;
      this.floatValues = var8;
      this.floatValues2 = var9;
      this.floatValues3 = var10;
      this.floatValues4 = var11;
      this.floatValues5 = var12;
      this.floatValues6 = var13;
      this.floatValues7 = var14;
      this.floatValues8 = var15;
      this.floatValue = var16;
      this.floatValues9 = var17;
      this.floatValues10 = var18;
      this.floatValues11 = var19;
      this.floatValues12 = var20;
      this.floatValues13 = var21;
      this.floatValues14 = var22;
      this.floatValues15 = var23;
      this.floatValues16 = var24;
      this.floatValues17 = var25;
      this.floatValues18 = var26;
      this.floatValues19 = var27;
      this.floatValues20 = var28;
      this.floatValues21 = var29;
      this.floatValues22 = var30;
      this.floatValues23 = new float[var4][var1];
   }

   public static HumanBurstIntent method6372() {
      HumanBurstIntent var0 = method6373(path);
      if (var0 != null) {
         return var0;
      } else {
         try {
            HumanBurstIntent var2;
            try (InputStream var1 = HumanBurstIntent.class.getResourceAsStream("/assets/zenith/pointclick/human_burst_intent.json")) {
               if (var1 == null) {
                  return null;
               }

               var2 = method6374(new String(var1.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/pointclick/human_burst_intent.json");
            }

            return var2;
         } catch (Throwable var6) {
            System.err.println("[HumanBurst] failed to load /assets/zenith/pointclick/human_burst_intent.json: " + var6);
            return null;
         }
      }
   }

   private static HumanBurstIntent method6373(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : method6374(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable var2) {
         System.err.println("[HumanBurst] failed to load " + var0 + ": " + var2);
         return null;
      }
   }

   private static HumanBurstIntent method6374(String var0, String var1) {
      JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
      method6399(var2.get("format_version").getAsInt() == 2, "format version");
      method6399("human_burst_target_relative_gru_bump".equals(var2.get("model").getAsString()), "model name");
      JsonObject var3 = var2.getAsJsonObject("architecture");
      int var4 = var3.get("input_size").getAsInt();
      int var5 = var3.get("hidden_size").getAsInt();
      int var6 = var3.get("layers").getAsInt();
      int var7 = var3.get("max_horizon").getAsInt();
      int var8 = var2.get("history_ticks").getAsInt();
      method6399(var4 == 61, "input size");
      method6399(var5 > 0 && var6 > 0 && var7 >= 2 && var8 > 0, "architecture");
      JsonArray var9 = var2.getAsJsonArray("feature_spec");
      method6399(var9.size() == var4, "feature count");
      HumanBurstIntent.Data[] var10 = new HumanBurstIntent.Data[var4];

      for (int var11 = 0; var11 < var4; var11++) {
         JsonObject var12 = var9.get(var11).getAsJsonObject();
         var10[var11] = new HumanBurstIntent.Data(
            var12.get("name").getAsString(),
            var12.get("transform").getAsString(),
            var12.get("scale").getAsFloat(),
            var12.get("mean").getAsFloat(),
            var12.get("std").getAsFloat()
         );
         method6399(Float.isFinite(var10[var11].getFloatValue3()) && var10[var11].getFloatValue3() > 0.0F, "feature std " + var11);
      }

      method6399("target_yaw_error_sin".equals(var10[var4 - 2].name()), "yaw sine feature");
      method6399("target_yaw_error_cos".equals(var10[var4 - 1].name()), "yaw cosine feature");
      JsonArray var19 = var2.getAsJsonArray("target_spec");
      method6399(var19.size() == 6, "target count");
      HumanBurstIntent.Data5[] var20 = new HumanBurstIntent.Data5[6];

      for (int var13 = 0; var13 < var20.length; var13++) {
         JsonObject var14 = var19.get(var13).getAsJsonObject();
         var20[var13] = new HumanBurstIntent.Data5(
            var14.get("transform").getAsString(), var14.get("scale").getAsFloat(), var14.get("mean").getAsFloat(), var14.get("std").getAsFloat()
         );
         method6399(Float.isFinite(var20[var13].getFloatValue3()) && var20[var13].getFloatValue3() > 0.0F, "target std " + var13);
      }

      JsonObject var21 = var2.getAsJsonObject("weights");
      JsonArray var22 = var21.getAsJsonArray("gru_layers");
      method6399(var22.size() == var6, "GRU layer count");
      HumanBurstIntent.Data2[] var15 = new HumanBurstIntent.Data2[var6];
      int var16 = var4;

      for (int var17 = 0; var17 < var6; var17++) {
         JsonObject var18 = var22.get(var17).getAsJsonObject();
         var15[var17] = new HumanBurstIntent.Data2(
            method6398(var18.getAsJsonArray("weight_ih")),
            method6398(var18.getAsJsonArray("weight_hh")),
            method6397(var18.getAsJsonArray("bias_ih")),
            method6397(var18.getAsJsonArray("bias_hh"))
         );
         method6384(var15[var17], var16, var5, "GRU layer " + var17);
         var16 = var5;
      }

      HumanBurstIntent var23 = new HumanBurstIntent(
         var4,
         var5,
         var7,
         var8,
         var10,
         var20,
         var15,
         method6398(var21.getAsJsonArray("attention_projection_weight")),
         method6397(var21.getAsJsonArray("attention_projection_bias")),
         method6398(var21.getAsJsonArray("attention_score_weight")),
         method6397(var21.getAsJsonArray("attention_score_bias")),
         method6398(var21.getAsJsonArray("trunk_weight")),
         method6397(var21.getAsJsonArray("trunk_bias")),
         method6397(var21.getAsJsonArray("layer_norm_weight")),
         method6397(var21.getAsJsonArray("layer_norm_bias")),
         var21.get("layer_norm_epsilon").getAsFloat(),
         method6398(var21.getAsJsonArray("mode_weight")),
         method6397(var21.getAsJsonArray("mode_bias")),
         method6398(var21.getAsJsonArray("horizon_weight")),
         method6397(var21.getAsJsonArray("horizon_bias")),
         method6398(var21.getAsJsonArray("motion_weight")),
         method6397(var21.getAsJsonArray("motion_bias")),
         method6398(var21.getAsJsonArray("aim_weight")),
         method6397(var21.getAsJsonArray("aim_bias")),
         method6398(var21.getAsJsonArray("terminal_weight")),
         method6397(var21.getAsJsonArray("terminal_bias")),
         method6398(var21.getAsJsonArray("residual_weight")),
         method6397(var21.getAsJsonArray("residual_bias")),
         method6398(var21.getAsJsonArray("commit_weight")),
         method6397(var21.getAsJsonArray("commit_bias"))
      );
      var23.method6383();
      method6386(var23, var2.getAsJsonArray("parity"));
      System.out.println("[HumanBurst] loaded " + var1 + " (GRU" + var5 + "x" + var6 + ", attention, history=" + var8 + ")");
      return var23;
   }

   public void reset() {
      this.intValue5 = 0;
   }

   public void method6375(float[] var1, float var2) {
      float[] var3 = this.method6378(var1, var2);
      if (this.intValue5 < this.intValue4) {
         this.floatValues23[this.intValue5++] = var3;
      } else {
         System.arraycopy(this.floatValues23, 1, this.floatValues23, 0, this.intValue4 - 1);
         this.floatValues23[this.intValue4 - 1] = var3;
      }
   }

   public HumanBurstIntent.Data4 method6376() {
      return this.intValue5 == 0 ? method6377() : this.method6382(this.method6379(this.floatValues23, this.intValue5));
   }

   private static HumanBurstIntent.Data4 method6377() {
      return new HumanBurstIntent.Data4(0, 0, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   private float[] method6378(float[] var1, float var2) {
      if (var1 != null && var1.length == 59) {
         method6399(Float.isFinite(var2) && var2 > 0.0F, "mouse GCD");
         float[] var3 = new float[this.intValue];

         for (int var4 = 0; var4 < 59; var4++) {
            float var5 = var4 == 26 ? method6387(var1[var4], var2) : var1[var4];
            var3[var4] = method6390(method6388(var5, this.datas[var4]), this.datas[var4], "feature " + var4);
         }

         double var6 = Math.toRadians(var1[0] * var2);
         var3[59] = method6390((float)Math.sin(var6), this.datas[59], "yaw sine");
         var3[60] = method6390((float)Math.cos(var6), this.datas[60], "yaw cosine");
         return var3;
      } else {
         throw new IllegalArgumentException("expected 59 raw features");
      }
   }

   private HumanBurstIntent.Data3 method6379(float[][] var1, int var2) {
      method6399(var2 > 0 && var2 <= var1.length, "history length");
      float[][] var3 = new float[this.data2s.length][this.intValue2];
      float[][] var4 = new float[var2][this.intValue2];

      for (int var5 = 0; var5 < var2; var5++) {
         float[] var6 = var1[var5];

         for (int var7 = 0; var7 < this.data2s.length; var7++) {
            var3[var7] = this.method6380(var6, var3[var7], this.data2s[var7]);
            var6 = var3[var7];
         }

         var4[var5] = (float[])var6.clone();
      }

      float[] var11 = new float[var2];

      for (int var12 = 0; var12 < var2; var12++) {
         float[] var14 = method6392(var4[var12], this.floatValues, this.floatValues2);

         for (int var8 = 0; var8 < var14.length; var8++) {
            var14[var8] = (float)Math.tanh(var14[var8]);
         }

         var11[var12] = method6392(var14, this.floatValues3, this.floatValues4)[0];
      }

      float[] var13 = method6394(var11);
      float[] var15 = new float[this.intValue2];

      for (int var16 = 0; var16 < var2; var16++) {
         for (int var9 = 0; var9 < this.intValue2; var9++) {
            var15[var9] += var13[var16] * var4[var16][var9];
         }
      }

      float[] var17 = new float[this.intValue2 * 2];
      System.arraycopy(var4[var2 - 1], 0, var17, 0, this.intValue2);
      System.arraycopy(var15, 0, var17, this.intValue2, this.intValue2);
      float[] var18 = method6392(var17, this.floatValues5, this.floatValues6);

      for (int var10 = 0; var10 < var18.length; var10++) {
         var18[var10] *= method6393(var18[var10]);
      }

      var18 = this.method6381(var18);
      return new HumanBurstIntent.Data3(
         method6392(var18, this.floatValues9, this.floatValues10),
         method6392(var18, this.floatValues11, this.floatValues12),
         method6392(var18, this.floatValues13, this.floatValues14),
         method6392(var18, this.floatValues15, this.floatValues16),
         method6392(var18, this.floatValues17, this.floatValues18),
         method6392(var18, this.floatValues19, this.floatValues20),
         method6392(var18, this.floatValues21, this.floatValues22)[0]
      );
   }

   private float[] method6380(float[] var1, float[] var2, HumanBurstIntent.Data2 var3) {
      int var4 = this.intValue2;
      float[] var5 = method6391(var1, var3.getFloatValues(), var3.getFloatValues3());
      float[] var6 = method6391(var2, var3.getFloatValues2(), var3.getFloatValues4());
      float[] var7 = new float[var4];

      for (int var8 = 0; var8 < var4; var8++) {
         float var9 = method6393(var5[var8] + var6[var8]);
         float var10 = method6393(var5[var4 + var8] + var6[var4 + var8]);
         float var11 = (float)Math.tanh(var5[2 * var4 + var8] + var9 * var6[2 * var4 + var8]);
         var7[var8] = (1.0F - var10) * var11 + var10 * var2[var8];
      }

      return var7;
   }

   private float[] method6381(float[] var1) {
      double var2 = 0.0;

      for (float var7 : var1) {
         var2 += var7;
      }

      var2 /= var1.length;
      double var13 = 0.0;

      for (float var9 : var1) {
         double var10 = var9 - var2;
         var13 += var10 * var10;
      }

      var13 /= var1.length;
      double var16 = 1.0 / Math.sqrt(var13 + this.floatValue);
      float[] var18 = new float[var1.length];

      for (int var19 = 0; var19 < var18.length; var19++) {
         var18[var19] = (float)((var1[var19] - var2) * var16) * this.floatValues7[var19] + this.floatValues8[var19];
      }

      return var18;
   }

   private HumanBurstIntent.Data4 method6382(HumanBurstIntent.Data3 var1) {
      int var2 = method6395(var1.getFloatValues());
      float[] var3 = method6394(var1.getFloatValues2());
      double var4 = 0.0;

      for (int var6 = 0; var6 < var3.length; var6++) {
         var4 += var3[var6] * (var6 + 2);
      }

      int var11 = Math.max(2, Math.min(this.intValue3, (int)Math.round(var4)));
      if (var2 == 0) {
         var11 = 0;
      } else if (var2 == 1) {
         var11 = 1;
      }

      float[] var7 = new float[]{
         var1.getFloatValues3()[0],
         var1.getFloatValues3()[1],
         var1.getFloatValues5()[0],
         var1.getFloatValues5()[1],
         var1.getFloatValues6()[0],
         var1.getFloatValues6()[1]
      };
      float[] var8 = new float[this.data5s.length];

      for (int var9 = 0; var9 < var8.length; var9++) {
         float var10 = var7[var9] * this.data5s[var9].getFloatValue3() + this.data5s[var9].getFloatValue2();
         var8[var9] = method6389(var10, this.data5s[var9]);
      }

      return new HumanBurstIntent.Data4(
         var2,
         var11,
         var8[0],
         var8[1],
         method6393(var1.getFloatValues4()[0]),
         method6393(var1.getFloatValues4()[1]),
         var8[2],
         var8[3],
         var8[4],
         var8[5],
         method6393(var1.getFloatValue())
      );
   }

   private void method6383() {
      method6385(this.floatValues, this.floatValues2, this.intValue2, this.intValue2, "attention projection");
      method6385(this.floatValues3, this.floatValues4, this.intValue2, 1, "attention score");
      method6385(this.floatValues5, this.floatValues6, this.intValue2 * 2, this.intValue2, "trunk");
      method6399(this.floatValues7.length == this.intValue2 && this.floatValues8.length == this.intValue2, "layer norm");
      method6385(this.floatValues9, this.floatValues10, this.intValue2, 3, "mode head");
      method6385(this.floatValues11, this.floatValues12, this.intValue2, this.intValue3 - 1, "horizon head");
      method6385(this.floatValues13, this.floatValues14, this.intValue2, 2, "motion head");
      method6385(this.floatValues15, this.floatValues16, this.intValue2, 2, "aim head");
      method6385(this.floatValues17, this.floatValues18, this.intValue2, 2, "terminal head");
      method6385(this.floatValues19, this.floatValues20, this.intValue2, 2, "residual head");
      method6385(this.floatValues21, this.floatValues22, this.intValue2, 1, "commit head");
      method6399(Float.isFinite(this.floatValue) && this.floatValue > 0.0F, "layer norm epsilon");
   }

   private static void method6384(HumanBurstIntent.Data2 var0, int var1, int var2, String var3) {
      method6385(var0.getFloatValues(), var0.getFloatValues3(), var1, var2 * 3, var3 + " input");
      method6385(var0.getFloatValues2(), var0.getFloatValues4(), var2, var2 * 3, var3 + " hidden");
   }

   private static void method6385(float[][] var0, float[] var1, int var2, int var3, String var4) {
      method6399(var0.length == var3 && var1.length == var3, var4 + " output size");

      for (float[] var8 : var0) {
         method6399(var8.length == var2, var4 + " input size");
      }
   }

   private static void method6386(HumanBurstIntent var0, JsonArray var1) {
      for (int var2 = 0; var2 < var1.size(); var2++) {
         JsonObject var3 = var1.get(var2).getAsJsonObject();
         JsonArray var4 = var3.getAsJsonArray("raw_history");
         float[][] var5 = new float[var4.size()][];
         float var6 = var3.get("mouse_gcd").getAsFloat();

         for (int var7 = 0; var7 < var4.size(); var7++) {
            var5[var7] = var0.method6378(method6397(var4.get(var7).getAsJsonArray()), var6);
         }

         HumanBurstIntent.Data3 var11 = var0.method6379(var5, var5.length);
         float var8 = 0.0F;
         var8 = Math.max(var8, method6396(var11.getFloatValues(), method6397(var3.getAsJsonArray("mode_logits"))));
         var8 = Math.max(var8, method6396(var11.getFloatValues2(), method6397(var3.getAsJsonArray("horizon_logits"))));
         var8 = Math.max(var8, method6396(var11.getFloatValues3(), method6397(var3.getAsJsonArray("motion"))));
         var8 = Math.max(var8, method6396(var11.getFloatValues4(), method6397(var3.getAsJsonArray("aim_logits"))));
         var8 = Math.max(var8, method6396(var11.getFloatValues5(), method6397(var3.getAsJsonArray("terminal"))));
         var8 = Math.max(var8, method6396(var11.getFloatValues6(), method6397(var3.getAsJsonArray("residual"))));
         var8 = Math.max(var8, Math.abs(var11.getFloatValue() - var3.get("commit_logit").getAsFloat()));
         method6399(var8 <= 0.005F, "parity case " + var2 + ", error=" + var8);
         HumanBurstIntent.Data4 var9 = var0.method6382(var11);
         JsonObject var10 = var3.getAsJsonObject("intent");
         method6399(var9.getIntValue() == var10.get("mode").getAsInt(), "parity mode " + var2);
         method6399(var9.getIntValue2() == var10.get("horizon_ticks").getAsInt(), "parity horizon " + var2);
      }
   }

   private static float method6387(float var0, float var1) {
      float var2 = 360.0F / var1;
      return var0 - var2 * (float)Math.floor((var0 + var2 * 0.5F) / var2);
   }

   private static float method6388(float var0, HumanBurstIntent.Data var1) {
      method6399(Float.isFinite(var0), "non-finite " + var1.name());
      String var2 = var1.getText2();

      return switch (var2) {
         case "signed_mu" -> (float)(Math.signum(var0) * Math.log1p(Math.abs(var0) / var1.getFloatValue()));
         case "log1p" -> (float)Math.log1p(Math.max(var0, 0.0F) / var1.getFloatValue());
         case "linear" -> var0;
         default -> throw new IllegalStateException("unsupported feature transform " + var1.getText2());
      };
   }

   private static float method6389(float var0, HumanBurstIntent.Data5 var1) {
      String var2 = var1.getText();

      return switch (var2) {
         case "signed_mu" -> (float)(Math.signum(var0) * Math.expm1(Math.abs(var0)) * var1.getFloatValue());
         case "log1p" -> (float)Math.max(Math.expm1(var0) * var1.getFloatValue(), 0.0);
         case "linear" -> var0;
         default -> throw new IllegalStateException("unsupported target transform " + var1.getText());
      };
   }

   private static float method6390(float var0, HumanBurstIntent.Data var1, String var2) {
      float var3 = (var0 - var1.getFloatValue2()) / var1.getFloatValue3();
      method6399(Float.isFinite(var3), "non-finite normalized " + var2);
      return var3;
   }

   private static float[] method6391(float[] var0, float[][] var1, float[] var2) {
      float[] var3 = new float[var2.length];

      for (int var4 = 0; var4 < var3.length; var4++) {
         double var5 = var2[var4];

         for (int var7 = 0; var7 < var0.length; var7++) {
            var5 += var1[var4][var7] * var0[var7];
         }

         var3[var4] = (float)var5;
      }

      return var3;
   }

   private static float[] method6392(float[] var0, float[][] var1, float[] var2) {
      return method6391(var0, var1, var2);
   }

   private static float method6393(float var0) {
      if (var0 >= 0.0F) {
         double var3 = Math.exp(-var0);
         return (float)(1.0 / (1.0 + var3));
      } else {
         double var1 = Math.exp(var0);
         return (float)(var1 / (1.0 + var1));
      }
   }

   private static float[] method6394(float[] var0) {
      float var1 = var0[0];

      for (int var2 = 1; var2 < var0.length; var2++) {
         var1 = Math.max(var1, var0[var2]);
      }

      float[] var6 = new float[var0.length];
      double var3 = 0.0;

      for (int var5 = 0; var5 < var6.length; var5++) {
         var6[var5] = (float)Math.exp(var0[var5] - var1);
         var3 += var6[var5];
      }

      for (int var7 = 0; var7 < var6.length; var7++) {
         var6[var7] /= (float)var3;
      }

      return var6;
   }

   private static int method6395(float[] var0) {
      int var1 = 0;

      for (int var2 = 1; var2 < var0.length; var2++) {
         if (var0[var2] > var0[var1]) {
            var1 = var2;
         }
      }

      return var1;
   }

   private static float method6396(float[] var0, float[] var1) {
      method6399(var0.length == var1.length, "parity vector length");
      float var2 = 0.0F;

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2 = Math.max(var2, Math.abs(var0[var3] - var1[var3]));
      }

      return var2;
   }

   private static float[] method6397(JsonArray var0) {
      float[] var1 = new float[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0.get(var2).getAsFloat();
      }

      return var1;
   }

   private static float[][] method6398(JsonArray var0) {
      float[][] var1 = new float[var0.size()][];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = method6397(var0.get(var2).getAsJsonArray());
      }

      return var1;
   }

   private static void method6399(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalStateException("invalid human-burst artifact: " + var1);
      }
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data(String var1, String var2, float var3, float var4, float var5) {
         this.text = var1;
         this.text2 = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }

   private static final class Data2 {
      private final float[][] floatValues;
      private final float[][] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;

      public float[][] floatValues() {
         return this.floatValues;
      }

      public float[][] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      public float[] floatValues4() {
         return this.floatValues4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, floatValues2, floatValues3, floatValues4);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + "]";
      }

      private Data2(float[][] var1, float[][] var2, float[] var3, float[] var4) {
         this.floatValues = var1;
         this.floatValues2 = var2;
         this.floatValues3 = var3;
         this.floatValues4 = var4;
      }

      public float[][] getFloatValues() {
         return this.floatValues;
      }

      public float[][] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }

      public float[] getFloatValues4() {
         return this.floatValues4;
      }
   }

   private static final class Data3 {
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;
      private final float[] floatValues5;
      private final float[] floatValues6;
      private final float floatValue;

      public float[] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      public float[] floatValues4() {
         return this.floatValues4;
      }

      public float[] floatValues5() {
         return this.floatValues5;
      }

      public float[] floatValues6() {
         return this.floatValues6;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4) && java.util.Objects.equals(this.floatValues5, other.floatValues5) && java.util.Objects.equals(this.floatValues6, other.floatValues6) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, floatValues2, floatValues3, floatValues4, floatValues5, floatValues6, floatValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + ", " + "floatValues5=" + this.floatValues5 + ", " + "floatValues6=" + this.floatValues6 + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data3(float[] var1, float[] var2, float[] var3, float[] var4, float[] var5, float[] var6, float var7) {
         this.floatValues = var1;
         this.floatValues2 = var2;
         this.floatValues3 = var3;
         this.floatValues4 = var4;
         this.floatValues5 = var5;
         this.floatValues6 = var6;
         this.floatValue = var7;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public float[] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }

      public float[] getFloatValues4() {
         return this.floatValues4;
      }

      public float[] getFloatValues5() {
         return this.floatValues5;
      }

      public float[] getFloatValues6() {
         return this.floatValues6;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }

   public static final class Data4 {
      private final int intValue;
      private final int intValue2;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9);
      }

      @Override
      public String toString() {
         return "Data4[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + "]";
      }

      public Data4(int var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
         this.floatValue4 = var6;
         this.floatValue5 = var7;
         this.floatValue6 = var8;
         this.floatValue7 = var9;
         this.floatValue8 = var10;
         this.floatValue9 = var11;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }
   }

   private static final class Data5 {
      private final String text;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public String text() {
         return this.text;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data5[" + "text=" + this.text + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data5(String var1, float var2, float var3, float var4) {
         this.text = var1;
         this.floatValue = var2;
         this.floatValue2 = var3;
         this.floatValue3 = var4;
      }

      public String getText() {
         return this.text;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }
}

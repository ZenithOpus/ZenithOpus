package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public final class TandemModelData {
   public static final String text = "neuraim_java_contract_tandem_v1";
   public static final String text2 = "neuraim_contract_tandem_v1.json";
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 6;
   private static final int CONST_intValue3 = 14;
   private static final int CONST_intValue4 = 6;
   private static final int CONST_intValue5 = 5;
   private static final float CONST_floatValue = 50.0F;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final int intValue6;
   private final int intValue7;
   private final int intValue8;
   private final int intValue9;
   private final int intValue10;
   private final int intValue11;
   private final int intValue12;
   private final int intValue13;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final int[] intValues;
   private final int[] intValues2;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final float[] floatValues5;
   private final float[] floatValues6;
   private final float[][][] floatValues7;
   private final float[][][] floatValues8;
   private final float[][] floatValues9;
   private final float[][] floatValues10;
   private final float[] floatValues11;
   private final float[] floatValues12;
   private final float[][] floatValues13;
   private final float[][] floatValues14;
   private final float[][] floatValues15;
   private final float[] floatValues16;
   private final float[] floatValues17;
   private final float[] floatValues18;
   private final float[][][] floatValues19;
   private final float[][][] floatValues20;
   private final float[][] floatValues21;
   private final float[][] floatValues22;
   private final float[][] floatValues23;
   private final float[][] floatValues24;
   private final float[][] floatValues25;
   private final float[][] floatValues26;
   private final float[][] floatValues27;
   private final float[][] floatValues28;
   private final float[][] floatValues29;
   private final float[] floatValues30;
   private final float[] floatValues31;
   private final float[] floatValues32;
   private final float[] floatValues33;
   private final float[] floatValues34;
   private final float[] floatValues35;
   private final float[] floatValues36;
   private final float[] floatValues37;
   private final float[] floatValues38;

   private TandemModelData(JsonObject var1) {
      method3188("neuraim_java_contract_tandem_v1".equals(method3182(var1, "format")), "unexpected contract model format");
      JsonObject var2 = method3180(var1, "runtime");
      this.intValue = method3184(var2, "history_ticks");
      this.intValue2 = method3184(var2, "block_ticks");
      this.floatValue = method3183(var2, "shared_noise_scale");
      method3188(this.intValue == 20 && this.intValue2 == 4, "unsupported temporal contract");
      method3188("argmax".equals(method3182(var2, "hit_selection")), "hit mode mismatch");
      method3188("argmax".equals(method3182(var2, "component_selection")), "component mode mismatch");
      JsonObject var3 = method3180(var1, "features");
      this.intValue3 = method3181(var3, "raw_columns").size();
      this.intValue4 = method3181(var3, "planner_columns").size();
      method3188(this.intValue3 == 54 && this.intValue4 == 34, "feature width mismatch");
      this.intValues = method3186(var3, "planner_feature_indices", this.intValue4);
      this.intValues2 = method3186(var3, "motor_feature_indices", 14);
      JsonObject var4 = method3180(var1, "planner");
      JsonObject var5 = method3180(var4, "architecture");
      method3188(method3184(var5, "input_dim") == this.intValue4, "planner input mismatch");
      this.intValue5 = method3184(var5, "hidden_dim");
      this.intValue6 = method3184(var5, "num_layers");
      this.intValue7 = method3184(var5, "mixture_components");
      this.intValue8 = method3184(var5, "residual_rank");
      method3188(this.intValue6 == 2 && this.intValue8 == 2, "unsupported planner architecture");
      JsonObject var6 = method3180(var4, "scales");
      this.floatValues = method3185(var6, "feature_mean", this.intValue4);
      this.floatValues2 = method3185(var6, "feature_scale", this.intValue4);
      this.floatValues3 = method3185(var6, "contract_mean", 6);
      this.floatValues4 = method3185(var6, "contract_scale", 6);
      this.floatValues5 = method3185(var6, "contract_min", 6);
      this.floatValues6 = method3185(var6, "contract_max", 6);
      JsonObject var7 = method3180(var4, "weights");
      this.floatValues7 = new float[this.intValue6][][];
      this.floatValues8 = new float[this.intValue6][][];
      this.floatValues9 = new float[this.intValue6][];
      this.floatValues10 = new float[this.intValue6][];

      for (int var8 = 0; var8 < this.intValue6; var8++) {
         int var9 = var8 == 0 ? this.intValue4 : this.intValue5;
         this.floatValues7[var8] = method3187(var7, "encoder.weight_ih_l" + var8, 3 * this.intValue5, var9);
         this.floatValues8[var8] = method3187(var7, "encoder.weight_hh_l" + var8, 3 * this.intValue5, this.intValue5);
         this.floatValues9[var8] = method3185(var7, "encoder.bias_ih_l" + var8, 3 * this.intValue5);
         this.floatValues10[var8] = method3185(var7, "encoder.bias_hh_l" + var8, 3 * this.intValue5);
      }

      this.floatValues11 = method3185(var7, "context.0.weight", this.intValue5);
      this.floatValues12 = method3185(var7, "context.0.bias", this.intValue5);
      this.floatValues13 = method3187(var7, "context.1.weight", this.intValue5, this.intValue5);
      this.floatValues16 = method3185(var7, "context.1.bias", this.intValue5);
      this.floatValues14 = method3187(var7, "hit_head.weight", 5, this.intValue5);
      this.floatValues17 = method3185(var7, "hit_head.bias", 5);
      int var15 = 7 + 6 * this.intValue8 + 6;
      this.floatValues15 = method3187(var7, "contract_head.weight", 5 * this.intValue7 * var15, this.intValue5);
      this.floatValues18 = method3185(var7, "contract_head.bias", 5 * this.intValue7 * var15);
      JsonObject var16 = method3180(var1, "motor");
      JsonObject var10 = method3180(var16, "architecture");
      this.intValue9 = method3184(var10, "hidden_dim");
      this.intValue10 = method3184(var10, "history_layers");
      this.intValue11 = method3184(var10, "contract_dim");
      this.intValue12 = method3184(var10, "mixture_components");
      this.intValue13 = method3184(var10, "maximum_magnitude");
      method3188(this.intValue10 == 2, "unsupported motor architecture");
      JsonObject var11 = method3180(var16, "action_scales");
      this.floatValue2 = method3183(var11, "yaw");
      this.floatValue3 = method3183(var11, "pitch");
      JsonObject var12 = method3180(var16, "weights");
      this.floatValues19 = new float[this.intValue10][][];
      this.floatValues20 = new float[this.intValue10][][];
      this.floatValues21 = new float[this.intValue10][];
      this.floatValues22 = new float[this.intValue10][];

      for (int var13 = 0; var13 < this.intValue10; var13++) {
         int var14 = var13 == 0 ? 14 : this.intValue9;
         this.floatValues19[var13] = method3187(var12, "history_encoder.weight_ih_l" + var13, 3 * this.intValue9, var14);
         this.floatValues20[var13] = method3187(var12, "history_encoder.weight_hh_l" + var13, 3 * this.intValue9, this.intValue9);
         this.floatValues21[var13] = method3185(var12, "history_encoder.bias_ih_l" + var13, 3 * this.intValue9);
         this.floatValues22[var13] = method3185(var12, "history_encoder.bias_hh_l" + var13, 3 * this.intValue9);
      }

      this.floatValues23 = method3187(var12, "initial_hidden.0.weight", this.intValue9, this.intValue9 + 12);
      this.floatValues30 = method3185(var12, "initial_hidden.0.bias", this.intValue9);
      this.floatValues24 = method3187(var12, "contract_encoder.0.weight", this.intValue11, 8);
      this.floatValues31 = method3185(var12, "contract_encoder.0.bias", this.intValue11);
      this.floatValues25 = method3187(var12, "contract_encoder.2.weight", this.intValue11, this.intValue11);
      this.floatValues32 = method3185(var12, "contract_encoder.2.bias", this.intValue11);
      this.floatValues33 = method3185(var12, "contract_encoder.4.weight", this.intValue11);
      this.floatValues34 = method3185(var12, "contract_encoder.4.bias", this.intValue11);
      this.floatValues26 = method3187(var12, "decoder.weight_ih", 3 * this.intValue9, 21 + this.intValue11);
      this.floatValues27 = method3187(var12, "decoder.weight_hh", 3 * this.intValue9, this.intValue9);
      this.floatValues35 = method3185(var12, "decoder.bias_ih", 3 * this.intValue9);
      this.floatValues36 = method3185(var12, "decoder.bias_hh", 3 * this.intValue9);
      this.floatValues28 = method3187(var12, "mode_head.weight", 10, this.intValue9);
      this.floatValues37 = method3185(var12, "mode_head.bias", 10);
      this.floatValues29 = method3187(var12, "mixture_head.weight", 2 * this.intValue12 * 3, this.intValue9);
      this.floatValues38 = method3185(var12, "mixture_head.bias", 2 * this.intValue12 * 3);
   }

   public static TandemModelData method3152(Path var0) throws IOException {
      TandemModelData var2;
      try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         var2 = method3154(var1);
      }

      return var2;
   }

   public static TandemModelData method3153(InputStream var0) throws IOException {
      TandemModelData var2;
      try (InputStreamReader var1 = new InputStreamReader(var0, StandardCharsets.UTF_8)) {
         var2 = method3154(var1);
      }

      return var2;
   }

   private static TandemModelData method3154(Reader var0) throws IOException {
      try {
         JsonObject var1 = (JsonObject)new Gson().fromJson(var0, JsonObject.class);
         method3188(var1 != null, "empty model JSON");
         return new TandemModelData(var1);
      } catch (RuntimeException var2) {
         throw new IOException("invalid Neuraim contract model: " + var2.getMessage(), var2);
      }
   }

   public static TandemModelData method3155() {
      for (Path var1 : List.of(
         Path.of("models", "neuraim_contract_tandem_v1.json"),
         Path.of("run", "models", "neuraim_contract_tandem_v1.json"),
         Path.of("neuraim_contract_tandem_v1.json"),
         Path.of("..", "models", "neuraim_contract_tandem_v1.json")
      )) {
         if (Files.isRegularFile(var1)) {
            try {
               TandemModelData var2 = method3152(var1);
               System.out.println("[Neuraim] Loaded contract tandem " + var1.toAbsolutePath());
               return var2;
            } catch (IOException var4) {
               System.err.println("[Neuraim] Failed to load " + var1 + ": " + var4.getMessage());
            }
         }
      }

      InputStream var5 = TandemModelData.class.getClassLoader().getResourceAsStream("models/neuraim_contract_tandem_v1.json");
      if (var5 != null) {
         try {
            return method3153(var5);
         } catch (IOException var3) {
            System.err.println("[Neuraim] Bundled contract tandem is invalid: " + var3.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_contract_tandem_v1.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public TandemModelData.Entry2 method3156(float[][] var1) {
      this.method3166(var1);
      float[][] var2 = new float[this.intValue10][this.intValue9];

      for (float[] var6 : var1) {
         float[] var7 = this.method3162(var6);

         for (int var8 = 0; var8 < this.intValue10; var8++) {
            var2[var8] = method3167(
               var7, var2[var8], this.floatValues19[var8], this.floatValues20[var8], this.floatValues21[var8], this.floatValues22[var8], this.intValue9
            );
            var7 = var2[var8];
         }
      }

      float[][] var9 = this.method3163(var1);
      float[] var10 = method3176(method3168(this.floatValues23, this.floatValues30, method3177(var2[this.intValue10 - 1], this.method3164(var9))));
      return new TandemModelData.Entry2(var10, var9);
   }

   public TandemModelData.Entry method3157(TandemModelData.Entry2 var1, float[][] var2, Random var3) {
      method3188(var3 != null, "random is required");
      return this.method3158(var1, var2, new float[]{(float)var3.nextGaussian(), (float)var3.nextGaussian()});
   }

   public TandemModelData.Entry method3158(TandemModelData.Entry2 var1, float[][] var2, float[] var3) {
      method3188(var1 != null, "session is required");
      this.method3166(var2);
      method3188(var3 != null && var3.length == this.intValue8, "noise width mismatch");
      TandemModelData.Entry3 var4 = this.method3159(var2, var3);
      float[] var5 = this.method3160(var4);
      float[] var6 = (float[])var4.floatValues.clone();
      float[] var7 = (float[])var4.floatValues3.clone();
      int[][] var8 = new int[this.intValue2][2];

      for (int var9 = 0; var9 < this.intValue2; var9++) {
         float[] var10 = this.method3161(var1, var4, var5, var6, var7, var9);
         var1.floatValues = method3167(var10, var1.floatValues, this.floatValues26, this.floatValues27, this.floatValues35, this.floatValues36, this.intValue9);
         float[] var11 = method3168(this.floatValues28, this.floatValues37, var1.floatValues);
         float[] var12 = method3168(this.floatValues29, this.floatValues38, var1.floatValues);

         for (int var13 = 0; var13 < 2; var13++) {
            int var14 = method3170(var11, var13 * 5, 5);
            int var15 = method3171(var12, var13 * this.intValue12 * 3, this.intValue12, 3);
            float var16 = method3174(var12[var13 * this.intValue12 * 3 + var15 * 3 + 1]);
            var16 = Math.min(var16, (float)Math.log(this.intValue13));
            int var17 = method3179(method3178(Math.exp(var16)), 1, this.intValue13);
            var8[var9][var13] = method3172(var14, var1.floatValues2[var13][4]) * var17;
         }

         method3165(var1.floatValues2, var8[var9]);

         for (int var18 = 0; var18 < 2; var18++) {
            var6[var18] -= var8[var9][var18];
            var7[var18] = var4.flag && var9 <= var4.intValue ? var7[var18] - var8[var9][var18] : 0.0F;
         }
      }

      return new TandemModelData.Entry(var4, var8);
   }

   private TandemModelData.Entry3 method3159(float[][] var1, float[] var2) {
      float[][] var3 = new float[this.intValue6][this.intValue5];

      for (float[] var7 : var1) {
         float[] var8 = new float[this.intValue4];

         for (int var9 = 0; var9 < this.intValue4; var9++) {
            var8[var9] = (var7[this.intValues[var9]] - this.floatValues[var9]) / this.floatValues2[var9];
         }

         for (int var22 = 0; var22 < this.intValue6; var22++) {
            var3[var22] = method3167(
               var8, var3[var22], this.floatValues7[var22], this.floatValues8[var22], this.floatValues9[var22], this.floatValues10[var22], this.intValue5
            );
            var8 = var3[var22];
         }
      }

      float[] var17 = method3175(
         method3168(this.floatValues13, this.floatValues16, method3169(var3[this.intValue6 - 1], this.floatValues11, this.floatValues12))
      );
      int var18 = method3170(method3168(this.floatValues14, this.floatValues17, var17), 0, 5);
      float[] var19 = method3168(this.floatValues15, this.floatValues18, var17);
      int var20 = 7 + 6 * this.intValue8 + 6;
      int var21 = var18 * this.intValue7 * var20;
      int var23 = 0;
      float var10 = -Float.MAX_VALUE;

      for (int var11 = 0; var11 < this.intValue7; var11++) {
         float var12 = var19[var21 + var11 * var20];
         if (var12 > var10) {
            var10 = var12;
            var23 = var11;
         }
      }

      int var24 = var21 + var23 * var20;
      float[] var25 = new float[6];

      for (int var13 = 0; var13 < 6; var13++) {
         float var14 = var19[var24 + 1 + var13];

         for (int var15 = 0; var15 < this.intValue8; var15++) {
            float var16 = 0.9F * (float)Math.tanh(var19[var24 + 1 + 6 + var13 * this.intValue8 + var15]);
            var14 += this.floatValue * var16 * var2[var15];
         }

         float var27 = var14 * this.floatValues4[var13] + this.floatValues3[var13];
         var27 = Math.max(this.floatValues5[var13], Math.min(this.floatValues6[var13], var27));
         var25[var13] = method3178(Math.copySign(Math.expm1(Math.abs(var27)), (double)var27));
      }

      boolean var26 = var18 != this.intValue2;
      return new TandemModelData.Entry3(
         var18, var23, var26, new float[]{var25[0], var25[1]}, new float[]{var25[2], var25[3]}, var26 ? new float[]{var25[4], var25[5]} : new float[2]
      );
   }

   private float[] method3160(TandemModelData.Entry3 var1) {
      float[] var2 = new float[]{this.floatValue2, this.floatValue3};
      float[] var3 = new float[8];

      for (int var4 = 0; var4 < 2; var4++) {
         var3[var4] = var1.floatValues[var4] / (var2[var4] * this.intValue2);
         var3[2 + var4] = var1.floatValues2[var4] / var2[var4];
         var3[4 + var4] = var1.floatValues3[var4] / (var2[var4] * this.intValue2);
      }

      var3[6] = (float)var1.intValue / this.intValue2;
      var3[7] = var1.flag ? 1.0F : 0.0F;
      float[] var6 = method3175(method3168(this.floatValues24, this.floatValues31, var3));
      float[] var5 = method3175(method3168(this.floatValues25, this.floatValues32, var6));
      return method3169(var5, this.floatValues33, this.floatValues34);
   }

   private float[] method3161(TandemModelData.Entry2 var1, TandemModelData.Entry3 var2, float[] var3, float[] var4, float[] var5, int var6) {
      float[] var7 = new float[]{this.floatValue2, this.floatValue3};
      float[] var8 = new float[2];
      float[] var9 = new float[2];
      float[] var10 = new float[2];

      for (int var11 = 0; var11 < 2; var11++) {
         var8[var11] = var1.floatValues2[var11][0] / var7[var11];
         var9[var11] = var4[var11] / (var7[var11] * this.intValue2);
         var10[var11] = var5[var11] / (var7[var11] * this.intValue2);
      }

      float var13 = (float)(this.intValue2 - var6) / this.intValue2;
      float var12 = Math.max(0.0F, Math.min((float)this.intValue2, var2.intValue - var6 + 1.0F)) / this.intValue2;
      return method3177(var8, this.method3164(var1.floatValues2), var9, var10, new float[]{var13, var12, var2.flag ? 1.0F : 0.0F}, var3);
   }

   private float[] method3162(float[] var1) {
      float[] var2 = new float[]{
         this.floatValue2,
         this.floatValue3,
         this.floatValue2,
         this.floatValue3,
         this.floatValue2,
         this.floatValue3,
         50.0F,
         50.0F,
         50.0F,
         50.0F,
         1.0F,
         1.0F,
         this.floatValue2,
         this.floatValue3
      };
      float[] var3 = new float[14];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = var1[this.intValues2[var4]] / var2[var4];
      }

      return var3;
   }

   private float[][] method3163(float[][] var1) {
      float[] var2 = var1[var1.length - 1];
      float[][] var3 = new float[2][6];
      int[] var4 = new int[]{27, 28};
      int[] var5 = new int[]{29, 30};
      int[] var6 = new int[]{33, 34};
      int[] var7 = new int[]{35, 36};
      int[] var8 = new int[]{41, 42};

      for (int var9 = 0; var9 < 2; var9++) {
         float var10 = 0.0F;

         for (int var11 = var1.length - 1; var11 >= 0; var11--) {
            if (var1[var11][var4[var9]] != 0.0F) {
               var10 = Math.signum(var1[var11][var4[var9]]);
               break;
            }
         }

         var3[var9][0] = var2[var4[var9]];
         var3[var9][1] = var2[var5[var9]];
         var3[var9][2] = var2[var6[var9]];
         var3[var9][3] = var10 == 0.0F ? 50.0F : var2[var7[var9]];
         var3[var9][4] = var10;
         var3[var9][5] = var2[var8[var9]];
      }

      return var3;
   }

   private float[] method3164(float[][] var1) {
      float[] var2 = new float[]{
         this.floatValue2, this.floatValue2, 50.0F, 50.0F, 1.0F, this.floatValue2, this.floatValue3, this.floatValue3, 50.0F, 50.0F, 1.0F, this.floatValue3
      };
      float[] var3 = new float[12];
      int var4 = 0;

      for (float[] var8 : var1) {
         for (float var12 : var8) {
            var3[var4] = var12 / var2[var4++];
         }
      }

      return var3;
   }

   private static void method3165(float[][] var0, int[] var1) {
      for (int var2 = 0; var2 < 2; var2++) {
         float var3 = var0[var2][0];
         float var4 = var0[var2][1];
         float var5 = Math.signum((float)var1[var2]);
         boolean var6 = var5 != 0.0F;
         boolean var7 = var6 && var0[var2][4] != 0.0F && var5 != var0[var2][4];
         float var8 = var1[var2] - var3;
         float var9 = var6 ? var0[var2][2] + 1.0F : 0.0F;
         float var10 = var7 ? 0.0F : Math.min(50.0F, var0[var2][3] + 1.0F);
         boolean var11 = var0[var2][4] == 0.0F || var5 != var0[var2][4];
         float var12 = !var6 ? 0.0F : (var11 ? Math.abs(var1[var2]) : Math.max(var0[var2][5], (float)Math.abs(var1[var2])));
         var0[var2][0] = var1[var2];
         var0[var2][1] = var8;
         var0[var2][2] = var9;
         var0[var2][3] = var10;
         if (var6) {
            var0[var2][4] = var5;
         }

         var0[var2][5] = var12;
         if (!Float.isFinite(var4)) {
            throw new IllegalStateException("invalid motor state");
         }
      }
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue3() {
      return this.intValue3;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   private void method3166(float[][] var1) {
      method3188(var1 != null && var1.length == this.intValue, "history tick count mismatch");

      for (float[] var5 : var1) {
         method3188(var5 != null && var5.length == this.intValue3, "history feature width mismatch");

         for (float var9 : var5) {
            method3188(Float.isFinite(var9), "non-finite history feature");
         }
      }
   }

   private static float[] method3167(float[] var0, float[] var1, float[][] var2, float[][] var3, float[] var4, float[] var5, int var6) {
      float[] var7 = method3168(var2, var4, var0);
      float[] var8 = method3168(var3, var5, var1);
      float[] var9 = new float[var6];

      for (int var10 = 0; var10 < var6; var10++) {
         float var11 = method3173(var7[var10] + var8[var10]);
         float var12 = method3173(var7[var6 + var10] + var8[var6 + var10]);
         float var13 = (float)Math.tanh(var7[2 * var6 + var10] + var11 * var8[2 * var6 + var10]);
         var9[var10] = (1.0F - var12) * var13 + var12 * var1[var10];
      }

      return var9;
   }

   private static float[] method3168(float[][] var0, float[] var1, float[] var2) {
      float[] var3 = new float[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         float var5 = var1[var4];

         for (int var6 = 0; var6 < var2.length; var6++) {
            var5 += var0[var4][var6] * var2[var6];
         }

         var3[var4] = var5;
      }

      return var3;
   }

   private static float[] method3169(float[] var0, float[] var1, float[] var2) {
      float var3 = 0.0F;

      for (float var7 : var0) {
         var3 += var7;
      }

      var3 /= var0.length;
      float var11 = 0.0F;

      for (float var8 : var0) {
         float var9 = var8 - var3;
         var11 += var9 * var9;
      }

      var11 /= var0.length;
      float var14 = (float)(1.0 / Math.sqrt(var11 + 1.0E-5));
      float[] var16 = new float[var0.length];

      for (int var18 = 0; var18 < var16.length; var18++) {
         var16[var18] = (var0[var18] - var3) * var14 * var1[var18] + var2[var18];
      }

      return var16;
   }

   private static int method3170(float[] var0, int var1, int var2) {
      int var3 = 0;

      for (int var4 = 1; var4 < var2; var4++) {
         if (var0[var1 + var4] > var0[var1 + var3]) {
            var3 = var4;
         }
      }

      return var3;
   }

   private static int method3171(float[] var0, int var1, int var2, int var3) {
      int var4 = 0;

      for (int var5 = 1; var5 < var2; var5++) {
         if (var0[var1 + var5 * var3] > var0[var1 + var4 * var3]) {
            var4 = var5;
         }
      }

      return var4;
   }

   private static int method3172(int var0, float var1) {
      int var2 = var1 == 0.0F ? 1 : (var1 > 0.0F ? 1 : -1);

      return switch (var0) {
         case 1 -> var2;
         case 2 -> -var2;
         case 3 -> 1;
         case 4 -> -1;
         default -> 0;
      };
   }

   private static float method3173(float var0) {
      return (float)(1.0 / (1.0 + Math.exp(-var0)));
   }

   private static float method3174(float var0) {
      return var0 > 20.0F ? var0 : (float)Math.log1p(Math.exp(var0));
   }

   private static float[] method3175(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] *= method3173(var0[var1]);
      }

      return var0;
   }

   private static float[] method3176(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = (float)Math.tanh(var0[var1]);
      }

      return var0;
   }

   private static float[] method3177(float[]... var0) {
      int var1 = 0;

      for (float[] var5 : var0) {
         var1 += var5.length;
      }

      float[] var8 = new float[var1];
      int var9 = 0;

      for (float[] var7 : var0) {
         System.arraycopy(var7, 0, var8, var9, var7.length);
         var9 += var7.length;
      }

      return var8;
   }

   private static int method3178(double var0) {
      return !Double.isFinite(var0) ? 0 : (int)Math.rint(var0);
   }

   private static int method3179(int var0, int var1, int var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static JsonObject method3180(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method3188(var2 != null && var2.isJsonObject(), "missing object " + var1);
      return var2.getAsJsonObject();
   }

   private static JsonArray method3181(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method3188(var2 != null && var2.isJsonArray(), "missing array " + var1);
      return var2.getAsJsonArray();
   }

   private static String method3182(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method3188(var2 != null && var2.isJsonPrimitive(), "missing string " + var1);
      return var2.getAsString();
   }

   private static float method3183(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method3188(var2 != null && var2.isJsonPrimitive(), "missing number " + var1);
      float var3 = var2.getAsFloat();
      method3188(Float.isFinite(var3), "non-finite " + var1);
      return var3;
   }

   private static int method3184(JsonObject var0, String var1) {
      return Math.round(method3183(var0, var1));
   }

   private static float[] method3185(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method3181(var0, var1);
      method3188(var3.size() == var2, var1 + " length mismatch");
      float[] var4 = new float[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsFloat();
      }

      return var4;
   }

   private static int[] method3186(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method3181(var0, var1);
      method3188(var3.size() == var2, var1 + " length mismatch");
      int[] var4 = new int[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsInt();
      }

      return var4;
   }

   private static float[][] method3187(JsonObject var0, String var1, int var2, int var3) {
      JsonArray var4 = method3181(var0, var1);
      method3188(var4.size() == var2, var1 + " rows mismatch");
      float[][] var5 = new float[var2][var3];

      for (int var6 = 0; var6 < var2; var6++) {
         JsonArray var7 = var4.get(var6).getAsJsonArray();
         method3188(var7.size() == var3, var1 + " columns mismatch");

         for (int var8 = 0; var8 < var3; var8++) {
            var5[var6][var8] = var7.get(var8).getAsFloat();
         }
      }

      return var5;
   }

   private static void method3188(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalArgumentException(var1);
      }
   }

   public static final class Entry {
      public final TandemModelData.Entry3 entry3;
      public final int[][] intValues;

      private Entry(TandemModelData.Entry3 var1, int[][] var2) {
         this.entry3 = var1;
         this.intValues = var2;
      }
   }

   public static final class Entry2 {
      private float[] floatValues;
      private final float[][] floatValues2;

      private Entry2(float[] var1, float[][] var2) {
         this.floatValues = var1;
         this.floatValues2 = var2;
      }
   }

   public static final class Entry3 {
      public final int intValue;
      public final int intValue2;
      public final boolean flag;
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float[] floatValues3;

      private Entry3(int var1, int var2, boolean var3, float[] var4, float[] var5, float[] var6) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.flag = var3;
         this.floatValues = var4;
         this.floatValues2 = var5;
         this.floatValues3 = var6;
      }
   }
}

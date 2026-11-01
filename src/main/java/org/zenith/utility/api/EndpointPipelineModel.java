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

public final class EndpointPipelineModel {
   public static final String text = "neuraim_java_endpoint_pipeline_v1";
   public static final String text2 = "neuraim_endpoint_pipeline_v1.json";
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 14;
   private static final int CONST_intValue3 = 6;
   private static final float CONST_floatValue = 50.0F;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final int intValue6;
   private final int intValue7;
   private final float floatValue;
   private final float floatValue2;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final int[] intValues;
   private final float[][] floatValues5;
   private final float[][] floatValues6;
   private final float[][] floatValues7;
   private final float[] floatValues8;
   private final float[] floatValues9;
   private final float[] floatValues10;
   private final float[][] floatValues11;
   private final float[][] floatValues12;
   private final float[][] floatValues13;
   private final float[] floatValues14;
   private final float[] floatValues15;
   private final float[] floatValues16;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private final float floatValue6;
   private final float[][] floatValues17;
   private final float[][] floatValues18;
   private final float[][] floatValues19;
   private final float[] floatValues20;
   private final float[] floatValues21;
   private final float[] floatValues22;
   private final float[][] floatValues23;
   private final float[][] floatValues24;
   private final float[] floatValues25;
   private final float[] floatValues26;
   private final float[][] floatValues27;
   private final float[][] floatValues28;
   private final float[][] floatValues29;
   private final float[] floatValues30;
   private final float[] floatValues31;
   private final float[] floatValues32;

   private EndpointPipelineModel(JsonObject var1) {
      method5711("neuraim_java_endpoint_pipeline_v1".equals(method5705(var1, "format")), "unexpected endpoint model format");
      JsonObject var2 = method5703(var1, "runtime");
      this.intValue = method5707(var2, "history_ticks");
      this.intValue2 = method5707(var2, "endpoint_horizon_ticks");
      this.intValue3 = method5707(var2, "planner_noise_refresh_ticks");
      this.floatValue = method5706(var2, "motor_magnitude_std_fraction");
      method5711(this.intValue == 20 && this.intValue2 == 5, "unsupported temporal contract");
      JsonObject var3 = method5703(var1, "features");
      JsonArray var4 = method5704(var3, "columns");
      this.intValue4 = var4.size();
      method5711(this.intValue4 == 55, "closed-loop planner requires 55 features");
      method5711("movement_input_jump".equals(var4.get(47).getAsString()), "planner feature order mismatch at movement_input_jump");
      this.intValues = method5709(var3, "motor_feature_indices", 14);
      JsonObject var5 = method5703(var1, "planner");
      JsonObject var6 = method5703(var5, "architecture");
      method5711(method5707(var6, "input_dim") == this.intValue4, "planner input width mismatch");
      this.intValue5 = method5707(var6, "hidden_dim");
      method5711(method5707(var6, "num_layers") == 1, "only one planner GRU layer is supported");
      JsonObject var7 = method5703(var5, "scales");
      this.floatValues = method5708(var7, "feature_mean", this.intValue4);
      this.floatValues2 = method5708(var7, "feature_scale", this.intValue4);
      this.floatValues3 = method5708(var7, "log_magnitude_mean", 2);
      this.floatValues4 = method5708(var7, "log_magnitude_scale", 2);

      for (float var11 : this.floatValues2) {
         method5711(var11 > 0.0F, "invalid feature scale");
      }

      JsonObject var13 = method5703(var5, "weights");
      this.floatValues5 = method5710(var13, "encoder.weight_ih_l0", 3 * this.intValue5, this.intValue4);
      this.floatValues6 = method5710(var13, "encoder.weight_hh_l0", 3 * this.intValue5, this.intValue5);
      this.floatValues8 = method5708(var13, "encoder.bias_ih_l0", 3 * this.intValue5);
      this.floatValues9 = method5708(var13, "encoder.bias_hh_l0", 3 * this.intValue5);
      this.floatValues7 = method5710(var13, "context.0.weight", this.intValue5, this.intValue5);
      this.floatValues10 = method5708(var13, "context.0.bias", this.intValue5);
      this.floatValues11 = method5710(var13, "mode_head.weight", 4, this.intValue5);
      this.floatValues14 = method5708(var13, "mode_head.bias", 4);
      this.floatValues12 = method5710(var13, "sign_head.weight", 2, this.intValue5);
      this.floatValues15 = method5708(var13, "sign_head.bias", 2);
      this.floatValues13 = method5710(var13, "magnitude_head.weight", 4, this.intValue5);
      this.floatValues16 = method5708(var13, "magnitude_head.bias", 4);
      JsonObject var14 = method5703(var1, "motor");
      JsonObject var15 = method5703(var14, "architecture");
      this.intValue6 = method5707(var15, "hidden_dim");
      this.intValue7 = method5707(var15, "style_dim");
      this.floatValue2 = method5706(var15, "sign_prior_strength");
      method5711(method5707(var15, "history_ticks") == this.intValue, "motor history mismatch");
      JsonObject var16 = method5703(var14, "scales");
      this.floatValue3 = method5706(var16, "action_yaw");
      this.floatValue4 = method5706(var16, "action_pitch");
      this.floatValue5 = method5706(var16, "endpoint_yaw");
      this.floatValue6 = method5706(var16, "endpoint_pitch");
      JsonObject var12 = method5703(var14, "weights");
      this.floatValues17 = method5710(var12, "history_encoder.weight_ih_l0", 3 * this.intValue6, 14);
      this.floatValues18 = method5710(var12, "history_encoder.weight_hh_l0", 3 * this.intValue6, this.intValue6);
      this.floatValues20 = method5708(var12, "history_encoder.bias_ih_l0", 3 * this.intValue6);
      this.floatValues21 = method5708(var12, "history_encoder.bias_hh_l0", 3 * this.intValue6);
      this.floatValues19 = method5710(var12, "context.0.weight", this.intValue6, this.intValue6 + 2 + this.intValue7 + 12);
      this.floatValues22 = method5708(var12, "context.0.bias", this.intValue6);
      this.floatValues23 = method5710(var12, "decoder.weight_ih", 3 * this.intValue6, 17);
      this.floatValues24 = method5710(var12, "decoder.weight_hh", 3 * this.intValue6, this.intValue6);
      this.floatValues25 = method5708(var12, "decoder.bias_ih", 3 * this.intValue6);
      this.floatValues26 = method5708(var12, "decoder.bias_hh", 3 * this.intValue6);
      this.floatValues27 = method5710(var12, "active_head.weight", 2, this.intValue6);
      this.floatValues30 = method5708(var12, "active_head.bias", 2);
      this.floatValues28 = method5710(var12, "sign_head.weight", 2, this.intValue6);
      this.floatValues31 = method5708(var12, "sign_head.bias", 2);
      this.floatValues29 = method5710(var12, "magnitude_head.weight", 2, this.intValue6);
      this.floatValues32 = method5708(var12, "magnitude_head.bias", 2);
   }

   public static EndpointPipelineModel method5681(Path var0) throws IOException {
      EndpointPipelineModel var2;
      try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         var2 = method5683(var1);
      }

      return var2;
   }

   public static EndpointPipelineModel method5682(InputStream var0) throws IOException {
      EndpointPipelineModel var2;
      try (InputStreamReader var1 = new InputStreamReader(var0, StandardCharsets.UTF_8)) {
         var2 = method5683(var1);
      }

      return var2;
   }

   private static EndpointPipelineModel method5683(Reader var0) throws IOException {
      try {
         JsonObject var1 = (JsonObject)new Gson().fromJson(var0, JsonObject.class);
         method5711(var1 != null, "empty endpoint model JSON");
         return new EndpointPipelineModel(var1);
      } catch (RuntimeException var2) {
         throw new IOException("invalid Neuraim endpoint model: " + var2.getMessage(), var2);
      }
   }

   public static EndpointPipelineModel method5684() {
      for (Path var1 : List.of(
         Path.of("models", "neuraim_endpoint_pipeline_v1.json"),
         Path.of("run", "models", "neuraim_endpoint_pipeline_v1.json"),
         Path.of("neuraim_endpoint_pipeline_v1.json"),
         Path.of("..", "models", "neuraim_endpoint_pipeline_v1.json")
      )) {
         if (Files.isRegularFile(var1)) {
            try {
               EndpointPipelineModel var2 = method5681(var1);
               System.out.println("[Neuraim] Loaded endpoint pipeline " + var1.toAbsolutePath());
               return var2;
            } catch (IOException var4) {
               System.err.println("[Neuraim] Failed to load " + var1 + ": " + var4.getMessage());
            }
         }
      }

      InputStream var5 = EndpointPipelineModel.class.getClassLoader().getResourceAsStream("models/neuraim_endpoint_pipeline_v1.json");
      if (var5 != null) {
         try {
            return method5682(var5);
         } catch (IOException var3) {
            System.err.println("[Neuraim] Bundled endpoint pipeline is invalid: " + var3.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_endpoint_pipeline_v1.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public int[] method5685(float[][] var1, float[] var2) {
      this.method5691(var1);
      method5711(var2 != null && var2.length == 5, "planner noise width mismatch");
      float[] var3 = new float[this.intValue5];

      for (float[] var7 : var1) {
         float[] var8 = new float[this.intValue4];

         for (int var9 = 0; var9 < this.intValue4; var9++) {
            var8[var9] = (var7[var9] - this.floatValues[var9]) / this.floatValues2[var9];
         }

         var3 = method5692(var8, var3, this.floatValues5, this.floatValues6, this.floatValues8, this.floatValues9, this.intValue5);
      }

      float[] var19 = method5698(method5693(this.floatValues7, this.floatValues10, var3));
      float[] var20 = method5693(this.floatValues11, this.floatValues14, var19);
      float[] var21 = method5693(this.floatValues12, this.floatValues15, var19);
      float[] var22 = method5693(this.floatValues13, this.floatValues16, var19);
      float var23 = method5695(var2[0]);
      float[] var24 = method5694(var20);
      int var10 = 3;
      float var11 = 0.0F;

      for (int var12 = 0; var12 < var24.length; var12++) {
         var11 += var24[var12];
         if (var23 <= var11) {
            var10 = var12;
            break;
         }
      }

      int[] var25 = new int[2];

      for (int var13 = 0; var13 < 2; var13++) {
         if ((var10 & 1 << var13) != 0) {
            float var14 = method5695(var2[1 + var13]) < method5697(var21[var13]) ? 1.0F : -1.0F;
            float var15 = -3.0F + 4.5F * method5697(var22[2 + var13]);
            float var16 = var22[var13] + (float)Math.exp(var15) * var2[3 + var13];
            float var17 = var16 * this.floatValues4[var13] + this.floatValues3[var13];
            int var18 = method5702(Math.max(1.0, Math.expm1(var17)));
            var25[var13] = var14 > 0.0F ? var18 : -var18;
         }
      }

      return var25;
   }

   public int[] method5686(float[][] var1, int[] var2, float[] var3, Random var4) {
      method5711(var4 != null, "random is required");
      float[] var5 = new float[2];
      float[] var6 = new float[2];
      float[] var7 = new float[2];

      for (int var8 = 0; var8 < 2; var8++) {
         var5[var8] = var4.nextFloat();
         var6[var8] = var4.nextFloat();
         var7[var8] = (float)var4.nextGaussian();
      }

      return this.method5687(var1, var2, var3, var5, var6, var7);
   }

   public int[] method5687(float[][] var1, int[] var2, float[] var3, float[] var4, float[] var5, float[] var6) {
      this.method5691(var1);
      method5711(var2 != null && var2.length == 2, "endpoint width mismatch");
      method5711(var3 != null && var3.length == this.intValue7, "motor style width mismatch");
      method5711(var4 != null && var4.length == 2, "active noise width mismatch");
      method5711(var5 != null && var5.length == 2, "sign noise width mismatch");
      method5711(var6 != null && var6.length == 2, "magnitude noise width mismatch");
      float[] var7 = new float[]{
         this.floatValue3,
         this.floatValue4,
         this.floatValue3,
         this.floatValue4,
         this.floatValue3,
         this.floatValue4,
         50.0F,
         50.0F,
         50.0F,
         50.0F,
         1.0F,
         1.0F,
         this.floatValue3,
         this.floatValue4
      };
      float[] var8 = new float[this.intValue6];

      for (float[] var12 : var1) {
         float[] var13 = new float[14];

         for (int var14 = 0; var14 < var13.length; var14++) {
            var13[var14] = var12[this.intValues[var14]] / var7[var14];
         }

         var8 = method5692(var13, var8, this.floatValues17, this.floatValues18, this.floatValues20, this.floatValues21, this.intValue6);
      }

      float[][] var32 = this.method5688(var1);
      float[] var33 = new float[]{
         this.floatValue3, this.floatValue3, 50.0F, 50.0F, 1.0F, this.floatValue3, this.floatValue4, this.floatValue4, 50.0F, 50.0F, 1.0F, this.floatValue4
      };
      float[] var34 = new float[12];
      int var35 = 0;

      for (int var36 = 0; var36 < 2; var36++) {
         for (int var38 = 0; var38 < 6; var38++) {
            var34[var35] = var32[var36][var38] / var33[var35++];
         }
      }

      float[] var37 = new float[]{var2[0] / this.floatValue5, var2[1] / this.floatValue6};
      float[] var39 = method5700(var8, var37, var3, var34);
      var8 = method5699(method5693(this.floatValues19, this.floatValues22, var39));
      float[] var15 = var1[var1.length - 1];
      float[] var16 = method5700(var37, var34, new float[]{var15[27] / this.floatValue3, var15[28] / this.floatValue4}, new float[]{0.2F});
      var8 = method5692(var16, var8, this.floatValues23, this.floatValues24, this.floatValues25, this.floatValues26, this.intValue6);
      float[] var17 = method5693(this.floatValues27, this.floatValues30, var8);
      float[] var18 = method5693(this.floatValues28, this.floatValues31, var8);
      float[] var19 = method5693(this.floatValues29, this.floatValues32, var8);
      float[] var20 = new float[]{this.floatValue3, this.floatValue4};
      int[] var21 = new int[2];

      for (int var22 = 0; var22 < 2; var22++) {
         if (!(var4[var22] >= method5697(var17[var22]))) {
            float var23 = Math.signum((float)var2[var22]);
            boolean var24 = var5[var22] < method5697(var18[var22] + this.floatValue2 * var23);
            double var25 = Math.max(1.0, Math.abs(var2[var22]) / 5.0 + Math.tanh(var19[var22]) * var20[var22] * 0.5);
            double var27 = var25 + var20[var22] * this.floatValue * var6[var22];
            int var29 = Math.max(1, method5702(var27));
            var21[var22] = var24 ? var29 : -var29;
         }
      }

      return var21;
   }

   private float[][] method5688(float[][] var1) {
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
            float var12 = var1[var11][var4[var9]];
            if (var12 != 0.0F) {
               var10 = Math.signum(var12);
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

   public float[] method5689(Random var1) {
      return method5701(var1, 5);
   }

   public float[] method5690(Random var1) {
      return method5701(var1, this.intValue7);
   }

   public int getIntValue4() {
      return this.intValue4;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue3() {
      return this.intValue3;
   }

   private void method5691(float[][] var1) {
      method5711(var1 != null && var1.length == this.intValue, "history tick count mismatch");

      for (float[] var5 : var1) {
         method5711(var5 != null && var5.length == this.intValue4, "history feature width mismatch");

         for (float var9 : var5) {
            method5711(Float.isFinite(var9), "non-finite history feature");
         }
      }
   }

   private static float[] method5692(float[] var0, float[] var1, float[][] var2, float[][] var3, float[] var4, float[] var5, int var6) {
      float[] var7 = method5693(var2, var4, var0);
      float[] var8 = method5693(var3, var5, var1);
      float[] var9 = new float[var6];

      for (int var10 = 0; var10 < var6; var10++) {
         float var11 = method5697(var7[var10] + var8[var10]);
         float var12 = method5697(var7[var6 + var10] + var8[var6 + var10]);
         float var13 = (float)Math.tanh(var7[2 * var6 + var10] + var11 * var8[2 * var6 + var10]);
         var9[var10] = (1.0F - var12) * var13 + var12 * var1[var10];
      }

      return var9;
   }

   private static float[] method5693(float[][] var0, float[] var1, float[] var2) {
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

   private static float[] method5694(float[] var0) {
      float var1 = -Float.MAX_VALUE;

      for (float var5 : var0) {
         var1 = Math.max(var1, var5);
      }

      float[] var6 = new float[var0.length];
      float var7 = 0.0F;

      for (int var8 = 0; var8 < var0.length; var8++) {
         var6[var8] = (float)Math.exp(var0[var8] - var1);
         var7 += var6[var8];
      }

      for (int var9 = 0; var9 < var6.length; var9++) {
         var6[var9] /= var7;
      }

      return var6;
   }

   private static float method5695(float var0) {
      return Math.max(1.0E-7F, Math.min(0.9999999F, 0.5F * (1.0F + method5696(var0 * 0.70710677F))));
   }

   private static float method5696(float var0) {
      float var1 = var0 < 0.0F ? -1.0F : 1.0F;
      double var2 = Math.abs((double)var0);
      double var4 = 1.0 / (1.0 + 0.5 * var2);
      double var6 = var4
         * Math.exp(
            -var2 * var2
               - 1.26551223
               + var4
                  * (
                     1.00002368
                        + var4
                           * (
                              0.37409196
                                 + var4
                                    * (
                                       0.09678418
                                          + var4
                                             * (
                                                -0.18628806
                                                   + var4
                                                      * (0.27886807 + var4 * (-1.13520398 + var4 * (1.48851587 + var4 * (-0.82215223 + var4 * 0.17087277))))
                                             )
                                    )
                           )
                  )
         );
      return (float)(var1 * (1.0 - var6));
   }

   private static float method5697(float var0) {
      return (float)(1.0 / (1.0 + Math.exp(-var0)));
   }

   private static float[] method5698(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] < 0.0F) {
            var0[var1] = (float)Math.expm1(var0[var1]);
         }
      }

      return var0;
   }

   private static float[] method5699(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = (float)Math.tanh(var0[var1]);
      }

      return var0;
   }

   private static float[] method5700(float[]... var0) {
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

   private static float[] method5701(Random var0, int var1) {
      float[] var2 = new float[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = (float)var0.nextGaussian();
      }

      return var2;
   }

   private static int method5702(double var0) {
      if (!Double.isFinite(var0)) {
         return 0;
      } else if (var0 >= 2.147483647E9) {
         return Integer.MAX_VALUE;
      } else {
         return var0 <= -2.1474836E9F ? Integer.MIN_VALUE : (int)Math.round(var0);
      }
   }

   private static JsonObject method5703(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method5711(var2 != null && var2.isJsonObject(), "missing object " + var1);
      return var2.getAsJsonObject();
   }

   private static JsonArray method5704(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method5711(var2 != null && var2.isJsonArray(), "missing array " + var1);
      return var2.getAsJsonArray();
   }

   private static String method5705(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method5711(var2 != null && var2.isJsonPrimitive(), "missing string " + var1);
      return var2.getAsString();
   }

   private static float method5706(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method5711(var2 != null && var2.isJsonPrimitive(), "missing number " + var1);
      float var3 = var2.getAsFloat();
      method5711(Float.isFinite(var3), "non-finite " + var1);
      return var3;
   }

   private static int method5707(JsonObject var0, String var1) {
      return Math.round(method5706(var0, var1));
   }

   private static float[] method5708(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method5704(var0, var1);
      method5711(var3.size() == var2, var1 + " length mismatch");
      float[] var4 = new float[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsFloat();
         method5711(Float.isFinite(var4[var5]), "non-finite " + var1);
      }

      return var4;
   }

   private static int[] method5709(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method5704(var0, var1);
      method5711(var3.size() == var2, var1 + " length mismatch");
      int[] var4 = new int[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsInt();
      }

      return var4;
   }

   private static float[][] method5710(JsonObject var0, String var1, int var2, int var3) {
      JsonArray var4 = method5704(var0, var1);
      method5711(var4.size() == var2, var1 + " rows mismatch");
      float[][] var5 = new float[var2][var3];

      for (int var6 = 0; var6 < var2; var6++) {
         JsonArray var7 = var4.get(var6).getAsJsonArray();
         method5711(var7.size() == var3, var1 + " columns mismatch");

         for (int var8 = 0; var8 < var3; var8++) {
            var5[var6][var8] = var7.get(var8).getAsFloat();
         }
      }

      return var5;
   }

   private static void method5711(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalArgumentException(var1);
      }
   }
}

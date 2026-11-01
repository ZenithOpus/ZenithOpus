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

public final class WorldStylePlanModel {
   public static final String text = "neuraim_java_world_style_plan_v1";
   public static final String text2 = "neuraim_aim_world_v1.json";
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
   private final String text3;
   private final String text4;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private final float floatValue6;
   private final float floatValue7;
   private final float floatValue8;
   private final float floatValue9;
   private final int intValue13;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final boolean[] flags;
   private final float[][][] floatValues3;
   private final float[][][] floatValues4;
   private final float[][] floatValues5;
   private final float[][] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final float[][][][][] floatValues9;
   private final float[][][][][] floatValues10;
   private final float[] floatValues11;
   private final float[] floatValues12;
   private final float[][] floatValues13;
   private final float[][] floatValues14;
   private final float[][] floatValues15;
   private final float[][] floatValues16;
   private final float[] floatValues17;
   private final float[] floatValues18;
   private final float[] floatValues19;
   private final float[] floatValues20;
   private final float[] floatValues21;
   private final float[] floatValues22;
   private final float[] floatValues23;
   private final float[] floatValues24;
   private final float[] floatValues25;
   private final float[] floatValues26;

   private WorldStylePlanModel(JsonObject var1) {
      method6195("neuraim_java_world_style_plan_v1".equals(method6189(var1, "format")), "unexpected model format");
      JsonObject var2 = method6187(var1, "architecture");
      this.intValue = method6190(var2, "input_dim");
      this.intValue2 = method6190(var2, "hidden_dim");
      this.intValue3 = method6190(var2, "num_layers");
      this.intValue4 = method6190(var2, "horizons");
      this.intValue5 = method6190(var2, "style_dim");
      this.intValue6 = method6190(var2, "physics_input_dim");
      this.intValue10 = method6190(var2, "physics_embedding_dim");
      this.intValue7 = method6190(var2, "wall_channels");
      this.intValue8 = method6190(var2, "wall_size");
      this.intValue9 = method6190(var2, "wall_embedding_dim");
      this.floatValue = method6191(var2, "count_scale");
      this.floatValue2 = method6191(var2, "min_log_scale");
      this.floatValue3 = method6191(var2, "max_log_scale");
      this.floatValue4 = method6191(var2, "max_correlation");
      method6195(this.intValue3 > 0, "num_layers must be positive");
      method6195(this.intValue8 == 8 && this.intValue7 == 3, "only a 3x8x8x8 cube is supported");
      JsonObject var3 = method6187(var1, "runtime");
      this.intValue11 = method6190(var3, "history_ticks");
      this.intValue12 = method6190(var3, "style_refresh_ticks");
      this.floatValue5 = method6191(var3, "variance_temperature");
      this.text3 = method6189(var3, "action_mode");
      this.text4 = method6189(var3, "style_scope");
      method6195(this.text3.equals("sampled") || this.text3.equals("means"), "unsupported action mode");
      method6195(this.text4.equals("target_session"), "unsupported style scope");
      this.flags = new boolean[this.intValue];
      JsonArray var4 = var3.getAsJsonArray("masked_history_feature_indices");
      if (var4 != null) {
         for (JsonElement var6 : var4) {
            int var7 = var6.getAsInt();
            method6195(var7 >= 0 && var7 < this.intValue, "masked feature index out of range");
            this.flags[var7] = true;
         }
      }

      JsonObject var11 = method6187(var1, "collision_cube");
      method6195(method6190(var11, "size") == this.intValue8, "collision cube size mismatch");
      this.floatValue6 = method6191(var11, "horizontal_radius");
      this.floatValue7 = method6191(var11, "min_y_offset");
      this.floatValue8 = method6191(var11, "max_y_offset");
      this.intValue13 = method6190(var11, "occupancy_samples_per_axis");
      this.floatValue9 = method6191(var11, "distance_cap");
      JsonObject var12 = method6187(var1, "standardizer");
      method6195(method6188(var12, "columns").size() == this.intValue, "standardizer column count mismatch");
      this.floatValues = method6192(var12, "mean", this.intValue);
      this.floatValues2 = method6192(var12, "scale", this.intValue);

      for (float var10 : this.floatValues2) {
         method6195(Float.isFinite(var10) && var10 > 0.0F, "invalid feature scale");
      }

      JsonObject var14 = method6187(var1, "weights");
      this.floatValues3 = new float[this.intValue3][][];
      this.floatValues4 = new float[this.intValue3][][];
      this.floatValues5 = new float[this.intValue3][];
      this.floatValues6 = new float[this.intValue3][];

      for (int var15 = 0; var15 < this.intValue3; var15++) {
         int var17 = var15 == 0 ? this.intValue : this.intValue2;
         this.floatValues3[var15] = method6193(var14, "encoder.weight_ih_l" + var15, 3 * this.intValue2, var17);
         this.floatValues4[var15] = method6193(var14, "encoder.weight_hh_l" + var15, 3 * this.intValue2, this.intValue2);
         this.floatValues5[var15] = method6192(var14, "encoder.bias_ih_l" + var15, 3 * this.intValue2);
         this.floatValues6[var15] = method6192(var14, "encoder.bias_hh_l" + var15, 3 * this.intValue2);
      }

      this.floatValues7 = method6192(var14, "history_norm.weight", this.intValue2);
      this.floatValues8 = method6192(var14, "history_norm.bias", this.intValue2);
      this.floatValues9 = method6194(var14, "wall_encoder.0.weight", 16, this.intValue7, 3, 3, 3);
      this.floatValues11 = method6192(var14, "wall_encoder.0.bias", 16);
      this.floatValues10 = method6194(var14, "wall_encoder.3.weight", 32, 16, 3, 3, 3);
      this.floatValues12 = method6192(var14, "wall_encoder.3.bias", 32);
      this.floatValues13 = method6193(var14, "wall_encoder.7.weight", this.intValue9, 256);
      this.floatValues17 = method6192(var14, "wall_encoder.7.bias", this.intValue9);
      this.floatValues18 = method6192(var14, "wall_encoder.8.weight", this.intValue9);
      this.floatValues19 = method6192(var14, "wall_encoder.8.bias", this.intValue9);
      this.floatValues14 = method6193(var14, "physics_encoder.0.weight", this.intValue10, this.intValue6);
      this.floatValues20 = method6192(var14, "physics_encoder.0.bias", this.intValue10);
      this.floatValues21 = method6192(var14, "physics_encoder.1.weight", this.intValue10);
      this.floatValues22 = method6192(var14, "physics_encoder.1.bias", this.intValue10);
      int var16 = this.intValue2 + this.intValue5 + this.intValue9 + this.intValue10;
      this.floatValues15 = method6193(var14, "style_fusion.0.weight", this.intValue2, var16);
      this.floatValues23 = method6192(var14, "style_fusion.0.bias", this.intValue2);
      this.floatValues24 = method6192(var14, "style_fusion.1.weight", this.intValue2);
      this.floatValues25 = method6192(var14, "style_fusion.1.bias", this.intValue2);
      this.floatValues16 = method6193(var14, "plan_head.weight", this.intValue4 * 5, this.intValue2);
      this.floatValues26 = method6192(var14, "plan_head.bias", this.intValue4 * 5);
   }

   public static WorldStylePlanModel method6162(Path var0) throws IOException {
      WorldStylePlanModel var2;
      try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         var2 = method6164(var1);
      }

      return var2;
   }

   public static WorldStylePlanModel method6163(InputStream var0) throws IOException {
      WorldStylePlanModel var2;
      try (InputStreamReader var1 = new InputStreamReader(var0, StandardCharsets.UTF_8)) {
         var2 = method6164(var1);
      }

      return var2;
   }

   private static WorldStylePlanModel method6164(Reader var0) throws IOException {
      try {
         JsonObject var1 = (JsonObject)new Gson().fromJson(var0, JsonObject.class);
         method6195(var1 != null, "empty model JSON");
         return new WorldStylePlanModel(var1);
      } catch (RuntimeException var2) {
         throw new IOException("invalid Neuraim model JSON: " + var2.getMessage(), var2);
      }
   }

   public static WorldStylePlanModel method6165() {
      for (Path var1 : List.of(
         Path.of("models", "neuraim_aim_world_v1.json"),
         Path.of("run", "models", "neuraim_aim_world_v1.json"),
         Path.of("neuraim_aim_world_v1.json"),
         Path.of("..", "models", "neuraim_aim_world_v1.json")
      )) {
         if (Files.isRegularFile(var1)) {
            try {
               WorldStylePlanModel var2 = method6162(var1);
               System.out.println("[Neuraim] Loaded " + var1.toAbsolutePath());
               return var2;
            } catch (IOException var4) {
               System.err.println("[Neuraim] Failed to load " + var1 + ": " + var4.getMessage());
            }
         }
      }

      InputStream var5 = WorldStylePlanModel.class.getClassLoader().getResourceAsStream("models/neuraim_aim_world_v1.json");
      if (var5 != null) {
         try {
            return method6163(var5);
         } catch (IOException var3) {
            System.err.println("[Neuraim] Bundled model is invalid: " + var3.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_aim_world_v1.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public WorldStylePlanModel.Data method6166(float[][] var1, float[] var2, float[][][][] var3, float[] var4) {
      method6195(var1 != null && var1.length == this.intValue11, "history tick count mismatch");
      method6195(var2 != null && var2.length == this.intValue5, "style width mismatch");
      method6195(var4 != null && var4.length == this.intValue6, "physics width mismatch");
      method6186(var3, this.intValue7, this.intValue8);
      float[][] var5 = new float[this.intValue11][this.intValue];

      for (int var6 = 0; var6 < this.intValue11; var6++) {
         method6195(var1[var6] != null && var1[var6].length == this.intValue, "history feature width mismatch");

         for (int var7 = 0; var7 < this.intValue; var7++) {
            var5[var6][var7] = this.flags[var7] ? 0.0F : (var1[var6][var7] - this.floatValues[var7]) / this.floatValues2[var7];
            method6195(Float.isFinite(var5[var6][var7]), "non-finite history feature");
         }
      }

      float[] var17 = this.method6171(var5);
      float[] var18 = this.method6173(var3);
      float[] var8 = method6179(method6178(method6183(this.floatValues14, this.floatValues20, var4), this.floatValues21, this.floatValues22));
      float[] var9 = method6179(
         method6178(method6183(this.floatValues15, this.floatValues23, method6177(var17, var2, var18, var8)), this.floatValues24, this.floatValues25)
      );
      float[] var10 = method6183(this.floatValues16, this.floatValues26, var9);
      float[][] var11 = new float[this.intValue4][2];
      float[][] var12 = new float[this.intValue4][2];
      float[] var13 = new float[this.intValue4];
      float var14 = (float)Math.log(this.floatValue);

      for (int var15 = 0; var15 < this.intValue4; var15++) {
         int var16 = var15 * 5;
         var11[var15][0] = var10[var16] * this.floatValue;
         var11[var15][1] = var10[var16 + 1] * this.floatValue;
         var12[var15][0] = clamp(var10[var16 + 2], this.floatValue2, this.floatValue3) + var14;
         var12[var15][1] = clamp(var10[var16 + 3], this.floatValue2, this.floatValue3) + var14;
         var13[var15] = (float)Math.tanh(var10[var16 + 4]) * this.floatValue4;
      }

      return new WorldStylePlanModel.Data(var17, var18, var8, var9, var11, var12, var13);
   }

   public int[][] method6167(float[][] var1, float[] var2, float[][][][] var3, float[] var4, Random var5) {
      method6195(var5 != null, "random must not be null");
      WorldStylePlanModel.Data var6 = this.method6166(var1, var2, var3, var4);
      int[][] var7 = new int[this.intValue4][2];
      double var8 = Math.sqrt(this.floatValue5);

      for (int var10 = 0; var10 < this.intValue4; var10++) {
         double var11 = var5.nextGaussian();
         double var13 = var5.nextGaussian();
         double var15 = var6.floatValues7[var10];
         double var17 = var15 * var11 + Math.sqrt(Math.max(1.0E-6, 1.0 - var15 * var15)) * var13;
         var7[var10][0] = method6185(var6.floatValues5[var10][0] + Math.exp(var6.floatValues6[var10][0]) * var8 * var11);
         var7[var10][1] = method6185(var6.floatValues5[var10][1] + Math.exp(var6.floatValues6[var10][1]) * var8 * var17);
      }

      return var7;
   }

   public int[][] method6168(float[][] var1, float[] var2, float[][][][] var3, float[] var4) {
      WorldStylePlanModel.Data var5 = this.method6166(var1, var2, var3, var4);
      int[][] var6 = new int[this.intValue4][2];

      for (int var7 = 0; var7 < this.intValue4; var7++) {
         var6[var7][0] = method6185(var5.floatValues5[var7][0]);
         var6[var7][1] = method6185(var5.floatValues5[var7][1]);
      }

      return var6;
   }

   public float[] method6169(Random var1) {
      float[] var2 = new float[this.intValue5];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = (float)var1.nextGaussian();
      }

      return var2;
   }

   public float[] method6170() {
      return new float[this.intValue5];
   }

   private float[] method6171(float[][] var1) {
      float[][] var2 = new float[this.intValue3][this.intValue2];

      for (float[] var6 : var1) {
         float[] var7 = var6;

         for (int var8 = 0; var8 < this.intValue3; var8++) {
            var2[var8] = this.method6172(var7, var2[var8], var8);
            var7 = var2[var8];
         }
      }

      return method6178(var2[this.intValue3 - 1], this.floatValues7, this.floatValues8);
   }

   private float[] method6172(float[] var1, float[] var2, int var3) {
      float[] var4 = method6183(this.floatValues3[var3], this.floatValues5[var3], var1);
      float[] var5 = method6183(this.floatValues4[var3], this.floatValues6[var3], var2);
      float[] var6 = new float[this.intValue2];

      for (int var7 = 0; var7 < this.intValue2; var7++) {
         float var8 = method6184(var4[var7] + var5[var7]);
         float var9 = method6184(var4[this.intValue2 + var7] + var5[this.intValue2 + var7]);
         float var10 = (float)Math.tanh(var4[2 * this.intValue2 + var7] + var8 * var5[2 * this.intValue2 + var7]);
         var6[var7] = (1.0F - var9) * var10 + var9 * var2[var7];
      }

      return var6;
   }

   private float[] method6173(float[][][][] var1) {
      float[][][][] var2 = method6175(method6180(method6174(var1, this.floatValues9, this.floatValues11)));
      float[] var3 = method6176(method6175(method6180(method6174(var2, this.floatValues10, this.floatValues12))));
      method6195(var3.length == 256, "wall flatten mismatch");
      return method6179(method6178(method6183(this.floatValues13, this.floatValues17, var3), this.floatValues18, this.floatValues19));
   }

   private static float[][][][] method6174(float[][][][] var0, float[][][][][] var1, float[] var2) {
      int var3 = var1.length;
      int var4 = var0.length;
      int var5 = var0[0].length;
      int var6 = var0[0][0].length;
      int var7 = var0[0][0][0].length;
      float[][][][] var8 = new float[var3][var5][var6][var7];

      for (int var9 = 0; var9 < var3; var9++) {
         for (int var10 = 0; var10 < var5; var10++) {
            for (int var11 = 0; var11 < var6; var11++) {
               for (int var12 = 0; var12 < var7; var12++) {
                  float var13 = var2[var9];

                  for (int var14 = 0; var14 < var4; var14++) {
                     for (int var15 = 0; var15 < 3; var15++) {
                        for (int var16 = 0; var16 < 3; var16++) {
                           for (int var17 = 0; var17 < 3; var17++) {
                              int var18 = var10 + var15 - 1;
                              int var19 = var11 + var16 - 1;
                              int var20 = var12 + var17 - 1;
                              if (var18 >= 0 && var18 < var5 && var19 >= 0 && var19 < var6 && var20 >= 0 && var20 < var7) {
                                 var13 += var1[var9][var14][var15][var16][var17] * var0[var14][var18][var19][var20];
                              }
                           }
                        }
                     }
                  }

                  var8[var9][var10][var11][var12] = var13;
               }
            }
         }
      }

      return var8;
   }

   private static float[][][][] method6175(float[][][][] var0) {
      int var1 = var0.length;
      int var2 = var0[0].length / 2;
      int var3 = var0[0][0].length / 2;
      int var4 = var0[0][0][0].length / 2;
      float[][][][] var5 = new float[var1][var2][var3][var4];

      for (int var6 = 0; var6 < var1; var6++) {
         for (int var7 = 0; var7 < var2; var7++) {
            for (int var8 = 0; var8 < var3; var8++) {
               for (int var9 = 0; var9 < var4; var9++) {
                  float var10 = -Float.MAX_VALUE;

                  for (int var11 = 0; var11 < 2; var11++) {
                     for (int var12 = 0; var12 < 2; var12++) {
                        for (int var13 = 0; var13 < 2; var13++) {
                           var10 = Math.max(var10, var0[var6][2 * var7 + var11][2 * var8 + var12][2 * var9 + var13]);
                        }
                     }
                  }

                  var5[var6][var7][var8][var9] = var10;
               }
            }
         }
      }

      return var5;
   }

   private static float[] method6176(float[][][][] var0) {
      float[] var1 = new float[var0.length * var0[0].length * var0[0][0].length * var0[0][0][0].length];
      int var2 = 0;

      for (float[][][] var6 : var0) {
         for (float[][] var10 : var6) {
            for (float[] var14 : var10) {
               for (float var18 : var14) {
                  var1[var2++] = var18;
               }
            }
         }
      }

      return var1;
   }

   private static float[] method6177(float[]... var0) {
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

   private static float[] method6178(float[] var0, float[] var1, float[] var2) {
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
      float var14 = (float)(1.0 / Math.sqrt(var11 + 1.0E-5F));
      float[] var16 = new float[var0.length];

      for (int var18 = 0; var18 < var0.length; var18++) {
         var16[var18] = (var0[var18] - var3) * var14 * var1[var18] + var2[var18];
      }

      return var16;
   }

   private static float[] method6179(float[] var0) {
      float[] var1 = new float[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = method6181(var0[var2]);
      }

      return var1;
   }

   private static float[][][][] method6180(float[][][][] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         for (int var2 = 0; var2 < var0[var1].length; var2++) {
            for (int var3 = 0; var3 < var0[var1][var2].length; var3++) {
               for (int var4 = 0; var4 < var0[var1][var2][var3].length; var4++) {
                  var0[var1][var2][var3][var4] = method6181(var0[var1][var2][var3][var4]);
               }
            }
         }
      }

      return var0;
   }

   private static float method6181(float var0) {
      return 0.5F * var0 * (1.0F + method6182(var0 * 0.70710677F));
   }

   private static float method6182(float var0) {
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

   private static float[] method6183(float[][] var0, float[] var1, float[] var2) {
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

   private static float method6184(float var0) {
      return (float)(1.0 / (1.0 + Math.exp(-var0)));
   }

   private static float clamp(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static int method6185(double var0) {
      if (!Double.isFinite(var0)) {
         return 0;
      } else if (var0 >= 2.147483647E9) {
         return Integer.MAX_VALUE;
      } else {
         return var0 <= -2.1474836E9F ? Integer.MIN_VALUE : (int)Math.round(var0);
      }
   }

   private static void method6186(float[][][][] var0, int var1, int var2) {
      method6195(var0 != null && var0.length == var1, "wall channel mismatch");

      for (float[][][] var6 : var0) {
         method6195(var6.length == var2, "wall depth mismatch");

         for (float[][] var10 : var6) {
            method6195(var10.length == var2, "wall height mismatch");

            for (float[] var14 : var10) {
               method6195(var14.length == var2, "wall width mismatch");
            }
         }
      }
   }

   private static JsonObject method6187(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method6195(var2 != null && var2.isJsonObject(), "missing object " + var1);
      return var2.getAsJsonObject();
   }

   private static JsonArray method6188(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method6195(var2 != null && var2.isJsonArray(), "missing array " + var1);
      return var2.getAsJsonArray();
   }

   private static String method6189(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method6195(var2 != null && var2.isJsonPrimitive(), "missing string " + var1);
      return var2.getAsString();
   }

   private static int method6190(JsonObject var0, String var1) {
      return Math.round(method6191(var0, var1));
   }

   private static float method6191(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method6195(var2 != null && var2.isJsonPrimitive(), "missing number " + var1);
      float var3 = var2.getAsFloat();
      method6195(Float.isFinite(var3), "non-finite " + var1);
      return var3;
   }

   private static float[] method6192(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method6188(var0, var1);
      method6195(var3.size() == var2, var1 + " length mismatch");
      float[] var4 = new float[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsFloat();
         method6195(Float.isFinite(var4[var5]), "non-finite " + var1);
      }

      return var4;
   }

   private static float[][] method6193(JsonObject var0, String var1, int var2, int var3) {
      JsonArray var4 = method6188(var0, var1);
      method6195(var4.size() == var2, var1 + " rows mismatch");
      float[][] var5 = new float[var2][var3];

      for (int var6 = 0; var6 < var2; var6++) {
         JsonArray var7 = var4.get(var6).getAsJsonArray();
         method6195(var7.size() == var3, var1 + " cols mismatch");

         for (int var8 = 0; var8 < var3; var8++) {
            var5[var6][var8] = var7.get(var8).getAsFloat();
         }
      }

      return var5;
   }

   private static float[][][][][] method6194(JsonObject var0, String var1, int var2, int var3, int var4, int var5, int var6) {
      JsonArray var7 = method6188(var0, var1);
      method6195(var7.size() == var2, var1 + " dim0");
      float[][][][][] var8 = new float[var2][var3][var4][var5][var6];

      for (int var9 = 0; var9 < var2; var9++) {
         JsonArray var10 = var7.get(var9).getAsJsonArray();
         method6195(var10.size() == var3, var1 + " dim1");

         for (int var11 = 0; var11 < var3; var11++) {
            JsonArray var12 = var10.get(var11).getAsJsonArray();
            method6195(var12.size() == var4, var1 + " dim2");

            for (int var13 = 0; var13 < var4; var13++) {
               JsonArray var14 = var12.get(var13).getAsJsonArray();
               method6195(var14.size() == var5, var1 + " dim3");

               for (int var15 = 0; var15 < var5; var15++) {
                  JsonArray var16 = var14.get(var15).getAsJsonArray();
                  method6195(var16.size() == var6, var1 + " dim4");

                  for (int var17 = 0; var17 < var6; var17++) {
                     var8[var9][var11][var13][var15][var17] = var16.get(var17).getAsFloat();
                  }
               }
            }
         }
      }

      return var8;
   }

   private static void method6195(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalArgumentException(var1);
      }
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue11() {
      return this.intValue11;
   }

   public int getIntValue4() {
      return this.intValue4;
   }

   public int getIntValue12() {
      return this.intValue12;
   }

   public String getText3() {
      return this.text3;
   }

   public String getText4() {
      return this.text4;
   }

   public int getIntValue6() {
      return this.intValue6;
   }

   public int getIntValue8() {
      return this.intValue8;
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

   public int getIntValue13() {
      return this.intValue13;
   }

   public float getFloatValue9() {
      return this.floatValue9;
   }

   public static final class Data {
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;
      private final float[][] floatValues5;
      private final float[][] floatValues6;
      private final float[] floatValues7;

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

      public float[][] floatValues5() {
         return this.floatValues5;
      }

      public float[][] floatValues6() {
         return this.floatValues6;
      }

      public float[] floatValues7() {
         return this.floatValues7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4) && java.util.Objects.equals(this.floatValues5, other.floatValues5) && java.util.Objects.equals(this.floatValues6, other.floatValues6) && java.util.Objects.equals(this.floatValues7, other.floatValues7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, floatValues2, floatValues3, floatValues4, floatValues5, floatValues6, floatValues7);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + ", " + "floatValues5=" + this.floatValues5 + ", " + "floatValues6=" + this.floatValues6 + ", " + "floatValues7=" + this.floatValues7 + "]";
      }

      public Data(float[] var1, float[] var2, float[] var3, float[] var4, float[][] var5, float[][] var6, float[] var7) {
         this.floatValues = var1;
         this.floatValues2 = var2;
         this.floatValues3 = var3;
         this.floatValues4 = var4;
         this.floatValues5 = var5;
         this.floatValues6 = var6;
         this.floatValues7 = var7;
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

      public float[][] getFloatValues5() {
         return this.floatValues5;
      }

      public float[][] getFloatValues6() {
         return this.floatValues6;
      }

      public float[] getFloatValues7() {
         return this.floatValues7;
      }
   }
}

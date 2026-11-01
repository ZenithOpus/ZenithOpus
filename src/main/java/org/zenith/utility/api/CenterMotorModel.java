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
import java.util.Arrays;
import java.util.List;

public final class CenterMotorModel {
   public static final String text = "neuraim_java_center_motor_v2";
   public static final String text2 = "neuraim_center_motor_v2.json";
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final int[] intValues;
   private final int intValue6;
   private final int intValue7;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final float[] floatValues5;
   private final float[][][] floatValues6;
   private final float[][][] floatValues7;
   private final float[][] floatValues8;
   private final float[][] floatValues9;
   private final float[][] floatValues10;
   private final float[][] floatValues11;
   private final float[][] floatValues12;
   private final float[] floatValues13;
   private final float[] floatValues14;
   private final float[] floatValues15;
   private final int[] intValues2 = new int[2];

   private CenterMotorModel(JsonObject var1) {
      method4511("neuraim_java_center_motor_v2".equals(method4506(var1, "format")), "unexpected center motor format");
      JsonObject var2 = method4504(var1, "runtime");
      JsonObject var3 = method4504(var1, "features");
      this.intValue = method4507(var2, "history_ticks");
      this.intValue2 = method4507(var2, "raw_input_dim");
      this.intValue3 = method4507(var2, "model_input_dim");
      this.intValues = method4509(var3, "model_feature_indices", this.intValue3);
      this.intValue6 = method4507(var3, "action_yaw_raw_index");
      this.intValue7 = method4507(var3, "action_pitch_raw_index");
      this.floatValues = method4508(var3, "mean", this.intValue3);
      this.floatValues2 = method4508(var3, "scale", this.intValue3);
      JsonObject var4 = method4504(var1, "architecture");
      JsonObject var5 = method4504(var1, "weights");
      this.intValue4 = method4507(var4, "hidden_dim");
      this.intValue5 = method4507(var4, "num_layers");
      method4511(this.intValue == 16 && this.intValue3 == 34 && this.intValue2 == 54 && this.intValue5 == 2, "unsupported architecture");
      this.floatValues6 = new float[this.intValue5][][];
      this.floatValues7 = new float[this.intValue5][][];
      this.floatValues8 = new float[this.intValue5][];
      this.floatValues9 = new float[this.intValue5][];

      for (int var6 = 0; var6 < this.intValue5; var6++) {
         int var7 = var6 == 0 ? this.intValue3 : this.intValue4;
         this.floatValues6[var6] = method4510(var5, "encoder.weight_ih_l" + var6, 3 * this.intValue4, var7);
         this.floatValues7[var6] = method4510(var5, "encoder.weight_hh_l" + var6, 3 * this.intValue4, this.intValue4);
         this.floatValues8[var6] = method4508(var5, "encoder.bias_ih_l" + var6, 3 * this.intValue4);
         this.floatValues9[var6] = method4508(var5, "encoder.bias_hh_l" + var6, 3 * this.intValue4);
      }

      this.floatValues3 = method4508(var5, "norm.weight", this.intValue4);
      this.floatValues4 = method4508(var5, "norm.bias", this.intValue4);
      this.floatValues10 = method4510(var5, "shared.0.weight", this.intValue4, this.intValue4);
      this.floatValues13 = method4508(var5, "shared.0.bias", this.intValue4);
      this.floatValues11 = method4510(var5, "mode_head.weight", 10, this.intValue4);
      this.floatValues14 = method4508(var5, "mode_head.bias", 10);
      this.floatValues12 = method4510(var5, "magnitude_head.weight", 2, this.intValue4);
      this.floatValues15 = method4508(var5, "magnitude_head.bias", 2);
      this.floatValues5 = method4508(var5, "action_scales", 2);
   }

   public static CenterMotorModel method4492(Path var0) throws IOException {
      CenterMotorModel var2;
      try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         var2 = method4494(var1);
      }

      return var2;
   }

   public static CenterMotorModel method4493(InputStream var0) throws IOException {
      CenterMotorModel var2;
      try (InputStreamReader var1 = new InputStreamReader(var0, StandardCharsets.UTF_8)) {
         var2 = method4494(var1);
      }

      return var2;
   }

   private static CenterMotorModel method4494(Reader var0) throws IOException {
      try {
         JsonObject var1 = (JsonObject)new Gson().fromJson(var0, JsonObject.class);
         method4511(var1 != null, "empty model JSON");
         return new CenterMotorModel(var1);
      } catch (RuntimeException var2) {
         throw new IOException("invalid center motor: " + var2.getMessage(), var2);
      }
   }

   public static CenterMotorModel method4495() {
      for (Path var1 : List.of(
         Path.of("models", "neuraim_center_motor_v2.json"),
         Path.of("run", "models", "neuraim_center_motor_v2.json"),
         Path.of("neuraim_center_motor_v2.json"),
         Path.of("..", "models", "neuraim_center_motor_v2.json")
      )) {
         if (Files.isRegularFile(var1)) {
            try {
               CenterMotorModel var2 = method4492(var1);
               System.out.println("[Neuraim] Loaded center motor " + var1.toAbsolutePath());
               return var2;
            } catch (IOException var4) {
               System.err.println("[Neuraim] Failed to load " + var1 + ": " + var4.getMessage());
            }
         }
      }

      InputStream var5 = CenterMotorModel.class.getClassLoader().getResourceAsStream("models/neuraim_center_motor_v2.json");
      if (var5 != null) {
         try {
            return method4493(var5);
         } catch (IOException var3) {
            System.err.println("[Neuraim] Bundled center motor is invalid: " + var3.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_center_motor_v2.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public int[] method4496(float[][] var1) {
      method4511(var1 != null && var1.length == this.intValue, "history tick count mismatch");
      float[][] var2 = new float[this.intValue5][this.intValue4];

      for (float[] var6 : var1) {
         method4511(var6 != null && var6.length == this.intValue2, "history feature width mismatch");
         float[] var7 = new float[this.intValue3];

         for (int var8 = 0; var8 < this.intValue3; var8++) {
            var7[var8] = (var6[this.intValues[var8]] - this.floatValues[var8]) / this.floatValues2[var8];
         }

         for (int var17 = 0; var17 < this.intValue5; var17++) {
            var2[var17] = method4498(
               var7, var2[var17], this.floatValues6[var17], this.floatValues7[var17], this.floatValues8[var17], this.floatValues9[var17], this.intValue4
            );
            var7 = var2[var17];
         }
      }

      float[] var12 = method4502(method4499(this.floatValues10, this.floatValues13, method4500(var2[this.intValue5 - 1], this.floatValues3, this.floatValues4)));
      float[] var13 = method4499(this.floatValues11, this.floatValues14, var12);
      float[] var14 = method4499(this.floatValues12, this.floatValues15, var12);
      if (this.intValues2[0] == 0 && this.intValues2[1] == 0) {
         this.method4497(var1);
      }

      int[] var15 = new int[2];

      for (int var16 = 0; var16 < 2; var16++) {
         int var18 = 0;

         for (int var9 = 1; var9 < 5; var9++) {
            if (var13[var16 * 5 + var9] > var13[var16 * 5 + var18]) {
               var18 = var9;
            }
         }

         int var19 = this.intValues2[var16] == 0 ? 1 : this.intValues2[var16];

         int var10 = switch (var18) {
            case 1 -> var19;
            case 2 -> -var19;
            case 3 -> 1;
            case 4 -> -1;
            default -> 0;
         };
         if (var10 == 0) {
            var15[var16] = 0;
         } else {
            int var11 = method4503(1.0 + Math.exp(Math.max(-5.0, Math.min(5.0, (double)var14[var16]))) * this.floatValues5[var16]);
            var15[var16] = var10 * var11;
            this.intValues2[var16] = var10;
         }
      }

      return var15;
   }

   public void reset() {
      Arrays.fill(this.intValues2, 0);
   }

   private void method4497(float[][] var1) {
      int[] var2 = new int[]{this.intValue6, this.intValue7};

      for (int var3 = 0; var3 < 2; var3++) {
         for (int var4 = this.intValue - 1; var4 >= 0; var4--) {
            float var5 = var1[var4][var2[var3]];
            if (var5 != 0.0F) {
               this.intValues2[var3] = var5 > 0.0F ? 1 : -1;
               break;
            }
         }
      }
   }

   private static float[] method4498(float[] var0, float[] var1, float[][] var2, float[][] var3, float[] var4, float[] var5, int var6) {
      float[] var7 = method4499(var2, var4, var0);
      float[] var8 = method4499(var3, var5, var1);
      float[] var9 = new float[var6];

      for (int var10 = 0; var10 < var6; var10++) {
         float var11 = method4501(var7[var10] + var8[var10]);
         float var12 = method4501(var7[var6 + var10] + var8[var6 + var10]);
         float var13 = (float)Math.tanh(var7[2 * var6 + var10] + var11 * var8[2 * var6 + var10]);
         var9[var10] = (1.0F - var12) * var13 + var12 * var1[var10];
      }

      return var9;
   }

   private static float[] method4499(float[][] var0, float[] var1, float[] var2) {
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

   private static float[] method4500(float[] var0, float[] var1, float[] var2) {
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

      for (int var18 = 0; var18 < var0.length; var18++) {
         var16[var18] = (var0[var18] - var3) * var14 * var1[var18] + var2[var18];
      }

      return var16;
   }

   private static float method4501(float var0) {
      return (float)(1.0 / (1.0 + Math.exp(-var0)));
   }

   private static float[] method4502(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] *= method4501(var0[var1]);
      }

      return var0;
   }

   private static int method4503(double var0) {
      return Double.isFinite(var0) ? (int)Math.rint(var0) : 0;
   }

   private static JsonObject method4504(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method4511(var2 != null && var2.isJsonObject(), "missing object " + var1);
      return var2.getAsJsonObject();
   }

   private static JsonArray method4505(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method4511(var2 != null && var2.isJsonArray(), "missing array " + var1);
      return var2.getAsJsonArray();
   }

   private static String method4506(JsonObject var0, String var1) {
      return var0.get(var1).getAsString();
   }

   private static int method4507(JsonObject var0, String var1) {
      return var0.get(var1).getAsInt();
   }

   private static float[] method4508(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method4505(var0, var1);
      method4511(var3.size() == var2, var1 + " length mismatch");
      float[] var4 = new float[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsFloat();
         method4511(Float.isFinite(var4[var5]), "non-finite " + var1);
      }

      return var4;
   }

   private static int[] method4509(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method4505(var0, var1);
      method4511(var3.size() == var2, var1 + " length mismatch");
      int[] var4 = new int[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsInt();
      }

      return var4;
   }

   private static float[][] method4510(JsonObject var0, String var1, int var2, int var3) {
      JsonArray var4 = method4505(var0, var1);
      method4511(var4.size() == var2, var1 + " rows mismatch");
      float[][] var5 = new float[var2][var3];

      for (int var6 = 0; var6 < var2; var6++) {
         JsonArray var7 = var4.get(var6).getAsJsonArray();
         method4511(var7.size() == var3, var1 + " cols mismatch");

         for (int var8 = 0; var8 < var3; var8++) {
            var5[var6][var8] = var7.get(var8).getAsFloat();
         }
      }

      return var5;
   }

   private static void method4511(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalArgumentException(var1);
      }
   }
}

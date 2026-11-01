package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BurstSumModel {
   private static final String[] texts = new String[]{
      "target_center_delta_yaw_count_units",
      "target_center_delta_pitch_count_units",
      "target_box_min_delta_yaw_count_units",
      "target_box_max_delta_yaw_count_units",
      "target_box_min_delta_pitch_count_units",
      "target_box_max_delta_pitch_count_units",
      "target_box_width_yaw_count_units",
      "target_box_height_pitch_count_units",
      "target_center_distance",
      "sim_2t_target_center_delta_yaw_count_units",
      "sim_2t_target_center_delta_pitch_count_units",
      "target_motion_x",
      "target_motion_y",
      "target_motion_z",
      "player_motion_x",
      "player_motion_y",
      "player_motion_z"
   };
   private static final String text = "/assets/zenith/aimpipe/burst_sum_model.json";
   private static final Path[] paths = new Path[]{Path.of("burst_sum_model_6t.json"), Path.of("burst_sum_model.json")};
   private static final Path[] paths2 = new Path[]{
      Path.of("..", "scripts", "out", "burst_sum_model_6t.json"),
      Path.of("scripts", "out", "burst_sum_model_6t.json"),
      Path.of("..", "scripts", "test", "aimpipe", "out", "burst_sum_model.json"),
      Path.of("scripts", "test", "aimpipe", "out", "burst_sum_model.json")
   };
   private final BurstSumModel.Data[] datas;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float floatValue;
   private final float[][] floatValues3;
   private final float[] floatValues4;
   private final float[][] floatValues5;
   private final float[] floatValues6;
   private final float[][] floatValues7;
   private final float[] floatValues8;

   private BurstSumModel(
      BurstSumModel.Data[] var1,
      float[] var2,
      float[] var3,
      float var4,
      float[][] var5,
      float[] var6,
      float[][] var7,
      float[] var8,
      float[][] var9,
      float[] var10
   ) {
      this.datas = var1;
      this.floatValues = var2;
      this.floatValues2 = var3;
      this.floatValue = var4;
      this.floatValues3 = var5;
      this.floatValues4 = var6;
      this.floatValues5 = var7;
      this.floatValues6 = var8;
      this.floatValues7 = var9;
      this.floatValues8 = var10;
   }

   public int[] method479(float[] var1) {
      float[] var2 = this.method480(var1);
      if (var2 == null) {
         return null;
      } else {
         float[] var3 = method487(var2, this.floatValues3, this.floatValues4, true);
         float[] var4 = method487(var3, this.floatValues5, this.floatValues6, true);
         float[] var5 = method487(var4, this.floatValues7, this.floatValues8, false);
         int[] var6 = new int[2];

         for (int var7 = 0; var7 < var6.length; var7++) {
            double var8 = var5[var7] * this.floatValues2[var7] + this.floatValues[var7];
            var8 = Math.signum(var8) * Math.expm1(Math.abs(var8)) * this.floatValue;
            if (!Double.isFinite(var8) || var8 < -2.1474836E9F || var8 > 2.147483647E9) {
               return null;
            }

            var6[var7] = (int)Math.round(var8);
         }

         return var6;
      }
   }

   private float[] method480(float[] var1) {
      if (var1 != null && var1.length == this.datas.length) {
         float[] var2 = new float[this.datas.length];

         for (int var3 = 0; var3 < var2.length; var3++) {
            float var4 = method486(var1[var3], this.datas[var3]);
            var4 = (var4 - this.datas[var3].getFloatValue2()) / this.datas[var3].getFloatValue3();
            if (!Float.isFinite(var4)) {
               return null;
            }

            var2[var3] = var4;
         }

         return var2;
      } else {
         return null;
      }
   }

   public static BurstSumModel method481() {
      for (Path var4 : paths) {
         BurstSumModel var0 = method482(var4);
         if (var0 != null) {
            return var0;
         }
      }

      for (Path var9 : paths2) {
         BurstSumModel var5 = method482(var9);
         if (var5 != null) {
            return var5;
         }
      }

      return method483();
   }

   private static BurstSumModel method482(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : method484(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable var2) {
         System.err.println("[BurstSumModel] failed to load " + var0 + ": " + var2);
         return null;
      }
   }

   private static BurstSumModel method483() {
      try {
         BurstSumModel var1;
         try (InputStream var0 = BurstSumModel.class.getResourceAsStream("/assets/zenith/aimpipe/burst_sum_model.json")) {
            if (var0 == null) {
               return null;
            }

            var1 = method484(new String(var0.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/burst_sum_model.json");
         }

         return var1;
      } catch (Throwable var5) {
         System.err.println("[BurstSumModel] failed to load /assets/zenith/aimpipe/burst_sum_model.json: " + var5);
         return null;
      }
   }

   private static BurstSumModel method484(String var0, String var1) {
      JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
      if (var2.get("format_version").getAsInt() != 1) {
         throw new IllegalStateException("unsupported burst-sum model format");
      } else {
         JsonArray var3 = var2.getAsJsonArray("feature_spec");
         if (var3.size() != texts.length) {
            throw new IllegalStateException("burst-sum feature count mismatch");
         } else {
            BurstSumModel.Data[] var4 = new BurstSumModel.Data[var3.size()];

            for (int var5 = 0; var5 < var4.length; var5++) {
               JsonObject var6 = var3.get(var5).getAsJsonObject();
               if (!texts[var5].equals(var6.get("name").getAsString())) {
                  throw new IllegalStateException("burst-sum feature order mismatch at " + var5);
               }

               var4[var5] = new BurstSumModel.Data(
                  var6.get("transform").getAsString(), var6.get("scale").getAsFloat(), var6.get("mean").getAsFloat(), var6.get("std").getAsFloat()
               );
               if (!Float.isFinite(var4[var5].getFloatValue3()) || var4[var5].getFloatValue3() <= 0.0F) {
                  throw new IllegalStateException("invalid burst-sum feature normalization");
               }
            }

            JsonObject var20 = var2.has("output_transform") ? var2.getAsJsonObject("output_transform") : var2.getAsJsonObject("output");
            if (var20.has("type") && !"signed_mu".equals(var20.get("type").getAsString())) {
               throw new IllegalStateException("unsupported burst-sum output transform");
            } else {
               float var21 = var20.get("scale").getAsFloat();
               float[] var7 = method488(var20.getAsJsonArray("mean"));
               float[] var8 = method488(var20.getAsJsonArray("std"));
               float[][] var9;
               float[] var10;
               float[][] var11;
               float[] var12;
               float[][] var13;
               float[] var14;
               if (var2.has("network")) {
                  JsonObject var15 = var2.getAsJsonObject("network");
                  if (!"tanh".equals(var15.get("activation").getAsString())) {
                     throw new IllegalStateException("unsupported burst-sum activation");
                  }

                  JsonArray var16 = var15.getAsJsonArray("layers");
                  if (var16.size() != 3) {
                     throw new IllegalStateException("burst-sum network must contain three layers");
                  }

                  JsonObject var17 = var16.get(0).getAsJsonObject();
                  JsonObject var18 = var16.get(1).getAsJsonObject();
                  JsonObject var19 = var16.get(2).getAsJsonObject();
                  var9 = method489(var17.getAsJsonArray("weight"));
                  var10 = method488(var17.getAsJsonArray("bias"));
                  var11 = method489(var18.getAsJsonArray("weight"));
                  var12 = method488(var18.getAsJsonArray("bias"));
                  var13 = method489(var19.getAsJsonArray("weight"));
                  var14 = method488(var19.getAsJsonArray("bias"));
               } else {
                  JsonObject var22 = var2.getAsJsonObject("weights");
                  var9 = method489(var22.getAsJsonArray("layer_0_weight"));
                  var10 = method488(var22.getAsJsonArray("layer_0_bias"));
                  var11 = method489(var22.getAsJsonArray("layer_1_weight"));
                  var12 = method488(var22.getAsJsonArray("layer_1_bias"));
                  var13 = method489(var22.getAsJsonArray("layer_2_weight"));
                  var14 = method488(var22.getAsJsonArray("layer_2_bias"));
               }

               method485(var9, var10, var4.length, "layer 0");
               method485(var11, var12, var10.length, "layer 1");
               method485(var13, var14, var12.length, "layer 2");
               if (var14.length == 2 && var7.length == 2 && var8.length == 2 && Float.isFinite(var21) && !(var21 <= 0.0F)) {
                  BurstSumModel var23 = new BurstSumModel(var4, var7, var8, var21, var9, var10, var11, var12, var13, var14);
                  System.out.println("[BurstSumModel] loaded " + var1 + " (features=" + var4.length + ", hidden=" + var10.length + ")");
                  return var23;
               } else {
                  throw new IllegalStateException("invalid burst-sum output shape");
               }
            }
         }
      }
   }

   private static void method485(float[][] var0, float[] var1, int var2, String var3) {
      if (var0.length != var1.length) {
         throw new IllegalStateException(var3 + " output size mismatch");
      } else {
         for (float[] var7 : var0) {
            if (var7.length != var2) {
               throw new IllegalStateException(var3 + " input size mismatch");
            }
         }
      }
   }

   private static float method486(float var0, BurstSumModel.Data var1) {
      String var2 = var1.getText();

      return switch (var2) {
         case "mu" -> (float)(Math.signum(var0) * Math.log1p(Math.abs(var0) / var1.getFloatValue()));
         case "log" -> (float)Math.log1p(Math.max(var0, 0.0F));
         case "lin", "linear" -> var0;
         default -> throw new IllegalStateException("unknown burst-sum feature transform " + var1.getText());
      };
   }

   private static float[] method487(float[] var0, float[][] var1, float[] var2, boolean var3) {
      float[] var4 = new float[var2.length];

      for (int var5 = 0; var5 < var4.length; var5++) {
         double var6 = var2[var5];

         for (int var8 = 0; var8 < var0.length; var8++) {
            var6 += var1[var5][var8] * var0[var8];
         }

         var4[var5] = var3 ? (float)Math.tanh(var6) : (float)var6;
      }

      return var4;
   }

   private static float[] method488(JsonArray var0) {
      float[] var1 = new float[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0.get(var2).getAsFloat();
      }

      return var1;
   }

   private static float[][] method489(JsonArray var0) {
      float[][] var1 = new float[var0.size()][];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = method488(var0.get(var2).getAsJsonArray());
      }

      return var1;
   }

   private static final class Data {
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data(String var1, float var2, float var3, float var4) {
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

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AimPolicy {
   public AimPolicy.Entry[] entrys;
   public int[][] intValues;
   public int[][] intValues2;
   public float floatValue;
   public float floatValue2;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public int intValue9;
   public int intValue10;
   public int intValue11;
   public float[][] floatValues;
   public float[][] floatValues2;
   public float[] floatValues3;
   public float[] floatValues4;
   public float[][] floatValues5;
   public float[] floatValues6;
   public float[][] floatValues7;
   public float[][] floatValues8;
   public float[][] floatValues9;
   public float[][] floatValues10;
   public float[] floatValues11;
   public float[] floatValues12;
   public float[][] floatValues13;
   public float[] floatValues14;
   public float[][] floatValues15;
   public float[] floatValues16;
   public float[][] floatValues17;
   public float[] floatValues18;
   public float[][] floatValues19;
   public float[] floatValues20;
   public float[][] floatValues21;
   public float[] floatValues22;
   private static final String text = "/assets/zenith/aimpipe/aim_policy.json";
   private static final Path path = Path.of("aim_policy.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "test", "aimpipe", "out", "policy", "aim_policy.json"),
      Path.of("scripts", "test", "aimpipe", "out", "policy", "aim_policy.json")
   };

   public static AimPolicy method5432() {
      AimPolicy var0 = method5433(path);
      if (var0 != null) {
         return var0;
      } else {
         var0 = method5434();
         if (var0 != null) {
            return var0;
         } else {
            for (Path var4 : paths) {
               var0 = method5433(var4);
               if (var0 != null) {
                  return var0;
               }
            }

            return null;
         }
      }
   }

   private static AimPolicy method5433(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : method5435(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable var2) {
         System.err.println("[AimPolicy] failed to load " + var0 + ": " + var2);
         return null;
      }
   }

   private static AimPolicy method5434() {
      try {
         AimPolicy var1;
         try (InputStream var0 = AimPolicy.class.getResourceAsStream("/assets/zenith/aimpipe/aim_policy.json")) {
            if (var0 == null) {
               return null;
            }

            var1 = method5435(new String(var0.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/aim_policy.json");
         }

         return var1;
      } catch (Throwable var5) {
         System.err.println("[AimPolicy] failed to load /assets/zenith/aimpipe/aim_policy.json: " + var5);
         return null;
      }
   }

   private static AimPolicy method5435(String var0, String var1) {
      AimPolicy var2 = method5436(JsonParser.parseString(var0).getAsJsonObject());
      System.out
         .println(
            "[AimPolicy] loaded "
               + var1
               + " (planner="
               + var2.intValue3
               + ", motor="
               + var2.intValue4
               + ", bins="
               + var2.intValue5
               + "/"
               + var2.intValue6
               + ", cadence="
               + var2.intValue
               + ", z="
               + var2.intValue10
               + ")"
         );
      return var2;
   }

   private static AimPolicy method5436(JsonObject var0) {
      int var1 = var0.has("format_version") ? var0.get("format_version").getAsInt() : 1;
      if (var1 != 5) {
         throw new IllegalStateException("unsupported artifact format_version " + var1 + " (expected 5; retrain and re-run policy/export.py)");
      } else {
         AimPolicy var2 = new AimPolicy();
         JsonArray var3 = var0.getAsJsonArray("feature_spec");
         var2.entrys = new AimPolicy.Entry[var3.size()];

         for (int var4 = 0; var4 < var3.size(); var4++) {
            JsonObject var5 = var3.get(var4).getAsJsonObject();
            var2.entrys[var4] = new AimPolicy.Entry(
               var5.get("index").getAsInt(),
               "mu".equals(var5.get("transform").getAsString()),
               var5.get("scale").getAsFloat(),
               var5.get("mean").getAsFloat(),
               var5.get("std").getAsFloat()
            );
         }

         var2.intValues = new int[2][];
         var2.intValues2 = new int[2][];
         String[] var9 = new String[]{"bins_yaw", "bins_pitch"};

         for (int var10 = 0; var10 < 2; var10++) {
            JsonArray var6 = var0.getAsJsonArray(var9[var10]);
            var2.intValues[var10] = new int[var6.size()];
            var2.intValues2[var10] = new int[var6.size()];

            for (int var7 = 0; var7 < var6.size(); var7++) {
               JsonObject var8 = var6.get(var7).getAsJsonObject();
               var2.intValues[var10][var7] = var8.get("lo").getAsInt();
               var2.intValues2[var10][var7] = var8.get("hi").getAsInt();
            }
         }

         JsonObject var11 = var0.getAsJsonObject("sampling");
         var2.floatValue = var11.get("temperature").getAsFloat();
         var2.floatValue2 = var11.get("top_p").getAsFloat();
         var2.intValue = var0.getAsJsonObject("planner").get("ticks").getAsInt();
         JsonObject var12 = var0.getAsJsonObject("model");
         var2.intValue2 = var12.get("in_dim").getAsInt();
         var2.intValue3 = var12.get("hidden").getAsInt();
         var2.intValue4 = var12.get("motor_hidden").getAsInt();
         var2.intValue5 = var12.get("n_bins_yaw").getAsInt();
         var2.intValue6 = var12.get("n_bins_pitch").getAsInt();
         var2.intValue7 = var12.get("zero_bin_yaw").getAsInt();
         var2.intValue8 = var12.get("zero_bin_pitch").getAsInt();
         var2.intValue9 = var12.get("bin_embed").getAsInt();
         var2.intValue10 = var12.get("z_dim").getAsInt();
         var2.intValue11 = var12.get("prior_hidden").getAsInt();
         JsonObject var13 = var0.getAsJsonObject("weights");
         var2.floatValues = method5439(var13.getAsJsonArray("planner_weight_ih"));
         var2.floatValues2 = method5439(var13.getAsJsonArray("planner_weight_hh"));
         var2.floatValues3 = method5438(var13.getAsJsonArray("planner_bias_ih"));
         var2.floatValues4 = method5438(var13.getAsJsonArray("planner_bias_hh"));
         var2.floatValues5 = method5439(var13.getAsJsonArray("intent_weight"));
         var2.floatValues6 = method5438(var13.getAsJsonArray("intent_bias"));
         var2.floatValues7 = method5439(var13.getAsJsonArray("yaw_embedding"));
         var2.floatValues8 = method5439(var13.getAsJsonArray("pitch_embedding"));
         var2.floatValues9 = method5439(var13.getAsJsonArray("motor_weight_ih"));
         var2.floatValues10 = method5439(var13.getAsJsonArray("motor_weight_hh"));
         var2.floatValues11 = method5438(var13.getAsJsonArray("motor_bias_ih"));
         var2.floatValues12 = method5438(var13.getAsJsonArray("motor_bias_hh"));
         var2.floatValues13 = method5439(var13.getAsJsonArray("yaw_head_weight"));
         var2.floatValues14 = method5438(var13.getAsJsonArray("yaw_head_bias"));
         var2.floatValues15 = method5439(var13.getAsJsonArray("pitch_context_weight"));
         var2.floatValues16 = method5438(var13.getAsJsonArray("pitch_context_bias"));
         var2.floatValues17 = method5439(var13.getAsJsonArray("pitch_head_weight"));
         var2.floatValues18 = method5438(var13.getAsJsonArray("pitch_head_bias"));
         var2.floatValues19 = method5439(var13.getAsJsonArray("prior_weight_0"));
         var2.floatValues20 = method5438(var13.getAsJsonArray("prior_bias_0"));
         var2.floatValues21 = method5439(var13.getAsJsonArray("prior_weight_1"));
         var2.floatValues22 = method5438(var13.getAsJsonArray("prior_bias_1"));
         var2.method5437();
         return var2;
      }
   }

   private void method5437() {
      if (this.entrys.length == this.intValue2 && this.intValue >= 1) {
         if (this.intValues[0].length != this.intValue5 || this.intValues[1].length != this.intValue6) {
            throw new IllegalStateException("bin table size mismatch");
         } else if (this.intValue7 < 0 || this.intValue7 >= this.intValue5 || this.intValue8 < 0 || this.intValue8 >= this.intValue6) {
            throw new IllegalStateException("zero-bin index out of range");
         } else if (this.floatValues.length == 4 * this.intValue3 && this.floatValues9.length == 3 * this.intValue4) {
            if (this.intValue10 < 1 || this.floatValues9[0].length != this.intValue4 + 2 * this.intValue9 + this.intValue10) {
               throw new IllegalStateException("motor input width must include the gesture latent");
            } else if (this.floatValues19.length != this.intValue11
               || this.floatValues19[0].length != this.intValue4 + 2 * this.intValue9
               || this.floatValues21.length != 2 * this.intValue10
               || this.floatValues21[0].length != this.intValue11) {
               throw new IllegalStateException("gesture prior weight shape mismatch");
            }
         } else {
            throw new IllegalStateException("recurrent weight shape mismatch");
         }
      } else {
         throw new IllegalStateException("feature/planner configuration mismatch");
      }
   }

   private static float[] method5438(JsonArray var0) {
      float[] var1 = new float[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0.get(var2).getAsFloat();
      }

      return var1;
   }

   private static float[][] method5439(JsonArray var0) {
      float[][] var1 = new float[var0.size()][];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = method5438(var0.get(var2).getAsJsonArray());
      }

      return var1;
   }

   public static final class Entry {
      public final int intValue;
      public final boolean flag;
      public final float floatValue;
      public final float floatValue2;
      public final float floatValue3;

      public Entry(int var1, boolean var2, float var3, float var4, float var5) {
         this.intValue = var1;
         this.flag = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
      }
   }
}

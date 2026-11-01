package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PointClickDqn {
   public static final int CONST_intValue = 11;
   public static final int CONST_intValue2 = 50;
   private static final String text = "/assets/zenith/pointclick/point_click_dqn.json";
   private static final Path path = Path.of("point_click_dqn.json");
   private final float[][] floatValues;
   private final float[] floatValues2;
   private final float[][] floatValues3;
   private final float[] floatValues4;

   private PointClickDqn(float[][] var1, float[] var2, float[][] var3, float[] var4) {
      this.floatValues = var1;
      this.floatValues2 = var2;
      this.floatValues3 = var3;
      this.floatValues4 = var4;
   }

   public static PointClickDqn method3223() {
      PointClickDqn var0 = method3224(path);
      if (var0 != null) {
         return var0;
      } else {
         try {
            PointClickDqn var2;
            try (InputStream var1 = PointClickDqn.class.getResourceAsStream("/assets/zenith/pointclick/point_click_dqn.json")) {
               if (var1 == null) {
                  return null;
               }

               var2 = method3225(new String(var1.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/pointclick/point_click_dqn.json");
            }

            return var2;
         } catch (Throwable var6) {
            System.err.println("[PointClick] failed to load /assets/zenith/pointclick/point_click_dqn.json: " + var6);
            return null;
         }
      }
   }

   private static PointClickDqn method3224(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : method3225(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable var2) {
         System.err.println("[PointClick] failed to load " + var0 + ": " + var2);
         return null;
      }
   }

   private static PointClickDqn method3225(String var0, String var1) {
      JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
      if (var2.get("format_version").getAsInt() == 1 && "point_and_click_dqn".equals(var2.get("model").getAsString())) {
         JsonObject var3 = var2.getAsJsonObject("architecture");
         method3231(var3.get("input").getAsInt() == 11, "input size");
         method3231(var3.get("output").getAsInt() == 50, "output size");
         int var4 = var3.get("hidden").getAsInt();
         JsonObject var5 = var2.getAsJsonObject("weights");
         PointClickDqn var6 = new PointClickDqn(
            method3230(var5.getAsJsonArray("hidden_kernel"), 11, var4),
            method3229(var5.getAsJsonArray("hidden_bias"), var4),
            method3230(var5.getAsJsonArray("output_kernel"), var4, 50),
            method3229(var5.getAsJsonArray("output_bias"), 50)
         );
         method3228(var6, var2.getAsJsonArray("parity"));
         System.out.println("[PointClick] loaded DQN " + var1 + " (11x" + var4 + "x50)");
         return var6;
      } else {
         throw new IllegalStateException("unsupported point-click policy artifact");
      }
   }

   public int method3226(float[] var1) {
      float[] var2 = this.method3227(var1);
      int var3 = 0;

      for (int var4 = 1; var4 < var2.length; var4++) {
         if (var2[var4] > var2[var3]) {
            var3 = var4;
         }
      }

      return var3;
   }

   public float[] method3227(float[] var1) {
      if (var1.length != 11) {
         throw new IllegalArgumentException("expected 11 inputs");
      } else {
         float[] var2 = new float[this.floatValues2.length];

         for (int var3 = 0; var3 < var2.length; var3++) {
            float var4 = this.floatValues2[var3];

            for (int var5 = 0; var5 < var1.length; var5++) {
               var4 += var1[var5] * this.floatValues[var5][var3];
            }

            var2[var3] = Math.max(var4, 0.0F);
         }

         float[] var6 = (float[])this.floatValues4.clone();

         for (int var7 = 0; var7 < var6.length; var7++) {
            for (int var8 = 0; var8 < var2.length; var8++) {
               var6[var7] += var2[var8] * this.floatValues3[var8][var7];
            }
         }

         return var6;
      }
   }

   private static void method3228(PointClickDqn var0, JsonArray var1) {
      for (int var2 = 0; var2 < var1.size(); var2++) {
         JsonObject var3 = var1.get(var2).getAsJsonObject();
         float[] var4 = method3229(var3.getAsJsonArray("input"), 11);
         float[] var5 = method3229(var3.getAsJsonArray("q_values"), 50);
         float[] var6 = var0.method3227(var4);
         float var7 = 0.0F;

         for (int var8 = 0; var8 < var6.length; var8++) {
            var7 = Math.max(var7, Math.abs(var6[var8] - var5[var8]));
         }

         method3231(var7 <= 0.002F, "parity case " + var2 + ", error=" + var7);
         method3231(var0.method3226(var4) == var3.get("action").getAsInt(), "parity action " + var2);
      }
   }

   private static float[] method3229(JsonArray var0, int var1) {
      method3231(var0.size() == var1, "vector length");
      float[] var2 = new float[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = var0.get(var3).getAsFloat();
      }

      return var2;
   }

   private static float[][] method3230(JsonArray var0, int var1, int var2) {
      method3231(var0.size() == var1, "matrix rows");
      float[][] var3 = new float[var1][var2];

      for (int var4 = 0; var4 < var1; var4++) {
         var3[var4] = method3229(var0.get(var4).getAsJsonArray(), var2);
      }

      return var3;
   }

   private static void method3231(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalStateException("invalid point-click artifact: " + var1);
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IntentSelector {
   public static final int CONST_intValue = 3;
   public static final int CONST_intValue2 = 4;
   public static final float CONST_floatValue = -2.5F;
   public static final float CONST_floatValue2 = 0.7F;
   private static final int CONST_intValue3 = 0;
   private static final int CONST_intValue4 = 1;
   private static final int CONST_intValue5 = 2;
   public String[] texts;
   public int[] intValues;
   public float[] floatValues;
   public float[] std;
   public float floatValue;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   private float[][] floatValues2;
   private float[] floatValues3;
   private float[] floatValues4;
   private float[] floatValues5;
   private float[][] floatValues6;
   private float[] floatValues7;
   private float[][] floatValues8;
   private float[] floatValues9;
   private float[][] floatValues10;
   private float[] floatValues11;
   private float[][] floatValues12;
   private float[] floatValues13;
   private float[][] floatValues14;
   private float[] floatValues15;
   private static final String text = "/assets/zenith/aimpipe/intent_selector.json";
   private static final Path path = Path.of("intent_selector.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "motor_intents", "out", "intent_selector.json"), Path.of("scripts", "motor_intents", "out", "intent_selector.json")
   };

   public IntentSelector.Entry method7315(float[] var1) {
      float[] var2 = new float[this.intValue];

      for (int var3 = 0; var3 < this.intValue; var3++) {
         float var4 = var1[var3];

         var4 = switch (this.intValues[var3]) {
            case 1 -> method7316(var4);
            case 2 -> (float)Math.log1p(Math.max(var4, 0.0F));
            default -> var4;
         };
         var2[var3] = (var4 - this.floatValues[var3]) / this.std[var3];
      }

      float[] var13 = method7317(this.floatValues2, this.floatValues3, var2);
      method7318(var13, this.floatValues4, this.floatValues5);
      method7319(var13);
      float[] var15 = method7317(this.floatValues6, this.floatValues7, var13);
      method7319(var15);
      IntentSelector.Entry var5 = new IntentSelector.Entry();
      var5.floatValue = method7317(this.floatValues8, this.floatValues9, var15)[0];
      float[] var6 = method7317(this.floatValues10, this.floatValues11, var15);
      System.arraycopy(var6, 0, var5.floatValues, 0, var6.length);
      float[] var7 = method7317(this.floatValues12, this.floatValues13, var15);
      byte var8 = 9;
      var5.floatValues2 = new float[4][this.intValue3][var8];
      int var9 = 0;

      for (int var10 = 0; var10 < 4; var10++) {
         for (int var11 = 0; var11 < this.intValue3; var11++) {
            for (int var12 = 0; var12 < var8; var12++) {
               var5.floatValues2[var10][var11][var12] = var7[var9++];
            }
         }
      }

      float[] var17 = method7317(this.floatValues14, this.floatValues15, var15);
      var5.floatValues3 = new float[this.intValue4][3];
      var9 = 0;

      for (int var18 = 0; var18 < this.intValue4; var18++) {
         for (int var19 = 0; var19 < 3; var19++) {
            var5.floatValues3[var18][var19] = var17[var9++];
         }
      }

      return var5;
   }

   public static float method7316(float var0) {
      return (float)Math.copySign(Math.log1p(Math.abs(var0)), (double)var0);
   }

   private static float[] method7317(float[][] var0, float[] var1, float[] var2) {
      float[] var3 = new float[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         float var5 = var1[var4];
         float[] var6 = var0[var4];

         for (int var7 = 0; var7 < var2.length; var7++) {
            var5 += var6[var7] * var2[var7];
         }

         var3[var4] = var5;
      }

      return var3;
   }

   private static void method7318(float[] var0, float[] var1, float[] var2) {
      double var3 = 0.0;

      for (float var8 : var0) {
         var3 += var8;
      }

      var3 /= var0.length;
      double var14 = 0.0;

      for (float var10 : var0) {
         double var11 = var10 - var3;
         var14 += var11 * var11;
      }

      var14 /= var0.length;
      double var17 = 1.0 / Math.sqrt(var14 + 1.0E-5);

      for (int var19 = 0; var19 < var0.length; var19++) {
         var0[var19] = (float)((var0[var19] - var3) * var17 * var1[var19] + var2[var19]);
      }
   }

   private static void method7319(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         double var2 = var0[var1];
         var0[var1] = (float)(0.5 * var2 * (1.0 + method7320(var2 / Math.sqrt(2.0))));
      }
   }

   private static double method7320(double var0) {
      double var2 = Math.signum(var0);
      double var4 = Math.abs(var0);
      double var6 = 1.0 / (1.0 + 0.3275911 * var4);
      double var8 = 1.0
         - ((((1.061405429 * var6 - 1.453152027) * var6 + 1.421413741) * var6 - 0.284496736) * var6 + 0.254829592) * var6 * Math.exp(-var4 * var4);
      return var2 * var8;
   }

   public static IntentSelector method7321() {
      IntentSelector var0 = method7322(path);
      if (var0 != null) {
         return var0;
      } else {
         for (Path var4 : paths) {
            var0 = method7322(var4);
            if (var0 != null) {
               return var0;
            }
         }

         return method7323();
      }
   }

   private static IntentSelector method7322(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : method7324(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable var2) {
         System.err.println("[MotorIntent] failed to load " + var0 + ": " + var2);
         return null;
      }
   }

   private static IntentSelector method7323() {
      try {
         IntentSelector var1;
         try (InputStream var0 = IntentSelector.class.getResourceAsStream("/assets/zenith/aimpipe/intent_selector.json")) {
            if (var0 == null) {
               return null;
            }

            var1 = method7324(new String(var0.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/intent_selector.json");
         }

         return var1;
      } catch (Throwable var5) {
         System.err.println("[MotorIntent] failed to load /assets/zenith/aimpipe/intent_selector.json: " + var5);
         return null;
      }
   }

   private static IntentSelector method7324(String var0, String var1) {
      JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
      if (!"motor_intent_selector_mlp".equals(var2.get("model").getAsString())) {
         throw new IllegalStateException("unexpected selector model");
      } else if (var2.get("format_version").getAsInt() != 3) {
         throw new IllegalStateException("unsupported selector format_version " + var2.get("format_version").getAsInt() + " (need 3)");
      } else {
         JsonObject var3 = var2.getAsJsonObject("architecture");
         IntentSelector var4 = new IntentSelector();
         var4.intValue = var3.get("input_dim").getAsInt();
         var4.intValue2 = var3.get("hidden").getAsInt();
         var4.intValue3 = var3.get("mixture_components").getAsInt();
         var4.intValue4 = var3.get("rest_mixture_components").getAsInt();
         JsonArray var5 = var3.getAsJsonArray("intents");
         if (var5.size() != 4) {
            throw new IllegalStateException("selector intent taxonomy mismatch");
         } else {
            for (int var6 = 0; var6 < var5.size(); var6++) {
               if (!IntentLibraryRt.texts[var6].equals(var5.get(var6).getAsString())) {
                  throw new IllegalStateException("selector intent order mismatch");
               }
            }

            if (var3.getAsJsonArray("param_names").size() != 4) {
               throw new IllegalStateException("selector param count mismatch");
            } else {
               var4.floatValue = var2.get("move_threshold").getAsFloat();
               JsonArray var12 = var2.getAsJsonArray("feature_spec");
               if (var12.size() != var4.intValue) {
                  throw new IllegalStateException("feature spec size mismatch");
               } else {
                  var4.texts = new String[var12.size()];
                  var4.intValues = new int[var12.size()];
                  var4.floatValues = new float[var12.size()];
                  var4.std = new float[var12.size()];

                  for (int var7 = 0; var7 < var12.size(); var7++) {
                     JsonObject var8 = var12.get(var7).getAsJsonObject();
                     var4.texts[var7] = var8.get("name").getAsString();
                     String var9 = var8.get("transform").getAsString();
                     int[] var10000 = var4.intValues;

                     var10000[var7] = switch (var9) {
                        case "signed_log" -> 1;
                        case "log1p" -> 2;
                        case "linear" -> 0;
                        default -> throw new IllegalStateException("unknown transform " + var9);
                     };
                     var4.floatValues[var7] = var8.get("mean").getAsFloat();
                     var4.std[var7] = var8.get("std").getAsFloat();
                  }

                  JsonObject var13 = var2.getAsJsonObject("weights");
                  var4.floatValues2 = method7325(var13, "backbone.0.weight");
                  var4.floatValues3 = method7326(var13, "backbone.0.bias");
                  var4.floatValues4 = method7326(var13, "backbone.1.weight");
                  var4.floatValues5 = method7326(var13, "backbone.1.bias");
                  var4.floatValues6 = method7325(var13, "backbone.4.weight");
                  var4.floatValues7 = method7326(var13, "backbone.4.bias");
                  var4.floatValues8 = method7325(var13, "move_head.weight");
                  var4.floatValues9 = method7326(var13, "move_head.bias");
                  var4.floatValues10 = method7325(var13, "intent_head.weight");
                  var4.floatValues11 = method7326(var13, "intent_head.bias");
                  var4.floatValues12 = method7325(var13, "params_head.weight");
                  var4.floatValues13 = method7326(var13, "params_head.bias");
                  var4.floatValues14 = method7325(var13, "rest_head.weight");
                  var4.floatValues15 = method7326(var13, "rest_head.bias");
                  int var14 = 4 * var4.intValue3 * 9;
                  if (var4.floatValues12.length != var14) {
                     throw new IllegalStateException("params head size mismatch: " + var4.floatValues12.length + " vs " + var14);
                  } else if (var4.floatValues14.length != var4.intValue4 * 3) {
                     throw new IllegalStateException("rest head size mismatch");
                  } else if (var4.floatValues2[0].length == var4.intValue && var4.floatValues2.length == var4.intValue2) {
                     System.out
                        .println(
                           "[MotorIntent] loaded selector "
                              + var1
                              + " (in="
                              + var4.intValue
                              + ", hidden="
                              + var4.intValue2
                              + ", mixture="
                              + var4.intValue3
                              + ", threshold="
                              + var4.floatValue
                              + ")"
                        );
                     return var4;
                  } else {
                     throw new IllegalStateException("backbone.0 shape mismatch");
                  }
               }
            }
         }
      }
   }

   private static float[][] method7325(JsonObject var0, String var1) {
      JsonArray var2 = var0.getAsJsonArray(var1);
      float[][] var3 = new float[var2.size()][];

      for (int var4 = 0; var4 < var2.size(); var4++) {
         JsonArray var5 = var2.get(var4).getAsJsonArray();
         var3[var4] = new float[var5.size()];

         for (int var6 = 0; var6 < var5.size(); var6++) {
            var3[var4][var6] = var5.get(var6).getAsFloat();
         }
      }

      return var3;
   }

   private static float[] method7326(JsonObject var0, String var1) {
      JsonArray var2 = var0.getAsJsonArray(var1);
      float[] var3 = new float[var2.size()];

      for (int var4 = 0; var4 < var2.size(); var4++) {
         var3[var4] = var2.get(var4).getAsFloat();
      }

      return var3;
   }

   public static final class Entry {
      public float floatValue;
      public final float[] floatValues = new float[4];
      public float[][][] floatValues2;
      public float[][] floatValues3;
   }
}

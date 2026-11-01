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

public final class GhostActorModel {
   public static final String text = "neuraim_java_ghost_actor_v1";
   public static final String text2 = "neuraim_ghost_actor_v1.json";
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   private final String[] texts;
   private final float[] floatValues;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final float[][] floatValues2;
   private final float[][] floatValues3;
   private final float[][] floatValues4;
   private final float[] floatValues5;
   private final float[] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final double doubleValue;
   private final double doubleValue2;
   private final double doubleValue3;
   private final double doubleValue4;
   private final double doubleValue5;
   private final double doubleValue6;
   private final double doubleValue7;
   private final double doubleValue8;

   private GhostActorModel(JsonObject var1) {
      method7491("neuraim_java_ghost_actor_v1".equals(method7494(var1, "format")), "unexpected ghost actor format");
      JsonObject var2 = method7492(var1, "observation");
      JsonArray var3 = method7493(var2, "channels");
      this.texts = new String[var3.size()];

      for (int var4 = 0; var4 < var3.size(); var4++) {
         this.texts[var4] = var3.get(var4).getAsString();
      }

      this.intValue2 = this.texts.length;
      this.floatValues = method7497(var2, "scale", this.intValue2);
      this.intValue = method7496(var2, "frames");
      this.intValue5 = method7496(var2, "target_delay_ticks");
      method7491("concatenated_oldest_first".equals(method7494(var2, "layout")), "unsupported observation layout");
      method7491(this.intValue >= 1 && this.intValue2 >= 1, "empty observation contract");
      this.intValue3 = this.intValue * this.intValue2;
      JsonObject var9 = method7492(var1, "network");
      this.intValue4 = method7496(var9, "hidden");
      method7491("relu".equals(method7494(var9, "activation")), "unsupported activation");
      JsonObject var5 = method7492(var9, "weights");
      this.floatValues2 = method7498(var5, "body.0.weight", this.intValue4, this.intValue3);
      this.floatValues5 = method7497(var5, "body.0.bias", this.intValue4);
      this.floatValues3 = method7498(var5, "body.2.weight", this.intValue4, this.intValue4);
      this.floatValues6 = method7497(var5, "body.2.bias", this.intValue4);
      JsonObject var6 = method7492(var1, "action");
      JsonArray var7 = method7493(var6, "order");
      method7491(
         var7.size() == 5
            && "yaw_counts".equals(var7.get(0).getAsString())
            && "pitch_counts".equals(var7.get(1).getAsString())
            && "forward".equals(var7.get(2).getAsString())
            && "sideways".equals(var7.get(3).getAsString())
            && "hold".equals(var7.get(4).getAsString()),
         "unexpected action channel order"
      );
      this.floatValues4 = method7498(var5, "mean.weight", var7.size(), this.intValue4);
      this.floatValues7 = method7497(var5, "mean.bias", var7.size());
      this.floatValues8 = method7497(var6, "scale", var7.size());
      this.doubleValue = method7495(var6, "mouse_degrees_per_count");
      this.doubleValue2 = method7495(var6, "yaw_cap_counts");
      this.doubleValue3 = method7495(var6, "pitch_cap_counts");
      this.doubleValue4 = method7495(var6, "key_threshold");
      method7491(this.doubleValue > 0.0 && this.doubleValue2 > 0.0 && this.doubleValue3 > 0.0, "non-positive action geometry");
      JsonObject var8 = method7492(method7492(var1, "server_side_rules"), "attack");
      this.doubleValue5 = method7495(var8, "recharge_per_tick");
      this.doubleValue6 = method7495(var8, "threshold_low");
      this.doubleValue7 = method7495(var8, "threshold_high");
      this.doubleValue8 = method7495(var8, "full_charge_probability");
      method7491(this.doubleValue5 > 0.0, "non-positive attack recharge");
   }

   public static GhostActorModel method7481(Path var0) throws IOException {
      GhostActorModel var2;
      try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         var2 = method7483(var1);
      }

      return var2;
   }

   public static GhostActorModel method7482(InputStream var0) throws IOException {
      GhostActorModel var2;
      try (InputStreamReader var1 = new InputStreamReader(var0, StandardCharsets.UTF_8)) {
         var2 = method7483(var1);
      }

      return var2;
   }

   private static GhostActorModel method7483(Reader var0) throws IOException {
      try {
         JsonObject var1 = (JsonObject)new Gson().fromJson(var0, JsonObject.class);
         method7491(var1 != null, "empty model JSON");
         return new GhostActorModel(var1);
      } catch (RuntimeException var2) {
         throw new IOException("invalid ghost actor: " + var2.getMessage(), var2);
      }
   }

   public static GhostActorModel method7484() {
      for (Path var1 : List.of(
         Path.of("models", "neuraim_ghost_actor_v1.json"),
         Path.of("run", "models", "neuraim_ghost_actor_v1.json"),
         Path.of("neuraim_ghost_actor_v1.json"),
         Path.of("..", "models", "neuraim_ghost_actor_v1.json")
      )) {
         if (Files.isRegularFile(var1)) {
            try {
               GhostActorModel var2 = method7481(var1);
               System.out.println("[Neuraim] Loaded ghost actor " + var1.toAbsolutePath());
               return var2;
            } catch (IOException var4) {
               System.err.println("[Neuraim] Failed to load " + var1 + ": " + var4.getMessage());
            }
         }
      }

      InputStream var5 = GhostActorModel.class.getClassLoader().getResourceAsStream("models/neuraim_ghost_actor_v1.json");
      if (var5 != null) {
         try {
            return method7482(var5);
         } catch (IOException var3) {
            System.err.println("[Neuraim] Bundled ghost actor is invalid: " + var3.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_ghost_actor_v1.json not found; TestRotationModeV4 disabled");
      return null;
   }

   public String[] method7485() {
      return (String[])this.texts.clone();
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public int getIntValue5() {
      return this.intValue5;
   }

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public double getDoubleValue5() {
      return this.doubleValue5;
   }

   public double getDoubleValue6() {
      return this.doubleValue6;
   }

   public double getDoubleValue7() {
      return this.doubleValue7;
   }

   public double getDoubleValue8() {
      return this.doubleValue8;
   }

   public GhostActorModel.Data method7486(float[][] var1) {
      method7491(var1 != null && var1.length == this.intValue, "history tick count mismatch");
      float[] var2 = new float[this.intValue3];
      int var3 = 0;

      for (float[] var7 : var1) {
         method7491(var7 != null && var7.length == this.intValue2, "history channel width mismatch");

         for (int var8 = 0; var8 < this.intValue2; var8++) {
            var2[var3++] = var7[var8] / this.floatValues[var8];
         }
      }

      float[] var13 = method7489(method7488(this.floatValues2, this.floatValues5, var2, this.intValue4));
      float[] var14 = method7489(method7488(this.floatValues3, this.floatValues6, var13, this.intValue4));
      float[] var15 = method7488(this.floatValues4, this.floatValues7, var14, this.floatValues7.length);
      double[] var16 = new double[var15.length];

      for (int var17 = 0; var17 < var15.length; var17++) {
         var16[var17] = Math.tanh(var15[var17]) * this.floatValues8[var17];
      }

      boolean var18 = var16[4] > 0.0;
      long var9 = var18 ? 0L : Math.round(method7490(var16[0], -this.doubleValue2, this.doubleValue2));
      long var11 = var18 ? 0L : Math.round(method7490(var16[1], -this.doubleValue3, this.doubleValue3));
      return new GhostActorModel.Data((int)var9, (int)var11, this.method7487(var16[2]), this.method7487(var16[3]), var18);
   }

   private int method7487(double var1) {
      if (var1 > this.doubleValue4) {
         return 1;
      } else {
         return var1 < -this.doubleValue4 ? -1 : 0;
      }
   }

   private static float[] method7488(float[][] var0, float[] var1, float[] var2, int var3) {
      float[] var4 = new float[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         float[] var6 = var0[var5];
         float var7 = var1[var5];

         for (int var8 = 0; var8 < var6.length; var8++) {
            var7 += var6[var8] * var2[var8];
         }

         var4[var5] = var7;
      }

      return var4;
   }

   private static float[] method7489(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] < 0.0F) {
            var0[var1] = 0.0F;
         }
      }

      return var0;
   }

   private static double method7490(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   private static void method7491(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalStateException(var1);
      }
   }

   private static JsonObject method7492(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method7491(var2 != null && var2.isJsonObject(), "missing object " + var1);
      return var2.getAsJsonObject();
   }

   private static JsonArray method7493(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method7491(var2 != null && var2.isJsonArray(), "missing array " + var1);
      return var2.getAsJsonArray();
   }

   private static String method7494(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method7491(var2 != null && var2.isJsonPrimitive(), "missing string " + var1);
      return var2.getAsString();
   }

   private static double method7495(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      method7491(var2 != null && var2.isJsonPrimitive(), "missing number " + var1);
      return var2.getAsDouble();
   }

   private static int method7496(JsonObject var0, String var1) {
      return (int)method7495(var0, var1);
   }

   private static float[] method7497(JsonObject var0, String var1, int var2) {
      JsonArray var3 = method7493(var0, var1);
      method7491(var3.size() == var2, var1 + " must have " + var2 + " entries, got " + var3.size());
      float[] var4 = new float[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.get(var5).getAsFloat();
      }

      return var4;
   }

   private static float[][] method7498(JsonObject var0, String var1, int var2, int var3) {
      JsonArray var4 = method7493(var0, var1);
      method7491(var4.size() == var2, var1 + " must have " + var2 + " rows, got " + var4.size());
      float[][] var5 = new float[var2][var3];

      for (int var6 = 0; var6 < var2; var6++) {
         JsonArray var7 = var4.get(var6).getAsJsonArray();
         method7491(var7.size() == var3, var1 + " row " + var6 + " must have " + var3 + " columns");

         for (int var8 = 0; var8 < var3; var8++) {
            var5[var6][var8] = var7.get(var8).getAsFloat();
         }
      }

      return var5;
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final boolean flag;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "flag=" + this.flag + "]";
      }

      public Data(int var1, int var2, int var3, int var4, boolean var5) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.intValue4 = var4;
         this.flag = var5;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public int getIntValue4() {
         return this.intValue4;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

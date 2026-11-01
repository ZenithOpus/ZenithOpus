package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import org.zenith.ChatLogger;
import org.zenith.Zenith;

public class JointCodebook {
   private static final String[] texts = new String[]{
      "rotation_model_v22_2_joint_codebook.json", "rotation_model_v22_1_joint_codebook.json", "rotation_model_v22_joint_codebook.json"
   };
   private static final int CONST_intValue = 22;
   private static final String text = "joint_nonuniform_codebook_residual_rotation_policy_v22";
   private static final String text2 = "joint_nonuniform_codebook_residual_rotation_policy_v22_1";
   private static final String text3 = "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2";
   private static final String text4 = "joint_codebook_plus_residual_count_policy";
   private static final String text5 = "joint_codebook_plus_class_conditioned_residual_count_policy";
   private static final float CONST_floatValue = 0.5F;
   private boolean loaded = false;
   private JointCodebook.Entry9 entry9;
   private int intValue = 24;
   private final Deque<float[]> deque = new ArrayDeque<>();

   private boolean method2524() {
      try {
         return Zenith.getInstance().getEntry().getUsername().equals("Bogdan");
      } catch (Throwable var2) {
         return false;
      }
   }

   private void method2525(String var1) {
      if (this.method2524()) {
         ChatLogger.method2019(var1);
      }
   }

   private void method2526(String var1) {
      if (this.method2524()) {
         System.out.println(var1);
      }
   }

   private void method2527(String var1) {
      if (this.method2524()) {
         System.err.println(var1);
      }
   }

   private void method2528(Exception var1) {
      if (this.method2524()) {
         var1.printStackTrace();
      }
   }

   public void method2529() {
      this.loaded = false;
      this.entry9 = null;
      this.method2536();

      try {
         for (File var4 : this.method2530()) {
            if (this.method2531(var4)) {
               this.method2525(var4.getAbsolutePath());
               return;
            }
         }

         for (String var9 : texts) {
            if (this.method2532("models/" + var9)) {
               this.method2525("jar:models/" + var9);
               return;
            }
         }

         this.method2527("[DeepLearning] No V22 joint-codebook rotation model found. Checked only V22 filenames; no v17 fallback is attempted.");
      } catch (Exception var5) {
         this.method2527("[DeepLearning] Failed to load V22 rotation model: " + var5.getMessage());
         this.method2528(var5);
      }
   }

   private File[] method2530() {
      String[] var1 = new String[]{"models", "run/models", "scripts", "../models", "../scripts", ""};
      ArrayList var2 = new ArrayList();

      for (String var6 : var1) {
         for (String var10 : texts) {
            var2.add(var6.isEmpty() ? new File(var10) : new File(var6, var10));
         }
      }

      return (File[])var2.toArray(new File[0]);
   }

   private boolean method2531(File var1) {
      if (var1 != null && var1.exists() && var1.isFile()) {
         try {
            this.method2533(var1);
            return true;
         } catch (RuntimeException | IOException var3) {
            this.method2527("[DeepLearning] Failed V22 candidate " + var1.getPath() + ": " + var3.getMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean method2532(String var1) {
      InputStream var2 = JointCodebook.class.getClassLoader().getResourceAsStream(var1);
      if (var2 == null) {
         return false;
      } else {
         try {
            InputStream var3 = var2;

            boolean var4;
            try {
               this.method2526("[DeepLearning] Loading V22 rotation model from jar: " + var1);
               this.method2534(var2);
               var4 = true;
            } catch (Throwable var7) {
               if (var2 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var2 != null) {
               var2.close();
            }

            return var4;
         } catch (RuntimeException | IOException var8) {
            this.method2527("[DeepLearning] Failed V22 resource " + var1 + ": " + var8.getMessage());
            return false;
         }
      }
   }

   public void method2533(File var1) throws IOException {
      if (var1 != null && var1.exists() && var1.isFile()) {
         try (FileInputStream var2 = new FileInputStream(var1)) {
            this.method2526("[DeepLearning] Loading V22 rotation model from: " + var1.getPath());
            this.method2534(var2);
         }
      } else {
         throw new IOException("Model file not found");
      }
   }

   public void method2534(InputStream var1) throws IOException {
      String var2 = new String(var1.readAllBytes(), StandardCharsets.UTF_8);
      if (!this.method2591(var2)) {
         throw new IOException("Unsupported V22 rotation model payload format");
      } else {
         this.method2535(var2);
      }
   }

   private void method2535(String var1) {
      JsonObject var2 = (JsonObject)new Gson().fromJson(var1, JsonObject.class);
      JointCodebook.Entry9 var3 = this.method2560(var2);
      this.entry9 = var3;
      this.intValue = Math.max(1, var3.intValue2);
      this.loaded = true;
      this.method2536();
      this.method2526("[DeepLearning] V22 model loaded successfully: " + var3.text);
      this.method2526("[DeepLearning] Input: " + this.method2537() + ", sequence: " + this.intValue + ", horizon: " + var3.intValue3);
      this.method2526(
         "[DeepLearning] Hidden: " + var3.intValue5 + ", FC: " + var3.intValue7 + ", layers: " + var3.intValue6 + ", joint codes: " + var3.intValue8
      );
      this.method2526("[DeepLearning] Decode: " + var3.text3 + ", topk=" + var3.intValue9 + ", class-conditioned residual=" + var3.flag);
   }

   public void method2536() {
      this.deque.clear();
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public int method2537() {
      return this.entry9 == null ? -1 : this.entry9.intValue4;
   }

   public int method2538() {
      return this.method2537();
   }

   public String[] method2539() {
      return this.entry9 != null && this.entry9.texts != null ? (String[])this.entry9.texts.clone() : new String[0];
   }

   public float method2540() {
      return this.entry9 == null ? 0.0F : this.entry9.floatValue5;
   }

   public float method2541() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue : 850.0F;
   }

   public float method2542() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue2 : 650.0F;
   }

   public float method2543() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue3 : 250.0F;
   }

   public float method2544() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue4 : 0.6F;
   }

   public boolean method2545() {
      return this.loaded && this.entry9 != null && this.method2568(this.entry9.text);
   }

   public JointCodebook.Entry7 method2546(float[] var1, float var2, float var3) {
      if (this.method2545() && var1 != null) {
         if (var1.length != this.entry9.intValue4) {
            this.method2527("[DeepLearning] Invalid V22 feature count: " + var1.length + ", expected " + this.entry9.intValue4);
            return null;
         } else if (this.method2588(var1) && this.isFinite(var2) && this.isFinite(var3)) {
            this.deque.addLast((float[])var1.clone());
            this.method2557();
            float[] var4 = this.method2554(this.method2556());
            return var4 != null && this.method2588(var4) ? this.method2547(var4, var2, var3) : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private JointCodebook.Entry7 method2547(float[] var1, float var2, float var3) {
      float[] var4 = this.method2559(var1, this.entry9.entry83.floatValues, this.entry9.entry83.floatValues2);
      if (var4.length >= this.entry9.intValue8 && this.method2588(var4)) {
         float[] var5 = new float[this.entry9.intValue8];
         System.arraycopy(var4, 0, var5, 0, this.entry9.intValue8);
         float[] var6 = this.method2559(var1, this.entry9.entry84.floatValues, this.entry9.entry84.floatValues2);
         if (!this.method2588(var6)) {
            return null;
         } else {
            JointCodebook.Entry2 var7 = this.entry9.flag ? this.method2549(var5, var6) : this.method2548(var5, var6);
            if (var7 != null && this.method2588(var7.floatValues) && this.method2588(var7.floatValues2) && this.method2588(var7.floatValues3)) {
               float[] var8 = (float[])var7.floatValues.clone();
               this.method2553(var8);
               float var9 = var8[0] * this.entry9.floatValue5;
               float var10 = var8[1] * this.entry9.floatValue5;
               if (this.isFinite(var9) && this.isFinite(var10)) {
                  float var11 = this.method2589((Math.abs(var8[0]) - this.entry9.floatValue6) * 3.0F);
                  float var12 = this.method2589((Math.abs(var8[1]) - this.entry9.floatValue6) * 3.0F);
                  float var13 = this.method2589((Math.max(Math.abs(var8[0]), Math.abs(var8[1])) - this.entry9.floatValue6) * 3.0F);
                  return new JointCodebook.Entry7(
                     var9,
                     var10,
                     var9 - var2,
                     var10 - var3,
                     var11,
                     var12,
                     var13,
                     var7.floatValues2[0],
                     var7.floatValues2[1],
                     var7.floatValues3[0],
                     var7.floatValues3[1],
                     var8[0],
                     var8[1]
                  );
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private JointCodebook.Entry2 method2548(float[] var1, float[] var2) {
      if (var2.length < 2) {
         return null;
      } else {
         JointCodebook.Entry3 var3 = this.method2551(var1);
         if (var3 == null) {
            return null;
         } else {
            float[] var4 = this.method2552(var3.floatValues2);
            float[] var5 = new float[]{this.tanh(var2[0]) * var4[0], this.tanh(var2[1]) * var4[1]};
            return new JointCodebook.Entry2(new float[]{var3.floatValues[0] + var5[0], var3.floatValues[1] + var5[1]}, var3.floatValues, var5, var3.floatValue);
         }
      }
   }

   private JointCodebook.Entry2 method2549(float[] var1, float[] var2) {
      if (var2.length < this.entry9.intValue8 * 2) {
         return null;
      } else {
         float[] var3 = this.method2585(var1, this.entry9.floatValue);
         if (var3 == null) {
            return null;
         } else {
            String var4 = this.entry9.text3;
            if ("argmax".equals(var4) || "sample".equals(var4)) {
               int var17 = this.method2586(var1);
               float[] var19 = this.method2550(var2, var17);
               float[] var21 = (float[])this.entry9.floatValues[var17].clone();
               return new JointCodebook.Entry2(new float[]{var21[0] + var19[0], var21[1] + var19[1]}, var21, var19, var3[var17]);
            } else if ("topk".equals(var4)) {
               int var16 = Math.min(Math.max(1, this.entry9.intValue9), this.entry9.intValue8);
               int[] var18 = this.method2587(var1, var16);
               float[] var20 = new float[var18.length];

               for (int var22 = 0; var22 < var18.length; var22++) {
                  var20[var22] = var1[var18[var22]];
               }

               float[] var23 = this.method2585(var20, this.entry9.floatValue);
               if (var23 == null) {
                  return null;
               } else {
                  float[] var24 = new float[2];
                  float[] var25 = new float[2];
                  float var11 = 0.0F;

                  for (int var12 = 0; var12 < var18.length; var12++) {
                     int var13 = var18[var12];
                     float[] var14 = this.method2550(var2, var13);
                     float var15 = var23[var12];
                     var25[0] += this.entry9.floatValues[var13][0] * var15;
                     var25[1] += this.entry9.floatValues[var13][1] * var15;
                     var24[0] += (this.entry9.floatValues[var13][0] + var14[0]) * var15;
                     var24[1] += (this.entry9.floatValues[var13][1] + var14[1]) * var15;
                     var11 = Math.max(var11, var3[var13]);
                  }

                  return new JointCodebook.Entry2(var24, var25, new float[]{var24[0] - var25[0], var24[1] - var25[1]}, var11);
               }
            } else {
               float[] var5 = new float[2];
               float[] var6 = new float[2];
               float var7 = 0.0F;

               for (int var8 = 0; var8 < this.entry9.intValue8; var8++) {
                  float var9 = var3[var8];
                  float[] var10 = this.method2550(var2, var8);
                  var6[0] += this.entry9.floatValues[var8][0] * var9;
                  var6[1] += this.entry9.floatValues[var8][1] * var9;
                  var5[0] += (this.entry9.floatValues[var8][0] + var10[0]) * var9;
                  var5[1] += (this.entry9.floatValues[var8][1] + var10[1]) * var9;
                  var7 = Math.max(var7, var9);
               }

               return new JointCodebook.Entry2(var5, var6, new float[]{var5[0] - var6[0], var5[1] - var6[1]}, var7);
            }
         }
      }
   }

   private float[] method2550(float[] var1, int var2) {
      float[] var3 = this.method2552(this.entry9.floatValues2[var2]);
      int var4 = var2 * 2;
      return new float[]{this.tanh(var1[var4]) * var3[0], this.tanh(var1[var4 + 1]) * var3[1]};
   }

   private JointCodebook.Entry3 method2551(float[] var1) {
      float[] var2 = this.method2585(var1, this.entry9.floatValue);
      if (var2 == null) {
         return null;
      } else {
         String var3 = this.entry9.text3;
         if ("argmax".equals(var3) || "sample".equals(var3)) {
            int var15 = this.method2586(var1);
            return new JointCodebook.Entry3((float[])this.entry9.floatValues[var15].clone(), (float[])this.entry9.floatValues2[var15].clone(), var2[var15]);
         } else if ("topk".equals(var3)) {
            int var14 = Math.min(Math.max(1, this.entry9.intValue9), this.entry9.intValue8);
            int[] var16 = this.method2587(var1, var14);
            float[] var17 = new float[var16.length];

            for (int var18 = 0; var18 < var16.length; var18++) {
               var17[var18] = var1[var16[var18]];
            }

            float[] var19 = this.method2585(var17, this.entry9.floatValue);
            if (var19 == null) {
               return null;
            } else {
               float[] var20 = new float[2];
               float[] var9 = new float[2];
               float var10 = 0.0F;

               for (int var11 = 0; var11 < var16.length; var11++) {
                  int var12 = var16[var11];
                  float var13 = var19[var11];
                  var20[0] += this.entry9.floatValues[var12][0] * var13;
                  var20[1] += this.entry9.floatValues[var12][1] * var13;
                  var9[0] += this.entry9.floatValues2[var12][0] * var13;
                  var9[1] += this.entry9.floatValues2[var12][1] * var13;
                  var10 = Math.max(var10, var2[var12]);
               }

               return new JointCodebook.Entry3(var20, var9, var10);
            }
         } else {
            float[] var4 = new float[2];
            float[] var5 = new float[2];
            float var6 = 0.0F;

            for (int var7 = 0; var7 < this.entry9.intValue8; var7++) {
               float var8 = var2[var7];
               var4[0] += this.entry9.floatValues[var7][0] * var8;
               var4[1] += this.entry9.floatValues[var7][1] * var8;
               var5[0] += this.entry9.floatValues2[var7][0] * var8;
               var5[1] += this.entry9.floatValues2[var7][1] * var8;
               var6 = Math.max(var6, var8);
            }

            return new JointCodebook.Entry3(var4, var5, var6);
         }
      }
   }

   private float[] method2552(float[] var1) {
      return new float[]{
         this.clamp(var1[0] * this.entry9.floatValue2, this.entry9.floatValue3, this.entry9.floatValue4),
         this.clamp(var1[1] * this.entry9.floatValue2, this.entry9.floatValue3, this.entry9.floatValue4)
      };
   }

   private void method2553(float[] var1) {
      if (var1 != null && var1.length >= 2 && this.entry9.entry5 != null && this.entry9.entry5.enabled) {
         var1[0] = this.clamp(var1[0], -this.entry9.entry5.floatValues[0], this.entry9.entry5.floatValues[0]);
         var1[1] = this.clamp(var1[1], -this.entry9.entry5.floatValues[1], this.entry9.entry5.floatValues[1]);
      }
   }

   private float[] method2554(float[][] var1) {
      if (var1 != null && this.entry9 != null && this.entry9.entry6 != null) {
         float[][] var2 = new float[var1.length][];

         for (int var3 = 0; var3 < var1.length; var3++) {
            var2[var3] = this.method2555(var1[var3], this.entry9.entry6);
         }

         for (int var8 = 0; var8 < this.entry9.intValue6; var8++) {
            JointCodebook.Entry4 var4 = this.entry9.entry4s[var8];
            float[] var5 = new float[this.entry9.intValue5];
            float[][] var6 = new float[var1.length][this.entry9.intValue5];

            for (int var7 = 0; var7 < var1.length; var7++) {
               var5 = this.method2558(var2[var7], var5, var4, this.entry9.intValue5);
               var6[var7] = (float[])var5.clone();
            }

            var2 = var6;
         }

         float[] var9 = var2[var2.length - 1];
         float[] var10 = this.method2590(this.method2559(var9, this.entry9.entry8.floatValues, this.entry9.entry8.floatValues2));
         return this.method2590(this.method2559(var10, this.entry9.entry82.floatValues, this.entry9.entry82.floatValues2));
      } else {
         return null;
      }
   }

   private float[] method2555(float[] var1, JointCodebook.Entry6 var2) {
      float[] var3 = new float[var1.length];

      for (int var4 = 0; var4 < var1.length; var4++) {
         float var5 = Math.max(Math.abs(var2.floatValues2[var4]), 1.0E-6F);
         var3[var4] = (var1[var4] - var2.floatValues[var4]) / var5;
      }

      return var3;
   }

   private float[][] method2556() {
      float[][] var1 = new float[this.intValue][this.entry9.intValue4];
      if (this.deque.isEmpty()) {
         return var1;
      } else {
         float[] var2 = this.deque.peekFirst();
         int var3 = this.intValue - this.deque.size();

         for (int var4 = 0; var4 < var3; var4++) {
            var1[var4] = (float[])var2.clone();
         }

         int var7 = Math.max(0, var3);

         for (float[] var6 : this.deque) {
            if (var7 >= var1.length) {
               break;
            }

            var1[var7++] = (float[])var6.clone();
         }

         return var1;
      }
   }

   private void method2557() {
      while (this.deque.size() > this.intValue) {
         this.deque.removeFirst();
      }
   }

   private float[] method2558(float[] var1, float[] var2, JointCodebook.Entry4 var3, int var4) {
      float[] var5 = this.method2559(var1, var3.floatValues, var3.floatValues3);
      float[] var6 = this.method2559(var2, var3.floatValues2, var3.floatValues4);
      float[] var7 = new float[var4];

      for (int var8 = 0; var8 < var4; var8++) {
         float var9 = this.method2589(var5[var8] + var6[var8]);
         float var10 = this.method2589(var5[var4 + var8] + var6[var4 + var8]);
         float var11 = (float)Math.tanh(var5[var4 * 2 + var8] + var9 * var6[var4 * 2 + var8]);
         var7[var8] = (1.0F - var10) * var11 + var10 * var2[var8];
      }

      return var7;
   }

   private float[] method2559(float[] var1, float[][] var2, float[] var3) {
      int var4 = var2.length;
      float[] var5 = new float[var4];

      for (int var6 = 0; var6 < var4; var6++) {
         float var7 = var3 != null && var6 < var3.length ? var3[var6] : 0.0F;
         float[] var8 = var2[var6];
         int var9 = Math.min(var1.length, var8.length);

         for (int var10 = 0; var10 < var9; var10++) {
            var7 += var8[var10] * var1[var10];
         }

         var5[var6] = var7;
      }

      return var5;
   }

   private JointCodebook.Entry9 method2560(JsonObject var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Model JSON is empty");
      } else {
         String var2 = this.method2575(var1, "model_type");
         if (!this.method2568(var2)) {
            throw new IllegalArgumentException("Unsupported V22 model_type: " + var2);
         } else {
            int var3 = this.method2577(var1, "schema_version");
            if (var3 != 22) {
               throw new IllegalArgumentException("Unsupported V22 model schema_version: " + var3);
            } else {
               String var4 = this.method2575(var1, "output_mode");
               if (!"joint_codebook_plus_residual_count_policy".equals(var4) && !"joint_codebook_plus_class_conditioned_residual_count_policy".equals(var4)) {
                  throw new IllegalArgumentException("Unsupported V22 output_mode: " + var4);
               } else {
                  JsonObject var5 = this.method2570(var1, "normalization");
                  JsonObject var6 = this.method2570(var1, "architecture");
                  JsonObject var7 = this.method2570(var1, "policy_config");
                  JsonObject var8 = this.method2570(var1, "state_dict");
                  int var9 = this.method2577(var7, "schema_version");
                  if (var9 != 22) {
                     throw new IllegalArgumentException("Unsupported policy_config schema_version: " + var9);
                  } else {
                     JointCodebook.Entry9 var10 = new JointCodebook.Entry9();
                     var10.text = var2;
                     var10.text2 = var4;
                     var10.intValue = var3;
                     var10.intValue2 = this.method2577(var1, "window");
                     var10.intValue3 = this.method2577(var1, "future_horizon");
                     var10.intValue4 = this.method2577(var6, "input_size");
                     var10.intValue5 = this.method2577(var6, "hidden");
                     var10.intValue6 = this.method2577(var6, "layers");
                     var10.intValue7 = this.method2577(var6, "fc");
                     var10.intValue8 = this.method2577(var6, "joint_codes");
                     var10.floatValue = Math.max(0.05F, this.method2580(var6, "joint_temperature", 1.0F));
                     var10.text3 = this.method2569(this.method2576(var6, "joint_inference_mode", "topk"));
                     var10.intValue9 = Math.max(1, this.method2578(var6, "joint_inference_topk", 5));
                     var10.floatValue2 = Math.max(0.0F, this.method2580(var6, "residual_width_multiplier", 1.0F));
                     var10.floatValue3 = Math.max(0.0F, this.method2580(var6, "residual_min_count", 0.15F));
                     var10.floatValue4 = Math.max(var10.floatValue3, this.method2580(var6, "residual_max_count", 24.0F));
                     var10.texts = this.method2582(this.method2572(var1, "feature_columns"));
                     var10.texts2 = this.method2582(this.method2572(var1, "target_columns"));
                     var10.floatValue5 = this.method2579(var1, "mouse_gcd");
                     var10.floatValue6 = this.method2580(var7, "move_threshold", 0.5F);
                     var10.flag = "joint_codebook_plus_class_conditioned_residual_count_policy".equals(var4)
                        || "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2".equals(var2);
                     if (this.isFinite(var10.floatValue5) && !(var10.floatValue5 <= 0.0F)) {
                        if (var10.texts.length != var10.intValue4) {
                           throw new IllegalArgumentException("Model feature_columns size mismatch: " + var10.texts.length + " != " + var10.intValue4);
                        } else if (var10.intValue8 <= 0) {
                           throw new IllegalArgumentException("Model architecture has invalid joint_codes");
                        } else {
                           var10.entry6 = this.method2564(var5);
                           if (var10.entry6.floatValues.length == var10.intValue4 && var10.entry6.floatValues2.length == var10.intValue4) {
                              JsonObject var11 = this.method2570(var7, "joint_codebook");
                              var10.floatValues = this.method2584(this.method2572(var11, "centers"));
                              var10.floatValues2 = this.method2584(this.method2572(var11, "residual_limits"));
                              var10.floatValues3 = this.method2583(this.method2573(var11, "scales"));
                              if (var10.floatValues.length == var10.intValue8 && var10.floatValues2.length == var10.intValue8) {
                                 for (int var12 = 0; var12 < var10.intValue8; var12++) {
                                    if (var10.floatValues[var12].length != 2 || var10.floatValues2[var12].length != 2) {
                                       throw new IllegalArgumentException("joint_codebook centers/residual_limits must be [K][2]");
                                    }
                                 }

                                 var10.entry4s = this.method2562(this.method2572(var8, "gru_layers"), var10.intValue6);
                                 var10.entry8 = this.method2563(this.method2570(var8, "fc1"));
                                 var10.entry82 = this.method2563(this.method2570(var8, "fc2"));
                                 var10.entry83 = this.method2563(this.method2570(var8, "joint_code_head"));
                                 var10.entry84 = this.method2563(this.method2570(var8, "count_residual_head"));
                                 int var13 = var10.flag ? var10.intValue3 * var10.intValue8 * 2 : var10.intValue3 * 2;
                                 if (var10.entry84.floatValues2.length == var10.intValue3 * var10.intValue8 * 2) {
                                    var10.flag = true;
                                    var13 = var10.intValue3 * var10.intValue8 * 2;
                                 }

                                 if (var10.entry83.floatValues2.length < var10.intValue3 * var10.intValue8) {
                                    throw new IllegalArgumentException("joint_code_head output is too small");
                                 } else if (var10.entry84.floatValues2.length < var13) {
                                    throw new IllegalArgumentException("count_residual_head output is too small for this V22 model");
                                 } else {
                                    var10.entry5 = this.method2565(this.method2571(var7, "runtime_safety"));
                                    var10.entry = this.method2566(this.method2571(var7, "runtime_feature_builder"));
                                    this.method2561(var10);
                                    return var10;
                                 }
                              } else {
                                 throw new IllegalArgumentException("joint_codebook count mismatch");
                              }
                           } else {
                              throw new IllegalArgumentException("V22 normalization size mismatch");
                           }
                        }
                     } else {
                        throw new IllegalArgumentException("Model config has invalid mouse_gcd");
                     }
                  }
               }
            }
         }
      }
   }

   private void method2561(JointCodebook.Entry9 var1) {
      if (var1.entry8.floatValues.length != var1.intValue7 || var1.entry82.floatValues.length != var1.intValue7) {
         throw new IllegalArgumentException("FC layer output size mismatch");
      } else if (var1.entry83.floatValues.length < var1.intValue3 * var1.intValue8) {
         throw new IllegalArgumentException("joint_code_head weight output size mismatch");
      } else if (var1.intValue5 <= 0 || var1.intValue7 <= 0 || var1.intValue6 <= 0) {
         throw new IllegalArgumentException("Invalid V22 architecture sizes");
      }
   }

   private JointCodebook.Entry4[] method2562(JsonArray var1, int var2) {
      if (var1.size() != var2) {
         throw new IllegalArgumentException("gru_layers count mismatch");
      } else {
         JointCodebook.Entry4[] var3 = new JointCodebook.Entry4[var2];

         for (int var4 = 0; var4 < var2; var4++) {
            JsonObject var5 = var1.get(var4).getAsJsonObject();
            JointCodebook.Entry4 var6 = new JointCodebook.Entry4();
            var6.floatValues = this.method2584(this.method2572(var5, "weight_ih"));
            var6.floatValues2 = this.method2584(this.method2572(var5, "weight_hh"));
            var6.floatValues3 = this.method2583(this.method2572(var5, "bias_ih"));
            var6.floatValues4 = this.method2583(this.method2572(var5, "bias_hh"));
            var3[var4] = var6;
         }

         return var3;
      }
   }

   private JointCodebook.Entry8 method2563(JsonObject var1) {
      float[][] var2 = this.method2584(this.method2572(var1, "weight"));
      float[] var3 = this.method2583(this.method2572(var1, "bias"));
      if (var2.length != var3.length) {
         throw new IllegalArgumentException("Linear layer weight/bias size mismatch");
      } else {
         return new JointCodebook.Entry8(var2, var3);
      }
   }

   private JointCodebook.Entry6 method2564(JsonObject var1) {
      return new JointCodebook.Entry6(this.method2583(this.method2572(var1, "mean")), this.method2583(this.method2572(var1, "std")));
   }

   private JointCodebook.Entry5 method2565(JsonObject var1) {
      JointCodebook.Entry5 var2 = new JointCodebook.Entry5();
      var2.enabled = false;
      var2.floatValues = new float[]{720.0F, 360.0F};
      if (var1 == null) {
         return var2;
      } else {
         var2.enabled = this.method2581(var1, "enabled", true);
         float[] var3 = this.method2583(this.method2573(var1, "max_abs_count"));
         if (var3.length >= 2 && this.isFinite(var3[0]) && this.isFinite(var3[1]) && var3[0] > 0.0F && var3[1] > 0.0F) {
            var2.floatValues = new float[]{Math.abs(var3[0]), Math.abs(var3[1])};
         }

         return var2;
      }
   }

   private JointCodebook.Entry method2566(JsonObject var1) {
      JointCodebook.Entry var2 = new JointCodebook.Entry();
      var2.floatValue = 850.0F;
      var2.floatValue2 = 650.0F;
      var2.floatValue3 = 250.0F;
      var2.floatValue4 = 0.6F;
      if (var1 == null) {
         return var2;
      } else {
         var2.floatValue = this.method2567(this.method2580(var1, "max_stable_width_yaw_count", var2.floatValue), var2.floatValue);
         var2.floatValue2 = this.method2567(this.method2580(var1, "max_stable_height_pitch_count", var2.floatValue2), var2.floatValue2);
         var2.floatValue3 = this.method2567(this.method2580(var1, "max_stable_inside_center_count", var2.floatValue3), var2.floatValue3);
         var2.floatValue4 = Math.max(0.05F, this.method2567(this.method2580(var1, "close_xz_threshold", var2.floatValue4), var2.floatValue4));
         return var2;
      }
   }

   private float method2567(float var1, float var2) {
      return this.isFinite(var1) && var1 > 0.0F ? var1 : var2;
   }

   private boolean method2568(String var1) {
      return "joint_nonuniform_codebook_residual_rotation_policy_v22".equals(var1)
         || "joint_nonuniform_codebook_residual_rotation_policy_v22_1".equals(var1)
         || "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2".equals(var1);
   }

   private String method2569(String var1) {
      if (var1 == null) {
         return "topk";
      } else {
         String var2 = var1.toLowerCase();
         return !"soft".equals(var2) && !"argmax".equals(var2) && !"topk".equals(var2) && !"sample".equals(var2) ? "topk" : var2;
      }
   }

   private JsonObject method2570(JsonObject var1, String var2) {
      JsonElement var3 = this.method2574(var1, var2);
      if (!var3.isJsonObject()) {
         throw new IllegalArgumentException("Model JSON field is not an object: " + var2);
      } else {
         return var3.getAsJsonObject();
      }
   }

   private JsonObject method2571(JsonObject var1, String var2) {
      if (var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull()) {
         JsonElement var3 = var1.get(var2);
         return var3.isJsonObject() ? var3.getAsJsonObject() : null;
      } else {
         return null;
      }
   }

   private JsonArray method2572(JsonObject var1, String var2) {
      JsonElement var3 = this.method2574(var1, var2);
      if (!var3.isJsonArray()) {
         throw new IllegalArgumentException("Model JSON field is not an array: " + var2);
      } else {
         return var3.getAsJsonArray();
      }
   }

   private JsonArray method2573(JsonObject var1, String var2) {
      if (var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull()) {
         JsonElement var3 = var1.get(var2);
         return var3.isJsonArray() ? var3.getAsJsonArray() : new JsonArray();
      } else {
         return new JsonArray();
      }
   }

   private JsonElement method2574(JsonObject var1, String var2) {
      if (var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull()) {
         return var1.get(var2);
      } else {
         throw new IllegalArgumentException("Model JSON missing field: " + var2);
      }
   }

   private String method2575(JsonObject var1, String var2) {
      return this.method2574(var1, var2).getAsString();
   }

   private String method2576(JsonObject var1, String var2, String var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsString() : var3;
   }

   private int method2577(JsonObject var1, String var2) {
      return this.method2574(var1, var2).getAsInt();
   }

   private int method2578(JsonObject var1, String var2, int var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsInt() : var3;
   }

   private float method2579(JsonObject var1, String var2) {
      return this.method2574(var1, var2).getAsFloat();
   }

   private float method2580(JsonObject var1, String var2, float var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsFloat() : var3;
   }

   private boolean method2581(JsonObject var1, String var2, boolean var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsBoolean() : var3;
   }

   private String[] method2582(JsonArray var1) {
      String[] var2 = new String[var1.size()];

      for (int var3 = 0; var3 < var1.size(); var3++) {
         var2[var3] = var1.get(var3).getAsString();
      }

      return var2;
   }

   private float[] method2583(JsonArray var1) {
      if (var1 == null) {
         return new float[0];
      } else {
         float[] var2 = new float[var1.size()];

         for (int var3 = 0; var3 < var1.size(); var3++) {
            var2[var3] = var1.get(var3).getAsFloat();
         }

         return var2;
      }
   }

   private float[][] method2584(JsonArray var1) {
      if (var1 == null) {
         return new float[0][0];
      } else {
         float[][] var2 = new float[var1.size()][];

         for (int var3 = 0; var3 < var1.size(); var3++) {
            var2[var3] = this.method2583(var1.get(var3).getAsJsonArray());
         }

         return var2;
      }
   }

   private float[] method2585(float[] var1, float var2) {
      if (var1 != null && var1.length != 0) {
         float var3 = Math.max(0.05F, var2);
         float var4 = -Float.MAX_VALUE;

         for (float var8 : var1) {
            float var9 = var8 / var3;
            if (this.isFinite(var9)) {
               var4 = Math.max(var4, var9);
            }
         }

         if (!this.isFinite(var4)) {
            return null;
         } else {
            float var10 = 0.0F;
            float[] var11 = new float[var1.length];

            for (int var12 = 0; var12 < var1.length; var12++) {
               float var14 = (float)Math.exp(var1[var12] / var3 - var4);
               var11[var12] = var14;
               var10 += var14;
            }

            if (this.isFinite(var10) && !(var10 <= 0.0F)) {
               for (int var13 = 0; var13 < var11.length; var13++) {
                  var11[var13] /= var10;
               }

               return var11;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private int method2586(float[] var1) {
      int var2 = 0;
      float var3 = -Float.MAX_VALUE;

      for (int var4 = 0; var4 < var1.length; var4++) {
         if (var1[var4] > var3) {
            var3 = var1[var4];
            var2 = var4;
         }
      }

      return var2;
   }

   private int[] method2587(float[] var1, int var2) {
      int var3 = Math.min(Math.max(1, var2), var1.length);
      int[] var4 = new int[var3];
      float[] var5 = new float[var3];

      for (int var6 = 0; var6 < var3; var6++) {
         var4[var6] = -1;
         var5[var6] = -Float.MAX_VALUE;
      }

      for (int var9 = 0; var9 < var1.length; var9++) {
         float var7 = var1[var9];
         if (!(var7 <= var5[var3 - 1])) {
            int var8;
            for (var8 = var3 - 1; var8 > 0 && var7 > var5[var8 - 1]; var8--) {
               var5[var8] = var5[var8 - 1];
               var4[var8] = var4[var8 - 1];
            }

            var5[var8] = var7;
            var4[var8] = var9;
         }
      }

      for (int var10 = 0; var10 < var3; var10++) {
         if (var4[var10] < 0) {
            var4[var10] = 0;
         }
      }

      return var4;
   }

   private boolean method2588(float[] var1) {
      if (var1 == null) {
         return false;
      } else {
         for (float var5 : var1) {
            if (!this.isFinite(var5)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   private float clamp(float var1, float var2, float var3) {
      if (var2 > var3) {
         float var4 = var2;
         var2 = var3;
         var3 = var4;
      }

      return Math.max(var2, Math.min(var3, var1));
   }

   private float method2589(float var1) {
      if (var1 >= 0.0F) {
         float var3 = (float)Math.exp(-var1);
         return 1.0F / (1.0F + var3);
      } else {
         float var2 = (float)Math.exp(var1);
         return var2 / (1.0F + var2);
      }
   }

   private float tanh(float var1) {
      return (float)Math.tanh(var1);
   }

   private float[] method2590(float[] var1) {
      float[] var2 = new float[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         float var4 = var1[var3];
         var2[var3] = 0.5F * var4 * (1.0F + (float)Math.tanh(Math.sqrt(0.6366197723675814) * (var4 + 0.044715F * var4 * var4 * var4)));
      }

      return var2;
   }

   private boolean method2591(String var1) {
      if (var1 == null) {
         return false;
      } else {
         for (int var2 = 0; var2 < var1.length(); var2++) {
            char var3 = var1.charAt(var2);
            if (var3 != '\ufeff' && !Character.isWhitespace(var3)) {
               return var3 == '{';
            }
         }

         return false;
      }
   }

   private static final class Entry {
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
   }

   private static final class Entry2 {
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float[] floatValues3;
      public final float floatValue;

      private Entry2(float[] var1, float[] var2, float[] var3, float var4) {
         this.floatValues = var1;
         this.floatValues2 = var2;
         this.floatValues3 = var3;
         this.floatValue = var4;
      }
   }

   private static final class Entry3 {
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float floatValue;

      private Entry3(float[] var1, float[] var2, float var3) {
         this.floatValues = var1;
         this.floatValues2 = var2;
         this.floatValue = var3;
      }
   }

   private static final class Entry4 {
      public float[][] floatValues;
      public float[][] floatValues2;
      public float[] floatValues3;
      public float[] floatValues4;
   }

   private static final class Entry5 {
      public boolean enabled;
      public float[] floatValues;
   }

   private static final class Entry6 {
      public final float[] floatValues;
      public final float[] floatValues2;

      private Entry6(float[] var1, float[] var2) {
         this.floatValues = var1;
         this.floatValues2 = var2;
      }
   }

   public static final class Entry7 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final float floatValue13;
      private final float floatValue14;
      private final float floatValue15;
      private final float floatValue16;
      private final float floatValue17;
      private final float floatValue18;
      private final float floatValue19;

      private Entry7(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         float var13
      ) {
         this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var12, var13, var12, var13, 0.0F, 0.0F);
      }

      private Entry7(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         float var13,
         float var14,
         float var15,
         float var16,
         float var17,
         float var18,
         float var19
      ) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
         this.floatValue9 = var9;
         this.floatValue10 = var10;
         this.floatValue11 = var11;
         this.floatValue12 = var12;
         this.floatValue13 = var13;
         this.floatValue14 = var14;
         this.floatValue15 = var15;
         this.floatValue16 = var16;
         this.floatValue17 = var17;
         this.floatValue18 = var18;
         this.floatValue19 = var19;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public JointCodebook.Entry7 method2592(float var1, float var2, float var3, float var4) {
         return new JointCodebook.Entry7(
            var1,
            var2,
            var1 - var3,
            var2 - var4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            this.floatValue14,
            this.floatValue15,
            this.floatValue16,
            this.floatValue17,
            this.floatValue18,
            this.floatValue19
         );
      }

      public JointCodebook.Entry7 method2593(
         float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10
      ) {
         return new JointCodebook.Entry7(
            var1,
            var2,
            var1 - var3,
            var2 - var4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10
         );
      }

      public float[] method2594() {
         return new float[]{this.floatValue, this.floatValue2};
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

      public float method2595() {
         return Math.abs(this.floatValue);
      }

      public float method2596() {
         return Math.abs(this.floatValue2);
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public float getFloatValue12() {
         return this.floatValue12;
      }

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }

      public float getFloatValue15() {
         return this.floatValue15;
      }

      public float getFloatValue16() {
         return this.floatValue16;
      }

      public float getFloatValue17() {
         return this.floatValue17;
      }

      public float getFloatValue18() {
         return this.floatValue18;
      }

      public float getFloatValue19() {
         return this.floatValue19;
      }
   }

   private static final class Entry8 {
      public final float[][] floatValues;
      public final float[] floatValues2;

      private Entry8(float[][] var1, float[] var2) {
         this.floatValues = var1;
         this.floatValues2 = var2;
      }
   }

   private static final class Entry9 {
      public String text;
      public String text2;
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int intValue4;
      public int intValue5;
      public int intValue6;
      public int intValue7;
      public int intValue8;
      public float floatValue;
      public String text3;
      public int intValue9;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public boolean flag;
      public String[] texts;
      public String[] texts2;
      public float floatValue5;
      public float floatValue6;
      public JointCodebook.Entry6 entry6;
      public JointCodebook.Entry4[] entry4s;
      public JointCodebook.Entry8 entry8;
      public JointCodebook.Entry8 entry82;
      public JointCodebook.Entry8 entry83;
      public JointCodebook.Entry8 entry84;
      public float[][] floatValues;
      public float[][] floatValues2;
      public float[] floatValues3;
      public JointCodebook.Entry5 entry5;
      public JointCodebook.Entry entry;
   }
}

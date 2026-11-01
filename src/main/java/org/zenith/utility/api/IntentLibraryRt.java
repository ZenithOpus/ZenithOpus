package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public final class IntentLibraryRt {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   public static final String[] texts = new String[]{"flick", "correction", "tracking", "settle"};
   public static final int CONST_intValue6 = 8;
   private static final float[] CONST_floatValues = new float[]{1.0F, 1.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.6F, 0.6F};
   private static final String text = "/assets/zenith/aimpipe/intent_library_rt.json";
   private static final Path path = Path.of("intent_library_rt.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "motor_intents", "out", "intent_library_rt.json"), Path.of("scripts", "motor_intents", "out", "intent_library_rt.json")
   };
   private final IntentLibraryRt.Entry3[] entry3s;
   private final IntentLibraryRt.Entry[] entrys = new IntentLibraryRt.Entry[4];

   public IntentLibraryRt.Entry3 method1378(int var1) {
      return this.entry3s[var1];
   }

   public int size() {
      return this.entry3s.length;
   }

   public IntentLibraryRt.Entry3 method1379(
      int var1,
      double[] var2,
      IntentLibraryRt.Entry2 var3,
      Random var4,
      int var5,
      double var6,
      int var8,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19
   ) {
      IntentLibraryRt.Entry var21 = this.entrys[var1];
      int var22 = var21.intValues.length;
      double[] var23 = new double[var22];

      for (int var24 = 0; var24 < var22; var24++) {
         double var25 = 0.0;
         float[] var27 = var21.floatValues[var24];

         for (int var28 = 0; var28 < 8; var28++) {
            if (CONST_floatValues[var28] != 0.0F) {
               double var29 = (var2[var28] - var21.floatValues2[var28]) / var21.std[var28];
               double var31 = (var27[var28] - var29) * CONST_floatValues[var28];
               var25 += var31 * var31;
            }
         }

         var23[var24] = Math.sqrt(var25);
         if (var21.intValues[var24] == var8 && var22 > 1) {
            var23[var24] = Double.POSITIVE_INFINITY;
         }
      }

      int var50 = Math.min(var5 * 4, var22);
      int[] var51 = method1380(var23, var50);
      double var26 = Math.hypot(var3.doubleValue, var3.doubleValue2);
      boolean var52 = var13 > 0.0 && (var3.intValue != 0 || var3.intValue2 != 0);
      double var53 = Math.hypot(var3.intValue, var3.intValue2);
      double[] var54 = new double[var50];

      for (int var32 = 0; var32 < var50; var32++) {
         int var33 = var51[var32];
         double var34 = var23[var33];
         if (var9 > 0.0) {
            double var36 = var3.doubleValue - var21.floatValues3[var33] + var3.doubleValue3 * var21.floatValues5[var33];
            double var38 = var3.doubleValue2 - var21.floatValues4[var33] + var3.doubleValue4 * var21.floatValues5[var33];
            double var40 = Math.max(var3.doubleValue5 * var11, 1.0);
            double var42 = Math.max(var3.doubleValue6 * var11, 1.0);
            double var44 = Math.max(Math.abs(var36) - var40, 0.0);
            double var46 = Math.max(Math.abs(var38) - var42, 0.0);
            double var48 = Math.hypot(var44, var46);
            var34 += var9 * Math.log1p(var48);
         }

         if (var52) {
            double var59 = var21.floatValues6[var33] - var3.intValue;
            double var61 = var21.floatValues7[var33] - var3.intValue2;
            double var64 = Math.hypot(var59, var61);
            double var68 = Math.hypot(var21.floatValues6[var33], var21.floatValues7[var33]);
            var34 += var13 * (var64 / (var53 + var68 + 1.0));
         }

         if (var15 > 0.0 && var26 > var19) {
            double var60 = var3.doubleValue - var21.floatValues8[var33] + var3.doubleValue3 * var21.floatValues10[var33];
            double var62 = var3.doubleValue2 - var21.floatValues9[var33] + var3.doubleValue4 * var21.floatValues10[var33];
            double var65 = Math.hypot(var60, var62) / var26;
            var34 += var15 * Math.abs(var65 - var17);
         }

         var54[var32] = var34;
      }

      int var55 = Math.min(var5, var50);
      int[] var56 = method1380(var54, var55);
      if (var6 <= 1.0E-9) {
         return this.entry3s[var21.intValues[var51[var56[0]]]];
      } else {
         double[] var57 = new double[var55];

         for (int var35 = 0; var35 < var55; var35++) {
            var57[var35] = var54[var56[var35]];
         }

         double var58 = Math.max(method1381(var57), 1.0E-6);
         double[] var37 = new double[var55];
         double var63 = Double.NEGATIVE_INFINITY;

         for (int var66 = 0; var66 < var55; var66++) {
            var37[var66] = -var57[var66] / (var58 * var6);
            var63 = Math.max(var63, var37[var66]);
         }

         double var67 = 0.0;

         for (int var69 = 0; var69 < var55; var69++) {
            var37[var69] = Math.exp(var37[var69] - var63);
            var67 += var37[var69];
         }

         double var70 = var4.nextDouble() * var67;
         int var71 = var55 - 1;
         double var45 = 0.0;

         for (int var47 = 0; var47 < var55; var47++) {
            var45 += var37[var47];
            if (var70 <= var45) {
               var71 = var47;
               break;
            }
         }

         return this.entry3s[var21.intValues[var51[var56[var71]]]];
      }
   }

   private static int[] method1380(double[] var0, int var1) {
      Integer[] var2 = new Integer[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2[var3] = var3;
      }

      Arrays.sort(var2, (var1x, var2x) -> Double.compare(var0[var1x], var0[var2x]));
      int[] var5 = new int[var1];

      for (int var4 = 0; var4 < var1; var4++) {
         var5[var4] = var2[var4];
      }

      return var5;
   }

   private static double method1381(double[] var0) {
      double[] var1 = (double[])var0.clone();
      Arrays.sort(var1);
      int var2 = var1.length / 2;
      return var1.length % 2 == 1 ? var1[var2] : 0.5 * (var1[var2 - 1] + var1[var2]);
   }

   private IntentLibraryRt(IntentLibraryRt.Entry3[] var1, float[][] var2) {
      this.entry3s = var1;
      int[] var3 = new int[4];

      for (IntentLibraryRt.Entry3 var7 : var1) {
         var3[var7.intValue2]++;
      }

      for (int var16 = 0; var16 < 4; var16++) {
         IntentLibraryRt.Entry var18 = new IntentLibraryRt.Entry();
         int var21 = var3[var16];
         var18.intValues = new int[var21];
         var18.floatValues = new float[var21][8];
         var18.floatValues3 = new float[var21];
         var18.floatValues4 = new float[var21];
         var18.floatValues5 = new float[var21];
         var18.floatValues6 = new float[var21];
         var18.floatValues7 = new float[var21];
         var18.floatValues8 = new float[var21];
         var18.floatValues9 = new float[var21];
         var18.floatValues10 = new float[var21];
         this.entrys[var16] = var18;
      }

      int[] var17 = new int[4];

      for (IntentLibraryRt.Entry3 var8 : var1) {
         IntentLibraryRt.Entry var9 = this.entrys[var8.intValue2];
         int var10 = var17[var8.intValue2]++;
         var9.intValues[var10] = var8.intValue;
         System.arraycopy(var2[var8.intValue], 0, var9.floatValues[var10], 0, 8);
         var9.floatValues3[var10] = var8.intValue3;
         var9.floatValues4[var10] = var8.intValue4;
         var9.floatValues5[var10] = var8.length();
         var9.floatValues6[var10] = var8.intValues[0];
         var9.floatValues7[var10] = var8.intValues2[0];
         int var11 = Math.min(var8.length(), 4);
         int var12 = 0;
         int var13 = 0;

         for (int var14 = 0; var14 < var11; var14++) {
            var12 += var8.intValues[var14];
            var13 += var8.intValues2[var14];
         }

         var9.floatValues8[var10] = var12;
         var9.floatValues9[var10] = var13;
         var9.floatValues10[var10] = var11;
      }

      for (int var20 = 0; var20 < 4; var20++) {
         IntentLibraryRt.Entry var23 = this.entrys[var20];
         int var25 = var23.intValues.length;
         if (var25 == 0) {
            throw new IllegalStateException("library has no primitives of intent " + texts[var20]);
         }

         for (int var26 = 0; var26 < 8; var26++) {
            double var27 = 0.0;

            for (int var29 = 0; var29 < var25; var29++) {
               var27 += var23.floatValues[var29][var26];
            }

            var27 /= var25;
            double var30 = 0.0;

            for (int var31 = 0; var31 < var25; var31++) {
               double var33 = var23.floatValues[var31][var26] - var27;
               var30 += var33 * var33;
            }

            double var32 = Math.max(Math.sqrt(var30 / var25), 1.0E-6);
            var23.floatValues2[var26] = (float)var27;
            var23.std[var26] = (float)var32;

            for (int var15 = 0; var15 < var25; var15++) {
               var23.floatValues[var15][var26] = (float)((var23.floatValues[var15][var26] - var27) / var32);
            }
         }
      }
   }

   public static IntentLibraryRt method1382() {
      IntentLibraryRt var0 = method1383(path);
      if (var0 != null) {
         return var0;
      } else {
         for (Path var4 : paths) {
            var0 = method1383(var4);
            if (var0 != null) {
               return var0;
            }
         }

         return method1384();
      }
   }

   private static IntentLibraryRt method1383(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : method1385(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable var2) {
         System.err.println("[MotorIntent] failed to load " + var0 + ": " + var2);
         return null;
      }
   }

   private static IntentLibraryRt method1384() {
      try {
         IntentLibraryRt var1;
         try (InputStream var0 = IntentLibraryRt.class.getResourceAsStream("/assets/zenith/aimpipe/intent_library_rt.json")) {
            if (var0 == null) {
               return null;
            }

            var1 = method1385(new String(var0.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/intent_library_rt.json");
         }

         return var1;
      } catch (Throwable var5) {
         System.err.println("[MotorIntent] failed to load /assets/zenith/aimpipe/intent_library_rt.json: " + var5);
         return null;
      }
   }

   private static IntentLibraryRt method1385(String var0, String var1) {
      JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
      if (var2.get("format_version").getAsInt() == 1 && "motor_intent_runtime_library".equals(var2.get("artifact").getAsString())) {
         JsonArray var3 = var2.getAsJsonArray("intent_types");
         if (var3.size() != 4) {
            throw new IllegalStateException("intent taxonomy mismatch");
         } else {
            for (int var4 = 0; var4 < 4; var4++) {
               if (!texts[var4].equals(var3.get(var4).getAsString())) {
                  throw new IllegalStateException("intent taxonomy mismatch at " + var4);
               }
            }

            JsonArray var15 = var2.getAsJsonArray("primitives");
            IntentLibraryRt.Entry3[] var5 = new IntentLibraryRt.Entry3[var15.size()];
            float[][] var6 = new float[var15.size()][8];

            for (int var7 = 0; var7 < var15.size(); var7++) {
               JsonArray var8 = var15.get(var7).getAsJsonArray();
               int var9 = var8.get(0).getAsInt();
               if (var9 < 0 || var9 >= 4) {
                  throw new IllegalStateException("bad intent index " + var9);
               }

               JsonArray var10 = var8.get(1).getAsJsonArray();
               if (var10.size() < 1) {
                  throw new IllegalStateException("empty primitive " + var7);
               }

               int[] var11 = new int[var10.size()];
               int[] var12 = new int[var10.size()];

               for (int var13 = 0; var13 < var10.size(); var13++) {
                  JsonArray var14 = var10.get(var13).getAsJsonArray();
                  var11[var13] = var14.get(0).getAsInt();
                  var12[var13] = var14.get(1).getAsInt();
               }

               JsonArray var17 = var8.get(2).getAsJsonArray();
               if (var17.size() != 8) {
                  throw new IllegalStateException("bad descriptor size for primitive " + var7);
               }

               for (int var18 = 0; var18 < 8; var18++) {
                  var6[var7][var18] = var17.get(var18).getAsFloat();
               }

               var5[var7] = new IntentLibraryRt.Entry3(var7, var9, var11, var12);
            }

            if (var2.has("count") && var2.get("count").getAsInt() != var5.length) {
               throw new IllegalStateException("library count mismatch");
            } else {
               IntentLibraryRt var16 = new IntentLibraryRt(var5, var6);
               System.out.println("[MotorIntent] loaded library " + var1 + " (" + var5.length + " primitives)");
               return var16;
            }
         }
      } else {
         throw new IllegalStateException("unsupported motor-intent library artifact");
      }
   }

   private static final class Entry {
      public int[] intValues;
      public float[][] floatValues;
      public float[] floatValues2 = new float[8];
      public float[] std = new float[8];
      public float[] floatValues3;
      public float[] floatValues4;
      public float[] floatValues5;
      public float[] floatValues6;
      public float[] floatValues7;
      public float[] floatValues8;
      public float[] floatValues9;
      public float[] floatValues10;
   }

   public static final class Entry2 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public int intValue;
      public int intValue2;
   }

   public static final class Entry3 {
      public final int intValue;
      public final int intValue2;
      public final int[] intValues;
      public final int[] intValues2;
      public final int intValue3;
      public final int intValue4;

      private Entry3(int var1, int var2, int[] var3, int[] var4) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValues = var3;
         this.intValues2 = var4;
         int var5 = 0;
         int var6 = 0;

         for (int var7 = 0; var7 < var3.length; var7++) {
            var5 += var3[var7];
            var6 += var4[var7];
         }

         this.intValue3 = var5;
         this.intValue4 = var6;
      }

      public int length() {
         return this.intValues.length;
      }
   }
}

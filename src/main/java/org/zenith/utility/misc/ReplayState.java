package org.zenith.utility.misc;

import java.util.Random;
import org.zenith.utility.api.IntentLibraryRt;
import org.zenith.utility.api.IntentSelector;

public final class ReplayState {
   private static final double CONST_doubleValue = 0.35;
   private static final int CONST_intValue = 16;
   private static final double CONST_doubleValue2 = 0.7;
   private static final double CONST_doubleValue3 = 1.0;
   private static final double CONST_doubleValue4 = 0.0;
   private static final double CONST_doubleValue5 = 0.0;
   private static final double CONST_doubleValue6 = 1.0;
   private static final double CONST_doubleValue7 = 1.0;
   private static final double CONST_doubleValue8 = 1.0;
   private static final int CONST_intValue2 = 40;
   private static final double CONST_doubleValue9 = 1.0;
   private static final double CONST_doubleValue10 = 1.0;
   private static final double CONST_doubleValue11 = 0.6;
   private static final double CONST_doubleValue12 = 0.4;
   private static final double CONST_doubleValue13 = 0.5;
   private static final double CONST_doubleValue14 = 40.0;
   private static final int CONST_intValue3 = 2;
   private static final double CONST_doubleValue15 = 1.25;
   private static final double CONST_doubleValue16 = 25.0;
   private static final double CONST_doubleValue17 = 30.0;
   private static final double CONST_doubleValue18 = 15.0;
   private final IntentSelector intentSelector;
   private final IntentLibraryRt intentLibraryRt;
   private final Random random;
   private IntentLibraryRt.Entry3 entry3;
   private int intValue;
   private double doubleValue;
   private boolean flag;
   private int intValue2;
   private boolean flag2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6 = -1;
   private String text = "idle";

   public ReplayState(IntentSelector var1, IntentLibraryRt var2) {
      this.intentSelector = var1;
      this.intentLibraryRt = var2;
      this.random = new Random();
      this.reset();
   }

   public void reset() {
      this.entry3 = null;
      this.intValue = 0;
      this.doubleValue = 0.0;
      this.flag = false;
      this.intValue2 = 0;
      this.flag2 = false;
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.intValue5 = 0;
      this.intValue6 = -1;
      this.text = "idle";
   }

   public String getText() {
      return this.text;
   }

   public int[] method7983(ReplayState.Entry var1) {
      if (this.entry3 != null && this.method7985(var1)) {
         this.entry3 = null;
         this.intValue = 0;
      }

      if (this.entry3 != null) {
         int var13 = this.entry3.intValues[this.intValue];
         int var14 = this.entry3.intValues2[this.intValue];
         this.intValue++;
         this.text = "replay:" + IntentLibraryRt.texts[this.entry3.intValue2] + " " + this.intValue + "/" + this.entry3.length();
         if (this.intValue >= this.entry3.length()) {
            this.entry3 = null;
            this.intValue = 0;
         }

         this.method7991(var13, var14);
         return new int[]{var13, var14};
      } else if (this.intValue2 > 0) {
         this.intValue2--;
         if (this.intValue2 <= 0) {
            this.flag2 = true;
         }

         this.text = "rest:" + this.intValue2 + " left";
         this.method7991(0, 0);
         return new int[]{0, 0};
      } else {
         IntentSelector.Entry var2 = this.intentSelector.method7315(this.method7986(var1));
         double var3 = method7992((var2.floatValue + 0.0) / 1.0);
         boolean var5 = this.flag2 || this.random.nextDouble() < var3;
         this.flag2 = false;
         if (!var5) {
            int var15 = 1;
            if (var1.doubleValue13 >= 0.5) {
               var15 = this.method7984(var2.floatValues3);
               if (var15 > 1) {
                  this.intValue2 = var15 - 1;
               } else {
                  this.flag2 = true;
               }
            }

            this.text = "wait dwell=" + var15;
            this.method7991(0, 0);
            return new int[]{0, 0};
         } else {
            int var6 = this.method7989(var2.floatValues, 0.7);
            double[] var7 = this.method7987(var2.floatValues2[var6]);
            double[] var8 = this.method7988(var7, var1);
            IntentLibraryRt.Entry2 var9 = new IntentLibraryRt.Entry2();
            var9.doubleValue = var1.doubleValue;
            var9.doubleValue2 = var1.doubleValue2;
            var9.doubleValue3 = var1.doubleValue9;
            var9.doubleValue4 = var1.doubleValue10;
            var9.doubleValue5 = (var1.doubleValue4 - var1.doubleValue3) * 0.5;
            var9.doubleValue6 = (var1.doubleValue6 - var1.doubleValue5) * 0.5;
            var9.intValue = this.intValue3;
            var9.intValue2 = this.intValue4;
            IntentLibraryRt.Entry3 var10 = this.intentLibraryRt
               .method1379(var6, var8, var9, this.random, 16, 0.35, this.intValue6, 1.0, 1.0, 0.6, 0.4, 0.5, 40.0);
            this.intValue6 = var10.intValue;
            this.entry3 = var10;
            this.intValue = 0;
            this.doubleValue = Math.hypot(var1.doubleValue, var1.doubleValue2);
            this.flag = var1.doubleValue13 >= 0.5;
            int var11 = var10.intValues[0];
            int var12 = var10.intValues2[0];
            this.intValue = 1;
            this.text = "start:" + IntentLibraryRt.texts[var6] + " #" + var10.intValue + " len=" + var10.length();
            if (this.intValue >= var10.length()) {
               this.entry3 = null;
               this.intValue = 0;
            }

            this.method7991(var11, var12);
            return new int[]{var11, var12};
         }
      }
   }

   private int method7984(float[][] var1) {
      float[] var2 = new float[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = var1[var3][0];
      }

      int var8 = this.method7989(var2, 1.0);
      double var4 = var1[var8][1];
      double var6 = method7993(var1[var8][2], -2.5, 0.7F);
      var4 += Math.exp(var6) * 1.0 * this.random.nextGaussian();
      int var10 = (int)Math.round(Math.expm1(Math.max(var4, 0.0)));
      return Math.max(1, Math.min(var10, 40));
   }

   private boolean method7985(ReplayState.Entry var1) {
      if (this.intValue < 2) {
         return false;
      } else {
         double var2 = Math.hypot(var1.doubleValue, var1.doubleValue2);
         boolean var4 = var2 > this.doubleValue * 1.25 && var2 > this.doubleValue + 25.0;
         double var5 = 0.0;
         double var7 = 0.0;

         for (int var9 = this.intValue; var9 < this.entry3.length(); var9++) {
            var5 += this.entry3.intValues[var9];
            var7 += this.entry3.intValues2[var9];
         }

         double var13 = Math.hypot(var5, var7);
         boolean var11 = var2 >= 30.0 && var13 >= 15.0 && var5 * var1.doubleValue + var7 * var1.doubleValue2 < 0.0;
         boolean var12 = this.flag && var1.doubleValue13 < 0.5;
         return var4 || var11 || var12;
      }
   }

   private float[] method7986(ReplayState.Entry var1) {
      float[] var2 = new float[this.intentSelector.intValue];

      for (int var3 = 0; var3 < this.intentSelector.intValue; var3++) {
         String var4 = this.intentSelector.texts[var3];

         var2[var3] = (float)(switch (var4) {
            case "center_yaw" -> var1.doubleValue;
            case "center_pitch" -> var1.doubleValue2;
            case "box_min_yaw" -> var1.doubleValue3;
            case "box_max_yaw" -> var1.doubleValue4;
            case "box_min_pitch" -> var1.doubleValue5;
            case "box_max_pitch" -> var1.doubleValue6;
            case "center_yaw_change" -> var1.doubleValue7;
            case "center_pitch_change" -> var1.doubleValue8;
            case "yaw_drift" -> var1.doubleValue9;
            case "pitch_drift" -> var1.doubleValue10;
            case "sim_2t_yaw" -> var1.doubleValue11;
            case "sim_2t_pitch" -> var1.doubleValue12;
            case "inside" -> var1.doubleValue13;
            case "distance" -> var1.doubleValue14;
            case "target_motion_x" -> var1.doubleValue15;
            case "target_motion_y" -> var1.doubleValue16;
            case "target_motion_z" -> var1.doubleValue17;
            case "player_motion_x" -> var1.doubleValue18;
            case "player_motion_y" -> var1.doubleValue19;
            case "player_motion_z" -> var1.doubleValue20;
            case "prev_cmd_yaw" -> this.intValue3;
            case "prev_cmd_pitch" -> this.intValue4;
            case "rest_run_so_far_log" -> this.intValue5;
            case "in_gesture" -> this.intValue3 == 0 && this.intValue4 == 0 ? 0.0 : 1.0;
            default -> throw new IllegalStateException("unknown selector input " + this.intentSelector.texts[var3]);
         });
      }

      return var2;
   }

   private double[] method7987(float[][] var1) {
      float[] var2 = new float[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = var1[var3][0];
      }

      int var6 = this.method7989(var2, 1.0);
      double[] var4 = new double[4];

      for (int var5 = 0; var5 < var4.length; var5++) {
         var4[var5] = var1[var6][1 + var5];
      }

      return var4;
   }

   private double[] method7988(double[] var1, ReplayState.Entry var2) {
      double var3 = Math.max(Math.expm1(var1[2]), 1.0);
      double var5 = Math.max(var3 - 2.0, 0.0);
      double var7 = var2.doubleValue11 + var2.doubleValue9 * var5;
      double var9 = var2.doubleValue12 + var2.doubleValue10 * var5;
      return new double[]{
         var1[0] + IntentSelector.method7316((float)var7),
         var1[1] + IntentSelector.method7316((float)var9),
         var1[2],
         var1[3],
         0.0,
         0.0,
         IntentSelector.method7316((float)(var2.doubleValue9 * var3)),
         IntentSelector.method7316((float)(var2.doubleValue10 * var3))
      };
   }

   private int method7989(float[] var1, double var2) {
      double var4 = Double.NEGATIVE_INFINITY;

      for (float var9 : var1) {
         var4 = Math.max(var4, (double)var9);
      }

      double[] var14 = new double[var1.length];
      double var15 = 0.0;

      for (int var16 = 0; var16 < var1.length; var16++) {
         var14[var16] = Math.exp((var1[var16] - var4) / var2);
         var15 += var14[var16];
      }

      double var17 = this.random.nextDouble() * var15;
      double var11 = 0.0;

      for (int var13 = 0; var13 < var1.length; var13++) {
         var11 += var14[var13];
         if (var17 <= var11) {
            return var13;
         }
      }

      return var1.length - 1;
   }

   private static int method7990(float[] var0) {
      int var1 = 0;

      for (int var2 = 1; var2 < var0.length; var2++) {
         if (var0[var2] > var0[var1]) {
            var1 = var2;
         }
      }

      return var1;
   }

   private void method7991(int var1, int var2) {
      this.intValue3 = var1;
      this.intValue4 = var2;
      this.intValue5 = var1 == 0 && var2 == 0 ? this.intValue5 + 1 : 0;
   }

   private static double method7992(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   private static double method7993(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   public static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public double doubleValue13;
      public double doubleValue14;
      public double doubleValue15;
      public double doubleValue16;
      public double doubleValue17;
      public double doubleValue18;
      public double doubleValue19;
      public double doubleValue20;
   }
}

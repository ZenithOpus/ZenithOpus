package org.zenith.base.rotation.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.IntStream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public final class TokenMotor extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final String text = "token_motor.json";
   private static final String text2 = "models/token_motor.json";
   private static final List<Path> items = List.of(
      Path.of("token_motor.json"), Path.of("models", "token_motor.json"), Path.of("..", "models", "token_motor.json")
   );
   private static final String text3 = "neurotest-token-motor-v2";
   private static final double CONST_doubleValue = 1.0E-9;
   private static final boolean CONST_flag = false;
   private static final int CONST_intValue2 = 8;
   private TokenMotor.Entry4 entry4;
   private boolean flag;
   private CompletableFuture<TokenMotor.Entry4> completableFuture;
   private long longValue;
   private final Random random = new Random();
   private final TokenMotor.Entry[] entrys = new TokenMotor.Entry[8];
   private int intValue;
   private double[][] doubleValues;
   private int[][] intValues;
   private int intValue2;
   private final double[] doubleValues2 = new double[6];
   private double doubleValue;
   private int[][] intValues2;
   private int intValue3;
   private boolean flag2;
   private float floatValue = 1.0F;
   private boolean flag3;
   private int intValue4 = Integer.MIN_VALUE;
   private int intValue5 = Integer.MIN_VALUE;
   private int intValue6 = Integer.MIN_VALUE;
   private Rotation rotation;

   public Rotation method3467() {
      Rotation var1 = rotationManager.method7844();
      LivingEntity var2 = AuraModule.INSTANCE.method6358();
      if (mc.player != null && var2 != null && var2.isAlive()) {
         TokenMotor.Entry4 var3 = this.method3481();
         if (var3 == null) {
            return var1;
         } else {
            int var4 = mc.player.age;
            if (var4 == this.intValue6 && this.rotation != null) {
               return this.rotation;
            } else {
               if (this.intValue5 != Integer.MIN_VALUE && var4 != this.intValue5 + 1) {
                  this.method3478(var2.getId());
               } else if (var2.getId() != this.intValue4) {
                  this.method3479(var2.getId());
               }

               this.intValue5 = var4;
               this.method3477();
               TokenMotor.Entry var5 = this.method3472(var1, var2);
               if (var5 == null) {
                  this.method3480(var4, var1);
                  return var1;
               } else {
                  this.method3473(var5);
                  if (this.intValue < var3.intValue10 + 3) {
                     this.method3480(var4, var1);
                     return var1;
                  } else {
                     double[] var6 = new double[]{var1.getFloatValue(), var1.getFloatValue2()};
                     double[] var7 = new double[6];
                     double[] var8 = this.method3476(var3, var6, var7);
                     if (!method3485(var8)) {
                        this.method3480(var4, var1);
                        return var1;
                     } else {
                        int[] var9;
                        if (this.flag2) {
                           var9 = this.intValues2[this.intValue3++];
                           this.flag2 = this.intValue3 < this.intValues2.length;
                        } else {
                           var9 = this.method3470(var3, var8, var6);
                        }

                        double var10 = var3.doubleValue4;
                        Rotation var12 = var1.method3140((float)(var9[0] * var10), (float)(var9[1] * var10));
                        this.method3475(var8, new int[]{var3.method3488(var9[0]), var3.method3488(var9[1])});
                        System.arraycopy(var7, 0, this.doubleValues2, 0, 6);
                        this.doubleValue = var9[0] == 0 && var9[1] == 0 ? 0.0 : this.doubleValue + 1.0;
                        this.method3480(var4, var12);
                        return var12;
                     }
                  }
               }
            }
         }
      } else {
         this.reset();
         if (mc.player != null) {
            mc.player.setYaw(var1.getFloatValue());
            mc.player.setPitch(var1.getFloatValue2());
         }

         return var1;
      }
   }

   public boolean method3468() {
      return this.method3481() != null;
   }

   public static void method3469(Path var0) throws IOException {
      long var1 = System.currentTimeMillis();
      TokenMotor.Entry4 var3 = TokenMotor.Entry4.method3507(var0);
      var3.method3504();
      System.out
         .println(
            "v5 token motor: эталон сошёлся, окно "
               + var3.intValue
               + ", префикс "
               + var3.intValue2
               + ", словарь "
               + var3.intValue3
               + ", сенса-константа "
               + var3.doubleValue4
               + ", "
               + (System.currentTimeMillis() - var1)
               + " мс"
         );
      int var4 = var3.intValue;
      int var5 = var3.intValue2;
      double[][] var6 = new double[var4][];
      int[][] var7 = new int[var4][2];

      for (int var8 = 0; var8 < var4; var8++) {
         var6[var8] = var3.entry3.doubleValues10[var8];
         var7[var8] = var8 < var5 ? (int[])var3.entry3.intValues[var8].clone() : new int[]{var3.intValue4, var3.intValue4};
      }

      Random var18 = new Random(0L);
      double var9 = -1.0;
      double var11 = -1.0;

      for (int var13 = 0; var13 < 4; var13++) {
         long var14 = System.nanoTime();

         for (int var16 = 0; var16 < var3.intValue12; var16++) {
            if (var3.flag) {
               var3.method3500(method3471(var7), var6, var3.intValue11, true, var18);
            } else {
               var3.method3499(method3471(var7), var6, var3.intValue13, true, true, var18);
            }
         }

         double var20 = (System.nanoTime() - var14) / 1000000.0;
         if (var13 == 0) {
            var9 = var20;
         }

         var11 = var20;
      }

      int var19 = var3.flag ? 2 * var3.intValue11 : var3.intValue13;
      System.out
         .println(
            "v5 одно решение ("
               + var3.intValue12
               + " x "
               + var19
               + " проходов, режим "
               + (var3.flag ? "first" : "fill")
               + "): холодное "
               + String.format(Locale.ROOT, "%.0f", var9)
               + " мс, прогретое "
               + String.format(Locale.ROOT, "%.0f", var11)
               + " мс при бюджете "
               + var3.intValue11 * 50
               + " мс"
         );
   }

   private int[] method3470(TokenMotor.Entry4 var1, double[] var2, double[] var3) {
      int var4 = var1.intValue;
      int var5 = var1.intValue2;
      double[][] var6 = new double[var4][];
      int[][] var7 = new int[var4][2];

      for (int var8 = 0; var8 < var5; var8++) {
         if (var8 < this.intValue2) {
            int var9 = this.intValue2 == var5 ? var8 : var8 - (var5 - this.intValue2);
            if (var9 >= 0) {
               var6[var8] = this.doubleValues[var9];
               var7[var8] = (int[])this.intValues[var9].clone();
               continue;
            }
         }

         var6[var8] = var1.method3491(var2);
         var7[var8] = new int[]{var1.intValue4, var1.intValue4};
      }

      var6[var5] = var2;

      for (int var15 = var5 + 1; var15 < var4; var15++) {
         var6[var15] = var1.method3491(var2);
      }

      for (int var16 = var5; var16 < var4; var16++) {
         var7[var16] = new int[]{var1.intValue4, var1.intValue4};
      }

      TokenMotor.Entry var17 = this.method3474(var1.intValue10);
      TokenMotor.Entry var18 = this.method3474(var1.intValue10 + 1);
      long[] var10 = new long[var1.intValue12];

      for (int var11 = 0; var11 < var1.intValue12; var11++) {
         var10[var11] = this.random.nextLong();
      }

      int[][][] var19 = new int[var1.intValue12][][];
      double[] var12 = new double[var1.intValue12];
      IntStream.range(0, var1.intValue12)
         .parallel()
         .forEach(
            var10x -> {
               Random var11x = new Random(var10[var10x]);
               int[][] var12x = var1.flag
                  ? var1.method3500(method3471(var7), var6, var1.intValue11, true, var11x)
                  : var1.method3499(method3471(var7), var6, var1.intValue13, true, true, var11x);
               int[][] var13x = new int[var1.intValue11][2];
               double var14x = 0.0;
               double var16x = 0.0;

               for (int var18x = 0; var18x < var1.intValue11; var18x++) {
                  var13x[var18x][0] = var1.method3490(var12x[var5 + var18x][0], 0, var11x);
                  var13x[var18x][1] = var1.method3490(var12x[var5 + var18x][1], 1, var11x);
                  var14x += var13x[var18x][0] * var1.doubleValue4;
                  var16x += var13x[var18x][1] * var1.doubleValue4;
               }

               var19[var10x] = var13x;
               var12[var10x] = TokenMotor.Entry4.method3493(var1, var17, var18, var3, var14x, var16x);
            }
         );
      int var13 = 0;

      for (int var14 = 1; var14 < var1.intValue12; var14++) {
         if (var12[var14] < var12[var13]) {
            var13 = var14;
         }
      }

      int[][] var20 = var19[var13];
      this.intValues2 = var20;
      this.intValue3 = 1;
      this.flag2 = var20.length > 1;
      return var20[0];
   }

   private static int[][] method3471(int[][] var0) {
      int[][] var1 = new int[var0.length][];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = (int[])var0[var2].clone();
      }

      return var1;
   }

   private TokenMotor.Entry method3472(Rotation var1, LivingEntity var2) {
      Vec3d var3 = mc.player.getEyePos();
      Box var4 = var2.getBoundingBox();
      Vec3d var5 = var4.getCenter();
      double var6 = var5.x - var3.x;
      double var8 = var5.y - var3.y;
      double var10 = var5.z - var3.z;
      if (var6 * var6 + var8 * var8 + var10 * var10 < 1.0E-10) {
         return null;
      } else {
         TokenMotor.Entry var12 = new TokenMotor.Entry();
         var12.doubleValue = var1.getFloatValue();
         var12.doubleValue2 = var1.getFloatValue2();
         var12.doubleValue3 = Math.toDegrees(Math.atan2(-var6, var10));
         var12.doubleValue4 = Math.toDegrees(Math.atan2(-var8, Math.hypot(var6, var10)));
         var12.doubleValue9 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
         double var13 = Double.MAX_VALUE;
         double var15 = -Double.MAX_VALUE;
         double var17 = Double.MAX_VALUE;
         double var19 = -Double.MAX_VALUE;

         for (int var21 = 0; var21 < 8; var21++) {
            double var22 = ((var21 & 1) == 0 ? var4.minX : var4.maxX) - var3.x;
            double var24 = ((var21 & 2) == 0 ? var4.minY : var4.maxY) - var3.y;
            double var26 = ((var21 & 4) == 0 ? var4.minZ : var4.maxZ) - var3.z;
            double var28 = Math.toDegrees(Math.atan2(-var22, var26));
            double var30 = Math.toDegrees(Math.atan2(-var24, Math.hypot(var22, var26)));
            if (Double.isFinite(var28) && Double.isFinite(var30)) {
               double var32 = MathHelper.wrapDegrees(var28 - var12.doubleValue3);
               double var34 = var30 - var12.doubleValue4;
               var13 = Math.min(var13, var32);
               var15 = Math.max(var15, var32);
               var17 = Math.min(var17, var34);
               var19 = Math.max(var19, var34);
            }
         }

         if (var13 == Double.MAX_VALUE) {
            return null;
         } else {
            var12.doubleValue5 = var13;
            var12.doubleValue6 = var15;
            var12.doubleValue7 = var17;
            var12.doubleValue8 = var19;
            return var12;
         }
      }
   }

   private void method3473(TokenMotor.Entry var1) {
      System.arraycopy(this.entrys, 0, this.entrys, 1, 7);
      this.entrys[0] = var1;
      if (this.intValue < 8) {
         this.intValue++;
      }
   }

   private TokenMotor.Entry method3474(int var1) {
      return this.entrys[Math.min(var1, this.intValue - 1)];
   }

   private void method3475(double[] var1, int[] var2) {
      int var3 = this.entry4.intValue2;
      if (this.intValue2 < var3) {
         this.doubleValues[this.intValue2] = var1;
         this.intValues[this.intValue2] = var2;
         this.intValue2++;
      } else {
         System.arraycopy(this.doubleValues, 1, this.doubleValues, 0, var3 - 1);
         System.arraycopy(this.intValues, 1, this.intValues, 0, var3 - 1);
         this.doubleValues[var3 - 1] = var1;
         this.intValues[var3 - 1] = var2;
      }
   }

   private double[] method3476(TokenMotor.Entry4 var1, double[] var2, double[] var3) {
      int var4 = var1.intValue10;
      TokenMotor.Entry var5 = this.method3474(var4);
      TokenMotor.Entry var6 = this.method3474(var4 + 1);
      TokenMotor.Entry var7 = this.method3474(var4 + 2);
      double[] var8 = new double[]{
         mc.options.forwardKey.isPressed() ? 1.0 : 0.0,
         mc.options.backKey.isPressed() ? 1.0 : 0.0,
         mc.options.leftKey.isPressed() ? 1.0 : 0.0,
         mc.options.rightKey.isPressed() ? 1.0 : 0.0,
         mc.options.jumpKey.isPressed() ? 1.0 : 0.0,
         mc.player.isOnGround() ? 1.0 : 0.0,
         mc.player.isSprinting() ? 1.0 : 0.0,
         mc.player.isInSneakingPose() ? 1.0 : 0.0,
         mc.player.getAttackCooldownProgress(0.0F),
         MathHelper.clamp((float)mc.player.ticksSinceLastAttack, 0.0F, 50.0F),
         this.flag3 ? 1.0 : 0.0
      };
      return TokenMotor.Entry4.method3492(
         var1,
         var2,
         new double[]{var5.doubleValue3, var5.doubleValue4},
         new double[]{var6.doubleValue3, var6.doubleValue4},
         new double[]{var7.doubleValue3, var7.doubleValue4},
         new double[]{var5.doubleValue5, var5.doubleValue7, var5.doubleValue6, var5.doubleValue8},
         var5.doubleValue9,
         var6.doubleValue9,
         var8,
         this.doubleValue,
         this.doubleValues2,
         var3
      );
   }

   private void method3477() {
      float var1 = mc.player.getAttackCooldownProgress(0.0F);
      this.flag3 = var1 < this.floatValue - 1.0E-4F;
      this.floatValue = var1;
   }

   private void method3478(int var1) {
      TokenMotor.Entry4 var2 = this.entry4;
      int var3 = var2 == null ? 16 : var2.intValue2;
      this.doubleValues = new double[var3][];
      this.intValues = new int[var3][];
      this.intValue2 = 0;
      Arrays.fill(this.entrys, null);
      this.intValue = 0;
      Arrays.fill(this.doubleValues2, 0.0);
      this.doubleValue = 0.0;
      this.intValues2 = null;
      this.intValue3 = 0;
      this.flag2 = false;
      this.floatValue = 1.0F;
      this.flag3 = false;
      this.intValue4 = var1;
      this.intValue5 = Integer.MIN_VALUE;
      this.intValue6 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3479(int var1) {
      this.intValue4 = var1;
      this.intValue6 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3480(int var1, Rotation var2) {
      this.intValue6 = var1;
      this.rotation = var2;
   }

   public void reset() {
      this.method3478(Integer.MIN_VALUE);
   }

   private TokenMotor.Entry4 method3481() {
      if (this.entry4 != null || this.flag) {
         return this.entry4;
      } else if (this.completableFuture == null) {
         this.longValue = System.currentTimeMillis();
         this.completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
               TokenMotor.Entry4 var0 = method3482();
               var0.method3504();
               return var0;
            } catch (IOException var1) {
               throw new CompletionException(var1);
            }
         });
         return null;
      } else if (!this.completableFuture.isDone()) {
         return null;
      } else {
         try {
            this.entry4 = this.completableFuture.join();
            this.doubleValues = new double[this.entry4.intValue2][];
            this.intValues = new int[this.entry4.intValue2][];
            this.intValue2 = 0;
            System.out
               .println(
                  "v5 token motor loaded: окно "
                     + this.entry4.intValue
                     + ", префикс "
                     + this.entry4.intValue2
                     + ", словарь "
                     + this.entry4.intValue3
                     + ", слоёв "
                     + this.entry4.entry2s.length
                     + ", сенса-константа "
                     + this.entry4.doubleValue4
                     + ", выбор из "
                     + this.entry4.intValue12
                     + " кусками по "
                     + this.entry4.intValue11
                     + ", эталон сошёлся, "
                     + (System.currentTimeMillis() - this.longValue)
                     + " мс"
               );
         } catch (Exception var3) {
            Object var2 = var3 instanceof CompletionException && var3.getCause() != null ? var3.getCause() : var3;
            this.entry4 = null;
            this.flag = true;
            System.out.println("v5 не смог загрузить token_motor.json: " + var2);
         }

         this.completableFuture = null;
         return this.entry4;
      }
   }

   private static TokenMotor.Entry4 method3482() throws IOException {
      for (Path var1 : items) {
         if (Files.isRegularFile(var1)) {
            TokenMotor.Entry4 var2 = TokenMotor.Entry4.method3507(var1);
            System.out.println("v5 token motor: " + var1.toAbsolutePath());
            return var2;
         }
      }

      InputStream var6 = TokenMotor.class.getClassLoader().getResourceAsStream("models/token_motor.json");
      if (var6 == null) {
         throw new IOException("token_motor.json не найден ни рядом с клиентом, ни в jar");
      } else {
         TokenMotor.Entry4 var8;
         try (InputStreamReader var7 = new InputStreamReader(var6, StandardCharsets.UTF_8)) {
            var8 = TokenMotor.Entry4.method3508(var7);
         }

         return var8;
      }
   }

   private static double method3483(double var0) {
      return ((var0 + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
   }

   private static double asinh(double var0) {
      return var0 >= 0.0 ? Math.log(var0 + Math.sqrt(var0 * var0 + 1.0)) : -Math.log(-var0 + Math.sqrt(var0 * var0 + 1.0));
   }

   private static double method3484(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   private static boolean method3485(double[] var0) {
      for (double var4 : var0) {
         if (!Double.isFinite(var4)) {
            return false;
         }
      }

      return true;
   }

   private static double method3486(double var0) {
      return 0.5 * var0 * (1.0 + method3487(var0 / Math.sqrt(2.0)));
   }

   private static double method3487(double var0) {
      double var2 = Math.abs(var0);
      double var4;
      if (var2 < 2.5) {
         double var6 = var2;
         double var8 = var2;
         double var10 = var2 * var2;

         for (int var12 = 1; var12 < 200; var12++) {
            var6 *= -var10 / var12;
            double var13 = var6 / (2 * var12 + 1);
            var8 += var13;
            if (Math.abs(var13) < 1.0E-17 * Math.abs(var8)) {
               break;
            }
         }

         var4 = 2.0 / Math.sqrt(Math.PI) * var8;
      } else {
         double var19 = var2 * var2;
         double var20 = var2;
         double var21 = var2;
         double var22 = 0.0;

         for (int var14 = 1; var14 < 400; var14++) {
            double var15 = var14 / 2.0;
            double var23 = var2 + var15 * var22;
            var22 = var23 == 0.0 ? 1.0E-300 : 1.0 / var23;
            var21 = var2 + var15 / var21;
            double var17 = var21 * var22;
            var20 *= var17;
            if (Math.abs(var17 - 1.0) < 1.0E-16) {
               break;
            }
         }

         double var24 = Math.exp(-var19) / Math.sqrt(Math.PI) / var20;
         var4 = 1.0 - var24;
      }

      return var0 < 0.0 ? -var4 : var4;
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
   }

   private static final class Entry2 {
      public double[][] doubleValues;
      public double[] doubleValues2;
      public double[][] doubleValues3;
      public double[] doubleValues4;
      public double[][] doubleValues5;
      public double[] doubleValues6;
      public double[][] doubleValues7;
      public double[] doubleValues8;
      public double[] doubleValues9;
      public double[] doubleValues10;
      public double[] doubleValues11;
      public double[] doubleValues12;
   }

   private static final class Entry3 {
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public double doubleValue;
      public double doubleValue2;
      public double[] doubleValues6;
      public double doubleValue3;
      public double doubleValue4;
      public double[] doubleValues7;
      public double[] doubleValues8;
      public double[] doubleValues9;
      public int[][] intValues;
      public double[][] doubleValues10;
      public double[] doubleValues11;
      public double[] doubleValues12;
      public int[][] intValues2;
      public int[][] intValues3;
      public double doubleValue5;
      public int[][] intValues4;
      public int[][] intValues5;
   }

   private static final class Entry4 {
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
      public int intValue12;
      public int intValue13;
      public boolean flag;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public int intValue14;
      public int intValue15;
      public int intValue16;
      public double[] doubleValues;
      public final Map<Integer, int[]>[] itemss = new Map[]{new HashMap(), new HashMap()};
      public double[][] doubleValues2;
      public double[][] doubleValues3;
      public double[][] doubleValues4;
      public double[][] doubleValues5;
      public double[] doubleValues6;
      public double[][] doubleValues7;
      public double[] doubleValues8;
      public TokenMotor.Entry2[] entry2s;
      public double[] doubleValues9;
      public double[] doubleValues10;
      public double[][] doubleValues11;
      public double[] doubleValues12;
      public TokenMotor.Entry3 entry3;
      public boolean[][] flags;

      public int method3488(int var1) {
         int var2 = Math.abs(var1);
         if (var2 <= this.intValue14) {
            return var1 + this.intValue14;
         } else {
            int var3 = 0;

            while (var3 + 1 < this.doubleValues.length && this.doubleValues[var3 + 1] <= var2) {
               var3++;
            }

            var3 = Math.max(0, Math.min(var3, this.intValue16 - 1));
            return var1 > 0 ? this.intValue15 + var3 : this.intValue15 + this.intValue16 + var3;
         }
      }

      private double[] method3489(double[] var1, int var2) {
         double[] var3 = (double[])var1.clone();
         boolean[] var4 = this.flags[var2];

         for (int var5 = 0; var5 < var3.length; var5++) {
            if (!var4[var5]) {
               var3[var5] = Double.NEGATIVE_INFINITY;
            }
         }

         return var3;
      }

      public int method3490(int var1, int var2, Random var3) {
         if (var1 < this.intValue15) {
            return var1 - this.intValue14;
         } else {
            int[] var4 = this.itemss[var2].get(var1);
            if (var4 != null && var4.length > 0) {
               return var3 == null ? var4[0] : var4[var3.nextInt(var4.length)];
            } else {
               int var5 = var1 - this.intValue15;
               byte var6 = 1;
               if (var5 >= this.intValue16) {
                  var5 -= this.intValue16;
                  var6 = -1;
               }

               return var6 * (int)Math.round(Math.sqrt(this.doubleValues[var5] * this.doubleValues[var5 + 1]));
            }
         }
      }

      public double[] method3491(double[] var1) {
         double[] var2 = new double[this.intValue7];
         System.arraycopy(var1, 0, var2, 0, this.intValue8);
         return var2;
      }

      public static double[] method3492(
         TokenMotor.Entry4 var0,
         double[] var1,
         double[] var2,
         double[] var3,
         double[] var4,
         double[] var5,
         double var6,
         double var8,
         double[] var10,
         double var11,
         double[] var13,
         double[] var14
      ) {
         double var15 = TokenMotor.method3483(var2[0] - var3[0]);
         double var17 = var2[1] - var3[1];
         var14[0] = var0.doubleValue * var15 + (1.0 - var0.doubleValue) * var13[0];
         var14[1] = var0.doubleValue * var17 + (1.0 - var0.doubleValue) * var13[1];
         var14[2] = var0.doubleValue2 * var15 + (1.0 - var0.doubleValue2) * var13[2];
         var14[3] = var0.doubleValue2 * var17 + (1.0 - var0.doubleValue2) * var13[3];
         var14[4] = var0.doubleValue3 * Math.abs(var15) + (1.0 - var0.doubleValue3) * var13[4];
         var14[5] = var0.doubleValue3 * Math.abs(var17) + (1.0 - var0.doubleValue3) * var13[5];
         double var19 = TokenMotor.method3483(var3[0] - var4[0]);
         double var21 = var3[1] - var4[1];
         double var23 = 0.5 * (var5[2] - var5[0]);
         double var25 = 0.5 * (var5[3] - var5[1]);
         double[] var27 = new double[var0.intValue7];
         int var28 = 0;
         var27[var28++] = TokenMotor.asinh(var15);
         var27[var28++] = TokenMotor.asinh(var17);
         var27[var28++] = TokenMotor.asinh(var15 - var19);
         var27[var28++] = TokenMotor.asinh(var17 - var21);
         var27[var28++] = TokenMotor.asinh(var23);
         var27[var28++] = TokenMotor.asinh(var25);
         var27[var28++] = Math.log1p(var6);
         var27[var28++] = TokenMotor.method3484(var6 - var8, -2.0, 2.0);
         var27[var28++] = Math.log(var0.doubleValue4);

         for (double var32 : var10) {
            var27[var28++] = var32;
         }

         for (int var89 = 0; var89 < 6; var89++) {
            var27[var28++] = TokenMotor.asinh(var14[var89]);
         }

         double var90 = TokenMotor.method3483(var2[0] - var1[0]);
         double var91 = var2[1] - var1[1];
         double var33 = var90 + (var0.intValue10 + 1) * var15;
         double var35 = var91 + (var0.intValue10 + 1) * var17;
         double var37 = Math.max(Math.hypot(var90, var91), 1.0E-6);
         double var39 = 0.5 * (var5[0] + var5[2]);
         double var41 = 0.5 * (var5[1] + var5[3]);
         double var43 = Math.max(var23, 0.001);
         double var45 = Math.max(var25, 0.001);
         double var47 = TokenMotor.method3484((-var90 - var39) / var43, -4.0, 4.0);
         double var49 = TokenMotor.method3484((-var91 - var41) / var45, -4.0, 4.0);
         double var51 = Math.max(Math.abs(var47), Math.abs(var49));
         double var53 = 1.0 / (1.0 + Math.exp(-6.0 * (1.0 - var51)));
         double var55 = -var15 / var43;
         double var57 = -var17 / var45;
         double var59 = Math.max(1.0 - var47 * (var55 >= 0.0 ? 1.0 : -1.0), 0.0);
         double var61 = Math.max(1.0 - var49 * (var57 >= 0.0 ? 1.0 : -1.0), 0.0);
         double var63 = Math.min(var59 / Math.max(Math.abs(var55), 1.0E-4), var61 / Math.max(Math.abs(var57), 1.0E-4));
         var63 = TokenMotor.method3484(var63, 0.0, 400.0);
         var27[var28++] = TokenMotor.asinh(var90);
         var27[var28++] = TokenMotor.asinh(var91);
         var27[var28++] = TokenMotor.asinh(var33);
         var27[var28++] = TokenMotor.asinh(var35);
         var27[var28++] = TokenMotor.asinh(var37);
         var27[var28++] = var90 / var37;
         var27[var28++] = var91 / var37;
         var27[var28++] = var47;
         var27[var28++] = var49;
         var27[var28++] = var53;
         var27[var28++] = Math.tanh(var63 / 8.0);
         var27[var28++] = 1.0 / (1.0 + var63);
         var27[var28++] = var1[1] / 90.0;
         var27[var28++] = (90.0 - Math.abs(var1[1])) / 90.0;
         var27[var28++] = Math.tanh(var11 / 32.0);
         var27[var28] = 1.0;
         return var27;
      }

      public static double method3493(TokenMotor.Entry4 var0, TokenMotor.Entry var1, TokenMotor.Entry var2, double[] var3, double var4, double var6) {
         return method3494(
            var0,
            new double[]{var1.doubleValue3, var1.doubleValue4},
            new double[]{var2.doubleValue3, var2.doubleValue4},
            new double[]{var1.doubleValue5, var1.doubleValue7, var1.doubleValue6, var1.doubleValue8},
            var3,
            var4,
            var6
         );
      }

      public static double method3494(TokenMotor.Entry4 var0, double[] var1, double[] var2, double[] var3, double[] var4, double var5, double var7) {
         double var9 = TokenMotor.method3483(var1[0] - var2[0]);
         double var11 = var1[1] - var2[1];
         int var13 = var0.intValue11 - 1 + var0.intValue10;
         double var14 = TokenMotor.method3483(var1[0] + var13 * var9 - (var4[0] + var5));
         double var16 = var1[1] + var13 * var11 - TokenMotor.method3484(var4[1] + var7, -90.0, 90.0);
         double var18 = 0.5 * (var3[0] + var3[2]);
         double var20 = 0.5 * (var3[1] + var3[3]);
         double var22 = Math.max(0.5 * (var3[2] - var3[0]), 0.001);
         double var24 = Math.max(0.5 * (var3[3] - var3[1]), 0.001);
         return Math.max(Math.abs((-var14 - var18) / var22), Math.abs((-var16 - var20) / var24));
      }

      public double[][] method3495(int[][] var1, double[][] var2) {
         int var3 = var1.length;
         int var4 = var3 * 2;
         int var5 = this.intValue5;
         double[][] var6 = new double[var4][var5];

         for (int var7 = 0; var7 < var3; var7++) {
            double[] var8 = this.method3496(var2[var7]);

            for (int var9 = 0; var9 < 2; var9++) {
               double[] var10 = var6[var7 * 2 + var9];
               double[] var11 = this.doubleValues2[var1[var7][var9]];
               double[] var12 = this.doubleValues3[var7];
               double[] var13 = this.doubleValues4[var9];

               for (int var14 = 0; var14 < var5; var14++) {
                  var10[var14] = var11[var14] + var12[var14] + var13[var14] + var8[var14];
               }
            }
         }

         int var18 = var5 / this.intValue6;
         double var19 = 1.0 / Math.sqrt(var18);

         for (TokenMotor.Entry2 var24 : this.entry2s) {
            double[][] var25 = new double[var4][var5];
            double[][] var15 = new double[var4][var5];
            double[][] var16 = new double[var4][var5];
            IntStream.range(0, var4).parallel().forEach(var6x -> {
               double[] var7x = method3498(var24.doubleValues, method3497(var6[var6x], var24.doubleValues9, var24.doubleValues10), var24.doubleValues2);
               System.arraycopy(var7x, 0, var25[var6x], 0, var5);
               System.arraycopy(var7x, var5, var15[var6x], 0, var5);
               System.arraycopy(var7x, 2 * var5, var16[var6x], 0, var5);
            });
            double[][] var17 = new double[var4][var5];
            IntStream.range(0, this.intValue6 * var4).parallel().forEach(var8x -> {
               int var9x = var8x / var4;
               int var10x = var8x % var4;
               int var11x = var9x * var18;
               double[] var12x = new double[var4];
               double var13x = Double.NEGATIVE_INFINITY;

               for (int var15x = 0; var15x < var4; var15x++) {
                  double var16x = 0.0;

                  for (int var18x = 0; var18x < var18; var18x++) {
                     var16x += var25[var10x][var11x + var18x] * var15[var15x][var11x + var18x];
                  }

                  var12x[var15x] = var16x * var19;
                  var13x = Math.max(var13x, var12x[var15x]);
               }

               double var21x = 0.0;

               for (int var17x = 0; var17x < var4; var17x++) {
                  var12x[var17x] = Math.exp(var12x[var17x] - var13x);
                  var21x += var12x[var17x];
               }

               for (int var22 = 0; var22 < var18; var22++) {
                  double var23 = 0.0;

                  for (int var20 = 0; var20 < var4; var20++) {
                     var23 += var12x[var20] * var16[var20][var11x + var22];
                  }

                  var17[var10x][var11x + var22] = var23 / var21x;
               }
            });
            IntStream.range(0, var4).parallel().forEach(var4x -> {
               double[] var5x = method3498(var24.doubleValues3, var17[var4x], var24.doubleValues4);

               for (int var6x = 0; var6x < var5; var6x++) {
                  var6[var4x][var6x] = var6[var4x][var6x] + var5x[var6x];
               }

               double[] var9x = method3498(var24.doubleValues5, method3497(var6[var4x], var24.doubleValues11, var24.doubleValues12), var24.doubleValues6);

               for (int var7x = 0; var7x < var9x.length; var7x++) {
                  var9x[var7x] = TokenMotor.method3486(var9x[var7x]);
               }

               double[] var10x = method3498(var24.doubleValues7, var9x, var24.doubleValues8);

               for (int var8x = 0; var8x < var5; var8x++) {
                  var6[var4x][var8x] = var6[var4x][var8x] + var10x[var8x];
               }
            });
         }

         double[][] var21 = new double[var4][];
         IntStream.range(0, var4)
            .parallel()
            .forEach(
               var3x -> var21[var3x] = method3498(this.doubleValues11, method3497(var6[var3x], this.doubleValues9, this.doubleValues10), this.doubleValues12)
            );
         return var21;
      }

      private double[] method3496(double[] var1) {
         double[] var2 = method3498(this.doubleValues5, var1, this.doubleValues6);

         for (int var3 = 0; var3 < var2.length; var3++) {
            var2[var3] /= 1.0 + Math.exp(-var2[var3]);
         }

         return method3498(this.doubleValues7, var2, this.doubleValues8);
      }

      private static double[] method3497(double[] var0, double[] var1, double[] var2) {
         double var3 = 0.0;

         for (double var8 : var0) {
            var3 += var8;
         }

         var3 /= var0.length;
         double var13 = 0.0;

         for (double var10 : var0) {
            var13 += (var10 - var3) * (var10 - var3);
         }

         var13 /= var0.length;
         double var16 = 1.0 / Math.sqrt(var13 + 1.0E-5);
         double[] var18 = new double[var0.length];

         for (int var19 = 0; var19 < var0.length; var19++) {
            var18[var19] = (var0[var19] - var3) * var16 * var1[var19] + var2[var19];
         }

         return var18;
      }

      private static double[] method3498(double[][] var0, double[] var1, double[] var2) {
         double[] var3 = new double[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            double var5 = var2[var4];
            double[] var7 = var0[var4];

            for (int var8 = 0; var8 < var1.length; var8++) {
               var5 += var7[var8] * var1[var8];
            }

            var3[var4] = var5;
         }

         return var3;
      }

      public int[][] method3499(int[][] var1, double[][] var2, int var3, boolean var4, boolean var5, Random var6) {
         int var7 = var1.length;
         int var8 = var7 * 2;

         for (int var9 = 0; var9 < Math.max(1, var3); var9++) {
            int var10 = 0;

            for (int var11 = 0; var11 < var8; var11++) {
               if (var1[var11 / 2][var11 % 2] == this.intValue4) {
                  var10++;
               }
            }

            if (var10 == 0) {
               break;
            }

            double[][] var22 = this.method3495(var1, var2);
            int[] var12 = new int[var8];
            double[] var13 = new double[var8];

            for (int var14 = 0; var14 < var8; var14++) {
               boolean var15 = var1[var14 / 2][var14 % 2] == this.intValue4;
               double[] var16 = method3501(this.method3489(var22[var14], var14 % 2));
               var12[var14] = var5 ? method3502(var16, var6) : method3503(var16);
               var13[var14] = !var15 ? -1.0 : (var4 ? var6.nextDouble() : var16[var12[var14]]);
            }

            int var24 = Math.min(Math.max(1, (int)Math.ceil((double)var10 / Math.max(1, var3))), var10);
            boolean[] var25 = new boolean[var8];

            for (int var26 = 0; var26 < var24; var26++) {
               int var17 = -1;

               for (int var18 = 0; var18 < var8; var18++) {
                  if (!var25[var18] && var13[var18] >= 0.0 && (var17 < 0 || var13[var18] > var13[var17])) {
                     var17 = var18;
                  }
               }

               if (var17 < 0) {
                  break;
               }

               var25[var17] = true;
               var1[var17 / 2][var17 % 2] = var12[var17];
            }
         }

         boolean var19 = false;

         for (int var20 = 0; var20 < var8; var20++) {
            if (var1[var20 / 2][var20 % 2] == this.intValue4) {
               var19 = true;
               break;
            }
         }

         if (var19) {
            double[][] var21 = this.method3495(var1, var2);

            for (int var23 = 0; var23 < var8; var23++) {
               if (var1[var23 / 2][var23 % 2] == this.intValue4) {
                  var1[var23 / 2][var23 % 2] = method3503(this.method3489(var21[var23], var23 % 2));
               }
            }
         }

         return var1;
      }

      public int[][] method3500(int[][] var1, double[][] var2, int var3, boolean var4, Random var5) {
         for (int var6 = 0; var6 < var3; var6++) {
            for (int var7 = 0; var7 < 2; var7++) {
               double[][] var8 = this.method3495(var1, var2);
               double[] var9 = method3501(this.method3489(var8[(this.intValue2 + var6) * 2 + var7], var7));
               var1[this.intValue2 + var6][var7] = var4 ? method3502(var9, var5) : method3503(var9);
            }
         }

         return var1;
      }

      private static double[] method3501(double[] var0) {
         double var1 = Double.NEGATIVE_INFINITY;

         for (double var6 : var0) {
            var1 = Math.max(var1, var6);
         }

         double[] var8 = new double[var0.length];
         double var9 = 0.0;

         for (int var10 = 0; var10 < var0.length; var10++) {
            var8[var10] = Math.exp(var0[var10] - var1);
            var9 += var8[var10];
         }

         for (int var11 = 0; var11 < var0.length; var11++) {
            var8[var11] /= var9;
         }

         return var8;
      }

      private static int method3502(double[] var0, Random var1) {
         double var2 = var1.nextDouble();
         double var4 = 0.0;

         for (int var6 = 0; var6 < var0.length; var6++) {
            var4 += var0[var6];
            if (var2 < var4) {
               return var6;
            }
         }

         return var0.length - 1;
      }

      private static int method3503(double[] var0) {
         int var1 = 0;

         for (int var2 = 1; var2 < var0.length; var2++) {
            if (var0[var2] > var0[var1]) {
               var1 = var2;
            }
         }

         return var1;
      }

      public void method3504() {
         TokenMotor.Entry3 var1 = this.entry3;
         if (var1 == null) {
            throw new IllegalStateException("в модели нет эталонного тика");
         } else {
            double[] var2 = new double[6];
            double[] var3 = method3492(
               this,
               var1.doubleValues,
               var1.doubleValues2,
               var1.doubleValues3,
               var1.doubleValues4,
               var1.doubleValues5,
               var1.doubleValue,
               var1.doubleValue2,
               var1.doubleValues6,
               var1.doubleValue4,
               var1.doubleValues7,
               var2
            );
            method3505(method3506(var3, var1.doubleValues8), "контекст");
            method3505(method3506(var2, var1.doubleValues9), "EMA");
            double[][] var4 = this.method3495(var1.intValues, var1.doubleValues10);
            method3505(method3506(var4[this.intValue2 * 2], var1.doubleValues11), "логиты yaw");
            method3505(method3506(var4[this.intValue2 * 2 + 1], var1.doubleValues12), "логиты pitch");
            int[][] var5 = this.method3499(TokenMotor.method3471(var1.intValues), var1.doubleValues10, this.intValue13, false, false, null);

            for (int var6 = 0; var6 < this.intValue; var6++) {
               for (int var7 = 0; var7 < 2; var7++) {
                  if (var5[var6][var7] != var1.intValues2[var6][var7]) {
                     throw new IllegalStateException(
                        "эталон: заполнение разошлось в позиции " + var6 + "/" + var7 + ": " + var5[var6][var7] + " вместо " + var1.intValues2[var6][var7]
                     );
                  }
               }
            }

            double var15 = 0.0;
            double var8 = 0.0;

            for (int var10 = 0; var10 < this.intValue11; var10++) {
               int var11 = this.method3490(var5[this.intValue2 + var10][0], 0, null);
               int var12 = this.method3490(var5[this.intValue2 + var10][1], 1, null);
               if (var11 != var1.intValues3[var10][0] || var12 != var1.intValues3[var10][1]) {
                  throw new IllegalStateException("эталон: декод тика " + var10 + " разошёлся");
               }

               var15 += var11 * this.doubleValue4;
               var8 += var12 * this.doubleValue4;
            }

            double var16 = method3494(this, var1.doubleValues2, var1.doubleValues3, var1.doubleValues5, var1.doubleValues, var15, var8);
            method3505(Math.abs(var16 - var1.doubleValue5), "оценка плана");
            if (var1.intValues4 == null) {
               if (this.flag) {
                  throw new IllegalStateException("файл требует режим first, но эталона для него нет");
               }
            } else {
               int[][] var17 = this.method3500(TokenMotor.method3471(var1.intValues), var1.doubleValues10, this.intValue11, false, null);

               for (int var13 = 0; var13 < this.intValue11; var13++) {
                  for (int var14 = 0; var14 < 2; var14++) {
                     if (var17[this.intValue2 + var13][var14] != var1.intValues4[var13][var14]) {
                        throw new IllegalStateException(
                           "эталон first: тик "
                              + var13
                              + "/"
                              + var14
                              + ": "
                              + var17[this.intValue2 + var13][var14]
                              + " вместо "
                              + var1.intValues4[var13][var14]
                        );
                     }
                  }

                  if (this.method3490(var17[this.intValue2 + var13][0], 0, null) != var1.intValues5[var13][0]
                     || this.method3490(var17[this.intValue2 + var13][1], 1, null) != var1.intValues5[var13][1]) {
                     throw new IllegalStateException("эталон first: декод тика " + var13 + " разошёлся");
                  }
               }
            }
         }
      }

      private static void method3505(double var0, String var2) {
         if (!(var0 <= 1.0E-9)) {
            throw new IllegalStateException(
               "эталон не сошёлся: max|Δ| = " + var0 + " в '" + var2 + "', допуск 1.0E-9. Раскладка весов не та, что ожидает рантайм."
            );
         }
      }

      private static double method3506(double[] var0, double[] var1) {
         if (var0.length != var1.length) {
            return Double.POSITIVE_INFINITY;
         } else {
            double var2 = 0.0;

            for (int var4 = 0; var4 < var0.length; var4++) {
               var2 = Math.max(var2, Math.abs(var0[var4] - var1[var4]));
            }

            return var2;
         }
      }

      public static TokenMotor.Entry4 method3507(Path var0) throws IOException {
         TokenMotor.Entry4 var2;
         try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
            var2 = method3508(var1);
         }

         return var2;
      }

      public static TokenMotor.Entry4 method3508(Reader var0) throws IOException {
         TokenMotor.Entry4 var1 = new TokenMotor.Entry4();
         JsonReader var2 = new JsonReader(var0);

         try {
            var2.beginObject();

            while (var2.hasNext()) {
               String var3 = var2.nextName();
               switch (var3) {
                  case "format":
                     String var5 = var2.nextString();
                     if (!"neurotest-token-motor-v2".equals(var5)) {
                        throw new IllegalStateException("формат '" + var5 + "', ожидался 'neurotest-token-motor-v2'");
                     }
                     break;
                  case "config":
                     var1.method3509(var2);
                     break;
                  case "decode_pools":
                     var1.method3511(var2);
                     break;
                  case "support":
                     var1.method3510(var2);
                     break;
                  case "weights":
                     var1.method3512(var2);
                     break;
                  case "golden":
                     var1.method3513(var2);
                     break;
                  default:
                     var2.skipValue();
               }
            }

            var2.endObject();
         } catch (Throwable var7) {
            try {
               var2.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         var2.close();
         var1.method3514();
         return var1;
      }

      private void method3509(JsonReader var1) throws IOException {
         var1.beginObject();

         while (var1.hasNext()) {
            String var2 = var1.nextName();
            switch (var2) {
               case "window":
                  this.intValue = var1.nextInt();
                  break;
               case "prefix":
                  this.intValue2 = var1.nextInt();
                  break;
               case "vocab":
                  this.intValue3 = var1.nextInt();
                  break;
               case "mask_id":
                  this.intValue4 = var1.nextInt();
                  break;
               case "d_model":
                  this.intValue5 = var1.nextInt();
                  break;
               case "n_head":
                  this.intValue6 = var1.nextInt();
                  break;
               case "n_ctx":
                  this.intValue7 = var1.nextInt();
                  break;
               case "n_ctx_exo":
                  this.intValue8 = var1.nextInt();
                  break;
               case "n_ctx_endo":
                  this.intValue9 = var1.nextInt();
                  break;
               case "latency":
                  this.intValue10 = var1.nextInt();
                  break;
               case "exec_k":
                  this.intValue11 = var1.nextInt();
                  break;
               case "best_of":
                  this.intValue12 = var1.nextInt();
                  break;
               case "steps_fill":
                  this.intValue13 = var1.nextInt();
                  break;
               case "fill_mode":
                  this.flag = "first".equals(var1.nextString());
                  break;
               case "mdpc":
                  this.doubleValue4 = var1.nextDouble();
                  break;
               case "vocab_n_named":
                  this.intValue14 = var1.nextInt();
                  break;
               case "vocab_n_named_tok":
                  this.intValue15 = var1.nextInt();
                  break;
               case "vocab_n_bucket":
                  this.intValue16 = var1.nextInt();
                  break;
               case "vocab_edges":
                  this.doubleValues = method3516(var1);
                  break;
               case "ema":
                  var1.beginObject();

                  while (var1.hasNext()) {
                     String var4 = var1.nextName();
                     switch (var4) {
                        case "a8":
                           this.doubleValue = var1.nextDouble();
                           break;
                        case "a32":
                           this.doubleValue2 = var1.nextDouble();
                           break;
                        case "a16":
                           this.doubleValue3 = var1.nextDouble();
                           break;
                        default:
                           var1.skipValue();
                     }
                  }

                  var1.endObject();
                  break;
               default:
                  var1.skipValue();
            }
         }

         var1.endObject();
      }

      private void method3510(JsonReader var1) throws IOException {
         this.flags = new boolean[2][];
         var1.beginObject();

         while (var1.hasNext()) {
            String var2 = var1.nextName();
            int var3 = var2.equals("yaw") ? 0 : (var2.equals("pitch") ? 1 : -1);
            double[] var4 = method3516(var1);
            if (var3 >= 0) {
               this.flags[var3] = new boolean[var4.length];

               for (int var5 = 0; var5 < var4.length; var5++) {
                  this.flags[var3][var5] = var4[var5] > 0.5;
               }
            }
         }

         var1.endObject();
      }

      private void method3511(JsonReader var1) throws IOException {
         var1.beginObject();

         while (var1.hasNext()) {
            String var2 = var1.nextName();
            int var3 = "yaw".equals(var2) ? 0 : 1;
            var1.beginObject();

            while (var1.hasNext()) {
               int var4 = Integer.parseInt(var1.nextName());
               double[] var5 = method3516(var1);
               int[] var6 = new int[var5.length];

               for (int var7 = 0; var7 < var5.length; var7++) {
                  var6[var7] = (int)Math.round(var5[var7]);
               }

               this.itemss[var3].put(var4, var6);
            }

            var1.endObject();
         }

         var1.endObject();
      }

      private void method3512(JsonReader var1) throws IOException {
         ArrayList var2 = new ArrayList();
         var1.beginObject();

         while (var1.hasNext()) {
            String var3 = var1.nextName();
            switch (var3) {
               case "tok":
                  this.doubleValues2 = method3518(var1);
                  break;
               case "pos":
                  this.doubleValues3 = method3518(var1);
                  break;
               case "axis":
                  this.doubleValues4 = method3518(var1);
                  break;
               case "ctx0_w":
                  this.doubleValues5 = method3518(var1);
                  break;
               case "ctx0_b":
                  this.doubleValues6 = method3516(var1);
                  break;
               case "ctx2_w":
                  this.doubleValues7 = method3518(var1);
                  break;
               case "ctx2_b":
                  this.doubleValues8 = method3516(var1);
                  break;
               case "norm_w":
                  this.doubleValues9 = method3516(var1);
                  break;
               case "norm_b":
                  this.doubleValues10 = method3516(var1);
                  break;
               case "out_w":
                  this.doubleValues11 = method3518(var1);
                  break;
               case "out_b":
                  this.doubleValues12 = method3516(var1);
                  break;
               case "layers":
                  var1.beginArray();

                  while (var1.hasNext()) {
                     TokenMotor.Entry2 var5 = new TokenMotor.Entry2();
                     var1.beginObject();

                     while (var1.hasNext()) {
                        String var6 = var1.nextName();
                        switch (var6) {
                           case "in_w":
                              var5.doubleValues = method3518(var1);
                              break;
                           case "in_b":
                              var5.doubleValues2 = method3516(var1);
                              break;
                           case "out_w":
                              var5.doubleValues3 = method3518(var1);
                              break;
                           case "out_b":
                              var5.doubleValues4 = method3516(var1);
                              break;
                           case "l1_w":
                              var5.doubleValues5 = method3518(var1);
                              break;
                           case "l1_b":
                              var5.doubleValues6 = method3516(var1);
                              break;
                           case "l2_w":
                              var5.doubleValues7 = method3518(var1);
                              break;
                           case "l2_b":
                              var5.doubleValues8 = method3516(var1);
                              break;
                           case "n1_w":
                              var5.doubleValues9 = method3516(var1);
                              break;
                           case "n1_b":
                              var5.doubleValues10 = method3516(var1);
                              break;
                           case "n2_w":
                              var5.doubleValues11 = method3516(var1);
                              break;
                           case "n2_b":
                              var5.doubleValues12 = method3516(var1);
                              break;
                           default:
                              var1.skipValue();
                        }
                     }

                     var1.endObject();
                     var2.add(var5);
                  }

                  var1.endArray();
                  break;
               default:
                  var1.skipValue();
            }
         }

         var1.endObject();
         this.entry2s = (TokenMotor.Entry2[])var2.toArray(new TokenMotor.Entry2[0]);
      }

      private void method3513(JsonReader var1) throws IOException {
         TokenMotor.Entry3 var2 = new TokenMotor.Entry3();
         var1.beginObject();

         while (var1.hasNext()) {
            String var3 = var1.nextName();
            switch (var3) {
               case "inputs":
                  var1.beginObject();

                  while (var1.hasNext()) {
                     String var5 = var1.nextName();
                     switch (var5) {
                        case "cam":
                           var2.doubleValues = method3516(var1);
                           break;
                        case "tgt_lag":
                           var2.doubleValues2 = method3516(var1);
                           break;
                        case "tgt_lag1":
                           var2.doubleValues3 = method3516(var1);
                           break;
                        case "tgt_lag2":
                           var2.doubleValues4 = method3516(var1);
                           break;
                        case "box":
                           var2.doubleValues5 = method3516(var1);
                           break;
                        case "dist":
                           var2.doubleValue = var1.nextDouble();
                           break;
                        case "dist_prev":
                           var2.doubleValue2 = var1.nextDouble();
                           break;
                        case "ctx11":
                           var2.doubleValues6 = method3516(var1);
                           break;
                        case "mdpc":
                           var2.doubleValue3 = var1.nextDouble();
                           break;
                        case "since_zero":
                           var2.doubleValue4 = var1.nextDouble();
                           break;
                        case "slow":
                           var2.doubleValues7 = method3516(var1);
                           break;
                        default:
                           var1.skipValue();
                     }
                  }

                  var1.endObject();
                  break;
               case "context":
                  var2.doubleValues8 = method3516(var1);
                  break;
               case "slow_out":
                  var2.doubleValues9 = method3516(var1);
                  break;
               case "tokens_in":
                  var2.intValues = method3515(method3518(var1));
                  break;
               case "ctx_window":
                  var2.doubleValues10 = method3518(var1);
                  break;
               case "logits_yaw_at_p":
                  var2.doubleValues11 = method3516(var1);
                  break;
               case "logits_pitch_at_p":
                  var2.doubleValues12 = method3516(var1);
                  break;
               case "filled_greedy":
                  var2.intValues2 = method3515(method3518(var1));
                  break;
               case "decoded_first":
                  var2.intValues3 = method3515(method3518(var1));
                  break;
               case "plan_score":
                  var2.doubleValue5 = var1.nextDouble();
                  break;
               case "first_greedy":
                  var2.intValues4 = method3515(method3518(var1));
                  break;
               case "decoded_first_mode":
                  var2.intValues5 = method3515(method3518(var1));
                  break;
               default:
                  var1.skipValue();
            }
         }

         var1.endObject();
         this.entry3 = var2;
      }

      private void method3514() {
         if (this.doubleValues2 != null && this.entry2s != null && this.doubleValues11 != null && this.doubleValues != null) {
            if (this.doubleValues2.length != this.intValue3 + 1 || this.doubleValues2[0].length != this.intValue5) {
               throw new IllegalStateException(
                  "эмбеддинг токенов "
                     + this.doubleValues2.length
                     + "x"
                     + this.doubleValues2[0].length
                     + ", ожидалось "
                     + (this.intValue3 + 1)
                     + "x"
                     + this.intValue5
               );
            } else if (this.doubleValues5[0].length != this.intValue7 || this.intValue7 != this.intValue8 + this.intValue9) {
               throw new IllegalStateException("ширина контекста не сходится");
            } else if (this.intValue15 + 2 * this.intValue16 != this.intValue3) {
               throw new IllegalStateException("словарь: " + this.intValue15 + " + 2*" + this.intValue16 + " != " + this.intValue3);
            } else if (this.intValue11 < 2 && this.intValue12 > 1) {
               throw new IllegalStateException("выбор из K требует кусков длиннее одного тика");
            } else if (this.flags != null
               && this.flags[0] != null
               && this.flags[1] != null
               && this.flags[0].length == this.intValue3
               && this.flags[1].length == this.intValue3) {
               for (int var1 = 0; var1 < 2; var1++) {
                  boolean var2 = false;

                  for (boolean var6 : this.flags[var1]) {
                     var2 |= var6;
                  }

                  if (!var2) {
                     throw new IllegalStateException("носитель оси " + var1 + " пуст");
                  }
               }
            } else {
               throw new IllegalStateException("в файле нет носителя `support` на " + this.intValue3 + " токенов");
            }
         } else {
            throw new IllegalStateException("в файле нет весов или словаря");
         }
      }

      private static int[][] method3515(double[][] var0) {
         int[][] var1 = new int[var0.length][];

         for (int var2 = 0; var2 < var0.length; var2++) {
            var1[var2] = new int[var0[var2].length];

            for (int var3 = 0; var3 < var0[var2].length; var3++) {
               var1[var2][var3] = (int)Math.round(var0[var2][var3]);
            }
         }

         return var1;
      }

      private static double[] method3516(JsonReader var0) throws IOException {
         var0.beginArray();
         double[] var1 = method3517(var0);
         var0.endArray();
         return var1;
      }

      private static double[] method3517(JsonReader var0) throws IOException {
         double[] var1 = new double[32];
         int var2 = 0;

         while (var0.hasNext()) {
            if (var2 == var1.length) {
               var1 = Arrays.copyOf(var1, var2 * 2);
            }

            var1[var2++] = var0.nextDouble();
         }

         return Arrays.copyOf(var1, var2);
      }

      private static double[][] method3518(JsonReader var0) throws IOException {
         ArrayList var1 = new ArrayList();
         var0.beginArray();

         while (var0.hasNext()) {
            if (var0.peek() == JsonToken.BEGIN_ARRAY) {
               var1.add(method3516(var0));
            } else {
               var1.add(method3517(var0));
            }
         }

         var0.endArray();
         return (double[][])var1.toArray(new double[0][]);
      }
   }
}

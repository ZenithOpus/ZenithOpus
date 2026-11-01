package org.zenith.base.rotation.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public final class Agent extends Util22 {
   private static final String text = "agent.json";
   private static final String text2 = "models/agent.json";
   private static final List<Path> items = List.of(Path.of("agent.json"), Path.of("models", "agent.json"), Path.of("..", "models", "agent.json"));
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final String text3 = "neurotest-agent-v1";
   private static final double CONST_doubleValue = 0.15190947;
   private static final double CONST_doubleValue2 = 1.0E-4;
   private static Agent.Entry entry;
   private static boolean flag;
   private final Random random = new Random();
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private Rotation rotation;
   private double[] doubleValues;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;

   private static synchronized Agent.Entry method3452() {
      if (flag) {
         return entry;
      } else {
         flag = true;

         try (InputStreamReader var0 = new InputStreamReader(method3453(), StandardCharsets.UTF_8)) {
            JsonReader var1 = new JsonReader(var0);

            try {
               entry = method3454(var1);
            } catch (Throwable var6) {
               try {
                  var1.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            var1.close();
         } catch (Exception var8) {
            System.out.println("агент не загрузился: " + var8);
            entry = null;
         }

         return entry;
      }
   }

   private static InputStream method3453() throws IOException {
      for (Path var1 : items) {
         if (Files.isRegularFile(var1)) {
            return Files.newInputStream(var1);
         }
      }

      InputStream var2 = Agent.class.getClassLoader().getResourceAsStream("models/agent.json");
      if (var2 == null) {
         throw new IOException("agent.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return var2;
      }
   }

   private static Agent.Entry method3454(JsonReader var0) throws IOException {
      Agent.Entry var1 = new Agent.Entry();
      HashMap var2 = new HashMap();
      HashMap var3 = new HashMap();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      String var6 = null;
      var0.beginObject();

      while (var0.hasNext()) {
         String var7 = var0.nextName();
         switch (var7) {
            case "format":
               var6 = var0.nextString();
               break;
            case "n_obs":
               var1.intValue = var0.nextInt();
               break;
            case "hidden":
               var1.intValue2 = var0.nextInt();
               break;
            case "logsig_min":
               var1.doubleValue = var0.nextDouble();
               break;
            case "logsig_max":
               var1.doubleValue2 = var0.nextDouble();
               break;
            case "weights":
               var0.beginObject();

               while (var0.hasNext()) {
                  String var10 = var0.nextName();
                  ArrayList var11 = new ArrayList();
                  int[] var12 = getInstance(var0, var11);
                  double[] var13 = new double[var11.size()];

                  for (int var14 = 0; var14 < var13.length; var14++) {
                     var13[var14] = (Double)var11.get(var14);
                  }

                  var2.put(var10, var13);
                  var3.put(var10, var12);
               }

               var0.endObject();
               break;
            case "ref_inputs":
               method3455(var0, var4);
               break;
            case "ref_outputs":
               method3455(var0, var5);
               break;
            default:
               var0.skipValue();
         }
      }

      var0.endObject();
      if (!"neurotest-agent-v1".equals(var6)) {
         throw new IOException("формат " + var6 + ", ожидался neurotest-agent-v1");
      } else {
         var1.doubleValues = method3457((double[])var2.get("gru.weight_ih"), 3 * var1.intValue2, var1.intValue);
         var1.doubleValues2 = method3457((double[])var2.get("gru.weight_hh"), 3 * var1.intValue2, var1.intValue2);
         var1.doubleValues3 = (double[])var2.get("gru.bias_ih");
         var1.doubleValues4 = (double[])var2.get("gru.bias_hh");
         var1.doubleValues5 = method3457((double[])var2.get("head.weight"), 4, var1.intValue2);
         var1.doubleValues6 = (double[])var2.get("head.bias");
         var1.doubleValues7 = method3457((double[])var2.get("gate.weight"), 1, var1.intValue2)[0];
         var1.doubleValue3 = ((double[])var2.get("gate.bias"))[0];
         double[] var15 = new double[var1.intValue2];
         double var8 = 0.0;

         for (int var16 = 0; var16 < var4.size(); var16++) {
            var15 = var1.method3465((double[])var4.get(var16), var15);
            double[] var17 = var1.method3466(var15);
            double[] var18 = (double[])var5.get(var16);

            for (int var19 = 0; var19 < var18.length; var19++) {
               var8 = Math.max(var8, Math.abs(var17[var19] - var18[var19]));
            }
         }

         if (var8 > 1.0E-4) {
            throw new IOException("эталонные тики разошлись на " + var8 + " — раскладка весов не та");
         } else {
            System.out.println("агент загружен: скрытых " + var1.intValue2 + ", эталон сошёлся (" + var8 + ")");
            return var1;
         }
      }
   }

   private static void method3455(JsonReader var0, List<double[]> var1) throws IOException {
      var0.beginArray();

      while (var0.hasNext()) {
         ArrayList var2 = new ArrayList();
         var0.beginArray();

         while (var0.hasNext()) {
            var2.add(var0.nextDouble());
         }

         var0.endArray();
         double[] var3 = new double[var2.size()];

         for (int var4 = 0; var4 < var3.length; var4++) {
            var3[var4] = (Double)var2.get(var4);
         }

         var1.add(var3);
      }

      var0.endArray();
   }

   private static int[] getInstance(JsonReader var0, List<Double> var1) throws IOException {
      ArrayList var2 = new ArrayList();
      method3456(var0, var1, var2, 0);
      int[] var3 = new int[var2.size()];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = (Integer)var2.get(var4);
      }

      return var3;
   }

   private static void method3456(JsonReader var0, List<Double> var1, List<Integer> var2, int var3) throws IOException {
      var0.beginArray();

      int var4;
      for (var4 = 0; var0.hasNext(); var4++) {
         if (var0.peek() == JsonToken.BEGIN_ARRAY) {
            method3456(var0, var1, var2, var3 + 1);
         } else {
            var1.add(var0.nextDouble());
         }
      }

      var0.endArray();

      while (var2.size() <= var3) {
         var2.add(0);
      }

      var2.set(var3, var4);
   }

   private static double[][] method3457(double[] var0, int var1, int var2) throws IOException {
      if (var0 != null && var0.length == var1 * var2) {
         double[][] var3 = new double[var1][var2];

         for (int var4 = 0; var4 < var1; var4++) {
            System.arraycopy(var0, var4 * var2, var3[var4], 0, var2);
         }

         return var3;
      } else {
         throw new IOException("не тот размер весов: ждали " + var1 + "x" + var2 + ", пришло " + (var0 == null ? 0 : var0.length));
      }
   }

   private static double method3458(double var0) {
      double var2 = (var0 + 180.0) % 360.0;
      if (var2 < 0.0) {
         var2 += 360.0;
      }

      return var2 - 180.0;
   }

   private void method3459(int var1) {
      this.doubleValues = null;
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.intValue = var1;
      this.intValue2 = Integer.MIN_VALUE;
      this.intValue3 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3460(int var1, Rotation var2) {
      this.intValue3 = var1;
      this.rotation = var2;
   }

   public void reset() {
      this.method3459(Integer.MIN_VALUE);
   }

   public boolean method3461() {
      return method3452() != null;
   }

   public Rotation method3462() {
      Rotation var1 = rotationManager.method7844();
      Agent.Entry var2 = method3452();
      if (var2 == null) {
         this.reset();
         return mc.player == null ? var1 : new Rotation(mc.player.getYaw(), mc.player.getPitch(), true);
      } else {
         LivingEntity var3 = AuraModule.INSTANCE.method6358();
         if (mc.player != null && var3 != null && var3.isAlive()) {
            int var4 = mc.player.age;
            if (var4 == this.intValue3 && this.rotation != null) {
               return this.rotation;
            } else {
               if (this.intValue2 != Integer.MIN_VALUE && var4 != this.intValue2 + 1) {
                  this.method3459(var3.getId());
               } else if (var3.getId() != this.intValue) {
                  this.method3459(var3.getId());
               }

               this.intValue2 = var4;
               this.intValue = var3.getId();
               if (this.doubleValues == null || this.doubleValues.length != var2.intValue2) {
                  this.doubleValues = new double[var2.intValue2];
               }

               Box var5 = var3.getBoundingBox();
               Vec3d var6 = mc.player.getEyePos();
               Vec3d var7 = var5.getCenter();
               double var8 = var7.x - var6.x;
               double var10 = var7.y - var6.y;
               double var12 = var7.z - var6.z;
               double var14 = Math.sqrt(var8 * var8 + var12 * var12);
               double var16 = Math.toDegrees(Math.atan2(-var8, var12));
               double var18 = Math.toDegrees(Math.atan2(-var10, var14));
               double var20 = 0.001;
               double var22 = 0.001;

               for (int var24 = 0; var24 < 8; var24++) {
                  double var25 = ((var24 & 1) == 0 ? var5.minX : var5.maxX) - var6.x;
                  double var27 = ((var24 & 2) == 0 ? var5.minY : var5.maxY) - var6.y;
                  double var29 = ((var24 & 4) == 0 ? var5.minZ : var5.maxZ) - var6.z;
                  double var31 = Math.sqrt(var25 * var25 + var29 * var29);
                  var20 = Math.max(var20, Math.abs(method3458(Math.toDegrees(Math.atan2(-var25, var29)) - var16)));
                  var22 = Math.max(var22, Math.abs(Math.toDegrees(Math.atan2(-var27, var31)) - var18));
               }

               double var48 = method3458(var16 - var1.getFloatValue());
               double var26 = var18 - var1.getFloatValue2();
               double var28 = Math.max(Math.hypot(var48, var26), 1.0E-6);
               PlayerInput var30 = mc.player.input.playerInput;
               double[] var49 = new double[var2.intValue];
               var49[0] = method3463(var48 / var20, -6.0, 6.0);
               var49[1] = method3463(var26 / var22, -6.0, 6.0);
               var49[2] = Math.log1p(var28) / 5.0;
               var49[3] = var48 / var28;
               var49[4] = var26 / var28;
               var49[5] = Math.log1p(var20) / 3.0;
               var49[6] = Math.log1p(var22) / 3.0;
               var49[7] = var30.forward() ? 1.0 : 0.0;
               var49[8] = var30.backward() ? 1.0 : 0.0;
               var49[9] = var30.left() ? 1.0 : 0.0;
               var49[10] = var30.right() ? 1.0 : 0.0;
               var49[11] = var30.jump() ? 1.0 : 0.0;
               var49[12] = mc.player.isOnGround() ? 1.0 : 0.0;
               var49[13] = mc.player.isSprinting() ? 1.0 : 0.0;
               var49[14] = mc.player.getAttackCooldownProgress(0.0F);
               var49[15] = method3463(this.doubleValue3 / 10.0, -6.0, 6.0);
               var49[16] = method3463(this.doubleValue4 / 10.0, -6.0, 6.0);
               var49[17] = Math.min(Math.hypot(this.doubleValue3, this.doubleValue4), 60.0) / 10.0;
               this.doubleValues = var2.method3465(var49, this.doubleValues);
               double[] var32 = var2.method3466(this.doubleValues);
               double var33 = 1.0 / (1.0 + Math.exp(-var32[4]));
               double var35 = 0.0;
               double var37 = 0.0;
               if (this.random.nextDouble() >= var33) {
                  var35 = var32[0] + Math.exp(var32[2]) * this.random.nextGaussian();
                  var37 = var32[1] + Math.exp(var32[3]) * this.random.nextGaussian();
               }

               double var39 = var35 / 0.15190947 + this.doubleValue;
               double var41 = var37 / 0.15190947 + this.doubleValue2;
               long var43 = (long)var39;
               long var45 = (long)var41;
               this.doubleValue = var39 - var43;
               this.doubleValue2 = var41 - var45;
               this.doubleValue3 = var43 * 0.15190947;
               this.doubleValue4 = var45 * 0.15190947;
               Rotation var47 = var1.method3140((float)this.doubleValue3, (float)this.doubleValue4);
               this.method3460(var4, var47);
               return var47;
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
   }

   private static double method3463(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   public static String method3464() {
      Agent.Entry var0 = method3452();
      return var0 == null ? "МОДЕЛЬ НЕ ЗАГРУЖЕНА" : "загружено: признаков " + var0.intValue + ", скрытых " + var0.intValue2 + ", эталонные тики сошлись";
   }

   private static final class Entry {
      public int intValue;
      public int intValue2;
      public double doubleValue;
      public double doubleValue2;
      public double[][] doubleValues;
      public double[][] doubleValues2;
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[][] doubleValues5;
      public double[] doubleValues6;
      public double[] doubleValues7;
      public double doubleValue3;

      public double[] method3465(double[] var1, double[] var2) {
         double[] var3 = new double[3 * this.intValue2];
         double[] var4 = new double[3 * this.intValue2];

         for (int var5 = 0; var5 < 3 * this.intValue2; var5++) {
            double var6 = this.doubleValues3[var5];

            for (int var8 = 0; var8 < this.intValue; var8++) {
               var6 += this.doubleValues[var5][var8] * var1[var8];
            }

            var3[var5] = var6;
            double var15 = this.doubleValues4[var5];

            for (int var10 = 0; var10 < this.intValue2; var10++) {
               var15 += this.doubleValues2[var5][var10] * var2[var10];
            }

            var4[var5] = var15;
         }

         double[] var13 = new double[this.intValue2];

         for (int var14 = 0; var14 < this.intValue2; var14++) {
            double var7 = 1.0 / (1.0 + Math.exp(-(var3[var14] + var4[var14])));
            double var9 = 1.0 / (1.0 + Math.exp(-(var3[this.intValue2 + var14] + var4[this.intValue2 + var14])));
            double var11 = Math.tanh(var3[2 * this.intValue2 + var14] + var7 * var4[2 * this.intValue2 + var14]);
            var13[var14] = (1.0 - var9) * var11 + var9 * var2[var14];
         }

         return var13;
      }

      public double[] method3466(double[] var1) {
         double[] var2 = new double[5];

         for (int var3 = 0; var3 < 4; var3++) {
            double var4 = this.doubleValues6[var3];

            for (int var6 = 0; var6 < this.intValue2; var6++) {
               var4 += this.doubleValues5[var3][var6] * var1[var6];
            }

            var2[var3] = var3 < 2 ? var4 : Math.max(this.doubleValue, Math.min(this.doubleValue2, var4));
         }

         double var7 = this.doubleValue3;

         for (int var5 = 0; var5 < this.intValue2; var5++) {
            var7 += this.doubleValues7[var5] * var1[var5];
         }

         var2[4] = var7;
         return var2;
      }
   }
}

package org.zenith.utility.game;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zenith.Zenith;
import org.zenith.utility.misc.FlakeUtil;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.render.Glowboost;

public final class Print {
   private static final int CONST_intValue = 128;
   private static final double CONST_doubleValue = 0.62;
   private static final double CONST_doubleValue2 = 0.13;
   private static final double CONST_doubleValue3 = 0.012;
   private static final double CONST_doubleValue4 = 0.006;
   private static final int CONST_intValue2 = 14;
   private static final float CONST_floatValue = 3.5F;
   private static final float CONST_floatValue2 = 0.55F;
   private static final float CONST_floatValue3 = 0.014F;
   private static final Identifier identifier = Zenith.method1501("visuals/weather/print.png");
   private static final Identifier identifier2 = Zenith.method1501("visuals/weather/flake.png");
   private static final long CONST_longValue = 420L;
   private static final double CONST_doubleValue5 = 0.105;
   private static final double CONST_doubleValue6 = 0.165;
   private final ArrayDeque<Print.Data> arrayDeque = new ArrayDeque<>();
   private final Map<UUID, Print.Entry> items = new HashMap<>();

   public void method5440(World var1, Iterable<? extends PlayerEntity> var2, WeatherFlakeChannel var3, long var4) {
      if (var3.intValue7 > 0 && var1 != null) {
         for (PlayerEntity var7 : var2) {
            if (!var7.isSpectator() && var7.isOnGround()) {
               double var8 = var7.getX();
               double var10 = var7.getZ();
               double var12 = var7.getY();
               UUID var14 = var7.getUuid();
               Print.Entry var15 = this.items.get(var14);
               if (var15 == null) {
                  var15 = new Print.Entry();
                  var15.doubleValue = var8;
                  var15.doubleValue2 = var10;
                  this.items.put(var14, var15);
               }

               var15.longValue = var4;
               double var16 = var8 - var15.doubleValue;
               double var18 = var10 - var15.doubleValue2;
               if (!(var16 * var16 + var18 * var18 < 0.3844)) {
                  var15.doubleValue = var8;
                  var15.doubleValue2 = var10;
                  var15.left = !var15.left;
                  float var20 = var7.getYaw();
                  double var21 = Math.toRadians(var20);
                  double var23 = Math.cos(var21);
                  double var25 = Math.sin(var21);
                  double var27 = var15.left ? -0.13 : 0.13;
                  double var29 = var8 + var23 * var27;
                  double var31 = var10 + var25 * var27;
                  if (method5441(var1, var29, var12, var31)) {
                     this.method5442(new Print.Data(var29, var12 + 0.012, var31, var20, var15.left, var4));
                  }
               }
            }
         }

         var var33 = this.items.values().iterator();

         while (var33.hasNext()) {
            if (var4 - ((Print.Entry)var33.next()).longValue > 30000L) {
               var33.remove();
            }
         }

         this.method5443(var3, var4);
      }
   }

   private static boolean method5441(World var0, double var1, double var3, double var5) {
      BlockPos var7 = BlockPos.ofFloored(var1, var3 - 0.1, var5);
      if (!var0.isChunkLoaded(var7.getX() >> 4, var7.getZ() >> 4)) {
         return false;
      } else {
         BlockState var8 = var0.getBlockState(var7);
         return !var8.isAir() && var8.isFullCube(var0, var7) && var8.getFluidState().isEmpty();
      }
   }

   private void method5442(Print.Data var1) {
      if (this.arrayDeque.size() >= 128) {
         this.arrayDeque.pollFirst();
      }

      this.arrayDeque.addLast(var1);
   }

   private void method5443(WeatherFlakeChannel var1, long var2) {
      var var4 = this.arrayDeque.iterator();

      while (var4.hasNext() && var2 - ((Print.Data)var4.next()).getLongValue() > var1.intValue7) {
         var4.remove();
      }
   }

   public void clear() {
      this.arrayDeque.clear();
      this.items.clear();
   }

   public void method5444(Glowboost var1, WeatherFlakeChannel var2, float var3, long var4, double var6, double var8, double var10) {
      if (var2.intValue7 > 0 && !this.arrayDeque.isEmpty()) {
         var1.method3244(identifier);
         boolean var12 = false;

         for (Print.Data var14 : this.arrayDeque) {
            float var15 = (float)(var4 - var14.getLongValue()) / var2.intValue7;
            if (!(var15 >= 1.0F)) {
               double var16 = var14.getDoubleValue() - var6;
               double var18 = var14.getDoubleValue3() - var10;
               double var20 = Math.sqrt(var16 * var16 + var18 * var18);
               if (!(var20 > 14.0)
                  && !(Math.abs(var14.getDoubleValue2() - var8) > 12.0)
                  && (!(var20 > 3.0) || !(var16 * var1.getDoubleValue10() + var18 * var1.getDoubleValue12() < -0.25 * var20))) {
                  float var22 = (float)Math.min(1.0, (14.0 - var20) / 3.5);
                  double var23 = var2.flag ? FlakeUtil.method7509(var14.getDoubleValue(), var14.getDoubleValue3()) + 0.006 : 0.0;
                  method5447(
                     var1,
                     var2,
                     var14.getDoubleValue(),
                     var14.getDoubleValue2() + var23,
                     var14.getDoubleValue3(),
                     var14.getFloatValue() + (var14.isFlag() ? -8.0F : 8.0F),
                     var3 * method5449(var15) * var22,
                     var15
                  );
                  var12 |= var2.flag && var4 - var14.getLongValue() < 420L;
               }
            }
         }

         if (var12) {
            var1.method3244(identifier2);

            for (Print.Data var26 : this.arrayDeque) {
               long var27 = var4 - var26.getLongValue();
               if (var27 < 420L) {
                  method5445(var1, var26, (float)var27 / 420.0F, var3, FlakeUtil.method7509(var26.getDoubleValue(), var26.getDoubleValue3()));
               }
            }
         }
      }
   }

   private static void method5445(Glowboost var0, Print.Data var1, float var2, float var3, float var4) {
      float var5 = (1.0F - var2) * (1.0F - var2);
      int var6 = method5451(-591105, (int)(150.0F * var5 * Math.clamp(var3, 0.0F, 1.0F)));
      if (var6 >>> 24 != 0) {
         long var7 = Double.doubleToLongBits(var1.getDoubleValue()) ^ Long.rotateLeft(var1.getLongValue(), 17);

         for (int var9 = 0; var9 < 5; var9++) {
            float var10 = 0.05F + 0.16F * Glowboost.method3268(var7, var9, 0);
            double var11 = Glowboost.method3268(var7, var9, 1) * Math.PI * 2.0;
            double var13 = var1.getDoubleValue() + Math.cos(var11) * var10 * (0.4 + var2);
            double var15 = var1.getDoubleValue3() + Math.sin(var11) * var10 * (0.4 + var2);
            double var17 = 0.2 * (0.5 + 0.5 * Glowboost.method3268(var7, var9, 2));
            double var19 = var1.getDoubleValue2() + var4 + var17 * (var2 * 2.2 - var2 * var2 * 2.4);
            float var21 = 0.035F + 0.03F * Glowboost.method3268(var7, var9, 3);
            var0.method3249(var13, var19, var15, 0.0, 1.0, 0.0, var21, var21, 0.0F, 0.0F, 1.0F, 1.0F, var6);
         }
      }
   }

   public static void method5446(Glowboost var0, WeatherFlakeChannel var1, float var2) {
      if (var1.intValue7 > 0) {
         var0.method3244(identifier);

         for (int var3 = 0; var3 < 6; var3++) {
            boolean var4 = (var3 & 1) == 0;
            double var5 = var4 ? -0.13 : 0.13;
            double var7 = 2.4 - var3 * 0.62;
            double var9 = var7 <= -2.0 ? 1.0 : 0.0;
            float var11 = 0.35F + 0.65F * (var3 / 5.0F);
            method5447(var0, var1, -0.4 + var5, var9 + 0.012, var7, var4 ? -8.0F : 8.0F, var2 * var11, 1.0F - var11);
         }
      }
   }

   private static void method5447(Glowboost var0, WeatherFlakeChannel var1, double var2, double var4, double var6, float var8, float var9, float var10) {
      if (!(var9 <= 0.01F)) {
         double var11 = Math.toRadians(var8);
         double var13 = -Math.sin(var11);
         double var15 = Math.cos(var11);
         int var17 = method5451(var1.intValue6, (int)((var1.intValue6 >>> 24 & 0xFF) * Math.clamp(var9, 0.0F, 1.0F)));
         if (var1.flag) {
            float var18 = Math.clamp(1.0F - var10 * 1.8F, 0.0F, 1.0F) * var9;
            if (var18 > 0.02F) {
               method5448(var0, var2, var4, var6, var13, var15, 1.22, method5451(-722689, (int)(70.0F * var18)));
            }

            method5448(var0, var2, var4 + 0.001, var6, var13, var15, 1.0 - 0.35 * var10, var17);
         } else {
            method5448(var0, var2, var4, var6, var13, var15, 1.0, var17);
         }
      }
   }

   private static void method5448(Glowboost var0, double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
      var0.method3246(var1, var3, var5, var7, var9, 0.105 * var11, 0.165 * var11, 0.0F, 0.0F, 1.0F, 1.0F, var13);
   }

   private static float method5449(float var0) {
      float var1 = method5450(Math.clamp(var0 / 0.014F, 0.0F, 1.0F));
      if (var0 <= 0.55F) {
         return var1;
      } else {
         float var2 = (var0 - 0.55F) / 0.45F;
         return var1 * (1.0F - method5450(Math.clamp(var2, 0.0F, 1.0F)));
      }
   }

   private static float method5450(float var0) {
      return var0 * var0 * (3.0F - 2.0F * var0);
   }

   private static int method5451(int var0, int var1) {
      return Math.clamp((long)var1, 0, 255) << 24 | var0 & 16777215;
   }

   private static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final boolean flag;
      private final long longValue;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, floatValue, flag, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(double var1, double var3, double var5, float var7, boolean var8, long var9) {
         this.doubleValue = var1;
         this.doubleValue2 = var3;
         this.doubleValue3 = var5;
         this.floatValue = var7;
         this.flag = var8;
         this.longValue = var9;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private double doubleValue;
      private double doubleValue2;
      private boolean left;
      private long longValue;
   }
}

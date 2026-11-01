package org.zenith.base.rotation.mods;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public final class SlewRotationMod extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final double CONST_doubleValue = 0.15190947;
   private static final double CONST_doubleValue2 = 0.3;
   private static final double CONST_doubleValue3 = 1.8;
   private static double doubleValue = 3.0;
   private static final double CONST_doubleValue4 = 0.8;
   private static final double CONST_doubleValue5 = 0.0;
   private static final double CONST_doubleValue6 = 0.7;
   private static final double CONST_doubleValue7 = 1.6;
   private static final double CONST_doubleValue8 = 4.2;
   private static final int CONST_intValue2 = 2;
   private static final int CONST_intValue3 = 10;
   private static final double CONST_doubleValue9 = 0.233125;
   private static final double CONST_doubleValue10 = 0.424375;
   private static final double CONST_doubleValue11 = 0.0;
   private static final double CONST_doubleValue12 = 2.125;
   private static final double CONST_doubleValue13 = 0.446;
   private static final double CONST_doubleValue14 = 0.0625;
   private static final double CONST_doubleValue15 = 38.0;
   private static final SlewRotationMod.Data data = new SlewRotationMod.Data(
      0.2014583333,
      0.02,
      1.0,
      300.0,
      0.2,
      85.0,
      0.3975,
      0.77,
      0.685,
      800.0,
      0.85,
      0.685,
      0.4166875,
      0.77,
      0.05,
      0.75,
      0.31828125,
      0.281875,
      3.0,
      60.0,
      23.725,
      0.735,
      0.1353125,
      0.049921875,
      1.2,
      801.0,
      0.6,
      24
   );
   private static final SlewRotationMod.Data data2 = new SlewRotationMod.Data(
      0.54953125,
      0.0105625,
      1.0,
      300.0,
      0.2,
      85.0,
      0.4275,
      0.425,
      0.561,
      300.0,
      0.125,
      0.561,
      0.88334375,
      0.43,
      0.5896875,
      0.75,
      0.1363541667,
      0.865,
      3.0,
      60.0,
      2.0,
      0.514609375,
      0.06859375,
      0.006953125,
      1.2,
      312.0,
      0.6,
      24
   );
   private final SlewRotationMod.Entry entry = new SlewRotationMod.Entry(data, 11L);
   private final SlewRotationMod.Entry entry2 = new SlewRotationMod.Entry(data2, 12L);
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private Rotation rotation;
   private Vec3d vec3d;
   private Vec3d vec3d2;
   private double doubleValue2 = Double.NaN;
   private double doubleValue3 = Double.NaN;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private double doubleValue7;
   private boolean flag;
   private boolean flag2;
   private long longValue = 1374496523L;
   private static final double CONST_doubleValue16 = 1184.9162530815229;

   public Rotation method3636() {
      Rotation var1 = rotationManager.method7844();
      LivingEntity var2 = AuraModule.INSTANCE.method6358();
      if (mc.player != null && var2 != null && var2.isAlive()) {
         int var3 = mc.player.age;
         if (var3 == this.intValue3 && this.rotation != null) {
            return this.rotation;
         } else {
            if (var2.getId() != this.intValue || this.intValue2 != Integer.MIN_VALUE && var3 != this.intValue2 + 1) {
               this.method3640(var2.getId(), var3);
            }

            this.intValue2 = var3;
            float var4 = Rotation.method3138();
            if (Float.isFinite(var4) && !(var4 <= 0.0F)) {
               Vec3d var5 = mc.player.getEyePos();
               Vec3d var6 = var2.getBoundingBox().getCenter();
               Vec3d var7 = var6;
               if (this.vec3d2 != null && doubleValue > 0.0) {
                  Vec3d var8 = var6.subtract(this.vec3d2);
                  double var9 = var8.length();
                  if (var9 > 0.8) {
                     var8 = var8.multiply(0.8 / var9);
                  }

                  var7 = var6.add(var8.multiply(doubleValue));
               }

               Rotation var31 = RotationUtils.method6912(var7.subtract(var5));
               Vec2f var32 = var1.method3131(var31);
               double var10 = var32.getFloatValue() / 0.15190947;
               double var12 = var32.getFloatValue2() / 0.15190947;
               double var14 = 0.0;
               double var16 = 0.0;
               if (this.vec3d != null) {
                  Rotation var18 = RotationUtils.method6912(var7.subtract(this.vec3d));
                  Vec2f var19 = var18.method3131(var31);
                  var14 = var19.getFloatValue() / 0.15190947;
                  var16 = var19.getFloatValue2() / 0.15190947;
               }

               if (Double.isFinite(this.doubleValue2)) {
                  this.doubleValue6 = method3639(this.doubleValue6, var10 - this.doubleValue2 + this.doubleValue4);
                  this.doubleValue7 = method3639(this.doubleValue7, var12 - this.doubleValue3 + this.doubleValue5);
               }

               this.doubleValue2 = var10;
               this.doubleValue3 = var12;
               this.vec3d = var5;
               this.vec3d2 = var6;
               double var33 = Math.max(0.5, var6.distanceTo(var5));
               double var20 = Math.toDegrees(Math.atan(0.3 / var33)) / 0.15190947;
               double var22 = Math.toDegrees(Math.atan(0.9 / var33)) / 0.15190947;
               if (Double.isFinite(var10) && Double.isFinite(var12) && Double.isFinite(var14) && Double.isFinite(var16)) {
                  if (this.flag) {
                     this.entry.method3642(this.longValue++);
                     this.flag = false;
                  }

                  double var24 = this.entry.method3649(var10, this.doubleValue6, var20, var14, false);
                  if (this.flag2) {
                     this.entry2.method3642(this.longValue++);
                     this.flag2 = false;
                  }

                  double var26 = this.entry2.method3649(var12, this.doubleValue7, var22, var16, var24 == 0.0);
                  this.doubleValue4 = var24;
                  this.doubleValue5 = var26;
                  int var28 = (int)Math.floor(var24 * 0.15190947 / var4 + 0.5);
                  int var29 = (int)Math.floor(var26 * 0.15190947 / var4 + 0.5);
                  if (var28 == 0 && var29 == 0) {
                     this.method3641(var3, var1);
                     return var1;
                  } else {
                     doubleValue = 3.0;
                     Rotation var30 = var1.method3140(var28 * var4, var29 * var4);
                     System.out.println("yawCounts=" + var24 + " yawErr=" + var10 + " yawDrift=" + this.doubleValue6 + " lead=" + doubleValue);
                     this.method3641(var3, var30);
                     return var30;
                  }
               } else {
                  this.method3640(var2.getId(), var3);
                  this.method3641(var3, var1);
                  return var1;
               }
            } else {
               this.method3641(var3, var1);
               return var1;
            }
         }
      } else {
         this.reset();
         return var1;
      }
   }

   private static double method3637(double var0, double var2) {
      double var4 = var0 * 0.7;
      return Math.copySign(Math.min(Math.abs(var4), var2), var0);
   }

   public boolean method3638() {
      return true;
   }

   private static double method3639(double var0, double var2) {
      double var4 = 2369.8325061630458;
      double var6 = var2 - var4 * Math.floor((var2 + 1184.9162530815229) / var4);
      double var8 = Math.max(-900.0, Math.min(900.0, var6));
      return 0.6 * var0 + 0.4 * var8;
   }

   private void method3640(int var1, int var2) {
      long var3 = Integer.toUnsignedLong(var1) * -7046029254386353131L ^ Integer.toUnsignedLong(var2);
      this.entry.method3642(var3 * 2L + 1L);
      this.entry2.method3642(var3 * 2L + 2L);
      this.intValue = var1;
      this.intValue2 = Integer.MIN_VALUE;
      this.intValue3 = Integer.MIN_VALUE;
      this.rotation = null;
      this.vec3d = null;
      this.vec3d2 = null;
      this.doubleValue2 = Double.NaN;
      this.doubleValue3 = Double.NaN;
      this.doubleValue4 = 0.0;
      this.doubleValue5 = 0.0;
      this.doubleValue6 = 0.0;
      this.doubleValue7 = 0.0;
      this.flag = false;
      this.flag2 = false;
   }

   private void method3641(int var1, Rotation var2) {
      this.intValue3 = var1;
      this.rotation = var2;
   }

   public void reset() {
      this.method3640(Integer.MIN_VALUE, 0);
   }

   private static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final double doubleValue4;
      private final double doubleValue5;
      private final double doubleValue6;
      private final double doubleValue7;
      private final double doubleValue8;
      private final double doubleValue9;
      private final double doubleValue10;
      private final double doubleValue11;
      private final double doubleValue12;
      private final double doubleValue13;
      private final double doubleValue14;
      private final double doubleValue15;
      private final double doubleValue16;
      private final double doubleValue17;
      private final double doubleValue18;
      private final double doubleValue19;
      private final double doubleValue20;
      private final double doubleValue21;
      private final double doubleValue22;
      private final double doubleValue23;
      private final double doubleValue24;
      private final double doubleValue25;
      private final double doubleValue26;
      private final double doubleValue27;
      private final int intValue;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public double doubleValue4() {
         return this.doubleValue4;
      }

      public double doubleValue5() {
         return this.doubleValue5;
      }

      public double doubleValue6() {
         return this.doubleValue6;
      }

      public double doubleValue7() {
         return this.doubleValue7;
      }

      public double doubleValue8() {
         return this.doubleValue8;
      }

      public double doubleValue9() {
         return this.doubleValue9;
      }

      public double doubleValue10() {
         return this.doubleValue10;
      }

      public double doubleValue11() {
         return this.doubleValue11;
      }

      public double doubleValue12() {
         return this.doubleValue12;
      }

      public double doubleValue13() {
         return this.doubleValue13;
      }

      public double doubleValue14() {
         return this.doubleValue14;
      }

      public double doubleValue15() {
         return this.doubleValue15;
      }

      public double doubleValue16() {
         return this.doubleValue16;
      }

      public double doubleValue17() {
         return this.doubleValue17;
      }

      public double doubleValue18() {
         return this.doubleValue18;
      }

      public double doubleValue19() {
         return this.doubleValue19;
      }

      public double doubleValue20() {
         return this.doubleValue20;
      }

      public double doubleValue21() {
         return this.doubleValue21;
      }

      public double doubleValue22() {
         return this.doubleValue22;
      }

      public double doubleValue23() {
         return this.doubleValue23;
      }

      public double doubleValue24() {
         return this.doubleValue24;
      }

      public double doubleValue25() {
         return this.doubleValue25;
      }

      public double doubleValue26() {
         return this.doubleValue26;
      }

      public double doubleValue27() {
         return this.doubleValue27;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.doubleValue4, other.doubleValue4) && java.util.Objects.equals(this.doubleValue5, other.doubleValue5) && java.util.Objects.equals(this.doubleValue6, other.doubleValue6) && java.util.Objects.equals(this.doubleValue7, other.doubleValue7) && java.util.Objects.equals(this.doubleValue8, other.doubleValue8) && java.util.Objects.equals(this.doubleValue9, other.doubleValue9) && java.util.Objects.equals(this.doubleValue10, other.doubleValue10) && java.util.Objects.equals(this.doubleValue11, other.doubleValue11) && java.util.Objects.equals(this.doubleValue12, other.doubleValue12) && java.util.Objects.equals(this.doubleValue13, other.doubleValue13) && java.util.Objects.equals(this.doubleValue14, other.doubleValue14) && java.util.Objects.equals(this.doubleValue15, other.doubleValue15) && java.util.Objects.equals(this.doubleValue16, other.doubleValue16) && java.util.Objects.equals(this.doubleValue17, other.doubleValue17) && java.util.Objects.equals(this.doubleValue18, other.doubleValue18) && java.util.Objects.equals(this.doubleValue19, other.doubleValue19) && java.util.Objects.equals(this.doubleValue20, other.doubleValue20) && java.util.Objects.equals(this.doubleValue21, other.doubleValue21) && java.util.Objects.equals(this.doubleValue22, other.doubleValue22) && java.util.Objects.equals(this.doubleValue23, other.doubleValue23) && java.util.Objects.equals(this.doubleValue24, other.doubleValue24) && java.util.Objects.equals(this.doubleValue25, other.doubleValue25) && java.util.Objects.equals(this.doubleValue26, other.doubleValue26) && java.util.Objects.equals(this.doubleValue27, other.doubleValue27) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, doubleValue4, doubleValue5, doubleValue6, doubleValue7, doubleValue8, doubleValue9, doubleValue10, doubleValue11, doubleValue12, doubleValue13, doubleValue14, doubleValue15, doubleValue16, doubleValue17, doubleValue18, doubleValue19, doubleValue20, doubleValue21, doubleValue22, doubleValue23, doubleValue24, doubleValue25, doubleValue26, doubleValue27, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "doubleValue4=" + this.doubleValue4 + ", " + "doubleValue5=" + this.doubleValue5 + ", " + "doubleValue6=" + this.doubleValue6 + ", " + "doubleValue7=" + this.doubleValue7 + ", " + "doubleValue8=" + this.doubleValue8 + ", " + "doubleValue9=" + this.doubleValue9 + ", " + "doubleValue10=" + this.doubleValue10 + ", " + "doubleValue11=" + this.doubleValue11 + ", " + "doubleValue12=" + this.doubleValue12 + ", " + "doubleValue13=" + this.doubleValue13 + ", " + "doubleValue14=" + this.doubleValue14 + ", " + "doubleValue15=" + this.doubleValue15 + ", " + "doubleValue16=" + this.doubleValue16 + ", " + "doubleValue17=" + this.doubleValue17 + ", " + "doubleValue18=" + this.doubleValue18 + ", " + "doubleValue19=" + this.doubleValue19 + ", " + "doubleValue20=" + this.doubleValue20 + ", " + "doubleValue21=" + this.doubleValue21 + ", " + "doubleValue22=" + this.doubleValue22 + ", " + "doubleValue23=" + this.doubleValue23 + ", " + "doubleValue24=" + this.doubleValue24 + ", " + "doubleValue25=" + this.doubleValue25 + ", " + "doubleValue26=" + this.doubleValue26 + ", " + "doubleValue27=" + this.doubleValue27 + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(
         double var1,
         double var3,
         double var5,
         double var7,
         double var9,
         double var11,
         double var13,
         double var15,
         double var17,
         double var19,
         double var21,
         double var23,
         double var25,
         double var27,
         double var29,
         double var31,
         double var33,
         double var35,
         double var37,
         double var39,
         double var41,
         double var43,
         double var45,
         double var47,
         double var49,
         double var51,
         double var53,
         int var55
      ) {
         if (!(var27 > 0.0) || !(var27 <= 1.0)) {
            throw new IllegalArgumentException("slew must lie in (0, 1]");
         } else if (!(var25 > 0.0) || !(var25 <= 1.0)) {
            throw new IllegalArgumentException("reviseProbability must lie in (0, 1]");
         } else if (var47 > var45) {
            throw new IllegalArgumentException("resting must be likelier when settled than when there is work");
         } else if (!(var51 <= 0.0) && !(var49 < 1.0)) {
            this.doubleValue = var1;
            this.doubleValue2 = var3;
            this.doubleValue3 = var5;
            this.doubleValue4 = var7;
            this.doubleValue5 = var9;
            this.doubleValue6 = var11;
            this.doubleValue7 = var13;
            this.doubleValue8 = var15;
            this.doubleValue9 = var17;
            this.doubleValue10 = var19;
            this.doubleValue11 = var21;
            this.doubleValue12 = var23;
            this.doubleValue13 = var25;
            this.doubleValue14 = var27;
            this.doubleValue15 = var29;
            this.doubleValue16 = var31;
            this.doubleValue17 = var33;
            this.doubleValue18 = var35;
            this.doubleValue19 = var37;
            this.doubleValue20 = var39;
            this.doubleValue21 = var41;
            this.doubleValue22 = var43;
            this.doubleValue23 = var45;
            this.doubleValue24 = var47;
            this.doubleValue25 = var49;
            this.doubleValue26 = var51;
            this.doubleValue27 = var53;
            this.intValue = var55;
         } else {
            throw new IllegalArgumentException("bad rate or hold duration");
         }
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

      public double getDoubleValue4() {
         return this.doubleValue4;
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

      public double getDoubleValue9() {
         return this.doubleValue9;
      }

      public double getDoubleValue10() {
         return this.doubleValue10;
      }

      public double getDoubleValue11() {
         return this.doubleValue11;
      }

      public double getDoubleValue12() {
         return this.doubleValue12;
      }

      public double getDoubleValue13() {
         return this.doubleValue13;
      }

      public double getDoubleValue14() {
         return this.doubleValue14;
      }

      public double getDoubleValue15() {
         return this.doubleValue15;
      }

      public double getDoubleValue16() {
         return this.doubleValue16;
      }

      public double getDoubleValue17() {
         return this.doubleValue17;
      }

      public double getDoubleValue18() {
         return this.doubleValue18;
      }

      public double getDoubleValue19() {
         return this.doubleValue19;
      }

      public double getDoubleValue20() {
         return this.doubleValue20;
      }

      public double getDoubleValue21() {
         return this.doubleValue21;
      }

      public double getDoubleValue22() {
         return this.doubleValue22;
      }

      public double getDoubleValue23() {
         return this.doubleValue23;
      }

      public double getDoubleValue24() {
         return this.doubleValue24;
      }

      public double getDoubleValue25() {
         return this.doubleValue25;
      }

      public double getDoubleValue26() {
         return this.doubleValue26;
      }

      public double getDoubleValue27() {
         return this.doubleValue27;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Entry {
      private final SlewRotationMod.Data data;
      private Random random;
      private double[] doubleValues = new double[0];
      private int intValue;
      private double doubleValue;
      private double doubleValue2;
      private int intValue2;
      private int intValue3;
      private int intValue4;
      private Boolean booleanValue;

      private Entry(SlewRotationMod.Data var1, long var2) {
         this.data = var1;
         this.method3642(var2);
      }

      private void method3642(long var1) {
         this.random = new Random(var1);
         this.doubleValues = new double[0];
         this.intValue = 0;
         this.doubleValue = 0.0;
         this.doubleValue2 = 0.0;
         this.intValue2 = 0;
         this.intValue3 = 0;
         this.intValue4 = 0;
         this.booleanValue = null;
      }

      private double method3643() {
         double var1 = Math.max(1.0E-9, this.data.getDoubleValue14());
         double var3 = this.data.getDoubleValue27() * this.data.getDoubleValue13() / var1;
         return Math.min(this.data.getDoubleValue3(), var3);
      }

      private double method3644(double var1) {
         return Double.isFinite(var1) && !(var1 <= 0.0)
            ? Math.min(this.data.getDoubleValue20(), Math.max(this.data.getDoubleValue19(), this.data.getDoubleValue18() * var1))
            : this.data.getDoubleValue19();
      }

      private int method3645(double var1, double var3) {
         double var5 = Math.max(2.0 * Math.abs(var3), 1.0);
         double var7 = Math.log(Math.abs(var1) / var5 + 1.0) / Math.log(2.0);
         long var9 = Math.round(1.6 + 4.2 * var7);
         return (int)Math.max(2L, Math.min(10L, var9));
      }

      private double method3646(double var1) {
         double var3 = 2.125 - 0.446 * Math.log1p(Math.abs(var1));
         if (this.booleanValue != null) {
            var3 += this.booleanValue ? 0.0625 : -0.0625;
         }

         return 1.0 / (1.0 + Math.exp(-Math.max(-30.0, Math.min(30.0, var3))));
      }

      private double method3647(double var1) {
         double var3 = this.data.getDoubleValue4();
         double var5 = Math.abs(var1);
         return !(var3 <= 0.0) && !(var5 <= var3) ? Math.pow(var3 / var5, this.data.getDoubleValue5()) : 1.0;
      }

      private void method3648(double var1, double var3, double var5) {
         double var7 = var1 * this.data.getDoubleValue8();
         int var9 = this.method3645(var7, var3);
         double var10 = var7 + this.data.getDoubleValue9() * var5 * var9;
         double var12 = this.data.getDoubleValue10();
         double var14 = this.method3647(var5);
         if (var14 < 1.0) {
            var12 = Math.min(var12, this.data.getDoubleValue6() * var14 * 2.0);
         }

         var10 = Math.copySign(Math.min(Math.abs(var10), var12), var10);
         double[] var16 = method3651(var10 * 1.0, var9 + 1);
         this.doubleValues = new double[var9];
         System.arraycopy(var16, 0, this.doubleValues, 0, var9);
         this.intValue = 0;
      }

      private double method3649(double var1, double var3, double var5, double var7, boolean var9) {
         if (this.intValue2 > 0) {
            this.intValue2--;
            this.doubleValue = 0.0;
            this.doubleValue2 = 0.0;
            return 0.0;
         } else if (this.intValue < this.doubleValues.length) {
            double var25 = this.doubleValues[this.intValue++];
            double var27 = 0.233125 * this.random.nextGaussian();
            var27 = Math.max(-0.424375, Math.min(0.424375, var27));
            var25 *= 1.0 + var27;
            if (this.intValue >= this.doubleValues.length) {
               this.booleanValue = Boolean.TRUE;
               if (Math.abs(var3) >= 38.0) {
                  this.doubleValue = 0.0;
                  this.doubleValue2 = 0.0;
               }
            }

            return this.method3650(var25, var7, var3);
         } else {
            double var10 = this.method3644(var5);
            boolean var12 = Math.abs(var1) < var10 && Math.abs(var3) < this.data.getDoubleValue21();
            if (!var12 && this.random.nextDouble() < this.method3646(var3)) {
               double var13 = var1 - Math.copySign(Math.min(Math.abs(var1), var10), var1);
               if (Math.abs(var13) >= 1.0) {
                  this.method3648(var1, var5, var3);
                  return this.method3649(var1, var3, var5, var7, var9);
               }
            }

            this.booleanValue = Boolean.FALSE;
            if (var12) {
               this.doubleValue = this.doubleValue * (1.0 - this.data.getDoubleValue14());
            } else if (this.random.nextDouble() < Math.min(1.0, this.data.getDoubleValue13())) {
               double var29 = Math.min(this.method3643(), this.data.getDoubleValue() + this.data.getDoubleValue2() * Math.abs(var3));
               double var15 = var1 - Math.copySign(Math.min(Math.abs(var1), var10), var1);
               double var17 = var29 * var15 * (1.0 + this.data.getDoubleValue17());
               var17 += this.data.getDoubleValue12() * var3;
               double var19 = this.data.getDoubleValue10();
               double var21 = this.method3647(var3);
               if (var21 < 1.0) {
                  var19 = Math.min(var19, this.data.getDoubleValue6() * var21);
               }

               var17 = Math.copySign(Math.min(Math.abs(var17), var19), var17);
               if (this.data.getDoubleValue15() > 0.0) {
                  double var23 = this.data.getDoubleValue15() * this.random.nextGaussian();
                  var23 = Math.max(-this.data.getDoubleValue16(), Math.min(this.data.getDoubleValue16(), var23));
                  var17 *= 1.0 + var23;
               }

               var17 = Math.max(-this.data.getDoubleValue26(), Math.min(this.data.getDoubleValue26(), var17));
               this.doubleValue = this.doubleValue + this.data.getDoubleValue14() * (var17 - this.doubleValue);
            }

            double var30 = var12 ? this.data.getDoubleValue23() : this.data.getDoubleValue24();
            if (var9) {
               var30 = Math.max(var30, this.data.getDoubleValue22());
            }

            double var31 = this.method3647(var3);
            if (this.data.getDoubleValue7() > var30) {
               var30 += (1.0 - var31) * (this.data.getDoubleValue7() - var30);
            }

            if (var30 > 0.0 && this.random.nextDouble() < var30) {
               double var35 = Math.max(1.0, this.data.getDoubleValue25());
               double var36 = -Math.log1p(-this.random.nextDouble()) * var35;
               this.intValue2 = Math.max(1, (int)Math.round(var36));
               this.doubleValue = 0.0;
               this.doubleValue2 = 0.0;
               return 0.0;
            } else {
               return this.method3650(this.doubleValue, var7, var3);
            }
         }
      }

      private double method3650(double var1, double var3, double var5) {
         this.doubleValue2 = this.doubleValue2 + (var1 + this.data.getDoubleValue11() * var3);
         double var7 = this.data.getDoubleValue26();
         double var9 = this.method3647(var5);
         if (var9 < 1.0) {
            var7 = Math.min(var7, this.data.getDoubleValue6() * var9);
         }

         double var11 = Math.max(-var7, Math.min(var7, this.doubleValue2));
         double var13 = Math.floor(var11 + 0.5);
         if (var13 != 0.0 && this.intValue3 >= this.data.getIntValue()) {
            this.doubleValue = 0.0;
            this.doubleValue2 = 0.0;
            this.intValue3 = 0;
            this.intValue4 = 0;
            return 0.0;
         } else {
            if (var13 == 0.0) {
               this.intValue3 = 0;
            } else if (this.intValue4 != 0 && var13 > 0.0 != this.intValue4 > 0) {
               this.intValue3 = 1;
            } else {
               this.intValue3++;
            }

            this.intValue4 = var13 > 0.0 ? 1 : (var13 < 0.0 ? -1 : 0);
            this.doubleValue2 -= var13;
            return var13;
         }
      }

      private static double[] method3651(double var0, int var2) {
         double[][] var3 = new double[var2 + 1][];
         var3[0] = new double[]{1.0, 0.0, 0.0, 1.0};

         for (int var4 = 1; var4 <= var2; var4++) {
            var3[var4] = new double[]{1.0, var4, 0.0, 1.0};
         }

         double[] var13 = new double[]{0.25, 0.5, 0.5, 1.0};
         double[][] var5 = new double[var2 + 1][];

         for (int var6 = 0; var6 <= var2; var6++) {
            double[] var7 = new double[]{0.0, 0.0, 0.0, 0.0};

            for (int var8 = 0; var8 < var6; var8++) {
               double[] var9 = var3[var2 - var6 + var8];
               double[] var10 = new double[]{var9[0], var9[2], var9[1], var9[3]};
               double[] var11 = method3652(method3652(var3[var8], var13), var10);

               for (int var12 = 0; var12 < 4; var12++) {
                  var7[var12] += var11[var12];
               }
            }

            var5[var6] = var7;
         }

         double[] var14 = var5[var2];
         double var15 = var14[0] * var14[3] - var14[1] * var14[2];
         if (Math.abs(var15) < 1.0E-12) {
            return new double[]{var0};
         } else {
            double[] var16 = new double[]{var14[3] / var15, -var14[1] / var15, -var14[2] / var15, var14[0] / var15};
            double[] var17 = new double[var2];

            for (int var18 = 1; var18 <= var2; var18++) {
               double[] var19 = method3652(var5[var18], var16);
               var17[var18 - 1] = var19[2] * var0;
            }

            return var17;
         }
      }

      private static double[] method3652(double[] var0, double[] var1) {
         return new double[]{
            var0[0] * var1[0] + var0[1] * var1[2],
            var0[0] * var1[1] + var0[1] * var1[3],
            var0[2] * var1[0] + var0[3] * var1[2],
            var0[2] * var1[1] + var0[3] * var1[3]
         };
      }
   }
}

package org.zenith.utility.misc;

public final class Util23 {
   private static final float[] CONST_floatValues = new float[]{-0.25F, -0.5F, -0.25F, 0.25F, 0.0F, 0.25F};
   private static final float[] CONST_floatValues2 = new float[]{-0.25F, 0.0F, -0.125F, 0.25F, 0.75F, 0.125F};
   private static final float[] CONST_floatValues3 = new float[]{0.25F, 0.0F, -0.125F, 0.5F, 0.75F, 0.125F};
   private static final float[] CONST_floatValues4 = new float[]{-0.5F, 0.0F, -0.125F, -0.25F, 0.75F, 0.125F};
   private static final float[] CONST_floatValues5 = new float[]{-0.00625F, 0.75F, -0.125F, 0.24375F, 1.5F, 0.125F};
   private static final float[] CONST_floatValues6 = new float[]{-0.24375F, 0.75F, -0.125F, 0.00625F, 1.5F, 0.125F};
   private static final float[] CONST_floatValues7 = new float[]{-0.3F, -0.5F, -0.3F, 0.3F, 1.5F, 0.3F};

   private Util23() {
   }

   public static float[] method5987(CosmeticType var0) {
      return switch (var0) {
         case HAT, HEAD -> CONST_floatValues;
         case BODY, FULL, WINGS, TAIL -> CONST_floatValues2;
         case LEFT_ARM -> CONST_floatValues3;
         case RIGHT_ARM -> CONST_floatValues4;
         case LEFT_LEG -> CONST_floatValues5;
         case RIGHT_LEG -> CONST_floatValues6;
         case MOUNT, INDEPENDENT -> CONST_floatValues7;
         default -> null;
      };
   }

   public static float method5988(CosmeticType var0) {
      return switch (var0) {
         case HAT, FULL -> 0.6F;
         case HEAD, BODY -> 0.5F;
         case WINGS -> 0.9F;
         case TAIL -> 0.7F;
         default -> 0.35F;
         case MOUNT, INDEPENDENT -> 1.5F;
      };
   }

   private static float method5989(CosmeticType var0) {
      return switch (var0) {
         case HAT, HEAD -> 2.5F;
         case BODY, FULL -> 3.0F;
         case WINGS -> 7.0F;
         case TAIL -> 4.0F;
         default -> 2.0F;
         case MOUNT, INDEPENDENT -> 5.0F;
      };
   }

   public static Util31.Data method5990(Util31.Data var0, CosmeticType var1, float[] var2) {
      float[] var3 = method5987(var1);
      if (var3 != null && var0 != null && !var0.method7853() && var2 != null && var2.length >= 6) {
         float[] var4 = Util31.method7850(var1.getType());
         float[] var5 = method5991(var0, var2, var4);
         if (var5 == null) {
            return var0;
         } else {
            float var6 = var4[0] + var0.getFloatValue();
            float var7 = var4[1] + var0.getFloatValue2();
            float var8 = var0.getFloatValue3();
            float var9 = Math.max(var5[3] - var5[0], Math.max(var5[4] - var5[1], var5[5] - var5[2]));
            float var10 = method5989(var1);
            float var11 = var9 > var10 && var9 > 1.0E-4F ? var10 / var9 : 1.0F;
            if (var11 < 1.0F) {
               for (int var12 = 0; var12 < 3; var12++) {
                  float var13 = var12 == 0 ? var6 : (var12 == 1 ? var7 : var8);
                  var5[var12] = var13 + (var5[var12] - var13) * var11;
                  var5[var12 + 3] = var13 + (var5[var12 + 3] - var13) * var11;
               }
            }

            float var17 = method5988(var1);
            float[] var18 = new float[3];

            for (int var14 = 0; var14 < 3; var14++) {
               float var15 = var3[var14] - var17;
               float var16 = var3[var14 + 3] + var17;
               if (var5[var14] > var16) {
                  var18[var14] = var16 - var5[var14];
               } else if (var5[var14 + 3] < var15) {
                  var18[var14] = var15 - var5[var14 + 3];
               }
            }

            return var11 == 1.0F && var18[0] == 0.0F && var18[1] == 0.0F && var18[2] == 0.0F
               ? var0
               : new Util31.Data(
                  var0.getFloatValue() + var18[0],
                  var0.getFloatValue2() + var18[1],
                  var0.getFloatValue3() + var18[2],
                  var0.getFloatValue4(),
                  var0.getFloatValue5(),
                  var0.getFloatValue6(),
                  var0.getFloatValue7() * var11,
                  var0.getFloatValue8() * var11,
                  var0.getFloatValue9() * var11,
                  var0.isFlag()
               );
         }
      } else {
         return var0;
      }
   }

   private static float[] method5991(Util31.Data var0, float[] var1, float[] var2) {
      double var3 = Math.toRadians(var0.getFloatValue4());
      double var5 = Math.toRadians(var0.getFloatValue5());
      double var7 = Math.toRadians(var0.getFloatValue6());
      float var9 = (float)Math.sin(var3);
      float var10 = (float)Math.cos(var3);
      float var11 = (float)Math.sin(var5);
      float var12 = (float)Math.cos(var5);
      float var13 = (float)Math.sin(var7);
      float var14 = (float)Math.cos(var7);
      float var15 = var2[0] + (var0.isFlag() ? var0.getFloatValue() : 0.0F);
      float var16 = var2[1] + (var0.isFlag() ? var0.getFloatValue2() : 0.0F);
      float var17 = var0.isFlag() ? var0.getFloatValue3() : 0.0F;
      float[] var18 = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

      for (int var19 = 0; var19 < 8; var19++) {
         float var20 = var1[(var19 & 1) == 0 ? 0 : 3] * var0.getFloatValue7();
         float var21 = var1[(var19 & 2) == 0 ? 1 : 4] * var0.getFloatValue8();
         float var22 = var1[(var19 & 4) == 0 ? 2 : 5] * var0.getFloatValue9();
         float var23 = var21 * var10 - var22 * var9;
         float var24 = var21 * var9 + var22 * var10;
         float var25 = var20 * var12 + var24 * var11;
         float var26 = -var20 * var11 + var24 * var12;
         float var27 = var25 * var14 - var23 * var13;
         float var28 = var25 * var13 + var23 * var14;
         var18[0] = Math.min(var18[0], var15 + var27);
         var18[1] = Math.min(var18[1], var16 + var28);
         var18[2] = Math.min(var18[2], var17 + var26);
         var18[3] = Math.max(var18[3], var15 + var27);
         var18[4] = Math.max(var18[4], var16 + var28);
         var18[5] = Math.max(var18[5], var17 + var26);
      }

      return Float.isFinite(var18[0]) && Float.isFinite(var18[3]) ? var18 : null;
   }
}

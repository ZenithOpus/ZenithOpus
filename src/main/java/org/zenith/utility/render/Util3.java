package org.zenith.utility.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerSkinType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;

public final class Util3 {
   private static final float CONST_floatValue = 0.0625F;
   private static final int CONST_intValue = 6;
   private static final int CONST_intValue2 = Class15.Type.HEAD.ordinal();
   private static final int CONST_intValue3 = Class15.Type.BODY.ordinal();
   private static final int CONST_intValue4 = Class15.Type.RIGHT_ARM.ordinal();
   private static final int CONST_intValue5 = Class15.Type.LEFT_ARM.ordinal();
   private static final int CONST_intValue6 = Class15.Type.RIGHT_LEG.ordinal();
   private static final int CONST_intValue7 = Class15.Type.LEFT_LEG.ordinal();
   private static final float[] CONST_floatValues = new float[]{0.0F, 0.0F, -0.0625F, 0.0625F, 0.0F, 0.0F};
   private static final float[] CONST_floatValues2 = new float[]{0.25F, 0.25F, 0.125F, 0.125F, 0.125F, 0.125F};
   private static final float[] CONST_floatValues3 = new float[]{0.25F, 0.125F, 0.125F, 0.125F, 0.125F, 0.125F};
   private static final float[] CONST_floatValues4 = new float[]{-0.5F, 0.0F, -0.125F, -0.125F, 0.0F, 0.0F};
   private static final float[] CONST_floatValues5 = new float[]{0.0F, 0.75F, 0.625F, 0.625F, 0.75F, 0.75F};
   private static final float CONST_floatValue2 = 0.09375F;
   private static final float[] CONST_floatValues6 = new float[]{0.0F, 0.0F, -0.03125F, 0.03125F, 0.0F, 0.0F};
   private static final float CONST_floatValue3 = 0.03125F;
   private static final float CONST_floatValue4 = 0.015625F;
   private static final float CONST_floatValue5 = 0.0625F;
   private static final float CONST_floatValue6 = 0.0625F;
   private static final float CONST_floatValue7 = 0.03125F;
   private static final float CONST_floatValue8 = 0.025F;
   private static final float CONST_floatValue9 = 0.05625F;
   private static final float CONST_floatValue10 = 0.0125F;
   private static final float CONST_floatValue11 = 0.00625F;
   private static final float CONST_floatValue12 = 1.9F;
   private static final float CONST_floatValue13 = 1.25F;
   private static final float CONST_floatValue14 = 1.15F;
   private static final float CONST_floatValue15 = 1.35F;
   private static final float CONST_floatValue16 = 1.6F;
   private static final float CONST_floatValue17 = 0.5F;
   private static final float CONST_floatValue18 = 2.5F;
   private static final float CONST_floatValue19 = 0.1875F;
   private static final float CONST_floatValue20 = 0.85F;
   private static final int CONST_intValue8 = 5;
   private static final float[] CONST_floatValues7 = new float[0];
   private static final int CONST_intValue9 = 512;
   private static final Map<Util3.Entry2, Util3.Data[]> items = new LinkedHashMap<Util3.Entry2, Util3.Data[]>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(java.util.Map.Entry<Util3.Entry2, Util3.Data[]> var1) {
         return this.size() > 512;
      }
   };
   private static final Object obj = new Object();
   private static final Object obj2 = new Object();

   private Util3() {
   }

   public static int method824(FocusSlotEnum var0) {
      return switch (var0) {
         case CHARACTER -> 0;
         case LEGS -> 1;
         case BODY -> 2;
         case HEAD -> 3;
         case BACK, CAPE -> 4;
         case WATCH, NECK -> 5;
         default -> -1;
      };
   }

   private static boolean method825(int var0) {
      return var0 == CONST_intValue4 || var0 == CONST_intValue5;
   }

   public static Util3.Entry method826(PlayerEntityRenderState var0, Wardrobe var1, List<HoveredData> var2, HoveredData var3) {
      boolean var4 = var0.skinTextures != null && var0.skinTextures.model() == PlayerSkinType.SLIM;
      Util3.Entry var5 = Util3.Entry.method843(var1, var2, var3, var4);
      if (var1.clothesOverArmor.isEnabled() && !WardrobeRenderer.method6688(var0)) {
         method828(var0, var5.floatValues);
      }

      if (var3 == null) {
         var5.floatValues2[CONST_intValue2] = var0.hatVisible ? 0.03125F : 0.0F;
         var5.floatValues2[CONST_intValue3] = var0.jacketVisible ? 0.015625F : 0.0F;
         var5.floatValues2[CONST_intValue4] = var0.rightSleeveVisible ? 0.015625F : 0.0F;
         var5.floatValues2[CONST_intValue5] = var0.leftSleeveVisible ? 0.015625F : 0.0F;
         var5.floatValues2[CONST_intValue6] = var0.rightPantsLegVisible ? 0.015625F : 0.0F;
         var5.floatValues2[CONST_intValue7] = var0.leftPantsLegVisible ? 0.015625F : 0.0F;
      }

      var5.method844();
      return var5;
   }

   public static Util3.Entry method827(Wardrobe var0, List<HoveredData> var1, HoveredData var2, boolean var3, int var4, boolean var5) {
      Util3.Entry var6 = Util3.Entry.method843(var0, var1, var2, var3);
      if (var2 == null && var5 && method825(var4)) {
         boolean var7 = false;

         for (Class15 var11 : var6.class15s) {
            var7 |= (var11.intValue4 & 4) != 0;
         }

         var6.floatValues2[var4] = var7 ? 0.0F : 0.015625F;
      }

      var6.method844();
      return var6;
   }

   private static void method828(PlayerEntityRenderState var0, float[] var1) {
      boolean var2 = ArmorFeatureRenderer.hasModel(var0.equippedHeadStack, EquipmentSlot.HEAD);
      boolean var3 = ArmorFeatureRenderer.hasModel(var0.equippedChestStack, EquipmentSlot.CHEST)
         && !var0.equippedChestStack.contains(DataComponentTypes.GLIDER);
      boolean var4 = ArmorFeatureRenderer.hasModel(var0.equippedLegsStack, EquipmentSlot.LEGS);
      boolean var5 = ArmorFeatureRenderer.hasModel(var0.equippedFeetStack, EquipmentSlot.FEET);
      var1[CONST_intValue2] = var2 ? 0.0625F : 0.0F;
      var1[CONST_intValue3] = var3 ? 0.0625F : (var4 ? 0.03125F : 0.0F);
      var1[CONST_intValue4] = var3 ? 0.0625F : 0.0F;
      var1[CONST_intValue5] = var1[CONST_intValue4];
      var1[CONST_intValue6] = var5 ? 0.05625F : (var4 ? 0.025F : 0.0F);
      var1[CONST_intValue7] = var1[CONST_intValue6];
   }

   private static float[] method829(int var0, int var1) {
      float var2 = var0 == CONST_intValue2 ? 0.0625F : 0.0F;
      float var3 = var1 == 1 ? CONST_floatValues[var0] - CONST_floatValues2[var0] : CONST_floatValues4[var0] - var2;
      float var4 = var1 == 1 ? CONST_floatValues[var0] + CONST_floatValues2[var0] : CONST_floatValues5[var0];
      float var5 = var1 == 2 ? CONST_floatValues[var0] - CONST_floatValues2[var0] : -CONST_floatValues3[var0];
      float var6 = var1 == 2 ? CONST_floatValues[var0] + CONST_floatValues2[var0] : CONST_floatValues3[var0];
      return new float[]{var3, var4, var5, var6};
   }

   private static float method830(int var0, int var1) {
      float var2 = var1 == 0 ? CONST_floatValues2[var0] : (var1 == 2 ? CONST_floatValues3[var0] : -CONST_floatValues4[var0]);
      return var2 * 0.85F;
   }

   public static float method831(Class15 var0, int var1, int var2, float var3) {
      Util3.Data3 var4 = new Util3.Data3(var1, var2, var3);
      float[] var5 = var0.items4.get(var4);
      if (var5 != null) {
         return var5 == CONST_floatValues7 ? Float.NaN : var5[0];
      } else {
         float[] var6 = method829(var1, var2);
         int var7 = var2 == 1 ? 0 : 1;
         int var8 = var2 == 2 ? 0 : 2;
         float var9 = var2 == 0 ? CONST_floatValues[var1] : 0.0F;
         float var10 = method830(var1, var2);
         float[] var11 = new float[Math.max(0, var0.method2029(var1) - var0.method2028(var1)) * 5];
         int var12 = method837(var0, var1, var2, var3, var9, var7, var8, false, var11);
         float[] var13 = new float[]{Float.MAX_VALUE};
         method833(var11, var12, var11, 0, var6, (var2x, var3x) -> {
            if (var2x >= var10 && var2x < var13[0]) {
               var13[0] = var2x;
            }
         });
         float[] var14 = var13[0] < Float.MAX_VALUE ? new float[]{var13[0]} : CONST_floatValues7;
         var0.items4.put(var4, var14);
         return var14 == CONST_floatValues7 ? Float.NaN : var13[0];
      }
   }

   private static float[] method832(Class15 var0, Class15 var1, int var2, int var3, float var4, float var5) {
      Util3.Data2 var6 = new Util3.Data2(var0, var2, var3, var4, var5);
      float[] var7 = var1.items4.get(var6);
      if (var7 != null) {
         return var7 == CONST_floatValues7 ? null : var7;
      } else {
         float[] var8 = method829(var2, var3);
         int var9 = var3 == 1 ? 0 : 1;
         int var10 = var3 == 2 ? 0 : 2;
         float var11 = var3 == 0 ? CONST_floatValues[var2] : 0.0F;
         float var12 = method830(var2, var3);
         float[] var13 = new float[Math.max(0, var1.method2029(var2) - var1.method2028(var2)) * 5];
         float[] var14 = new float[Math.max(0, var0.method2029(var2) - var0.method2028(var2)) * 5];
         int var15 = method837(var1, var2, var3, var4, var11, var9, var10, false, var13);
         int var16 = method837(var0, var2, var3, var4, var11, var9, var10, true, var14);
         Util3.Entry3 var17 = new Util3.Entry3();
         if (var16 > 0) {
            method833(var13, var15, var14, var16, var8, (var3x, var4x) -> {
               if (var3x >= var12 && var4x >= var12 && var4x <= var3x * var5) {
                  var17.method851(var3x, var4x);
               }
            });
         }

         float[] var18 = var17.method852();
         var1.items4.put(var6, var18);
         return var18 == CONST_floatValues7 ? null : var18;
      }
   }

   private static void method833(float[] var0, int var1, float[] var2, int var3, float[] var4, Util3.Handler var5) {
      int[] var6 = new int[var1];
      int[] var7 = new int[var3];
      float[] var8 = new float[(var1 + var3) * 2 + 2];
      float[] var9 = new float[var8.length];

      for (int var10 = 0; var10 < var1; var10++) {
         int var11 = var10 * 5;
         float var12 = Math.max(var4[0], var0[var11 + 1]);
         float var13 = Math.min(var4[1], var0[var11 + 2]);
         float var14 = Math.max(var4[2], var0[var11 + 3]);
         float var15 = Math.min(var4[3], var0[var11 + 4]);
         if (!(var12 >= var13) && !(var14 >= var15)) {
            int var16 = method834(var0, var1, var12, var13, var14, var15, var6);
            int var17 = method834(var2, var3, var12, var13, var14, var15, var7);
            int var18 = method835(var8, var12, var13, 1, var0, var6, var16, var2, var7, var17);
            int var19 = method835(var9, var14, var15, 3, var0, var6, var16, var2, var7, var17);

            for (int var20 = 0; var20 + 1 < var18; var20++) {
               float var21 = (var8[var20] + var8[var20 + 1]) * 0.5F;

               for (int var22 = 0; var22 + 1 < var19; var22++) {
                  float var23 = (var9[var22] + var9[var22 + 1]) * 0.5F;
                  var5.accept(method838(var0, var6, var16, var21, var23), method838(var2, var7, var17, var21, var23));
               }
            }
         }
      }
   }

   private static int method834(float[] var0, int var1, float var2, float var3, float var4, float var5, int[] var6) {
      int var7 = 0;

      for (int var8 = 0; var8 < var1; var8++) {
         int var9 = var8 * 5;
         if (var0[var9 + 1] < var3 && var0[var9 + 2] > var2 && var0[var9 + 3] < var5 && var0[var9 + 4] > var4) {
            var6[var7++] = var8;
         }
      }

      return var7;
   }

   private static int method835(float[] var0, float var1, float var2, int var3, float[] var4, int[] var5, int var6, float[] var7, int[] var8, int var9) {
      int var10 = 0;
      var0[var10++] = var1;
      var0[var10++] = var2;

      for (int var11 = 0; var11 < var6; var11++) {
         var10 = method836(var0, var10, var1, var2, var4[var5[var11] * 5 + var3]);
         var10 = method836(var0, var10, var1, var2, var4[var5[var11] * 5 + var3 + 1]);
      }

      for (int var17 = 0; var17 < var9; var17++) {
         var10 = method836(var0, var10, var1, var2, var7[var8[var17] * 5 + var3]);
         var10 = method836(var0, var10, var1, var2, var7[var8[var17] * 5 + var3 + 1]);
      }

      Arrays.sort(var0, 0, var10);
      int var18 = 1;

      for (int var12 = 1; var12 < var10; var12++) {
         if (var0[var12] - var0[var18 - 1] > 1.0E-6F) {
            var0[var18++] = var0[var12];
         }
      }

      return var18;
   }

   private static int method836(float[] var0, int var1, float var2, float var3, float var4) {
      if (var4 > var2 && var4 < var3) {
         var0[var1++] = var4;
      }

      return var1;
   }

   private static int method837(Class15 var0, int var1, int var2, float var3, float var4, int var5, int var6, boolean var7, float[] var8) {
      float[] var9 = method841(var0, obj);
      float[] var10 = method841(var0, obj2);
      boolean var11 = var7 && method840(var0, var1);
      int var12 = 0;
      int var13 = var0.method2028(var1);

      for (int var14 = var0.method2029(var1); var13 < var14; var13++) {
         int var15 = var0.intValues4[var13];
         if (var0.intValues3[var15] == var1
            && (var0.intValues5[var13] & 4) == 0
            && (!var11 || var0.intValues[var15] < 0)
            && !(var10[var13 * 3 + var2] * var3 < 0.9F)) {
            float var16 = 0.0F;
            float var17 = Float.MAX_VALUE;
            float var18 = -Float.MAX_VALUE;
            float var19 = Float.MAX_VALUE;
            float var20 = -Float.MAX_VALUE;

            for (int var21 = 0; var21 < 4; var21++) {
               int var22 = var13 * 12 + var21 * 3;
               var16 += var9[var22 + var2];
               float var23 = var9[var22 + var5];
               float var24 = var9[var22 + var6];
               var17 = Math.min(var17, var23);
               var18 = Math.max(var18, var23);
               var19 = Math.min(var19, var24);
               var20 = Math.max(var20, var24);
            }

            int var25 = var12++ * 5;
            var8[var25] = var3 * (var16 / 4.0F - var4);
            var8[var25 + 1] = var17;
            var8[var25 + 2] = var18;
            var8[var25 + 3] = var19;
            var8[var25 + 4] = var20;
         }
      }

      return var12;
   }

   private static float method838(float[] var0, int[] var1, int var2, float var3, float var4) {
      float var5 = -Float.MAX_VALUE;

      for (int var6 = 0; var6 < var2; var6++) {
         int var7 = var1[var6] * 5;
         if (var0[var7] > var5 && var3 >= var0[var7 + 1] && var3 <= var0[var7 + 2] && var4 >= var0[var7 + 3] && var4 <= var0[var7 + 4]) {
            var5 = var0[var7];
         }
      }

      return var5;
   }

   private static float method839(float[] var0, int var1, float var2, float var3) {
      float var4 = -Float.MAX_VALUE;

      for (int var5 = 0; var5 < var1; var5++) {
         int var6 = var5 * 5;
         if (var0[var6] > var4 && var2 >= var0[var6 + 1] && var2 <= var0[var6 + 2] && var3 >= var0[var6 + 3] && var3 <= var0[var6 + 4]) {
            var4 = var0[var6];
         }
      }

      return var4;
   }

   private static boolean method840(Class15 var0, int var1) {
      for (int var2 = 0; var2 < var0.method2030(); var2++) {
         if (var0.intValues[var2] < 0 && var0.intValues2[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   private static float[] method841(Class15 var0, Object var1) {
      float[] var2 = var0.items4.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         int var3 = var0.method2030();
         Matrix4f[] var4 = new Matrix4f[var3];

         for (int var5 = 0; var5 < var3; var5++) {
            var4[var5] = new Matrix4f();
            BoneMatrices.method4808(var0, null, var5, var4[var5]);
         }

         float[] var12 = new float[var0.intValue3 * 12];
         float[] var6 = new float[var0.intValue3 * 3];
         Vector3f var7 = new Vector3f();

         for (int var8 = 0; var8 < var0.intValue3; var8++) {
            Matrix4f var9 = var4[var0.intValues4[var8]];

            for (int var10 = 0; var10 < 4; var10++) {
               int var11 = var8 * 12 + var10 * 3;
               var7.set(var0.floatValues4[var11], var0.floatValues4[var11 + 1], var0.floatValues4[var11 + 2]);
               var9.transformPosition(var7);
               var12[var11] = var7.x;
               var12[var11 + 1] = var7.y;
               var12[var11 + 2] = var7.z;
            }

            var7.set(var0.floatValues6[var8 * 3], var0.floatValues6[var8 * 3 + 1], var0.floatValues6[var8 * 3 + 2]);
            var9.transformDirection(var7);
            if (var7.lengthSquared() > 1.0E-8F) {
               var7.normalize();
            }

            var6[var8 * 3] = var7.x;
            var6[var8 * 3 + 1] = var7.y;
            var6[var8 * 3 + 2] = var7.z;
         }

         var0.items4.put(obj, var12);
         var0.items4.put(obj2, var6);
         return var1 == obj ? var12 : var6;
      }
   }

   public static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + "]";
      }
      public static final Util3.Data INSTANCE = new Util3.Data(0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);

      public Data(float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
      }

      public void method842(MatrixStack var1) {
         if (this != INSTANCE) {
            var1.translate(this.floatValue2 + this.floatValue4, 0.0F, this.floatValue7);
            var1.scale(this.floatValue3, this.floatValue5, this.floatValue6);
            var1.translate(-this.floatValue, 0.0F, 0.0F);
         }
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
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
   }

   private static final class Data2 {
      private final Class15 class15;
      private final int intValue;
      private final int intValue2;
      private final float floatValue;
      private final float floatValue2;

      public Class15 class15() {
         return this.class15;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(class15, intValue, intValue2, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "class15=" + this.class15 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data2(Class15 var1, int var2, int var3, float var4, float var5) {
         this.class15 = var1;
         this.intValue = var2;
         this.intValue2 = var3;
         this.floatValue = var4;
         this.floatValue2 = var5;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data3 {
      private final int intValue;
      private final int intValue2;
      private final float floatValue;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, floatValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data3(int var1, int var2, float var3) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.floatValue = var3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }

   public static final class Entry {
      private final HoveredData[] hoveredDatas;
      private final Class15[] class15s;
      private final int[] intValues;
      private final boolean flag;
      private final float[] floatValues = new float[6];
      private final float[] floatValues2 = new float[6];
      private Util3.Data[] datas;

      public Entry(List<HoveredData> var1, List<Class15> var2, boolean var3) {
         int var4 = var1.size();
         this.hoveredDatas = var1.toArray(new HoveredData[0]);
         this.class15s = var2.toArray(new Class15[0]);
         this.intValues = new int[var4];
         boolean var5 = false;

         for (int var6 = 0; var6 < var4; var6++) {
            this.intValues[var6] = Util3.method824(this.hoveredDatas[var6].getFocusSlotEnum());
            var5 |= this.hoveredDatas[var6].getFocusSlotEnum() == FocusSlotEnum.CHARACTER;
         }

         this.flag = var3 && !var5;
         this.datas = new Util3.Data[var4 * 6];
      }

      private static Util3.Entry method843(Wardrobe var0, List<HoveredData> var1, HoveredData var2, boolean var3) {
         List<HoveredData> var4 = new ArrayList<>(var1.size());
         List<Class15> var5 = new ArrayList<>(var1.size());

         for (HoveredData var7 : var1) {
            if (Util3.method824(var7.getFocusSlotEnum()) >= 0 && (var2 == null || var7 == var2 || var7.method1546() == WardrobeCategory.ACCESSORY)) {
               Class15 var8 = var0.getRegistry().method1427(var7);
               if (var8 != null) {
                  var4.add(var7);
                  var5.add(var8);
               }
            }
         }

         return new Util3.Entry(var4, var5, var3);
      }

      private void method844() {
         Util3.Entry2 var1 = new Util3.Entry2(this);
         Util3.Data[] var2 = Util3.items.get(var1);
         if (var2 == null) {
            Util3.items.put(var1, this.datas);
         } else {
            this.datas = var2;
         }
      }

      public Util3.Data method845(HoveredData var1, int var2) {
         for (int var3 = 0; var3 < this.hoveredDatas.length; var3++) {
            if (this.hoveredDatas[var3] == var1) {
               return this.method847(var3, var2);
            }
         }

         return Util3.Data.INSTANCE;
      }

      public void method846(MatrixStack var1, HoveredData var2, int var3) {
         this.method845(var2, var3).method842(var1);
      }

      private Util3.Data method847(int var1, int var2) {
         if (var2 >= 0 && var2 < 6) {
            int var3 = var1 * 6 + var2;
            Util3.Data var4 = this.datas[var3];
            if (var4 == null) {
               var4 = this.hoveredDatas[var1].getFocusSlotEnum() == FocusSlotEnum.CHARACTER ? Util3.Data.INSTANCE : this.method848(var1, var2);
               this.datas[var3] = var4;
            }

            return var4;
         } else {
            return Util3.Data.INSTANCE;
         }
      }

      private Util3.Data method848(int var1, int var2) {
         boolean var3 = this.flag && Util3.method825(var2) && this.floatValues[var2] <= 0.0F;
         float var4 = var3 ? 0.09375F : Util3.CONST_floatValues2[var2];
         float var5 = var3 ? Util3.CONST_floatValues6[var2] : Util3.CONST_floatValues[var2];
         float[] var6 = this.method849(var1, var2, 0, var4, var4 / Util3.CONST_floatValues2[var2]);
         float[] var7 = this.method849(var1, var2, 2, Util3.CONST_floatValues3[var2], 1.0F);
         float var8 = var2 == Util3.CONST_intValue2 ? this.method849(var1, var2, 1, -Util3.CONST_floatValues4[Util3.CONST_intValue2], 1.0F)[0] : 1.0F;
         return var6[0] == 1.0F && var6[1] == 0.0F && var7[0] == 1.0F && var7[1] == 0.0F && var8 == 1.0F && var5 == Util3.CONST_floatValues[var2]
            ? Util3.Data.INSTANCE
            : new Util3.Data(Util3.CONST_floatValues[var2], var5, var6[0], var6[1], var8, var7[0], var7[1]);
      }

      private float[] method849(int var1, int var2, int var3, float var4, float var5) {
         Class15 var6 = this.class15s[var1];
         boolean var7 = this.hoveredDatas[var1].method1546() == WardrobeCategory.ACCESSORY;
         float[][] var8 = new float[2][];
         int[] var9 = new int[2];

         for (int var10 = 0; var10 < 2; var10++) {
            float var11 = var3 == 1 ? -1.0F : (var10 == 0 ? -1.0F : 1.0F);
            if (var3 == 1 && var10 == 1) {
               break;
            }

            float var12 = Util3.method831(var6, var2, var3, var11);
            if (!Float.isNaN(var12)) {
               float var13 = var4 + this.floatValues2[var2];
               float var14;
               if (var7) {
                  var14 = Util3.method825(var2) ? var13 + 0.00625F : Math.max(var12 * var5, var13 + 0.00625F);
               } else {
                  var14 = var12 * var5;
                  if (this.floatValues2[var2] > 0.0F) {
                     var14 = Math.max(var14, Math.min(var13 + 0.0125F, var12 * var5 * 1.25F));
                  }
               }

               if (this.floatValues[var2] > 0.0F) {
                  var14 = Math.max(var14, Math.min(var4 + this.floatValues[var2] + 0.0125F, var12 * var5 * 1.9F));
               }

               float[] var15 = new float[8];
               int var16 = 0;
               var15[var16++] = var12;
               var15[var16++] = var14;
               float var17 = !var7 && this.intValues[var1] < Util3.method824(FocusSlotEnum.CAPE) ? 1.15F : 1.9F;

               for (int var18 = 0; var18 < this.hoveredDatas.length; var18++) {
                  if (var18 != var1 && this.intValues[var18] < this.intValues[var1] && this.class15s[var18].method2033(var2)) {
                     float[] var19 = Util3.method832(this.class15s[var18], var6, var2, var3, var11, var7 ? 1.6F : 1.35F);
                     if (var19 != null) {
                        Util3.Data var20 = this.method847(var18, var2);
                        float var21 = var3 == 0 ? var20.getFloatValue3() : (var3 == 1 ? var20.getFloatValue5() : var20.getFloatValue6());
                        float var22 = var3 == 0 ? var20.getFloatValue4() : (var3 == 1 ? 0.0F : var20.getFloatValue7());
                        if (var16 + var19.length > var15.length) {
                           var15 = Arrays.copyOf(var15, Math.max(var15.length * 2, var16 + var19.length));
                        }

                        for (byte var23 = 0; var23 < var19.length; var23 += 2) {
                           float var24 = var19[var23];
                           float var25 = var21 * var19[var23 + 1] + var11 * var22 + 0.0125F;
                           if (var25 <= var24 * var5 * var17) {
                              var15[var16++] = var24;
                              var15[var16++] = var25;
                           }
                        }
                     }
                  }
               }

               var8[var10] = method850(var15, var16);
               var9[var10] = var8[var10].length;
            }
         }

         float var26 = 0.0F;
         float var28 = 0.0F;
         if (var8[0] != null && var8[1] != null) {
            float[] var31 = var8[0];
            float[] var33 = var8[1];

            for (byte var34 = 0; var34 < var9[0]; var34 += 2) {
               for (byte var36 = 0; var36 < var9[1]; var36 += 2) {
                  var26 = Math.max(var26, (var31[var34 + 1] + var33[var36 + 1]) / (var31[var34] + var33[var36]));
               }
            }

            float var35 = -Float.MAX_VALUE;
            float var37 = Float.MAX_VALUE;

            for (byte var41 = 0; var41 < var9[1]; var41 += 2) {
               var35 = Math.max(var35, var33[var41 + 1] - var26 * var33[var41]);
            }

            for (byte var42 = 0; var42 < var9[0]; var42 += 2) {
               var37 = Math.min(var37, var26 * var31[var42] - var31[var42 + 1]);
            }

            var28 = Math.max(var35, Math.min(var37, 0.0F));
         } else if (var8[0] == null && var8[1] == null) {
            var26 = var5;
         } else {
            int var30 = var8[0] != null ? 0 : 1;

            for (byte var32 = 0; var32 < var9[var30]; var32 += 2) {
               var26 = Math.max(var26, var8[var30][var32 + 1] / var8[var30][var32]);
            }
         }

         var26 = Math.max(0.5F, Math.min(2.5F, var26));
         var28 = Math.max(-0.1875F, Math.min(0.1875F, var28));
         if (Math.abs(var26 - 1.0F) < 0.005F) {
            var26 = 1.0F;
         }

         if (Math.abs(var28) < 3.125E-4F) {
            var28 = 0.0F;
         }

         return new float[]{var26, var28};
      }

      private static float[] method850(float[] var0, int var1) {
         int var2 = var1 / 2;
         Integer[] var3 = new Integer[var2];

         for (int var4 = 0; var4 < var2; var4++) {
            var3[var4] = var4;
         }

         Arrays.sort(var3, (var1x, var2x) -> {
            int var3x = Float.compare(var0[var1x * 2], var0[var2x * 2]);
            return var3x != 0 ? var3x : Float.compare(var0[var2x * 2 + 1], var0[var1x * 2 + 1]);
         });
         float[] var11 = new float[var1];
         int var5 = 0;
         float var6 = -Float.MAX_VALUE;
         Integer[] var7 = var3;
         int var8 = var3.length;

         for (int var9 = 0; var9 < var8; var9++) {
            int var10 = var7[var9];
            if (var0[var10 * 2 + 1] > var6) {
               var6 = var0[var10 * 2 + 1];
               var11[var5++] = var0[var10 * 2];
               var11[var5++] = var6;
            }
         }

         return Arrays.copyOf(var11, var5);
      }
   }

   private static final class Entry2 {
      private final Class15[] class15s;
      private final FocusSlotEnum[] focusSlotEnums;
      private final boolean flag;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final int intValue;

      public Entry2(Util3.Entry var1) {
         this.class15s = var1.class15s;
         this.focusSlotEnums = new FocusSlotEnum[var1.hoveredDatas.length];

         for (int var2 = 0; var2 < this.focusSlotEnums.length; var2++) {
            this.focusSlotEnums[var2] = var1.hoveredDatas[var2].getFocusSlotEnum();
         }

         this.flag = var1.flag;
         this.floatValues = (float[])var1.floatValues.clone();
         this.floatValues2 = (float[])var1.floatValues2.clone();
         int var3 = Arrays.hashCode((Object[])this.class15s);
         var3 = var3 * 31 + Arrays.hashCode((Object[])this.focusSlotEnums);
         var3 = var3 * 31 + Boolean.hashCode(this.flag);
         var3 = var3 * 31 + Arrays.hashCode(this.floatValues);
         this.intValue = var3 * 31 + Arrays.hashCode(this.floatValues2);
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof Util3.Entry2 var2
            && this.intValue == var2.intValue
            && this.flag == var2.flag
            && Arrays.equals((Object[])this.class15s, (Object[])var2.class15s)
            && Arrays.equals((Object[])this.focusSlotEnums, (Object[])var2.focusSlotEnums)
            && Arrays.equals(this.floatValues, var2.floatValues)
            && Arrays.equals(this.floatValues2, var2.floatValues2);
      }

      @Override
      public int hashCode() {
         return this.intValue;
      }
   }

   private static final class Entry3 {
      private float[] floatValues = new float[64];
      private int size;

      public void method851(float var1, float var2) {
         if (this.size == this.floatValues.length) {
            this.floatValues = Arrays.copyOf(this.floatValues, this.size * 2);
         }

         this.floatValues[this.size++] = var1;
         this.floatValues[this.size++] = var2;
      }

      public float[] method852() {
         if (this.size == 0) {
            return Util3.CONST_floatValues7;
         } else {
            HashSet var1 = new HashSet();
            float[] var2 = new float[this.size];
            int var3 = 0;

            for (int var4 = 0; var4 < this.size; var4 += 2) {
               long var5 = Math.round(this.floatValues[var4] * 16.0F * 256.0F);
               long var7 = Math.round(this.floatValues[var4 + 1] * 16.0F * 256.0F);
               if (var1.add(var5 << 32 | var7 & 4294967295L)) {
                  var2[var3++] = this.floatValues[var4];
                  var2[var3++] = this.floatValues[var4 + 1];
               }
            }

            return Arrays.copyOf(var2, var3);
         }
      }
   }

   private interface Handler {
      void accept(float var1, float var2);
   }
}

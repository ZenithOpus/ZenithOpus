package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;

public final class Class15 {
   public static final int CONST_intValue = 1;
   public static final int CONST_intValue2 = 2;
   public static final int CONST_intValue3 = 4;
   public static final int CONST_intValue4 = 8;
   public static final int CONST_intValue5 = 16;
   public static final int CONST_intValue6 = 32;
   public static final int CONST_intValue7 = 6;
   public static final int CONST_intValue8 = 1;
   public static final int CONST_intValue9 = 2;
   public static final int CONST_intValue10 = 4;
   public static final int CONST_intValue11 = 8;
   public static final int CONST_intValue12 = 16;
   public final String text;
   public final Identifier identifier;
   public final int intValue;
   public final int intValue2;
   public final String[] texts;
   public final int[] intValues;
   public final float[] floatValues;
   public final float[] floatValues2;
   public final int[] intValues2;
   public final int[] intValues3;
   public final Class15.Type2[] type2s;
   public final float[] floatValues3;
   public final int intValue3;
   public final float[] floatValues4;
   public final float[] floatValues5;
   public final float[] floatValues6;
   public final int[] intValues4;
   public final int[] intValues5;
   public final float[] floatValues7;
   public final Map<String, Class37> items;
   public final List<Class15.Data4> items2;
   public final Class15.Data3 data3;
   public final Class15.Data2 data2;
   public final Class15.Data data;
   public final float floatValue;
   public final int intValue4;
   public final List<Class15.Data4> items3;
   public final Map<Object, float[]> items4 = new HashMap<>();
   private final boolean[] flags = new boolean[7];
   private final int[] intValues6 = new int[7];
   private final int[] intValues7 = new int[7];
   private final int intValue5;

   public Class15(
      String var1,
      Identifier var2,
      int var3,
      int var4,
      String[] var5,
      int[] var6,
      float[] var7,
      float[] var8,
      int[] var9,
      int[] var10,
      Class15.Type2[] var11,
      float[] var12,
      float[] var13,
      float[] var14,
      float[] var15,
      int[] var16,
      int[] var17,
      Map<String, Class37> var18,
      List<Class15.Data4> var19,
      Class15.Data3 var20,
      Class15.Data2 var21,
      Class15.Data var22,
      float var23,
      float[] var24,
      int var25
   ) {
      this.text = var1;
      this.identifier = var2;
      this.intValue = var3;
      this.intValue2 = var4;
      this.texts = var5;
      this.intValues = var6;
      this.floatValues = var7;
      this.floatValues2 = var8;
      this.intValues2 = var9;
      this.intValues3 = var10;
      this.type2s = var11;
      this.floatValues3 = var12;
      this.floatValues4 = var13;
      this.floatValues5 = var14;
      this.floatValues6 = var15;
      this.intValues4 = var16;
      this.intValues5 = var17;
      this.intValue3 = var16.length;
      this.items = var18;
      this.items2 = var19;
      this.data3 = var20;
      this.data2 = var21;
      this.data = var22;
      this.floatValue = var23;
      this.floatValues7 = var24;
      this.intValue4 = var25;
      int var26 = 0;
      boolean var27 = true;
      int var28 = -1;
      Arrays.fill(this.intValues6, -1);

      for (int var29 = 0; var29 < var16.length; var29++) {
         int var30 = var10[var16[var29]];
         this.flags[var30] = true;
         var26 |= var17[var29];
         if (var30 < var28) {
            var27 = false;
         }

         var28 = var30;
         if (this.intValues6[var30] < 0) {
            this.intValues6[var30] = var29;
         }

         this.intValues7[var30] = var29 + 1;
      }

      for (int var31 = 0; var31 <= 6; var31++) {
         if (!var27) {
            this.intValues6[var31] = 0;
            this.intValues7[var31] = var16.length;
         } else if (this.intValues6[var31] < 0) {
            this.intValues6[var31] = 0;
            this.intValues7[var31] = 0;
         }
      }

      this.intValue5 = var26;
      var var32 = var22 == null ? null : var22.getItems();
      if (var32 == null || var32.isEmpty()) {
         this.items3 = var19;
      } else if (var19.isEmpty()) {
         this.items3 = var32;
      } else {
         var var33 = new ArrayList<>(var19);
         var33.addAll(var32);
         this.items3 = var33;
      }
   }

   public int method2028(int var1) {
      return var1 >= 0 && var1 < this.intValues6.length ? this.intValues6[var1] : 0;
   }

   public int method2029(int var1) {
      return var1 >= 0 && var1 < this.intValues7.length ? this.intValues7[var1] : this.intValue3;
   }

   public int method2030() {
      return this.texts.length;
   }

   public int method2031(String var1) {
      for (int var2 = 0; var2 < this.texts.length; var2++) {
         if (this.texts[var2].equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public Class37 method2032(String var1) {
      return this.items.get(var1);
   }

   public boolean method2033(int var1) {
      return var1 >= 0 && var1 < this.flags.length && this.flags[var1];
   }

   public boolean method2034(int var1) {
      return (this.intValue5 & var1) != 0;
   }

   public boolean method2035(Class15.Type var1) {
      return this.method2033(var1.ordinal());
   }

   public static final class Data {
      private final float[] floatValues;
      private final boolean flag;
      private final List<Class15.Data4> items;

      public float[] floatValues() {
         return this.floatValues;
      }

      public boolean flag() {
         return this.flag;
      }

      public List<Class15.Data4> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, flag, items);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValues=" + this.floatValues + ", " + "flag=" + this.flag + ", " + "items=" + this.items + "]";
      }

      public Data(float[] var1, boolean var2, List<Class15.Data4> var3) {
         this.floatValues = var1;
         this.flag = var2;
         this.items = var3;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<Class15.Data4> getItems() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final ParticleEffect particleEffect;
      private final ParticleEffect particleEffect2;
      private final int intValue;

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

      public ParticleEffect particleEffect() {
         return this.particleEffect;
      }

      public ParticleEffect particleEffect2() {
         return this.particleEffect2;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.particleEffect, other.particleEffect) && java.util.Objects.equals(this.particleEffect2, other.particleEffect2) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, particleEffect, particleEffect2, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "particleEffect=" + this.particleEffect + ", " + "particleEffect2=" + this.particleEffect2 + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(float var1, float var2, float var3, float var4, float var5, float var6, ParticleEffect var7, ParticleEffect var8, int var9) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.particleEffect = var7;
         this.particleEffect2 = var8;
         this.intValue = var9;
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

      public ParticleEffect getParticleEffect() {
         return this.particleEffect;
      }

      public ParticleEffect getParticleEffect2() {
         return this.particleEffect2;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data3 {
      private final Class15.Type4 type4;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final String text;
      private final ParticleEffect particleEffect;
      private final List<String> items;

      public Class15.Type4 type4() {
         return this.type4;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public String text() {
         return this.text;
      }

      public ParticleEffect particleEffect() {
         return this.particleEffect;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.type4, other.type4) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.particleEffect, other.particleEffect) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type4, floatValue, floatValue2, floatValue3, text, particleEffect, items);
      }

      @Override
      public String toString() {
         return "Data3[" + "type4=" + this.type4 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "text=" + this.text + ", " + "particleEffect=" + this.particleEffect + ", " + "items=" + this.items + "]";
      }

      public Data3(Class15.Type4 var1, float var2, float var3, float var4, String var5, ParticleEffect var6, List<String> var7) {
         this.type4 = var1;
         this.floatValue = var2;
         this.floatValue2 = var3;
         this.floatValue3 = var4;
         this.text = var5;
         this.particleEffect = var6;
         this.items = var7;
      }

      public boolean method2036(String var1) {
         return this.items.contains(var1);
      }

      public Class15.Type4 getType4() {
         return this.type4;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float hover() {
         return this.floatValue3;
      }

      public String getText() {
         return this.text;
      }

      public ParticleEffect getParticleEffect() {
         return this.particleEffect;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   public static final class Data4 {
      private final ParticleEffect particleEffect;
      private final int intValue;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final Class15.Type3 type3;

      public ParticleEffect particleEffect() {
         return this.particleEffect;
      }

      public int intValue() {
         return this.intValue;
      }

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

      public float floatValue8() {
         return this.floatValue8;
      }

      public Class15.Type3 type3() {
         return this.type3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.particleEffect, other.particleEffect) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.type3, other.type3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(particleEffect, intValue, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, type3);
      }

      @Override
      public String toString() {
         return "Data4[" + "particleEffect=" + this.particleEffect + ", " + "intValue=" + this.intValue + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "type3=" + this.type3 + "]";
      }

      public Data4(
         ParticleEffect var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, Class15.Type3 var11
      ) {
         this.particleEffect = var1;
         this.intValue = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
         this.floatValue4 = var6;
         this.floatValue5 = var7;
         this.floatValue6 = var8;
         this.floatValue7 = var9;
         this.floatValue8 = var10;
         this.type3 = var11;
      }

      public ParticleEffect getParticleEffect() {
         return this.particleEffect;
      }

      public int getIntValue() {
         return this.intValue;
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

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public Class15.Type3 getType3() {
         return this.type3;
      }
   }

   public static enum Type {
      HEAD,
      BODY,
      RIGHT_ARM,
      LEFT_ARM,
      RIGHT_LEG,
      LEFT_LEG;
   }

   public static enum Type2 {
      NONE,
      CAPE,
      LEGS_FRONT,
      LEGS_BACK,
      SPRING,
      SWAY,
      CLOTH,
      CLOCK;
   }

   public static enum Type3 {
      ALWAYS,
      MOVING,
      IDLE,
      AIRBORNE,
      SNEAKING,
      SET_BONUS;
   }

   public static enum Type4 {
      WALKER,
      HOPPER,
      FLYER,
      FLOATER,
      CRAWLER;
   }
}

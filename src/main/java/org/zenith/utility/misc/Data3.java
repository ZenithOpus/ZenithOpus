package org.zenith.utility.misc;

public final class Data3 {
   private final boolean flag;
   private final float floatValue;
   private final float floatValue2;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final float floatValue3;
   private final Data3.Type type;
   private final boolean flag2;
   private final boolean flag3;
   private final boolean flag4;
   private final boolean flag5;
   private final boolean flag6;

   public boolean flag() {
      return this.flag;
   }

   public float floatValue() {
      return this.floatValue;
   }

   public float floatValue2() {
      return this.floatValue2;
   }

   public int intValue() {
      return this.intValue;
   }

   public int intValue2() {
      return this.intValue2;
   }

   public int intValue3() {
      return this.intValue3;
   }

   public float floatValue3() {
      return this.floatValue3;
   }

   public Data3.Type type() {
      return this.type;
   }

   public boolean flag2() {
      return this.flag2;
   }

   public boolean flag3() {
      return this.flag3;
   }

   public boolean flag4() {
      return this.flag4;
   }

   public boolean flag5() {
      return this.flag5;
   }

   public boolean flag6() {
      return this.flag6;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Data3 other)) return false;
      return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.flag6, other.flag6);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(flag, floatValue, floatValue2, intValue, intValue2, intValue3, floatValue3, type, flag2, flag3, flag4, flag5, flag6);
   }

   @Override
   public String toString() {
      return "Data3[" + "flag=" + this.flag + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "floatValue3=" + this.floatValue3 + ", " + "type=" + this.type + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "flag6=" + this.flag6 + "]";
   }
   public static final Data3 INSTANCE = new Data3(false, 4.5F, 3.0F, 1, 0, 0, 180.0F, Data3.Type.KEEP, false, false, true, false, true);
   public static final Data3 INSTANCE2 = new Data3(true, 4.5F, 3.0F, 2, 0, 0, 180.0F, Data3.Type.KEEP, true, false, true, false, true);
   public static final Data3 INSTANCE3 = new Data3(false, 4.5F, 3.0F, 1, 50, 50, 35.0F, Data3.Type.KEEP, false, false, true, false, true);
   public static final Data3 INSTANCE4 = new Data3(true, 5.5F, 5.0F, 3, 0, 0, 180.0F, Data3.Type.SILENT, true, true, false, true, false);
   public static final Data3[] data3s = new Data3[]{null, INSTANCE, INSTANCE2, INSTANCE3, INSTANCE4};

   public Data3(
      boolean var1,
      float var2,
      float var3,
      int var4,
      int var5,
      int var6,
      float var7,
      Data3.Type var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      boolean var13
   ) {
      this.flag = var1;
      this.floatValue = var2;
      this.floatValue2 = var3;
      this.intValue = var4;
      this.intValue2 = var5;
      this.intValue3 = var6;
      this.floatValue3 = var7;
      this.type = var8;
      this.flag2 = var9;
      this.flag3 = var10;
      this.flag4 = var11;
      this.flag5 = var12;
      this.flag6 = var13;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public int getIntValue3() {
      return this.intValue3;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public Data3.Type getType() {
      return this.type;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public boolean isFlag5() {
      return this.flag5;
   }

   public boolean isFlag6() {
      return this.flag6;
   }

   public static enum Type {
      KEEP,
      SILENT,
      OFF;
   }
}

package org.zenith.utility.render.display.base;

import org.jetbrains.annotations.NotNull;

public final class ProxyDotRadiusData {
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;

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

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ProxyDotRadiusData other)) return false;
      return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
   }
   public static final ProxyDotRadiusData INSTANCE = new ProxyDotRadiusData(0.0F, 0.0F, 0.0F, 0.0F);

   public ProxyDotRadiusData(float var1, float var2, float var3, float var4) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      this.floatValue3 = var3;
      this.floatValue4 = var4;
   }

   public static ProxyDotRadiusData method8069(float var0) {
      return new ProxyDotRadiusData(var0, var0, var0, var0);
   }

   public static ProxyDotRadiusData method8070(float var0) {
      return new ProxyDotRadiusData(var0, 0.0F, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8071(float var0) {
      return new ProxyDotRadiusData(0.0F, var0, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8072(float var0) {
      return new ProxyDotRadiusData(0.0F, 0.0F, var0, 0.0F);
   }

   public static ProxyDotRadiusData method8073(float var0) {
      return new ProxyDotRadiusData(0.0F, 0.0F, 0.0F, var0);
   }

   public static ProxyDotRadiusData method8074(float var0, float var1) {
      return new ProxyDotRadiusData(var0, var1, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8075(float var0) {
      return new ProxyDotRadiusData(var0, var0, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8076(float var0, float var1) {
      return new ProxyDotRadiusData(0.0F, 0.0F, var1, var0);
   }

   public static ProxyDotRadiusData method8077(float var0) {
      return new ProxyDotRadiusData(0.0F, 0.0F, var0, var0);
   }

   public static ProxyDotRadiusData method8078(float var0, float var1) {
      return new ProxyDotRadiusData(var0, 0.0F, 0.0F, var1);
   }

   public static ProxyDotRadiusData method8079(float var0, float var1) {
      return new ProxyDotRadiusData(0.0F, var0, var1, 0.0F);
   }

   @NotNull
   @Override
   public String toString() {
      return "BorderRadius{topLeftRadius="
         + this.floatValue
         + ", topRightRadius="
         + this.floatValue2
         + ", bottomRightRadius="
         + this.floatValue3
         + ", bottomLeftRadius="
         + this.floatValue4
         + "}";
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
}

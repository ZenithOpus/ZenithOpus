package org.zenith.utility.misc;

public final class FloatArrayBuffer {
   private float[] floatValues = new float[3072];
   private float[] floatValues2 = new float[3072];
   private int intValue;

   public void clear() {
      this.intValue = 0;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean isEmpty() {
      return this.intValue == 0;
   }

   public void method379(float[] var1, float[] var2) {
      this.ensure(this.intValue + 1);
      System.arraycopy(var1, 0, this.floatValues, this.intValue * 12, 12);
      System.arraycopy(var2, 0, this.floatValues2, this.intValue * 12, 12);
      this.intValue++;
   }

   public float method380(int var1, int var2) {
      return this.floatValues[var1 * 12 + var2 * 3];
   }

   public float method381(int var1, int var2) {
      return this.floatValues[var1 * 12 + var2 * 3 + 1];
   }

   public float method382(int var1, int var2) {
      return this.floatValues[var1 * 12 + var2 * 3 + 2];
   }

   public float method383(int var1, int var2) {
      return this.floatValues2[var1 * 12 + var2 * 3];
   }

   public float method384(int var1, int var2) {
      return this.floatValues2[var1 * 12 + var2 * 3 + 1];
   }

   public float method385(int var1, int var2) {
      return this.floatValues2[var1 * 12 + var2 * 3 + 2];
   }

   public long method386() {
      return this.floatValues.length * 4L + this.floatValues2.length * 4L;
   }

   private void ensure(int var1) {
      if (var1 * 12 > this.floatValues.length) {
         int var2 = Math.max(this.floatValues.length * 2, var1 * 12);
         float[] var3 = new float[var2];
         float[] var4 = new float[var2];
         System.arraycopy(this.floatValues, 0, var3, 0, this.intValue * 12);
         System.arraycopy(this.floatValues2, 0, var4, 0, this.intValue * 12);
         this.floatValues = var3;
         this.floatValues2 = var4;
      }
   }
}

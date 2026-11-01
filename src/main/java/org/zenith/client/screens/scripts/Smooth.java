package org.zenith.client.screens.scripts;

public final class Smooth {
   private float value;
   private float target;
   private long last = System.nanoTime();
   private final float speed;
   private float min = -Float.MAX_VALUE;
   private float max = Float.MAX_VALUE;

   public Smooth(float var1) {
      this.speed = var1;
   }

   public float update() {
      long var1 = System.nanoTime();
      float var3 = Math.min(0.1F, (float)(var1 - this.last) / 1.0E9F);
      this.last = var1;
      this.value = this.value + (this.target - this.value) * (1.0F - (float)Math.exp(-var3 * this.speed));
      if (Math.abs(this.target - this.value) < 0.05F) {
         this.value = this.target;
      }

      return this.value;
   }

   public float value() {
      return this.value;
   }

   public float target() {
      return this.target;
   }

   public void target(float var1) {
      this.target = var1;
   }

   public void add(float var1) {
      this.target = Math.max(this.min, Math.min(this.max, this.target + var1));
   }

   public void clamp(float var1, float var2) {
      this.min = var1;
      this.max = var2;
      this.target = Math.max(var1, Math.min(var2, this.target));
      this.value = Math.max(var1, Math.min(var2, this.value));
   }

   public void reset(float var1) {
      this.value = var1;
      this.target = var1;
      this.last = System.nanoTime();
   }

   public static float press(long var0, long var2) {
      if (var0 <= 0L) {
         return 0.0F;
      } else {
         float var4 = (float)(System.currentTimeMillis() - var0) / (float)var2;
         return var4 >= 1.0F ? 0.0F : (1.0F - var4) * (1.0F - var4);
      }
   }
}

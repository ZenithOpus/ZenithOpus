package org.zenith;

import lombok.Generated;

public class Animation {
   private long duration;
   private float value;
   private Easing easing;
   private long startTime;
   private float floatValue;
   private float floatValue2;
   private boolean done;
   private boolean flag;

   public Animation(long var1, float var3, Easing var4) {
      this.duration = var1;
      this.easing = var4;
      this.value = var3;
      this.floatValue = var3;
      this.floatValue2 = var3;
      this.done = true;
   }

   public Animation(long var1, Easing var3) {
      this(var1, 0.0F, var3);
   }

   public void method2807(boolean var1) {
      this.method2808(var1 ? 1.0F : 0.0F);
   }

   public float method2808(float var1) {
      long var2 = System.currentTimeMillis();
      if (var1 != this.floatValue2) {
         this.floatValue = this.value;
         this.floatValue2 = var1;
         this.startTime = var2;
         this.done = false;
      }

      long var4 = var2 - this.startTime;
      if (var4 >= this.duration) {
         this.value = this.floatValue2;
         this.done = true;
         return this.value;
      } else {
         float var6 = (float)var4 / (float)this.duration;
         float var7 = this.easing.ease(var6, 0.0F, 1.0F, 1.0F);
         this.value = this.floatValue + (this.floatValue2 - this.floatValue) * var7;
         return this.value;
      }
   }

   public void method2809(float var1) {
      this.value = var1;
      this.floatValue = var1;
      this.floatValue2 = var1;
      this.done = true;
   }

   public void reset(float var1) {
      this.value = var1;
      this.floatValue = var1;
      this.floatValue2 = var1;
      this.done = true;
   }

   public void reset() {
      this.reset(0.0F);
   }

   public void method2810(float var1) {
      if (var1 != this.floatValue2) {
         this.floatValue = this.value;
         this.floatValue2 = var1;
         this.startTime = System.currentTimeMillis();
         this.done = false;
      }
   }

   public float update() {
      return this.method2808(this.floatValue2);
   }

   @Generated
   public long getDuration() {
      return this.duration;
   }

   @Generated
   public float getValue() {
      return this.value;
   }

   @Generated
   public Easing getEasing() {
      return this.easing;
   }

   @Generated
   public long getStartTime() {
      return this.startTime;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public boolean isDone() {
      return this.done;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public void setDuration(long var1) {
      this.duration = var1;
   }

   @Generated
   public void setEasing(Easing var1) {
      this.easing = var1;
   }

   @Generated
   public void setStartTime(long var1) {
      this.startTime = var1;
   }

   @Generated
   public void setFloatValue(float var1) {
      this.floatValue = var1;
   }

   @Generated
   public void setFloatValue2(float var1) {
      this.floatValue2 = var1;
   }

   @Generated
   public void setDone(boolean var1) {
      this.done = var1;
   }

   @Generated
   public void setFlag(boolean var1) {
      this.flag = var1;
   }
}

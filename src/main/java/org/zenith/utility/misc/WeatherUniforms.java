package org.zenith.utility.misc;

import org.zenith.utility.render.PostEffectManager;

public final class WeatherUniforms {
   private WeatherUniforms() {
   }

   public static void method799(
      WeatherUniforms.Handler var0,
      WeatherFlakeChannel var1,
      float var2,
      float var3,
      float var4,
      double var5,
      double var7,
      double var9,
      float var11,
      float var12,
      float var13,
      float var14
   ) {
      var0.method259("Intensity", var2);
      var0.method259("time", var4);
      var0.method260("CameraPosition", (float)var5, (float)var7, (float)var9);
      var0.method259("SkyPitch", var11);
      var0.method259("SkyYaw", var12);
      var0.method259("TanHalfFov", var13);
      var0.method259("Aspect", var14);
      method800(var0, "SkyZenith", var1.floatValues);
      method800(var0, "SkyHorizon", var1.floatValues2);
      method800(var0, "SkyNadir", var1.floatValues3);
      method800(var0, "SunDir", var1.floatValues4);
      method800(var0, "SunColor", var1.floatValues5);
      var0.method259("SunSize", var1.floatValue2);
      var0.method259("SunGlare", var1.floatValue3);
      var0.method259("CloudCover", var1.floatValue4);
      var0.method259("CloudDark", var1.floatValue5);
      var0.method259("CloudSpeed", var1.floatValue6);
      var0.method259("CloudScale", var1.floatValue7);
      method800(var0, "CloudLit", var1.floatValues7);
      method800(var0, "CloudShadow", var1.floatValues8);
      var0.method259("StarAmount", var1.floatValue8);
      var0.method259("BandAmount", var1.floatValue9);
      var0.method259("AuroraAmount", var1.floatValue10);
      var0.method259("MeteorAmount", var1.floatValue11);
      var0.method259("HazeAmount", var1.floatValue12);
      method800(var0, "HazeColor", var1.floatValues9);
      method800(var0, "GlowDir", var1.floatValues6);
      var0.method259("GlowFocus", var1.floatValue);
      var0.method259("FrostAmount", var1.flag ? Math.clamp(var1.floatValue23, 0.0F, 1.0F) * Math.clamp(var2, 0.0F, 1.0F) * 0.55F : 0.0F);
      var0.method259("Flash", var3);
      method800(var0, "FlashColor", var1.floatValues10);
   }

   private static void method800(WeatherUniforms.Handler var0, String var1, float[] var2) {
      var0.method260(var1, var2[0], var2[1], var2[2]);
   }

   public static final class Data implements WeatherUniforms.Handler {
      private final PostEffectManager.Entry entry;

      public PostEffectManager.Entry entry() {
         return this.entry;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.entry, other.entry);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(entry);
      }

      @Override
      public String toString() {
         return "Data[" + "entry=" + this.entry + "]";
      }

      public Data(PostEffectManager.Entry var1) {
         this.entry = var1;
      }

      @Override
      public void method259(String var1, float var2) {
         this.entry.method2828(var1, var2);
      }

      @Override
      public void method260(String var1, float var2, float var3, float var4) {
         this.entry.method2830(var1, var2, var3, var4);
      }

      public PostEffectManager.Entry getEntry() {
         return this.entry;
      }
   }

   public interface Handler {
      void method259(String var1, float var2);

      void method260(String var1, float var2, float var3, float var4);
   }
}

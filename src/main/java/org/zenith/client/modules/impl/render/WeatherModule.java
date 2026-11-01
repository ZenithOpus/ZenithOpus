package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BackgroundRenderEvent;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.game.Print;
import org.zenith.utility.game.Util11;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.FlakeUtil;
import org.zenith.utility.misc.FoliageSprites;
import org.zenith.utility.misc.SnowVisibility;
import org.zenith.utility.misc.Util21;
import org.zenith.utility.misc.Util32;
import org.zenith.utility.misc.Util8;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.misc.WeatherPresets;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.RippleUtil;
import org.zenith.utility.render.SnowShader;
import org.zenith.utility.render.Util25;
import org.zenith.utility.render.WeatherPreviewUtil;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Weather",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.worldweather.description"
)
public final class WeatherModule extends Module {
   public static final WeatherModule INSTANCE = new WeatherModule();
   private static final int CONST_intValue = 52;
   private static final long CONST_longValue = 400L;
   private static final long CONST_longValue2 = 118049640925L;
   private static final long CONST_longValue3 = System.currentTimeMillis();
   private static final float CONST_floatValue = 0.9F;
   private static final int CONST_intValue2 = 0;
   private static final int CONST_intValue3 = 1;
   private static final int CONST_intValue4 = 2;
   private final ModeSetting mode = new ModeSetting(
      "module.weather.mode", "module.weather.mode.desc", () -> true, "module.weather.mode.rain", "module.weather.mode.fog", "module.weather.mode.snow"
   );
   private static final int CONST_intValue5 = 0;
   private static final int CONST_intValue6 = 1;
   private static final int CONST_intValue7 = 2;
   private static final int CONST_intValue8 = 3;
   private final ModeSetting sky = new ModeSetting(
      "module.weather.sky",
      "module.weather.sky.desc",
      () -> true,
      "module.weather.sky.weather",
      "module.weather.sky.clear",
      "module.weather.sky.storm",
      "module.weather.sky.stars"
   );
   private final SliderSetting time = new SliderSetting("module.weather.time", 12.0F, 0.0F, 24.0F, 0.25F, "module.weather.time.desc", "", () -> true, null)
      .method2399(WeatherModule::method3095);
   private final ModeSetting water = new ModeSetting(
      "module.weather.water", "module.weather.water.desc", this::method3088, "module.weather.water.puddles", "module.weather.water.film"
   );
   private final SliderSetting drops = new SliderSetting(
      "module.weather.drops", 100.0F, 10.0F, 200.0F, 5.0F, "module.weather.drops.desc", "%", this::method3091, null
   );
   private final SliderSetting wind = new SliderSetting(
      "module.weather.wind", 100.0F, 0.0F, 250.0F, 10.0F, "module.weather.wind.desc", "%", this::method3091, null
   );
   private final BooleanSetting storm = new BooleanSetting("module.weather.storm", "module.weather.storm.desc", true, this::method3088);
   private final SliderSetting snowDepth = new SliderSetting(
      "module.weather.snowDepth", 100.0F, 20.0F, 250.0F, 5.0F, "module.weather.snowDepth.desc", "%", this::method3090, null
   );
   private final SliderSetting fogRange = new SliderSetting(
      "module.weather.fogRange", 40.0F, 12.0F, 160.0F, 4.0F, "module.weather.fogRange.desc", "b", this::method3089, null
   );
   private final SliderSetting mist = new SliderSetting(
      "module.weather.mist", 100.0F, 0.0F, 200.0F, 5.0F, "module.weather.mist.desc", "%", this::method3089, null
   );
   private final SliderSetting haze = new SliderSetting(
      "module.weather.haze", 100.0F, 0.0F, 200.0F, 5.0F, "module.weather.haze.desc", "%", this::method3089, null
   );
   private final SliderSetting reflect = new SliderSetting(
      "module.weather.reflect", 100.0F, 0.0F, 200.0F, 5.0F, "module.weather.reflect.desc", "%", () -> true, null
   );
   private WeatherFlakeChannel weatherFlakeChannel;
   private WeatherFlakeChannel weatherFlakeChannel2;
   private float floatValue = Float.NaN;
   private WeatherFlakeChannel.Data data;
   private WeatherPresets weatherPresets;
   private final Glowboost glowboost = new Glowboost();
   private final Glowboost glowboost2 = new Glowboost();
   private final Print print = new Print();
   private final MatrixStack matrixStack = new MatrixStack();
   private FlakeState flakeState;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private long longValue;
   private FlakeState flakeState2;
   private static final float CONST_floatValue2 = 4500.0F;
   private static final float CONST_floatValue3 = 14000.0F;
   private float floatValue2;
   private long longValue2 = System.currentTimeMillis();
   private static final float CONST_floatValue4 = 900.0F;
   private float floatValue3;
   private long longValue3 = System.currentTimeMillis();
   private boolean flag;

   private WeatherModule() {
      this.drops.setTitleKey(() -> this.method3090() ? "module.weather.flakes" : "module.weather.drops");
      this.drops.setTipKey(() -> this.method3090() ? "module.weather.flakes.desc" : "module.weather.drops.desc");
      this.wind.setTipKey(() -> this.method3090() ? "module.weather.wind.snow.desc" : "module.weather.wind.desc");
      this.reflect.setTitleKey(() -> this.method3090() ? "module.weather.sheen" : "module.weather.reflect");
      this.reflect.setTipKey(() -> this.method3090() ? "module.weather.sheen.desc" : "module.weather.reflect.desc");
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.WORLD_WEATHER;
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(
         this.mode, this.sky, this.time, this.water, this.drops, this.wind, this.storm, this.snowDepth, this.fogRange, this.mist, this.haze, this.reflect
      );
   }

   public WeatherFlakeChannel method3085() {
      WeatherFlakeChannel var1 = this.method3092();
      float var2 = this.time.getValue3();
      WeatherPresets var3 = this.method3087();
      if (this.weatherFlakeChannel == null
         || var1 != this.weatherFlakeChannel2
         || var2 != this.floatValue
         || !this.method3086(this.data)
         || var3 != this.weatherPresets) {
         WeatherFlakeChannel.Data var4 = this.method3093();
         this.weatherFlakeChannel = var1.method7458(Util8.method1722(var2), var4, var3);
         this.weatherFlakeChannel2 = var1;
         this.floatValue = var2;
         this.data = var4;
         this.weatherPresets = var3;
      }

      return this.weatherFlakeChannel;
   }

   private boolean method3086(WeatherFlakeChannel.Data var1) {
      if (var1 == null) {
         return false;
      } else {
         boolean var2 = this.method3089();
         return Float.compare(var1.getFloatValue(), method3094(this.drops)) == 0
            && Float.compare(var1.getFloatValue2(), method3094(this.wind)) == 0
            && Float.compare(var1.getFloatValue3(), method3094(this.reflect)) == 0
            && Float.compare(var1.getFloatValue4(), var2 ? method3094(this.mist) : 1.0F) == 0
            && Float.compare(var1.getFloatValue5(), var2 ? method3094(this.haze) : 1.0F) == 0
            && Float.compare(var1.getFloatValue6(), var2 ? this.fogRange.getValue3() : -1.0F) == 0
            && var1.isFlag() == (this.storm.isEnabled() && this.method3088());
      }
   }

   private WeatherPresets method3087() {
      return switch (this.sky.getIndex()) {
         case 1 -> WeatherPresets.INSTANCE;
         case 2 -> WeatherPresets.INSTANCE2;
         case 3 -> WeatherPresets.INSTANCE3;
         default -> null;
      };
   }

   private boolean method3088() {
      return this.mode.getIndex() == 0;
   }

   private boolean method3089() {
      return this.mode.getIndex() == 1;
   }

   private boolean method3090() {
      return this.mode.getIndex() == 2;
   }

   private boolean method3091() {
      return this.method3088() || this.method3090();
   }

   private WeatherFlakeChannel method3092() {
      if (this.method3090()) {
         return WeatherFlakeChannel.INSTANCE3;
      } else if (this.method3089()) {
         return WeatherFlakeChannel.INSTANCE4;
      } else {
         return this.water.getIndex() == 0 ? WeatherFlakeChannel.INSTANCE : WeatherFlakeChannel.INSTANCE2;
      }
   }

   private WeatherFlakeChannel.Data method3093() {
      boolean var1 = this.method3089();
      return new WeatherFlakeChannel.Data(
         method3094(this.drops),
         method3094(this.wind),
         method3094(this.reflect),
         var1 ? method3094(this.mist) : 1.0F,
         var1 ? method3094(this.haze) : 1.0F,
         var1 ? this.fogRange.getValue3() : -1.0F,
         this.storm.isEnabled() && this.method3088()
      );
   }

   private static float method3094(SliderSetting var0) {
      return var0.getValue3() / 100.0F;
   }

   private static String method3095(float var0) {
      int var1 = Math.round(var0 * 60.0F) % 1440;
      if (var1 < 0) {
         var1 += 1440;
      }

      return String.format(Locale.US, "%02d:%02d", var1 / 60, var1 % 60);
   }

   public float method3096() {
      return (float)(System.currentTimeMillis() - CONST_longValue3) / 1000.0F;
   }

   private float method3097() {
      float var1 = this.isEnabled() ? 1.0F : 0.0F;
      float var2 = Math.clamp((float)(System.currentTimeMillis() - this.longValue3) / 900.0F, 0.0F, 1.0F);
      var2 = var2 * var2 * (3.0F - 2.0F * var2);
      return this.floatValue3 + (var1 - this.floatValue3) * var2;
   }

   public float method3098() {
      return this.method3097();
   }

   public boolean method3099() {
      return this.method3098() > 0.01F && mc.world != null && mc.getFramebuffer() != null;
   }

   public float method3100() {
      float var1 = this.isEnabled() ? 1.0F : 0.0F;
      float var2 = var1 > this.floatValue2 ? 4500.0F : 14000.0F;
      float var3 = Math.clamp((float)(System.currentTimeMillis() - this.longValue2) / var2, 0.0F, 1.0F);
      return this.floatValue2 + (var1 - this.floatValue2) * var3;
   }

   public float method3101() {
      return this.flag ? 0.85F : 0.0F;
   }

   private void method3102() {
      boolean var1 = this.isEnabled() && this.method3092().flag;
      if (var1 != this.flag) {
         this.flag = var1;
         FoliageSprites.enabled = var1;
         mc.execute(() -> {
            if (mc.world != null) {
               mc.reloadResources();
            }
         });
      }
   }

   public boolean method3103() {
      return this.method3100() > 0.01F && this.method3085().floatValue23 > 0.001F && mc.world != null && mc.getFramebuffer() != null;
   }

   public float method3104() {
      return method3105(this.method3096(), this.method3085().floatValue13) * this.method3097();
   }

   private static float method3105(float var0, float var1) {
      if (var1 <= 0.0F) {
         return 0.0F;
      } else {
         int var2 = (int)Math.floor(var0 * var1);
         float var3 = 0.0F;

         for (int var4 = var2 - 1; var4 <= var2; var4++) {
            float var5 = (var4 + Glowboost.method3268(118049640925L, var4, 0)) / var1;
            float var6 = var0 - var5;
            if (!(var6 < 0.0F)) {
               float var7 = 0.35F + 0.65F * Glowboost.method3268(118049640925L, var4, 1);
               float var8 = method3106(var6, 0.09F) + method3106(var6 - 0.13F, 0.17F) * 0.65F;
               var3 = Math.max(var3, Math.min(1.0F, var8) * var7);
            }
         }

         return var3;
      }
   }

   private static float method3106(float var0, float var1) {
      return var0 < 0.0F ? 0.0F : (float)(Math.exp(-var0 / var1) * (1.0 - Math.exp(-var0 / 0.012)));
   }

   public float method3107() {
      return lerp(1.0F, this.method3085().floatValue16, this.method3097());
   }

   public void method3108(float[] var1) {
      float var2 = this.method3097();
      WeatherFlakeChannel var3 = this.method3085();
      var1[0] = lerp(1.0F, var3.floatValues11[0], var2);
      var1[1] = lerp(1.0F, var3.floatValues11[1], var2);
      var1[2] = lerp(1.0F, var3.floatValues11[2], var2);
      var1[3] = lerp(1.0F, var3.floatValue15, var2) * (1.0F + this.method3104() * 0.85F);
   }

   public float method3109() {
      return this.method3097() < 0.15F ? -1.0F : this.method3085().floatValue17;
   }

   public float method3110() {
      float var1 = this.method3097();
      if (var1 <= 0.01F) {
         return -1.0F;
      } else {
         WeatherFlakeChannel var2 = this.method3085();
         return var2.intValue2 == 0 ? 0.0F : Math.min(1.0F, var2.floatValue18 * var1);
      }
   }

   public float method3111() {
      float var1 = this.method3110();
      if (var1 < 0.0F) {
         return -1.0F;
      } else {
         return this.method3085().floatValue13 > 0.0F ? var1 : 0.0F;
      }
   }

   public int method3112() {
      return this.method3110() < 0.0F ? -1 : this.method3085().intValue2;
   }

   public boolean method3113() {
      return this.method3110() > 0.0F;
   }

   public boolean method3114(Entity var1, BlockState var2) {
      if (this.isEnabled() && var1.getEntityWorld().isClient()) {
         BlockSoundGroup var3 = var2.getSoundGroup();
         float var4 = 0.94F + var1.getRandom().nextFloat() * 0.12F;
         WeatherFlakeChannel var5 = this.method3085();
         if (var5.intValue2 == 1) {
            var1.playSound(SoundEvents.BLOCK_WET_GRASS_STEP, var3.getVolume() * 0.22F, var3.getPitch() * var4);
            return true;
         } else if (var5.flag) {
            var1.playSound(SoundEvents.BLOCK_SNOW_STEP, var3.getVolume() * 0.2F, var3.getPitch() * var4);
            return true;
         } else if (var5.floatValue27 >= 0.9F) {
            var1.playSound(var3.getStepSound(), var3.getVolume() * 0.08F, var3.getPitch() * 0.78F * var4);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @EventTarget
   public void method3115(BackgroundRenderEvent var1) {
      if (!WorldModule.INSTANCE.method5648()) {
         float var2 = this.method3097();
         if (!(var2 <= 0.01F)) {
            WeatherFlakeChannel var3 = this.method3085();
            float var4 = var1.getFloatValue2() > 0.0F ? lerp(var1.getFloatValue2(), var3.floatValue14, var2) : var3.floatValue14;
            var1.setFloatValue(var4);
            int var5 = method3120(var1.getIntValue(), var3.intValue, var2);
            float var6 = ColorUtils.method6427(var5) * lerp(1.0F, 0.9F, var2);
            var1.setColor(Math.clamp((long)((int)(var6 * 255.0F + 0.5F)), 0, 255) << 24 | var5 & 16777215);
            var1.setCancelled(true);
         }
      }
   }

   public boolean method3116() {
      return this.method3097() > 0.01F && mc.world != null && mc.player != null;
   }

   public void method3117(Matrix4f var1) {
      this.method3102();
      if (this.method3116()) {
         float var2 = this.method3097();
         MatrixStack var3 = this.matrixStack;
         var3.loadIdentity();
         var3.multiplyPositionMatrix(var1);
         WeatherFlakeChannel var4 = this.method3085();
         Camera var5 = mc.getEntityRenderDispatcher().camera;
         Vec3d var6 = var5.getCameraPos();
         boolean var7 = var4.flag || var4.intValue2 != 0 || !this.method3103();
         FlakeState var8 = var7 ? this.method3118(var6) : null;
         if (!var7 || var8 != null) {
            float var9 = this.method3096();
            long var10 = System.currentTimeMillis();
            this.glowboost.method3232(var5, 0.0F);
            this.print.method5440(mc.world, mc.world.getPlayers(), var4, var10);
            SnowShader.method403(method3094(this.reflect));
            SnowShader.method392(var6, var4, var4.floatValue23);
            if (SnowVisibility.isVisible()) {
               SnowVisibility.method2402(this.glowboost);
            } else {
               FlakeUtil.method7507(this.glowboost, var8, var4, var2, this.print, var10, var6.x, var6.y, var6.z, method3094(this.snowDepth));
            }

            this.print.method5444(this.glowboost, var4, var2, var10, var6.x, var6.y, var6.z);
            if (var8 != null && !this.method3103()) {
               Util21.method5356(this.glowboost, var8, var4, var2, var9, var6.x, var6.y, var6.z);
            }

            if (var8 != null) {
               RippleUtil.method7826(this.glowboost, var8, var4, var2, var9, var6.x, var6.y, var6.z);
            }

            ImmediateRenderer.method8013(null);
            Matrix4fStack var12 = RenderSystem.getModelViewStack();
            var12.pushMatrix();
            var12.identity();

            try {
               SnowShader.method397(var3);
               this.glowboost.method3253(var3);
            } finally {
               var12.popMatrix();
            }
         }
      }
   }

   private FlakeState method3118(Vec3d var1) {
      int var2 = (int)Math.floor(var1.x);
      int var3 = (int)Math.floor(var1.z);
      long var4 = System.currentTimeMillis();
      if (this.flakeState == null) {
         this.flakeState = FlakeState.method4535(mc.world, var2, var3, 52);
         this.longValue = var4;
      } else if (var4 - this.longValue > 400L) {
         this.flakeState.method4536(mc.world, var2, var3);
         this.longValue = var4;
      } else if (var2 != this.intValue || var3 != this.intValue2) {
         this.flakeState.method4537(mc.world, var2, var3);
      }

      Util11.method2332(mc.world, this.flakeState);
      this.intValue = var2;
      this.intValue2 = var3;
      return this.flakeState;
   }

   @Override
   public void onEnable() {
      this.floatValue2 = this.method3100();
      this.floatValue3 = this.method3097();
      this.longValue2 = System.currentTimeMillis();
      this.longValue3 = this.longValue2;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.floatValue2 = this.method3100();
      this.floatValue3 = this.method3097();
      this.longValue2 = System.currentTimeMillis();
      this.longValue3 = this.longValue2;
      super.onDisable();
      if (this.flag) {
         this.flag = false;
         FoliageSprites.enabled = false;
         mc.send(mc::reloadResources);
      }

      this.flakeState = null;
      this.intValue = Integer.MIN_VALUE;
      this.print.clear();
   }

   public void method3119(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      WeatherFlakeChannel var7 = this.method3085();
      float var8 = 1.0F;
      float var9 = this.method3096();
      float var10 = method3105(var9, var7.floatValue13);
      WeatherPreviewUtil.method255(var1, var2, var3, var4, var5, var7, var8, var10, var9, var6);
      if (this.flakeState2 == null) {
         this.flakeState2 = FlakeState.method4542(5);
      }

      float var11 = var4 / 11.0F * 1.15F;
      float var12 = var3 + var5 * 0.82F;
      MatrixStack var13 = Util25.method6477(var1, var2 + var4 / 2.0F, var12, var11);
      this.glowboost.method3233(Util25.method6478(), Util25.method6479(), 0.0F);
      Util25.method6480(this.glowboost, this.flakeState2, Util25.Data.method6483(var7, var8), var6);
      Util32.method7994(this.glowboost, this.flakeState2, var7, var8 * var6, var9, 0.0, 2.0, 0.0);
      FlakeUtil.method7508(this.glowboost, this.flakeState2, var7, var8 * var6, 0.0, 2.0, 0.0);
      Print.method5446(this.glowboost, var7, var6);
      Util21.method5356(this.glowboost, this.flakeState2, var7, var8 * var6, var9, 0.0, 2.0, 0.0);
      RippleUtil.method7827(this.glowboost, this.flakeState2, var7, var8 * var6, var9, 0.0, 2.0, 0.0);
      this.glowboost.method3253(var13);
   }

   private static float lerp(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * Math.clamp(var2, 0.0F, 1.0F);
   }

   private static int method3120(int var0, int var1, float var2) {
      if (var0 >>> 24 == 0) {
         return var1;
      } else {
         int var3 = method3121(ColorUtils.method6423(var0), ColorUtils.method6423(var1), var2);
         int var4 = method3121(ColorUtils.method6420(var0), ColorUtils.method6420(var1), var2);
         int var5 = method3121(ColorUtils.method6421(var0), ColorUtils.method6421(var1), var2);
         int var6 = method3121(ColorUtils.method6422(var0), ColorUtils.method6422(var1), var2);
         return var3 << 24 | var4 << 16 | var5 << 8 | var6;
      }
   }

   private static int method3121(int var0, int var1, float var2) {
      return Math.clamp((long)((int)(var0 + (var1 - var0) * var2 + 0.5F)), 0, 255);
   }
}

package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ModuleHookDefinitions;

@ModuleInfo(
   name = "ElytraBooster",
   category = ModuleCategory.MOVEMENT,
   description = "module.elytrabooster.description"
)
public final class ElytraBoosterModule extends Module {
   public static final ElytraBoosterModule INSTANCE = new ElytraBoosterModule();
   private static final int CONST_intValue = 30;
   private static final int CONST_intValue2 = 56;
   private static final int CONST_intValue3 = 25;
   private static final float[] CONST_floatValues = new float[]{1.67F, 1.68F, 1.69F, 1.72F, 1.72F, 1.73F, 1.74F, 1.75F, 1.76F};
   private static final float[] CONST_floatValues2 = new float[]{1.72F, 1.72F, 1.72F, 1.72F, 1.72F, 1.73F, 1.74F, 1.76F, 1.8F};
   private static final float[] CONST_floatValues3 = new float[]{1.6F, 1.63F, 1.66F, 1.71F, 1.73F, 1.81F, 1.81F, 1.81F, 1.81F};
   private static final float[] CONST_floatValues4 = new float[]{1.6F, 1.62F, 1.6F, 1.62F, 1.64F, 1.68F, 1.75F, 1.9F, 2.16F};
   private final BooleanSetting acceleration = new BooleanSetting("module.elytraBooster.acceleration", "module.elytraBooster.acceleration.desc", true);
   private final ModeSetting mode = new ModeSetting(
      "module.elytraBooster.mode",
      "module.elytraBooster.mode.desc",
      this.acceleration::isEnabled,
      "module.elytraBooster.mode.reallyWorld",
      "module.elytraBooster.mode.bravo",
      "module.elytraBooster.mode.bravoGrief",
      "module.elytraBooster.mode.lightningGrim",
      "module.elytraBooster.mode.custom"
   );
   private final ModeSetting.Entry entry = this.mode.getValues().get(0);
   private final ModeSetting.Entry entry2 = this.mode.getValues().get(1);
   private final ModeSetting.Entry entry3 = this.mode.getValues().get(2);
   private final ModeSetting.Entry entry4 = this.mode.getValues().get(3);
   private final ModeSetting.Entry entry5 = this.mode.getValues().get(4);
   private final SliderSetting lightningGrimMaxVelocity = new SliderSetting(
      "module.elytraBooster.lightningGrimMaxVelocity",
      1.67F,
      0.1F,
      3.0F,
      0.01F,
      "module.elytraBooster.lightningGrimMaxVelocity.desc",
      "",
      () -> this.acceleration.isEnabled() && this.entry4.isSelected()
   );
   private final SliderSetting[] sliderSettings = method1781(
      "module.elytraBooster.slider.xz", 9, 1.52F, () -> this.acceleration.isEnabled() && this.entry5.isSelected()
   );
   private final SliderSetting[] sliderSettings2 = method1781(
      "module.elytraBooster.slider.y", 11, 1.51F, () -> this.acceleration.isEnabled() && this.entry5.isSelected() && !this.individualY.isEnabled()
   );
   private final SliderSetting[] sliderSettings3 = method1782(
      "module.elytraBooster.slider.individualY",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled()
   );
   private final SliderSetting[] sliderSettings4 = method1782(
      "module.elytraBooster.slider.accelerationStart",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled()
   );
   private final SliderSetting[] sliderSettings5 = method1782(
      "module.elytraBooster.slider.accelerationFurther",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled()
   );
   private final GroupSetting xz = new GroupSetting(
      "module.elytraBooster.window.xz", () -> this.acceleration.isEnabled() && this.entry5.isSelected(), this.sliderSettings
   );
   private final BooleanSetting individualY = new BooleanSetting(
      "module.elytraBooster.individualY", "module.elytraBooster.individualY.desc", false, () -> this.acceleration.isEnabled() && this.entry5.isSelected()
   );
   private final GroupSetting y = new GroupSetting(
      "module.elytraBooster.window.y", () -> this.acceleration.isEnabled() && this.entry5.isSelected() && !this.individualY.isEnabled(), this.sliderSettings2
   );
   private final BooleanSetting customAcceleration = new BooleanSetting(
      "module.elytraBooster.customAcceleration",
      "module.elytraBooster.customAcceleration.desc",
      false,
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled()
   );
   private final GroupSetting individualY2 = new GroupSetting(
      "module.elytraBooster.window.individualY",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings3, 0, 25)
   );
   private final GroupSetting individualYContinuation = new GroupSetting(
      "module.elytraBooster.window.individualYContinuation",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings3, 25, 50)
   );
   private final GroupSetting individualYContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.individualYContinuationSecond",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings3, 50, 56)
   );
   private final GroupSetting accelerationStart = new GroupSetting(
      "module.elytraBooster.window.accelerationStart",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings4, 0, 25)
   );
   private final GroupSetting accelerationStartContinuation = new GroupSetting(
      "module.elytraBooster.window.accelerationStartContinuation",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings4, 25, 50)
   );
   private final GroupSetting accelerationStartContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.accelerationStartContinuationSecond",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings4, 50, 56)
   );
   private final GroupSetting accelerationFurther = new GroupSetting(
      "module.elytraBooster.window.accelerationFurther",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings5, 0, 25)
   );
   private final GroupSetting accelerationFurtherContinuation = new GroupSetting(
      "module.elytraBooster.window.accelerationFurtherContinuation",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings5, 25, 50)
   );
   private final GroupSetting accelerationFurtherContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.accelerationFurtherContinuationSecond",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings5, 50, 56)
   );
   private final MultiSelectSetting render = new MultiSelectSetting("module.elytraBooster.render", "module.elytraBooster.render.desc");
   private final MultiSelectSetting.Entry entry6 = new MultiSelectSetting.Entry(this.render, "module.elytraBooster.renderAngles", false);
   private int intValue;

   private ElytraBoosterModule() {
      this.render.setVisible(this.entry5::isSelected);
      ModuleHookDefinitions.class123.method1646(0, var1 -> {
         if (this.method1767()) {
            if (this.method1768(var1.intValue, var1.intValue2, var1.vec3d3)) {
               var1.vec3d = var1.vec3d3;
            } else {
               var1.vec3d = this.method1771(var1.vec3d3, var1.vec3d4);
            }
         }
      });
   }

   public boolean method1767() {
      return this.isEnabled() && this.acceleration.isEnabled() && mc.player != null && mc.player.isGliding();
   }

   public boolean method1768(int var1, int var2, Vec3d var3) {
      this.intValue = var1;
      return false;
   }

   public Vec3d method1769(Vec3d var1) {
      return var1;
   }

   public Vec3d method1770(Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4) {
      return var2;
   }

   public Vec3d method1771(Vec3d var1, Vec3d var2) {
      ClientPlayerEntity var3 = mc.player;
      if (var3 != null && var2 != null) {
         float var4 = MathHelper.wrapDegrees(var3.getYaw());
         float var5 = var3.getPitch();
         Vec3d var6 = var2.normalize();
         if (var6.lengthSquared() > 0.0) {
            var4 = (float)Math.toDegrees(Math.atan2(-var6.x, var6.z));
            var5 = (float)(-Math.toDegrees(Math.asin(MathHelper.clamp(var6.y, -1.0, 1.0))));
         }

         float var7;
         float var8;
         if (this.entry4.isSelected()) {
            var8 = var7 = this.lightningGrimMaxVelocity.getValue3();
         } else if (this.entry5.isSelected()) {
            boolean var9 = this.customAcceleration.isEnabled() && this.intValue < 30;
            var7 = this.method1777(var4, this.sliderSettings);
            var8 = this.individualY.isEnabled() ? this.method1773(var5, var9) : this.method1777(var5, this.sliderSettings2);
            var7 = Math.max(var7, var8);
         } else if (this.entry2.isSelected()) {
            var7 = this.method1776(var5, var4);
            var8 = this.method1775(var5);
         } else if (this.entry3.isSelected()) {
            var7 = method1774(CONST_floatValues, var4);
            var8 = method1774(CONST_floatValues2, var5);
         } else {
            if (!this.entry.isSelected()) {
               return var1;
            }

            var7 = method1774(CONST_floatValues3, var4);
            var8 = method1774(CONST_floatValues4, var5);
            var7 = Math.max(var7, var8);
         }

         return var1.add(
            var2.x * 0.1 + (var2.x * var7 - var1.x) * 0.5, var2.y * 0.1 + (var2.y * var8 - var1.y) * 0.5, var2.z * 0.1 + (var2.z * var7 - var1.z) * 0.5
         );
      } else {
         return var1;
      }
   }

   @EventTarget
   public void method1772(DrawEvent var1) {
      if (mc.player != null && mc.world != null && !mc.getDebugHud().shouldShowDebugHud() && this.entry5.isSelected() && this.entry6.isEnabled()) {
         String var2 = "Pitch: " + (int)mc.player.getPitch();
         int var3 = (mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(var2)) / 2;
         int var4 = mc.getWindow().getScaledHeight() / 2 + 25;
         var1.getCustomDrawContext().drawText(mc.textRenderer, var2, var3, var4, -1, true);
      }
   }

   private float method1773(float var1, boolean var2) {
      int var3 = MathHelper.clamp(Math.round(method1778(var1)), 0, 55);
      return !this.customAcceleration.isEnabled()
         ? this.sliderSettings3[var3].getValue3()
         : (var2 ? this.sliderSettings4[var3] : this.sliderSettings5[var3]).getValue3();
   }

   private static float method1774(float[] var0, float var1) {
      int var2 = MathHelper.clamp((int)(method1779(var1) / 5.0F), 0, var0.length - 1);
      return var0[var2];
   }

   private float method1775(float var1) {
      float var2;
      if ((var2 = Math.abs(var1)) >= 37.0F && var2 <= 38.0F) {
         return 2.03F;
      } else if (var2 >= 25.0F && var2 <= 30.0F) {
         return 2.0F;
      } else if (var2 >= 35.0F && var2 <= 45.0F) {
         return 1.99F;
      } else if (var2 >= 40.0F && var2 <= 50.0F) {
         return 1.97F;
      } else if (var2 >= 50.0F && var2 <= 60.0F) {
         return 1.96F;
      } else if (var2 >= 51.0F && var2 <= 61.0F) {
         return 1.85F;
      } else {
         return var2 >= 52.0F && var2 <= 65.0F ? 1.8F : 1.59F;
      }
   }

   private float method1776(float var1, float var2) {
      float var3 = Math.abs(var1);
      float var4 = Math.abs(MathHelper.wrapDegrees(var2) % 90.0F);
      float var5;
      if (var3 >= 38.0F && var3 <= 52.0F) {
         var5 = 2.0F;
      } else if (var3 >= 32.0F && var3 <= 58.0F) {
         var5 = 1.96F;
      } else if (var3 >= 28.0F && var3 <= 62.0F) {
         var5 = 1.95F;
      } else {
         float var8 = 61.0F;
         float var7 = 29.0F;
         if (!(var4 >= var7) || !(var4 <= var8)) {
            var8 = 61.0F;
            var7 = 29.0F;
            if (!(var3 >= var7) || !(var3 <= var8)) {
               var8 = 60.0F;
               var7 = 28.0F;
               if (!(var4 >= var7) || !(var4 <= var8)) {
                  var8 = 60.0F;
                  var7 = 28.0F;
                  if (!(var3 >= var7) || !(var3 <= var8)) {
                     var8 = 64.0F;
                     var7 = 26.0F;
                     if (!(var4 >= var7) || !(var4 <= var8)) {
                        var8 = 64.0F;
                        var7 = 26.0F;
                        if (!(var3 >= var7) || !(var3 <= var8)) {
                           var8 = 66.0F;
                           var7 = 24.0F;
                           if (!(var4 >= var7) || !(var4 <= var8)) {
                              var8 = 66.0F;
                              var7 = 24.0F;
                              if (!(var3 >= var7) || !(var3 <= var8)) {
                                 var8 = 75.0F;
                                 var7 = 15.0F;
                                 if (!(var4 >= var7) || !(var4 <= var8)) {
                                    var8 = 75.0F;
                                    var7 = 15.0F;
                                    if (!(var3 >= var7) || !(var3 <= var8)) {
                                       var8 = 77.0F;
                                       var7 = 13.0F;
                                       if (!(var4 >= var7) || !(var4 <= var8)) {
                                          var8 = 77.0F;
                                          var7 = 13.0F;
                                          if (!(var3 >= var7) || !(var3 <= var8)) {
                                             var8 = 78.0F;
                                             var7 = 12.0F;
                                             if (!(var4 >= var7) || !(var4 <= var8)) {
                                                var8 = 78.0F;
                                                var7 = 12.0F;
                                                if (!(var3 >= var7) || !(var3 <= var8)) {
                                                   var8 = 82.0F;
                                                   var7 = 8.0F;
                                                   if (!(var4 >= var7) || !(var4 <= var8)) {
                                                      var8 = 79.0F;
                                                      var7 = 11.0F;
                                                      if (!(var3 >= var7) || !(var3 <= var8)) {
                                                         var8 = 85.0F;
                                                         var7 = 5.0F;
                                                         if (!(var4 >= var7) || !(var4 <= var8)) {
                                                            var8 = 82.0F;
                                                            var7 = 8.0F;
                                                            if (!(var3 >= var7) || !(var3 <= var8)) {
                                                               var5 = !(var4 <= 90.0F) && !(var3 <= 90.0F) ? 1.66F : 1.67F;
                                                               return var1 > 15.0F ? var5 - 0.068F : var5;
                                                            }
                                                         }

                                                         var5 = 1.67F;
                                                         return var1 > 15.0F ? var5 - 0.068F : var5;
                                                      }
                                                   }

                                                   var5 = 1.75F;
                                                   return var1 > 15.0F ? var5 - 0.068F : var5;
                                                }
                                             }

                                             var5 = 1.75F;
                                             return var1 > 15.0F ? var5 - 0.068F : var5;
                                          }
                                       }

                                       var5 = 1.75F;
                                       return var1 > 15.0F ? var5 - 0.068F : var5;
                                    }
                                 }

                                 var5 = 1.75F;
                                 return var1 > 15.0F ? var5 - 0.068F : var5;
                              }
                           }

                           var5 = 1.75F;
                           return var1 > 15.0F ? var5 - 0.068F : var5;
                        }
                     }

                     var5 = 1.874F;
                     return var1 > 15.0F ? var5 - 0.068F : var5;
                  }
               }

               var5 = 1.954F;
               return var1 > 15.0F ? var5 - 0.068F : var5;
            }
         }

         var5 = 1.963F;
      }

      return var1 > 15.0F ? var5 - 0.068F : var5;
   }

   private float method1777(float var1, SliderSetting[] var2) {
      int var3 = MathHelper.clamp((int)(method1779(var1) / 5.0F), 0, var2.length - 1);
      return var2[var3].getValue3();
   }

   private static float method1778(float var0) {
      float var1;
      return (var1 = Math.abs(var0)) > 90.0F ? 180.0F - var1 : var1;
   }

   private static float method1779(float var0) {
      float var1;
      return (var1 = method1778(var0)) > 45.0F ? 90.0F - var1 : var1;
   }

   private static boolean method1780(float var0, float var1, float var2) {
      return var0 >= var1 && var0 <= var2;
   }

   private static SliderSetting[] method1781(String var0, int var1, float var2, Supplier<Boolean> var3) {
      SliderSetting[] var4 = new SliderSetting[var1];

      for (int var5 = 0; var5 < var1; var5++) {
         var4[var5] = new SliderSetting(var0 + "." + var5 * 5 + "-" + (var5 + 1) * 5, var2 + var5 * 0.01F, 1.5F, 3.0F, 0.01F, var3);
      }

      return var4;
   }

   private static SliderSetting[] method1782(String var0, Supplier<Boolean> var1) {
      SliderSetting[] var2 = new SliderSetting[56];

      for (int var3 = 0; var3 < 56; var3++) {
         float var4 = 1.5F + var3 / 55.0F * 0.11F;
         var2[var3] = new SliderSetting(var0 + "." + var3, var4, 1.5F, 3.0F, 0.01F, var1);
      }

      return var2;
   }
}

package org.zenith.client.modules.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "SwingAnimation",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.swinganimation.description"
)
public final class SwingAnimationModule extends Module {
   public static final SwingAnimationModule INSTANCE = new SwingAnimationModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 3;
   private static final int CONST_intValue5 = 4;
   private static final int CONST_intValue6 = 5;
   private static final int CONST_intValue7 = 6;
   private static final int CONST_intValue8 = 7;
   private static final int CONST_intValue9 = 8;
   public ModeSetting animationMode = new ModeSetting(
      "module.swingAnimation.animationMode",
      "module.swingAnimation.animationMode.desc",
      "module.swingAnimation.normal",
      "module.swingAnimation.first",
      "module.swingAnimation.second",
      "module.swingAnimation.third",
      "module.swingAnimation.fourth",
      "module.swingAnimation.fifth",
      "module.swingAnimation.sixth",
      "module.swingAnimation.orbit",
      "module.swingAnimation.recoil"
   );
   public final SliderSetting duration = new SliderSetting("module.swingAnimation.duration", 2.0F, 1.0F, 5.0F, 0.5F, "module.swingAnimation.duration.desc", "x");
   public final BooleanSetting onlyAura = new BooleanSetting("module.swingAnimation.onlyAura", "module.swingAnimation.onlyAura.desc", false);

   private SwingAnimationModule() {
   }

   public boolean active() {
      return !this.isEnabled() ? false : !this.onlyAura.isEnabled() || AuraModule.INSTANCE.isEnabled() && AuraModule.INSTANCE.method6358() != null;
   }

   public int method3269(int var1) {
      float var2 = this.duration.getValue3();
      if (!Float.isFinite(var2)) {
         var2 = 2.0F;
      }

      return Math.max(1, (int)(var1 * MathHelper.clamp(var2, 1.0F, 5.0F)));
   }

   public void method3270(MatrixStack var1, float var2, float var3, Arm var4) {
      int var5 = var4 == Arm.RIGHT ? 1 : -1;
      int var6 = MathHelper.clamp(this.animationMode.getIndex(), 0, 8);
      float var7 = MathHelper.clamp(var2, 0.0F, 1.0F);
      float var8 = MathHelper.sin(MathHelper.sqrt(var7) * (float) Math.PI);
      float var9 = MathHelper.sin(var7 * (float) Math.PI);
      if (var6 <= 6) {
         var1.translate(-var5 * 0.56F, 0.52F + var3 * 0.6F, 0.72F);
      }

      switch (var6) {
         case 1:
            if (var7 > 0.0F) {
               var1.translate(var5 * 0.56F, var3 * -0.2F - 0.5F, -0.7F);
               var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var5 * 45.0F));
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var8 * -85.0F));
               var1.translate(var5 * -0.1F, 0.28F, 0.2F);
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85.0F));
            } else {
               method3271(var1, var5, var3);
               method3272(var1, var7, var5);
            }
            break;
         case 2:
            method3271(var1, var5, 0.0F);
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0F));
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var5 * -60.0F));
            var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var5 * (110.0F + 20.0F * var8)));
            break;
         case 3:
            method3271(var1, var5, 0.0F);
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0F));
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var5 * (-30.0F * (1.0F - var8) - 30.0F)));
            var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var5 * 110.0F));
            break;
         case 4:
            method3271(var1, var5, 0.0F);
            var1.translate(var5 * 0.1F, -0.2F, -0.3F);
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0F * var9 - 36.0F));
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var5 * 25.0F * var9));
            var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var5 * 12.0F));
            break;
         case 5:
            method3271(var1, var5, 0.0F);
            var1.translate(0.0F, -0.2F, -0.4F);
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-120.0F * var8 - 3.0F));
            break;
         case 6:
            method3271(var1, var5, var3);
            var1.translate(0.0F, 0.12F * var9, 0.0F);
            method3272(var1, var7, var5);
            break;
         case 7:
            float var10 = MathHelper.sin(var7 * (float) Math.PI * 2.0F) * var9;
            method3273(var1, var5, 0.0F, 0.2F, -0.1F, -0.16F * var10, 0.1F * var9, -0.12F * var9, -35.0F * var9, 55.0F * var10, -45.0F * var10);
            break;
         case 8:
            float var11 = MathHelper.sin(var7 * (float) Math.PI * 3.0F) * var9 * (1.0F - var7);
            method3273(var1, var5, 0.0F, -0.1F, 0.05F, -0.05F * var8, 0.06F * var8, -0.22F * var8, -65.0F * var8 + 12.0F * var11, 10.0F * var11, 15.0F * var8);
            break;
         default:
            method3271(var1, var5, 0.0F);
            method3272(var1, var7, var5);
      }
   }

   private static void method3271(MatrixStack var0, int var1, float var2) {
      var0.translate(var1 * 0.56F, -0.52F - var2 * 0.6F, -0.72F);
   }

   private static void method3272(MatrixStack var0, float var1, int var2) {
      float var3 = MathHelper.sin(var1 * var1 * (float) Math.PI);
      float var4 = MathHelper.sin(MathHelper.sqrt(var1) * (float) Math.PI);
      var0.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var2 * (45.0F - var3 * 20.0F)));
      var0.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var2 * var4 * -20.0F));
      var0.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var4 * -80.0F));
      var0.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var2 * -45.0F));
   }

   private static void method3273(
      MatrixStack var0, int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10
   ) {
      var0.translate(var1 * var2, var3, var4);
      var0.translate(var1 * var5, var6, var7);
      var0.multiply(
         new Quaternionf().rotationXYZ(var8 * (float) (Math.PI / 180.0), var1 * var9 * (float) (Math.PI / 180.0), var1 * var10 * (float) (Math.PI / 180.0))
      );
      var0.translate(-var1 * var2, -var3, -var4);
   }
}

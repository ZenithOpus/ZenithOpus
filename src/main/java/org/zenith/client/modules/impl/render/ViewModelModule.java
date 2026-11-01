package org.zenith.client.modules.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "ViewModel",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.viewmodel.description"
)
public final class ViewModelModule extends Module {
   public static final ViewModelModule INSTANCE = new ViewModelModule();
   public final GroupSetting rightArmZ = new GroupSetting(
      "module.viewModel.rightArm",
      "module.viewModel.rightArm.desc",
      () -> true,
      new SliderSetting("module.viewModel.rightArmX", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.rightArmX.desc", "b"),
      new SliderSetting("module.viewModel.rightArmY", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.rightArmY.desc", "b"),
      new SliderSetting("module.viewModel.rightArmZ", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.rightArmZ.desc", "b")
   );
   public final GroupSetting leftArmZ = new GroupSetting(
      "module.viewModel.leftArm",
      "module.viewModel.leftArm.desc",
      () -> true,
      new SliderSetting("module.viewModel.leftArmX", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.leftArmX.desc", "b"),
      new SliderSetting("module.viewModel.leftArmY", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.leftArmY.desc", "b"),
      new SliderSetting("module.viewModel.leftArmZ", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.leftArmZ.desc", "b")
   );
   public final GroupSetting leftArmSize = new GroupSetting(
      "module.viewModel.sizeList",
      "module.viewModel.sizeList.desc",
      () -> true,
      new SliderSetting("module.viewModel.rightArmSize", 1.0F, 0.5F, 1.5F, 0.05F, "module.viewModel.rightArmSize.desc", "x"),
      new SliderSetting("module.viewModel.leftArmSize", 1.0F, 0.5F, 1.5F, 0.05F, "module.viewModel.leftArmSize.desc", "x")
   );
   public final ButtonSetting reset = new ButtonSetting("module.viewModel.reset", "W", () -> {
      SliderSetting var1 = this.leftArmSize.method2786(0);
      SliderSetting var2 = this.leftArmSize.method2786(1);
      SliderSetting var3 = this.rightArmZ.method2786(0);
      SliderSetting var4 = this.rightArmZ.method2786(1);
      SliderSetting var5 = this.rightArmZ.method2786(2);
      SliderSetting var6 = this.leftArmZ.method2786(0);
      SliderSetting var7 = this.leftArmZ.method2786(1);
      SliderSetting var8 = this.leftArmZ.method2786(2);
      var1.method2400(1.0F);
      var2.method2400(1.0F);
      var3.method2400(0.0F);
      var4.method2400(0.0F);
      var5.method2400(0.0F);
      var6.method2400(0.0F);
      var7.method2400(0.0F);
      var8.method2400(0.0F);
   });

   private ViewModelModule() {
   }

   public void method235(MatrixStack var1, Arm var2) {
      if (this.isEnabled()) {
         if (var2 == Arm.RIGHT) {
            SliderSetting var3 = this.leftArmSize.method2786(0);
            var1.scale(var3.getValue3(), var3.getValue3(), var3.getValue3());
         } else {
            SliderSetting var4 = this.leftArmSize.method2786(1);
            var1.scale(var4.getValue3(), var4.getValue3(), var4.getValue3());
         }
      } else {
         var1.scale(1.0F, 1.0F, 1.0F);
      }
   }

   public void method236(MatrixStack var1, Arm var2) {
      if (this.isEnabled()) {
         if (var2 == Arm.RIGHT) {
            SliderSetting var3 = this.rightArmZ.method2786(0);
            SliderSetting var4 = this.rightArmZ.method2786(1);
            SliderSetting var5 = this.rightArmZ.method2786(2);
            var1.translate(var3.getValue3(), var4.getValue3(), var5.getValue3());
         } else {
            SliderSetting var6 = this.leftArmZ.method2786(0);
            SliderSetting var7 = this.leftArmZ.method2786(1);
            SliderSetting var8 = this.leftArmZ.method2786(2);
            var1.translate(-var6.getValue3(), var7.getValue3(), var8.getValue3());
         }
      } else {
         var1.translate(0.0F, 0.0F, 0.0F);
      }
   }
}

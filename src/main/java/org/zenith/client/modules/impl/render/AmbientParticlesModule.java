package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorProvider;
import org.zenith.utility.misc.Util12;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.MatrixStackUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Ambient Particles",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.ambientparticles.description"
)
public final class AmbientParticlesModule extends Module {
   public static final AmbientParticlesModule INSTANCE = new AmbientParticlesModule();
   private static final float CONST_floatValue = 3.0F;
   private static final float CONST_floatValue2 = 1.15F;
   private static final double CONST_doubleValue = 4.0;
   private static final float CONST_floatValue3 = 900.0F;
   private final SliderSetting strands = new SliderSetting(
      "module.ambientParticles.strands", 80.0F, 4.0F, 160.0F, 2.0F, "module.ambientParticles.strands.desc", "", () -> true, null
   );
   private final SliderSetting radius = new SliderSetting(
      "module.ambientParticles.radius", 25.0F, 6.0F, 64.0F, 1.0F, "module.ambientParticles.radius.desc", "b", () -> true, null
   );
   private final ModeSetting color = new ModeSetting(
      "module.ambientParticles.color", "module.ambientParticles.colorMode.desc", () -> true, "module.particles.sync", "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.ambientParticles.customColor", "module.ambientParticles.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(1)
   );
   private final Glowboost glowboost = new Glowboost();
   private final ColorProvider colorProvider = this::method2395;
   private static final long CONST_longValue = System.currentTimeMillis();
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private long longValue;
   private boolean flag;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(this.strands, this.radius, this.color, this.customColor);
   }

   private AmbientParticlesModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.longValue2 = System.currentTimeMillis();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.flag = false;
   }

   private float method2390() {
      return MathHelper.clamp((float)(System.currentTimeMillis() - this.longValue2) / 900.0F, 0.0F, 1.0F);
   }

   @EventTarget
   public void method2391(WorldRenderEvent var1) {
      if (mc.player != null && mc.world != null) {
         Camera var2 = mc.getEntityRenderDispatcher().camera;
         Vec3d var3 = mc.player.getEntityPos();
         this.method2393(var3);
         this.glowboost.method3232(var2, 1.15F);
         Util12.method2340(
            this.glowboost,
            var3.x,
            var3.y + 1.0,
            var3.z,
            this.doubleValue,
            this.doubleValue2,
            this.doubleValue3,
            method2394(),
            (int)this.strands.getValue3(),
            this.radius.getValue3(),
            this.method2390(),
            this.colorProvider
         );
         this.glowboost.method3253(var1.getMatrixStack());
      }
   }

   public void method2392(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      MatrixStack var6 = MatrixStackUtils.method5467(var1, var2, var3, var4);
      this.glowboost.method3233(MatrixStackUtils.method5468(), MatrixStackUtils.method5469(), 1.15F * var5);
      Util12.method2340(this.glowboost, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, method2394(), (int)this.strands.getValue3(), 3.0F, 1.0F, this.colorProvider);
      this.glowboost.method3253(var6);
   }

   private void method2393(Vec3d var1) {
      long var2 = System.currentTimeMillis();
      if (!this.flag) {
         this.doubleValue = var1.x;
         this.doubleValue2 = var1.y + 1.0;
         this.doubleValue3 = var1.z;
         this.longValue = var2;
         this.flag = true;
      } else {
         double var4 = Math.min(0.25, (var2 - this.longValue) / 1000.0);
         this.longValue = var2;
         double var6 = 1.0 - Math.exp(-4.0 * var4);
         this.doubleValue = this.doubleValue + (var1.x - this.doubleValue) * var6;
         this.doubleValue2 = this.doubleValue2 + (var1.y + 1.0 - this.doubleValue2) * var6;
         this.doubleValue3 = this.doubleValue3 + (var1.z - this.doubleValue3) * var6;
      }
   }

   private static float method2394() {
      return (float)(System.currentTimeMillis() - CONST_longValue) / 1000.0F;
   }

   private int method2395(int var1) {
      return this.color.isSelected(1)
         ? this.customColor.getZenithColor().method7287()
         : Zenith.getInstance().getStyleManager().getClientColor(var1).method7287();
   }
}

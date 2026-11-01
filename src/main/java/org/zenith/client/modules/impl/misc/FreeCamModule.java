package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.CameraPosEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "FreeCam",
   description = "module.freecam.description",
   category = ModuleCategory.MISC
)
public final class FreeCamModule extends Module {
   public static final FreeCamModule INSTANCE = new FreeCamModule();
   private final SliderSetting speedSetting = new SliderSetting("module.freeCam.speedSetting", 2.0F, 0.5F, 5.0F, 0.5F, "module.freeCam.speedSetting.desc", "x");
   public Vec3d vec3d;
   public Vec3d vec3d2;

   public Vec3d method1660() {
      return this.isEnabled() && this.vec3d != null ? this.vec3d : mc.getCameraEntity().getEyePos();
   }

   private FreeCamModule() {
   }

   @Override
   public void onEnable() {
      Camera var1 = mc.getEntityRenderDispatcher() == null ? null : mc.getEntityRenderDispatcher().camera;
      this.vec3d2 = this.vec3d = var1 != null && mc.world != null ? new Vec3d(var1.getCameraPos().toVector3f()) : null;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.options.setPerspective(Perspective.FIRST_PERSON);
      super.onDisable();
   }

   @EventTarget
   public void method1661(WorldRenderEvent var1) {
      GlowUtil.method2230(
         mc.player.getBoundingBox().offset(MathUtils.method2656(mc.player).subtract(mc.player.getEntityPos())),
         zenith.getStyleManager().getClientColor(90).method7287(),
         1.0F
      );
   }

   @EventTarget
   public void method1662(DrawEvent var1) {
      if (this.vec3d != null) {
         String var2 = MathUtils.round((float)this.vec3d.x) + "   " + MathUtils.round((float)this.vec3d.y) + "   " + MathUtils.round((float)this.vec3d.z);
         float var3 = Fonts.MEDIUM.getWidth(var2, 7.0F);
         var1.getCustomDrawContext()
            .drawText(
               Fonts.MEDIUM.getFont(7.0F),
               var2,
               mc.getWindow().getScaledWidth() / 2.0F - var3 / 2.0F,
               10.0F,
               Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor()
            );
      }
   }

   @EventTarget
   public void method1663(MoveInputEvent var1) {
      if (this.vec3d == null) {
         Entity var2 = mc.getCameraEntity();
         if (var2 == null) {
            var1.method3044();
            return;
         }

         this.vec3d2 = this.vec3d = var2.getEyePos();
      }

      float var4 = this.speedSetting.getValue3();
      double[] var3 = MovementInputUtils.method7421(var4);
      this.vec3d2 = this.vec3d;
      this.vec3d = this.vec3d.add(var3[0], var1.getPlayerInput().jump() ? var4 : (var1.getPlayerInput().sneak() ? -var4 : 0.0), var3[1]);
      var1.method3044();
   }

   @EventTarget
   public void method1664(CameraPosEvent var1) {
      if (this.vec3d == null && var1.getVec3d() != null) {
         this.vec3d2 = this.vec3d = var1.getVec3d();
      }

      if (this.vec3d2 != null && this.vec3d != null) {
         var1.setVec3d(MathUtils.method2655(this.vec3d2, this.vec3d));
         mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
      }
   }
}

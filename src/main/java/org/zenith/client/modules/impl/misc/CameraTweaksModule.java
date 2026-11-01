package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.CameraUpdateEvent;
import org.zenith.client.events.FovEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.ProjectionMatrixEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "CameraTweaks",
   description = "module.cameratweaks.description",
   category = ModuleCategory.MISC
)
public final class CameraTweaksModule extends Module {
   public static final CameraTweaksModule INSTANCE = new CameraTweaksModule();
   private float floatValue = 110.0F;
   private float floatValue2 = 30.0F;
   private float floatValue3 = 30.0F;
   private Perspective perspective;
   private Rotation rotation;
   private final MultiSelectSetting multiSetting = MultiSelectSetting.method5224(
      "module.cameraTweaks.multiSetting",
      "module.cameraTweaks.multiSetting.desc",
      List.of("module.cameraTweaks.ratio", "module.cameraTweaks.clip", "module.cameraTweaks.distance")
   );
   private final SliderSetting ratioSetting = new SliderSetting(
      "module.cameraTweaks.ratioSetting", 1.0F, 0.1F, 2.0F, 0.1F, "module.cameraTweaks.ratioSetting.desc", "x", () -> this.multiSetting.method5227(0), null
   );
   private final SliderSetting distanceSetting = new SliderSetting(
      "module.cameraTweaks.distanceSetting",
      3.0F,
      2.0F,
      5.0F,
      0.5F,
      "module.cameraTweaks.distanceSetting.desc",
      "b",
      () -> this.multiSetting.method5227(2),
      null
   );
   private final BindSetting zoomSetting = new BindSetting("module.cameraTweaks.zoomSetting", "module.cameraTweaks.zoomSetting.desc");
   private final BindSetting freeLookSetting = new BindSetting("module.cameraTweaks.freeLookSetting", "module.cameraTweaks.freeLookSetting.desc");

   private CameraTweaksModule() {
   }

   @EventTarget
   public void method2927(KeyEvent var1) {
      if (mc.currentScreen == null) {
         if (var1.isPress(this.zoomSetting.getKeyCode())) {
            this.floatValue = Math.min(this.floatValue3, (float)(mc.options.getFov().getValue() - 20));
         }

         if (var1.isRelease(this.zoomSetting.getKeyCode(), true)) {
            this.floatValue3 = this.floatValue;
            this.floatValue = mc.options.getFov().getValue().intValue();
         }

         if (var1.isPress(this.freeLookSetting.getKeyCode())) {
            this.perspective = mc.options.getPerspective();
         }
      }
   }

   @EventTarget
   public void method2928(MouseScrollEvent var1) {
      if (UseItemUtils.method6521(this.zoomSetting)) {
         this.floatValue = (int)MathHelper.clamp(this.floatValue - var1.getDoubleValue2() * 10.0, 10.0, (double)mc.options.getFov().getValue().intValue());
         var1.setCancelled(true);
      }
   }

   @EventTarget
   public void method2929(FovEvent var1) {
      if (UseItemUtils.method6521(this.freeLookSetting)) {
         if (mc.options.getPerspective().isFirstPerson()) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
         }
      } else if (this.perspective != null) {
         mc.options.setPerspective(this.perspective);
         this.perspective = null;
      }

      if (this.zoomSetting.isVisible()) {
         var1.setIntValue(
            (int)MathHelper.clamp(
               (this.floatValue2 = MathUtils.method2660(1.6, this.floatValue2, this.floatValue)) + 1.0F,
               10.0F,
               (float)mc.options.getFov().getValue().intValue()
            )
         );
         var1.cancel();
      }
   }

   @EventTarget
   public void method2930(MouseRotationEvent var1) {
      if (UseItemUtils.method6521(this.freeLookSetting)) {
         Rotation var2 = this.rotation != null ? this.rotation : new Rotation(mc.player.getYaw(), mc.player.getPitch());
         this.rotation = new Rotation(
            (float)(var2.getFloatValue() + var1.getDoubleValue() * 0.15F),
            (float)MathHelper.clamp(var2.getFloatValue2() + var1.getDoubleValue2() * 0.15F, -90.0, 90.0)
         );
         var1.setCancelled(true);
      } else {
         this.rotation = null;
      }
   }

   @EventTarget
   public void method2931(CameraUpdateEvent var1) {
      var1.setFlag(this.multiSetting.method5227(1));
      if (this.multiSetting.method5227(2)) {
         var1.setFloatValue(this.distanceSetting.getValue3());
      }

      if (this.rotation != null) {
         var1.setRotation(this.rotation);
      }

      if (this.multiSetting.method5227(1) || this.multiSetting.method5227(2) || this.rotation != null) {
         var1.cancel();
      }
   }

   @EventTarget
   public void method2932(ProjectionMatrixEvent var1) {
      if (this.multiSetting.method5227(0)) {
         var1.setFloatValue(this.ratioSetting.getValue3());
         var1.setCancelled(true);
      }
   }
}

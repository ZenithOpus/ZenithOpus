package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Objects;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;

@ModuleInfo(
   name = "NoDelay",
   description = "module.nodelay.description",
   category = ModuleCategory.MOVEMENT
)
public final class NoDelayModule extends Module {
   private final MultiSelectSetting ignoreSetting = MultiSelectSetting.method5224(
      "module.noDelay.ignoreSetting",
      "module.noDelay.ignoreSetting.desc",
      List.of("module.noDelay.ignoreSetting.jump", "module.noDelay.ignoreSetting.use", "module.noDelay.ignoreSetting.break")
   );
   public static final NoDelayModule INSTANCE = new NoDelayModule();

   private NoDelayModule() {
   }

   @EventTarget
   public void method4845(UpdateEvent var1) {
      if (this.ignoreSetting.method5227(2)) {
         mc.interactionManager.blockBreakingCooldown = 0;
      }

      if (this.ignoreSetting.method5227(0)) {
         Objects.requireNonNull(mc.player).jumpingCooldown = 0;
      }

      if (this.ignoreSetting.method5227(1)) {
         mc.itemUseCooldown = 0;
      }
   }
}

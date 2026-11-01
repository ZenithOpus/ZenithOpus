package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "TapeMouse",
   category = ModuleCategory.MISC,
   description = "module.tapemouse.description"
)
public final class TapeMouseModule extends Module {
   public static final TapeMouseModule INSTANCE = new TapeMouseModule();
   private final ModeSetting mouseButton = new ModeSetting(
      "module.tapeMouse.mouseButton", "module.tapeMouse.mouseButton.desc", "module.tapeMouse.right", "module.tapeMouse.left"
   );
   private final SliderSetting delay = new SliderSetting("module.tapeMouse.delay", 100.0F, 0.0F, 1000.0F, 10.0F, "module.tapeMouse.delay.desc", "ms");
   private final StopWatch stopWatch = new StopWatch();

   private TapeMouseModule() {
   }

   @Override
   public void onDisable() {
      this.method2780();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (!this.stopWatch.method7546((long)this.delay.getValue3())) {
            this.method2780();
         } else {
            this.method2780();
            this.method2779(this.method2778());
            this.stopWatch.reset();
         }
      } else {
         this.method2780();
      }
   }

   private KeyBinding method2778() {
      return this.mouseButton.isSelected(0) ? mc.options.useKey : mc.options.attackKey;
   }

   private void method2779(KeyBinding var1) {
      var1.setPressed(true);
      KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(var1));
   }

   private void method2780() {
      mc.options.useKey.setPressed(false);
      mc.options.attackKey.setPressed(false);
   }
}

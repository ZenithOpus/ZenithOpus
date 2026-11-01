package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.screens.bot.BotScreen;
import org.zenith.client.settings.BindSetting;

@ModuleInfo(
   name = "Bot",
   category = ModuleCategory.PLAYER,
   description = "module.bot.description",
   method894 = true
)
public final class BotModule extends Module {
   public static final BotModule INSTANCE = new BotModule();
   private final BindSetting openGui = new BindSetting("module.bot.openGui", 345);

   private void method4084() {
      if (!(mc.currentScreen instanceof BotScreen)) {
         mc.setScreen(new BotScreen());
         Zenith.getInstance().getClientSounds().method1561(Zenith.getInstance().getClientSounds().soundEvent);
      }
   }

   @EventTarget
   public void method4085(KeyEvent var1) {
      if (var1.isPress(this.openGui.getKeyCode())) {
         this.method4084();
      }
   }

   @EventTarget(3)
   public void method4086(ScreenRenderEvent var1) {
      if (mc.currentScreen instanceof BotScreen var2) {
         var2.renderTop(var1.getMenuDrawContext(), var1.getMenuDrawContext().getMouseX(), var1.getMenuDrawContext().getMouseY());
      }
   }
}

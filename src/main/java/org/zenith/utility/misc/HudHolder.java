package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.text.Text;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.hud.InterfaceHud;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.utility.network.UseItemUtils;

public class HudHolder {
   private static volatile HudHolder INSTANCE;
   private InterfaceHud interfaceHud;

   public HudHolder() {
      INSTANCE = this;
      EventManager.register(this);
   }

   public static HudHolder method6969() {
      Zenith var0 = Zenith.getInstance();
      HudHolder var1 = var0 == null ? null : var0.getHudHolder();
      if (var1 != null) {
         return var1;
      } else {
         HudHolder var2 = INSTANCE;
         if (var2 == null) {
            synchronized (HudHolder.class) {
               var2 = INSTANCE;
               if (var2 == null) {
                  var2 = new HudHolder();
               }
            }
         }

         return var2;
      }
   }

   public void setInterfaceHud(InterfaceHud var1) {
      this.interfaceHud = var1;
   }

   @EventTarget
   public void method6970(ModuleToggleEvent var1) {
      if (var1.getModule() != MenuModule.INSTANCE && !UseItemUtils.method6526()) {
         try {
            Zenith.getInstance().getClientSounds().method1565(var1.isEnabled());
         } catch (Exception var3) {
            var3.printStackTrace();
         }

         if (this.interfaceHud != null && this.interfaceHud.method5083() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814()) {
            this.interfaceHud.method5084(var1.getModule(), var1.isEnabled());
         }
      }
   }

   public void method6971(Module var1, boolean var2) {
      if (this.interfaceHud != null
         && this.interfaceHud.method5083()
         && !UseItemUtils.method6526()
         && InterfaceModule.INSTANCE.isEnabled()
         && InterfaceModule.INSTANCE.method6814()) {
         this.interfaceHud.method5084(var1, var2);
      }
   }

   public void method6972(String var1, Text var2) {
      if (this.interfaceHud != null && !UseItemUtils.method6526() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814()) {
         this.interfaceHud.method5086(var1, var2);
      }
   }

   public void method6973(String var1, Text var2, long var3) {
      if (this.interfaceHud != null && !UseItemUtils.method6526() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814()) {
         this.interfaceHud.method5087(var1, var2, var3);
      }
   }
}

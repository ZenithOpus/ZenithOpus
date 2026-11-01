package org.zenith.utility.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.PlayerScreenHandler;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.client.screens.bot.BotScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

public final class KeyBindUtils implements MinecraftAccess {
   public static final List<KeyBinding> items = List.of(mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey);
   private static final Map<Class<?>, TaskScheduler.Entry2> items2 = new HashMap<>();

   public static void method2627(Class<?> var0, Runnable var1) {
      method2628(var0, var1, 0);
   }

   public static void method2628(Class<?> var0, Runnable var1, int var2) {
      TaskScheduler var3 = Zenith.getInstance().getTaskScheduler();
      TaskScheduler.Entry2 var4 = var3.method5109(var0)
         .method5121(
            MoveInputEvent.class,
            var1x -> {
               if (InventorySettingModule.INSTANCE.method2397()) {
                  var1x.method3044();
                  if (mc.player.lastPlayerInput.jump()
                     || mc.player.isSprinting()
                     || mc.player.lastPlayerInput.forward()
                     || mc.player.lastPlayerInput.backward()
                     || mc.player.lastPlayerInput.left()
                     || mc.player.lastPlayerInput.right()) {
                     return false;
                  }
               }

               var1.run();
               InventoryUtils.closeScreen();
               return true;
            },
            var2
         );
      var3.method5111(var4, var2);
   }

   public static boolean method2629(Class<?> var0) {
      return Zenith.getInstance().getTaskScheduler().method5117(var0);
   }

   public static boolean method2630(Class<?> var0) {
      return var0 == AutoTotemModule.class ? !AutoTotemModule.INSTANCE.method725() : method2629(var0);
   }

   public static void method2631() {
      method2633();
   }

   public static void method2632() {
      method2634();
   }

   public static void method2633() {
      items.forEach(var0 -> var0.setPressed(false));
   }

   public static void method2634() {
      items.forEach(var0 -> var0.setPressed(InputUtil.isKeyPressed(mc.getWindow(), var0.getDefaultKey().getCode())));
   }

   public static boolean method2635() {
      if (mc.currentScreen == null) {
         return false;
      } else if (!Zenith.getInstance().getTaskScheduler().method5116()) {
         return false;
      } else if (UseItemUtils.method6525(mc.currentScreen)) {
         return false;
      } else if (mc.currentScreen instanceof SignEditScreen) {
         return false;
      } else if (mc.currentScreen instanceof AnvilScreen) {
         return false;
      } else if (mc.currentScreen instanceof AbstractCommandBlockScreen) {
         return false;
      } else if (mc.currentScreen instanceof StructureBlockScreen) {
         return false;
      } else if (mc.currentScreen instanceof BotScreen var2) {
         return !var2.isSearch();
      } else {
         return mc.currentScreen instanceof NLMenuScreen var0 ? !var0.isSearch() : mc.player.currentScreenHandler instanceof PlayerScreenHandler;
      }
   }

   @Generated
   private KeyBindUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

package org.zenith.utility.misc;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.utils.IInputOverrideHandler;
import baritone.api.utils.input.Input;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.option.KeyBinding;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.UpdateEvent;

public final class SettingsBackup implements MinecraftAccess {
   private static final Input[] inputs = new Input[]{
      Input.MOVE_FORWARD, Input.MOVE_BACK, Input.MOVE_LEFT, Input.MOVE_RIGHT, Input.JUMP, Input.SNEAK, Input.SPRINT
   };
   private static Boolean booleanValue;
   private static Boolean booleanValue2;
   private static Boolean booleanValue3;
   private static Integer integer;
   private static Boolean booleanValue4;
   private static Double doubleValue;
   private static Double doubleValue2;
   private static boolean flag;

   private SettingsBackup() {
   }

   public static void apply() {
      Settings var0 = BaritoneAPI.getSettings();
      if (var0 != null) {
         if (booleanValue == null) {
            booleanValue = (Boolean)var0.allowBreak.value;
            booleanValue2 = (Boolean)var0.allowPlace.value;
            booleanValue3 = (Boolean)var0.allowParkourPlace.value;
            integer = (Integer)var0.maxFallHeightNoWater.value;
            booleanValue4 = (Boolean)var0.remainWithExistingLookDirection.value;
            doubleValue = (Double)var0.randomLooking.value;
            doubleValue2 = (Double)var0.randomLooking113.value;
         }

         var0.allowBreak.value = Boolean.FALSE;
         var0.allowPlace.value = Boolean.FALSE;
         var0.allowParkourPlace.value = Boolean.FALSE;
         var0.maxFallHeightNoWater.value = 6;
         var0.remainWithExistingLookDirection.value = Boolean.FALSE;
         var0.randomLooking.value = 0.6;
         var0.randomLooking113.value = 0.0;
      }
   }

   public static void restore() {
      Settings var0 = BaritoneAPI.getSettings();
      if (var0 != null && booleanValue != null) {
         var0.allowBreak.value = booleanValue;
         var0.allowPlace.value = booleanValue2;
         var0.allowParkourPlace.value = booleanValue3;
         var0.maxFallHeightNoWater.value = integer;
         var0.remainWithExistingLookDirection.value = booleanValue4;
         var0.randomLooking.value = doubleValue;
         var0.randomLooking113.value = doubleValue2;
      }

      booleanValue = null;
      booleanValue2 = null;
      booleanValue3 = null;
      integer = null;
      booleanValue4 = null;
      doubleValue = null;
      doubleValue2 = null;
   }

   private static void method2522() {
      if (mc.player != null && mc.options != null && BaritoneAPI.getProvider() != null) {
         if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            if (flag) {
               flag = false;

               for (Input var9 : inputs) {
                  KeyBinding var10 = method2523(var9);
                  if (var10 != null) {
                     var10.setPressed(false);
                  }
               }
            }
         } else {
            IInputOverrideHandler var0 = BaritoneAPI.getProvider().getPrimaryBaritone().getInputOverrideHandler();
            flag = true;

            for (Input var4 : inputs) {
               if (var4 != Input.SPRINT || !mc.player.isTouchingWater()) {
                  KeyBinding var5 = method2523(var4);
                  if (var5 != null) {
                     var5.setPressed(var0.isInputForcedDown(var4));
                  }
               }
            }
         }
      }
   }

   private static KeyBinding method2523(Input var0) {
      return switch (var0) {
         case MOVE_FORWARD -> mc.options.forwardKey;
         case MOVE_BACK -> mc.options.backKey;
         case MOVE_LEFT -> mc.options.leftKey;
         case MOVE_RIGHT -> mc.options.rightKey;
         case JUMP -> mc.options.jumpKey;
         case SNEAK -> mc.options.sneakKey;
         case SPRINT -> mc.options.sprintKey;
         default -> null;
      };
   }

   static {
      EventManager.register(new Object() {
         @EventTarget
         public void onUpdate(UpdateEvent var1) {
            SettingsBackup.method2522();
         }
      });
   }
}

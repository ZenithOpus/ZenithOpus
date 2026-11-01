package org.zenith.utility.mixin.scripting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({Properties.class})
public abstract class MixinScriptWorldTime {
   @ModifyReturnValue(
      method = {"getTimeOfDay()J"},
      at = {@At("RETURN")}
   )
   private long zenith$scriptTime(long var1) {
      return !ModuleHookDefinitions.class1212.active() ? var1 : (long)ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1212, var1);
   }
}

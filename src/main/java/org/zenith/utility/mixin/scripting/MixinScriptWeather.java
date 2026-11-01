package org.zenith.utility.mixin.scripting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({World.class})
public abstract class MixinScriptWeather {
   @ModifyReturnValue(
      method = {"getRainGradient(F)F"},
      at = {@At("RETURN")}
   )
   private float zenith$scriptRain(float var1) {
      return ModuleHookDefinitions.class1213.active() && (Object)this instanceof ClientWorld
         ? (float)Math.max(0.0, Math.min(1.0, ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1213, var1)))
         : var1;
   }

   @ModifyReturnValue(
      method = {"getThunderGradient(F)F"},
      at = {@At("RETURN")}
   )
   private float zenith$scriptThunder(float var1) {
      return ModuleHookDefinitions.class1214.active() && (Object)this instanceof ClientWorld
         ? (float)Math.max(0.0, Math.min(1.0, ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1214, var1)))
         : var1;
   }
}

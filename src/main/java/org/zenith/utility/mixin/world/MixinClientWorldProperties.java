package org.zenith.utility.mixin.world;

import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.client.modules.impl.render.WorldModule;

@Mixin({Properties.class})
public abstract class MixinClientWorldProperties {
   @Shadow
   private long timeOfDay;

   @Shadow
   public abstract boolean isRaining();

   @Inject(
      method = {"setTimeOfDay(J)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void setTimeOfDayHook(long var1, CallbackInfo var3) {
      float var4 = WorldModule.INSTANCE.method5647();
      if (var4 == -1.0F) {
         var4 = WeatherModule.INSTANCE.method3109();
      }

      if (var4 != -1.0F) {
         float var5 = (var4 % 24.0F + 24.0F) % 24.0F;
         this.timeOfDay = (long)((var5 + 18.0F) % 24.0F * 1000.0F);
         var3.cancel();
      }
   }
}

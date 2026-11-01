package org.zenith.utility.mixin.world;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({World.class})
public class MixinWorld {
   @Inject(
      method = {"getRainGradient(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getRainGradient(float var1, CallbackInfoReturnable<Float> var2) {
      if ((Object)this instanceof ClientWorld) {
         float var3 = WeatherModule.INSTANCE.method3110();
         if (var3 >= 0.0F) {
            var2.setReturnValue(var3);
         }
      }
   }

   @Inject(
      method = {"getThunderGradient(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getThunderGradient(float var1, CallbackInfoReturnable<Float> var2) {
      if ((Object)this instanceof ClientWorld) {
         float var3 = WeatherModule.INSTANCE.method3111();
         if (var3 >= 0.0F) {
            var2.setReturnValue(var3);
         }
      }
   }

   @Inject(
      method = {"isRaining()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isRaining(CallbackInfoReturnable<Boolean> var1) {
      if ((Object)this instanceof ClientWorld) {
         float var2 = WeatherModule.INSTANCE.method3110();
         if (var2 >= 0.0F) {
            var1.setReturnValue(var2 > 0.2F);
         }
      }
   }

   @Inject(
      method = {"isThundering()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isThundering(CallbackInfoReturnable<Boolean> var1) {
      if ((Object)this instanceof ClientWorld) {
         float var2 = WeatherModule.INSTANCE.method3111();
         if (var2 >= 0.0F) {
            var1.setReturnValue(var2 > 0.2F);
         }
      }
   }
}

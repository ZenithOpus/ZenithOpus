package org.zenith.utility.mixin.scripting;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem.PlayResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({SoundManager.class})
public abstract class MixinScriptSound {
   @Inject(
      method = {"play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$scriptSound(SoundInstance var1, CallbackInfoReturnable<PlayResult> var2) {
      if (ModuleHookDefinitions.class1215.active() && var1 != null) {
         ModuleHookDefinitions.Entry2 var3 = new ModuleHookDefinitions.Entry2(
            String.valueOf(var1.getId()), var1.getCategory().getName(), var1.getX(), var1.getY(), var1.getZ(), 1.0F, 1.0F
         );
         if (ModuleHookDefinitions.class1215.method1645(var3).cancelled) {
            var2.setReturnValue(PlayResult.NOT_STARTED);
         }
      }
   }
}

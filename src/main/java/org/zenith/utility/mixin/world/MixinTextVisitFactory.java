package org.zenith.utility.mixin.world;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({TextVisitFactory.class})
public class MixinTextVisitFactory {
   @ModifyArg(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
         ordinal = 0
      ),
      method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"},
      index = 0
   )
   private static String adjustText(String var0) {
      return protect(var0);
   }

   @Unique
   private static String protect(String var0) {
      return var0 != null && ModuleHookDefinitions.class124.active()
         ? ModuleHookDefinitions.class124.method1645(new ModuleHookDefinitions.Entry5(var0)).text
         : var0;
   }
}

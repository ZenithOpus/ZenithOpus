package org.zenith.utility.mixin.screen;

import java.util.List;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zenith.utility.misc.ScoreboardParser;

@Mixin({DebugHud.class})
public abstract class MixinDebugHud {
   @ModifyVariable(
      method = {"drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private List<String> zenith$randomizeCoords(List<String> var1) {
      return ScoreboardParser.isActive() ? ScoreboardParser.method7071(var1) : var1;
   }
}

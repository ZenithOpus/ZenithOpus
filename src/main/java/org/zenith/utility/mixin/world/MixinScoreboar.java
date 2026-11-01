package org.zenith.utility.mixin.world;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Scoreboard.class})
public abstract class MixinScoreboar {
   @Shadow
   @Nullable
   public abstract Team getScoreHolderTeam(String var1);

   @Inject(
      method = {"removeScoreHolderFromTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/Team;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void remove(String var1, Team var2, CallbackInfo var3) {
      if (this.getScoreHolderTeam(var1) != var2) {
         var3.cancel();
      }
   }
}

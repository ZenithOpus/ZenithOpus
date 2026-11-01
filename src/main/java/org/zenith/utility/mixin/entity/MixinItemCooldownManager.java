package org.zenith.utility.mixin.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zenith.client.modules.impl.combat.PvpHelperModule;

@Mixin({ItemCooldownManager.class})
public abstract class MixinItemCooldownManager {
   @ModifyVariable(
      method = {"set(Lnet/minecraft/util/Identifier;I)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private int zenith$reduceCooldown(int var1, Identifier var2) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      return var3.player != null && (Object)this == var3.player.getItemCooldownManager() ? PvpHelperModule.INSTANCE.method4641(var2, var1) : var1;
   }
}

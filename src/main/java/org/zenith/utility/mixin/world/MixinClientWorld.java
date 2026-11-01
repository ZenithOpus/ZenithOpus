package org.zenith.utility.mixin.world;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.utility.game.Util11;

@Mixin({ClientWorld.class})
public class MixinClientWorld {
   @Inject(
      method = {"addEntity(Lnet/minecraft/entity/Entity;)V"},
      at = {@At("RETURN")}
   )
   public void injectAddEntity(Entity var1, CallbackInfo var2) {
      EventManager.call(new EntityAddEvent(var1));
   }

   @Inject(
      method = {"updateListeners(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V"},
      at = {@At("RETURN")}
   )
   public void zenith$snowBlockChanged(BlockPos var1, BlockState var2, BlockState var3, int var4, CallbackInfo var5) {
      Util11.method2330((ClientWorld)(Object)this, var1, var2, var3);
   }
}

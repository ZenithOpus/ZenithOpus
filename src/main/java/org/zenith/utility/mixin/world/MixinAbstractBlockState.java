package org.zenith.utility.mixin.world;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.BlockCollisionEvent;

@Mixin({AbstractBlockState.class})
public abstract class MixinAbstractBlockState {
   @Shadow
   public abstract Block getBlock();

   @Inject(
      method = {"onEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityCollisionHandler;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityCollision(World var1, BlockPos var2, Entity var3, EntityCollisionHandler var4, boolean var5, CallbackInfo var6) {
      if (var3 == MinecraftClient.getInstance().player) {
         BlockCollisionEvent var7 = new BlockCollisionEvent(this.getBlock(), var2);
         EventManager.call(var7);
         if (var7.isCancelled()) {
            var6.cancel();
         }
      }
   }
}

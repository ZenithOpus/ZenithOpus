package org.zenith.utility.mixin.world;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData.BlockEntityVisitor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.BlockESPModule;

@Mixin({ClientChunkManager.class})
public class MixinClientChunkManager {
   @Inject(
      method = {"loadChunkFromPacket(IILnet/minecraft/network/PacketByteBuf;Ljava/util/Map;Ljava/util/function/Consumer;)Lnet/minecraft/world/chunk/WorldChunk;"},
      at = {@At("RETURN")}
   )
   private void zenith$onChunkLoaded(
      int var1, int var2, PacketByteBuf var3, Map<?, ?> var4, Consumer<BlockEntityVisitor> var5, CallbackInfoReturnable<WorldChunk> var6
   ) {
      WorldChunk var7 = (WorldChunk)var6.getReturnValue();
      if (var7 != null) {
         BlockESPModule.INSTANCE.method7560(var7, var1, var2);
      }
   }

   @Inject(
      method = {"unload(Lnet/minecraft/util/math/ChunkPos;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$onChunkUnloaded(ChunkPos var1, CallbackInfo var2) {
      BlockESPModule.INSTANCE.method7561(var1.x, var1.z);
   }
}

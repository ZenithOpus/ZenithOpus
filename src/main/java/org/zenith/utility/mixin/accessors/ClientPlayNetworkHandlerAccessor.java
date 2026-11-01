package org.zenith.utility.mixin.accessors;

import java.util.Set;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientPlayNetworkHandler.class})
public interface ClientPlayNetworkHandlerAccessor {
   @Accessor("world")
   ClientWorld zenith$getWorld();

   @Accessor("world")
   void zenith$setWorld(ClientWorld var1);

   @Accessor("worldProperties")
   void zenith$setWorldProperties(Properties var1);

   @Accessor("worldKeys")
   void zenith$setWorldKeys(Set<RegistryKey<World>> var1);

   @Accessor("chunkLoadDistance")
   void zenith$setChunkLoadDistance(int var1);

   @Accessor("chunkLoadDistance")
   int zenith$getChunkLoadDistance();

   @Accessor("simulationDistance")
   void zenith$setSimulationDistance(int var1);

   @Invoker("sendAcknowledgment")
   void zenith$sendAcknowledgment();
}

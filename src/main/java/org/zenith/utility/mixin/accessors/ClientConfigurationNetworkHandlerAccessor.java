package org.zenith.utility.mixin.accessors;

import com.mojang.authlib.GameProfile;
import java.util.function.Function;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientConfigurationNetworkHandler.class})
public interface ClientConfigurationNetworkHandlerAccessor {
   @Accessor("profile")
   GameProfile zenith$getProfile();

   @Accessor("enabledFeatures")
   FeatureSet zenith$getEnabledFeatures();

   @Accessor("clientRegistries")
   ClientRegistries zenith$getClientRegistries();

   @Accessor("registryManager")
   Immutable zenith$getRegistryManager();

   @Accessor("dataPackManager")
   ClientDataPackManager zenith$getDataPackManager();

   @Accessor("dataPackManager")
   void zenith$setDataPackManager(ClientDataPackManager var1);

   @Invoker("openClientDataPack")
   <T> T zenith$openClientDataPack(Function<ResourceFactory, T> var1);
}

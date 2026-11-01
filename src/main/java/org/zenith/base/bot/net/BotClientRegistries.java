package org.zenith.base.bot.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.Registry.PendingTagLoad;
import net.minecraft.registry.RegistryLoader.ElementsAndTags;
import net.minecraft.registry.SerializableRegistries.SerializedRegistryEntry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer.Serialized;
import net.minecraft.resource.ResourceFactory;

public final class BotClientRegistries {
   private final Map<RegistryKey<? extends Registry<?>>, List<SerializedRegistryEntry>> dynamicRegistries = new HashMap<>();
   private final Map<RegistryKey<? extends Registry<?>>, Serialized> tags = new HashMap<>();

   public void putDynamicRegistry(RegistryKey<? extends Registry<?>> var1, List<SerializedRegistryEntry> var2) {
      this.dynamicRegistries.computeIfAbsent(var1, var0 -> new ArrayList<>()).addAll(var2);
   }

   public void putTags(Map<RegistryKey<? extends Registry<?>>, Serialized> var1) {
      this.tags.putAll(var1);
   }

   public Immutable createRegistryManager(ResourceFactory var1, Immutable var2) {
      if (this.dynamicRegistries.isEmpty()) {
         return var2;
      } else {
         CombinedDynamicRegistries var3 = ClientDynamicRegistryType.createCombinedDynamicRegistries();
         Immutable var4 = var3.getPrecedingRegistryManagers(ClientDynamicRegistryType.REMOTE);
         HashMap var5 = new HashMap();
         this.dynamicRegistries.forEach((var1x, var2x) -> var5.put(var1x, new ElementsAndTags((List<SerializedRegistryEntry>)var2x, Serialized.NONE)));
         ArrayList var6 = new ArrayList();
         this.tags.forEach((var3x, var4x) -> {
            if (!var4x.isEmpty()) {
               if (SerializableRegistries.isSynced((RegistryKey<? extends Registry<?>>)var3x)) {
                  var5.compute(var3x, (var1xx, var2xx) -> new ElementsAndTags(var2xx != null ? ((ElementsAndTags)var2xx).elements() : List.of(), var4x));
               } else {
                  var6.add(startTagReload(var4, (RegistryKey<? extends Registry<?>>)var3x, var4x));
               }
            }
         });
         var var7 = TagGroupLoader.collectRegistries(var4, var6);
         Immutable var8 = RegistryLoader.loadFromNetwork(var5, var1, var7, RegistryLoader.SYNCED_REGISTRIES).toImmutable();
         return var3.with(ClientDynamicRegistryType.REMOTE, var8).getCombinedRegistryManager().toImmutable();
      }
   }

   private static <T> PendingTagLoad<T> startTagReload(Immutable var0, RegistryKey<? extends Registry<? extends T>> var1, Serialized var2) {
      Registry var3 = var0.getOrThrow(var1);
      return var3.startTagReload(var2.toRegistryTags(var3));
   }
}

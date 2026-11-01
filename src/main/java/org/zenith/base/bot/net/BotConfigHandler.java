package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.entity.passive.WolfVariants;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.ClearDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.CodeOfConductS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.network.state.PlayStateFactories.PacketCodecModifierContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.SerializableRegistries.SerializedRegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;

public final class BotConfigHandler extends BotCommonHandler implements TickablePacketListener, ClientConfigurationPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile profile;
   private final Immutable receivedRegistries;
   private final BotClientRegistries clientRegistries = new BotClientRegistries();
   private final Map<RegistryKey<? extends Registry<?>>, Integer> receivedRegistryEntries = new HashMap<>();
   private FeatureSet enabledFeatures;
   @Nullable
   private ClientDataPackManager dataPackManager;

   public BotConfigHandler(BotClient var1, BotConnection var2, BotConnectionState var3) {
      super(var1, var2, var3);
      this.profile = var3.profile();
      this.receivedRegistries = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
   }

   @Override
   public void onDynamicRegistries(DynamicRegistriesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.clientRegistries.putDynamicRegistry(packet.registry(), packet.entries());
      this.receivedRegistryEntries.merge(packet.registry(), packet.entries().size(), Integer::sum);
   }

   @Override
   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.clientRegistries.putTags(packet.getGroups());
   }

   @Override
   public void onFeatures(FeaturesS2CPacket packet) {
      this.enabledFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(packet.features());
   }

   @Override
   public void onSelectKnownPacks(SelectKnownPacksS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.dataPackManager == null) {
         this.dataPackManager = new ClientDataPackManager();
      }

      this.sendPacket(new SelectKnownPacksC2SPacket(this.dataPackManager.getCommonKnownPacks(packet.knownPacks())));
   }

   @Override
   public void onResetChat(ResetChatS2CPacket packet) {
   }

   @Override
   public void onReady(ReadyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.connection.isOpen() && this.connection.getPacketListener() == this) {
         this.installMissingWolfVariantFallback();

         Immutable var2;
         try {
            var2 = this.openClientDataPack(var1 -> this.clientRegistries.createRegistryManager(var1, this.receivedRegistries));
         } catch (RuntimeException var5) {
            LOGGER.error("Bot {} registry loading failed", this.client.getName(), var5);
            this.connection.disconnect(Text.literal("registry loading failed"));
            return;
         }

         BotConnectionState var3 = new BotConnectionState(
            this.profile, var2, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
         );
         BotPlayHandler var4 = new BotPlayHandler(this.client, this.connection, var3);
         this.client.setPlayHandler(var4);
         this.connection.transitionInbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(var2)), var4);
         this.connection.send(ReadyC2SPacket.INSTANCE);
         this.connection.transitionOutbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(var2), new PacketCodecModifierContext() {
            @Override
            public boolean isInCreativeMode() {
               return false;
            }
         }));
      }
   }

   private <T> T openClientDataPack(Function<ResourceFactory, T> var1) {
      if (this.dataPackManager == null) {
         return (T)var1.apply(ResourceFactory.MISSING);
      } else {
         Object var3;
         try (LifecycledResourceManager var2 = this.dataPackManager.createResourceManager()) {
            var3 = var1.apply(var2);
         }

         return (T)var3;
      }
   }

   private void installMissingWolfVariantFallback() {
      if (this.receivedRegistryEntries.getOrDefault(RegistryKeys.WOLF_VARIANT, 0) <= 0) {
         var var1 = List.of(
            WolfVariants.PALE,
            WolfVariants.SPOTTED,
            WolfVariants.SNOWY,
            WolfVariants.BLACK,
            WolfVariants.ASHEN,
            WolfVariants.RUSTY,
            WolfVariants.WOODS,
            WolfVariants.CHESTNUT,
            WolfVariants.STRIPED
         );
         var var2 = var1.stream().map(var0 -> new SerializedRegistryEntry(var0.getValue(), Optional.empty())).toList();
         this.clientRegistries.putDynamicRegistry(RegistryKeys.WOLF_VARIANT, var2);
         if (this.dataPackManager == null) {
            this.dataPackManager = new ClientDataPackManager();
            this.dataPackManager.getCommonKnownPacks(List.of(VanillaResourcePackProvider.VANILLA_ID));
         }

         LOGGER.debug("Bot {} installed wolf_variant registry fallback ({} entries)", this.client.getName(), var2.size());
      }
   }

   @Override
   public void tick() {
   }

   @Override
   public void onShowDialog(ShowDialogS2CPacket packet) {
   }

   @Override
   public void onClearDialog(ClearDialogS2CPacket packet) {
   }

   @Override
   public void onCodeOfConduct(CodeOfConductS2CPacket packet) {
   }
}

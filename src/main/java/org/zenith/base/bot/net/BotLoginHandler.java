package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;

public final class BotLoginHandler implements ClientLoginPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BotClient client;
   private final BotConnection connection;
   private final Map<Identifier, byte[]> serverCookies = new HashMap<>();

   public BotLoginHandler(BotClient var1, BotConnection var2) {
      this.client = var1;
      this.connection = var2;
   }

   @Override
   public void onHello(LoginHelloS2CPacket packet) {
      if (packet.needsAuthentication()) {
         this.connection.disconnect(Text.literal("Online-mode server is not supported by bots"));
      } else {
         Cipher var2;
         Cipher var3;
         LoginKeyC2SPacket var4;
         try {
            SecretKey var5 = NetworkEncryptionUtils.generateSecretKey();
            PublicKey var6 = packet.getPublicKey();
            var2 = NetworkEncryptionUtils.cipherFromKey(2, var5);
            var3 = NetworkEncryptionUtils.cipherFromKey(1, var5);
            var4 = new LoginKeyC2SPacket(var5, var6, packet.getNonce());
         } catch (Exception var7) {
            throw new IllegalStateException("Protocol error", var7);
         }

         this.connection.send(var4, PacketCallbacks.always(() -> this.connection.setupEncryption(var2, var3)));
      }
   }

   @Override
   public void onSuccess(LoginSuccessS2CPacket packet) {
      if (this.connection.isOpen() && this.connection.getPacketListener() == this) {
         GameProfile var2 = packet.profile();
         this.client.setPhase(BotPhase.CONFIGURATION);
         LOGGER.debug("Bot {} login success, profileId={}", this.client.getName(), var2.id());
         BotConnectionState var3 = new BotConnectionState(
            var2,
            ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(),
            FeatureFlags.DEFAULT_ENABLED_FEATURES,
            null,
            this.serverCookies,
            Map.of(),
            ServerLinks.EMPTY
         );
         BotConfigHandler var4 = new BotConfigHandler(this.client, this.connection, var3);
         this.connection.transitionInbound(ConfigurationStates.S2C, var4);
         this.connection.send(EnterConfigurationC2SPacket.INSTANCE);
         this.connection.transitionOutbound(ConfigurationStates.C2S);
         this.connection.send(new CustomPayloadC2SPacket(new BrandCustomPayload(this.client.getConfig().brand())));
         this.connection.send(new ClientOptionsC2SPacket(SyncedClientOptions.createDefault()));
      }
   }

   @Override
   public void onDisconnect(LoginDisconnectS2CPacket packet) {
      this.connection.disconnect(packet.reason());
   }

   @Override
   public void onCompression(LoginCompressionS2CPacket packet) {
      this.connection.setCompressionThreshold(packet.getCompressionThreshold(), false);
   }

   @Override
   public void onQueryRequest(LoginQueryRequestS2CPacket packet) {
      this.connection.send(new LoginQueryResponseC2SPacket(packet.queryId(), null));
   }

   @Override
   public void onCookieRequest(CookieRequestS2CPacket packet) {
      this.connection.send(new CookieResponseC2SPacket(packet.key(), this.serverCookies.get(packet.key())));
   }

   @Override
   public void onDisconnected(DisconnectionInfo info) {
      this.client.onConnectionTerminated(info);
   }

   @Override
   public boolean isConnectionOpen() {
      return this.connection.isOpen();
   }
}

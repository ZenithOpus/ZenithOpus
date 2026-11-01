package org.zenith.base.bot.via;

import com.mojang.logging.LogUtils;
import com.viaversion.vialoader.ViaLoader;
import com.viaversion.vialoader.impl.platform.ViaVersionPlatformImpl;
import com.viaversion.vialoader.impl.viaversion.VLCommandHandler;
import com.viaversion.vialoader.impl.viaversion.VLInjector;
import com.viaversion.vialoader.netty.ViaDecoder;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;

public final class BotVia {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int NATIVE_PROTOCOL = SharedConstants.getGameVersion().protocolVersion();
   public static final AttributeKey<Integer> TARGET_PROTOCOL = AttributeKey.valueOf("zenith-via-target-protocol");
   private static final String VIA_ENCODER_NAME = "via-encoder";
   private static final String VIA_DECODER_NAME = "via-decoder";
   private static boolean initialized;
   private static boolean failed;

   private BotVia() {
   }

   public static boolean isTranslationNeeded(int var0) {
      return var0 > 0 && var0 != NATIVE_PROTOCOL;
   }

   public static synchronized boolean ensureInitialized() {
      if (initialized) {
         return true;
      } else if (failed) {
         return false;
      } else {
         try {
            if (isViaLoaded()) {
               installVersionProvider();
               LOGGER.info("Bot ViaVersion: attached to existing Via platform '{}'", Via.getPlatform().getPlatformName());
            } else {
               ViaLoader.init(
                  new ViaVersionPlatformImpl(FabricLoader.getInstance().getConfigDir().resolve("zenith-via").toFile()), new BotViaLoader(), new VLInjector() {
                     @Override
                     public String getEncoderName() {
                        return "via-encoder";
                     }

                     @Override
                     public String getDecoderName() {
                        return "via-decoder";
                     }
                  }, new VLCommandHandler()
               );
               LOGGER.info("Bot ViaVersion initialized (native protocol {})", NATIVE_PROTOCOL);
            }

            initialized = true;
            return true;
         } catch (Throwable var1) {
            failed = true;
            LOGGER.error("Failed to initialize ViaVersion for bots", var1);
            return false;
         }
      }
   }

   private static boolean isViaLoaded() {
      try {
         return Via.getManager() != null;
      } catch (Throwable var1) {
         return false;
      }
   }

   private static void installVersionProvider() {
      ViaProviders var0 = Via.getManager().getProviders();
      VersionProvider var1 = (VersionProvider)var0.get(VersionProvider.class);
      if (!(var1 instanceof BotViaVersionProvider)) {
         var0.use(VersionProvider.class, new BotViaVersionProvider(var1));
      }
   }

   public static void injectPipeline(Channel var0, int var1) {
      var0.attr(TARGET_PROTOCOL).set(var1);
      UserConnectionImpl var2 = new UserConnectionImpl(var0, true);
      new ProtocolPipelineImpl(var2);
      ChannelPipeline var3 = var0.pipeline();
      var3.addBefore("encoder", "via-encoder", new BotViaEncoder(var2));
      var3.addBefore("inbound_config", "via-decoder", new ViaDecoder(var2));
   }

   public static Integer targetProtocol(Channel var0) {
      return (Integer)var0.attr(TARGET_PROTOCOL).get();
   }
}

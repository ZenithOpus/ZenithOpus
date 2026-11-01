package org.zenith.base.bot.via;

import com.mojang.logging.LogUtils;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class BotViaVersionProvider extends BaseVersionProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private final VersionProvider delegate;

   public BotViaVersionProvider(@Nullable VersionProvider var1) {
      this.delegate = var1;
   }

   public ProtocolVersion getClosestServerProtocol(UserConnection var1) throws Exception {
      Integer var2 = var1.isClientSide() ? BotVia.targetProtocol(var1.getChannel()) : null;
      if (var2 != null) {
         ProtocolInfo var3 = Objects.requireNonNull(var1.getProtocolInfo());
         if (!ProtocolVersion.isRegistered(var2)) {
            LOGGER.warn("Bot ViaVersion: protocol {} is not registered, using native", var2);
            return var3.protocolVersion();
         } else {
            ProtocolVersion var4 = ProtocolVersion.getProtocol(var2);
            boolean var5 = var4.equals(var3.protocolVersion()) || Via.getManager().getProtocolManager().getProtocolPath(var3.protocolVersion(), var4) != null;
            if (!var5) {
               LOGGER.warn("Bot ViaVersion: no protocol path {} -> {}, using native", var3.protocolVersion(), var4);
               return var3.protocolVersion();
            } else {
               LOGGER.info("Bot ViaVersion: translating {} -> {}", var3.protocolVersion(), var4);
               return var4;
            }
         }
      } else if (this.delegate != null) {
         return this.delegate.getClosestServerProtocol(var1);
      } else {
         return var1.isClientSide() ? Objects.requireNonNull(var1.getProtocolInfo()).protocolVersion() : super.getClosestServerProtocol(var1);
      }
   }
}

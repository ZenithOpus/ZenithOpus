package org.zenith.base.bot.via;

import com.viaversion.vialoader.netty.ViaEncoder;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public final class BotViaEncoder extends ViaEncoder {
   public BotViaEncoder(UserConnection var1) {
      super(var1);
   }

   @Override
   public void encode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      super.encode(var1, var2, var3);
      ProtocolPipeline var4 = this.connection.getProtocolInfo().getPipeline();
      if (var4.hasNonBaseProtocols() && !var4.contains(BotViaCompatibilityProtocol.class)) {
         var4.add(BotViaCompatibilityProtocol.INSTANCE);
      }
   }
}

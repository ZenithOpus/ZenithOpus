package org.zenith.utility.mixin.network;

import io.netty.channel.Channel;
import io.netty.handler.proxy.ProxyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Zenith;

@Mixin(
   targets = {"net/minecraft/network/ClientConnection$1"}
)
public class MixinClientConnectionChannelInit {
   @Inject(
      method = {"initChannel(Lio/netty/channel/Channel;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$addProxy(Channel var1, CallbackInfo var2) {
      Zenith var3 = Zenith.getInstance();
      if (var3 != null && var3.getProxies() != null) {
         ProxyHandler var4 = var3.getProxies().createHandler();
         if (var4 != null) {
            var1.pipeline().addFirst("zenith_proxy", var4);
         }
      }
   }
}

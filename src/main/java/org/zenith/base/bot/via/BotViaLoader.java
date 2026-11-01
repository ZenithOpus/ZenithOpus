package org.zenith.base.bot.via;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;

public final class BotViaLoader implements ViaPlatformLoader {
   public void load() {
      Via.getManager().getProviders().use(VersionProvider.class, new BotViaVersionProvider(null));
   }

   public void unload() {
   }
}

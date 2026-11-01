package org.zenith.utility.discord.callbacks;

import com.sun.jna.Callback;
import org.zenith.utility.discord.utils.DiscordUser;

public interface ReadyCallback extends Callback {
   void apply(DiscordUser var1);
}

package org.zenith.utility.discord.callbacks;

import com.sun.jna.Callback;
import org.zenith.utility.discord.utils.DiscordUser;

public interface JoinRequestCallback extends Callback {
   void apply(DiscordUser var1);
}

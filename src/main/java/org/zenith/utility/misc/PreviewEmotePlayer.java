package org.zenith.utility.misc;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;

public final class PreviewEmotePlayer extends KeyframeAnimationPlayer {
   private final EmoteInfo emoteInfo;

   public PreviewEmotePlayer(EmoteInfo var1, int var2) {
      super(var1.getKeyframeAnimation(), Math.max(0, var2));
      this.emoteInfo = var1;
   }

   public EmoteInfo getEmoteInfo() {
      return this.emoteInfo;
   }
}

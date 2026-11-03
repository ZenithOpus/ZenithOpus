package org.zenith.utility.mixin.network;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.utility.session.SharedSession;

@Mixin(
   targets = {"net/minecraft/network/PacketApplyBatcher$Entry"}
)
public abstract class MixinPacketApplyEntry {
   @Inject(method = "apply()V", at = @At("RETURN"))
   private void zenith$shareAppliedPacket(CallbackInfo ci) {
      SharedSession.getInstance().applied(this.packet());
   }
   @Shadow
   public abstract Packet<?> packet();

   @Inject(
      method = {"apply()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$beforeApply(CallbackInfo var1) {
      if (EventManager.hasListeners(PacketReceiveEvent.class)) {
         if (((PacketReceiveEvent)EventManager.call(new PacketReceiveEvent(this.packet()))).isCancelled()) {
            var1.cancel();
         }
      }
   }
}

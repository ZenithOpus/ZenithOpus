package org.zenith.utility.mixin.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.session.SharedSession;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinSessionPlayer {
    @Inject(method = {"tickMovement()V", "sendMovementPackets()V", "tickRiding()V"}, at = @At("HEAD"), cancellable = true)
    private void zenith$remotePhysics(CallbackInfo ci) {
        if (SharedSession.getInstance().isControlled()) ci.cancel();
    }
}

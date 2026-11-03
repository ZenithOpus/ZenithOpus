package org.zenith.utility.mixin.client_core;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.session.SharedSession;

@Mixin(MinecraftClient.class)
public abstract class MixinSessionLifecycle {
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void zenith$sessionTick(CallbackInfo ci) { SharedSession.getInstance().tick(); }

    @Inject(method = "scheduleStop()V", at = @At("HEAD"), cancellable = true)
    private void zenith$backgroundSession(CallbackInfo ci) {
        if (SharedSession.getInstance().keepInBackground()) ci.cancel();
    }

    @Inject(method = "handleInputEvents()V", at = @At("HEAD"), cancellable = true)
    private void zenith$remoteInput(CallbackInfo ci) {
        if (SharedSession.getInstance().isControlled()) ci.cancel();
    }

    @Inject(method = "close()V", at = @At("HEAD"))
    private void zenith$closeSession(CallbackInfo ci) { SharedSession.getInstance().stop(); }
}

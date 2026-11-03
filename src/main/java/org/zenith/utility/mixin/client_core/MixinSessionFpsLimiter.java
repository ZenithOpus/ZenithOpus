package org.zenith.utility.mixin.client_core;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.option.InactivityFpsLimiter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.session.SharedSession;

@Mixin(InactivityFpsLimiter.class)
public abstract class MixinSessionFpsLimiter {
    @ModifyReturnValue(method = "update()I", at = @At("RETURN"))
    private int zenith$keepSessionResponsive(int limit) {
        // Input and upstream packets are applied by the render thread. Vanilla's 10 FPS
        // minimized/AFK limit batches them into visible jumps on the second PC.
        return SharedSession.getInstance().isActive() ? Math.max(60, limit) : limit;
    }
}

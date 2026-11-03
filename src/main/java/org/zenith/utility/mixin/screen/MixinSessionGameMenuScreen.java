package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.session.SharedSession;
import org.zenith.client.screens.session.SessionMenuButton;

@Mixin(GameMenuScreen.class)
public abstract class MixinSessionGameMenuScreen extends Screen {
    @Shadow private ButtonWidget exitButton;
    protected MixinSessionGameMenuScreen() { super(Text.empty()); }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void zenith$sessionButton(CallbackInfo ci) {
        if (exitButton != null && SharedSession.getInstance().isActive()) exitButton.setMessage(Text.literal("Оставить сессию в фоне"));
        addDrawableChild(new SessionMenuButton(Math.max(4, width - 136), 8,
            SharedSession.getInstance().isActive() ? "Общая сессия" : "Подключить сессию", () -> SharedSession.getInstance().open(this)));
    }

    @Inject(method = "method_19836", at = @At("HEAD"), cancellable = true)
    private void zenith$leaveInBackground(ButtonWidget button, CallbackInfo ci) {
        if (SharedSession.getInstance().keepInBackground()) ci.cancel();
    }
}

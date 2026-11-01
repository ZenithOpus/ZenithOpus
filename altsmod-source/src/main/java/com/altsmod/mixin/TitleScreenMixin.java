package com.altsmod.mixin;

import com.altsmod.gui.AltManagerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TitleScreen.class})
public abstract class TitleScreenMixin extends Screen {
   protected TitleScreenMixin(Text title) {
      super(title);
   }

   @Inject(
      method = {"init"},
      at = {@At("RETURN")}
   )
   private void altsmod$addAltsButton(CallbackInfo ci) {
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Alts"), (btn) -> {
         if (this.client != null) {
            this.client.setScreen(new AltManagerScreen(this));
         }

      }).dimensions(this.width / 2 + 104, this.height / 4 + 48 + 24, 55, 20).build());
   }
}

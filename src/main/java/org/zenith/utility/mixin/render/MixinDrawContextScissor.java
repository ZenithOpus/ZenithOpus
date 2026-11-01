package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.ScissorStack;

@Mixin({DrawContext.class})
public abstract class MixinDrawContextScissor {
   @Inject(
      method = {"enableScissor(IIII)V"},
      at = {@At("TAIL")}
   )
   private void zenith$pushScissor(int var1, int var2, int var3, int var4, CallbackInfo var5) {
      ScissorStack.method1750(((DrawContext)(Object)this).scissorStack.peekLast());
   }

   @Inject(
      method = {"disableScissor()V"},
      at = {@At("TAIL")}
   )
   private void zenith$popScissor(CallbackInfo var1) {
      ScissorStack.method1750(((DrawContext)(Object)this).scissorStack.peekLast());
   }
}

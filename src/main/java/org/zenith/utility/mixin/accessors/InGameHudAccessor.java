package org.zenith.utility.mixin.accessors;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({InGameHud.class})
public interface InGameHudAccessor {
   @Invoker("renderHotbar")
   void invokeRenderHotbar(DrawContext var1, RenderTickCounter var2);

   @Invoker("renderStatusBars")
   void invokeRenderStatusBars(DrawContext var1);
}

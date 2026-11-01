package org.zenith.utility.mixin.accessors;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.fog.FogRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GameRenderer.class})
public interface GameRendererAccessor {
   @Invoker("renderHand")
   void zenith$renderHand(float var1, boolean var2, Matrix4f var3);

   @Accessor("overlayRenderer")
   InGameOverlayRenderer zenith$overlayRenderer();

   @Accessor("guiRenderer")
   GuiRenderer zenith$guiRenderer();

   @Accessor("guiState")
   GuiRenderState zenith$guiState();

   @Accessor("fogRenderer")
   FogRenderer zenith$fogRenderer();

   @Accessor("entityRenderDispatcher")
   RenderDispatcher zenith$entityRenderDispatcher();
}

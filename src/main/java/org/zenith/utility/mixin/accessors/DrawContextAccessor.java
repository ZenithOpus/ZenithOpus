package org.zenith.utility.mixin.accessors;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({DrawContext.class})
public interface DrawContextAccessor {
   @Accessor("state")
   GuiRenderState zenith$state();

   @Accessor("mouseX")
   int zenith$mouseX();

   @Accessor("mouseY")
   int zenith$mouseY();

   @Invoker("drawItemBar")
   void callDrawItemBar(ItemStack var1, int var2, int var3);

   @Invoker("drawCooldownProgress")
   void callDrawCooldownProgress(ItemStack var1, int var2, int var3);
}

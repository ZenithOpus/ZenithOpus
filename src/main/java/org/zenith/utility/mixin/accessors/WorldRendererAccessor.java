package org.zenith.utility.mixin.accessors;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({WorldRenderer.class})
public interface WorldRendererAccessor {
   @Accessor("world")
   @Nullable
   ClientWorld zenith$getWorld();

   @Invoker("scheduleSectionRender")
   void zenith$scheduleSectionRender(BlockPos var1, boolean var2);
}

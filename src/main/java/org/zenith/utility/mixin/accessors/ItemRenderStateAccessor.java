package org.zenith.utility.mixin.accessors;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderState.LayerRenderState;
import net.minecraft.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemRenderState.class})
public interface ItemRenderStateAccessor {
   @Accessor("layers")
   LayerRenderState[] zenith$getLayers();

   @Accessor("layerCount")
   int zenith$getLayerCount();

   @Accessor("displayContext")
   ItemDisplayContext zenith$getDisplayContext();
}

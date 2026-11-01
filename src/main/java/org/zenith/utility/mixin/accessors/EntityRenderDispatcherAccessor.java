package org.zenith.utility.mixin.accessors;

import java.util.Map;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerSkinType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityRenderManager.class})
public interface EntityRenderDispatcherAccessor {
   @Accessor("renderers")
   Map<PlayerSkinType, EntityRenderer<? extends PlayerEntity, ?>> zenith$getModelRenderers();
}

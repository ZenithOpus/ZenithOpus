package org.zenith.utility.mixin.accessors;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Particle.class})
public interface ParticleAccessor {
   @Accessor("world")
   ClientWorld zenith$getWorld();
}

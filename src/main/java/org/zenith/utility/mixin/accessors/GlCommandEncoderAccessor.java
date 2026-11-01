package org.zenith.utility.mixin.accessors;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.GlCommandEncoder;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GlCommandEncoder.class})
public interface GlCommandEncoderAccessor {
   @Accessor("currentProgram")
   void zenith$setCurrentProgram(ShaderProgram var1);

   @Accessor("currentPipeline")
   void zenith$setCurrentPipeline(RenderPipeline var1);
}

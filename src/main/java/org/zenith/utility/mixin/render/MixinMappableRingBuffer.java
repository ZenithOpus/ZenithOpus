package org.zenith.utility.mixin.render;

import net.minecraft.client.gl.MappableRingBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({MappableRingBuffer.class})
public class MixinMappableRingBuffer {
   @Unique
   private static final int ZENITH_DEPTH = 6;

   @ModifyConstant(
      method = {"<init>(Ljava/util/function/Supplier;II)V", "rotate()V", "close()V"},
      constant = {@Constant(
         intValue = 3
      )}
   )
   private int zenith$depth(int var1) {
      return 6;
   }
}

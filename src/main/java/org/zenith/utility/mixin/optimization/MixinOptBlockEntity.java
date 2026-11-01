package org.zenith.utility.mixin.optimization;

import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.Util20;

@Mixin({BlockEntity.class})
public class MixinOptBlockEntity implements Util20.Handler {
   @Unique
   private Util20.Entry zenith$optData;

   @Override
   public Util20.Entry zenith$optData() {
      Util20.Entry var1 = this.zenith$optData;
      if (var1 == null) {
         var1 = new Util20.Entry();
         this.zenith$optData = var1;
      }

      return var1;
   }
}

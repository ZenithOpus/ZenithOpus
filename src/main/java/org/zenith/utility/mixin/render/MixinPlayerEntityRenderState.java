package org.zenith.utility.mixin.render;

import java.util.List;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.Handler4;
import org.zenith.utility.misc.HoveredData;

@Mixin({PlayerEntityRenderState.class})
public class MixinPlayerEntityRenderState implements Handler4 {
   @Unique
   private String zenith$profileName;
   @Unique
   private List<HoveredData> zenith$wardrobeLoadout;

   @Override
   public String zenith$profileName() {
      return this.zenith$profileName;
   }

   @Override
   public void zenith$setProfileName(String var1) {
      this.zenith$profileName = var1;
   }

   @Override
   public List<HoveredData> zenith$wardrobeLoadout() {
      return this.zenith$wardrobeLoadout;
   }

   @Override
   public void zenith$setWardrobeLoadout(List<HoveredData> var1) {
      this.zenith$wardrobeLoadout = var1;
   }
}

package org.zenith.utility.mixin.optimization;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.TextureManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.MapAtlas;
import org.zenith.utility.misc.Util30;

@Mixin({MapTextureManager.class})
public abstract class MixinOptMapTextureManager implements Util30.Handler {
   @Shadow
   @Final
   private Int2ObjectMap<?> texturesByMapId;
   @Shadow
   @Final
   public TextureManager textureManager;
   @Unique
   private MapAtlas zenith$atlasPool;

   @Override
   public MapAtlas zenith$getAtlasPool() {
      if (this.zenith$atlasPool == null) {
         this.zenith$atlasPool = new MapAtlas();
      }

      return this.zenith$atlasPool;
   }

   @Override
   public TextureManager zenith$getTextureManager() {
      return this.textureManager;
   }

   @Nullable
   @Override
   public MapAtlas.Data zenith$getAtlasSlot(int var1) {
      if (this.zenith$atlasPool != null && !this.zenith$atlasPool.isEmpty()) {
         return this.texturesByMapId.get(var1) instanceof Util30.Handler2 var3 ? var3.zenith$getAtlasSlot() : null;
      } else {
         return null;
      }
   }

   @Inject(
      method = {"clear()V"},
      at = {@At("RETURN")}
   )
   private void zenith$clearMapAtlas(CallbackInfo var1) {
      if (this.zenith$atlasPool != null) {
         this.zenith$atlasPool.method6152(this.textureManager);
      }
   }
}

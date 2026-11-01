package org.zenith.utility.misc;

import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class Util30 {
   private Util30() {
   }

   public interface Handler {
      MapAtlas zenith$getAtlasPool();

      TextureManager zenith$getTextureManager();

      @Nullable
      MapAtlas.Data zenith$getAtlasSlot(int var1);
   }

   public interface Handler2 {
      @Nullable
      MapAtlas.Data zenith$getAtlasSlot();
   }

   public interface Handler3 {
      void zenith$setAtlasSlot(@Nullable MapAtlas.Data var1);

      @Nullable
      MapAtlas.Data zenith$getAtlasSlot();

      @Nullable
      default MapAtlas.Data method7747(@Nullable Identifier var1) {
         MapAtlas.Data var2 = this.zenith$getAtlasSlot();
         return var2 != null && var2.method6153().equals(var1) ? var2 : null;
      }
   }
}

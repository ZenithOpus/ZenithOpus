package org.zenith.utility.render;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class TextureFilterUtils {
   private static final Set<Identifier> items = ConcurrentHashMap.newKeySet();
   private static final Map<GpuTextureView, AddressMode> items2 = Collections.synchronizedMap(new WeakHashMap<>());

   private TextureFilterUtils() {
   }

   public static void smooth(@Nullable Identifier var0) {
      if (var0 != null) {
         items.add(var0);
      }
   }

   public static FilterMode method7268(@Nullable Identifier var0) {
      return var0 != null && items.contains(var0) ? FilterMode.LINEAR : FilterMode.NEAREST;
   }

   @Nullable
   public static GpuTextureView method7269(@Nullable Identifier var0) {
      return var0 == null ? null : method7270(MinecraftClient.getInstance().getTextureManager().getTexture(var0));
   }

   @Nullable
   public static GpuTextureView method7270(@Nullable AbstractTexture var0) {
      if (var0 == null) {
         return null;
      } else {
         GpuTextureView var1 = var0.getGlTextureView();
         items2.put(var1, var0.getSampler().getAddressModeU());
         return var1;
      }
   }

   public static AddressMode method7271(@Nullable GpuTextureView var0) {
      AddressMode var1 = var0 == null ? null : items2.get(var0);
      return var1 != null ? var1 : AddressMode.CLAMP_TO_EDGE;
   }

   @Nullable
   public static GpuTextureView method7272(int var0) {
      return null;
   }

   @Nullable
   public static GpuTextureView method7273(@Nullable GpuTextureView var0) {
      return var0;
   }
}

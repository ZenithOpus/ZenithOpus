package org.zenith.utility.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;

public final class CosmeticTextureCache implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final byte[] CONST_byteValues = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
   private final Map<String, Identifier> items = new HashMap<>();
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Nexus-Cosmetics-PNG");
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   });

   public Identifier method2616(String var1, byte[] var2) {
      Identifier var3 = this.items.get(var1);
      if (var3 != null) {
         return var3;
      } else if (var2 == null || var2.length == 0) {
         return null;
      } else if (!method2620(var2)) {
         logger.warn("Косметика: текстура {} не PNG ({} байт)", var1, var2.length);
         return null;
      } else {
         Identifier var4 = Identifier.of("zenith", "cosmetic/" + var1);
         mc.getTextureManager().registerTexture(var4, method2619(new NativeImage(1, 1, true)));
         this.items.put(var1, var4);
         this.executorService.execute(() -> this.method2617(var1, var4, var2));
         return var4;
      }
   }

   private void method2617(String var1, Identifier var2, byte[] var3) {
      NativeImage var4;
      try {
         var4 = NativeImage.read(var3);
      } catch (RuntimeException | IOException var6) {
         logger.warn("Косметика: текстура {} не читается: {}", var1, var6.toString());
         return;
      }

      mc.execute(() -> this.method2618(var1, var2, var4));
   }

   private void method2618(String var1, Identifier var2, NativeImage var3) {
      if (!var2.equals(this.items.get(var1))) {
         var3.close();
      } else {
         mc.getTextureManager().registerTexture(var2, method2619(var3));
      }
   }

   private static NativeImageBackedTexture method2619(NativeImage var0) {
      return new NativeImageBackedTexture(() -> "zenith", var0) {
         {
            this.sampler = RenderSystem.getSamplerCache()
               .get(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, false);
         }
      };
   }

   private static boolean method2620(byte[] var0) {
      if (var0.length < CONST_byteValues.length) {
         return false;
      } else {
         for (int var1 = 0; var1 < CONST_byteValues.length; var1++) {
            if (var0[var1] != CONST_byteValues[var1]) {
               return false;
            }
         }

         return true;
      }
   }

   public Identifier method2621(String var1) {
      return this.items.get(var1);
   }

   public void method2622(String var1) {
      String var2 = var1 + "/";
      ArrayList<String> var3 = new ArrayList(2);

      for (String var5 : this.items.keySet()) {
         if (var5.equals(var1) || var5.startsWith(var2)) {
            var3.add(var5);
         }
      }

      for (Object var8_o : (Iterable<?>)(Object)(var3)) {
         String var8 = (String)var8_o;
         Identifier var6 = this.items.remove(var8);
         if (var6 != null) {
            mc.getTextureManager().destroyTexture(var6);
         }
      }
   }

   public static String method2623(String var0, String var1) {
      if (var1 != null && !var1.isBlank()) {
         StringBuilder var2 = new StringBuilder(var1.length());

         for (int var3 = 0; var3 < var1.length(); var3++) {
            char var4 = Character.toLowerCase(var1.charAt(var3));
            var2.append((var4 < 'a' || var4 > 'z') && (var4 < '0' || var4 > '9') && var4 != '_' && var4 != '.' && var4 != '-' ? '_' : var4);
         }

         return var0 + "/" + var2;
      } else {
         return var0;
      }
   }

   public void releaseAll() {
      for (String var3 : new ArrayList<>(this.items.keySet())) {
         this.method2622(var3);
      }
   }

   public int size() {
      return this.items.size();
   }
}

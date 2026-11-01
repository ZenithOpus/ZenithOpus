package org.zenith.client.screens.scripts;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;
import org.zenith.client.scripting.Script;
import org.zenith.utility.misc.ImageLoader;

public final class ScriptIcons {
   private static final Map<String, ScriptIcons.Entry> CACHE = new HashMap<>();
   private static final Map<String, Long> CHECKED = new HashMap<>();

   private ScriptIcons() {
   }

   public static Identifier get(Script var0) {
      Path var1 = var0.method1412();
      if (var1 == null) {
         return null;
      } else {
         String var2 = var0.getId();
         long var3 = System.currentTimeMillis();
         ScriptIcons.Entry var5 = CACHE.get(var2);
         Long var6 = CHECKED.get(var2);
         if (var6 != null && var3 - var6 < 1000L) {
            return var5 == null ? null : var5.id();
         } else {
            CHECKED.put(var2, var3);

            long var7;
            try {
               var7 = Files.isRegularFile(var1) ? Files.getLastModifiedTime(var1).toMillis() : 0L;
            } catch (Exception var14) {
               var7 = 0L;
            }

            if (var7 == 0L) {
               drop(var2);
               return null;
            } else if (var5 != null && var5.stamp() == var7) {
               return var5.id();
            } else {
               drop(var2);

               try {
                  if (Files.size(var1) > 4194304L) {
                     return reject(var2, var7);
                  } else {
                     byte[] var9 = Files.readAllBytes(var1);
                     int[] var10 = ImageLoader.method231(var9);
                     if (var10[0] <= 1024 && var10[1] <= 1024) {
                        NativeImage var11 = NativeImage.read(new ByteArrayInputStream(var9));
                        Identifier var12 = Zenith.method1501("script_icon/" + var2.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_") + "_" + var7);
                        MinecraftClient.getInstance()
                           .getTextureManager()
                           .registerTexture(var12, new NativeImageBackedTexture(() -> "zenith-script-icon", var11));
                        CACHE.put(var2, new ScriptIcons.Entry(var7, var12));
                        return var12;
                     } else {
                        return reject(var2, var7);
                     }
                  }
               } catch (Exception var13) {
                  return reject(var2, var7);
               }
            }
         }
      }
   }

   private static Identifier reject(String var0, long var1) {
      CACHE.put(var0, new ScriptIcons.Entry(var1, null));
      return null;
   }

   public static void invalidate(Script var0) {
      CHECKED.remove(var0.getId());
   }

   private static void drop(String var0) {
      ScriptIcons.Entry var1 = CACHE.remove(var0);
      if (var1 != null && var1.id() != null) {
         MinecraftClient.getInstance().getTextureManager().destroyTexture(var1.id());
      }
   }

   private static final class Entry {
      private final long stamp;
      private final Identifier id;

      public long stamp() {
         return this.stamp;
      }

      public Identifier id() {
         return this.id;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Entry other)) return false;
         return java.util.Objects.equals(this.stamp, other.stamp) && java.util.Objects.equals(this.id, other.id);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(stamp, id);
      }

      @Override
      public String toString() {
         return "Entry[" + "stamp=" + this.stamp + ", " + "id=" + this.id + "]";
      }

      private Entry(long var1, Identifier var3) {
         this.stamp = var1;
         this.id = var3;
      }
   }
}

package org.zenith.base.font;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;

public final class ResourceProvider implements MinecraftAccess {
   private static final ResourceManager RESOURCE_MANAGER = mc.getResourceManager();
   private static final Gson GSON = new Gson();

   public static Identifier getShaderIdentifier(String var0) {
      return Zenith.method1501("core/" + var0);
   }

   public static <T> T fromJsonToInstance(Identifier var0, Class<T> var1) {
      return (T)GSON.fromJson(toString(var0), var1);
   }

   public static String toString(Identifier var0) {
      return toString(var0, "\n");
   }

   public static String toString(Identifier var0, String var1) {
      try {
         String var4;
         try (
            InputStream var2 = RESOURCE_MANAGER.open(var0);
            BufferedReader var3 = new BufferedReader(new InputStreamReader(var2));
         ) {
            var4 = var3.lines().collect(Collectors.joining(var1));
         }

         return var4;
      } catch (IOException var10) {
         throw new RuntimeException(var10);
      }
   }
}

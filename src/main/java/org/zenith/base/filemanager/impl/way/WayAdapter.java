package org.zenith.base.filemanager.impl.way;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.math.BlockPos;

public final class WayAdapter implements JsonDeserializer<Way>, JsonSerializer<Way> {
   public JsonElement serialize(Way var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("name", var1.name());
      var4.addProperty("server", var1.server());
      JsonObject var5 = new JsonObject();
      BlockPos var6 = var1.pos();
      var5.addProperty("x", var6.getX());
      var5.addProperty("y", var6.getY());
      var5.addProperty("z", var6.getZ());
      var4.add("pos", var5);
      return var4;
   }

   public Way deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      String var5 = getString(var4, "name", "");
      String var6 = getString(var4, "server", "");
      JsonObject var7 = var4.getAsJsonObject("pos");
      if (var7 == null) {
         throw new JsonParseException("Missing 'pos' for Way");
      } else {
         int var8 = getInt(var7, "x");
         int var9 = getInt(var7, "y");
         int var10 = getInt(var7, "z");
         return new Way(var5, new BlockPos(var8, var9, var10), var6);
      }
   }

   private static String getString(JsonObject var0, String var1, String var2) {
      JsonElement var3 = var0.get(var1);
      return var3 != null && !var3.isJsonNull() ? var3.getAsString() : var2;
   }

   private static int getInt(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      if (var2 != null && !var2.isJsonNull()) {
         return var2.getAsInt();
      } else {
         throw new JsonParseException("Missing int: " + var1);
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;

public class Class1 {
   public static final Class1 INSTANCE = new Class1(Long.MIN_VALUE);
   private final long longValue;

   public Class1(long var1) {
      this.longValue = var1;
   }

   public static Class1 method55(BlockPos var0) {
      return new Class1(var0.asLong());
   }

   public boolean method56() {
      return this.longValue != Long.MIN_VALUE;
   }

   public BlockPos method57() {
      return BlockPos.fromLong(this.longValue);
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("packedPos", this.longValue);
      return var1;
   }

   public static Class1 method58(JsonObject var0) {
      return var0.has("packedPos") ? new Class1(var0.get("packedPos").getAsLong()) : INSTANCE;
   }
}

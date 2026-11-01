package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.misc.CosmeticType;
import org.zenith.utility.misc.Util31;

public final class Overrides {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final String text = "overrides.json";
   private static final String text2 = "/assets/zenith/cosmetics/overrides.json";
   private final Path path;
   private volatile Map<String, Overrides.Data> entries = Map.of();
   private volatile JsonObject jsonObject;
   private volatile JsonObject jsonObject2 = new JsonObject();
   private volatile int intValue;
   private volatile int intValue2;
   private volatile int intValue3;

   public Overrides(Path var1) {
      this.path = var1.resolve("overrides.json");
      JsonObject var2 = JsonIo.method1009(this.path);
      if (var2 != null) {
         this.jsonObject2 = var2;
      }

      this.load();
   }

   public Overrides.Data method6458(String var1) {
      return var1 == null ? null : this.entries.get(var1);
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public boolean method6459(String var1) {
      Overrides.Data var2 = this.method6458(var1);
      return var2 != null && var2.isFlag();
   }

   public String method6460(CosmeticItemInfo var1) {
      if (var1 == null) {
         return "";
      } else {
         Overrides.Data var2 = this.method6458(var1.getText());
         return var2 != null && var2.name() != null && !var2.name().isBlank() ? var2.name() : var1.method7749();
      }
   }

   public String method6461(CosmeticItemInfo var1) {
      if (var1 == null) {
         return "";
      } else {
         Overrides.Data var2 = this.method6458(var1.getText());
         if (var2 != null && var2.getText2() != null && !var2.getText2().isBlank()) {
            return "-".equals(var2.getText2()) ? "" : var2.getText2();
         } else {
            return var1.getText5();
         }
      }
   }

   public int size() {
      return this.entries.size();
   }

   public Path getPath() {
      return this.path;
   }

   public CosmeticType method6462(CosmeticItemInfo var1) {
      if (var1 == null) {
         return CosmeticType.UNKNOWN;
      } else {
         Overrides.Data var2 = this.method6458(var1.getText());
         return var2 != null && var2.getCosmeticType() != null ? var2.getCosmeticType() : var1.getCosmeticType();
      }
   }

   public Util31.Data method6463(String var1, Util31.Data var2) {
      Overrides.Data var3 = this.method6458(var1);
      return var3 != null && var2 != null && !var3.empty()
         ? new Util31.Data(
            var2.getFloatValue() + var3.getFloatValues()[0],
            var2.getFloatValue2() + var3.getFloatValues()[1],
            var2.getFloatValue3() + var3.getFloatValues()[2],
            var2.getFloatValue4() + var3.getFloatValues2()[0],
            var2.getFloatValue5() + var3.getFloatValues2()[1],
            var2.getFloatValue6() + var3.getFloatValues2()[2],
            var2.getFloatValue7() * var3.getFloatValues3()[0],
            var2.getFloatValue8() * var3.getFloatValues3()[1],
            var2.getFloatValue9() * var3.getFloatValues3()[2],
            var2.isFlag()
         )
         : var2;
   }

   public void method6464(String var1, Overrides.Data var2) {
      if (var1 != null && !var1.isBlank()) {
         JsonObject var3 = this.jsonObject2.deepCopy();
         if (var2 != null && !var2.empty()) {
            var3.add(var1, method6469(var2));
         } else {
            var3.add(var1, new JsonObject());
         }

         this.jsonObject2 = var3;
         this.save();
         this.load();
      }
   }

   public void clear() {
      this.jsonObject2 = new JsonObject();
      this.save();
      this.load();
   }

   private void load() {
      HashMap var1 = new HashMap();
      this.intValue = method6467(method6468(), var1);
      this.intValue2 = method6467(this.jsonObject, var1);
      this.intValue3 = method6467(this.jsonObject2, var1);
      this.entries = Map.copyOf(var1);
      logger.info("Косметика: подгонка посадки — {} из сборки, {} с узла, {} своих", new Object[]{this.intValue, this.intValue2, this.intValue3});
   }

   public String method6465() {
      return this.intValue + " из сборки, " + this.intValue2 + " с узла, " + this.intValue3 + " своих";
   }

   public int method6466(byte[] var1) {
      if (var1 != null && var1.length != 0) {
         try {
            JsonElement var2 = JsonParser.parseString(new String(var1, StandardCharsets.UTF_8));
            if (!var2.isJsonObject()) {
               return -1;
            } else {
               this.jsonObject = var2.getAsJsonObject();
               this.load();
               return this.jsonObject.size();
            }
         } catch (RuntimeException var3) {
            logger.warn("Косметика: подгонка с узла не разобралась: {}", var3.toString());
            return -1;
         }
      } else {
         return -1;
      }
   }

   public JsonObject getJsonObject2() {
      return this.jsonObject2;
   }

   private static int method6467(JsonObject var0, Map<String, Overrides.Data> var1) {
      if (var0 == null) {
         return 0;
      } else {
         int var2 = 0;

         for (Entry var4 : var0.entrySet()) {
            if (((JsonElement)var4.getValue()).isJsonObject()) {
               JsonObject var5 = ((JsonElement)var4.getValue()).getAsJsonObject();
               Overrides.Data var6 = new Overrides.Data(
                  method6471(var5),
                  method6473(var5, "offset", 0.0F),
                  method6473(var5, "rotation", 0.0F),
                  method6473(var5, "scale", 1.0F),
                  var5.has("hidden") && var5.get("hidden").isJsonPrimitive() && var5.get("hidden").getAsBoolean(),
                  method6472(var5, "name"),
                  method6472(var5, "rarity")
               );
               var2++;
               if (var6.empty()) {
                  var1.remove(var4.getKey());
               } else {
                  var1.put((String)var4.getKey(), var6);
               }
            }
         }

         return var2;
      }
   }

   private static JsonObject method6468() {
      try {
         JsonObject var1;
         try (InputStream var0 = Overrides.class.getResourceAsStream("/assets/zenith/cosmetics/overrides.json")) {
            if (var0 == null) {
               return null;
            }

            var1 = JsonParser.parseString(new String(var0.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
         }

         return var1;
      } catch (Exception var5) {
         logger.warn("Косметика: подгонка из сборки не прочиталась: {}", var5.toString());
         return null;
      }
   }

   private void save() {
      JsonIo.method1010(this.path, this.jsonObject2);
   }

   private static JsonObject method6469(Overrides.Data var0) {
      JsonObject var1 = new JsonObject();
      if (var0.getCosmeticType() != null) {
         var1.addProperty("type", var0.getCosmeticType().name());
      }

      if (var0.isFlag()) {
         var1.addProperty("hidden", true);
      }

      if (var0.name() != null && !var0.name().isBlank()) {
         var1.addProperty("name", var0.name());
      }

      if (var0.getText2() != null && !var0.getText2().isBlank()) {
         var1.addProperty("rarity", var0.getText2());
      }

      var1.add("offset", method6470(var0.getFloatValues()));
      var1.add("rotation", method6470(var0.getFloatValues2()));
      var1.add("scale", method6470(var0.getFloatValues3()));
      return var1;
   }

   private static JsonArray method6470(float[] var0) {
      JsonArray var1 = new JsonArray(3);

      for (float var5 : var0) {
         var1.add(var5);
      }

      return var1;
   }

   private static CosmeticType method6471(JsonObject var0) {
      if (var0.has("type") && var0.get("type").isJsonPrimitive()) {
         try {
            CosmeticType var1 = CosmeticType.valueOf(var0.get("type").getAsString().strip().toUpperCase(Locale.ROOT));
            return var1 == CosmeticType.UNKNOWN ? null : var1;
         } catch (RuntimeException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String method6472(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : "";
      } catch (RuntimeException var3) {
         return "";
      }
   }

   private static float[] method6473(JsonObject var0, String var1, float var2) {
      float[] var3 = new float[]{var2, var2, var2};
      if (var0.has(var1) && var0.get(var1).isJsonArray()) {
         JsonArray var4 = var0.getAsJsonArray(var1);

         for (int var5 = 0; var5 < 3 && var5 < var4.size(); var5++) {
            try {
               var3[var5] = var4.get(var5).getAsFloat();
            } catch (RuntimeException var7) {
            }
         }

         return var3;
      } else {
         return var3;
      }
   }

   public static final class Data {
      private final CosmeticType cosmeticType;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final boolean flag;
      private final String text;
      private final String text2;

      public CosmeticType cosmeticType() {
         return this.cosmeticType;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      public boolean flag() {
         return this.flag;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.cosmeticType, other.cosmeticType) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(cosmeticType, floatValues, floatValues2, floatValues3, flag, text, text2);
      }

      @Override
      public String toString() {
         return "Data[" + "cosmeticType=" + this.cosmeticType + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "flag=" + this.flag + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }
      public static final float[] CONST_floatValues = new float[]{0.0F, 0.0F, 0.0F};
      public static final float[] CONST_floatValues2 = new float[]{1.0F, 1.0F, 1.0F};

      public Data(CosmeticType var1, float[] var2, float[] var3, float[] var4) {
         this(var1, var2, var3, var4, false, "", "");
      }

      public Data(CosmeticType var1, float[] var2, float[] var3, float[] var4, boolean var5) {
         this(var1, var2, var3, var4, var5, "", "");
      }

      public Data(CosmeticType var1, float[] var2, float[] var3, float[] var4, boolean var5, String var6, String var7) {
         this.cosmeticType = var1;
         this.floatValues = var2;
         this.floatValues2 = var3;
         this.floatValues3 = var4;
         this.flag = var5;
         this.text = var6;
         this.text2 = var7;
      }

      public boolean empty() {
         return !this.flag
            && this.cosmeticType == null
            && method6474(this.text)
            && method6474(this.text2)
            && method6475(this.floatValues, CONST_floatValues)
            && method6475(this.floatValues2, CONST_floatValues)
            && method6475(this.floatValues3, CONST_floatValues2);
      }

      private static boolean method6474(String var0) {
         return var0 == null || var0.isBlank();
      }

      private static boolean method6475(float[] var0, float[] var1) {
         return var0[0] == var1[0] && var0[1] == var1[1] && var0[2] == var1[2];
      }

      public CosmeticType getCosmeticType() {
         return this.cosmeticType;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public float[] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }
}

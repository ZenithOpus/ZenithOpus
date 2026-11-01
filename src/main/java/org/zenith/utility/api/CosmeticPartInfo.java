package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.utility.misc.CosmeticType;

public final class CosmeticPartInfo {
   private final String text;
   private final String text2;
   private final String text3;
   private final String text4;
   private final CosmeticType cosmeticType;
   private final CosmeticPartInfo.Type type;
   private final List<String> items;
   private final List<String> items2;
   private final String text5;
   private final int intValue;
   private final CosmeticPartInfo.Data data;
   private final CosmeticPartInfo.Data data2;
   private final String text6;

   public String text() {
      return this.text;
   }

   public String text2() {
      return this.text2;
   }

   public String text3() {
      return this.text3;
   }

   public String text4() {
      return this.text4;
   }

   public CosmeticType cosmeticType() {
      return this.cosmeticType;
   }

   public CosmeticPartInfo.Type type() {
      return this.type;
   }

   public List<String> items() {
      return this.items;
   }

   public List<String> items2() {
      return this.items2;
   }

   public String text5() {
      return this.text5;
   }

   public int intValue() {
      return this.intValue;
   }

   public CosmeticPartInfo.Data data() {
      return this.data;
   }

   public CosmeticPartInfo.Data data2() {
      return this.data2;
   }

   public String text6() {
      return this.text6;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CosmeticPartInfo other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.cosmeticType, other.cosmeticType) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.data, other.data) && java.util.Objects.equals(this.data2, other.data2) && java.util.Objects.equals(this.text6, other.text6);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, text3, text4, cosmeticType, type, items, items2, text5, intValue, data, data2, text6);
   }

   @Override
   public String toString() {
      return "CosmeticPartInfo[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "cosmeticType=" + this.cosmeticType + ", " + "type=" + this.type + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + ", " + "text5=" + this.text5 + ", " + "intValue=" + this.intValue + ", " + "data=" + this.data + ", " + "data2=" + this.data2 + ", " + "text6=" + this.text6 + "]";
   }

   public CosmeticPartInfo(
      String var1,
      String var2,
      String var3,
      String var4,
      CosmeticType var5,
      CosmeticPartInfo.Type var6,
      List<String> var7,
      List<String> var8,
      String var9,
      int var10,
      CosmeticPartInfo.Data var11,
      CosmeticPartInfo.Data var12,
      String var13
   ) {
      this.text = var1;
      this.text2 = var2;
      this.text3 = var3;
      this.text4 = var4;
      this.cosmeticType = var5;
      this.type = var6;
      this.items = var7;
      this.items2 = var8;
      this.text5 = var9;
      this.intValue = var10;
      this.data = var11;
      this.data2 = var12;
      this.text6 = var13;
   }

   public boolean method6492() {
      return this.cosmeticType.method1682() && this.type != CosmeticPartInfo.Type.UNKNOWN;
   }

   public String method6493() {
      return this.text2 != null && !this.text2.isBlank() ? this.text2 : this.text4;
   }

   public static CosmeticPartInfo method6494(JsonObject var0, String var1) {
      if (var0 == null) {
         return null;
      } else {
         String var2 = method6496(var0, "id", "");
         if (var2.isBlank()) {
            return null;
         } else {
            JsonObject var3 = var0.has("locationRotation") && var0.get("locationRotation").isJsonObject() ? var0.getAsJsonObject("locationRotation") : null;
            return new CosmeticPartInfo(
               var2,
               method6496(var0, "name", ""),
               method6496(var0, "categoryId", ""),
               var1,
               CosmeticType.method1684(method6496(var0, "type", "")),
               CosmeticPartInfo.Type.method6499(method6496(var0, "sourceFormat", "")),
               method6498(var0, "conflictGroups"),
               method6498(var0, "hidePartBody"),
               method6496(var0, "rarity", ""),
               method6497(var0, "price"),
               CosmeticPartInfo.Data.method6500(method6495(var3, "normal"), CosmeticPartInfo.Data.INSTANCE),
               CosmeticPartInfo.Data.method6500(method6495(var3, "gui"), CosmeticPartInfo.Data.INSTANCE),
               method6496(var0, "hash256", "")
            );
         }
      }
   }

   private static JsonObject method6495(JsonObject var0, String var1) {
      return var0 != null && var0.has(var1) && var0.get(var1).isJsonObject() ? var0.getAsJsonObject(var1) : null;
   }

   private static String method6496(JsonObject var0, String var1, String var2) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static int method6497(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsInt() : 0;
      } catch (RuntimeException var3) {
         return 0;
      }
   }

   private static List<String> method6498(JsonObject var0, String var1) {
      ArrayList var2 = new ArrayList(2);
      if (!var0.has(var1)) {
         return List.copyOf(var2);
      } else {
         JsonElement var3 = var0.get(var1);

         try {
            if (var3.isJsonArray()) {
               for (JsonElement var5 : var3.getAsJsonArray()) {
                  if (var5.isJsonPrimitive()) {
                     var2.add(var5.getAsString());
                  }
               }
            } else if (var3.isJsonPrimitive()) {
               var2.add(var3.getAsString());
            }
         } catch (RuntimeException var6) {
         }

         return List.copyOf(var2);
      }
   }

   public String getText() {
      return this.text;
   }

   public String name() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public String getText4() {
      return this.text4;
   }

   public CosmeticType getCosmeticType() {
      return this.cosmeticType;
   }

   public CosmeticPartInfo.Type getType() {
      return this.type;
   }

   public List<String> getItems() {
      return this.items;
   }

   public List<String> getItems2() {
      return this.items2;
   }

   public String getText5() {
      return this.text5;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public CosmeticPartInfo.Data getData() {
      return this.data;
   }

   public CosmeticPartInfo.Data getData2() {
      return this.data2;
   }

   public String getText6() {
      return this.text6;
   }

   public static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + "]";
      }
      public static final CosmeticPartInfo.Data INSTANCE = new CosmeticPartInfo.Data(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

      public Data(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
         this.floatValue9 = var9;
      }

      public static CosmeticPartInfo.Data method6500(JsonObject var0, CosmeticPartInfo.Data var1) {
         if (var0 == null) {
            return var1;
         } else {
            float[] var2 = method6501(var0.get("location"), 0.0F);
            float[] var3 = method6501(var0.get("rotation"), 0.0F);
            float[] var4 = method6501(var0.get("scale"), 1.0F);
            return new CosmeticPartInfo.Data(var2[0], var2[1], var2[2], var3[0], var3[1], var3[2], var4[0], var4[1], var4[2]);
         }
      }

      private static float[] method6501(JsonElement var0, float var1) {
         float[] var2 = new float[]{var1, var1, var1};
         if (var0 != null && var0.isJsonArray()) {
            JsonArray var3 = var0.getAsJsonArray();

            for (int var4 = 0; var4 < 3 && var4 < var3.size(); var4++) {
               try {
                  var2[var4] = var3.get(var4).getAsFloat();
               } catch (RuntimeException var6) {
               }
            }

            return var2;
         } else {
            return var2;
         }
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }
   }

   public static enum Type {
      WAVEFRONT,
      BEDROCK,
      TECHNE,
      UNKNOWN;

      public static CosmeticPartInfo.Type method6499(String var0) {
         if (var0 == null) {
            return UNKNOWN;
         } else {
            String var1 = var0.strip().toLowerCase(Locale.ROOT);

            return switch (var1) {
               case "wavefront" -> WAVEFRONT;
               case "bedrock" -> BEDROCK;
               case "techne2", "techne" -> TECHNE;
               default -> UNKNOWN;
            };
         }
      }
   }
}

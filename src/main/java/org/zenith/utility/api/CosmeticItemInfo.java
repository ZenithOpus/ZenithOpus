package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.utility.misc.CosmeticType;

public final class CosmeticItemInfo {
   private final String text;
   private final String text2;
   private final String text3;
   private final String text4;
   private final CosmeticType cosmeticType;
   private final CosmeticPartInfo.Type type;
   private final String text5;
   private final int intValue;
   private final String text6;
   private final int intValue2;
   private final String text7;
   private final List<String> items;
   private final List<String> items2;
   private final CosmeticPartInfo.Data data;
   private final CosmeticPartInfo.Data data2;

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

   public String text5() {
      return this.text5;
   }

   public int intValue() {
      return this.intValue;
   }

   public String text6() {
      return this.text6;
   }

   public int intValue2() {
      return this.intValue2;
   }

   public String text7() {
      return this.text7;
   }

   public List<String> items() {
      return this.items;
   }

   public List<String> items2() {
      return this.items2;
   }

   public CosmeticPartInfo.Data data() {
      return this.data;
   }

   public CosmeticPartInfo.Data data2() {
      return this.data2;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CosmeticItemInfo other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.cosmeticType, other.cosmeticType) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text6, other.text6) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.text7, other.text7) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.data, other.data) && java.util.Objects.equals(this.data2, other.data2);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, text3, text4, cosmeticType, type, text5, intValue, text6, intValue2, text7, items, items2, data, data2);
   }

   @Override
   public String toString() {
      return "CosmeticItemInfo[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "cosmeticType=" + this.cosmeticType + ", " + "type=" + this.type + ", " + "text5=" + this.text5 + ", " + "intValue=" + this.intValue + ", " + "text6=" + this.text6 + ", " + "intValue2=" + this.intValue2 + ", " + "text7=" + this.text7 + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + ", " + "data=" + this.data + ", " + "data2=" + this.data2 + "]";
   }
   private static final int CONST_intValue = 9;

   public CosmeticItemInfo(
      String var1,
      String var2,
      String var3,
      String var4,
      CosmeticType var5,
      CosmeticPartInfo.Type var6,
      String var7,
      int var8,
      String var9,
      int var10,
      String var11,
      List<String> var12,
      List<String> var13,
      CosmeticPartInfo.Data var14,
      CosmeticPartInfo.Data var15
   ) {
      this.text = var1;
      this.text2 = var2;
      this.text3 = var3;
      this.text4 = var4;
      this.cosmeticType = var5;
      this.type = var6;
      this.text5 = var7;
      this.intValue = var8;
      this.text6 = var9;
      this.intValue2 = var10;
      this.text7 = var11;
      this.items = var12;
      this.items2 = var13;
      this.data = var14;
      this.data2 = var15;
   }

   public static CosmeticItemInfo method7748(JsonObject var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = method7754(var0, "id");
         String var2 = method7754(var0, "hash");
         return !var1.isBlank() && !var2.isBlank()
            ? new CosmeticItemInfo(
               var1,
               method7754(var0, "name"),
               method7754(var0, "category"),
               method7754(var0, "categoryId"),
               CosmeticType.method1684(method7754(var0, "type")),
               CosmeticPartInfo.Type.method6499(method7754(var0, "format")),
               method7754(var0, "rarity"),
               method7755(var0, "price"),
               var2,
               method7755(var0, "size"),
               method7754(var0, "texture"),
               method7756(var0, "conflictGroups"),
               method7756(var0, "hiddenParts"),
               method7753(var0, "normal"),
               method7753(var0, "gui")
            )
            : null;
      }
   }

   public String method7749() {
      if (this.text2 != null && !this.text2.isBlank()) {
         return this.text2;
      } else {
         return this.text3 != null && !this.text3.isBlank() ? this.text3 : this.text;
      }
   }

   public boolean method7750() {
      return this.cosmeticType.method1682() && this.type != CosmeticPartInfo.Type.UNKNOWN;
   }

   public boolean method7751() {
      if (!this.method7752()) {
         return false;
      } else {
         boolean var1 = false;
         boolean var2 = false;

         for (int var3 = 0; var3 < this.items.size(); var3++) {
            String var4 = this.items.get(var3);
            if (var4 != null) {
               String var5 = var4.toLowerCase(Locale.ROOT);
               var1 |= var5.startsWith("left");
               var2 |= var5.startsWith("right");
            }
         }

         return var1 && var2;
      }
   }

   public boolean method7752() {
      return this.cosmeticType == CosmeticType.LEFT_ARM
         || this.cosmeticType == CosmeticType.RIGHT_ARM
         || this.cosmeticType == CosmeticType.LEFT_LEG
         || this.cosmeticType == CosmeticType.RIGHT_LEG;
   }

   private static CosmeticPartInfo.Data method7753(JsonObject var0, String var1) {
      if (var0.has(var1) && var0.get(var1).isJsonArray()) {
         JsonArray var2 = var0.getAsJsonArray(var1);
         if (var2.size() < 9) {
            return CosmeticPartInfo.Data.INSTANCE;
         } else {
            float[] var3 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};

            for (int var4 = 0; var4 < 9; var4++) {
               try {
                  var3[var4] = var2.get(var4).getAsFloat();
               } catch (RuntimeException var6) {
               }
            }

            return new CosmeticPartInfo.Data(var3[0], var3[1], var3[2], var3[3], var3[4], var3[5], var3[6], var3[7], var3[8]);
         }
      } else {
         return CosmeticPartInfo.Data.INSTANCE;
      }
   }

   private static String method7754(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : "";
      } catch (RuntimeException var3) {
         return "";
      }
   }

   private static int method7755(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsInt() : 0;
      } catch (RuntimeException var3) {
         return 0;
      }
   }

   private static List<String> method7756(JsonObject var0, String var1) {
      if (var0.has(var1) && var0.get(var1).isJsonArray()) {
         ArrayList var2 = new ArrayList(2);

         for (JsonElement var4 : var0.getAsJsonArray(var1)) {
            if (var4.isJsonPrimitive()) {
               var2.add(var4.getAsString());
            }
         }

         return List.copyOf(var2);
      } else {
         return List.of();
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

   public String getText5() {
      return this.text5;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public String getText6() {
      return this.text6;
   }

   public int size() {
      return this.intValue2;
   }

   public String getText7() {
      return this.text7;
   }

   public List<String> getItems() {
      return this.items;
   }

   public List<String> getItems2() {
      return this.items2;
   }

   public CosmeticPartInfo.Data getData() {
      return this.data;
   }

   public CosmeticPartInfo.Data getData2() {
      return this.data2;
   }
}

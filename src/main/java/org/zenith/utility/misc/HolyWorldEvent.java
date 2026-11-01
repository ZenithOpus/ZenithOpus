package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;

public class HolyWorldEvent {
   private static final Pattern pattern = Pattern.compile("(\\([^)]+\\))?\\s*#(\\d+)");
   private final HolyWorldEvent.Type type;
   private final String text;
   private final long longValue;
   private final String text2;

   public HolyWorldEvent(String var1, String var2, long var3) {
      this.type = HolyWorldEvent.Type.method7746(var1);
      this.text = var2;
      this.longValue = var3;
      this.text2 = this.method7744();
   }

   public String method7742() {
      return this.getType().getText();
   }

   public String getIcon() {
      return this.getType().getIcon();
   }

   public boolean method7743() {
      Matcher var1 = pattern.matcher(this.text);

      while (var1.find()) {
         String var2 = var1.group(2);
         if (var2 != null && var2.length() > 0) {
            try {
               int var3 = Integer.parseInt(var2);
               if (var3 > 0) {
                  return true;
               }
            } catch (NumberFormatException var4) {
               return true;
            }
         }
      }

      return false;
   }

   private String method7744() {
      ArrayList<HolyWorldEvent.Data> var1 = new ArrayList();
      Matcher var2 = pattern.matcher(this.text);

      while (var2.find()) {
         String var3 = var2.group(1);
         String var4 = var2.group(2);
         if (var4 != null && var4.length() > 0) {
            try {
               int var5 = Integer.parseInt(var4);
               if (var5 > 0) {
                  var1.add(new HolyWorldEvent.Data(var5, var3));
               }
            } catch (NumberFormatException var13) {
            }
         }
      }

      if (var1.isEmpty()) {
         return "";
      } else {
         var1.sort(Comparator.comparingInt(var0 -> var0.intValue));
         int var14 = var1.size();
         ArrayList var15 = new ArrayList();
         ArrayList var16 = new ArrayList();
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();

         for (Object var9_o : (Iterable<?>)(Object)(var1)) {
            HolyWorldEvent.Data var9 = (HolyWorldEvent.Data)var9_o;
            if (var9.intValue >= 1 && var9.intValue <= 14) {
               var15.add(var9);
            } else if (var9.intValue >= 15 && var9.intValue <= 32) {
               var16.add(var9);
            } else if (var9.intValue >= 33 && var9.intValue <= 47) {
               var6.add(var9);
            } else if (var9.intValue >= 48) {
               var7.add(var9);
            }
         }

         ArrayList var17 = new ArrayList();
         byte var18 = 4;
         if (var14 <= 4) {
            for (Object var11_o : (Iterable<?>)(Object)(var1)) {
               HolyWorldEvent.Data var11 = (HolyWorldEvent.Data)var11_o;
               var17.add(this.method7745(var11));
            }
         } else {
            ArrayList var19 = new ArrayList();
            var19.add(var15);
            var19.add(var16);
            var19.add(var6);
            var19.add(var7);
            int[] var21 = new int[4];

            for (int var12 = 0; var12 < 4 && var17.size() < var18; var12++) {
               if (!((List)var19.get(var12)).isEmpty()) {
                  var17.add(this.method7745((HolyWorldEvent.Data)((List)var19.get(var12)).get(0)));
                  var21[var12] = 1;
               }
            }

            for (int var22 = 0; var22 < 4 && var17.size() < var18; var22++) {
               while (var21[var22] < ((List)var19.get(var22)).size() && var17.size() < var18) {
                  var17.add(this.method7745((HolyWorldEvent.Data)((List)var19.get(var22)).get(var21[var22])));
                  var21[var22]++;
               }
            }
         }

         String var20 = String.join(", ", var17);
         if (var14 > 4) {
            var20 = var20 + "...";
         }

         return var20;
      }
   }

   private String method7745(HolyWorldEvent.Data var1) {
      return var1.text != null && !var1.text.isEmpty() ? var1.intValue + " " + var1.text : String.valueOf(var1.intValue);
   }

   @Generated
   public HolyWorldEvent.Type getType() {
      return this.type;
   }

   @Generated
   public String getMessage() {
      return this.text;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   private static final class Data {
      private final int intValue;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + "]";
      }

      private Data(int var1, String var2) {
         this.intValue = var1;
         this.text = var2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String version() {
         return this.text;
      }
   }

   public static enum Type {
      CARGO("Груз", "C"),
      BOSS("Босс", "B"),
      CUBE("Кубик", "B"),
      CONTAINER("Контейнер", "D"),
      GOLD_RUSH("Золотая лихорадка", "E"),
      PACKAGE("Посылка", "F"),
      SHIP("Корабль", "G"),
      FLOWER("Цветочная поляна", "H"),
      FLOWER_FIELD("Цветочная поляна", "H"),
      MINE("Смертельная шахта", "I"),
      DEADLY_MINE("Смертельная шахта", "I"),
      EXPERT("Опытный Тыпо", "J"),
      VOTING("Голосование", "K"),
      UNKNOWN("Неизвестно", "A");

      private final String text;
      private final String text2;

      private Type(String var3, String var4) {
         this.text = var3;
         this.text2 = var4;
      }

      public static HolyWorldEvent.Type method7746(String var0) {
         if (var0 == null) {
            return UNKNOWN;
         } else {
            String var1 = var0.toUpperCase(Locale.ROOT);
            switch (var1) {
               case "GOLDEN_FORTRESS":
                  return GOLD_RUSH;
               case "PARCELS":
                  return PACKAGE;
               case "SNOWQUARRY":
                  return FLOWER_FIELD;
               case "JAYCOB":
                  return EXPERT;
               default:
                  try {
                     return valueOf(var1);
                  } catch (IllegalArgumentException var4) {
                     return UNKNOWN;
                  }
            }
         }
      }

      @Generated
      public String getText() {
         return this.text;
      }

      @Generated
      public String getIcon() {
         return this.text2;
      }
   }
}

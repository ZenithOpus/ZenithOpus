package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Coord {
   private static final String text = "[z:";
   private static final char CONST_charValue = ']';
   private static final int CONST_intValue = 900;
   public static final String text2 = "coord";
   public static final String text3 = "inv";
   public static final String text4 = "st";
   public static final String text5 = "at";
   private final String text6;
   private final Map<String, String> items;

   private Coord(String var1, Map<String, String> var2) {
      this.text6 = var1;
      this.items = var2;
   }

   public String kind() {
      return this.text6;
   }

   public String method5169(String var1, String var2) {
      String var3 = this.items.get(var1);
      return var3 != null && !var3.isEmpty() ? var3 : var2;
   }

   public int method5170(String var1, int var2) {
      try {
         String var3 = this.items.get(var1);
         return var3 != null && !var3.isEmpty() ? Integer.parseInt(var3.trim()) : var2;
      } catch (NumberFormatException var4) {
         return var2;
      }
   }

   public static Coord.Entry method5171(String var0) {
      return new Coord.Entry(var0);
   }

   public static Coord.Data method5172(String var0) {
      if (var0 == null || var0.isEmpty()) {
         return new Coord.Data("", List.of());
      } else if (var0.indexOf("[z:") < 0) {
         return new Coord.Data(var0, List.of());
      } else {
         ArrayList var1 = new ArrayList();
         StringBuilder var2 = new StringBuilder();
         boolean var3 = false;
         int var4 = 0;

         while (var4 < var0.length()) {
            int var5 = var0.indexOf("[z:", var4);
            if (var5 < 0) {
               var2.append(var0, var4, var0.length());
               break;
            }

            int var6 = var0.indexOf(93, var5);
            if (var6 < 0 || var6 - var5 > 900) {
               if (method5173(var0, var5)) {
                  var2.append(var0, var4, var5);
                  var3 = true;
               } else {
                  var2.append(var0, var4, var0.length());
               }
               break;
            }

            Coord var7 = method5175(var0.substring(var5 + "[z:".length(), var6));
            if (var7 == null) {
               var2.append(var0, var4, var6 + 1);
            } else {
               var2.append(var0, var4, var5);
               var1.add(var7);
            }

            var4 = var6 + 1;
         }

         return new Coord.Data(var2.toString().strip(), var1, var3);
      }
   }

   private static boolean method5173(String var0, int var1) {
      int var2 = var1 + "[z:".length();

      int var3;
      for (var3 = 0; var2 < var0.length() && var0.charAt(var2) >= 'a' && var0.charAt(var2) <= 'z'; var3++) {
         var2++;
      }

      return var3 > 0 && var2 < var0.length() && var0.charAt(var2) == '|';
   }

   public static String method5174(String var0, int var1) {
      if (var0 != null && var1 > 0) {
         if (var0.length() <= var1) {
            return var0;
         } else {
            int var2 = var1;
            int var3 = 0;

            while (var3 < var0.length()) {
               int var4 = var0.indexOf("[z:", var3);
               if (var4 < 0 || var4 >= var2) {
                  break;
               }

               int var5 = var0.indexOf(93, var4);
               if (var5 < 0 || var5 >= var2) {
                  var2 = var4;
                  break;
               }

               var3 = var5 + 1;
            }

            return var0.substring(0, Math.max(0, var2)).stripTrailing();
         }
      } else {
         return "";
      }
   }

   private static Coord method5175(String var0) {
      if (var0.isEmpty()) {
         return null;
      } else {
         String[] var1 = var0.split("\\|");
         String var2 = var1[0].trim().toLowerCase(Locale.ROOT);
         if (!var2.isEmpty() && var2.chars().allMatch(var0x -> var0x >= 97 && var0x <= 122)) {
            LinkedHashMap var3 = new LinkedHashMap();

            for (int var4 = 1; var4 < var1.length; var4++) {
               int var5 = var1[var4].indexOf(61);
               if (var5 > 0) {
                  var3.put(var1[var4].substring(0, var5).trim().toLowerCase(Locale.ROOT), method5176(var1[var4].substring(var5 + 1)));
               }
            }

            return new Coord(var2, var3);
         } else {
            return null;
         }
      }
   }

   private static String escape(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() + 8);

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         switch (var3) {
            case '\t':
            case '\n':
            case '\r':
               var1.append(' ');
               break;
            case '%':
               var1.append("%25");
               break;
            case '[':
               var1.append("%5B");
               break;
            case ']':
               var1.append("%5D");
               break;
            case '|':
               var1.append("%7C");
               break;
            default:
               var1.append(var3);
         }
      }

      return var1.toString();
   }

   private static String method5176(String var0) {
      if (var0.indexOf(37) < 0) {
         return var0;
      } else {
         StringBuilder var1 = new StringBuilder(var0.length());

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            if (var3 == '%' && var2 + 2 < var0.length()) {
               try {
                  var1.append((char)Integer.parseInt(var0.substring(var2 + 1, var2 + 3), 16));
                  var2 += 2;
                  continue;
               } catch (NumberFormatException var5) {
               }
            }

            var1.append(var3);
         }

         return var1.toString();
      }
   }

   public static final class Data {
      private final String text;
      private final List<Coord> items;
      private final boolean broken;

      public String text() {
         return this.text;
      }

      public List<Coord> items() {
         return this.items;
      }

      public boolean broken() {
         return this.broken;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.broken, other.broken);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, broken);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "broken=" + this.broken + "]";
      }

      public Data(String var1, List<Coord> var2) {
         this(var1, var2, false);
      }

      public Data(String var1, List<Coord> var2, boolean var3) {
         this.text = var1;
         this.items = var2;
         this.broken = var3;
      }

      public Coord method5179(String var1) {
         for (Coord var3 : this.items) {
            if (var3.kind().equals(var1)) {
               return var3;
            }
         }

         return null;
      }

      public boolean method5180(String var1) {
         return this.method5179(var1) != null;
      }

      public List<Coord> getItems() {
         return this.items;
      }

      public boolean isBroken() {
         return this.broken;
      }
   }

   public static final class Entry {
      private final String text;
      private final Map<String, String> items = new LinkedHashMap<>();

      private Entry(String var1) {
         this.text = var1;
      }

      public Coord.Entry method5177(String var1, String var2) {
         if (var2 != null && !var2.isBlank()) {
            this.items.put(var1, var2.strip());
         }

         return this;
      }

      public Coord.Entry method5178(String var1, int var2) {
         this.items.put(var1, Integer.toString(var2));
         return this;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder("[z:").append(this.text);

         for (Map.Entry var3 : this.items.entrySet()) {
            var1.append('|').append((String)var3.getKey()).append('=').append(Coord.escape((String)var3.getValue()));
         }

         return var1.append(']').toString();
      }
   }
}

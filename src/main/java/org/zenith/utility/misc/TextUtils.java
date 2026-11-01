package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import lombok.Generated;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.MinecraftAccess;

public final class TextUtils implements MinecraftAccess {
   private static final String text = "ёйцукенгшщзхъфывапролджэячсмитьбю";
   private static final String text2 = "`qwertyuiop[]asdfghjkl;'zxcvbnm,.";

   public static String method2104(double var0) {
      return String.format(Locale.US, "%.1f", var0);
   }

   public static Text method2105(Text var0) {
      OrderedText var1 = var0.asOrderedText();
      MutableText var2 = Text.empty();
      StringBuilder var3 = new StringBuilder();
      Style[] var4 = new Style[]{Style.EMPTY};
      boolean[] var5 = new boolean[]{true};
      var1.accept((var4x, var5x, var6) -> {
         if (var5[0] && Character.isWhitespace(var6)) {
            return true;
         } else {
            var5[0] = false;
            if (!var5x.equals(var4[0])) {
               if (var3.length() > 0) {
                  var2.append(Text.literal(var3.toString()).setStyle(var4[0]));
                  var3.setLength(0);
               }

               var4[0] = var5x;
            }

            var3.appendCodePoint(var6);
            return true;
         }
      });
      if (var3.length() > 0) {
         var2.append(Text.literal(var3.toString()).setStyle(var4[0]));
      }

      return var2;
   }

   public static MutableText method2106(Text var0, Text var1, Text var2) {
      String var3 = var0.getString();
      String var4 = var1.getString();
      if (var4 != null && !var4.isEmpty()) {
         int var5 = var3.indexOf(var4);
         if (var5 == -1) {
            return var0.copy();
         } else {
            int var6 = var5 + var4.length();
            int var7 = var3.codePointCount(0, var5);
            int var8 = var3.codePointCount(0, var6);
            OrderedText var9 = var0.asOrderedText();
            ArrayList var10 = new ArrayList();
            var9.accept((var1x, var2x, var3x) -> {
               var10.add(new TextUtils.Data(new String(Character.toChars(var3x)), var2x));
               return true;
            });
            MutableText var11 = Text.empty();

            for (int var12 = 0; var12 < var10.size(); var12++) {
               if (var12 == var7) {
                  var11.append(var2.copy());
               }

               if (var12 < var7 || var12 >= var8) {
                  TextUtils.Data var13 = (TextUtils.Data)var10.get(var12);
                  var11.append(Text.literal(var13.text()).setStyle(var13.style()));
               }
            }

            return var11;
         }
      } else {
         return var0.copy();
      }
   }

   public static MutableText method2107(Text var0, String var1, Text var2) {
      if (var0 != null && var1 != null && !var1.isEmpty()) {
         OrderedText var3 = var0.asOrderedText();
         ArrayList var4 = new ArrayList();
         StringBuilder var5 = new StringBuilder();
         var3.accept((var2x, var3x, var4x) -> {
            String var5x = new String(Character.toChars(var4x));
            var4.add(new TextUtils.Data(var5x, var3x));
            var5.append(var5x);
            return true;
         });
         String var6 = var5.toString();
         int var7 = var6.indexOf(var1);
         if (var7 == -1) {
            return var0.copy();
         } else {
            int var8 = var7 + var1.length();
            int var9 = var6.codePointCount(0, var8);
            MutableText var10 = Text.empty();
            var10.append(var2.copy());

            for (int var11 = var9; var11 < var4.size(); var11++) {
               TextUtils.Data var12 = (TextUtils.Data)var4.get(var11);
               var10.append(Text.literal(var12.text()).setStyle(var12.style()));
            }

            return var10;
         }
      } else {
         return var0 == null ? Text.empty() : var0.copy();
      }
   }

   public static Text method2108(Text var0, boolean var1) {
      OrderedText var2 = var0.asOrderedText();
      MutableText var3 = Text.empty();
      StringBuilder var4 = new StringBuilder();
      Style[] var5 = new Style[]{Style.EMPTY};
      int[] var6 = new int[]{0};
      boolean[] var7 = new boolean[]{false};
      var2.accept((var5x, var6x, var7x) -> {
         if (var6[0] > 2) {
            var7[0] = true;
            return false;
         } else {
            if (!var6x.equals(var5[0])) {
               if (!var4.isEmpty()) {
                  var3.append(Text.literal(var4.toString()).setStyle(var5[0]));
                  var4.setLength(0);
               }

               var5[0] = var6x;
            }

            var4.appendCodePoint(var7x);
            if (Character.isWhitespace(var7x)) {
               var6[0]++;
            }

            return true;
         }
      });
      if (var4.length() > 0) {
         var3.append(Text.literal(var4.toString()).setStyle(var5[0]));
      }

      if (var1 && var7[0]) {
         Style var8 = var5[0] != null ? var5[0] : var0.getStyle();
         var3.append(Text.literal("…").setStyle(var8));
      }

      return var3;
   }

   public static Text method2109(Text var0, int var1, boolean var2) {
      if (var1 <= 0) {
         return Text.empty();
      } else {
         OrderedText var3 = var0.asOrderedText();
         MutableText var4 = Text.empty();
         StringBuilder var5 = new StringBuilder();
         Style[] var6 = new Style[]{Style.EMPTY};
         int[] var7 = new int[]{0};
         boolean[] var8 = new boolean[]{false};
         var3.accept((var6x, var7x, var8x) -> {
            if (var7[0] >= var1) {
               var8[0] = true;
               return false;
            } else {
               if (!var7x.equals(var6[0])) {
                  if (var5.length() > 0) {
                     var4.append(Text.literal(var5.toString()).setStyle(var6[0]));
                     var5.setLength(0);
                  }

                  var6[0] = var7x;
               }

               var5.appendCodePoint(var8x);
               var7[0]++;
               return true;
            }
         });
         if (var5.length() > 0) {
            var4.append(Text.literal(var5.toString()).setStyle(var6[0]));
         }

         if (var2 && var8[0]) {
            Style var9 = var6[0] != null ? var6[0] : var0.getStyle();
            return var4.append(Text.literal("…").setStyle(var9));
         } else {
            return var4;
         }
      }
   }

   public static MutableText method2110(Text var0, String var1, String var2) {
      return method2112(var0, var1, var1x -> {
         Style var2x = var1x.get(0).style() != null ? var1x.get(0).style() : Style.EMPTY;
         return Text.literal(var2).setStyle(var2x);
      });
   }

   public static MutableText method2111(Text var0, String var1, Text var2) {
      return method2112(var0, var1, var1x -> var2.copy());
   }

   private static MutableText method2112(Text var0, String var1, Function<List<TextUtils.Data>, Text> var2) {
      OrderedText var3 = var0.asOrderedText();
      MutableText var4 = Text.empty();
      ArrayList<TextUtils.Data> var5 = new ArrayList();
      Runnable var6 = () -> {
         if (!var5.isEmpty()) {
            StringBuilder var4x = new StringBuilder();

            for (Object var6x_o : (Iterable<?>)(Object)(var5)) {
               TextUtils.Data var6x = (TextUtils.Data)var6x_o;
               var4x.append(var6x.text());
            }

            String var8 = var4x.toString();
            if (var8.equals(var1)) {
               var4.append((Text)var2.apply(var5));
            } else {
               for (Object var7_o : (Iterable<?>)(Object)(var5)) {
                  TextUtils.Data var7 = (TextUtils.Data)var7_o;
                  var4.append(Text.literal(var7.text()).setStyle(var7.style()));
               }
            }

            var5.clear();
         }
      };
      var3.accept((var3x, var4x, var5x) -> {
         String var6x = new String(Character.toChars(var5x));
         boolean var7 = Character.isLetterOrDigit(var5x) || var5x == 95;
         if (var7) {
            var5.add(new TextUtils.Data(var6x, var4x));
         } else {
            var6.run();
            var4.append(Text.literal(var6x).setStyle(var4x));
         }

         return true;
      });
      var6.run();
      return var4;
   }

   public static MutableText method2113(Text var0, String var1) {
      if (var1 != null && !var1.isEmpty()) {
         OrderedText var2 = var0.asOrderedText();
         ArrayList var3 = new ArrayList();
         StringBuilder var4 = new StringBuilder();
         var2.accept((var2x, var3x, var4x) -> {
            String var5x = new String(Character.toChars(var4x));
            var3.add(new TextUtils.Data(var5x, var3x));
            var4.append(var5x);
            return true;
         });
         String var5 = var4.toString();
         int var6 = var5.indexOf(var1);
         if (var6 == -1) {
            return Text.empty();
         } else {
            MutableText var7 = Text.empty();

            for (int var8 = 0; var8 < var6; var8++) {
               TextUtils.Data var9 = (TextUtils.Data)var3.get(var8);
               var7.append(Text.literal(var9.text()).setStyle(var9.style()));
            }

            return var7;
         }
      } else {
         return Text.empty();
      }
   }

   public static MutableText method2114(Text var0, String var1, Text var2) {
      String var3 = var0.getString();
      int var4 = var3.indexOf(var1);
      if (var4 == -1) {
         return var0.copy();
      } else {
         OrderedText var5 = var0.asOrderedText();
         ArrayList var6 = new ArrayList();
         StringBuilder var7 = new StringBuilder();
         var5.accept((var2x, var3x, var4x) -> {
            String var5x = new String(Character.toChars(var4x));
            var6.add(new TextUtils.Data(var5x, var3x));
            var7.append(var5x);
            return true;
         });
         var3 = var7.toString();
         int var8 = var4 + var1.length();

         while (var8 < var3.length()) {
            int var9 = var3.codePointAt(var8);
            if (!Character.isWhitespace(var9)) {
               break;
            }

            var8 += Character.charCount(var9);
         }

         if (var8 >= var3.length()) {
            return var0.copy();
         } else {
            int var17 = var8;

            while (var17 < var3.length()) {
               int var10 = var3.codePointAt(var17);
               if (Character.isWhitespace(var10)) {
                  break;
               }

               var17 += Character.charCount(var10);
            }

            MutableText var18 = method2115(var2, 1);
            if (var18.getString().isEmpty()) {
               var18 = method2115(var2, 0);
            }

            int var11 = var3.codePointCount(0, var8);
            int var12 = var3.codePointCount(0, var17);
            MutableText var13 = Text.empty();

            for (int var14 = 0; var14 < var11; var14++) {
               TextUtils.Data var15 = (TextUtils.Data)var6.get(var14);
               var13.append(Text.literal(var15.text()).setStyle(var15.style()));
            }

            var13.append(var18.copy());

            for (int var19 = var12; var19 < var6.size(); var19++) {
               TextUtils.Data var20 = (TextUtils.Data)var6.get(var19);
               var13.append(Text.literal(var20.text()).setStyle(var20.style()));
            }

            return var13;
         }
      }
   }

   private static MutableText method2115(Text var0, int var1) {
      OrderedText var2 = var0.asOrderedText();
      ArrayList var3 = new ArrayList();
      StringBuilder var4 = new StringBuilder();
      var2.accept((var2x, var3x, var4x) -> {
         String var5x = new String(Character.toChars(var4x));
         var3.add(new TextUtils.Data(var5x, var3x));
         var4.append(var5x);
         return true;
      });
      String var5 = var4.toString();
      int var6 = 0;

      for (int var7 = 0; var7 < var5.length(); var6++) {
         while (var7 < var5.length()) {
            int var8 = var5.codePointAt(var7);
            if (!Character.isWhitespace(var8)) {
               break;
            }

            var7 += Character.charCount(var8);
         }

         if (var7 >= var5.length()) {
            break;
         }

         int var15 = var7;

         while (true) {
            if (var7 < var5.length()) {
               int var9 = var5.codePointAt(var7);
               if (!Character.isWhitespace(var9)) {
                  var7 += Character.charCount(var9);
                  continue;
               }
            }

            if (var6 == var1) {
               int var10 = var5.codePointCount(0, var15);
               int var11 = var5.codePointCount(0, var7);
               MutableText var12 = Text.empty();

               for (int var13 = var10; var13 < var11; var13++) {
                  TextUtils.Data var14 = (TextUtils.Data)var3.get(var13);
                  var12.append(Text.literal(var14.text()).setStyle(var14.style()));
               }

               return var12;
            }
            break;
         }
      }

      return Text.empty();
   }

   public static MutableText method2116(Text var0, String var1) {
      OrderedText var2 = var0.asOrderedText();
      int[] var3 = new int[]{0};
      var2.accept((var1x, var2x, var3x) -> {
         var3[0]++;
         return true;
      });
      if (var3[0] == 0) {
         return var0.copy();
      } else {
         int var4 = var3[0] - 1;
         MutableText var5 = Text.empty();
         StringBuilder var6 = new StringBuilder();
         Style[] var7 = new Style[]{null};
         int[] var8 = new int[]{0};
         var2.accept((var6x, var7x, var8x) -> {
            boolean var9 = var8[0] == var4;
            if (var7[0] == null || !var7[0].equals(var7x)) {
               method2117(var5, var6, var7[0]);
               var7[0] = var7x;
            }

            if (var9) {
            }

            var6.appendCodePoint(var8x);
            var8[0]++;
            return true;
         });
         method2117(var5, var6, var7[0]);
         return var5;
      }
   }

   private static void method2117(MutableText var0, StringBuilder var1, Style var2) {
      if (var1.length() != 0) {
         MutableText var3 = Text.literal(var1.toString());
         if (var2 != null) {
            var3.setStyle(var2);
         }

         var0.append(var3);
         var1.setLength(0);
      }
   }

   public static Text method2118(Text var0, String var1, boolean var2) {
      if (var1 != null && !var1.isEmpty()) {
         OrderedText var3 = var0.asOrderedText();
         MutableText var4 = Text.empty();
         StringBuilder var5 = new StringBuilder();
         Style[] var6 = new Style[]{Style.EMPTY};
         StringBuilder var7 = new StringBuilder();
         boolean[] var8 = new boolean[]{false};
         var3.accept((var6x, var7x, var8x) -> {
            String var9x = new String(Character.toChars(var8x));
            if (var7.toString().contains(var1)) {
               var8[0] = true;
               return false;
            } else {
               var7.append(var9x);
               if (!var7x.equals(var6[0])) {
                  if (var5.length() > 0) {
                     var4.append(Text.literal(var5.toString()).setStyle(var6[0]));
                     var5.setLength(0);
                  }

                  var6[0] = var7x;
               }

               var5.append(var9x);
               return true;
            }
         });
         if (var5.length() > 0) {
            var4.append(Text.literal(var5.toString()).setStyle(var6[0]));
         }

         if (var2 && var8[0]) {
            Style var9 = var6[0] != null ? var6[0] : var0.getStyle();
            var4.append(Text.literal("…").setStyle(var9));
         }

         return var4;
      } else {
         return var0;
      }
   }

   public static String method2119(String var0) {
      if (var0.isEmpty()) {
         return var0;
      } else {
         char[] var1 = new char[var0.length()];

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            boolean var4 = var3 >= 1040 && var3 <= 1071 || var3 == 1025;
            char var5 = Character.toLowerCase(var3);
            int var6 = "ёйцукенгшщзхъфывапролджэячсмитьбю".indexOf(var5);
            if (var6 != -1) {
               char var7 = "`qwertyuiop[]asdfghjkl;'zxcvbnm,.".charAt(var6);
               var1[var2] = var4 ? Character.toUpperCase(var7) : var7;
            } else {
               var1[var2] = var3;
            }
         }

         return new String(var1);
      }
   }

   @Generated
   private TextUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Data {
      private final String text;
      private final Style style;

      public String text() {
         return this.text;
      }

      public Style style() {
         return this.style;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.style, other.style);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, style);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "style=" + this.style + "]";
      }

      public Data(String var1, Style var2) {
         this.text = var1;
         this.style = var2;
      }
   }
}

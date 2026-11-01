package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.zenith.client.modules.impl.misc.StreamerModeModule;

public final class ScoreboardParser {
   private static final Pattern pattern = Pattern.compile("(?iu)(?:соло|дуо|трио|клан)?[\\s-]*лайт[\\s#]*\\d*");
   private static final Pattern pattern2 = Pattern.compile("(?i)^(?:§.)*\\s*(XYZ|Block|Chunk|Targeted Block|Targeted Fluid)\\b");
   private static final Pattern pattern3 = Pattern.compile("-?\\d+(?:\\.\\d+)?");
   private static long longValue;
   private static long longValue2;
   private static boolean flag;
   private static final Pattern pattern4 = Pattern.compile("(?i)онлайн:\\s*(\\d+)");
   private static final Pattern pattern5 = Pattern.compile("[\\s#◀▶\\-]+");
   private static final ScoreboardParser.Entry[] entrys = new ScoreboardParser.Entry[]{new ScoreboardParser.Entry(), new ScoreboardParser.Entry()};
   private static int intValue;

   public static boolean isActive() {
      return StreamerModeModule.INSTANCE.isEnabled();
   }

   private static boolean method7066() {
      return StreamerModeModule.INSTANCE.method6289();
   }

   private static void method7067() {
      if (!flag) {
         Random var0 = new Random();
         longValue = method7068(var0);
         longValue2 = method7068(var0);
         flag = true;
      }
   }

   private static long method7068(Random var0) {
      long var1 = (50000L + var0.nextInt(850000)) / 512L * 512L;
      return var0.nextBoolean() ? var1 : -var1;
   }

   public static int method7069(int var0) {
      method7067();
      return (int)(var0 + longValue);
   }

   public static int method7070(int var0) {
      method7067();
      return (int)(var0 + longValue2);
   }

   public static List<String> method7071(List<String> var0) {
      if (isActive() && var0 != null) {
         List<String> var1 = new ArrayList<>(var0.size());

         for (String var3 : var0) {
            var1.add(method7072(var3));
         }

         return var1;
      } else {
         return var0;
      }
   }

   private static String method7072(String var0) {
      if (var0 == null) {
         return null;
      } else {
         Matcher var1 = pattern2.matcher(var0);
         if (!var1.find()) {
            return var0;
         } else {
            method7067();
            boolean var2 = var1.group(1).equalsIgnoreCase("Chunk");
            long var3 = var2 ? longValue >> 4 : longValue;
            long var5 = var2 ? longValue2 >> 4 : longValue2;
            Matcher var7 = pattern3.matcher(var0);
            StringBuilder var8 = new StringBuilder(var0.length());

            for (int var9 = 0; var7.find(); var9++) {
               String var10 = var7.group();

               long var11 = switch (var9) {
                  case 0 -> var3;
                  default -> 0L;
                  case 2 -> var5;
                  case 5 -> var2 ? longValue >> 9 : 0L;
                  case 6 -> var2 ? longValue2 >> 9 : 0L;
               };
               String var13 = var10;
               if (var11 != 0L) {
                  try {
                     var13 = var10.contains(".")
                        ? String.format(Locale.US, "%." + method7073(var10) + "f", Double.parseDouble(var10) + var11)
                        : String.valueOf(Long.parseLong(var10) + var11);
                  } catch (NumberFormatException var15) {
                  }
               }

               var7.appendReplacement(var8, Matcher.quoteReplacement(var13));
            }

            var7.appendTail(var8);
            return var8.toString();
         }
      }
   }

   private static int method7073(String var0) {
      return var0.length() - var0.indexOf(46) - 1;
   }

   @Nullable
   public static Text method7074(@Nullable Text var0) {
      if (var0 != null && isActive()) {
         Language var1 = Language.getInstance();
         boolean var2 = method7066();
         Text var3 = StreamerModeModule.INSTANCE.getText4();
         int var4 = StreamerModeModule.INSTANCE.getIntValue32();
         Text var5 = StreamerModeModule.INSTANCE.getText32();
         String var6 = method7087();

         for (ScoreboardParser.Entry var10 : entrys) {
            if (var10.text == var0
               && var10.language == var1
               && var10.flag == var2
               && var10.text2 == var3
               && var10.intValue == var4
               && var10.text3 == var5
               && var6.equals(var10.text4)) {
               return var10.text5;
            }
         }

         Text var11 = method7075(var0);
         ScoreboardParser.Entry var12 = entrys[intValue];
         intValue = (intValue + 1) % entrys.length;
         var12.text = var0;
         var12.language = var1;
         var12.flag = var2;
         var12.text2 = var3;
         var12.intValue = var4;
         var12.text3 = var5;
         var12.text4 = var6;
         var12.text5 = var11;
         return var11;
      } else {
         return var0;
      }
   }

   private static Text method7075(Text var0) {
      var var1 = method7093(var0);
      List<List<ScoreboardParser.Data>> var2 = method7094(var1);
      List<List<Data>> var3 = new ArrayList<>(var2.size());
      boolean var4 = false;

      for (Object var6_o : (Iterable<?>)(Object)(var2)) {
         List var6 = (List)var6_o;
         String var7 = method7098(var6);
         String var8 = var7.trim();
         if (method7090(var8)) {
            if (method7066()) {
               Text var9 = method7088(var6, var8);
               if (var9 != null) {
                  var3.add(method7093(var9));
                  var4 = true;
                  continue;
               }
            }

            var4 = true;
         } else {
            var var10 = method7092(var6, var7, method7087());
            if (!var10.equals(var6)) {
               var4 = true;
            }

            var3.add(var10);
         }
      }

      return var4 ? method7095(var3) : var0;
   }

   @Nullable
   public static Text method7076(@Nullable Text var0) {
      if (isActive() && var0 != null) {
         String var1 = var0.getString();
         if (method7080(var1)) {
            if (method7066()) {
               Text var4 = StreamerModeModule.INSTANCE.getText3();
               if (var4 != null) {
                  return var4.copy();
               } else {
                  var var3 = method7093(var0);
                  return method7095(List.of(method7092(var3, method7098(var3), method7087())));
               }
            } else {
               return null;
            }
         } else if (!method7081(var1)) {
            return var0;
         } else if (method7066()) {
            Text var2 = StreamerModeModule.INSTANCE.getText22();
            return (Text)(var2 != null ? var2.copy() : method7089(var0, StreamerModeModule.INSTANCE.getIntValue32()));
         } else {
            return null;
         }
      } else {
         return var0;
      }
   }

   public static boolean method7077(@Nullable Text var0) {
      return var0 == null ? false : method7076(var0) == null;
   }

   public static boolean method7078(@Nullable Text var0) {
      return var0 == null ? false : method7080(var0.getString());
   }

   public static boolean method7079(@Nullable Text var0) {
      return var0 == null ? false : method7081(var0.getString());
   }

   private static boolean method7080(String var0) {
      return var0.contains("-◆-") && pattern.matcher(var0).find();
   }

   private static boolean method7081(String var0) {
      return var0.toLowerCase(Locale.ROOT).startsWith("онлайн:");
   }

   @Nullable
   public static Text method7082(@Nullable Text var0) {
      if (var0 == null) {
         return null;
      } else {
         for (List var2 : method7094(method7093(var0))) {
            String var3 = method7098(var2).trim();
            if (method7091(var3)) {
               return method7095(List.of(var2));
            }
         }

         return null;
      }
   }

   @Nullable
   public static String method7083(@Nullable Text var0) {
      if (var0 == null) {
         return null;
      } else {
         Matcher var1 = pattern.matcher(var0.getString());
         if (!var1.find()) {
            return null;
         } else {
            String var2 = var1.group().trim();
            return var2.isEmpty() ? null : var2;
         }
      }
   }

   @Nullable
   public static Text method7084(@Nullable Text var0) {
      if (var0 == null) {
         return null;
      } else {
         for (List var2 : method7094(method7093(var0))) {
            String var3 = method7098(var2).trim();
            if (var3.toLowerCase(Locale.ROOT).startsWith("онлайн:")) {
               return method7095(List.of(var2));
            }
         }

         return null;
      }
   }

   public static int method7085(@Nullable Text var0) {
      if (var0 == null) {
         return -1;
      } else {
         Matcher var1 = pattern4.matcher(var0.getString());
         return var1.find() ? Integer.parseInt(var1.group(1)) : -1;
      }
   }

   public static Text method7086(Text var0) {
      var var1 = method7093(var0);
      return method7095(List.of(method7092(var1, method7098(var1), "лайт")));
   }

   private static String method7087() {
      if (method7066()) {
         String var0 = StreamerModeModule.INSTANCE.getText5();
         if (var0 != null && !var0.isEmpty()) {
            return var0;
         }
      }

      return "лайт";
   }

   @Nullable
   private static Text method7088(List<ScoreboardParser.Data> var0, String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      if (var2.equals("анархия")) {
         return method7095(List.of(var0));
      } else if (var2.startsWith("онлайн:")) {
         Text var4 = StreamerModeModule.INSTANCE.getText4();
         return (Text)(var4 != null ? var4.copy() : method7089(method7095(List.of(var0)), StreamerModeModule.INSTANCE.getIntValue32()));
      } else if (method7091(var1)) {
         Text var3 = StreamerModeModule.INSTANCE.getText32();
         return (Text)(var3 != null ? var3.copy() : method7095(List.of(method7092(var0, method7098(var0), method7087()))));
      } else {
         return null;
      }
   }

   private static Text method7089(Text var0, int var1) {
      if (var1 <= 0) {
         return var0.copy();
      } else {
         String var2 = var0.getString();
         Matcher var3 = pattern4.matcher(var2);
         if (!var3.find()) {
            return var0.copy();
         } else {
            var var4 = method7093(var0);
            int var5 = var3.start(1);
            int var6 = var3.end(1);
            String var7 = String.valueOf(var1);
            List<ScoreboardParser.Data> var8 = new ArrayList<>(var4.size() - (var6 - var5) + var7.length());

            for (int var9 = 0; var9 < var5 && var9 < var4.size(); var9++) {
               var8.add((ScoreboardParser.Data)var4.get(var9));
            }

            Style var11 = var5 < var4.size() ? ((ScoreboardParser.Data)var4.get(var5)).style() : Style.EMPTY;

            for (int var10 = 0; var10 < var7.length(); var10++) {
               var8.add(new ScoreboardParser.Data(var7.charAt(var10), var11));
            }

            for (int var12 = var6; var12 < var4.size(); var12++) {
               var8.add((ScoreboardParser.Data)var4.get(var12));
            }

            return method7095(List.of(var8));
         }
      }
   }

   private static boolean method7090(String var0) {
      if (var0.isEmpty()) {
         return false;
      } else {
         String var1 = var0.toLowerCase(Locale.ROOT);
         return var1.equals("анархия") || var1.startsWith("онлайн:") || method7091(var0);
      }
   }

   private static boolean method7091(String var0) {
      if (var0.isEmpty()) {
         return false;
      } else {
         String var1 = pattern5.matcher(var0).replaceAll("");
         return pattern.matcher(var0).matches() || pattern.matcher(var1).matches();
      }
   }

   private static List<ScoreboardParser.Data> method7092(List<ScoreboardParser.Data> var0, String var1, String var2) {
      Matcher var3 = pattern.matcher(var1);
      if (!var3.find()) {
         return var0;
      } else {
         List<Data> var4 = new ArrayList<>(var0.size());
         var3.reset();

         int var5;
         for (var5 = 0; var3.find(); var5 = var3.end()) {
            var4.addAll(var0.subList(var5, var3.start()));
            Style var6 = ((ScoreboardParser.Data)var0.get(var3.start())).style();

            for (int var7 = 0; var7 < var2.length(); var7++) {
               var4.add(new ScoreboardParser.Data(var2.charAt(var7), var6));
            }
         }

         var4.addAll(var0.subList(var5, var0.size()));
         return var4;
      }
   }

   private static List<ScoreboardParser.Data> method7093(Text var0) {
      ArrayList<ScoreboardParser.Data> var1 = new ArrayList<>();
      OrderedText var2 = var0.asOrderedText();
      var2.accept((var1x, var2x, var3) -> {
         String var4 = new String(Character.toChars(var3));

         for (int var5 = 0; var5 < var4.length(); var5++) {
            var1.add(new ScoreboardParser.Data(var4.charAt(var5), var2x));
         }

         return true;
      });
      return var1;
   }

   private static List<List<ScoreboardParser.Data>> method7094(List<ScoreboardParser.Data> var0) {
      ArrayList<List<ScoreboardParser.Data>> var1 = new ArrayList<>();
      ArrayList var2 = new ArrayList();

      for (ScoreboardParser.Data var4 : var0) {
         if (var4.getCharValue() == '\n') {
            var1.add(var2);
            var2 = new ArrayList();
         } else {
            var2.add(var4);
         }
      }

      var1.add(var2);
      return var1;
   }

   private static Text method7095(List<List<ScoreboardParser.Data>> var0) {
      MutableText var1 = Text.empty();
      Style var2 = Style.EMPTY;

      for (int var3 = 0; var3 < var0.size(); var3++) {
         if (var3 > 0) {
            var1.append(Text.literal("\n").setStyle(var2));
         }

         List var4 = (List)var0.get(var3);
         if (!var4.isEmpty()) {
            var1.append(method7096(var4));
            var2 = ((ScoreboardParser.Data)var4.getLast()).style();
         }
      }

      return var1;
   }

   private static MutableText method7096(List<ScoreboardParser.Data> var0) {
      MutableText var1 = Text.empty();
      Style var2 = null;
      StringBuilder var3 = new StringBuilder();

      for (ScoreboardParser.Data var5 : var0) {
         if (var2 == null || !var5.style().equals(var2)) {
            method7097(var1, var3, var2);
            var2 = var5.style();
         }

         var3.append(var5.getCharValue());
      }

      method7097(var1, var3, var2);
      return var1;
   }

   private static void method7097(MutableText var0, StringBuilder var1, Style var2) {
      if (!var1.isEmpty()) {
         var0.append(Text.literal(var1.toString()).setStyle(var2 == null ? Style.EMPTY : var2));
         var1.setLength(0);
      }
   }

   private static String method7098(List<ScoreboardParser.Data> var0) {
      StringBuilder var1 = new StringBuilder(var0.size());

      for (ScoreboardParser.Data var3 : var0) {
         var1.append(var3.getCharValue());
      }

      return var1.toString();
   }

   @Generated
   private ScoreboardParser() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   private static final class Data {
      private final char charValue;
      private final Style style;

      public char charValue() {
         return this.charValue;
      }

      public Style style() {
         return this.style;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.charValue, other.charValue) && java.util.Objects.equals(this.style, other.style);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(charValue, style);
      }

      @Override
      public String toString() {
         return "Data[" + "charValue=" + this.charValue + ", " + "style=" + this.style + "]";
      }

      private Data(char var1, Style var2) {
         this.charValue = var1;
         this.style = var2;
      }

      public char getCharValue() {
         return this.charValue;
      }
   }

   private static final class Entry {
      public Text text;
      public Language language;
      public boolean flag;
      public Text text2;
      public int intValue;
      public Text text3;
      public String text4;
      public Text text5;
   }
}

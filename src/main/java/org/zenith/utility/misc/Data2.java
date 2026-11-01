package org.zenith.utility.misc;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Data2 {
   private final String text;
   private final boolean flag;
   private final int intValue;

   public String text() {
      return this.text;
   }

   public boolean flag() {
      return this.flag;
   }

   public int intValue() {
      return this.intValue;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Data2 other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.intValue, other.intValue);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, flag, intValue);
   }

   @Override
   public String toString() {
      return "Data2[" + "text=" + this.text + ", " + "flag=" + this.flag + ", " + "intValue=" + this.intValue + "]";
   }
   private static final int CONST_intValue = 322;
   private static final Pattern pattern = Pattern.compile("§[0-9a-fk-orx]", 322);
   private static final Pattern pattern2 = Pattern.compile(
      method1517("(?<![\\p{L}\\p{N}])(?:pvp|пвп|бой|бою|боевой\\s+режим|режим\\s+боя|combat)(?![\\p{L}\\p{N}])"), 322
   );
   private static final Pattern pattern3 = Pattern.compile(
      method1517(
         "(?<![\\p{L}\\p{N}])(?:выключ[её]н[аоы]?|отключ[её]н[аоы]?|заверш[её]н[аоы]?|закончен[аоы]?|неактивен|не\\s+активен|вне\\s+боя|не\\s+в\\s+бою|вышли\\s+из|покинули|disabled|ended|expired|not\\s+in\\s+combat|off)(?![\\p{L}\\p{N}])"
      ),
      322
   );
   private static final Pattern pattern4 = Pattern.compile(
      method1517("(?:будет\\s+(?:выключ|отключ|заверш|закончен)|(?:выключ|отключ|заверш|закончен)\\p{L}*\\s+через)"), 322
   );
   private static final Pattern pattern5 = Pattern.compile("(?<![\\d:.])(\\d{1,3}):([0-5]\\d)(?![\\d:.])");
   private static final Pattern pattern6 = Pattern.compile(
      "(?<![\\d.,:])(\\d{1,4})(?![\\d.,:])\\s*(?:[сc][еe][кk](?:[уy][нnh][дd](?:ы|а|у)?)?\\.?|seconds?|secs?\\.?|[sсc])(?=$|[^\\p{L}\\p{N}])", 322
   );
   private static final Pattern pattern7 = Pattern.compile("\\d+", 322);
   private static final Pattern pattern8 = Pattern.compile("\\d\\s*:\\s*\\d");
   private static final Pattern pattern9 = Pattern.compile("(?<![\\p{L}])(?:мин(?:ут[аыу]?)?|minutes?|mins?)\\.?(?![\\p{L}])", 322);

   public Data2(String var1, boolean var2, int var3) {
      this.text = var1;
      this.flag = var2;
      this.intValue = var3;
   }

   public static Data2 method1515(String var0) {
      String var1 = method1518(var0);
      String var2 = var1.toLowerCase(Locale.ROOT);
      String var3 = method1517(var2);
      boolean var4 = pattern2.matcher(var3).find();
      if (var4 && pattern3.matcher(var3).find() && !pattern4.matcher(var3).find()) {
         return new Data2(var1, false, -1);
      } else {
         int var5 = method1516(var2, var4);
         return new Data2(var1, var4 && var5 != 0, var5);
      }
   }

   private static int method1516(String var0, boolean var1) {
      Matcher var2 = pattern5.matcher(var0);
      if (var2.find()) {
         return Integer.parseInt(var2.group(1)) * 60 + Integer.parseInt(var2.group(2));
      } else if (!pattern9.matcher(var0).find() && !pattern8.matcher(var0).find()) {
         Matcher var3 = pattern6.matcher(var0);
         if (var3.find()) {
            int var7 = Integer.parseInt(var3.group(1));
            return var3.find() ? -1 : var7;
         } else if (var1 && var0.indexOf(37) < 0 && var0.indexOf(65285) < 0) {
            Matcher var4 = pattern7.matcher(var0);
            if (!var4.find()) {
               return -1;
            } else {
               String var5 = var4.group();
               if (var5.length() > 4) {
                  return -1;
               } else {
                  int var6 = Integer.parseInt(var5);
                  return var4.find() ? -1 : var6;
               }
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private static String method1517(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length());
      String var2 = "асеорхукнвтм";
      String var3 = "aceopxykhbtm";

      for (int var4 = 0; var4 < var0.length(); var4++) {
         char var5 = var0.charAt(var4);
         int var6 = var2.indexOf(var5);
         var1.append(var6 < 0 ? var5 : var3.charAt(var6));
      }

      return var1.toString();
   }

   private static String method1518(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         String var1 = pattern.matcher(var0).replaceAll("");
         StringBuilder var2 = new StringBuilder(var1.length());
         boolean var3 = false;
         int var4 = 0;

         while (var4 < var1.length()) {
            int var5 = var1.codePointAt(var4);
            var4 += Character.charCount(var5);
            int var6 = Character.getType(var5);
            if (var6 != 18 && var6 != 16) {
               if (!Character.isWhitespace(var5) && !Character.isSpaceChar(var5)) {
                  if (var3) {
                     var2.append(' ');
                     var3 = false;
                  }

                  var2.appendCodePoint(var5);
               } else {
                  var3 = !var2.isEmpty();
               }
            }
         }

         return var2.toString();
      } else {
         return "";
      }
   }

   public String value() {
      if (this.intValue < 0) {
         return this.flag ? "" : null;
      } else {
         return this.intValue >= 60 ? this.intValue / 60 + ":" + (this.intValue % 60 < 10 ? "0" : "") + this.intValue % 60 : this.intValue + "s";
      }
   }

   public String title() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int getIntValue() {
      return this.intValue;
   }
}

package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;

public class CalcCommand extends CommandAbstract {
   public CalcCommand() {
      super("calc");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .calc <выражение>");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Пример: .calc (2 + 2) * 2 или .calc 64 * 3.5кк, подробнее: .calc help");
         return 1;
      });
      var1.then(literal("help").executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Операторы: + - * / ^ и скобки, умножение можно писать как x или х");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Числа: 1.5 или 1,5; суффиксы к/кк/ккк: 1кк = 1 000 000");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Процент от числа: .calc 250 + 10% = 275, .calc 1кк - 15% = 850 000");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Функции: sqrt, cbrt, abs, round, floor, ceil, sin, cos, tan, ln, log; константы pi, e");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Пример: .calc sqrt(16) + 2 ^ 10 или .calc (27 * 1.8кк) - 10%");
         return 1;
      }));
      var1.then(arg("expression", StringArgumentType.greedyString()).executes(var1x -> {
         String var2 = ((String)var1x.getArgument("expression", String.class)).trim();

         try {
            double var3 = new CalcCommand.ExpressionParser(var2).parse();
            ChatLogger.method2020(ChatLogger.Type.INFO, var2 + " = " + this.format(var3));
         } catch (CalcCommand.CalcException var5) {
            ChatLogger.method2020(ChatLogger.Type.ERROR, var5.getMessage());
         }

         return 1;
      }));
   }

   private String format(double var1) {
      BigDecimal var3 = BigDecimal.valueOf(var1).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
      if (var3.compareTo(BigDecimal.ZERO) == 0) {
         var3 = BigDecimal.ZERO;
      }

      String var4 = var3.toPlainString();
      boolean var5 = var4.startsWith("-");
      if (var5) {
         var4 = var4.substring(1);
      }

      int var6 = var4.indexOf(46);
      String var7 = var6 == -1 ? var4 : var4.substring(0, var6);
      String var8 = var6 == -1 ? "" : var4.substring(var6);
      StringBuilder var9 = new StringBuilder();

      for (int var10 = 0; var10 < var7.length(); var10++) {
         if (var10 > 0 && (var7.length() - var10) % 3 == 0) {
            var9.append(' ');
         }

         var9.append(var7.charAt(var10));
      }

      return (var5 ? "-" : "") + var9 + var8;
   }

   private static final class CalcException extends RuntimeException {
      public CalcException(String var1) {
         super(var1);
      }
   }

   private static final class ExpressionParser {
      private final String src;
      private int pos;

      public ExpressionParser(String var1) {
         this.src = var1.toLowerCase(Locale.ROOT)
            .replace(',', '.')
            .replace('x', '*')
            .replace('х', '*')
            .replace('×', '*')
            .replace('÷', '/')
            .replace(':', '/')
            .replace('−', '-');
      }

      public double parse() {
         double var1 = this.expression();
         this.skipWs();
         if (this.pos < this.src.length()) {
            throw this.err("Лишний символ '" + this.src.charAt(this.pos) + "'", this.pos);
         } else if (Double.isNaN(var1)) {
            throw new CalcCommand.CalcException("Результат не определён");
         } else if (Double.isInfinite(var1)) {
            throw new CalcCommand.CalcException("Результат слишком большой");
         } else {
            return var1;
         }
      }

      private double expression() {
         double var1 = resolve(this.term());

         while (true) {
            this.skipWs();
            char var3 = this.peek();
            if (var3 != '+' && var3 != '-') {
               return var1;
            }

            this.pos++;
            CalcCommand.Val var4 = this.term();
            double var5 = var4.pct() ? var1 * var4.v() / 100.0 : var4.v();
            var1 = var3 == '+' ? var1 + var5 : var1 - var5;
         }
      }

      private CalcCommand.Val term() {
         CalcCommand.Val var1 = this.unary();

         while (true) {
            this.skipWs();
            char var2 = this.peek();
            if (var2 != '*' && var2 != '/') {
               if (var2 != '(') {
                  return var1;
               }

               var1 = this.multiply(var1, this.unary(), false);
            } else {
               this.pos++;
               var1 = this.multiply(var1, this.unary(), var2 == '/');
            }
         }
      }

      private CalcCommand.Val multiply(CalcCommand.Val var1, CalcCommand.Val var2, boolean var3) {
         double var4 = resolve(var1);
         double var6 = resolve(var2);
         if (var3) {
            if (var6 == 0.0) {
               throw new CalcCommand.CalcException("Деление на ноль");
            } else {
               return new CalcCommand.Val(var4 / var6, false);
            }
         } else {
            return new CalcCommand.Val(var4 * var6, false);
         }
      }

      private CalcCommand.Val unary() {
         this.skipWs();
         char var1 = this.peek();
         if (var1 == '+') {
            this.pos++;
            return this.unary();
         } else if (var1 == '-') {
            this.pos++;
            CalcCommand.Val var2 = this.unary();
            return new CalcCommand.Val(-var2.v(), var2.pct());
         } else {
            return this.power();
         }
      }

      private CalcCommand.Val power() {
         CalcCommand.Val var1 = this.postfix();
         this.skipWs();
         if (this.peek() != '^') {
            return var1;
         } else {
            this.pos++;
            CalcCommand.Val var2 = this.unary();
            return new CalcCommand.Val(Math.pow(resolve(var1), resolve(var2)), false);
         }
      }

      private CalcCommand.Val postfix() {
         double var1 = this.primary();
         this.skipWs();
         if (this.peek() == '%') {
            this.pos++;
            return new CalcCommand.Val(var1, true);
         } else {
            return new CalcCommand.Val(var1, false);
         }
      }

      private double primary() {
         this.skipWs();
         if (this.pos >= this.src.length()) {
            throw this.err("Ожидалось число", this.pos);
         } else {
            char var1 = this.src.charAt(this.pos);
            if (var1 == '(') {
               int var2 = this.pos++;
               double var3 = this.expression();
               this.skipWs();
               if (this.peek() != ')') {
                  throw this.err("Не закрыта скобка", var2);
               } else {
                  this.pos++;
                  return var3;
               }
            } else if (Character.isDigit(var1) || var1 == '.') {
               return this.number();
            } else if (Character.isLetter(var1)) {
               return this.identifier();
            } else {
               throw this.err("Непонятный символ '" + var1 + "'", this.pos);
            }
         }
      }

      private double number() {
         int var1 = this.pos;
         boolean var2 = false;

         while (this.pos < this.src.length()) {
            char var3 = this.src.charAt(this.pos);
            if (Character.isDigit(var3)) {
               this.pos++;
            } else {
               if (var3 != '.' || var2) {
                  break;
               }

               var2 = true;
               this.pos++;
            }
         }

         String var7 = this.src.substring(var1, this.pos);
         if (var7.equals(".")) {
            throw this.err("Непонятное число", var1);
         } else {
            double var4 = Double.parseDouble(var7);

            int var6;
            for (var6 = 0; this.pos < this.src.length() && (this.src.charAt(this.pos) == 1082 || this.src.charAt(this.pos) == 'k'); this.pos++) {
               var6++;
            }

            if (var6 > 4) {
               throw this.err("Слишком длинный суффикс из 'к'", this.pos - var6);
            } else {
               if (var6 > 0) {
                  var4 *= Math.pow(1000.0, var6);
               }

               return var4;
            }
         }
      }

      private double identifier() {
         int var1 = this.pos;

         while (this.pos < this.src.length() && Character.isLetter(this.src.charAt(this.pos))) {
            this.pos++;
         }

         String var2 = this.src.substring(var1, this.pos);
         switch (var2) {
            case "pi":
            case "пи":
               return Math.PI;
            case "e":
               return Math.E;
            default:
               this.skipWs();
               if (this.peek() != '(') {
                  throw this.err("Неизвестная функция или константа '" + var2 + "'", var1);
               } else {
                  int var3 = this.pos++;
                  double var6 = this.expression();
                  this.skipWs();
                  if (this.peek() != ')') {
                     throw this.err("Не закрыта скобка", var3);
                  } else {
                     this.pos++;
                     return this.apply(var2, var6, var1);
                  }
               }
         }
      }

      private double apply(String var1, double var2, int var4) {
         return switch (var1) {
            case "sqrt" -> Math.sqrt(var2);
            case "cbrt" -> Math.cbrt(var2);
            case "abs" -> Math.abs(var2);
            case "round" -> Math.round(var2);
            case "floor" -> Math.floor(var2);
            case "ceil" -> Math.ceil(var2);
            case "sin" -> Math.sin(var2);
            case "cos" -> Math.cos(var2);
            case "tan" -> Math.tan(var2);
            case "ln" -> Math.log(var2);
            case "log" -> Math.log10(var2);
            default -> throw this.err("Неизвестная функция '" + var1 + "'", var4);
         };
      }

      private static double resolve(CalcCommand.Val var0) {
         return var0.pct() ? var0.v() / 100.0 : var0.v();
      }

      private char peek() {
         return this.pos < this.src.length() ? this.src.charAt(this.pos) : '\u0000';
      }

      private void skipWs() {
         while (this.pos < this.src.length() && Character.isWhitespace(this.src.charAt(this.pos))) {
            this.pos++;
         }
      }

      private CalcCommand.CalcException err(String var1, int var2) {
         return new CalcCommand.CalcException(var1 + " (позиция " + (var2 + 1) + ")");
      }
   }

   private static final class Val {
      private final double v;
      private final boolean pct;

      public double v() {
         return this.v;
      }

      public boolean pct() {
         return this.pct;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Val other)) return false;
         return java.util.Objects.equals(this.v, other.v) && java.util.Objects.equals(this.pct, other.pct);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(v, pct);
      }

      @Override
      public String toString() {
         return "Val[" + "v=" + this.v + ", " + "pct=" + this.pct + "]";
      }

      private Val(double var1, boolean var3) {
         this.v = var1;
         this.pct = var3;
      }
   }
}

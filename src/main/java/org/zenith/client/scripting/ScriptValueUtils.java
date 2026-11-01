package org.zenith.client.scripting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.TopLevel.Builtins;

public final class ScriptValueUtils {
   public static final int CONST_intValue = 32768;
   public static final int CONST_intValue2 = 32;
   public static final int CONST_intValue3 = 500000;

   private ScriptValueUtils() {
   }

   public static ScriptValueUtils.Entry2 method6246(Scriptable var0, ScriptableObject var1) {
      return new ScriptValueUtils.Entry2(var0, var1);
   }

   public static ScriptValueUtils.Entry2 method6247(Scriptable var0) {
      NativeObject var1 = new NativeObject();
      ScriptRuntime.setBuiltinProtoAndParent(var1, var0, Builtins.Object);
      return new ScriptValueUtils.Entry2(var0, var1);
   }

   public static ScriptValueUtils.Entry2 method6248(Scriptable var0) {
      return method6247(var0);
   }

   public static BaseFunction method6249(Scriptable var0, String var1, ScriptValueUtils.Handler var2) {
      Callable var3 = (var2x, var3x, var4, var5) -> {
         try {
            Object var6 = var2.call(var5);
            return var6 == null ? Undefined.instance : var6;
         } catch (RhinoException | ScriptContextFactory.Entry var7) {
            throw var7;
         } catch (ScriptValueUtils.Entry var8) {
            throw ScriptRuntime.constructError("Error", var8.getMessage());
         } catch (RuntimeException var9) {
            throw ScriptRuntime.constructError("Error", var1 + ": " + method6250(var9));
         }
      };
      return new LambdaFunction(var0, var1, 0, var3);
   }

   private static String method6250(RuntimeException var0) {
      String var1 = var0.getMessage();
      if (var1 != null && !var1.isBlank()) {
         return var1.length() > 200 ? var1.substring(0, 200) : var1;
      } else {
         return "внутренняя ошибка";
      }
   }

   public static ScriptValueUtils.Entry method6251(String var0) {
      return new ScriptValueUtils.Entry(var0);
   }

   public static Object arg(Object[] var0, int var1) {
      return var1 < var0.length ? var0[var1] : Undefined.instance;
   }

   public static boolean method6252(Object[] var0, int var1) {
      return var1 < var0.length && !method6253(var0[var1]);
   }

   public static boolean method6253(Object var0) {
      return var0 == null || var0 == Undefined.instance;
   }

   public static String method6254(Object[] var0, int var1, String var2) {
      Object var3 = arg(var0, var1);
      return method6253(var3) ? var2 : method6255(var3);
   }

   public static String method6255(Object var0) {
      if (method6253(var0)) {
         return "";
      } else if (var0 instanceof CharSequence var6) {
         if (var6.length() > 32768) {
            throw method6251("строка длиннее 32768 символов");
         } else {
            return var6.toString();
         }
      } else if (!(var0 instanceof Number var1)) {
         if (var0 instanceof Boolean var5) {
            return var5.toString();
         } else {
            String var4 = Context.toString(var0);
            if (var4.length() > 32768) {
               throw method6251("строка длиннее 32768 символов");
            } else {
               return var4;
            }
         }
      } else {
         double var2 = var1.doubleValue();
         return var2 == Math.rint(var2) && !Double.isInfinite(var2) && Math.abs(var2) < 1.0E15 ? Long.toString((long)var2) : Double.toString(var2);
      }
   }

   public static double method6256(Object[] var0, int var1, double var2) {
      Object var4 = arg(var0, var1);
      if (method6253(var4)) {
         return var2;
      } else {
         double var5 = Context.toNumber(var4);
         return Double.isNaN(var5) ? var2 : var5;
      }
   }

   public static int method6257(Object[] var0, int var1, int var2) {
      double var3 = method6256(var0, var1, var2);
      if (var3 > 2.147483647E9) {
         return Integer.MAX_VALUE;
      } else {
         return var3 < -2.1474836E9F ? Integer.MIN_VALUE : (int)var3;
      }
   }

   public static int method6258(Object[] var0, int var1, int var2) {
      Object var3 = arg(var0, var1);
      return method6253(var3) ? var2 : (int)((long)Context.toNumber(var3));
   }

   public static boolean method6259(Object[] var0, int var1, boolean var2) {
      Object var3 = arg(var0, var1);
      return method6253(var3) ? var2 : Context.toBoolean(var3);
   }

   public static Function method6260(Object[] var0, int var1) {
      if (arg(var0, var1) instanceof Function var3) {
         return var3;
      } else {
         throw method6251("ожидалась функция в аргументе " + (var1 + 1));
      }
   }

   public static Function method6261(Object[] var0, int var1) {
      return arg(var0, var1) instanceof Function var3 ? var3 : null;
   }

   public static Scriptable method6262(Object[] var0, int var1) {
      Object var2 = arg(var0, var1);
      return var2 instanceof Scriptable var3 && !(var2 instanceof Function) ? var3 : null;
   }

   public static Object method6263(Scriptable var0, String var1) {
      if (var0 == null) {
         return Undefined.instance;
      } else {
         Object var2 = ScriptableObject.getProperty(var0, var1);
         return var2 == Scriptable.NOT_FOUND ? Undefined.instance : var2;
      }
   }

   public static double method6264(Scriptable var0, String var1, double var2) {
      Object var4 = method6263(var0, var1);
      if (method6253(var4)) {
         return var2;
      } else {
         double var5 = Context.toNumber(var4);
         return Double.isNaN(var5) ? var2 : var5;
      }
   }

   public static boolean method6265(Scriptable var0, String var1, boolean var2) {
      Object var3 = method6263(var0, var1);
      return method6253(var3) ? var2 : Context.toBoolean(var3);
   }

   public static String method6266(Scriptable var0, String var1, String var2) {
      Object var3 = method6263(var0, var1);
      return method6253(var3) ? var2 : method6255(var3);
   }

   public static Scriptable method6267(Scriptable var0, List<?> var1) {
      NativeArray var2 = new NativeArray(var1.toArray());
      ScriptRuntime.setBuiltinProtoAndParent(var2, var0, Builtins.Array);
      return var2;
   }

   public static Scriptable method6268(Scriptable var0, Object[] var1) {
      NativeArray var2 = new NativeArray(var1);
      ScriptRuntime.setBuiltinProtoAndParent(var2, var0, Builtins.Array);
      return var2;
   }

   public static List<Object> method6269(Object var0) {
      ArrayList<Object> var1 = new ArrayList<>();
      if (var0 instanceof NativeArray var2) {
         long var3 = var2.getLength();
         if (var3 > 10000L) {
            throw method6251("массив длиннее 10000 элементов");
         }

         for (int var5 = 0; var5 < var3; var5++) {
            Object var6 = var2.get(var5, var2);
            var1.add(var6 == Scriptable.NOT_FOUND ? Undefined.instance : var6);
         }
      }

      return var1;
   }

   public static Scriptable method6270(Scriptable var0, double var1, double var3, double var5) {
      return method6247(var0).method6277("x", var1).method6277("y", var3).method6277("z", var5).getScriptableObject();
   }

   public static JsonElement method6271(Object var0, int var1) {
      return method6272(var0, var1, new int[1]);
   }

   private static JsonElement method6272(Object var0, int var1, int[] var2) {
      if (var1 > 32) {
         throw method6251("слишком глубокая вложенность");
      } else if (++var2[0] > 500000) {
         throw method6251("слишком большой объект (больше 500000 значений)");
      } else if (method6253(var0)) {
         return JsonNull.INSTANCE;
      } else if (var0 instanceof Boolean var15) {
         return new JsonPrimitive(var15);
      } else if (var0 instanceof Number var14) {
         return new JsonPrimitive(var14.doubleValue());
      } else if (var0 instanceof CharSequence || var0 instanceof ConsString) {
         return new JsonPrimitive(method6255(var0));
      } else if (var0 instanceof NativeArray var13) {
         JsonArray var16 = new JsonArray();

         for (Object var18 : method6269(var13)) {
            var16.add(method6272(var18, var1 + 1, var2));
         }

         return var16;
      } else if (var0 instanceof Function) {
         return JsonNull.INSTANCE;
      } else if (var0 instanceof Scriptable var3) {
         JsonObject var4 = new JsonObject();
         Object[] var5 = var3.getIds();
         if (var5.length > 10000) {
            throw method6251("объект больше 10000 ключей");
         } else {
            for (Object var9 : var5) {
               String var10 = method6255(var9);
               Object var11 = var9 instanceof Number var12 ? var3.get(var12.intValue(), var3) : var3.get(var10, var3);
               if (!(var11 instanceof Function)) {
                  var4.add(var10, method6272(var11, var1 + 1, var2));
               }
            }

            return var4;
         }
      } else {
         return new JsonPrimitive(method6255(var0));
      }
   }

   public static Object method6273(Scriptable var0, JsonElement var1) {
      if (var1 == null || var1.isJsonNull()) {
         return null;
      } else if (var1.isJsonPrimitive()) {
         JsonPrimitive var6 = var1.getAsJsonPrimitive();
         if (var6.isBoolean()) {
            return var6.getAsBoolean();
         } else {
            return var6.isNumber() ? var6.getAsDouble() : var6.getAsString();
         }
      } else if (var1.isJsonArray()) {
         ArrayList var5 = new ArrayList();

         for (JsonElement var8 : var1.getAsJsonArray()) {
            var5.add(method6273(var0, var8));
         }

         return method6267(var0, var5);
      } else {
         ScriptValueUtils.Entry2 var2 = method6247(var0);

         for (Map.Entry var4 : var1.getAsJsonObject().entrySet()) {
            var2.method6277((String)var4.getKey(), method6273(var0, (JsonElement)var4.getValue()));
         }

         return var2.getScriptableObject();
      }
   }

   public static final class Entry extends RuntimeException {
      public Entry(String var1) {
         super(var1, null, false, false);
      }
   }

   public static final class Entry2 {
      private final Scriptable scriptable;
      private final ScriptableObject scriptableObject;

      private Entry2(Scriptable var1, ScriptableObject var2) {
         this.scriptable = var1;
         this.scriptableObject = var2;
      }

      public ScriptValueUtils.Entry2 method6274(String var1, ScriptValueUtils.Handler var2) {
         this.scriptableObject.defineProperty(var1, ScriptValueUtils.method6249(this.scriptable, var1, var2), 3);
         return this;
      }

      public ScriptValueUtils.Entry2 method6275(String var1, Object var2) {
         this.scriptableObject.defineProperty(var1, var2, 1);
         return this;
      }

      public ScriptValueUtils.Entry2 method6276(String var1, Supplier<Object> var2) {
         this.scriptableObject.defineProperty(var1, ScriptValueUtils.method6249(this.scriptable, var1, var1x -> var2.get()), 3);
         return this;
      }

      public ScriptValueUtils.Entry2 method6277(String var1, Object var2) {
         this.scriptableObject.put(var1, this.scriptableObject, var2);
         return this;
      }

      public ScriptableObject getScriptableObject() {
         return this.scriptableObject;
      }
   }

   @FunctionalInterface
   public interface Handler {
      Object call(Object[] var1);
   }
}

package org.zenith.client.scripting;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Locale;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

public final class ScriptContextFactory extends ContextFactory {
   public static final ScriptContextFactory INSTANCE = new ScriptContextFactory();
   public static final long CONST_longValue = 250L;
   public static final long CONST_longValue2 = 3000L;
   public static final int CONST_intValue = 1000;
   private static final ClassShutter classShutter = var0 -> false;
   private static final WrapFactory wrapFactory = new WrapFactory() {
      @Override
      public Scriptable wrapAsJavaObject(Context var1, Scriptable var2, Object var3, Class<?> var4) {
         throw Context.reportRuntimeError("доступ к Java закрыт");
      }

      @Override
      public Scriptable wrapJavaClass(Context var1, Scriptable var2, Class<?> var3) {
         throw Context.reportRuntimeError("доступ к Java закрыт");
      }
   };
   private static final String[] texts = new String[]{
      "Packages",
      "java",
      "javax",
      "org",
      "com",
      "edu",
      "net",
      "getClass",
      "JavaAdapter",
      "JavaImporter",
      "importClass",
      "importPackage",
      "Continuation",
      "XML",
      "XMLList",
      "Namespace",
      "QName",
      "JavaException",
      "With",
      "Call",
      "load",
      "loadClass",
      "defineClass",
      "readFile",
      "readUrl",
      "runCommand",
      "spawn",
      "sync",
      "serialize",
      "deserialize",
      "ArrayBuffer",
      "SharedArrayBuffer",
      "DataView",
      "Int8Array",
      "Uint8Array",
      "Uint8ClampedArray",
      "Int16Array",
      "Uint16Array",
      "Int32Array",
      "Uint32Array",
      "Float32Array",
      "Float64Array",
      "BigInt64Array",
      "BigUint64Array"
   };
   private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
   private static final boolean CONST_flag = threadMXBean.isCurrentThreadCpuTimeSupported() && method7402();
   private static final MemoryPoolMXBean memoryPoolMXBean = method7403();
   private static final String text = "(function () {\n  var LIMIT = 1000000;\n  function guard(owner, name, check) {\n    var original = owner[name];\n    if (typeof original !== 'function') return;\n    Object.defineProperty(owner, name, {\n      value: function () { check.apply(this, arguments); return original.apply(this, arguments); },\n      writable: false, configurable: false, enumerable: false\n    });\n  }\n  function big(n) { return typeof n === 'number' && n > LIMIT; }\n  guard(String.prototype, 'repeat', function (n) {\n    if (String(this).length * (+n || 0) > LIMIT) throw new RangeError('строка больше миллиона символов');\n  });\n  guard(String.prototype, 'padStart', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(String.prototype, 'padEnd', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(Array.prototype, 'fill', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array.prototype, 'join', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array, 'from', function (src) { if (src != null && big(src.length)) throw new RangeError('массив больше миллиона элементов'); });\n  ['map', 'forEach', 'filter', 'reduce', 'reduceRight', 'some', 'every', 'indexOf', 'lastIndexOf', 'includes',\n   'find', 'findIndex', 'sort', 'reverse', 'splice', 'slice', 'concat', 'copyWithin', 'entries', 'keys', 'values',\n   'toString', 'toLocaleString', 'flat', 'flatMap'].forEach(function (name) {\n    guard(Array.prototype, name, function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  });\n  guard(String.prototype, 'split', function () { if (String(this).length > LIMIT) throw new RangeError('строка больше миллиона символов'); });\n})();\n";
   private static final org.mozilla.javascript.Script script = method7406();
   private static int intValue;

   private static boolean method7402() {
      try {
         if (!threadMXBean.isThreadCpuTimeEnabled()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
         }

         return threadMXBean.isThreadCpuTimeEnabled();
      } catch (RuntimeException var1) {
         return false;
      }
   }

   private static MemoryPoolMXBean method7403() {
      for (MemoryPoolMXBean var1 : ManagementFactory.getMemoryPoolMXBeans()) {
         if (var1.getType() == MemoryType.HEAP && var1.isCollectionUsageThresholdSupported()) {
            String var2 = var1.getName().toLowerCase(Locale.ROOT);
            if (var2.contains("old") || var2.contains("tenured")) {
               return var1;
            }
         }
      }

      return null;
   }

   private static long method7404() {
      return CONST_flag ? threadMXBean.getCurrentThreadCpuTime() / 1000000L : System.currentTimeMillis();
   }

   public static boolean method7405() {
      if (memoryPoolMXBean != null) {
         MemoryUsage var0 = memoryPoolMXBean.getCollectionUsage();
         long var1 = memoryPoolMXBean.getUsage().getMax();
         if (var0 != null && var1 > 0L && var0.getUsed() > var1 * 0.94) {
            return true;
         }
      }

      Runtime var3 = Runtime.getRuntime();
      return var3.maxMemory() - (var3.totalMemory() - var3.freeMemory()) < 50331648L;
   }

   private ScriptContextFactory() {
   }

   @Override
   public Context makeContext() {
      Context var1 = super.makeContext();
      var1.setLanguageVersion(200);
      var1.setOptimizationLevel(-1);
      var1.setMaximumInterpreterStackDepth(1000);
      var1.setInstructionObserverThreshold(5000);
      var1.setClassShutter(classShutter);
      var1.setWrapFactory(wrapFactory);
      var1.setGeneratingDebug(true);
      return var1;
   }

   @Override
   public boolean hasFeature(Context var1, int var2) {
      return switch (var2) {
         case 6, 13, 20, 21 -> false;
         default -> super.hasFeature(var1, var2);
         case 10, 14 -> true;
      };
   }

   @Override
   public void observeInstructionCount(Context var1, int var2) {
      if (var1.getThreadLocal(ScriptContextFactory.Entry2.class) instanceof ScriptContextFactory.Entry2 var4 && var4.longValue != 0L) {
         long var5 = System.currentTimeMillis();
         if (method7404() <= var4.longValue && var5 <= var4.longValue2) {
            if (var5 - var4.longValue3 > 20L) {
               var4.longValue3 = var5;
               if (method7405()) {
                  throw new ScriptContextFactory.Entry("скрипт исчерпал память");
               }
            }
         } else {
            throw new ScriptContextFactory.Entry("скрипт превысил лимит времени (вечный цикл?)");
         }
      }
   }

   private static org.mozilla.javascript.Script method7406() {
      Context var0 = INSTANCE.enterContext();

      org.mozilla.javascript.Script var1;
      try {
         var1 = var0.compileString(
            "(function () {\n  var LIMIT = 1000000;\n  function guard(owner, name, check) {\n    var original = owner[name];\n    if (typeof original !== 'function') return;\n    Object.defineProperty(owner, name, {\n      value: function () { check.apply(this, arguments); return original.apply(this, arguments); },\n      writable: false, configurable: false, enumerable: false\n    });\n  }\n  function big(n) { return typeof n === 'number' && n > LIMIT; }\n  guard(String.prototype, 'repeat', function (n) {\n    if (String(this).length * (+n || 0) > LIMIT) throw new RangeError('строка больше миллиона символов');\n  });\n  guard(String.prototype, 'padStart', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(String.prototype, 'padEnd', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(Array.prototype, 'fill', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array.prototype, 'join', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array, 'from', function (src) { if (src != null && big(src.length)) throw new RangeError('массив больше миллиона элементов'); });\n  ['map', 'forEach', 'filter', 'reduce', 'reduceRight', 'some', 'every', 'indexOf', 'lastIndexOf', 'includes',\n   'find', 'findIndex', 'sort', 'reverse', 'splice', 'slice', 'concat', 'copyWithin', 'entries', 'keys', 'values',\n   'toString', 'toLocaleString', 'flat', 'flatMap'].forEach(function (name) {\n    guard(Array.prototype, name, function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  });\n  guard(String.prototype, 'split', function () { if (String(this).length > LIMIT) throw new RangeError('строка больше миллиона символов'); });\n})();\n",
            "guards",
            1,
            null
         );
      } finally {
         Context.exit();
      }

      return var1;
   }

   public static void method7407() {
      if ((++intValue & 4095) == 0) {
         Context var0 = Context.getCurrentContext();
         if (var0 != null && var0.getFactory() == INSTANCE) {
            if (var0.getThreadLocal(ScriptContextFactory.Entry2.class) instanceof ScriptContextFactory.Entry2 var2
               && var2.longValue != 0L
               && (method7404() > var2.longValue || System.currentTimeMillis() > var2.longValue2)) {
               throw new ScriptContextFactory.Entry("регулярное выражение работает слишком долго");
            }
         }
      }
   }

   public static ScriptableObject method7408(Context var0) {
      ScriptableObject var1 = var0.initSafeStandardObjects(null, false);

      for (String var5 : texts) {
         var1.delete(var5);
      }

      script.exec(var0, var1);
      return var1;
   }

   public static Context method7409() {
      return INSTANCE.enterContext();
   }

   public static Object method7410(long var0, Callable var2, Scriptable var3, Object[] var4) {
      Context var5 = INSTANCE.enterContext();
      ScriptContextFactory.Entry2 var6 = var5.getThreadLocal(ScriptContextFactory.Entry2.class) instanceof ScriptContextFactory.Entry2 var7
         ? var7
         : new ScriptContextFactory.Entry2();
      long var14 = var6.longValue;
      boolean var9 = var14 == 0L;

      Object var10;
      try {
         if (var9) {
            var6.longValue = method7404() + var0;
            var6.longValue2 = System.currentTimeMillis() + var0 * 6L + 500L;
            var6.longValue3 = 0L;
            var5.putThreadLocal(ScriptContextFactory.Entry2.class, var6);
         }

         var10 = var2.call(var5, var3, var3, var4);
      } finally {
         if (var9) {
            var6.longValue = 0L;
         }

         Context.exit();
      }

      return var10;
   }

   public static final class Entry extends Error {
      public Entry(String var1) {
         super(var1, null, false, false);
      }
   }

   private static final class Entry2 {
      public long longValue;
      public long longValue2;
      public long longValue3;
   }
}

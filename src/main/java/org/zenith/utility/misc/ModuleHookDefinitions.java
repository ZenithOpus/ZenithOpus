package org.zenith.utility.misc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.math.Vec3d;

public final class ModuleHookDefinitions {
   private static final Map<String, Class12<?>> items = new LinkedHashMap<>();
   public static final Class12<ModuleHookDefinitions.Entry6> class12 = method6116(
      "entity_reach", ModuleHookDefinitions.Entry6.class, "дистанция удара по сущностям: e.value, e.vanilla; e.set(v)"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class122 = method6116(
      "block_reach", ModuleHookDefinitions.Entry6.class, "дистанция взаимодействия с блоками: e.value, e.vanilla; e.set(v)"
   );
   public static final Class12<ModuleHookDefinitions.Entry4> class123 = method6116(
      "firework_boost", ModuleHookDefinitions.Entry4.class, "разгон фейерверком на элитрах: e.velocity, e.player, e.look, e.life; e.set(x, y, z)"
   );
   public static final Class12<ModuleHookDefinitions.Entry5> class124 = method6116(
      "text", ModuleHookDefinitions.Entry5.class, "любой текст игры перед отрисовкой: e.text; e.setText(s), e.replace(a, b) — очень частый хук"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class125 = method6116(
      "fire_overlay", ModuleHookDefinitions.Entry.class, "оверлей огня на экране: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class126 = method6116(
      "wall_overlay", ModuleHookDefinitions.Entry.class, "оверлей блока в голове: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class127 = method6116(
      "darkness", ModuleHookDefinitions.Entry6.class, "затемнение от эффекта Darkness 0..1: e.value; e.set(v)"
   );
   public static final Class12<ModuleHookDefinitions.Entry3> class128 = method6116(
      "effect_fog", ModuleHookDefinitions.Entry3.class, "туман от эффекта: e.kind (blindness или darkness); e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class129 = method6116(
      "hurt_cam", ModuleHookDefinitions.Entry.class, "тряска камеры при уроне: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class1210 = method6116(
      "view_bob", ModuleHookDefinitions.Entry.class, "покачивание камеры при ходьбе: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1211 = method6116(
      "brightness", ModuleHookDefinitions.Entry6.class, "яркость (gamma) 0..1+, e.value; e.set(v) — например 10 для полной яркости"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1212 = method6116(
      "world_time", ModuleHookDefinitions.Entry6.class, "время суток мира на клиенте в тиках (0..24000): e.value; e.set(t) — только визуально"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1213 = method6116(
      "rain", ModuleHookDefinitions.Entry6.class, "сила дождя на клиенте 0..1: e.value; e.set(v) — только визуально"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1214 = method6116(
      "thunder", ModuleHookDefinitions.Entry6.class, "сила грозы на клиенте 0..1: e.value; e.set(v) — только визуально"
   );
   public static final Class12<ModuleHookDefinitions.Entry2> class1215 = method6116(
      "sound", ModuleHookDefinitions.Entry2.class, "игровой звук перед проигрыванием: e.id, e.category, e.x, e.y, e.z, e.volume, e.pitch; e.cancel()"
   );

   private static <C> Class12<C> method6116(String var0, Class<C> var1, String var2) {
      Class12 var3 = new Class12(var0, var1, var2);
      items.put(var0, var3);
      return var3;
   }

   private ModuleHookDefinitions() {
   }

   public static Map<String, Class12<?>> method6117() {
      return Collections.unmodifiableMap(items);
   }

   public static double method6118(Class12<ModuleHookDefinitions.Entry6> var0, double var1) {
      return var0.active() ? var0.method1645(new ModuleHookDefinitions.Entry6(var1)).value : var1;
   }

   public static boolean method6119(Class12<ModuleHookDefinitions.Entry> var0) {
      return var0.active() && var0.method1645(new ModuleHookDefinitions.Entry()).cancelled;
   }

   public static final class Entry {
      public boolean cancelled;
   }

   public static final class Entry2 {
      public final String text;
      public final String text2;
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final float floatValue;
      public final float floatValue2;
      public boolean cancelled;

      public Entry2(String var1, String var2, double var3, double var5, double var7, float var9, float var10) {
         this.text = var1;
         this.text2 = var2;
         this.doubleValue = var3;
         this.doubleValue2 = var5;
         this.doubleValue3 = var7;
         this.floatValue = var9;
         this.floatValue2 = var10;
      }
   }

   public static final class Entry3 {
      public final String text;
      public boolean cancelled;

      public Entry3(String var1) {
         this.text = var1;
      }
   }

   public static final class Entry4 {
      public Vec3d vec3d;
      public final Vec3d vec3d2;
      public final Vec3d vec3d3;
      public final Vec3d vec3d4;
      public final int intValue;
      public final int intValue2;

      public Entry4(Vec3d var1, Vec3d var2, Vec3d var3, int var4, int var5) {
         this.vec3d = var1;
         this.vec3d2 = var1;
         this.vec3d3 = var2;
         this.vec3d4 = var3;
         this.intValue = var4;
         this.intValue2 = var5;
      }
   }

   public static final class Entry5 {
      public String text;

      public Entry5(String var1) {
         this.text = var1;
      }
   }

   public static final class Entry6 {
      public double value;
      public final double doubleValue;

      public Entry6(double var1) {
         this.value = var1;
         this.doubleValue = var1;
      }
   }
}

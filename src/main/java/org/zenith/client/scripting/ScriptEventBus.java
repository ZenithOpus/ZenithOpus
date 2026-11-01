package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.events.Cancellable;
import com.darkmagician6.eventapi.events.Event;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.KeyEvent;
import org.zenith.PacketEvent;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.BlockBreakingEvent;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.ChatSendEvent;
import org.zenith.client.events.ClickSlotEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.Event6;
import org.zenith.client.events.FovEvent;
import org.zenith.client.events.JumpEvent;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.SetScreenEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;

public final class ScriptEventBus {
   public static final Map<String, ScriptEventBus.Data> items = new LinkedHashMap<>();
   public static final Map<String, Script.Type> items2 = new LinkedHashMap<>();
   public static final Map<String, String> items3 = new LinkedHashMap<>();

   private static void method1864(String var0, Class<? extends Event> var1, boolean var2, String var3, BiFunction<Scriptable, Event, Scriptable> var4) {
      items.put(var0, new ScriptEventBus.Data(var0, var1, var2, true, var4, var3, List.of()));
   }

   private static void method1865(String var0, Script.Type var1, String var2) {
      items2.put(var0, var1);
      items3.put(var0, var2);
   }

   private static ScriptValueUtils.Entry2 method1866(Scriptable var0, Event var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var0);
      if (var1 instanceof Cancellable var3) {
         var2.method6274("cancel", var1x -> {
            var3.setCancelled(!ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true));
            return null;
         });
         var2.method6274("cancelled", var1x -> var3.isCancelled());
      }

      return var2;
   }

   private ScriptEventBus() {
   }

   public static List<String> method1867() {
      ArrayList<String> var0 = new ArrayList<>();

      for (ScriptEventBus.Data var2 : items.values()) {
         var0.add("`" + var2.name() + "`" + (var2.isFlag() ? " *(отменяемое)*" : "") + " — " + var2.getText2());
      }

      for (Entry var4 : items3.entrySet()) {
         var0.add("`" + (String)var4.getKey() + "` — " + (String)var4.getValue());
      }

      return var0;
   }

   public static byte method1868(Object var0) {
      if (ScriptValueUtils.method6253(var0)) {
         return 2;
      } else {
         String var1 = ScriptValueUtils.method6255(var0).toLowerCase(Locale.ROOT);

         return switch (var1) {
            case "highest" -> 0;
            case "high" -> 1;
            case "normal", "medium" -> 2;
            case "low" -> 3;
            case "lowest" -> 4;
            default -> throw ScriptValueUtils.method6251("неизвестный приоритет: " + ScriptValueUtils.method6255(var0));
         };
      }
   }

   public static Scriptable method1869(Storage var0, Script var1, Scriptable var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      int[] var4 = new int[]{0};
      LinkedHashMap var5 = new LinkedHashMap();
      BiFunction<String, Object[], Object> var6 = (var4x, var5x) -> {
         var var6x = ScriptValueUtils.method6260(var5x, 1);
         Script.Type var7 = items2.get(var4x);
         if (var7 != null) {
            ScriptScreenApi.method7046(var1, var7, var6x);
            return -1.0;
         } else {
            ScriptEventBus.Data var8x = items.get(var4x);
            if (var8x == null) {
               throw ScriptValueUtils.method6251("неизвестное событие: " + var4x + " (список — events.names())");
            } else {
               byte var9 = method1868(ScriptValueUtils.arg(var5x, 2));
               ArrayList var10x = new ArrayList();
               var10x.add(EventManager.main().subscribe(var8x.getClassValue(), var9, (var4xx, var5xx) -> method1870(var0, var1, var8x, var6x, (Event)var5xx)));

               for (Class var12 : var8x.getItems()) {
                  var10x.add(EventManager.main().subscribe(var12, var9, (var4xx, var5xx) -> method1870(var0, var1, var8x, var6x, (Event)var5xx)));
               }

               int var13 = ++var4[0];
               AutoCloseable var14 = () -> var10x.forEach(r -> ((Runnable)r).run());
               var5.put(var13, var14);
               var1.method1413(var14);
               return (double)var13;
            }
         }
      };
      var3.method6274("on", var1x -> var6.apply(ScriptValueUtils.method6254(var1x, 0, ""), var1x));
      var3.method6274("once", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");
         var var5x = ScriptValueUtils.method6260(var3x, 1);
         int[] var6x = new int[]{0};
         boolean[] var7 = new boolean[]{false};
         BaseFunction var8x = ScriptValueUtils.method6249(var2, "once", var5xx -> {
            if (var7[0]) {
               return null;
            } else {
               var7[0] = true;
               AutoCloseable var6xx = (AutoCloseable)var5.remove(var6x[0]);
               if (var6xx != null) {
                  try {
                     var6xx.close();
                  } catch (Exception var8xx) {
                  }
               }

               return var5x.call(Context.getCurrentContext(), var2, var2, var5xx);
            }
         });
         Object var9 = var6.apply(var4x, new Object[]{var4x, var8x, ScriptValueUtils.arg(var3x, 2)});
         var6x[0] = (int)Context.toNumber(var9);
         return var9;
      });
      var3.method6274("off", var1x -> {
         AutoCloseable var2x = (AutoCloseable)var5.remove(ScriptValueUtils.method6257(var1x, 0, -1));
         if (var2x == null) {
            return false;
         } else {
            try {
               var2x.close();
            } catch (Exception var4x) {
            }

            return true;
         }
      });
      var3.method6274("names", var1x -> {
         var var2x = new ArrayList<>(items.keySet());
         var2x.addAll(items2.keySet());
         return ScriptValueUtils.method6267(var2, var2x);
      });

      for (String var8 : items.keySet()) {
         var3.method6274(var8, var2x -> var6.apply(var8, new Object[]{var8, ScriptValueUtils.arg(var2x, 0), ScriptValueUtils.arg(var2x, 1)}));
      }

      for (String var10 : items2.keySet()) {
         var3.method6274(var10, var2x -> var6.apply(var10, new Object[]{var10, ScriptValueUtils.arg(var2x, 0)}));
      }

      return var3.getScriptableObject();
   }

   private static void method1870(Storage var0, Script var1, ScriptEventBus.Data var2, Function var3, Event var4) {
      if (var1.isRunning() && ScriptEntityApi.method6575().isOnThread()) {
         if (!(var4 instanceof PacketEvent var5 && !var5.method7099())) {
            if (!(var4 instanceof Cancellable var21 && var21.isCancelled() && var2.isFlag())) {
               Scriptable var22 = ScriptScreenApi.method7044(var1);
               if (var22 != null) {
                  if (var4 instanceof DrawEvent var6) {
                     ScriptDrawApi.method2597(var6.getCustomDrawContext());
                     ScriptEntityApi.method6573(var6.getTickDelta());
                  }

                  if (var4 instanceof WorldRenderEvent var23) {
                     ScriptDrawApi.method2598(var23.getMatrixStack());
                     ScriptEntityApi.method6573(var23.getFloatValue());
                  }

                  try {
                     Context var7 = ScriptContextFactory.method7409();

                     Scriptable var8;
                     try {
                        var8 = var2.getBiFunction().apply(var22, var4);
                     } catch (RuntimeException var18) {
                        return;
                     } finally {
                        Context.exit();
                     }

                     Object var24 = var0.method1321(var1, var3, var8);
                     if (var2.isFlag() && var4 instanceof Cancellable var9 && var24 != null && !ScriptValueUtils.method6253(var24) && Context.toBoolean(var24)) {
                        var9.setCancelled(true);
                     }
                  } finally {
                     if (var4 instanceof DrawEvent) {
                        ScriptDrawApi.end();
                     }

                     if (var4 instanceof WorldRenderEvent) {
                        ScriptDrawApi.method2599();
                     }
                  }
               }
            }
         }
      }
   }

   static {
      method1864("tick", TickEvent.class, false, "каждый тик клиента (20/с), и в меню", (var0, var1) -> method1866(var0, var1).getScriptableObject());
      method1864("update", UpdateEvent.class, false, "тик своего игрока, до физики", (var0, var1) -> method1866(var0, var1).getScriptableObject());
      method1864(
         "render2d",
         DrawEvent.class,
         false,
         "каждый кадр поверх HUD: рисуй render.* ; e.partial",
         (var0, var1) -> method1866(var0, var1).method6277("partial", (double)((DrawEvent)var1).getTickDelta()).getScriptableObject()
      );
      method1864(
         "render3d",
         WorldRenderEvent.class,
         false,
         "каждый кадр мира: рисуй render3d.* ; e.partial",
         (var0, var1) -> method1866(var0, var1).method6277("partial", (double)((WorldRenderEvent)var1).getFloatValue()).getScriptableObject()
      );
      method1864(
         "key",
         KeyEvent.class,
         false,
         "клавиша: key, action, mods, press, release, repeat, screenOpen",
         (var0, var1) -> {
            KeyEvent var2 = (KeyEvent)var1;
            return method1866(var0, var1)
               .method6277("key", (double)var2.getKeyCode())
               .method6277("action", (double)var2.getIntValue())
               .method6277("mods", (double)var2.getModifiers())
               .method6277("press", var2.getIntValue() == 1)
               .method6277("release", var2.getIntValue() == 0)
               .method6277("repeat", var2.getIntValue() == 2)
               .method6277("screenOpen", ScriptEntityApi.method6575().currentScreen != null)
               .getScriptableObject();
         }
      );
      method1864(
         "mouse",
         MouseButtonEvent.class,
         true,
         "кнопка мыши: button, press, release, screenOpen",
         (var0, var1) -> {
            MouseButtonEvent var2 = (MouseButtonEvent)var1;
            return method1866(var0, var1)
               .method6277("button", (double)var2.getIntValue())
               .method6277("press", var2.getIntValue2() == 1)
               .method6277("release", var2.getIntValue2() == 0)
               .method6277("screenOpen", ScriptEntityApi.method6575().currentScreen != null)
               .getScriptableObject();
         }
      );
      method1864("scroll", MouseScrollEvent.class, true, "прокрутка колеса в игре: vertical, horizontal", (var0, var1) -> {
         MouseScrollEvent var2 = (MouseScrollEvent)var1;
         return method1866(var0, var1).method6277("vertical", var2.getDoubleValue2()).method6277("horizontal", var2.getDoubleValue()).getScriptableObject();
      });
      method1864("look", MouseRotationEvent.class, true, "поворот мышью за кадр: dx, dy; setDx(v), setDy(v)", (var0, var1) -> {
         MouseRotationEvent var2 = (MouseRotationEvent)var1;
         return method1866(var0, var1).method6277("dx", var2.getDoubleValue()).method6277("dy", var2.getDoubleValue2()).method6274("setDx", var1x -> {
            var2.setDoubleValue(ScriptValueUtils.method6256(var1x, 0, var2.getDoubleValue()));
            return null;
         }).method6274("setDy", var1x -> {
            var2.setDoubleValue2(ScriptValueUtils.method6256(var1x, 0, var2.getDoubleValue2()));
            return null;
         }).getScriptableObject();
      });
      method1864(
         "attack",
         AttackEvent.class,
         true,
         "удар по сущности: target, stage ('pre'/'post')",
         (var0, var1) -> {
            AttackEvent var2 = (AttackEvent)var1;
            return method1866(var0, var1)
               .method6277("target", ScriptEntityApi.method6576(var0, var2.getEntity()))
               .method6277("stage", var2.getType4().name().toLowerCase(Locale.ROOT))
               .getScriptableObject();
         }
      );
      method1864(
         "move",
         PlayerMoveEvent.class,
         true,
         "вектор движения тика: x, y, z; set(x, y, z)",
         (var0, var1) -> {
            PlayerMoveEvent var2 = (PlayerMoveEvent)var1;
            Vec3d var3 = var2.getVec3d();
            return method1866(var0, var1)
               .method6277("x", var3.x)
               .method6277("y", var3.y)
               .method6277("z", var3.z)
               .method6274(
                  "set",
                  var1x -> {
                     Vec3d var2x = var2.getVec3d();
                     var2.setVec3d(
                        new Vec3d(
                           ScriptValueUtils.method6256(var1x, 0, var2x.x),
                           ScriptValueUtils.method6256(var1x, 1, var2x.y),
                           ScriptValueUtils.method6256(var1x, 2, var2x.z)
                        )
                     );
                     return null;
                  }
               )
               .method6274("setX", var1x -> {
                  Vec3d var2x = var2.getVec3d();
                  var2.setVec3d(new Vec3d(ScriptValueUtils.method6256(var1x, 0, var2x.x), var2x.y, var2x.z));
                  return null;
               })
               .method6274("setY", var1x -> {
                  Vec3d var2x = var2.getVec3d();
                  var2.setVec3d(new Vec3d(var2x.x, ScriptValueUtils.method6256(var1x, 0, var2x.y), var2x.z));
                  return null;
               })
               .method6274("setZ", var1x -> {
                  Vec3d var2x = var2.getVec3d();
                  var2.setVec3d(new Vec3d(var2x.x, var2x.y, ScriptValueUtils.method6256(var1x, 0, var2x.z)));
                  return null;
               })
               .getScriptableObject();
         }
      );
      method1864(
         "move_input",
         MoveInputEvent.class,
         true,
         "ввод движения: forward, back, left, right, jump, sneak, sprint; setForward(b)... set(forward, strafe), none()",
         (var0, var1) -> {
            MoveInputEvent var2 = (MoveInputEvent)var1;
            PlayerInput var3 = var2.getPlayerInput();
            return method1866(var0, var1)
               .method6277("forward", var3.forward())
               .method6277("back", var3.backward())
               .method6277("left", var3.left())
               .method6277("right", var3.right())
               .method6277("jump", var3.jump())
               .method6277("sneak", var3.sneak())
               .method6277("sprint", var3.sprint())
               .method6274("setForward", var1x -> {
                  var2.method3038(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setBack", var1x -> {
                  var2.method3047(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setLeft", var1x -> {
                  var2.method3045(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setRight", var1x -> {
                  var2.method3046(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setJump", var1x -> {
                  var2.method3041(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setSneak", var1x -> {
                  var2.method3040(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setSprint", var1x -> {
                  var2.method3039(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("set", var1x -> {
                  var2.method3042((float)ScriptValueUtils.method6256(var1x, 0, 0.0), (float)ScriptValueUtils.method6256(var1x, 1, 0.0));
                  return null;
               })
               .method6274("none", var1x -> {
                  var2.method3044();
                  return null;
               })
               .getScriptableObject();
         }
      );
      method1864("jump", JumpEvent.class, false, "игрок прыгает", (var0, var1) -> method1866(var0, var1).getScriptableObject());
      method1864("sprint", SprintUpdateEvent.class, true, "можно ли бежать: canSprint; setCanSprint(b)", (var0, var1) -> {
         SprintUpdateEvent var2 = (SprintUpdateEvent)var1;
         return method1866(var0, var1).method6277("canSprint", var2.isFlag2()).method6274("setCanSprint", var1x -> {
            var2.setFlag2(ScriptValueUtils.method6259(var1x, 0, true));
            return null;
         }).getScriptableObject();
      });
      method1864("fov", FovEvent.class, true, "поле зрения: fov; setFov(n)", (var0, var1) -> {
         FovEvent var2 = (FovEvent)var1;
         return method1866(var0, var1).method6277("fov", (double)var2.getIntValue()).method6274("setFov", var1x -> {
            var2.setIntValue(Math.max(1, Math.min(179, ScriptValueUtils.method6257(var1x, 0, var2.getIntValue()))));
            return null;
         }).getScriptableObject();
      });
      method1864(
         "click_slot",
         ClickSlotEvent.class,
         true,
         "клик по слоту: windowId, slot, button, action",
         (var0, var1) -> {
            ClickSlotEvent var2 = (ClickSlotEvent)var1;
            return method1866(var0, var1)
               .method6277("windowId", (double)var2.getIntValue())
               .method6277("slot", (double)var2.getIntValue2())
               .method6277("button", (double)var2.getIntValue3())
               .method6277("action", var2.getSlotActionType() == null ? "" : var2.getSlotActionType().name().toLowerCase(Locale.ROOT))
               .getScriptableObject();
         }
      );
      method1864(
         "break_block",
         BlockBreakingEvent.class,
         true,
         "ломание блока: x, y, z, side",
         (var0, var1) -> {
            BlockBreakingEvent var2 = (BlockBreakingEvent)var1;
            return method1866(var0, var1)
               .method6277("x", (double)var2.getBlockPos().getX())
               .method6277("y", (double)var2.getBlockPos().getY())
               .method6277("z", (double)var2.getBlockPos().getZ())
               .method6277("side", var2.getDirection() == null ? "" : var2.getDirection().asString())
               .getScriptableObject();
         }
      );
      method1864("chat", ChatReceiveEvent.class, true, "входящее сообщение чата: text; setText(s) — подменить", (var0, var1) -> {
         ChatReceiveEvent var2 = (ChatReceiveEvent)var1;
         return method1866(var0, var1).method6277("text", var2.getText() == null ? "" : var2.getText().getString()).method6274("setText", var1x -> {
            var2.method7858(Text.literal(ScriptValueUtils.method6254(var1x, 0, "")));
            return null;
         }).getScriptableObject();
      });
      method1864(
         "chat_send",
         ChatSendEvent.class,
         true,
         "своё сообщение перед отправкой: text, command",
         (var0, var1) -> {
            ChatSendEvent var2 = (ChatSendEvent)var1;
            return method1866(var0, var1)
               .method6277("text", var2.getMessage() == null ? "" : var2.getMessage())
               .method6277("command", var2.method4602())
               .method6277("pre", var2.method4603())
               .getScriptableObject();
         }
      );
      items.put(
         "packet",
         new ScriptEventBus.Data(
            "packet",
            PacketEvent.class,
            true,
            true,
            (var0, var1) -> {
               Packet var2 = var1 instanceof PacketEvent var3 ? var3.getPacket3() : ((PacketReceiveEvent)var1).getPacket();
               boolean var5 = var1 instanceof PacketEvent var4 && var4.method7099();
               return method1866(var0, var1)
                  .method6277("name", ScriptEntityEvents.method7557(var2))
                  .method6277("send", var5)
                  .method6277("receive", !var5)
                  .method6274("data", var2x -> ScriptEntityEvents.method7558(var0, var2))
                  .getScriptableObject();
            },
            "пакет: name, send, receive, data(); отмена работает в обе стороны",
            List.of(PacketReceiveEvent.class)
         )
      );
      method1864("respawn", Event6.class, false, "возрождение или смена измерения", (var0, var1) -> method1866(var0, var1).getScriptableObject());
      method1864(
         "entity_spawn",
         EntityAddEvent.class,
         false,
         "сущность появилась: entity",
         (var0, var1) -> method1866(var0, var1)
            .method6277("entity", ScriptEntityApi.method6576(var0, ((EntityAddEvent)var1).getEntity()))
            .getScriptableObject()
      );
      method1864(
         "entity_death",
         EntityDeathEvent.class,
         false,
         "сущность умерла: entity",
         (var0, var1) -> method1866(var0, var1)
            .method6277("entity", ScriptEntityApi.method6576(var0, ((EntityDeathEvent)var1).getLivingEntity()))
            .getScriptableObject()
      );
      method1864("module_toggle", ModuleToggleEvent.class, false, "модуль переключён: name, enabled", (var0, var1) -> {
         ModuleToggleEvent var2 = (ModuleToggleEvent)var1;
         return method1866(var0, var1).method6277("name", var2.getModule().getName()).method6277("enabled", var2.isEnabled()).getScriptableObject();
      });
      method1864("set_screen", SetScreenEvent.class, false, "открывается экран: screen (имя класса или '')", (var0, var1) -> {
         SetScreenEvent var2 = (SetScreenEvent)var1;
         return method1866(var0, var1).method6277("screen", ScriptScreenApi.method7047(var2.getScreen())).getScriptableObject();
      });
      method1865("enable", Script.Type.ENABLE, "скрипт запущен");
      method1865("disable", Script.Type.DISABLE, "скрипт остановлен или перезагружается");
      method1865("world_change", Script.Type.WORLD_CHANGE, "сменился мир (вход, измерение)");
      method1865("health_change", Script.Type.HEALTH_CHANGE, "здоровье изменилось: e.from, e.to");
      method1865("death", Script.Type.DEATH, "свой игрок умер");
      method1865("title", Script.Type.TITLE, "титр на экране: e.kind (title/subtitle), e.text");
      method1865("actionbar", Script.Type.ACTIONBAR, "надпись над хотбаром: e.text");
      method1865("bossbar", Script.Type.BOSSBAR, "изменились боссбары: e.bars[{name, percent, color}]");
      method1865("sound_received", Script.Type.SOUND_RECEIVED, "сервер проиграл звук: e.id, e.category, e.x, e.y, e.z, e.volume, e.pitch");
      method1865("item_pickup", Script.Type.ITEM_PICKUP, "ты подобрал предмет: e — предмет, e.picked — сколько");
      method1865("block_update", Script.Type.BLOCK_UPDATE, "блок изменился: e.x, e.y, e.z, e.id, e.previous");
      method1865("container_open", Script.Type.CONTAINER_OPEN, "сервер открыл контейнер: e.type, e.title, e.syncId");
      method1865("server_join", Script.Type.SERVER_JOIN, "вход на сервер: e.address");
      method1865("server_leave", Script.Type.SERVER_LEAVE, "выход с сервера");
      method1865("screen", Script.Type.SCREEN, "сменился экран: e.screen, e.previous");
   }

   public static final class Data {
      private final String text;
      private final Class<? extends Event> classValue;
      private final boolean flag;
      private final boolean flag2;
      private final BiFunction<Scriptable, Event, Scriptable> biFunction;
      private final String text2;
      private final List<Class<? extends Event>> items;

      public String text() {
         return this.text;
      }

      public Class<? extends Event> classValue() {
         return this.classValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public BiFunction<Scriptable, Event, Scriptable> biFunction() {
         return this.biFunction;
      }

      public String text2() {
         return this.text2;
      }

      public List<Class<? extends Event>> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.classValue, other.classValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.biFunction, other.biFunction) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, classValue, flag, flag2, biFunction, text2, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "classValue=" + this.classValue + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "biFunction=" + this.biFunction + ", " + "text2=" + this.text2 + ", " + "items=" + this.items + "]";
      }

      public Data(
         String var1,
         Class<? extends Event> var2,
         boolean var3,
         boolean var4,
         BiFunction<Scriptable, Event, Scriptable> var5,
         String var6,
         List<Class<? extends Event>> var7
      ) {
         this.text = var1;
         this.classValue = var2;
         this.flag = var3;
         this.flag2 = var4;
         this.biFunction = var5;
         this.text2 = var6;
         this.items = var7;
      }

      public String name() {
         return this.text;
      }

      public Class<? extends Event> getClassValue() {
         return this.classValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public BiFunction<Scriptable, Event, Scriptable> getBiFunction() {
         return this.biFunction;
      }

      public String getText2() {
         return this.text2;
      }

      public List<Class<? extends Event>> getItems() {
         return this.items;
      }
   }
}

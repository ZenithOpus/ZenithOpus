package org.zenith.client.scripting;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.process.ICustomGoalProcess;
import com.darkmagician6.eventapi.EventManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.Heightmap.Type;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.utility.misc.ScriptPermissions;

public final class ScriptNotifyApi {
   private static final int CONST_intValue = 300;
   private static final Deque<Object[]> deque = new ArrayDeque<>();
   private static final Map<Script, Deque<String>> items = new WeakHashMap<>();
   private static final Map<Script, long[]> items2 = new WeakHashMap<>();
   private static final Map<Script, ScriptNotifyApi.Entry> items3 = new WeakHashMap<>();
   private static final Map<Script, List<ScriptNotifyApi.Entry2>> items4 = new WeakHashMap<>();
   private static final Map<Script, ScriptNotifyApi.Entry3> items5 = new WeakHashMap<>();
   private static long longValue;
   private static double doubleValue = 20.0;
   private static boolean flag;
   private static boolean flag2;
   private static final Pattern pattern = Pattern.compile("§.");
   private static final Pattern pattern2 = Pattern.compile(
      "(?iu)^\\[?\\s*(?:лс|pm|msg|от|from|whisper)\\s*\\]?\\s*:?\\s*([A-Za-z0-9_]{2,16})\\s*(?:->|→|»|>)\\s*(?:я|me|вам|you)?\\s*[:»]?\\s*(.*)$"
   );
   private static final Pattern pattern3 = Pattern.compile("^((?:\\[[^\\]]{1,24}\\]\\s*)+)([A-Za-z0-9_]{2,16})\\s*[:»>]+\\s*(.*)$");
   private static final Pattern pattern4 = Pattern.compile("^<([A-Za-z0-9_]{2,16})>\\s*(.*)$");
   private static final Pattern pattern5 = Pattern.compile("^(?:\\[[^\\]]{1,24}\\]\\s*)?([^\\s:»<>\\[\\]]{1,24})\\s+([A-Za-z0-9_]{2,16})\\s*[:»>]+\\s*(.*)$");
   private static final Pattern pattern6 = Pattern.compile("^([A-Za-z0-9_]{2,16})\\s*[:»>]+\\s*(.*)$");

   private ScriptNotifyApi() {
   }

   private static MinecraftClient method2186() {
      return MinecraftClient.getInstance();
   }

   private static ClientPlayerEntity method2187() {
      return method2186().player;
   }

   private static Storage host() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStorage();
   }

   public static synchronized void method2188() {
      if (!flag2) {
         flag2 = true;
         EventManager.main().subscribe(PacketReceiveEvent.class, (byte)4, (var0, var1) -> method2191(((PacketReceiveEvent)var1).getPacket()));
         EventManager.main().subscribe(ChatReceiveEvent.class, (byte)4, (var0, var1) -> {
            ChatReceiveEvent var2 = (ChatReceiveEvent)var1;
            if (var2.getText() != null) {
               synchronized (deque) {
                  deque.addLast(new Object[]{System.currentTimeMillis(), var2.getText().getString()});

                  while (deque.size() > 300) {
                     deque.removeFirst();
                  }
               }
            }
         });
         EventManager.main().subscribe(TickEvent.class, (byte)3, (var0, var1) -> tick());
      }
   }

   private static Object[] method2189(Script var0, Function<Scriptable, Object> var1) {
      Scriptable var2 = ScriptScreenApi.method7044(var0);
      if (var2 == null) {
         return new Object[0];
      } else {
         ScriptContextFactory.method7409();

         Object[] var3;
         try {
            var3 = new Object[]{var1.apply(var2)};
         } finally {
            Context.exit();
         }

         return var3;
      }
   }

   private static void method2190(Script.Type var0, Function<Scriptable, Object> var1) {
      Storage var2 = host();
      if (var2 != null) {
         var2.method1332(var0, var1x -> method2189(var1x, var1));
      }
   }

   private static void method2191(Packet<?> var0) {
      if (method2186().isOnThread()) {
         if (var0 instanceof WorldTimeUpdateS2CPacket) {
            long var11 = System.currentTimeMillis();
            if (longValue != 0L) {
               long var12 = var11 - longValue;
               if (var12 > 50L) {
                  double var13 = MathHelper.clamp(20000.0 / var12, 0.0, 20.0);
                  doubleValue = doubleValue * 0.7 + var13 * 0.3;
               }
            }

            longValue = var11;
         } else {
            if (var0 instanceof TitleS2CPacket var1) {
               if (!ScriptModuleApi.method322(Script.Type.TITLE)) {
                  return;
               }

               String var8 = var1.text() == null ? "" : var1.text().getString();
               method2190(
                  Script.Type.TITLE, var1x -> ScriptValueUtils.method6247(var1x).method6277("kind", "title").method6277("text", var8).getScriptableObject()
               );
            } else if (var0 instanceof SubtitleS2CPacket var2) {
               if (!ScriptModuleApi.method322(Script.Type.TITLE)) {
                  return;
               }

               String var14 = var2.text() == null ? "" : var2.text().getString();
               method2190(
                  Script.Type.TITLE, var1x -> ScriptValueUtils.method6247(var1x).method6277("kind", "subtitle").method6277("text", var14).getScriptableObject()
               );
            } else if (var0 instanceof OverlayMessageS2CPacket var3) {
               if (!ScriptModuleApi.method322(Script.Type.ACTIONBAR)) {
                  return;
               }

               String var15 = var3.text() == null ? "" : var3.text().getString();
               method2190(Script.Type.ACTIONBAR, var1x -> ScriptValueUtils.method6247(var1x).method6277("text", var15).getScriptableObject());
            } else if (var0 instanceof BossBarS2CPacket) {
               flag = true;
            } else if (var0 instanceof PlaySoundS2CPacket var4) {
               if (!ScriptModuleApi.method322(Script.Type.SOUND_RECEIVED)) {
                  return;
               }

               String var16 = var4.getSound().value().id().toString();
               method2190(
                  Script.Type.SOUND_RECEIVED,
                  var2x -> ScriptValueUtils.method6247(var2x)
                     .method6277("id", var16)
                     .method6277("category", var4.getCategory().getName())
                     .method6277("x", var4.getX())
                     .method6277("y", var4.getY())
                     .method6277("z", var4.getZ())
                     .method6277("volume", (double)var4.getVolume())
                     .method6277("pitch", (double)var4.getPitch())
                     .getScriptableObject()
               );
            } else if (var0 instanceof ItemPickupAnimationS2CPacket var5) {
               if (!ScriptModuleApi.method322(Script.Type.ITEM_PICKUP)) {
                  return;
               }

               if (method2187() == null || method2186().world == null || var5.getCollectorEntityId() != method2187().getId()) {
                  return;
               }

               ItemStack var9 = method2186().world.getEntityById(var5.getEntityId()) instanceof ItemEntity var10 ? var10.getStack().copy() : ItemStack.EMPTY;
               int var22 = var5.getStackAmount();
               method2190(Script.Type.ITEM_PICKUP, var2x -> {
                  Scriptable var3x = (Scriptable)ScriptEntityApi.method6579(var2x, var9);
                  var3x.put("picked", var3x, (double)var22);
                  return var3x;
               });
            } else if (var0 instanceof BlockUpdateS2CPacket var6) {
               if (method2186().world == null || !ScriptModuleApi.method322(Script.Type.BLOCK_UPDATE)) {
                  return;
               }

               BlockPos var18 = var6.getPos();
               String var20 = Registries.BLOCK.getId(method2186().world.getBlockState(var18).getBlock()).toString();
               String var23 = Registries.BLOCK.getId(var6.getState().getBlock()).toString();
               if (var20.equals(var23)) {
                  return;
               }

               method2190(
                  Script.Type.BLOCK_UPDATE,
                  var3x -> ScriptValueUtils.method6247(var3x)
                     .method6277("x", (double)var18.getX())
                     .method6277("y", (double)var18.getY())
                     .method6277("z", (double)var18.getZ())
                     .method6277("id", var23)
                     .method6277("previous", var20)
                     .getScriptableObject()
               );
            } else if (var0 instanceof OpenScreenS2CPacket var7) {
               if (!ScriptModuleApi.method322(Script.Type.CONTAINER_OPEN)) {
                  return;
               }

               String var19 = Registries.SCREEN_HANDLER.getId(var7.getScreenHandlerType()) == null
                  ? ""
                  : Registries.SCREEN_HANDLER.getId(var7.getScreenHandlerType()).toString();
               String var21 = var7.getName() == null ? "" : var7.getName().getString();
               method2190(
                  Script.Type.CONTAINER_OPEN,
                  var3x -> ScriptValueUtils.method6247(var3x)
                     .method6277("type", var19)
                     .method6277("title", var21)
                     .method6277("syncId", (double)var7.getSyncId())
                     .getScriptableObject()
               );
            }
         }
      }
   }

   private static void tick() {
      if (method2186().world == null) {
         longValue = 0L;
         doubleValue = 20.0;
      }

      if (flag) {
         flag = false;
         method2190(
            Script.Type.BOSSBAR,
            var0x -> {
               ArrayList var1x = new ArrayList();

               for (Object var3 : method2186().inGameHud.getBossBarHud().bossBars.values()) {
                  ClientBossBar var4x = (ClientBossBar)var3;
                  var1x.add(
                     ScriptValueUtils.method6247(var0x)
                        .method6277("name", var4x.getName().getString())
                        .method6277("percent", (double)var4x.getPercent())
                        .method6277("color", var4x.getColor().getName())
                        .getScriptableObject()
                  );
               }

               return ScriptValueUtils.method6247(var0x).method6277("bars", ScriptValueUtils.method6267(var0x, var1x)).getScriptableObject();
            }
         );
      }

      Storage var0 = host();
      if (var0 != null) {
         long var1 = System.currentTimeMillis();

         for (Map.Entry var4 : new ArrayList<>(items.entrySet())) {
            Script var5 = (Script)var4.getKey();
            Deque var6 = (Deque)var4.getValue();
            if (!var5.isRunning()) {
               var6.clear();
            } else {
               long[] var7 = items2.computeIfAbsent(var5, var0x -> new long[]{0L, 1200L});
               if (!var6.isEmpty() && var1 >= var7[0] && method2186().getNetworkHandler() != null) {
                  String var8 = (String)var6.pollFirst();

                  try {
                     ScriptScreenApi.method7062(var5);
                     if (var8.startsWith("/")) {
                        method2186().getNetworkHandler().sendChatCommand(var8.substring(1));
                     } else {
                        method2186().getNetworkHandler().sendChatMessage(var8);
                     }
                  } catch (RuntimeException var10) {
                     var6.addFirst(var8);
                  }

                  var7[0] = var1 + var7[1];
               }
            }
         }

         for (Map.Entry var14 : new ArrayList<>(items3.entrySet())) {
            Script var17 = (Script)var14.getKey();
            ScriptNotifyApi.Entry var20 = (ScriptNotifyApi.Entry)var14.getValue();
            if (!var17.isRunning()) {
               items3.remove(var17);
            } else {
               boolean var22 = method2186().currentScreen instanceof HandledScreen && !(method2186().currentScreen instanceof InventoryScreen);
               if (var22 && ++var20.intValue >= 2) {
                  items3.remove(var17);
                  Scriptable var24 = ScriptScreenApi.method7044(var17);
                  var0.method1321(var17, var20.function, method2210(var24), null);
               } else if (--var20.ticks <= 0) {
                  items3.remove(var17);
                  var0.method1321(var17, var20.function, null, "контейнер не открылся");
               }
            }
         }

         for (Map.Entry var15 : new ArrayList<>(items4.entrySet())) {
            Script var18 = (Script)var15.getKey();
            if (!var18.isRunning()) {
               items4.remove(var18);
            } else {
               for (Object var23_o : (Iterable<?>)(Object)(new ArrayList((Collection)var15.getValue()))) {
                  ScriptNotifyApi.Entry2 var23 = (ScriptNotifyApi.Entry2)var23_o;
                  var23.method2216(var0, var18);
                  if (var23.finished) {
                     ((List)var15.getValue()).remove(var23);
                  }
               }
            }
         }

         for (Map.Entry var16 : new ArrayList<>(items5.entrySet())) {
            Script var19 = (Script)var16.getKey();
            if (!var19.isRunning()) {
               items5.remove(var19);
            } else {
               ((ScriptNotifyApi.Entry3)var16.getValue()).method2217(var0, var19);
            }
         }
      }
   }

   public static void method2192(Script var0, double var1, double var3, double var5) {
      ScriptNotifyApi.Entry3 var7 = items5.computeIfAbsent(var0, var0x -> new ScriptNotifyApi.Entry3());
      var7.items.clear();
      var7.items.add(new double[]{var1, var3, var5});
      var7.index = 0;
      var7.flag = false;
      var7.longValue = System.currentTimeMillis();
   }

   private static boolean method2193() {
      try {
         return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      } catch (Throwable var1) {
         return false;
      }
   }

   private static void method2194(double[] var0) {
      ICustomGoalProcess var1 = BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess();
      if (Double.isNaN(var0[1])) {
         var1.setGoalAndPath(new GoalXZ((int)Math.floor(var0[0]), (int)Math.floor(var0[2])));
      } else {
         var1.setGoalAndPath(new GoalBlock((int)Math.floor(var0[0]), (int)Math.floor(var0[1]), (int)Math.floor(var0[2])));
      }
   }

   public static void method2195(Script var0, Scriptable var1, ScriptableObject var2) {
      method2188();
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var1, var2);
      var3.method6274("follow", var0x -> {
         Entity var1x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var0x, 0));
         if (var1x == null) {
            throw ScriptValueUtils.method6251("pathfind.follow(entity)");
         } else {
            int var2x = var1x.getId();
            BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().follow(var1xx -> var1xx.getId() == var2x);
            return null;
         }
      });
      var3.method6274("waypoints", var1x -> {
         var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 0));
         if (!var2x.isEmpty() && var2x.size() <= 256) {
            ScriptNotifyApi.Entry3 var3x = items5.computeIfAbsent(var0, var0xx -> new ScriptNotifyApi.Entry3());
            var3x.items.clear();

            for (Object var5 : var2x) {
               var3x.items.add(method2196(var5));
            }

            var3x.index = 0;
            Scriptable var6 = ScriptValueUtils.method6262(var1x, 1);
            var3x.flag = var6 != null && ScriptValueUtils.method6265(var6, "loop", false);
            var3x.longValue = System.currentTimeMillis();
            method2194(var3x.items.get(0));
            return (double)var3x.items.size();
         } else {
            throw ScriptValueUtils.method6251("pathfind.waypoints([[x, y, z], ...]) — от 1 до 256 точек");
         }
      });
      var3.method6274("onArrive", var1x -> {
         items5.computeIfAbsent(var0, var0xx -> new ScriptNotifyApi.Entry3()).function = ScriptValueUtils.method6260(var1x, 0);
         return null;
      });
      var3.method6274(
         "route",
         var2x -> {
            ScriptNotifyApi.Entry3 var3x = items5.get(var0);
            return var3x == null
               ? null
               : ScriptValueUtils.method6247(var1)
                  .method6277("index", (double)var3x.index)
                  .method6277("count", (double)var3x.items.size())
                  .method6277("loop", var3x.flag)
                  .getScriptableObject();
         }
      );
   }

   private static double[] method2196(Object var0) {
      var var1 = ScriptValueUtils.method6269(var0);
      if (var1.size() >= 3) {
         return new double[]{Context.toNumber(var1.get(0)), Context.toNumber(var1.get(1)), Context.toNumber(var1.get(2))};
      } else if (var1.size() == 2) {
         return new double[]{Context.toNumber(var1.get(0)), Double.NaN, Context.toNumber(var1.get(1))};
      } else if (var0 instanceof Scriptable var2) {
         Object var3 = ScriptValueUtils.method6263(var2, "y");
         return new double[]{
            ScriptValueUtils.method6264(var2, "x", 0.0),
            ScriptValueUtils.method6253(var3) ? Double.NaN : Context.toNumber(var3),
            ScriptValueUtils.method6264(var2, "z", 0.0)
         };
      } else {
         throw ScriptValueUtils.method6251("точка — [x, y, z], [x, z] или {x, y, z}");
      }
   }

   public static Scriptable method2197(Script var0, Storage var1, Scriptable var2) {
      method2188();
      var0.method1413(() -> {
         items.remove(var0);
         items3.remove(var0);
         items4.remove(var0);
         ScriptNotifyApi.Entry3 var1x = items5.remove(var0);
         if (var1x != null) {
            var1x.items.clear();
         }
      });
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274(
         "history",
         var1x -> {
            int var2x = MathHelper.clamp(ScriptValueUtils.method6257(var1x, 0, 50), 1, 300);
            ArrayList var3x = new ArrayList();
            synchronized (deque) {
               ArrayList<Object[]> var5 = new ArrayList<>(deque);

               for (Object var7_o : (Iterable<?>)(Object)(var5.subList(Math.max(0, var5.size() - var2x), var5.size()))) {
                  Object[] var7 = (Object[])var7_o;
                  var3x.add(
                     ScriptValueUtils.method6247(var2)
                        .method6277("time", (double)((Long)var7[0]).longValue())
                        .method6277("text", var7[1])
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(var2, var3x);
         }
      );
      var3.method6274("parse", var1x -> method2198(var2, ScriptValueUtils.method6254(var1x, 0, "")));
      var3.method6274("send", var1x -> {
         ScriptPermissions.method4(var0, "chat");
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         if (!var2x.isBlank() && var2x.length() <= 256) {
            Scriptable var3x = ScriptValueUtils.method6262(var1x, 1);
            var var4 = items.computeIfAbsent(var0, var0xx -> new ArrayDeque<>());
            if (var4.size() >= 50) {
               throw ScriptValueUtils.method6251("в очереди уже 50 сообщений");
            } else {
               long[] var5 = items2.computeIfAbsent(var0, var0xx -> new long[]{0L, 1200L});
               if (var3x != null) {
                  var5[1] = (long)MathHelper.clamp(ScriptValueUtils.method6264(var3x, "delay", 1200.0), 500.0, 60000.0);
               }

               var4.addLast(var2x);
               return (double)var4.size();
            }
         } else {
            throw ScriptValueUtils.method6251("сообщение от 1 до 256 символов");
         }
      });
      var3.method6274("queue", var1x -> {
         var var2x = items.get(var0);
         return (double)(var2x == null ? 0 : var2x.size());
      });
      var3.method6274("clearQueue", var1x -> {
         var var2x = items.get(var0);
         if (var2x != null) {
            var2x.clear();
         }

         return null;
      });
      return var3.getScriptableObject();
   }

   public static Scriptable method2198(Scriptable var0, String var1) {
      String var2 = pattern.matcher(var1).replaceAll("").strip();
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var0).method6277("raw", var2);
      Matcher var4 = pattern2.matcher(var2);
      if (var4.matches()) {
         return var3.method6277("kind", "private")
            .method6277("rank", "")
            .method6277("name", var4.group(1))
            .method6277("message", var4.group(2))
            .getScriptableObject();
      } else {
         var4 = pattern3.matcher(var2);
         if (var4.matches()) {
            String var5 = var4.group(1).replaceAll("[\\[\\]]", " ").strip().replaceAll("\\s+", " ");
            return var3.method6277("kind", "public")
               .method6277("rank", var5)
               .method6277("name", var4.group(2))
               .method6277("message", var4.group(3))
               .getScriptableObject();
         } else {
            var4 = pattern4.matcher(var2);
            if (var4.matches()) {
               return var3.method6277("kind", "public")
                  .method6277("rank", "")
                  .method6277("name", var4.group(1))
                  .method6277("message", var4.group(2))
                  .getScriptableObject();
            } else {
               var4 = pattern5.matcher(var2);
               if (var4.matches()) {
                  return var3.method6277("kind", "public")
                     .method6277("rank", var4.group(1))
                     .method6277("name", var4.group(2))
                     .method6277("message", var4.group(3))
                     .getScriptableObject();
               } else {
                  var4 = pattern6.matcher(var2);
                  return var4.matches()
                     ? var3.method6277("kind", "public")
                        .method6277("rank", "")
                        .method6277("name", var4.group(1))
                        .method6277("message", var4.group(2))
                        .getScriptableObject()
                     : var3.method6277("kind", "system").method6277("rank", "").method6277("name", "").method6277("message", var2).getScriptableObject();
               }
            }
         }
      }
   }

   public static Scriptable method2199(Scriptable var0) {
      method2188();
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274("tps", var0x -> longValue != 0L && System.currentTimeMillis() - longValue <= 5000L ? Math.round(doubleValue * 10.0) / 10.0 : 0.0);
      var1.method6274("lagging", var0x -> longValue != 0L && System.currentTimeMillis() - longValue > 2500L);
      var1.method6274(
         "players",
         var1x -> {
            ArrayList var2 = new ArrayList();
            if (method2186().getNetworkHandler() != null) {
               for (PlayerListEntry var4 : method2186().getNetworkHandler().getPlayerList()) {
                  if (var2.size() >= 1000) {
                     break;
                  }

                  var2.add(
                     ScriptValueUtils.method6247(var0)
                        .method6277("name", var4.getProfile().name())
                        .method6277("ping", (double)var4.getLatency())
                        .method6277("gamemode", var4.getGameMode() == null ? "" : var4.getGameMode().asString())
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(var0, var2);
         }
      );
      var1.method6274("online", var0x -> method2186().getNetworkHandler() == null ? 0.0 : method2186().getNetworkHandler().getPlayerList().size());
      var1.method6274(
         "brand",
         var0x -> method2186().getNetworkHandler() != null && method2186().getNetworkHandler().getBrand() != null
            ? method2186().getNetworkHandler().getBrand()
            : ""
      );
      var1.method6274("address", var0x -> method2186().getCurrentServerEntry() == null ? "" : method2186().getCurrentServerEntry().address);
      var1.method6274("name", var0x -> method2186().getCurrentServerEntry() == null ? "" : method2186().getCurrentServerEntry().name);
      var1.method6274(
         "motd",
         var0x -> method2186().getCurrentServerEntry() != null && method2186().getCurrentServerEntry().label != null
            ? method2186().getCurrentServerEntry().label.getString()
            : ""
      );
      var1.method6274(
         "version",
         var0x -> method2186().getCurrentServerEntry() != null && method2186().getCurrentServerEntry().version != null
            ? method2186().getCurrentServerEntry().version.getString()
            : ""
      );
      var1.method6274("singleplayer", var0x -> method2186().isInSingleplayer());
      return var1.getScriptableObject();
   }

   public static void method2200(Scriptable var0, ScriptableObject var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6246(var0, var1);
      var2.method6274("chunks", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method2186().world != null && method2187() != null) {
            int var3 = MathHelper.clamp(ScriptValueUtils.method6257(var1x, 0, 8), 1, 32);
            int var4 = method2187().getBlockX() >> 4;
            int var5 = method2187().getBlockZ() >> 4;

            for (int var6 = var4 - var3; var6 <= var4 + var3; var6++) {
               for (int var7 = var5 - var3; var7 <= var5 + var3; var7++) {
                  if (method2186().world.getChunkManager().isChunkLoaded(var6, var7)) {
                     var2x.add(ScriptValueUtils.method6247(var0).method6277("x", (double)var6).method6277("z", (double)var7).getScriptableObject());
                  }
               }
            }

            return ScriptValueUtils.method6267(var0, var2x);
         } else {
            return ScriptValueUtils.method6267(var0, var2x);
         }
      });
      var2.method6274(
         "isLoaded",
         var0x -> method2186().world != null
            && method2186()
               .world
               .getChunkManager()
               .isChunkLoaded(
                  MathHelper.floor(ScriptValueUtils.method6256(var0x, 0, 0.0)) >> 4, MathHelper.floor(ScriptValueUtils.method6256(var0x, 1, 0.0)) >> 4
               )
      );
      var2.method6274(
         "heightAt",
         var0x -> {
            if (method2186().world == null) {
               return -1.0;
            } else {
               int var1x = MathHelper.floor(ScriptValueUtils.method6256(var0x, 0, 0.0));
               int var2x = MathHelper.floor(ScriptValueUtils.method6256(var0x, 1, 0.0));
               return !method2186().world.getChunkManager().isChunkLoaded(var1x >> 4, var2x >> 4)
                  ? -1.0
                  : (double)method2186().world.getTopY(Type.MOTION_BLOCKING, var1x, var2x);
            }
         }
      );
      var2.method6274(
         "entitiesIn",
         var1x -> {
            ArrayList var2x = new ArrayList();
            if (method2186().world == null) {
               return ScriptValueUtils.method6267(var0, var2x);
            } else {
               Box var3 = new Box(
                  ScriptValueUtils.method6256(var1x, 0, 0.0),
                  ScriptValueUtils.method6256(var1x, 1, 0.0),
                  ScriptValueUtils.method6256(var1x, 2, 0.0),
                  ScriptValueUtils.method6256(var1x, 3, 0.0),
                  ScriptValueUtils.method6256(var1x, 4, 0.0),
                  ScriptValueUtils.method6256(var1x, 5, 0.0)
               );
               if (var3.getLengthX() * var3.getLengthY() * var3.getLengthZ() > 1.6777216E7) {
                  throw ScriptValueUtils.method6251("область больше 256³ блоков");
               } else {
                  boolean var4 = ScriptValueUtils.method6259(var1x, 6, false);

                  for (Entity var6 : method2186().world.getOtherEntities(method2187(), var3, var1xx -> !var4 || var1xx instanceof LivingEntity)) {
                     if (var2x.size() >= 512) {
                        break;
                     }

                     var2x.add(ScriptEntityApi.method6576(var0, var6));
                  }

                  return ScriptValueUtils.method6267(var0, var2x);
               }
            }
         }
      );
      var2.method6274("nearest", var1x -> {
         if (method2186().world != null && method2187() != null) {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "living").toLowerCase(Locale.ROOT);
            double var3 = MathHelper.clamp(ScriptValueUtils.method6256(var1x, 1, 64.0), 1.0, 256.0);
            Entity var5 = null;
            double var6 = var3 * var3;

            for (Entity var9 : method2186().world.getEntities()) {
               if (var9 != method2187() && var9.isAlive() && method2201(var9, var2x)) {
                  double var10 = var9.squaredDistanceTo(method2187());
                  if (var10 <= var6) {
                     var6 = var10;
                     var5 = var9;
                  }
               }
            }

            return var5 == null ? null : ScriptEntityApi.method6576(var0, var5);
         } else {
            return null;
         }
      });
   }

   private static boolean method2201(Entity var0, String var1) {
      return switch (var1) {
         case "any", "" -> true;
         case "living" -> var0 instanceof LivingEntity;
         case "player", "players" -> var0 instanceof PlayerEntity;
         case "hostile", "monster" -> var0 instanceof Monster;
         case "animal", "passive" -> var0 instanceof AnimalEntity;
         case "item", "items" -> var0 instanceof ItemEntity;
         default -> ScriptEntityApi.matches(Registries.ENTITY_TYPE.getId(var0.getType()).toString(), var1);
      };
   }

   public static Scriptable method2202(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274("predict", var1x -> {
         Entity var2 = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (var2 == null) {
            return null;
         } else {
            double var3 = MathHelper.clamp(ScriptValueUtils.method6256(var1x, 1, 1.0), 0.0, 40.0);
            Vec3d var5 = var2.getVelocity();
            Vec3d var6 = new Vec3d(var2.getX() - var2.lastX, var2.getY() - var2.lastY, var2.getZ() - var2.lastZ);
            double var7 = Math.abs(var5.x) > Math.abs(var6.x) ? var5.x : var6.x;
            double var9 = Math.abs(var5.z) > Math.abs(var6.z) ? var5.z : var6.z;
            double var11 = var2.isOnGround() ? 0.0 : var5.y;
            double var13 = var2.getY();
            if (!var2.isOnGround()) {
               double var15 = var11;

               for (int var17 = 0; var17 < var3; var17++) {
                  var13 += var15;
                  var15 = (var15 - 0.08) * 0.98;
               }
            }

            return ScriptValueUtils.method6270(var0, var2.getX() + var7 * var3, var13, var2.getZ() + var9 * var3);
         }
      });
      var1.method6274(
         "canHit",
         var0x -> {
            Entity var1x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var0x, 0));
            if (var1x != null && method2187() != null) {
               double var2 = MathHelper.clamp(
                  ScriptValueUtils.method6256(var0x, 1, method2187().getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE)), 0.0, 8.0
               );
               Vec3d var4 = method2187().getEyePos();
               Box var5 = var1x.getBoundingBox();
               double var6 = MathHelper.clamp(var4.x, var5.minX, var5.maxX);
               double var8 = MathHelper.clamp(var4.y, var5.minY, var5.maxY);
               double var10 = MathHelper.clamp(var4.z, var5.minZ, var5.maxZ);
               return var4.squaredDistanceTo(var6, var8, var10) <= var2 * var2 && method2187().canSee(var1x);
            } else {
               return false;
            }
         }
      );
      var1.method6274(
         "armorValue",
         var1x -> ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0)) instanceof LivingEntity var3
            ? ScriptValueUtils.method6247(var0)
               .method6277("armor", (double)var3.getArmor())
               .method6277("toughness", var3.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS))
               .getScriptableObject()
            : null
      );
      var1.method6274("attackDamage", var0x -> method2187() == null ? 0.0 : method2187().getAttributeValue(EntityAttributes.ATTACK_DAMAGE));
      var1.method6274(
         "damageCalc",
         var1x -> {
            if (ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0)) instanceof LivingEntity var3 && method2187() != null) {
               double var4 = ScriptValueUtils.method6252(var1x, 1)
                  ? ScriptValueUtils.method6256(var1x, 1, 1.0)
                  : method2187().getAttributeValue(EntityAttributes.ATTACK_DAMAGE) * (0.2 + 0.8 * Math.pow(method2187().getAttackCooldownProgress(0.5F), 2.0));
               double var6 = var3.getArmor();
               double var8 = var3.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS);
               double var10 = MathHelper.clamp(var6 - var4 / (2.0 + var8 / 4.0), var6 * 0.2, 20.0);
               double var12 = var4 * (1.0 - var10 / 25.0);
               double var14 = var3.getAbsorptionAmount();
               return ScriptValueUtils.method6247(var0)
                  .method6277("raw", var4)
                  .method6277("afterArmor", var12)
                  .method6277("kills", var12 >= var3.getHealth() + var14)
                  .method6277("hits", Math.ceil((var3.getHealth() + var14) / Math.max(0.01, var12)))
                  .getScriptableObject();
            } else {
               return null;
            }
         }
      );
      return var1.getScriptableObject();
   }

   public static void method2203(Script var0, Scriptable var1, ScriptableObject var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var1, var2);
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var1);
      var4.method6274("boost", var0x -> {
         if (method2187() != null && method2186().interactionManager != null) {
            if (method2204(method2187().getOffHandStack())) {
               method2186().interactionManager.interactItem(method2187(), Hand.OFF_HAND);
               return true;
            } else {
               int var1x = method2187().getInventory().getSelectedSlot();

               for (int var2x = 0; var2x < 9; var2x++) {
                  if (method2204(method2187().getInventory().getStack(var2x))) {
                     method2187().getInventory().setSelectedSlot(var2x);
                     method2186().interactionManager.interactItem(method2187(), Hand.MAIN_HAND);
                     method2187().getInventory().setSelectedSlot(var1x);
                     return true;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      });
      var4.method6274("equipped", var0x -> method2187() != null && method2187().getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA));
      var4.method6274("durability", var0x -> {
         if (method2187() == null) {
            return 0.0;
         } else {
            ItemStack var1x = method2187().getEquippedStack(EquipmentSlot.CHEST);
            return var1x.isOf(Items.ELYTRA) && var1x.getMaxDamage() > 0 ? (double)(var1x.getMaxDamage() - var1x.getDamage()) : 0.0;
         }
      });
      var4.method6274("gliding", var0x -> method2187() != null && method2187().isGliding());
      var3.method6277("elytra", var4.getScriptableObject());
      var3.method6274("nearbyBlocks", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method2186().world != null && method2187() != null) {
            int var3x = MathHelper.clamp(ScriptValueUtils.method6257(var1x, 0, 4), 1, 8);
            ArrayList<String> var4x = new ArrayList<>();
            Object var5 = ScriptValueUtils.arg(var1x, 1);
            if (var5 instanceof NativeArray) {
               for (Object var7 : ScriptValueUtils.method6269(var5)) {
                  var4x.add(ScriptValueUtils.method6255(var7));
               }
            } else if (!ScriptValueUtils.method6253(var5)) {
               var4x.add(ScriptValueUtils.method6255(var5));
            }

            for (BlockPos var11 : BlockPos.iterateOutwards(method2187().getBlockPos(), var3x, var3x, var3x)) {
               BlockState var8 = method2186().world.getBlockState(var11);
               if (!var8.isAir()) {
                  String var9 = Registries.BLOCK.getId(var8.getBlock()).toString();
                  if (var4x.isEmpty() || !var4x.stream().noneMatch(var1xx -> ScriptEntityApi.matches(var9, var1xx))) {
                     var2x.add(ScriptEntityApi.method6580(var1, var11.toImmutable()));
                     if (var2x.size() >= 2000) {
                        break;
                     }
                  }
               }
            }

            return ScriptValueUtils.method6267(var1, var2x);
         } else {
            return ScriptValueUtils.method6267(var1, var2x);
         }
      });
   }

   private static boolean method2204(ItemStack var0) {
      return !var0.isEmpty() && var0.isOf(Items.FIREWORK_ROCKET);
   }

   private static int method2205(String var0) {
      if (var0.contains("netherite")) {
         return 7;
      } else if (var0.contains("diamond")) {
         return 6;
      } else if (var0.contains("iron")) {
         return 5;
      } else if (var0.contains("chainmail") || var0.contains("copper")) {
         return 4;
      } else if (var0.contains("stone")) {
         return 3;
      } else if (var0.contains("golden")) {
         return 2;
      } else {
         return !var0.contains("wooden") && !var0.contains("leather") ? 0 : 1;
      }
   }

   private static int method2206(ItemStack var0) {
      int var1 = 0;

      for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var3 : var0.getEnchantments().getEnchantmentEntries()) {
         var1 += var3.getIntValue();
      }

      return var1;
   }

   public static void method2207(Script var0, Scriptable var1, ScriptableObject var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var1, var2);
      var3.method6274("moveTo", var0x -> {
         if (method2187() != null && method2186().interactionManager != null) {
            int var1x = method2187().currentScreenHandler.syncId;
            int var2x = ScriptScreenApi.method7053(ScriptValueUtils.method6257(var0x, 0, -1));
            int var3x = ScriptScreenApi.method7053(ScriptValueUtils.method6257(var0x, 1, -1));
            if (var2x >= 0 && var3x >= 0 && var2x != var3x) {
               method2186().interactionManager.clickSlot(var1x, var2x, 0, SlotActionType.PICKUP, method2187());
               method2186().interactionManager.clickSlot(var1x, var3x, 0, SlotActionType.PICKUP, method2187());
               if (!method2187().currentScreenHandler.getCursorStack().isEmpty()) {
                  method2186().interactionManager.clickSlot(var1x, var2x, 0, SlotActionType.PICKUP, method2187());
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      var3.method6274(
         "quickMove",
         var0x -> {
            if (method2187() != null && method2186().interactionManager != null) {
               method2186()
                  .interactionManager
                  .clickSlot(
                     method2187().currentScreenHandler.syncId,
                     ScriptScreenApi.method7053(ScriptValueUtils.method6257(var0x, 0, -1)),
                     0,
                     SlotActionType.QUICK_MOVE,
                     method2187()
                  );
               return true;
            } else {
               return false;
            }
         }
      );
      var3.method6274("sort", var0x -> {
         if (method2187() != null && method2186().interactionManager != null) {
            if (method2187().currentScreenHandler != method2187().playerScreenHandler) {
               throw ScriptValueUtils.method6251("закрой контейнер перед сортировкой");
            } else {
               int var1x = 0;

               for (int var2x = 9; var2x < 36 && var1x < 40; var2x++) {
                  int var3x = var2x;

                  for (int var4 = var2x + 1; var4 < 36; var4++) {
                     if (method2208(method2187().getInventory().getStack(var4), method2187().getInventory().getStack(var3x)) < 0) {
                        var3x = var4;
                     }
                  }

                  if (var3x != var2x) {
                     int var5 = method2187().currentScreenHandler.syncId;
                     method2186().interactionManager.clickSlot(var5, ScriptScreenApi.method7053(var3x), 0, SlotActionType.PICKUP, method2187());
                     method2186().interactionManager.clickSlot(var5, ScriptScreenApi.method7053(var2x), 0, SlotActionType.PICKUP, method2187());
                     method2186().interactionManager.clickSlot(var5, ScriptScreenApi.method7053(var3x), 0, SlotActionType.PICKUP, method2187());
                     var1x++;
                  }
               }

               return (double)var1x;
            }
         } else {
            return 0.0;
         }
      });
      var3.method6274(
         "equipBest",
         var0x -> {
            if (method2187() != null && method2186().interactionManager != null) {
               String var1x = ScriptValueUtils.method6254(var0x, 0, "sword").toLowerCase(Locale.ROOT);
               if (var1x.equals("armor")) {
                  return method2209();
               } else {
                  int var2x = -1;
                  int var3x = -1;

                  for (int var4 = 0; var4 < 36; var4++) {
                     ItemStack var5 = method2187().getInventory().getStack(var4);
                     if (!var5.isEmpty()) {
                        String var6 = Registries.ITEM.getId(var5.getItem()).getPath();
                        if (var6.endsWith("_" + var1x) || var6.equals(var1x)) {
                           int var7 = method2205(var6) * 100 + method2206(var5);
                           if (var7 > var3x) {
                              var3x = var7;
                              var2x = var4;
                           }
                        }
                     }
                  }

                  if (var2x < 0) {
                     return false;
                  } else {
                     if (var2x < 9) {
                        method2187().getInventory().setSelectedSlot(var2x);
                     } else {
                        int var8 = method2187().getInventory().getSelectedSlot();
                        method2186()
                           .interactionManager
                           .clickSlot(method2187().currentScreenHandler.syncId, ScriptScreenApi.method7053(var2x), var8, SlotActionType.SWAP, method2187());
                     }

                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      );
   }

   private static int method2208(ItemStack var0, ItemStack var1) {
      if (var0.isEmpty() != var1.isEmpty()) {
         return var0.isEmpty() ? 1 : -1;
      } else if (var0.isEmpty()) {
         return 0;
      } else {
         int var2 = Registries.ITEM.getId(var0.getItem()).toString().compareTo(Registries.ITEM.getId(var1.getItem()).toString());
         return var2 != 0 ? var2 : Integer.compare(var1.getCount(), var0.getCount());
      }
   }

   private static boolean method2209() {
      if (method2187().currentScreenHandler != method2187().playerScreenHandler) {
         throw ScriptValueUtils.method6251("закрой контейнер, чтобы надеть броню");
      } else {
         String[] var0 = new String[]{"helmet", "chestplate", "leggings", "boots"};
         EquipmentSlot[] var1 = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
         boolean var2 = false;
         int var3 = method2187().currentScreenHandler.syncId;

         for (int var4 = 0; var4 < 4; var4++) {
            ItemStack var5 = method2187().getEquippedStack(var1[var4]);
            int var6 = var5.isEmpty() ? -1 : method2205(Registries.ITEM.getId(var5.getItem()).getPath()) * 100 + method2206(var5);
            int var7 = -1;
            int var8 = var6;

            for (int var9 = 0; var9 < 36; var9++) {
               ItemStack var10 = method2187().getInventory().getStack(var9);
               if (!var10.isEmpty()) {
                  String var11 = Registries.ITEM.getId(var10.getItem()).getPath();
                  if (var11.endsWith("_" + var0[var4])) {
                     int var12 = method2205(var11) * 100 + method2206(var10);
                     if (var12 > var8) {
                        var8 = var12;
                        var7 = var9;
                     }
                  }
               }
            }

            if (var7 >= 0) {
               int var13 = 5 + var4;
               method2186().interactionManager.clickSlot(var3, ScriptScreenApi.method7053(var7), 0, SlotActionType.PICKUP, method2187());
               method2186().interactionManager.clickSlot(var3, var13, 0, SlotActionType.PICKUP, method2187());
               if (!method2187().currentScreenHandler.getCursorStack().isEmpty()) {
                  method2186().interactionManager.clickSlot(var3, ScriptScreenApi.method7053(var7), 0, SlotActionType.PICKUP, method2187());
               }

               var2 = true;
            }
         }

         return var2;
      }
   }

   private static Scriptable method2210(Scriptable var0) {
      ArrayList var1 = new ArrayList();
      if (method2187() != null && var0 != null) {
         ScreenHandler var2 = method2187().currentScreenHandler;

         for (int var3 = 0; var3 < var2.slots.size() && var1.size() < 128; var3++) {
            Slot var4 = var2.slots.get(var3);
            if (var4.inventory != method2187().getInventory()) {
               Scriptable var5 = (Scriptable)ScriptEntityApi.method6579(var0, var4.getStack());
               var5.put("slot", var5, (double)var3);
               var1.add(var5);
            }
         }
      }

      return ScriptValueUtils.method6267(var0, var1);
   }

   public static Scriptable method2211(Script var0, Storage var1, Scriptable var2) {
      method2188();
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274(
         "open",
         var1x -> {
            org.mozilla.javascript.Function var2x = ScriptValueUtils.method6260(var1x, 3);
            if (method2187() == null) {
               throw ScriptValueUtils.method6251("нет игрока");
            } else {
               Object var3x = ScriptWorldApi.method4823(
                  ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
               );
               ScriptNotifyApi.Entry var4 = new ScriptNotifyApi.Entry();
               var4.function = var2x;
               var4.ticks = MathHelper.clamp(ScriptValueUtils.method6257(var1x, 4, 40), 5, 200);
               items3.put(var0, var4);
               return var3x;
            }
         }
      );
      var3.method6274("isOpen", var0x -> method2186().currentScreen instanceof HandledScreen && !(method2186().currentScreen instanceof InventoryScreen));
      var3.method6274("title", var0x -> method2186().currentScreen == null ? "" : method2186().currentScreen.getTitle().getString());
      var3.method6274("items", var1x -> method2210(var2));
      var3.method6274("take", var0x -> {
         if (method2187() != null && method2186().interactionManager != null) {
            int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
            if (var1x >= 0 && var1x < method2187().currentScreenHandler.slots.size()) {
               method2186().interactionManager.clickSlot(method2187().currentScreenHandler.syncId, var1x, 0, SlotActionType.QUICK_MOVE, method2187());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      var3.method6274(
         "takeAll",
         var0x -> {
            if (method2187() != null && method2186().interactionManager != null) {
               String var1x = ScriptValueUtils.method6254(var0x, 0, "");
               ScreenHandler var2x = method2187().currentScreenHandler;
               int var3x = 0;

               for (int var4 = 0; var4 < var2x.slots.size() && var3x < 64; var4++) {
                  Slot var5 = var2x.slots.get(var4);
                  if (var5.inventory != method2187().getInventory()
                     && !var5.getStack().isEmpty()
                     && (var1x.isEmpty() || ScriptEntityApi.matches(Registries.ITEM.getId(var5.getStack().getItem()).toString(), var1x))) {
                     method2186().interactionManager.clickSlot(var2x.syncId, var4, 0, SlotActionType.QUICK_MOVE, method2187());
                     var3x++;
                  }
               }

               return (double)var3x;
            } else {
               return 0.0;
            }
         }
      );
      var3.method6274("put", var0x -> {
         if (method2187() != null && method2186().interactionManager != null) {
            int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
            ScreenHandler var2x = method2187().currentScreenHandler;

            for (int var3x = 0; var3x < var2x.slots.size(); var3x++) {
               Slot var4 = var2x.slots.get(var3x);
               if (var4.inventory == method2187().getInventory() && var4.getIndex() == var1x) {
                  method2186().interactionManager.clickSlot(var2x.syncId, var3x, 0, SlotActionType.QUICK_MOVE, method2187());
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      });
      var3.method6274("close", var0x -> {
         if (method2187() != null) {
            method2187().closeHandledScreen();
         }

         return null;
      });
      var3.method6274(
         "trades",
         var1x -> {
            ArrayList var2x = new ArrayList();
            if (method2187() != null && method2187().currentScreenHandler instanceof MerchantScreenHandler var3x) {
               int var7 = 0;

               for (TradeOffer var6 : var3x.getRecipes()) {
                  var2x.add(
                     ScriptValueUtils.method6247(var2)
                        .method6277("index", (double)(var7++))
                        .method6277("buy", ScriptEntityApi.method6579(var2, var6.getDisplayedFirstBuyItem()))
                        .method6277("buy2", ScriptEntityApi.method6579(var2, var6.getDisplayedSecondBuyItem()))
                        .method6277("sell", ScriptEntityApi.method6579(var2, var6.getSellItem()))
                        .method6277("uses", (double)var6.getUses())
                        .method6277("maxUses", (double)var6.getMaxUses())
                        .method6277("disabled", var6.isDisabled())
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(var2, var2x);
         }
      );
      var3.method6274("selectTrade", var0x -> {
         if (method2187() != null && method2187().currentScreenHandler instanceof MerchantScreenHandler var1x && method2186().getNetworkHandler() != null) {
            int var3x = ScriptValueUtils.method6257(var0x, 0, 0);
            if (var3x >= 0 && var3x < var1x.getRecipes().size()) {
               var1x.setRecipeIndex(var3x);
               var1x.switchTo(var3x);
               method2186().getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(var3x));
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      var3.method6274("buyTrade", var0x -> {
         if (method2187() != null && method2187().currentScreenHandler instanceof MerchantScreenHandler var1x && method2186().getNetworkHandler() != null) {
            int var3x = ScriptValueUtils.method6257(var0x, 0, 0);
            if (var3x >= 0 && var3x < var1x.getRecipes().size()) {
               var1x.setRecipeIndex(var3x);
               var1x.switchTo(var3x);
               method2186().getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(var3x));
               method2186().interactionManager.clickSlot(var1x.syncId, 2, 0, SlotActionType.QUICK_MOVE, method2187());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      return var3.getScriptableObject();
   }

   private static int method2212(String var0) {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = method2187().getInventory().getStack(var1);
         if (!var2.isEmpty() && Registries.ITEM.getId(var2.getItem()).toString().equals(var0)) {
            return var1;
         }
      }

      return -1;
   }

   public static Scriptable method2213(Script var0, Storage var1, Scriptable var2) {
      method2188();
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274("load", var2x -> {
         if (var0.getPath() == null) {
            throw ScriptValueUtils.method6251("схематики доступны только скриптам-папкам");
         } else {
            Path var3x = var0.getPath().toAbsolutePath().normalize();
            Path var4 = var3x.resolve(ScriptValueUtils.method6254(var2x, 0, "")).toAbsolutePath().normalize();
            if (var4.startsWith(var3x) && Files.isRegularFile(var4)) {
               try {
                  if (Files.size(var4) > 4194304L) {
                     throw ScriptValueUtils.method6251("схематика больше 4 МБ");
                  } else {
                     NbtCompound var5 = NbtIo.readCompressed(var4, NbtSizeTracker.of(67108864L));
                     return method2215(var2, var5);
                  }
               } catch (IOException var6) {
                  throw ScriptValueUtils.method6251("не удалось прочитать .nbt: " + var6.getMessage());
               }
            } else {
               throw ScriptValueUtils.method6251("нет файла " + ScriptValueUtils.method6254(var2x, 0, ""));
            }
         }
      });
      var3.method6274(
         "fromList",
         var1x -> {
            var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 0));
            ArrayList var3x = new ArrayList();

            for (Object var5 : var2x) {
               if (var5 instanceof Scriptable var6) {
                  var3x.add(
                     ScriptValueUtils.method6247(var2)
                        .method6277("x", (double)((int)ScriptValueUtils.method6264(var6, "x", 0.0)))
                        .method6277("y", (double)((int)ScriptValueUtils.method6264(var6, "y", 0.0)))
                        .method6277("z", (double)((int)ScriptValueUtils.method6264(var6, "z", 0.0)))
                        .method6277("id", method2214(ScriptValueUtils.method6266(var6, "id", "stone")))
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6247(var2)
               .method6277("blocks", ScriptValueUtils.method6267(var2, var3x))
               .method6277("count", (double)var3x.size())
               .getScriptableObject();
         }
      );
      var3.method6274(
         "build",
         var2x -> {
            Scriptable var3x = ScriptValueUtils.method6262(var2x, 0);
            if (var3x == null) {
               throw ScriptValueUtils.method6251("schematic.build(схема, x, y, z[, опции])");
            } else {
               ArrayList var4 = new ArrayList();

               for (Object var6 : ScriptValueUtils.method6269(ScriptValueUtils.method6263(var3x, "blocks"))) {
                  if (var6 instanceof Scriptable var7) {
                     String var8 = method2214(ScriptValueUtils.method6266(var7, "id", ""));
                     if (!var8.isEmpty() && !var8.endsWith(":air")) {
                        var4.add(
                           new ScriptNotifyApi.Data(
                              (int)ScriptValueUtils.method6264(var7, "x", 0.0),
                              (int)ScriptValueUtils.method6264(var7, "y", 0.0),
                              (int)ScriptValueUtils.method6264(var7, "z", 0.0),
                              var8
                           )
                        );
                     }
                  }
               }

               if (var4.isEmpty()) {
                  throw ScriptValueUtils.method6251("в схеме нет блоков");
               } else {
                  var4.sort(
                     Comparator.comparingInt(ScriptNotifyApi.Data::getIntValue2)
                        .thenComparingInt(ScriptNotifyApi.Data::getIntValue)
                        .thenComparingInt(ScriptNotifyApi.Data::getIntValue3)
                  );
                  ScriptNotifyApi.Entry2 var10 = new ScriptNotifyApi.Entry2(
                     var4,
                     MathHelper.floor(ScriptValueUtils.method6256(var2x, 1, 0.0)),
                     MathHelper.floor(ScriptValueUtils.method6256(var2x, 2, 0.0)),
                     MathHelper.floor(ScriptValueUtils.method6256(var2x, 3, 0.0))
                  );
                  Scriptable var11 = ScriptValueUtils.method6262(var2x, 4);
                  var10.intValue4 = var11 == null ? 1 : (int)MathHelper.clamp(ScriptValueUtils.method6264(var11, "perTick", 1.0), 1.0, 4.0);
                  var10.intValue5 = var11 == null ? 1 : (int)MathHelper.clamp(ScriptValueUtils.method6264(var11, "delay", 1.0), 0.0, 100.0);
                  if (var11 != null) {
                     Object var12 = ScriptValueUtils.method6263(var11, "onDone");
                     Object var14 = ScriptValueUtils.method6263(var11, "onProgress");
                     var10.function = var12 instanceof org.mozilla.javascript.Function var9 ? var9 : null;
                     var10.function2 = var14 instanceof org.mozilla.javascript.Function var15 ? var15 : null;
                  }

                  var var13 = items4.computeIfAbsent(var0, var0xx -> new ArrayList<>());
                  if (var13.size() >= 4) {
                     throw ScriptValueUtils.method6251("у скрипта уже 4 стройки");
                  } else {
                     var13.add(var10);
                     return ScriptValueUtils.method6247(var2)
                        .method6274("pause", var1xx -> {
                           var10.flag = true;
                           return null;
                        })
                        .method6274("resume", var1xx -> {
                           var10.flag = false;
                           return null;
                        })
                        .method6274("stop", var1xx -> {
                           var10.finished = true;
                           return null;
                        })
                        .method6274("done", var1xx -> var10.finished)
                        .method6274(
                           "progress",
                           var3xx -> ScriptValueUtils.method6247(var2)
                              .method6277("placed", (double)var10.index)
                              .method6277("total", (double)var4.size())
                              .method6277("stuck", var10.text)
                              .method6277("missing", ScriptValueUtils.method6267(var2, new ArrayList<>(var10.items2)))
                              .getScriptableObject()
                        )
                        .getScriptableObject();
                  }
               }
            }
         }
      );
      var3.method6274("stopAll", var1x -> {
         var var2x = items4.get(var0);
         if (var2x != null) {
            var2x.forEach(var0xx -> var0xx.finished = true);
         }

         return null;
      });
      return var3.getScriptableObject();
   }

   private static String method2214(String var0) {
      String var1 = var0.strip().toLowerCase(Locale.ROOT);
      int var2 = var1.indexOf(91);
      if (var2 > 0) {
         var1 = var1.substring(0, var2);
      }

      return var1.isEmpty() ? "" : (var1.contains(":") ? var1 : "minecraft:" + var1);
   }

   private static Scriptable method2215(Scriptable var0, NbtCompound var1) {
      NbtList var2 = var1.getListOrEmpty("size");
      NbtList var3 = var1.getListOrEmpty("palette");
      NbtList var4 = var1.getListOrEmpty("blocks");
      if (var3.isEmpty() && var1.contains("palettes")) {
         NbtList var5 = var1.getListOrEmpty("palettes");
         if (!var5.isEmpty() && var5.get(0) instanceof NbtList var6) {
            var3 = var6;
         }
      }

      ArrayList var13 = new ArrayList();

      for (NbtElement var16 : var3) {
         var13.add(var16 instanceof NbtCompound var8 ? method2214(var8.getString("Name", "minecraft:air")) : "minecraft:air");
      }

      ArrayList var15 = new ArrayList();

      for (NbtElement var19 : var4) {
         if (var19 instanceof NbtCompound var9) {
            int var10 = var9.getInt("state", 0);
            if (var10 >= 0 && var10 < var13.size()) {
               String var11 = (String)var13.get(var10);
               if (!var11.endsWith(":air") && !var11.endsWith(":structure_void")) {
                  NbtList var12 = var9.getListOrEmpty("pos");
                  if (var12.size() >= 3) {
                     var15.add(
                        ScriptValueUtils.method6247(var0)
                           .method6277("x", (double)var12.getInt(0, 0))
                           .method6277("y", (double)var12.getInt(1, 0))
                           .method6277("z", (double)var12.getInt(2, 0))
                           .method6277("id", var11)
                           .getScriptableObject()
                     );
                     if (var15.size() >= 20000) {
                        break;
                     }
                  }
               }
            }
         }
      }

      ScriptValueUtils.Entry2 var18 = ScriptValueUtils.method6247(var0)
         .method6277("x", (double)(var2.size() > 0 ? var2.getInt(0, 0) : 0))
         .method6277("y", (double)(var2.size() > 1 ? var2.getInt(1, 0) : 0))
         .method6277("z", (double)(var2.size() > 2 ? var2.getInt(2, 0) : 0));
      return ScriptValueUtils.method6247(var0)
         .method6277("size", var18.getScriptableObject())
         .method6277("blocks", ScriptValueUtils.method6267(var0, var15))
         .method6277("count", (double)var15.size())
         .getScriptableObject();
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, text);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "text=" + this.text + "]";
      }

      private Data(int var1, int var2, int var3, String var4) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.text = var4;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public String getText() {
         return this.text;
      }
   }

   private static final class Entry {
      public org.mozilla.javascript.Function function;
      public int ticks;
      public int intValue;
   }

   private static final class Entry2 {
      public final List<ScriptNotifyApi.Data> items;
      public final int intValue;
      public final int intValue2;
      public final int intValue3;
      public int index;
      public int intValue4;
      public int intValue5;
      public int intValue6;
      public boolean flag;
      public boolean finished;
      public String text = "";
      public final List<String> items2 = new ArrayList<>();
      public org.mozilla.javascript.Function function;
      public org.mozilla.javascript.Function function2;

      public Entry2(List<ScriptNotifyApi.Data> var1, int var2, int var3, int var4) {
         this.items = var1;
         this.intValue = var2;
         this.intValue2 = var3;
         this.intValue3 = var4;
      }

      public void method2216(Storage var1, Script var2) {
         if (!this.finished && !this.flag && ScriptNotifyApi.method2187() != null && ScriptNotifyApi.method2186().world != null) {
            if (this.intValue6 > 0) {
               this.intValue6--;
            } else {
               int var3 = 0;

               while (var3 < this.intValue4 && this.index < this.items.size()) {
                  ScriptNotifyApi.Data var4 = this.items.get(this.index);
                  BlockPos var5 = new BlockPos(this.intValue + var4.intValue, this.intValue2 + var4.intValue2, this.intValue3 + var4.intValue3);
                  String var6 = Registries.BLOCK.getId(ScriptNotifyApi.method2186().world.getBlockState(var5).getBlock()).toString();
                  if (var6.equals(var4.text)) {
                     this.index++;
                  } else {
                     if (ScriptNotifyApi.method2187().getEyePos().squaredDistanceTo(Vec3d.ofCenter(var5)) > 20.25) {
                        this.text = "далеко: подойди к " + var5.getX() + " " + var5.getY() + " " + var5.getZ();
                        return;
                     }

                     int var7 = ScriptNotifyApi.method2212(var4.text);
                     if (var7 < 0) {
                        if (!this.items2.contains(var4.text)) {
                           this.items2.add(var4.text);
                        }

                        this.text = "нет блока " + var4.text;
                        this.index++;
                     } else {
                        if (var7 < 9) {
                           ScriptNotifyApi.method2187().getInventory().setSelectedSlot(var7);
                        } else {
                           ScriptNotifyApi.method2186()
                              .interactionManager
                              .clickSlot(
                                 ScriptNotifyApi.method2187().currentScreenHandler.syncId,
                                 ScriptScreenApi.method7053(var7),
                                 ScriptNotifyApi.method2187().getInventory().getSelectedSlot(),
                                 SlotActionType.SWAP,
                                 ScriptNotifyApi.method2187()
                              );
                        }

                        boolean var8 = ScriptWorldApi.method4822(var5);
                        this.text = var8 ? "" : "не встаёт " + var4.text + " в " + var5.getX() + " " + var5.getY() + " " + var5.getZ() + " — нужна опора рядом";
                        this.index++;
                        var3++;
                     }
                  }
               }

               this.intValue6 = this.intValue5;
               if (this.function2 != null && var3 > 0) {
                  var1.method1321(var2, this.function2, (double)this.index, (double)this.items.size());
               }

               if (this.index >= this.items.size()) {
                  this.finished = true;
                  if (this.function != null) {
                     Scriptable var9 = ScriptScreenApi.method7044(var2);
                     if (var9 != null) {
                        var1.method1321(var2, this.function, ScriptValueUtils.method6267(var9, new ArrayList<>(this.items2)));
                     }
                  }
               }
            }
         }
      }
   }

   private static final class Entry3 {
      public final List<double[]> items = new ArrayList<>();
      public int index;
      public boolean flag;
      public long longValue;
      public org.mozilla.javascript.Function function;

      public void method2217(Storage var1, Script var2) {
         if (!this.items.isEmpty() && ScriptNotifyApi.method2187() != null && this.index < this.items.size()) {
            if (System.currentTimeMillis() - this.longValue >= 600L) {
               double[] var3 = this.items.get(this.index);
               double var4 = Math.sqrt(
                  ScriptNotifyApi.method2187()
                     .squaredDistanceTo(var3[0] + 0.5, Double.isNaN(var3[1]) ? ScriptNotifyApi.method2187().getY() : var3[1], var3[2] + 0.5)
               );
               boolean var6 = ScriptNotifyApi.method2193();
               if (!(var4 > 2.5) || !var6) {
                  if (var4 > 2.5 && !var6) {
                     this.items.clear();
                     if (this.function != null) {
                        var1.method1321(var2, this.function, (double)this.index, false);
                     }
                  } else {
                     int var7 = this.index++;
                     if (this.index >= this.items.size() && this.flag) {
                        this.index = 0;
                     }

                     if (this.function != null) {
                        var1.method1321(var2, this.function, (double)var7, true);
                     }

                     if (this.index < this.items.size()) {
                        ScriptNotifyApi.method2194(this.items.get(this.index));
                        this.longValue = System.currentTimeMillis();
                     } else {
                        this.items.clear();
                     }
                  }
               }
            }
         }
      }
   }
}

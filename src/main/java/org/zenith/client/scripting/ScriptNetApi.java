package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.client.events.Event10;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.misc.Friends;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class ScriptNetApi {
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(Redirect.NEVER).build();
   private static final long CONST_longValue = 2097152L;
   private static final int CONST_intValue = 30;
   private static final int CONST_intValue2 = 120;
   private static final int CONST_intValue3 = 2048;
   private static final Map<Script, Deque<Long>> items = new ConcurrentHashMap<>();
   private static final Map<Script, Deque<Long>> items2 = new ConcurrentHashMap<>();

   private ScriptNetApi() {
   }

   private static boolean method3274(Map<Script, Deque<Long>> var0, Script var1, int var2) {
      Deque<Long> var3 = var0.computeIfAbsent(var1, var0x -> new ArrayDeque<Long>());
      long var4 = System.currentTimeMillis();
      synchronized (var3) {
         while (!var3.isEmpty() && var4 - var3.peekFirst() > 60000L) {
            var3.pollFirst();
         }

         if (var3.size() >= var2) {
            return false;
         } else {
            var3.addLast(var4);
            return true;
         }
      }
   }

   public static URI method3275(Script var0, String var1) {
      URI var2;
      try {
         var2 = URI.create(var1.strip());
      } catch (IllegalArgumentException var8) {
         throw ScriptValueUtils.method6251("неверный адрес: " + var1);
      }

      String var3 = var2.getScheme() == null ? "" : var2.getScheme().toLowerCase(Locale.ROOT);
      if (!var3.equals("https") && !var3.equals("http")) {
         throw ScriptValueUtils.method6251("разрешены только http и https");
      } else {
         String var4 = var2.getHost() == null ? "" : var2.getHost().toLowerCase(Locale.ROOT);
         if (var4.isEmpty()) {
            throw ScriptValueUtils.method6251("в адресе нет хоста");
         } else {
            boolean var5 = false;

            for (String var7 : var0.method1407()) {
               if (var4.equals(var7) || var4.endsWith("." + var7)) {
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               if (!var0.method1408().isEmpty()) {
                  throw ScriptValueUtils.method6251(
                     "в // @net есть недопустимые записи " + var0.method1408() + " — укажи полный домен сайта, например api.example.com, а не зону вроде com"
                  );
               } else {
                  throw ScriptValueUtils.method6251("домен " + var4 + " не объявлен: добавь в шапку скрипта строку // @net " + var4);
               }
            } else if (var2.getUserInfo() != null) {
               throw ScriptValueUtils.method6251("логин и пароль в адресе запрещены");
            } else {
               return var2;
            }
         }
      }
   }

   public static boolean method3276(InetAddress var0) {
      if (!var0.isLoopbackAddress() && !var0.isSiteLocalAddress() && !var0.isLinkLocalAddress() && !var0.isAnyLocalAddress() && !var0.isMulticastAddress()) {
         byte[] var1 = var0.getAddress();
         if (var1.length == 16) {
            boolean var2 = true;

            for (int var3 = 0; var3 < 10; var3++) {
               if (var1[var3] != 0) {
                  var2 = false;
               }
            }

            if (var2 && (var1[10] & 255) == 255 && (var1[11] & 255) == 255) {
               var1 = new byte[]{var1[12], var1[13], var1[14], var1[15]};
            } else {
               int var5 = var1[0] & 255;
               if ((var5 & 254) == 252) {
                  return true;
               }

               if (var5 == 32 && (var1[1] & 255) == 1 && (var1[2] & 255) == 13 && (var1[3] & 255) == 184) {
                  return true;
               }

               if (var5 != 0 || (var1[1] & 255) != 100 || (var1[2] & 255) != 255 || (var1[3] & 255) != 155) {
                  return false;
               }

               var1 = new byte[]{var1[12], var1[13], var1[14], var1[15]};
            }
         }

         int var4 = var1[0] & 255;
         int var6 = var1[1] & 255;
         return var4 == 0
            || var4 == 10
            || var4 == 127
            || var4 >= 224
            || var4 == 100 && (var6 & 192) == 64
            || var4 == 169 && var6 == 254
            || var4 == 172 && (var6 & 240) == 16
            || var4 == 192 && var6 == 168
            || var4 == 192 && var6 == 0 && (var1[2] & 255) == 0
            || var4 == 198 && (var6 & 254) == 18;
      } else {
         return true;
      }
   }

   private static void method3277(URI var0) throws IOException {
      String var1 = var0.getHost().toLowerCase(Locale.ROOT);
      if (!var1.equals("localhost") && !var1.endsWith(".localhost") && !var1.endsWith(".local") && !var1.endsWith(".internal")) {
         InetAddress[] var2 = InetAddress.getAllByName(var1);
         if (var2.length == 0) {
            throw new IOException("адрес не найден");
         } else {
            for (InetAddress var6 : var2) {
               if (method3276(var6)) {
                  throw new IOException("запросы в локальную сеть запрещены");
               }
            }
         }
      } else {
         throw new IOException("запросы в локальную сеть запрещены");
      }
   }

   public static void method3278(
      Script var0, String var1, String var2, byte[] var3, Map<String, String> var4, long var5, BiConsumer<ScriptNetApi.Data, String> var7
   ) {
      URI var8 = method3275(var0, var2);
      if (!method3274(items, var0, 30)) {
         throw ScriptValueUtils.method6251("больше 30 запросов в минуту");
      } else {
         Thread.ofPlatform()
            .daemon(true)
            .name("zenith-script-http")
            .start(
               () -> {
                  try {
                     method3277(var8);
                     Builder var8x = HttpRequest.newBuilder(var8).timeout(Duration.ofSeconds(15L)).header("User-Agent", "ZenithScript/" + var0.getId());
                     int var9 = 0;

                     for (Entry var11 : var4.entrySet()) {
                        String var12 = ((String)var11.getKey()).toLowerCase(Locale.ROOT);
                        if (!var12.equals("host")
                           && !var12.equals("cookie")
                           && !var12.startsWith("proxy")
                           && !var12.equals("connection")
                           && !var12.startsWith("x-forwarded")
                           && !var12.equals("forwarded")
                           && !var12.equals("x-real-ip")) {
                           var9++;
                           if (var9 <= 32 && ((String)var11.getKey()).length() <= 64 && ((String)var11.getValue()).length() <= 4096) {
                              try {
                                 var8x.header((String)var11.getKey(), (String)var11.getValue());
                              } catch (IllegalArgumentException var16) {
                              }
                           }
                        }
                     }

                     var8x.method(var1, var3 == null ? BodyPublishers.noBody() : BodyPublishers.ofByteArray(var3));
                     HttpResponse var19 = httpClient.send(var8x.build(), BodyHandlers.ofInputStream());

                     byte[] var20;
                     try (InputStream var21 = (InputStream)var19.body()) {
                        var20 = var21.readNBytes((int)var5 + 1);
                     }

                     if (var20.length > var5) {
                        var7.accept(null, "ответ больше " + var5 / 1024L + " КБ");
                        return;
                     }

                     var7.accept(new ScriptNetApi.Data(var19.statusCode(), var20, var19.headers().map()), null);
                  } catch (Exception var18) {
                     var7.accept(null, var18.getMessage() == null ? var18.getClass().getSimpleName() : var18.getMessage());
                  }
               }
            );
      }
   }

   public static void method3279(Script var0, String var1, long var2, BiConsumer<byte[], String> var4) {
      method3278(var0, "GET", var1, null, Map.of(), var2, (var1x, var2x) -> {
         if (var2x != null) {
            var4.accept(null, var2x);
         } else if (var1x.getIntValue() / 100 != 2) {
            var4.accept(null, "сервер ответил " + var1x.getIntValue());
         } else {
            var4.accept(var1x.getByteValues(), null);
         }
      });
   }

   private static Scriptable method3280(Scriptable var0, ScriptNetApi.Data var1) {
      String var2 = new String(var1.getByteValues(), StandardCharsets.UTF_8);
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var0);
      var1.getItems().forEach((var1x, var2x) -> {
         if (var1x != null && !var2x.isEmpty()) {
            var3.method6277(var1x.toLowerCase(Locale.ROOT), var2x.get(0));
         }
      });
      return ScriptValueUtils.method6247(var0)
         .method6277("status", (double)var1.getIntValue())
         .method6277("ok", var1.getIntValue() / 100 == 2)
         .method6277("body", var2)
         .method6277("headers", var3.getScriptableObject())
         .method6274("json", var2x -> {
            try {
               return ScriptValueUtils.method6273(var0, JsonParser.parseString(var2));
            } catch (RuntimeException var4) {
               throw ScriptValueUtils.method6251("ответ не JSON");
            }
         })
         .getScriptableObject();
   }

   public static Scriptable method3281(Script var0, Storage var1, Scriptable var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      BiFunction<String, Object[], Object> var4 = (var3x, var4x) -> {
         String var5 = ScriptValueUtils.method6254(var4x, 0, "");
         byte var6 = 1;
         byte[] var7 = null;
         HashMap var8 = new HashMap();
         if (!var3x.equals("GET") && !var3x.equals("DELETE")) {
            Object var9 = ScriptValueUtils.arg(var4x, 1);
            if (var9 instanceof Scriptable var10 && !(var9 instanceof Function)) {
               var7 = ScriptValueUtils.method6271(var10, 0).toString().getBytes(StandardCharsets.UTF_8);
               var8.put("Content-Type", "application/json");
            } else if (!ScriptValueUtils.method6253(var9)) {
               var7 = ScriptValueUtils.method6255(var9).getBytes(StandardCharsets.UTF_8);
            }

            var6 = 2;
         }

         Function var20;
         if (ScriptValueUtils.arg(var4x, var6) instanceof Function var11) {
            var20 = var11;
         } else {
            Scriptable var12 = ScriptValueUtils.method6262(var4x, var6);
            if (var12 != null && ScriptValueUtils.method6263(var12, "headers") instanceof Scriptable var14) {
               for (Object var18 : var14.getIds()) {
                  var8.put(String.valueOf(var18), ScriptValueUtils.method6255(var14.get(String.valueOf(var18), var14)));
               }
            }

            var20 = ScriptValueUtils.method6261(var4x, var6 + 1);
         }

         var var21 = var20;
         method3278(var0, var3x, var5, var7, var8, 2097152L, (var4xx, var5x) -> MinecraftClient.getInstance().execute(() -> {
            if (var0.isRunning() && var21 != null) {
               if (var5x != null) {
                  var1.method1321(var0, var21, null, var5x);
               } else {
                  var1.method1321(var0, var21, method3280(var2, var4xx), null);
               }
            }
         }));
         return null;
      };
      var3.method6274("get", var1x -> var4.apply("GET", var1x));
      var3.method6274("post", var1x -> var4.apply("POST", var1x));
      var3.method6274("put", var1x -> var4.apply("PUT", var1x));
      var3.method6274("delete", var1x -> var4.apply("DELETE", var1x));
      var3.method6274("domains", var2x -> ScriptValueUtils.method6267(var2, new ArrayList<>(var0.method1407())));
      var3.method6274(
         "webhook",
         var2x -> {
            String var3x = ScriptValueUtils.method6254(var2x, 0, "");
            JsonObject var4x = new JsonObject();
            var4x.addProperty("content", ScriptValueUtils.method6254(var2x, 1, ""));
            var4x.addProperty("username", ScriptValueUtils.method6254(var2x, 2, "Zenith"));
            method3278(
               var0,
               "POST",
               var3x,
               var4x.toString().getBytes(StandardCharsets.UTF_8),
               Map.of("Content-Type", "application/json"),
               65536L,
               (var2xx, var3xx) -> {
                  if (var3xx != null) {
                     MinecraftClient.getInstance().execute(() -> var1.method1327(var0, "warn", "webhook: " + var3xx));
                  }
               }
            );
            return null;
         }
      );
      return var3.getScriptableObject();
   }

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   public static String method3282(Script var0) {
      OriginUtil.Data2 var1 = OriginUtil.method6921(var0);
      return var1 != null ? "m:" + var1.getUUID() : "l:" + var0.getId();
   }

   public static Scriptable method3283(Script var0, Storage var1, Scriptable var2) {
      ConcurrentHashMap var3 = new ConcurrentHashMap();
      String var4 = method3282(var0);
      Runnable var5 = EventManager.main()
         .subscribe(
            Event10.class,
            (byte)2,
            (var5x, var6x) -> {
               CloudPackets.Data18 var7 = ((Event10)var6x).getData18();
               if (var7.kind() == 50 && var0.isRunning()) {
                  JsonObject var8 = var7.getJsonObject();
                  String var9 = var8.has("s") ? var8.get("s").getAsString() : "";
                  String var10 = var8.has("c") ? var8.get("c").getAsString() : "";
                  if (var9.equals(var4)) {
                     List<Function> var11 = (List)var3.get(var10);
                     if (var11 != null && !var11.isEmpty()) {
                        String var12 = var8.has("d") ? var8.get("d").getAsString() : "null";
                        MinecraftClient.getInstance()
                           .execute(
                              () -> {
                                 Object var6xx;
                                 try {
                                    var6xx = ScriptValueUtils.method6273(var2, JsonParser.parseString(var12));
                                 } catch (RuntimeException var10x) {
                                    var6xx = var12;
                                 }

                                 ScriptableObject var7x = ScriptValueUtils.method6247(var2)
                                    .method6277("uid", (double)var7.uid())
                                    .method6277("name", var7.username())
                                    .getScriptableObject();

                                 for (Object var9x_o : (Iterable<?>)(Object)(var11)) {
                                    Function var9x = (Function)var9x_o;
                                    var1.method1321(var0, var9x, var6xx, var7x);
                                 }
                              }
                           );
                     }
                  }
               }
            }
         );
      var0.method1413(var5::run);
      ScriptValueUtils.Entry2 var6 = ScriptValueUtils.method6247(var2);
      var6.method6274("on", var2x -> {
         var var3x = ScriptValueUtils.method6260(var2x, 1);
         String var4x = ScriptValueUtils.method6254(var2x, 0, "default");
         if (var4x.length() > 64) {
            throw ScriptValueUtils.method6251("имя канала длиннее 64 символов");
         } else {
            java.util.List<Function> var5x = (java.util.List<Function>)var3.computeIfAbsent(var4x, var0xx -> new CopyOnWriteArrayList());
            if (var5x.size() >= 32) {
               throw ScriptValueUtils.method6251("больше 32 обработчиков на канал");
            } else {
               var5x.add(var3x);
               return ScriptValueUtils.method6247(var2).method6274("off", var2xx -> var5x.remove(var3x)).getScriptableObject();
            }
         }
      });
      var6.method6274("off", var1x -> {
         List var2x = (List)var3.remove(ScriptValueUtils.method6254(var1x, 0, "default"));
         return var2x != null;
      });
      var6.method6274("send", var2x -> {
         Cloud var3x = cloud();
         if (var3x == null || !var3x.method7860()) {
            return false;
         } else if (!method3274(items2, var0, 120)) {
            throw ScriptValueUtils.method6251("больше 120 сообщений в минуту");
         } else {
            String var4x = ScriptValueUtils.method6271(ScriptValueUtils.arg(var2x, 1), 0).toString();
            if (var4x.length() > 2048) {
               throw ScriptValueUtils.method6251("сообщение больше 2048 символов");
            } else {
               JsonObject var5x = new JsonObject();
               String var6x = ScriptValueUtils.method6254(var2x, 0, "default");
               if (var6x.length() > 64) {
                  throw ScriptValueUtils.method6251("имя канала длиннее 64 символов");
               } else {
                  var5x.addProperty("s", var4);
                  var5x.addProperty("c", var6x);
                  var5x.addProperty("d", var4x);
                  Object var7 = ScriptValueUtils.arg(var2x, 2);
                  if (ScriptValueUtils.method6253(var7)) {
                     var5x.addProperty("to", "party");
                  } else if (var7 instanceof Number var8) {
                     var5x.addProperty("to", String.valueOf(var8.longValue()));
                  } else {
                     var5x.addProperty("to", ScriptValueUtils.method6255(var7));
                  }

                  var3x.getPartyMember().method1179(var5x);
                  return true;
               }
            }
         }
      });
      var6.method6274("online", var0x -> {
         Cloud var1x = cloud();
         return var1x != null && var1x.method7860();
      });
      var6.method6274(
         "me",
         var1x -> ScriptValueUtils.method6247(var2).method6277("uid", (double)Cloud.accountUid()).method6277("name", Cloud.accountName()).getScriptableObject()
      );
      return var6.getScriptableObject();
   }

   public static Scriptable method3284(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var2.method6274(
         "send",
         var1x -> {
            MinecraftClient var2x = MinecraftClient.getInstance();
            if (var2x.player != null && var2x.getNetworkHandler() != null) {
               ClientPlayNetworkHandler var3 = var2x.getNetworkHandler();
               String var4 = ScriptValueUtils.method6254(var1x, 0, "").toLowerCase(Locale.ROOT);
               if (var4.equals("chat") || var4.equals("command")) {
                  ScriptPermissions.method4(var0, "chat");
                  String var5 = ScriptValueUtils.method6254(var1x, 1, "");
                  if (var5.isBlank() || var5.length() > 256) {
                     return false;
                  }

                  ScriptScreenApi.method7062(var0);
               }

               switch (var4) {
                  case "swing":
                     var3.sendPacket(new HandSwingC2SPacket(ScriptValueUtils.method6254(var1x, 1, "main").equals("off") ? Hand.OFF_HAND : Hand.MAIN_HAND));
                     break;
                  case "slot":
                     var3.sendPacket(new UpdateSelectedSlotC2SPacket(Math.max(0, Math.min(8, ScriptValueUtils.method6257(var1x, 1, 0)))));
                     break;
                  case "sprint":
                     var3.sendPacket(
                        new ClientCommandC2SPacket(var2x.player, ScriptValueUtils.method6259(var1x, 1, true) ? Mode.START_SPRINTING : Mode.STOP_SPRINTING)
                     );
                     break;
                  case "look":
                     var3.sendPacket(
                        new LookAndOnGround(
                           (float)ScriptValueUtils.method6256(var1x, 1, var2x.player.getYaw()),
                           (float)Math.max(-90.0, Math.min(90.0, ScriptValueUtils.method6256(var1x, 2, var2x.player.getPitch()))),
                           ScriptValueUtils.method6259(var1x, 3, var2x.player.isOnGround()),
                           var2x.player.horizontalCollision
                        )
                     );
                     break;
                  case "ground":
                     var3.sendPacket(new OnGroundOnly(ScriptValueUtils.method6259(var1x, 1, var2x.player.isOnGround()), var2x.player.horizontalCollision));
                     break;
                  case "swap_hands":
                     var3.sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                     break;
                  case "drop":
                     var3.sendPacket(
                        new PlayerActionC2SPacket(
                           ScriptValueUtils.method6259(var1x, 1, false) ? Action.DROP_ALL_ITEMS : Action.DROP_ITEM, BlockPos.ORIGIN, Direction.DOWN
                        )
                     );
                     break;
                  case "release_use":
                     var3.sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
                     break;
                  case "close":
                     var3.sendPacket(new CloseHandledScreenC2SPacket(var2x.player.currentScreenHandler.syncId));
                     break;
                  case "chat":
                     var3.sendChatMessage(ScriptValueUtils.method6254(var1x, 1, ""));
                     break;
                  case "command":
                     var3.sendChatCommand(ScriptValueUtils.method6254(var1x, 1, "").replaceFirst("^/", ""));
                     break;
                  default:
                     throw ScriptValueUtils.method6251("packets.send: swing, slot, sprint, look, ground, swap_hands, drop, release_use, close, chat, command");
               }

               return true;
            } else {
               return false;
            }
         }
      );
      var2.method6274(
         "names",
         var1x -> ScriptValueUtils.method6267(
            var1, List.of("swing", "slot", "sprint", "look", "ground", "swap_hands", "drop", "release_use", "close", "chat", "command")
         )
      );
      return var2.getScriptableObject();
   }

   public static Scriptable method3285(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var2.method6274("list", var1x -> ScriptValueUtils.method6267(var1, new ArrayList<>(Zenith.getInstance().getFriends().getItems())));
      var2.method6274("is", var0x -> Zenith.getInstance().getFriends().method7627(ScriptValueUtils.method6254(var0x, 0, "")));
      var2.method6274("add", var1x -> {
         ScriptPermissions.method4(var0, "friends");
         String var2x = ScriptValueUtils.method6254(var1x, 0, "").strip();
         if (!var2x.matches("[A-Za-z0-9_]{2,16}")) {
            throw ScriptValueUtils.method6251("ник: 2-16 символов, латиница, цифры и _");
         } else {
            Friends var3 = Zenith.getInstance().getFriends();
            if (!var3.method7627(var2x)) {
               var3.add(var2x);
               var3.save();
            }

            return null;
         }
      });
      var2.method6274("remove", var1x -> {
         ScriptPermissions.method4(var0, "friends");
         Zenith.getInstance().getFriends().method7629(ScriptValueUtils.method6254(var1x, 0, ""));
         return null;
      });
      return var2.getScriptableObject();
   }

   public static Scriptable method3286(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var2.method6274("list", var1x -> {
         ArrayList var2x = new ArrayList();
         File var3 = Manager.file;
         File[] var4 = var3 == null ? null : var3.listFiles();
         if (var4 != null) {
            for (File var8 : var4) {
               String var9 = var8.getName();
               int var10 = var9.lastIndexOf(46);
               if (var8.isFile() && var10 > 0) {
                  var2x.add(var9.substring(0, var10));
               }
            }
         }

         return ScriptValueUtils.method6267(var1, var2x);
      });
      var2.method6274("load", var1x -> {
         ScriptPermissions.method4(var0, "configs");
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         if (var2x.matches("[\\p{L}\\p{N} _.-]{1,64}") && !var2x.contains("..")) {
            return Zenith.getInstance().getManager().method7766(var2x);
         } else {
            throw ScriptValueUtils.method6251("неверное имя конфига");
         }
      });
      return var2.getScriptableObject();
   }

   public static final class Data {
      private final int intValue;
      private final byte[] byteValues;
      private final Map<String, List<String>> items;

      public int intValue() {
         return this.intValue;
      }

      public byte[] byteValues() {
         return this.byteValues;
      }

      public Map<String, List<String>> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.byteValues, other.byteValues) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, byteValues, items);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "byteValues=" + this.byteValues + ", " + "items=" + this.items + "]";
      }

      public Data(int var1, byte[] var2, Map<String, List<String>> var3) {
         this.intValue = var1;
         this.byteValues = var2;
         this.items = var3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public byte[] getByteValues() {
         return this.byteValues;
      }

      public Map<String, List<String>> getItems() {
         return this.items;
      }
   }
}

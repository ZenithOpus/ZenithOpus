package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptyMap;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState.UpdateData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.CaptchaLogger;
import org.zenith.utility.mixin.accessors.ItemFrameEntityAccessor;
import org.zenith.utility.network.Cloud;

@ModuleInfo(
   name = "AutoCapcha",
   category = ModuleCategory.MISC,
   description = "module.autocapcha.description"
)
public final class AutoCapchaModule extends Module {
   public static final AutoCapchaModule INSTANCE = new AutoCapchaModule();
   private static final int CONST_intValue = 128;
   private static final int CONST_intValue2 = 250;
   private static final int CONST_intValue3 = 150;
   private static final int CONST_intValue4 = 3;
   private static final long CONST_longValue = 15000L;
   private static final long CONST_longValue2 = 10000L;
   private static final long CONST_longValue3 = 700L;
   private static final long CONST_longValue4 = 5000L;
   private static final long CONST_longValue5 = 6000L;
   private static final String[] texts = new String[]{
      "капч", "captcha", "введите код", "введи код", "код с карты", "введите номер", "номер с картин", "с картинки", "картинке", "введите число"
   };
   private static final String[] texts2 = new String[]{"успеш", "passed", "verified"};
   private static final ExecutorService executorService = Executors.newCachedThreadPool(var0 -> {
      Thread var1 = new Thread(var0, "auto-capcha");
      var1.setDaemon(true);
      return var1;
   });
   private final Map<Integer, int[]> items = new ConcurrentHashMap<>();
   private final Map<Integer, double[]> items2 = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> items3 = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> items4 = new ConcurrentHashMap<>();
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   private final AtomicLong atomicLong = new AtomicLong();
   private final Object obj = new Object();
   private volatile double[] look;
   private volatile long joinTime;
   private volatile long keywordTime;
   private volatile long lastTileTime;
   private volatile long lastAttemptTime;
   private volatile int lastMapId = -1;
   private volatile int lastAttemptHash;

   private AutoCapchaModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState(true);
      this.log("enabled and armed");
   }

   @Override
   public void onDisable() {
      this.resetState(false);
      super.onDisable();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         Packet var2 = var1.getPacket3();
         AutoCapchaModule.Data var3 = this.method157();
         if (var2 instanceof GameJoinS2CPacket || var2 instanceof PlayerRespawnS2CPacket) {
            this.resetState(true);
            this.log("join detected, armed");
         } else if (var2 instanceof PlayerPositionLookS2CPacket var9) {
            EntityPosition var5 = var9.change();
            this.look = new double[]{var5.position().x, var5.position().y, var5.position().z, var5.yaw()};
         } else if (var2 instanceof EntitySpawnS2CPacket var4
            && (var4.getEntityType() == EntityType.ITEM_FRAME || var4.getEntityType() == EntityType.GLOW_ITEM_FRAME)) {
            this.items2.put(var4.getEntityId(), new double[]{var4.getX(), var4.getY(), var4.getZ(), var4.getEntityData()});
         } else if (var2 instanceof EntityTrackerUpdateS2CPacket var8) {
            this.method148(var8, var3);
         } else if (var2 instanceof MapUpdateS2CPacket var7) {
            this.method149(var7.mapId().id(), var7.updateData().orElse(null), var3);
         } else {
            String var6 = packetText(var2);
            if (var6 != null) {
               this.method150(var6, var3);
            }
         }
      }
   }

   private void method148(EntityTrackerUpdateS2CPacket var1, AutoCapchaModule.Data var2) {
      for (SerializedEntry var4 : var1.trackedValues()) {
         if (var4.value() instanceof ItemStack var5) {
            MapIdComponent var8 = var5.get(DataComponentTypes.MAP_ID);
            if (var8 != null) {
               this.items3.put(var1.id(), var8.id());
               this.lastTileTime = System.currentTimeMillis();
               this.method151(var2);
            }
         } else if (var4.id() == ItemFrameEntityAccessor.getRotationData().id() && var4.value() instanceof Integer var6) {
            this.items4.put(var1.id(), var6);
            this.lastTileTime = System.currentTimeMillis();
         }
      }
   }

   private void method149(int var1, UpdateData var2, AutoCapchaModule.Data var3) {
      if (var2 != null && var2.width() > 0 && var2.height() > 0) {
         byte[] var4 = var2.colors();
         int var5 = var2.width();
         int var6 = var2.height();
         if (var4.length >= var5 * var6) {
            synchronized (this.obj) {
               int[] var8 = this.items.computeIfAbsent(var1, var0 -> new int[16384]);

               for (int var9 = 0; var9 < var6; var9++) {
                  int var10;
                  if ((var10 = var2.startZ() + var9) >= 0 && var10 < 128) {
                     for (int var11 = 0; var11 < var5; var11++) {
                        int var12;
                        if ((var12 = var2.startX() + var11) >= 0 && var12 < 128) {
                           var8[var12 + var10 * 128] = var4[var11 + var9 * var5] & 255;
                        }
                     }
                  }
               }
            }

            this.lastMapId = var1;
            this.lastTileTime = System.currentTimeMillis();
            this.method151(var3);
         }
      }
   }

   private void method150(String var1, AutoCapchaModule.Data var2) {
      if (!var1.isBlank()) {
         String var3 = var1.toLowerCase(Locale.ROOT);

         for (String var7 : texts) {
            if (var3.contains(var7)) {
               this.keywordTime = System.currentTimeMillis();
               this.log("captcha prompt: " + CaptchaLogger.trim(var1));
               this.method151(var2);
               return;
            }
         }

         for (String var11 : texts2) {
            if (var3.contains(var11)) {
               this.log("captcha accepted: " + CaptchaLogger.trim(var1));
               this.resetState(false);
               return;
            }
         }
      }
   }

   private void method151(AutoCapchaModule.Data var1) {
      if (var1 != null && (this.lastMapId >= 0 || (this.items3 != null && this.items3.getClass() == EmptyMap.class ? false : !this.items3.isEmpty()))) {
         long var2 = System.currentTimeMillis();
         boolean var4 = this.joinTime != 0L && var2 - this.joinTime <= 15000L;
         boolean var5 = this.keywordTime != 0L && var2 - this.keywordTime <= 10000L;
         if (var4 || var5) {
            if (this.atomicBoolean.compareAndSet(false, true)) {
               long var6 = this.atomicLong.get();
               executorService.submit(() -> {
                  label62: {
                     label63: {
                        try {
                           try {
                              while (true) {
                                 long var4x;
                                 if (var6 == this.atomicLong.get() && (var4x = System.currentTimeMillis() - this.lastTileTime) < 700L) {
                                    Thread.sleep(700L - var4x);
                                 } else {
                                    if (var6 != this.atomicLong.get()) {
                                       break label62;
                                    }

                                    this.awaitFullWall(var6);
                                    this.method152(var1, var6);
                                    break label63;
                                 }
                              }
                           } catch (InterruptedException var9) {
                              Thread.currentThread().interrupt();
                           }
                        } catch (Throwable var10) {
                           this.atomicBoolean.set(false);
                           throw var10;
                        }

                        this.atomicBoolean.set(false);
                        return;
                     }

                     this.atomicBoolean.set(false);
                     return;
                  }

                  this.atomicBoolean.set(false);
               });
            }
         }
      }
   }

   private void awaitFullWall(long var1) throws InterruptedException {
      long var3 = System.currentTimeMillis() + 5000L;

      while (var1 == this.atomicLong.get() && System.currentTimeMillis() < var3) {
         AutoCapchaModule.Data2 var5 = this.method156();
         if (var5 == null || var5.tiles() >= var5.cols() * var5.rows()) {
            return;
         }

         Thread.sleep(250L);
      }
   }

   private void method152(AutoCapchaModule.Data var1, long var2) {
      if (var2 == this.atomicLong.get() && this.isEnabledRaw() && this.atomicBoolean2.compareAndSet(false, true)) {
         AutoCapchaModule.Data2 var6 = this.method156();
         BufferedImage var4;
         int var5;
         if (var6 != null) {
            var4 = var6.image();
            var5 = var6.hash();
            method158(var1, "prepared wall " + var6.cols() + "x" + var6.rows() + " tiles=" + var6.tiles());
         } else {
            int[] var7 = this.cloneBuffer(this.lastMapId);
            if (var7 == null || isUniform(var7)) {
               this.atomicBoolean2.set(false);
               return;
            }

            var4 = render(var7);
            var5 = Arrays.hashCode(var7);
            method158(var1, "prepared map " + this.lastMapId);
         }

         long var10 = System.currentTimeMillis();
         if (var5 == this.lastAttemptHash && var10 - this.lastAttemptTime < 6000L) {
            this.atomicBoolean2.set(false);
         } else {
            this.lastAttemptHash = var5;
            BufferedImage var9 = resizeForApi(var4);
            executorService.submit(() -> this.method153(var9, var1, var2));
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method153(BufferedImage var1, AutoCapchaModule.Data var2, long var3) {
      String var5 = null;
      boolean var17 = false /* VF: Semaphore variable */;

      label177: {
         label176: {
            label175: {
               label189: {
                  label190: {
                     try {
                        var17 = true;
                        int var6 = 1;

                        while (true) {
                           label182: {
                              label161:
                              if (var6 <= 3) {
                                 if (var3 != this.atomicLong.get()) {
                                    var17 = false;
                                    break label177;
                                 }

                                 if (!this.isEnabledRaw()) {
                                    var17 = false;
                                    break label177;
                                 }

                                 if (!var2.isConnected()) {
                                    var17 = false;
                                    break label177;
                                 }

                                 StoreApi var7 = Zenith.getInstance().getStoreApi();
                                 if (var7 == null || Cloud.accountUid() <= 0L) {
                                    var5 = "Cloud account is not ready";
                                    method158(var2, var5);
                                    method159(var2.label() + ": облако ещё не готово для решения капчи", true);
                                    var17 = false;
                                    break label176;
                                 }

                                 try {
                                    method158(var2, "Cloud captcha attempt " + var6 + "/3");
                                    int var8 = this.method154(var2);
                                    byte[] var9 = encodePng(var1);
                                    String var10 = var7.method1984(var9, var8, var8).join();
                                    if (isSafeAnswer(var10)) {
                                       String var11 = var10.trim();
                                       if (var8 > 0 && var11.length() != var8) {
                                          var5 = "answer length " + var11.length() + " != " + var8;
                                          method158(var2, var5 + ": " + CaptchaLogger.trim(var11));
                                          Thread.sleep(1000L * var6);
                                          break label182;
                                       }

                                       if (this.method155(var2, var11)) {
                                          method158(var2, "answer sent: " + var11);
                                          method159(var2.label() + ": капча решена -> " + var11, false);
                                          this.resetState(false);
                                          var17 = false;
                                       } else {
                                          method159(var2.label() + ": не удалось отправить ответ капчи", true);
                                          var17 = false;
                                       }
                                       break label175;
                                    }

                                    if (var10 != null && !var10.isBlank()) {
                                       var5 = "Cloud returned an unsafe answer";
                                       method158(var2, var5 + ": " + CaptchaLogger.trim(var10));
                                       break label161;
                                    }

                                    method158(var2, "Cloud returned no answer on attempt " + var6);
                                 } catch (InterruptedException var18) {
                                    Thread.currentThread().interrupt();
                                    var17 = false;
                                    break label189;
                                 } catch (Exception var19) {
                                    var5 = rootMessage(var19);
                                    method158(var2, "Cloud attempt " + var6 + " failed: " + var5);
                                    if (isFinalFailure(var19)) {
                                       break label161;
                                    }
                                 }

                                 Thread.sleep(1000L * var6);
                                 break label182;
                              }

                              method159(var2.label() + ": капча не решена" + (var5 == null ? "" : " (" + var5 + ")"), true);
                              var17 = false;
                              break label190;
                           }

                           var6++;
                        }
                     } catch (InterruptedException var20) {
                        Thread.currentThread().interrupt();
                        var17 = false;
                     } finally {
                        if (var17) {
                           this.lastAttemptTime = System.currentTimeMillis();
                           this.atomicBoolean2.set(false);
                        }
                     }

                     this.lastAttemptTime = System.currentTimeMillis();
                     this.atomicBoolean2.set(false);
                     return;
                  }

                  this.lastAttemptTime = System.currentTimeMillis();
                  this.atomicBoolean2.set(false);
                  return;
               }

               this.lastAttemptTime = System.currentTimeMillis();
               this.atomicBoolean2.set(false);
               return;
            }

            this.lastAttemptTime = System.currentTimeMillis();
            this.atomicBoolean2.set(false);
            return;
         }

         this.lastAttemptTime = System.currentTimeMillis();
         this.atomicBoolean2.set(false);
         return;
      }

      this.lastAttemptTime = System.currentTimeMillis();
      this.atomicBoolean2.set(false);
   }

   private int method154(AutoCapchaModule.Data var1) {
      ClientPlayNetworkHandler var2 = var1.getClientPlayNetworkHandler();
      return var2 != null && var2.getServerInfo() != null && var2.getServerInfo().address.toLowerCase(Locale.ROOT).contains("funtime.su") ? 5 : 0;
   }

   private boolean method155(AutoCapchaModule.Data var1, String var2) {
      ClientPlayNetworkHandler var3 = var1.getClientPlayNetworkHandler();
      if (var3 == null) {
         return false;
      } else {
         MinecraftClient.getInstance().execute(() -> {
            if (var2.startsWith("/")) {
               var3.sendChatCommand(var2.substring(1));
            } else {
               var3.sendChatMessage(var2);
            }

            System.out.println(var2);
         });
         return true;
      }
   }

   private AutoCapchaModule.Data2 method156() {
      HashMap var1 = new HashMap();

      for (Entry var3 : this.items3.entrySet()) {
         double[] var4 = this.items2.get(var3.getKey());
         if (var4 != null) {
            boolean var40;
            label122: {
               var var10000 = this.items;
               Object var10001 = var3.getValue();
               if (var10000 != null) {
                  if (var10000.isEmpty()) {
                     var40 = false;
                     break label122;
                  }
               }

               var40 = var10000.containsKey(var10001);
            }

            int var5;
            if (var40 && (var5 = (int)var4[3]) >= 2 && var5 <= 5) {
               double var6 = var5 >= 4 ? var4[0] : var4[2];
               long var8 = (long)var5 << 40 ^ Math.round(var6 * 4.0) & 1099511627775L;
               ((java.util.List)var1.computeIfAbsent(var8, var0 -> new ArrayList<>())).add(new int[]{(Integer)var3.getKey(), (Integer)var3.getValue()});
            }
         }
      }

      List<int[]> var29 = this.choosePlane(var1);
      if (var29 != null && (!var29.isEmpty())) {
         double[] var30 = this.items2.get(((int[])var29.getFirst())[0]);
         if (var30 == null) {
            return null;
         } else {
            int var31;
            boolean var32 = (var31 = (int)var30[3]) >= 4;
            double var33 = Double.MAX_VALUE;
            double var34 = -Double.MAX_VALUE;
            double var10 = Double.MAX_VALUE;
            double var12 = -Double.MAX_VALUE;

            for (Object var15_o : (Iterable<?>)(Object)(var29)) {
               int[] var15 = (int[])var15_o;
               double[] var16 = this.items2.get(var15[0]);
               if (var16 != null) {
                  double var17 = var32 ? var16[2] : var16[0];
                  var33 = Math.min(var33, var17);
                  var34 = Math.max(var34, var17);
                  var10 = Math.min(var10, var16[1]);
                  var12 = Math.max(var12, var16[1]);
               }
            }

            int var35 = (int)Math.round(var34 - var33) + 1;
            int var36 = (int)Math.round(var12 - var10) + 1;
            if (var35 >= 1 && var36 >= 1 && var35 <= 8 && var36 <= 8 && var35 * var36 >= 2) {
               BufferedImage var37 = new BufferedImage(var35 * 128, var36 * 128, 1);
               Graphics2D var38 = var37.createGraphics();
               var38.setColor(Color.WHITE);
               var38.fillRect(0, 0, var37.getWidth(), var37.getHeight());
               int var18 = 1;
               int var19 = 0;

               for (Object var21_o : (Iterable<?>)(Object)(var29)) {
                  int[] var21 = (int[])var21_o;
                  int[] var22 = this.cloneBuffer(var21[1]);
                  double[] var23 = this.items2.get(var21[0]);
                  if (var22 != null && var23 != null) {
                     int var24 = this.items4.getOrDefault(var21[0], 0) & 3;
                     var22 = rotateClockwise(var22, var24);
                     double var25 = var32 ? var23[2] : var23[0];

                     int var27 = switch (var31) {
                        case 3, 4 -> (int)Math.round(var25 - var33);
                        default -> (int)Math.round(var34 - var25);
                     };
                     int var28 = (int)Math.round(var12 - var23[1]);
                     if (var27 >= 0 && var27 < var35 && var28 >= 0 && var28 < var36) {
                        var38.drawImage(render(var22), var27 * 128, var28 * 128, null);
                        var18 = ((var18 * 31 + var21[1]) * 31 + Arrays.hashCode(var22)) * 31 + var27 * 17 + var28;
                        var19++;
                     }
                  }
               }

               var38.dispose();
               return var19 == 0 ? null : new AutoCapchaModule.Data2(var37, var18, var35, var36, var19);
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private List<int[]> choosePlane(Map<Long, List<int[]>> var1) {
      if (var1.isEmpty()) {
         return null;
      } else {
         List var2 = null;
         double var3 = -1.0;
         double[] var5 = this.look;
         if (var5 != null) {
            double var6;
            double var8 = -Math.sin(var6 = Math.toRadians(var5[3]));
            double var10 = Math.cos(var6);

            for (List var13 : var1.values()) {
               double var14 = 0.0;
               double var16 = 0.0;
               int var18 = 0;

               for (Object var20_o : (Iterable<?>)(Object)(var13)) {
                  int[] var20 = (int[])var20_o;
                  double[] var21 = this.items2.get(var20[0]);
                  if (var21 != null) {
                     var14 += var21[0];
                     var16 += var21[2];
                     var18++;
                  }
               }

               if (var18 != 0) {
                  var14 /= var18;
                  var16 /= var18;
                  double var32 = var14 - var5[0];
                  double var33 = var16 - var5[2];
                  double var23;
                  double var25;
                  double var27;
                  if (!((var23 = Math.sqrt(var32 * var32 + var33 * var33)) < 0.01)
                     && !(var23 > 32.0)
                     && (var27 = (var25 = var32 / var23 * var8 + var33 / var23 * var10) < 0.2 ? -1.0 : var25 * 100.0 / (1.0 + var23)) > var3) {
                     var3 = var27;
                     var2 = var13;
                  }
               }
            }
         }

         if (var2 == null) {
            for (List var7 : var1.values()) {
               if (var2 == null || var7.size() > var2.size()) {
                  var2 = var7;
               }
            }
         }

         return var2;
      }
   }

   private int[] cloneBuffer(int var1) {
      synchronized (this.obj) {
         int[] var3 = this.items.get(var1);
         return var3 == null ? null : (int[])var3.clone();
      }
   }

   private void resetState(boolean var1) {
      this.atomicLong.incrementAndGet();
      synchronized (this.obj) {
         this.items.clear();
      }

      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.atomicBoolean.set(false);
      this.atomicBoolean2.set(false);
      this.look = null;
      this.lastMapId = -1;
      this.lastAttemptHash = 0;
      this.lastAttemptTime = 0L;
      this.lastTileTime = System.currentTimeMillis();
      this.keywordTime = 0L;
      this.joinTime = var1 ? System.currentTimeMillis() : 0L;
   }

   private AutoCapchaModule.Data method157() {
      ClientPlayNetworkHandler var1 = MinecraftClient.getInstance().getNetworkHandler();
      return var1 == null ? null : new AutoCapchaModule.Data(var1, "main");
   }

   private void log(String var1) {
      CaptchaLogger.log("[main] " + var1);
   }

   private static void method158(AutoCapchaModule.Data var0, String var1) {
      CaptchaLogger.log("[" + var0.label() + "] " + var1);
   }

   private static String packetText(Packet<?> var0) {
      if (var0 instanceof GameMessageS2CPacket var6) {
         return var6.content().getString();
      } else if (var0 instanceof ProfilelessChatMessageS2CPacket var5) {
         return var5.message().getString();
      } else if (var0 instanceof ChatMessageS2CPacket var4) {
         return var4.unsignedContent() == null ? var4.body().content() : var4.unsignedContent().getString();
      } else if (var0 instanceof OverlayMessageS2CPacket var3) {
         return var3.text().getString();
      } else if (var0 instanceof SubtitleS2CPacket var2) {
         return var2.text().getString();
      } else {
         return var0 instanceof TitleS2CPacket var1 ? var1.text().getString() : null;
      }
   }

   private static boolean isSafeAnswer(String var0) {
      if (var0 == null) {
         return false;
      } else {
         String var1 = var0.trim();
         return !var1.isEmpty() && var1.length() <= 64
            ? var1.codePoints().allMatch(var0x -> Character.isLetterOrDigit(var0x) || var0x == 95 || var0x == 45)
            : false;
      }
   }

   private static int[] rotateClockwise(int[] var0, int var1) {
      int[] var2 = var0;

      for (int var3 = 0; var3 < var1; var3++) {
         int[] var4 = new int[16384];

         for (int var5 = 0; var5 < 128; var5++) {
            for (int var6 = 0; var6 < 128; var6++) {
               var4[var6 + var5 * 128] = var2[var5 + (127 - var6) * 128];
            }
         }

         var2 = var4;
      }

      return var2;
   }

   private static BufferedImage resizeForApi(BufferedImage var0) {
      if (var0.getWidth() == 250 && var0.getHeight() == 150) {
         return var0;
      } else {
         BufferedImage var1 = new BufferedImage(250, 150, 1);
         Graphics2D var2 = var1.createGraphics();
         var2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         var2.drawImage(var0, 0, 0, 250, 150, null);
         var2.dispose();
         return var1;
      }
   }

   private static byte[] encodePng(BufferedImage var0) throws Exception {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      if (!ImageIO.write(var0, "png", var1)) {
         throw new IllegalStateException("PNG encoder is unavailable");
      } else {
         return var1.toByteArray();
      }
   }

   private static String rootMessage(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null && var1.getCause() != var1) {
         var1 = var1.getCause();
      }

      String var2 = var1.getMessage();
      return var2 != null && !var2.isBlank() ? var2 : var1.getClass().getSimpleName();
   }

   private static boolean isFinalFailure(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null && var1.getCause() != var1) {
         var1 = var1.getCause();
      }

      int var3;
      return !(var1 instanceof StoreApi.Entry var2) ? false : (var3 = var2.getIntValue()) == 503 || var3 >= 400 && var3 < 500 && var3 != 429;
   }

   private static BufferedImage render(int[] var0) {
      BufferedImage var1 = new BufferedImage(128, 128, 1);

      for (int var2 = 0; var2 < 128; var2++) {
         for (int var3 = 0; var3 < 128; var3++) {
            int var4;
            if ((var4 = MapColor.getRenderColor(var0[var3 + var2 * 128])) >>> 24 == 0) {
               var4 = 255 << 16 | (char)-1;
            }

            var1.setRGB(var3, var2, var4 & (255 << 16 | (char)-1));
         }
      }

      return var1;
   }

   private static boolean isUniform(int[] var0) {
      int var1 = var0[0];
      int[] var2 = var0;
      int var3 = var0.length;

      for (int var4 = 0; var4 < var3; var4++) {
         int var5;
         if ((var5 = var2[var4]) != var1) {
            return false;
         }
      }

      return true;
   }

   private static void method159(String var0, boolean var1) {
      MinecraftClient.getInstance().execute(() -> {
         if (var1) {
            ChatLogger.method2021(var0);
         } else {
            ChatLogger.method2019(var0);
         }
      });
   }

   private static final class Data {
      private final ClientPlayNetworkHandler clientPlayNetworkHandler;
      private final String text;

      public ClientPlayNetworkHandler clientPlayNetworkHandler() {
         return this.clientPlayNetworkHandler;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.clientPlayNetworkHandler, other.clientPlayNetworkHandler) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(clientPlayNetworkHandler, text);
      }

      @Override
      public String toString() {
         return "Data[" + "clientPlayNetworkHandler=" + this.clientPlayNetworkHandler + ", " + "text=" + this.text + "]";
      }

      private Data(ClientPlayNetworkHandler var1, String var2) {
         this.clientPlayNetworkHandler = var1;
         this.text = var2;
      }

      private boolean isConnected() {
         return this.clientPlayNetworkHandler != null && this.clientPlayNetworkHandler.getConnection().isOpen();
      }

      public ClientPlayNetworkHandler getClientPlayNetworkHandler() {
         return this.clientPlayNetworkHandler;
      }

      public String label() {
         return this.text;
      }
   }

   private static final class Data2 {
      private final BufferedImage bufferedImage;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public BufferedImage bufferedImage() {
         return this.bufferedImage;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.bufferedImage, other.bufferedImage) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bufferedImage, intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data2[" + "bufferedImage=" + this.bufferedImage + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      private Data2(BufferedImage var1, int var2, int var3, int var4, int var5) {
         this.bufferedImage = var1;
         this.intValue = var2;
         this.intValue2 = var3;
         this.intValue3 = var4;
         this.intValue4 = var5;
      }

      public BufferedImage image() {
         return this.bufferedImage;
      }

      public int hash() {
         return this.intValue;
      }

      public int cols() {
         return this.intValue2;
      }

      public int rows() {
         return this.intValue3;
      }

      public int tiles() {
         return this.intValue4;
      }
   }
}

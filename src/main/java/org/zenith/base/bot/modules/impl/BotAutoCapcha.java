package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState.UpdateData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
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
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.client.events.PacketEvent;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.CaptchaLogger;
import org.zenith.utility.mixin.accessors.ItemFrameEntityAccessor;
import org.zenith.utility.network.Cloud;

@ModuleInfo(
   name = "BotAutoCapcha",
   category = ModuleCategory.MISC,
   description = "module.botautocapcha.description"
)
public final class BotAutoCapcha extends BotModule {
   private static final int MAP_SIZE = 128;
   private static final int API_WIDTH = 250;
   private static final int API_HEIGHT = 150;
   private static final int MAX_RETRIES = 3;
   private static final long POST_JOIN_WINDOW_MS = 15000L;
   private static final long KEYWORD_WINDOW_MS = 10000L;
   private static final long SETTLE_MS = 700L;
   private static final long WALL_WAIT_MS = 5000L;
   private static final long COOLDOWN_MS = 6000L;
   private static final int MAX_SPARE_BUFFERS = 128;
   private static final String[] KEYWORDS = new String[]{
      "капч", "captcha", "введите код", "введи код", "код с карты", "введите номер", "номер с картин", "с картинки", "картинке", "введите число"
   };
   private static final String[] SUCCESS_MARKERS = new String[]{"успеш", "passed", "verified"};
   private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(var0 -> {
      Thread var1 = new Thread(var0, "bot-auto-capcha");
      var1.setDaemon(true);
      return var1;
   });
   private final Map<Integer, byte[]> buffers = new ConcurrentHashMap<>();
   private final LinkedHashSet<Integer> bufferOrder = new LinkedHashSet<>();
   private final Map<Integer, double[]> frames = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> frameItems = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> frameRotations = new ConcurrentHashMap<>();
   private final AtomicBoolean pending = new AtomicBoolean();
   private final AtomicBoolean solving = new AtomicBoolean();
   private final AtomicLong generation = new AtomicLong();
   private final Object bufferLock = new Object();
   private volatile double[] look;
   private volatile long joinTime;
   private volatile long keywordTime;
   private volatile long lastTileTime;
   private volatile long lastAttemptTime;
   private volatile int lastMapId = -1;
   private volatile int lastAttemptHash;

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
      Packet var2 = var1.getPacket();
      BotAutoCapcha.SolveTarget var3 = this.currentTarget();
      if (var2 instanceof GameJoinS2CPacket || var2 instanceof PlayerRespawnS2CPacket) {
         this.resetState(true);
         this.log("join detected, armed");
      } else if (var2 instanceof PlayerPositionLookS2CPacket var11) {
         EntityPosition var12 = var11.change();
         this.look = new double[]{var12.position().x, var12.position().y, var12.position().z, var12.yaw()};
      } else if (var2 instanceof EntitySpawnS2CPacket var4
         && (var4.getEntityType() == EntityType.ITEM_FRAME || var4.getEntityType() == EntityType.GLOW_ITEM_FRAME)) {
         this.frames.put(var4.getEntityId(), new double[]{var4.getX(), var4.getY(), var4.getZ(), var4.getEntityData()});
      } else if (var2 instanceof EntityTrackerUpdateS2CPacket var10) {
         this.handleTrackedValues(var10, var3);
      } else if (var2 instanceof EntitiesDestroyS2CPacket var7) {
         for (int var5 = 0; var5 < var7.getEntityIds().size(); var5++) {
            int var6 = var7.getEntityIds().getInt(var5);
            this.frames.remove(var6);
            this.frameItems.remove(var6);
            this.frameRotations.remove(var6);
         }
      } else if (var2 instanceof MapUpdateS2CPacket var9) {
         this.handleMapUpdate(var9.mapId().id(), var9.updateData().orElse(null), var3);
      } else {
         String var8 = packetText(var2);
         if (var8 != null) {
            this.handleText(var8, var3);
         }
      }
   }

   private void handleTrackedValues(EntityTrackerUpdateS2CPacket var1, BotAutoCapcha.SolveTarget var2) {
      boolean var10;
      label37: {
         var var10000 = this.frames;
         Integer var10001 = var1.id();
         if (var10000 != null) {
            if (var10000.isEmpty()) {
               var10 = false;
               break label37;
            }
         }

         var10 = var10000.containsKey(var10001);
      }

      if (var10) {
         for (SerializedEntry var4 : var1.trackedValues()) {
            if (var4.value() instanceof ItemStack var5) {
               MapIdComponent var8 = var5.get(DataComponentTypes.MAP_ID);
               if (var8 != null) {
                  this.frameItems.put(var1.id(), var8.id());
                  this.lastTileTime = System.currentTimeMillis();
                  this.tryTrigger(var2);
               }
            } else if (var4.id() == ItemFrameEntityAccessor.getRotationData().id() && var4.value() instanceof Integer var6) {
               this.frameRotations.put(var1.id(), var6);
               this.lastTileTime = System.currentTimeMillis();
            }
         }
      }
   }

   private void handleMapUpdate(int var1, UpdateData var2, BotAutoCapcha.SolveTarget var3) {
      if (var2 != null && var2.width() > 0 && var2.height() > 0) {
         byte[] var4 = var2.colors();
         int var5 = var2.width();
         int var6 = var2.height();
         if (var4.length >= var5 * var6) {
            synchronized (this.bufferLock) {
               byte[] var8 = this.buffers.get(var1);
               if (var8 == null) {
                  var8 = new byte[16384];
                  this.buffers.put(var1, var8);
                  this.bufferOrder.add(var1);
                  this.trimBuffers(var1);
               }

               for (int var9 = 0; var9 < var6; var9++) {
                  int var10;
                  if ((var10 = var2.startZ() + var9) >= 0 && var10 < 128) {
                     for (int var11 = 0; var11 < var5; var11++) {
                        int var12;
                        if ((var12 = var2.startX() + var11) >= 0 && var12 < 128) {
                           var8[var12 + var10 * 128] = var4[var11 + var9 * var5];
                        }
                     }
                  }
               }
            }

            this.lastMapId = var1;
            this.lastTileTime = System.currentTimeMillis();
            this.tryTrigger(var3);
         }
      }
   }

   private void handleText(String var1, BotAutoCapcha.SolveTarget var2) {
      if (!var1.isBlank()) {
         String var3 = var1.toLowerCase(Locale.ROOT);

         for (String var7 : KEYWORDS) {
            if (var3.contains(var7)) {
               this.keywordTime = System.currentTimeMillis();
               this.log("captcha prompt: " + CaptchaLogger.trim(var1));
               this.tryTrigger(var2);
               return;
            }
         }

         for (String var11 : SUCCESS_MARKERS) {
            if (var3.contains(var11)) {
               this.log("captcha accepted: " + CaptchaLogger.trim(var1));
               this.resetState(false);
               return;
            }
         }
      }
   }

   private void tryTrigger(BotAutoCapcha.SolveTarget var1) {
      if (var1 != null) {
         if (this.lastMapId < 0) {
            boolean var10000;
            label51: {
               if (this.frameItems != null) {
                  if (this.frameItems.getClass() == EmptyMap.class) {
                     var10000 = true;
                     break label51;
                  }
               }

               var10000 = this.frameItems.isEmpty();
            }

            if (var10000) {
               return;
            }
         }

         long var2 = System.currentTimeMillis();
         int var4 = this.joinTime != 0L && var2 - this.joinTime <= 15000L ? 1 : 0;
         int var5 = this.keywordTime != 0L && var2 - this.keywordTime <= 10000L ? 1 : 0;
         if (var4 != 0 || var5 != 0) {
            if (this.pending.compareAndSet(false, true)) {
               long var6 = this.generation.get();
               EXECUTOR.submit(() -> {
                  label62: {
                     label63: {
                        try {
                           try {
                              while (true) {
                                 long var4x;
                                 if (var6 == this.generation.get() && (var4x = System.currentTimeMillis() - this.lastTileTime) < 700L) {
                                    Thread.sleep(700L - var4x);
                                 } else {
                                    if (var6 != this.generation.get()) {
                                       break label62;
                                    }

                                    this.awaitFullWall(var6);
                                    this.launch(var1, var6);
                                    break label63;
                                 }
                              }
                           } catch (InterruptedException var9) {
                              Thread.currentThread().interrupt();
                           }
                        } catch (Throwable var10) {
                           this.pending.set(false);
                           throw var10;
                        }

                        this.pending.set(false);
                        return;
                     }

                     this.pending.set(false);
                     return;
                  }

                  this.pending.set(false);
               });
            }
         }
      }
   }

   private void awaitFullWall(long var1) throws InterruptedException {
      long var3 = System.currentTimeMillis() + 5000L;

      while (var1 == this.generation.get() && System.currentTimeMillis() < var3) {
         BotAutoCapcha.Stitched var5 = this.stitchCaptchaWall();
         if (var5 == null || var5.tiles() >= var5.cols() * var5.rows()) {
            return;
         }

         Thread.sleep(250L);
      }
   }

   private void launch(BotAutoCapcha.SolveTarget var1, long var2) {
      if (var2 == this.generation.get() && this.isEnabled() && this.solving.compareAndSet(false, true)) {
         BotAutoCapcha.Stitched var6 = this.stitchCaptchaWall();
         BufferedImage var4;
         int var5;
         if (var6 != null) {
            var4 = var6.image();
            var5 = var6.hash();
            log(var1, "prepared wall " + var6.cols() + "x" + var6.rows() + " tiles=" + var6.tiles());
         } else {
            int[] var7 = this.cloneBuffer(this.lastMapId);
            if (var7 == null || isUniform(var7)) {
               this.solving.set(false);
               return;
            }

            var4 = render(var7);
            var5 = Arrays.hashCode(var7);
            log(var1, "prepared map " + this.lastMapId);
         }

         long var10 = System.currentTimeMillis();
         if (var5 == this.lastAttemptHash && var10 - this.lastAttemptTime < 6000L) {
            this.solving.set(false);
         } else {
            this.lastAttemptHash = var5;
            BufferedImage var9 = resizeForApi(var4);
            EXECUTOR.submit(() -> this.solve(var9, var1, var2));
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void solve(BufferedImage var1, BotAutoCapcha.SolveTarget var2, long var3) {
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
                                 if (var3 != this.generation.get()) {
                                    var17 = false;
                                    break label177;
                                 }

                                 if (!this.isEnabled()) {
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
                                    log(var2, var5);
                                    feedback(var2, "облако ещё не готово для решения капчи");
                                    var17 = false;
                                    break label176;
                                 }

                                 try {
                                    log(var2, "Cloud captcha attempt " + var6 + "/3");
                                    int var8 = this.expectedLength(var2);
                                    byte[] var9 = encodePng(var1);
                                    String var10 = var7.method1984(var9, var8, var8).join();
                                    if (isSafeAnswer(var10)) {
                                       String var11 = var10.trim();
                                       if (var8 > 0 && var11.length() != var8) {
                                          var5 = "answer length " + var11.length() + " != " + var8;
                                          log(var2, var5 + ": " + CaptchaLogger.trim(var11));
                                          Thread.sleep(1000L * var6);
                                          break label182;
                                       }

                                       if (this.sendAnswer(var2, var11)) {
                                          log(var2, "answer sent: " + var11);
                                          feedback(var2, "капча решена -> " + var11);
                                          this.resetState(false);
                                          var17 = false;
                                       } else {
                                          feedback(var2, "не удалось отправить ответ капчи");
                                          var17 = false;
                                       }
                                       break label175;
                                    }

                                    if (var10 != null && !var10.isBlank()) {
                                       var5 = "Cloud returned an unsafe answer";
                                       log(var2, var5 + ": " + CaptchaLogger.trim(var10));
                                       break label161;
                                    }

                                    log(var2, "Cloud returned no answer on attempt " + var6);
                                 } catch (InterruptedException var18) {
                                    Thread.currentThread().interrupt();
                                    var17 = false;
                                    break label189;
                                 } catch (Exception var19) {
                                    var5 = rootMessage(var19);
                                    log(var2, "Cloud attempt " + var6 + " failed: " + var5);
                                    if (isFinalFailure(var19)) {
                                       break label161;
                                    }
                                 }

                                 Thread.sleep(1000L * var6);
                                 break label182;
                              }

                              feedback(var2, "капча не решена" + (var5 == null ? "" : " (" + var5 + ")"));
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
                           this.solving.set(false);
                        }
                     }

                     this.lastAttemptTime = System.currentTimeMillis();
                     this.solving.set(false);
                     return;
                  }

                  this.lastAttemptTime = System.currentTimeMillis();
                  this.solving.set(false);
                  return;
               }

               this.lastAttemptTime = System.currentTimeMillis();
               this.solving.set(false);
               return;
            }

            this.lastAttemptTime = System.currentTimeMillis();
            this.solving.set(false);
            return;
         }

         this.lastAttemptTime = System.currentTimeMillis();
         this.solving.set(false);
         return;
      }

      this.lastAttemptTime = System.currentTimeMillis();
      this.solving.set(false);
   }

   private int expectedLength(BotAutoCapcha.SolveTarget var1) {
      String var2 = var1.bot().getAddress();
      return var2.toLowerCase(Locale.ROOT).contains("funtime.su") ? 5 : 0;
   }

   private boolean sendAnswer(BotAutoCapcha.SolveTarget var1, String var2) {
      return var1.bot().sendChat(var2);
   }

   private BotAutoCapcha.Stitched stitchCaptchaWall() {
      HashMap var1 = new HashMap();

      for (Entry var3 : this.frameItems.entrySet()) {
         double[] var4 = this.frames.get(var3.getKey());
         if (var4 != null) {
            boolean var40;
            label122: {
               var var10000 = this.buffers;
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
      if (var29 != null) {
         boolean var41;
         label113: {
            if (var29 != null) {
               if (var29.isEmpty()) {
                  var41 = true;
                  break label113;
               }
            }

            var41 = var29.isEmpty();
         }

         if (!var41) {
            double[] var30 = this.frames.get(((int[])var29.getFirst())[0]);
            if (var30 == null) {
               return null;
            }

            int var31;
            int var32 = (var31 = (int)var30[3]) >= 4 ? 1 : 0;
            double var33 = Double.MAX_VALUE;
            double var34 = -Double.MAX_VALUE;
            double var10 = Double.MAX_VALUE;
            double var12 = -Double.MAX_VALUE;

            for (Object var15_o : (Iterable<?>)(Object)(var29)) {
               int[] var15 = (int[])var15_o;
               double[] var16 = this.frames.get(var15[0]);
               if (var16 != null) {
                  double var17 = var32 != 0 ? var16[2] : var16[0];
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
                  double[] var23 = this.frames.get(var21[0]);
                  if (var22 != null && var23 != null) {
                     int var24 = this.frameRotations.getOrDefault(var21[0], 0) & 3;
                     var22 = rotateClockwise(var22, var24);
                     double var25 = var32 != 0 ? var23[2] : var23[0];

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
               return var19 == 0 ? null : new BotAutoCapcha.Stitched(var37, var18, var35, var36, var19);
            }

            return null;
         }
      }

      return null;
   }

   private List<int[]> choosePlane(Map<Long, List<int[]>> var1) {
      boolean var10000;
      label75: {
         if (var1 != null) {
            if (var1.isEmpty()) {
               var10000 = true;
               break label75;
            }
         }

         var10000 = var1.isEmpty();
      }

      if (var10000) {
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
                  double[] var21 = this.frames.get(var20[0]);
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
      synchronized (this.bufferLock) {
         byte[] var3 = this.buffers.get(var1);
         if (var3 == null) {
            return null;
         } else {
            int[] var4 = new int[var3.length];

            for (int var5 = 0; var5 < var3.length; var5++) {
               var4[var5] = var3[var5] & 255;
            }

            return var4;
         }
      }
   }

   private void trimBuffers(int var1) {
      if (this.buffers.size() > 128) {
         var var2 = new HashSet<>(this.frameItems.values());
         var2.add(var1);
         var2.add(this.lastMapId);
         int var3 = 0;

         for (Integer var5 : this.bufferOrder) {
            if (!var2.contains(var5)) {
               var3++;
            }
         }

         var var6 = this.bufferOrder.iterator();

         while (var3 > 128 && var6.hasNext()) {
            Integer var7 = (Integer)var6.next();
            if (!var2.contains(var7)) {
               var6.remove();
               this.buffers.remove(var7);
               var3--;
            }
         }
      }
   }

   private void resetState(boolean var1) {
      this.generation.incrementAndGet();
      synchronized (this.bufferLock) {
         this.buffers.clear();
         this.bufferOrder.clear();
      }

      this.frames.clear();
      this.frameItems.clear();
      this.frameRotations.clear();
      this.pending.set(false);
      this.solving.set(false);
      this.look = null;
      this.lastMapId = -1;
      this.lastAttemptHash = 0;
      this.lastAttemptTime = 0L;
      this.lastTileTime = System.currentTimeMillis();
      this.keywordTime = 0L;
      this.joinTime = var1 ? System.currentTimeMillis() : 0L;
   }

   private BotAutoCapcha.SolveTarget currentTarget() {
      BotClient var1 = this.bot();
      return var1 != null && var1.isJoined() ? new BotAutoCapcha.SolveTarget(var1) : null;
   }

   private void log(String var1) {
      CaptchaLogger.log("[" + this.botLabel() + "] " + var1);
   }

   private static void log(BotAutoCapcha.SolveTarget var0, String var1) {
      CaptchaLogger.log("[" + var0.label() + "] " + var1);
   }

   private String botLabel() {
      BotClient var1 = this.bot();
      return var1 == null ? "bot" : var1.getName();
   }

   private static void feedback(BotAutoCapcha.SolveTarget var0, String var1) {
      var0.bot().execute(() -> var0.bot().systemMessage("AutoCapcha: " + var1));
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
            && var1.codePoints().allMatch(var0x -> Character.isLetterOrDigit(var0x) || var0x == 95 || var0x == 45);
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

      if (!(var1 instanceof StoreApi.Entry var2)) {
         return false;
      } else {
         int var3;
         return !((var3 = var2.getIntValue()) != 503 && (var3 < 400 || var3 >= 500 || var3 == 429));
      }
   }

   private static BufferedImage render(int[] var0) {
      BufferedImage var1 = new BufferedImage(128, 128, 1);

      for (int var2 = 0; var2 < 128; var2++) {
         for (int var3 = 0; var3 < 128; var3++) {
            int var4;
            if ((var4 = MapColor.getRenderColor(var0[var3 + var2 * 128])) >>> 24 == 0) {
               var4 = 16777215;
            }

            var1.setRGB(var3, var2, var4 & 16777215);
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

   private static final class SolveTarget {
      private final BotClient bot;

      public BotClient bot() {
         return this.bot;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SolveTarget other)) return false;
         return java.util.Objects.equals(this.bot, other.bot);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bot);
      }

      @Override
      public String toString() {
         return "SolveTarget[" + "bot=" + this.bot + "]";
      }

      private SolveTarget(BotClient var1) {
         this.bot = var1;
      }

      private boolean isConnected() {
         return this.bot.isJoined();
      }

      private String label() {
         return this.bot.getName();
      }
   }

   private static final class Stitched {
      private final BufferedImage image;
      private final int hash;
      private final int cols;
      private final int rows;
      private final int tiles;

      public BufferedImage image() {
         return this.image;
      }

      public int hash() {
         return this.hash;
      }

      public int cols() {
         return this.cols;
      }

      public int rows() {
         return this.rows;
      }

      public int tiles() {
         return this.tiles;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Stitched other)) return false;
         return java.util.Objects.equals(this.image, other.image) && java.util.Objects.equals(this.hash, other.hash) && java.util.Objects.equals(this.cols, other.cols) && java.util.Objects.equals(this.rows, other.rows) && java.util.Objects.equals(this.tiles, other.tiles);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(image, hash, cols, rows, tiles);
      }

      @Override
      public String toString() {
         return "Stitched[" + "image=" + this.image + ", " + "hash=" + this.hash + ", " + "cols=" + this.cols + ", " + "rows=" + this.rows + ", " + "tiles=" + this.tiles + "]";
      }

      private Stitched(BufferedImage var1, int var2, int var3, int var4, int var5) {
         this.image = var1;
         this.hash = var2;
         this.cols = var3;
         this.rows = var4;
         this.tiles = var5;
      }
   }
}

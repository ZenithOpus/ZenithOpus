package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.zenith.ChatLogger;
import org.zenith.PacketEvent;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.UpdateEvent;

public class ExchangeCommand extends CommandAbstract {
   private static final Pattern EXPECTED = Pattern.compile("Ожидается:\\s*([\\d ]+)");
   private static final Pattern GIVE = Pattern.compile("Вы отдадите:\\s*([\\d ]+)");
   private static final Pattern PURCHASED = Pattern.compile("успешно закупили .+? на бюджет\\s*([\\d\\s]+)", 66);
   private static final int CONFIRM_SLOT = 13;
   private static final int DECREASE_SLOT = 12;
   private static final int INCREASE_SLOT = 14;
   private static final int STUCK_LIMIT = 40;
   private static final int ADJUST_RETRY_INTERVAL = 5;
   private int phase;
   private int ticks;
   private int retryTicks;
   private int targetAmount = -1;
   private int remainingAmount = -1;
   private int lastGive = -1;
   private int lastClickSlot = -1;
   private int capBelowGoal = -1;
   private int stuckTicks;
   private int lastConfirmedGive = -1;
   private int lastReportedExpected = -1;
   private volatile boolean waitingPurchase;

   public ExchangeCommand() {
      super("exc");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2019("Использование: .exc <количество>");
         return 1;
      });
      var1.then(arg("amount", IntegerArgumentType.integer(1)).executes(var1x -> {
         int var2 = (Integer)var1x.getArgument("amount", Integer.class);
         this.stop();
         this.targetAmount = var2;
         this.remainingAmount = var2;
         this.send();
         return 1;
      }));
   }

   private void send() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.player != null && var1.player.currentScreenHandler != var1.player.playerScreenHandler) {
         var1.player.closeHandledScreen();
      }

      if (var1.getNetworkHandler() != null) {
         var1.getNetworkHandler().sendChatCommand("exchange");
      }

      this.phase = 1;
      this.ticks = 0;
      this.waitingPurchase = false;
      this.lastConfirmedGive = -1;
      this.lastReportedExpected = -1;
      this.resetAdjustState();
   }

   private void resetAdjustState() {
      this.lastGive = -1;
      this.lastClickSlot = -1;
      this.capBelowGoal = -1;
      this.stuckTicks = 0;
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.retryTicks > 0 && --this.retryTicks == 0) {
         this.send();
      }

      if (this.phase != 0) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2.player != null && var2.interactionManager != null) {
            ScreenHandler var3 = var2.player.currentScreenHandler;
            boolean var4 = var3 != null && var3 != var2.player.playerScreenHandler;
            if (this.phase == 1) {
               if (++this.ticks > 100) {
                  this.stop();
                  return;
               }

               if (!var4) {
                  return;
               }

               int var5 = -1;
               if (var3.isValid(2)) {
                  var5 = this.parseLore(var3.getSlot(2).getStack(), EXPECTED);
               }

               if (var5 >= 0 && var5 != this.lastReportedExpected) {
                  this.lastReportedExpected = var5;
                  ChatLogger.method2019("Ожидается: " + var5 + " коинов");
               }

               int var6 = this.goal();
               if (var6 >= 0 && var5 >= 0 && var6 < var5) {
                  var2.interactionManager.clickSlot(var3.syncId, 2, 1, SlotActionType.PICKUP, var2.player);
                  this.phase = 3;
                  this.ticks = 0;
                  this.resetAdjustState();
                  return;
               }

               this.phase = 2;
               this.ticks = 0;
            }

            if (this.phase == 2) {
               if (!var4) {
                  this.stop();
                  return;
               }

               if (this.isBuyScreen(var2)) {
                  this.phase = 3;
                  this.ticks = 0;
                  this.resetAdjustState();
                  return;
               }

               if (++this.ticks > 100) {
                  this.stop();
                  return;
               }

               if (this.ticks % 10 == 1) {
                  var2.interactionManager.clickSlot(var3.syncId, 2, 0, SlotActionType.PICKUP, var2.player);
               }
            }

            if (this.phase == 3) {
               if (++this.ticks > 200) {
                  this.stop();
               } else if (var4 && this.isBuyScreen(var2)) {
                  int var9 = -1;
                  if (var3.isValid(13)) {
                     var9 = this.parseLore(var3.getSlot(13).getStack(), GIVE);
                  }

                  if (var9 >= 0) {
                     int var10 = this.goal();
                     if (var9 == var10) {
                        this.confirmPurchase(var2, var3, var9);
                     } else {
                        boolean var7 = var9 != this.lastGive;
                        if (var7) {
                           if (!this.onGiveChanged(var9, var10)) {
                              return;
                           }
                        } else {
                           this.stuckTicks++;
                        }

                        if (var9 > var10) {
                           if (this.stuckTicks > 40) {
                              ChatLogger.method2021("Exchange: не удалось уменьшить количество до " + var10 + ", отмена");
                              this.stop();
                           } else {
                              if (this.shouldAdjust(var7)) {
                                 this.adjust(var2, var3, 12, var9);
                              }
                           }
                        } else if (var9 > 0 && this.capBelowGoal >= 0 && var9 <= var10 && var9 >= this.capBelowGoal) {
                           this.confirmPurchase(var2, var3, var9);
                        } else if (this.stuckTicks > 40) {
                           if (var9 > 0) {
                              this.confirmPurchase(var2, var3, var9);
                           } else {
                              this.stop();
                           }
                        } else {
                           if (this.shouldAdjust(var7)) {
                              this.adjust(var2, var3, 14, var9);
                           }
                        }
                     }
                  }
               }
            } else {
               if (this.phase == 4) {
                  this.ticks++;
                  boolean var8 = var4 && this.isBuyScreen(var2);
                  if (!var8 && this.ticks > 10) {
                     this.onPurchased(this.lastConfirmedGive);
                     return;
                  }

                  if (this.ticks > 200) {
                     ChatLogger.method2021("Exchange: нет подтверждения покупки, отмена");
                     this.stop();
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && this.waitingPurchase) {
         String var2 = this.packetText(var1.getPacket3());
         if (var2 != null) {
            Matcher var3 = PURCHASED.matcher(var2);
            if (var3.find()) {
               int var4;
               try {
                  var4 = Integer.parseInt(var3.group(1).replaceAll("\\s", ""));
               } catch (NumberFormatException var6) {
                  return;
               }

               MinecraftClient.getInstance().execute(() -> {
                  if (this.waitingPurchase) {
                     this.onPurchased(var4);
                  }
               });
            }
         }
      }
   }

   private boolean onGiveChanged(int var1, int var2) {
      if (this.lastGive >= 0) {
         if (this.lastClickSlot == 12 && var1 > this.lastGive) {
            ChatLogger.method2021("Exchange: кнопка уменьшения увеличивает количество, отмена");
            this.stop();
            return false;
         }

         if (this.lastClickSlot == 14 && var1 > var2 && this.lastGive <= var2) {
            this.capBelowGoal = this.lastGive;
         }
      }

      this.stuckTicks = 0;
      return true;
   }

   private boolean shouldAdjust(boolean var1) {
      return var1 || this.stuckTicks > 0 && this.stuckTicks % 5 == 0;
   }

   private void adjust(MinecraftClient var1, ScreenHandler var2, int var3, int var4) {
      var1.interactionManager.clickSlot(var2.syncId, var3, 0, SlotActionType.PICKUP, var1.player);
      this.lastClickSlot = var3;
      this.lastGive = var4;
   }

   private void confirmPurchase(MinecraftClient var1, ScreenHandler var2, int var3) {
      this.waitingPurchase = true;
      this.lastConfirmedGive = var3;
      this.phase = 4;
      this.ticks = 0;
      var1.interactionManager.clickSlot(var2.syncId, 13, 0, SlotActionType.PICKUP, var1.player);
   }

   private void onPurchased(int var1) {
      if (this.phase != 0 || this.waitingPurchase) {
         this.waitingPurchase = false;
         this.phase = 0;
         int var2 = this.lastConfirmedGive > 0 ? this.lastConfirmedGive : var1;
         this.lastConfirmedGive = -1;
         if (var2 <= 0) {
            ChatLogger.method2021("Exchange: не удалось определить количество покупки, отмена");
            this.stop();
         } else {
            this.remainingAmount -= var2;
            if (this.remainingAmount <= 0) {
               ChatLogger.method2019("Закупка завершена");
               this.stop();
            } else {
               ChatLogger.method2019("Осталось закупить: " + this.remainingAmount);
               this.retryTicks = 10;
            }
         }
      }
   }

   private String packetText(Packet<?> var1) {
      if (var1 instanceof GameMessageS2CPacket var3) {
         return var3.content().getString();
      } else {
         return var1 instanceof ProfilelessChatMessageS2CPacket var2 ? var2.message().getString() : null;
      }
   }

   private int goal() {
      return this.remainingAmount >= 0 ? this.remainingAmount : this.targetAmount;
   }

   private void stop() {
      this.phase = 0;
      this.ticks = 0;
      this.retryTicks = 0;
      this.targetAmount = -1;
      this.remainingAmount = -1;
      this.lastConfirmedGive = -1;
      this.lastReportedExpected = -1;
      this.waitingPurchase = false;
      this.resetAdjustState();
   }

   private boolean isBuyScreen(MinecraftClient var1) {
      return var1.currentScreen != null && var1.currentScreen.getTitle().getString().contains("Покупка");
   }

   private int parseLore(ItemStack var1, Pattern var2) {
      if (var1.isEmpty()) {
         return -1;
      } else {
         LoreComponent var3 = var1.get(DataComponentTypes.LORE);
         if (var3 == null) {
            return -1;
         } else {
            for (Text var5 : var3.lines()) {
               Matcher var6 = var2.matcher(var5.getString());
               if (var6.find()) {
                  try {
                     return Integer.parseInt(var6.group(1).replaceAll("\\s", ""));
                  } catch (NumberFormatException var8) {
                     return -1;
                  }
               }
            }

            return -1;
         }
      }
   }
}

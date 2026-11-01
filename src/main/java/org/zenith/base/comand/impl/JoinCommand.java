package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.zenith.ChatLogger;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.utility.network.InventoryUtils;

public class JoinCommand extends CommandAbstract {
   private static final long CLICK_DELAY_MS = 100L;
   private static final long REOPEN_DELAY_MS = 1000L;
   private static final long TRANSFER_WAIT_MS = 5000L;
   private static final long TIMEOUT_MS = 30000L;
   private final StopWatch clickTimer = new StopWatch();
   private final StopWatch reopenTimer = new StopWatch();
   private final StopWatch timeoutTimer = new StopWatch();
   private boolean active;
   private boolean targetClicked;
   private int grief = -1;

   public JoinCommand() {
      super("join");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2019("Используйте: .join <номер грифа> или .join stop");
         return 1;
      });
      var1.then(literal("stop").executes(var1x -> {
         if (!this.active) {
            ChatLogger.method2019("Joiner сейчас не запущен");
         } else {
            this.stop();
            ChatLogger.method2019("Joiner остановлен");
         }

         return 1;
      }));
      var1.then(arg("grief", IntegerArgumentType.integer(1)).executes(var1x -> {
         this.start((Integer)var1x.getArgument("grief", Integer.class));
         return 1;
      }));
   }

   private void start(int var1) {
      this.grief = var1;
      this.active = true;
      this.targetClicked = false;
      this.clickTimer.reset();
      this.reopenTimer.reset();
      this.timeoutTimer.reset();
      ChatLogger.method2019("Пробую зайти на гриф #" + var1);
      if (!this.useCompass()) {
         this.stop();
         ChatLogger.method2022("Компас не найден в хотбаре");
      }
   }

   private void stop() {
      this.active = false;
      this.targetClicked = false;
      this.grief = -1;
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (this.active && var1.method7100() && var1.getPacket3() instanceof GameJoinS2CPacket) {
         mc.execute(() -> {
            if (this.active) {
               if (this.targetClicked) {
                  ChatLogger.method2019("Зашёл на гриф #" + this.grief);
                  this.stop();
               } else {
                  this.reopenTimer.reset();
               }
            }
         });
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.active && mc.interactionManager != null) {
         if (this.timeoutTimer.method7546(30000L)) {
            int var4 = this.grief;
            this.stop();
            ChatLogger.method2022("Не удалось зайти на гриф #" + var4 + " за 30 секунд");
         } else if (mc.currentScreen == null) {
            if (this.reopenTimer.method7546(this.targetClicked ? 5000L : 1000L)) {
               this.reopenTimer.reset();
               this.targetClicked = false;
               this.useCompass();
            }
         } else {
            this.reopenTimer.reset();
            if (mc.currentScreen instanceof GenericContainerScreen var2 && this.clickTimer.method7546(100L)) {
               this.handleMenu(var2);
            }
         }
      }
   }

   private void handleMenu(GenericContainerScreen var1) {
      int var2 = -1;
      int var3 = -1;
      int var4 = -1;
      boolean var5 = false;
      String var6 = "гриф #" + this.grief;
      int var7 = var1.getScreenHandler().getInventory().size();

      for (Slot var9 : var1.getScreenHandler().slots) {
         if (var9.id >= var7) {
            break;
         }

         ItemStack var10 = var9.getStack();
         if (!var10.isEmpty()) {
            String var11 = normalize(var10.getName().getString());
            if (isTargetGrief(var11, var6)) {
               var2 = var9.id;
            }

            if (var11.contains("гриф #")) {
               var5 = true;
            }

            if (var11.contains("гриферское выживание") && !var11.contains("ru")) {
               var3 = var9.id;
            }

            if (var11.contains("следующ")) {
               var4 = var9.id;
            }
         }
      }

      if (var2 != -1) {
         this.click(var2);
         this.targetClicked = true;
      } else if (var3 != -1 && !var5 && ServerHelperModule.INSTANCE.method5181()) {
         this.click(var3);
      } else if (var5) {
         if (var4 != -1) {
            this.click(var4);
         } else {
            int var12 = this.grief;
            this.stop();
            mc.player.closeHandledScreen();
            ChatLogger.method2022("Гриф #" + var12 + " не найден");
         }
      }
   }

   private static boolean isTargetGrief(String var0, String var1) {
      for (int var2 = var0.indexOf(var1); var2 != -1; var2 = var0.indexOf(var1, var2 + 1)) {
         int var3 = var2 + var1.length();
         if (var3 >= var0.length() || !Character.isDigit(var0.charAt(var3))) {
            return true;
         }
      }

      return false;
   }

   private void click(int var1) {
      InventoryUtils.method904(var1, 0, SlotActionType.PICKUP, false);
      this.clickTimer.reset();
   }

   private boolean useCompass() {
      if (mc.player != null && mc.interactionManager != null && mc.getNetworkHandler() != null) {
         for (int var1 = 0; var1 < 9; var1++) {
            if (mc.player.getInventory().getStack(var1).isOf(Items.COMPASS)) {
               mc.player.getInventory().setSelectedSlot(var1);
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String normalize(String var0) {
      String var1 = Formatting.strip(var0);
      return (var1 == null ? var0 : var1).toLowerCase(Locale.ROOT);
   }
}

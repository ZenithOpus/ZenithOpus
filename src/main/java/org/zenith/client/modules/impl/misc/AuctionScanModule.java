package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.network.Names;

@ModuleInfo(
   name = "AuctionScan",
   category = ModuleCategory.MISC,
   description = "module.auctionscan.description"
)
public class AuctionScanModule extends Module {
   public static final AuctionScanModule INSTANCE = new AuctionScanModule();
   private final BooleanSetting openSelf = new BooleanSetting("module.auctionScan.openSelf", true);
   private final TextSetting command = new TextSetting("module.auctionScan.command", "ah", "ah");
   private final BooleanSetting categories = new BooleanSetting("module.auctionScan.categories", true);
   private final SliderSetting pageLimit = new SliderSetting("module.auctionScan.pageLimit", 60.0F, 5.0F, 300.0F, 5.0F, "module.auctionScan.pageLimit.desc", "");
   private final SliderSetting delay = new SliderSetting("module.auctionScan.delay", 60.0F, 20.0F, 1000.0F, 10.0F, "module.auctionScan.delay.desc", "ms");
   private static final String[] texts = new String[]{"следующая", "next", "вперёд", "вперед", "▶", "»"};
   private static final String[] texts2 = new String[]{"категори", "categor"};
   private static final String[] texts3 = new String[]{"назад", "back", "◀", "«"};
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final Set<String> items = new HashSet<>();
   private final Set<Integer> items2 = new LinkedHashSet<>();
   private final Set<Integer> items3 = new HashSet<>();
   private volatile boolean flag;
   private AuctionScanModule.Type type = AuctionScanModule.Type.IDLE;
   private int intValue;
   private int intValue2;
   private int intValue3 = -1;

   private AuctionScanModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.intValue = 0;
      this.intValue3 = -1;
      this.intValue2 = Names.size();
      this.stopWatch.reset();
      this.stopWatch2.reset();
      this.flag = true;
      this.type = this.openSelf.isEnabled() ? AuctionScanModule.Type.OPEN : AuctionScanModule.Type.PAGES;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.type = AuctionScanModule.Type.IDLE;
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && this.type != AuctionScanModule.Type.IDLE && this.type != AuctionScanModule.Type.DONE) {
         Packet var2 = var1.getPacket3();
         if (var2 instanceof InventoryS2CPacket || var2 instanceof ScreenHandlerSlotUpdateS2CPacket) {
            this.flag = true;
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      this.method1649();
   }

   @EventTarget
   public void method1648(DrawEvent var1) {
      this.method1649();
   }

   private void method1649() {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         if (this.stopWatch.method7546((long)this.delay.getValue3())) {
            if (this.type != AuctionScanModule.Type.PAGES || this.flag || this.stopWatch2.method7546(6000L)) {
               this.flag = false;
               switch (this.type) {
                  case OPEN:
                     this.method1650();
                     break;
                  case PAGES:
                     this.method1651();
                     break;
                  case CATEGORY_LIST:
                     this.method1654();
                     break;
                  case DONE:
                     this.method1655();
               }
            }
         }
      }
   }

   private void method1650() {
      String var1 = this.command.getValue().trim();
      if (var1.isEmpty()) {
         var1 = "ah";
      }

      mc.getNetworkHandler().sendChatCommand(var1);
      this.type = AuctionScanModule.Type.PAGES;
      this.stopWatch.reset();
      this.stopWatch2.reset();
   }

   private void method1651() {
      ScreenHandler var1 = mc.player.currentScreenHandler;
      if (!this.method1657(var1)) {
         if (this.stopWatch2.method7546(6000L)) {
            ChatLogger.method2022("AuctionScan: окно аукциона не открылось");
            this.method1655();
         }
      } else {
         this.stopWatch2.reset();
         String var2 = this.method1658(var1);
         boolean var3 = !this.items.add(var2);
         if (!var3) {
            this.intValue++;
         }

         if (this.categories.isEnabled() && this.intValue3 < 0 && this.items2.isEmpty()) {
            this.method1653(var1);
         }

         if (this.intValue >= (int)this.pageLimit.getValue3()) {
            this.method1652();
         } else {
            int var4 = this.method1659(var1, texts);
            if (var4 >= 0 && !var3) {
               this.method1656(var1, var4);
            } else {
               this.method1652();
            }
         }
      }
   }

   private void method1652() {
      if (this.categories.isEnabled() && !this.items2.isEmpty()) {
         this.type = AuctionScanModule.Type.CATEGORY_LIST;
         this.stopWatch.reset();
      } else {
         this.method1655();
      }
   }

   private void method1653(ScreenHandler var1) {
      int var2 = this.method1659(var1, texts2);
      if (var2 >= 0) {
         this.items2.add(var2);
      }
   }

   private void method1654() {
      ScreenHandler var1 = mc.player.currentScreenHandler;
      if (!this.method1657(var1)) {
         this.method1655();
      } else {
         Integer var2 = this.items2.stream().filter(var1x -> !this.items3.contains(var1x)).findFirst().orElse(null);
         if (var2 == null) {
            this.method1655();
         } else {
            this.items3.add(var2);
            this.items2.remove(var2);
            this.intValue3 = var2;
            this.items.clear();
            this.method1656(var1, var2);
            this.type = AuctionScanModule.Type.PAGES;
         }
      }
   }

   private void method1655() {
      int var1 = Names.size() - this.intValue2;
      ChatLogger.method2019("AuctionScan: страниц " + this.intValue + ", новых записей " + var1);
      Names.save();
      this.type = AuctionScanModule.Type.DONE;
      this.setToggled(false);
   }

   private void method1656(ScreenHandler var1, int var2) {
      mc.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, mc.player);
      this.stopWatch.reset();
      this.stopWatch2.reset();
   }

   private boolean method1657(ScreenHandler var1) {
      return var1 != null && var1.syncId != 0 && var1.slots.size() > 36;
   }

   private String method1658(ScreenHandler var1) {
      StringBuilder var2 = new StringBuilder();
      int var3 = var1.slots.size() - 36;

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = var1.getSlot(var4).getStack();
         if (!var5.isEmpty()) {
            var2.append(var4).append(var5.getItem()).append(var5.getName().getString()).append(';');
         }
      }

      return this.intValue3 + "#" + var2;
   }

   private int method1659(ScreenHandler var1, String[] var2) {
      int var3 = var1.slots.size() - 36;

      for (int var4 = 0; var4 < var3; var4++) {
         Slot var5 = var1.getSlot(var4);
         ItemStack var6 = var5.getStack();
         if (!var6.isEmpty()) {
            String var7 = var6.getName().getString().toLowerCase(Locale.ROOT);

            for (String var11 : var2) {
               if (var7.contains(var11)) {
                  return var4;
               }
            }
         }
      }

      return -1;
   }

   private static enum Type {
      IDLE,
      OPEN,
      PAGES,
      CATEGORY_LIST,
      DONE;
   }
}

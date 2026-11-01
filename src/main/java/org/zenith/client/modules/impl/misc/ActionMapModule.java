package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "ActionMap",
   category = ModuleCategory.MISC,
   description = "module.actionmap.description"
)
public class ActionMapModule extends Module {
   public static final ActionMapModule INSTANCE = new ActionMapModule();
   private final BooleanSetting hideNoise = new BooleanSetting("module.actionMap.hideNoise", true);
   private final BooleanSetting gapMark = new BooleanSetting("module.actionMap.gapMark", true);
   private PrintWriter printWriter;
   private long longValue;
   private long longValue2;
   private String text = "";
   private int intValue;
   private float floatValue;
   private float floatValue2;
   private long longValue3;
   private boolean flag;

   private ActionMapModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.longValue = System.currentTimeMillis();
      this.longValue2 = this.longValue;
      this.text = "";
      this.intValue = 0;
      this.method1366();
      this.method1367(0L, "=== ActionMap start — выполняй ОДНО действие, пауза 2с, следующее ===");
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method1361();
      ChatLogger.method2019("ActionMap: остановлен");
      this.method1368();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (!var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (var2 != null) {
            long var3 = System.currentTimeMillis();
            long var5 = var3 - this.longValue;
            if (var2 instanceof ClientTickEndC2SPacket && mc.player != null) {
               this.floatValue = mc.player.getYaw();
               this.floatValue2 = mc.player.getPitch();
               this.flag = true;
            }

            if (!this.hideNoise.isEnabled()
               || !(var2 instanceof ClientTickEndC2SPacket) && !(var2 instanceof CommonPongC2SPacket) && !(var2 instanceof KeepAliveC2SPacket)) {
               if (var2 instanceof PlayerMoveC2SPacket var7) {
                  String var8 = this.method1362(var7);
                  if (var8.equals(this.text)) {
                     this.intValue++;
                  } else {
                     this.method1361();
                     this.text = var8;
                     this.intValue = 1;
                  }
               } else {
                  this.method1361();
                  if (this.gapMark.isEnabled() && var3 - this.longValue2 > 900L) {
                     this.method1367(var5, "· · · пауза " + (var3 - this.longValue2) + "мс · · ·");
                  }

                  this.longValue2 = var3;
                  this.method1367(var5, this.method1363(var2));
               }
            }
         }
      }
   }

   private void method1361() {
      if (this.intValue > 0) {
         this.method1367(System.currentTimeMillis() - this.longValue, "MOVE:" + this.text + " ×" + this.intValue);
         this.intValue = 0;
         this.text = "";
      }
   }

   private String method1362(PlayerMoveC2SPacket var1) {
      boolean var2 = var1.changesPosition();
      boolean var3 = var1.changesLook();
      if (var2 && var3) {
         return "Full";
      } else if (var2) {
         return "PositionAndOnGround";
      } else {
         return var3 ? "LookAndOnGround" : "OnGroundOnly";
      }
   }

   private String method1363(Packet<?> var1) {
      if (var1 instanceof PlayerInteractEntityC2SPacket) {
         return this.method1364();
      } else if (var1 instanceof HandSwingC2SPacket) {
         return "HandSwing  (взмах рукой)";
      } else if (var1 instanceof PlayerInteractBlockC2SPacket var11) {
         return "PlayerInteractBlock  hand=" + var11.getHand() + " seq=" + var11.getSequence() + "  (ПКМ по блоку / постановка)";
      } else if (var1 instanceof PlayerInteractItemC2SPacket var10) {
         return "PlayerInteractItem  hand=" + var10.getHand() + " seq=" + var10.getSequence() + "  (ПКМ предметом / еда, зелье)";
      } else if (var1 instanceof ClickSlotC2SPacket var9) {
         return "ClickSlot  syncId=" + var9.syncId() + " slot=" + var9.slot() + " btn=" + var9.button() + " type=" + var9.actionType() + "  (клик в окне)";
      } else if (var1 instanceof CloseHandledScreenC2SPacket var8) {
         return "CloseHandledScreen  syncId=" + var8.getSyncId() + "  (закрытие окна)";
      } else if (var1 instanceof UpdateSelectedSlotC2SPacket var7) {
         return "UpdateSelectedSlot  slot=" + var7.getSelectedSlot() + "  (смена слота хотбара)";
      } else if (var1 instanceof ClientCommandC2SPacket var6) {
         return "ClientCommand  " + var6.getMode() + "  (спринт/шифт/респавн)";
      } else if (var1 instanceof CommandExecutionC2SPacket var5) {
         return "CommandExecution  /" + var5.command() + "  (ввод команды)";
      } else if (var1 instanceof ChatMessageC2SPacket var4) {
         return "ChatMessage  \"" + var4.chatMessage() + "\"  (сообщение в чат)";
      } else if (var1 instanceof PlayerActionC2SPacket var2) {
         String var3 = switch (var2.getAction()) {
            case START_DESTROY_BLOCK -> "начало ломки блока";
            case STOP_DESTROY_BLOCK -> "конец ломки блока";
            case ABORT_DESTROY_BLOCK -> "отмена ломки";
            case DROP_ITEM -> "выброс 1 предмета (Q)";
            case DROP_ALL_ITEMS -> "выброс стака (Ctrl+Q)";
            case RELEASE_USE_ITEM -> "отпуск использования (лук, еда)";
            case SWAP_ITEM_WITH_OFFHAND -> "перекладка в оффхенд (F)";
            default -> var2.getAction().name();
         };
         return "PlayerAction  " + var2.getAction() + "  (" + var3 + ")";
      } else {
         return var1.getClass().getSimpleName();
      }
   }

   private String method1364() {
      long var1 = System.currentTimeMillis();
      long var3 = this.longValue3 == 0L ? -1L : var1 - this.longValue3;
      this.longValue3 = var1;
      StringBuilder var5 = new StringBuilder("ATTACK  ");
      if (mc.player != null) {
         float var6 = mc.player.getYaw();
         float var7 = mc.player.getPitch();
         float var8 = this.flag ? this.method1365(var6 - this.floatValue) : 0.0F;
         float var9 = var7 - this.floatValue2;
         var5.append(String.format("yaw=%.1f pitch=%.1f  Δyaw=%.1f Δpitch=%.1f  ", var6, var7, var8, var9));
      }

      var5.append("interval=").append(var3).append("ms  ");
      boolean var10 = mc.targetedEntity != null;
      var5.append(var10 ? "цель=ПОД ПРИЦЕЛОМ" : "цель=НЕ под прицелом");
      return var5.toString();
   }

   private float method1365(float var1) {
      while (var1 > 180.0F) {
         var1 -= 360.0F;
      }

      while (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   private void method1366() {
      try {
         File var1 = new File(MinecraftClient.getInstance().runDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         File var2 = new File(var1, "actionmap-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(var2, true), true);
         ChatLogger.method2019("ActionMap: " + var2.getName());
      } catch (Exception var3) {
         this.printWriter = null;
      }
   }

   private void method1367(long var1, String var3) {
      if (this.printWriter != null) {
         this.printWriter.printf("%6d  %s%n", var1, var3);
      }
   }

   private void method1368() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }
}

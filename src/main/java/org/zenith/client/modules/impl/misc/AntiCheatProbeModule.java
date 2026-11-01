package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;

@ModuleInfo(
   name = "AntiCheatProbe",
   category = ModuleCategory.MISC,
   description = "module.anticheatprobe.description"
)
public class AntiCheatProbeModule extends Module {
   public static final AntiCheatProbeModule INSTANCE = new AntiCheatProbeModule();
   private static final long CONST_longValue = 80L;
   private PrintWriter printWriter;
   private long longValue;
   private long longValue2;
   private int intValue;
   private long longValue3;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private long longValue4;
   private long longValue5;
   private final List<Double> items = new ArrayList<>();
   private final List<Long> items2 = new ArrayList<>();
   private final List<AntiCheatProbeModule.Entry> items3 = new ArrayList<>();

   private AntiCheatProbeModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.longValue = System.currentTimeMillis();
      this.longValue2 = 0L;
      this.intValue = 0;
      this.longValue3 = 0L;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.longValue4 = 0L;
      this.longValue5 = 0L;
      this.items.clear();
      this.items3.clear();
      this.items2.clear();
      this.method6980();
      this.line("=== AntiCheatProbe start " + System.currentTimeMillis() + " ===");
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method6977();
      this.method6981();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      Packet var2 = var1.getPacket3();
      if (var2 != null) {
         long var3 = System.currentTimeMillis();
         long var5 = var3 - this.longValue;
         if (!var1.method7100()) {
            String var7 = this.method6975(var2);
            if (var7 != null) {
               this.items3.add(new AntiCheatProbeModule.Entry(var7, var3));
            }
         } else {
            if (var2 instanceof CommonPingS2CPacket
               || var2 instanceof KeepAliveS2CPacket
               || var2 instanceof PlayerPositionLookS2CPacket
               || var2 instanceof EntityStatusS2CPacket
               || var2 instanceof WorldTimeUpdateS2CPacket
               || var2 instanceof GameMessageS2CPacket) {
               mc.execute(() -> this.method6974(var2, var3, var5));
            }
         }
      }
   }

   private void method6974(Packet<?> var1, long var2, long var4) {
      if (this.isEnabled()) {
         if (var1 instanceof CommonPingS2CPacket var6) {
            long var11 = this.longValue2 == 0L ? 0L : var2 - this.longValue2;
            this.longValue2 = var2;
            this.intValue++;
            this.items2.add(var2);
            long var13 = var6.getParameter() & 4294967295L;
            if (this.longValue3 != 0L && Math.abs(var13 - this.longValue3) > 4L) {
               this.intValue2++;
            }

            this.longValue3 = var13;
            if (var11 < 20L || var11 > 85L) {
               this.line(String.format("%6d PING id=%d gap=%dms", var4, var13, var11));
            }
         } else if (var1 instanceof KeepAliveS2CPacket) {
            this.intValue4++;
            this.line(var4 + " KEEPALIVE");
         } else if (var1 instanceof PlayerPositionLookS2CPacket var7) {
            this.intValue3++;
            this.line(
               String.format(
                  "%6d CORRECTION id=%d pos=%.1f,%.1f,%.1f",
                  var4,
                  var7.teleportId(),
                  var7.change().position().x,
                  var7.change().position().y,
                  var7.change().position().z
               )
            );
         } else if (var1 instanceof EntityStatusS2CPacket var8 && mc.world != null && var8.getEntity(mc.world) == mc.player) {
            this.line(var4 + " SELF_STATUS code=" + var8.getStatus() + " (" + this.method6978(var8.getStatus()) + ")");
         } else if (var1 instanceof WorldTimeUpdateS2CPacket var9) {
            long var19 = var9.time();
            if (this.longValue4 != 0L) {
               long var21 = var2 - this.longValue4;
               long var15 = var19 - this.longValue5;
               if (var15 > 0L && var21 > 0L) {
                  double var17 = var15 / (var21 / 1000.0);
                  if (var17 > 0.0 && var17 < 40.0) {
                     this.items.add(var17);
                  }
               }
            }

            this.longValue4 = var2;
            this.longValue5 = var19;
         } else if (var1 instanceof GameMessageS2CPacket var10) {
            String var20 = var10.content().getString().replaceAll("§.", "").toLowerCase(Locale.ROOT);
            if (var20.contains("бан")
               || var20.contains("ban")
               || var20.contains("подозр")
               || var20.contains("чит")
               || var20.contains("kick")
               || var20.contains("флаг")) {
               this.line(var4 + " FLAGMSG| " + var10.content().getString().replaceAll("§.", "").trim());
            }
         }
      }
   }

   private String method6975(Packet<?> var1) {
      if (var1 instanceof PlayerInteractEntityC2SPacket) {
         return "ATTACK/USE_ENTITY";
      } else if (var1 instanceof HandSwingC2SPacket) {
         return "SWING";
      } else if (var1 instanceof PlayerInteractBlockC2SPacket) {
         return "PLACE/USE_BLOCK";
      } else if (var1 instanceof PlayerInteractItemC2SPacket) {
         return "USE_ITEM";
      } else if (var1 instanceof ClickSlotC2SPacket) {
         return "INV_CLICK";
      } else if (var1 instanceof CloseHandledScreenC2SPacket) {
         return "INV_CLOSE";
      } else if (var1 instanceof UpdateSelectedSlotC2SPacket) {
         return "HOTBAR";
      } else if (var1 instanceof PlayerActionC2SPacket var2) {
         return switch (var2.getAction()) {
            case START_DESTROY_BLOCK, STOP_DESTROY_BLOCK, ABORT_DESTROY_BLOCK -> "DIG";
            case DROP_ITEM, DROP_ALL_ITEMS -> "DROP";
            case SWAP_ITEM_WITH_OFFHAND -> "SWAP_HAND";
            case RELEASE_USE_ITEM -> "RELEASE_USE";
            default -> "ACTION";
         };
      } else {
         return null;
      }
   }

   private int method6976(long var1) {
      int var3 = Collections.binarySearch(this.items2, var1);
      if (var3 < 0) {
         var3 = -var3 - 1;
      }

      int var4 = 0;

      for (int var5 = var3; var5 < this.items2.size(); var5++) {
         long var6 = this.items2.get(var5);
         if (var6 > var1 + 80L) {
            break;
         }

         var4++;
      }

      return var4;
   }

   private void method6977() {
      long var1 = Math.max(1L, (System.currentTimeMillis() - this.longValue) / 1000L);
      this.line("=== summary (" + var1 + "s) ===");
      this.line("pings: " + this.intValue + " (" + this.method6979((double)this.intValue / var1) + "/s)");
      this.line("keepAlives: " + this.intValue4);
      this.line("corrections(rubber-band): " + this.intValue3);
      this.line("ping-id resets: " + this.intValue2);
      if (!this.items.isEmpty()) {
         double var3 = 0.0;
         double var5 = 99.0;
         double var7 = 0.0;

         for (double var10 : this.items) {
            var3 += var10;
            var5 = Math.min(var5, var10);
            var7 = Math.max(var7, var10);
         }

         this.line(
            "server TPS: avg "
               + this.method6979(var3 / this.items.size())
               + " min "
               + this.method6979(var5)
               + " max "
               + this.method6979(var7)
               + " (samples "
               + this.items.size()
               + ")"
         );
      }

      LinkedHashMap var12 = new LinkedHashMap();

      for (AntiCheatProbeModule.Entry var14 : this.items3) {
         int[] var6 = (int[])var12.computeIfAbsent(var14.text, var0 -> new int[2]);
         var6[0]++;
         var6[1] += this.method6976(var14.longValue);
      }

      this.line("--- бракетирование: пингов в 80мс после действия ---");

      for (Object var15_o : (Iterable<?>)(Object)(var12.entrySet())) {
         Map.Entry var15 = (Map.Entry)var15_o;
         int var16 = ((int[])var15.getValue())[0];
         double var17 = var16 == 0 ? 0.0 : (double)((int[])var15.getValue())[1] / var16;
         this.line(String.format(Locale.ROOT, "  %-18s n=%-4d pings=%.2f", var15.getKey(), var16, var17));
      }

      this.line("(фон ~1.3-1.6 пинга/80мс; заметно выше = действие тик-пинится)");
      ChatLogger.method2019("AntiCheatProbe: pings " + this.intValue + ", действий " + this.items3.size() + ", TPS замеров " + this.items.size());
   }

   private String method6978(int var1) {
      return switch (var1) {
         case 2, 33 -> "hurt";
         case 3 -> "death";
         case 9 -> "eat_done";
         case 29 -> "shield_disable";
         case 30 -> "shield_block";
         case 35 -> "totem";
         default -> "?";
      };
   }

   private String method6979(double var1) {
      return String.format(Locale.ROOT, "%.2f", var1);
   }

   private void method6980() {
      try {
         File var1 = new File(MinecraftClient.getInstance().runDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         File var2 = new File(var1, "anticheat-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(var2, true), true);
         ChatLogger.method2019("AntiCheatProbe: " + var2.getName());
      } catch (Exception var3) {
         this.printWriter = null;
      }
   }

   private void line(String var1) {
      if (this.printWriter != null) {
         this.printWriter.println(var1);
      }
   }

   private void method6981() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }

   private static final class Entry {
      public final String text;
      public final long longValue;

      public Entry(String var1, long var2) {
         this.text = var1;
         this.longValue = var2;
      }
   }
}

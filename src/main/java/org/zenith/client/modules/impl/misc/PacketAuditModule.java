package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "PacketAudit",
   category = ModuleCategory.MISC,
   description = "module.packetaudit.description"
)
public class PacketAuditModule extends Module {
   public static final PacketAuditModule INSTANCE = new PacketAuditModule();
   private final BooleanSetting census = new BooleanSetting("module.packetAudit.census", true);
   private final BooleanSetting details = new BooleanSetting("module.packetAudit.details", true);
   private final BooleanSetting raw = new BooleanSetting("module.packetAudit.raw", true);
   private final BooleanSetting outgoing = new BooleanSetting("module.packetAudit.outgoing", true);
   private final BooleanSetting snapshots = new BooleanSetting("module.packetAudit.snapshots", true);
   private static final int CONST_intValue = 40;
   private static final int CONST_intValue2 = 4;
   private static final int CONST_intValue3 = 400;
   private static final long CONST_longValue = 30000L;
   private static final Set<String> items = Set.of(
      "CommonPingS2CPacket", "KeepAliveS2CPacket", "WorldTimeUpdateS2CPacket", "ChunkDataS2CPacket", "LightUpdateS2CPacket", "ChunkDeltaUpdateS2CPacket"
   );
   private static final Pattern pattern = Pattern.compile("§.");
   private final Map<String, AtomicInteger> items2 = new ConcurrentHashMap<>();
   private final Map<String, Set<String>> items3 = new LinkedHashMap<>();
   private final Map<String, AtomicInteger> items4 = new ConcurrentHashMap<>();
   private final Map<String, AtomicInteger> items5 = new ConcurrentHashMap<>();
   private long lastSnapshot;
   private PrintWriter printWriter;
   private long longValue;

   private PacketAuditModule() {
   }

   @Override
   public void onEnable() {
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.items5.clear();
      super.onEnable();
      this.longValue = System.currentTimeMillis();
      this.method252();
      this.line("=== PacketAudit start ===");
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method251();
      this.method253();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      Packet var2 = var1.getPacket3();
      if (var2 != null) {
         if (!var1.method7100()) {
            if (this.outgoing.isEnabled()) {
               String var15 = var2.getClass().getSimpleName();
               this.items5.computeIfAbsent(var15, var0 -> new AtomicInteger()).incrementAndGet();
               this.method247("sent:" + var15, var2);
            }
         } else {
            this.method247(var2.getClass().getSimpleName(), var2);
            if (this.census.isEnabled()) {
               this.items2.computeIfAbsent(var2.getClass().getSimpleName(), var0 -> new AtomicInteger()).incrementAndGet();
            }

            if (this.details.isEnabled()) {
               if (var2 instanceof ScoreboardObjectiveUpdateS2CPacket var3) {
                  this.method249("scoreboard.objective", var3.getName() + " = " + this.method250(var3.getDisplayName()));
               } else if (var2 instanceof ScoreboardScoreUpdateS2CPacket var4) {
                  this.method249(
                     "scoreboard.score",
                     var4.objectiveName()
                        + " "
                        + var4.scoreHolderName()
                        + " = "
                        + var4.score()
                        + var4.display().map(var1x -> " [" + this.method250(var1x) + "]").orElse("")
                  );
               } else if (var2 instanceof TeamS2CPacket var5) {
                  this.method249("team", var5.getTeamName() + " " + var5.getTeamOperation() + " members=" + var5.getPlayerNames());
               } else if (var2 instanceof PlayerListHeaderS2CPacket var6) {
                  this.method249("tab.header", this.method250(var6.header()));
                  this.method249("tab.footer", this.method250(var6.footer()));
               } else if (var2 instanceof PlayerListS2CPacket var7) {
                  for (Entry var14 : var7.getEntries()) {
                     if (var14.displayName() != null) {
                        this.method249("tab.entry", this.method250(var14.displayName()) + " ping=" + var14.latency());
                     }
                  }
               } else if (var2 instanceof OpenScreenS2CPacket var8) {
                  this.method249("screen.open", this.method250(var8.getName()) + " type=" + var8.getScreenHandlerType());
               } else if (var2 instanceof TitleS2CPacket var9) {
                  this.method249("title", this.method250(var9.text()));
               } else if (var2 instanceof SubtitleS2CPacket var10) {
                  this.method249("subtitle", this.method250(var10.text()));
               } else if (var2 instanceof OverlayMessageS2CPacket var11) {
                  this.method249("actionbar", this.method250(var11.text()));
               } else if (var2 instanceof CustomPayloadS2CPacket var12) {
                  this.method249("payload", var12.payload().getId().id().toString());
               }
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.snapshots.isEnabled()) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastSnapshot >= 30000L) {
            this.lastSnapshot = var2;
            this.method246();
         }
      }
   }

   private void method246() {
      if (mc.getNetworkHandler() != null && mc.world != null) {
         this.line("--- snapshot ---");
         Collection<PlayerListEntry> var1 = mc.getNetworkHandler().getPlayerList();
         this.line("tab.size| " + var1.size());

         for (Object var3_o : (Iterable<?>)(Object)(var1)) {
            PlayerListEntry var3 = (PlayerListEntry)var3_o;
            Team var4 = var3.getScoreboardTeam();
            this.line(
               "tab| "
                  + var3.getProfile().name()
                  + " uuid="
                  + var3.getProfile().id()
                  + " ping="
                  + var3.getLatency()
                  + " mode="
                  + var3.getGameMode()
                  + " team="
                  + (var4 == null ? "-" : var4.getName())
                  + " display="
                  + this.method250(var3.getDisplayName())
            );
         }

         Scoreboard var7 = mc.world.getScoreboard();

         for (ScoreboardObjective var11 : var7.getObjectives()) {
            this.line("obj| " + var11.getName() + " crit=" + var11.getCriterion().getName() + " display=" + this.method250(var11.getDisplayName()));

            for (ScoreboardEntry var6 : var7.getScoreboardEntries(var11)) {
               this.line("score| " + var11.getName() + " " + var6.owner() + " = " + var6.value());
            }
         }

         for (Team var12 : var7.getTeams()) {
            this.line(
               "team.full| "
                  + var12.getName()
                  + " prefix="
                  + this.method250(var12.getPrefix())
                  + " suffix="
                  + this.method250(var12.getSuffix())
                  + " color="
                  + var12.getColor()
                  + " members="
                  + var12.getPlayerList()
            );
         }

         LinkedHashMap var10 = new LinkedHashMap();

         for (Entity var14 : mc.world.getEntities()) {
            var10.merge(var14.getType().toString(), 1, (a, b) -> (Integer)a + (Integer)b);
         }

         this.line("entities| " + var10);
      }
   }

   private void method247(String var1, Packet<?> var2) {
      if (this.raw.isEnabled()) {
         if (!items.contains(var1.replace("sent:", ""))) {
            AtomicInteger var3 = this.items4.computeIfAbsent(var1, var0 -> new AtomicInteger());
            if (var3.incrementAndGet() <= 4) {
               this.line("raw| " + var1 + " " + this.method248(var2));
            }
         }
      }
   }

   private String method248(Object var1) {
      StringBuilder var2 = new StringBuilder();

      for (Class var3 = var1.getClass(); var3 != null && var3 != Object.class; var3 = var3.getSuperclass()) {
         for (Field var7 : var3.getDeclaredFields()) {
            if (!Modifier.isStatic(var7.getModifiers())) {
               try {
                  var7.setAccessible(true);
                  Object var8 = var7.get(var1);
                  String var9 = var8 instanceof Text var10 ? this.method250(var10) : String.valueOf(var8);
                  if (var9.length() > 400) {
                     var9 = var9.substring(0, 400) + "...";
                  }

                  var2.append(var7.getName()).append('=').append(var9).append("; ");
               } catch (Throwable var11) {
               }
            }
         }
      }

      return var2.toString();
   }

   private void method249(String var1, String var2) {
      if (var2 != null && !var2.isBlank()) {
         var var3 = this.items3.computeIfAbsent(var1, var0 -> new HashSet<>());
         if (var3.size() < 40 && var3.add(var2)) {
            this.line(var1 + "| " + var2);
         }
      }
   }

   private String method250(Text var1) {
      return var1 == null ? "" : pattern.matcher(var1.getString()).replaceAll("").trim();
   }

   private void method251() {
      long var1 = Math.max(1L, (System.currentTimeMillis() - this.longValue) / 1000L);
      this.line("=== PacketAudit report (" + var1 + " c) ===");
      ArrayList<java.util.Map.Entry> var3 = new ArrayList<>(this.items2.entrySet());
      var3.sort((var0, var1x) -> Integer.compare(((AtomicInteger)var1x.getValue()).get(), ((AtomicInteger)var0.getValue()).get()));

      for (Object var5_o : (Iterable<?>)(Object)(var3)) {
         java.util.Map.Entry var5 = (java.util.Map.Entry)var5_o;
         this.line(
            String.format("%-46s %6d  %.1f/с", var5.getKey(), ((AtomicInteger)var5.getValue()).get(), (double)((AtomicInteger)var5.getValue()).get() / var1)
         );
      }

      if (!this.items5.isEmpty()) {
         this.line("--- исходящие ---");
         ArrayList<java.util.Map.Entry> var7 = new ArrayList<>(this.items5.entrySet());
         var7.sort((var0, var1x) -> Integer.compare(((AtomicInteger)var1x.getValue()).get(), ((AtomicInteger)var0.getValue()).get()));

         for (Object var6_o : (Iterable<?>)(Object)(var7)) {
            java.util.Map.Entry var6 = (java.util.Map.Entry)var6_o;
            this.line(
               String.format("%-46s %6d  %.1f/s", var6.getKey(), ((AtomicInteger)var6.getValue()).get(), (double)((AtomicInteger)var6.getValue()).get() / var1)
            );
         }
      }

      this.method246();
      ChatLogger.method2019("PacketAudit: типов " + this.items2.size() + ", отчёт записан");
   }

   private void method252() {
      try {
         File var1 = new File(MinecraftClient.getInstance().runDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         File var2 = new File(var1, "packetaudit-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(var2, true), true);
         ChatLogger.method2019("PacketAudit: " + var2.getName());
      } catch (Exception var3) {
         this.printWriter = null;
      }
   }

   private void line(String var1) {
      if (this.printWriter != null) {
         this.printWriter.println(var1);
      }
   }

   private void method253() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }
}

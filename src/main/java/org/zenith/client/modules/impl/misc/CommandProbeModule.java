package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket.Suggestion;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "CommandProbe",
   category = ModuleCategory.MISC,
   description = "module.commandprobe.description"
)
public class CommandProbeModule extends Module {
   public static final CommandProbeModule INSTANCE = new CommandProbeModule();
   private final BooleanSetting tree = new BooleanSetting("module.commandProbe.tree", true);
   private final BooleanSetting completions = new BooleanSetting("module.commandProbe.completions", true);
   private final BooleanSetting namespaces = new BooleanSetting("module.commandProbe.namespaces", true);
   private final SliderSetting depth = new SliderSetting("module.commandProbe.depth", 2.0F, 1.0F, 3.0F, 1.0F, "module.commandProbe.depth.desc", "");
   private final SliderSetting delay = new SliderSetting("module.commandProbe.delay", 120.0F, 50.0F, 1000.0F, 10.0F, "module.commandProbe.delay.desc", "ms");
   private static final String[] texts = new String[]{
      "minecraft",
      "bukkit",
      "paper",
      "spigot",
      "purpur",
      "velocity",
      "bungee",
      "waterfall",
      "essentials",
      "essentialsx",
      "cmi",
      "luckperms",
      "lp",
      "vault",
      "placeholderapi",
      "papi",
      "worldedit",
      "fawe",
      "worldguard",
      "multiverse",
      "mv",
      "coreprotect",
      "co",
      "plan",
      "viaversion",
      "via",
      "viabackwards",
      "viarewind",
      "protocollib",
      "packetevents",
      "holographicdisplays",
      "decentholograms",
      "holograms",
      "citizens",
      "mythicmobs",
      "litebans",
      "advancedban",
      "banmanager",
      "maintenance",
      "authme",
      "nlogin",
      "libreLogin",
      "spark",
      "chunky",
      "clearlag",
      "farmlimiter",
      "epicanchors",
      "wildstacker",
      "shopgui",
      "shopguiplus",
      "quickshop",
      "chestshop",
      "playervaults",
      "enderchestplus",
      "jobs",
      "mcmmo",
      "aurelium",
      "auraskills",
      "itemsadder",
      "oraxen",
      "nexo",
      "headdatabase",
      "betterrtp",
      "huskhomes",
      "residence",
      "griefprevention",
      "lands",
      "towny",
      "factions",
      "clans",
      "simpleclans",
      "discordsrv",
      "votifier",
      "crazycrates",
      "excellentcrates",
      "tab",
      "featherboard",
      "animatedscoreboard",
      "skript",
      "denizen",
      "plugman",
      "nocheatplus",
      "matrix",
      "vulcan",
      "spartan",
      "themis",
      "negativity",
      "grim",
      "aac",
      "anticheat",
      "polar",
      "kauri",
      "horizon",
      "abyss",
      "sentinel",
      "guardian",
      "holyworld",
      "hw",
      "holy",
      "core",
      "hcore",
      "anarchy",
      "auction",
      "ah",
      "donate"
   };
   private final Deque<String> deque = new ArrayDeque<>();
   private final Set<String> items = new HashSet<>();
   private final List<String> items2 = new ArrayList<>();
   private PrintWriter printWriter;
   private String text;
   private int intValue = 9000;
   private long longValue;
   private int intValue2;

   private CommandProbeModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.deque.clear();
      this.items.clear();
      this.intValue2 = 0;
      this.text = null;
      this.method6370();
      if (this.tree.isEnabled()) {
         this.method6364();
      }

      if (this.completions.isEnabled()) {
         this.method6367();
      }

      if (this.namespaces.isEnabled()) {
         this.method6368();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.line("=== completions answered: " + this.intValue2 + " ===");
      ChatLogger.method2019("CommandProbe: ответов " + this.intValue2);
      this.method6371();
   }

   private void method6364() {
      if (mc.getNetworkHandler() != null) {
         RootCommandNode var1 = mc.getNetworkHandler().getCommandDispatcher().getRoot();
         this.line("=== command tree ===");

         for (Object var3_o : (Iterable<?>)(Object)(var1.getChildren())) {
            CommandNode var3 = (CommandNode)var3_o;
            this.method6365(var3, "", 0);
         }
      }
   }

   private void method6365(CommandNode<ClientCommandSource> var1, String var2, int var3) {
      if (var3 <= 4) {
         String var4 = this.method6366(var1);
         this.line("  ".repeat(var3) + var2 + var4);

         for (CommandNode var6 : var1.getChildren()) {
            this.method6365(var6, "", var3 + 1);
         }

         if (var1.getRedirect() != null) {
            this.line("  ".repeat(var3 + 1) + "-> redirect " + var1.getRedirect().getName());
         }
      }
   }

   private String method6366(CommandNode<ClientCommandSource> var1) {
      if (var1 instanceof LiteralCommandNode var4) {
         return var4.getLiteral() + (var1.getCommand() != null ? " *" : "");
      } else if (var1 instanceof ArgumentCommandNode var2) {
         String var3 = var2.getCustomSuggestions() == null ? "" : " suggest=" + var2.getCustomSuggestions().getClass().getSimpleName();
         return "<" + var2.getName() + ":" + var2.getType().getClass().getSimpleName() + ">" + var3 + (var1.getCommand() != null ? " *" : "");
      } else {
         return var1.getName();
      }
   }

   private void method6367() {
      if (mc.getNetworkHandler() != null) {
         for (CommandNode var2 : mc.getNetworkHandler().getCommandDispatcher().getRoot().getChildren()) {
            if (var2 instanceof LiteralCommandNode var3) {
               this.deque.add(var3.getLiteral() + " ");
            }
         }

         this.line("=== completions: в очереди " + this.deque.size() + " ===");
      }
   }

   private void method6368() {
      for (String var4 : texts) {
         this.deque.add(var4 + ":");
      }

      this.line("=== namespace probe: " + texts.length + " ===");
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.getNetworkHandler() != null) {
         if (this.text == null || System.currentTimeMillis() - this.longValue >= 1500L) {
            if (System.currentTimeMillis() - this.longValue >= (long)this.delay.getValue3()) {
               String var2 = this.deque.poll();
               if (var2 == null) {
                  if (this.text == null) {
                     this.setToggled(false);
                  }
               } else if (this.items.add(var2)) {
                  this.text = var2;
                  this.longValue = System.currentTimeMillis();
                  mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(++this.intValue, "/" + var2));
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         if (var1.getPacket3() instanceof CommandSuggestionsS2CPacket var3) {
            mc.execute(() -> this.method6369(var3));
         }
      }
   }

   private void method6369(CommandSuggestionsS2CPacket var1) {
      if (this.isEnabled()) {
         String var2 = this.text;
         this.text = null;
         if (var2 != null) {
            ArrayList<String> var3 = new ArrayList();

            for (Suggestion var5 : var1.suggestions()) {
               var3.add(var5.text());
            }

            if (!var3.isEmpty()) {
               this.intValue2++;
               this.line("suggest| /" + var2.trim() + " -> " + String.join(", ", var3));
               int var8 = var2.trim().split("\\s+").length;
               if (var8 < (int)this.depth.getValue3()) {
                  int var9 = 0;

                  for (Object var7_o : (Iterable<?>)(Object)(var3)) {
                     String var7 = (String)var7_o;
                     if (!var7.startsWith("@") && var7.length() <= 24) {
                        if (var9++ >= 12) {
                           break;
                        }

                        this.deque.add(var2 + var7 + " ");
                     }
                  }
               }
            }
         }
      }
   }

   private void method6370() {
      try {
         File var1 = new File(MinecraftClient.getInstance().runDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         File var2 = new File(var1, "commandprobe-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(var2, true), true);
         ChatLogger.method2019("CommandProbe: " + var2.getName());
      } catch (Exception var3) {
         this.printWriter = null;
      }
   }

   private void line(String var1) {
      if (this.printWriter != null) {
         this.printWriter.println(var1);
      }
   }

   private void method6371() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }
}

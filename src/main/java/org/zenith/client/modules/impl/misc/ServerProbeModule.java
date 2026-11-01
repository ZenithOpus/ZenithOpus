package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "ServerProbe",
   category = ModuleCategory.MISC,
   description = "module.serverprobe.description"
)
public class ServerProbeModule extends Module {
   public static final ServerProbeModule INSTANCE = new ServerProbeModule();
   private final BooleanSetting commands = new BooleanSetting("module.serverProbe.commands", true);
   private final BooleanSetting tracking = new BooleanSetting("module.serverProbe.tracking", true);
   private final BooleanSetting teleports = new BooleanSetting("module.serverProbe.teleports", true);
   private final BooleanSetting sounds = new BooleanSetting("module.serverProbe.sounds", true);
   private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
   private final Map<String, ServerProbeModule.Entry> items = new HashMap<>();
   private final Map<String, int[]> items2 = new ConcurrentSkipListMap<>();
   private final Set<String> items3 = new TreeSet<>();
   private PrintWriter printWriter;
   private boolean flag;
   private double doubleValue;
   private double doubleValue2 = Double.MAX_VALUE;
   private double doubleValue3;
   private double doubleValue4;

   private ServerProbeModule() {
   }

   @Override
   public void onEnable() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.doubleValue = 0.0;
      this.doubleValue2 = Double.MAX_VALUE;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.flag = true;
      super.onEnable();
      this.method6966();
      this.line("=== ServerProbe start " + simpleDateFormat.format(new Date()) + " ===");
      this.line("server: " + this.method6965());
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method6963();
      this.method6967();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (var2 instanceof CommandTreeS2CPacket && this.commands.isEnabled()) {
            this.flag = true;
         } else if (var2 instanceof CustomPayloadS2CPacket var12) {
            if (var12.payload() instanceof BrandCustomPayload var15) {
               this.line("brand: " + var15.brand());
            } else {
               this.line("payload: " + var12.payload().getId().id());
            }
         } else if (var2 instanceof EntityPositionS2CPacket var3 && this.teleports.isEnabled()) {
            this.method6961(var3);
         } else if (var2 instanceof EntitiesDestroyS2CPacket var9 && this.tracking.isEnabled()) {
            IntListIterator var14 = var9.getEntityIds().iterator();

            while (var14.hasNext()) {
               int var16 = (Integer)var14.next();
               this.method6960(var16);
            }
         } else if (var2 instanceof PlaySoundS2CPacket var10 && this.sounds.isEnabled()) {
            Vec3d var13 = new Vec3d(var10.getX(), var10.getY(), var10.getZ());
            double var5 = this.method6964(var13);
            if (!(var5 < 0.0)) {
               this.doubleValue3 = Math.max(this.doubleValue3, var5);
               String var7 = var10.getSound().getKey().map(var0 -> var0.getValue().toString()).orElse("unknown");
               int[] var8 = this.items2.computeIfAbsent(var7, var0 -> new int[]{0, 0});
               var8[0]++;
               var8[1] = Math.max(var8[1], (int)var5);
            }
         } else {
            if (var2 instanceof ParticleS2CPacket var11 && this.sounds.isEnabled()) {
               double var4 = this.method6964(new Vec3d(var11.getX(), var11.getY(), var11.getZ()));
               if (var4 > 0.0) {
                  this.doubleValue4 = Math.max(this.doubleValue4, var4);
               }
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.flag) {
         this.flag = false;
         this.method6962();
      }

      if (this.tracking.isEnabled() && mc.world != null && mc.player != null) {
         for (PlayerEntity var3 : mc.world.getPlayers()) {
            if (var3 != mc.player) {
               String var4 = var3.getGameProfile().name();
               ServerProbeModule.Entry var5 = this.items.computeIfAbsent(var4, var2 -> {
                  ServerProbeModule.Entry var3x = new ServerProbeModule.Entry();
                  double var4x = mc.player.distanceTo(var3);
                  this.doubleValue = Math.max(this.doubleValue, var4x);
                  this.line(String.format(Locale.ROOT, "spawn %s at %.1f блоков", var2, var4x));
                  return var3x;
               });
               var5.vec3d = var3.getEntityPos();
               var5.longValue = System.currentTimeMillis();
            }
         }
      }
   }

   private void method6960(int var1) {
      if (mc.world != null && mc.player != null) {
         if (mc.world.getEntityById(var1) instanceof PlayerEntity var3 && var3 != mc.player) {
            double var4 = mc.player.distanceTo(var3);
            this.doubleValue2 = Math.min(this.doubleValue2, var4);
            this.line(
               String.format(
                  Locale.ROOT,
                  "despawn %s at %.1f блоков, последняя точка %.0f %.0f %.0f",
                  var3.getGameProfile().name(),
                  var4,
                  var3.getX(),
                  var3.getY(),
                  var3.getZ()
               )
            );
         }
      }
   }

   private void method6961(EntityPositionS2CPacket var1) {
      if (mc.world != null && mc.player != null) {
         Entity var2 = mc.world.getEntityById(var1.entityId());
         if (var2 instanceof PlayerEntity var3) {
            Vec3d var4 = var1.change().position();
            double var5 = var2.getEntityPos().distanceTo(var4);
            if (!(var5 < 8.0)) {
               this.line(
                  String.format(
                     Locale.ROOT,
                     "teleport %s %.0f %.0f %.0f -> %.0f %.0f %.0f (%.0f блоков)",
                     var3.getGameProfile().name(),
                     var2.getX(),
                     var2.getY(),
                     var2.getZ(),
                     var4.x,
                     var4.y,
                     var4.z,
                     var5
                  )
               );
            }
         }
      }
   }

   private void method6962() {
      if (mc.getNetworkHandler() != null) {
         RootCommandNode var1 = mc.getNetworkHandler().getCommandDispatcher().getRoot();
         ArrayList var2 = new ArrayList();

         for (Object var4_o : (Iterable<?>)(Object)(var1.getChildren())) {
            CommandNode var4 = (CommandNode)var4_o;
            String var5 = var4.getName();
            var2.add(var5);
            int var6 = var5.indexOf(58);
            if (var6 > 0) {
               this.items3.add(var5.substring(0, var6));
            }
         }

         Collections.sort(var2);
         this.line("commands (" + var2.size() + "): " + String.join(", ", var2));
         this.line("namespaces (" + this.items3.size() + "): " + String.join(", ", this.items3));
      }
   }

   private void method6963() {
      this.line("=== ServerProbe report ===");
      this.line(String.format(Locale.ROOT, "радиус появления игроков: до %.1f блоков", this.doubleValue));
      this.line(String.format(Locale.ROOT, "ближайшее исчезновение: %.1f блоков", this.doubleValue2 == Double.MAX_VALUE ? -1.0 : this.doubleValue2));
      this.line(String.format(Locale.ROOT, "звук слышен до %.1f блоков, частицы до %.1f", this.doubleValue3, this.doubleValue4));
      this.line("плагинные namespace: " + (this.items3.isEmpty() ? "нет" : String.join(", ", this.items3)));
      this.line("звуки по дальности:");
      var var1 = new ArrayList<>(this.items2.entrySet());
      var1.sort((var0, var1x) -> Integer.compare(((int[])var1x.getValue())[1], ((int[])var0.getValue())[1]));

      for (int var2 = 0; var2 < Math.min(25, var1.size()); var2++) {
         Map.Entry var3 = (Map.Entry)var1.get(var2);
         this.line("  " + (String)var3.getKey() + " x" + ((int[])var3.getValue())[0] + " до " + ((int[])var3.getValue())[1] + " блоков");
      }

      ChatLogger.method2019("ServerProbe: отчёт записан, namespace " + this.items3.size());
   }

   private double method6964(Vec3d var1) {
      return mc.player == null ? -1.0 : mc.player.getEntityPos().distanceTo(var1);
   }

   private String method6965() {
      return mc.getCurrentServerEntry() == null ? "singleplayer" : mc.getCurrentServerEntry().address;
   }

   private void method6966() {
      try {
         File var1 = new File(MinecraftClient.getInstance().runDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         File var2 = new File(var1, "serverprobe-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(var2, true), true);
         ChatLogger.method2019("ServerProbe: лог " + var2.getAbsolutePath());
      } catch (Exception var3) {
         this.printWriter = null;
      }
   }

   private void line(String var1) {
      System.out.println("[ServerProbe] " + var1);
      if (this.printWriter != null) {
         this.printWriter.println(var1);
      }
   }

   private void method6967() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }

   private static final class Entry {
      public Vec3d vec3d;
      public long longValue;
   }
}

package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

public final class ChatBridge implements MinecraftAccess {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
   private static final Pattern pattern = Pattern.compile("§.");
   private static final long CONST_longValue = 200L;
   private static final long CONST_longValue2 = 200L;
   private static final long CONST_longValue3 = 33554432L;
   private final File file;
   private final File file2;
   private final File file3;
   private PrintWriter printWriter;
   private int intValue;
   private long lastPoll;
   private long longValue;
   private boolean dirty;
   private long longValue2;
   private long longValue3;

   public ChatBridge() {
      this.file = new File(Zenith.file, "bridge");
      this.file2 = new File(this.file, "in.txt");
      this.file3 = new File(this.file, "out.log");
      this.longValue2 = -1L;
      this.longValue3 = -1L;
      if (!this.file.exists()) {
         this.file.mkdirs();
      }

      this.method509();
      this.intValue = this.method498();
      this.method508();
      EventManager.register(this);
      this.write("bridge ready");
      this.flush();
      Runtime.getRuntime().addShutdownHook(new Thread(this::flush, "zenith-bridge-flush"));
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      long var2 = System.currentTimeMillis();
      if (var2 - this.lastPoll >= 200L) {
         this.lastPoll = var2;
         this.method499();
         this.flush();
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         Packet var2 = var1.getPacket3();
         Text var3 = null;
         if (var2 instanceof GameMessageS2CPacket var4) {
            var3 = var4.content();
         } else if (var2 instanceof ProfilelessChatMessageS2CPacket var5) {
            var3 = var5.message();
         } else if (var2 instanceof ChatMessageS2CPacket var6) {
            var3 = var6.unsignedContent();
         }

         if (var3 != null) {
            String var7 = pattern.matcher(var3.getString()).replaceAll("").trim();
            if (!var7.isEmpty()) {
               this.write("chat| " + var7);
            }
         }
      }
   }

   private int method498() {
      if (!this.file2.exists()) {
         return 0;
      } else {
         try {
            return Files.readAllLines(this.file2.toPath(), StandardCharsets.UTF_8).size();
         } catch (Exception var2) {
            return 0;
         }
      }
   }

   private void method499() {
      if (this.file2.exists()) {
         long var1 = this.file2.length();
         long var3 = this.file2.lastModified();
         if (var1 != this.longValue2 || var3 != this.longValue3) {
            List var5;
            try {
               var5 = Files.readAllLines(this.file2.toPath(), StandardCharsets.UTF_8);
            } catch (Exception var10) {
               return;
            }

            this.longValue2 = var1;
            this.longValue3 = var3;
            if (var5.size() <= this.intValue) {
               if (var5.size() < this.intValue) {
                  this.intValue = 0;
                  this.longValue2 = -1L;
               }
            } else {
               for (int var6 = this.intValue; var6 < var5.size(); var6++) {
                  String var7 = ((String)var5.get(var6)).trim();
                  this.intValue = var6 + 1;
                  if (!var7.isEmpty() && !var7.startsWith("#")) {
                     try {
                        this.method500(var7);
                     } catch (Exception var9) {
                        this.write("error| " + var7 + " -> " + var9);
                     }
                  }
               }
            }
         }
      }
   }

   private void method500(String var1) {
      String[] var2 = var1.split("\\s+", 2);
      String var3 = var2[0].toLowerCase(Locale.ROOT);
      String var4 = var2.length > 1 ? var2[1] : "";
      this.write("> " + var1);
      switch (var3) {
         case "cmd":
            if (mc.getNetworkHandler() == null) {
               return;
            }

            mc.getNetworkHandler().sendChatCommand(var4.startsWith("/") ? var4.substring(1) : var4);
            break;
         case "chat":
            if (mc.getNetworkHandler() == null) {
               return;
            }

            mc.getNetworkHandler().sendChatMessage(var4);
            break;
         case "module":
            this.method501(var4);
            break;
         case "pos":
            this.method502();
            break;
         case "players":
            this.method503();
            break;
         case "screen":
            this.method504();
            break;
         case "click":
            this.method506(var4);
            break;
         case "close":
            if (mc.player != null) {
               mc.player.closeHandledScreen();
            }
            break;
         case "names":
            this.write("names| " + Names.size());
            break;
         case "target":
            this.method505();
            break;
         default:
            this.write("unknown| " + var3);
      }
   }

   private void method501(String var1) {
      String[] var2 = var1.split("\\s+");
      if (var2.length != 0 && !var2[0].isEmpty()) {
         Module var3 = Zenith.getInstance().getModuleManager().method2484(var2[0]);
         if (var3 == null) {
            this.write("module| не найден " + var2[0]);
         } else {
            boolean var4 = var2.length < 2 ? !var3.isEnabled() : var2[1].equalsIgnoreCase("on");
            var3.setToggled(var4);
            this.write("module| " + var3.getName() + " = " + var3.isEnabled());
         }
      }
   }

   private void method502() {
      if (mc.player != null && mc.world != null) {
         this.write(
            String.format(
               Locale.ROOT,
               "pos| %.1f %.1f %.1f dim=%s server=%s",
               mc.player.getX(),
               mc.player.getY(),
               mc.player.getZ(),
               mc.world.getRegistryKey().getValue(),
               mc.getCurrentServerEntry() == null ? "-" : mc.getCurrentServerEntry().address
            )
         );
      } else {
         this.write("pos| нет мира");
      }
   }

   private void method503() {
      if (mc.world != null && mc.player != null) {
         for (PlayerEntity var2 : mc.world.getPlayers()) {
            if (var2 != mc.player) {
               this.write(
                  String.format(
                     Locale.ROOT,
                     "player| %s %.0f %.0f %.0f dist=%.1f hand=%s",
                     var2.getGameProfile().name(),
                     var2.getX(),
                     var2.getY(),
                     var2.getZ(),
                     mc.player.distanceTo(var2),
                     this.method507(var2.getMainHandStack())
                  )
               );
            }
         }
      } else {
         this.write("players| нет мира");
      }
   }

   private void method504() {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         ScreenHandler var1 = mc.player.currentScreenHandler;
         this.write("screen| syncId=" + var1.syncId + " slots=" + var1.slots.size());
         int var2 = Math.max(0, var1.slots.size() - 36);

         for (int var3 = 0; var3 < var2; var3++) {
            ItemStack var4 = var1.getSlot(var3).getStack();
            if (!var4.isEmpty()) {
               this.write("slot| " + var3 + " " + this.method507(var4));
            }
         }
      } else {
         this.write("screen| нет окна");
      }
   }

   private void method505() {
      if (mc.targetedEntity instanceof PlayerEntity var1) {
         this.write(
            "target| " + var1.getGameProfile().name() + " main=" + this.method507(var1.getMainHandStack()) + " off=" + this.method507(var1.getOffHandStack())
         );
      } else {
         this.write("target| нет цели");
      }
   }

   private void method506(String var1) {
      if (mc.player != null && mc.interactionManager != null && mc.player.currentScreenHandler != null) {
         String[] var2 = var1.split("\\s+");
         int var3 = Integer.parseInt(var2[0]);
         int var4 = var2.length > 1 ? Integer.parseInt(var2[1]) : 0;
         mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, var3, var4, SlotActionType.PICKUP, mc.player);
         this.write("click| " + var3 + " button=" + var4);
      }
   }

   private String method507(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         StringBuilder var2 = new StringBuilder(var1.getItem().toString());
         var2.append(" x").append(var1.getCount());
         String var3 = pattern.matcher(var1.getName().getString()).replaceAll("");
         var2.append(" \"").append(var3).append('"');
         var var4 = Names.method4519(var1);
         if (!var4.isEmpty()) {
            var2.append(" ench=").append(String.join(",", var4));
         }

         LoreComponent var5 = var1.get(DataComponentTypes.LORE);
         if (var5 != null && !var5.lines().isEmpty()) {
            var2.append(" lore=").append(var5.lines().size());
         }

         return var2.toString();
      } else {
         return "-";
      }
   }

   private synchronized void write(String var1) {
      if (this.printWriter == null) {
         this.method508();
      }

      if (this.printWriter != null) {
         this.printWriter.println("[" + dateTimeFormatter.format(LocalTime.now()) + "] " + var1);
         this.dirty = true;
         if (System.currentTimeMillis() - this.longValue >= 200L) {
            this.flush();
         }
      }
   }

   private synchronized void flush() {
      this.longValue = System.currentTimeMillis();
      if (this.dirty && this.printWriter != null) {
         this.dirty = false;
         this.printWriter.flush();
      }
   }

   private void method508() {
      try {
         this.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.file3, true)), false);
      } catch (Exception var2) {
         this.printWriter = null;
      }
   }

   private void method509() {
      try {
         if (this.file3.length() <= 33554432L) {
            return;
         }

         File var1 = new File(this.file, "out.old.log");
         Files.deleteIfExists(var1.toPath());
         this.file3.renameTo(var1);
      } catch (Exception var2) {
      }
   }
}

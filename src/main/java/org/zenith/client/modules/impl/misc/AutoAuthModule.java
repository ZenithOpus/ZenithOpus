package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.TextSetting;

@ModuleInfo(
   name = "AutoAuth",
   category = ModuleCategory.MISC,
   description = "module.autoauth.description"
)
public final class AutoAuthModule extends Module {
   public static final AutoAuthModule INSTANCE = new AutoAuthModule();
   private final TextSetting command = (new TextSetting(
         "autoauth.passSetting.command", "autoauth.passSetting.command.desc", "BogdanSuperCoder", "Default password"
      ) {
         @Override
         public void safe(JsonObject var1) {
         }

         @Override
         public void load(JsonObject var1) {
         }

         @Override
         public boolean method2122(String var1) {
            boolean var2 = !var1.equals(this.getValue());
            if (!super.method2122(var1)) {
               return false;
            } else {
               if (var2) {
                  AutoAuthModule.this.method2711();
               }

               return true;
            }
         }
      })
      .method2120();
   private final BooleanSetting randomPass = new BooleanSetting("autoauth.randomPass", "autoauth.randomPass.description", true);
   private final File file = new File("autoauth_accounts.txt");
   private final BooleanSetting login = new BooleanSetting("autoauth.loginSetting.login", "autoauth.loginSetting.description", true);
   private final ButtonSetting openFile = new ButtonSetting("autoauth.openFile", "K", () -> {
      try {
         String var1 = this.file.getAbsolutePath();
         new ProcessBuilder("cmd", "/c", "start", "", "\"" + var1 + "\"").start();
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   });
   private final Map<String, String> items = new HashMap<>();

   private AutoAuthModule() {
      this.method2710();
   }

   private void method2710() {
      try {
         if (!this.file.exists()) {
            this.file.createNewFile();
            this.method2711();
            return;
         }

         for (String var3 : Files.readAllLines(this.file.toPath())) {
            if (var3.contains(":")) {
               String[] var4 = var3.split(":", 2);
               if (var4[0].trim().equalsIgnoreCase("default")) {
                  this.command.setValue(var4[1].trim());
               } else {
                  String[] var5 = var3.split(":", 3);
                  if (var5.length >= 3) {
                     String var6 = var5[0].trim().toLowerCase();
                     String var7 = var5[1].trim();
                     String var8 = var5[2].trim();
                     String var9 = var6 + ":" + var7.toLowerCase();
                     this.items.put(var9, var8);
                  }
               }
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }
   }

   private void method2711() {
      try {
         StringBuilder var1 = new StringBuilder();
         var1.append("default : ").append(this.command.getValue()).append("\n");

         for (Entry var3 : this.items.entrySet()) {
            String var4 = (String)var3.getKey();
            String var5 = (String)var3.getValue();
            String[] var6 = var4.split(":", 2);
            String var7 = var6[0];
            String var8 = var6[1];
            var1.append(var7).append(" : ").append(var8).append(" : ").append(var5).append("\n");
         }

         Files.writeString(this.file.toPath(), var1.toString(), StandardOpenOption.TRUNCATE_EXISTING);
      } catch (Exception var9) {
         var9.printStackTrace();
      }
   }

   private String method2712() {
      String var1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
      Random var2 = new Random();
      StringBuilder var3 = new StringBuilder();

      for (int var4 = 0; var4 < 12; var4++) {
         var3.append(var1.charAt(var2.nextInt(var1.length())));
      }

      return var3.toString();
   }

   private String method2713(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         String[] var2 = var1.split("\\.");
         return var2.length >= 2 ? var2[var2.length - 2] : var1;
      } else {
         return "unknown";
      }
   }

   @EventTarget
   public void method2714(PacketEvent var1) {
      if (var1.method7100()) {
         if (var1.getPacket3() instanceof GameMessageS2CPacket var2) {
            if (mc.getNetworkHandler() != null) {
               String var6 = var2.content().getString().toLowerCase();
               boolean var4 = var6.contains("/register") || var6.contains("зарегистрируйтесь");
               boolean var5 = this.login.isEnabled() && (var6.contains("/login") || var6.contains("/ʟ [пᴀᴘᴏль]"));
               if (var4 || var5) {
                  mc.execute(() -> this.method2715(var4));
               }
            }
         }
      }
   }

   private void method2715(boolean var1) {
      if (mc.getNetworkHandler() != null) {
         String var2 = mc.getCurrentServerEntry() != null ? mc.getCurrentServerEntry().address : "";
         String var3 = this.method2713(var2).toLowerCase();
         String var4 = mc.getSession().getUsername().toLowerCase();
         String var5 = var3 + ":" + var4;
         String var6 = this.items.getOrDefault(var5, null);
         if (var1) {
            if (var6 == null) {
               var6 = this.randomPass.isEnabled() ? this.method2712() : this.command.getValue();
            }

            this.items.put(var5, var6);
            this.method2711();
            mc.getNetworkHandler().sendChatCommand("register %s %s".formatted(var6, var6));
         } else {
            if (var6 == null) {
               var6 = this.command.getValue();
               this.items.put(var5, var6);
               this.method2711();
            }

            mc.getNetworkHandler().sendChatCommand("login " + var6);
         }
      }
   }
}

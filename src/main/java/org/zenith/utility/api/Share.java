package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;

public final class Share {
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final File file = new File(Zenith.file, "cloud-share.json");
   public final ModeSetting cosmeticsVisibility = new ModeSetting(
      "cloud.share.cosmeticsVisibility",
      "cloud.share.cosmeticsVisibility.desc",
      "cloud.share.cosmeticsVisibility.everyone",
      "cloud.share.cosmeticsVisibility.friends",
      "cloud.share.cosmeticsVisibility.nobody"
   );
   public final BooleanSetting position = new BooleanSetting("cloud.share.position", "cloud.share.position.desc", true);
   public final BooleanSetting health = new BooleanSetting("cloud.share.health", "cloud.share.health.desc", true);
   public final BooleanSetting inventory = new BooleanSetting("cloud.share.inventory", "cloud.share.inventory.desc", true);
   public final BooleanSetting server = new BooleanSetting("cloud.share.server", "cloud.share.server.desc", true);
   public final ColorSetting colorSetting = new ColorSetting("cloud.share.cosmeticAccent", "cloud.share.cosmeticAccent.desc", new ZenithColor(-7643914));
   public final BooleanSetting allowJoin = new BooleanSetting("cloud.share.allowJoin", "cloud.share.allowJoin.desc", true);
   public final BooleanSetting deaths = new BooleanSetting("cloud.share.deaths", "cloud.share.deaths.desc", true);
   public final BooleanSetting stats = new BooleanSetting("cloud.share.stats", "cloud.share.stats.desc", true);
   public final BooleanSetting notifyMessages = new BooleanSetting("cloud.share.notifyMessages", "cloud.share.notifyMessages.desc", true);
   public final BooleanSetting notifyInvites = new BooleanSetting("cloud.share.notifyInvites", "cloud.share.notifyInvites.desc", true);
   public final ModeSetting status = new ModeSetting(
      "cloud.presence.status",
      "cloud.presence.status.desc",
      "cloud.presence.auto",
      "cloud.presence.online",
      "cloud.presence.pvp",
      "cloud.presence.farm",
      "cloud.presence.build",
      "cloud.presence.trade",
      "cloud.presence.afk",
      "cloud.presence.dnd"
   );
   public final TextSetting text = new TextSetting(
      "cloud.presence.text", "cloud.presence.text.desc", "", "cloud.presence.text.empty", TextSetting.Entry.method2125(48)
   );
   public final BooleanSetting autoPvp = new BooleanSetting("cloud.presence.autoPvp", "cloud.presence.autoPvp.desc", true);
   public final SliderSetting autoAfk = new SliderSetting("cloud.presence.autoAfk", 5.0F, 0.0F, 30.0F, 1.0F, "cloud.presence.autoAfk.desc", " min", null, null);
   private boolean loaded;
   public static final String[] texts = new String[]{"auto", "online", "pvp", "farm", "build", "trade", "afk", "dnd"};

   public List<Setting> method946() {
      return List.of(
         this.cosmeticsVisibility,
         this.position,
         this.health,
         this.inventory,
         this.server,
         this.colorSetting,
         this.allowJoin,
         this.deaths,
         this.stats,
         this.notifyMessages,
         this.notifyInvites,
         this.status,
         this.text,
         this.autoPvp,
         this.autoAfk
      );
   }

   public String method947() {
      int var1 = this.status.getIndex();
      return var1 >= 0 && var1 < texts.length ? texts[var1] : "auto";
   }

   public void method948(String var1) {
      for (int var2 = 0; var2 < texts.length; var2++) {
         if (texts[var2].equals(var1)) {
            this.status.getValues().get(var2).method2103();
            return;
         }
      }
   }

   public boolean method949() {
      return this.cosmeticsVisibility.isSelected(2);
   }

   public void load() {
      if (!this.loaded) {
         this.loaded = true;
         if (file.exists()) {
            try {
               JsonObject var1 = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();

               for (Setting var3 : this.method946()) {
                  if (var1.has(var3.getKey())) {
                     try {
                        var3.load(var1);
                     } catch (Exception var5) {
                     }
                  }
               }
            } catch (Exception var6) {
            }
         }
      }
   }

   public synchronized void save() {
      try {
         JsonObject var1 = new JsonObject();

         for (Setting var3 : this.method946()) {
            var3.safe(var1);
         }

         file.getParentFile().mkdirs();
         Path var7 = file.toPath();
         Path var8 = var7.resolveSibling(var7.getFileName() + ".tmp");
         Files.writeString(var8, gson.toJson(var1), StandardCharsets.UTF_8);

         try {
            Files.move(var8, var7, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException var5) {
            Files.move(var8, var7, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }
}

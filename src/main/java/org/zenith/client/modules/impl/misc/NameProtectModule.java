package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.misc.ModuleHookDefinitions;
import org.zenith.utility.misc.TextUtils;

@ModuleInfo(
   name = "NameProtect",
   category = ModuleCategory.MISC,
   description = "module.nameprotect.description"
)
public final class NameProtectModule extends Module {
   public static final NameProtectModule INSTANCE = new NameProtectModule();
   private final TextSetting nameSetting = new TextSetting(
      "module.nameProtect.nameSetting",
      "module.nameProtect.nameSetting.desc",
      "Zenith",
      "module.nameProtect.nameSetting.empty",
      TextSetting.Entry.method2125(16)
   );
   private final TextSetting gowno = new TextSetting("gowno", "gowno", "hownp", () -> false);
   public Text text = null;
   public Text text2;
   private int intValue = Integer.MIN_VALUE;
   private String text3;
   private Runnable runnable;
   private final BooleanSetting hideFriends = new BooleanSetting("module.nameProtect.hideFriends", "module.nameProtect.hideFriends.desc", false);
   private final ButtonSetting openCommand = new ButtonSetting(
      "module.nameProtect.openCommand", "K", "module.nameProtect.openCommand.desc", () -> mc.setScreen(new ChatScreen(".nameprotect ", false))
   );

   private NameProtectModule() {
   }

   @Override
   public void onEnable() {
      if (this.runnable == null) {
         this.runnable = ModuleHookDefinitions.class124.method1646(0, var0 -> var0.text = method5149(var0.text));
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.runnable != null) {
         this.runnable.run();
         this.runnable = null;
      }

      super.onDisable();
   }

   public boolean method5140() {
      return this.hideFriends.isEnabled();
   }

   public static String method5141() {
      return INSTANCE.isEnabled() ? INSTANCE.getNameSetting().getValue() : mc.player.getNameForScoreboard();
   }

   public Text method5142(Text var1) {
      String var2 = mc.player.getNameForScoreboard();
      String var3 = method5141();
      this.setEnabled(false);

      MutableText var5;
      try {
         Text var4 = this.method5143(var1, var2, var3);
         if (this.text != null) {
            return TextUtils.method2107(var1, var2, this.text.copy().append(var4.copy()));
         }

         var5 = TextUtils.method2111(var1, var2, var4);
      } finally {
         this.setEnabled(true);
      }

      return var5;
   }

   private Text method5143(Text var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         var var4 = this.method5144(var1, var2);
         if (var4.isEmpty()) {
            return Text.literal(var3);
         } else {
            MutableText var5 = Text.empty();
            int var6 = 0;

            for (int var7 = 0; var6 < var3.length(); var7++) {
               int var8 = var3.codePointAt(var6);
               Style var9 = (Style)var4.get(Math.min(var7, var4.size() - 1));
               var5.append(Text.literal(new String(Character.toChars(var8))).setStyle(var9));
               var6 += Character.charCount(var8);
            }

            return var5;
         }
      } else {
         return Text.empty();
      }
   }

   private List<Style> method5144(Text var1, String var2) {
      if (var1 != null && var2 != null && !var2.isEmpty()) {
         ArrayList<Style> var3 = new ArrayList<>();
         ArrayList var4 = new ArrayList();
         StringBuilder var5 = new StringBuilder();
         var1.asOrderedText().accept((var2x, var3x, var4x) -> {
            var5.appendCodePoint(var4x);
            var4.add(var3x != null ? var3x : Style.EMPTY);
            return true;
         });
         String var6 = var5.toString();
         int var7 = var6.indexOf(var2);
         if (var7 == -1) {
            return var3;
         } else {
            int var8 = var6.codePointCount(0, var7);
            int var9 = var2.codePointCount(0, var2.length());
            int var10 = Math.min(var8 + var9, var4.size());

            for (int var11 = var8; var11 < var10; var11++) {
               var3.add((Style)var4.get(var11));
            }

            return var3;
         }
      } else {
         return List.of();
      }
   }

   @EventTarget
   public void method5145(UpdateEvent var1) {
      String var2 = this.gowno.getValue();
      if (!var2.equals(this.text3)) {
         this.text3 = var2;
         if (var2.isEmpty()) {
            this.text = null;
            this.intValue = Integer.MIN_VALUE;
         } else {
            try {
               int var3 = var2.indexOf(32);
               if (var3 <= 0 || var3 >= var2.length() - 1) {
                  this.text = null;
                  this.intValue = Integer.MIN_VALUE;
                  return;
               }

               this.intValue = Integer.parseInt(var2.substring(0, var3));
               JsonElement var4 = JsonParser.parseString(var2.substring(var3 + 1));
               this.text = TextCodecs.CODEC.parse(JsonOps.INSTANCE, var4).result().orElse(Text.empty());
            } catch (Exception var5) {
               this.text = null;
               this.intValue = Integer.MIN_VALUE;
               var5.printStackTrace();
            }
         }
      }
   }

   public Text method5146(PlayerListEntry var1) {
      return var1.getDisplayName() != null
         ? this.method5147(var1, var1.getDisplayName().copy())
         : this.method5147(var1, Team.decorateName(var1.getScoreboardTeam(), Text.literal(var1.getProfile().name())));
   }

   private Text method5147(PlayerListEntry var1, MutableText var2) {
      return var1.getGameMode() == GameMode.SPECTATOR ? var2.formatted(Formatting.ITALIC) : var2;
   }

   @EventTarget
   public void method5148(ChatReceiveEvent var1) {
      String var2 = this.method5150(var1.getText().getString());
      if (var2 != null) {
         var1.method7858(TextUtils.method2110(var1.getText(), var2, method5141()));
      }
   }

   public static String method5149(String var0) {
      NameProtectModule var1 = INSTANCE;
      if (var0 != null && !var0.isEmpty() && var1 != null && var1.isEnabled() && mc.player != null) {
         String var2 = mc.player.getNameForScoreboard();
         if (var0.contains(var2)) {
            return var0.replace(var2, INSTANCE.getNameSetting().getValue());
         } else {
            if (var1 instanceof NameProtectModule var3 && var3.hideFriends.isEnabled()) {
               for (String var6 : Zenith.getInstance().getFriends().getItems()) {
                  if (var0.contains(var6)) {
                     return var0.replace(var6, INSTANCE.getNameSetting().getValue());
                  }
               }
            }

            return var0;
         }
      } else {
         return var0;
      }
   }

   private String method5150(String var1) {
      String var2 = mc.player.getNameForScoreboard();
      if (var1.contains(var2)) {
         return var2;
      } else {
         if (this.hideFriends.isEnabled()) {
            for (String var5 : Zenith.getInstance().getFriends().getItems()) {
               if (var1.contains(var5)) {
                  return var5;
               }
            }
         }

         return null;
      }
   }

   public void method5151(JsonElement var1, int var2) {
      this.gowno.setValue(var2 + " " + var1.toString());
      this.intValue = var2;
   }

   public void method5152() {
      this.gowno.setValue("");
      this.intValue = Integer.MIN_VALUE;
   }

   @Generated
   public TextSetting getNameSetting() {
      return this.nameSetting;
   }

   @Generated
   public Text getText() {
      return this.text;
   }

   @Generated
   public int getIntValue18() {
      return this.intValue;
   }
}

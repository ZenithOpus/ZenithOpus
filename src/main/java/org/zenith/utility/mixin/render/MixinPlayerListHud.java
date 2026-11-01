package org.zenith.utility.mixin.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.utility.misc.ScoreboardParser;

@Mixin({PlayerListHud.class})
public abstract class MixinPlayerListHud {
   @Shadow
   public Text header;
   @Shadow
   public Text footer;
   @Unique
   private Text zenith$savedHeader;
   @Unique
   private Text zenith$savedFooter;

   @Shadow
   public abstract Text applyGameModeFormatting(PlayerListEntry var1, MutableText var2);

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$streamerTabStart(DrawContext var1, int var2, Scoreboard var3, ScoreboardObjective var4, CallbackInfo var5) {
      if (ScoreboardParser.isActive()) {
         this.zenith$savedHeader = this.header;
         this.zenith$savedFooter = this.footer;
         this.header = ScoreboardParser.method7074(this.header);
         this.footer = ScoreboardParser.method7074(this.footer);
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$streamerTabEnd(DrawContext var1, int var2, Scoreboard var3, ScoreboardObjective var4, CallbackInfo var5) {
      if (this.zenith$savedHeader != null || this.zenith$savedFooter != null) {
         this.header = this.zenith$savedHeader;
         this.footer = this.zenith$savedFooter;
         this.zenith$savedHeader = null;
         this.zenith$savedFooter = null;
      }
   }

   @Inject(
      method = {"getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getPlayerName(PlayerListEntry var1, CallbackInfoReturnable<Text> var2) {
      if (NameProtectModule.INSTANCE.isEnabled()) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         boolean var4 = var3.player != null && var3.player.getUuid().equals(var1.getProfile().id());
         if (var4 || NameProtectModule.INSTANCE.method5140() && Zenith.getInstance().getFriends().method7627(var1.getProfile().name())) {
            var2.setReturnValue(
               NameProtectModule.INSTANCE
                  .method5142(
                     var1.getDisplayName() != null
                        ? this.applyGameModeFormatting(var1, var1.getDisplayName().copy())
                        : this.applyGameModeFormatting(var1, Team.decorateName(var1.getScoreboardTeam(), Text.literal(var1.getProfile().name())))
                  )
            );
            return;
         }
      }

      Text var5 = StreamerModeModule.INSTANCE.method6290(var1);
      if (var5 != null) {
         var2.setReturnValue(var5);
      }
   }

   @Inject(
      method = {"collectPlayerEntries()Ljava/util/List;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void collectPlayerEntries(CallbackInfoReturnable<List<PlayerListEntry>> var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.player != null) {
         List<PlayerListEntry> var3 = (List)var1.getReturnValue();
         if (var3 != null && !var3.isEmpty()) {
            if (StreamerModeModule.INSTANCE.isEnabled()) {
               ArrayList var9 = new ArrayList();

               for (Object var11_o : (Iterable<?>)(Object)(var3)) {
                  PlayerListEntry var11 = (PlayerListEntry)var11_o;
                  if (!var11.getProfile().id().equals(var2.player.getUuid())) {
                     var9.add(var11);
                  }
               }

               StreamerModeModule.INSTANCE.method6296(var9);
               var1.setReturnValue(var9);
            } else if (NameProtectModule.INSTANCE.isEnabled()) {
               int var4 = NameProtectModule.INSTANCE.getIntValue18();
               if (var4 != Integer.MIN_VALUE) {
                  if (var3.size() >= 2) {
                     var var5 = new ArrayList<>(var3);
                     int var6 = -1;

                     for (int var7 = 0; var7 < var5.size(); var7++) {
                        if (((PlayerListEntry)var5.get(var7)).getProfile().id().equals(var2.player.getUuid())) {
                           var6 = var7;
                           break;
                        }
                     }

                     if (var6 != -1) {
                        int var12 = Math.max(0, Math.min(var5.size() - 1, var4));
                        if (var12 != var6) {
                           PlayerListEntry var8 = (PlayerListEntry)var5.remove(var6);
                           if (var12 > var6) {
                              var12--;
                           }

                           var5.add(Math.min(var5.size(), var12), var8);
                           var1.setReturnValue(var5);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}

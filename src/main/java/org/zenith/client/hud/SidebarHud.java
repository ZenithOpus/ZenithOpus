package org.zenith.client.hud;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.PlayerListHud.ScoreDisplayEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class SidebarHud extends Interface {
   private final Animation animation = new Animation(250L, Easing.INSTANCE4);

   public SidebarHud(String var1) {
      super(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, null);
   }

   @Override
   public void method285(CustomDrawContext var1, Interface.Entry var2) {
   }

   @Override
   public void method286(CustomDrawContext var1, Interface.Entry var2) {
   }

   @Override
   public void method287(float var1, float var2) {
   }

   @Override
   public void update(float var1, float var2) {
   }

   @Override
   public void release() {
   }

   @Override
   public void method288(float var1, float var2) {
   }

   @Override
   public void method289(CustomDrawContext var1, float var2, float var3, InterfaceModule var4, float var5, float var6) {
   }

   @Override
   public JsonObject save() {
      return new JsonObject();
   }

   @Override
   public void load(JsonObject var1) {
   }

   @Override
   public float method290() {
      return 1.0F;
   }

   @Override
   public void method291(MenuDrawContext var1) {
      float var2 = this.method290();
      var1.pushMatrix();
      var1.getMatrices().translate(this.floatValue + this.width / 2.0F, this.floatValue2);
      var1.getMatrices().scale(var2, var2);
      var1.getMatrices().translate(-this.floatValue - this.width / 2.0F, -this.floatValue2);
      this.method8(var1);
      var1.popMatrix();
   }

   @Override
   public void method8(CustomDrawContext var1) {
      Scoreboard var2 = mc.world.getScoreboard();
      ScoreboardObjective var3 = var2.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
      this.animation
         .method2807(
            mc.options.playerListKey.isPressed()
               && (!mc.isInSingleplayer() || mc.player.networkHandler.getListedPlayerListEntries().size() > 1 || var3 != null)
         );
      if (this.animation.getValue() != 0.0F) {
         this.method292(var1, var1.getScaledWindowWidth(), var2, var3);
      }

      this.animation.setDuration(250L);
      this.animation.setEasing(Easing.INSTANCE4);
   }

   public void method292(DrawContext var1, int var2, Scoreboard var3, @Nullable ScoreboardObjective var4) {
      float var5 = this.animation.getValue();
      List<PlayerListEntry> var6 = mc.inGameHud.getPlayerListHud().collectPlayerEntries();
      var var7 = new ArrayList<>(var6.size());
      int var8 = mc.textRenderer.getWidth(" ");
      int var9 = 0;
      int var10 = 0;

      for (Object var12_o : (Iterable<?>)(Object)(var6)) {
         PlayerListEntry var12 = (PlayerListEntry)var12_o;
         Text var13 = mc.inGameHud.getPlayerListHud().getPlayerName(var12);
         var9 = Math.max(var9, mc.textRenderer.getWidth(var13));
         int var14 = 0;
         MutableText var15 = null;
         int var16 = 0;
         if (var4 != null) {
            ScoreHolder var17 = ScoreHolder.fromProfile(var12.getProfile());
            ReadableScoreboardScore var18 = var3.getScore(var17, var4);
            if (var18 != null) {
               var14 = var18.getScore();
            }

            if (var4.getRenderType() != RenderType.HEARTS) {
               NumberFormat var19 = var4.getNumberFormatOr(StyledNumberFormat.YELLOW);
               var15 = ReadableScoreboardScore.getFormattedScore(var18, var19);
               var16 = mc.textRenderer.getWidth(var15);
               var10 = Math.max(var10, var16 > 0 ? var8 + var16 : 0);
            }
         }

         var7.add(new ScoreDisplayEntry(var13, var14, var15, var16));
      }

      if (!mc.inGameHud.getPlayerListHud().hearts.isEmpty()) {
         var var45 = var6.stream().map(var0 -> var0.getProfile().id()).collect(Collectors.toSet());
         mc.inGameHud.getPlayerListHud().hearts.keySet().removeIf(var1x -> !var45.contains(var1x));
      }

      int var46 = var6.size();
      int var47 = var46;

      int var48;
      for (var48 = 1; var47 > 20; var47 = (var46 + var48 - 1) / var48) {
         var48++;
      }

      boolean var49 = true;
      int var50;
      if (var4 != null) {
         var50 = var4.getRenderType() == RenderType.HEARTS ? 90 : var10;
      } else {
         var50 = 0;
      }

      int var51 = Math.min(var48 * ((var49 ? 9 : 0) + var9 + var50 + 13), var2 - 50) / var48;
      int var52 = var2 / 2 - (var51 * var48 + (var48 - 1) * 5) / 2;
      this.floatValue = var52;
      int var53 = 10;
      int var55 = var51 * var48 + (var48 - 1) * 5;
      this.floatValue2 = var53;
      this.width = var55;
      List<OrderedText> var20 = null;
      if (mc.inGameHud.getPlayerListHud().header != null) {
         Text var21 = ScoreboardParser.method7074(mc.inGameHud.getPlayerListHud().header);
         var20 = mc.textRenderer.wrapLines(var21, var2 - 50);

         for (Object var23_o : (Iterable<?>)(Object)(var20)) {
            OrderedText var23 = (OrderedText)var23_o;
            var55 = Math.max(var55, mc.textRenderer.getWidth(var23));
         }
      }

      List<OrderedText> var56 = null;
      if (mc.inGameHud.getPlayerListHud().footer != null) {
         Text var57 = ScoreboardParser.method7074(mc.inGameHud.getPlayerListHud().footer);
         var56 = mc.textRenderer.wrapLines(var57, var2 - 50);

         for (Object var24_o : (Iterable<?>)(Object)(var56)) {
            OrderedText var24 = (OrderedText)var24_o;
            var55 = Math.max(var55, mc.textRenderer.getWidth(var24));
         }
      }

      int var58 = var20 != null ? var20.size() * 9 : 0;
      int var60 = var20 != null ? 1 : 0;
      int var61 = var47 * 9;
      int var25 = var56 != null ? 1 : 0;
      int var26 = var56 != null ? var56.size() * 9 : 0;
      int var27 = var58 + var60 + var61 + var25 + var26;
      this.height = var27;
      float var28 = var2 / 2 - 1;
      float var29 = 10.0F + var27 / 2.0F;
      float var30 = Math.max(1.0E-4F, var5);
      Matrix3x2fStack var31 = var1.getMatrices();
      var31.pushMatrix();
      var31.translate(var28, var29);
      var31.scale(var30, var30);
      var31.translate(-var28, -var29);
      if (var20 != null) {
         var1.fill(var2 / 2 - var55 / 2 - 1, var53 - 1, var2 / 2 + var55 / 2 + 1, var53 + var20.size() * 9, Integer.MIN_VALUE);

         for (Object var33_o : (Iterable<?>)(Object)(var20)) {
            OrderedText var33 = (OrderedText)var33_o;
            int var34 = mc.textRenderer.getWidth(var33);
            var1.drawTextWithShadow(mc.textRenderer, var33, var2 / 2 - var34 / 2, var53, -1);
            var53 += 9;
         }

         var53++;
      }

      var1.fill(var2 / 2 - var55 / 2 - 1, var53 - 1, var2 / 2 + var55 / 2 + 1, var53 + var47 * 9, Integer.MIN_VALUE);
      int var62 = mc.options.getTextBackgroundColor(553648127);

      for (int var63 = 0; var63 < var46; var63++) {
         int var65 = var63 / var47;
         int var35 = var63 % var47;
         int var36 = var52 + var65 * var51 + var65 * 5;
         int var37 = var53 + var35 * 9;
         var1.fill(var36, var37, var36 + var51, var37 + 8, var62);
         if (var63 < var6.size()) {
            PlayerListEntry var38 = (PlayerListEntry)var6.get(var63);
            ScoreDisplayEntry var39 = (ScoreDisplayEntry)var7.get(var63);
            GameProfile var40 = var38.getProfile();
            int var41 = var36;
            if (var49) {
               PlayerEntity var42 = mc.world.getPlayerByUuid(var40.id());
               boolean var43 = var42 != null && method298(var42);
               PlayerSkinDrawer.draw(var1, var38.getSkinTextures().body().texturePath(), var36, var37, 8, var38.shouldShowHat(), var43, -1);
               var41 = var36 + 9;
            }

            int var68 = var38.getGameMode() == GameMode.SPECTATOR ? -1859310289 : -1;
            var1.drawTextWithShadow(mc.textRenderer, var39.name(), var41, var37, var68);
            if (var4 != null && var38.getGameMode() != GameMode.SPECTATOR) {
               int var69 = var41 + var9 + 1;
               int var44 = var69 + var50;
               if (var44 - var69 > 5) {
                  mc.inGameHud.getPlayerListHud().renderScoreboardObjective(var4, var37, var39, var69, var44, var40.id(), var1);
               }
            }

            mc.inGameHud.getPlayerListHud().renderLatencyIcon(var1, var51, var41 - (var49 ? 9 : 0), var37, var38);
         }
      }

      if (var56 != null) {
         var53 += var47 * 9 + 1;
         var1.fill(var2 / 2 - var55 / 2 - 1, var53 - 1, var2 / 2 + var55 / 2 + 1, var53 + var56.size() * 9, Integer.MIN_VALUE);

         for (Object var66_o : (Iterable<?>)(Object)(var56)) {
            OrderedText var66 = (OrderedText)var66_o;
            int var67 = mc.textRenderer.getWidth(var66);
            var1.drawTextWithShadow(mc.textRenderer, var66, var2 / 2 - var67 / 2, var53, -1);
            var53 += 9;
         }
      }

      var31.popMatrix();
   }

   @Override
   public boolean method293(double var1, double var3) {
      return false;
   }

   @Override
   public float method294() {
      return 0.0F;
   }

   @Override
   public float method295() {
      return 0.0F;
   }

   @Override
   public float method296() {
      return 0.0F;
   }

   @Override
   public float method297() {
      return 0.0F;
   }

   private static boolean method298(PlayerEntity var0) {
      return PlayerEntityRenderer.shouldFlipUpsideDown(var0) && var0.isModelPartVisible(PlayerModelPart.CAPE);
   }
}

package org.zenith.client.scripting;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalXZ;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.utility.game.Util4;

public final class ScriptWorldApi {
   private ScriptWorldApi() {
   }

   private static MinecraftClient method4816() {
      return MinecraftClient.getInstance();
   }

   public static void method4817(Script var0, Scriptable var1, ScriptableObject var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var1, var2);
      var3.method6274(
         "scoreboard",
         var1x -> {
            if (method4816().world == null) {
               return null;
            } else {
               Scoreboard var2x = method4816().world.getScoreboard();
               ScoreboardObjective var3x = var2x.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
               if (var3x == null) {
                  return null;
               } else {
                  ArrayList<ScoreboardEntry> var4 = new ArrayList<>(var2x.getScoreboardEntries(var3x));
                  var4.removeIf(ScoreboardEntry::hidden);
                  var4.sort(Comparator.comparingInt(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER));
                  ArrayList var5 = new ArrayList();

                  for (Object var7_o : (Iterable<?>)(Object)(var4.subList(0, Math.min(15, var4.size())))) {
                     ScoreboardEntry var7 = (ScoreboardEntry)var7_o;
                     Team var8 = var2x.getScoreHolderTeam(var7.owner());
                     Object var9 = var7.display() != null ? var7.display() : Team.decorateName(var8, Text.literal(var7.owner()));
                     var5.add(
                        ScriptValueUtils.method6247(var1)
                           .method6277("text", ((net.minecraft.text.Text)var9).getString())
                           .method6277("owner", var7.owner())
                           .method6277("score", (double)var7.value())
                           .getScriptableObject()
                     );
                  }

                  return ScriptValueUtils.method6247(var1)
                     .method6277("title", var3x.getDisplayName().getString())
                     .method6277("lines", ScriptValueUtils.method6267(var1, var5))
                     .getScriptableObject();
               }
            }
         }
      );
      var3.method6274(
         "tablist",
         var1x -> {
            if (method4816().getNetworkHandler() == null) {
               return null;
            } else {
               PlayerListHud var2x = method4816().inGameHud.getPlayerListHud();
               ArrayList var3x = new ArrayList();

               for (PlayerListEntry var5 : var2x.collectPlayerEntries()) {
                  Object var6 = var5.getDisplayName() != null
                     ? var5.getDisplayName()
                     : Team.decorateName(var5.getScoreboardTeam(), Text.literal(var5.getProfile().name()));
                  var3x.add(
                     ScriptValueUtils.method6247(var1)
                        .method6277("name", var5.getProfile().name())
                        .method6277("display", ((net.minecraft.text.Text)var6).getString())
                        .method6277("ping", (double)var5.getLatency())
                        .method6277("gamemode", var5.getGameMode() == null ? "" : var5.getGameMode().asString())
                        .getScriptableObject()
                  );
               }

               return ScriptValueUtils.method6247(var1)
                  .method6277("header", var2x.header == null ? "" : var2x.header.getString())
                  .method6277("footer", var2x.footer == null ? "" : var2x.footer.getString())
                  .method6277("players", ScriptValueUtils.method6267(var1, var3x))
                  .getScriptableObject();
            }
         }
      );
      var3.method6274(
         "bossbars",
         var1x -> {
            ArrayList var2x = new ArrayList();

            for (Object var4 : method4816().inGameHud.getBossBarHud().bossBars.values()) {
               ClientBossBar var5 = (ClientBossBar)var4;
               var2x.add(
                  ScriptValueUtils.method6247(var1)
                     .method6277("name", var5.getName().getString())
                     .method6277("percent", (double)var5.getPercent())
                     .method6277("color", var5.getColor().getName())
                     .getScriptableObject()
               );
            }

            return ScriptValueUtils.method6267(var1, var2x);
         }
      );
      var3.method6274(
         "title",
         var1x -> ScriptValueUtils.method6247(var1)
            .method6277("title", method4816().inGameHud.title == null ? "" : method4816().inGameHud.title.getString())
            .method6277("subtitle", method4816().inGameHud.subtitle == null ? "" : method4816().inGameHud.subtitle.getString())
            .getScriptableObject()
      );
      var3.method6274(
         "sign",
         var1x -> {
            if (method4816().world == null) {
               return null;
            } else if (!(
               method4816()
                  .world
                  .getBlockEntity(
                     BlockPos.ofFloored(
                        ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
                     )
                  ) instanceof SignBlockEntity var3x
            )) {
               return null;
            } else {
               ArrayList var4 = new ArrayList();
               ArrayList var5 = new ArrayList();

               for (Text var9 : var3x.getFrontText().getMessages(false)) {
                  var4.add(var9.getString());
               }

               for (Text var13 : var3x.getBackText().getMessages(false)) {
                  var5.add(var13.getString());
               }

               return ScriptValueUtils.method6247(var1)
                  .method6277("front", ScriptValueUtils.method6267(var1, var4))
                  .method6277("back", ScriptValueUtils.method6267(var1, var5))
                  .getScriptableObject();
            }
         }
      );
      var3.method6274(
         "biome",
         var0x -> method4816().world == null
            ? ""
            : method4816()
               .world
               .getBiome(
                  BlockPos.ofFloored(
                     ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
                  )
               )
               .getIdAsString()
      );
      var3.method6274(
         "light",
         var0x -> {
            if (method4816().world == null) {
               return 0.0;
            } else {
               BlockPos var1x = BlockPos.ofFloored(
                  ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
               );
               String var2x = ScriptValueUtils.method6254(var0x, 3, "max").toLowerCase(Locale.ROOT);
               int var3x = method4816().world.getLightLevel(LightType.BLOCK, var1x);
               int var4 = method4816().world.getLightLevel(LightType.SKY, var1x);
               return (double)(var2x.equals("block") ? var3x : (var2x.equals("sky") ? var4 : Math.max(var3x, var4)));
            }
         }
      );
      var3.method6274("glow", var1x -> {
         Entity var2x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (var2x == null) {
            return null;
         } else {
            Object var3x = ScriptValueUtils.arg(var1x, 1);
            boolean var4 = var3x instanceof Boolean var5 && !var5 || var3x == null;
            Util4.method1282(var0, var2x.getId(), var4 && !ScriptValueUtils.method6253(var3x) ? null : ScriptValueUtils.method6258(var1x, 1, -1));
            return null;
         }
      });
      var3.method6274("hide", var1x -> {
         Entity var2x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (var2x != null) {
            Util4.method1283(var0, var2x.getId(), !ScriptValueUtils.method6252(var1x, 1) || ScriptValueUtils.method6259(var1x, 1, true));
         }

         return null;
      });
   }

   public static Scriptable method4818(Scriptable var0, ItemStack var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var0);
      WrittenBookContentComponent var3 = var1.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
      WritableBookContentComponent var4 = var1.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
      ArrayList var5 = new ArrayList();
      if (var3 != null) {
         for (RawFilteredPair var7 : var3.pages()) {
            if (var5.size() >= 100) {
               break;
            }

            var5.add(((Text)var7.raw()).getString());
         }

         var2.method6277("title", var3.title().raw()).method6277("author", var3.author());
      } else if (var4 != null) {
         for (RawFilteredPair var10 : var4.pages()) {
            if (var5.size() >= 100) {
               break;
            }

            var5.add(var10.raw());
         }
      }

      var2.method6277("pages", ScriptValueUtils.method6267(var0, var5));
      String var9 = var1.getComponents().toString();
      var2.method6277("components", var9.length() > 8000 ? var9.substring(0, 8000) : var9);
      return var2.getScriptableObject();
   }

   private static Direction method4819(Object[] var0, int var1) {
      String var2 = ScriptValueUtils.method6254(var0, var1, "up").toLowerCase(Locale.ROOT);

      for (Direction var6 : Direction.values()) {
         if (var6.asString().equals(var2)) {
            return var6;
         }
      }

      return Direction.UP;
   }

   public static void method4820(Scriptable var0, ScriptableObject var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6246(var0, var1);
      var2.method6274("place", var0x -> method4824(var0x, true));
      var2.method6274("useOn", var0x -> method4824(var0x, false));
      var2.method6274(
         "breakBlock",
         var0x -> {
            if (method4816().player != null && method4816().interactionManager != null) {
               BlockPos var1x = BlockPos.ofFloored(
                  ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
               );
               Direction var2x = method4819(var0x, 3);
               boolean var3 = method4816().interactionManager.isBreakingBlock()
                  ? method4816().interactionManager.updateBlockBreakingProgress(var1x, var2x)
                  : method4816().interactionManager.attackBlock(var1x, var2x);
               method4816().player.swingHand(Hand.MAIN_HAND);
               return var3;
            } else {
               return false;
            }
         }
      );
      var2.method6274("stopBreaking", var0x -> {
         if (method4816().interactionManager != null) {
            method4816().interactionManager.cancelBlockBreaking();
         }

         return null;
      });
      var2.method6274("selectSlot", var0x -> {
         if (method4816().player == null) {
            return null;
         } else {
            method4816().player.getInventory().setSelectedSlot(Math.max(0, Math.min(8, ScriptValueUtils.method6257(var0x, 0, 0))));
            return null;
         }
      });
      var2.method6274("item", var1x -> method4816().player == null ? null : method4818(var0, method4816().player.getMainHandStack()));
   }

   private static boolean method4821(BlockPos var0) {
      BlockState var1 = method4816().world.getBlockState(var0);
      return !var1.isAir() && !var1.isReplaceable() && var1.getFluidState().isEmpty();
   }

   public static boolean method4822(BlockPos var0) {
      return Boolean.TRUE.equals(method4824(new Object[]{(double)var0.getX(), (double)var0.getY(), (double)var0.getZ()}, true));
   }

   public static Object method4823(double var0, double var2, double var4) {
      return method4824(new Object[]{var0, var2, var4}, false);
   }

   private static Object method4824(Object[] var0, boolean var1) {
      if (method4816().player != null && method4816().interactionManager != null && method4816().world != null) {
         BlockPos var2 = BlockPos.ofFloored(
            ScriptValueUtils.method6256(var0, 0, 0.0), ScriptValueUtils.method6256(var0, 1, 0.0), ScriptValueUtils.method6256(var0, 2, 0.0)
         );
         Hand var3 = ScriptValueUtils.method6254(var0, 4, "main").equals("off") ? Hand.OFF_HAND : Hand.MAIN_HAND;
         BlockPos var4;
         Direction var5;
         if (var1) {
            if (!method4816().world.getBlockState(var2).isReplaceable()) {
               return false;
            }

            var4 = null;
            var5 = null;
            ArrayList<Direction> var6 = new ArrayList<>(List.of(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP));
            if (ScriptValueUtils.method6252(var0, 3) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(var0, 3))) {
               var6.add(0, method4819(var0, 3).getOpposite());
            }

            for (Object var8_o : (Iterable<?>)(Object)(var6)) {
               Direction var8 = (Direction)var8_o;
               BlockPos var9 = var2.offset(var8);
               if (method4821(var9)) {
                  var4 = var9;
                  var5 = var8.getOpposite();
                  break;
               }
            }

            if (var4 == null) {
               return false;
            }
         } else {
            var4 = var2;
            var5 = method4819(var0, 3);
         }

         Vec3d var10 = Vec3d.ofCenter(var4).add(var5.getOffsetX() * 0.5, var5.getOffsetY() * 0.5, var5.getOffsetZ() * 0.5);
         if (method4816().player.getEyePos().squaredDistanceTo(var10) > 30.0) {
            return false;
         } else {
            ActionResult var11 = method4816().interactionManager.interactBlock(method4816().player, var3, new BlockHitResult(var10, var5, var4, false));
            if (var11.isAccepted()) {
               method4816().player.swingHand(var3);
            }

            return var11.isAccepted();
         }
      } else {
         return false;
      }
   }

   public static Scriptable method4825(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var0.method1413(() -> {
         try {
            if (active()) {
               BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            }
         } catch (Throwable var1x) {
         }
      });
      var2.method6274("available", var0x -> method4826());
      var2.method6274(
         "goto",
         var1x -> {
            method4827();
            ScriptNotifyApi.method2192(
               var0,
               ScriptValueUtils.method6256(var1x, 0, 0.0),
               ScriptValueUtils.method6252(var1x, 2) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(var1x, 2))
                  ? ScriptValueUtils.method6256(var1x, 1, 0.0)
                  : Double.NaN,
               ScriptValueUtils.method6252(var1x, 2) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(var1x, 2))
                  ? ScriptValueUtils.method6256(var1x, 2, 0.0)
                  : ScriptValueUtils.method6256(var1x, 1, 0.0)
            );
            BaritoneAPI.getProvider()
               .getPrimaryBaritone()
               .getCustomGoalProcess()
               .setGoalAndPath(
                  (Goal)(ScriptValueUtils.method6252(var1x, 2) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(var1x, 2))
                     ? new GoalBlock(
                        ScriptValueUtils.method6257(var1x, 0, 0), ScriptValueUtils.method6257(var1x, 1, 0), ScriptValueUtils.method6257(var1x, 2, 0)
                     )
                     : new GoalXZ(ScriptValueUtils.method6257(var1x, 0, 0), ScriptValueUtils.method6257(var1x, 1, 0)))
               );
            return null;
         }
      );
      var2.method6274(
         "gotoXZ",
         var0x -> {
            method4827();
            BaritoneAPI.getProvider()
               .getPrimaryBaritone()
               .getCustomGoalProcess()
               .setGoalAndPath(new GoalXZ(ScriptValueUtils.method6257(var0x, 0, 0), ScriptValueUtils.method6257(var0x, 1, 0)));
            return null;
         }
      );
      var2.method6274(
         "near",
         var1x -> {
            method4827();
            ScriptNotifyApi.method2192(
               var0, ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            );
            BaritoneAPI.getProvider()
               .getPrimaryBaritone()
               .getCustomGoalProcess()
               .setGoalAndPath(
                  new GoalNear(
                     new BlockPos(ScriptValueUtils.method6257(var1x, 0, 0), ScriptValueUtils.method6257(var1x, 1, 0), ScriptValueUtils.method6257(var1x, 2, 0)),
                     ScriptValueUtils.method6257(var1x, 3, 2)
                  )
               );
            return null;
         }
      );
      var2.method6274("stop", var0x -> {
         if (method4826()) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
         }

         return null;
      });
      var2.method6274("active", var0x -> active());
      var2.method6274(
         "mine",
         var0x -> {
            method4827();
            ArrayList var1x = new ArrayList();

            for (Object var3 : ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0))) {
               Identifier var4 = Identifier.tryParse(
                  ScriptValueUtils.method6255(var3).contains(":") ? ScriptValueUtils.method6255(var3) : "minecraft:" + ScriptValueUtils.method6255(var3)
               );
               if (var4 != null && Registries.BLOCK.containsId(var4)) {
                  var1x.add(Registries.BLOCK.get(var4));
               }
            }

            if (var1x.isEmpty()) {
               throw ScriptValueUtils.method6251("pathfind.mine([\"diamond_ore\"])");
            } else {
               BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().mine((Block[])var1x.toArray(new Block[0]));
               return null;
            }
         }
      );
      return var2.getScriptableObject();
   }

   private static boolean method4826() {
      try {
         return BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone() != null;
      } catch (Throwable var1) {
         return false;
      }
   }

   private static boolean active() {
      return method4826() && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   private static void method4827() {
      if (!method4826()) {
         throw ScriptValueUtils.method6251("Baritone недоступен");
      }
   }

   public static Scriptable method4828(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var0.method1413(() -> Util4.method1291(var0));
      var2.method6274("perspective", var0x -> {
         if (!ScriptValueUtils.method6252(var0x, 0)) {
            return switch (method4816().options.getPerspective()) {
               case FIRST_PERSON -> "first";
               case THIRD_PERSON_BACK -> "back";
               case THIRD_PERSON_FRONT -> "front";
            };
         } else {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "first").toLowerCase(Locale.ROOT);
            GameOptions var10000 = method4816().options;

            var10000.setPerspective(switch (var1x) {
               case "back", "third", "1" -> Perspective.THIRD_PERSON_BACK;
               case "front", "2" -> Perspective.THIRD_PERSON_FRONT;
               default -> Perspective.FIRST_PERSON;
            });
            return null;
         }
      });
      var2.method6274("zoom", var1x -> {
         Util4.method1284(var0, (float)Math.max(1.0, ScriptValueUtils.method6256(var1x, 0, 1.0)));
         return null;
      });
      var2.method6274("freecam", var0x -> {
         FreeCamModule var1x = FreeCamModule.INSTANCE;
         if (!ScriptValueUtils.method6252(var0x, 0)) {
            return var1x.isEnabled();
         } else {
            boolean var2x = ScriptValueUtils.method6259(var0x, 0, true);
            if (var1x.isEnabled() != var2x) {
               var1x.toggle();
            }

            return null;
         }
      });
      var2.method6274("position", var1x -> {
         Vec3d var2x = method4816().gameRenderer.getCamera().getCameraPos();
         return ScriptValueUtils.method6270(var1, var2x.x, var2x.y, var2x.z);
      });
      var2.method6274("yaw", var0x -> (double)method4816().gameRenderer.getCamera().getYaw());
      var2.method6274("pitch", var0x -> (double)method4816().gameRenderer.getCamera().getPitch());
      return var2.getScriptableObject();
   }
}

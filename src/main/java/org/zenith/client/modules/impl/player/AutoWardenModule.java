package org.zenith.client.modules.impl.player;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalBlock;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kotlin.collections.EmptyList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationLock;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationConfig;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.Util10;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.PvpDetector;

@ModuleInfo(
   name = "AutoWarden",
   category = ModuleCategory.PLAYER,
   description = "module.autowarden.description"
)
public class AutoWardenModule extends Module {
   public static final AutoWardenModule INSTANCE = new AutoWardenModule();
   private final BooleanSetting useSpeed = new BooleanSetting("module.autoWarden.useSpeed", "module.autoWarden.useSpeed.desc", false);
   private final BooleanSetting reportKillers = new BooleanSetting("module.autoWarden.reportKillers", "module.autoWarden.reportKillers.desc", false);
   private final ModeSetting lootPriority = new ModeSetting(
      "module.autoWarden.lootPriority",
      "module.autoWarden.lootPriority.desc",
      "module.autoWarden.lootPriority.medium",
      "module.autoWarden.lootPriority.low",
      "module.autoWarden.lootPriority.high"
   );
   private final SliderSetting escapeLoot = new SliderSetting(
      "module.autoWarden.escapeLoot", 8.0F, 1.0F, 27.0F, 1.0F, "module.autoWarden.escapeLoot.desc", "слот"
   );
   private final BooleanSetting debug = new BooleanSetting("module.autoWarden.debug", "module.autoWarden.debug.desc", false);
   private final BooleanSetting humanLook = new BooleanSetting("module.autoWarden.humanLook", "module.autoWarden.humanLook.desc", true);
   private final TextSetting homeAnarchy = new TextSetting(
      "module.autoWarden.homeAnarchy", "module.autoWarden.homeAnarchy.desc", "", "module.autoWarden.homeAnarchy.empty"
   );
   private final TextSetting wardenAnarchies = new TextSetting(
      "module.autoWarden.wardenAnarchies", "module.autoWarden.wardenAnarchies.desc", "", "303, 304, 318"
   );
   private final GroupSetting anarchySettings = new GroupSetting(
      "module.autoWarden.anarchySettings", "module.autoWarden.anarchySettings.desc", this.homeAnarchy, this.wardenAnarchies
   );
   private final List<Integer> items = new ArrayList<>();
   private String text;
   private final Map<BlockPos, Integer> items2 = new HashMap<>();
   private final Map<BlockPos, Integer> items3 = new HashMap<>();
   private final Map<BlockPos, Integer> items4 = new HashMap<>();
   private int intValue = -32768 << 16 | 0;
   private Rotation rotation;
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private static final long CONST_longValue = 30000L;
   private static final double CONST_doubleValue = 45.0;
   private static final double CONST_doubleValue2 = 16.0;
   private static final double CONST_doubleValue3 = 16.0;
   private static final double CONST_doubleValue4 = 20.0;
   private static final int CONST_intValue = 3;
   private static final int CONST_intValue2 = 600;
   private static final double CONST_doubleValue5 = 6400.0;
   private AutoWardenModule.Type type = AutoWardenModule.Type.SAVE;
   private int intValue2 = 1;
   private int intValue3;
   private boolean flag;
   private Box box;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private String text2 = "";
   private String text3;
   private int intValue4;
   private int intValue5 = -1;
   private static final int CONST_intValue3 = 9;

   @Override
   public List<Setting> getSettings() {
      return Zenith.method1502()
         ? List.of(this.anarchySettings, this.lootPriority, this.escapeLoot, this.useSpeed, this.reportKillers, this.humanLook, this.debug)
         : List.of(this.anarchySettings, this.lootPriority, this.escapeLoot, this.useSpeed, this.reportKillers, this.humanLook);
   }

   public boolean method3949() {
      return this.humanLook.isEnabled();
   }

   private AutoWardenModule() {
   }

   public List<Integer> method3950() {
      String var1 = method3952(this.homeAnarchy);
      String var2 = method3952(this.wardenAnarchies);
      String var3 = var1 + "|" + var2;
      if (this.text != null && this.text.equals(var3)) {
         return this.items;
      } else {
         this.text = var3;
         this.items.clear();
         int var4;
         if ((var4 = method3953(var1)) > 0) {
            this.items.add(var4);
         }

         for (int var6 : method3954(var2)) {
            if (this.items.size() >= 10) {
               break;
            }

            if (!this.items.contains(var6)) {
               this.items.add(var6);
            }
         }

         return this.items;
      }
   }

   public void method3951() {
      String var1 = (this.items != null && this.items.getClass() == EmptyList.class ? true : this.items.isEmpty()) ? "" : String.valueOf(this.items.getFirst());
      String var2 = this.items.stream().skip(1L).map(String::valueOf).collect(Collectors.joining(", "));
      this.homeAnarchy.setValue(var1);
      this.wardenAnarchies.setValue(var2);
      this.text = var1 + "|" + var2;
   }

   private static String method3952(TextSetting var0) {
      return var0.getValue() == null ? "" : var0.getValue();
   }

   private static int method3953(String var0) {
      var var1 = method3954(var0).iterator();
      return var1.hasNext() ? (Integer)var1.next() : -1;
   }

   private static List<Integer> method3954(String var0) {
      ArrayList<Integer> var1 = new ArrayList<>();

      for (String var5 : var0.split("[^0-9]+")) {
         if (!var5.isEmpty() && var5.length() <= 3) {
            try {
               int var6;
               if ((var6 = Integer.parseInt(var5)) >= 1 && var6 <= 999 && !var1.contains(var6)) {
                  var1.add(var6);
               }
            } catch (NumberFormatException var7) {
            }
         }
      }

      return var1;
   }

   private int method3955() {
      var var1 = this.method3950();
      return (var1.isEmpty()) ? -1 : (Integer)var1.getFirst();
   }

   private boolean method3956() {
      return this.method3955() >= 0 && this.method3955() == PvpDetector.method2724();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabledRaw()) {
         if (mc.player != null && mc.world != null) {
            var var10000 = this.method3950();
            if (var10000.isEmpty()) {
               int var1;
               if ((var1 = PvpDetector.method2724()) < 0) {
                  this.notify("Заполни «Анархия дома» в настройках модуля");
                  this.setToggled(false);
                  return;
               }

               this.homeAnarchy.setValue(String.valueOf(var1));
               this.text = null;
               this.notify("Анархия дома не задана — взял текущую: " + var1);
            }

            this.intValue2 = 1;
            this.type = AutoWardenModule.Type.COLLECTING;
            this.blockPos3 = null;
            this.items4.clear();
            PvpDetector.reset();
            this.notify("Shift + Пробел — быстрое выключение функции");
            Settings var2 = BaritoneAPI.getSettings();
            var2.avoidance.value = Boolean.TRUE;
            var2.maxFallHeightNoWater.value = 256;
            var2.blockFreeLook.value = Boolean.TRUE;
            var2.randomLooking.value = 1.0;
            var2.randomLooking113.value = 1.0;
            this.method3957(true);
         } else {
            this.setToggled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Settings var1 = BaritoneAPI.getSettings();
      var1.allowBreak.value = Boolean.FALSE;
      var1.allowPlace.value = Boolean.FALSE;
      var1.avoidance.value = Boolean.FALSE;
      var1.maxFallHeightNoWater.value = 3;
      this.method3957(false);
      this.method4032();
      this.method3977();
      this.rotation = null;
      RotationLock.reset();
      this.blockPos3 = null;
      this.intValue4 = 0;
      this.intValue5 = -1;
   }

   private void method3957(boolean var1) {
      Settings var2 = BaritoneAPI.getSettings();
      if (var2 != null) {
         List var3 = (List)var2.blocksToAvoid.value;

         try {
            for (Block var5 : Registries.BLOCK) {
               if (var5.getDefaultState().isIn(BlockTags.CANDLES)) {
                  if (var1) {
                     if (!var3.contains(var5)) {
                        var3.add(var5);
                     }
                  } else {
                     var3.remove(var5);
                  }
               }
            }
         } catch (IllegalStateException var6) {
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.currentScreen instanceof DeathScreen && mc.player != null && mc.player.deathTime >= 5) {
         mc.player.requestRespawn();
      }

      if (mc.player != null && mc.world != null) {
         this.rotation = null;
         if (mc.player.age < this.intValue) {
            this.items4.clear();
            this.items3.clear();
         }

         this.intValue = mc.player.age;
         PvpDetector.update();
         if (this.debug.isEnabled() && mc.player.age % 20 == 0) {
            this.method4039();
         }

         if (mc.options.sneakKey.isPressed() && mc.options.jumpKey.isPressed()) {
            this.setToggled(false);
         } else {
            for (WardenEntity var3 : mc.world.getEntitiesByClass(WardenEntity.class, mc.player.getBoundingBox().expand(256.0), var0 -> true)) {
               this.items4.put(var3.getBlockPos(), mc.player.age + 100);
            }

            this.items4.values().removeIf(var0 -> mc.player.age > var0);
            if (!this.method4029()) {
               if (this.text3 != null) {
                  if (mc.player.age >= 20 && mc.player.age < 30) {
                     mc.player.networkHandler.sendChatMessage("/report " + this.text3 + " чит");
                     this.text3 = null;
                  }
               } else if (mc.player.age < 5) {
                  this.intValue3 = 0;
                  this.items2.clear();
               } else if (PvpDetector.method2724() < 0) {
                  if (mc.player.age % 100 == 0 && this.method3955() >= 0 && mc.player.age > 300) {
                     mc.player.networkHandler.sendChatCommand("an" + this.method3955());
                  }

                  this.type = AutoWardenModule.Type.SAVE;
               } else {
                  if (mc.player.age % 100 == 0 && PvpDetector.method2727() && (this.box == null || !this.method3979())) {
                     this.method3978();
                  }

                  if (mc.player.hasStatusEffect(StatusEffects.GLOWING) && this.method4036(32.0)) {
                     this.method3972(false);
                  } else {
                     switch (this.type) {
                        case SAVE:
                           this.method3959();
                           break;
                        case TAKE:
                           this.method3960();
                           break;
                        case COLLECTING:
                           this.method3961();
                           break;
                        case ESCAPE:
                           this.method3964();
                     }

                     if (this.method4034() && mc.player.age % 15 == 0) {
                        this.method3977();
                     }
                  }
               }
            }
         }
      } else {
         this.rotation = null;
      }
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent var1) {
      if (mc.player != null && var1.getText() != null) {
         String var2 = var1.getText().getString();
         if (var2.contains("Помянем. Вы погибли")) {
            this.flag = true;
            if (this.reportKillers.isEnabled() && var2.contains("Вас убил")) {
               StringBuilder var3 = new StringBuilder();

               for (StatusEffectInstance var5 : mc.player.getStatusEffects()) {
                  var3.append(var5.getEffectType().value().getName().getString()).append(", ");
               }

               this.notify("Эффекты при смерти: " + (var3.isEmpty() ? "нет" : var3.toString().trim()));
               if (!mc.player.hasStatusEffect(StatusEffects.GLOWING) && !this.method4001(2.0)) {
                  this.text3 = var2.split("Вас убил ")[1].split(",")[0].trim();
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && mc.player != null) {
         String var2 = null;
         if (var1.getPacket3() instanceof PlaySoundS2CPacket var3) {
            var2 = this.method3958(var3.getSound());
         } else if (var1.getPacket3() instanceof PlaySoundFromEntityS2CPacket var4) {
            var2 = this.method3958(var4.getSound());
         }

         if (var2 != null) {
            if (var2.contains("warden.roar") || var2.contains("warden.angry") || var2.contains("warden.sonic")) {
               this.intValue3 = mc.player.age + 100;
            }
         }
      }
   }

   private String method3958(RegistryEntry<SoundEvent> var1) {
      return var1 != null && !var1.getKey().isEmpty() ? ((RegistryKey)var1.getKey().get()).getValue().getPath() : null;
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      if (mc.player != null) {
         if (!mc.player.isOnGround() && !mc.player.isClimbing()) {
            var1.method3040(false);
         }

         if (this.humanLook.isEnabled() && mc.player.isOnGround() && this.method3998() > 70.0F) {
            var1.method3042(0.0F, 0.0F);
            var1.method3039(false);
         }

         if (this.method4034() && mc.player.getMainHandStack().isEmpty() && !this.method4001(3.0)) {
            int var2 = mc.player.age % 10 <= ThreadLocalRandom.current().nextFloat(3.0F, 8.0F) ? -1 : 1;
            var1.method3042(var2, var2);
         }
      }
   }

   private void method3959() {
      if (this.method3955() >= 0 && PvpDetector.method2724() != this.method3955() && !PvpDetector.method2725() && mc.player.age % 5 == 0 && mc.player.age > 5) {
         mc.player.networkHandler.sendChatCommand("an" + this.method3955());
      }

      if (this.method3956()) {
         this.method4014();
         this.method4004(this.method4020(), true, AutoWardenModule.Type.TAKE);
      }
   }

   private void method3960() {
      if (this.flag && this.method3950().size() > 1) {
         if (++this.intValue2 >= this.method3950().size()) {
            this.intValue2 = 1;
         }

         this.flag = false;
      }

      this.intValue3 = 0;
      this.items2.clear();
      this.items3.clear();
      if (mc.player.age % 20 == 0) {
         StringBuilder var1 = new StringBuilder("Собираем (возможно не хватает) -> ");
         if (this.method4017() < 1) {
            var1.append("зелье невидимости, ");
         }

         if (InventoryUtils.method920(Items.GOLDEN_CARROT) < 3) {
            var1.append("золотая морковь, ");
         }

         if (this.useSpeed.isEnabled() && this.method4015(this::method4025) < 0) {
            var1.append("зелье скорости ");
         }

         if (mc.player.age % 200 == 0 && !var1.isEmpty() && !this.method4012()) {
            mc.player.closeHandledScreen();
         }
      }

      if (this.method3956()) {
         this.method4004(this.method4018() || this.method4012(), false, AutoWardenModule.Type.COLLECTING);
      }
   }

   private void method3961() {
      if (!this.method3965()) {
         if (this.intValue4 != 0) {
            if (this.method4033()) {
               this.method3977();
            }
         } else {
            var var1 = this.method3950();
            if (var1.size() <= 1) {
               if (mc.player.age % 20 == 0) {
                  this.notify("Анархии Вардена не заданы — вписать через запятую в настройках функции");
               }
            } else {
               if (this.intValue2 >= var1.size()) {
                  this.intValue2 = 1;
               }

               int var2 = (Integer)var1.get(this.intValue2);
               if (PvpDetector.method2724() != var2) {
                  if (mc.player.age % 10 == 0 && mc.player.age > 10) {
                     mc.player.networkHandler.sendChatCommand("an" + var2);
                  }
               } else {
                  if (mc.player.age > 5) {
                     this.method3963();
                  }
               }
            }
         }
      }
   }

   private int method3962() {
      StatusEffectInstance var1 = mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
      return var1 == null ? 0 : var1.getDuration() / 20;
   }

   private void method3963() {
      StatusEffectInstance var1 = mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
      boolean var2;
      if (!(var2 = mc.player.hasStatusEffect(StatusEffects.GLOWING) || var1 != null && var1.getDuration() >= 400)
         && var1 == null
         && this.method4017() < 1
         && mc.player.age % 5 == 0
         && !PvpDetector.method2725()) {
         this.method4000("ухожу домой: невидимости нет и зелий в инвентаре нет");
         this.type = AutoWardenModule.Type.ESCAPE;
      } else {
         if (!var2) {
            this.method4028(this::method4024);
         }

         if (!PvpDetector.method2727()) {
            if (mc.player.age % 50 == 0) {
               mc.player.networkHandler.sendChatCommand("home");
            }
         } else if (var2) {
            if ((this.useSpeed.isEnabled() && mc.player.getStatusEffect(StatusEffects.SPEED) == null ? this.method4015(this::method4025) : -1) < 0) {
               this.method3982();
            } else {
               this.method4028(this::method4025);
            }
         }
      }
   }

   private void method3964() {
      if (this.method3956()) {
         this.blockPos3 = null;
         this.type = AutoWardenModule.Type.SAVE;
      } else if (this.method4011()) {
         this.method4013();
      } else if (PvpDetector.method2725()) {
         this.method3972(this.method3968());
      } else {
         this.blockPos3 = null;
         this.type = AutoWardenModule.Type.SAVE;
      }
   }

   private boolean method3965() {
      boolean var1 = this.method3968();
      int var2 = this.method4016();
      int var3;
      int var4 = Math.max(var3 = this.method3966(), this.method3967(20));
      int var5 = mc.player.getHungerManager().getFoodLevel();
      if (mc.player.age > 100) {
         String var6 = null;
         if (var1) {
            var6 = "варден агрится";
         } else if (var2 > var4) {
            var6 = "инвентарь полон: " + var2 + " > " + var4;
         } else if (var5 < 8) {
            var6 = "голод " + var5;
         }

         if (var6 != null) {
            this.method4000("ухожу: " + var6);
            if (var1) {
               this.flag = true;
            }

            this.type = AutoWardenModule.Type.ESCAPE;
            return true;
         }
      }

      if (!PvpDetector.method2725() && var2 > var3) {
         this.method4000("ухожу сдавать: " + var2 + " слотов > порога " + var3);
         this.type = AutoWardenModule.Type.ESCAPE;
         return true;
      } else {
         int var7;
         if ((var7 = PvpDetector.method2726()) >= 0 && var7 < 7 && !this.method4036(14.0) && var2 > var3 - 1) {
            this.method4000("ухожу под конец пвп: " + var2 + " слотов, пвп " + var7 + " сек");
            this.type = AutoWardenModule.Type.ESCAPE;
            return true;
         } else {
            return false;
         }
      }
   }

   private int method3966() {
      return Math.max(1, this.method3967((int)this.escapeLoot.getValue3()));
   }

   private int method3967(int var1) {
      double var2 = this.lootPriority.method2101("module.autoWarden.lootPriority.low")
         ? 1.5
         : (this.lootPriority.method2101("module.autoWarden.lootPriority.high") ? 0.8 : 1.0);
      return (int)(var1 * var2);
   }

   private boolean method3968() {
      if (mc.player.age >= this.intValue3) {
         return false;
      } else {
         for (Entity var2 : mc.world.getEntities()) {
            double var4;
            if (var2 instanceof WardenEntity var3
               && (var4 = mc.player.squaredDistanceTo(var3)) < 900.0
               && this.method3970(var3)
               && (var4 < 16.0 || this.method3969(var3))) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean method3969(WardenEntity var1) {
      return (mc.player.getX() - var1.getX()) * (var1.getX() - var1.lastX) + (mc.player.getZ() - var1.getZ()) * (var1.getZ() - var1.lastZ) > 0.01;
   }

   private boolean method3970(WardenEntity var1) {
      double var2 = Math.toDegrees(Math.atan2(-(mc.player.getX() - var1.getX()), mc.player.getZ() - var1.getZ()));
      return Math.abs(((var1.getBodyYaw() - var2) % 360.0 + 540.0) % 360.0 - 180.0) < 10.0;
   }

   private boolean method3971(BlockPos var1) {
      for (BlockPos var3 : this.items4.keySet()) {
         if (var3.getSquaredDistance(var1) < 25.0) {
            return true;
         }
      }

      return false;
   }

   private void method3972(boolean var1) {
      this.method4013();
      this.method4014();
      if (this.blockPos3 == null
         || mc.player.squaredDistanceTo(Vec3d.ofCenter(this.blockPos3)) <= 16.0
         || this.method3974(this.blockPos3.getX(), this.blockPos3.getZ(), var1) < 16.0) {
         this.blockPos3 = this.method3973(var1);
         this.method4000("бегу в " + this.blockPos3);
      }

      this.method3975(this.blockPos3);
   }

   private BlockPos method3973(boolean var1) {
      BlockPos var2 = null;
      double var3 = -1.0;
      int var5 = mc.player.getBlockPos().getY();

      for (byte var6 = 0; var6 < 360; var6 += 30) {
         int var7 = this.method3980((int)(mc.player.getX() + Math.cos(Math.toRadians(var6)) * 45.0));
         int var8 = this.method3981((int)(mc.player.getZ() + Math.sin(Math.toRadians(var6)) * 45.0));
         double var9;
         if ((var9 = this.method3974(var7, var8, var1)) > var3) {
            var3 = var9;
            var2 = new BlockPos(var7, var5, var8);
         }
      }

      return var2;
   }

   private double method3974(int var1, int var2, boolean var3) {
      double var4 = Double.MAX_VALUE;

      for (Entity var7 : mc.world.getEntities()) {
         if (var7 != mc.player && (var7 instanceof PlayerEntity || var3 && var7 instanceof WardenEntity)) {
            var4 = Math.min(var4, Math.hypot(var7.getX() - var1, var7.getZ() - var2));
         }
      }

      return var4;
   }

   private void method3975(BlockPos var1) {
      if (var1 != null) {
         BlockPos var2 = new BlockPos(this.method3980(var1.getX()), var1.getY(), this.method3981(var1.getZ()));
         if (!var2.equals(this.blockPos2) || !this.method3976()) {
            this.blockPos2 = var2;
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(var2));
         }
      }
   }

   private boolean method3976() {
      boolean var10000;
      try {
         var10000 = BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath();
      } catch (Throwable var2) {
         return false;
      }

      return var10000;
   }

   private void method3977() {
      this.blockPos2 = null;
      if (BaritoneAPI.getProvider() != null) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      }
   }

   private void method3978() {
      this.box = new Box(-2070.0, mc.player.getBlockPos().getY(), -2076.0, -1921.0, mc.player.getBlockPos().getY(), -1929.0);
   }

   private boolean method3979() {
      return this.box != null
         && mc.player.getX() >= this.box.minX
         && mc.player.getX() <= this.box.maxX
         && mc.player.getZ() >= this.box.minZ
         && mc.player.getZ() <= this.box.maxZ;
   }

   private int method3980(int var1) {
      return this.box == null ? var1 : (int)Math.max(this.box.minX + 10.0, Math.min(this.box.maxX - 10.0, (double)var1));
   }

   private int method3981(int var1) {
      return this.box == null ? var1 : (int)Math.max(this.box.minZ + 10.0, Math.min(this.box.maxZ - 10.0, (double)var1));
   }

   private void method3982() {
      HandledScreen var1 = this.method4010();
      if (var1 != null) {
         this.method4005(var1);
      } else {
         BlockPos var2 = this.method3983();
         if (var2 == null) {
            this.method4000("цели нет: подходящих сундуков не нашлось");
            if (mc.player.age % 40 == 0) {
               this.type = AutoWardenModule.Type.ESCAPE;
               this.flag = true;
            }
         } else {
            double var3;
            if ((var3 = mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var2))) > 20.0) {
               if (var3 > 10.0) {
                  this.method4014();
               }

               BlockPos var8 = this.method3993(var2.up()) ? var2.up() : this.method3990(var2);
               BlockPos var6 = var8 == null ? var2.up() : var8;
               this.method4000("иду к " + var2 + " (точка " + var6 + ")");
               this.method3975(var6);
            } else {
               if (this.method4033()) {
                  this.method3977();
               }

               long var5;
               if ((var5 = PvpDetector.method2721(var2)) > 1000L && this.method4037(var2, 7.0)) {
                  this.method4000("жду " + var2 + ": рядом игрок");
               } else if (var5 > 0L) {
                  this.method4000("жду " + var2 + ": сами вычерпали, лут через " + var5 / 1000L + " сек");
               } else if (this.items2.getOrDefault(var2, 0) >= 3) {
                  this.method4000("выдоен " + var2 + ", беру следующий");
               } else if (this.method3995(mc.player.getEyePos(), var2) == null) {
                  BlockPos var7 = this.method3990(var2);
                  if (var7 != null) {
                     this.method4000("не вижу " + var2 + ", перехожу на " + var7);
                     this.method3975(var7);
                  } else {
                     this.method4000("не вижу " + var2 + " и встать негде — бросаю");
                     this.method3984(var2);
                  }
               } else {
                  if (this.method3999(var2, 1)) {
                     this.items2.merge(var2, 1, Integer::sum);
                     PvpDetector.method2723(var2);
                  }
               }
            }
         }
      }
   }

   private BlockPos method3983() {
      if (this.blockPos != null) {
         if (this.stopWatch.method7546(30000L)) {
            this.method3984(this.blockPos);
         } else {
            if (this.method3985(this.blockPos)) {
               return this.blockPos;
            }

            this.method4000(
               "бросаю цель "
                  + this.blockPos
                  + " (дист "
                  + (int)Math.sqrt(mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(this.blockPos)))
                  + "): "
                  + this.method3986(this.blockPos)
            );
            this.blockPos = null;
         }
      }

      BlockPos var1 = this.method3987();
      if (var1 != null && !var1.equals(this.blockPos)) {
         this.stopWatch.reset();
      }

      this.blockPos = var1;
      return var1;
   }

   private void method3984(BlockPos var1) {
      this.method4000("бросаю " + var1 + " на 30 сек");
      this.items3.put(var1, mc.player.age + 600);
      this.blockPos = null;
      this.stopWatch.reset();
   }

   private boolean method3985(BlockPos var1) {
      return this.method3986(var1) == null;
   }

   private String method3986(BlockPos var1) {
      if (mc.player.age < this.items3.getOrDefault(var1, -32768 << 16 | 0)) {
         return "в списке пропущенных";
      } else if (this.items2.getOrDefault(var1, 0) >= 3) {
         return "уже выдоен";
      } else {
         long var2;
         return (var2 = PvpDetector.method2721(var1)) <= 20000L ? null : "мы его вычерпали, лут через " + var2 / 1000L + " сек";
      }
   }

   private BlockPos method3987() {
      BlockPos var1 = null;
      byte var2 = 99;
      double var3 = Double.MAX_VALUE;
      BlockPos var5 = null;
      long var6 = Long.MAX_VALUE;

      for (BlockPos var9 : this.method3989()) {
         if (this.method3985(var9) && (!this.method3988(var9) || !this.method3971(var9) && !this.method4038(var9))) {
            double var11 = mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var9));
            long var13;
            if ((var13 = PvpDetector.method2721(var9)) > 0L && var13 < var6) {
               var5 = var9;
               var6 = var13;
            }

            boolean var15;
            byte var16;
            if ((var15 = var13 <= 0L) && var11 <= 144.0) {
               var16 = 0;
            } else if (var15 && var11 <= 625.0) {
               var16 = 1;
            } else if (var13 <= 20000L) {
               var16 = 2;
            } else {
               if (!var15) {
                  continue;
               }

               var16 = 3;
            }

            double var17 = Vec3d.ofCenter(var9).y - mc.player.getEyeY();
            double var19 = var9.getX() + 0.5 - mc.player.getX();
            double var21 = var9.getZ() + 0.5 - mc.player.getZ();
            double var23 = var19 * var19 + var21 * var21 + (var17 > 0.0 ? 2 : 1) * var17 * var17;
            if (var16 < var2 || var16 == var2 && var23 < var3) {
               var2 = var16;
               var3 = var23;
               var1 = var9;
            }
         }
      }

      if (var1 == null && var5 != null) {
         this.method4000("нечего лутать, иду ждать " + var5 + " (" + var6 / 1000L + " сек)");
         return var5;
      } else {
         return var1;
      }
   }

   private boolean method3988(BlockPos var1) {
      return mc.world.isChunkLoaded(var1.getX() >> 4, var1.getZ() >> 4);
   }

   private List<BlockPos> method3989() {
      var var1 = new ArrayList<>(PvpDetector.method2728());

      for (BlockPos var3 : PvpDetector.method2718()) {
         if (!var1.contains(var3) && mc.player.squaredDistanceTo(Vec3d.ofCenter(var3)) <= 6400.0) {
            var1.add(var3);
         }
      }

      return var1;
   }

   private BlockPos method3990(BlockPos var1) {
      BlockPos var2 = this.method3991(var1, 1);
      return var2 != null ? var2 : this.method3991(var1, 3);
   }

   private BlockPos method3991(BlockPos var1, int var2) {
      double var3;
      double var5 = (var3 = mc.player.getBlockInteractionRange() + 0.4) * var3;
      Vec3d var7 = Vec3d.ofCenter(var1);
      BlockPos var8 = null;
      double var9 = Double.MAX_VALUE;

      for (int var11 = -var2; var11 <= var2; var11++) {
         for (int var12 = -var2; var12 <= var2; var12++) {
            for (int var13 = -var2; var13 <= 1; var13++) {
               BlockPos var14 = var1.add(var11, var13, var12);
               double var15 = Vec3d.ofCenter(var14).squaredDistanceTo(var7);
               if (!var14.equals(var1) && !(var15 >= var9) && !(var15 > var5) && this.method3992(var14) && this.method3994(var14, var1)) {
                  var9 = var15;
                  var8 = var14;
               }
            }
         }
      }

      return var8;
   }

   private boolean method3992(BlockPos var1) {
      return this.method3993(var1) && this.method3993(var1.up()) && !this.method3993(var1.down());
   }

   private boolean method3993(BlockPos var1) {
      return mc.world.getBlockState(var1).getCollisionShape(mc.world, var1).isEmpty();
   }

   private boolean method3994(BlockPos var1, BlockPos var2) {
      Vec3d var3 = Vec3d.ofCenter(var1).add(0.0, mc.player.getEyeHeight(mc.player.getPose()) - 0.5, 0.0);
      Vec3d var4 = this.method3995(var3, var2);
      double var5 = mc.player.getBlockInteractionRange() - 0.2;
      return var4 != null && var3.squaredDistanceTo(var4) <= var5 * var5;
   }

   private Vec3d method3995(Vec3d var1, BlockPos var2) {
      double var3;
      double var5 = (var3 = mc.player.getBlockInteractionRange() - 0.15) * var3;
      Vec3d var7 = Vec3d.ofCenter(var2);
      Vec3d var8 = null;
      double var9 = Double.MAX_VALUE;

      for (double var11 = -0.4; var11 <= 0.41; var11 += 0.4) {
         for (double var13 = -0.4; var13 <= 0.41; var13 += 0.4) {
            for (double var15 = -0.4; var15 <= 0.41; var15 += 0.4) {
               Vec3d var17 = var7.add(var11, var13, var15);
               double var18;
               if (!((var18 = var1.squaredDistanceTo(var17)) >= var9)
                  && !(var18 > var5)
                  && this.method3996(var1, Rotation.method3128(var17, var1), var2) != null) {
                  var9 = var18;
                  var8 = var17;
               }
            }
         }
      }

      return var8;
   }

   private BlockHitResult method3996(Vec3d var1, Rotation var2, BlockPos var3) {
      BlockHitResult var4 = TargetUtils.method3216(
         var1,
         var2,
         mc.player.getBlockInteractionRange(),
         var1x -> var1x != null && var1x.getType() == HitResult.Type.BLOCK && var1x.getBlockPos().equals(var3)
      );
      return var4 != null && var4.getType() == HitResult.Type.BLOCK && var4.getBlockPos().equals(var3) ? var4 : null;
   }

   @EventTarget
   public void method3997(Event5 var1) {
      Rotation var2 = this.rotation;
      boolean var3 = this.humanLook.isEnabled();
      if (var2 == null && var3) {
         var2 = RotationLock.method2708();
      }

      if (var2 != null) {
         Rotation var4 = var2;
         RotationConfig var5 = var3 ? bcCameraTestnerv.getRotationConfig17() : bcCameraTestnerv.getRotationConfig();
         rotationManager.method7849(new RotationRequest(var4, () -> bcCameraTestnerv.method1851(var5, var4), var5), 4, this);
      }
   }

   private float method3998() {
      Rotation var1 = RotationLock.method2708();
      return var1 != null && this.rotation == null
         ? Math.abs(MathHelper.wrapDegrees(var1.getFloatValue() - rotationManager.method7844().getFloatValue()))
         : 0.0F;
   }

   private boolean method3999(BlockPos var1, int var2) {
      if (var1 != null && !this.method4011()) {
         Vec3d var3 = mc.player.getEyePos();
         Vec3d var4 = this.method3995(var3, var1);
         if (var4 == null) {
            this.rotation = null;
            this.method4000("interact: точку прицела не нашёл");
            return false;
         } else {
            Rotation var5 = Rotation.method3128(var4, var3);
            this.rotation = var5;
            if (mc.player.age % var2 != 0) {
               return false;
            } else {
               Rotation var6 = rotationManager.getRotation2();
               float var7;
               if ((var7 = var6.method3130(var5)) > 5.0F) {
                  this.method4000(
                     String.format(
                        "interact: ротация не доведена, промах %.1f (надо %.1f/%.1f, сервер %.1f/%.1f, current %s)",
                        var7,
                        var5.getFloatValue(),
                        var5.getFloatValue2(),
                        var6.getFloatValue(),
                        var6.getFloatValue2(),
                        rotationManager.getCurrentRotation() == null ? "null" : "есть"
                     )
                  );
                  return false;
               } else {
                  BlockHitResult var8 = this.method3996(var3, var6, var1);
                  if (var8 == null) {
                     this.method4000("interact: луч не дотянулся до " + var1);
                     return false;
                  } else {
                     mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, var8);
                     mc.player.swingHand(Hand.MAIN_HAND);
                     this.method4000("interact: ОТКРЫВАЮ " + var1);
                     return true;
                  }
               }
            }
         }
      } else {
         this.rotation = null;
         this.method4000("interact: экран открыт");
         return false;
      }
   }

   private void method4000(String var1) {
      if (this.debug.isEnabled() && !var1.equals(this.text2)) {
         this.text2 = var1;
         this.notify("[trace] " + var1);
      }
   }

   private boolean method4001(double var1) {
      for (BlockPos var4 : PvpDetector.method2728()) {
         if (mc.player.squaredDistanceTo(Vec3d.ofCenter(var4)) <= var1 * var1) {
            return true;
         }
      }

      return false;
   }

   private BlockPos method4002(boolean var1) {
      BlockPos var2 = mc.player.getBlockPos();
      Mutable var3 = new Mutable();

      for (int var4 = -4; var4 <= 4; var4++) {
         for (int var5 = -4; var5 <= 4; var5++) {
            for (int var6 = -4; var6 <= 4; var6++) {
               var3.set(var2.getX() + var4, var2.getY() + var5, var2.getZ() + var6);
               if (mc.world.getBlockState(var3).isOf(Blocks.CHEST) && this.method4003(var3) == var1 && mc.world.getBlockState(var3.up()).isAir()) {
                  BlockHitResult var7 = mc.world
                     .raycast(new RaycastContext(mc.player.getEyePos(), Vec3d.ofCenter(var3), ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
                  if (var7.getBlockPos().equals(var3)) {
                     return var3.toImmutable();
                  }
               }
            }
         }
      }

      return null;
   }

   private boolean method4003(BlockPos var1) {
      if (mc.world.getBlockState(var1.down()).isOf(Blocks.HOPPER)) {
         return true;
      } else {
         BlockState var2 = mc.world.getBlockState(var1);
         if (var2.contains(Properties.CHEST_TYPE) && var2.get(Properties.CHEST_TYPE) != ChestType.SINGLE) {
            for (Direction var4 : Direction.Type.HORIZONTAL) {
               BlockPos var5 = var1.offset(var4);
               BlockState var6 = mc.world.getBlockState(var5);
               if (var6.isOf(Blocks.CHEST)
                  && var6.get(Properties.CHEST_TYPE) != ChestType.SINGLE
                  && var6.get(Properties.CHEST_TYPE) != var2.get(Properties.CHEST_TYPE)
                  && var6.get(Properties.HORIZONTAL_FACING) == var2.get(Properties.HORIZONTAL_FACING)
                  && mc.world.getBlockState(var5.down()).isOf(Blocks.HOPPER)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private void method4004(boolean var1, boolean var2, AutoWardenModule.Type var3) {
      if (!var1) {
         if (this.method4013()) {
            if (var2 && this.type == AutoWardenModule.Type.SAVE) {
            }

            this.type = var3;
         }
      } else {
         HandledScreen var4 = this.method4010();
         if (var4 != null) {
            if (var2) {
               this.method4006(var4);
            } else {
               this.method4007(var4);
            }
         } else {
            this.method3999(this.method4002(var2), 2);
         }
      }
   }

   private void method4005(HandledScreen<?> var1) {
      if (this.method4033()) {
         this.method3977();
      } else if (mc.player.age % 2 == 0) {
         Slot var2 = this.method4008(var1, false, var1x -> !var1x.isEmpty() && !this.method4021(var1x));
         if (var2 == null) {
            this.method4013();
         } else {
            this.method4009(var1, var2, 0, SlotActionType.QUICK_MOVE);
         }
      }
   }

   private void method4006(HandledScreen<?> var1) {
      if (mc.player.age % 2 == 0) {
         boolean var2 = false;
         boolean var3 = false;
         int var4 = 0;

         for (Slot var6 : var1.getScreenHandler().slots) {
            if (var4 >= 4) {
               return;
            }

            ItemStack var7 = var6.getStack();
            if (var6.inventory == mc.player.getInventory() && !var7.isEmpty() && (!this.useSpeed.isEnabled() || !this.method4025(var7))) {
               if (!var2 && this.method4024(var7)) {
                  var2 = true;
               } else if (!var3 && var7.isOf(Items.GOLDEN_CARROT)) {
                  var3 = true;
               } else {
                  this.method4009(var1, var6, 0, SlotActionType.QUICK_MOVE);
                  var4++;
               }
            }
         }
      }
   }

   private void method4007(HandledScreen<?> var1) {
      if (mc.player.age % 2 == 0) {
         ItemStack var2 = var1.getScreenHandler().getCursorStack();
         if (!var2.isEmpty()) {
            java.util.function.Predicate<ItemStack> var3 = var1x -> var1x.isEmpty() || ItemStack.areItemsAndComponentsEqual(var1x, var2);
            if (this.method4019(var2)) {
               this.method4009(var1, this.method4008(var1, true, var3), 1, SlotActionType.PICKUP);
            } else {
               this.method4009(var1, this.method4008(var1, false, var3), 0, SlotActionType.PICKUP);
            }
         } else {
            this.method4009(var1, this.method4008(var1, false, this::method4019), 0, SlotActionType.PICKUP);
         }
      }
   }

   private Slot method4008(HandledScreen<?> var1, boolean var2, Predicate<ItemStack> var3) {
      for (Slot var5 : var1.getScreenHandler().slots) {
         if (var5.inventory == mc.player.getInventory() == var2 && var3.test(var5.getStack())) {
            return var5;
         }
      }

      return null;
   }

   private void method4009(HandledScreen<?> var1, Slot var2, int var3, SlotActionType var4) {
      if (var2 != null) {
         mc.interactionManager.clickSlot(var1.getScreenHandler().syncId, var2.id, var3, var4, mc.player);
      }
   }

   private HandledScreen<?> method4010() {
      return mc.currentScreen instanceof HandledScreen var1
            && !(mc.currentScreen instanceof InventoryScreen)
            && !(mc.player.currentScreenHandler instanceof PlayerScreenHandler)
         ? var1
         : null;
   }

   private boolean method4011() {
      return this.method4010() != null;
   }

   private boolean method4012() {
      HandledScreen var1 = this.method4010();
      return var1 != null && !var1.getScreenHandler().getCursorStack().isEmpty();
   }

   private boolean method4013() {
      if (this.method4011() && mc.player.age % 2 == 0) {
         mc.player.closeHandledScreen();
      }

      return !this.method4011();
   }

   private void method4014() {
      if (!mc.player.getMainHandStack().isEmpty()) {
         for (int var1 = 0; var1 < 9; var1++) {
            if (mc.player.getInventory().getStack(var1).isEmpty()) {
               mc.player.getInventory().setSelectedSlot(var1);
               return;
            }
         }

         for (int var2 = 9; var2 < 36; var2++) {
            if (mc.player.getInventory().getStack(var2).isEmpty()) {
               if (mc.player.age % 10 >= 2 && this.method4033()) {
                  this.method3977();
               }

               if (mc.player.age % 10 == 4) {
                  InventoryUtils.method904(var2, mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, false);
               }

               return;
            }
         }
      }
   }

   private int method4015(Predicate<ItemStack> var1) {
      for (int var2 = 0; var2 < 36; var2++) {
         if (var1.test(mc.player.getInventory().getStack(var2))) {
            return var2;
         }
      }

      return -1;
   }

   private int method4016() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (!mc.player.getInventory().getStack(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   private int method4017() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (this.method4024(mc.player.getInventory().getStack(var2))) {
            var1++;
         }
      }

      if (this.method4024(mc.player.getOffHandStack())) {
         var1++;
      }

      return var1;
   }

   private boolean method4018() {
      return this.method4017() < 1 || InventoryUtils.method920(Items.GOLDEN_CARROT) < 3 || this.useSpeed.isEnabled() && this.method4015(this::method4025) < 0;
   }

   private boolean method4019(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else if (this.method4024(var1) && this.method4017() < 1) {
         return true;
      } else {
         return var1.isOf(Items.GOLDEN_CARROT) && InventoryUtils.method920(Items.GOLDEN_CARROT) < 3
            ? true
            : this.useSpeed.isEnabled() && this.method4025(var1) && this.method4015(this::method4025) < 0;
      }
   }

   private boolean method4020() {
      boolean var1 = false;
      boolean var2 = false;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && (!this.useSpeed.isEnabled() || !this.method4025(var4))) {
            if (!var1 && this.method4024(var4)) {
               var1 = true;
            } else {
               if (var2 || !var4.isOf(Items.GOLDEN_CARROT)) {
                  return true;
               }

               var2 = true;
            }
         }
      }

      return false;
   }

   private boolean method4021(ItemStack var1) {
      return this.lootPriority.method2101("module.autoWarden.lootPriority.low")
         ? false
         : this.method4023(var1) || this.lootPriority.method2101("module.autoWarden.lootPriority.high") && this.method4022(var1);
   }

   private boolean method4022(ItemStack var1) {
      Item var2 = var1.getItem();
      return var2 instanceof ArrowItem
         || Util10.method2177(var2.getDefaultStack())
         || var2 instanceof AxeItem
         || var1.isOf(Items.CHORUS_FRUIT)
         || var1.isOf(Items.DISC_FRAGMENT_5)
         || var1.isOf(Items.NAUTILUS_SHELL)
         || var1.isOf(Items.BOOKSHELF)
         || var1.isOf(Items.COOKED_MUTTON)
         || var1.isOf(Items.SKELETON_SPAWN_EGG)
         || var1.isOf(Items.CREEPER_SPAWN_EGG)
         || var1.isOf(Items.ZOMBIE_SPAWN_EGG)
         || var1.isOf(Items.VINDICATOR_SPAWN_EGG)
         || var1.isOf(Items.PIGLIN_SPAWN_EGG)
         || var1.isOf(Items.FIRE_CHARGE)
         || var1.isOf(Items.LEATHER)
         || var1.isOf(Items.SHULKER_SHELL)
         || var1.isOf(Items.EXPERIENCE_BOTTLE)
         || var1.isOf(Items.WITHER_ROSE)
         || var1.isOf(Items.EMERALD)
         || var1.isOf(Items.SUGAR)
         || InventoryUtils.method918(var1, "potion-popper")
         || var1.contains(DataComponentTypes.JUKEBOX_PLAYABLE)
         || var1.isOf(Items.GHAST_TEAR)
         || var1.isOf(Items.DRAGON_BREATH)
         || var1.isOf(Items.VEX_SPAWN_EGG)
         || var1.isOf(Items.ENDERMITE_SPAWN_EGG)
         || var1.isOf(Items.CAT_SPAWN_EGG)
         || var1.isOf(Items.ENCHANTING_TABLE)
         || var1.isOf(Items.DIAMOND_HELMET)
         || var1.isOf(Items.DIAMOND_CHESTPLATE)
         || var1.isOf(Items.DIAMOND_LEGGINGS)
         || var1.isOf(Items.DIAMOND_BOOTS);
   }

   private boolean method4023(ItemStack var1) {
      Item var2 = var1.getItem();
      return var2 instanceof ShovelItem
         || var2 instanceof AxeItem
         || var2 instanceof BannerItem
         || var2 instanceof SmithingTemplateItem && !var1.isOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
         || var1.isOf(Items.BLAZE_ROD)
         || var1.isOf(Items.ENCHANTED_BOOK)
         || var1.isOf(Items.TRIDENT)
         || var1.isOf(Items.NAME_TAG)
         || var1.isOf(Items.SCULK)
         || var1.isOf(Items.SCULK_SENSOR)
         || var1.isOf(Items.ENDER_CHEST)
         || var1.isOf(Items.REINFORCED_DEEPSLATE)
         || var1.isOf(Items.PUFFERFISH)
         || var1.isOf(Items.HONEY_BOTTLE)
         || var1.isOf(Items.FERMENTED_SPIDER_EYE)
         || var1.isOf(Items.ANVIL)
         || var1.isOf(Items.COOKED_PORKCHOP);
   }

   private boolean method4024(ItemStack var1) {
      return this.method4026(var1, StatusEffects.INVISIBILITY);
   }

   private boolean method4025(ItemStack var1) {
      return this.method4026(var1, StatusEffects.SPEED);
   }

   private boolean method4026(ItemStack var1, RegistryEntry<StatusEffect> var2) {
      if (!var1.isOf(Items.POTION)) {
         return false;
      } else {
         PotionContentsComponent var3 = var1.get(DataComponentTypes.POTION_CONTENTS);
         if (var3 == null) {
            return false;
         } else {
            for (StatusEffectInstance var5 : var3.getEffects()) {
               if (var5.getEffectType().equals(var2)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private boolean method4027() {
      return this.intValue4 != 0;
   }

   private void method4028(Predicate<ItemStack> var1) {
      if (this.intValue4 == 0 && mc.player.age > 20) {
         if (this.method4011()) {
            this.method4013();
         } else {
            Slot var2 = InventoryUtils.method910(var1x -> var1x.inventory == mc.player.getInventory() && var1.test(var1x.getStack()));
            if (var2 != null) {
               this.method3977();
               this.intValue5 = var2.id;
               InventoryUtils.method900(var2, Hand.OFF_HAND, false);
               this.intValue4 = 1;
               this.stopWatch2.reset();
            }
         }
      }
   }

   private boolean method4029() {
      if (this.intValue4 == 0) {
         return false;
      } else if (this.intValue4 == 3) {
         return this.method4031();
      } else if (this.method4011()) {
         this.method4030();
         return true;
      } else {
         this.method3977();
         if (this.intValue4 == 1) {
            if (!this.stopWatch2.method7546(150L)) {
               return true;
            }

            this.intValue4 = 2;
            this.stopWatch2.reset();
         }

         mc.options.sneakKey.setPressed(true);
         mc.options.useKey.setPressed(true);
         if (this.stopWatch2.method7546(2200L)) {
            this.method4030();
         }

         return true;
      }
   }

   private void method4030() {
      this.method4032();
      this.intValue4 = this.intValue5 >= 0 ? 3 : 0;
      this.stopWatch2.reset();
   }

   private boolean method4031() {
      if (this.method4011()) {
         this.method4013();
         return true;
      } else if (!mc.player.getOffHandStack().isEmpty() && !this.stopWatch2.method7546(3000L)) {
         if (this.stopWatch2.method7546(100L)) {
            InventoryUtils.method904(this.intValue5, 40, SlotActionType.SWAP, false);
            this.intValue5 = -1;
            this.intValue4 = 0;
            this.stopWatch2.reset();
         }

         return true;
      } else {
         this.intValue5 = -1;
         this.intValue4 = 0;
         this.stopWatch2.reset();
         return false;
      }
   }

   private void method4032() {
      mc.options.useKey.setPressed(false);
      mc.options.sneakKey.setPressed(false);
   }

   private boolean method4033() {
      return mc.player.getVelocity().horizontalLengthSquared() > 0.0025;
   }

   private boolean method4034() {
      return !mc.world.getBlockState(mc.player.getBlockPos()).isIn(BlockTags.CANDLES)
            && !mc.world.getBlockState(mc.player.getBlockPos().down()).isIn(BlockTags.CANDLES)
         ? !this.method3968()
            && this.type == AutoWardenModule.Type.COLLECTING
            && PvpDetector.method2727()
            && mc.currentScreen == null
            && !this.method4033()
            && this.intValue4 == 0
            && this.method4035()
         : true;
   }

   private boolean method4035() {
      Box var1 = mc.player.getBoundingBox().expand(0.05, 0.0, 0.05);

      for (BlockPos var3 : BlockPos.iterate(BlockPos.ofFloored(var1.minX, var1.minY, var1.minZ), BlockPos.ofFloored(var1.maxX, var1.maxY, var1.maxZ))) {
         if (!mc.world.getBlockState(var3).isAir()) {
            return true;
         }
      }

      return false;
   }

   private boolean method4036(double var1) {
      for (PlayerEntity var4 : mc.world.getPlayers()) {
         if (var4 != mc.player && mc.player.squaredDistanceTo(var4) < var1 * var1) {
            return true;
         }
      }

      return false;
   }

   private boolean method4037(BlockPos var1, double var2) {
      for (PlayerEntity var5 : mc.world.getPlayers()) {
         if (var5 != mc.player && var5.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(var1)) < var2 * var2) {
            return true;
         }
      }

      return false;
   }

   private boolean method4038(BlockPos var1) {
      for (PlayerEntity var3 : mc.world.getPlayers()) {
         if (var3 != mc.player
            && !(var3.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(var1)) >= 20.0)
            && Stream.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
               .anyMatch(var1x -> Util10.method2181(var3.getEquippedStack(var1x)))) {
            return true;
         }
      }

      return false;
   }

   private void method4039() {
      this.notify(
         "состояние "
            + this.type
            + " | анархия "
            + PvpDetector.method2724()
            + ", нужна "
            + this.method3955()
            + " (список: "
            + this.method3950().size()
            + ") | в зоне фермы "
            + PvpDetector.method2727()
            + " | сундуков "
            + PvpDetector.method2728().size()
            + " (в памяти "
            + PvpDetector.method2718().size()
            + ") | варден "
            + this.method3968()
            + " | пвп "
            + PvpDetector.method2725()
            + " ("
            + PvpDetector.method2726()
            + ") | зелий инвиза "
            + this.method4017()
            + ", эффект "
            + this.method3962()
            + " сек, морковь "
            + InventoryUtils.method920(Items.GOLDEN_CARROT)
            + " | слотов "
            + this.method4016()
            + "/"
            + this.method3966()
            + ", голод "
            + mc.player.getHungerManager().getFoodLevel()
      );
      this.notify(
         "застрял "
            + this.method4034()
            + " | двигаюсь "
            + this.method4033()
            + " | в блоке "
            + this.method4035()
            + " | лут "
            + this.method4020()
            + " | цель "
            + (this.blockPos == null ? "нет" : this.blockPos)
            + " | "
            + this.method4040()
            + " | приёмник в руке "
            + (this.method4002(true) == null ? "нет" : "есть")
            + " | ближайший приёмник "
            + (this.method4041() == null ? "нет" : "есть")
      );
   }

   private String method4040() {
      BlockPos var1 = this.blockPos;
      if (var1 == null) {
         return "цели нет";
      } else {
         Vec3d var2 = mc.player.getEyePos();
         Vec3d var3 = this.method3995(var2, var1);
         String var4 = "-";
         if (var3 != null) {
            Rotation var5 = Rotation.method3128(var3, var2);
            var4 = String.format("%.1f", rotationManager.getRotation2().method3130(var5));
         }

         return "вижу "
            + (var3 != null)
            + " | откр "
            + this.items2.getOrDefault(var1, 0)
            + "/3 | ост "
            + PvpDetector.method2729(var1)
            + " | дист "
            + (int)Math.sqrt(var2.squaredDistanceTo(Vec3d.ofCenter(var1)))
            + " | угол "
            + var4;
      }
   }

   private BlockPos method4041() {
      BlockPos var1 = mc.player.getBlockPos();
      Mutable var2 = new Mutable();
      BlockPos var3 = null;
      double var4 = Double.MAX_VALUE;

      for (int var6 = -16; var6 <= 16; var6++) {
         for (int var7 = -6; var7 <= 6; var7++) {
            for (int var8 = -16; var8 <= 16; var8++) {
               var2.set(var1.getX() + var6, var1.getY() + var7, var1.getZ() + var8);
               double var9;
               if (mc.world.getBlockState(var2).isOf(Blocks.CHEST)
                  && this.method4003(var2)
                  && mc.world.getBlockState(var2.up()).isAir()
                  && (var9 = mc.player.squaredDistanceTo(Vec3d.ofCenter(var2))) < var4) {
                  var4 = var9;
                  var3 = var2.toImmutable();
               }
            }
         }
      }

      return var3;
   }

   private void notify(String var1) {
      ChatLogger.method2019("Auto Warden: " + var1);
   }

   private static enum Type {
      SAVE,
      TAKE,
      COLLECTING,
      ESCAPE;
   }
}

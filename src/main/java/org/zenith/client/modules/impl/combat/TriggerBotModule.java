package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.NeuroClicks;
import org.zenith.utility.game.AttackUtils;
import org.zenith.utility.game.AuraUtil;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "TriggerBot",
   category = ModuleCategory.COMBAT,
   description = "module.triggerbot.description"
)
public final class TriggerBotModule extends Module {
   public static final TriggerBotModule INSTANCE = new TriggerBotModule();
   private final GroupSetting safeTarget = new GroupSetting(
      "module.aura.targetWindow",
      "module.aura.targetWindow.desc",
      () -> true,
      MultiSelectSetting.method5224(
         "module.aura.targetTypeSetting",
         "module.aura.targetTypeSetting.desc",
         List.of("module.aura.targetPlayers", "module.aura.noarmor", "module.aura.targetHostile", "module.aura.targetPeaceful")
      ),
      MultiSelectSetting.method5224(
         "module.aura.targetSortSetting",
         "module.aura.targetSortSetting.desc",
         List.of("module.aura.targetFov", "module.aura.targetArmor", "module.aura.targetHp", "module.aura.targetDistance")
      ),
      new BooleanSetting("module.aura.safeTarget", "module.aura.safeTarget.desc", false)
   );
   private final SliderSetting distance = new SliderSetting("module.aura.distance", 3.0F, 0.5F, 6.0F, 0.1F, "module.aura.distance.desc", "b");
   private final BooleanSetting onlyCrit = new BooleanSetting("module.aura.onlyCrit", "module.aura.onlyCrit.desc", true);
   private final BooleanSetting smartCrit = new BooleanSetting("module.aura.smartCrit", "module.aura.smartCrit.desc", false, this.onlyCrit::isEnabled);
   private final ModeSetting cooldownMode = new ModeSetting("module.aura.cooldownMode", "module.aura.cooldownMode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.cooldownMode, "module.aura.slowMode");
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.cooldownMode, "module.aura.speedMode").method2103();
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.cooldownMode, "module.aura.customMode");
   private final ModeSetting.Entry entry4 = new ModeSetting.Entry(this.cooldownMode, "module.triggerbot.neuroMode");
   private final ModeSetting neuroProfile = new ModeSetting("module.triggerbot.neuroProfile", "module.triggerbot.neuroProfile.desc", this.entry4::isSelected);
   private final ModeSetting.Entry entry5 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileStrong").method2103();
   private final ModeSetting.Entry entry6 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileEarly");
   private final ModeSetting.Entry entry7 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileExact");
   private final ModeSetting.Entry entry8 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileRelaxed");
   private final SliderSetting timeCooldown = new SliderSetting(
      "module.aura.timeCooldown", 0.0F, -1.0F, 1.0F, 0.5F, "module.aura.timeCooldown.desc", "t", this.entry3::isSelected, null
   );
   private final SliderSetting attackCooldown = new SliderSetting(
      "module.aura.attackCooldown", 1.0F, 0.0F, 1.0F, 0.01F, "module.aura.attackCooldown.desc", "%", this.entry3::isSelected, null
   );
   private final BooleanSetting randomDelay = new BooleanSetting("module.aura.randomDelay", "module.aura.randomDelay.desc", false);
   private final ModeSetting sprintMode = new ModeSetting("module.aura.sprintMode", "module.aura.sprintMode.desc");
   private final ModeSetting.Entry entry9 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintHvh");
   private final ModeSetting.Entry entry10 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNormal").method2103();
   private final ModeSetting.Entry entry11 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintLegit");
   private final ModeSetting.Entry entry12 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintSuperLegit");
   private final ModeSetting.Entry entry13 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNone");
   private final ModeSetting.Entry entry14 = new ModeSetting.Entry(this.sprintMode, "module.triggerbot.sprintNeuro");
   private final BooleanSetting neuroWtap = new BooleanSetting(
      "module.triggerbot.neuroWtap", "module.triggerbot.neuroWtap.desc", false, this.entry14::isSelected
   );
   private final MultiSelectSetting settings = new MultiSelectSetting("module.aura.settings", "module.aura.settings.desc");
   private final MultiSelectSetting.Entry entry15 = new MultiSelectSetting.Entry(this.settings, "module.aura.shieldBreak", true);
   private final MultiSelectSetting.Entry entry16 = new MultiSelectSetting.Entry(this.settings, "module.aura.shielRealese", true);
   private final MultiSelectSetting.Entry entry17 = new MultiSelectSetting.Entry(this.settings, "module.aura.eatUseAttack", true);
   private final MultiSelectSetting.Entry entry18 = new MultiSelectSetting.Entry(this.settings, "module.aura.attackIgnoreWals", true);
   private LivingEntity livingEntity = null;
   private int intValue = -1;
   private LivingEntity livingEntity2 = null;
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag = false;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private StopWatch stopWatch2;
   private final NeuroClicks neuroClicks = NeuroClicks.INSTANCE;
   public int intValue2 = 0;
   public int intValue3 = 0;

   private TriggerBotModule() {
   }

   @EventTarget
   public void method5574(EntityAddEvent var1) {
      if (var1.getEntity() instanceof ClientPlayerEntity) {
         this.livingEntity = null;
         this.livingEntity2 = null;
      }
   }

   @EventTarget
   public void method5575(Event2 var1) {
      if (this.livingEntity != null) {
         if (this.flag2 && this.method5580() && (!this.method5577() || this.neuroClicks.method451(this.method5591(), this.method5578()))) {
            this.flag = false;
            this.flag2 = false;
            this.flag3 = true;
         }
      }
   }

   @EventTarget
   public void method5576(UpdateEvent var1) {
      if (this.stopWatch2 == null) {
         this.stopWatch2 = new StopWatch();
      }

      if (mc.player.isDead() || this.livingEntity == null) {
         this.flag3 = false;
      }

      if (this.flag3) {
         if (this.entry9.isSelected() && Zenith.getInstance().getServerDetector().isFlag()) {
            mc.player.setSprinting(false);
            mc.player.sendSprintingPacket();
         }

         boolean var2 = Zenith.getInstance().getServerDetector().isFlag();
         AttackUtils.method816(this.livingEntity);
         this.intValue2 = mc.player.age - this.intValue3;
         this.intValue3 = mc.player.age;
         if (this.entry14.isSelected() && this.neuroClicks.method442()) {
            this.neuroClicks.method452(var2, mc.options.forwardKey.isPressed(), this.neuroWtap.isEnabled());
         }
      }
   }

   private boolean method5577() {
      return this.entry4.isSelected() && this.neuroClicks.method442();
   }

   private String method5578() {
      if (this.entry6.isSelected()) {
         return "early";
      } else if (this.entry7.isSelected()) {
         return "exact";
      } else {
         return this.entry8.isSelected() ? "relaxed" : "strong";
      }
   }

   @EventTarget
   public void method5579(Event5 var1) {
      this.flag3 = false;
      if (mc.player.isDead()) {
         this.livingEntity = null;
         this.neuroClicks.reset();
      } else {
         if ((this.entry4.isSelected() || this.entry14.isSelected()) && this.neuroClicks.method442()) {
            this.neuroClicks.method447();
         }

         this.livingEntity = this.method5597();
         if (mc.targetedEntity instanceof LivingEntity var2
            && AuraUtil.method7447(((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(), var2)) {
            this.livingEntity = var2;
         }

         if (this.livingEntity != null) {
            if (mc.world.getEntityById(this.livingEntity.getId()) instanceof LivingEntity var4) {
               this.livingEntity = var4;
            } else {
               this.livingEntity = null;
            }

            this.livingEntity2 = this.livingEntity;
         }

         this.flag = false;
         this.flag2 = false;
         if (this.livingEntity == null) {
            this.method5581();
         } else {
            this.flag = this.method5589();
            this.flag2 = this.method5592();
            int var5 = InventoryUtils.method922(var0 -> mc.player.getInventory().getStack(var0).getItem() instanceof AxeItem);
            if (this.entry15.isEnabled()
               && this.stopWatch.method7546(300L)
               && Zenith.getInstance().getTaskScheduler().method5117(TriggerBotModule.class)
               && var5 != -1
               && UseItemUtils.method6530(this.livingEntity)) {
               if (mc.player.getMainHandStack().getItem() instanceof AxeItem) {
                  if (this.method5580()) {
                     if (this.entry16.isEnabled() && mc.player.isUsingItem() && mc.player.getActiveItem().getItem() instanceof ShieldItem) {
                        mc.interactionManager.stopUsingItem(mc.player);
                     }

                     AttackUtils.method816(this.livingEntity);
                     this.stopWatch.reset();
                     this.flag = false;
                     this.flag2 = false;
                  }
               } else {
                  if (this.intValue == -1) {
                     this.intValue = mc.player.getInventory().getSelectedSlot();
                  }

                  mc.player.getInventory().setSelectedSlot(var5);
               }
            } else {
               this.method5581();
            }

            if (this.flag && this.entry16.isEnabled() && mc.player.isUsingItem() && mc.player.getActiveItem().getItem() instanceof ShieldItem) {
               mc.interactionManager.stopUsingItem(mc.player);
            }
         }
      }
   }

   public boolean method5580() {
      return this.method5582(rotationManager.method7844());
   }

   private void method5581() {
      if (this.intValue != -1) {
         mc.player.getInventory().setSelectedSlot(this.intValue);
         this.intValue = -1;
      }
   }

   public boolean method5582(Rotation var1) {
      return this.method5583(var1, this.method5584(), this.livingEntity.getBoundingBox());
   }

   public boolean method5583(Rotation var1, Vec3d var2, Box var3) {
      return TargetUtils.method3212(var1, var2, var3, this.distance.getValue3(), !this.entry18.isEnabled());
   }

   public Vec3d method5584() {
      return mc.player.getCameraPosVec(1.0F);
   }

   public Vec3d method5585(Box var1, float var2) {
      return new Vec3d(
         MathHelper.lerp(0.5, var1.minX, var1.maxX), MathHelper.lerp((double)var2, var1.minY, var1.maxY), MathHelper.lerp(0.5, var1.minZ, var1.maxZ)
      );
   }

   public Box method5586() {
      return this.livingEntity.getBoundingBox();
   }

   @EventTarget
   public void method5587(MoveInputEvent var1) {
      if (this.livingEntity != null) {
         this.method5594(var1);
      }
   }

   @EventTarget(0)
   public void method5588(Event9 var1) {
      if (this.flag3) {
         var1.cancel();
      }
   }

   private boolean method5589() {
      return this.method5590(1);
   }

   private boolean method5590(int var1) {
      if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem()
         && (!this.entry16.isEnabled() || !(mc.player.getActiveItem().getItem() instanceof ShieldItem))
         && !this.entry17.isEnabled()) {
         return false;
      } else {
         if (this.onlyCrit.isEnabled()) {
            PlayerSimulation var2 = PlayerSimulation.method6031(var1);
            if (!AttackUtils.method818(var2) && !AttackUtils.method822(var2) && (!this.smartCrit.isEnabled() || mc.options.jumpKey.isPressed())) {
               return false;
            }
         }

         return this.method5593(1.0F, !this.method5591());
      }
   }

   public boolean method5591() {
      if (mc.player.isGliding() || AttackUtils.method817()) {
         return false;
      } else {
         return !this.onlyCrit.isEnabled() ? false : !this.smartCrit.isEnabled() || mc.options.jumpKey.isPressed();
      }
   }

   public boolean method5592() {
      if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem() && !this.entry17.isEnabled()) {
         return false;
      } else if (!this.method5591()
         || AttackUtils.method819() && (!Zenith.getInstance().getServerDetector().isFlag() || this.entry13.isSelected() || this.entry9.isSelected())) {
         long var1;
         return this.randomDelay.isEnabled()
               && !this.method5577()
               && MathUtils.method2650((float)(var1 = mc.player.age - this.intValue3), this.intValue2) == 0.0F
            ? false
            : this.method5593(0.0F, !this.method5591() || this.flag);
      } else {
         return false;
      }
   }

   public boolean method5593(float var1, boolean var2) {
      if (this.method5577()) {
         return this.neuroClicks.method450(this.method5591(), this.method5578(), (int)var1);
      } else if (this.cooldownMode.isSelected(1)) {
         return !AttackUtils.method817() && !mc.player.isOnGround()
            ? mc.player.getAttackCooldownProgress(0.5F) > 0.9
            : mc.player.getAttackCooldownProgress(0.5F) >= 1.0F;
      } else {
         if (!this.entry.isSelected()) {
            var2 = false;
         }

         float var3 = this.entry3.isSelected() ? this.timeCooldown.getValue3() : 1.0F;
         float var4 = this.entry3.isSelected() ? this.attackCooldown.getValue3() : 1.0F;
         return var2 && !mc.player.isSubmergedInWater()
            ? mc.player.getAttackCooldownProgress(0.0F + var1) == 1.0F
            : mc.player.getAttackCooldownProgress(var3 + var1) >= var4;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.intValue != -1 && mc.player != null) {
         mc.player.getInventory().setSelectedSlot(this.intValue);
      }

      this.intValue = -1;
      this.flag3 = false;
      this.flag = false;
      this.flag2 = false;
      this.livingEntity = null;
      this.livingEntity2 = null;
      this.neuroClicks.reset();
      super.onDisable();
   }

   public void method5594(MoveInputEvent var1) {
      if (this.method5596()) {
         if (this.entry14.isSelected()) {
            if (this.neuroClicks.method442()) {
               boolean var6 = var1.getPlayerInput().forward();
               boolean var7 = mc.player.isSprinting() || Zenith.getInstance().getServerDetector().isFlag();
               boolean var4 = this.method5591() && !mc.player.isOnGround() && this.method5589();
               boolean var5;
               if (!(var5 = this.neuroClicks.method453(var6, var7, var4)) && var6) {
                  var1.method3038(false);
               }

               this.neuroClicks.method454(true, var6, var5, var7, this.intValue3 == mc.player.age);
            }
         } else {
            boolean var2 = mc.options.sprintKey.isPressed();
            boolean var3 = mc.options.forwardKey.isPressed();
            if (this.entry12.isSelected()) {
               if (!mc.player.isOnGround()) {
                  var2 = false;
                  if (mc.player.isSprinting()) {
                     var3 = false;
                  }
               }
            } else if (!this.flag3 && !mc.player.isOnGround() && this.method5589()) {
               if (this.entry11.isSelected()) {
                  var2 = false;
                  if (mc.player.isSprinting()) {
                     var3 = false;
                  }
               }

               if (this.entry10.isSelected()) {
                  if (mc.player.isSprinting()) {
                     mc.player.setSprinting(false);
                  }

                  var2 = false;
               }
            }

            if (!var2) {
               mc.options.sprintKey.setPressed(false);
               var1.method3039(false);
            }

            if (!var3) {
               var1.method3038(false);
            }
         }
      }
   }

   @EventTarget
   public void method5595(SprintUpdateEvent var1) {
      if (this.livingEntity != null && this.entry10.isSelected() && !this.flag3 && !mc.player.isOnGround() && this.method5589()) {
         var1.setFlag2(false);
      }
   }

   public boolean method5596() {
      return !this.entry13.isSelected() && !AttackUtils.method817();
   }

   private LivingEntity method5597() {
      return AuraUtil.method7446(
         mc.world.getEntities(),
         this.distance.getValue3() + 0.5F,
         this.entry18.isEnabled(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(1)).method5229(),
         ((BooleanSetting)this.safeTarget.getSettings().get(2)).isEnabled()
      );
   }

   public LivingEntity method5598() {
      return this.isEnabled() ? this.livingEntity : null;
   }

   @Generated
   public LivingEntity getLivingEntity2() {
      return this.livingEntity2;
   }

   @Generated
   public boolean isFlag8() {
      return this.flag;
   }

   @Generated
   public boolean isFlag25() {
      return this.flag2;
   }

   @Generated
   public StopWatch getStopWatch23() {
      return this.stopWatch2;
   }
}

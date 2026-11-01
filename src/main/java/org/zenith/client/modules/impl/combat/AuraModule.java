package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.base.rotation.mods.Class24;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.SendPacketEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.movement.WallBypassModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.AttackUtils;
import org.zenith.utility.game.AuraRandomizer;
import org.zenith.utility.game.AuraUtil;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationConfig;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "Aura",
   category = ModuleCategory.COMBAT,
   description = "module.aura.description"
)
public final class AuraModule extends Module {
   public static final AuraModule INSTANCE = new AuraModule();
   private final GroupSetting safeTarget = new GroupSetting(
      "module.aura.targetSettingWindow",
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
      new BooleanSetting("module.aura.safeTarget", "module.aura.safeTarget.desc", true)
   );
   private final SliderSetting distance = new SliderSetting("module.aura.distance", 3.0F, 0.5F, 6.0F, 0.1F, "module.aura.distance.desc", "b");
   private final SliderSetting distanceRotation = new SliderSetting(
      "module.aura.distanceRotation", 0.1F, 0.0F, 6.0F, 0.1F, "module.aura.distanceRotation.desc", "b"
   );
   private final BooleanSetting onlyCrit = new BooleanSetting("module.aura.onlyCrit", "module.aura.onlyCrit.desc", true);
   private final BooleanSetting smartCrit = new BooleanSetting("module.aura.smartCrit", "module.aura.smartCrit.desc", false, this.onlyCrit::isEnabled);
   private final ModeSetting cooldownMode = new ModeSetting("module.aura.cooldownMode", "module.aura.cooldownMode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.cooldownMode, "module.aura.slowMode");
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.cooldownMode, "module.aura.speedMode").method2103();
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.cooldownMode, "module.aura.customMode");
   private final SliderSetting timeCooldown = new SliderSetting(
      "module.aura.timeCooldown", 0.0F, -1.0F, 1.0F, 0.5F, "module.aura.timeCooldown.desc", "t", this.entry3::isSelected, null
   );
   private final SliderSetting attackCooldown = new SliderSetting(
      "module.aura.attackCooldown", 1.0F, 0.0F, 1.0F, 0.01F, "module.aura.attackCooldown.desc", "%", this.entry3::isSelected, null
   );
   private final BooleanSetting tpsSynch = new BooleanSetting("module.aura.tpsSynch", "module.aura.tpsSynch.desc", false);
   private final ModeSetting rotationMode = new ModeSetting("module.aura.rotationMode", "module.aura.rotationMode.desc");
   private final ModeSetting.Entry entry4 = new ModeSetting.Entry(this.rotationMode, "HolyWorld").method2103();
   private final ModeSetting.Entry entry5 = new ModeSetting.Entry(this.rotationMode, "FunTime");
   private final ModeSetting.Entry entry6 = new ModeSetting.Entry(this.rotationMode, "SpokyTime");
   private final ModeSetting.Entry entry7 = new ModeSetting.Entry(this.rotationMode, "module.aura.legit");
   private final ModeSetting.Entry entry8 = new ModeSetting.Entry(this.rotationMode, "module.aura.snap");
   private final ModeSetting.Entry entry9 = new ModeSetting.Entry(this.rotationMode, "module.aura.hvh");
   private final ModeSetting.Entry entry10 = new ModeSetting.Entry(this.rotationMode, "ReallyWorld");
   private final ModeSetting.Entry entry11 = new ModeSetting.Entry(this.rotationMode, "NeuroBC");
   private final ModeSetting.Entry entry12 = new ModeSetting.Entry(this.rotationMode, "NeuroBC-2");
   private final ModeSetting.Entry entry13 = new ModeSetting.Entry(this.rotationMode, "TestNerv");
   private final SliderSetting sliderSetting = new SliderSetting(
      "TestNerv удержание", 50.0F, 0.0F, 100.0F, 5.0F, "Доля времени прицела на цели (0 — слабый, 100 — профи)", "%", this::method6360
   );
   private final ModeSetting.Entry entry14 = new ModeSetting.Entry(this.rotationMode, "NeuroBC-Hand");
   private final ModeSetting legitMode = new ModeSetting("module.aura.legitMode", "module.aura.legitMode.desc", this.entry7::isSelected);
   private final ModeSetting.Entry entry15 = new ModeSetting.Entry(this.legitMode, "module.aura.legitSnap").method2103();
   private final ModeSetting.Entry entry16 = new ModeSetting.Entry(this.legitMode, "module.aura.legitJerk");
   private final ModeSetting.Entry entry17 = new ModeSetting.Entry(this.legitMode, "module.aura.legitConstant");
   private final SliderSetting legitMaxAngle = new SliderSetting(
      "module.aura.legitMaxAngle", 5.0F, 0.5F, 180.0F, 0.5F, "module.aura.legitMaxAngle.desc", "°", this.entry7::isSelected, null
   );
   private final ModeSetting sprintMode = new ModeSetting("module.aura.sprintMode", "module.aura.sprintMode.desc");
   private final ModeSetting.Entry entry18 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintHvh");
   private final ModeSetting.Entry entry19 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNormal").method2103();
   private final ModeSetting.Entry entry20 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintLegit");
   private final ModeSetting.Entry entry21 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintSuperLegit");
   private final ModeSetting.Entry entry22 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNone");
   private final ModeSetting correction = new ModeSetting("module.aura.correction", "module.aura.correction.desc");
   private final ModeSetting.Entry entry23 = new ModeSetting.Entry(this.correction, "module.aura.correctionFocus");
   private final ModeSetting.Entry entry24 = new ModeSetting.Entry(this.correction, "module.aura.correctionGood").method2103();
   private final ModeSetting.Entry entry25 = new ModeSetting.Entry(this.correction, "module.aura.correctionNone");
   private final MultiSelectSetting settings = new MultiSelectSetting("module.aura.settings", "module.aura.settings.desc");
   private final MultiSelectSetting.Entry entry26 = new MultiSelectSetting.Entry(this.settings, "module.aura.shieldBreak", true);
   private final MultiSelectSetting.Entry entry27 = new MultiSelectSetting.Entry(this.settings, "module.aura.shielRealese", true);
   private final MultiSelectSetting.Entry entry28 = new MultiSelectSetting.Entry(this.settings, "module.aura.eatUseAttack", true);
   private final MultiSelectSetting.Entry entry29 = new MultiSelectSetting.Entry(this.settings, "module.aura.attackIgnoreWals", true);
   private final ModeSetting wallBypassMode = new ModeSetting(
      "module.aura.wallBypassMode",
      "module.aura.wallBypassMode.desc",
      this.entry29::isEnabled,
      "module.aura.wallBypassMode.realyworld",
      "module.aura.wallBypassMode.none"
   );
   private final BooleanSetting wallbypass = new BooleanSetting("module.aura.wallbypass", "module.aura.wallbypass.desc", true, () -> !this.entry29.isEnabled());
   private final AuraRandomizer auraRandomizer = new AuraRandomizer();
   private LivingEntity livingEntity = null;
   private int intValue = -1;
   private final List<Entity> items = new ArrayList<>();
   private LivingEntity livingEntity2 = null;
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag = false;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private boolean flag4 = false;
   public int intValue2 = 0;

   private AuraModule() {
   }

   @EventTarget
   public void method6313(EntityAddEvent var1) {
      if (var1.getEntity() instanceof ClientPlayerEntity) {
         this.livingEntity = null;
         this.livingEntity2 = null;
         this.flag4 = false;
      }
   }

   @EventTarget
   public void method6314(SendPacketEvent var1) {
   }

   @EventTarget
   public void method6315(Event2 var1) {
      if (this.livingEntity != null) {
         if (this.method6316()
            && zenith.getRotationManager().method7844().method3131(new Rotation(mc.player.lastYawClient, mc.player.lastPitchClient)).method5329(0.0F)) {
            zenith.getRotationManager()
               .setCurrentRotation(new Rotation((float)(mc.player.lastYawClient + MathUtils.method2651(-1.0, 1.0)), mc.player.lastPitchClient));
         }

         if (this.flag2 && this.method6321()) {
            this.flag = false;
            this.flag2 = false;
            this.flag4 = true;
         }
      }
   }

   public boolean method6316() {
      return false;
   }

   @EventTarget
   public void method6317(PostMotionEvent var1) {
      if (this.livingEntity != null
         && !this.method6321()
         && this.method6316()
         && mc.player != null
         && this.livingEntity != null
         && (this.flag4 || this.flag2 || this.flag)) {
         Vec3d var2 = mc.player.getEntityPos().subtract(this.livingEntity.getEntityPos());
         if (var2.horizontalLengthSquared() > 1.0E-5) {
            Rotation var3 = rotationManager.method7844();
            float var4 = Rotation.method3129(var2).getFloatValue();
            UseItemUtils.method6533(0.0, new Rotation(var3.getFloatValue() + MathHelper.wrapDegrees(var4 - var3.getFloatValue()), var3.getFloatValue2()));
         } else {
            Rotation var5 = rotationManager.method7844();
            UseItemUtils.method6533(0.0, new Rotation(var5.getFloatValue() + 179.0F, var5.getFloatValue2()));
         }
      }
   }

   @EventTarget
   public void method6318(UpdateEvent var1) {
      if (mc.player.isDead() || this.livingEntity == null) {
         this.flag4 = false;
      }

      if (this.flag4 && this.livingEntity != null && !mc.player.isDead()) {
         if (this.entry18.isSelected() && Zenith.getInstance().getServerDetector().isFlag()) {
            mc.player.setSprinting(false);
            mc.player.sendSprintingPacket();
         }

         this.method6328();
         AttackUtils.method816(this.livingEntity);
      }
   }

   @EventTarget
   public void method6319(Event5 var1) {
      this.flag3 = false;
      this.flag4 = false;
      if (mc.player.isDead()) {
         this.livingEntity = null;
         bcCameraTestnerv.getClass24().method3537();
      } else {
         this.livingEntity = this.method6356();
         if (this.livingEntity != null) {
            if (mc.world.getEntityById(this.livingEntity.getId()) instanceof LivingEntity var2) {
               this.livingEntity = var2;
            } else {
               this.livingEntity = null;
            }

            this.livingEntity2 = this.livingEntity;
         }

         this.flag = false;
         this.flag2 = false;
         if (this.livingEntity == null) {
            bcCameraTestnerv.getClass24().method3537();
         } else {
            this.flag = this.method6341();
            this.flag2 = this.method6344();
            Rotation var5 = this.method6334();
            int var6 = InventoryUtils.method922(var0 -> mc.player.getInventory().getStack(var0).getItem() instanceof AxeItem);
            boolean var4;
            if ((
                  var4 = this.entry26.isEnabled()
                     && this.stopWatch.method7546(300L)
                     && Zenith.getInstance().getTaskScheduler().method5116()
                     && var6 != -1
                     && UseItemUtils.method6530(this.livingEntity)
               )
               && mc.player.getMainHandStack().getItem() instanceof AxeItem
               && this.method6321()) {
               if (this.entry27.isEnabled() && mc.player.isUsingItem() && mc.player.getActiveItem().getItem() instanceof ShieldItem) {
                  mc.interactionManager.stopUsingItem(mc.player);
                  this.flag3 = true;
               }

               this.method6328();
               AttackUtils.method816(this.livingEntity);
               this.stopWatch.reset();
               this.flag = false;
               this.flag2 = false;
            }

            this.method6337(var5, this.flag || var4, this.flag2 || var4);
            if (var4) {
               if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem)) {
                  if (this.intValue == -1) {
                     this.intValue = mc.player.getInventory().getSelectedSlot();
                  }

                  mc.player.getInventory().setSelectedSlot(var6);
               }
            } else if (this.intValue != -1 && this.entry26.isEnabled() && !UseItemUtils.method6530(this.livingEntity)) {
               mc.player.getInventory().setSelectedSlot(this.intValue);
               this.intValue = -1;
            }

            if (this.flag && this.entry27.isEnabled() && mc.player.isUsingItem() && mc.player.getActiveItem().getItem() instanceof ShieldItem) {
               mc.interactionManager.stopUsingItem(mc.player);
               this.flag3 = true;
            }
         }
      }
   }

   public RotationConfig method6320() {
      return bcCameraTestnerv.getRotationConfig();
   }

   public boolean method6321() {
      return this.method6322(rotationManager.method7844());
   }

   public boolean method6322(Rotation var1) {
      return this.method6325(var1, this.method6333(), this.livingEntity.getBoundingBox()) || this.method6325(var1, this.method6333(), this.method6336());
   }

   public boolean method6323() {
      return TargetUtils.method3212(
         zenith.getRotationManager().method7844(),
         PlayerSimulation.method6031(1).vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0),
         this.method6324(),
         6.0,
         false
      );
   }

   private Box method6324() {
      return this.livingEntity instanceof PlayerEntity var1 ? PlayerSimulation.method6032(var1, 1).box : this.livingEntity.getBoundingBox();
   }

   public boolean method6325(Rotation var1, Vec3d var2, Box var3) {
      return this.method6326() ? this.method6332(var3, var2) : TargetUtils.method3212(var1, var2, var3, this.method6330(), !this.method6327());
   }

   private boolean method6326() {
      return WallBypassModule.INSTANCE.method7501();
   }

   private boolean method6327() {
      return this.entry29.isEnabled() || this.method6326();
   }

   private void method6328() {
      if (this.livingEntity != null
         && this.entry29.isEnabled()
         && this.wallBypassMode.method2101("module.aura.wallBypassMode.realyworld")
         && !mc.player.canSee(this.livingEntity)) {
         this.method6329();
      }
   }

   private void method6329() {
      if (this.livingEntity != null) {
         Vec3d var1 = mc.player.getEyePos();
         Vec3d var2 = this.livingEntity.getEyePos();
         Vec3d var3 = var2.subtract(var1);
         double var4;
         if (!((var4 = var3.length()) < 0.01)) {
            int var6 = Math.max(1, (int)Math.ceil(var4 * 2.0));
            HashSet var7 = new HashSet();

            for (int var8 = 0; var8 <= var6; var8++) {
               double var9 = (double)var8 / var6;
               BlockPos var11 = BlockPos.ofFloored(var1.x + var3.x * var9, var1.y + var3.y * var9, var1.z + var3.z * var9);
               if (var7.add(var11) && !mc.world.getBlockState(var11).isAir()) {
                  Vec3d var12 = var1.subtract(var11.toCenterPos());
                  double var13 = Math.abs(var12.x);
                  double var15 = Math.abs(var12.y);
                  double var17 = Math.abs(var12.z);
                  Direction var19 = var15 >= var13 && var15 >= var17
                     ? (var12.y > 0.0 ? Direction.UP : Direction.DOWN)
                     : (var13 >= var17 ? (var12.x > 0.0 ? Direction.EAST : Direction.WEST) : (var12.z > 0.0 ? Direction.SOUTH : Direction.NORTH));
                  mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var11, var19, 0));
                  mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var11, var19, 0));
               }
            }
         }
      }
   }

   private float method6330() {
      return this.method6326() ? WallBypassModule.INSTANCE.method7502() : this.distance.getValue3();
   }

   public boolean method6331(LivingEntity var1) {
      return AuraUtil.method7447(((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(), var1);
   }

   public boolean method6332(Box var1, Vec3d var2) {
      Vec3d var3 = new Vec3d(
         MathHelper.clamp(var2.x, var1.minX, var1.maxX), MathHelper.clamp(var2.y, var1.minY, var1.maxY), MathHelper.clamp(var2.z, var1.minZ, var1.maxZ)
      );
      return var3.isInRange(var2, this.method6330());
   }

   public Vec3d method6333() {
      return mc.player.getCameraPosVec(1.0F);
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (var2 instanceof PlayerPositionLookS2CPacket) {
            this.intValue2 = 3;
         }
      }
   }

   public Rotation method6334() {
      Vec3d var1 = mc.player.getCameraPosVec(1.0F);
      if (this.livingEntity instanceof PlayerEntity var2) {
         Box var6 = ReachV3Module.INSTANCE.isEnabled() && ReachV3Module.INSTANCE.getVec3d() != null
            ? this.livingEntity.dimensions.getBoxAt(ReachV3Module.INSTANCE.getVec3d())
            : PlayerSimulation.method6032(var2, 1).box;
         Rotation var4 = RotationUtils.method6912(
            this.method6335(var6, 0.5F).subtract(PlayerSimulation.method6031(1).vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0))
         );
         if (this.method6322(var4) || !this.flag2) {
            return var4;
         }
      }

      Vec3d var5 = this.auraRandomizer.method4673(var1, this.method6336(), this.method6330(), new Vec3d(0.0, 0.0, 0.0), this.method6327());
      return RotationUtils.method6912(var5.subtract(var1));
   }

   public Vec3d method6335(Box var1, float var2) {
      return new Vec3d(
         MathHelper.lerp(0.5, var1.minX, var1.maxX), MathHelper.lerp((double)var2, var1.minY, var1.maxY), MathHelper.lerp(0.5, var1.minZ, var1.maxZ)
      );
   }

   public Box method6336() {
      LivingEntity var2 = this.livingEntity;
      return var2 instanceof PlayerEntity ? this.livingEntity.getBoundingBox() : this.livingEntity.getBoundingBox();
   }

   public void method6337(Rotation var1, boolean var2, boolean var3) {
      if (this.entry7.isSelected()) {
         this.method6338(var3);
      } else {
         bcCameraTestnerv.getClass24().method3537();
         if (this.entry14.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(
                  var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig16(), var1), bcCameraTestnerv.getRotationConfig16()
               ),
               1,
               this
            );
         } else if (this.method6360()) {
            rotationManager.method7849(
               new RotationRequest(
                  var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig15(), var1), bcCameraTestnerv.getRotationConfig15()
               ),
               1,
               this
            );
         } else if (this.entry12.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(
                  var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig14(), var1), bcCameraTestnerv.getRotationConfig14()
               ),
               1,
               this
            );
         } else if (this.entry11.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(
                  var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig13(), var1), bcCameraTestnerv.getRotationConfig13()
               ),
               1,
               this
            );
         } else if (this.entry6.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig2(), var1), bcCameraTestnerv.getRotationConfig2()),
               1,
               this
            );
         } else if (this.entry4.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig4(), var1), bcCameraTestnerv.getRotationConfig4()),
               1,
               this
            );
         } else {
            if (var3 && this.entry8.isSelected() || this.entry9.isSelected() || this.entry10.isSelected()) {
               rotationManager.method7849(
                  new RotationRequest(var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var1), bcCameraTestnerv.getRotationConfig()),
                  var3 ? 3 : 3,
                  this
               );
            }
         }
      }
   }

   private void method6338(boolean var1) {
      boolean var2 = this.entry17.isSelected();
      Class24 var3 = bcCameraTestnerv.getClass24();
      Class24.Data var4;
      if (var2) {
         var4 = var3.method3536(
            new Rotation(mc.player.getYaw(), mc.player.getPitch(), true),
            rotationManager.method7844(),
            this.method6333(),
            this.method6336(),
            this.legitMaxAngle.getValue3(),
            this.livingEntity.getId(),
            mc.player.age
         );
      } else {
         var3.method3537();
         var4 = var3.method3535(rotationManager.method7844(), this.method6333(), this.method6336(), this.legitMaxAngle.getValue3(), false);
      }

      if (var4.isFlag()) {
         if (this.entry15.isSelected()) {
            if (!var1) {
               return;
            }
         } else if (!this.entry16.isSelected() && !var2) {
            return;
         }

         if (var2 || var4.isFlag3()) {
            int var5 = this.entry15.isSelected() ? 3 : 1;
            rotationManager.method7849(new RotationRequest(var4.rotation(), var4::rotation, bcCameraTestnerv.getRotationConfig()), var5, this);
         }
      }
   }

   @EventTarget
   public void method6339(MoveInputEvent var1) {
      if (this.entry23.isSelected() && this.livingEntity != null) {
         PlayerSimulation var2 = PlayerSimulation.method6031(1);
         Box var3 = ReachV3Module.INSTANCE.isEnabled() && ReachV3Module.INSTANCE.getVec3d2() != null
            ? this.livingEntity.dimensions.getBoxAt(ReachV3Module.INSTANCE.getVec3d2())
            : (this.livingEntity instanceof PlayerEntity var4 ? PlayerSimulation.method6032(var4, 1).box : this.livingEntity.getBoundingBox());
         Rotation var6 = RotationUtils.method6912(var3.getCenter().subtract(var2.vec3d));
         MovementInputUtils.method7426(var1, rotationManager.method7844().getFloatValue(), var6.getFloatValue());
      } else if (this.entry24.isSelected() || this.livingEntity == null) {
         MovementInputUtils.method7426(var1, rotationManager.method7844().getFloatValue(), mc.player.getYaw());
      }

      if (this.livingEntity != null) {
         this.method6353(var1);
      }
   }

   @EventTarget(0)
   public void method6340(Event9 var1) {
      if (this.flag4 || this.flag3) {
         var1.cancel();
      }
   }

   private boolean method6341() {
      return this.method6342(1);
   }

   public boolean method6342(int var1) {
      PlayerSimulation var2 = PlayerSimulation.method6031(var1);
      if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (CrystalAuraModule.INSTANCE.method5745(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem()
         && (!this.entry27.isEnabled() || !(mc.player.getActiveItem().getItem() instanceof ShieldItem))
         && !this.entry28.isEnabled()) {
         return false;
      } else if (AutoMaceModule.INSTANCE.method309(this.livingEntity) != AutoMaceModule.Type.NONE) {
         return AutoMaceModule.INSTANCE.getIntValue() <= var1 + 1;
      } else {
         return !this.onlyCrit.isEnabled()
               || AttackUtils.method818(var2)
               || AttackUtils.method822(var2)
               || this.smartCrit.isEnabled() && !mc.options.jumpKey.isPressed()
            ? this.method6345(1 + var1, !this.method6343())
            : false;
      }
   }

   public boolean method6343() {
      if (mc.player.isGliding() || AttackUtils.method817()) {
         return false;
      } else {
         return !this.onlyCrit.isEnabled() ? false : !this.smartCrit.isEnabled() || mc.options.jumpKey.isPressed();
      }
   }

   private boolean method6344() {
      if (this.intValue2 > 0) {
         this.intValue2--;
         return false;
      } else if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem() && !this.entry28.isEnabled()) {
         return false;
      } else if (CrystalAuraModule.INSTANCE.method5745(this.livingEntity)) {
         return false;
      } else {
         AutoMaceModule.Type var1 = AutoMaceModule.INSTANCE.method309(this.livingEntity);
         if (var1 == AutoMaceModule.Type.HIT) {
            return true;
         } else if (var1 == AutoMaceModule.Type.HOLD) {
            return false;
         } else {
            return !this.method6343() || AttackUtils.method819() && (!mc.player.lastSprinting || this.entry22.isSelected() || this.entry18.isSelected())
               ? this.method6345(0.0F, !this.method6343() || this.flag)
               : false;
         }
      }
   }

   public boolean method6345(float var1, boolean var2) {
      if (this.tpsSynch.isEnabled()) {
         return CriticalsModule.INSTANCE.method4667()
            ? CriticalsModule.INSTANCE.method4668(var1)
            : Zenith.getInstance().getServerDetector().getCriticalsState().method1708(this.method6346(), var1);
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

   public double method6346() {
      if (mc.player == null) {
         return 0.0;
      } else {
         double var1;
         if (!Double.isFinite(var1 = mc.player.getAttributeValue(EntityAttributes.ATTACK_SPEED)) || var1 <= 0.0) {
            var1 = 4.0;
         }

         double var3 = 20.0 / var1;
         double var5;
         double var7;
         if (this.cooldownMode.isSelected(1)) {
            var5 = 0.5;
            var7 = Math.nextUp(0.9F);
         } else {
            var5 = this.entry3.isSelected() ? this.timeCooldown.getValue3() : 1.0;
            var7 = this.entry3.isSelected() ? this.attackCooldown.getValue3() : 1.0;
         }

         return Math.max(0.0, var3 * var7 - var5);
      }
   }

   public boolean method6347() {
      return this.tpsSynch.isEnabled();
   }

   public boolean method6348() {
      if (mc.player == null || mc.world == null || this.livingEntity == null || this.intValue2 > 0) {
         return false;
      } else if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem() && !this.entry28.isEnabled()) {
         return false;
      } else {
         return !this.method6343() || AttackUtils.method819() && (!mc.player.lastSprinting || this.entry22.isSelected() || this.entry18.isSelected())
            ? this.method6321()
            : false;
      }
   }

   public int method6349(int var1, boolean var2) {
      Box var3 = this.method6351();
      if (var3 == null) {
         return -1;
      } else {
         int var4 = MathHelper.clamp(var1, 1, 20);

         for (int var5 = 1; var5 <= var4; var5++) {
            PlayerSimulation var6 = PlayerSimulation.method6031(var5);
            if (this.method6352(var6, var3, var2)) {
               return var5;
            }
         }

         return -1;
      }
   }

   public int method6350(int var1) {
      if (mc.player != null && !mc.player.isOnGround() && !(mc.player.fallDistance <= 0.0)) {
         Box var2 = this.method6351();
         if (var2 == null) {
            return -1;
         } else {
            int var3 = 0;
            int var4 = MathHelper.clamp(var1, 1, 20);

            for (int var5 = 1; var5 <= var4; var3 = var5++) {
               PlayerSimulation var6 = PlayerSimulation.method6031(var5);
               if (!this.method6352(var6, var2, true)) {
                  break;
               }
            }

            return var3 > 0 ? var3 : -1;
         }
      } else {
         return -1;
      }
   }

   private Box method6351() {
      return mc.player != null
            && mc.world != null
            && this.livingEntity != null
            && !UseItemUtils.method6530(this.livingEntity)
            && (!mc.player.isUsingItem() || this.entry28.isEnabled())
            && (this.method6327() || mc.player.canSee(this.livingEntity))
         ? this.method6324()
         : null;
   }

   private boolean method6352(PlayerSimulation var1, Box var2, boolean var3) {
      if (!var3 || !var1.flag4 && !(var1.floatValue3 <= 0.0F)) {
         if (!this.method6343() || AttackUtils.method822(var1) && !var1.flag4) {
            Vec3d var4 = var1.vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);
            return this.method6332(var2, var4);
         } else {
            return false;
         }
      } else {
         return false;
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
      this.flag4 = false;
      this.livingEntity = null;
      super.onDisable();
   }

   public void method6353(MoveInputEvent var1) {
      if (this.method6355()) {
         boolean var2 = mc.options.sprintKey.isPressed();
         boolean var3 = mc.options.forwardKey.isPressed();
         if (this.entry21.isSelected()) {
            if (!mc.player.isOnGround() && (!(mc.player.fallDistance > 0.0) || this.method6345(0.0F, false))) {
               var2 = false;
               if (mc.player.isSprinting()) {
                  var3 = false;
               }
            }
         } else if (!this.flag4 && !mc.player.isOnGround() && this.method6341()) {
            if (this.entry20.isSelected()) {
               var2 = false;
               if (mc.player.isSprinting()) {
                  var3 = false;
               }
            }

            if (this.entry19.isSelected()) {
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

   @EventTarget
   public void method6354(SprintUpdateEvent var1) {
      if (this.livingEntity != null && !this.flag4 && !mc.player.isOnGround() && this.method6341() && this.entry19.isSelected()) {
         var1.setFlag2(false);
      }
   }

   public boolean method6355() {
      return !this.entry22.isSelected() && !AttackUtils.method817();
   }

   private LivingEntity method6356() {
      float var1 = this.method6330() + this.distanceRotation.getValue3();
      LivingEntity var2 = AuraUtil.method7446(
         this.method6357(var1),
         var1,
         this.method6327(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(1)).method5229(),
         ((BooleanSetting)this.safeTarget.getSettings().get(2)).isEnabled()
      );
      this.items.clear();
      return var2;
   }

   private List<Entity> method6357(float var1) {
      this.items.clear();
      Vec3d var2 = mc.player.getEyePos();
      double var3 = (var1 + 0.01) * (var1 + 0.01);

      for (Entity var6 : mc.world.getEntities()) {
         if (var6 instanceof LivingEntity && var6.getBoundingBox().squaredMagnitude(var2) <= var3) {
            this.items.add(var6);
         }
      }

      return this.items;
   }

   public LivingEntity method6358() {
      return this.isEnabled() ? this.livingEntity : null;
   }

   public boolean method6359(int var1) {
      return var1 == 4 ? this.method6360() : (var1 == 3 ? this.entry14.isSelected() : (var1 == 2 ? this.entry12.isSelected() : this.entry11.isSelected()));
   }

   private boolean method6360() {
      return this.entry13.isSelected() || this.entry5.isSelected();
   }

   public double method6361() {
      return this.sliderSetting.getValue3() / 100.0;
   }

   public boolean method6362(BlockHitResult var1) {
      if (var1 != null && this.wallbypass.isEnabled()) {
         BlockState var2 = mc.world.getBlockState(var1.getBlockPos());
         return !(var2.getBlock() instanceof StairsBlock)
            && var2.getBlock() != Blocks.KELP
            && var2.getBlock() != Blocks.KELP_PLANT
            && var2.getBlock() != Blocks.TALL_SEAGRASS
            && var2.getBlock() != Blocks.TALL_GRASS
            && !(var2.getBlock() instanceof GrassBlock)
            && !(var2.getBlock() instanceof TrapdoorBlock)
            && var2.getBlock() != Blocks.COBWEB
            && !(var2.getBlock() instanceof DoorBlock);
      } else {
         return true;
      }
   }

   @Generated
   public LivingEntity getLivingEntity22() {
      return this.livingEntity2;
   }

   @Generated
   public boolean isFlag9() {
      return this.flag;
   }

   @Generated
   public boolean isFlag26() {
      return this.flag2;
   }

   public static class Entry {
      private double doubleValue;
      private double doubleValue2;
      private double doubleValue3;
      private int ticks;

      public Entry(double var1, double var3, double var5) {
         this.doubleValue = var1;
         this.doubleValue2 = var3;
         this.doubleValue3 = var5;
      }

      public boolean method6363() {
         return this.ticks++ > 2;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }
   }
}

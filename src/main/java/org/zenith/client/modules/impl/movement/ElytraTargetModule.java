package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ElytraMath;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Handler6;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "ElytraTarget",
   description = "module.elytratarget.description",
   category = ModuleCategory.MOVEMENT
)
public final class ElytraTargetModule extends Module {
   private static final double CONST_doubleValue = 0.35;
   public static final ElytraTargetModule INSTANCE = new ElytraTargetModule();
   private final SliderSetting pursuitDistance = new SliderSetting(
      "module.elytraTarget.pursuitDistance", 30.0F, 10.0F, 100.0F, 5.0F, "module.elytraTarget.pursuitDistance.desc", "b"
   );
   private final BooleanSetting autoFWSetting = new BooleanSetting("module.elytraTarget.autoFWSetting", "module.elytraTarget.autoFWSetting.desc", true);
   private final ModeSetting predictMode = new ModeSetting(
      "module.elytraTarget.predictMode",
      "module.elytraTarget.predictMode.desc",
      "module.elytraTarget.predictMode.simulation",
      "module.elytraTarget.predictMode.velocity",
      "module.elytraTarget.predictMode.rotation"
   );
   private final SliderSetting predictDistance = new SliderSetting(
      "module.elytraTarget.predictDistance", 2.7F, 1.0F, 5.0F, 0.1F, "module.elytraTarget.predictDistance.desc", "", this::method5246, null
   );
   private final SliderSetting predictTicks = new SliderSetting(
      "module.elytraTarget.predictTicks", 3.0F, 0.0F, 6.0F, 1.0F, "module.elytraTarget.predictTicks.desc", "t", this::method5244, null
   );
   private final SliderSetting predictStrength = new SliderSetting(
      "module.elytraTarget.predictStrength", 3.0F, 1.5F, 10.0F, 0.5F, "module.elytraTarget.predictStrength.desc", "b", this::method5245, null
   );
   private final SliderSetting overtakeBps = new SliderSetting(
      "module.elytraTarget.overtakeBps", 15.0F, 10.0F, 30.0F, 1.0F, "module.elytraTarget.overtakeBps.desc", "bps", this::method5245, null
   );
   private final BooleanSetting instantResponse = new BooleanSetting("module.elytraTarget.instantResponse", "module.elytraTarget.instantResponse.desc", true);
   private final SliderSetting responseSpeed = new SliderSetting(
      "module.elytraTarget.responseSpeed", 1.7F, 1.0F, 3.0F, 0.05F, "module.elytraTarget.responseSpeed.desc", "", this.instantResponse::isEnabled, null
   );
   private final BooleanSetting predictCube = new BooleanSetting("module.elytraTarget.predictCube", "module.elytraTarget.predictCube.desc", true);
   private final SliderSetting predictFillAlpha = new SliderSetting(
      "module.elytraTarget.predictFillAlpha", 40.0F, 0.0F, 255.0F, 1.0F, "module.elytraTarget.predictFillAlpha.desc", "", this.predictCube::isEnabled
   );
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private LivingEntity livingEntity;
   private Box box;

   private ElytraTargetModule() {
   }

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      super.onDisable();
   }

   @EventTarget
   public void method5240(WorldRenderEvent var1) {
      if (this.predictCube.isEnabled() && this.box != null) {
         int var2 = Zenith.getInstance().getStyleManager().getClientColor(0).method7287();
         int var4 = Math.round(this.predictFillAlpha.getValue3()) << 24 | var2 & (255 << 16 | (char)-1);
         GlowUtil.method2232(this.box, var4, 1.5F, true, true, false);
      }
   }

   @EventTarget
   public void method5241(Event5 var1) {
      ClientPlayerEntity var2 = mc.player;
      this.livingEntity = this.method5251();
      if (var2 != null && this.method5242(this.livingEntity)) {
         Vec3d var3 = this.method5247(this.livingEntity);
         this.box = this.method5253(var3);
         Vec3d var4 = var2.getEyePos();
         Rotation var5 = RotationUtils.method6912(var3.subtract(var4));
         rotationManager.method7849(
            new RotationRequest(var5, () -> bcCameraTestnerv.method1851(AuraModule.INSTANCE.method6320(), var5), AuraModule.INSTANCE.method6320()), 7, this
         );
         if (this.autoFWSetting.isEnabled()
            && !ElytraMotionModule.INSTANCE.isFlag10()
            && !AutoTotemModule.INSTANCE.method725()
            && this.stopWatch.method7546(400L)) {
            InventoryUtils.method930(Items.FIREWORK_ROCKET, var0 -> true, Hand.OFF_HAND);
            this.stopWatch.reset();
         }
      } else {
         this.method5254();
      }
   }

   public boolean method5242(LivingEntity var1) {
      return this.isEnabled() && this.method5252(var1);
   }

   private int method5243() {
      return (int)this.predictTicks.getValue3();
   }

   private boolean method5244() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.simulation");
   }

   private boolean method5245() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.rotation");
   }

   private boolean method5246() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.velocity");
   }

   private Vec3d method5247(LivingEntity var1) {
      if (this.method5244()) {
         int var5 = this.method5243();
         return var1 instanceof PlayerEntity var3
            ? PlayerSimulation.method6032(var3, var5).box.getCenter()
            : var1.getBoundingBox().getCenter().add(var1.getVelocity().multiply(var5));
      } else if (this.method5246()) {
         Vec3d var4 = var1.getTrackedPosition().getPos();
         return var1.dimensions.getBoxAt(var4).getCenter().add(((Handler6)var1).zenithDLC$getServerDelta().multiply(this.predictDistance.getValue3()));
      } else if (this.method5245()) {
         Vec3d var2 = var1.getBoundingBox().getCenter();
         return method5248(var1) <= this.overtakeBps.getValue3() ? var2 : var2.add(var1.getRotationVector().multiply(this.predictStrength.getValue3()));
      } else {
         return var1.getTrackedPosition().getPos();
      }
   }

   private static double method5248(LivingEntity var0) {
      double var1 = var0.getX() - var0.lastX;
      double var3 = var0.getY() - var0.lastY;
      double var5 = var0.getZ() - var0.lastZ;
      return Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 20.0;
   }

   public Vec3d method5249(Vec3d var1, Vec3d var2, Vec3d var3) {
      ClientPlayerEntity var4 = mc.player;
      if (this.isEnabled() && this.instantResponse.isEnabled() && var4 != null && mc.world != null && var4.isGliding() && UseItemUtils.method6517() != null) {
         LivingEntity var5 = this.livingEntity;
         if (var5 != null && !var5.isGliding() && var4.getEyePos().distanceTo(method5250(var4.getEyePos(), var5.getBoundingBox())) < 2.6) {
            return var2;
         } else {
            double var6 = this.responseSpeed.getValue3();
            if (var5 != null && var4.getEyePos().distanceTo(var5.getEyePos().add(var5.getVelocity())) < 2.0) {
               var6 = var5.getVelocity().length();
            }

            return ElytraMath.method5973(var4, var3.multiply(var6), var3, 1.0);
         }
      } else {
         return var2;
      }
   }

   private static Vec3d method5250(Vec3d var0, Box var1) {
      return new Vec3d(
         MathHelper.clamp(var0.x, var1.minX, var1.maxX), MathHelper.clamp(var0.y, var1.minY, var1.maxY), MathHelper.clamp(var0.z, var1.minZ, var1.maxZ)
      );
   }

   private LivingEntity method5251() {
      LivingEntity var1 = AuraModule.INSTANCE.method6358();
      if (this.method5252(var1)) {
         return var1;
      } else {
         ClientPlayerEntity var2 = mc.player;
         return var2 != null && mc.world != null
            ? mc.world
               .getPlayers()
               .stream()
               .filter(var1x -> var1x != var2)
               .filter(this::method5252)
               .filter(var0 -> !Zenith.getInstance().getFriends().method7626(var0))
               .min(Comparator.comparingDouble(var2::distanceTo))
               .orElse(null)
            : null;
      }
   }

   private boolean method5252(LivingEntity var1) {
      ClientPlayerEntity var2 = mc.player;
      return var2 != null
         && var1 != null
         && var1.isAlive()
         && var2.isGliding()
         && var1.isGliding()
         && var2.distanceTo(var1) <= this.pursuitDistance.getValue3();
   }

   private Box method5253(Vec3d var1) {
      return new Box(var1.x - 0.35, var1.y - 0.35, var1.z - 0.35, var1.x + 0.35, var1.y + 0.35, var1.z + 0.35);
   }

   private void method5254() {
      this.box = null;
   }

   private void resetState() {
      this.livingEntity = null;
      this.box = null;
      this.stopWatch.reset();
      this.stopWatch2.reset();
   }

   @Generated
   public Box getBox() {
      return this.box;
   }
}

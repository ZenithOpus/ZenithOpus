package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.authlib.GameProfile;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.KeyName;

@ModuleInfo(
   name = "FakePlayer",
   description = "module.fakeplayer.description",
   category = ModuleCategory.MISC
)
public class FakePlayerModule extends Module {
   public static final FakePlayerModule INSTANCE = new FakePlayerModule();
   private final ModeSetting mode = new ModeSetting(
      "module.fakePlayer.mode",
      "module.fakePlayer.mode.desc",
      "module.fakePlayer.mode.default",
      "module.fakePlayer.mode.teleportation",
      "module.fakePlayer.mode.trajectory"
   );
   private final BindSetting recordingKey = new BindSetting("module.fakePlayer.recordingKey", "module.fakePlayer.recordingKey.desc", -1);
   private final BindSetting moveKey = new BindSetting("module.fakePlayer.moveKey", "module.fakePlayer.moveKey.desc", -1);
   private final SliderSetting minDistance = new SliderSetting(
      "module.fakePlayer.minDistance", 1.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.minDistance.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxDistance = new SliderSetting(
      "module.fakePlayer.maxDistance", 4.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.maxDistance.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting minXZOffset = new SliderSetting(
      "module.fakePlayer.minXZOffset", 0.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.minXZOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxXZOffset = new SliderSetting(
      "module.fakePlayer.maxXZOffset", 4.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.maxXZOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting minYOffset = new SliderSetting(
      "module.fakePlayer.minYOffset", 0.0F, 0.0F, 10.0F, 0.1F, "module.fakePlayer.minYOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxYOffset = new SliderSetting(
      "module.fakePlayer.maxYOffset", 0.0F, 0.0F, 10.0F, 0.1F, "module.fakePlayer.maxYOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting minIdlingTicks = new SliderSetting(
      "module.fakePlayer.minIdlingTicks", 5.0F, 0.0F, 200.0F, 1.0F, "module.fakePlayer.minIdlingTicks.desc", "t", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxIdlingTicks = new SliderSetting(
      "module.fakePlayer.maxIdlingTicks", 20.0F, 0.0F, 200.0F, 1.0F, "module.fakePlayer.maxIdlingTicks.desc", "t", () -> this.mode.isSelected(1), null
   );
   private final ModeSetting pattern = new ModeSetting(
      "module.fakePlayer.pattern",
      "module.fakePlayer.pattern.desc",
      () -> this.mode.isSelected(2),
      "module.fakePlayer.pattern.sine",
      "module.fakePlayer.pattern.sweep",
      "module.fakePlayer.pattern.circle",
      "module.fakePlayer.pattern.step",
      "module.fakePlayer.pattern.static",
      "module.fakePlayer.pattern.random"
   );
   private final SliderSetting yawAmplitude = new SliderSetting(
      "module.fakePlayer.yawAmplitude",
      30.0F,
      0.0F,
      170.0F,
      1.0F,
      "module.fakePlayer.yawAmplitude.desc",
      "d",
      () -> this.mode.isSelected(2) && !this.pattern.isSelected(4),
      null
   );
   private final SliderSetting pitchAmplitude = new SliderSetting(
      "module.fakePlayer.pitchAmplitude",
      0.0F,
      0.0F,
      60.0F,
      1.0F,
      "module.fakePlayer.pitchAmplitude.desc",
      "d",
      () -> this.mode.isSelected(2) && !this.pattern.isSelected(4),
      null
   );
   private final SliderSetting period = new SliderSetting(
      "module.fakePlayer.period",
      40.0F,
      4.0F,
      400.0F,
      1.0F,
      "module.fakePlayer.period.desc",
      "t",
      () -> this.mode.isSelected(2) && (this.pattern.isSelected(0) || this.pattern.isSelected(2)),
      null
   );
   private final SliderSetting sweepFrom = new SliderSetting(
      "module.fakePlayer.sweepFrom",
      200.0F,
      4.0F,
      400.0F,
      1.0F,
      "module.fakePlayer.sweepFrom.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(1),
      null
   );
   private final SliderSetting sweepTo = new SliderSetting(
      "module.fakePlayer.sweepTo",
      8.0F,
      4.0F,
      400.0F,
      1.0F,
      "module.fakePlayer.sweepTo.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(1),
      null
   );
   private final SliderSetting sweepLength = new SliderSetting(
      "module.fakePlayer.sweepLength",
      1200.0F,
      100.0F,
      12000.0F,
      100.0F,
      "module.fakePlayer.sweepLength.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(1),
      null
   );
   private final SliderSetting stepMin = new SliderSetting(
      "module.fakePlayer.stepMin",
      10.0F,
      1.0F,
      180.0F,
      1.0F,
      "module.fakePlayer.stepMin.desc",
      "d",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(3),
      null
   );
   private final SliderSetting stepMax = new SliderSetting(
      "module.fakePlayer.stepMax",
      90.0F,
      1.0F,
      180.0F,
      1.0F,
      "module.fakePlayer.stepMax.desc",
      "d",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(3),
      null
   );
   private final SliderSetting stepHold = new SliderSetting(
      "module.fakePlayer.stepHold",
      30.0F,
      5.0F,
      200.0F,
      1.0F,
      "module.fakePlayer.stepHold.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(3),
      null
   );
   private final SliderSetting trajDistance = new SliderSetting(
      "module.fakePlayer.trajDistance", 3.0F, 1.0F, 20.0F, 0.1F, "module.fakePlayer.trajDistance.desc", "b", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting trajDistanceMax = new SliderSetting(
      "module.fakePlayer.trajDistanceMax", 3.0F, 1.0F, 20.0F, 0.1F, "module.fakePlayer.trajDistanceMax.desc", "b", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting ladderSteps = new SliderSetting(
      "module.fakePlayer.ladderSteps", 1.0F, 1.0F, 10.0F, 1.0F, "module.fakePlayer.ladderSteps.desc", "", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting ladderHold = new SliderSetting(
      "module.fakePlayer.ladderHold", 1200.0F, 100.0F, 12000.0F, 100.0F, "module.fakePlayer.ladderHold.desc", "t", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting baseElevation = new SliderSetting(
      "module.fakePlayer.baseElevation", 0.0F, -45.0F, 45.0F, 1.0F, "module.fakePlayer.baseElevation.desc", "d", () -> this.mode.isSelected(2), null
   );
   private final ModeSetting anchor = new ModeSetting(
      "module.fakePlayer.anchor",
      "module.fakePlayer.anchor.desc",
      () -> this.mode.isSelected(2),
      "module.fakePlayer.anchor.player",
      "module.fakePlayer.anchor.world"
   );
   private final ModeSetting motion = new ModeSetting(
      "module.fakePlayer.motion",
      "module.fakePlayer.motion.desc",
      () -> this.mode.isSelected(2),
      "module.fakePlayer.motion.smooth",
      "module.fakePlayer.motion.network",
      "module.fakePlayer.motion.exact"
   );
   private final BooleanSetting record_ = new BooleanSetting("module.fakePlayer.record", "module.fakePlayer.record.desc", false, () -> this.mode.isSelected(2));
   private final BindSetting recordKey = new BindSetting("module.fakePlayer.recordKey", "module.fakePlayer.recordKey.desc", -1);
   private boolean flag = false;
   private boolean flag2 = false;
   private int intValue = 0;
   private int intValue2 = 0;
   private int intValue3 = 0;
   private int intValue4 = 0;
   private int intValue5 = -1;
   private double doubleValue = 0.0;
   private static final double CONST_doubleValue = 0.7;
   private double doubleValue2 = 0.0;
   private double doubleValue3 = 0.0;
   private double doubleValue4 = 0.0;
   private int intValue6 = 0;
   private Vec3d vec3d = null;
   private double doubleValue5 = 0.0;
   private boolean flag3 = false;
   private double doubleValue6 = 0.0;
   private boolean flag4 = false;
   private static final double CONST_doubleValue2 = 15.0;
   private static final Path path = Path.of("rotation_recordings", "fakeplayer_traj_v1.csv");
   private static final String text = "session_id,frame_id,t_ms,dt_ms,tick,sens,raw_dx,raw_dy,ev_dyaw,ev_dpitch,real_dyaw,real_dpitch,yaw,pitch,tgt_az,tgt_el,dist,err_yaw,err_pitch,px,py,pz,vx,vz,on_ground,sprint,sneak,k_w,k_s,k_a,k_d,k_jump,pattern,anchor,traj_tick";
   private BufferedWriter bufferedWriter;
   private boolean flag5 = false;
   private long longValue = 0L;
   private long longValue2 = 0L;
   private long longValue3 = 0L;
   private long longValue4 = 0L;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private boolean flag6 = false;
   private double doubleValue7 = 0.0;
   private double doubleValue8 = 0.0;
   private double doubleValue9 = 0.0;
   private int intValue7 = Integer.MAX_VALUE;
   private OtherClientPlayerEntity otherClientPlayerEntity;
   private final List<FakePlayerModule.Data> items = new ArrayList<>();

   private FakePlayerModule() {
   }

   private static String method7595(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   @Override
   public void onEnable() {
      if (mc.world != null) {
         if (mc.player != null) {
            this.method7611(mc.player.getEntityPos(), mc.player.getYaw(), mc.player.getPitch());
         } else {
            this.method7611(Vec3d.ZERO, 0.0F, 0.0F);
         }

         this.flag = false;
         this.flag2 = false;
         this.intValue = 0;
         this.intValue2 = 0;
         this.intValue3 = this.mode.getIndex();
         this.intValue5 = this.pattern.getIndex();
         this.method7605();
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.method7612(RemovalReason.KILLED);
      this.flag = false;
      this.flag2 = false;
      this.intValue = 0;
      this.intValue2 = 0;
      this.method7605();
      this.method7604();
      super.onDisable();
   }

   @EventTarget
   public void method7596(AttackEvent var1) {
   }

   @EventTarget
   public void method7597(KeyEvent var1) {
      if (this.mode.isSelected(2)) {
         if (var1.isPress(this.recordKey.getKeyCode())) {
            this.record_.setEnabled(!this.record_.isEnabled());
            if (this.record_.isEnabled()) {
               this.method7604();
               this.flag5 = false;
               this.method7603();
            } else {
               this.method7604();
            }
         }
      } else if (this.mode.isSelected(0)) {
         if (var1.isPress(this.recordingKey.getKeyCode())) {
            this.flag = !this.flag;
            if (this.flag) {
               this.items.clear();
            }
         }

         if (var1.isPress(this.moveKey.getKeyCode())) {
            this.flag2 = !this.flag2;
         }
      }
   }

   @EventTarget
   public void method7598(ScreenRenderEvent var1) {
      if (this.mode.isSelected(2)) {
         double var2 = this.method7607();
         String var4;
         if (this.pattern.isSelected(1)) {
            int var5 = Math.max(1, Math.round(this.sweepLength.getValue3()));
            var4 = "sweep " + this.intValue4 % var5 + "/" + var5;
         } else if (this.pattern.isSelected(3)) {
            var4 = String.format("step %.0fd, %dt", this.doubleValue4, this.intValue6);
         } else {
            var4 = "T=" + Math.round(this.period.getValue3()) + "t";
         }

         var1.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Trajectory " + method7595(this.pattern.get()) + " / " + method7595(this.anchor.get()) + " / " + method7595(this.motion.get()),
               20.0F,
               20.0F,
               ZenithColor.INSTANCE
            );
         var1.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F), String.format("dist %.2fb   %s   tick %d", var2, var4, this.intValue4), 20.0F, 35.0F, ZenithColor.INSTANCE
            );
         var1.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               (this.record_.isEnabled() ? "REC " + this.longValue3 + " строк" : "не пишет")
                  + "  "
                  + KeyName.method6701(this.recordKey.getKeyCode())
                  + (this.flag5 ? "  ОШИБКА ЗАПИСИ" : ""),
               20.0F,
               50.0F,
               ZenithColor.INSTANCE
            );
      } else if (this.mode.isSelected(0)) {
         var1.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Recoding " + this.flag + "  " + KeyName.method6701(this.recordingKey.getKeyCode()),
               20.0F,
               20.0F,
               ZenithColor.INSTANCE
            );
         var1.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Moving " + this.flag2 + "  " + KeyName.method6701(this.moveKey.getKeyCode()),
               20.0F,
               35.0F,
               ZenithColor.INSTANCE
            );
      } else {
         var1.getMenuDrawContext().drawText(Fonts.NEW_MEDIUM.getFont(10.0F), "Teleportation idle: " + this.intValue2 + "t", 20.0F, 20.0F, ZenithColor.INSTANCE);
      }
   }

   @EventTarget
   public void method7599(UpdateEvent var1) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         if (this.intValue3 != this.mode.getIndex()) {
            this.intValue3 = this.mode.getIndex();
            this.intValue = 0;
            this.flag = false;
            this.flag2 = false;
            this.intValue2 = 0;
            this.method7605();
         }

         if (this.mode.isSelected(1)) {
            this.method7609();
         } else if (this.mode.isSelected(2)) {
            this.method7606();
         } else if (this.flag) {
            this.items
               .add(
                  new FakePlayerModule.Data(
                     mc.player.getX(),
                     mc.player.getY(),
                     mc.player.getZ(),
                     mc.player.getYaw(),
                     mc.player.getPitch(),
                     mc.player.isSwimming(),
                     mc.player.isSneaking()
                  )
               );
         } else {
            if (this.flag2 && !this.items.isEmpty()) {
               this.intValue++;
               if (this.intValue >= this.items.size()) {
                  this.intValue = 0;
                  return;
               }

               FakePlayerModule.Data var2 = this.items.get(this.intValue);
               this.otherClientPlayerEntity.setYaw(var2.floatValue);
               this.otherClientPlayerEntity.setPitch(var2.floatValue2);
               this.otherClientPlayerEntity.setHeadYaw(var2.floatValue);
               this.otherClientPlayerEntity.setSwimming(var2.flag);
               this.otherClientPlayerEntity.setSneaking(var2.flag2);
               this.otherClientPlayerEntity.setPose(var2.flag ? EntityPose.SWIMMING : (var2.flag2 ? EntityPose.CROUCHING : EntityPose.STANDING));
               this.otherClientPlayerEntity.updateTrackedPosition(var2.doubleValue, var2.doubleValue2, var2.doubleValue3);
               this.otherClientPlayerEntity
                  .updateTrackedPositionAndAngles(new Vec3d(var2.doubleValue, var2.doubleValue2, var2.doubleValue3), var2.floatValue, var2.floatValue2);
            } else {
               this.intValue = 0;
            }
         }
      }
   }

   @EventTarget
   public void method7600(MouseRotationEvent var1) {
      if (this.mode.isSelected(2) && this.record_.isEnabled() && mc.player != null && !this.flag5) {
         if (this.bufferedWriter == null) {
            this.method7603();
            if (this.bufferedWriter == null) {
               return;
            }
         }

         long var2 = System.nanoTime();
         double var4 = this.longValue4 == 0L ? 0.0 : (var2 - this.longValue4) / 1000000.0;
         this.longValue4 = var2;
         float var6 = mc.getRenderTickCounter().getTickProgress(false);
         this.method7601(var6);
         float var7 = mc.player.getYaw();
         float var8 = mc.player.getPitch();
         double var9 = this.flag6 ? MathHelper.wrapDegrees(var7 - this.floatValue) : 0.0;
         double var11 = this.flag6 ? var8 - this.floatValue2 : 0.0;
         this.floatValue = var7;
         this.floatValue2 = var8;
         this.flag6 = true;
         double var13 = MathHelper.wrapDegrees((float)(this.doubleValue7 - var7));
         double var15 = -this.doubleValue8 - var8;
         Vec3d var17 = mc.player.getVelocity();
         StringBuilder var18 = new StringBuilder(256);
         var18.append(this.longValue)
            .append(',')
            .append(this.longValue2++)
            .append(',')
            .append(var2 / 1000000L)
            .append(',')
            .append(method7602(var4))
            .append(',')
            .append(mc.player.age)
            .append(',')
            .append(method7602(mc.options.getMouseSensitivity().getValue()))
            .append(',')
            .append(method7602(var1.getDoubleValue3()))
            .append(',')
            .append(method7602(var1.getDoubleValue4()))
            .append(',')
            .append(method7602(var1.getDoubleValue() * 0.15))
            .append(',')
            .append(method7602(var1.getDoubleValue2() * 0.15))
            .append(',')
            .append(method7602(var9))
            .append(',')
            .append(method7602(var11))
            .append(',')
            .append(method7602(var7))
            .append(',')
            .append(method7602(var8))
            .append(',')
            .append(method7602(this.doubleValue7))
            .append(',')
            .append(method7602(this.doubleValue8))
            .append(',')
            .append(method7602(this.doubleValue9))
            .append(',')
            .append(method7602(var13))
            .append(',')
            .append(method7602(var15))
            .append(',')
            .append(method7602(mc.player.getX()))
            .append(',')
            .append(method7602(mc.player.getY()))
            .append(',')
            .append(method7602(mc.player.getZ()))
            .append(',')
            .append(method7602(var17.x))
            .append(',')
            .append(method7602(var17.z))
            .append(',')
            .append(mc.player.isOnGround() ? 1 : 0)
            .append(',')
            .append(mc.player.isSprinting() ? 1 : 0)
            .append(',')
            .append(mc.player.isSneaking() ? 1 : 0)
            .append(',')
            .append(mc.options.forwardKey.isPressed() ? 1 : 0)
            .append(',')
            .append(mc.options.backKey.isPressed() ? 1 : 0)
            .append(',')
            .append(mc.options.leftKey.isPressed() ? 1 : 0)
            .append(',')
            .append(mc.options.rightKey.isPressed() ? 1 : 0)
            .append(',')
            .append(mc.options.jumpKey.isPressed() ? 1 : 0)
            .append(',')
            .append(this.pattern.getIndex())
            .append(',')
            .append(this.anchor.getIndex())
            .append(',')
            .append(this.intValue4);

         try {
            this.bufferedWriter.write(var18.toString());
            this.bufferedWriter.newLine();
            this.longValue3++;
            if ((this.longValue3 & 255L) == 0L) {
               this.bufferedWriter.flush();
            }
         } catch (IOException var20) {
            this.flag5 = true;
            System.err.println("[FakePlayer] traj log write failed: " + var20.getMessage());
            this.method7604();
         }
      }
   }

   private void method7601(float var1) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         Box var2 = this.otherClientPlayerEntity.getBoundingBox();
         double var3 = (var2.maxY - var2.minY) * 0.5;
         double var5 = MathHelper.lerp((double)var1, this.otherClientPlayerEntity.lastRenderX, this.otherClientPlayerEntity.getX());
         double var7 = MathHelper.lerp((double)var1, this.otherClientPlayerEntity.lastRenderY, this.otherClientPlayerEntity.getY()) + var3;
         double var9 = MathHelper.lerp((double)var1, this.otherClientPlayerEntity.lastRenderZ, this.otherClientPlayerEntity.getZ());
         double var11 = mc.player.getEyeY() - mc.player.getY();
         double var13 = MathHelper.lerp((double)var1, mc.player.lastRenderX, mc.player.getX());
         double var15 = MathHelper.lerp((double)var1, mc.player.lastRenderY, mc.player.getY()) + var11;
         double var17 = MathHelper.lerp((double)var1, mc.player.lastRenderZ, mc.player.getZ());
         double var19 = var5 - var13;
         double var21 = var7 - var15;
         double var23 = var9 - var17;
         double var25 = Math.sqrt(var19 * var19 + var23 * var23);
         this.doubleValue7 = Math.toDegrees(Math.atan2(-var19, var23));
         this.doubleValue8 = Math.toDegrees(Math.atan2(var21, var25));
         this.doubleValue9 = Math.sqrt(var25 * var25 + var21 * var21);
      }
   }

   private static String method7602(double var0) {
      return var0 == 0.0 ? "0" : String.format(Locale.ROOT, "%.5f", var0);
   }

   private void method7603() {
      try {
         Files.createDirectories(path.getParent());
         boolean var1 = !Files.exists(path) || Files.size(path) == 0L;
         this.bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         if (var1) {
            this.bufferedWriter
               .write(
                  "session_id,frame_id,t_ms,dt_ms,tick,sens,raw_dx,raw_dy,ev_dyaw,ev_dpitch,real_dyaw,real_dpitch,yaw,pitch,tgt_az,tgt_el,dist,err_yaw,err_pitch,px,py,pz,vx,vz,on_ground,sprint,sneak,k_w,k_s,k_a,k_d,k_jump,pattern,anchor,traj_tick"
               );
            this.bufferedWriter.newLine();
         }

         this.longValue = System.currentTimeMillis();
         this.longValue2 = 0L;
         this.longValue3 = 0L;
         this.longValue4 = 0L;
         this.flag6 = false;
      } catch (IOException var2) {
         this.flag5 = true;
         this.bufferedWriter = null;
         System.err.println("[FakePlayer] traj log open failed: " + var2.getMessage());
      }
   }

   private void method7604() {
      if (this.bufferedWriter != null) {
         try {
            this.bufferedWriter.flush();
            this.bufferedWriter.close();
         } catch (IOException var2) {
         }

         this.bufferedWriter = null;
      }
   }

   private void method7605() {
      this.intValue4 = 0;
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.intValue6 = 0;
      this.vec3d = null;
      this.flag3 = false;
      this.flag4 = false;
   }

   private void method7606() {
      if (mc.player != null) {
         if (!this.record_.isEnabled() && this.bufferedWriter != null) {
            this.method7604();
         }

         Vec3d var1;
         if (this.anchor.isSelected(1)) {
            if (this.vec3d == null) {
               this.vec3d = mc.player.getEntityPos();
            }

            var1 = this.vec3d;
         } else {
            var1 = mc.player.getEntityPos();
            this.vec3d = null;
         }

         if (!this.flag3) {
            this.doubleValue5 = mc.player.getYaw();
            this.flag3 = true;
         }

         double var2 = this.method7607();
         int var8 = this.pattern.getIndex();
         if (var8 != this.intValue5) {
            this.intValue5 = var8;
            this.doubleValue = 0.0;
            this.doubleValue2 = 0.0;
            this.doubleValue3 = 0.0;
            this.intValue6 = 0;
            this.doubleValue4 = 0.0;
         }

         double var4;
         double var6;
         switch (var8) {
            case 1:
               float var18 = Math.max(1.0F, this.sweepLength.getValue3());
               double var10 = (double)(this.intValue4 % (int)var18) / var18;
               double var12 = this.sweepFrom.getValue3() + (this.sweepTo.getValue3() - this.sweepFrom.getValue3()) * var10;
               var12 = Math.max(2.0, var12);
               this.doubleValue += (Math.PI * 2) / var12;
               var4 = this.yawAmplitude.getValue3() * Math.sin(this.doubleValue);
               var6 = this.pitchAmplitude.getValue3() * Math.sin(this.doubleValue);
               break;
            case 2:
               double var17 = (Math.PI * 2) * this.intValue4 / Math.max(2.0F, this.period.getValue3());
               var4 = this.yawAmplitude.getValue3() * Math.sin(var17);
               var6 = this.pitchAmplitude.getValue3() * Math.cos(var17);
               break;
            case 3:
               if (this.intValue6 <= 0) {
                  double var16 = ThreadLocalRandom.current()
                     .nextDouble(
                        Math.min(this.stepMin.getValue3(), this.stepMax.getValue3()), Math.max(this.stepMin.getValue3(), this.stepMax.getValue3()) + 1.0E-6
                     );
                  this.doubleValue4 = this.method7619(var16);
                  this.intValue6 = Math.max(1, Math.round(this.stepHold.getValue3()));
               }

               this.intValue6--;
               var4 = this.doubleValue4;
               var6 = 0.0;
               break;
            case 4:
               var4 = 0.0;
               var6 = 0.0;
               break;
            case 5:
               double var15 = (Math.PI * 2) / Math.max(2.0F, this.period.getValue3());
               double var11 = Math.max(0.001F, this.yawAmplitude.getValue3());
               double var13 = ThreadLocalRandom.current().nextGaussian() * var11 * var15 * Math.sqrt(1.4 * var15);
               this.doubleValue3 = this.doubleValue3 + (-var15 * var15 * this.doubleValue2 - 1.4 * var15 * this.doubleValue3 + var13);
               this.doubleValue2 = this.doubleValue2 + this.doubleValue3;
               this.doubleValue2 = MathHelper.clamp(this.doubleValue2, -3.0 * var11, 3.0 * var11);
               var4 = this.doubleValue2;
               var6 = this.pitchAmplitude.getValue3() / var11 * this.doubleValue2 * 0.5;
               break;
            default:
               double var9 = (Math.PI * 2) * this.intValue4 / Math.max(2.0F, this.period.getValue3());
               var4 = this.yawAmplitude.getValue3() * Math.sin(var9);
               var6 = this.pitchAmplitude.getValue3() * Math.sin(var9);
         }

         double var19 = this.doubleValue5 + var4;
         double var20 = this.baseElevation.getValue3() + var6;
         this.method7608(var1, var2, var19, var20);
         this.intValue4++;
      }
   }

   private double method7607() {
      int var1 = Math.max(1, Math.round(this.ladderSteps.getValue3()));
      double var2 = Math.min(this.trajDistance.getValue3(), this.trajDistanceMax.getValue3());
      double var4 = Math.max(this.trajDistance.getValue3(), this.trajDistanceMax.getValue3());
      if (var1 != 1 && !(var4 - var2 < 1.0E-6)) {
         int var6 = Math.max(1, Math.round(this.ladderHold.getValue3()));
         int var7 = this.intValue4 / var6 % var1;
         return var2 + (var4 - var2) * var7 / (var1 - 1);
      } else {
         return var2;
      }
   }

   private void method7608(Vec3d var1, double var2, double var4, double var6) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         double var8 = Math.toRadians(var4);
         double var10 = Math.toRadians(var6);
         double var12 = var2 * Math.cos(var10);
         double var14 = this.anchor.isSelected(1) ? var1.y + mc.player.getStandingEyeHeight() : mc.player.getEyeY();
         double var16 = var1.x - var12 * Math.sin(var8);
         double var18 = var1.z + var12 * Math.cos(var8);
         Box var20 = this.otherClientPlayerEntity.getBoundingBox();
         double var21 = var14 + var2 * Math.sin(var10);
         double var23 = var21 - (var20.maxY - var20.minY) * 0.5;
         double var25 = this.flag4 ? Math.abs(MathHelper.wrapDegrees((float)(var4 - this.doubleValue6))) : 0.0;
         this.doubleValue6 = var4;
         this.flag4 = true;
         float var27 = MathHelper.wrapDegrees((float)var4 + 180.0F);
         if (this.motion.isSelected(2) || var25 > 15.0) {
            this.otherClientPlayerEntity.refreshPositionAndAngles(var16, var23, var18, var27, 0.0F);
            this.otherClientPlayerEntity.updateTrackedPosition(var16, var23, var18);
         } else if (this.motion.isSelected(1)) {
            this.otherClientPlayerEntity.updateTrackedPosition(var16, var23, var18);
            this.otherClientPlayerEntity.updateTrackedPositionAndAngles(new Vec3d(var16, var23, var18), var27, 0.0F);
         } else {
            double var28 = this.otherClientPlayerEntity.getX();
            double var30 = this.otherClientPlayerEntity.getY();
            double var32 = this.otherClientPlayerEntity.getZ();
            this.otherClientPlayerEntity.setPosition(var16, var23, var18);
            this.otherClientPlayerEntity.updateTrackedPosition(var16, var23, var18);
            this.otherClientPlayerEntity.lastX = var28;
            this.otherClientPlayerEntity.lastY = var30;
            this.otherClientPlayerEntity.lastZ = var32;
            this.otherClientPlayerEntity.lastRenderX = var28;
            this.otherClientPlayerEntity.lastRenderY = var30;
            this.otherClientPlayerEntity.lastRenderZ = var32;
            this.otherClientPlayerEntity.setYaw(var27);
            this.otherClientPlayerEntity.setPitch(0.0F);
         }

         this.otherClientPlayerEntity.setHeadYaw(var27);
         this.otherClientPlayerEntity.bodyYaw = var27;
         this.otherClientPlayerEntity.setPose(EntityPose.STANDING);
      }
   }

   private void method7609() {
      if (mc.player != null) {
         if (this.intValue2 > 0) {
            this.intValue2--;
         } else {
            Vec3d var1 = mc.player.getEntityPos();
            double var2 = this.method7616(this.minDistance, this.maxDistance);
            Vec3d var4 = this.method7614(var2);
            Vec3d var5 = var1.add(var4.x, this.method7618(this.minYOffset, this.maxYOffset), var4.z);
            float var6 = this.method7615(var5, var1);
            float var7 = 0.0F;
            this.method7610(var5, var6, var7);
            this.intValue2 = this.method7617(this.minIdlingTicks, this.maxIdlingTicks);
         }
      }
   }

   private void method7610(Vec3d var1, float var2, float var3) {
      this.method7612(RemovalReason.DISCARDED);
      this.method7611(var1, var2, var3);
   }

   private void method7611(Vec3d var1, float var2, float var3) {
      if (mc.world != null) {
         this.otherClientPlayerEntity = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), "Fake"));
         this.otherClientPlayerEntity.setId(this.method7613());
         this.otherClientPlayerEntity.refreshPositionAndAngles(var1, var2, var3);
         this.otherClientPlayerEntity.setHeadYaw(var2);
         this.otherClientPlayerEntity.bodyYaw = var2;
         this.otherClientPlayerEntity.lastBodyYaw = var2;
         this.otherClientPlayerEntity.lastHeadYaw = var2;
         mc.world.addEntity(this.otherClientPlayerEntity);
      }
   }

   private void method7612(RemovalReason var1) {
      if (this.otherClientPlayerEntity != null) {
         if (mc.world != null && mc.world.getEntityById(this.otherClientPlayerEntity.getId()) == this.otherClientPlayerEntity) {
            mc.world.removeEntity(this.otherClientPlayerEntity.getId(), var1);
         } else {
            this.otherClientPlayerEntity.setRemoved(var1);
            this.otherClientPlayerEntity.onRemoved();
         }

         this.otherClientPlayerEntity = null;
      }
   }

   private int method7613() {
      for (int var1 = 0; var1 < 1000000; var1++) {
         if (this.intValue7 <= 2146483647) {
            this.intValue7 = Integer.MAX_VALUE;
         }

         int var2 = --this.intValue7;
         if (mc.world == null || mc.world.getEntityById(var2) == null) {
            return var2;
         }
      }

      return Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 1000000);
   }

   private Vec3d method7614(double var1) {
      if (var1 <= 0.0) {
         return Vec3d.ZERO;
      } else {
         double var3 = Math.min((double)this.method7616(this.minXZOffset, this.maxXZOffset), var1);
         double var5 = Math.sqrt(var1 * var1 - var3 * var3);
         double var7 = this.method7619(var5);
         double var9 = this.method7619(var3);
         return ThreadLocalRandom.current().nextBoolean() ? new Vec3d(var7, 0.0, var9) : new Vec3d(var9, 0.0, var7);
      }
   }

   private float method7615(Vec3d var1, Vec3d var2) {
      double var3 = var2.x - var1.x;
      double var5 = var2.z - var1.z;
      return MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(var5, var3)) - 90.0F);
   }

   private float method7616(SliderSetting var1, SliderSetting var2) {
      float var3 = Math.min(var1.getValue3(), var2.getValue3());
      float var4 = Math.max(var1.getValue3(), var2.getValue3());
      return var3 == var4 ? var3 : (float)ThreadLocalRandom.current().nextDouble(var3, var4);
   }

   private int method7617(SliderSetting var1, SliderSetting var2) {
      int var3 = Math.round(Math.min(var1.getValue3(), var2.getValue3()));
      int var4 = Math.round(Math.max(var1.getValue3(), var2.getValue3()));
      return var3 >= var4 ? var3 : ThreadLocalRandom.current().nextInt(var3, var4 + 1);
   }

   private double method7618(SliderSetting var1, SliderSetting var2) {
      float var3 = this.method7616(var1, var2);
      return var3 <= 0.0F ? 0.0 : this.method7619(var3);
   }

   private double method7619(double var1) {
      return ThreadLocalRandom.current().nextBoolean() ? var1 : -var1;
   }

   @EventTarget
   public void method7620(AttackEvent var1) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         if (var1.getType4() == AttackEvent.Type.POST && this.otherClientPlayerEntity.isDead()) {
            this.otherClientPlayerEntity.setHealth(20.0F);
            new EntityStatusS2CPacket(this.otherClientPlayerEntity, (byte)35).apply((ClientPlayPacketListener)mc.player.networkHandler);
         }
      }
   }

   private static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final float floatValue2;
      private final boolean flag;
      private final boolean flag2;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, floatValue, floatValue2, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data(double var1, double var3, double var5, float var7, float var8, boolean var9, boolean var10) {
         this.doubleValue = var1;
         this.doubleValue2 = var3;
         this.doubleValue3 = var5;
         this.floatValue = var7;
         this.floatValue2 = var8;
         this.flag = var9;
         this.flag2 = var10;
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

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }
}

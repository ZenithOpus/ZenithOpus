package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.AssistSilent;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.ReplayStrokes;
import org.zenith.utility.game.AuraUtil;
import org.zenith.utility.misc.MovementInputUtils;

@ModuleInfo(
   name = "AimAssist",
   description = "module.aimassist.description",
   category = ModuleCategory.COMBAT
)
public class AimAssistModule extends Module {
   public static final AimAssistModule INSTANCE = new AimAssistModule();
   private final GroupSetting safeTarget = new GroupSetting(
      "module.aimAssist.targetSettingWindow",
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
   private final SliderSetting distance = new SliderSetting("module.aimAssist.distance", 4.0F, 0.5F, 6.0F, 0.1F);
   private final ModeSetting mode = new ModeSetting(
      "module.aimAssist.mode",
      "module.aimAssist.mode.desc",
      "module.aimAssist.mode.multiply",
      "module.aimAssist.mode.silent",
      "module.aimAssist.mode.silentStrong",
      "module.aimAssist.mode.replay"
   );
   private final BooleanSetting replayLearn = new BooleanSetting("module.aimAssist.replayLearn", "module.aimAssist.replayLearn.desc", true, this::method5154);
   private final SliderSetting replayFov = new SliderSetting(
      "module.aimAssist.replayFov", 60.0F, 5.0F, 180.0F, 5.0F, "module.aimAssist.replayFov.desc", "°", this::method5154
   );
   private final SliderSetting replayIdle = new SliderSetting(
      "module.aimAssist.replayIdle", 2.0F, 1.0F, 10.0F, 1.0F, "module.aimAssist.replayIdle.desc", "t", this::method5154
   );
   private final BooleanSetting replayPitch = new BooleanSetting("module.aimAssist.replayPitch", "module.aimAssist.replayPitch.desc", false, this::method5154);
   private final ButtonSetting replayClear = new ButtonSetting("module.aimAssist.replayClear", "t", "module.aimAssist.replayClear.desc", () -> {
      ReplayStrokes.INSTANCE.clear();
      ChatLogger.method2019(Zenith.getInstance().getLocalization().translate("module.aimAssist.replayCleared"));
   });
   private final BooleanSetting neuroDebug = new BooleanSetting("module.aimAssist.neuroDebug", false, () -> Zenith.method1502() && !this.method5153());
   private int intValue = -32768 << 16 | 0;
   private int intValue2;
   private final BooleanSetting changeX = new BooleanSetting("module.aimAssist.changeX", true, this::method5153);
   private final SliderSetting accelerationX = new SliderSetting(
      "module.aimAssist.accelerationX", 1.75F, 0.1F, 2.5F, 0.05F, () -> this.method5153() && this.changeX.isEnabled()
   );
   private final SliderSetting decelerationX = new SliderSetting(
      "module.aimAssist.decelerationX", 0.4F, 0.1F, 1.0F, 0.05F, () -> this.method5153() && this.changeX.isEnabled()
   );
   private final SliderSetting diffRangeX = new SliderSetting(
      "module.aimAssist.diffRangeX", 20.0F, 0.0F, 100.0F, 5.0F, () -> this.method5153() && this.changeX.isEnabled()
   );
   private final BooleanSetting changeY = new BooleanSetting("module.aimAssist.changeY", false, this::method5153);
   private final SliderSetting accelerationY = new SliderSetting(
      "module.aimAssist.accelerationY", 1.25F, 0.1F, 2.5F, 0.05F, () -> this.method5153() && this.changeY.isEnabled()
   );
   private final SliderSetting decelerationY = new SliderSetting(
      "module.aimAssist.decelerationY", 0.75F, 0.1F, 1.0F, 0.05F, () -> this.method5153() && this.changeY.isEnabled()
   );
   private final SliderSetting diffRangeY = new SliderSetting(
      "module.aimAssist.diffRangeY", 20.0F, 0.0F, 100.0F, 5.0F, () -> this.method5153() && this.changeY.isEnabled()
   );
   private final ModeSetting correction = new ModeSetting("module.aura.correction", "module.aura.correction.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.correction, "module.aura.correctionFocus").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.correction, "module.aura.correctionNone");
   private LivingEntity livingEntity;
   private final List<Entity> items = new ArrayList<>();

   private boolean method5153() {
      return this.mode.method2101("module.aimAssist.mode.multiply");
   }

   private boolean method5154() {
      return this.mode.method2101("module.aimAssist.mode.replay");
   }

   private AssistSilent method5155() {
      if (this.mode.method2101("module.aimAssist.mode.silent")) {
         return AssistSilent.INSTANCE;
      } else {
         return this.mode.method2101("module.aimAssist.mode.silentStrong") ? AssistSilent.INSTANCE2 : null;
      }
   }

   private AimAssistModule() {
      this.replayClear.setVisible(this::method5154);
   }

   public LivingEntity method5156() {
      return !this.isEnabled() ? null : this.livingEntity;
   }

   @EventTarget
   public void method5157(MouseRotationEvent var1) {
      if (mc.player != null && mc.world != null) {
         this.livingEntity = this.method5159();
         if (!RotationRecorderModule.INSTANCE.isEnabled()) {
            if (this.method5154()) {
               ReplayStrokes.INSTANCE
                  .method6649(
                     var1,
                     this.livingEntity,
                     this.replayLearn.isEnabled(),
                     this.replayFov.getValue3(),
                     this.replayPitch.isEnabled(),
                     this.replayIdle.getValue3() * 50.0
                  );
               if (Zenith.method1502() && this.neuroDebug.isEnabled() && mc.player.age != this.intValue) {
                  this.intValue = mc.player.age;
                  if (++this.intValue2 >= 20) {
                     this.intValue2 = 0;
                     ChatLogger.method2019(ReplayStrokes.INSTANCE.method6667());
                  }
               }
            } else {
               ReplayStrokes.INSTANCE.reset();
               if (this.method5155() == null) {
                  if (this.livingEntity != null) {
                     Rotation var2 = new Rotation(mc.player.getYaw(), mc.player.getPitch());
                     Rotation var3 = RotationUtils.method6912(this.livingEntity.getBoundingBox().getCenter().subtract(mc.player.getEyePos()));
                     float var4 = MathHelper.wrapDegrees(var3.getFloatValue() - var2.getFloatValue());
                     float var5 = MathHelper.wrapDegrees(var3.getFloatValue2() - var2.getFloatValue2());
                     if (this.changeX.isEnabled() && Math.abs(var4) > this.diffRangeX.getValue3()) {
                        var1.setDoubleValue(
                           this.method5161(
                              var1.getDoubleValue(), var4, this.accelerationX.getValue3(), Math.abs(var4) < 40.0F ? this.decelerationX.getValue3() : 1.0F
                           )
                        );
                     }

                     if (this.changeY.isEnabled() && Math.abs(var5) > this.diffRangeY.getValue3()) {
                        var1.setDoubleValue2(this.method5161(var1.getDoubleValue2(), var5, this.accelerationY.getValue3(), this.decelerationY.getValue3()));
                     }
                  }
               }
            }
         }
      } else {
         this.livingEntity = null;
      }
   }

   @EventTarget
   public void method5158(HandleInputPostEvent var1) {
      AssistSilent var2 = this.method5155();
      if (var2 != AssistSilent.INSTANCE) {
         AssistSilent.INSTANCE.reset();
      }

      if (var2 != AssistSilent.INSTANCE2) {
         AssistSilent.INSTANCE2.reset();
      }

      if (var2 != null && mc.player != null && mc.world != null && !RotationRecorderModule.INSTANCE.isEnabled()) {
         this.livingEntity = this.method5159();
         var2.method6309(this.livingEntity, this);
         if (Zenith.method1502() && this.neuroDebug.isEnabled() && ++this.intValue2 >= 20) {
            this.intValue2 = 0;
            ChatLogger.method2019(var2.method6312());
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.livingEntity = null;
      AssistSilent.INSTANCE.reset();
      AssistSilent.INSTANCE2.reset();
      ReplayStrokes.INSTANCE.shutdown();
   }

   private LivingEntity method5159() {
      float var1 = this.distance.getValue3();
      LivingEntity var2 = AuraUtil.method7446(
         this.method5160(var1),
         var1,
         true,
         ((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(1)).method5229(),
         ((BooleanSetting)this.safeTarget.getSettings().get(2)).isEnabled()
      );
      this.items.clear();
      return var2;
   }

   private List<Entity> method5160(float var1) {
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

   private double method5161(double var1, float var3, float var4, float var5) {
      float var7 = Math.signum(var1) == Math.signum(var3) ? var4 : var5;
      return var1 * var7;
   }

   @EventTarget
   private void method5162(MoveInputEvent var1) {
      if (!this.entry2.isSelected() && this.livingEntity != null) {
         Rotation var2 = RotationUtils.method6912(this.livingEntity.getBoundingBox().getCenter().subtract(mc.player.getBoundingBox().getCenter()));
         MovementInputUtils.method7426(var1, rotationManager.method7844().getFloatValue(), var2.getFloatValue());
      }
   }
}

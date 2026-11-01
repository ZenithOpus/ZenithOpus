package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AimBotModule;
import org.zenith.client.modules.impl.combat.AntiBotModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.AutoExplosionModule;
import org.zenith.client.modules.impl.combat.AutoMaceModule;
import org.zenith.client.modules.impl.combat.AutoPotionModule;
import org.zenith.client.modules.impl.combat.AutoSwapModule;
import org.zenith.client.modules.impl.combat.AutoThrowModule;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.combat.AutoTrapModule;
import org.zenith.client.modules.impl.combat.BlinkModule;
import org.zenith.client.modules.impl.combat.BowAimBotModule;
import org.zenith.client.modules.impl.combat.CriticalsModule;
import org.zenith.client.modules.impl.combat.CrystalAuraModule;
import org.zenith.client.modules.impl.combat.FakeLagModule;
import org.zenith.client.modules.impl.combat.OffHandManagerModule;
import org.zenith.client.modules.impl.combat.PvpHelperModule;
import org.zenith.client.modules.impl.combat.ReachModule;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.modules.impl.combat.RotationRecorderModule;
import org.zenith.client.modules.impl.combat.TargetPearlModule;
import org.zenith.client.modules.impl.combat.TrapTpModule;
import org.zenith.client.modules.impl.combat.TridentAimbotModule;
import org.zenith.client.modules.impl.combat.TriggerBotModule;
import org.zenith.client.modules.impl.misc.ActionMapModule;
import org.zenith.client.modules.impl.misc.AntiCheatProbeModule;
import org.zenith.client.modules.impl.misc.AuctionScanModule;
import org.zenith.client.modules.impl.misc.AutoAcceptModule;
import org.zenith.client.modules.impl.misc.AutoAuthModule;
import org.zenith.client.modules.impl.misc.AutoCapchaModule;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.client.modules.impl.misc.AutoDuelsModule;
import org.zenith.client.modules.impl.misc.AutoInventoryModule;
import org.zenith.client.modules.impl.misc.AutoLeaveModule;
import org.zenith.client.modules.impl.misc.AutoPayModule;
import org.zenith.client.modules.impl.misc.AutoRespawnModule;
import org.zenith.client.modules.impl.misc.AutoToolModule;
import org.zenith.client.modules.impl.misc.AutoUseModule;
import org.zenith.client.modules.impl.misc.AutoWebModule;
import org.zenith.client.modules.impl.misc.CameraTweaksModule;
import org.zenith.client.modules.impl.misc.ChestStealerModule;
import org.zenith.client.modules.impl.misc.ClickActionModule;
import org.zenith.client.modules.impl.misc.CommandProbeModule;
import org.zenith.client.modules.impl.misc.ContainerHelperModule;
import org.zenith.client.modules.impl.misc.CrystalOptimizerModule;
import org.zenith.client.modules.impl.misc.ElytraHelperModule;
import org.zenith.client.modules.impl.misc.EmotesModule;
import org.zenith.client.modules.impl.misc.EventTrackerModule;
import org.zenith.client.modules.impl.misc.FakePlayerModule;
import org.zenith.client.modules.impl.misc.FastBreakModule;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.client.modules.impl.misc.ItemDebugModule;
import org.zenith.client.modules.impl.misc.ItemScrollerModule;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.NoFriendDamageModule;
import org.zenith.client.modules.impl.misc.NoInteractModule;
import org.zenith.client.modules.impl.misc.OpenWalsModule;
import org.zenith.client.modules.impl.misc.PacketAuditModule;
import org.zenith.client.modules.impl.misc.PathTeleportModule;
import org.zenith.client.modules.impl.misc.PotionThrowerModule;
import org.zenith.client.modules.impl.misc.PvpSafeModule;
import org.zenith.client.modules.impl.misc.ResourcePackSpoofModule;
import org.zenith.client.modules.impl.misc.SelfFlagModule;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.client.modules.impl.misc.ServerProbeModule;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.modules.impl.misc.TapeMouseModule;
import org.zenith.client.modules.impl.misc.ViaFabricFixModule;
import org.zenith.client.modules.impl.movement.AirStuckModule;
import org.zenith.client.modules.impl.movement.AutoElytraArmorModule;
import org.zenith.client.modules.impl.movement.AutoSprintModule;
import org.zenith.client.modules.impl.movement.BoatHighJumpModule;
import org.zenith.client.modules.impl.movement.BoatLongJumpModule;
import org.zenith.client.modules.impl.movement.CastleFlyModule;
import org.zenith.client.modules.impl.movement.ElytraBoosterModule;
import org.zenith.client.modules.impl.movement.ElytraFlyModule;
import org.zenith.client.modules.impl.movement.ElytraMotionModule;
import org.zenith.client.modules.impl.movement.ElytraTargetModule;
import org.zenith.client.modules.impl.movement.GrimGlideModule;
import org.zenith.client.modules.impl.movement.GuiWalkModule;
import org.zenith.client.modules.impl.movement.NoDelayModule;
import org.zenith.client.modules.impl.movement.NoFallModule;
import org.zenith.client.modules.impl.movement.NoPushModule;
import org.zenith.client.modules.impl.movement.NoSlowModule;
import org.zenith.client.modules.impl.movement.NoSweetSlowModule;
import org.zenith.client.modules.impl.movement.NoWebModule;
import org.zenith.client.modules.impl.movement.ShulkerJumpModule;
import org.zenith.client.modules.impl.movement.SlimeFlightModule;
import org.zenith.client.modules.impl.movement.SpeedModule;
import org.zenith.client.modules.impl.movement.SpiderModule;
import org.zenith.client.modules.impl.movement.StrafeModule;
import org.zenith.client.modules.impl.movement.TimerModule;
import org.zenith.client.modules.impl.movement.VelocityModule;
import org.zenith.client.modules.impl.movement.WallBypassModule;
import org.zenith.client.modules.impl.movement.WaterRiseModule;
import org.zenith.client.modules.impl.player.AHHelperModule;
import org.zenith.client.modules.impl.player.AncientFarmModule;
import org.zenith.client.modules.impl.player.AppleFarmModule;
import org.zenith.client.modules.impl.player.AutoBrewingModule;
import org.zenith.client.modules.impl.player.AutoFarmRW;
import org.zenith.client.modules.impl.player.AutoLootModule;
import org.zenith.client.modules.impl.player.AutoMineModule;
import org.zenith.client.modules.impl.player.AutoWardenModule;
import org.zenith.client.modules.impl.player.AutoZamokModule;
import org.zenith.client.modules.impl.player.BaseFinderModule;
import org.zenith.client.modules.impl.player.BotModule;
import org.zenith.client.modules.impl.player.CropFarmerModule;
import org.zenith.client.modules.impl.player.EndLooterModule;
import org.zenith.client.modules.impl.player.TpKeyFinderModule;
import org.zenith.client.modules.impl.player.TpLootModule;
import org.zenith.client.modules.impl.player.WarpFarmModule;
import org.zenith.client.modules.impl.player.XrayBypassModule;
import org.zenith.client.modules.impl.render.AmbientParticlesModule;
import org.zenith.client.modules.impl.render.AntiInvisibleModule;
import org.zenith.client.modules.impl.render.ArrowsModule;
import org.zenith.client.modules.impl.render.BetterMinecraftModule;
import org.zenith.client.modules.impl.render.BlockESPModule;
import org.zenith.client.modules.impl.render.ChamsModule;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.modules.impl.render.CrimsonEmbersModule;
import org.zenith.client.modules.impl.render.CrosshairModule;
import org.zenith.client.modules.impl.render.ESPModule;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.client.modules.impl.render.FireWorkESPModule;
import org.zenith.client.modules.impl.render.HandsModule;
import org.zenith.client.modules.impl.render.HitParticlesModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.JumpCircleModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.modules.impl.render.NoRenderModule;
import org.zenith.client.modules.impl.render.OptimizationModule;
import org.zenith.client.modules.impl.render.ParticlesModule;
import org.zenith.client.modules.impl.render.PartyMarksModule;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.client.modules.impl.render.SwingAnimationModule;
import org.zenith.client.modules.impl.render.TargetESPModule;
import org.zenith.client.modules.impl.render.TotemParticlesModule;
import org.zenith.client.modules.impl.render.ViewArmorDurabilityModule;
import org.zenith.client.modules.impl.render.ViewModelModule;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.client.modules.impl.render.WorldModule;
import org.zenith.client.modules.impl.render.WorldTweaksModule;
import org.zenith.client.screens.nlgui.BindCapture;

public final class ModuleManager implements MinecraftAccess {
   private final List<Module> items = new CopyOnWriteArrayList<>();

   public ModuleManager() {
      this.init();
      EventManager.register(this);
   }

   private void init() {
      this.method2476();
      this.method2477();
      this.method2478();
      this.method2479();
      this.method2480();
      InterfaceModule.INSTANCE.setToggled(true);
   }

   private void method2476() {
      if (Zenith.method1502()) {
         this.method2481(RotationRecorderModule.INSTANCE);
      }

      this.method2481(AimAssistModule.INSTANCE);
      this.method2481(AuraModule.INSTANCE);
      this.method2481(ReachModule.INSTANCE);
      this.method2481(AutoSwapModule.INSTANCE);
      this.method2481(TriggerBotModule.INSTANCE);
      this.method2481(OffHandManagerModule.INSTANCE);
      this.method2481(ElytraTargetModule.INSTANCE);
      this.method2481(CriticalsModule.INSTANCE);
      this.method2481(AutoMaceModule.INSTANCE);
      this.method2481(TargetPearlModule.INSTANCE);
      this.method2481(AutoTrapModule.INSTANCE);
      this.method2481(CrystalAuraModule.INSTANCE);
      this.method2481(AutoTotemModule.INSTANCE);
      this.method2481(AntiBotModule.INSTANCE);
      this.method2481(InventorySettingModule.INSTANCE);
      this.method2481(BlinkModule.INSTANCE);
      this.method2481(TrapTpModule.INSTANCE);
      this.method2481(FakeLagModule.INSTANCE);
      this.method2481(ReachV3Module.INSTANCE);
      this.method2481(AutoThrowModule.INSTANCE);
      this.method2481(AutoPotionModule.INSTANCE);
      this.method2481(PvpHelperModule.INSTANCE);
   }

   private void method2477() {
      this.method2481(AutoSprintModule.INSTANCE);
      this.method2481(ElytraBoosterModule.INSTANCE);
      this.method2481(CastleFlyModule.INSTANCE);
      this.method2481(GrimGlideModule.INSTANCE);
      this.method2481(SlimeFlightModule.INSTANCE);
      this.method2481(ElytraFlyModule.INSTANCE);
      this.method2481(VelocityModule.INSTANCE);
      this.method2481(NoWebModule.INSTANCE);
      this.method2481(GuiWalkModule.INSTANCE);
      this.method2481(NoSlowModule.INSTANCE);
      this.method2481(NoFallModule.INSTANCE);
      this.method2481(WaterRiseModule.INSTANCE);
      this.method2481(SpiderModule.INSTANCE);
      this.method2481(SpeedModule.INSTANCE);
      this.method2481(TimerModule.INSTANCE);
      this.method2481(ElytraMotionModule.INSTANCE);
      this.method2481(AutoElytraArmorModule.INSTANCE);
      this.method2481(BoatHighJumpModule.INSTANCE);
      this.method2481(BoatLongJumpModule.INSTANCE);
      this.method2481(ShulkerJumpModule.INSTANCE);
      this.method2481(StrafeModule.INSTANCE);
      this.method2481(AirStuckModule.INSTANCE);
      this.method2481(WallBypassModule.INSTANCE);
   }

   private void method2478() {
      this.method2481(InterfaceModule.INSTANCE);
      this.method2481(BetterMinecraftModule.INSTANCE);
      this.method2481(OptimizationModule.INSTANCE);
      this.method2481(AntiInvisibleModule.INSTANCE);
      this.method2481(ArrowsModule.INSTANCE);
      this.method2481(MenuModule.INSTANCE);
      this.method2481(NoRenderModule.INSTANCE);
      this.method2481(PredictionsModule.INSTANCE);
      this.method2481(ESPModule.INSTANCE);
      this.method2481(BlockESPModule.INSTANCE);
      this.method2481(HandsModule.INSTANCE);
      this.method2481(SwingAnimationModule.INSTANCE);
      this.method2481(CrosshairModule.INSTANCE);
      this.method2481(ViewModelModule.INSTANCE);
      this.method2481(WorldTweaksModule.INSTANCE);
      this.method2481(WeatherModule.INSTANCE);
      this.method2481(WorldModule.INSTANCE);
      this.method2481(ChamsModule.INSTANCE);
      this.method2481(EntityESPModule.INSTANCE);
      this.method2481(CloudWaypointsModule.INSTANCE);
      this.method2481(PartyMarksModule.INSTANCE);
      this.method2481(TargetESPModule.INSTANCE);
      this.method2481(CrimsonEmbersModule.INSTANCE);
      this.method2481(ShaderHandModule.INSTANCE);
      this.method2481(AutoExplosionModule.INSTANCE);
      this.method2481(JumpCircleModule.INSTANCE);
      this.method2481(AmbientParticlesModule.INSTANCE);
      this.method2481(HitParticlesModule.INSTANCE);
      this.method2481(TotemParticlesModule.INSTANCE);
      this.method2481(ParticlesModule.INSTANCE);
      this.method2481(ViewArmorDurabilityModule.INSTANCE);
      this.method2481(FireWorkESPModule.INSTANCE);
   }

   private void method2479() {
      this.method2481(AutoToolModule.INSTANCE);
      this.method2481(NoDelayModule.INSTANCE);
      this.method2481(NoSweetSlowModule.INSTANCE);
      this.method2481(NoPushModule.INSTANCE);
      this.method2481(OpenWalsModule.INSTANCE);
      this.method2481(BaseFinderModule.INSTANCE);
      this.method2481(AppleFarmModule.INSTANCE);
      this.method2481(AncientFarmModule.INSTANCE);
      this.method2481(FastBreakModule.INSTANCE);
      this.method2481(AutoLootModule.INSTANCE);
      this.method2481(AutoMineModule.INSTANCE);
      this.method2481(CropFarmerModule.INSTANCE);
      this.method2481(AutoZamokModule.INSTANCE);
      this.method2481(AutoBrewingModule.INSTANCE);
      this.method2481(BotModule.INSTANCE);
      this.method2481(AutoWardenModule.INSTANCE);
      this.method2481(EndLooterModule.INSTANCE);
      this.method2481(WarpFarmModule.INSTANCE);
      this.method2481(AutoFarmRW.INSTANCE);
      this.method2481(TpLootModule.INSTANCE);
      this.method2481(TpKeyFinderModule.INSTANCE);
   }

   private void method2480() {
      this.method2481(ResourcePackSpoofModule.INSTANCE);
      this.method2481(ViaFabricFixModule.INSTANCE);
      this.method2481(CrystalOptimizerModule.INSTANCE);
      this.method2481(EmotesModule.INSTANCE);
      this.method2481(AimBotModule.INSTANCE);
      this.method2481(BowAimBotModule.INSTANCE);
      this.method2481(TridentAimbotModule.INSTANCE);
      this.method2481(AuctionScanModule.INSTANCE);
      if (Zenith.method1502()) {
         this.method2481(ItemDebugModule.INSTANCE);
         this.method2481(ServerProbeModule.INSTANCE);
         this.method2481(PacketAuditModule.INSTANCE);
         this.method2481(CommandProbeModule.INSTANCE);
         this.method2481(AntiCheatProbeModule.INSTANCE);
         this.method2481(ActionMapModule.INSTANCE);
         this.method2481(SelfFlagModule.INSTANCE);
         this.method2481(FakePlayerModule.INSTANCE);
         this.method2481(PathTeleportModule.INSTANCE);
      }

      this.method2481(ShaderESPModule.INSTANCE);
      this.method2481(ShulkerPreviewModule.INSTANCE);
      this.method2481(ServerHelperModule.INSTANCE);
      this.method2481(PotionThrowerModule.INSTANCE);
      this.method2481(ElytraHelperModule.INSTANCE);
      this.method2481(ItemScrollerModule.INSTANCE);
      this.method2481(ClickActionModule.INSTANCE);
      this.method2481(FreeCamModule.INSTANCE);
      this.method2481(PvpSafeModule.INSTANCE);
      this.method2481(CameraTweaksModule.INSTANCE);
      this.method2481(AutoAuthModule.INSTANCE);
      this.method2481(AutoCapchaModule.INSTANCE);
      this.method2481(AutoPayModule.INSTANCE);
      this.method2481(AutoDuelsModule.INSTANCE);
      this.method2481(AutoLeaveModule.INSTANCE);
      this.method2481(AHHelperModule.INSTANCE);
      this.method2481(AutoInventoryModule.INSTANCE);
      this.method2481(AutoCraftModule.INSTANCE);
      this.method2481(ContainerHelperModule.INSTANCE);
      this.method2481(NoInteractModule.INSTANCE);
      this.method2481(NoFriendDamageModule.INSTANCE);
      this.method2481(ChestStealerModule.INSTANCE);
      this.method2481(AutoAcceptModule.INSTANCE);
      this.method2481(EventTrackerModule.INSTANCE);
      this.method2481(AutoUseModule.INSTANCE);
      this.method2481(TapeMouseModule.INSTANCE);
      this.method2481(AutoRespawnModule.INSTANCE);
      this.method2481(NameProtectModule.INSTANCE);
      this.method2481(AutoWebModule.INSTANCE);
      this.method2481(XrayBypassModule.INSTANCE);
      this.method2481(StreamerModeModule.INSTANCE);
   }

   private void method2481(Module var1) {
      this.method2482(var1);
   }

   public void method2482(Module var1) {
      Objects.requireNonNull(var1, "module");
      if (this.items.stream().anyMatch(var1x -> var1x.getId().equals(var1.getId()))) {
         throw new IllegalArgumentException("Duplicate module id: " + var1.getId());
      } else {
         this.items.add(var1);
      }
   }

   public boolean method2483(Module var1) {
      if (var1 == null) {
         return false;
      } else {
         var1.setToggled(false);
         return this.items.remove(var1);
      }
   }

   public Module method2484(String var1) {
      return this.items.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public Set<Module> method2485() {
      LinkedHashSet var1 = new LinkedHashSet();

      for (Module var3 : this.items) {
         if (var3.isEnabled()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   @EventTarget
   public void method2486(KeyEvent var1) {
      if (var1.getIntValue() == 1) {
         if (!BindCapture.isActive() || mc.currentScreen == null || mc.currentScreen != Zenith.getInstance().getNLMenuScreen()) {
            if (KeyName.method6703(MenuModule.INSTANCE.getKeyCode(), MenuModule.INSTANCE.getKeyModifiers(), var1.getKeyCode(), var1.getModifiers())) {
               MenuModule.INSTANCE.toggle();
            } else if (mc.currentScreen == null) {
               for (Module var3 : this.items) {
                  if (KeyName.method6703(var3.getKeyCode(), var3.getKeyModifiers(), var1.getKeyCode(), var1.getModifiers())) {
                     var3.toggle();
                  }
               }
            }
         }
      }
   }

   @Generated
   public List<Module> getItems() {
      return this.items;
   }
}

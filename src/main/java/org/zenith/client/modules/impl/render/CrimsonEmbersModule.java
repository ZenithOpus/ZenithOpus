package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.CameraPosEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.EmberField;
import org.zenith.utility.game.EmberShards;
import org.zenith.utility.render.Glowboost;

@ModuleInfo(
   name = "Crimson Embers",
   category = ModuleCategory.RENDER,
   description = "module.crimsonEmbers.description"
)
public final class CrimsonEmbersModule extends Module {
   public static final CrimsonEmbersModule INSTANCE = new CrimsonEmbersModule();
   private static final int CONST_intValue = 8;
   private static final double CONST_doubleValue = 24.0;
   private static final int CONST_intValue2 = -53700;
   private final SliderSetting density = new SliderSetting("module.crimsonEmbers.density", 1.0F, 0.2F, 3.0F, 0.1F, "module.crimsonEmbers.density.desc", "x");
   private final SliderSetting span = new SliderSetting("module.crimsonEmbers.span", 1.0F, 0.5F, 2.0F, 0.05F, "module.crimsonEmbers.span.desc", "x");
   private final SliderSetting glow = new SliderSetting("module.crimsonEmbers.glow", 1.0F, 0.0F, 2.0F, 0.05F, "module.crimsonEmbers.glow.desc", "x");
   private final ModeSetting color = new ModeSetting(
      "module.crimsonEmbers.color",
      "module.crimsonEmbers.color.desc",
      () -> true,
      "module.crimsonEmbers.color.crimson",
      "module.particles.sync",
      "module.particles.custom"
   );
   private final ColorSetting colorSetting = new ColorSetting(
      "module.crimsonEmbers.customColor", "module.crimsonEmbers.customColor.desc", new ZenithColor(255, 46, 60), () -> this.color.isSelected(2)
   );
   private final BooleanSetting shards = new BooleanSetting("module.crimsonEmbers.shards", "module.crimsonEmbers.shards.desc", true);
   private final SliderSetting volley = new SliderSetting(
      "module.crimsonEmbers.volley", 2.0F, 1.0F, 4.0F, 1.0F, "module.crimsonEmbers.volley.desc", "", this.shards::isEnabled
   );
   private final BooleanSetting shake = new BooleanSetting("module.crimsonEmbers.shake", "module.crimsonEmbers.shake.desc", true, this.shards::isEnabled);
   private final BooleanSetting sounds = new BooleanSetting("module.crimsonEmbers.sounds", "module.crimsonEmbers.sounds.desc", true, this.shards::isEnabled);
   private final EmberShards emberShards = new EmberShards();
   private final Glowboost glowboost = new Glowboost();
   private final Queue<LivingEntity> queue = new ConcurrentLinkedQueue<>();
   private final Random random = new Random();
   private final long longValue = System.nanoTime();
   private EmberField emberField;
   private ClientWorld clientWorld;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(this.density, this.span, this.glow, this.color, this.colorSetting, this.shards, this.volley, this.shake, this.sounds);
   }

   private CrimsonEmbersModule() {
      this.emberShards.setHandler((var1, var3, var5) -> this.method7765(var1, var3, var5, SoundEvents.ITEM_TRIDENT_HIT, 0.5F, 1.35F));
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.reset();
   }

   private void reset() {
      this.emberField = null;
      this.emberShards.clear();
      this.queue.clear();
      this.longValue2 = 0L;
   }

   @EventTarget
   public void method7761(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.POST && this.shards.isEnabled()) {
         Entity var2 = var1.getEntity();
         if (var2 instanceof LivingEntity var3 && var2 != mc.player) {
            if (this.queue.size() < 8) {
               this.queue.offer(var3);
            }
         }
      }
   }

   @EventTarget
   public void method7762(CameraPosEvent var1) {
      if (this.shake.isEnabled()) {
         float var2 = this.emberShards.getFloatValue();
         if (!(var2 <= 0.001F)) {
            double var3 = (System.nanoTime() - this.longValue) / 1.0E9;
            double var5 = 0.075 * var2 * var2;
            var1.setVec3d(var1.getVec3d().add(Math.sin(var3 * 61.0) * var5, Math.sin(var3 * 53.0 + 1.3) * var5, Math.sin(var3 * 67.0 + 2.1) * var5));
         }
      }
   }

   @EventTarget
   public void method7763(WorldRenderEvent var1) {
      ClientPlayerEntity var2 = mc.player;
      if (var2 != null && mc.world != null) {
         if (mc.world != this.clientWorld) {
            this.reset();
            this.clientWorld = mc.world;
         }

         long var3 = System.nanoTime();
         float var5 = this.longValue2 == 0L ? 0.0F : Math.min(0.1F, (float)(var3 - this.longValue2) / 1.0E9F);
         this.longValue2 = var3;
         float var6 = var1.getFloatValue();
         Camera var7 = mc.getEntityRenderDispatcher().camera;
         if (var7 != null) {
            if (this.emberField == null) {
               this.emberField = new EmberField();
            }

            boolean var8 = var2.isAlive() && !var2.isSpectator() && !var2.isInvisible();
            if (var8) {
               this.emberField.method7018(var2, var6, var5, (var3 - this.longValue) / 1.0E9, this.span.getValue3());
               this.method7764(var2, var6);
               this.emberField.method7019(this.emberShards, var5, this.density.getValue3());
            } else {
               this.queue.clear();
            }

            this.emberShards.update(var5, var6);
            this.glowboost.method3232(var7, Math.max(0.03F, this.glow.getValue3()));
            this.emberShards.method7587(this.glowboost, this.color());
            this.glowboost.method3253(var1.getMatrixStack());
         }
      } else {
         this.reset();
      }
   }

   private void method7764(ClientPlayerEntity var1, float var2) {
      LivingEntity var3;
      while ((var3 = this.queue.poll()) != null) {
         if (!var3.isRemoved()
            && !(var3.squaredDistanceTo(var1) > 576.0)
            && this.emberField.method7020(this.emberShards, var3, Math.round(this.volley.getValue3()), var2)) {
            this.method7765(var1.getX(), var1.getY() + 1.4, var1.getZ(), SoundEvents.ITEM_TRIDENT_THROW.value(), 0.6F, 1.6F);
         }
      }
   }

   private int color() {
      if (this.color.isSelected(1)) {
         return Zenith.getInstance().getStyleManager().getClientColor(0).method7287() | 0xFF000000;
      } else {
         return this.color.isSelected(2) ? this.colorSetting.getZenithColor().method7287() | 0xFF000000 : -53700;
      }
   }

   private void method7765(double var1, double var3, double var5, SoundEvent var7, float var8, float var9) {
      if (this.sounds.isEnabled() && mc.world != null) {
         mc.world.playSoundClient(var1, var3, var5, var7, SoundCategory.PLAYERS, var8, var9 * (0.92F + this.random.nextFloat() * 0.16F), false);
      }
   }
}

package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.OptimizationFlags;

@ModuleInfo(
   name = "Optimization",
   category = ModuleCategory.RENDER,
   description = "module.optimization.description"
)
public final class OptimizationModule extends Module {
   public static final OptimizationModule INSTANCE = new OptimizationModule();
   private final MultiSelectSetting particles = new MultiSelectSetting(
      "module.optimization.particles",
      "module.optimization.particles.desc",
      MultiSelectSetting.Entry.method5231("module.optimization.particleLight", true),
      MultiSelectSetting.Entry.method5231("module.optimization.particlePhysics", true),
      MultiSelectSetting.Entry.method5231("module.optimization.particleLod", true),
      MultiSelectSetting.Entry.method5231("module.optimization.bubbleCache", true)
   );
   private final MultiSelectSetting blocks = new MultiSelectSetting(
      "module.optimization.blocks",
      "module.optimization.blocks.desc",
      MultiSelectSetting.Entry.method5231("module.optimization.chestCache", true),
      MultiSelectSetting.Entry.method5231("module.optimization.signCache", true),
      MultiSelectSetting.Entry.method5231("module.optimization.blockEntityCache", true),
      MultiSelectSetting.Entry.method5231("module.optimization.spawnerCull", true),
      MultiSelectSetting.Entry.method5231("module.optimization.spawnerTickCache", true),
      MultiSelectSetting.Entry.method5231("module.optimization.movingBlockFlatLight", true)
   );
   private final MultiSelectSetting entities = new MultiSelectSetting(
      "module.optimization.entities",
      "module.optimization.entities.desc",
      MultiSelectSetting.Entry.method5231("module.optimization.outlineSkip", true),
      MultiSelectSetting.Entry.method5231("module.optimization.skinLayerLod", true),
      MultiSelectSetting.Entry.method5231("module.optimization.itemLod", true)
   );
   private final MultiSelectSetting render = new MultiSelectSetting(
      "module.optimization.render",
      "module.optimization.render.desc",
      MultiSelectSetting.Entry.method5231("module.optimization.writerCache", true),
      MultiSelectSetting.Entry.method5231("module.optimization.renderLayerCache", true),
      MultiSelectSetting.Entry.method5231("module.optimization.batchingQueueCache", true),
      MultiSelectSetting.Entry.method5231("module.optimization.fontAtlas", true),
      MultiSelectSetting.Entry.method5231("module.optimization.mapAtlas", true),
      MultiSelectSetting.Entry.method5231("module.optimization.fastTextLookup", true),
      MultiSelectSetting.Entry.method5231("module.optimization.frameFence", false)
   );
   private final SliderSetting particleDistance = new SliderSetting(
      "module.optimization.particleDistance", 16.0F, 4.0F, 64.0F, 1.0F, "module.optimization.particleDistance.desc", "", null, null
   );
   private final SliderSetting spawnerDistance = new SliderSetting(
      "module.optimization.spawnerDistance", 16.0F, 4.0F, 64.0F, 1.0F, "module.optimization.spawnerDistance.desc", "", null, null
   );
   private final SliderSetting skinLodDistance = new SliderSetting(
      "module.optimization.skinLodDistance", 32.0F, 8.0F, 96.0F, 1.0F, "module.optimization.skinLodDistance.desc", "", null, null
   );
   private final SliderSetting itemLodDistance = new SliderSetting(
      "module.optimization.itemLodDistance", 12.0F, 4.0F, 48.0F, 1.0F, "module.optimization.itemLodDistance.desc", "", null, null
   );
   private boolean flag;

   private OptimizationModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.method2739();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      OptimizationFlags.clear();
      this.method2740();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      this.method2739();
   }

   public void method2739() {
      if (!this.isEnabled()) {
         OptimizationFlags.clear();
      } else {
         OptimizationFlags.flag = this.particles.method5227(0);
         OptimizationFlags.flag2 = this.particles.method5227(1);
         OptimizationFlags.flag3 = this.particles.method5227(2);
         OptimizationFlags.flag4 = this.particles.method5227(3);
         OptimizationFlags.flag5 = this.blocks.method5227(0);
         OptimizationFlags.flag6 = this.blocks.method5227(1);
         OptimizationFlags.flag7 = this.blocks.method5227(2);
         OptimizationFlags.flag8 = this.blocks.method5227(3);
         OptimizationFlags.flag9 = this.blocks.method5227(4);
         OptimizationFlags.flag10 = this.blocks.method5227(5);
         OptimizationFlags.flag11 = this.entities.method5227(0);
         OptimizationFlags.flag12 = this.entities.method5227(1);
         OptimizationFlags.flag13 = this.entities.method5227(2);
         OptimizationFlags.flag14 = this.render.method5227(0);
         OptimizationFlags.flag15 = this.render.method5227(1);
         OptimizationFlags.flag16 = this.render.method5227(2);
         OptimizationFlags.flag17 = this.render.method5227(3);
         OptimizationFlags.flag18 = this.render.method5227(4);
         OptimizationFlags.flag19 = this.render.method5227(5);
         OptimizationFlags.flag20 = this.render.method5227(6);
         OptimizationFlags.method5636(this.particleDistance.getValue3());
         OptimizationFlags.method5637(this.spawnerDistance.getValue3());
         OptimizationFlags.method5638(this.skinLodDistance.getValue3());
         OptimizationFlags.method5639(this.itemLodDistance.getValue3());
         this.method2740();
      }
   }

   private void method2740() {
      if (OptimizationFlags.flag17 != this.flag && mc.fontManager != null) {
         this.flag = OptimizationFlags.flag17;
         mc.execute(() -> mc.fontManager.setActiveFilters(mc.options));
      }
   }
}

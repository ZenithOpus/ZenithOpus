package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.game.NoRenderBlocks;
import org.zenith.utility.misc.ModuleHookDefinitions;

@ModuleInfo(
   name = "NoRender",
   category = ModuleCategory.RENDER,
   description = "module.norender.description"
)
public final class NoRenderModule extends Module {
   public static final NoRenderModule INSTANCE = new NoRenderModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 3;
   private static final int CONST_intValue5 = 4;
   private static final int CONST_intValue6 = 5;
   private final MultiSelectSetting settings = new MultiSelectSetting(
      "module.noRender.settings",
      "module.noRender.settings.desc",
      MultiSelectSetting.Entry.method5231("module.noRender.fire", true),
      MultiSelectSetting.Entry.method5231("module.noRender.badEffects", true),
      MultiSelectSetting.Entry.method5231("module.noRender.blockOverlay", true),
      MultiSelectSetting.Entry.method5231("module.noRender.darkness", true),
      MultiSelectSetting.Entry.method5231("module.noRender.totemParticles", true),
      MultiSelectSetting.Entry.method5231("module.noRender.thinGrass", false)
   );

   private NoRenderModule() {
      ModuleHookDefinitions.class125.method1646(0, var1 -> {
         if (this.method4529()) {
            var1.cancelled = true;
         }
      });
      ModuleHookDefinitions.class126.method1646(0, var1 -> {
         if (this.method4527()) {
            var1.cancelled = true;
         }
      });
      ModuleHookDefinitions.class127.method1646(0, var1 -> {
         if (this.method4531()) {
            var1.value = 0.0;
         }
      });
      ModuleHookDefinitions.class128.method1646(0, var1 -> {
         if (this.method4530() || "darkness".equals(var1.text) && this.method4531()) {
            var1.cancelled = true;
         }
      });
   }

   public boolean method4527() {
      return this.isEnabled() && this.settings.method5227(2);
   }

   public boolean method4528() {
      return InterfaceModule.INSTANCE.method6770();
   }

   public boolean method4529() {
      return this.isEnabled() && this.settings.method5227(0);
   }

   public boolean method4530() {
      return this.isEnabled() && this.settings.method5227(1);
   }

   public boolean method4531() {
      return this.isEnabled() && this.settings.method5227(3);
   }

   public boolean method4532() {
      return this.isEnabled() && this.settings.method5227(4);
   }

   public boolean method4533() {
      return this.isEnabled() && this.settings.method5227(5);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.method4534();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method4534();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      this.method4534();
   }

   private void method4534() {
      boolean var1 = this.method4533();
      if (NoRenderBlocks.enabled != var1) {
         NoRenderBlocks.enabled = var1;
         if (mc.world != null && mc.worldRenderer != null) {
            mc.worldRenderer.reload();
         }
      }
   }
}

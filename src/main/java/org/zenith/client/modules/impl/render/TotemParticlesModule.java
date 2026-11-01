package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.Class28;
import org.zenith.utility.misc.Util24;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.MatrixStackUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Totem Particles",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.totemparticles.description"
)
public final class TotemParticlesModule extends Module {
   public static final TotemParticlesModule INSTANCE = new TotemParticlesModule();
   private static final int CONST_intValue = 35;
   private static final int CONST_intValue2 = 8;
   private static final int CONST_intValue3 = 3000;
   private static final float CONST_floatValue = 2.5F;
   private static final float CONST_floatValue2 = 0.5F;
   private static final float CONST_floatValue3 = 130.0F;
   private static final float CONST_floatValue4 = 1.0F;
   private static final float CONST_floatValue5 = 1.8F;
   private static final float CONST_floatValue6 = 1.6F;
   private static final float CONST_floatValue7 = 1.0F;
   private final ModeSetting mode = new ModeSetting(
      "module.totemParticles.mode",
      "module.totemParticles.mode.desc",
      () -> true,
      "module.totemParticles.burst",
      "module.totemParticles.halo",
      "module.totemParticles.storm"
   );
   private final SliderSetting burstDensity = new SliderSetting(
      "module.totemParticles.burstDensity", 70.0F, 10.0F, 160.0F, 5.0F, "module.totemParticles.burstDensity.desc", "", () -> this.mode.isSelected(0), null
   );
   private final SliderSetting burstScale = new SliderSetting(
      "module.totemParticles.burstScale", 0.9F, 0.3F, 2.5F, 0.05F, "module.totemParticles.burstScale.desc", "x", () -> this.mode.isSelected(0), null
   );
   private final SliderSetting stormDensity = new SliderSetting(
      "module.totemParticles.stormDensity", 90.0F, 10.0F, 160.0F, 5.0F, "module.totemParticles.stormDensity.desc", "", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting stormScale = new SliderSetting(
      "module.totemParticles.stormScale", 1.0F, 0.3F, 2.5F, 0.05F, "module.totemParticles.stormScale.desc", "x", () -> this.mode.isSelected(2), null
   );
   private final ModeSetting color = new ModeSetting(
      "module.totemParticles.color",
      "module.totemParticles.colorMode.desc",
      () -> true,
      "module.totemParticles.vanilla",
      "module.particles.sync",
      "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.totemParticles.customColor", "module.totemParticles.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(2)
   );
   private final List<Class28> items = new ArrayList<>();
   private final Glowboost glowboost = new Glowboost();
   private long longValue;
   private int intValue;
   private Class28 class28;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(this.mode, this.burstDensity, this.burstScale, this.stormDensity, this.stormScale, this.color, this.customColor);
   }

   private TotemParticlesModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
   }

   @EventTarget
   public void method4696(EntityStatusEvent var1) {
      EntityStatusS2CPacket var2 = var1.getEntityStatusS2CPacket();
      if (var2.getStatus() == 35 && mc.world != null) {
         this.method4697(var2.getEntity(mc.world));
      }
   }

   public void method4697(Entity var1) {
      if (var1 != null) {
         Box var2 = var1.getBoundingBox();
         Vec3d var3 = new Vec3d((var2.minX + var2.maxX) * 0.5, var2.minY, (var2.minZ + var2.maxZ) * 0.5);
         if (this.items.size() >= 8) {
            this.items.removeFirst();
         }

         this.items
            .add(
               new Class28(
                     this.method4702(),
                     var3,
                     new Vec3d(0.0, 1.0, 0.0),
                     var1.getWidth(),
                     var1.getHeight(),
                     var2.minY,
                     3000,
                     this.intValue += 53,
                     ++this.longValue
                  )
                  .method4652(var1 instanceof LivingEntity var4 ? var4 : null)
            );
      }
   }

   @EventTarget
   public void method4698(WorldRenderEvent var1) {
      if (!this.items.isEmpty()) {
         if (mc.player != null && mc.world != null) {
            long var2 = System.currentTimeMillis();
            this.items.removeIf(var2x -> var2x.method4656(var2));
            if (!this.items.isEmpty()) {
               Camera var4 = mc.getEntityRenderDispatcher().camera;
               boolean var5 = false;

               for (Class28 var7 : this.items) {
                  if (!var5) {
                     this.glowboost.method3232(var4, method4701(this.method4702()));
                     var5 = true;
                  }

                  this.method4700(this.glowboost, var7, var7.method4655(var2), 1.0F);
               }

               if (var5) {
                  this.glowboost.method3253(var1.getMatrixStack());
               }
            }
         } else {
            this.items.clear();
         }
      }
   }

   public void method4699(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      long var6 = System.currentTimeMillis();
      if (this.class28 == null || var6 >= this.longValue2) {
         this.class28 = new Class28(this.method4702(), Vec3d.ZERO, new Vec3d(0.0, 1.0, 0.0), 0.6, 1.8, 0.0, 3000, this.intValue += 53, ++this.longValue);
         this.longValue2 = var6 + 3000L + 350L;
      }

      float var8 = this.class28.method4655(var6);
      if (!(var8 >= 1.0F)) {
         MatrixStack var9 = MatrixStackUtils.method5467(var1, var2, var3, var4);
         this.glowboost.method3233(MatrixStackUtils.method5468(), MatrixStackUtils.method5469(), method4701(this.class28.intValue));
         this.method4700(this.glowboost, this.class28, var8, var5);
         this.glowboost.method3253(var9);
      }
   }

   private void method4700(Glowboost var1, Class28 var2, float var3, float var4) {
      if (!(var3 < 0.0F) && !(var3 >= 1.0F)) {
         var1.method3235(method4701(var2.intValue) * var4);
         int var5 = this.method4703(var2);
         switch (var2.intValue) {
            case 1:
               Util24.method6029(var1, var2, var3, var5, 1.0F, 130.0F);
               break;
            case 2:
               Util24.method6030(var1, var2, var3, var5, this.stormScale.getValue3(), this.stormDensity.getValue3(), 1.6F);
               break;
            default:
               Util24.method6028(var1, var2, var3, var5, this.burstScale.getValue3(), this.burstDensity.getValue3(), 2.5F);
         }
      }
   }

   private static float method4701(int var0) {
      return switch (var0) {
         case 1 -> 1.8F;
         case 2 -> 1.0F;
         default -> 0.5F;
      };
   }

   private int method4702() {
      for (int var1 = 0; var1 < 3; var1++) {
         if (this.mode.isSelected(var1)) {
            return var1;
         }
      }

      return 0;
   }

   private int method4703(Class28 var1) {
      if (this.color.isSelected(2)) {
         return this.customColor.getZenithColor().method7287();
      } else if (this.color.isSelected(1)) {
         return Zenith.getInstance().getStyleManager().getClientColor(var1.intValue3).method7287();
      } else {
         float var2 = 60.0F + Glowboost.method3268(var1.longValue2, 0, 31) * 60.0F;
         return ZenithColor.method7297(var2 / 360.0F, 0.9F, 1.0F).method7287();
      }
   }
}

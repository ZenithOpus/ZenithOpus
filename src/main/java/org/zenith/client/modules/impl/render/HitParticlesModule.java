package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.Class28;
import org.zenith.utility.misc.Class42;
import org.zenith.utility.misc.Util28;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.MatrixStackUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Hit Particles",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.hitparticles.description"
)
public final class HitParticlesModule extends Module {
   public static final HitParticlesModule INSTANCE = new HitParticlesModule();
   private static final int CONST_intValue = 16;
   private static final float[] CONST_floatValues = new float[]{0.8F, 1.15F, 1.0F};
   private static final float CONST_floatValue = 100.0F;
   private static final float CONST_floatValue2 = 2.1F;
   private static final float CONST_floatValue3 = 110.0F;
   private static final float CONST_floatValue4 = 0.55F;
   private static final float CONST_floatValue5 = 0.3F;
   private static final float CONST_floatValue6 = 1.2F;
   private static final float CONST_floatValue7 = 1.5F;
   private static final float CONST_floatValue8 = 0.4F;
   private static final double CONST_doubleValue = 0.18;
   private final ModeSetting mode = new ModeSetting(
      "module.hitParticles.mode",
      "module.hitParticles.mode.desc",
      () -> true,
      "module.hitParticles.impact",
      "module.hitParticles.rift",
      "module.hitParticles.sparks"
   );
   private final SliderSetting impactScale = new SliderSetting(
      "module.hitParticles.impactScale", 0.6F, 0.3F, 1.5F, 0.05F, "module.hitParticles.impactScale.desc", "x", () -> this.mode.isSelected(0), null
   );
   private final SliderSetting impactLifetime = new SliderSetting(
      "module.hitParticles.impactLifetime",
      1500.0F,
      200.0F,
      2500.0F,
      50.0F,
      "module.hitParticles.impactLifetime.desc",
      "ms",
      () -> this.mode.isSelected(0),
      null
   );
   private final SliderSetting riftSpeed = new SliderSetting(
      "module.hitParticles.riftSpeed", 2.2F, 0.3F, 3.0F, 0.1F, "module.hitParticles.riftSpeed.desc", "x", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting riftLifetime = new SliderSetting(
      "module.hitParticles.riftLifetime", 1750.0F, 200.0F, 2500.0F, 50.0F, "module.hitParticles.riftLifetime.desc", "ms", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting sparksDensity = new SliderSetting(
      "module.hitParticles.sparksDensity", 100.0F, 6.0F, 100.0F, 2.0F, "module.hitParticles.sparksDensity.desc", "", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting sparksLifetime = new SliderSetting(
      "module.hitParticles.sparksLifetime",
      1150.0F,
      200.0F,
      2500.0F,
      50.0F,
      "module.hitParticles.sparksLifetime.desc",
      "ms",
      () -> this.mode.isSelected(2),
      null
   );
   private final ModeSetting color = new ModeSetting(
      "module.hitParticles.color", "module.hitParticles.colorMode.desc", () -> true, "module.particles.sync", "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.hitParticles.customColor", "module.hitParticles.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(1)
   );
   private final BooleanSetting onlyCrit = new BooleanSetting("module.hitParticles.onlyCrit", "module.hitParticles.onlyCrit.desc", false);
   private final List<Class28> items = new CopyOnWriteArrayList<>();
   private final Glowboost glowboost = new Glowboost();
   private long longValue;
   private int intValue;
   private static final Class42.Handler handler = (var0, var2, var4) -> var2 < 0.0;
   private final Class42.Handler handler2 = (var0, var2, var4) -> {
      ClientWorld var6 = mc.world;
      if (var6 == null) {
         return false;
      } else {
         BlockPos var7 = BlockPos.ofFloored(var0, var2, var4);
         if (!var6.isChunkLoaded(var7.getX() >> 4, var7.getZ() >> 4)) {
            return false;
         } else {
            BlockState var8 = var6.getBlockState(var7);
            if (var8.isAir()) {
               return false;
            } else {
               VoxelShape var9 = var8.getCollisionShape(var6, var7);
               if (var9.isEmpty()) {
                  return false;
               } else {
                  double var10 = var0 - var7.getX();
                  double var12 = var2 - var7.getY();
                  double var14 = var4 - var7.getZ();

                  for (Box var17 : var9.getBoundingBoxes()) {
                     if (var17.contains(var10, var12, var14)) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }
   };
   private Class28 class28;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(
         this.mode,
         this.impactScale,
         this.impactLifetime,
         this.riftSpeed,
         this.riftLifetime,
         this.sparksDensity,
         this.sparksLifetime,
         this.color,
         this.customColor,
         this.onlyCrit
      );
   }

   private HitParticlesModule() {
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
   public void method7792(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.POST) {
         if (mc.player != null && mc.world != null) {
            Entity var2 = var1.getEntity();
            if (var2 instanceof LivingEntity && var2 != mc.player) {
               if (!this.onlyCrit.isEnabled() || this.method7801()) {
                  int var3 = this.method7797();
                  Vec3d var4 = var3 == 0 ? var2.getEyePos() : var2.getBoundingBox().getCenter();
                  if (var3 == 1) {
                     var4 = var4.add(0.0, var2.getHeight() * 0.15, 0.0);
                  }

                  Vec3d var5 = mc.player.getEyePos().subtract(var4);
                  var5 = var5.lengthSquared() > 1.0E-6 ? var5.normalize() : new Vec3d(0.0, 0.0, -1.0);
                  Vec3d var6 = var3 == 0 ? var4.add(var5.multiply(0.18)) : var4;
                  int var7 = Math.round(this.method7798(var3) * CONST_floatValues[var3]);
                  if (this.items.size() >= 16) {
                     this.items.stream().findFirst().ifPresent(this.items::remove);
                  }

                  this.items.add(new Class28(var3, var6, var5, var2.getWidth(), var7, this.intValue += 37, ++this.longValue).method4652((LivingEntity)var2));
               }
            }
         }
      }
   }

   @EventTarget
   public void method7793(WorldRenderEvent var1) {
      if (!this.items.isEmpty()) {
         if (mc.player != null && mc.world != null) {
            long var2 = System.currentTimeMillis();
            this.items.removeIf(var2x -> var2x.method4656(var2));
            if (!this.items.isEmpty()) {
               Camera var4 = mc.getEntityRenderDispatcher().camera;
               float var5 = method7799(this.method7797());
               Class42.method7379(this.handler2);
               this.method7794(var1, var4, var5, var2, false);
               this.method7794(var1, var4, var5, var2, true);
            }
         } else {
            this.items.clear();
         }
      }
   }

   private void method7794(WorldRenderEvent var1, Camera var2, float var3, long var4, boolean var6) {
      boolean var7 = false;

      for (Class28 var9 : this.items) {
         if (var9.intValue == 0 == var6) {
            if (!var7) {
               this.glowboost.method3232(var2, var3);
               this.glowboost.setFlag(var6);
               var7 = true;
            }

            this.method7796(this.glowboost, var9, var9.method4655(var4), 1.0F);
         }
      }

      if (var7) {
         this.glowboost.method3253(var1.getMatrixStack());
      }
   }

   public void method7795(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      long var6 = System.currentTimeMillis();
      if (this.class28 == null || var6 >= this.longValue2) {
         int var8 = this.method7797();
         int var9 = Math.round(this.method7798(var8) * CONST_floatValues[var8]);
         Vec3d var10 = new Vec3d(0.3, 0.16, 0.94).normalize();
         Vec3d var11 = var8 == 0 ? new Vec3d(0.0, 1.62, 0.0) : new Vec3d(0.0, 0.9 + (var8 == 1 ? 0.27 : 0.0), 0.0);
         Vec3d var12 = var8 == 0 ? var11.add(var10.multiply(0.18)) : var11;
         this.class28 = new Class28(var8, var12, var10, 0.6, var9, this.intValue += 37, ++this.longValue);
         this.longValue2 = var6 + var9 + 350L;
      }

      float var13 = this.class28.method4655(var6);
      if (!(var13 >= 1.0F)) {
         Class42.method7379(handler);
         MatrixStack var14 = MatrixStackUtils.method5467(var1, var2, var3, var4);
         this.glowboost.method3233(MatrixStackUtils.method5468(), MatrixStackUtils.method5469(), method7799(this.class28.intValue));
         this.method7796(this.glowboost, this.class28, var13, var5);
         this.glowboost.method3253(var14);
      }
   }

   private void method7796(Glowboost var1, Class28 var2, float var3, float var4) {
      if (!(var3 < 0.0F) && !(var3 >= 1.0F)) {
         var1.method3235(method7799(var2.intValue) * var4);
         int var5 = this.method7800(var2.intValue3);
         switch (var2.intValue) {
            case 0:
               Util28.method7552(var1, var2, var3, var5, this.impactScale.getValue3(), 100.0F);
               break;
            case 1:
               Util28.method7553(var1, var2, var3, var5, this.riftSpeed.getValue3(), 0.55F, 110.0F);
               break;
            default:
               Util28.method7556(var1, var2, var3, var5, 1.5F, 1.2F, this.sparksDensity.getValue3());
         }
      }
   }

   private int method7797() {
      for (int var1 = 0; var1 < 3; var1++) {
         if (this.mode.isSelected(var1)) {
            return var1;
         }
      }

      return 0;
   }

   private float method7798(int var1) {
      return switch (var1) {
         case 0 -> this.impactLifetime.getValue3();
         case 1 -> this.riftLifetime.getValue3();
         default -> this.sparksLifetime.getValue3();
      };
   }

   private static float method7799(int var0) {
      return switch (var0) {
         case 0 -> 2.1F;
         case 1 -> 0.3F;
         default -> 0.4F;
      };
   }

   private int method7800(int var1) {
      return this.color.isSelected(1)
         ? this.customColor.getZenithColor().method7287()
         : Zenith.getInstance().getStyleManager().getClientColor(var1).method7287();
   }

   private boolean method7801() {
      ClientPlayerEntity var1 = mc.player;
      return var1 != null
         && var1.fallDistance > 0.0
         && !var1.isOnGround()
         && !var1.isClimbing()
         && !var1.isTouchingWater()
         && !var1.hasVehicle()
         && !var1.isSprinting();
   }
}

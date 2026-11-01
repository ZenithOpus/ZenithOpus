package org.zenith.utility.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({FireworkRocketEntity.class})
public abstract class MixinFireworkRocketEntity {
   @Shadow
   public LivingEntity shooter;
   @Shadow
   private int life;

   @ModifyExpressionValue(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;",
         ordinal = 0
      )}
   )
   private Vec3d hookExtendedFirework(Vec3d var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (this.shooter != var2.player || var2.player == null) {
         return var1;
      } else if (ModuleHookDefinitions.class123.active()) {
         FireworksComponent var3 = ((FireworkRocketEntity)(Object)this).getStack().get(DataComponentTypes.FIREWORKS);
         int var4 = var3 == null ? 0 : var3.flightDuration();
         Rotation var5 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         Vec3d var6 = var5 == null ? var2.player.getRotationVector() : var5.method3135();
         return ModuleHookDefinitions.class123
               .method1645(new ModuleHookDefinitions.Entry4(var1, this.shooter.getVelocity(), var6, this.life, 10 * (1 + var4)))
            .vec3d;
      } else {
         return var1;
      }
   }

   @ModifyExpressionValue(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"
      )}
   )
   private Vec3d getRotationVector(Vec3d var1) {
      if (this.shooter != MinecraftClient.getInstance().player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.method3135();
      }
   }
}

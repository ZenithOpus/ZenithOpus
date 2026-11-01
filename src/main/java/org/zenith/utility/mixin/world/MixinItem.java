package org.zenith.utility.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.Rotation;
import org.zenith.Zenith;

@Mixin({Item.class})
public class MixinItem {
   @ModifyExpressionValue(
      method = {"raycast(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/RaycastContext$FluidHandling;)Lnet/minecraft/util/hit/BlockHitResult;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;"
      )}
   )
   private static Vec3d hookFixRotation(Vec3d var0, World var1, PlayerEntity var2, FluidHandling var3) {
      Rotation var4 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return var2 == MinecraftClient.getInstance().player && var4 != null ? var4.method3135() : var0;
   }
}

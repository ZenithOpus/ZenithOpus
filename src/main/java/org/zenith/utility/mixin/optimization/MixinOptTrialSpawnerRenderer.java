package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.MobSpawnerBlockEntityRenderState;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.render.Util27;

@Mixin({TrialSpawnerBlockEntityRenderer.class})
public class MixinOptTrialSpawnerRenderer {
   @WrapOperation(
      method = {"updateRenderState(Lnet/minecraft/block/entity/TrialSpawnerBlockEntity;Lnet/minecraft/client/render/block/entity/state/MobSpawnerBlockEntityRenderState;FLnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/block/entity/TrialSpawnerBlockEntityRenderer;updateSpawnerRenderState(Lnet/minecraft/client/render/block/entity/state/MobSpawnerBlockEntityRenderState;FLnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/EntityRenderManager;DD)V"
      )}
   )
   private void zenith$spawnerEntity(
      MobSpawnerBlockEntityRenderState var1,
      float var2,
      Entity var3,
      EntityRenderManager var4,
      double var5,
      double var7,
      Operation<Void> var9,
      TrialSpawnerBlockEntity var10,
      MobSpawnerBlockEntityRenderState var11,
      float var12,
      Vec3d var13
   ) {
      Util27.method7106(var10, var13, var1, var2, var3, var4, var5, var7, var9);
   }
}

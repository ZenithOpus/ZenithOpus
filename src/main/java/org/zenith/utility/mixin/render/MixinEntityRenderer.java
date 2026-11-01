package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.game.Util4;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {
   @ModifyExpressionValue(
      method = {"getLight(Lnet/minecraft/entity/Entity;F)I"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/BlockPos;ofFloored(Lnet/minecraft/util/math/Position;)Lnet/minecraft/util/math/BlockPos;"
      )}
   )
   private BlockPos zenith$liftPetLightSample(BlockPos var1, Entity var2, float var3) {
      if (PetManager.method4559() && PetManager.method4557(var2.getId())) {
         World var4 = var2.getEntityWorld();
         BlockPos var5 = var1;

         for (int var6 = 0; var6 < 2; var6++) {
            if (var4.getLightLevel(LightType.SKY, var5) > 0 || var4.getLightLevel(LightType.BLOCK, var5) > 0) {
               return var5;
            }

            var5 = var5.up();
         }

         return var5;
      } else {
         return var1;
      }
   }

   @Redirect(
      method = {"updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"
      )
   )
   private int zenith$shaderEspGlowColor(Entity var1) {
      if (Util4.method1278(var1.getId())) {
         return Util4.method1279(var1.getId(), var1.getTeamColorValue());
      } else {
         ShaderESPModule var2 = ShaderESPModule.INSTANCE;
         if (var2 != null && var2.isEnabled() && var2.method4584(var1)) {
            return var2.method4587(var1);
         } else {
            PredictionsModule var3 = PredictionsModule.INSTANCE;
            return var3 != null && var3.method2911(var1) ? var3.method2912() : var1.getTeamColorValue();
         }
      }
   }
}

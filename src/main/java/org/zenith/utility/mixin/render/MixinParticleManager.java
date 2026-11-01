package org.zenith.utility.mixin.render;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.NoRenderModule;
import org.zenith.utility.mixin.accessors.ParticleAccessor;

@Mixin({ParticleManager.class})
public class MixinParticleManager {
   @Shadow
   public ClientWorld world;

   @Inject(
      method = {"addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$dropParticleWithoutManagerWorld(
      ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12, CallbackInfoReturnable<Particle> var14
   ) {
      if (this.world == null) {
         var14.setReturnValue(null);
      }
   }

   @Inject(
      method = {"addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideTotemEmitter(Entity var1, ParticleEffect var2, int var3, CallbackInfo var4) {
      if (var2.getType() == ParticleTypes.TOTEM_OF_UNDYING && NoRenderModule.INSTANCE.method4532()) {
         var4.cancel();
      }
   }

   @Inject(
      method = {"addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideTotemParticle(
      ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12, CallbackInfoReturnable<Particle> var14
   ) {
      if (var1.getType() == ParticleTypes.TOTEM_OF_UNDYING && NoRenderModule.INSTANCE.method4532()) {
         var14.setReturnValue(null);
      }
   }

   @Inject(
      method = {"addParticle(Lnet/minecraft/client/particle/Particle;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$dropWorldlessParticle(Particle var1, CallbackInfo var2) {
      if (var1 == null || ((ParticleAccessor)(Object)var1).zenith$getWorld() == null) {
         var2.cancel();
      }
   }
}

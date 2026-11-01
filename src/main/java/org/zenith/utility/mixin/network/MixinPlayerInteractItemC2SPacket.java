package org.zenith.utility.mixin.network;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;

@Mixin({PlayerInteractItemC2SPacket.class})
public class MixinPlayerInteractItemC2SPacket {
   @Shadow
   public float yaw;
   @Shadow
   public float pitch;

   @Inject(
      method = {"<init>(Lnet/minecraft/util/Hand;IFF)V"},
      at = {@At("RETURN")}
   )
   private void modifyRotation(Hand var1, int var2, float var3, float var4, CallbackInfo var5) {
      if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null) {
         Rotation var6 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         this.yaw = var6.getFloatValue();
         this.pitch = var6.getFloatValue2();
      }
   }
}

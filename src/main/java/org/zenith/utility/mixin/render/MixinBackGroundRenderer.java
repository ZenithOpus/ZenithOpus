package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.BlindnessEffectFogModifier;
import net.minecraft.client.render.fog.DarknessEffectFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.events.BackgroundRenderEvent;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.ModuleHookDefinitions;
import org.zenith.utility.render.Util26;

@Mixin({FogRenderer.class})
public class MixinBackGroundRenderer {
   private static final String APPLY_FOG = "applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;";
   @Unique
   private static Vector4f zenith$clearColor;

   @Redirect(
      method = {"applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/fog/FogModifier;shouldApply(Lnet/minecraft/block/enums/CameraSubmersionType;Lnet/minecraft/entity/Entity;)Z"
      )
   )
   private boolean zenith$skipEffectFog(FogModifier var1, CameraSubmersionType var2, Entity var3) {
      boolean var4 = var1 instanceof BlindnessEffectFogModifier || var1 instanceof DarknessEffectFogModifier;
      if (var4 && ModuleHookDefinitions.class128.active()) {
         String var5 = var1 instanceof DarknessEffectFogModifier ? "darkness" : "blindness";
         if (ModuleHookDefinitions.class128.method1645(new ModuleHookDefinitions.Entry3(var5)).cancelled) {
            return false;
         }
      }

      return var1.shouldApply(var2, var3);
   }

   @ModifyReturnValue(
      method = {"getFogColor(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)Lorg/joml/Vector4f;"},
      at = {@At("RETURN")}
   )
   private Vector4f zenith$fogColor(Vector4f var1, Camera var2, float var3, ClientWorld var4, int var5, float var6) {
      BackgroundRenderEvent var7 = new BackgroundRenderEvent();
      var7.setIntValue(pack(var1));
      EventManager.call(var7);
      if (!var7.isCancelled()) {
         return var1;
      } else {
         int var8 = var7.getColor();
         return new Vector4f(ColorUtils.method6424(var8), ColorUtils.method6425(var8), ColorUtils.method6426(var8), ColorUtils.method6427(var8));
      }
   }

   @Inject(
      method = {"applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/render/fog/FogData;renderDistanceEnd:F",
         opcode = 181,
         shift = Shift.AFTER
      )}
   )
   private void zenith$fog(
      Camera var1,
      int var2,
      RenderTickCounter var3,
      float var4,
      ClientWorld var5,
      CallbackInfoReturnable<Vector4f> var6,
      @Local Vector4f var7,
      @Local FogData var8
   ) {
      zenith$clearColor = null;
      BackgroundRenderEvent var9 = new BackgroundRenderEvent();
      var9.setIntValue(pack(var7));
      var9.setFloatValue2(var8.renderDistanceEnd);
      EventManager.call(var9);
      if (var9.isCancelled()) {
         float var10 = var9.getFloatValue();
         var8.environmentalStart = Float.MAX_VALUE;
         var8.environmentalEnd = Float.MAX_VALUE;
         var8.renderDistanceStart = 2.0F;
         var8.renderDistanceEnd = var10;
         var8.skyEnd = var10;
         var8.cloudEnd = var10;
         zenith$clearColor = new Vector4f(var7);
         int var11 = var9.getColor();
         var7.set(ColorUtils.method6424(var11), ColorUtils.method6425(var11), ColorUtils.method6426(var11), ColorUtils.method6427(var11));
      }
   }

   @Inject(
      method = {"applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/fog/FogRenderer;applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"
      )}
   )
   private void zenith$captureFog(
      Camera var1,
      int var2,
      RenderTickCounter var3,
      float var4,
      ClientWorld var5,
      CallbackInfoReturnable<Vector4f> var6,
      @Local Vector4f var7,
      @Local FogData var8
   ) {
      Util26.method6982(var7, var8);
   }

   @ModifyReturnValue(
      method = {"applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;"},
      at = {@At("RETURN")}
   )
   private Vector4f zenith$restoreClearColor(Vector4f var1) {
      Vector4f var2 = zenith$clearColor;
      zenith$clearColor = null;
      return var2 != null ? var2 : var1;
   }

   private static int pack(Vector4f var0) {
      return channel(var0.w) << 24 | channel(var0.x) << 16 | channel(var0.y) << 8 | channel(var0.z);
   }

   private static int channel(float var0) {
      return Math.clamp((long)((int)(var0 * 255.0F + 0.5F)), 0, 255);
   }
}

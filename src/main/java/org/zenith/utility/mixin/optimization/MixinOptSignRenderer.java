package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model.SinglePartModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util20;

@Mixin({AbstractSignBlockEntityRenderer.class})
public abstract class MixinOptSignRenderer {
   @Unique
   private final Reference2ObjectOpenHashMap<BlockState, SinglePartModel> zenith$models = new Reference2ObjectOpenHashMap();
   @Unique
   private final Reference2ObjectOpenHashMap<WoodType, SpriteIdentifier> zenith$textures = new Reference2ObjectOpenHashMap();
   @Unique
   private final Reference2ObjectOpenHashMap<SinglePartModel, SpriteIdentifier> zenith$layerIds = new Reference2ObjectOpenHashMap();
   @Unique
   private final Reference2ObjectOpenHashMap<SinglePartModel, RenderLayer> zenith$layers = new Reference2ObjectOpenHashMap();
   @Unique
   private final Reference2ObjectOpenHashMap<SpriteIdentifier, Sprite> zenith$sprites = new Reference2ObjectOpenHashMap();
   @Unique
   private int zenith$generation = Integer.MIN_VALUE;

   @Unique
   private void zenith$validate() {
      int var1 = Util20.intValue;
      if (this.zenith$generation != var1) {
         this.zenith$generation = var1;
         this.zenith$models.clear();
         this.zenith$textures.clear();
         this.zenith$layerIds.clear();
         this.zenith$layers.clear();
         this.zenith$sprites.clear();
      }
   }

   @WrapOperation(
      method = {"render(Lnet/minecraft/client/render/block/entity/state/SignBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/block/entity/AbstractSignBlockEntityRenderer;getModel(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/WoodType;)Lnet/minecraft/client/model/Model$SinglePartModel;"
      )}
   )
   private SinglePartModel zenith$cachedModel(AbstractSignBlockEntityRenderer var1, BlockState var2, WoodType var3, Operation<SinglePartModel> var4) {
      if (!OptimizationFlags.flag6) {
         return (SinglePartModel)var4.call(new Object[]{var1, var2, var3});
      } else {
         this.zenith$validate();
         SinglePartModel var5 = (SinglePartModel)(Object)this.zenith$models.get(var2);
         if (var5 == null) {
            var5 = (SinglePartModel)var4.call(new Object[]{var1, var2, var3});
            if (var5 != null) {
               this.zenith$models.put(var2, var5);
            }
         }

         return var5;
      }
   }

   @WrapOperation(
      method = {"renderSign(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model$SinglePartModel;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/block/entity/AbstractSignBlockEntityRenderer;getTextureId(Lnet/minecraft/block/WoodType;)Lnet/minecraft/client/util/SpriteIdentifier;"
      )}
   )
   private SpriteIdentifier zenith$cachedTexture(AbstractSignBlockEntityRenderer var1, WoodType var2, Operation<SpriteIdentifier> var3) {
      if (!OptimizationFlags.flag6) {
         return (SpriteIdentifier)var3.call(new Object[]{var1, var2});
      } else {
         this.zenith$validate();
         SpriteIdentifier var4 = (SpriteIdentifier)(Object)this.zenith$textures.get(var2);
         if (var4 == null) {
            var4 = (SpriteIdentifier)var3.call(new Object[]{var1, var2});
            if (var4 != null) {
               this.zenith$textures.put(var2, var4);
            }
         }

         return var4;
      }
   }

   @WrapOperation(
      method = {"renderSign(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model$SinglePartModel;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/util/SpriteIdentifier;getRenderLayer(Ljava/util/function/Function;)Lnet/minecraft/client/render/RenderLayer;"
      )}
   )
   private RenderLayer zenith$cachedLayer(
      SpriteIdentifier var1,
      Function<Identifier, RenderLayer> var2,
      Operation<RenderLayer> var3,
      MatrixStack var4,
      int var5,
      WoodType var6,
      SinglePartModel var7
   ) {
      if (OptimizationFlags.flag6 && var7 != null) {
         this.zenith$validate();
         RenderLayer var8 = (RenderLayer)(Object)this.zenith$layers.get(var7);
         if (var8 == null || this.zenith$layerIds.get(var7) != var1) {
            var8 = (RenderLayer)var3.call(new Object[]{var1, var2});
            this.zenith$layers.put(var7, var8);
            this.zenith$layerIds.put(var7, var1);
         }

         return var8;
      } else {
         return (RenderLayer)var3.call(new Object[]{var1, var2});
      }
   }

   @WrapOperation(
      method = {"renderSign(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model$SinglePartModel;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/texture/SpriteHolder;getSprite(Lnet/minecraft/client/util/SpriteIdentifier;)Lnet/minecraft/client/texture/Sprite;"
      )}
   )
   private Sprite zenith$cachedSprite(SpriteHolder var1, SpriteIdentifier var2, Operation<Sprite> var3) {
      if (!OptimizationFlags.flag6) {
         return (Sprite)var3.call(new Object[]{var1, var2});
      } else {
         this.zenith$validate();
         Sprite var4 = (Sprite)(Object)this.zenith$sprites.get(var2);
         if (var4 == null) {
            var4 = (Sprite)var3.call(new Object[]{var1, var2});
            if (var4 != null) {
               this.zenith$sprites.put(var2, var4);
            }
         }

         return var4;
      }
   }
}

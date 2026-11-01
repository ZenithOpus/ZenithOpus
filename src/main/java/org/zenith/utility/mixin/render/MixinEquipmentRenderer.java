package org.zenith.utility.mixin.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.render.ViewArmorDurabilityModule;

@Mixin({EquipmentRenderer.class})
public class MixinEquipmentRenderer implements MinecraftAccess {
   @Unique
   private ItemStack renderStack = ItemStack.EMPTY;
   @Unique
   private boolean renderSelf;

   @Inject(
      method = {"render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V"},
      at = {@At("HEAD")}
   )
   private void onRenderHead(
      LayerType var1,
      RegistryKey<EquipmentAsset> var2,
      Model var3,
      Object var4,
      ItemStack var5,
      MatrixStack var6,
      OrderedRenderCommandQueue var7,
      int var8,
      Identifier var9,
      int var10,
      int var11,
      CallbackInfo var12
   ) {
      this.renderStack = var5;
      this.renderSelf = var4 instanceof PlayerEntityRenderState var13 && mc.player != null && var13.id == mc.player.getId();
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V"},
      at = {@At("TAIL")}
   )
   private void onRenderTail(
      LayerType var1,
      RegistryKey<EquipmentAsset> var2,
      Model var3,
      Object var4,
      ItemStack var5,
      MatrixStack var6,
      OrderedRenderCommandQueue var7,
      int var8,
      Identifier var9,
      int var10,
      int var11,
      CallbackInfo var12
   ) {
      this.renderStack = ItemStack.EMPTY;
      this.renderSelf = false;
   }

   @ModifyArg(
      method = {"render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/command/RenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
      ),
      index = 6
   )
   private int onModifyColor(int var1) {
      if (mc.player == null || mc.world == null) {
         return var1;
      } else if (this.renderStack != null && !this.renderStack.isEmpty()) {
         ViewArmorDurabilityModule var2 = ViewArmorDurabilityModule.INSTANCE;
         if (!var2.isEnabled()) {
            return var1;
         } else {
            return !var2.method7550(this.renderStack, this.renderSelf) ? var1 : var2.method7551(this.renderStack);
         }
      } else {
         return var1;
      }
   }
}

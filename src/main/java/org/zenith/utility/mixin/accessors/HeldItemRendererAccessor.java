package org.zenith.utility.mixin.accessors;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({HeldItemRenderer.class})
public interface HeldItemRendererAccessor {
   @Accessor("mainHand")
   ItemStack zenith$getMainHand();

   @Accessor("mainHand")
   void zenith$setMainHand(ItemStack var1);

   @Accessor("offHand")
   ItemStack zenith$getOffHand();

   @Accessor("offHand")
   void zenith$setOffHand(ItemStack var1);

   @Accessor("equipProgressMainHand")
   float zenith$getEquipProgressMainHand();

   @Accessor("equipProgressMainHand")
   void zenith$setEquipProgressMainHand(float var1);

   @Accessor("lastEquipProgressMainHand")
   float zenith$getPrevEquipProgressMainHand();

   @Accessor("lastEquipProgressMainHand")
   void zenith$setPrevEquipProgressMainHand(float var1);

   @Accessor("equipProgressOffHand")
   float zenith$getEquipProgressOffHand();

   @Accessor("equipProgressOffHand")
   void zenith$setEquipProgressOffHand(float var1);

   @Accessor("lastEquipProgressOffHand")
   float zenith$getPrevEquipProgressOffHand();

   @Accessor("lastEquipProgressOffHand")
   void zenith$setPrevEquipProgressOffHand(float var1);

   @Invoker("renderArmHoldingItem")
   void zenith$renderArmHoldingItem(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, float var5, Arm var6);
}

package org.zenith.utility.network;

import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.PlayerInput;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.misc.InventorySettingModule;

public final class Class33 implements MinecraftAccess {
   private static final int CONST_intValue = 20;
   private Predicate<ItemStack> predicate;
   private int intValue;
   private boolean flag;

   public boolean pending() {
      return this.predicate != null;
   }

   public boolean method5302(Predicate<ItemStack> var1) {
      return mc.player != null && this.method5306(var1) != null;
   }

   public void method5303(Predicate<ItemStack> var1) {
      if (this.predicate == null && mc.player != null) {
         this.predicate = var1;
         this.intValue = mc.player.age;
         this.flag = false;
      }
   }

   public void cancel() {
      this.predicate = null;
      this.flag = false;
   }

   public void onMoveInput(MoveInputEvent var1) {
      if (this.flag && this.predicate != null) {
         var1.method3044();
      }
   }

   public boolean method5304() {
      if (this.predicate != null && mc.player != null && mc.interactionManager != null) {
         if (mc.player.age - this.intValue <= 20 && mc.player.currentScreenHandler == mc.player.playerScreenHandler) {
            Slot var1 = this.method5306(this.predicate);
            if (var1 == null) {
               this.cancel();
               return false;
            } else if (InventorySettingModule.INSTANCE.method2397() && this.method5305()) {
               this.flag = true;
               if (mc.player.isSprinting()) {
                  mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                  mc.player.setSprinting(false);
               }

               return false;
            } else {
               InventoryUtils.method901(var1, this.method5307(), false);
               InventoryUtils.closeScreen();
               this.cancel();
               return true;
            }
         } else {
            this.cancel();
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean method5305() {
      PlayerInput var1 = mc.player.lastPlayerInput;
      return var1.jump() || var1.forward() || var1.backward() || var1.left() || var1.right() || mc.player.isSprinting();
   }

   private Slot method5306(Predicate<ItemStack> var1) {
      for (Slot var3 : mc.player.playerScreenHandler.slots) {
         if (var3.id >= 9 && var3.id <= 35 && var1.test(var3.getStack())) {
            return var3;
         }
      }

      return null;
   }

   private int method5307() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      int var3 = mc.player.getInventory().getSelectedSlot();

      for (int var2 = 8; var2 >= 0; var2--) {
         if (var2 != var3 && !method5308(mc.player.getInventory().getStack(var2))) {
            return var2;
         }
      }

      return var3 == 8 ? 7 : 8;
   }

   private static boolean method5308(ItemStack var0) {
      return var0.isIn(ItemTags.SWORDS)
         || var0.isIn(ItemTags.AXES)
         || var0.isOf(Items.TOTEM_OF_UNDYING)
         || var0.isOf(Items.GOLDEN_APPLE)
         || var0.isOf(Items.ENCHANTED_GOLDEN_APPLE)
         || var0.isOf(Items.END_CRYSTAL)
         || var0.isOf(Items.OBSIDIAN)
         || var0.isOf(Items.ENDER_PEARL)
         || var0.isOf(Items.COBWEB)
         || var0.isOf(Items.RESPAWN_ANCHOR)
         || var0.isOf(Items.GLOWSTONE)
         || var0.isOf(Items.MACE);
   }
}

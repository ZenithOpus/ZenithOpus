package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.Mutable;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.Util17;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "AppleFarm",
   category = ModuleCategory.PLAYER,
   description = "module.applefarm.description"
)
public final class AppleFarmModule extends Module {
   public static final AppleFarmModule INSTANCE = new AppleFarmModule();
   private static final double CONST_doubleValue = 10.0;
   private boolean flag;

   private AppleFarmModule() {
   }

   @Override
   public void onDisable() {
      this.flag = false;
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (mc.player.age % 600 < 10) {
            if (mc.player.getMainHandStack().getItem() instanceof AxeItem) {
               mc.player.getInventory().setSelectedSlot((int)MathUtils.method2651(0.0, 8.0));
            }
         } else if (!this.method4103(19)) {
            if (KeyBindUtils.method2630(AppleFarmModule.class) && this.method4105()) {
               BlockPos var2 = this.method4111(5.0, var0 -> var0.isIn(BlockTags.LEAVES), this::method4110);
               if (var2 != null) {
                  this.method4087(var2, var0 -> var0.getItem() instanceof HoeItem);
               } else {
                  BlockPos var3 = this.method4111(5.0, var0 -> var0.isIn(BlockTags.LOGS), Vec3i::getY);
                  if (var3 != null) {
                     this.method4087(var3, var0 -> var0.getItem() instanceof AxeItem);
                  } else if (mc.currentScreen instanceof GenericContainerScreen) {
                     this.method4089();
                  } else if (this.method4099(Items.BONE) > 0 && this.method4099(Items.BONE_MEAL) == 0) {
                     this.method4090();
                  } else if (!this.method4101() && !this.method4102()) {
                     BlockPos var4 = this.method4112(16.0, var0 -> var0.isIn(BlockTags.DIRT));
                     if (var4 != null) {
                        if (this.method4099(Items.STICK) <= 128 && this.method4099(Items.OAK_SAPLING) <= 128) {
                           this.method4088(var4);
                        } else {
                           this.method4091();
                        }
                     }
                  } else {
                     this.method4089();
                  }
               }
            }
         }
      }
   }

   private void method4087(BlockPos var1, Predicate<ItemStack> var2) {
      if (!var2.test(mc.player.getMainHandStack())) {
         this.method4094(var2);
      } else if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem) || !(mc.player.getAttackCooldownProgress(0.0F) <= 0.15F)) {
         Vec3d var3 = var1.toCenterPos();
         if (this.method4107(var3)) {
            Vec3d var4 = new Box(var1).raycast(mc.player.getEyePos(), var3).orElse(var3);
            mc.interactionManager.updateBlockBreakingProgress(var1, Direction.getFacing(var4.subtract(var3)));
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void method4088(BlockPos var1) {
      boolean var2;
      Predicate<net.minecraft.item.ItemStack> var3 = (var2 = mc.world.getBlockState(var1.up()).getBlock() instanceof SaplingBlock)
         ? var0 -> var0.isOf(Items.BONE_MEAL)
         : var0 -> var0.getItem() instanceof BlockItem var1x && var1x.getBlock() instanceof SaplingBlock;
      if (!var3.test(mc.player.getMainHandStack())) {
         this.method4094(var3);
      } else {
         Vec3d var4 = new Vec3d(var1.getX() + 0.5, var1.getY() + 1, var1.getZ() + 0.5);
         if (this.method4107(var4) && mc.player.age % 4 == 0) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var4, Direction.UP, var2 ? var1.up() : var1, false));
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void method4089() {
      if (!(mc.currentScreen instanceof GenericContainerScreen var1)) {
         this.method4092(this.method4112(5.0, var1x -> var1x.isOf(this.method4101() ? Blocks.CHEST : Blocks.BARREL)));
      } else if (mc.player.age % 50 == 0) {
         mc.player.closeHandledScreen();
      } else if (mc.player.age % 2 == 0) {
         ScreenHandler var6 = var1.getScreenHandler();
         int var7;
         if (var1.getTitle().getContent() instanceof TranslatableTextContent var4 && var4.getKey().contains("chest")) {
            var7 = this.method4093(var6, false, 5, var0 -> var0.isOf(Items.APPLE) || var0.isOf(Items.OAK_LOG));
         } else {
            var7 = this.method4102() ? this.method4093(var6, true, 3, this::method4100) : 0;
         }

         if (var7 == 0) {
            mc.player.closeHandledScreen();
         }
      }
   }

   private void method4090() {
      if (mc.player.age % 2 == 0) {
         PlayerScreenHandler var1 = mc.player.playerScreenHandler;
         if (var1.getSlot(0).getStack().isOf(Items.BONE_MEAL)) {
            this.method4096(var1, 0, 0, SlotActionType.QUICK_MOVE);
            mc.player.closeHandledScreen();
         } else {
            int var2;
            if ((var2 = this.method4098(Items.BONE)) >= 0) {
               this.method4096(var1, var2, 0, SlotActionType.PICKUP);
               this.method4096(var1, 1, 0, SlotActionType.PICKUP);
            }
         }
      }
   }

   private void method4091() {
      float var1;
      Util17.method3332((var1 = this.method4108()) * 10.0F, MathHelper.clamp(var1 / 4.0F, -30.0F, 30.0F));
      if (mc.player.age % 5 == 0) {
         PlayerScreenHandler var2 = mc.player.playerScreenHandler;
         int var3 = -1;
         int var4 = -1;

         for (Slot var6 : var2.slots) {
            if (this.method4097(var6) && var6.getStack().isOf(Items.OAK_SAPLING) && var6.getStack().getCount() > var4) {
               var4 = var6.getStack().getCount();
               var3 = var6.id;
            }
         }

         for (Slot var8 : var2.slots) {
            if (this.method4097(var8) && (var8.getStack().isOf(Items.STICK) || var8.getStack().isOf(Items.OAK_SAPLING) && var8.id != var3)) {
               this.method4096(var2, var8.id, 1, SlotActionType.THROW);
            }
         }
      }
   }

   private void method4092(BlockPos var1) {
      if (var1 != null && this.method4107(var1.toCenterPos()) && mc.player.age % 4 == 0) {
         Vec3d var2 = var1.toCenterPos();
         Vec3d var3 = new Box(var1).raycast(mc.player.getEyePos(), var2).orElse(var2);
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(var3, Direction.getFacing(var3.subtract(var2)), var1, false));
      }
   }

   private int method4093(ScreenHandler var1, boolean var2, int var3, Predicate<ItemStack> var4) {
      int var5 = 0;

      for (Slot var7 : var1.slots) {
         if (var5 >= var3) {
            break;
         }

         if (this.method4097(var7) != var2 && var4.test(var7.getStack())) {
            this.method4096(var1, var7.id, 0, SlotActionType.QUICK_MOVE);
            var5++;
         }
      }

      return var5;
   }

   private void method4094(Predicate<ItemStack> var1) {
      int var2;
      if ((var2 = IntStream.range(0, 36).filter(var1x -> var1.test(mc.player.getInventory().getStack(var1x))).findFirst().orElse(-1)) >= 0) {
         if (var2 < 9) {
            mc.player.getInventory().setSelectedSlot(var2);
         } else if (mc.player.age % 4 == 0) {
            int var3 = IntStream.range(0, 9)
               .filter(var0 -> mc.player.getInventory().getStack(var0).isEmpty())
               .findFirst()
               .orElse(mc.player.getInventory().getSelectedSlot());
            InventoryUtils.method935(this.method4095(var2), this.method4095(var3), true, false);
            mc.player.getInventory().setSelectedSlot(var3);
         }
      }
   }

   private int method4095(int var1) {
      return var1 < 9 ? var1 + 36 : var1;
   }

   private void method4096(ScreenHandler var1, int var2, int var3, SlotActionType var4) {
      mc.interactionManager.clickSlot(var1.syncId, var2, var3, var4, mc.player);
   }

   private boolean method4097(Slot var1) {
      return var1.inventory == mc.player.getInventory();
   }

   private int method4098(Item var1) {
      for (Slot var3 : mc.player.playerScreenHandler.slots) {
         if (this.method4097(var3) && var3.getStack().isOf(var1)) {
            return var3.id;
         }
      }

      return -1;
   }

   private int method4099(Item var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (var4.getItem() == var1) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private boolean method4100(ItemStack var1) {
      return var1.getItem() instanceof BlockItem var2 && !(var2.getBlock() instanceof SaplingBlock)
         ? !var1.isOf(Items.BONE_MEAL) || var1.isOf(Items.BONE) && this.method4099(Items.BONE_MEAL) == 0
         : true;
   }

   private boolean method4101() {
      return this.method4099(Items.APPLE) > 128 || this.method4099(Items.OAK_LOG) > 192;
   }

   private boolean method4102() {
      return this.method4099(Items.OAK_SAPLING) == 0 || this.method4099(Items.BONE_MEAL) == 0;
   }

   private boolean method4103(int var1) {
      if (mc.player.getHungerManager().getFoodLevel() < var1 && KeyBindUtils.method2630(AppleFarmModule.class)) {
         int var2 = -1;
         int var3 = 32767 << 16 | (char)-1;

         for (int var4 = 0; var4 < 36 && var3 > 0; var4++) {
            ItemStack var5 = mc.player.getInventory().getStack(var4);
            if (!var5.isEmpty()
               && var5.getUseAction() == UseAction.EAT
               && !var5.contains(DataComponentTypes.CUSTOM_NAME)
               && !var5.isOf(Items.CHORUS_FRUIT)
               && !var5.isOf(Items.PUFFERFISH)
               && !var5.isOf(Items.DRIED_KELP)) {
               int var6;
               if (var5.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
                  var6 = 3;
               } else if (var5.isOf(Items.GOLDEN_APPLE)) {
                  var6 = 2;
               } else {
                  var6 = this.method4104(var5) ? 1 : 0;
               }

               if (var6 < var3) {
                  var3 = var6;
                  var2 = var4;
               }
            }
         }

         if (var2 < 0) {
            return false;
         } else if (var2 > 8) {
            if (mc.player.age % 5 == 0) {
               this.method4096(mc.player.playerScreenHandler, this.method4095(var2), 8, SlotActionType.SWAP);
            }

            return true;
         } else {
            mc.player.getInventory().setSelectedSlot(var2);
            if (!mc.player.isUsingItem() || mc.player.getActiveHand() != Hand.MAIN_HAND) {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean method4104(ItemStack var1) {
      return var1.isOf(Items.BEEF)
         || var1.isOf(Items.PORKCHOP)
         || var1.isOf(Items.CHICKEN)
         || var1.isOf(Items.MUTTON)
         || var1.isOf(Items.RABBIT)
         || var1.isOf(Items.COD)
         || var1.isOf(Items.SALMON)
         || var1.isOf(Items.POTATO);
   }

   private boolean method4105() {
      ItemStack var1 = mc.player.getMainHandStack();
      if (this.method4106(var1) > 10.0) {
         this.flag = false;
         return true;
      } else if (var1.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
            .getLevel(mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.MENDING))
         < 1) {
         if (!this.flag) {
            this.flag = true;
            ChatLogger.method2019("На предмете нету починки, отмена.");
         }

         return true;
      } else {
         if (!this.flag) {
            this.flag = true;
            ChatLogger.method2019("Прочность ниже 10%: починка бутылками опыта не перенесена, фарм ждёт");
         }

         return false;
      }
   }

   private double method4106(ItemStack var1) {
      return var1.isDamageable() && var1.getMaxDamage() > 0 ? (1.0 - (double)var1.getDamage() / var1.getMaxDamage()) * 100.0 : 100.0;
   }

   private boolean method4107(Vec3d var1) {
      Util17.method3329(var1);
      return Util17.method3330(var1, 20.0F);
   }

   private float method4108() {
      float var1;
      return (float)(
         (
               Math.sin((var1 = mc.player.age + mc.getRenderTickCounter().getTickProgress(false)) * 0.31F) * 0.5
                  + Math.sin(var1 * 0.73F + 1.1F) * 0.30000003042305273
                  + Math.sin(var1 * 1.7F + 2.6F) * 0.2000000149681302
            )
            * 8.0
      );
   }

   private double method4109(BlockPos var1) {
      return mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var1));
   }

   private double method4110(BlockPos var1) {
      Vec3d var2 = mc.player.getEyePos();
      Vec3d var3 = mc.player.getRotationVec(1.0F);
      Vec3d var4 = Vec3d.ofCenter(var1).subtract(var2);
      double var5;
      return (var5 = var4.dotProduct(var3)) <= 0.0 ? 1.7976922776554332E308 : var4.subtract(var3.multiply(var5)).lengthSquared();
   }

   private BlockPos method4111(double var1, Predicate<BlockState> var3, ToDoubleFunction<BlockPos> var4) {
      return this.method4113(var1, var3, var4, false);
   }

   private BlockPos method4112(double var1, Predicate<BlockState> var3) {
      return this.method4113(var1, var3, null, true);
   }

   private BlockPos method4113(double var1, Predicate<BlockState> var3, ToDoubleFunction<BlockPos> var4, boolean var5) {
      BlockPos var6 = mc.player.getBlockPos();
      Vec3d var7 = mc.player.getEyePos();
      BlockPos var8 = null;
      double var9 = 1.7976922776554332E308;
      double var11 = var1 * var1;
      int var13 = (int)Math.ceil(var1);
      Mutable var14 = new Mutable();

      for (int var15 = -var13; var15 <= var13; var15++) {
         for (int var16 = -var13; var16 <= var13; var16++) {
            for (int var17 = -var13; var17 <= var13; var17++) {
               double var18 = var6.getX() + var15 + 0.5 - var7.x;
               double var20 = var6.getY() + var16 + 0.5 - var7.y;
               double var22 = var6.getZ() + var17 + 0.5 - var7.z;
               double var24;
               if (!((var24 = var18 * var18 + var20 * var20 + var22 * var22) > var11)) {
                  var14.set(var6.getX() + var15, var6.getY() + var16, var6.getZ() + var17);
                  double var26;
                  if (var3.test(mc.world.getBlockState(var14)) && (var26 = var5 ? var24 : var4.applyAsDouble(var14)) < var9) {
                     var9 = var26;
                     var8 = var14.toImmutable();
                  }
               }
            }
         }
      }

      return var8;
   }
}

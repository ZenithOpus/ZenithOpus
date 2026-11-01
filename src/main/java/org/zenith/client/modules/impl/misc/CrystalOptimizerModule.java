package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "CrystalOptimizer",
   category = ModuleCategory.MISC,
   description = "module.crystaloptimizer.description"
)
public final class CrystalOptimizerModule extends Module {
   public static final CrystalOptimizerModule INSTANCE = new CrystalOptimizerModule();
   private static final double CONST_doubleValue = 3.0;
   private static final double CONST_doubleValue2 = 3.0;
   private static final double CONST_doubleValue3 = 2.0;
   private static final Set<Item> items = Set.of(
      Items.NETHERITE_HELMET,
      Items.NETHERITE_CHESTPLATE,
      Items.NETHERITE_LEGGINGS,
      Items.NETHERITE_BOOTS,
      Items.DIAMOND_HELMET,
      Items.DIAMOND_CHESTPLATE,
      Items.DIAMOND_LEGGINGS,
      Items.DIAMOND_BOOTS,
      Items.NETHERITE_SWORD,
      Items.DIAMOND_SWORD,
      Items.NETHERITE_PICKAXE,
      Items.DIAMOND_PICKAXE,
      Items.NETHERITE_SHOVEL,
      Items.DIAMOND_SHOVEL,
      Items.NETHERITE_AXE,
      Items.DIAMOND_AXE,
      Items.TOTEM_OF_UNDYING,
      Items.END_CRYSTAL,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.GOLDEN_APPLE,
      Items.ENDER_PEARL,
      Items.TRIDENT,
      Items.CROSSBOW,
      Items.ELYTRA
   );
   private final BooleanSetting protectValuableDrops = new BooleanSetting(
      "module.crystalOptimizer.protectValuableDrops", "module.crystalOptimizer.protectValuableDrops.desc", true
   );
   private EndCrystalEntity endCrystalEntity;

   private CrystalOptimizerModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.endCrystalEntity = this.method5234();
         if (this.endCrystalEntity != null && mc.options.useKey.isPressed()) {
            mc.itemUseCooldown = 0;
            mc.interactionManager.attackEntity(mc.player, this.endCrystalEntity);
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      } else {
         this.endCrystalEntity = null;
      }
   }

   @EventTarget
   public void method5233(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.POST && var1.getEntity() instanceof EndCrystalEntity var2) {
         var2.remove(RemovalReason.DISCARDED);
      }
   }

   public EndCrystalEntity getEndCrystalEntity() {
      return this.endCrystalEntity;
   }

   private EndCrystalEntity method5234() {
      for (Entity var2 : mc.world.getEntities()) {
         if (var2 instanceof EndCrystalEntity var3
            && !(mc.player.distanceTo(var3) > 3.0)
            && this.method5235(var3)
            && (!this.protectValuableDrops.isEnabled() || !this.method5236(var3.getBlockPos()))) {
            return var3;
         }
      }

      return null;
   }

   private boolean method5235(EndCrystalEntity var1) {
      Vec3d var2 = mc.player.getEyePos();
      Vec3d var3 = var2.add(mc.player.getRotationVec(1.0F).multiply(3.0));
      EntityHitResult var4 = ProjectileUtil.getEntityCollision(mc.world, mc.player, var2, var3, new Box(var2, var3).expand(1.0), var1x -> var1x == var1, 0.3F);
      if (var4 != null) {
         return true;
      } else {
         BlockHitResult var5 = mc.world.raycast(new RaycastContext(var2, var3, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
         return var5.getType() == Type.BLOCK && var5.getBlockPos().equals(var1.getBlockPos().down());
      }
   }

   private boolean method5236(BlockPos var1) {
      Box var2 = new Box(var1.getX() - 3.0, var1.getY() - 3.0, var1.getZ() - 3.0, var1.getX() + 3.0 + 1.0, var1.getY() + 3.0 + 2.0, var1.getZ() + 3.0 + 1.0);
      return mc.world
         .getOtherEntities(null, var2)
         .stream()
         .filter(ItemEntity.class::isInstance)
         .map(ItemEntity.class::cast)
         .map(var0 -> var0.getStack().getItem())
         .anyMatch(items::contains);
   }
}

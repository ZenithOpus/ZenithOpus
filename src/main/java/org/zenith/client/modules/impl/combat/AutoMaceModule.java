package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoMace",
   category = ModuleCategory.COMBAT,
   description = "module.autoMace.description"
)
public final class AutoMaceModule extends Module {
   public static final AutoMaceModule INSTANCE = new AutoMaceModule();
   private static final float CONST_floatValue = 1.5F;
   private static final double CONST_doubleValue = 0.5;
   private final SliderSetting minFall = new SliderSetting("module.autoMace.minFall", 3.0F, 1.5F, 30.0F, 0.5F, "module.autoMace.minFall.desc", "b");
   private final SliderSetting predictTicks = new SliderSetting(
      "module.autoMace.predictTicks", 40.0F, 5.0F, 100.0F, 1.0F, "module.autoMace.predictTicks.desc", "t"
   );
   private final SliderSetting swapRadius = new SliderSetting("module.autoMace.swapRadius", 6.0F, 3.0F, 20.0F, 0.5F, "module.autoMace.swapRadius.desc", "b");
   private final BooleanSetting holdAura = new BooleanSetting("module.autoMace.holdAura", "module.autoMace.holdAura.desc", true);
   private final BooleanSetting fullCooldown = new BooleanSetting("module.autoMace.fullCooldown", "module.autoMace.fullCooldown.desc", true);
   private final BooleanSetting swapBack = new BooleanSetting("module.autoMace.swapBack", "module.autoMace.swapBack.desc", true);
   private boolean flag;
   private int intValue = -1;
   private float floatValue;
   private Vec3d vec3d;
   private final Map<Integer, Box> items = new HashMap<>();
   private int intValue2 = -1;
   private int intValue3 = -1;
   private AutoMaceModule.Type type = AutoMaceModule.Type.NONE;

   private AutoMaceModule() {
   }

   @EventTarget(0)
   public void method308(Event5 var1) {
      this.method316();
      if (mc.player != null && mc.world != null && !mc.player.isDead()) {
         int var2 = InventoryUtils.method922(var0 -> mc.player.getInventory().getStack(var0).isOf(Items.MACE));
         if (AuraModule.INSTANCE.isEnabled() && var2 != -1 && this.method311() && this.method312()) {
            this.flag = true;
            int var3 = mc.player.getInventory().getSelectedSlot();
            if (var3 != var2 && !mc.player.isUsingItem()) {
               if (this.intValue2 == -1) {
                  this.intValue2 = var3;
               }

               mc.player.getInventory().setSelectedSlot(var2);
               mc.interactionManager.syncSelectedSlot();
            }

            if (this.intValue == 1 && this.fullCooldown.isEnabled() && mc.player.getMainHandStack().isOf(Items.MACE)) {
               mc.player.ticksSinceLastAttack = MathHelper.ceil(mc.player.getAttackCooldownProgressPerTick()) + 1;
            }
         } else {
            if (!this.method314()) {
               this.method315();
            }
         }
      }
   }

   public AutoMaceModule.Type method309(LivingEntity var1) {
      if (this.isEnabled() && this.flag && var1 != null && mc.player != null && mc.player.getMainHandStack().isOf(Items.MACE)) {
         if (this.intValue3 != var1.getId()) {
            this.intValue3 = var1.getId();
            this.type = this.method310(var1);
         }

         return this.type;
      } else {
         return AutoMaceModule.Type.NONE;
      }
   }

   private AutoMaceModule.Type method310(LivingEntity var1) {
      if (this.intValue == 1) {
         return AutoMaceModule.Type.HIT;
      } else if (!this.holdAura.isEnabled()) {
         return AutoMaceModule.Type.NONE;
      } else {
         Box var2 = this.items.get(var1.getId());
         if (var2 == null) {
            var2 = this.method313(var1, this.intValue - 1);
         }

         return AuraModule.INSTANCE.method6332(var2, this.vec3d) ? AutoMaceModule.Type.HOLD : AutoMaceModule.Type.NONE;
      }
   }

   private boolean method311() {
      if (!this.method314()) {
         return false;
      } else {
         float var1 = mc.player.getEyeHeight(mc.player.getPose());
         PlayerSimulation var2 = PlayerSimulation.method6031(0);
         Vec3d var3 = var2.vec3d.add(0.0, var1, 0.0);
         float var4 = var2.floatValue3;
         int var5 = (int)this.predictTicks.getValue3();

         for (int var6 = 1; var6 <= var5; var6++) {
            var2.tick();
            if (var2.flag4) {
               if (!(var4 <= 1.5F) && !(var4 < this.minFall.getValue3())) {
                  this.intValue = var6;
                  this.floatValue = var4;
                  this.vec3d = var3;
                  return true;
               }

               return false;
            }

            if (var2.flag3 || var2.flag7 || var2.method6046() || var2.method6064()) {
               return false;
            }

            var3 = var2.vec3d.add(0.0, var1, 0.0);
            var4 = var2.floatValue3;
         }

         return false;
      }
   }

   private boolean method312() {
      int var1 = this.intValue - 1;
      double var2 = this.swapRadius.getValue3();
      double var4 = var2 + 0.5 * var1;
      double var6 = var4 * var4;
      double var8 = var2 * var2;
      boolean var10 = false;

      for (Entity var12 : mc.world.getEntities()) {
         if (var12 instanceof LivingEntity var13
            && !(var12.getBoundingBox().squaredMagnitude(this.vec3d) > var6)
            && AuraModule.INSTANCE.method6331(var13)
            && !UseItemUtils.method6530(var13)) {
            Box var14 = this.method313(var13, var1);
            this.items.put(var12.getId(), var14);
            if (var14.squaredMagnitude(this.vec3d) <= var8) {
               var10 = true;
            }
         }
      }

      return var10;
   }

   private Box method313(LivingEntity var1, int var2) {
      return var1 instanceof PlayerEntity var3 ? PlayerSimulation.method6032(var3, var2).box : var1.getBoundingBox();
   }

   private boolean method314() {
      return !mc.player.isOnGround()
         && !mc.player.isGliding()
         && !mc.player.isTouchingWater()
         && !mc.player.isClimbing()
         && !mc.player.hasVehicle()
         && !mc.player.getAbilities().flying;
   }

   private void method315() {
      if (this.intValue2 != -1) {
         if (this.swapBack.isEnabled() && mc.player != null && mc.player.getInventory().getSelectedSlot() != this.intValue2) {
            mc.player.getInventory().setSelectedSlot(this.intValue2);
         }

         this.intValue2 = -1;
      }
   }

   private void method316() {
      this.flag = false;
      this.intValue = -1;
      this.floatValue = 0.0F;
      this.vec3d = null;
      this.items.clear();
      this.intValue3 = -1;
      this.type = AutoMaceModule.Type.NONE;
   }

   @Override
   public void onDisable() {
      this.method315();
      this.method316();
      super.onDisable();
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   public static enum Type {
      NONE,
      HOLD,
      HIT;
   }
}

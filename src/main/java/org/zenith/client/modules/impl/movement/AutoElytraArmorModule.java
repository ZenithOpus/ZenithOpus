package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.misc.ElytraHelperModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.Util10;

@ModuleInfo(
   name = "AutoElytraArmor",
   description = "module.autoelytraarmor.description",
   category = ModuleCategory.MOVEMENT
)
public final class AutoElytraArmorModule extends Module {
   public static final AutoElytraArmorModule INSTANCE = new AutoElytraArmorModule();
   private final ModeSetting mode = new ModeSetting(
      "module.autoElytraArmor.mode", "module.autoElytraArmor.mode.desc", "module.autoElytraArmor.mode.onSwap", "module.autoElytraArmor.mode.always"
   );
   private final SliderSetting range = new SliderSetting("module.autoElytraArmor.range", 5.0F, 1.0F, 10.0F, 1.0F, "module.autoElytraArmor.range.desc", "b");
   private final BooleanSetting autoAirStuck = new BooleanSetting("module.autoElytraArmor.autoAirStuck", "module.autoElytraArmor.autoAirStuck.desc", true);
   private final BooleanSetting reverseSwap = new BooleanSetting("module.autoElytraArmor.reverseSwap", "module.autoElytraArmor.reverseSwap.desc", false);
   private PlayerEntity playerEntity;
   private boolean flag;
   private boolean flag2;
   private AutoElytraArmorModule.Type type = AutoElytraArmorModule.Type.NONE;

   private AutoElytraArmorModule() {
   }

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      ClientPlayerEntity var2 = mc.player;
      if (var2 == null || mc.world == null) {
         this.resetState();
      } else if (this.mode.isSelected(1)) {
         this.method412(var2);
      } else {
         this.type = AutoElytraArmorModule.Type.NONE;
         this.method417(var2);
         this.method416(this.method411(var2));
      }
   }

   private PlayerEntity method411(PlayerEntity var1) {
      if (this.method414(var1, this.playerEntity)) {
         return this.playerEntity;
      } else if (AuraModule.INSTANCE.method6358() instanceof PlayerEntity var3 && this.method415(var1, var3)) {
         return var3;
      } else {
         ClientWorld var4 = mc.world;
         return var4 == null
            ? null
            : var4.getPlayers()
               .stream()
               .filter(var1x -> var1x != var1)
               .filter(var2 -> this.method415(var1, var2))
               .filter(var0 -> !Zenith.getInstance().getFriends().method7626(var0))
               .min(Comparator.comparingDouble(var2 -> this.method420(var1, var2)))
               .orElse(null);
      }
   }

   private void method412(PlayerEntity var1) {
      PlayerEntity var2 = this.method413(var1);
      if (var2 == null) {
         this.type = AutoElytraArmorModule.Type.NONE;
         this.method419(false);
      } else {
         ItemStack var3 = var1.getEquippedStack(EquipmentSlot.CHEST);
         boolean var4 = var3.isOf(Items.ELYTRA);
         boolean var5 = Util10.method2182(var3);
         if (this.type == AutoElytraArmorModule.Type.ELYTRA && var4 || this.type == AutoElytraArmorModule.Type.CHESTPLATE && var5) {
            this.type = AutoElytraArmorModule.Type.NONE;
         }

         boolean var6 = var2.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
         this.method419(!var6);
         if (this.type == AutoElytraArmorModule.Type.NONE) {
            if (var6) {
               if (var5) {
                  this.type = AutoElytraArmorModule.Type.ELYTRA;
                  ElytraHelperModule.INSTANCE.method7454();
               }
            } else if (var4) {
               this.type = AutoElytraArmorModule.Type.CHESTPLATE;
               this.method418();
            }
         }
      }
   }

   private PlayerEntity method413(PlayerEntity var1) {
      if (AuraModule.INSTANCE.method6358() instanceof PlayerEntity var3 && this.method414(var1, var3)) {
         return var3;
      } else {
         ClientWorld var4 = mc.world;
         return var4 == null
            ? null
            : var4.getPlayers()
               .stream()
               .filter(var1x -> var1x != var1)
               .filter(var2 -> this.method414(var1, var2))
               .filter(var0 -> !Zenith.getInstance().getFriends().method7626(var0))
               .min(Comparator.comparingDouble(var2 -> this.method420(var1, var2)))
               .orElse(null);
      }
   }

   private boolean method414(PlayerEntity var1, PlayerEntity var2) {
      if (var2 != null && var2.isAlive() && !var2.isRemoved() && !(this.method420(var1, var2) > this.method421())) {
         ItemStack var3 = var2.getEquippedStack(EquipmentSlot.CHEST);
         return var3.isOf(Items.ELYTRA) || Util10.method2182(var3);
      } else {
         return false;
      }
   }

   private boolean method415(PlayerEntity var1, PlayerEntity var2) {
      return var2 != null
         && var2.isAlive()
         && !var2.isRemoved()
         && var2.isGliding()
         && var2.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)
         && this.method420(var1, var2) <= this.method421();
   }

   private void method416(PlayerEntity var1) {
      if (var1 == null) {
         this.method422();
      } else {
         if (this.playerEntity != var1) {
            this.playerEntity = var1;
            this.flag = var1.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
            this.flag2 = Util10.method2182(var1.getEquippedStack(EquipmentSlot.CHEST));
         } else if (var1.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            this.flag = true;
            this.flag2 = false;
         } else if (Util10.method2182(var1.getEquippedStack(EquipmentSlot.CHEST))) {
            this.flag = false;
            this.flag2 = true;
         }
      }
   }

   private void method417(PlayerEntity var1) {
      PlayerEntity var2 = this.playerEntity;
      if (var2 != null) {
         if (!this.method414(var1, var2)) {
            this.method422();
         } else {
            ItemStack var3 = var2.getEquippedStack(EquipmentSlot.CHEST);
            if (var3.isOf(Items.ELYTRA)) {
               boolean var5 = this.flag2;
               this.flag = true;
               this.flag2 = false;
               if (this.reverseSwap.isEnabled() && var5 && Util10.method2182(var1.getEquippedStack(EquipmentSlot.CHEST))) {
                  ElytraHelperModule.INSTANCE.method7454();
                  this.method419(false);
               }
            } else if (!Util10.method2182(var3)) {
               this.flag = false;
               this.flag2 = false;
            } else {
               boolean var4 = this.flag;
               this.flag = false;
               this.flag2 = true;
               if (var4 && var1.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
                  this.method418();
               }
            }
         }
      }
   }

   private void method418() {
      ElytraHelperModule.INSTANCE.method7454();
      this.method419(true);
   }

   private void method419(boolean var1) {
      if (this.autoAirStuck.isEnabled() && AirStuckModule.INSTANCE.isEnabled() != var1) {
         AirStuckModule.INSTANCE.setToggled(var1);
      }
   }

   private double method420(PlayerEntity var1, PlayerEntity var2) {
      Vec3d var3 = var2.getTrackedPosition().getPos();
      double var4 = var1.getX() - var3.x;
      double var6 = var1.getZ() - var3.z;
      return var4 * var4 + var6 * var6;
   }

   private double method421() {
      double var1;
      return (var1 = this.range.getValue3()) * var1;
   }

   private void resetState() {
      this.method422();
      this.type = AutoElytraArmorModule.Type.NONE;
   }

   private void method422() {
      this.playerEntity = null;
      this.flag = false;
      this.flag2 = false;
   }

   private static enum Type {
      NONE,
      CHESTPLATE,
      ELYTRA;
   }
}

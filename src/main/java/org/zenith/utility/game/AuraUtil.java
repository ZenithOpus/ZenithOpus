package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.modules.impl.combat.AntiBotModule;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.network.UseItemUtils;

public final class AuraUtil implements ZenithAccess {
   private static final AuraRandomizer auraRandomizer = new AuraRandomizer();
   private static LivingEntity livingEntity;

   public static LivingEntity method7442(
      Iterable<Entity> var0, float var1, boolean var2, List<String> var3, List<String> var4, Predicate<LivingEntity> var5, boolean var6
   ) {
      if (var6 && livingEntity != null && method7445(var3, livingEntity, var1, var2, var5)) {
         return livingEntity;
      } else {
         ArrayList var7 = new ArrayList();

         for (Entity var9 : var0) {
            if (var9 instanceof LivingEntity var10 && method7445(var3, var9, var1, var2, var5)) {
               var7.add(var10);
            }
         }

         var7.sort(method7443(Comparator.comparing(var0x -> true), var4));
         livingEntity = var7.isEmpty() ? null : (LivingEntity)var7.getFirst();
         return livingEntity;
      }
   }

   public static Comparator<LivingEntity> method7443(Comparator<LivingEntity> var0, List<String> var1) {
      var0 = var0.thenComparing((var0x, var1x) -> Boolean.compare(!(var0x instanceof PlayerEntity), !(var1x instanceof PlayerEntity)));
      if (var1.contains("module.aura.targetFov")) {
         var0 = var0.thenComparingDouble(AuraUtil::method7444);
      }

      if (var1.contains("module.aura.targetArmor")) {
         var0 = var0.thenComparing(Comparator.comparingDouble(UseItemUtils::method6529).reversed());
      }

      if (var1.contains("module.aura.targetHp")) {
         var0 = var0.thenComparingDouble(UseItemUtils::method6527);
      }

      if (var1.contains("module.aura.targetDistance")) {
         var0 = var0.thenComparingDouble(mc.player::squaredDistanceTo);
      }

      return var0;
   }

   private static double method7444(LivingEntity var0) {
      if (mc.player == null) {
         return Double.MAX_VALUE;
      } else {
         Rotation var1 = new Rotation(mc.player.getYaw(), mc.player.getPitch());
         Vec3d var2 = mc.player.getEyePos();
         Rotation var3 = Rotation.method3128(var0.getBoundingBox().getCenter(), var2);
         return var1.method3130(var3);
      }
   }

   public static boolean method7445(List<String> var0, Entity var1, float var2, boolean var3, Predicate<LivingEntity> var4) {
      return var1 instanceof LivingEntity var5 && method7447(var0, var5) && var4.test((LivingEntity)var1) ? auraRandomizer.method4676(var5, var2, var3) : false;
   }

   public static LivingEntity method7446(Iterable<Entity> var0, float var1, boolean var2, List<String> var3, List<String> var4, boolean var5) {
      return method7442(var0, var1, var2, var3, var4, var0x -> true, var5);
   }

   public static boolean method7447(List<String> var0, LivingEntity var1) {
      if (method7448(var1)) {
         return false;
      } else if (method7449(var1)) {
         return false;
      } else {
         return method7450(var1) ? false : method7451(var0, var1);
      }
   }

   private static boolean method7448(LivingEntity var0) {
      return var0 == mc.player;
   }

   private static boolean method7449(LivingEntity var0) {
      return !var0.isAlive() || var0.getHealth() <= 0.0F;
   }

   private static boolean method7450(LivingEntity var0) {
      return var0 instanceof PlayerEntity var1 && AntiBotModule.INSTANCE.method2785(var1);
   }

   private static boolean method7451(List<String> var0, LivingEntity var1) {
      if (var1 instanceof PlayerEntity var2) {
         if (PetManager.method4557(var2.getId())) {
            return false;
         } else if (Zenith.getInstance().getFriends().method7627(var2.getGameProfile().name())) {
            return false;
         } else {
            return UseItemUtils.method6529(var2) == 0.0F ? var0.contains("module.aura.noarmor") : var0.contains("module.aura.targetPlayers");
         }
      } else if (var1 instanceof MobEntity) {
         return var0.contains("module.aura.targetHostile");
      } else {
         return var1 instanceof AnimalEntity ? var0.contains("module.aura.targetPeaceful") : !(var1 instanceof ArmorStandEntity);
      }
   }

   @Generated
   private AuraUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

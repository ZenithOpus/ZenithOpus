package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "TridentAimbot",
   description = "module.tridentaimbot.description",
   category = ModuleCategory.COMBAT,
   method896 = true
)
public final class TridentAimbotModule extends Module {
   public static final TridentAimbotModule INSTANCE = new TridentAimbotModule();
   private static final int CONST_intValue = 1;
   private static final double CONST_doubleValue = 2.5;
   private static final int CONST_intValue2 = 90;
   private static final int CONST_intValue3 = 50;
   private final SliderSetting predictSetting = new SliderSetting(
      "module.elytraTarget.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.itemUseController.predictSetting.desc", "t"
   );

   @EventTarget
   public void method2788(Event5 var1) {
      if (mc.player.isUsingItem() && mc.player.getActiveItem().getItem() == Items.TRIDENT) {
         Rotation var2 = this.method2789();
         if (var2 == null) {
            return;
         }

         Rotation var3 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var2);
         Zenith.getInstance().getRotationManager().method7849(new RotationRequest(var3, () -> var3, bcCameraTestnerv.getRotationConfig()), 20, this);
      }
   }

   private Rotation method2789() {
      Rotation var1 = new Rotation(mc.player.getYaw(), mc.player.getPitch());

      try {
         ItemStack var2 = mc.player.getActiveItem();
         PlayerSimulation var3 = PlayerSimulation.method6031(1);
         Vec3d var4 = this.method2795(var3);
         LivingEntity var5 = this.method2792(var3, var2);
         if (var5 == null) {
            return null;
         } else {
            Box var6 = this.method2797(var5);
            Rotation var7 = Rotation.method3128(this.method2793(var6), var4);
            return this.method2798(var2, var3, var7, var6);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
         return var1;
      }
   }

   public Vec3d method2790(LivingEntity var1) {
      return this.method2793(this.method2797(var1));
   }

   public LivingEntity method2791() {
      return mc.player.isUsingItem() && mc.player.getActiveItem().getItem() == Items.TRIDENT
         ? this.method2792(PlayerSimulation.method6031(1), mc.player.getActiveItem())
         : null;
   }

   private LivingEntity method2792(PlayerSimulation var1, ItemStack var2) {
      var var3 = this.method2794(var2, RotationUtils.method6911(), var1);
      if (var3 != null && !var3.isEmpty()) {
         HitResult var4 = (HitResult)var3.getFirst();
         if (var4 == null) {
            return null;
         } else if (var4 instanceof EntityHitResult var5
            && var5.getEntity() instanceof LivingEntity var6
            && !Zenith.getInstance().getFriends().method7626(var6)) {
            return var6;
         } else {
            PlayerEntity var12 = null;
            double var13 = Double.MAX_VALUE;

            for (PlayerEntity var9 : mc.world.getPlayers()) {
               if (mc.player != var9 && !Zenith.getInstance().getFriends().method7626(var9)) {
                  double var10 = this.method2790(var9).squaredDistanceTo(var4.getPos());
                  if (var10 < var13) {
                     var13 = var10;
                     var12 = var9;
                  }
               }
            }

            return var12;
         }
      } else {
         return null;
      }
   }

   public Vec3d method2793(Box var1) {
      return new Vec3d(MathHelper.lerp(0.5, var1.minX, var1.maxX), MathHelper.lerp(0.8, var1.minY, var1.maxY), MathHelper.lerp(0.5, var1.minZ, var1.maxZ));
   }

   private List<HitResult> method2794(ItemStack var1, Rotation var2, PlayerSimulation var3) {
      HitResult var4 = PredictionsModule.INSTANCE
         .method2918(this.method2795(var3), var3.vec3d2, var2.method3135(), new TridentEntity(mc.world, mc.player, var1), this.method2804());
      return var4 == null ? List.of() : List.of(var4);
   }

   private Vec3d method2795(PlayerSimulation var1) {
      return var1.vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);
   }

   private int method2796() {
      return 1 + (int)this.predictSetting.getValue3();
   }

   private Box method2797(LivingEntity var1) {
      if (var1 instanceof PlayerEntity var3) {
         return PlayerSimulation.method6032(var3, this.method2796()).box;
      } else {
         Vec3d var2 = var1.getEntityPos().subtract(var1.lastX, var1.lastY, var1.lastZ);
         return var1.getBoundingBox().offset(var2.multiply(1.0));
      }
   }

   private Rotation method2798(ItemStack var1, PlayerSimulation var2, Rotation var3, Box var4) {
      TridentEntity var5 = new TridentEntity(mc.world, mc.player, var1);
      Vec3d var6 = this.method2795(var2);
      Vec3d var7 = var2.vec3d2;
      double var8 = this.method2804();
      if (this.method2802(var3, var6, var7, var4, var5, var8)) {
         return var3;
      } else {
         int var10 = Math.round(var3.getFloatValue());
         int var11 = Math.round(var3.getFloatValue2());
         Integer var12 = this.method2799(var10, var11, var6, var7, var4, var5, var8);
         if (var12 == null) {
            return null;
         } else {
            Rotation var13 = new Rotation(var10, var12.intValue());
            return this.method2802(var13, var6, var7, var4, var5, var8) ? var13 : this.method2800(var10, var12, var6, var7, var4, var5, var8);
         }
      }
   }

   private Integer method2799(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, TridentEntity var6, double var7) {
      for (int var9 = 0; var9 <= 90; var9++) {
         int var10 = var2 + var9;
         if (this.method2801(var1, var10, var3, var4, var5, var6, var7)) {
            return var10;
         }

         if (var9 != 0) {
            int var11 = var2 - var9;
            if (this.method2801(var1, var11, var3, var4, var5, var6, var7)) {
               return var11;
            }
         }
      }

      return null;
   }

   private Rotation method2800(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, TridentEntity var6, double var7) {
      for (int var9 = 1; var9 <= 50; var9++) {
         Rotation var10 = new Rotation(var1 + var9, var2);
         if (this.method2802(var10, var3, var4, var5, var6, var7)) {
            return var10.method3140(3.0F, 0.0F);
         }

         Rotation var11 = new Rotation(var1 - var9, var2);
         if (this.method2802(var11, var3, var4, var5, var6, var7)) {
            return var11.method3140(-3.0F, 0.0F);
         }
      }

      return null;
   }

   private boolean method2801(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, TridentEntity var6, double var7) {
      Rotation var9 = new Rotation(var1, var2);
      Vec3d var10 = var9.method3135();
      double var11 = var10.length();
      if (var11 <= 1.0E-6) {
         return false;
      } else {
         Vec3d var13 = this.method2793(var5);
         Vec3d var14 = new Vec3d(var13.x - var3.x, 0.0, var13.z - var3.z);
         double var15 = var14.horizontalLength();
         if (var15 <= 1.0E-6) {
            return true;
         } else {
            var14 = var14.normalize();
            Vec3d var17 = var3;
            Vec3d var18 = var10.multiply(var7 / var11).add(var4);

            for (int var19 = 0; var19 < 300; var19++) {
               Vec3d var20 = var17;
               var17 = var17.add(var18);
               Vec3d var21 = PredictionsModule.INSTANCE.method2921(var6, var20, var18);
               BlockHitResult var22 = TargetUtils.method3208(var20, var17, ShapeType.COLLIDER, var6);
               double var23 = this.method2806(var20, var17, var22);
               double var25 = this.method2805(var20, var3, var14);
               double var27 = this.method2805(var17, var3, var14);
               if (var27 >= var15) {
                  double var29 = var27 - var25;
                  double var31 = var29 <= 1.0E-6 ? 0.0 : MathHelper.clamp((var15 - var25) / var29, 0.0, 1.0);
                  if (!(var31 <= var23)) {
                     return false;
                  }

                  double var33 = MathHelper.lerp(var31, var20.y, var17.y);
                  return var33 >= var5.minY && var33 <= var5.maxY;
               }

               if (var23 < 1.0 || var17.y < -128.0) {
                  return false;
               }

               if (this.method2803(var17, var21, var5)) {
                  return false;
               }

               var18 = var21;
            }

            return false;
         }
      }
   }

   private boolean method2802(Rotation var1, Vec3d var2, Vec3d var3, Box var4, TridentEntity var5, double var6) {
      Vec3d var8 = var1.method3135();
      double var9 = var8.length();
      if (var9 <= 1.0E-6) {
         return false;
      } else {
         Vec3d var11 = var2;
         Vec3d var12 = var8.multiply(var6 / var9).add(var3);

         for (int var13 = 0; var13 < 300; var13++) {
            Vec3d var14 = var11;
            var11 = var11.add(var12);
            Vec3d var15 = PredictionsModule.INSTANCE.method2921(var5, var14, var12);
            BlockHitResult var16 = TargetUtils.method3208(var14, var11, ShapeType.COLLIDER, var5);
            double var17 = var16.getType() == Type.MISS ? Double.POSITIVE_INFINITY : var14.squaredDistanceTo(var16.getPos());
            var var19 = var4.raycast(var14, var11);
            if (var19.isPresent() && var14.squaredDistanceTo((Vec3d)var19.get()) <= var17) {
               return true;
            }

            if (var16.getType() != Type.MISS || var11.y < -128.0) {
               return false;
            }

            if (this.method2803(var11, var15, var4)) {
               return false;
            }

            var12 = var15;
         }

         return false;
      }
   }

   private boolean method2803(Vec3d var1, Vec3d var2, Box var3) {
      return var2.y <= 0.0 && var1.y < var3.minY - 1.0E-6;
   }

   private double method2804() {
      return 2.5;
   }

   private double method2805(Vec3d var1, Vec3d var2, Vec3d var3) {
      Vec3d var4 = var1.subtract(var2);
      return var4.x * var3.x + var4.z * var3.z;
   }

   private double method2806(Vec3d var1, Vec3d var2, BlockHitResult var3) {
      if (var3.getType() == Type.MISS) {
         return Double.POSITIVE_INFINITY;
      } else {
         double var4 = var1.distanceTo(var2);
         return var4 <= 1.0E-6 ? 0.0 : var1.distanceTo(var3.getPos()) / var4;
      }
   }
}

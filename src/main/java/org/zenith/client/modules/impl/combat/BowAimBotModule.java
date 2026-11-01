package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Optional;
import kotlin.collections.EmptyList;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
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
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "BowAimBot",
   description = "module.bowaimbot.description",
   category = ModuleCategory.COMBAT,
   method896 = true
)
public final class BowAimBotModule extends Module {
   public static final BowAimBotModule INSTANCE = new BowAimBotModule();
   private static final int CONST_intValue = 1;
   private static final int CONST_intValue2 = 90;
   private static final int CONST_intValue3 = 50;
   private int intValue;
   private boolean flag;
   private final SliderSetting predictSetting = new SliderSetting(
      "module.bowAimBot.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.bowAimBot.predictSetting.desc", "t"
   );
   private final BooleanSetting autoShoot = new BooleanSetting("module.bowAimBot.autoShoot", true);
   private final SliderSetting autoShootDelay = new SliderSetting(
      "module.bowAimBot.autoShootDelay", 0.0F, 0.0F, 20.0F, 1.0F, "module.bowAimBot.autoShootDelay.desc", "t", this.autoShoot::isEnabled
   );

   @EventTarget
   public void method5721(Event5 var1) {
      if (this.flag) {
         this.flag = false;
         this.method5722();
      }

      if (mc.player.isUsingItem() && mc.player.getActiveItem().getItem() == Items.BOW) {
         this.method5722();
         Rotation var2 = this.method5723();
         if (var2 == null) {
            this.intValue = 0;
            return;
         }

         Rotation var3 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var2);
         Zenith.getInstance().getRotationManager().method7849(new RotationRequest(var3, () -> var3, bcCameraTestnerv.getRotationConfig3()), 20, this);
      } else {
         this.intValue = 0;
      }
   }

   private void method5722() {
      mc.options.useKey.setPressed(UseItemUtils.method6520(KeyBindingHelper.getBoundKeyOf(mc.options.useKey)));
   }

   private Rotation method5723() {
      Rotation var1 = new Rotation(mc.player.getYaw(), mc.player.getPitch());

      try {
         ItemStack var2 = mc.player.getActiveItem();
         PlayerSimulation var3 = PlayerSimulation.method6031(1);
         Vec3d var4 = this.method5729(var3);
         LivingEntity var5 = this.method5726(var3);
         if (var5 == null) {
            return null;
         } else {
            Box var6 = this.method5731(var5);
            Rotation var7 = Rotation.method3128(this.method5727(var6), var4);
            return this.autoShoot.isEnabled() && this.method5733(var2, var3, var6) ? null : this.method5732(var2, var3, var7, var6);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
         return var1;
      }
   }

   public Vec3d method5724(LivingEntity var1) {
      return this.method5727(this.method5731(var1));
   }

   public LivingEntity method5725() {
      return this.method5726(PlayerSimulation.method6031(1));
   }

   private LivingEntity method5726(PlayerSimulation var1) {
      var var2 = this.method5728(mc.player.getActiveItem(), RotationUtils.method6911(), var1);
      if (var2 != null && (!var2.isEmpty())) {
         HitResult var3 = (HitResult)var2.getFirst();
         if (var3 == null) {
            return null;
         } else if (var3 instanceof EntityHitResult var4
            && var4.getEntity() instanceof LivingEntity var5
            && !Zenith.getInstance().getFriends().method7626(var5)) {
            return var5;
         } else {
            PlayerEntity var11 = null;
            double var12 = Double.MAX_VALUE;

            for (PlayerEntity var8 : mc.world.getPlayers()) {
               double var9;
               if (mc.player != var8
                  && !Zenith.getInstance().getFriends().method7626(var8)
                  && (var9 = this.method5724(var8).squaredDistanceTo(var3.getPos())) < var12) {
                  var12 = var9;
                  var11 = var8;
               }
            }

            return var11;
         }
      } else {
         return null;
      }
   }

   public Vec3d method5727(Box var1) {
      return new Vec3d(MathHelper.lerp(0.5, var1.minX, var1.maxX), MathHelper.lerp(0.8, var1.minY, var1.maxY), MathHelper.lerp(0.5, var1.minZ, var1.maxZ));
   }

   private List<HitResult> method5728(ItemStack var1, Rotation var2, PlayerSimulation var3) {
      return PredictionsModule.INSTANCE.method2916(var1, var2, this.method5729(var3), var3.vec3d2, 1);
   }

   private Vec3d method5729(PlayerSimulation var1) {
      return var1.vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);
   }

   private int method5730() {
      return 1 + (int)this.predictSetting.getValue3();
   }

   private Box method5731(LivingEntity var1) {
      if (var1 instanceof PlayerEntity var3) {
         return PlayerSimulation.method6032(var3, this.method5730()).box;
      } else {
         Vec3d var2 = var1.getEntityPos().subtract(var1.lastX, var1.lastY, var1.lastZ);
         return var1.getBoundingBox().offset(var2.multiply(1.0));
      }
   }

   private Rotation method5732(ItemStack var1, PlayerSimulation var2, Rotation var3, Box var4) {
      ArrowEntity var5 = new ArrowEntity(mc.world, mc.player, var1, var1);
      Vec3d var6 = this.method5729(var2);
      Vec3d var7 = var2.vec3d2;
      double var8 = this.method5739();
      if (this.method5738(var3, var6, var7, var4, var5, var8)) {
         return var3;
      } else {
         int var10 = Math.round(var3.getFloatValue());
         int var11 = Math.round(var3.getFloatValue2());
         Integer var12 = this.method5735(var10, var11, var6, var7, var4, var5, var8);
         if (var12 == null) {
            return null;
         } else {
            Rotation var13 = new Rotation(var10, var12.intValue());
            return this.method5738(var13, var6, var7, var4, var5, var8) ? var13 : this.method5736(var10, var12, var6, var7, var4, var5, var8);
         }
      }
   }

   private boolean method5733(ItemStack var1, PlayerSimulation var2, Box var3) {
      if (mc.interactionManager == null) {
         return false;
      } else if (!this.method5734(var3)) {
         return false;
      } else {
         mc.options.useKey.setPressed(false);
         this.flag = true;
         return true;
      }
   }

   private boolean method5734(Box var1) {
      double var2;
      int var5 = ((var2 = mc.player.squaredDistanceTo(var1.getCenter())) < 100.0 ? (var2 < 16.0 ? 2 : 5) : 7) + (int)this.autoShootDelay.getValue3();
      if (this.intValue++ <= var5) {
         return false;
      } else {
         this.intValue = 0;
         return true;
      }
   }

   private Integer method5735(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, ArrowEntity var6, double var7) {
      for (int var9 = 0; var9 <= 90; var9++) {
         int var10 = var2 + var9;
         if (this.method5737(var1, var10, var3, var4, var5, var6, var7)) {
            return var10;
         }

         if (var9 != 0) {
            int var11 = var2 - var9;
            if (this.method5737(var1, var11, var3, var4, var5, var6, var7)) {
               return var11;
            }
         }
      }

      return null;
   }

   private Rotation method5736(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, ArrowEntity var6, double var7) {
      for (int var9 = 1; var9 <= 50; var9++) {
         Rotation var10 = new Rotation(var1 + var9, var2);
         if (this.method5738(var10, var3, var4, var5, var6, var7)) {
            return var10.method3140(3.0F, 0.0F);
         }

         Rotation var11 = new Rotation(var1 - var9, var2);
         if (this.method5738(var11, var3, var4, var5, var6, var7)) {
            return var11.method3140(-3.0F, 0.0F);
         }
      }

      return null;
   }

   private boolean method5737(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, ArrowEntity var6, double var7) {
      Rotation var9 = new Rotation(var1, var2);
      Vec3d var10 = var9.method3135();
      double var11;
      if ((var11 = var10.length()) <= 1.0E-6) {
         return false;
      } else {
         Vec3d var13 = this.method5727(var5);
         Vec3d var14 = new Vec3d(var13.x - var3.x, 0.0, var13.z - var3.z);
         double var15;
         if ((var15 = var14.horizontalLength()) <= 1.0E-6) {
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
               double var23 = this.method5741(var20, var17, var22);
               double var25 = this.method5740(var20, var3, var14);
               double var27;
               if ((var27 = this.method5740(var17, var3, var14)) >= var15) {
                  double var29;
                  double var31;
                  if (!((var31 = (var29 = var27 - var25) <= 1.0E-6 ? 0.0 : MathHelper.clamp((var15 - var25) / var29, 0.0, 1.0)) <= var23)) {
                     return false;
                  }

                  double var33;
                  return (var33 = MathHelper.lerp(var31, var20.y, var17.y)) >= var5.minY && var33 <= var5.maxY;
               }

               if (var23 < 1.0) {
                  return false;
               }

               if (var17.y < -128.0) {
                  return false;
               }

               if (var17.y < var5.minY && var21.y <= 0.0) {
                  return false;
               }

               var18 = var21;
            }

            return false;
         }
      }
   }

   private boolean method5738(Rotation var1, Vec3d var2, Vec3d var3, Box var4, ArrowEntity var5, double var6) {
      Vec3d var8 = var1.method3135();
      double var9;
      if ((var9 = var8.length()) <= 1.0E-6) {
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

            if (var11.y < var4.minY - 1.0E-6 && var15.y <= 0.0) {
               return false;
            }

            var12 = var15;
         }

         return false;
      }
   }

   private double method5739() {
      float var1 = mc.player.getItemUseTime() + mc.getRenderTickCounter().getTickProgress(false) + 1.0F;
      return 3.0F * MathHelper.clamp(var1 / 20.0F, 0.0F, 1.0F);
   }

   private double method5740(Vec3d var1, Vec3d var2, Vec3d var3) {
      Vec3d var4 = var1.subtract(var2);
      return var4.x * var3.x + var4.z * var3.z;
   }

   private double method5741(Vec3d var1, Vec3d var2, BlockHitResult var3) {
      if (var3.getType() == Type.MISS) {
         return Double.POSITIVE_INFINITY;
      } else {
         double var4;
         return (var4 = var1.distanceTo(var2)) <= 1.0E-6 ? 0.0 : var1.distanceTo(var3.getPos()) / var4;
      }
   }
}

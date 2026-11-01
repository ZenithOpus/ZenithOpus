package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.StopUsingItemEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "ItemUseController",
   description = "module.itemusecontroller.description",
   category = ModuleCategory.MISC
)
public final class ItemUseControllerModule extends Module {
   public static final ItemUseControllerModule INSTANCE = new ItemUseControllerModule();
   private final BooleanSetting newVersion = new BooleanSetting("module.itemUseController.newVersion", "module.itemUseController.newVersion.desc", false);
   private final BooleanSetting aim = new BooleanSetting("module.itemUseController.aim", "module.itemUseController.aim.desc", false);
   private final MultiSelectSetting items = MultiSelectSetting.method5224(
      "module.itemUseController.items",
      "module.itemUseController.items.desc",
      List.of("module.itemUseController.bow", "module.itemUseController.crossbow", "module.itemUseController.snowball", "module.itemUseController.trident")
   );
   private final SliderSetting predictSetting = new SliderSetting(
      "module.itemUseController.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.itemUseController.predictSetting.desc", "t"
   );
   public boolean flag = false;
   public boolean flag2 = false;

   public boolean method4810() {
      ItemStack var1 = mc.player.getMainHandStack();
      Item var2 = var1.getItem();
      if (this.items.method5226("module.itemUseController.bow") && var2 == Items.BOW) {
         return true;
      } else if (this.items.method5226("module.itemUseController.crossbow") && var2 == Items.CROSSBOW) {
         return true;
      } else {
         return this.items.method5226("module.itemUseController.snowball") && var2 == Items.SNOWBALL
            ? true
            : this.items.method5226("module.itemUseController.trident") && var2 == Items.TRIDENT;
      }
   }

   private ItemUseControllerModule() {
   }

   @EventTarget
   public void method4811(Event5 var1) {
      if (!this.newVersion.isEnabled()) {
         this.flag = false;
         if (mc.getOverlay() == null
            && mc.currentScreen == null
            && (Zenith.getInstance().getRotationManager().getCurrentRotation() != null || this.aim.isEnabled() && this.method4810())
            && !Zenith.getInstance().getRotationManager().method7844().method3131(new Rotation(mc.player.getYaw(), mc.player.getPitch())).method5329(5.0F)
            && (
               mc.options.useKey.isPressed()
                     && mc.itemUseCooldown == 0
                     && !mc.player.isUsingItem()
                     && mc.player.getMainHandStack().getMaxUseTime(mc.player) == 0
                  || Zenith.getInstance().getTaskScheduler().method5118(HandleInputEvent.class)
            )) {
            Rotation var2 = this.method4813();
            Rotation var3 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var2);
            Zenith.getInstance().getRotationManager().method7849(new RotationRequest(var3, () -> var3, bcCameraTestnerv.getRotationConfig()), 20, this);
            this.flag = true;
         }
      } else if (mc.getOverlay() == null
         && mc.currentScreen == null
         && (Zenith.getInstance().getRotationManager().getCurrentRotation() != null || this.aim.isEnabled() && this.method4810())
         && (
            mc.options.useKey.isPressed() && mc.itemUseCooldown == 0 && !mc.player.isUsingItem() && mc.player.getMainHandStack().getMaxUseTime(mc.player) == 0
               || Zenith.getInstance().getTaskScheduler().method5118(HandleInputEvent.class)
         )) {
         Rotation var4 = this.method4813();
         Rotation var6 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var4);
         Zenith.getInstance().getRotationManager().method7849(new RotationRequest(var6, () -> var6, bcCameraTestnerv.getRotationConfig()), 20, this);
      }

      this.flag2 = false;
      if (!mc.options.useKey.isPressed()
         && mc.player.isUsingItem()
         && (Zenith.getInstance().getRotationManager().getCurrentRotation() != null || this.aim.isEnabled() && this.method4810())) {
         Rotation var5 = this.method4813();
         if (!Zenith.getInstance().getRotationManager().method7844().method3131(var5).method5329(5.0F)) {
            Rotation var7 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var5);
            Zenith.getInstance().getRotationManager().method7849(new RotationRequest(var7, () -> var7, bcCameraTestnerv.getRotationConfig()), 20, this);
            this.flag2 = true;
         }
      }
   }

   @EventTarget
   public void method4812(Event9 var1) {
      if (!this.newVersion.isEnabled() && this.flag) {
         var1.cancel();
      }
   }

   private Rotation method4813() {
      Rotation var1 = new Rotation(mc.player.getYaw(), mc.player.getPitch());

      try {
         if (this.aim.isEnabled() && this.method4810()) {
            ItemStack var2 = mc.player.getMainHandStack();
            LivingEntity var3 = AuraModule.INSTANCE.getLivingEntity22();
            if (!(var3 instanceof PlayerEntity var4)) {
               return var1;
            } else {
               Rotation var5 = RotationUtils.method6912(
                  this.method4814(
                        this.predictSetting.getValue3() == 0.0F
                           ? var4.getBoundingBox()
                           : PlayerSimulation.method6032(var4, (int)this.predictSetting.getValue3()).box
                     )
                     .subtract(PlayerSimulation.method6031(1).vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0))
               );
               float var6 = var5.getFloatValue();
               int var7 = Math.round(var5.getFloatValue2());
               float var8 = Float.MAX_VALUE;
               Rotation var9 = var1;

               for (int var10 = 0; var10 < 90; var10++) {
                  int[] var11 = new int[]{var7 + var10, var7 - var10};

                  for (int var15 : var11) {
                     Rotation var16 = new Rotation(var6, var15);
                     var var17 = PredictionsModule.INSTANCE.method2903(var2, var2.getItem(), var16);
                     if (var17 != null && !var17.isEmpty() && var17.getFirst() instanceof EntityHitResult var18 && var18.getEntity().equals(var3)) {
                        float var21 = Math.abs(var5.getFloatValue2() - var15);
                        if (var21 <= 5.0F) {
                           return var16;
                        }

                        if (var21 < var8) {
                           var8 = var21;
                           var9 = var16;
                        }
                     }
                  }
               }

               return var9;
            }
         } else {
            return var1;
         }
      } catch (Exception var20) {
         var20.printStackTrace();
         return var1;
      }
   }

   public Vec3d method4814(Box var1) {
      return new Vec3d(MathHelper.lerp(0.5, var1.minX, var1.maxX), MathHelper.lerp(0.8, var1.minY, var1.maxY), MathHelper.lerp(0.5, var1.minZ, var1.maxZ));
   }

   @EventTarget
   public void method4815(StopUsingItemEvent var1) {
      if (this.flag2) {
         var1.cancel();
      }
   }
}

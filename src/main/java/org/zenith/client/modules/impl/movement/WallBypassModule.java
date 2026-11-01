package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "WallBypass",
   description = "module.wallbypass.description",
   category = ModuleCategory.MOVEMENT
)
public final class WallBypassModule extends Module {
   public static final WallBypassModule INSTANCE = new WallBypassModule();
   private static final double CONST_doubleValue = 3.0;
   private static final int CONST_intValue = 70;
   private static final int[] CONST_intValues = new int[]{15, 5, 1};
   private final SliderSetting attackDistance = new SliderSetting(
      "module.wallBypass.attackDistance", 3.0F, 3.0F, 6.0F, 0.1F, "module.wallBypass.attackDistance.desc", "b"
   );
   private Rotation rotation;
   private boolean flag;
   private boolean flag2;
   private int intValue;

   private WallBypassModule() {
   }

   @Override
   public void onEnable() {
      this.rotation = this.method7503();
      this.flag = this.rotation != null;
      this.flag2 = false;
      this.intValue = 1;
      if (!this.flag) {
         this.setToggled(false);
      } else {
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.rotation = null;
      this.flag = false;
      this.flag2 = false;
      this.intValue = 0;
      super.onDisable();
   }

   @EventTarget
   public void method7499(PlayerMoveEvent var1) {
      if (this.intValue <= 0) {
         var1.setVec3d(Vec3d.ZERO);
      }
   }

   @EventTarget
   public void method7500(Event5 var1) {
      if (!this.flag || this.rotation == null || mc.player == null || mc.world == null) {
         this.setToggled(false);
      } else if (!this.flag2) {
         Rotation var2 = this.rotation;
         rotationManager.method7848(new RotationRequest(var2, () -> var2, bcCameraTestnerv.getRotationConfig()), 70, this, 1);
         this.flag2 = true;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.getPacket3() instanceof PlayerMoveC2SPacket) {
         if (this.intValue > 0) {
            if (this.flag2) {
               this.intValue--;
            }

            return;
         }

         var1.setCancelled(true);
      }
   }

   public boolean method7501() {
      return this.isEnabled() && this.flag && this.rotation != null && this.intValue <= 0;
   }

   public float method7502() {
      return this.attackDistance.getValue3();
   }

   private Rotation method7503() {
      if (mc.player != null && mc.world != null) {
         Rotation var1 = rotationManager.method7844();
         float var2 = var1.getFloatValue();
         Rotation[] var3 = new Rotation[]{
            new Rotation(var2, -90.0F),
            new Rotation(var2, 90.0F),
            new Rotation(var2, 0.0F),
            new Rotation(mc.player.getYaw(), -90.0F),
            new Rotation(mc.player.getYaw(), 90.0F),
            new Rotation(mc.player.getYaw(), 0.0F)
         };

         for (Rotation var7 : var3) {
            if (this.method7505(var7)) {
               return var7;
            }
         }

         for (int var12 : CONST_intValues) {
            Rotation var8 = this.method7504(var2, var12);
            if (var8 != null) {
               return var8;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Rotation method7504(float var1, int var2) {
      int var3 = -90;

      while (var3 <= 90) {
         for (int var4 = 0; var4 < 360; var4 += var2) {
            Rotation var5 = new Rotation(var1 + var4, var3);
            if (this.method7505(var5)) {
               return var5;
            }

            if (var4 != 0) {
               Rotation var6 = new Rotation(var1 - var4, var3);
               if (this.method7505(var6)) {
                  return var6;
               }
            }
         }

         var3 += var2;
      }

      return null;
   }

   private boolean method7505(Rotation var1) {
      if (mc.player != null && mc.world != null) {
         BlockHitResult var2 = TargetUtils.method3216(mc.player.getCameraPosVec(1.0F), var1, 3.0, var0 -> true);
         return var2 != null && var2.getType() == Type.MISS && UseItemUtils.method6523(BlockPos.ofFloored(var2.getPos()));
      } else {
         return false;
      }
   }
}

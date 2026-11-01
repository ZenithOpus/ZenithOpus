package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ElytraMath;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.Util13;

@ModuleInfo(
   name = "Grim Glide",
   category = ModuleCategory.MOVEMENT,
   description = "module.grimglide.description"
)
public final class GrimGlideModule extends Module {
   public static final GrimGlideModule INSTANCE = new GrimGlideModule();
   private final SliderSetting timerBoost = new SliderSetting("module.grimGlide.timerBoost", 1.5F, 1.0F, 5.0F, 0.1F, "module.grimGlide.timerBoost.desc", "x");
   private final SliderSetting timerReset = new SliderSetting("module.grimGlide.timerReset", 0.5F, 0.0F, 0.9F, 0.01F, "module.grimGlide.timerReset.desc", "x");
   private int intValue;
   private boolean flag;
   private int intValue2;
   private boolean flag2;

   private GrimGlideModule() {
   }

   @Override
   public void onEnable() {
      this.intValue = 0;
      this.flag = false;
      this.intValue2 = 0;
      this.flag2 = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.flag2) {
         Util13.method2404(1.0F);
         this.flag2 = false;
      }

      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.intValue > 0) {
         this.intValue--;
      }

      boolean var2 = mc.player != null && mc.world != null && mc.player.isGliding();
      if (!var2) {
         if (this.flag2) {
            Util13.method2404(1.0F);
            this.flag2 = false;
         }
      } else {
         Util13.method2404(this.intValue2 % 2 == 0 ? this.timerBoost.getValue3() : this.timerReset.getValue3());
         this.flag2 = true;
         this.intValue2++;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc.player != null) {
         if (var1.method7100() && var1.getPacket3() instanceof PlayerPositionLookS2CPacket) {
            mc.execute(() -> {
               this.intValue = 2;
               this.flag = true;
               if (this.intValue2 % 2 == 1) {
                  this.intValue2++;
               }
            });
         } else {
            if (var1.method7099() && var1.getPacket3() instanceof PlayerMoveC2SPacket var2 && !PacketUtils.getItems().contains(var2)) {
               if (mc.player.isGliding() && this.intValue == 0 && !this.flag) {
                  var1.setPacket2(new OnGroundOnly(true, true));
               }

               this.flag = false;
            }
         }
      }
   }

   public Vec3d method5976(Vec3d var1, Vec3d var2, Vec3d var3) {
      if (this.isEnabled() && mc.player != null) {
         Vec3d var4 = ElytraMath.method5974(mc.player, var1, var3, 3.2);
         if (this.intValue >= 1) {
            double var5 = Math.toRadians(mc.player.getYaw());
            return var4.multiply(0.99F, 0.98F, 0.99F).add(-Math.sin(var5) * 0.09F, 0.03F, Math.cos(var5) * 0.09F);
         } else {
            return var4.multiply(0.3F, 0.3F, 0.3F);
         }
      } else {
         return var2;
      }
   }
}

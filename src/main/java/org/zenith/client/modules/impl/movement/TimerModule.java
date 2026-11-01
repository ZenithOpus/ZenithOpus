package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.Util13;
import org.zenith.utility.misc.ViaCompat;

@ModuleInfo(
   name = "Timer",
   description = "module.timer.description",
   category = ModuleCategory.MOVEMENT
)
public final class TimerModule extends Module {
   public static final TimerModule INSTANCE = new TimerModule();
   private final ModeSetting mode = new ModeSetting("module.timer.mode", "module.timer.mode.desc", "module.timer.mode.normal", "module.timer.mode.grim");
   private final SliderSetting speed = new SliderSetting("module.timer.speed", 2.0F, 0.1F, 10.0F, 0.1F, "module.timer.speed.desc", "x");
   private final BindSetting boostKey = new BindSetting("module.timer.boostKey", "module.timer.boostKey.desc", -1, () -> this.mode.isSelected(1));
   private final ModeSetting onFlag = new ModeSetting(
      "module.timer.onFlag", "module.timer.onFlag.desc", "module.timer.onFlag.reset", "module.timer.onFlag.disable", "module.timer.onFlag.none"
   );
   private float floatValue = 1.0F;
   private float energy;
   private long longValue;
   private long longValue2;

   private TimerModule() {
   }

   @Override
   public void onEnable() {
      this.floatValue = 1.0F;
      this.energy = 0.0F;
      if (this.mode.isSelected(1)) {
         this.longValue = System.currentTimeMillis();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method132(1.0F);
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (this.mode.isSelected(0)) {
            this.method132(this.speed.getValue3());
         } else if (this.mode.isSelected(1)) {
            long var2 = System.currentTimeMillis() - this.longValue2;
            if (!(this.energy <= 0.0F) && this.method135(this.boostKey.getKeyCode()) && var2 >= 2000L) {
               this.energy = MathHelper.clamp(this.energy - (0.0025F * this.speed.getValue3() - 0.0025F), 0.0F, 1.0F);
               this.method132(Math.max(this.speed.getValue3(), 1.0F));
            } else {
               this.method132(1.0F);
            }
         } else {
            this.method132(1.0F);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc.player != null && mc.world != null && var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (this.mode.isSelected(1) && var2 instanceof CommonPingS2CPacket && System.currentTimeMillis() - this.longValue2 > 2000L) {
            if (System.currentTimeMillis() - this.longValue > 25000L) {
               this.longValue = System.currentTimeMillis();
               this.energy = 0.0F;
               return;
            }

            if (!this.method134()) {
               this.energy = MathHelper.clamp(this.energy + 0.005F, 0.0F, 1.0F);
            }

            var1.cancel();
         }

         if (var2 instanceof PlayerPositionLookS2CPacket) {
            this.longValue2 = System.currentTimeMillis();
            if (this.onFlag.isSelected(0)) {
               this.method132(1.0F);
               this.energy = 0.0F;
            } else if (this.onFlag.isSelected(1)) {
               this.energy = 0.0F;
               mc.execute(() -> {
                  if (this.isEnabled()) {
                     this.method133("Отключён т.к. тебя флагнуло!");
                  }
               });
            }
         }

         if (var2 instanceof EntityVelocityUpdateS2CPacket var3 && this.mode.isSelected(1) && var3.getEntityId() == mc.player.getId()) {
            this.method132(1.0F);
            this.energy = 0.0F;
         }
      }
   }

   public float method130() {
      return this.isEnabled() ? this.floatValue : 1.0F;
   }

   public float method131() {
      if (!this.isEnabled()) {
         return 0.0F;
      } else {
         return this.mode.isSelected(0) ? 1.0F : MathHelper.clamp(this.energy, 0.0F, 1.0F);
      }
   }

   private void method132(float var1) {
      this.floatValue = var1;
      Util13.method2404(var1);
   }

   private void method133(String var1) {
      Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Text.of(var1));
      this.setToggled(false);
   }

   private boolean method134() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F;
   }

   private boolean method135(int var1) {
      return var1 != -1 && GLFW.glfwGetKey(mc.getWindow().getHandle(), var1) == 1;
   }
}

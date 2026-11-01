package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.Handler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.CriticalsState;
import org.zenith.utility.misc.Util13;
import org.zenith.utility.misc.Vec2f;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "Criticals",
   category = ModuleCategory.COMBAT,
   description = "module.criticals.description"
)
public final class CriticalsModule extends Module {
   public static final CriticalsModule INSTANCE = new CriticalsModule();
   private static final String text = "module.criticals.packet";
   private static final String text2 = "module.criticals.cooldownTimer";
   private static final String text3 = "module.criticals.levitation";
   private static final String text4 = "module.criticals.web";
   private static final double CONST_doubleValue = 120.0;
   private static final double CONST_doubleValue2 = 15.0;
   private static final double CONST_doubleValue3 = 50.0;
   private static final float CONST_floatValue = 0.3F;
   private static final float CONST_floatValue2 = 6.0F;
   private static final int CONST_intValue = 12;
   private final MultiSelectSetting mode = new MultiSelectSetting(
      "module.criticals.mode",
      "module.criticals.mode.desc",
      MultiSelectSetting.Entry.method5231("module.criticals.packet", true),
      MultiSelectSetting.Entry.method5231("module.criticals.cooldownTimer", false),
      MultiSelectSetting.Entry.method5231("module.criticals.levitation", false),
      MultiSelectSetting.Entry.method5231("module.criticals.web", false)
   );
   private boolean flag;
   private boolean flag2;
   private float floatValue = 1.0F;
   private double doubleValue;
   private double doubleValue2;
   private long longValue;

   private CriticalsModule() {
   }

   @EventTarget
   public void method4660(PacketEvent var1) {
      if (var1.method7099() && mc.player != null && var1.getPacket3() instanceof PlayerInteractEntityC2SPacket var2 && method4670(var2)) {
         if (this.method4667()) {
            this.doubleValue = AuraModule.INSTANCE.method6346();
            this.flag = true;
            Util13.method2404(this.method4661(this.method4669().method1709(this.doubleValue, 0.0)));
         } else {
            boolean var5 = this.mode.method5226("module.criticals.levitation") && mc.player.hasStatusEffect(StatusEffects.LEVITATION);
            boolean var4 = this.mode.method5226("module.criticals.web") && UseItemUtils.method6513(Blocks.COBWEB);
            if (!var5 && !var4) {
               if (this.mode.method5226("module.criticals.packet") && !mc.player.isOnGround() && mc.player.fallDistance == 0.0) {
                  UseItemUtils.method6533(
                     -(mc.player.fallDistance = (float)MathUtils.method2651(1.0E-5F, 1.0E-4F)),
                     Zenith.getInstance()
                        .getRotationManager()
                        .method7844()
                        .method3141(new Vec2f((float)MathUtils.method2651(-0.001F, 0.001F), (float)MathUtils.method2651(-0.001F, 0.001F)))
                  );
               }
            } else {
               mc.player
                  .networkHandler
                  .sendPacket(
                     new PositionAndOnGround(mc.player.getX(), mc.player.getY() - MathUtils.method2651(1.0E-5F, 1.0E-4F), mc.player.getZ(), false, false)
                  );
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (!this.method4667()) {
            if (this.flag) {
               Util13.method2404(1.0F);
               this.method4671();
            }
         } else {
            this.method4666();
            if (this.flag) {
               CriticalsState var2 = this.method4669();
               this.doubleValue = AuraModule.INSTANCE.method6346();
               long var3 = var2.method1709(this.doubleValue, 0.0);
               if (mc.player.fallDistance > 0.0 && var2.method1708(this.doubleValue, 0.0) && AuraModule.INSTANCE.method6348()) {
                  this.flag = false;
                  this.method4665();
                  Util13.method2404(1.0F);
               } else {
                  Util13.method2404(this.method4661(var3));
               }
            }
         }
      }
   }

   private float method4661(long var1) {
      if (mc.player != null && !mc.player.isOnGround()) {
         double var3 = var1 / 1000000.0;
         if (mc.player.fallDistance > 0.0) {
            this.method4665();
            return this.method4664(var3);
         } else if (mc.player.fallDistance != 0.0) {
            this.method4665();
            return 1.0F;
         } else {
            int var5 = AuraModule.INSTANCE.method6349(12, true);
            if (var5 < 0) {
               this.method4665();
               return 1.0F;
            } else if (var1 <= 0L) {
               float var14 = this.method4662(var5);
               if (var14 <= 1.0F) {
                  this.method4665();
                  return 1.0F;
               } else {
                  this.flag2 = true;
                  this.floatValue = var14;
                  return var14;
               }
            } else if (this.flag2) {
               this.floatValue = Math.min(this.floatValue, this.method4662(var5));
               if (this.floatValue <= 1.0F) {
                  this.method4665();
                  return 1.0F;
               } else {
                  return this.floatValue;
               }
            } else {
               long var6 = (long)Math.ceil(this.method4669().getDoubleValue());
               if (var1 > var6) {
                  return 0.3F;
               } else {
                  double var8 = var5 * 50.0 / var3;
                  if (var8 <= 1.0) {
                     return 1.0F;
                  } else {
                     float var10 = (float)Math.min(6.0, var8);
                     double var11 = var5 * (50.0 - 50.0 / var10);
                     boolean var13 = var8 <= 6.0;
                     if (var13 && this.method4663() + 0.001 >= var11) {
                        this.flag2 = true;
                        this.floatValue = var10;
                        return this.floatValue;
                     } else {
                        return 0.3F;
                     }
                  }
               }
            }
         }
      } else {
         this.method4665();
         return 1.0F;
      }
   }

   private float method4662(int var1) {
      double var2 = var1 * 50.0;
      double var4 = this.method4663();
      if (var2 <= 0.0 || var4 <= 0.0) {
         return 1.0F;
      } else if (var4 >= var2) {
         return 6.0F;
      } else {
         double var6 = 1.0 / (1.0 - var4 / var2);
         return (float)Math.min(6.0, var6);
      }
   }

   private double method4663() {
      return Math.max(0.0, this.doubleValue2 - 15.0);
   }

   private float method4664(double var1) {
      int var3 = AuraModule.INSTANCE.method6350(12);
      if (var3 < 0) {
         return 1.0F;
      } else {
         double var4 = var3 * 50.0;
         if (var1 <= var4) {
            return 1.0F;
         } else {
            double var6 = var4 / 0.3F;
            return var1 > var6 ? 1.0F : (float)Math.max(0.3F, var4 / var1);
         }
      }
   }

   private void method4665() {
      this.flag2 = false;
      this.floatValue = 1.0F;
   }

   private void method4666() {
      long var1 = System.nanoTime();
      if (this.longValue != 0L) {
         double var3 = (var1 - this.longValue) / 1000000.0;
         this.doubleValue2 = Math.max(0.0, Math.min(120.0, this.doubleValue2 + var3 - 50.0));
      }

      this.longValue = var1;
   }

   public boolean method4667() {
      return this.isEnabled() && this.mode.method5226("module.criticals.cooldownTimer") && AuraModule.INSTANCE.method6347();
   }

   public boolean method4668(float var1) {
      if (!this.method4667()) {
         return true;
      } else {
         this.doubleValue = AuraModule.INSTANCE.method6346();
         CriticalsState var2 = this.method4669();
         return mc.player != null && !(mc.player.fallDistance <= 0.0) ? var2.method1708(this.doubleValue, var1) : false;
      }
   }

   private CriticalsState method4669() {
      return Zenith.getInstance().getServerDetector().getCriticalsState();
   }

   private static boolean method4670(PlayerInteractEntityC2SPacket var0) {
      final boolean[] var1 = new boolean[]{false};
      var0.handle(new Handler() {
         @Override
         public void interact(Hand hand) {
         }

         @Override
         public void interactAt(Hand hand, Vec3d pos) {
         }

         @Override
         public void attack() {
            var1[0] = true;
         }
      });
      return var1[0];
   }

   @Override
   public void onEnable() {
      this.method4671();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      Util13.method2404(1.0F);
      this.method4671();
      super.onDisable();
   }

   private void method4671() {
      this.flag = false;
      this.method4665();
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.longValue = 0L;
   }

   public boolean method4672() {
      return mc.player != null && !this.method4667()
         ? this.mode.method5226("module.criticals.packet") && !mc.player.isOnGround()
            || this.mode.method5226("module.criticals.levitation") && mc.player.hasStatusEffect(StatusEffects.LEVITATION)
            || this.mode.method5226("module.criticals.web") && UseItemUtils.method6513(Blocks.COBWEB)
         : false;
   }

   @Generated
   public MultiSelectSetting getMode() {
      return this.mode;
   }

   @Generated
   public boolean isFlag7() {
      return this.flag;
   }

   @Generated
   public boolean isFlag24() {
      return this.flag2;
   }

   @Generated
   public float getFloatValue3() {
      return this.floatValue;
   }

   @Generated
   public double getDoubleValue() {
      return this.doubleValue;
   }

   @Generated
   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   @Generated
   public long getLongValue4() {
      return this.longValue;
   }
}

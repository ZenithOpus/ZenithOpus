package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.List;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.MovementCorrectionEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.Util13;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "Speed",
   description = "module.speed.description",
   category = ModuleCategory.MOVEMENT
)
public final class SpeedModule extends Module {
   public static final SpeedModule INSTANCE = new SpeedModule();
   private static final int CONST_intValue = 16;
   private final ModeSetting mode = new ModeSetting(
      "module.speed.mode",
      "module.speed.mode.desc",
      "module.speed.mode.holyWorld",
      "module.speed.mode.grimOld",
      "module.speed.mode.reallyWorld",
      "module.speed.mode.grimElytra",
      "module.speed.mode.grim72"
   );
   private final SliderSetting grim72SlowTimer = new SliderSetting(
      "module.speed.grim72SlowTimer", 0.4F, 0.0F, 2.0F, 0.1F, "module.speed.grim72SlowTimer.desc", "x", this::method6601
   );
   private final SliderSetting grim72FastTimer = new SliderSetting(
      "module.speed.grim72FastTimer", 3.4F, 1.0F, 5.0F, 0.1F, "module.speed.grim72FastTimer.desc", "x", this::method6601
   );
   private final SliderSetting speed = new SliderSetting("module.speed.speed", 0.1F, 0.0F, 0.5F, 0.01F, "module.speed.speed.desc", "x", this::method6607);
   private final SliderSetting collisionRadius = new SliderSetting(
      "module.speed.collisionRadius", 0.3F, 0.0F, 1.0F, 0.01F, "module.speed.collisionRadius.desc", "b", this::method6607
   );
   private final BooleanSetting onlyAura = new BooleanSetting("module.speed.onlyAura", "module.speed.onlyAura.desc", true, this::method6607);
   private int ticks = 0;
   private int intValue = 0;
   private double doubleValue;
   public boolean flag = false;
   public int intValue2 = 0;

   private SpeedModule() {
   }

   @Override
   public void onEnable() {
      this.intValue2 = 0;
      this.flag = false;
      this.ticks = 0;
      this.intValue = 0;
      Util13.method2404(1.0F);
      if (this.method6606() && mc.player != null && !mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
         this.method6608();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      Util13.method2404(1.0F);
      if (this.method6606() && mc.player != null && mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
         this.method6608();
      }

      super.onDisable();
   }

   @EventTarget
   public void method6592(SprintUpdateEvent var1) {
      if (this.flag) {
         var1.setFlag2(false);
         this.flag = false;
      }
   }

   @EventTarget(4)
   public void method6593(UpdateEvent var1) {
      if (this.intValue2 > 0) {
         this.intValue2--;
      }

      if (this.method6607()) {
         if (AuraModule.INSTANCE.isEnabled() || !this.onlyAura.isEnabled()) {
            this.method6602(null);
         }
      }
   }

   @EventTarget
   public void method6594(MovementCorrectionEvent var1) {
      if (this.method6603() && mc.player != null) {
         Util13.method2404(1.7F);
         if (this.ticks > 3) {
            double var2 = 0.03;
            if (this.ticks % 2 == 0) {
               mc.player.addVelocity(0.0, 0.03F, 0.0);
               var2 = mc.player.isOnGround() ? 0.085 : 0.03;
            }

            if (this.method6609()) {
               double var4 = MovementInputUtils.method7427(mc.player.getYaw(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input));
               mc.player.addVelocity(-Math.sin(var4) * var2, 0.0, Math.cos(var4) * var2);
            }
         }

         this.ticks++;
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      if (this.method6603() && mc.player != null) {
         if (mc.player.verticalCollision) {
            this.intValue++;
         } else {
            this.intValue = 0;
         }

         if (this.intValue >= 1) {
            mc.player.jump();
         }
      }
   }

   @EventTarget
   public void method6595(PostMotionEvent var1) {
      if (this.method6603() && mc.player != null && mc.getNetworkHandler() != null) {
         if (this.ticks % 2 == 0) {
            Util13.method2404(0.3F);
            PacketUtils.method3150(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (this.method6603()) {
         if (var1.getPacket3() instanceof PlayerPositionLookS2CPacket) {
            mc.execute(() -> {
               if (this.ticks % 2 == 1) {
                  this.ticks++;
               }

               Util13.method2404(1.0F);
            });
         }
      }
   }

   @EventTarget
   public void method6596(PostMotionEvent var1) {
      if (this.method6601()) {
         if (this.method6599()) {
            this.method6600();
         } else {
            if (this.ticks % 2 == 0) {
               Util13.method2404(this.grim72SlowTimer.getValue3());
               PacketUtils.method3150(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               PacketUtils.method3150(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            }
         }
      }
   }

   @EventTarget
   public void method6597(MovementCorrectionEvent var1) {
      if (this.method6601()) {
         if (this.method6599()) {
            this.method6600();
         } else {
            Util13.method2404(this.grim72FastTimer.getValue3());
            if (this.ticks > 3) {
               double var2 = 0.03;
               if (this.ticks % 2 == 0) {
                  mc.player.addVelocity(0.0, 0.03, 0.0);
                  var2 = mc.player.isOnGround() ? 0.085 : 0.03;
               }

               double var4 = MovementInputUtils.method7427(mc.player.getYaw(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input));
               mc.player.addVelocity(-Math.sin(var4) * var2, 0.0, Math.cos(var4) * var2);
            }

            this.ticks++;
         }
      }
   }

   @EventTarget
   public void method6598(PacketEvent var1) {
      if (this.method6601() && var1.method7100() && var1.getPacket3() instanceof PlayerPositionLookS2CPacket) {
         mc.execute(() -> {
            if (!this.method6599()) {
               if (this.ticks % 2 == 1) {
                  this.ticks++;
               }

               Util13.method2404(1.0F);
            }
         });
      }
   }

   private boolean method6599() {
      return mc.player == null
         || mc.world == null
         || mc.getNetworkHandler() == null
         || !this.method6609()
         || mc.player.hasVehicle()
         || mc.player.getAbilities().flying;
   }

   private void method6600() {
      this.ticks = 0;
      Util13.method2404(1.0F);
   }

   public boolean method6601() {
      return this.mode.isSelected(4);
   }

   private void method6602(MoveInputEvent var1) {
      boolean var2;
      if (var2 = mc.player.isSprinting()) {
         mc.player.setSprinting(false);
      }

      PlayerSimulation var3 = PlayerSimulation.method6031(1);
      if (var2) {
         mc.player.setSprinting(true);
      }

      Box var4 = var3.box.expand(this.collisionRadius.getValue3(), this.collisionRadius.getValue3(), this.collisionRadius.getValue3());
      Object var5 = null;
      if (!this.onlyAura.isEnabled()) {
         if (AuraModule.INSTANCE.method6358() == null) {
            for (Entity var7 : mc.world.getEntities()) {
               if (var7 != mc.player && (var7 instanceof LivingEntity || var7 instanceof BoatEntity) && var4.intersects(var7.getBoundingBox())) {
                  var5 = var7;
                  break;
               }
            }
         }
      } else {
         if (AuraModule.INSTANCE.method6358() == null) {
            return;
         }

         if (var4.intersects(AuraModule.INSTANCE.method6358().getBoundingBox())) {
            var5 = AuraModule.INSTANCE.method6358();
         }
      }

      if (var5 instanceof PlayerEntity) {
         Vec3d var23 = var3.vec3d;
         Vec3d var24 = (ReachV3Module.INSTANCE.isEnabled() && ReachV3Module.INSTANCE.getVec3d2() != null
               ? ((Entity)var5).dimensions.getBoxAt(ReachV3Module.INSTANCE.getVec3d2())
               : (var5 instanceof PlayerEntity ? PlayerSimulation.method6032((PlayerEntity)var5, 2).box : ((Entity)var5).getBoundingBox()))
            .getCenter();
         double var8 = var24.x - var23.x;
         double var10 = var24.z - var23.z;
         double var12 = Math.sqrt(var8 * var8 + var10 * var10);
         double var14 = this.speed.getValue3();
         double var16 = 0.0;
         double var18 = Math.max(0.0, var12 - var16);
         double var20 = Math.min(var14, var18);
         double[] var22 = new double[]{var8 * var20, var10 * var20};
         mc.player.addVelocity(var22[0], 0.0, var22[1]);
         if (this.mode.isSelected(0)) {
            if (mc.player.lastSprinting) {
               mc.player.addVelocity(-var22[0], 0.0, -var22[1]);
            }

            mc.player.setSprinting(false);
            mc.options.sprintKey.setPressed(false);
            this.flag = true;
         }

         if (mc.player.getBoundingBox().intersects(((Entity)var5).getBoundingBox())) {
            this.intValue2 = 3;
            return;
         }
      } else {
         this.intValue2 = 0;
      }
   }

   private boolean method6603() {
      return this.mode.isSelected(2);
   }

   @EventTarget
   public void method6604(UpdateEvent var1) {
      if (this.method6606() && mc.player != null && mc.world != null) {
         if (this.method6609()) {
            mc.player.setSprinting(true);
         }

         if (!mc.player.isOnGround() && !mc.player.isGliding() && mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            PacketUtils.method3150(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            mc.player.startGliding();
         }

         if (mc.player.isOnGround()) {
            this.ticks = 16;
            this.doubleValue = mc.player.getY();
         }
      }
   }

   @EventTarget
   public void method6605(Event5 var1) {
      if (this.method6606()
         && mc.player != null
         && mc.world != null
         && this.method6609()
         && !mc.player.isOnGround()
         && mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
         float var2 = (float)Math.toDegrees(
            MovementInputUtils.method7427(mc.player.getYaw(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input))
         );
         if (this.ticks > 0) {
            double var3 = Math.abs(mc.player.getY() - this.doubleValue);
            Math.max(0.0, 1.0 - var3);
            float var7 = 75.0F;
            Rotation var8 = new Rotation(var2, var7);
            zenith.getRotationManager().method7849(new RotationRequest(var8, () -> var8, bcCameraTestnerv.getRotationConfig()), 2, this);
         }
      }
   }

   public boolean method6606() {
      return this.mode.isSelected(3);
   }

   private boolean method6607() {
      return this.mode.isSelected(0) || this.mode.isSelected(1);
   }

   private void method6608() {
      if (mc.player != null) {
         Slot var1;
         if (mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            var1 = InventoryUtils.method916(
               List.of(
                  Items.NETHERITE_CHESTPLATE,
                  Items.DIAMOND_CHESTPLATE,
                  Items.CHAINMAIL_CHESTPLATE,
                  Items.IRON_CHESTPLATE,
                  Items.GOLDEN_CHESTPLATE,
                  Items.LEATHER_CHESTPLATE
               ),
               Comparator.comparingInt(var0 -> var0.id)
            );
         } else {
            var1 = InventoryUtils.method912(mc.player.playerScreenHandler, Items.ELYTRA, Comparator.comparingInt(var0 -> var0.id), var0 -> true);
         }

         if (var1 != null) {
            InventoryUtils.method934(var1, 6, true, false);
         }
      }
   }

   private boolean method6609() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F;
   }

   @Generated
   public int getIntValue23() {
      return this.intValue2;
   }
}

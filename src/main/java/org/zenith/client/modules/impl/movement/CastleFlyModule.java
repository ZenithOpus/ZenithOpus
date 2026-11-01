package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.Vec2f;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "CastleFly",
   description = "module.flybypass.description",
   category = ModuleCategory.MOVEMENT
)
public final class CastleFlyModule extends Module {
   public static final CastleFlyModule INSTANCE = new CastleFlyModule();
   private UseItemUtils.Data data;
   private ModeSetting mode = new ModeSetting("module.flyBypass.mode", "module.flyBypass.mode.desc", "module.flyBypass.safeMode", "module.flyBypass.riskMode");
   private boolean flag = false;
   private int intValue;
   private final SliderSetting delay = new SliderSetting("module.flyBypass.delay", 500.0F, 50.0F, 1000.0F, 50.0F, "module.flyBypass.delay.desc", "ms");
   private final Queue<ReachV3Module.Entry> queue = new ConcurrentLinkedQueue<>();
   private BlockHitResult blockHitResult = null;
   public int intValue2 = 0;

   private CastleFlyModule() {
   }

   @Override
   public void onEnable() {
      this.flag = false;
      super.onEnable();
      ChatLogger.method2019("Для работы держите ЛЮБОЙ блок руках");
   }

   @Override
   public void onDisable() {
      this.flag = true;
   }

   @EventTarget
   public void method7307(WorldRenderEvent var1) {
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && !var1.isCancelled()) {
         try {
            Packet var2 = var1.getPacket3();
            if (this.queue.isEmpty() && mc.world == null) {
               return;
            }

            if (var2 instanceof ChatMessageC2SPacket
               || var2 instanceof HealthUpdateS2CPacket
               || var2 instanceof GameMessageS2CPacket
               || var2 instanceof CommandExecutionC2SPacket) {
               return;
            }

            var1.cancel();
            Vec3d var3 = null;
            if (var2 instanceof BlockUpdateS2CPacket var4) {
               var3 = new Vec3d(var4.getPos());
            }

            this.queue.add(new ReachV3Module.Entry(var2, var3));
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method7308(PacketProcessEndEvent var1) {
      long var2 = System.currentTimeMillis();
      if (mc.world == null || mc.player == null) {
         this.queue.clear();
      }

      try {
         this.queue.removeIf(var3 -> {
            if (var2 - var3.getTime() < (long)(this.delay.getValue3() * 2.0F) && mc.world != null && !this.flag) {
               return false;
            } else {
               try {
                  var3.getPacket().apply(mc.getNetworkHandler());
               } catch (Throwable var6) {
               }

               return true;
            }
         });
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      if (this.flag) {
         this.flag = false;
         super.onDisable();
      }
   }

   @EventTarget
   public void method7309(Event5 var1) {
      this.blockHitResult = null;
      if (mc.crosshairTarget instanceof BlockHitResult var2) {
         this.blockHitResult = var2;
      }

      if (this.mode.isSelected(0)) {
         rotationManager.method7848(
            new RotationRequest(
               rotationManager.method7844().method3141(new Vec2f(0.0F, 900.0F)),
               () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotationManager.method7844().method3141(new Vec2f(0.0F, 900.0F))),
               bcCameraTestnerv.getRotationConfig()
            ),
            20,
            this,
            2
         );
      } else {
         this.data = null;
         if (mc.options.jumpKey.isPressed() && !MovementInputUtils.method7418()) {
            this.intValue = (int)Math.floor(mc.player.getY() - 1.0);
         }

         PlayerSimulation var6 = PlayerSimulation.method6031(2);
         BlockPos var7 = new BlockPos((int)Math.floor(var6.getVec3d().x), (int)Math.floor(mc.player.getY() - 1.0), (int)Math.floor(var6.getVec3d().z));
         this.data = this.method7313(var7);
         if (this.data != null) {
            BlockHitResult var4 = new BlockHitResult(
               new Vec3d(this.data.getBlockPos().getX() + 0.5, this.data.getBlockPos().getY() + 0.5, this.data.getBlockPos().getZ() + 0.5),
               this.data.getDirection(),
               this.data.getBlockPos(),
               false
            );
            Rotation var5 = RotationUtils.method6913(var4.getPos());
            rotationManager.method7848(
               new RotationRequest(var5, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var5), bcCameraTestnerv.getRotationConfig()),
               20,
               this,
               1
            );
         }
      }
   }

   @EventTarget(0)
   public void method7310(MoveInputEvent var1) {
      MovementInputUtils.method7426(var1, Zenith.getInstance().getRotationManager().method7844().getFloatValue(), RotationUtils.method6911().getFloatValue());
   }

   @EventTarget
   public void method7311(UpdateEvent var1) {
      if (this.intValue2 > 0) {
         this.intValue2--;
      } else {
         if (this.mode.isSelected(0)) {
            if (mc.crosshairTarget instanceof BlockHitResult var2 && var2.getType() == Type.BLOCK) {
               UseItemUtils.method6533(0.0, zenith.getRotationManager().method7844().method3140((float)Math.random(), (float)Math.random()));
               UseItemUtils.method6533(0.0, zenith.getRotationManager().method7844().method3140((float)Math.random(), (float)Math.random()));
               UseItemUtils.method6514(var2, Hand.MAIN_HAND);
               this.intValue2 = 2;
            }
         } else if (mc.crosshairTarget instanceof BlockHitResult var5 && this.data != null && this.data.getBlockPos().equals(var5.getBlockPos())) {
            Vec3d var7 = mc.player.getEyePos();
            BlockPos var4 = var5.getBlockPos().offset(this.data.getDirection());
            if (!method7312(var7, var4, this.data.getDirection())) {
               return;
            }

            UseItemUtils.method6514(
               new BlockHitResult(
                  this.data.getBlockPos().toCenterPos().add((float)(Math.random() / 2.0)), this.data.getDirection(), this.data.getBlockPos(), false
               ),
               Hand.MAIN_HAND
            );
            this.intValue2 = 1;
         }
      }
   }

   public static boolean method7312(Vec3d var0, BlockPos var1, Direction var2) {
      double var10000 = Double.MAX_VALUE;
      double var5 = Double.MIN_VALUE;
      Box var7 = new Box(var0.x, mc.player.getY() - 1.0, var0.z, var0.x, var0.y + var5, var0.z);
      Box var8 = new Box(var1.offset(var2));
      if (var7.intersects(var8)) {
         return true;
      } else {
         return switch (var2) {
            case NORTH -> var7.minZ > var8.minZ;
            case SOUTH -> var7.maxZ < var8.maxZ;
            case EAST -> var7.maxX < var8.maxX;
            case WEST -> var7.minX > var8.minX;
            case UP -> var7.maxY < var8.maxY;
            case DOWN -> var7.minY > var8.minY;
         };
      }
   }

   private UseItemUtils.Data method7313(BlockPos var1) {
      UseItemUtils.Data var2 = UseItemUtils.method6534(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = UseItemUtils.method6534(var1.add(-1, 0, 0));
         if (var2 != null) {
            return var2;
         } else {
            var2 = UseItemUtils.method6534(var1.add(1, 0, 0));
            if (var2 != null) {
               return var2;
            } else {
               var2 = UseItemUtils.method6534(var1.add(0, 0, 1));
               if (var2 != null) {
                  return var2;
               } else {
                  var2 = UseItemUtils.method6534(var1.add(0, 0, -1));
                  if (var2 != null) {
                     return var2;
                  } else {
                     var2 = UseItemUtils.method6534(var1.add(-2, 0, 0));
                     if (var2 != null) {
                        return var2;
                     } else {
                        var2 = UseItemUtils.method6534(var1.add(2, 0, 0));
                        if (var2 != null) {
                           return var2;
                        } else {
                           var2 = UseItemUtils.method6534(var1.add(0, 0, 2));
                           if (var2 != null) {
                              return var2;
                           } else {
                              var2 = UseItemUtils.method6534(var1.add(0, 0, -2));
                              if (var2 != null) {
                                 return var2;
                              } else {
                                 var2 = UseItemUtils.method6534(var1.add(0, -1, 0));
                                 if (var2 != null) {
                                    return var2;
                                 } else {
                                    var2 = UseItemUtils.method6534(var1.add(1, -1, 0));
                                    if (var2 != null) {
                                       return var2;
                                    } else {
                                       var2 = UseItemUtils.method6534(var1.add(-1, -1, 0));
                                       if (var2 != null) {
                                          return var2;
                                       } else {
                                          var2 = UseItemUtils.method6534(var1.add(0, -1, 1));
                                          return var2 != null ? var2 : UseItemUtils.method6534(var1.add(0, -1, -1));
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean method7314(double var1, double var3) {
      return !mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().expand(-0.1, 0.0, -0.1).offset(var1, -2.0, var3)).iterator().hasNext();
   }
}

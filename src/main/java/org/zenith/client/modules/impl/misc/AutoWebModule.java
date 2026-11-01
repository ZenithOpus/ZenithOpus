package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.combat.AutoTrapModule;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoWeb",
   description = "module.autoweb.description",
   category = ModuleCategory.MISC
)
public final class AutoWebModule extends Module {
   public static final AutoWebModule INSTANCE = new AutoWebModule();
   private final SliderSetting distance = new SliderSetting("module.autoWeb.distance", 4.0F, 1.0F, 6.0F, 0.1F, "module.autoWeb.distance.desc", "b");
   private final SliderSetting searchXZ = new SliderSetting("module.autoWeb.searchXZ", 4.0F, 0.0F, 6.0F, 0.1F, "module.autoWeb.searchXZ.desc", "b");
   private final SliderSetting search = new SliderSetting("module.autoWeb.search", 4.0F, 1.0F, 6.0F, 0.1F, "module.autoWeb.search.desc", "b");
   private final ModeSetting hand = new ModeSetting("module.autoWeb.hand", "module.autoWeb.hand.desc", "module.autoWeb.rightHand", "module.autoWeb.leftHand");
   private PlayerEntity playerEntity;
   private Slot slot = null;
   private Rotation rotation = null;
   private Rotation rotation2 = null;
   private AutoWebModule.Handler handler = null;
   private StopWatch stopWatch = new StopWatch();
   public List<BlockPos> list;
   private Set<BlockPos> items = Set.of();

   private AutoWebModule() {
   }

   @Override
   public void onEnable() {
      if (UseItemUtils.method6526()) {
         this.setEnabled(false);
      } else {
         super.onEnable();
      }
   }

   @EventTarget
   public void method278(Event9 var1) {
      if (mc.player != null && mc.world != null) {
         if (!AutoTrapModule.INSTANCE.isEnabled() || !AutoTrapModule.INSTANCE.method2427()) {
            if (!var1.isCancelled() && mc.player.age % 2 == 0) {
               BlockHitResult var2 = this.handler != null ? this.handler.isRotate(Zenith.getInstance().getRotationManager().method7844()) : null;
               if (var2 != null && mc.player.getStackInHand(this.hand.isSelected(0) ? Hand.MAIN_HAND : Hand.OFF_HAND).getItem() == Items.COBWEB) {
                  UseItemUtils.method6514(var2, this.hand.isSelected(0) ? Hand.MAIN_HAND : Hand.OFF_HAND);
                  GlowUtil.method2230(new Box(var2.getBlockPos()), -1, 1.0F);
                  this.rotation = null;
                  this.handler = null;
               }
            }
         }
      }
   }

   @EventTarget
   public void method279(Event5 var1) {
      if (!AutoTrapModule.INSTANCE.isEnabled() || !AutoTrapModule.INSTANCE.method2427()) {
         this.playerEntity = this.method284();
         if (this.playerEntity != null) {
            this.method280();
            Rotation var2 = this.rotation;
            if (var2 != null) {
               rotationManager.method7849(
                  new RotationRequest(var2, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var2), bcCameraTestnerv.getRotationConfig()),
                  5,
                  this
               );
            }
         } else {
            this.method281();
            if (KeyBindUtils.method2630(AutoWebModule.class) && (KeyBindUtils.method2630(AutoTotemModule.class) || this.hand.isSelected(0))) {
               this.method282();
            }
         }
      }
   }

   public void method280() {
      if (!AutoTrapModule.INSTANCE.isEnabled() || !AutoTrapModule.INSTANCE.method2427()) {
         this.method281();

         try {
            Slot var1 = InventoryUtils.method909(mc.player.playerScreenHandler, var0 -> var0.getStack().getItem() == Items.COBWEB);
            byte var2 = 0;
            if (var1 != null) {
               BlockPos var3 = this.playerEntity.getBlockPos();
               Box var4 = this.playerEntity.getBoundingBox().expand(this.searchXZ.getValue3(), this.search.getValue3(), this.searchXZ.getValue3());
               this.list = UseItemUtils.method6506(var3, (int)Math.ceil(this.searchXZ.getValue3()), (int)Math.ceil(this.search.getValue3()))
                  .stream()
                  .filter(var1x -> {
                     BlockState var2x = mc.world.getBlockState(var1x);
                     return var2x.isAir() && var4.intersects(var1x.getX(), var1x.getY(), var1x.getZ(), var1x.getX() + 1, var1x.getY() + 1, var1x.getZ() + 1);
                  })
                  .sorted(Comparator.comparingDouble(var1x -> var1x.getSquaredDistance(var3)))
                  .toList();
               this.items = new HashSet<>(this.list);
               Vec3d var5 = mc.player.getCameraPosVec(1.0F);

               label84:
               for (BlockPos var7 : this.list) {
                  BlockState var9 = mc.world.getBlockState(var7);
                  if (var9.isReplaceable() && !var9.isAir()) {
                     Rotation var8 = RotationUtils.method6912(new Vec3d(var7.getX() + 0.5F, var7.getY() + 0.5F, var7.getZ() + 0.5F));
                     BlockHitResult var18 = TargetUtils.method3216(var5, var8, 3.0, var1x -> var1x != null && this.items.contains(var1x.getBlockPos()));
                     if (var18 != null && var18.getType() != Type.MISS) {
                        this.handler = var1x -> {
                           BlockHitResult var2x = TargetUtils.method3216(
                              mc.player.getCameraPosVec(1.0F),
                              var1x,
                              this.distance.getValue3(),
                              var1xx -> var1xx != null
                                 && !mc.world.getBlockState(var1xx.getBlockPos()).isReplaceable()
                                 && this.items.contains(var1xx.getBlockPos().offset(var1xx.getSide()))
                           );
                           return var2x != null && var2x.getType() != Type.MISS ? var2x : null;
                        };
                        this.rotation = var8;
                     }
                  } else {
                     Direction[] var10 = Direction.values();
                     int var11 = var10.length;
                     int var12 = 0;

                     while (true) {
                        if (var12 < var11) {
                           Direction var13 = var10[var12];
                           Vec3d var14 = Vec3d.ofCenter(var7).add(Vec3d.of(var13.getVector()).multiply(0.5));
                           Rotation var15 = RotationUtils.method6913(var14);
                           BlockHitResult var16 = TargetUtils.method3216(
                              var5,
                              var15,
                              this.distance.getValue3(),
                              var1x -> var1x != null
                                 && !mc.world.getBlockState(var1x.getBlockPos()).isReplaceable()
                                 && this.items.contains(var1x.getBlockPos().offset(var1x.getSide()))
                           );
                           if (var16 == null || var16.getType() == Type.MISS) {
                              var12++;
                              continue;
                           }

                           if (var2 == 0) {
                              this.handler = var1x -> {
                                 BlockHitResult var2x = TargetUtils.method3216(
                                    mc.player.getCameraPosVec(1.0F),
                                    var1x,
                                    this.distance.getValue3(),
                                    var1xx -> var1xx != null
                                       && !mc.world.getBlockState(var1xx.getBlockPos()).isReplaceable()
                                       && this.items.contains(var1xx.getBlockPos().offset(var1xx.getSide()))
                                 );
                                 return var2x != null && var2x.getType() != Type.MISS ? var2x : null;
                              };
                              this.rotation = var15;
                           } else {
                              this.rotation2 = var15;
                           }

                           var2++;
                        }

                        if (var2 == 2) {
                           break label84;
                        }
                        break;
                     }
                  }
               }

               if (KeyBindUtils.method2630(AutoWebModule.class) && (KeyBindUtils.method2630(AutoTotemModule.class) || this.hand.isSelected(0))) {
                  if (var2 > 0) {
                     if (mc.player.getStackInHand(this.hand.isSelected(0) ? Hand.MAIN_HAND : Hand.OFF_HAND).getItem() != Items.COBWEB) {
                        KeyBindUtils.method2627(
                           AutoWebModule.class, () -> InventoryUtils.method900(var1, this.hand.isSelected(0) ? Hand.MAIN_HAND : Hand.OFF_HAND, true)
                        );
                        if (this.slot == null) {
                           this.slot = var1;
                        }
                     }
                  } else if (this.slot != null) {
                     this.method282();
                     return;
                  }
               }
            }
         } catch (Exception var17) {
            var17.printStackTrace();
         }
      }
   }

   private void method281() {
      this.rotation = null;
      this.rotation2 = null;
      this.handler = null;
   }

   private void method282() {
      if (this.slot != null) {
         Slot var1 = this.slot;
         KeyBindUtils.method2627(AutoWebModule.class, () -> InventoryUtils.method900(var1, this.hand.isSelected(0) ? Hand.MAIN_HAND : Hand.OFF_HAND, true));
         this.slot = null;
      }
   }

   @Override
   public void onDisable() {
      if (mc.player != null) {
         this.method282();
      }

      this.slot = null;
      this.method281();
      this.playerEntity = null;
      super.onDisable();
   }

   @EventTarget
   private void method283(MoveInputEvent var1) {
      if (this.handler != null) {
         MovementInputUtils.method7426(var1, zenith.getRotationManager().method7844().getFloatValue(), mc.player.getYaw());
      }
   }

   public PlayerEntity method284() {
      return mc.world
         .getPlayers()
         .stream()
         .filter(
            var1 -> var1 != mc.player
               && !Zenith.getInstance().getFriends().method7626(var1)
               && mc.player.squaredDistanceTo(var1) < (this.distance.getValue3() + 0.1) * (this.distance.getValue3() + 0.1)
         )
         .min(Comparator.comparingDouble(var0 -> mc.player.squaredDistanceTo(var0) - (var0 == AuraModule.INSTANCE.getLivingEntity22() ? 100 : 0)))
         .orElse(null);
   }

   public interface Handler {
      BlockHitResult isRotate(Rotation var1);
   }
}

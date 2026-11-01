package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptySet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.Util10;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "AncientFarm",
   category = ModuleCategory.PLAYER,
   description = "module.ancientfarm.description"
)
public final class AncientFarmModule extends Module {
   public static final AncientFarmModule INSTANCE = new AncientFarmModule();
   private static final int CONST_intValue = 20;
   private static final int CONST_intValue2 = 8;
   private static final int CONST_intValue3 = 4;
   private static final double CONST_doubleValue = 26.0;
   private static final int CONST_intValue4 = 45;
   private static final long CONST_longValue = 30L;
   private static final long CONST_longValue2 = 25L;
   private static final int CONST_intValue5 = 4;
   private static final int CONST_intValue6 = 80;
   private static final long CONST_longValue3 = 9000L;
   private static final double CONST_doubleValue2 = 5.0;
   private static final int CONST_intValue7 = 24;
   private static final int CONST_intValue8 = 45;
   private final ModeSetting searchMode = new ModeSetting(
      "module.ancientFarm.searchMode",
      "module.ancientFarm.searchMode.desc",
      new String[]{"module.ancientFarm.searchMode.top", "module.ancientFarm.searchMode.bottom"}
   );
   private final SliderSetting tntPerRun = new SliderSetting("module.ancientFarm.tntPerRun", 1.0F, 1.0F, 5.0F, 1.0F, "module.ancientFarm.tntPerRun.desc", "шт");
   private final SliderSetting blastsPerArea = new SliderSetting(
      "module.ancientFarm.blastsPerArea", 6.0F, 1.0F, 20.0F, 1.0F, "module.ancientFarm.blastsPerArea.desc", "шт"
   );
   private final SliderSetting rangeWeight = new SliderSetting(
      "module.ancientFarm.rangeWeight", 35.0F, 0.0F, 100.0F, 5.0F, "module.ancientFarm.rangeWeight.desc", "%"
   );
   private final SliderSetting scanRadius = new SliderSetting(
      "module.ancientFarm.scanRadius", 24.0F, 12.0F, 48.0F, 4.0F, "module.ancientFarm.scanRadius.desc", "бл"
   );
   private final BooleanSetting autoEat = new BooleanSetting("module.ancientFarm.autoEat", "module.ancientFarm.autoEat.desc", true);
   private final BooleanSetting autoFireRes = new BooleanSetting("module.ancientFarm.autoFireRes", "module.ancientFarm.autoFireRes.desc", true);
   private final SliderSetting tolerance = new SliderSetting("module.ancientFarm.tolerance", 12.0F, 3.0F, 40.0F, 1.0F, "module.ancientFarm.tolerance.desc", "°");
   private final BooleanSetting render = new BooleanSetting("module.ancientFarm.render", "module.ancientFarm.render.desc", true);
   private final BooleanSetting debug = new BooleanSetting("module.ancientFarm.debug", "module.ancientFarm.debug.desc", false, Zenith::method1502);
   private static final Predicate<ItemStack> predicate = var0 -> Util10.method2177(var0) && method4258(var0) > 0.05;
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Zenith-AncientFarm");
      var1.setDaemon(true);
      return var1;
   });
   private final Set<BlockPos> items = new LinkedHashSet<>();
   private final Set<BlockPos> items2 = new HashSet<>();
   private final ConcurrentLinkedQueue<BlockPos> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private final ConcurrentLinkedQueue<BlockPos> concurrentLinkedQueue2 = new ConcurrentLinkedQueue<>();
   private final Deque<AncientFarmModule.Entry> deque = new ArrayDeque<>();
   private final AncientFarmModule.Entry2 entry2 = new AncientFarmModule.Entry2();
   private final AncientFarmModule.Entry2 entry22 = new AncientFarmModule.Entry2();
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private final StopWatch stopWatch4 = new StopWatch();
   private final StopWatch stopWatch5 = new StopWatch();
   private AncientFarmModule.Type2 type2 = AncientFarmModule.Type2.SEARCH;
   private volatile Box box;
   private volatile Box box2;
   private volatile boolean flag;
   private int intValue;
   private int intValue2;
   private Vec3d vec3d;
   private Vec3d vec3d2;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6;
   private boolean flag7;
   private boolean flag8;
   private boolean flag9;
   private boolean flag10;
   private Vec3d vec3d3;
   private int stuckTicks;
   private Vec3d vec3d4;
   private int intValue3;

   private AncientFarmModule() {
   }

   public void onEnable() {
      super.onEnable();
      if (this.isEnabledRaw()) {
         if (mc.player != null && mc.world != null) {
            this.reset();
         } else {
            this.setToggled(false);
         }
      }
   }

   public void onDisable() {
      super.onDisable();
      this.method4238();
      this.flag3 = false;
      this.reset();
   }

   private void reset() {
      this.flag3 = false;
      this.method4238();
      this.type2 = AncientFarmModule.Type2.SEARCH;
      this.box = null;
      this.box2 = null;
      this.intValue = 0;
      this.intValue2 = 0;
      this.vec3d = null;
      this.vec3d2 = null;
      this.blockPos = null;
      this.blockPos2 = null;
      this.items.clear();
      this.items2.clear();
      this.concurrentLinkedQueue.clear();
      this.concurrentLinkedQueue2.clear();
      this.deque.clear();
      this.flag2 = false;
      this.entry2.method4263();
      this.vec3d3 = null;
      this.stuckTicks = 0;
      this.vec3d4 = null;
      this.intValue3 = 0;
      this.entry22.method4263();
      this.stopWatch2.reset();
   }

   @EventTarget
   public void method4220(Event5 var1) {
      if (mc.player != null && mc.world != null) {
         Vec3d var2 = this.vec3d != null ? this.vec3d : this.vec3d2;
         if (var2 != null) {
            Rotation var3 = RotationUtils.method6912(var2.subtract(mc.player.getEyePos()));
            float var4 = this.method4221();
            Rotation var5 = new Rotation(var3.getFloatValue() + var4 / 2.0F, MathHelper.clamp(var3.getFloatValue2() + var4 / 4.0F, -90.0F, 90.0F));
            rotationManager.method7849(
               new RotationRequest(var5, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig3(), var5), bcCameraTestnerv.getRotationConfig3()),
               7,
               this
            );
         }
      }
   }

   private float method4221() {
      float var1;
      return (float)(
         (
               Math.sin((var1 = mc.player.age + mc.getRenderTickCounter().getTickProgress(false)) * 0.31F) * 0.5
                  + Math.sin(var1 * 0.73F + 1.1F) * 0.3
                  + Math.sin(var1 * 1.7F + 2.6F) * 0.2
            )
            * 8.0
      );
   }

   private boolean method4222(Vec3d var1) {
      Rotation var2 = RotationUtils.method6912(var1.subtract(mc.player.getEyePos()));
      return rotationManager.method7844().method3134(var2, this.tolerance.getValue3());
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.blockPos = null;
         this.vec3d2 = null;
         this.method4230();
         this.method4231();
         this.method4233();
         this.method4232();
         AncientFarmModule.Type3 var2 = this.method4260();
         if (var2 == null || var2 == AncientFarmModule.Type3.TNT && this.type2 != AncientFarmModule.Type2.SEARCH) {
            this.method4239();
            this.method4238();
            this.flag3 = true;
            this.vec3d = null;
            if (mc.currentScreen == null && !this.method4240()) {
               switch (this.type2) {
                  case SEARCH:
                     this.method4223();
                     break;
                  case TNT:
                     this.method4224();
                     break;
                  case RETREAT:
                     this.method4227();
                     break;
                  case MINE:
                     this.method4228();
               }
            } else {
               this.method4237();
            }
         } else {
            if (this.stopWatch5.method7544(10000L)) {
               ChatLogger.method2019("AncientFarm: " + var2.description());
            }

            this.setToggled(false);
         }
      }
   }

   private void method4223() {
      if (this.items != null && this.items.getClass() == EmptySet.class ? false : !this.items.isEmpty()) {
         ChatLogger.method2019("AncientFarm: вскапываем обломки, найденные по пути");
         this.entry22.method4263();
         this.type2 = AncientFarmModule.Type2.MINE;
      } else if (this.box == null) {
         this.method4236();
         if (!this.flag && mc.player.age > 20) {
            this.flag = true;
            ClientWorld var7 = mc.world;
            this.executorService.execute(() -> {});
         }
      } else {
         BlockPos var1 = BlockPos.ofFloored(this.box.getCenter());
         BlockPos var2 = this.method4255(var1);
         Vec3d var3 = Vec3d.ofCenter(var2 != null ? var2 : var1);
         double var4;
         boolean var6 = (var4 = Math.sqrt(mc.player.getEntityPos().squaredDistanceTo(var3))) <= 25.0;
         if (!(var4 <= 5.0) && (!var6 || this.stuckTicks < 24)) {
            if (mc.player.age % 40 == 0) {
               this.method4259(
                  "к территории %.1f бл | полёт %s | земля %s | ввод F%s B%s L%s R%s | v=%.2f",
                  var4,
                  mc.player.getAbilities().flying ? "да" : "НЕТ",
                  mc.player.isOnGround() ? "да" : "нет",
                  this.flag4 ? "+" : "-",
                  this.flag5 ? "+" : "-",
                  this.flag6 ? "+" : "-",
                  this.flag7 ? "+" : "-",
                  mc.player.getVelocity().horizontalLength()
               );
            }

            this.method4235(var3);
         } else {
            this.stuckTicks = 0;
            this.vec3d3 = null;
            this.intValue = 0;
            this.method4259("прилетели к территории, дистанция %.1f", var4);
            this.type2 = AncientFarmModule.Type2.TNT;
         }
      }
   }

   private void method4224() {
      double var1 = mc.player.getBlockInteractionRange();
      List<TntEntity> var3 = this.method4250(10.0);
      boolean var4 = var3.stream().anyMatch(var0 -> var0.getFuse() <= 45);
      BlockPos var5 = BlockPos.streamOutwards(mc.player.getBlockPos(), (int)var1, (int)var1, (int)var1)
         .filter(var0 -> mc.world.getBlockState(var0).isOf(Blocks.TNT))
         .map(BlockPos::toImmutable)
         .findFirst()
         .orElse(null);
      if (var5 == null && (this.intValue >= this.tntPerRun.getValue3() || var4)) {
         if (var3.isEmpty()) {
            this.intValue = 0;
            this.box = null;
            this.type2 = AncientFarmModule.Type2.SEARCH;
         } else {
            this.type2 = AncientFarmModule.Type2.RETREAT;
         }
      } else if (var4 && var5 != null) {
         this.type2 = AncientFarmModule.Type2.RETREAT;
      } else if (var5 != null && this.intValue >= this.tntPerRun.getValue3()) {
         this.method4225(var5, var1);
      } else {
         this.method4226(var1);
      }
   }

   private void method4225(BlockPos var1, double var2) {
      this.blockPos = var1;
      Vec3d var4 = mc.player.getEyePos();
      Vec3d var5 = Vec3d.ofCenter(var1);
      Direction var6 = null;
      Vec3d var7 = null;
      double var8 = Double.MAX_VALUE;

      for (Direction var13 : Direction.values()) {
         Vec3d var14 = var5.add(var13.getOffsetX() * 0.45, var13.getOffsetY() * 0.45, var13.getOffsetZ() * 0.45);
         if (!(var4.distanceTo(var14) > var2)) {
            BlockHitResult var15 = mc.world.raycast(new RaycastContext(var4, var14, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
            double var16;
            if (var15.getType() == HitResult.Type.BLOCK && var15.getBlockPos().equals(var1) && (var16 = var4.squaredDistanceTo(var14)) < var8) {
               var8 = var16;
               var7 = var14;
               var6 = var13;
            }
         }
      }

      if (var7 == null) {
         this.method4235(this.method4254(var1));
      } else {
         this.method4238();
         this.vec3d = null;
         this.vec3d2 = var7;
         if (this.method4256(var0 -> var0.isOf(Items.FLINT_AND_STEEL))) {
            if (this.method4222(var7) && this.stopWatch3.method7544(250L)) {
               UseItemUtils.method6514(new BlockHitResult(var7, var6, var1, false), Hand.MAIN_HAND);
               mc.player.swingHand(Hand.MAIN_HAND);
            }
         }
      }
   }

   private void method4226(double var1) {
      Vec3d var3 = mc.player.getEyePos();
      BlockPos var4 = BlockPos.streamOutwards(mc.player.getBlockPos(), (int)var1, (int)var1, (int)var1)
         .filter(var0 -> mc.world.getBlockState(var0).isReplaceable())
         .filter(var1x -> this.method4251(var1x.down()))
         .filter(var0 -> !mc.player.getBoundingBox().stretch(mc.player.getVelocity()).expand(0.1).intersects(new Box(var0)))
         .filter(var4x -> this.method4252(var4x, var3, var1))
         .map(BlockPos::toImmutable)
         .min(Comparator.comparingDouble(var1x -> var3.squaredDistanceTo(Vec3d.ofCenter(var1x))))
         .orElse(null);
      if (var4 == null) {
         Vec3d var19 = mc.player.getEntityPos();
         BlockPos var20 = null;
         double var7 = Double.MAX_VALUE;

         for (BlockPos var10 : BlockPos.iterateOutwards(mc.player.getBlockPos(), 16, 12, 16)) {
            if (this.method4251(var10)) {
               double var11 = var10.getX() + 0.5 - var19.x;
               double var13 = var10.getY() + 0.5 - var19.y;
               double var15 = var10.getZ() + 0.5 - var19.z;
               double var17;
               if ((var17 = var11 * var11 + var13 * var13 + var15 * var15) < var7) {
                  var7 = var17;
                  var20 = var10.toImmutable();
               }
            }
         }

         if (var20 != null && this.stuckTicks < 24) {
            this.method4235(Vec3d.ofCenter(var20.up(2)));
         } else {
            this.box = null;
            this.intValue = 0;
            this.type2 = AncientFarmModule.Type2.SEARCH;
         }
      } else {
         this.blockPos = var4;
         BlockPos var5 = var4.down();
         Vec3d var6 = new Vec3d(var5.getX() + 0.5, var5.getY() + 1, var5.getZ() + 0.5);
         this.method4238();
         this.vec3d = null;
         this.vec3d2 = var6;
         if (this.method4256(var0 -> var0.isOf(Items.TNT))) {
            if (this.method4222(var6) && this.stopWatch3.method7544(250L)) {
               UseItemUtils.method6514(new BlockHitResult(var6, Direction.UP, var5, false), Hand.MAIN_HAND);
               mc.player.swingHand(Hand.MAIN_HAND);
               this.intValue++;
               this.intValue2++;
            }
         }
      }
   }

   private void method4227() {
      List<TntEntity> var1 = this.method4250(34.0);
      if (var1.isEmpty()) {
         this.method4259("заряд %d/%d отработал", this.intValue2, (int)this.blastsPerArea.getValue3());
         this.method4238();
         this.vec3d = null;
         this.intValue = 0;
         this.entry22.method4263();
         this.type2 = AncientFarmModule.Type2.MINE;
      } else {
         Vec3d var2 = Vec3d.ZERO;

         for (TntEntity var4 : var1) {
            var2 = var2.add(mc.player.getEntityPos().subtract(var4.getEntityPos()));
         }

         if (var2.lengthSquared() < 1.0E-4) {
            var2 = new Vec3d(1.0, 0.0, 0.0);
         }

         Vec3d var5 = var2.normalize();
         this.method4235(mc.player.getEntityPos().add(var5.x * 26.0, 6.0, var5.z * 26.0));
      }
   }

   private void method4228() {
      this.entry22.tick();
      if (!this.entry22.method4265(30L, 25L)) {
         this.method4236();
      } else {
         BlockPos var1 = this.items
            .stream()
            .min(Comparator.comparingDouble(var0 -> mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var0))))
            .orElse(null);
         if (var1 == null) {
            this.method4238();
            this.vec3d = null;
            this.blockPos2 = null;
            if (this.box != null && this.intValue2 < this.blastsPerArea.getValue3()) {
               this.intValue = 0;
               this.type2 = AncientFarmModule.Type2.TNT;
            } else {
               this.method4259("территория выработана за %d зарядов, ищем новую", this.intValue2);
               this.intValue2 = 0;
               this.box = null;
               this.type2 = AncientFarmModule.Type2.SEARCH;
            }
         } else {
            if (!var1.equals(this.blockPos2)) {
               this.blockPos2 = var1;
               this.stopWatch2.reset();
            } else if (this.stopWatch2.method7546(9000L)) {
               this.items2.add(var1);
               this.items.remove(var1);
               this.blockPos2 = null;
               return;
            }

            this.blockPos = var1;
            double var2 = mc.player.getBlockInteractionRange();
            BlockHitResult var4 = this.method4253(var1, var2);
            if (var4 == null) {
               this.method4235(this.method4254(var1));
            } else {
               this.method4238();
               this.vec3d = null;
               this.vec3d2 = var4.getPos();
               if (this.method4256(predicate)) {
                  if (this.method4222(this.vec3d2)) {
                     mc.interactionManager.updateBlockBreakingProgress(var1, var4.getSide());
                     mc.player.swingHand(Hand.MAIN_HAND);
                  }
               }
            }
         }
      }
   }

   private Box method4229(ClientWorld var1) {
      int var2 = (int)((Math.sqrt(var1.getChunkManager().getLoadedChunkCount()) - 1.0) / 2.0) * 16;
      BlockPos var3 = mc.player.getBlockPos();
      WorldBorder var4 = var1.getWorldBorder();
      boolean var5 = this.searchMode.method2101("module.ancientFarm.searchMode.top");
      double var6 = this.rangeWeight.getValue3() / 100.0;
      int var8 = var5 ? 90 : 20;
      int var9 = var5 ? 118 : 60;
      Box var10 = null;
      double var11 = -1.0;
      double var13 = 0.0;
      Mutable var15 = new Mutable();

      for (int var16 = var3.getX() - var2; var16 <= var3.getX() + var2; var16 += 8) {
         for (int var17 = var3.getZ() - var2; var17 <= var3.getZ() + var2; var17 += 8) {
            if (var1.isChunkLoaded(var16 >> 4, var17 >> 4) && var4.contains(var16 - 20, var17 - 20) && var4.contains(var16 + 20, var17 + 20)) {
               for (int var18 = var8; var18 <= var9; var18 += 8) {
                  int var19 = 0;
                  int var20 = 0;
                  boolean var21 = false;

                  for (byte var22 = -20; var22 <= 20 && !var21; var22 += 4) {
                     for (byte var23 = -20; var23 <= 20 && !var21; var23 += 4) {
                        for (byte var24 = -20; var24 <= 20; var24 += 4) {
                           var15.set(var16 + var22, var18 + var23, var17 + var24);
                           RegistryEntry var25 = var1.getBiome(var15);
                           if (var25.matchesKey(BiomeKeys.BASALT_DELTAS) || var25.matchesKey(BiomeKeys.WARPED_FOREST)) {
                              var21 = true;
                              break;
                           }

                           var20++;
                           BlockState var26 = var1.getBlockState(var15);
                           if (!var26.isAir() && var26.getFluidState().isEmpty()) {
                              var19++;
                           }
                        }
                     }
                  }

                  if (!var21 && var20 != 0) {
                     double var34 = (double)var19 / var20;
                     double var35 = Math.sqrt(var3.getSquaredDistance(var16, var18, var17));
                     double var36 = var2 > 0 ? Math.min(1.0, var35 / var2) * var6 : 0.0;
                     double var28;
                     if (!((var28 = var34 - var36) <= var11)) {
                        BlockPos var30 = BlockPos.streamOutwards(new BlockPos(var16, var18, var17), 20, 20, 20).filter(var1x -> {
                           BlockState var2x = var1.getBlockState(var1x);
                           return !var2x.isAir() && var2x.getFluidState().isEmpty();
                        }).map(BlockPos::toImmutable).findFirst().orElse(null);
                        if (var30 != null) {
                           var11 = var28;
                           var13 = var34;
                           var10 = new Box(var30.getX() - 20, var30.getY() - 20, var30.getZ() - 20, var30.getX() + 20, var30.getY() + 20, var30.getZ() + 20);
                        }
                     }
                  }
               }
            }
         }
      }

      if (var10 != null && !var10.equals(this.box2)) {
         this.box2 = var10;
         long var31 = Math.round(var13 * 100.0);
         long var32 = Math.round(Math.sqrt(var3.getSquaredDistance(var10.getCenter())));
         String var33 = "AncientFarm: нашли территорию, плотность " + var31 + "%, до неё " + var32 + " бл";
         mc.execute(() -> ChatLogger.method2019(var33));
      }

      return var10;
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         if (var1.getPacket3() instanceof ChunkDeltaUpdateS2CPacket var4) {
            var4.visitUpdates((var1x, var2x) -> {
               if (var2x.isOf(Blocks.ANCIENT_DEBRIS)) {
                  this.concurrentLinkedQueue.add(var1x.toImmutable());
               } else {
                  this.concurrentLinkedQueue2.add(var1x.toImmutable());
               }
            });
         } else {
            if (var1.getPacket3() instanceof BlockUpdateS2CPacket var2) {
               if (var2.getState().isOf(Blocks.ANCIENT_DEBRIS)) {
                  this.concurrentLinkedQueue.add(var2.getPos().toImmutable());
               } else {
                  this.concurrentLinkedQueue2.add(var2.getPos().toImmutable());
               }
            }
         }
      }
   }

   private void method4230() {
      BlockPos var1;
      while ((var1 = this.concurrentLinkedQueue2.poll()) != null) {
         this.items.remove(var1);
      }

      while ((var1 = this.concurrentLinkedQueue.poll()) != null) {
         if (!this.items2.contains(var1) && this.items.add(var1)) {
            this.flag2 = true;
            this.entry2.method4263();
         }
      }
   }

   private void method4231() {
      this.items
         .removeIf(
            var0 -> mc.world.getBlockState(var0).isAir()
               || var0.getSquaredDistance(mc.player.getEntityPos()) >= 6400.0
               || !mc.world.isChunkLoaded(var0.getX() >> 4, var0.getZ() >> 4)
         );
   }

   private void method4232() {
      this.entry2.tick();
      if (this.flag2 && this.entry2.method4264(5L)) {
         this.flag2 = false;
         ChatLogger.method2019("AncientFarm: обнаружено " + this.items.size() + " древних обломков");
         zenith.getHudHolder().method6972(this.getCategory().getIcon(), Text.of("AncientFarm: обломков — " + this.items.size()));
      }
   }

   private void method4233() {
      if (this.stopWatch.method7544(500L)) {
         int var1 = (int)this.scanRadius.getValue3();
         BlockPos var2 = this.box != null && this.type2 != AncientFarmModule.Type2.SEARCH ? BlockPos.ofFloored(this.box.getCenter()) : mc.player.getBlockPos();
         Mutable var3 = new Mutable();
         int var4 = Math.max(var2.getY() - var1, mc.world.getBottomY());
         int var5 = Math.min(var2.getY() + var1, mc.world.getTopYInclusive());

         for (int var6 = -var1; var6 <= var1; var6++) {
            for (int var7 = -var1; var7 <= var1; var7++) {
               int var8 = var2.getX() + var6;
               int var9 = var2.getZ() + var7;
               if (mc.world.isChunkLoaded(var8 >> 4, var9 >> 4)) {
                  WorldChunk var10 = mc.world.getChunk(var8 >> 4, var9 >> 4);
                  int var11 = -32768 << 16 | 0;

                  for (int var12 = var4; var12 <= var5; var12++) {
                     if (var12 > var11) {
                        var11 = (var12 & -16) + 15;
                        int var13;
                        ChunkSection var14 = (var13 = var10.getSectionIndex(var12)) >= 0 && var13 < var10.getSectionArray().length
                           ? var10.getSection(var13)
                           : null;
                        if (var14 != null && (var14.isEmpty() || !var14.hasAny(var0 -> var0.isOf(Blocks.ANCIENT_DEBRIS)))) {
                           var12 = var11;
                           continue;
                        }
                     }

                     var3.set(var8, var12, var9);
                     if (mc.world.getBlockState(var3).isOf(Blocks.ANCIENT_DEBRIS)) {
                        BlockPos var15 = var3.toImmutable();
                        if (!this.items2.contains(var15) && this.method4234(var15) && this.items.add(var15)) {
                           this.flag2 = true;
                           this.entry2.method4263();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean method4234(BlockPos var1) {
      for (Direction var5 : Direction.values()) {
         BlockPos var6 = var1.offset(var5);
         BlockState var7 = mc.world.getBlockState(var6);
         if (var7.isAir() || !var7.getFluidState().isEmpty() || !this.method4251(var6)) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      if (mc.player != null && this.flag3) {
         var1.setPlayerInput(new PlayerInput(this.flag4, this.flag5, this.flag6, this.flag7, this.flag8, this.flag9, this.flag10));
      }
   }

   private void method4235(Vec3d var1) {
      this.vec3d = var1;
      Vec3d var2 = this.vec3d4 != null ? this.vec3d4 : var1;
      this.flag3 = true;
      double var3 = var2.x - mc.player.getX();
      double var5 = var2.z - mc.player.getZ();
      double var7;
      if ((var7 = Math.sqrt(var3 * var3 + var5 * var5)) > 0.75) {
         float var10 = MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(var5, var3)) - 90.0F - mc.player.getYaw());
         this.flag4 = var10 > -67.5F && var10 < 67.5F;
         this.flag5 = var10 > 112.5F || var10 < -112.5F;
         this.flag6 = var10 < -22.5F && var10 > -157.5F;
         this.flag7 = var10 > 22.5F && var10 < 157.5F;
         this.flag10 = var7 > 6.0 && var10 > -30.0F && var10 < 30.0F;
      } else {
         this.flag4 = this.flag5 = this.flag6 = this.flag7 = this.flag10 = false;
      }

      double var9 = var2.y - mc.player.getY();
      this.flag8 = var9 > 0.4;
      this.flag9 = var9 < -0.4;
      this.method4237();
   }

   private void method4236() {
      this.vec3d = null;
      this.method4238();
      this.method4237();
   }

   private void method4237() {
      PlayerAbilities var1 = mc.player.getAbilities();
      if (var1.allowFlying) {
         if (mc.player.isOnGround()) {
            this.flag8 = true;
         } else {
            if (!var1.flying) {
               var1.flying = true;
               mc.player.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(var1));
            }
         }
      }
   }

   private void method4238() {
      this.flag4 = this.flag5 = this.flag6 = this.flag7 = this.flag8 = this.flag9 = this.flag10 = false;
   }

   private void method4239() {
      if (this.intValue3 > 0) {
         this.intValue3--;
         if (this.intValue3 == 0) {
            this.vec3d4 = null;
            this.stuckTicks = 0;
            this.vec3d3 = null;
         }
      } else if (this.vec3d == null) {
         this.stuckTicks = 0;
         this.vec3d3 = null;
      } else if (this.vec3d3 != null && !(mc.player.getEntityPos().squaredDistanceTo(this.vec3d3) > 1.0)) {
         this.stuckTicks++;
         if (this.stuckTicks >= 24) {
            this.vec3d4 = mc.player.getEntityPos().add(0.0, 10.0, 0.0);
            this.intValue3 = 45;
            this.vec3d3 = null;
         }
      } else {
         this.vec3d3 = mc.player.getEntityPos();
         this.stuckTicks = 0;
      }
   }

   private boolean method4240() {
      if (this.deque.isEmpty()) {
         if (this.autoEat.isEnabled() && this.method4243() && mc.player.canConsume(false)) {
            this.method4241(AncientFarmModule.Type.FOOD, AncientFarmModule::method4248);
         }

         if (this.deque.isEmpty() && this.autoFireRes.isEnabled() && this.method4247()) {
            this.method4241(AncientFarmModule.Type.FIRE_RESISTANCE, AncientFarmModule::method4249);
         }
      }

      if (this.deque.isEmpty()) {
         return false;
      } else if (mc.player.age <= 40) {
         return true;
      } else {
         this.method4244(this.deque.peek());
         return true;
      }
   }

   private void method4241(AncientFarmModule.Type var1, Predicate<ItemStack> var2) {
      int var3 = -1;

      for (int var4 = 0; var4 < 36; var4++) {
         if (var2.test(mc.player.getInventory().getStack(var4))) {
            var3 = var4;
            break;
         }
      }

      if (var3 != -1) {
         this.deque.add(new AncientFarmModule.Entry(var1, var3, mc.player.getInventory().getSelectedSlot()));
      }
   }

   private boolean method4242(AncientFarmModule.Type var1) {
      return switch (var1) {
         case FOOD -> this.method4243();
         case FIRE_RESISTANCE -> this.method4247();
      };
   }

   private boolean method4243() {
      return mc.player.getHungerManager().getFoodLevel() <= 17;
   }

   private void method4244(AncientFarmModule.Entry var1) {
      var1.ticks++;
      if (var1.ticks == 1) {
         if (var1.method4262()) {
            this.method4246(var1.intValue, var1.intValue2);
         } else {
            this.method4257(var1.intValue);
         }
      } else {
         if (!var1.flag) {
            if (mc.player.isUsingItem()) {
               var1.flag = true;
            } else {
               if (!this.method4242(var1.type)) {
                  this.method4245(var1);
                  return;
               }

               if (var1.ticks % 4 == 0) {
                  UseItemUtils.useItem(Hand.MAIN_HAND);
               }
            }
         } else if (!mc.player.isUsingItem()) {
            this.method4245(var1);
            return;
         }

         if (var1.ticks >= 80) {
            if (this.stopWatch4.method7544(15000L)) {
               ChatLogger.method2019("AncientFarm: применить предмет не удалось, пропускаем");
            }

            this.method4245(var1);
         }
      }
   }

   private void method4245(AncientFarmModule.Entry var1) {
      if (var1.method4262()) {
         this.method4246(var1.intValue, var1.intValue2);
      } else {
         this.method4257(var1.intValue2);
      }

      this.deque.poll();
   }

   private void method4246(int var1, int var2) {
      mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, var1, var2, SlotActionType.SWAP, mc.player);
      this.method4257(var2);
   }

   private boolean method4247() {
      return !mc.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) || mc.player.getStatusEffect(StatusEffects.FIRE_RESISTANCE).getDuration() <= 100;
   }

   private static boolean method4248(ItemStack var0) {
      return var0.contains(DataComponentTypes.FOOD) && var0.getUseAction() == UseAction.EAT;
   }

   private static boolean method4249(ItemStack var0) {
      return StreamSupport.stream(var0.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).getEffects().spliterator(), false)
         .anyMatch(var0x -> var0x.getEffectType() == StatusEffects.FIRE_RESISTANCE);
   }

   private List<TntEntity> method4250(double var1) {
      return mc.world.getEntitiesByClass(TntEntity.class, mc.player.getBoundingBox().expand(var1), var0 -> true);
   }

   private boolean method4251(BlockPos var1) {
      return mc.world.getBlockState(var1).isSolidBlock(mc.world, var1);
   }

   private boolean method4252(BlockPos var1, Vec3d var2, double var3) {
      Vec3d var5 = new Vec3d(var1.getX() + 0.5, var1.getY(), var1.getZ() + 0.5);
      if (!(var2.distanceTo(var5) > var3) && !(var2.subtract(var5).normalize().y <= 0.0)) {
         BlockHitResult var6 = mc.world.raycast(new RaycastContext(var2, var5, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
         return var6.getType() != HitResult.Type.BLOCK || var6.getBlockPos().equals(var1.down());
      } else {
         return false;
      }
   }

   private BlockHitResult method4253(BlockPos var1, double var2) {
      Vec3d var4 = mc.player.getEyePos();
      Vec3d var5 = Vec3d.ofCenter(var1);
      BlockHitResult var6 = null;
      double var7 = Double.MAX_VALUE;

      for (Direction var12 : Direction.values()) {
         Vec3d var13 = var5.add(var12.getOffsetX() * 0.48, var12.getOffsetY() * 0.48, var12.getOffsetZ() * 0.48);
         if (!(var4.distanceTo(var13) > var2)) {
            BlockHitResult var14 = mc.world.raycast(new RaycastContext(var4, var13, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
            double var15;
            if (var14.getType() == HitResult.Type.BLOCK && var14.getBlockPos().equals(var1) && (var15 = var4.squaredDistanceTo(var13)) < var7) {
               var7 = var15;
               var6 = new BlockHitResult(var13, var12, var1, false);
            }
         }
      }

      return var6;
   }

   private Vec3d method4254(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);
      Vec3d var3 = mc.player.getEntityPos().subtract(var2);
      if (var3.lengthSquared() < 1.0E-4) {
         var3 = new Vec3d(0.0, 1.0, 0.0);
      }

      return var2.add(var3.normalize().multiply(3.0)).add(0.0, 0.5, 0.0);
   }

   private BlockPos method4255(BlockPos var1) {
      Mutable var2 = new Mutable();

      for (BlockPos var4 : BlockPos.iterateOutwards(var1, 14, 14, 14)) {
         if (!this.method4251(var4)) {
            var2.set(var4.getX(), var4.getY() + 1, var4.getZ());
            if (!this.method4251(var2) && mc.world.getBlockState(var4).getFluidState().isEmpty()) {
               return var4.toImmutable();
            }
         }
      }

      return null;
   }

   private boolean method4256(Predicate<ItemStack> var1) {
      if (var1.test(mc.player.getMainHandStack())) {
         return true;
      } else {
         int var2 = -1;

         for (int var3 = 0; var3 < 36; var3++) {
            if (var1.test(mc.player.getInventory().getStack(var3))) {
               var2 = var3;
               break;
            }
         }

         if (var2 < 0) {
            return false;
         } else if (var2 < 9) {
            this.method4257(var2);
            return var1.test(mc.player.getMainHandStack());
         } else {
            if (this.stopWatch3.method7544(250L)) {
               mc.interactionManager
                  .clickSlot(mc.player.playerScreenHandler.syncId, var2, mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, mc.player);
            }

            return false;
         }
      }
   }

   private void method4257(int var1) {
      if (var1 >= 0 && var1 <= 8 && mc.player.getInventory().getSelectedSlot() != var1) {
         mc.player.getInventory().setSelectedSlot(var1);
         mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }
   }

   private static double method4258(ItemStack var0) {
      return var0.isDamageable() && var0.getMaxDamage() > 0 ? 1.0 - (double)var0.getDamage() / var0.getMaxDamage() : 1.0;
   }

   private void method4259(String var1, Object... var2) {
      if (Zenith.method1502() && this.debug.isEnabled()) {
         ChatLogger.method2019("AncientFarm: " + String.format(var1, var2));
      }
   }

   private AncientFarmModule.Type3 method4260() {
      for (AncientFarmModule.Type3 var4 : AncientFarmModule.Type3.values()) {
         if (!var4.method4267()) {
            return var4;
         }
      }

      return null;
   }

   @EventTarget
   public void method4261(WorldRenderEvent var1) {
      if (this.render.isEnabled() && mc.player != null && mc.world != null) {
         for (BlockPos var3 : List.copyOf(this.items)) {
            GlowUtil.method2230(new Box(var3), new Color(0, 190, 255).getRGB(), 1.0F);
         }

         if (this.blockPos != null) {
            GlowUtil.method2230(new Box(this.blockPos), new Color(230, 90, 70).getRGB(), 1.5F);
         }
      }
   }

   private static final class Entry {
      private final AncientFarmModule.Type type;
      private final int intValue;
      private final int intValue2;
      private boolean flag;
      private int ticks;

      private Entry(AncientFarmModule.Type var1, int var2, int var3) {
         this.type = var1;
         this.intValue = var2;
         this.intValue2 = var3;
      }

      private boolean method4262() {
         return this.intValue > 8;
      }
   }

   private static final class Entry2 {
      private long longValue;
      private long longValue2;

      private Entry2() {
         this.method4263();
      }

      private void method4263() {
         this.longValue = 0L;
         this.longValue2 = (long)(Math.random() * 9.223372E18F);
      }

      private void tick() {
         this.longValue++;
      }

      private boolean method4264(long var1) {
         return this.longValue >= var1;
      }

      private boolean method4265(long var1, long var3) {
         return this.longValue >= var1 + this.longValue2 % (var3 + 1L);
      }
   }

   private static enum Type {
      FOOD,
      FIRE_RESISTANCE;
   }

   private static enum Type2 {
      SEARCH,
      TNT,
      RETREAT,
      MINE;
   }

   private static enum Type3 implements MinecraftAccess {
      FLY("нужен режим полёта (/fly)", () -> mc.player.getAbilities().allowFlying),
      NETHER("работает только в Незере", () -> mc.world.getRegistryKey() == World.NETHER),
      FOOD(
         "в хотбаре должна быть еда",
         () -> !AncientFarmModule.INSTANCE.autoEat.isEnabled() || !AncientFarmModule.INSTANCE.method4243() || method4266(AncientFarmModule::method4248)
      ),
      TNT("в хотбаре должен быть динамит", var0 -> var0.isOf(Items.TNT)),
      FLINT_AND_STEEL("в хотбаре должно быть огниво", var0 -> var0.isOf(Items.FLINT_AND_STEEL)),
      PICKAXE("в хотбаре должна быть кирка с прочностью больше 5%", AncientFarmModule.predicate),
      FIRE_RESISTANCE(
         "в хотбаре должно быть зелье огнестойкости",
         () -> !AncientFarmModule.INSTANCE.autoFireRes.isEnabled() || !AncientFarmModule.INSTANCE.method4247() || method4266(AncientFarmModule::method4249)
      );

      private final String text;
      private final BooleanSupplier booleanSupplier;

      private Type3(String var3, BooleanSupplier var4) {
         this.text = var3;
         this.booleanSupplier = var4;
      }

      private Type3(String var3, Predicate<ItemStack> var4) {
         this(var3, () -> method4266(var4));
      }

      private static boolean method4266(Predicate<ItemStack> var0) {
         return IntStream.range(0, 9).anyMatch(var1 -> var0.test(mc.player.getInventory().getStack(var1)));
      }

      public String description() {
         return this.text;
      }

      public boolean method4267() {
         return this.booleanSupplier.getAsBoolean();
      }
   }

   private void l1I1IlllIlI(net.minecraft.client.world.ClientWorld var1) {
      net.minecraft.util.math.Box var2 = null;

      try {
         var2 = this.method4229(var1);
      } finally {
         if (mc.world == var1) {
            this.box = var2;
         }

         this.flag = false;
      }
   }
}

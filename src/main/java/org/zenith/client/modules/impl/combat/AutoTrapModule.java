package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptyMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.Class33;
import org.zenith.utility.network.Island;
import org.zenith.utility.network.PlacementQueue;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "AutoTrap",
   description = "module.autotrap.description",
   category = ModuleCategory.COMBAT
)
public final class AutoTrapModule extends Module {
   public static final AutoTrapModule INSTANCE = new AutoTrapModule();
   private static final double[] CONST_doubleValues = new double[]{0.5, 0.3, 0.7, 0.15, 0.85};
   private static final double[] CONST_doubleValues2 = new double[]{0.5, 0.25, 0.75};
   private static final double CONST_doubleValue = 0.03;
   private static final double CONST_doubleValue2 = 3.0;
   private static final int CONST_intValue = 4;
   private static final int CONST_intValue2 = 20;
   private static final long CONST_longValue = 350L;
   private static final long CONST_longValue2 = 2000L;
   private final ModeSetting activation = new ModeSetting(
      "module.autoTrap.activation", "module.autoTrap.activation.desc", "module.autoTrap.activationAlways", "module.autoTrap.activationBind"
   );
   private final BindSetting bind = new BindSetting("module.autoTrap.bind", "module.autoTrap.bind.desc", -1, () -> this.activation.isSelected(1));
   private final MultiSelectSetting traps = new MultiSelectSetting("module.autoTrap.traps", "module.autoTrap.traps.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapWeb", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapSnow", false);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapLava", false);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapFire", false);
   private final MultiSelectSetting.Entry entry5 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapAnvil", false);
   private final ModeSetting layout = new ModeSetting(
      "module.autoTrap.layout",
      "module.autoTrap.layout.desc",
      () -> this.entry.isEnabled() || this.entry2.isEnabled(),
      "module.autoTrap.layoutFeet",
      "module.autoTrap.layoutFull"
   );
   private final BooleanSetting lavaPickup = new BooleanSetting("module.autoTrap.lavaPickup", "module.autoTrap.lavaPickup.desc", true, this.entry3::isEnabled);
   private final SliderSetting lavaPickupDelay = new SliderSetting(
      "module.autoTrap.lavaPickupDelay",
      20.0F,
      2.0F,
      100.0F,
      1.0F,
      "module.autoTrap.lavaPickupDelay.desc",
      "t",
      () -> this.entry3.isEnabled() && this.lavaPickup.isEnabled()
   );
   private final ModeSetting predict = new ModeSetting(
      "module.autoTrap.predict",
      "module.autoTrap.predict.desc",
      "module.autoTrap.predictTrajectory",
      "module.autoTrap.predictZone",
      "module.autoTrap.predictLook",
      "module.autoTrap.predictOff"
   );
   private final SliderSetting predictTicks = new SliderSetting(
      "module.autoTrap.predictTicks", 2.0F, 0.0F, 10.0F, 1.0F, "module.autoTrap.predictTicks.desc", "t", () -> !this.predict.isSelected(3)
   );
   private final SliderSetting zoneSpread = new SliderSetting(
      "module.autoTrap.zoneSpread", 40.0F, 10.0F, 90.0F, 5.0F, "module.autoTrap.zoneSpread.desc", "°", () -> this.predict.isSelected(1)
   );
   private final SliderSetting range = new SliderSetting("module.autoTrap.range", 4.5F, 2.0F, 6.0F, 0.1F, "module.autoTrap.range.desc", "b");
   private final SliderSetting delay = new SliderSetting("module.autoTrap.delay", 0.0F, 0.0F, 10.0F, 1.0F, "module.autoTrap.delay.desc", "t");
   private final ModeSetting swap = new ModeSetting(
      "module.autoTrap.swap", "module.autoTrap.swap.desc", "module.autoTrap.swapHotbar", "module.autoTrap.swapInventory"
   );
   private final BooleanSetting protectSelf = new BooleanSetting("module.autoTrap.protectSelf", "module.autoTrap.protectSelf.desc", true);
   private final BooleanSetting pauseOnUse = new BooleanSetting("module.autoTrap.pauseOnUse", "module.autoTrap.pauseOnUse.desc", true);
   private final BooleanSetting hideSwing = new BooleanSetting("module.autoTrap.hideSwing", "module.autoTrap.hideSwing.desc", false);
   private final BooleanSetting render = new BooleanSetting("module.autoTrap.render", "module.autoTrap.render.desc", true);
   private final Map<BlockPos, AutoTrapModule.Data> items = new HashMap<>();
   private final Map<BlockPos, Integer> items2 = new HashMap<>();
   private final Queue<AutoTrapModule.Data3> queue = new ConcurrentLinkedQueue<>();
   private volatile boolean flag;
   private final ArrayDeque<Vec3d> arrayDeque = new ArrayDeque<>();
   private final List<AutoTrapModule.Data5> items3 = new ArrayList<>();
   private final PlacementQueue placementQueue = new PlacementQueue();
   private final Class33 class33 = new Class33();
   private PlayerEntity playerEntity;
   private List<BlockPos> items4 = List.of();
   private List<Box> items5 = List.of();
   private List<AutoTrapModule.Data6> items6 = List.of();
   private AutoTrapModule.Data2 data2;
   private BlockPos blockPos;
   private Rotation rotation;
   private int intValue = -32768 << 16 | 0;
   private int intValue2 = -32768 << 16 | 0;
   private int intValue3 = -16384 << 16 | 0;
   private int intValue4 = -16384 << 16 | 0;
   private int intValue5 = -32768 << 16 | 0;
   private long longValue;

   private AutoTrapModule() {
   }

   public boolean method2427() {
      return this.rotation != null || this.data2 != null;
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   @EventTarget
   public void method2428(Event5 var1) {
      if (!UseItemUtils.method6526() && mc.interactionManager != null) {
         int var2 = mc.player.age;
         if (this.intValue != (-32768 << 16 | 0) && var2 < this.intValue) {
            this.reset();
         }

         if (this.intValue != var2) {
            this.intValue = var2;
            this.method2431(var2);
         }

         Rotation var3 = this.rotation;
         if (var3 != null) {
            rotationManager.method7849(
               new RotationRequest(var3, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var3), bcCameraTestnerv.getRotationConfig()),
               10,
               this
            );
         }
      }
   }

   @EventTarget
   public void method2429(Event9 var1) {
      AutoTrapModule.Data2 var2 = this.data2;
      if (var2 != null && mc.player != null && mc.interactionManager != null) {
         int var3 = mc.player.age;
         if (this.intValue2 != var3) {
            this.intValue2 = var3;
            this.data2 = null;
            if (this.intValue5 != var3 && this.method2455(var2.getData6())) {
               AutoTrapModule.Type var4 = var2.getData6().getType();
               BlockPos var5 = var2.getData6().pos();
               this.flag = true;
               if (this.method2459(var2, var3)) {
                  if (var4 == AutoTrapModule.Type.PICKUP) {
                     this.items2.remove(var5);
                  }

                  if (var4 == AutoTrapModule.Type.ANVIL) {
                     this.intValue4 = var3;
                  }

                  this.items.put(var5, new AutoTrapModule.Data(var4, var3 + this.method2446()));
                  this.intValue3 = var3;
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         this.placementQueue.method2815(var1.getPacket3());
         if (this.flag) {
            if (var1.getPacket3() instanceof BlockUpdateS2CPacket var2) {
               this.queue.add(new AutoTrapModule.Data3(var2.getPos().toImmutable(), var2.getState()));
            } else if (var1.getPacket3() instanceof ChunkDeltaUpdateS2CPacket var3) {
               var3.visitUpdates((var1x, var2x) -> this.queue.add(new AutoTrapModule.Data3(var1x.toImmutable(), var2x)));
            }
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      this.class33.onMoveInput(var1);
      if (this.rotation != null && mc.player != null) {
         MovementInputUtils.method7426(var1, rotationManager.method7844().getFloatValue(), mc.player.getYaw());
      }
   }

   @EventTarget
   public void method2430(WorldRenderEvent var1) {
      if (this.render.isEnabled() && mc.player != null) {
         int var2 = zenith.getStyleManager().getClientColor(0).method7287();
         if (this.playerEntity != null) {
            for (Box var4 : this.items5) {
               Vec3d var5 = new Vec3d((var4.minX + var4.maxX) * 0.5, var4.minY + 0.05, (var4.minZ + var4.maxZ) * 0.5);
               GlowUtil.method2230(new Box(var5.subtract(0.05, 0.05, 0.05), var5.add(0.05, 0.05, 0.05)), ColorUtils.method6439(var2, 0.6F), 1.0F);
            }
         }

         for (BlockPos var10 : this.items.keySet()) {
            GlowUtil.method2230(method2475(var10, 0.9), ColorUtils.method6439(var2, 0.45F), 1.0F);
         }

         if (this.blockPos != null) {
            GlowUtil.method2230(method2475(this.blockPos, 0.96), var2, 1.5F);
         }

         long var9 = System.currentTimeMillis();
         var var11 = this.items3.iterator();

         while (var11.hasNext()) {
            AutoTrapModule.Data5 var6 = (AutoTrapModule.Data5)var11.next();
            float var7;
            if ((var7 = (float)(var9 - var6.time()) / 350.0F) >= 1.0F) {
               var11.remove();
            } else {
               GlowUtil.method2230(method2475(var6.pos(), 0.5 + 0.5 * var7), ColorUtils.method6439(var2, 1.0F - var7), 1.5F);
            }
         }
      } else {
         this.items3.clear();
      }
   }

   private void method2431(int var1) {
      this.data2 = null;
      this.blockPos = null;
      this.rotation = null;
      this.method2469(var1);
      this.placementQueue.method2816(var1);
      this.method2470(var1);
      this.flag = this.items != null && this.items.getClass() == EmptyMap.class ? false : !this.items.isEmpty();
      this.items2.keySet().removeIf(var1x -> !this.method2458(var1x));
      if (!this.method2464()) {
         this.class33.cancel();
         this.method2472();
         this.items6 = List.of();
      } else {
         if (this.class33.method5304()) {
            this.intValue5 = var1;
         }

         PlayerEntity var2 = this.method2465() ? this.method2466() : null;
         if (var2 != this.playerEntity) {
            this.arrayDeque.clear();
         }

         this.playerEntity = var2;
         if (this.playerEntity == null) {
            this.method2472();
         } else {
            this.arrayDeque.addLast(this.playerEntity.getEntityPos());

            while (this.arrayDeque.size() > 4) {
               this.arrayDeque.removeFirst();
            }

            this.method2437();
         }

         this.items6 = this.method2432(var1);
         this.method2436();
         if ((this.items6 != null && this.items6.getClass() == EmptyList.class ? false : !this.items6.isEmpty())
            && !(var1 - this.intValue3 <= this.delay.getValue3())) {
            this.data2 = this.method2451();
            if (this.data2 != null && this.data2.getData6().getType().flag2) {
               this.blockPos = this.data2.getData6().pos();
               this.rotation = rotationManager.getRotation2();
            } else if (this.data2 == null || !this.data2.getData6().getType().flag || !this.layout.isSelected(0)) {
               BlockPos var3 = this.data2 == null ? null : this.data2.getData6().pos();

               for (AutoTrapModule.Data6 var5 : this.items6) {
                  if (!var5.pos().equals(var3)) {
                     AutoTrapModule.Data4 var6 = this.method2448(var5);
                     if (var6 != null) {
                        this.blockPos = var5.pos();
                        this.rotation = var6.rotation();
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private List<AutoTrapModule.Data6> method2432(int var1) {
      ArrayList<AutoTrapModule.Data6> var2 = new ArrayList<>();
      if (this.entry3.isEnabled() && this.lavaPickup.isEnabled() && this.method2460(AutoTrapModule.Type.PICKUP)) {
         int var3 = Math.round(this.lavaPickupDelay.getValue3());

         for (Entry var5 : this.items2.entrySet()) {
            if (this.playerEntity == null || var1 - (Integer)var5.getValue() >= var3) {
               var2.add(new AutoTrapModule.Data6(AutoTrapModule.Type.PICKUP, (BlockPos)var5.getKey()));
            }
         }
      }

      if (this.playerEntity == null) {
         return var2;
      } else {
         AutoTrapModule.Type var12 = this.method2433();
         if (var12 != null) {
            for (BlockPos var15 : this.items4) {
               var2.add(new AutoTrapModule.Data6(var12, var15));
            }
         }

         Box var14 = this.playerEntity.getBoundingBox();
         if ((var12 != null || this.entry.isEnabled() || this.entry2.isEnabled()) && !this.method2440(var14)) {
            return var2;
         } else {
            List<BlockPos> var6 = this.method2441(var14);
            boolean var7 = this.playerEntity.isFireImmune() || this.playerEntity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
            boolean var8 = this.playerEntity.isInLava() || this.method2435(var14);
            if (this.entry3.isEnabled() && !var7 && !var8 && this.method2460(AutoTrapModule.Type.LAVA)) {
               for (Object var10_o : (Iterable<?>)(Object)(var6)) {
                  BlockPos var10 = (BlockPos)var10_o;
                  var2.add(new AutoTrapModule.Data6(AutoTrapModule.Type.LAVA, var10));
               }

               for (Object var19_o : (Iterable<?>)(Object)(var6)) {
                  BlockPos var19 = (BlockPos)var19_o;
                  var2.add(new AutoTrapModule.Data6(AutoTrapModule.Type.LAVA, var19.up()));
               }
            }

            if (this.entry4.isEnabled()
               && !var7
               && !var8
               && !this.playerEntity.isOnFire()
               && !this.playerEntity.isTouchingWater()
               && this.method2460(AutoTrapModule.Type.FIRE)) {
               for (Object var20_o : (Iterable<?>)(Object)(var6)) {
                  BlockPos var20 = (BlockPos)var20_o;
                  var2.add(new AutoTrapModule.Data6(AutoTrapModule.Type.FIRE, var20));
               }
            }

            if (this.entry5.isEnabled()
               && var1 - this.intValue4 >= 20
               && this.method2460(AutoTrapModule.Type.ANVIL)
               && (!var6.isEmpty())) {
               BlockPos var18 = (BlockPos)var6.getFirst();

               for (int var21 = 6; var21 >= 3; var21--) {
                  BlockPos var11 = var18.up(var21);
                  if (this.method2434(var18.up(), var11)) {
                     var2.add(new AutoTrapModule.Data6(AutoTrapModule.Type.ANVIL, var11));
                  }
               }
            }

            return var2;
         }
      }
   }

   private AutoTrapModule.Type method2433() {
      if (this.entry.isEnabled() && this.method2460(AutoTrapModule.Type.WEB)) {
         return AutoTrapModule.Type.WEB;
      } else {
         return this.entry2.isEnabled() && this.method2460(AutoTrapModule.Type.SNOW) ? AutoTrapModule.Type.SNOW : null;
      }
   }

   private boolean method2434(BlockPos var1, BlockPos var2) {
      for (int var3 = var1.getY(); var3 < var2.getY(); var3++) {
         if (!FallingBlock.canFallThrough(mc.world.getBlockState(new BlockPos(var1.getX(), var3, var1.getZ())))) {
            return false;
         }
      }

      return true;
   }

   private boolean method2435(Box var1) {
      Box var2 = var1.expand(1.0);

      for (BlockPos var4 : this.items2.keySet()) {
         if (var2.intersects(new Box(var4))) {
            return true;
         }
      }

      for (Entry var6 : this.items.entrySet()) {
         if (((AutoTrapModule.Data)var6.getValue()).getType() == AutoTrapModule.Type.LAVA && var2.intersects(new Box((BlockPos)var6.getKey()))) {
            return true;
         }
      }

      return false;
   }

   private void method2436() {
      if (this.swap.isSelected(1)
         && !this.class33.pending()
         && (this.playerEntity != null || (this.items2 != null && this.items2.getClass() == EmptyMap.class ? false : !this.items2.isEmpty()))) {
         ArrayList<AutoTrapModule.Type> var1 = new ArrayList();
         if (this.playerEntity != null) {
            if (this.entry.isEnabled()) {
               var1.add(AutoTrapModule.Type.WEB);
            } else if (this.entry2.isEnabled()) {
               var1.add(AutoTrapModule.Type.SNOW);
            }

            if (this.entry3.isEnabled()) {
               var1.add(AutoTrapModule.Type.LAVA);
            }

            if (this.entry4.isEnabled()) {
               var1.add(AutoTrapModule.Type.FIRE);
            }

            if (this.entry5.isEnabled()) {
               var1.add(AutoTrapModule.Type.ANVIL);
            }
         }

         if ((this.items2 != null && this.items2.getClass() == EmptyMap.class ? false : !this.items2.isEmpty()) && this.lavaPickup.isEnabled()) {
            var1.add(AutoTrapModule.Type.PICKUP);
         }

         for (Object var3_o : (Iterable<?>)(Object)(var1)) {
            AutoTrapModule.Type var3 = (AutoTrapModule.Type)var3_o;
            if (!this.method2460(var3) && this.class33.method5302(var1x -> method2462(var3, var1x))) {
               this.class33.method5303(var1x -> method2462(var3, var1x));
               return;
            }
         }
      }
   }

   private void method2437() {
      ArrayList<List> var1 = new ArrayList();
      Vec3d var2 = this.method2444();
      int var3 = this.method2445();
      if (this.predict.isSelected(3) || var3 == 0) {
         var1.add(List.of(this.playerEntity.getBoundingBox()));
      } else if (this.predict.isSelected(2)) {
         double var4 = method2473(var2);
         Vec3d var6 = Vec3d.fromPolar(0.0F, this.playerEntity.getHeadYaw()).multiply(var4);
         var1.add(this.method2443(new Vec3d(var6.x, var2.y, var6.z), var3));
      } else if (this.predict.isSelected(1)) {
         float var9 = this.zoneSpread.getValue3();

         for (float var8 : new float[]{0.0F, -var9 * 0.5F, var9 * 0.5F, -var9, var9}) {
            var1.add(this.method2443(method2474(var2, var8), var3));
            if (method2473(var2) < 0.03) {
               break;
            }
         }
      } else {
         var1.add(this.method2443(var2, var3));
      }

      this.items5 = (List<Box>)var1.getFirst();
      Box var10 = this.playerEntity.getBoundingBox();
      Box var11 = this.items5.getLast();
      if (!this.method2439(var10) && !this.method2439(var11)) {
         LinkedHashSet var13 = new LinkedHashSet();

         for (Object var16_o : (Iterable<?>)(Object)(var1)) {
            List var16 = (List)var16_o;
            this.method2438(var13, (Box)var16.getLast());
         }

         for (int var15 = this.items5.size() - 2; var15 >= 0; var15--) {
            this.method2438(var13, this.items5.get(var15));
         }

         this.items4 = new ArrayList<>(var13);
      } else {
         this.items4 = List.of();
      }
   }

   private void method2438(Set<BlockPos> var1, Box var2) {
      List<BlockPos> var3 = this.method2441(var2);
      boolean var4 = this.layout.isSelected(1);
      boolean var5 = var3.stream().anyMatch(this::method2457);
      boolean var6 = !var4 || var3.stream().map(BlockPos::up).anyMatch(this::method2457);
      if (!var5) {
         var1.addAll(var3);
      }

      if (!var6) {
         for (Object var8_o : (Iterable<?>)(Object)(var3)) {
            BlockPos var8 = (BlockPos)var8_o;
            var1.add(var8.up());
         }
      }
   }

   private boolean method2439(Box var1) {
      var var2 = this.method2441(var1);
      boolean var3 = var2.stream().anyMatch(this::method2457);
      return !this.layout.isSelected(1) ? var3 : var3 && var2.stream().map(BlockPos::up).anyMatch(this::method2457);
   }

   private boolean method2440(Box var1) {
      return this.method2441(var1).stream().anyMatch(this::method2457);
   }

   private List<BlockPos> method2441(Box var1) {
      int var2 = MathHelper.floor(var1.minY + 0.001);
      int var3 = MathHelper.floor(var1.minX + 0.001);
      int var4 = MathHelper.floor(var1.maxX - 0.001);
      int var5 = MathHelper.floor(var1.minZ + 0.001);
      int var6 = MathHelper.floor(var1.maxZ - 0.001);
      double var7 = (var1.minX + var1.maxX) * 0.5;
      double var9 = (var1.minZ + var1.maxZ) * 0.5;
      ArrayList var11 = new ArrayList(4);

      for (int var12 = var3; var12 <= var4; var12++) {
         for (int var13 = var5; var13 <= var6; var13++) {
            BlockPos var14 = this.method2442(new BlockPos(var12, var2, var13));
            if (!var11.contains(var14)) {
               var11.add(var14);
            }
         }
      }

      var11.sort(Comparator.comparingDouble((net.minecraft.util.math.BlockPos var4x) -> {
         double var5x = var4x.getX() + 0.5 - var7;
         double var7x = var4x.getZ() + 0.5 - var9;
         return var5x * var5x + var7x * var7x;
      }));
      return var11;
   }

   private BlockPos method2442(BlockPos var1) {
      BlockState var2 = mc.world.getBlockState(var1);
      if (!var2.isReplaceable() && !var2.isOf(Blocks.COBWEB) && !var2.isOf(Blocks.POWDER_SNOW)) {
         VoxelShape var3 = var2.getCollisionShape(mc.world, var1);
         if (!var3.isEmpty() && var3.getMax(Axis.Y) < 1.0) {
            return var1.up();
         }
      }

      return var1;
   }

   private List<Box> method2443(Vec3d var1, int var2) {
      List<Box> var3 = new ArrayList<>(var2 + 1);
      Box var4 = this.playerEntity.getBoundingBox();
      var3.add(var4);
      double var5 = var1.x;
      double var7 = this.playerEntity.isOnGround() && var1.y <= 0.0 ? -0.0784 : var1.y;
      double var9 = var1.z;

      for (int var11 = 0; var11 < var2; var11++) {
         Vec3d var12 = new Vec3d(var5, var7, var9);
         Vec3d var13 = Entity.adjustMovementForCollisions(this.playerEntity, var12, var4, mc.world, List.of());
         boolean var14 = var7 < 0.0 && var13.y > var7 + 1.0E-4;
         boolean var15 = var7 > 0.0 && var13.y < var7 - 1.0E-4;
         if (Math.abs(var13.x - var5) > 1.0E-4) {
            var5 = 0.0;
         }

         if (Math.abs(var13.z - var9) > 1.0E-4) {
            var9 = 0.0;
         }

         var4 = var4.offset(var13);
         var3.add(var4);
         var7 = var14 ? -0.0784 : ((var15 ? 0.0 : var7) - 0.08) * 0.98;
      }

      return var3;
   }

   private Vec3d method2444() {
      if (this.arrayDeque.size() < 2) {
         return Vec3d.ZERO;
      } else {
         Vec3d[] var1 = this.arrayDeque.toArray(new Vec3d[0]);
         double var2 = 0.0;
         double var4 = 0.0;
         double var6 = 0.0;

         for (int var8 = 1; var8 < var1.length; var8++) {
            double var9 = var8;
            var2 += (var1[var8].x - var1[var8 - 1].x) * var9;
            var4 += (var1[var8].z - var1[var8 - 1].z) * var9;
            var6 += var9;
         }

         var2 /= var6;
         var4 /= var6;
         double var13 = var1[var1.length - 1].y - var1[var1.length - 2].y;
         if (Math.hypot(var2, var4) < 0.03) {
            var2 = 0.0;
            var4 = 0.0;
         }

         return new Vec3d(var2, var13, var4);
      }
   }

   private int method2445() {
      return this.predict.isSelected(3) ? 0 : MathHelper.clamp(Math.round(this.predictTicks.getValue3()) + Math.round(this.method2447() / 50.0F), 0, 15);
   }

   private int method2446() {
      return MathHelper.clamp((int)Math.ceil(this.method2447() / 50.0) + 4, 4, 24);
   }

   private int method2447() {
      if (mc.getNetworkHandler() == null) {
         return 0;
      } else {
         PlayerListEntry var1 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
         return var1 == null ? 0 : Math.max(0, var1.getLatency());
      }
   }

   private AutoTrapModule.Data4 method2448(AutoTrapModule.Data6 var1) {
      if (this.method2460(var1.getType()) && this.method2455(var1)) {
         if (var1.getType() == AutoTrapModule.Type.PICKUP) {
            return this.method2449(var1.pos());
         } else {
            BlockPos var2 = var1.pos();
            Vec3d var3 = mc.player.getEyePos();
            double var4 = this.method2468();
            Rotation var6 = rotationManager.getRotation2();
            AutoTrapModule.Data4 var7 = null;
            double var8 = Double.MAX_VALUE;

            for (Direction var13 : Direction.values()) {
               BlockPos var14 = var2.offset(var13);
               BlockState var15 = mc.world.getBlockState(var14);
               if (this.method2450(var1.getType(), var14, var15)) {
                  VoxelShape var16 = var15.getOutlineShape(mc.world, var14);
                  if (!var16.isEmpty()) {
                     Direction var17 = var13.getOpposite();

                     for (Box var19 : var16.getBoundingBoxes()) {
                        Box var20 = var19.offset(var14);

                        for (double var24 : CONST_doubleValues) {
                           for (double var29 : CONST_doubleValues) {
                              Vec3d var31 = this.method2454(var20, var17, var24, var29);
                              if (!(var3.squaredDistanceTo(var31) > var4 * var4)) {
                                 Rotation var32 = RotationUtils.method6913(var31);
                                 BlockHitResult var33 = this.method2453(var3, var32, var4, FluidHandling.NONE);
                                 double var34;
                                 if (var33.getType() == HitResult.Type.BLOCK
                                    && !var33.isInsideBlock()
                                    && var33.getBlockPos().equals(var14)
                                    && var33.getSide() == var17
                                    && (
                                          var34 = var6.method3131(var32).method5327()
                                             + var3.distanceTo(var31) * 2.0
                                             + (var13 == Direction.DOWN ? 0 : 3)
                                             + (Math.abs(var24 - 0.5) + Math.abs(var29 - 0.5)) * 6.0
                                       )
                                       < var8) {
                                    var8 = var34;
                                    var7 = new AutoTrapModule.Data4(var32);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return var7;
         }
      } else {
         return null;
      }
   }

   private AutoTrapModule.Data4 method2449(BlockPos var1) {
      Vec3d var2 = mc.player.getEyePos();
      double var3 = this.method2468();
      double var5 = mc.player.getBlockInteractionRange();
      Rotation var7 = rotationManager.getRotation2();
      AutoTrapModule.Data4 var8 = null;
      double var9 = Double.MAX_VALUE;

      for (double var14 : CONST_doubleValues2) {
         for (double var19 : CONST_doubleValues2) {
            for (double var24 : CONST_doubleValues2) {
               Vec3d var26 = new Vec3d(var1.getX() + var14, var1.getY() + var24 * 0.85, var1.getZ() + var19);
               if (!(var2.squaredDistanceTo(var26) > var3 * var3)) {
                  Rotation var27 = RotationUtils.method6913(var26);
                  BlockHitResult var28 = this.method2453(var2, var27, var5, FluidHandling.SOURCE_ONLY);
                  double var29;
                  if (var28.getType() == HitResult.Type.BLOCK && var28.getBlockPos().equals(var1) && (var29 = var7.method3131(var27).method5327()) < var9) {
                     var9 = var29;
                     var8 = new AutoTrapModule.Data4(var27);
                  }
               }
            }
         }
      }

      return var8;
   }

   private boolean method2450(AutoTrapModule.Type var1, BlockPos var2, BlockState var3) {
      if (var3.isReplaceable()) {
         return false;
      } else {
         return var1 != AutoTrapModule.Type.FIRE || !CampfireBlock.canBeLit(var3) && !CandleBlock.canBeLit(var3) && !CandleCakeBlock.canBeLit(var3)
            ? var1.flag2 || mc.player.isSneaking() || var3.createScreenHandlerFactory(mc.world, var2) == null
            : false;
      }
   }

   private AutoTrapModule.Data2 method2451() {
      Rotation var1 = rotationManager.getRotation2();
      if (var1 == null) {
         return null;
      } else {
         Vec3d var2 = mc.player.getEyePos();
         double var3 = mc.player.getBlockInteractionRange();
         BlockHitResult var5 = this.method2453(var2, var1, this.method2468(), FluidHandling.NONE);
         BlockHitResult var6 = this.method2453(var2, var1, var3, FluidHandling.NONE);
         BlockHitResult var7 = this.method2453(var2, var1, var3, FluidHandling.SOURCE_ONLY);

         for (AutoTrapModule.Data6 var9 : this.items6) {
            if (this.method2460(var9.getType()) && this.method2455(var9)) {
               BlockHitResult var10 = switch (var9.getType()) {
                  case LAVA -> var6;
                  case PICKUP -> var7;
                  default -> var5;
               };
               if (var10.getType() == HitResult.Type.BLOCK
                  && !var10.isInsideBlock()
                  && this.method2452(var9.getType(), var10).equals(var9.pos())
                  && (
                     var9.getType() == AutoTrapModule.Type.PICKUP
                        || mc.world.getBlockState(var10.getBlockPos()).isReplaceable()
                        || this.method2450(var9.getType(), var10.getBlockPos(), mc.world.getBlockState(var10.getBlockPos()))
                  )) {
                  return new AutoTrapModule.Data2(var9, var10);
               }
            }
         }

         return null;
      }
   }

   private BlockPos method2452(AutoTrapModule.Type var1, BlockHitResult var2) {
      return switch (var1) {
         case LAVA, FIRE -> var2.getBlockPos().offset(var2.getSide());
         default -> mc.world.getBlockState(var2.getBlockPos()).isReplaceable() ? var2.getBlockPos() : var2.getBlockPos().offset(var2.getSide());
         case PICKUP -> var2.getBlockPos();
      };
   }

   private BlockHitResult method2453(Vec3d var1, Rotation var2, double var3, FluidHandling var5) {
      Vec3d var6 = var1.add(var2.method3135().multiply(var3));
      return mc.world.raycast(new RaycastContext(var1, var6, ShapeType.OUTLINE, var5, mc.player));
   }

   private Vec3d method2454(Box var1, Direction var2, double var3, double var5) {
      double var7 = MathHelper.lerp(var3, var1.minX, var1.maxX);
      double var9 = MathHelper.lerp(var5, var1.minY, var1.maxY);
      double var11 = MathHelper.lerp(var5, var1.minZ, var1.maxZ);
      double var13 = MathHelper.lerp(var3, var1.minZ, var1.maxZ);

      return switch (var2) {
         case UP -> new Vec3d(var7, var1.maxY, var11);
         case DOWN -> new Vec3d(var7, var1.minY, var11);
         case NORTH -> new Vec3d(var7, var9, var1.minZ);
         case SOUTH -> new Vec3d(var7, var9, var1.maxZ);
         case WEST -> new Vec3d(var1.minX, var9, var13);
         case EAST -> new Vec3d(var1.maxX, var9, var13);
      };
   }

   private boolean method2455(AutoTrapModule.Data6 var1) {
      BlockPos var2 = var1.pos();
      if (!mc.world.isInBuildLimit(var2) || !mc.world.getWorldBorder().contains(var2)) {
         return false;
      } else if (var1.getType() == AutoTrapModule.Type.PICKUP) {
         return this.method2458(var2);
      } else {
         boolean var10000;
         label63: {
            if (this.items != null) {
               if (this.items.getClass() == EmptyMap.class) {
                  var10000 = false;
                  break label63;
               }
            }

            var10000 = this.items.containsKey(var2);
         }

         if (var10000) {
            return false;
         } else {
            BlockState var3 = mc.world.getBlockState(var2);
            switch (var1.getType()) {
               case LAVA:
                  if (!var3.isAir() && (!var3.isReplaceable() || !var3.getFluidState().isEmpty())) {
                     return false;
                  }

                  if (!this.method2456(var2)) {
                     return false;
                  }
                  break;
               case FIRE:
                  BlockPos var4 = var2.down();
                  if (!var3.isAir() || !mc.world.getBlockState(var4).isSideSolidFullSquare(mc.world, var4, Direction.UP)) {
                     return false;
                  }
                  break;
               default:
                  if (!var3.isReplaceable()) {
                     return false;
                  }
            }

            if (this.protectSelf.isEnabled()) {
               Box var5 = mc.player.getBoundingBox().stretch(mc.player.getVelocity()).expand(0.2, 0.0, 0.2);
               return !var5.intersects(new Box(var2));
            } else {
               return true;
            }
         }
      }
   }

   private boolean method2456(BlockPos var1) {
      if (!this.protectSelf.isEnabled()) {
         return true;
      } else {
         double var2 = var1.getX() + 0.5 - mc.player.getX();
         double var4 = var1.getZ() + 0.5 - mc.player.getZ();
         return Math.hypot(var2, var4) >= 3.0 || var1.getY() + 1 < mc.player.getBlockY();
      }
   }

   private boolean method2457(BlockPos var1) {
      AutoTrapModule.Data var2 = this.items.get(var1);
      if (var2 != null && var2.getType().flag) {
         return true;
      } else {
         BlockState var3 = mc.world.getBlockState(var1);
         return var3.isOf(Blocks.COBWEB) || var3.isOf(Blocks.POWDER_SNOW);
      }
   }

   private boolean method2458(BlockPos var1) {
      return mc.world.getFluidState(var1).isOf(Fluids.LAVA);
   }

   private boolean method2459(AutoTrapModule.Data2 var1, int var2) {
      AutoTrapModule.Type var3 = var1.getData6().getType();
      Hand var4 = Hand.MAIN_HAND;
      int var5 = -1;
      if (method2462(var3, mc.player.getOffHandStack())) {
         var4 = Hand.OFF_HAND;
      } else if (!method2462(var3, mc.player.getMainHandStack())) {
         int var6;
         if ((var6 = this.method2461(var3)) == -1) {
            return false;
         }

         var5 = mc.player.getInventory().getSelectedSlot();
         mc.player.getInventory().setSelectedSlot(var6);
      }

      int var12 = PlacementQueue.method2811(var4);
      Item var7 = mc.player.getStackInHand(var4).getItem();

      ActionResult var8;
      try {
         var8 = var3.flag2
            ? mc.interactionManager.interactItem(mc.player, var4)
            : mc.interactionManager.interactBlock(mc.player, var4, var1.getBlockHitResult());
      } finally {
         if (var5 != -1) {
            mc.player.getInventory().setSelectedSlot(var5);
            mc.interactionManager.syncSelectedSlot();
         }
      }

      if (!var8.isAccepted()) {
         return false;
      } else {
         if (var3 == AutoTrapModule.Type.WEB || var3 == AutoTrapModule.Type.ANVIL || var7 == Items.FIRE_CHARGE) {
            this.placementQueue.method2812(var1.getData6().pos().asLong(), var12, var7, var2 + this.method2446() + 2);
         }

         if (var8 instanceof Success var9 && var9.swingSource() == SwingSource.CLIENT) {
            if (this.hideSwing.isEnabled()) {
               mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(var4));
            } else {
               mc.player.swingHand(var4);
            }
         }

         return true;
      }
   }

   private boolean method2460(AutoTrapModule.Type var1) {
      return !method2462(var1, mc.player.getOffHandStack()) && !method2462(var1, mc.player.getMainHandStack()) ? this.method2461(var1) != -1 : true;
   }

   private int method2461(AutoTrapModule.Type var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (method2462(var1, mc.player.getInventory().getStack(var2))) {
            return var2;
         }
      }

      return -1;
   }

   private static boolean method2462(AutoTrapModule.Type var0, ItemStack var1) {
      return switch (var0) {
         case WEB -> var1.isOf(Items.COBWEB);
         case SNOW -> var1.isOf(Items.POWDER_SNOW_BUCKET);
         case LAVA -> var1.isOf(Items.LAVA_BUCKET);
         case FIRE -> var1.isOf(Items.FLINT_AND_STEEL) || var1.isOf(Items.FIRE_CHARGE);
         case ANVIL -> var1.isIn(ItemTags.ANVIL);
         case PICKUP -> var1.isOf(Items.BUCKET);
      };
   }

   private static boolean method2463(AutoTrapModule.Type var0, BlockState var1) {
      return switch (var0) {
         case WEB -> var1.isOf(Blocks.COBWEB);
         case SNOW -> var1.isOf(Blocks.POWDER_SNOW);
         case LAVA -> var1.getFluidState().isOf(Fluids.LAVA);
         case FIRE -> var1.isIn(BlockTags.FIRE);
         case ANVIL -> var1.isIn(BlockTags.ANVIL);
         case PICKUP -> !var1.getFluidState().isOf(Fluids.LAVA);
      };
   }

   private boolean method2464() {
      if (mc.currentScreen != null || mc.player.isSpectator() || mc.player.isDead()) {
         return false;
      } else if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         return false;
      } else {
         return this.pauseOnUse.isEnabled() && mc.player.isUsingItem() ? false : !FreeCamModule.INSTANCE.isEnabled();
      }
   }

   private boolean method2465() {
      return !this.activation.isSelected(1) || UseItemUtils.method6521(this.bind);
   }

   private PlayerEntity method2466() {
      double var1 = this.range.getValue3() + 2.0;
      LivingEntity var3 = AuraModule.INSTANCE.getLivingEntity22();
      PlayerEntity var4 = null;
      double var5 = Double.MAX_VALUE;

      for (PlayerEntity var8 : mc.world.getPlayers()) {
         double var9;
         double var11;
         if (this.method2467(var8)
            && !((var9 = mc.player.distanceTo(var8)) > var1)
            && (var11 = var9 - (var8 == var3 ? 100 : 0) - (var8 == this.playerEntity ? 0.5 : 0.0)) < var5) {
            var5 = var11;
            var4 = var8;
         }
      }

      return var4;
   }

   private boolean method2467(PlayerEntity var1) {
      return var1 != mc.player
         && CrystalAuraModule.method5901(var1)
         && !Zenith.getInstance().getFriends().method7626(var1)
         && !AntiBotModule.INSTANCE.method2785(var1);
   }

   private double method2468() {
      return Math.min((double)this.range.getValue3(), mc.player.getBlockInteractionRange());
   }

   private void method2469(int var1) {
      AutoTrapModule.Data3 var2;
      while ((var2 = this.queue.poll()) != null) {
         AutoTrapModule.Data var3 = this.items.remove(var2.pos());
         if (var3 != null) {
            long var4 = var2.pos().asLong();
            if (!method2463(var3.getType(), var2.state())) {
               this.placementQueue.method2814(var4);
            } else {
               this.placementQueue.method2813(var4);
               if (var3.getType() == AutoTrapModule.Type.LAVA) {
                  this.items2.put(var2.pos(), var1);
               }

               if (var3.getType().flag) {
                  this.method2471();
               }

               if (this.render.isEnabled() && var3.getType() != AutoTrapModule.Type.PICKUP) {
                  this.items3.add(new AutoTrapModule.Data5(var2.pos(), System.currentTimeMillis()));
               }
            }
         }
      }
   }

   private void method2470(int var1) {
      var var2 = this.items.entrySet().iterator();

      while (var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((AutoTrapModule.Data)var3.getValue()).getIntValue() < var1) {
            if (((AutoTrapModule.Data)var3.getValue()).getType() == AutoTrapModule.Type.LAVA && this.method2458((BlockPos)var3.getKey())) {
               this.items2.put((BlockPos)var3.getKey(), var1);
            }

            var2.remove();
         }
      }
   }

   private void method2471() {
      long var1;
      if ((var1 = System.currentTimeMillis()) - this.longValue >= 2000L) {
         this.longValue = var1;
         Island.method2878(Zenith.getInstance().getLocalization().translate("hud.island.trap"));
      }
   }

   private void method2472() {
      this.playerEntity = null;
      this.arrayDeque.clear();
      this.items4 = List.of();
      this.items5 = List.of();
   }

   private void reset() {
      this.method2472();
      this.items6 = List.of();
      this.items.clear();
      this.items2.clear();
      this.flag = false;
      this.queue.clear();
      this.items3.clear();
      this.placementQueue.clear();
      this.class33.cancel();
      this.intValue5 = -32768 << 16 | 0;
      this.data2 = null;
      this.blockPos = null;
      this.rotation = null;
      this.intValue = -32768 << 16 | 0;
      this.intValue2 = -32768 << 16 | 0;
      this.intValue3 = -16384 << 16 | 0;
      this.intValue4 = -16384 << 16 | 0;
   }

   private static double method2473(Vec3d var0) {
      return Math.hypot(var0.x, var0.z);
   }

   private static Vec3d method2474(Vec3d var0, float var1) {
      double var2;
      double var4 = Math.cos(var2 = Math.toRadians(var1));
      double var6 = Math.sin(var2);
      return new Vec3d(var0.x * var4 - var0.z * var6, var0.y, var0.x * var6 + var0.z * var4);
   }

   private static Box method2475(BlockPos var0, double var1) {
      double var3 = 0.5 * MathHelper.clamp(var1, 0.05, 1.0);
      Vec3d var5 = Vec3d.ofCenter(var0);
      return new Box(var5.subtract(var3, var3, var3), var5.add(var3, var3, var3));
   }

   private static final class Data {
      private final AutoTrapModule.Type type;
      private final int intValue;

      public AutoTrapModule.Type type() {
         return this.type;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "type=" + this.type + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(AutoTrapModule.Type var1, int var2) {
         this.type = var1;
         this.intValue = var2;
      }

      public AutoTrapModule.Type getType() {
         return this.type;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data2 {
      private final AutoTrapModule.Data6 data6;
      private final BlockHitResult blockHitResult;

      public AutoTrapModule.Data6 data6() {
         return this.data6;
      }

      public BlockHitResult blockHitResult() {
         return this.blockHitResult;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.data6, other.data6) && java.util.Objects.equals(this.blockHitResult, other.blockHitResult);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data6, blockHitResult);
      }

      @Override
      public String toString() {
         return "Data2[" + "data6=" + this.data6 + ", " + "blockHitResult=" + this.blockHitResult + "]";
      }

      private Data2(AutoTrapModule.Data6 var1, BlockHitResult var2) {
         this.data6 = var1;
         this.blockHitResult = var2;
      }

      public AutoTrapModule.Data6 getData6() {
         return this.data6;
      }

      public BlockHitResult getBlockHitResult() {
         return this.blockHitResult;
      }
   }

   private static final class Data3 {
      private final BlockPos blockPos;
      private final BlockState blockState;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public BlockState blockState() {
         return this.blockState;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.blockState, other.blockState);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, blockState);
      }

      @Override
      public String toString() {
         return "Data3[" + "blockPos=" + this.blockPos + ", " + "blockState=" + this.blockState + "]";
      }

      private Data3(BlockPos var1, BlockState var2) {
         this.blockPos = var1;
         this.blockState = var2;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public BlockState state() {
         return this.blockState;
      }
   }

   private static final class Data4 {
      private final Rotation rotation;

      public Rotation rotation() {
         return this.rotation;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation);
      }

      @Override
      public String toString() {
         return "Data4[" + "rotation=" + this.rotation + "]";
      }

      private Data4(Rotation var1) {
         this.rotation = var1;
      }
   }

   private static final class Data5 {
      private final BlockPos blockPos;
      private final long longValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, longValue);
      }

      @Override
      public String toString() {
         return "Data5[" + "blockPos=" + this.blockPos + ", " + "longValue=" + this.longValue + "]";
      }

      private Data5(BlockPos var1, long var2) {
         this.blockPos = var1;
         this.longValue = var2;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public long time() {
         return this.longValue;
      }
   }

   private static final class Data6 {
      private final AutoTrapModule.Type type;
      private final BlockPos blockPos;

      public AutoTrapModule.Type type() {
         return this.type;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.blockPos, other.blockPos);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type, blockPos);
      }

      @Override
      public String toString() {
         return "Data6[" + "type=" + this.type + ", " + "blockPos=" + this.blockPos + "]";
      }

      private Data6(AutoTrapModule.Type var1, BlockPos var2) {
         this.type = var1;
         this.blockPos = var2;
      }

      public AutoTrapModule.Type getType() {
         return this.type;
      }

      public BlockPos pos() {
         return this.blockPos;
      }
   }

   private static enum Type {
      WEB(true, false),
      SNOW(true, false),
      LAVA(false, true),
      FIRE(false, false),
      ANVIL(false, false),
      PICKUP(false, true);

      private final boolean flag;
      private final boolean flag2;

      private Type(boolean var3, boolean var4) {
         this.flag = var3;
         this.flag2 = var4;
      }
   }
}

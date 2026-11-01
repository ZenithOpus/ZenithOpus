package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import kotlin.collections.EmptyList;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.Mutable;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "CropFarmer",
   category = ModuleCategory.PLAYER,
   description = "module.milanafarm.description"
)
public final class CropFarmerModule extends Module {
   public static final CropFarmerModule INSTANCE = new CropFarmerModule();
   private static final long CONST_longValue = 1000000000L;
   private final ModeSetting crop;
   private final BooleanSetting renderUnsuitableBeds;
   private final BooleanSetting renderLowLightBeds;
   private final BooleanSetting renderWaterPositions;
   private final BooleanSetting x;
   private final StopWatch stopWatch;
   private final StopWatch stopWatch2;
   private final StopWatch stopWatch3;
   private final Set<Integer> items;
   private final Map<BlockPos, CropFarmerModule.Entry> items2;
   private List<BlockPos> items3;
   private boolean flag;
   private List<BlockPos> items4;
   private BitSet bitSet;
   private List<BlockPos> items5;
   private String text;
   private CropFarmerModule.Type type;
   private static final int CONST_intValue = 4194304;
   private byte[] byteValues;
   private boolean flag2;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private BlockHitResult blockHitResult;
   private static final Comparator<BlockPos> comparator = Comparator.<BlockPos>comparingInt(Vec3i::getX).thenComparingInt(Vec3i::getZ).thenComparingInt(Vec3i::getY);

   private CropFarmerModule() {
      this.crop = new ModeSetting(
         "module.cropFarmer.crop",
         "module.cropFarmer.crop.desc",
         CropFarmerModule.Type.NETHER_WART.text,
         CropFarmerModule.Type.CARROT.text,
         CropFarmerModule.Type.POTATO.text,
         CropFarmerModule.Type.BEETROOT.text,
         CropFarmerModule.Type.WHEAT.text,
         CropFarmerModule.Type.TORCHFLOWER.text,
         CropFarmerModule.Type.PITCHER_PLANT.text
      );
      this.renderUnsuitableBeds = new BooleanSetting(
         "module.cropFarmer.renderUnsuitableBeds", "module.cropFarmer.renderUnsuitableBeds.desc", false, this::method4429
      );
      this.renderLowLightBeds = new BooleanSetting(
         "module.cropFarmer.renderLowLightBeds", "module.cropFarmer.renderLowLightBeds.desc", false, () -> this.method4430().intValue > 0
      );
      this.renderWaterPositions = new BooleanSetting(
         "module.cropFarmer.renderWaterPositions", "module.cropFarmer.renderWaterPositions.desc", false, this::method4429
      );
      this.x = new BooleanSetting("1.20.x", "module.cropFarmer.legacyBypass.desc", false);
      this.stopWatch = new StopWatch();
      this.stopWatch2 = new StopWatch();
      this.stopWatch3 = new StopWatch();
      this.items = new HashSet<>();
      this.items2 = new HashMap<>();
      this.items3 = Collections.emptyList();
      this.items5 = Collections.emptyList();
      this.byteValues = new byte[0];
      this.blockHitResult = null;
   }

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.items2.clear();
      this.blockHitResult = null;
      this.items3 = Collections.emptyList();
      this.flag = false;
      this.items4 = null;
      this.bitSet = null;
      this.items5 = Collections.emptyList();
   }

   @EventTarget
   public void method4390(WorldRenderEvent var1) {
      label60: {
         Item var2 = this.method4402();
         if (mc.player.playerScreenHandler.slots.stream().filter(var1x -> var1x.id != 45 && var1x.getStack().getItem() == var2).count() < 20L) {
            if (!(mc.currentScreen instanceof GenericContainerScreen var3) || !var3.getTitle().getString().contains("Скупщик")) {
               break label60;
            }

            ScreenHandler var8 = mc.player.currentScreenHandler;
            if (!(var8 instanceof GenericContainerScreenHandler)) {
               break label60;
            }
         }

         if (!this.stopWatch2.method7546(30L)) {
            return;
         }

         this.stopWatch2.reset();
         if (!(mc.currentScreen instanceof GenericContainerScreen) && this.stopWatch.method7546(3000L)) {
            mc.getNetworkHandler().sendChatCommand("buyer");
            this.stopWatch.reset();
         } else if (mc.currentScreen instanceof GenericContainerScreen var7
            && var7.getTitle().getString().contains("Скупщик")
            && mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var4) {
            for (Slot var6 : mc.player.currentScreenHandler.slots) {
               if (!this.items.contains(var6.id) && var6.id >= var4.getInventory().size() && var6.getStack().getItem() == var2) {
                  this.items.add(var6.id);
                  InventoryUtils.method903(var6, 0, SlotActionType.QUICK_MOVE, false);
                  return;
               }
            }

            InventoryUtils.method904(53, 0, SlotActionType.QUICK_MOVE, false);
            mc.player.closeHandledScreen();
         }

         return;
      }

      this.items.clear();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @EventTarget
   public void method4391(WorldRenderEvent var1) {
      if (this.renderUnsuitableBeds.isEnabled() && this.method4429()) {
         BlockPos var2 = Zenith.getInstance().getTimedBlockPos().getBlockPos();
         BlockPos var3 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
         if (var2 != null && var3 != null) {
            int var4 = Color.RED.getRGB();
            this.method4423(var2, var3);

            try {
               method4426(var2, var3, var2x -> {
                  if (this.method4417(var2x) && !this.method4418(var2x)) {
                     GlowUtil.method2230(new Box(var2x), var4, 1.0F);
                  }
               });
            } catch (Throwable var7) {
               this.method4424();
               throw var7;
            }

            this.method4424();
         }
      }
   }

   @EventTarget
   public void method4392(WorldRenderEvent var1) {
      if (this.renderLowLightBeds.isEnabled() && this.method4430().intValue != 0) {
         BlockPos var2 = Zenith.getInstance().getTimedBlockPos().getBlockPos();
         BlockPos var3 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
         if (var2 != null && var3 != null) {
            int var4 = Color.YELLOW.getRGB();
            method4426(var2, var3, var2x -> {
               if (this.method4417(var2x) && !this.method4419(var2x)) {
                  GlowUtil.method2230(new Box(var2x), var4, 1.0F);
               }
            });
         }
      }
   }

   @EventTarget
   public void method4393(WorldRenderEvent var1) {
      if (this.renderWaterPositions.isEnabled() && this.method4429()) {
         BlockPos var2 = Zenith.getInstance().getTimedBlockPos().getBlockPos();
         BlockPos var3 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
         if (var2 != null && var3 != null) {
            if (!this.flag || this.stopWatch3.method7544(1000L)) {
               this.items3 = this.method4420(var2, var3);
               this.flag = true;
            }

            this.items3.forEach(var0 -> GlowUtil.method2230(new Box(var0), new Color(0, 120, 255).getRGB(), 1.0F));
         }
      }
   }

   @EventTarget
   public void method4394(Event5 var1) {
      if (Zenith.getInstance().getTimedBlockPos().getBlockPos() == null) {
         if (mc.player.age % 80 == 0) {
            Zenith.getInstance().getHudHolder().method6973("4", Text.of("Отметьте точку .region pos1"), 4000L);
         }
      } else if (Zenith.getInstance().getTimedBlockPos().getBlockPos2() == null) {
         if (mc.player.age % 80 == 0) {
            Zenith.getInstance()
               .getHudHolder()
               .method6973(
                  "4",
                  Text.of("Отметьте точку .region ")
                     .copy()
                     .append(
                        Text.of("pos2")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                     ),
                  4000L
               );
         }
      } else if (!this.method4413()) {
         Slot var2 = InventoryUtils.method908(mc.player.playerScreenHandler, this.method4401());
         if (!this.method4428() && var2 != null && KeyBindUtils.method2630(AutoTotemModule.class) && KeyBindUtils.method2630(CropFarmerModule.class)) {
            KeyBindUtils.method2627(CropFarmerModule.class, () -> {
               if (!(mc.player.currentScreenHandler instanceof PlayerScreenHandler)) {
                  InventoryUtils.closeScreen();
               }

               InventoryUtils.method900(var2, Hand.OFF_HAND, true);
            });
         } else {
            Rotation var3 = this.method4405();
            if (var3 != null) {
               if (this.x.isEnabled()) {
                  this.method4396();
               } else {
                  Rotation var4 = this.method4400(var3);
                  Zenith.getInstance()
                     .getRotationManager()
                     .method7849(
                        new RotationRequest(
                           var4, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var4), bcCameraTestnerv.getRotationConfig3()
                        ),
                        7,
                        this
                     );
               }
            }
         }
      }
   }

   @EventTarget
   public void method4395(UpdateEvent var1) {
      if (!this.method4413()) {
         if (!this.x.isEnabled()) {
            BlockHitResult var2 = this.method4407(zenith.getRotationManager().method7844());
            if (var2 != null) {
               if (this.blockHitResult != null && this.blockHitResult.getBlockPos().equals(var2.getBlockPos())) {
                  this.blockHitResult = var2;
                  BlockState var3 = mc.world.getBlockState(var2.getBlockPos());
                  this.method4409(var2, var3);
               } else {
                  this.blockHitResult = var2;
               }
            }
         }
      }
   }

   public void method4396() {
      this.method4397();
      Rotation var1 = null;
      var var2 = new HashSet<>(this.items2.keySet());

      for (int var3 = 0; var3 < 4; var3++) {
         Rotation var4 = this.method4406(var2);
         if (var4 == null) {
            break;
         }

         BlockHitResult var5 = this.method4408(var4, var2);
         if (var5 == null) {
            var1 = var4;
            break;
         }

         BlockPos var6 = var5.getBlockPos();
         var2.add(var6);
         BlockState var7 = mc.world.getBlockState(var6);
         CropFarmerModule.Type2 var8 = this.method4399(var6, var7);
         if (var8 != null) {
            UseItemUtils.method6533(0.0, var4);
            this.method4410(var5, var7);
            this.items2.put(var6, new CropFarmerModule.Entry(var8, System.nanoTime()));
         }

         var1 = var4;
      }

      Rotation var9 = this.method4406(var2);
      Rotation var10 = var9 != null ? var9 : (var1 == null ? zenith.getRotationManager().method7844() : var1);
      Rotation var11 = this.method4400(var10).method3140((float)MathUtils.method2651(-5.0, 5.0), (float)MathUtils.method2651(-5.0, 5.0));
      Zenith.getInstance()
         .getRotationManager()
         .method7849(
            new RotationRequest(var11, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var11), bcCameraTestnerv.getRotationConfig()),
            7,
            this
         );
   }

   private void method4397() {
      long var1 = System.nanoTime();
      this.items2.entrySet().removeIf(var3 -> var1 - var3.getValue().longValue >= 1000000000L || this.method4398(var3.getKey(), var3.getValue().type2));
   }

   private boolean method4398(BlockPos var1, CropFarmerModule.Type2 var2) {
      BlockState var3 = mc.world.getBlockState(var1);
      BlockState var4 = mc.world.getBlockState(var1.up());

      return switch (var2) {
         case BREAK -> !this.method4404(var3);
         case TILL -> !this.method4415(var3, var4);
         case PLACE -> !var4.isAir();
      };
   }

   private CropFarmerModule.Type2 method4399(BlockPos var1, BlockState var2) {
      if (this.method4404(var2)) {
         return CropFarmerModule.Type2.BREAK;
      } else {
         BlockState var3 = mc.world.getBlockState(var1.up());
         if (this.method4427() && this.method4415(var2, var3)) {
            return CropFarmerModule.Type2.TILL;
         } else {
            return this.method4414(var1, var2, var3) ? CropFarmerModule.Type2.PLACE : null;
         }
      }
   }

   private Rotation method4400(Rotation var1) {
      Rotation var2 = zenith.getRotationManager().method7844();
      return var2.method3141(var2.method3131(var1));
   }

   public Item method4401() {
      return this.method4430().item;
   }

   public Item method4402() {
      return this.method4430().item2;
   }

   public boolean method4403(BlockState var1, BlockState var2) {
      return var1.isOf(this.method4430().block) && var2.isAir();
   }

   public boolean method4404(BlockState var1) {
      return switch (this.method4430()) {
         case NETHER_WART -> var1.isOf(Blocks.NETHER_WART) && var1.get(NetherWartBlock.AGE) == 3;
         case CARROT -> var1.isOf(Blocks.CARROTS) && var1.get(CropBlock.AGE) == 7;
         case POTATO -> var1.isOf(Blocks.POTATOES) && var1.get(CropBlock.AGE) == 7;
         case BEETROOT -> var1.isOf(Blocks.BEETROOTS) && var1.get(BeetrootsBlock.AGE) == 3;
         case WHEAT -> var1.isOf(Blocks.WHEAT) && var1.get(CropBlock.AGE) == 7;
         case TORCHFLOWER -> var1.isOf(Blocks.TORCHFLOWER);
         case PITCHER_PLANT -> var1.isOf(Blocks.PITCHER_CROP)
            && var1.get(PitcherCropBlock.AGE) == 4
            && var1.get(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER;
      };
   }

   public Rotation method4405() {
      return this.method4406(Collections.emptySet());
   }

   private Rotation method4406(Set<BlockPos> var1) {
      BlockPos var2 = Zenith.getInstance().getTimedBlockPos().getBlockPos();
      BlockPos var3 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
      boolean var4 = this.method4428();
      boolean var5 = this.method4427();
      BlockPos var6 = null;
      BlockPos var7 = null;
      BlockPos var8 = null;
      double var9 = Double.MAX_VALUE;
      double var11 = Double.MAX_VALUE;
      double var13 = Double.MAX_VALUE;
      int var15 = Math.min(var2.getX(), var3.getX());
      int var16 = Math.max(var2.getX(), var3.getX());
      int var17 = Math.min(var2.getY(), var3.getY());
      int var18 = Math.max(var2.getY(), var3.getY());
      int var19 = Math.min(var2.getZ(), var3.getZ());
      int var20 = Math.max(var2.getZ(), var3.getZ());
      Mutable var21 = new Mutable();
      Mutable var22 = new Mutable();
      this.method4423(var2, var3);

      try {
         for (int var23 = var15; var23 <= var16; var23++) {
            for (int var24 = var17; var24 <= var18; var24++) {
               for (int var25 = var19; var25 <= var20; var25++) {
                  var21.set(var23, var24, var25);
                  if (!var1.contains(var21)) {
                     BlockState var26 = mc.world.getBlockState(var21);
                     BlockState var27 = mc.world.getBlockState(var22.set(var23, var24 + 1, var25));
                     if (var4 && this.method4414(var21, var26, var27)) {
                        double var35;
                        if ((var35 = mc.player.squaredDistanceTo(Vec3d.of(var21))) < var9) {
                           var9 = var35;
                           var6 = var21.toImmutable();
                        }
                     } else {
                        double var28;
                        if (var5 && this.method4415(var26, var27) && (var28 = mc.player.squaredDistanceTo(Vec3d.of(var21))) < var11) {
                           var11 = var28;
                           var7 = var21.toImmutable();
                        }
                     }

                     double var36;
                     if (this.method4404(var26) && (var36 = mc.player.squaredDistanceTo(Vec3d.ofCenter(var21))) < var13) {
                        var13 = var36;
                        var8 = var21.toImmutable();
                     }
                  }
               }
            }
         }
      } finally {
         this.method4424();
      }

      Vec3d var34 = mc.player.getEyePos();
      Rotation var33;
      if (var4 && var6 != null) {
         var33 = RotationUtils.method6912(var6.toCenterPos().add(0.0, 0.5, 0.0).subtract(var34));
      } else if (var7 == null || !(var7.toCenterPos().squaredDistanceTo(var34) < 20.0) && var8 != null) {
         if (var8 != null) {
            var33 = RotationUtils.method6912(var8.toCenterPos().add(0.0, -0.5, 0.0).subtract(var34));
         } else {
            var33 = null;
         }
      } else {
         var33 = RotationUtils.method6912(var7.toCenterPos().add(0.0, 0.5, 0.0).subtract(var34));
      }

      return var33 == null ? null : zenith.getRotationManager().method7844().method3141(rotationManager.method7844().method3131(var33));
   }

   public BlockHitResult method4407(Rotation var1) {
      return this.method4408(var1, Collections.emptySet());
   }

   private BlockHitResult method4408(Rotation var1, Set<BlockPos> var2) {
      BlockHitResult var3 = TargetUtils.method3216(mc.player.getCameraPosVec(1.0F), var1, mc.player.getBlockInteractionRange(), var2x -> {
         if (var2x == null) {
            return false;
         } else {
            BlockPos var3x = var2x.getBlockPos();
            if (var2.contains(var3x)) {
               return false;
            } else {
               BlockState var4 = mc.world.getBlockState(var3x);
               BlockState var5 = mc.world.getBlockState(var3x.up());
               return this.method4404(var4) || this.method4414(var3x, var4, var5) || this.method4427() && this.method4415(var4, var5);
            }
         }
      });
      return var3 != null && var3.getType() != HitResult.Type.MISS ? var3 : null;
   }

   public void method4409(BlockHitResult var1, BlockState var2) {
      if (this.method4404(var2)) {
         mc.interactionManager.updateBlockBreakingProgress(var1.getBlockPos(), var1.getSide());
         mc.player.swingHand(Hand.MAIN_HAND);
         this.method4411(var1.getBlockPos());
      } else if (this.method4415(var2, mc.world.getBlockState(var1.getBlockPos().up()))) {
         if (this.method4427()) {
            UseItemUtils.method6514(var1, Hand.MAIN_HAND);
         }
      } else if (this.method4414(var1.getBlockPos(), var2, mc.world.getBlockState(var1.getBlockPos().up()))) {
         UseItemUtils.method6514(this.method4412(var1.getBlockPos()), Hand.OFF_HAND);
      }
   }

   public void method4410(BlockHitResult var1, BlockState var2) {
      if (this.method4404(var2)) {
         mc.interactionManager.updateBlockBreakingProgress(var1.getBlockPos(), var1.getSide());
         this.method4411(var1.getBlockPos());
      } else if (this.method4415(var2, mc.world.getBlockState(var1.getBlockPos().up()))) {
         if (this.method4427()) {
            UseItemUtils.method6514(var1, Hand.MAIN_HAND);
         }
      } else if (this.method4414(var1.getBlockPos(), var2, mc.world.getBlockState(var1.getBlockPos().up()))) {
         UseItemUtils.method6514(this.method4412(var1.getBlockPos()), Hand.OFF_HAND);
      }
   }

   private void method4411(BlockPos var1) {
      BlockPos var2 = var1.down();
      BlockState var3 = mc.world.getBlockState(var2);
      if (this.method4428() && var3.isOf(this.method4430().block) && (!this.method4429() || this.method4418(var2))) {
         UseItemUtils.method6514(this.method4412(var2), Hand.OFF_HAND);
      }
   }

   private BlockHitResult method4412(BlockPos var1) {
      return new BlockHitResult(var1.toCenterPos().add(0.0, 0.5, 0.0), Direction.UP, var1, false);
   }

   public boolean method4413() {
      return Zenith.getInstance().getTimedBlockPos().getBlockPos() != null && Zenith.getInstance().getTimedBlockPos().getBlockPos2() != null
         ? mc.player.isUsingItem()
            || mc.currentScreen instanceof ChatScreen
            || mc.currentScreen instanceof InventoryScreen
            || !(mc.player.currentScreenHandler instanceof PlayerScreenHandler)
         : true;
   }

   private boolean method4414(BlockPos var1, BlockState var2, BlockState var3) {
      return this.method4428() && this.method4403(var2, var3) && (!this.method4429() || this.method4418(var1));
   }

   private boolean method4415(BlockState var1, BlockState var2) {
      return this.method4429() && var2.isAir() && this.method4416(var1);
   }

   private boolean method4416(BlockState var1) {
      return var1.isOf(Blocks.GRASS_BLOCK)
         || var1.isOf(Blocks.DIRT_PATH)
         || var1.isOf(Blocks.DIRT)
         || var1.isOf(Blocks.COARSE_DIRT)
         || var1.isOf(Blocks.ROOTED_DIRT);
   }

   private boolean method4417(BlockPos var1) {
      BlockState var2 = mc.world.getBlockState(var1);
      return var2.isOf(Blocks.FARMLAND) || this.method4416(var2) && mc.world.getBlockState(var1.up()).isAir();
   }

   private boolean method4418(BlockPos var1) {
      if (!this.method4429()) {
         return true;
      } else {
         BlockPos var2 = var1.up();
         if (!this.method4422(var1) && !mc.world.hasRain(var2)) {
            return false;
         } else {
            BlockState var3 = mc.world.getBlockState(var2.up());
            return this.method4430() != CropFarmerModule.Type.PITCHER_PLANT || var3.isAir() || var3.isOf(Blocks.PITCHER_CROP);
         }
      }
   }

   private boolean method4419(BlockPos var1) {
      return mc.world.getBaseLightLevel(var1.up(), 0) >= this.method4430().intValue;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private List<BlockPos> method4420(BlockPos var1, BlockPos var2) {
      TreeMap var3 = new TreeMap();
      HashMap var4 = new HashMap();
      ArrayList var5 = new ArrayList();
      BitSet var6 = new BitSet();
      this.method4423(var1, var2);

      try {
         for (BlockPos var8 : getBlocksInZone(var1, var2)) {
            if (this.method4417(var8)) {
               ((java.util.List)var3.computeIfAbsent(var8.getY(), var0 -> new ArrayList<>())).add(var8);
               if (!this.method4422(var8)) {
                  var6.set(var5.size());
                  ((java.util.List)var4.computeIfAbsent(var8.getY(), var0 -> new ArrayList<>())).add(var8);
               }

               var5.add(var8);
            }
         }
      } catch (Throwable var11) {
         this.method4424();
         throw var11;
      }

      this.method4424();
      if (var5.equals(this.items4) && var6.equals(this.bitSet)) {
         return this.items5;
      } else {
         ArrayList<BlockPos> var12 = new ArrayList<>();

         for (Object var9_o : (Iterable<?>)(Object)(var3.entrySet())) {
            Map.Entry var9 = (Map.Entry)var9_o;
            var12.addAll(this.method4421((List<BlockPos>)var9.getValue(), (List<BlockPos>)var4.getOrDefault(var9.getKey(), Collections.emptyList())));
         }

         this.items4 = var5;
         this.bitSet = var6;
         this.items5 = var12;
         return var12;
      }
   }

   private List<BlockPos> method4421(List<BlockPos> var1, List<BlockPos> var2) {
      var var3 = var2.stream().sorted(comparator).toList();
      if (var3.isEmpty()) {
         return Collections.emptyList();
      } else {
         LinkedHashMap var4 = new LinkedHashMap();

         for (BlockPos var6 : var1.stream().sorted(comparator).toList()) {
            BitSet var7 = new BitSet(var3.size());

            for (int var8 = 0; var8 < var3.size(); var8++) {
               BlockPos var9 = (BlockPos)var3.get(var8);
               if (Math.abs(var6.getX() - var9.getX()) <= 4 && Math.abs(var6.getZ() - var9.getZ()) <= 4) {
                  var7.set(var8);
               }
            }

            if (!var7.isEmpty()) {
               var4.putIfAbsent(var7, var6);
            }
         }

         var var10 = new ArrayList<>(var4.values());
         var var11 = new ArrayList<>(var4.keySet());
         CropFarmerModule.Entry2 var12 = new CropFarmerModule.Entry2(var10, var11, var3.size(), System.nanoTime() + 25000000L);
         return var12.method4433();
      }
   }

   private boolean method4422(BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      Mutable var5 = new Mutable();

      for (int var6 = -4; var6 <= 4; var6++) {
         for (int var7 = 0; var7 <= 1; var7++) {
            for (int var8 = -4; var8 <= 4; var8++) {
               if (this.method4425(var5.set(var2 + var8, var3 + var7, var4 + var6))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private void method4423(BlockPos var1, BlockPos var2) {
      int var3 = Math.min(var1.getX(), var2.getX()) - 4;
      int var4 = Math.max(var1.getX(), var2.getX()) + 4;
      int var5 = Math.min(var1.getY(), var2.getY());
      int var6 = Math.max(var1.getY(), var2.getY()) + 1;
      int var7 = Math.min(var1.getZ(), var2.getZ()) - 4;
      int var8 = Math.max(var1.getZ(), var2.getZ()) + 4;
      long var9;
      if ((var9 = (long)(var4 - var3 + 1) * (var6 - var5 + 1) * (var8 - var7 + 1)) > 0L && var9 <= 4194304L) {
         int var11 = (int)var9;
         if (this.byteValues.length < var11) {
            this.byteValues = new byte[var11];
         } else {
            Arrays.fill(this.byteValues, 0, var11, (byte)0);
         }

         this.intValue = var3;
         this.intValue2 = var5;
         this.intValue3 = var7;
         this.intValue4 = var4 - var3 + 1;
         this.intValue5 = var6 - var5 + 1;
         this.intValue6 = var8 - var7 + 1;
         this.flag2 = true;
      } else {
         this.flag2 = false;
      }
   }

   private void method4424() {
      this.flag2 = false;
   }

   private boolean method4425(BlockPos var1) {
      if (this.flag2) {
         int var2 = var1.getX() - this.intValue;
         int var3 = var1.getY() - this.intValue2;
         int var4 = var1.getZ() - this.intValue3;
         if (var2 >= 0 && var2 < this.intValue4 && var3 >= 0 && var3 < this.intValue5 && var4 >= 0 && var4 < this.intValue6) {
            int var5 = (var2 * this.intValue5 + var3) * this.intValue6 + var4;
            int var6;
            if ((var6 = this.byteValues[var5]) == 0) {
               var6 = mc.world.getFluidState(var1).isIn(FluidTags.WATER) ? 1 : 2;
               this.byteValues[var5] = (byte)var6;
            }

            return var6 == 1;
         }
      }

      return mc.world.getFluidState(var1).isIn(FluidTags.WATER);
   }

   private static void method4426(BlockPos var0, BlockPos var1, Consumer<BlockPos> var2) {
      int var3 = Math.min(var0.getX(), var1.getX());
      int var4 = Math.max(var0.getX(), var1.getX());
      int var5 = Math.min(var0.getY(), var1.getY());
      int var6 = Math.max(var0.getY(), var1.getY());
      int var7 = Math.min(var0.getZ(), var1.getZ());
      int var8 = Math.max(var0.getZ(), var1.getZ());
      Mutable var9 = new Mutable();

      for (int var10 = var3; var10 <= var4; var10++) {
         for (int var11 = var5; var11 <= var6; var11++) {
            for (int var12 = var7; var12 <= var8; var12++) {
               var2.accept(var9.set(var10, var11, var12));
            }
         }
      }
   }

   private boolean method4427() {
      return mc.player.getMainHandStack().getItem() instanceof HoeItem;
   }

   private boolean method4428() {
      return mc.player.getStackInHand(Hand.OFF_HAND).getItem() == this.method4401();
   }

   private boolean method4429() {
      return this.method4430().block == Blocks.FARMLAND;
   }

   private CropFarmerModule.Type method4430() {
      String var1 = this.crop.get();
      CropFarmerModule.Type var2 = this.type;
      if (var2 == null || !var1.equals(this.text)) {
         var2 = CropFarmerModule.Type.method4432(var1);
         this.type = var2;
         this.text = var1;
      }

      return var2;
   }

   @EventTarget(0)
   public void method4431(MoveInputEvent var1) {
      if (!this.method4413() && this.method4405() != null) {
         var1.method3038(true);
      }
   }

   public static List<BlockPos> getBlocksInZone(BlockPos var0, BlockPos var1) {
      ArrayList<BlockPos> var2 = new ArrayList<>();
      int var3 = Math.min(var0.getX(), var1.getX());
      int var4 = Math.max(var0.getX(), var1.getX());
      int var5 = Math.min(var0.getY(), var1.getY());
      int var6 = Math.max(var0.getY(), var1.getY());
      int var7 = Math.min(var0.getZ(), var1.getZ());
      int var8 = Math.max(var0.getZ(), var1.getZ());

      for (int var9 = var3; var9 <= var4; var9++) {
         for (int var10 = var5; var10 <= var6; var10++) {
            for (int var11 = var7; var11 <= var8; var11++) {
               var2.add(new BlockPos(var9, var10, var11));
            }
         }
      }

      return var2;
   }

   private static final class Entry {
      private final CropFarmerModule.Type2 type2;
      private final long longValue;

      private Entry(CropFarmerModule.Type2 var1, long var2) {
         this.type2 = var1;
         this.longValue = var2;
      }
   }

   private static final class Entry2 {
      private final List<BlockPos> items;
      private final List<BitSet> items2;
      private final List<List<Integer>> items3;
      private final Map<BitSet, Integer> items4 = new HashMap<>();
      private final long longValue;
      private List<Integer> items5;

      private Entry2(List<BlockPos> var1, List<BitSet> var2, int var3, long var4) {
         this.items = var1;
         this.items2 = var2;
         this.longValue = var4;
         this.items3 = new ArrayList<>(var3);

         for (int var6 = 0; var6 < var3; var6++) {
            this.items3.add(new ArrayList<>());
         }

         for (int var8 = 0; var8 < var2.size(); var8++) {
            for (int var7 = ((BitSet)var2.get(var8)).nextSetBit(0); var7 >= 0; var7 = ((BitSet)var2.get(var8)).nextSetBit(var7 + 1)) {
               this.items3.get(var7).add(var8);
            }
         }
      }

      private List<BlockPos> method4433() {
         BitSet var1 = new BitSet(this.items3.size());
         var1.set(0, this.items3.size());
         this.items5 = this.method4435(var1);
         this.method4434(var1, new ArrayList<>());
         return this.items5.stream().map(this.items::get).toList();
      }

      private void method4434(BitSet var1, List<Integer> var2) {
         if (System.nanoTime() < this.longValue && var2.size() < this.items5.size()) {
            if (var1.isEmpty()) {
               this.items5 = new ArrayList<>(var2);
            } else {
               Integer var3 = this.items4.get(var1);
               if (var3 == null || var3 > var2.size()) {
                  this.items4.put((BitSet)var1.clone(), var2.size());
                  int var4 = 0;

                  for (BitSet var6 : this.items2) {
                     var4 = Math.max(var4, this.method4437(var6, var1));
                  }

                  if (var4 != 0 && var2.size() + (var1.cardinality() + var4 - 1) / var4 < this.items5.size()) {
                     int var10 = this.method4436(var1);
                     ArrayList<Integer> var11 = new ArrayList<>(this.items3.get(var10));
                     var11.sort(Comparator.<Integer>comparingInt(var2x -> this.method4437(this.items2.get(var2x), var1)).reversed());

                     for (Object var8_o : (Iterable<?>)(Object)(var11)) {
                        int var8 = (Integer)var8_o;
                        BitSet var9 = (BitSet)var1.clone();
                        var9.andNot(this.items2.get(var8));
                        var2.add(var8);
                        this.method4434(var9, var2);
                        var2.remove(var2.size() - 1);
                        if (System.nanoTime() >= this.longValue) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      private List<Integer> method4435(BitSet var1) {
         BitSet var2 = (BitSet)var1.clone();
         ArrayList<Integer> var3 = new ArrayList<>();

         while (!var2.isEmpty()) {
            int var4 = -1;
            int var5 = 0;

            for (int var6 = 0; var6 < this.items2.size(); var6++) {
               int var7 = this.method4437(this.items2.get(var6), var2);
               if (var7 > var5) {
                  var5 = var7;
                  var4 = var6;
               }
            }

            if (var4 == -1) {
               break;
            }

            var3.add(var4);
            var2.andNot(this.items2.get(var4));
         }

         return var3;
      }

      private int method4436(BitSet var1) {
         int var2 = var1.nextSetBit(0);
         int var3 = Integer.MAX_VALUE;

         for (int var4 = var2; var4 >= 0; var4 = var1.nextSetBit(var4 + 1)) {
            int var5 = this.items3.get(var4).size();
            if (var5 < var3) {
               var3 = var5;
               var2 = var4;
            }
         }

         return var2;
      }

      private int method4437(BitSet var1, BitSet var2) {
         BitSet var3 = (BitSet)var1.clone();
         var3.and(var2);
         return var3.cardinality();
      }
   }

   private static enum Type {
      NETHER_WART("module.cropFarmer.crop.netherWart", Items.NETHER_WART, Items.NETHER_WART, Blocks.SOUL_SAND, 0),
      CARROT("module.cropFarmer.crop.carrot", Items.CARROT, Items.CARROT, Blocks.FARMLAND, 9),
      POTATO("module.cropFarmer.crop.potato", Items.POTATO, Items.POTATO, Blocks.FARMLAND, 9),
      BEETROOT("module.cropFarmer.crop.beetroot", Items.BEETROOT_SEEDS, Items.BEETROOT, Blocks.FARMLAND, 9),
      WHEAT("module.cropFarmer.crop.wheat", Items.WHEAT_SEEDS, Items.WHEAT, Blocks.FARMLAND, 9),
      TORCHFLOWER("module.cropFarmer.crop.torchflower", Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, Blocks.FARMLAND, 9),
      PITCHER_PLANT("module.cropFarmer.crop.pitcherPlant", Items.PITCHER_POD, Items.PITCHER_PLANT, Blocks.FARMLAND, 8);

      private final String text;
      private final Item item;
      private final Item item2;
      private final Block block;
      private final int intValue;
      private static final CropFarmerModule.Type[] types = values();

      private Type(String var3, Item var4, Item var5, Block var6, int var7) {
         this.text = var3;
         this.item = var4;
         this.item2 = var5;
         this.block = var6;
         this.intValue = var7;
      }

      private static CropFarmerModule.Type method4432(String var0) {
         for (CropFarmerModule.Type var4 : types) {
            if (var4.text.equals(var0)) {
               return var4;
            }
         }

         return NETHER_WART;
      }
   }

   private static enum Type2 {
      BREAK,
      TILL,
      PLACE;
   }
}

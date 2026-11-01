package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.display.base.CustomDrawContext;

@ModuleInfo(
   name = "BlockESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.blockesp.description"
)
public final class BlockESPModule extends Module {
   private final Map<Long, BlockESPModule.Entry> items = new ConcurrentHashMap<>();
   private int intValue = -1;
   private ClientWorld clientWorld;
   private boolean flag;
   private final BlockListSetting blockListSetting = new BlockListSetting(
      "module.blockESP.itemSelectSetting", "module.blockESP.itemSelectSetting.desc", new ArrayList<>(), () -> true
   );
   private final ModeSetting mode = new ModeSetting("module.blockESP.mode", "module.blockESP.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.blockESP.onlyUpdate").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.blockESP.all");
   private final SliderSetting range = new SliderSetting(
      "module.blockESP.range", 8.0F, 1.0F, 32.0F, 1.0F, "module.blockESP.range.desc", "ch", this.entry2::isSelected, null
   );
   private final BooleanSetting tracers = new BooleanSetting("module.blockESP.tracers", "module.blockESP.tracers.desc", false);
   public static final BlockESPModule INSTANCE = new BlockESPModule();
   private static final float CONST_floatValue = 45.0F;
   private static final float CONST_floatValue2 = 30.0F;
   private static final float CONST_floatValue3 = 10.0F;

   private BlockESPModule() {
      this.blockListSetting.method1617(Blocks.DIAMOND_ORE);
   }

   @Override
   public void onEnable() {
      this.items.clear();
      this.intValue = this.blockListSetting.getVersion();
      this.flag = this.entry2.isSelected();
      super.onEnable();
      if (this.isEnabled() && this.entry2.isSelected()) {
         this.method7567();
      }
   }

   @Override
   public void onDisable() {
      this.items.clear();
      this.clientWorld = null;
      super.onDisable();
   }

   public void method7560(Chunk var1, int var2, int var3) {
      if (this.isEnabled() && this.entry2.isSelected()) {
         this.method7569(var1, var2, var3, this.method7568(new HashMap<>()));
      }
   }

   public void method7561(int var1, int var2) {
      this.items.remove(ChunkPos.toLong(var1, var2));
   }

   @EventTarget
   public void method7562(UpdateEvent var1) {
      ClientWorld var2 = mc.world;
      if (mc.player == null || var2 == null) {
         this.items.clear();
         this.clientWorld = null;
      } else if (var2 != this.clientWorld) {
         this.clientWorld = var2;
         this.items.clear();
         if (this.entry2.isSelected()) {
            this.method7567();
         }
      } else {
         boolean var3 = this.entry2.isSelected();
         if (var3 && !this.flag) {
            this.flag = true;
            this.method7567();
         } else {
            this.flag = var3;
            if (this.blockListSetting.getVersion() != this.intValue) {
               this.intValue = this.blockListSetting.getVersion();
               this.method7564();
               if (var3) {
                  this.method7567();
               }
            }

            this.method7563(var2);
         }
      }
   }

   private void method7563(ClientWorld var1) {
      if (!this.items.isEmpty()) {
         this.items.values().removeIf(var1x -> !var1.isChunkLoaded(var1x.intValue, var1x.intValue2));
      }
   }

   private void method7564() {
      for (BlockESPModule.Entry var2 : this.items.values()) {
         var2.items.values().removeIf(var1 -> !this.blockListSetting.method1621(var1.getBlock()));
      }

      this.items.values().removeIf(var0 -> var0.items.isEmpty());
   }

   @EventTarget
   public void method7565(PacketEvent var1) {
      if (var1.method7100()) {
         if (var1.getPacket3() instanceof ChunkDeltaUpdateS2CPacket var2) {
            var2.visitUpdates((var1x, var2x) -> this.method7566(var1x.toImmutable(), var2x.getBlock()));
         }

         if (var1.getPacket3() instanceof BlockUpdateS2CPacket var4) {
            this.method7566(var4.getPos().toImmutable(), var4.getState().getBlock());
         }
      }
   }

   private void method7566(BlockPos var1, Block var2) {
      int var3 = var1.getX() >> 4;
      int var4 = var1.getZ() >> 4;
      long var5 = ChunkPos.toLong(var3, var4);
      if (this.method7570(var2)) {
         this.items.computeIfAbsent(var5, var2x -> new BlockESPModule.Entry(var3, var4)).items.put(var1, new BlockESPModule.Data(var2, this.method7576(var2)));
      } else {
         BlockESPModule.Entry var7 = this.items.get(var5);
         if (var7 != null) {
            var7.items.remove(var1);
         }
      }
   }

   private void method7567() {
      ClientWorld var1 = mc.world;
      ClientPlayerEntity var2 = mc.player;
      if (var1 != null && var2 != null) {
         int var3 = Math.max((int)this.range.getValue3(), mc.options.getViewDistance().getValue() + 3);
         int var4 = var2.getBlockX() >> 4;
         int var5 = var2.getBlockZ() >> 4;
         var var6 = this.method7568(new HashMap<>());

         for (int var7 = var4 - var3; var7 <= var4 + var3; var7++) {
            for (int var8 = var5 - var3; var8 <= var5 + var3; var8++) {
               Chunk var9 = var1.getChunk(var7, var8, ChunkStatus.FULL, false);
               if (var9 != null) {
                  this.method7569(var9, var7, var8, var6);
               }
            }
         }
      }
   }

   private Predicate<BlockState> method7568(Map<Block, Boolean> var1) {
      return var2 -> var1.computeIfAbsent(var2.getBlock(), this::method7570);
   }

   private void method7569(Chunk var1, int var2, int var3, Predicate<BlockState> var4) {
      BlockESPModule.Entry var5 = new BlockESPModule.Entry(var2, var3);
      long var6 = ChunkPos.toLong(var2, var3);

      try {
         ChunkSection[] var8 = var1.getSectionArray();
         int var9 = var1.getBottomSectionCoord();
         int var10 = var2 << 4;
         int var11 = var3 << 4;

         for (int var12 = 0; var12 < var8.length; var12++) {
            ChunkSection var13 = var8[var12];
            if (var13 != null && !var13.isEmpty() && var13.hasAny(var4)) {
               int var14 = var9 + var12 << 4;

               for (int var15 = 0; var15 < 16; var15++) {
                  for (int var16 = 0; var16 < 16; var16++) {
                     for (int var17 = 0; var17 < 16; var17++) {
                        BlockState var18 = var13.getBlockState(var15, var17, var16);
                        if (var4.test(var18)) {
                           Block var19 = var18.getBlock();
                           var5.items.put(new BlockPos(var10 + var15, var14 + var17, var11 + var16), new BlockESPModule.Data(var19, this.method7576(var19)));
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var20) {
         return;
      }

      if (var5.items.isEmpty()) {
         this.items.remove(var6);
      } else {
         this.items.put(var6, var5);
      }
   }

   private boolean method7570(Block var1) {
      return !(var1 instanceof AirBlock) && this.blockListSetting.method1621(var1);
   }

   @EventTarget
   public void method7571(WorldRenderEvent var1) {
      if (mc.world != null && mc.player != null) {
         boolean var2 = this.entry2.isSelected();
         int var3 = (int)this.range.getValue3();
         int var4 = mc.player.getBlockX() >> 4;
         int var5 = mc.player.getBlockZ() >> 4;
         Vec3d var6 = this.tracers.isEnabled() ? this.method7572() : null;
         Frustum var7 = FrustumHolder.getFrustum();
         int var8 = mc.world.getBottomY();
         int var9 = mc.world.getTopYInclusive() + 1;

         for (BlockESPModule.Entry var11 : this.items.values()) {
            if (!var11.items.isEmpty() && (!var2 || Math.abs(var11.intValue - var4) <= var3 && Math.abs(var11.intValue2 - var5) <= var3)) {
               int var12 = var11.intValue << 4;
               int var13 = var11.intValue2 << 4;
               boolean var14 = var7 == null || var7.isVisible(new Box(var12, var8, var13, var12 + 16, var9, var13 + 16));
               if (var14 || var6 != null) {
                  for (Map.Entry var16 : var11.items.entrySet()) {
                     int var17 = ((BlockESPModule.Data)var16.getValue()).color();
                     if (var17 != 0) {
                        BlockPos var18 = (BlockPos)var16.getKey();
                        if (var14) {
                           GlowUtil.method2230(new Box(var18), var17, 1.0F);
                        }

                        if (var6 != null) {
                           GlowUtil.method2245(var6, var18.toCenterPos(), var17, 1.0F, false);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Vec3d method7572() {
      return new Vec3d(0.0, 0.0, 75.0)
         .rotateX(-((float)Math.toRadians(mc.gameRenderer.getCamera().getPitch())))
         .rotateY(-((float)Math.toRadians(mc.gameRenderer.getCamera().getYaw())))
         .add(FreeCamModule.INSTANCE.method1660());
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.BLOCK;
   }

   public void method7573(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      if (var1 != null) {
         int var6 = this.method7576(Blocks.DIRT);
         if (var6 != 0) {
            if (this.tracers.isEnabled()) {
               this.method7575(var1, var2, var5, var2, var3, 1.2F, ZenithColor.method7291(var6).method7294(0.9F));
            }

            float var7 = var4 / 10.0F;
            float var8 = 16.0F * var7;
            var1.pushMatrix();
            var1.getMatrices().translate(var2 - var8 / 2.0F, var3 - var8 / 2.0F);
            var1.getMatrices().scale(var7, var7);
            var1.drawItem(Items.DIRT.getDefaultStack(), 0, 0);
            var1.popMatrix();
            this.method7574(var1, var2, var3, var4, ZenithColor.method7291(var6));
         }
      }
   }

   private void method7574(CustomDrawContext var1, float var2, float var3, float var4, ZenithColor var5) {
      float var6 = (float)Math.toRadians(45.0);
      float var7 = (float)Math.toRadians(30.0);
      float var8 = (float)Math.cos(var6);
      float var9 = (float)Math.sin(var6);
      float var10 = (float)Math.cos(var7);
      float var11 = (float)Math.sin(var7);
      float var12 = 0.5F;
      float[][] var13 = new float[][]{
         {-var12, -var12, -var12},
         {var12, -var12, -var12},
         {var12, -var12, var12},
         {-var12, -var12, var12},
         {-var12, var12, -var12},
         {var12, var12, -var12},
         {0.5F, 0.5F, 0.5F},
         {-var12, var12, var12}
      };
      float[][] var14 = new float[8][2];

      for (int var15 = 0; var15 < 8; var15++) {
         float var16 = var13[var15][0] * var8 + var13[var15][2] * var9;
         float var17 = -var13[var15][0] * var9 + var13[var15][2] * var8;
         var14[var15][0] = var2 + var16 * var4;
         var14[var15][1] = var3 - (var13[var15][1] * var10 - var17 * var11) * var4;
      }

      for (int var18 = 0; var18 < 4; var18++) {
         int var19 = (var18 + 1) % 4;
         this.method7575(var1, var14[var18][0], var14[var18][1], var14[var19][0], var14[var19][1], 1.0F, var5);
         this.method7575(var1, var14[var18 + 4][0], var14[var18 + 4][1], var14[var19 + 4][0], var14[var19 + 4][1], 1.0F, var5);
         this.method7575(var1, var14[var18][0], var14[var18][1], var14[var18 + 4][0], var14[var18 + 4][1], 1.0F, var5);
      }
   }

   private void method7575(CustomDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7) {
      float var8 = var4 - var2;
      float var9 = var5 - var3;
      float var10 = (float)Math.hypot(var8, var9);
      if (!(var10 < 0.01F)) {
         var1.pushMatrix();
         var1.getMatrices().translate(var2, var3);
         var1.getMatrices().rotate((float)Math.atan2(var9, var8));
         var1.drawRect(0.0F, -var6 / 2.0F, var10, var6, var7);
         var1.popMatrix();
      }
   }

   private int method7576(Block var1) {
      if (var1 == Blocks.DIAMOND_ORE || var1 == Blocks.DEEPSLATE_DIAMOND_ORE) {
         return Color.cyan.getRGB();
      } else if (var1 == Blocks.GOLD_ORE || var1 == Blocks.DEEPSLATE_GOLD_ORE || var1 == Blocks.NETHER_GOLD_ORE) {
         return 55040;
      } else if (var1 == Blocks.EMERALD_ORE || var1 == Blocks.DEEPSLATE_EMERALD_ORE) {
         return -16711859;
      } else if (var1 == Blocks.IRON_ORE || var1 == Blocks.DEEPSLATE_IRON_ORE) {
         return -2763307;
      } else if (var1 == Blocks.REDSTONE_ORE || var1 == Blocks.DEEPSLATE_REDSTONE_ORE) {
         return -65536;
      } else if (var1 == Blocks.ANCIENT_DEBRIS) {
         return -1;
      } else {
         MapColor var2 = var1.getDefaultMapColor();
         return var2 != null ? new Color(var2.color).getRGB() : 0;
      }
   }

   private static final class Data {
      private final Block block;
      private final int intValue;

      public Block block() {
         return this.block;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.block, other.block) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(block, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "block=" + this.block + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(Block var1, int var2) {
         this.block = var1;
         this.intValue = var2;
      }

      public Block getBlock() {
         return this.block;
      }

      public int color() {
         return this.intValue;
      }
   }

   private static final class Entry {
      private final int intValue;
      private final int intValue2;
      private final Map<BlockPos, BlockESPModule.Data> items = new ConcurrentHashMap<>();

      private Entry(int var1, int var2) {
         this.intValue = var1;
         this.intValue2 = var2;
      }
   }
}

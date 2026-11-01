package org.zenith.base.bot.view;

import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.base.bot.world.BotWorld;

public final class BotSectionMesher {
   private static final int INITIAL_LAYER_BUFFER_SIZE = 65536;

   private BotSectionMesher() {
   }

   public static BotSectionMesher.MeshResult build(BotWorld var0, long var1) {
      int var3 = ChunkSectionPos.unpackX(var1);
      int var4 = ChunkSectionPos.unpackY(var1);
      int var5 = ChunkSectionPos.unpackZ(var1);
      WorldChunk var6 = var0.getChunkManager().getChunk(var3, var5, null, false);
      if (var6 == null) {
         return new BotSectionMesher.MeshResult(var1, List.of(), List.of(), true);
      } else {
         int var7 = var0.sectionCoordToIndex(var4);
         if (var7 >= 0 && var7 < var6.getSectionArray().length && !var6.getSectionArray()[var7].isEmpty()) {
            BlockRenderManager var8 = MinecraftClient.getInstance().getBlockRenderManager();
            BlockPos var9 = new BlockPos(var3 << 4, var4 << 4, var5 << 4);
            BlockPos var10 = var9.add(15, 15, 15);
            HashMap var11 = new HashMap();
            HashMap var12 = new HashMap();
            ArrayList var13 = new ArrayList();
            MatrixStack var14 = new MatrixStack();
            Random var15 = Random.create();
            ArrayList var16 = new ArrayList();
            BlockModelRenderer.enableBrightnessCache();

            try {
               for (BlockPos var29 : BlockPos.iterate(var9, var10)) {
                  BlockState var31 = var0.getBlockState(var29);
                  if (!var31.isAir()) {
                     if (var31.hasBlockEntity()) {
                        BlockEntity var20 = var0.getBlockEntity(var29);
                        if (var20 != null) {
                           var13.add(var20);
                        }
                     }

                     FluidState var33 = var31.getFluidState();
                     if (!var33.isEmpty()) {
                        BlockRenderLayer var21 = BlockRenderLayers.getFluidLayer(var33);
                        BufferBuilder var22 = beginLayer(var11, var12, var21);
                        var8.renderFluid(var29, var0, var22, var31, var33);
                     }

                     if (var31.getRenderType() == BlockRenderType.MODEL) {
                        BlockRenderLayer var35 = BlockRenderLayers.getBlockLayer(var31);
                        BufferBuilder var37 = beginLayer(var11, var12, var35);
                        var14.push();
                        var14.translate((float)(var29.getX() & 15), (float)(var29.getY() & 15), (float)(var29.getZ() & 15));
                        var16.clear();
                        var15.setSeed(var31.getRenderingSeed(var29));
                        var8.getModels().getModel(var31).addParts(var15, var16);
                        var8.renderBlock(var31, var29, var0, var14, var37, true, var16);
                        var14.pop();
                     }
                  }
               }
            } catch (Throwable var26) {
               for (Object var19_o : (Iterable<?>)(Object)(var12.values())) {
                  BufferAllocator var19 = (BufferAllocator)var19_o;
                  var19.close();
               }

               throw var26;
            } finally {
               BlockModelRenderer.disableBrightnessCache();
            }

            List<LayerMesh> var28 = new ArrayList<>(var11.size());

            for (Object var32_o : (Iterable<?>)(Object)(var11.entrySet())) {
               Entry var32 = (Entry)var32_o;
               BuiltBuffer var34 = ((BufferBuilder)var32.getValue()).endNullable();
               BufferAllocator var36 = (BufferAllocator)var12.get(var32.getKey());
               if (var34 != null) {
                  var28.add(new BotSectionMesher.LayerMesh((BlockRenderLayer)var32.getKey(), var34, var36));
               } else {
                  var36.close();
               }
            }

            return new BotSectionMesher.MeshResult(var1, var28, var13, false);
         } else {
            return new BotSectionMesher.MeshResult(var1, List.of(), List.of(), false);
         }
      }
   }

   private static BufferBuilder beginLayer(Map<BlockRenderLayer, BufferBuilder> var0, Map<BlockRenderLayer, BufferAllocator> var1, BlockRenderLayer var2) {
      BufferBuilder var3 = (BufferBuilder)var0.get(var2);
      if (var3 == null) {
         BufferAllocator var4 = new BufferAllocator(Math.min(var2.getBufferSize(), 65536));
         var1.put(var2, var4);
         var3 = new BufferBuilder(var4, DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
         var0.put(var2, var3);
      }

      return var3;
   }

   public static final class LayerMesh {
      private final BlockRenderLayer layer;
      private final BuiltBuffer buffer;
      private final BufferAllocator allocator;

      public BlockRenderLayer layer() {
         return this.layer;
      }

      public BuiltBuffer buffer() {
         return this.buffer;
      }

      public BufferAllocator allocator() {
         return this.allocator;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof LayerMesh other)) return false;
         return java.util.Objects.equals(this.layer, other.layer) && java.util.Objects.equals(this.buffer, other.buffer) && java.util.Objects.equals(this.allocator, other.allocator);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(layer, buffer, allocator);
      }

      @Override
      public String toString() {
         return "LayerMesh[" + "layer=" + this.layer + ", " + "buffer=" + this.buffer + ", " + "allocator=" + this.allocator + "]";
      }

      public LayerMesh(BlockRenderLayer var1, BuiltBuffer var2, BufferAllocator var3) {
         this.layer = var1;
         this.buffer = var2;
         this.allocator = var3;
      }
   }

   public static final class MeshResult {
      private final long sectionPos;
      private final List<BotSectionMesher.LayerMesh> layers;
      private final List<BlockEntity> blockEntities;
      private final boolean chunkMissing;

      public long sectionPos() {
         return this.sectionPos;
      }

      public List<BotSectionMesher.LayerMesh> layers() {
         return this.layers;
      }

      public List<BlockEntity> blockEntities() {
         return this.blockEntities;
      }

      public boolean chunkMissing() {
         return this.chunkMissing;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof MeshResult other)) return false;
         return java.util.Objects.equals(this.sectionPos, other.sectionPos) && java.util.Objects.equals(this.layers, other.layers) && java.util.Objects.equals(this.blockEntities, other.blockEntities) && java.util.Objects.equals(this.chunkMissing, other.chunkMissing);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(sectionPos, layers, blockEntities, chunkMissing);
      }

      @Override
      public String toString() {
         return "MeshResult[" + "sectionPos=" + this.sectionPos + ", " + "layers=" + this.layers + ", " + "blockEntities=" + this.blockEntities + ", " + "chunkMissing=" + this.chunkMissing + "]";
      }

      public MeshResult(long var1, List<BotSectionMesher.LayerMesh> var3, List<BlockEntity> var4, boolean var5) {
         this.sectionPos = var1;
         this.layers = var3;
         this.blockEntities = var4;
         this.chunkMissing = var5;
      }
   }
}

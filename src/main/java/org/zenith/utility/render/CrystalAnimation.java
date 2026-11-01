package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix3f;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;

public final class CrystalAnimation implements MinecraftAccess {
   private static final int CONST_intValue = -1;
   private static final int CONST_intValue2 = 6;
   private static final double CONST_doubleValue = 9216.0;
   private static final float CONST_floatValue = 2.0F;
   private static final float CONST_floatValue2 = 6.2F;
   private static volatile boolean flag;
   private static final List<BlockPos> items = new ArrayList<>();
   private static final Map<Class15, int[]> items2 = new IdentityHashMap<>();
   private int intValue;

   public CrystalAnimation() {
      EventManager.register(this);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1 -> this.tick());
   }

   public static boolean method4460(BlockState var0) {
      return flag && var0.isOf(Blocks.RESPAWN_ANCHOR);
   }

   private static HoveredData method4461(FocusSlotEnum var0) {
      Wardrobe var1 = Wardrobe.method2488();
      return var1 != null && var1.showSelf.isEnabled() ? var1.method2491(var0) : null;
   }

   private static Class15 method4462(HoveredData var0) {
      Wardrobe var1 = Wardrobe.method2488();
      return var0 != null && var1 != null ? var1.getRegistry().method1427(var0) : null;
   }

   public static boolean method4463(EndCrystalEntityRenderState var0, MatrixStack var1, OrderedRenderCommandQueue var2) {
      HoveredData var3 = method4461(FocusSlotEnum.CRYSTAL);
      Class15 var4 = method4462(var3);
      if (var4 == null) {
         return false;
      } else {
         float var5 = var0.age / 20.0F;
         int var6 = (int)(Double.doubleToLongBits(var0.x * 31.0 + var0.z) ^ Double.doubleToLongBits(var0.y));
         AnimationCache.Entry var7 = AnimationCache.method7621(var6, var3, false);
         var7.animationState.method6539(var4, "idle", var5);
         float[] var8 = var7.class16.method2139(var4, var7.animationState, var5, null);
         var1.push();

         try {
            var1.translate(0.0F, 0.5F, 0.0F);
            var1.scale(0.5F, 0.5F, 0.5F);
            var1.translate(0.0F, 0.775F, 0.0F);
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            var1.scale(2.0F, 2.0F, 2.0F);
            BoneMatrices.method4787(var4, var8, 6, var1, var2, var0.light, OverlayTexture.DEFAULT_UV, true, var3.getRarity().isFlag(), -1);
         } finally {
            var1.pop();
         }

         return true;
      }
   }

   private void tick() {
      boolean var1 = mc.world != null && mc.player != null && method4462(method4461(FocusSlotEnum.ANCHOR)) != null;
      if (!var1) {
         if (flag) {
            flag = false;
            if (mc.world != null && mc.player != null) {
               method4464(Math.max(6, mc.options.getClampedViewDistance() + 1));
            }

            rebuild();
         }

         items.clear();
      } else if (!flag) {
         flag = true;
         method4464(6);
         rebuild();
         this.intValue = 0;
      } else {
         if (++this.intValue >= 5) {
            this.intValue = 0;
            method4464(6);
         }
      }
   }

   private static void rebuild() {
      if (mc.worldRenderer != null && mc.world != null) {
         for (BlockPos var1 : items) {
            mc.worldRenderer.scheduleBlockRenders(var1.getX(), var1.getY(), var1.getZ(), var1.getX(), var1.getY(), var1.getZ());
         }
      }
   }

   private static void method4464(int var0) {
      items.clear();
      if (mc.world != null && mc.player != null) {
         ChunkPos var1 = mc.player.getChunkPos();

         for (int var2 = var1.x - var0; var2 <= var1.x + var0; var2++) {
            for (int var3 = var1.z - var0; var3 <= var1.z + var0; var3++) {
               WorldChunk var4 = mc.world.getChunkManager().getChunk(var2, var3, ChunkStatus.FULL, false);
               if (var4 != null) {
                  ChunkSection[] var5 = var4.getSectionArray();

                  for (int var6 = 0; var6 < var5.length; var6++) {
                     ChunkSection var7 = var5[var6];
                     if (var7 != null && !var7.isEmpty() && var7.hasAny(var0x -> var0x.isOf(Blocks.RESPAWN_ANCHOR))) {
                        int var8 = var4.sectionIndexToCoord(var6) << 4;

                        for (int var9 = 0; var9 < 16; var9++) {
                           for (int var10 = 0; var10 < 16; var10++) {
                              for (int var11 = 0; var11 < 16; var11++) {
                                 if (var7.getBlockState(var11, var9, var10).isOf(Blocks.RESPAWN_ANCHOR)) {
                                    items.add(new BlockPos((var2 << 4) + var11, var8 + var9, (var3 << 4) + var10));
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

   private static int[] method4465(Class15 var0) {
      return items2.computeIfAbsent(var0, var1 -> {
         ArrayList var2 = new ArrayList();

         for (int var3 = 0; var3 < var0.texts.length; var3++) {
            if (var0.texts[var3].startsWith("charge")) {
               var2.add(var3);
            }
         }

         return var2.stream().mapToInt(x -> (Integer)x).toArray();
      });
   }

   @EventTarget
   public void method4466(WorldRenderEvent var1) {
      if (flag && !items.isEmpty() && mc.world != null) {
         HoveredData var2 = method4461(FocusSlotEnum.ANCHOR);
         Class15 var3 = method4462(var2);
         if (var3 != null) {
            Vec3d var4 = mc.gameRenderer.getCamera().getCameraPos();
            float var5 = ((float)mc.world.getTime() + var1.getFloatValue()) / 20.0F;
            int[] var6 = method4465(var3);
            Immediate var7 = mc.getBufferBuilders().getEntityVertexConsumers();
            Entry var8 = var1.getMatrixStack().peek();
            Matrix3f var9 = new Matrix3f(var8.getPositionMatrix());
            boolean var10 = false;

            for (BlockPos var12 : items) {
               double var13 = var12.getX() + 0.5 - var4.x;
               double var15 = var12.getY() + 0.5 - var4.y;
               double var17 = var12.getZ() + 0.5 - var4.z;
               if (!(var13 * var13 + var15 * var15 + var17 * var17 > 9216.0)) {
                  BlockState var19 = mc.world.getBlockState(var12);
                  if (var19.isOf(Blocks.RESPAWN_ANCHOR)) {
                     AnimationCache.Entry var20 = AnimationCache.method7621(var12.hashCode(), var2, false);
                     var20.animationState.method6539(var3, "idle", var5);
                     float[] var21 = var20.class16.method2139(var3, var20.animationState, var5, null);
                     int var22 = var19.get(RespawnAnchorBlock.CHARGES);

                     for (int var23 = var22; var23 < var6.length; var23++) {
                        int var24 = var6[var23] * 9;
                        var21[var24 + 6] = 0.0F;
                        var21[var24 + 7] = 0.0F;
                        var21[var24 + 8] = 0.0F;
                     }

                     int var25 = WorldRenderer.getLightmapCoordinates(mc.world, var12.up());
                     MatrixStack var26 = new MatrixStack();
                     var26.peek().getPositionMatrix().set(var8.getPositionMatrix());
                     var26.peek().getNormalMatrix().set(var9);
                     var26.translate(var13, var15, var17);
                     var26.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
                     BoneMatrices.method4793(var3, var21, 6, var26, var7, var25, OverlayTexture.DEFAULT_UV, true, var2.getRarity().isFlag(), -1);
                     var10 = true;
                  }
               }
            }

            if (var10) {
               var7.draw();
            }
         }
      }
   }
}

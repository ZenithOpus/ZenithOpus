package org.zenith.utility.render;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.lang.ref.WeakReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.utility.misc.OptimizationFlags;

public final class FluidColorCache {
   private static final Long2ObjectOpenHashMap<FluidState> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   private static long longValue = Long.MIN_VALUE;
   private static WeakReference<ClientWorld> weakReference = new WeakReference<>(null);

   private FluidColorCache() {
   }

   public static double method7101(double var0, double var2, double var4) {
      MinecraftClient var6 = MinecraftClient.getInstance();
      if (var6.gameRenderer == null) {
         return 0.0;
      } else {
         Vec3d var7 = var6.gameRenderer.getCamera().getCameraPos();
         if (var7 == null) {
            return 0.0;
         } else {
            double var8 = var0 - var7.x;
            double var10 = var2 - var7.y;
            double var12 = var4 - var7.z;
            return var8 * var8 + var10 * var10 + var12 * var12;
         }
      }
   }

   public static boolean method7102(Object var0, double var1) {
      double var3 = OptimizationFlags.doubleValue;
      if (var1 < var3) {
         return true;
      } else {
         int var5 = System.identityHashCode(var0) * -1640531527 >>> 28;
         return var1 < var3 * 4.0 ? (var5 & 1) == 0 : (var5 & 3) == 0;
      }
   }

   public static FluidState method7103(ClientWorld var0, BlockPos var1) {
      long var2 = var0.getTime();
      ClientWorld var4 = weakReference.get();
      if (var2 == longValue && var4 == var0) {
         return (FluidState)long2ObjectOpenHashMap.get(var1.asLong());
      } else {
         long2ObjectOpenHashMap.clear();
         longValue = var2;
         if (var4 != var0) {
            weakReference = new WeakReference<>(var0);
         }

         return null;
      }
   }

   public static void method7104(BlockPos var0, FluidState var1) {
      long2ObjectOpenHashMap.put(var0.asLong(), var1);
   }
}

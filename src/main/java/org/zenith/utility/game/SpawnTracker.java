package org.zenith.utility.game;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.UpdateEvent;

public final class SpawnTracker implements MinecraftAccess {
   public static final double CONST_doubleValue = -66.0;
   private static final double CONST_doubleValue2 = 80.0;
   private static final double CONST_doubleValue3 = 200.0;
   private static final double CONST_doubleValue4 = 0.001;
   private static final double CONST_doubleValue5 = 0.5;
   private static final double CONST_doubleValue6 = 16.0;
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 100;
   private static final int CONST_intValue3 = 12000;
   private static final String text = "minecraft:world_spawn";
   private static final SpawnTracker INSTANCE = new SpawnTracker();
   private static Vec3d vec3d;
   private static Vec3d vec3d2;
   private static int cooldown;
   private static int intValue;
   private static int intValue2;
   private static boolean flag;
   private static boolean flag2;
   private static double doubleValue;
   private static double doubleValue2;
   private static Object owner;
   private static ClientWorld clientWorld;
   private static ClientPlayerEntity clientPlayerEntity;

   private SpawnTracker() {
   }

   public static boolean method6206() {
      return mc.player != null
         && mc.world != null
         && mc.player.isAlive()
         && !mc.player.isRemoved()
         && mc.player.getAbilities().flying
         && !"minecraft:world_spawn".equals(mc.world.getRegistryKey().getValue().toString());
   }

   public static boolean isActive() {
      if ((vec3d != null || flag) && (!method6206() || clientWorld != mc.world || clientPlayerEntity != mc.player)) {
         reset();
      }

      return vec3d != null || flag;
   }

   public static boolean method6207(Object var0) {
      return isActive() && owner == var0;
   }

   public static boolean method6208() {
      return mc.player != null && method6225(mc.player.getY());
   }

   public static void method6209(Object var0) {
      if (method6207(var0)) {
         reset();
      }
   }

   public static boolean method6210(Vec3d var0) {
      if (method6206() && method6232(var0) && method6236(var0)) {
         Vec3d var1 = mc.player.getEntityPos();
         Vec3d var2 = method6226(var1);
         return method6232(var2) && !method6227(var1, var2) && !method6227(var2, var0);
      } else {
         return false;
      }
   }

   public static boolean method6211(Object var0, Vec3d var1) {
      return method6221(var0) && method6206() && method6223(var0, var1);
   }

   public static boolean method6212(Entity var0) {
      reset();
      return var0 != null
         && method6206()
         && var0.getEntityWorld() == mc.world
         && !var0.isRemoved()
         && method6239(var0)
         && method6223(null, var0.getEntityPos());
   }

   public static boolean method6213(Object var0, double var1, double var3, boolean var5) {
      if (method6221(var0) && method6206() && method6232(new Vec3d(var1, -66.0, var3))) {
         method6222(var0);
         flag = true;
         flag2 = var5;
         doubleValue = var1;
         doubleValue2 = var3;
         Vec3d var6 = mc.player.getEntityPos();
         Vec3d var7 = method6226(var6);
         if (method6225(var6.y)) {
            vec3d2 = method6229(var6.x, var6.z);
         } else if (method6227(var6, var7)) {
            method6224(method6228(var6.x, var6.y, var6.z, -66.0));
         } else {
            method6224(var7);
            vec3d2 = method6229(var6.x, var6.z);
         }

         cooldown = 2;
         return true;
      } else {
         return false;
      }
   }

   public static boolean method6214(Object var0) {
      if (method6221(var0) && method6206()) {
         Vec3d var1 = mc.player.getEntityPos();
         Vec3d var2 = method6226(var1);
         if (method6232(var2) && !method6227(var1, var2)) {
            method6224(var2);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static Vec3d method6215(Vec3d var0) {
      if (method6238(var0)) {
         return var0;
      } else if (var0 != null && mc.player != null && mc.world != null && method6232(var0)) {
         for (int var1 = 1; var1 <= 2; var1++) {
            Vec3d var2 = var0.add(0.0, var1, 0.0);
            if (method6238(var2)) {
               return var2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @EventTarget(0)
   public void onUpdate(UpdateEvent var1) {
      if (isActive()) {
         if (++intValue <= 100 && ++intValue2 <= 12000) {
            if (cooldown > 0) {
               cooldown--;
            } else if (flag) {
               method6216();
            } else {
               if (method6232(vec3d) && method6236(vec3d) && !method6227(mc.player.getEntityPos(), vec3d)) {
                  method6224(vec3d);
               }

               reset();
            }
         } else {
            if (owner == null) {
               ChatLogger.method2022("Телепорт остановлен: маршрут недоступен или чанки не загрузились");
            }

            reset();
         }
      }
   }

   private static void method6216() {
      if (!method6232(new Vec3d(doubleValue, -66.0, doubleValue2))) {
         reset();
      } else if (!method6217()) {
         Vec3d var0 = mc.player.getEntityPos();
         if (Math.hypot(var0.x - doubleValue, var0.z - doubleValue2) > 0.5) {
            method6218(var0);
         } else if (flag2) {
            if (method6225(var0.y)) {
               reset();
            } else {
               method6224(method6228(var0.x, var0.y, var0.z, -66.0));
               cooldown = 2;
            }
         } else {
            method6219(var0);
         }
      }
   }

   private static boolean method6217() {
      if (vec3d2 == null) {
         return false;
      } else {
         Vec3d var0 = mc.player.getEntityPos();
         if (!method6232(vec3d2) || method6227(var0, vec3d2)) {
            vec3d2 = null;
            return true;
         } else if (!method6225(var0.y) && method6225(vec3d2.y)) {
            vec3d2 = null;
            method6218(var0);
            return true;
         } else {
            if (method6225(vec3d2.y)) {
               if (!method6235(var0.x, var0.z, vec3d2.x, vec3d2.z)) {
                  vec3d2 = method6230(var0.x, var0.z);
                  if (vec3d2 == null) {
                     return true;
                  }
               }
            } else if (!method6234(vec3d2.x, vec3d2.z)) {
               return true;
            }

            method6224(vec3d2);
            vec3d2 = null;
            cooldown = 2;
            return true;
         }
      }
   }

   private static void method6218(Vec3d var0) {
      if (!method6225(var0.y)) {
         method6224(method6228(var0.x, var0.y, var0.z, -66.0));
         cooldown = 2;
      } else {
         Vec3d var1 = method6230(var0.x, var0.z);
         if (var1 != null) {
            method6220(var1);
         }
      }
   }

   private static void method6219(Vec3d var0) {
      Double var1 = method6231(doubleValue, doubleValue2);
      if (var1 != null) {
         if (method6237(doubleValue, var0.y, doubleValue2) && Math.abs(var0.y - var1) <= 1.0) {
            reset();
         } else {
            Vec3d var2 = method6228(doubleValue, var0.y, doubleValue2, var1);
            if (method6225(var2.y)) {
               method6220(var2);
            } else {
               vec3d2 = var2;
               cooldown = 2;
            }
         }
      }
   }

   private static void method6220(Vec3d var0) {
      vec3d2 = var0;
      method6224(method6226(mc.player.getEntityPos()));
      cooldown = 2;
   }

   private static boolean method6221(Object var0) {
      if (var0 != null && isActive() && owner != var0) {
         return false;
      } else {
         reset();
         return true;
      }
   }

   private static void method6222(Object var0) {
      owner = var0;
      clientWorld = mc.world;
      clientPlayerEntity = mc.player;
   }

   private static boolean method6223(Object var0, Vec3d var1) {
      if (!method6210(var1)) {
         return false;
      } else {
         method6222(var0);
         vec3d = var1;
         cooldown = 2;
         method6224(method6226(mc.player.getEntityPos()));
         return true;
      }
   }

   private static void reset() {
      vec3d = null;
      vec3d2 = null;
      cooldown = 0;
      intValue = 0;
      intValue2 = 0;
      flag = false;
      flag2 = false;
      doubleValue = 0.0;
      doubleValue2 = 0.0;
      owner = null;
      clientWorld = null;
      clientPlayerEntity = null;
   }

   private static void method6224(Vec3d var0) {
      if (mc.player.getEntityPos().squaredDistanceTo(var0) > 1.0E-6) {
         intValue = 0;
      }

      mc.player.setVelocity(Vec3d.ZERO);
      mc.player.setPosition(var0.x, var0.y, var0.z);
   }

   private static boolean method6225(double var0) {
      return Math.abs(var0 - -66.0) <= 0.001;
   }

   private static Vec3d method6226(Vec3d var0) {
      return new Vec3d(var0.x, -66.0, var0.z);
   }

   private static boolean method6227(Vec3d var0, Vec3d var1) {
      return Math.hypot(var0.x - var1.x, var0.z - var1.z) > 80.001 || Math.abs(var0.y - var1.y) > 200.001;
   }

   private static Vec3d method6228(double var0, double var2, double var4, double var6) {
      double var8 = var6 - var2;
      return new Vec3d(var0, Math.abs(var8) <= 200.0 ? var6 : var2 + Math.copySign(200.0, var8), var4);
   }

   private static Vec3d method6229(double var0, double var2) {
      double var4 = doubleValue - var0;
      double var6 = doubleValue2 - var2;
      double var8 = Math.hypot(var4, var6);
      if (var8 <= 0.001) {
         return null;
      } else {
         return var8 <= 80.0 ? new Vec3d(doubleValue, -66.0, doubleValue2) : new Vec3d(var0 + var4 / var8 * 80.0, -66.0, var2 + var6 / var8 * 80.0);
      }
   }

   private static Vec3d method6230(double var0, double var2) {
      Vec3d var4 = method6229(var0, var2);
      if (var4 != null && !method6235(var0, var2, var4.x, var4.z)) {
         double var5 = var4.x - var0;
         double var7 = var4.z - var2;
         double var9 = Math.hypot(var5, var7);
         double var11 = var5 / var9;
         double var13 = var7 / var9;
         double var15 = 0.0;

         for (double var17 = 16.0; var17 < var9 && method6235(var0, var2, var0 + var11 * var17, var2 + var13 * var17); var17 += 16.0) {
            var15 = var17;
         }

         return var15 < 16.0 ? null : new Vec3d(var0 + var11 * var15, -66.0, var2 + var13 * var15);
      } else {
         return var4;
      }
   }

   private static Double method6231(double var0, double var2) {
      if (!method6234(var0, var2)) {
         return null;
      } else {
         int var4 = mc.world.getTopY(Type.WORLD_SURFACE, MathHelper.floor(var0), MathHelper.floor(var2));
         if (var4 <= mc.world.getBottomY() + 1) {
            return null;
         } else {
            while (var4 <= mc.world.getTopYInclusive()) {
               if (method6237(var0, var4, var2)) {
                  return (double)var4;
               }

               var4++;
            }

            return null;
         }
      }
   }

   private static boolean method6232(Vec3d var0) {
      if (var0 != null && Double.isFinite(var0.x) && Double.isFinite(var0.y) && Double.isFinite(var0.z)) {
         BlockPos var1 = BlockPos.ofFloored(var0);
         return World.isValid(var1) && mc.world.getWorldBorder().contains(var1);
      } else {
         return false;
      }
   }

   private static boolean method6233(int var0, int var1) {
      return mc.world.getChunkManager().getChunk(var0, var1, ChunkStatus.FULL, false) instanceof WorldChunk;
   }

   private static boolean method6234(double var0, double var2) {
      return method6233(MathHelper.floor(var0) >> 4, MathHelper.floor(var2) >> 4);
   }

   private static boolean method6235(double var0, double var2, double var4, double var6) {
      int var8 = Math.min(MathHelper.floor(var0), MathHelper.floor(var4)) >> 4;
      int var9 = Math.max(MathHelper.floor(var0), MathHelper.floor(var4)) >> 4;
      int var10 = Math.min(MathHelper.floor(var2), MathHelper.floor(var6)) >> 4;
      int var11 = Math.max(MathHelper.floor(var2), MathHelper.floor(var6)) >> 4;

      for (int var12 = var8; var12 <= var9; var12++) {
         for (int var13 = var10; var13 <= var11; var13++) {
            if (!method6233(var12, var13)) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean method6236(Vec3d var0) {
      Box var1 = mc.player.getDimensions(EntityPose.STANDING).getBoxAt(var0);
      return method6235(var1.minX, var1.minZ, var1.maxX, var1.maxZ);
   }

   private static boolean method6237(double var0, double var2, double var4) {
      Box var6 = mc.player.getDimensions(EntityPose.STANDING).getBoxAt(var0, var2, var4);
      return method6235(var6.minX, var6.minZ, var6.maxX, var6.maxZ) && !mc.world.getBlockCollisions(mc.player, var6).iterator().hasNext();
   }

   private static boolean method6238(Vec3d var0) {
      return mc.player != null && mc.world != null && method6232(var0) && method6237(var0.x, var0.y, var0.z);
   }

   private static boolean method6239(Entity var0) {
      Vec3d var1 = var0.getEntityPos();
      Box var2 = mc.player.getDimensions(EntityPose.STANDING).getBoxAt(var1);
      return method6232(var1) && method6235(var2.minX, var2.minZ, var2.maxX, var2.maxZ) && !mc.world.getBlockCollisions(mc.player, var2).iterator().hasNext();
   }

   static {
      EventManager.register(INSTANCE);
   }
}

package org.zenith.utility.misc;

import baritone.api.BaritoneAPI;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.client.events.UpdateEvent;

public final class Util17 implements MinecraftAccess {
   private static final float CONST_floatValue = 47.0F;
   private static final float CONST_floatValue2 = 1.2F;
   private static final long CONST_longValue = 200L;
   private static float floatValue;
   private static float floatValue2;
   private static long longValue;
   private static float floatValue3;
   private static float floatValue4;
   private static boolean flag;
   private static float floatValue5;
   private static float floatValue6;
   private static float floatValue7;
   private static float floatValue8;
   private static boolean flag2;
   private static float floatValue9;
   private static float floatValue10;

   private Util17() {
   }

   public static void method3329(Vec3d var0) {
      float[] var1 = method3331(var0);
      method3332(var1[0], var1[1]);
   }

   public static boolean method3330(Vec3d var0, float var1) {
      float[] var2 = method3331(var0);
      float var3 = MathHelper.wrapDegrees(var2[0] - floatValue);
      float var4 = var2[1] - floatValue2;
      return Math.sqrt(var3 * var3 + var4 * var4) <= var1;
   }

   public static float[] method3331(Vec3d var0) {
      Vec3d var1 = mc.player.getEyePos();
      double var2 = var0.x - var1.x;
      double var4 = var0.y - var1.y;
      double var6 = var0.z - var1.z;
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      return new float[]{(float)Math.toDegrees(Math.atan2(var6, var2)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(var4, var8)))};
   }

   public static void method3332(float var0, float var1) {
      long var2 = System.currentTimeMillis();
      if (longValue == 0L || !flag2 && var2 - longValue > 400L) {
         floatValue = mc.player.getYaw();
         floatValue2 = mc.player.getPitch();
      }

      floatValue = MathHelper.wrapDegrees(floatValue);
      longValue = var2;
      float[] var4 = method3335(floatValue, floatValue2, var0, var1);
      floatValue = var4[0];
      floatValue2 = var4[1];
   }

   public static void method3333(Vec3d var0) {
      float[] var1 = method3331(var0);
      float[] var2 = method3335(MathHelper.wrapDegrees(mc.player.getYaw()), mc.player.getPitch(), var1[0], var1[1]);
      mc.player.setYaw(var2[0]);
      mc.player.setPitch(var2[1]);
      floatValue = var2[0];
      floatValue2 = var2[1];
      longValue = System.currentTimeMillis();
   }

   public static void method3334(boolean var0) {
      flag2 = var0;
      if (var0) {
         longValue = 0L;
      }
   }

   private static float[] method3335(float var0, float var1, float var2, float var3) {
      ThreadLocalRandom var4 = ThreadLocalRandom.current();
      floatValue9 = MathHelper.clamp(floatValue9 * 0.96F + (float)var4.nextGaussian() * 0.15F, -1.2F, 1.2F);
      floatValue10 = MathHelper.clamp(floatValue10 * 0.96F + (float)var4.nextGaussian() * 0.1F, -0.9F, 0.9F);
      float var5 = MathHelper.wrapDegrees(var2 + floatValue9 - var0);
      float var6 = MathHelper.clamp(var3 + floatValue10, -90.0F, 90.0F) - var1;
      float var7 = (float)Math.sqrt(var5 * var5 + var6 * var6);
      if (var7 < 0.08F) {
         return new float[]{var0, var1};
      } else {
         float var8 = 0.31F + var4.nextFloat() * 0.28F;
         float var9 = MathHelper.clamp(var7 * var8, Math.min(var7, 1.2F), 47.0F);
         if (var7 < 5.0F && var7 > 1.5F && var4.nextFloat() < 0.1F) {
            var9 = Math.min(var7 * 1.2F, 47.0F);
         }

         float var10 = var9 / var7;
         float var11 = var5 * var10 + (float)var4.nextGaussian() * 0.27F;
         float var12 = var6 * var10 * 0.85F + (float)var4.nextGaussian() * 0.18F;
         return new float[]{var0 + method3343(var11), MathHelper.clamp(var1 + method3343(var12), -90.0F, 90.0F)};
      }
   }

   public static void method3336(Vec3d var0) {
      double var1 = var0.x - mc.player.getX();
      double var3 = var0.z - mc.player.getZ();
      float var5 = (float)Math.toDegrees(Math.atan2(var3, var1)) - 90.0F;
      float var6 = MathHelper.wrapDegrees(var5 - mc.player.getYaw());
      boolean var7 = var6 > -67.5F && var6 < 67.5F;
      boolean var8 = var6 > 112.5F || var6 < -112.5F;
      boolean var9 = var6 < -22.5F && var6 > -157.5F;
      boolean var10 = var6 > 22.5F && var6 < 157.5F;
      mc.options.forwardKey.setPressed(var7);
      mc.options.backKey.setPressed(var8);
      mc.options.leftKey.setPressed(var9);
      mc.options.rightKey.setPressed(var10);
      mc.options.sprintKey.setPressed(var7);
   }

   public static void method3337() {
      mc.options.forwardKey.setPressed(false);
      mc.options.backKey.setPressed(false);
      mc.options.leftKey.setPressed(false);
      mc.options.rightKey.setPressed(false);
      mc.options.sprintKey.setPressed(false);
   }

   public static boolean method3338() {
      return flag && mc.player != null;
   }

   public static float method3339(float var0) {
      return floatValue7 + MathHelper.wrapDegrees(floatValue5 - floatValue7) * var0;
   }

   public static float method3340(float var0) {
      return MathHelper.lerp(var0, floatValue8, floatValue6);
   }

   private static void method3341(float var0, float var1) {
      float var2 = MathHelper.wrapDegrees(var0 - floatValue);
      if (Math.abs(var2) > 30.0F) {
         floatValue = MathHelper.wrapDegrees(var0 - Math.copySign(30.0F, var2));
      }

      float var3 = var1 - floatValue2;
      if (Math.abs(var3) > 30.0F) {
         floatValue2 = MathHelper.clamp(var1 - Math.copySign(30.0F, var3), -90.0F, 90.0F);
      }
   }

   private static boolean method3342() {
      try {
         return BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      } catch (Throwable var1) {
         return false;
      }
   }

   private static float method3343(float var0) {
      double var1 = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
      double var3 = var1 * var1 * var1 * 1.2;
      return var3 <= 0.0 ? var0 : (float)(Math.round(var0 / var3) * var3);
   }

   static {
      EventManager.register(new Object() {
         @EventTarget
         public void onPacket(PacketEvent var1) {
            if (var1.method7099() && MinecraftAccess.mc.player != null && var1.getPacket3() instanceof PlayerMoveC2SPacket var2 && var2.changeLook) {
               if (System.currentTimeMillis() - Util17.longValue <= 200L) {
                  var2.yaw = Util17.floatValue;
                  var2.pitch = Util17.floatValue2;
               } else {
                  float var5 = MathHelper.wrapDegrees(var2.yaw - Util17.floatValue);
                  float var4 = var2.pitch - Util17.floatValue2;
                  if (Util17.method3342()) {
                     Util17.floatValue = var2.yaw;
                     Util17.floatValue2 = var2.pitch;
                  } else if (Util17.flag2 && Util17.longValue != 0L) {
                     var2.yaw = Util17.floatValue;
                     var2.pitch = Util17.floatValue2;
                  } else if (!(Math.abs(var5) > 8.0F) && !(Math.abs(var4) > 8.0F)) {
                     Util17.floatValue = var2.yaw;
                     Util17.floatValue2 = var2.pitch;
                  } else {
                     Util17.floatValue = MathHelper.wrapDegrees(Util17.floatValue + MathHelper.clamp(var5, -35.0F, 35.0F));
                     Util17.floatValue2 = MathHelper.clamp(Util17.floatValue2 + MathHelper.clamp(var4, -35.0F, 35.0F), -90.0F, 90.0F);
                     Util17.method3341(var2.yaw, var2.pitch);
                     var2.yaw = Util17.floatValue;
                     var2.pitch = Util17.floatValue2;
                  }

                  Util17.floatValue3 = var2.yaw;
                  Util17.floatValue4 = var2.pitch;
                  if (!Util17.flag) {
                     Util17.flag = true;
                     Util17.floatValue5 = Util17.floatValue7 = Util17.floatValue3;
                     Util17.floatValue6 = Util17.floatValue8 = Util17.floatValue4;
                  }
               }
            }
         }

         @EventTarget
         public void onUpdate(UpdateEvent var1) {
            if (MinecraftAccess.mc.player != null) {
               boolean var2 = System.currentTimeMillis() - Util17.longValue <= 200L;
               float var3 = var2 ? Util17.floatValue : (Util17.flag ? Util17.floatValue3 : MinecraftAccess.mc.player.getYaw());
               float var4 = var2 ? Util17.floatValue2 : (Util17.flag ? Util17.floatValue4 : MinecraftAccess.mc.player.getPitch());
               Util17.floatValue7 = Util17.floatValue5;
               Util17.floatValue8 = Util17.floatValue6;
               float var5 = MathHelper.wrapDegrees(var3 - Util17.floatValue5);
               float var6 = var4 - Util17.floatValue6;
               Util17.floatValue5 = Util17.floatValue5 + MathHelper.clamp(var5 * 0.45F, -40.0F, 40.0F);
               Util17.floatValue6 = MathHelper.clamp(Util17.floatValue6 + MathHelper.clamp(var6 * 0.45F, -40.0F, 40.0F), -90.0F, 90.0F);
            }
         }
      });
   }
}

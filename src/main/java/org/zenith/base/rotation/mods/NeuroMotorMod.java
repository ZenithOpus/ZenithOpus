package org.zenith.base.rotation.mods;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public final class NeuroMotorMod extends Util22 {
   private static final String text = "motor_v10.json";
   private static final String text2 = "models/motor_v10.json";
   private static final List<Path> items = List.of(Path.of("motor_v10.json"), Path.of("models", "motor_v10.json"), Path.of("..", "models", "motor_v10.json"));
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static volatile MotorNet motorNet;
   private static volatile boolean flag;
   private static volatile CompletableFuture<?> completableFuture;
   private static volatile String status = "не загружался";
   private final Random random = new Random();
   private double[][] doubleValues;
   private final double[] doubleValues2 = new double[2];
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private Rotation rotation;

   private static synchronized MotorNet method3875() {
      if (flag) {
         return motorNet;
      } else {
         MotorNet var0 = null;

         try (InputStream var1 = method3877()) {
            var0 = MotorNet.method3616(var1);
            status = "NeuroMotor загружен. " + var0.text2;
         } catch (Exception var11) {
            var0 = null;
            status = "NeuroMotor НЕ загружен: " + var11.getMessage();
         } finally {
            motorNet = var0;
            flag = true;
         }

         System.out.println(status);
         if (mc != null && mc.player != null) {
            String var13 = status;
            mc.execute(() -> ChatLogger.method2019(var13));
         }

         return var0;
      }
   }

   private static boolean method3876() {
      if (flag) {
         return true;
      } else {
         if (completableFuture == null) {
            synchronized (NeuroMotorMod.class) {
               if (completableFuture == null && !flag) {
                  completableFuture = CompletableFuture.runAsync(NeuroMotorMod::method3875);
               }
            }
         }

         return flag;
      }
   }

   private static InputStream method3877() throws IOException {
      for (Path var1 : items) {
         if (Files.isRegularFile(var1)) {
            return Files.newInputStream(var1);
         }
      }

      InputStream var2 = NeuroMotorMod.class.getClassLoader().getResourceAsStream("models/motor_v10.json");
      if (var2 == null) {
         throw new IOException("motor_v10.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return var2;
      }
   }

   public static String getStatus4() {
      return status;
   }

   public boolean method3878() {
      return method3875() != null;
   }

   public void reset() {
      this.doubleValues = null;
      this.doubleValues2[0] = 0.0;
      this.doubleValues2[1] = 0.0;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private static double method3879() {
      double var0 = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return var0 * var0 * var0 * 8.0 * 0.15;
   }

   public Rotation method3880() {
      Rotation var1 = rotationManager.method7844();
      if (!method3876()) {
         this.reset();
         return var1;
      } else {
         MotorNet var2 = motorNet;
         if (var2 == null) {
            this.reset();
            return mc.player == null ? var1 : new Rotation(mc.player.getYaw(), mc.player.getPitch(), true);
         } else {
            LivingEntity var3 = AuraModule.INSTANCE.method6358();
            if (mc.player != null && var3 != null && var3.isAlive()) {
               int var4 = mc.player.age;
               if (var4 == this.intValue2 && this.rotation != null) {
                  return this.rotation;
               } else {
                  if (this.intValue != Integer.MIN_VALUE && var4 != this.intValue + 1) {
                     this.reset();
                  }

                  this.intValue = var4;
                  if (this.doubleValues == null) {
                     this.doubleValues = new double[var2.intValue][2];
                  }

                  Box var5 = var3.getBoundingBox();
                  Vec3d var6 = mc.player.getEyePos();
                  PlayerInput var7 = mc.player.input.playerInput;
                  double[] var8 = new double[]{
                     var7.forward() ? 1.0 : 0.0, var7.backward() ? 1.0 : 0.0, var7.left() ? 1.0 : 0.0, var7.right() ? 1.0 : 0.0, var7.jump() ? 1.0 : 0.0
                  };
                  double[] var9 = MotorNet.method3612(
                     var1.getFloatValue(),
                     var1.getFloatValue2(),
                     new double[]{var6.x, var6.y, var6.z},
                     new double[]{var5.minX, var5.minY, var5.minZ, var5.maxX, var5.maxY, var5.maxZ},
                     var8,
                     mc.player.isOnGround() ? 1.0 : 0.0,
                     mc.player.isSprinting() ? 1.0 : 0.0,
                     mc.player.getAttackCooldownProgress(0.0F)
                  );
                  Vec3d var10 = mc.player.getVelocity();
                  var9 = var2.method3613(var9, var1.getFloatValue(), new double[]{var10.x, var10.y, var10.z});
                  double[] var11 = new double[var2.intValue8];

                  for (int var12 = 0; var12 < var11.length; var12++) {
                     var11[var12] = this.random.nextDouble();
                  }

                  MotorNet.Entry var21 = var2.method3611(this.doubleValues, var9, this.doubleValues2, var11);
                  double var13 = method3879();
                  double var15 = var21.doubleValue * var13;
                  double var17 = var21.doubleValue2 * var13;

                  for (int var19 = 0; var19 + 1 < this.doubleValues.length; var19++) {
                     this.doubleValues[var19] = this.doubleValues[var19 + 1];
                  }

                  this.doubleValues[this.doubleValues.length - 1] = new double[]{var15, var17};
                  this.doubleValues2[0] = var21.doubleValue;
                  this.doubleValues2[1] = var21.doubleValue2;
                  Rotation var22 = var1.method3140((float)var15, (float)var17);
                  this.intValue2 = var4;
                  this.rotation = var22;
                  return var22;
               }
            } else {
               this.reset();
               if (mc.player != null) {
                  mc.player.setYaw(var1.getFloatValue());
                  mc.player.setPitch(var1.getFloatValue2());
               }

               return var1;
            }
         }
      }
   }
}

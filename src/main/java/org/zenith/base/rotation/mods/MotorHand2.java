package org.zenith.base.rotation.mods;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.utility.misc.Util22;

public class MotorHand2 extends Util22 {
   private static final String text = "motor_hand.json";
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static MotorHand motorHand;
   private static boolean flag;
   private static String status = "не загружался";
   private final Random random = new Random();
   private MotorHand.Entry entry;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private Rotation rotation;

   private static synchronized MotorHand method3426() {
      if (flag) {
         return motorHand;
      } else {
         flag = true;

         try (InputStream var0 = method3427()) {
            motorHand = MotorHand.method3593(var0);
            status = "MotorClaude (рука без модели) загружен. " + motorHand.text2;
         } catch (Exception var5) {
            motorHand = null;
            status = "MotorClaude НЕ загружен: " + var5.getMessage();
         }

         System.out.println(status);
         if (mc != null && mc.player != null) {
            String var6 = status;
            mc.execute(() -> ChatLogger.method2019(var6));
         }

         return motorHand;
      }
   }

   private static InputStream method3427() throws IOException {
      for (Path var1 : List.of(Path.of("motor_hand.json"), Path.of("models", "motor_hand.json"), Path.of("..", "models", "motor_hand.json"))) {
         if (Files.isRegularFile(var1)) {
            return Files.newInputStream(var1);
         }
      }

      InputStream var2 = MotorHand2.class.getClassLoader().getResourceAsStream("models/motor_hand.json");
      if (var2 == null) {
         throw new IOException("motor_hand.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return var2;
      }
   }

   public static String getStatus() {
      return status;
   }

   public boolean method3428() {
      return method3426() != null;
   }

   public void reset() {
      this.entry = null;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private static double method3429() {
      double var0 = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return var0 * var0 * var0 * 8.0 * 0.15;
   }

   public Rotation method3430(Rotation var1) {
      Rotation var2 = zenith.getRotationManager().method7844();
      MotorHand var3 = method3426();
      if (var3 != null && mc.player != null && var1 != null) {
         int var4 = mc.player.age;
         if (var4 == this.intValue2 && this.rotation != null) {
            return this.rotation;
         } else {
            if (this.intValue != Integer.MIN_VALUE && var4 != this.intValue + 1) {
               this.reset();
            }

            this.intValue = var4;
            if (this.entry == null) {
               this.entry = var3.method3588();
            }

            double var5 = method3429();
            double[] var7 = var3.method3592(
               this.entry,
               new double[]{var1.getFloatValue(), var1.getFloatValue2()},
               new double[]{var2.getFloatValue(), var2.getFloatValue2()},
               var5,
               this.random.nextDouble()
            );
            Rotation var8 = var2.method3140((float)(var7[0] * var5), (float)(var7[1] * var5));
            this.intValue2 = var4;
            this.rotation = var8;
            return var8;
         }
      } else {
         this.reset();
         return var2;
      }
   }
}

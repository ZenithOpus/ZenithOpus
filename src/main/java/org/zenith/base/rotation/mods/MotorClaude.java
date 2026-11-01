package org.zenith.base.rotation.mods;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.utility.misc.Util22;

public class MotorClaude extends Util22 {
   public static final String text = "motor_claude.json";
   public static final String text2 = "motor_imitation.json";
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final Map<String, MotorClaudeNet> items = new HashMap<>();
   private static final Map<String, String> items2 = new HashMap<>();
   private final String text3;
   private final Random random = new Random();
   private MotorClaudeNet.Entry entry;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3 = 4.0;
   private static final double CONST_doubleValue = 0.4;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private Rotation rotation;
   private double doubleValue4 = Double.NaN;

   public MotorClaude() {
      this("motor_claude.json");
   }

   public MotorClaude(String var1) {
      this.text3 = var1;
   }

   private MotorClaudeNet method3420() {
      synchronized (items) {
         if (items2.containsKey(this.text3)) {
            return items.get(this.text3);
         } else {
            MotorClaudeNet var2 = null;

            String var3;
            try (InputStream var4 = method3421(this.text3)) {
               var2 = MotorClaudeNet.method3580(var4);
               var3 = this.text3 + " загружен. " + var2.text2;
            } catch (Exception var10) {
               var3 = this.text3 + " НЕ загружен: " + var10.getMessage();
            }

            items.put(this.text3, var2);
            items2.put(this.text3, var3);
            System.out.println(var3);
            if (mc != null && mc.player != null) {
               String var12 = var3;
               mc.execute(() -> ChatLogger.method2019(var12));
            }

            return var2;
         }
      }
   }

   private static InputStream method3421(String var0) throws IOException {
      for (Path var2 : List.of(Path.of(var0), Path.of("models", var0), Path.of("..", "models", var0))) {
         if (Files.isRegularFile(var2)) {
            return Files.newInputStream(var2);
         }
      }

      InputStream var3 = MotorClaude.class.getClassLoader().getResourceAsStream("models/" + var0);
      if (var3 == null) {
         throw new IOException(var0 + " не найден ни рядом с клиентом, ни в jar");
      } else {
         return var3;
      }
   }

   public String method3422() {
      synchronized (items) {
         return items2.getOrDefault(this.text3, "не загружался");
      }
   }

   public boolean method3423() {
      return this.method3420() != null;
   }

   public void setDoubleValue4(double var1) {
      this.doubleValue4 = var1;
   }

   public void setDoubleValue3(double var1) {
      this.doubleValue3 = var1;
   }

   public void reset() {
      this.entry = null;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private static double method3424() {
      double var0 = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return var0 * var0 * var0 * 8.0 * 0.15;
   }

   public Rotation method3425(Rotation var1) {
      Rotation var2 = zenith.getRotationManager().method7844();
      MotorClaudeNet var3 = this.method3420();
      if (var3 != null && mc.player != null && var1 != null) {
         int var4 = mc.player.age;
         if (var4 == this.intValue2 && this.rotation != null) {
            return this.rotation;
         } else {
            if (this.intValue != Integer.MIN_VALUE && var4 != this.intValue + 1) {
               this.reset();
            }

            this.intValue = var4;
            double var5 = method3424();
            double var7 = Math.log(Double.isNaN(this.doubleValue4) ? var3.getDoubleValue16() : this.doubleValue4);
            if (this.entry == null) {
               this.entry = var3.method3563();
               this.doubleValue = var2.getFloatValue();
               this.doubleValue2 = var2.getFloatValue2();
               double[] var9 = new double[]{0.0, 0.0};

               for (int var10 = 0; var10 < var3.getIntValue8(); var10++) {
                  var3.method3579(this.entry, var9, var9, var2.getFloatValue2(), var5, var7, new double[var3.getIntValue9()], var9);
               }
            }

            double var21 = this.doubleValue;
            double var11 = this.doubleValue2;
            if (this.doubleValue3 > 0.0) {
               double var13 = MathHelper.wrapDegrees(var1.getFloatValue() - this.doubleValue);
               double var15 = var1.getFloatValue2() - this.doubleValue2;
               double var17 = Math.sqrt(var13 * var13 + var15 * var15);
               double var19 = var17 < 1.0E-9 ? 0.0 : Math.min(1.0, Math.max(0.4, var17 / this.doubleValue3) / var17);
               this.doubleValue += var13 * var19;
               this.doubleValue2 += var15 * var19;
            } else {
               this.doubleValue = var1.getFloatValue();
               this.doubleValue2 = var1.getFloatValue2();
            }

            double[] var22 = new double[]{MathHelper.wrapDegrees(this.doubleValue - var2.getFloatValue()), this.doubleValue2 - var2.getFloatValue2()};
            double[] var14 = new double[]{MathHelper.wrapDegrees(this.doubleValue - var21), this.doubleValue2 - var11};
            double[] var23 = new double[var3.getIntValue9()];

            for (int var16 = 0; var16 < var23.length; var16++) {
               var23[var16] = this.random.nextDouble();
            }

            MotorClaudeNet.Entry2 var24 = var3.method3579(this.entry, var22, var14, var2.getFloatValue2(), var5, var7, var23, null);
            Rotation var25 = var2.method3140((float)(var24.doubleValue * var5), (float)(var24.doubleValue2 * var5));
            this.intValue2 = var4;
            this.rotation = var25;
            return var25;
         }
      } else {
         this.reset();
         return var2;
      }
   }
}

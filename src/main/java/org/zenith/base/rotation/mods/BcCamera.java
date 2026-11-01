package org.zenith.base.rotation.mods;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.PlayerInput;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public class BcCamera extends Util22 {
   private final String text;
   private final int intValue;
   private final double doubleValue;
   private volatile BcCameraNet bcCameraNet;
   private CompletableFuture<Void> completableFuture;
   private volatile String status = "не загружался";
   private static volatile int intValue2;
   private final Random random = new Random();
   private BcCameraNet.Entry2 entry2;
   private long longValue;
   private long longValue2 = Long.MIN_VALUE;
   private long longValue3 = Long.MIN_VALUE;
   private Rotation rotation;
   private Object obj;
   private double doubleValue2;
   private double doubleValue3;
   private boolean flag;
   private float floatValue = 1.0F;
   private int intValue3;
   private static final int CONST_intValue = 72000;
   private BufferedWriter bufferedWriter;
   private int intValue4;
   private int intValue5;
   private volatile KeyFixNet keyFixNet;
   private double[] doubleValues;
   private long longValue4 = Long.MIN_VALUE;
   private int intValue6 = -1;

   public BcCamera() {
      this("bc_camera.json", 1);
   }

   public BcCamera(String var1, int var2) {
      this(var1, var2, 1.0);
   }

   public BcCamera(String var1, int var2, double var3) {
      this.text = var1;
      this.intValue = var2;
      this.doubleValue = var3;
      EventManager.register(this);
   }

   private String name() {
      return this.intValue == 1 ? "NeuroBC" : (this.intValue == 4 ? "TestNerv" : "NeuroBC-" + this.intValue);
   }

   public static void method3546() {
      intValue2++;
   }

   public String getStatus2() {
      return this.status;
   }

   private synchronized BcCameraNet method3547() {
      if (this.bcCameraNet != null) {
         return this.bcCameraNet;
      } else {
         if (this.completableFuture == null) {
            this.completableFuture = CompletableFuture.runAsync(() -> {
               try (InputStream var1 = this.method3549()) {
                  BcCameraNet var2 = BcCameraNet.method3387(var1);
                  var2.doubleValue5 = this.doubleValue;
                  this.status = this.name() + " загружен (" + this.text + ", температура ненулевых ходов " + this.doubleValue + "). " + var2.text2;
                  String var3 = this.method3548();

                  try (InputStream var4 = this.method3550(var3)) {
                     KeyFixNet var5 = KeyFixNet.method3562(var4);
                     this.keyFixNet = var5;
                     this.status = this.status + " | " + var5.text2;
                  } catch (NoSuchFileException | FileNotFoundException var10) {
                     this.status = this.status + " | поправщика клавиш нет (" + var3 + ")";
                  } catch (Exception var11) {
                     this.status = this.status + " | поправщик клавиш НЕ загружен (" + var3 + "): " + var11.getMessage();
                  }

                  this.bcCameraNet = var2;
               } catch (Exception var13) {
                  this.status = this.name() + " НЕ загружен (" + this.text + "): " + var13.getMessage();
               }

               System.out.println(this.status);
               if (mc != null) {
                  String var14 = this.status;
                  mc.execute(() -> {
                     if (mc.player != null) {
                        ChatLogger.method2019(var14);
                     }
                  });
               }
            });
         }

         return null;
      }
   }

   private String method3548() {
      return this.intValue == 1 ? "bc_keyfix.json" : "bc_keyfix_" + this.intValue + ".json";
   }

   private InputStream method3549() throws IOException {
      return this.method3550(this.text);
   }

   private InputStream method3550(String var1) throws IOException {
      for (Path var3 : List.of(Path.of(var1), Path.of("models", var1), Path.of("..", "models", var1))) {
         if (Files.isRegularFile(var3)) {
            return Files.newInputStream(var3);
         }
      }

      InputStream var4 = BcCamera.class.getClassLoader().getResourceAsStream("models/" + var1);
      if (var4 == null) {
         throw new FileNotFoundException(var1 + " не найден ни рядом с клиентом, ни в jar");
      } else {
         return var4;
      }
   }

   public boolean method3551() {
      return this.bcCameraNet != null;
   }

   public void reset() {
      this.entry2 = null;
      this.rotation = null;
      this.doubleValues = null;
      this.flag = false;
      this.obj = null;
   }

   private static double method3552() {
      double var0 = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return var0 * var0 * var0 * 8.0 * 0.15;
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      this.longValue++;
      if (mc.player != null && mc.world != null && AuraModule.INSTANCE.isEnabled() && AuraModule.INSTANCE.method6359(this.intValue)) {
         BcCameraNet var2 = this.method3547();
         if (var2 == null) {
            this.reset();
         } else {
            if (this.intValue == 4 && var2.doubleValue2 >= 0.0) {
               var2.doubleValue2 = AuraModule.INSTANCE.method6361();
            }

            if (this.entry2 == null || mc.world != this.obj) {
               this.entry2 = var2.method3355();
               this.flag = false;
            }

            this.obj = mc.world;
            Rotation var3 = zenith.getRotationManager().method7844();
            double var4 = var3.getFloatValue();
            double var6 = var3.getFloatValue2();
            BcCameraNet.Entry4 var8 = new BcCameraNet.Entry4();
            var8.doubleValue = mc.player.getX();
            var8.doubleValue2 = mc.player.getY();
            var8.doubleValue3 = mc.player.getZ();
            PlayerInput var9 = mc.player.input.playerInput;
            var8.intValue = (var9.forward() ? 1 : 0)
               | (var9.backward() ? 2 : 0)
               | (var9.left() ? 4 : 0)
               | (var9.right() ? 8 : 0)
               | (var9.jump() ? 16 : 0)
               | (var9.sneak() ? 32 : 0)
               | (var9.sprint() ? 64 : 0);
            var8.flag = mc.player.isOnGround();
            var8.flag2 = mc.player.horizontalCollision;
            int var10 = intValue2;
            float var11 = mc.player.getAttackCooldownProgress(0.0F);
            var8.flag3 = var10 != this.intValue3 || var11 < this.floatValue - 1.0E-4F;
            this.intValue3 = var10;
            this.floatValue = var11;
            LivingEntity var12 = AuraModule.INSTANCE.method6358();
            if (var12 != null && var12.isAlive()) {
               var8.doubleValue4 = var12.getX();
               var8.doubleValue5 = var12.getY();
               var8.doubleValue6 = var12.getZ();
               var8.intValue2 = var12.getId();
               var8.intValue3 = 1;
               var8.flag4 = var12.isSneaking();
            }

            var8.doubleValue7 = var4;
            var8.doubleValue8 = var6;
            if (this.flag) {
               var8.doubleValue9 = var4 - this.doubleValue2;
               var8.doubleValue10 = var6 - this.doubleValue3;
               var8.flag5 = Math.abs(var8.doubleValue9) <= 180.0;
            } else {
               var8.flag5 = false;
            }

            this.doubleValue2 = var4;
            this.doubleValue3 = var6;
            this.flag = true;
            this.entry2.method3394(var8);
            double var13 = method3552();
            if (this.longValue3 != this.longValue - 1L) {
               this.entry2.method3396();
            }

            GameOptions var15 = mc.options;
            int var16 = (var15.forwardKey.isPressed() ? 1 : 0)
               | (var15.backKey.isPressed() ? 2 : 0)
               | (var15.leftKey.isPressed() ? 4 : 0)
               | (var15.rightKey.isPressed() ? 8 : 0)
               | (var15.jumpKey.isPressed() ? 16 : 0)
               | (var15.sneakKey.isPressed() ? 32 : 0)
               | (var15.sprintKey.isPressed() ? 64 : 0);
            double[] var17 = var2.method3365(this.entry2, var13);
            this.doubleValues = var17;
            this.longValue4 = this.longValue;
            double[] var18 = var2.method3379(var17, var16);
            double[] var19;
            if (var2.method3381()) {
               var19 = var2.method3383(this.entry2, var17, var18, var13, this.random);
            } else if (var2.entry3 != null) {
               var19 = var2.method3350(this.entry2, var17, var18, var13, this.random);
            } else if (var2.flag3) {
               var19 = var2.method3348(this.entry2, var17, var18, var13, this.random);
            } else {
               var19 = var2.method3380(this.entry2, var18, var13, this.random);
            }

            var19 = var2.method3352(var17, var19[0], var19[1]);
            double[] var20 = BcCameraNet.method3384(this.entry2, var19[0], var19[1], var13, var6);
            this.rotation = var3.method3140((float)var20[0], (float)var20[1]);
            this.method3553(var8, var18, var19, var20, var13, var11);
            this.longValue2 = this.longValue;
            this.intValue5 = 0;
         }
      } else {
         this.reset();
      }
   }

   @EventTarget(4)
   public void onMoveInput(MoveInputEvent var1) {
      KeyFixNet var2 = this.keyFixNet;
      double[] var3 = this.doubleValues;
      this.intValue6 = -1;
      if (var2 != null && var3 != null && this.longValue4 == this.longValue && var1.getPlayerInput() != null && var2.method3559(var3)) {
         PlayerInput var4 = var1.getPlayerInput();
         int var5 = (var4.forward() ? 1 : 0)
            | (var4.backward() ? 2 : 0)
            | (var4.left() ? 4 : 0)
            | (var4.right() ? 8 : 0)
            | (var4.jump() ? 16 : 0)
            | (var4.sneak() ? 32 : 0)
            | (var4.sprint() ? 64 : 0);
         int var6 = var2.method3560(var3, var5, this.random);
         var1.setPlayerInput(
            new PlayerInput((var6 & 1) != 0, (var6 & 2) != 0, (var6 & 4) != 0, (var6 & 8) != 0, (var6 & 16) != 0, var4.sneak(), (var6 & 64) != 0)
         );
         this.intValue6 = var5;
      }
   }

   private void method3553(BcCameraNet.Entry4 var1, double[] var2, double[] var3, double[] var4, double var5, float var7) {
      if (this.intValue4 < 72000) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of(this.intValue == 1 ? "bc_camera_log.csv" : "bc_camera" + this.intValue + "_log.csv"),
                  StandardCharsets.UTF_8,
                  StandardOpenOption.CREATE,
                  StandardOpenOption.APPEND
               );
               StringBuilder var8 = new StringBuilder(
                  "tick,age,applied_prev,process_calls_prev,grid,cooldown,x,y,z,keys,on_ground,hcoll,attack,tgt_id,tx,ty,tz,tgt_sneak,yaw,pitch,my,mp,mv_ok,act_yaw,act_pitch,q_yaw,q_pitch,cam_yaw,cam_pitch,model,ukeys_prev"
               );

               for (int var9 = 0; var9 < var2.length; var9++) {
                  var8.append(",f").append(var9);
               }

               this.bufferedWriter.write(var8.append('\n').toString());
            }

            StringBuilder var15 = new StringBuilder(512);
            var15.append(this.longValue)
               .append(',')
               .append(mc.player.age)
               .append(',')
               .append(this.longValue3 == this.longValue - 1L ? 1 : 0)
               .append(',')
               .append(this.intValue5)
               .append(',')
               .append(var5)
               .append(',')
               .append(var7)
               .append(',')
               .append(var1.doubleValue)
               .append(',')
               .append(var1.doubleValue2)
               .append(',')
               .append(var1.doubleValue3)
               .append(',')
               .append(var1.intValue)
               .append(',')
               .append(var1.flag ? 1 : 0)
               .append(',')
               .append(var1.flag2 ? 1 : 0)
               .append(',')
               .append(var1.flag3 ? 1 : 0)
               .append(',')
               .append(var1.intValue2)
               .append(',')
               .append(var1.doubleValue4)
               .append(',')
               .append(var1.doubleValue5)
               .append(',')
               .append(var1.doubleValue6)
               .append(',')
               .append(var1.flag4 ? 1 : 0)
               .append(',')
               .append(var1.doubleValue7)
               .append(',')
               .append(var1.doubleValue8)
               .append(',')
               .append(var1.doubleValue9)
               .append(',')
               .append(var1.doubleValue10)
               .append(',')
               .append(var1.flag5 ? 1 : 0)
               .append(',')
               .append(var3[0])
               .append(',')
               .append(var3[1])
               .append(',')
               .append(var4[0])
               .append(',')
               .append(var4[1])
               .append(',')
               .append(mc.player.getYaw())
               .append(',')
               .append(mc.player.getPitch())
               .append(',')
               .append(this.bcCameraNet != null ? this.bcCameraNet.source : "?")
               .append(',')
               .append(this.intValue6);

            for (double var12 : var2) {
               var15.append(',').append((float)var12);
            }

            this.bufferedWriter.write(var15.append('\n').toString());
            if (++this.intValue4 % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException var14) {
            this.intValue4 = 72000;
         }
      }
   }

   public Rotation method3554(Rotation var1) {
      Rotation var2 = zenith.getRotationManager().method7844();
      this.intValue5++;
      Rotation var3 = zenith.getRotationManager().method7844();
      if (this.rotation != null && this.longValue2 == this.longValue) {
         this.longValue3 = this.longValue;
         return this.rotation;
      } else {
         mc.player.setYaw(var2.getFloatValue());
         mc.player.setPitch(var2.getFloatValue2());
         return var2;
      }
   }
}

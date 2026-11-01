package org.zenith.base.rotation;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.PlayerInput;
import org.zenith.ChatLogger;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.ZenithAccess;
import org.zenith.base.Module;
import org.zenith.base.rotation.mods.BcCameraNet;
import org.zenith.base.rotation.mods.SilentAssistNet;
import org.zenith.utility.misc.RotationRequest;

public final class AssistSilent implements ZenithAccess {
   public static final AssistSilent INSTANCE = new AssistSilent("assist_silent.json", "NeuroSilent");
   public static final AssistSilent INSTANCE2 = new AssistSilent("assist_silent_strong.json", "NeuroSilent-strong");
   private static final int CONST_intValue = 1;
   private static final int CONST_intValue2 = 72000;
   private final String text;
   private final String text2;
   private volatile SilentAssistNet silentAssistNet;
   private CompletableFuture<Void> completableFuture;
   private volatile String status = "не загружался";
   private volatile boolean flag;
   private SilentAssistNet.Entry entry;
   private Object obj;
   private boolean flag2;
   private boolean flag3;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private float floatValue = 1.0F;
   private Rotation rotation;
   private BufferedWriter bufferedWriter;
   private int intValue;
   private long longValue;

   private AssistSilent(String var1, String var2) {
      this.text = var1;
      this.text2 = var2;
      EventManager.register(this);
   }

   public String getStatus() {
      return this.status;
   }

   public void method6305() {
      this.method6306();
   }

   private synchronized SilentAssistNet method6306() {
      if (this.silentAssistNet != null) {
         return this.silentAssistNet;
      } else {
         if (this.completableFuture == null) {
            this.completableFuture = CompletableFuture.runAsync(() -> {
               try (InputStream var1 = this.method6307()) {
                  SilentAssistNet var2 = SilentAssistNet.method3634(var1);
                  this.status = this.text2 + " загружен (" + this.text + "). " + var2.text2;
                  this.silentAssistNet = var2;
               } catch (Exception var6) {
                  this.status = this.text2 + " НЕ загружен (" + this.text + "): " + var6.getMessage();
               }

               System.out.println(this.status);
               if (mc != null) {
                  String var7 = this.status;
                  mc.execute(() -> {
                     if (mc.player != null) {
                        ChatLogger.method2019(var7);
                     }
                  });
               }
            });
         }

         return null;
      }
   }

   private InputStream method6307() throws IOException {
      for (Path var2 : List.of(Path.of(this.text), Path.of("models", this.text), Path.of("..", "models", this.text))) {
         if (Files.isRegularFile(var2)) {
            return Files.newInputStream(var2);
         }
      }

      InputStream var3 = AssistSilent.class.getClassLoader().getResourceAsStream("models/" + this.text);
      if (var3 == null) {
         throw new IOException(this.text + " не найден ни рядом с клиентом, ни в jar");
      } else {
         return var3;
      }
   }

   public void reset() {
      this.entry = null;
      this.flag2 = false;
      this.rotation = null;
      this.obj = null;
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() instanceof PlayerPositionLookS2CPacket) {
         this.flag = true;
      }
   }

   private static double method6308() {
      double var0 = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return var0 * var0 * var0 * 8.0 * 0.15;
   }

   public void method6309(LivingEntity var1, Module var2) {
      this.longValue++;
      if (mc.player != null && mc.world != null && !mc.player.isDead()) {
         SilentAssistNet var3 = this.method6306();
         if (var3 == null) {
            this.reset();
         } else {
            if (this.entry == null || mc.world != this.obj) {
               this.entry = var3.method3623();
               this.flag2 = false;
               this.rotation = null;
            }

            this.obj = mc.world;
            Rotation var4 = rotationManager.getCurrentRotation();
            if (this.rotation != null
               && (
                  var4 == null
                     || Math.abs(method6310(var4.getFloatValue() - this.rotation.getFloatValue())) > 0.001
                     || Math.abs(var4.getFloatValue2() - this.rotation.getFloatValue2()) > 0.001
               )) {
               this.entry = var3.method3623();
            }

            this.rotation = null;
            double var5 = mc.player.getYaw();
            double var7 = mc.player.getPitch();
            float var9 = mc.player.getAttackCooldownProgress(0.0F);
            boolean var10 = var9 < this.floatValue - 1.0E-4F;
            this.floatValue = var9;
            boolean var11 = this.flag;
            this.flag = false;
            if (!this.flag2) {
               this.doubleValue = var5;
               this.doubleValue2 = var7;
               this.doubleValue3 = 0.0;
               this.doubleValue4 = 0.0;
               this.flag3 = false;
               this.flag2 = true;
            } else {
               BcCameraNet.Entry4 var12 = new BcCameraNet.Entry4();
               var12.doubleValue = mc.player.getX();
               var12.doubleValue2 = mc.player.getY();
               var12.doubleValue3 = mc.player.getZ();
               PlayerInput var13 = mc.player.input.playerInput;
               var12.intValue = (var13.forward() ? 1 : 0)
                  | (var13.backward() ? 2 : 0)
                  | (var13.left() ? 4 : 0)
                  | (var13.right() ? 8 : 0)
                  | (var13.jump() ? 16 : 0)
                  | (var13.sneak() ? 32 : 0)
                  | (var13.sprint() ? 64 : 0);
               var12.flag = mc.player.isOnGround();
               var12.flag2 = mc.player.horizontalCollision;
               var12.flag3 = var10;
               if (var1 != null && var1.isAlive()) {
                  var12.doubleValue4 = var1.getX();
                  var12.doubleValue5 = var1.getY();
                  var12.doubleValue6 = var1.getZ();
                  var12.intValue2 = var1.getId();
                  var12.intValue3 = 1;
                  var12.flag4 = var1.isSneaking();
               }

               var12.doubleValue7 = this.doubleValue;
               var12.doubleValue8 = this.doubleValue2;
               var12.doubleValue9 = this.doubleValue3;
               var12.doubleValue10 = this.doubleValue4;
               var12.flag5 = this.flag3;
               double var14 = var5 - this.doubleValue;
               double var16 = var7 - this.doubleValue2;
               boolean var18 = !var11 && Math.abs(var14) <= 180.0 && Math.abs(var16) <= 180.0;
               double var19 = method6308();
               var3.method3629(this.entry, var12);
               double[] var21 = var3.method3630(this.entry, var12, var14, var16, var18, var19, var5, var7, null);
               this.doubleValue = var5;
               this.doubleValue2 = var7;
               this.doubleValue3 = var14;
               this.doubleValue4 = var16;
               this.flag3 = var18;
               this.method6311(var12, var14, var16, var18, var19, var21);
               if (this.entry.isFlag()) {
                  Rotation var22 = new Rotation((float)var21[0], (float)var21[1]);
                  rotationManager.method7848(new RotationRequest(var22, () -> var22, bcCameraTestnerv.getRotationConfig()), 1, var2, 1);
                  this.rotation = var22;
               }
            }
         }
      } else {
         this.reset();
      }
   }

   private static double method6310(double var0) {
      double var2 = (var0 + 180.0) % 360.0;
      if (var2 < 0.0) {
         var2 += 360.0;
      }

      return var2 - 180.0;
   }

   private void method6311(BcCameraNet.Entry4 var1, double var2, double var4, boolean var6, double var7, double[] var9) {
      if (this.intValue < 72000) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of("neuro_silent_log.csv"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND
               );
               this.bufferedWriter
                  .write(
                     "tick,age,model,grid,x,y,z,keys,on_ground,hcoll,attack,tgt_id,tx,ty,tz,tgt_sneak,yaw_prev,pitch_prev,my_prev,mp_prev,ok_prev,my,mp,ok,control,sent_yaw,sent_pitch,o_yaw,o_pitch,mul_yaw,d_yaw,d_pitch,margin_sent\n"
                  );
            }

            StringBuilder var10 = new StringBuilder(320);
            var10.append(this.longValue)
               .append(',')
               .append(mc.player.age)
               .append(',')
               .append(this.text2)
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
               .append(var2)
               .append(',')
               .append(var4)
               .append(',')
               .append(var6 ? 1 : 0)
               .append(',')
               .append(this.entry.isFlag() ? 1 : 0)
               .append(',')
               .append(var9[0])
               .append(',')
               .append(var9[1])
               .append(',')
               .append(this.entry.getDoubleValue3())
               .append(',')
               .append(this.entry.getDoubleValue4())
               .append(',')
               .append(this.entry.doubleValue9)
               .append(',')
               .append(this.entry.doubleValue10)
               .append(',')
               .append(this.entry.doubleValue11)
               .append(',')
               .append(this.entry.doubleValue12)
               .append('\n');
            this.bufferedWriter.write(var10.toString());
            if (++this.intValue % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException var11) {
            this.intValue = 72000;
         }
      }
   }

   public String method6312() {
      if (this.silentAssistNet == null) {
         return this.text2 + ": " + this.status;
      } else {
         return this.entry == null
            ? this.text2 + ": нет прохода"
            : String.format(
               "%s: %s, сдвиг yaw %.2f° pitch %.2f°, множитель yaw %.2f",
               this.text2,
               this.entry.isFlag() ? "ведёт" : "прогрев",
               this.entry.getDoubleValue3(),
               this.entry.getDoubleValue4(),
               1.0 + this.entry.doubleValue9
            );
      }
   }
}

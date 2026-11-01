package org.zenith.base.rotation.mods;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.PlayerInput;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.player.AutoMineModule;
import org.zenith.utility.misc.Util22;

public class BcHand extends Util22 {
   public static final String text = "bc_hand.json";
   private volatile HandAnchorNet handAnchorNet;
   private CompletableFuture<Void> completableFuture;
   private volatile String status = "не загружался";
   private final Random random = new Random();
   private HandAnchorNet.Entry entry;
   private long longValue;
   private long longValue2 = Long.MIN_VALUE;
   private long longValue3 = Long.MIN_VALUE;
   private Rotation rotation;
   private Object obj;
   private double doubleValue;
   private double doubleValue2;
   private boolean flag;
   private BcCameraNet.Entry4 entry4;
   private BcCameraNet.Entry4 entry42;
   private long longValue4 = Long.MIN_VALUE;
   private Rotation rotation2;
   private double doubleValue3;
   private static final int CONST_intValue = 72000;
   private BufferedWriter bufferedWriter;
   private int intValue;

   public BcHand() {
      EventManager.register(this);
   }

   public String getStatus3() {
      return this.status;
   }

   private synchronized HandAnchorNet method3867() {
      if (this.handAnchorNet != null) {
         return this.handAnchorNet;
      } else {
         if (this.completableFuture == null) {
            this.completableFuture = CompletableFuture.runAsync(() -> {
               try (InputStream var1 = this.method3868()) {
                  HandAnchorNet var2 = HandAnchorNet.method3417(var1);
                  this.status = "NeuroBC-Hand загружен (bc_hand.json). " + var2.text2 + "; модель " + var2.source;
                  this.handAnchorNet = var2;
               } catch (Exception var6) {
                  this.status = "NeuroBC-Hand НЕ загружен (bc_hand.json): " + var6.getMessage();
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

   private InputStream method3868() throws IOException {
      for (Path var2 : List.of(Path.of("bc_hand.json"), Path.of("models", "bc_hand.json"), Path.of("..", "models", "bc_hand.json"))) {
         if (Files.isRegularFile(var2)) {
            return Files.newInputStream(var2);
         }
      }

      InputStream var3 = BcHand.class.getClassLoader().getResourceAsStream("models/bc_hand.json");
      if (var3 == null) {
         throw new FileNotFoundException("bc_hand.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return var3;
      }
   }

   public boolean method3869() {
      return this.handAnchorNet != null;
   }

   public void reset() {
      this.entry = null;
      this.rotation = null;
      this.entry42 = null;
      this.flag = false;
      this.obj = null;
      this.entry4 = null;
   }

   private static double method3870() {
      double var0 = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return var0 * var0 * var0 * 8.0 * 0.15;
   }

   private static BcHand.Type method3871() {
      if (AutoMineModule.INSTANCE.isEnabled()) {
         return BcHand.Type.MINE;
      } else {
         return AuraModule.INSTANCE.isEnabled() && AuraModule.INSTANCE.method6359(3) ? BcHand.Type.AURA : BcHand.Type.NONE;
      }
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      this.longValue++;
      if (this.entry42 != null) {
         this.method3872(null);
         this.rotation = null;
      }

      BcHand.Type var2 = method3871();
      if (mc.player != null && mc.world != null && var2 != BcHand.Type.NONE) {
         HandAnchorNet var3 = this.method3867();
         if (var3 == null) {
            this.reset();
         } else {
            if (this.entry == null || mc.world != this.obj) {
               this.entry = var3.method3398();
               this.flag = false;
               this.entry4 = null;
            }

            this.obj = mc.world;
            Rotation var4 = zenith.getRotationManager().method7844();
            double var5 = var4.getFloatValue();
            double var7 = var4.getFloatValue2();
            BcCameraNet.Entry4 var9 = new BcCameraNet.Entry4();
            var9.doubleValue = mc.player.getX();
            var9.doubleValue2 = mc.player.getY();
            var9.doubleValue3 = mc.player.getZ();
            PlayerInput var10 = mc.player.input.playerInput;
            var9.intValue = (var10.forward() ? 1 : 0)
               | (var10.backward() ? 2 : 0)
               | (var10.left() ? 4 : 0)
               | (var10.right() ? 8 : 0)
               | (var10.jump() ? 16 : 0)
               | (var10.sneak() ? 32 : 0)
               | (var10.sprint() ? 64 : 0);
            var9.flag = mc.player.isOnGround();
            LivingEntity var11 = var2 == BcHand.Type.AURA ? AuraModule.INSTANCE.method6358() : null;
            if (var11 != null && var11.isAlive()) {
               var9.doubleValue4 = var11.getX();
               var9.doubleValue5 = var11.getY();
               var9.doubleValue6 = var11.getZ();
               var9.intValue2 = var11.getId();
               var9.flag4 = var11.isSneaking();
            }

            var9.doubleValue7 = var5;
            var9.doubleValue8 = var7;
            if (this.flag) {
               var9.doubleValue9 = var5 - this.doubleValue;
               var9.doubleValue10 = var7 - this.doubleValue2;
               var9.flag5 = Math.abs(var9.doubleValue9) <= 180.0;
            } else {
               var9.flag5 = false;
            }

            this.doubleValue = var5;
            this.doubleValue2 = var7;
            this.flag = true;
            double[] var12 = null;
            boolean var13 = var9.intValue2 >= 0 && BcCameraNet.method3361(var9.doubleValue4, var9.doubleValue5, var9.doubleValue6);
            if (var13) {
               boolean var14 = this.entry4 != null
                  && this.entry4.intValue2 == var9.intValue2
                  && BcCameraNet.method3361(this.entry4.doubleValue4, this.entry4.doubleValue5, this.entry4.doubleValue6);
               var12 = var3.method3410(
                  new double[]{var9.doubleValue, var9.doubleValue2, var9.doubleValue3},
                  this.entry4 == null ? null : new double[]{this.entry4.doubleValue, this.entry4.doubleValue2, this.entry4.doubleValue3},
                  new double[]{var9.doubleValue4, var9.doubleValue5, var9.doubleValue6},
                  var14 ? new double[]{this.entry4.doubleValue4, this.entry4.doubleValue5, this.entry4.doubleValue6} : null,
                  var14,
                  var9.flag4,
                  (var9.intValue >> 5 & 1) != 0,
                  var5,
                  var7
               );
            }

            this.entry4 = var9;
            this.entry42 = var9;
            this.longValue4 = this.longValue;
            this.rotation2 = var4;
            this.doubleValue3 = method3870();
            if (var2 == BcHand.Type.AURA) {
               this.method3872(var12);
            }
         }
      } else {
         this.reset();
      }
   }

   private void method3872(double[] var1) {
      BcCameraNet.Entry4 var2 = this.entry42;
      HandAnchorNet var3 = this.handAnchorNet;
      this.entry42 = null;
      if (var2 != null && var3 != null && this.entry != null) {
         double var4 = this.doubleValue3;
         if (this.longValue3 != this.longValue4 - 1L) {
            this.entry.method3419();
         }

         double[] var6 = var2.flag5 ? new double[]{var2.doubleValue9, var2.doubleValue10} : new double[2];
         double[] var7 = var3.method3414(this.entry, var6, var1, var2.doubleValue7, var2.doubleValue8, var4, this.random);
         double[] var8 = HandAnchorNet.method3415(this.entry, var7[0], var7[1], var4, var2.doubleValue8);
         this.rotation = this.rotation2.method3140((float)var8[0], (float)var8[1]);
         this.method3873(this.longValue4, var2, var1, var7, var8, var4);
         this.longValue2 = this.longValue4;
      }
   }

   private void method3873(long var1, BcCameraNet.Entry4 var3, double[] var4, double[] var5, double[] var6, double var7) {
      if (this.intValue < 72000) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of("bc_hand_log.csv"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND
               );
               this.bufferedWriter
                  .write(
                     "tick,age,applied_prev,grid,x,y,z,keys,on_ground,tgt_id,tx,ty,tz,tgt_sneak,yaw,pitch,my,mp,mv_ok,cmd_yaw,cmd_pitch,act_yaw,act_pitch,q_yaw,q_pitch,cam_yaw,cam_pitch,model\n"
                  );
            }

            StringBuilder var9 = new StringBuilder(320);
            var9.append(var1)
               .append(',')
               .append(mc.player.age)
               .append(',')
               .append(this.longValue3 == var1 - 1L ? 1 : 0)
               .append(',')
               .append(var7)
               .append(',')
               .append(var3.doubleValue)
               .append(',')
               .append(var3.doubleValue2)
               .append(',')
               .append(var3.doubleValue3)
               .append(',')
               .append(var3.intValue)
               .append(',')
               .append(var3.flag ? 1 : 0)
               .append(',')
               .append(var3.intValue2)
               .append(',')
               .append(var3.doubleValue4)
               .append(',')
               .append(var3.doubleValue5)
               .append(',')
               .append(var3.doubleValue6)
               .append(',')
               .append(var3.flag4 ? 1 : 0)
               .append(',')
               .append(var3.doubleValue7)
               .append(',')
               .append(var3.doubleValue8)
               .append(',')
               .append(var3.doubleValue9)
               .append(',')
               .append(var3.doubleValue10)
               .append(',')
               .append(var3.flag5 ? 1 : 0)
               .append(',')
               .append(var4 == null ? Double.NaN : var4[0])
               .append(',')
               .append(var4 == null ? Double.NaN : var4[1])
               .append(',')
               .append(var5[0])
               .append(',')
               .append(var5[1])
               .append(',')
               .append(var6[0])
               .append(',')
               .append(var6[1])
               .append(',')
               .append(mc.player.getYaw())
               .append(',')
               .append(mc.player.getPitch())
               .append(',')
               .append(this.handAnchorNet != null ? this.handAnchorNet.source : "?");
            this.bufferedWriter.write(var9.append('\n').toString());
            if (++this.intValue % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException var10) {
            this.intValue = 72000;
         }
      }
   }

   public Rotation method3874(Rotation var1) {
      if (this.entry42 != null && this.longValue4 == this.longValue) {
         this.method3872(
            var1 == null
               ? null
               : new double[]{
                  BcCameraNet.method3357(var1.getFloatValue() - this.entry42.doubleValue7),
                  BcCameraNet.method3358(var1.getFloatValue2(), -90.0, 90.0) - this.entry42.doubleValue8
               }
         );
      }

      Rotation var2 = zenith.getRotationManager().method7844();
      if (this.rotation != null && this.longValue2 == this.longValue) {
         this.longValue3 = this.longValue;
         return this.rotation;
      } else {
         return var2;
      }
   }

   private static enum Type {
      NONE,
      AURA,
      MINE;
   }
}

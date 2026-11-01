package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.awt.Color;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "XrayBypass",
   category = ModuleCategory.MISC,
   description = "module.xraybypass.description"
)
public final class XrayBypassModule extends Module {
   public static final XrayBypassModule INSTANCE = new XrayBypassModule();
   private final SliderSetting range = new SliderSetting("module.xrayBypass.range", 30.0F, 1.0F, 128.0F, 2.0F, "module.xrayBypass.range.desc", "b");
   private final SliderSetting height = new SliderSetting("module.xrayBypass.height", 20.0F, 1.0F, 255.0F, 2.0F, "module.xrayBypass.height.desc", "b");
   private final SliderSetting delay = new SliderSetting("module.xrayBypass.delay", 3.0F, 1.0F, 5.0F, 1.0F, "module.xrayBypass.delay.desc", "t");
   private BlockPos blockPos;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private boolean flag = true;
   private final LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
   private StopWatch stopWatch = new StopWatch();

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onEnable() {
      if (mc.player != null && mc.world != null) {
         this.method4213(mc.player.getBlockPos(), this.range.getValue3(), this.height.getValue3());
         this.stopWatch = new StopWatch();
         this.stopWatch.reset();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   private void method4213(BlockPos var1, float var2, float var3) {
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();
      int var7 = var4 - (int)var2;
      this.intValue5 = var6 - (int)var2;
      this.intValue4 = var5;
      this.floatValue = var4 + var2;
      this.floatValue3 = var6 + var2;
      this.floatValue2 = var5 + var3;
      this.intValue = var7;
      this.intValue3 = this.intValue5;
      this.intValue2 = this.intValue4;
      this.flag = !(this.intValue <= this.floatValue) || !(this.intValue3 <= this.floatValue3) || !(this.intValue2 <= this.floatValue2);
      this.longOpenHashSet.clear();
      if (!this.flag) {
         int var8 = var7;

         while (var8 + 1 <= this.floatValue) {
            var8++;
         }

         int var9 = this.intValue5;

         while (var9 + 1 <= this.floatValue3) {
            var9++;
         }

         for (int var10 = var7 >> 4; var10 <= var8 >> 4; var10++) {
            for (int var11 = this.intValue5 >> 4; var11 <= var9 >> 4; var11++) {
               if (mc.world.isChunkLoaded(var10, var11)) {
                  this.longOpenHashSet.add(ChunkPos.toLong(var10, var11));
               }
            }
         }
      }

      this.blockPos = this.method4214();
   }

   private BlockPos method4214() {
      while (!this.flag) {
         int var1 = this.intValue;
         int var2 = this.intValue2;
         int var3 = this.intValue3;
         if (++this.intValue2 > this.floatValue2) {
            this.intValue2 = this.intValue4;
            if (++this.intValue3 > this.floatValue3) {
               this.intValue3 = this.intValue5;
               if (++this.intValue > this.floatValue) {
                  this.flag = true;
               }
            }
         }

         if (this.longOpenHashSet.contains(ChunkPos.toLong(var1 >> 4, var3 >> 4))) {
            return new BlockPos(var1, var2, var3);
         }
      }

      return null;
   }

   @EventTarget
   public void method4215(WorldRenderEvent var1) {
      BlockHitResult var2 = TargetUtils.method3216(mc.player.getCameraPosVec(1.0F), rotationManager.method7844(), 5.0, var0 -> true);
      if (var2.getType() != Type.MISS) {
         GlowUtil.method2230(new Box(var2.getBlockPos()), Color.RED.getRGB(), 1.0F);
      }

      if (this.blockPos != null) {
         GlowUtil.method2230(new Box(this.blockPos), Color.GREEN.getRGB(), 1.0F);
         GlowUtil.method2230(
            mc.player
               .getBoundingBox()
               .expand(this.range.getValue3(), 0.0, this.range.getValue3())
               .withMaxY(mc.player.getEntityPos().y + this.height.getValue3()),
            Zenith.getInstance().getStyleManager().getClientColor(90).method7287(),
            1.0F
         );
      }
   }

   @EventTarget
   public void method4216(PacketEvent var1) {
      if (var1.getPacket3() instanceof BlockUpdateS2CPacket) {
         this.stopWatch.reset();
      }
   }

   @EventTarget
   public void method4217(Event5 var1) {
      Rotation var2 = new Rotation(rotationManager.method7844().getFloatValue(), -90.0F);
      rotationManager.method7849(new RotationRequest(var2, () -> var2, rotationManager.getBcCameraTestnerv().getRotationConfig3()), 50, this);
   }

   @EventTarget(4)
   public void method4218(MoveInputEvent var1) {
      var1.method3044();
   }

   @EventTarget
   public void method4219(UpdateEvent var1) {
      BlockHitResult var2 = TargetUtils.method3216(mc.player.getCameraPosVec(1.0F), rotationManager.method7844(), 5.0, var0 -> true);
      if (var2.getType() != Type.MISS) {
         if (mc.player.age % 80 == 0) {
            Zenith.getInstance().getHudHolder().method6973("4", Text.of("Сломайте блок над  головой"), 4000L);
         }

         this.stopWatch.reset();
      } else {
         if (this.stopWatch.method7546(20000L)) {
            this.stopWatch.reset();
         }

         if (!this.stopWatch.method7546(1000L)) {
            for (int var3 = 0; var3 < this.delay.getValue3(); var3++) {
               if (this.blockPos != null) {
                  BlockPos var4 = this.blockPos;
                  UseItemUtils.method6502(var1x -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var4, Direction.UP, var1x));
                  UseItemUtils.method6502(var1x -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var4, Direction.UP, var1x));
                  this.blockPos = this.method4214();
               }
            }
         }
      }
   }
}

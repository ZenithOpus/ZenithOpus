package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.text.Text;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "SelfFlag",
   category = ModuleCategory.MISC,
   description = "module.selfflag.description"
)
public class SelfFlagModule extends Module {
   public static final SelfFlagModule INSTANCE = new SelfFlagModule();
   private final BooleanSetting overlay = new BooleanSetting("module.selfFlag.overlay", true);
   private final SliderSetting x = new SliderSetting("module.selfFlag.x", 6.0F, 0.0F, 400.0F, 1.0F);
   private final SliderSetting y = new SliderSetting("module.selfFlag.y", 60.0F, 0.0F, 400.0F, 1.0F);
   private final BooleanSetting warn = new BooleanSetting("module.selfFlag.warn", true);
   private static final float CONST_floatValue = 12.0F;
   private static final float CONST_floatValue2 = 30.0F;
   private static final float CONST_floatValue3 = 220.0F;
   private static final float CONST_floatValue4 = 500.0F;
   private static final float CONST_floatValue5 = 3.0F;
   private static final float CONST_floatValue6 = 3.6F;
   private static final long CONST_longValue = 3000L;
   private float floatValue;
   private float floatValue2;
   private boolean flag;
   private long longValue;
   private final Deque<long[]> deque = new ArrayDeque<>();
   private final Deque<float[]> deque2 = new ArrayDeque<>();
   private long longValue2;

   private SelfFlagModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag = false;
      this.longValue = 0L;
      this.deque.clear();
      this.deque2.clear();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null) {
         float var2 = mc.player.getYaw();
         float var3 = mc.player.getPitch();
         if (this.flag) {
            float var4 = Math.abs(this.method7982(var2 - this.floatValue));
            long var5 = System.currentTimeMillis();
            this.deque.addLast(new long[]{var5, (long)(var4 * 100.0F)});

            while (!this.deque.isEmpty() && var5 - ((long[])this.deque.peekFirst())[0] > 1000L) {
               this.deque.removeFirst();
            }
         }

         this.floatValue = var2;
         this.floatValue2 = var3;
         this.flag = true;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (!var1.method7100() && mc.player != null) {
         Packet var2 = var1.getPacket3();
         if (var2 instanceof PlayerInteractEntityC2SPacket) {
            long var3 = System.currentTimeMillis();
            float var5 = this.flag ? Math.abs(this.method7982(mc.player.getYaw() - this.floatValue)) : 0.0F;
            float var6 = this.longValue == 0L ? -1.0F : (float)(var3 - this.longValue);
            this.longValue = var3;
            float var7 = mc.targetedEntity != null ? mc.player.distanceTo(mc.targetedEntity) : -1.0F;
            float var8 = mc.targetedEntity != null ? 1.0F : 0.0F;
            this.deque2.addLast(new float[]{(float)var3, var5, var6, var7, var8});

            while (this.deque2.size() > 12) {
               this.deque2.removeFirst();
            }

            if (this.warn.isEnabled() && this.method7979() >= 70.0F && var3 - this.longValue2 > 4000L) {
               this.longValue2 = var3;
               Zenith.getInstance().getHudHolder().method6972("!", Text.of("SelfFlag: риск палева " + (int)this.method7979() + "%"));
            }
         }
      }
   }

   private float method7972() {
      long var1 = 0L;

      for (long[] var4 : this.deque) {
         var1 += var4[1];
      }

      return (float)var1 / 100.0F;
   }

   private float method7973(float var1, float var2, float var3) {
      if (var1 <= var2) {
         return 0.0F;
      } else {
         return var1 >= var3 ? 100.0F : (var1 - var2) / (var3 - var2) * 100.0F;
      }
   }

   private float method7974() {
      float var1 = 0.0F;

      for (float[] var3 : this.deque2) {
         if (this.method7978(var3) < 3000L) {
            var1 = Math.max(var1, var3[1]);
         }
      }

      return this.method7973(var1, 12.0F, 30.0F);
   }

   private float method7975() {
      float var1 = 0.0F;
      int var2 = 0;

      for (float[] var4 : this.deque2) {
         if (var4[2] > 0.0F && this.method7978(var4) < 9000L) {
            var1 += var4[2];
            var2++;
         }
      }

      if (var2 < 4) {
         return 0.0F;
      } else {
         var1 /= var2;
         float var7 = 0.0F;

         for (float[] var5 : this.deque2) {
            if (var5[2] > 0.0F && this.method7978(var5) < 9000L) {
               var7 += (var5[2] - var1) * (var5[2] - var1);
            }
         }

         float var9 = (float)Math.sqrt(var7 / var2) / Math.max(1.0F, var1);
         return this.method7973(0.5F - var9, 0.0F, 0.35F);
      }
   }

   private float method7976() {
      float var1 = 0.0F;

      for (float[] var3 : this.deque2) {
         if (var3[3] > 0.0F && this.method7978(var3) < 3000L) {
            var1 = Math.max(var1, var3[3]);
         }
      }

      return this.method7973(var1, 3.0F, 3.6F);
   }

   private float method7977() {
      int var1 = 0;
      int var2 = 0;

      for (float[] var4 : this.deque2) {
         if (this.method7978(var4) < 3000L) {
            var2++;
            if (var4[4] < 0.5F) {
               var1++;
            }
         }
      }

      return var2 == 0 ? 0.0F : (float)var1 / var2 * 100.0F;
   }

   private long method7978(float[] var1) {
      return System.currentTimeMillis() - (long)var1[0];
   }

   private boolean active() {
      return this.longValue != 0L && System.currentTimeMillis() - this.longValue < 3000L;
   }

   public float method7979() {
      if (!this.active()) {
         return 0.0F;
      } else {
         float var1 = this.method7974();
         float var2 = this.method7973(this.method7972(), 220.0F, 500.0F);
         float var3 = this.method7975();
         float var4 = this.method7976();
         float var5 = this.method7977();
         return Math.min(100.0F, var1 * 0.3F + var2 * 0.2F + var3 * 0.2F + var4 * 0.2F + var5 * 0.1F);
      }
   }

   @EventTarget
   public void method7980(DrawEvent var1) {
      if (this.overlay.isEnabled()) {
         CustomDrawContext var2 = var1.getCustomDrawContext();
         if (var2 != null) {
            ZenithStyle var3 = Zenith.getInstance().getStyleManager().getCurrentStyle();
            Font var4 = Fonts.NEW_MEDIUM.getFont(7.0F);
            Font var5 = Fonts.NEW_MEDIUM.getFont(6.0F);
            float var6 = this.x.getValue3();
            float var7 = this.y.getValue3();
            float var8 = 150.0F;
            float var9 = 11.0F;
            float var10 = 20.0F + var9 * 5.0F + 6.0F;
            ZenithColor var11 = var3.getHudBackground().getZenithColor();
            var2.drawRoundedRect(var6, var7, var8, var10, ProxyDotRadiusData.method8069(6.0F), var11);
            float var12 = this.method7979();
            ZenithColor var13 = var12 >= 70.0F
               ? new ZenithColor(239, 83, 83)
               : (var12 >= 40.0F ? new ZenithColor(242, 169, 59) : new ZenithColor(51, 214, 166));
            var2.drawText(var4, "DETECT RISK", var6 + 8.0F, var7 + 7.0F, var3.getTextEnable().getZenithColor());
            var2.drawText(var4, (int)var12 + "%", var6 + var8 - 8.0F - var4.width((int)var12 + "%"), var7 + 7.0F, var13);
            float var14 = var7 + 20.0F;
            this.method7981(var2, var5, "Snap  ", this.method7974(), var6, var14, var8, var3);
            this.method7981(var2, var5, "Spin  ", this.method7973(this.method7972(), 220.0F, 500.0F), var6, var14 + var9, var8, var3);
            this.method7981(var2, var5, "Ритм  ", this.method7975(), var6, var14 + var9 * 2.0F, var8, var3);
            this.method7981(var2, var5, "Reach ", this.method7976(), var6, var14 + var9 * 3.0F, var8, var3);
            this.method7981(var2, var5, "МимоПрицела", this.method7977(), var6, var14 + var9 * 4.0F, var8, var3);
         }
      }
   }

   private void method7981(CustomDrawContext var1, Font var2, String var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      var1.drawText(var2, var3, var5 + 8.0F, var6, var8.getTextSecondary().getZenithColor());
      float var9 = var5 + 62.0F;
      float var10 = var7 - 62.0F - 30.0F;
      var1.drawRoundedRect(var9, var6 + 1.5F, var10, 4.0F, ProxyDotRadiusData.method8069(2.0F), var8.getTextTertiary().getZenithColor().method7294(0.25F));
      ZenithColor var11 = var4 >= 70.0F ? new ZenithColor(239, 83, 83) : (var4 >= 40.0F ? new ZenithColor(242, 169, 59) : new ZenithColor(51, 214, 166));
      var1.drawRoundedRect(var9, var6 + 1.5F, Math.max(1.0F, var10 * var4 / 100.0F), 4.0F, ProxyDotRadiusData.method8069(2.0F), var11);
      var1.drawText(var2, (int)var4 + "", var5 + var7 - 8.0F - var2.width((int)var4 + ""), var6, var8.getTextSecondary().getZenithColor());
   }

   private float method7982(float var1) {
      while (var1 > 180.0F) {
         var1 -= 360.0F;
      }

      while (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }
}

package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.AttackEvent;

public final class Island implements MinecraftAccess {
   public static final Island INSTANCE = new Island();
   public static final int CONST_intValue = 100;
   public static final int CONST_intValue2 = 90;
   public static final int CONST_intValue3 = 80;
   public static final int CONST_intValue4 = 70;
   public static final int CONST_intValue5 = 40;
   public static final int CONST_intValue6 = 60;
   public static final int CONST_intValue7 = 75;
   public static final int CONST_intValue8 = 35;
   private static final long CONST_longValue = 15000L;
   private static final long CONST_longValue2 = 3000L;
   private static final long CONST_longValue3 = 4000L;
   private static final ZenithColor zenithColor = new ZenithColor(255, 107, 90, 255);
   private static final ZenithColor zenithColor2 = new ZenithColor(216, 222, 232, 255);
   private static final ZenithColor zenithColor3 = new ZenithColor(154, 123, 255, 255);
   private static final ZenithColor zenithColor4 = new ZenithColor(255, 196, 74, 255);
   private static final ZenithColor zenithColor5 = new ZenithColor(255, 138, 61, 255);
   private static final ZenithColor zenithColor6 = new ZenithColor(90, 216, 208, 255);
   private static final ZenithColor zenithColor7 = new ZenithColor(110, 180, 255, 255);
   private final List<Island.Entry> items = new ArrayList<>();
   private boolean flag;
   private WeakReference<PlayerEntity> weakReference = new WeakReference<>(null);

   private Island() {
   }

   public static void method2867() {
      if (!INSTANCE.flag) {
         INSTANCE.flag = true;
         EventManager.register(INSTANCE);
      }
   }

   private static Island.Entry method2868(String var0) {
      for (Island.Entry var2 : INSTANCE.items) {
         if (var2.text.equals(var0)) {
            return var2;
         }
      }

      return null;
   }

   private static Island.Entry method2869(String var0, int var1) {
      Island.Entry var2 = method2868(var0);
      if (var2 == null) {
         var2 = new Island.Entry(var0);
         var2.intValue = var1;
         var2.startedAt = System.currentTimeMillis();
         INSTANCE.items.add(var2);
      }

      return var2;
   }

   public static void method2870(String var0, String var1, ZenithColor var2, int var3, long var4, boolean var6) {
      if (!mc.isOnThread()) {
         mc.execute(() -> method2870(var0, var1, var2, var3, var4, var6));
      } else {
         long var7 = System.currentTimeMillis();
         Island.Entry var9 = method2869(var0, var3);
         var9.title = var1;
         var9.zenithColor = var2;
         var9.intValue = var3;
         var9.startedAt = var7;
         var9.longValue = var4 <= 0L ? 0L : var7 + var4;
         var9.flag = var6;
         var9.longValue2 = var7;
      }
   }

   public static void method2871(String var0, String var1, ZenithColor var2, int var3) {
      if (!mc.isOnThread()) {
         mc.execute(() -> method2871(var0, var1, var2, var3));
      } else {
         Island.Entry var4 = method2869(var0, var3);
         long var5 = System.currentTimeMillis();
         var4.title = var1;
         var4.zenithColor = var2;
         var4.intValue = var3;
         var4.longValue = 0L;
         var4.flag = false;
         var4.longValue2 = var5;
      }
   }

   public static void method2872(String var0, ZenithColor var1, float var2, String var3) {
      if (!mc.isOnThread()) {
         mc.execute(() -> method2872(var0, var1, var2, var3));
      } else {
         method2871("boss", var0, var1, 100);
         Island.Entry var4 = method2868("boss");
         var4.floatValue = var2;
         var4.text2 = var3;
      }
   }

   public static void method2873(String var0, String var1, ZenithColor var2, int var3, float var4) {
      if (!mc.isOnThread()) {
         mc.execute(() -> method2873(var0, var1, var2, var3, var4));
      } else {
         Island.Entry var5 = method2869(var0, var3);
         long var6 = System.currentTimeMillis();
         var5.title = var1;
         var5.zenithColor = var2;
         var5.intValue = var3;
         var5.longValue = var6 + 350L;
         var5.flag = false;
         var5.floatValue = -1.0F;
         var5.floatValue2 = var4;
         var5.flag2 = true;
         var5.longValue2 = var6;
      }
   }

   public static void method2874(String var0, float var1) {
      if (!mc.isOnThread()) {
         mc.execute(() -> method2874(var0, var1));
      } else {
         Island.Entry var2 = method2868(var0);
         if (var2 != null) {
            var2.floatValue = var1;
         }
      }
   }

   public static void drop(String var0) {
      if (!mc.isOnThread()) {
         mc.execute(() -> drop(var0));
      } else {
         INSTANCE.items.removeIf(var1 -> var1.text.equals(var0));
      }
   }

   public static Island.Entry method2875(long var0) {
      if (INSTANCE.weakReference.get() != mc.player) {
         INSTANCE.weakReference = new WeakReference<>(mc.player);
         INSTANCE.items.removeIf(var0x -> !var0x.text.equals("boss"));
      }

      INSTANCE.items.removeIf(var2x -> !var2x.method2883(var0) || !var2x.text.equals("boss") && !var2x.flag2);
      Island.Entry var2 = null;

      for (Island.Entry var4 : INSTANCE.items) {
         if (var2 == null || var4.intValue > var2.intValue || var4.intValue == var2.intValue && var4.longValue2 > var2.longValue2) {
            var2 = var4;
         }
      }

      return var2;
   }

   public static void method2876(long var0) {
      method2867();
      if (mc.player != null && mc.world != null) {
         if (INSTANCE.weakReference.get() != mc.player) {
            INSTANCE.weakReference = new WeakReference<>(mc.player);
            drop("combat");
            drop("totem");
            drop("stun");
            drop("trap");
         }

         if (mc.player.isDead()) {
            drop("combat");
            drop("totem");
         }

         if (!mc.player.isDead() && mc.player.hurtTime > 0 && mc.player.hurtTime == mc.player.maxHurtTime) {
            method2877();
         }

         BlockPos var2 = mc.player.getBlockPos().up();
         boolean var3 = mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.COBWEB) || mc.world.getBlockState(var2).isOf(Blocks.COBWEB);
         if (var3) {
            method2871("stun", method2882("hud.island.web"), zenithColor2, 90);
         } else {
            drop("stun");
         }
      } else {
         INSTANCE.items.clear();
         INSTANCE.weakReference.clear();
      }
   }

   public static void method2877() {
      method2870("combat", method2882("hud.island.combat"), zenithColor, 40, 15000L, true);
   }

   public static void method2878(String var0) {
      method2870("trap", var0, zenithColor3, 80, 4000L, true);
   }

   public static void method2879(String var0, long var1) {
      if (var1 > 0L) {
         switch (var0) {
            case "trap":
               method2870("helper.trap", method2882("hud.island.trap.small"), zenithColor3, 80, var1, true);
               break;
            case "bigTrap":
               method2870("helper.bigTrap", method2882("hud.island.trap.big"), zenithColor5, 81, var1, true);
               break;
            case "beacon":
               method2870("helper.beacon", method2882("hud.island.beacon"), zenithColor6, 60, var1, true);
               break;
            case "blast":
               method2870("helper.blast", method2882("hud.island.blast"), zenithColor5, 82, var1, true);
         }
      }
   }

   public static void method2880(String var0, long var1) {
      if (var1 > 0L && var0 != null && !var0.isBlank()) {
         method2870("helper.event", var0, zenithColor7, 60, var1, true);
      }
   }

   @EventTarget
   public void method2881(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.POST && var1.getEntity() instanceof PlayerEntity) {
         method2877();
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc.player != null) {
         if (var1.getPacket3() instanceof EntityStatusS2CPacket var2 && var2.getStatus() == 35) {
            mc.execute(() -> {
               if (mc.player != null && mc.world != null) {
                  Entity var1x = var2.getEntity(mc.world);
                  if (var1x == mc.player) {
                     method2870("totem", method2882("hud.island.totem"), zenithColor4, 70, 3000L, true);
                  }
               }
            });
         }
      }
   }

   private static String method2882(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   public static final class Entry {
      public final String text;
      public String title;
      public ZenithColor zenithColor;
      public int intValue;
      public long startedAt;
      public long longValue;
      public float floatValue = -1.0F;
      public boolean flag;
      public long longValue2;
      public float floatValue2 = Float.NaN;
      public boolean flag2;
      public String text2;

      private Entry(String var1) {
         this.text = var1;
      }

      public boolean method2883(long var1) {
         return this.longValue == 0L || var1 < this.longValue;
      }

      public float method2884(long var1) {
         if (this.flag && this.longValue != 0L) {
            float var3 = Math.max(1.0F, (float)(this.longValue - this.startedAt));
            return Math.clamp((float)(this.longValue - var1) / var3, 0.0F, 1.0F);
         } else {
            return this.floatValue;
         }
      }

      public String method2885(long var1) {
         if (this.text2 != null) {
            return this.text2;
         } else if (this.flag && this.longValue != 0L) {
            long var3 = Math.max(0L, this.longValue - var1);
            long var5 = (var3 + 999L) / 1000L;
            return var5 >= 60L ? var5 / 60L + ":" + String.format("%02d", var5 % 60L) : var5 + "s";
         } else {
            return this.floatValue >= 0.0F ? Math.round(this.floatValue * 100.0F) + "%" : "";
         }
      }
   }
}

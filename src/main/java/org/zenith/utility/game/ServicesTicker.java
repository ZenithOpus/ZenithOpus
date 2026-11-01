package org.zenith.utility.game;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.zenith.MinecraftAccess;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.api.CosmeticItemInfo;
import org.zenith.utility.api.Cosmetics;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.api.Share;
import org.zenith.utility.config.FiguraManager;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class ServicesTicker implements MinecraftAccess {
   private static final long CONST_longValue = 5000L;
   private final Cloud cloud;
   private final Cosmetics cosmetics;
   private final Share share;
   private final StopWatch stopWatch = new StopWatch();
   private String text;
   private String text2;
   private double lastX;
   private double lastY;
   private double doubleValue;
   private float floatValue;
   private float floatValue2;
   private long longValue;
   private float floatValue3 = -1.0F;
   private float floatValue4 = -1.0F;
   private float floatValue5 = -1.0F;
   private int intValue = -1;
   private long longValue2;
   private long longValue3;
   private long longValue4;
   private int intValue2 = -1;
   private boolean flag = true;
   private int intValue3 = -1;
   private String text3 = "";
   private long longValue5;
   private static final long CONST_longValue2 = 30000L;
   private int intValue4;
   private boolean flag2 = true;
   private boolean flag3;

   public ServicesTicker(Cloud var1, Cosmetics var2, Share var3) {
      this.cloud = var1;
      this.cosmetics = var2;
      this.share = var3;
      EventManager.register(this);
   }

   @EventTarget
   private void method1758(CloudStateEvent var1) {
      if (var1.method6858()) {
         this.method1759();
      }
   }

   private void method1759() {
      this.text = null;
      this.longValue = 0L;
      this.floatValue3 = -1.0F;
      this.longValue2 = 0L;
      this.longValue3 = 0L;
      this.longValue4 = 0L;
      this.intValue2 = -1;
      this.intValue3 = -1;
      this.text3 = "";
      this.longValue5 = 0L;
      this.flag = true;
   }

   @EventTarget
   private void onTick(TickEvent var1) {
      if (this.cloud.method7862()) {
         boolean var2 = this.share.position.isEnabled();
         if (var2 != this.flag2) {
            this.flag2 = var2;
            this.text = null;
            this.longValue = 0L;
         }

         if (++this.intValue4 % 20 == 0 || this.text == null) {
            String var3 = this.share.server.isEnabled() ? this.cloud.method7911() : "";
            String var4 = this.share.server.isEnabled() && var2 ? this.cloud.method7913() : "";
            if (!var3.equals(this.text) || !var4.equals(this.text2)) {
               this.text = var3;
               this.text2 = var4;
               this.cloud.method7897(var3, var4);
            }
         }

         this.method1760();
      }
   }

   private void method1760() {
      int var1 = this.share.cosmeticsVisibility.getIndex();
      if (var1 != this.intValue3) {
         this.intValue3 = var1;
         this.flag = true;
      }

      if (this.share.method949()) {
         if (this.flag) {
            this.flag = false;
            this.intValue2 = -1;
            this.cloud.method7901(List.of());
         }
      } else {
         int var2 = this.cosmetics.getIntValue();
         String var3 = method1761().method7678();
         String var4 = method1761().method7679();
         String var5 = method1762().method4555();
         float var6 = method1762().method4572();
         String var7 = var3 + "|" + var4 + "|" + var5 + "|" + var6;
         if (this.flag || var2 != this.intValue2 || !var7.equals(this.text3)) {
            Set<String> var8 = this.cosmetics.getItems10();
            if (!var8.isEmpty() && !this.cosmetics.method2972()) {
               long var9 = System.currentTimeMillis();
               if (this.longValue5 == 0L) {
                  this.longValue5 = var9;
               }

               if (var9 - this.longValue5 < 30000L) {
                  return;
               }
            } else {
               this.longValue5 = 0L;
            }

            this.flag = false;
            this.intValue2 = var2;
            this.text3 = var7;
            int var14 = this.share.colorSetting.method1445();
            List<CloudPackets.Data9> var10 = new ArrayList<>(var8.size() + 3);
            var10.addAll(ShareCodec.method6721(var3, var4, var5, var6));

            for (Object var12_o : (Iterable<?>)(Object)(var8)) {
               String var12 = (String)var12_o;
               CosmeticItemInfo var13 = this.cosmetics.method2971(var12);
               if (var13 != null && var13.getCosmeticType().method1682() && var12.length() <= 40) {
                  var10.add(new CloudPackets.Data9(var12, var13.getCosmeticType().ordinal(), var14));
               }
            }

            this.cloud.method7901(var10);
         }
      }
   }

   private static FiguraManager method1761() {
      return Zenith.getInstance().getFiguraManager();
   }

   private static PetManager method1762() {
      return Zenith.getInstance().getPetManager();
   }

   @EventTarget
   private void onUpdate(UpdateEvent var1) {
      if (this.cloud.method7862() && mc.player != null) {
         if (this.share.position.isEnabled()) {
            this.method1763();
         }

         if (this.share.health.isEnabled()) {
            this.method1764();
         }

         boolean var2 = this.share.inventory.isEnabled();
         if (var2) {
            this.method1765();
         } else if (this.flag3) {
            this.longValue3 = 0L;
            this.longValue4 = 0L;
            this.cloud.method7900(List.of());
         }

         this.flag3 = var2;
      }
   }

   private void method1763() {
      double var1 = mc.player.getX();
      double var3 = mc.player.getY();
      double var5 = mc.player.getZ();
      float var7 = mc.player.getYaw();
      float var8 = mc.player.getPitch();
      long var9 = System.currentTimeMillis();
      double var11 = var1 - this.lastX;
      double var13 = var3 - this.lastY;
      double var15 = var5 - this.doubleValue;
      boolean var17 = var11 * var11 + var13 * var13 + var15 * var15 > 4.0E-4
         || Math.abs(var7 - this.floatValue) > 0.5F
         || Math.abs(var8 - this.floatValue2) > 0.5F;
      if (var17 || var9 - this.longValue >= 2000L) {
         this.lastX = var1;
         this.lastY = var3;
         this.doubleValue = var5;
         this.floatValue = var7;
         this.floatValue2 = var8;
         this.longValue = var9;
         this.cloud.method7898(var1, var3, var5, var7, var8);
      }
   }

   private void method1764() {
      float var1 = mc.player.getHealth();
      float var2 = mc.player.getMaxHealth();
      float var3 = mc.player.getAbsorptionAmount();
      int var4 = mc.player.getHungerManager().getFoodLevel();
      long var5 = System.currentTimeMillis();
      boolean var7 = var1 == this.floatValue3 && var2 == this.floatValue4 && var3 == this.floatValue5 && var4 == this.intValue;
      if (!var7 || var5 - this.longValue2 >= 5000L) {
         if (this.stopWatch.method7546(50L) || !(this.floatValue3 >= 0.0F)) {
            this.stopWatch.reset();
            this.floatValue3 = var1;
            this.floatValue4 = var2;
            this.floatValue5 = var3;
            this.intValue = var4;
            this.longValue2 = var5;
            this.cloud.method7899(var1, var2, var3, var4);
         }
      }
   }

   private void method1765() {
      PlayerInventory var1 = mc.player.getInventory();
      int var2 = Math.min(var1.size(), 48);
      long var3 = 1469598103934665603L;

      for (int var5 = 0; var5 < var2; var5++) {
         ItemStack var6 = var1.getStack(var5);
         var3 = (var3 ^ (var6.isEmpty() ? 0 : System.identityHashCode(var6.getItem()))) * 1099511628211L;
         var3 = (var3 ^ var6.getCount()) * 1099511628211L;
         var3 = (var3 ^ var6.getDamage()) * 1099511628211L;
      }

      long var12 = System.currentTimeMillis();
      if (var3 != this.longValue3 || var12 - this.longValue4 >= 5000L) {
         this.longValue3 = var3;
         this.longValue4 = var12;
         List<CloudPackets.Data7> var7 = new ArrayList<>(var2);

         for (int var8 = 0; var8 < var2; var8++) {
            ItemStack var9 = var1.getStack(var8);
            var7.add(
               var9.isEmpty()
                  ? CloudPackets.Data7.INSTANCE
                  : new CloudPackets.Data7(Registries.ITEM.getId(var9.getItem()).toString(), Math.min(var9.getCount(), 255), Math.max(0, var9.getDamage()))
            );
         }

         this.cloud.method7900(var7);
      }
   }
}

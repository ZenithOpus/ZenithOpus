package org.zenith.client.hud;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class CooldownsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "n";
   private static final String text2 = "Cooldown";
   private final Map<String, CooldownsHud.Entry> items = new LinkedHashMap<>();
   private final Map<String, CooldownsHud.Entry2> items2 = new LinkedHashMap<>();
   private final Map<String, CooldownsHud.Entry2> items3 = new LinkedHashMap<>();
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);

   public CooldownsHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      EventManager.register(this);
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc != null && mc.world != null) {
         if (var1.getPacket3() instanceof CooldownUpdateS2CPacket var2) {
            this.method6829(() -> this.method6830(var2));
         } else if (var1.getPacket3() instanceof PlayerRespawnS2CPacket) {
            this.method6829(() -> {
               this.items3.clear();
               this.items2.clear();
               this.items.clear();
            });
         }
      }
   }

   private void method6829(Runnable var1) {
      if (mc.isOnThread()) {
         var1.run();
      } else {
         mc.execute(var1);
      }
   }

   private void method6830(CooldownUpdateS2CPacket var1) {
      if (mc.world != null) {
         Identifier var2 = var1.cooldownGroup();
         int var3 = var1.cooldown();
         String var4 = "vanilla:" + var2;
         if (var3 <= 0) {
            this.items3.remove(var4);
            this.items.remove(var4);
         } else {
            String var5;
            ItemStack var6;
            if (Registries.ITEM.containsId(var2)) {
               Item var7 = Registries.ITEM.get(var2);
               var5 = var7.getName().getString();
               var6 = var7.getDefaultStack();
            } else {
               ItemStack var9 = this.method6831(var2);
               if (var9 != null) {
                  var5 = var9.getName().getString();
                  var6 = var9.copyWithCount(1);
               } else {
                  var5 = var2.getPath();
                  var6 = ItemStack.EMPTY;
               }
            }

            long var10 = mc.world.getTime();
            this.items3.put(var4, CooldownsHud.Entry2.method6848(var4, var5, var6, var10, var3));
         }
      }
   }

   private ItemStack method6831(Identifier var1) {
      if (mc.player == null) {
         return null;
      } else {
         PlayerInventory var2 = mc.player.getInventory();

         for (int var3 = 0; var3 < var2.size(); var3++) {
            ItemStack var4 = var2.getStack(var3);
            if (!var4.isEmpty() && var4.contains(DataComponentTypes.USE_COOLDOWN) && var1.equals(mc.player.getItemCooldownManager().getGroup(var4))) {
               return var4;
            }
         }

         return null;
      }
   }

   @EventTarget
   public void method6832(PacketEvent var1) {
      if (mc != null && mc.player != null) {
         ItemStack var2 = mc.player.getActiveItem();
         if (var2 != null && !var2.isEmpty()) {
            if (mc.player.getItemUseTime() >= var2.getMaxUseTime(mc.player)) {
               PotionContentsComponent var3 = var2.get(DataComponentTypes.POTION_CONTENTS);
               if (var3 != null && (var3.getColor() == 33461 || var3.getColor() == -515037)) {
                  ItemStack var4 = Items.POTION.getDefaultStack();
                  var4.set(
                     DataComponentTypes.POTION_CONTENTS,
                     new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(var3.getColor()), List.of(), Optional.empty())
                  );
                  this.method6834(var4, "Исцел", 10000L);
               }
            }
         }
      }
   }

   public void method6833(ItemStack var1, long var2) {
      String var4 = "custom:" + var1.getItem().getTranslationKey();
      this.method6835(var4, var1.getItem().getName().getString(), var1, var2);
   }

   public void method6834(ItemStack var1, String var2, long var3) {
      this.method6835("custom:" + var2, var2, var1, var3);
   }

   public void method6835(String var1, String var2, ItemStack var3, long var4) {
      if (mc != null) {
         if (!mc.isOnThread()) {
            mc.execute(() -> this.method6835(var1, var2, var3, var4));
         } else if (!this.items.containsKey(var1)) {
            long var6 = System.nanoTime();
            this.items2.put(var1, CooldownsHud.Entry2.method6849(var1, var2, var3, var6, Math.max(1L, var4) * 1000000L));
         }
      }
   }

   public void method6836(String var1, String var2, ItemStack var3, long var4) {
      if (mc != null) {
         if (!mc.isOnThread()) {
            mc.execute(() -> this.method6836(var1, var2, var3, var4));
         } else if (mc.world != null && !this.items.containsKey(var1)) {
            long var6 = mc.world.getTime();
            this.items2.put(var1, CooldownsHud.Entry2.method6848(var1, var2, var3, var6, Math.max(1L, var4)));
         }
      }
   }

   @Override
   public void method8(CustomDrawContext var1) {
      if (mc != null) {
         this.method6837();
         if (this.items.isEmpty()) {
            this.animation2.method2808(0.0F);
         } else {
            CooldownsHud.Entry var2 = this.items.values().iterator().next();
            this.animation2.method2808(this.items.size() == 1 && var2.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
         }

         float var10 = this.floatValue;
         float var3 = this.floatValue2;
         float var4 = (float)(
            20.0F + GuiStyle.PADDING.intValue() / 2.0F
               + this.items.values().stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var0.animation.getValue()).sum()
         );
         float var5 = (float)this.items.values().stream().mapToDouble(CooldownsHud.Entry::method6842).max().orElse(0.0);
         var5 = this.animation.method2808(Math.max(HudListLayout.method7638("n", "Cooldown"), var5));
         this.width = var5;
         this.height = var4;
         float var6 = InterfaceModule.method6801();
         this.animation3.method2807(mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
         var1.pushMatrix();
         var1.getMatrices().translate(var10 + var5 / 2.0F, var3 + var4 / 2.0F);
         var1.getMatrices().scale(this.animation3.getValue(), this.animation3.getValue());
         var1.getMatrices().translate(-(var10 + var5 / 2.0F), -(var3 + var4 / 2.0F));
         HudListLayout.method7635(var1, var10, var3, var5, var4);
         HudListLayout.method7636(var1, var10, var3, var5, "n", "Cooldown");
         if (this.animation3.getValue() == 1.0F) {
            float var7 = var3 + 20.0F;
            var1.enableScissor((int)var10, (int)var3, (int)(var10 + var5), (int)(var3 + var4));

            for (CooldownsHud.Entry var9 : this.items.values()) {
               var9.method6843(var1, var10, var7, var5);
               var7 += (var9.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var9.animation.getValue();
            }

            var1.disableScissor();
         }

         this.items.entrySet().removeIf(var0 -> var0.getValue().method6844());
         this.method6839();
         var1.popMatrix();
      }
   }

   private void method6837() {
      long var1 = this.method6840();

      for (CooldownsHud.Entry2 var4 : this.items3.values()) {
         if (!var4.method6851(var1)) {
            this.items.computeIfAbsent(var4.text, var2 -> this.method2070(var4));
         }
      }

      for (CooldownsHud.Entry2 var8 : this.items2.values()) {
         long var5 = this.method6841(var8.type);
         if (!var8.method6851(var5)) {
            this.items.computeIfAbsent(var8.text, var2 -> this.method2070(var8));
         }
      }
   }

   private CooldownsHud.Entry method2070(CooldownsHud.Entry2 var1) {
      long var2 = this.method6841(var1.type);
      return new CooldownsHud.Entry(
         var1.itemStack,
         var1.text2,
         var1.method6850(var2),
         () -> var1.method6850(this.method6841(var1.type)),
         () -> var1.method6851(this.method6841(var1.type)),
         () -> var1.method6852(this.method6841(var1.type))
      );
   }

   private void method6839() {
      long var1 = this.method6840();
      this.items3.entrySet().removeIf(var2 -> var2.getValue().method6851(var1));
      this.items2.entrySet().removeIf(var1x -> var1x.getValue().method6851(this.method6841(var1x.getValue().type)));
   }

   private long method6840() {
      return mc != null && mc.world != null ? mc.world.getTime() : 0L;
   }

   private long method6841(CooldownsHud.Type var1) {
      return var1 == CooldownsHud.Type.TICKS ? this.method6840() : 0L;
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private class Entry {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private final Animation animation2;
      private final ItemStack itemStack;
      private final String text;
      private final Supplier<Float> supplier;
      private final BooleanSupplier booleanSupplier;
      private final Supplier<Integer> supplier2;
      private int intValue;
      private String text2;

      public Entry(ItemStack var2, String var3, float var4, Supplier<Float> var5, BooleanSupplier var6, Supplier<Integer> var7) {
         this.itemStack = var2;
         this.text = var3;
         this.supplier = var5;
         this.booleanSupplier = var6;
         this.supplier2 = var7;
         this.animation2 = new Animation(200L, var4, Easing.INSTANCE10);
      }

      public float method6842() {
         return HudListLayout.method7651(this.text, this.method6846());
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method6843(CustomDrawContext var1, float var2, float var3, float var4) {
         float var5 = this.method6845();
         this.animation.method2808(var5 > 0.0F ? 1.0F : 0.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         var1.getMatrices().scale(this.animation.getValue(), this.animation.getValue());
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         float var6 = HudListLayout.method7640(var1, var2, var3);
         float var7 = 8.0F;
         float var8 = var7 / 16.0F;
         var1.pushMatrix();
         var1.getMatrices().translate(HudListLayout.method7641(var2) + (10.0F - var7) / 2.0F, HudListLayout.method7642(var3) + (10.0F - var7) / 2.0F);
         var1.getMatrices().scale(var8, var8);
         var1.drawItem(this.itemStack, 0, 0);
         var1.popMatrix();
         HudListLayout.method7644(var1, var6, var3, this.text);
         HudListLayout.method7649(var1, var2, var3, var4, this.method6846(), var5);
         var1.popMatrix();
      }

      public boolean method6844() {
         return this.animation.getValue() == 0.0F && this.booleanSupplier.getAsBoolean();
      }

      private float method6845() {
         float var1 = MathHelper.clamp(this.supplier.get(), 0.0F, 1.0F);
         this.animation2.method2808(var1);
         return MathHelper.clamp(this.animation2.getValue(), 0.0F, 1.0F);
      }

      private String method6846() {
         int var1 = this.supplier2.get();
         if (this.text2 == null || var1 != this.intValue) {
            this.intValue = var1;
            this.text2 = this.method6847(var1);
         }

         return this.text2;
      }

      private String method6847(int var1) {
         if (var1 < 0) {
            var1 = 0;
         }

         int var2 = var1 / 60;
         int var3 = var1 % 60;
         return String.format("%d:%02d", var2, var3);
      }
   }

   private static class Entry2 {
      public final String text;
      public final String text2;
      public final ItemStack itemStack;
      public final CooldownsHud.Type type;
      public final long longValue;
      public final long longValue2;

      private Entry2(String var1, String var2, ItemStack var3, CooldownsHud.Type var4, long var5, long var7) {
         this.text = var1;
         this.text2 = var2;
         this.itemStack = var3;
         this.type = var4;
         this.longValue = var5;
         this.longValue2 = var7;
      }

      public static CooldownsHud.Entry2 method6848(String var0, String var1, ItemStack var2, long var3, long var5) {
         return new CooldownsHud.Entry2(var0, var1, var2, CooldownsHud.Type.TICKS, var3, var5);
      }

      public static CooldownsHud.Entry2 method6849(String var0, String var1, ItemStack var2, long var3, long var5) {
         return new CooldownsHud.Entry2(var0, var1, var2, CooldownsHud.Type.REAL, var3, var5);
      }

      public float method6850(long var1) {
         long var3 = switch (this.type) {
            case TICKS -> Math.max(0L, this.longValue + this.longValue2 - var1);
            case REAL -> Math.max(0L, this.longValue + this.longValue2 - System.nanoTime());
         };
         return this.longValue2 <= 0L ? 0.0F : (float)var3 / (float)this.longValue2;
      }

      public boolean method6851(long var1) {
         return switch (this.type) {
            case TICKS -> var1 >= this.longValue + this.longValue2;
            case REAL -> System.nanoTime() >= this.longValue + this.longValue2;
         };
      }

      public int method6852(long var1) {
         long var3 = switch (this.type) {
            case TICKS -> Math.max(0L, this.longValue + this.longValue2 - var1);
            case REAL -> Math.max(0L, this.longValue + this.longValue2 - System.nanoTime());
         };
         return this.type == CooldownsHud.Type.TICKS ? (int)(var3 / 20L) : (int)(var3 / 1000000000L);
      }
   }

   private static enum Type {
      TICKS,
      REAL;
   }
}

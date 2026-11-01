package org.zenith.client.hud;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class InterfaceHud extends Interface {
   private static final int CONST_intValue = 10;
   private static final float CONST_floatValue = 15.0F;
   private static final long CONST_longValue = 600000L;
   private final Deque<InterfaceHud.Entry> deque = new ArrayDeque<>();
   private final Set<String> items = new HashSet<>();
   private final Map<UUID, InterfaceHud.Entry4> items2 = new HashMap<>();
   private boolean flag = false;
   private final MultiSelectSetting types = new MultiSelectSetting("module.interface.notifications.types", "module.interface.notifications.types.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.modules", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.strength", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.armor", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.totems", true);
   private final ModeSetting direction = new ModeSetting(
      "module.interface.notifications.direction",
      "module.interface.notifications.direction.desc",
      "module.interface.notifications.direction.down",
      "module.interface.notifications.direction.up"
   );

   public InterfaceHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      EventManager.register(this);
   }

   public boolean method5083() {
      return this.entry.isEnabled();
   }

   public void method5084(Module var1, boolean var2) {
      this.method5085(var1, var2, 3000L);
   }

   public void method5085(Module var1, boolean var2, long var3) {
      this.method5088(new InterfaceHud.Entry3(var1, var2, var3));
   }

   public void method5086(String var1, Text var2) {
      this.method5087(var1, var2, 1500L);
   }

   public void method5087(String var1, Text var2, long var3) {
      this.method5088(new InterfaceHud.Entry2(var1, var2, var3));
   }

   private void method5088(InterfaceHud.Entry var1) {
      if (mc.isOnThread()) {
         this.deque.addLast(var1);
      } else {
         mc.execute(() -> this.deque.addLast(var1));
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      ClientPlayerEntity var2 = mc.player;
      if (var2 != null && mc.world != null) {
         if (this.method5092(this.entry2)) {
            this.method5090(var2);
         } else {
            this.flag = false;
         }

         if (this.method5092(this.entry3)) {
            this.method5091(var2);
         } else {
            this.items.clear();
         }

         if (this.method5092(this.entry4)) {
            if (var2.age % 20 == 0) {
               this.items2.values().removeIf(InterfaceHud.Entry4::method5101);
            }
         } else {
            this.items2.clear();
         }
      } else {
         this.method5093();
      }
   }

   @EventTarget
   public void method5089(EntityStatusEvent var1) {
      if (this.method5092(this.entry4) && mc.world != null) {
         EntityStatusS2CPacket var2 = var1.getEntityStatusS2CPacket();
         if (var2.getStatus() == 35) {
            if (var2.getEntity(mc.world) instanceof PlayerEntity var4 && var4 != mc.player) {
               InterfaceHud.Entry4 var5 = this.items2.computeIfAbsent(var4.getUuid(), var1x -> new InterfaceHud.Entry4());
               var5.method5100(var4);
            }
         }
      }
   }

   @Override
   public void method8(CustomDrawContext var1) {
      Font var2 = Fonts.NEW_MEDIUM.getFont(5.5F);
      long var3 = System.currentTimeMillis();
      var var5 = this.deque.iterator();
      ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      float var7 = this.floatValue;
      float var8 = this.floatValue2;
      float var9 = 17.0F;
      int var10 = 0;

      while (var5.hasNext()) {
         InterfaceHud.Entry var11 = (InterfaceHud.Entry)var5.next();
         if (!var11.flag && var3 - var11.longValue > var11.longValue2) {
            var11.flag = true;
            var11.animation2.method2808(0.0F);
         }

         if (var11.flag && var11.animation2.getValue() < 0.01F) {
            var5.remove();
         } else {
            if (!var11.flag) {
               if (var11.animation.isDone() && var11.animation.getValue() == 0.0F) {
                  var11.animation.reset(var10);
               }

               var11.animation.method2808(var10);
               var10++;
            }

            var11.animation2.method2808(var11.flag ? 0.0F : 1.0F);
         }
      }

      for (InterfaceHud.Entry var12 : this.deque) {
         float var13 = 6.0F;
         float var14 = var12.animation.getValue() * (var9 + var13);
         if (var14 < 100.0F) {
            var12.method5099(var1, var7, var8 + (this.direction.isSelected(0) ? var14 : -var14), var2, var6, var9, this);
         } else {
            var12.longValue = System.currentTimeMillis();
         }
      }
   }

   @Override
   public void method2069(CustomDrawContext var1) {
   }

   private void method5090(PlayerEntity var1) {
      StatusEffectInstance var2 = var1.getStatusEffect(StatusEffects.STRENGTH);
      if (var2 == null) {
         this.flag = false;
      } else {
         int var3 = var2.getDuration() / 20;
         if (var3 <= 10 && !this.flag) {
            this.flag = true;
            this.method5087("3", this.method5094(method5098("hud.notify.strength"), var3 + " " + method5098("hud.notify.sec")), 3500L);
         }

         if (var3 > 10) {
            this.flag = false;
         }
      }
   }

   private void method5091(PlayerEntity var1) {
      HashSet var2 = new HashSet();

      for (ItemStack var4 : ArmorSlots.method5384(var1)) {
         if (var4 != null && !var4.isEmpty() && var4.isDamageable()) {
            String var5 = var4.getItem().getTranslationKey();
            var2.add(var5);
            int var6 = var4.getMaxDamage();
            if (var6 > 0) {
               int var7 = var6 - var4.getDamage();
               float var8 = var7 * 100.0F / var6;
               if (var8 <= 15.0F && !this.items.contains(var5)) {
                  this.items.add(var5);
                  this.method5087(
                     "3",
                     this.method5094(method5098("hud.notify.durability"), var4.getItem().getName().getString() + " • " + String.format("%.1f", var8) + "%"),
                     3500L
                  );
               }

               if (var8 > 15.0F) {
                  this.items.remove(var5);
               }
            }
         }
      }

      this.items.retainAll(var2);
   }

   private boolean method5092(MultiSelectSetting.Entry var1) {
      return var1.isEnabled() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814() && !UseItemUtils.method6526();
   }

   private void method5093() {
      this.flag = false;
      this.items.clear();
      this.items2.clear();
   }

   private Text method5094(String var1, String var2) {
      return this.method5096(var1, Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287())
         .append(Text.literal(" "))
         .append(this.method5096(var2, Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()));
   }

   private Text method5095(PlayerEntity var1, int var2) {
      return this.method5096(
            var1.getGameProfile().name(), Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()
         )
         .append(
            this.method5096(
               " " + method5098("hud.notify.lost") + " ",
               Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()
            )
         )
         .append(this.method5096(var2 + " ", Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
         .append(
            this.method5096(this.method5097(var2) + ".", Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287())
         );
   }

   private MutableText method5096(String var1, int var2) {
      return Text.literal(var1).setStyle(Style.EMPTY.withColor(var2));
   }

   private String method5097(int var1) {
      int var2 = var1 % 100;
      if (var2 >= 11 && var2 <= 14) {
         return method5098("hud.notify.totems.5");
      } else {
         return switch (var1 % 10) {
            case 1 -> method5098("hud.notify.totems.1");
            case 2, 3, 4 -> method5098("hud.notify.totems.2");
            default -> method5098("hud.notify.totems.5");
         };
      }
   }

   private static String method5098(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private abstract static class Entry {
      public long longValue;
      public final long longValue2;
      public boolean flag = false;
      public final Animation animation = new Animation(300L, Easing.INSTANCE13);
      public final Animation animation2 = new Animation(300L, Easing.INSTANCE13);

      public Entry(long var1) {
         this.longValue = System.currentTimeMillis();
         this.longValue2 = var1;
      }

      public abstract void method5099(CustomDrawContext var1, float var2, float var3, Font var4, ZenithStyle var5, float var6, InterfaceHud var7);
   }

   private static class Entry2 extends InterfaceHud.Entry {
      public final String text;
      public final Text text2;

      public Entry2(String var1, Text var2, long var3) {
         super(var3);
         this.text = var1;
         this.text2 = var2;
      }

      @Override
      public void method5099(CustomDrawContext var1, float var2, float var3, Font var4, ZenithStyle var5, float var6, InterfaceHud var7) {
         float var8 = InterfaceModule.method6801();
         float var9 = 16.0F;
         ZenithColor var10 = var5.getHeaderHudBackground().getZenithColor();
         ZenithColor var11 = var5.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778());
         ZenithColor var12 = var5.getPrimaryColor().getZenithColor();
         float var13 = var4.width(this.text2);
         float var14 = var9 + 4.0F + var13 + 8.0F;
         var7.height = var6;
         var7.width = Math.max(var7.width, var14);
         var2 += (100.0F - var14) / 2.0F;
         float var15 = this.animation2.getValue();
         Font var17 = Fonts.ICONS.getFont(this.text.equals("Y") ? 8.0F : 6.0F);
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var2 + var14 / 2.0F, var3 + var6 / 2.0F);
         var1.getMatrices().scale(var15, var15);
         var1.getMatrices().translate(-(var2 + var14 / 2.0F), -(var3 + var6 / 2.0F));
         var1.drawBlurHud(var2, var3, var14, var6, 21.0F, ProxyDotRadiusData.method8069(var8), ZenithColor.INSTANCE);
         var1.drawRoundedRect(var2, var3, var14, var6, ProxyDotRadiusData.method8069(var8), var10);
         var1.drawRoundedRect(var2, var3, var9, var6, ProxyDotRadiusData.method8069(var8), var11);
         float var18 = var2 + (var9 - var17.width(this.text)) / 2.0F;
         float var19 = var3 + (var6 - var17.height()) / 2.0F;
         var1.drawText(var17, this.text, var18, var19, var12);
         float var20 = var2 + var9 + 4.0F;
         float var21 = var3 + (var6 - var4.height()) / 2.0F;
         var1.drawText(var4, this.text2, var20, var21);
         var1.getMatrices().popMatrix();
         var7.method4679(var1, var2, var3, var14, var6, var15);
      }
   }

   private static class Entry3 extends InterfaceHud.Entry {
      public final Module module;
      public final boolean flag;

      public Entry3(Module var1, boolean var2, long var3) {
         super(var3);
         this.module = var1;
         this.flag = var2;
      }

      @Override
      public void method5099(CustomDrawContext var1, float var2, float var3, Font var4, ZenithStyle var5, float var6, InterfaceHud var7) {
         float var8 = InterfaceModule.method6801();
         float var9 = 16.0F;
         ZenithColor var10 = var5.getHeaderHudBackground().getZenithColor();
         ZenithColor var11 = var5.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778());
         ZenithColor var12 = this.flag ? var5.getPrimaryColor().getZenithColor() : var5.getTextSecondary().getZenithColor();
         ZenithColor var13 = this.flag ? var5.getTextEnable().getZenithColor() : var5.getTextTertiary().getZenithColor();
         String var14 = this.module.getName();
         String var15 = "  "
            + Zenith.getInstance()
               .getLocalization()
               .translate(this.flag ? "module.interface.notifications.state.enabled" : "module.interface.notifications.state.disabled");
         float var16 = var4.width(var14);
         float var17 = var4.width(var15);
         float var18 = var9 + 4.0F + var16 + var17 + 8.0F;
         var7.height = var6;
         var7.width = Math.max(var7.width, var18);
         var2 += (100.0F - var18) / 2.0F;
         float var19 = this.animation2.getValue();
         Font var21 = Fonts.NEW_ICONS.getFont(6.0F);
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var2 + var18 / 2.0F, var3 + var6 / 2.0F);
         var1.getMatrices().scale(var19, var19);
         var1.getMatrices().translate(-(var2 + var18 / 2.0F), -(var3 + var6 / 2.0F));
         var1.drawBlurHud(var2, var3, var18, var6, 21.0F, ProxyDotRadiusData.method8069(var8), ZenithColor.INSTANCE);
         var1.drawRoundedRect(var2, var3, var18, var6, ProxyDotRadiusData.method8069(var8), var10);
         var1.drawRoundedRect(var2, var3, var9, var6, ProxyDotRadiusData.method8069(var8), var11);
         String var22 = this.module.getCategory().getIcon();
         float var23 = var2 + (var9 - var21.width(var22)) / 2.0F;
         float var24 = var3 + (var6 - var21.height()) / 2.0F;
         var1.drawText(var21, var22, var23, var24, var12);
         float var25 = var2 + var9 + 4.0F;
         float var26 = var3 + (var6 - var4.height()) / 2.0F;
         var1.drawText(var4, var14, var25, var26, var12);
         var1.drawText(var4, var15, var25 + var16, var26, var13);
         var1.getMatrices().popMatrix();
         var7.method4679(var1, var2, var3, var18, var6, var19);
      }
   }

   private class Entry4 {
      private long longValue = System.currentTimeMillis();
      private int intValue = 0;

      private void method5100(PlayerEntity var1) {
         this.longValue = System.currentTimeMillis();
         this.intValue++;
         InterfaceHud.this.method5087("Y", InterfaceHud.this.method5095(var1, this.intValue), 3000L);
      }

      private boolean method5101() {
         return System.currentTimeMillis() - this.longValue > 600000L;
      }
   }
}

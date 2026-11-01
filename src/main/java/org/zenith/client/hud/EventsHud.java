package org.zenith.client.hud;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.screen.ChatScreen;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.utility.misc.HolyWorldEvent;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class EventsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "L";
   private static final String text2 = "Events";
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Map<String, EventsHud.Entry> items = new LinkedHashMap<>();
   private final Set<String> items2 = new HashSet<>();

   public EventsHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      List<HolyWorldEvent> var2 = Zenith.getInstance().getHolyWorldEventsFetcher().getItems();
      this.items2.clear();

      for (Object var4_o : (Iterable<?>)(Object)(var2)) {
         HolyWorldEvent var4 = (HolyWorldEvent)var4_o;
         String var5 = this.method7013(var4);
         this.items2.add(var5);
         this.items.computeIfAbsent(var5, var2x -> new EventsHud.Entry(var4)).setHolyWorldEvent(var4);
      }

      this.items.values().removeIf(EventsHud.Entry::method7017);
      if (this.items.isEmpty()) {
         this.animation2.method2808(0.0F);
      } else {
         EventsHud.Entry var12 = this.items.values().iterator().next();
         this.animation2.method2808(this.items.size() == 1 && var12.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
      }

      float var13 = this.floatValue;
      float var14 = this.floatValue2;
      float var15 = (float)(
         20.0F + GuiStyle.PADDING.intValue() / 2.0F
            + this.items.values().stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var0.animation.getValue()).sum()
      );
      float var6 = (float)this.items.values().stream().mapToDouble(EventsHud.Entry::method7014).max().orElse(0.0);
      var6 = this.animation.method2808(Math.max(HudListLayout.method7638("L", "Events"), var6));
      this.width = var6;
      this.height = var15;
      this.animation3.method2807(mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
      float var7 = InterfaceModule.method6801();
      var1.pushMatrix();
      var1.getMatrices().translate(var13 + var6 / 2.0F, var14 + var15 / 2.0F);
      var1.getMatrices().scale(this.animation3.getValue(), this.animation3.getValue());
      var1.getMatrices().translate(-(var13 + var6 / 2.0F), -(var14 + var15 / 2.0F));
      HudListLayout.method7635(var1, var13, var14, var6, var15);
      HudListLayout.method7636(var1, var13, var14, var6, "L", "Events");
      if (this.animation3.getValue() == 1.0F) {
         float var8 = var14 + 20.0F;
         var1.enableScissor((int)var13, (int)var14, (int)(var13 + var6), (int)(var14 + var15));

         for (Map.Entry var10 : this.items.entrySet()) {
            EventsHud.Entry var11 = (EventsHud.Entry)var10.getValue();
            var11.method7016(var1, var13, var8, var6, this.items2.contains(var10.getKey()));
            var8 += (var11.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var11.animation.getValue();
         }

         var1.disableScissor();
      }

      var1.popMatrix();
   }

   private String method7013(HolyWorldEvent var1) {
      return var1.getType().name() + "|" + var1.getLongValue() + "|" + var1.getMessage();
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private class Entry {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private HolyWorldEvent holyWorldEvent;

      public Entry(HolyWorldEvent var2) {
         this.holyWorldEvent = var2;
      }

      public void setHolyWorldEvent(HolyWorldEvent var1) {
         this.holyWorldEvent = var1;
      }

      public float method7014() {
         return HudListLayout.method7648(this.holyWorldEvent.method7742(), this.method7015());
      }

      private String method7015() {
         String var1 = this.holyWorldEvent.getText2();
         return var1 != null && !var1.isEmpty() ? var1 : "-";
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method7016(CustomDrawContext var1, float var2, float var3, float var4, boolean var5) {
         this.animation.method2808(var5 ? 1.0F : 0.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         var1.getMatrices().scale(this.animation.getValue(), this.animation.getValue());
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         float var6 = HudListLayout.method7639(var1, var2, var3, this.holyWorldEvent.getIcon());
         HudListLayout.method7644(var1, var6, var3, this.holyWorldEvent.method7742());
         HudListLayout.method7646(var1, var2, var3, var4, this.method7015());
         var1.popMatrix();
      }

      public boolean method7017() {
         return this.animation.getValue() == 0.0F;
      }
   }
}

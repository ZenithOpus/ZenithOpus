package org.zenith.client.hud;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.gui.screen.ChatScreen;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class KeybindsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "n";
   private static final String text2 = "Keybinds";
   private final LinkedHashSet<KeybindsHud.Entry> linkedHashSet = new LinkedHashSet<>();
   private final Set<Module> items = Collections.newSetFromMap(new IdentityHashMap<>());
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);

   public KeybindsHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      for (Module var3 : Zenith.getInstance().getModuleManager().getItems()) {
         if (var3.isEnabled() && var3.getKeyCode() != -1 && !this.items.contains(var3)) {
            this.linkedHashSet.addLast(new KeybindsHud.Entry(var3));
            this.items.add(var3);
         }
      }

      var var12 = this.linkedHashSet.iterator();

      while (var12.hasNext()) {
         KeybindsHud.Entry var13 = (KeybindsHud.Entry)var12.next();
         if ((!var13.module.isEnabled() || var13.module.getKeyCode() == -1) && var13.method6488()) {
            var12.remove();
            this.items.remove(var13.module);
         }
      }

      if (this.linkedHashSet.isEmpty()) {
         this.animation2.method2808(0.0F);
      } else {
         this.animation2.method2808(this.linkedHashSet.size() == 1 && this.linkedHashSet.getFirst().animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
      }

      float var14 = this.floatValue;
      float var4 = this.floatValue2;
      float var5 = (float)(
         20.0F + GuiStyle.PADDING.intValue() / 2.0F
            + this.linkedHashSet.stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var0.animation.getValue()).sum()
      );
      float var6 = (float)this.linkedHashSet.stream().mapToDouble(KeybindsHud.Entry::method6486).max().orElse(0.0);
      var6 = this.animation.method2808(Math.max(HudListLayout.method7638("n", "Keybinds"), var6));
      this.width = var6;
      this.height = var5;
      this.animation3
         .method2807(mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.linkedHashSet.isEmpty());
      var1.pushMatrix();
      float var7 = InterfaceModule.method6801();
      var1.getMatrices().translate(var14 + var6 / 2.0F, var4 + var5 / 2.0F);
      var1.getMatrices().scale(this.animation3.getValue(), this.animation3.getValue());
      var1.getMatrices().translate(-(var14 + var6 / 2.0F), -(var4 + var5 / 2.0F));
      HudListLayout.method7635(var1, var14, var4, var6, var5);
      HudListLayout.method7636(var1, var14, var4, var6, "n", "Keybinds");
      if (this.animation3.getValue() == 1.0F) {
         float var8 = var4 + 20.0F;
         int var9 = 0;
         var1.enableScissor((int)var14, (int)var4, (int)(var14 + var6), (int)(var4 + var5));

         for (KeybindsHud.Entry var11 : this.linkedHashSet) {
            var11.method6487(var1, var14, var8, var6, var9, var7);
            var8 += (var11.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var11.animation.getValue();
            var9++;
         }

         var1.disableScissor();
      }

      var1.popMatrix();
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   public class Entry implements Comparable<KeybindsHud.Entry> {
      private final Animation animation = new Animation(150L, Easing.INSTANCE13);
      private final Module module;

      public Entry(Module var2) {
         this.module = var2;
      }

      private String method6485() {
         return KeyName.method6702(this.module.getKeyCode(), this.module.getKeyModifiers());
      }

      public float method6486() {
         return HudListLayout.method7648(this.module.getName(), this.method6485());
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method6487(CustomDrawContext var1, float var2, float var3, float var4, int var5, float var6) {
         this.animation.method2808(this.module.isEnabled() && this.module.getKeyCode() != -1 ? 1.0F : 0.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         float var7 = this.animation.getValue();
         var1.getMatrices().scale(var7, var7);
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         float var8 = HudListLayout.method7639(var1, var2, var3, this.module.getCategory().getIcon());
         HudListLayout.method7644(var1, var8, var3, this.module.getName());
         HudListLayout.method7646(var1, var2, var3, var4, this.method6485());
         var1.popMatrix();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            KeybindsHud.Entry var2 = (KeybindsHud.Entry)var1;
            return Objects.equals(this.module.getId(), var2.module.getId());
         } else {
            return false;
         }
      }

      public boolean method6488() {
         return this.animation.getValue() == 0.0F;
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.module.getId());
      }

      public int compareTo(KeybindsHud.Entry var1) {
         return this.module.getName().compareTo(var1.module.getName());
      }
   }
}

package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.lwjgl.glfw.GLFW;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class Class31 implements MinecraftAccess {
   private static final float CONST_floatValue = 5.0F;
   private static final float CONST_floatValue2 = 4.0F;
   private static final float CONST_floatValue3 = 32.0F;
   private static final float CONST_floatValue4 = 0.035F;
   private final Map<Interface, Class31.Entry2> items = new IdentityHashMap<>();
   private long longValue;
   private final List<Class31.Entry> items2 = new ArrayList<>();
   private Interface interfaceValue;
   private Interface interfaceValue2;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private long last;

   public boolean method4877(Interface var1, float var2, float var3) {
      if (var1 == null) {
         return false;
      } else {
         if (this.interfaceValue2 != null && this.interfaceValue2 != var1) {
            this.method4879(this.interfaceValue2);
         }

         this.interfaceValue = var1;
         this.interfaceValue2 = null;
         this.floatValue5 = var1.method294();
         this.floatValue6 = var1.method295();
         this.floatValue3 = this.floatValue5;
         this.floatValue4 = this.floatValue6;
         this.floatValue = var2 - this.floatValue5;
         this.floatValue2 = var3 - this.floatValue6;
         return true;
      }
   }

   public void end() {
      if (this.interfaceValue != null) {
         this.interfaceValue2 = this.interfaceValue;
         this.interfaceValue = null;
      }
   }

   public void cancel() {
      if (this.interfaceValue != null) {
         this.interfaceValue2 = this.interfaceValue;
         this.interfaceValue = null;
      }

      if (this.interfaceValue2 != null) {
         this.method4879(this.interfaceValue2);
      }
   }

   public Interface getInterfaceValue() {
      return this.interfaceValue;
   }

   public boolean method4878(Interface var1) {
      return var1 != null && (var1 == this.interfaceValue || var1 == this.interfaceValue2);
   }

   private void method4879(Interface var1) {
      float var2 = mc.getWindow().getScaledWidth();
      float var3 = mc.getWindow().getScaledHeight();
      var1.method2067(this.floatValue3, this.floatValue4, var2, var3);
      var1.release();
      var1.setFloatValue14(1.0F);
      if (this.interfaceValue2 == var1) {
         this.interfaceValue2 = null;
      }
   }

   public void method4880(CustomDrawContext var1, List<Interface> var2, float var3, float var4, float var5, float var6, boolean var7) {
      long var8 = System.nanoTime();
      float var10 = this.last == 0L ? 0.016F : Math.min(0.05F, (float)(var8 - this.last) / 1.0E9F);
      this.last = var8;
      this.floatValue8 = method4888(this.floatValue8, var7 ? 1.0F : 0.0F, var10 * 7.0F);
      this.floatValue7 = method4888(this.floatValue7, this.interfaceValue != null ? 1.0F : 0.0F, var10 * 9.0F);

      for (Class31.Entry var12 : this.items2) {
         var12.flag3 = false;
      }

      Interface var24 = this.interfaceValue != null ? this.interfaceValue : this.interfaceValue2;
      boolean var25 = false;
      boolean var13 = false;
      if (this.interfaceValue != null) {
         float var14 = var3 - this.floatValue;
         float var15 = var4 - this.floatValue2;
         boolean var16 = GLFW.glfwGetKey(mc.getWindow().getHandle(), 340) == 1;
         float var17 = this.interfaceValue.method296();
         float var18 = this.interfaceValue.method297();
         Class31.Data var19 = var16 ? null : method4886(var14, var17, this.method4884(var2, var5));
         Class31.Data var20 = var16 ? null : method4886(var15, var18, this.method4885(var2, var6));
         this.floatValue3 = var19 != null ? var19.getFloatValue() - var19.getFloatValue2() * var17 : var14;
         this.floatValue4 = var20 != null ? var20.getFloatValue() - var20.getFloatValue2() * var18 : var15;
         float var21 = InterfaceModule.INSTANCE.method6786();
         if (var21 >= 1.0F) {
            if (var19 == null) {
               this.floatValue3 = Math.round(this.floatValue3 / var21) * var21;
            }

            if (var20 == null) {
               this.floatValue4 = Math.round(this.floatValue4 / var21) * var21;
            }
         }

         this.floatValue3 = this.interfaceValue.method2088(this.floatValue3, var5);
         this.floatValue4 = this.interfaceValue.method2089(this.floatValue4, var6);
         if (var19 != null) {
            this.method4887(true, var19, this.floatValue4, this.floatValue4 + var18, var6);
            var25 = var19.isFlag();
         }

         if (var20 != null) {
            this.method4887(false, var20, this.floatValue3, this.floatValue3 + var17, var5);
            var13 = var20.isFlag();
         }
      }

      this.floatValue9 = method4888(this.floatValue9, this.interfaceValue != null ? 1.0F : 0.0F, var10 * 6.0F);
      if (var24 != null) {
         float var26 = 1.0F - (float)Math.exp(-var10 * 32.0F);
         this.floatValue5 = this.floatValue5 + (this.floatValue3 - this.floatValue5) * var26;
         this.floatValue6 = this.floatValue6 + (this.floatValue4 - this.floatValue6) * var26;
         var24.method2067(this.floatValue5, this.floatValue6, var5, var6);
         var24.setFloatValue14(1.0F + 0.035F * this.floatValue7);
         if (var24 == this.interfaceValue2
            && Math.abs(this.floatValue3 - this.floatValue5) < 0.15F
            && Math.abs(this.floatValue4 - this.floatValue6) < 0.15F
            && this.floatValue7 < 0.02F) {
            this.method4879(var24);
         }
      }

      var var27 = this.items2.iterator();

      while (var27.hasNext()) {
         Class31.Entry var28 = (Class31.Entry)var27.next();
         var28.alpha = method4888(var28.alpha, var28.flag3 ? 1.0F : 0.0F, var10 * (var28.flag3 ? 14.0F : 6.0F));
         if (!var28.flag3 && var28.alpha <= 0.01F) {
            var27.remove();
         }
      }

      Interface var29 = null;
      if (var7) {
         for (int var30 = var2.size() - 1; var30 >= 0; var30--) {
            if (((Interface)var2.get(var30)).method293(var3, var4)) {
               var29 = (Interface)var2.get(var30);
               break;
            }
         }
      }

      Interface var31 = var24 != null ? var24 : var29;
      long var32 = ++this.longValue;

      for (Interface var35 : var2) {
         float var37 = var35 == var31 && var7 ? 1.0F : 0.0F;
         Class31.Entry2 var22 = this.items.computeIfAbsent(var35, var0 -> new Class31.Entry2());
         float var23 = method4888(var22.value, var37, var10 * 12.0F);
         var22.value = var23;
         var22.longValue = var32;
         var35.method2066(var23 * Math.max(this.floatValue8, 0.001F), this.method4878(var35));
      }

      var var34 = this.items.entrySet().iterator();

      while (var34.hasNext()) {
         Map.Entry var36 = (Map.Entry)var34.next();
         if (((Class31.Entry2)var36.getValue()).longValue != var32) {
            ((Interface)var36.getKey()).method2066(0.0F, false);
            var34.remove();
         }
      }

      if (!(this.floatValue8 <= 0.01F)) {
         this.method4881(var1, var2, var5, var6, var29, var25, var13);
      }
   }

   private void method4881(CustomDrawContext var1, List<Interface> var2, float var3, float var4, Interface var5, boolean var6, boolean var7) {
      ZenithColor var8 = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
      ZenithColor var9 = ZenithColor.INSTANCE;
      if (this.floatValue9 > 0.01F) {
         float var10 = var3 / 2.0F;
         float var11 = var4 / 2.0F;
         this.method4883(var1, true, var10, 0.0F, var4, var9.method7292(0.1F * this.floatValue9 * this.floatValue8), var6);
         this.method4883(var1, false, var11, 0.0F, var3, var9.method7292(0.1F * this.floatValue9 * this.floatValue8), var7);
         var1.drawRoundedRect(
            var10 - 2.0F, var11 - 2.0F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), var9.method7292(0.25F * this.floatValue9 * this.floatValue8)
         );
      }

      for (Interface var19 : var2) {
         Class31.Entry2 var12 = this.items.get(var19);
         float var13 = var12 == null ? 0.0F : var12.value;
         if (var13 > 0.02F && !var19.method2059() && var19 == var5 && !this.method4878(var19)) {
            float var14 = 5.0F;
            float var15 = var19.method294() + var19.method296();
            float var16 = var19.method295() + var19.method297();
            var1.drawRoundedRect(
               var15 - var14 + 1.0F, var16 - var14 + 1.0F, var14, var14, ProxyDotRadiusData.method8069(1.5F), var8.method7292(0.85F * var13 * this.floatValue8)
            );
         }
      }

      for (Class31.Entry var20 : this.items2) {
         float var21 = var20.alpha * this.floatValue8;
         if (!(var21 <= 0.01F)) {
            ZenithColor var22 = (var20.flag2 ? var9.method7295(var8, 0.35F) : var8).method7292(0.9F * var21);
            if (var20.flag) {
               var1.drawRect(var20.floatValue - 0.5F, var20.floatValue2, 1.0F, var20.floatValue3 - var20.floatValue2, var22);
               method4882(var1, var20.floatValue, var20.floatValue2, var22);
               method4882(var1, var20.floatValue, var20.floatValue3, var22);
            } else {
               var1.drawRect(var20.floatValue2, var20.floatValue - 0.5F, var20.floatValue3 - var20.floatValue2, 1.0F, var22);
               method4882(var1, var20.floatValue2, var20.floatValue, var22);
               method4882(var1, var20.floatValue3, var20.floatValue, var22);
            }
         }
      }
   }

   private static void method4882(CustomDrawContext var0, float var1, float var2, ZenithColor var3) {
      var0.drawRoundedRect(var1 - 1.5F, var2 - 1.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), var3);
   }

   private void method4883(CustomDrawContext var1, boolean var2, float var3, float var4, float var5, ZenithColor var6, boolean var7) {
      if (!var7) {
         float var8 = 4.0F;
         float var9 = 4.0F;

         for (float var10 = var4; var10 < var5; var10 += var8 + var9) {
            float var11 = Math.min(var8, var5 - var10);
            if (var2) {
               var1.drawRect(var3 - 0.5F, var10, 1.0F, var11, var6);
            } else {
               var1.drawRect(var10, var3 - 0.5F, var11, 1.0F, var6);
            }
         }
      }
   }

   private List<Class31.Data> method4884(List<Interface> var1, float var2) {
      ArrayList<Class31.Data> var3 = new ArrayList<>();
      float var4 = this.interfaceValue.method2081(var2);
      float var5 = this.interfaceValue.method2082(var2);
      var3.add(new Class31.Data(var4, 0.0F, null, false));
      var3.add(new Class31.Data(var5, 1.0F, null, false));
      var3.add(new Class31.Data(var2 / 2.0F, 0.5F, null, true));

      for (Interface var7 : var1) {
         if (var7 != this.interfaceValue) {
            float var8 = var7.method294();
            float var9 = var8 + var7.method296();
            float var10 = (var8 + var9) / 2.0F;

            for (float var14 : new float[]{0.0F, 0.5F, 1.0F}) {
               var3.add(new Class31.Data(var8, var14, var7, false));
               var3.add(new Class31.Data(var10, var14, var7, false));
               var3.add(new Class31.Data(var9, var14, var7, false));
            }

            var3.add(new Class31.Data(var9 + 4.0F, 0.0F, var7, false));
            var3.add(new Class31.Data(var8 - 4.0F, 1.0F, var7, false));
         }
      }

      return var3;
   }

   private List<Class31.Data> method4885(List<Interface> var1, float var2) {
      ArrayList<Class31.Data> var3 = new ArrayList<>();
      float var4 = this.interfaceValue.method2084(var2);
      float var5 = this.interfaceValue.method2085(var2);
      var3.add(new Class31.Data(var4, 0.0F, null, false));
      var3.add(new Class31.Data(var5, 1.0F, null, false));
      var3.add(new Class31.Data(var2 / 2.0F, 0.5F, null, true));

      for (Interface var7 : var1) {
         if (var7 != this.interfaceValue) {
            float var8 = var7.method295();
            float var9 = var8 + var7.method297();
            float var10 = (var8 + var9) / 2.0F;

            for (float var14 : new float[]{0.0F, 0.5F, 1.0F}) {
               var3.add(new Class31.Data(var8, var14, var7, false));
               var3.add(new Class31.Data(var10, var14, var7, false));
               var3.add(new Class31.Data(var9, var14, var7, false));
            }

            var3.add(new Class31.Data(var9 + 4.0F, 0.0F, var7, false));
            var3.add(new Class31.Data(var8 - 4.0F, 1.0F, var7, false));
         }
      }

      return var3;
   }

   private static Class31.Data method4886(float var0, float var1, List<Class31.Data> var2) {
      Class31.Data var3 = null;
      float var4 = 5.0F;

      for (Class31.Data var6 : var2) {
         float var7 = Math.abs(var6.getFloatValue() - (var0 + var6.getFloatValue2() * var1));
         float var8 = var6.isFlag() ? 0.5F : 0.0F;
         if (var7 - var8 < var4) {
            var4 = var7 - var8;
            var3 = var6;
         }
      }

      return var3;
   }

   private void method4887(boolean var1, Class31.Data var2, float var3, float var4, float var5) {
      float var6;
      float var7;
      if (var2.getInterfaceValue() != null) {
         Interface var8 = var2.getInterfaceValue();
         float var9 = var1 ? var8.method295() : var8.method294();
         float var10 = var9 + (var1 ? var8.method297() : var8.method296());
         var6 = Math.min(var3, var9) - 3.0F;
         var7 = Math.max(var4, var10) + 3.0F;
      } else if (var2.isFlag()) {
         var6 = 0.0F;
         var7 = var5;
      } else {
         var6 = var3 - 6.0F;
         var7 = var4 + 6.0F;
      }

      for (Class31.Entry var13 : this.items2) {
         if (var13.flag == var1 && Math.abs(var13.floatValue - var2.getFloatValue()) < 0.5F) {
            var13.flag3 = true;
            var13.floatValue = var2.getFloatValue();
            var13.floatValue2 = var6;
            var13.floatValue3 = var7;
            return;
         }
      }

      Class31.Entry var12 = new Class31.Entry(var1, var2.isFlag(), var2.getFloatValue());
      var12.floatValue2 = var6;
      var12.floatValue3 = var7;
      var12.flag3 = true;
      this.items2.add(var12);
   }

   private static float method4888(float var0, float var1, float var2) {
      return var0 < var1 ? Math.min(var1, var0 + var2) : Math.max(var1, var0 - var2);
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final Interface interfaceValue;
      private final boolean flag;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public Interface interfaceValue() {
         return this.interfaceValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.interfaceValue, other.interfaceValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, interfaceValue, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "interfaceValue=" + this.interfaceValue + ", " + "flag=" + this.flag + "]";
      }

      private Data(float var1, float var2, Interface var3, boolean var4) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.interfaceValue = var3;
         this.flag = var4;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public Interface getInterfaceValue() {
         return this.interfaceValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Entry {
      public final boolean flag;
      public final boolean flag2;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float alpha;
      public boolean flag3;

      public Entry(boolean var1, boolean var2, float var3) {
         this.flag = var1;
         this.flag2 = var2;
         this.floatValue = var3;
      }
   }

   private static final class Entry2 {
      public float value;
      public long longValue;
   }
}

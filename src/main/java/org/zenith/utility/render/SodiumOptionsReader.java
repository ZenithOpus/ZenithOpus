package org.zenith.utility.render;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public final class SodiumOptionsReader {
   private static final String text = "net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI";
   private static final String text2 = "net.caffeinemc.mods.sodium.client.gui.options.Option";
   private static List<SodiumOptionsReader.Data> items;

   private SodiumOptionsReader() {
   }

   public static List<SodiumOptionsReader.Data> method1598() {
      if (items != null) {
         return items;
      } else {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null && var0.getWindow() != null) {
            try {
               Class var1 = Class.forName("net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI");
               Constructor var2 = null;

               for (Constructor var6 : var1.getDeclaredConstructors()) {
                  if (var6.getParameterCount() == 1) {
                     var2 = var6;
                  }
               }

               if (var2 == null) {
                  return List.of();
               }

               var2.setAccessible(true);
               Object var12 = var2.newInstance((Screen)null);
               Field var13 = var1.getDeclaredField("pages");
               var13.setAccessible(true);
               ArrayList var14 = new ArrayList();

               for (Object var7 : (List)var13.get(var12)) {
                  String var8 = ((Text)method1607(var7, "getName")).getString();
                  var var9 = new ArrayList<>((Collection)method1607(var7, "getOptions"));
                  if (!var9.isEmpty()) {
                     var14.add(new SodiumOptionsReader.Data(var8, var9));
                  }
               }

               items = List.copyOf(var14);
            } catch (ClassNotFoundException var10) {
               items = List.of();
            } catch (Throwable var11) {
               System.err.println("[Sodium] could not read option pages: " + var11);
               items = List.of();
            }

            return items == null ? List.of() : items;
         } else {
            return List.of();
         }
      }
   }

   public static ClickableWidget method1599(Object var0) {
      try {
         Object var1 = method1607(var0, "getControl");
         if (var1 == null) {
            return null;
         } else {
            String var2 = var1.getClass().getName();
            ClickableWidget var3;
            if (var2.endsWith("TickBoxControl")) {
               var3 = method1600(var0);
            } else if (var2.endsWith("CyclingControl")) {
               var3 = method1601(var0, var1);
            } else {
               if (!var2.endsWith("SliderControl") && !var2.endsWith("SliderControlExtended")) {
                  return null;
               }

               var3 = method1603(var0, var1);
            }

            var3.active = (Boolean)method1607(var0, "isAvailable");
            return var3;
         }
      } catch (Throwable var4) {
         return null;
      }
   }

   private static ClickableWidget method1600(Object var0) {
      String var1 = method1605(var0);
      boolean var2 = (Boolean)method1607(var0, "getValue");
      ButtonWidget[] var3 = new ButtonWidget[]{ButtonWidget.builder(method1604(var1, ScreenTexts.onOrOff(var2)), var2x -> {
         boolean var3x = !(Boolean)method1607(var0, "getValue");
         method1606(var0, var3x);
         var2x.setMessage(method1604(var1, ScreenTexts.onOrOff(var3x)));
      }).build()};
      return var3[0];
   }

   private static ClickableWidget method1601(Object var0, Object var1) throws Exception {
      String var2 = method1605(var0);
      Object[] var3 = (Object[])method1609(var1, "allowedValues");
      Text[] var4 = (Text[])method1609(var1, "names");
      return ButtonWidget.builder(method1604(var2, method1602(var0, var3, var4)), var4x -> {
         Object var5 = method1607(var0, "getValue");
         int var6 = 0;

         for (int var7 = 0; var7 < var3.length; var7++) {
            if (var3[var7] == var5) {
               var6 = var7;
            }
         }

         Object var8 = var3[(var6 + 1) % var3.length];
         method1606(var0, var8);
         var4x.setMessage(method1604(var2, method1602(var0, var3, var4)));
      }).build();
   }

   private static Text method1602(Object var0, Object[] var1, Text[] var2) {
      Object var3 = method1607(var0, "getValue");

      for (int var4 = 0; var4 < var1.length; var4++) {
         if (var1[var4] == var3) {
            if (var2 != null && var4 < var2.length && var2[var4] != null) {
               return var2[var4];
            }
            break;
         }
      }

      return Text.literal(String.valueOf(var3));
   }

   private static ClickableWidget method1603(final Object var0, Object var1) throws Exception {
      final int var2 = (Integer)method1609(var1, "min");
      int var3 = (Integer)method1609(var1, "max");
      final int var4 = (Integer)method1609(var1, "interval");
      final Object var5 = method1609(var1, "mode");
      final Method var6 = var5.getClass().getMethod("format", int.class);
      var6.setAccessible(true);
      final String var7 = method1605(var0);
      final int var8 = (Integer)method1607(var0, "getValue");
      final int var9 = Math.max(1, (var3 - var2) / var4);
      return new SliderWidget(0, 0, 150, 20, Text.empty(), (double)(var8 - var2) / (var3 - var2)) {
         private int intValue = var8;

         {
            this.updateMessage();
         }

         @Override
         public void updateMessage() {
            Object var1x;
            try {
               var1x = (Text)var6.invoke(var5, this.intValue);
            } catch (Throwable var3x) {
               var1x = Text.literal(String.valueOf(this.intValue));
            }

            this.setMessage(SodiumOptionsReader.method1604(var7, (Text)var1x));
         }

         @Override
         public void applyValue() {
            this.intValue = var2 + (int)Math.round(this.value * var9) * var4;
         }

         @Override
         public void onRelease(Click click) {
            SodiumOptionsReader.method1606(var0, this.intValue);
         }
      };
   }

   private static Text method1604(String var0, Text var1) {
      return Text.literal(var0 + ": ").append(var1);
   }

   private static String method1605(Object var0) {
      return ((Text)method1607(var0, "getName")).getString();
   }

   private static void method1606(Object var0, Object var1) {
      try {
         Class var2 = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.Option");
         Method var3 = var2.getMethod("setValue", Object.class);
         var3.invoke(var0, var1);
         var2.getMethod("applyChanges").invoke(var0);
         Object var4 = method1607(var0, "getStorage");
         if (var4 != null) {
            var4.getClass().getMethod("save").invoke(var4);
         }

         MinecraftClient var5 = MinecraftClient.getInstance();

         for (Object var7 : (Collection)method1607(var0, "getFlags")) {
            String var8 = String.valueOf(var7);
            switch (var8) {
               case "REQUIRES_RENDERER_RELOAD":
                  var5.worldRenderer.reload();
                  break;
               case "REQUIRES_RENDERER_UPDATE":
                  var5.worldRenderer.scheduleTerrainUpdate();
                  break;
               case "REQUIRES_ASSET_RELOAD":
                  var5.reloadResourcesConcurrently();
                  break;
               case "REQUIRES_VIDEOMODE_RELOAD":
                  var5.getWindow().applyFullscreenVideoMode();
            }
         }
      } catch (Throwable var10) {
      }
   }

   private static Object method1607(Object var0, String var1) {
      try {
         Method var2 = method1608(var0.getClass(), var1);
         var2.setAccessible(true);
         return var2.invoke(var0);
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Method method1608(Class<?> var0, String var1) throws NoSuchMethodException {
      for (Class var2 = var0; var2 != null; var2 = var2.getSuperclass()) {
         try {
            return var2.getDeclaredMethod(var1);
         } catch (NoSuchMethodException var4) {
         }
      }

      return var0.getMethod(var1);
   }

   private static Object method1609(Object var0, String var1) throws Exception {
      Field var2 = var0.getClass().getDeclaredField(var1);
      var2.setAccessible(true);
      return var2.get(var0);
   }

   public static final class Data {
      private final String text;
      private final List<Object> items;

      public String text() {
         return this.text;
      }

      public List<Object> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(String var1, List<Object> var2) {
         this.text = var1;
         this.items = var2;
      }

      public String name() {
         return this.text;
      }

      public List<Object> getItems() {
         return this.items;
      }
   }
}

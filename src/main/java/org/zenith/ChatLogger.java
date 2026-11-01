package org.zenith;

import lombok.Generated;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.ThemeStyle;

public final class ChatLogger implements MinecraftAccess {
   public static final Text text = method2023(ZenithColor.INSTANCE5, ZenithColor.INSTANCE, ZenithColor.INSTANCE7);
   private static final String text2 = "\\u00A7.";

   public static void method2019(String var0) {
      method2020(ChatLogger.Type.INFO, var0);
   }

   public static void method2020(ChatLogger.Type var0, String var1) {
      if (mc.player != null) {
         ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         MutableText var3 = method2023(
               var2.getPrimaryColor().getZenithColor(), var2.getSecondaryPrimaryColor().getZenithColor(), var2.getTextSecondary().getZenithColor()
            )
            .append(Text.literal(" "))
            .append(method2025(var1, var0, var2));
         mc.player.sendMessage(var3, false);
      }
   }

   public static void method2021(String var0) {
      method2020(ChatLogger.Type.WARN, var0);
   }

   public static void method2022(String var0) {
      method2020(ChatLogger.Type.ERROR, var0);
   }

   private static MutableText method2023(ZenithColor var0, ZenithColor var1, ZenithColor var2) {
      return Text.empty()
         .append(method2026("[ ", var2, false))
         .append(method2027("Zenith", var0.method7303(0.12F), var1.method7303(0.08F), true, 0))
         .append(method2026(" ]", var2, false));
   }

   private static MutableText method2024(ChatLogger.Type var0, ThemeStyle var1) {
      ZenithColor var2 = var0.getZenithColor().method7295(var1.getZenithColor(), 0.15F).method7303(0.08F);
      ZenithColor var3 = var2.method7295(var1.getZenithColor2(), 0.25F);
      return Text.empty()
         .append(method2026("<", var1.getZenithColor7(), false))
         .append(method2027(var0.getText(), var2, var3, true, 0))
         .append(method2026(">", var1.getZenithColor7(), false));
   }

   private static MutableText method2025(String var0, ChatLogger.Type var1, ZenithStyle var2) {
      String var3 = var0 == null ? "" : var0.replaceAll("\\u00A7.", "");
      int var4 = Math.min(8, var3.codePointCount(0, var3.length()));
      ZenithColor var5 = var1.getZenithColor().method7295(var2.getPrimaryColor().getZenithColor(), 0.3F).method7303(0.12F);
      ZenithColor var6 = var2.getTextEnable().getZenithColor().method7295(var2.getSecondaryPrimaryColor().getZenithColor(), 0.15F);
      return method2027(var3, var5, var6, false, var4);
   }

   private static MutableText method2026(String var0, ZenithColor var1, boolean var2) {
      return Text.literal(var0).setStyle(Style.EMPTY.withColor(var1.method7287()).withBold(var2));
   }

   private static MutableText method2027(String var0, ZenithColor var1, ZenithColor var2, boolean var3, int var4) {
      MutableText var5 = Text.empty();
      if (var0 != null && !var0.isEmpty()) {
         int var6 = var0.codePointCount(0, var0.length());
         int[] var7 = new int[]{0};
         var0.codePoints().forEach(var7x -> {
            float var8 = var6 <= 1 ? 0.0F : (float)var7[0] / (var6 - 1);
            ZenithColor var9 = var1.method7295(var2, var8);
            boolean var10 = var3 || var7[0] < var4;
            var5.append(Text.literal(new String(Character.toChars(var7x))).setStyle(Style.EMPTY.withColor(var9.method7287()).withBold(var10)));
            var7[0]++;
         });
         return var5;
      } else {
         return var5;
      }
   }

   @Generated
   private ChatLogger() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static enum Type {
      WARN("WARN", new ZenithColor(247, 206, 59)),
      ERROR("ERROR", new ZenithColor(242, 79, 68)),
      INFO("INFO", new ZenithColor(87, 126, 255));

      private final String text;
      private final ZenithColor zenithColor;

      @Generated
      public String getText() {
         return this.text;
      }

      @Generated
      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      @Generated
      private Type(String var3, ZenithColor var4) {
         this.text = var3;
         this.zenithColor = var4;
      }
   }
}

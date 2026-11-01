package org.zenith.client.screens.scripts;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class FormField {
   private final StringBuilder text = new StringBuilder();
   private final int maxLength;
   private final boolean multiline;
   private int caret;
   private boolean focused;
   private BoundsData bounds;
   private String original = "";
   private final Animation focus = new Animation(200L, 0.0F, Easing.INSTANCE15);
   private long typedAt;
   private boolean masked;
   private int maxLines = Integer.MAX_VALUE;
   private int minLines = 3;
   private int version;
   private int wrapVersion = -1;
   private Font wrapFont;
   private float wrapWidth = Float.NaN;
   private List<String> wrapLines;
   private int shownVersion = -1;
   private boolean shownMasked;
   private Font shownFont;
   private float shownWidth = Float.NaN;
   private String shownText;

   public FormField(int var1, boolean var2) {
      this.maxLength = var1;
      this.multiline = var2;
   }

   public void set(String var1) {
      this.text.setLength(0);
      this.text.append(var1 == null ? "" : var1);
      this.version++;
      this.caret = this.text.length();
      this.original = this.text.toString();
   }

   public String get() {
      return this.text.toString().trim();
   }

   public boolean changed() {
      return !this.text.toString().equals(this.original);
   }

   public void commit() {
      this.original = this.text.toString();
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setFocused(boolean var1) {
      this.focused = var1;
      if (var1) {
         this.caret = this.text.length();
      }
   }

   public FormField masked(boolean var1) {
      this.masked = var1;
      return this;
   }

   public FormField lines(int var1, int var2) {
      this.minLines = var1;
      this.maxLines = var2;
      return this;
   }

   public void clear() {
      this.text.setLength(0);
      this.version++;
      this.caret = 0;
   }

   public float height(Font var1, float var2) {
      if (!this.multiline) {
         return 16.0F;
      } else {
         int var3 = Math.min(this.maxLines, Math.max(this.minLines, this.wrap(var1, var2 - 12.0F).size()));
         return var3 * (var1.height() + 3.0F) + 8.0F;
      }
   }

   public BoundsData bounds() {
      return this.bounds;
   }

   public float render(
      MenuDrawContext var1,
      Font var2,
      float var3,
      float var4,
      float var5,
      float var6,
      String var7,
      ZenithColor var8,
      ZenithColor var9,
      ZenithColor var10,
      ZenithColor var11,
      ZenithColor var12
   ) {
      var var13 = this.multiline ? this.wrap(var2, var5 - 12.0F) : List.of(this.text.toString());
      int var14 = Math.max(0, var13.size() - this.maxLines);
      List<String> var15 = var14 > 0 ? var13.subList(var14, var13.size()) : var13;
      float var16 = var2.height() + 3.0F;
      float var17 = this.multiline ? Math.min(this.maxLines, Math.max(this.minLines, var15.size())) * var16 + 8.0F : 16.0F;
      this.bounds = new BoundsData(var3, var4, var5, var17);
      ProxyDotRadiusData var18 = ProxyDotRadiusData.method8069(4.0F);
      float var19 = this.focus.method2808(this.focused ? 1.0F : 0.0F);
      var1.drawRoundedRect(var3, var4, var5, var17, var18, var8.method7295(var10.method7292(0.08F), var19 * 0.6F).method7294(var6));
      var1.drawRoundedBorder(var3, var4, var5, var17, 0.15F, var18, var9.method7295(var10, var19).method7294(var6));
      if (var19 > 0.01F) {
         var1.drawRoundedBorder(
            var3 - var19,
            var4 - var19,
            var5 + var19 * 2.0F,
            var17 + var19 * 2.0F,
            0.12F,
            ProxyDotRadiusData.method8069(4.0F + var19),
            var10.method7292(0.18F * var19).method7294(var6)
         );
      }

      long var20 = System.currentTimeMillis() - this.typedAt;
      float var22 = var20 < 500L ? 1.0F : 0.5F + 0.5F * (float)Math.cos((var20 - 500L) / 530.0 * Math.PI);
      boolean var23 = this.focused && var22 > 0.02F;
      if (this.text.isEmpty() && !this.focused) {
         var1.drawText(
            var2, var2.clip(var7, var5 - 12.0F), var3 + 6.0F, var4 + (this.multiline ? 5.0F : (var17 - var2.height()) / 2.0F), var12.method7294(var6)
         );
         return var17;
      } else if (!this.multiline) {
         String var29 = this.masked ? "*".repeat(this.text.length()) : this.text.toString();
         String var30;
         if (this.shownVersion == this.version && this.shownMasked == this.masked && sameFont(this.shownFont, var2) && this.shownWidth == var5) {
            var30 = this.shownText;
         } else {
            var30 = var29;

            while (var2.width(var30) > var5 - 14.0F && var30.length() > 1) {
               var30 = var30.substring(1);
            }

            this.shownVersion = this.version;
            this.shownMasked = this.masked;
            this.shownFont = var2;
            this.shownWidth = var5;
            this.shownText = var30;
         }

         float var32 = var4 + (var17 - var2.height()) / 2.0F;
         var1.drawText(var2, var30, var3 + 6.0F, var32, var11.method7294(var6));
         if (var23) {
            int var33 = var29.length() - var30.length();
            float var34 = var3 + 6.0F + var2.width(var30.substring(0, Math.max(0, Math.min(var30.length(), this.caret - var33))));
            var1.drawRect(var34, var32 - 0.5F, 0.7F, var2.height() + 1.0F, var11.method7294(var6 * var22 * var19));
         }

         return var17;
      } else {
         float var24 = var4 + 5.0F;
         int var25 = 0;

         for (int var26 = 0; var26 < var14; var26++) {
            var25 += ((String)var13.get(var26)).length();
         }

         for (Object var27_o : (Iterable<?>)(Object)(var15)) {
            String var27 = (String)var27_o;
            var1.drawText(var2, var27, var3 + 6.0F, var24, var11.method7294(var6));
            if (var23 && this.caret >= var25 && this.caret <= var25 + var27.length()) {
               float var28 = var3 + 6.0F + var2.width(var27.substring(0, this.caret - var25));
               var1.drawRect(var28, var24 - 0.5F, 0.7F, var2.height() + 1.0F, var11.method7294(var6 * var22 * var19));
               var23 = false;
            }

            var25 += var27.length();
            var24 += var16;
         }

         return var17;
      }
   }

   private List<String> wrap(Font var1, float var2) {
      if (this.wrapVersion == this.version && sameFont(this.wrapFont, var1) && this.wrapWidth == var2) {
         return this.wrapLines;
      } else {
         var var3 = this.wrapUncached(var1, var2);
         this.wrapVersion = this.version;
         this.wrapFont = var1;
         this.wrapWidth = var2;
         this.wrapLines = var3;
         return var3;
      }
   }

   private static boolean sameFont(Font var0, Font var1) {
      return var0 != null && var0.getFont() == var1.getFont() && var0.getSize() == var1.getSize();
   }

   private List<String> wrapUncached(Font var1, float var2) {
      ArrayList<String> var3 = new ArrayList<>();
      String var4 = this.text.toString();
      int var5 = 0;

      while (var5 < var4.length()) {
         int var6 = var5;

         int var7;
         for (var7 = -1; var6 < var4.length() && var1.width(var4.substring(var5, var6 + 1)) <= var2; var6++) {
            if (var4.charAt(var6) == ' ') {
               var7 = var6;
            }
         }

         if (var6 < var4.length() && var7 > var5) {
            var6 = var7 + 1;
         }

         if (var6 == var5) {
            var6 = var5 + 1;
         }

         var3.add(var4.substring(var5, var6));
         var5 = var6;
      }

      if (var3.isEmpty()) {
         var3.add("");
      }

      return var3;
   }

   public boolean keyPressed(int var1, int var2) {
      if (!this.focused) {
         return false;
      } else {
         this.typedAt = System.currentTimeMillis();
         boolean var3 = (var2 & 2) != 0;
         switch (var1) {
            case 65:
               if (var3) {
                  this.caret = this.text.length();
               }
               break;
            case 67:
               if (var3 && !this.masked) {
                  MinecraftClient.getInstance().keyboard.setClipboard(this.text.toString());
               }
               break;
            case 86:
               if (var3) {
                  String var5 = MinecraftClient.getInstance().keyboard.getClipboard();
                  if (var5 != null) {
                     this.insert(var5.replace("\r", " ").replace("\n", " "));
                  }
               }
               break;
            case 256:
            case 257:
            case 258:
            case 335:
               this.focused = false;
               break;
            case 259:
               if (var3) {
                  int var4 = this.caret;

                  while (var4 > 0 && this.text.charAt(var4 - 1) == ' ') {
                     var4--;
                  }

                  while (var4 > 0 && this.text.charAt(var4 - 1) != ' ') {
                     var4--;
                  }

                  this.text.delete(var4, this.caret);
                  this.version++;
                  this.caret = var4;
               } else if (this.caret > 0) {
                  this.text.deleteCharAt(--this.caret);
                  this.version++;
               }
               break;
            case 261:
               if (this.caret < this.text.length()) {
                  this.text.deleteCharAt(this.caret);
                  this.version++;
               }
               break;
            case 262:
               this.caret = Math.min(this.text.length(), this.caret + 1);
               break;
            case 263:
               this.caret = Math.max(0, this.caret - 1);
               break;
            case 268:
               this.caret = 0;
               break;
            case 269:
               this.caret = this.text.length();
         }

         return true;
      }
   }

   public boolean charTyped(char var1) {
      if (this.focused && var1 >= ' ') {
         this.insert(String.valueOf(var1));
         return true;
      } else {
         return false;
      }
   }

   private void insert(String var1) {
      this.typedAt = System.currentTimeMillis();
      int var2 = this.maxLength - this.text.length();
      if (var2 > 0) {
         String var3 = var1.length() > var2 ? var1.substring(0, var2) : var1;
         this.text.insert(this.caret, var3);
         this.version++;
         this.caret = this.caret + var3.length();
      }
   }
}

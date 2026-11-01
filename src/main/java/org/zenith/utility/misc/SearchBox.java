package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class SearchBox implements MinecraftAccess {
   private static final Map<Character, Character> items = Map.ofEntries(
      Map.entry('й', 'q'),
      Map.entry('ц', 'w'),
      Map.entry('у', 'e'),
      Map.entry('к', 'r'),
      Map.entry('е', 't'),
      Map.entry('н', 'y'),
      Map.entry('г', 'u'),
      Map.entry('ш', 'i'),
      Map.entry('щ', 'o'),
      Map.entry('з', 'p'),
      Map.entry('х', '['),
      Map.entry('ъ', ']'),
      Map.entry('ф', 'a'),
      Map.entry('ы', 's'),
      Map.entry('в', 'd'),
      Map.entry('а', 'f'),
      Map.entry('п', 'g'),
      Map.entry('р', 'h'),
      Map.entry('о', 'j'),
      Map.entry('л', 'k'),
      Map.entry('д', 'l'),
      Map.entry('ж', ';'),
      Map.entry('э', '\''),
      Map.entry('я', 'z'),
      Map.entry('ч', 'x'),
      Map.entry('с', 'c'),
      Map.entry('м', 'v'),
      Map.entry('и', 'b'),
      Map.entry('т', 'n'),
      Map.entry('ь', 'm'),
      Map.entry('б', ','),
      Map.entry('ю', '.'),
      Map.entry('ё', '`')
   );
   private String text = "";
   private boolean selected;
   private boolean flag;
   private int cursor;
   private float floatValue;
   private Font font;
   private Vector2f vector2f;
   private String text2;
   private float width;
   private float height;
   private boolean multiline;
   private long longValue = System.currentTimeMillis();
   private int maxLength = Integer.MAX_VALUE;
   private SearchBox.Type type = SearchBox.Type.ANY;
   private SearchBox.Type2 type2 = SearchBox.Type2.NO_LIMIT;
   private float scrollOffset = 0.0F;
   private boolean masked;
   private final transient SearchBox.Entry entry = new SearchBox.Entry();
   public Animation animation = new Animation(400L, 0.2F, Easing.INSTANCE13);

   public SearchBox(Vector2f var1, Font var2, String var3, float var4) {
      this.font = var2;
      this.text2 = var3;
      this.width = var4;
      this.vector2f = var1;
   }

   public void method987(CustomDrawContext var1, float var2, float var3, ZenithColor var4, ZenithColor var5) {
      this.vector2f = new Vector2f(var2, var3);
      this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
      this.floatValue = var2;
      boolean var6 = this.isEmpty();
      if (this.multiline) {
         this.method988(var1, var2, var3, var4, var5, var6);
      } else {
         String var7 = this.masked ? "*".repeat(this.text.length()) : this.text;
         float var8 = this.width;
         SearchBox.Entry var9 = this.entry;
         int var10;
         int var11;
         if (var9.font == this.font && var9.intValue == this.cursor && var9.flag == this.masked && var9.floatValue == var8 && this.text.equals(var9.text)) {
            var10 = var9.intValue2;
            var11 = var9.intValue3;
         } else {
            var10 = 0;

            while (this.font.width(var7.substring(var10, this.cursor)) > var8) {
               var10++;
            }

            var11 = this.cursor;

            while (var11 < var7.length() && this.font.width(var7.substring(var10, var11 + 1)) <= var8) {
               var11++;
            }

            var9.text = this.text;
            var9.flag = this.masked;
            var9.intValue = this.cursor;
            var9.floatValue = var8;
            var9.font = this.font;
            var9.intValue2 = var10;
            var9.intValue3 = var11;
         }

         String var12 = var7.substring(var10, var11);
         if (this.flag && !var12.isEmpty()) {
            var1.drawRect(var2 - 1.0F, var3 - 1.0F, Math.min(this.font.width(var12), var8) + 2.0F, this.font.height() + 2.0F, var5.method7294(0.5F));
         }

         if (var6) {
            var1.drawText(this.font, this.method994(this.text2), var2, var3, var5);
         } else {
            var1.drawText(this.font, var12, var2, var3, var4);
         }

         if (this.selected && System.currentTimeMillis() - this.longValue > 200L) {
            float var13 = var6 ? 0.0F : this.font.width(var7.substring(var10, this.cursor));
            float var14 = this.floatValue + Math.min(var13, this.width);
            this.animation.setDuration(250L);
            var1.drawRect(
               var14,
               var3 - 1.0F,
               1.0F,
               this.font.height() + 2.0F,
               var4.method7294(
                  this.animation
                     .method2808(this.animation.getValue() == 0.2F ? 1.0F : (this.animation.getValue() == 1.0F ? 0.2F : this.animation.getFloatValue2()))
               )
            );
         }
      }
   }

   private void method988(CustomDrawContext var1, float var2, float var3, ZenithColor var4, ZenithColor var5, boolean var6) {
      String var7 = this.masked ? "*".repeat(this.text.length()) : this.text;
      if (var6) {
         var var8 = this.method989(1, this.text2 == null ? "" : this.text2);

         for (int var9 = 0; var9 < var8.size() && (var9 + 1) * this.font.height() <= this.height; var9++) {
            var1.drawText(this.font, (String)var8.get(var9), var2, var3 + var9 * this.font.height(), var5);
         }
      } else {
         var var12 = this.method989(0, var7);
         float var14 = this.font.height();

         for (int var10 = 0; var10 < var12.size() && (var10 + 1) * var14 <= this.height; var10++) {
            var1.drawText(this.font, (String)var12.get(var10), var2, var3 + var10 * var14, var4);
         }
      }

      float var13 = this.font.height();
      if (this.selected && System.currentTimeMillis() - this.longValue > 200L) {
         var var15 = this.method989(2, var7.substring(0, this.cursor));
         int var16 = Math.max(0, var15.size() - 1);
         if ((var16 + 1) * var13 <= this.height) {
            String var11 = var15.isEmpty() ? "" : (String)var15.get(var16);
            this.animation.setDuration(250L);
            var1.drawRect(
               var2 + this.font.width(var11),
               var3 + var16 * var13 - 1.0F,
               1.0F,
               var13 + 2.0F,
               var4.method7294(
                  this.animation
                     .method2808(this.animation.getValue() == 0.2F ? 1.0F : (this.animation.getValue() == 1.0F ? 0.2F : this.animation.getFloatValue2()))
               )
            );
         }
      }
   }

   private List<String> method989(int var1, String var2) {
      SearchBox.Entry var3 = this.entry;
      if (var3.fonts[var1] == this.font && var3.floatValues[var1] == this.width && var2.equals(var3.texts[var1])) {
         return var3.itemss[var1];
      } else {
         var var4 = Collections.unmodifiableList(this.method990(var2));
         var3.texts[var1] = var2;
         var3.floatValues[var1] = this.width;
         var3.fonts[var1] = this.font;
         var3.itemss[var1] = var4;
         return var4;
      }
   }

   private List<String> method990(String var1) {
      ArrayList<String> var2 = new ArrayList<>();
      StringBuilder var3 = new StringBuilder();

      for (int var4 = 0; var4 < var1.length(); var4++) {
         char var5 = var1.charAt(var4);
         if (var5 == '\n') {
            var2.add(var3.toString());
            var3.setLength(0);
         } else {
            if (!var3.isEmpty() && this.font.width(var3.toString() + var5) > this.width) {
               var2.add(var3.toString());
               var3.setLength(0);
            }

            var3.append(var5);
         }
      }

      var2.add(var3.toString());
      return var2;
   }

   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      Vector2f var6 = this.getVector2f();
      float var7 = this.multiline ? Math.max(this.font.height(), this.height) : this.font.height();
      this.selected = var5.getIntValue() == 0 && MathUtils.method2647(var1, var3, var6.x(), var6.y() - 3.0F, this.width, var7 + 6.0F);
      if (this.selected) {
         this.flag = false;
         if (!this.multiline) {
            this.method991(var1);
         }
      }

      return this.selected;
   }

   private void method991(double var1) {
      String var3 = this.masked ? "*".repeat(this.text.length()) : this.text;
      this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
      int var4 = 0;

      while (var4 < this.cursor && this.font.width(var3.substring(var4, this.cursor)) > this.width) {
         var4++;
      }

      float var5 = MathHelper.clamp((float)var1 - this.vector2f.x(), 0.0F, this.width);
      int var6 = var4;

      for (float var7 = 0.0F; var6 < var3.length(); var6++) {
         float var8 = this.font.width(var3.substring(var4, var6 + 1));
         if (var8 > this.width || var5 < (var7 + var8) / 2.0F) {
            break;
         }

         var7 = var8;
      }

      this.cursor = var6;
      this.longValue = System.currentTimeMillis();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.selected) {
         return false;
      } else if (var1 == 256) {
         this.selected = false;
         return true;
      } else if (var1 == 257) {
         if (this.multiline) {
            this.method992("\n", this.cursor);
            this.cursor++;
            this.longValue = System.currentTimeMillis();
         } else {
            this.selected = false;
         }

         return true;
      } else {
         this.longValue = System.currentTimeMillis();
         this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
         if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
            if (var1 == 86) {
               String var4 = mc.keyboard.getClipboard();
               if (this.flag) {
                  this.text = "";
                  this.cursor = 0;
                  this.flag = false;
               }

               this.cursor = this.cursor + this.method992(var4, this.cursor);
               this.flag = false;
            } else if (var1 == 65) {
               this.flag = true;
               this.cursor = this.text.length();
            } else if (var1 == 67 && this.selected && this.flag) {
               mc.keyboard.setClipboard(this.text);
            }
         } else if (var1 == 261 && !this.text.isEmpty()) {
            this.method993(this.cursor + 1);
            this.flag = false;
         } else if (var1 == 259 && !this.text.isEmpty()) {
            if (this.flag) {
               this.text = "";
               this.cursor = 0;
               this.flag = false;
            } else {
               this.method993(this.cursor);
               this.cursor--;
               if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
                  while (!this.text.isEmpty() && this.cursor > 0) {
                     this.method993(this.cursor);
                     this.cursor--;
                  }
               }
            }
         } else if (var1 == 262) {
            this.cursor++;
            if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
               this.cursor = this.text.length();
            }

            this.flag = false;
         } else if (var1 == 263) {
            this.cursor--;
            if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
               this.cursor = 0;
            }

            this.flag = false;
         } else if (var1 == 269) {
            this.cursor = this.text.length();
            this.flag = false;
         } else if (var1 == 268) {
            this.cursor = 0;
            this.flag = false;
         }

         this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
         return true;
      }
   }

   public boolean charTyped(char var1, int var2) {
      if (!this.selected) {
         return false;
      } else {
         this.longValue = System.currentTimeMillis();
         this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
         if (this.flag) {
            this.text = "";
            this.cursor = 0;
            this.flag = false;
         }

         this.cursor = this.cursor + this.method992(Character.toString(var1), this.cursor);
         this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
         return true;
      }
   }

   private int method992(String var1, int var2) {
      String var3 = this.method995(var1);
      var3 = this.method996(var3);
      StringBuilder var4 = new StringBuilder();

      for (char var8 : var3.toCharArray()) {
         if (this.type.method999(var8)) {
            var4.append(var8);
         }
      }

      String var10 = var4.toString();
      if (this.text.length() + var10.length() > this.maxLength) {
         int var11 = this.maxLength - this.text.length();
         if (var11 <= 0) {
            return 0;
         }

         var10 = var10.substring(0, Math.min(var11, var10.length()));
      }

      StringBuilder var12 = new StringBuilder();
      boolean var13 = false;

      for (int var14 = 0; var14 < this.text.length(); var14++) {
         if (var14 == var2) {
            var13 = true;
            var12.append(var10);
         }

         var12.append(this.text.charAt(var14));
      }

      if (!var13) {
         var12.append(var10);
      }

      this.text = var12.toString();
      return var10.length();
   }

   private void method993(int var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < this.text.length(); var3++) {
         if (var3 != var1 - 1) {
            var2.append(this.text.charAt(var3));
         }
      }

      this.text = var2.toString();
   }

   public boolean isEmpty() {
      return this.text.isEmpty();
   }

   private String method994(String var1) {
      if (var1 != null && !var1.isEmpty() && !(this.width <= 0.0F) && !(this.font.width(var1) <= this.width)) {
         int var2 = var1.length();

         while (var2 > 0 && this.font.width(var1.substring(0, var2)) > this.width) {
            var2--;
         }

         return var1.substring(0, var2);
      } else {
         return var1 == null ? "" : var1;
      }
   }

   private String method995(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         return switch (this.type) {
            case ENGLISH, ENGLISH_NUMBERS, ENGLISH_NAME, SERVER_ADDRESS -> method997(var1, items);
            default -> var1;
         };
      } else {
         return var1;
      }
   }

   private String method996(String var1) {
      if (var1 != null && !var1.isEmpty() && this.type2 != SearchBox.Type2.NO_LIMIT) {
         StringBuilder var2 = new StringBuilder(var1.length());

         for (int var3 = 0; var3 < var1.length(); var3++) {
            char var4 = var1.charAt(var3);
            if (!Character.isWhitespace(var4)) {
               var2.append(var4);
            }
         }

         return var2.toString();
      } else {
         return var1;
      }
   }

   private static String method997(String var0, Map<Character, Character> var1) {
      StringBuilder var2 = new StringBuilder(var0.length());

      for (int var3 = 0; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         char var5 = Character.toLowerCase(var4);
         Character var6 = (Character)var1.get(var5);
         if (var6 == null) {
            var2.append(var4);
         } else {
            char var7 = Character.isUpperCase(var4) ? Character.toUpperCase(var6) : var6;
            var2.append(var7);
         }
      }

      return var2.toString();
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public boolean isSelected() {
      return this.selected;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public int getCursor() {
      return this.cursor;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public Font getFont() {
      return this.font;
   }

   @Generated
   public Vector2f getVector2f() {
      return this.vector2f;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public boolean isMultiline() {
      return this.multiline;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public int getMaxLength() {
      return this.maxLength;
   }

   @Generated
   public SearchBox.Type getType() {
      return this.type;
   }

   @Generated
   public SearchBox.Type2 getType2() {
      return this.type2;
   }

   @Generated
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Generated
   public boolean isMasked() {
      return this.masked;
   }

   @Generated
   public Animation getAnimation() {
      return this.animation;
   }

   @Generated
   public void setText(String var1) {
      this.text = var1;
   }

   @Generated
   public void setSelected(boolean var1) {
      this.selected = var1;
   }

   @Generated
   public void setFlag(boolean var1) {
      this.flag = var1;
   }

   @Generated
   public void setCursor(int var1) {
      this.cursor = var1;
   }

   @Generated
   public void setFloatValue(float var1) {
      this.floatValue = var1;
   }

   @Generated
   public void setFont(Font var1) {
      this.font = var1;
   }

   @Generated
   public void setVector2f(Vector2f var1) {
      this.vector2f = var1;
   }

   @Generated
   public void setText2(String var1) {
      this.text2 = var1;
   }

   @Generated
   public void setWidth(float var1) {
      this.width = var1;
   }

   @Generated
   public void setHeight(float var1) {
      this.height = var1;
   }

   @Generated
   public void setMultiline(boolean var1) {
      this.multiline = var1;
   }

   @Generated
   public void setLongValue(long var1) {
      this.longValue = var1;
   }

   @Generated
   public void setMaxLength(int var1) {
      this.maxLength = var1;
   }

   @Generated
   public void setType(SearchBox.Type var1) {
      this.type = var1;
   }

   @Generated
   public void setType2(SearchBox.Type2 var1) {
      this.type2 = var1;
   }

   @Generated
   public void setScrollOffset(float var1) {
      this.scrollOffset = var1;
   }

   @Generated
   public void setMasked(boolean var1) {
      this.masked = var1;
   }

   @Generated
   public void setAnimation(Animation var1) {
      this.animation = var1;
   }

   @Generated
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SearchBox var2)) {
         return false;
      } else if (!var2.method998(this)) {
         return false;
      } else if (this.isSelected() != var2.isSelected()) {
         return false;
      } else if (this.isFlag() != var2.isFlag()) {
         return false;
      } else if (this.getCursor() != var2.getCursor()) {
         return false;
      } else if (Float.compare(this.getFloatValue(), var2.getFloatValue()) != 0) {
         return false;
      } else if (Float.compare(this.getWidth(), var2.getWidth()) != 0) {
         return false;
      } else if (Float.compare(this.getHeight(), var2.getHeight()) != 0) {
         return false;
      } else if (this.isMultiline() != var2.isMultiline()) {
         return false;
      } else if (this.getLongValue() != var2.getLongValue()) {
         return false;
      } else if (this.getMaxLength() != var2.getMaxLength()) {
         return false;
      } else if (Float.compare(this.getScrollOffset(), var2.getScrollOffset()) != 0) {
         return false;
      } else if (this.isMasked() != var2.isMasked()) {
         return false;
      } else {
         String var3 = this.getText();
         String var4 = var2.getText();
         if (var3 == null ? var4 == null : var3.equals(var4)) {
            Font var5 = this.getFont();
            Font var6 = var2.getFont();
            if (var5 == null ? var6 == null : var5.equals(var6)) {
               Vector2f var7 = this.getVector2f();
               Vector2f var8 = var2.getVector2f();
               if (var7 == null ? var8 == null : var7.equals(var8)) {
                  String var9 = this.getText2();
                  String var10 = var2.getText2();
                  if (var9 == null ? var10 == null : var9.equals(var10)) {
                     SearchBox.Type var11 = this.getType();
                     SearchBox.Type var12 = var2.getType();
                     if (var11 == null ? var12 == null : var11.equals(var12)) {
                        SearchBox.Type2 var13 = this.getType2();
                        SearchBox.Type2 var14 = var2.getType2();
                        if (var13 == null ? var14 == null : var13.equals(var14)) {
                           Animation var15 = this.getAnimation();
                           Animation var16 = var2.getAnimation();
                           return var15 == null ? var16 == null : var15.equals(var16);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   public boolean method998(Object var1) {
      return var1 instanceof SearchBox;
   }

   @Generated
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isSelected() ? 79 : 97);
      var2 = var2 * 59 + (this.isFlag() ? 79 : 97);
      var2 = var2 * 59 + this.getCursor();
      var2 = var2 * 59 + Float.floatToIntBits(this.getFloatValue());
      var2 = var2 * 59 + Float.floatToIntBits(this.getWidth());
      var2 = var2 * 59 + Float.floatToIntBits(this.getHeight());
      var2 = var2 * 59 + (this.isMultiline() ? 79 : 97);
      long var3 = this.getLongValue();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      var2 = var2 * 59 + this.getMaxLength();
      var2 = var2 * 59 + Float.floatToIntBits(this.getScrollOffset());
      var2 = var2 * 59 + (this.isMasked() ? 79 : 97);
      String var5 = this.getText();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      Font var6 = this.getFont();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      Vector2f var7 = this.getVector2f();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      String var8 = this.getText2();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      SearchBox.Type var9 = this.getType();
      var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
      SearchBox.Type2 var10 = this.getType2();
      var2 = var2 * 59 + (var10 == null ? 43 : var10.hashCode());
      Animation var11 = this.getAnimation();
      return var2 * 59 + (var11 == null ? 43 : var11.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "TextBox(text="
         + this.getText()
         + ", selected="
         + this.isSelected()
         + ", selectAll="
         + this.isFlag()
         + ", cursor="
         + this.getCursor()
         + ", posX="
         + this.getFloatValue()
         + ", font="
         + this.getFont()
         + ", position="
         + this.getVector2f()
         + ", emptyText="
         + this.getText2()
         + ", width="
         + this.getWidth()
         + ", height="
         + this.getHeight()
         + ", multiline="
         + this.isMultiline()
         + ", lastInputTime="
         + this.getLongValue()
         + ", maxLength="
         + this.getMaxLength()
         + ", charFilter="
         + this.getType()
         + ", wordLimit="
         + this.getType2()
         + ", scrollOffset="
         + this.getScrollOffset()
         + ", masked="
         + this.isMasked()
         + ", animation="
         + this.getAnimation()
         + ")";
   }

   private static final class Entry {
      private static final int CONST_intValue = 0;
      private static final int CONST_intValue2 = 1;
      private static final int CONST_intValue3 = 2;
      private String text;
      private boolean flag;
      private int intValue = -1;
      private float floatValue;
      private Font font;
      private int intValue2;
      private int intValue3;
      private final String[] texts = new String[3];
      private final float[] floatValues = new float[3];
      private final Font[] fonts = new Font[3];
      private final List<String>[] itemss = new List[3];
   }

   public static enum Type {
      ANY,
      ENGLISH,
      ENGLISH_NUMBERS,
      ENGLISH_NAME,
      SERVER_ADDRESS,
      CYRILLIC,
      NUMBERS_ONLY,
      DECIMAL_NUMBER;

      public boolean method999(char var1) {
         return switch (this) {
            case ANY -> true;
            case ENGLISH -> Character.isLetter(var1) && var1 <= 127 && Character.isAlphabetic(var1);
            case ENGLISH_NUMBERS -> Character.isLetterOrDigit(var1) && var1 <= 127;
            case ENGLISH_NAME -> Character.isLetterOrDigit(var1) && var1 <= 127 || var1 == '_';
            case SERVER_ADDRESS -> Character.isLetterOrDigit(var1) && var1 <= 127 || var1 == '.' || var1 == ':' || var1 == '-' || var1 == '_';
            case CYRILLIC -> String.valueOf(var1).matches("[А-Яа-яЁё]");
            case NUMBERS_ONLY -> Character.isDigit(var1);
            case DECIMAL_NUMBER -> Character.isDigit(var1) || var1 == '-' || var1 == '.' || var1 == ',';
         };
      }
   }

   public static enum Type2 {
      NO_LIMIT,
      ONLY_ONE_WORLD;
   }
}

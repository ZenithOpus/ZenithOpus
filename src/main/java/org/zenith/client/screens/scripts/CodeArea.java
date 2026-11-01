package org.zenith.client.screens.scripts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class CodeArea {
   private static final Set<String> KEYWORDS = Set.of(
      "var",
      "let",
      "const",
      "function",
      "return",
      "if",
      "else",
      "for",
      "while",
      "do",
      "break",
      "continue",
      "new",
      "typeof",
      "instanceof",
      "true",
      "false",
      "null",
      "undefined",
      "this",
      "try",
      "catch",
      "finally",
      "throw",
      "switch",
      "case",
      "default",
      "in",
      "of",
      "delete",
      "void",
      "yield"
   );
   private static final ZenithColor KEYWORD = new ZenithColor(199, 146, 234);
   private static final ZenithColor STRING = new ZenithColor(195, 232, 141);
   private static final ZenithColor NUMBER = new ZenithColor(247, 140, 108);
   private static final ZenithColor COMMENT = new ZenithColor(103, 110, 149);
   private static final ZenithColor API = new ZenithColor(130, 170, 255);
   private static final ZenithColor PLAIN = new ZenithColor(220, 224, 235);
   private static final ZenithColor GUTTER = new ZenithColor(96, 102, 124);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final Pattern WORD_BEFORE = Pattern.compile("(?:([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\.)?([A-Za-z_$][\\w$]*)?$");
   private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_$][\\w$]{2,}");
   private static final int MAX_UNDO = 200;
   private static final int MAX_CHARS = 1048576;
   private final List<StringBuilder> lines = new ArrayList<>();
   private final Deque<CodeArea.Snapshot> undo = new ArrayDeque<>();
   private final Deque<CodeArea.Snapshot> redo = new ArrayDeque<>();
   private final Map<Character, Float> widths = new HashMap<>();
   private final Supplier<Map<String, List<String>>> api;
   private Font font;
   private float fontSize = 6.0F;
   private int line;
   private int col;
   private int anchorLine = -1;
   private int anchorCol;
   private final Smooth scroller = new Smooth(18.0F);
   private float scroll;
   private float scrollX;
   private boolean dragging;
   private boolean focused = true;
   private boolean dirty;
   private long lastSnapshot;
   private String lastKind = "";
   private int errorLine = -1;
   private long blinkStart = System.currentTimeMillis();
   private final List<String> completions = new ArrayList<>();
   private int completionIndex;
   private String completionPrefix = "";
   private float x;
   private float y;
   private float width;
   private float height;
   private float gutter;
   private int editVersion;
   private int commentVersion = -1;
   private boolean[] commentAtStart = new boolean[0];

   public CodeArea(Supplier<Map<String, List<String>>> var1) {
      this.api = var1;
      this.setText("");
   }

   public void setText(String var1) {
      this.lines.clear();

      for (String var5 : var1.replace("\r\n", "\n").replace("\t", "    ").split("\n", -1)) {
         this.lines.add(new StringBuilder(var5));
      }

      if (this.lines.isEmpty()) {
         this.lines.add(new StringBuilder());
      }

      this.line = 0;
      this.col = 0;
      this.anchorLine = -1;
      this.undo.clear();
      this.redo.clear();
      this.editVersion++;
      this.dirty = false;
      this.scroller.reset(0.0F);
      this.scroll = 0.0F;
      this.scrollX = 0.0F;
      this.completions.clear();
   }

   public String getText() {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < this.lines.size(); var2++) {
         if (var2 > 0) {
            var1.append('\n');
         }

         var1.append(this.lines.get(var2));
      }

      return var1.toString();
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void markSaved() {
      this.dirty = false;
   }

   public void setErrorLine(int var1) {
      this.errorLine = var1;
   }

   public void setFocused(boolean var1) {
      this.focused = var1;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public int caretLine() {
      return this.line + 1;
   }

   public int caretColumn() {
      return this.col + 1;
   }

   public int lineCount() {
      return this.lines.size();
   }

   public void goToLine(int var1) {
      this.line = MathHelper.clamp(var1 - 1, 0, this.lines.size() - 1);
      this.col = 0;
      this.anchorLine = -1;
      this.ensureCaretVisible();
   }

   public void zoom(float var1) {
      this.fontSize = MathHelper.clamp(this.fontSize + var1, 4.5F, 10.0F);
      this.widths.clear();
   }

   private Font font() {
      if (this.font == null || this.font.getSize() != this.fontSize) {
         this.font = Fonts.NEW_MEDIUM.getFont(this.fontSize);
         this.widths.clear();
      }

      return this.font;
   }

   private float lineHeight() {
      return this.fontSize * 1.55F;
   }

   private float charWidth(char var1) {
      Float var2 = this.widths.get(var1);
      if (var2 == null) {
         var2 = this.font().width(String.valueOf(var1));
         this.widths.put(var1, var2);
      }

      return var2;
   }

   private float textWidth(CharSequence var1, int var2, int var3) {
      float var4 = 0.0F;

      for (int var5 = var2; var5 < var3 && var5 < var1.length(); var5++) {
         var4 += this.charWidth(var1.charAt(var5));
      }

      return var4;
   }

   private int columnAt(StringBuilder var1, float var2) {
      float var3 = 0.0F;

      for (int var4 = 0; var4 < var1.length(); var4++) {
         float var5 = this.charWidth(var1.charAt(var4));
         if (var3 + var5 / 2.0F >= var2) {
            return var4;
         }

         var3 += var5;
      }

      return var1.length();
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, ZenithColor var9) {
      this.x = var2;
      this.y = var3;
      this.width = var4;
      this.height = var5;
      Font var10 = this.font();
      float var11 = this.lineHeight();
      this.gutter = var10.width(String.valueOf(this.lines.size())) + 12.0F;
      float var12 = this.lines.size() * var11 + var5 * 0.5F;
      this.scroller.clamp(Math.min(0.0F, var5 - var12), 0.0F);
      this.scroll = this.scroller.update();
      var1.drawRoundedRect(var2, var3, var4, var5, ProxyDotRadiusData.method8069(4.0F), var9.method7294(var8));
      var1.drawRect(var2 + this.gutter - 3.0F, var3 + 3.0F, 1.0F, var5 - 6.0F, GUTTER.method7294(var8 * 0.35F));
      if (this.dragging) {
         this.placeCaret(var6, var7, true);
      }

      MsdfRenderer.flushBatch();
      var1.enableScissor(var2, var3, var2 + var4, var3 + var5);
      int var13 = Math.max(0, (int)(-this.scroll / var11) - 1);
      int var14 = Math.min(this.lines.size() - 1, var13 + (int)(var5 / var11) + 2);
      boolean var15 = this.blockCommentBefore(var13);
      float var16 = var2 + this.gutter + 2.0F - this.scrollX;

      for (int var17 = var13; var17 <= var14; var17++) {
         float var18 = var3 + 4.0F + this.scroll + var17 * var11;
         float var19 = var18 + (var11 - var10.height()) / 2.0F;
         if (var17 == this.line && this.focused) {
            var1.drawRect(var2 + this.gutter - 2.0F, var18, var4 - this.gutter + 2.0F, var11, PLAIN.method7294(var8 * 0.045F));
         }

         if (var17 == this.errorLine - 1) {
            var1.drawRect(var2 + 1.0F, var18, var4 - 2.0F, var11, ERROR.method7294(var8 * 0.14F));
            var1.drawRect(var2 + 2.0F, var18 + 1.0F, 2.0F, var11 - 2.0F, ERROR.method7294(var8));
         }

         this.renderSelection(var1, var17, var16, var18, var11, var8);
         String var20 = String.valueOf(var17 + 1);
         var1.drawText(
            var10,
            var20,
            var2 + this.gutter - 6.0F - var10.width(var20),
            var19,
            (var17 == this.line ? PLAIN : GUTTER).method7294(var8 * (var17 == this.line ? 0.8F : 1.0F))
         );
         var15 = this.renderLine(var1, this.lines.get(var17), var16, var19, var8, var15);
      }

      if (this.focused && (System.currentTimeMillis() - this.blinkStart) / 530L % 2L == 0L) {
         float var21 = var16 + this.textWidth(this.lines.get(this.line), 0, this.col);
         float var22 = var3 + 4.0F + this.scroll + this.line * var11;
         var1.drawRect(var21, var22 + 1.0F, 0.8F, var11 - 2.0F, PLAIN.method7294(var8));
      }

      MsdfRenderer.flushBatch();
      var1.disableScissor();
      this.renderCompletions(var1, var16, var8);
   }

   private void renderSelection(MenuDrawContext var1, int var2, float var3, float var4, float var5, float var6) {
      if (this.hasSelection()) {
         int[] var7 = this.selection();
         if (var2 >= var7[0] && var2 <= var7[2]) {
            StringBuilder var8 = this.lines.get(var2);
            int var9 = var2 == var7[0] ? var7[1] : 0;
            int var10 = var2 == var7[2] ? var7[3] : var8.length();
            float var11 = var3 + this.textWidth(var8, 0, var9);
            float var12 = var3 + this.textWidth(var8, 0, var10) + (var2 != var7[2] ? this.charWidth(' ') : 0.0F);
            var1.drawRect(var11, var4, Math.max(1.0F, var12 - var11), var5, API.method7294(var6 * 0.28F));
         }
      }
   }

   private boolean blockCommentBefore(int var1) {
      if (this.commentVersion != this.editVersion || this.commentAtStart.length != this.lines.size() + 1) {
         boolean[] var2 = new boolean[this.lines.size() + 1];
         boolean var3 = false;

         for (int var4 = 0; var4 < this.lines.size(); var4++) {
            var2[var4] = var3;
            var3 = scanComment(this.lines.get(var4), var3);
         }

         var2[this.lines.size()] = var3;
         this.commentAtStart = var2;
         this.commentVersion = this.editVersion;
      }

      return this.commentAtStart[Math.max(0, Math.min(var1, this.lines.size()))];
   }

   private static boolean scanComment(CharSequence var0, boolean var1) {
      char var2 = 0;

      for (int var3 = 0; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         char var5 = var3 + 1 < var0.length() ? var0.charAt(var3 + 1) : 0;
         if (var1) {
            if (var4 == '*' && var5 == '/') {
               var1 = false;
               var3++;
            }
         } else if (var2 != 0) {
            if (var4 == '\\') {
               var3++;
            } else if (var4 == var2) {
               var2 = 0;
            }
         } else if (var4 != '"' && var4 != '\'' && var4 != '`') {
            if (var4 == '/' && var5 == '/') {
               return false;
            }

            if (var4 == '/' && var5 == '*') {
               var1 = true;
               var3++;
            }
         } else {
            var2 = var4;
         }
      }

      return var1;
   }

   private boolean renderLine(MenuDrawContext var1, StringBuilder var2, float var3, float var4, float var5, boolean var6) {
      Font var7 = this.font();
      float var8 = var3;
      int var9 = 0;
      int var10 = var2.length();
      float var11 = this.x + this.width;

      while (var9 < var10) {
         int var12 = var9;
         ZenithColor var13 = PLAIN;
         char var14 = var2.charAt(var9);
         if (var6) {
            int var15 = var2.indexOf("*/", var9);
            var9 = var15 < 0 ? var10 : var15 + 2;
            var6 = var15 < 0;
            var13 = COMMENT;
         } else if (var14 == '/' && var9 + 1 < var10 && var2.charAt(var9 + 1) == '/') {
            var9 = var10;
            var13 = COMMENT;
         } else if (var14 == '/' && var9 + 1 < var10 && var2.charAt(var9 + 1) == '*') {
            int var19 = var2.indexOf("*/", var9 + 2);
            var9 = var19 < 0 ? var10 : var19 + 2;
            var6 = var19 < 0;
            var13 = COMMENT;
         } else if (var14 == '"' || var14 == '\'' || var14 == '`') {
            var9++;

            for (; var9 < var10 && var2.charAt(var9) != var14; var9++) {
               if (var2.charAt(var9) == '\\') {
                  var9++;
               }
            }

            var9 = Math.min(var10, var9 + 1);
            var13 = STRING;
         } else if (!Character.isDigit(var14)) {
            if (!Character.isJavaIdentifierStart(var14)) {
               var9++;
            } else {
               while (var9 < var10 && Character.isJavaIdentifierPart(var2.charAt(var9))) {
                  var9++;
               }

               String var18 = var2.substring(var12, var9);
               if (KEYWORDS.contains(var18)) {
                  var13 = KEYWORD;
               } else if (this.api.get().containsKey(var18)) {
                  var13 = API;
               }
            }
         } else {
            while (var9 < var10 && (Character.isLetterOrDigit(var2.charAt(var9)) || var2.charAt(var9) == '.')) {
               var9++;
            }

            var13 = NUMBER;
         }

         String var20 = var2.substring(var12, var9);
         float var16 = this.textWidth(var20, 0, var20.length());
         if (var8 + var16 >= this.x && var8 <= var11) {
            var1.drawText(var7, var20, var8, var4, var13.method7294(var5));
         }

         var8 += var16;
         if (var8 > var11) {
            break;
         }
      }

      if (var9 < var10) {
         var6 = scanComment(var2.subSequence(var9, var10), var6);
      }

      return var6;
   }

   private void renderCompletions(MenuDrawContext var1, float var2, float var3) {
      if (!this.completions.isEmpty() && this.focused) {
         Font var4 = this.font();
         float var5 = this.lineHeight();
         int var6 = Math.min(8, this.completions.size());
         int var7 = MathHelper.clamp(this.completionIndex - var6 + 1, 0, Math.max(0, this.completions.size() - var6));
         float var8 = 0.0F;

         for (int var9 = var7; var9 < var7 + var6; var9++) {
            var8 = Math.max(var8, var4.width(this.completions.get(var9)));
         }

         var8 += 14.0F;
         float var15 = Math.min(
            var2 + this.textWidth(this.lines.get(this.line), 0, this.col - this.completionPrefix.length()), this.x + this.width - var8 - 4.0F
         );
         float var10 = this.y + 4.0F + this.scroll + (this.line + 1) * var5 + 2.0F;
         if (var10 + var6 * var5 > this.y + this.height) {
            var10 = this.y + 4.0F + this.scroll + this.line * var5 - var6 * var5 - 2.0F;
         }

         var1.drawRoundedRect(var15, var10, var8, var6 * var5 + 4.0F, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(24, 26, 34).method7294(var3 * 0.97F));

         for (int var11 = 0; var11 < var6; var11++) {
            int var12 = var7 + var11;
            float var13 = var10 + 2.0F + var11 * var5;
            if (var12 == this.completionIndex) {
               var1.drawRoundedRect(var15 + 2.0F, var13, var8 - 4.0F, var5, ProxyDotRadiusData.method8069(2.0F), API.method7294(var3 * 0.25F));
            }

            var1.drawText(
               var4,
               this.completions.get(var12),
               var15 + 7.0F,
               var13 + (var5 - var4.height()) / 2.0F,
               (var12 == this.completionIndex ? PLAIN : GUTTER.method7303(0.4F)).method7294(var3)
            );
         }
      }
   }

   public boolean contains(double var1, double var3) {
      return var1 >= this.x && var1 <= this.x + this.width && var3 >= this.y && var3 <= this.y + this.height;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (!this.contains(var1, var3)) {
         this.focused = false;
         this.completions.clear();
         return false;
      } else {
         this.focused = true;
         this.completions.clear();
         if (var5 == 0) {
            boolean var6 = shiftDown();
            if (!var6) {
               this.anchorLine = -1;
            } else if (this.anchorLine < 0) {
               this.anchorLine = this.line;
               this.anchorCol = this.col;
            }

            this.placeCaret(var1, var3, false);
            if (!var6) {
               this.anchorLine = this.line;
               this.anchorCol = this.col;
            }

            this.dragging = true;
         }

         return true;
      }
   }

   public void mouseReleased() {
      this.dragging = false;
      if (this.anchorLine == this.line && this.anchorCol == this.col) {
         this.anchorLine = -1;
      }
   }

   private void placeCaret(double var1, double var3, boolean var5) {
      float var6 = this.lineHeight();
      int var7 = (int)Math.floor((var3 - this.y - 4.0 - this.scroll) / var6);
      this.line = MathHelper.clamp(var7, 0, this.lines.size() - 1);
      this.col = this.columnAt(this.lines.get(this.line), (float)(var1 - (this.x + this.gutter + 2.0F - this.scrollX)));
      this.blinkStart = System.currentTimeMillis();
      if (var5 && var3 < this.y) {
         this.scroller.add(var6);
      }

      if (var5 && var3 > this.y + this.height) {
         this.scroller.add(-var6);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (!this.contains(var1, var3)) {
         return false;
      } else if (ctrlDown()) {
         this.zoom((float)Math.signum(var5) * 0.5F);
         return true;
      } else if (shiftDown()) {
         this.scrollX = Math.max(0.0F, this.scrollX - (float)var5 * 30.0F);
         return true;
      } else {
         this.scroller.add((float)var5 * this.lineHeight() * 3.0F);
         return true;
      }
   }

   private static boolean ctrlDown() {
      long var0 = MinecraftClient.getInstance().getWindow().getHandle();
      return GLFW.glfwGetKey(var0, 341) == 1 || GLFW.glfwGetKey(var0, 345) == 1;
   }

   private static boolean shiftDown() {
      long var0 = MinecraftClient.getInstance().getWindow().getHandle();
      return GLFW.glfwGetKey(var0, 340) == 1 || GLFW.glfwGetKey(var0, 344) == 1;
   }

   public boolean hasCompletions() {
      return !this.completions.isEmpty();
   }

   public void closeCompletions() {
      this.completions.clear();
   }

   public boolean keyPressed(int var1, int var2) {
      if (!this.focused) {
         return false;
      } else {
         boolean var3 = (var2 & 2) != 0;
         boolean var4 = (var2 & 1) != 0;
         this.blinkStart = System.currentTimeMillis();
         if (!this.completions.isEmpty()) {
            switch (var1) {
               case 256:
                  this.completions.clear();
                  return true;
               case 257:
               case 258:
               case 335:
                  this.acceptCompletion();
                  return true;
               case 264:
                  this.completionIndex = (this.completionIndex + 1) % this.completions.size();
                  return true;
               case 265:
                  this.completionIndex = (this.completionIndex - 1 + this.completions.size()) % this.completions.size();
                  return true;
            }
         }

         if (var3) {
            switch (var1) {
               case 32:
                  this.updateCompletions(true);
                  return true;
               case 45:
               case 333:
                  this.zoom(-0.5F);
                  return true;
               case 47:
                  this.toggleComment();
                  return true;
               case 61:
               case 334:
                  this.zoom(0.5F);
                  return true;
               case 65:
                  this.anchorLine = 0;
                  this.anchorCol = 0;
                  this.line = this.lines.size() - 1;
                  this.col = this.lines.get(this.line).length();
                  return true;
               case 67:
                  if (this.hasSelection()) {
                     MinecraftClient.getInstance().keyboard.setClipboard(this.selectedText());
                  } else {
                     MinecraftClient.getInstance().keyboard.setClipboard(this.lines.get(this.line) + "\n");
                  }

                  return true;
               case 68:
                  this.snapshot("dup", true);
                  this.lines.add(this.line + 1, new StringBuilder(this.lines.get(this.line)));
                  this.line++;
                  this.changed();
                  return true;
               case 86:
                  String var17 = MinecraftClient.getInstance().keyboard.getClipboard();
                  if (var17 != null && !var17.isEmpty()) {
                     this.snapshot("paste", true);
                     this.insert(var17.replace("\r\n", "\n").replace("\t", "    "));
                  }

                  return true;
               case 88:
                  this.snapshot("cut", true);
                  if (this.hasSelection()) {
                     MinecraftClient.getInstance().keyboard.setClipboard(this.selectedText());
                     this.deleteSelection();
                  } else {
                     MinecraftClient.getInstance().keyboard.setClipboard(this.lines.get(this.line) + "\n");
                     this.removeLine(this.line);
                  }

                  this.changed();
                  return true;
               case 89:
                  this.redo();
                  return true;
               case 90:
                  if (var4) {
                     this.redo();
                  } else {
                     this.undo();
                  }

                  return true;
               case 262:
                  this.moveTo(this.line, this.wordBoundary(1), var4);
                  return true;
               case 263:
                  this.moveTo(this.line, this.wordBoundary(-1), var4);
                  return true;
               case 268:
                  this.moveTo(0, 0, var4);
                  return true;
               case 269:
                  this.moveTo(this.lines.size() - 1, this.lines.get(this.lines.size() - 1).length(), var4);
                  return true;
               default:
                  return false;
            }
         } else {
            switch (var1) {
               case 257:
               case 335:
                  this.snapshot("enter", true);
                  if (this.hasSelection()) {
                     this.deleteSelection();
                  }

                  StringBuilder var16 = this.lines.get(this.line);
                  String var19 = " ".repeat(indentOf(var16));
                  boolean var20 = this.col > 0 && "{[(".indexOf(var16.charAt(this.col - 1)) >= 0;
                  String var8 = var16.substring(this.col);
                  var16.setLength(this.col);
                  if (!var20) {
                     this.lines.add(this.line + 1, new StringBuilder(var19 + var8));
                     this.line++;
                     this.col = var19.length();
                  } else {
                     boolean var9 = !var8.isEmpty() && "}])".indexOf(var8.charAt(0)) >= 0;
                     this.lines.add(this.line + 1, new StringBuilder(var19 + "    " + (var9 ? "" : var8)));
                     if (var9) {
                        this.lines.add(this.line + 2, new StringBuilder(var19 + var8));
                     }

                     this.line++;
                     this.col = var19.length() + 4;
                  }

                  this.changed();
                  break;
               case 258:
                  this.snapshot("indent", true);
                  if (this.hasSelection()) {
                     int[] var15 = this.selection();

                     for (int var18 = var15[0]; var18 <= var15[2]; var18++) {
                        StringBuilder var7 = this.lines.get(var18);
                        if (var4) {
                           var7.delete(0, Math.min(4, indentOf(var7)));
                        } else {
                           var7.insert(0, "    ");
                        }
                     }
                  } else if (var4) {
                     int var14 = Math.min(4, indentOf(this.lines.get(this.line)));
                     this.lines.get(this.line).delete(0, var14);
                     this.col = Math.max(0, this.col - var14);
                  } else {
                     this.insert("    ");
                  }

                  this.changed();
                  break;
               case 259:
                  this.snapshot("delete", false);
                  if (this.hasSelection()) {
                     this.deleteSelection();
                  } else if (this.col > 0) {
                     StringBuilder var12 = this.lines.get(this.line);
                     byte var6 = 1;
                     if (this.col >= 4 && var12.substring(this.col - 4, this.col).equals("    ") && this.col <= indentOf(var12)) {
                        var6 = 4;
                     }

                     var12.delete(this.col - var6, this.col);
                     this.col -= var6;
                  } else if (this.line > 0) {
                     int var13 = this.lines.get(this.line - 1).length();
                     this.lines.get(this.line - 1).append(this.lines.get(this.line));
                     this.lines.remove(this.line);
                     this.line--;
                     this.col = var13;
                  }

                  this.changed();
                  this.updateCompletions(false);
                  break;
               case 261:
                  this.snapshot("delete", false);
                  if (this.hasSelection()) {
                     this.deleteSelection();
                  } else if (this.col < this.lines.get(this.line).length()) {
                     this.lines.get(this.line).deleteCharAt(this.col);
                  } else if (this.line < this.lines.size() - 1) {
                     this.lines.get(this.line).append(this.lines.get(this.line + 1));
                     this.lines.remove(this.line + 1);
                  }

                  this.changed();
                  break;
               case 262:
                  if (this.hasSelection() && !var4) {
                     int[] var11 = this.selection();
                     this.moveTo(var11[2], var11[3], false);
                  } else if (this.col < this.lines.get(this.line).length()) {
                     this.moveTo(this.line, this.col + 1, var4);
                  } else if (this.line < this.lines.size() - 1) {
                     this.moveTo(this.line + 1, 0, var4);
                  }
                  break;
               case 263:
                  if (this.hasSelection() && !var4) {
                     int[] var10 = this.selection();
                     this.moveTo(var10[0], var10[1], false);
                  } else if (this.col > 0) {
                     this.moveTo(this.line, this.col - 1, var4);
                  } else if (this.line > 0) {
                     this.moveTo(this.line - 1, this.lines.get(this.line - 1).length(), var4);
                  }
                  break;
               case 264:
                  this.moveVertical(1, var4);
                  break;
               case 265:
                  this.moveVertical(-1, var4);
                  break;
               case 266:
                  this.moveVertical(-((int)(this.height / this.lineHeight())), var4);
                  break;
               case 267:
                  this.moveVertical((int)(this.height / this.lineHeight()), var4);
                  break;
               case 268:
                  int var5 = indentOf(this.lines.get(this.line));
                  this.moveTo(this.line, this.col == var5 ? 0 : var5, var4);
                  break;
               case 269:
                  this.moveTo(this.line, this.lines.get(this.line).length(), var4);
                  break;
               default:
                  return var1 != 256 && var1 < 290;
            }

            this.ensureCaretVisible();
            return true;
         }
      }
   }

   public boolean charTyped(char var1) {
      if (this.focused && var1 >= ' ' && var1 != 127) {
         this.snapshot("type", false);
         String var2 = "()[]{}\"\"''``";
         int var3 = var2.indexOf(var1);
         StringBuilder var4 = this.lines.get(this.line);
         if ((var1 == ')' || var1 == ']' || var1 == '}' || var1 == '"' || var1 == '\'' || var1 == '`')
            && !this.hasSelection()
            && this.col < var4.length()
            && var4.charAt(this.col) == var1) {
            this.col++;
         } else if (var3 < 0 || var3 % 2 != 0 || this.hasSelection() || this.col < var4.length() && " )]};,".indexOf(var4.charAt(this.col)) < 0) {
            this.insert(String.valueOf(var1));
         } else {
            this.insert("" + var1 + var2.charAt(var3 + 1));
            this.col--;
         }

         if (var1 == '}' && this.col > 0) {
            StringBuilder var5 = this.lines.get(this.line);
            if (var5.toString().trim().equals("}") && indentOf(var5) >= 4) {
               var5.delete(0, 4);
               this.col = Math.max(0, this.col - 4);
            }
         }

         this.changed();
         this.updateCompletions(var1 == '.');
         this.ensureCaretVisible();
         return true;
      } else {
         return false;
      }
   }

   private void insert(String var1) {
      if (this.hasSelection()) {
         this.deleteSelection();
      }

      if (this.charCount() + var1.length() <= 1048576) {
         String[] var2 = var1.split("\n", -1);
         StringBuilder var3 = this.lines.get(this.line);
         String var4 = var3.substring(this.col);
         var3.setLength(this.col);
         var3.append(var2[0]);
         int var5 = this.line;

         for (int var6 = 1; var6 < var2.length; var6++) {
            this.lines.add(++var5, new StringBuilder(var2[var6]));
         }

         StringBuilder var7 = this.lines.get(var5);
         this.col = var7.length();
         var7.append(var4);
         this.line = var5;
         this.changed();
      }
   }

   private int charCount() {
      int var1 = 0;

      for (StringBuilder var3 : this.lines) {
         var1 += var3.length() + 1;
      }

      return var1;
   }

   private void removeLine(int var1) {
      this.editVersion++;
      if (this.lines.size() == 1) {
         this.lines.get(0).setLength(0);
         this.col = 0;
      } else {
         this.lines.remove(var1);
         this.line = Math.min(var1, this.lines.size() - 1);
         this.col = Math.min(this.col, this.lines.get(this.line).length());
      }
   }

   private void toggleComment() {
      this.snapshot("comment", true);
      int var1 = this.line;
      int var2 = this.line;
      if (this.hasSelection()) {
         int[] var3 = this.selection();
         var1 = var3[0];
         var2 = var3[2];
      }

      boolean var8 = true;

      for (int var4 = var1; var4 <= var2; var4++) {
         if (!this.lines.get(var4).toString().trim().startsWith("//") && !this.lines.get(var4).toString().isBlank()) {
            var8 = false;
         }
      }

      for (int var9 = var1; var9 <= var2; var9++) {
         StringBuilder var5 = this.lines.get(var9);
         int var6 = indentOf(var5);
         if (var8) {
            int var7 = var5.indexOf("//");
            if (var7 >= 0) {
               var5.delete(var7, Math.min(var5.length(), var7 + (var5.length() > var7 + 2 && var5.charAt(var7 + 2) == ' ' ? 3 : 2)));
            }
         } else if (!var5.toString().isBlank()) {
            var5.insert(var6, "// ");
         }
      }

      this.col = Math.min(this.col, this.lines.get(this.line).length());
      this.changed();
   }

   private int wordBoundary(int var1) {
      StringBuilder var2 = this.lines.get(this.line);
      int var3 = this.col;
      if (var1 < 0) {
         while (var3 > 0 && !Character.isJavaIdentifierPart(var2.charAt(var3 - 1))) {
            var3--;
         }

         while (var3 > 0 && Character.isJavaIdentifierPart(var2.charAt(var3 - 1))) {
            var3--;
         }

         return var3;
      } else {
         while (var3 < var2.length() && !Character.isJavaIdentifierPart(var2.charAt(var3))) {
            var3++;
         }

         while (var3 < var2.length() && Character.isJavaIdentifierPart(var2.charAt(var3))) {
            var3++;
         }

         return var3;
      }
   }

   private static int indentOf(CharSequence var0) {
      int var1 = 0;

      while (var1 < var0.length() && var0.charAt(var1) == ' ') {
         var1++;
      }

      return var1;
   }

   private void moveVertical(int var1, boolean var2) {
      float var3 = this.textWidth(this.lines.get(this.line), 0, this.col);
      int var4 = MathHelper.clamp(this.line + var1, 0, this.lines.size() - 1);
      this.moveTo(var4, this.columnAt(this.lines.get(var4), var3), var2);
   }

   private void moveTo(int var1, int var2, boolean var3) {
      if (var3) {
         if (this.anchorLine < 0) {
            this.anchorLine = this.line;
            this.anchorCol = this.col;
         }
      } else {
         this.anchorLine = -1;
      }

      this.line = MathHelper.clamp(var1, 0, this.lines.size() - 1);
      this.col = MathHelper.clamp(var2, 0, this.lines.get(this.line).length());
      this.completions.clear();
   }

   private boolean hasSelection() {
      return this.anchorLine >= 0 && (this.anchorLine != this.line || this.anchorCol != this.col);
   }

   private int[] selection() {
      boolean var1 = this.anchorLine < this.line || this.anchorLine == this.line && this.anchorCol <= this.col;
      return var1 ? new int[]{this.anchorLine, this.anchorCol, this.line, this.col} : new int[]{this.line, this.col, this.anchorLine, this.anchorCol};
   }

   private String selectedText() {
      int[] var1 = this.selection();
      if (var1[0] == var1[2]) {
         return this.lines.get(var1[0]).substring(var1[1], var1[3]);
      } else {
         StringBuilder var2 = new StringBuilder(this.lines.get(var1[0]).substring(var1[1]));

         for (int var3 = var1[0] + 1; var3 < var1[2]; var3++) {
            var2.append('\n').append(this.lines.get(var3));
         }

         var2.append('\n').append(this.lines.get(var1[2]), 0, var1[3]);
         return var2.toString();
      }
   }

   private void deleteSelection() {
      int[] var1 = this.selection();
      StringBuilder var2 = this.lines.get(var1[0]);
      String var3 = this.lines.get(var1[2]).substring(var1[3]);
      var2.setLength(var1[1]);
      var2.append(var3);

      for (int var4 = var1[2]; var4 > var1[0]; var4--) {
         this.lines.remove(var4);
      }

      this.line = var1[0];
      this.col = var1[1];
      this.anchorLine = -1;
      this.editVersion++;
   }

   private void snapshot(String var1, boolean var2) {
      long var3 = System.currentTimeMillis();
      if (var2 || !var1.equals(this.lastKind) || var3 - this.lastSnapshot >= 800L) {
         this.lastKind = var1;
         this.lastSnapshot = var3;
         this.undo.push(new CodeArea.Snapshot(this.getText(), this.line, this.col));

         while (this.undo.size() > 200) {
            this.undo.removeLast();
         }

         this.redo.clear();
      }
   }

   private void undo() {
      if (!this.undo.isEmpty()) {
         this.redo.push(new CodeArea.Snapshot(this.getText(), this.line, this.col));
         this.restore(this.undo.pop());
      }
   }

   private void redo() {
      if (!this.redo.isEmpty()) {
         this.undo.push(new CodeArea.Snapshot(this.getText(), this.line, this.col));
         this.restore(this.redo.pop());
      }
   }

   private void restore(CodeArea.Snapshot var1) {
      this.lines.clear();

      for (String var5 : var1.text().split("\n", -1)) {
         this.lines.add(new StringBuilder(var5));
      }

      this.line = MathHelper.clamp(var1.line(), 0, this.lines.size() - 1);
      this.col = MathHelper.clamp(var1.col(), 0, this.lines.get(this.line).length());
      this.anchorLine = -1;
      this.lastKind = "";
      this.editVersion++;
      this.dirty = true;
      this.ensureCaretVisible();
   }

   private void changed() {
      this.editVersion++;
      this.dirty = true;
      this.errorLine = -1;
   }

   private void ensureCaretVisible() {
      float var1 = this.lineHeight();
      float var2 = this.line * var1 + this.scroller.target();
      if (var2 < 0.0F) {
         this.scroller.target(-this.line * var1);
      } else if (var2 + var1 * 2.0F > this.height) {
         this.scroller.target(this.height - (this.line + 2) * var1);
      }

      float var3 = this.textWidth(this.lines.get(this.line), 0, this.col);
      float var4 = this.width - this.gutter - 12.0F;
      if (var3 - this.scrollX > var4) {
         this.scrollX = var3 - var4;
      }

      if (var3 < this.scrollX) {
         this.scrollX = Math.max(0.0F, var3 - 20.0F);
      }
   }

   private void updateCompletions(boolean var1) {
      this.completions.clear();
      this.completionIndex = 0;
      String var2 = this.lines.get(this.line).substring(0, this.col);
      Matcher var3 = WORD_BEFORE.matcher(var2);
      if (var3.find()) {
         String var4 = var3.group(1);
         String var5 = var3.group(2) == null ? "" : var3.group(2);
         if (var1 || !var5.isEmpty() || var4 != null) {
            if (var1 || var4 != null || var5.length() >= 2) {
               this.completionPrefix = var5;
               var var7 = this.api.get();
               Object var6;
               if (var4 != null) {
                  List var8 = (List)var7.get(var4);
                  if (var8 == null) {
                     return;
                  }

                  var6 = var8;
               } else {
                  LinkedHashSet var11 = new LinkedHashSet();

                  for (Object var10_o : (Iterable<?>)(Object)(var7.keySet())) {
                     String var10 = (String)var10_o;
                     if (!var10.contains(".")) {
                        var11.add(var10);
                     }
                  }

                  var11.addAll(KEYWORDS);
                  Matcher var13 = IDENTIFIER.matcher(this.getText());
                  int var15 = 0;

                  while (var13.find() && var15++ < 5000) {
                     var11.add(var13.group());
                  }

                  var6 = var11;
               }

               String var12 = var5.toLowerCase(Locale.ROOT);

               for (Object var16_o : (Iterable<?>)(Object)(var6)) {
                  String var16 = (String)var16_o;
                  if (!var16.equals(var5)) {
                     if (var16.toLowerCase(Locale.ROOT).startsWith(var12)) {
                        this.completions.add(var16);
                     }

                     if (this.completions.size() >= 60) {
                        break;
                     }
                  }
               }

               this.completions.sort(String.CASE_INSENSITIVE_ORDER);
            }
         }
      }
   }

   private void acceptCompletion() {
      if (!this.completions.isEmpty()) {
         String var1 = this.completions.get(this.completionIndex);
         this.snapshot("complete", true);
         StringBuilder var2 = this.lines.get(this.line);
         var2.replace(this.col - this.completionPrefix.length(), this.col, var1);
         this.col = this.col - this.completionPrefix.length() + var1.length();
         this.completions.clear();
         this.changed();
      }
   }

   private static final class Snapshot {
      private final String text;
      private final int line;
      private final int col;

      public String text() {
         return this.text;
      }

      public int line() {
         return this.line;
      }

      public int col() {
         return this.col;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Snapshot other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.line, other.line) && java.util.Objects.equals(this.col, other.col);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, line, col);
      }

      @Override
      public String toString() {
         return "Snapshot[" + "text=" + this.text + ", " + "line=" + this.line + ", " + "col=" + this.col + "]";
      }

      private Snapshot(String var1, int var2, int var3) {
         this.text = var1;
         this.line = var2;
         this.col = var3;
      }
   }
}

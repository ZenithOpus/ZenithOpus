package org.zenith.client.screens.bot;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.zenith.MinecraftAccess;
import org.zenith.base.bot.net.BotCommandSource;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;

public final class BotChatInputSuggestor implements MinecraftAccess {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private static final Style ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);
   private static final Style INFO_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
   private static final List<Style> HIGHLIGHT_STYLES = Stream.of(Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD)
      .map(Style.EMPTY::withColor)
      .toList();
   private static final int IN_WINDOW_INDEX_OFFSET = 1;
   private static final int MAX_SUGGESTION_SIZE = 10;
   private static final int COLOR = -805306368;
   private final Screen owner;
   private final SearchBox textField;
   private final TextRenderer textRenderer;
   private final Supplier<BotPlayHandler> handlerSupplier;
   private final IntUnaryOperator characterX;
   private final IntSupplier fieldTop;
   private final List<OrderedText> messages = new ArrayList<>();
   private int x;
   private int width;
   @Nullable
   private ParseResults<CommandSource> parse;
   @Nullable
   private CompletableFuture<Suggestions> pendingSuggestions;
   @Nullable
   private BotChatInputSuggestor.SuggestionWindow window;
   private boolean windowActive;
   private boolean completingSuggestions;
   @Nullable
   private String suggestion;
   private String lastText = "";

   public BotChatInputSuggestor(Screen var1, SearchBox var2, TextRenderer var3, Supplier<BotPlayHandler> var4, IntUnaryOperator var5, IntSupplier var6) {
      this.owner = var1;
      this.textField = var2;
      this.textRenderer = var3;
      this.handlerSupplier = var4;
      this.characterX = var5;
      this.fieldTop = var6;
   }

   public void setWindowActive(boolean var1) {
      this.windowActive = var1;
      if (!var1) {
         this.window = null;
      }
   }

   public void update(String var1) {
      String var2 = this.textField.getText();
      if (!var2.equals(this.lastText)) {
         this.lastText = var2;
         this.setWindowActive(!var2.equals(var1));
         this.refresh();
      }
   }

   public void reset(String var1) {
      this.lastText = var1;
      this.parse = null;
      this.pendingSuggestions = null;
      this.suggestion = null;
      this.messages.clear();
      this.setWindowActive(false);
      this.refresh();
   }

   @Nullable
   public String getSuggestion() {
      return this.suggestion;
   }

   public boolean isOpen() {
      return this.window != null;
   }

   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      boolean var4 = this.window != null;
      if (var4 && this.window.keyPressed(var1)) {
         return true;
      } else if (var1 == 258) {
         this.show();
         return this.window != null;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.window != null && this.window.mouseScrolled(MathHelper.clamp(var1, -1.0, 1.0), var3, var5);
   }

   public boolean mouseClicked(double var1, double var3) {
      return this.window != null && this.window.mouseClicked((int)var1, (int)var3);
   }

   private void show() {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         Suggestions var1 = this.pendingSuggestions.join();
         if (!var1.isEmpty()) {
            int var2 = 0;

            for (Suggestion var4 : var1.getList()) {
               var2 = Math.max(var2, this.textRenderer.getWidth(var4.getText()));
            }

            int var5 = MathHelper.clamp(this.characterX.applyAsInt(var1.getRange().getStart()), 0, Math.max(0, this.owner.width - var2));
            this.window = new BotChatInputSuggestor.SuggestionWindow(var5, var2, this.sortSuggestions(var1));
         }
      }
   }

   public void clearWindow() {
      this.window = null;
   }

   private List<Suggestion> sortSuggestions(Suggestions var1) {
      String var2 = this.textField.getText().substring(0, this.textField.getCursor());
      int var3 = getStartOfCurrentWord(var2);
      String var4 = var2.substring(var3).toLowerCase(Locale.ROOT);
      ArrayList<Suggestion> var5 = new ArrayList<>();
      ArrayList var6 = new ArrayList();

      for (Suggestion var8 : var1.getList()) {
         if (!var8.getText().startsWith(var4) && !var8.getText().startsWith("minecraft:" + var4)) {
            var6.add(var8);
         } else {
            var5.add(var8);
         }
      }

      var5.addAll(var6);
      return var5;
   }

   public void refresh() {
      BotPlayHandler var1 = this.handlerSupplier.get();
      String var2 = this.textField.getText();
      if (this.parse != null && !this.parse.getReader().getString().equals(var2)) {
         this.parse = null;
      }

      if (!this.completingSuggestions) {
         this.suggestion = null;
         this.window = null;
      }

      this.messages.clear();
      StringReader var3 = new StringReader(var2);
      boolean var4 = var3.canRead() && var3.peek() == '/';
      if (var4) {
         var3.skip();
      }

      int var5 = this.textField.getCursor();
      if (var4 && var1 != null) {
         CommandDispatcher var9 = var1.getCommandDispatcher();
         BotCommandSource var10 = var1.getCommandSource();
         if (this.parse == null) {
            this.parse = var9.parse(var3, var10);
         }

         if (var5 >= 1 && (this.window == null || !this.completingSuggestions)) {
            this.pendingSuggestions = var9.getCompletionSuggestions(this.parse, var5);
            this.pendingSuggestions.thenRun(() -> {
               var var1x = this.pendingSuggestions;
               if (var1x != null && var1x.isDone()) {
                  mc.execute(() -> {
                     if (this.pendingSuggestions == var1x) {
                        this.showCommandSuggestions();
                     }
                  });
               }
            });
         }
      } else {
         String var6 = var2.substring(0, var5);
         int var7 = getStartOfCurrentWord(var6);
         Object var8 = var1 != null ? var1.getCommandSource().getChatSuggestions() : List.of();
         this.pendingSuggestions = CommandSource.suggestMatching((Iterable<String>)var8, new SuggestionsBuilder(var6, var7));
      }
   }

   private static int getStartOfCurrentWord(String var0) {
      if (Strings.isNullOrEmpty(var0)) {
         return 0;
      } else {
         int var1 = 0;
         Matcher var2 = WHITESPACE_PATTERN.matcher(var0);

         while (var2.find()) {
            var1 = var2.end();
         }

         return var1;
      }
   }

   private static OrderedText formatException(CommandSyntaxException var0) {
      Text var1 = Texts.toText(var0.getRawMessage());
      String var2 = var0.getContext();
      return var2 == null ? var1.asOrderedText() : Text.translatable("command.context.parse_error", var1, var0.getCursor(), var2).asOrderedText();
   }

   private void showCommandSuggestions() {
      if (this.pendingSuggestions != null && this.parse != null) {
         boolean var1 = false;
         if (this.textField.getCursor() == this.textField.getText().length()) {
            if (this.pendingSuggestions.join().isEmpty() && !this.parse.getExceptions().isEmpty()) {
               int var2 = 0;

               for (Entry var4 : this.parse.getExceptions().entrySet()) {
                  CommandSyntaxException var5 = (CommandSyntaxException)var4.getValue();
                  if (var5.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                     var2++;
                  } else {
                     this.messages.add(formatException(var5));
                  }
               }

               if (var2 > 0) {
                  this.messages.add(formatException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
               }
            } else if (this.parse.getReader().canRead()) {
               var1 = true;
            }
         }

         this.x = 0;
         this.width = this.owner.width;
         if (this.messages.isEmpty() && !this.showUsages() && var1) {
            this.messages.add(formatException(CommandManager.getException(this.parse)));
         }

         this.window = null;
         if (this.windowActive) {
            this.show();
         }
      }
   }

   private boolean showUsages() {
      BotPlayHandler var1 = this.handlerSupplier.get();
      if (var1 != null && this.parse != null) {
         CommandContextBuilder var2 = this.parse.getContext();
         SuggestionContext var3 = var2.findSuggestionContext(this.textField.getCursor());
         var var4 = var1.getCommandDispatcher().getSmartUsage(var3.parent, var1.getCommandSource());
         ArrayList var5 = new ArrayList();
         int var6 = 0;

         for (Object var8_o : (Iterable<?>)(Object)(var4.entrySet())) {
            Entry var8 = (Entry)var8_o;
            if (!(var8.getKey() instanceof LiteralCommandNode)) {
               var5.add(OrderedText.styledForwardsVisitedString((String)var8.getValue(), INFO_STYLE));
               var6 = Math.max(var6, this.textRenderer.getWidth((String)var8.getValue()));
            }
         }

         if (!var5.isEmpty()) {
            this.messages.addAll(var5);
            this.x = MathHelper.clamp(this.characterX.applyAsInt(var3.startPos), 0, Math.max(0, this.owner.width - var6));
            this.width = var6;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public OrderedText provideRenderText(String var1, int var2) {
      return this.parse != null ? highlight(this.parse, var1, var2) : OrderedText.styledForwardsVisitedString(var1, Style.EMPTY);
   }

   @Nullable
   private static String getSuggestionSuffix(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   private static OrderedText highlight(ParseResults<CommandSource> var0, String var1, int var2) {
      ArrayList var3 = new ArrayList();
      int var4 = 0;
      int var5 = -1;
      CommandContextBuilder var6 = var0.getContext().getLastChild();

      for (Object var8_o : (Iterable<?>)(Object)(var6.getArguments().values())) {
         ParsedArgument var8 = (ParsedArgument)var8_o;
         if (++var5 >= HIGHLIGHT_STYLES.size()) {
            var5 = 0;
         }

         int var9 = Math.max(var8.getRange().getStart() - var2, 0);
         if (var9 >= var1.length()) {
            break;
         }

         int var10 = Math.min(var8.getRange().getEnd() - var2, var1.length());
         if (var10 > 0) {
            var3.add(OrderedText.styledForwardsVisitedString(var1.substring(var4, var9), INFO_STYLE));
            var3.add(OrderedText.styledForwardsVisitedString(var1.substring(var9, var10), HIGHLIGHT_STYLES.get(var5)));
            var4 = var10;
         }
      }

      if (var0.getReader().canRead()) {
         int var11 = Math.max(var0.getReader().getCursor() - var2, 0);
         if (var11 < var1.length()) {
            int var12 = Math.min(var11 + var0.getReader().getRemainingLength(), var1.length());
            var3.add(OrderedText.styledForwardsVisitedString(var1.substring(var4, var11), INFO_STYLE));
            var3.add(OrderedText.styledForwardsVisitedString(var1.substring(var11, var12), ERROR_STYLE));
            var4 = var12;
         }
      }

      var3.add(OrderedText.styledForwardsVisitedString(var1.substring(var4), INFO_STYLE));
      return OrderedText.concat(var3);
   }

   public void render(DrawContext var1, int var2, int var3) {
      if (this.window != null) {
         this.window.render(var1, var2, var3);
      } else {
         this.renderMessages(var1);
      }
   }

   private void renderMessages(DrawContext var1) {
      int var2 = 0;

      for (OrderedText var4 : this.messages) {
         int var5 = this.fieldTop.getAsInt() - 13 - 12 * var2;
         var1.fill(this.x - 1, var5, this.x + this.width + 1, var5 + 12, -805306368);
         var1.drawTextWithShadow(this.textRenderer, var4, this.x, var5 + 2, -1);
         var2++;
      }
   }

   private final class SuggestionWindow {
      private final Rect2i area;
      private final String typedText;
      private final List<Suggestion> suggestions;
      private int inWindowIndex;
      private int selection;
      private double lastMouseX;
      private double lastMouseY;
      private boolean completed;

      public SuggestionWindow(int var2, int var3, List<Suggestion> var4) {
         int var5 = BotChatInputSuggestor.this.fieldTop.getAsInt() - 3 - Math.min(var4.size(), 10) * 12;
         this.area = new Rect2i(var2 - 1, var5, var3 + 1, Math.min(var4.size(), 10) * 12);
         this.typedText = BotChatInputSuggestor.this.textField.getText();
         this.suggestions = var4;
         this.select(0);
      }

      public void render(DrawContext var1, int var2, int var3) {
         int var4 = Math.min(this.suggestions.size(), 10);
         boolean var5 = this.inWindowIndex > 0;
         boolean var6 = this.suggestions.size() > this.inWindowIndex + var4;
         boolean var7 = var5 || var6;
         boolean var8 = this.lastMouseX != var2 || this.lastMouseY != var3;
         if (var8) {
            this.lastMouseX = var2;
            this.lastMouseY = var3;
         }

         if (var7) {
            var1.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), -805306368);
            var1.fill(
               this.area.getX(),
               this.area.getY() + this.area.getHeight(),
               this.area.getX() + this.area.getWidth(),
               this.area.getY() + this.area.getHeight() + 1,
               -805306368
            );
            if (var5) {
               for (int var9 = 0; var9 < this.area.getWidth(); var9++) {
                  if (var9 % 2 == 0) {
                     var1.fill(this.area.getX() + var9, this.area.getY() - 1, this.area.getX() + var9 + 1, this.area.getY(), -1);
                  }
               }
            }

            if (var6) {
               for (int var12 = 0; var12 < this.area.getWidth(); var12++) {
                  if (var12 % 2 == 0) {
                     var1.fill(
                        this.area.getX() + var12,
                        this.area.getY() + this.area.getHeight(),
                        this.area.getX() + var12 + 1,
                        this.area.getY() + this.area.getHeight() + 1,
                        -1
                     );
                  }
               }
            }
         }

         boolean var13 = false;

         for (int var10 = 0; var10 < var4; var10++) {
            Suggestion var11 = this.suggestions.get(var10 + this.inWindowIndex);
            var1.fill(this.area.getX(), this.area.getY() + 12 * var10, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * var10 + 12, -805306368);
            if (var2 > this.area.getX()
               && var2 < this.area.getX() + this.area.getWidth()
               && var3 > this.area.getY() + 12 * var10
               && var3 < this.area.getY() + 12 * var10 + 12) {
               if (var8) {
                  this.select(var10 + this.inWindowIndex);
               }

               var13 = true;
            }

            var1.drawTextWithShadow(
               BotChatInputSuggestor.this.textRenderer,
               var11.getText(),
               this.area.getX() + 1,
               this.area.getY() + 2 + 12 * var10,
               var10 + this.inWindowIndex == this.selection ? '\uff00' : -5592406
            );
         }

         if (var13) {
            Message var14 = this.suggestions.get(this.selection).getTooltip();
            if (var14 != null) {
               var1.drawTooltip(BotChatInputSuggestor.this.textRenderer, Texts.toText(var14), var2, var3);
            }
         }
      }

      public boolean mouseClicked(int var1, int var2) {
         if (!this.area.contains(var1, var2)) {
            return false;
         } else {
            int var3 = (var2 - this.area.getY()) / 12 + this.inWindowIndex;
            if (var3 >= 0 && var3 < this.suggestions.size()) {
               this.select(var3);
               this.complete();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double var1, double var3, double var5) {
         if (this.area.contains((int)var3, (int)var5)) {
            this.inWindowIndex = MathHelper.clamp((int)(this.inWindowIndex - var1), 0, Math.max(this.suggestions.size() - 10, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int var1) {
         if (var1 == 265) {
            this.scroll(-1);
            this.completed = false;
            return true;
         } else if (var1 == 264) {
            this.scroll(1);
            this.completed = false;
            return true;
         } else if (var1 == 258) {
            if (this.completed) {
               this.scroll(WindowUtils.method5476() ? -1 : 1);
            }

            this.complete();
            return true;
         } else if (var1 == 256) {
            BotChatInputSuggestor.this.clearWindow();
            BotChatInputSuggestor.this.suggestion = null;
            return true;
         } else {
            return false;
         }
      }

      public void scroll(int var1) {
         this.select(this.selection + var1);
         int var2 = this.inWindowIndex;
         int var3 = this.inWindowIndex + 10 - 1;
         if (this.selection < var2) {
            this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.size() - 10, 0));
         } else if (this.selection > var3) {
            this.inWindowIndex = MathHelper.clamp(this.selection + 1 - 10, 0, Math.max(this.suggestions.size() - 10, 0));
         }
      }

      public void select(int var1) {
         this.selection = var1;
         if (this.selection < 0) {
            this.selection = this.selection + this.suggestions.size();
         }

         if (this.selection >= this.suggestions.size()) {
            this.selection = this.selection - this.suggestions.size();
         }

         Suggestion var2 = this.suggestions.get(this.selection);
         BotChatInputSuggestor.this.suggestion = BotChatInputSuggestor.getSuggestionSuffix(
            BotChatInputSuggestor.this.textField.getText(), var2.apply(this.typedText)
         );
      }

      public void complete() {
         Suggestion var1 = this.suggestions.get(this.selection);
         BotChatInputSuggestor.this.completingSuggestions = true;
         String var2 = var1.apply(this.typedText);
         BotChatInputSuggestor.this.textField.setText(var2);
         BotChatInputSuggestor.this.lastText = var2;
         int var3 = var1.getRange().getStart() + var1.getText().length();
         BotChatInputSuggestor.this.textField.setCursor(var3);
         BotChatInputSuggestor.this.windowActive = true;
         BotChatInputSuggestor.this.refresh();
         this.select(this.selection);
         BotChatInputSuggestor.this.completingSuggestions = false;
         this.completed = true;
      }
   }
}

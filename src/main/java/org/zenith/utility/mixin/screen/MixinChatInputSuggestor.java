package org.zenith.utility.mixin.screen;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatInputSuggestor.SuggestionWindow;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.zenith.Zenith;

@Mixin({ChatInputSuggestor.class})
public abstract class MixinChatInputSuggestor {
   @Final
   @Shadow
   public TextFieldWidget textField;
   @Shadow
   public boolean completingSuggestions;
   @Shadow
   private ParseResults<CommandSource> parse;
   @Shadow
   private CompletableFuture<Suggestions> pendingSuggestions;
   @Shadow
   private SuggestionWindow window;

   @Shadow
   public abstract void showCommandSuggestions();

   @Inject(
      method = {"refresh()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
         remap = false
      )},
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   public void refreshHook(CallbackInfo var1, String var2, StringReader var3) {
      if (var3.canRead(Zenith.getInstance().getCommandManager().getPrefix().length())
         && var3.getString().startsWith(Zenith.getInstance().getCommandManager().getPrefix(), var3.getCursor())) {
         var3.setCursor(var3.getCursor() + 1);
         if (this.parse == null) {
            this.parse = Zenith.getInstance().getCommandManager().getDispatcher().parse(var3, Zenith.getInstance().getCommandManager().getSource());
         }

         int var4 = this.textField.getCursor();
         if (var4 >= 1 && (this.window == null || !this.completingSuggestions)) {
            this.pendingSuggestions = Zenith.getInstance().getCommandManager().getDispatcher().getCompletionSuggestions(this.parse, var4);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.showCommandSuggestions();
               }
            });
         }

         var1.cancel();
      }
   }
}

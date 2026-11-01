package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ShownProvider {
   public static final ShownProvider INSTANCE = new ShownProvider(
      "anthropic",
      "Claude",
      "Anthropic",
      ShownProvider.Type.ANTHROPIC,
      "https://api.anthropic.com/v1/messages",
      List.of("claude-opus-5-5", "claude-sonnet-5-5", "claude-haiku-4-5", "claude-fable-5-1"),
      true,
      "https://console.anthropic.com/settings/keys",
      false
   );
   public static final ShownProvider INSTANCE2 = new ShownProvider(
      "openai",
      "ChatGPT",
      "OpenAI",
      ShownProvider.Type.OPENAI,
      "https://api.openai.com/v1/chat/completions",
      List.of("gpt-5", "gpt-5-mini", "gpt-4.1", "o4-mini"),
      true,
      "https://platform.openai.com/api-keys",
      false
   );
   public static final ShownProvider INSTANCE3 = new ShownProvider(
      "gemini",
      "Gemini",
      "Google",
      ShownProvider.Type.GEMINI,
      "https://generativelanguage.googleapis.com/v1beta/models/",
      List.of("gemini-2.5-pro", "gemini-2.5-flash"),
      true,
      "https://aistudio.google.com/apikey",
      false
   );
   public static final ShownProvider INSTANCE4 = new ShownProvider(
      "deepseek",
      "DeepSeek",
      "DeepSeek",
      ShownProvider.Type.OPENAI,
      "https://api.deepseek.com/chat/completions",
      List.of("deepseek-chat", "deepseek-reasoner"),
      true,
      "https://platform.deepseek.com/api_keys",
      false
   );
   public static final ShownProvider INSTANCE5 = new ShownProvider(
      "xai",
      "Grok",
      "xAI",
      ShownProvider.Type.OPENAI,
      "https://api.x.ai/v1/chat/completions",
      List.of("grok-4", "grok-3-mini"),
      true,
      "https://console.x.ai",
      false
   );
   public static final ShownProvider INSTANCE6 = new ShownProvider(
      "openrouter",
      "OpenRouter",
      "OpenRouter",
      ShownProvider.Type.OPENAI,
      "https://openrouter.ai/api/v1/chat/completions",
      List.of("openrouter/auto", "anthropic/claude-opus-5-5", "openai/gpt-5", "google/gemini-2.5-pro"),
      true,
      "https://openrouter.ai/keys",
      false
   );
   public static final ShownProvider INSTANCE7 = new ShownProvider(
      "mistral",
      "Mistral",
      "Mistral AI",
      ShownProvider.Type.OPENAI,
      "https://api.mistral.ai/v1/chat/completions",
      List.of("mistral-large-latest", "codestral-latest"),
      true,
      "https://console.mistral.ai/api-keys",
      false
   );
   public static final ShownProvider INSTANCE8 = new ShownProvider(
      "groq",
      "Groq",
      "Groq",
      ShownProvider.Type.OPENAI,
      "https://api.groq.com/openai/v1/chat/completions",
      List.of("llama-3.3-70b-versatile", "qwen-qwq-32b"),
      true,
      "https://console.groq.com/keys",
      false
   );
   public static final ShownProvider INSTANCE9 = new ShownProvider(
      "ollama",
      "Ollama",
      "локально",
      ShownProvider.Type.OPENAI,
      "http://127.0.0.1:11434/v1/chat/completions",
      List.of("qwen2.5-coder:14b", "llama3.1:8b"),
      false,
      "https://ollama.com/download",
      false
   );
   private static final List<ShownProvider> items = List.of(INSTANCE, INSTANCE2, INSTANCE3, INSTANCE4, INSTANCE5, INSTANCE6, INSTANCE7, INSTANCE8, INSTANCE9);
   private static final List<ShownProvider> items2 = new CopyOnWriteArrayList<>();
   private final String text;
   private final String text2;
   private final String text3;
   private final ShownProvider.Type type;
   private final String text4;
   private final List<String> items3;
   private final boolean flag;
   private final String text5;
   private final boolean flag2;

   private ShownProvider(
      String var1, String var2, String var3, ShownProvider.Type var4, String var5, List<String> var6, boolean var7, String var8, boolean var9
   ) {
      this.text = var1;
      this.text2 = var2;
      this.text3 = var3;
      this.type = var4;
      this.text4 = var5;
      this.items3 = var6;
      this.flag = var7;
      this.text5 = var8;
      this.flag2 = var9;
   }

   public static ShownProvider method7332(String var0, String var1, String var2, String var3) {
      return new ShownProvider(
         var0, var1, "свой API", ShownProvider.Type.OPENAI, var2, List.of(var3 != null && !var3.isBlank() ? var3.strip() : "default"), false, var2, true
      );
   }

   public static void method7333(ShownProvider var0) {
      items2.removeIf(var1 -> var1.text.equals(var0.text));
      items2.add(var0);
   }

   public static void method7334(ShownProvider var0) {
      items2.removeIf(var1 -> var1.text.equals(var0.text));
   }

   public static List<ShownProvider> method7335() {
      return new ArrayList<>(items2);
   }

   public static ShownProvider[] method7336() {
      var var0 = new ArrayList<>(items);
      var0.addAll(items2);
      return (ShownProvider[])var0.toArray(new ShownProvider[0]);
   }

   public String getText() {
      return this.text;
   }

   public String title() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public ShownProvider.Type getType() {
      return this.type;
   }

   public String getText4() {
      return this.text4;
   }

   public List<String> getItems3() {
      return this.items3;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String getText5() {
      return this.text5;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public static ShownProvider method7337(String var0) {
      for (ShownProvider var4 : method7336()) {
         if (var4.text.equalsIgnoreCase(var0)) {
            return var4;
         }
      }

      return INSTANCE;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof ShownProvider var2 && var2.text.equals(this.text);
   }

   @Override
   public int hashCode() {
      return this.text.hashCode();
   }

   public static enum Type {
      ANTHROPIC,
      OPENAI,
      GEMINI;
   }
}

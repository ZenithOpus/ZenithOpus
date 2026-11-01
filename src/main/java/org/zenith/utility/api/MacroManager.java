package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.SuggestCommand;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.util.Formatting;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;

public class MacroManager extends JsonListStorage<MacroManager.Entry> implements MinecraftAccess {
   public MacroManager() {
      super("macros", "", (new TypeToken<ArrayList<MacroManager.Entry>>() {}).getType(), ArrayList::new);
      EventManager.register(this);
   }

   @Override
   public Gson createGson() {
      return new GsonBuilder().registerTypeAdapter(MacroManager.Entry.class, new MacroManager.Entry2()).setPrettyPrinting().create();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public void method6983(MacroManager.Entry var1) {
      this.items.add(var1);
   }

   public void method6984(String var1, int var2, String var3) {
      this.items.add(new MacroManager.Entry(var1, var2, var3));
   }

   public void method6985(String var1) {
      this.items.removeIf(var1x -> var1x.name().equalsIgnoreCase(var1));
   }

   public MacroManager.Entry method6986(String var1) {
      return this.items.stream().filter(var1x -> var1x.name().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public boolean method6987(String var1) {
      return this.method6986(var1) != null;
   }

   public void clear() {
      this.items.clear();
   }

   @EventTarget
   public void method6988(KeyEvent var1) {
      if (mc.player != null) {
         for (MacroManager.Entry var3 : this.items) {
            if (var1.isPress(var3.getIntValue())) {
               this.method6989(var3);
            }
         }
      }
   }

   private void method6989(MacroManager.Entry var1) {
      if (var1.getText2().startsWith("/")) {
         mc.player.networkHandler.sendChatCommand(var1.getText2().substring(1));
      } else {
         mc.player.networkHandler.sendChatMessage(var1.getText2());
      }
   }

   public static class Entry {
      @SerializedName("name")
      private final String text;
      @SerializedName("key")
      private final int intValue;
      @SerializedName("command")
      private final String text2;

      public Entry(String var1, int var2, String var3) {
         this.text = var1;
         this.intValue = var2;
         this.text2 = var3;
      }

      public String name() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText2() {
         return this.text2;
      }

      public Text toText() {
         String var1 = this.text.replace("\"", "\\\"");
         MutableText var2 = Text.literal(" " + this.text).formatted(Formatting.GOLD, Formatting.BOLD);
         MutableText var3 = Text.literal(" [  " + Type.KEYSYM.createFromCode(this.intValue).getLocalizedText().getString() + "  ]").formatted(Formatting.AQUA);
         MutableText var4 = Text.literal(" -> " + this.text2).formatted(Formatting.GRAY);
         MutableText var5 = Text.literal("  [Удалить]")
            .formatted(Formatting.RED, Formatting.BOLD)
            .styled(
               var2x -> var2x.withHoverEvent(new ShowText(Text.literal("Подставить команду удаления макроса " + this.text)))
                  .withClickEvent(new SuggestCommand(".macro remove " + var1))
            );
         return Text.empty().append(var2).append(var3).append(var4).append(var5);
      }
   }

   public static class Entry2 implements JsonDeserializer<MacroManager.Entry>, JsonSerializer<MacroManager.Entry> {
      public JsonElement serialize(MacroManager.Entry var1, java.lang.reflect.Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("name", var1.name());
         var4.addProperty("key", var1.getIntValue());
         var4.addProperty("command", var1.getText2());
         return var4;
      }

      public MacroManager.Entry deserialize(JsonElement var1, java.lang.reflect.Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         String var5 = getString(var4, "name", "");
         int var6 = getInt(var4, "key");
         String var7 = getString(var4, "command", "");
         return new MacroManager.Entry(var5, var6, var7);
      }

      private static String getString(JsonObject var0, String var1, String var2) {
         JsonElement var3 = var0.get(var1);
         return var3 != null && !var3.isJsonNull() ? var3.getAsString() : var2;
      }

      private static int getInt(JsonObject var0, String var1) {
         JsonElement var2 = var0.get(var1);
         if (var2 != null && !var2.isJsonNull()) {
            return var2.getAsInt();
         } else {
            throw new JsonParseException("Missing key " + var1);
         }
      }
   }
}

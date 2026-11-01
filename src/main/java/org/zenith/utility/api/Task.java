package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.media.NowPlayingScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.game.FocusSlotEnum;

public final class Task {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/WardrobeShots");
   private final Path path;
   private final Path path2;
   private final List<Task.Data> items = new ArrayList<>();
   private final List<String> items2 = new ArrayList<>();
   private final List<String> items3 = new ArrayList<>();
   private boolean flag;
   private int intValue;
   private int index = -1;
   private int intValue2;
   private boolean flag2;

   private Task(Path var1) {
      this.path = var1.resolve("wardrobe").resolve("autoshot.task.json");
      this.path2 = var1.resolve("wardrobe").resolve("shots");
   }

   public static void method5982(Path var0) {
      if (System.getProperty("DevMode") != null && Boolean.getBoolean("zenith.wardrobe.autoshot")) {
         Task var1 = new Task(var0);
         if (Files.isRegularFile(var1.path)) {
            var1.load();
            ClientTickEvents.END_CLIENT_TICK.register(var1::method5983);
         }
      }
   }

   private void load() {
      JsonObject var1 = JsonIo.method1009(this.path);
      if (var1 != null) {
         this.flag = var1.has("quit") && var1.get("quit").getAsBoolean();
         if (var1.has("setup")) {
            for (JsonElement var3 : var1.getAsJsonArray("setup")) {
               this.items2.add(var3.getAsString());
            }
         }

         if (var1.has("modules")) {
            for (JsonElement var11 : var1.getAsJsonArray("modules")) {
               this.items3.add(var11.getAsString());
            }
         }

         for (JsonElement var4 : var1.has("shots") ? var1.getAsJsonArray("shots") : new JsonArray()) {
            JsonObject var5 = var4.getAsJsonObject();
            ArrayList var6 = new ArrayList();
            if (var5.has("equip")) {
               for (JsonElement var8 : var5.getAsJsonArray("equip")) {
                  var6.add(var8.getAsString());
               }
            }

            this.items
               .add(
                  new Task.Data(
                     var5.get("name").getAsString(),
                     var6,
                     Perspective.valueOf(var5.has("perspective") ? var5.get("perspective").getAsString().toUpperCase(Locale.ROOT) : "THIRD_PERSON_FRONT"),
                     var5.has("yaw") ? var5.get("yaw").getAsFloat() : 0.0F,
                     var5.has("pitch") ? var5.get("pitch").getAsFloat() : 0.0F,
                     var5.has("hold") ? var5.get("hold").getAsString() : "",
                     var5.has("wait") ? var5.get("wait").getAsInt() : 30,
                     var5.has("sneak") && var5.get("sneak").getAsBoolean(),
                     var5.has("cloud") ? var5.get("cloud").getAsInt() : -1,
                     var5.has("friendsAdd") ? var5.get("friendsAdd").getAsString() : null,
                     var5.has("wardrobeTab") ? var5.get("wardrobeTab").getAsInt() : -1,
                     var5.has("screen") ? var5.get("screen").getAsString() : null
                  )
               );
         }

         logger.info("Автоснимки гардероба: {} кадров", this.items.size());
      }
   }

   private void method5983(MinecraftClient var1) {
      if (var1.player != null && var1.world != null) {
         var1.options.pauseOnLostFocus = false;
         boolean var2 = this.index >= 0
            && this.index < this.items.size()
            && (this.items.get(this.index).getIntValue2() >= 0 || this.items.get(this.index).getText4() != null);
         if (var1.currentScreen != null && !var2) {
            var1.setScreen(null);
         }

         if (this.intValue >= 80) {
            if (this.index < this.items.size()) {
               if (this.index < 0) {
                  this.index = 0;
                  this.intValue2 = 0;
                  this.flag2 = false;
                  this.method5984(var1, this.items.get(0));
               }

               Task.Data var8 = this.items.get(this.index);
               this.method5985(var1, var8);
               this.intValue2++;
               if (this.intValue2 % 20 == 0) {
                  logger.info("Автоснимки гардероба: {} fps {}", var8.name(), var1.getCurrentFps());
               }

               if (this.intValue2 == var8.getIntValue() && !this.flag2) {
                  this.flag2 = true;
                  this.method5986(var1, var8.name());
               }

               if (this.intValue2 >= var8.getIntValue() + 5) {
                  this.index++;
                  if (this.index >= this.items.size()) {
                     var1.options.hudHidden = false;
                     logger.info("Автоснимки гардероба готовы: {}", this.path2);
                     if (this.flag) {
                        var1.scheduleStop();
                     }

                     return;
                  }

                  this.intValue2 = 0;
                  this.flag2 = false;
                  this.method5984(var1, this.items.get(this.index));
               }
            }
         } else {
            this.intValue++;
            if (this.intValue == 30) {
               for (String var4 : this.items3) {
                  boolean var5 = var4.startsWith("-");
                  Module var6 = Zenith.getInstance().getModuleManager().method2484(var5 ? var4.substring(1) : var4);
                  if (var6 != null && var6.isEnabled() == var5) {
                     var6.toggle();
                  }
               }
            }

            if (this.intValue == 20 || this.intValue == 40) {
               for (String var9 : this.items2) {
                  var1.player.networkHandler.sendChatCommand(var9);
               }
            }
         }
      } else {
         this.intValue = 0;
      }
   }

   private void method5984(MinecraftClient var1, Task.Data var2) {
      Wardrobe var3 = Wardrobe.method2488();
      if (var3 != null) {
         var3.method2496();

         for (String var5 : var2.getItems()) {
            var3.method2492(var3.getRegistry().method1421(var5));
         }
      }

      var1.options.hudHidden = var2.getPerspective() != Perspective.FIRST_PERSON;
      var1.options.setPerspective(var2.getPerspective());
      ItemStack var7 = ItemStack.EMPTY;
      FocusSlotEnum var8 = FocusSlotEnum.method546(var2.getText2());
      if (var8 != null && var8.method543()) {
         var7 = var8.method544();
      }

      var1.player.setStackInHand(Hand.MAIN_HAND, var7);
      if (var2.getText4() != null && var2.getText4().startsWith("module:")) {
         Module var6 = Zenith.getInstance().getModuleManager().method2484(var2.getText4().substring(7));
         if (!MenuModule.INSTANCE.isEnabled()) {
            MenuModule.INSTANCE.toggle();
         }

         if (var6 != null) {
            Zenith.getInstance().getNLMenuScreen().devShowModule(var6);
         }
      }

      if ("music".equals(var2.getText4())) {
         var1.setScreen(new NowPlayingScreen());
      }

      if (var2.getIntValue2() >= 0) {
         NLMenuScreen var9 = Zenith.getInstance().getNLMenuScreen();
         if (!MenuModule.INSTANCE.isEnabled()) {
            MenuModule.INSTANCE.toggle();
         }

         if (var2.getIntValue3() >= 0) {
            var9.openCosmetics(var2.getIntValue3());
         } else if (var2.getText3() != null) {
            var9.setType(NLMenuScreen.ElementsType.CLOUD);
            var9.getCloudSectionPanel().openFriendsAdd(var2.getText3());
         } else {
            var9.setType(NLMenuScreen.ElementsType.CLOUD);
            var9.getCloudSectionPanel().open(var2.getIntValue2());
         }
      }
   }

   private void method5985(MinecraftClient var1, Task.Data var2) {
      var1.player.setYaw(var2.getFloatValue());
      var1.player.setPitch(var2.getFloatValue2());
      var1.player.setHeadYaw(var2.getFloatValue());
      var1.player.setBodyYaw(var2.getFloatValue());
      var1.player.lastYaw = var2.getFloatValue();
      var1.player.lastPitch = var2.getFloatValue2();
      var1.player.lastBodyYaw = var2.getFloatValue();
      var1.player.lastHeadYaw = var2.getFloatValue();
      var1.player.setSneaking(var2.isFlag());
   }

   private void method5986(MinecraftClient var1, String var2) {
      ScreenshotRecorder.takeScreenshot(var1.getFramebuffer(), var2x -> {
         try {
            Files.createDirectories(this.path2);
            var2x.writeTo(this.path2.resolve(var2 + ".png"));
         } catch (IOException var7) {
            logger.error("Снимок {} не сохранён", var2, var7);
         } finally {
            var2x.close();
         }
      });
   }

   private static final class Data {
      private final String text;
      private final List<String> items;
      private final Perspective perspective;
      private final float floatValue;
      private final float floatValue2;
      private final String text2;
      private final int intValue;
      private final boolean flag;
      private final int intValue2;
      private final String text3;
      private final int intValue3;
      private final String text4;

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      public Perspective perspective() {
         return this.perspective;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public String text3() {
         return this.text3;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public String text4() {
         return this.text4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.perspective, other.perspective) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.text4, other.text4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, perspective, floatValue, floatValue2, text2, intValue, flag, intValue2, text3, intValue3, text4);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "perspective=" + this.perspective + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + ", " + "intValue2=" + this.intValue2 + ", " + "text3=" + this.text3 + ", " + "intValue3=" + this.intValue3 + ", " + "text4=" + this.text4 + "]";
      }

      private Data(
         String var1,
         List<String> var2,
         Perspective var3,
         float var4,
         float var5,
         String var6,
         int var7,
         boolean var8,
         int var9,
         String var10,
         int var11,
         String var12
      ) {
         this.text = var1;
         this.items = var2;
         this.perspective = var3;
         this.floatValue = var4;
         this.floatValue2 = var5;
         this.text2 = var6;
         this.intValue = var7;
         this.flag = var8;
         this.intValue2 = var9;
         this.text3 = var10;
         this.intValue3 = var11;
         this.text4 = var12;
      }

      public String name() {
         return this.text;
      }

      public List<String> getItems() {
         return this.items;
      }

      public Perspective getPerspective() {
         return this.perspective;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public String getText2() {
         return this.text2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public String getText3() {
         return this.text3;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public String getText4() {
         return this.text4;
      }
   }
}

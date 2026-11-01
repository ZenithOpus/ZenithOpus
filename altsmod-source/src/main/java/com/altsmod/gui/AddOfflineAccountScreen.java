package com.altsmod.gui;

import com.altsmod.auth.AccountStorage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AddOfflineAccountScreen extends Screen {
   private static final List<String> PREFIXES = List.of(
      "Shadow", "Frost", "Nova", "Cyber", "Echo", "Lunar", "Neon", "Void",
      "Ember", "Storm", "Vortex", "Phantom", "Pulse", "Zen", "Onyx", "Solar"
   );
   private static final List<String> SUFFIXES = List.of(
      "Runner", "Wolf", "Knight", "Fox", "Player", "Craft", "Master", "Strike",
      "Blade", "Ghost", "Hawk", "King", "Lord", "Rider", "Soul", "Zero"
   );

   private final AltManagerScreen parent;
   private TextFieldWidget usernameField;
   private ButtonWidget addButton;

   public AddOfflineAccountScreen(AltManagerScreen parent) {
      super(Text.literal("Добавить оффлайн аккаунт"));
      this.parent = parent;
   }

   @Override
   protected void init() {
      int centerX = this.width / 2;
      int centerY = this.height / 2 - 20;

      this.usernameField = new TextFieldWidget(this.textRenderer, centerX - 100, centerY, 200, 20, Text.literal("Никнейм"));
      this.usernameField.setMaxLength(16);
      this.usernameField.setPlaceholder(Text.literal("Введите ник..."));
      this.usernameField.setChangedListener(this::onTextChanged);
      this.addDrawableChild(this.usernameField);

      this.addButton = ButtonWidget.builder(Text.literal("Добавить"), btn -> this.addAccount())
         .dimensions(centerX - 100, centerY + 26, 98, 20)
         .build();
      this.addButton.active = false;
      this.addDrawableChild(this.addButton);

      this.addDrawableChild(ButtonWidget.builder(Text.literal("Рандом"), btn -> this.randomName())
         .dimensions(centerX + 2, centerY + 26, 98, 20)
         .build());

      this.addDrawableChild(ButtonWidget.builder(Text.literal("Отмена"), btn -> this.close())
         .dimensions(centerX - 100, centerY + 52, 200, 20)
         .build());

      this.setInitialFocus(this.usernameField);
   }

   private void onTextChanged(String text) {
      String trimmed = text.trim();
      this.addButton.active = !trimmed.isEmpty() && trimmed.length() <= 16;
   }

   private void randomName() {
      ThreadLocalRandom rnd = ThreadLocalRandom.current();
      String prefix = PREFIXES.get(rnd.nextInt(PREFIXES.size()));
      String suffix = SUFFIXES.get(rnd.nextInt(SUFFIXES.size()));
      int num = rnd.nextInt(10, 100);
      String generated = prefix + suffix + num;
      if (generated.length() > 16) {
         generated = generated.substring(0, 16);
      }
      this.usernameField.setText(generated);
   }

   private void addAccount() {
      String name = this.usernameField.getText().trim();
      if (name.isEmpty() || name.length() > 16) {
         return;
      }

      if (this.client != null) {
         UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
         AccountStorage.saveAccount(this.client, name, uuid, "");
         this.parent.onOfflineAccountAdded(name);
         this.client.setScreen(this.parent);
      }
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      if (keyInput.key() == GLFW.GLFW_KEY_ENTER || keyInput.key() == GLFW.GLFW_KEY_KP_ENTER) {
         if (this.addButton != null && this.addButton.active) {
            this.addAccount();
            return true;
         }
      }
      return super.keyPressed(keyInput);
   }

   @Override
   public void close() {
      if (this.client != null) {
         this.client.setScreen(this.parent);
      }
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      context.fill(0, 0, this.width, this.height, -805306368);
      int centerX = this.width / 2;
      int centerY = this.height / 2 - 20;

      context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§lДобавить оффлайн аккаунт"), centerX, centerY - 32, 0xFFFFFF);
      context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Введите никнейм и нажмите Добавить или Enter"), centerX, centerY - 18, 0xAAAAAA);

      super.render(context, mouseX, mouseY, deltaTicks);
   }
}

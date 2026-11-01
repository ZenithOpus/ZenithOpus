package org.zenith.client.screens.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.ChatMessage;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.view.BotPlayerGuiRender;
import org.zenith.base.bot.view.BotRemoteControl;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.base.font.Fonts;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class BotControlScreen extends CustomScreen {
   private static final Identifier HOTBAR_TEXTURE = Identifier.ofVanilla("hud/hotbar");
   private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.ofVanilla("hud/hotbar_selection");
   private static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = Identifier.ofVanilla("hud/hotbar_offhand_left");
   private static final Identifier HEART_CONTAINER_TEXTURE = Identifier.ofVanilla("hud/heart/container");
   private static final Identifier HEART_FULL_TEXTURE = Identifier.ofVanilla("hud/heart/full");
   private static final Identifier HEART_HALF_TEXTURE = Identifier.ofVanilla("hud/heart/half");
   private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.ofVanilla("hud/food_empty");
   private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");
   private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
   private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/armor_empty");
   private static final Identifier ARMOR_FULL_TEXTURE = Identifier.ofVanilla("hud/armor_full");
   private static final Identifier ARMOR_HALF_TEXTURE = Identifier.ofVanilla("hud/armor_half");
   private static final Identifier AIR_TEXTURE = Identifier.ofVanilla("hud/air");
   private static final Identifier AIR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/air_empty");
   private static final Identifier XP_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/experience_bar_background");
   private static final Identifier XP_BAR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/experience_bar_progress");
   private static final Identifier INVENTORY_TEXTURE = Identifier.ofVanilla("textures/gui/container/inventory.png");
   private static final Identifier GENERIC_54_TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
   private static final Identifier SHULKER_BOX_TEXTURE = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
   private static final Identifier HOPPER_TEXTURE = Identifier.ofVanilla("textures/gui/container/hopper.png");
   private static final Identifier DISPENSER_TEXTURE = Identifier.ofVanilla("textures/gui/container/dispenser.png");
   private static final Identifier FURNACE_TEXTURE = Identifier.ofVanilla("textures/gui/container/furnace.png");
   private static final Identifier BLAST_FURNACE_TEXTURE = Identifier.ofVanilla("textures/gui/container/blast_furnace.png");
   private static final Identifier SMOKER_TEXTURE = Identifier.ofVanilla("textures/gui/container/smoker.png");
   private static final Identifier CRAFTING_TABLE_TEXTURE = Identifier.ofVanilla("textures/gui/container/crafting_table.png");
   private static final Identifier SLOT_HIGHLIGHT_BACK_TEXTURE = Identifier.ofVanilla("container/slot_highlight_back");
   private static final Identifier SLOT_HIGHLIGHT_FRONT_TEXTURE = Identifier.ofVanilla("container/slot_highlight_front");
   private final String botName;
   private BotClient boundClient;
   private BotRemoteControl control;
   private BotWorldView view;
   private boolean inventoryOpen;
   private boolean cursorLocked;
   private double lastMouseX = Double.NaN;
   private double lastMouseY = Double.NaN;
   @Nullable
   private Slot hoveredSlotCache;
   private int wheelSlot = -1;
   private static final int CHAT_WIDTH = 320;
   private static final int CHAT_LINE_HEIGHT = 9;
   private static final long CHAT_VISIBLE_MS = 10000L;
   private final SearchBox chatField = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "", 0.0F);
   private final BotChatInputSuggestor chatSuggestor;
   private String originalChatText = "";
   private int chatFirstChar;
   private int messageHistoryIndex;
   private String chatLastMessage = "";
   private boolean chatOpen;
   private boolean suppressNextChar;
   private int chatScrollLines;
   private int chatTotalLines;
   private final Map<ChatMessage, BotControlScreen.CachedChat> chatCache = new IdentityHashMap<>();
   private boolean chatSeeded;
   private boolean keyForward;
   private boolean keyBack;
   private boolean keyLeft;
   private boolean keyRight;
   private boolean keyJump;
   private boolean keySneak;
   private boolean keySprint;
   private BotClient sidebarClient;
   private Text sidebarTitle;
   private List<BotControlScreen.SidebarRow> sidebarRows = List.of();

   public String getBotName() {
      return this.botName;
   }

   public BotControlScreen(String var1) {
      this.botName = var1;
      this.chatSuggestor = new BotChatInputSuggestor(
         this,
         this.chatField,
         mc.textRenderer,
         () -> this.boundClient != null ? this.boundClient.getPlayHandler() : null,
         this::chatCharacterX,
         () -> this.height - 16
      );
   }

   private BotClient ensureBound() {
      BotClient var1 = HeadlessBots.get(this.botName);
      if (var1 != this.boundClient) {
         this.unbind();
         this.boundClient = var1;
         if (var1 != null) {
            this.control = new BotRemoteControl(var1);
            this.control.attach();
            this.view = new BotWorldView(var1);
         }
      }

      return var1;
   }

   private void unbind() {
      if (this.control != null) {
         this.control.detach();
         this.control = null;
      }

      if (this.view != null) {
         this.view.close();
         this.view = null;
      }

      this.boundClient = null;
      this.closeChat();
      this.chatCache.clear();
      this.chatSeeded = false;
   }

   @Override
   public void removed() {
      this.setCursorLocked(false);
      this.unbind();
      super.removed();
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3) {
      BotClient var4 = this.ensureBound();
      BotPlayer var5 = var4 != null ? var4.getPlayer() : null;
      boolean var6 = var4 != null && var4.isJoined() && var5 != null;
      ScreenHandler var7 = var6 ? this.currentUiHandler(var4, var5) : null;
      boolean var8 = var7 != null;
      boolean var9 = mc.currentScreen != this;
      if (this.chatOpen && (!var6 || var8)) {
         this.closeChat();
      }

      if (this.control != null) {
         this.control.setUiScreenOpen(var8 || this.chatOpen || var9);
      }

      this.setCursorLocked(!var9 && var6 && !var8 && !this.chatOpen);
      var1.drawRoundedRect(0.0F, 0.0F, this.width, this.height, ProxyDotRadiusData.INSTANCE, new ZenithColor(10, 10, 14, 255));
      if (!var6) {
         String var14 = t("gui.bot.notInGame");
         var1.drawText(mc.textRenderer, var14, (this.width - mc.textRenderer.getWidth(var14)) / 2, this.height / 2 - 4, -1, true);
         String var15 = t("gui.bot.escBack");
         var1.drawText(mc.textRenderer, var15, (this.width - mc.textRenderer.getWidth(var15)) / 2, this.height / 2 + 8, -6643542, true);
      } else {
         int var10 = mc.getWindow().getFramebufferWidth();
         int var11 = mc.getWindow().getFramebufferHeight();
         boolean var12 = this.view != null && this.view.renderToFbo(var10, var11);
         if (var12) {
            RenderShaders.method5298(
               MatrixUtils.method664(var1.getMatrices()),
               this.view.getColorAttachment(),
               0.0F,
               0.0F,
               this.width,
               this.height,
               ProxyDotRadiusData.INSTANCE,
               ZenithColor.INSTANCE,
               0.0F,
               1.0F,
               1.0F,
               0.0F
            );
         } else {
            String var13 = t("gui.bot.loadingWorld");
            var1.drawText(mc.textRenderer, var13, (this.width - mc.textRenderer.getWidth(var13)) / 2, this.height / 2 - 4, -1, true);
         }

         this.renderHud(var1, var4, var5);
         this.renderScoreboardSidebar(var1, var4);
         ScissorFlusher.flush();
         this.renderChat(var1, var4);
         ScissorFlusher.flush();
         if (var8) {
            this.renderContainer(var1, var5, var7, (int)var2, (int)var3);
            ScissorFlusher.flush();
         } else {
            this.hoveredSlotCache = null;
            this.renderCrosshair(var1);
         }
      }
   }

   private ScreenHandler currentUiHandler(BotClient var1, BotPlayer var2) {
      BotPlayHandler var3 = var1.getPlayHandler();
      if (var3 != null && var3.hasOpenScreen()) {
         this.inventoryOpen = false;
         return var2.currentScreenHandler;
      } else {
         return this.inventoryOpen ? var2.playerScreenHandler : null;
      }
   }

   private void renderCrosshair(MenuDrawContext var1) {
      int var2 = this.width / 2;
      int var3 = this.height / 2;
      int var4 = -855638017;
      var1.fill(var2 - 5, var3, var2 + 4, var3 + 1, var4);
      var1.fill(var2, var3 - 5, var2 + 1, var3 + 4, var4);
   }

   private void renderChat(MenuDrawContext var1, BotClient var2) {
      List<ChatMessage> var3 = var2.getChatLog();
      long var4 = System.currentTimeMillis();
      this.syncChatCache(var3, var4);
      var var6 = new ArrayList<>(var3.size());
      this.chatTotalLines = 0;

      for (Object var8_o : (Iterable<?>)(Object)(var3)) {
         ChatMessage var8 = (ChatMessage)var8_o;
         BotControlScreen.CachedChat var9 = this.chatCache.get(var8);
         if (var9 != null) {
            var6.add(var9);
            this.chatTotalLines = this.chatTotalLines + var9.lines().size();
         }
      }

      int var20 = this.visibleChatLines();
      this.chatScrollLines = MathHelper.clamp(this.chatScrollLines, 0, Math.max(0, this.chatTotalLines - var20));
      int var21 = this.chatOpen ? this.chatScrollLines : 0;
      int var22 = this.height - 52;
      byte var10 = 4;
      int var11 = 0;

      label55:
      for (int var12 = var6.size() - 1; var12 >= 0 && var11 < var20; var12--) {
         BotControlScreen.CachedChat var13 = (BotControlScreen.CachedChat)var6.get(var12);
         float var14 = 1.0F;
         if (!this.chatOpen) {
            long var15 = var4 - var13.seenAt();
            if (var15 >= 10000L) {
               break;
            }

            float var17 = (float)(10000L - var15) / 10000.0F * 10.0F;
            var14 = MathHelper.clamp(var17, 0.0F, 1.0F);
            var14 *= var14;
         }

         var var24 = var13.lines();

         for (int var16 = var24.size() - 1; var16 >= 0; var16--) {
            if (var21 > 0) {
               var21--;
            } else {
               int var25 = var22 - var11 * 9;
               int var18 = (int)(var14 * 128.0F);
               int var19 = (int)(var14 * 255.0F);
               if (var19 > 8) {
                  var1.fill(var10 - 2, var25 - 1, var10 + 320 + 2, var25 + 9 - 1, var18 << 24);
                  var1.drawText(mc.textRenderer, (OrderedText)var24.get(var16), var10, var25, var19 << 24 | 16777215, true);
               }

               if (++var11 >= var20) {
                  break label55;
               }
            }
         }
      }

      if (this.chatOpen) {
         this.renderChatField(var1);
      }
   }

   private void renderChatField(MenuDrawContext var1) {
      this.clampChatFieldScroll();
      this.chatSuggestor.update(this.originalChatText);
      String var2 = this.chatField.getText();
      int var3 = this.height - 16;
      var1.drawRoundedRect(2.0F, var3, this.width - 2, 14.0F, ProxyDotRadiusData.INSTANCE, new ZenithColor(Integer.MIN_VALUE));
      byte var4 = 6;
      int var5 = var3 + 3;
      int var6 = MathHelper.clamp(this.chatField.getCursor(), 0, var2.length());
      String var7 = mc.textRenderer.trimToWidth(var2.substring(this.chatFirstChar), this.chatInnerWidth());
      if (this.chatField.isFlag() && !var2.isEmpty()) {
         var1.fill(var4 - 1, var3 + 1, var4 + mc.textRenderer.getWidth(var7) + 1, var3 + 13, -2139062144);
      }

      if (!var7.isEmpty()) {
         OrderedText var8 = this.chatSuggestor.provideRenderText(var7, this.chatFirstChar);
         var1.drawText(mc.textRenderer, var8, var4, var5, -2039584, true);
      }

      String var13 = this.chatSuggestor.getSuggestion();
      boolean var9 = this.chatFirstChar + var7.length() >= var2.length();
      int var10 = var4 + mc.textRenderer.getWidth(var7);
      if (var13 != null && !var13.isEmpty() && var6 == var2.length() && var9) {
         var1.drawText(mc.textRenderer, mc.textRenderer.trimToWidth(var13, this.width - 6 - var10), var10, var5, -8355712, true);
      }

      if (System.currentTimeMillis() / 300L % 2L == 0L) {
         int var11 = MathHelper.clamp(var6 - this.chatFirstChar, 0, var7.length());
         int var12 = var4 + mc.textRenderer.getWidth(var7.substring(0, var11));
         if (var6 == var2.length()) {
            var1.drawText(mc.textRenderer, "_", var12, var5, -2039584, true);
         } else {
            var1.fill(var12, var5 - 1, var12 + 1, var5 + 10, -3092272);
         }
      }

      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(0.0F, 0.0F);
      this.chatSuggestor.render(var1, var1.getMouseX(), var1.getMouseY());
      var1.getMatrices().popMatrix();
   }

   private int chatInnerWidth() {
      return this.width - 12;
   }

   private void clampChatFieldScroll() {
      String var1 = this.chatField.getText();
      int var2 = MathHelper.clamp(this.chatField.getCursor(), 0, var1.length());
      this.chatFirstChar = MathHelper.clamp(this.chatFirstChar, 0, var1.length());
      if (var2 < this.chatFirstChar) {
         this.chatFirstChar = var2;
      }

      while (this.chatFirstChar < var2 && mc.textRenderer.getWidth(var1.substring(this.chatFirstChar, var2)) > this.chatInnerWidth()) {
         this.chatFirstChar++;
      }
   }

   private int chatCharacterX(int var1) {
      String var2 = this.chatField.getText();
      int var3 = MathHelper.clamp(var1, this.chatFirstChar, var2.length());
      return 6 + mc.textRenderer.getWidth(var2.substring(this.chatFirstChar, var3));
   }

   private void syncChatCache(List<ChatMessage> var1, long var2) {
      if (!this.chatSeeded) {
         this.chatSeeded = true;

         for (ChatMessage var5 : var1) {
            this.chatCache.put(var5, new BotControlScreen.CachedChat(0L, this.wrapChatLines(var5)));
         }
      }

      for (ChatMessage var8 : var1) {
         this.chatCache.computeIfAbsent(var8, var3 -> new BotControlScreen.CachedChat(var2, this.wrapChatLines(var3)));
      }

      if (this.chatCache.size() > var1.size()) {
         var var7 = Collections.newSetFromMap(new IdentityHashMap());
         var7.addAll(var1);
         this.chatCache.keySet().retainAll(var7);
      }
   }

   private List<OrderedText> wrapChatLines(ChatMessage var1) {
      return ChatMessages.breakRenderedChatMessageLines(var1.text(), 312, mc.textRenderer);
   }

   private int visibleChatLines() {
      return this.chatOpen ? MathHelper.clamp((this.height - 100) / 9, 4, 20) : 12;
   }

   private void openChat(String var1) {
      this.chatOpen = true;
      this.suppressNextChar = true;
      this.chatScrollLines = 0;
      this.chatFirstChar = 0;
      this.chatField.setMaxLength(256);
      this.chatField.setText(var1);
      this.chatField.setCursor(var1.length());
      this.chatField.setSelected(true);
      this.chatField.setFlag(false);
      this.originalChatText = var1;
      this.chatLastMessage = "";
      BotClient var2 = this.boundClient;
      this.messageHistoryIndex = var2 != null ? var2.getSentMessages().size() : 0;
      this.chatSuggestor.reset(var1);
      this.clearMovementKeys();
   }

   private void closeChat() {
      this.chatOpen = false;
      this.suppressNextChar = false;
      this.chatScrollLines = 0;
      this.chatFirstChar = 0;
      this.chatField.setText("");
      this.chatField.setCursor(0);
      this.chatField.setSelected(false);
      this.originalChatText = "";
      this.chatSuggestor.setWindowActive(false);
      this.chatSuggestor.clearWindow();
   }

   private void setChatFromHistory(int var1) {
      BotClient var2 = this.boundClient;
      if (var2 != null) {
         var var3 = var2.getSentMessages();
         int var4 = MathHelper.clamp(this.messageHistoryIndex + var1, 0, var3.size());
         if (var4 != this.messageHistoryIndex) {
            if (var4 == var3.size()) {
               this.messageHistoryIndex = var4;
               this.chatField.setText(this.chatLastMessage);
               this.chatField.setCursor(this.chatLastMessage.length());
            } else {
               if (this.messageHistoryIndex == var3.size()) {
                  this.chatLastMessage = this.chatField.getText();
               }

               String var5 = (String)var3.get(var4);
               this.chatField.setText(var5);
               this.chatField.setCursor(var5.length());
               this.chatSuggestor.update(this.originalChatText);
               this.chatSuggestor.setWindowActive(false);
               this.messageHistoryIndex = var4;
            }
         }
      }
   }

   private void clearMovementKeys() {
      this.keyForward = this.keyBack = this.keyLeft = this.keyRight = this.keyJump = this.keySneak = this.keySprint = false;
      if (this.control != null) {
         this.control.setMovement(false, false, false, false, false, false, false);
      }
   }

   private void renderHud(MenuDrawContext var1, BotClient var2, BotPlayer var3) {
      int var4 = this.width / 2;
      int var5 = this.height;
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, var4 - 91, var5 - 22, 182, 22);
      int var6 = var3.getInventory().getSelectedSlot();
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_TEXTURE, var4 - 91 - 1 + var6 * 20, var5 - 22 - 1, 24, 23);
      ItemStack var7 = var3.getOffHandStack();
      if (!var7.isEmpty()) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_TEXTURE, var4 - 91 - 29, var5 - 23, 29, 24);
      }

      int var8 = var5 - 16 - 3;

      for (int var9 = 0; var9 < 9; var9++) {
         ItemStack var10 = var3.getInventory().getMainStacks().get(var9);
         int var11 = var4 - 90 + var9 * 20 + 2;
         this.drawStack(var1, var10, var11, var8);
      }

      if (!var7.isEmpty()) {
         this.drawStack(var1, var7, var4 - 91 - 26, var8);
      }

      int var23 = var4 - 91;
      int var24 = var4 + 91;
      int var25 = var5 - 39;
      int var12 = MathHelper.ceil(var3.getHealth());
      int var13 = MathHelper.ceil(var3.getMaxHealth() / 2.0F);

      for (int var14 = 0; var14 < Math.min(var13, 10); var14++) {
         int var15 = var23 + var14 * 8;
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HEART_CONTAINER_TEXTURE, var15, var25, 9, 9);
         if (var14 * 2 + 1 < var12) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HEART_FULL_TEXTURE, var15, var25, 9, 9);
         } else if (var14 * 2 + 1 == var12) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HEART_HALF_TEXTURE, var15, var25, 9, 9);
         }
      }

      int var26 = var3.getArmor();
      if (var26 > 0) {
         int var27 = var25 - 10;

         for (int var16 = 0; var16 < 10; var16++) {
            int var17 = var23 + var16 * 8;
            if (var16 * 2 + 1 < var26) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_FULL_TEXTURE, var17, var27, 9, 9);
            } else if (var16 * 2 + 1 == var26) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_HALF_TEXTURE, var17, var27, 9, 9);
            } else {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_TEXTURE, var17, var27, 9, 9);
            }
         }
      }

      int var28 = var3.getHungerManager().getFoodLevel();

      for (int var29 = 0; var29 < 10; var29++) {
         int var31 = var24 - var29 * 8 - 9;
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FOOD_EMPTY_TEXTURE, var31, var25, 9, 9);
         if (var29 * 2 + 1 < var28) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FOOD_FULL_TEXTURE, var31, var25, 9, 9);
         } else if (var29 * 2 + 1 == var28) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FOOD_HALF_TEXTURE, var31, var25, 9, 9);
         }
      }

      int var30 = var3.getMaxAir();
      int var32 = Math.min(var3.getAir(), var30);
      if (var3.isSubmergedInWater() || var32 < var30) {
         int var18 = var25 - 10;
         int var19 = MathHelper.ceil((var32 - 2.0) * 10.0 / var30);
         int var20 = MathHelper.ceil(var32 * 10.0 / var30) - var19;

         for (int var21 = 0; var21 < var19 + var20; var21++) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, var21 < var19 ? AIR_TEXTURE : AIR_EMPTY_TEXTURE, var24 - var21 * 8 - 9, var18, 9, 9);
         }
      }

      int var33 = var5 - 32 + 3;
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, XP_BAR_BACKGROUND_TEXTURE, var23, var33, 182, 5);
      int var34 = (int)(var3.experienceProgress * 183.0F);
      if (var34 > 0) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, XP_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, var23, var33, var34, 5);
      }

      if (var3.experienceLevel > 0) {
         String var35 = String.valueOf(var3.experienceLevel);
         int var37 = (this.width - mc.textRenderer.getWidth(var35)) / 2;
         int var22 = var5 - 31 - 4;
         var1.drawText(mc.textRenderer, var35, var37 + 1, var22, -16777216, false);
         var1.drawText(mc.textRenderer, var35, var37 - 1, var22, -16777216, false);
         var1.drawText(mc.textRenderer, var35, var37, var22 + 1, -16777216, false);
         var1.drawText(mc.textRenderer, var35, var37, var22 - 1, -16777216, false);
         var1.drawText(mc.textRenderer, var35, var37, var22, -8323296, false);
      }

      String var36 = var2.getName() + "  §7" + (int)var3.getX() + " " + (int)var3.getY() + " " + (int)var3.getZ();
      var1.drawText(mc.textRenderer, var36, 6, 6, -1, true);
      String var38 = t("gui.bot.escExit");
      var1.drawText(mc.textRenderer, var38, 6, 17, -6643542, true);
      if (this.view != null) {
      }
   }

   private void renderScoreboardSidebar(MenuDrawContext var1, BotClient var2) {
      BotWorld var3 = var2.getWorld();
      if (var3 != null) {
         Text var4;
         List var5;
         try {
            synchronized (var2.getRenderStateLock()) {
               label120: {
                  Scoreboard var7 = var3.getScoreboard();
                  synchronized (var7) {
                     ScoreboardObjective var9 = null;
                     Team var10 = var7.getScoreHolderTeam(var2.getName());
                     if (var10 != null) {
                        ScoreboardDisplaySlot var11 = ScoreboardDisplaySlot.fromFormatting(var10.getColor());
                        if (var11 != null) {
                           var9 = var7.getObjectiveForSlot(var11);
                        }
                     }

                     if (var9 == null) {
                        var9 = var7.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
                     }

                     if (var9 == null) {
                        this.sidebarTitle = null;
                     } else {
                        NumberFormat var27 = var9.getNumberFormatOr(StyledNumberFormat.RED);
                        List<ScoreboardEntry> var12 = new ArrayList<>(var7.getScoreboardEntries(var9));
                        var12.removeIf(ScoreboardEntry::hidden);
                        var12.sort(InGameHud.SCOREBOARD_ENTRY_COMPARATOR);
                        if (var12.size() > 15) {
                           var12 = var12.subList(0, 15);
                        }

                        var5 = new ArrayList(var12.size());

                        for (Object var14_o : (Iterable<?>)(Object)(var12)) {
                           ScoreboardEntry var14 = (ScoreboardEntry)var14_o;
                           MutableText var15 = Team.decorateName(var7.getScoreHolderTeam(var14.owner()), var14.name());
                           MutableText var16 = var14.formatted(var27);
                           var5.add(new BotControlScreen.SidebarRow(var15, var16, mc.textRenderer.getWidth(var16)));
                        }

                        var4 = var9.getDisplayName();
                        break label120;
                     }
                  }

                  return;
               }
            }

            this.sidebarClient = var2;
            this.sidebarTitle = var4;
            this.sidebarRows = (List<BotControlScreen.SidebarRow>)var5;
         } catch (RuntimeException var21) {
            if (this.sidebarClient != var2 || this.sidebarTitle == null) {
               return;
            }

            var4 = this.sidebarTitle;
            var5 = this.sidebarRows;
         }

         int var6 = mc.textRenderer.getWidth(var4);
         int var22 = var6;
         int var8 = mc.textRenderer.getWidth(": ");

         for (Object var25_o : (Iterable<?>)(Object)(var5)) {
            BotControlScreen.SidebarRow var25 = (BotControlScreen.SidebarRow)var25_o;
            var22 = Math.max(var22, mc.textRenderer.getWidth(var25.name()) + (var25.scoreWidth() > 0 ? var8 + var25.scoreWidth() : 0));
         }

         byte var24 = 9;
         int var26 = var5.size() * var24;
         int var28 = this.height / 2 + var26 / 3;
         int var29 = this.width - 3;
         int var30 = var29 - var22;
         int var31 = var28 - var26;
         var1.fill(var30 - 2, var31 - var24 - 1, var29 + 2, var31 - 1, 1711276032);
         var1.fill(var30 - 2, var31 - 1, var29 + 2, var28, 1275068416);
         var1.drawText(mc.textRenderer, var4, var30 + var22 / 2 - var6 / 2, var31 - var24, -1, false);

         for (int var32 = 0; var32 < var5.size(); var32++) {
            BotControlScreen.SidebarRow var33 = (BotControlScreen.SidebarRow)var5.get(var32);
            int var17 = var31 + var32 * var24;
            var1.drawText(mc.textRenderer, var33.name(), var30, var17, -1, false);
            if (var33.scoreWidth() > 0) {
               var1.drawText(mc.textRenderer, var33.score(), var29 - var33.scoreWidth(), var17, -1, false);
            }
         }
      }
   }

   private void drawStack(MenuDrawContext var1, ItemStack var2, int var3, int var4) {
      if (!var2.isEmpty()) {
         var1.drawItemWithoutEntity(var2, var3, var4);
         var1.drawStackOverlay(mc.textRenderer, var2, var3, var4);
      }
   }

   private BotControlScreen.ContainerLayout layoutFor(BotPlayer var1, ScreenHandler var2) {
      if (var2 == var1.playerScreenHandler) {
         return new BotControlScreen.ContainerLayout(INVENTORY_TEXTURE, 176, 166, 0, 97, false, false);
      } else if (var2 instanceof GenericContainerScreenHandler var8) {
         int var9 = var8.getRows();
         return new BotControlScreen.ContainerLayout(GENERIC_54_TEXTURE, 176, 114 + var9 * 18, var9, 8, false, true);
      } else if (var2 instanceof ShulkerBoxScreenHandler) {
         return new BotControlScreen.ContainerLayout(SHULKER_BOX_TEXTURE, 176, 166, 0, 8, false, true);
      } else if (var2 instanceof HopperScreenHandler) {
         return new BotControlScreen.ContainerLayout(HOPPER_TEXTURE, 176, 133, 0, 8, false, true);
      } else if (var2 instanceof Generic3x3ContainerScreenHandler) {
         return new BotControlScreen.ContainerLayout(DISPENSER_TEXTURE, 176, 166, 0, 8, true, true);
      } else if (var2 instanceof AbstractFurnaceScreenHandler) {
         Identifier var7 = var2 instanceof BlastFurnaceScreenHandler
            ? BLAST_FURNACE_TEXTURE
            : (var2 instanceof SmokerScreenHandler ? SMOKER_TEXTURE : FURNACE_TEXTURE);
         return new BotControlScreen.ContainerLayout(var7, 176, 166, 0, 8, true, true);
      } else if (var2 instanceof CraftingScreenHandler) {
         return new BotControlScreen.ContainerLayout(CRAFTING_TABLE_TEXTURE, 176, 166, 0, 29, false, true);
      } else {
         int var3 = 160;
         int var4 = 142;

         for (Slot var6 : var2.slots) {
            var3 = Math.max(var3, var6.x);
            var4 = Math.max(var4, var6.y);
         }

         return new BotControlScreen.ContainerLayout(null, Math.max(176, var3 + 16 + 8), var4 + 16 + 8, 0, 8, false, false);
      }
   }

   private void renderContainer(MenuDrawContext var1, BotPlayer var2, ScreenHandler var3, int var4, int var5) {
      var1.drawRoundedRect(0.0F, 0.0F, this.width, this.height, ProxyDotRadiusData.INSTANCE, new ZenithColor(0, 0, 0, 120));
      BotControlScreen.ContainerLayout var6 = this.layoutFor(var2, var3);
      int var7 = (this.width - var6.width()) / 2;
      int var8 = (this.height - var6.height()) / 2;
      if (var6.texture() == null) {
         this.drawSyntheticPanel(var1, var7, var8, var6.width(), var6.height());

         for (Slot var10 : var3.slots) {
            this.drawSlotFrame(var1, var7 + var10.x, var8 + var10.y);
         }
      } else if (var6.chestRows() > 0) {
         var1.drawTexture(RenderPipelines.GUI_TEXTURED, var6.texture(), var7, var8, 0.0F, 0.0F, var6.width(), var6.chestRows() * 18 + 17, 256, 256);
         var1.drawTexture(RenderPipelines.GUI_TEXTURED, var6.texture(), var7, var8 + var6.chestRows() * 18 + 17, 0.0F, 126.0F, var6.width(), 96, 256, 256);
      } else {
         var1.drawTexture(RenderPipelines.GUI_TEXTURED, var6.texture(), var7, var8, 0.0F, 0.0F, var6.width(), var6.height(), 256, 256);
      }

      if (var3 == var2.playerScreenHandler) {
         BotClient var15 = this.boundClient;
         BotPlayerGuiRender.drawEntity(
            var1, var7 + 26, var8 + 8, var7 + 75, var8 + 78, 30, 0.0625F, var4, var5, var2, var15 != null ? var15.getPlayHandler() : null
         );
      }

      Slot var16 = this.hoveredSlot(var2, var3, var4, var5);
      this.hoveredSlotCache = var16;
      if (var16 != null && var16.canBeHighlighted()) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_TEXTURE, var7 + var16.x - 4, var8 + var16.y - 4, 24, 24);
      }

      for (Slot var11 : var3.slots) {
         if (var11.isEnabled()) {
            this.drawStack(var1, var11.getStack(), var7 + var11.x, var8 + var11.y);
         }
      }

      if (var16 != null && var16.canBeHighlighted()) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_TEXTURE, var7 + var16.x - 4, var8 + var16.y - 4, 24, 24);
      }

      Text var18 = this.containerTitle(var2, var3);
      int var19 = var6.centerTitle() ? (var6.width() - mc.textRenderer.getWidth(var18)) / 2 : var6.titleX();
      var1.drawText(mc.textRenderer, var18, var7 + var19, var8 + 6, 4210752, false);
      if (var6.playerInventoryLabel()) {
         var1.drawText(mc.textRenderer, Text.translatable("container.inventory"), var7 + 8, var8 + var6.height() - 94, 4210752, false);
      }

      ItemStack var12 = var3.getCursorStack();
      if (!var12.isEmpty()) {
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(0.0F, 0.0F);
         this.drawStack(var1, var12, var4 - 8, var5 - 8);
         var1.getMatrices().popMatrix();
      }

      if (var16 != null && var16.hasStack() && var12.isEmpty()) {
         ItemStack var13 = var16.getStack();
         var var14 = var13.getTooltip(
            TooltipContext.create(var2.getEntityWorld()), var2, mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC
         );
         var1.drawTooltip(mc.textRenderer, var14, var13.getTooltipData(), var4, var5, var13.get(DataComponentTypes.TOOLTIP_STYLE));
      }
   }

   private void drawSyntheticPanel(MenuDrawContext var1, int var2, int var3, int var4, int var5) {
      var1.fill(var2 - 1, var3 - 1, var2 + var4 + 1, var3 + var5 + 1, -16777216);
      var1.fill(var2, var3, var2 + var4, var3 + var5, -3750202);
      var1.fill(var2, var3, var2 + var4 - 1, var3 + 1, -1);
      var1.fill(var2, var3 + 1, var2 + 1, var3 + var5 - 1, -1);
      var1.fill(var2 + 1, var3 + var5 - 1, var2 + var4, var3 + var5, -11184811);
      var1.fill(var2 + var4 - 1, var3 + 1, var2 + var4, var3 + var5 - 1, -11184811);
   }

   private void drawSlotFrame(MenuDrawContext var1, int var2, int var3) {
      var1.fill(var2 - 1, var3 - 1, var2 + 17, var3 + 17, -7631989);
      var1.fill(var2 - 1, var3 - 1, var2 + 16, var3, -13158601);
      var1.fill(var2 - 1, var3, var2, var3 + 16, -13158601);
      var1.fill(var2, var3 + 16, var2 + 17, var3 + 17, -1);
      var1.fill(var2 + 16, var3, var2 + 17, var3 + 16, -1);
   }

   private Text containerTitle(BotPlayer var1, ScreenHandler var2) {
      if (var2 == var1.playerScreenHandler) {
         return Text.translatable("container.crafting");
      } else {
         BotClient var3 = this.boundClient;
         BotPlayHandler var4 = var3 != null ? var3.getPlayHandler() : null;
         Text var5 = var4 != null ? var4.getCurrentScreenTitle() : null;
         return (Text)(var5 != null ? var5 : Text.literal(t("gui.bot.container")));
      }
   }

   @Nullable
   private Slot hoveredSlot(BotPlayer var1, ScreenHandler var2, int var3, int var4) {
      BotControlScreen.ContainerLayout var5 = this.layoutFor(var1, var2);
      int var6 = (this.width - var5.width()) / 2;
      int var7 = (this.height - var5.height()) / 2;

      for (Slot var9 : var2.slots) {
         if (var9.isEnabled()) {
            int var10 = var3 - var6;
            int var11 = var4 - var7;
            if (var10 >= var9.x - 1 && var10 < var9.x + 17 && var11 >= var9.y - 1 && var11 < var9.y + 17) {
               return var9;
            }
         }
      }

      return null;
   }

   private boolean isClickOutsideBounds(BotPlayer var1, ScreenHandler var2, double var3, double var5) {
      BotControlScreen.ContainerLayout var7 = this.layoutFor(var1, var2);
      int var8 = (this.width - var7.width()) / 2;
      int var9 = (this.height - var7.height()) / 2;
      return var3 < var8 || var5 < var9 || var3 >= var8 + var7.width() || var5 >= var9 + var7.height();
   }

   private boolean uiOpen() {
      BotClient var1 = this.boundClient;
      BotPlayer var2 = var1 != null ? var1.getPlayer() : null;
      return var1 != null && var2 != null ? this.currentUiHandler(var1, var2) != null : false;
   }

   @Override
   public void mouseMoved(double mouseX, double mouseY) {
      if (this.cursorLocked && this.control != null) {
         if (!Double.isNaN(this.lastMouseX)) {
            double var5 = mc.getWindow().getScaleFactor();
            this.control.turn((mouseX - this.lastMouseX) * var5, (mouseY - this.lastMouseY) * var5);
         }

         this.lastMouseX = mouseX;
         this.lastMouseY = mouseY;
      } else {
         this.lastMouseX = Double.NaN;
         this.lastMouseY = Double.NaN;
      }

      super.mouseMoved(mouseX, mouseY);
   }

   @Override
   public void onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.chatOpen) {
         this.chatSuggestor.mouseClicked(var1, var3);
      } else if (this.control != null) {
         BotClient var6 = this.boundClient;
         BotPlayer var7 = var6 != null ? var6.getPlayer() : null;
         if (var7 != null) {
            ScreenHandler var8 = this.currentUiHandler(var6, var7);
            if (var8 != null) {
               int var9 = var5 == Enum3.RIGHT ? 1 : 0;
               Slot var10 = this.hoveredSlot(var7, var8, (int)var1, (int)var3);
               if (var10 != null) {
                  SlotActionType var11 = WindowUtils.method5476() ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP;
                  this.control.clickSlot(var10.id, var9, var11);
               } else if (!var8.getCursorStack().isEmpty() && this.isClickOutsideBounds(var7, var8, var1, var3)) {
                  this.control.clickSlot(64537, var9, SlotActionType.PICKUP);
               }
            } else {
               if (var5 == Enum3.LEFT) {
                  this.control.pressAttack();
               } else if (var5 == Enum3.RIGHT) {
                  this.control.pressUse();
               }
            }
         }
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      if (this.control != null) {
         if (var5 == Enum3.LEFT) {
            this.control.releaseAttack();
         } else if (var5 == Enum3.RIGHT) {
            this.control.releaseUse();
         }
      }
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      BotClient var4 = this.boundClient;
      boolean var5 = var4 != null && var4.getPlayHandler() != null && var4.getPlayHandler().hasOpenScreen();
      if (this.chatOpen) {
         if (this.chatSuggestor.keyPressed(var1, var2, var3)) {
            return true;
         } else if (var1 == 256) {
            this.closeChat();
            return true;
         } else if (var1 == 257 || var1 == 335) {
            String var9 = this.chatField.getText().trim();
            if (!var9.isEmpty() && var4 != null) {
               var4.addSentMessage(var9);
               var4.sendChat(var9);
            }

            this.closeChat();
            return true;
         } else if (var1 == 265) {
            this.setChatFromHistory(-1);
            return true;
         } else if (var1 == 264) {
            this.setChatFromHistory(1);
            return true;
         } else if (var1 == 266) {
            this.chatScrollLines = MathHelper.clamp(
               this.chatScrollLines + this.visibleChatLines() - 1, 0, Math.max(0, this.chatTotalLines - this.visibleChatLines())
            );
            return true;
         } else if (var1 == 267) {
            this.chatScrollLines = MathHelper.clamp(
               this.chatScrollLines - this.visibleChatLines() + 1, 0, Math.max(0, this.chatTotalLines - this.visibleChatLines())
            );
            return true;
         } else {
            this.chatField.keyPressed(var1, var2, var3);
            return true;
         }
      } else if (var1 == 256) {
         if (var5 && this.control != null) {
            this.control.closeScreen();
            return true;
         } else if (this.inventoryOpen) {
            this.inventoryOpen = false;
            if (this.control != null) {
               this.control.closeScreen();
            }

            return true;
         } else {
            mc.setScreen(new BotScreen());
            return true;
         }
      } else if (var1 == 69) {
         if (this.control != null) {
            if (var5) {
               this.control.closeScreen();
            } else if (this.inventoryOpen) {
               this.inventoryOpen = false;
               this.control.closeScreen();
            } else {
               this.inventoryOpen = true;
            }
         }

         return true;
      } else if (var1 == 70 && this.control != null && this.uiOpen()) {
         BotPlayer var6 = var4 != null ? var4.getPlayer() : null;
         ScreenHandler var7 = var6 != null ? this.currentUiHandler(var4, var6) : null;
         Slot var8 = this.hoveredSlotCache;
         if (var7 != null && var8 != null && var7.getCursorStack().isEmpty()) {
            this.control.clickSlot(var8.id, 40, SlotActionType.SWAP);
         }

         return true;
      } else {
         if (this.control != null && !this.uiOpen()) {
            if (var1 >= 49 && var1 <= 57) {
               this.wheelSlot = var1 - 49;
               this.control.requestSlot(this.wheelSlot);
               return true;
            }

            if (var1 == 81) {
               this.control.requestDrop(WindowUtils.method5475());
               return true;
            }

            if (var1 == 70) {
               this.control.requestSwapHands();
               return true;
            }

            if (var1 == 84) {
               this.openChat("");
               return true;
            }

            if (var1 == 47) {
               this.openChat("/");
               return true;
            }
         }

         return this.updateMovementKey(var1, true) ? true : super.keyPressed(new KeyInput(var1, var2, var3));
      }
   }

   @Override
   public boolean charTyped(CharInput input) {
      return this.charTyped((char)input.codepoint(), input.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      if (this.suppressNextChar) {
         this.suppressNextChar = false;
         return true;
      } else if (this.chatOpen) {
         this.chatField.charTyped(var1, var2);
         return true;
      } else {
         return super.charTyped(new CharInput(var1, var2));
      }
   }

   @Override
   public boolean keyReleased(KeyInput input) {
      return this.keyReleased(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      return this.updateMovementKey(var1, false) ? true : super.keyReleased(new KeyInput(var1, var2, var3));
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.chatOpen && this.chatSuggestor.mouseScrolled(verticalAmount, mouseX, mouseY)) {
         return true;
      } else if (this.chatOpen && verticalAmount != 0.0) {
         int var13 = Math.max(0, this.chatTotalLines - this.visibleChatLines());
         this.chatScrollLines = MathHelper.clamp(this.chatScrollLines + (verticalAmount > 0.0 ? 1 : -1), 0, var13);
         return true;
      } else {
         if (this.control != null && verticalAmount != 0.0 && !this.uiOpen()) {
            BotClient var9 = this.boundClient;
            BotPlayer var10 = var9 != null ? var9.getPlayer() : null;
            if (var10 != null) {
               int var11 = var10.getInventory().getSelectedSlot();
               int var12 = this.wheelSlot >= 0 && this.wheelSlot != var11 ? this.wheelSlot : var11;
               this.wheelSlot = Math.floorMod(var12 - (int)Math.signum(verticalAmount), 9);
               this.control.requestSlot(this.wheelSlot);
               return true;
            }
         }

         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   private boolean updateMovementKey(int var1, boolean var2) {
      switch (var1) {
         case 32:
            this.keyJump = var2;
            break;
         case 65:
            this.keyLeft = var2;
            break;
         case 68:
            this.keyRight = var2;
            break;
         case 83:
            this.keyBack = var2;
            break;
         case 87:
            this.keyForward = var2;
            break;
         case 340:
            this.keySneak = var2;
            break;
         case 341:
            this.keySprint = var2;
            break;
         default:
            return false;
      }

      if (this.control != null) {
         this.control.setMovement(this.keyForward, this.keyBack, this.keyLeft, this.keyRight, this.keyJump, this.keySneak, this.keySprint);
      }

      return true;
   }

   private void setCursorLocked(boolean var1) {
      if (this.cursorLocked != var1) {
         this.cursorLocked = var1;
         this.lastMouseX = Double.NaN;
         this.lastMouseY = Double.NaN;
         Window var2 = mc.getWindow();
         double var3 = mc.getWindow().getWidth() / 2.0;
         double var5 = mc.getWindow().getHeight() / 2.0;
         InputUtil.setCursorParameters(var2, var1 ? 212995 : 212993, var3, var5);
      }
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private static final class CachedChat {
      private final long seenAt;
      private final List<OrderedText> lines;

      public long seenAt() {
         return this.seenAt;
      }

      public List<OrderedText> lines() {
         return this.lines;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CachedChat other)) return false;
         return java.util.Objects.equals(this.seenAt, other.seenAt) && java.util.Objects.equals(this.lines, other.lines);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(seenAt, lines);
      }

      @Override
      public String toString() {
         return "CachedChat[" + "seenAt=" + this.seenAt + ", " + "lines=" + this.lines + "]";
      }

      private CachedChat(long var1, List<OrderedText> var3) {
         this.seenAt = var1;
         this.lines = var3;
      }
   }

   private static final class ContainerLayout {
      @Nullable
      private final Identifier texture;
      private final int width;
      private final int height;
      private final int chestRows;
      private final int titleX;
      private final boolean centerTitle;
      private final boolean playerInventoryLabel;

      public Identifier texture() {
         return this.texture;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public int chestRows() {
         return this.chestRows;
      }

      public int titleX() {
         return this.titleX;
      }

      public boolean centerTitle() {
         return this.centerTitle;
      }

      public boolean playerInventoryLabel() {
         return this.playerInventoryLabel;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ContainerLayout other)) return false;
         return java.util.Objects.equals(this.texture, other.texture) && java.util.Objects.equals(this.width, other.width) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.chestRows, other.chestRows) && java.util.Objects.equals(this.titleX, other.titleX) && java.util.Objects.equals(this.centerTitle, other.centerTitle) && java.util.Objects.equals(this.playerInventoryLabel, other.playerInventoryLabel);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(texture, width, height, chestRows, titleX, centerTitle, playerInventoryLabel);
      }

      @Override
      public String toString() {
         return "ContainerLayout[" + "texture=" + this.texture + ", " + "width=" + this.width + ", " + "height=" + this.height + ", " + "chestRows=" + this.chestRows + ", " + "titleX=" + this.titleX + ", " + "centerTitle=" + this.centerTitle + ", " + "playerInventoryLabel=" + this.playerInventoryLabel + "]";
      }

      private ContainerLayout(@Nullable Identifier var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7) {
         this.texture = var1;
         this.width = var2;
         this.height = var3;
         this.chestRows = var4;
         this.titleX = var5;
         this.centerTitle = var6;
         this.playerInventoryLabel = var7;
      }
   }

   private static final class SidebarRow {
      private final Text name;
      private final Text score;
      private final int scoreWidth;

      public Text name() {
         return this.name;
      }

      public Text score() {
         return this.score;
      }

      public int scoreWidth() {
         return this.scoreWidth;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SidebarRow other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.score, other.score) && java.util.Objects.equals(this.scoreWidth, other.scoreWidth);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, score, scoreWidth);
      }

      @Override
      public String toString() {
         return "SidebarRow[" + "name=" + this.name + ", " + "score=" + this.score + ", " + "scoreWidth=" + this.scoreWidth + "]";
      }

      private SidebarRow(Text var1, Text var2, int var3) {
         this.name = var1;
         this.score = var2;
         this.scoreWidth = var3;
      }
   }
}

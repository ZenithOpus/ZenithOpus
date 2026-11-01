package org.zenith.client.screens.nlgui.panel;

import com.darkmagician6.eventapi.EventManager;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.TickEvent;
import org.zenith.client.screens.media.MediaGlyphs;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.screens.scripts.ScriptInfoPage;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.api.AiChatStorage;
import org.zenith.utility.api.AiSettingsStore;
import org.zenith.utility.config.AiImageStore;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.ShownProvider;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptAiTab {
   private static final float INSET = 8.0F;
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor WARN = new ZenithColor(247, 206, 59);
   private static final Pattern BLOCK = Pattern.compile("```([^\\n`]*)\\n(.*?)(```|$)", 32);
   private static final Pattern FILE = Pattern.compile("(?:file|path|title)\\s*[:=]\\s*\"?([^\"]+)\"?");
   private static final String[] IDEAS = new String[]{"gui.scripts.ai.idea.1", "gui.scripts.ai.idea.2", "gui.scripts.ai.idea.3", "gui.scripts.ai.idea.4"};
   private final ScriptsSectionPanel panel;
   public final FormField input = new FormField(6000, true).lines(2, 5);
   public final FormField keyField = new FormField(300, false).masked(true);
   public final FormField modelField = new FormField(120, false);
   public final FormField endpointField = new FormField(300, false).masked(true);
   public final FormField addName = new FormField(24, false);
   public final FormField addUrl = new FormField(300, false).masked(true);
   public final FormField addKey = new FormField(300, false).masked(true);
   public final FormField addModel = new FormField(120, false);
   private boolean adding;
   private String addError = "";
   private String armedDelete = "";
   private long armedDeleteAt;
   private final Map<String, BoundsData> hits = new HashMap<>();
   private final Map<String, Long> pressedAt = new HashMap<>();
   private BoundsData sendRect;
   public static final int VIEW_CHATS = 0;
   public static final int VIEW_SETTINGS = 1;
   private int asideView = -1;
   private final ChipStrip asideTabs = new ChipStrip("gui.scripts.ai.view.chats", "gui.scripts.ai.view.settings");
   private String armedChat = "";
   private long armedChatAt;
   private static final SimpleDateFormat CLOCK = new SimpleDateFormat("HH:mm");
   private ShownProvider shownProvider;
   private int seenRevision = -1;
   private int seenMessages;
   private static final float LINE = 7.4F;
   private static final float THUMB = 44.0F;
   private static final float CHIP = 13.0F;
   private static final float CODE_LINE = 6.6F;
   private static final int CODE_PREVIEW = 6;
   private static final float CARD_PAD = 7.0F;
   private static final float BUBBLE_PAD = 5.0F;
   private static final float HEAD_GAP = 4.0F;
   private static final float BUTTON = 12.0F;
   private static final float BUTTON_GAP = 3.0F;
   private final Map<AiChatStorage.Entry2, ScriptAiTab.CachedLayout> layouts = new WeakHashMap<>();
   private static final Pattern HEADING = Pattern.compile("(?m)^#{1,6}\\s*");
   private double lastMouseX;
   private double lastMouseY;
   private static final float CARD = 22.0F;
   private static final float CHAT_CARD = 28.0F;
   private static final float AFTER_TABS = 2.0F;
   private static final ZenithColor LOCAL = new ZenithColor(110, 168, 255);
   private static final float STATUS_DOT = 4.0F;
   private final List<String> pending = new CopyOnWriteArrayList<>();
   private volatile int importing;
   private static final int MAX_ATTACH = 6;
   private static final float STRIP = 30.0F;
   private static final float ATTACH_RADIUS = 4.0F;
   private static final float REMOVE = 8.0F;
   private static final String[][] COMMANDS = new String[][]{
      {"/new", "gui.scripts.ai.cmd.new"},
      {"/fix", "gui.scripts.ai.cmd.fix"},
      {"/explain", "gui.scripts.ai.cmd.explain"},
      {"/shot", "gui.scripts.ai.cmd.shot"},
      {"/model", "gui.scripts.ai.cmd.model"},
      {"/export", "gui.scripts.ai.cmd.export"},
      {"/clear", "gui.scripts.ai.cmd.clear"},
      {"/help", "gui.scripts.ai.cmd.help"}
   };
   private static int shotCountdown = -1;
   private static Screen shotReturn;
   private static Consumer<String> shotDone;
   private static boolean shotHooked;

   private List<FormField> fields() {
      return List.of(this.input, this.keyField, this.modelField, this.endpointField, this.addName, this.addUrl, this.addKey, this.addModel);
   }

   public ScriptAiTab(ScriptsSectionPanel var1) {
      this.panel = var1;
   }

   private static String t(String var0) {
      return SectionPanel.t(var0);
   }

   public int revision() {
      return AiChatStorage.method2741().revision();
   }

   public boolean pollChanged() {
      int var1 = AiChatStorage.method2741().revision();
      if (var1 == this.seenRevision) {
         return false;
      } else {
         this.seenRevision = var1;
         return true;
      }
   }

   private void syncFields() {
      AiSettingsStore var1 = AiSettingsStore.method4467();
      ShownProvider var2 = var1.getShownProvider();
      if (!var2.equals(this.shownProvider)) {
         this.commitFields();
         this.shownProvider = var2;
         this.keyField.set(var1.method4469(var2));
         this.modelField.set(var1.method4472(var2));
         this.endpointField.set(var1.method4474(var2));
      }
   }

   public void commitFields() {
      if (this.shownProvider != null) {
         AiSettingsStore var1 = AiSettingsStore.method4467();
         if (this.keyField.changed()) {
            var1.method4470(this.shownProvider, this.keyField.get());
            this.keyField.commit();
         }

         if (this.modelField.changed()) {
            var1.method4473(this.shownProvider, this.modelField.get());
            this.modelField.commit();
         }

         if (this.endpointField.changed()) {
            var1.method4475(this.shownProvider, this.endpointField.get());
            this.endpointField.commit();
         }
      }
   }

   public void build(Storage var1) {
      this.syncFields();
      AiChatStorage var2 = AiChatStorage.method2741();
      var var3 = new ArrayList<>(var2.method2745());
      if (var3.isEmpty()) {
         this.welcome(var1);
      } else {
         float var4 = 236.0F;

         for (int var5 = 0; var5 < var3.size(); var5++) {
            AiChatStorage.Entry2 var6 = (AiChatStorage.Entry2)var3.get(var5);
            boolean var7 = var5 == var3.size() - 1;
            ScriptAiTab.Layout var8 = this.layout(var6, var4, var7);
            int var9 = var5;
            this.panel
               .custom(
                  var8.height + 8.0F,
                  (var6x, var7x, var8x, var9x, var10, var11, var12, var13) -> this.renderMessage(
                     var6x, var7x, var8x, var9x, var10, var11, var12, var13, var1, var6, var8, var9, var7
                  ),
                  () -> this.clickMessage(var1, var6, var9),
                  null
               );
         }

         this.quickReplies(var1, (AiChatStorage.Entry2)var3.get(var3.size() - 1));
         this.panel.gap(4.0F);
      }

      this.aside(var1);
      if (var3.size() != this.seenMessages || var2.isBusy()) {
         this.seenMessages = var3.size();
         this.panel.scrollToEnd();
      }
   }

   private List<String> suggestions(AiChatStorage.Entry2 var1) {
      if (var1.isFlag() || var1.streaming() || AiChatStorage.method2741().isBusy()) {
         return List.of();
      } else if (!var1.error().isEmpty()) {
         return List.of();
      } else if (var1.isFlag3()) {
         return List.of("gui.scripts.ai.quick.fix", "gui.scripts.ai.quick.simpler");
      } else {
         return !var1.method2775().isEmpty()
            ? List.of("gui.scripts.ai.quick.settings", "gui.scripts.ai.quick.prettier", "gui.scripts.ai.quick.explain", "gui.scripts.ai.quick.publish")
            : List.of("gui.scripts.ai.quick.doit", "gui.scripts.ai.quick.example");
      }
   }

   private void quickReplies(Storage var1, AiChatStorage.Entry2 var2) {
      List<String> var3 = this.suggestions(var2);
      if (!var3.isEmpty()) {
         Font var4 = Fonts.NEW_MEDIUM.getFont(4.5F);
         float var5 = 220.0F;
         ArrayList var6 = new ArrayList();
         float var7 = 0.0F;
         float var8 = 0.0F;

         for (Object var10_o : (Iterable<?>)(Object)(var3)) {
            String var10 = (String)var10_o;
            float var11 = var4.width(t(var10)) + 14.0F;
            if (var7 > 0.0F && var7 + var11 > var5) {
               var7 = 0.0F;
               var8 += 15.0F;
            }

            var6.add(new float[]{var7, var8, var11});
            var7 += var11 + 4.0F;
         }

         long var12 = System.currentTimeMillis();
         this.panel
            .custom(
               var8 + 16.0F,
               (var6x, var7x, var8x, var9, var10x, var11x, var12x, var13) -> {
                  ZenithColor var14 = var13.getPrimaryColor().getZenithColor();

                  for (int var15 = 0; var15 < var3.size(); var15++) {
                     float[] var16 = (float[])var6.get(var15);
                     float var17 = Math.min(1.0F, Math.max(0.0F, (float)(System.currentTimeMillis() - var12 - var15 * 60L) / 260.0F));
                     float var18 = 1.0F - (1.0F - var17) * (1.0F - var17) * (1.0F - var17);
                     BoundsData var19 = new BoundsData(var9 + 8.0F + var16[0], var10x + 1.0F + var16[1] + (1.0F - var18) * 5.0F, var16[2], 12.0F);
                     this.hits.put("quick:" + var15, var19);
                     boolean var20 = var19.contains(var7x, var8x);
                     float var21 = this.panel.hot("quick:" + (String)var3.get(var15), var20);
                     var6x.drawRoundedRect(
                        var19.getFloatValue(),
                        var19.getFloatValue2(),
                        var19.width(),
                        12.0F,
                        ProxyDotRadiusData.method8069(6.0F),
                        var14.method7292(0.08F + 0.14F * var21).method7294(var12x * var18)
                     );
                     var6x.drawRoundedBorder(
                        var19.getFloatValue(),
                        var19.getFloatValue2(),
                        var19.width(),
                        12.0F,
                        0.2F,
                        ProxyDotRadiusData.method8069(6.0F),
                        var14.method7292(0.3F + 0.4F * var21).method7294(var12x * var18)
                     );
                     var6x.drawText(
                        var4,
                        t((String)var3.get(var15)),
                        var19.getFloatValue() + 7.0F,
                        var19.getFloatValue2() + (12.0F - var4.height()) / 2.0F,
                        var14.method7295(ZenithColor.INSTANCE, 0.35F + 0.4F * var21).method7294(var12x * var18)
                     );
                  }
               },
               () -> {
                  for (int var3x = 0; var3x < var3.size(); var3x++) {
                     if (this.hovered(this.hits.get("quick:" + var3x))) {
                        AiChatStorage.method2741().method2757(var1, t((String)var3.get(var3x) + ".prompt"));
                        this.panel.invalidate();
                        return;
                     }
                  }
               },
               null
            );
      }
   }

   private void welcome(Storage var1) {
      this.panel
         .custom(
            84.0F,
            this.panel
               .entering(
                  0,
                  (var1x, var2x, var3x, var4x, var5, var6, var7x, var8) -> {
                     Font var9 = Fonts.NEW_SEMIBOLD.getFont(7.0F);
                     Font var10 = Fonts.NEW_REGULAR.getFont(4.5F);
                     ZenithColor var11 = var8.getPrimaryColor().getZenithColor();
                     float var12 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 700.0);
                     float var13 = var4x + var6 / 2.0F;
                     var1x.drawRoundedRect(
                        var13 - 14.0F,
                        var5 + 10.0F,
                        28.0F,
                        28.0F,
                        ProxyDotRadiusData.method8069(9.0F),
                        var11.method7292(0.12F + 0.08F * var12).method7294(var7x)
                     );
                     Font var14 = Fonts.NEW_SEMIBOLD.getFont(10.0F);
                     var1x.drawText(var14, "AI", var13 - var14.width("AI") / 2.0F, var5 + 24.0F - var14.height() / 2.0F, var11.method7294(var7x));
                     String var15 = t("gui.scripts.ai.welcome");
                     var1x.drawText(var9, var15, var13 - var9.width(var15) / 2.0F, var5 + 44.0F, var8.getTextEnable().getZenithColor().method7294(var7x));
                     List<String> var16 = SectionPanel.wrapText(this.readyHint(), var10, var6 - 48.0F, 3);
                     float var17 = var5 + 47.0F + var9.height();

                     for (Object var19_o : (Iterable<?>)(Object)(var16)) {
                        String var19 = (String)var19_o;
                        var1x.drawText(var10, var19, var13 - var10.width(var19) / 2.0F, var17, var8.getTextTertiary().getZenithColor().method7294(var7x));
                        var17 += var10.height() + 2.0F;
                     }
                  }
               )
         );
      int var2 = 1;
      var var3 = AiChatStorage.method2741().method2744();
      if (!var3.isEmpty() && var3.get(0) != AiChatStorage.method2741().getEntry()) {
         AiChatStorage.Entry var4 = (AiChatStorage.Entry)var3.get(0);
         this.panel
            .custom(
               26.0F,
               this.panel
                  .entering(
                     var2++,
                     (var2x, var3x, var4x, var5, var6, var7x, var8, var9) -> {
                        BoundsData var10 = new BoundsData(var5 + 24.0F, var6 + 1.0F, var7x - 48.0F, 22.0F);
                        boolean var11 = var10.contains(var3x, var4x);
                        float var12 = this.panel.hot("continue", var11);
                        ZenithColor var13 = var9.getPrimaryColor().getZenithColor();
                        var2x.drawRoundedRect(
                           var10.getFloatValue(),
                           var10.getFloatValue2(),
                           var10.width(),
                           var10.height(),
                           ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
                           var13.method7292(0.1F + 0.1F * var12).method7294(var8)
                        );
                        var2x.drawRoundedBorder(
                           var10.getFloatValue(),
                           var10.getFloatValue2(),
                           var10.width(),
                           var10.height(),
                           0.2F,
                           ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
                           var13.method7292(0.35F + 0.3F * var12).method7294(var8)
                        );
                        Font var14 = Fonts.NEW_MEDIUM.getFont(4.3F);
                        Font var15 = Fonts.NEW_MEDIUM.getFont(4.6F);
                        var2x.drawText(
                           var14,
                           t("gui.scripts.ai.continue"),
                           var10.getFloatValue() + 8.0F + var12 * 2.0F,
                           var10.getFloatValue2() + 4.0F,
                           var13.method7294(var8)
                        );
                        var2x.drawText(
                           var15,
                           var15.clip(var4.title(), var10.width() - 50.0F),
                           var10.getFloatValue() + 8.0F + var12 * 2.0F,
                           var10.getFloatValue2() + 6.0F + var14.height(),
                           var9.getTextEnable().getZenithColor().method7294(var8)
                        );
                        String var16 = ScriptsSectionPanel.ago(var4.getLongValue2());
                        var2x.drawText(
                           var14,
                           var16,
                           var10.getFloatValue() + var10.width() - 8.0F - var14.width(var16),
                           var10.getFloatValue2() + (var10.height() - var14.height()) / 2.0F,
                           var9.getTextTertiary().getZenithColor().method7294(var8)
                        );
                     }
                  ),
               () -> {
                  AiChatStorage.method2741().method2749(var4);
                  this.panel.invalidate();
               },
               null
            );
      }

      for (String var7 : IDEAS) {
         this.panel
            .custom(
               20.0F,
               this.panel
                  .entering(
                     var2++,
                     (var2x, var3x, var4x, var5, var6, var7x, var8, var9) -> {
                        BoundsData var10 = new BoundsData(var5 + 24.0F, var6 + 1.0F, var7x - 48.0F, 17.0F);
                        boolean var11 = var10.contains(var3x, var4x);
                        float var12 = this.panel.hot("idea:" + var7, var11);
                        ScriptsSectionPanel.card(var2x, var9, var10.getFloatValue(), var10.getFloatValue2(), var10.width(), var10.height(), var12, var8);
                        Font var13 = Fonts.NEW_REGULAR.getFont(4.5F);
                        ZenithColor var14 = var9.getPrimaryColor().getZenithColor();
                        var2x.drawText(
                           var13,
                           "›",
                           var10.getFloatValue() + 7.0F + var12 * 2.0F,
                           var10.getFloatValue2() + (var10.height() - var13.height()) / 2.0F,
                           var14.method7294(var8)
                        );
                        var2x.drawText(
                           var13,
                           var13.clip(t(var7), var10.width() - 24.0F),
                           var10.getFloatValue() + 15.0F + var12 * 2.0F,
                           var10.getFloatValue2() + (var10.height() - var13.height()) / 2.0F,
                           var9.getTextSecondary().getZenithColor().method7295(var9.getTextEnable().getZenithColor(), var12).method7294(var8)
                        );
                     }
                  ),
               () -> {
                  this.input.set(t(var7));
                  this.input.setFocused(true);
               },
               null
            );
      }
   }

   private String readyHint() {
      AiSettingsStore var1 = AiSettingsStore.method4467();
      ShownProvider var2 = var1.getShownProvider();
      return !var1.method4471(var2) ? t("gui.scripts.ai.welcome.nokey").replace("{p}", var2.title()) : t("gui.scripts.ai.welcome.desc");
   }

   private static Font headFont() {
      return Fonts.NEW_MEDIUM.getFont(4.3F);
   }

   private ScriptAiTab.Layout layout(AiChatStorage.Entry2 var1, float var2, boolean var3) {
      String var4 = var1.text();
      String var5 = !var1.isFlag() && !var1.error().isEmpty() ? this.errorText(var1) : "";
      ScriptAiTab.CachedLayout var6 = this.layouts.get(var1);
      if (var6 != null && var6.matches(var1, var4, var5, var2, var3)) {
         return var6.layout();
      } else {
         ScriptAiTab.Layout var7 = this.computeLayout(var1, var2, var3);
         this.layouts
            .put(var1, new ScriptAiTab.CachedLayout(var4, var1.streaming(), var1.note(), var5, var1.result(), var1.getItems().size(), var2, var3, var7));
         return var7;
      }
   }

   private ScriptAiTab.Layout computeLayout(AiChatStorage.Entry2 var1, float var2, boolean var3) {
      Font var4 = Fonts.NEW_REGULAR.getFont(4.6F);
      boolean var5 = !var1.isFlag() && !var1.note();
      float var6 = var5 ? 7.0F : 5.0F;
      float var7 = var1.isFlag() ? Math.min(var2 * 0.78F, var2 - 16.0F) : var2 - 16.0F;
      ArrayList<ScriptAiTab.Piece> var8 = new ArrayList();
      String var9 = var1.text();
      Matcher var10 = BLOCK.matcher(var9);

      int var11;
      for (var11 = 0; var10.find(); var11 = var10.end()) {
         if (var10.start() > var11) {
            var8.add(new ScriptAiTab.Piece(0, var9.substring(var11, var10.start()), "", 0));
         }

         String var12 = var10.group(1).trim();
         String var13 = var10.group(2);
         String var14 = var13.stripTrailing();
         int var15 = var14.isEmpty() ? 0 : var14.split("\n", -1).length;
         Matcher var16 = FILE.matcher(var12);
         if (var16.find()) {
            var8.add(new ScriptAiTab.Piece(1, var16.group(1).trim(), var10.group(3).isEmpty() ? "…" : "", var15));
         } else {
            var8.add(new ScriptAiTab.Piece(2, var13, var12, var15));
         }
      }

      if (var11 < var9.length()) {
         var8.add(new ScriptAiTab.Piece(0, var9.substring(var11), "", 0));
      }

      ArrayList var23 = new ArrayList();
      float var24 = Math.max(0.0F, 7.4F - var4.height());
      float var25 = var6;
      float var26 = 0.0F;
      if (!var1.getItems().isEmpty()) {
         var25 = var6 + 48.0F;
         var26 = 4.0F;
      }

      if (var5) {
         var25 += headFont().height() + 4.0F;
         var26 = 4.0F;
      }

      for (Object var17_o : (Iterable<?>)(Object)(var8)) {
         ScriptAiTab.Piece var17 = (ScriptAiTab.Piece)var17_o;
         if (var17.kind == 0) {
            ArrayList var28 = new ArrayList();

            for (String var22 : clean(var17.text).split("\n")) {
               if (var22.isBlank()) {
                  if (!var28.isEmpty() && !((String)var28.get(var28.size() - 1)).isEmpty()) {
                     var28.add("");
                  }
               } else {
                  var28.addAll(SectionPanel.wrapText(var22, var4, var7 - 14.0F, 400));
               }
            }

            while (!var28.isEmpty() && ((String)var28.get(var28.size() - 1)).isEmpty()) {
               var28.remove(var28.size() - 1);
            }

            var23.add(var28);
            if (!var28.isEmpty()) {
               var25 += var28.size() * 7.4F;
               var26 = var24;
            }
         } else if (var17.kind == 1) {
            var23.add(List.of());
            var25 += 16.0F;
            var26 = 3.0F;
         } else {
            ArrayList var18 = new ArrayList();
            String[] var19 = var17.text.split("\n");

            for (int var20 = 0; var20 < Math.min(6, var19.length); var20++) {
               var18.add(var19[var20].replace("\t", "  "));
            }

            var23.add(var18);
            var25 += codeHeight(var18.size(), var17.lines) + 2.0F;
            var26 = 2.0F;
         }
      }

      if (var9.isEmpty() && var1.streaming()) {
         var25 += 7.4F;
         var26 = var24;
      }

      if (var5) {
         if (!var1.error().isEmpty()) {
            var25 += 2.0F + SectionPanel.wrapText(this.errorText(var1), var4, var7 - 14.0F, 6).size() * 7.4F;
            var26 = var24;
         }

         if (!var1.result().isEmpty()) {
            var25 += 2.0F + SectionPanel.wrapText(var1.result(), var4, var7 - 14.0F, 6).size() * 7.4F;
            var26 = var24;
         }

         if (!var1.streaming() && hasButtons(var1, var3)) {
            var25 += 15.0F;
            var26 = 0.0F;
         }
      }

      return new ScriptAiTab.Layout(var8, var23, var25 - var26 + var6, var7);
   }

   private static float codeHeight(int var0, int var1) {
      return var0 * 6.6F + 6.0F + (var1 > 6 ? 6.6F : 0.0F);
   }

   private String extractCode(AiChatStorage.Entry2 var1, int var2) {
      Matcher var3 = BLOCK.matcher(var1.text());
      int var4 = 0;
      int var5 = 0;

      for (int var6 = 0; var3.find(); var4++) {
         if (var3.start() > var5) {
            var6++;
         }

         if (var6 == var2) {
            return var3.group(2);
         }

         var6++;
         var5 = var3.end();
      }

      return var4 == 0 ? var1.text() : "";
   }

   private static String clean(String var0) {
      return HEADING.matcher(var0.replace("**", "").replace("__", "")).replaceAll("").replace("`", "");
   }

   private static boolean hasButtons(AiChatStorage.Entry2 var0, boolean var1) {
      return !var0.method2775().isEmpty() || !var0.error().isEmpty() || var0.isFlag3() || !var0.text().isBlank() || var1;
   }

   private String errorText(AiChatStorage.Entry2 var1) {
      return var1.error().equals("nokey")
         ? t("gui.scripts.ai.nokey.msg").replace("{p}", AiSettingsStore.method4467().getShownProvider().title())
         : var1.error();
   }

   private void renderMessage(
      MenuDrawContext var1,
      int var2,
      int var3,
      float var4,
      float var5,
      float var6,
      float var7,
      ZenithStyle var8,
      Storage var9,
      AiChatStorage.Entry2 var10,
      ScriptAiTab.Layout var11,
      int var12,
      boolean var13
   ) {
      Font var14 = Fonts.NEW_REGULAR.getFont(4.6F);
      Font var15 = headFont();
      Font var16 = Fonts.NEW_REGULAR.getFont(4.3F);
      ZenithColor var17 = var8.getPrimaryColor().getZenithColor();
      float var18 = this.panel.hot("msg:" + var12 + ":" + var10.getLongValue(), true);
      float var19 = (1.0F - var18) * 6.0F;
      var7 *= var18;
      boolean var20 = !var10.isFlag() && !var10.note();
      float var21 = var10.isFlag() ? var4 + var6 - 8.0F - var11.bubble : var4 + 8.0F;
      float var22 = var5 + 3.0F + var19;
      float var23 = var11.height;
      ProxyDotRadiusData var24 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.8F);
      if (var10.note()) {
         var1.drawRoundedRect(var21, var22, var11.bubble, var23, var24, var8.getDisableActiveBg().getZenithColor().method7294(var7 * 0.35F));
         var1.drawRoundedRect(var21, var22 + 4.0F, 2.0F, var23 - 8.0F, ProxyDotRadiusData.method8069(1.0F), var17.method7294(var7 * 0.6F));
      } else if (var10.isFlag()) {
         var1.drawRoundedRect(var21, var22, var11.bubble, var23, var24, var17.method7292(var10.isFlag5() ? 0.08F : 0.16F).method7294(var7));
         Font var25 = Fonts.NEW_REGULAR.getFont(4.2F);
         String var26 = CLOCK.format(new Date(var10.getLongValue()));
         boolean var27 = new BoundsData(var21 - 30.0F, var22, var11.bubble + 30.0F, var23).contains(var2, var3);
         float var28 = this.panel.hot("time:" + var10.getLongValue(), var27);
         var1.drawText(
            var25,
            var26,
            var21 - 5.0F - var25.width(var26),
            var22 + (var23 - var25.height()) / 2.0F,
            var8.getTextTertiary().getZenithColor().method7294(var7 * (0.35F + 0.65F * var28))
         );
      } else {
         ScriptsSectionPanel.card(var1, var8, var21, var22, var11.bubble, var23, 0.0F, var7);
      }

      float var35 = var22 + (var20 ? 7.0F : 5.0F);
      if (!var10.getItems().isEmpty()) {
         float var36 = var21 + 7.0F;

         for (int var39 = 0; var39 < var10.getItems().size(); var39++) {
            String var45 = var10.getItems().get(var39);
            Identifier var29 = AiImageStore.method4639(var45);
            int[] var30 = AiImageStore.method4638(var45);
            float var31 = Math.max(26.400002F, Math.min(96.8F, 44.0F * var30[0] / Math.max(1, var30[1])));
            if (var36 + var31 > var21 + var11.bubble - 7.0F) {
               break;
            }

            BoundsData var32 = new BoundsData(var36, var35, var31, 44.0F);
            this.hits.put("img:" + var39 + ":" + var12, var32);
            float var33 = this.panel.hot("img:" + var45, var32.contains(var2, var3));
            thumb(var1, var29, var32, 5.0F, ZenithColor.INSTANCE.method7294(var7 * (0.15F + 0.5F * var33)), var7, 1.0F);
            if (var32.contains(var2, var3)) {
               HoverInfo.request(t("gui.scripts.ai.image"), t("gui.scripts.ai.image.desc"));
            }

            var36 += var31 + 4.0F;
         }

         var35 += 48.0F;
      }

      if (var20) {
         String var37 = var10.getText2().isEmpty() ? "AI" : var10.getText2();
         var1.drawText(var15, var15.clip(var37, var11.bubble - 60.0F), var21 + 7.0F, var35, var17.method7294(var7 * 0.9F));
         String var40 = CLOCK.format(new Date(var10.getLongValue()));
         var1.drawText(var15, var40, var21 + var11.bubble - 7.0F - var15.width(var40), var35, var8.getTextTertiary().getZenithColor().method7294(var7 * 0.8F));
         if (var10.streaming()) {
            long var46 = System.currentTimeMillis();

            for (int var54 = 0; var54 < 3; var54++) {
               float var58 = (float)Math.sin(var46 / 180.0 - var54 * 0.9);
               var1.drawRoundedRect(
                  var21 + 10.0F + var15.width(var15.clip(var37, var11.bubble - 60.0F)) + var54 * 4.0F,
                  var35 + (var15.height() - 2.0F) / 2.0F - Math.max(0.0F, var58) * 1.2F,
                  2.0F,
                  2.0F,
                  ProxyDotRadiusData.method8069(1.0F),
                  var17.method7294(var7 * (0.4F + 0.6F * Math.max(0.0F, var58)))
               );
            }
         }

         var35 += var15.height() + 4.0F;
      }

      ZenithColor var38 = var10.isFlag()
         ? var8.getTextEnable().getZenithColor()
         : var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), 0.5F);

      for (int var41 = 0; var41 < var11.pieces.size(); var41++) {
         ScriptAiTab.Piece var47 = var11.pieces.get(var41);
         List<String> var51 = var11.wrapped.get(var41);
         if (var47.kind == 0) {
            for (Object var61_o : (Iterable<?>)(Object)(var51)) {
               String var61 = (String)var61_o;
               var1.drawText(var14, var61, var21 + 7.0F, var35, var38.method7294(var7));
               var35 += 7.4F;
            }
         } else if (var47.kind == 1) {
            String var56 = var47.text + "  ·  " + var47.lines + " " + t("gui.scripts.ai.lines") + var47.extra;
            float var60 = Math.min(var11.bubble - 14.0F, var15.width(var56) + 22.0F);
            BoundsData var64 = new BoundsData(var21 + 7.0F, var35, var60, 13.0F);
            this.hits.put("file:" + var41 + ":" + var12, var64);
            float var66 = this.panel.hot("chip:" + var12 + ":" + var41, var64.contains(var2, var3));
            var1.drawRoundedRect(
               var21 + 7.0F, var35, var60, 13.0F, ProxyDotRadiusData.method8069(4.0F), var17.method7292(0.12F + 0.12F * var66).method7294(var7)
            );
            if (var64.contains(var2, var3)) {
               HoverInfo.request(var47.text, t("gui.scripts.ai.chip.desc"));
            }

            var1.drawRoundedRect(var21 + 11.0F, var35 + 3.5F, 5.0F, 6.0F, ProxyDotRadiusData.method8069(1.0F), var17.method7292(0.6F).method7294(var7));
            var1.drawText(
               var15,
               var15.clip(var56, var60 - 22.0F),
               var21 + 20.0F,
               var35 + (13.0F - var15.height()) / 2.0F,
               var17.method7295(ZenithColor.INSTANCE, 0.3F).method7294(var7)
            );
            var35 += 16.0F;
         } else {
            float var55 = codeHeight(var51.size(), var47.lines);
            var1.drawRoundedRect(
               var21 + 7.0F, var35, var11.bubble - 14.0F, var55, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(10, 11, 16, 140).method7294(var7)
            );
            float var59 = var35 + 3.0F;

            for (Object var65_o : (Iterable<?>)(Object)(var51)) {
               String var65 = (String)var65_o;
               var1.drawText(
                  var16, var16.clip(var65, var11.bubble - 24.0F), var21 + 11.0F, var59, var8.getTextSecondary().getZenithColor().method7294(var7 * 0.9F)
               );
               var59 += 6.6F;
            }

            if (var47.lines > 6) {
               var1.drawText(var16, "… +" + (var47.lines - 6), var21 + 11.0F, var59, var8.getTextTertiary().getZenithColor().method7294(var7));
            }

            var35 += var55 + 2.0F;
         }
      }

      if (var10.text().isEmpty() && var10.streaming()) {
         var1.drawText(
            var14,
            t("gui.scripts.ai.thinking"),
            var21 + 7.0F,
            var35,
            var8.getTextTertiary().getZenithColor().method7294(var7 * (0.5F + 0.5F * (float)Math.abs(Math.sin(System.currentTimeMillis() / 400.0))))
         );
         var35 += 7.4F;
      }

      if (var20) {
         if (!var10.error().isEmpty()) {
            var35 += 2.0F;
            boolean var42 = var10.error().equals("nokey");

            for (String var52 : SectionPanel.wrapText(this.errorText(var10), var14, var11.bubble - 14.0F, 6)) {
               var1.drawText(var14, var52, var21 + 7.0F, var35, (var42 ? WARN : ERROR).method7294(var7));
               var35 += 7.4F;
            }
         }

         if (!var10.result().isEmpty()) {
            var35 += 2.0F;

            for (String var49 : SectionPanel.wrapText(var10.result(), var14, var11.bubble - 14.0F, 6)) {
               var1.drawText(var14, var49, var21 + 7.0F, var35, (var10.isFlag3() ? ERROR : OK).method7294(var7));
               var35 += 7.4F;
            }
         }

         if (!var10.streaming() && hasButtons(var10, var13)) {
            float var44 = var21 + 7.0F;
            float var50 = var35 + 3.0F;
            if (!var10.method2775().isEmpty()) {
               var44 = this.button(
                  var1,
                  var2,
                  var3,
                  var8,
                  "apply:" + var12,
                  var10.isFlag4() ? t("gui.scripts.ai.reapply") : t("gui.scripts.ai.apply").replace("{n}", String.valueOf(var10.method2775().size())),
                  var44,
                  var50,
                  true,
                  var7
               );
               if (var10.isFlag4()) {
                  var44 = this.button(var1, var2, var3, var8, "ide:" + var12, t("gui.scripts.ai.openIde"), var44, var50, false, var7);
               }
            }

            if (var10.error().equals("nokey")) {
               var44 = this.button(var1, var2, var3, var8, "key:" + var12, t("gui.scripts.ai.nokey.button"), var44, var50, true, var7);
            } else if (!var10.error().isEmpty()) {
               var44 = this.button(var1, var2, var3, var8, "retry:" + var12, t("gui.scripts.ai.retry"), var44, var50, true, var7);
            }

            if (var10.isFlag3()) {
               var44 = this.button(var1, var2, var3, var8, "fix:" + var12, t("gui.scripts.ai.fix"), var44, var50, false, var7);
            }

            if (!var10.text().isBlank()) {
               long var53 = this.pressedAt.getOrDefault("copy:" + var12, 0L);
               boolean var62 = System.currentTimeMillis() - var53 < 1500L;
               var44 = this.button(
                  var1, var2, var3, var8, "copy:" + var12, t(var62 ? "gui.scripts.ai.copied" : "gui.scripts.ai.copy"), var44, var50, false, var7
               );
            }

            if (var13 && !AiChatStorage.method2741().isBusy()) {
               this.button(var1, var2, var3, var8, "again:" + var12, t("gui.scripts.ai.again"), var44, var50, false, var7);
            }
         }
      }
   }

   private float button(MenuDrawContext var1, int var2, int var3, ZenithStyle var4, String var5, String var6, float var7, float var8, boolean var9, float var10) {
      Font var11 = Fonts.NEW_MEDIUM.getFont(4.5F);
      float var12 = var11.width(var6) + 12.0F;
      BoundsData var13 = new BoundsData(var7, var8, var12, 12.0F);
      this.hits.put(var5, var13);
      boolean var14 = var13.contains(var2, var3);
      float var15 = this.panel.hot("aib:" + var5, var14);
      float var16 = Smooth.press(this.pressedAt.getOrDefault(var5, 0L), 320L);
      ZenithColor var17 = var4.getPrimaryColor().getZenithColor();
      ZenithColor var18 = var9
         ? var17.method7292(0.2F + 0.18F * var15)
         : var4.getFieldSurfaceBackground().getZenithColor().method7295(var4.getDisableActiveBg().getZenithColor(), 0.4F + var15 * 0.5F);
      var1.drawRoundedRect(var7, var8, var12, 12.0F, ProxyDotRadiusData.method8069(6.0F), var18.method7294(var10));
      if (var16 > 0.01F) {
         float var19 = (1.0F - var16) * 3.0F;
         var1.drawRoundedRect(
            var7 - var19 / 2.0F,
            var8 - var19 / 2.0F,
            var12 + var19,
            12.0F + var19,
            ProxyDotRadiusData.method8069(6.0F + var19 / 2.0F),
            var17.method7292(0.3F * var16).method7294(var10)
         );
      }

      var1.drawText(
         var11,
         var6,
         var7 + 6.0F,
         var8 + (12.0F - var11.height()) / 2.0F,
         (var9
               ? var17.method7295(ZenithColor.INSTANCE, 0.3F + var15 * 0.4F)
               : var4.getTextSecondary().getZenithColor().method7295(var4.getTextEnable().getZenithColor(), var15))
            .method7294(var10)
      );
      return var7 + var12 + 4.0F;
   }

   private void clickMessage(Storage var1, AiChatStorage.Entry2 var2, int var3) {
      AiChatStorage var4 = AiChatStorage.method2741();

      for (Entry var6 : this.hits.entrySet()) {
         if (((String)var6.getKey()).endsWith(":" + var3) && this.hovered((BoundsData)var6.getValue())) {
            this.pressedAt.put((String)var6.getKey(), System.currentTimeMillis());
            String var7 = ((String)var6.getKey()).substring(0, ((String)var6.getKey()).indexOf(58));
            label96:
            switch (var7) {
               case "apply":
                  var4.method2767(var1, var2);
                  break;
               case "ide":
                  Script var16 = var4.method2747() == null ? null : var1.method1294(var4.method2747());
                  if (var16 != null) {
                     this.panel.openWorkspace(var16);
                  }
                  break;
               case "retry":
                  var var15 = var4.method2745();

                  for (int var17 = var15.indexOf(var2) - 1; var17 >= 0; var17--) {
                     if (((AiChatStorage.Entry2)var15.get(var17)).isFlag()) {
                        var4.method2757(var1, ((AiChatStorage.Entry2)var15.get(var17)).text());
                        break label96;
                     }
                  }
                  break;
               case "fix":
                  var4.method2769(var1);
                  break;
               case "copy":
                  MinecraftClient.getInstance().keyboard.setClipboard(var2.text());
                  break;
               case "img":
                  int var14 = Integer.parseInt(((String)var6.getKey()).split(":")[1]);
                  if (var14 < var2.getItems().size()) {
                     Util.getOperatingSystem().open(AiImageStore.method4637(var2.getItems().get(var14)).toFile());
                  }
                  break;
               case "file":
                  int var10 = Integer.parseInt(((String)var6.getKey()).split(":")[1]);
                  ScriptAiTab.CachedLayout var11 = this.layouts.get(var2);
                  ScriptAiTab.Layout var12 = var11 != null ? var11.layout() : this.layout(var2, 236.0F, false);
                  Script var13 = var4.method2747() == null ? null : var1.method1294(var4.method2747());
                  if (var10 < var12.pieces.size() && var13 != null && var2.isFlag4()) {
                     this.panel.openWorkspaceFile(var13, var12.pieces.get(var10).text);
                  } else {
                     MinecraftClient.getInstance().keyboard.setClipboard(this.extractCode(var2, var10));
                     HudHolder.method6969().method6973("i", Text.literal(t("gui.scripts.ai.chip.copied")), 2000L);
                  }
                  break;
               case "again":
                  var4.method2761(var1);
                  break;
               case "key":
                  this.asideView = 1;
                  this.focus(this.keyField);
            }

            this.panel.invalidate();
            return;
         }
      }
   }

   public void mouse(double var1, double var3) {
      this.lastMouseX = var1;
      this.lastMouseY = var3;
   }

   private boolean hovered(BoundsData var1) {
      return var1 != null && var1.contains(this.lastMouseX, this.lastMouseY);
   }

   private int view() {
      return this.asideView >= 0 ? this.asideView : (AiSettingsStore.method4467().method4471(AiSettingsStore.method4467().getShownProvider()) ? 0 : 1);
   }

   private void aside(Storage var1) {
      AiSettingsStore var2 = AiSettingsStore.method4467();
      ShownProvider var3 = var2.getShownProvider();
      int var4 = this.view();
      this.panel
         .asideCustom(
            11.0F + GuiStyle.PADDING.intValue() * 2.0F,
            (var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8) -> this.asideTabs
               .render(var1x, var2x, var3x, var4x + 8.0F, var5x + GuiStyle.PADDING.intValue(), var6x - 16.0F, this.view(), var7x, var8),
            () -> {
               int var1x = this.asideTabs.hovered();
               if (var1x >= 0 && var1x != this.view()) {
                  this.commitFields();
                  this.asideView = var1x;
                  this.panel.resetAsideScroll();
                  this.panel.replayAside();
                  this.panel.invalidate();
               }
            },
            null
         );
      if (var4 == 0) {
         this.chatsAside(var1);
      } else {
         this.leadCaption("gui.scripts.ai.caption.provider");
         this.providerGrid();
         if (this.adding) {
            this.addForm();
         } else {
            this.panel.asideCaption("gui.scripts.ai.caption.key");
            if (!var3.isFlag() && !var3.isFlag2()) {
               this.panel.paragraph(t("gui.scripts.ai.key.local"), true);
            } else {
               this.field(this.keyField, t("gui.scripts.ai.key.hint").replace("{p}", var3.title()), "paste", t("gui.scripts.ai.key.paste"));
               Font var5 = Fonts.NEW_REGULAR.getFont(4.5F);
               float var6 = 122.0F;
               int var7 = Math.max(
                  SectionPanel.wrapText(t("gui.scripts.ai.key.none"), var5, var6, 2).size(),
                  SectionPanel.wrapText(t("gui.scripts.ai.key.saved").replace("{k}", AiSettingsStore.method4480("sk-ant-0000000000")), var5, var6, 2).size()
               );
               this.panel
                  .asideCustom(
                     var7 * (var5.height() + 2.0F) + 4.0F,
                     (var3x, var4x, var5x, var6x, var7x, var8, var9, var10) -> {
                        String var11 = AiSettingsStore.method4467().method4469(AiSettingsStore.method4467().getShownProvider());
                        boolean var12 = !var11.isEmpty();
                        float var13 = var12 ? this.panel.hot("keyok:" + AiSettingsStore.method4467().getShownProvider().getText(), true) : 0.0F;
                        String var14 = var12 ? t("gui.scripts.ai.key.saved").replace("{k}", AiSettingsStore.method4480(var11)) : t("gui.scripts.ai.key.none");
                        float var15 = var7x + 2.0F;
                        if (var12) {
                           var3x.drawRoundedRect(
                              var6x + 8.0F + 1.0F,
                              var15 + (var5.height() - 3.0F) / 2.0F,
                              3.0F,
                              3.0F,
                              ProxyDotRadiusData.method8069(1.5F),
                              OK.method7294(var9 * var13)
                           );
                        }

                        ZenithColor var16 = (var12 ? OK.method7295(var10.getTextTertiary().getZenithColor(), 0.35F) : var10.getTextTertiary().getZenithColor())
                           .method7294(var9 * (var12 ? var13 : 1.0F));

                        for (String var18 : SectionPanel.wrapText(var14, var5, var8 - 16.0F - 6.0F, var7)) {
                           var3x.drawText(var5, var18, var6x + 8.0F + (var12 ? 6.0F : 0.0F), var15, var16);
                           var15 += var5.height() + 2.0F;
                        }
                     }
                  );
               if (!var3.isFlag2()) {
                  this.panel
                     .asideAction(
                        "gui.scripts.ai.key.where",
                        MenuGlyph.NETWORK,
                        () -> "",
                        () -> Util.getOperatingSystem().open(var3.getText5()),
                        "gui.scripts.ai.key.where.desc"
                     );
               }
            }

            this.panel.asideCaption("gui.scripts.ai.caption.model");
            this.field(this.modelField, t("gui.scripts.ai.model.hint"), null, null);
            this.modelChips(var3);
            if (var3.equals(ShownProvider.INSTANCE9) || var3.isFlag2()) {
               this.field(this.endpointField, t("gui.scripts.ai.endpoint.hint"), null, null);
            }

            if (var3.isFlag2()) {
               this.panel
                  .asideAction(
                     "gui.scripts.ai.custom.remove",
                     MenuGlyph.CLOSE,
                     () -> this.armedDelete.equals(var3.getText()) && System.currentTimeMillis() - this.armedDeleteAt < 3000L
                        ? t("gui.scripts.market.again")
                        : "",
                     () -> {
                        if (this.armedDelete.equals(var3.getText()) && System.currentTimeMillis() - this.armedDeleteAt < 3000L) {
                           this.armedDelete = "";
                           AiSettingsStore.method4467().method4477(var3);
                           this.syncFields();
                        } else {
                           this.armedDelete = var3.getText();
                           this.armedDeleteAt = System.currentTimeMillis();
                        }

                        this.panel.invalidate();
                     },
                     "gui.scripts.ai.custom.remove.desc"
                  );
            }

            this.panel.asideCaption("gui.scripts.ai.caption.behaviour");
            this.toggle("autoApply", "gui.scripts.ai.autoApply", "gui.scripts.ai.autoApply.desc", var2::isFlag2, var2::method4478);
            this.toggle("autoFix", "gui.scripts.ai.autoFix", "gui.scripts.ai.autoFix.desc", var2::isFlag3, var2::method4479);
            this.panel.paragraph(t("gui.scripts.ai.privacy"), true);
         }
      }
   }

   private void leadCaption(String var1) {
      Font var2 = Fonts.NEW_MEDIUM.getFont(4.0F);
      float var3 = (16.0F - var2.height()) / 2.0F - 2.0F;
      this.panel
         .asideCustom(
            2.0F + var2.height() + var3,
            (var2x, var3x, var4, var5, var6, var7, var8, var9) -> var2x.drawText(
               var2, HoverInfo.spaced(t(var1).toUpperCase(Locale.ROOT)), var5 + 8.0F, var6 + 2.0F, var9.getTextTertiary().getZenithColor().method7294(var8)
            )
         );
   }

   private void cardFrame(MenuDrawContext var1, ZenithStyle var2, BoundsData var3, float var4, float var5, float var6, float var7) {
      ScriptsSectionPanel.card(var1, var2, var3.getFloatValue(), var3.getFloatValue2(), var3.width(), var3.height(), Math.max(var4, var5 * 0.6F), var7);
      ZenithColor var8 = var2.getPrimaryColor().getZenithColor();
      if (var5 > 0.01F) {
         var1.drawRoundedBorder(
            var3.getFloatValue(),
            var3.getFloatValue2(),
            var3.width(),
            var3.height(),
            0.25F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            var8.method7294(var7 * 0.7F * var5)
         );
      }

      if (var6 > 0.01F) {
         var1.drawRoundedRect(
            var3.getFloatValue(),
            var3.getFloatValue2(),
            var3.width(),
            var3.height(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            var8.method7292(0.16F * var6).method7294(var7)
         );
      }
   }

   private void iconTile(MenuDrawContext var1, ZenithStyle var2, MenuGlyph var3, float var4, float var5, float var6, boolean var7, float var8) {
      ZenithColor var9 = var2.getPrimaryColor().getZenithColor();
      var1.drawRoundedRect(
         var4,
         var5,
         var6,
         var6,
         ProxyDotRadiusData.method8069(var6 * 0.28F),
         (var7 ? var9.method7292(0.18F) : var2.getDisableActiveBg().getZenithColor().method7294(0.8F)).method7294(var8)
      );
      var3.draw(
         var1,
         var4 + (var6 - 6.0F) / 2.0F,
         var5 + (var6 - 6.0F) / 2.0F,
         (var7 ? var9.method7295(ZenithColor.INSTANCE, 0.2F) : var2.getTextSecondary().getZenithColor()).method7294(var8)
      );
   }

   private void chatsAside(Storage var1) {
      AiChatStorage var2 = AiChatStorage.method2741();
      this.panel
         .asideCustom(
            26.0F,
            this.panel
               .asideEntering(
                  0,
                  (var1x, var2x, var3x, var4, var5x, var6x, var7, var8x) -> {
                     BoundsData var9 = new BoundsData(var4 + 8.0F, var5x + 2.0F, var6x - 16.0F, 22.0F);
                     float var10 = this.panel.hot("newchat", var9.contains(var2x, var3x));
                     float var11 = Smooth.press(this.pressedAt.getOrDefault("newchat", 0L), 320L);
                     this.cardFrame(var1x, var8x, var9, var10, 0.0F, var11, var7);
                     float var12 = 14.0F;
                     this.iconTile(var1x, var8x, MenuGlyph.CHAT, var9.getFloatValue() + 4.0F, var9.getFloatValue2() + 4.0F, var12, true, var7);
                     Font var13 = Fonts.NEW_MEDIUM.getFont(4.8F);
                     var1x.drawText(
                        var13,
                        t("gui.scripts.ai.newChat"),
                        var9.getFloatValue() + var12 + 9.0F,
                        var9.getFloatValue2() + (22.0F - var13.height()) / 2.0F,
                        var8x.getTextEnable().getZenithColor().method7294(var7)
                     );
                     String var14 = "+";
                     Font var15 = Fonts.NEW_MEDIUM.getFont(6.0F);
                     var1x.drawText(
                        var15,
                        var14,
                        var9.getFloatValue() + var9.width() - 8.0F - var15.width(var14),
                        var9.getFloatValue2() + (22.0F - var15.height()) / 2.0F,
                        var8x.getPrimaryColor().getZenithColor().method7295(ZenithColor.INSTANCE, var10 * 0.4F).method7294(var7)
                     );
                     if (var9.contains(var2x, var3x)) {
                        HoverInfo.request(t("gui.scripts.ai.newChat"), t("gui.scripts.ai.newChat.desc"));
                     }
                  }
               ),
            () -> {
               this.pressedAt.put("newchat", System.currentTimeMillis());
               AiChatStorage.method2741().method2750(null);
               this.input.clear();
               this.focus(this.input);
               this.panel.replayEntrance();
               this.panel.invalidate();
            },
            null
         );
      if (var2.isBusy()) {
         this.panel.asideAction("gui.scripts.ai.stop", MenuGlyph.CLOSE, () -> "", () -> AiChatStorage.method2741().stop(), "gui.scripts.ai.stop.desc");
      }

      this.panel.asideCaption("gui.scripts.ai.caption.target");
      int var3 = 1;
      this.target(var1, null, var3++);

      for (Script var5 : var1.method1293()) {
         this.target(var1, var5, var3++);
      }

      List<AiChatStorage.Entry> var8 = var2.method2744();
      this.panel.asideCaption("gui.scripts.ai.caption.history");
      if (var8.isEmpty()) {
         this.panel.paragraph(t("gui.scripts.ai.history.empty"), true);
      } else {
         for (Object var6_o : (Iterable<?>)(Object)(var8.subList(0, Math.min(40, var8.size())))) {
            AiChatStorage.Entry var6 = (AiChatStorage.Entry)var6_o;
            this.panel
               .asideCustom(
                  31.0F,
                  this.panel
                     .asideEntering(
                        var3++,
                        (var3x, var4, var5x, var6x, var7, var8x, var9, var10) -> this.renderChatRow(
                           var3x, var4, var5x, var6x, var7, var8x, var9, var10, var1, var6
                        )
                     ),
                  () -> {
                     AiChatStorage.method2741().method2749(var6);
                     this.panel.invalidate();
                  },
                  null,
                  () -> {
                     if (this.armedChat.equals(var6.getText()) && System.currentTimeMillis() - this.armedChatAt < 3000L) {
                        this.armedChat = "";
                        AiChatStorage.method2741().method2752(var6);
                     } else {
                        this.armedChat = var6.getText();
                        this.armedChatAt = System.currentTimeMillis();
                     }

                     this.panel.invalidate();
                  }
               );
         }

         this.panel.gap(2.0F);
         if (!var2.getEntry().empty()) {
            this.panel.asideAction("gui.scripts.ai.export", MenuGlyph.STORAGE, () -> "", () -> {
               try {
                  Path var0 = AiChatStorage.method2741().method2760();
                  Util.getOperatingSystem().open(var0.getParent().toFile());
               } catch (Exception var1x) {
                  AiChatStorage.method2741().method2759(String.valueOf(var1x.getMessage()));
               }
            }, "gui.scripts.ai.export.desc");
         }

         this.panel
            .asideAction(
               "gui.scripts.ai.history.clear",
               MenuGlyph.CLOSE,
               () -> this.armedChat.equals("*") && System.currentTimeMillis() - this.armedChatAt < 3000L ? t("gui.scripts.market.again") : "",
               () -> {
                  if (this.armedChat.equals("*") && System.currentTimeMillis() - this.armedChatAt < 3000L) {
                     this.armedChat = "";
                     AiChatStorage.method2741().method2753();
                  } else {
                     this.armedChat = "*";
                     this.armedChatAt = System.currentTimeMillis();
                  }

                  this.panel.invalidate();
               },
               "gui.scripts.ai.history.clear.desc"
            );
      }
   }

   private void renderChatRow(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, Storage var9, AiChatStorage.Entry var10
   ) {
      BoundsData var11 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 28.0F);
      boolean var12 = var11.contains(var2, var3);
      boolean var13 = var10 == AiChatStorage.method2741().getEntry();
      boolean var14 = this.armedChat.equals(var10.getText()) && System.currentTimeMillis() - this.armedChatAt < 3000L;
      float var15 = this.panel.hot("chat:" + var10.getText(), var12);
      float var16 = this.panel.hot("chatsel:" + var10.getText(), var13);
      this.cardFrame(var1, var8, var11, var15, var16, 0.0F, var7);
      if (var14) {
         var1.drawRoundedRect(
            var11.getFloatValue(),
            var11.getFloatValue2(),
            var11.width(),
            var11.height(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            ERROR.method7292(0.12F).method7294(var7)
         );
      }

      float var17 = 20.0F;
      float var18 = var11.getFloatValue() + 4.0F;
      Script var19 = var10.getText2() == null ? null : var9.method1294(var10.getText2());
      if (var19 != null) {
         ScriptInfoPage.drawAvatar(var1, var8, var19, var18, var11.getFloatValue2() + 4.0F, var17, var7, -2);
      } else {
         this.iconTile(var1, var8, MenuGlyph.CHAT, var18, var11.getFloatValue2() + 4.0F, var17, false, var7);
      }

      Font var20 = Fonts.NEW_MEDIUM.getFont(4.6F);
      Font var21 = Fonts.NEW_REGULAR.getFont(4.3F);
      float var22 = var18 + var17 + 5.0F;
      String var23 = ScriptsSectionPanel.ago(var10.getLongValue2());
      float var24 = var11.getFloatValue() + var11.width() - 5.0F;
      float var25 = var11.getFloatValue2() + (28.0F - var20.height() - var21.height() - 2.5F) / 2.0F;
      var1.drawText(
         var20,
         var20.clip(var10.title().isEmpty() ? t("gui.scripts.ai.newChat") : var10.title(), var24 - var21.width(var23) - 4.0F - var22),
         var22,
         var25,
         (var13 ? var8.getTextEnable().getZenithColor() : var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var15))
            .method7294(var7)
      );
      var1.drawText(
         var21, var23, var24 - var21.width(var23), var25 + (var20.height() - var21.height()), var8.getTextTertiary().getZenithColor().method7294(var7)
      );
      String var26 = var14 ? t("gui.scripts.ai.history.confirm") : var10.method2772();
      var1.drawText(
         var21,
         var21.clip(var26, var24 - var22),
         var22,
         var25 + var20.height() + 2.5F,
         (var14 ? ERROR : var8.getTextTertiary().getZenithColor()).method7294(var7)
      );
      if (AiChatStorage.method2741().isBusy() && !var13 && var10.getItems().stream().anyMatch(AiChatStorage.Entry2::streaming)) {
         float var27 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 200.0);
         var1.drawRoundedRect(
            var18 + var17 - 3.0F,
            var11.getFloatValue2() + 3.0F,
            4.0F,
            4.0F,
            ProxyDotRadiusData.method8069(2.0F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * var27)
         );
      }

      if (var12) {
         HoverInfo.request(var10.title(), t("gui.scripts.ai.history.hint"));
      }
   }

   public float headerHeight() {
      return 18.0F;
   }

   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      AiChatStorage var9 = AiChatStorage.method2741();
      Storage var10 = this.panel.hostOrNull();
      Font var11 = Fonts.NEW_SEMIBOLD.getFont(5.2F);
      Font var12 = Fonts.NEW_MEDIUM.getFont(4.3F);
      ZenithColor var13 = var8.getPrimaryColor().getZenithColor();
      String var14 = var9.getEntry().title().isEmpty() ? t("gui.scripts.ai.newChat") : var9.getEntry().title();
      AiSettingsStore var15 = AiSettingsStore.method4467();
      String var16 = var15.getShownProvider().title() + " · " + var15.method4472(var15.getShownProvider());
      Script var17 = var9.method2747() != null && var10 != null ? var10.method1294(var9.method2747()) : null;
      String var18 = var17 == null ? t("gui.scripts.ai.target.new") : var17.method1403();
      float var19 = var4 + var6 - 8.0F;
      float var20 = Math.min(var12.width(var16) + 12.0F, var6 * 0.34F);
      BoundsData var21 = new BoundsData(var19 - var20, var5 + 3.0F, var20, 12.0F);
      float var22 = Math.min(var12.width(var18) + 12.0F, var6 * 0.28F);
      BoundsData var23 = new BoundsData(var21.getFloatValue() - 4.0F - var22, var5 + 3.0F, var22, 12.0F);
      this.hits.put("hdr:model", var21);
      this.hits.put("hdr:script", var23);

      for (Object[] var27 : new Object[][]{{var23, var18, "hdr:script"}, {var21, var16, "hdr:model"}}) {
         BoundsData var28 = (BoundsData)var27[0];
         float var29 = this.panel.hot((String)var27[2], var28.contains(var2, var3));
         var1.drawRoundedRect(
            var28.getFloatValue(),
            var28.getFloatValue2(),
            var28.width(),
            12.0F,
            ProxyDotRadiusData.method8069(6.0F),
            var8.getDisableActiveBg().getZenithColor().method7294((0.35F + 0.5F * var29) * var7)
         );
         var1.drawText(
            var12,
            var12.clip((String)var27[1], var28.width() - 10.0F),
            var28.getFloatValue() + 6.0F,
            var28.getFloatValue2() + (12.0F - var12.height()) / 2.0F,
            var8.getTextSecondary().getZenithColor().method7295(var13, var29 * 0.6F).method7294(var7)
         );
      }

      var1.drawText(
         var11,
         var11.clip(var14, var23.getFloatValue() - var4 - 8.0F - 6.0F),
         var4 + 8.0F,
         var5 + (18.0F - var11.height()) / 2.0F,
         var8.getTextEnable().getZenithColor().method7294(var7)
      );
      var1.drawRect(var4 + 8.0F, var5 + 17.5F, var6 - 16.0F, 0.5F, var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F));
      if (var23.contains(var2, var3)) {
         HoverInfo.request(t("gui.scripts.ai.caption.target"), t("gui.scripts.ai.hdr.script"));
      }

      if (var21.contains(var2, var3)) {
         HoverInfo.request(t("gui.scripts.ai.caption.model"), t("gui.scripts.ai.hdr.model"));
      }
   }

   public boolean clickHeader(double var1, double var3) {
      this.mouse(var1, var3);
      if (this.hovered(this.hits.get("hdr:script"))) {
         this.asideView = 0;
         this.panel.resetAsideScroll();
         this.panel.invalidate();
         return true;
      } else if (this.hovered(this.hits.get("hdr:model"))) {
         this.asideView = 1;
         this.panel.resetAsideScroll();
         this.panel.invalidate();
         return true;
      } else {
         return false;
      }
   }

   private void addForm() {
      this.panel.asideCaption("gui.scripts.ai.custom.caption");
      this.panel.paragraph(t("gui.scripts.ai.custom.desc"), true);
      this.field(this.addName, t("gui.scripts.ai.custom.name"), null, null);
      this.field(this.addUrl, t("gui.scripts.ai.custom.url"), null, null);
      this.field(this.addKey, t("gui.scripts.ai.custom.key"), "addpaste", t("gui.scripts.ai.key.paste"));
      this.field(this.addModel, t("gui.scripts.ai.custom.model"), null, null);
      this.panel
         .asideCustom(
            this.addError.isEmpty() ? 22.0F : 34.0F,
            (var1, var2, var3, var4, var5, var6, var7, var8) -> {
               Font var9 = Fonts.NEW_MEDIUM.getFont(4.6F);
               ZenithColor var10 = var8.getPrimaryColor().getZenithColor();
               float var11 = var5 + 3.0F;
               if (!this.addError.isEmpty()) {
                  Font var12 = Fonts.NEW_REGULAR.getFont(4.5F);
                  var1.drawText(var12, var12.clip(this.addError, var6 - 16.0F), var4 + 8.0F, var5 + 2.0F, ERROR.method7294(var7));
                  var11 += 12.0F;
               }

               float var20 = (var6 - 16.0F - 4.0F) / 2.0F;
               BoundsData var13 = new BoundsData(var4 + 8.0F, var11, var20, 16.0F);
               BoundsData var14 = new BoundsData(var4 + 8.0F + var20 + 4.0F, var11, var20, 16.0F);
               this.hits.put("add:ok", var13);
               this.hits.put("add:cancel", var14);
               float var15 = this.panel.hot("add:ok", var13.contains(var2, var3));
               float var16 = this.panel.hot("add:cancel", var14.contains(var2, var3));
               float var17 = Smooth.press(this.pressedAt.getOrDefault("add:ok", 0L), 320L);
               var1.drawRoundedRect(
                  var13.getFloatValue(),
                  var13.getFloatValue2(),
                  var13.width(),
                  16.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  var10.method7292(0.22F + 0.2F * var15 + 0.3F * var17).method7294(var7)
               );
               var1.drawRoundedRect(
                  var14.getFloatValue(),
                  var14.getFloatValue2(),
                  var14.width(),
                  16.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  var8.getDisableActiveBg().getZenithColor().method7294(0.4F + 0.5F * var16).method7294(var7)
               );
               String var18 = t("gui.scripts.ai.custom.add");
               String var19 = t("gui.scripts.ai.custom.cancel");
               var1.drawText(
                  var9,
                  var18,
                  var13.getFloatValue() + (var20 - var9.width(var18)) / 2.0F,
                  var13.getFloatValue2() + (16.0F - var9.height()) / 2.0F,
                  var10.method7295(ZenithColor.INSTANCE, 0.3F + var15 * 0.4F).method7294(var7)
               );
               var1.drawText(
                  var9,
                  var19,
                  var14.getFloatValue() + (var20 - var9.width(var19)) / 2.0F,
                  var14.getFloatValue2() + (16.0F - var9.height()) / 2.0F,
                  var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var16).method7294(var7)
               );
            },
            () -> {
               if (this.hovered(this.hits.get("add:ok"))) {
                  this.pressedAt.put("add:ok", System.currentTimeMillis());
                  this.submitCustom();
               } else if (this.hovered(this.hits.get("add:cancel"))) {
                  this.adding = false;
                  this.addError = "";
                  this.panel.invalidate();
               }
            },
            null
         );
   }

   private void submitCustom() {
      String var1 = this.addName.get();
      String var2 = this.addUrl.get();
      if (var1.isEmpty()) {
         this.addError = t("gui.scripts.ai.custom.err.name");
      } else if (!var2.startsWith("http://") && !var2.startsWith("https://")) {
         this.addError = t("gui.scripts.ai.custom.err.url");
      } else if (this.addModel.get().isEmpty()) {
         this.addError = t("gui.scripts.ai.custom.err.model");
      } else {
         this.commitFields();
         AiSettingsStore.method4467().method4476(var1, var2, this.addKey.get(), this.addModel.get());

         for (FormField var4 : List.of(this.addName, this.addUrl, this.addKey, this.addModel)) {
            var4.clear();
            var4.setFocused(false);
         }

         this.adding = false;
         this.addError = "";
         this.syncFields();
         this.panel.replayAside();
      }

      this.panel.invalidate();
   }

   private static ScriptAiTab.KeyState keyState(ShownProvider var0) {
      if (!AiSettingsStore.method4467().method4469(var0).isEmpty()) {
         return ScriptAiTab.KeyState.SAVED;
      } else {
         return var0.isFlag() ? ScriptAiTab.KeyState.MISSING : ScriptAiTab.KeyState.NOT_NEEDED;
      }
   }

   private static void statusDot(MenuDrawContext var0, ZenithStyle var1, ScriptAiTab.KeyState var2, float var3, float var4, float var5) {
      ProxyDotRadiusData var6 = ProxyDotRadiusData.method8069(2.0F);
      switch (var2) {
         case MISSING:
            var0.drawRoundedBorder(var3, var4, 4.0F, 4.0F, 0.6F, var6, var1.getTextTertiary().getZenithColor().method7294(var5));
            break;
         case SAVED:
            var0.drawRoundedRect(var3, var4, 4.0F, 4.0F, var6, OK.method7294(var5));
            break;
         case NOT_NEEDED:
            var0.drawRoundedRect(var3, var4, 4.0F, 4.0F, var6, LOCAL.method7294(var5));
      }
   }

   private void providerGrid() {
      ShownProvider[] var1 = ShownProvider.method7336();
      byte var2 = 3;
      int var3 = (var1.length + 1 + var2 - 1) / var2;
      float var4 = 20.0F;
      this.panel
         .asideCustom(
            var3 * (var4 + 3.0F) + 2.0F,
            (var4x, var5, var6, var7, var8, var9, var10, var11) -> {
               float var12 = var7 + 8.0F;
               float var13 = var9 - 16.0F;
               float var14 = (var13 - (var2 - 1) * 3.0F) / var2;
               ZenithColor var15 = var11.getPrimaryColor().getZenithColor();
               Font var16 = Fonts.NEW_MEDIUM.getFont(4.7F);

               for (int var17 = 0; var17 < var1.length; var17++) {
                  ShownProvider var18 = var1[var17];
                  float var19 = var12 + var17 % var2 * (var14 + 3.0F);
                  float var20 = var8 + 1.0F + var17 / var2 * (var4 + 3.0F);
                  BoundsData var21 = new BoundsData(var19, var20, var14, var4);
                  this.hits.put("prov:" + var18.getText(), var21);
                  boolean var22 = var21.contains(var5, var6);
                  boolean var23 = AiSettingsStore.method4467().getShownProvider().equals(var18);
                  ScriptAiTab.KeyState var24 = keyState(var18);
                  float var25 = this.panel.hot("prov:" + var18.getText(), var22);
                  float var26 = this.panel.hot("provsel:" + var18.getText(), var23);
                  float var27 = this.panel.asideEnter(var17);
                  float var28 = (1.0F - var27) * 5.0F;
                  ProxyDotRadiusData var29 = ProxyDotRadiusData.method8069(5.0F);
                  var4x.drawRoundedRect(
                     var19,
                     var20 + var28,
                     var14,
                     var4,
                     var29,
                     ScriptsSectionPanel.cardFill(var11, var25 * 0.8F).method7295(var15.method7292(0.18F), var26 * 0.7F).method7294(var10 * var27)
                  );
                  var4x.drawRoundedBorder(
                     var19,
                     var20 + var28,
                     var14,
                     var4,
                     0.2F,
                     var29,
                     var11.getFieldBorder()
                        .getZenithColor()
                        .method7294(2.0F)
                        .method7295(var15, var26)
                        .method7294(var10 * var27 * (0.6F + 0.4F * Math.max(var26, var25)))
                  );
                  statusDot(var4x, var11, var24, var19 + var14 - 3.5F - 4.0F, var20 + var28 + 3.5F, var10 * var27);
                  var4x.drawText(
                     var16,
                     var16.clip(var18.title(), var14 - 10.0F),
                     var19 + 5.0F,
                     var20 + var28 + (var4 - var16.height()) / 2.0F,
                     var11.getTextSecondary()
                        .getZenithColor()
                        .method7295(var11.getTextEnable().getZenithColor(), Math.max(var26, var25))
                        .method7294(var10 * var27)
                  );
                  if (var22) {
                     HoverInfo.request(var18.title() + " · " + var18.getText3(), t(var24.description));
                  }
               }

               int var30 = var1.length;
               float var31 = var12 + var30 % var2 * (var14 + 3.0F);
               float var32 = var8 + 1.0F + var30 / var2 * (var4 + 3.0F);
               BoundsData var33 = new BoundsData(var31, var32, var14, var4);
               this.hits.put("prov:+", var33);
               boolean var34 = var33.contains(var5, var6);
               float var35 = this.panel.hot("prov:+", var34 || this.adding);
               float var36 = this.panel.asideEnter(var30);
               float var37 = (1.0F - var36) * 5.0F;
               ProxyDotRadiusData var38 = ProxyDotRadiusData.method8069(5.0F);
               var4x.drawRoundedRect(var31, var32 + var37, var14, var4, var38, var15.method7292(0.06F + 0.12F * var35).method7294(var10 * var36));
               var4x.drawRoundedBorder(var31, var32 + var37, var14, var4, 0.2F, var38, var15.method7292(0.35F + 0.4F * var35).method7294(var10 * var36));
               String var39 = "+ " + t("gui.scripts.ai.custom.tile");
               var4x.drawText(
                  var16,
                  var16.clip(var39, var14 - 8.0F),
                  var31 + (var14 - var16.width(var16.clip(var39, var14 - 8.0F))) / 2.0F,
                  var32 + var37 + (var4 - var16.height()) / 2.0F,
                  var15.method7295(ZenithColor.INSTANCE, 0.25F + var35 * 0.4F).method7294(var10 * var36)
               );
               if (var34) {
                  HoverInfo.request(t("gui.scripts.ai.custom.caption"), t("gui.scripts.ai.custom.desc"));
               }
            },
            () -> {
               if (this.hovered(this.hits.get("prov:+"))) {
                  this.commitFields();
                  this.adding = !this.adding;
                  this.addError = "";
                  if (this.adding) {
                     this.focus(this.addName);
                  }

                  this.panel.invalidate();
               } else {
                  for (ShownProvider var5 : var1) {
                     if (this.hovered(this.hits.get("prov:" + var5.getText()))) {
                        this.commitFields();
                        this.adding = false;
                        AiSettingsStore.method4467().method4468(var5);
                        this.syncFields();
                        this.panel.invalidate();
                        return;
                     }
                  }
               }
            },
            null
         );
   }

   private void field(FormField var1, String var2, String var3, String var4) {
      this.panel
         .asideCustom(
            21.0F,
            (var5, var6, var7, var8, var9, var10, var11, var12) -> {
               Font var13 = Fonts.NEW_MEDIUM.getFont(4.5F);
               float var14 = var4 == null ? 0.0F : var13.width(var4) + 12.0F;
               float var15 = var10 - 16.0F - (var14 > 0.0F ? var14 + 4.0F : 0.0F);
               var1.render(
                  var5,
                  var13,
                  var8 + 8.0F,
                  var9 + 2.5F,
                  var15,
                  var11,
                  var2,
                  var12.getFieldSurfaceBackground().getZenithColor(),
                  var12.getFieldBorder().getZenithColor().method7294(2.0F),
                  var12.getPrimaryColor().getZenithColor(),
                  var12.getTextEnable().getZenithColor(),
                  var12.getTextTertiary().getZenithColor()
               );
               if (var4 != null) {
                  BoundsData var16 = new BoundsData(var8 + 8.0F + var15 + 4.0F, var9 + 2.5F, var14, 16.0F);
                  this.hits.put("aside:" + var3, var16);
                  boolean var17 = var16.contains(var6, var7);
                  float var18 = this.panel.hot("aside:" + var3, var17);
                  ZenithColor var19 = var12.getPrimaryColor().getZenithColor();
                  var5.drawRoundedRect(
                     var16.getFloatValue(),
                     var16.getFloatValue2(),
                     var14,
                     16.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     var19.method7292(0.16F + 0.2F * var18).method7294(var11)
                  );
                  var5.drawText(
                     var13,
                     var4,
                     var16.getFloatValue() + 6.0F,
                     var16.getFloatValue2() + (16.0F - var13.height()) / 2.0F,
                     var19.method7295(ZenithColor.INSTANCE, var18 * 0.5F).method7294(var11)
                  );
               }
            },
            () -> {
               if (var3 != null && this.hovered(this.hits.get("aside:" + var3))) {
                  String var3x = MinecraftClient.getInstance().keyboard.getClipboard();
                  if (var3.equals("paste") && var3x != null && !var3x.isBlank()) {
                     this.keyField.set(var3x.strip());
                     AiSettingsStore.method4467().method4470(AiSettingsStore.method4467().getShownProvider(), var3x.strip());
                     this.panel.invalidate();
                  } else if (var3.equals("addpaste") && var3x != null && !var3x.isBlank()) {
                     this.addKey.set(var3x.strip());
                  }
               } else {
                  this.focus(var1);
               }
            },
            null
         );
   }

   private void modelChips(ShownProvider var1) {
      Font var2 = Fonts.NEW_MEDIUM.getFont(4.4F);
      float var3 = 128.0F;
      ArrayList var4 = new ArrayList();
      float var5 = 0.0F;
      float var6 = 0.0F;

      for (String var8 : var1.getItems3()) {
         float var9 = var2.width(var8) + 10.0F;
         if (var5 > 0.0F && var5 + var9 > var3) {
            var5 = 0.0F;
            var6 += 13.0F;
         }

         var4.add(new float[]{var5, var6, var9});
         var5 += var9 + 3.0F;
      }

      float var10 = var6 + 16.0F;
      this.panel
         .asideCustom(
            var10,
            (var4x, var5x, var6x, var7, var8x, var9x, var10x, var11) -> {
               for (int var12 = 0; var12 < var1.getItems3().size(); var12++) {
                  String var13 = var1.getItems3().get(var12);
                  float[] var14 = (float[])var4.get(var12);
                  BoundsData var15 = new BoundsData(var7 + 8.0F + var14[0], var8x + 2.0F + var14[1], var14[2], 11.0F);
                  this.hits.put("model:" + var13, var15);
                  boolean var16 = var15.contains(var5x, var6x);
                  boolean var17 = var13.equals(AiSettingsStore.method4467().method4472(var1));
                  float var18 = this.panel.hot("model:" + var13, var16 || var17);
                  var4x.drawRoundedRect(
                     var15.getFloatValue(),
                     var15.getFloatValue2(),
                     var15.width(),
                     11.0F,
                     ProxyDotRadiusData.method8069(5.5F),
                     (var17
                           ? var11.getPrimaryColor().getZenithColor().method7292(0.22F)
                           : var11.getDisableActiveBg().getZenithColor().method7294(0.3F + var18 * 0.5F))
                        .method7294(var10x)
                  );
                  var4x.drawText(
                     var2,
                     var13,
                     var15.getFloatValue() + 5.0F,
                     var15.getFloatValue2() + (11.0F - var2.height()) / 2.0F,
                     (var17
                           ? var11.getPrimaryColor().getZenithColor().method7295(ZenithColor.INSTANCE, 0.3F)
                           : var11.getTextTertiary().getZenithColor().method7295(var11.getTextEnable().getZenithColor(), var18))
                        .method7294(var10x)
                  );
               }
            },
            () -> {
               for (String var3x : var1.getItems3()) {
                  if (this.hovered(this.hits.get("model:" + var3x))) {
                     AiSettingsStore.method4467().method4473(var1, var3x);
                     this.modelField.set(var3x);
                     this.panel.invalidate();
                     return;
                  }
               }
            },
            null
         );
   }

   private void target(Storage var1, Script var2, int var3) {
      String var4 = var2 == null ? null : var2.getId();
      this.panel
         .asideCustom(
            25.0F,
            this.panel
               .asideEntering(
                  var3,
                  (var4x, var5, var6, var7, var8, var9, var10, var11) -> {
                     BoundsData var12 = new BoundsData(var7 + 8.0F, var8 + 1.0F, var9 - 16.0F, 22.0F);
                     String var13 = AiChatStorage.method2741().method2747();
                     boolean var14 = var4 == null ? var13 == null || var1.method1294(var13) == null : var4.equals(var13);
                     float var15 = this.panel.hot("tgt:" + var4, var12.contains(var5, var6));
                     float var16 = this.panel.hot("tgtsel:" + var4, var14);
                     this.cardFrame(var4x, var11, var12, var15, var16, 0.0F, var10);
                     float var17 = 14.0F;
                     float var18 = var12.getFloatValue() + 4.0F;
                     Font var19 = Fonts.NEW_MEDIUM.getFont(4.6F);
                     Font var20 = Fonts.NEW_REGULAR.getFont(4.3F);
                     ZenithColor var23 = var11.getTextTertiary().getZenithColor();
                     String var21;
                     String var22;
                     if (var2 == null) {
                        this.iconTile(var4x, var11, MenuGlyph.ACTION, var18, var12.getFloatValue2() + 4.0F, var17, true, var10);
                        var21 = t("gui.scripts.ai.target.new");
                        var22 = t("gui.scripts.ai.target.new.sub");
                     } else {
                        ScriptInfoPage.drawAvatar(var4x, var11, var2, var18, var12.getFloatValue2() + 4.0F, var17, var10, -2);
                        var21 = var2.method1403();
                        if (!var2.getError().isBlank()) {
                           var22 = t("gui.scripts.state.failed");
                           var23 = ERROR;
                        } else if (var2.isRunning()) {
                           var22 = t("gui.scripts.state.running");
                           var23 = OK.method7295(var11.getTextTertiary().getZenithColor(), 0.3F);
                        } else {
                           var22 = t("gui.scripts.state.off");
                        }
                     }

                     float var24 = var18 + var17 + 5.0F;
                     float var25 = var12.getFloatValue() + var12.width() - 5.0F - var24;
                     float var26 = var12.getFloatValue2() + (22.0F - var19.height() - var20.height() - 2.0F) / 2.0F;
                     var4x.drawText(
                        var19,
                        var19.clip(var21, var25),
                        var24,
                        var26,
                        (var14
                              ? var11.getTextEnable().getZenithColor()
                              : var11.getTextSecondary().getZenithColor().method7295(var11.getTextEnable().getZenithColor(), var15))
                           .method7294(var10)
                     );
                     var4x.drawText(var20, var20.clip(var22, var25), var24, var26 + var19.height() + 2.0F, var23.method7294(var10));
                  }
               ),
            () -> {
               AiChatStorage.method2741().method2748(var4);
               this.panel.invalidate();
            },
            null
         );
   }

   private void toggle(String var1, String var2, String var3, BooleanSupplier var4, Consumer<Boolean> var5) {
      this.panel
         .asideCustom(
            16.0F,
            (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
               Font var13 = Fonts.NEW_MEDIUM.getFont(4.6F);
               boolean var14 = new BoundsData(var8, var9, var10, 16.0F).contains(var6, var7);
               var5x.drawText(
                  var13,
                  t(var2),
                  var8 + 8.0F,
                  var9 + (16.0F - var13.height()) / 2.0F,
                  (var14 ? var12.getTextEnable().getZenithColor() : var12.getTextSecondary().getZenithColor()).method7294(var11)
               );
               this.panel.drawSwitch(var5x, "ai:" + var1, var8 + var10 - 8.0F - 14.0F, var9 + 4.0F, 14.0F, 8.0F, var4.getAsBoolean(), var11, var12);
               if (var14) {
                  HoverInfo.request(t(var2), t(var3));
               }
            },
            () -> {
               var5.accept(!var4.getAsBoolean());
               this.panel.invalidate();
            },
            null
         );
   }

   private void focus(FormField var1) {
      for (FormField var3 : this.fields()) {
         if (var3 != var1 && var3.isFocused()) {
            var3.setFocused(false);
         }
      }

      this.commitFields();
      if (var1 != null) {
         var1.setFocused(true);
      }
   }

   public FormField focused() {
      for (FormField var2 : this.fields()) {
         if (var2.isFocused()) {
            return var2;
         }
      }

      return null;
   }

   private static void thumb(MenuDrawContext var0, Identifier var1, BoundsData var2, float var3, ZenithColor var4, float var5, float var6) {
      ProxyDotRadiusData var7 = ProxyDotRadiusData.method8069(var3);
      if (var1 != null) {
         float var8 = (1.0F - Math.min(1.0F, var6)) / 2.0F;
         var0.drawRoundedTexture(
            var1,
            var2.getFloatValue(),
            var2.getFloatValue2(),
            var2.width(),
            var2.height(),
            var7,
            ZenithColor.INSTANCE.method7294(var5),
            var8,
            0.0F,
            1.0F - var8,
            1.0F
         );
      } else {
         var0.drawRoundedRect(
            var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), var7, ZenithColor.INSTANCE.method7292(0.05F).method7294(var5)
         );
      }

      var0.drawRoundedBorder(var2.getFloatValue(), var2.getFloatValue2(), var2.width(), var2.height(), 0.3F, var7, var4);
   }

   private static void removeBadge(MenuDrawContext var0, BoundsData var1, float var2, float var3) {
      float var4 = var1.width();
      ProxyDotRadiusData var5 = ProxyDotRadiusData.method8069(var4 / 2.0F);
      var0.drawRoundedRect(
         var1.getFloatValue(), var1.getFloatValue2(), var4, var4, var5, new ZenithColor(12, 12, 14, 190).method7295(ERROR, 0.85F * var2).method7294(var3)
      );
      var0.drawRoundedBorder(
         var1.getFloatValue(), var1.getFloatValue2(), var4, var4, 0.2F, var5, ZenithColor.INSTANCE.method7292(0.16F + 0.24F * var2).method7294(var3)
      );
      MediaGlyphs.close(
         var0,
         var1.getFloatValue() + var4 / 2.0F,
         var1.getFloatValue2() + var4 / 2.0F,
         var4 * 0.5F,
         ZenithColor.INSTANCE.method7292(0.8F + 0.2F * var2).method7294(var3)
      );
   }

   private boolean slashMode() {
      String var1 = this.input.get();
      return var1.startsWith("/") && !var1.contains(" ") && this.input.isFocused();
   }

   public float footerHeight() {
      float var1 = this.input.height(Fonts.NEW_REGULAR.getFont(4.7F), 158.0F);
      return var1 + 12.0F + (this.pending.isEmpty() && this.importing == 0 ? 0.0F : 30.0F) + (this.slashMode() ? 13.0F : 0.0F);
   }

   public void renderFooter(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      var1.drawRect(var4 + 8.0F, var5, var6 - 16.0F, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F));
      Font var9 = Fonts.NEW_REGULAR.getFont(4.7F);
      Font var10 = Fonts.NEW_MEDIUM.getFont(4.3F);
      AiChatStorage var11 = AiChatStorage.method2741();
      ZenithColor var12 = var8.getPrimaryColor().getZenithColor();
      float var13 = var5 + 6.0F;
      if (this.slashMode()) {
         String var14 = this.input.get().toLowerCase(Locale.ROOT);
         float var15 = var4 + 8.0F;

         for (String[] var19 : COMMANDS) {
            if (var19[0].startsWith(var14)) {
               String var20 = var19[0];
               float var21 = var10.width(var20) + 8.0F;
               if (var15 + var21 > var4 + var6 - 8.0F) {
                  break;
               }

               BoundsData var22 = new BoundsData(var15, var13, var21, 10.0F);
               this.hits.put("cmd:" + var19[0], var22);
               boolean var23 = var22.contains(var2, var3);
               float var24 = this.panel.hot("cmd:" + var19[0], var23);
               var1.drawRoundedRect(
                  var22.getFloatValue(),
                  var22.getFloatValue2(),
                  var21,
                  10.0F,
                  ProxyDotRadiusData.method8069(5.0F),
                  var12.method7292(0.14F + 0.2F * var24).method7294(var7)
               );
               var1.drawText(
                  var10,
                  var20,
                  var15 + 4.0F,
                  var13 + (10.0F - var10.height()) / 2.0F,
                  var12.method7295(ZenithColor.INSTANCE, 0.3F + var24 * 0.4F).method7294(var7)
               );
               if (var23) {
                  HoverInfo.request(var20, t(var19[1]));
               }

               var15 += var21 + 3.0F;
            }
         }

         var13 += 13.0F;
      }

      if (!this.pending.isEmpty() || this.importing > 0) {
         float var35 = var4 + 8.0F;
         float var37 = 24.0F;
         String var39 = this.pending.size() + "/6";
         float[] var41 = new float[this.pending.size()];
         float var43 = 0.0F;

         for (int var45 = 0; var45 < var41.length; var45++) {
            int[] var48 = AiImageStore.method4638(this.pending.get(var45));
            var41[var45] = Math.max(var37 * 0.6F, Math.min(var37 * 2.0F, var37 * var48[0] / Math.max(1, var48[1])));
            var43 += var41[var45];
         }

         float var46 = var6 - 16.0F - var10.width(var39) - 6.0F - (this.importing > 0 ? var37 + 4.0F : 0.0F) - Math.max(0, var41.length - 1) * 4.0F;
         float var49 = var43 > var46 && var43 > 0.0F ? Math.max(0.1F, var46 / var43) : 1.0F;

         for (int var51 = 0; var51 < var41.length; var51++) {
            String var54 = this.pending.get(var51);
            Identifier var56 = AiImageStore.method4639(var54);
            float var59 = var41[var51] * var49;
            BoundsData var25 = new BoundsData(var35, var13, var59, var37);
            boolean var26 = var25.contains(var2, var3);
            float var27 = this.panel.hot("att:" + var54, var26);
            thumb(var1, var56, var25, 4.0F, var12.method7292(0.3F + 0.4F * var27).method7294(var7), var7, var49);
            BoundsData var28 = new BoundsData(var35 + var59 - 8.0F - 2.0F, var13 + 2.0F, 8.0F, 8.0F);
            this.hits.put("attx:" + var51, var28);
            if (var27 > 0.01F) {
               removeBadge(var1, var28, this.panel.hot("attx:" + var54, var28.contains(var2, var3)), var7 * var27);
            }

            var35 += var59 + 4.0F;
         }

         if (this.importing > 0) {
            float var52 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 160.0);
            var1.drawRoundedRect(var35, var13, 24.0F, 24.0F, ProxyDotRadiusData.method8069(4.0F), var12.method7292(0.1F + 0.15F * var52).method7294(var7));
         }

         var1.drawText(
            var10,
            var39,
            var4 + var6 - 8.0F - var10.width(var39),
            var13 + (24.0F - var10.height()) / 2.0F,
            var8.getTextTertiary().getZenithColor().method7294(var7)
         );
         var13 += 30.0F;
      }

      String var36 = var11.method2747() != null && this.panel.hostOrNull() != null && this.panel.hostOrNull().method1294(var11.method2747()) != null
         ? this.panel.hostOrNull().method1294(var11.method2747()).method1403()
         : t("gui.scripts.ai.target.new");
      String var38 = t("gui.scripts.ai.input.hint").replace("{s}", var36);
      float var40 = 18.0F;
      float var42 = var4 + 8.0F + var40 + 4.0F;
      float var44 = var6 - 16.0F - 40.0F - var40 - 4.0F;
      float var47 = this.input
         .render(
            var1,
            var9,
            var42,
            var13,
            var44,
            var7,
            var38,
            var8.getFieldSurfaceBackground().getZenithColor(),
            var8.getFieldBorder().getZenithColor().method7294(2.0F),
            var8.getPrimaryColor().getZenithColor(),
            var8.getTextEnable().getZenithColor(),
            var8.getTextTertiary().getZenithColor()
         );
      float var50 = (var47 - 2.0F) / 2.0F;
      BoundsData var53 = new BoundsData(var4 + 8.0F, var13, var40, var50);
      BoundsData var55 = new BoundsData(var4 + 8.0F, var13 + var50 + 2.0F, var40, var50);
      this.hits.put("tool:attach", var53);
      this.hits.put("tool:shot", var55);

      for (Object[] var64 : new Object[][]{{var53, "attach"}, {var55, "shot"}}) {
         BoundsData var66 = (BoundsData)var64[0];
         String var68 = (String)var64[1];
         boolean var29 = var66.contains(var2, var3);
         float var30 = this.panel.hot("tool:" + var68, var29);
         float var31 = Smooth.press(this.pressedAt.getOrDefault("tool:" + var68, 0L), 320L);
         var1.drawRoundedRect(
            var66.getFloatValue(),
            var66.getFloatValue2(),
            var66.width(),
            var66.height(),
            ProxyDotRadiusData.method8069(4.0F),
            var8.getFieldSurfaceBackground().getZenithColor().method7295(var12.method7292(0.3F), 0.2F + var30 * 0.5F + var31 * 0.3F).method7294(var7)
         );
         ZenithColor var32 = var8.getTextSecondary().getZenithColor().method7295(var12, var30).method7294(var7);
         float var33 = var66.getFloatValue() + var66.width() / 2.0F;
         float var34 = var66.getFloatValue2() + var66.height() / 2.0F;
         if (var68.equals("attach")) {
            var1.drawRect(var33 - 3.0F, var34 - 0.4F, 6.0F, 0.8F, var32);
            var1.drawRect(var33 - 0.4F, var34 - 3.0F, 0.8F, 6.0F, var32);
         } else {
            var1.drawRoundedBorder(var33 - 4.0F, var34 - 2.6F, 8.0F, 5.6F, 0.4F, ProxyDotRadiusData.method8069(1.2F), var32);
            var1.drawRoundedRect(var33 - 1.3F, var34 - 1.3F, 2.6F, 2.6F, ProxyDotRadiusData.method8069(1.3F), var32);
         }

         if (var29) {
            HoverInfo.request(t("gui.scripts.ai.tool." + var68), t("gui.scripts.ai.tool." + var68 + ".desc"));
         }
      }

      this.sendRect = new BoundsData(var42 + var44 + 4.0F, var13, 36.0F, var47);
      boolean var58 = this.sendRect.contains(var2, var3);
      float var61 = this.panel.hot("ai:send", var58);
      boolean var63 = var11.isBusy();
      float var65 = Smooth.press(this.pressedAt.getOrDefault("send", 0L), 320L);
      var1.drawRoundedRect(
         this.sendRect.getFloatValue(),
         this.sendRect.getFloatValue2(),
         this.sendRect.width(),
         this.sendRect.height(),
         ProxyDotRadiusData.method8069(5.0F),
         (var63 ? ERROR.method7292(0.2F + var61 * 0.15F) : var12.method7292(0.22F + var61 * 0.2F + var65 * 0.3F)).method7294(var7)
      );
      Font var67 = Fonts.NEW_MEDIUM.getFont(4.6F);
      String var69 = t(var63 ? "gui.scripts.ai.stop" : "gui.scripts.ai.send");
      var1.drawText(
         var67,
         var69,
         this.sendRect.getFloatValue() + (this.sendRect.width() - var67.width(var69)) / 2.0F,
         this.sendRect.getFloatValue2() + (this.sendRect.height() - var67.height()) / 2.0F,
         (var63 ? ERROR : var12.method7295(ZenithColor.INSTANCE, 0.3F + var61 * 0.4F)).method7294(var7)
      );
      if (var58) {
         HoverInfo.request(var69, t("gui.scripts.ai.send.desc"));
      }

      int var70 = this.input.get().length();
      if (var70 > 300) {
         String var71 = var70 + "/6000";
         var1.drawText(
            var10,
            var71,
            var42 + var44 - 4.0F - var10.width(var71),
            var13 + var47 - var10.height() - 2.0F,
            var8.getTextTertiary().getZenithColor().method7294(var7 * 0.8F)
         );
      }
   }

   public boolean clickFooter(double var1, double var3) {
      this.mouse(var1, var3);
      if (this.sendRect != null && this.sendRect.contains(var1, var3)) {
         this.pressedAt.put("send", System.currentTimeMillis());
         if (AiChatStorage.method2741().isBusy()) {
            AiChatStorage.method2741().stop();
         } else {
            this.send();
         }

         return true;
      } else {
         for (String[] var8 : COMMANDS) {
            if (this.hovered(this.hits.get("cmd:" + var8[0]))) {
               this.input.set(var8[0] + (var8[0].equals("/model") ? " " : ""));
               this.focus(this.input);
               if (!var8[0].equals("/model")) {
                  this.send();
               }

               return true;
            }
         }

         if (this.hovered(this.hits.get("tool:attach"))) {
            this.pressedAt.put("tool:attach", System.currentTimeMillis());
            this.pickImages();
            return true;
         } else if (this.hovered(this.hits.get("tool:shot"))) {
            this.pressedAt.put("tool:shot", System.currentTimeMillis());
            this.screenshot();
            return true;
         } else {
            for (int var9 = this.pending.size() - 1; var9 >= 0; var9--) {
               if (this.hovered(this.hits.get("attx:" + var9))) {
                  this.pending.remove(var9);
                  this.panel.invalidate();
                  return true;
               }
            }

            this.focus(this.input);
            return true;
         }
      }
   }

   private void notify(String var1) {
      HudHolder.method6969().method6973("i", Text.literal(var1), 3000L);
   }

   public void attachFiles(List<Path> var1) {
      for (Path var3 : var1) {
         if (this.pending.size() + this.importing >= 6) {
            this.notify(t("gui.scripts.ai.attach.limit"));
            break;
         }

         if (!AiImageStore.method4631(var3)) {
            this.notify(t("gui.scripts.ai.attach.type") + " " + var3.getFileName());
         } else {
            this.importing++;
            Thread.ofPlatform().daemon(true).name("zenith-ai-attach").start(() -> {
               try {
                  String var2 = AiImageStore.method4632(var3);
                  MinecraftClient.getInstance().execute(() -> {
                     this.pending.add(var2);
                     this.importing--;
                     this.panel.invalidate();
                  });
               } catch (Exception var3x) {
                  MinecraftClient.getInstance().execute(() -> {
                     this.importing--;
                     this.notify(var3x.getMessage());
                  });
               }
            });
         }
      }

      this.focus(this.input);
      this.panel.invalidate();
   }

   private void pickImages() {
      Thread.ofPlatform().daemon(true).name("zenith-ai-pick").start(() -> {
         MemoryStack var2 = MemoryStack.stackPush();

         String var1;
         try {
            PointerBuffer var3 = var2.mallocPointer(5);

            for (String var7 : new String[]{"*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"}) {
               var3.put(var2.UTF8(var7));
            }

            var3.flip();
            var1 = TinyFileDialogs.tinyfd_openFileDialog(t("gui.scripts.ai.tool.attach"), "", var3, "Картинки", true);
         } catch (Throwable var9) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var2 != null) {
            var2.close();
         }

         if (var1 != null && !var1.isBlank()) {
            ArrayList var10 = new ArrayList();

            for (String var14 : var1.split("\\|")) {
               if (!var14.isBlank()) {
                  var10.add(Path.of(var14));
               }
            }

            MinecraftClient.getInstance().execute(() -> this.attachFiles(var10));
         }
      });
   }

   private void screenshot() {
      if (this.pending.size() + this.importing >= 6) {
         this.notify(t("gui.scripts.ai.attach.limit"));
      } else {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1.world == null) {
            this.notify(t("gui.scripts.ai.attach.noworld"));
         } else {
            if (!shotHooked) {
               shotHooked = true;
               EventManager.main().subscribe(TickEvent.class, (byte)4, (var0, var1x) -> tickShot());
            }

            this.importing++;
            shotReturn = var1.currentScreen;
            shotDone = var1x -> {
               this.importing--;
               if (var1x != null) {
                  this.pending.add(var1x);
               }

               this.panel.invalidate();
            };
            shotCountdown = 3;
            var1.setScreen(null);
         }
      }
   }

   private static void tickShot() {
      if (shotCountdown >= 0) {
         if (--shotCountdown <= 0) {
            shotCountdown = -1;
            MinecraftClient var0 = MinecraftClient.getInstance();
            Screen var1 = shotReturn;
            var var2 = shotDone;
            ScreenshotRecorder.takeScreenshot(var0.getFramebuffer(), 1, var3 -> Thread.ofPlatform().daemon(true).start(() -> {
               String var4 = null;

               try {
                  var4 = AiImageStore.method4633(var3);
               } catch (Exception var9) {
               } finally {
                  var3.close();
               }

               String var5 = var4;
               var0.execute(() -> {
                  if (var1 != null) {
                     var0.setScreen(var1);
                  }

                  if (var2 != null) {
                     var2.accept(var5);
                  }
               });
            }));
         }
      }
   }

   public void send() {
      Storage var1 = this.panel.hostOrNull();
      String var2 = this.input.get();
      if (var1 != null && (!var2.isEmpty() || !this.pending.isEmpty()) && !AiChatStorage.method2741().isBusy() && this.importing <= 0) {
         if (var2.startsWith("/") && this.command(var1, var2)) {
            this.input.clear();
            this.panel.invalidate();
         } else {
            this.input.clear();
            var var3 = new ArrayList<>(this.pending);
            this.pending.clear();
            AiChatStorage.method2741().method2758(var1, var2, var3);
            this.panel.invalidate();
         }
      }
   }

   private boolean command(Storage var1, String var2) {
      String[] var3 = var2.strip().split("\\s+", 2);
      String var4 = var3.length > 1 ? var3[1] : "";
      AiChatStorage var5 = AiChatStorage.method2741();
      String var6 = var3[0].toLowerCase(Locale.ROOT);
      switch (var6) {
         case "/new":
            var5.method2750(null);
            break;
         case "/clear":
            var5.clear();
            break;
         case "/fix":
            if (var5.method2747() == null) {
               var5.method2759(t("gui.scripts.ai.cmd.noscript"));
            } else {
               var5.method2769(var1);
            }
            break;
         case "/explain":
            var5.method2757(var1, t("gui.scripts.ai.quick.explain.prompt"));
            break;
         case "/shot":
            this.screenshot();
            break;
         case "/export":
            try {
               Path var14 = var5.method2760();
               Util.getOperatingSystem().open(var14.getParent().toFile());
               var5.method2759(t("gui.scripts.ai.cmd.exported").replace("{f}", var14.getFileName().toString()));
            } catch (Exception var13) {
               var5.method2759(var13.getMessage());
            }
            break;
         case "/model":
            if (var4.isBlank()) {
               var5.method2759(
                  t("gui.scripts.ai.cmd.model.now").replace("{m}", AiSettingsStore.method4467().method4472(AiSettingsStore.method4467().getShownProvider()))
               );
            } else {
               AiSettingsStore.method4467().method4473(AiSettingsStore.method4467().getShownProvider(), var4);
               this.modelField.set(AiSettingsStore.method4467().method4472(AiSettingsStore.method4467().getShownProvider()));
               var5.method2759(
                  t("gui.scripts.ai.cmd.model.set").replace("{m}", AiSettingsStore.method4467().method4472(AiSettingsStore.method4467().getShownProvider()))
               );
            }
            break;
         case "/help":
            StringBuilder var8 = new StringBuilder(t("gui.scripts.ai.cmd.helpTitle")).append('\n');

            for (String[] var12 : COMMANDS) {
               var8.append(var12[0]).append(" — ").append(t(var12[1])).append('\n');
            }

            var8.append(t("gui.scripts.ai.cmd.helpTail"));
            var5.method2759(var8.toString());
            break;
         default:
            return false;
      }

      return true;
   }

   public boolean keyPressed(int var1, int var2) {
      FormField var3 = this.focused();
      if (var3 == null) {
         return false;
      } else if (var1 == 257 || var1 == 335) {
         var var10 = List.of(this.addName, this.addUrl, this.addKey, this.addModel);
         int var11 = var10.indexOf(var3);
         if (var11 >= 0) {
            if (var11 == var10.size() - 1) {
               this.submitCustom();
            } else {
               this.focus((FormField)var10.get(var11 + 1));
            }

            return true;
         } else {
            if (var3 == this.input) {
               this.send();
            } else {
               this.commitFields();
               var3.setFocused(false);
               this.panel.invalidate();
            }

            return true;
         }
      } else if (var3 == this.input && var1 == 265 && this.input.get().isEmpty()) {
         String var9 = AiChatStorage.method2741().method2762();
         if (!var9.isEmpty()) {
            this.input.set(var9);
         }

         return true;
      } else if (var1 == 256 && var3 == this.input && AiChatStorage.method2741().isBusy()) {
         AiChatStorage.method2741().stop();
         return true;
      } else if (var1 == 256) {
         var3.setFocused(false);
         this.commitFields();
         return true;
      } else if (var3 == this.input && var1 == 86 && (var2 & 2) != 0) {
         String var4 = MinecraftClient.getInstance().keyboard.getClipboard();
         if (var4 != null) {
            for (char var8 : var4.replace("\r", "").replace('\n', ' ').replace('\t', ' ').toCharArray()) {
               var3.charTyped(var8);
            }
         }

         return true;
      } else {
         var3.keyPressed(var1, var2);
         return true;
      }
   }

   public boolean charTyped(char var1) {
      FormField var2 = this.focused();
      return var2 != null && var2.charTyped(var1);
   }

   public void blurAll(double var1, double var3) {
      for (FormField var6 : this.fields()) {
         BoundsData var7 = var6.bounds();
         if (var6.isFocused() && (var7 == null || !var7.contains(var1, var3))) {
            var6.setFocused(false);
         }
      }

      this.commitFields();
   }

   private static final class CachedLayout {
      private final String text;
      private final boolean streaming;
      private final boolean note;
      private final String error;
      private final String result;
      private final int images;
      private final float width;
      private final boolean lastOne;
      private final ScriptAiTab.Layout layout;

      public String text() {
         return this.text;
      }

      public boolean streaming() {
         return this.streaming;
      }

      public boolean note() {
         return this.note;
      }

      public String error() {
         return this.error;
      }

      public String result() {
         return this.result;
      }

      public int images() {
         return this.images;
      }

      public float width() {
         return this.width;
      }

      public boolean lastOne() {
         return this.lastOne;
      }

      public ScriptAiTab.Layout layout() {
         return this.layout;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CachedLayout other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.streaming, other.streaming) && java.util.Objects.equals(this.note, other.note) && java.util.Objects.equals(this.error, other.error) && java.util.Objects.equals(this.result, other.result) && java.util.Objects.equals(this.images, other.images) && java.util.Objects.equals(this.width, other.width) && java.util.Objects.equals(this.lastOne, other.lastOne) && java.util.Objects.equals(this.layout, other.layout);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, streaming, note, error, result, images, width, lastOne, layout);
      }

      @Override
      public String toString() {
         return "CachedLayout[" + "text=" + this.text + ", " + "streaming=" + this.streaming + ", " + "note=" + this.note + ", " + "error=" + this.error + ", " + "result=" + this.result + ", " + "images=" + this.images + ", " + "width=" + this.width + ", " + "lastOne=" + this.lastOne + ", " + "layout=" + this.layout + "]";
      }

      private CachedLayout(String var1, boolean var2, boolean var3, String var4, String var5, int var6, float var7, boolean var8, ScriptAiTab.Layout var9) {
         this.text = var1;
         this.streaming = var2;
         this.note = var3;
         this.error = var4;
         this.result = var5;
         this.images = var6;
         this.width = var7;
         this.lastOne = var8;
         this.layout = var9;
      }

      public boolean matches(AiChatStorage.Entry2 var1, String var2, String var3, float var4, boolean var5) {
         return (this.text == var2 || this.text.equals(var2))
            && this.streaming == var1.streaming()
            && this.note == var1.note()
            && this.error.equals(var3)
            && this.result.equals(var1.result())
            && this.images == var1.getItems().size()
            && this.width == var4
            && this.lastOne == var5;
      }
   }

   private static enum KeyState {
      MISSING("gui.scripts.ai.provider.nokey"),
      SAVED("gui.scripts.ai.provider.ready"),
      NOT_NEEDED("gui.scripts.ai.provider.local");

      private final String description;

      private KeyState(String var3) {
         this.description = var3;
      }
   }

   private static final class Layout {
      private final List<ScriptAiTab.Piece> pieces;
      private final List<List<String>> wrapped;
      private final float height;
      private final float bubble;

      public List<ScriptAiTab.Piece> pieces() {
         return this.pieces;
      }

      public List<List<String>> wrapped() {
         return this.wrapped;
      }

      public float height() {
         return this.height;
      }

      public float bubble() {
         return this.bubble;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Layout other)) return false;
         return java.util.Objects.equals(this.pieces, other.pieces) && java.util.Objects.equals(this.wrapped, other.wrapped) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.bubble, other.bubble);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(pieces, wrapped, height, bubble);
      }

      @Override
      public String toString() {
         return "Layout[" + "pieces=" + this.pieces + ", " + "wrapped=" + this.wrapped + ", " + "height=" + this.height + ", " + "bubble=" + this.bubble + "]";
      }

      private Layout(List<ScriptAiTab.Piece> var1, List<List<String>> var2, float var3, float var4) {
         this.pieces = var1;
         this.wrapped = var2;
         this.height = var3;
         this.bubble = var4;
      }
   }

   private static final class Piece {
      private final int kind;
      private final String text;
      private final String extra;
      private final int lines;

      public int kind() {
         return this.kind;
      }

      public String text() {
         return this.text;
      }

      public String extra() {
         return this.extra;
      }

      public int lines() {
         return this.lines;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Piece other)) return false;
         return java.util.Objects.equals(this.kind, other.kind) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.extra, other.extra) && java.util.Objects.equals(this.lines, other.lines);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(kind, text, extra, lines);
      }

      @Override
      public String toString() {
         return "Piece[" + "kind=" + this.kind + ", " + "text=" + this.text + ", " + "extra=" + this.extra + ", " + "lines=" + this.lines + "]";
      }

      private Piece(int var1, String var2, String var3, int var4) {
         this.kind = var1;
         this.text = var2;
         this.extra = var3;
         this.lines = var4;
      }
   }
}

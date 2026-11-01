package org.zenith.utility.misc;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;

public final class ScriptHeaderParser {
   public static final List<String> items = List.of("empty", "hud", "esp", "combat", "chat", "fun");

   private ScriptHeaderParser() {
   }

   public static String method1000(String var0, String var1) {
      String var2 = "// @name " + var1 + "\n// @author " + author() + "\n// @version 1.0\n// @description \n\n";
      String var3 = var0 == null ? "empty" : var0.toLowerCase(Locale.ROOT);

      return var2
         + switch (var3) {
            case "fun" -> "var mod = Module(\"%s\", { category: \"render\", enabled: true, description: \"звук и искры на ударе\" });\nvar volume = mod.slider(\"Громкость\", 1, 0, 2, 0.1);\nvar symbol = mod.text(\"Символ\", \"★\");\nvar color = mod.color(\"Цвет\", Color.accent());\n\nevents.attack(function (e) {\n    if (!mod.enabled() || e.stage !== \"pre\" || !e.target) return;\n    var t = e.target;\n    sound.play3d(\"entity.player.attack.crit\", t.x(), t.y() + 1, t.z(), volume.get(), 0.8 + Math.random() * 0.4);\n    particles.burst(symbol.get(), t.x(), t.y() + t.height() * 0.7, t.z(), 10, 4, { color: color.get(), size: 0.4 });\n});\n"
               .formatted(var1);
            case "hud" -> "var mod = Module(\"%s HUD\", { category: \"render\", enabled: true });\nvar color = mod.color(\"Цвет\", 0xFF8AB4FF);\nvar size = mod.slider(\"Размер\", 7, 5, 14, 0.5);\n\nevents.render2d(function (e) {\n    if (!mod.enabled() || !player.exists()) return;\n    var text = \"FPS \" + mc.fps() + \"  HP \" + player.health().toFixed(1);\n    var w = render.textWidth(text, size.get()) + 10;\n    render.blur(6, 6, w, size.get() + 8, 4);\n    render.text(text, 11, 10, color.get(), size.get());\n});\n"
               .formatted(var1);
            case "esp" -> "var mod = Module(\"%s ESP\", { category: \"render\" });\nvar range = mod.slider(\"Дальность\", 64, 8, 128, 1);\nvar friendColor = mod.color(\"Друзья\", 0xFF55FF55);\nvar enemyColor = mod.color(\"Остальные\", 0xFFFF5555);\n\nevents.render3d(function (e) {\n    if (!mod.enabled()) return;\n    world.players().forEach(function (p) {\n        if (p.distance() > range.get()) return;\n        var c = p.isFriend() ? friendColor.get() : enemyColor.get();\n        render3d.entityBox(p, Color.alpha(c, 0.15), c);\n    });\n});\n"
               .formatted(var1);
            case "combat" -> "var mod = Module(\"%s\", { category: \"combat\" });\nvar range = mod.slider(\"Дистанция\", 3.0, 2.0, 4.5, 0.1);\nvar onlyPlayers = mod.checkbox(\"Только игроки\", true);\n\nfunction target() {\n    var best = null;\n    world.entities(true, range.get() + 1).forEach(function (e) {\n        if (!e.alive() || e.isFriend() || (onlyPlayers.get() && !e.isPlayer())) return;\n        if (e.distance() > range.get()) return;\n        if (!best || e.distance() < best.distance()) best = e;\n    });\n    return best;\n}\n\nevents.update(function () {\n    if (!mod.enabled() || client.screenOpen()) return;\n    var t = target();\n    if (!t) return;\n    var box = t.box();\n    rotation.lookAt(t.x(), t.y() + t.height() * 0.6, t.z(), { speed: 40 });\n    if (player.attackCooldown() >= 0.95) player.attack(t);\n});\n"
               .formatted(var1);
            case "chat" -> "var mod = Module(\"%s\", { category: \"misc\" });\nvar word = mod.text(\"Слово\", \"привет\");\n\nevents.chat(function (e) {\n    if (!mod.enabled()) return;\n    if (e.text.toLowerCase().indexOf(word.get().toLowerCase()) >= 0) {\n        client.info(\"В чате: \" + e.text);\n    }\n});\n\ncommands.register(\"hello\", function (args) {\n    client.msg(\"Привет из скрипта! Аргументы: \" + args);\n});\n"
               .formatted(var1);
            default -> "var mod = Module(\"%s\");\nvar enabledText = mod.checkbox(\"Показывать текст\", true);\n\nevents.render2d(function (e) {\n    if (!mod.enabled() || !enabledText.get()) return;\n    render.text(\"Привет из скрипта\", 8, 8, Color.accent(), 8);\n});\n\nevents.enable(function () {\n    print(\"скрипт запущен\");\n});\n"
               .formatted(var1);
         };
   }

   private static String author() {
      try {
         return MinecraftClient.getInstance().getSession().getUsername();
      } catch (RuntimeException var1) {
         return "";
      }
   }
}

package org.zenith.client.scripting;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFileFormat.Type;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.MediaSessionManager;
import org.zenith.utility.misc.QrCode;
import org.zenith.utility.misc.ScriptPermissions;

public final class ScriptMediaApi {
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private static final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   private static int[] intValues;

   private ScriptMediaApi() {
   }

   public static Scriptable method1736(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274(
         "current",
         var1x -> {
            MediaSessionManager var2 = MediaSessionManager.getInstance();
            return !var2.active()
               ? null
               : ScriptValueUtils.method6247(var0)
                  .method6277("title", var2.title())
                  .method6277("artist", var2.method6006())
                  .method6277("playing", var2.method6007())
                  .method6277("position", var2.method6009())
                  .method6277("duration", var2.method6008())
                  .getScriptableObject();
         }
      );
      var1.method6274("playing", var0x -> MediaSessionManager.getInstance().active() && MediaSessionManager.getInstance().method6007());
      var1.method6274("toggle", var0x -> {
         MediaSessionManager.getInstance().playPause();
         return null;
      });
      var1.method6274("next", var0x -> {
         MediaSessionManager.getInstance().next();
         return null;
      });
      var1.method6274("previous", var0x -> {
         MediaSessionManager.getInstance().previous();
         return null;
      });
      var1.method6274("seek", var0x -> {
         MediaSessionManager.getInstance().method6019(Math.max(0.0, ScriptValueUtils.method6256(var0x, 0, 0.0)));
         return null;
      });
      return var1.getScriptableObject();
   }

   private static Scriptable method1737(Script var0, Storage var1, Scriptable var2, BufferedImage var3) {
      int var4 = Math.min(2048, var3.getWidth());
      int var5 = Math.min(2048, var3.getHeight());
      NativeImage var6 = new NativeImage(var4, var5, false);
      int[] var7 = new int[var4];

      for (int var8 = 0; var8 < var5; var8++) {
         var3.getRGB(0, var8, var4, 1, var7, 0, var4);

         for (int var9 = 0; var9 < var4; var9++) {
            var6.setColorArgb(var9, var8, var7[var9]);
         }
      }

      return RemoteUtil.method1836(var2, var1, RemoteUtil.method1829(var0, List.of(var6), List.of(0)));
   }

   public static void method1738(Script var0, Storage var1, Scriptable var2, ScriptableObject var3) {
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6246(var2, var3);
      var4.method6274("qr", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");
         if (var4x.isEmpty()) {
            throw ScriptValueUtils.method6251("image.qr: пустой текст");
         } else {
            Scriptable var5 = ScriptValueUtils.method6262(var3x, 1);
            int var6 = var5 == null ? 4 : (int)Math.max(1.0, Math.min(16.0, ScriptValueUtils.method6264(var5, "scale", 4.0)));
            int var7 = var5 == null ? 4 : (int)Math.max(0.0, Math.min(8.0, ScriptValueUtils.method6264(var5, "margin", 4.0)));
            int var8 = var5 == null ? -16777216 : (int)((long)ScriptValueUtils.method6264(var5, "color", 4.27819E9F));
            int var9 = var5 == null ? -1 : (int)((long)ScriptValueUtils.method6264(var5, "background", 4.294967295E9));

            boolean[][] var10;
            try {
               var10 = QrCode.method3020(var4x.getBytes(StandardCharsets.UTF_8));
            } catch (IllegalArgumentException var18) {
               throw ScriptValueUtils.method6251(var18.getMessage());
            }

            int var11 = (var10.length + var7 * 2) * var6;
            NativeImage var12 = new NativeImage(var11, var11, false);

            for (int var13 = 0; var13 < var11; var13++) {
               for (int var14 = 0; var14 < var11; var14++) {
                  int var15 = var14 / var6 - var7;
                  int var16 = var13 / var6 - var7;
                  boolean var17 = var15 >= 0 && var16 >= 0 && var15 < var10.length && var16 < var10.length && var10[var16][var15];
                  var12.setColorArgb(var14, var13, var17 ? var8 : var9);
               }
            }

            return RemoteUtil.method1836(var2, var1, RemoteUtil.method1829(var0, List.of(var12), List.of(0)));
         }
      });
      var4.method6274("text", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");
         if (!var4x.isEmpty() && var4x.length() <= 2000) {
            Scriptable var5 = ScriptValueUtils.method6262(var3x, 1);
            int var6 = var5 == null ? 32 : (int)Math.max(6.0, Math.min(160.0, ScriptValueUtils.method6264(var5, "size", 32.0)));
            int var7 = var5 == null ? -1 : (int)((long)ScriptValueUtils.method6264(var5, "color", 4.294967295E9));
            int var8 = var5 == null ? 0 : (int)((long)ScriptValueUtils.method6264(var5, "background", 0.0));
            int var9 = var5 == null ? 4 : (int)Math.max(0.0, Math.min(64.0, ScriptValueUtils.method6264(var5, "padding", 4.0)));
            boolean var10 = var5 != null && ScriptValueUtils.method6265(var5, "bold", false);
            String var10000;
            if (var5 == null) {
               var10000 = "SansSerif";
            } else {
               String var12 = ScriptValueUtils.method6266(var5, "font", "sans").toLowerCase(Locale.ROOT);
               switch (var12) {
                  case "serif":
                     var10000 = "Serif";
                     break;
                  case "mono":
                  case "monospace":
                     var10000 = "Monospaced";
                     break;
                  default:
                     var10000 = "SansSerif";
               }
            }

            String var11 = var10000;
            Font var22 = new Font(var11, var10 ? 1 : 0, var6);
            BufferedImage var23 = new BufferedImage(1, 1, 2);
            Graphics2D var14 = var23.createGraphics();
            var14.setFont(var22);
            FontMetrics var15 = var14.getFontMetrics();
            String[] var16 = var4x.split("\n");
            int var17 = 1;

            for (String var21 : var16) {
               var17 = Math.max(var17, var15.stringWidth(var21));
            }

            int var25 = var15.getHeight() * var16.length;
            var14.dispose();
            var17 = Math.min(2048, var17 + var9 * 2);
            var25 = Math.min(2048, var25 + var9 * 2);
            BufferedImage var27 = new BufferedImage(var17, var25, 2);
            Graphics2D var28 = var27.createGraphics();
            var28.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            var28.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (var8 >>> 24 != 0) {
               var28.setColor(new Color(var8, true));
               var28.fillRect(0, 0, var17, var25);
            }

            var28.setFont(var22);
            var28.setColor(new Color(var7, true));

            for (int var29 = 0; var29 < var16.length; var29++) {
               var28.drawString(var16[var29], var9, var9 + var15.getAscent() + var29 * var15.getHeight());
            }

            var28.dispose();
            return method1737(var0, var1, var2, var27);
         } else {
            throw ScriptValueUtils.method6251("image.text: от 1 до 2000 символов");
         }
      });
      var4.method6274("mapArt", var3x -> {
         RemoteUtil.Entry var4x = RemoteUtil.method1827(ScriptValueUtils.arg(var3x, 0));
         NativeImage var5 = var4x.method1848();
         int[] var6 = method1739();
         NativeImage var7 = new NativeImage(128, 128, false);

         for (int var8 = 0; var8 < 128; var8++) {
            for (int var9 = 0; var9 < 128; var9++) {
               int var10 = Math.min(var5.getWidth() - 1, var9 * var5.getWidth() / 128);
               int var11 = Math.min(var5.getHeight() - 1, var8 * var5.getHeight() / 128);
               int var12 = var5.getColorArgb(var10, var11);
               if (var12 >>> 24 < 128) {
                  var7.setColorArgb(var9, var8, 0);
               } else {
                  int var13 = var6[0];
                  long var14 = Long.MAX_VALUE;

                  for (int var19 : var6) {
                     int var20 = (var12 >> 16 & 0xFF) - (var19 >> 16 & 0xFF);
                     int var21 = (var12 >> 8 & 0xFF) - (var19 >> 8 & 0xFF);
                     int var22 = (var12 & 0xFF) - (var19 & 0xFF);
                     long var23 = 3L * var20 * var20 + 4L * var21 * var21 + 2L * var22 * var22;
                     if (var23 < var14) {
                        var14 = var23;
                        var13 = var19;
                     }
                  }

                  var7.setColorArgb(var9, var8, var13 | 0xFF000000);
               }
            }
         }

         return RemoteUtil.method1836(var2, var1, RemoteUtil.method1829(var0, List.of(var7), List.of(0)));
      });
   }

   private static synchronized int[] method1739() {
      if (intValues != null) {
         return intValues;
      } else {
         ArrayList var0 = new ArrayList();

         for (int var1 = 1; var1 < 64; var1++) {
            MapColor var2 = MapColor.get(var1);
            if (var2 != null && var2.color != 0 && var2.id == var1) {
               for (int var3 = 0; var3 < 4; var3++) {
                  var0.add(MapColor.getRenderColor(var1 * 4 + var3));
               }
            }
         }

         intValues = var0.stream().mapToInt(x -> (Integer)x).toArray();
         return intValues;
      }
   }

   public static void method1740(Script var0, Storage var1, Scriptable var2, ScriptableObject var3) {
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6246(var2, var3);
      var4.method6274(
         "tts",
         var2x -> {
            ScriptPermissions.method4(var0, "tts");
            String var3x = ScriptValueUtils.method6254(var2x, 0, "").replaceAll("[\\p{Cntrl}]", " ").strip();
            if (var3x.isEmpty() || var3x.length() > 500) {
               throw ScriptValueUtils.method6251("sound.tts: от 1 до 500 символов");
            } else if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
               throw ScriptValueUtils.method6251("озвучка работает только на Windows");
            } else if (!atomicBoolean.compareAndSet(false, true)) {
               return false;
            } else {
               Scriptable var4x = ScriptValueUtils.method6262(var2x, 1);
               int var5 = var4x == null ? 0 : (int)Math.max(-10.0, Math.min(10.0, ScriptValueUtils.method6264(var4x, "rate", 0.0)));
               int var6 = var4x == null ? 100 : (int)Math.max(0.0, Math.min(100.0, ScriptValueUtils.method6264(var4x, "volume", 100.0)));
               var var7 = var4x == null ? null : (ScriptValueUtils.method6263(var4x, "onDone") instanceof Function var8 ? var8 : null);
               Thread.ofPlatform()
                  .daemon(true)
                  .name("zenith-script-tts")
                  .start(
                     () -> {
                        try {
                           ProcessBuilder var6x = new ProcessBuilder(
                              "powershell",
                              "-NoProfile",
                              "-NonInteractive",
                              "-ExecutionPolicy",
                              "Bypass",
                              "-Command",
                              "Add-Type -AssemblyName System.Speech; $s = New-Object System.Speech.Synthesis.SpeechSynthesizer; $s.Rate = "
                                 + var5
                                 + "; $s.Volume = "
                                 + var6
                                 + "; $s.Speak($env:ZENITH_TTS_TEXT)"
                           );
                           var6x.environment().put("ZENITH_TTS_TEXT", var3x);
                           var6x.redirectErrorStream(true);
                           Process var7x = var6x.start();
                           var7x.getInputStream().readAllBytes();
                           if (!var7x.waitFor(60L, TimeUnit.SECONDS)) {
                              var7x.destroyForcibly();
                           }
                        } catch (Exception var11) {
                        } finally {
                           atomicBoolean.set(false);
                           if (var7 != null) {
                              RemoteUtil.method1847(var1, var0, var7);
                           }
                        }
                     }
                  );
               return true;
            }
         }
      );
      var4.method6274("speaking", var0x -> atomicBoolean.get());
      var4.method6274("record", var2x -> {
         ScriptPermissions.method4(var0, "microphone");
         if (var0.getPath() == null) {
            throw ScriptValueUtils.method6251("запись доступна только скриптам-папкам");
         } else {
            long var3x = (long)Math.max(200.0, Math.min(30000.0, ScriptValueUtils.method6256(var2x, 0, 3000.0)));
            var var5 = ScriptValueUtils.method6260(var2x, 1);
            if (!atomicBoolean2.compareAndSet(false, true)) {
               throw ScriptValueUtils.method6251("уже идёт запись");
            } else {
               HudHolder.method6969().method6973("d", Text.literal(var0.method1403() + ": запись микрофона " + var3x / 1000.0 + " с"), Math.max(1500L, var3x));
               Thread.ofPlatform().daemon(true).name("zenith-script-record").start(() -> {
                  String var5x = null;
                  String var6 = null;
                  AudioFormat var7 = new AudioFormat(44100.0F, 16, 1, true, false);

                  try (TargetDataLine var8 = AudioSystem.getTargetDataLine(var7)) {
                     var8.open(var7);
                     var8.start();
                     ByteArrayOutputStream var9 = new ByteArrayOutputStream();
                     byte[] var10 = new byte[4096];
                     long var11 = System.currentTimeMillis() + var3x;

                     while (System.currentTimeMillis() < var11 && var0.isRunning()) {
                        int var13 = var8.read(var10, 0, var10.length);
                        if (var13 > 0) {
                           var9.write(var10, 0, var13);
                        }
                     }

                     var8.stop();
                     byte[] var34 = var9.toByteArray();
                     Path var14 = var0.getPath().resolve("sounds");
                     Files.createDirectories(var14);
                     String var15 = "rec-" + System.currentTimeMillis() + ".wav";

                     try (AudioInputStream var16 = new AudioInputStream(new ByteArrayInputStream(var34), var7, var34.length / var7.getFrameSize())) {
                        AudioSystem.write(var16, Type.WAVE, var14.resolve(var15).toFile());
                     }

                     var6 = "sounds/" + var15;
                  } catch (Exception var30) {
                     var5x = var30.getMessage() == null ? var30.getClass().getSimpleName() : var30.getMessage();
                  } finally {
                     atomicBoolean2.set(false);
                  }

                  String var32 = var6;
                  String var33 = var5x;
                  MinecraftClient.getInstance().execute(() -> {
                     if (var0.isRunning()) {
                        var1.method1321(var0, var5, var32, var33);
                     }
                  });
               });
               return null;
            }
         }
      });
      var4.method6274("recording", var0x -> atomicBoolean2.get());
   }
}

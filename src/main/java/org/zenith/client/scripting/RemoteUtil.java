package org.zenith.client.scripting;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.zenith.utility.config.ScriptSoundLoader;
import org.zenith.utility.misc.ImageLoader;
import org.zenith.utility.misc.ScriptPermissions;

public final class RemoteUtil {
   public static final int CONST_intValue = 2048;
   public static final int CONST_intValue2 = 64;
   public static final long CONST_longValue = 25165824L;
   public static final int CONST_intValue3 = 120;
   private static final AtomicInteger atomicInteger = new AtomicInteger();
   private static final Map<Script, List<RemoteUtil.Entry>> items = new HashMap<>();
   private static final AtomicInteger atomicInteger2 = new AtomicInteger();
   private static final Map<Integer, RemoteUtil.Entry> items2 = new ConcurrentHashMap<>();

   private RemoteUtil() {
   }

   public static synchronized List<RemoteUtil.Entry> method1826(Script var0) {
      return items.computeIfAbsent(var0, var0x -> new ArrayList<>());
   }

   public static RemoteUtil.Entry method1827(Object var0) {
      if (var0 instanceof Scriptable var1 && var1.get("__img", var1) instanceof Number var3) {
         RemoteUtil.Entry var4 = items2.get(var3.intValue());
         if (var4 != null && !var4.flag) {
            return var4;
         } else {
            throw ScriptValueUtils.method6251("картинка уже освобождена");
         }
      } else {
         throw ScriptValueUtils.method6251("ожидалась картинка (объект из image.load / image.create)");
      }
   }

   private static void method1828(Script var0, long var1) {
      List<RemoteUtil.Entry> var3 = method1826(var0);
      if (var3.size() >= 64) {
         throw ScriptValueUtils.method6251("у скрипта больше 64 картинок — освободи лишние через img.release()");
      } else {
         long var4 = 0L;

         for (Object var7_o : (Iterable<?>)(Object)(var3)) {
            RemoteUtil.Entry var7 = (RemoteUtil.Entry)var7_o;
            var4 += var7.method1849();
         }

         if (var4 + var1 > 25165824L) {
            throw ScriptValueUtils.method6251("картинки скрипта занимают слишком много памяти");
         }
      }
   }

   public static RemoteUtil.Entry method1829(Script var0, List<NativeImage> var1, List<Integer> var2) {
      long var3 = 0L;

      for (NativeImage var6 : var1) {
         var3 += (long)var6.getWidth() * var6.getHeight();
      }

      try {
         method1828(var0, var3);
      } catch (RuntimeException var8) {
         for (NativeImage var7 : var1) {
            var7.close();
         }

         throw var8;
      }

      RemoteUtil.Entry var9 = new RemoteUtil.Entry(var0);
      var9.items.addAll(var1);
      var9.items2.addAll(var2);
      method1826(var0).add(var9);
      items2.put(var9.intValue, var9);
      return var9;
   }

   public static void method1830(Script var0) {
      List var1;
      synchronized (RemoteUtil.class) {
         var1 = items.remove(var0);
      }

      if (var1 != null) {
         for (Object var3_o : (Iterable<?>)(Object)(new ArrayList(var1))) {
            RemoteUtil.Entry var3 = (RemoteUtil.Entry)var3_o;
            items2.remove(var3.intValue);
            MinecraftClient var4 = MinecraftClient.getInstance();

            for (Identifier var6 : var3.items3) {
               try {
                  var4.getTextureManager().destroyTexture(var6);
               } catch (RuntimeException var8) {
               }
            }

            if (var3.items4.isEmpty()) {
               for (NativeImage var12 : var3.items) {
                  var12.close();
               }
            }

            var3.flag = true;
         }
      }
   }

   private static NativeImage method1831(BufferedImage var0) {
      int var1 = Math.min(2048, var0.getWidth());
      int var2 = Math.min(2048, var0.getHeight());
      int[] var3 = new int[var1];
      NativeImage var4 = new NativeImage(var1, var2, false);

      for (int var5 = 0; var5 < var2; var5++) {
         var0.getRGB(0, var5, var1, 1, var3, 0, var1);

         for (int var6 = 0; var6 < var1; var6++) {
            var4.setColorArgb(var6, var5, var3[var6]);
         }
      }

      return var4;
   }

   public static List<Object[]> method1832(byte[] var0, String var1) throws IOException {
      ArrayList<Object[]> var2 = new ArrayList<>();
      if (var1.toLowerCase(Locale.ROOT).endsWith(".gif")) {
         try (ImageInputStream var34 = ImageIO.createImageInputStream(new ByteArrayInputStream(var0))) {
            var var4 = ImageIO.getImageReadersByFormatName("gif");
            if (!var4.hasNext()) {
               throw new IOException("gif не поддерживается");
            }

            ImageReader var5 = (ImageReader)var4.next();

            try {
               var5.setInput(var34, false, false);
               int var6 = var5.getWidth(0);
               int var7 = var5.getHeight(0);
               IIOMetadata var8 = var5.getStreamMetadata();
               if (var8 != null) {
                  Node var9 = var8.getAsTree("javax_imageio_gif_stream_1.0");

                  for (Node var10 = var9.getFirstChild(); var10 != null; var10 = var10.getNextSibling()) {
                     if ("LogicalScreenDescriptor".equals(var10.getNodeName())) {
                        NamedNodeMap var11 = var10.getAttributes();
                        var6 = Math.max(1, Integer.parseInt(var11.getNamedItem("logicalScreenWidth").getNodeValue()));
                        var7 = Math.max(1, Integer.parseInt(var11.getNamedItem("logicalScreenHeight").getNodeValue()));
                     }
                  }
               }

               ImageLoader.method233(var6, var7);
               int var35 = Math.min(2048, var6);
               int var36 = Math.min(2048, var7);
               long var37 = (long)var35 * var36;
               int var13 = (int)Math.max(1L, Math.min(120L, 25165824L / Math.max(1L, var37)));
               BufferedImage var14 = new BufferedImage(var35, var36, 2);

               for (int var15 = 0; var15 < var13; var15++) {
                  int var16;
                  int var17;
                  try {
                     var16 = var5.getWidth(var15);
                     var17 = var5.getHeight(var15);
                  } catch (IndexOutOfBoundsException var31) {
                     break;
                  }

                  ImageLoader.method233(var16, var17);
                  if (var16 > var6 * 2 || var17 > var7 * 2) {
                     throw new IOException("повреждённый gif");
                  }

                  int var18 = 0;
                  int var19 = 0;
                  int var20 = 100;
                  IIOMetadata var21 = var5.getImageMetadata(var15);
                  Node var22 = var21.getAsTree("javax_imageio_gif_image_1.0");

                  for (Node var23 = var22.getFirstChild(); var23 != null; var23 = var23.getNextSibling()) {
                     NamedNodeMap var24 = var23.getAttributes();
                     if ("ImageDescriptor".equals(var23.getNodeName())) {
                        var18 = Integer.parseInt(var24.getNamedItem("imageLeftPosition").getNodeValue());
                        var19 = Integer.parseInt(var24.getNamedItem("imageTopPosition").getNodeValue());
                     } else if ("GraphicControlExtension".equals(var23.getNodeName())) {
                        var20 = Math.max(20, Integer.parseInt(var24.getNamedItem("delayTime").getNodeValue()) * 10);
                     }
                  }

                  BufferedImage var38 = var5.read(var15);
                  Graphics2D var39 = var14.createGraphics();
                  var39.drawImage(var38, var18, var19, null);
                  var39.dispose();
                  var2.add(new Object[]{method1831(var14), var20});
               }
            } finally {
               var5.dispose();
            }
         }

         if (var2.isEmpty()) {
            throw new IOException("пустой gif");
         }
      } else {
         BufferedImage var3 = ImageLoader.method234(var0, 2048);
         var2.add(new Object[]{method1831(var3), 0});
      }

      return var2;
   }

   public static RemoteUtil.Entry method1833(Script var0, byte[] var1, String var2) throws IOException {
      List<Object[]> var3 = method1832(var1, var2);
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();

      for (Object var7_o : (Iterable<?>)(Object)(var3)) {
         Object[] var7 = (Object[])var7_o;
         var4.add((NativeImage)var7[0]);
         var5.add((Integer)var7[1]);
      }

      return method1829(var0, var4, var5);
   }

   public static Path method1834(Script var0, String var1) {
      if (var0.getPath() == null) {
         throw ScriptValueUtils.method6251("файлы доступны только скриптам-папкам");
      } else {
         Path var2 = var0.getPath().toAbsolutePath().normalize();
         Path var3 = var2.resolve(var1).toAbsolutePath().normalize();
         if (!var3.startsWith(var2)) {
            throw ScriptValueUtils.method6251("путь вне папки скрипта");
         } else {
            return var3;
         }
      }
   }

   private static void method1835(Storage var0, Script var1, Function var2, Supplier<Object[]> var3) {
      MinecraftClient.getInstance().execute(() -> {
         if (var1.isRunning() && var2 != null) {
            Scriptable var4 = ScriptScreenApi.method7044(var1);
            if (var4 != null) {
               var0.method1321(var1, var2, (Object[])var3.get());
            }
         }
      });
   }

   public static Scriptable method1836(Scriptable var0, Storage var1, RemoteUtil.Entry var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var0);
      var3.method6277("__img", (double)var2.intValue);
      var3.method6274("width", var1x -> (double)var2.width());
      var3.method6274("height", var1x -> (double)var2.height());
      var3.method6274("frames", var1x -> (double)var2.items.size());
      var3.method6274("animated", var1x -> var2.items.size() > 1);
      var3.method6274("get", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         return var2x >= 0 && var3x >= 0 && var2x < var2.width() && var3x < var2.height() ? (double)var2.method1848().getColorArgb(var2x, var3x) : 0.0;
      });
      var3.method6274("set", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         if (var2x >= 0 && var3x >= 0 && var2x < var2.width() && var3x < var2.height()) {
            var2.method1848().setColorArgb(var2x, var3x, ScriptValueUtils.method6258(var1x, 2, -1));
            var2.dirty = true;
         }

         return null;
      });
      var3.method6274("fill", var1x -> {
         int var2x = ScriptValueUtils.method6258(var1x, 0, 0);
         method1837(var2, 0, 0, var2.width(), var2.height(), var2x);
         return null;
      });
      var3.method6274(
         "rect",
         var1x -> {
            method1837(
               var2,
               ScriptValueUtils.method6257(var1x, 0, 0),
               ScriptValueUtils.method6257(var1x, 1, 0),
               ScriptValueUtils.method6257(var1x, 2, 0),
               ScriptValueUtils.method6257(var1x, 3, 0),
               ScriptValueUtils.method6258(var1x, 4, -1)
            );
            return null;
         }
      );
      var3.method6274("circle", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         int var4 = Math.max(0, ScriptValueUtils.method6257(var1x, 2, 1));
         int var5 = ScriptValueUtils.method6258(var1x, 3, -1);

         for (int var6 = Math.max(0, var3x - var4); var6 <= Math.min(var2.height() - 1, var3x + var4); var6++) {
            for (int var7 = Math.max(0, var2x - var4); var7 <= Math.min(var2.width() - 1, var2x + var4); var7++) {
               if ((var7 - var2x) * (var7 - var2x) + (var6 - var3x) * (var6 - var3x) <= var4 * var4) {
                  method1838(var2.method1848(), var7, var6, var5);
               }
            }
         }

         var2.dirty = true;
         return null;
      });
      var3.method6274("line", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         int var4 = ScriptValueUtils.method6257(var1x, 2, 0);
         int var5 = ScriptValueUtils.method6257(var1x, 3, 0);
         int var6 = ScriptValueUtils.method6258(var1x, 4, -1);
         int var7 = Math.abs(var4 - var2x);
         int var8 = -Math.abs(var5 - var3x);
         int var9 = var2x < var4 ? 1 : -1;
         int var10 = var3x < var5 ? 1 : -1;
         int var11 = var7 + var8;

         for (int var12 = 0; var12 < 8192; var12++) {
            if (var2x >= 0 && var3x >= 0 && var2x < var2.width() && var3x < var2.height()) {
               method1838(var2.method1848(), var2x, var3x, var6);
            }

            if (var2x == var4 && var3x == var5) {
               break;
            }

            int var13 = 2 * var11;
            if (var13 >= var8) {
               var11 += var8;
               var2x += var9;
            }

            if (var13 <= var7) {
               var11 += var7;
               var3x += var10;
            }
         }

         var2.dirty = true;
         return null;
      });
      var3.method6274("draw", var1x -> {
         RemoteUtil.Entry var2x = method1827(ScriptValueUtils.arg(var1x, 0));
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         int var4 = ScriptValueUtils.method6257(var1x, 2, 0);
         int var5 = ScriptValueUtils.method6257(var1x, 3, var2x.width());
         int var6 = ScriptValueUtils.method6257(var1x, 4, var2x.height());

         for (int var7 = 0; var7 < var6; var7++) {
            for (int var8 = 0; var8 < var5; var8++) {
               int var9 = var3x + var8;
               int var10 = var4 + var7;
               if (var9 >= 0 && var10 >= 0 && var9 < var2.width() && var10 < var2.height()) {
                  int var11 = var8 * var2x.width() / Math.max(1, var5);
                  int var12 = var7 * var2x.height() / Math.max(1, var6);
                  method1838(var2.method1848(), var9, var10, var2x.method1848().getColorArgb(var11, var12));
               }
            }
         }

         var2.dirty = true;
         return null;
      });
      var3.method6274("filter", var1x -> {
         method1839(var2, ScriptValueUtils.method6254(var1x, 0, "grayscale").toLowerCase(Locale.ROOT), ScriptValueUtils.method6256(var1x, 1, 1.0));
         var2.dirty = true;
         return null;
      });
      var3.method6274("crop", var3x -> {
         int var4 = Math.max(0, ScriptValueUtils.method6257(var3x, 0, 0));
         int var5 = Math.max(0, ScriptValueUtils.method6257(var3x, 1, 0));
         int var6 = Math.max(1, Math.min(var2.width() - var4, ScriptValueUtils.method6257(var3x, 2, var2.width())));
         int var7 = Math.max(1, Math.min(var2.height() - var5, ScriptValueUtils.method6257(var3x, 3, var2.height())));
         NativeImage var8 = new NativeImage(var6, var7, false);

         for (int var9 = 0; var9 < var7; var9++) {
            for (int var10 = 0; var10 < var6; var10++) {
               var8.setColorArgb(var10, var9, var2.method1848().getColorArgb(var4 + var10, var5 + var9));
            }
         }

         return method1836(var0, var1, method1829(var2.script, List.of(var8), List.of(0)));
      });
      var3.method6274("resize", var3x -> {
         int var4 = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 0, var2.width())));
         int var5 = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 1, var2.height())));
         NativeImage var6 = new NativeImage(var4, var5, false);

         for (int var7 = 0; var7 < var5; var7++) {
            for (int var8 = 0; var8 < var4; var8++) {
               var6.setColorArgb(var8, var7, var2.method1848().getColorArgb(var8 * var2.width() / var4, var7 * var2.height() / var5));
            }
         }

         return method1836(var0, var1, method1829(var2.script, List.of(var6), List.of(0)));
      });
      var3.method6274("copy", var3x -> {
         NativeImage var4 = new NativeImage(var2.width(), var2.height(), false);
         var4.copyFrom(var2.method1848());
         return method1836(var0, var1, method1829(var2.script, List.of(var4), List.of(0)));
      });
      var3.method6274("flip", var1x -> {
         boolean var2x = !ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true);
         NativeImage var3x = var2.method1848();
         int var4 = var3x.getWidth();
         int var5 = var3x.getHeight();

         for (int var6 = 0; var6 < (var2x ? var5 : var5 / 2); var6++) {
            for (int var7 = 0; var7 < (var2x ? var4 / 2 : var4); var7++) {
               int var8 = var2x ? var4 - 1 - var7 : var7;
               int var9 = var2x ? var6 : var5 - 1 - var6;
               int var10 = var3x.getColorArgb(var7, var6);
               int var11 = var3x.getColorArgb(var8, var9);
               var3x.setColorArgb(var7, var6, var11);
               var3x.setColorArgb(var8, var9, var10);
            }
         }

         var2.dirty = true;
         return null;
      });
      var3.method6274("rotate", var3x -> {
         int var4 = (ScriptValueUtils.method6257(var3x, 0, 90) / 90 % 4 + 4) % 4;
         NativeImage var5 = var2.method1848();
         int var6 = var5.getWidth();
         int var7 = var5.getHeight();
         boolean var8 = var4 % 2 == 1;
         NativeImage var9 = new NativeImage(var8 ? var7 : var6, var8 ? var6 : var7, false);

         for (int var10 = 0; var10 < var7; var10++) {
            for (int var11 = 0; var11 < var6; var11++) {
               int var12 = var5.getColorArgb(var11, var10);
               switch (var4) {
                  case 1:
                     var9.setColorArgb(var7 - 1 - var10, var11, var12);
                     break;
                  case 2:
                     var9.setColorArgb(var6 - 1 - var11, var7 - 1 - var10, var12);
                     break;
                  case 3:
                     var9.setColorArgb(var10, var6 - 1 - var11, var12);
                     break;
                  default:
                     var9.setColorArgb(var11, var10, var12);
               }
            }
         }

         return method1836(var0, var1, method1829(var2.script, List.of(var9), List.of(0)));
      });
      var3.method6274("average", var1x -> {
         long var2x = 0L;
         long var4 = 0L;
         long var6 = 0L;
         long var8 = 0L;
         NativeImage var10 = var2.method1848();
         int var11 = Math.max(1, var10.getWidth() * var10.getHeight() / 40000);

         for (int var12 = 0; var12 < var10.getWidth() * var10.getHeight(); var12 += var11) {
            int var13 = var10.getColorArgb(var12 % var10.getWidth(), var12 / var10.getWidth());
            if (var13 >>> 24 >= 16) {
               var2x += var13 >> 16 & 0xFF;
               var4 += var13 >> 8 & 0xFF;
               var6 += var13 & 0xFF;
               var8++;
            }
         }

         return var8 == 0L ? 0.0 : (double)(0xFF000000 | (int)(var2x / var8) << 16 | (int)(var4 / var8) << 8 | (int)(var6 / var8));
      });
      var3.method6274("save", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "image.png");
         if (!var2x.toLowerCase(Locale.ROOT).endsWith(".png")) {
            var2x = var2x + ".png";
         }

         Path var3x = method1834(var2.script, var2x.contains("/") ? var2x : "images/" + var2x);

         try {
            Files.createDirectories(var3x.getParent());
            var2.method1848().writeTo(var3x);
         } catch (IOException var5) {
            throw ScriptValueUtils.method6251("не удалось сохранить: " + var5.getMessage());
         }

         return var2.script.getPath().relativize(var3x).toString().replace('\\', '/');
      });
      var3.method6274("release", var1x -> {
         var2.release();
         return null;
      });
      var2.scriptable = var3.getScriptableObject();
      return var2.scriptable;
   }

   private static void method1837(RemoteUtil.Entry var0, int var1, int var2, int var3, int var4, int var5) {
      for (int var6 = Math.max(0, var2); var6 < Math.min(var0.height(), var2 + var4); var6++) {
         for (int var7 = Math.max(0, var1); var7 < Math.min(var0.width(), var1 + var3); var7++) {
            var0.method1848().setColorArgb(var7, var6, var5);
         }
      }

      var0.dirty = true;
   }

   private static void method1838(NativeImage var0, int var1, int var2, int var3) {
      int var4 = var3 >>> 24;
      if (var4 == 255) {
         var0.setColorArgb(var1, var2, var3);
      } else if (var4 != 0) {
         int var5 = var0.getColorArgb(var1, var2);
         float var6 = var4 / 255.0F;
         int var7 = Math.round((var5 >> 16 & 0xFF) * (1.0F - var6) + (var3 >> 16 & 0xFF) * var6);
         int var8 = Math.round((var5 >> 8 & 0xFF) * (1.0F - var6) + (var3 >> 8 & 0xFF) * var6);
         int var9 = Math.round((var5 & 0xFF) * (1.0F - var6) + (var3 & 0xFF) * var6);
         int var10 = Math.min(255, (var5 >>> 24) + var4);
         var0.setColorArgb(var1, var2, var10 << 24 | var7 << 16 | var8 << 8 | var9);
      }
   }

   private static void method1839(RemoteUtil.Entry var0, String var1, double var2) {
      NativeImage var4 = var0.method1848();
      int var5 = var4.getWidth();
      int var6 = var4.getHeight();
      if (var1.equals("blur")) {
         int var19 = (int)Math.max(1.0, Math.min(16.0, var2));
         int[] var20 = new int[var5 * var6];

         for (int var23 = 0; var23 < var6; var23++) {
            for (int var25 = 0; var25 < var5; var25++) {
               var20[var23 * var5 + var25] = var4.getColorArgb(var25, var23);
            }
         }

         var20 = method1840(var20, var5, var6, var19, true);
         var20 = method1840(var20, var5, var6, var19, false);

         for (int var24 = 0; var24 < var6; var24++) {
            for (int var26 = 0; var26 < var5; var26++) {
               var4.setColorArgb(var26, var24, var20[var24 * var5 + var26]);
            }
         }
      } else {
         for (int var7 = 0; var7 < var6; var7++) {
            for (int var8 = 0; var8 < var5; var8++) {
               int var9 = var4.getColorArgb(var8, var7);
               int var10 = var9 >>> 24;
               int var11 = var9 >> 16 & 0xFF;
               int var12 = var9 >> 8 & 0xFF;
               int var13 = var9 & 0xFF;
               switch (var1) {
                  case "grayscale":
                  case "gray":
                     int var29 = (int)(var11 * 0.299 + var12 * 0.587 + var13 * 0.114);
                     var11 = method1842(var11, var29, var2);
                     var12 = method1842(var12, var29, var2);
                     var13 = method1842(var13, var29, var2);
                     break;
                  case "invert":
                     var11 = method1842(var11, 255 - var11, var2);
                     var12 = method1842(var12, 255 - var12, var2);
                     var13 = method1842(var13, 255 - var13, var2);
                     break;
                  case "sepia":
                     int var28 = (int)Math.min(255.0, var11 * 0.393 + var12 * 0.769 + var13 * 0.189);
                     int var30 = (int)Math.min(255.0, var11 * 0.349 + var12 * 0.686 + var13 * 0.168);
                     int var18 = (int)Math.min(255.0, var11 * 0.272 + var12 * 0.534 + var13 * 0.131);
                     var11 = method1842(var11, var28, var2);
                     var12 = method1842(var12, var30, var2);
                     var13 = method1842(var13, var18, var2);
                     break;
                  case "brightness":
                     var11 = method1843(var11 * var2);
                     var12 = method1843(var12 * var2);
                     var13 = method1843(var13 * var2);
                     break;
                  case "contrast":
                     var11 = method1843((var11 - 128) * var2 + 128.0);
                     var12 = method1843((var12 - 128) * var2 + 128.0);
                     var13 = method1843((var13 - 128) * var2 + 128.0);
                     break;
                  case "threshold":
                     int var27 = (var11 + var12 + var13) / 3 >= var2 * 255.0 ? 255 : 0;
                     var11 = var27;
                     var12 = var27;
                     var13 = var27;
                     break;
                  case "opacity":
                     var10 = method1843(var10 * var2);
                     break;
                  case "pixelate":
                     int var16 = (int)Math.max(2.0, Math.min(64.0, var2));
                     int var17 = var4.getColorArgb(var8 - var8 % var16, var7 - var7 % var16);
                     var10 = var17 >>> 24;
                     var11 = var17 >> 16 & 0xFF;
                     var12 = var17 >> 8 & 0xFF;
                     var13 = var17 & 0xFF;
                     break;
                  default:
                     throw ScriptValueUtils.method6251("фильтры: grayscale, invert, sepia, brightness, contrast, threshold, opacity, blur, pixelate");
               }

               var4.setColorArgb(var8, var7, var10 << 24 | var11 << 16 | var12 << 8 | var13);
            }
         }
      }
   }

   private static int[] method1840(int[] var0, int var1, int var2, int var3, boolean var4) {
      int[] var5 = new int[var0.length];
      int var6 = var4 ? var2 : var1;
      int var7 = var4 ? var1 : var2;
      int var8 = var3 * 2 + 1;

      for (int var9 = 0; var9 < var6; var9++) {
         long var10 = 0L;
         long var12 = 0L;
         long var14 = 0L;
         long var16 = 0L;

         for (int var18 = -var3; var18 <= var3; var18++) {
            int var19 = var0[method1841(var9, Math.max(0, Math.min(var7 - 1, var18)), var1, var4)];
            var10 += var19 >>> 24;
            var12 += var19 >> 16 & 0xFF;
            var14 += var19 >> 8 & 0xFF;
            var16 += var19 & 0xFF;
         }

         for (int var21 = 0; var21 < var7; var21++) {
            var5[method1841(var9, var21, var1, var4)] = (int)(var10 / var8) << 24 | (int)(var12 / var8) << 16 | (int)(var14 / var8) << 8 | (int)(var16 / var8);
            int var22 = var0[method1841(var9, Math.max(0, var21 - var3), var1, var4)];
            int var20 = var0[method1841(var9, Math.min(var7 - 1, var21 + var3 + 1), var1, var4)];
            var10 += (var20 >>> 24) - (var22 >>> 24);
            var12 += (var20 >> 16 & 0xFF) - (var22 >> 16 & 0xFF);
            var14 += (var20 >> 8 & 0xFF) - (var22 >> 8 & 0xFF);
            var16 += (var20 & 0xFF) - (var22 & 0xFF);
         }
      }

      return var5;
   }

   private static int method1841(int var0, int var1, int var2, boolean var3) {
      return var3 ? var0 * var2 + var1 : var1 * var2 + var0;
   }

   private static int method1842(int var0, int var1, double var2) {
      return method1843(var0 + (var1 - var0) * Math.max(0.0, Math.min(1.0, var2)));
   }

   private static int method1843(double var0) {
      return (int)Math.max(0L, Math.min(255L, Math.round(var0)));
   }

   public static Scriptable method1844(Script var0, Storage var1, Scriptable var2) {
      var0.method1413(() -> method1830(var0));
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274("load", var3x -> {
         String var4 = ScriptValueUtils.method6254(var3x, 0, "");
         Path var5 = method1834(var0, var4);
         if (!Files.isRegularFile(var5)) {
            throw ScriptValueUtils.method6251("нет файла " + var4);
         } else {
            try {
               if (Files.size(var5) > 16777216L) {
                  throw ScriptValueUtils.method6251("картинка больше 16 МБ");
               } else {
                  return method1836(var2, var1, method1833(var0, Files.readAllBytes(var5), var5.getFileName().toString()));
               }
            } catch (IOException var7) {
               throw ScriptValueUtils.method6251(var7.getMessage());
            }
         }
      });
      var3.method6274("create", var3x -> {
         int var4 = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 0, 64)));
         int var5 = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 1, 64)));
         NativeImage var6 = new NativeImage(var4, var5, false);
         int var7 = ScriptValueUtils.method6258(var3x, 2, 0);

         for (int var8 = 0; var8 < var5; var8++) {
            for (int var9 = 0; var9 < var4; var9++) {
               var6.setColorArgb(var9, var8, var7);
            }
         }

         return method1836(var2, var1, method1829(var0, List.of(var6), List.of(0)));
      });
      var3.method6274("list", var2x -> {
         ArrayList var3x = new ArrayList();
         if (var0.getPath() == null) {
            return ScriptValueUtils.method6267(var2, var3x);
         } else {
            try (Stream<java.nio.file.Path> var4 = Files.walk(var0.getPath(), 3)) {
               var4.filter(var0xx -> Files.isRegularFile(var0xx)).forEach(var2xx -> {
                  String var3xx = var2xx.getFileName().toString().toLowerCase(Locale.ROOT);
                  if (var3xx.endsWith(".png") || var3xx.endsWith(".jpg") || var3xx.endsWith(".jpeg") || var3xx.endsWith(".gif") || var3xx.endsWith(".bmp")) {
                     var3x.add(var0.getPath().relativize(var2xx).toString().replace('\\', '/'));
                  }
               });
            } catch (IOException var9) {
            }

            return ScriptValueUtils.method6267(var2, var3x);
         }
      });
      var3.method6274("screenshot", var3x -> {
         ScriptPermissions.method4(var0, "screenshot");
         var var4 = ScriptValueUtils.method6260(var3x, 0);
         int var5 = Math.max(1, Math.min(8, ScriptValueUtils.method6257(var3x, 1, 2)));
         MinecraftClient var6 = MinecraftClient.getInstance();
         ScreenshotRecorder.takeScreenshot(var6.getFramebuffer(), var5, var5x -> var6.execute(() -> {
            if (!var0.isRunning()) {
               var5x.close();
            } else {
               try {
                  RemoteUtil.Entry var5xx = method1829(var0, List.of(var5x), List.of(0));
                  var1.method1321(var0, var4, method1836(var2, var1, var5xx));
               } catch (RuntimeException var6x) {
                  var1.method1327(var0, "error", "screenshot: " + var6x.getMessage());
               }
            }
         }));
         return null;
      });
      var3.method6274("pick", var3x -> {
         var var4 = ScriptValueUtils.method6260(var3x, 0);
         if (var0.getPath() == null) {
            throw ScriptValueUtils.method6251("image.pick доступен только скриптам-папкам");
         } else {
            Thread.ofPlatform().daemon(true).name("zenith-image-pick").start(() -> {
               MemoryStack var5 = MemoryStack.stackPush();

               String var4x;
               try {
                  PointerBuffer var6 = var5.mallocPointer(5);

                  for (String var10 : new String[]{"*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"}) {
                     var6.put(var5.UTF8(var10));
                  }

                  var6.flip();
                  var4x = TinyFileDialogs.tinyfd_openFileDialog("Выбери картинку", "", var6, "Картинки", false);
               } catch (Throwable var13) {
                  if (var5 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var11) {
                        var13.addSuppressed(var11);
                     }
                  }

                  throw var13;
               }

               if (var5 != null) {
                  var5.close();
               }

               if (var4x == null) {
                  method1835(var1, var0, var4, () -> new Object[]{null});
               } else {
                  try {
                     Path var14 = Path.of(var4x);
                     if (Files.size(var14) > 16777216L) {
                        throw new IOException("картинка больше 16 МБ");
                     }

                     byte[] var15 = Files.readAllBytes(var14);
                     Path var16 = method1834(var0, "images");
                     Files.createDirectories(var16);
                     String var17 = var14.getFileName().toString().replaceAll("[^\\p{L}\\p{N}_.-]", "_");
                     Files.copy(var14, var16.resolve(var17), StandardCopyOption.REPLACE_EXISTING);
                     MinecraftClient.getInstance().execute(() -> {
                        if (var0.isRunning()) {
                           try {
                              RemoteUtil.Entry var6x = method1833(var0, var15, var17);
                              var1.method1321(var0, var4, method1836(var2, var1, var6x));
                           } catch (Exception var7) {
                              var1.method1327(var0, "error", "image.pick: " + var7.getMessage());
                           }
                        }
                     });
                  } catch (IOException var12) {
                     var1.method1327(var0, "error", "image.pick: " + var12.getMessage());
                  }
               }
            });
            return null;
         }
      });
      var3.method6274("fromUrl", var3x -> {
         String var4 = ScriptValueUtils.method6254(var3x, 0, "");
         var var5 = ScriptValueUtils.method6260(var3x, 1);
         ScriptNetApi.method3279(var0, var4, 16777216L, (var5x, var6) -> MinecraftClient.getInstance().execute(() -> {
            if (var0.isRunning()) {
               if (var6 != null) {
                  var1.method1321(var0, var5, null, var6);
               } else {
                  try {
                     String var7 = var4.toLowerCase(Locale.ROOT).contains(".gif") ? "remote.gif" : "remote.png";
                     var1.method1321(var0, var5, method1836(var2, var1, method1833(var0, var5x, var7)), null);
                  } catch (Exception var8) {
                     var1.method1321(var0, var5, null, var8.getMessage());
                  }
               }
            }
         }));
         return null;
      });
      return var3.getScriptableObject();
   }

   public static Scriptable method1845(Script var0, Storage var1, Scriptable var2) {
      HashMap var3 = new HashMap();
      ArrayList<ScriptSoundLoader.Entry> var4 = new ArrayList();
      var0.method1413(() -> {
         for (Object var2x_o : (Iterable<?>)(Object)(var4)) {
            ScriptSoundLoader.Entry var2x = (ScriptSoundLoader.Entry)var2x_o;
            var2x.stopped = true;
         }
      });
      java.util.function.Function var5 = var2x -> var3.computeIfAbsent(var2x, var1xx -> {
         try {
            return ScriptSoundLoader.method735(method1834(var0, (String)var1xx));
         } catch (IOException var3x) {
            throw ScriptValueUtils.method6251(var1xx + ": " + var3x.getMessage());
         }
      });
      ScriptValueUtils.Entry2 var6 = ScriptValueUtils.method6247(var2);
      BiFunction<Object[], Boolean, Object> var7 = (var3x, var4x) -> {
         String var5x = ScriptValueUtils.method6254(var3x, 0, "");
         float var6x = (float)Math.max(0.0, Math.min(4.0, ScriptValueUtils.method6256(var3x, var4x ? 4 : 1, 1.0)));
         float var7x = (float)Math.max(0.25, Math.min(4.0, ScriptValueUtils.method6256(var3x, var4x ? 5 : 2, 1.0)));
         String var8 = var5x.toLowerCase(Locale.ROOT);
         if (!var8.endsWith(".ogg") && !var8.endsWith(".wav")) {
            Identifier var13 = Identifier.tryParse(var5x.contains(":") ? var5x : "minecraft:" + var5x);
            if (var13 == null) {
               throw ScriptValueUtils.method6251("неверный id звука: " + var5x);
            } else {
               SoundEvent var14 = Registries.SOUND_EVENT.containsId(var13) ? Registries.SOUND_EVENT.get(var13) : SoundEvent.of(var13);
               MinecraftClient var11 = MinecraftClient.getInstance();
               PositionedSoundInstance var12 = var4x
                  ? new PositionedSoundInstance(
                     var14,
                     SoundCategory.MASTER,
                     var6x,
                     var7x,
                     Random.create(),
                     ScriptValueUtils.method6256(var3x, 1, 0.0),
                     ScriptValueUtils.method6256(var3x, 2, 0.0),
                     ScriptValueUtils.method6256(var3x, 3, 0.0)
                  )
                  : PositionedSoundInstance.ui(var14, var7x, var6x);
               var11.getSoundManager().play(var12);
               return ScriptValueUtils.method6247(var2).method6274("stop", var2xx -> {
                  var11.getSoundManager().stop(var12);
                  return null;
               }).method6274("playing", var2xx -> var11.getSoundManager().isPlaying(var12)).getScriptableObject();
            }
         } else {
            ScriptSoundLoader.Data var9 = (ScriptSoundLoader.Data)var5.apply(var5x);
            ScriptSoundLoader.Entry var10 = ScriptSoundLoader.method738(var9, var6x, var7x, false);
            if (var4x) {
               var10.doubleValue = ScriptValueUtils.method6256(var3x, 1, 0.0);
               var10.doubleValue2 = ScriptValueUtils.method6256(var3x, 2, 0.0);
               var10.doubleValue3 = ScriptValueUtils.method6256(var3x, 3, 0.0);
            }

            var4.removeIf(var0xx -> !ScriptSoundLoader.items.containsKey(var0xx.intValue));
            var4.add(var10);
            return method1846(var2, var10);
         }
      };
      var6.method6274("play", var1x -> var7.apply(var1x, false));
      var6.method6274("play3d", var1x -> var7.apply(var1x, true));
      var6.method6274(
         "loop",
         var3x -> {
            ScriptSoundLoader.Entry var4x = ScriptSoundLoader.method738(
               (ScriptSoundLoader.Data)var5.apply(ScriptValueUtils.method6254(var3x, 0, "")),
               (float)ScriptValueUtils.method6256(var3x, 1, 1.0),
               (float)ScriptValueUtils.method6256(var3x, 2, 1.0),
               true
            );
            var4.removeIf(var0xx -> !ScriptSoundLoader.items.containsKey(var0xx.intValue));
            var4.add(var4x);
            return method1846(var2, var4x);
         }
      );
      var6.method6274(
         "load",
         var2x -> {
            ScriptSoundLoader.Data var3x = (ScriptSoundLoader.Data)var5.apply(ScriptValueUtils.method6254(var2x, 0, ""));
            return ScriptValueUtils.method6247(var2)
               .method6277("duration", var3x.method743())
               .method6277("channels", (double)var3x.getIntValue())
               .method6277("rate", (double)var3x.getIntValue2())
               .getScriptableObject();
         }
      );
      var6.method6274("stopAll", var1x -> {
         for (Object var3x_o : (Iterable<?>)(Object)(var4)) {
            ScriptSoundLoader.Entry var3x = (ScriptSoundLoader.Entry)var3x_o;
            var3x.stopped = true;
         }

         var4.clear();
         return null;
      });
      var6.method6274("ids", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "").toLowerCase(Locale.ROOT);
         ArrayList var3x = new ArrayList();

         for (Identifier var5x : Registries.SOUND_EVENT.getIds()) {
            String var6x = var5x.toString();
            if (var2x.isEmpty() || var6x.contains(var2x)) {
               var3x.add(var6x);
            }

            if (var3x.size() >= 300) {
               break;
            }
         }

         var3x.sort(null);
         return ScriptValueUtils.method6267(var2, var3x);
      });
      var6.method6274("files", var2x -> {
         ArrayList var3x = new ArrayList();
         if (var0.getPath() == null) {
            return ScriptValueUtils.method6267(var2, var3x);
         } else {
            try (Stream<java.nio.file.Path> var4x = Files.walk(var0.getPath(), 3)) {
               var4x.filter(var0xx -> Files.isRegularFile(var0xx)).forEach(var2xx -> {
                  String var3xx = var2xx.getFileName().toString().toLowerCase(Locale.ROOT);
                  if (var3xx.endsWith(".ogg") || var3xx.endsWith(".wav")) {
                     var3x.add(var0.getPath().relativize(var2xx).toString().replace('\\', '/'));
                  }
               });
            } catch (IOException var9) {
            }

            return ScriptValueUtils.method6267(var2, var3x);
         }
      });
      return var6.getScriptableObject();
   }

   private static Scriptable method1846(Scriptable var0, ScriptSoundLoader.Entry var1) {
      return ScriptValueUtils.method6247(var0)
         .method6274("stop", var1x -> {
            var1.stopped = true;
            return null;
         })
         .method6274("playing", var1x -> ScriptSoundLoader.items.containsKey(var1.intValue))
         .method6274("volume", var1x -> {
            var1.floatValue = (float)Math.max(0.0, Math.min(4.0, ScriptValueUtils.method6256(var1x, 0, var1.floatValue)));
            return null;
         })
         .method6274("pitch", var1x -> (double)var1.floatValue2)
         .method6274("progress", var1x -> var1.doubleValue5)
         .method6274("duration", var1x -> var1.data.method743())
         .method6274("move", var1x -> {
            var1.doubleValue = ScriptValueUtils.method6256(var1x, 0, 0.0);
            var1.doubleValue2 = ScriptValueUtils.method6256(var1x, 1, 0.0);
            var1.doubleValue3 = ScriptValueUtils.method6256(var1x, 2, 0.0);
            return null;
         })
         .method6274("range", var1x -> {
            var1.doubleValue4 = Math.max(1.0, Math.min(256.0, ScriptValueUtils.method6256(var1x, 0, 32.0)));
            return null;
         })
         .getScriptableObject();
   }

   public static void method1847(Storage var0, Script var1, Function var2, Object... var3) {
      MinecraftClient.getInstance().execute(() -> {
         if (var1.isRunning()) {
            var0.method1321(var1, var2, var3);
         }
      });
   }

   public static final class Entry {
      public final Script script;
      public final int intValue = RemoteUtil.atomicInteger2.incrementAndGet();
      public final List<NativeImage> items = new ArrayList<>();
      public final List<Integer> items2 = new ArrayList<>();
      public final List<Identifier> items3 = new ArrayList<>();
      public final List<NativeImageBackedTexture> items4 = new ArrayList<>();
      public final long longValue = System.currentTimeMillis();
      public boolean dirty = true;
      public boolean flag;
      public Scriptable scriptable;

      public Entry(Script var1) {
         this.script = var1;
      }

      public NativeImage method1848() {
         return this.items.get(0);
      }

      public int width() {
         return this.method1848().getWidth();
      }

      public int height() {
         return this.method1848().getHeight();
      }

      public long method1849() {
         long var1 = 0L;

         for (NativeImage var4 : this.items) {
            var1 += (long)var4.getWidth() * var4.getHeight();
         }

         return var1;
      }

      public Identifier texture() {
         if (this.flag) {
            return null;
         } else {
            MinecraftClient var1 = MinecraftClient.getInstance();
            if (this.items4.isEmpty()) {
               for (NativeImage var3 : this.items) {
                  Identifier var4 = Identifier.of("zenith", "script_image/" + RemoteUtil.atomicInteger.incrementAndGet());
                  NativeImageBackedTexture var5 = new NativeImageBackedTexture(() -> "zenith-script", var3);
                  var1.getTextureManager().registerTexture(var4, var5);
                  this.items3.add(var4);
                  this.items4.add(var5);
               }

               this.dirty = false;
            } else if (this.dirty) {
               this.items4.get(0).upload();
               this.dirty = false;
            }

            if (this.items.size() == 1) {
               return this.items3.get(0);
            } else {
               long var7 = 0L;

               for (int var10 : this.items2) {
                  var7 += var10;
               }

               long var9 = (System.currentTimeMillis() - this.longValue) % Math.max(1L, var7);

               for (int var6 = 0; var6 < this.items2.size(); var6++) {
                  var9 -= this.items2.get(var6).intValue();
                  if (var9 < 0L) {
                     return this.items3.get(var6);
                  }
               }

               return this.items3.get(0);
            }
         }
      }

      public void release() {
         if (!this.flag) {
            this.flag = true;
            MinecraftClient var1 = MinecraftClient.getInstance();

            for (Identifier var3 : this.items3) {
               try {
                  var1.getTextureManager().destroyTexture(var3);
               } catch (RuntimeException var5) {
               }
            }

            if (this.items4.isEmpty()) {
               for (NativeImage var7 : this.items) {
                  var7.close();
               }
            }

            this.items3.clear();
            this.items4.clear();
            RemoteUtil.items2.remove(this.intValue);
            RemoteUtil.method1826(this.script).remove(this);
         }
      }
   }
}

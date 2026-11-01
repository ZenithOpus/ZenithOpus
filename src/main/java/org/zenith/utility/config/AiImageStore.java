package org.zenith.utility.config;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.AiStreamClient;

public final class AiImageStore {
   public static final int CONST_intValue = 1568;
   public static final long CONST_longValue = 25165824L;
   private static final Map<String, Identifier> items = new ConcurrentHashMap<>();
   private static final Map<String, int[]> items2 = new ConcurrentHashMap<>();
   private static final Set<String> items3 = ConcurrentHashMap.newKeySet();

   private AiImageStore() {
   }

   public static Path method4630() {
      return Zenith.file.toPath().resolve("ai-chats").resolve("images");
   }

   public static boolean method4631(Path var0) {
      String var1 = var0.getFileName().toString().toLowerCase(Locale.ROOT);
      return var1.endsWith(".png")
         || var1.endsWith(".jpg")
         || var1.endsWith(".jpeg")
         || var1.endsWith(".gif")
         || var1.endsWith(".bmp")
         || var1.endsWith(".webp");
   }

   public static String method4632(Path var0) throws IOException {
      if (Files.size(var0) > 25165824L) {
         throw new IOException("картинка больше 24 МБ");
      } else {
         BufferedImage var1;
         try (InputStream var2 = Files.newInputStream(var0)) {
            var1 = ImageIO.read(var2);
         }

         if (var1 == null) {
            throw new IOException("не удалось прочитать " + var0.getFileName() + " (png, jpg, gif, bmp)");
         } else {
            return method4634(var1);
         }
      }
   }

   public static String method4633(NativeImage var0) throws IOException {
      BufferedImage var1 = new BufferedImage(var0.getWidth(), var0.getHeight(), 2);

      for (int var2 = 0; var2 < var0.getHeight(); var2++) {
         for (int var3 = 0; var3 < var0.getWidth(); var3++) {
            var1.setRGB(var3, var2, var0.getColorArgb(var3, var2));
         }
      }

      return method4634(var1);
   }

   private static String method4634(BufferedImage var0) throws IOException {
      BufferedImage var1 = method4635(var0, 1568, 2);
      Files.createDirectories(method4630());
      String var2 = UUID.randomUUID().toString().substring(0, 12) + ".png";
      ImageIO.write(var1, "png", method4630().resolve(var2).toFile());
      return var2;
   }

   private static BufferedImage method4635(BufferedImage var0, int var1, int var2) {
      int var3 = var0.getWidth();
      int var4 = var0.getHeight();
      double var5 = Math.min(1.0, (double)var1 / Math.max(var3, var4));
      int var7 = Math.max(1, (int)Math.round(var3 * var5));
      int var8 = Math.max(1, (int)Math.round(var4 * var5));
      BufferedImage var9 = new BufferedImage(var7, var8, var2);
      Graphics2D var10 = var9.createGraphics();
      var10.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      var10.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      if (var2 == 1) {
         var10.setColor(Color.BLACK);
         var10.fillRect(0, 0, var7, var8);
      }

      var10.drawImage(var0, 0, 0, var7, var8, null);
      var10.dispose();
      return var9;
   }

   public static AiStreamClient.Data2 method4636(String var0) throws IOException {
      Path var1 = method4630().resolve(var0);
      BufferedImage var2 = ImageIO.read(var1.toFile());
      if (var2 == null) {
         throw new IOException("картинка " + var0 + " испорчена");
      } else {
         BufferedImage var3 = method4635(var2, 1568, 1);
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         ImageWriter var5 = ImageIO.getImageWritersByFormatName("jpg").next();

         try (MemoryCacheImageOutputStream var6 = new MemoryCacheImageOutputStream(var4)) {
            var5.setOutput(var6);
            ImageWriteParam var7 = var5.getDefaultWriteParam();
            var7.setCompressionMode(2);
            var7.setCompressionQuality(0.85F);
            var5.write(null, new IIOImage(var3, null, null), var7);
         } finally {
            var5.dispose();
         }

         return new AiStreamClient.Data2("image/jpeg", Base64.getEncoder().encodeToString(var4.toByteArray()));
      }
   }

   public static Path method4637(String var0) {
      return method4630().resolve(var0);
   }

   public static int[] method4638(String var0) {
      return items2.getOrDefault(var0, new int[]{1, 1});
   }

   public static Identifier method4639(String var0) {
      Identifier var1 = items.get(var0);
      if (var1 != null) {
         return var1;
      } else if (items3.contains(var0)) {
         return null;
      } else {
         Path var2 = method4630().resolve(var0);
         if (!Files.isRegularFile(var2)) {
            items3.add(var0);
            return null;
         } else {
            NativeImage var3 = null;

            try {
               Identifier var8;
               try (InputStream var4 = Files.newInputStream(var2)) {
                  var3 = NativeImage.read(var4);
                  int var5 = var3.getWidth();
                  int var6 = var3.getHeight();
                  Identifier var7 = Identifier.of("zenith", "ai_image/" + var0.replace(".png", "").toLowerCase(Locale.ROOT));
                  MinecraftClient.getInstance().getTextureManager().registerTexture(var7, new NativeImageBackedTexture(() -> "zenith-ai", var3));
                  var3 = null;
                  items2.put(var0, new int[]{var5, var6});
                  items.put(var0, var7);
                  var8 = var7;
               }

               return var8;
            } catch (Exception var11) {
               if (var3 != null) {
                  var3.close();
               }

               items3.add(var0);
               return null;
            }
         }
      }
   }

   public static void method4640(Collection<String> var0) {
      if (!var0.isEmpty()) {
         List<String> var1 = List.copyOf(var0);
         MinecraftClient var2 = MinecraftClient.getInstance();
         var2.execute(() -> {
            for (Object var3_o : (Iterable<?>)(Object)(var1)) {
               String var3 = (String)var3_o;
               Identifier var4 = items.remove(var3);
               items2.remove(var3);
               items3.remove(var3);
               if (var4 != null) {
                  var2.getTextureManager().destroyTexture(var4);
               }
            }
         });
      }
   }
}

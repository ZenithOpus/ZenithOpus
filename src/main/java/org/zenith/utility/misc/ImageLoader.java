package org.zenith.utility.misc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class ImageLoader {
   public static final int CONST_intValue = 16384;
   public static final long CONST_longValue = 41943040L;

   private ImageLoader() {
   }

   public static int[] method231(byte[] var0) throws IOException {
      int[] var4;
      try (ImageInputStream var1 = ImageIO.createImageInputStream(new ByteArrayInputStream(var0))) {
         if (var1 == null) {
            throw new IOException("не удалось прочитать картинку");
         }

         var var2 = ImageIO.getImageReaders(var1);
         if (!var2.hasNext()) {
            throw new IOException("неизвестный формат картинки (png, jpg, gif, bmp)");
         }

         ImageReader var3 = (ImageReader)var2.next();

         try {
            var3.setInput(var1, true, true);
            var4 = new int[]{var3.getWidth(0), var3.getHeight(0)};
         } finally {
            var3.dispose();
         }
      }

      return var4;
   }

   public static void method232(byte[] var0) throws IOException {
      int[] var1 = method231(var0);
      method233(var1[0], var1[1]);
   }

   public static void method233(int var0, int var1) throws IOException {
      if (var0 <= 0 || var1 <= 0 || var0 > 16384 || var1 > 16384 || (long)var0 * var1 > 41943040L) {
         throw new IOException("картинка слишком большая: " + var0 + "×" + var1 + " (до 16384 по стороне и 40 Мпикс)");
      }
   }

   public static BufferedImage method234(byte[] var0, int var1) throws IOException {
      BufferedImage var10;
      try (ImageInputStream var2 = ImageIO.createImageInputStream(new ByteArrayInputStream(var0))) {
         if (var2 == null) {
            throw new IOException("не удалось прочитать картинку");
         }

         var var3 = ImageIO.getImageReaders(var2);
         if (!var3.hasNext()) {
            throw new IOException("неизвестный формат картинки (png, jpg, gif, bmp)");
         }

         ImageReader var4 = (ImageReader)var3.next();

         try {
            var4.setInput(var2, true, true);
            int var5 = var4.getWidth(0);
            int var6 = var4.getHeight(0);
            method233(var5, var6);
            ImageReadParam var7 = var4.getDefaultReadParam();
            int var8 = Math.max(1, (int)Math.ceil((double)Math.max(var5, var6) / var1));
            if (var8 > 1) {
               var7.setSourceSubsampling(var8, var8, 0, 0);
            }

            BufferedImage var9 = var4.read(0, var7);
            if (var9 == null) {
               throw new IOException("не удалось прочитать картинку");
            }

            var10 = var9;
         } finally {
            var4.dispose();
         }
      }

      return var10;
   }
}

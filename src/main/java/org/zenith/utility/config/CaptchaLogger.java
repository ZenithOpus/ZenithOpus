package org.zenith.utility.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;

public final class CaptchaLogger {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private static final ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private static volatile boolean flag;

   private CaptchaLogger() {
   }

   public static void log(String var0) {
      concurrentLinkedQueue.add(LocalTime.now().format(dateTimeFormatter) + " " + var0);
      method4689();
   }

   public static String trim(String var0) {
      if (var0 == null) {
         return "null";
      } else {
         String var1 = var0.trim().replace('\n', ' ');
         return var1.length() > 140 ? var1.substring(0, 140) + "..." : var1;
      }
   }

   private static void method4689() {
      if (!flag) {
         synchronized (CaptchaLogger.class) {
            if (!flag) {
               flag = true;
               ScheduledExecutorService var1 = Executors.newSingleThreadScheduledExecutor(var0 -> {
                  Thread var1x = new Thread(var0, "captcha-log");
                  var1x.setDaemon(true);
                  return var1x;
               });
               var1.scheduleWithFixedDelay(CaptchaLogger::flush, 0L, 400L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   private static void flush() {
      if (!concurrentLinkedQueue.isEmpty()) {
         ArrayList var0 = new ArrayList();

         String var1;
         while ((var1 = concurrentLinkedQueue.poll()) != null) {
            var0.add(var1);
         }

         try {
            MinecraftClient var2 = MinecraftClient.getInstance();
            Path var3 = var2.runDirectory.toPath().resolve("captcha.log");
            Files.write(var3, var0, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         } catch (Throwable var4) {
         }
      }
   }
}

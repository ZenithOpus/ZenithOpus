package org.zenith.utility.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.MinecraftClient;
import org.zenith.Zenith;
import org.zenith.utility.misc.MediaPlayerState;
import org.zenith.utility.misc.MediaSessionManager;

public final class MediaHelperProcess {
   private static final MediaHelperProcess INSTANCE = new MediaHelperProcess();
   private static final boolean CONST_flag = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "zenith-media-helper");
      var1.setDaemon(true);
      return var1;
   });
   private Process process;
   private BufferedWriter bufferedWriter;
   private long longValue;
   private volatile boolean flag;
   private volatile int intValue;
   private volatile long longValue2;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private final AtomicReference<String> atomicReference = new AtomicReference<>();

   private MediaHelperProcess() {
   }

   public static MediaHelperProcess getInstance() {
      return INSTANCE;
   }

   public boolean method6859() {
      return CONST_flag;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public void method6860(String var1, double var2) {
      this.method6869("seek|" + clean(var1) + "|" + String.format(Locale.ROOT, "%.3f", var2));
   }

   public void method6861(String var1, float var2) {
      String var3 = "vol|" + clean(var1) + "|" + String.format(Locale.ROOT, "%.3f", var2);
      if (this.atomicReference.getAndSet(var3) == null && CONST_flag) {
         this.executorService.execute(() -> {
            String var1x = this.atomicReference.getAndSet(null);
            if (var1x != null) {
               this.write(var1x);
            }
         });
      }
   }

   public void method6862(String var1, float var2, int var3) {
      this.method6869("fade|" + clean(var1) + "|" + String.format(Locale.ROOT, "%.3f", var2) + "|" + var3);
   }

   public void method6863(String var1) {
      this.method6869("getvol|" + clean(var1));
   }

   public void method6864(String var1) {
      this.method6869("shuffle|" + clean(var1));
   }

   public void method6865(String var1) {
      this.method6869("repeat|" + clean(var1));
   }

   public void method6866(String var1) {
      this.method6869("time|" + clean(var1));
   }

   public void method6867(String var1) {
      this.method6869("meter|" + clean(var1));
   }

   public void method6868(String var1) {
      this.method6869("state|" + clean(var1));
   }

   private static String clean(String var0) {
      return var0 == null ? "" : var0.replace('|', ' ').replace('\n', ' ');
   }

   private void method6869(String var1) {
      if (CONST_flag) {
         this.executorService.execute(() -> this.write(var1));
      }
   }

   private void write(String var1) {
      try {
         if (!this.method6870()) {
            return;
         }

         this.bufferedWriter.write(var1);
         this.bufferedWriter.write(10);
         this.bufferedWriter.flush();
      } catch (Throwable var3) {
         this.stop();
      }
   }

   private boolean method6870() {
      if (this.process != null && this.process.isAlive()) {
         return true;
      } else if (System.currentTimeMillis() - this.longValue < 30000L) {
         return false;
      } else {
         try {
            Path var1 = Zenith.file.toPath().resolve("media");
            Files.createDirectories(var1);
            Path var2 = var1.resolve("helper.ps1");

            try (InputStream var3 = MediaHelperProcess.class.getResourceAsStream("/assets/zenith/media/helper.ps1")) {
               if (var3 == null) {
                  this.longValue = System.currentTimeMillis();
                  return false;
               }

               Files.copy(var3, var2, StandardCopyOption.REPLACE_EXISTING);
            }

            ProcessBuilder var9 = new ProcessBuilder(
               "powershell.exe", "-NoLogo", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden", "-File", var2.toString()
            );
            var9.redirectErrorStream(false);
            var9.redirectError(Redirect.DISCARD);
            this.process = var9.start();
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream(), StandardCharsets.UTF_8));
            Process var4 = this.process;
            Thread var5 = new Thread(() -> this.method6871(var4), "zenith-media-reader");
            var5.setDaemon(true);
            var5.start();
            return true;
         } catch (Throwable var8) {
            this.longValue = System.currentTimeMillis();
            this.process = null;
            return false;
         }
      }
   }

   private void method6871(Process var1) {
      String var3;
      try (BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getInputStream(), StandardCharsets.UTF_8))) {
         while ((var3 = var2.readLine()) != null) {
            String var4 = var3.trim();
            if (var4.startsWith("peak ")) {
               MediaPlayerState.getInstance().method1671(method6873(var4.substring(5)));
            } else if (var4.startsWith("time ")) {
               String[] var5 = var4.split(" ");
               if (var5.length >= 5) {
                  double var6 = method6873(var5[1]);
                  double var8 = method6873(var5[2]);
                  long var10 = method6872(var5[3]);
                  boolean var12 = var5[4].trim().equals("4");
                  MinecraftClient.getInstance().execute(() -> MediaSessionManager.getInstance().method6002(var6, var8, var10, var12));
               }
            } else if (var4.startsWith("vol ")) {
               float var16 = method6873(var4.substring(4));
               MinecraftClient.getInstance().execute(() -> MediaPlayerState.getInstance().method1670(var16));
            } else if (var4.startsWith("state ")) {
               String[] var17 = var4.split(" ");
               if (var17.length >= 3) {
                  this.flag = var17[1].equalsIgnoreCase("true");
                  this.intValue = (int)method6873(var17[2]);
                  if (var17.length >= 5) {
                     this.flag2 = var17[3].equalsIgnoreCase("true");
                     this.flag3 = var17[4].equalsIgnoreCase("true");
                  }

                  this.longValue2 = System.currentTimeMillis();
               }
            }
         }
      } catch (Throwable var15) {
      }
   }

   private static long method6872(String var0) {
      try {
         return Long.parseLong(var0.trim());
      } catch (NumberFormatException var2) {
         return 0L;
      }
   }

   private static float method6873(String var0) {
      try {
         return Float.parseFloat(var0.trim().replace(',', '.'));
      } catch (NumberFormatException var2) {
         return -1.0F;
      }
   }

   public void stop() {
      if (this.process != null) {
         this.process.destroy();
         this.process = null;
      }
   }
}

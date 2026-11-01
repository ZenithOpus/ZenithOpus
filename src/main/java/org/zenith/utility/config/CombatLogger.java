package org.zenith.utility.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import org.zenith.Zenith;

public final class CombatLogger {
   public static final File file = new File(Zenith.file, "combat-logs");
   private static final long CONST_longValue = 10000L;
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
   private static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private static final String text = "\u0000OPEN ";
   private static final String text2 = "\u0000CLOSE";
   private static final String text3 = "\u0001";
   private final Queue<String> queue = new ConcurrentLinkedQueue<>();
   private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Zenith-CombatLog");
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   });
   private BufferedWriter bufferedWriter;
   private BufferedWriter bufferedWriter2;
   private final List<String> items = new ArrayList<>();
   private volatile boolean active;
   private volatile int intValue;
   private long longValue;
   private long longValue2;
   private String text4;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public int intValue9;
   public float floatValue;
   public float floatValue2;

   public CombatLogger() {
      this.scheduledExecutorService.scheduleWithFixedDelay(this::method4618, 500L, 500L, TimeUnit.MILLISECONDS);
   }

   public boolean active() {
      return this.active;
   }

   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   public void method4611(boolean var1, String var2) {
      long var3 = System.currentTimeMillis();
      if (var1) {
         this.longValue2 = var3;
         if (!this.active) {
            this.method4617(var2);
         }
      } else if (this.active && var3 - this.longValue2 > 10000L) {
         this.method4616("целей нет 10 с");
      }
   }

   public void method4612(String var1, String var2, Object... var3) {
      if (this.active) {
         String var4 = var3.length == 0 ? var2 : String.format(Locale.ROOT, var2, var3);
         this.queue.add(LocalDateTime.now().format(dateTimeFormatter2) + " t" + this.intValue + " " + var1 + " " + var4);
         if (MinecraftClient.getInstance().isOnThread()) {
            this.items.add(var1 + " " + var4);
         }
      }
   }

   public void method4613(String var1) {
      if (this.active) {
         this.queue.add("\u0001" + var1);
      }
   }

   public List<String> method4614() {
      var var1 = new ArrayList<>(this.items);
      this.items.clear();
      return var1;
   }

   public void method4615(String var1) {
      if (this.active && !var1.equals(this.text4)) {
         this.text4 = var1;
         this.method4612("STATE", "%s", var1);
      }
   }

   public void method4616(String var1) {
      if (this.active) {
         long var2 = (System.currentTimeMillis() - this.longValue) / 1000L;
         this.method4612("END", "%s, бой %d с", var1, var2);
         this.method4612(
            "TOTAL",
            "кристаллов поставлено %d, сломано %d; блоков защиты %d; выкопано %d; ударов мечом %d",
            this.intValue2,
            this.intValue3,
            this.intValue4,
            this.intValue5,
            this.intValue6
         );
         this.method4612(
            "TOTAL",
            "урон по нам %.1f, по целям (по hp) %.1f; попы: наши %d, их %d; вражеских кристаллов рядом %d",
            this.floatValue,
            this.floatValue2,
            this.intValue7,
            this.intValue8,
            this.intValue9
         );
         this.queue.add("\u0000CLOSE");
         this.active = false;
      }
   }

   public void shutdown() {
      this.method4616("аура выключена");
      this.scheduledExecutorService.execute(this::method4618);
   }

   private void method4617(String var1) {
      this.intValue2 = this.intValue3 = this.intValue4 = this.intValue5 = this.intValue6 = this.intValue7 = this.intValue8 = this.intValue9 = 0;
      this.floatValue = this.floatValue2 = 0.0F;
      this.text4 = null;
      this.longValue = System.currentTimeMillis();
      this.queue.add("\u0000OPEN " + LocalDateTime.now().format(dateTimeFormatter));
      this.active = true;
      this.method4612("BEGIN", "%s", var1);
   }

   private void method4618() {
      try {
         while (true) {
            String var1;
            if ((var1 = this.queue.poll()) != null) {
               if (var1.startsWith("\u0000OPEN ")) {
                  this.method4619();
                  file.mkdirs();
                  String var2 = var1.substring("\u0000OPEN ".length());
                  this.bufferedWriter = Files.newBufferedWriter(new File(file, var2 + ".log").toPath(), StandardCharsets.UTF_8);
                  this.bufferedWriter2 = Files.newBufferedWriter(new File(file, var2 + ".jsonl").toPath(), StandardCharsets.UTF_8);
               } else if (var1.equals("\u0000CLOSE")) {
                  this.method4619();
               } else if (var1.startsWith("\u0001")) {
                  if (this.bufferedWriter2 != null) {
                     this.bufferedWriter2.write(var1, 1, var1.length() - 1);
                     this.bufferedWriter2.write(10);
                  }
               } else if (this.bufferedWriter != null) {
                  this.bufferedWriter.write(var1);
                  this.bufferedWriter.newLine();
               }
            } else {
               if (this.bufferedWriter != null) {
                  this.bufferedWriter.flush();
               }

               if (this.bufferedWriter2 != null) {
                  this.bufferedWriter2.flush();
               }
               break;
            }
         }
      } catch (IOException var3) {
         this.method4619();
      }
   }

   private void method4619() {
      try {
         if (this.bufferedWriter != null) {
            this.bufferedWriter.close();
         }

         if (this.bufferedWriter2 != null) {
            this.bufferedWriter2.close();
         }
      } catch (IOException var2) {
      }

      this.bufferedWriter = null;
      this.bufferedWriter2 = null;
   }
}

package org.zenith.release;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReleaseStartup {
   private static final AtomicBoolean opened = new AtomicBoolean();

   private ReleaseStartup() {
   }

   public static void configure() {
      if (System.getProperty("DevMode") == null) {
         System.setProperty("DevMode", "true");
      }

      if (System.getProperty("DevName") == null) {
         System.setProperty("DevName", "@moluchclient and @yoezproject");
      }
   }

   public static void openLinks() {
      if (opened.compareAndSet(false, true)) {
         Thread var0 = new Thread(() -> {
            if (Desktop.isDesktopSupported()) {
               Desktop var0x = Desktop.getDesktop();
               if (var0x.isSupported(Action.BROWSE)) {
                  for (String var4 : new String[]{"https://t.me/yoezproject", "https://t.me/moluchclient"}) {
                     try {
                        var0x.browse(URI.create(var4));
                     } catch (Exception var6) {
                        System.err.println("[Zenith] Cannot open startup link: " + var6.getClass().getSimpleName());
                     }
                  }
               }
            }
         }, "zenith-startup-links");
         var0.setDaemon(true);
         var0.start();
      }
   }
}

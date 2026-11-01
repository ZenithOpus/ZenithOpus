package org.zenith.utility.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class AccountFilePicker {
   private AccountFilePicker() {
   }

   public static CompletableFuture<Optional<Path>> method1001() {
      return CompletableFuture.supplyAsync(
         () -> {
            try {
               Path var0 = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop", "Alts", "cookies");
               if (!Files.exists(var0)) {
                  var0 = Path.of(System.getProperty("user.home"), "Desktop", "Alts", "cookies");
               }

               if (!Files.exists(var0)) {
                  var0 = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop", "Alts");
               }

               if (!Files.exists(var0)) {
                  var0 = Path.of(System.getProperty("user.home"), "Desktop", "Alts");
               }

               String var1 = Files.exists(var0) ? var0.toAbsolutePath().toString().replace("'", "''") : "";
               String var2 = "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;Add-Type -AssemblyName System.Windows.Forms | Out-Null;$f=New-Object System.Windows.Forms.OpenFileDialog;$f.Title='Выберите файл с куки или токеном';$f.Filter='Файлы аккаунтов (*.txt;*.json)|*.txt;*.json|Все файлы (*.*)|*.*';"
                  + (var1.isEmpty() ? "" : "$f.InitialDirectory='" + var1 + "';")
                  + "$f.Multiselect=$false;if($f.ShowDialog() -eq 'OK'){[Console]::Write($f.FileName)}";
               Process var3 = new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-STA", "-Command", var2)
                  .redirectErrorStream(true)
                  .start();
               StringBuilder var4 = new StringBuilder();
               BufferedReader var5 = new BufferedReader(new InputStreamReader(var3.getInputStream(), StandardCharsets.UTF_8));

               String var6;
               try {
                  while ((var6 = var5.readLine()) != null) {
                     var4.append(var6);
                  }
               } catch (Throwable var9) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }

                  throw var9;
               }

               var5.close();
               if (var3.waitFor() != 0) {
                  throw new IllegalStateException("PowerShell file dialog process returned error.");
               } else {
                  String var11 = var4.toString().trim();
                  if (var11.isEmpty()) {
                     return Optional.empty();
                  } else {
                     File var12 = new File(var11);
                     return var12.isFile() ? Optional.of(var12.toPath()) : Optional.empty();
                  }
               }
            } catch (Exception var10) {
               throw new RuntimeException("Не удалось открыть окно выбора файла: " + var10.getMessage(), var10);
            }
         }
      );
   }
}

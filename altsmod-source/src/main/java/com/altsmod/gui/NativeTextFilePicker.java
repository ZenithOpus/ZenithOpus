package com.altsmod.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class NativeTextFilePicker {
   private NativeTextFilePicker() {
   }

   public static CompletableFuture chooseCredentialFile() {
      return CompletableFuture.supplyAsync(() -> {
         try {
            Path preferred = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop", "Alts", "cookies");
            if (!Files.exists(preferred, new LinkOption[0])) {
               preferred = Path.of(System.getProperty("user.home"), "Desktop", "Alts", "cookies");
            }

            if (!Files.exists(preferred, new LinkOption[0])) {
               preferred = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop", "Alts");
            }

            if (!Files.exists(preferred, new LinkOption[0])) {
               preferred = Path.of(System.getProperty("user.home"), "Desktop", "Alts");
            }

            String initDir = Files.exists(preferred, new LinkOption[0]) ? preferred.toAbsolutePath().toString().replace("'", "''") : "";
            String var10000 = initDir.isEmpty() ? "" : "$f.InitialDirectory='" + initDir + "';";
            String script = "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;Add-Type -AssemblyName System.Windows.Forms | Out-Null;$f=New-Object System.Windows.Forms.OpenFileDialog;$f.Title='Выберите файл с куки или токеном';$f.Filter='Файлы аккаунтов (*.txt;*.json)|*.txt;*.json|Все файлы (*.*)|*.*';" + var10000 + "$f.Multiselect=$false;if($f.ShowDialog() -eq 'OK'){[Console]::Write($f.FileName)}";
            Process process = (new ProcessBuilder(new String[]{"powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-STA", "-Command", script})).redirectErrorStream(true).start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            String line;
            try {
               while((line = reader.readLine()) != null) {
                  output.append(line);
               }
            } catch (Throwable var9) {
               try {
                  reader.close();
               } catch (Throwable x2) {
                  var9.addSuppressed(x2);
               }

               throw var9;
            }

            reader.close();
            if (process.waitFor() != 0) {
               throw new IllegalStateException("PowerShell file dialog process returned error.");
            } else {
               String selected = output.toString().trim();
               if (selected.isEmpty()) {
                  return Optional.empty();
               } else {
                  File file = new File(selected);
                  return file.isFile() ? Optional.of(file.toPath()) : Optional.empty();
               }
            }
         } catch (Exception error) {
            throw new RuntimeException("Не удалось открыть окно выбора файла: " + error.getMessage(), error);
         }
      });
   }
}

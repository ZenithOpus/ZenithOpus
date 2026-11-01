package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Collection;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public class JsonListStorage<T> {
   public Collection<T> items;
   private final String fileName;
   private final String shifr;
   private final Type type;
   private final Supplier<Collection<T>> collectionSupplier;

   public JsonListStorage(String var1, String var2, Type var3, Supplier<Collection<T>> var4) {
      this.fileName = var1;
      this.shifr = var2;
      this.type = var3;
      this.collectionSupplier = var4;
      File var5 = new File(Zenith.file, var1);
      if (!var5.exists()) {
         try {
            var5.createNewFile();
            this.items = (Collection<T>)var4.get();
         } catch (Exception var7) {
            this.items = (Collection<T>)var4.get();
         }
      } else {
         this.load();
      }
   }

   public Gson createGson() {
      return new GsonBuilder().create();
   }

   public void save() {
      Gson var1 = this.createGson();
      String var2 = var1.toJson(this.items);

      try {
         writeAtomic(
            new File(Zenith.file, this.fileName),
            this.shifr.isEmpty() ? var2 : Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(var2.getBytes(), this.shifr))
         );
      } catch (Exception var4) {
      }
   }

   public static void writeAtomic(File var0, String var1) throws IOException {
      Path var2 = var0.toPath();
      Path var3 = var2.resolveSibling(var2.getFileName() + ".tmp");
      Files.writeString(var3, var1, Charset.defaultCharset());

      try {
         Files.move(var3, var2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException var5) {
         Files.move(var3, var2, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private void keepUnreadable() {
      try {
         Path var1 = new File(Zenith.file, this.fileName).toPath();
         if (Files.isRegularFile(var1) && Files.size(var1) > 0L) {
            Files.copy(var1, var1.resolveSibling(this.fileName + ".broken-" + System.currentTimeMillis()), StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception var2) {
      }
   }

   public void load() {
      try (BufferedReader var1 = new BufferedReader(new FileReader(new File(Zenith.file, this.fileName)))) {
         Gson var2 = this.createGson();
         if (!this.shifr.isEmpty()) {
            String var3 = var1.readLine();
            if (var3 != null && !var3.isEmpty()) {
               byte[] var4 = Base64.getDecoder().decode(var3);
               byte[] var5 = AesGcmCrypto.method5340(var4, this.shifr);
               String var6 = new String(var5, StandardCharsets.UTF_8);
               this.items = (Collection<T>)var2.fromJson(var6, this.type);
            } else {
               this.items = this.collectionSupplier.get();
            }
         } else {
            this.items = (Collection<T>)var2.fromJson(var1, this.type);
         }

         if (this.items == null) {
            this.items = this.collectionSupplier.get();
         }
      } catch (Exception var9) {
         this.keepUnreadable();
         this.items = this.collectionSupplier.get();
      }
   }

   public void add(T var1) {
      this.items.add((T)var1);
   }

   public void remove(T var1) {
      this.items.remove(var1);
   }

   @Generated
   public Collection<T> getItems() {
      return this.items;
   }
}

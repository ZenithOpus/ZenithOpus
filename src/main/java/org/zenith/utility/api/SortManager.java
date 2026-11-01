package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.zenith.Zenith;

public final class SortManager {
   private static final Logger logger = LogUtils.getLogger();
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final Type type = (new TypeToken<List<SortManager.Entry2>>() {}).getType();
   private final Path path = Zenith.file.toPath().resolve("sort-layouts.json");
   private final List<SortManager.Entry2> items = new ArrayList<>();

   public SortManager() {
      this.load();
   }

   public List<SortManager.Entry2> method7002() {
      return List.copyOf(this.items);
   }

   public SortManager.Entry2 method7003(String var1) {
      return var1 == null ? null : this.items.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public boolean method7004(String var1, Collection<SortManager.Entry> var2) {
      var var3 = new ArrayList<>(this.items);
      this.items.removeIf(var1x -> var1x.getName().equalsIgnoreCase(var1));
      this.items.add(new SortManager.Entry2(var1, System.currentTimeMillis(), var2));
      if (this.method7006()) {
         return true;
      } else {
         this.items.clear();
         this.items.addAll(var3);
         return false;
      }
   }

   public boolean method7005(String var1) {
      var var2 = new ArrayList<>(this.items);
      if (!this.items.removeIf(var1x -> var1x.getName().equalsIgnoreCase(var1))) {
         return false;
      } else if (this.method7006()) {
         return true;
      } else {
         this.items.clear();
         this.items.addAll(var2);
         return false;
      }
   }

   private void load() {
      this.items.clear();
      if (Files.isRegularFile(this.path)) {
         try {
            String var1 = Files.readString(this.path, StandardCharsets.UTF_8);
            if (var1.isBlank()) {
               return;
            }

            List<SortManager.Entry2> var2 = (List)gson.fromJson(var1, type);
            if (var2 == null) {
               return;
            }

            for (Object var4_o : (Iterable<?>)(Object)(var2)) {
               SortManager.Entry2 var4 = (SortManager.Entry2)var4_o;
               if (var4 != null && var4.method7010()) {
                  this.items.add(var4);
               }
            }
         } catch (Exception var5) {
            logger.warn("Не удалось загрузить раскладки сортировки из {}", this.path, var5);
         }
      }
   }

   private boolean method7006() {
      Path var1 = this.path.resolveSibling(this.path.getFileName() + ".tmp");

      try {
         Files.createDirectories(this.path.getParent());
         Files.writeString(var1, gson.toJson(this.items), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

         try {
            Files.move(var1, this.path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException var3) {
            Files.move(var1, this.path, StandardCopyOption.REPLACE_EXISTING);
         }

         return true;
      } catch (IOException var4) {
         logger.warn("Не удалось сохранить раскладки сортировки в {}", this.path, var4);
         return false;
      }
   }

   public static final class Entry {
      private int slot;
      private String text;
      private boolean flag;
      private Set<String> items;

      public Entry(int var1, String var2, boolean var3, Collection<String> var4) {
         this.slot = var1;
         this.text = var2;
         this.flag = var3;
         this.items = new LinkedHashSet<>(var4);
      }

      public int getSlot() {
         return this.slot;
      }

      public String getText() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public Set<String> method7007() {
         return Set.copyOf(this.items);
      }

      private boolean method7008() {
         if (this.text != null && !this.text.isBlank()) {
            if (this.items == null) {
               this.items = new LinkedHashSet<>();
            } else {
               this.items.removeIf(Objects::isNull);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static final class Entry2 {
      private String name;
      private long longValue;
      private List<SortManager.Entry> items;

      private Entry2(String var1, long var2, Collection<SortManager.Entry> var4) {
         this.name = var1;
         this.longValue = var2;
         this.items = new ArrayList<>(var4);
      }

      public String getName() {
         return this.name;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public List<SortManager.Entry> method7009() {
         return List.copyOf(this.items);
      }

      private boolean method7010() {
         if (this.name != null && !this.name.isBlank()) {
            if (this.items == null) {
               this.items = new ArrayList<>();
            } else {
               this.items.removeIf(var0 -> var0 == null || !var0.method7008());
            }

            return true;
         } else {
            return false;
         }
      }
   }
}

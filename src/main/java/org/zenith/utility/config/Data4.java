package org.zenith.utility.config;

import java.nio.file.Path;
import java.util.UUID;

public final class Data4 {
   private final String text;
   private final UUID uUID;
   private final Path path;
   private final String text2;

   public String text() {
      return this.text;
   }

   public UUID uUID() {
      return this.uUID;
   }

   public Path path() {
      return this.path;
   }

   public String text2() {
      return this.text2;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Data4 other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.path, other.path) && java.util.Objects.equals(this.text2, other.text2);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, uUID, path, text2);
   }

   @Override
   public String toString() {
      return "Data4[" + "text=" + this.text + ", " + "uUID=" + this.uUID + ", " + "path=" + this.path + ", " + "text2=" + this.text2 + "]";
   }

   public Data4(String var1, UUID var2, Path var3, String var4) {
      this.text = var1;
      this.uUID = var2;
      this.path = var3;
      this.text2 = var4;
   }

   public String getText() {
      return this.text;
   }

   public String name() {
      return this.text;
   }

   public UUID uuid() {
      return this.uUID;
   }

   public Path getPath() {
      return this.path;
   }

   public String getText2() {
      return this.text2;
   }
}

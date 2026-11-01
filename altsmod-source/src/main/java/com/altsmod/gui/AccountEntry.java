package com.altsmod.gui;

import java.nio.file.Path;
import java.util.UUID;

public record AccountEntry(String name, UUID uuid, Path path, String credential) {
   public String displayName() {
      return this.name;
   }
}

package org.zenith.utility.misc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.zenith.Zenith;

public final class EmoteAccess {
   private EmoteAccess() {
   }

   public static List<String> method7916() {
      EmoteManager var0 = method7924();
      return var0 == null ? List.of() : var0.getEmoteRegistry().method1592().stream().filter(EmoteAccess::method7925).toList();
   }

   public static Collection<EmoteInfo> method7917() {
      EmoteManager var0 = method7924();
      return var0 == null ? List.of() : var0.getEmoteRegistry().all().stream().filter(var0x -> method7925(var0x.getText())).toList();
   }

   public static Optional<EmoteInfo> method7918(String var0) {
      EmoteManager var1 = method7924();
      return var1 != null && method7925(var0) ? var1.getEmoteRegistry().method1591(var0) : Optional.empty();
   }

   public static boolean method7919(String var0) {
      EmoteManager var1 = method7924();
      return var1 != null && var1.getEmoteRegistry().method1591(var0).isPresent();
   }

   public static boolean method7920(String var0) {
      EmoteManager var1 = method7924();
      return var1 != null && method7925(var0) && var1.method1688(var0);
   }

   public static boolean method7921(UUID var0, String var1, int var2) {
      EmoteManager var3 = method7924();
      return var3 != null && var3.method1689(var0, var1, var2, EmoteManager.Type.LOCAL);
   }

   public static void method7922(UUID var0) {
      EmoteManager var1 = method7924();
      if (var1 != null) {
         var1.method1690(var0);
      }
   }

   public static boolean method7923(UUID var0) {
      EmoteManager var1 = method7924();
      return var1 != null && var1.method1691(var0);
   }

   private static EmoteManager method7924() {
      Zenith var0 = Zenith.getInstance();
      return var0 == null ? null : var0.getEmoteManager();
   }

   private static boolean method7925(String var0) {
      return var0 != null && !var0.isBlank();
   }
}

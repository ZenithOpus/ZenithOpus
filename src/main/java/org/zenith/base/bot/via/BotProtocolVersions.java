package org.zenith.base.bot.via;

import java.util.List;
import net.minecraft.SharedConstants;

public final class BotProtocolVersions {
   public static final int NATIVE = -1;
   public static final List<BotProtocolVersions.Entry> ENTRIES = List.of(
      new BotProtocolVersions.Entry(-1, SharedConstants.getGameVersion().name()),
      new BotProtocolVersions.Entry(773, "1.21.10"),
      new BotProtocolVersions.Entry(772, "1.21.8"),
      new BotProtocolVersions.Entry(771, "1.21.6"),
      new BotProtocolVersions.Entry(770, "1.21.5"),
      new BotProtocolVersions.Entry(769, "1.21.4"),
      new BotProtocolVersions.Entry(768, "1.21.3"),
      new BotProtocolVersions.Entry(767, "1.21.1"),
      new BotProtocolVersions.Entry(766, "1.20.6"),
      new BotProtocolVersions.Entry(765, "1.20.4"),
      new BotProtocolVersions.Entry(764, "1.20.2"),
      new BotProtocolVersions.Entry(763, "1.20.1"),
      new BotProtocolVersions.Entry(762, "1.19.4"),
      new BotProtocolVersions.Entry(761, "1.19.3"),
      new BotProtocolVersions.Entry(760, "1.19.2"),
      new BotProtocolVersions.Entry(759, "1.19"),
      new BotProtocolVersions.Entry(758, "1.18.2"),
      new BotProtocolVersions.Entry(757, "1.18.1"),
      new BotProtocolVersions.Entry(756, "1.17.1"),
      new BotProtocolVersions.Entry(755, "1.17"),
      new BotProtocolVersions.Entry(754, "1.16.5"),
      new BotProtocolVersions.Entry(753, "1.16.3"),
      new BotProtocolVersions.Entry(751, "1.16.2"),
      new BotProtocolVersions.Entry(736, "1.16.1"),
      new BotProtocolVersions.Entry(578, "1.15.2"),
      new BotProtocolVersions.Entry(498, "1.14.4"),
      new BotProtocolVersions.Entry(477, "1.14")
   );

   private BotProtocolVersions() {
   }

   public static String label(int var0) {
      for (BotProtocolVersions.Entry var2 : ENTRIES) {
         if (var2.protocol() == var0) {
            return var2.label();
         }
      }

      return var0 <= 0 ? ENTRIES.get(0).label() : "#" + var0;
   }

   public static final class Entry {
      private final int protocol;
      private final String label;

      public int protocol() {
         return this.protocol;
      }

      public String label() {
         return this.label;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Entry other)) return false;
         return java.util.Objects.equals(this.protocol, other.protocol) && java.util.Objects.equals(this.label, other.label);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(protocol, label);
      }

      @Override
      public String toString() {
         return "Entry[" + "protocol=" + this.protocol + ", " + "label=" + this.label + "]";
      }

      public Entry(int var1, String var2) {
         this.protocol = var1;
         this.label = var2;
      }
   }
}

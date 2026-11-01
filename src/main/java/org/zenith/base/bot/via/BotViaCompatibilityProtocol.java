package org.zenith.base.bot.via;

import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.exception.InformativeException;
import com.viaversion.viaversion.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.protocols.v1_19_3to1_19_4.packet.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.storage.ConfigurationState;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.storage.ConfigurationState.BridgePhase;

public final class BotViaCompatibilityProtocol extends AbstractSimpleProtocol {
   public static final BotViaCompatibilityProtocol INSTANCE = new BotViaCompatibilityProtocol();

   private BotViaCompatibilityProtocol() {
   }

   public void registerPackets() {
   }

   public void transform(Direction var1, State var2, PacketWrapper var3) throws InformativeException, CancelException {
      if (var1 == Direction.CLIENTBOUND
         && var2 == State.CONFIGURATION
         && var3.getId() == ClientboundPackets1_19_4.RESPAWN.getId()
         && ProtocolVersion.v1_19_4.equals(var3.user().getProtocolInfo().serverProtocolVersion())) {
         ConfigurationState var4 = (ConfigurationState)var3.user().get(ConfigurationState.class);
         if (var4 != null && var4.bridgePhase() != BridgePhase.NONE) {
            var3.passthrough(Types.REMAINING_BYTES);
            var3.write(Types.VAR_INT, 0);
         }
      }

      super.transform(var1, var2, var3);
   }
}

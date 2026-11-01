package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;

@ModuleInfo(
   name = "ResourcePackSpoof",
   category = ModuleCategory.MISC,
   description = "module.srpspoof.description"
)
public final class ResourcePackSpoofModule extends Module {
   public static final ResourcePackSpoofModule INSTANCE = new ResourcePackSpoofModule();

   private ResourcePackSpoofModule() {
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() instanceof ResourcePackSendS2CPacket var2) {
         net.minecraft.client.network.ClientCommonNetworkHandler var6 = var1.getPacketListener() instanceof ClientCommonNetworkHandler var4 ? var4 : mc.getNetworkHandler();
         if (var6 != null) {
            var1.setCancelled(true);
            var6.sendPacket(new ResourcePackStatusC2SPacket(var2.id(), Status.ACCEPTED));
            var6.sendPacket(new ResourcePackStatusC2SPacket(var2.id(), Status.SUCCESSFULLY_LOADED));
         }
      }
   }
}

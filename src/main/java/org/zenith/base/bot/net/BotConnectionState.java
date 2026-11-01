package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class BotConnectionState {
   private final GameProfile profile;
   private final Immutable receivedRegistries;
   private final FeatureSet enabledFeatures;
   @Nullable
   private final String brand;
   private final Map<Identifier, byte[]> serverCookies;
   private final Map<String, String> customReportDetails;
   private final ServerLinks serverLinks;

   public GameProfile profile() {
      return this.profile;
   }

   public Immutable receivedRegistries() {
      return this.receivedRegistries;
   }

   public FeatureSet enabledFeatures() {
      return this.enabledFeatures;
   }

   public String brand() {
      return this.brand;
   }

   public Map<Identifier, byte[]> serverCookies() {
      return this.serverCookies;
   }

   public Map<String, String> customReportDetails() {
      return this.customReportDetails;
   }

   public ServerLinks serverLinks() {
      return this.serverLinks;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BotConnectionState other)) return false;
      return java.util.Objects.equals(this.profile, other.profile) && java.util.Objects.equals(this.receivedRegistries, other.receivedRegistries) && java.util.Objects.equals(this.enabledFeatures, other.enabledFeatures) && java.util.Objects.equals(this.brand, other.brand) && java.util.Objects.equals(this.serverCookies, other.serverCookies) && java.util.Objects.equals(this.customReportDetails, other.customReportDetails) && java.util.Objects.equals(this.serverLinks, other.serverLinks);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(profile, receivedRegistries, enabledFeatures, brand, serverCookies, customReportDetails, serverLinks);
   }

   @Override
   public String toString() {
      return "BotConnectionState[" + "profile=" + this.profile + ", " + "receivedRegistries=" + this.receivedRegistries + ", " + "enabledFeatures=" + this.enabledFeatures + ", " + "brand=" + this.brand + ", " + "serverCookies=" + this.serverCookies + ", " + "customReportDetails=" + this.customReportDetails + ", " + "serverLinks=" + this.serverLinks + "]";
   }

   public BotConnectionState(
      GameProfile var1, Immutable var2, FeatureSet var3, @Nullable String var4, Map<Identifier, byte[]> var5, Map<String, String> var6, ServerLinks var7
   ) {
      this.profile = var1;
      this.receivedRegistries = var2;
      this.enabledFeatures = var3;
      this.brand = var4;
      this.serverCookies = var5;
      this.customReportDetails = var6;
      this.serverLinks = var7;
   }
}

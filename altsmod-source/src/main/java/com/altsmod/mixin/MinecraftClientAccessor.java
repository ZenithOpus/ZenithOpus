package com.altsmod.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import java.net.Proxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.util.ApiServices;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MinecraftClient.class})
public interface MinecraftClientAccessor {
   @Mutable
   @Accessor("session")
   void setSession(Session var1);

   @Accessor("networkProxy")
   Proxy getNetworkProxy();

   @Mutable
   @Accessor("apiServices")
   void setApiServices(ApiServices var1);

   @Mutable
   @Accessor("userApiService")
   void setUserApiService(UserApiService var1);

   @Mutable
   @Accessor("profileKeys")
   void setProfileKeys(ProfileKeys var1);

   @Mutable
   @Accessor("abuseReportContext")
   void setAbuseReportContext(AbuseReportContext var1);
}

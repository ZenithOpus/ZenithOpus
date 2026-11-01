package com.altsmod.auth;

import com.altsmod.mixin.MinecraftClientAccessor;
import com.mojang.authlib.Environment;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.util.ApiServices;

public final class SessionHelper {
   private static final Environment MOJANG_PROD = new Environment("https://sessionserver.mojang.com", "https://api.minecraftservices.com", "https://api.mojang.com", "PROD");

   private SessionHelper() {
   }

   public static void applySession(MinecraftClient mc, Session session) {
      MinecraftClientAccessor mca = (MinecraftClientAccessor)mc;
      mca.setSession(session);

      try {
         YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(mca.getNetworkProxy(), MOJANG_PROD);

         try {
            ApiServices apiServices = ApiServices.create(authService, mc.runDirectory);
            mca.setApiServices(apiServices);
            System.out.println("[AltsMod] Updated ApiServices to Mojang PROD environment.");
         } catch (Throwable t) {
            System.err.println("[AltsMod] Failed to update ApiServices: " + t.getMessage());
            t.printStackTrace();
         }

         UserApiService apiService = UserApiService.OFFLINE;

         try {
            if (session.getAccessToken() != null && !session.getAccessToken().isBlank()) {
               apiService = authService.createUserApiService(session.getAccessToken());
               System.out.println("[AltsMod] UserApiService created with access token.");
            }
         } catch (Throwable var8) {
         }

         mca.setUserApiService(apiService);

         try {
            mca.setProfileKeys(ProfileKeys.create(apiService, session, mc.runDirectory.toPath()));
         } catch (Throwable var7) {
         }

         try {
            mca.setAbuseReportContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiService));
         } catch (Throwable var6) {
         }
      } catch (Throwable t) {
         System.err.println("[AltsMod] Error in applySession: " + t.getMessage());
         t.printStackTrace();
      }

   }
}

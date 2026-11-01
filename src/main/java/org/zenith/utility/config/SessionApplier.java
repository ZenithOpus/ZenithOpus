package org.zenith.utility.config;

import com.mojang.authlib.Environment;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.util.ApiServices;
import org.zenith.utility.mixin.accessors.MinecraftClientAccessor;

public final class SessionApplier {
   private static final Environment environment = new Environment(
      "https://sessionserver.mojang.com", "https://api.minecraftservices.com", "https://api.mojang.com", "PROD"
   );

   private SessionApplier() {
   }

   public static void method4657(MinecraftClient var0, Session var1) {
      MinecraftClientAccessor var2 = (MinecraftClientAccessor)(Object)var0;
      var2.zenith$setSession(var1);

      try {
         YggdrasilAuthenticationService var3 = new YggdrasilAuthenticationService(var2.zenith$getNetworkProxy(), environment);

         try {
            ApiServices var4 = ApiServices.create(var3, var0.runDirectory);
            var2.zenith$setApiServices(var4);
            System.out.println("[ZenithAlts] Обновлены ApiServices на окружение Mojang PROD.");
         } catch (Throwable var9) {
            System.err.println("[ZenithAlts] Не удалось обновить ApiServices: " + var9.getMessage());
            var9.printStackTrace();
         }

         UserApiService var11 = UserApiService.OFFLINE;

         try {
            if (var1.getAccessToken() != null && !var1.getAccessToken().isBlank()) {
               var11 = var3.createUserApiService(var1.getAccessToken());
               System.out.println("[ZenithAlts] UserApiService создан с access-токеном.");
            }
         } catch (Throwable var8) {
         }

         var2.zenith$setUserApiService(var11);

         try {
            var2.zenith$setProfileKeys(ProfileKeys.create(var11, var1, var0.runDirectory.toPath()));
         } catch (Throwable var7) {
         }

         try {
            var2.zenith$setAbuseReportContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), var11));
         } catch (Throwable var6) {
         }
      } catch (Throwable var10) {
         System.err.println("[ZenithAlts] Ошибка в applySession: " + var10.getMessage());
         var10.printStackTrace();
      }
   }
}

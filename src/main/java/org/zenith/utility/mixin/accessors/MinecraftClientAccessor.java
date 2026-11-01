package org.zenith.utility.mixin.accessors;

import com.mojang.authlib.minecraft.UserApiService;
import java.net.Proxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.particle.ParticleSpriteManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ApiServices;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface MinecraftClientAccessor {
   @Invoker("setWorld")
   void zenith$setWorld(@Nullable ClientWorld var1);

   @Invoker("doItemUse")
   void zenith$doItemUse();

   @Invoker("doAttack")
   boolean zenith$doAttack();

   @Accessor("framebuffer")
   @Mutable
   void zenith$setFramebuffer(Framebuffer var1);

   @Accessor("session")
   @Mutable
   void zenith$setSession(Session var1);

   @Accessor("networkProxy")
   Proxy zenith$getNetworkProxy();

   @Accessor("apiServices")
   @Mutable
   void zenith$setApiServices(ApiServices var1);

   @Accessor("userApiService")
   @Mutable
   void zenith$setUserApiService(UserApiService var1);

   @Accessor("profileKeys")
   @Mutable
   void zenith$setProfileKeys(ProfileKeys var1);

   @Accessor("abuseReportContext")
   @Mutable
   void zenith$setAbuseReportContext(AbuseReportContext var1);

   @Accessor("attackCooldown")
   int zenith$getAttackCooldown();

   @Accessor("attackCooldown")
   void zenith$setAttackCooldown(int var1);

   @Accessor("worldRenderer")
   WorldRenderer zenith$getWorldRenderer();

   @Accessor("worldRenderer")
   @Mutable
   void zenith$setWorldRenderer(WorldRenderer var1);

   @Accessor("particleSpriteManager")
   ParticleSpriteManager zenith$particleSpriteManager();
}

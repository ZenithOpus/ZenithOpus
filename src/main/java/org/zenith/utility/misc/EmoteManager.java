package org.zenith.utility.misc;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.layered.AnimationContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.EmoteRegistry;

public final class EmoteManager {
   private static final int CONST_intValue = 1000;
   private static final long CONST_longValue = 30000000000L;
   private final EmoteRegistry emoteRegistry = new EmoteRegistry();
   private final AtomicLong atomicLong = new AtomicLong();
   private final Map<UUID, EmoteManager.Data2> items = new ConcurrentHashMap<>();
   private final Map<UUID, EmoteManager.Data> items2 = new ConcurrentHashMap<>();
   private ClientWorld clientWorld;
   private final MultiSelectSetting render = new MultiSelectSetting("cosmetics.emotes.render");

   public EmoteManager() {
      new MultiSelectSetting.Entry(this.render, "cosmetics.emotes.render.self", true);
      new MultiSelectSetting.Entry(this.render, "cosmetics.emotes.render.friends", true);
      ClientTickEvents.END_CLIENT_TICK.register(this::method1695);
   }

   public EmoteRegistry getEmoteRegistry() {
      return this.emoteRegistry;
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (ReflectiveOperationException var3) {
            return null;
         }
      }).filter(Setting.class::isInstance).map(Setting.class::cast).collect(Collectors.toList());
   }

   public boolean method1688(String var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      return var2.player != null && this.method1689(var2.player.getUuid(), var1, 0, EmoteManager.Type.LOCAL);
   }

   public boolean method1689(UUID var1, String var2, int var3, EmoteManager.Type var4) {
      if (var1 != null && var4 != null) {
         EmoteInfo var5 = this.emoteRegistry.method1591(var2).orElse(null);
         if (var5 == null) {
            return false;
         } else {
            method1702(() -> this.method1694(var1, var5, var3, var4));
            return true;
         }
      } else {
         return false;
      }
   }

   public void method1690(UUID var1) {
      if (var1 != null) {
         method1702(() -> this.method1699(var1));
      }
   }

   public boolean method1691(UUID var1) {
      EmoteManager.Data var2 = this.items2.get(var1);
      return var2 != null && var2.getPreviewEmotePlayer().isActive();
   }

   public String method1692(UUID var1) {
      EmoteManager.Data2 var2 = this.items.get(var1);
      return var2 == null ? "" : var2.getEmoteInfo().getText();
   }

   public void method1693() {
      method1702(this::method1701);
   }

   private void method1694(UUID var1, EmoteInfo var2, int var3, EmoteManager.Type var4) {
      MinecraftClient var5 = MinecraftClient.getInstance();
      if (var5.world != this.clientWorld) {
         this.method1701();
         this.clientWorld = var5.world;
      }

      if (this.method1698(var5, var1, var4)) {
         this.method1699(var1);
      } else {
         EmoteManager.Data2 var6 = new EmoteManager.Data2(var2, Math.max(0, var3), System.nanoTime(), this.atomicLong.incrementAndGet(), var4);
         this.items.put(var1, var6);
         this.method1696(var5, var1, var6);
      }
   }

   private void method1695(MinecraftClient var1) {
      if (var1.world != this.clientWorld) {
         this.method1701();
         this.clientWorld = var1.world;
      }

      if (var1.world != null) {
         long var2 = System.nanoTime();

         for (Entry var5 : new ArrayList<>(this.items.entrySet())) {
            UUID var6 = (UUID)var5.getKey();
            EmoteManager.Data2 var7 = (EmoteManager.Data2)var5.getValue();
            if (this.method1698(var1, var6, var7.getType())) {
               this.method1699(var6);
            } else {
               AbstractClientPlayerEntity var8 = method1703(var1.world, var6);
               if (var8 == null) {
                  if (var2 - var7.getLongValue() > 30000000000L) {
                     this.method1699(var6);
                  }
               } else {
                  this.method1697(var8, var7, var2);
                  EmoteManager.Data var9 = this.items2.get(var6);
                  if (var9 != null && var9.getLongValue() == var7.getLongValue2() && !var9.getPreviewEmotePlayer().isActive()) {
                     this.method1699(var6);
                  }
               }
            }
         }
      }
   }

   private void method1696(MinecraftClient var1, UUID var2, EmoteManager.Data2 var3) {
      if (var1.world != null) {
         AbstractClientPlayerEntity var4 = method1703(var1.world, var2);
         if (var4 != null) {
            this.method1697(var4, var3, System.nanoTime());
         }
      }
   }

   private void method1697(AbstractClientPlayerEntity var1, EmoteManager.Data2 var2, long var3) {
      UUID var5 = var1.getUuid();
      EmoteManager.Data var6 = this.items2.get(var5);
      if (var6 == null || var6.getAbstractClientPlayerEntity() != var1 || var6.getLongValue() != var2.getLongValue2()) {
         if (var6 != null) {
            method1700(var6);
         }

         AnimationContainer var7 = new AnimationContainer(null);
         ((IPlayer)var1).playerAnimator$getAnimationStack().addAnimLayer(1000, var7);
         long var8 = Math.max(0L, (var3 - var2.getLongValue()) / 50000000L);
         int var10 = (int)Math.min(2147483647L, var2.getIntValue() + var8);
         PreviewEmotePlayer var11 = new PreviewEmotePlayer(var2.getEmoteInfo(), var10);
         var7.setAnim(var11);
         this.items2.put(var5, new EmoteManager.Data(var1, var7, var11, var2.getLongValue2()));
      }
   }

   private boolean method1698(MinecraftClient var1, UUID var2, EmoteManager.Type var3) {
      if (var3 == EmoteManager.Type.CLOUD) {
         return !this.render.method5222("cosmetics.emotes.render.friends").isEnabled();
      } else {
         boolean var4 = var1.player != null && var1.player.getUuid().equals(var2);
         return var4 && !this.render.method5222("cosmetics.emotes.render.self").isEnabled();
      }
   }

   private void method1699(UUID var1) {
      this.items.remove(var1);
      EmoteManager.Data var2 = this.items2.remove(var1);
      if (var2 != null) {
         method1700(var2);
      }
   }

   private static void method1700(EmoteManager.Data var0) {
      var0.getPreviewEmotePlayer().stop();
      var0.getAnimationContainer().setAnim(null);
      ((IPlayer)var0.getAbstractClientPlayerEntity()).playerAnimator$getAnimationStack().removeLayer(var0.getAnimationContainer());
   }

   private void method1701() {
      for (EmoteManager.Data var2 : this.items2.values()) {
         method1700(var2);
      }

      this.items2.clear();
      this.items.clear();
   }

   private static void method1702(Runnable var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (!var1.isOnThread()) {
         var1.execute(var0);
      } else {
         var0.run();
      }
   }

   public void close() {
      this.method1693();
   }

   private static AbstractClientPlayerEntity method1703(ClientWorld var0, UUID var1) {
      return var0.getPlayerByUuid(var1) instanceof AbstractClientPlayerEntity var3 ? var3 : null;
   }

   private static final class Data {
      private final AbstractClientPlayerEntity abstractClientPlayerEntity;
      private final AnimationContainer<PreviewEmotePlayer> animationContainer;
      private final PreviewEmotePlayer previewEmotePlayer;
      private final long longValue;

      public AbstractClientPlayerEntity abstractClientPlayerEntity() {
         return this.abstractClientPlayerEntity;
      }

      public AnimationContainer<PreviewEmotePlayer> animationContainer() {
         return this.animationContainer;
      }

      public PreviewEmotePlayer previewEmotePlayer() {
         return this.previewEmotePlayer;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.abstractClientPlayerEntity, other.abstractClientPlayerEntity) && java.util.Objects.equals(this.animationContainer, other.animationContainer) && java.util.Objects.equals(this.previewEmotePlayer, other.previewEmotePlayer) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(abstractClientPlayerEntity, animationContainer, previewEmotePlayer, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "abstractClientPlayerEntity=" + this.abstractClientPlayerEntity + ", " + "animationContainer=" + this.animationContainer + ", " + "previewEmotePlayer=" + this.previewEmotePlayer + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(AbstractClientPlayerEntity var1, AnimationContainer<PreviewEmotePlayer> var2, PreviewEmotePlayer var3, long var4) {
         this.abstractClientPlayerEntity = var1;
         this.animationContainer = var2;
         this.previewEmotePlayer = var3;
         this.longValue = var4;
      }

      public AbstractClientPlayerEntity getAbstractClientPlayerEntity() {
         return this.abstractClientPlayerEntity;
      }

      public AnimationContainer<PreviewEmotePlayer> getAnimationContainer() {
         return this.animationContainer;
      }

      public PreviewEmotePlayer getPreviewEmotePlayer() {
         return this.previewEmotePlayer;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final EmoteInfo emoteInfo;
      private final int intValue;
      private final long longValue;
      private final long longValue2;
      private final EmoteManager.Type type;

      public EmoteInfo emoteInfo() {
         return this.emoteInfo;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public EmoteManager.Type type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.emoteInfo, other.emoteInfo) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(emoteInfo, intValue, longValue, longValue2, type);
      }

      @Override
      public String toString() {
         return "Data2[" + "emoteInfo=" + this.emoteInfo + ", " + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "type=" + this.type + "]";
      }

      private Data2(EmoteInfo var1, int var2, long var3, long var5, EmoteManager.Type var7) {
         this.emoteInfo = var1;
         this.intValue = var2;
         this.longValue = var3;
         this.longValue2 = var5;
         this.type = var7;
      }

      public EmoteInfo getEmoteInfo() {
         return this.emoteInfo;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public EmoteManager.Type getType() {
         return this.type;
      }
   }

   public static enum Type {
      LOCAL,
      CLOUD;
   }
}
